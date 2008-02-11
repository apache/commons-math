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
 * This class implements the Nelder-Mead direct search method.
 *
 * @version $Revision$ $Date$
 * @see MultiDirectional
 * @since 1.2
 */
public class NelderMead
  extends DirectSearchOptimizer {

  /** Build a Nelder-Mead optimizer with default coefficients.
   * <p>The default coefficients are 1.0 for rho, 2.0 for khi and 0.5
   * for both gamma and sigma.</p>
   */
  public NelderMead() {
    super();
    this.rho   = 1.0;
    this.khi   = 2.0;
    this.gamma = 0.5;
    this.sigma = 0.5;
  }

  /** Build a Nelder-Mead optimizer with specified coefficients.
   * @param rho reflection coefficient
   * @param khi expansion coefficient
   * @param gamma contraction coefficient
   * @param sigma shrinkage coefficient
   */
  public NelderMead(double rho, double khi, double gamma, double sigma) {
    super();
    this.rho   = rho;
    this.khi   = khi;
    this.gamma = gamma;
    this.sigma = sigma;
  }

  /** Compute the next simplex of the algorithm.
   * @exception CostException if the function cannot be evaluated at
   * some point
   */
  protected void iterateSimplex()
    throws CostException {

    // the simplex has n+1 point if dimension is n
    int n = simplex.length - 1;

    // interesting costs
    double   smallest      = simplex[0].getCost();
    double   secondLargest = simplex[n-1].getCost();
    double   largest       = simplex[n].getCost();
    double[] xLargest      = simplex[n].getPoint();

    // compute the centroid of the best vertices
    // (dismissing the worst point at index n)
    double[] centroid = new double[n];
    for (int i = 0; i < n; ++i) {
      double[] x = simplex[i].getPoint();
      for (int j = 0; j < n; ++j) {
        centroid[j] += x[j];
      }
    }
    double scaling = 1.0 / n;
    for (int j = 0; j < n; ++j) {
      centroid[j] *= scaling;
    }

    // compute the reflection point
    double[] xR       = new double[n];
    for (int j = 0; j < n; ++j) {
      xR[j] = centroid[j] + rho * (centroid[j] - xLargest[j]);
    }
    double costR = evaluateCost(xR);

    if ((smallest <= costR) && (costR < secondLargest)) {

      // accept the reflected point
      replaceWorstPoint(new PointCostPair(xR, costR));

    } else if (costR < smallest) {

      // compute the expansion point
      double[] xE = new double[n];
      for (int j = 0; j < n; ++j) {
        xE[j] = centroid[j] + khi * (xR[j] - centroid[j]);
      }
      double costE = evaluateCost(xE);

      if (costE < costR) {
        // accept the expansion point
        replaceWorstPoint(new PointCostPair(xE, costE));
      } else {
        // accept the reflected point
        replaceWorstPoint(new PointCostPair(xR, costR));
      }

    } else {

      if (costR < largest) {

        // perform an outside contraction
        double[] xC = new double[n];
        for (int j = 0; j < n; ++j) {
          xC[j] = centroid[j] + gamma * (xR[j] - centroid[j]);
        }
        double costC = evaluateCost(xC);

        if (costC <= costR) {
          // accept the contraction point
          replaceWorstPoint(new PointCostPair(xC, costC));
          return;
        }

      } else {

        // perform an inside contraction
        double[] xC = new double[n];
        for (int j = 0; j < n; ++j) {
          xC[j] = centroid[j] - gamma * (centroid[j] - xLargest[j]);
        }
        double costC = evaluateCost(xC);

        if (costC < largest) {
          // accept the contraction point
          replaceWorstPoint(new PointCostPair(xC, costC));
          return;
        }

      }

      // perform a shrink
      double[] xSmallest = simplex[0].getPoint();
      for (int i = 1; i < simplex.length; ++i) {
        double[] x = simplex[i].getPoint();
        for (int j = 0; j < n; ++j) {
          x[j] = xSmallest[j] + sigma * (x[j] - xSmallest[j]);
        }
        simplex[i] = new PointCostPair(x, Double.NaN);
      }
      evaluateSimplex();

    }

  }

  /** Reflection coefficient. */
  private double rho;

  /** Expansion coefficient. */
  private double khi;

  /** Contraction coefficient. */
  private double gamma;

  /** Shrinkage coefficient. */
  private double sigma;

}
