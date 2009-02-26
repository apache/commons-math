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

import junit.framework.*;

public class MultiDirectionalTest
  extends TestCase {

  public MultiDirectionalTest(String name) {
    super(name);
  }

  public void testObjectiveExceptions() throws ConvergenceException {
      ObjectiveFunction wrong =
          new ObjectiveFunction() {
            private static final long serialVersionUID = 4751314470965489371L;
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
          new MultiDirectional(1.9, 0.4).optimize(wrong, 10, new ValueChecker(1.0e-3), true,
                                                  new double[] { -0.5 }, new double[] { 0.5 });
          fail("an exception should have been thrown");
      } catch (ObjectiveException ce) {
          // expected behavior
          assertNull(ce.getCause());
      } catch (Exception e) {
          fail("wrong exception caught: " + e.getMessage());
      } 
      try {
          new MultiDirectional(1.9, 0.4).optimize(wrong, 10, new ValueChecker(1.0e-3), true,
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

      MultiDirectional md = new MultiDirectional();

      // minimization
      md.optimize(fourExtrema, 200, new ValueChecker(1.0e-8), true,
                  new double[] { -4, -2 }, new double[] { 1, 2 }, 10, 38821113105892l);
      PointValuePair[] optima = md.getOptima();
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
                  assertEquals(valueXmYp, optimum.getValue(),     3.0e-8);
              } else {
                  // this should be the global minimum
                  ++globalCount;
                  assertEquals(xP,        optimum.getPoint()[0], 1.0e-3);
                  assertEquals(yM,        optimum.getPoint()[1], 1.0e-3);
                  assertEquals(valueXpYm, optimum.getValue(),     3.0e-8);              
              }
          }
      }
      assertTrue(localCount  > 0);
      assertTrue(globalCount > 0);
      assertTrue(md.getTotalEvaluations() > 1400);
      assertTrue(md.getTotalEvaluations() < 1700);

      // minimization
      md.optimize(fourExtrema, 200, new ValueChecker(1.0e-8), false,
                  new double[] { -3.5, -1 }, new double[] { 0.5, 1.5 }, 10, 38821113105892l);
      optima = md.getOptima();
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
                  assertEquals(valueXmYm, optimum.getValue(),     4.0e-8);
              } else {
                  // this should be the global maximum
                  ++globalCount;
                  assertEquals(xP,        optimum.getPoint()[0], 1.0e-3);
                  assertEquals(yP,        optimum.getPoint()[1], 1.0e-3);
                  assertEquals(valueXpYp, optimum.getValue(),     4.0e-8);              
              }
          }
      }
      assertTrue(localCount  > 0);
      assertTrue(globalCount > 0);
      assertTrue(md.getTotalEvaluations() > 1400);
      assertTrue(md.getTotalEvaluations() < 1700);

  }

  public void testRosenbrock()
    throws ObjectiveException, ConvergenceException {

    ObjectiveFunction rosenbrock =
      new ObjectiveFunction() {
        private static final long serialVersionUID = -9044950469615237490L;
        public double objective(double[] x) {
          ++count;
          double a = x[1] - x[0] * x[0];
          double b = 1.0 - x[0];
          return 100 * a * a + b * b;
        }
      };

    count = 0;
    PointValuePair optimum =
      new MultiDirectional().optimize(rosenbrock, 100, new ValueChecker(1.0e-3), true,
                                      new double[][] {
                                        { -1.2,  1.0 }, { 0.9, 1.2 } , {  3.5, -2.3 }
                                      });

    assertTrue(count > 60);
    assertTrue(optimum.getValue() > 0.01);

  }

  public void testPowell()
    throws ObjectiveException, ConvergenceException {

    ObjectiveFunction powell =
      new ObjectiveFunction() {
        private static final long serialVersionUID = -832162886102041840L;
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
    PointValuePair optimum =
      new MultiDirectional().optimize(powell, 1000, new ValueChecker(1.0e-3), true,
                                      new double[] {  3.0, -1.0, 0.0, 1.0 },
                                      new double[] {  4.0,  0.0, 1.0, 2.0 });
    assertTrue(count > 850);
    assertTrue(optimum.getValue() > 0.015);

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
    return new TestSuite(MultiDirectionalTest.class);
  }

  private int count;

}
