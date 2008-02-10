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
 * This class implements the common part of all fixed step Runge-Kutta
 * integrators for Ordinary Differential Equations.
 *
 * <p>These methods are explicit Runge-Kutta methods, their Butcher
 * arrays are as follows :
 * <pre>
 *    0  |
 *   c2  | a21
 *   c3  | a31  a32
 *   ... |        ...
 *   cs  | as1  as2  ...  ass-1
 *       |--------------------------
 *       |  b1   b2  ...   bs-1  bs
 * </pre>
 * </p>
 *
 * @see EulerIntegrator
 * @see ClassicalRungeKuttaIntegrator
 * @see GillIntegrator
 * @see MidpointIntegrator
 * @version $Revision$ $Date$
 * @since 1.2
 */

public abstract class RungeKuttaIntegrator
  implements FirstOrderIntegrator {

  /** Simple constructor.
   * Build a Runge-Kutta integrator with the given
   * step. The default step handler does nothing.
   * @param c time steps from Butcher array (without the first zero)
   * @param a internal weights from Butcher array (without the first empty row)
   * @param b propagation weights for the high order method from Butcher array
   * @param prototype prototype of the step interpolator to use
   * @param step integration step
   */
  protected RungeKuttaIntegrator(double[] c, double[][] a, double[] b,
                                 RungeKuttaStepInterpolator prototype,
                                 double step) {
    this.c          = c;
    this.a          = a;
    this.b          = b;
    this.prototype  = prototype;
    this.step       = step;
    handler         = DummyStepHandler.getInstance();
    switchesHandler = new SwitchingFunctionsHandler();
    resetInternalState();
  }

  /** Get the name of the method.
   * @return name of the method
   */
  public abstract String getName();

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
   */
  public void addSwitchingFunction(SwitchingFunction function,
                                   double maxCheckInterval,
                                   double convergence,
                                   int maxIterationCount) {
    switchesHandler.add(function, maxCheckInterval, convergence, maxIterationCount);
  }

  /** Perform some sanity checks on the integration parameters.
   * @param equations differential equations set
   * @param t0 start time
   * @param y0 state vector at t0
   * @param t target time for the integration
   * @param y placeholder where to put the state vector
   * @exception IntegratorException if some inconsistency is detected
   */
  private void sanityChecks(FirstOrderDifferentialEquations equations,
                            double t0, double[] y0, double t, double[] y)
    throws IntegratorException {
    if (equations.getDimension() != y0.length) {
      throw new IntegratorException("dimensions mismatch: ODE problem has dimension {0}," +
                                    " initial state vector has dimension {1}",
                                    new Object[] {
                                      new Integer(equations.getDimension()),
                                      new Integer(y0.length)
                                    });
    }
    if (equations.getDimension() != y.length) {
        throw new IntegratorException("dimensions mismatch: ODE problem has dimension {0}," +
                                      " final state vector has dimension {1}",
                                      new Object[] {
                                        new Integer(equations.getDimension()),
                                        new Integer(y.length)
                                      });
      }
    if (Math.abs(t - t0) <= 1.0e-12 * Math.max(Math.abs(t0), Math.abs(t))) {
      throw new IntegratorException("too small integration interval: length = {0}",
                                    new Object[] { new Double(Math.abs(t - t0)) });
    }      
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
  public void integrate(FirstOrderDifferentialEquations equations,
                        double t0, double[] y0,
                        double t, double[] y)
  throws DerivativeException, IntegratorException {

    sanityChecks(equations, t0, y0, t, y);
    boolean forward = (t > t0);

    // create some internal working arrays
    int stages = c.length + 1;
    if (y != y0) {
      System.arraycopy(y0, 0, y, 0, y0.length);
    }
    double[][] yDotK = new double[stages][];
    for (int i = 0; i < stages; ++i) {
      yDotK [i] = new double[y0.length];
    }
    double[] yTmp = new double[y0.length];

    // set up an interpolator sharing the integrator arrays
    AbstractStepInterpolator interpolator;
    if (handler.requiresDenseOutput() || (! switchesHandler.isEmpty())) {
      RungeKuttaStepInterpolator rki = (RungeKuttaStepInterpolator) prototype.copy();
      rki.reinitialize(equations, yTmp, yDotK, forward);
      interpolator = rki;
    } else {
      interpolator = new DummyStepInterpolator(yTmp, forward);
    }
    interpolator.storeTime(t0);

    // recompute the step
    long    nbStep    = Math.max(1l, Math.abs(Math.round((t - t0) / step)));
    boolean lastStep  = false;
    stepStart = t0;
    stepSize  = (t - t0) / nbStep;
    handler.reset();
    for (long i = 0; ! lastStep; ++i) {

      interpolator.shift();

      boolean needUpdate = false;
      for (boolean loop = true; loop;) {

        // first stage
        equations.computeDerivatives(stepStart, y, yDotK[0]);

        // next stages
        for (int k = 1; k < stages; ++k) {

          for (int j = 0; j < y0.length; ++j) {
            double sum = a[k-1][0] * yDotK[0][j];
            for (int l = 1; l < k; ++l) {
              sum += a[k-1][l] * yDotK[l][j];
            }
            yTmp[j] = y[j] + stepSize * sum;
          }

          equations.computeDerivatives(stepStart + c[k-1] * stepSize, yTmp, yDotK[k]);

        }

        // estimate the state at the end of the step
        for (int j = 0; j < y0.length; ++j) {
          double sum    = b[0] * yDotK[0][j];
          for (int l = 1; l < stages; ++l) {
            sum    += b[l] * yDotK[l][j];
          }
          yTmp[j] = y[j] + stepSize * sum;
        }

        // Switching functions handling
        interpolator.storeTime(stepStart + stepSize);
        if (switchesHandler.evaluateStep(interpolator)) {
          needUpdate = true;
          stepSize = switchesHandler.getEventTime() - stepStart;
        } else {
          loop = false;
        }

      }

      // the step has been accepted
      double nextStep = stepStart + stepSize;
      System.arraycopy(yTmp, 0, y, 0, y0.length);
      switchesHandler.stepAccepted(nextStep, y);
      if (switchesHandler.stop()) {
        lastStep = true;
      } else {
        lastStep = (i == (nbStep - 1));
      }

      // provide the step data to the step handler
      interpolator.storeTime(nextStep);
      handler.handleStep(interpolator, lastStep);
      stepStart = nextStep;

      if (switchesHandler.reset(stepStart, y) && ! lastStep) {
        // some switching function has triggered changes that
        // invalidate the derivatives, we need to recompute them
        equations.computeDerivatives(stepStart, y, yDotK[0]);
      }

      if (needUpdate) {
        // a switching function has changed the step
        // we need to recompute stepsize
        nbStep = Math.max(1l, Math.abs(Math.round((t - stepStart) / step)));
        stepSize = (t - stepStart) / nbStep;
        i = -1;
      }

    }

    resetInternalState();

  }

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
  private void resetInternalState() {
    stepStart = Double.NaN;
    stepSize  = Double.NaN;
  }

  /** Time steps from Butcher array (without the first zero). */
  private double[] c;

  /** Internal weights from Butcher array (without the first empty row). */
  private double[][] a;

  /** External weights for the high order method from Butcher array. */
  private double[] b;

  /** Prototype of the step interpolator. */
  private RungeKuttaStepInterpolator prototype;
                                         
  /** Integration step. */
  private double step;

  /** Step handler. */
  private StepHandler handler;

  /** Switching functions handler. */
  protected SwitchingFunctionsHandler switchesHandler;

  /** Current step start time. */
  private double stepStart;

  /** Current stepsize. */
  private double stepSize;

}
