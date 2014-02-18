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

    /** The decomposition algorithm to use to solve the normal equations. */
    //TODO move to linear package and expand options?
    public static enum Decomposition {
        /** Use {@link LUDecomposition}. */
        LU {
            @Override
            protected DecompositionSolver getSolver(final RealMatrix matrix) {
                return new LUDecomposition(matrix, SINGULARITY_THRESHOLD).getSolver();
            }
        },
        /** Use {@link QRDecomposition}. */
        QR {
            @Override
            protected DecompositionSolver getSolver(final RealMatrix matrix) {
                return new QRDecomposition(matrix, SINGULARITY_THRESHOLD).getSolver();
            }
        };

        /**
         * Decompose the normal equations.
         *
         * @param matrix the normal matrix.
         * @return a solver.
         */
        protected abstract DecompositionSolver getSolver(RealMatrix matrix);
    }

    /**
     * The singularity threshold for matrix decompositions. Determines when a {@link
     * ConvergenceException} is thrown. The current value was the default value for {@link
     * LUDecomposition}.
     */
    private static final double SINGULARITY_THRESHOLD = 1e-11;

    /** Indicator for using LU decomposition. */
    private final Decomposition decomposition;

    /**
     * Creates a Gauss Newton optimizer.
     * <p/>
     * The default for the algorithm is to solve the normal equations using LU
     * decomposition.
     */
    public GaussNewtonOptimizer() {
        this(Decomposition.LU);
    }

    /**
     * Create a Gauss Newton optimizer that uses the given decomposition algorithm to
     * solve the normal equations.
     *
     * @param decomposition the {@link Decomposition} algorithm.
     */
    public GaussNewtonOptimizer(final Decomposition decomposition) {
        this.decomposition = decomposition;
    }

    /**
     * Get the matrix decomposition algorithm used to solve the normal equations.
     *
     * @return the matrix {@link Decomposition} algoritm.
     */
    public Decomposition getDecomposition() {
        return this.decomposition;
    }

    /**
     * Configure the decomposition algorithm.
     *
     * @param decomposition the {@link Decomposition} algorithm to use.
     * @return a new instance.
     */
    public GaussNewtonOptimizer withDecomposition(final Decomposition decomposition) {
        return new GaussNewtonOptimizer(decomposition);
    }

    public Optimum optimize(final LeastSquaresProblem lsp) {
        //create local evaluation and iteration counts
        final Incrementor evaluationCounter = lsp.getEvaluationCounter();
        final Incrementor iterationCounter = lsp.getIterationCounter();
        final ConvergenceChecker<Evaluation> checker
                = lsp.getConvergenceChecker();

        // Computation will be useless without a checker (see "for-loop").
        if (checker == null) {
            throw new NullArgumentException();
        }

        final int nR = lsp.getObservationSize(); // Number of observed data.
        final int nC = lsp.getParameterSize();

        final double[] currentPoint = lsp.getStart();

        // iterate until convergence is reached
        Evaluation current = null;
        while (true) {
            iterationCounter.incrementCount();

            // evaluate the objective function and its jacobian
            Evaluation previous = current;
            // Value of the objective function at "currentPoint".
            evaluationCounter.incrementCount();
            current = lsp.evaluate(currentPoint);
            final double[] currentResiduals = current.computeResiduals();
            final RealMatrix weightedJacobian = current.computeJacobian();

            // Check convergence.
            if (previous != null) {
                if (checker.converged(iterationCounter.getCount(), previous, current)) {
                    return new OptimumImpl(
                            current,
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
                DecompositionSolver solver = this.decomposition.getSolver(mA);
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
                "decomposition=" + decomposition +
                '}';
    }

}
