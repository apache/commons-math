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
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the bicubic function.
 */
public final class BicubicInterpolatingFunctionTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2.5};
        double[][] zval = new double[xval.length][yval.length];

        @SuppressWarnings("unused")
        BivariateFunction bcf = new BicubicInterpolatingFunction(xval, yval, zval,
                                                                 zval, zval, zval);

        double[] wxval = new double[] {3, 2, 5, 6.5};
        try {
            bcf = new BicubicInterpolatingFunction(wxval, yval, zval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }
        double[] wyval = new double[] {-4, -1, -1, 2.5};
        try {
            bcf = new BicubicInterpolatingFunction(xval, wyval, zval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }
        double[][] wzval = new double[xval.length][yval.length - 1];
        try {
            bcf = new BicubicInterpolatingFunction(xval, yval, wzval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicInterpolatingFunction(xval, yval, zval, wzval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicInterpolatingFunction(xval, yval, zval, zval, wzval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicInterpolatingFunction(xval, yval, zval, zval, zval, wzval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }

        wzval = new double[xval.length - 1][yval.length];
        try {
            bcf = new BicubicInterpolatingFunction(xval, yval, wzval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicInterpolatingFunction(xval, yval, zval, wzval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicInterpolatingFunction(xval, yval, zval, zval, wzval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicInterpolatingFunction(xval, yval, zval, zval, zval, wzval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
    }

    @Test
    public void testIsValidPoint() {
        final double xMin = -12;
        final double xMax = 34;
        final double yMin = 5;
        final double yMax = 67;
        final double[] xval = new double[] { xMin, xMax };
        final double[] yval = new double[] { yMin, yMax };
        final double[][] f = new double[][] { { 1, 2 },
                                              { 3, 4 } };
        final double[][] dFdX = f;
        final double[][] dFdY = f;
        final double[][] dFdXdY = f;

        final BicubicInterpolatingFunction bcf
            = new BicubicInterpolatingFunction(xval, yval, f,
                                                     dFdX, dFdY, dFdXdY);

        double x, y;

        x = xMin;
        y = yMin;
        Assert.assertTrue(bcf.isValidPoint(x, y));
        // Ensure that no exception is thrown.
        bcf.value(x, y);

        x = xMax;
        y = yMax;
        Assert.assertTrue(bcf.isValidPoint(x, y));
        // Ensure that no exception is thrown.
        bcf.value(x, y);

        final double xRange = xMax - xMin;
        final double yRange = yMax - yMin;
        x = xMin + xRange / 3.4;
        y = yMin + yRange / 1.2;
        Assert.assertTrue(bcf.isValidPoint(x, y));
        // Ensure that no exception is thrown.
        bcf.value(x, y);

        final double small = 1e-8;
        x = xMin - small;
        y = yMax;
        Assert.assertFalse(bcf.isValidPoint(x, y));
        // Ensure that an exception would have been thrown.
        try {
            bcf.value(x, y);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException expected) {}

        x = xMin;
        y = yMax + small;
        Assert.assertFalse(bcf.isValidPoint(x, y));
        // Ensure that an exception would have been thrown.
        try {
            bcf.value(x, y);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException expected) {}
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
        final int numberOfSamples = 1000;

        final double interpolationTolerance = 1e-15;
        final double maxTolerance = 1e-14;

        // Function values
        BivariateFunction f = new BivariateFunction() {
                public double value(double x, double y) {
                    return 2 * x - 3 * y + 5;
                }
            };
        BivariateFunction dfdx = new BivariateFunction() {
                public double value(double x, double y) {
                    return 2;
                }
            };
        BivariateFunction dfdy = new BivariateFunction() {
                public double value(double x, double y) {
                    return -3;
                }
            };
        BivariateFunction d2fdxdy = new BivariateFunction() {
                public double value(double x, double y) {
                    return 0;
                }
            };

        testInterpolation(minimumX,
                          maximumX,
                          minimumY,
                          maximumY,
                          numberOfElements,
                          numberOfSamples,
                          f,
                          dfdx,
                          dfdy,
                          d2fdxdy,
                          interpolationTolerance,
                          maxTolerance,
                          false);
    }

    /**
     * Interpolating a paraboloid.
     * <p>
     * z = 2 x<sup>2</sup> - 3 y<sup>2</sup> + 4 x y - 5
     */
    @Test
    public void testParaboloid() {
        final int numberOfElements = 10;
        final double minimumX = -10;
        final double maximumX = 10;
        final double minimumY = -10;
        final double maximumY = 10;
        final int numberOfSamples = 1000;

        final double interpolationTolerance = 2e-14;
        final double maxTolerance = 1e-12;

        // Function values
        BivariateFunction f = new BivariateFunction() {
                public double value(double x, double y) {
                    return 2 * x * x - 3 * y * y + 4 * x * y - 5;
                }
            };
        BivariateFunction dfdx = new BivariateFunction() {
                public double value(double x, double y) {
                    return 4 * (x + y);
                }
            };
        BivariateFunction dfdy = new BivariateFunction() {
                public double value(double x, double y) {
                    return 4 * x - 6 * y;
                }
            };
        BivariateFunction d2fdxdy = new BivariateFunction() {
                public double value(double x, double y) {
                    return 4;
                }
            };

        testInterpolation(minimumX,
                          maximumX,
                          minimumY,
                          maximumY,
                          numberOfElements,
                          numberOfSamples,
                          f,
                          dfdx,
                          dfdy,
                          d2fdxdy,
                          interpolationTolerance,
                          maxTolerance,
                          false);
    }

    /**
     * @param minimumX Lower bound of interpolation range along the x-coordinate.
     * @param maximumX Higher bound of interpolation range along the x-coordinate.
     * @param minimumY Lower bound of interpolation range along the y-coordinate.
     * @param maximumY Higher bound of interpolation range along the y-coordinate.
     * @param numberOfElements Number of data points (along each dimension).
     * @param numberOfSamples Number of test points.
     * @param f Function to test.
     * @param dfdx Partial derivative w.r.t. x of the function to test.
     * @param dfdy Partial derivative w.r.t. y of the function to test.
     * @param d2fdxdy Second partial cross-derivative of the function to test.
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
                                   BivariateFunction dfdx,
                                   BivariateFunction dfdy,
                                   BivariateFunction d2fdxdy,
                                   double meanTolerance,
                                   double maxTolerance,
                                   boolean print) {
        double expected;
        double actual;
        double currentX;
        double currentY;
        final double deltaX = (maximumX - minimumX) / numberOfElements;
        final double deltaY = (maximumY - minimumY) / numberOfElements;
        final double[] xValues = new double[numberOfElements];
        final double[] yValues = new double[numberOfElements];
        final double[][] zValues = new double[numberOfElements][numberOfElements];
        final double[][] dzdx = new double[numberOfElements][numberOfElements];
        final double[][] dzdy = new double[numberOfElements][numberOfElements];
        final double[][] d2zdxdy = new double[numberOfElements][numberOfElements];

        for (int i = 0; i < numberOfElements; i++) {
            xValues[i] = minimumX + deltaX * i;
            final double x = xValues[i];
            for (int j = 0; j < numberOfElements; j++) {
                yValues[j] = minimumY + deltaY * j;
                final double y = yValues[j];
                zValues[i][j] = f.value(x, y);
                dzdx[i][j] = dfdx.value(x, y);
                dzdy[i][j] = dfdy.value(x, y);
                d2zdxdy[i][j] = d2fdxdy.value(x, y);
            }
        }

        final BivariateFunction interpolation
            = new BicubicInterpolatingFunction(xValues,
                                               yValues,
                                               zValues,
                                               dzdx,
                                               dzdy,
                                               d2zdxdy);

        for (int i = 0; i < numberOfElements; i++) {
            currentX = xValues[i];
            for (int j = 0; j < numberOfElements; j++) {
                currentY = yValues[j];
                expected = f.value(currentX, currentY);
                actual = interpolation.value(currentX, currentY);
                Assert.assertTrue("On data point: " + expected + " != " + actual,
                                  Precision.equals(expected, actual));
            }
        }

        final RandomGenerator rng = new Well19937c(1234567L);
        final UniformRealDistribution distX = new UniformRealDistribution(rng, xValues[0], xValues[xValues.length - 1]);
        final UniformRealDistribution distY = new UniformRealDistribution(rng, yValues[0], yValues[yValues.length - 1]);

        double sumError = 0;
        for (int i = 0; i < numberOfSamples; i++) {
            currentX = distX.sample();
            currentY = distY.sample();
            expected = f.value(currentX, currentY);

            if (print) {
                System.out.println(currentX + " " + currentY + " -> ");
            }

            actual = interpolation.value(currentX, currentY);
            sumError += FastMath.abs(actual - expected);

            if (print) {
                System.out.println(actual + " (diff=" + (expected - actual) + ")");
            }

            Assert.assertEquals(expected, actual, maxTolerance);
        }

        final double meanError = sumError / numberOfSamples;
        Assert.assertEquals(0, meanError, meanTolerance);
    }
}
