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

import org.apache.commons.math4.legacy.analysis.solvers.BracketingNthOrderBrentSolver;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NoBracketingException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math4.legacy.ode.FirstOrderIntegrator;
import org.apache.commons.math4.legacy.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

public class EventFilterTest {

    @Test
    public void testHistoryIncreasingForward() {

        // start point: g > 0
        testHistory(FilterType.TRIGGER_ONLY_INCREASING_EVENTS,
                    0.5 * JdkMath.PI, 30.5 * JdkMath.PI, JdkMath.PI, -1);

        // start point: g = 0
        testHistory(FilterType.TRIGGER_ONLY_INCREASING_EVENTS,
                    0, 30.5 * JdkMath.PI, JdkMath.PI, -1);

        // start point: g < 0
        testHistory(FilterType.TRIGGER_ONLY_INCREASING_EVENTS,
                    1.5 * JdkMath.PI, 30.5 * JdkMath.PI, JdkMath.PI, +1);

    }

    @Test
    public void testHistoryIncreasingBackward() {

        // start point: g > 0
        testHistory(FilterType.TRIGGER_ONLY_INCREASING_EVENTS,
                    0.5 * JdkMath.PI, -30.5 * JdkMath.PI, JdkMath.PI, -1);

        // start point: g = 0
        testHistory(FilterType.TRIGGER_ONLY_INCREASING_EVENTS,
                    0, -30.5 * JdkMath.PI, JdkMath.PI, +1);

        // start point: g < 0
        testHistory(FilterType.TRIGGER_ONLY_INCREASING_EVENTS,
                    1.5 * JdkMath.PI, -30.5 * JdkMath.PI, JdkMath.PI, -1);

    }

    @Test
    public void testHistoryDecreasingForward() {

        // start point: g > 0
        testHistory(FilterType.TRIGGER_ONLY_DECREASING_EVENTS,
                    0.5 * JdkMath.PI, 30.5 * JdkMath.PI, 0, +1);

        // start point: g = 0
        testHistory(FilterType.TRIGGER_ONLY_DECREASING_EVENTS,
                    0, 30.5 * JdkMath.PI, 0, +1);

        // start point: g < 0
        testHistory(FilterType.TRIGGER_ONLY_DECREASING_EVENTS,
                    1.5 * JdkMath.PI, 30.5 * JdkMath.PI, 0, +1);

    }

    @Test
    public void testHistoryDecreasingBackward() {

        // start point: g > 0
        testHistory(FilterType.TRIGGER_ONLY_DECREASING_EVENTS,
                    0.5 * JdkMath.PI, -30.5 * JdkMath.PI, 0, -1);

        // start point: g = 0
        testHistory(FilterType.TRIGGER_ONLY_DECREASING_EVENTS,
                    0, -30.5 * JdkMath.PI, 0, -1);

        // start point: g < 0
        testHistory(FilterType.TRIGGER_ONLY_DECREASING_EVENTS,
                    1.5 * JdkMath.PI, -30.5 * JdkMath.PI, 0, +1);

    }

    public void testHistory(FilterType type, double t0, double t1, double refSwitch, double signEven) {
        Event onlyIncreasing = new Event(false, true);
        EventFilter eventFilter =
                new EventFilter(onlyIncreasing, type);
        eventFilter.init(t0, new double[] {1.0,  0.0}, t1);

        // first pass to set up switches history for a long period
        double h = JdkMath.copySign(0.05, t1 - t0);
        double n = (int) JdkMath.floor((t1 - t0) / h);
        for (int i = 0; i < n; ++i) {
            double t = t0 + i * h;
            eventFilter.g(t, new double[] { JdkMath.sin(t), JdkMath.cos(t) });
        }

        // verify old events are preserved, even if randomly accessed
        UniformRandomProvider rng = RandomSource.TWO_CMRES.create(0xb0e7401265af8cd3L);
        for (int i = 0; i < 5000; i++) {
            double t = t0 + (t1 - t0) * rng.nextDouble();
            double g = eventFilter.g(t, new double[] { JdkMath.sin(t), JdkMath.cos(t) });
            int turn = (int) JdkMath.floor((t - refSwitch) / (2 * JdkMath.PI));
            if (turn % 2 == 0) {
                Assert.assertEquals( signEven * JdkMath.sin(t), g, 1.0e-10);
            } else {
                Assert.assertEquals(-signEven * JdkMath.sin(t), g, 1.0e-10);
            }
        }

    }

