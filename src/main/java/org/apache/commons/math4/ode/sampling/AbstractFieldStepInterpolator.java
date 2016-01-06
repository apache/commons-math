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

    /** Current state. */
    protected T[] currentState;

    /** Global previous state. */
    private FieldODEStateAndDerivative<T> globalPreviousState;

    /** Global current state. */
    private FieldODEStateAndDerivative<T> globalCurrentState;

    /** Soft previous state. */
    private FieldODEStateAndDerivative<T> softPreviousState;

    /** Soft current state. */
    private FieldODEStateAndDerivative<T> softCurrentState;

    /** indicate if the step has been finalized or not. */
    private boolean finalized;

    /** integration direction. */
    private boolean forward;

    /** Mapper for ODE equations primary and secondary components. */
    private FieldEquationsMapper<T> mapper;

    /** Simple constructor.
     * This constructor builds an instance that is not usable yet, the
     * {@link #reinitialize} method should be called before using the
     * instance in order to initialize the internal arrays. This
     * constructor is used only in order to delay the initialization in
     * some cases. As an example, the {@link
     * org.apache.commons.math4.ode.nonstiff.EmbeddedRungeKuttaIntegrator}
     * class uses the prototyping design pattern to create the step
     * interpolators by cloning an uninitialized model and latter
     * initializing the copy.
     */
    protected AbstractFieldStepInterpolator() {
        globalPreviousState = null;
        globalCurrentState  = null;
        softPreviousState   = null;
        softCurrentState    = null;
        h                   = null;
        currentState        = null;
        finalized           = false;
        forward             = true;
        mapper              = null;
    }

    /** Simple constructor.
     * @param y reference to the integrator array holding the state at
     * the end of the step
     * @param forward integration direction indicator
     * @param mapper mapper for ODE equations primary and secondary components
     */
    protected AbstractFieldStepInterpolator(final T[] y, final boolean forward,
                                            final FieldEquationsMapper<T> mapper) {

        globalPreviousState = null;
        globalCurrentState  = null;
        softPreviousState   = null;
        softCurrentState    = null;
        h                   = null;
        currentState        = y;
        finalized           = false;
        this.forward        = forward;
        this.mapper         = mapper;

    }

    /** Copy constructor.

     * <p>The copied interpolator should have been finalized before the
     * copy, otherwise the copy will not be able to perform correctly
     * any derivative computation and will throw a {@link
     * NullPointerException} later. Since we don't want this constructor
     * to throw the exceptions finalization may involve and since we
     * don't want this method to modify the state of the copied
     * interpolator, finalization is <strong>not</strong> done
     * automatically, it remains under user control.</p>

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

        if (interpolator.currentState == null) {
            currentState = null;
            mapper       = null;
        } else {
            currentState   = interpolator.currentState.clone();
        }

        finalized = interpolator.finalized;
        forward   = interpolator.forward;
        mapper    = interpolator.mapper;

    }

    /** Reinitialize the instance
     * @param y reference to the integrator array holding the state at the end of the step
     * @param isForward integration direction indicator
     * @param equationsMapper mapper for ODE equations primary and secondary components
     */
    protected void reinitialize(final T[] y, final boolean isForward, final FieldEquationsMapper<T> equationsMapper) {

        globalPreviousState = null;
        globalCurrentState  = null;
        softPreviousState   = null;
        softCurrentState    = null;
        h                   = null;
        currentState        = y.clone();
        finalized           = false;
        this.forward        = isForward;
        this.mapper         = equationsMapper;

    }

    /** {@inheritDoc} */
    public FieldStepInterpolator<T> copy() throws MaxCountExceededException {

        // finalize the step before performing copy
        finalizeStep();

        // create the new independent instance
        return doCopy();

    }

    /** Really copy the finalized instance.
     * <p>This method is called by {@link #copy()} after the
     * step has been finalized. It must perform a deep copy
     * to have an new instance completely independent for the
     * original instance.
     * @return a copy of the finalized instance
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
        h                  = globalCurrentState.getTime().subtract(globalPreviousState.getTime());

        // the step is not finalized anymore
        finalized  = false;

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

    /**
     * Finalize the step.

     * <p>Some embedded Runge-Kutta integrators need fewer functions
     * evaluations than their counterpart step interpolators. These
     * interpolators should perform the last evaluations they need by
     * themselves only if they need them. This method triggers these
     * extra evaluations. It can be called directly by the user step
     * handler and it is called automatically if {@link
     * #setInterpolatedTime} is called.</p>

     * <p>Once this method has been called, <strong>no</strong> other
     * evaluation will be performed on this step. If there is a need to
     * have some side effects between the step handler and the
     * differential equations (for example update some data in the
     * equations once the step has been done), it is advised to call
     * this method explicitly from the step handler before these side
     * effects are set up. If the step handler induces no side effect,
     * then this method can safely be ignored, it will be called
     * transparently as needed.</p>

     * <p><strong>Warning</strong>: since the step interpolator provided
     * to the step handler as a parameter of the {@link
     * StepHandler#handleStep handleStep} is valid only for the duration
     * of the {@link StepHandler#handleStep handleStep} call, one cannot
     * simply store a reference and reuse it later. One should first
     * finalize the instance, then copy this finalized instance into a
     * new object that can be kept.</p>

     * <p>This method calls the protected <code>doFinalize</code> method
     * if it has never been called during this step and set a flag
     * indicating that it has been called once. It is the <code>
     * doFinalize</code> method which should perform the evaluations.
     * This wrapping prevents from calling <code>doFinalize</code> several
     * times and hence evaluating the differential equations too often.
     * Therefore, subclasses are not allowed not reimplement it, they
     * should rather reimplement <code>doFinalize</code>.</p>

     * @exception MaxCountExceededException if the number of functions evaluations is exceeded

     */
    public final void finalizeStep() throws MaxCountExceededException {
        if (! finalized) {
            doFinalize();
            finalized = true;
        }
    }

    /**
     * Really finalize the step.
     * The default implementation of this method does nothing.
     * @exception MaxCountExceededException if the number of functions evaluations is exceeded
     */
    protected void doFinalize() throws MaxCountExceededException {
    }

}
