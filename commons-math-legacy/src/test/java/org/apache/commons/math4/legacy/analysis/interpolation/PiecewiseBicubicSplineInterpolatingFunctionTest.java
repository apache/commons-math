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

import org.apache.commons.math4.legacy.analysis.BivariateFunction;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.UniformContinuousDistribution;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.InsufficientDataException;
import org.apache.commons.math4.legacy.exception.NonMonotonicSequenceException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.numbers.core.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the piecewise bicubic function.
 */
public final class PiecewiseBicubicSplineInterpolatingFunctionTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() {
        double[] xval = new double[] { 3, 4, 5, 6.5, 7.5 };
        double[] yval = new double[] { -4, -3, -1, 2.5, 3.5 };
        double[][] zval = new double[xval.length][yval.length];

        @SuppressWarnings("unused")
        PiecewiseBicubicSplineInterpolatingFunction bcf = new PiecewiseBicubicSplineInterpolatingFunction(xval, yval, zval);

        try {
            bcf = new PiecewiseBicubicSplineInterpolatingFunction(null, yval, zval);
            Assert.fail("Failed to detect x null pointer");
        } catch (NullArgumentException iae) {
            // Expected.
        }

        try {
            bcf = new PiecewiseBicubicSplineInterpolatingFunction(xval, null, zval);
            Assert.fail("Failed to detect y null pointer");
        } catch (NullArgumentException iae) {
            // Expected.
        }

        try {
            bcf = new PiecewiseBicubicSplineInterpolatingFunction(xval, yval, null);
            Assert.fail("Failed to detect z null pointer");
        } catch (NullArgumentException iae) {
            // Expected.
        }

        try {
            double xval1[] = { 0.0, 1.0, 2.0, 3.0 };
            bcf = new PiecewiseBicubicSplineInterpolatingFunction(xval1, yval, zval);
            Assert.fail("Failed to detect insufficient x data");
        } catch (InsufficientDataException iae) {
            // Expected.
        }

        try {
            double yval1[] = { 0.0, 1.0, 2.0, 3.0 };
            bcf = new PiecewiseBicubicSplineInterpolatingFunction(xval, yval1, zval);
            Assert.fail("Failed to detect insufficient y data");
        } catch (InsufficientDataException iae) {
            // Expected.
        }

        try {
            double zval1[][] = new double[4][4];
            bcf = new PiecewiseBicubicSplineInterpolatingFunction(xval, yval, zval1);
            Assert.fail("Failed to detect insufficient z data");
        } catch (InsufficientDataException iae) {
            // Expected.
        }

        try {
            double xval1[] = { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 };
            bcf = new PiecewiseBicubicSplineInterpolatingFunction(xval1, yval, zval);
            Assert.fail("Failed to detect data set array with different sizes.");
        } catch (DimensionMismatchException iae) {
            // Expected.
        }

        try {
            double yval1[] = { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 };
            bcf = new PiecewiseBicubicSplineInterpolatingFunction(xval, yval1, zval);
            Assert.fail("Failed to detect data set array with different sizes.");
        } catch (DimensionMismatchException iae) {
            // Expected.
        }

        // X values not sorted.
        try {
            double xval1[] = { 0.0, 1.0, 0.5, 7.0, 3.5 };
            bcf = new PiecewiseBicubicSplineInterpolatingFunction(xval1, yval, zval);
            Assert.fail("Failed to detect unsorted x arguments.");
        } catch (NonMonotonicSequenceException iae) {
            // Expected.
        }

