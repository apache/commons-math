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

import java.util.Collection;

/**
 * This abstract class holds the common part of all adaptive
 * stepsize integrators for Ordinary Differential Equations.
 *
 * <p>These algorithms perform integration with stepsize control, which
 * means the user does not specify the integration step but rather a
 * tolerance on error. The error threshold is computed as
 * <pre>
 * threshold_i = absTol_i + relTol_i * max (abs (ym), abs (ym+1))
 * </pre>
 * where absTol_i is the absolute tolerance for component i of the
 * state vector and relTol_i is the relative tolerance for the same
 * component. The user can also use only two scalar values absTol and
 * relTol which will be used for all components.</p>
 *
 * <p>If the estimated error for ym+1 is such that
 * <pre>
 * sqrt((sum (errEst_i / threshold_i)^2 ) / n) < 1
 * </pre>
 *
 * (where n is the state vector dimension) then the step is accepted,
 * otherwise the step is rejected and a new attempt is made with a new
 * stepsize.</p>
 *
 * @version $Revision$ $Date$
 * @since 1.2
 *
 */

public abstract class AdaptiveStepsizeIntegrator
  implements FirstOrderIntegrator {

  /** Build an integrator with the given stepsize bounds.
   * The default step handler does nothing.
   * @param minStep minimal step (must be positive even for backward
   * integration), the last step can be smaller than this
   * @param maxStep maximal step (must be positive even for backward
   * integration)
   * @param scalAbsoluteTolerance allowed absolute error
   * @param scalRelativeTolerance allowed relative error
   */
  public AdaptiveStepsizeIntegrator(double minStep, double maxStep,
                                    double scalAbsoluteTolerance,
                                    double scalRelativeTolerance) {

    this.minStep     = minStep;
    this.maxStep     = maxStep;
    this.initialStep = -1.0;

    this.scalAbsoluteTolerance = scalAbsoluteTolerance;
    this.scalRelativeTolerance = scalRelativeTolerance;
    this.vecAbsoluteTolerance  = null;
    this.vecRelativeTolerance  = null;

    // set the default step handler
    handler = DummyStepHandler.getInstance();

    switchesHandler = new SwitchingFunctionsHandler();

    resetInternalState();

  }

  /** Build an integrator with the given stepsize bounds.
   * The default step handler does nothing.
   * @param minStep minimal step (must be positive even for backward
   * integration), the last step can be smaller than this
   * @param maxStep maximal step (must be positive even for backward
   * integration)
   * @param vecAbsoluteTolerance allowed absolute error
   * @param vecRelativeTolerance allowed relative error
   */
  public AdaptiveStepsizeIntegrator(double minStep, double maxStep,
                                    double[] vecAbsoluteTolerance,
                                    double[] vecRelativeTolerance) {

    this.minStep     = minStep;
    this.maxStep     = maxStep;
    this.initialStep = -1.0;

    this.scalAbsoluteTolerance = 0;
    this.scalRelativeTolerance = 0;
    this.vecAbsoluteTolerance  = vecAbsoluteTolerance;
    this.vecRelativeTolerance  = vecRelativeTolerance;

    // set the default step handler
    handler = DummyStepHandler.getInstance();

    switchesHandler = new SwitchingFunctionsHandler();

    resetInternalState();

  }

  /** Set the initial step size.
   * <p>This method allows the user to specify an initial positive
   * step size instead of letting the integrator guess it by
   * itself. If this method is not called before integration is
   * started, the initial step size will be estimated by the
   * integrator.</p>
   * @param initialStepSize initial step size to use (must be positive even
   * for backward integration ; providing a negative value or a value
   * outside of the min/max step interval will lead the integrator to
   * ignore the value and compute the initial step size by itself)
   */
  public void setInitialStepSize(double initialStepSize) {
    if ((initialStepSize < minStep) || (initialStepSize > maxStep)) {
      initialStep = -1.0;
    } else {
      initialStep = initialStepSize;
    }
  }

  /** Set the step handler for this integrator.
   * The handler will be called by the integrator for each accepted
   * step.
   * @param handler handler for the accepted steps
   */
  public void setStepHandler (StepHandler handler) {
    this.handler = handler;
  }

  /** Get the step handler for this integrator.
   * @return the step handler for this integrator
   */
  public StepHandler getStepHandler() {
    return handler;
  }

  /** Add a switching function to the integrator.
   * @param function switching function
   * @param maxCheckInterval maximal time interval between switching
   * function checks (this interval prevents missing sign changes in
   * case the integration steps becomes very large)
   * @param convergence convergence threshold in the event time search
   * @param maxIterationCount upper limit of the iteration count in
   * the event time search
   * @see #getSwitchingFunctions()
   * @see #clearSwitchingFunctions()
   */
  public void addSwitchingFunction(SwitchingFunction function,
                                   double maxCheckInterval,
                                   double convergence,
                                   int maxIterationCount) {
    switchesHandler.addSwitchingFunction(function, maxCheckInterval, convergence, maxIterationCount);
  }

  /** Get all the switching functions that have been added to the integrator.
   * @return an unmodifiable collection of the added switching functions
   * @see #addSwitchingFunction(SwitchingFunction, double, double, int)
   * @see #clearSwitchingFunctions()
   */
  public Collection<SwitchState> getSwitchingFunctions() {
      return switchesHandler.getSwitchingFunctions();
  }

  /** Remove all the switching functions that have been added to the integrator.
   * @see #addSwitchingFunction(SwitchingFunction, double, double, int)
   * @see #getSwitchingFunctions()
   */
  public void clearSwitchingFunctions() {
      switchesHandler.clearSwitchingFunctions();
  }

  /** Perform some sanity checks on the integration parameters.
   * @param equations differential equations set
   * @param t0 start time
   * @param y0 state vector at t0
   * @param t target time for the integration
   * @param y placeholder where to put the state vector
   * @exception IntegratorException if some inconsistency is detected
   */
  protected void sanityChecks(FirstOrderDifferentialEquations equations,
                              double t0, double[] y0, double t, double[] y)
    throws IntegratorException {
      if (equations.getDimension() != y0.length) {
          throw new IntegratorException("dimensions mismatch: ODE problem has dimension {0}," +
                                        " initial state vector has dimension {1}",
                                        new Object[] {
                                          Integer.valueOf(equations.getDimension()),
                                          Integer.valueOf(y0.length)
                                        });
      }
      if (equations.getDimension() != y.length) {
          throw new IntegratorException("dimensions mismatch: ODE problem has dimension {0}," +
                                        " final state vector has dimension {1}",
                                        new Object[] {
                                          Integer.valueOf(equations.getDimension()),
                                          Integer.valueOf(y.length)
                                        });
      }
      if ((vecAbsoluteTolerance != null) && (vecAbsoluteTolerance.length != y0.length)) {
          throw new IntegratorException("dimensions mismatch: state vector has dimension {0}," +
                                        " absolute tolerance vector has dimension {1}",
                                        new Object[] {
                                          Integer.valueOf(y0.length),
                                          Integer.valueOf(vecAbsoluteTolerance.length)
                                        });
      }
      if ((vecRelativeTolerance != null) && (vecRelativeTolerance.length != y0.length)) {
          throw new IntegratorException("dimensions mismatch: state vector has dimension {0}," +
                                        " relative tolerance vector has dimension {1}",
                                        new Object[] {
                                          Integer.valueOf(y0.length),
                                          Integer.valueOf(vecRelativeTolerance.length)
                                        });
      }
      if (Math.abs(t - t0) <= 1.0e-12 * Math.max(Math.abs(t0), Math.abs(t))) {
        throw new IntegratorException("too small integration interval: length = {0}",
                                      new Object[] { Double.valueOf(Math.abs(t - t0)) });
      }
      
  }

  /** Initialize the integration step.
   * @param equations differential equations set
   * @param forward forward integration indicator
   * @param order order of the method
   * @param scale scaling vector for the state vector
   * @param t0 start time
   * @param y0 state vector at t0
   * @param yDot0 first time derivative of y0
   * @param y1 work array for a state vector
   * @param yDot1 work array for the first time derivative of y1
   * @return first integration step
   * @exception DerivativeException this exception is propagated to
   * the caller if the underlying user function triggers one
   */
  public double initializeStep(FirstOrderDifferentialEquations equations,
                               boolean forward, int order, double[] scale,
                               double t0, double[] y0, double[] yDot0,
                               double[] y1, double[] yDot1)
    throws DerivativeException {

    if (initialStep > 0) {
      // use the user provided value
      return forward ? initialStep : -initialStep;
    }

    // very rough first guess : h = 0.01 * ||y/scale|| / ||y'/scale||
    // this guess will be used to perform an Euler step
    double ratio;
    double yOnScale2 = 0;
    double yDotOnScale2 = 0;
    for (int j = 0; j < y0.length; ++j) {
      ratio         = y0[j] / scale[j];
      yOnScale2    += ratio * ratio;
      ratio         = yDot0[j] / scale[j];
      yDotOnScale2 += ratio * ratio;
    }

    double h = ((yOnScale2 < 1.0e-10) || (yDotOnScale2 < 1.0e-10)) ?
               1.0e-6 : (0.01 * Math.sqrt(yOnScale2 / yDotOnScale2));
    if (! forward) {
      h = -h;
    }

    // perform an Euler step using the preceding rough guess
    for (int j = 0; j < y0.length; ++j) {
      y1[j] = y0[j] + h * yDot0[j];
    }
    equations.computeDerivatives(t0 + h, y1, yDot1);

    // estimate the second derivative of the solution
    double yDDotOnScale = 0;
    for (int j = 0; j < y0.length; ++j) {
      ratio         = (yDot1[j] - yDot0[j]) / scale[j];
      yDDotOnScale += ratio * ratio;
    }
    yDDotOnScale = Math.sqrt(yDDotOnScale) / h;

    // step size is computed such that
    // h^order * max (||y'/tol||, ||y''/tol||) = 0.01
    double maxInv2 = Math.max(Math.sqrt(yDotOnScale2), yDDotOnScale);
    double h1 = (maxInv2 < 1.0e-15) ?
                Math.max(1.0e-6, 0.001 * Math.abs(h)) :
                Math.pow(0.01 / maxInv2, 1.0 / order);
    h = Math.min(100.0 * Math.abs(h), h1);
    h = Math.max(h, 1.0e-12 * Math.abs(t0));  // avoids cancellation when computing t1 - t0
    if (h < getMinStep()) {
      h = getMinStep();
    }
    if (h > getMaxStep()) {
      h = getMaxStep();
    }
    if (! forward) {
      h = -h;
    }

    return h;

  }

  /** Filter the integration step.
   * @param h signed step
   * @param acceptSmall if true, steps smaller than the minimal value
   * are silently increased up to this value, if false such small
   * steps generate an exception
   * @return a bounded integration step (h if no bound is reach, or a bounded value)
   * @exception IntegratorException if the step is too small and acceptSmall is false
   */
  protected double filterStep(double h, boolean acceptSmall)
    throws IntegratorException {

    if (Math.abs(h) < minStep) {
      if (acceptSmall) {
        h = (h < 0) ? -minStep : minStep;
      } else {
        throw new IntegratorException("minimal step size ({0}) reached," +
                                      " integration needs {1}",
                                      new Object[] {
                                        Double.valueOf(minStep),
                                        Double.valueOf(Math.abs(h))
                                      });
      }
    }

    if (h > maxStep) {
      h = maxStep;
    } else if (h < -maxStep) {
      h = -maxStep;
    }

    return h;

  }

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
  public abstract void integrate (FirstOrderDifferentialEquations equations,
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
  public double getCurrentStepStart() {
    return stepStart;
  }

  /** Get the current signed value of the integration stepsize.
   * <p>This method can be called during integration (typically by
   * the object implementing the {@link FirstOrderDifferentialEquations
   * differential equations} problem) if the signed value of the current stepsize
   * that is tried is needed.</p>
   * <p>The result is undefined if the method is called outside of
   * calls to {@link #integrate}</p>
   * @return current signed value of the stepsize
   */
  public double getCurrentSignedStepsize() {
    return stepSize;
  }

  /** Reset internal state to dummy values. */
  protected void resetInternalState() {
    stepStart = Double.NaN;
    stepSize  = Math.sqrt(minStep * maxStep);
  }

  /** Get the minimal step.
   * @return minimal step
   */
  public double getMinStep() {
    return minStep;
  }

  /** Get the maximal step.
   * @return maximal step
   */
  public double getMaxStep() {
    return maxStep;
  }

  /** Minimal step. */
  private double minStep;

  /** Maximal step. */
  private double maxStep;

  /** User supplied initial step. */
  private double initialStep;

  /** Allowed absolute scalar error. */
  protected double scalAbsoluteTolerance;

  /** Allowed relative scalar error. */
  protected double scalRelativeTolerance;

  /** Allowed absolute vectorial error. */
  protected double[] vecAbsoluteTolerance;

  /** Allowed relative vectorial error. */
  protected double[] vecRelativeTolerance;

  /** Step handler. */
  protected StepHandler handler;

  /** Switching functions handler. */
  protected SwitchingFunctionsHandler switchesHandler;

  /** Current step start time. */
  protected double stepStart;

  /** Current stepsize. */
  protected double stepSize;

}
