/*
 * Copyright 2004 The Apache Software Foundation.
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
 * Implements <a href="http://mathworld.wolfram.com/NewtonsMethod.html">
 * Newton's Method</a> for finding zeros of real univariate functions. This
 * algorithm will find only one zero in the given interval.  The function should
 * be continuous but not necessarily smooth.
 *
 * @version $Revision: 1.5 $ $Date: 2004/06/23 16:26:14 $
 */
public class NewtonSolver extends UnivariateRealSolverImpl implements Serializable {
    
    /** Serializable version identifier */
    static final long serialVersionUID = 2606474895443431607L;
    
    /** The first derivative of the target function. */
    private UnivariateRealFunction derivative;
    
    /**
     * Construct a solver for the given function.
     * @param f function to solve.
     */
    public NewtonSolver(DifferentiableUnivariateRealFunction f) {
        super(f, 100, 1E-6);
        derivative = f.derivative();
    }

    /**
     * Find a zero near the midpoint of <code>min</code> and <code>max</code>.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return the value where the function is zero
     * @throws MathException if the iteration count was exceeded or the
     *  solver detects convergence problems otherwise.
     */
    public double solve(double min, double max) throws MathException {
        return solve(min, max, UnivariateRealSolverUtils.midpoint(min, max));
    }

    /**
     * Find a zero near the value <code>startValue</code>.
     * @param min the lower bound for the interval (ignored).
     * @param max the upper bound for the interval (ignored).
     * @param startValue the start value to use.
     * @return the value where the function is zero
     * @throws MathException if the iteration count was exceeded or the
     *  solver detects convergence problems otherwise.
     */
    public double solve(double min, double max, double startValue)
        throws MathException {
        
        clearResult();

        double x0 = startValue;
        double x1;
        
        int i = 0;
        while (i < maximalIterationCount) {
            x1 = x0 - (f.value(x0) / derivative.value(x0));

            if (Math.abs(x1 - x0) <= absoluteAccuracy) {
                
                setResult(x1, i);
                return x1;
            }
            
            x0 = x1;
            ++i;
        }
        
        throw new MathException("Maximum number of iterations exceeded");
    }

}
