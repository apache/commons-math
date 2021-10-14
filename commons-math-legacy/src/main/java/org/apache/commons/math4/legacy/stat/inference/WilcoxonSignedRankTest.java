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
package org.apache.commons.math4.legacy.stat.inference;

import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.math4.legacy.exception.ConvergenceException;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NoDataException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.stat.ranking.NaNStrategy;
import org.apache.commons.math4.legacy.stat.ranking.NaturalRanking;
import org.apache.commons.math4.legacy.stat.ranking.TiesStrategy;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * An implementation of the Wilcoxon signed-rank test.
 *
 */
public class WilcoxonSignedRankTest {

    /** Ranking algorithm. */
    private NaturalRanking naturalRanking;

    /**
     * Create a test instance where NaN's are left in place and ties get
     * the average of applicable ranks. Use this unless you are very sure
     * of what you are doing.
     */
    public WilcoxonSignedRankTest() {
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
    public WilcoxonSignedRankTest(final NaNStrategy nanStrategy,
                                  final TiesStrategy tiesStrategy) {
        naturalRanking = new NaturalRanking(nanStrategy, tiesStrategy);
    }

    /**
     * Ensures that the provided arrays fulfills the assumptions.
     *
     * @param x first sample
     * @param y second sample
     * @throws NullArgumentException if {@code x} or {@code y} are {@code null}.
     * @throws NoDataException if {@code x} or {@code y} are zero-length.
     * @throws DimensionMismatchException if {@code x} and {@code y} do not
     * have the same length.
     */
    private void ensureDataConformance(final double[] x, final double[] y)
        throws NullArgumentException, NoDataException, DimensionMismatchException {

        if (x == null ||
            y == null) {
                throw new NullArgumentException();
        }
        if (x.length == 0 ||
            y.length == 0) {
            throw new NoDataException();
        }
        if (y.length != x.length) {
            throw new DimensionMismatchException(y.length, x.length);
        }
    }

    /**
     * Calculates y[i] - x[i] for all i.
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
     * Calculates |z[i]| for all i.
     *
     * @param z sample
     * @return |z|
     * @throws NullArgumentException if {@code z} is {@code null}
     * @throws NoDataException if {@code z} is zero-length.
     */
    private double[] calculateAbsoluteDifferences(final double[] z)
        throws NullArgumentException, NoDataException {

        if (z == null) {
            throw new NullArgumentException();
        }

        if (z.length == 0) {
            throw new NoDataException();
        }

        final double[] zAbs = new double[z.length];

        for (int i = 0; i < z.length; ++i) {
            zAbs[i] = JdkMath.abs(z[i]);
        }

        return zAbs;
    }

    /**
     * Computes the <a
     * href="http://en.wikipedia.org/wiki/Wilcoxon_signed-rank_test">
     * Wilcoxon signed ranked statistic</a> comparing mean for two related
     * samples or repeated measurements on a single sample.
     * <p>
     * This statistic can be used to perform a Wilcoxon signed ranked test
     * evaluating the null hypothesis that the two related samples or repeated
     * measurements on a single sample has equal mean.
     * </p>
     * <p>
     * Let X<sub>i</sub> denote the i'th individual of the first sample and
     * Y<sub>i</sub> the related i'th individual in the second sample. Let
     * Z<sub>i</sub> = Y<sub>i</sub> - X<sub>i</sub>.
     * </p>
     * <p>
     * <strong>Preconditions</strong>:
     * <ul>
     * <li>The differences Z<sub>i</sub> must be independent.</li>
     * <li>Each Z<sub>i</sub> comes from a continuous population (they must be
     * identical) and is symmetric about a common median.</li>
     * <li>The values that X<sub>i</sub> and Y<sub>i</sub> represent are
     * ordered, so the comparisons greater than, less than, and equal to are
     * meaningful.</li>
     * </ul>
     *
     * @param x the first sample
     * @param y the second sample
     * @return wilcoxonSignedRank statistic (the larger of W+ and W-)
     * @throws NullArgumentException if {@code x} or {@code y} are {@code null}.
     * @throws NoDataException if {@code x} or {@code y} are zero-length.
     * @throws DimensionMismatchException if {@code x} and {@code y} do not
     * have the same length.
     */
    public double wilcoxonSignedRank(final double[] x, final double[] y)
        throws NullArgumentException, NoDataException, DimensionMismatchException {

        ensureDataConformance(x, y);

        // throws IllegalArgumentException if x and y are not correctly
        // specified
        final double[] z = calculateDifferences(x, y);
        final double[] zAbs = calculateAbsoluteDifferences(z);

        final double[] ranks = naturalRanking.rank(zAbs);

        double wPlus = 0;

        for (int i = 0; i < z.length; ++i) {
            if (z[i] > 0) {
                wPlus += ranks[i];
            }
        }

        final int n = x.length;
        final double wMinus = (((double) (n * (n + 1))) / 2.0) - wPlus;

        return JdkMath.max(wPlus, wMinus);
    }

    /**
     * Algorithm inspired by.
     * http://www.fon.hum.uva.nl/Service/Statistics/Signed_Rank_Algorihms.html#C
     * by Rob van Son, Institute of Phonetic Sciences & IFOTT,
     * University of Amsterdam
     *
     * @param wMax largest Wilcoxon signed rank value
     * @param n number of subjects (corresponding to x.length)
     * @return two-sided exact p-value
     */
    private double calculateExactPValue(final double wMax, final int n) {

        // Total number of outcomes (equal to 2^N but a lot faster)
        final int m = 1 << n;

        int largerRankSums = 0;

        for (int i = 0; i < m; ++i) {
            int rankSum = 0;

            // Generate all possible rank sums
            for (int j = 0; j < n; ++j) {

                // (i >> j) & 1 extract i's j-th bit from the right
                if (((i >> j) & 1) == 1) {
                    rankSum += j + 1;
                }
            }

            if (rankSum >= wMax) {
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
     * @param wMin smallest Wilcoxon signed rank value
     * @param n number of subjects (corresponding to x.length)
     * @return two-sided asymptotic p-value
     */
    private double calculateAsymptoticPValue(final double wMin, final int n) {

        final double es = (double) (n * (n + 1)) / 4.0;

        /* Same as (but saves computations):
         * final double VarW = ((double) (N * (N + 1) * (2*N + 1))) / 24;
         */
        final double varS = es * ((double) (2 * n + 1) / 6.0);

        // - 0.5 is a continuity correction
        final double z = (wMin - es - 0.5) / JdkMath.sqrt(varS);

        // No try-catch or advertised exception because args are valid
        // pass a null rng to avoid unneeded overhead as we will not sample from this distribution
        final NormalDistribution standardNormal = NormalDistribution.of(0, 1);

        return 2*standardNormal.cumulativeProbability(z);
    }

    /**
     * Returns the <i>observed significance level</i>, or <a href=
     * "http://www.cas.lancs.ac.uk/glossary_v1.1/hyptest.html#pvalue">
     * p-value</a>, associated with a <a
     * href="http://en.wikipedia.org/wiki/Wilcoxon_signed-rank_test">
     * Wilcoxon signed ranked statistic</a> comparing mean for two related
     * samples or repeated measurements on a single sample.
     * <p>
     * Let X<sub>i</sub> denote the i'th individual of the first sample and
     * Y<sub>i</sub> the related i'th individual in the second sample. Let
     * Z<sub>i</sub> = Y<sub>i</sub> - X<sub>i</sub>.
     * </p>
     * <p>
     * <strong>Preconditions</strong>:
     * <ul>
     * <li>The differences Z<sub>i</sub> must be independent.</li>
     * <li>Each Z<sub>i</sub> comes from a continuous population (they must be
     * identical) and is symmetric about a common median.</li>
     * <li>The values that X<sub>i</sub> and Y<sub>i</sub> represent are
     * ordered, so the comparisons greater than, less than, and equal to are
     * meaningful.</li>
     * </ul>
     *
     * @param x the first sample
     * @param y the second sample
     * @param exactPValue
     *            if the exact p-value is wanted (only works for x.length &gt;= 30,
     *            if true and x.length &lt; 30, this is ignored because
     *            calculations may take too long)
     * @return p-value
     * @throws NullArgumentException if {@code x} or {@code y} are {@code null}.
     * @throws NoDataException if {@code x} or {@code y} are zero-length.
     * @throws DimensionMismatchException if {@code x} and {@code y} do not
     * have the same length.
     * @throws NumberIsTooLargeException if {@code exactPValue} is {@code true}
     * and {@code x.length} &gt; 30
     * @throws ConvergenceException if the p-value can not be computed due to
     * a convergence error
     * @throws MaxCountExceededException if the maximum number of iterations
     * is exceeded
     */
    public double wilcoxonSignedRankTest(final double[] x, final double[] y,
                                         final boolean exactPValue)
        throws NullArgumentException, NoDataException, DimensionMismatchException,
        NumberIsTooLargeException, ConvergenceException, MaxCountExceededException {

        ensureDataConformance(x, y);

        final int n = x.length;
        final double wMax = wilcoxonSignedRank(x, y);

        if (exactPValue && n > 30) {
            throw new NumberIsTooLargeException(n, 30, true);
        }

        if (exactPValue) {
            return calculateExactPValue(wMax, n);
        } else {
            final double wMin = ( (double)(n*(n+1)) / 2.0 ) - wMax;
            return calculateAsymptoticPValue(wMin, n);
        }
    }
}
