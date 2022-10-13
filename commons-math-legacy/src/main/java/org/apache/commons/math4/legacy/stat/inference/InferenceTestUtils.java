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

import java.util.Collection;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.math4.legacy.exception.ConvergenceException;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.InsufficientDataException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NoDataException;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.exception.ZeroException;
import org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary;

/**
 * A collection of static methods to create inference test instances or to
 * perform inference tests.
 *
 * @since 1.1
 */
public final class InferenceTestUtils {

    /** Singleton TTest instance. */
    private static final TTest T_TEST = new TTest();

    /** Singleton ChiSquareTest instance. */
    private static final ChiSquareTest CHI_SQUARE_TEST = new ChiSquareTest();

    /** Singleton OneWayAnova instance. */
    private static final OneWayAnova ONE_WAY_ANANOVA = new OneWayAnova();

    /** Singleton G-Test instance. */
    private static final GTest G_TEST = new GTest();

    /** Singleton K-S test instance. */
    private static final KolmogorovSmirnovTest KS_TEST = new KolmogorovSmirnovTest();

    /**
     * Prevent instantiation.
     */
    private InferenceTestUtils() {
        super();
    }

    // CHECKSTYLE: stop JavadocMethodCheck

    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return t statistic
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#homoscedasticT(double[], double[])
     */
    public static double homoscedasticT(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.homoscedasticT(sample1, sample2);
    }

