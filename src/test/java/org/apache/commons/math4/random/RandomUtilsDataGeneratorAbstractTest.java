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
package org.apache.commons.math4.random;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.List;

import org.apache.commons.math4.RetryRunner;
import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.stat.Frequency;
import org.apache.commons.math4.stat.inference.ChiSquareTest;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test cases for the {@link RandomUtils#DataGenerator} class.
 */
@RunWith(RetryRunner.class)
public abstract class RandomUtilsDataGeneratorAbstractTest {
    private final long smallSampleSize = 1000;
    private final double[] expected = { 250, 250, 250, 250 };
    private final int largeSampleSize = 10000;
    private final String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                                   "a", "b", "c", "d", "e", "f" };
    private final ChiSquareTest testStatistic = new ChiSquareTest();
    /** Data generator. */
    private final RandomUtils.DataGenerator randomData;

    /**
     * @param rng RNG.
     */
    protected RandomUtilsDataGeneratorAbstractTest(UniformRandomProvider rng) {
        randomData = RandomUtils.createDataGenerator(rng);
    }

    @Test
    public void testNextLongExtremeValues() {
        long x = randomData.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
        long y = randomData.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
        Assert.assertFalse(x == y);
    }

    @Test
    public void testNextUniformExtremeValues() {
        double x = randomData.nextUniform(-Double.MAX_VALUE, Double.MAX_VALUE);
        double y = randomData.nextUniform(-Double.MAX_VALUE, Double.MAX_VALUE);
        Assert.assertFalse(x == y);
        Assert.assertFalse(Double.isNaN(x));
        Assert.assertFalse(Double.isNaN(y));
        Assert.assertFalse(Double.isInfinite(x));
        Assert.assertFalse(Double.isInfinite(y));
    }

    @Test
    public void testNextLongIAE() {
        try {
            randomData.nextLong(4, 3);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    @Test
    public void testNextLongNegativeToPositiveRange() {
        for (int i = 0; i < 5; i++) {
            checkNextLongUniform(-3, 5);
            checkNextLongUniform(-3, 6);
        }
    }

    @Test
    public void testNextLongNegativeRange() {
        for (int i = 0; i < 5; i++) {
            checkNextLongUniform(-7, -4);
            checkNextLongUniform(-15, -2);
            checkNextLongUniform(Long.MIN_VALUE + 1, Long.MIN_VALUE + 12);
        }
    }

    @Test
    public void testNextLongPositiveRange() {
        for (int i = 0; i < 5; i++) {
            checkNextLongUniform(0, 3);
            checkNextLongUniform(2, 12);
            checkNextLongUniform(Long.MAX_VALUE - 12, Long.MAX_VALUE - 1);
        }
    }

    private void checkNextLongUniform(long min, long max) {
        final Frequency freq = new Frequency();
        for (int i = 0; i < smallSampleSize; i++) {
            final long value = randomData.nextLong(min, max);
            Assert.assertTrue("nextLong range: " + value + " " + min + " " + max,
                              (value >= min) && (value <= max));
            freq.addValue(value);
        }
        final int len = ((int) (max - min)) + 1;
        final long[] observed = new long[len];
        for (int i = 0; i < len; i++) {
            observed[i] = freq.getCount(min + i);
        }
        final double[] expected = new double[len];
        for (int i = 0; i < len; i++) {
            expected[i] = 1d / len;
        }

        TestUtils.assertChiSquareAccept(expected, observed, 0.01);
    }

    @Test
    public void testNextLongWideRange() {
        long lower = -0x6543210FEDCBA987L;
        long upper =  0x456789ABCDEF0123L;
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        for (int i = 0; i < 10000000; ++i) {
            long r = randomData.nextLong(lower, upper);
            max = FastMath.max(max, r);
            min = FastMath.min(min, r);
            Assert.assertTrue(r >= lower);
            Assert.assertTrue(r <= upper);
        }
        double ratio = (((double) max)   - ((double) min)) /
                       (((double) upper) - ((double) lower));
        Assert.assertTrue(ratio > 0.99999);
    }
 
   /** Test dispersion and failure modes for "nextHex". */
    @Test
    public void testNextHexWithoutSha1() {
        checkNextHex(false);
    }
    @Test
    public void testNextHexWithSha1() {
        checkNextHex(true);
    }

    /**
     * @param useSha1 Alternative.
     */
    private void checkNextHex(boolean useSha1) {
        try {
            randomData.nextHexString(-1, useSha1);
            Assert.fail("negative length supplied -- MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            randomData.nextHexString(0, useSha1);
            Assert.fail("zero length supplied -- MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        String hexString = randomData.nextHexString(3, useSha1);
        if (hexString.length() != 3) {
            Assert.fail("incorrect length for generated string");
        }
        hexString = randomData.nextHexString(1, useSha1);
        if (hexString.length() != 1) {
            Assert.fail("incorrect length for generated string");
        }
        try {
            hexString = randomData.nextHexString(0, useSha1);
            Assert.fail("zero length requested -- expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        Frequency f = new Frequency();
        for (int i = 0; i < smallSampleSize; i++) {
            hexString = randomData.nextHexString(100, useSha1);
            if (hexString.length() != 100) {
                Assert.fail("incorrect length for generated string");
            }
            for (int j = 0; j < hexString.length(); j++) {
                f.addValue(hexString.substring(j, j + 1));
            }
        }
        double[] expected = new double[16];
        long[] observed = new long[16];
        for (int i = 0; i < 16; i++) {
            expected[i] = (double) smallSampleSize * 100 / 16;
            observed[i] = f.getCount(hex[i]);
        }
        TestUtils.assertChiSquareAccept(expected, observed, 0.001);
    }

    @Test
    public void testNextUniformIAE() {
        try {
            randomData.nextUniform(4, 3);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            randomData.nextUniform(0, Double.POSITIVE_INFINITY);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            randomData.nextUniform(Double.NEGATIVE_INFINITY, 0);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            randomData.nextUniform(0, Double.NaN);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            randomData.nextUniform(Double.NaN, 0);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    @Test
    public void testNextUniformUniformPositiveBounds() {
        for (int i = 0; i < 5; i++) {
            checkNextUniformUniform(0, 10);
        }
    }

    @Test
    public void testNextUniformUniformNegativeToPositiveBounds() {
        for (int i = 0; i < 5; i++) {
            checkNextUniformUniform(-3, 5);
        }
    }

    @Test
    public void testNextUniformUniformNegaiveBounds() {
        for (int i = 0; i < 5; i++) {
            checkNextUniformUniform(-7, -3);
        }
    }

    @Test
    public void testNextUniformUniformMaximalInterval() {
        for (int i = 0; i < 5; i++) {
            checkNextUniformUniform(-Double.MAX_VALUE, Double.MAX_VALUE);
        }
    }

    private void checkNextUniformUniform(double min, double max) {
        // Set up bin bounds - min, binBound[0], ..., binBound[binCount-2], max
        final int binCount = 5;
        final double binSize = max / binCount - min/binCount; // Prevent overflow in extreme value case
        final double[] binBounds = new double[binCount - 1];
        binBounds[0] = min + binSize;
        for (int i = 1; i < binCount - 1; i++) {
            binBounds[i] = binBounds[i - 1] + binSize;  // + instead of * to avoid overflow in extreme case
        }

        final Frequency freq = new Frequency();
        for (int i = 0; i < smallSampleSize; i++) {
            final double value = randomData.nextUniform(min, max);
            Assert.assertTrue("nextUniform range", (value > min) && (value < max));
            // Find bin
            int j = 0;
            while (j < binCount - 1 && value > binBounds[j]) {
                j++;
            }
            freq.addValue(j);
        }

        final long[] observed = new long[binCount];
        for (int i = 0; i < binCount; i++) {
            observed[i] = freq.getCount(i);
        }
        final double[] expected = new double[binCount];
        for (int i = 0; i < binCount; i++) {
            expected[i] = 1d / binCount;
        }

        TestUtils.assertChiSquareAccept(expected, observed, 0.01);
    }

    /** test exclusive endpoints of nextUniform **/
    @Test
    public void testNextUniformExclusiveEndpoints() {
        for (int i = 0; i < 1000; i++) {
            double u = randomData.nextUniform(0.99, 1);
            Assert.assertTrue(u > 0.99 && u < 1);
        }
    }

    /** Tests for "nextSample" (sampling from Collection). */
    @Test
    public void testNextSample() {
        Object[][] c = { { "0", "1" }, { "0", "2" }, { "0", "3" },
                { "0", "4" }, { "1", "2" }, { "1", "3" }, { "1", "4" },
                { "2", "3" }, { "2", "4" }, { "3", "4" } };
        long[] observed = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        double[] expected = { 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 };

        HashSet<Object> cPop = new HashSet<>(); // {0,1,2,3,4}
        for (int i = 0; i < 5; i++) {
            cPop.add(Integer.toString(i));
        }

        Object[] sets = new Object[10]; // 2-sets from 5
        for (int i = 0; i < 10; i++) {
            HashSet<Object> hs = new HashSet<>();
            hs.add(c[i][0]);
            hs.add(c[i][1]);
            sets[i] = hs;
        }

        for (int i = 0; i < 1000; i++) {
            List<Object> cSamp = randomData.nextSample(cPop, 2);
            observed[findSample(sets, cSamp)]++;
        }

        // Use ChiSquare dist with df = 10-1 = 9, alpha = 0.001
        // Change to 21.67 for alpha = 0.01
        Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times",
                          testStatistic.chiSquare(expected, observed) < 27.88);

        // Make sure sample of size = size of collection returns same collection
        HashSet<Object> hs = new HashSet<>();
        hs.add("one");
        List<Object> one = randomData.nextSample(hs, 1);
        String oneString = (String) one.get(0);
        if (one.size() != 1 ||
            !oneString.equals("one")) {
            Assert.fail("bad sample for set size = 1, sample size = 1");
        }

        // Make sure we fail for sample size > collection size.
        try {
            one = randomData.nextSample(hs, 2);
            Assert.fail("sample size > set size, expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

        // Make sure we fail for empty collection.
        try {
            hs = new HashSet<>();
            one = randomData.nextSample(hs, 0);
            Assert.fail("n = k = 0, expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    @SuppressWarnings("unchecked")
    private int findSample(Object[] u, List<Object> sampList) {
        Object[] samp = sampList.toArray(new Object[sampList.size()]);
        for (int i = 0; i < u.length; i++) {
            HashSet<Object> set = (HashSet<Object>) u[i];
            HashSet<Object> sampSet = new HashSet<>();
            for (int j = 0; j < samp.length; j++) {
                sampSet.add(samp[j]);
            }
            if (set.equals(sampSet)) {
                return i;
            }
        }
        Assert.fail("sample not found:{" + samp[0] + "," + samp[1] + "}");
        return -1;
    }

    /** tests for nextPermutation */
    @Test
    public void testNextPermutation() {
        int[][] p = { { 0, 1, 2 }, { 0, 2, 1 }, { 1, 0, 2 }, { 1, 2, 0 },
                      { 2, 0, 1 }, { 2, 1, 0 } };
        long[] observed = { 0, 0, 0, 0, 0, 0 };
        double[] expected = { 100, 100, 100, 100, 100, 100 };

        for (int i = 0; i < 600; i++) {
            int[] perm = randomData.nextPermutation(3, 3);
            observed[findPerm(p, perm)]++;
        }

        String[] labels = {"{0, 1, 2}", "{ 0, 2, 1 }", "{ 1, 0, 2 }",
                           "{ 1, 2, 0 }", "{ 2, 0, 1 }", "{ 2, 1, 0 }"};
        TestUtils.assertChiSquareAccept(labels, expected, observed, 0.001);

        // Check size = 1 boundary case
        int[] perm = randomData.nextPermutation(1, 1);
        if ((perm.length != 1) || (perm[0] != 0)) {
            Assert.fail("bad permutation for n = 1, sample k = 1");

            // Make sure we fail for k size > n
            try {
                perm = randomData.nextPermutation(2, 3);
                Assert.fail("permutation k > n, expecting MathIllegalArgumentException");
            } catch (MathIllegalArgumentException ex) {
                // ignored
            }

            // Make sure we fail for n = 0
            try {
                perm = randomData.nextPermutation(0, 0);
                Assert.fail("permutation k = n = 0, expecting MathIllegalArgumentException");
            } catch (MathIllegalArgumentException ex) {
                // ignored
            }

            // Make sure we fail for k < n < 0
            try {
                perm = randomData.nextPermutation(-1, -3);
                Assert.fail("permutation k < n < 0, expecting MathIllegalArgumentException");
            } catch (MathIllegalArgumentException ex) {
                // ignored
            }

        }
    }

    private int findPerm(int[][] p, int[] samp) {
        for (int i = 0; i < p.length; i++) {
            boolean good = true;
            for (int j = 0; j < samp.length; j++) {
                if (samp[j] != p[i][j]) {
                    good = false;
                }
            }
            if (good) {
                return i;
            }
        }
        Assert.fail("permutation not found");
        return -1;
    }
}
