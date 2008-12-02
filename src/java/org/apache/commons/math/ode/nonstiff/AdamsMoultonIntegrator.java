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
 * This class implements implicit Adams-Moulton integrators for Ordinary
 * Differential Equations.
 *
 * <p>Adams-Moulton (in fact due to Adams alone) methods are implicit
 * multistep ODE solvers witch fixed stepsize. The value of state vector
 * at step n+1 is a simple combination of the value at step n and of the
 * derivatives at steps n+1, n, n-1 ... Depending on the number k of previous
 * steps one wants to use for computing the next value, different formulas
 * are available:</p>
 * <ul>
 *   <li>k = 0: y<sub>n+1</sub> = y<sub>n</sub> + h f<sub>n+1</sub></li>
 *   <li>k = 1: y<sub>n+1</sub> = y<sub>n</sub> + h (f<sub>n+1</sub>+f<sub>n</sub>)/2</li>
 *   <li>k = 2: y<sub>n+1</sub> = y<sub>n</sub> + h (5f<sub>n+1</sub>+8f<sub>n</sub>-f<sub>n-1</sub>)/12</li>
 *   <li>k = 3: y<sub>n+1</sub> = y<sub>n</sub> + h (9f<sub>n+1</sub>+19f<sub>n</sub>-5f<sub>n-1</sub>+f<sub>n-2)/24</sub></li>
 *   <li>...</li>
 * </ul>
 *
 * <p>A k-steps Adams-Moulton method is of order k+1. There is no limit to the
 * value of k.</p>
 *
 * <p>These methods are implicit: f<sub>n+1</sub> is used to compute
 * y<sub>n+1</sub>. Simpler explicit Adams methods exist: the
 * Adams-Bashforth methods (which are also due to Adams alone). They are
 * provided by the {@link AdamsBashforthIntegrator AdamsBashforthIntegrator} class.</p>
 *
 * @see AdamsBashforthIntegrator
 * @see BDFIntegrator
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class AdamsMoultonIntegrator extends MultistepIntegrator {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 4990335331377040417L;

    /** Integrator method name. */
    private static final String METHOD_NAME = "Adams-Moulton";

    /** Coefficients for the predictor phase of the method. */
    private final double[] predictorCoeffs;

    /** Coefficients for the corrector phase of the method. */
    private final double[] correctorCoeffs;

    /** Integration step. */
    private final double step;

    /**
     * Build an Adams-Moulton integrator with the given order and step size.
     * @param order order of the method (must be strictly positive)
     * @param step integration step size
     */
    public AdamsMoultonIntegrator(final int order, final double step) {

        super(METHOD_NAME, order + 1, new AdamsMoultonStepInterpolator());

        // compute the integration coefficients
        int[][] bdArray      = AdamsBashforthIntegrator.computeBackwardDifferencesArray(order + 1);

        Fraction[] gamma     = AdamsBashforthIntegrator.computeGammaArray(order);
        predictorCoeffs = new double[order];
        for (int i = 0; i < order; ++i) {
            Fraction fPredictor = Fraction.ZERO;
            for (int j = i; j < order; ++j) {
                Fraction f = new Fraction(bdArray[j][i], 1);
                fPredictor = fPredictor.add(gamma[j].multiply(f));
            }
            predictorCoeffs[i] = fPredictor.doubleValue();
        }

        Fraction[] gammaStar = computeGammaStarArray(order);
        correctorCoeffs = new double[order + 1];
        for (int i = 0; i <= order; ++i) {
            Fraction fCorrector = Fraction.ZERO;
            for (int j = i; j <= order; ++j) {
                Fraction f = new Fraction(bdArray[j][i], 1);
                fCorrector = fCorrector.add(gammaStar[j].multiply(f));
            }
            correctorCoeffs[i] = fCorrector.doubleValue();
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
        final AdamsMoultonStepInterpolator interpolator =
                (AdamsMoultonStepInterpolator) prototype.copy();
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
            start(previousF.length - 1, stepSize, manager, equations, stepStart, y);
        if (Double.isNaN(previousT[0])) {
            return stopTime;
        }
        stepStart = previousT[0];
        rotatePreviousSteps();
        previousF[0] = new double[y0.length];
        interpolator.storeTime(stepStart);

        boolean lastStep = false;
        while (!lastStep) {

            // shift all data
            interpolator.shift();

            // predict state at end of step
            for (int j = 0; j < y0.length; ++j) {
                double sum = 0;
                for (int l = 0; l < predictorCoeffs.length; ++l) {
                    sum += predictorCoeffs[l] * previousF[l+1][j];
                }
                yTmp[j] = y[j] + stepSize * sum;
            }

            // evaluate the derivatives
            final double stepEnd = stepStart + stepSize;
            equations.computeDerivatives(stepEnd, yTmp, previousF[0]);

            // apply corrector
            for (int j = 0; j < y0.length; ++j) {
                double sum = 0;
                for (int l = 0; l < correctorCoeffs.length; ++l) {
                    sum += correctorCoeffs[l] * previousF[l][j];
                }
                yTmp[j] = y[j] + stepSize * sum;
            }

            // discrete events handling
            interpolator.storeTime(stepEnd);
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
                        start(previousF.length - 1, stepSize, manager, equations, stepStart, y);
                    if (Double.isNaN(previousT[0])) {
                        return stopTime;
                    }
                    stepStart = previousT[0];
                    rotatePreviousSteps();
                    previousF[0] = new double[y0.length];

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

    /** Get the coefficients of the predictor phase of the method.
     * <p>The coefficients are the c<sub>i</sub> terms in the following formula:</p>
     * <pre>
     *   y<sub>n+1</sub> = y<sub>n</sub> + h &times; &sum;<sub>i=0</sub><sup>i=k-1</sup> c<sub>i</sub>f<sub>n-i</sub></li>
     * </pre>
     * @return a copy of the coefficients of the method
     */
    public double[] getPredictorCoeffs() {
        return predictorCoeffs.clone();
    }

    /** Get the coefficients of the corrector phase of the method.
     * <p>The coefficients are the c<sub>i</sub> terms in the following formula:</p>
     * <pre>
     *   y<sub>n+1</sub> = y<sub>n</sub> + h &times; &sum;<sub>i=0</sub><sup>i=k</sup> c<sub>i</sub>f<sub>n-i</sub></li>
     * </pre>
     * @return a copy of the coefficients of the method
     */
    public double[] getCorrectorCoeffs() {
        return correctorCoeffs.clone();
    }

    /** Compute the gamma star coefficients.
     * @param order order of the integration method
     * @return gamma star coefficients array
     */
    static Fraction[] computeGammaStarArray(final int order) {

        // create the array
        Fraction[] gammaStarArray = new Fraction[order + 1];

        // recurrence initialization
        gammaStarArray[0] = Fraction.ONE;

        // fill up array using recurrence relation
        for (int i = 1; i <= order; ++i) {
            Fraction gammaStar = Fraction.ZERO;
            for (int j = 1; j <= i; ++j) {
                gammaStar = gammaStar.subtract(gammaStarArray[i - j].multiply(new Fraction(1, j + 1)));
            }
            gammaStarArray[i] = gammaStar;
        }

        return gammaStarArray;

    }

}
