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
package org.apache.commons.math.analysis.interpolation;

import junit.framework.TestCase;

import org.apache.commons.math.MathException;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;

/**
 * Test the SplineInterpolator.
 *
 * @version $Revision$ $Date$
 */
public class SplineInterpolatorTest extends TestCase {

    /** error tolerance for spline interpolator value at knot points */
    protected double knotTolerance = 1E-12;

    /** error tolerance for interpolating polynomial coefficients */
    protected double coefficientTolerance = 1E-6;

    /** error tolerance for interpolated values -- high value is from sin test */
    protected double interpolationTolerance = 1E-2;

    public SplineInterpolatorTest(String name) {
        super(name);
    }

    public void testInterpolateLinearDegenerateTwoSegment()
        throws Exception {
        double x[] = { 0.0, 0.5, 1.0 };
        double y[] = { 0.0, 0.5, 1.0 };
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(x, y);
        verifyInterpolation(f, x, y);
        verifyConsistency((PolynomialSplineFunction) f, x);

        // Verify coefficients using analytical values
        PolynomialFunction polynomials[] = ((PolynomialSplineFunction) f).getPolynomials();
        double target[] = {y[0], 1d};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[1], 1d};
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);

        // Check interpolation
        assertEquals(0.0,f.value(0.0), interpolationTolerance);
        assertEquals(0.4,f.value(0.4), interpolationTolerance);
        assertEquals(1.0,f.value(1.0), interpolationTolerance);
    }

    public void testInterpolateLinearDegenerateThreeSegment()
        throws Exception {
        double x[] = { 0.0, 0.5, 1.0, 1.5 };
        double y[] = { 0.0, 0.5, 1.0, 1.5 };
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(x, y);
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
        assertEquals(0,f.value(0), interpolationTolerance);
        assertEquals(1.4,f.value(1.4), interpolationTolerance);
        assertEquals(1.5,f.value(1.5), interpolationTolerance);
    }

    public void testInterpolateLinear() throws Exception {
        double x[] = { 0.0, 0.5, 1.0 };
        double y[] = { 0.0, 0.5, 0.0 };
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(x, y);
        verifyInterpolation(f, x, y);
        verifyConsistency((PolynomialSplineFunction) f, x);

        // Verify coefficients using analytical values
        PolynomialFunction polynomials[] = ((PolynomialSplineFunction) f).getPolynomials();
        double target[] = {y[0], 1.5d, 0d, -2d};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[1], 0d, -3d, 2d};
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);
    }

    public void testInterpolateSin() throws Exception {
        double x[] =
            {
                0.0,
                Math.PI / 6d,
                Math.PI / 2d,
                5d * Math.PI / 6d,
                Math.PI,
                7d * Math.PI / 6d,
                3d * Math.PI / 2d,
                11d * Math.PI / 6d,
                2.d * Math.PI };
        double y[] = { 0d, 0.5d, 1d, 0.5d, 0d, -0.5d, -1d, -0.5d, 0d };
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(x, y);
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
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[1], 8.594367e-01, -2.735672e-01, -0.08707914};
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[2], 1.471804e-17,-5.471344e-01, 0.08707914};
        TestUtils.assertEquals(polynomials[2].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[3], -8.594367e-01, -2.735672e-01, 0.17415829};
        TestUtils.assertEquals(polynomials[3].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[4], -1.002676, 6.548562e-17, 0.17415829};
        TestUtils.assertEquals(polynomials[4].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[5], -8.594367e-01, 2.735672e-01, 0.08707914};
        TestUtils.assertEquals(polynomials[5].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[6], 3.466465e-16, 5.471344e-01, -0.08707914};
        TestUtils.assertEquals(polynomials[6].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[7], 8.594367e-01, 2.735672e-01, -0.17415829};
        TestUtils.assertEquals(polynomials[7].getCoefficients(), target, coefficientTolerance);

        //Check interpolation
        assertEquals(Math.sqrt(2d) / 2d,f.value(Math.PI/4d),interpolationTolerance);
        assertEquals(Math.sqrt(2d) / 2d,f.value(3d*Math.PI/4d),interpolationTolerance);
    }


    public void testIllegalArguments() throws MathException {
        // Data set arrays of different size.
        UnivariateRealInterpolator i = new SplineInterpolator();
        try {
            double xval[] = { 0.0, 1.0 };
            double yval[] = { 0.0, 1.0, 2.0 };
            i.interpolate(xval, yval);
            fail("Failed to detect data set array with different sizes.");
        } catch (IllegalArgumentException iae) {
        }
        // X values not sorted.
        try {
            double xval[] = { 0.0, 1.0, 0.5 };
            double yval[] = { 0.0, 1.0, 2.0 };
            i.interpolate(xval, yval);
            fail("Failed to detect unsorted arguments.");
        } catch (IllegalArgumentException iae) {
        }
    }

    /**
     * verifies that f(x[i]) = y[i] for i = 0..n-1 where n is common length.
     */
    protected void verifyInterpolation(UnivariateRealFunction f, double x[], double y[])
        throws Exception{
        for (int i = 0; i < x.length; i++) {
            assertEquals(f.value(x[i]), y[i], knotTolerance);
        }
    }

    /**
     * Verifies that interpolating polynomials satisfy consistency requirement:
     *    adjacent polynomials must agree through two derivatives at knot points
     */
    protected void verifyConsistency(PolynomialSplineFunction f, double x[])
        throws Exception {
        PolynomialFunction polynomials[] = f.getPolynomials();
        for (int i = 1; i < x.length - 2; i++) {
            // evaluate polynomials and derivatives at x[i + 1]
            assertEquals(polynomials[i].value(x[i +1] - x[i]), polynomials[i + 1].value(0), 0.1);
            assertEquals(polynomials[i].derivative().value(x[i +1] - x[i]),
                    polynomials[i + 1].derivative().value(0), 0.5);
            assertEquals(polynomials[i].polynomialDerivative().derivative().value(x[i +1] - x[i]),
                    polynomials[i + 1].polynomialDerivative().derivative().value(0), 0.5);
        }
    }

}
