// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.ode;

import org.spaceroots.mantissa.functions.FunctionException;
import org.spaceroots.mantissa.functions.scalar.ComputableFunction;

import org.spaceroots.mantissa.roots.ConvergenceChecker;
import org.spaceroots.mantissa.roots.RootsFinder;
import org.spaceroots.mantissa.roots.BrentSolver;

/** This class handles the state for one {@link SwitchingFunction
 * switching function} during integration steps.
 *
 * <p>Each time the integrator proposes a step, the switching function
 * should be checked. This class handles the state of one function
 * during one integration step, with references to the state at the
 * end of the preceding step. This information is used to determine if
 * the function should trigger an event or not during the proposed
 * step (and hence the step should be reduced to ensure the event
 * occurs at a bound rather than inside the step).</p>
 *
 * @version $Id: SwitchState.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe
 *
 */
class SwitchState
  implements ComputableFunction, ConvergenceChecker {

  private static final long serialVersionUID = 6944466361876662425L;

  /** Switching function. */
  private SwitchingFunction function;

  /** Maximal time interval between switching function checks. */
  private double maxCheckInterval;

  /** Convergence threshold for event localisation. */
  private double convergence;

  /** Time at the beginning of the step. */
  private double t0;

  /** Value of the switching function at the beginning of the step. */
  private double g0;

  /** Simulated sign of g0 (we cheat when crossing events). */
  private boolean g0Positive;

  /** Indicator of event expected during the step. */
  private boolean pendingEvent;

  /** Occurrence time of the pending event. */
  private double pendingEventTime;

  /** Occurrence time of the previous event. */
  private double previousEventTime;

  /** Variation direction around pending event.
   *  (this is considered with respect to the integration direction)
   */
  private boolean increasing;

  /** Next action indicator. */
  private int nextAction;

  /** Interpolator valid for the current step. */
  private StepInterpolator interpolator;

  /** Simple constructor.
   * @param function switching function
   * @param maxCheckInterval maximal time interval between switching
   * function checks (this interval prevents missing sign changes in
   * case the integration steps becomes very large)
   * @param convergence convergence threshold in the event time search
   */
  public SwitchState(SwitchingFunction function,
                     double maxCheckInterval, double convergence) {
    this.function         = function;
    this.maxCheckInterval = maxCheckInterval;
    this.convergence      = Math.abs(convergence);

    // some dummy values ...
    t0                = Double.NaN;
    g0                = Double.NaN;
    g0Positive        = true;
    pendingEvent      = false;
    pendingEventTime  = Double.NaN;
    previousEventTime = Double.NaN;
    increasing        = true;
    nextAction        = SwitchingFunction.CONTINUE;

    interpolator      = null;

  }

  /** Reinitialize the beginning of the step.
   * @param t0 value of the independant <i>time</i> variable at the
   * beginning of the step
   * @param y0 array containing the current value of the state vector
   * at the beginning of the step
   */
  public void reinitializeBegin(double t0, double[] y0) {
    this.t0 = t0;
    g0 = function.g(t0, y0);
    g0Positive = (g0 >= 0);
  }

  /** Evaluate the impact of the proposed step on the switching function.
   * @param interpolator step interpolator for the proposed step
   * @return true if the switching function triggers an event before
   * the end of the proposed step (this implies the step should be
   * rejected)
   */
  public boolean evaluateStep(StepInterpolator interpolator) {

    try {

      this.interpolator = interpolator;

      double t1 = interpolator.getCurrentTime();
      int    n  = Math.max(1, (int) Math.ceil(Math.abs(t1 - t0) / maxCheckInterval));
      double h  = (t1 - t0) / n;

      double ta = t0;
      double ga = g0;
      double tb = t0 + ((t1 > t0) ? convergence : -convergence);
      for (int i = 0; i < n; ++i) {

        // evaluate function value at the end of the substep
        tb += h;
        interpolator.setInterpolatedTime(tb);
        double gb = function.g(tb, interpolator.getInterpolatedState());

        // check events occurrence
        if (g0Positive ^ (gb >= 0)) {
          // there is a sign change: an event is expected during this step

          // variation direction, with respect to the integration direction
          increasing = (gb >= ga);

          RootsFinder solver = new BrentSolver();
          if (solver.findRoot(this, this, 1000, ta, ga, tb, gb)) {
            if (Double.isNaN(previousEventTime)
                || (Math.abs(previousEventTime - solver.getRoot()) > convergence)) {
              pendingEventTime = solver.getRoot();
              if (pendingEvent
                  && (Math.abs(t1 - pendingEventTime) <= convergence)) {
                // we were already waiting for this event which was
                // found during a previous call for a step that was
                // rejected, this step must now be accepted since it
                // properly ends exactly at the event occurrence
                return false;
              }
              // either we were not waiting for the event or it has
              // moved in such a way the step cannot be accepted
              pendingEvent = true;
              return true;
            }
          } else {
            throw new RuntimeException("internal error");
          }

        } else {
          // no sign change: there is no event for now
          ta = tb;
          ga = gb;
        }

      }

      // no event during the whole step
      pendingEvent     = false;
      pendingEventTime = Double.NaN;
      return false;

    } catch (DerivativeException e) {
      throw new RuntimeException("unexpected exception", e);
    } catch (FunctionException e) {
      throw new RuntimeException("unexpected exception", e);
    }

  }

  /** Get the occurrence time of the event triggered in the current
   * step.
   * @return occurrence time of the event triggered in the current
   * step.
   */
  public double getEventTime() {
    return pendingEventTime;
  }

  /** Acknowledge the fact the step has been accepted by the integrator.
   * @param t value of the independant <i>time</i> variable at the
   * end of the step
   * @param y array containing the current value of the state vector
   * at the end of the step
   */
  public void stepAccepted(double t, double[] y) {

    t0 = t;
    g0 = function.g(t, y);

    if (pendingEvent) {
      // force the sign to its value "just after the event"
      previousEventTime = t;
      g0Positive        = increasing;
      nextAction        = function.eventOccurred(t, y);
    } else {
      g0Positive = (g0 >= 0);
      nextAction = SwitchingFunction.CONTINUE;
    }
  }

  /** Check if the integration should be stopped at the end of the
   * current step.
   * @return true if the integration should be stopped
   */
  public boolean stop() {
    return nextAction == SwitchingFunction.STOP;
  }

  /** Let the switching function reset the state if it wants.
   * @param t value of the independant <i>time</i> variable at the
   * beginning of the next step
   * @param y array were to put the desired state vector at the beginning
   * of the next step
   * @return true if the integrator should reset the derivatives too
   */
  public boolean reset(double t, double[] y) {

    if (! pendingEvent) {
      return false;
    }

    if (nextAction == SwitchingFunction.RESET_STATE) {
      function.resetState(t, y);
    }
    pendingEvent      = false;
    pendingEventTime  = Double.NaN;

    return (nextAction == SwitchingFunction.RESET_STATE)
        || (nextAction == SwitchingFunction.RESET_DERIVATIVES);

  }

  /** Get the value of the g function at the specified time.
   * @param t current time
   * @return g function value
   * @exception FunctionException if the underlying interpolator is
   * unable to interpolate the state at the specified time
   */
  public double valueAt(double t)
    throws FunctionException {
    try {
      interpolator.setInterpolatedTime(t);
      return function.g(t, interpolator.getInterpolatedState());
    } catch (DerivativeException e) {
      throw new FunctionException(e);
    }
  }

  /** Check if the event time has been found.
   * @param x0 lower bound of the interval
   * @param y0 value of the function at x0
   * @param x1 higher bound of the interval
   * @param y1 value of the function at x1
   * @return convergence indicator
   */
  public int converged(double x0, double y0, double x1, double y1) {
    if (Math.abs(x1 - x0) < convergence) {
      return (Math.abs(y0) < Math.abs(y1))
        ? ConvergenceChecker.LOW : ConvergenceChecker.HIGH;
    }
    return ConvergenceChecker.NONE;
  }

}
