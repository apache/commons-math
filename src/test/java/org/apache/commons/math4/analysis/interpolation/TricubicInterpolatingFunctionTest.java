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

import org.apache.commons.math4.analysis.TrivariateFunction;
import org.apache.commons.math4.distribution.UniformRealDistribution;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well19937c;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the bicubic function.
 */
public final class TricubicInterpolatingFunctionTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2.5};
        double[] zval = new double[] {-12, -8, -5.5, -3, 0, 2.5};
        double[][][] fval = new double[xval.length][yval.length][zval.length];

        @SuppressWarnings("unused")
        TrivariateFunction tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                                   fval, fval, fval, fval,
                                                                   fval, fval, fval, fval);

        double[] wxval = new double[] {3, 2, 5, 6.5};
        try {
            tcf = new TricubicInterpolatingFunction(wxval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }
        double[] wyval = new double[] {-4, -1, -1, 2.5};
        try {
            tcf = new TricubicInterpolatingFunction(xval, wyval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }
        double[] wzval = new double[] {-12, -8, -9, -3, 0, 2.5};
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, wzval,
                                                    fval, fval, fval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }
        double[][][] wfval = new double[xval.length - 1][yval.length - 1][zval.length];
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    wfval, fval, fval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, wfval, fval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, wfval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, wfval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    wfval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, wfval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, fval, wfval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, fval, fval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        wfval = new double[xval.length][yval.length - 1][zval.length];
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    wfval, fval, fval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, wfval, fval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, wfval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, wfval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    wfval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, wfval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, fval, wfval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, fval, fval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        wfval = new double[xval.length][yval.length][zval.length - 1];
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    wfval, fval, fval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, wfval, fval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, wfval, fval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, wfval,
                                                    fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    wfval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, wfval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, fval, wfval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicInterpolatingFunction(xval, yval, zval,
                                                    fval, fval, fval, fval,
                                                    fval, fval, fval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
    }

    /**
     * @param minimumX Lower bound of interpolation range along the x-coordinate.
     * @param maximumX Higher bound of interpolation range along the x-coordinate.
     * @param minimumY Lower bound of interpolation range along the y-coordinate.
     * @param maximumY Higher bound of interpolation range along the y-coordinate.
     * @param minimumZ Lower bound of interpolation range along the z-coordinate.
     * @param maximumZ Higher bound of interpolation range along the z-coordinate.
     * @param numberOfElements Number of data points (along each dimension).
     * @param numberOfSamples Number of test points.
     * @param f Function to test.
     * @param dfdx Partial derivative w.r.t. x of the function to test.
     * @param dfdy Partial derivative w.r.t. y of the function to test.
     * @param dfdz Partial derivative w.r.t. z of the function to test.
     * @param d2fdxdy Second partial cross-derivative w.r.t x and y of the function to test.
     * @param d2fdxdz Second partial cross-derivative w.r.t x and z of the function to test.
     * @param d2fdydz Second partial cross-derivative w.r.t y and z of the function to test.
     * @param d3fdxdy Third partial cross-derivative w.r.t x, y and z of the function to test.
     * @param meanRelativeTolerance Allowed average error (mean error on all interpolated values).
     * @param maxRelativeTolerance Allowed error on each interpolated value.
     * @param onDataMaxAbsoluteTolerance Allowed error on a data point.
     */
    private void testInterpolation(double minimumX,
                                   double maximumX,
                                   double minimumY,
                                   double maximumY,
                                   double minimumZ,
                                   double maximumZ,
                                   int numberOfElements,
                                   int numberOfSamples,
                                   TrivariateFunction f,
                                   TrivariateFunction dfdx,
                                   TrivariateFunction dfdy,
                                   TrivariateFunction dfdz,
                                   TrivariateFunction d2fdxdy,
                                   TrivariateFunction d2fdxdz,
                                   TrivariateFunction d2fdydz,
                                   TrivariateFunction d3fdxdydz,
                                   double meanRelativeTolerance,
                                   double maxRelativeTolerance,
                                   double onDataMaxAbsoluteTolerance,
                                   boolean print) {
        double expected;
        double actual;
        double currentX;
        double currentY;
        double currentZ;
        final double deltaX = (maximumX - minimumX) / numberOfElements;
        final double deltaY = (maximumY - minimumY) / numberOfElements;
        final double deltaZ = (maximumZ - minimumZ) / numberOfElements;
        final double[] xValues = new double[numberOfElements];
        final double[] yValues = new double[numberOfElements];
        final double[] zValues = new double[numberOfElements];
        final double[][][] fValues = new double[numberOfElements][numberOfElements][numberOfElements];
        final double[][][] dfdxValues = new double[numberOfElements][numberOfElements][numberOfElements];
        final double[][][] dfdyValues = new double[numberOfElements][numberOfElements][numberOfElements];
        final double[][][] dfdzValues = new double[numberOfElements][numberOfElements][numberOfElements];
        final double[][][] d2fdxdyValues = new double[numberOfElements][numberOfElements][numberOfElements];
        final double[][][] d2fdxdzValues = new double[numberOfElements][numberOfElements][numberOfElements];
        final double[][][] d2fdydzValues = new double[numberOfElements][numberOfElements][numberOfElements];
        final double[][][] d3fdxdydzValues = new double[numberOfElements][numberOfElements][numberOfElements];

        for (int i = 0; i < numberOfElements; i++) {
            xValues[i] = minimumX + deltaX * i;
            final double x = xValues[i];
            for (int j = 0; j < numberOfElements; j++) {
                yValues[j] = minimumY + deltaY * j;
                final double y = yValues[j];
                for (int k = 0; k < numberOfElements; k++) {
                    zValues[k] = minimumZ + deltaZ * k;
                    final double z = zValues[k];
                    fValues[i][j][k] = f.value(x, y, z);
                    dfdxValues[i][j][k] = dfdx.value(x, y, z);
                    dfdyValues[i][j][k] = dfdy.value(x, y, z);
                    dfdzValues[i][j][k] = dfdz.value(x, y, z);
                    d2fdxdyValues[i][j][k] = d2fdxdy.value(x, y, z);
                    d2fdxdzValues[i][j][k] = d2fdxdz.value(x, y, z);
                    d2fdydzValues[i][j][k] = d2fdydz.value(x, y, z);
                    d3fdxdydzValues[i][j][k] = d3fdxdydz.value(x, y, z);
                }
            }
        }

        final TrivariateFunction interpolation
            = new TricubicInterpolatingFunction(xValues,
                                                yValues,
                                                zValues,
                                                fValues,
                                                dfdxValues,
                                                dfdyValues,
                                                dfdzValues,
                                                d2fdxdyValues,
                                                d2fdxdzValues,
                                                d2fdydzValues,
                                                d3fdxdydzValues);

        for (int i = 0; i < numberOfElements; i++) {
            currentX = xValues[i];
            for (int j = 0; j < numberOfElements; j++) {
                currentY = yValues[j];
                for (int k = 0; k < numberOfElements; k++) {
                    currentZ = zValues[k];
                    expected = f.value(currentX, currentY, currentZ);
                    actual = interpolation.value(currentX, currentY, currentZ);
                    Assert.assertTrue("On data point: " + expected + " != " + actual,
                                      Precision.equals(expected, actual, onDataMaxAbsoluteTolerance));
                }
            }
        }

        final RandomGenerator rng = new Well19937c(1234567L);
        final UniformRealDistribution distX = new UniformRealDistribution(rng, xValues[0], xValues[xValues.length - 1]);
        final UniformRealDistribution distY = new UniformRealDistribution(rng, yValues[0], yValues[yValues.length - 1]);
        final UniformRealDistribution distZ = new UniformRealDistribution(rng, zValues[0], zValues[zValues.length - 1]);

        double sumError = 0;
        for (int i = 0; i < numberOfSamples; i++) {
            currentX = distX.sample();
            currentY = distY.sample();
            currentZ = distZ.sample();
            expected = f.value(currentX, currentY, currentZ);

            actual = interpolation.value(currentX, currentY, currentZ);
            final double relativeError = FastMath.abs(actual - expected) / FastMath.max(FastMath.abs(actual), FastMath.abs(expected));
            sumError += relativeError;

            if (print) {
                System.out.println(currentX + " " + currentY + " " + currentZ
                                   + " " + actual
                                   + " " + expected
                                   + " " + (expected - actual));
            }

            Assert.assertEquals(0, relativeError, maxRelativeTolerance);
        }

        final double meanError = sumError / numberOfSamples;
        Assert.assertEquals(0, meanError, meanRelativeTolerance);
    }

    /**
     * Test for a plane.
     * <p>
     *  f(x, y, z) = 2 x - 3 y - 4 z + 5
     * </p>
     */
    @Test
    public void testPlane() {
        final TrivariateFunction f = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return 2 * x - 3 * y - 4 * z + 5;
                }
            };

        final TrivariateFunction dfdx = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return 2;
                }
            };

        final TrivariateFunction dfdy = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return -3;
                }
            };

        final TrivariateFunction dfdz = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return -4;
                }
            };

        final TrivariateFunction zero = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return 0;
                }
            };

        testInterpolation(-10, 3,
                          4.5, 6,
                          -150, -117,
                          7,
                          1000,
                          f,
                          dfdx,
                          dfdy,
                          dfdz,
                          zero,
                          zero,
                          zero,
                          zero,
                          1e-12,
                          1e-11,
                          1e-10,
                          false);
    }

    /**
     * Test for a quadric.
     * <p>
     *  f(x, y, z) = 2 x<sup>2</sup> - 3 y<sup>2</sup> - 4 z<sup>2</sup> + 5 x y + 6 x z - 2 y z + 3
     * </p>
     */
    @Test
    public void testQuadric() {
        final TrivariateFunction f = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return 2 * x * x - 3 * y * y - 4 * z * z + 5 * x * y + 6 * x * z - 2 * y * z + 3;
                }
            };

        final TrivariateFunction dfdx = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return 4 * x + 5 * y + 6 * z;
                }
            };

        final TrivariateFunction dfdy = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return -6 * y + 5 * x - 2 * z;
                }
            };

        final TrivariateFunction dfdz = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return -8 * z + 6 * x - 2 * y;
                }
            };

        final TrivariateFunction d2fdxdy = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return 5;
                }
            };

        final TrivariateFunction d2fdxdz = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return 6;
                }
            };

        final TrivariateFunction d2fdydz = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return -2;
                }
            };

        final TrivariateFunction d3fdxdydz = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return 0;
                }
            };

        testInterpolation(-10, 3,
                          4.5, 6,
                          -150, -117,
                          7,
                          5000,
                          f,
                          dfdx,
                          dfdy,
                          dfdz,
                          d2fdxdy,
                          d2fdxdz,
                          d2fdydz,
                          d3fdxdydz,
                          1e-12,
                          1e-11,
                          1e-8,
                          false);
    }

    /**
     * Wave.
     * <p>
     *  f(x, y, z) = a cos (&omega; z - k<sub>x</sub> x - k<sub>y</sub> y)
     * </p>
     * with a = 5, &omega; = 0.3, k<sub>x</sub> = 0.8, k<sub>y</sub> = 1.
     */
    @Test
    public void testWave() {
        final double a = 5;
        final double omega = 0.3;
        final double kx = 0.8;
        final double ky = 1;

        final TrivariateFunction arg = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return omega * z - kx * x - ky * y;
                }
            };

        final TrivariateFunction f = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return a * FastMath.cos(arg.value(x, y, z));
                }
            };

        final TrivariateFunction dfdx = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return kx * a * FastMath.sin(arg.value(x, y, z));
                }
            };

        final TrivariateFunction dfdy = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return ky * a * FastMath.sin(arg.value(x, y, z));
                }
            };

        final TrivariateFunction dfdz = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return -omega * a * FastMath.sin(arg.value(x, y, z));
                }
            };

        final TrivariateFunction d2fdxdy = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return -ky * kx * a * FastMath.cos(arg.value(x, y, z));
                }
            };

        final TrivariateFunction d2fdxdz = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return omega * kx * a * FastMath.cos(arg.value(x, y, z));
                }
            };

        final TrivariateFunction d2fdydz = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return omega * ky * a * FastMath.cos(arg.value(x, y, z));
                }
            };

        final TrivariateFunction d3fdxdydz = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return omega * ky * kx * a * FastMath.sin(arg.value(x, y, z));
                }
            };

        testInterpolation(-10, 3,
                          4.5, 6,
                          -150, -117,
                          30,
                          5000,
                          f,
                          dfdx,
                          dfdy,
                          dfdz,
                          d2fdxdy,
                          d2fdxdz,
                          d2fdydz,
                          d3fdxdydz,
                          1e-3,
                          1e-2,
                          1e-12,
                          false);
    }
}
