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
package org.apache.commons.math.analysis.integration;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.exception.MaxCountExceededException;

/**
 * Provide a default implementation for several generic functions.
 *
 * @version $Id$
 * @since 1.2
 */
public abstract class UnivariateRealIntegratorImpl implements UnivariateRealIntegrator {

    /** Maximum absolute error. */
    protected double absoluteAccuracy;

    /** Maximum relative error. */
    protected double relativeAccuracy;

    /** Maximum number of iterations. */
    protected int maximalIterationCount;

    /** minimum number of iterations */
    protected int minimalIterationCount;

    /** default minimum number of iterations */
    protected int defaultMinimalIterationCount;

    /** The last iteration count. */
    protected int iterationCount;

    /** indicates whether an integral has been computed */
    protected boolean resultComputed = false;

    /** the last computed integral */
    protected double result;

    /**
     * Construct an integrator with given iteration count and accuracy.
     *
     * @param maximalIterationCount maximum number of iterations
     */
    protected UnivariateRealIntegratorImpl(final int maximalIterationCount) {

        setMaximalIterationCount(maximalIterationCount);
        setAbsoluteAccuracy(1.0e-15);
        setRelativeAccuracy(1.0e-6);
        setMinimalIterationCount(3);

        verifyIterationCount();
    }

    /** {@inheritDoc} */
    public void setMaximalIterationCount(final int count) {
        maximalIterationCount = count;
    }

    /** {@inheritDoc} */
    public int getMaximalIterationCount() {
        return maximalIterationCount;
    }

    /** {@inheritDoc} */
    public void setAbsoluteAccuracy(double accuracy) {
        absoluteAccuracy = accuracy;
    }

    /** {@inheritDoc} */
    public double getAbsoluteAccuracy() {
        return absoluteAccuracy;
    }

    /** {@inheritDoc} */
    public void setRelativeAccuracy(final double accuracy) {
        relativeAccuracy = accuracy;
    }

    /** {@inheritDoc} */
    public double getRelativeAccuracy() {
        return relativeAccuracy;
    }

    /** {@inheritDoc} */
    public double getResult() throws IllegalStateException {
        if (resultComputed) {
            return result;
        } else {
            throw MathRuntimeException.createIllegalStateException(LocalizedFormats.NO_RESULT_AVAILABLE);
        }
    }

    /**
     * Convenience function for implementations.
     *
     * @param newResult the result to set
     * @param newCount the iteration count to set
     */
    protected final void setResult(final double newResult, final int newCount) {
        this.result         = newResult;
        this.iterationCount = newCount;
        this.resultComputed = true;
    }

    /**
     * Convenience function for implementations.
     */
    protected final void clearResult() {
        this.iterationCount = 0;
        this.resultComputed = false;
    }

    /** {@inheritDoc} */
    public void setMinimalIterationCount(final int count) {
        minimalIterationCount = count;
    }

    /** {@inheritDoc} */
    public int getMinimalIterationCount() {
        return minimalIterationCount;
    }

    /** {@inheritDoc} */
    public void resetMinimalIterationCount() {
        minimalIterationCount = defaultMinimalIterationCount;
    }

    /**
     * Verifies that the endpoints specify an interval.
     *
     * @param lower lower endpoint
     * @param upper upper endpoint
     * @throws IllegalArgumentException if not interval
     */
    protected void verifyInterval(final double lower, final double upper)
        throws IllegalArgumentException {
        if (lower >= upper) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL,
                    lower, upper);
        }
    }

    /**
     * Verifies that the upper and lower limits of iterations are valid.
     *
     * @throws IllegalArgumentException if not valid
     */
    protected void verifyIterationCount() throws IllegalArgumentException {
        if ((minimalIterationCount <= 0) || (maximalIterationCount <= minimalIterationCount)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INVALID_ITERATIONS_LIMITS,
                    minimalIterationCount, maximalIterationCount);
        }
    }

    /**
     * Reset the iterations counter to 0.
     *
     * @since 2.2
     */
    protected void resetIterationsCounter() {
        iterationCount = 0;
    }

    /**
     * Increment the iterations counter by 1.
     *
     * @throws MaxCountExceededException if the maximal number
     * of iterations is exceeded.
     * @since 2.2
     */
    protected void incrementIterationsCounter() {
        if (++iterationCount > maximalIterationCount) {
            throw new MaxCountExceededException(maximalIterationCount);
        }
    }

}
