/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.math.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link Precision} class.
 *
 * @version $Id$
 */
public class PrecisionTest {
    @Test
    public void testEqualsIncludingNaN() {
        double[] testArray = {
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            1d,
            0d };
        for (int i = 0; i < testArray.length; i++) {
            for (int j = 0; j < testArray.length; j++) {
                if (i == j) {
                    Assert.assertTrue(Precision.equalsIncludingNaN(testArray[i], testArray[j]));
                    Assert.assertTrue(Precision.equalsIncludingNaN(testArray[j], testArray[i]));
                } else {
                    Assert.assertTrue(!Precision.equalsIncludingNaN(testArray[i], testArray[j]));
                    Assert.assertTrue(!Precision.equalsIncludingNaN(testArray[j], testArray[i]));
                }
            }
        }
    }

    @Test
    public void testEqualsWithAllowedDelta() {
        Assert.assertTrue(Precision.equals(153.0000, 153.0000, .0625));
        Assert.assertTrue(Precision.equals(153.0000, 153.0625, .0625));
        Assert.assertTrue(Precision.equals(152.9375, 153.0000, .0625));
        Assert.assertFalse(Precision.equals(153.0000, 153.0625, .0624));
        Assert.assertFalse(Precision.equals(152.9374, 153.0000, .0625));
        Assert.assertFalse(Precision.equals(Double.NaN, Double.NaN, 1.0));
        Assert.assertTrue(Precision.equals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        Assert.assertTrue(Precision.equals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1.0));
        Assert.assertFalse(Precision.equals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
    }

    @Test
    public void testMath475() {
        final double a = 1.7976931348623182E16;
        final double b = FastMath.nextUp(a);

        double diff = FastMath.abs(a - b);
        // Because they are adjacent floating point numbers, "a" and "b" are
        // considered equal even though the allowed error is smaller than
        // their difference.
        Assert.assertTrue(Precision.equals(a, b, 0.5 * diff));

        final double c = FastMath.nextUp(b);
        diff = FastMath.abs(a - c);
        // Because "a" and "c" are not adjacent, the tolerance is taken into
        // account for assessing equality.
        Assert.assertTrue(Precision.equals(a, c, diff));
        Assert.assertFalse(Precision.equals(a, c, (1 - 1e-16) * diff));
    }

    @Test
    public void testEqualsIncludingNaNWithAllowedDelta() {
        Assert.assertTrue(Precision.equalsIncludingNaN(153.0000, 153.0000, .0625));
        Assert.assertTrue(Precision.equalsIncludingNaN(153.0000, 153.0625, .0625));
        Assert.assertTrue(Precision.equalsIncludingNaN(152.9375, 153.0000, .0625));
        Assert.assertTrue(Precision.equalsIncludingNaN(Double.NaN, Double.NaN, 1.0));
        Assert.assertTrue(Precision.equalsIncludingNaN(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        Assert.assertTrue(Precision.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1.0));
        Assert.assertFalse(Precision.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        Assert.assertFalse(Precision.equalsIncludingNaN(153.0000, 153.0625, .0624));
        Assert.assertFalse(Precision.equalsIncludingNaN(152.9374, 153.0000, .0625));
    }

    // Tests for floating point equality
    @Test
    public void testFloatEqualsWithAllowedUlps() {
        Assert.assertTrue("+0.0f == -0.0f",Precision.equals(0.0f, -0.0f));
        Assert.assertTrue("+0.0f == -0.0f (1 ulp)",Precision.equals(0.0f, -0.0f, 1));
        float oneFloat = 1.0f;
        Assert.assertTrue("1.0f == 1.0f + 1 ulp",Precision.equals(oneFloat, Float.intBitsToFloat(1 + Float.floatToIntBits(oneFloat))));
        Assert.assertTrue("1.0f == 1.0f + 1 ulp (1 ulp)",Precision.equals(oneFloat, Float.intBitsToFloat(1 + Float.floatToIntBits(oneFloat)), 1));
        Assert.assertFalse("1.0f != 1.0f + 2 ulp (1 ulp)",Precision.equals(oneFloat, Float.intBitsToFloat(2 + Float.floatToIntBits(oneFloat)), 1));

        Assert.assertTrue(Precision.equals(153.0f, 153.0f, 1));

        // These tests need adjusting for floating point precision
//        Assert.assertTrue(Precision.equals(153.0f, 153.00000000000003f, 1));
//        Assert.assertFalse(Precision.equals(153.0f, 153.00000000000006f, 1));
//        Assert.assertTrue(Precision.equals(153.0f, 152.99999999999997f, 1));
//        Assert.assertFalse(Precision.equals(153f, 152.99999999999994f, 1));
//
//        Assert.assertTrue(Precision.equals(-128.0f, -127.99999999999999f, 1));
//        Assert.assertFalse(Precision.equals(-128.0f, -127.99999999999997f, 1));
//        Assert.assertTrue(Precision.equals(-128.0f, -128.00000000000003f, 1));
//        Assert.assertFalse(Precision.equals(-128.0f, -128.00000000000006f, 1));

        Assert.assertTrue(Precision.equals(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, 1));
        Assert.assertTrue(Precision.equals(Double.MAX_VALUE, Float.POSITIVE_INFINITY, 1));

        Assert.assertTrue(Precision.equals(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, 1));
        Assert.assertTrue(Precision.equals(-Float.MAX_VALUE, Float.NEGATIVE_INFINITY, 1));

        Assert.assertFalse(Precision.equals(Float.NaN, Float.NaN, 1));
        Assert.assertFalse(Precision.equals(Float.NaN, Float.NaN, 0));
        Assert.assertFalse(Precision.equals(Float.NaN, 0, 0));
        Assert.assertFalse(Precision.equals(Float.NaN, Float.POSITIVE_INFINITY, 0));
        Assert.assertFalse(Precision.equals(Float.NaN, Float.NEGATIVE_INFINITY, 0));

        Assert.assertFalse(Precision.equals(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 100000));
    }

    @Test
    public void testEqualsWithAllowedUlps() {
        Assert.assertTrue(Precision.equals(0.0, -0.0, 1));

        Assert.assertTrue(Precision.equals(1.0, 1 + FastMath.ulp(1d), 1));
        Assert.assertFalse(Precision.equals(1.0, 1 + 2 * FastMath.ulp(1d), 1));

        final double nUp1 = FastMath.nextAfter(1d, Double.POSITIVE_INFINITY);
        final double nnUp1 = FastMath.nextAfter(nUp1, Double.POSITIVE_INFINITY);
        Assert.assertTrue(Precision.equals(1.0, nUp1, 1));
        Assert.assertTrue(Precision.equals(nUp1, nnUp1, 1));
        Assert.assertFalse(Precision.equals(1.0, nnUp1, 1));

        Assert.assertTrue(Precision.equals(0.0, FastMath.ulp(0d), 1));
        Assert.assertTrue(Precision.equals(0.0, -FastMath.ulp(0d), 1));

        Assert.assertTrue(Precision.equals(153.0, 153.0, 1));

        Assert.assertTrue(Precision.equals(153.0, 153.00000000000003, 1));
        Assert.assertFalse(Precision.equals(153.0, 153.00000000000006, 1));
        Assert.assertTrue(Precision.equals(153.0, 152.99999999999997, 1));
        Assert.assertFalse(Precision.equals(153, 152.99999999999994, 1));

        Assert.assertTrue(Precision.equals(-128.0, -127.99999999999999, 1));
        Assert.assertFalse(Precision.equals(-128.0, -127.99999999999997, 1));
        Assert.assertTrue(Precision.equals(-128.0, -128.00000000000003, 1));
        Assert.assertFalse(Precision.equals(-128.0, -128.00000000000006, 1));

        Assert.assertTrue(Precision.equals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        Assert.assertTrue(Precision.equals(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));

        Assert.assertTrue(Precision.equals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        Assert.assertTrue(Precision.equals(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, 1));

        Assert.assertFalse(Precision.equals(Double.NaN, Double.NaN, 1));
        Assert.assertFalse(Precision.equals(Double.NaN, Double.NaN, 0));
        Assert.assertFalse(Precision.equals(Double.NaN, 0, 0));
        Assert.assertFalse(Precision.equals(Double.NaN, Double.POSITIVE_INFINITY, 0));
        Assert.assertFalse(Precision.equals(Double.NaN, Double.NEGATIVE_INFINITY, 0));

        Assert.assertFalse(Precision.equals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 100000));
    }

    @Test
    public void testEqualsIncludingNaNWithAllowedUlps() {
        Assert.assertTrue(Precision.equalsIncludingNaN(0.0, -0.0, 1));

        Assert.assertTrue(Precision.equalsIncludingNaN(1.0, 1 + FastMath.ulp(1d), 1));
        Assert.assertFalse(Precision.equalsIncludingNaN(1.0, 1 + 2 * FastMath.ulp(1d), 1));

        final double nUp1 = FastMath.nextAfter(1d, Double.POSITIVE_INFINITY);
        final double nnUp1 = FastMath.nextAfter(nUp1, Double.POSITIVE_INFINITY);
        Assert.assertTrue(Precision.equalsIncludingNaN(1.0, nUp1, 1));
        Assert.assertTrue(Precision.equalsIncludingNaN(nUp1, nnUp1, 1));
        Assert.assertFalse(Precision.equalsIncludingNaN(1.0, nnUp1, 1));

        Assert.assertTrue(Precision.equalsIncludingNaN(0.0, FastMath.ulp(0d), 1));
        Assert.assertTrue(Precision.equalsIncludingNaN(0.0, -FastMath.ulp(0d), 1));

        Assert.assertTrue(Precision.equalsIncludingNaN(153.0, 153.0, 1));

        Assert.assertTrue(Precision.equalsIncludingNaN(153.0, 153.00000000000003, 1));
        Assert.assertFalse(Precision.equalsIncludingNaN(153.0, 153.00000000000006, 1));
        Assert.assertTrue(Precision.equalsIncludingNaN(153.0, 152.99999999999997, 1));
        Assert.assertFalse(Precision.equalsIncludingNaN(153, 152.99999999999994, 1));

        Assert.assertTrue(Precision.equalsIncludingNaN(-128.0, -127.99999999999999, 1));
        Assert.assertFalse(Precision.equalsIncludingNaN(-128.0, -127.99999999999997, 1));
        Assert.assertTrue(Precision.equalsIncludingNaN(-128.0, -128.00000000000003, 1));
        Assert.assertFalse(Precision.equalsIncludingNaN(-128.0, -128.00000000000006, 1));

        Assert.assertTrue(Precision.equalsIncludingNaN(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        Assert.assertTrue(Precision.equalsIncludingNaN(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));

        Assert.assertTrue(Precision.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        Assert.assertTrue(Precision.equalsIncludingNaN(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, 1));

        Assert.assertTrue(Precision.equalsIncludingNaN(Double.NaN, Double.NaN, 1));

        Assert.assertFalse(Precision.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 100000));
    }

    @Test
    public void testCompareToEpsilon() {
        Assert.assertEquals(0, Precision.compareTo(152.33, 152.32, .011));
        Assert.assertTrue(Precision.compareTo(152.308, 152.32, .011) < 0);
        Assert.assertTrue(Precision.compareTo(152.33, 152.318, .011) > 0);
        Assert.assertEquals(0, Precision.compareTo(Double.MIN_VALUE, +0.0, Double.MIN_VALUE));
        Assert.assertEquals(0, Precision.compareTo(Double.MIN_VALUE, -0.0, Double.MIN_VALUE));
    }

    @Test
    public void testCompareToMaxUlps() {
        double a     = 152.32;
        double delta = FastMath.ulp(a);
        for (int i = 0; i <= 10; ++i) {
            if (i <= 5) {
                Assert.assertEquals( 0, Precision.compareTo(a, a + i * delta, 5));
                Assert.assertEquals( 0, Precision.compareTo(a, a - i * delta, 5));
            } else {
                Assert.assertEquals(-1, Precision.compareTo(a, a + i * delta, 5));
                Assert.assertEquals(+1, Precision.compareTo(a, a - i * delta, 5));
            }
        }

        Assert.assertEquals( 0, Precision.compareTo(-0.0, 0.0, 0));

        Assert.assertEquals(-1, Precision.compareTo(-Double.MIN_VALUE, -0.0, 0));
        Assert.assertEquals( 0, Precision.compareTo(-Double.MIN_VALUE, -0.0, 1));
        Assert.assertEquals(-1, Precision.compareTo(-Double.MIN_VALUE, +0.0, 0));
        Assert.assertEquals( 0, Precision.compareTo(-Double.MIN_VALUE, +0.0, 1));

        Assert.assertEquals(+1, Precision.compareTo( Double.MIN_VALUE, -0.0, 0));
        Assert.assertEquals( 0, Precision.compareTo( Double.MIN_VALUE, -0.0, 1));
        Assert.assertEquals(+1, Precision.compareTo( Double.MIN_VALUE, +0.0, 0));
        Assert.assertEquals( 0, Precision.compareTo( Double.MIN_VALUE, +0.0, 1));

        Assert.assertEquals(-1, Precision.compareTo(-Double.MIN_VALUE, Double.MIN_VALUE, 0));
        Assert.assertEquals(-1, Precision.compareTo(-Double.MIN_VALUE, Double.MIN_VALUE, 1));
        Assert.assertEquals( 0, Precision.compareTo(-Double.MIN_VALUE, Double.MIN_VALUE, 2));

        Assert.assertEquals( 0, Precision.compareTo(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));
        Assert.assertEquals(-1, Precision.compareTo(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 0));

        Assert.assertEquals(+1, Precision.compareTo(Double.MAX_VALUE, Double.NaN, Integer.MAX_VALUE));
        Assert.assertEquals(+1, Precision.compareTo(Double.NaN, Double.MAX_VALUE, Integer.MAX_VALUE));

    }
}
