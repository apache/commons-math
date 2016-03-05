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

package org.apache.commons.math4.analysis.function;

import org.apache.commons.math4.analysis.FunctionUtils;
import org.apache.commons.math4.analysis.UnivariateFunction;
import org.apache.commons.math4.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math4.analysis.function.Logit;
import org.apache.commons.math4.analysis.function.Sigmoid;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well1024a;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link Logit}.
 */
public class LogitTest {
    private final double EPS = Math.ulp(1d);

    @Test(expected=OutOfRangeException.class)
    public void testPreconditions1() {
        final double lo = -1;
        final double hi = 2;
        final UnivariateFunction f = new Logit(lo, hi);

        f.value(lo - 1);
    }

    @Test(expected=OutOfRangeException.class)
    public void testPreconditions2() {
        final double lo = -1;
        final double hi = 2;
        final UnivariateFunction f = new Logit(lo, hi);

        f.value(hi + 1);
    }

    @Test
    public void testSomeValues() {
        final double lo = 1;
        final double hi = 2;
        final UnivariateFunction f = new Logit(lo, hi);

        Assert.assertEquals(Double.NEGATIVE_INFINITY, f.value(1), EPS);
        Assert.assertEquals(Double.POSITIVE_INFINITY, f.value(2), EPS);
        Assert.assertEquals(0, f.value(1.5), EPS);
    }

    @Test
    public void testDerivative() {
        final double lo = 1;
        final double hi = 2;
        final Logit f = new Logit(lo, hi);
        final DerivativeStructure f15 = f.value(new DerivativeStructure(1, 1, 0, 1.5));

        Assert.assertEquals(4, f15.getPartialDerivative(1), EPS);
    }

    @Test
    public void testDerivativeLargeArguments() {
        final Logit f = new Logit(1, 2);

        for (double arg : new double[] {
            Double.NEGATIVE_INFINITY, -Double.MAX_VALUE, -1e155, 1e155, Double.MAX_VALUE, Double.POSITIVE_INFINITY
            }) {
            try {
                f.value(new DerivativeStructure(1, 1, 0, arg));
                Assert.fail("an exception should have been thrown");
            } catch (OutOfRangeException ore) {
                // expected
            } catch (Exception e) {
                Assert.fail("wrong exception caught: " + e.getMessage());
            }
        }
    }

