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
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the bicubic interpolator.
 *
 * @deprecated as of 3.4 replaced by {@link org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolator}
 */
@Deprecated
public final class BicubicSplineInterpolatorTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2.5};
        double[][] zval = new double[xval.length][yval.length];

        BivariateGridInterpolator interpolator = new BicubicSplineInterpolator();

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
    }

    /**
     * Interpolating a plane.
     * <p>
     * z = 2 x - 3 y + 5
     */
    @Test
    public void testInterpolation1() {
        final int sz = 21;
        double[] xval = new double[sz];
        double[] yval = new double[sz];
        // Coordinate values
        final double delta = 1d / (sz - 1);
        for (int i = 0; i < sz; i++) {
            xval[i] = -1 + 15 * i * delta;
            yval[i] = -20 + 30 * i * delta;
        }

        // Function values
        BivariateFunction f = new BivariateFunction() {
                public double value(double x, double y) {
                    return 2 * x - 3 * y + 5;
                }
            };
        double[][] zval = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                zval[i][j] = f.value(xval[i], yval[j]);
            }
        }

        BivariateGridInterpolator interpolator = new BicubicSplineInterpolator();
        BivariateFunction p = interpolator.interpolate(xval, yval, zval);
        double x, y;

        final RandomGenerator rng = new Well19937c(1234567L); // "tol" depends on the seed.
        final UniformRealDistribution distX
            = new UniformRealDistribution(rng, xval[0], xval[xval.length - 1]);
        final UniformRealDistribution distY
            = new UniformRealDistribution(rng, yval[0], yval[yval.length - 1]);

        final int numSamples = 50;
        final double tol = 6;
        for (int i = 0; i < numSamples; i++) {
            x = distX.sample();
            for (int j = 0; j < numSamples; j++) {
                y = distY.sample();
//                 System.out.println(x + " " + y + " " + f.value(x, y) + " " + p.value(x, y));
                Assert.assertEquals(f.value(x, y),  p.value(x, y), tol);
            }
//             System.out.println();
        }
    }

    /**
     * Interpolating a paraboloid.
     * <p>
     * z = 2 x<sup>2</sup> - 3 y<sup>2</sup> + 4 x y - 5
     */
    @Test
    public void testInterpolation2() {
        final int sz = 21;
        double[] xval = new double[sz];
        double[] yval = new double[sz];
        // Coordinate values
        final double delta = 1d / (sz - 1);
        for (int i = 0; i < sz; i++) {
            xval[i] = -1 + 15 * i * delta;
            yval[i] = -20 + 30 * i * delta;
        }

        // Function values
        BivariateFunction f = new BivariateFunction() {
                public double value(double x, double y) {
                    return 2 * x * x - 3 * y * y + 4 * x * y - 5;
                }
            };
        double[][] zval = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                zval[i][j] = f.value(xval[i], yval[j]);
            }
        }

        BivariateGridInterpolator interpolator = new BicubicSplineInterpolator();
        BivariateFunction p = interpolator.interpolate(xval, yval, zval);
        double x, y;

        final RandomGenerator rng = new Well19937c(1234567L); // "tol" depends on the seed.
        final UniformRealDistribution distX
            = new UniformRealDistribution(rng, xval[0], xval[xval.length - 1]);
        final UniformRealDistribution distY
            = new UniformRealDistribution(rng, yval[0], yval[yval.length - 1]);

        final int numSamples = 50;
        final double tol = 251;
        for (int i = 0; i < numSamples; i++) {
            x = distX.sample();
            for (int j = 0; j < numSamples; j++) {
                y = distY.sample();
//                 System.out.println(x + " " + y + " " + f.value(x, y) + " " + p.value(x, y));
                Assert.assertEquals(f.value(x, y),  p.value(x, y), tol);
            }
//             System.out.println();
        }
    }
}
