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
package org.apache.commons.math4.analysis.interpolation;

import org.apache.commons.math4.analysis.BivariateFunction;
import org.apache.commons.math4.distribution.RealDistribution;
import org.apache.commons.math4.distribution.UniformRealDistribution;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the bicubic interpolator.
 */
public final class BicubicInterpolatorTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2.5};
        double[][] zval = new double[xval.length][yval.length];

        BivariateGridInterpolator interpolator = new BicubicInterpolator();

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
    public void testPlane() {
        BivariateFunction f = new BivariateFunction() {
                @Override
                public double value(double x, double y) {
                    return 2 * x - 3 * y + 5;
                }
            };

        testInterpolation(3000,
                          1e-13,
                          f,
                          false);
    }

    /**
     * Interpolating a paraboloid.
     * <p>
     * z = 2 x<sup>2</sup> - 3 y<sup>2</sup> + 4 x y - 5
     */
    @Test
    public void testParaboloid() {
        BivariateFunction f = new BivariateFunction() {
                @Override
                public double value(double x, double y) {
                    return 2 * x * x - 3 * y * y + 4 * x * y - 5;
                }
            };

        testInterpolation(3000,
                          1e-12,
                          f,
                          false);
    }

    /**
     * @param numSamples Number of test samples.
     * @param tolerance Allowed tolerance on the interpolated value.
     * @param f Test function.
     * @param print Whether to print debugging output to the console.
     */
    private void testInterpolation(int numSamples,
                                   double tolerance,
                                   BivariateFunction f,
                                   boolean print) {
        final int sz = 21;
        final double[] xval = new double[sz];
        final double[] yval = new double[sz];
        // Coordinate values
        final double delta = 1d / (sz - 1);
        for (int i = 0; i < sz; i++) {
            xval[i] = -1 + 15 * i * delta;
            yval[i] = -20 + 30 * i * delta;
        }

        final double[][] zval = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                zval[i][j] = f.value(xval[i], yval[j]);
            }
        }

        final BicubicInterpolator interpolator = new BicubicInterpolator();
        final BicubicInterpolatingFunction p = interpolator.interpolate(xval, yval, zval);
        double x, y;

        final UniformRandomProvider rng = RandomSource.create(RandomSource.WELL_19937_C);
        final RealDistribution.Sampler distX = new UniformRealDistribution(xval[0], xval[xval.length - 1]).createSampler(rng);
        final RealDistribution.Sampler distY = new UniformRealDistribution(yval[0], yval[yval.length - 1]).createSampler(rng);

        int count = 0;
        while (true) {
            x = distX.sample();
            y = distY.sample();
            if (!p.isValidPoint(x, y)) {
                if (print) {
                    System.out.println("# " + x + " " + y);
                }
                continue;
            }

            if (count++ > numSamples) {
                break;
            }
            final double expected = f.value(x, y);
            final double actual = p.value(x, y);

            if (print) {
                System.out.println(x + " " + y + " " + expected + " " + actual);
            }

            Assert.assertEquals(expected, actual, tolerance);
        }
    }
}
