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

package org.apache.commons.math4.legacy.ode.nonstiff;


import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NoBracketingException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math4.legacy.ode.FirstOrderIntegrator;
import org.apache.commons.math4.legacy.ode.TestProblem1;
import org.apache.commons.math4.legacy.ode.TestProblem2;
import org.apache.commons.math4.legacy.ode.TestProblem3;
import org.apache.commons.math4.legacy.ode.TestProblem4;
import org.apache.commons.math4.legacy.ode.TestProblem5;
import org.apache.commons.math4.legacy.ode.TestProblem6;
import org.apache.commons.math4.legacy.ode.TestProblemAbstract;
import org.apache.commons.math4.legacy.ode.TestProblemHandler;
import org.apache.commons.math4.legacy.ode.events.EventHandler;
import org.apache.commons.math4.legacy.ode.sampling.StepHandler;
import org.apache.commons.math4.legacy.ode.sampling.StepInterpolator;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

public class LutherIntegratorTest {

    @Test
    public void testMissedEndEvent()
            throws DimensionMismatchException, NumberIsTooSmallException,
            MaxCountExceededException, NoBracketingException {
        final double   t0     = 1878250320.0000029;
        final double   tEvent = 1878250379.9999986;
        final double[] k      = { 1.0e-4, 1.0e-5, 1.0e-6 };
        FirstOrderDifferentialEquations ode = new FirstOrderDifferentialEquations() {

            @Override
            public int getDimension() {
                return k.length;
            }

            @Override
            public void computeDerivatives(double t, double[] y, double[] yDot) {
                for (int i = 0; i < y.length; ++i) {
                    yDot[i] = k[i] * y[i];
                }
            }
        };

        LutherIntegrator integrator = new LutherIntegrator(60.0);

        double[] y0   = new double[k.length];
        for (int i = 0; i < y0.length; ++i) {
            y0[i] = i + 1;
        }
        double[] y    = new double[k.length];

        double finalT = integrator.integrate(ode, t0, y0, tEvent, y);
        Assert.assertEquals(tEvent, finalT, 1.0e-15);
        for (int i = 0; i < y.length; ++i) {
            Assert.assertEquals(y0[i] * JdkMath.exp(k[i] * (finalT - t0)), y[i], 1.0e-15);
        }

        integrator.addEventHandler(new EventHandler() {

            @Override
            public void init(double t0, double[] y0, double t) {
            }

            @Override
            public void resetState(double t, double[] y) {
            }

            @Override
            public double g(double t, double[] y) {
                return t - tEvent;
            }

            @Override
            public Action eventOccurred(double t, double[] y, boolean increasing) {
                Assert.assertEquals(tEvent, t, 1.0e-15);
                return Action.CONTINUE;
            }
        }, Double.POSITIVE_INFINITY, 1.0e-20, 100);
        finalT = integrator.integrate(ode, t0, y0, tEvent + 120, y);
        Assert.assertEquals(tEvent + 120, finalT, 1.0e-15);
        for (int i = 0; i < y.length; ++i) {
            Assert.assertEquals(y0[i] * JdkMath.exp(k[i] * (finalT - t0)), y[i], 1.0e-15);
        }
    }

    @Test
    public void testSanityChecks()
            throws DimensionMismatchException, NumberIsTooSmallException,
            MaxCountExceededException, NoBracketingException {
        try  {
            TestProblem1 pb = new TestProblem1();
            new LutherIntegrator(0.01).integrate(pb,
                                                 0.0, new double[pb.getDimension()+10],
                                                 1.0, new double[pb.getDimension()]);
            Assert.fail("an exception should have been thrown");
        } catch(DimensionMismatchException ie) {
        }
        try  {
            TestProblem1 pb = new TestProblem1();
            new LutherIntegrator(0.01).integrate(pb,
                                                 0.0, new double[pb.getDimension()],
                                                 1.0, new double[pb.getDimension()+10]);
            Assert.fail("an exception should have been thrown");
        } catch(DimensionMismatchException ie) {
        }
        try  {
            TestProblem1 pb = new TestProblem1();
            new LutherIntegrator(0.01).integrate(pb,
                                                 0.0, new double[pb.getDimension()],
                                                 0.0, new double[pb.getDimension()]);
            Assert.fail("an exception should have been thrown");
        } catch(NumberIsTooSmallException ie) {
        }
    }

