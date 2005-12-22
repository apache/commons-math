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

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.ConvergenceException;

/**
 * Implements the <a href="http://mathworld.wolfram.com/Bisection.html">
 * bisection algorithm</a> for finding zeros of univariate real functions. 
 * <p>
 * The function should be continuous but not necessarily smooth.
 * 
 * @version $Revision$ $Date$
 */
public class BisectionSolver extends UnivariateRealSolverImpl {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = 7137520585963699578L;
    
    /**
     * Construct a solver for the given function.
     * 
     * @param f function to solve.
     */
    public BisectionSolver(UnivariateRealFunction f) {
        super(f, 100, 1E-6);
    }

    /**
     * Find a zero in the given interval.
     * 
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param initial the start value to use (ignored).
     * @return the value where the function is zero
     * @throws ConvergenceException the maximum iteration count is exceeded 
     * @throws FunctionEvaluationException if an error occurs evaluating
     *  the function
     * @throws IllegalArgumentException if min is not less than max
     */
    public double solve(double min, double max, double initial)
        throws ConvergenceException, FunctionEvaluationException {
          
        return solve(min, max);
    }
    
    /**
     * Find a zero root in the given interval.
     * 
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded.
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if min is not less than max
     */
    public double solve(double min, double max) throws ConvergenceException,
        FunctionEvaluationException {
        
        clearResult();
        verifyInterval(min,max);
        double m;
        double fm;
        double fmin;
        
        int i = 0;
        while (i < maximalIterationCount) {
            m = UnivariateRealSolverUtils.midpoint(min, max);
           fmin = f.value(min);
           fm = f.value(m);

            if (fm * fmin > 0.0) {
                // max and m bracket the root.
                min = m;
            } else {
                // min and m bracket the root.
                max = m;
            }

            if (Math.abs(max - min) <= absoluteAccuracy) {
                m = UnivariateRealSolverUtils.midpoint(min, max);
                setResult(m, i);
                return m;
            }
            ++i;
        }
        
        throw new ConvergenceException
            ("Maximum number of iterations exceeded: "  + maximalIterationCount);
    }
}
