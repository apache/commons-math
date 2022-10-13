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
package org.apache.commons.math4.legacy.stat.descriptive;

import java.util.Locale;

import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math4.legacy.stat.descriptive.moment.Mean;
import org.apache.commons.math4.legacy.stat.descriptive.moment.Variance;
import org.apache.commons.math4.legacy.stat.descriptive.rank.Max;
import org.apache.commons.math4.legacy.stat.descriptive.rank.Min;
import org.apache.commons.math4.legacy.stat.descriptive.rank.Percentile;
import org.apache.commons.math4.legacy.stat.descriptive.summary.Sum;
import org.apache.commons.math4.legacy.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.numbers.core.Precision;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link DescriptiveStatistics} class.
 */
public class DescriptiveStatisticsTest {
    private static UniformRandomProvider random = RandomSource.WELL_1024_A.create(2345789432894L);

    protected DescriptiveStatistics createDescriptiveStatistics() {
        return new DescriptiveStatistics();
    }

    @Test
    public void testSetterInjection() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(3);
        Assert.assertEquals(2, stats.getMean(), 1E-10);
        // Now lets try some new math
        stats.setMeanImpl(new DeepMean());
        Assert.assertEquals(42, stats.getMean(), 1E-10);
    }

    @Test
    public void testCopy() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(3);
        DescriptiveStatistics copy = new DescriptiveStatistics(stats);
        Assert.assertEquals(2, copy.getMean(), 1E-10);
        // Now lets try some new math
        stats.setMeanImpl(new DeepMean());
        copy = stats.copy();
        Assert.assertEquals(42, copy.getMean(), 1E-10);
    }

    @Test
    public void testWindowSize() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        stats.setWindowSize(300);
        for (int i = 0; i < 100; ++i) {
            stats.addValue(i + 1);
        }
        int refSum = (100 * 101) / 2;
        Assert.assertEquals(refSum / 100.0, stats.getMean(), 1E-10);
        Assert.assertEquals(300, stats.getWindowSize());
        try {
            stats.setWindowSize(-3);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException iae) {
            // expected
        }
        Assert.assertEquals(300, stats.getWindowSize());
        stats.setWindowSize(50);
        Assert.assertEquals(50, stats.getWindowSize());
        int refSum2 = refSum - (50 * 51) / 2;
        Assert.assertEquals(refSum2 / 50.0, stats.getMean(), 1E-10);
    }

    @Test
    public void testGetValues() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        for (int i = 100; i > 0; --i) {
            stats.addValue(i);
        }
        int refSum = (100 * 101) / 2;
        Assert.assertEquals(refSum / 100.0, stats.getMean(), 1E-10);
        double[] v = stats.getValues();
        for (int i = 0; i < v.length; ++i) {
            Assert.assertEquals(100.0 - i, v[i], 1.0e-10);
        }
        double[] s = stats.getSortedValues();
        for (int i = 0; i < s.length; ++i) {
            Assert.assertEquals(i + 1.0, s[i], 1.0e-10);
        }
        Assert.assertEquals(12.0, stats.getElement(88), 1.0e-10);
    }

    @Test
    public void testQuadraticMean() {
        final double[] values = { 1.2, 3.4, 5.6, 7.89 };
        final DescriptiveStatistics stats = new DescriptiveStatistics(values);

        final int len = values.length;
        double expected = 0;
        for (int i = 0; i < len; i++) {
            final double v = values[i];
            expected += v * v / len;
        }
        expected = Math.sqrt(expected);

        Assert.assertEquals(expected, stats.getQuadraticMean(), Math.ulp(expected));
    }

    @Test
    public void testToString() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(2);
        stats.addValue(3);
        Locale d = Locale.getDefault();
        Locale.setDefault(Locale.US);
        Assert.assertEquals("DescriptiveStatistics:\n" +
                     "n: 3\n" +
                     "min: 1.0\n" +
                     "max: 3.0\n" +
                     "mean: 2.0\n" +
                     "std dev: 1.0\n" +
                     "median: 2.0\n" +
                     "skewness: 0.0\n" +
                     "kurtosis: NaN\n",  stats.toString());
        Locale.setDefault(d);
    }

    @Test
    public void testShuffledStatistics() {
        // the purpose of this test is only to check the get/set methods
        // we are aware shuffling statistics like this is really not
        // something sensible to do in production ...
        DescriptiveStatistics reference = createDescriptiveStatistics();
        DescriptiveStatistics shuffled  = createDescriptiveStatistics();

        UnivariateStatistic tmp = shuffled.getGeometricMeanImpl();
        shuffled.setGeometricMeanImpl(shuffled.getMeanImpl());
        shuffled.setMeanImpl(shuffled.getKurtosisImpl());
        shuffled.setKurtosisImpl(shuffled.getSkewnessImpl());
        shuffled.setSkewnessImpl(shuffled.getVarianceImpl());
        shuffled.setVarianceImpl(shuffled.getMaxImpl());
        shuffled.setMaxImpl(shuffled.getMinImpl());
        shuffled.setMinImpl(shuffled.getSumImpl());
        shuffled.setSumImpl(shuffled.getSumsqImpl());
        shuffled.setSumsqImpl(tmp);

        for (int i = 100; i > 0; --i) {
            reference.addValue(i);
            shuffled.addValue(i);
        }

        Assert.assertEquals(reference.getMean(),          shuffled.getGeometricMean(), 1.0e-10);
        Assert.assertEquals(reference.getKurtosis(),      shuffled.getMean(),          1.0e-10);
        Assert.assertEquals(reference.getSkewness(),      shuffled.getKurtosis(), 1.0e-10);
        Assert.assertEquals(reference.getVariance(),      shuffled.getSkewness(), 1.0e-10);
        Assert.assertEquals(reference.getMax(),           shuffled.getVariance(), 1.0e-10);
        Assert.assertEquals(reference.getMin(),           shuffled.getMax(), 1.0e-10);
        Assert.assertEquals(reference.getSum(),           shuffled.getMin(), 1.0e-10);
        Assert.assertEquals(reference.getSumsq(),         shuffled.getSum(), 1.0e-10);
        Assert.assertEquals(reference.getGeometricMean(), shuffled.getSumsq(), 1.0e-10);
    }

    @Test
    public void testPercentileSetter() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(2);
        stats.addValue(3);
        Assert.assertEquals(2, stats.getPercentile(50.0), 1E-10);

        // Inject wrapped Percentile impl
        stats.setPercentileImpl(new GoodPercentile());
        Assert.assertEquals(2, stats.getPercentile(50.0), 1E-10);

        // Try "new math" impl
        stats.setPercentileImpl(new SubPercentile());
        Assert.assertEquals(10.0, stats.getPercentile(10.0), 1E-10);

        // Try to set bad impl
        try {
            stats.setPercentileImpl(new BadPercentile());
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void test20090720() {
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(100);
        for (int i = 0; i < 161; i++) {
            descriptiveStatistics.addValue(1.2);
        }
        descriptiveStatistics.clear();
        descriptiveStatistics.addValue(1.2);
        Assert.assertEquals(1, descriptiveStatistics.getN());
    }

    @Test
    public void testRemoval() {

        final DescriptiveStatistics dstat = createDescriptiveStatistics();

        checkremoval(dstat, 1, 6.0, 0.0, Double.NaN);
        checkremoval(dstat, 3, 5.0, 3.0, 4.5);
        checkremoval(dstat, 6, 3.5, 2.5, 3.0);
        checkremoval(dstat, 9, 3.5, 2.5, 3.0);
        checkremoval(dstat, DescriptiveStatistics.INFINITE_WINDOW, 3.5, 2.5, 3.0);
    }

    @Test
    public void testSummaryConsistency() {
        final DescriptiveStatistics dstats = new DescriptiveStatistics();
        final SummaryStatistics sstats = new SummaryStatistics();
        final int windowSize = 5;
        dstats.setWindowSize(windowSize);
        final double tol = 1E-12;
        for (int i = 0; i < 20; i++) {
            dstats.addValue(i);
            sstats.clear();
            double[] values = dstats.getValues();
            for (int j = 0; j < values.length; j++) {
                sstats.addValue(values[j]);
            }
            TestUtils.assertEquals(dstats.getMean(), sstats.getMean(), tol);
            TestUtils.assertEquals(new Mean().evaluate(values), dstats.getMean(), tol);
            TestUtils.assertEquals(dstats.getMax(), sstats.getMax(), tol);
            TestUtils.assertEquals(new Max().evaluate(values), dstats.getMax(), tol);
            TestUtils.assertEquals(dstats.getGeometricMean(), sstats.getGeometricMean(), tol);
            TestUtils.assertEquals(new GeometricMean().evaluate(values), dstats.getGeometricMean(), tol);
            TestUtils.assertEquals(dstats.getMin(), sstats.getMin(), tol);
            TestUtils.assertEquals(new Min().evaluate(values), dstats.getMin(), tol);
            TestUtils.assertEquals(dstats.getStandardDeviation(), sstats.getStandardDeviation(), tol);
            TestUtils.assertEquals(dstats.getVariance(), sstats.getVariance(), tol);
            TestUtils.assertEquals(new Variance().evaluate(values), dstats.getVariance(), tol);
            TestUtils.assertEquals(dstats.getSum(), sstats.getSum(), tol);
            TestUtils.assertEquals(new Sum().evaluate(values), dstats.getSum(), tol);
            TestUtils.assertEquals(dstats.getSumsq(), sstats.getSumsq(), tol);
            TestUtils.assertEquals(new SumOfSquares().evaluate(values), dstats.getSumsq(), tol);
            TestUtils.assertEquals(dstats.getPopulationVariance(), sstats.getPopulationVariance(), tol);
            TestUtils.assertEquals(new Variance(false).evaluate(values), dstats.getPopulationVariance(), tol);
        }
    }

    @Test
    public void testMath1129(){
        final double[] data = new double[] {
            -0.012086732064244697,
            -0.24975668704012527,
            0.5706168483164684,
            -0.322111769955327,
            0.24166759508327315,
            Double.NaN,
            0.16698443218942854,
            -0.10427763937565114,
            -0.15595963093172435,
            -0.028075857595882995,
            -0.24137994506058857,
            0.47543170476574426,
            -0.07495595384947631,
            0.37445697625436497,
            -0.09944199541668033
        };

        final DescriptiveStatistics ds = new DescriptiveStatistics(data);

        final double t = ds.getPercentile(75);
        final double o = ds.getPercentile(25);

        final double iqr = t - o;
        // System.out.println(String.format("25th percentile %s 75th percentile %s", o, t));
        Assert.assertTrue(iqr >= 0);
    }

    @Test
    public void testInit0() {
        //test window constructor
        int window = 1 + random.nextInt(Integer.MAX_VALUE-1);
        DescriptiveStatistics instance = new DescriptiveStatistics(window);
        Assert.assertEquals(window,
                            instance.getWindowSize());
    }

    @Test
    public void testInitDouble() {
        //test double[] constructor
        double[] initialDoubleArray = null;
        new DescriptiveStatistics(initialDoubleArray);
            //a null argument corresponds to DescriptiveStatistics(), so test
            //that no exception is thrown
        int initialDoubleArraySize = random.nextInt(1024 //some random
            //memory consumption and test size limitation
        );
//        System.out.println(String.format("initialDoubleArraySize: %s",
//                initialDoubleArraySize));
        initialDoubleArray = new double[initialDoubleArraySize];
        for(int i = 0; i < initialDoubleArraySize; i++) {
            double value = random.nextDouble();
            initialDoubleArray[i] = value;
        }
        new DescriptiveStatistics(initialDoubleArray);
    }

    @Test
    public void testInitDoubleWrapper() {
        //test Double[] constructor
        Double[] initialDoubleWrapperArray = null;
        new DescriptiveStatistics(initialDoubleWrapperArray);
        int initialDoubleWrapperArraySize = random.nextInt(1024 //some random
            //memory consumption and test size limitation
        );
        initialDoubleWrapperArray = generateInitialDoubleArray(initialDoubleWrapperArraySize);
        new DescriptiveStatistics(initialDoubleWrapperArray);
    }

    @Test
    public void testInitCopy() {
        //test copy constructor
        int initialDoubleArray = random.nextInt(1024 //some random
            //memory consumption and test size limitation
        );
        DescriptiveStatistics original = new DescriptiveStatistics(initialDoubleArray);
        DescriptiveStatistics instance = new DescriptiveStatistics(original);
        Assert.assertEquals(original.getGeometricMean(),
                            instance.getGeometricMean(),
                            0);
        Assert.assertEquals(original.getKurtosis(),
                            instance.getKurtosis(),
                            0);
        Assert.assertEquals(original.getMax(),
                            instance.getMax(),
                            0);
        Assert.assertEquals(original.getMean(),
                            instance.getMean(),
                            0);
        Assert.assertEquals(original.getMin(),
                            instance.getMin(),
                            0);
        Assert.assertEquals(original.getN(),
                            instance.getN());
        Assert.assertEquals(original.getSkewness(),
                            instance.getSkewness(),
                            0);
        Assert.assertArrayEquals(original.getValues(),
                                 instance.getValues(),
                                 0);
        Assert.assertEquals(original.getWindowSize(),
                            instance.getWindowSize());
            //doesn't implement equals
    }

    public void checkremoval(DescriptiveStatistics dstat, int wsize,
                             double mean1, double mean2, double mean3) {

        dstat.setWindowSize(wsize);
        dstat.clear();

        for (int i = 1 ; i <= 6 ; ++i) {
            dstat.addValue(i);
        }

        Assert.assertTrue(Precision.equalsIncludingNaN(mean1, dstat.getMean()));
        dstat.replaceMostRecentValue(0);
        Assert.assertTrue(Precision.equalsIncludingNaN(mean2, dstat.getMean()));
        dstat.removeMostRecentValue();
        Assert.assertTrue(Precision.equalsIncludingNaN(mean3, dstat.getMean()));
    }

    private Double[] generateInitialDoubleArray(int size) {
        Double[] retValue = new Double[size];
        for(int i = 0; i < size; i++) {
            Double value = random.nextDouble();
            retValue[i] = value;
        }
        return retValue;
    }

    // Test UnivariateStatistics impls for setter injection tests

    /**
     * A new way to compute the mean
     */
    static class DeepMean implements UnivariateStatistic {

        @Override
        public double evaluate(double[] values, int begin, int length) {
            return 42;
        }

        @Override
        public double evaluate(double[] values) {
            return 42;
        }
        @Override
        public UnivariateStatistic copy() {
            return new DeepMean();
        }
    }

    /**
     * Test percentile implementation - wraps a Percentile
     */
    static class GoodPercentile implements UnivariateStatistic {
        private final Percentile percentile = new Percentile();
        public void setQuantile(double quantile) {
            percentile.setQuantile(quantile);
        }
        @Override
        public double evaluate(double[] values, int begin, int length) {
            return percentile.evaluate(values, begin, length);
        }
        @Override
        public double evaluate(double[] values) {
            return percentile.evaluate(values);
        }
        @Override
        public UnivariateStatistic copy() {
            GoodPercentile result = new GoodPercentile();
            result.setQuantile(percentile.getQuantile());
            return result;
        }
    }

    /**
     * Test percentile subclass - another "new math" impl
     * Always returns currently set quantile
     */
    static class SubPercentile extends Percentile {
        @Override
        public double evaluate(double[] values, int begin, int length) {
            return getQuantile();
        }
        @Override
        public double evaluate(double[] values) {
            return getQuantile();
        }
        private static final long serialVersionUID = 8040701391045914979L;
        @Override
        public Percentile copy() {
            SubPercentile result = new SubPercentile();
            return result;
        }
    }

    /**
     * "Bad" test percentile implementation - no setQuantile
     */
    static class BadPercentile implements UnivariateStatistic {
        private final Percentile percentile = new Percentile();
        @Override
        public double evaluate(double[] values, int begin, int length) {
            return percentile.evaluate(values, begin, length);
        }
        @Override
        public double evaluate(double[] values) {
            return percentile.evaluate(values);
        }
        @Override
        public UnivariateStatistic copy() {
            return new BadPercentile();
        }
    }
}
