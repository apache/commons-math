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
import org.apache.commons.math4.util.MathArrays;

/**
 * This class represents an interpolator over the last step during an
 * ODE integration for the 5(4) Higham and Hall integrator.
 *
 * @see HighamHall54FieldIntegrator
 *
 * @param <T> the type of the field elements
 * @since 3.6
 */

class HighamHall54FieldStepInterpolator<T extends RealFieldElement<T>>
    extends RungeKuttaFieldStepInterpolator<T> {

    /** Simple constructor.
     * This constructor builds an instance that is not usable yet, the
     * {@link
     * org.apache.commons.math4.ode.sampling.AbstractStepInterpolator#reinitialize}
     * method should be called before using the instance in order to
     * initialize the internal arrays. This constructor is used only
     * in order to delay the initialization in some cases. The {@link
     * EmbeddedRungeKuttaIntegrator} uses the prototyping design pattern
     * to create the step interpolators by cloning an uninitialized model
     * and later initializing the copy.
     */
    HighamHall54FieldStepInterpolator() {
        super();
    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    HighamHall54FieldStepInterpolator(final HighamHall54FieldStepInterpolator<T> interpolator) {
        super(interpolator);
    }

    /** {@inheritDoc} */
    @Override
    protected HighamHall54FieldStepInterpolator<T> doCopy() {
        return new HighamHall54FieldStepInterpolator<T>(this);
    }


    /** {@inheritDoc} */
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper,
                                                                                   final T time, final T theta,
                                                                                   final T oneMinusThetaH) {

        final T bDot0 = theta.multiply(theta.multiply(theta.multiply( -10.0      ).add( 16.0       )).add(-15.0 /  2.0)).add(1);
        final T bDot2 = theta.multiply(theta.multiply(theta.multiply( 135.0 / 2.0).add(-729.0 / 8.0)).add(459.0 / 16.0));
        final T bDot3 = theta.multiply(theta.multiply(theta.multiply(-120.0      ).add( 152.0      )).add(-44.0       ));
        final T bDot4 = theta.multiply(theta.multiply(theta.multiply( 125.0 / 2.0).add(-625.0 / 8.0)).add(375.0 / 16.0));
        final T bDot5 = theta.multiply(  5.0 /  8.0).multiply(theta.multiply(2).subtract(1));
        final T[] interpolatedState       = MathArrays.buildArray(theta.getField(), previousState.length);
        final T[] interpolatedDerivatives = MathArrays.buildArray(theta.getField(), previousState.length);

        if ((previousState != null) && (theta.getReal() <= 0.5)) {
            final T hTheta = h.multiply(theta);
            final T b0 = hTheta.multiply(theta.multiply(theta.multiply(theta.multiply( -5.0 / 2.0).add(  16.0 /  3.0)).add(-15.0 /  4.0)).add(1));
            final T b2 = hTheta.multiply(theta.multiply(theta.multiply(theta.multiply(135.0 / 8.0).add(-243.0 /  8.0)).add(459.0 / 32.0)));
            final T b3 = hTheta.multiply(theta.multiply(theta.multiply(theta.multiply(-30.0      ).add( 152.0 /  3.0)).add(-22.0       )));
            final T b4 = hTheta.multiply(theta.multiply(theta.multiply(theta.multiply(125.0 / 8.0).add(-625.0 / 24.0)).add(375.0 / 32.0)));
            final T b5 = hTheta.multiply(theta.multiply(theta.multiply(                                   5.0 / 12.0)).add( -5.0 / 16.0));
            for (int i = 0; i < interpolatedState.length; ++i) {
                final T yDot0 = yDotK[0][i];
                final T yDot2 = yDotK[2][i];
                final T yDot3 = yDotK[3][i];
                final T yDot4 = yDotK[4][i];
                final T yDot5 = yDotK[5][i];
                interpolatedState[i] = previousState[i].
                                       add(b0.multiply(yDot0)).
                                       add(b2.multiply(yDot2)).
                                       add(b3.multiply(yDot3)).
                                       add(b4.multiply(yDot4)).
                                       add(b5.multiply(yDot5));
                interpolatedDerivatives[i] =     bDot0.multiply(yDot0).
                                             add(bDot2.multiply(yDot2)).
                                             add(bDot3.multiply(yDot3)).
                                             add(bDot4.multiply(yDot4)).
                                             add(bDot5.multiply(yDot5));
            }
        } else {
            final T theta2 = theta.multiply(theta);
            final T b0 = h.multiply( theta.multiply(theta.multiply(theta.multiply(theta.multiply(-5.0 / 2.0).add( 16.0 / 3.0)).add( -15.0 /  4.0)).add(  1.0       )).add(  -1.0 / 12.0));
            final T b2 = h.multiply(theta2.multiply(theta.multiply(theta.multiply(                               135.0 / 8.0 ).add(-243.0 /  8.0)).add(459.0 / 32.0)).add( -27.0 / 32.0));
            final T b3 = h.multiply(theta2.multiply(theta.multiply(theta.multiply(                               -30.0       ).add( 152.0 /  3.0)).add(-22.0       )).add(  4.0  /  3.0));
            final T b4 = h.multiply(theta2.multiply(theta.multiply(theta.multiply(                               125.0 / 8.0 ).add(-625.0 / 24.0)).add(375.0 / 32.0)).add(-125.0 / 96.0));
            final T b5 = h.multiply(theta2.multiply(theta.multiply(                                                                   5.0 / 12.0 ).add(-5.0  / 16.0)).add(  -5.0 / 48.0));
            for (int i = 0; i < interpolatedState.length; ++i) {
                final T yDot0 = yDotK[0][i];
                final T yDot2 = yDotK[2][i];
                final T yDot3 = yDotK[3][i];
                final T yDot4 = yDotK[4][i];
                final T yDot5 = yDotK[5][i];
                interpolatedState[i] = currentState[i].
                                       add(b0.multiply(yDot0)).
                                       add(b2.multiply(yDot2)).
                                       add(b3.multiply(yDot3)).
                                       add(b4.multiply(yDot4)).
                                       add(b5.multiply(yDot5));
         interpolatedDerivatives[i] =     bDot0.multiply(yDot0).
                                      add(bDot2.multiply(yDot2)).
                                      add(bDot3.multiply(yDot3)).
                                      add(bDot4.multiply(yDot4)).
                                      add(bDot5.multiply(yDot5));
            }
        }

        return new FieldODEStateAndDerivative<T>(time, interpolatedState, yDotK[0]);

    }

}
