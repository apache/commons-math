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

import org.apache.commons.math.MathException;

/**
 * Implements the <a href="http://mathworld.wolfram.com/Bisection.html">bisection algorithm</a>
 *  for finding zeros of univariate real functions.  This algorithm will find only one zero in the given interval.
 * The function should be continuous but not necessarily smooth.
 * @version $Revision: 1.16 $ $Date: 2004/06/02 00:11:08 $
 */
public class BisectionSolver extends UnivariateRealSolverImpl implements Serializable {
    
    /** Serializable version identifier */
    static final long serialVersionUID = 7137520585963699578L;
    
    /**
     * Construct a solver for the given function.
     * @param f function to solve.
     */
    public BisectionSolver(UnivariateRealFunction f) {
        super(f, 100, 1E-6);
    }

    /**
     * Find a zero in the given interval.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param initial the start value to use (ignored).
     * @return the value where the function is zero
     * @throws MathException if the iteration count was exceeded or the
     *  solver detects convergence problems otherwise.
     */
    public double solve(double min, double max, double initial)
        throws MathException {
            
        return solve(min, max);
    }
    
    /**
     * Find a zero root in the given interval.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return the value where the function is zero
     * @throws MathException if the iteration count was exceeded or the
     *  solver detects convergence problems otherwise.
     */
    public double solve(double min, double max) throws MathException {
        clearResult();

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
                fmin = fm;
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
        
        throw new MathException("Maximum number of iterations exceeded");
    }
}
