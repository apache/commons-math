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

package org.apache.commons.math4.legacy.ode.events;


import org.apache.commons.math4.legacy.analysis.solvers.BrentSolver;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NoBracketingException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ode.ExpandableStatefulODE;
import org.apache.commons.math4.legacy.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math4.legacy.ode.SecondaryEquations;
import org.apache.commons.math4.legacy.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math4.legacy.ode.nonstiff.LutherIntegrator;
import org.apache.commons.math4.legacy.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math4.legacy.ode.sampling.DummyStepInterpolator;
import org.junit.Assert;
import org.junit.Test;

public class EventStateTest {

    // JIRA: MATH-322
    @Test
    public void closeEvents() throws MaxCountExceededException, NoBracketingException {

        final double r1  = 90.0;
        final double r2  = 135.0;
        final double gap = r2 - r1;

        final double tolerance = 0.1;
        EventState es = new EventState(new CloseEventsGenerator(r1, r2), 1.5 * gap,
                                       tolerance, 100,
                                       new BrentSolver(tolerance));
        es.setExpandable(new ExpandableStatefulODE(new FirstOrderDifferentialEquations() {
            @Override
            public int getDimension() {
                return 0;
            }
            @Override
            public void computeDerivatives(double t, double[] y, double[] yDot) {
            }
        }));

        AbstractStepInterpolator interpolator =
            new DummyStepInterpolator(new double[0], new double[0], true);
        interpolator.storeTime(r1 - 2.5 * gap);
        interpolator.shift();
        interpolator.storeTime(r1 - 1.5 * gap);
        es.reinitializeBegin(interpolator);

        interpolator.shift();
        interpolator.storeTime(r1 - 0.5 * gap);
        Assert.assertFalse(es.evaluateStep(interpolator));

        interpolator.shift();
        interpolator.storeTime(0.5 * (r1 + r2));
        Assert.assertTrue(es.evaluateStep(interpolator));
        Assert.assertEquals(r1, es.getEventTime(), tolerance);
        es.stepAccepted(es.getEventTime(), new double[0]);

        interpolator.shift();
        interpolator.storeTime(r2 + 0.4 * gap);
        Assert.assertTrue(es.evaluateStep(interpolator));
        Assert.assertEquals(r2, es.getEventTime(), tolerance);
    }

    // Jira: MATH-695
    @Test
    public void testIssue695()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        FirstOrderDifferentialEquations equation = new FirstOrderDifferentialEquations() {

            @Override
            public int getDimension() {
                return 1;
            }

            @Override
            public void computeDerivatives(double t, double[] y, double[] yDot) {
                yDot[0] = 1.0;
            }
        };

        DormandPrince853Integrator integrator = new DormandPrince853Integrator(0.001, 1000, 1.0e-14, 1.0e-14);
        integrator.addEventHandler(new ResettingEvent(10.99), 0.1, 1.0e-9, 1000);
        integrator.addEventHandler(new ResettingEvent(11.01), 0.1, 1.0e-9, 1000);
        integrator.setInitialStepSize(3.0);

