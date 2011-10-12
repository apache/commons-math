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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.math.TestUtils;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.NotFiniteNumberException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomDataImpl;
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
            result = ArithmeticsUtils.addAndCheck(binomialCoefficient(n - 1, k - 1),
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

    /** Verify that b(0,0) = 1 */
    @Test
    public void test0Choose0() {
        Assert.assertEquals(MathUtils.binomialCoefficientDouble(0, 0), 1d, 0);
        Assert.assertEquals(MathUtils.binomialCoefficientLog(0, 0), 0d, 0);
        Assert.assertEquals(MathUtils.binomialCoefficient(0, 0), 1);
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
}
