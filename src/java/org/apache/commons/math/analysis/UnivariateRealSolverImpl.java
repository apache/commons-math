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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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
 * Provide a default implementation for several functions useful to generic
 * solvers.
 *  
 * @author pietsch at apache.org
 */
public abstract class UnivariateRealSolverImpl
    implements UnivariateRealSolver {

    /** Maximum absolute error. */
    protected double absoluteAccuracy;

    /** Maximum relative error. */
    protected double relativeAccuracy;

    /** Maximum error of function. */
    protected double functionValueAccuracy;

    /** Maximum number of iterations. */
    protected int maximalIterationCount;

    /** Default maximum absolute error. */
    protected double defaultAbsoluteAccuracy;

    /** Default maximum relative error. */
    protected double defaultRelativeAccuracy;

    /** Default maximum error of function. */
    protected double defaultFunctionValueAccuracy;

    /** Default maximum number of iterations. */
    protected int defaultMaximalIterationCount;

    /** Indicates where a root has been computed. */
    protected boolean resultComputed = false;

    /** The last computed root. */
    protected double result;

    // Mainly for test framework.
    /** The last iteration count. */
    protected int iterationCount;

    /** The function to solve. */
    protected UnivariateRealFunction f;

    /**
     * Construct a solver with given iteration count and accuracy.
     * @param f the function to solve.
     * @param defaultAbsoluteAccuracy maximum absolue error.
     * @param defaultMaximalIterationCount maximum number of iterations.
     */
    protected UnivariateRealSolverImpl(
        UnivariateRealFunction f,
        int defaultMaximalIterationCount,
        double defaultAbsoluteAccuracy) {
        
        super();
        
        this.f = f;
        this.defaultAbsoluteAccuracy = defaultAbsoluteAccuracy;
        this.defaultRelativeAccuracy = 1E-14;
        this.defaultFunctionValueAccuracy = 1E-15;
        this.absoluteAccuracy = defaultAbsoluteAccuracy;
        this.relativeAccuracy = defaultRelativeAccuracy;
        this.functionValueAccuracy = defaultFunctionValueAccuracy;
        this.defaultMaximalIterationCount = defaultMaximalIterationCount;
        this.maximalIterationCount = defaultMaximalIterationCount;
    }

    /**
     * Access the last computed root.
     * @return the last computed root.
     * @throws MathException if no root has been computed.
     */
    public double getResult() throws MathException {
        if (resultComputed) {
            return result;
        } else {
            // TODO: could this be an IllegalStateException instead?
            throw new MathException("No result available");
        }
    }

    /**
     * Access the last iteration count.
     * @return the last iteration count.
     * @throws MathException if no root has been computed.
     *  
     */
    public int getIterationCount() throws MathException {
        if (resultComputed) {
            return iterationCount;
        } else {
            // TODO: could this be an IllegalStateException instead?
            throw new MathException("No result available");
        }
    }

    /**
     * Convenience function for implementations.
     * @param result the result to set
     * @param iterationCount the iteration count to set
     */
    protected final void setResult(double result, int iterationCount) {
        this.result = result;
        this.iterationCount = iterationCount;
        this.resultComputed = true;
    }

    /**
     * Convenience function for implementations.
     */
    protected final void clearResult() {
        this.resultComputed = false;
    }

    /**
     * Set the absolute accuracy.
     * 
     * @param accuracy the accuracy.
     * @throws MathException if the accuracy can't be achieved by the solver or
     *         is otherwise deemed unreasonable. 
     */
    public void setAbsoluteAccuracy(double accuracy)
        throws MathException {
        absoluteAccuracy = accuracy;
    }

    /**
     * Get the actual absolute accuracy.
     * 
     * @return the accuracy
     */
    public double getAbsoluteAccuracy() {
        return absoluteAccuracy;
    }

    /**
     * Reset the absolute accuracy to the default.
     */
    public void resetAbsoluteAccuracy() {
        absoluteAccuracy = defaultAbsoluteAccuracy;
    }

    /**
     * Set the upper limit for the number of iterations.
     * 
     * @param count maximum number of iterations
     */
    public void setMaximalIterationCount(int count) {
        maximalIterationCount = count;
    }

    /**
     * Get the upper limit for the number of iterations.
     * 
     * @return the actual upper limit
     */
    public int getMaximalIterationCount() {
        return maximalIterationCount;
    }

    /**
     * Reset the upper limit for the number of iterations to the default.
     */
    public void resetMaximalIterationCount() {
        maximalIterationCount = defaultMaximalIterationCount;
    }

    /**
     * Set the relative accuracy.
     * 
     * @param accuracy the relative accuracy.
     * @throws MathException if the accuracy can't be achieved by the solver or
     *         is otherwise deemed unreasonable. 
     */
    public void setRelativeAccuracy(double accuracy) throws MathException {
        relativeAccuracy = accuracy;
    }

    /**
     * Get the actual relative accuracy.
     * @return the accuracy
     */
    public double getRelativeAccuracy() {
        return relativeAccuracy;
    }

    /**
     * Reset the relative accuracy to the default.
     */
    public void resetRelativeAccuracy() {
        relativeAccuracy = defaultRelativeAccuracy;
    }

    /**
     * Set the function value accuracy.
     * 
     * @param accuracy the accuracy.
     * @throws MathException if the accuracy can't be achieved by the solver or
     *         is otherwise deemed unreasonable. 
     */
    public void setFunctionValueAccuracy(double accuracy)
        throws MathException {
        functionValueAccuracy = accuracy;
    }

    /**
     * Get the actual function value accuracy.
     * @return the accuracy
     */
    public double getFunctionValueAccuracy() {
        return functionValueAccuracy;
    }

    /**
     * Reset the actual function accuracy to the default.
     */
    public void resetFunctionValueAccuracy() {
        functionValueAccuracy = defaultFunctionValueAccuracy;
    }
}
