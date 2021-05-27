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
package org.apache.commons.math4.legacy.analysis.integration;

import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.util.FastMath;

/**
 * Implements <a href="http://mathworld.wolfram.com/SimpsonsRule.html">
 * Simpson's Rule</a> for integration of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 3.
 * <p>
 * This implementation employs the basic trapezoid rule to calculate Simpson's
 * rule.</p>
 *
 * @since 1.2
 */
public class SimpsonIntegrator extends BaseAbstractUnivariateIntegrator {

    /** Maximal number of iterations for Simpson. */
    public static final int SIMPSON_MAX_ITERATIONS_COUNT = 63;

    /**
     * Build a Simpson integrator with given accuracies and iterations counts.
     * @param relativeAccuracy relative accuracy of the result
     * @param absoluteAccuracy absolute accuracy of the result
     * @param minimalIterationCount minimum number of iterations
     * @param maximalIterationCount maximum number of iterations
     * (must be less than or equal to {@link #SIMPSON_MAX_ITERATIONS_COUNT})
     * @exception org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException if minimal number of iterations
     * is not strictly positive
     * @exception org.apache.commons.math4.legacy.exception.NumberIsTooSmallException if maximal number of iterations
     * is lesser than or equal to the minimal number of iterations
     * @exception NumberIsTooLargeException if maximal number of iterations
     * is greater than {@link #SIMPSON_MAX_ITERATIONS_COUNT}
     */
    public SimpsonIntegrator(final double relativeAccuracy,
                             final double absoluteAccuracy,
                             final int minimalIterationCount,
                             final int maximalIterationCount) {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > SIMPSON_MAX_ITERATIONS_COUNT) {
            throw new NumberIsTooLargeException(maximalIterationCount,
                                                SIMPSON_MAX_ITERATIONS_COUNT, false);
        }
    }

    /**
     * Build a Simpson integrator with given iteration counts.
     * @param minimalIterationCount minimum number of iterations
     * @param maximalIterationCount maximum number of iterations
     * (must be less than or equal to {@link #SIMPSON_MAX_ITERATIONS_COUNT})
     * @exception org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException if minimal number of iterations
     * is not strictly positive
     * @exception org.apache.commons.math4.legacy.exception.NumberIsTooSmallException if maximal number of iterations
     * is lesser than or equal to the minimal number of iterations
     * @exception NumberIsTooLargeException if maximal number of iterations
     * is greater than {@link #SIMPSON_MAX_ITERATIONS_COUNT}
     */
    public SimpsonIntegrator(final int minimalIterationCount,
                             final int maximalIterationCount) {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > SIMPSON_MAX_ITERATIONS_COUNT) {
            throw new NumberIsTooLargeException(maximalIterationCount,
                                                SIMPSON_MAX_ITERATIONS_COUNT, false);
        }
    }

    /**
     * Construct an integrator with default settings.
     * (max iteration count set to {@link #SIMPSON_MAX_ITERATIONS_COUNT})
     */
    public SimpsonIntegrator() {
        super(DEFAULT_MIN_ITERATIONS_COUNT, SIMPSON_MAX_ITERATIONS_COUNT);
    }

    /** {@inheritDoc} */
    @Override
    protected double doIntegrate() {
        // Simpson's rule requires at least two trapezoid stages.
        // So we set the first sum using two trapezoid stages.
        final TrapezoidIntegrator qtrap = new TrapezoidIntegrator();

        final double s0 = qtrap.stage(this, 0);
        double oldt = qtrap.stage(this, 1);
        double olds = (4 * oldt - s0) / 3.0;
        while (true) {
            // The first iteration is the first refinement of the sum.
            iterations.increment();
            final int i = getIterations();
            final double t = qtrap.stage(this, i + 1); // 1-stage ahead of the iteration
            final double s = (4 * t - oldt) / 3.0;
            if (i >= getMinimalIterationCount()) {
                final double delta = FastMath.abs(s - olds);
                final double rLimit = getRelativeAccuracy() * (FastMath.abs(olds) + FastMath.abs(s)) * 0.5;
                if (delta <= rLimit ||
                    delta <= getAbsoluteAccuracy()) {
                    return s;
                }
            }
            olds = s;
            oldt = t;
        }
    }
}
