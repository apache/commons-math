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

    protected double absoluteAccuracy;
    protected double relativeAccuracy;
    protected double functionValueAccuracy;
    protected int maximalIterationCount;

    protected double defaultAbsoluteAccuracy;
    protected double defaultRelativeAccuracy;
    protected double defaultFunctionValueAccuracy;
    protected int defaultMaximalIterationCount;

    protected boolean resultComputed = false;
    protected double result;
    // Mainly for test framework.
    protected int iterationCount;

    protected UnivariateRealSolverImpl(
        int defaultMaximalIterationCount,
        double defaultAbsoluteAccuracy) {
        this.defaultAbsoluteAccuracy = defaultAbsoluteAccuracy;
        this.defaultRelativeAccuracy = 1E-14;
        this.defaultFunctionValueAccuracy = 1E-15;
        this.absoluteAccuracy = defaultAbsoluteAccuracy;
        this.relativeAccuracy = defaultRelativeAccuracy;
        this.functionValueAccuracy = defaultFunctionValueAccuracy;
        this.defaultMaximalIterationCount = defaultMaximalIterationCount;
        this.maximalIterationCount = defaultMaximalIterationCount;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#solve(double, double)
     */
    public double solve(double min, double max) throws MathException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#solve(double, double, double)
     */
    public double solve(double min, double max, double startValue)
        throws MathException {
        throw new UnsupportedOperationException();
    }

    /*
     * Get result of last solver run.
     * @see org.apache.commons.math.UnivariateRealSolver#getResult()
     */
    public double getResult() throws MathException {
        if (resultComputed) {
            return result;
        } else {
            throw new MathException("No result available");
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#getIterationCount()
     */
    public int getIterationCount() throws MathException {
        if (resultComputed) {
            return iterationCount;
        } else {
            throw new MathException("No result available");
        }
    }

    /*
     * Convenience function for implementations.
     * @param result the result to set
     * @param iteratinCount the iteration count to set
     */
    protected final void setResult(double result, int iterationCount) {
        this.result = result;
        this.iterationCount = iterationCount;
        this.resultComputed = true;
    }

    /*
     * Convenience function for implementations.
     */
    protected final void clearResult() {
        this.resultComputed = false;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#setAccuracy(double)
     */
    public void setAbsoluteAccuracy(double accuracy)
        throws MathException {
        absoluteAccuracy = accuracy;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#getAccuracy()
     */
    public double getAbsoluteAccuracy() {
        return absoluteAccuracy;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#resetAbsoluteAccuracy()
     */
    public void resetAbsoluteAccuracy() {
        absoluteAccuracy = defaultAbsoluteAccuracy;
    }

    /* Set maximum iteration count.
     * @see org.apache.commons.math.UnivariateRealSolver#setMaximalIterationCount(int)
     */
    public void setMaximalIterationCount(int count) {
        maximalIterationCount = count;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#getMaximalIterationCount()
     */
    public int getMaximalIterationCount() {
        return maximalIterationCount;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#resetMaximalIterationCount()
     */
    public void resetMaximalIterationCount() {
        maximalIterationCount = defaultMaximalIterationCount;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#setRelativeAccuracy(double)
     */
    public void setRelativeAccuracy(double accuracy) throws MathException {
        relativeAccuracy = accuracy;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#getRelativeAccuracy()
     */
    public double getRelativeAccuracy() {
        return relativeAccuracy;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#resetRelativeAccuracy()
     */
    public void resetRelativeAccuracy() {
        relativeAccuracy = defaultRelativeAccuracy;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#setFunctionValueAccuracy(double)
     */
    public void setFunctionValueAccuracy(double accuracy)
        throws MathException {
        functionValueAccuracy = accuracy;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#getFunctionValueAccuracy()
     */
    public double getFunctionValueAccuracy() {
        return functionValueAccuracy;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealSolver#resetFunctionValueAccuracy()
     */
    public void resetFunctionValueAccuracy() {
        functionValueAccuracy = defaultFunctionValueAccuracy;
    }

}
