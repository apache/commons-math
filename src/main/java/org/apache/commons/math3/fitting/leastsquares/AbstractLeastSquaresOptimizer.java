/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.BaseOptimizer;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.util.FastMath;

/**
 * Base class for implementing least-squares optimizers.
 * It provides methods for error estimation.
 *
 * @version $Id$
 * @since 3.3
 */
public abstract class AbstractLeastSquaresOptimizer
    extends BaseOptimizer<PointVectorValuePair> {
    /** Target values for the model function at optimum. */
    private final double[] target;
    /** Weight matrix. */
    private final RealMatrix weight;
    /** Model function. */
    private final MultivariateVectorFunction model;
    /** Jacobian of the model function. */
    private final MultivariateMatrixFunction jacobian;
    /** Square-root of the weight matrix. */
    private final RealMatrix weightSqrt;
    /** Initial guess. */
    private final double[] start;

    /**
     * @param target Observations.
     * @param weight Weight of the observations.
     * For performance, no defensive copy is performed.
     * @param weightSqrt Square-root of the {@code weight} matrix.
     * If {@code null}, it will be computed; otherwise it is the caller's
     * responsibility that {@code weight} and {@code weightSqrt} are
     * consistent.
     * No defensive copy is performed.
     * @param model ModelFunction.
     * @param jacobian Jacobian of the model function.
     * @param checker Convergence checker.
     * @param start Initial guess.
     * @param maxEval Maximum number of evaluations of the model
     * function.
     * @param maxIter Maximum number of iterations.
     */
    protected AbstractLeastSquaresOptimizer(double[] target,
                                            RealMatrix weight,
                                            RealMatrix weightSqrt,
                                            MultivariateVectorFunction model,
                                            MultivariateMatrixFunction jacobian,
                                            ConvergenceChecker<PointVectorValuePair> checker,
                                            double[] start,
                                            int maxEval,
                                            int maxIter) {
        super(checker, maxEval, maxIter);

        this.target = target;
        this.weight = weight;
        this.model = model;
        this.jacobian = jacobian;
        this.start = start;

        this.weightSqrt = weightSqrt == null ?
            (weight == null ?
             null : squareRoot(weight)) : weightSqrt;
    }

    /**
     * Gets the target values.
     *
     * @return the target values.
     */
    public double[] getTarget() {
        return target == null ? null : target.clone();
    }

    /**
     * Gets the initial guess.
     *
     * @return the initial guess values.
     */
    public double[] getStart() {
        return start == null ? null : start.clone();
    }

    /**
     * Gets the square-root of the weight matrix.
     *
     * @return the square-root of the weight matrix.
     */
    public RealMatrix getWeightSquareRoot() {
        return weightSqrt == null ? null : weightSqrt.copy();
    }

    /**
     * Gets the model function.
     *
     * @return the model function.
     */
    public MultivariateVectorFunction getModel() {
        return model;
    }

    /**
     * Gets the model function's Jacobian.
     *
     * @return the Jacobian.
     */
    public MultivariateMatrixFunction getJacobian() {
        return jacobian;
    }

    /**
     * Get the covariance matrix of the optimized parameters.
     * <br/>
     * Note that this operation involves the inversion of the
     * <code>J<sup>T</sup>J</code> matrix, where {@code J} is the
     * Jacobian matrix.
     * The {@code threshold} parameter is a way for the caller to specify
     * that the result of this computation should be considered meaningless,
     * and thus trigger an exception.
     *
     * @param params Model parameters.
     * @param threshold Singularity threshold.
     * @return the covariance matrix.
     * @throws org.apache.commons.math3.linear.SingularMatrixException
     * if the covariance matrix cannot be computed (singular problem).
     */
    public double[][] computeCovariances(double[] params,
                                         double threshold) {
        // Set up the Jacobian.
        final RealMatrix j = computeWeightedJacobian(params);

        // Compute transpose(J)J.
        final RealMatrix jTj = j.transpose().multiply(j);

        // Compute the covariances matrix.
        final DecompositionSolver solver
            = new QRDecomposition(jTj, threshold).getSolver();
        return solver.getInverse().getData();
    }

    /**
     * Computes an estimate of the standard deviation of the parameters. The
     * returned values are the square root of the diagonal coefficients of the
     * covariance matrix, {@code sd(a[i]) ~= sqrt(C[i][i])}, where {@code a[i]}
     * is the optimized value of the {@code i}-th parameter, and {@code C} is
     * the covariance matrix.
     *
     * @param params Model parameters.
     * @param covarianceSingularityThreshold Singularity threshold (see
     * {@link #computeCovariances(double[],double) computeCovariances}).
     * @return an estimate of the standard deviation of the optimized parameters
     * @throws org.apache.commons.math3.linear.SingularMatrixException
     * if the covariance matrix cannot be computed.
     */
    public double[] computeSigma(double[] params,
                                 double covarianceSingularityThreshold) {
        final int nC = params.length;
        final double[] sig = new double[nC];
        final double[][] cov = computeCovariances(params, covarianceSingularityThreshold);
        for (int i = 0; i < nC; ++i) {
            sig[i] = FastMath.sqrt(cov[i][i]);
        }
        return sig;
    }

    /**
     * Gets the weight matrix of the observations.
     *
     * @return the weight matrix.
     */
    public RealMatrix getWeight() {
        return weight.copy();
    }

    /**
     * Computes the normalized cost.
     * It is the square-root of the sum of squared of the residuals, divided
     * by the number of measurements.
     *
     * @param params Model function parameters.
     * @return the cost.
     */
    public double computeRMS(double[] params) {
        final double cost = computeCost(computeResiduals(getModel().value(params)));
        return FastMath.sqrt(cost * cost / target.length);
    }

    /**
     * Calling this method will raise an exception.
     *
     * @param optData Obsolete.
     * @return nothing.
     * @throws MathUnsupportedOperationException if called.
     * @deprecated Do not use this method.
     */
    @Deprecated
    @Override
    public PointVectorValuePair optimize(OptimizationData... optData)
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /**
     * Gets a reference to the corresponding field.
     * Altering it could jeopardize the consistency of this class.
     *
     * @return the reference.
     */
    protected double[] getTargetInternal() {
        return target;
    }

    /**
     * Gets a reference to the corresponding field.
     * Altering it could jeopardize the consistency of this class.
     *
     * @return the reference.
     */
    protected RealMatrix getWeightInternal() {
        return weight;
    }

    /**
     * Gets a reference to the corresponding field.
     * Altering it could jeopardize the consistency of this class.
     *
     * @return the reference.
     */
    protected RealMatrix getWeightSquareRootInternal() {
        return weightSqrt;
    }

    /**
     * Computes the objective function value.
     * This method <em>must</em> be called by subclasses to enforce the
     * evaluation counter limit.
     *
     * @param params Point at which the objective function must be evaluated.
     * @return the objective function value at the specified point.
     * @throws org.apache.commons.math3.exception.TooManyEvaluationsException
     * if the maximal number of evaluations (of the model vector function) is
     * exceeded.
     */
    protected double[] computeObjectiveValue(double[] params) {
        super.incrementEvaluationCount();
        return model.value(params);
    }

    /**
     * Computes the weighted Jacobian matrix.
     *
     * @param params Model parameters at which to compute the Jacobian.
     * @return the weighted Jacobian: W<sup>1/2</sup> J.
     * @throws DimensionMismatchException if the Jacobian dimension does not
     * match problem dimension.
     */
    protected RealMatrix computeWeightedJacobian(double[] params) {
        return weightSqrt.multiply(MatrixUtils.createRealMatrix(computeJacobian(params)));
    }

    /**
     * Computes the Jacobian matrix.
     *
     * @param params Point at which the Jacobian must be evaluated.
     * @return the Jacobian at the specified point.
     */
    protected double[][] computeJacobian(final double[] params) {
        return jacobian.value(params);
    }

    /**
     * Computes the cost.
     *
     * @param residuals Residuals.
     * @return the cost.
     * @see #computeResiduals(double[])
     */
    protected double computeCost(double[] residuals) {
        final ArrayRealVector r = new ArrayRealVector(residuals);
        return FastMath.sqrt(r.dotProduct(weight.operate(r)));
    }

    /**
     * Computes the residuals.
     * The residual is the difference between the observed (target)
     * values and the model (objective function) value.
     * There is one residual for each element of the vector-valued
     * function.
     *
     * @param objectiveValue Value of the the objective function. This is
     * the value returned from a call to
     * {@link #computeObjectiveValue(double[]) computeObjectiveValue}
     * (whose array argument contains the model parameters).
     * @return the residuals.
     * @throws DimensionMismatchException if {@code params} has a wrong
     * length.
     */
    protected double[] computeResiduals(double[] objectiveValue) {
        if (objectiveValue.length != target.length) {
            throw new DimensionMismatchException(target.length,
                                                 objectiveValue.length);
        }

        final double[] residuals = new double[target.length];
        for (int i = 0; i < target.length; i++) {
            residuals[i] = target[i] - objectiveValue[i];
        }

        return residuals;
    }

    /**
     * Computes the square-root of the weight matrix.
     *
     * @param m Symmetric, positive-definite (weight) matrix.
     * @return the square-root of the weight matrix.
     */
    private RealMatrix squareRoot(RealMatrix m) {
        if (m instanceof DiagonalMatrix) {
            final int dim = m.getRowDimension();
            final RealMatrix sqrtM = new DiagonalMatrix(dim);
            for (int i = 0; i < dim; i++) {
                sqrtM.setEntry(i, i, FastMath.sqrt(m.getEntry(i, i)));
            }
            return sqrtM;
        } else {
            final EigenDecomposition dec = new EigenDecomposition(m);
            return dec.getSquareRoot();
        }
    }
}
