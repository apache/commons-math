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

/**
 * This abstract class holds the common part of all adaptive
 * stepsize integrators for Ordinary Differential Equations.

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

 * <p>If the estimated error for ym+1 is such that
 * <pre>
 * sqrt((sum (errEst_i / threshold_i)^2 ) / n) < 1
 * </pre>

 * (where n is the state vector dimension) then the step is accepted,
 * otherwise the step is rejected and a new attempt is made with a new
 * stepsize.</p>

 * @version $Id: AdaptiveStepsizeIntegrator.java 1705 2006-09-17 19:57:39Z luc $

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
   */
  public void addSwitchingFunction(SwitchingFunction function,
                                   double maxCheckInterval,
                                   double convergence) {
    switchesHandler.add(function, maxCheckInterval, convergence);
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

    double h = ((yOnScale2 < 1.0e-10) || (yDotOnScale2 < 1.0e-10))
      ? 1.0e-6 : (0.01 * Math.sqrt(yOnScale2 / yDotOnScale2));
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
    double h1 = (maxInv2 < 1.0e-15)
      ? Math.max(1.0e-6, 0.001 * Math.abs(h))
      : Math.pow(0.01 / maxInv2, 1.0 / order);
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
        throw new IntegratorException("minimal step size ({0}) reached,"
                                      + " integration needs {1}",
                                      new String[] {
                                        Double.toString(minStep),
                                        Double.toString(Math.abs(h))
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

  public abstract void integrate (FirstOrderDifferentialEquations equations,
                                  double t0, double[] y0,
                                  double t, double[] y)
    throws DerivativeException, IntegratorException;

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

}
