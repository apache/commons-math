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

package org.apache.commons.math.optimization.univariate;

import org.apache.commons.math.ConvergingAlgorithmImpl;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.UnivariateRealOptimizer;
import org.apache.commons.math.util.LocalizedFormats;

/**
 * Provide a default implementation for several functions useful to generic
 * optimizers.
 *
 * @version $Revision$ $Date$
 * @since 2.0
 */
public abstract class AbstractUnivariateRealOptimizer
    extends ConvergingAlgorithmImpl implements UnivariateRealOptimizer {

    /** Indicates where a root has been computed. */
    protected boolean resultComputed;

    /** The last computed root. */
    protected double result;

    /** Value of the function at the last computed result. */
    protected double functionValue;

    /** Maximal number of evaluations allowed. */
    private int maxEvaluations;

    /** Number of evaluations already performed. */
    private int evaluations;

    /**
     * Construct a solver with given iteration count and accuracy.
     * FunctionEvaluationExceptionFunctionEvaluationException
     * @param defaultAbsoluteAccuracy maximum absolute error
     * @param defaultMaximalIterationCount maximum number of iterations
     * @throws IllegalArgumentException if f is null or the
     * defaultAbsoluteAccuracy is not valid
     */
    protected AbstractUnivariateRealOptimizer(final int defaultMaximalIterationCount,
                                              final double defaultAbsoluteAccuracy) {
        super(defaultMaximalIterationCount, defaultAbsoluteAccuracy);
        resultComputed = false;
        setMaxEvaluations(Integer.MAX_VALUE);
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
        this.resultComputed = false;
    }

    /** {@inheritDoc} */
    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getEvaluations() {
        return evaluations;
    }

    /**
     * Compute the objective function value.
     * @param f objective function
     * @param point point at which the objective function must be evaluated
     * @return objective function value at specified point
     * @exception FunctionEvaluationException if the function cannot be evaluated
     * or the maximal number of iterations is exceeded
     */
    protected double computeObjectiveValue(final UnivariateRealFunction f,
                                           final double point)
        throws FunctionEvaluationException {
        if (++evaluations > maxEvaluations) {
            throw new FunctionEvaluationException(new MaxEvaluationsExceededException(maxEvaluations),
                                                  point);
        }
        return f.value(point);
    }

}
