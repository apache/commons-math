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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.math.TestUtils;
import org.apache.commons.math.exception.NonMonotonousSequenceException;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.NotFiniteNumberException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.random.Well1024a;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the MathUtils class.
 * @version $Id$
 *          2007) $
 */
public final class MathUtilsTest {

    /** cached binomial coefficients */
    private static final List<Map<Integer, Long>> binomialCache = new ArrayList<Map<Integer, Long>>();

    /**
     * Exact (caching) recursive implementation to test against
     */
    private long binomialCoefficient(int n, int k) throws MathArithmeticException {
        if (binomialCache.size() > n) {
            Long cachedResult = binomialCache.get(n).get(Integer.valueOf(k));
            if (cachedResult != null) {
                return cachedResult.longValue();
            }
        }
        long result = -1;
        if ((n == k) || (k == 0)) {
            result = 1;
        } else if ((k == 1) || (k == n - 1)) {
            result = n;
        } else {
            // Reduce stack depth for larger values of n
            if (k < n - 100) {
                binomialCoefficient(n - 100, k);
            }
            if (k > 100) {
                binomialCoefficient(n - 100, k - 100);
            }
            result = MathUtils.addAndCheck(binomialCoefficient(n - 1, k - 1),
                binomialCoefficient(n - 1, k));
        }
        if (result == -1) {
            throw new MathArithmeticException();
        }
        for (int i = binomialCache.size(); i < n + 1; i++) {
            binomialCache.add(new HashMap<Integer, Long>());
        }
        binomialCache.get(n).put(Integer.valueOf(k), Long.valueOf(result));
        return result;
    }

    /**
     * Exact direct multiplication implementation to test against
     */
    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /** Verify that b(0,0) = 1 */
    @Test
    public void test0Choose0() {
        Assert.assertEquals(MathUtils.binomialCoefficientDouble(0, 0), 1d, 0);
        Assert.assertEquals(MathUtils.binomialCoefficientLog(0, 0), 0d, 0);
        Assert.assertEquals(MathUtils.binomialCoefficient(0, 0), 1);
    }

