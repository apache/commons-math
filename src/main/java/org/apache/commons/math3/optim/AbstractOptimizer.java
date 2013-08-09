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
package org.apache.commons.math3.optim;

import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.fitting.leastsquares.WithMaxEvaluations;
import org.apache.commons.math3.fitting.leastsquares.WithMaxIterations;
import org.apache.commons.math3.fitting.leastsquares.WithConvergenceChecker;

/**
 * Base class for implementing optimizers.
 * It contains the boiler-plate code for counting the number of evaluations
 * of the objective function and the number of iterations of the algorithm,
 * and storing the convergence checker.
 *
 * @param <PAIR> Type of the point/value pair returned by the optimization
 * algorithm.
 * @param <OPTIM> Type of a subclass of this class.
 * This parameter allows to implement fluent API methods at upper levels
 * of the class hierarchy (since the fluent API requires that the actual
 * type of the subclass is returned).
 *
 * @version $Id$
 * @since 3.3
 */
public abstract class AbstractOptimizer<PAIR, OPTIM extends AbstractOptimizer<PAIR, OPTIM>>
    implements WithMaxEvaluations<OPTIM>,
               WithMaxIterations<OPTIM>,
               WithConvergenceChecker<PAIR, OPTIM> {
    /** Evaluations counter. */
    private Incrementor evaluations = new Incrementor(Integer.MAX_VALUE, new MaxEvalCallback());
    /** Iterations counter. */
    private Incrementor iterations = new Incrementor(Integer.MAX_VALUE, new MaxIterCallback());
    /** Convergence checker. */
    private ConvergenceChecker<PAIR> checker = null;

    /**
     * Default constructor.
     */
    protected AbstractOptimizer() {}

    /**
     * Copy constructor.
     *
     * @param other Instance to copy.
     */
    protected AbstractOptimizer(AbstractOptimizer other) {
        checker = other.checker; // XXX Not thread-safe.
        evaluations.setMaximalCount(other.getMaxEvaluations());
        iterations.setMaximalCount(other.getMaxIterations());
    }

    /**
     * Returns this instance, cast to the type of its actual subclass.
     *
     * @return the "self-type" instance.
     */
    protected OPTIM self() {
        final OPTIM optim = (OPTIM) this;
        return optim;
    }

    /** {@inheritDoc} */
    public OPTIM withConvergenceChecker(ConvergenceChecker<PAIR> checker) {
        this.checker = checker;
        return self();
    }

    /** {@inheritDoc} */
    public OPTIM withMaxEvaluations(int max) {
        evaluations.setMaximalCount(max);
        return self();
    }

    /** {@inheritDoc} */
    public OPTIM withMaxIterations(int max) {
        iterations.setMaximalCount(max);
        return self();
    }

    /**
     * Gets the maximal number of function evaluations.
     *
     * @return the maximal number of function evaluations.
     */
    public int getMaxEvaluations() {
        return evaluations.getMaximalCount();
    }

    /**
     * Gets the number of evaluations of the objective function.
     * The number of evaluations corresponds to the last call to the
     * {@code optimize} method. It is 0 if the method has not been
     * called yet.
     *
     * @return the number of evaluations of the objective function.
     */
    public int getEvaluations() {
        return evaluations.getCount();
    }

    /**
     * Gets the maximal number of iterations.
     *
     * @return the maximal number of iterations.
     */
    public int getMaxIterations() {
        return iterations.getMaximalCount();
    }

    /**
     * Gets the number of iterations performed by the algorithm.
     * The number iterations corresponds to the last call to the
     * {@code optimize} method. It is 0 if the method has not been
     * called yet.
     *
     * @return the number of evaluations of the objective function.
     */
    public int getIterations() {
        return iterations.getCount();
    }

    /**
     * Gets the convergence checker.
     *
     * @return the object used to check for convergence.
     */
    public ConvergenceChecker<PAIR> getConvergenceChecker() {
        return checker;
    }

    /**
     * Performs the optimization.
     *
     * @return a point/value pair that satifies the convergence criteria.
     * @throws TooManyEvaluationsException if the maximal number of
     * evaluations is exceeded.
     * @throws TooManyIterationsException if the maximal number of
     * iterations is exceeded.
     */
    public PAIR optimize()
        throws TooManyEvaluationsException,
               TooManyIterationsException {
        // Reset counters.
        evaluations.resetCount();
        iterations.resetCount();
        // Perform optimization.
        return doOptimize();
    }

    /**
     * Performs the bulk of the optimization algorithm.
     *
     * @return the point/value pair giving the optimal value of the
     * objective function.
     */
    protected abstract PAIR doOptimize();

    /**
     * Increment the evaluation count.
     *
     * @throws TooManyEvaluationsException if the allowed evaluations
     * have been exhausted.
     */
    protected void incrementEvaluationCount()
        throws TooManyEvaluationsException {
        evaluations.incrementCount();
    }

    /**
     * Increment the iteration count.
     *
     * @throws TooManyIterationsException if the allowed iterations
     * have been exhausted.
     */
    protected void incrementIterationCount()
        throws TooManyIterationsException {
        iterations.incrementCount();
    }

    /**
     * Defines the action to perform when reaching the maximum number
     * of evaluations.
     */
    private static class MaxEvalCallback
        implements  Incrementor.MaxCountExceededCallback {
        /**
         * {@inheritDoc}
         * @throws TooManyEvaluationsException
         */
        public void trigger(int max) {
            throw new TooManyEvaluationsException(max);
        }
    }

    /**
     * Defines the action to perform when reaching the maximum number
     * of evaluations.
     */
    private static class MaxIterCallback
        implements Incrementor.MaxCountExceededCallback {
        /**
         * {@inheritDoc}
         * @throws TooManyIterationsException
         */
        public void trigger(int max) {
            throw new TooManyIterationsException(max);
        }
    }
}
