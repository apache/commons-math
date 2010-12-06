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

import org.apache.commons.math.exception.NoBracketingException;
import org.apache.commons.math.util.FastMath;


/**
 * Implements a modified version of the
 * <a href="http://mathworld.wolfram.com/SecantMethod.html">secant method</a>
 * for approximating a zero of a real univariate function.
 * <p>
 * The algorithm is modified to maintain bracketing of a root by successive
 * approximations. Because of forced bracketing, convergence may be slower than
 * the unrestricted secant algorithm. However, this implementation should in
 * general outperform the
 * <a href="http://mathworld.wolfram.com/MethodofFalsePosition.html">
 * regula falsi method.</a></p>
 * <p>
 * The function is assumed to be continuous but not necessarily smooth.</p>
 *
 * @version $Revision$ $Date$
 */
public class SecantSolver extends AbstractUnivariateRealSolver {
    /** Default absolute accuracy. */
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1e-6;

    /**
     * Construct a solver with default accuracy (1e-6).
     */
    public SecantSolver() {
        this(DEFAULT_ABSOLUTE_ACCURACY);
    }
    /**
     * Construct a solver.
     *
     * @param absoluteAccuracy Absolute accuracy.
     */
    public SecantSolver(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     */
    public SecantSolver(double relativeAccuracy,
                        double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double doSolve() {
        double min = getMin();
        double max = getMax();
        verifyInterval(min, max);

        final double functionValueAccuracy = getFunctionValueAccuracy();

        // Index 0 is the old approximation for the root.
        // Index 1 is the last calculated approximation  for the root.
        // Index 2 is a bracket for the root with respect to x0.
        // OldDelta is the length of the bracketing interval of the last
        // iteration.
        double x0 = min;
        double x1 = max;

        double y0 = computeObjectiveValue(x0);
        // return the first endpoint if it is good enough
        if (FastMath.abs(y0) <= functionValueAccuracy) {
            return x0;
        }

        // return the second endpoint if it is good enough
        double y1 = computeObjectiveValue(x1);
        if (FastMath.abs(y1) <= functionValueAccuracy) {
            return x1;
        }

        // Verify bracketing
        if (y0 * y1 >= 0) {
            throw new NoBracketingException(min, max, y0, y1);
        }

        final double absoluteAccuracy = getAbsoluteAccuracy();
        final double relativeAccuracy = getRelativeAccuracy();

        double x2 = x0;
        double y2 = y0;
        double oldDelta = x2 - x1;
        while (true) {
            if (FastMath.abs(y2) < FastMath.abs(y1)) {
                x0 = x1;
                x1 = x2;
                x2 = x0;
                y0 = y1;
                y1 = y2;
                y2 = y0;
            }
            if (FastMath.abs(y1) <= functionValueAccuracy) {
                return x1;
            }
            if (FastMath.abs(oldDelta) < FastMath.max(relativeAccuracy * FastMath.abs(x1),
                                                      absoluteAccuracy)) {
                return x1;
            }
            double delta;
            if (FastMath.abs(y1) > FastMath.abs(y0)) {
                // Function value increased in last iteration. Force bisection.
                delta = 0.5 * oldDelta;
            } else {
                delta = (x0 - x1) / (1 - y0 / y1);
                if (delta / oldDelta > 1) {
                    // New approximation falls outside bracket.
                    // Fall back to bisection.
                    delta = 0.5 * oldDelta;
                }
            }
            x0 = x1;
            y0 = y1;
            x1 = x1 + delta;
            y1 = computeObjectiveValue(x1);
            if ((y1 > 0) == (y2 > 0)) {
                // New bracket is (x0,x1).
                x2 = x0;
                y2 = y0;
            }
            oldDelta = x2 - x1;
        }
    }
}
