/*
 * Copyright 2003-2004 The Apache Software Foundation.
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

import java.io.Serializable;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;


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
 * regula falsi method.</a>
 * <p>
 * The function is assumed to be continuous but not necessarily smooth.
 *  
 * @version $Revision$ $Date$
 */
public class SecantSolver extends UnivariateRealSolverImpl implements Serializable {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = 1984971194738974867L;
    
    /**
     * Construct a solver for the given function.
     * @param f function to solve.
     */
    public SecantSolver(UnivariateRealFunction f) {
        super(f, 100, 1E-6);
    }

    /**
     * Find a zero in the given interval.
     * 
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param initial the start value to use (ignored)
     * @return the value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function 
     * @throws IllegalArgumentException if min is not less than max or the
     * signs of the values of the function at the endpoints are not opposites
     */
    public double solve(double min, double max, double initial)
        throws ConvergenceException, FunctionEvaluationException {
            
        return solve(min, max);
    }
    
    /**
     * Find a zero in the given interval.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return the value where the function is zero
     * @throws ConvergenceException  if the maximum iteration count is exceeded
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
        // Index 2 is a bracket for the root with respect to x0.
        // OldDelta is the length of the bracketing interval of the last
        // iteration.
        double x0 = min;
        double x1 = max;
        double y0 = f.value(x0);
        double y1 = f.value(x1);
        
        // Verify bracketing
        if (y0 * y1 >= 0) {
            throw new IllegalArgumentException
            ("Function values at endpoints do not have different signs." +
                    "  Endpoints: [" + min + "," + max + "]" + 
                    "  Values: [" + y0 + "," + y1 + "]");       
        }
        
        double x2 = x0;
        double y2 = y0;
        double oldDelta = x2 - x1;
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
                setResult(x1, i);
                return result;
            }
            if (Math.abs(oldDelta) <
                Math.max(relativeAccuracy * Math.abs(x1), absoluteAccuracy)) {
                setResult(x1, i);
                return result;
            }
            double delta;
            if (Math.abs(y1) > Math.abs(y0)) {
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
            y1 = f.value(x1);
            if ((y1 > 0) == (y2 > 0)) {
                // New bracket is (x0,x1).                    
                x2 = x0;
                y2 = y0;
            }
            oldDelta = x2 - x1;
            i++;
        }
        throw new ConvergenceException("Maximal iteration number exceeded" + i);
    }

}
