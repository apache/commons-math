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

package org.apache.commons.math.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.ode.events.CombinedEventsManager;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.events.EventState;
import org.apache.commons.math.ode.sampling.StepHandler;

/**
 * Base class managing common boilerplate for all integrators.
 * @version $Revision$ $Date$
 * @since 2.0
 */
public abstract class AbstractIntegrator implements FirstOrderIntegrator {

    /** Step handler. */
    protected Collection<StepHandler> stepHandlers;

    /** Current step start time. */
    protected double stepStart;

    /** Current stepsize. */
    protected double stepSize;

    /** Events handlers manager. */
    protected CombinedEventsManager eventsHandlersManager;

    /** Name of the method. */
    private final String name;

    /** Maximal number of evaluations allowed. */
    private int maxEvaluations;

    /** Number of evaluations already performed. */
    private int evaluations;

    /** Differential equations to integrate. */
    private transient FirstOrderDifferentialEquations equations;

    /** Build an instance.
     * @param name name of the method
     */
    public AbstractIntegrator(final String name) {
        this.name = name;
        stepHandlers = new ArrayList<StepHandler>();
        stepStart = Double.NaN;
        stepSize  = Double.NaN;
        eventsHandlersManager = new CombinedEventsManager();
        setMaxEvaluations(-1);
        resetEvaluations();
    }

    /** Build an instance with a null name.
     */
    protected AbstractIntegrator() {
        this(null);
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void addStepHandler(final StepHandler handler) {
        stepHandlers.add(handler);
    }

    /** {@inheritDoc} */
    public Collection<StepHandler> getStepHandlers() {
        return Collections.unmodifiableCollection(stepHandlers);
    }

    /** {@inheritDoc} */
    public void clearStepHandlers() {
        stepHandlers.clear();
    }

    /** {@inheritDoc} */
    public void addEventHandler(final EventHandler function,
                                final double maxCheckInterval,
                                final double convergence,
                                final int maxIterationCount) {
        eventsHandlersManager.addEventHandler(function, maxCheckInterval,
                                              convergence, maxIterationCount);
    }

    /** {@inheritDoc} */
    public Collection<EventHandler> getEventHandlers() {
        return eventsHandlersManager.getEventsHandlers();
    }

    /** {@inheritDoc} */
    public void clearEventHandlers() {
        eventsHandlersManager.clearEventsHandlers();
    }

    /** Check if one of the step handlers requires dense output.
     * @return true if one of the step handlers requires dense output
     */
    protected boolean requiresDenseOutput() {
        for (StepHandler handler : stepHandlers) {
            if (handler.requiresDenseOutput()) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public double getCurrentStepStart() {
        return stepStart;
    }

    /** {@inheritDoc} */
    public double getCurrentSignedStepsize() {
        return stepSize;
    }

    /** {@inheritDoc} */
    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = (maxEvaluations < 0) ? Integer.MAX_VALUE : maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getEvaluations() {
        return evaluations;
    }

    /** Reset the number of evaluations to zero.
     */
    protected void resetEvaluations() {
        evaluations = 0;
    }

    /** Set the differential equations.
     * @param equations differential equations to integrate
     * @see #computeDerivatives(double, double[], double[])
     */
    protected void setEquations(final FirstOrderDifferentialEquations equations) {
        this.equations = equations;
    }

    /** Compute the derivatives and check the number of evaluations.
     * @param t current value of the independent <I>time</I> variable
     * @param y array containing the current value of the state vector
     * @param yDot placeholder array where to put the time derivative of the state vector
     * @throws DerivativeException this exception is propagated to the caller if the
     * underlying user function triggers one
     */
    public void computeDerivatives(final double t, final double[] y, final double[] yDot)
        throws DerivativeException {
        if (++evaluations > maxEvaluations) {
            throw new DerivativeException(new MaxEvaluationsExceededException(maxEvaluations));
        }
        equations.computeDerivatives(t, y, yDot);
    }

    /** Perform some sanity checks on the integration parameters.
     * @param ode differential equations set
     * @param t0 start time
     * @param y0 state vector at t0
     * @param t target time for the integration
     * @param y placeholder where to put the state vector
     * @exception IntegratorException if some inconsistency is detected
     */
    protected void sanityChecks(final FirstOrderDifferentialEquations ode,
                                final double t0, final double[] y0,
                                final double t, final double[] y)
        throws IntegratorException {

        if (ode.getDimension() != y0.length) {
            throw new IntegratorException(
                    "dimensions mismatch: ODE problem has dimension {0}," +
                    " initial state vector has dimension {1}",
                    ode.getDimension(), y0.length);
        }

        if (ode.getDimension() != y.length) {
            throw new IntegratorException(
                    "dimensions mismatch: ODE problem has dimension {0}," +
                    " final state vector has dimension {1}",
                    ode.getDimension(), y.length);
        }

        if (Math.abs(t - t0) <= 1.0e-12 * Math.max(Math.abs(t0), Math.abs(t))) {
            throw new IntegratorException(
                    "too small integration interval: length = {0}",
                    Math.abs(t - t0));
        }

    }

    /** Add an event handler for end time checking.
     * <p>This method can be used to simplify handling of integration end time.
     * It leverages the nominal stop condition with the exceptional stop
     * conditions.</p>
     * @param startTime integration start time
     * @param endTime desired end time
     * @param manager manager containing the user-defined handlers
     * @return a new manager containing all the user-defined handlers plus a
     * dedicated manager triggering a stop event at entTime
     */
    protected CombinedEventsManager addEndTimeChecker(final double startTime,
                                                      final double endTime,
                                                      final CombinedEventsManager manager) {
        CombinedEventsManager newManager = new CombinedEventsManager();
        for (final EventState state : manager.getEventsStates()) {
            newManager.addEventHandler(state.getEventHandler(),
                                       state.getMaxCheckInterval(),
                                       state.getConvergence(),
                                       state.getMaxIterationCount());
        }
        newManager.addEventHandler(new EndTimeChecker(endTime),
                                   Double.POSITIVE_INFINITY,
                                   Math.ulp(Math.max(Math.abs(startTime), Math.abs(endTime))),
                                   100);
        return newManager;
    }

    /** Specialized event handler to stop integration. */
    private static class EndTimeChecker implements EventHandler {

        /** Desired end time. */
        private final double endTime;

        /** Build an instance.
         * @param endTime desired time
         */
        public EndTimeChecker(final double endTime) {
            this.endTime = endTime;
        }

        /** {@inheritDoc} */
        public int eventOccurred(double t, double[] y, boolean increasing) {
            return STOP;
        }

        /** {@inheritDoc} */
        public double g(double t, double[] y) {
            return t - endTime;
        }

        /** {@inheritDoc} */
        public void resetState(double t, double[] y) {
        }

    }

}
