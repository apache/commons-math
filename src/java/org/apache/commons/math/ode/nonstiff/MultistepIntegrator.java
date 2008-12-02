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

import java.util.Arrays;

import org.apache.commons.math.ode.AbstractIntegrator;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.ODEIntegrator;
import org.apache.commons.math.ode.events.CombinedEventsManager;
import org.apache.commons.math.ode.events.EventException;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.events.EventState;
import org.apache.commons.math.ode.sampling.FixedStepHandler;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.ode.sampling.StepNormalizer;

/**
 * This class is the base class for multistep integrators for Ordinary
 * Differential Equations.
 *
 * @see AdamsBashforthIntegrator
 * @see AdamsMoultonIntegrator
 * @see BDFIntegrator
 * @version $Revision$ $Date$
 * @since 2.0
 */
public abstract class MultistepIntegrator extends AbstractIntegrator {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -1705864253238417163L;

    /** Starter integrator. */
    private FirstOrderIntegrator starter;

    /** Previous steps times. */
    protected double[] previousT;

    /** Previous steps derivatives. */
    protected double[][] previousF;

    /** Time of last detected reset. */
    private double resetTime;

    /** Prototype of the step interpolator. */
    protected MultistepStepInterpolator prototype;
                                           
    /**
     * Build a multistep integrator with the given number of steps.
     * <p>The default starter integrator is set to the {@link
     * DormandPrince853Integrator Dormand-Prince 8(5,3)} integrator with
     * some defaults settings.</p>
     * @param name name of the method
     * @param k number of steps of the multistep method
     * (including the one being computed)
     * @param prototype prototype of the step interpolator to use
     */
    protected MultistepIntegrator(final String name, final int k,
                                  final MultistepStepInterpolator prototype) {
        super(name);
        starter = new DormandPrince853Integrator(1.0e-6, 1.0e6, 1.0e-5, 1.0e-6);
        previousT = new double[k];
        previousF = new double[k][];
        this.prototype = prototype;
    }

    /**
     * Get the starter integrator.
     * @return starter integrator
     */
    public ODEIntegrator getStarterIntegrator() {
        return starter;
    }

    /**
     * Set the starter integrator.
     * <p>The various step and event handlers for this starter integrator
     * will be managed automatically by the multi-step integrator. Any
     * user configuration for these elements will be cleared before use.</p>
     * @param starter starter integrator
     */
    public void setStarterIntegrator(FirstOrderIntegrator starter) {
        this.starter = starter;
    }

    /** Start the integration.
     * <p>This method computes the first few steps of the multistep method,
     * using the underlying starter integrator, ensuring the returned steps
     * all belong to the same smooth range.</p>
     * <p>In order to ensure smoothness, the start phase is automatically
     * restarted when a state or derivative reset is triggered by the
     * registered events handlers before this start phase is completed. As
     * an example, consider integrating a differential equation from t=0
     * to t=100 with a 4 steps method and step size equal to 0.2. If an event
     * resets the state at t=0.5, the start phase will not end at t=0.7 with
     * steps at [0.0, 0.2, 0.4, 0.6] but instead will end at t=1.1 with steps
     * at [0.5, 0.7, 0.9, 1.1].</p>
     * <p>A side effect of the need for smoothness is that an ODE triggering
     * short period regular resets will remain in the start phase throughout
     * the integration range if the step size or the number of steps to store
     * are too large.</p>
     * <p>If the start phase ends prematurely (because of some triggered event
     * for example), then the time of latest previous steps will be set to
     * <code>Double.NaN</code>.</p>
     * @param n number of steps to store
     * @param h signed step size to use for the first steps
     * @param manager discrete events manager to use
     * @param equations differential equations to integrate
     * @param t0 initial time
     * @param y state vector: contains the initial value of the state vector at t0,
     * will be used to put the state vector at each successful step and hence
     * contains the final value at the end of the start phase
     * @return time of the end of the start phase
     * @throws IntegratorException if the integrator cannot perform integration
     * @throws DerivativeException this exception is propagated to the caller if
     * the underlying user function triggers one
     */
    protected double start(final int n, final double h,
                           final CombinedEventsManager manager,
                           final FirstOrderDifferentialEquations equations,
                           final double t0, final double[] y)
        throws DerivativeException, IntegratorException {

        // clear the first steps
        Arrays.fill(previousT, Double.NaN);
        Arrays.fill(previousF, null);

        // configure the event handlers
        starter.clearEventHandlers();
        for (EventState state : manager.getEventsStates()) {
            starter.addEventHandler(new ResetCheckingWrapper(state.getEventHandler()),
                                    state.getMaxCheckInterval(),
                                    state.getConvergence(), state.getMaxIterationCount());
        }

        // configure the step handlers
        starter.clearStepHandlers();
        for (final StepHandler handler : stepHandlers) {
            // add the user defined step handlers, filtering out the isLast indicator
            starter.addStepHandler(new FilteringWrapper(handler));
        }

        // add one specific step handler to store the first steps
        final StoringStepHandler store = new StoringStepHandler(n);
        starter.addStepHandler(new StepNormalizer(h, store));

        // integrate over the first few steps, ensuring no intermediate reset occurs
        double t = t0;
        double stopTime = Double.NaN;
        do {
            resetTime = Double.NaN;
            store.restart();
            // we overshoot by 1/10000 step the end to make sure we get don't miss the last point
            stopTime = starter.integrate(equations, t, y, t + (n - 0.9999) * h, y);
            if (!Double.isNaN(resetTime)) {
                // there was an intermediate reset, we restart
                t = resetTime;
            }
        } while (!Double.isNaN(resetTime));

        // clear configuration
        starter.clearEventHandlers();
        starter.clearStepHandlers();

        if (store.getFinalState() != null) {
            System.arraycopy(store.getFinalState(), 0, y, 0, y.length);
        }
        return stopTime;

    }

