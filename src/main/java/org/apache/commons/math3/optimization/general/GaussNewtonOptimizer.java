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

package org.apache.commons.math3.optimization.general;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.SimpleVectorValueChecker;
import org.apache.commons.math3.optimization.PointVectorValuePair;

/**
 * Gauss-Newton least-squares solver.
 * <p>
 * This class solve a least-square problem by solving the normal equations
 * of the linearized problem at each iteration. Either LU decomposition or
 * QR decomposition can be used to solve the normal equations. LU decomposition
 * is faster but QR decomposition is more robust for difficult problems.
 * </p>
 *
 * @version $Id$
 * @since 2.0
 *
 */

public class GaussNewtonOptimizer extends AbstractLeastSquaresOptimizer {
    /** Indicator for using LU decomposition. */
    private final boolean useLU;

    /**
     * Simple constructor with default settings.
     * The normal equations will be solved using LU decomposition and the
     * convergence check is set to a {@link SimpleVectorValueChecker}
     * with default tolerances.
     * @deprecated See {@link SimpleVectorValueChecker#SimpleVectorValueChecker()}
     */
    @Deprecated
    public GaussNewtonOptimizer() {
        this(true);
    }

    /**
     * Simple constructor with default settings.
     * The normal equations will be solved using LU decomposition.
     *
     * @param checker Convergence checker.
     */
    public GaussNewtonOptimizer(ConvergenceChecker<PointVectorValuePair> checker) {
        this(true, checker);
    }

    /**
     * Simple constructor with default settings.
     * The convergence check is set to a {@link SimpleVectorValueChecker}
     * with default tolerances.
     *
     * @param useLU If {@code true}, the normal equations will be solved
     * using LU decomposition, otherwise they will be solved using QR
     * decomposition.
     * @deprecated See {@link SimpleVectorValueChecker#SimpleVectorValueChecker()}
     */
    @Deprecated
    public GaussNewtonOptimizer(final boolean useLU) {
        this(useLU, new SimpleVectorValueChecker());
    }

    /**
     * @param useLU If {@code true}, the normal equations will be solved
     * using LU decomposition, otherwise they will be solved using QR
     * decomposition.
     * @param checker Convergence checker.
     */
    public GaussNewtonOptimizer(final boolean useLU,
                                ConvergenceChecker<PointVectorValuePair> checker) {
        super(checker);
        this.useLU = useLU;
    }

    /** {@inheritDoc} */
    @Override
    public PointVectorValuePair doOptimize() {

        final ConvergenceChecker<PointVectorValuePair> checker
            = getConvergenceChecker();

        // iterate until convergence is reached
        PointVectorValuePair current = null;
        int iter = 0;
        for (boolean converged = false; !converged;) {
            ++iter;

            // evaluate the objective function and its jacobian
            PointVectorValuePair previous = current;
            updateResidualsAndCost();
            updateJacobian();
            current = new PointVectorValuePair(point, objective);

            final double[] targetValues = getTargetRef();
            final double[] residualsWeights = getWeightRef();

            // build the linear problem
            final double[]   b = new double[cols];
            final double[][] a = new double[cols][cols];
            for (int i = 0; i < rows; ++i) {

                final double[] grad   = weightedResidualJacobian[i];
                final double weight   = residualsWeights[i];
                final double residual = objective[i] - targetValues[i];

                // compute the normal equation
                final double wr = weight * residual;
                for (int j = 0; j < cols; ++j) {
                    b[j] += wr * grad[j];
                }

                // build the contribution matrix for measurement i
                for (int k = 0; k < cols; ++k) {
                    double[] ak = a[k];
                    double wgk = weight * grad[k];
                    for (int l = 0; l < cols; ++l) {
                        ak[l] += wgk * grad[l];
                    }
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
                for (int i = 0; i < cols; ++i) {
                    point[i] += dX[i];
                }
            } catch (SingularMatrixException e) {
                throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM);
            }

            // check convergence
            if (checker != null) {
                if (previous != null) {
                    converged = checker.converged(iter, previous, current);
                }
            }
        }
        // we have converged
        return current;
    }
}
