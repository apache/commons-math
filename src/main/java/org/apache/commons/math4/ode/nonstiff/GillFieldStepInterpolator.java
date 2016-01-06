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
import org.apache.commons.math4.ode.FieldEquationsMapper;
import org.apache.commons.math4.ode.FieldODEStateAndDerivative;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.MathArrays;

/**
 * This class implements a step interpolator for the Gill fourth
 * order Runge-Kutta integrator.
 *
 * <p>This interpolator allows to compute dense output inside the last
 * step computed. The interpolation equation is consistent with the
 * integration scheme :
 * <ul>
 *   <li>Using reference point at step start:<br>
 *   y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub>)
 *                    + &theta; (h/6) [ (6 - 9 &theta; + 4 &theta;<sup>2</sup>) y'<sub>1</sub>
 *                                    + (    6 &theta; - 4 &theta;<sup>2</sup>) ((1-1/&radic;2) y'<sub>2</sub> + (1+1/&radic;2)) y'<sub>3</sub>)
 *                                    + (  - 3 &theta; + 4 &theta;<sup>2</sup>) y'<sub>4</sub>
 *                                    ]
 *   </li>
 *   <li>Using reference point at step start:<br>
 *   y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub> + h)
 *                    - (1 - &theta;) (h/6) [ (1 - 5 &theta; + 4 &theta;<sup>2</sup>) y'<sub>1</sub>
 *                                          + (2 + 2 &theta; - 4 &theta;<sup>2</sup>) ((1-1/&radic;2) y'<sub>2</sub> + (1+1/&radic;2)) y'<sub>3</sub>)
 *                                          + (1 +   &theta; + 4 &theta;<sup>2</sup>) y'<sub>4</sub>
 *                                          ]
 *   </li>
 * </ul>
 * </p>
 * where &theta; belongs to [0 ; 1] and where y'<sub>1</sub> to y'<sub>4</sub>
 * are the four evaluations of the derivatives already computed during
 * the step.</p>
 *
 * @see GillFieldIntegrator
 * @param <T> the type of the field elements
 * @since 3.6
 */

class GillFieldStepInterpolator<T extends RealFieldElement<T>>
  extends RungeKuttaFieldStepInterpolator<T> {

    /** First Gill coefficient. */
    private static final double ONE_MINUS_INV_SQRT_2 = 1 - FastMath.sqrt(0.5);

    /** Second Gill coefficient. */
    private static final double ONE_PLUS_INV_SQRT_2 = 1 + FastMath.sqrt(0.5);

    /** Simple constructor.
     * This constructor builds an instance that is not usable yet, the
     * {@link
     * org.apache.commons.math4.ode.sampling.AbstractFieldStepInterpolator#reinitialize}
     * method should be called before using the instance in order to
     * initialize the internal arrays. This constructor is used only
     * in order to delay the initialization in some cases. The {@link
     * RungeKuttaFieldIntegrator} class uses the prototyping design pattern
     * to create the step interpolators by cloning an uninitialized model
     * and later initializing the copy.
     */
    GillFieldStepInterpolator() {
    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    GillFieldStepInterpolator(final GillFieldStepInterpolator<T> interpolator) {
        super(interpolator);
    }

    /** {@inheritDoc} */
    @Override
    protected GillFieldStepInterpolator<T> doCopy() {
        return new GillFieldStepInterpolator<T>(this);
    }


    /** {@inheritDoc} */
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper,
                                                                                   final T time, final T theta,
                                                                                   final T oneMinusThetaH) {

        final T one        = time.getField().getOne();
        final T twoTheta   = theta.multiply(2);
        final T fourTheta2 = twoTheta.multiply(twoTheta);
        final T coeffDot1  = theta.multiply(twoTheta.subtract(3)).add(1);
        final T cDot23     = twoTheta.multiply(one.subtract(theta));
        final T coeffDot2  = cDot23.multiply(ONE_MINUS_INV_SQRT_2);
        final T coeffDot3  = cDot23.multiply(ONE_PLUS_INV_SQRT_2);
        final T coeffDot4  = theta.multiply(twoTheta.subtract(1));
        final T[] interpolatedState       = MathArrays.buildArray(theta.getField(), previousState.length);
        final T[] interpolatedDerivatives = MathArrays.buildArray(theta.getField(), previousState.length);

        if ((previousState != null) && (theta.getReal() <= 0.5)) {
            final T s         = theta.multiply(h).divide(6.0);
            final T c23       = s.multiply(theta.multiply(6).subtract(fourTheta2));
            final T coeff1    = s.multiply(fourTheta2.subtract(theta.multiply(6)).add(6));
            final T coeff2    = c23.multiply(ONE_MINUS_INV_SQRT_2);
            final T coeff3    = c23.multiply(ONE_PLUS_INV_SQRT_2);
            final T coeff4    = s.multiply(fourTheta2.subtract(theta.multiply(3)));
            for (int i = 0; i < interpolatedState.length; ++i) {
                final T yDot1 = yDotK[0][i];
                final T yDot2 = yDotK[1][i];
                final T yDot3 = yDotK[2][i];
                final T yDot4 = yDotK[3][i];
                interpolatedState[i]       = previousState[i].
                                             add(coeff1.multiply(yDot1)).add(coeff2.multiply(yDot2)).
                                             add(coeff3.multiply(yDot3)).add(coeff4.multiply(yDot4));
                interpolatedDerivatives[i] = coeffDot1.multiply(yDot1).add(coeffDot2.multiply(yDot2)).
                                             add(coeffDot3.multiply(yDot3)).add(coeffDot4.multiply(yDot4));
            }
        } else {
            final T s      = oneMinusThetaH.divide(6.0);
            final T c23    = s .multiply(twoTheta.add(2).subtract(fourTheta2));
            final T coeff1 = s.multiply(fourTheta2.subtract(theta.multiply(5)).add(1));
            final T coeff2 = c23.multiply(ONE_MINUS_INV_SQRT_2);
            final T coeff3 = c23.multiply(ONE_PLUS_INV_SQRT_2);
            final T coeff4 = s.multiply(fourTheta2.add(theta).add(1));
            for (int i = 0; i < interpolatedState.length; ++i) {
                final T yDot1 = yDotK[0][i];
                final T yDot2 = yDotK[1][i];
                final T yDot3 = yDotK[2][i];
                final T yDot4 = yDotK[3][i];
                interpolatedState[i]       = currentState[i].
                                             subtract(coeff1.multiply(yDot1)).subtract(coeff2.multiply(yDot2)).
                                             subtract(coeff3.multiply(yDot3)).subtract(coeff4.multiply(yDot4));
                interpolatedDerivatives[i] = coeffDot1.multiply(yDot1).add(coeffDot2.multiply(yDot2)).
                                             add(coeffDot3.multiply(yDot3)).add(coeffDot4.multiply(yDot4));
            }
        }

        return new FieldODEStateAndDerivative<T>(time, interpolatedState, yDotK[0]);

    }

}
