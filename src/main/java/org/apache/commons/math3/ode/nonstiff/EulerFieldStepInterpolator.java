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
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

/**
 * This class implements a linear interpolator for step.
 *
 * <p>This interpolator computes dense output inside the last
 * step computed. The interpolation equation is consistent with the
 * integration scheme :
 * <ul>
 *   <li>Using reference point at step start:<br>
 *     y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub>) + &theta; h y'
 *   </li>
 *   <li>Using reference point at step end:<br>
 *     y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub> + h) - (1-&theta;) h y'
 *   </li>
 * </ul>
 * </p>
 *
 * where &theta; belongs to [0 ; 1] and where y' is the evaluation of
 * the derivatives already computed during the step.</p>
 *
 * @see EulerFieldIntegrator
 * @param <T> the type of the field elements
 * @since 3.6
 */

class EulerFieldStepInterpolator<T extends RealFieldElement<T>>
    extends RungeKuttaFieldStepInterpolator<T> {

    /** Simple constructor.
     * This constructor builds an instance that is not usable yet, the
     * {@link
     * org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator#reinitialize}
     * method should be called before using the instance in order to
     * initialize the internal arrays. This constructor is used only
     * in order to delay the initialization in some cases. The {@link
     * RungeKuttaFieldIntegrator} class uses the prototyping design pattern
     * to create the step interpolators by cloning an uninitialized model
     * and later initializing the copy.
     */
    EulerFieldStepInterpolator() {
    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    EulerFieldStepInterpolator(final EulerFieldStepInterpolator<T> interpolator) {
        super(interpolator);
    }

    /** {@inheritDoc} */
    @Override
    protected EulerFieldStepInterpolator<T> doCopy() {
        return new EulerFieldStepInterpolator<T>(this);
    }

    /** {@inheritDoc} */
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper,
                                                                                   final T time, final T theta,
                                                                                   final T oneMinusThetaH) {
        final T[] interpolatedState = MathArrays.buildArray(theta.getField(), previousState.length);
        if ((previousState != null) && (theta.getReal() <= 0.5)) {
            for (int i = 0; i < previousState.length; ++i) {
                interpolatedState[i] = previousState[i].add(theta.multiply(h).multiply(yDotK[0][i]));
            }
        } else {
            for (int i = 0; i < previousState.length; ++i) {
                interpolatedState[i] = currentState[i].subtract(oneMinusThetaH.multiply(yDotK[0][i]));
            }
        }

        return new FieldODEStateAndDerivative<T>(time, interpolatedState, yDotK[0]);
    }

}
