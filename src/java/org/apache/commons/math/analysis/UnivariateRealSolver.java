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
 * Provide an interface to algorithms for solving for zeros of real univariate
 * functions.
 * An implementation will only search for one zero in the given interval.
 *  
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:09:31 $
 */
public interface UnivariateRealSolver {

    /**
     * Set the upper limit for the number of iterations.
     * 
     * Usually a high iteration count indicates convergence problems. However,
     * the "reasonable value" varies widely for different solvers, users are
     * advised to use the default value supplied by the solver.
     *  
     * An exception will be thrown if the number is exceeded.
     *  
     * @param count maximum number of iterations
     */
    public void setMaximalIterationCount(int count);

    /**
     * Get the upper limit for the number of iterations.
     * 
     * @return the actual upper limit
     */
    public int getMaximalIterationCount();

    /**
     * Reset the upper limit for the number of iterations to the default.
     * 
     * The default value is supplied by the solver implementation.
     * 
     * @see #setMaximalIterationCount(int)
     */
    public void resetMaximalIterationCount();

    /**
     * Set the absolute accuracy.
     * 
     * The default is usually choosen so taht roots in the interval
     * -10..-0.1 and +0.1..+10 can be found wit a reasonable accuracy. If the
     * expected absolute value of your roots is of much smaller magnitude, set
     * this to a smaller value.
     * 
     * Solvers are advised to do a plausibility check with the relative
     * accuracy, but clients should not rely on this.
     *  
     * @param accuracy the accuracy.
     * @throws MathException if the accuracy can't be achieved by the solver or
     *         is otherwise deemed unreasonable. 
     */
    public void setAbsoluteAccuracy(double accuracy) throws MathException;

    /**
     * Get the actual absolute accuracy.
     * 
     * @return the accuracy
     */
    public double getAbsoluteAccuracy();

    /**
     * Reset the absolute accuracy to the default.
     * 
     * The default value is provided by the solver implementation.
     */
    public void resetAbsoluteAccuracy();

    /**
     * Set the relative accuracy.
     * 
     * This is used to stop iterations if the absolute accuracy can't be
     * achieved due to large values or short mantissa length.
     * 
     * If this should be the primary criterium for convergence rather then a
     * safety measure, set the absolute accuracy to a ridiculously small value,
     * like 1E-1000.
     * 
     * @param accuracy the relative accuracy.
     * @throws MathException if the accuracy can't be achieved by the solver or
     *         is otherwise deemed unreasonable. 
     */
    public void setRelativeAccuracy(double accuracy) throws MathException;

    /**
     * Get the actual relative accuracy.
     * @return the accuracy
     */
    public double getRelativeAccuracy();

    /**
     * Reset the relative accuracy to the default.
     * The default value is provided by the solver implementation.
     */
    public void resetRelativeAccuracy();

    /**
     * Set the function value accuracy.
     * 
     * This is used to determine whan an evaluated function value or some other
     * value which is used as divisor is zero.
     * 
     * This is a safety guard and it shouldn't be necesary to change this in
     * general.
     * 
     * @param accuracy the accuracy.
     * @throws MathException if the accuracy can't be achieved by the solver or
     *         is otherwise deemed unreasonable. 
     */
    public void setFunctionValueAccuracy(double accuracy) throws MathException;

    /**
     * Get the actual function value accuracy.
     * @return the accuracy
     */
    public double getFunctionValueAccuracy();

    /**
     * Reset the actual function accuracy to the default.
     * The default value is provided by the solver implementation.
     */
    public void resetFunctionValueAccuracy();

    /**
     * Solve for a zero root in the given interval.
     * A solver may require that the interval brackets a single zero root.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return a value where the function is zero
     * @throws MathException if the iteration count was exceeded or the
     *  solver detects convergence problems otherwise.
     */
    public double solve(double min, double max) throws MathException;

    /**
     * Solve for a zero in the given interval, start at startValue.
     * A solver may require that the interval brackets a single zero root.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param startValue the start value to use
     * @return a value where the function is zero
     * @throws MathException if the iteration count was exceeded or the
     *  solver detects convergence problems otherwise.
     */
    public double solve(double min, double max, double startValue)
        throws MathException;

    /**
     * Get the result of the last run of the solver.
     * @return the last result.
     * @throws MathException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    public double getResult() throws MathException;

    /**
     * Get the number of iterations in the last run of the solver.
     * This is mainly meant for testing purposes. It may occasionally
     * help track down performance problems: if the iteration count
     * is notoriously high, check whether the function is evaluated
     * properly, and whether another solver is more amenable to the
     * problem.
     * @return the last iteration count.
     * @throws MathException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    public int getIterationCount() throws MathException;
}