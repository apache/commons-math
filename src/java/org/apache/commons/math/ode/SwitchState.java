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

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.BrentSolver;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealSolver;

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
 * @version $Revision$ $Date$
 * @since 1.2
 */
class SwitchState implements Serializable {

  /** Serializable version identifier. */
    private static final long serialVersionUID = -7307007422156119622L;

  /** Switching function. */
  private SwitchingFunction function;

  /** Maximal time interval between switching function checks. */
  private double maxCheckInterval;

  /** Convergence threshold for event localization. */
  private double convergence;

  /** Upper limit in the iteration count for event localization. */
  private int maxIterationCount;

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

  /** Simple constructor.
   * @param function switching function
   * @param maxCheckInterval maximal time interval between switching
   * function checks (this interval prevents missing sign changes in
   * case the integration steps becomes very large)
   * @param convergence convergence threshold in the event time search
   * @param maxIterationCount upper limit of the iteration count in
   * the event time search
   */
  public SwitchState(SwitchingFunction function, double maxCheckInterval,
                     double convergence, int maxIterationCount) {
    this.function          = function;
    this.maxCheckInterval  = maxCheckInterval;
    this.convergence       = Math.abs(convergence);
    this.maxIterationCount = maxIterationCount;

    // some dummy values ...
    t0                = Double.NaN;
    g0                = Double.NaN;
    g0Positive        = true;
    pendingEvent      = false;
    pendingEventTime  = Double.NaN;
    previousEventTime = Double.NaN;
    increasing        = true;
    nextAction        = SwitchingFunction.CONTINUE;

  }

  /** Reinitialize the beginning of the step.
   * @param t0 value of the independent <i>time</i> variable at the
   * beginning of the step
   * @param y0 array containing the current value of the state vector
   * at the beginning of the step
   * @exception SwitchException if the switching function
   * value cannot be evaluated at the beginning of the step
   */
  public void reinitializeBegin(double t0, double[] y0)
    throws SwitchException {
    this.t0 = t0;
    g0 = function.g(t0, y0);
    g0Positive = (g0 >= 0);
  }

  /** Evaluate the impact of the proposed step on the switching function.
   * @param interpolator step interpolator for the proposed step
   * @return true if the switching function triggers an event before
   * the end of the proposed step (this implies the step should be
   * rejected)
   * @exception DerivativeException if the interpolator fails to
   * compute the function somewhere within the step
   * @exception SwitchException if the switching function
   * cannot be evaluated
   * @exception ConvergenceException if an event cannot be located
   */
  public boolean evaluateStep(final StepInterpolator interpolator)
    throws DerivativeException, SwitchException, ConvergenceException {

    try {

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

          UnivariateRealSolver solver = new BrentSolver(new UnivariateRealFunction() {
              public double value(double t) throws FunctionEvaluationException {
                  try {
                      interpolator.setInterpolatedTime(t);
                      return function.g(t, interpolator.getInterpolatedState());
                  } catch (DerivativeException e) {
                      throw new FunctionEvaluationException(t, e);
                  } catch (SwitchException e) {
                      throw new FunctionEvaluationException(t, e);
                  }
              }
          });
          solver.setAbsoluteAccuracy(convergence);
          solver.setMaximalIterationCount(maxIterationCount);
          double root = solver.solve(ta, tb);
          if (Double.isNaN(previousEventTime) || (Math.abs(previousEventTime - root) > convergence)) {
              pendingEventTime = root;
              if (pendingEvent && (Math.abs(t1 - pendingEventTime) <= convergence)) {
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
          // no sign change: there is no event for now
          ta = tb;
          ga = gb;
        }

      }

      // no event during the whole step
      pendingEvent     = false;
      pendingEventTime = Double.NaN;
      return false;

    } catch (FunctionEvaluationException e) {
     Throwable cause = e.getCause();
     if ((cause != null) && (cause instanceof DerivativeException)) {
         throw (DerivativeException) cause;
     } else if ((cause != null) && (cause instanceof SwitchException)) {
         throw (SwitchException) cause;
     }
     throw new SwitchException(e);
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
   * @param t value of the independent <i>time</i> variable at the
   * end of the step
   * @param y array containing the current value of the state vector
   * at the end of the step
   * @exception SwitchException if the value of the switching
   * function cannot be evaluated
   */
  public void stepAccepted(double t, double[] y) throws SwitchException {

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
   * @param t value of the independent <i>time</i> variable at the
   * beginning of the next step
   * @param y array were to put the desired state vector at the beginning
   * of the next step
   * @return true if the integrator should reset the derivatives too
   * @exception SwitchException if the state cannot be reseted by the switching
   * function
   */
  public boolean reset(double t, double[] y) throws SwitchException {

    if (! pendingEvent) {
      return false;
    }

    if (nextAction == SwitchingFunction.RESET_STATE) {
      function.resetState(t, y);
    }
    pendingEvent      = false;
    pendingEventTime  = Double.NaN;

    return (nextAction == SwitchingFunction.RESET_STATE) ||
           (nextAction == SwitchingFunction.RESET_DERIVATIVES);

  }

}
