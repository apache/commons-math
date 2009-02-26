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

import java.io.Serializable;

/** 
 * This interface represents an optimization algorithm.
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface Optimizer extends Serializable {

    /** Set the maximal number of objective function calls.
     * @param maxEvaluations maximal number of function calls for each
     * start (note that the number may be checked <em>after</em>
     * a few related calls have been made, this means that in some
     * cases this number will be exceeded by a few units, depending on
     * the dimension of the problem and kind of optimizer).
     */
    void setMaxEvaluations(int maxEvaluations);

    /** Set the convergence checker.
     * @param checker object to use to check for convergence
     */
    void setConvergenceChecker(ConvergenceChecker checker);

    /** Optimizes an objective function.
     * @param f objective function
     * @param goalType type of optimization goal: either {@link GoalType#MAXIMIZE}
     * or {@link GoalType#MINIMIZE}
     * @return the point/value pair giving the optimal value for objective function
     * @exception ObjectiveException if the objective function throws one during
     * the search
     * @exception OptimizationException if the algorithm failed to converge
     */
    PointValuePair optimize(final ObjectiveFunction f, final GoalType goalType)
        throws ObjectiveException, OptimizationException;

}
