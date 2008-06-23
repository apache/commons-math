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

package org.spaceroots.mantissa.quadrature.scalar;

import org.spaceroots.mantissa.functions.scalar.ComputableFunction;
import org.spaceroots.mantissa.functions.FunctionException;

/** This class implements a Gauss-Legendre integrator.

 * <p>Gauss-Legendre integrators are efficient integrators that can
 * accurately integrate functions with few functions evaluations. A
 * Gauss-Legendre integrator using an n-points quadrature formula can
 * integrate exactly 2n-1 degree polynoms.</p>

 * <p>These integrators evaluate the function on n carefully chosen
 * points in each step interval. These points are not evenly
 * spaced. The function is <emph>never</emph> evaluated at the
 * boundary points, which means it can be undefined at these
 * points.</p>

 * @version $Id$
 * @author L. Maisonobe

 */

public class GaussLegendreIntegrator
  implements ComputableFunctionIntegrator {
  /** Build a Gauss-Legendre integrator.

   * <p>A Gauss-Legendre integrator is a formula like:
   * <pre>
   *    int (f) from -1 to +1 = Sum (ai * f(xi))
   * </pre>
   * </p>
   *
   * <p>The coefficients of the formula are computed as follow:
   * <pre>
   *   let n be the desired number of points
   *   the xi are the roots of the degree n Legendre polynomial
   *   the ai are the integrals int (Li^2) from -1 to +1
   *   where Li (x) = Prod (x-xk)/(xi-xk) for k != i
   * </pre>
   * </p>
   *
   * <p>A formula in n points can integrate exactly polynoms of degree
   * up to 2n-1.</p>
   *
   * @param minPoints minimal number of points desired
   * @param rawStep raw integration step (the precise step will be
   * adjusted in order to have an integer number of steps in the
   * integration range).
   * */
  public GaussLegendreIntegrator(int minPoints, double rawStep) {
    if (minPoints <= 2) {
      weightedRoots = new double[][] {
        { 1.0, -1.0 / Math.sqrt(3.0) },
        { 1.0,  1.0 / Math.sqrt(3.0) }
      };
    } else if (minPoints <= 3) {
      weightedRoots = new double[][] {
        { 5.0 / 9.0, -Math.sqrt(0.6) },
        { 8.0 / 9.0,            0.0  },
        { 5.0 / 9.0,  Math.sqrt(0.6) }
      };
    } else if (minPoints <= 4) {
      weightedRoots = new double[][] {
        { (90.0 - 5.0 * Math.sqrt(30.0)) / 180.0,
             -Math.sqrt((15.0 + 2.0 * Math.sqrt(30.0)) / 35.0) },
        { (90.0 + 5.0 * Math.sqrt(30.0)) / 180.0,
             -Math.sqrt((15.0 - 2.0 * Math.sqrt(30.0)) / 35.0) },
        { (90.0 + 5.0 * Math.sqrt(30.0)) / 180.0,
              Math.sqrt((15.0 - 2.0 * Math.sqrt(30.0)) / 35.0) },
        { (90.0 - 5.0 * Math.sqrt(30.0)) / 180.0,
              Math.sqrt((15.0 + 2.0 * Math.sqrt(30.0)) / 35.0) }
      };
    } else {
      weightedRoots = new double[][] {
        { (322.0 - 13.0 * Math.sqrt(70.0)) / 900.0,
             -Math.sqrt((35.0 + 2.0 * Math.sqrt(70.0)) / 63.0) },
        { (322.0 + 13.0 * Math.sqrt(70.0)) / 900.0,
             -Math.sqrt((35.0 - 2.0 * Math.sqrt(70.0)) / 63.0) },
        { 128.0 / 225.0,
              0.0 },
        { (322.0 + 13.0 * Math.sqrt(70.0)) / 900.0,
              Math.sqrt((35.0 - 2.0 * Math.sqrt(70.0)) / 63.0) },
        { (322.0 - 13.0 * Math.sqrt(70.0)) / 900.0,
              Math.sqrt((35.0 + 2.0 * Math.sqrt(70.0)) / 63.0) }
      };
    }

    this.rawStep = rawStep;

  }

  /** Get the number of functions evaluation per step.
   * @return number of function evaluation per step
   */
  public int getEvaluationsPerStep() {
    return weightedRoots.length;
  }

  public double integrate(ComputableFunction f, double a, double b)
    throws FunctionException {

    // swap the bounds if they are not in ascending order
    if (b < a) {
      double tmp = b;
      b          = a;
      a          = tmp;
    }

    // adjust the step according to the bounds
    long   n     = Math.round(0.5 + (b - a) / rawStep);
    double step  = (b - a) / n;

    // integrate over all elementary steps
    double halfStep = step / 2.0;
    double midPoint = a + halfStep;
    double sum = 0.0;
    for (long i = 0; i < n; ++i) {
      for (int j = 0; j < weightedRoots.length; ++j) {
        sum += weightedRoots[j][0]
          * f.valueAt(midPoint + halfStep * weightedRoots[j][1]);
      }
      midPoint += step;
    }

    return halfStep * sum;

  }

  double[][] weightedRoots;

  double rawStep;

}
