/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.commons.math.MathRuntimeException;

/**
 * Provide a default implementation for several generic functions.
 *  
 * @version $Revision$ $Date$
 * @since 1.2
 */
public abstract class UnivariateRealIntegratorImpl implements
    UnivariateRealIntegrator, Serializable {

    /** serializable version identifier */
    static final long serialVersionUID = -3365294665201465048L;

    /** maximum relative error */
    protected double relativeAccuracy;

    /** maximum number of iterations */
    protected int maximalIterationCount;

    /** minimum number of iterations */
    protected int minimalIterationCount;

    /** default maximum relative error */
    protected double defaultRelativeAccuracy;

    /** default maximum number of iterations */
    protected int defaultMaximalIterationCount;

    /** default minimum number of iterations */
    protected int defaultMinimalIterationCount;

    /** indicates whether an integral has been computed */
    protected boolean resultComputed = false;

    /** the last computed integral */
    protected double result;

    /** the last iteration count */
    protected int iterationCount;

    /** the integrand function */
    protected UnivariateRealFunction f;

    /**
     * Construct an integrator with given iteration count and accuracy.
     * 
     * @param f the integrand function
     * @param defaultMaximalIterationCount maximum number of iterations
     * @throws IllegalArgumentException if f is null or the iteration
     * limits are not valid
     */
    protected UnivariateRealIntegratorImpl(
        UnivariateRealFunction f,
        int defaultMaximalIterationCount) throws IllegalArgumentException {
        
        if (f == null) {
            throw new IllegalArgumentException("Function can not be null.");
        }

        this.f = f;
        // parameters that may depend on algorithm
        this.defaultMaximalIterationCount = defaultMaximalIterationCount;
        this.maximalIterationCount = defaultMaximalIterationCount;
        // parameters that are problem specific
        this.defaultRelativeAccuracy = 1E-6;
        this.relativeAccuracy = defaultRelativeAccuracy;
        this.defaultMinimalIterationCount = 3;
        this.minimalIterationCount = defaultMinimalIterationCount;
        
        verifyIterationCount();
    }

    /**
     * Access the last computed integral.
     * 
     * @return the last computed integral
     * @throws IllegalStateException if no integral has been computed
     */
    public double getResult() throws IllegalStateException {
        if (resultComputed) {
            return result;
        } else {
            throw MathRuntimeException.createIllegalStateException("no result available", null);
        }
    }

    /**
     * Access the last iteration count.
     * 
     * @return the last iteration count
     * @throws IllegalStateException if no integral has been computed
     */
    public int getIterationCount() throws IllegalStateException {
        if (resultComputed) {
            return iterationCount;
        } else {
            throw MathRuntimeException.createIllegalStateException("no result available", null);
        }
    }

    /**
     * Convenience function for implementations.
     * 
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
     * Set the lower limit for the number of iterations.
     * 
     * @param count minimum number of iterations
     */
    public void setMinimalIterationCount(int count) {
        minimalIterationCount = count;
    }

    /**
     * Get the lower limit for the number of iterations.
     * 
     * @return the actual lower limit
     */
    public int getMinimalIterationCount() {
        return minimalIterationCount;
    }

    /**
     * Reset the lower limit for the number of iterations to the default.
     */
    public void resetMinimalIterationCount() {
        minimalIterationCount = defaultMinimalIterationCount;
    }

    /**
     * Set the relative accuracy.
     * 
     * @param accuracy the relative accuracy
     * @throws IllegalArgumentException if the accuracy can't be achieved by
     * the integrator or is otherwise deemed unreasonable
     */
    public void setRelativeAccuracy(double accuracy) {
        relativeAccuracy = accuracy;
    }

    /**
     * Get the actual relative accuracy.
     *
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
     * Returns true if the arguments form a (strictly) increasing sequence
     * 
     * @param start first number
     * @param mid second number
     * @param end third number
     * @return true if the arguments form an increasing sequence
     */
    protected boolean isSequence(double start, double mid, double end) {
        return (start < mid) && (mid < end);
    }

    /**
     * Verifies that the endpoints specify an interval.
     * 
     * @param lower lower endpoint
     * @param upper upper endpoint
     * @throws IllegalArgumentException if not interval
     */
    protected void verifyInterval(double lower, double upper) throws
        IllegalArgumentException {
        if (lower >= upper) {
            throw new IllegalArgumentException
                ("Endpoints do not specify an interval: [" + lower +
                ", " + upper + "]");
        }       
    }

    /**
     * Verifies that the upper and lower limits of iterations are valid.
     * 
     * @throws IllegalArgumentException if not valid
     */
    protected void verifyIterationCount() throws IllegalArgumentException {
        if (!isSequence(0, minimalIterationCount, maximalIterationCount+1)) {
            throw new IllegalArgumentException
                ("Invalid iteration limits: min=" + minimalIterationCount +
                " max=" + maximalIterationCount);
        }       
    }
}
