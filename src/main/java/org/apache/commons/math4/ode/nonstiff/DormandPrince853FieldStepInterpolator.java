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

    /** Interpolation weights.
     * (beware that only the non-null values are in the table)
     */
    private final T[][] d;

    /** Simple constructor.
     * @param field field to which the time and state vector elements belong
     * @param forward integration direction indicator
     * @param mapper equations mapper for the all equations
     */
    DormandPrince853FieldStepInterpolator(final Field<T> field, final boolean forward,
                                          final FieldEquationsMapper<T> mapper) {
        super(field, forward, mapper);

        // interpolation weights
        d = MathArrays.buildArray(getField(), 7, 16);

        // this row is the same as the b array
        d[0][ 0] = fraction(104257, 1929240);
        d[0][ 1] = getField().getZero();
        d[0][ 2] = getField().getZero();
        d[0][ 3] = getField().getZero();
        d[0][ 4] = getField().getZero();
        d[0][ 5] = fraction(        3399327.0,          763840.0);
        d[0][ 6] = fraction(       66578432.0,        35198415.0);
        d[0][ 7] = fraction(    -1674902723.0,       288716400.0);
        d[0][ 8] = fraction( 54980371265625.0, 176692375811392.0);
        d[0][ 9] = fraction(        -734375.0,         4826304.0);
        d[0][10] = fraction(      171414593.0,       851261400.0);
        d[0][11] = fraction(         137909.0,         3084480.0);
        d[0][12] = getField().getZero();
        d[0][13] = getField().getZero();
        d[0][14] = getField().getZero();
        d[0][15] = getField().getZero();

        d[1][ 0] = d[0][ 0].negate().add(1);
        d[1][ 1] = d[0][ 1].negate();
        d[1][ 2] = d[0][ 2].negate();
        d[1][ 3] = d[0][ 3].negate();
        d[1][ 4] = d[0][ 4].negate();
        d[1][ 5] = d[0][ 5].negate();
        d[1][ 6] = d[0][ 6].negate();
        d[1][ 7] = d[0][ 7].negate();
        d[1][ 8] = d[0][ 8].negate();
        d[1][ 9] = d[0][ 9].negate();
        d[1][10] = d[0][10].negate();
        d[1][11] = d[0][11].negate();
        d[1][12] = d[0][12].negate(); // really 0
        d[1][13] = d[0][13].negate(); // really 0
        d[1][14] = d[0][14].negate(); // really 0
        d[1][15] = d[0][15].negate(); // really 0

        d[2][ 0] = d[0][ 0].multiply(2).subtract(1);
        d[2][ 1] = d[0][ 1].multiply(2);
        d[2][ 2] = d[0][ 2].multiply(2);
        d[2][ 3] = d[0][ 3].multiply(2);
        d[2][ 4] = d[0][ 4].multiply(2);
        d[2][ 5] = d[0][ 5].multiply(2);
        d[2][ 6] = d[0][ 6].multiply(2);
        d[2][ 7] = d[0][ 7].multiply(2);
        d[2][ 8] = d[0][ 8].multiply(2);
        d[2][ 9] = d[0][ 9].multiply(2);
        d[2][10] = d[0][10].multiply(2);
        d[2][11] = d[0][11].multiply(2);
        d[2][12] = d[0][12].multiply(2); // really 0
        d[2][13] = d[0][13].multiply(2); // really 0
        d[2][14] = d[0][14].multiply(2); // really 0
        d[2][15] = d[0][15].multiply(2); // really 0

        d[3][ 0] = fraction(        -17751989329.0, 2106076560.0);
        d[3][ 1] = getField().getZero();
        d[3][ 2] = getField().getZero();
        d[3][ 3] = getField().getZero();
        d[3][ 4] = getField().getZero();
        d[3][ 5] = fraction(          4272954039.0, 7539864640.0);
        d[3][ 6] = fraction(       -118476319744.0, 38604839385.0);
        d[3][ 7] = fraction(        755123450731.0, 316657731600.0);
        d[3][ 8] = fraction( 3692384461234828125.0, 1744130441634250432.0);
        d[3][ 9] = fraction(         -4612609375.0, 5293382976.0);
        d[3][10] = fraction(       2091772278379.0, 933644586600.0);
        d[3][11] = fraction(          2136624137.0, 3382989120.0);
        d[3][12] = fraction(             -126493.0, 1421424.0);
        d[3][13] = fraction(            98350000.0, 5419179.0);
        d[3][14] = fraction(           -18878125.0, 2053168.0);
        d[3][15] = fraction(         -1944542619.0, 438351368.0);

        d[4][ 0] = fraction(         32941697297.0, 3159114840.0);
        d[4][ 1] = getField().getZero();
        d[4][ 2] = getField().getZero();
        d[4][ 3] = getField().getZero();
        d[4][ 4] = getField().getZero();
        d[4][ 5] = fraction(        456696183123.0, 1884966160.0);
        d[4][ 6] = fraction(      19132610714624.0, 115814518155.0);
        d[4][ 7] = fraction(    -177904688592943.0, 474986597400.0);
        d[4][ 8] = fraction(-4821139941836765625.0, 218016305204281304.0);
        d[4][ 9] = fraction(         30702015625.0, 3970037232.0);
        d[4][10] = fraction(     -85916079474274.0, 2800933759800.0);
        d[4][11] = fraction(         -5919468007.0, 634310460.0);
        d[4][12] = fraction(             2479159.0, 157936.0);
        d[4][13] = fraction(           -18750000.0, 602131.0);
        d[4][14] = fraction(           -19203125.0, 2053168.0);
        d[4][15] = fraction(         15700361463.0, 438351368.0);

        d[5][ 0] = fraction(         12627015655.0, 631822968.0);
        d[5][ 1] = getField().getZero();
        d[5][ 2] = getField().getZero();
        d[5][ 3] = getField().getZero();
        d[5][ 4] = getField().getZero();
        d[5][ 5] = fraction(        -72955222965.0, 188496616.0);
        d[5][ 6] = fraction(     -13145744952320.0, 69488710893.0);
        d[5][ 7] = fraction(      30084216194513.0, 56998391688.0);
        d[5][ 8] = fraction( -296858761006640625.0, 25648977082856624.0);
        d[5][ 9] = fraction(           569140625.0, 82709109.0);
        d[5][10] = fraction(        -18684190637.0, 18672891732.0);
        d[5][11] = fraction(            69644045.0, 89549712.0);
        d[5][12] = fraction(           -11847025.0, 4264272.0);
        d[5][13] = fraction(          -978650000.0, 16257537.0);
        d[5][14] = fraction(           519371875.0, 6159504.0);
        d[5][15] = fraction(          5256837225.0, 438351368.0);

        d[6][ 0] = fraction(          -450944925.0, 17550638.0);
        d[6][ 1] = getField().getZero();
        d[6][ 2] = getField().getZero();
        d[6][ 3] = getField().getZero();
        d[6][ 4] = getField().getZero();
        d[6][ 5] = fraction(        -14532122925.0, 94248308.0);
        d[6][ 6] = fraction(       -595876966400.0, 2573655959.0);
        d[6][ 7] = fraction(        188748653015.0, 527762886.0);
        d[6][ 8] = fraction( 2545485458115234375.0, 27252038150535163.0);
        d[6][ 9] = fraction(         -1376953125.0, 36759604.0);
        d[6][10] = fraction(         53995596795.0, 518691437.0);
        d[6][11] = fraction(           210311225.0, 7047894.0);
        d[6][12] = fraction(            -1718875.0, 39484.0);
        d[6][13] = fraction(            58000000.0, 602131.0);
        d[6][14] = fraction(            -1546875.0, 39484.0);
        d[6][15] = fraction(         -1262172375.0, 8429834.0);

    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    DormandPrince853FieldStepInterpolator(final DormandPrince853FieldStepInterpolator<T> interpolator) {

        super(interpolator);

        d = MathArrays.buildArray(getField(), 4, -1);
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
        return getField().getOne().multiply(p).divide(q);
    }

    /** {@inheritDoc} */
    @Override
    protected DormandPrince853FieldStepInterpolator<T> doCopy() {
        return new DormandPrince853FieldStepInterpolator<T>(this);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper,
                                                                                   final T time, final T theta,
                                                                                   final T oneMinusThetaH)
        throws MaxCountExceededException {

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
        final T[] interpolatedState;
        final T[] interpolatedDerivatives;

        if (getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
            final T f0 = theta.multiply(h);
            final T f1 = f0.multiply(eta);
            final T f2 = f1.multiply(theta);
            final T f3 = f2.multiply(eta);
            final T f4 = f3.multiply(theta);
            final T f5 = f4.multiply(eta);
            final T f6 = f5.multiply(theta);
            interpolatedState       = previousStateLinearCombination(f0, f1, f2, f3, f4, f5, f6);
            interpolatedDerivatives = derivativeLinearCombination(one, dot1, dot2, dot3, dot4, dot5, dot6);
        } else {
            final T f0 = oneMinusThetaH;
            final T f1 = f0.multiply(theta).negate();
            final T f2 = f1.multiply(theta);
            final T f3 = f2.multiply(eta);
            final T f4 = f3.multiply(theta);
            final T f5 = f4.multiply(eta);
            final T f6 = f5.multiply(theta);
            interpolatedState       = currentStateLinearCombination(f0, f1, f2, f3, f4, f5, f6);
            interpolatedDerivatives = derivativeLinearCombination(one, dot1, dot2, dot3, dot4, dot5, dot6);
        }

        return new FieldODEStateAndDerivative<T>(time, interpolatedState, interpolatedDerivatives);

    }

}
