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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.legacy.analysis.solvers.BaseSecantSolver;
import org.apache.commons.math4.legacy.analysis.solvers.PegasusSolver;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NoBracketingException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math4.legacy.ode.FirstOrderIntegrator;
import org.apache.commons.math4.legacy.ode.nonstiff.DormandPrince853Integrator;
import org.junit.Assert;
import org.junit.Test;

/** Tests for overlapping state events. Also tests an event function that does
 * not converge to zero, but does have values of opposite sign around its root.
 */
public class OverlappingEventsTest implements FirstOrderDifferentialEquations {

    /** Expected event times for first event. */
    private static final double[] EVENT_TIMES1 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0,
                                                  7.0, 8.0, 9.0};

    /** Expected event times for second event. */
    private static final double[] EVENT_TIMES2 = {0.5, 1.0, 1.5, 2.0, 2.5, 3.0,
                                                  3.5, 4.0, 4.5, 5.0, 5.5, 6.0,
                                                  6.5, 7.0, 7.5, 8.0, 8.5, 9.0,
                                                  9.5};

    /** Test for events that occur at the exact same time, but due to numerical
     * calculations occur very close together instead. Uses event type 0. See
     * {@link org.apache.commons.math4.legacy.ode.events.EventHandler#g(double, double[])
     * EventHandler.g(double, double[])}.
     */
    @Test
    public void testOverlappingEvents0()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        test(0);
    }

    /** Test for events that occur at the exact same time, but due to numerical
     * calculations occur very close together instead. Uses event type 1. See
     * {@link org.apache.commons.math4.legacy.ode.events.EventHandler#g(double, double[])
     * EventHandler.g(double, double[])}.
     */
    @Test
    public void testOverlappingEvents1()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        test(1);
    }

    /** Test for events that occur at the exact same time, but due to numerical
     * calculations occur very close together instead.
     * @param eventType the type of events to use. See
     * {@link org.apache.commons.math4.legacy.ode.events.EventHandler#g(double, double[])
     * EventHandler.g(double, double[])}.
     */
    public void test(int eventType)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double e = 1e-15;
        FirstOrderIntegrator integrator = new DormandPrince853Integrator(e, 100.0, 1e-7, 1e-7);
        BaseSecantSolver rootSolver = new PegasusSolver(e, e);
        EventHandler evt1 = new Event(0, eventType);
        EventHandler evt2 = new Event(1, eventType);
        integrator.addEventHandler(evt1, 0.1, e, 999, rootSolver);
        integrator.addEventHandler(evt2, 0.1, e, 999, rootSolver);
        double t = 0.0;
        double tEnd = 10.0;
        double[] y = {0.0, 0.0};
        List<Double> events1 = new ArrayList<>();
        List<Double> events2 = new ArrayList<>();
        while (t < tEnd) {
            t = integrator.integrate(this, t, y, tEnd, y);
            //System.out.println("t=" + t + ",\t\ty=[" + y[0] + "," + y[1] + "]");

            if (y[0] >= 1.0) {
                y[0] = 0.0;
                events1.add(t);
                //System.out.println("Event 1 @ t=" + t);
            }
            if (y[1] >= 1.0) {
                y[1] = 0.0;
                events2.add(t);
                //System.out.println("Event 2 @ t=" + t);
            }
        }
        Assert.assertEquals(EVENT_TIMES1.length, events1.size());
        Assert.assertEquals(EVENT_TIMES2.length, events2.size());
        for(int i = 0; i < EVENT_TIMES1.length; i++) {
            Assert.assertEquals(EVENT_TIMES1[i], events1.get(i), 1e-7);
        }
        for(int i = 0; i < EVENT_TIMES2.length; i++) {
            Assert.assertEquals(EVENT_TIMES2[i], events2.get(i), 1e-7);
        }
        //System.out.println();
    }

    /** {@inheritDoc} */
    @Override
    public int getDimension() {
        return 2;
    }

    /** {@inheritDoc} */
    @Override
    public void computeDerivatives(double t, double[] y, double[] yDot) {
        yDot[0] = 1.0;
        yDot[1] = 2.0;
    }

    /** State events for this unit test. */
    private class Event implements EventHandler {
        /** The index of the continuous variable to use. */
        private final int idx;

        /** The event type to use. See {@link #g}. */
        private final int eventType;

        /** Constructor for the {@link Event} class.
         * @param idx the index of the continuous variable to use
         * @param eventType the type of event to use. See {@link #g}
         */
        Event(int idx, int eventType) {
            this.idx = idx;
            this.eventType = eventType;
        }

        /** {@inheritDoc} */
        @Override
        public void init(double t0, double[] y0, double t) {
        }

        /** {@inheritDoc} */
        @Override
        public double g(double t, double[] y) {
            return (eventType == 0) ? y[idx] >= 1.0 ? 1.0 : -1.0
                                    : y[idx] - 1.0;
        }

        /** {@inheritDoc} */
        @Override
        public Action eventOccurred(double t, double[] y, boolean increasing) {
            return Action.STOP;
        }

        /** {@inheritDoc} */
        @Override
        public void resetState(double t, double[] y) {
            // Never called.
        }
    }
}