    @Test
    public void testIncreasingOnly()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double e = 1e-15;
        FirstOrderIntegrator integrator;
        integrator = new DormandPrince853Integrator(1.0e-3, 100.0, 1e-7, 1e-7);
        Event allEvents = new Event(true, true);
        integrator.addEventHandler(allEvents, 0.1, e, 1000,
                                   new BracketingNthOrderBrentSolver(1.0e-7, 5));
        Event onlyIncreasing = new Event(false, true);
        integrator.addEventHandler(new EventFilter(onlyIncreasing,
                                                   FilterType.TRIGGER_ONLY_INCREASING_EVENTS),
                                   0.1, e, 100,
                                   new BracketingNthOrderBrentSolver(1.0e-7, 5));
        double t0 = 0.5 * JdkMath.PI;
        double tEnd = 5.5 * JdkMath.PI;
        double[] y = { 0.0, 1.0 };
        Assert.assertEquals(tEnd,
                            integrator.integrate(new SineCosine(), t0, y, tEnd, y),
                            1.0e-7);

        Assert.assertEquals(5, allEvents.getEventCount());
        Assert.assertEquals(2, onlyIncreasing.getEventCount());

    }

    @Test
    public void testDecreasingOnly()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double e = 1e-15;
        FirstOrderIntegrator integrator;
        integrator = new DormandPrince853Integrator(1.0e-3, 100.0, 1e-7, 1e-7);
        Event allEvents = new Event(true, true);
        integrator.addEventHandler(allEvents, 0.1, e, 1000,
                                   new BracketingNthOrderBrentSolver(1.0e-7, 5));
        Event onlyDecreasing = new Event(true, false);
        integrator.addEventHandler(new EventFilter(onlyDecreasing,
                                                   FilterType.TRIGGER_ONLY_DECREASING_EVENTS),
                                   0.1, e, 1000,
                                   new BracketingNthOrderBrentSolver(1.0e-7, 5));
        double t0 = 0.5 * JdkMath.PI;
        double tEnd = 5.5 * JdkMath.PI;
        double[] y = { 0.0, 1.0 };
        Assert.assertEquals(tEnd,
                            integrator.integrate(new SineCosine(), t0, y, tEnd, y),
                            1.0e-7);

        Assert.assertEquals(5, allEvents.getEventCount());
        Assert.assertEquals(3, onlyDecreasing.getEventCount());

    }

    @Test
    public void testTwoOppositeFilters()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double e = 1e-15;
        FirstOrderIntegrator integrator;
        integrator = new DormandPrince853Integrator(1.0e-3, 100.0, 1e-7, 1e-7);
        Event allEvents = new Event(true, true);
        integrator.addEventHandler(allEvents, 0.1, e, 1000,
                                   new BracketingNthOrderBrentSolver(1.0e-7, 5));
        Event onlyIncreasing = new Event(false, true);
        integrator.addEventHandler(new EventFilter(onlyIncreasing,
                                                   FilterType.TRIGGER_ONLY_INCREASING_EVENTS),
                                   0.1, e, 1000,
                                   new BracketingNthOrderBrentSolver(1.0e-7, 5));
        Event onlyDecreasing = new Event(true, false);
        integrator.addEventHandler(new EventFilter(onlyDecreasing,
                                                   FilterType.TRIGGER_ONLY_DECREASING_EVENTS),
                                   0.1, e, 1000,
                                   new BracketingNthOrderBrentSolver(1.0e-7, 5));
        double t0 = 0.5 * JdkMath.PI;
        double tEnd = 5.5 * JdkMath.PI;
        double[] y = { 0.0, 1.0 };
        Assert.assertEquals(tEnd,
                            integrator.integrate(new SineCosine(), t0, y, tEnd, y),
                            1.0e-7);

        Assert.assertEquals(5, allEvents.getEventCount());
        Assert.assertEquals(2, onlyIncreasing.getEventCount());
        Assert.assertEquals(3, onlyDecreasing.getEventCount());

    }

    private static class SineCosine implements FirstOrderDifferentialEquations {
        @Override
        public int getDimension() {
            return 2;
        }

        @Override
        public void computeDerivatives(double t, double[] y, double[] yDot) {
            yDot[0] =  y[1];
            yDot[1] = -y[0];
        }
    }

    /** State events for this unit test. */
    protected static class Event implements EventHandler {

        private final boolean expectDecreasing;
        private final boolean expectIncreasing;
        private int eventCount;

        public Event(boolean expectDecreasing, boolean expectIncreasing) {
            this.expectDecreasing = expectDecreasing;
            this.expectIncreasing = expectIncreasing;
        }

        public int getEventCount() {
            return eventCount;
        }

        @Override
        public void init(double t0, double[] y0, double t) {
            eventCount = 0;
        }

        @Override
        public double g(double t, double[] y) {
            return y[0];
        }

        @Override
        public Action eventOccurred(double t, double[] y, boolean increasing) {
            if (increasing) {
                Assert.assertTrue(expectIncreasing);
            } else {
                Assert.assertTrue(expectDecreasing);
            }
            eventCount++;
            return Action.RESET_STATE;
        }

        @Override
        public void resetState(double t, double[] y) {
            // in fact, we don't really reset anything for this test
        }

    }
}