    @Test
    public void testDecreasingSteps()
            throws DimensionMismatchException, NumberIsTooSmallException,
            MaxCountExceededException, NoBracketingException {

        for (TestProblemAbstract pb : new TestProblemAbstract[] {
            new TestProblem1(), new TestProblem2(), new TestProblem3(),
            new TestProblem4(), new TestProblem5(), new TestProblem6()
        }) {

            double previousValueError = Double.NaN;
            double previousTimeError = Double.NaN;
            for (int i = 4; i < 10; ++i) {

                double step = (pb.getFinalTime() - pb.getInitialTime()) * JdkMath.pow(2.0, -i);

                FirstOrderIntegrator integ = new LutherIntegrator(step);
                TestProblemHandler handler = new TestProblemHandler(pb, integ);
                integ.addStepHandler(handler);
                EventHandler[] functions = pb.getEventsHandlers();
                for (int l = 0; l < functions.length; ++l) {
                    integ.addEventHandler(functions[l],
                                          Double.POSITIVE_INFINITY, 1.0e-6 * step, 1000);
                }
                Assert.assertEquals(functions.length, integ.getEventHandlers().size());
                double stopTime = integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                                                  pb.getFinalTime(), new double[pb.getDimension()]);
                if (functions.length == 0) {
                    Assert.assertEquals(pb.getFinalTime(), stopTime, 1.0e-10);
                }

                double error = handler.getMaximalValueError();
                if (i > 4) {
                    Assert.assertTrue(error < 1.01 * JdkMath.abs(previousValueError));
                }
                previousValueError = error;

                double timeError = handler.getMaximalTimeError();
                if (i > 4) {
                    Assert.assertTrue(timeError <= JdkMath.abs(previousTimeError));
                }
                previousTimeError = timeError;

                integ.clearEventHandlers();
                Assert.assertEquals(0, integ.getEventHandlers().size());
            }
        }
    }

    @Test
    public void testSmallStep()
            throws DimensionMismatchException, NumberIsTooSmallException,
            MaxCountExceededException, NoBracketingException {

        TestProblem1 pb = new TestProblem1();
        double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.001;

        FirstOrderIntegrator integ = new LutherIntegrator(step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        Assert.assertTrue(handler.getLastError() < 9.0e-17);
        Assert.assertTrue(handler.getMaximalValueError() < 4.0e-15);
        Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);
        Assert.assertEquals("Luther", integ.getName());
    }

    @Test
    public void testBigStep()
            throws DimensionMismatchException, NumberIsTooSmallException,
            MaxCountExceededException, NoBracketingException {

        TestProblem1 pb = new TestProblem1();
        double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.2;

        FirstOrderIntegrator integ = new LutherIntegrator(step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        Assert.assertTrue(handler.getLastError() > 0.00002);
        Assert.assertTrue(handler.getMaximalValueError() > 0.001);
        Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);
    }

    @Test
    public void testBackward()
            throws DimensionMismatchException, NumberIsTooSmallException,
            MaxCountExceededException, NoBracketingException {

        TestProblem5 pb = new TestProblem5();
        double step = JdkMath.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.001;

        FirstOrderIntegrator integ = new LutherIntegrator(step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        Assert.assertTrue(handler.getLastError() < 3.0e-13);
        Assert.assertTrue(handler.getMaximalValueError() < 5.0e-13);
        Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);
        Assert.assertEquals("Luther", integ.getName());
    }

    @Test
    public void testKepler()
            throws DimensionMismatchException, NumberIsTooSmallException,
            MaxCountExceededException, NoBracketingException {

        final TestProblem3 pb  = new TestProblem3(0.9);
        double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.0003;

        FirstOrderIntegrator integ = new LutherIntegrator(step);
        integ.addStepHandler(new KeplerHandler(pb));
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);
    }

    private static class KeplerHandler implements StepHandler {
        KeplerHandler(TestProblem3 pb) {
            this.pb = pb;
            maxError = 0;
        }
        @Override
        public void init(double t0, double[] y0, double t) {
            maxError = 0;
        }
        @Override
        public void handleStep(StepInterpolator interpolator, boolean isLast) {

            double[] interpolatedY = interpolator.getInterpolatedState ();
            double[] theoreticalY  = pb.computeTheoreticalState(interpolator.getCurrentTime());
            double dx = interpolatedY[0] - theoreticalY[0];
            double dy = interpolatedY[1] - theoreticalY[1];
            double error = dx * dx + dy * dy;
            if (error > maxError) {
                maxError = error;
            }
            if (isLast) {
                Assert.assertTrue(maxError < 2.2e-7);
            }
        }
        private double maxError = 0;
        private TestProblem3 pb;
    }

    @Test
    public void testStepSize()
            throws DimensionMismatchException, NumberIsTooSmallException,
            MaxCountExceededException, NoBracketingException {
        final double step = 1.23456;
        FirstOrderIntegrator integ = new LutherIntegrator(step);
        integ.addStepHandler(new StepHandler() {
            @Override
            public void handleStep(StepInterpolator interpolator, boolean isLast) {
                if (! isLast) {
                    Assert.assertEquals(step,
                                        interpolator.getCurrentTime() - interpolator.getPreviousTime(),
                                        1.0e-12);
                }
            }
            @Override
            public void init(double t0, double[] y0, double t) {
            }
        });
        integ.integrate(new FirstOrderDifferentialEquations() {
            @Override
            public void computeDerivatives(double t, double[] y, double[] dot) {
                dot[0] = 1.0;
            }
            @Override
            public int getDimension() {
                return 1;
            }
        }, 0.0, new double[] { 0.0 }, 5.0, new double[1]);
    }

    @Test
    public void testSingleStep() {

        final TestProblem3 pb  = new TestProblem3(0.9);
        double h = (pb.getFinalTime() - pb.getInitialTime()) * 0.0003;

        RungeKuttaIntegrator integ = new LutherIntegrator(Double.NaN);
        double   t = pb.getInitialTime();
        double[] y = pb.getInitialState();
        for (int i = 0; i < 100; ++i) {
            y = integ.singleStep(pb, t, y, t + h);
            t += h;
        }
        double[] yth = pb.computeTheoreticalState(t);
        double dx = y[0] - yth[0];
        double dy = y[1] - yth[1];
        double error = dx * dx + dy * dy;
        Assert.assertEquals(0.0, error, 1.0e-11);
    }
}
