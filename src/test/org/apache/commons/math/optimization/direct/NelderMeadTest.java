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

package org.apache.commons.math.optimization.direct;

import org.apache.commons.math.linear.decomposition.NotPositiveDefiniteMatrixException;
import org.apache.commons.math.optimization.ConvergenceChecker;
import org.apache.commons.math.optimization.ObjectiveException;
import org.apache.commons.math.optimization.ObjectiveFunction;
import org.apache.commons.math.optimization.PointValuePair;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.random.JDKRandomGenerator;
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

  public void testObjectiveExceptions() throws ConvergenceException {
      ObjectiveFunction wrong =
          new ObjectiveFunction() {
            private static final long serialVersionUID = 2624035220997628868L;
            public double objective(double[] x) throws ObjectiveException {
                if (x[0] < 0) {
                    throw new ObjectiveException("{0}", "oops");
                } else if (x[0] > 1) {
                    throw new ObjectiveException(new RuntimeException("oops"));
                } else {
                    return x[0] * (1 - x[0]);
                }
            }
      };
      try {
          new NelderMead(0.9, 1.9, 0.4, 0.6).optimize(wrong, 10, new ValueChecker(1.0e-3), true,
                                                      new double[] { -0.5 }, new double[] { 0.5 });
          fail("an exception should have been thrown");
      } catch (ObjectiveException ce) {
          // expected behavior
          assertNull(ce.getCause());
      } catch (Exception e) {
          fail("wrong exception caught: " + e.getMessage());
      } 
      try {
          new NelderMead(0.9, 1.9, 0.4, 0.6).optimize(wrong, 10, new ValueChecker(1.0e-3), true,
                                                      new double[] { 0.5 }, new double[] { 1.5 });
          fail("an exception should have been thrown");
      } catch (ObjectiveException ce) {
          // expected behavior
          assertNotNull(ce.getCause());
      } catch (Exception e) {
          fail("wrong exception caught: " + e.getMessage());
      } 
  }

  public void testMinimizeMaximize()
      throws ObjectiveException, ConvergenceException, NotPositiveDefiniteMatrixException {

      // the following function has 4 local extrema:
      final double xM        = -3.841947088256863675365;
      final double yM        = -1.391745200270734924416;
      final double xP        =  0.2286682237349059125691;
      final double yP        = -yM;
      final double valueXmYm =  0.2373295333134216789769; // local  maximum
      final double valueXmYp = -valueXmYm;                // local  minimum
      final double valueXpYm = -0.7290400707055187115322; // global minimum
      final double valueXpYp = -valueXpYm;                // global maximum
      ObjectiveFunction fourExtrema = new ObjectiveFunction() {
        private static final long serialVersionUID = -7039124064449091152L;
        public double objective(double[] variables) {
              final double x = variables[0];
              final double y = variables[1];
              return Math.atan(x) * Math.atan(x + 2) * Math.atan(y) * Math.atan(y) / (x * y);
          }
      };

      NelderMead nm = new NelderMead();

      // minimization
      nm.optimize(fourExtrema, 100, new ValueChecker(1.0e-8), true,
                  new double[] { -5, -5 }, new double[] { 5, 5 }, 10, 38821113105892l);
      PointValuePair[] optima = nm.getOptima();
      assertEquals(10, optima.length);
      int localCount  = 0;
      int globalCount = 0;
      for (PointValuePair optimum : optima) {
          if (optimum != null) {
              if (optimum.getPoint()[0] < 0) {
                  // this should be the local minimum
                  ++localCount;
                  assertEquals(xM,        optimum.getPoint()[0], 1.0e-3);
                  assertEquals(yP,        optimum.getPoint()[1], 1.0e-3);
                  assertEquals(valueXmYp, optimum.getValue(),     2.0e-8);
              } else {
                  // this should be the global minimum
                  ++globalCount;
                  assertEquals(xP,        optimum.getPoint()[0], 1.0e-3);
                  assertEquals(yM,        optimum.getPoint()[1], 1.0e-3);
                  assertEquals(valueXpYm, optimum.getValue(),     2.0e-8);              
              }
          }
      }
      assertTrue(localCount  > 0);
      assertTrue(globalCount > 0);
      assertTrue(nm.getTotalEvaluations() > 600);
      assertTrue(nm.getTotalEvaluations() < 800);

      // minimization
      nm.optimize(fourExtrema, 100, new ValueChecker(1.0e-8), false,
                  new double[] { -5, -5 }, new double[] { 5, 5 }, 10, 38821113105892l);
      optima = nm.getOptima();
      assertEquals(10, optima.length);
      localCount  = 0;
      globalCount = 0;
      for (PointValuePair optimum : optima) {
          if (optimum != null) {
              if (optimum.getPoint()[0] < 0) {
                  // this should be the local maximum
                  ++localCount;
                  assertEquals(xM,        optimum.getPoint()[0], 1.0e-3);
                  assertEquals(yM,        optimum.getPoint()[1], 1.0e-3);
                  assertEquals(valueXmYm, optimum.getValue(),     2.0e-8);
              } else {
                  // this should be the global maximum
                  ++globalCount;
                  assertEquals(xP,        optimum.getPoint()[0], 1.0e-3);
                  assertEquals(yP,        optimum.getPoint()[1], 1.0e-3);
                  assertEquals(valueXpYp, optimum.getValue(),     2.0e-8);              
              }
          }
      }
      assertTrue(localCount  > 0);
      assertTrue(globalCount > 0);
      assertTrue(nm.getTotalEvaluations() > 600);
      assertTrue(nm.getTotalEvaluations() < 800);

  }

  public void testRosenbrock()
    throws ObjectiveException, ConvergenceException, NotPositiveDefiniteMatrixException {

    ObjectiveFunction rosenbrock =
      new ObjectiveFunction() {
        private static final long serialVersionUID = -7039124064449091152L;
        public double objective(double[] x) {
          ++count;
          double a = x[1] - x[0] * x[0];
          double b = 1.0 - x[0];
          return 100 * a * a + b * b;
        }
      };

    count = 0;
    NelderMead nm = new NelderMead();
    try {
      nm.optimize(rosenbrock, 100, new ValueChecker(1.0e-3), true,
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
    PointValuePair optimum =
        nm.optimize(rosenbrock, 100, new ValueChecker(1.0e-3), true,
                    new double[][] {
                      { -1.2, 1.0 }, { 0.9, 1.2 }, { 3.5, -2.3 }
                    }, 10, 1642738l);

    assertTrue(count > 700);
    assertTrue(count < 800);
    assertEquals(0.0, optimum.getValue(), 5.0e-5);
    assertEquals(1.0, optimum.getPoint()[0], 0.01);
    assertEquals(1.0, optimum.getPoint()[1], 0.01);

    PointValuePair[] minima = nm.getOptima();
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
                assertTrue(minima[i-1].getValue() <= minima[i].getValue());
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
        nm.optimize(rosenbrock, 100, new ValueChecker(1.0e-3), true, rvg);
    assertEquals(0.0, optimum.getValue(), 2.0e-4);
    optimum =
        nm.optimize(rosenbrock, 100, new ValueChecker(1.0e-3), true, rvg, 3);
    assertEquals(0.0, optimum.getValue(), 3.0e-5);

  }

  public void testPowell()
    throws ObjectiveException, ConvergenceException {

    ObjectiveFunction powell =
      new ObjectiveFunction() {
        private static final long serialVersionUID = -7681075710859391520L;
        public double objective(double[] x) {
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
    PointValuePair optimum =
      nm.optimize(powell, 200, new ValueChecker(1.0e-3), true,
                  new double[] {  3.0, -1.0, 0.0, 1.0 },
                  new double[] {  4.0,  0.0, 1.0, 2.0 },
                  1, 1642738l);
    assertTrue(count < 150);
    assertEquals(0.0, optimum.getValue(), 6.0e-4);
    assertEquals(0.0, optimum.getPoint()[0], 0.07);
    assertEquals(0.0, optimum.getPoint()[1], 0.07);
    assertEquals(0.0, optimum.getPoint()[2], 0.07);
    assertEquals(0.0, optimum.getPoint()[3], 0.07);

  }

  private static class ValueChecker implements ConvergenceChecker {

    public ValueChecker(double threshold) {
      this.threshold = threshold;
    }

    public boolean converged(PointValuePair[] simplex) {
      PointValuePair smallest = simplex[0];
      PointValuePair largest  = simplex[simplex.length - 1];
      return (largest.getValue() - smallest.getValue()) < threshold;
    }

    private double threshold;

  };

  public static Test suite() {
    return new TestSuite(NelderMeadTest.class);
  }

  private int count;

}
