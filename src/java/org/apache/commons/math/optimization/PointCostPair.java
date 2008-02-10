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

/** 
 * This class holds a point and its associated cost.
 * <p>This is a simple immutable container.</p>
 * @version $Revision$ $Date$
 * @see CostFunction
 * @since 1.2
 */
public class PointCostPair {

  /** Build a point/cost pair.
   * @param point point coordinates (the built instance will store
   * a copy of the array, not the array passed as argument)
   * @param cost point cost
   */
  public PointCostPair(double[] point, double cost) {
    this.point = (double[]) point.clone();
    this.cost = cost;
  }

  /** Get the point.
   * @return a copy of the stored point
   */
  public double[] getPoint() {
      return (double[]) point.clone();
  }

  /** Get the cost.
   * @return the stored cost
   */
  public double getCost() {
      return cost;
  }

  /** Point coordinates. */
  private final double[] point;

  /** Cost associated to the point. */
  private final double cost;

}
