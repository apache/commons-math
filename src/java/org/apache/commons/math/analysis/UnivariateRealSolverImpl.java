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
 * Provide a default implementation for several functions useful to generic
 * solvers.
 *  
 * @version $Revision: 1.12 $ $Date: 2004/04/27 16:42:33 $
 */
public abstract class UnivariateRealSolverImpl
    implements UnivariateRealSolver, Serializable {

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
     * @throws IllegalArgumentException if function is null.
     */
    protected UnivariateRealSolverImpl(
        UnivariateRealFunction f,
        int defaultMaximalIterationCount,
        double defaultAbsoluteAccuracy) {
        
        super();
        
        if (f == null) {
            throw new IllegalArgumentException("function can not be null.");
        }
        
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
