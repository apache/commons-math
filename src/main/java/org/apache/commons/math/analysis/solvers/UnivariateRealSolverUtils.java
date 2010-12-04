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

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.NoBracketingException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.util.FastMath;

/**
 * Utility routines for {@link UnivariateRealSolver} objects.
 *
 * @version $Revision$ $Date$
 */
public class UnivariateRealSolverUtils {
    /**
     * Class contains only static methods.
     */
    private UnivariateRealSolverUtils() {}

    /**
     * Convenience method to find a zero of a univariate real function.  A default
     * solver is used.
     *
     * @param function Function.
     * @param x0 Lower bound for the interval.
     * @param x1 Upper bound for the interval.
     * @return a value where the function is zero.
     * @throws IllegalArgumentException if f is null or the endpoints do not
     * specify a valid interval.
     */
    public static double solve(UnivariateRealFunction function, double x0, double x1) {
        if (function == null) {
            throw new NullArgumentException(LocalizedFormats.FUNCTION);
        }
        final UnivariateRealSolver solver = new BrentSolver();
        return solver.solve(Integer.MAX_VALUE, function, x0, x1);
    }

    /**
     * Convenience method to find a zero of a univariate real function.  A default
     * solver is used.
     *
     * @param function Function.
     * @param x0 Lower bound for the interval.
     * @param x1 Upper bound for the interval.
     * @param absoluteAccuracy Accuracy to be used by the solver.
     * @return a value where the function is zero.
     * @throws IllegalArgumentException if {@code function} is {@code null},
     * the endpoints do not specify a valid interval, or the absolute accuracy
     * is not valid for the default solver.
     */
    public static double solve(UnivariateRealFunction function,
                               double x0, double x1,
                               double absoluteAccuracy) {
        if (function == null) {
            throw new NullArgumentException(LocalizedFormats.FUNCTION);
        }
        final UnivariateRealSolver solver = new BrentSolver(absoluteAccuracy);
        return solver.solve(Integer.MAX_VALUE, function, x0, x1);
    }

    /**
     * This method attempts to find two values a and b satisfying <ul>
    * <li> <code> lowerBound <= a < initial < b <= upperBound</code> </li>
     * <li> <code> f(a) * f(b) < 0 </code></li>
     * </ul>
     * If f is continuous on <code>[a,b],</code> this means that <code>a</code>
     * and <code>b</code> bracket a root of f.
     * <p>
     * The algorithm starts by setting
     * <code>a := initial -1; b := initial +1,</code> examines the value of the
     * function at <code>a</code> and <code>b</code> and keeps moving
     * the endpoints out by one unit each time through a loop that terminates
     * when one of the following happens: <ul>
     * <li> <code> f(a) * f(b) < 0 </code> --  success!</li>
     * <li> <code> a = lower </code> and <code> b = upper</code>
     * -- ConvergenceException </li>
     * <li> <code> Integer.MAX_VALUE</code> iterations elapse
     * -- ConvergenceException </li>
     * </ul></p>
     * <p>
     * <strong>Note: </strong> this method can take
     * <code>Integer.MAX_VALUE</code> iterations to throw a
     * <code>ConvergenceException.</code>  Unless you are confident that there
     * is a root between <code>lowerBound</code> and <code>upperBound</code>
     * near <code>initial,</code> it is better to use
     * {@link #bracket(UnivariateRealFunction, double, double, double, int)},
     * explicitly specifying the maximum number of iterations.</p>
     *
     * @param function Function.
     * @param initial Initial midpoint of interval being expanded to
     * bracket a root.
     * @param lowerBound Lower bound (a is never lower than this value)
     * @param upperBound Upper bound (b never is greater than this
     * value).
     * @return a two-element array holding a and b.
     * @throws NoBracketingException if a root cannot be bracketted.
     * @throws IllegalArgumentException if function is null, maximumIterations
     * is not positive, or initial is not between lowerBound and upperBound.
     */
    public static double[] bracket(UnivariateRealFunction function,
                                   double initial,
                                   double lowerBound, double upperBound) {
        return bracket(function, initial, lowerBound, upperBound, Integer.MAX_VALUE);
    }