    /**
     * @param sampleStats1 StatisticalSummary describing data from the first sample
     * @param sampleStats2 StatisticalSummary describing data from the second sample
     * @return t statistic
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#homoscedasticT(org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary, org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary)
     */
    public static double homoscedasticT(final StatisticalSummary sampleStats1,
                                        final StatisticalSummary sampleStats2)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.homoscedasticT(sampleStats1, sampleStats2);
    }

    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @param alpha significance level of the test
     * @return true if the null hypothesis can be rejected with
     * confidence 1 - alpha
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#homoscedasticTTest(double[], double[], double)
     */
    public static boolean homoscedasticTTest(final double[] sample1, final double[] sample2,
                                             final double alpha)
        throws NullArgumentException, NumberIsTooSmallException,
        OutOfRangeException, MaxCountExceededException {
        return T_TEST.homoscedasticTTest(sample1, sample2, alpha);
    }

    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return p-value for t-test
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#homoscedasticTTest(double[], double[])
     */
    public static double homoscedasticTTest(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        return T_TEST.homoscedasticTTest(sample1, sample2);
    }

    /**
     * @param sampleStats1  StatisticalSummary describing data from the first sample
     * @param sampleStats2  StatisticalSummary describing data from the second sample
     * @return p-value for t-test
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#homoscedasticTTest(org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary, org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary)
     */
    public static double homoscedasticTTest(final StatisticalSummary sampleStats1,
                                            final StatisticalSummary sampleStats2)
        throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        return T_TEST.homoscedasticTTest(sampleStats1, sampleStats2);
    }

    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return t statistic
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#pairedT(double[], double[])
     */
    public static double pairedT(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NoDataException,
        DimensionMismatchException, NumberIsTooSmallException {
        return T_TEST.pairedT(sample1, sample2);
    }

    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @param alpha significance level of the test
     * @return true if the null hypothesis can be rejected with
     * confidence 1 - alpha
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#pairedTTest(double[], double[], double)
     */
    public static boolean pairedTTest(final double[] sample1, final double[] sample2,
                                      final double alpha)
        throws NullArgumentException, NoDataException, DimensionMismatchException,
        NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        return T_TEST.pairedTTest(sample1, sample2, alpha);
    }

    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return p-value for t-test
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#pairedTTest(double[], double[])
     */
    public static double pairedTTest(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NoDataException, DimensionMismatchException,
        NumberIsTooSmallException, MaxCountExceededException {
        return T_TEST.pairedTTest(sample1, sample2);
    }

    /**
     * @param mu comparison constant
     * @param observed array of values
     * @return t statistic
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#t(double, double[])
     */
    public static double t(final double mu, final double[] observed)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.t(mu, observed);
    }

    /**
     * @param mu comparison constant
     * @param sampleStats DescriptiveStatistics holding sample summary statitstics
     * @return t statistic
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#t(double, org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary)
     */
    public static double t(final double mu, final StatisticalSummary sampleStats)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.t(mu, sampleStats);
    }

    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return t statistic
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#t(double[], double[])
     */
    public static double t(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.t(sample1, sample2);
    }

    /**
     * @param sampleStats1 StatisticalSummary describing data from the first sample
     * @param sampleStats2 StatisticalSummary describing data from the second sample
     * @return t statistic
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#t(org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary, org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary)
     */
    public static double t(final StatisticalSummary sampleStats1,
                           final StatisticalSummary sampleStats2)
        throws NullArgumentException, NumberIsTooSmallException {
        return T_TEST.t(sampleStats1, sampleStats2);
    }

    /**
     * @param mu constant value to compare sample mean against
     * @param sample array of sample data values
     * @param alpha significance level of the test
     * @return p-value
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#tTest(double, double[], double)
     */
    public static boolean tTest(final double mu, final double[] sample, final double alpha)
        throws NullArgumentException, NumberIsTooSmallException,
        OutOfRangeException, MaxCountExceededException {
        return T_TEST.tTest(mu, sample, alpha);
    }

    /**
     * @param mu constant value to compare sample mean against
     * @param sample array of sample data values
     * @return p-value
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#tTest(double, double[])
     */
    public static double tTest(final double mu, final double[] sample)
        throws NullArgumentException, NumberIsTooSmallException,
        MaxCountExceededException {
        return T_TEST.tTest(mu, sample);
    }

    /**
     * @param mu constant value to compare sample mean against
     * @param sampleStats StatisticalSummary describing sample data values
     * @param alpha significance level of the test
     * @return p-value
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#tTest(double, org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary, double)
     */
    public static boolean tTest(final double mu, final StatisticalSummary sampleStats,
                                final double alpha)
        throws NullArgumentException, NumberIsTooSmallException,
        OutOfRangeException, MaxCountExceededException {
        return T_TEST.tTest(mu, sampleStats, alpha);
    }

    /**
     * @param mu constant value to compare sample mean against
     * @param sampleStats StatisticalSummary describing sample data
     * @return p-value
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#tTest(double, org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary)
     */
    public static double tTest(final double mu, final StatisticalSummary sampleStats)
        throws NullArgumentException, NumberIsTooSmallException,
        MaxCountExceededException {
        return T_TEST.tTest(mu, sampleStats);
    }

    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @param alpha significance level of the test
     * @return true if the null hypothesis can be rejected with
     * confidence 1 - alpha
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#tTest(double[], double[], double)
     */
    public static boolean tTest(final double[] sample1, final double[] sample2,
                                final double alpha)
        throws NullArgumentException, NumberIsTooSmallException,
        OutOfRangeException, MaxCountExceededException {
        return T_TEST.tTest(sample1, sample2, alpha);
    }

    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return p-value for t-test
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#tTest(double[], double[])
     */
    public static double tTest(final double[] sample1, final double[] sample2)
        throws NullArgumentException, NumberIsTooSmallException,
        MaxCountExceededException {
        return T_TEST.tTest(sample1, sample2);
    }

    /**
     * @param sampleStats1 StatisticalSummary describing sample data values
     * @param sampleStats2 StatisticalSummary describing sample data values
     * @param alpha significance level of the test
     * @return true if the null hypothesis can be rejected with
     * confidence 1 - alpha
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#tTest(org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary, org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary, double)
     */
    public static boolean tTest(final StatisticalSummary sampleStats1,
                                final StatisticalSummary sampleStats2,
                                final double alpha)
        throws NullArgumentException, NumberIsTooSmallException,
        OutOfRangeException, MaxCountExceededException {
        return T_TEST.tTest(sampleStats1, sampleStats2, alpha);
    }

    /**
     * @param sampleStats1  StatisticalSummary describing data from the first sample
     * @param sampleStats2  StatisticalSummary describing data from the second sample
     * @return p-value for t-test
     * @see org.apache.commons.math4.legacy.stat.inference.TTest#tTest(org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary, org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary)
     */
    public static double tTest(final StatisticalSummary sampleStats1,
                               final StatisticalSummary sampleStats2)
        throws NullArgumentException, NumberIsTooSmallException,
        MaxCountExceededException {
        return T_TEST.tTest(sampleStats1, sampleStats2);
    }

    /**
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @return chiSquare test statistic
* @see org.apache.commons.math4.legacy.stat.inference.ChiSquareTest#chiSquare(double[], long[])
     */
    public static double chiSquare(final double[] expected, final long[] observed)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException {
        return CHI_SQUARE_TEST.chiSquare(expected, observed);
    }

    /**
     * @param counts array representation of 2-way table
     * @return chiSquare test statistic
     * @see org.apache.commons.math4.legacy.stat.inference.ChiSquareTest#chiSquare(long[][])
     */
    public static double chiSquare(final long[][] counts)
        throws NullArgumentException, NotPositiveException,
        DimensionMismatchException {
        return CHI_SQUARE_TEST.chiSquare(counts);
    }

    /**
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @see org.apache.commons.math4.legacy.stat.inference.ChiSquareTest#chiSquareTest(double[], long[], double)
     */
    public static boolean chiSquareTest(final double[] expected, final long[] observed,
                                        final double alpha)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException, OutOfRangeException, MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTest(expected, observed, alpha);
    }

    /**
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @return p-value
     * @see org.apache.commons.math4.legacy.stat.inference.ChiSquareTest#chiSquareTest(double[], long[])
     */
    public static double chiSquareTest(final double[] expected, final long[] observed)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException, MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTest(expected, observed);
    }

    /**
     * @param counts array representation of 2-way table
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @see org.apache.commons.math4.legacy.stat.inference.ChiSquareTest#chiSquareTest(long[][], double)
     */
    public static boolean chiSquareTest(final long[][] counts, final double alpha)
        throws NullArgumentException, DimensionMismatchException,
        NotPositiveException, OutOfRangeException, MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTest(counts, alpha);
    }

    /**
     * @param counts array representation of 2-way table
     * @return p-value
     * @see org.apache.commons.math4.legacy.stat.inference.ChiSquareTest#chiSquareTest(long[][])
     */
    public static double chiSquareTest(final long[][] counts)
        throws NullArgumentException, DimensionMismatchException,
        NotPositiveException, MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTest(counts);
    }

    /**
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data set
     * @return chiSquare test statistic
     * @see org.apache.commons.math4.legacy.stat.inference.ChiSquareTest#chiSquareDataSetsComparison(long[], long[])
     *
     * @since 1.2
     */
    public static double chiSquareDataSetsComparison(final long[] observed1,
                                                     final long[] observed2)
        throws DimensionMismatchException, NotPositiveException, ZeroException {
        return CHI_SQUARE_TEST.chiSquareDataSetsComparison(observed1, observed2);
    }

    /**
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data set
     * @return p-value
     * @see org.apache.commons.math4.legacy.stat.inference.ChiSquareTest#chiSquareTestDataSetsComparison(long[], long[])
     *
     * @since 1.2
     */
    public static double chiSquareTestDataSetsComparison(final long[] observed1,
                                                         final long[] observed2)
        throws DimensionMismatchException, NotPositiveException, ZeroException,
        MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTestDataSetsComparison(observed1, observed2);
    }

    /**
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data set
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @see org.apache.commons.math4.legacy.stat.inference.ChiSquareTest#chiSquareTestDataSetsComparison(long[], long[], double)
     *
     * @since 1.2
     */
    public static boolean chiSquareTestDataSetsComparison(final long[] observed1,
                                                          final long[] observed2,
                                                          final double alpha)
        throws DimensionMismatchException, NotPositiveException,
        ZeroException, OutOfRangeException, MaxCountExceededException {
        return CHI_SQUARE_TEST.chiSquareTestDataSetsComparison(observed1, observed2, alpha);
    }

    /**
     * @param categoryData <code>Collection</code> of <code>double[]</code>
     * arrays each containing data for one category
     * @return Fvalue
     * @see org.apache.commons.math4.legacy.stat.inference.OneWayAnova#anovaFValue(Collection)
     *
     * @since 1.2
     */
    public static double oneWayAnovaFValue(final Collection<double[]> categoryData)
        throws NullArgumentException, DimensionMismatchException {
        return ONE_WAY_ANANOVA.anovaFValue(categoryData);
    }

    /**
     * @param categoryData <code>Collection</code> of <code>double[]</code>
     * arrays each containing data for one category
     * @return Pvalue
     * @see org.apache.commons.math4.legacy.stat.inference.OneWayAnova#anovaPValue(Collection)
     *
     * @since 1.2
     */
    public static double oneWayAnovaPValue(final Collection<double[]> categoryData)
        throws NullArgumentException, DimensionMismatchException,
        ConvergenceException, MaxCountExceededException {
        return ONE_WAY_ANANOVA.anovaPValue(categoryData);
    }

    /**
     * @param categoryData <code>Collection</code> of <code>double[]</code>
     * arrays each containing data for one category
     * @param alpha significance level of the test
     * @return true if the null hypothesis can be rejected with
     * confidence 1 - alpha
     * @see org.apache.commons.math4.legacy.stat.inference.OneWayAnova#anovaTest(Collection,double)
     *
     * @since 1.2
     */
    public static boolean oneWayAnovaTest(final Collection<double[]> categoryData,
                                          final double alpha)
        throws NullArgumentException, DimensionMismatchException,
        OutOfRangeException, ConvergenceException, MaxCountExceededException {
        return ONE_WAY_ANANOVA.anovaTest(categoryData, alpha);
    }

     /**
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @return G-Test statistic
     * @see org.apache.commons.math4.legacy.stat.inference.GTest#g(double[], long[])
     * @since 3.1
     */
    public static double g(final double[] expected, final long[] observed)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException {
        return G_TEST.g(expected, observed);
    }

    /**
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @return p-value
     * @see org.apache.commons.math4.legacy.stat.inference.GTest#gTest( double[],  long[] )
     * @since 3.1
     */
    public static double gTest(final double[] expected, final long[] observed)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException, MaxCountExceededException {
        return G_TEST.gTest(expected, observed);
    }

    /**
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @return p-value
     * @see org.apache.commons.math4.legacy.stat.inference.GTest#gTestIntrinsic(double[], long[] )
     * @since 3.1
     */
    public static double gTestIntrinsic(final double[] expected, final long[] observed)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException, MaxCountExceededException {
        return G_TEST.gTestIntrinsic(expected, observed);
    }

     /**
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence 1 -
     * alpha
     * @see org.apache.commons.math4.legacy.stat.inference.GTest#gTest( double[],long[],double)
     * @since 3.1
     */
    public static boolean gTest(final double[] expected, final long[] observed,
                                final double alpha)
        throws NotPositiveException, NotStrictlyPositiveException,
        DimensionMismatchException, OutOfRangeException, MaxCountExceededException {
        return G_TEST.gTest(expected, observed, alpha);
    }

    /**
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data
     * set
     * @return G-Test statistic
     * @see org.apache.commons.math4.legacy.stat.inference.GTest#gDataSetsComparison(long[], long[])
     * @since 3.1
     */
    public static double gDataSetsComparison(final long[] observed1,
                                                  final long[] observed2)
        throws DimensionMismatchException, NotPositiveException, ZeroException {
        return G_TEST.gDataSetsComparison(observed1, observed2);
    }

    /**
     * @param k11 number of times the two events occurred together (AB)
     * @param k12 number of times the second event occurred WITHOUT the
     * first event (notA,B)
     * @param k21 number of times the first event occurred WITHOUT the
     * second event (A, notB)
     * @param k22 number of times something else occurred (i.e. was neither
     * of these events (notA, notB)
     * @return root log-likelihood ratio
     * @see org.apache.commons.math4.legacy.stat.inference.GTest#rootLogLikelihoodRatio(long, long, long, long)
     * @since 3.1
     */
    public static double rootLogLikelihoodRatio(final long k11, final long k12, final long k21, final long k22)
        throws DimensionMismatchException, NotPositiveException, ZeroException {
        return G_TEST.rootLogLikelihoodRatio(k11, k12, k21, k22);
    }


    /**
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data
     * set
     * @return p-value
     * @see org.apache.commons.math4.legacy.stat.inference.GTest#gTestDataSetsComparison(long[], long[])
     * @since 3.1
     */
    public static double gTestDataSetsComparison(final long[] observed1,
                                                        final long[] observed2)
        throws DimensionMismatchException, NotPositiveException, ZeroException,
        MaxCountExceededException {
        return G_TEST.gTestDataSetsComparison(observed1, observed2);
    }

    /**
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data
     * set
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence 1 -
     * alpha
     * @see org.apache.commons.math4.legacy.stat.inference.GTest#gTestDataSetsComparison(long[],long[],double)
     * @since 3.1
     */
    public static boolean gTestDataSetsComparison(final long[] observed1,
                                                  final long[] observed2,
                                                  final double alpha)
        throws DimensionMismatchException, NotPositiveException,
        ZeroException, OutOfRangeException, MaxCountExceededException {
        return G_TEST.gTestDataSetsComparison(observed1, observed2, alpha);
    }

    /**
     * @param dist reference distribution
     * @param data sample being evaluated
     * @return Kolmogorov-Smirnov statistic \(D_n\)
     * @see org.apache.commons.math4.legacy.stat.inference.KolmogorovSmirnovTest#kolmogorovSmirnovStatistic(ContinuousDistribution, double[])
     * @since 3.3
     */
    public static double kolmogorovSmirnovStatistic(ContinuousDistribution dist, double[] data)
            throws InsufficientDataException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovStatistic(dist, data);
    }

    /**
     * @param dist reference distribution
     * @param data sample being being evaluated
     * @return the p-value associated with the null hypothesis that {@code data} is a sample from
     *         {@code distribution}
     * @see org.apache.commons.math4.legacy.stat.inference.KolmogorovSmirnovTest#kolmogorovSmirnovTest(ContinuousDistribution, double[])
     * @since 3.3
     */
    public static double kolmogorovSmirnovTest(ContinuousDistribution dist, double[] data)
            throws InsufficientDataException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovTest(dist, data);
    }

    /**
     * @param dist reference distribution
     * @param data sample being being evaluated
     * @param strict whether or not to force exact computation of the p-value
     * @return the p-value associated with the null hypothesis that {@code data} is a sample from
     *         {@code distribution}
     * @see org.apache.commons.math4.legacy.stat.inference.KolmogorovSmirnovTest#kolmogorovSmirnovTest(ContinuousDistribution, double[], boolean)
     * @since 3.3
     */
    public static double kolmogorovSmirnovTest(ContinuousDistribution dist, double[] data, boolean strict)
            throws InsufficientDataException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovTest(dist, data, strict);
    }

    /**
     * @param dist reference distribution
     * @param data sample being being evaluated
     * @param alpha significance level of the test
     * @return true iff the null hypothesis that {@code data} is a sample from {@code distribution}
     *         can be rejected with confidence 1 - {@code alpha}
     * @see org.apache.commons.math4.legacy.stat.inference.KolmogorovSmirnovTest#kolmogorovSmirnovTest(ContinuousDistribution, double[], double)
     * @since 3.3
     */
    public static boolean kolmogorovSmirnovTest(ContinuousDistribution dist, double[] data, double alpha)
            throws InsufficientDataException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovTest(dist, data, alpha);
    }

    /**
     * @param x first sample
     * @param y second sample
     * @return test statistic \(D_{n,m}\) used to evaluate the null hypothesis that {@code x} and
     *         {@code y} represent samples from the same underlying distribution
     * @see org.apache.commons.math4.legacy.stat.inference.KolmogorovSmirnovTest#kolmogorovSmirnovStatistic(double[], double[])
     * @since 3.3
     */
    public static double kolmogorovSmirnovStatistic(double[] x, double[] y)
            throws InsufficientDataException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovStatistic(x, y);
    }

    /**
     * @param x first sample dataset
     * @param y second sample dataset
     * @return p-value associated with the null hypothesis that {@code x} and {@code y} represent
     *         samples from the same distribution
     * @see org.apache.commons.math4.legacy.stat.inference.KolmogorovSmirnovTest#kolmogorovSmirnovTest(double[], double[])
     * @since 3.3
     */
    public static double kolmogorovSmirnovTest(double[] x, double[] y)
            throws InsufficientDataException, NullArgumentException {
        return KS_TEST.kolmogorovSmirnovTest(x, y);
    }

    /**
     * @param x first sample dataset.
     * @param y second sample dataset.
     * @param strict whether or not the probability to compute is expressed as
     * a strict inequality (ignored for large samples).
     * @return p-value associated with the null hypothesis that {@code x} and
     * {@code y} represent samples from the same distribution.
     * @see org.apache.commons.math4.legacy.stat.inference.KolmogorovSmirnovTest#kolmogorovSmirnovTest(double[], double[], boolean)
     * @since 3.3
     */
    public static double kolmogorovSmirnovTest(double[] x, double[] y, boolean strict)
            throws InsufficientDataException, NullArgumentException  {
        return KS_TEST.kolmogorovSmirnovTest(x, y, strict);
    }

    /**
     * @param d D-statistic value
     * @param n first sample size
     * @param m second sample size
     * @param strict whether or not the probability to compute is expressed as a strict inequality
     * @return probability that a randomly selected m-n partition of m + n generates \(D_{n,m}\)
     *         greater than (resp. greater than or equal to) {@code d}
     * @see org.apache.commons.math4.legacy.stat.inference.KolmogorovSmirnovTest#exactP(double, int, int, boolean)
     * @since 3.3
     */
    public static double exactP(double d, int m, int n, boolean strict) {
        return KS_TEST.exactP(d, n, m, strict);
    }

    /**
     * @param d D-statistic value
     * @param n first sample size
     * @param m second sample size
     * @return approximate probability that a randomly selected m-n partition of m + n generates
     *         \(D_{n,m}\) greater than {@code d}
     * @see org.apache.commons.math4.legacy.stat.inference.KolmogorovSmirnovTest#approximateP(double, int, int)
     * @since 3.3
     */
    public static double approximateP(double d, int n, int m) {
        return KS_TEST.approximateP(d, n, m);
    }

    /**
     * @param d D-statistic value
     * @param n first sample size
     * @param m second sample size
     * @param iterations number of random partitions to generate
     * @param strict whether or not the probability to compute is expressed as a strict inequality
     * @param rng RNG used for generating the partitions.
     * @return proportion of randomly generated m-n partitions of m + n that result in \(D_{n,m}\)
     * greater than (resp. greater than or equal to) {@code d}
     * @see org.apache.commons.math4.legacy.stat.inference.KolmogorovSmirnovTest#monteCarloP(double,int,int,boolean,int,UniformRandomProvider)
     * @since 3.3
     */
    public static double monteCarloP(double d, int n, int m, boolean strict, int iterations, UniformRandomProvider rng) {
        return KS_TEST.monteCarloP(d, n, m, strict, iterations, rng);
    }


    // CHECKSTYLE: resume JavadocMethodCheck
}
