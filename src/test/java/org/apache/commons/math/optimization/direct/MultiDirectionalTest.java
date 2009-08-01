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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;

public class MultiDirectionalTest
  extends TestCase {

  public MultiDirectionalTest(String name) {
    super(name);
  }

  public void testFunctionEvaluationExceptions() {
      MultivariateRealFunction wrong =
          new MultivariateRealFunction() {
            private static final long serialVersionUID = 4751314470965489371L;
            public double value(double[] x) throws FunctionEvaluationException {
                if (x[0] < 0) {
                    throw new FunctionEvaluationException(x, "{0}", "oops");
                } else if (x[0] > 1) {
                    throw new FunctionEvaluationException(new RuntimeException("oops"), x);
                } else {
                    return x[0] * (1 - x[0]);
                }
            }
      };
      try {
          MultiDirectional optimizer = new MultiDirectional(0.9, 1.9);
          optimizer.optimize(wrong, GoalType.MINIMIZE, new double[] { -1.0 });
          fail("an exception should have been thrown");
      } catch (FunctionEvaluationException ce) {
          // expected behavior
          assertNull(ce.getCause());
      } catch (Exception e) {
          fail("wrong exception caught: " + e.getMessage());
      } 
      try {
          MultiDirectional optimizer = new MultiDirectional(0.9, 1.9);
          optimizer.optimize(wrong, GoalType.MINIMIZE, new double[] { +2.0 });
          fail("an exception should have been thrown");
      } catch (FunctionEvaluationException ce) {
          // expected behavior
          assertNotNull(ce.getCause());
      } catch (Exception e) {
          fail("wrong exception caught: " + e.getMessage());
      } 
  }

  public void testMinimizeMaximize()
      throws FunctionEvaluationException, ConvergenceException {

      // the following function has 4 local extrema:
      final double xM        = -3.841947088256863675365;
      final double yM        = -1.391745200270734924416;
      final double xP        =  0.2286682237349059125691;
      final double yP        = -yM;
      final double valueXmYm =  0.2373295333134216789769; // local  maximum
      final double valueXmYp = -valueXmYm;                // local  minimum
      final double valueXpYm = -0.7290400707055187115322; // global minimum
      final double valueXpYp = -valueXpYm;                // global maximum
      MultivariateRealFunction fourExtrema = new MultivariateRealFunction() {
          private static final long serialVersionUID = -7039124064449091152L;
          public double value(double[] variables) throws FunctionEvaluationException {
              final double x = variables[0];
              final double y = variables[1];
              return ((x == 0) || (y == 0)) ? 0 : (Math.atan(x) * Math.atan(x + 2) * Math.atan(y) * Math.atan(y) / (x * y));
          }
      };

      MultiDirectional optimizer = new MultiDirectional();
      optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-10, 1.0e-30));
      optimizer.setMaxIterations(200);
      optimizer.setStartConfiguration(new double[] { 0.2, 0.2 });
      RealPointValuePair optimum;

      // minimization
      optimum = optimizer.optimize(fourExtrema, GoalType.MINIMIZE, new double[] { -3.0, 0 });
      assertEquals(xM,        optimum.getPoint()[0], 4.0e-6);
      assertEquals(yP,        optimum.getPoint()[1], 3.0e-6);
      assertEquals(valueXmYp, optimum.getValue(),    8.0e-13);
      assertTrue(optimizer.getEvaluations() > 120);
      assertTrue(optimizer.getEvaluations() < 150);

      optimum = optimizer.optimize(fourExtrema, GoalType.MINIMIZE, new double[] { +1, 0 });
      assertEquals(xP,        optimum.getPoint()[0], 2.0e-8);
      assertEquals(yM,        optimum.getPoint()[1], 3.0e-6);
      assertEquals(valueXpYm, optimum.getValue(),    2.0e-12);              
      assertTrue(optimizer.getEvaluations() > 120);
      assertTrue(optimizer.getEvaluations() < 150);

      // maximization
      optimum = optimizer.optimize(fourExtrema, GoalType.MAXIMIZE, new double[] { -3.0, 0.0 });
      assertEquals(xM,        optimum.getPoint()[0], 7.0e-7);
      assertEquals(yM,        optimum.getPoint()[1], 3.0e-7);
      assertEquals(valueXmYm, optimum.getValue(),    2.0e-14);
      assertTrue(optimizer.getEvaluations() > 120);
      assertTrue(optimizer.getEvaluations() < 150);

      optimum = optimizer.optimize(fourExtrema, GoalType.MAXIMIZE, new double[] { +1, 0 });
      assertEquals(xP,        optimum.getPoint()[0], 2.0e-8);
      assertEquals(yP,        optimum.getPoint()[1], 3.0e-6);
      assertEquals(valueXpYp, optimum.getValue(),    2.0e-12);
      assertTrue(optimizer.getEvaluations() > 120);
      assertTrue(optimizer.getEvaluations() < 150);

  }

  public void testRosenbrock()
    throws FunctionEvaluationException, ConvergenceException {

    MultivariateRealFunction rosenbrock =
      new MultivariateRealFunction() {
        private static final long serialVersionUID = -9044950469615237490L;
        public double value(double[] x) throws FunctionEvaluationException {
          ++count;
          double a = x[1] - x[0] * x[0];
          double b = 1.0 - x[0];
          return 100 * a * a + b * b;
        }
      };

    count = 0;
    MultiDirectional optimizer = new MultiDirectional();
    optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1, 1.0e-3));
    optimizer.setMaxIterations(100);
    optimizer.setStartConfiguration(new double[][] {
            { -1.2,  1.0 }, { 0.9, 1.2 } , {  3.5, -2.3 }
    });
    RealPointValuePair optimum =
        optimizer.optimize(rosenbrock, GoalType.MINIMIZE, new double[] { -1.2, 1.0 });

    assertEquals(count, optimizer.getEvaluations());
    assertTrue(optimizer.getEvaluations() > 70);
    assertTrue(optimizer.getEvaluations() < 100);
    assertTrue(optimum.getValue() > 1.0e-2);

  }

  public void testPowell()
    throws FunctionEvaluationException, ConvergenceException {

    MultivariateRealFunction powell =
      new MultivariateRealFunction() {
        private static final long serialVersionUID = -832162886102041840L;
        public double value(double[] x) throws FunctionEvaluationException {
          ++count;
          double a = x[0] + 10 * x[1];
          double b = x[2] - x[3];
          double c = x[1] - 2 * x[2];
          double d = x[0] - x[3];
          return a * a + 5 * b * b + c * c * c * c + 10 * d * d * d * d;
        }
      };

    count = 0;
    MultiDirectional optimizer = new MultiDirectional();
    optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1.0, 1.0e-3));
    optimizer.setMaxIterations(1000);
    RealPointValuePair optimum =
      optimizer.optimize(powell, GoalType.MINIMIZE, new double[] { 3.0, -1.0, 0.0, 1.0 });
    assertEquals(count, optimizer.getEvaluations());
    assertTrue(optimizer.getEvaluations() > 800);
    assertTrue(optimizer.getEvaluations() < 900);
    assertTrue(optimum.getValue() > 1.0e-2);

  }

  public static Test suite() {
    return new TestSuite(MultiDirectionalTest.class);
  }

  private int count;

}
