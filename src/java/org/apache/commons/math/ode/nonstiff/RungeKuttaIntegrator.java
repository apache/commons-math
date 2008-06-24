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

import java.util.Collection;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.events.CombinedEventsManager;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.DummyStepHandler;
import org.apache.commons.math.ode.sampling.DummyStepInterpolator;
import org.apache.commons.math.ode.sampling.StepHandler;

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
  protected RungeKuttaIntegrator(final double[] c, final double[][] a, final double[] b,
                                 final RungeKuttaStepInterpolator prototype,
                                 final double step) {
    this.c          = c;
    this.a          = a;
    this.b          = b;
    this.prototype  = prototype;
    this.step       = step;
    handler         = DummyStepHandler.getInstance();
    eventsHandlersManager = new CombinedEventsManager();
    resetInternalState();
  }

  /** {@inheritDoc} */
  public abstract String getName();

  /** {@inheritDoc} */
  public void setStepHandler (final StepHandler handler) {
    this.handler = handler;
  }

  /** {@inheritDoc} */
  public StepHandler getStepHandler() {
    return handler;
  }

  /** {@inheritDoc} */
  public void addEventHandler(final EventHandler function,
                              final double maxCheckInterval,
                              final double convergence,
                              final int maxIterationCount) {
    eventsHandlersManager.addEventHandler(function, maxCheckInterval, convergence, maxIterationCount);
  }

  /** {@inheritDoc} */
  public Collection<EventHandler> getEventsHandlers() {
      return eventsHandlersManager.getEventsHandlers();
  }

  /** {@inheritDoc} */
  public void clearEventsHandlers() {
      eventsHandlersManager.clearEventsHandlers();
  }

  /** Perform some sanity checks on the integration parameters.
   * @param equations differential equations set
   * @param t0 start time
   * @param y0 state vector at t0
   * @param t target time for the integration
   * @param y placeholder where to put the state vector
   * @exception IntegratorException if some inconsistency is detected
   */
  private void sanityChecks(final FirstOrderDifferentialEquations equations,
                            final double t0, final double[] y0, final double t, final double[] y)
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
    if (Math.abs(t - t0) <= 1.0e-12 * Math.max(Math.abs(t0), Math.abs(t))) {
      throw new IntegratorException("too small integration interval: length = {0}",
                                    new Object[] { Double.valueOf(Math.abs(t - t0)) });
    }      
  }

  /** {@inheritDoc} */
  public double integrate(final FirstOrderDifferentialEquations equations,
                          final double t0, final double[] y0,
                          final double t, final double[] y)
  throws DerivativeException, IntegratorException {

    sanityChecks(equations, t0, y0, t, y);
    final boolean forward = (t > t0);

    // create some internal working arrays
    final int stages = c.length + 1;
    if (y != y0) {
      System.arraycopy(y0, 0, y, 0, y0.length);
    }
    final double[][] yDotK = new double[stages][];
    for (int i = 0; i < stages; ++i) {
      yDotK [i] = new double[y0.length];
    }
    final double[] yTmp = new double[y0.length];

    // set up an interpolator sharing the integrator arrays
    AbstractStepInterpolator interpolator;
    if (handler.requiresDenseOutput() || (! eventsHandlersManager.isEmpty())) {
      final RungeKuttaStepInterpolator rki = (RungeKuttaStepInterpolator) prototype.copy();
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

        // Discrete events handling
        interpolator.storeTime(stepStart + stepSize);
        if (eventsHandlersManager.evaluateStep(interpolator)) {
          needUpdate = true;
          stepSize = eventsHandlersManager.getEventTime() - stepStart;
        } else {
          loop = false;
        }

      }

      // the step has been accepted
      final double nextStep = stepStart + stepSize;
      System.arraycopy(yTmp, 0, y, 0, y0.length);
      eventsHandlersManager.stepAccepted(nextStep, y);
      if (eventsHandlersManager.stop()) {
        lastStep = true;
      } else {
        lastStep = (i == (nbStep - 1));
      }

      // provide the step data to the step handler
      interpolator.storeTime(nextStep);
      handler.handleStep(interpolator, lastStep);
      stepStart = nextStep;

      if (eventsHandlersManager.reset(stepStart, y) && ! lastStep) {
        // some events handler has triggered changes that
        // invalidate the derivatives, we need to recompute them
        equations.computeDerivatives(stepStart, y, yDotK[0]);
      }

      if (needUpdate) {
        // an event handler has changed the step
        // we need to recompute stepsize
        nbStep = Math.max(1l, Math.abs(Math.round((t - stepStart) / step)));
        stepSize = (t - stepStart) / nbStep;
        i = -1;
      }

    }

    final double stopTime = stepStart;
    resetInternalState();
    return stopTime;

  }

  /** {@inheritDoc} */
  public double getCurrentStepStart() {
    return stepStart;
  }

  /** {@inheritDoc} */
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

  /** Events handlers manager. */
  protected CombinedEventsManager eventsHandlersManager;

  /** Current step start time. */
  private double stepStart;

  /** Current stepsize. */
  private double stepSize;

}
