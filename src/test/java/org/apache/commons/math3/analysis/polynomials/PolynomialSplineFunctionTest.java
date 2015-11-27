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
package org.apache.commons.math3.analysis.polynomials;

import java.util.Arrays;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the PolynomialSplineFunction implementation.
 *
 */
public class PolynomialSplineFunctionTest {

    /** Error tolerance for tests */
    protected double tolerance = 1.0e-12;

    /**
     * Quadratic polynomials used in tests:
     *
     * x^2 + x            [-1, 0)
     * x^2 + x + 2        [0, 1)
     * x^2 + x + 4        [1, 2)
     *
     * Defined so that evaluation using PolynomialSplineFunction evaluation
     * algorithm agrees at knot point boundaries.
     */
    protected PolynomialFunction[] polynomials = {
        new PolynomialFunction(new double[] {0d, 1d, 1d}),
        new PolynomialFunction(new double[] {2d, 1d, 1d}),
        new PolynomialFunction(new double[] {4d, 1d, 1d})
    };

    /** Knot points  */
    protected double[] knots = {-1, 0, 1, 2};

    /** Derivative of test polynomials -- 2x + 1  */
    protected PolynomialFunction dp =
        new PolynomialFunction(new double[] {1d, 2d});


    @Test
    public void testConstructor() {
        PolynomialSplineFunction spline =
            new PolynomialSplineFunction(knots, polynomials);
        Assert.assertTrue(Arrays.equals(knots, spline.getKnots()));
        Assert.assertEquals(1d, spline.getPolynomials()[0].getCoefficients()[2], 0);
        Assert.assertEquals(3, spline.getN());

        try { // too few knots
            new PolynomialSplineFunction(new double[] {0}, polynomials);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }

        try { // too many knots
            new PolynomialSplineFunction(new double[] {0,1,2,3,4}, polynomials);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }

        try { // knots not increasing
            new PolynomialSplineFunction(new double[] {0,1, 3, 2}, polynomials);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testValues() {
        PolynomialSplineFunction spline =
            new PolynomialSplineFunction(knots, polynomials);
        UnivariateFunction dSpline = spline.derivative();

        /**
         * interior points -- spline value at x should equal p(x - knot)
         * where knot is the largest knot point less than or equal to x and p
         * is the polynomial defined over the knot segment to which x belongs.
         */
        double x = -1;
        int index = 0;
        for (int i = 0; i < 10; i++) {
           x+=0.25;
           index = findKnot(knots, x);
           Assert.assertEquals("spline function evaluation failed for x=" + x,
                   polynomials[index].value(x - knots[index]), spline.value(x), tolerance);
           Assert.assertEquals("spline derivative evaluation failed for x=" + x,
                   dp.value(x - knots[index]), dSpline.value(x), tolerance);
        }

        // knot points -- centering should zero arguments
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals("spline function evaluation failed for knot=" + knots[i],
                    polynomials[i].value(0), spline.value(knots[i]), tolerance);
            Assert.assertEquals("spline function evaluation failed for knot=" + knots[i],
                    dp.value(0), dSpline.value(knots[i]), tolerance);
        }

        try { //outside of domain -- under min
            x = spline.value(-1.5);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }

        try { //outside of domain -- over max
            x = spline.value(2.5);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
    }

    @Test
    public void testIsValidPoint() {
        final PolynomialSplineFunction spline =
            new PolynomialSplineFunction(knots, polynomials);
        final double xMin = knots[0];
        final double xMax = knots[knots.length - 1];

        double x;

        x = xMin;
        Assert.assertTrue(spline.isValidPoint(x));
        // Ensure that no exception is thrown.
        spline.value(x);

        x = xMax;
        Assert.assertTrue(spline.isValidPoint(x));
        // Ensure that no exception is thrown.
        spline.value(x);

        final double xRange = xMax - xMin;
        x = xMin + xRange / 3.4;
        Assert.assertTrue(spline.isValidPoint(x));
        // Ensure that no exception is thrown.
        spline.value(x);

        final double small = 1e-8;
        x = xMin - small;
        Assert.assertFalse(spline.isValidPoint(x));
        // Ensure that an exception would have been thrown.
        try {
            spline.value(x);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException expected) {}
    }

    /**
     *  Do linear search to find largest knot point less than or equal to x.
     *  Implementation does binary search.
     */
     protected int findKnot(double[] knots, double x) {
         if (x < knots[0] || x >= knots[knots.length -1]) {
             throw new OutOfRangeException(x, knots[0], knots[knots.length -1]);
         }
         for (int i = 0; i < knots.length; i++) {
             if (knots[i] > x) {
                 return i - 1;
             }
         }
         throw new MathIllegalStateException();
     }
}

