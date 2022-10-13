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

package org.apache.commons.math4.legacy.analysis;

import org.apache.commons.math4.legacy.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.legacy.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math4.legacy.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math4.legacy.analysis.function.Add;
import org.apache.commons.math4.legacy.analysis.function.Constant;
import org.apache.commons.math4.legacy.analysis.function.Cos;
import org.apache.commons.math4.legacy.analysis.function.Cosh;
import org.apache.commons.math4.legacy.analysis.function.Divide;
import org.apache.commons.math4.legacy.analysis.function.Identity;
import org.apache.commons.math4.legacy.analysis.function.Inverse;
import org.apache.commons.math4.legacy.analysis.function.Log;
import org.apache.commons.math4.legacy.analysis.function.Max;
import org.apache.commons.math4.legacy.analysis.function.Min;
import org.apache.commons.math4.legacy.analysis.function.Minus;
import org.apache.commons.math4.legacy.analysis.function.Multiply;
import org.apache.commons.math4.legacy.analysis.function.Pow;
import org.apache.commons.math4.legacy.analysis.function.Power;
import org.apache.commons.math4.legacy.analysis.function.Sin;
import org.apache.commons.math4.legacy.analysis.function.Sinc;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link FunctionUtils}.
 */
public class FunctionUtilsTest {
    private final double EPS = JdkMath.ulp(1d);

    @Test
    public void testCompose() {
        UnivariateFunction id = new Identity();
        Assert.assertEquals(3, FunctionUtils.compose(id, id, id).value(3), EPS);

        UnivariateFunction c = new Constant(4);
        Assert.assertEquals(4, FunctionUtils.compose(id, c).value(3), EPS);
        Assert.assertEquals(4, FunctionUtils.compose(c, id).value(3), EPS);

        UnivariateFunction m = new Minus();
        Assert.assertEquals(-3, FunctionUtils.compose(m).value(3), EPS);
        Assert.assertEquals(3, FunctionUtils.compose(m, m).value(3), EPS);

        UnivariateFunction inv = new Inverse();
        Assert.assertEquals(-0.25, FunctionUtils.compose(inv, m, c, id).value(3), EPS);

        UnivariateFunction pow = new Power(2);
        Assert.assertEquals(81, FunctionUtils.compose(pow, pow).value(3), EPS);
    }

