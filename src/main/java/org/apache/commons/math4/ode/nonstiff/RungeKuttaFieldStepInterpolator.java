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

package org.apache.commons.math4.ode.nonstiff;

import org.apache.commons.math4.RealFieldElement;
import org.apache.commons.math4.ode.AbstractFieldIntegrator;
import org.apache.commons.math4.ode.FieldEquationsMapper;
import org.apache.commons.math4.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math4.util.MathArrays;

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

    /** Previous state. */
    protected T[] previousState;

    /** Slopes at the intermediate points */
    protected T[][] yDotK;

    /** Reference to the integrator. */
    protected AbstractFieldIntegrator<T> integrator;

    /** Simple constructor.
     * This constructor builds an instance that is not usable yet, the
     * {@link #reinitialize} method should be called before using the
     * instance in order to initialize the internal arrays. This
     * constructor is used only in order to delay the initialization in
     * some cases. The {@link RungeKuttaIntegrator} and {@link
     * EmbeddedRungeKuttaIntegrator} classes use the prototyping design
     * pattern to create the step interpolators by cloning an
     * uninitialized model and latter initializing the copy.
     */
    protected RungeKuttaFieldStepInterpolator() {
        previousState = null;
        yDotK         = null;
        integrator    = null;
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

        if (interpolator.currentState != null) {

            previousState = interpolator.previousState.clone();

            yDotK = MathArrays.buildArray(interpolator.integrator.getField(),
                                          interpolator.yDotK.length, interpolator.yDotK[0].length);
            for (int k = 0; k < interpolator.yDotK.length; ++k) {
                System.arraycopy(interpolator.yDotK[k], 0, yDotK[k], 0, interpolator.yDotK[k].length);
            }

        } else {
            previousState = null;
            yDotK = null;
        }

        // we cannot keep any reference to the equations in the copy
        // the interpolator should have been finalized before
        integrator = null;

    }

    /** Reinitialize the instance
     * <p>Some Runge-Kutta integrators need fewer functions evaluations
     * than their counterpart step interpolators. So the interpolator
     * should perform the last evaluations they need by themselves. The
     * {@link RungeKuttaFieldIntegrator RungeKuttaFieldIntegrator} and {@link
     * EmbeddedRungeKuttaFieldIntegrator EmbeddedRungeKuttaFieldIntegrator}
     * abstract classes call this method in order to let the step
     * interpolator perform the evaluations it needs. These evaluations
     * will be performed during the call to <code>doFinalize</code> if
     * any, i.e. only if the step handler either calls the {@link
     * AbstractFieldStepInterpolator#finalizeStep finalizeStep} method or the
     * {@link AbstractFieldStepInterpolator#getInterpolatedState
     * getInterpolatedState} method (for an interpolator which needs a
     * finalization) or if it clones the step interpolator.</p>
     * @param rkIntegrator integrator being used
     * @param y reference to the integrator array holding the state at
     * the end of the step
     * @param yDotArray reference to the integrator array holding all the
     * intermediate slopes
     * @param forward integration direction indicator
     * @param mapper equations mapper for the all equations
     */
    public void reinitialize(final AbstractFieldIntegrator<T> rkIntegrator,
                             final T[] y, final T[][] yDotArray, final boolean forward,
                             final FieldEquationsMapper<T> mapper) {
        reinitialize(y, forward, mapper);
        this.previousState = null;
        this.yDotK         = yDotArray;
        this.integrator    = rkIntegrator;
    }

    /** {@inheritDoc} */
    @Override
    public void shift() {
        previousState = currentState.clone();
        super.shift();
    }

}
