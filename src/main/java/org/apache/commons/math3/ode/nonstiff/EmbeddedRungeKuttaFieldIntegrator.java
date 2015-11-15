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

package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

/**
 * This class implements the common part of all embedded Runge-Kutta
 * integrators for Ordinary Differential Equations.
 *
 * <p>These methods are embedded explicit Runge-Kutta methods with two
 * sets of coefficients allowing to estimate the error, their Butcher
 * arrays are as follows :
 * <pre>
 *    0  |
 *   c2  | a21
 *   c3  | a31  a32
 *   ... |        ...
 *   cs  | as1  as2  ...  ass-1
 *       |--------------------------
 *       |  b1   b2  ...   bs-1  bs
 *       |  b'1  b'2 ...   b's-1 b's
 * </pre>
 * </p>
 *
 * <p>In fact, we rather use the array defined by ej = bj - b'j to
 * compute directly the error rather than computing two estimates and
 * then comparing them.</p>
 *
 * <p>Some methods are qualified as <i>fsal</i> (first same as last)
 * methods. This means the last evaluation of the derivatives in one
 * step is the same as the first in the next step. Then, this
 * evaluation can be reused from one step to the next one and the cost
 * of such a method is really s-1 evaluations despite the method still
 * has s stages. This behaviour is true only for successful steps, if
 * the step is rejected after the error estimation phase, no
 * evaluation is saved. For an <i>fsal</i> method, we have cs = 1 and
 * asi = bi for all i.</p>
 *
 * @param <T> the type of the field elements
 * @since 3.6
 */

