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
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * Implements the <a href="https://en.wikipedia.org/wiki/Riemann_sum#Midpoint_rule">
 * Midpoint Rule</a> for integration of real univariate functions. For
 * reference, see <b>Numerical Mathematics</b>, ISBN 0387989595,
 * chapter 9.2.
 * <p>
 * The function should be integrable.</p>
 *
 * @since 3.3
 */
public class MidPointIntegrator extends BaseAbstractUnivariateIntegrator {

    /** Maximum number of iterations for midpoint. 39 = floor(log_3(2^63)), the
     * maximum number of triplings allowed before exceeding 64-bit bounds.
     */
    private static final int MIDPOINT_MAX_ITERATIONS_COUNT = 39;

    /**
     * Build a midpoint integrator with given accuracies and iterations counts.
     * @param relativeAccuracy relative accuracy of the result
     * @param absoluteAccuracy absolute accuracy of the result
     * @param minimalIterationCount minimum number of iterations
     * @param maximalIterationCount maximum number of iterations
     * @exception org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException if minimal number of iterations
     * is not strictly positive
     * @exception org.apache.commons.math4.legacy.exception.NumberIsTooSmallException if maximal number of iterations
     * is lesser than or equal to the minimal number of iterations
     * @exception NumberIsTooLargeException if maximal number of iterations
     * is greater than 39.
     */
    public MidPointIntegrator(final double relativeAccuracy,
                              final double absoluteAccuracy,
                              final int minimalIterationCount,
                              final int maximalIterationCount) {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > MIDPOINT_MAX_ITERATIONS_COUNT) {
            throw new NumberIsTooLargeException(maximalIterationCount,
                                                MIDPOINT_MAX_ITERATIONS_COUNT, false);
        }
    }

    /**
     * Build a midpoint integrator with given iteration counts.
     * @param minimalIterationCount minimum number of iterations
     * @param maximalIterationCount maximum number of iterations
     * @exception org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException if minimal number of iterations
     * is not strictly positive
     * @exception org.apache.commons.math4.legacy.exception.NumberIsTooSmallException if maximal number of iterations
     * is lesser than or equal to the minimal number of iterations
     * @exception NumberIsTooLargeException if maximal number of iterations
     * is greater than 39.
     */
    public MidPointIntegrator(final int minimalIterationCount,
                              final int maximalIterationCount) {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > MIDPOINT_MAX_ITERATIONS_COUNT) {
            throw new NumberIsTooLargeException(maximalIterationCount,
                                                MIDPOINT_MAX_ITERATIONS_COUNT, false);
        }
    }

    /**
     * Construct a midpoint integrator with default settings.
     * (max iteration count set to {@link #MIDPOINT_MAX_ITERATIONS_COUNT})
     */
    public MidPointIntegrator() {
        super(DEFAULT_MIN_ITERATIONS_COUNT, MIDPOINT_MAX_ITERATIONS_COUNT);
    }

    /**
     * Compute the n-th stage integral of midpoint rule.
     * This function should only be called by API <code>integrate()</code> in the package.
     * To save time it does not verify arguments - caller does.
     * <p>
     * The interval is divided equally into 3^n sections rather than an
     * arbitrary m sections because this configuration can best utilize the
     * already computed values.</p>
     *
     * @param n the stage of 1/3 refinement. Must be larger than 0.
     * @param previousStageResult Result from the previous call to the
     * {@code stage} method.
     * @param min Lower bound of the integration interval.
     * @param diffMaxMin Difference between the lower bound and upper bound
     * of the integration interval.
     * @return the value of n-th stage integral
     * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException if the maximal number of evaluations
     * is exceeded.
     */
    private double stage(final int n,
                         double previousStageResult,
                         double min,
                         double diffMaxMin) {
        // number of points in the previous stage. This stage will contribute
        // 2*3^{n-1} more points.
        final long np = (long) JdkMath.pow(3, n - 1);
        double sum = 0;

        // spacing between adjacent new points
        final double spacing = diffMaxMin / np;
        final double leftOffset = spacing / 6;
        final double rightOffset = 5 * leftOffset;

        double x = min;
        for (long i = 0; i < np; i++) {
            // The first and second new points are located at the new midpoints
            // generated when each previous integration slice is split into 3.
            //
            // |--------x--------|
            // |--x--|--x--|--x--|
            sum += computeObjectiveValue(x + leftOffset);
            sum += computeObjectiveValue(x + rightOffset);
            x += spacing;
        }
        // add the new sum to previously calculated result
        return (previousStageResult + sum * spacing) / 3.0;
    }


    /** {@inheritDoc} */
    @Override
    protected double doIntegrate() {
        final double min = getMin();
        final double diff = getMax() - min;
        final double midPoint = min + 0.5 * diff;

        double oldt = diff * computeObjectiveValue(midPoint);

        while (true) {
            iterations.increment();
            final int i = iterations.getCount();
            final double t = stage(i, oldt, min, diff);
            if (i >= getMinimalIterationCount()) {
                final double delta = JdkMath.abs(t - oldt);
                final double rLimit =
                        getRelativeAccuracy() * (JdkMath.abs(oldt) + JdkMath.abs(t)) * 0.5;
                if ((delta <= rLimit) || (delta <= getAbsoluteAccuracy())) {
                    return t;
                }
            }
            oldt = t;
        }

    }

}
