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
package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the SplineInterpolator.
 *
 */
public class SplineInterpolatorTest {

    /** error tolerance for spline interpolator value at knot points */
    protected double knotTolerance = 1E-14;

    /** error tolerance for interpolating polynomial coefficients */
    protected double coefficientTolerance = 1E-14;

    /** error tolerance for interpolated values -- high value is from sin test */
    protected double interpolationTolerance = 1E-14;

    @Test
    public void testInterpolateLinearDegenerateTwoSegment()
        {
        double tolerance = 1e-15;
        double x[] = { 0.0, 0.5, 1.0 };
        double y[] = { 0.0, 0.5, 1.0 };
        UnivariateInterpolator i = new SplineInterpolator();
        UnivariateFunction f = i.interpolate(x, y);
        verifyInterpolation(f, x, y);
        verifyConsistency((PolynomialSplineFunction) f, x);

        // Verify coefficients using analytical values
        PolynomialFunction polynomials[] = ((PolynomialSplineFunction) f).getPolynomials();
        double target[] = {y[0], 1d};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[1], 1d};
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);

        // Check interpolation
        Assert.assertEquals(0.0,f.value(0.0), tolerance);
        Assert.assertEquals(0.4,f.value(0.4), tolerance);
        Assert.assertEquals(1.0,f.value(1.0), tolerance);
    }

    @Test
    public void testInterpolateLinearDegenerateThreeSegment()
        {
        double tolerance = 1e-15;
        double x[] = { 0.0, 0.5, 1.0, 1.5 };
        double y[] = { 0.0, 0.5, 1.0, 1.5 };
        UnivariateInterpolator i = new SplineInterpolator();
        UnivariateFunction f = i.interpolate(x, y);
        verifyInterpolation(f, x, y);

        // Verify coefficients using analytical values
        PolynomialFunction polynomials[] = ((PolynomialSplineFunction) f).getPolynomials();
        double target[] = {y[0], 1d};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[1], 1d};
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[2], 1d};
        TestUtils.assertEquals(polynomials[2].getCoefficients(), target, coefficientTolerance);

        // Check interpolation
        Assert.assertEquals(0,f.value(0), tolerance);
        Assert.assertEquals(1.4,f.value(1.4), tolerance);
        Assert.assertEquals(1.5,f.value(1.5), tolerance);
    }

    @Test
    public void testInterpolateLinear() {
        double x[] = { 0.0, 0.5, 1.0 };
        double y[] = { 0.0, 0.5, 0.0 };
        UnivariateInterpolator i = new SplineInterpolator();
        UnivariateFunction f = i.interpolate(x, y);
        verifyInterpolation(f, x, y);
        verifyConsistency((PolynomialSplineFunction) f, x);

        // Verify coefficients using analytical values
        PolynomialFunction polynomials[] = ((PolynomialSplineFunction) f).getPolynomials();
        double target[] = {y[0], 1.5d, 0d, -2d};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[1], 0d, -3d, 2d};
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);
    }

    @Test
    public void testInterpolateSin() {
        double sineCoefficientTolerance = 1e-6;
        double sineInterpolationTolerance = 0.0043;
        double x[] =
            {
                0.0,
                FastMath.PI / 6d,
                FastMath.PI / 2d,
                5d * FastMath.PI / 6d,
                FastMath.PI,
                7d * FastMath.PI / 6d,
                3d * FastMath.PI / 2d,
                11d * FastMath.PI / 6d,
                2.d * FastMath.PI };
        double y[] = { 0d, 0.5d, 1d, 0.5d, 0d, -0.5d, -1d, -0.5d, 0d };
        UnivariateInterpolator i = new SplineInterpolator();
        UnivariateFunction f = i.interpolate(x, y);
        verifyInterpolation(f, x, y);
        verifyConsistency((PolynomialSplineFunction) f, x);

        /* Check coefficients against values computed using R (version 1.8.1, Red Hat Linux 9)
         *
         * To replicate in R:
         *     x[1] <- 0
         *     x[2] <- pi / 6, etc, same for y[] (could use y <- scan() for y values)
         *     g <- splinefun(x, y, "natural")
         *     splinecoef <- eval(expression(z), envir = environment(g))
         *     print(splinecoef)
         */
        PolynomialFunction polynomials[] = ((PolynomialSplineFunction) f).getPolynomials();
        double target[] = {y[0], 1.002676d, 0d, -0.17415829d};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target, sineCoefficientTolerance);
        target = new double[]{y[1], 8.594367e-01, -2.735672e-01, -0.08707914};
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target, sineCoefficientTolerance);
        target = new double[]{y[2], 1.471804e-17,-5.471344e-01, 0.08707914};
        TestUtils.assertEquals(polynomials[2].getCoefficients(), target, sineCoefficientTolerance);
        target = new double[]{y[3], -8.594367e-01, -2.735672e-01, 0.17415829};
        TestUtils.assertEquals(polynomials[3].getCoefficients(), target, sineCoefficientTolerance);
        target = new double[]{y[4], -1.002676, 6.548562e-17, 0.17415829};
        TestUtils.assertEquals(polynomials[4].getCoefficients(), target, sineCoefficientTolerance);
        target = new double[]{y[5], -8.594367e-01, 2.735672e-01, 0.08707914};
        TestUtils.assertEquals(polynomials[5].getCoefficients(), target, sineCoefficientTolerance);
        target = new double[]{y[6], 3.466465e-16, 5.471344e-01, -0.08707914};
        TestUtils.assertEquals(polynomials[6].getCoefficients(), target, sineCoefficientTolerance);
        target = new double[]{y[7], 8.594367e-01, 2.735672e-01, -0.17415829};
        TestUtils.assertEquals(polynomials[7].getCoefficients(), target, sineCoefficientTolerance);

        //Check interpolation
        Assert.assertEquals(FastMath.sqrt(2d) / 2d,f.value(FastMath.PI/4d),sineInterpolationTolerance);
        Assert.assertEquals(FastMath.sqrt(2d) / 2d,f.value(3d*FastMath.PI/4d),sineInterpolationTolerance);
    }

    @Test
    public void testIllegalArguments() {
        // Data set arrays of different size.
        UnivariateInterpolator i = new SplineInterpolator();
        try {
            double xval[] = { 0.0, 1.0 };
            double yval[] = { 0.0, 1.0, 2.0 };
            i.interpolate(xval, yval);
            Assert.fail("Failed to detect data set array with different sizes.");
        } catch (DimensionMismatchException iae) {
            // Expected.
        }
        // X values not sorted.
        try {
            double xval[] = { 0.0, 1.0, 0.5 };
            double yval[] = { 0.0, 1.0, 2.0 };
            i.interpolate(xval, yval);
            Assert.fail("Failed to detect unsorted arguments.");
        } catch (NonMonotonicSequenceException iae) {
            // Expected.
        }
        // Not enough data to interpolate.
        try {
            double xval[] = { 0.0, 1.0 };
            double yval[] = { 0.0, 1.0 };
            i.interpolate(xval, yval);
            Assert.fail("Failed to detect unsorted arguments.");
        } catch (NumberIsTooSmallException iae) {
            // Expected.
        }
    }

    /**
     * verifies that f(x[i]) = y[i] for i = 0..n-1 where n is common length.
     */
    protected void verifyInterpolation(UnivariateFunction f, double x[], double y[])
       {
        for (int i = 0; i < x.length; i++) {
            Assert.assertEquals(f.value(x[i]), y[i], knotTolerance);
        }
    }

    /**
     * Verifies that interpolating polynomials satisfy consistency requirement:
     *    adjacent polynomials must agree through two derivatives at knot points
     */
    protected void verifyConsistency(PolynomialSplineFunction f, double x[])
        {
        PolynomialFunction polynomials[] = f.getPolynomials();
        for (int i = 1; i < x.length - 2; i++) {
            // evaluate polynomials and derivatives at x[i + 1]
            Assert.assertEquals(polynomials[i].value(x[i +1] - x[i]), polynomials[i + 1].value(0), 0.1);
            Assert.assertEquals(polynomials[i].derivative().value(x[i +1] - x[i]),
                                polynomials[i + 1].derivative().value(0), 0.5);
            Assert.assertEquals(polynomials[i].polynomialDerivative().derivative().value(x[i +1] - x[i]),
                                polynomials[i + 1].polynomialDerivative().derivative().value(0), 0.5);
        }
    }

}
