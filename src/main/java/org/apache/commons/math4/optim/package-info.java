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

/**
 * <p>
 *  Generally, optimizers are algorithms that will either
 *  {@link org.apache.commons.math4.optim.nonlinear.scalar.GoalType#MINIMIZE minimize} or
 *  {@link org.apache.commons.math4.optim.nonlinear.scalar.GoalType#MAXIMIZE maximize}
 *  a scalar function, called the
 *  {@link org.apache.commons.math4.optim.nonlinear.scalar.ObjectiveFunction <em>objective
 *  function</em>}.
 *  <br/>
 *  For some scalar objective functions the gradient can be computed (analytically
 *  or numerically). Algorithms that use this knowledge are defined in the
 *  {@link org.apache.commons.math4.optim.nonlinear.scalar.gradient} package.
 *  The algorithms that do not need this additional information are located in
 *  the {@link org.apache.commons.math4.optim.nonlinear.scalar.noderiv} package.
 * </p>
 *
 * <p>
 *  Some problems are solved more efficiently by algorithms that, instead of an
 *  objective function, need access to all the observations.
 *  Such methods are implemented in the {@link org.apache.commons.math4.fitting}
 *  package.
 * </p>
 *
 * <p>
 *  This package provides common functionality for the optimization algorithms.
 *  Abstract classes ({@link org.apache.commons.math4.optim.BaseOptimizer} and
 *  {@link org.apache.commons.math4.optim.BaseMultivariateOptimizer}) contain
 *  boiler-plate code for storing {@link org.apache.commons.math4.optim.MaxEval
 *  evaluations} and {@link org.apache.commons.math4.optim.MaxIter iterations}
 *  counters and a user-defined
 *  {@link org.apache.commons.math4.optim.ConvergenceChecker convergence checker}.
 * </p>
 *
 * <p>
 *  For each of the optimizer types, there is a special implementation that
 *  wraps an optimizer instance and provides a "multi-start" feature: it calls
 *  the underlying optimizer several times with different starting points and
 *  returns the best optimum found, or all optima if so desired.
 *  This could be useful to avoid being trapped in a local extremum.
 * </p>
 */
package org.apache.commons.math4.optim;
