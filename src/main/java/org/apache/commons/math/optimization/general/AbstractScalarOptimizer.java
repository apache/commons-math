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

package org.apache.commons.math.optimization.general;

import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.optimization.MultivariateRealOptimizer;
import org.apache.commons.math.optimization.ConvergenceChecker;
import org.apache.commons.math.optimization.RealPointValuePair;

/**
 * Base class for implementing optimizers for multivariate (not necessarily
 * differentiable) scalar functions.
 *
 * @version $Revision$ $Date$
 * @since 2.2
 */
public abstract class AbstractScalarOptimizer
    extends BaseAbstractScalarOptimizer<MultivariateRealFunction>
    implements MultivariateRealOptimizer {
    /**
     * Simple constructor with default settings.
     * The convergence check is set to a
     * {@link org.apache.commons.math.optimization.SimpleScalarValueChecker}.
     */
    protected AbstractScalarOptimizer() {}
    /**
     * @param checker Convergence checker.
     * @param maxEvaluations Maximum number of function evaluations.
     */
    protected AbstractScalarOptimizer(ConvergenceChecker<RealPointValuePair> checker,
                                      int maxEvaluations) {
        super(checker, maxEvaluations);
    }
}
