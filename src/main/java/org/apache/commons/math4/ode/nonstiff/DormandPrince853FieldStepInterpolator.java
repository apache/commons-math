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

import org.apache.commons.math4.Field;
import org.apache.commons.math4.RealFieldElement;
import org.apache.commons.math4.exception.MaxCountExceededException;
import org.apache.commons.math4.ode.AbstractFieldIntegrator;
import org.apache.commons.math4.ode.FieldEquationsMapper;
import org.apache.commons.math4.ode.FieldODEStateAndDerivative;
import org.apache.commons.math4.util.MathArrays;

/**
 * This class represents an interpolator over the last step during an
 * ODE integration for the 8(5,3) Dormand-Prince integrator.
 *
 * @see DormandPrince853FieldIntegrator
 *
 * @param <T> the type of the field elements
 * @since 3.6
 */

class DormandPrince853FieldStepInterpolator<T extends RealFieldElement<T>>
    extends RungeKuttaFieldStepInterpolator<T> {

    /** Propagation weights, element 1. */
    private final T b_01;

    // elements 2 to 5 are zero, so they are neither stored nor used

    /** Propagation weights, element 6. */
    private final T b_06;

    /** Propagation weights, element 7. */
    private final T b_07;

    /** Propagation weights, element 8. */
    private final T b_08;

    /** Propagation weights, element 9. */
    private final T b_09;

    /** Propagation weights, element 10. */
    private final T b_10;

    /** Propagation weights, element 11. */
    private final T b_11;

    /** Propagation weights, element 12. */
    private final T b_12;

    /** Time step for stage 14 (interpolation only). */
    private final T c14;

    /** Internal weights for stage 14, element 1. */
    private final T k14_01;

    // elements 2 to 5 are zero, so they are neither stored nor used

    /** Internal weights for stage 14, element 6. */
    private final T k14_06;

    /** Internal weights for stage 14, element 7. */
    private final T k14_07;

    /** Internal weights for stage 14, element 8. */
    private final T k14_08;

    /** Internal weights for stage 14, element 9. */
    private final T k14_09;

    /** Internal weights for stage 14, element 10. */
    private final T k14_10;

    /** Internal weights for stage 14, element 11. */
    private final T k14_11;

    /** Internal weights for stage 14, element 12. */
    private final T k14_12;

    /** Internal weights for stage 14, element 13. */
    private final T k14_13;

    /** Time step for stage 15 (interpolation only). */
    private final T c15;


    /** Internal weights for stage 15, element 1. */
    private final T k15_01;

    // elements 2 to 5 are zero, so they are neither stored nor used

    /** Internal weights for stage 15, element 6. */
    private final T k15_06;

    /** Internal weights for stage 15, element 7. */
    private final T k15_07;

    /** Internal weights for stage 15, element 8. */
    private final T k15_08;

    /** Internal weights for stage 15, element 9. */
    private final T k15_09;

    /** Internal weights for stage 15, element 10. */
    private final T k15_10;

    /** Internal weights for stage 15, element 11. */
    private final T k15_11;

    /** Internal weights for stage 15, element 12. */
    private final T k15_12;

    /** Internal weights for stage 15, element 13. */
    private final T k15_13;

    /** Internal weights for stage 15, element 14. */
    private final T k15_14;

    /** Time step for stage 16 (interpolation only). */
    private final T c16;


    /** Internal weights for stage 16, element 1. */
    private final T k16_01;

    // elements 2 to 5 are zero, so they are neither stored nor used

    /** Internal weights for stage 16, element 6. */
    private final T k16_06;

    /** Internal weights for stage 16, element 7. */
    private final T k16_07;

    /** Internal weights for stage 16, element 8. */
    private final T k16_08;

    /** Internal weights for stage 16, element 9. */
    private final T k16_09;

    /** Internal weights for stage 16, element 10. */
    private final T k16_10;

    /** Internal weights for stage 16, element 11. */
    private final T k16_11;

    /** Internal weights for stage 16, element 12. */
    private final T k16_12;

    /** Internal weights for stage 16, element 13. */
    private final T k16_13;

    /** Internal weights for stage 16, element 14. */
    private final T k16_14;

    /** Internal weights for stage 16, element 15. */
    private final T k16_15;

    /** Interpolation weights.
     * (beware that only the non-null values are in the table)
     */
    private final T[][] d;

    /** Last evaluations. */
    private T[][] yDotKLast;

    /** Vectors for interpolation. */
    private T[][] v;

    /** Initialization indicator for the interpolation vectors. */
    private boolean vectorsInitialized;

    /** Simple constructor.
     * @param rkIntegrator integrator being used
     * @param y reference to the integrator array holding the state at
     * the end of the step
     * @param yDotArray reference to the integrator array holding all the
     * intermediate slopes
     * @param forward integration direction indicator
     * @param mapper equations mapper for the all equations
     */
    DormandPrince853FieldStepInterpolator(final AbstractFieldIntegrator<T> rkIntegrator,
                                          final T[] y, final T[][] yDotArray, final boolean forward,
                                          final FieldEquationsMapper<T> mapper) {
        super(rkIntegrator, y, yDotArray, forward, mapper);
        yDotKLast = null;
        v         = null;
        vectorsInitialized = false;

        b_01   = fraction(        104257.0, 1920240.0);
        b_06   = fraction(       3399327.0, 763840.0);
        b_07   = fraction(      66578432.0, 35198415.0);
        b_08   = fraction(   -1674902723.0, 288716400.0);
        b_09   = fraction(54980371265625.0, 176692375811392.0);
        b_10   = fraction(       -734375.0, 4826304.0);
        b_11   = fraction(     171414593.0, 851261400.0);
        b_12   = fraction(        137909.0, 3084480.0);
        c14    = fraction(1.0, 10.0);
        k14_01 = fraction(      13481885573.0, 240030000000.0)     .subtract(b_01);
        k14_06 = integrator.getField().getZero()                   .subtract(b_06);
        k14_07 = fraction(     139418837528.0, 549975234375.0)     .subtract(b_07);
        k14_08 = fraction(  -11108320068443.0, 45111937500000.0)   .subtract(b_08);
        k14_09 = fraction(-1769651421925959.0, 14249385146080000.0).subtract(b_09);
        k14_10 = fraction(         57799439.0, 377055000.0)        .subtract(b_10);
        k14_11 = fraction(     793322643029.0, 96734250000000.0)   .subtract(b_11);
        k14_12 = fraction(       1458939311.0, 192780000000.0)     .subtract(b_12);
        k14_13 = fraction(            -4149.0, 500000.0);
        c15    = fraction(1.0, 5.0);
        k15_01 = fraction(    1595561272731.0, 50120273500000.0)   .subtract(b_01);
        k15_06 = fraction(     975183916491.0, 34457688031250.0)   .subtract(b_06);
        k15_07 = fraction(   38492013932672.0, 718912673015625.0)  .subtract(b_07);
        k15_08 = fraction(-1114881286517557.0, 20298710767500000.0).subtract(b_08);
        k15_09 = integrator.getField().getZero()                   .subtract(b_09);
        k15_10 = integrator.getField().getZero()                   .subtract(b_10);
        k15_11 = fraction(   -2538710946863.0, 23431227861250000.0).subtract(b_11);
        k15_12 = fraction(       8824659001.0, 23066716781250.0)   .subtract(b_12);
        k15_13 = fraction(     -11518334563.0, 33831184612500.0);
        k15_14 = fraction(       1912306948.0, 13532473845.0);
        c16    = fraction(7.0, 9.0);
        k16_01 = fraction(     -13613986967.0, 31741908048.0)      .subtract(b_01);
        k16_06 = fraction(      -4755612631.0, 1012344804.0)       .subtract(b_06);
        k16_07 = fraction(   42939257944576.0, 5588559685701.0)    .subtract(b_07);
        k16_08 = fraction(   77881972900277.0, 19140370552944.0)   .subtract(b_08);
        k16_09 = fraction(   22719829234375.0, 63689648654052.0)   .subtract(b_09);
        k16_10 = integrator.getField().getZero()                   .subtract(b_10);
        k16_11 = integrator.getField().getZero()                   .subtract(b_11);
        k16_12 = integrator.getField().getZero()                   .subtract(b_12);
        k16_13 = fraction(      -1199007803.0, 857031517296.0);
        k16_14 = fraction(     157882067000.0, 53564469831.0);
        k16_15 = fraction(    -290468882375.0, 31741908048.0);

        /** Interpolation weights.
         * (beware that only the non-null values are in the table)
         */
        d = MathArrays.buildArray(integrator.getField(), 4, 12);

         d[0][ 0] = fraction(        -17751989329.0, 2106076560.0);
         d[0][ 1] = fraction(          4272954039.0, 7539864640.0);
         d[0][ 2] = fraction(       -118476319744.0, 38604839385.0);
         d[0][ 3] = fraction(        755123450731.0, 316657731600.0);
         d[0][ 4] = fraction( 3692384461234828125.0, 1744130441634250432.0);
         d[0][ 5] = fraction(         -4612609375.0, 5293382976.0);
         d[0][ 6] = fraction(       2091772278379.0, 933644586600.0);
         d[0][ 7] = fraction(          2136624137.0, 3382989120.0);
         d[0][ 8] = fraction(             -126493.0, 1421424.0);
         d[0][ 9] = fraction(            98350000.0, 5419179.0);
         d[0][10] = fraction(           -18878125.0, 2053168.0);
         d[0][11] = fraction(         -1944542619.0, 438351368.0);

         d[1][ 0] = fraction(         32941697297.0, 3159114840.0);
         d[1][ 1] = fraction(        456696183123.0, 1884966160.0);
         d[1][ 2] = fraction(      19132610714624.0, 115814518155.0);
         d[1][ 3] = fraction(    -177904688592943.0, 474986597400.0);
         d[1][ 4] = fraction(-4821139941836765625.0, 218016305204281304.0);
         d[1][ 5] = fraction(         30702015625.0, 3970037232.0);
         d[1][ 6] = fraction(     -85916079474274.0, 2800933759800.0);
         d[1][ 7] = fraction(         -5919468007.0, 634310460.0);
         d[1][ 8] = fraction(             2479159.0, 157936.0);
         d[1][ 9] = fraction(           -18750000.0, 602131.0);
         d[1][10] = fraction(           -19203125.0, 2053168.0);
         d[1][11] = fraction(         15700361463.0, 438351368.0);

         d[2][ 0] = fraction(         12627015655.0, 631822968.0);
         d[2][ 1] = fraction(        -72955222965.0, 188496616.0);
         d[2][ 2] = fraction(     -13145744952320.0, 69488710893.0);
         d[2][ 3] = fraction(      30084216194513.0, 56998391688.0);
         d[2][ 4] = fraction( -296858761006640625.0, 25648977082856624.0);
         d[2][ 5] = fraction(           569140625.0, 82709109.0);
         d[2][ 6] = fraction(        -18684190637.0, 18672891732.0);
         d[2][ 7] = fraction(            69644045.0, 89549712.0);
         d[2][ 8] = fraction(           -11847025.0, 4264272.0);
         d[2][ 9] = fraction(          -978650000.0, 16257537.0);
         d[2][10] = fraction(           519371875.0, 6159504.0);
         d[2][11] = fraction(          5256837225.0, 438351368.0);

         d[3][ 0] = fraction(          -450944925.0, 17550638.0);
         d[3][ 1] = fraction(        -14532122925.0, 94248308.0);
         d[3][ 2] = fraction(       -595876966400.0, 2573655959.0);
         d[3][ 3] = fraction(        188748653015.0, 527762886.0);
         d[3][ 4] = fraction( 2545485458115234375.0, 27252038150535163.0);
         d[3][ 5] = fraction(         -1376953125.0, 36759604.0);
         d[3][ 6] = fraction(         53995596795.0, 518691437.0);
         d[3][ 7] = fraction(           210311225.0, 7047894.0);
         d[3][ 8] = fraction(            -1718875.0, 39484.0);
         d[3][ 9] = fraction(            58000000.0, 602131.0);
         d[3][10] = fraction(            -1546875.0, 39484.0);
         d[3][11] = fraction(         -1262172375.0, 8429834.0);

    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    DormandPrince853FieldStepInterpolator(final DormandPrince853FieldStepInterpolator<T> interpolator) {

        super(interpolator);

        if (interpolator.currentState == null) {

            yDotKLast = null;
            v         = null;
            vectorsInitialized = false;

        } else {

            final int dimension = interpolator.currentState.length;
            final Field<T> field = interpolator.getGlobalPreviousState().getTime().getField();

            yDotKLast = MathArrays.buildArray(field, 3, dimension);
            for (int k = 0; k < yDotKLast.length; ++k) {
                System.arraycopy(interpolator.yDotKLast[k], 0, yDotKLast[k], 0,
                                 dimension);
            }

            v = MathArrays.buildArray(field, 7, dimension);
            for (int k = 0; k < v.length; ++k) {
                System.arraycopy(interpolator.v[k], 0, v[k], 0, dimension);
            }

            vectorsInitialized = interpolator.vectorsInitialized;

        }

        b_01   = interpolator.b_01;
        b_06   = interpolator.b_06;
        b_07   = interpolator.b_07;
        b_08   = interpolator.b_08;
        b_09   = interpolator.b_09;
        b_10   = interpolator.b_10;
        b_11   = interpolator.b_11;
        b_12   = interpolator.b_12;
        c14    = interpolator.c14;
        k14_01 = interpolator.k14_01;
        k14_06 = interpolator.k14_06;
        k14_07 = interpolator.k14_07;
        k14_08 = interpolator.k14_08;
        k14_09 = interpolator.k14_09;
        k14_10 = interpolator.k14_10;
        k14_11 = interpolator.k14_11;
        k14_12 = interpolator.k14_12;
        k14_13 = interpolator.k14_13;
        c15    = interpolator.c15;
        k15_01 = interpolator.k15_01;
        k15_06 = interpolator.k15_06;
        k15_07 = interpolator.k15_07;
        k15_08 = interpolator.k15_08;
        k15_09 = interpolator.k15_09;
        k15_10 = interpolator.k15_10;
        k15_11 = interpolator.k15_11;
        k15_12 = interpolator.k15_12;
        k15_13 = interpolator.k15_13;
        k15_14 = interpolator.k15_14;
        c16    = interpolator.c16;
        k16_01 = interpolator.k16_01;
        k16_06 = interpolator.k16_06;
        k16_07 = interpolator.k16_07;
        k16_08 = interpolator.k16_08;
        k16_09 = interpolator.k16_09;
        k16_10 = interpolator.k16_10;
        k16_11 = interpolator.k16_11;
        k16_12 = interpolator.k16_12;
        k16_13 = interpolator.k16_13;
        k16_14 = interpolator.k16_14;
        k16_15 = interpolator.k16_15;

        d = MathArrays.buildArray(integrator.getField(), 4, -1);
        for (int i = 0; i < d.length; ++i) {
            d[i] = interpolator.d[i].clone();
        }

    }

    /** Create a fraction.
     * @param p numerator
     * @param q denominator
     * @return p/q computed in the instance field
     */
    private T fraction(final double p, final double q) {
        return integrator.getField().getOne().multiply(p).divide(q);
    }

    /** {@inheritDoc} */
    @Override
    protected DormandPrince853FieldStepInterpolator<T> doCopy() {
        return new DormandPrince853FieldStepInterpolator<T>(this);
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
                                                                                   final T oneMinusThetaH)
        throws MaxCountExceededException {

        if (! vectorsInitialized) {

            if (v == null) {
                v = MathArrays.buildArray(time.getField(), 7, previousState.length);
            }

            // perform the last evaluations if they have not been done yet
            finalizeStep();

            // compute the interpolation vectors for this time step
            for (int i = 0; i < previousState.length; ++i) {
                final T yDot01 = yDotK[0][i];
                final T yDot06 = yDotK[5][i];
                final T yDot07 = yDotK[6][i];
                final T yDot08 = yDotK[7][i];
                final T yDot09 = yDotK[8][i];
                final T yDot10 = yDotK[9][i];
                final T yDot11 = yDotK[10][i];
                final T yDot12 = yDotK[11][i];
                final T yDot13 = yDotK[12][i];
                final T yDot14 = yDotKLast[0][i];
                final T yDot15 = yDotKLast[1][i];
                final T yDot16 = yDotKLast[2][i];
                v[0][i] =     yDot01.multiply(b_01).
                          add(yDot06.multiply(b_06)).
                          add(yDot07.multiply(b_07)).
                          add(yDot08.multiply(b_08)).
                          add(yDot09.multiply(b_09)).
                          add(yDot10.multiply(b_10)).
                          add(yDot11.multiply(b_11)).
                          add(yDot12.multiply(b_12));
                v[1][i] = yDot01.subtract(v[0][i]);
                v[2][i] = v[0][i].subtract(v[1][i]).subtract(yDotK[12][i]);
                for (int k = 0; k < d.length; ++k) {
                    v[k+3][i] =     yDot01.multiply(d[k][ 0]).
                                add(yDot06.multiply(d[k][ 1])).
                                add(yDot07.multiply(d[k][ 2])).
                                add(yDot08.multiply(d[k][ 3])).
                                add(yDot09.multiply(d[k][ 4])).
                                add(yDot10.multiply(d[k][ 5])).
                                add(yDot11.multiply(d[k][ 6])).
                                add(yDot12.multiply(d[k][ 7])).
                                add(yDot13.multiply(d[k][ 8])).
                                add(yDot14.multiply(d[k][ 9])).
                                add(yDot15.multiply(d[k][10])).
                                add(yDot16.multiply(d[k][11]));
                }
            }

            vectorsInitialized = true;

        }

        final T one      = theta.getField().getOne();
        final T eta      = one.subtract(theta);
        final T twoTheta = theta.multiply(2);
        final T theta2   = theta.multiply(theta);
        final T dot1     = one.subtract(twoTheta);
        final T dot2     = theta.multiply(theta.multiply(-3).add(2));
        final T dot3     = twoTheta.multiply(theta.multiply(twoTheta.subtract(3)).add(1));
        final T dot4     = theta2.multiply(theta.multiply(theta.multiply(5).subtract(8)).add(3));
        final T dot5     = theta2.multiply(theta.multiply(theta.multiply(theta.multiply(-6).add(15)).subtract(12)).add(3));
        final T dot6     = theta2.multiply(theta.multiply(theta.multiply(theta.multiply(theta.multiply(-7).add(18)).subtract(15)).add(4)));
        final T[] interpolatedState       = MathArrays.buildArray(theta.getField(), previousState.length);
        final T[] interpolatedDerivatives = MathArrays.buildArray(theta.getField(), previousState.length);

        if ((previousState != null) && (theta.getReal() <= 0.5)) {
            for (int i = 0; i < interpolatedState.length; ++i) {
                interpolatedState[i] = previousState[i].
                                       add(theta.multiply(h.multiply(v[0][i].
                                                          add(eta.multiply(v[1][i].
                                                                           add(theta.multiply(v[2][i].
                                                                                              add(eta.multiply(v[3][i].
                                                                                                               add(theta.multiply(v[4][i].
                                                                                                                   add(eta.multiply(v[5][i].
                                                                                                                                    add(theta.multiply(v[6][i])))))))))))))));
                interpolatedDerivatives[i] =                   v[0][i].
                                             add(dot1.multiply(v[1][i])).
                                             add(dot2.multiply(v[2][i])).
                                             add(dot3.multiply(v[3][i])).
                                             add(dot4.multiply(v[4][i])).
                                             add(dot5.multiply(v[5][i])).
                                             add(dot6.multiply(v[6][i]));
            }
        } else {
            for (int i = 0; i < interpolatedState.length; ++i) {
                interpolatedState[i] = currentState[i].
                                       subtract(oneMinusThetaH.multiply(v[0][i].
                                                                        subtract(theta.multiply(v[1][i].
                                                                                                add(theta.multiply(v[2][i].
                                                                                                                   add(eta.multiply(v[3][i].
                                                                                                                                    add(theta.multiply(v[4][i].
                                                                                                                                                       add(eta.multiply(v[5][i].
                                                                                                                                                                        add(theta.multiply(v[6][i]))))))))))))));
                interpolatedDerivatives[i] =                   v[0][i].
                                             add(dot1.multiply(v[1][i])).
                                             add(dot2.multiply(v[2][i])).
                                             add(dot3.multiply(v[3][i])).
                                             add(dot4.multiply(v[4][i])).
                                             add(dot5.multiply(v[5][i])).
                                             add(dot6.multiply(v[6][i]));
            }
        }

        return new FieldODEStateAndDerivative<T>(time, interpolatedState, yDotK[0]);

    }

    /** {@inheritDoc} */
    @Override
    protected void doFinalize() throws MaxCountExceededException {

        if (currentState == null) {
            // we are finalizing an uninitialized instance
            return;
        }

        T s;
        final T   pT   = getGlobalPreviousState().getTime();
        final T[] yTmp = MathArrays.buildArray(pT.getField(), currentState.length);

        // k14
        for (int j = 0; j < currentState.length; ++j) {
            s =     yDotK[ 0][j].multiply(k14_01).
                add(yDotK[ 5][j].multiply(k14_06)).
                add(yDotK[ 6][j].multiply(k14_07)).
                add(yDotK[ 7][j].multiply(k14_08)).
                add(yDotK[ 8][j].multiply(k14_09)).
                add(yDotK[ 9][j].multiply(k14_10)).
                add(yDotK[10][j].multiply(k14_11)).
                add(yDotK[11][j].multiply(k14_12)).
                add(yDotK[12][j].multiply(k14_13));
            yTmp[j] = currentState[j].add(h.multiply(s));
        }
        yDotKLast[0] = integrator.computeDerivatives(pT.add(h.multiply(c14)), yTmp);

        // k15
        for (int j = 0; j < currentState.length; ++j) {
            s =     yDotK[ 0][j].multiply(k15_01).
                add(yDotK[ 5][j].multiply(k15_06)).
                add(yDotK[ 6][j].multiply(k15_07)).
                add(yDotK[ 7][j].multiply(k15_08)).
                add(yDotK[ 8][j].multiply(k15_09)).
                add(yDotK[ 9][j].multiply(k15_10)).
                add(yDotK[10][j].multiply(k15_11)).
                add(yDotK[11][j].multiply(k15_12)).
                add(yDotK[12][j].multiply(k15_13)).
                add(yDotKLast[0][j].multiply(k15_14));
            yTmp[j] = currentState[j].add(h.multiply(s));
        }
        yDotKLast[1] = integrator.computeDerivatives(pT.add(h.multiply(c15)), yTmp);

        // k16
        for (int j = 0; j < currentState.length; ++j) {
            s =     yDotK[ 0][j].multiply(k16_01).
                add(yDotK[ 5][j].multiply(k16_06)).
                add(yDotK[ 6][j].multiply(k16_07)).
                add(yDotK[ 7][j].multiply(k16_08)).
                add(yDotK[ 8][j].multiply(k16_09)).
                add(yDotK[ 9][j].multiply(k16_10)).
                add(yDotK[10][j].multiply(k16_11)).
                add(yDotK[11][j].multiply(k16_12)).
                add(yDotK[12][j].multiply(k16_13)).
                add(yDotKLast[0][j].multiply(k16_14)).
                add(yDotKLast[0][j].multiply(k16_15));
            yTmp[j] = currentState[j].add(h.multiply(s));
        }
        yDotKLast[2] = integrator.computeDerivatives(pT.add(h.multiply(c16)), yTmp);

    }

}
