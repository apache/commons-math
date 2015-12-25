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

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the smoothing bicubic interpolator.
 *
 * @deprecated To be removed in 4.0 (see MATH-1166).
 */
@Deprecated
public final class SmoothingPolynomialBicubicSplineInterpolatorTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() {
        double[] xval = new double[] {3, 4, 5, 6.5, 7.5};
        double[] yval = new double[] {-4, -3, -1, 2.5, 3};
        double[][] zval = new double[xval.length][yval.length];

        BivariateGridInterpolator interpolator = new SmoothingPolynomialBicubicSplineInterpolator(0);

        @SuppressWarnings("unused")
        BivariateFunction p = interpolator.interpolate(xval, yval, zval);

        double[] wxval = new double[] {3, 2, 5, 6.5};
        try {
            p = interpolator.interpolate(wxval, yval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }

        double[] wyval = new double[] {-4, -3, -1, -1};
        try {
            p = interpolator.interpolate(xval, wyval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }

        double[][] wzval = new double[xval.length][yval.length + 1];
        try {
            p = interpolator.interpolate(xval, yval, wzval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        wzval = new double[xval.length - 1][yval.length];
        try {
            p = interpolator.interpolate(xval, yval, wzval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        wzval = new double[xval.length][yval.length - 1];
        try {
            p = interpolator.interpolate(xval, yval, wzval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
    }

    /**
     * Test of interpolator for a plane.
     * <p>
     * z = 2 x - 3 y + 5
     */
    @Test
    public void testPlane() {
        BivariateFunction f = new BivariateFunction() {
                public double value(double x, double y) {
                    return 2 * x - 3 * y + 5
                        + ((int) (FastMath.abs(5 * x + 3 * y)) % 2 == 0 ? 1 : -1);
                }
            };

        BivariateGridInterpolator interpolator = new SmoothingPolynomialBicubicSplineInterpolator(1);

        double[] xval = new double[] {3, 4, 5, 6.5, 7.5};
        double[] yval = new double[] {-4, -3, -1, 2, 2.5, 3.5};
        double[][] zval = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                zval[i][j] = f.value(xval[i], yval[j]);
            }
        }

        BivariateFunction p = interpolator.interpolate(xval, yval, zval);
        double x, y;
        double expected, result;

        x = 4;
        y = -3;
        expected = f.value(x, y);
        result = p.value(x, y);
        Assert.assertEquals("On sample point", expected, result, 2);

        x = 4.5;
        y = -1.5;
        expected = f.value(x, y);
        result = p.value(x, y);
        Assert.assertEquals("half-way between sample points (middle of the patch)", expected, result, 2);

        x = 3.5;
        y = -3.5;
        expected = f.value(x, y);
        result = p.value(x, y);
        Assert.assertEquals("half-way between sample points (border of the patch)", expected, result, 2);
    }

    /**
     * Test of interpolator for a paraboloid.
     * <p>
     * z = 2 x<sup>2</sup> - 3 y<sup>2</sup> + 4 x y - 5
     */
    @Test
    public void testParaboloid() {
        BivariateFunction f = new BivariateFunction() {
                public double value(double x, double y) {
                    return 2 * x * x - 3 * y * y + 4 * x * y - 5
                        + ((int) (FastMath.abs(5 * x + 3 * y)) % 2 == 0 ? 1 : -1);
                }
            };

        BivariateGridInterpolator interpolator = new SmoothingPolynomialBicubicSplineInterpolator(4);

        double[] xval = new double[] {3, 4, 5, 6.5, 7.5, 8};
        double[] yval = new double[] {-4, -3, -2, -1, 0.5, 2.5};
        double[][] zval = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                zval[i][j] = f.value(xval[i], yval[j]);
            }
        }

        BivariateFunction p = interpolator.interpolate(xval, yval, zval);
        double x, y;
        double expected, result;

        x = 5;
        y = 0.5;
        expected = f.value(x, y);
        result = p.value(x, y);
        Assert.assertEquals("On sample point", expected, result, 2);

        x = 4.5;
        y = -1.5;
        expected = f.value(x, y);
        result = p.value(x, y);
        Assert.assertEquals("half-way between sample points (middle of the patch)", expected, result, 2);

        x = 3.5;
        y = -3.5;
        expected = f.value(x, y);
        result = p.value(x, y);
        Assert.assertEquals("half-way between sample points (border of the patch)", expected, result, 2);
    }
}
