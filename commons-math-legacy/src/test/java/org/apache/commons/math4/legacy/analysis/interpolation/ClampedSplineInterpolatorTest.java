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
package org.apache.commons.math4.legacy.analysis.interpolation;

import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.NonMonotonicSequenceException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the ClampedSplineInterpolator.
 */
public class ClampedSplineInterpolatorTest {
    /** Error tolerance for spline interpolator value at knot points. */
    private static final double KNOT_TOL = 1e-14;
    /** Error tolerance for interpolating polynomial coefficients. */
    private static final double COEF_TOL = 1e-14;

    @Test
    public void testInterpolateLinearDegenerateTwoSegment() {
        final double[] x = {0, 0.5, 1};
        final double[] y = {1, Math.exp(0.5), Math.exp(1)};
        final double fpo = 1;
        final double fpn = Math.exp(1);
        final ClampedSplineInterpolator i = new ClampedSplineInterpolator();
        final PolynomialSplineFunction f = i.interpolate(x, y, fpo, fpn);
        verifyInterpolation(f, x, y);
        verifyConsistency(f, x);

        // Verify coefficients using analytical values
        final PolynomialFunction[] polynomials = f.getPolynomials();

        final double[] target0 = {1, 1, 0.4889506772539256, 0.21186881109317435};
        final double[] target1 = {1.6487212707001282, 1.6478522855738063, 0.8067538938936871, 0.35156753198873575};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target0, COEF_TOL);
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target1, COEF_TOL);
    }

    @Test
    public void testInterpolateLinearDegenerateThreeSegment() {
        final double[] x = {0, 1, 2, 3};
        final double[] y = {1, Math.exp(1), Math.exp(2), Math.exp(3)};
        final double fpo = 1;
        final double fpn = Math.exp(3);
        final ClampedSplineInterpolator i = new ClampedSplineInterpolator();
        final PolynomialSplineFunction f = i.interpolate(x, y, fpo, fpn);
        verifyInterpolation(f, x, y);
        verifyConsistency(f, x);

        // Verify coefficients using analytical values
        final PolynomialFunction[] polynomials = f.getPolynomials();

        final double[] target0 = {1, 0.9999999999999999, 0.4446824969658283, 0.27359933149321697};
        final double[] target1 = {2.718281828459045, 2.710162988411307, 1.2654804914454791, 0.6951307906148195};
        final double[] target2 = {7.38905609893065, 7.326516343146723, 3.3508728632899376, 2.019091617820356};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target0, COEF_TOL);
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target1, COEF_TOL);
        TestUtils.assertEquals(polynomials[2].getCoefficients(), target2, COEF_TOL);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testArrayLengthMismatch() {
        // Data set arrays of different size.
        new ClampedSplineInterpolator().interpolate(new double[] {1, 2, 3, 4},
                                                    new double[] {2, 3, 5},
                                                    2, 1);
    }
    @Test(expected=NonMonotonicSequenceException.class)
    public void testUnsortedArray() {
        // Knot values not sorted.
        new ClampedSplineInterpolator().interpolate(new double[] {1, 3, 2 },
                                                    new double[] {2, 3, 5},
                                                    2, 1);
    }
    @Test(expected=NumberIsTooSmallException.class)
    public void testInsufficientData() {
        // Not enough data to interpolate.
        new ClampedSplineInterpolator().interpolate(new double[] {1, 2 },
                                                    new double[] {2, 3},
                                                    2, 1);
    }

    /**
     * Verifies that a clamped spline <i>without</i> specified boundary conditions behaves similar to a natural
     * ("unclamped") spline.
     *
     * <p>
     * Using the exponential function <code>e<sup>x</sup></code> over the interval <code>[0, 3]</code>, the test
     * evaluates:
     * <ol>
     * <li>The integral of a clamped spline with specified boundary conditions (endpoint slopes/derivatives).</li>
     * <li>The integral of a clamped spline without specified boundary conditions.</li>
     * <li>The integral of a natural spline (without boundary conditions by default).</li>
     * </ol>
     *
     * These integrals are compared against the direct integral of the function <code>e<sup>x</sup></code> over the same
     * interval.</p>
     *
     * <p>
     * This test is based on "Example 4" in R.L. Burden, J.D. Faires,
     * <u>Numerical Analysis</u>, 9th Ed., 2010, Cengage Learning, ISBN 0-538-73351-9, pp 156-157.
     * </p>
     */
    @Test
    public void testIntegral() {
        final double[] x = {0, 1, 2, 3};
        final double[] y = {1, Math.exp(1), Math.exp(2), Math.exp(3)};
        final double fpo = 1;
        final double fpn = Math.exp(3);

        final ClampedSplineInterpolator clampedSplineInterpolator = new ClampedSplineInterpolator();
        final PolynomialSplineFunction clampedSpline = clampedSplineInterpolator.interpolate(x, y, fpo, fpn);
        final PolynomialSplineFunction clampedSplineAsNaturalSpline = clampedSplineInterpolator.interpolate(x, y);

        final SplineInterpolator naturalSplineInterpolator = new SplineInterpolator();
        final PolynomialSplineFunction naturalSpline = naturalSplineInterpolator.interpolate(x, y);

        final SimpsonIntegrator integrator = new SimpsonIntegrator();

        final double clampedSplineIntegral = integrator.integrate(1000, clampedSpline, 0, 3);
        final double clampedSplineAsNaturalSplineIntegral = integrator.integrate(1000, clampedSplineAsNaturalSpline, 0, 3);
        final double naturalSplineIntegral = integrator.integrate(1000, naturalSpline, 0, 3);
        final double exponentialFunctionIntegral = integrator.integrate(1000, arg -> Math.exp(arg), 0, 3);

        Assert.assertEquals(Math.abs(clampedSplineAsNaturalSplineIntegral - naturalSplineIntegral), 0, 0);
        Assert.assertEquals(Math.abs(exponentialFunctionIntegral - clampedSplineIntegral), 0.02589, 0.1);
        Assert.assertEquals(Math.abs(exponentialFunctionIntegral - naturalSplineIntegral), 0.46675, 0.1);
    }

    /**
     * Verifies that f(x[i]) = y[i] for i = 0, ..., n-1 (where n is common length).
     */
    private void verifyInterpolation(PolynomialSplineFunction f,
                                     double[] x, double[] y) {
        for (int i = 0; i < x.length; i++) {
            Assert.assertEquals(f.value(x[i]), y[i], KNOT_TOL);
        }
    }

    /**
     * Verifies that interpolating polynomials satisfy consistency requirement: adjacent polynomials must agree through
     * two derivatives at knot points.
     */
    private void verifyConsistency(PolynomialSplineFunction f,
                                   double[] x) {
        PolynomialFunction polynomials[] = f.getPolynomials();
        for (int i = 1; i < x.length - 2; i++) {
            // evaluate polynomials and derivatives at x[i + 1]
            Assert.assertEquals(polynomials[i].value(x[i + 1] - x[i]), polynomials[i + 1].value(0), 0.1);
            Assert.assertEquals(polynomials[i].polynomialDerivative().value(x[i + 1] - x[i]),
                                polynomials[i + 1].polynomialDerivative().value(0), 0.5);
            Assert.assertEquals(polynomials[i].polynomialDerivative().polynomialDerivative().value(x[i + 1] - x[i]),
                                polynomials[i + 1].polynomialDerivative().polynomialDerivative().value(0), 0.5);
        }
    }
}
