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

import java.util.function.DoubleBinaryOperator;
import org.apache.commons.math4.legacy.analysis.BivariateFunction;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.UniformContinuousDistribution;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.numbers.core.Precision;
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

        double x = xMin;
        double y = yMin;
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
     * \[
     *   z = 2 x - 3 y + 5
     * \]
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
                @Override
                public double value(double x, double y) {
                    return 2 * x - 3 * y + 5;
                }
            };
        BivariateFunction dfdx = new BivariateFunction() {
                @Override
                public double value(double x, double y) {
                    return 2;
                }
            };
        BivariateFunction dfdy = new BivariateFunction() {
                @Override
                public double value(double x, double y) {
                    return -3;
                }
            };
        BivariateFunction d2fdxdy = new BivariateFunction() {
                @Override
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
     * \[
     *   z = 2 x^2 - 3 y^2 + 4 x y - 5
     * \]
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
                @Override
                public double value(double x, double y) {
                    return 2 * x * x - 3 * y * y + 4 * x * y - 5;
                }
            };
        BivariateFunction dfdx = new BivariateFunction() {
                @Override
                public double value(double x, double y) {
                    return 4 * (x + y);
                }
            };
        BivariateFunction dfdy = new BivariateFunction() {
                @Override
                public double value(double x, double y) {
                    return 4 * x - 6 * y;
                }
            };
        BivariateFunction d2fdxdy = new BivariateFunction() {
                @Override
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
     * Test for partial derivatives of {@link BicubicFunction}.
     * \[
     *   f(x, y) = \Sigma_i \Sigma_j (i+1) (j+2) x^i y^j
     * \]
     */
    @Test
    public void testSplinePartialDerivatives() {
        final int N = 4;
        final double[] coeff = new double[16];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                coeff[i + N * j] = (i + 1) * (j + 2);
            }
        }

        final BicubicFunction f = new BicubicFunction(coeff, 1, 1, true);
        BivariateFunction derivative;
        final double x = 0.435;
        final double y = 0.776;
        final double tol = 1e-13;

        derivative = new BivariateFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double y2 = y * y;
                    final double y3 = y2 * y;
                    final double yFactor = 2 + 3 * y + 4 * y2 + 5 * y3;
                    return yFactor * (2 + 6 * x + 12 * x2);
                }
            };
        Assert.assertEquals("dFdX", derivative.value(x, y),
                            f.partialDerivativeX().value(x, y), tol);

        derivative = new BivariateFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double x3 = x2 * x;
                    final double y2 = y * y;
                    final double xFactor = 1 + 2 * x + 3 * x2 + 4 * x3;
                    return xFactor * (3 + 8 * y + 15 * y2);
                }
            };
        Assert.assertEquals("dFdY", derivative.value(x, y),
                            f.partialDerivativeY().value(x, y), tol);

        derivative = new BivariateFunction() {
                public double value(double x, double y) {
                    final double y2 = y * y;
                    final double y3 = y2 * y;
                    final double yFactor = 2 + 3 * y + 4 * y2 + 5 * y3;
                    return yFactor * (6 + 24 * x);
                }
            };
        Assert.assertEquals("d2FdX2", derivative.value(x, y),
                            f.partialDerivativeXX().value(x, y), tol);

        derivative = new BivariateFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double x3 = x2 * x;
                    final double xFactor = 1 + 2 * x + 3 * x2 + 4 * x3;
                    return xFactor * (8 + 30 * y);
                }
            };
        Assert.assertEquals("d2FdY2", derivative.value(x, y),
                            f.partialDerivativeYY().value(x, y), tol);

        derivative = new BivariateFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double y2 = y * y;
                    final double yFactor = 3 + 8 * y + 15 * y2;
                    return yFactor * (2 + 6 * x + 12 * x2);
                }
            };
        Assert.assertEquals("d2FdXdY", derivative.value(x, y),
                            f.partialDerivativeXY().value(x, y), tol);
    }

    /**
     * Test that the partial derivatives computed from a
     * {@link BicubicInterpolatingFunction} match the input data.
     * \[
     *   f(x, y) = 5
     *             - 3 x + 2 y
     *             - x y + 2 x^2 - 3 y^2
     *             + 4 x^2 y - x y^2 - 3 x^3 + y^3
     * \]
     */
    @Test
    public void testMatchingPartialDerivatives() {
        final int sz = 21;
        double[] xval = new double[sz];
        double[] yval = new double[sz];
        // Coordinate values
        final double delta = 1d / (sz - 1);
        for (int i = 0; i < sz; i++) {
            xval[i] = i * delta;
            yval[i] = i * delta / 3;
        }
        // Function values
        BivariateFunction f = new BivariateFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double x3 = x2 * x;
                    final double y2 = y * y;
                    final double y3 = y2 * y;

                    return 5
                        - 3 * x + 2 * y
                        - x * y + 2 * x2 - 3 * y2
                        + 4 * x2 * y - x * y2 - 3 * x3 + y3;
                }
            };
        double[][] fval = new double[sz][sz];
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                fval[i][j] = f.value(xval[i], yval[j]);
            }
        }
        // Partial derivatives with respect to x
        double[][] dFdX = new double[sz][sz];
        BivariateFunction dfdX = new BivariateFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double y2 = y * y;
                    return - 3 - y + 4 * x + 8 * x * y - y2 - 9 * x2;
                }
            };
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                dFdX[i][j] = dfdX.value(xval[i], yval[j]);
            }
        }
        // Partial derivatives with respect to y
        double[][] dFdY = new double[sz][sz];
        BivariateFunction dfdY = new BivariateFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double y2 = y * y;
                    return 2 - x - 6 * y + 4 * x2 - 2 * x * y + 3 * y2;
                }
            };
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                dFdY[i][j] = dfdY.value(xval[i], yval[j]);
            }
        }
        // Second partial derivatives with respect to x
        double[][] d2Fd2X = new double[sz][sz];
        BivariateFunction d2fd2X = new BivariateFunction() {
                public double value(double x, double y) {
                    return 4 + 8 * y - 18 * x;
                }
            };
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                d2Fd2X[i][j] = d2fd2X.value(xval[i], yval[j]);
            }
        }
        // Second partial derivatives with respect to y
        double[][] d2Fd2Y = new double[sz][sz];
        BivariateFunction d2fd2Y = new BivariateFunction() {
                public double value(double x, double y) {
                    return - 6 - 2 * x + 6 * y;
                }
            };
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                d2Fd2Y[i][j] = d2fd2Y.value(xval[i], yval[j]);
            }
        }
        // Partial cross-derivatives
        double[][] d2FdXdY = new double[sz][sz];
        BivariateFunction d2fdXdY = new BivariateFunction() {
                public double value(double x, double y) {
                    return -1 + 8 * x - 2 * y;
                }
            };
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                d2FdXdY[i][j] = d2fdXdY.value(xval[i], yval[j]);
            }
        }

        BicubicInterpolatingFunction bcf
            = new BicubicInterpolatingFunction(xval, yval, fval, dFdX, dFdY, d2FdXdY, true);
        DoubleBinaryOperator partialDerivativeX = bcf.partialDerivativeX();
        DoubleBinaryOperator partialDerivativeY = bcf.partialDerivativeY();
        DoubleBinaryOperator partialDerivativeXX = bcf.partialDerivativeXX();
        DoubleBinaryOperator partialDerivativeYY = bcf.partialDerivativeYY();
        DoubleBinaryOperator partialDerivativeXY = bcf.partialDerivativeXY();

        double x;
        double y;
        double expected;
        double result;

        final double tol = 1e-10;
        for (int i = 0; i < sz; i++) {
            x = xval[i];
            for (int j = 0; j < sz; j++) {
                y = yval[j];

                expected = dfdX.value(x, y);
                result = partialDerivativeX.applyAsDouble(x, y);
                Assert.assertEquals(x + " " + y + " dFdX", expected, result, tol);

                expected = dfdY.value(x, y);
                result = partialDerivativeY.applyAsDouble(x, y);
                Assert.assertEquals(x + " " + y + " dFdY", expected, result, tol);

                expected = d2fd2X.value(x, y);
                result = partialDerivativeXX.applyAsDouble(x, y);
                Assert.assertEquals(x + " " + y + " d2Fd2X", expected, result, tol);

                expected = d2fd2Y.value(x, y);
                result = partialDerivativeYY.applyAsDouble(x, y);
                Assert.assertEquals(x + " " + y + " d2Fd2Y", expected, result, tol);

                expected = d2fdXdY.value(x, y);
                result = partialDerivativeXY.applyAsDouble(x, y);
                Assert.assertEquals(x + " " + y + " d2FdXdY", expected, result, tol);
            }
        }
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

        final UniformRandomProvider rng = RandomSource.WELL_19937_C.create(1234567L);
        final ContinuousDistribution.Sampler distX = UniformContinuousDistribution.of(xValues[0], xValues[xValues.length - 1]).createSampler(rng);
        final ContinuousDistribution.Sampler distY = UniformContinuousDistribution.of(yValues[0], yValues[yValues.length - 1]).createSampler(rng);

        double sumError = 0;
        for (int i = 0; i < numberOfSamples; i++) {
            currentX = distX.sample();
            currentY = distY.sample();
            expected = f.value(currentX, currentY);

            if (print) {
                System.out.println(currentX + " " + currentY + " -> ");
            }

            actual = interpolation.value(currentX, currentY);
            sumError += JdkMath.abs(actual - expected);

            if (print) {
                System.out.println(actual + " (diff=" + (expected - actual) + ")");
            }

            Assert.assertEquals(expected, actual, maxTolerance);
        }

        final double meanError = sumError / numberOfSamples;
        Assert.assertEquals(0, meanError, meanTolerance);
    }
}
