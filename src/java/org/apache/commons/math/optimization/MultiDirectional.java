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
 * This class implements the multi-directional direct search method.
 *
 * @version $Revision$ $Date$
 * @see NelderMead
 * @since 1.2
 */
public class MultiDirectional
  extends DirectSearchOptimizer {

  /** Build a multi-directional optimizer with default coefficients.
   * <p>The default values are 2.0 for khi and 0.5 for gamma.</p>
   */
  public MultiDirectional() {
    super();
    this.khi   = 2.0;
    this.gamma = 0.5;
  }

  /** Build a multi-directional optimizer with specified coefficients.
   * @param khi expansion coefficient
   * @param gamma contraction coefficient
   */
  public MultiDirectional(double khi, double gamma) {
    super();
    this.khi   = khi;
    this.gamma = gamma;
  }

  /** Compute the next simplex of the algorithm.
   * @exception CostException if the function cannot be evaluated at
   * some point
   */
  protected void iterateSimplex()
    throws CostException {

    while (true) {

      // save the original vertex
      PointCostPair[] original = simplex;
      double originalCost = original[0].getCost();

      // perform a reflection step
      double reflectedCost = evaluateNewSimplex(original, 1.0);
      if (reflectedCost < originalCost) {

        // compute the expanded simplex
        PointCostPair[] reflected = simplex;
        double expandedCost = evaluateNewSimplex(original, khi);
        if (reflectedCost <= expandedCost) {
          // accept the reflected simplex
          simplex = reflected;
        }

        return;

      }

      // compute the contracted simplex
      double contractedCost = evaluateNewSimplex(original, gamma);
      if (contractedCost < originalCost) {
        // accept the contracted simplex
        return;
      }

    }

  }

  /** Compute and evaluate a new simplex.
   * @param original original simplex (to be preserved)
   * @param coeff linear coefficient
   * @return smallest cost in the transformed simplex
   * @exception CostException if the function cannot be evaluated at
   * some point
   */
  private double evaluateNewSimplex(PointCostPair[] original, double coeff)
    throws CostException {

    double[] xSmallest = original[0].getPoint();
    int n = xSmallest.length;

    // create the linearly transformed simplex
    simplex = new PointCostPair[n + 1];
    simplex[0] = original[0];
    for (int i = 1; i <= n; ++i) {
      double[] xOriginal    = original[i].getPoint();
      double[] xTransformed = new double[n];
      for (int j = 0; j < n; ++j) {
        xTransformed[j] = xSmallest[j] + coeff * (xSmallest[j] - xOriginal[j]);
      }
      simplex[i] = new PointCostPair(xTransformed, Double.NaN);
    }

    // evaluate it
    evaluateSimplex();
    return simplex[0].getCost();

  }

  /** Expansion coefficient. */
  private double khi;

  /** Contraction coefficient. */
  private double gamma;

}