    @Test
    public void testDerivativesHighOrder() {
        DerivativeStructure l = new Logit(1, 3).value(new DerivativeStructure(1, 5, 0, 1.2));
        Assert.assertEquals(-2.1972245773362193828, l.getPartialDerivative(0), 1.0e-16);
        Assert.assertEquals(5.5555555555555555555,  l.getPartialDerivative(1), 9.0e-16);
        Assert.assertEquals(-24.691358024691358025, l.getPartialDerivative(2), 2.0e-14);
        Assert.assertEquals(250.34293552812071331,  l.getPartialDerivative(3), 2.0e-13);
        Assert.assertEquals(-3749.4284407864654778, l.getPartialDerivative(4), 4.0e-12);
        Assert.assertEquals(75001.270131585632282,  l.getPartialDerivative(5), 8.0e-11);
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage1() {
        final Logit.Parametric g = new Logit.Parametric();
        g.value(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage2() {
        final Logit.Parametric g = new Logit.Parametric();
        g.value(0, new double[] {0});
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage3() {
        final Logit.Parametric g = new Logit.Parametric();
        g.gradient(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage4() {
        final Logit.Parametric g = new Logit.Parametric();
        g.gradient(0, new double[] {0});
    }

    @Test(expected=OutOfRangeException.class)
    public void testParametricUsage5() {
        final Logit.Parametric g = new Logit.Parametric();
        g.value(-1, new double[] {0, 1});
    }

    @Test(expected=OutOfRangeException.class)
    public void testParametricUsage6() {
        final Logit.Parametric g = new Logit.Parametric();
        g.value(2, new double[] {0, 1});
    }

    @Test
    public void testParametricValue() {
        final double lo = 2;
        final double hi = 3;
        final Logit f = new Logit(lo, hi);

        final Logit.Parametric g = new Logit.Parametric();
        Assert.assertEquals(f.value(2), g.value(2, new double[] {lo, hi}), 0);
        Assert.assertEquals(f.value(2.34567), g.value(2.34567, new double[] {lo, hi}), 0);
        Assert.assertEquals(f.value(3), g.value(3, new double[] {lo, hi}), 0);
    }

    @Test
    public void testValueWithInverseFunction() {
        final double lo = 2;
        final double hi = 3;
        final Logit f = new Logit(lo, hi);
        final Sigmoid g = new Sigmoid(lo, hi);
        RandomGenerator random = new Well1024a(0x49914cdd9f0b8db5l);
        final UnivariateDifferentiableFunction id = FunctionUtils.compose((UnivariateDifferentiableFunction) g,
                                                                (UnivariateDifferentiableFunction) f);

        for (int i = 0; i < 10; i++) {
            final double x = lo + random.nextDouble() * (hi - lo);
            Assert.assertEquals(x, id.value(new DerivativeStructure(1, 1, 0, x)).getValue(), EPS);
        }

        Assert.assertEquals(lo, id.value(new DerivativeStructure(1, 1, 0, lo)).getValue(), EPS);
        Assert.assertEquals(hi, id.value(new DerivativeStructure(1, 1, 0, hi)).getValue(), EPS);
    }

    @Test
    public void testDerivativesWithInverseFunction() {
        double[] epsilon = new double[] { 1.0e-20, 4.0e-16, 3.0e-15, 2.0e-11, 3.0e-9, 1.0e-6 };
        final double lo = 2;
        final double hi = 3;
        final Logit f = new Logit(lo, hi);
        final Sigmoid g = new Sigmoid(lo, hi);
        RandomGenerator random = new Well1024a(0x96885e9c1f81cea5l);
        final UnivariateDifferentiableFunction id =
                FunctionUtils.compose((UnivariateDifferentiableFunction) g, (UnivariateDifferentiableFunction) f);
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            double max = 0;
            for (int i = 0; i < 10; i++) {
                final double x = lo + random.nextDouble() * (hi - lo);
                final DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                max = FastMath.max(max, FastMath.abs(dsX.getPartialDerivative(maxOrder) -
                                                     id.value(dsX).getPartialDerivative(maxOrder)));
                Assert.assertEquals(dsX.getPartialDerivative(maxOrder),
                                    id.value(dsX).getPartialDerivative(maxOrder),
                                    epsilon[maxOrder]);
            }

            // each function evaluates correctly near boundaries,
            // but combination leads to NaN as some intermediate point is infinite
            final DerivativeStructure dsLo = new DerivativeStructure(1, maxOrder, 0, lo);
            if (maxOrder == 0) {
                Assert.assertTrue(Double.isInfinite(f.value(dsLo).getPartialDerivative(maxOrder)));
                Assert.assertEquals(lo, id.value(dsLo).getPartialDerivative(maxOrder), epsilon[maxOrder]);
            } else if (maxOrder == 1) {
                Assert.assertTrue(Double.isInfinite(f.value(dsLo).getPartialDerivative(maxOrder)));
                Assert.assertTrue(Double.isNaN(id.value(dsLo).getPartialDerivative(maxOrder)));
            } else {
                Assert.assertTrue(Double.isNaN(f.value(dsLo).getPartialDerivative(maxOrder)));
                Assert.assertTrue(Double.isNaN(id.value(dsLo).getPartialDerivative(maxOrder)));
            }

            final DerivativeStructure dsHi = new DerivativeStructure(1, maxOrder, 0, hi);
            if (maxOrder == 0) {
                Assert.assertTrue(Double.isInfinite(f.value(dsHi).getPartialDerivative(maxOrder)));
                Assert.assertEquals(hi, id.value(dsHi).getPartialDerivative(maxOrder), epsilon[maxOrder]);
            } else if (maxOrder == 1) {
                Assert.assertTrue(Double.isInfinite(f.value(dsHi).getPartialDerivative(maxOrder)));
                Assert.assertTrue(Double.isNaN(id.value(dsHi).getPartialDerivative(maxOrder)));
            } else {
                Assert.assertTrue(Double.isNaN(f.value(dsHi).getPartialDerivative(maxOrder)));
                Assert.assertTrue(Double.isNaN(id.value(dsHi).getPartialDerivative(maxOrder)));
            }

        }
    }
}
