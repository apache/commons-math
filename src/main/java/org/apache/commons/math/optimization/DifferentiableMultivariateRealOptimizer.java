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
import org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction;

/**
 * This interface represents an optimization algorithm for {@link DifferentiableMultivariateRealFunction
 * scalar differentiable objective functions}.
 * <p>Optimization algorithms find the input point set that either {@link GoalType
 * maximize or minimize} an objective function.</p>
 * @see MultivariateRealOptimizer
 * @see DifferentiableMultivariateVectorialOptimizer
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface DifferentiableMultivariateRealOptimizer
    extends BaseMultivariateRealOptimizer<DifferentiableMultivariateRealFunction> {
    /**
     * Get the number of evaluations of the objective function gradient.
     * The number of evaluations corresponds to the last call to the
     * {@link #optimize(DifferentiableMultivariateRealFunction, GoalType, double[]) optimize}
     * method. It is 0 if the method has not been called yet.
     *
     * @return the number of evaluations of the objective function gradient.
     */
    int getGradientEvaluations();
}
