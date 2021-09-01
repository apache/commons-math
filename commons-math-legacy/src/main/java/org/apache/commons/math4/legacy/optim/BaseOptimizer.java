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
package org.apache.commons.math4.legacy.optim;

import org.apache.commons.math4.legacy.exception.TooManyEvaluationsException;
import org.apache.commons.math4.legacy.exception.TooManyIterationsException;
import org.apache.commons.math4.legacy.core.IntegerSequence;

/**
 * Base class for implementing optimizers.
 * It contains the boiler-plate code for counting the number of evaluations
 * of the objective function and the number of iterations of the algorithm,
 * and storing the convergence checker.
 * <em>It is not a "user" class.</em>
 *
 * @param <PAIR> Type of the point/value pair returned by the optimization
 * algorithm.
 *
 * @since 3.1
 */
public abstract class BaseOptimizer<PAIR> {
    /** Callback to use for the evaluation counter. */
    private static final MaxEvalCallback MAX_EVAL_CALLBACK = new MaxEvalCallback();
    /** Callback to use for the iteration counter. */
    private static final MaxIterCallback MAX_ITER_CALLBACK = new MaxIterCallback();

    /** Convergence checker. */
    private final ConvergenceChecker<PAIR> checker;
    /** Maximum number of evaluations. */
    private int maxEvaluations;
    /** Maximum number of iterations. */
    private int maxIterations;
    /** Evaluations counter. */
    private IntegerSequence.Incrementor evaluations;
    /** Iterations counter. */
    private IntegerSequence.Incrementor iterations;

    /**
     * @param checker Convergence checker.
     */
    protected BaseOptimizer(ConvergenceChecker<PAIR> checker) {
        this(checker, 0, Integer.MAX_VALUE);
    }

    /**
     * @param checker Convergence checker.
     * @param maxEval Maximum number of objective function evaluations.
     * @param maxIter Maximum number of algorithm iterations.
     */
    protected BaseOptimizer(ConvergenceChecker<PAIR> checker,
                            int maxEval,
                            int maxIter) {
        this.checker = checker;
        this.maxEvaluations = maxEval;
        this.maxIterations = maxIter;
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
     * Stores data and performs the optimization.
     * <p>
     * The list of parameters is open-ended so that sub-classes can extend it
     * with arguments specific to their concrete implementations.
     * <p>
     * When the method is called multiple times, instance data is overwritten
     * only when actually present in the list of arguments: when not specified,
     * data set in a previous call is retained (and thus is optional in
     * subsequent calls).
     * <p>
     * Important note: Subclasses <em>must</em> override
     * {@link #parseOptimizationData(OptimizationData[])} if they need to register
     * their own options; but then, they <em>must</em> also call
     * {@code super.parseOptimizationData(optData)} within that method.
     *
     * @param optData Optimization data.
     * This method will register the following data:
     * <ul>
     *  <li>{@link MaxEval}</li>
     *  <li>{@link MaxIter}</li>
     * </ul>
     * @return a point/value pair that satisfies the convergence criteria.
     * @throws TooManyEvaluationsException if the maximal number of
     * evaluations is exceeded.
     * @throws TooManyIterationsException if the maximal number of
     * iterations is exceeded.
     */
    public PAIR optimize(OptimizationData... optData) {
        // Parse options.
        parseOptimizationData(optData);
        // Reset counters.
        resetCounters();
        // Perform optimization.
        return doOptimize();
    }

    /**
     * Performs the optimization.
     *
     * @return a point/value pair that satisfies the convergence criteria.
     * @throws TooManyEvaluationsException if the maximal number of
     * evaluations is exceeded.
     * @throws TooManyIterationsException if the maximal number of
     * iterations is exceeded.
     */
    public PAIR optimize() {
        // Reset counters.
        resetCounters();
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
    protected void incrementEvaluationCount() {
        evaluations.increment();
    }

    /**
     * Increment the iteration count.
     *
     * @throws TooManyIterationsException if the allowed iterations
     * have been exhausted.
     */
    protected void incrementIterationCount() {
        iterations.increment();
    }

    /**
     * Scans the list of (required and optional) optimization data that
     * characterize the problem.
     *
     * @param optData Optimization data.
     * The following data will be looked for:
     * <ul>
     *  <li>{@link MaxEval}</li>
     *  <li>{@link MaxIter}</li>
     * </ul>
     */
    protected void parseOptimizationData(OptimizationData... optData) {
        // The existing values (as set by the previous call) are reused if
        // not provided in the argument list.
        for (OptimizationData data : optData) {
            if (data instanceof MaxEval) {
                maxEvaluations = ((MaxEval) data).getMaxEval();
                continue;
            }
            if (data instanceof MaxIter) {
                maxIterations = ((MaxIter) data).getMaxIter();
                continue;
            }
        }
    }

    /** Reset counters. */
    private void resetCounters() {
        evaluations = IntegerSequence.Incrementor.create()
            .withMaximalCount(maxEvaluations)
            .withCallback(MAX_EVAL_CALLBACK);
        iterations = IntegerSequence.Incrementor.create()
            .withMaximalCount(maxIterations)
            .withCallback(MAX_ITER_CALLBACK);
    }

    /**
     * Defines the action to perform when reaching the maximum number
     * of evaluations.
     */
    private static class MaxEvalCallback
        implements IntegerSequence.Incrementor.MaxCountExceededCallback {
        /**
         * {@inheritDoc}
         * @throws TooManyEvaluationsException
         */
        @Override
        public void trigger(int max) {
            throw new TooManyEvaluationsException(max);
        }
    }

    /**
     * Defines the action to perform when reaching the maximum number
     * of evaluations.
     */
    private static class MaxIterCallback
        implements IntegerSequence.Incrementor.MaxCountExceededCallback {
        /**
         * {@inheritDoc}
         * @throws TooManyIterationsException
         */
        @Override
        public void trigger(int max) {
            throw new TooManyIterationsException(max);
        }
    }
}
