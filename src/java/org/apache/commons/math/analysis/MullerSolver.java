/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.analysis;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.util.MathUtils;

/**
 * Implements the <a href="http://mathworld.wolfram.com/MullersMethod.html">
 * Muller's Method</a> for root finding of real univariate functions. For
 * reference, see <b>Elementary Numerical Analysis</b>, ISBN 0070124477,
 * chapter 3.
 * <p>
 * Muller's method applies to both real and complex functions, but here we
 * restrict ourselves to real functions. Methods solve() and solve2() find
 * real zeros, using different ways to bypass complex arithmetics.
 *
 * @version $Revision$ $Date$
 */
public class MullerSolver extends UnivariateRealSolverImpl {

    /** serializable version identifier */
    static final long serialVersionUID = 2619993603551148137L;

    /**
     * Construct a solver for the given function.
     * 
     * @param f function to solve
     */
    public MullerSolver(UnivariateRealFunction f) {
        super(f, 100, 1E-6);
    }

    /**
     * Find a real root in the given interval with initial value.
     * <p>
     * Requires bracketing condition.
     * 
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param initial the start value to use
     * @return the point at which the function value is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double solve(double min, double max, double initial) throws
        ConvergenceException, FunctionEvaluationException {

        // check for zeros before verifying bracketing
        if (f.value(min) == 0.0) { return min; }
        if (f.value(max) == 0.0) { return max; }
        if (f.value(initial) == 0.0) { return initial; }

        verifyBracketing(min, max, f);
        verifySequence(min, initial, max);
        if (isBracketing(min, initial, f)) {
            return solve(min, initial);
        } else {
            return solve(initial, max);
        }
    }

    /**
     * Find a real root in the given interval.
     * <p>
     * Original Muller's method would have function evaluation at complex point.
     * Since our f(x) is real, we have to find ways to avoid that. Bracketing
     * condition is one way to go: by requiring bracketing in every iteration,
     * the newly computed approximation is guaranteed to be real.
     * <p>
     * Normally Muller's method converges quadratically in the vicinity of a
     * zero, however it may be very slow in regions far away from zeros. For
     * example, f(x) = exp(x) - 1, min = -50, max = 100. In such case we use
     * bisection as a safety backup if it performs very poorly.
     * <p>
     * The formulas here use divided differences directly.
     * 
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the point at which the function value is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function 
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double solve(double min, double max) throws ConvergenceException, 
        FunctionEvaluationException {

        // [x0, x2] is the bracketing interval in each iteration
        // x1 is the last approximation and an interpolation point in (x0, x2)
        // x is the new root approximation and new x1 for next round
        // d01, d12, d012 are divided differences
        double x0, x1, x2, x, oldx, y0, y1, y2, y;
        double d01, d12, d012, c1, delta, xplus, xminus, tolerance;

        x0 = min; y0 = f.value(x0);
        x2 = max; y2 = f.value(x2);
        x1 = 0.5 * (x0 + x2); y1 = f.value(x1);

        // check for zeros before verifying bracketing
        if (y0 == 0.0) { return min; }
        if (y2 == 0.0) { return max; }
        verifyBracketing(min, max, f);

        int i = 1;
        oldx = Double.POSITIVE_INFINITY;
        while (i <= maximalIterationCount) {
            // Muller's method employs quadratic interpolation through
            // x0, x1, x2 and x is the zero of the interpolating parabola.
            // Due to bracketing condition, this parabola must have two
            // real roots and we choose one in [x0, x2] to be x.
            d01 = (y1 - y0) / (x1 - x0);
            d12 = (y2 - y1) / (x2 - x1);
            d012 = (d12 - d01) / (x2 - x0);
            c1 = d01 + (x1 - x0) * d012;
            delta = c1 * c1 - 4 * y1 * d012;
            xplus = x1 + (-2.0 * y1) / (c1 + Math.sqrt(delta));
            xminus = x1 + (-2.0 * y1) / (c1 - Math.sqrt(delta));
            // xplus and xminus are two roots of parabola and at least
            // one of them should lie in (x0, x2)
            x = isSequence(x0, xplus, x2) ? xplus : xminus;
            y = f.value(x);

            // check for convergence
            tolerance = Math.max(relativeAccuracy * Math.abs(x), absoluteAccuracy);
            if (Math.abs(x - oldx) <= tolerance) {
                setResult(x, i);
                return result;
            }
            if (Math.abs(y) <= functionValueAccuracy) {
                setResult(x, i);
                return result;
            }

            // Bisect if convergence is too slow. Bisection would waste
            // our calculation of x, hopefully it won't happen often.
            boolean bisect = (x < x1 && (x1 - x0) > 0.95 * (x2 - x0)) ||
                             (x > x1 && (x2 - x1) > 0.95 * (x2 - x0)) ||
                             (x == x1);
            // prepare the new bracketing interval for next iteration
            if (!bisect) {
                x0 = x < x1 ? x0 : x1;
                y0 = x < x1 ? y0 : y1;
                x2 = x > x1 ? x2 : x1;
                y2 = x > x1 ? y2 : y1;
                x1 = x; y1 = y;
                oldx = x;
            } else {
                double xm = 0.5 * (x0 + x2);
                double ym = f.value(xm);
                if (MathUtils.sign(y0) + MathUtils.sign(ym) == 0.0) {
                    x2 = xm; y2 = ym;
                } else {
                    x0 = xm; y0 = ym;
                }
                x1 = 0.5 * (x0 + x2);
                y1 = f.value(x1);
                oldx = Double.POSITIVE_INFINITY;
            }
            i++;
        }
        throw new ConvergenceException("Maximum number of iterations exceeded.");
    }

    /**
     * Find a real root in the given interval.
     * <p>
     * solve2() differs from solve() in the way it avoids complex operations.
     * Except for the initial [min, max], solve2() does not require bracketing
     * condition, e.g. f(x0), f(x1), f(x2) can have the same sign. If complex
     * number arises in the computation, we simply use its modulus as real
     * approximation.
     * <p>
     * Because the interval may not be bracketing, bisection alternative is
     * not applicable here. However in practice our treatment usually works
     * well, especially near real zeros where the imaginary part of complex
     * approximation is often negligible.
     * <p>
     * The formulas here do not use divided differences directly.
     * 
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the point at which the function value is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function 
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double solve2(double min, double max) throws ConvergenceException, 
        FunctionEvaluationException {

        // x2 is the last root approximation
        // x is the new approximation and new x2 for next round
        // x0 < x1 < x2 does not hold here
        double x0, x1, x2, x, oldx, y0, y1, y2, y;
        double q, A, B, C, delta, denominator, tolerance;

        x0 = min; y0 = f.value(x0);
        x1 = max; y1 = f.value(x1);
        x2 = 0.5 * (x0 + x1); y2 = f.value(x2);

        // check for zeros before verifying bracketing
        if (y0 == 0.0) { return min; }
        if (y1 == 0.0) { return max; }
        verifyBracketing(min, max, f);

        int i = 1;
        oldx = Double.POSITIVE_INFINITY;
        while (i <= maximalIterationCount) {
            // quadratic interpolation through x0, x1, x2
            q = (x2 - x1) / (x1 - x0);
            A = q * (y2 - (1 + q) * y1 + q * y0);
            B = (2*q + 1) * y2 - (1 + q) * (1 + q) * y1 + q * q * y0;
            C = (1 + q) * y2;
            delta = B * B - 4 * A * C;
            if (delta >= 0.0) {
                // choose a denominator larger in magnitude
                double dplus = B + Math.sqrt(delta);
                double dminus = B - Math.sqrt(delta);
                denominator = Math.abs(dplus) > Math.abs(dminus) ? dplus : dminus;
            } else {
                // take the modulus of (B +/- Math.sqrt(delta))
                denominator = Math.sqrt(B * B - delta);
            }
            if (denominator != 0) {
                x = x2 - 2.0 * C * (x2 - x1) / denominator;
                // perturb x if it coincides with x1 or x2
                while (x == x1 || x == x2) {
                    x += absoluteAccuracy;
                }
            } else {
                // extremely rare case, get a random number to skip it
                x = min + Math.random() * (max - min);
                oldx = Double.POSITIVE_INFINITY;
            }
            y = f.value(x);

            // check for convergence
            tolerance = Math.max(relativeAccuracy * Math.abs(x), absoluteAccuracy);
            if (Math.abs(x - oldx) <= tolerance) {
                setResult(x, i);
                return result;
            }
            if (Math.abs(y) <= functionValueAccuracy) {
                setResult(x, i);
                return result;
            }

            // prepare the next iteration
            x0 = x1; y0 = y1;
            x1 = x2; y1 = y2;
            x2 = x; y2 = y;
            oldx = x;
            i++;
        }
        throw new ConvergenceException("Maximum number of iterations exceeded.");
    }
}
