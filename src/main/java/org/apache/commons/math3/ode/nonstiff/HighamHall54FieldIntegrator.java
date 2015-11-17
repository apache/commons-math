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

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.util.MathUtils;


/**
 * This class implements the 5(4) Higham and Hall integrator for
 * Ordinary Differential Equations.
 *
 * <p>This integrator is an embedded Runge-Kutta integrator
 * of order 5(4) used in local extrapolation mode (i.e. the solution
 * is computed using the high order formula) with stepsize control
 * (and automatic step initialization) and continuous output. This
 * method uses 7 functions evaluations per step.</p>
 *
 * @param <T> the type of the field elements
 * @since 3.6
 */

public class HighamHall54FieldIntegrator<T extends RealFieldElement<T>>
    extends EmbeddedRungeKuttaFieldIntegrator<T> {

    /** Integrator method name. */
    private static final String METHOD_NAME = "Higham-Hall 5(4)";

    /** Time steps Butcher array. */
    private static final double[] STATIC_C = {
        2.0/9.0, 1.0/3.0, 1.0/2.0, 3.0/5.0, 1.0, 1.0
    };

    /** Internal weights Butcher array. */
    private static final double[][] STATIC_A = {
        {2.0/9.0},
        {1.0/12.0, 1.0/4.0},
        {1.0/8.0, 0.0, 3.0/8.0},
        {91.0/500.0, -27.0/100.0, 78.0/125.0, 8.0/125.0},
        {-11.0/20.0, 27.0/20.0, 12.0/5.0, -36.0/5.0, 5.0},
        {1.0/12.0, 0.0, 27.0/32.0, -4.0/3.0, 125.0/96.0, 5.0/48.0}
    };

    /** Propagation weights Butcher array. */
    private static final double[] STATIC_B = {
        1.0/12.0, 0.0, 27.0/32.0, -4.0/3.0, 125.0/96.0, 5.0/48.0, 0.0
    };

    /** Error weights Butcher array. */
    private static final double[] STATIC_E = {
        -1.0/20.0, 0.0, 81.0/160.0, -6.0/5.0, 25.0/32.0, 1.0/16.0, -1.0/10.0
    };

    /** Simple constructor.
     * Build a fifth order Higham and Hall integrator with the given step bounds
     * @param field field to which the time and state vector elements belong
     * @param minStep minimal step (sign is irrelevant, regardless of
     * integration direction, forward or backward), the last step can
     * be smaller than this
     * @param maxStep maximal step (sign is irrelevant, regardless of
     * integration direction, forward or backward), the last step can
     * be smaller than this
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     */
    public HighamHall54FieldIntegrator(final Field<T> field,
                                       final double minStep, final double maxStep,
                                       final double scalAbsoluteTolerance,
                                       final double scalRelativeTolerance) {
        super(field, METHOD_NAME, false, STATIC_C, STATIC_A, STATIC_B,
              new HighamHall54FieldStepInterpolator<T>(),
              minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }

    /** Simple constructor.
     * Build a fifth order Higham and Hall integrator with the given step bounds
     * @param field field to which the time and state vector elements belong
     * @param minStep minimal step (sign is irrelevant, regardless of
     * integration direction, forward or backward), the last step can
     * be smaller than this
     * @param maxStep maximal step (sign is irrelevant, regardless of
     * integration direction, forward or backward), the last step can
     * be smaller than this
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     */
    public HighamHall54FieldIntegrator(final Field<T> field,
                                       final double minStep, final double maxStep,
                                       final double[] vecAbsoluteTolerance,
                                       final double[] vecRelativeTolerance) {
        super(field, METHOD_NAME, false, STATIC_C, STATIC_A, STATIC_B,
              new HighamHall54FieldStepInterpolator<T>(),
              minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }

    /** {@inheritDoc} */
    @Override
    public int getOrder() {
        return 5;
    }

    /** {@inheritDoc} */
    @Override
    protected T estimateError(final T[][] yDotK, final T[] y0, final T[] y1, final T h) {

        T error = getField().getZero();

        for (int j = 0; j < mainSetDimension; ++j) {
            T errSum = yDotK[0][j].multiply(STATIC_E[0]);
            for (int l = 1; l < STATIC_E.length; ++l) {
                errSum = errSum.add(yDotK[l][j].multiply(STATIC_E[l]));
            }

            final T yScale = MathUtils.max(y0[j].abs(), y1[j].abs());
            final T tol    = (vecAbsoluteTolerance == null) ?
                             yScale.multiply(scalRelativeTolerance).add(scalAbsoluteTolerance) :
                             yScale.multiply(vecRelativeTolerance[j]).add(vecAbsoluteTolerance[j]);
            final T ratio  = h.multiply(errSum).divide(tol);
            error = error.add(ratio.multiply(ratio));

        }

        return error.divide(mainSetDimension).sqrt();

    }

}
