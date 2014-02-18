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

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.util.Incrementor;

/**
 * Gauss-Newton least-squares solver. <p/> <p> This class solve a least-square problem by
 * solving the normal equations of the linearized problem at each iteration. Either LU
 * decomposition or QR decomposition can be used to solve the normal equations. LU
 * decomposition is faster but QR decomposition is more robust for difficult problems.
 * </p>
 *
 * @version $Id$
 * @since 3.3
 */
public class GaussNewtonOptimizer implements LeastSquaresOptimizer {

    /**
     * The singularity threshold for matrix decompositions. Determines when a {@link
     * ConvergenceException} is thrown. The current value was the default value for {@link
     * LUDecomposition}.
     */
    private static final double SINGULARITY_THRESHOLD = 1e-11;

    /** Indicator for using LU decomposition. */
    private final boolean useLU;

    /**
     * Creates a Gauss Newton optimizer.
     *
     * The default for the algorithm is to solve the normal equations
     * using LU decomposition.
     */
    public GaussNewtonOptimizer() {
        this(true);
    }

    /**
     * Creates a Gauss Newton optimizer.
     *
     * @param useLU if {@code true} the {@link LUDecomposition} will be used to solve the
     *              normal equations. Otherwise the {@link QRDecomposition} will be used.
     */
    public GaussNewtonOptimizer(final boolean useLU) {
        this.useLU = useLU;
    }

    /**
     * If the LU decomposition is used in the optimization.
     *
     * @return {@code true} if the LU decomposition is used. {@code false} if the QR
     *         decomposition is used.
     */
    public boolean isUseLU() {
        return useLU;
    }

    /**
     * @param useLU Whether to use LU decomposition.
     * @return this instance.
     */
    public GaussNewtonOptimizer withLU(final boolean useLU) {
        return new GaussNewtonOptimizer(useLU);
    }

    public Optimum optimize(final LeastSquaresProblem lsp) {
        //create local evaluation and iteration counts
        final Incrementor evaluationCounter = lsp.getEvaluationCounter();
        final Incrementor iterationCounter = lsp.getIterationCounter();
        final ConvergenceChecker<PointVectorValuePair> checker
                = lsp.getConvergenceChecker();

        // Computation will be useless without a checker (see "for-loop").
        if (checker == null) {
            throw new NullArgumentException();
        }

        final int nR = lsp.getObservationSize(); // Number of observed data.
        final int nC = lsp.getParameterSize();

        final double[] currentPoint = lsp.getStart();

        // iterate until convergence is reached
        PointVectorValuePair current = null;
        while (true) {
            iterationCounter.incrementCount();

            // evaluate the objective function and its jacobian
            PointVectorValuePair previous = current;
            // Value of the objective function at "currentPoint".
            evaluationCounter.incrementCount();
            final Evaluation value = lsp.evaluate(currentPoint);
            final double[] currentObjective = value.computeValue();
            final double[] currentResiduals = value.computeResiduals();
            final RealMatrix weightedJacobian = value.computeJacobian();
            current = new PointVectorValuePair(currentPoint, currentObjective);

            // Check convergence.
            if (previous != null) {
                if (checker.converged(iterationCounter.getCount(), previous, current)) {
                    return new OptimumImpl(
                            value,
                            evaluationCounter.getCount(),
                            iterationCounter.getCount());
                }
            }

            // build the linear problem
            final double[] b = new double[nC];
            final double[][] a = new double[nC][nC];
            for (int i = 0; i < nR; ++i) {

                final double[] grad = weightedJacobian.getRow(i);
                final double residual = currentResiduals[i];

                // compute the normal equation
                //residual is already weighted
                for (int j = 0; j < nC; ++j) {
                    b[j] += residual * grad[j];
                }

                // build the contribution matrix for measurement i
                for (int k = 0; k < nC; ++k) {
                    double[] ak = a[k];
                    //Jacobian/gradient is already weighted
                    for (int l = 0; l < nC; ++l) {
                        ak[l] += grad[k] * grad[l];
                    }
                }
            }

            try {
                // solve the linearized least squares problem
                RealMatrix mA = new BlockRealMatrix(a);
                DecompositionSolver solver = useLU ?
                        new LUDecomposition(mA, SINGULARITY_THRESHOLD).getSolver() :
                        new QRDecomposition(mA, SINGULARITY_THRESHOLD).getSolver();
                final double[] dX = solver.solve(new ArrayRealVector(b, false)).toArray();
                // update the estimated parameters
                for (int i = 0; i < nC; ++i) {
                    currentPoint[i] += dX[i];
                }
            } catch (SingularMatrixException e) {
                throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM);
            }
        }
    }

    @Override
    public String toString() {
        return "GaussNewtonOptimizer{" +
                "useLU=" + useLU +
                '}';
    }

}
