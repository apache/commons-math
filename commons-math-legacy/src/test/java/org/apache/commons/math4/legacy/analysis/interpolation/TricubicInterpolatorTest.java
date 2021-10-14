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

import org.apache.commons.math4.legacy.analysis.TrivariateFunction;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the {@link TricubicInterpolator tricubic interpolator}.
 */
public final class TricubicInterpolatorTest {
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
        TrivariateFunction tcf = new TricubicInterpolator().interpolate(xval, yval, zval, fval);

        double[] wxval = new double[] {3, 2, 5, 6.5};
        try {
            tcf = new TricubicInterpolator().interpolate(wxval, yval, zval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }
        double[] wyval = new double[] {-4, -1, -1, 2.5};
        try {
            tcf = new TricubicInterpolator().interpolate(xval, wyval, zval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }
        double[] wzval = new double[] {-12, -8, -9, -3, 0, 2.5};
        try {
            tcf = new TricubicInterpolator().interpolate(xval, yval, wzval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
            // Expected
        }
        double[][][] wfval = new double[xval.length - 1][yval.length][zval.length];
        try {
            tcf = new TricubicInterpolator().interpolate(xval, yval, zval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        wfval = new double[xval.length][yval.length - 1][zval.length];
        try {
            tcf = new TricubicInterpolator().interpolate(xval, yval, zval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        wfval = new double[xval.length][yval.length][zval.length - 1];
        try {
            tcf = new TricubicInterpolator().interpolate(xval, yval, zval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
    }

    @Test
    public void testIsValid() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2.5};
        double[] zval = new double[] {-12, -8, -5.5, -3, 0, 2.5};
        double[][][] fval = new double[xval.length][yval.length][zval.length];

        TricubicInterpolatingFunction tcf = new TricubicInterpolator().interpolate(xval, yval, zval, fval);

        // Valid.
        Assert.assertTrue(tcf.isValidPoint(4, -3, -8));
        Assert.assertTrue(tcf.isValidPoint(5, -3, -8));
        Assert.assertTrue(tcf.isValidPoint(4, -1, -8));
        Assert.assertTrue(tcf.isValidPoint(5, -1, -8));
        Assert.assertTrue(tcf.isValidPoint(4, -3, 0));
        Assert.assertTrue(tcf.isValidPoint(5, -3, 0));
        Assert.assertTrue(tcf.isValidPoint(4, -1, 0));
        Assert.assertTrue(tcf.isValidPoint(5, -1, 0));

        // Invalid.
        Assert.assertFalse(tcf.isValidPoint(3.5, -3, -8));
        Assert.assertFalse(tcf.isValidPoint(4.5, -3.1, -8));
        Assert.assertFalse(tcf.isValidPoint(4.5, -2, 0.1));
        Assert.assertFalse(tcf.isValidPoint(4.5, 0, -3.5));
        Assert.assertFalse(tcf.isValidPoint(4.1, -1, -10));
    }

    /**
     * Test for a plane.
     * <p>
     *  f(x, y, z) = 2 x - 3 y - 4 z + 5
     * </p>
     */
    @Test
    public void testPlane() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2, 2.5};
        double[] zval = new double[] {-12, -8, -5.5, -3, 0, 2.5};

        // Function values
        TrivariateFunction f = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return 2 * x - 3 * y - 4 * z + 5;
                }
            };

        double[][][] fval = new double[xval.length][yval.length][zval.length];

        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    fval[i][j][k] = f.value(xval[i], yval[j], zval[k]);
                }
            }
        }

        TrivariateFunction tcf = new TricubicInterpolator().interpolate(xval,
                                                                        yval,
                                                                        zval,
                                                                        fval);
        double x;
        double y;
        double z;
        double expected;
        double result;

        x = 4;
        y = -3;
        z = 0;
        expected = f.value(x, y, z);
        result = tcf.value(x, y, z);
        Assert.assertEquals("On sample point",
                            expected, result, 1e-15);

        x = 4.5;
        y = -1.5;
        z = -4.25;
        expected = f.value(x, y, z);
        result = tcf.value(x, y, z);
        Assert.assertEquals("Half-way between sample points (middle of the patch)",
                            expected, result, 1e-14);
    }

    /**
     * Sine wave.
     * <p>
     *  f(x, y, z) = a cos [&omega; z - k<sub>y</sub> x - k<sub>y</sub> y]
     * </p>
     * with A = 0.2, &omega; = 0.5, k<sub>x</sub> = 2, k<sub>y</sub> = 1.
     */
    @Test
    public void testWave() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2, 2.5};
        double[] zval = new double[] {-12, -8, -5.5, -3, 0, 4};

        final double a = 0.2;
        final double omega = 0.5;
        final double kx = 2;
        final double ky = 1;

        // Function values
        TrivariateFunction f = new TrivariateFunction() {
                @Override
                public double value(double x, double y, double z) {
                    return a * JdkMath.cos(omega * z - kx * x - ky * y);
                }
            };

        double[][][] fval = new double[xval.length][yval.length][zval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    fval[i][j][k] = f.value(xval[i], yval[j], zval[k]);
                }
            }
        }

        TrivariateFunction tcf = new TricubicInterpolator().interpolate(xval,
                                                                        yval,
                                                                        zval,
                                                                        fval);

        double x;
        double y;
        double z;
        double expected;
        double result;

        x = 4;
        y = -3;
        z = 0;
        expected = f.value(x, y, z);
        result = tcf.value(x, y, z);
        Assert.assertEquals("On sample point",
                            expected, result, 1e-14);

        x = 4.5;
        y = -1.5;
        z = -4.25;
        expected = f.value(x, y, z);
        result = tcf.value(x, y, z);
        Assert.assertEquals("Half-way between sample points (middle of the patch)",
                            expected, result, 1e-1); // XXX Too high tolerance!
    }
}