     /**
     * This method attempts to find two values a and b satisfying <ul>
     * <li> <code> lowerBound <= a < initial < b <= upperBound</code> </li>
     * <li> <code> f(a) * f(b) <= 0 </code> </li>
     * </ul>
     * If f is continuous on <code>[a,b],</code> this means that <code>a</code>
     * and <code>b</code> bracket a root of f.
     * <p>
     * The algorithm starts by setting
     * <code>a := initial -1; b := initial +1,</code> examines the value of the
     * function at <code>a</code> and <code>b</code> and keeps moving
     * the endpoints out by one unit each time through a loop that terminates
     * when one of the following happens: <ul>
     * <li> <code> f(a) * f(b) <= 0 </code> --  success!</li>
     * <li> <code> a = lower </code> and <code> b = upper</code>
     * -- ConvergenceException </li>
     * <li> <code> maximumIterations</code> iterations elapse
     * -- ConvergenceException </li></ul></p>
     *
     * @param function Function.
     * @param initial Initial midpoint of interval being expanded to
     * bracket a root.
     * @param lowerBound Lower bound (a is never lower than this value).
     * @param upperBound Upper bound (b never is greater than this
     * value).
     * @param maximumIterations Maximum number of iterations to perform
     * @return a two element array holding a and b.
     * @throws NoBracketingException if the algorithm fails to find a and b
     * satisfying the desired conditions.
     * @throws IllegalArgumentException if function is null, maximumIterations
     * is not positive, or initial is not between lowerBound and upperBound.
     */
    public static double[] bracket(UnivariateRealFunction function,
                                   double initial,
                                   double lowerBound, double upperBound,
                                   int maximumIterations)  {
        if (function == null) {
            throw new NullArgumentException(LocalizedFormats.FUNCTION);
        }
        if (maximumIterations <= 0)  {
            throw new NotStrictlyPositiveException(LocalizedFormats.INVALID_MAX_ITERATIONS, maximumIterations);
        }
        verifySequence(lowerBound, initial, upperBound);

        double a = initial;
        double b = initial;
        double fa;
        double fb;
        int numIterations = 0;

        do {
            a = FastMath.max(a - 1.0, lowerBound);
            b = FastMath.min(b + 1.0, upperBound);
            fa = function.value(a);

            fb = function.value(b);
            ++numIterations;
        } while ((fa * fb > 0.0) && (numIterations < maximumIterations) &&
                ((a > lowerBound) || (b < upperBound)));

        if (fa * fb > 0.0) {
            throw new NoBracketingException(LocalizedFormats.FAILED_BRACKETING,
                                            a, b, fa, fb,
                                            numIterations, maximumIterations, initial,
                                            lowerBound, upperBound);
        }

        return new double[] {a, b};
    }

    /**
     * Compute the midpoint of two values.
     *
     * @param a first value.
     * @param b second value.
     * @return the midpoint.
     */
    public static double midpoint(double a, double b) {
        return (a + b) * 0.5;
    }

    /**
     * Check whether the function takes opposite signs at the endpoints.
     *
     * @param function Function.
     * @param lower Lower endpoint.
     * @param upper Upper endpoint.
     * @return {@code true} if the function values have opposite signs at the
     * given points.
     */
    public static boolean isBracketing(UnivariateRealFunction function,
                                       final double lower,
                                       final double upper) {
        if (function == null) {
            throw new NullArgumentException(LocalizedFormats.FUNCTION);
        }
        final double fLo = function.value(lower);
        final double fHi = function.value(upper);
        return (fLo > 0 && fHi < 0) || (fLo < 0 && fHi > 0);
    }

    /**
     * Check whether the arguments form a (strictly) increasing sequence.
     *
     * @param start First number.
     * @param mid Second number.
     * @param end Third number.
     * @return {@code true} if the arguments form an increasing sequence.
     */
    public static boolean isSequence(final double start,
                                     final double mid,
                                     final double end) {
        return (start < mid) && (mid < end);
    }

    /**
     * Check that the endpoints specify an interval.
     *
     * @param lower Lower endpoint.
     * @param upper Upper endpoint.
     * @throws NumberIsTooLargeException if {@code lower >= upper}.
     */
    public static void verifyInterval(final double lower,
                                      final double upper) {
        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL,
                                                lower, upper, false);
        }
    }

    /**
     * Check that {@code lower < initial < upper}.
     *
     * @param lower Lower endpoint.
     * @param initial Initial value.
     * @param upper Upper endpoint.
     * @throws NumberIsTooLargeException if {@code lower >= initial} or
     * {@code initial >= upper}.
     */
    public static void verifySequence(final double lower,
                                      final double initial,
                                      final double upper) {
        verifyInterval(lower, initial);
        verifyInterval(initial, upper);
    }

    /**
     * Check that the endpoints specify an interval and the function takes
     * opposite signs at the endpoints.
     *
     * @param function Function.
     * @param lower Lower endpoint.
     * @param upper Upper endpoint.
     * @throws NoBracketingException if function has the same sign at the
     * endpoints.
     */
    public static void verifyBracketing(UnivariateRealFunction function,
                                        final double lower,
                                        final double upper) {
        if (function == null) {
            throw new NullArgumentException(LocalizedFormats.FUNCTION);
        }
        verifyInterval(lower, upper);
        if (!isBracketing(function, lower, upper)) {
            throw new NoBracketingException(lower, upper,
                                            function.value(lower),
                                            function.value(upper));
        }
    }
}
