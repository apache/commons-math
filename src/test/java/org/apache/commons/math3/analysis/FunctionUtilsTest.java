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

package org.apache.commons.math3.analysis;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Add;
import org.apache.commons.math3.analysis.function.Constant;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Cosh;
import org.apache.commons.math3.analysis.function.Divide;
import org.apache.commons.math3.analysis.function.Identity;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.function.Max;
import org.apache.commons.math3.analysis.function.Min;
import org.apache.commons.math3.analysis.function.Minus;
import org.apache.commons.math3.analysis.function.Multiply;
import org.apache.commons.math3.analysis.function.Pow;
import org.apache.commons.math3.analysis.function.Power;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.Sinc;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link FunctionUtils}.
 */
public class FunctionUtilsTest {
    private final double EPS = FastMath.ulp(1d);

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
        Assert.assertEquals(- 1 / (a * a) -1 + FastMath.cos(a),
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
        Assert.assertEquals(1.5 * FastMath.sqrt(a) * FastMath.cos(a) - FastMath.pow(a, 1.5) * FastMath.sin(a),
                            FunctionUtils.multiply(inv, pow, cos).value(new DerivativeStructure(1, 1, 0, a)).getPartialDerivative(1), EPS);

        UnivariateDifferentiableFunction cosh = new Cosh();
        Assert.assertEquals(1.5 * FastMath.sqrt(a) * FastMath.cosh(a) + FastMath.pow(a, 1.5) * FastMath.sinh(a),
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
            double x = FastMath.random();
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
            double x = FastMath.random() * 10;
            Assert.assertEquals(pow1.value(x), pow2.value(x), 0);
        }
    }

    @Test(expected = NumberIsTooLargeException.class)
    public void testSampleWrongBounds(){
        FunctionUtils.sample(new Sin(), FastMath.PI, 0.0, 10);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testSampleNegativeNumberOfPoints(){
        FunctionUtils.sample(new Sin(), 0.0, FastMath.PI, -1);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testSampleNullNumberOfPoints(){
        FunctionUtils.sample(new Sin(), 0.0, FastMath.PI, 0);
    }

    @Test
    public void testSample() {
        final int n = 11;
        final double min = 0.0;
        final double max = FastMath.PI;
        final double[] actual = FunctionUtils.sample(new Sin(), min, max, n);
        for (int i = 0; i < n; i++) {
            final double x = min + (max - min) / n * i;
            Assert.assertEquals("x = " + x, FastMath.sin(x), actual[i], 0.0);
        }
    }

    @Test
    @Deprecated
    public void testToDifferentiableUnivariateFunction() {

        // Sin implements both UnivariateDifferentiableFunction and DifferentiableUnivariateFunction
        Sin sin = new Sin();
        DifferentiableUnivariateFunction converted = FunctionUtils.toDifferentiableUnivariateFunction(sin);
        for (double x = 0.1; x < 0.5; x += 0.01) {
            Assert.assertEquals(sin.value(x), converted.value(x), 1.0e-10);
            Assert.assertEquals(sin.derivative().value(x), converted.derivative().value(x), 1.0e-10);
        }

    }

    @Test
    @Deprecated
    public void testToUnivariateDifferential() {

        // Sin implements both UnivariateDifferentiableFunction and DifferentiableUnivariateFunction
        Sin sin = new Sin();
        UnivariateDifferentiableFunction converted = FunctionUtils.toUnivariateDifferential(sin);
        for (double x = 0.1; x < 0.5; x += 0.01) {
            DerivativeStructure t = new DerivativeStructure(2, 1, x, 1.0, 2.0);
            Assert.assertEquals(sin.value(t).getValue(), converted.value(t).getValue(), 1.0e-10);
            Assert.assertEquals(sin.value(t).getPartialDerivative(1, 0),
                                converted.value(t).getPartialDerivative(1, 0),
                                1.0e-10);
            Assert.assertEquals(sin.value(t).getPartialDerivative(0, 1),
                                converted.value(t).getPartialDerivative(0, 1),
                                1.0e-10);
        }

    }

    @Test
    @Deprecated
    public void testToDifferentiableMultivariateFunction() {

        MultivariateDifferentiableFunction hypot = new MultivariateDifferentiableFunction() {

            public double value(double[] point) {
                return FastMath.hypot(point[0], point[1]);
            }

            public DerivativeStructure value(DerivativeStructure[] point) {
                return DerivativeStructure.hypot(point[0], point[1]);
            }
        };

        DifferentiableMultivariateFunction converted = FunctionUtils.toDifferentiableMultivariateFunction(hypot);
        for (double x = 0.1; x < 0.5; x += 0.01) {
            for (double y = 0.1; y < 0.5; y += 0.01) {
                double[] point = new double[] { x, y };
                Assert.assertEquals(hypot.value(point), converted.value(point), 1.0e-10);
                Assert.assertEquals(x / hypot.value(point), converted.gradient().value(point)[0], 1.0e-10);
                Assert.assertEquals(y / hypot.value(point), converted.gradient().value(point)[1], 1.0e-10);
            }
        }

    }

    @Test
    @Deprecated
    public void testToMultivariateDifferentiableFunction() {

        DifferentiableMultivariateFunction hypot = new DifferentiableMultivariateFunction() {

            public double value(double[] point) {
                return FastMath.hypot(point[0], point[1]);
            }

            public MultivariateFunction partialDerivative(final int k) {
                return new MultivariateFunction() {
                    public double value(double[] point) {
                        return point[k] / FastMath.hypot(point[0], point[1]);
                    }
                };
            }

            public MultivariateVectorFunction gradient() {
                return new MultivariateVectorFunction() {
                    public double[] value(double[] point) {
                        final double h = FastMath.hypot(point[0], point[1]);
                        return new double[] { point[0] / h, point[1] / h };
                    }
                };
            }

        };

        MultivariateDifferentiableFunction converted = FunctionUtils.toMultivariateDifferentiableFunction(hypot);
        for (double x = 0.1; x < 0.5; x += 0.01) {
            for (double y = 0.1; y < 0.5; y += 0.01) {
                DerivativeStructure[] t = new DerivativeStructure[] {
                    new DerivativeStructure(3, 1, x, 1.0, 2.0, 3.0 ),
                    new DerivativeStructure(3, 1, y, 4.0, 5.0, 6.0 )
                };
                DerivativeStructure h = DerivativeStructure.hypot(t[0], t[1]);
                Assert.assertEquals(h.getValue(), converted.value(t).getValue(), 1.0e-10);
                Assert.assertEquals(h.getPartialDerivative(1, 0, 0),
                                    converted.value(t).getPartialDerivative(1, 0, 0),
                                    1.0e-10);
                Assert.assertEquals(h.getPartialDerivative(0, 1, 0),
                                    converted.value(t).getPartialDerivative(0, 1, 0),
                                    1.0e-10);
                Assert.assertEquals(h.getPartialDerivative(0, 0, 1),
                                    converted.value(t).getPartialDerivative(0, 0, 1),
                                    1.0e-10);
            }
        }
    }

}
