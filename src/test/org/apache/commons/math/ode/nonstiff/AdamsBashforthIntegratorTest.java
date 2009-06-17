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

public class AdamsBashforthIntegratorTest {

    @Test(expected=IntegratorException.class)
    public void dimensionCheck() throws DerivativeException, IntegratorException {
        TestProblem1 pb = new TestProblem1();
        new AdamsBashforthIntegrator(3, 0.01).integrate(pb,
                                                        0.0, new double[pb.getDimension()+10],
                                                        1.0, new double[pb.getDimension()+10]);
    }

    @Test
    public void decreasingSteps() throws DerivativeException, IntegratorException {

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
    public void smallStep() throws DerivativeException, IntegratorException {

        TestProblem1 pb  = new TestProblem1();
        double range = pb.getFinalTime() - pb.getInitialTime();
        double step = range * 0.001;

        AdamsBashforthIntegrator integ = new AdamsBashforthIntegrator(3, step);
        integ.setStarterIntegrator(new DormandPrince853Integrator(0, range, 1.0e-12, 1.0e-12));
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() < 2.0e-9);
        assertTrue(handler.getMaximalValueError() < 9.0e-9);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-16);
        assertEquals("Adams-Bashforth", integ.getName());
        assertTrue(integ.getEvaluations() > 1000);
        assertEquals(Integer.MAX_VALUE, integ.getMaxEvaluations());

    }

    @Test(expected = DerivativeException.class)
    public void exceedMaxEvaluations() throws DerivativeException, IntegratorException {

        TestProblem1 pb  = new TestProblem1();
        double range = pb.getFinalTime() - pb.getInitialTime();
        double step = range * 0.001;

        AdamsBashforthIntegrator integ = new AdamsBashforthIntegrator(3, step);
        integ.setStarterIntegrator(new DormandPrince853Integrator(0, range, 1.0e-12, 1.0e-12));
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.setMaxEvaluations(1000);
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

    }

    @Test
    public void bigStep() throws DerivativeException, IntegratorException {

        TestProblem1 pb  = new TestProblem1();
        double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.2;

        FirstOrderIntegrator integ = new AdamsBashforthIntegrator(3, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() > 0.06);
        assertTrue(handler.getMaximalValueError() > 0.06);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-16);

    }

    @Test
    public void backward() throws DerivativeException, IntegratorException {

        TestProblem5 pb = new TestProblem5();
        double step = Math.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.001;

        FirstOrderIntegrator integ = new AdamsBashforthIntegrator(5, step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() < 8.0e-11);
        assertTrue(handler.getMaximalValueError() < 8.0e-11);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-16);
        assertEquals("Adams-Bashforth", integ.getName());
    }

    @Test
    public void polynomial() throws DerivativeException, IntegratorException {
        TestProblem6 pb = new TestProblem6();
        double step = Math.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.02;

        for (int order = 2; order < 9; ++order) {
            AdamsBashforthIntegrator integ = new AdamsBashforthIntegrator(order, step);
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
    public void serialization()
        throws IntegratorException, DerivativeException,
               IOException, ClassNotFoundException {

        TestProblem6 pb = new TestProblem6();
        double step = Math.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.01;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(bos);
        oos.writeObject(new AdamsBashforthIntegrator(8, step));
        
        ByteArrayInputStream  bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream     ois = new ObjectInputStream(bis);
        FirstOrderIntegrator integ  = (AdamsBashforthIntegrator) ois.readObject();
        assertEquals("Adams-Bashforth", integ.getName());
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);
        assertTrue(handler.getMaximalValueError() < 7.0e-13);

    }

}
