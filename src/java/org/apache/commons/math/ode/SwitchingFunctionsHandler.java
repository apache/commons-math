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

import org.apache.commons.math.ode.DerivativeException;

import java.util.ArrayList;
import java.util.Iterator;

/** This class handles several {@link SwitchingFunction switching
 * functions} during integration.
 *
 * @see SwitchingFunction
 *
 * @version $Id: SwitchingFunctionsHandler.java 1705 2006-09-17 19:57:39Z luc $
 *
 */

public class SwitchingFunctionsHandler {

  /** Simple constructor.
   * Create an empty handler
   */
  public SwitchingFunctionsHandler() {
    functions   = new ArrayList();
    first       = null;
    initialized = false;
  }

  /** Add a switching function.
   * @param function switching function
   * @param maxCheckInterval maximal time interval between switching
   * function checks (this interval prevents missing sign changes in
   * case the integration steps becomes very large)
   * @param convergence convergence threshold in the event time search
   */
  public void add(SwitchingFunction function,
                  double maxCheckInterval, double convergence) {
    functions.add(new SwitchState(function, maxCheckInterval, convergence));
  }

  /** Check if the handler does not have any condition.
   * @return true if handler is empty
   */
  public boolean isEmpty() {
    return functions.isEmpty();
  }

  /** Evaluate the impact of the proposed step on all handled
   * switching functions.
   * @param interpolator step interpolator for the proposed step
   * @return true if at least one switching function triggers an event
   * before the end of the proposed step (this implies the step should
   * be rejected)
   */
  public boolean evaluateStep(StepInterpolator interpolator) {

    try {

      first = null;
      if (functions.isEmpty()) {
        // there is nothing to do, return now to avoid setting the
        // interpolator time (and hence avoid unneeded calls to the
        // user function due to interpolator finalization)
        return false;
      }

      if (! initialized) {

        // initialize the switching functions
        double t0 = interpolator.getPreviousTime();
        interpolator.setInterpolatedTime(t0);
        double [] y = interpolator.getInterpolatedState();
        for (Iterator iter = functions.iterator(); iter.hasNext();) {
          ((SwitchState) iter.next()).reinitializeBegin(t0, y);
        }

        initialized = true;

      }

      // check events occurrence
      for (Iterator iter = functions.iterator(); iter.hasNext();) {

        SwitchState state = (SwitchState) iter.next();
        if (state.evaluateStep(interpolator)) {
          if (first == null) {
            first = state;
          } else {
            if (interpolator.isForward()) {
              if (state.getEventTime() < first.getEventTime()) {
                first = state;
              }
            } else {
              if (state.getEventTime() > first.getEventTime()) {
                first = state;
              }
            }
          }
        }

      }

      return first != null;

    } catch (DerivativeException e) {
      throw new RuntimeException("unexpected exception: " + e.getMessage());
    }

  }

  /** Get the occurrence time of the first event triggered in the
   * last evaluated step.
   * @return occurrence time of the first event triggered in the last
   * evaluated step, or </code>Double.NaN</code> if no event is
   * triggered
   */
  public double getEventTime() {
    return (first == null) ? Double.NaN : first.getEventTime();
  }

  /** Inform the switching functions that the step has been accepted
   * by the integrator.
   * @param t value of the independant <i>time</i> variable at the
   * end of the step
   * @param y array containing the current value of the state vector
   * at the end of the step
   */
  public void stepAccepted(double t, double[] y) {
    for (Iterator iter = functions.iterator(); iter.hasNext();) {
      ((SwitchState) iter.next()).stepAccepted(t, y);
    }
  }

  /** Check if the integration should be stopped at the end of the
   * current step.
   * @return true if the integration should be stopped
   */
  public boolean stop() {
    for (Iterator iter = functions.iterator(); iter.hasNext();) {
      if (((SwitchState) iter.next()).stop()) {
        return true;
      }
    }
    return false;
  }

  /** Let the switching functions reset the state if they want.
   * @param t value of the independant <i>time</i> variable at the
   * beginning of the next step
   * @param y array were to put the desired state vector at the beginning
   * of the next step
   * @return true if the integrator should reset the derivatives too
   */
  public boolean reset(double t, double[] y) {
    boolean resetDerivatives = false;
    for (Iterator iter = functions.iterator(); iter.hasNext();) {
      if (((SwitchState) iter.next()).reset(t, y)) {
        resetDerivatives = true;
      }
    }
    return resetDerivatives;
  }

  /** Switching functions. */
  private ArrayList functions;

  /** First active switching function. */
  private SwitchState first;

  /** Initialization indicator. */
  private boolean initialized;

}
