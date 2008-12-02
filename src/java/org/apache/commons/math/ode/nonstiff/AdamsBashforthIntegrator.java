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

import org.apache.commons.math.fraction.Fraction;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.events.CombinedEventsManager;
import org.apache.commons.math.ode.sampling.StepHandler;


/**
 * This class implements explicit Adams-Bashforth integrators for Ordinary
 * Differential Equations.
 *
 * <p>Adams-Bashforth (in fact due to Adams alone) methods are explicit
 * multistep ODE solvers witch fixed stepsize. The value of state vector
 * at step n+1 is a simple combination of the value at step n and of the
 * derivatives at steps n, n-1, n-2 ... Depending on the number k of previous
 * steps one wants to use for computing the next value, different formulas
 * are available:</p>
 * <ul>
 *   <li>k = 1: y<sub>n+1</sub> = y<sub>n</sub> + h f<sub>n</sub></li>
 *   <li>k = 2: y<sub>n+1</sub> = y<sub>n</sub> + h (3f<sub>n</sub>-f<sub>n-1</sub>)/2</li>
 *   <li>k = 3: y<sub>n+1</sub> = y<sub>n</sub> + h (23f<sub>n</sub>-16f<sub>n-1</sub>+5f<sub>n-2</sub>)/12</li>
 *   <li>k = 4: y<sub>n+1</sub> = y<sub>n</sub> + h (55f<sub>n</sub>-59f<sub>n-1</sub>+37f<sub>n-2</sub>-9f<sub>n-3)/24</sub></li>
 *   <li>...</li>
 * </ul>
 *
 * <p>A k-steps Adams-Bashforth method is of order k. There is no limit to the
 * value of k.</p>
 *
 * <p>These methods are explicit: f<sub>n+1</sub> is not used to compute
 * y<sub>n+1</sub>. More accurate implicit Adams methods exist: the
 * Adams-Moulton methods (which are also due to Adams alone). They are
 * provided by the {@link AdamsMoultonIntegrator AdamsMoultonIntegrator} class.</p>
 *
 * @see AdamsMoultonIntegrator
 * @see BDFIntegrator
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class AdamsBashforthIntegrator extends MultistepIntegrator {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 1676381657635800870L;

    /** Integrator method name. */
    private static final String METHOD_NAME = "Adams-Bashforth";

   /** Coefficients for the current method. */
    private final double[] coeffs;

    /** Integration step. */
    private final double step;

    /**
     * Build an Adams-Bashforth integrator with the given order and step size.
     * @param order order of the method (must be strictly positive)
     * @param step integration step size
     */
    public AdamsBashforthIntegrator(final int order, final double step) {

        super(METHOD_NAME, order, new AdamsBashforthStepInterpolator());

        // compute the integration coefficients
        int[][] bdArray = computeBackwardDifferencesArray(order);
        Fraction[] gamma = computeGammaArray(order);
        coeffs = new double[order];
        for (int i = 0; i < order; ++i) {
            Fraction f = Fraction.ZERO;
            for (int j = i; j < order; ++j) {
                f = f.add(gamma[j].multiply(new Fraction(bdArray[j][i], 1)));
            }
            coeffs[i] = f.doubleValue();
        }

        this.step = Math.abs(step);

    }

    /** {@inheritDoc} */
    public double integrate(FirstOrderDifferentialEquations equations,
                            double t0, double[] y0, double t, double[] y)
        throws DerivativeException, IntegratorException {

        sanityChecks(equations, t0, y0, t, y);
        final boolean forward = (t > t0);

        // initialize working arrays
        if (y != y0) {
            System.arraycopy(y0, 0, y, 0, y0.length);
        }
        final double[] yTmp = new double[y0.length];

        // set up an interpolator sharing the integrator arrays
        final AdamsBashforthStepInterpolator interpolator =
                (AdamsBashforthStepInterpolator) prototype.copy();
        interpolator.reinitialize(yTmp, previousT, previousF, forward);

        // set up integration control objects
        stepStart = t0;
        stepSize  = forward ? step : -step;
        for (StepHandler handler : stepHandlers) {
            handler.reset();
        }
        CombinedEventsManager manager = addEndTimeChecker(t0, t, eventsHandlersManager);

        // compute the first few steps using the configured starter integrator
        double stopTime =
            start(previousF.length, stepSize, manager, equations, stepStart, y);
        if (Double.isNaN(previousT[0])) {
            return stopTime;
        }
        stepStart = previousT[0];
        interpolator.storeTime(stepStart);

        boolean lastStep = false;
        while (!lastStep) {

            // shift all data
            interpolator.shift();

            // estimate the state at the end of the step
            for (int j = 0; j < y0.length; ++j) {
                double sum = 0;
                for (int l = 0; l < coeffs.length; ++l) {
                    sum += coeffs[l] * previousF[l][j];
                }
                yTmp[j] = y[j] + stepSize * sum;
            }

            // discrete events handling
            interpolator.storeTime(stepStart + stepSize);
            final boolean truncated;
            if (manager.evaluateStep(interpolator)) {
                truncated = true;
                interpolator.truncateStep(manager.getEventTime());
            } else {
                truncated = false;
            }

            // the step has been accepted (may have been truncated)
            final double nextStep = interpolator.getCurrentTime();
            interpolator.setInterpolatedTime(nextStep);
            System.arraycopy(interpolator.getInterpolatedState(), 0, y, 0, y0.length);
            manager.stepAccepted(nextStep, y);
            lastStep = manager.stop();

            // provide the step data to the step handler
            for (StepHandler handler : stepHandlers) {
                handler.handleStep(interpolator, lastStep);
            }
            stepStart = nextStep;

            if (!lastStep) {
                // prepare next step

                if (manager.reset(stepStart, y)) {

                    // some events handler has triggered changes that
                    // invalidate the derivatives, we need to restart from scratch
                    stopTime =
                        start(previousF.length, stepSize, manager, equations, stepStart, y);
                    if (Double.isNaN(previousT[0])) {
                        return stopTime;
                    }
                    stepStart = previousT[0];

                } else {

                    if (truncated) {
                        // the step has been truncated, we need to adjust the previous steps
                        for (int i = 1; i < previousF.length; ++i) {
                            previousT[i] = stepStart - i * stepSize;
                            interpolator.setInterpolatedTime(previousT[i]);
                            System.arraycopy(interpolator.getInterpolatedState(), 0,
                                             previousF[i], 0, y0.length);
                        }
                    } else {
                        rotatePreviousSteps();
                    }

                    // evaluate differential equations for next step
                    previousT[0] = stepStart;
                    equations.computeDerivatives(stepStart, y, previousF[0]);

                }
            }

        }

        stopTime  = stepStart;
        stepStart = Double.NaN;
        stepSize  = Double.NaN;
        return stopTime;

    }

    /** Get the coefficients of the method.
     * <p>The coefficients are the c<sub>i</sub> terms in the following formula:</p>
     * <pre>
     *   y<sub>n+1</sub> = y<sub>n</sub> + h &times; &sum;<sub>i=0</sub><sup>i=k-1</sup> c<sub>i</sub>f<sub>n-i</sub></li>
     * </pre>
     * @return a copy of the coefficients of the method
     */
    public double[] getCoeffs() {
        return coeffs.clone();
    }

    /** Compute the backward differences coefficients array.
     * <p>This is quite similar to the Pascal triangle containing the
     * binomial coefficiens, except for an additional (-1)<sup>i</sup> sign.
     * We use a straightforward approach here, since we don't expect this to
     * be run too many times with too high k. It is based on the recurrence
     * relations:</p>
     * <pre>
     *   &nabla;<sup>0</sup> f<sub>n</sub> = f<sub>n</sub>
     *   &nabla;<sup>i+1</sup> f<sub>n</sub> = &nabla;<sup>i</sup>f<sub>n</sub> - &nabla;<sup>i</sup>f<sub>n-1</sub>
     * </pre>
     * @param order order of the integration method
     * @return the coefficients array for backward differences
     */
    static int[][] computeBackwardDifferencesArray(final int order) {

        // create the array
        int[][] bdArray = new int[order][];

        // recurrence initialization
        bdArray[0] = new int[] { 1 };

        // fill up array using recurrence relation
        for (int i = 1; i < order; ++i) {
            bdArray[i] = new int[i + 1];
            bdArray[i][0] = 1;
            for (int j = 0; j < i - 1; ++j) {
                bdArray[i][j + 1] = bdArray[i - 1][j + 1] - bdArray[i - 1][j];
            }
            bdArray[i][i] = -bdArray[i - 1][i - 1];
        }

        return bdArray;

    }

    /** Compute the gamma coefficients.
     * @param order order of the integration method
     * @return gamma coefficients array
     */
    static Fraction[] computeGammaArray(final int order) {

        // create the array
        Fraction[] gammaArray = new Fraction[order];

        // recurrence initialization
        gammaArray[0] = Fraction.ONE;

        // fill up array using recurrence relation
        for (int i = 1; i < order; ++i) {
            Fraction gamma = Fraction.ONE;
            for (int j = 1; j <= i; ++j) {
                gamma = gamma.subtract(gammaArray[i - j].multiply(new Fraction(1, j + 1)));
            }
            gammaArray[i] = gamma;
        }

        return gammaArray;

    }

}
