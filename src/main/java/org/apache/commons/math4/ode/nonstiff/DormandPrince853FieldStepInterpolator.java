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
    private static final double B_01 =         104257.0 / 1920240.0;

    // elements 2 to 5 are zero, so they are neither stored nor used

    /** Propagation weights, element 6. */
    private static final double B_06 =        3399327.0 / 763840.0;

    /** Propagation weights, element 7. */
    private static final double B_07 =       66578432.0 / 35198415.0;

    /** Propagation weights, element 8. */
    private static final double B_08 =    -1674902723.0 / 288716400.0;

    /** Propagation weights, element 9. */
    private static final double B_09 = 54980371265625.0 / 176692375811392.0;

    /** Propagation weights, element 10. */
    private static final double B_10 =        -734375.0 / 4826304.0;

    /** Propagation weights, element 11. */
    private static final double B_11 =      171414593.0 / 851261400.0;

    /** Propagation weights, element 12. */
    private static final double B_12 =         137909.0 / 3084480.0;

    /** Time step for stage 14 (interpolation only). */
    private static final double C14    = 1.0 / 10.0;

    /** Internal weights for stage 14, element 1. */
    private static final double K14_01 =       13481885573.0 / 240030000000.0      - B_01;

    // elements 2 to 5 are zero, so they are neither stored nor used

    /** Internal weights for stage 14, element 6. */
    private static final double K14_06 =                 0.0                       - B_06;

    /** Internal weights for stage 14, element 7. */
    private static final double K14_07 =      139418837528.0 / 549975234375.0      - B_07;

    /** Internal weights for stage 14, element 8. */
    private static final double K14_08 =   -11108320068443.0 / 45111937500000.0    - B_08;

    /** Internal weights for stage 14, element 9. */
    private static final double K14_09 = -1769651421925959.0 / 14249385146080000.0 - B_09;

    /** Internal weights for stage 14, element 10. */
    private static final double K14_10 =          57799439.0 / 377055000.0         - B_10;

    /** Internal weights for stage 14, element 11. */
    private static final double K14_11 =      793322643029.0 / 96734250000000.0    - B_11;

    /** Internal weights for stage 14, element 12. */
    private static final double K14_12 =        1458939311.0 / 192780000000.0      - B_12;

    /** Internal weights for stage 14, element 13. */
    private static final double K14_13 =             -4149.0 / 500000.0;

    /** Time step for stage 15 (interpolation only). */
    private static final double C15    = 1.0 / 5.0;


    /** Internal weights for stage 15, element 1. */
    private static final double K15_01 =     1595561272731.0 / 50120273500000.0    - B_01;

    // elements 2 to 5 are zero, so they are neither stored nor used

    /** Internal weights for stage 15, element 6. */
    private static final double K15_06 =      975183916491.0 / 34457688031250.0    - B_06;

    /** Internal weights for stage 15, element 7. */
    private static final double K15_07 =    38492013932672.0 / 718912673015625.0   - B_07;

    /** Internal weights for stage 15, element 8. */
    private static final double K15_08 = -1114881286517557.0 / 20298710767500000.0 - B_08;

    /** Internal weights for stage 15, element 9. */
    private static final double K15_09 =                 0.0                       - B_09;

    /** Internal weights for stage 15, element 10. */
    private static final double K15_10 =                 0.0                       - B_10;

    /** Internal weights for stage 15, element 11. */
    private static final double K15_11 =    -2538710946863.0 / 23431227861250000.0 - B_11;

    /** Internal weights for stage 15, element 12. */
    private static final double K15_12 =        8824659001.0 / 23066716781250.0    - B_12;

    /** Internal weights for stage 15, element 13. */
    private static final double K15_13 =      -11518334563.0 / 33831184612500.0;

    /** Internal weights for stage 15, element 14. */
    private static final double K15_14 =        1912306948.0 / 13532473845.0;

    /** Time step for stage 16 (interpolation only). */
    private static final double C16    = 7.0 / 9.0;


    /** Internal weights for stage 16, element 1. */
    private static final double K16_01 =      -13613986967.0 / 31741908048.0       - B_01;

    // elements 2 to 5 are zero, so they are neither stored nor used

    /** Internal weights for stage 16, element 6. */
    private static final double K16_06 =       -4755612631.0 / 1012344804.0        - B_06;

    /** Internal weights for stage 16, element 7. */
    private static final double K16_07 =    42939257944576.0 / 5588559685701.0     - B_07;

    /** Internal weights for stage 16, element 8. */
    private static final double K16_08 =    77881972900277.0 / 19140370552944.0    - B_08;

    /** Internal weights for stage 16, element 9. */
    private static final double K16_09 =    22719829234375.0 / 63689648654052.0    - B_09;

    /** Internal weights for stage 16, element 10. */
    private static final double K16_10 =                 0.0                       - B_10;

    /** Internal weights for stage 16, element 11. */
    private static final double K16_11 =                 0.0                       - B_11;

    /** Internal weights for stage 16, element 12. */
    private static final double K16_12 =                 0.0                       - B_12;

    /** Internal weights for stage 16, element 13. */
    private static final double K16_13 =       -1199007803.0 / 857031517296.0;

    /** Internal weights for stage 16, element 14. */
    private static final double K16_14 =      157882067000.0 / 53564469831.0;

    /** Internal weights for stage 16, element 15. */
    private static final double K16_15 =     -290468882375.0 / 31741908048.0;

    /** Interpolation weights.
     * (beware that only the non-null values are in the table)
     */
    private static final double[][] D = {

      {        -17751989329.0 / 2106076560.0,               4272954039.0 / 7539864640.0,
              -118476319744.0 / 38604839385.0,            755123450731.0 / 316657731600.0,
        3692384461234828125.0 / 1744130441634250432.0,     -4612609375.0 / 5293382976.0,
              2091772278379.0 / 933644586600.0,             2136624137.0 / 3382989120.0,
                    -126493.0 / 1421424.0,                    98350000.0 / 5419179.0,
                  -18878125.0 / 2053168.0,                 -1944542619.0 / 438351368.0},

      {         32941697297.0 / 3159114840.0,             456696183123.0 / 1884966160.0,
             19132610714624.0 / 115814518155.0,       -177904688592943.0 / 474986597400.0,
       -4821139941836765625.0 / 218016305204281304.0,      30702015625.0 / 3970037232.0,
            -85916079474274.0 / 2800933759800.0,           -5919468007.0 / 634310460.0,
                    2479159.0 / 157936.0,                    -18750000.0 / 602131.0,
                  -19203125.0 / 2053168.0,                 15700361463.0 / 438351368.0},

      {         12627015655.0 / 631822968.0,              -72955222965.0 / 188496616.0,
            -13145744952320.0 / 69488710893.0,          30084216194513.0 / 56998391688.0,
        -296858761006640625.0 / 25648977082856624.0,         569140625.0 / 82709109.0,
               -18684190637.0 / 18672891732.0,                69644045.0 / 89549712.0,
                  -11847025.0 / 4264272.0,                  -978650000.0 / 16257537.0,
                  519371875.0 / 6159504.0,                  5256837225.0 / 438351368.0},

      {          -450944925.0 / 17550638.0,               -14532122925.0 / 94248308.0,
              -595876966400.0 / 2573655959.0,             188748653015.0 / 527762886.0,
        2545485458115234375.0 / 27252038150535163.0,       -1376953125.0 / 36759604.0,
                53995596795.0 / 518691437.0,                 210311225.0 / 7047894.0,
                   -1718875.0 / 39484.0,                      58000000.0 / 602131.0,
                   -1546875.0 / 39484.0,                   -1262172375.0 / 8429834.0}

    };

    /** Last evaluations. */
    private T[][] yDotKLast;

    /** Vectors for interpolation. */
    private T[][] v;

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
    DormandPrince853FieldStepInterpolator() {
        super();
        yDotKLast = null;
        v         = null;
        vectorsInitialized = false;
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

    }

    /** {@inheritDoc} */
    @Override
    protected DormandPrince853FieldStepInterpolator<T> doCopy() {
        return new DormandPrince853FieldStepInterpolator<T>(this);
    }

    /** {@inheritDoc} */
    @Override
    public void reinitialize(final T[] y, final boolean isForward, final FieldEquationsMapper<T> equationsMapper) {

        super.reinitialize(y, isForward, equationsMapper);

        final int dimension = currentState.length;

        yDotKLast = MathArrays.buildArray(y[0].getField(), 3, dimension);
        v         = MathArrays.buildArray(y[0].getField(), 7, dimension);

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
                v[0][i] =     yDot01.multiply(B_01).
                          add(yDot06.multiply(B_06)).
                          add(yDot07.multiply(B_07)).
                          add(yDot08.multiply(B_08)).
                          add(yDot09.multiply(B_09)).
                          add(yDot10.multiply(B_10)).
                          add(yDot11.multiply(B_11)).
                          add(yDot12.multiply(B_12));
                v[1][i] = yDot01.subtract(v[0][i]);
                v[2][i] = v[0][i].subtract(v[1][i]).subtract(yDotK[12][i]);
                for (int k = 0; k < D.length; ++k) {
                    v[k+3][i] =     yDot01.multiply(D[k][ 0]).
                                add(yDot06.multiply(D[k][ 1])).
                                add(yDot07.multiply(D[k][ 2])).
                                add(yDot08.multiply(D[k][ 3])).
                                add(yDot09.multiply(D[k][ 4])).
                                add(yDot10.multiply(D[k][ 5])).
                                add(yDot11.multiply(D[k][ 6])).
                                add(yDot12.multiply(D[k][ 7])).
                                add(yDot13.multiply(D[k][ 8])).
                                add(yDot14.multiply(D[k][ 9])).
                                add(yDot15.multiply(D[k][10])).
                                add(yDot16.multiply(D[k][11]));
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
            s =     yDotK[ 0][j].multiply(K14_01).
                add(yDotK[ 5][j].multiply(K14_06)).
                add(yDotK[ 6][j].multiply(K14_07)).
                add(yDotK[ 7][j].multiply(K14_08)).
                add(yDotK[ 8][j].multiply(K14_09)).
                add(yDotK[ 9][j].multiply(K14_10)).
                add(yDotK[10][j].multiply(K14_11)).
                add(yDotK[11][j].multiply(K14_12)).
                add(yDotK[12][j].multiply(K14_13));
            yTmp[j] = currentState[j].add(h.multiply(s));
        }
        yDotKLast[0] = integrator.computeDerivatives(pT.add(h.multiply(C14)), yTmp);

        // k15
        for (int j = 0; j < currentState.length; ++j) {
            s =     yDotK[ 0][j].multiply(K15_01).
                add(yDotK[ 5][j].multiply(K15_06)).
                add(yDotK[ 6][j].multiply(K15_07)).
                add(yDotK[ 7][j].multiply(K15_08)).
                add(yDotK[ 8][j].multiply(K15_09)).
                add(yDotK[ 9][j].multiply(K15_10)).
                add(yDotK[10][j].multiply(K15_11)).
                add(yDotK[11][j].multiply(K15_12)).
                add(yDotK[12][j].multiply(K15_13)).
                add(yDotKLast[0][j].multiply(K15_14));
            yTmp[j] = currentState[j].add(h.multiply(s));
        }
        yDotKLast[1] = integrator.computeDerivatives(pT.add(h.multiply(C15)), yTmp);

        // k16
        for (int j = 0; j < currentState.length; ++j) {
            s =     yDotK[ 0][j].multiply(K16_01).
                add(yDotK[ 5][j].multiply(K16_06)).
                add(yDotK[ 6][j].multiply(K16_07)).
                add(yDotK[ 7][j].multiply(K16_08)).
                add(yDotK[ 8][j].multiply(K16_09)).
                add(yDotK[ 9][j].multiply(K16_10)).
                add(yDotK[10][j].multiply(K16_11)).
                add(yDotK[11][j].multiply(K16_12)).
                add(yDotK[12][j].multiply(K16_13)).
                add(yDotKLast[0][j].multiply(K16_14)).
                add(yDotKLast[0][j].multiply(K16_15));
            yTmp[j] = currentState[j].add(h.multiply(s));
        }
        yDotKLast[2] = integrator.computeDerivatives(pT.add(h.multiply(C16)), yTmp);

    }

}