    @Test
    public void testComposeDifferentiable() {
        UnivariateDifferentiableFunction id = new Identity();
        Assert.assertEquals(1, FunctionUtils.compose(id, id, id).value(new DerivativeStructure(1, 1, 0, 3)).getPartialDerivative(1), EPS);

        UnivariateDifferentiableFunction c = new Constant(4);
        Assert.assertEquals(0, FunctionUtils.compose(id, c).value(new DerivativeStructure(1, 1, 0, 3)).getPartialDerivative(1), EPS);
        Assert.assertEquals(0, FunctionUtils.compose(c, id).value(new DerivativeStructure(1, 1, 0, 3)).getPartialDerivative(1), EPS);

        UnivariateDifferentiableFunction m = new Minus();
        Assert.assertEquals(-1, FunctionUtils.compose(m).value(new DerivativeStructure(1, 1, 0, 3)).getPartialDerivative(1), EPS);
        Assert.assertEquals(1, FunctionUtils.compose(m, m).value(new DerivativeStructure(1, 1, 0, 3)).getPartialDerivative(1), EPS);

        UnivariateDifferentiableFunction inv = new Inverse();
        Assert.assertEquals(0.25, FunctionUtils.compose(inv, m, id).value(new DerivativeStructure(1, 1, 0, 2)).getPartialDerivative(1), EPS);

        UnivariateDifferentiableFunction pow = new Power(2);
        Assert.assertEquals(108, FunctionUtils.compose(pow, pow).value(new DerivativeStructure(1, 1, 0, 3)).getPartialDerivative(1), EPS);

        UnivariateDifferentiableFunction log = new Log();
        double a = 9876.54321;
        Assert.assertEquals(pow.value(new DerivativeStructure(1, 1, 0, a)).getPartialDerivative(1) / pow.value(a),
                            FunctionUtils.compose(log, pow).value(new DerivativeStructure(1, 1, 0, a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testAdd() {
        UnivariateFunction id = new Identity();
        UnivariateFunction c = new Constant(4);
        UnivariateFunction m = new Minus();
        UnivariateFunction inv = new Inverse();

        Assert.assertEquals(4.5, FunctionUtils.add(inv, m, c, id).value(2), EPS);
        Assert.assertEquals(4 + 2, FunctionUtils.add(c, id).value(2), EPS);
        Assert.assertEquals(4 - 2, FunctionUtils.add(c, FunctionUtils.compose(m, id)).value(2), EPS);
    }

    @Test
    public void testAddDifferentiable() {
        UnivariateDifferentiableFunction sin = new Sin();
        UnivariateDifferentiableFunction c = new Constant(4);
        UnivariateDifferentiableFunction m = new Minus();
        UnivariateDifferentiableFunction inv = new Inverse();

        final double a = 123.456;
        Assert.assertEquals(- 1 / (a * a) -1 + JdkMath.cos(a),
                            FunctionUtils.add(inv, m, c, sin).value(new DerivativeStructure(1, 1, 0, a)).getPartialDerivative(1),
                            EPS);
    }

    @Test
    public void testMultiply() {
        UnivariateFunction c = new Constant(4);
        Assert.assertEquals(16, FunctionUtils.multiply(c, c).value(12345), EPS);

        UnivariateFunction inv = new Inverse();
        UnivariateFunction pow = new Power(2);
        Assert.assertEquals(1, FunctionUtils.multiply(FunctionUtils.compose(inv, pow), pow).value(3.5), EPS);
    }

    @Test
    public void testMultiplyDifferentiable() {
        UnivariateDifferentiableFunction c = new Constant(4);
        UnivariateDifferentiableFunction id = new Identity();
        final double a = 1.2345678;
        Assert.assertEquals(8 * a, FunctionUtils.multiply(c, id, id).value(new DerivativeStructure(1, 1, 0, a)).getPartialDerivative(1), EPS);

        UnivariateDifferentiableFunction inv = new Inverse();
        UnivariateDifferentiableFunction pow = new Power(2.5);
        UnivariateDifferentiableFunction cos = new Cos();
        Assert.assertEquals(1.5 * JdkMath.sqrt(a) * JdkMath.cos(a) - JdkMath.pow(a, 1.5) * JdkMath.sin(a),
                            FunctionUtils.multiply(inv, pow, cos).value(new DerivativeStructure(1, 1, 0, a)).getPartialDerivative(1), EPS);

        UnivariateDifferentiableFunction cosh = new Cosh();
        Assert.assertEquals(1.5 * JdkMath.sqrt(a) * JdkMath.cosh(a) + JdkMath.pow(a, 1.5) * JdkMath.sinh(a),
                            FunctionUtils.multiply(inv, pow, cosh).value(new DerivativeStructure(1, 1, 0, a)).getPartialDerivative(1), 8 * EPS);
    }

    @Test
    public void testCombine() {
        BivariateFunction bi = new Add();
        UnivariateFunction id = new Identity();
        UnivariateFunction m = new Minus();
        UnivariateFunction c = FunctionUtils.combine(bi, id, m);
        Assert.assertEquals(0, c.value(2.3456), EPS);

        bi = new Multiply();
        UnivariateFunction inv = new Inverse();
        c = FunctionUtils.combine(bi, id, inv);
        Assert.assertEquals(1, c.value(2.3456), EPS);
    }

    @Test
    public void testCollector() {
        BivariateFunction bi = new Add();
        MultivariateFunction coll = FunctionUtils.collector(bi, 0);
        Assert.assertEquals(10, coll.value(new double[] {1, 2, 3, 4}), EPS);

        bi = new Multiply();
        coll = FunctionUtils.collector(bi, 1);
        Assert.assertEquals(24, coll.value(new double[] {1, 2, 3, 4}), EPS);

        bi = new Max();
        coll = FunctionUtils.collector(bi, Double.NEGATIVE_INFINITY);
        Assert.assertEquals(10, coll.value(new double[] {1, -2, 7.5, 10, -24, 9.99}), 0);

        bi = new Min();
        coll = FunctionUtils.collector(bi, Double.POSITIVE_INFINITY);
        Assert.assertEquals(-24, coll.value(new double[] {1, -2, 7.5, 10, -24, 9.99}), 0);
    }

    @Test
    public void testSinc() {
        BivariateFunction div = new Divide();
        UnivariateFunction sin = new Sin();
        UnivariateFunction id = new Identity();
        UnivariateFunction sinc1 = FunctionUtils.combine(div, sin, id);
        UnivariateFunction sinc2 = new Sinc();

        for (int i = 0; i < 10; i++) {
            double x = JdkMath.random();
            Assert.assertEquals(sinc1.value(x), sinc2.value(x), EPS);
        }
    }

    @Test
    public void testFixingArguments() {
        UnivariateFunction scaler = FunctionUtils.fix1stArgument(new Multiply(), 10);
        Assert.assertEquals(1.23456, scaler.value(0.123456), EPS);

        UnivariateFunction pow1 = new Power(2);
        UnivariateFunction pow2 = FunctionUtils.fix2ndArgument(new Pow(), 2);

        for (int i = 0; i < 10; i++) {
            double x = JdkMath.random() * 10;
            Assert.assertEquals(pow1.value(x), pow2.value(x), 0);
        }
    }

    @Test
    public void testToDifferentiableUnivariate() {

        final UnivariateFunction f0 = new UnivariateFunction() {
            @Override
            public double value(final double x) {
                return x * x;
            }
        };
        final UnivariateFunction f1 = new UnivariateFunction() {
            @Override
            public double value(final double x) {
                return 2 * x;
            }
        };
        final UnivariateFunction f2 = new UnivariateFunction() {
            @Override
            public double value(final double x) {
                return 2;
            }
        };
        final UnivariateDifferentiableFunction f = FunctionUtils.toDifferentiable(f0, f1, f2);

        for (double t = -1.0; t < 1; t += 0.01) {
            // x = sin(t)
            DerivativeStructure dsT = new DerivativeStructure(1, 2, 0, t);
            DerivativeStructure y = f.value(dsT.sin());
            Assert.assertEquals(JdkMath.sin(t) * JdkMath.sin(t),               f.value(JdkMath.sin(t)),  1.0e-15);
            Assert.assertEquals(JdkMath.sin(t) * JdkMath.sin(t),               y.getValue(),              1.0e-15);
            Assert.assertEquals(2 * JdkMath.cos(t) * JdkMath.sin(t),           y.getPartialDerivative(1), 1.0e-15);
            Assert.assertEquals(2 * (1 - 2 * JdkMath.sin(t) * JdkMath.sin(t)), y.getPartialDerivative(2), 1.0e-15);
        }

        try {
            f.value(new DerivativeStructure(1, 3, 0.0));
            Assert.fail("an exception should have been thrown");
        } catch (NumberIsTooLargeException e) {
            Assert.assertEquals(2, e.getMax());
            Assert.assertEquals(3, e.getArgument());
        }
    }

    @Test
    public void testToDifferentiableMultivariate() {

        final double a = 1.5;
        final double b = 0.5;
        final MultivariateFunction f = new MultivariateFunction() {
            @Override
            public double value(final double[] point) {
                return a * point[0] + b * point[1];
            }
        };
        final MultivariateVectorFunction gradient = new MultivariateVectorFunction() {
            @Override
            public double[] value(final double[] point) {
                return new double[] { a, b };
            }
        };
        final MultivariateDifferentiableFunction mdf = FunctionUtils.toDifferentiable(f, gradient);

        for (double t = -1.0; t < 1; t += 0.01) {
            // x = sin(t), y = cos(t), hence the method really becomes univariate
            DerivativeStructure dsT = new DerivativeStructure(1, 1, 0, t);
            DerivativeStructure y = mdf.value(new DerivativeStructure[] { dsT.sin(), dsT.cos() });
            Assert.assertEquals(a * JdkMath.sin(t) + b * JdkMath.cos(t), y.getValue(),              1.0e-15);
            Assert.assertEquals(a * JdkMath.cos(t) - b * JdkMath.sin(t), y.getPartialDerivative(1), 1.0e-15);
        }

        for (double u = -1.0; u < 1; u += 0.01) {
            DerivativeStructure dsU = new DerivativeStructure(2, 1, 0, u);
            for (double v = -1.0; v < 1; v += 0.01) {
                DerivativeStructure dsV = new DerivativeStructure(2, 1, 1, v);
                DerivativeStructure y = mdf.value(new DerivativeStructure[] { dsU, dsV });
                Assert.assertEquals(a * u + b * v, mdf.value(new double[] { u, v }), 1.0e-15);
                Assert.assertEquals(a * u + b * v, y.getValue(),                     1.0e-15);
                Assert.assertEquals(a,             y.getPartialDerivative(1, 0),     1.0e-15);
                Assert.assertEquals(b,             y.getPartialDerivative(0, 1),     1.0e-15);
            }
        }

        try {
            mdf.value(new DerivativeStructure[] { new DerivativeStructure(1, 3, 0.0), new DerivativeStructure(1, 3, 0.0) });
            Assert.fail("an exception should have been thrown");
        } catch (NumberIsTooLargeException e) {
            Assert.assertEquals(1, e.getMax());
            Assert.assertEquals(3, e.getArgument());
        }
    }

    @Test
    public void testToDifferentiableMultivariateInconsistentGradient() {

        final double a = 1.5;
        final double b = 0.5;
        final MultivariateFunction f = new MultivariateFunction() {
            @Override
            public double value(final double[] point) {
                return a * point[0] + b * point[1];
            }
        };
        final MultivariateVectorFunction gradient = new MultivariateVectorFunction() {
            @Override
            public double[] value(final double[] point) {
                return new double[] { a, b, 0.0 };
            }
        };
        final MultivariateDifferentiableFunction mdf = FunctionUtils.toDifferentiable(f, gradient);

        try {
            DerivativeStructure dsT = new DerivativeStructure(1, 1, 0, 0.0);
            mdf.value(new DerivativeStructure[] { dsT.sin(), dsT.cos() });
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            Assert.assertEquals(2, e.getDimension());
            Assert.assertEquals(3, e.getArgument());
        }
    }

    @Test
    public void testDerivativeUnivariate() {

        final UnivariateDifferentiableFunction f = new UnivariateDifferentiableFunction() {

            @Override
            public double value(double x) {
                return x * x;
            }

            @Override
            public DerivativeStructure value(DerivativeStructure x) {
                return x.multiply(x);
            }
        };

        final UnivariateFunction f0 = FunctionUtils.derivative(f, 0);
        final UnivariateFunction f1 = FunctionUtils.derivative(f, 1);
        final UnivariateFunction f2 = FunctionUtils.derivative(f, 2);

        for (double t = -1.0; t < 1; t += 0.01) {
            Assert.assertEquals(t * t, f0.value(t), 1.0e-15);
            Assert.assertEquals(2 * t, f1.value(t), 1.0e-15);
            Assert.assertEquals(2,     f2.value(t), 1.0e-15);
        }
    }

    @Test
    public void testDerivativeMultivariate() {

        final double a = 1.5;
        final double b = 0.5;
        final double c = 0.25;
        final MultivariateDifferentiableFunction mdf = new MultivariateDifferentiableFunction() {

            @Override
            public double value(double[] point) {
                return a * point[0] * point[0] + b * point[1] * point[1] + c * point[0] * point[1];
            }

            @Override
            public DerivativeStructure value(DerivativeStructure[] point) {
                DerivativeStructure x  = point[0];
                DerivativeStructure y  = point[1];
                DerivativeStructure x2 = x.multiply(x);
                DerivativeStructure y2 = y.multiply(y);
                DerivativeStructure xy = x.multiply(y);
                return x2.multiply(a).add(y2.multiply(b)).add(xy.multiply(c));
            }
        };

        final MultivariateFunction f       = FunctionUtils.derivative(mdf, new int[] { 0, 0 });
        final MultivariateFunction dfdx    = FunctionUtils.derivative(mdf, new int[] { 1, 0 });
        final MultivariateFunction dfdy    = FunctionUtils.derivative(mdf, new int[] { 0, 1 });
        final MultivariateFunction d2fdx2  = FunctionUtils.derivative(mdf, new int[] { 2, 0 });
        final MultivariateFunction d2fdy2  = FunctionUtils.derivative(mdf, new int[] { 0, 2 });
        final MultivariateFunction d2fdxdy = FunctionUtils.derivative(mdf, new int[] { 1, 1 });

        for (double x = -1.0; x < 1; x += 0.01) {
            for (double y = -1.0; y < 1; y += 0.01) {
                Assert.assertEquals(a * x * x + b * y * y + c * x * y, f.value(new double[]       { x, y }), 1.0e-15);
                Assert.assertEquals(2 * a * x + c * y,                 dfdx.value(new double[]    { x, y }), 1.0e-15);
                Assert.assertEquals(2 * b * y + c * x,                 dfdy.value(new double[]    { x, y }), 1.0e-15);
                Assert.assertEquals(2 * a,                             d2fdx2.value(new double[]  { x, y }), 1.0e-15);
                Assert.assertEquals(2 * b,                             d2fdy2.value(new double[]  { x, y }), 1.0e-15);
                Assert.assertEquals(c,                                 d2fdxdy.value(new double[] { x, y }), 1.0e-15);
            }
        }
    }
}
