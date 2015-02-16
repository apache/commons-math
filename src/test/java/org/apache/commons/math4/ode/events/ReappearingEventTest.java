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
package org.apache.commons.math3.ode.events;

import org.junit.Assert;

import java.util.Arrays;

import org.apache.commons.math3.analysis.solvers.PegasusSolver;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.GraggBulirschStoerIntegrator;
import org.junit.Test;

public class ReappearingEventTest {
    @Test
    public void testDormandPrince()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double tEnd = test(1);
        Assert.assertEquals(10.0, tEnd, 1e-7);
    }

    @Test
    public void testGragg()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double tEnd = test(2);
        Assert.assertEquals(10.0, tEnd, 1e-7);
    }

    public double test(int integratorType)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double e = 1e-15;
        FirstOrderIntegrator integrator;
        integrator = (integratorType == 1)
                     ? new DormandPrince853Integrator(e, 100.0, 1e-7, 1e-7)
                     : new GraggBulirschStoerIntegrator(e, 100.0, 1e-7, 1e-7);
        PegasusSolver rootSolver = new PegasusSolver(e, e);
        integrator.addEventHandler(new Event(), 0.1, e, 1000, rootSolver);
        double t0 = 6.0;
        double tEnd = 10.0;
        double[] y = {2.0, 2.0, 2.0, 4.0, 2.0, 7.0, 15.0};
        return integrator.integrate(new Ode(), t0, y, tEnd, y);
    }

    private static class Ode implements FirstOrderDifferentialEquations {
        public int getDimension() {
            return 7;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            Arrays.fill(yDot, 1.0);
        }
    }

    /** State events for this unit test. */
    protected static class Event implements EventHandler {

        public void init(double t0, double[] y0, double t) {
        }

        public double g(double t, double[] y) {
            return y[6] - 15.0;
        }

        public Action eventOccurred(double t, double[] y, boolean increasing) {
            return Action.STOP;
        }

        public void resetState(double t, double[] y) {
            // Never called.
        }
    }
}
