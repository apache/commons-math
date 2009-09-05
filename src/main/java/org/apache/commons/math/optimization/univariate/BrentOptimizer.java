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
package org.apache.commons.math.optimization.univariate;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;

/**
 * Implements Richard Brent's algorithm (from his book "Algorithms for
 * Minimization without Derivatives", p. 79) for finding minima of real
 * univariate functions.
 *
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class BrentOptimizer extends AbstractUnivariateRealOptimizer {

    /**
     * Golden section.
     */
    private static final double GOLDEN_SECTION = 0.5 * (3 - Math.sqrt(5));

    /**
     * Construct a solver.
     */
    public BrentOptimizer() {
        super(100, 1E-10);
    }

    /** {@inheritDoc} */
    public double optimize(final UnivariateRealFunction f, final GoalType goalType,
                           final double min, final double max, final double startValue)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        return optimize(f, goalType, min, max);
    }

    /** {@inheritDoc} */
    public double optimize(final UnivariateRealFunction f, final GoalType goalType,
                           final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        clearResult();
        return localMin(f, goalType, min, max, relativeAccuracy, absoluteAccuracy);
    }

    /**
     * Find the minimum of the function {@code f} within the interval {@code (a, b)}.
     *
     * If the function {@code f} is defined on the interval {@code (a, b)}, then
     * this method finds an approximation {@code x} to the point at which {@code f}
     * attains its minimum.<br/>
     * {@code t} and {@code eps} define a tolerance {@code tol = eps |x| + t} and
     * {@code f} is never evaluated at two points closer together than {@code tol}.
     * {@code eps} should be no smaller than <em>2 macheps</em> and preferable not
     * much less than <em>sqrt(macheps)</em>, where <em>macheps</em> is the relative
     * machine precision. {@code t} should be positive.
     * @param f the function to solve
     * @param goalType type of optimization goal: either {@link GoalType#MAXIMIZE}
     * or {@link GoalType#MINIMIZE}
     * @param a Lower bound of the interval
     * @param b Higher bound of the interval
     * @param eps Relative accuracy
     * @param t Absolute accuracy
     * @return the point at which the function is minimal.
     * @throws MaxIterationsExceededException if the maximum iteration count
     * is exceeded.
     * @throws FunctionEvaluationException if an error occurs evaluating
     * the function.
     */
    private double localMin(final UnivariateRealFunction f, final GoalType goalType,
                            double a, double b, final double eps, final double t)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        double x = a + GOLDEN_SECTION * (b - a);
        double v = x;
        double w = x;
        double e = 0;
        double fx = computeObjectiveValue(f, x);
        if (goalType == GoalType.MAXIMIZE) {
            fx = -fx;
        }
        double fv = fx;
        double fw = fx;

        int count = 0;
        while (count < maximalIterationCount) {
            double m = 0.5 * (a + b);
            double tol = eps * Math.abs(x) + t;
            double t2 = 2 * tol;

            // Check stopping criterion.
            if (Math.abs(x - m) > t2 - 0.5 * (b - a)) {
                double p = 0;
                double q = 0;
                double r = 0;
                double d = 0;
                double u = 0;

                if (Math.abs(e) > tol) { // Fit parabola.
                    r = (x - w) * (fx - fv);
                    q = (x - v) * (fx - fw);
                    p = (x - v) * q - (x - w) * r;
                    q = 2 * (q - r);

                    if (q > 0) {
                        p = -p;
                    } else {
                        q = -q;
                    }

                    r = e;
                    e = d;
                }

                if (Math.abs(p) < Math.abs(0.5 * q * r) &&
                    (p < q * (a - x)) && (p < q * (b - x))) { // Parabolic interpolation step.
                    d = p / q;
                    u = x + d;

                    // f must not be evaluated too close to a or b.
                    if (((u - a) < t2) || ((b - u) < t2)) {
                        d = (x < m) ? tol : -tol;
                    }
                } else { // Golden section step.
                    e = ((x < m) ? b : a) - x;
                    d = GOLDEN_SECTION * e;
                }

                // f must not be evaluated too close to a or b.
                u = x + ((Math.abs(d) > tol) ? d : ((d > 0) ? tol : -tol));
                double fu = computeObjectiveValue(f, u);
                if (goalType == GoalType.MAXIMIZE) {
                    fu = -fu;
                }

                // Update a, b, v, w and x.
                if (fu <= fx) {
                    if (u < x) {
                        b = x;
                    } else {
                        a = x;
                    }
                    v = w;
                    fv = fw;
                    w = x;
                    fw = fx;
                    x = u;
                    fx = fu;
                } else {
                    if (u < x) {
                        a = u;
                    } else {
                        b = u;
                    }
                    if ((fu <= fw) || (w == x)) {
                        v = w;
                        fv = fw;
                        w = u;
                        fw = fu;
                    } else if ((fu <= fv) || (v == x) || (v == w)) {
                        v = u;
                        fv = fu;
                    }
                }
            } else { // termination
                setResult(x, (goalType == GoalType.MAXIMIZE) ? -fx : fx, count);
                return x;
            }

            ++count;
        }

        throw new MaxIterationsExceededException(maximalIterationCount);

    }

}
