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
package org.apache.commons.math4.legacy.ml.distance;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link CanberraDistance} class.
 */
public class CanberraDistanceTest {
    private final DistanceMeasure distance = new CanberraDistance();

    @Test
    public void testZero() {
        final double[] a = { 0, 1, -2, 3.4, 5, -6.7, 89 };
        Assert.assertEquals(0, distance.compute(a, a), 0d);
    }

    @Test
    public void testZero2() {
        final double[] a = { 0, 0 };
        Assert.assertEquals(0, distance.compute(a, a), 0d);
    }

    @Test
    public void test() {
        final double[] a = { 1, 2, 3, 4, 9 };
        final double[] b = { -5, -6, 7, 4, 3 };
        final double expected = 2.9;
        Assert.assertEquals(expected, distance.compute(a, b), 0d);
        Assert.assertEquals(expected, distance.compute(b, a), 0d);
    }

    @Test
    public void testExtremeOppositeSigns() {
        // Both |MAX - (-MAX)| and |MAX| + |-MAX| overflow to infinity: the true ratio is 1.
        final double max = Double.MAX_VALUE;
        Assert.assertEquals(1, distance.compute(new double[] {max}, new double[] {-max}), 0d);
        Assert.assertEquals(1, distance.compute(new double[] {-max}, new double[] {max}), 0d);
        Assert.assertEquals(1, distance.compute(new double[] {max}, new double[] {-max / 2}), 0d);
        Assert.assertEquals(1, distance.compute(new double[] {-max / 2}, new double[] {max}), 0d);
    }

    @Test
    public void testExtremeSameSign() {
        // |MAX - MAX/2| / (|MAX| + |MAX/2|) == 1/3; only the denominator overflows.
        final double max = Double.MAX_VALUE;
        // 1/3 is not representable, so allow one ulp instead of an exact match.
        final double expected = 1.0 / 3.0;
        final double tol = Math.ulp(expected);
        Assert.assertEquals(expected, distance.compute(new double[] {max}, new double[] {max / 2}), tol);
        Assert.assertEquals(expected, distance.compute(new double[] {max / 2}, new double[] {max}), tol);
        Assert.assertEquals(expected, distance.compute(new double[] {-max}, new double[] {-max / 2}), tol);
        Assert.assertEquals(expected, distance.compute(new double[] {-max / 2}, new double[] {-max}), tol);
        // Identical extreme values have distance 0.
        Assert.assertEquals(0, distance.compute(new double[] {max}, new double[] {max}), 0d);
        Assert.assertEquals(0, distance.compute(new double[] {-max}, new double[] {-max}), 0d);
    }

    @Test
    public void testExtremeVector() {
        final double max = Double.MAX_VALUE;
        final double[] a = { max, max / 2, max };
        final double[] b = { -max, max, max };
        // 1 + 1/3 + 0
        Assert.assertEquals(4.0 / 3.0, distance.compute(a, b), 1e-15);
        Assert.assertEquals(4.0 / 3.0, distance.compute(b, a), 1e-15);
    }

    @Test
    public void testExtremeFiniteValuesAgainstBigDecimalReference() {
        final double max = Double.MAX_VALUE;
        final double[] a = {max, max, 0.75 * max, max, -max, max};
        final double[] b = {max / 2, Math.nextDown(max), Math.nextDown(0.75 * max), -max, -max / 2, max / 16};
        for (int i = 0; i < a.length; i++) {
            final double expected = referenceCanberra(a[i], b[i]);
            assertWithinOneUlp(expected, distance.compute(new double[] {a[i]}, new double[] {b[i]}));
            assertWithinOneUlp(expected, distance.compute(new double[] {b[i]}, new double[] {a[i]}));
        }
    }

    /**
     * Computes a high-precision reference from the exact decimal representations of the input doubles.
     */
    private static double referenceCanberra(double a, double b) {
        final BigDecimal bdA = new BigDecimal(a);
        final BigDecimal bdB = new BigDecimal(b);
        final BigDecimal num = bdA.subtract(bdB).abs();
        final BigDecimal den = bdA.abs().add(bdB.abs());
        return num.divide(den, MathContext.DECIMAL128).doubleValue();
    }

    private static void assertWithinOneUlp(double expected, double actual) {
        Assert.assertEquals("expected " + expected + " but was " + actual,
                            expected, actual, Math.ulp(expected));
    }

    @Test
    public void testSpecialValuesPreserved() {
        final double min = Double.MIN_VALUE;
        // Subnormal ratios are unaffected by the extreme-value handling.
        Assert.assertEquals(1.0 / 3.0, distance.compute(new double[] {min}, new double[] {2 * min}), 0d);
        Assert.assertEquals(1, distance.compute(new double[] {min}, new double[] {-min}), 0d);
        // Signed zero: the zero/zero convention contributes 0.
        Assert.assertEquals(0, distance.compute(new double[] {0.0}, new double[] {-0.0}), 0d);
        // Infinite and NaN inputs keep their historical NaN result.
        Assert.assertTrue(Double.isNaN(distance.compute(new double[] {Double.POSITIVE_INFINITY}, new double[] {1})));
        Assert.assertTrue(Double.isNaN(distance.compute(new double[] {Double.NaN}, new double[] {0})));
    }
}