    @Test
    public void testAddAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, MathUtils.addAndCheck(big, 0));
        try {
            MathUtils.addAndCheck(big, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            MathUtils.addAndCheck(bigNeg, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }

    @Test
    public void testAddAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, MathUtils.addAndCheck(max, 0L));
        Assert.assertEquals(min, MathUtils.addAndCheck(min, 0L));
        Assert.assertEquals(max, MathUtils.addAndCheck(0L, max));
        Assert.assertEquals(min, MathUtils.addAndCheck(0L, min));
        Assert.assertEquals(1, MathUtils.addAndCheck(-1L, 2L));
        Assert.assertEquals(1, MathUtils.addAndCheck(2L, -1L));
        Assert.assertEquals(-3, MathUtils.addAndCheck(-2L, -1L));
        Assert.assertEquals(min, MathUtils.addAndCheck(min + 1, -1L));
        testAddAndCheckLongFailure(max, 1L);
        testAddAndCheckLongFailure(min, -1L);
        testAddAndCheckLongFailure(1L, max);
        testAddAndCheckLongFailure(-1L, min);
    }

    private void testAddAndCheckLongFailure(long a, long b) {
        try {
            MathUtils.addAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }
    }

    @Test
    public void testBinomialCoefficient() {
        long[] bcoef5 = {
            1,
            5,
            10,
            10,
            5,
            1 };
        long[] bcoef6 = {
            1,
            6,
            15,
            20,
            15,
            6,
            1 };
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals("5 choose " + i, bcoef5[i], MathUtils.binomialCoefficient(5, i));
        }
        for (int i = 0; i < 7; i++) {
            Assert.assertEquals("6 choose " + i, bcoef6[i], MathUtils.binomialCoefficient(6, i));
        }

        for (int n = 1; n < 10; n++) {
            for (int k = 0; k <= n; k++) {
                Assert.assertEquals(n + " choose " + k, binomialCoefficient(n, k), MathUtils.binomialCoefficient(n, k));
                Assert.assertEquals(n + " choose " + k, binomialCoefficient(n, k), MathUtils.binomialCoefficientDouble(n, k), Double.MIN_VALUE);
                Assert.assertEquals(n + " choose " + k, FastMath.log(binomialCoefficient(n, k)), MathUtils.binomialCoefficientLog(n, k), 10E-12);
            }
        }

        int[] n = { 34, 66, 100, 1500, 1500 };
        int[] k = { 17, 33, 10, 1500 - 4, 4 };
        for (int i = 0; i < n.length; i++) {
            long expected = binomialCoefficient(n[i], k[i]);
            Assert.assertEquals(n[i] + " choose " + k[i], expected,
                MathUtils.binomialCoefficient(n[i], k[i]));
            Assert.assertEquals(n[i] + " choose " + k[i], expected,
                MathUtils.binomialCoefficientDouble(n[i], k[i]), 0.0);
            Assert.assertEquals("log(" + n[i] + " choose " + k[i] + ")", FastMath.log(expected),
                MathUtils.binomialCoefficientLog(n[i], k[i]), 0.0);
        }
    }

    /**
     * Tests correctness for large n and sharpness of upper bound in API doc
     * JIRA: MATH-241
     */
    @Test
    public void testBinomialCoefficientLarge() throws Exception {
        // This tests all legal and illegal values for n <= 200.
        for (int n = 0; n <= 200; n++) {
            for (int k = 0; k <= n; k++) {
                long ourResult = -1;
                long exactResult = -1;
                boolean shouldThrow = false;
                boolean didThrow = false;
                try {
                    ourResult = MathUtils.binomialCoefficient(n, k);
                } catch (MathArithmeticException ex) {
                    didThrow = true;
                }
                try {
                    exactResult = binomialCoefficient(n, k);
                } catch (MathArithmeticException ex) {
                    shouldThrow = true;
                }
                Assert.assertEquals(n + " choose " + k, exactResult, ourResult);
                Assert.assertEquals(n + " choose " + k, shouldThrow, didThrow);
                Assert.assertTrue(n + " choose " + k, (n > 66 || !didThrow));

                if (!shouldThrow && exactResult > 1) {
                    Assert.assertEquals(n + " choose " + k, 1.,
                        MathUtils.binomialCoefficientDouble(n, k) / exactResult, 1e-10);
                    Assert.assertEquals(n + " choose " + k, 1,
                        MathUtils.binomialCoefficientLog(n, k) / FastMath.log(exactResult), 1e-10);
                }
            }
        }

        long ourResult = MathUtils.binomialCoefficient(300, 3);
        long exactResult = binomialCoefficient(300, 3);
        Assert.assertEquals(exactResult, ourResult);

        ourResult = MathUtils.binomialCoefficient(700, 697);
        exactResult = binomialCoefficient(700, 697);
        Assert.assertEquals(exactResult, ourResult);

        // This one should throw
        try {
            MathUtils.binomialCoefficient(700, 300);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // Expected
        }

        int n = 10000;
        ourResult = MathUtils.binomialCoefficient(n, 3);
        exactResult = binomialCoefficient(n, 3);
        Assert.assertEquals(exactResult, ourResult);
        Assert.assertEquals(1, MathUtils.binomialCoefficientDouble(n, 3) / exactResult, 1e-10);
        Assert.assertEquals(1, MathUtils.binomialCoefficientLog(n, 3) / FastMath.log(exactResult), 1e-10);

    }

    @Test
    public void testBinomialCoefficientFail() {
        try {
            MathUtils.binomialCoefficient(4, 5);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

        try {
            MathUtils.binomialCoefficientDouble(4, 5);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

        try {
            MathUtils.binomialCoefficientLog(4, 5);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

        try {
            MathUtils.binomialCoefficient(-1, -2);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            MathUtils.binomialCoefficientDouble(-1, -2);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            MathUtils.binomialCoefficientLog(-1, -2);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

        try {
            MathUtils.binomialCoefficient(67, 30);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // ignored
        }
        try {
            MathUtils.binomialCoefficient(67, 34);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // ignored
        }
        double x = MathUtils.binomialCoefficientDouble(1030, 515);
        Assert.assertTrue("expecting infinite binomial coefficient", Double
            .isInfinite(x));
    }

    @Test
    public void testCompareToEpsilon() {
        Assert.assertEquals(0, MathUtils.compareTo(152.33, 152.32, .011));
        Assert.assertTrue(MathUtils.compareTo(152.308, 152.32, .011) < 0);
        Assert.assertTrue(MathUtils.compareTo(152.33, 152.318, .011) > 0);
        Assert.assertEquals(0, MathUtils.compareTo(Double.MIN_VALUE, +0.0, Double.MIN_VALUE));
        Assert.assertEquals(0, MathUtils.compareTo(Double.MIN_VALUE, -0.0, Double.MIN_VALUE));
    }

    @Test
    public void testCompareToMaxUlps() {
        double a     = 152.32;
        double delta = FastMath.ulp(a);
        for (int i = 0; i <= 10; ++i) {
            if (i <= 5) {
                Assert.assertEquals( 0, MathUtils.compareTo(a, a + i * delta, 5));
                Assert.assertEquals( 0, MathUtils.compareTo(a, a - i * delta, 5));
            } else {
                Assert.assertEquals(-1, MathUtils.compareTo(a, a + i * delta, 5));
                Assert.assertEquals(+1, MathUtils.compareTo(a, a - i * delta, 5));
            }
        }

        Assert.assertEquals( 0, MathUtils.compareTo(-0.0, 0.0, 0));

        Assert.assertEquals(-1, MathUtils.compareTo(-Double.MIN_VALUE, -0.0, 0));
        Assert.assertEquals( 0, MathUtils.compareTo(-Double.MIN_VALUE, -0.0, 1));
        Assert.assertEquals(-1, MathUtils.compareTo(-Double.MIN_VALUE, +0.0, 0));
        Assert.assertEquals( 0, MathUtils.compareTo(-Double.MIN_VALUE, +0.0, 1));

        Assert.assertEquals(+1, MathUtils.compareTo( Double.MIN_VALUE, -0.0, 0));
        Assert.assertEquals( 0, MathUtils.compareTo( Double.MIN_VALUE, -0.0, 1));
        Assert.assertEquals(+1, MathUtils.compareTo( Double.MIN_VALUE, +0.0, 0));
        Assert.assertEquals( 0, MathUtils.compareTo( Double.MIN_VALUE, +0.0, 1));

        Assert.assertEquals(-1, MathUtils.compareTo(-Double.MIN_VALUE, Double.MIN_VALUE, 0));
        Assert.assertEquals(-1, MathUtils.compareTo(-Double.MIN_VALUE, Double.MIN_VALUE, 1));
        Assert.assertEquals( 0, MathUtils.compareTo(-Double.MIN_VALUE, Double.MIN_VALUE, 2));

        Assert.assertEquals( 0, MathUtils.compareTo(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));
        Assert.assertEquals(-1, MathUtils.compareTo(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 0));

        Assert.assertEquals(+1, MathUtils.compareTo(Double.MAX_VALUE, Double.NaN, Integer.MAX_VALUE));
        Assert.assertEquals(+1, MathUtils.compareTo(Double.NaN, Double.MAX_VALUE, Integer.MAX_VALUE));

    }

    @Test
    public void testCosh() {
        double x = 3.0;
        double expected = 10.06766;
        Assert.assertEquals(expected, MathUtils.cosh(x), 1.0e-5);
    }

    @Test
    public void testCoshNaN() {
        Assert.assertTrue(Double.isNaN(MathUtils.cosh(Double.NaN)));
    }

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
                    Assert.assertTrue(MathUtils.equalsIncludingNaN(testArray[i], testArray[j]));
                    Assert.assertTrue(MathUtils.equalsIncludingNaN(testArray[j], testArray[i]));
                } else {
                    Assert.assertTrue(!MathUtils.equalsIncludingNaN(testArray[i], testArray[j]));
                    Assert.assertTrue(!MathUtils.equalsIncludingNaN(testArray[j], testArray[i]));
                }
            }
        }
    }

    @Test
    public void testEqualsWithAllowedDelta() {
        Assert.assertTrue(MathUtils.equals(153.0000, 153.0000, .0625));
        Assert.assertTrue(MathUtils.equals(153.0000, 153.0625, .0625));
        Assert.assertTrue(MathUtils.equals(152.9375, 153.0000, .0625));
        Assert.assertFalse(MathUtils.equals(153.0000, 153.0625, .0624));
        Assert.assertFalse(MathUtils.equals(152.9374, 153.0000, .0625));
        Assert.assertFalse(MathUtils.equals(Double.NaN, Double.NaN, 1.0));
        Assert.assertTrue(MathUtils.equals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        Assert.assertTrue(MathUtils.equals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1.0));
        Assert.assertFalse(MathUtils.equals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
    }

    @Test
    public void testMath475() {
        final double a = 1.7976931348623182E16;
        final double b = FastMath.nextUp(a);

        double diff = FastMath.abs(a - b);
        // Because they are adjacent floating point numbers, "a" and "b" are
        // considered equal even though the allowed error is smaller than
        // their difference.
        Assert.assertTrue(MathUtils.equals(a, b, 0.5 * diff));

        final double c = FastMath.nextUp(b);
        diff = FastMath.abs(a - c);
        // Because "a" and "c" are not adjacent, the tolerance is taken into
        // account for assessing equality.
        Assert.assertTrue(MathUtils.equals(a, c, diff));
        Assert.assertFalse(MathUtils.equals(a, c, (1 - 1e-16) * diff));
    }

    @Test
    public void testEqualsIncludingNaNWithAllowedDelta() {
        Assert.assertTrue(MathUtils.equalsIncludingNaN(153.0000, 153.0000, .0625));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(153.0000, 153.0625, .0625));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(152.9375, 153.0000, .0625));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(Double.NaN, Double.NaN, 1.0));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1.0));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(153.0000, 153.0625, .0624));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(152.9374, 153.0000, .0625));
    }

    // Tests for floating point equality
    @Test
    public void testFloatEqualsWithAllowedUlps() {
        Assert.assertTrue("+0.0f == -0.0f",MathUtils.equals(0.0f, -0.0f));
        Assert.assertTrue("+0.0f == -0.0f (1 ulp)",MathUtils.equals(0.0f, -0.0f, 1));
        float oneFloat = 1.0f;
        Assert.assertTrue("1.0f == 1.0f + 1 ulp",MathUtils.equals(oneFloat, Float.intBitsToFloat(1 + Float.floatToIntBits(oneFloat))));
        Assert.assertTrue("1.0f == 1.0f + 1 ulp (1 ulp)",MathUtils.equals(oneFloat, Float.intBitsToFloat(1 + Float.floatToIntBits(oneFloat)), 1));
        Assert.assertFalse("1.0f != 1.0f + 2 ulp (1 ulp)",MathUtils.equals(oneFloat, Float.intBitsToFloat(2 + Float.floatToIntBits(oneFloat)), 1));

        Assert.assertTrue(MathUtils.equals(153.0f, 153.0f, 1));

        // These tests need adjusting for floating point precision
//        Assert.assertTrue(MathUtils.equals(153.0f, 153.00000000000003f, 1));
//        Assert.assertFalse(MathUtils.equals(153.0f, 153.00000000000006f, 1));
//        Assert.assertTrue(MathUtils.equals(153.0f, 152.99999999999997f, 1));
//        Assert.assertFalse(MathUtils.equals(153f, 152.99999999999994f, 1));
//
//        Assert.assertTrue(MathUtils.equals(-128.0f, -127.99999999999999f, 1));
//        Assert.assertFalse(MathUtils.equals(-128.0f, -127.99999999999997f, 1));
//        Assert.assertTrue(MathUtils.equals(-128.0f, -128.00000000000003f, 1));
//        Assert.assertFalse(MathUtils.equals(-128.0f, -128.00000000000006f, 1));

        Assert.assertTrue(MathUtils.equals(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, 1));
        Assert.assertTrue(MathUtils.equals(Double.MAX_VALUE, Float.POSITIVE_INFINITY, 1));

        Assert.assertTrue(MathUtils.equals(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, 1));
        Assert.assertTrue(MathUtils.equals(-Float.MAX_VALUE, Float.NEGATIVE_INFINITY, 1));

        Assert.assertFalse(MathUtils.equals(Float.NaN, Float.NaN, 1));
        Assert.assertFalse(MathUtils.equals(Float.NaN, Float.NaN, 0));
        Assert.assertFalse(MathUtils.equals(Float.NaN, 0, 0));
        Assert.assertFalse(MathUtils.equals(Float.NaN, Float.POSITIVE_INFINITY, 0));
        Assert.assertFalse(MathUtils.equals(Float.NaN, Float.NEGATIVE_INFINITY, 0));

        Assert.assertFalse(MathUtils.equals(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 100000));
    }

    @Test
    public void testEqualsWithAllowedUlps() {
        Assert.assertTrue(MathUtils.equals(0.0, -0.0, 1));

        Assert.assertTrue(MathUtils.equals(1.0, 1 + FastMath.ulp(1d), 1));
        Assert.assertFalse(MathUtils.equals(1.0, 1 + 2 * FastMath.ulp(1d), 1));

        final double nUp1 = FastMath.nextAfter(1d, Double.POSITIVE_INFINITY);
        final double nnUp1 = FastMath.nextAfter(nUp1, Double.POSITIVE_INFINITY);
        Assert.assertTrue(MathUtils.equals(1.0, nUp1, 1));
        Assert.assertTrue(MathUtils.equals(nUp1, nnUp1, 1));
        Assert.assertFalse(MathUtils.equals(1.0, nnUp1, 1));

        Assert.assertTrue(MathUtils.equals(0.0, FastMath.ulp(0d), 1));
        Assert.assertTrue(MathUtils.equals(0.0, -FastMath.ulp(0d), 1));

        Assert.assertTrue(MathUtils.equals(153.0, 153.0, 1));

        Assert.assertTrue(MathUtils.equals(153.0, 153.00000000000003, 1));
        Assert.assertFalse(MathUtils.equals(153.0, 153.00000000000006, 1));
        Assert.assertTrue(MathUtils.equals(153.0, 152.99999999999997, 1));
        Assert.assertFalse(MathUtils.equals(153, 152.99999999999994, 1));

        Assert.assertTrue(MathUtils.equals(-128.0, -127.99999999999999, 1));
        Assert.assertFalse(MathUtils.equals(-128.0, -127.99999999999997, 1));
        Assert.assertTrue(MathUtils.equals(-128.0, -128.00000000000003, 1));
        Assert.assertFalse(MathUtils.equals(-128.0, -128.00000000000006, 1));

        Assert.assertTrue(MathUtils.equals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        Assert.assertTrue(MathUtils.equals(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));

        Assert.assertTrue(MathUtils.equals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        Assert.assertTrue(MathUtils.equals(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, 1));

        Assert.assertFalse(MathUtils.equals(Double.NaN, Double.NaN, 1));
        Assert.assertFalse(MathUtils.equals(Double.NaN, Double.NaN, 0));
        Assert.assertFalse(MathUtils.equals(Double.NaN, 0, 0));
        Assert.assertFalse(MathUtils.equals(Double.NaN, Double.POSITIVE_INFINITY, 0));
        Assert.assertFalse(MathUtils.equals(Double.NaN, Double.NEGATIVE_INFINITY, 0));

        Assert.assertFalse(MathUtils.equals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 100000));
    }

    @Test
    public void testEqualsIncludingNaNWithAllowedUlps() {
        Assert.assertTrue(MathUtils.equalsIncludingNaN(0.0, -0.0, 1));

        Assert.assertTrue(MathUtils.equalsIncludingNaN(1.0, 1 + FastMath.ulp(1d), 1));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(1.0, 1 + 2 * FastMath.ulp(1d), 1));

        final double nUp1 = FastMath.nextAfter(1d, Double.POSITIVE_INFINITY);
        final double nnUp1 = FastMath.nextAfter(nUp1, Double.POSITIVE_INFINITY);
        Assert.assertTrue(MathUtils.equalsIncludingNaN(1.0, nUp1, 1));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(nUp1, nnUp1, 1));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(1.0, nnUp1, 1));

        Assert.assertTrue(MathUtils.equalsIncludingNaN(0.0, FastMath.ulp(0d), 1));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(0.0, -FastMath.ulp(0d), 1));

        Assert.assertTrue(MathUtils.equalsIncludingNaN(153.0, 153.0, 1));

        Assert.assertTrue(MathUtils.equalsIncludingNaN(153.0, 153.00000000000003, 1));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(153.0, 153.00000000000006, 1));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(153.0, 152.99999999999997, 1));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(153, 152.99999999999994, 1));

        Assert.assertTrue(MathUtils.equalsIncludingNaN(-128.0, -127.99999999999999, 1));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(-128.0, -127.99999999999997, 1));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(-128.0, -128.00000000000003, 1));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(-128.0, -128.00000000000006, 1));

        Assert.assertTrue(MathUtils.equalsIncludingNaN(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));

        Assert.assertTrue(MathUtils.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, 1));

        Assert.assertTrue(MathUtils.equalsIncludingNaN(Double.NaN, Double.NaN, 1));

        Assert.assertFalse(MathUtils.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 100000));
    }

    @Test
    public void testArrayEquals() {
        Assert.assertFalse(MathUtils.equals(new double[] { 1d }, null));
        Assert.assertFalse(MathUtils.equals(null, new double[] { 1d }));
        Assert.assertTrue(MathUtils.equals((double[]) null, (double[]) null));

        Assert.assertFalse(MathUtils.equals(new double[] { 1d }, new double[0]));
        Assert.assertTrue(MathUtils.equals(new double[] { 1d }, new double[] { 1d }));
        Assert.assertTrue(MathUtils.equals(new double[] {
                                      Double.POSITIVE_INFINITY,
                                      Double.NEGATIVE_INFINITY, 1d, 0d
                                    }, new double[] {
                                      Double.POSITIVE_INFINITY,
                                      Double.NEGATIVE_INFINITY, 1d, 0d
                                    }));
        Assert.assertFalse(MathUtils.equals(new double[] { Double.NaN },
                                     new double[] { Double.NaN }));
        Assert.assertFalse(MathUtils.equals(new double[] { Double.POSITIVE_INFINITY },
                                     new double[] { Double.NEGATIVE_INFINITY }));
        Assert.assertFalse(MathUtils.equals(new double[] { 1d },
                                     new double[] { FastMath.nextAfter(FastMath.nextAfter(1d, 2d), 2d) }));

    }

    @Test
    public void testArrayEqualsIncludingNaN() {
        Assert.assertFalse(MathUtils.equalsIncludingNaN(new double[] { 1d }, null));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(null, new double[] { 1d }));
        Assert.assertTrue(MathUtils.equalsIncludingNaN((double[]) null, (double[]) null));

        Assert.assertFalse(MathUtils.equalsIncludingNaN(new double[] { 1d }, new double[0]));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(new double[] { 1d }, new double[] { 1d }));
        Assert.assertTrue(MathUtils.equalsIncludingNaN(new double[] {
                    Double.NaN, Double.POSITIVE_INFINITY,
                    Double.NEGATIVE_INFINITY, 1d, 0d
                }, new double[] {
                    Double.NaN, Double.POSITIVE_INFINITY,
                    Double.NEGATIVE_INFINITY, 1d, 0d
                }));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(new double[] { Double.POSITIVE_INFINITY },
                                                 new double[] { Double.NEGATIVE_INFINITY }));
        Assert.assertFalse(MathUtils.equalsIncludingNaN(new double[] { 1d },
                                                 new double[] { FastMath.nextAfter(FastMath.nextAfter(1d, 2d), 2d) }));
    }

    @Test
    public void testFactorial() {
        for (int i = 1; i < 21; i++) {
            Assert.assertEquals(i + "! ", factorial(i), MathUtils.factorial(i));
            Assert.assertEquals(i + "! ", factorial(i), MathUtils.factorialDouble(i), Double.MIN_VALUE);
            Assert.assertEquals(i + "! ", FastMath.log(factorial(i)), MathUtils.factorialLog(i), 10E-12);
        }

        Assert.assertEquals("0", 1, MathUtils.factorial(0));
        Assert.assertEquals("0", 1.0d, MathUtils.factorialDouble(0), 1E-14);
        Assert.assertEquals("0", 0.0d, MathUtils.factorialLog(0), 1E-14);
    }

    @Test
    public void testFactorialFail() {
        try {
            MathUtils.factorial(-1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            MathUtils.factorialDouble(-1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            MathUtils.factorialLog(-1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            MathUtils.factorial(21);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // ignored
        }
        Assert.assertTrue("expecting infinite factorial value", Double.isInfinite(MathUtils.factorialDouble(171)));
    }

    @Test
    public void testGcd() {
        int a = 30;
        int b = 50;
        int c = 77;

        Assert.assertEquals(0, MathUtils.gcd(0, 0));

        Assert.assertEquals(b, MathUtils.gcd(0, b));
        Assert.assertEquals(a, MathUtils.gcd(a, 0));
        Assert.assertEquals(b, MathUtils.gcd(0, -b));
        Assert.assertEquals(a, MathUtils.gcd(-a, 0));

        Assert.assertEquals(10, MathUtils.gcd(a, b));
        Assert.assertEquals(10, MathUtils.gcd(-a, b));
        Assert.assertEquals(10, MathUtils.gcd(a, -b));
        Assert.assertEquals(10, MathUtils.gcd(-a, -b));

        Assert.assertEquals(1, MathUtils.gcd(a, c));
        Assert.assertEquals(1, MathUtils.gcd(-a, c));
        Assert.assertEquals(1, MathUtils.gcd(a, -c));
        Assert.assertEquals(1, MathUtils.gcd(-a, -c));

        Assert.assertEquals(3 * (1<<15), MathUtils.gcd(3 * (1<<20), 9 * (1<<15)));

        Assert.assertEquals(Integer.MAX_VALUE, MathUtils.gcd(Integer.MAX_VALUE, 0));
        Assert.assertEquals(Integer.MAX_VALUE, MathUtils.gcd(-Integer.MAX_VALUE, 0));
        Assert.assertEquals(1<<30, MathUtils.gcd(1<<30, -Integer.MIN_VALUE));
        try {
            // gcd(Integer.MIN_VALUE, 0) > Integer.MAX_VALUE
            MathUtils.gcd(Integer.MIN_VALUE, 0);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Integer.MIN_VALUE) > Integer.MAX_VALUE
            MathUtils.gcd(0, Integer.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Integer.MIN_VALUE, Integer.MIN_VALUE) > Integer.MAX_VALUE
            MathUtils.gcd(Integer.MIN_VALUE, Integer.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void  testGcdLong(){
        long a = 30;
        long b = 50;
        long c = 77;

        Assert.assertEquals(0, MathUtils.gcd(0L, 0));

        Assert.assertEquals(b, MathUtils.gcd(0, b));
        Assert.assertEquals(a, MathUtils.gcd(a, 0));
        Assert.assertEquals(b, MathUtils.gcd(0, -b));
        Assert.assertEquals(a, MathUtils.gcd(-a, 0));

        Assert.assertEquals(10, MathUtils.gcd(a, b));
        Assert.assertEquals(10, MathUtils.gcd(-a, b));
        Assert.assertEquals(10, MathUtils.gcd(a, -b));
        Assert.assertEquals(10, MathUtils.gcd(-a, -b));

        Assert.assertEquals(1, MathUtils.gcd(a, c));
        Assert.assertEquals(1, MathUtils.gcd(-a, c));
        Assert.assertEquals(1, MathUtils.gcd(a, -c));
        Assert.assertEquals(1, MathUtils.gcd(-a, -c));

        Assert.assertEquals(3L * (1L<<45), MathUtils.gcd(3L * (1L<<50), 9L * (1L<<45)));

        Assert.assertEquals(1L<<45, MathUtils.gcd(1L<<45, Long.MIN_VALUE));

        Assert.assertEquals(Long.MAX_VALUE, MathUtils.gcd(Long.MAX_VALUE, 0L));
        Assert.assertEquals(Long.MAX_VALUE, MathUtils.gcd(-Long.MAX_VALUE, 0L));
        Assert.assertEquals(1, MathUtils.gcd(60247241209L, 153092023L));
        try {
            // gcd(Long.MIN_VALUE, 0) > Long.MAX_VALUE
            MathUtils.gcd(Long.MIN_VALUE, 0);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Long.MIN_VALUE) > Long.MAX_VALUE
            MathUtils.gcd(0, Long.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Long.MIN_VALUE, Long.MIN_VALUE) > Long.MAX_VALUE
            MathUtils.gcd(Long.MIN_VALUE, Long.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void testGcdConsistency() {
        int[] primeList = {19, 23, 53, 67, 73, 79, 101, 103, 111, 131};
        ArrayList<Integer> primes = new ArrayList<Integer>();
        for (int i = 0; i < primeList.length; i++) {
            primes.add(Integer.valueOf(primeList[i]));
        }
        RandomDataImpl randomData = new RandomDataImpl();
        for (int i = 0; i < 20; i++) {
            Object[] sample = randomData.nextSample(primes, 4);
            int p1 = ((Integer) sample[0]).intValue();
            int p2 = ((Integer) sample[1]).intValue();
            int p3 = ((Integer) sample[2]).intValue();
            int p4 = ((Integer) sample[3]).intValue();
            int i1 = p1 * p2 * p3;
            int i2 = p1 * p2 * p4;
            int gcd = p1 * p2;
            Assert.assertEquals(gcd, MathUtils.gcd(i1, i2));
            long l1 = i1;
            long l2 = i2;
            Assert.assertEquals(gcd, MathUtils.gcd(l1, l2));
        }
    }

    @Test
    public void testHash() {
        double[] testArray = {
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            1d,
            0d,
            1E-14,
            (1 + 1E-14),
            Double.MIN_VALUE,
            Double.MAX_VALUE };
        for (int i = 0; i < testArray.length; i++) {
            for (int j = 0; j < testArray.length; j++) {
                if (i == j) {
                    Assert.assertEquals(MathUtils.hash(testArray[i]), MathUtils.hash(testArray[j]));
                    Assert.assertEquals(MathUtils.hash(testArray[j]), MathUtils.hash(testArray[i]));
                } else {
                    Assert.assertTrue(MathUtils.hash(testArray[i]) != MathUtils.hash(testArray[j]));
                    Assert.assertTrue(MathUtils.hash(testArray[j]) != MathUtils.hash(testArray[i]));
                }
            }
        }
    }

    @Test
    public void testArrayHash() {
        Assert.assertEquals(0, MathUtils.hash((double[]) null));
        Assert.assertEquals(MathUtils.hash(new double[] {
                                      Double.NaN, Double.POSITIVE_INFINITY,
                                      Double.NEGATIVE_INFINITY, 1d, 0d
                                    }),
                     MathUtils.hash(new double[] {
                                      Double.NaN, Double.POSITIVE_INFINITY,
                                      Double.NEGATIVE_INFINITY, 1d, 0d
                                    }));
        Assert.assertFalse(MathUtils.hash(new double[] { 1d }) ==
                    MathUtils.hash(new double[] { FastMath.nextAfter(1d, 2d) }));
        Assert.assertFalse(MathUtils.hash(new double[] { 1d }) ==
                    MathUtils.hash(new double[] { 1d, 1d }));
    }

    /**
     * Make sure that permuted arrays do not hash to the same value.
     */
    @Test
    public void testPermutedArrayHash() {
        double[] original = new double[10];
        double[] permuted = new double[10];
        RandomDataImpl random = new RandomDataImpl();

        // Generate 10 distinct random values
        for (int i = 0; i < 10; i++) {
            original[i] = random.nextUniform(i + 0.5, i + 0.75);
        }

        // Generate a random permutation, making sure it is not the identity
        boolean isIdentity = true;
        do {
            int[] permutation = random.nextPermutation(10, 10);
            for (int i = 0; i < 10; i++) {
                if (i != permutation[i]) {
                    isIdentity = false;
                }
                permuted[i] = original[permutation[i]];
            }
        } while (isIdentity);

        // Verify that permuted array has different hash
        Assert.assertFalse(MathUtils.hash(original) == MathUtils.hash(permuted));
    }

    @Test
    public void testIndicatorByte() {
        Assert.assertEquals((byte)1, MathUtils.indicator((byte)2));
        Assert.assertEquals((byte)1, MathUtils.indicator((byte)0));
        Assert.assertEquals((byte)(-1), MathUtils.indicator((byte)(-2)));
    }

    @Test
    public void testIndicatorDouble() {
        double delta = 0.0;
        Assert.assertEquals(1.0, MathUtils.indicator(2.0), delta);
        Assert.assertEquals(1.0, MathUtils.indicator(0.0), delta);
        Assert.assertEquals(-1.0, MathUtils.indicator(-2.0), delta);
        Assert.assertTrue(Double.isNaN(MathUtils.indicator(Double.NaN)));
    }

    @Test
    public void testIndicatorFloat() {
        float delta = 0.0F;
        Assert.assertEquals(1.0F, MathUtils.indicator(2.0F), delta);
        Assert.assertEquals(1.0F, MathUtils.indicator(0.0F), delta);
        Assert.assertEquals(-1.0F, MathUtils.indicator(-2.0F), delta);
    }

    @Test
    public void testIndicatorInt() {
        Assert.assertEquals(1, MathUtils.indicator((2)));
        Assert.assertEquals(1, MathUtils.indicator((0)));
        Assert.assertEquals((-1), MathUtils.indicator((-2)));
    }

    @Test
    public void testIndicatorLong() {
        Assert.assertEquals(1L, MathUtils.indicator(2L));
        Assert.assertEquals(1L, MathUtils.indicator(0L));
        Assert.assertEquals(-1L, MathUtils.indicator(-2L));
    }

    @Test
    public void testIndicatorShort() {
        Assert.assertEquals((short)1, MathUtils.indicator((short)2));
        Assert.assertEquals((short)1, MathUtils.indicator((short)0));
        Assert.assertEquals((short)(-1), MathUtils.indicator((short)(-2)));
    }

    @Test
    public void testLcm() {
        int a = 30;
        int b = 50;
        int c = 77;

        Assert.assertEquals(0, MathUtils.lcm(0, b));
        Assert.assertEquals(0, MathUtils.lcm(a, 0));
        Assert.assertEquals(b, MathUtils.lcm(1, b));
        Assert.assertEquals(a, MathUtils.lcm(a, 1));
        Assert.assertEquals(150, MathUtils.lcm(a, b));
        Assert.assertEquals(150, MathUtils.lcm(-a, b));
        Assert.assertEquals(150, MathUtils.lcm(a, -b));
        Assert.assertEquals(150, MathUtils.lcm(-a, -b));
        Assert.assertEquals(2310, MathUtils.lcm(a, c));

        // Assert that no intermediate value overflows:
        // The naive implementation of lcm(a,b) would be (a*b)/gcd(a,b)
        Assert.assertEquals((1<<20)*15, MathUtils.lcm((1<<20)*3, (1<<20)*5));

        // Special case
        Assert.assertEquals(0, MathUtils.lcm(0, 0));

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            MathUtils.lcm(Integer.MIN_VALUE, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            MathUtils.lcm(Integer.MIN_VALUE, 1<<20);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        try {
            MathUtils.lcm(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void testLcmLong() {
        long a = 30;
        long b = 50;
        long c = 77;

        Assert.assertEquals(0, MathUtils.lcm(0, b));
        Assert.assertEquals(0, MathUtils.lcm(a, 0));
        Assert.assertEquals(b, MathUtils.lcm(1, b));
        Assert.assertEquals(a, MathUtils.lcm(a, 1));
        Assert.assertEquals(150, MathUtils.lcm(a, b));
        Assert.assertEquals(150, MathUtils.lcm(-a, b));
        Assert.assertEquals(150, MathUtils.lcm(a, -b));
        Assert.assertEquals(150, MathUtils.lcm(-a, -b));
        Assert.assertEquals(2310, MathUtils.lcm(a, c));

        Assert.assertEquals(Long.MAX_VALUE, MathUtils.lcm(60247241209L, 153092023L));

        // Assert that no intermediate value overflows:
        // The naive implementation of lcm(a,b) would be (a*b)/gcd(a,b)
        Assert.assertEquals((1L<<50)*15, MathUtils.lcm((1L<<45)*3, (1L<<50)*5));

        // Special case
        Assert.assertEquals(0L, MathUtils.lcm(0L, 0L));

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            MathUtils.lcm(Long.MIN_VALUE, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            MathUtils.lcm(Long.MIN_VALUE, 1<<20);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        Assert.assertEquals((long) Integer.MAX_VALUE * (Integer.MAX_VALUE - 1),
            MathUtils.lcm((long)Integer.MAX_VALUE, Integer.MAX_VALUE - 1));
        try {
            MathUtils.lcm(Long.MAX_VALUE, Long.MAX_VALUE - 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void testLog() {
        Assert.assertEquals(2.0, MathUtils.log(2, 4), 0);
        Assert.assertEquals(3.0, MathUtils.log(2, 8), 0);
        Assert.assertTrue(Double.isNaN(MathUtils.log(-1, 1)));
        Assert.assertTrue(Double.isNaN(MathUtils.log(1, -1)));
        Assert.assertTrue(Double.isNaN(MathUtils.log(0, 0)));
        Assert.assertEquals(0, MathUtils.log(0, 10), 0);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, MathUtils.log(10, 0), 0);
    }

    @Test
    public void testMulAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, MathUtils.mulAndCheck(big, 1));
        try {
            MathUtils.mulAndCheck(big, 2);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            MathUtils.mulAndCheck(bigNeg, 2);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }

    @Test
    public void testMulAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, MathUtils.mulAndCheck(max, 1L));
        Assert.assertEquals(min, MathUtils.mulAndCheck(min, 1L));
        Assert.assertEquals(0L, MathUtils.mulAndCheck(max, 0L));
        Assert.assertEquals(0L, MathUtils.mulAndCheck(min, 0L));
        Assert.assertEquals(max, MathUtils.mulAndCheck(1L, max));
        Assert.assertEquals(min, MathUtils.mulAndCheck(1L, min));
        Assert.assertEquals(0L, MathUtils.mulAndCheck(0L, max));
        Assert.assertEquals(0L, MathUtils.mulAndCheck(0L, min));
        Assert.assertEquals(1L, MathUtils.mulAndCheck(-1L, -1L));
        Assert.assertEquals(min, MathUtils.mulAndCheck(min / 2, 2));
        testMulAndCheckLongFailure(max, 2L);
        testMulAndCheckLongFailure(2L, max);
        testMulAndCheckLongFailure(min, 2L);
        testMulAndCheckLongFailure(2L, min);
        testMulAndCheckLongFailure(min, -1L);
        testMulAndCheckLongFailure(-1L, min);
    }

    private void testMulAndCheckLongFailure(long a, long b) {
        try {
            MathUtils.mulAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }
    }

    @Test
    public void testNormalizeAngle() {
        for (double a = -15.0; a <= 15.0; a += 0.1) {
            for (double b = -15.0; b <= 15.0; b += 0.2) {
                double c = MathUtils.normalizeAngle(a, b);
                Assert.assertTrue((b - FastMath.PI) <= c);
                Assert.assertTrue(c <= (b + FastMath.PI));
                double twoK = FastMath.rint((a - c) / FastMath.PI);
                Assert.assertEquals(c, a - twoK * FastMath.PI, 1.0e-14);
            }
        }
    }

    @Test
    public void testReduce() {
        final double period = -12.222;
        final double offset = 13;

        final double delta = 1.5;

        double orig = offset + 122456789 * period + delta;
        double expected = delta;
        Assert.assertEquals(expected,
                            MathUtils.reduce(orig, period, offset),
                            1e-7);
        Assert.assertEquals(expected,
                            MathUtils.reduce(orig, -period, offset),
                            1e-7);

        orig = offset - 123356789 * period - delta;
        expected = Math.abs(period) - delta;
        Assert.assertEquals(expected,
                            MathUtils.reduce(orig, period, offset),
                            1e-6);
        Assert.assertEquals(expected,
                            MathUtils.reduce(orig, -period, offset),
                            1e-6);

        orig = offset - 123446789 * period + delta;
        expected = delta;
        Assert.assertEquals(expected,
                            MathUtils.reduce(orig, period, offset),
                            1e-6);
        Assert.assertEquals(expected,
                            MathUtils.reduce(orig, -period, offset),
                            1e-6);
        
        Assert.assertTrue(Double.isNaN(MathUtils.reduce(orig, Double.NaN, offset)));
        Assert.assertTrue(Double.isNaN(MathUtils.reduce(Double.NaN, period, offset)));
        Assert.assertTrue(Double.isNaN(MathUtils.reduce(orig, period, Double.NaN)));
        Assert.assertTrue(Double.isNaN(MathUtils.reduce(orig, period,
                Double.POSITIVE_INFINITY)));
        Assert.assertTrue(Double.isNaN(MathUtils.reduce(Double.POSITIVE_INFINITY,
                period, offset)));
        Assert.assertTrue(Double.isNaN(MathUtils.reduce(orig,
                Double.POSITIVE_INFINITY, offset)));   
        Assert.assertTrue(Double.isNaN(MathUtils.reduce(orig,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)));
        Assert.assertTrue(Double.isNaN(MathUtils.reduce(Double.POSITIVE_INFINITY,
                period, Double.POSITIVE_INFINITY)));
        Assert.assertTrue(Double.isNaN(MathUtils.reduce(Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY, offset))); 
        Assert.assertTrue(Double.isNaN(MathUtils.reduce(Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY,  Double.POSITIVE_INFINITY)));
    }

    @Test
    public void testReduceComparedWithNormalizeAngle() {
        final double tol = Math.ulp(1d);
        final double period = 2 * Math.PI;
        for (double a = -15; a <= 15; a += 0.5) {
            for (double center = -15; center <= 15; center += 1) {
                final double nA = MathUtils.normalizeAngle(a, center);
                final double offset = center - Math.PI;
                final double r = MathUtils.reduce(a, period, offset);
                Assert.assertEquals(nA, r + offset, tol);
            }
        }
    }

    @Test
    public void testNormalizeArray() {
        double[] testValues1 = new double[] {1, 1, 2};
        TestUtils.assertEquals(
                new double[] {.25, .25, .5},
                MathUtils.normalizeArray(testValues1, 1),
                Double.MIN_VALUE);

        double[] testValues2 = new double[] {-1, -1, 1};
        TestUtils.assertEquals(
                new double[] {1, 1, -1},
                MathUtils.normalizeArray(testValues2, 1),
                Double.MIN_VALUE);

        // Ignore NaNs
        double[] testValues3 = new double[] {-1, -1, Double.NaN, 1, Double.NaN};
        TestUtils.assertEquals(
                new double[] {1, 1,Double.NaN, -1, Double.NaN},
                MathUtils.normalizeArray(testValues3, 1),
                Double.MIN_VALUE);

        // Zero sum -> MathArithmeticException
        double[] zeroSum = new double[] {-1, 1};
        try {
            MathUtils.normalizeArray(zeroSum, 1);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {}

        // Infinite elements -> MathArithmeticException
        double[] hasInf = new double[] {1, 2, 1, Double.NEGATIVE_INFINITY};
        try {
            MathUtils.normalizeArray(hasInf, 1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {}

        // Infinite target -> MathIllegalArgumentException
        try {
            MathUtils.normalizeArray(testValues1, Double.POSITIVE_INFINITY);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {}

        // NaN target -> MathIllegalArgumentException
        try {
            MathUtils.normalizeArray(testValues1, Double.NaN);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {}

    }

    @Test
    public void testRoundDouble() {
        double x = 1.234567890;
        Assert.assertEquals(1.23, MathUtils.round(x, 2), 0.0);
        Assert.assertEquals(1.235, MathUtils.round(x, 3), 0.0);
        Assert.assertEquals(1.2346, MathUtils.round(x, 4), 0.0);

        // JIRA MATH-151
        Assert.assertEquals(39.25, MathUtils.round(39.245, 2), 0.0);
        Assert.assertEquals(39.24, MathUtils.round(39.245, 2, BigDecimal.ROUND_DOWN), 0.0);
        double xx = 39.0;
        xx = xx + 245d / 1000d;
        Assert.assertEquals(39.25, MathUtils.round(xx, 2), 0.0);

        // BZ 35904
        Assert.assertEquals(30.1d, MathUtils.round(30.095d, 2), 0.0d);
        Assert.assertEquals(30.1d, MathUtils.round(30.095d, 1), 0.0d);
        Assert.assertEquals(33.1d, MathUtils.round(33.095d, 1), 0.0d);
        Assert.assertEquals(33.1d, MathUtils.round(33.095d, 2), 0.0d);
        Assert.assertEquals(50.09d, MathUtils.round(50.085d, 2), 0.0d);
        Assert.assertEquals(50.19d, MathUtils.round(50.185d, 2), 0.0d);
        Assert.assertEquals(50.01d, MathUtils.round(50.005d, 2), 0.0d);
        Assert.assertEquals(30.01d, MathUtils.round(30.005d, 2), 0.0d);
        Assert.assertEquals(30.65d, MathUtils.round(30.645d, 2), 0.0d);

        Assert.assertEquals(1.24, MathUtils.round(x, 2, BigDecimal.ROUND_CEILING), 0.0);
        Assert.assertEquals(1.235, MathUtils.round(x, 3, BigDecimal.ROUND_CEILING), 0.0);
        Assert.assertEquals(1.2346, MathUtils.round(x, 4, BigDecimal.ROUND_CEILING), 0.0);
        Assert.assertEquals(-1.23, MathUtils.round(-x, 2, BigDecimal.ROUND_CEILING), 0.0);
        Assert.assertEquals(-1.234, MathUtils.round(-x, 3, BigDecimal.ROUND_CEILING), 0.0);
        Assert.assertEquals(-1.2345, MathUtils.round(-x, 4, BigDecimal.ROUND_CEILING), 0.0);

        Assert.assertEquals(1.23, MathUtils.round(x, 2, BigDecimal.ROUND_DOWN), 0.0);
        Assert.assertEquals(1.234, MathUtils.round(x, 3, BigDecimal.ROUND_DOWN), 0.0);
        Assert.assertEquals(1.2345, MathUtils.round(x, 4, BigDecimal.ROUND_DOWN), 0.0);
        Assert.assertEquals(-1.23, MathUtils.round(-x, 2, BigDecimal.ROUND_DOWN), 0.0);
        Assert.assertEquals(-1.234, MathUtils.round(-x, 3, BigDecimal.ROUND_DOWN), 0.0);
        Assert.assertEquals(-1.2345, MathUtils.round(-x, 4, BigDecimal.ROUND_DOWN), 0.0);

        Assert.assertEquals(1.23, MathUtils.round(x, 2, BigDecimal.ROUND_FLOOR), 0.0);
        Assert.assertEquals(1.234, MathUtils.round(x, 3, BigDecimal.ROUND_FLOOR), 0.0);
        Assert.assertEquals(1.2345, MathUtils.round(x, 4, BigDecimal.ROUND_FLOOR), 0.0);
        Assert.assertEquals(-1.24, MathUtils.round(-x, 2, BigDecimal.ROUND_FLOOR), 0.0);
        Assert.assertEquals(-1.235, MathUtils.round(-x, 3, BigDecimal.ROUND_FLOOR), 0.0);
        Assert.assertEquals(-1.2346, MathUtils.round(-x, 4, BigDecimal.ROUND_FLOOR), 0.0);

        Assert.assertEquals(1.23, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(1.235, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(1.2346, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(-1.23, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(-1.235, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(-1.2346, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(1.234, MathUtils.round(1.2345, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(-1.234, MathUtils.round(-1.2345, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);

        Assert.assertEquals(1.23, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(1.235, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(1.2346, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(-1.23, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(-1.235, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(-1.2346, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(1.234, MathUtils.round(1.2345, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(-1.234, MathUtils.round(-1.2345, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(1.236, MathUtils.round(1.2355, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(-1.236, MathUtils.round(-1.2355, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);

        Assert.assertEquals(1.23, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(1.235, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(1.2346, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(-1.23, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(-1.235, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(-1.2346, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(1.235, MathUtils.round(1.2345, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(-1.235, MathUtils.round(-1.2345, 3, BigDecimal.ROUND_HALF_UP), 0.0);

        Assert.assertEquals(-1.23, MathUtils.round(-1.23, 2, BigDecimal.ROUND_UNNECESSARY), 0.0);
        Assert.assertEquals(1.23, MathUtils.round(1.23, 2, BigDecimal.ROUND_UNNECESSARY), 0.0);

        try {
            MathUtils.round(1.234, 2, BigDecimal.ROUND_UNNECESSARY);
            Assert.fail();
        } catch (ArithmeticException ex) { 
            // expected
        }

        Assert.assertEquals(1.24, MathUtils.round(x, 2, BigDecimal.ROUND_UP), 0.0);
        Assert.assertEquals(1.235, MathUtils.round(x, 3, BigDecimal.ROUND_UP), 0.0);
        Assert.assertEquals(1.2346, MathUtils.round(x, 4, BigDecimal.ROUND_UP), 0.0);
        Assert.assertEquals(-1.24, MathUtils.round(-x, 2, BigDecimal.ROUND_UP), 0.0);
        Assert.assertEquals(-1.235, MathUtils.round(-x, 3, BigDecimal.ROUND_UP), 0.0);
        Assert.assertEquals(-1.2346, MathUtils.round(-x, 4, BigDecimal.ROUND_UP), 0.0);

        try {
            MathUtils.round(1.234, 2, 1923);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }

        // MATH-151
        Assert.assertEquals(39.25, MathUtils.round(39.245, 2, BigDecimal.ROUND_HALF_UP), 0.0);

        // special values
        TestUtils.assertEquals(Double.NaN, MathUtils.round(Double.NaN, 2), 0.0);
        Assert.assertEquals(0.0, MathUtils.round(0.0, 2), 0.0);
        Assert.assertEquals(Double.POSITIVE_INFINITY, MathUtils.round(Double.POSITIVE_INFINITY, 2), 0.0);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, MathUtils.round(Double.NEGATIVE_INFINITY, 2), 0.0);
    }

    @Test
    public void testRoundFloat() {
        float x = 1.234567890f;
        Assert.assertEquals(1.23f, MathUtils.round(x, 2), 0.0);
        Assert.assertEquals(1.235f, MathUtils.round(x, 3), 0.0);
        Assert.assertEquals(1.2346f, MathUtils.round(x, 4), 0.0);

        // BZ 35904
        Assert.assertEquals(30.1f, MathUtils.round(30.095f, 2), 0.0f);
        Assert.assertEquals(30.1f, MathUtils.round(30.095f, 1), 0.0f);
        Assert.assertEquals(50.09f, MathUtils.round(50.085f, 2), 0.0f);
        Assert.assertEquals(50.19f, MathUtils.round(50.185f, 2), 0.0f);
        Assert.assertEquals(50.01f, MathUtils.round(50.005f, 2), 0.0f);
        Assert.assertEquals(30.01f, MathUtils.round(30.005f, 2), 0.0f);
        Assert.assertEquals(30.65f, MathUtils.round(30.645f, 2), 0.0f);

        Assert.assertEquals(1.24f, MathUtils.round(x, 2, BigDecimal.ROUND_CEILING), 0.0);
        Assert.assertEquals(1.235f, MathUtils.round(x, 3, BigDecimal.ROUND_CEILING), 0.0);
        Assert.assertEquals(1.2346f, MathUtils.round(x, 4, BigDecimal.ROUND_CEILING), 0.0);
        Assert.assertEquals(-1.23f, MathUtils.round(-x, 2, BigDecimal.ROUND_CEILING), 0.0);
        Assert.assertEquals(-1.234f, MathUtils.round(-x, 3, BigDecimal.ROUND_CEILING), 0.0);
        Assert.assertEquals(-1.2345f, MathUtils.round(-x, 4, BigDecimal.ROUND_CEILING), 0.0);

        Assert.assertEquals(1.23f, MathUtils.round(x, 2, BigDecimal.ROUND_DOWN), 0.0);
        Assert.assertEquals(1.234f, MathUtils.round(x, 3, BigDecimal.ROUND_DOWN), 0.0);
        Assert.assertEquals(1.2345f, MathUtils.round(x, 4, BigDecimal.ROUND_DOWN), 0.0);
        Assert.assertEquals(-1.23f, MathUtils.round(-x, 2, BigDecimal.ROUND_DOWN), 0.0);
        Assert.assertEquals(-1.234f, MathUtils.round(-x, 3, BigDecimal.ROUND_DOWN), 0.0);
        Assert.assertEquals(-1.2345f, MathUtils.round(-x, 4, BigDecimal.ROUND_DOWN), 0.0);

        Assert.assertEquals(1.23f, MathUtils.round(x, 2, BigDecimal.ROUND_FLOOR), 0.0);
        Assert.assertEquals(1.234f, MathUtils.round(x, 3, BigDecimal.ROUND_FLOOR), 0.0);
        Assert.assertEquals(1.2345f, MathUtils.round(x, 4, BigDecimal.ROUND_FLOOR), 0.0);
        Assert.assertEquals(-1.24f, MathUtils.round(-x, 2, BigDecimal.ROUND_FLOOR), 0.0);
        Assert.assertEquals(-1.235f, MathUtils.round(-x, 3, BigDecimal.ROUND_FLOOR), 0.0);
        Assert.assertEquals(-1.2346f, MathUtils.round(-x, 4, BigDecimal.ROUND_FLOOR), 0.0);

        Assert.assertEquals(1.23f, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(1.235f, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(1.2346f, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(-1.23f, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(-1.235f, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(-1.2346f, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(1.234f, MathUtils.round(1.2345f, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        Assert.assertEquals(-1.234f, MathUtils.round(-1.2345f, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);

        Assert.assertEquals(1.23f, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(1.235f, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(1.2346f, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(-1.23f, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(-1.235f, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(-1.2346f, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(1.234f, MathUtils.round(1.2345f, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(-1.234f, MathUtils.round(-1.2345f, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(1.236f, MathUtils.round(1.2355f, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        Assert.assertEquals(-1.236f, MathUtils.round(-1.2355f, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);

        Assert.assertEquals(1.23f, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(1.235f, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(1.2346f, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(-1.23f, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(-1.235f, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(-1.2346f, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(1.235f, MathUtils.round(1.2345f, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        Assert.assertEquals(-1.235f, MathUtils.round(-1.2345f, 3, BigDecimal.ROUND_HALF_UP), 0.0);

        Assert.assertEquals(-1.23f, MathUtils.round(-1.23f, 2, BigDecimal.ROUND_UNNECESSARY), 0.0);
        Assert.assertEquals(1.23f, MathUtils.round(1.23f, 2, BigDecimal.ROUND_UNNECESSARY), 0.0);

        try {
            MathUtils.round(1.234f, 2, BigDecimal.ROUND_UNNECESSARY);
            Assert.fail();
        } catch (MathArithmeticException ex) {
            // success
        }

        Assert.assertEquals(1.24f, MathUtils.round(x, 2, BigDecimal.ROUND_UP), 0.0);
        Assert.assertEquals(1.235f, MathUtils.round(x, 3, BigDecimal.ROUND_UP), 0.0);
        Assert.assertEquals(1.2346f, MathUtils.round(x, 4, BigDecimal.ROUND_UP), 0.0);
        Assert.assertEquals(-1.24f, MathUtils.round(-x, 2, BigDecimal.ROUND_UP), 0.0);
        Assert.assertEquals(-1.235f, MathUtils.round(-x, 3, BigDecimal.ROUND_UP), 0.0);
        Assert.assertEquals(-1.2346f, MathUtils.round(-x, 4, BigDecimal.ROUND_UP), 0.0);

        try {
            MathUtils.round(1.234f, 2, 1923);
            Assert.fail();
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        // special values
        TestUtils.assertEquals(Float.NaN, MathUtils.round(Float.NaN, 2), 0.0f);
        Assert.assertEquals(0.0f, MathUtils.round(0.0f, 2), 0.0f);
        Assert.assertEquals(Float.POSITIVE_INFINITY, MathUtils.round(Float.POSITIVE_INFINITY, 2), 0.0f);
        Assert.assertEquals(Float.NEGATIVE_INFINITY, MathUtils.round(Float.NEGATIVE_INFINITY, 2), 0.0f);
    }

    @Test
    public void testSignByte() {
        Assert.assertEquals((byte) 1, MathUtils.sign((byte) 2));
        Assert.assertEquals((byte) 0, MathUtils.sign((byte) 0));
        Assert.assertEquals((byte) (-1), MathUtils.sign((byte) (-2)));
    }

    @Test
    public void testSignDouble() {
        double delta = 0.0;
        Assert.assertEquals(1.0, MathUtils.sign(2.0), delta);
        Assert.assertEquals(0.0, MathUtils.sign(0.0), delta);
        Assert.assertEquals(-1.0, MathUtils.sign(-2.0), delta);
        TestUtils.assertSame(-0. / 0., MathUtils.sign(Double.NaN));
    }

    @Test
    public void testSignFloat() {
        float delta = 0.0F;
        Assert.assertEquals(1.0F, MathUtils.sign(2.0F), delta);
        Assert.assertEquals(0.0F, MathUtils.sign(0.0F), delta);
        Assert.assertEquals(-1.0F, MathUtils.sign(-2.0F), delta);
        TestUtils.assertSame(Float.NaN, MathUtils.sign(Float.NaN));
    }

    @Test
    public void testSignInt() {
        Assert.assertEquals(1, MathUtils.sign(2));
        Assert.assertEquals(0, MathUtils.sign(0));
        Assert.assertEquals((-1), MathUtils.sign((-2)));
    }

    @Test
    public void testSignLong() {
        Assert.assertEquals(1L, MathUtils.sign(2L));
        Assert.assertEquals(0L, MathUtils.sign(0L));
        Assert.assertEquals(-1L, MathUtils.sign(-2L));
    }

    @Test
    public void testSignShort() {
        Assert.assertEquals((short) 1, MathUtils.sign((short) 2));
        Assert.assertEquals((short) 0, MathUtils.sign((short) 0));
        Assert.assertEquals((short) (-1), MathUtils.sign((short) (-2)));
    }

    @Test
    public void testSinh() {
        double x = 3.0;
        double expected = 10.01787;
        Assert.assertEquals(expected, MathUtils.sinh(x), 1.0e-5);
    }

    @Test
    public void testSinhNaN() {
        Assert.assertTrue(Double.isNaN(MathUtils.sinh(Double.NaN)));
    }

    @Test
    public void testSubAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, MathUtils.subAndCheck(big, 0));
        Assert.assertEquals(bigNeg + 1, MathUtils.subAndCheck(bigNeg, -1));
        Assert.assertEquals(-1, MathUtils.subAndCheck(bigNeg, -big));
        try {
            MathUtils.subAndCheck(big, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            MathUtils.subAndCheck(bigNeg, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }

    @Test
    public void testSubAndCheckErrorMessage() {
        int big = Integer.MAX_VALUE;
        try {
            MathUtils.subAndCheck(big, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            Assert.assertTrue(ex.getMessage().length() > 1);
        }
    }

    @Test
    public void testSubAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, MathUtils.subAndCheck(max, 0));
        Assert.assertEquals(min, MathUtils.subAndCheck(min, 0));
        Assert.assertEquals(-max, MathUtils.subAndCheck(0, max));
        Assert.assertEquals(min + 1, MathUtils.subAndCheck(min, -1));
        // min == -1-max
        Assert.assertEquals(-1, MathUtils.subAndCheck(-max - 1, -max));
        Assert.assertEquals(max, MathUtils.subAndCheck(-1, -1 - max));
        testSubAndCheckLongFailure(0L, min);
        testSubAndCheckLongFailure(max, -1L);
        testSubAndCheckLongFailure(min, 1L);
    }

    private void testSubAndCheckLongFailure(long a, long b) {
        try {
            MathUtils.subAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }

    }

    @Test
    public void testPow() {

        Assert.assertEquals(1801088541, MathUtils.pow(21, 7));
        Assert.assertEquals(1, MathUtils.pow(21, 0));
        try {
            MathUtils.pow(21, -7);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(1801088541, MathUtils.pow(21, 7l));
        Assert.assertEquals(1, MathUtils.pow(21, 0l));
        try {
            MathUtils.pow(21, -7l);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(1801088541l, MathUtils.pow(21l, 7));
        Assert.assertEquals(1l, MathUtils.pow(21l, 0));
        try {
            MathUtils.pow(21l, -7);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(1801088541l, MathUtils.pow(21l, 7l));
        Assert.assertEquals(1l, MathUtils.pow(21l, 0l));
        try {
            MathUtils.pow(21l, -7l);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        BigInteger twentyOne = BigInteger.valueOf(21l);
        Assert.assertEquals(BigInteger.valueOf(1801088541l), MathUtils.pow(twentyOne, 7));
        Assert.assertEquals(BigInteger.ONE, MathUtils.pow(twentyOne, 0));
        try {
            MathUtils.pow(twentyOne, -7);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(BigInteger.valueOf(1801088541l), MathUtils.pow(twentyOne, 7l));
        Assert.assertEquals(BigInteger.ONE, MathUtils.pow(twentyOne, 0l));
        try {
            MathUtils.pow(twentyOne, -7l);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(BigInteger.valueOf(1801088541l), MathUtils.pow(twentyOne, BigInteger.valueOf(7l)));
        Assert.assertEquals(BigInteger.ONE, MathUtils.pow(twentyOne, BigInteger.ZERO));
        try {
            MathUtils.pow(twentyOne, BigInteger.valueOf(-7l));
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        BigInteger bigOne =
            new BigInteger("1543786922199448028351389769265814882661837148" +
                           "4763915343722775611762713982220306372888519211" +
                           "560905579993523402015636025177602059044911261");
        Assert.assertEquals(bigOne, MathUtils.pow(twentyOne, 103));
        Assert.assertEquals(bigOne, MathUtils.pow(twentyOne, 103l));
        Assert.assertEquals(bigOne, MathUtils.pow(twentyOne, BigInteger.valueOf(103l)));

    }

    @Test
    public void testL1DistanceDouble() {
        double[] p1 = { 2.5,  0.0 };
        double[] p2 = { -0.5, 4.0 };
        Assert.assertTrue(MathUtils.equals(7.0, MathUtils.distance1(p1, p2), 1));
    }

    @Test
    public void testL1DistanceInt() {
        int[] p1 = { 3, 0 };
        int[] p2 = { 0, 4 };
        Assert.assertEquals(7, MathUtils.distance1(p1, p2));
    }

    @Test
    public void testL2DistanceDouble() {
        double[] p1 = { 2.5,  0.0 };
        double[] p2 = { -0.5, 4.0 };
        Assert.assertTrue(MathUtils.equals(5.0, MathUtils.distance(p1, p2), 1));
    }

    @Test
    public void testL2DistanceInt() {
        int[] p1 = { 3, 0 };
        int[] p2 = { 0, 4 };
        Assert.assertTrue(MathUtils.equals(5, MathUtils.distance(p1, p2), 1));
    }

    @Test
    public void testLInfDistanceDouble() {
        double[] p1 = { 2.5,  0.0 };
        double[] p2 = { -0.5, 4.0 };
        Assert.assertTrue(MathUtils.equals(4.0, MathUtils.distanceInf(p1, p2), 1));
    }

    @Test
    public void testLInfDistanceInt() {
        int[] p1 = { 3, 0 };
        int[] p2 = { 0, 4 };
        Assert.assertEquals(4, MathUtils.distanceInf(p1, p2));
    }

    @Test
    public void testCheckOrder() {
        MathUtils.checkOrder(new double[] {-15, -5.5, -1, 2, 15},
                             MathUtils.OrderDirection.INCREASING, true);
        MathUtils.checkOrder(new double[] {-15, -5.5, -1, 2, 2},
                             MathUtils.OrderDirection.INCREASING, false);
        MathUtils.checkOrder(new double[] {3, -5.5, -11, -27.5},
                             MathUtils.OrderDirection.DECREASING, true);
        MathUtils.checkOrder(new double[] {3, 0, 0, -5.5, -11, -27.5},
                             MathUtils.OrderDirection.DECREASING, false);

        try {
            MathUtils.checkOrder(new double[] {-15, -5.5, -1, -1, 2, 15},
                                 MathUtils.OrderDirection.INCREASING, true);
            Assert.fail("an exception should have been thrown");
        } catch (NonMonotonousSequenceException e) {
            // Expected
        }
        try {
            MathUtils.checkOrder(new double[] {-15, -5.5, -1, -2, 2},
                                 MathUtils.OrderDirection.INCREASING, false);
            Assert.fail("an exception should have been thrown");
        } catch (NonMonotonousSequenceException e) {
            // Expected
        }
        try {
            MathUtils.checkOrder(new double[] {3, 3, -5.5, -11, -27.5},
                                 MathUtils.OrderDirection.DECREASING, true);
            Assert.fail("an exception should have been thrown");
        } catch (NonMonotonousSequenceException e) {
            // Expected
        }
        try {
            MathUtils.checkOrder(new double[] {3, -1, 0, -5.5, -11, -27.5},
                                 MathUtils.OrderDirection.DECREASING, false);
            Assert.fail("an exception should have been thrown");
        } catch (NonMonotonousSequenceException e) {
            // Expected
        }
    }

    @Test
    public void testCheckFinite() {
        try {
            MathUtils.checkFinite(Double.POSITIVE_INFINITY);
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
        try {
            MathUtils.checkFinite(Double.NEGATIVE_INFINITY);
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
        try {
            MathUtils.checkFinite(Double.NaN);
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }

        try {
            MathUtils.checkFinite(new double[] {0, -1, Double.POSITIVE_INFINITY, -2, 3});
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
        try {
            MathUtils.checkFinite(new double[] {1, Double.NEGATIVE_INFINITY, -2, 3});
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
        try {
            MathUtils.checkFinite(new double[] {4, 3, -1, Double.NaN, -2, 1});
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
    }

    @Test
    public void testSortInPlace() {
        final double[] x1 = {2,   5,  -3, 1,  4};
        final double[] x2 = {4,  25,   9, 1, 16};
        final double[] x3 = {8, 125, -27, 1, 64};
        
        MathUtils.sortInPlace(x1, x2, x3);

        Assert.assertEquals(-3,  x1[0], Math.ulp(1d));
        Assert.assertEquals(9,   x2[0], Math.ulp(1d));
        Assert.assertEquals(-27, x3[0], Math.ulp(1d));

        Assert.assertEquals(1, x1[1], Math.ulp(1d));
        Assert.assertEquals(1, x2[1], Math.ulp(1d));
        Assert.assertEquals(1, x3[1], Math.ulp(1d));

        Assert.assertEquals(2, x1[2], Math.ulp(1d));
        Assert.assertEquals(4, x2[2], Math.ulp(1d));
        Assert.assertEquals(8, x3[2], Math.ulp(1d));

        Assert.assertEquals(4,  x1[3], Math.ulp(1d));
        Assert.assertEquals(16, x2[3], Math.ulp(1d));
        Assert.assertEquals(64, x3[3], Math.ulp(1d));

        Assert.assertEquals(5,   x1[4], Math.ulp(1d));
        Assert.assertEquals(25,  x2[4], Math.ulp(1d));
        Assert.assertEquals(125, x3[4], Math.ulp(1d));
    }

    @Test
    public void testCopyOfInt() {
        final int[] source = { Integer.MIN_VALUE,
                               -1, 0, 1, 3, 113, 4769,
                               Integer.MAX_VALUE };
        final int[] dest = MathUtils.copyOf(source);

        Assert.assertEquals(dest.length, source.length);
        for (int i = 0; i < source.length; i++) {
            Assert.assertEquals(source[i], dest[i]);
        }
    }

    @Test
    public void testCopyOfInt2() {
        final int[] source = { Integer.MIN_VALUE,
                               -1, 0, 1, 3, 113, 4769,
                               Integer.MAX_VALUE };
        final int offset = 3;
        final int[] dest = MathUtils.copyOf(source, source.length - offset);

        Assert.assertEquals(dest.length, source.length - offset);
        for (int i = 0; i < source.length - offset; i++) {
            Assert.assertEquals(source[i], dest[i]);
        }
    }

    @Test
    public void testCopyOfInt3() {
        final int[] source = { Integer.MIN_VALUE,
                               -1, 0, 1, 3, 113, 4769,
                               Integer.MAX_VALUE };
        final int offset = 3;
        final int[] dest = MathUtils.copyOf(source, source.length + offset);

        Assert.assertEquals(dest.length, source.length + offset);
        for (int i = 0; i < source.length; i++) {
            Assert.assertEquals(source[i], dest[i]);
        }
        for (int i = source.length; i < source.length + offset; i++) {
            Assert.assertEquals(0, dest[i], 0);
        }
    }

    @Test
    public void testCopyOfDouble() {
        final double[] source = { Double.NEGATIVE_INFINITY,
                                  -Double.MAX_VALUE,
                                  -1, 0,
                                  Double.MIN_VALUE,
                                  Math.ulp(1d),
                                  1, 3, 113, 4769,
                                  Double.MAX_VALUE,
                                  Double.POSITIVE_INFINITY };
        final double[] dest = MathUtils.copyOf(source);

        Assert.assertEquals(dest.length, source.length);
        for (int i = 0; i < source.length; i++) {
            Assert.assertEquals(source[i], dest[i], 0);
        }
    }

    @Test
    public void testCopyOfDouble2() {
        final double[] source = { Double.NEGATIVE_INFINITY,
                                  -Double.MAX_VALUE,
                                  -1, 0,
                                  Double.MIN_VALUE,
                                  Math.ulp(1d),
                                  1, 3, 113, 4769,
                                  Double.MAX_VALUE,
                                  Double.POSITIVE_INFINITY };
        final int offset = 3;
        final double[] dest = MathUtils.copyOf(source, source.length - offset);

        Assert.assertEquals(dest.length, source.length - offset);
        for (int i = 0; i < source.length - offset; i++) {
            Assert.assertEquals(source[i], dest[i], 0);
        }
    }

    @Test
    public void testCopyOfDouble3() {
        final double[] source = { Double.NEGATIVE_INFINITY,
                                  -Double.MAX_VALUE,
                                  -1, 0,
                                  Double.MIN_VALUE,
                                  Math.ulp(1d),
                                  1, 3, 113, 4769,
                                  Double.MAX_VALUE,
                                  Double.POSITIVE_INFINITY };
        final int offset = 3;
        final double[] dest = MathUtils.copyOf(source, source.length + offset);

        Assert.assertEquals(dest.length, source.length + offset);
        for (int i = 0; i < source.length; i++) {
            Assert.assertEquals(source[i], dest[i], 0);
        }
        for (int i = source.length; i < source.length + offset; i++) {
            Assert.assertEquals(0, dest[i], 0);
        }
    }

    @Test
    public void testCheckNotNull1() {
        try {
            Object obj = null;
            MathUtils.checkNotNull(obj);
        } catch (NullArgumentException e) {
            // Expected.
        }
    }

    @Test
    public void testCheckNotNull2() {
        try {
            double[] array = null;
            MathUtils.checkNotNull(array, LocalizedFormats.INPUT_ARRAY);
        } catch (NullArgumentException e) {
            // Expected.
        }
    }

    @Test
    public void testLinearCombination1() {
        final double[] a = new double[] {
            -1321008684645961.0 / 268435456.0,
            -5774608829631843.0 / 268435456.0,
            -7645843051051357.0 / 8589934592.0
        };
        final double[] b = new double[] {
            -5712344449280879.0 / 2097152.0,
            -4550117129121957.0 / 2097152.0,
            8846951984510141.0 / 131072.0
        };

        final double abSumInline = MathUtils.linearCombination(a[0], b[0],
                                                               a[1], b[1],
                                                               a[2], b[2]);
        final double abSumArray = MathUtils.linearCombination(a, b);

        Assert.assertEquals(abSumInline, abSumArray, 0);
    }

    @Test
    public void testLinearCombination2() {
        // we compare accurate versus naive dot product implementations
        // on regular vectors (i.e. not extreme cases like in the previous test)
        Well1024a random = new Well1024a(553267312521321234l);

        for (int i = 0; i < 10000; ++i) {
            final double ux = 1e17 * random.nextDouble();
            final double uy = 1e17 * random.nextDouble();
            final double uz = 1e17 * random.nextDouble();
            final double vx = 1e17 * random.nextDouble();
            final double vy = 1e17 * random.nextDouble();
            final double vz = 1e17 * random.nextDouble();
            final double sInline = MathUtils.linearCombination(ux, vx,
                                                               uy, vy,
                                                               uz, vz);
            final double sArray = MathUtils.linearCombination(new double[] {ux, uy, uz},
                                                              new double[] {vx, vy, vz});
            Assert.assertEquals(sInline, sArray, 0);
        }
    }
}