        // Y values not sorted.
        try {
            double yval1[] = { 0.0, 1.0, 1.5, 0.0, 3.0 };
            bcf = new PiecewiseBicubicSplineInterpolatingFunction(xval, yval1, zval);
            Assert.fail("Failed to detect unsorted y arguments.");
        } catch (NonMonotonicSequenceException iae) {
            // Expected.
        }
    }

    /**
     * Interpolating a plane.
     * <p>
     * z = 2 x - 3 y + 5
     */
    @Test
    public void testPlane() {
        final int numberOfElements = 10;
        final double minimumX = -10;
        final double maximumX = 10;
        final double minimumY = -10;
        final double maximumY = 10;
        final int numberOfSamples = 100;

        final double interpolationTolerance = 7e-15;
        final double maxTolerance = 6e-14;

        // Function values
        BivariateFunction f = new BivariateFunction() {
                @Override
                public double value(double x, double y) {
                    return 2 * x - 3 * y + 5;
                }
            };

        testInterpolation(minimumX,
                          maximumX,
                          minimumY,
                          maximumY,
                          numberOfElements,
                          numberOfSamples,
                          f,
                          interpolationTolerance,
                          maxTolerance);
    }

    /**
     * Interpolating a paraboloid.
     * <p>
     * z = 2 x<sup>2</sup> - 3 y<sup>2</sup> + 4 x y - 5
     */
    @Test
    public void testParabaloid() {
        final int numberOfElements = 10;
        final double minimumX = -10;
        final double maximumX = 10;
        final double minimumY = -10;
        final double maximumY = 10;
        final int numberOfSamples = 100;

        final double interpolationTolerance = 1e-13;
        final double maxTolerance = 6e-14;

        // Function values
        BivariateFunction f = new BivariateFunction() {
                @Override
                public double value(double x, double y) {
                    return 2 * x * x - 3 * y * y + 4 * x * y - 5;
                }
            };

        testInterpolation(minimumX,
                          maximumX,
                          minimumY,
                          maximumY,
                          numberOfElements,
                          numberOfSamples,
                          f,
                          interpolationTolerance,
                          maxTolerance);
    }

    /**
     * @param minimumX Lower bound of interpolation range along the x-coordinate.
     * @param maximumX Higher bound of interpolation range along the x-coordinate.
     * @param minimumY Lower bound of interpolation range along the y-coordinate.
     * @param maximumY Higher bound of interpolation range along the y-coordinate.
     * @param numberOfElements Number of data points (along each dimension).
     * @param numberOfSamples Number of test points.
     * @param f Function to test.
     * @param meanTolerance Allowed average error (mean error on all interpolated values).
     * @param maxTolerance Allowed error on each interpolated value.
     */
    private void testInterpolation(double minimumX,
                                   double maximumX,
                                   double minimumY,
                                   double maximumY,
                                   int numberOfElements,
                                   int numberOfSamples,
                                   BivariateFunction f,
                                   double meanTolerance,
                                   double maxTolerance) {
        double expected;
        double actual;
        double currentX;
        double currentY;
        final double deltaX = (maximumX - minimumX) / ((double) numberOfElements);
        final double deltaY = (maximumY - minimumY) / ((double) numberOfElements);
        final double[] xValues = new double[numberOfElements];
        final double[] yValues = new double[numberOfElements];
        final double[][] zValues = new double[numberOfElements][numberOfElements];

        for (int i = 0; i < numberOfElements; i++) {
            xValues[i] = minimumX + deltaX * (double) i;
            for (int j = 0; j < numberOfElements; j++) {
                yValues[j] = minimumY + deltaY * (double) j;
                zValues[i][j] = f.value(xValues[i], yValues[j]);
            }
        }

        final BivariateFunction interpolation
            = new PiecewiseBicubicSplineInterpolatingFunction(xValues,
                                                              yValues,
                                                              zValues);

        for (int i = 0; i < numberOfElements; i++) {
            currentX = xValues[i];
            for (int j = 0; j < numberOfElements; j++) {
                currentY = yValues[j];
                expected = f.value(currentX, currentY);
                actual = interpolation.value(currentX, currentY);
                Assert.assertTrue(Precision.equals(expected, actual));
            }
        }

        final UniformRandomProvider rng = RandomSource.WELL_19937_C.create(1234567L);
        final ContinuousDistribution.Sampler distX = UniformContinuousDistribution.of(xValues[0], xValues[xValues.length - 1]).createSampler(rng);
        final ContinuousDistribution.Sampler distY = UniformContinuousDistribution.of(yValues[0], yValues[yValues.length - 1]).createSampler(rng);

        double sumError = 0;
        for (int i = 0; i < numberOfSamples; i++) {
            currentX = distX.sample();
            currentY = distY.sample();
            expected = f.value(currentX, currentY);
            actual = interpolation.value(currentX, currentY);
            sumError += JdkMath.abs(actual - expected);
            Assert.assertEquals(expected, actual, maxTolerance);
        }

        final double meanError = sumError / numberOfSamples;
        Assert.assertEquals(0, meanError, meanTolerance);
    }
}
