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
 * This interface represents an optimization algorithm for {@link ScalarObjectiveFunction
 * scalar objective functions}.
 * @see ScalarDifferentiableOptimizer
 * @see VectorialDifferentiableOptimizer
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface ScalarOptimizer extends Serializable {

    /** Set the maximal number of objective function calls.
     * <p>
     * The number of objective function calls may be checked <em>after</em> a few
     * related calls have been made. This implies that in some cases this number may
     * be exceeded by a few units, depending on the dimension of the problem and kind
     * of optimizer.
     * </p>
     * @param maxEvaluations maximal number of function calls
     * .
     */
    void setMaxEvaluations(int maxEvaluations);

    /** Get the maximal number of objective function calls.
     * <p>
     * The number of objective function calls may be checked <em>after</em> a few
     * related calls have been made. This implies that in some cases this number may
     * be exceeded by a few units, depending on the dimension of the problem and kind
     * of optimizer.
     * </p>
      * @return maximal number of function calls
     */
    int getMaxEvaluations();

    /** Get the number of evaluations of the objective function.
     * <p>
     * The number of evaluation correspond to the last call to the
     * {@link #optimize(ScalarObjectiveFunction, GoalType, double[]) optimize}
     * method. It is 0 if the method has not been called yet.
     * </p>
     * @return number of evaluations of the objective function
     */
   int getEvaluations();

    /** Set the convergence checker.
     * @param checker object to use to check for convergence
     */
    void setConvergenceChecker(ScalarConvergenceChecker checker);

    /** Get the convergence checker.
     * @return object used to check for convergence
     */
    ScalarConvergenceChecker getConvergenceChecker();

    /** Optimizes an objective function.
     * @param f objective function
     * @param goalType type of optimization goal: either {@link GoalType#MAXIMIZE}
     * or {@link GoalType#MINIMIZE}
     * @param startPoint the start point for optimization
     * @return the point/value pair giving the optimal value for objective function
     * @exception ObjectiveException if the objective function throws one during
     * the search
     * @exception OptimizationException if the algorithm failed to converge
     * @exception IllegalArgumentException if the start point dimension is wrong
     */
    ScalarPointValuePair optimize(ScalarObjectiveFunction f,
                                  GoalType goalType,
                                  double[] startPoint)
        throws ObjectiveException, OptimizationException, IllegalArgumentException;

}
