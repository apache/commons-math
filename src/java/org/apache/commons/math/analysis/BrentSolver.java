/*
 * Copyright 2003-2005 The Apache Software Foundation.
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

/**
 * Implements the <a href="http://mathworld.wolfram.com/BrentsMethod.html">
 * Brent algorithm</a> for  finding zeros of real univariate functions.
 * <p>
 * The function should be continuous but not necessarily smooth.
 *  
 * @version $Revision$ $Date$
 */
public class BrentSolver extends UnivariateRealSolverImpl {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = 3350616277306882875L;

    /**
     * Construct a solver for the given function.
     * 
     * @param f function to solve.
     */
    public BrentSolver(UnivariateRealFunction f) {
        super(f, 100, 1E-6);
    }

    /**
     * Find a zero in the given interval.
     * <p>
     * Throws <code>ConvergenceException</code> if the values of the function
     * at the endpoints of the interval have the same sign.
     * 
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param initial the start value to use (ignored).
     * @return the value where the function is zero
     * @throws ConvergenceException the maximum iteration count is exceeded 
     * @throws FunctionEvaluationException if an error occurs evaluating
     *  the function
     * @throws IllegalArgumentException if initial is not between min and max
     */
    public double solve(double min, double max, double initial)
        throws ConvergenceException, FunctionEvaluationException {
            
        return solve(min, max);
    }
    
    /**
     * Find a zero in the given interval.
     * <p>
     * Requires that the values of the function at the endpoints have opposite
     * signs. An <code>IllegalArgumentException</code> is thrown if this is not
     * the case.
     * 
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return the value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function 
     * @throws IllegalArgumentException if min is not less than max or the
     * signs of the values of the function at the endpoints are not opposites
     */
    public double solve(double min, double max) throws ConvergenceException, 
        FunctionEvaluationException {
        
        clearResult();
        verifyInterval(min, max);
        
        // Index 0 is the old approximation for the root.
        // Index 1 is the last calculated approximation  for the root.
        // Index 2 is a bracket for the root with respect to x1.
        double x0 = min;
        double x1 = max;
        double y0;
        double y1;
        y0 = f.value(x0);
        y1 = f.value(x1);
        
        // Verify bracketing
        if (y0 * y1 >= 0) {
            throw new IllegalArgumentException
            ("Function values at endpoints do not have different signs." +
                    "  Endpoints: [" + min + "," + max + "]" + 
                    "  Values: [" + y0 + "," + y1 + "]");       
        }
   
        double x2 = x0;
        double y2 = y0;
        double delta = x1 - x0;
        double oldDelta = delta;

        int i = 0;
        while (i < maximalIterationCount) {
            if (Math.abs(y2) < Math.abs(y1)) {
                x0 = x1;
                x1 = x2;
                x2 = x0;
                y0 = y1;
                y1 = y2;
                y2 = y0;
            }
            if (Math.abs(y1) <= functionValueAccuracy) {
                // Avoid division by very small values. Assume
                // the iteration has converged (the problem may
                // still be ill conditioned)
                setResult(x1, i);
                return result;
            }
            double dx = (x2 - x1);
            double tolerance =
                Math.max(relativeAccuracy * Math.abs(x1), absoluteAccuracy);
            if (Math.abs(dx) <= tolerance) {
                setResult(x1, i);
                return result;
            }
            if ((Math.abs(oldDelta) < tolerance) ||
                    (Math.abs(y0) <= Math.abs(y1))) {
                // Force bisection.
                delta = 0.5 * dx;
                oldDelta = delta;
            } else {
                double r3 = y1 / y0;
                double p;
                double p1;
                if (x0 == x2) {
                    // Linear interpolation.
                    p = dx * r3;
                    p1 = 1.0 - r3;
                } else {
                    // Inverse quadratic interpolation.
                    double r1 = y0 / y2;
                    double r2 = y1 / y2;
                    p = r3 * (dx * r1 * (r1 - r2) - (x1 - x0) * (r2 - 1.0));
                    p1 = (r1 - 1.0) * (r2 - 1.0) * (r3 - 1.0);
                }
                if (p > 0.0) {
                    p1 = -p1;
                } else {
                    p = -p;
                }
                if (2.0 * p >= 1.5 * dx * p1 - Math.abs(tolerance * p1) ||
                        p >= Math.abs(0.5 * oldDelta * p1)) {
                    // Inverse quadratic interpolation gives a value
                    // in the wrong direction, or progress is slow.
                    // Fall back to bisection.
                    delta = 0.5 * dx;
                    oldDelta = delta;
                } else {
                    oldDelta = delta;
                    delta = p / p1;
                }
            }
            // Save old X1, Y1 
            x0 = x1;
            y0 = y1;
            // Compute new X1, Y1
            if (Math.abs(delta) > tolerance) {
                x1 = x1 + delta;
            } else if (dx > 0.0) {
                x1 = x1 + 0.5 * tolerance;
            } else if (dx <= 0.0) {
                x1 = x1 - 0.5 * tolerance;
            }
            y1 = f.value(x1);
            if ((y1 > 0) == (y2 > 0)) {
                x2 = x0;
                y2 = y0;
                delta = x1 - x0;
                oldDelta = delta;
            }
            i++;
        }
        throw new ConvergenceException("Maximum number of iterations exceeded.");
    }
}
