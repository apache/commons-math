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

import org.apache.commons.math.optimization.ConvergenceChecker;
import org.apache.commons.math.optimization.CostException;
import org.apache.commons.math.optimization.CostFunction;
import org.apache.commons.math.optimization.NelderMead;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.optimization.PointCostPair;

import junit.framework.*;

public class NelderMeadTest
  extends TestCase {

  public NelderMeadTest(String name) {
    super(name);
  }

  public void testRosenbrock()
    throws CostException, ConvergenceException {

    CostFunction rosenbrock =
      new CostFunction() {
        public double cost(double[] x) {
          ++count;
          double a = x[1] - x[0] * x[0];
          double b = 1.0 - x[0];
          return 100 * a * a + b * b;
        }
      };

    count = 0;
    PointCostPair optimum =
      new NelderMead().minimizes(rosenbrock, 100, new ValueChecker(1.0e-3),
                                 new double[] { -1.2,  1.0 },
                                 new double[] {  3.5, -2.3 });

    assertTrue(count < 50);
    assertEquals(0.0, optimum.cost, 6.0e-4);
    assertEquals(1.0, optimum.point[0], 0.05);
    assertEquals(1.0, optimum.point[1], 0.05);

  }

  public void testPowell()
    throws CostException, ConvergenceException {

    CostFunction powell =
      new CostFunction() {
        public double cost(double[] x) {
          ++count;
          double a = x[0] + 10 * x[1];
          double b = x[2] - x[3];
          double c = x[1] - 2 * x[2];
          double d = x[0] - x[3];
          return a * a + 5 * b * b + c * c * c * c + 10 * d * d * d * d;
        }
      };

    count = 0;
    PointCostPair optimum =
      new NelderMead().minimizes(powell, 200, new ValueChecker(1.0e-3),
                                 new double[] {  3.0, -1.0, 0.0, 1.0 },
                                 new double[] {  4.0,  0.0, 1.0, 2.0 });
    assertTrue(count < 150);
    assertEquals(0.0, optimum.cost, 6.0e-4);
    assertEquals(0.0, optimum.point[0], 0.07);
    assertEquals(0.0, optimum.point[1], 0.07);
    assertEquals(0.0, optimum.point[2], 0.07);
    assertEquals(0.0, optimum.point[3], 0.07);

  }

  private static class ValueChecker implements ConvergenceChecker {

    public ValueChecker(double threshold) {
      this.threshold = threshold;
    }

    public boolean converged(PointCostPair[] simplex) {
      PointCostPair smallest = simplex[0];
      PointCostPair largest  = simplex[simplex.length - 1];
      return (largest.cost - smallest.cost) < threshold;
    }

    private double threshold;

  };

  public static Test suite() {
    return new TestSuite(NelderMeadTest.class);
  }

  private int count;

}
