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

package org.apache.commons.math4.legacy.ode.nonstiff;


import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math4.legacy.linear.Array2DRowRealMatrix;
import org.junit.Assert;
import org.junit.Test;

public class AdamsNordsieckTransformerTest {

    @Test
    public void testPolynomialExtraDerivative() {
        checkNordsieckStart(new PolynomialFunction(new double[] { 6, 5, 4, 3, 2, 1 }),
                            5, 0.0, 0.125, 3.2e-16);
    }

    @Test
    public void testPolynomialRegular() {
        checkNordsieckStart(new PolynomialFunction(new double[] { 6, 5, 4, 3, 2, 1 }),
                            4, 0.0, 0.125, 3.1e-16);
    }

    @Test
    public void testPolynomialMissingLastDerivative() {
        // this test intentionally uses not enough start points,
        // the Nordsieck vector is therefore not expected to match the exact scaled derivatives
        checkNordsieckStart(new PolynomialFunction(new double[] { 6, 5, 4, 3, 2, 1 }),
                            3, 0.0, 0.125, 1.6e-4);
    }

    @Test
    public void testTransformExact() {
        // a 5 steps transformer handles a degree 5 polynomial exactly
        // the Nordsieck vector holds the full information about the function
        // transforming the vector from t0 to t0+h or recomputing it from scratch
        // at t0+h yields the same result
        checkTransform(new PolynomialFunction(new double[] { 6, 5, 4, 3, 2, 1 }), 5, 2.567e-15);
    }

    @Test
    public void testTransformInexact() {
        // a 4 steps transformer cannot handle a degree 5 polynomial exactly
        // the Nordsieck vector lacks some high degree information about the function
        // transforming the vector from t0 to t0+h or recomputing it from scratch
        // at t0+h yields different results
        checkTransform(new PolynomialFunction(new double[] { 6, 5, 4, 3, 2, 1 }), 4, 5.658e-4);
    }

    private void checkNordsieckStart(final PolynomialFunction polynomial, final int nbSteps, final double t0,
                                     final double h, final double epsilon) {

        final AdamsNordsieckTransformer transformer = AdamsNordsieckTransformer.getInstance(nbSteps);
        PolynomialFunction derivative = polynomial.polynomialDerivative();
        final Array2DRowRealMatrix nordsieck = start(transformer, nbSteps, t0, h, polynomial, derivative);

        Assert.assertEquals(nbSteps - 1, nordsieck.getRowDimension());
        double coeff = h;
        for (int i = 0; i < nordsieck.getRowDimension(); ++i) {
            coeff *= h / (i + 2);
            derivative = derivative.polynomialDerivative();
            Assert.assertEquals(derivative.value(t0) * coeff, nordsieck.getEntry(i, 0), epsilon);
        }
    }

    private void checkTransform(final PolynomialFunction polynomial, final int nbSteps, final double expectedError) {

        final AdamsNordsieckTransformer transformer = AdamsNordsieckTransformer.getInstance(nbSteps);
        final PolynomialFunction derivative = polynomial.polynomialDerivative();

        final double t0 = 0.0;
        final double h  = 0.125;
        final Array2DRowRealMatrix n0 = start(transformer, nbSteps, t0, h, polynomial, derivative);
        final Array2DRowRealMatrix n1 = transformer.updateHighOrderDerivativesPhase1(n0);
        transformer.updateHighOrderDerivativesPhase2(new double[] { h * derivative.value(t0)     },
                                                     new double[] { h * derivative.value(t0 + h) },
                                                     n1);
        final Array2DRowRealMatrix n2 = start(transformer, nbSteps, t0 + h, h, polynomial, derivative);

        Assert.assertEquals(expectedError, n2.subtract(n1).getNorm(), expectedError * 0.001);
    }

    private Array2DRowRealMatrix start(final AdamsNordsieckTransformer transformer, final int nbSteps,
                                       final double t0, final double h,
                                       final UnivariateFunction f0, final UnivariateFunction f1) {

        final int        nbStartPoints = (nbSteps + 3) / 2;
        final double[]   t             = new double[nbStartPoints];
        final double[][] y             = new double[nbStartPoints][1];
        final double[][] yDot          = new double[nbStartPoints][1];
        for (int i = 0; i < nbStartPoints; ++i) {
            t[i]       = t0 + i * h;
            y[i][0]    = f0.value(t[i]);
            yDot[i][0] = f1.value(t[i]);
        }

        return transformer.initializeHighOrderDerivatives(h, t, y, yDot);
    }
}
