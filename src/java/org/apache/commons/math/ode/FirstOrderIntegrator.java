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

/** This interface represents a first order integrator for
 * differential equations.

 * <p>The classes which are devoted to solve first order differential
 * equations should implement this interface. The problems which can
 * be handled should implement the {@link
 * FirstOrderDifferentialEquations} interface.</p>

 * @see FirstOrderDifferentialEquations
 * @see StepHandler
 * @see SwitchingFunction

 * @version $Id: FirstOrderIntegrator.java 1705 2006-09-17 19:57:39Z luc $

 */

public interface FirstOrderIntegrator {

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

  /** Add a switching function to the integrator.
   * @param function switching function
   * @param maxCheckInterval maximal time interval between switching
   * function checks (this interval prevents missing sign changes in
   * case the integration steps becomes very large)
   * @param convergence convergence threshold in the event time search
   */
  public void addSwitchingFunction(SwitchingFunction function,
                                   double maxCheckInterval,
                                   double convergence);

  /** Integrate the differential equations up to the given time
   * @param equations differential equations to integrate
   * @param t0 initial time
   * @param y0 initial value of the state vector at t0
   * @param t target time for the integration
   * (can be set to a value smaller thant <code>t0</code> for backward integration)
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

}
