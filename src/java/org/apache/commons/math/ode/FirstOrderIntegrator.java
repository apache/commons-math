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

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.sampling.StepHandler;

/** This interface represents a first order integrator for
 * differential equations.

 * <p>The classes which are devoted to solve first order differential
 * equations should implement this interface. The problems which can
 * be handled should implement the {@link
 * FirstOrderDifferentialEquations} interface.</p>
 *
 * @see FirstOrderDifferentialEquations
 * @see StepHandler
 * @see EventHandler
 * @version $Revision$ $Date$
 * @since 1.2
 */

public interface FirstOrderIntegrator extends Serializable {

  /** Get the name of the method.
   * @return name of the method
   */
  public String getName();

  /** Set the step handler for this integrator.
   * The handler will be called by the integrator for each accepted
   * step.
   * @param handler handler for the accepted steps
   */
  public void setStepHandler (StepHandler handler);

  /** Get the step handler for this integrator.
   * @return the step handler for this integrator
   */
  public StepHandler getStepHandler();

  /** Add an event handler to the integrator.
   * @param handler event handler
   * @param maxCheckInterval maximal time interval between switching
   * function checks (this interval prevents missing sign changes in
   * case the integration steps becomes very large)
   * @param convergence convergence threshold in the event time search
   * @param maxIterationCount upper limit of the iteration count in
   * the event time search
   * @see #getEventsHandlers()
   * @see #clearEventsHandlers()
   */
  public void addEventHandler(EventHandler handler, double maxCheckInterval,
                              double convergence, int maxIterationCount);

  /** Get all the events handlers that have been added to the integrator.
   * @return an unmodifiable collection of the added events handlers
   * @see #addEventHandler(EventHandler, double, double, int)
   * @see #clearEventsHandlers()
   */
  public Collection<EventHandler> getEventsHandlers();

  /** Remove all the events handlers that have been added to the integrator.
   * @see #addEventHandler(EventHandler, double, double, int)
   * @see #getEventsHandlers()
   */
  public void clearEventsHandlers();

  /** Integrate the differential equations up to the given time.
   * <p>This method solves an Initial Value Problem (IVP).</p>
   * <p>Since this method stores some internal state variables made
   * available in its public interface during integration ({@link
   * #getCurrentSignedStepsize()}), it is <em>not</em> thread-safe.</p>
   * @param equations differential equations to integrate
   * @param t0 initial time
   * @param y0 initial value of the state vector at t0
   * @param t target time for the integration
   * (can be set to a value smaller than <code>t0</code> for backward integration)
   * @param y placeholder where to put the state vector at each successful
   *  step (and hence at the end of integration), can be the same object as y0
   * @throws IntegratorException if the integrator cannot perform integration
   * @throws DerivativeException this exception is propagated to the caller if
   * the underlying user function triggers one
   */
  public void integrate (FirstOrderDifferentialEquations equations,
                         double t0, double[] y0,
                         double t, double[] y)
    throws DerivativeException, IntegratorException;

  /** Get the current value of the step start time t<sub>i</sub>.
   * <p>This method can be called during integration (typically by
   * the object implementing the {@link FirstOrderDifferentialEquations
   * differential equations} problem) if the value of the current step that
   * is attempted is needed.</p>
   * <p>The result is undefined if the method is called outside of
   * calls to {@link #integrate}</p>
   * @return current value of the step start time t<sub>i</sub>
   */
  public double getCurrentStepStart();

  /** Get the current signed value of the integration stepsize.
   * <p>This method can be called during integration (typically by
   * the object implementing the {@link FirstOrderDifferentialEquations
   * differential equations} problem) if the signed value of the current stepsize
   * that is tried is needed.</p>
   * <p>The result is undefined if the method is called outside of
   * calls to {@link #integrate}</p>
   * @return current signed value of the stepsize
   */
  public double getCurrentSignedStepsize();

}
