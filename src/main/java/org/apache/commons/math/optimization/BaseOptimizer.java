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
 * This interface is mainly intended to enforce the internal coherence of
 * Commons-Math. Users of the API are advised to base their code on
 * the following interfaces:
 * <ul>
 *  <li>{@link org.apache.commons.math.optimization.MultivariateRealOptimizer}</li>
 *  <li>{@link org.apache.commons.math.optimization.DifferentiableMultivariateRealOptimizer}</li>
 *  <li>{@link org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer}</li>
 *  <li>{@link org.apache.commons.math.optimization.univariate.UnivariateRealOptimizer}</li>
 * </ul>
 *
 * @param <PAIR> Type of the point/objective pair.
 *
 * @version $Revision$ $Date$
 * @since 3.0
 */
public interface BaseOptimizer<PAIR> {
    /**
     * Set the maximal number of function evaluations.
     *
     * @param maxEvaluations Maximal number of function evaluations.
     */
    void setMaxEvaluations(int maxEvaluations);

    /**
     * Get the maximal number of function evaluations.
     *
     * @return the maximal number of function evaluations.
     */
    int getMaxEvaluations();

    /**
     * Get the number of evaluations of the objective function.
     * The number of evaluations corresponds to the last call to the
     * {@code optimize} method. It is 0 if the method has not been
     * called yet.
     *
     * @return the number of evaluations of the objective function.
     */
    int getEvaluations();

    /**
     * Set the convergence checker.
     *
     * @param checker Object to use to check for convergence.
     */
    void setConvergenceChecker(ConvergenceChecker<PAIR> checker);

    /**
     * Get the convergence checker.
     *
     * @return the object used to check for convergence.
     */
    ConvergenceChecker<PAIR> getConvergenceChecker();
}
