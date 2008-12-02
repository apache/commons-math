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

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.events.EventHandler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AdamsMoultonIntegratorTest
  extends TestCase {

    public AdamsMoultonIntegratorTest(String name) {
        super(name);
    }

    public void testPredictorCoefficients() {
        for (int order = 1; order < 10; ++order) {
            double[] moulton = new AdamsMoultonIntegrator(order, 0.01).getPredictorCoeffs();
            double[] bashforth  = new AdamsBashforthIntegrator(order, 0.01).getCoeffs();
            assertEquals(bashforth.length, moulton.length);
            for (int i = 0; i < moulton.length; ++i) {
                assertEquals(bashforth[i], moulton[i], 1.0e-16);
            }
        }
    }

    public void testCorrectorCoefficients() {

        double[] coeffs1 = new AdamsMoultonIntegrator(1, 0.01).getCorrectorCoeffs();
        assertEquals(2, coeffs1.length);
        assertEquals(1.0 / 2.0, coeffs1[0], 1.0e-16);
        assertEquals(1.0 / 2.0, coeffs1[1], 1.0e-16);

        double[] coeffs2 = new AdamsMoultonIntegrator(2, 0.01).getCorrectorCoeffs();
        assertEquals(3, coeffs2.length);
        assertEquals( 5.0 / 12.0, coeffs2[0], 1.0e-16);
        assertEquals( 8.0 / 12.0, coeffs2[1], 1.0e-16);
        assertEquals(-1.0 / 12.0, coeffs2[2], 1.0e-16);

        double[] coeffs3 = new AdamsMoultonIntegrator(3, 0.01).getCorrectorCoeffs();
        assertEquals(4, coeffs3.length);
        assertEquals( 9.0 / 24.0, coeffs3[0], 1.0e-16);
        assertEquals(19.0 / 24.0, coeffs3[1], 1.0e-16);
        assertEquals(-5.0 / 24.0, coeffs3[2], 1.0e-16);
        assertEquals( 1.0 / 24.0, coeffs3[3], 1.0e-16);

        double[] coeffs4 = new AdamsMoultonIntegrator(4, 0.01).getCorrectorCoeffs();
        assertEquals(5, coeffs4.length);
        assertEquals( 251.0 / 720.0, coeffs4[0], 1.0e-16);
        assertEquals( 646.0 / 720.0, coeffs4[1], 1.0e-16);
        assertEquals(-264.0 / 720.0, coeffs4[2], 1.0e-16);
        assertEquals( 106.0 / 720.0, coeffs4[3], 1.0e-16);
        assertEquals( -19.0 / 720.0, coeffs4[4], 1.0e-16);

        double[] coeffs5 = new AdamsMoultonIntegrator(5, 0.01).getCorrectorCoeffs();
        assertEquals(6, coeffs5.length);
        assertEquals( 475.0 / 1440.0, coeffs5[0], 1.0e-16);
        assertEquals(1427.0 / 1440.0, coeffs5[1], 1.0e-16);
        assertEquals(-798.0 / 1440.0, coeffs5[2], 1.0e-16);
        assertEquals( 482.0 / 1440.0, coeffs5[3], 1.0e-16);
        assertEquals(-173.0 / 1440.0, coeffs5[4], 1.0e-16);
        assertEquals(  27.0 / 1440.0, coeffs5[5], 1.0e-16);

        double[] coeffs6 = new AdamsMoultonIntegrator(6, 0.01).getCorrectorCoeffs();
        assertEquals(7, coeffs6.length);
        assertEquals( 19087.0 / 60480.0, coeffs6[0], 1.0e-16);
        assertEquals( 65112.0 / 60480.0, coeffs6[1], 1.0e-16);
        assertEquals(-46461.0 / 60480.0, coeffs6[2], 1.0e-16);
        assertEquals( 37504.0 / 60480.0, coeffs6[3], 1.0e-16);
        assertEquals(-20211.0 / 60480.0, coeffs6[4], 1.0e-16);
        assertEquals(  6312.0 / 60480.0, coeffs6[5], 1.0e-16);
        assertEquals(  -863.0 / 60480.0, coeffs6[6], 1.0e-16);

        double[] coeffs7 = new AdamsMoultonIntegrator(7, 0.01).getCorrectorCoeffs();
        assertEquals(8, coeffs7.length);
        assertEquals(  36799.0 / 120960.0, coeffs7[0], 1.0e-16);
        assertEquals( 139849.0 / 120960.0, coeffs7[1], 1.0e-16);
        assertEquals(-121797.0 / 120960.0, coeffs7[2], 1.0e-16);
        assertEquals( 123133.0 / 120960.0, coeffs7[3], 1.0e-16);
        assertEquals( -88547.0 / 120960.0, coeffs7[4], 1.0e-16);
        assertEquals(  41499.0 / 120960.0, coeffs7[5], 1.0e-16);
        assertEquals( -11351.0 / 120960.0, coeffs7[6], 1.0e-16);
        assertEquals(   1375.0 / 120960.0, coeffs7[7], 1.0e-16);

        double[] coeffs8 = new AdamsMoultonIntegrator(8, 0.01).getCorrectorCoeffs();
        assertEquals(9, coeffs8.length);
        assertEquals( 1070017.0 / 3628800.0, coeffs8[0], 1.0e-16);
        assertEquals( 4467094.0 / 3628800.0, coeffs8[1], 1.0e-16);
        assertEquals(-4604594.0 / 3628800.0, coeffs8[2], 1.0e-16);
        assertEquals( 5595358.0 / 3628800.0, coeffs8[3], 1.0e-16);
        assertEquals(-5033120.0 / 3628800.0, coeffs8[4], 1.0e-16);
        assertEquals( 3146338.0 / 3628800.0, coeffs8[5], 1.0e-16);
        assertEquals(-1291214.0 / 3628800.0, coeffs8[6], 1.0e-16);
        assertEquals(  312874.0 / 3628800.0, coeffs8[7], 1.0e-16);
        assertEquals(  -33953.0 / 3628800.0, coeffs8[8], 1.0e-16);

    }

    public void testDimensionCheck() {
        try  {
            TestProblem1 pb = new TestProblem1();
            new AdamsMoultonIntegrator(3, 0.01).integrate(pb,
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
                if (pb instanceof TestProblem3) {
                    step /= 8;
                }

                FirstOrderIntegrator integ = new AdamsMoultonIntegrator(5, step);
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

        FirstOrderIntegrator integ = new AdamsMoultonIntegrator(3, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb,
                pb.getInitialTime(), pb.getInitialState(),
                pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() < 7.0e-12);
        assertTrue(handler.getMaximalValueError() < 4.0e-11);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-14);
        assertEquals("Adams-Moulton", integ.getName());

    }

    public void testBigStep()
        throws DerivativeException, IntegratorException {

        TestProblem1 pb  = new TestProblem1();
        double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.2;

        FirstOrderIntegrator integ = new AdamsMoultonIntegrator(3, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb,
                pb.getInitialTime(), pb.getInitialState(),
                pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() > 0.01);
        assertTrue(handler.getMaximalValueError() > 0.03);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-14);

    }

    public void testBackward()
        throws DerivativeException, IntegratorException {

        TestProblem5 pb = new TestProblem5();
        double step = Math.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.001;

        FirstOrderIntegrator integ = new AdamsMoultonIntegrator(5, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() < 5.0e-10);
        assertTrue(handler.getMaximalValueError() < 7.0e-10);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);
        assertEquals("Adams-Moulton", integ.getName());
    }

    public static Test suite() {
        return new TestSuite(AdamsMoultonIntegratorTest.class);
    }

}
