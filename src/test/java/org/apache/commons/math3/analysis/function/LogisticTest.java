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

package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link Logistic}.
 */
public class LogisticTest {
    private final double EPS = Math.ulp(1d);

    @Test(expected=NotStrictlyPositiveException.class)
    public void testPreconditions1() {
        new Logistic(1, 0, 1, 1, 0, -1);
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testPreconditions2() {
        new Logistic(1, 0, 1, 1, 0, 0);
    }

    @Test
    public void testCompareSigmoid() {
        final UnivariateFunction sig = new Sigmoid();
        final UnivariateFunction sigL = new Logistic(1, 0, 1, 1, 0, 1);

        final double min = -2;
        final double max = 2;
        final int n = 100;
        final double delta = (max - min) / n;
        for (int i = 0; i < n; i++) {
            final double x = min + i * delta;
            Assert.assertEquals("x=" + x, sig.value(x), sigL.value(x), EPS);
        }
    }

    @Test
    public void testSomeValues() {
        final double k = 4;
        final double m = 5;
        final double b = 2;
        final double q = 3;
        final double a = -1;
        final double n = 2;

        final UnivariateFunction f = new Logistic(k, m, b, q, a, n);

        double x;
        x = m;
        Assert.assertEquals("x=" + x, a + (k - a) / FastMath.sqrt(1 + q), f.value(x), EPS);

        x = Double.NEGATIVE_INFINITY;
        Assert.assertEquals("x=" + x, a, f.value(x), EPS);

        x = Double.POSITIVE_INFINITY;
        Assert.assertEquals("x=" + x, k, f.value(x), EPS);
    }

    @Test
    public void testCompareDerivativeSigmoid() {
        final double k = 3;
        final double a = 2;

        final Logistic f = new Logistic(k, 0, 1, 1, a, 1);
        final Sigmoid g = new Sigmoid(a, k);

        final double min = -10;
        final double max = 10;
        final double n = 20;
        final double delta = (max - min) / n;
        for (int i = 0; i < n; i++) {
            final DerivativeStructure x = new DerivativeStructure(1, 5, 0, min + i * delta);
            for (int order = 0; order <= x.getOrder(); ++order) {
                Assert.assertEquals("x=" + x.getValue(),
                                    g.value(x).getPartialDerivative(order),
                                    f.value(x).getPartialDerivative(order),
                                    3.0e-15);
            }
        }
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage1() {
        final Logistic.Parametric g = new Logistic.Parametric();
        g.value(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage2() {
        final Logistic.Parametric g = new Logistic.Parametric();
        g.value(0, new double[] {0});
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage3() {
        final Logistic.Parametric g = new Logistic.Parametric();
        g.gradient(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage4() {
        final Logistic.Parametric g = new Logistic.Parametric();
        g.gradient(0, new double[] {0});
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testParametricUsage5() {
        final Logistic.Parametric g = new Logistic.Parametric();
        g.value(0, new double[] {1, 0, 1, 1, 0 ,0});
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testParametricUsage6() {
        final Logistic.Parametric g = new Logistic.Parametric();
        g.gradient(0, new double[] {1, 0, 1, 1, 0 ,0});
    }

    @Test
    public void testGradientComponent0Component4() {
        final double k = 3;
        final double a = 2;

        final Logistic.Parametric f = new Logistic.Parametric();
        // Compare using the "Sigmoid" function.
        final Sigmoid.Parametric g = new Sigmoid.Parametric();

        final double x = 0.12345;
        final double[] gf = f.gradient(x, new double[] {k, 0, 1, 1, a, 1});
        final double[] gg = g.gradient(x, new double[] {a, k});

        Assert.assertEquals(gg[0], gf[4], EPS);
        Assert.assertEquals(gg[1], gf[0], EPS);
    }

    @Test
    public void testGradientComponent5() {
        final double m = 1.2;
        final double k = 3.4;
        final double a = 2.3;
        final double q = 0.567;
        final double b = -FastMath.log(q);
        final double n = 3.4;

        final Logistic.Parametric f = new Logistic.Parametric();

        final double x = m - 1;
        final double qExp1 = 2;

        final double[] gf = f.gradient(x, new double[] {k, m, b, q, a, n});

        Assert.assertEquals((k - a) * FastMath.log(qExp1) / (n * n * FastMath.pow(qExp1, 1 / n)),
                            gf[5], EPS);
    }

    @Test
    public void testGradientComponent1Component2Component3() {
        final double m = 1.2;
        final double k = 3.4;
        final double a = 2.3;
        final double b = 0.567;
        final double q = 1 / FastMath.exp(b * m);
        final double n = 3.4;

        final Logistic.Parametric f = new Logistic.Parametric();

        final double x = 0;
        final double qExp1 = 2;

        final double[] gf = f.gradient(x, new double[] {k, m, b, q, a, n});

        final double factor = (a - k) / (n * FastMath.pow(qExp1, 1 / n + 1));
        Assert.assertEquals(factor * b, gf[1], EPS);
        Assert.assertEquals(factor * m, gf[2], EPS);
        Assert.assertEquals(factor / q, gf[3], EPS);
    }
}
