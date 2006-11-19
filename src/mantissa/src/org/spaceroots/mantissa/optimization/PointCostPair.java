// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.optimization;

/** This class holds a point and its associated cost.
 * <p>A cost/point pair is not evaluated at build time. Its associated
 * cost set to <code>Double.NaN</code> until it is evaluated.</p>
 * @author Luc Maisonobe
 * @version $Id: PointCostPair.java 1705 2006-09-17 19:57:39Z luc $
 * @see CostFunction
 */
public class PointCostPair {

  /** Build a point/cost pair with non-evaluated cost.
   * @param point point coordinates
   */
  public PointCostPair(double[] point) {
    this.point = point;
    cost = Double.NaN;
  }

  /** Reset the point coordinates.
   * <p>Resetting the points coordinates automatically reset the cost
   * to non-evaluated</p>
   * @param point new point coordinates
   * @return old point coordinates (this can be re-used to put the
   * coordinates of another point without re-allocating an array)
   */
  public double[] setPoint(double[] point) {
    double[] oldPoint = this.point;
    this.point = point;
    cost = Double.NaN;
    return oldPoint;
  }

  /** Get the point coordinates.
   * @return point coordinates
   */
  public double[] getPoint() {
    return point;
  }

  /** Set the cost.
   * @param cost cost to store in the instance (can be
   * <code>Double.NaN</code> to reset the instance to non-evaluated)
   */
  public void setCost(double cost) {
    this.cost = cost;
  }

  /** Get the cost.
   * @return cost associated to the point (or <code>Double.NaN</code>
   * if the instance is not evaluated)
   */
  public double getCost() {
    return cost;
  }

  /** Check if the cost has been evaluated.
   * <p>The cost is considered to be non-evaluated if it is
   * <code>Double.isNaN(pair.getCost())</code> would return true</p>
   * @return true if the cost has been evaluated
   */
  public boolean isEvaluated() {
    return ! Double.isNaN(cost);
  }

  /** Point coordinates. */
  private double[] point;

  /** Cost associated to the point. */
  private double cost;

}