    /** Rotate the previous steps arrays.
     */
    protected void rotatePreviousSteps() {
        final double[] rolled = previousF[previousT.length - 1];
        for (int k = previousF.length - 1; k > 0; --k) {
            previousT[k] = previousT[k - 1];
            previousF[k] = previousF[k - 1];
        }
        previousF[0] = rolled;
    }

    /** Event handler wrapper to check if state or derivatives have been reset. */
    private class ResetCheckingWrapper implements EventHandler {

        /** Serializable version identifier. */
        private static final long serialVersionUID = 4922660285376467937L;

        /** Wrapped event handler. */
        private final EventHandler handler;

        /** Build a new instance.
         * @param handler event handler to wrap
         */
        public ResetCheckingWrapper(final EventHandler handler) {
            this.handler = handler;
        }

        /** {@inheritDoc} */
        public int eventOccurred(double t, double[] y) throws EventException {
            final int action = handler.eventOccurred(t, y);
            if ((action == RESET_DERIVATIVES) || (action == RESET_STATE)) {
                // a singularity has been encountered
                // we need to restart the start phase
                resetTime = t;
                return STOP;
            }
            return action;
        }

        /** {@inheritDoc} */
        public double g(double t, double[] y) throws EventException {
            return handler.g(t, y);
        }

        /** {@inheritDoc} */
        public void resetState(double t, double[] y) throws EventException {
            handler.resetState(t, y);
        }
        
    }

    /** Step handler wrapper filtering out the isLast indicator. */
    private class FilteringWrapper implements StepHandler {

        /** Serializable version identifier. */
        private static final long serialVersionUID = 4607975253344802232L;

        /** Wrapped step handler. */
        private final StepHandler handler;

        /** Build a new instance.
         * @param handler step handler to wrap
         */
        public FilteringWrapper(final StepHandler handler) {
            this.handler = handler;
        }

        /** {@inheritDoc} */
        public void handleStep(StepInterpolator interpolator, boolean isLast)
                throws DerivativeException {
            // we force the isLast indicator to false EXCEPT if some event handler triggered a stop
            handler.handleStep(interpolator, eventsHandlersManager.stop());
        }

        /** {@inheritDoc} */
        public boolean requiresDenseOutput() {
            return handler.requiresDenseOutput();
        }

        /** {@inheritDoc} */
        public void reset() {
            handler.reset();
        }
        
    }

    /** Specialized step handler storing the first few steps. */
    private class StoringStepHandler implements FixedStepHandler {

        /** Serializable version identifier. */
        private static final long serialVersionUID = 4592974435520688797L;

        /** Number of steps to store. */
        private final int n;

        /** Counter for already stored steps. */
        private int count;

        /** Final state. */
        private double[] finalState;

        /** Build a new instance.
         * @param n number of steps to store
         */
        public StoringStepHandler(final int n) {
            this.n = n;
            restart();
        }

        /** Restart storage.
         */
        public void restart() {
            count = 0;
            finalState = null;
        }

        /** Get the final state.
         * @return final state
         */
        public double[] getFinalState() {
            return finalState;
        }

        /** {@inheritDoc} */
        public void handleStep(final double t, final double[] y, final double[] yDot,
                               final boolean isLast) {
            if (count++ < n) {
                previousT[n - count] = t;
                previousF[n - count] = yDot.clone();
                if (count == n) {
                    finalState = y.clone();
                }
            }
        }

    }

}
