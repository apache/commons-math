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
package org.apache.commons.math4.legacy.random;

import org.apache.commons.math4.legacy.RetryRunner;
import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.stat.Frequency;
import org.apache.commons.math4.legacy.core.jdkmath.AccurateMath;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test cases for the {@link RandomUtils.DataGenerator} class.
 */
@RunWith(RetryRunner.class)
public abstract class RandomUtilsDataGeneratorAbstractTest {
    private final long smallSampleSize = 1000;
    private final String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                                   "a", "b", "c", "d", "e", "f" };
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
        final Frequency<Long> freq = new Frequency<>();
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
            max = AccurateMath.max(max, r);
            min = AccurateMath.min(min, r);
            Assert.assertTrue(r >= lower);
            Assert.assertTrue(r <= upper);
        }
        double ratio = (((double) max)   - ((double) min)) /
                       (((double) upper) - ((double) lower));
        Assert.assertTrue(ratio > 0.99999);
    }
}
