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

import org.apache.commons.math4.legacy.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math4.legacy.ode.FirstOrderIntegrator;
import org.apache.commons.math4.legacy.ode.nonstiff.DormandPrince853Integrator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Check events are detected correctly when the event times are close.
 *
 * @author Evan Ward
 */
public class CloseEventsTest {

    @Test
    public void testCloseEvents() {
        // setup
        double e = 1e-15;
        FirstOrderIntegrator integrator =
                new DormandPrince853Integrator(e, 100.0, 1e-7, 1e-7);

        TimeDetector detector1 = new TimeDetector(5);
        integrator.addEventHandler(detector1, 10, 1, 100);
        TimeDetector detector2 = new TimeDetector(5.5);
        integrator.addEventHandler(detector2, 10, 1, 100);

        // action
        integrator.integrate(new Equation(), 0, new double[2], 20, new double[2]);

        // verify
        Assert.assertEquals(5, detector1.getActualT(), 0.0);
        Assert.assertEquals(5.5, detector2.getActualT(), 0.0);
    }

    @Test
    public void testSimultaneousEvents() {
        // setup
        double e = 1e-15;
        FirstOrderIntegrator integrator =
                new DormandPrince853Integrator(e, 100.0, 1e-7, 1e-7);

        TimeDetector detector1 = new TimeDetector(5);
        integrator.addEventHandler(detector1, 10, 1, 100);
        TimeDetector detector2 = new TimeDetector(5);
        integrator.addEventHandler(detector2, 10, 1, 100);

        // action
        integrator.integrate(new Equation(), 0, new double[2], 20, new double[2]);

        // verify
        Assert.assertEquals(5, detector1.getActualT(), 0.0);
        Assert.assertEquals(5, detector2.getActualT(), 0.0);
    }


    /** Trigger an event at a particular time. */
    private static class TimeDetector implements EventHandler {

        /** time of the event to trigger. */
        private final double eventT;

        /** time the event was actually triggered. */
        private double actualT;

        /**
         * Create a new detector.
         *
         * @param eventT the time to trigger an event.
         */
        TimeDetector(double eventT) {
            this.eventT = eventT;
        }

        /** Get the actual time the event occurred. */
        public double getActualT() {
            return actualT;
        }

        @Override
        public void init(double t0, double[] y0, double t) {
        }

        @Override
        public double g(double t, double[] y) {
            return t - eventT;
        }

        @Override
        public Action eventOccurred(double t, double[] y, boolean increasing) {
            this.actualT = t;
            return Action.CONTINUE;
        }

        @Override
        public void resetState(double t, double[] y) {
        }
    }

    /** Some basic equations to integrate. */
    public static class Equation implements FirstOrderDifferentialEquations {

        @Override
        public int getDimension() {
            return 2;
        }

        @Override
        public void computeDerivatives(double t, double[] y, double[] yDot) {
            yDot[0] = 1.0;
            yDot[1] = 2.0;
        }
    }
}
