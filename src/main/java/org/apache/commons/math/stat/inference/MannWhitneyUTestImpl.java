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
 * An implementation of the Mann-Whitney U test (also called Wilcoxon rank-sum
 * test).
 *
 * @version $Id$
 */
public class MannWhitneyUTestImpl implements MannWhitneyUTest {

    /** Ranking algorithm. */
    private NaturalRanking naturalRanking;

    /**
     * Create a test instance using where NaN's are left in place and ties get
     * the average of applicable ranks. Use this unless you are very sure of
     * what you are doing.
     */
    public MannWhitneyUTestImpl() {
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
    public MannWhitneyUTestImpl(NaNStrategy nanStrategy,
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

        if (x.length == 0) {
            throw new IllegalArgumentException(
                    "x must contain at least one element");
        }

        if (y.length == 0) {
            throw new IllegalArgumentException(
                    "y must contain at least one element");
        }
    }

    /** Concatenate the samples into one array.
     * @param x first sample
     * @param y second sample
     * @return concatenated array
     */
    private double[] concatenateSamples(final double[] x, final double[] y) {
        final double[] z = new double[x.length + y.length];

        System.arraycopy(x, 0, z, 0, x.length);
        System.arraycopy(y, 0, z, x.length, y.length);

        return z;
    }

    /**
     * {@inheritDoc}
     *
     * @param x the first sample
     * @param y the second sample
     * @return mannWhitneyU statistic U (maximum of U<sup>x</sup> and U<sup>y</sup>)
     * @throws IllegalArgumentException if preconditions are not met
     */
    public double mannWhitneyU(final double[] x, final double[] y)
            throws IllegalArgumentException {

        ensureDataConformance(x, y);

        final double[] z = concatenateSamples(x, y);
        final double[] ranks = naturalRanking.rank(z);

        double sumRankX = 0;

        /*
         * The ranks for x is in the first x.length entries in ranks because x
         * is in the first x.length entries in z
         */
        for (int i = 0; i < x.length; ++i) {
            sumRankX += ranks[i];
        }

        /*
         * U1 = R1 - (n1 * (n1 + 1)) / 2 where R1 is sum of ranks for sample 1,
         * e.g. x, n1 is the number of observations in sample 1.
         */
        final double U1 = sumRankX - (x.length * (x.length + 1)) / 2;

        /*
         * It can be shown that U1 + U2 = n1 * n2
         */
        final double U2 = x.length * y.length - U1;

        return FastMath.max(U1, U2);
    }

    /**
     * @param Umin smallest Mann-Whitney U value
     * @param n1 number of subjects in first sample
     * @param n2 number of subjects in second sample
     * @return two-sided asymptotic p-value
     * @throws MathException if an error occurs computing the p-value
     */
    private double calculateAsymptoticPValue(final double Umin, final int n1,
            final int n2) throws MathException {

        final int n1n2prod = n1 * n2;

        // http://en.wikipedia.org/wiki/Mann%E2%80%93Whitney_U#Normal_approximation
        final double EU = (double) n1n2prod / 2.0;
        final double VarU = (double) (n1n2prod * (n1 + n2 + 1)) / 12.0;

        final double z = (Umin - EU) / FastMath.sqrt(VarU);

        final NormalDistribution standardNormal = new NormalDistribution(
                0, 1);

        return 2 * standardNormal.cumulativeProbability(z);
    }

    /**
     * Ties give rise to biased variance at the moment. See e.g. <a
     * href="http://mlsc.lboro.ac.uk/resources/statistics/Mannwhitney.pdf"
     * >http://mlsc.lboro.ac.uk/resources/statistics/Mannwhitney.pdf</a>.
     *
     * {@inheritDoc}
     *
     * @param x the first sample
     * @param y the second sample
     * @return asymptotic p-value (biased for samples with ties)
     * @throws IllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs computing the p-value
     */
    public double mannWhitneyUTest(final double[] x, final double[] y)
            throws IllegalArgumentException, MathException {

        ensureDataConformance(x, y);

        final double Umax = mannWhitneyU(x, y);

        /*
         * It can be shown that U1 + U2 = n1 * n2
         */
        final double Umin = x.length * y.length - Umax;

        return calculateAsymptoticPValue(Umin, x.length, y.length);
    }
}
