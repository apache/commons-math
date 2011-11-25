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
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;

/**
 * Implements Chi-Square test statistics defined in the
 * {@link UnknownDistributionChiSquareTest} interface.
 *
 * @version $Id$
 */
public class ChiSquareTestImpl implements UnknownDistributionChiSquareTest {

    /**
     * Construct a ChiSquareTestImpl
     */
    public ChiSquareTestImpl() {
        super();
    }

     /**
     * {@inheritDoc}
     * <p><strong>Note: </strong>This implementation rescales the
     * <code>expected</code> array if necessary to ensure that the sum of the
     * expected and observed counts are equal.</p>
     *
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @return chi-square test statistic
     * @throws DimensionMismatchException if the arrays length is less than 2.
     */
    public double chiSquare(double[] expected, long[] observed) {
        if (expected.length < 2) {
            throw new DimensionMismatchException(expected.length, 2);
        }
        if (expected.length != observed.length) {
            throw new DimensionMismatchException(expected.length, observed.length);
        }
        checkPositive(expected);
        checkNonNegative(observed);
        double sumExpected = 0d;
        double sumObserved = 0d;
        for (int i = 0; i < observed.length; i++) {
            sumExpected += expected[i];
            sumObserved += observed[i];
        }
        double ratio = 1.0d;
        boolean rescale = false;
        if (FastMath.abs(sumExpected - sumObserved) > 10E-6) {
            ratio = sumObserved / sumExpected;
            rescale = true;
        }
        double sumSq = 0.0d;
        for (int i = 0; i < observed.length; i++) {
            if (rescale) {
                final double dev = observed[i] - ratio * expected[i];
                sumSq += dev * dev / (ratio * expected[i]);
            } else {
                final double dev = observed[i] - expected[i];
                sumSq += dev * dev / expected[i];
            }
        }
        return sumSq;
    }

    /**
     * {@inheritDoc}
     * <p><strong>Note: </strong>This implementation rescales the
     * <code>expected</code> array if necessary to ensure that the sum of the
     * expected and observed counts are equal.</p>
     *
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @return p-value
     * @throws MathIllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs computing the p-value
     */
    public double chiSquareTest(double[] expected, long[] observed)
        throws MathException {
        ChiSquaredDistribution distribution =
            new ChiSquaredDistribution(expected.length - 1.0);
        return 1.0 - distribution.cumulativeProbability(
            chiSquare(expected, observed));
    }

