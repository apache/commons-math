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
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.NotPositiveDefiniteMatrixException;
import org.apache.commons.math.random.RandomGenerator;
import org.apache.commons.math.random.RandomVectorGenerator;
import org.apache.commons.math.random.UncorrelatedRandomVectorGenerator;
import org.apache.commons.math.random.UniformRandomGenerator;

import junit.framework.*;

public class NelderMeadTest
  extends TestCase {

  public NelderMeadTest(String name) {
    super(name);
  }

  public void testCostExceptions() throws ConvergenceException {
      CostFunction wrong =
          new CostFunction() {
            public double cost(double[] x) throws CostException {
                if (x[0] < 0) {
                    throw new CostException("{0}", new Object[] { "oops"});
                } else if (x[0] > 1) {
                    throw new CostException(new RuntimeException("oops"));
                } else {
                    return x[0] * (1 - x[0]);
                }
            }
      };
      try {
          new NelderMead(0.9, 1.9, 0.4, 0.6).minimize(wrong, 10, new ValueChecker(1.0e-3),
                                                      new double[] { -0.5 }, new double[] { 0.5 });
          fail("an exception should have been thrown");
      } catch (CostException ce) {
          // expected behavior
          assertNull(ce.getCause());
      } catch (Exception e) {
          fail("wrong exception caught: " + e.getMessage());
      } 
      try {
          new NelderMead(0.9, 1.9, 0.4, 0.6).minimize(wrong, 10, new ValueChecker(1.0e-3),
                                                      new double[] { 0.5 }, new double[] { 1.5 });
          fail("an exception should have been thrown");
      } catch (CostException ce) {
          // expected behavior
          assertNotNull(ce.getCause());
      } catch (Exception e) {
          fail("wrong exception caught: " + e.getMessage());
      } 
  }

  public void testRosenbrock()
    throws CostException, ConvergenceException, NotPositiveDefiniteMatrixException {

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
    NelderMead nm = new NelderMead();
    try {
      nm.minimize(rosenbrock, 100, new ValueChecker(1.0e-3),
                  new double[][] {
                    { -1.2, 1.0 }, { 3.5, -2.3 }, { 0.4, 1.5 }
                  }, 1, 5384353l);
      fail("an exception should have been thrown");
    } catch (ConvergenceException ce) {
        // expected behavior
    } catch (Exception e) {
        e.printStackTrace(System.err);
        fail("wrong exception caught: " + e.getMessage());
    }

    count = 0;
    PointCostPair optimum =
        nm.minimize(rosenbrock, 100, new ValueChecker(1.0e-3),
                    new double[][] {
                      { -1.2, 1.0 }, { 0.9, 1.2 }, { 3.5, -2.3 }
                    }, 10, 1642738l);

    assertTrue(count > 700);
    assertTrue(count < 800);
    assertEquals(0.0, optimum.getCost(), 5.0e-5);
    assertEquals(1.0, optimum.getPoint()[0], 0.01);
    assertEquals(1.0, optimum.getPoint()[1], 0.01);

    PointCostPair[] minima = nm.getMinima();
    assertEquals(10, minima.length);
    assertNotNull(minima[0]);
    assertNull(minima[minima.length - 1]);
    for (int i = 0; i < minima.length; ++i) {
        if (minima[i] == null) {
            if ((i + 1) < minima.length) {
                assertTrue(minima[i+1] == null);
            }
        } else {
            if (i > 0) {
                assertTrue(minima[i-1].getCost() <= minima[i].getCost());
            }
        }
    }

    RandomGenerator rg = new JDKRandomGenerator();
    rg.setSeed(64453353l);
    RandomVectorGenerator rvg =
        new UncorrelatedRandomVectorGenerator(new double[] { 0.9, 1.1 },
                                              new double[] { 0.2, 0.2 },
                                              new UniformRandomGenerator(rg));
    optimum =
        nm.minimize(rosenbrock, 100, new ValueChecker(1.0e-3), rvg);
    assertEquals(0.0, optimum.getCost(), 2.0e-4);
    optimum =
        nm.minimize(rosenbrock, 100, new ValueChecker(1.0e-3), rvg, 3);
    assertEquals(0.0, optimum.getCost(), 3.0e-5);

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
    NelderMead nm = new NelderMead();
    PointCostPair optimum =
      nm.minimize(powell, 200, new ValueChecker(1.0e-3),
                  new double[] {  3.0, -1.0, 0.0, 1.0 },
                  new double[] {  4.0,  0.0, 1.0, 2.0 },
                  1, 1642738l);
    assertTrue(count < 150);
    assertEquals(0.0, optimum.getCost(), 6.0e-4);
    assertEquals(0.0, optimum.getPoint()[0], 0.07);
    assertEquals(0.0, optimum.getPoint()[1], 0.07);
    assertEquals(0.0, optimum.getPoint()[2], 0.07);
    assertEquals(0.0, optimum.getPoint()[3], 0.07);

  }

  private static class ValueChecker implements ConvergenceChecker {

    public ValueChecker(double threshold) {
      this.threshold = threshold;
    }

    public boolean converged(PointCostPair[] simplex) {
      PointCostPair smallest = simplex[0];
      PointCostPair largest  = simplex[simplex.length - 1];
      return (largest.getCost() - smallest.getCost()) < threshold;
    }

    private double threshold;

  };

  public static Test suite() {
    return new TestSuite(NelderMeadTest.class);
  }

  private int count;

}
