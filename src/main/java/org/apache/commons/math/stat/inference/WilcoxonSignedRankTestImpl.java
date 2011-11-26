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
package org.apache.commons.math.stat.inference;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.stat.ranking.NaNStrategy;
import org.apache.commons.math.stat.ranking.NaturalRanking;
import org.apache.commons.math.stat.ranking.TiesStrategy;
import org.apache.commons.math.util.FastMath;

/**
 * An implementation of the Wilcoxon signed-rank test.
 *
 * @version $Id$
 */
public class WilcoxonSignedRankTestImpl implements WilcoxonSignedRankTest {

    /** Ranking algorithm. */
    private NaturalRanking naturalRanking;

    /**
     * Create a test instance where NaN's are left in place and ties get
     * the average of applicable ranks. Use this unless you are very sure
     * of what you are doing.
     */
    public WilcoxonSignedRankTestImpl() {
        naturalRanking = new NaturalRanking(NaNStrategy.FIXED,
                TiesStrategy.AVERAGE);
    }

    /**
     * Create a test instance using the given strategies for NaN's and ties.
     * Only use this if you are sure of what you are doing.
     *
     * @param nanStrategy
     *            specifies the strategy that should be used for Double.NaN's
     * @param tiesStrategy
     *            specifies the strategy that should be used for ties
     */
    public WilcoxonSignedRankTestImpl(NaNStrategy nanStrategy,
            TiesStrategy tiesStrategy) {
        naturalRanking = new NaturalRanking(nanStrategy, tiesStrategy);
    }

    /**
     * Ensures that the provided arrays fulfills the assumptions.
     *
     * @param x first sample
     * @param y second sample
     * @throws IllegalArgumentException
     *             if assumptions are not met
     */
    private void ensureDataConformance(final double[] x, final double[] y)
            throws IllegalArgumentException {
        if (x == null) {
            throw new IllegalArgumentException("x must not be null");
        }

        if (y == null) {
            throw new IllegalArgumentException("y must not be null");
        }

        if (x.length != y.length) {
            throw new IllegalArgumentException(
                    "x and y must contain the same number of elements");
        }

        if (x.length == 0) {
            throw new IllegalArgumentException(
                    "x and y must contain at least one element");
        }
    }

    /**
     * Calculates y[i] - x[i] for all i
     *
     * @param x first sample
     * @param y second sample
     * @return z = y - x
     */
    private double[] calculateDifferences(final double[] x, final double[] y) {

        final double[] z = new double[x.length];

        for (int i = 0; i < x.length; ++i) {
            z[i] = y[i] - x[i];
        }

        return z;
    }

    /**
     * Calculates |z[i]| for all i
     *
     * @param z sample
     * @return |z|
     * @throws IllegalArgumentException
     *             if assumptions are not met
     */
    private double[] calculateAbsoluteDifferences(final double[] z)
            throws IllegalArgumentException {
        if (z == null) {
            throw new IllegalArgumentException("z must not be null");
        }

        if (z.length == 0) {
            throw new IllegalArgumentException(
                    "z must contain at least one element");
        }

        final double[] zAbs = new double[z.length];

        for (int i = 0; i < z.length; ++i) {
            zAbs[i] = FastMath.abs(z[i]);
        }

        return zAbs;
    }

    /**
     * {@inheritDoc}
     *
     * @param x
     *            the first sample
     * @param y
     *            the second sample
     * @return wilcoxonSignedRank statistic (the larger of W+ and W-)
     * @throws IllegalArgumentException
     *             if preconditions are not met
     */
    public double wilcoxonSignedRank(final double[] x, final double[] y)
            throws IllegalArgumentException {

        ensureDataConformance(x, y);

        // throws IllegalArgumentException if x and y are not correctly
        // specified
        final double[] z = calculateDifferences(x, y);
        final double[] zAbs = calculateAbsoluteDifferences(z);

        final double[] ranks = naturalRanking.rank(zAbs);

        double Wplus = 0;

        for (int i = 0; i < z.length; ++i) {
            if (z[i] > 0) {
                Wplus += ranks[i];
            }
        }

        final int N = x.length;
        final double Wminus = (((double) (N * (N + 1))) / 2.0) - Wplus;

        return FastMath.max(Wplus, Wminus);
    }

    /**
     * Algorithm inspired by
     * http://www.fon.hum.uva.nl/Service/Statistics/Signed_Rank_Algorihms.html#C
     * by Rob van Son, Institute of Phonetic Sciences & IFOTT,
     * University of Amsterdam
     *
     * @param Wmax largest Wilcoxon signed rank value
     * @param N number of subjects (corresponding to x.length)
     * @return two-sided exact p-value
     */
    private double calculateExactPValue(final double Wmax, final int N) {

        // Total number of outcomes (equal to 2^N but a lot faster)
        final int m = 1 << N;

        int largerRankSums = 0;

        for (int i = 0; i < m; ++i) {
            int rankSum = 0;

            // Generate all possible rank sums
            for (int j = 0; j < N; ++j) {

                // (i >> j) & 1 extract i's j-th bit from the right
                if (((i >> j) & 1) == 1) {
                    rankSum += j + 1;
                }
            }

            if (rankSum >= Wmax) {
                ++largerRankSums;
            }
        }

        /*
         * largerRankSums / m gives the one-sided p-value, so it's multiplied
         * with 2 to get the two-sided p-value
         */
        return 2 * ((double) largerRankSums) / ((double) m);
    }

    /**
     * @param Wmin smallest Wilcoxon signed rank value
     * @param N number of subjects (corresponding to x.length)
     * @return two-sided asymptotic p-value
     * @throws MathException if an error occurs computing the p-value
     */
    private double calculateAsymptoticPValue(final double Wmin, final int N) throws MathException {

        final double ES = (double) (N * (N + 1)) / 4.0;

        /* Same as (but saves computations):
         * final double VarW = ((double) (N * (N + 1) * (2*N + 1))) / 24;
         */
        final double VarS = ES * ((double) (2 * N + 1) / 6.0);

        // - 0.5 is a continuity correction
        final double z = (Wmin - ES - 0.5) / FastMath.sqrt(VarS);

        final NormalDistribution standardNormal = new NormalDistribution(0, 1);

        return 2*standardNormal.cumulativeProbability(z);
    }

    /**
     * {@inheritDoc}
     *
     * @param x
     *            the first sample
     * @param y
     *            the second sample
     * @param exactPValue
     *            if the exact p-value is wanted (only for x.length <= 30)
     * @return p-value
     * @throws IllegalArgumentException
     *             if preconditions are not met or exact p-value is wanted for
     *             when x.length > 30
     * @throws MathException
     *             if an error occurs computing the p-value
     */
    public double wilcoxonSignedRankTest(final double[] x, final double[] y,
            boolean exactPValue) throws IllegalArgumentException,
            MathException {

        ensureDataConformance(x, y);

        final int N = x.length;
        final double Wmax = wilcoxonSignedRank(x, y);

        if (exactPValue && N > 30) {
            throw new IllegalArgumentException("Exact test can only be made for N <= 30.");
        }

        if (exactPValue) {
            return calculateExactPValue(Wmax, N);
        } else {
            final double Wmin = ( (double)(N*(N+1)) / 2.0 ) - Wmax;
            return calculateAsymptoticPValue(Wmin, N);
        }
    }
}
