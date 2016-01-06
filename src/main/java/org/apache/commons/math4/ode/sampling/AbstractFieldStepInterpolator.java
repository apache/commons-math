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

package org.apache.commons.math4.ode.sampling;

import org.apache.commons.math4.RealFieldElement;
import org.apache.commons.math4.exception.MaxCountExceededException;
import org.apache.commons.math4.ode.FieldEquationsMapper;
import org.apache.commons.math4.ode.FieldODEStateAndDerivative;

/** This abstract class represents an interpolator over the last step
 * during an ODE integration.
 *
 * <p>The various ODE integrators provide objects extending this class
 * to the step handlers. The handlers can use these objects to
 * retrieve the state vector at intermediate times between the
 * previous and the current grid points (dense output).</p>
 *
 * @see org.apache.commons.math4.ode.FieldFirstOrderIntegrator
 * @see StepHandler
 *
 * @param <T> the type of the field elements
 * @since 3.6
 */

public abstract class AbstractFieldStepInterpolator<T extends RealFieldElement<T>>
    implements FieldStepInterpolator<T> {

    /** Current time step. */
    protected T h;

    /** Global previous state. */
    private FieldODEStateAndDerivative<T> globalPreviousState;

    /** Global current state. */
    private FieldODEStateAndDerivative<T> globalCurrentState;

    /** Soft previous state. */
    private FieldODEStateAndDerivative<T> softPreviousState;

    /** Soft current state. */
    private FieldODEStateAndDerivative<T> softCurrentState;

    /** integration direction. */
    private boolean forward;

    /** Mapper for ODE equations primary and secondary components. */
    private FieldEquationsMapper<T> mapper;

    /** Simple constructor.
     * @param isForward integration direction indicator
     * @param equationsMapper mapper for ODE equations primary and secondary components
     */
    protected AbstractFieldStepInterpolator(final boolean isForward,
                                            final FieldEquationsMapper<T> equationsMapper) {
        globalPreviousState = null;
        globalCurrentState  = null;
        softPreviousState   = null;
        softCurrentState    = null;
        h                   = null;
        this.forward        = isForward;
        this.mapper         = equationsMapper;
    }

    /** Copy constructor.
     * <p>The copy is a deep copy: its arrays are separated from the
     * original arrays of the instance.</p>
     * @param interpolator interpolator to copy from.
     */
    protected AbstractFieldStepInterpolator(final AbstractFieldStepInterpolator<T> interpolator) {

        globalPreviousState = interpolator.globalPreviousState;
        globalCurrentState  = interpolator.globalCurrentState;
        softPreviousState   = interpolator.softPreviousState;
        softCurrentState    = interpolator.softCurrentState;
        h                   = interpolator.h;
        forward             = interpolator.forward;
        mapper              = interpolator.mapper;

    }

    /** {@inheritDoc} */
    public FieldStepInterpolator<T> copy() throws MaxCountExceededException {

        // create the new independent instance
        return doCopy();

    }

    /** Really copy the instance.
     * @return a copy of the instance
     */
    protected abstract FieldStepInterpolator<T> doCopy();

    /** Shift one step forward.
     * Copy the current time into the previous time, hence preparing the
     * interpolator for future calls to {@link #storeTime storeTime}
     */
    public void shift() {
        globalPreviousState = globalCurrentState;
        softPreviousState   = globalPreviousState;
        softCurrentState    = globalCurrentState;
    }

    /** Store the current step state.
     * @param state current state
     */
    public void storeState(final FieldODEStateAndDerivative<T> state) {
        globalCurrentState = state;
        softCurrentState   = globalCurrentState;
        if (globalPreviousState != null) {
            h = globalCurrentState.getTime().subtract(globalPreviousState.getTime());
        }
    }

    /** Restrict step range to a limited part of the global step.
     * <p>
     * This method can be used to restrict a step and make it appear
     * as if the original step was smaller. Calling this method
     * <em>only</em> changes the value returned by {@link #getPreviousState()},
     * it does not change any other property
     * </p>
     * @param softPreviousState start of the restricted step
     */
    public void setSoftPreviousState(final FieldODEStateAndDerivative<T> softPreviousState) {
        this.softPreviousState = softPreviousState;
    }

    /** Restrict step range to a limited part of the global step.
     * <p>
     * This method can be used to restrict a step and make it appear
     * as if the original step was smaller. Calling this method
     * <em>only</em> changes the value returned by {@link #getCurrentState()},
     * it does not change any other property
     * </p>
     * @param softCurrentState end of the restricted step
     */
    public void setSoftCurrentState(final FieldODEStateAndDerivative<T> softCurrentState) {
        this.softCurrentState  = softCurrentState;
    }

    /**
     * Get the previous global grid point state.
     * @return previous global grid point state
     */
    public FieldODEStateAndDerivative<T> getGlobalPreviousState() {
        return globalPreviousState;
    }

    /**
     * Get the current global grid point state.
     * @return current global grid point state
     */
    public FieldODEStateAndDerivative<T> getGlobalCurrentState() {
        return globalCurrentState;
    }

    /** {@inheritDoc}
     * @see #setSoftPreviousState(FieldODEStateAndDerivative)
     */
    public FieldODEStateAndDerivative<T> getPreviousState() {
        return softPreviousState;
    }

    /** {@inheritDoc}
     * @see #setSoftCurrentState(FieldODEStateAndDerivative)
     */
    public FieldODEStateAndDerivative<T> getCurrentState() {
        return softCurrentState;
    }

    /** {@inheritDoc} */
    public FieldODEStateAndDerivative<T> getInterpolatedState(final T time) {
        final T oneMinusThetaH = globalCurrentState.getTime().subtract(time);
        final T theta = (h.getReal() == 0) ? h.getField().getZero() : h.subtract(oneMinusThetaH).divide(h);
        return computeInterpolatedStateAndDerivatives(mapper, time, theta, oneMinusThetaH);
    }

    /** {@inheritDoc} */
    public boolean isForward() {
        return forward;
    }

    /** Compute the state and derivatives at the interpolated time.
     * This is the main processing method that should be implemented by
     * the derived classes to perform the interpolation.
     * @param equationsMapper mapper for ODE equations primary and secondary components
     * @param time interpolation time
     * @param theta normalized interpolation abscissa within the step
     * (theta is zero at the previous time step and one at the current time step)
     * @param oneMinusThetaH time gap between the interpolated time and
     * the current time
     * @return interpolated state and derivatives
     * @exception MaxCountExceededException if the number of functions evaluations is exceeded
     */
    protected abstract FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> equationsMapper,
                                                                                            T time, T theta, T oneMinusThetaH)
        throws MaxCountExceededException;

}
