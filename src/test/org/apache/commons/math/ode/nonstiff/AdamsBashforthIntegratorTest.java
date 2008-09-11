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

package org.apache.commons.math.ode.nonstiff;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.events.EventHandler;

public class AdamsBashforthIntegratorTest
  extends TestCase {

  public AdamsBashforthIntegratorTest(String name) {
    super(name);
  }

  public void testCoefficients() {

      double[] coeffs1 = new AdamsBashforthIntegrator(1, 0.01).getCoeffs();
      assertEquals(1, coeffs1.length);
      assertEquals(1.0, coeffs1[0], 1.0e-16);

      double[] coeffs2 = new AdamsBashforthIntegrator(2, 0.01).getCoeffs();
      assertEquals(2, coeffs2.length);
      assertEquals( 3.0 / 2.0, coeffs2[0], 1.0e-16);
      assertEquals(-1.0 / 2.0, coeffs2[1], 1.0e-16);

      double[] coeffs3 = new AdamsBashforthIntegrator(3, 0.01).getCoeffs();
      assertEquals(3, coeffs3.length);
      assertEquals( 23.0 / 12.0, coeffs3[0], 1.0e-16);
      assertEquals(-16.0 / 12.0, coeffs3[1], 1.0e-16);
      assertEquals(  5.0 / 12.0, coeffs3[2], 1.0e-16);

      double[] coeffs4 = new AdamsBashforthIntegrator(4, 0.01).getCoeffs();
      assertEquals(4, coeffs4.length);
      assertEquals( 55.0 / 24.0, coeffs4[0], 1.0e-16);
      assertEquals(-59.0 / 24.0, coeffs4[1], 1.0e-16);
      assertEquals( 37.0 / 24.0, coeffs4[2], 1.0e-16);
      assertEquals( -9.0 / 24.0, coeffs4[3], 1.0e-16);

      double[] coeffs5 = new AdamsBashforthIntegrator(5, 0.01).getCoeffs();
      assertEquals(5, coeffs5.length);
      assertEquals( 1901.0 / 720.0, coeffs5[0], 1.0e-16);
      assertEquals(-2774.0 / 720.0, coeffs5[1], 1.0e-16);
      assertEquals( 2616.0 / 720.0, coeffs5[2], 1.0e-16);
      assertEquals(-1274.0 / 720.0, coeffs5[3], 1.0e-16);
      assertEquals(  251.0 / 720.0, coeffs5[4], 1.0e-16);

      double[] coeffs6 = new AdamsBashforthIntegrator(6, 0.01).getCoeffs();
      assertEquals(6, coeffs6.length);
      assertEquals( 4277.0 / 1440.0, coeffs6[0], 1.0e-16);
      assertEquals(-7923.0 / 1440.0, coeffs6[1], 1.0e-16);
      assertEquals( 9982.0 / 1440.0, coeffs6[2], 1.0e-16);
      assertEquals(-7298.0 / 1440.0, coeffs6[3], 1.0e-16);
      assertEquals( 2877.0 / 1440.0, coeffs6[4], 1.0e-16);
      assertEquals( -475.0 / 1440.0, coeffs6[5], 1.0e-16);

      double[] coeffs7 = new AdamsBashforthIntegrator(7, 0.01).getCoeffs();
      assertEquals(7, coeffs7.length);
      assertEquals( 198721.0 / 60480.0, coeffs7[0], 1.0e-16);
      assertEquals(-447288.0 / 60480.0, coeffs7[1], 1.0e-16);
      assertEquals( 705549.0 / 60480.0, coeffs7[2], 1.0e-16);
      assertEquals(-688256.0 / 60480.0, coeffs7[3], 1.0e-16);
      assertEquals( 407139.0 / 60480.0, coeffs7[4], 1.0e-16);
      assertEquals(-134472.0 / 60480.0, coeffs7[5], 1.0e-16);
      assertEquals(  19087.0 / 60480.0, coeffs7[6], 1.0e-16);

      double[] coeffs8 = new AdamsBashforthIntegrator(8, 0.01).getCoeffs();
      assertEquals(8, coeffs8.length);
      assertEquals(  434241.0 / 120960.0, coeffs8[0], 1.0e-16);
      assertEquals(-1152169.0 / 120960.0, coeffs8[1], 1.0e-16);
      assertEquals( 2183877.0 / 120960.0, coeffs8[2], 1.0e-16);
      assertEquals(-2664477.0 / 120960.0, coeffs8[3], 1.0e-16);
      assertEquals( 2102243.0 / 120960.0, coeffs8[4], 1.0e-16);
      assertEquals(-1041723.0 / 120960.0, coeffs8[5], 1.0e-16);
      assertEquals(  295767.0 / 120960.0, coeffs8[6], 1.0e-16);
      assertEquals(  -36799.0 / 120960.0, coeffs8[7], 1.0e-16);

      double[] coeffs9 = new AdamsBashforthIntegrator(9, 0.01).getCoeffs();
      assertEquals(9, coeffs9.length);
      assertEquals(  14097247.0 / 3628800.0, coeffs9[0], 1.0e-16);
      assertEquals( -43125206.0 / 3628800.0, coeffs9[1], 1.0e-16);
      assertEquals(  95476786.0 / 3628800.0, coeffs9[2], 1.0e-16);
      assertEquals(-139855262.0 / 3628800.0, coeffs9[3], 1.0e-16);
      assertEquals( 137968480.0 / 3628800.0, coeffs9[4], 1.0e-16);
      assertEquals( -91172642.0 / 3628800.0, coeffs9[5], 1.0e-16);
      assertEquals(  38833486.0 / 3628800.0, coeffs9[6], 1.0e-16);
      assertEquals(  -9664106.0 / 3628800.0, coeffs9[7], 1.0e-16);
      assertEquals(   1070017.0 / 3628800.0, coeffs9[8], 1.0e-16);

  }

  public void testDimensionCheck() {
    try  {
      TestProblem1 pb = new TestProblem1();
      new AdamsBashforthIntegrator(3, 0.01).integrate(pb,
                                                      0.0, new double[pb.getDimension()+10],
                                                      1.0, new double[pb.getDimension()+10]);
        fail("an exception should have been thrown");
    } catch(DerivativeException de) {
      fail("wrong exception caught");
    } catch(IntegratorException ie) {
    }
  }
  
  public void testDecreasingSteps()
    throws DerivativeException, IntegratorException {

    TestProblemAbstract[] problems = TestProblemFactory.getProblems();
    for (int k = 0; k < problems.length; ++k) {

      double previousError = Double.NaN;
      for (int i = 6; i < 10; ++i) {

        TestProblemAbstract pb  = (TestProblemAbstract) problems[k].clone();
        double step = (pb.getFinalTime() - pb.getInitialTime()) * Math.pow(2.0, -i);

        FirstOrderIntegrator integ = new AdamsBashforthIntegrator(5, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        EventHandler[] functions = pb.getEventsHandlers();
        for (int l = 0; l < functions.length; ++l) {
          integ.addEventHandler(functions[l],
                                Double.POSITIVE_INFINITY, 1.0e-6 * step, 1000);
        }
        double stopTime = integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                                          pb.getFinalTime(), new double[pb.getDimension()]);
        if (functions.length == 0) {
          assertEquals(pb.getFinalTime(), stopTime, 1.0e-10);
        }

        double error = handler.getMaximalValueError();
        if (i > 6) {
          assertTrue(error < Math.abs(previousError));
        }
        previousError = error;

      }

    }

  }

  public void testSmallStep()
    throws DerivativeException, IntegratorException {

    TestProblem1 pb  = new TestProblem1();
    double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.001;

    FirstOrderIntegrator integ = new AdamsBashforthIntegrator(3, step);
    TestProblemHandler handler = new TestProblemHandler(pb, integ);
    integ.addStepHandler(handler);
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

   assertTrue(handler.getLastError() < 2.0e-9);
   assertTrue(handler.getMaximalValueError() < 3.0e-8);
   assertEquals(0, handler.getMaximalTimeError(), 1.0e-14);
   assertEquals("Adams-Bashforth", integ.getName());

  }

  public void testBigStep()
    throws DerivativeException, IntegratorException {

    TestProblem1 pb  = new TestProblem1();
    double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.2;

    FirstOrderIntegrator integ = new AdamsBashforthIntegrator(3, step);
    TestProblemHandler handler = new TestProblemHandler(pb, integ);
    integ.addStepHandler(handler);
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    assertTrue(handler.getLastError() > 0.05);
    assertTrue(handler.getMaximalValueError() > 0.1);
    assertEquals(0, handler.getMaximalTimeError(), 1.0e-14);

  }

  public void testBackward()
      throws DerivativeException, IntegratorException {

      TestProblem5 pb = new TestProblem5();
      double step = Math.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.001;

      FirstOrderIntegrator integ = new AdamsBashforthIntegrator(5, step);
      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);

      assertTrue(handler.getLastError() < 8.0e-11);
      assertTrue(handler.getMaximalValueError() < 8.0e-11);
      assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);
      assertEquals("Adams-Bashforth", integ.getName());
  }

  public static Test suite() {
    return new TestSuite(AdamsBashforthIntegratorTest.class);
  }

}
