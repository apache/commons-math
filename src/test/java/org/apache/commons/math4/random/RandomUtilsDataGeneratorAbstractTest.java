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
    public void testNextUniformPositiveBounds() {
        for (int i = 0; i < 5; i++) {
            checkNextUniform(0, 10);
        }
    }

    @Test
    public void testNextUniformNegativeToPositiveBounds() {
        for (int i = 0; i < 5; i++) {
            checkNextUniform(-3, 5);
        }
    }

    @Test
    public void testNextUniformNegativeBounds() {
        for (int i = 0; i < 5; i++) {
            checkNextUniform(-7, -3);
        }
    }

    @Test
    public void testNextUniformMaximalInterval() {
        for (int i = 0; i < 5; i++) {
            checkNextUniform(-Double.MAX_VALUE, Double.MAX_VALUE);
        }
    }

    private void checkNextUniform(double min, double max) {
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
}
