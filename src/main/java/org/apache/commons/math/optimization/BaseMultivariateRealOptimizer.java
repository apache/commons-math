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

package org.apache.commons.math.optimization;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;

/**
 * Optimization algorithms find the input point set that either {@link GoalType
 * maximize or minimize} an objective function.
 * This interface is mainly intended to enforce the internal coherence of
 * Commons-Math. Users of the API are advised to base their code on
 * {@link MultivariateRealOptimizer} or on
 * {@link DifferentiableMultivariateRealOptimizer}.
 *
 * @see MultivariateRealOptimizer
 * @see DifferentiableMultivariateRealOptimizer
 * @version $Revision$ $Date$
 * @since 2.2
 */
public interface BaseMultivariateRealOptimizer<T extends MultivariateRealFunction> {
    /**
     * Set the maximal number of iterations of the algorithm.
     *
     * @param maxIterations Maximal number of algorithm iterations.
     */
    void setMaxIterations(int maxIterations);

    /**
     * Get the maximal number of iterations of the algorithm.
     *
     * @return the maximal number of iterations.
     */
    int getMaxIterations();

    /**
     * Set the maximal number of functions evaluations.
     *
     * @param maxEvaluations Maximal number of function evaluations.
     */
    void setMaxEvaluations(int maxEvaluations);

    /**
     * Get the maximal number of functions evaluations.
     *
     * @return the maximal number of functions evaluations.
     */
    int getMaxEvaluations();

    /**
     * Get the number of iterations realized by the algorithm.
     * The number of evaluations corresponds to the last call to the
     * {@link #optimize(MultivariateRealFunction, GoalType, double[]) optimize}
     * method. It is 0 if the method has not been called yet.
     *
     * @return the number of iterations.
     */
    int getIterations();

    /**
     * Get the number of evaluations of the objective function.
     *
     * The number of evaluations corresponds to the last call to the
     * {@link #optimize(T, GoalType, double[]) optimize}
     * method. It is 0 if the method has not been called yet.
     *
     * @return the number of evaluations of the objective function.
     */
    int getEvaluations();

    /**
     * Set the convergence checker.
     *
     * @param checker Object to use to check for convergence.
     */
    void setConvergenceChecker(RealConvergenceChecker checker);

    /**
     * Get the convergence checker.
     *
     * @return the object used to check for convergence.
     */
    RealConvergenceChecker getConvergenceChecker();

    /**
     * Optimize an objective function.
     *     
     * @param f Objective function.
     * @param goalType Type of optimization goal: either {@link GoalType#MAXIMIZE}
     * or {@link GoalType#MINIMIZE}.
     * @param startPoint Start point for optimization.
     * @return the point/value pair giving the optimal value for objective function.
     * @throws FunctionEvaluationException if the objective function throws one during
     * the search.
     * @throws OptimizationException if the algorithm failed to converge.
     * @throws IllegalArgumentException if the start point dimension is wrong.
     */
    RealPointValuePair optimize(T f, GoalType goalType, double[] startPoint)
        throws FunctionEvaluationException, OptimizationException;
}