public abstract class EmbeddedRungeKuttaFieldIntegrator<T extends RealFieldElement<T>>
    extends AdaptiveStepsizeFieldIntegrator<T> {

    /** Indicator for <i>fsal</i> methods. */
    private final boolean fsal;

    /** Time steps from Butcher array (without the first zero). */
    private final double[] c;

    /** Internal weights from Butcher array (without the first empty row). */
    private final double[][] a;

    /** External weights for the high order method from Butcher array. */
    private final double[] b;

    /** Prototype of the step interpolator. */
    private final RungeKuttaFieldStepInterpolator<T> prototype;

    /** Stepsize control exponent. */
    private final double exp;

    /** Safety factor for stepsize control. */
    private T safety;

    /** Minimal reduction factor for stepsize control. */
    private T minReduction;

    /** Maximal growth factor for stepsize control. */
    private T maxGrowth;

    /** Build a Runge-Kutta integrator with the given Butcher array.
     * @param field field to which the time and state vector elements belong
     * @param name name of the method
     * @param fsal indicate that the method is an <i>fsal</i>
     * @param c time steps from Butcher array (without the first zero)
     * @param a internal weights from Butcher array (without the first empty row)
     * @param b propagation weights for the high order method from Butcher array
     * @param prototype prototype of the step interpolator to use
     * @param minStep minimal step (sign is irrelevant, regardless of
     * integration direction, forward or backward), the last step can
     * be smaller than this
     * @param maxStep maximal step (sign is irrelevant, regardless of
     * integration direction, forward or backward), the last step can
     * be smaller than this
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     */
    protected EmbeddedRungeKuttaFieldIntegrator(final Field<T> field, final String name, final boolean fsal,
                                                final double[] c, final double[][] a, final double[] b,
                                                final RungeKuttaFieldStepInterpolator<T> prototype,
                                                final double minStep, final double maxStep,
                                                final double scalAbsoluteTolerance,
                                                final double scalRelativeTolerance) {

        super(field, name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);

        this.fsal      = fsal;
        this.c         = c;
        this.a         = a;
        this.b         = b;
        this.prototype = prototype;

        exp = -1.0 / getOrder();

        // set the default values of the algorithm control parameters
        setSafety(field.getZero().add(0.9));
        setMinReduction(field.getZero().add(0.2));
        setMaxGrowth(field.getZero().add(10.0));

    }

    /** Build a Runge-Kutta integrator with the given Butcher array.
     * @param field field to which the time and state vector elements belong
     * @param name name of the method
     * @param fsal indicate that the method is an <i>fsal</i>
     * @param c time steps from Butcher array (without the first zero)
     * @param a internal weights from Butcher array (without the first empty row)
     * @param b propagation weights for the high order method from Butcher array
     * @param prototype prototype of the step interpolator to use
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     */
    protected EmbeddedRungeKuttaFieldIntegrator(final Field<T> field, final String name, final boolean fsal,
                                                final double[] c, final double[][] a, final double[] b,
                                                final RungeKuttaFieldStepInterpolator<T> prototype,
                                                final double   minStep, final double maxStep,
                                                final double[] vecAbsoluteTolerance,
                                                final double[] vecRelativeTolerance) {

        super(field, name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);

        this.fsal      = fsal;
        this.c         = c;
        this.a         = a;
        this.b         = b;
        this.prototype = prototype;

        exp = -1.0 / getOrder();

        // set the default values of the algorithm control parameters
        setSafety(field.getZero().add(0.9));
        setMinReduction(field.getZero().add(0.2));
        setMaxGrowth(field.getZero().add(10.0));

    }

    /** Get the order of the method.
     * @return order of the method
     */
    public abstract int getOrder();

    /** Get the safety factor for stepsize control.
     * @return safety factor
     */
    public T getSafety() {
        return safety;
    }

    /** Set the safety factor for stepsize control.
     * @param safety safety factor
     */
    public void setSafety(final T safety) {
        this.safety = safety;
    }

    /** {@inheritDoc} */
    @Override
    public FieldODEStateAndDerivative<T> integrate(final FieldExpandableODE<T> equations,
                                                   final FieldODEState<T> initialState, final T finalTime)
        throws NumberIsTooSmallException, DimensionMismatchException,
        MaxCountExceededException, NoBracketingException {

        sanityChecks(initialState, finalTime);
        final T   t0 = initialState.getTime();
        final T[] y0 = equations.getMapper().mapState(initialState);
        stepStart    = initIntegration(equations, t0, y0, finalTime);
        final boolean forward = finalTime.subtract(initialState.getTime()).getReal() > 0;

        // create some internal working arrays
        final int   stages = c.length + 1;
        T[]         y      = y0;
        final T[][] yDotK  = MathArrays.buildArray(getField(), stages, -1);
        final T[]   yTmp   = MathArrays.buildArray(getField(), y0.length);

        // set up an interpolator sharing the integrator arrays
        final RungeKuttaFieldStepInterpolator<T> interpolator = (RungeKuttaFieldStepInterpolator<T>) prototype.copy();
        interpolator.reinitialize(this, y0, yDotK, forward, equations.getMapper());
        interpolator.storeState(stepStart);

        // set up integration control objects
        T  hNew           = getField().getZero();
        boolean firstTime = true;

        // main integration loop
        isLastStep = false;
        do {

            interpolator.shift();

            // iterate over step size, ensuring local normalized error is smaller than 1
            T error = getField().getZero().add(10);
            while (error.subtract(1.0).getReal() >= 0) {

                // first stage
                yDotK[0] = stepStart.getDerivative();

                if (firstTime) {
                    final T[] scale = MathArrays.buildArray(getField(), mainSetDimension);
                    if (vecAbsoluteTolerance == null) {
                        for (int i = 0; i < scale.length; ++i) {
                            scale[i] = y[i].abs().multiply(scalRelativeTolerance).add(scalAbsoluteTolerance);
                        }
                    } else {
                        for (int i = 0; i < scale.length; ++i) {
                            scale[i] = y[i].abs().multiply(vecRelativeTolerance[i]).add(vecAbsoluteTolerance[i]);
                        }
                    }
                    hNew = initializeStep(forward, getOrder(), scale, stepStart, equations.getMapper());
                    firstTime = false;
                }

                stepSize = hNew;
                if (forward) {
                    if (stepStart.getTime().add(stepSize).subtract(finalTime).getReal() >= 0) {
                        stepSize = finalTime.subtract(stepStart.getTime());
                    }
                } else {
                    if (stepStart.getTime().add(stepSize).subtract(finalTime).getReal() <= 0) {
                        stepSize = finalTime.subtract(stepStart.getTime());
                    }
                }

                // next stages
                for (int k = 1; k < stages; ++k) {

                    for (int j = 0; j < y0.length; ++j) {
                        T sum = yDotK[0][j].multiply(a[k-1][0]);
                        for (int l = 1; l < k; ++l) {
                            sum = sum.add(yDotK[l][j].multiply(a[k-1][l]));
                        }
                        yTmp[j] = y[j].add(stepSize.multiply(sum));
                    }

                    yDotK[k] = computeDerivatives(stepStart.getTime().add(stepSize.multiply(c[k-1])), yTmp);

                }

                // estimate the state at the end of the step
                for (int j = 0; j < y0.length; ++j) {
                    T sum    = yDotK[0][j].multiply(b[0]);
                    for (int l = 1; l < stages; ++l) {
                        sum = sum.add(yDotK[l][j].multiply(b[l]));
                    }
                    yTmp[j] = y[j].add(stepSize.multiply(sum));
                }

                // estimate the error at the end of the step
                error = estimateError(yDotK, y, yTmp, stepSize);
                if (error.subtract(1.0).getReal() >= 0) {
                    // reject the step and attempt to reduce error by stepsize control
                    final T factor = MathUtils.min(maxGrowth,
                                                   MathUtils.max(minReduction, safety.multiply(error.pow(exp))));
                    hNew = filterStep(stepSize.multiply(factor), forward, false);
                }

            }
            final T stepEnd   = stepStart.getTime().add(stepSize);
            final T[] yDotTmp = fsal ? yDotK[stages - 1] : computeDerivatives(stepEnd, yTmp);
            final FieldODEStateAndDerivative<T> stateTmp = new FieldODEStateAndDerivative<T>(stepEnd, yTmp, yDotTmp);

            // local error is small enough: accept the step, trigger events and step handlers
            interpolator.storeState(stateTmp);
            System.arraycopy(yTmp, 0, y, 0, y0.length);
            stepStart = acceptStep(interpolator, finalTime);
            System.arraycopy(y, 0, yTmp, 0, y.length);

            if (!isLastStep) {

                // prepare next step
                interpolator.storeState(stepStart);

                // stepsize control for next step
                final T factor = MathUtils.min(maxGrowth,
                                               MathUtils.max(minReduction, safety.multiply(error.pow(exp))));
                final T  scaledH    = stepSize.multiply(factor);
                final T  nextT      = stepStart.getTime().add(scaledH);
                final boolean nextIsLast = forward ?
                                           nextT.subtract(finalTime).getReal() >= 0 :
                                           nextT.subtract(finalTime).getReal() <= 0;
                hNew = filterStep(scaledH, forward, nextIsLast);

                final T  filteredNextT      = stepStart.getTime().add(hNew);
                final boolean filteredNextIsLast = forward ?
                                                   filteredNextT.subtract(finalTime).getReal() >= 0 :
                                                   filteredNextT.subtract(finalTime).getReal() <= 0;
                if (filteredNextIsLast) {
                    hNew = finalTime.subtract(stepStart.getTime());
                }

            }

        } while (!isLastStep);

        final FieldODEStateAndDerivative<T> finalState = stepStart;
        resetInternalState();
        return finalState;

    }

    /** Get the minimal reduction factor for stepsize control.
     * @return minimal reduction factor
     */
    public T getMinReduction() {
        return minReduction;
    }

    /** Set the minimal reduction factor for stepsize control.
     * @param minReduction minimal reduction factor
     */
    public void setMinReduction(final T minReduction) {
        this.minReduction = minReduction;
    }

    /** Get the maximal growth factor for stepsize control.
     * @return maximal growth factor
     */
    public T getMaxGrowth() {
        return maxGrowth;
    }

    /** Set the maximal growth factor for stepsize control.
     * @param maxGrowth maximal growth factor
     */
    public void setMaxGrowth(final T maxGrowth) {
        this.maxGrowth = maxGrowth;
    }

    /** Compute the error ratio.
     * @param yDotK derivatives computed during the first stages
     * @param y0 estimate of the step at the start of the step
     * @param y1 estimate of the step at the end of the step
     * @param h  current step
     * @return error ratio, greater than 1 if step should be rejected
     */
    protected abstract T estimateError(T[][] yDotK, T[] y0, T[] y1, T h);

}
