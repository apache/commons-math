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
 * ODE integration for the 5(4) Dormand-Prince integrator.
 *
 * @see DormandPrince54Integrator
 *
 * @param <T> the type of the field elements
 * @since 3.6
 */

class DormandPrince54FieldStepInterpolator<T extends RealFieldElement<T>>
      extends RungeKuttaFieldStepInterpolator<T> {

    /** Last row of the Butcher-array internal weights, element 0. */
    private static final double A70 =    35.0 /  384.0;

    // element 1 is zero, so it is neither stored nor used

    /** Last row of the Butcher-array internal weights, element 2. */
    private static final double A72 =   500.0 / 1113.0;

    /** Last row of the Butcher-array internal weights, element 3. */
    private static final double A73 =   125.0 /  192.0;

    /** Last row of the Butcher-array internal weights, element 4. */
    private static final double A74 = -2187.0 / 6784.0;

    /** Last row of the Butcher-array internal weights, element 5. */
    private static final double A75 =    11.0 /   84.0;

    /** Shampine (1986) Dense output, element 0. */
    private static final double D0 =  -12715105075.0 /  11282082432.0;

    // element 1 is zero, so it is neither stored nor used

    /** Shampine (1986) Dense output, element 2. */
    private static final double D2 =   87487479700.0 /  32700410799.0;

    /** Shampine (1986) Dense output, element 3. */
    private static final double D3 =  -10690763975.0 /   1880347072.0;

    /** Shampine (1986) Dense output, element 4. */
    private static final double D4 =  701980252875.0 / 199316789632.0;

    /** Shampine (1986) Dense output, element 5. */
    private static final double D5 =   -1453857185.0 /    822651844.0;

    /** Shampine (1986) Dense output, element 6. */
    private static final double D6 =      69997945.0 /     29380423.0;

    /** First vector for interpolation. */
    private T[] v1;

    /** Second vector for interpolation. */
    private T[] v2;

    /** Third vector for interpolation. */
    private T[] v3;

    /** Fourth vector for interpolation. */
    private T[] v4;

    /** Initialization indicator for the interpolation vectors. */
    private boolean vectorsInitialized;

    /** Simple constructor.
     * This constructor builds an instance that is not usable yet, the
     * {@link #reinitialize} method should be called before using the
     * instance in order to initialize the internal arrays. This
     * constructor is used only in order to delay the initialization in
     * some cases. The {@link EmbeddedRungeKuttaIntegrator} uses the
     * prototyping design pattern to create the step interpolators by
     * cloning an uninitialized model and latter initializing the copy.
     */
    DormandPrince54FieldStepInterpolator() {
        super();
        v1 = null;
        v2 = null;
        v3 = null;
        v4 = null;
        vectorsInitialized = false;
    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    DormandPrince54FieldStepInterpolator(final DormandPrince54FieldStepInterpolator<T> interpolator) {

        super(interpolator);

        if (interpolator.v1 == null) {

            v1 = null;
            v2 = null;
            v3 = null;
            v4 = null;
            vectorsInitialized = false;

        } else {

            v1 = interpolator.v1.clone();
            v2 = interpolator.v2.clone();
            v3 = interpolator.v3.clone();
            v4 = interpolator.v4.clone();
            vectorsInitialized = interpolator.vectorsInitialized;

        }

    }

    /** {@inheritDoc} */
    @Override
    protected DormandPrince54FieldStepInterpolator<T> doCopy() {
        return new DormandPrince54FieldStepInterpolator<T>(this);
    }


    /** {@inheritDoc} */
    @Override
    protected void reinitialize(final T[] y, final boolean isForward, final FieldEquationsMapper<T> equationsMapper) {
        super.reinitialize(y, isForward, equationsMapper);
        v1 = null;
        v2 = null;
        v3 = null;
        v4 = null;
        vectorsInitialized = false;
    }

    /** {@inheritDoc} */
    @Override
    public void storeState(final FieldODEStateAndDerivative<T> state) {
        super.storeState(state);
        vectorsInitialized = false;
    }

    /** {@inheritDoc} */
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper,
                                                                                   final T time, final T theta,
                                                                                   final T oneMinusThetaH) {

        if (! vectorsInitialized) {

            if (v1 == null) {
                v1 = MathArrays.buildArray(time.getField(), previousState.length);
                v2 = MathArrays.buildArray(time.getField(), previousState.length);
                v3 = MathArrays.buildArray(time.getField(), previousState.length);
                v4 = MathArrays.buildArray(time.getField(), previousState.length);
            }

            // no step finalization is needed for this interpolator

            // we need to compute the interpolation vectors for this time step
            for (int i = 0; i < previousState.length; ++i) {
                final T yDot0 = yDotK[0][i];
                final T yDot2 = yDotK[2][i];
                final T yDot3 = yDotK[3][i];
                final T yDot4 = yDotK[4][i];
                final T yDot5 = yDotK[5][i];
                final T yDot6 = yDotK[6][i];
                v1[i] =     yDot0.multiply(A70).
                        add(yDot2.multiply(A72)).
                        add(yDot3.multiply(A73)).
                        add(yDot4.multiply(A74)).
                        add(yDot5.multiply(A75));
                v2[i] = yDot0.subtract(v1[i]);
                v3[i] = v1[i].subtract(v2[i]).subtract(yDot6);
                v4[i] =     yDot0.multiply(D0).
                        add(yDot2.multiply(D2)).
                        add(yDot3.multiply(D3)).
                        add(yDot4.multiply(D4)).
                        add(yDot5.multiply(D5)).
                        add(yDot6.multiply(D6));
            }

            vectorsInitialized = true;

        }

        // interpolate
        final T one      = theta.getField().getOne();
        final T eta      = one.subtract(theta);
        final T twoTheta = theta.multiply(2);
        final T dot2     = one.subtract(twoTheta);
        final T dot3     = theta.multiply(theta.multiply(-3).add(2));
        final T dot4     = twoTheta.multiply(theta.multiply(twoTheta.subtract(3)).add(1));
        final T[] interpolatedState       = MathArrays.buildArray(theta.getField(), previousState.length);
        final T[] interpolatedDerivatives = MathArrays.buildArray(theta.getField(), previousState.length);
        if ((previousState != null) && (theta.getReal() <= 0.5)) {
            for (int i = 0; i < interpolatedState.length; ++i) {
                interpolatedState[i] = previousState[i].
                                       add(theta.multiply(h.multiply(v1[i].
                                                                     add(eta.multiply(v2[i].
                                                                                      add(theta.multiply(v3[i].
                                                                                                         add(eta.multiply(v4[i])))))))));
                interpolatedDerivatives[i] =                   v1[i].
                                             add(dot2.multiply(v2[i])).
                                             add(dot3.multiply(v3[i])).
                                             add(dot4.multiply(v4[i]));
            }
        } else {
            for (int i = 0; i < interpolatedState.length; ++i) {
                interpolatedState[i] = currentState[i].
                                       subtract(oneMinusThetaH.multiply(v1[i].
                                                                        subtract(theta.multiply(v2[i].
                                                                                                add(theta.multiply(v3[i].
                                                                                                                   add(eta.multiply(v4[i]))))))));
                interpolatedDerivatives[i] =                   v1[i].
                                             add(dot2.multiply(v2[i])).
                                             add(dot3.multiply(v3[i])).
                                             add(dot4.multiply(v4[i]));
            }
        }

        return new FieldODEStateAndDerivative<T>(time, interpolatedState, yDotK[0]);

    }

}
