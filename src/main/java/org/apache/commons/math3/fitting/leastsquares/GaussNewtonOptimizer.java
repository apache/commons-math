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

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;

/**
 * Gauss-Newton least-squares solver.
 *
 * <p>
 * This class solve a least-square problem by solving the normal equations
 * of the linearized problem at each iteration. Either LU decomposition or
 * QR decomposition can be used to solve the normal equations. LU decomposition
 * is faster but QR decomposition is more robust for difficult problems.
 * </p>
 *
 * @version $Id$
 * @since 3.3
 *
 */
public class GaussNewtonOptimizer extends AbstractLeastSquaresOptimizer
    implements WithTarget<GaussNewtonOptimizer>,
               WithWeight<GaussNewtonOptimizer>,
               WithModelAndJacobian<GaussNewtonOptimizer>,
               WithConvergenceChecker<GaussNewtonOptimizer>,
               WithStartPoint<GaussNewtonOptimizer>,
               WithMaxIterations<GaussNewtonOptimizer>,
               WithMaxEvaluations<GaussNewtonOptimizer> {
    /** Indicator for using LU decomposition. */
    private final boolean useLU;

    /**
     * Constructor called by the various {@code withXxx} methods.
     *
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
     * @param useLU Whether to use LU decomposition.
     */
    private GaussNewtonOptimizer(double[] target,
                                 RealMatrix weight,
                                 RealMatrix weightSqrt,
                                 MultivariateVectorFunction model,
                                 MultivariateMatrixFunction jacobian,
                                 ConvergenceChecker<PointVectorValuePair> checker,
                                 double[] start,
                                 int maxEval,
                                 int maxIter,
                                 boolean useLU) {
        super(target, weight, weightSqrt, model, jacobian, checker, start, maxEval, maxIter);

        this.useLU = useLU;
    }

    /**
     * Creates a bare-bones instance.
     * Several calls to {@code withXxx} methods are necessary to obtain
     * an object with all necessary fields set to sensible values.
     * <br/>
     * The default for the algorithm is to solve the normal equations
     * using LU decomposition.
     *
     * @return an instance of this class.
     */
    public static GaussNewtonOptimizer create() {
        return new GaussNewtonOptimizer(null, null, null, null, null, null, null,
                                        0, 0, true);
    }

    /** {@inheritDoc} */
    public GaussNewtonOptimizer withTarget(double[] target) {
        return new GaussNewtonOptimizer(target,
                                        getWeightInternal(),
                                        getWeightSquareRootInternal(),
                                        getModel(),
                                        getJacobian(),
                                        getConvergenceChecker(),
                                        getStart(),
                                        getMaxEvaluations(),
                                        getMaxIterations(),
                                        useLU);
    }

    /** {@inheritDoc} */
    public GaussNewtonOptimizer withWeight(RealMatrix weight) {
        return new GaussNewtonOptimizer(getTargetInternal(),
                                        weight,
                                        null,
                                        getModel(),
                                        getJacobian(),
                                        getConvergenceChecker(),
                                        getStart(),
                                        getMaxEvaluations(),
                                        getMaxIterations(),
                                        useLU);
    }

    /** {@inheritDoc} */
    public GaussNewtonOptimizer withModelAndJacobian(MultivariateVectorFunction model,
                                                     MultivariateMatrixFunction jacobian) {
        return new GaussNewtonOptimizer(getTargetInternal(),
                                        getWeightInternal(),
                                        getWeightSquareRootInternal(),
                                        model,
                                        jacobian,
                                        getConvergenceChecker(),
                                        getStart(),
                                        getMaxEvaluations(),
                                        getMaxIterations(),
                                        useLU);
    }

    /** {@inheritDoc} */
    public GaussNewtonOptimizer withConvergenceChecker(ConvergenceChecker<PointVectorValuePair> checker) {
        return new GaussNewtonOptimizer(getTarget(),
                                        getWeightInternal(),
                                        getWeightSquareRootInternal(),
                                        getModel(),
                                        getJacobian(),
                                        checker,
                                        getStart(),
                                        getMaxEvaluations(),
                                        getMaxIterations(),
                                        useLU);
    }

    /** {@inheritDoc} */
    public GaussNewtonOptimizer withStartPoint(double[] start) {
        return new GaussNewtonOptimizer(getTarget(),
                                        getWeightInternal(),
                                        getWeightSquareRootInternal(),
                                        getModel(),
                                        getJacobian(),
                                        getConvergenceChecker(),
                                        start,
                                        getMaxEvaluations(),
                                        getMaxIterations(),
                                        useLU);
    }

    /** {@inheritDoc} */
    public GaussNewtonOptimizer withMaxIterations(int maxIter) {
        return new GaussNewtonOptimizer(getTarget(),
                                        getWeightInternal(),
                                        getWeightSquareRootInternal(),
                                        getModel(),
                                        getJacobian(),
                                        getConvergenceChecker(),
                                        getStart(),
                                        getMaxEvaluations(),
                                        maxIter,
                                        useLU);
    }

    /** {@inheritDoc} */
    public GaussNewtonOptimizer withMaxEvaluations(int maxEval) {
        return new GaussNewtonOptimizer(getTarget(),
                                        getWeightInternal(),
                                        getWeightSquareRootInternal(),
                                        getModel(),
                                        getJacobian(),
                                        getConvergenceChecker(),
                                        getStart(),
                                        maxEval,
                                        getMaxIterations(),
                                        useLU);
    }

    /**
     * Creates a new instance.
     *
     * @param withLU Whether to use LU decomposition.
     * @return a new instance with all fields identical to this instance except
     * for the givens arguments.
     */
    public GaussNewtonOptimizer withLU(boolean withLU) {
        return new GaussNewtonOptimizer(getTarget(),
                                        getWeightInternal(),
                                        getWeightSquareRootInternal(),
                                        getModel(),
                                        getJacobian(),
                                        getConvergenceChecker(),
                                        getStart(),
                                        getMaxEvaluations(),
                                        getMaxIterations(),
                                        withLU);
    }

    /** {@inheritDoc} */
    @Override
    public PointVectorValuePair doOptimize() {
        final ConvergenceChecker<PointVectorValuePair> checker
            = getConvergenceChecker();

        // Computation will be useless without a checker (see "for-loop").
        if (checker == null) {
            throw new NullArgumentException();
        }

        final double[] targetValues = getTarget();
        final int nR = targetValues.length; // Number of observed data.

        final RealMatrix weightMatrix = getWeight();
        if (weightMatrix.getRowDimension() != nR) {
            throw new DimensionMismatchException(weightMatrix.getRowDimension(), nR);
        }
        if (weightMatrix.getColumnDimension() != nR) {
            throw new DimensionMismatchException(weightMatrix.getColumnDimension(), nR);
        }

        // Diagonal of the weight matrix.
        final double[] residualsWeights = new double[nR];
        for (int i = 0; i < nR; i++) {
            residualsWeights[i] = weightMatrix.getEntry(i, i);
        }

        final double[] currentPoint = getStart();
        final int nC = currentPoint.length;

        // iterate until convergence is reached
        PointVectorValuePair current = null;
        for (boolean converged = false; !converged;) {
            incrementIterationCount();

            // evaluate the objective function and its jacobian
            PointVectorValuePair previous = current;
            // Value of the objective function at "currentPoint".
            final double[] currentObjective = computeObjectiveValue(currentPoint);
            final double[] currentResiduals = computeResiduals(currentObjective);
            final RealMatrix weightedJacobian = computeWeightedJacobian(currentPoint);
            current = new PointVectorValuePair(currentPoint, currentObjective);

            // build the linear problem
            final double[]   b = new double[nC];
            final double[][] a = new double[nC][nC];
            for (int i = 0; i < nR; ++i) {

                final double[] grad   = weightedJacobian.getRow(i);
                final double weight   = residualsWeights[i];
                final double residual = currentResiduals[i];

                // compute the normal equation
                final double wr = weight * residual;
                for (int j = 0; j < nC; ++j) {
                    b[j] += wr * grad[j];
                }

                // build the contribution matrix for measurement i
                for (int k = 0; k < nC; ++k) {
                    double[] ak = a[k];
                    double wgk = weight * grad[k];
                    for (int l = 0; l < nC; ++l) {
                        ak[l] += wgk * grad[l];
                    }
                }
            }

            // Check convergence.
            if (previous != null) {
                converged = checker.converged(getIterations(), previous, current);
                if (converged) {
                    return current;
                }
            }

            try {
                // solve the linearized least squares problem
                RealMatrix mA = new BlockRealMatrix(a);
                DecompositionSolver solver = useLU ?
                        new LUDecomposition(mA).getSolver() :
                        new QRDecomposition(mA).getSolver();
                final double[] dX = solver.solve(new ArrayRealVector(b, false)).toArray();
                // update the estimated parameters
                for (int i = 0; i < nC; ++i) {
                    currentPoint[i] += dX[i];
                }
            } catch (SingularMatrixException e) {
                throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM);
            }
        }
        // Must never happen.
        throw new MathInternalError();
    }
}
