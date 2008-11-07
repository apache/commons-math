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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;

/** This class represents an interpolator over the last step during an
 * ODE integration for multistep integrators.
 *
 * @see MultistepIntegrator
 *
 * @version $Revision$ $Date$
 * @since 2.0
 */

abstract class MultistepStepInterpolator
    extends AbstractStepInterpolator {

    /** Previous steps times. */
    protected double[] previousT;

    /** Previous steps derivatives. */
    protected double[][] previousF;

    /** Simple constructor.
     * This constructor builds an instance that is not usable yet, the
     * {@link #reinitialize} method should be called before using the
     * instance in order to initialize the internal arrays. This
     * constructor is used only in order to delay the initialization in
     * some cases. The {@link MultistepIntegrator} classe uses the
     * prototyping design pattern to create the step interpolators by
     * cloning an uninitialized model and latter initializing the copy.
     */
    protected MultistepStepInterpolator() {
        previousT = null;
        previousF = null;
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
    public MultistepStepInterpolator(final MultistepStepInterpolator interpolator) {

        super(interpolator);

        if (interpolator.currentState != null) {
            previousT = interpolator.previousT.clone();
            previousF = new double[interpolator.previousF.length][];
            for (int k = 0; k < interpolator.previousF.length; ++k) {
                previousF[k] = interpolator.previousF[k].clone();
            }
            initializeCoefficients();
        } else {
            previousT = null;
            previousF = null;
        }

    }

    /** Reinitialize the instance
     * @param y reference to the integrator array holding the state at
     * the end of the step
     * @param previousT reference to the integrator array holding the times
     * of the previous steps
     * @param previousF reference to the integrator array holding the
     * previous slopes
     * @param forward integration direction indicator
     */
    public void reinitialize(final double[] y,
                             final double[] previousT, final double[][] previousF,
                             final boolean forward) {
        reinitialize(y, forward);
        this.previousT = previousT;
        this.previousF = previousF;
        initializeCoefficients();
    }

    /** Initialize the coefficients arrays.
     */
    protected abstract void initializeCoefficients();

    /** {@inheritDoc} */
    public void writeExternal(final ObjectOutput out)
    throws IOException {

        // save the state of the base class
        writeBaseExternal(out);

        // save the local attributes
        out.writeInt(previousT.length);
        for (int k = 0; k < previousF.length; ++k) {
            out.writeDouble(previousT[k]);
            for (int i = 0; i < currentState.length; ++i) {
                out.writeDouble(previousF[k][i]);
            }
        }

    }

    /** {@inheritDoc} */
    public void readExternal(final ObjectInput in)
    throws IOException {

        // read the base class 
        final double t = readBaseExternal(in);

        // read the local attributes
        final int kMax = in.readInt();
        previousT = new double[kMax];
        previousF = new double[kMax][];
        for (int k = 0; k < kMax; ++k) {
            previousT[k] = in.readDouble();
            previousF[k] = new double[currentState.length];
            for (int i = 0; i < currentState.length; ++i) {
                previousF[k][i] = in.readDouble();
            }
        }

        // initialize the coefficients
        initializeCoefficients();

        try {
            // we can now set the interpolated time and state
            setInterpolatedTime(t);
        } catch (DerivativeException e) {
            throw MathRuntimeException.createIOException(e);
        }

    }

}