    /**
     * {@inheritDoc}
     * <p><strong>Note: </strong>This implementation rescales the
     * <code>expected</code> array if necessary to ensure that the sum of the
     * expected and observed counts are equal.</p>
     *
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @throws MathIllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs performing the test
     */
    public boolean chiSquareTest(double[] expected, long[] observed,
                                 double alpha)
        throws MathException {
        if ((alpha <= 0) || (alpha > 0.5)) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL,
                                          alpha, 0, 0.5);
        }
        return chiSquareTest(expected, observed) < alpha;
    }

    /**
     * @param counts array representation of 2-way table
     * @return chi-square test statistic
     * @throws MathIllegalArgumentException if preconditions are not met.
     */
    public double chiSquare(long[][] counts) {
        checkArray(counts);
        int nRows = counts.length;
        int nCols = counts[0].length;

        // compute row, column and total sums
        double[] rowSum = new double[nRows];
        double[] colSum = new double[nCols];
        double total = 0.0d;
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                rowSum[row] += counts[row][col];
                colSum[col] += counts[row][col];
                total += counts[row][col];
            }
        }

        // compute expected counts and chi-square
        double sumSq = 0.0d;
        double expected = 0.0d;
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                expected = (rowSum[row] * colSum[col]) / total;
                sumSq += ((counts[row][col] - expected) *
                        (counts[row][col] - expected)) / expected;
            }
        }
        return sumSq;
    }

    /**
     * @param counts array representation of 2-way table
     * @return p-value
     * @throws MathIllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs computing the p-value
     */
    public double chiSquareTest(long[][] counts)
    throws MathException {
        checkArray(counts);
        double df = ((double) counts.length -1) * ((double) counts[0].length - 1);
        ChiSquaredDistribution distribution;
        distribution = new ChiSquaredDistribution(df);
        return 1 - distribution.cumulativeProbability(chiSquare(counts));
    }

    /**
     * @param counts array representation of 2-way table
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @throws MathIllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs performing the test
     */
    public boolean chiSquareTest(long[][] counts, double alpha)
    throws MathException {
        if ((alpha <= 0) || (alpha > 0.5)) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL,
                                          alpha, 0, 0.5);
        }
        return chiSquareTest(counts) < alpha;
    }

    /**
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data set
     * @return chi-square test statistic
     * @throws MathIllegalArgumentException if preconditions are not met
     * @since 1.2
     */
    public double chiSquareDataSetsComparison(long[] observed1, long[] observed2) {
        // Make sure lengths are same
        if (observed1.length < 2) {
            throw new DimensionMismatchException(observed1.length, 2);
        }
        if (observed1.length != observed2.length) {
            throw new DimensionMismatchException(observed1.length, observed2.length);
        }

        // Ensure non-negative counts
        checkNonNegative(observed1);
        checkNonNegative(observed2);

        // Compute and compare count sums
        long countSum1 = 0;
        long countSum2 = 0;
        boolean unequalCounts = false;
        double weight = 0.0;
        for (int i = 0; i < observed1.length; i++) {
            countSum1 += observed1[i];
            countSum2 += observed2[i];
        }
        // Ensure neither sample is uniformly 0
        if (countSum1 == 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.OBSERVED_COUNTS_ALL_ZERO, 1);
        }
        if (countSum2 == 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.OBSERVED_COUNTS_ALL_ZERO, 2);
        }
        // Compare and compute weight only if different
        unequalCounts = countSum1 != countSum2;
        if (unequalCounts) {
            weight = FastMath.sqrt((double) countSum1 / (double) countSum2);
        }
        // Compute ChiSquare statistic
        double sumSq = 0.0d;
        double dev = 0.0d;
        double obs1 = 0.0d;
        double obs2 = 0.0d;
        for (int i = 0; i < observed1.length; i++) {
            if (observed1[i] == 0 && observed2[i] == 0) {
                throw new MathIllegalArgumentException(LocalizedFormats.OBSERVED_COUNTS_BOTTH_ZERO_FOR_ENTRY, i);
            } else {
                obs1 = observed1[i];
                obs2 = observed2[i];
                if (unequalCounts) { // apply weights
                    dev = obs1/weight - obs2 * weight;
                } else {
                    dev = obs1 - obs2;
                }
                sumSq += (dev * dev) / (obs1 + obs2);
            }
        }
        return sumSq;
    }

    /**
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data set
     * @return p-value
     * @throws MathIllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs computing the p-value
     * @since 1.2
     */
    public double chiSquareTestDataSetsComparison(long[] observed1, long[] observed2)
        throws MathException {
        ChiSquaredDistribution distribution;
        distribution = new ChiSquaredDistribution((double) observed1.length - 1);
        return 1 - distribution.cumulativeProbability(
                chiSquareDataSetsComparison(observed1, observed2));
    }

    /**
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data set
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @throws MathIllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs performing the test
     * @since 1.2
     */
    public boolean chiSquareTestDataSetsComparison(long[] observed1, long[] observed2,
                                                   double alpha)
        throws MathException {
        if (alpha <= 0 ||
            alpha > 0.5) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL,
                                          alpha, 0, 0.5);
        }
        return chiSquareTestDataSetsComparison(observed1, observed2) < alpha;
    }

    /**
     * Checks to make sure that the input long[][] array is rectangular,
     * has at least 2 rows and 2 columns, and has all non-negative entries,
     * throwing MathIllegalArgumentException if any of these checks fail.
     *
     * @param in input 2-way table to check
     * @throws MathIllegalArgumentException if the array is not valid
     */
    private void checkArray(long[][] in) {
        if (in.length < 2) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.INSUFFICIENT_DIMENSION, in.length, 2);
        }

        if (in[0].length < 2) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.INSUFFICIENT_DIMENSION, in[0].length, 2);
        }

        checkRectangular(in);
        checkNonNegative(in);

    }

    //---------------------  Private array methods -- should find a utility home for these

    /**
     * Throws MathIllegalArgumentException if the input array is not rectangular.
     *
     * @param in array to be tested
     * @throws NullArgumentException if input array is null
     * @throws MathIllegalArgumentException if input array is not rectangular
     */
    private void checkRectangular(long[][] in)
        throws NullArgumentException {
        MathUtils.checkNotNull(in);
        for (int i = 1; i < in.length; i++) {
            if (in[i].length != in[0].length) {
                throw new DimensionMismatchException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS,
                                                     in[i].length, in[0].length);
            }
        }
    }

    /**
     * Check all entries of the input array are strictly positive.
     *
     * @param in Array to be tested.
     * @exception MathIllegalArgumentException if one entry is not positive.
     */
    private void checkPositive(double[] in) {
        for (int i = 0; i < in.length; i++) {
            if (in[i] <= 0) {
                throw new MathIllegalArgumentException(
                        LocalizedFormats.NOT_POSITIVE_ELEMENT_AT_INDEX,
                        i, in[i]);
            }
        }
    }

    /**
     * Check all entries of the input array are >= 0.
     *
     * @param in Array to be tested.
     * @exception MathIllegalArgumentException if one entry is negative.
     */
    private void checkNonNegative(long[] in) {
        for (int i = 0; i < in.length; i++) {
            if (in[i] < 0) {
                throw new MathIllegalArgumentException(
                        LocalizedFormats.NEGATIVE_ELEMENT_AT_INDEX,
                        i, in[i]);
            }
        }
    }

    /**
     * Check all entries of the input array are >= 0.
     *
     * @param in Array to be tested.
     * @exception MathIllegalArgumentException if one entry is negative.
     */
    private void checkNonNegative(long[][] in) {
        for (int i = 0; i < in.length; i ++) {
            for (int j = 0; j < in[i].length; j++) {
                if (in[i][j] < 0) {
                    throw new MathIllegalArgumentException(
                            LocalizedFormats.NEGATIVE_ELEMENT_AT_2D_INDEX,
                            i, j, in[i][j]);
                }
            }
        }
    }
}
