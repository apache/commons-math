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

package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.ConvergingAlgorithmImpl;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.exception.MathUserException;

/**
 * Provide a default implementation for several functions useful to generic
 * solvers.
 *
 * @version $Revision$ $Date$
 * @deprecated in 2.2 (to be removed in 3.0). Please use
 * {@link AbstractUnivariateRealSolver} instead.
 */
@Deprecated
public abstract class UnivariateRealSolverImpl extends ConvergingAlgorithmImpl {

    /** Maximum error of function. */
    protected double functionValueAccuracy;

    /** Default maximum error of function. */
    protected double defaultFunctionValueAccuracy;

    /** Indicates where a root has been computed. */
    protected boolean resultComputed = false;

    /** The last computed root. */
    protected double result;

    /** Value of the function at the last computed result. */
    protected double functionValue;

    /**
     * Construct a solver with given iteration count and accuracy.
     *
     * @param defaultAbsoluteAccuracy maximum absolute error
     * @param defaultMaximalIterationCount maximum number of iterations
     * @throws IllegalArgumentException if f is null or the
     * defaultAbsoluteAccuracy is not valid
     */
    protected UnivariateRealSolverImpl(final int defaultMaximalIterationCount,
                                       final double defaultAbsoluteAccuracy) {
        super(defaultMaximalIterationCount, defaultAbsoluteAccuracy);
        this.defaultFunctionValueAccuracy = 1.0e-15;
        this.functionValueAccuracy = defaultFunctionValueAccuracy;
    }

    /** Check if a result has been computed.
     * @exception IllegalStateException if no result has been computed
     */
    protected void checkResultComputed() throws IllegalStateException {
        if (!resultComputed) {
            throw MathRuntimeException.createIllegalStateException(LocalizedFormats.NO_RESULT_AVAILABLE);
        }
    }

    /** {@inheritDoc} */
    public double getResult() {
        checkResultComputed();
        return result;
    }

    /** {@inheritDoc} */
    public double getFunctionValue() {
        checkResultComputed();
        return functionValue;
    }

    /** {@inheritDoc} */
    public void setFunctionValueAccuracy(final double accuracy) {
        functionValueAccuracy = accuracy;
    }

    /** {@inheritDoc} */
    public double getFunctionValueAccuracy() {
        return functionValueAccuracy;
    }

    /** {@inheritDoc} */
    public void resetFunctionValueAccuracy() {
        functionValueAccuracy = defaultFunctionValueAccuracy;
    }

    /**
     * Convenience function for implementations.
     *
     * @param newResult the result to set
     * @param iterationCount the iteration count to set
     */
    protected final void setResult(final double newResult, final int iterationCount) {
        this.result         = newResult;
        this.iterationCount = iterationCount;
        this.resultComputed = true;
    }

    /**
     * Convenience function for implementations.
     *
     * @param x the result to set
     * @param fx the result to set
     * @param iterationCount the iteration count to set
     */
    protected final void setResult(final double x, final double fx,
                                   final int iterationCount) {
        this.result         = x;
        this.functionValue  = fx;
        this.iterationCount = iterationCount;
        this.resultComputed = true;
    }

    /**
     * Convenience function for implementations.
     */
    protected final void clearResult() {
        this.iterationCount = 0;
        this.resultComputed = false;
    }

    /**
     * Returns true iff the function takes opposite signs at the endpoints.
     *
     * @param lower  the lower endpoint
     * @param upper  the upper endpoint
     * @param function the function
     * @return true if f(lower) * f(upper) < 0
     * @throws MathUserException if an error occurs evaluating the function at the endpoints
     */
    protected boolean isBracketing(final double lower, final double upper,
                                   final UnivariateRealFunction function)
        throws MathUserException {
        final double f1 = function.value(lower);
        final double f2 = function.value(upper);
        return (f1 > 0 && f2 < 0) || (f1 < 0 && f2 > 0);
    }

    /**
     * Returns true if the arguments form a (strictly) increasing sequence
     *
     * @param start  first number
     * @param mid   second number
     * @param end  third number
     * @return true if the arguments form an increasing sequence
     */
    protected boolean isSequence(final double start, final double mid, final double end) {
        return (start < mid) && (mid < end);
    }

    /**
     * Verifies that the endpoints specify an interval,
     * throws IllegalArgumentException if not
     *
     * @param lower  lower endpoint
     * @param upper upper endpoint
     * @throws IllegalArgumentException
     */
    protected void verifyInterval(final double lower, final double upper) {
        if (lower >= upper) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL,
                    lower, upper);
        }
    }

    /**
     * Verifies that <code>lower < initial < upper</code>
     * throws IllegalArgumentException if not
     *
     * @param lower  lower endpoint
     * @param initial initial value
     * @param upper upper endpoint
     * @throws IllegalArgumentException
     */
    protected void verifySequence(final double lower, final double initial, final double upper) {
        if (!isSequence(lower, initial, upper)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INVALID_INTERVAL_INITIAL_VALUE_PARAMETERS,
                    lower, initial, upper);
        }
    }

    /**
     * Verifies that the endpoints specify an interval and the function takes
     * opposite signs at the endpoints, throws IllegalArgumentException if not
     *
     * @param lower  lower endpoint
     * @param upper upper endpoint
     * @param function function
     * @throws IllegalArgumentException
     * @throws MathUserException if an error occurs evaluating the function at the endpoints
     */
    protected void verifyBracketing(final double lower, final double upper,
                                    final UnivariateRealFunction function)
        throws MathUserException {

        verifyInterval(lower, upper);
        if (!isBracketing(lower, upper, function)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.SAME_SIGN_AT_ENDPOINTS,
                    lower, upper, function.value(lower), function.value(upper));
        }
    }
}
