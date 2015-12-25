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
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

/**
 * Test case for the bicubic function.
 *
 * @deprecated as of 3.4 replaced by
 * {@link org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction}
 */
@Deprecated
public final class BicubicSplineInterpolatingFunctionTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2.5};
        double[][] zval = new double[xval.length][yval.length];

        @SuppressWarnings("unused")
        BivariateFunction bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval,
                                                                           zval, zval, zval);

        double[] wxval = new double[] {3, 2, 5, 6.5};
        try {
            bcf = new BicubicSplineInterpolatingFunction(wxval, yval, zval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }
        double[] wyval = new double[] {-4, -1, -1, 2.5};
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, wyval, zval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }
        double[][] wzval = new double[xval.length][yval.length - 1];
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, wzval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, wzval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, zval, wzval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, zval, zval, wzval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }

        wzval = new double[xval.length - 1][yval.length];
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, wzval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, wzval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, zval, wzval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, zval, zval, wzval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
    }

    /**
     * Test for a plane.
     * <p>
     * z = 2 x - 3 y + 5
     */
    @Ignore@Test
    public void testPlane() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2, 2.5};
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
        // Partial derivatives with respect to x
        double[][] dZdX = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdX[i][j] = 2;
            }
        }
        // Partial derivatives with respect to y
        double[][] dZdY = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdY[i][j] = -3;
            }
        }
        // Partial cross-derivatives
        double[][] dZdXdY = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdXdY[i][j] = 0;
            }
        }

        BivariateFunction bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval,
                                                                           dZdX, dZdY, dZdXdY);
        double x, y;
        double expected, result;

        x = 4;
        y = -3;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("On sample point",
                            expected, result, 1e-15);

        x = 4.5;
        y = -1.5;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("Half-way between sample points (middle of the patch)",
                            expected, result, 0.3);

        x = 3.5;
        y = -3.5;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("Half-way between sample points (border of the patch)",
                            expected, result, 0.3);
    }

    /**
     * Test for a paraboloid.
     * <p>
     * z = 2 x<sup>2</sup> - 3 y<sup>2</sup> + 4 x y - 5
     */
    @Ignore@Test
    public void testParaboloid() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2, 2.5};
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
        // Partial derivatives with respect to x
        double[][] dZdX = new double[xval.length][yval.length];
        BivariateFunction dfdX = new BivariateFunction() {
                public double value(double x, double y) {
                    return 4 * (x + y);
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdX[i][j] = dfdX.value(xval[i], yval[j]);
            }
        }
        // Partial derivatives with respect to y
        double[][] dZdY = new double[xval.length][yval.length];
        BivariateFunction dfdY = new BivariateFunction() {
                public double value(double x, double y) {
                    return 4 * x - 6 * y;
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdY[i][j] = dfdY.value(xval[i], yval[j]);
            }
        }
        // Partial cross-derivatives
        double[][] dZdXdY = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdXdY[i][j] = 4;
            }
        }

        BivariateFunction bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval,
                                                                           dZdX, dZdY, dZdXdY);
        double x, y;
        double expected, result;

        x = 4;
        y = -3;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("On sample point",
                            expected, result, 1e-15);

        x = 4.5;
        y = -1.5;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("Half-way between sample points (middle of the patch)",
                            expected, result, 2);

        x = 3.5;
        y = -3.5;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("Half-way between sample points (border of the patch)",
                            expected, result, 2);
    }

    /**
     * Test for partial derivatives of {@link BicubicSplineFunction}.
     * <p>
     * f(x, y) = &Sigma;<sub>i</sub>&Sigma;<sub>j</sub> (i+1) (j+2) x<sup>i</sup> y<sup>j</sup>
     */
    @Ignore@Test
    public void testSplinePartialDerivatives() {
        final int N = 4;
        final double[] coeff = new double[16];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                coeff[i + N * j] = (i + 1) * (j + 2);
            }
        }

        final BicubicSplineFunction f = new BicubicSplineFunction(coeff);
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
     * {@link BicubicSplineInterpolatingFunction} match the input data.
     * <p>
     * f(x, y) = 5
     *           - 3 x + 2 y
     *           - x y + 2 x<sup>2</sup> - 3 y<sup>2</sup>
     *           + 4 x<sup>2</sup> y - x y<sup>2</sup> - 3 x<sup>3</sup> + y<sup>3</sup>
     */
    @Ignore@Test
    public void testMatchingPartialDerivatives() {
        final int sz = 21;
        double[] val = new double[sz];
        // Coordinate values
        final double delta = 1d / (sz - 1);
        for (int i = 0; i < sz; i++) {
            val[i] = i * delta;
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
                fval[i][j] = f.value(val[i], val[j]);
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
                dFdX[i][j] = dfdX.value(val[i], val[j]);
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
                dFdY[i][j] = dfdY.value(val[i], val[j]);
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
                d2FdXdY[i][j] = d2fdXdY.value(val[i], val[j]);
            }
        }

        BicubicSplineInterpolatingFunction bcf
            = new BicubicSplineInterpolatingFunction(val, val, fval, dFdX, dFdY, d2FdXdY);

        double x, y;
        double expected, result;

        final double tol = 1e-12;
        for (int i = 0; i < sz; i++) {
            x = val[i];
            for (int j = 0; j < sz; j++) {
                y = val[j];

                expected = dfdX.value(x, y);
                result = bcf.partialDerivativeX(x, y);
                Assert.assertEquals(x + " " + y + " dFdX", expected, result, tol);

                expected = dfdY.value(x, y);
                result = bcf.partialDerivativeY(x, y);
                Assert.assertEquals(x + " " + y + " dFdY", expected, result, tol);

                expected = d2fdXdY.value(x, y);
                result = bcf.partialDerivativeXY(x, y);
                Assert.assertEquals(x + " " + y + " d2FdXdY", expected, result, tol);
            }
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
        // Partial derivatives with respect to x
        double[][] dZdX = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdX[i][j] = 2;
            }
        }
        // Partial derivatives with respect to y
        double[][] dZdY = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdY[i][j] = -3;
            }
        }
        // Partial cross-derivatives
        double[][] dZdXdY = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdXdY[i][j] = 0;
            }
        }

        final BivariateFunction bcf
            = new BicubicSplineInterpolatingFunction(xval, yval, zval,
                                                     dZdX, dZdY, dZdXdY);
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
//                 System.out.println(x + " " + y + " " + f.value(x, y) + " " + bcf.value(x, y));
                Assert.assertEquals(f.value(x, y),  bcf.value(x, y), tol);
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
        // Partial derivatives with respect to x
        double[][] dZdX = new double[xval.length][yval.length];
        BivariateFunction dfdX = new BivariateFunction() {
                public double value(double x, double y) {
                    return 4 * (x + y);
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdX[i][j] = dfdX.value(xval[i], yval[j]);
            }
        }
        // Partial derivatives with respect to y
        double[][] dZdY = new double[xval.length][yval.length];
        BivariateFunction dfdY = new BivariateFunction() {
                public double value(double x, double y) {
                    return 4 * x - 6 * y;
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdY[i][j] = dfdY.value(xval[i], yval[j]);
            }
        }
        // Partial cross-derivatives
        double[][] dZdXdY = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdXdY[i][j] = 4;
            }
        }

        BivariateFunction bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval,
                                                                       dZdX, dZdY, dZdXdY);
        double x, y;

        final RandomGenerator rng = new Well19937c(1234567L); // "tol" depends on the seed.
        final UniformRealDistribution distX
            = new UniformRealDistribution(rng, xval[0], xval[xval.length - 1]);
        final UniformRealDistribution distY
            = new UniformRealDistribution(rng, yval[0], yval[yval.length - 1]);

        final double tol = 224;
        for (int i = 0; i < sz; i++) {
            x = distX.sample();
            for (int j = 0; j < sz; j++) {
                y = distY.sample();
//                 System.out.println(x + " " + y + " " + f.value(x, y) + " " + bcf.value(x, y));
                Assert.assertEquals(f.value(x, y),  bcf.value(x, y), tol);
            }
//             System.out.println();
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

        final BicubicSplineInterpolatingFunction bcf
            = new BicubicSplineInterpolatingFunction(xval, yval, f,
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
}
