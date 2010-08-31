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

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.optimization.BaseOptimizer;
import org.apache.commons.math.optimization.GoalType;

/**
 * This interface is mainly intended to enforce the internal coherence of
 * Commons-Math. Users of the API are advised to base their code on
 * the following interfaces:
 * <ul>
 *  <li>{@link org.apache.commons.math.optimization.univariate.UnivariateRealOptimizer}</li>
 * </ul>
 *
 * @param <FUNC> Type of the objective function to be optimized.
 *
 * @version $Revision$ $Date$
 * @since 3.0
 */
public interface BaseUnivariateRealOptimizer<FUNC extends UnivariateRealFunction>
    extends BaseOptimizer<UnivariateRealPointValuePair> {
    /**
     * Find an optimum in the given interval.
     *
     * An optimizer may require that the interval brackets a single optimum.
     *
     * @param f Function to optimize.
     * @param goalType Type of optimization goal: either
     * {@link GoalType#MAXIMIZE} or {@link GoalType#MINIMIZE}.
     * @param min Lower bound for the interval.
     * @param max Upper bound for the interval.
     * @return a (point, value) pair where the function is optimum.
     * @throws {@link org.apache.commons.math.exception.TooManyEvaluationsException}
     * if the maximum evaluation count is exceeded.
     * @throws {@link org.apache.commons.math.exception.ConvergenceException}
     * if the optimizer detects a convergence problem.
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function.
     * @throws IllegalArgumentException if {@code min > max} or the endpoints
     * do not satisfy the requirements specified by the optimizer.
     */
    UnivariateRealPointValuePair optimize(FUNC f, GoalType goalType,
                                          double min, double max)
        throws FunctionEvaluationException;

    /**
     * Find an optimum in the given interval, start at startValue.
     * An optimizer may require that the interval brackets a single optimum.
     *
     * @param f Function to optimize.
     * @param goalType Type of optimization goal: either
     * {@link GoalType#MAXIMIZE} or {@link GoalType#MINIMIZE}.
     * @param min Lower bound for the interval.
     * @param max Upper bound for the interval.
     * @param startValue Start value to use.
     * @return a (point, value) pair where the function is optimum.
     * @throws {@link org.apache.commons.math.exception.TooManyEvaluationsException}
     * if the maximum evaluation count is exceeded.
     * @throws {@link org.apache.commons.math.exception.ConvergenceException}
     * if the optimizer detects a convergence problem.
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function.
     * @throws IllegalArgumentException if {@code min > max} or the endpoints
     * do not satisfy the requirements specified by the optimizer.
     * @throws NullArgumentException if any argument is {@code null}.
     */
    UnivariateRealPointValuePair optimize(FUNC f, GoalType goalType,
                                          double min, double max,
                                          double startValue)
        throws FunctionEvaluationException;
}
