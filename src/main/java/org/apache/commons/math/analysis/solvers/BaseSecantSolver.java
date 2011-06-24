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

package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.exception.MathInternalError;

/**
 * Base class for all <em>Secant</em>-based methods for root-finding
 * (approximating a zero of a univariate real function).
 *
 * <p>Implementation of the {@link SecantSolver <em>Secant</em>},
 * {@link RegulaFalsiSolver <em>Regula Falsi</em>}, and
 * {@link IllinoisSolver <em>Illinois</em>} methods is based on the
 * following article: M. Dowell and P. Jarratt,
 * <em>A modified regula falsi method for computing the root of an
 * equation</em>, BIT Numerical Mathematics, volume 11, number 2,
 * pages 168-174, Springer, 1971.</p>
 *
 * <p>Implementation of the {@link PegasusSolver <em>Pegasus</em>} method is
 * based on the following article: M. Dowell and P. Jarratt,
 * <em>The "Pegasus" method for computing the root of an equation</em>,
 * BIT Numerical Mathematics, volume 12, number 4, pages 503-508, Springer,
 * 1972.</p>
 *
 * @since 3.0
 * @version $Id$
 */
public abstract class BaseSecantSolver extends AbstractUnivariateRealSolver {
    /** Default absolute accuracy. */
    protected static final double DEFAULT_ABSOLUTE_ACCURACY = 1e-6;
    /** The kinds of solutions that the algorithm may accept. */
    protected AllowedSolutions allowedSolutions = AllowedSolutions.EITHER_SIDE;
    /** The <em>Secant</em>-based root-finding method to use. */
    private final Method method;

    /**
     * Construct a solver.
     *
     * @param absoluteAccuracy absolute accuracy
     * @param method <em>Secant</em>-based root-finding method to use
     */
    protected BaseSecantSolver(final double absoluteAccuracy, final Method method) {
        super(absoluteAccuracy);
        this.method = method;
    }

    /**
     * Construct a solver.
     *
     * @param relativeAccuracy relative accuracy
     * @param absoluteAccuracy absolute accuracy
     * @param method <em>Secant</em>-based root-finding method to use
     */
    protected BaseSecantSolver(final double relativeAccuracy,
                               final double absoluteAccuracy,
                               final Method method) {
        super(relativeAccuracy, absoluteAccuracy);
        this.method = method;
    }

    /** {@inheritDoc} */
    @Override
    protected final double doSolve() {
        // Get initial solution
        double x0 = getMin();
        double x1 = getMax();
        double f0 = computeObjectiveValue(x0);
        double f1 = computeObjectiveValue(x1);

        // If one of the bounds is the exact root, return it. Since these are
        // not under-approximations or over-approximations, we can return them
        // regardless of the allowed solutions.
        if (f0 == 0.0) {
            return x0;
        }
        if (f1 == 0.0) {
            return x1;
        }

        // Verify bracketing of initial solution.
        verifyBracketing(x0, x1);

        // Get accuracies.
        final double ftol = getFunctionValueAccuracy();
        final double atol = getAbsoluteAccuracy();
        final double rtol = getRelativeAccuracy();

        // Variables to hold new bounds.
        double x;
        double fx;

        // Keep track of inverted intervals, meaning that the left bound is
        // larger than the right bound. Not used for the original Secant
        // method.
        boolean inverted = false;

        // Keep finding better approximations.
        while (true) {
            // Calculate the next approximation.
            x = x1 - ((f1 * (x1 - x0)) / (f1 - f0));
            fx = computeObjectiveValue(x);

            // If the new approximation is the exact root, return it. Since
            // this is not an under-approximation or an over-approximation,
            // we can return it regardless of the allowed solutions.
            if (fx == 0.0) {
                return x;
            }

            // Update the bounds with the new approximation.
            if (method == Method.SECANT) {
                x0 = x1;
                f0 = f1;
                x1 = x;
                f1 = fx;
            } else if (f1 * fx < 0) {
                // We had [x0..x1]. We update it to [x1, x]. Note that the
                // value of x1 has switched to the other bound, thus inverting
                // the interval.
                x0 = x1;
                f0 = f1;
                x1 = x;
                f1 = fx;
                inverted = !inverted;
            } else {
                // We had [x0..x1]. We update it to [x0, x].
                if (method == Method.ILLINOIS) {
                    f0 *= 0.5;
                }
                if (method == Method.PEGASUS) {
                    f0 *= f1 / (f1 + fx);
                }
                x1 = x;
                f1 = fx;
            }

            // If the function value of the last approximation is too small,
            // given the function value accuracy, then we can't get close to
            // the root than we already are.
            if (FastMath.abs(f1) <= ftol) {
                switch (allowedSolutions) {
                case EITHER_SIDE:
                    return x1;
                case LEFT_SIDE:
                    if (inverted) {
                        return x1;
                    }
                    break;
                case RIGHT_SIDE:
                    if (!inverted) {
                        return x1;
                    }
                    break;
                default:
                    throw new MathInternalError();
                }
            }

            // If the current interval is within the given accuracies, we
            // are satisfied with the current approximation.
            if (FastMath.abs(x1 - x0) < FastMath.max(rtol * FastMath.abs(x1),
                                                     atol)) {
                switch (allowedSolutions) {
                case EITHER_SIDE:
                    return x1;
                case LEFT_SIDE:
                    return inverted ? x1 : x0;
                case RIGHT_SIDE:
                    return inverted ? x0 : x1;
                default:
                    throw new MathInternalError();
                }
            }
        }
    }

    /** <em>Secant</em>-based root-finding methods. */
    protected enum Method {
        /** The original {@link SecantSolver <em>Secant</em>} method. */
        SECANT,

        /**
         * The {@link RegulaFalsiSolver <em>Regula Falsi</em>} or
         * <em>False Position</em> method.
         */
        REGULA_FALSI,

        /** The {@link IllinoisSolver <em>Illinois</em>} method. */
        ILLINOIS,

        /** The {@link PegasusSolver <em>Pegasus</em>} method. */
        PEGASUS,
    }
}
