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
import org.apache.commons.math4.ode.FieldODEStateAndDerivative;
import org.apache.commons.math4.util.MathArrays;

/**
 * This class implements a step interpolator for second order
 * Runge-Kutta integrator.
 *
 * <p>This interpolator computes dense output inside the last
 * step computed. The interpolation equation is consistent with the
 * integration scheme :
 * <ul>
 *   <li>Using reference point at step start:<br>
 *   y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub>) + &theta; h [(1 - &theta;) y'<sub>1</sub> + &theta; y'<sub>2</sub>]
 *   </li>
 *   <li>Using reference point at step end:<br>
 *   y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub> + h) + (1-&theta;) h [&theta; y'<sub>1</sub> - (1+&theta;) y'<sub>2</sub>]
 *   </li>
 * </ul>
 * </p>
 *
 * where &theta; belongs to [0 ; 1] and where y'<sub>1</sub> and y'<sub>2</sub> are the two
 * evaluations of the derivatives already computed during the
 * step.</p>
 *
 * @see MidpointFieldIntegrator
 * @param <T> the type of the field elements
 * @since 3.6
 */

class MidpointFieldStepInterpolator<T extends RealFieldElement<T>>
    extends RungeKuttaFieldStepInterpolator<T> {

    /** Simple constructor.
     * @param rkIntegrator integrator being used
     * @param y reference to the integrator array holding the state at
     * the end of the step
     * @param yDotArray reference to the integrator array holding all the
     * intermediate slopes
     * @param forward integration direction indicator
     * @param mapper equations mapper for the all equations
     */
    MidpointFieldStepInterpolator(final AbstractFieldIntegrator<T> rkIntegrator,
                                  final T[] y, final T[][] yDotArray, final boolean forward,
                                  final FieldEquationsMapper<T> mapper) {
        super(rkIntegrator, y, yDotArray, forward, mapper);
    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    MidpointFieldStepInterpolator(final MidpointFieldStepInterpolator<T> interpolator) {
        super(interpolator);
    }

    /** {@inheritDoc} */
    @Override
    protected MidpointFieldStepInterpolator<T> doCopy() {
        return new MidpointFieldStepInterpolator<T>(this);
    }


    /** {@inheritDoc} */
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper,
                                                                                   final T time, final T theta,
                                                                                   final T oneMinusThetaH) {

        final T coeffDot2                 = theta.multiply(2);
        final T coeffDot1                 = time.getField().getOne().subtract(coeffDot2);
        final T[] interpolatedState       = MathArrays.buildArray(theta.getField(), previousState.length);
        final T[] interpolatedDerivatives = MathArrays.buildArray(theta.getField(), previousState.length);

        if ((previousState != null) && (theta.getReal() <= 0.5)) {
            final T coeff1 = theta.multiply(oneMinusThetaH);
            final T coeff2 = theta.multiply(theta).multiply(h);
            for (int i = 0; i < previousState.length; ++i) {
                final T yDot1 = yDotK[0][i];
                final T yDot2 = yDotK[1][i];
                interpolatedState[i] = previousState[i].add(coeff1.multiply(yDot1)).add(coeff2.multiply(yDot2));
                interpolatedDerivatives[i] = coeffDot1.multiply(yDot1).add(coeffDot2.multiply(yDot2));
            }
        } else {
            final T coeff1 = oneMinusThetaH.multiply(theta);
            final T coeff2 = oneMinusThetaH.multiply(theta.add(1));
            for (int i = 0; i < previousState.length; ++i) {
                final T yDot1 = yDotK[0][i];
                final T yDot2 = yDotK[1][i];
                interpolatedState[i] = currentState[i].add(coeff1.multiply(yDot1)).subtract(coeff2.multiply(yDot2));
                interpolatedDerivatives[i] = coeffDot1.multiply(yDot1).add(coeffDot2.multiply(yDot2));
            }
        }

        return new FieldODEStateAndDerivative<T>(time, interpolatedState, yDotK[0]);

    }

}