        double target = 30.0;
        double[] y = new double[1];
        double tEnd = integrator.integrate(equation, 0.0, y, target, y);
        Assert.assertEquals(target, tEnd, 1.0e-10);
        Assert.assertEquals(32.0, y[0], 1.0e-10);
    }

    private static class ResettingEvent implements EventHandler {

        private static double lastTriggerTime = Double.NEGATIVE_INFINITY;
        private final double tEvent;

        ResettingEvent(final double tEvent) {
            this.tEvent = tEvent;
        }

        @Override
        public void init(double t0, double[] y0, double t) {
        }

        @Override
        public double g(double t, double[] y) {
            // the bug corresponding to issue 695 causes the g function
            // to be called at obsolete times t despite an event
            // occurring later has already been triggered.
            // When this occurs, the following assertion is violated
            Assert.assertTrue("going backward in time! (" + t + " < " + lastTriggerTime + ")",
                              t >= lastTriggerTime);
            return t - tEvent;
        }

        @Override
        public Action eventOccurred(double t, double[] y, boolean increasing) {
            // remember in a class variable when the event was triggered
            lastTriggerTime = t;
            return Action.RESET_STATE;
        }

        @Override
        public void resetState(double t, double[] y) {
            y[0] += 1.0;
        }
    }

    // Jira: MATH-965
    @Test
    public void testIssue965()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        ExpandableStatefulODE equation =
                new ExpandableStatefulODE(new FirstOrderDifferentialEquations() {

            @Override
            public int getDimension() {
                return 1;
            }

            @Override
            public void computeDerivatives(double t, double[] y, double[] yDot) {
                yDot[0] = 2.0;
            }
        });
        equation.setTime(0.0);
        equation.setPrimaryState(new double[1]);
        equation.addSecondaryEquations(new SecondaryEquations() {

            @Override
            public int getDimension() {
                return 1;
            }

            @Override
            public void computeDerivatives(double t, double[] primary,
                                           double[] primaryDot, double[] secondary,
                                           double[] secondaryDot) {
                secondaryDot[0] = -3.0;
            }
        });
        int index = equation.getSecondaryMappers()[0].getFirstIndex();

        DormandPrince853Integrator integrator = new DormandPrince853Integrator(0.001, 1000, 1.0e-14, 1.0e-14);
        integrator.addEventHandler(new SecondaryStateEvent(index, -3.0), 0.1, 1.0e-9, 1000);
        integrator.setInitialStepSize(3.0);

        integrator.integrate(equation, 30.0);
        Assert.assertEquals( 1.0, equation.getTime(), 1.0e-10);
        Assert.assertEquals( 2.0, equation.getPrimaryState()[0], 1.0e-10);
        Assert.assertEquals(-3.0, equation.getSecondaryState(0)[0], 1.0e-10);
    }

    private static class SecondaryStateEvent implements EventHandler {

        private int index;
        private final double target;

        SecondaryStateEvent(final int index, final double target) {
            this.index  = index;
            this.target = target;
        }

        @Override
        public void init(double t0, double[] y0, double t) {
        }

        @Override
        public double g(double t, double[] y) {
            return y[index] - target;
        }

        @Override
        public Action eventOccurred(double t, double[] y, boolean increasing) {
            return Action.STOP;
        }

        @Override
        public void resetState(double t, double[] y) {
        }
    }

    @Test
    public void testEventsCloserThanThreshold()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        FirstOrderDifferentialEquations equation = new FirstOrderDifferentialEquations() {

            @Override
            public int getDimension() {
                return 1;
            }

            @Override
            public void computeDerivatives(double t, double[] y, double[] yDot) {
                yDot[0] = 1.0;
            }
        };

        LutherIntegrator integrator = new LutherIntegrator(20.0);
        CloseEventsGenerator eventsGenerator =
                        new CloseEventsGenerator(9.0 - 1.0 / 128, 9.0 + 1.0 / 128);
        integrator.addEventHandler(eventsGenerator, 1.0, 0.02, 1000);
        double[] y = new double[1];
        double tEnd = integrator.integrate(equation, 0.0, y, 100.0, y);
        Assert.assertEquals( 2, eventsGenerator.getCount());
        Assert.assertEquals( 9.0 + 1.0 / 128, tEnd, 1.0 / 32.0);
    }

    private class CloseEventsGenerator implements EventHandler {

        private final double r1;
        private final double r2;
        private int count;

        CloseEventsGenerator(final double r1, final double r2) {
            this.r1    = r1;
            this.r2    = r2;
            this.count = 0;
        }

        @Override
        public void init(double t0, double[] y0, double t) {
        }

        @Override
        public void resetState(double t, double[] y) {
        }

        @Override
        public double g(double t, double[] y) {
            return (t - r1) * (r2 - t);
        }

        @Override
        public Action eventOccurred(double t, double[] y, boolean increasing) {
            return ++count < 2 ? Action.CONTINUE : Action.STOP;
        }

        public int getCount() {
            return count;
        }
    }
}
