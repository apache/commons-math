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

/** This interface specifies how to check if a {@link
 * DirectSearchOptimizer direct search method} has converged.
 *
 * <p>Deciding if convergence has been reached is a problem-dependent
 * issue. The user should provide a class implementing this interface
 * to allow the optimization algorithm to stop its search according to
 * the problem at hand.</p>
 *
 * @version $Revision$ $Date$
 * @since 1.2
 */

public interface ConvergenceChecker {

  /** Check if the optimization algorithm has converged on the simplex.
   * @param simplex ordered simplex (all points in the simplex have
   * been eavluated and are sorted from lowest to largest cost)
   * @return true if the algorithm is considered to have converged
   */
  public boolean converged (PointCostPair[] simplex);

}
