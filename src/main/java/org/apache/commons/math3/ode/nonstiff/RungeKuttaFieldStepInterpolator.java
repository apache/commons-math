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

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.AbstractFieldIntegrator;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.util.MathArrays;

/** This class represents an interpolator over the last step during an
 * ODE integration for Runge-Kutta and embedded Runge-Kutta integrators.
 *
 * @see RungeKuttaFieldIntegrator
 * @see EmbeddedRungeKuttaFieldIntegrator
 *
 * @param <T> the type of the field elements
 * @since 3.6
 */

abstract class RungeKuttaFieldStepInterpolator<T extends RealFieldElement<T>>
    extends AbstractFieldStepInterpolator<T> {

    /** Reference to the integrator. */
    protected AbstractFieldIntegrator<T> integrator;

    /** Slopes at the intermediate points. */
    private T[][] yDotK;

    /** Simple constructor.
     * @param rkIntegrator integrator being used
     * @param forward integration direction indicator
     * @param mapper equations mapper for the all equations
     */
    protected RungeKuttaFieldStepInterpolator(final AbstractFieldIntegrator<T> rkIntegrator,
                                              final boolean forward,
                                              final FieldEquationsMapper<T> mapper) {
        super(forward, mapper);
        this.yDotK      = null;
        this.integrator = rkIntegrator;
    }

    /** Copy constructor.

     * <p>The copied interpolator should have been finalized before the
     * copy, otherwise the copy will not be able to perform correctly any
     * interpolation and will throw a {@link NullPointerException}
     * later. Since we don't want this constructor to throw the
     * exceptions finalization may involve and since we don't want this
     * method to modify the state of the copied interpolator,
     * finalization is <strong>not</strong> done automatically, it
     * remains under user control.</p>

     * <p>The copy is a deep copy: its arrays are separated from the
     * original arrays of the instance.</p>

     * @param interpolator interpolator to copy from.

     */
    RungeKuttaFieldStepInterpolator(final RungeKuttaFieldStepInterpolator<T> interpolator) {

        super(interpolator);

        if (yDotK != null) {
            yDotK = MathArrays.buildArray(interpolator.integrator.getField(),
                                          interpolator.yDotK.length, -1);
            for (int k = 0; k < yDotK.length; ++k) {
                yDotK[k] = interpolator.yDotK[k].clone();
            }

        } else {
            yDotK = null;
        }

        // we cannot keep any reference to the equations in the copy
        // the interpolator should have been finalized before
        integrator = null;

    }

    /** Store the slopes at the intermediate points.
     * @param slopes slopes at the intermediate points
     */
    void setSlopes(final T[][] slopes) {
        this.yDotK = slopes.clone();
    }

    /** Compute a state by linear combination added to previous state.
     * @param coefficients coefficients to apply to the method staged derivatives
     * @return combined state
     */
    @SafeVarargs
    protected final T[] previousStateLinearCombination(final T ... coefficients) {
        return combine(getPreviousState().getState(),
                       coefficients);
    }

    /** Compute a state by linear combination added to current state.
     * @param coefficients coefficients to apply to the method staged derivatives
     * @return combined state
     */
    @SuppressWarnings("unchecked")
    protected T[] currentStateLinearCombination(final T ... coefficients) {
        return combine(getCurrentState().getState(),
                       coefficients);
    }

    /** Compute a state derivative by linear combination.
     * @param coefficients coefficients to apply to the method staged derivatives
     * @return combined state
     */
    @SuppressWarnings("unchecked")
    protected T[] derivativeLinearCombination(final T ... coefficients) {
        return combine(MathArrays.buildArray(integrator.getField(), yDotK[0].length),
                       coefficients);
    }

    /** Linearly combine arrays.
     * @param a array to add to
     * @param coefficients coefficients to apply to the method staged derivatives
     * @return a itself, as a conveniency for fluent API
     */
    @SuppressWarnings("unchecked")
    private T[] combine(final T[] a, final T ... coefficients) {
        for (int i = 0; i < a.length; ++i) {
            for (int k = 0; k < coefficients.length; ++k) {
                a[i] = a[i].add(coefficients[k].multiply(yDotK[k][i]));
            }
        }
        return a;
    }

}
