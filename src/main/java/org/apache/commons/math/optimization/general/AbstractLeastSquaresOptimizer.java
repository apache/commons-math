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

package org.apache.commons.math.optimization.general;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.exception.ConvergenceException;
import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
import org.apache.commons.math.analysis.MultivariateMatrixFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.optimization.SimpleVectorialValueChecker;
import org.apache.commons.math.optimization.ConvergenceChecker;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.VectorialPointValuePair;
import org.apache.commons.math.util.FastMath;

/**
 * Base class for implementing least squares optimizers.
 * It handles the boilerplate methods associated to thresholds settings,
 * jacobian and error estimation.
 *
 * @version $Revision$ $Date$
 * @since 1.2
 *
 */
public abstract class AbstractLeastSquaresOptimizer
    extends BaseAbstractVectorialOptimizer<DifferentiableMultivariateVectorialFunction>
    implements DifferentiableMultivariateVectorialOptimizer {
    /**
     * Jacobian matrix of the weighted residuals.
     * This matrix is in canonical form just after the calls to
     * {@link #updateJacobian()}, but may be modified by the solver
     * in the derived class (the {@link LevenbergMarquardtOptimizer
     * Levenberg-Marquardt optimizer} does this).
     */
    protected double[][] weightedResidualJacobian;
    /** Number of columns of the jacobian matrix. */
    protected int cols;
    /** Number of rows of the jacobian matrix. */
    protected int rows;
    /** Current point. */
    protected double[] point;
    /** Current objective function value. */
    protected double[] objective;
    /** Current residuals. */
    protected double[] residuals;
    /** Weighted residuals */
    protected double[] weightedResiduals;
    /** Cost value (square root of the sum of the residuals). */
    protected double cost;
    /** Objective function derivatives. */
    private MultivariateMatrixFunction jF;
    /** Number of evaluations of the Jacobian. */
    private int jacobianEvaluations;

    /**
     * Simple constructor with default settings.
     * The convergence check is set to a {@link SimpleVectorialValueChecker}.
     */
    protected AbstractLeastSquaresOptimizer() {}
    /**
     * @param checker Convergence checker.
     * @param maxEvaluations Maximal number of function evaluations.
     */
    protected AbstractLeastSquaresOptimizer(ConvergenceChecker<VectorialPointValuePair> checker,
                                            int maxEvaluations) {
        super(checker, maxEvaluations);
    }

    /**
     * @return the number of evaluations of the Jacobian function.
     */
    public int getJacobianEvaluations() {
        return jacobianEvaluations;
    }

    /**
     * Update the jacobian matrix.
     * @exception FunctionEvaluationException if the function jacobian
     * cannot be evaluated or its dimension doesn't match problem dimension
     */
    protected void updateJacobian() throws FunctionEvaluationException {
        ++jacobianEvaluations;
        weightedResidualJacobian = jF.value(point);
        if (weightedResidualJacobian.length != rows) {
            throw new FunctionEvaluationException(point, LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE,
                                                  weightedResidualJacobian.length, rows);
        }

        final double[] residualsWeights = getWeightRef();

        for (int i = 0; i < rows; i++) {
            final double[] ji = weightedResidualJacobian[i];
            double wi = FastMath.sqrt(residualsWeights[i]);
            for (int j = 0; j < cols; ++j) {
                //ji[j] *=  -1.0;
                weightedResidualJacobian[i][j] = -ji[j]*wi;
            }
        }
    }

    /**
     * Update the residuals array and cost function value.
     * @exception FunctionEvaluationException if the function cannot be evaluated
     * or its dimension doesn't match problem dimension or maximal number of
     * of evaluations is exceeded
     */
    protected void updateResidualsAndCost() throws FunctionEvaluationException {
        objective = computeObjectiveValue(point);
        if (objective.length != rows) {
            throw new FunctionEvaluationException(point, LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE,
                                                  objective.length, rows);
        }

        final double[] targetValues = getTargetRef();
        final double[] residualsWeights = getWeightRef();

        cost = 0;
        int index = 0;
        for (int i = 0; i < rows; i++) {
            final double residual = targetValues[i] - objective[i];
            weightedResiduals[i]= residual*FastMath.sqrt(residualsWeights[i]);
            cost += residualsWeights[i] * residual * residual;
            index += cols;
        }
        cost = FastMath.sqrt(cost);
    }

    /**
     * Get the Root Mean Square value.
     * Get the Root Mean Square value, i.e. the root of the arithmetic
     * mean of the square of all weighted residuals. This is related to the
     * criterion that is minimized by the optimizer as follows: if
     * <em>c</em> if the criterion, and <em>n</em> is the number of
     * measurements, then the RMS is <em>sqrt (c/n)</em>.
     *
     * @return RMS value
     */
    public double getRMS() {
        return FastMath.sqrt(getChiSquare() / rows);
    }

    /**
     * Get a Chi-Square-like value assuming the N residuals follow N
     * distinct normal distributions centered on 0 and whose variances are
     * the reciprocal of the weights.
     * @return chi-square value
     */
    public double getChiSquare() {
        return cost * cost;
    }

    /**
     * Get the covariance matrix of optimized parameters.
     * @return covariance matrix
     * @exception FunctionEvaluationException if the function jacobian cannot
     * be evaluated
     * @exception ConvergenceException if the covariance matrix
     * cannot be computed (singular problem)
     */
    public double[][] getCovariances()
        throws FunctionEvaluationException {

        // set up the jacobian
        updateJacobian();

        // compute transpose(J).J, avoiding building big intermediate matrices
        double[][] jTj = new double[cols][cols];
        for (int i = 0; i < cols; ++i) {
            for (int j = i; j < cols; ++j) {
                double sum = 0;
                for (int k = 0; k < rows; ++k) {
                    sum += weightedResidualJacobian[k][i] * weightedResidualJacobian[k][j];
                }
                jTj[i][j] = sum;
                jTj[j][i] = sum;
            }
        }

        try {
            // compute the covariances matrix
            RealMatrix inverse =
                new LUDecompositionImpl(MatrixUtils.createRealMatrix(jTj)).getSolver().getInverse();
            return inverse.getData();
        } catch (InvalidMatrixException ime) {
            throw new ConvergenceException(LocalizedFormats.UNABLE_TO_COMPUTE_COVARIANCE_SINGULAR_PROBLEM);
        }

    }

    /**
     * Guess the errors in optimized parameters.
     * <p>Guessing is covariance-based, it only gives rough order of magnitude.</p>
     * @return errors in optimized parameters
     * @exception FunctionEvaluationException if the function jacobian cannot b evaluated
     * @exception ConvergenceException if the covariances matrix cannot be computed
     * or the number of degrees of freedom is not positive (number of measurements
     * lesser or equal to number of parameters)
     */
    public double[] guessParametersErrors()
        throws FunctionEvaluationException {
        if (rows <= cols) {
            throw new ConvergenceException(LocalizedFormats.NO_DEGREES_OF_FREEDOM,
                                           rows, cols);
        }
        double[] errors = new double[cols];
        final double c = FastMath.sqrt(getChiSquare() / (rows - cols));
        double[][] covar = getCovariances();
        for (int i = 0; i < errors.length; ++i) {
            errors[i] = FastMath.sqrt(covar[i][i]) * c;
        }
        return errors;
    }

    /** {@inheritDoc} */
    public VectorialPointValuePair optimize(final DifferentiableMultivariateVectorialFunction f,
                                            final double[] target, final double[] weights,
                                            final double[] startPoint)
        throws FunctionEvaluationException {
        // Reset counter.
        jacobianEvaluations = 0;

        // Store least squares problem characteristics.
        jF = f.jacobian();
        this.residuals = new double[target.length];

        // Arrays shared with the other private methods.
        point = startPoint.clone();
        rows = target.length;
        cols = point.length;

        weightedResidualJacobian = new double[rows][cols];
        this.weightedResiduals = new double[rows];

        cost = Double.POSITIVE_INFINITY;

        return super.optimize(f, target, weights, startPoint);
    }
}
