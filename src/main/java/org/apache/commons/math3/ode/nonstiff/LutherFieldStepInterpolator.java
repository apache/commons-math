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
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

/**
 * This class represents an interpolator over the last step during an
 * ODE integration for the 6th order Luther integrator.
 *
 * <p>This interpolator computes dense output inside the last
 * step computed. The interpolation equation is consistent with the
 * integration scheme.</p>
 *
 * @see LutherFieldIntegrator
 * @param <T> the type of the field elements
 * @since 3.6
 */

class LutherFieldStepInterpolator<T extends RealFieldElement<T>>
    extends RungeKuttaFieldStepInterpolator<T> {

    /** -49 - 49 q. */
    private final T c5a;

    /** 392 + 287 q. */
    private final T c5b;

    /** -637 - 357 q. */
    private final T c5c;

    /** 833 + 343 q. */
    private final T c5d;

    /** -49 + 49 q. */
    private final T c6a;

    /** -392 - 287 q. */
    private final T c6b;

    /** -637 + 357 q. */
    private final T c6c;

    /** 833 - 343 q. */
    private final T c6d;

    /** 49 + 49 q. */
    private final T d5a;

    /** -1372 - 847 q. */
    private final T d5b;

    /** 2254 + 1029 q */
    private final T d5c;

    /** 49 - 49 q. */
    private final T d6a;

    /** -1372 + 847 q. */
    private final T d6b;

    /** 2254 - 1029 q */
    private final T d6c;

    /** Simple constructor.
     * @param rkIntegrator integrator being used
     * @param y reference to the integrator array holding the state at
     * the end of the step
     * @param yDotArray reference to the integrator array holding all the
     * intermediate slopes
     * @param forward integration direction indicator
     * @param mapper equations mapper for the all equations
     */
    LutherFieldStepInterpolator(final AbstractFieldIntegrator<T> rkIntegrator,
                                final T[] y, final T[][] yDotArray, final boolean forward,
                                final FieldEquationsMapper<T> mapper) {
        super(rkIntegrator, y, yDotArray, forward, mapper);
        final T q = rkIntegrator.getField().getOne().multiply(21).sqrt();
        c5a = q.multiply(  -49).add(  -49);
        c5b = q.multiply(  287).add(  392);
        c5c = q.multiply( -357).add( -637);
        c5d = q.multiply(  343).add(  833);
        c6a = q.multiply(   49).add(  -49);
        c6b = q.multiply( -287).add( -392);
        c6c = q.multiply(  357).add( -637);
        c6d = q.multiply( -343).add(  833);
        d5a = q.multiply(   49).add(   49);
        d5b = q.multiply( -847).add(-1372);
        d5c = q.multiply( 1029).add( 2254);
        d6a = q.multiply(  -49).add(   49);
        d6b = q.multiply(  847).add(-1372);
        d6c = q.multiply(-1029).add( 2254);

    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    LutherFieldStepInterpolator(final LutherFieldStepInterpolator<T> interpolator) {
        super(interpolator);
        c5a = interpolator.c5a;
        c5b = interpolator.c5b;
        c5c = interpolator.c5c;
        c5d = interpolator.c5d;
        c6a = interpolator.c6a;
        c6b = interpolator.c6b;
        c6c = interpolator.c6c;
        c6d = interpolator.c6d;
        d5a = interpolator.d5a;
        d5b = interpolator.d5b;
        d5c = interpolator.d5c;
        d6a = interpolator.d6a;
        d6b = interpolator.d6b;
        d6c = interpolator.d6c;
    }

    /** {@inheritDoc} */
    @Override
    protected LutherFieldStepInterpolator<T> doCopy() {
        return new LutherFieldStepInterpolator<T>(this);
    }


    /** {@inheritDoc} */
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper,
                                                                                   final T time, final T theta,
                                                                                   final T oneMinusThetaH) {

        // the coefficients below have been computed by solving the
        // order conditions from a theorem from Butcher (1963), using
        // the method explained in Folkmar Bornemann paper "Runge-Kutta
        // Methods, Trees, and Maple", Center of Mathematical Sciences, Munich
        // University of Technology, February 9, 2001
        //<http://wwwzenger.informatik.tu-muenchen.de/selcuk/sjam012101.html>

        // the method is implemented in the rkcheck tool
        // <https://www.spaceroots.org/software/rkcheck/index.html>.
        // Running it for order 5 gives the following order conditions
        // for an interpolator:
        // order 1 conditions
        // \sum_{i=1}^{i=s}\left(b_{i} \right) =1
        // order 2 conditions
        // \sum_{i=1}^{i=s}\left(b_{i} c_{i}\right) = \frac{\theta}{2}
        // order 3 conditions
        // \sum_{i=2}^{i=s}\left(b_{i} \sum_{j=1}^{j=i-1}{\left(a_{i,j} c_{j} \right)}\right) = \frac{\theta^{2}}{6}
        // \sum_{i=1}^{i=s}\left(b_{i} c_{i}^{2}\right) = \frac{\theta^{2}}{3}
        // order 4 conditions
        // \sum_{i=3}^{i=s}\left(b_{i} \sum_{j=2}^{j=i-1}{\left(a_{i,j} \sum_{k=1}^{k=j-1}{\left(a_{j,k} c_{k} \right)} \right)}\right) = \frac{\theta^{3}}{24}
        // \sum_{i=2}^{i=s}\left(b_{i} \sum_{j=1}^{j=i-1}{\left(a_{i,j} c_{j}^{2} \right)}\right) = \frac{\theta^{3}}{12}
        // \sum_{i=2}^{i=s}\left(b_{i} c_{i}\sum_{j=1}^{j=i-1}{\left(a_{i,j} c_{j} \right)}\right) = \frac{\theta^{3}}{8}
        // \sum_{i=1}^{i=s}\left(b_{i} c_{i}^{3}\right) = \frac{\theta^{3}}{4}
        // order 5 conditions
        // \sum_{i=4}^{i=s}\left(b_{i} \sum_{j=3}^{j=i-1}{\left(a_{i,j} \sum_{k=2}^{k=j-1}{\left(a_{j,k} \sum_{l=1}^{l=k-1}{\left(a_{k,l} c_{l} \right)} \right)} \right)}\right) = \frac{\theta^{4}}{120}
        // \sum_{i=3}^{i=s}\left(b_{i} \sum_{j=2}^{j=i-1}{\left(a_{i,j} \sum_{k=1}^{k=j-1}{\left(a_{j,k} c_{k}^{2} \right)} \right)}\right) = \frac{\theta^{4}}{60}
        // \sum_{i=3}^{i=s}\left(b_{i} \sum_{j=2}^{j=i-1}{\left(a_{i,j} c_{j}\sum_{k=1}^{k=j-1}{\left(a_{j,k} c_{k} \right)} \right)}\right) = \frac{\theta^{4}}{40}
        // \sum_{i=2}^{i=s}\left(b_{i} \sum_{j=1}^{j=i-1}{\left(a_{i,j} c_{j}^{3} \right)}\right) = \frac{\theta^{4}}{20}
        // \sum_{i=3}^{i=s}\left(b_{i} c_{i}\sum_{j=2}^{j=i-1}{\left(a_{i,j} \sum_{k=1}^{k=j-1}{\left(a_{j,k} c_{k} \right)} \right)}\right) = \frac{\theta^{4}}{30}
        // \sum_{i=2}^{i=s}\left(b_{i} c_{i}\sum_{j=1}^{j=i-1}{\left(a_{i,j} c_{j}^{2} \right)}\right) = \frac{\theta^{4}}{15}
        // \sum_{i=2}^{i=s}\left(b_{i} \left(\sum_{j=1}^{j=i-1}{\left(a_{i,j} c_{j} \right)} \right)^{2}\right) = \frac{\theta^{4}}{20}
        // \sum_{i=2}^{i=s}\left(b_{i} c_{i}^{2}\sum_{j=1}^{j=i-1}{\left(a_{i,j} c_{j} \right)}\right) = \frac{\theta^{4}}{10}
        // \sum_{i=1}^{i=s}\left(b_{i} c_{i}^{4}\right) = \frac{\theta^{4}}{5}

        // The a_{j,k} and c_{k} are given by the integrator Butcher arrays. What remains to solve
        // are the b_i for the interpolator. They are found by solving the above equations.
        // For a given interpolator, some equations are redundant, so in our case when we select
        // all equations from order 1 to 4, we still don't have enough independent equations
        // to solve from b_1 to b_7. We need to also select one equation from order 5. Here,
        // we selected the last equation. It appears this choice implied at least the last 3 equations
        // are fulfilled, but some of the former ones are not, so the resulting interpolator is order 5.
        // At the end, we get the b_i as polynomials in theta.

        final T coeffDot1 =  theta.multiply(theta.multiply(theta.multiply(theta.multiply(   21        ).add( -47          )).add(   36         )).add( -54     /   5.0)).add(1);
        // not really needed as it is zero: final T coeffDot2 =  theta.getField().getZero();
        final T coeffDot3 =  theta.multiply(theta.multiply(theta.multiply(theta.multiply(  112        ).add(-608    /  3.0)).add(  320   / 3.0 )).add(-208    /  15.0));
        final T coeffDot4 =  theta.multiply(theta.multiply(theta.multiply(theta.multiply( -567  /  5.0).add( 972    /  5.0)).add( -486   / 5.0 )).add( 324    /  25.0));
        final T coeffDot5 =  theta.multiply(theta.multiply(theta.multiply(theta.multiply(c5a.divide(5)).add(c5b.divide(15))).add(c5c.divide(30))).add(c5d.divide(150)));
        final T coeffDot6 =  theta.multiply(theta.multiply(theta.multiply(theta.multiply(c6a.divide(5)).add(c6b.divide(15))).add(c6c.divide(30))).add(c6d.divide(150)));
        final T coeffDot7 =  theta.multiply(theta.multiply(theta.multiply(                                              3 )).add(   -3         )).add(   3   /   5.0);
        final T[] interpolatedState       = MathArrays.buildArray(theta.getField(), previousState.length);
        final T[] interpolatedDerivatives = MathArrays.buildArray(theta.getField(), previousState.length);

        if ((previousState != null) && (theta.getReal() <= 0.5)) {

            final T coeff1    = theta.multiply(theta.multiply(theta.multiply(theta.multiply(  21    /  5.0).add( -47    /  4.0)).add(   12         )).add( -27    /   5.0)).add(1);
            // not really needed as it is zero: final T coeff2    =  theta.getField().getZero();
            final T coeff3    = theta.multiply(theta.multiply(theta.multiply(theta.multiply( 112    /  5.0).add(-152    /  3.0)).add(  320   / 9.0 )).add(-104    /  15.0));
            final T coeff4    = theta.multiply(theta.multiply(theta.multiply(theta.multiply(-567    / 25.0).add( 243    /  5.0)).add( -162   / 5.0 )).add( 162    /  25.0));
            final T coeff5    = theta.multiply(theta.multiply(theta.multiply(theta.multiply(c5a.divide(25)).add(c5b.divide(60))).add(c5c.divide(90))).add(c5d.divide(300)));
            final T coeff6    = theta.multiply(theta.multiply(theta.multiply(theta.multiply(c5a.divide(25)).add(c6b.divide(60))).add(c6c.divide(90))).add(c6d.divide(300)));
            final T coeff7    = theta.multiply(theta.multiply(theta.multiply(                              3            /  4.0)).add(   -1         )).add(   3     /  10.0);
            for (int i = 0; i < interpolatedState.length; ++i) {
                final T yDot1 = yDotK[0][i];
                // not really needed as associated coefficients are zero: final T yDot2 = yDotK[1][i];
                final T yDot3 = yDotK[2][i];
                final T yDot4 = yDotK[3][i];
                final T yDot5 = yDotK[4][i];
                final T yDot6 = yDotK[5][i];
                final T yDot7 = yDotK[6][i];
                interpolatedState[i] = previousState[i].
                                add(theta.multiply(h).
                                    multiply(    coeff1.multiply(yDot1).
                                             // not really needed as it is zero: add(coeff2.multiply(yDot2)).
                                             add(coeff3.multiply(yDot3)).
                                             add(coeff4.multiply(yDot4)).
                                             add(coeff5.multiply(yDot5)).
                                             add(coeff6.multiply(yDot6)).
                                             add(coeff7.multiply(yDot7))));
                interpolatedDerivatives[i] =     coeffDot1.multiply(yDot1).
                                             // not really needed as it is zero: add(coeffDot2.multiply(yDot2)).
                                             add(coeffDot3.multiply(yDot3)).
                                             add(coeffDot4.multiply(yDot4)).
                                             add(coeffDot5.multiply(yDot5)).
                                             add(coeffDot6.multiply(yDot6)).
                                             add(coeffDot7.multiply(yDot7));
            }
        } else {

            final T coeff1    = theta.multiply(theta.multiply(theta.multiply(theta.multiply( -21   /   5.0).add(   151  /  20.0)).add(  -89   /  20.0)).add(  19 /  20.0)).add( -1 /  20.0);
            // not really needed as it is zero: final T coeff2    =  theta.getField().getZero();
            final T coeff3    = theta.multiply(theta.multiply(theta.multiply(theta.multiply(-112   /   5.0).add(   424  /  15.0)).add( -328   /  45.0)).add( -16 /  45.0)).add(-16 /  45.0);
            final T coeff4    = theta.multiply(theta.multiply(theta.multiply(theta.multiply( 567   /  25.0).add(  -648  /  25.0)).add(  162   /  25.0)));
            final T coeff5    = theta.multiply(theta.multiply(theta.multiply(theta.multiply(d5a.divide(25)).add(d5b.divide(300))).add(d5c.divide(900))).add( -49 / 180.0)).add(-49 / 180.0);
            final T coeff6    = theta.multiply(theta.multiply(theta.multiply(theta.multiply(d6a.divide(25)).add(d6b.divide(300))).add(d6c.divide(900))).add( -49 / 180.0)).add(-49 / 180.0);
            final T coeff7    = theta.multiply(theta.multiply(theta.multiply(                             -3            /   4.0 ).add(    1   /   4.0)).add(  -1 /  20.0)).add( -1 /  20.0);
            for (int i = 0; i < interpolatedState.length; ++i) {
                final T yDot1 = yDotK[0][i];
                // not really needed as associated coefficients are zero: final T yDot2 = yDotK[1][i];
                final T yDot3 = yDotK[2][i];
                final T yDot4 = yDotK[3][i];
                final T yDot5 = yDotK[4][i];
                final T yDot6 = yDotK[5][i];
                final T yDot7 = yDotK[6][i];
                interpolatedState[i] = currentState[i].
                                add(oneMinusThetaH.
                                    multiply(    coeff1.multiply(yDot1).
                                             // not really needed as it is zero: add(coeff2.multiply(yDot2)).
                                             add(coeff3.multiply(yDot3)).
                                             add(coeff4.multiply(yDot4)).
                                             add(coeff5.multiply(yDot5)).
                                             add(coeff6.multiply(yDot6)).
                                             add(coeff7.multiply(yDot7))));
                interpolatedDerivatives[i] =     coeffDot1.multiply(yDot1).
                                             // not really needed as it is zero: add(coeffDot2.multiply(yDot2)).
                                             add(coeffDot3.multiply(yDot3)).
                                             add(coeffDot4.multiply(yDot4)).
                                             add(coeffDot5.multiply(yDot5)).
                                             add(coeffDot6.multiply(yDot6)).
                                             add(coeffDot7.multiply(yDot7));
            }
        }

        return new FieldODEStateAndDerivative<T>(time, interpolatedState, yDotK[0]);

    }

}
