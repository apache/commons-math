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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.events.EventHandler;
import org.junit.Test;

public class AdamsIntegratorTest {

    @Test(expected=IntegratorException.class)
    public void dimensionCheckBashforth() throws DerivativeException, IntegratorException {
        TestProblem1 pb = new TestProblem1();
        new AdamsIntegrator(3, false, 0.01).integrate(pb,
                                                      0.0, new double[pb.getDimension()+10],
                                                      1.0, new double[pb.getDimension()+10]);
    }

    @Test
    public void decreasingStepsBashforth() throws DerivativeException, IntegratorException {

        TestProblemAbstract[] problems = TestProblemFactory.getProblems();
        for (int k = 0; k < problems.length; ++k) {

            double previousError = Double.NaN;
            for (int i = 6; i < 10; ++i) {

                TestProblemAbstract pb  = (TestProblemAbstract) problems[k].clone();
                double step = (pb.getFinalTime() - pb.getInitialTime()) * Math.pow(2.0, -i);

                FirstOrderIntegrator integ = new AdamsIntegrator(5, false, step);
                TestProblemHandler handler = new TestProblemHandler(pb, integ);
                integ.addStepHandler(handler);
                EventHandler[] functions = pb.getEventsHandlers();
                for (int l = 0; l < functions.length; ++l) {
                    integ.addEventHandler(functions[l],
                                          Double.POSITIVE_INFINITY, 1.0e-3 * step, 1000);
                }
                double stopTime = integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                                                  pb.getFinalTime(), new double[pb.getDimension()]);
                if (functions.length == 0) {
                    assertEquals(pb.getFinalTime(), stopTime, 1.0e-10);
                }

                double error = handler.getMaximalValueError();
                if ((i > 6) && !(pb instanceof TestProblem4) && !(pb instanceof TestProblem6)) {
                    assertTrue(error <= Math.abs(1.05 * previousError));
                }
                previousError = error;

            }

        }

    }

    @Test
    public void smallStepBashforth() throws DerivativeException, IntegratorException {

        TestProblem1 pb  = new TestProblem1();
        double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.001;

        FirstOrderIntegrator integ = new AdamsIntegrator(3, false, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() < 2.0e-9);
        assertTrue(handler.getMaximalValueError() < 9.0e-9);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-14);
        assertEquals("Adams-Bashforth", integ.getName());

    }

    @Test
    public void bigStepBashforth() throws DerivativeException, IntegratorException {

        TestProblem1 pb  = new TestProblem1();
        double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.2;

        FirstOrderIntegrator integ = new AdamsIntegrator(3, false, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() > 0.06);
        assertTrue(handler.getMaximalValueError() > 0.06);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-14);

    }

    @Test
    public void backwardBashforth() throws DerivativeException, IntegratorException {

        TestProblem5 pb = new TestProblem5();
        double step = Math.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.001;

        FirstOrderIntegrator integ = new AdamsIntegrator(5, false, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() < 8.0e-11);
        assertTrue(handler.getMaximalValueError() < 8.0e-11);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-15);
        assertEquals("Adams-Bashforth", integ.getName());
    }

    @Test
    public void polynomialBashforth() throws DerivativeException, IntegratorException {
        TestProblem6 pb = new TestProblem6();
        double step = Math.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.02;

        for (int order = 2; order < 9; ++order) {
            AdamsIntegrator integ = new AdamsIntegrator(order, false, step);
            integ.setStarterIntegrator(new DormandPrince853Integrator(1.0e-3 * step, 1.0e3 * step,
                                                                      1.0e-5, 1.0e-5));
            TestProblemHandler handler = new TestProblemHandler(pb, integ);
            integ.addStepHandler(handler);
            integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                            pb.getFinalTime(), new double[pb.getDimension()]);
            if (order < 5) {
                assertTrue(handler.getMaximalValueError() > 1.0e-5);
            } else {
                assertTrue(handler.getMaximalValueError() < 7.0e-12);
            }
        }

    }

    @Test
    public void serializationBashforth()
        throws IntegratorException, DerivativeException,
               IOException, ClassNotFoundException {

        TestProblem6 pb = new TestProblem6();
        double step = Math.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.01;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(bos);
        oos.writeObject(new AdamsIntegrator(8, false, step));
        assertTrue(bos.size() > 3000);
        assertTrue(bos.size() < 3100);

        ByteArrayInputStream  bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream     ois = new ObjectInputStream(bis);
        FirstOrderIntegrator integ  = (AdamsIntegrator) ois.readObject();
        assertEquals("Adams-Bashforth", integ.getName());
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);
        assertTrue(handler.getMaximalValueError() < 7.0e-13);

    }

    @Test(expected=IntegratorException.class)
    public void dimensionCheckMoulton()
        throws DerivativeException, IntegratorException {
        TestProblem1 pb = new TestProblem1();
        new AdamsIntegrator(3, true, 0.01).integrate(pb,
                                                     0.0, new double[pb.getDimension()+10],
                                                     1.0, new double[pb.getDimension()+10]);
    }

    @Test
    public void decreasingStepsMoulton()
        throws DerivativeException, IntegratorException {

        TestProblemAbstract[] problems = TestProblemFactory.getProblems();
        for (int k = 0; k < problems.length; ++k) {

            double previousError = Double.NaN;
            for (int i = 6; i < 10; ++i) {

                TestProblemAbstract pb  = (TestProblemAbstract) problems[k].clone();
                double step = (pb.getFinalTime() - pb.getInitialTime()) * Math.pow(2.0, -i);

                FirstOrderIntegrator integ = new AdamsIntegrator(5, true, step);
                TestProblemHandler handler = new TestProblemHandler(pb, integ);
                integ.addStepHandler(handler);
                EventHandler[] functions = pb.getEventsHandlers();
                for (int l = 0; l < functions.length; ++l) {
                    integ.addEventHandler(functions[l],
                                          Double.POSITIVE_INFINITY, 1.0e-3 * step, 1000);
                }
                double stopTime = integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                                                  pb.getFinalTime(), new double[pb.getDimension()]);
                if (functions.length == 0) {
                    assertEquals(pb.getFinalTime(), stopTime, 1.0e-10);
                }

                double error = handler.getMaximalValueError();
                if ((i > 6) && !(pb instanceof TestProblem4) && !(pb instanceof TestProblem6)) {
                    assertTrue(error <= Math.abs(1.05 * previousError));
                }
                previousError = error;

            }

        }

    }

    @Test
    public void smallStepMoulton()
        throws DerivativeException, IntegratorException {

        TestProblem1 pb  = new TestProblem1();
        double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.001;

        FirstOrderIntegrator integ = new AdamsIntegrator(3, true, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb,
                pb.getInitialTime(), pb.getInitialState(),
                pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() < 1.0e-14);
        assertTrue(handler.getMaximalValueError() < 2.0e-17);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-15);
        assertEquals("Adams-Moulton", integ.getName());

    }

    @Test
    public void bigStepMoulton()
        throws DerivativeException, IntegratorException {

        TestProblem1 pb  = new TestProblem1();
        double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.2;

        FirstOrderIntegrator integ = new AdamsIntegrator(3, true, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb,
                pb.getInitialTime(), pb.getInitialState(),
                pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getMaximalValueError() > 6.0e-6);

    }

    @Test
    public void backwardMoulton()
        throws DerivativeException, IntegratorException {

        TestProblem5 pb = new TestProblem5();
        double step = Math.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.001;

        FirstOrderIntegrator integ = new AdamsIntegrator(5, true, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() < 1.0e-15);
        assertTrue(handler.getMaximalValueError() < 3.0e-16);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-15);
        assertEquals("Adams-Moulton", integ.getName());
    }

    @Test
    public void polynomialMoulton()
        throws DerivativeException, IntegratorException {
        TestProblem6 pb = new TestProblem6();
        double step = Math.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.02;

        for (int order = 2; order < 9; ++order) {
            AdamsIntegrator integ = new AdamsIntegrator(order, true, step);
            integ.setStarterIntegrator(new DormandPrince853Integrator(1.0e-3 * step, 1.0e3 * step,
                                                                      1.0e-5, 1.0e-5));
            TestProblemHandler handler = new TestProblemHandler(pb, integ);
            integ.addStepHandler(handler);
            integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                            pb.getFinalTime(), new double[pb.getDimension()]);
            assertTrue(handler.getMaximalValueError() < 2.0e-13);
        }

    }

}
