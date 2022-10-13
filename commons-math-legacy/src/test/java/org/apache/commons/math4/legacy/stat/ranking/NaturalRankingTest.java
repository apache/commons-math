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
package org.apache.commons.math4.legacy.stat.ranking;

import org.junit.Assert;
import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.exception.NotANumberException;
import org.junit.Test;
import org.junit.Ignore;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;


/**
 * Test cases for NaturalRanking class
 *
 * @since 2.0
 */
public class NaturalRankingTest {

    private final double[] exampleData = { 20, 17, 30, 42.3, 17, 50,
            Double.NaN, Double.NEGATIVE_INFINITY, 17 };
    private final double[] tiesFirst = { 0, 0, 2, 1, 4 };
    private final double[] tiesLast = { 4, 4, 1, 0 };
    private final double[] multipleNaNs = { 0, 1, Double.NaN, Double.NaN };
    private final double[] multipleTies = { 3, 2, 5, 5, 6, 6, 1 };
    private final double[] allSame = { 0, 0, 0, 0 };

    @Test
    public void testDefault() { // Ties averaged, NaNs failed
        NaturalRanking ranking = new NaturalRanking();
        double[] ranks;

        try {
            ranks = ranking.rank(exampleData);
            Assert.fail("expected NotANumberException due to NaNStrategy.FAILED");
        } catch (NotANumberException e) {
            // expected
        }

        ranks = ranking.rank(tiesFirst);
        double[] correctRanks = new double[] { 1.5, 1.5, 4, 3, 5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesLast);
        correctRanks = new double[] { 3.5, 3.5, 2, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);

        try {
            ranks = ranking.rank(multipleNaNs);
            Assert.fail("expected NotANumberException due to NaNStrategy.FAILED");
        } catch (NotANumberException e) {
            // expected
        }

        ranks = ranking.rank(multipleTies);
        correctRanks = new double[] { 3, 2, 4.5, 4.5, 6.5, 6.5, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(allSame);
        correctRanks = new double[] { 2.5, 2.5, 2.5, 2.5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
    }

    @Test
    public void testNaNsMaximalTiesMinimum() {
        NaturalRanking ranking = new NaturalRanking(NaNStrategy.MAXIMAL, TiesStrategy.MINIMUM);
        double[] ranks = ranking.rank(exampleData);
        double[] correctRanks = { 5, 2, 6, 7, 2, 8, 9, 1, 2 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesFirst);
        correctRanks = new double[] { 1, 1, 4, 3, 5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesLast);
        correctRanks = new double[] { 3, 3, 2, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(multipleNaNs);
        correctRanks = new double[] { 1, 2, 3, 3 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(multipleTies);
        correctRanks = new double[] { 3, 2, 4, 4, 6, 6, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(allSame);
        correctRanks = new double[] { 1, 1, 1, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
    }

    @Test
    public void testNaNsRemovedTiesSequential() {
        NaturalRanking ranking = new NaturalRanking(NaNStrategy.REMOVED,
                TiesStrategy.SEQUENTIAL);
        double[] ranks = ranking.rank(exampleData);
        double[] correctRanks = { 5, 2, 6, 7, 3, 8, 1, 4 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesFirst);
        correctRanks = new double[] { 1, 2, 4, 3, 5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesLast);
        correctRanks = new double[] { 3, 4, 2, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(multipleNaNs);
        correctRanks = new double[] { 1, 2 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(multipleTies);
        correctRanks = new double[] { 3, 2, 4, 5, 6, 7, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(allSame);
        correctRanks = new double[] { 1, 2, 3, 4 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
    }

    @Test
    public void testNaNsMinimalTiesMaximum() {
        NaturalRanking ranking = new NaturalRanking(NaNStrategy.MINIMAL,
                TiesStrategy.MAXIMUM);
        double[] ranks = ranking.rank(exampleData);
        double[] correctRanks = { 6, 5, 7, 8, 5, 9, 2, 2, 5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesFirst);
        correctRanks = new double[] { 2, 2, 4, 3, 5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesLast);
        correctRanks = new double[] { 4, 4, 2, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(multipleNaNs);
        correctRanks = new double[] { 3, 4, 2, 2 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(multipleTies);
        correctRanks = new double[] { 3, 2, 5, 5, 7, 7, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(allSame);
        correctRanks = new double[] { 4, 4, 4, 4 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
    }

    @Test
    public void testNaNsMinimalTiesAverage() {
        NaturalRanking ranking = new NaturalRanking(NaNStrategy.MINIMAL);
        double[] ranks = ranking.rank(exampleData);
        double[] correctRanks = { 6, 4, 7, 8, 4, 9, 1.5, 1.5, 4 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesFirst);
        correctRanks = new double[] { 1.5, 1.5, 4, 3, 5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesLast);
        correctRanks = new double[] { 3.5, 3.5, 2, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(multipleNaNs);
        correctRanks = new double[] { 3, 4, 1.5, 1.5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(multipleTies);
        correctRanks = new double[] { 3, 2, 4.5, 4.5, 6.5, 6.5, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(allSame);
        correctRanks = new double[] { 2.5, 2.5, 2.5, 2.5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
    }

    /*
     * Cf. MATH-1361
     * XXX To be removed when issue is fixed.
     */
    @Ignore
    @Test
    public void testNaNsFixedTiesRandomDEBUG() {
        int count = 0;
        final long start = 0;
        final int num = 10000000;
        final long max = start + num;
        for (long i = start; i <= max; i++) {
            UniformRandomProvider randomGenerator = RandomSource.MT.create(i);
            NaturalRanking ranking = new NaturalRanking(NaNStrategy.FIXED,
                                                        randomGenerator);
            double[] ranks = ranking.rank(exampleData);
            double[] correctRanks = { 5, 3, 6, 7, 3, 8, Double.NaN, 1, 2 };
            if (!org.apache.commons.math4.legacy.core.MathArrays.equalsIncludingNaN(correctRanks, ranks)) {
                continue;
            }

            ranks = ranking.rank(tiesFirst);
            correctRanks = new double[] { 1, 2, 4, 3, 5 };
            if (!org.apache.commons.math4.legacy.core.MathArrays.equalsIncludingNaN(correctRanks, ranks)) {
                continue;
            }

            ranks = ranking.rank(tiesLast);
            correctRanks = new double[] { 3, 3, 2, 1 };
            if (!org.apache.commons.math4.legacy.core.MathArrays.equalsIncludingNaN(correctRanks, ranks)) {
                continue;
            }

            ranks = ranking.rank(multipleNaNs);
            correctRanks = new double[] { 1, 2, Double.NaN, Double.NaN };
            if (!org.apache.commons.math4.legacy.core.MathArrays.equalsIncludingNaN(correctRanks, ranks)) {
                continue;
            }

            ranks = ranking.rank(multipleTies);
            correctRanks = new double[] { 3, 2, 4, 4, 6, 7, 1 };
            if (!org.apache.commons.math4.legacy.core.MathArrays.equalsIncludingNaN(correctRanks, ranks)) {
                continue;
            }

            ranks = ranking.rank(allSame);
            correctRanks = new double[] { 2, 3, 3, 3 };
            if (!org.apache.commons.math4.legacy.core.MathArrays.equalsIncludingNaN(correctRanks, ranks)) {
                continue;
            }

            ++count;
            //System.out.println("seed = " + i);
            //break;
        }
        System.out.println("success rate = " + count + " / " + num);
    }

    @Ignore
    @Test
    public void testNaNsFixedTiesRandom() {
        UniformRandomProvider randomGenerator = RandomSource.SPLIT_MIX_64.create();
        NaturalRanking ranking = new NaturalRanking(NaNStrategy.FIXED, randomGenerator);
        double[] ranks = ranking.rank(exampleData);
        double[] correctRanks = { 5, 3, 6, 7, 3, 8, Double.NaN, 1, 2 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesFirst);
        correctRanks = new double[] { 1, 2, 4, 3, 5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(tiesLast);
        correctRanks = new double[] { 3, 3, 2, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(multipleNaNs);
        correctRanks = new double[] { 1, 2, Double.NaN, Double.NaN };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(multipleTies);
        correctRanks = new double[] { 3, 2, 4, 4, 6, 7, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranks = ranking.rank(allSame);
        correctRanks = new double[] { 2, 3, 3, 3 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
    }

    @Test
    public void testNaNsAndInfs() {
        double[] data = { 0, Double.POSITIVE_INFINITY, Double.NaN,
                Double.NEGATIVE_INFINITY };
        NaturalRanking ranking = new NaturalRanking(NaNStrategy.MAXIMAL);
        double[] ranks = ranking.rank(data);
        double[] correctRanks = new double[] { 2, 3.5, 3.5, 1 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
        ranking = new NaturalRanking(NaNStrategy.MINIMAL);
        ranks = ranking.rank(data);
        correctRanks = new double[] { 3, 4, 1.5, 1.5 };
        TestUtils.assertEquals(correctRanks, ranks, 0d);
    }

    @Test(expected=NotANumberException.class)
    public void testNaNsFailed() {
        double[] data = { 0, Double.POSITIVE_INFINITY, Double.NaN, Double.NEGATIVE_INFINITY };
        NaturalRanking ranking = new NaturalRanking(NaNStrategy.FAILED);
        ranking.rank(data);
    }

    @Test
    public void testNoNaNsFailed() {
        double[] data = { 1, 2, 3, 4 };
        NaturalRanking ranking = new NaturalRanking(NaNStrategy.FAILED);
        double[] ranks = ranking.rank(data);
        TestUtils.assertEquals(data, ranks, 0d);
    }
}
