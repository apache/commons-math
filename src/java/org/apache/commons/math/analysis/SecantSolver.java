/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.analysis;

import org.apache.commons.math.MathException;

/**
 * Provide the secant algorithm for solving for zeros of real univariate
 * functions. Because of forced bracketing, convergence is slower than
 * the unrestricted secant algorithm. However, slow convergence of the
 * Regula Falsi can be avoided.
 * It will only search for one zero in the given interval.
 * The function is supposed to be continuous but not necessarily smooth.
 *  
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:09:31 $
 */
public class SecantSolver extends UnivariateRealSolverImpl {
    /**
     * Construct a solver for the given function.
     * @param f function to solve.
     */
    public SecantSolver(UnivariateRealFunction f) {
        super(f, 100, 1E-6);
    }

    /**
     * Solve for a zero in the given interval.
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
     * Solve for a zero root in the given interval.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return the value where the function is zero
     * @throws MathException if the iteration count was exceeded or the
     *  solver detects convergence problems otherwise.
     */
    public double solve(double min, double max) throws MathException {
        clearResult();
        // Index 0 is the old approximation for the root.
        // Index 1 is the last calculated approximation  for the root.
        // Index 2 is a bracket for the root with respect to x0.
        // OldDelta is the length of the bracketing interval of the last
        // iteration.
        double x0 = min;
        double x1 = max;
        double y0 = f.value(x0);
        double y1 = f.value(x1);
        if ((y0 > 0) == (y1 > 0)) {
            throw new MathException("Interval doesn't bracket a zero.");
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
        throw new MathException("Maximal iteration number exceeded");
    }

}
