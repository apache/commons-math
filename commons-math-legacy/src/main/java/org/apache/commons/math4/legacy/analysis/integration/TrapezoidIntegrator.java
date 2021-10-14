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
 * Implements the <a href="http://mathworld.wolfram.com/TrapezoidalRule.html">
 * Trapezoid Rule</a> for integration of real univariate functions.
 *
 * See <b>Introduction to Numerical Analysis</b>, ISBN 038795452X, chapter 3.
 *
 * <p>
 * The function should be integrable.
 *
 * <p>
 * <em>Caveat:</em> At each iteration, the algorithm refines the estimation by
 * evaluating the function twice as many times as in the previous iteration;
 * When specifying a {@link #integrate(int,UnivariateFunction,double,double)
 * maximum number of function evaluations}, the caller must ensure that it
 * is compatible with the {@link #TrapezoidIntegrator(int,int) requested
 * minimal number of iterations}.
 *
 * @since 1.2
 */
public class TrapezoidIntegrator extends BaseAbstractUnivariateIntegrator {
    /** Maximum number of iterations for trapezoid. */
    private static final int TRAPEZOID_MAX_ITERATIONS_COUNT = 30;

    /** Intermediate result. */
    private double s;

    /**
     * Build a trapezoid integrator with given accuracies and iterations counts.
     * @param relativeAccuracy relative accuracy of the result
     * @param absoluteAccuracy absolute accuracy of the result
     * @param minimalIterationCount minimum number of iterations
     * @param maximalIterationCount maximum number of iterations
     * @throws org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException
     * if {@code minimalIterationCount <= 0}.
     * @throws org.apache.commons.math4.legacy.exception.NumberIsTooSmallException
     * if {@code maximalIterationCount < minimalIterationCount}.
     * is lesser than or equal to the minimal number of iterations
     * @throws NumberIsTooLargeException if {@code maximalIterationCount > 30}.
     */
    public TrapezoidIntegrator(final double relativeAccuracy,
                               final double absoluteAccuracy,
                               final int minimalIterationCount,
                               final int maximalIterationCount) {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > TRAPEZOID_MAX_ITERATIONS_COUNT) {
            throw new NumberIsTooLargeException(maximalIterationCount,
                                                TRAPEZOID_MAX_ITERATIONS_COUNT, false);
        }
    }

    /**
     * Build a trapezoid integrator with given iteration counts.
     * @param minimalIterationCount minimum number of iterations
     * @param maximalIterationCount maximum number of iterations
     * @throws org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException
     * if {@code minimalIterationCount <= 0}.
     * @throws org.apache.commons.math4.legacy.exception.NumberIsTooSmallException
     * if {@code maximalIterationCount < minimalIterationCount}.
     * is lesser than or equal to the minimal number of iterations
     * @throws NumberIsTooLargeException if {@code maximalIterationCount > 30}.
     */
    public TrapezoidIntegrator(final int minimalIterationCount,
                               final int maximalIterationCount) {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > TRAPEZOID_MAX_ITERATIONS_COUNT) {
            throw new NumberIsTooLargeException(maximalIterationCount,
                                                TRAPEZOID_MAX_ITERATIONS_COUNT, false);
        }
    }

    /**
     * Construct a trapezoid integrator with default settings.
     */
    public TrapezoidIntegrator() {
        super(DEFAULT_MIN_ITERATIONS_COUNT, TRAPEZOID_MAX_ITERATIONS_COUNT);
    }

    /**
     * Compute the n-th stage integral of trapezoid rule. This function
     * should only be called by API <code>integrate()</code> in the package.
     * To save time it does not verify arguments - caller does.
     * <p>
     * The interval is divided equally into 2^n sections rather than an
     * arbitrary m sections because this configuration can best utilize the
     * already computed values.</p>
     *
     * @param baseIntegrator integrator holding integration parameters
     * @param n the stage of 1/2 refinement, n = 0 is no refinement
     * @return the value of n-th stage integral
     * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException if the maximal number of evaluations
     * is exceeded.
     */
    double stage(final BaseAbstractUnivariateIntegrator baseIntegrator, final int n) {
        if (n == 0) {
            final double max = baseIntegrator.getMax();
            final double min = baseIntegrator.getMin();
            s = 0.5 * (max - min) *
                      (baseIntegrator.computeObjectiveValue(min) +
                       baseIntegrator.computeObjectiveValue(max));
            return s;
        } else {
            final long np = 1L << (n-1);           // number of new points in this stage
            double sum = 0;
            final double max = baseIntegrator.getMax();
            final double min = baseIntegrator.getMin();
            // spacing between adjacent new points
            final double spacing = (max - min) / np;
            double x = min + 0.5 * spacing;    // the first new point
            for (long i = 0; i < np; i++) {
                sum += baseIntegrator.computeObjectiveValue(x);
                x += spacing;
            }
            // add the new sum to previously calculated result
            s = 0.5 * (s + sum * spacing);
            return s;
        }
    }

    /** {@inheritDoc} */
    @Override
    protected double doIntegrate() {
        double oldt = stage(this, 0);
        iterations.increment();
        while (true) {
            final int i = iterations.getCount();
            final double t = stage(this, i);
            if (i >= getMinimalIterationCount()) {
                final double delta = JdkMath.abs(t - oldt);
                final double rLimit =
                    getRelativeAccuracy() * (JdkMath.abs(oldt) + JdkMath.abs(t)) * 0.5;
                if ((delta <= rLimit) || (delta <= getAbsoluteAccuracy())) {
                    return t;
                }
            }
            oldt = t;
            iterations.increment();
        }
    }
}
