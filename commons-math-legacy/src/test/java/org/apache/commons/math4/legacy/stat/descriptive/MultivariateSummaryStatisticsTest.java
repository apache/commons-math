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
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathIllegalStateException;
import org.apache.commons.math4.legacy.stat.descriptive.moment.Mean;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Test;
import org.junit.Assert;

/**
 * Test cases for the {@link MultivariateSummaryStatistics} class.
 *
 */

public class MultivariateSummaryStatisticsTest {

    protected MultivariateSummaryStatistics createMultivariateSummaryStatistics(int k, boolean isCovarianceBiasCorrected) {
        return new MultivariateSummaryStatistics(k, isCovarianceBiasCorrected);
    }

    @Test
    public void testSetterInjection() {
        MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(2, true);
        u.setMeanImpl(new StorelessUnivariateStatistic[] {
                        new SumMean(), new SumMean()
                      });
        u.addValue(new double[] { 1, 2 });
        u.addValue(new double[] { 3, 4 });
        Assert.assertEquals(4, u.getMean()[0], 1E-14);
        Assert.assertEquals(6, u.getMean()[1], 1E-14);
        u.clear();
        u.addValue(new double[] { 1, 2 });
        u.addValue(new double[] { 3, 4 });
        Assert.assertEquals(4, u.getMean()[0], 1E-14);
        Assert.assertEquals(6, u.getMean()[1], 1E-14);
        u.clear();
        u.setMeanImpl(new StorelessUnivariateStatistic[] {
                        new Mean(), new Mean()
                      }); // OK after clear
        u.addValue(new double[] { 1, 2 });
        u.addValue(new double[] { 3, 4 });
        Assert.assertEquals(2, u.getMean()[0], 1E-14);
        Assert.assertEquals(3, u.getMean()[1], 1E-14);
        Assert.assertEquals(2, u.getDimension());
    }

    @Test
    public void testSetterIllegalState() {
        MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(2, true);
        u.addValue(new double[] { 1, 2 });
        u.addValue(new double[] { 3, 4 });
        try {
            u.setMeanImpl(new StorelessUnivariateStatistic[] { new SumMean(), new SumMean() });
            Assert.fail("Expecting MathIllegalStateException");
        } catch (MathIllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void testToString() {
        MultivariateSummaryStatistics stats = createMultivariateSummaryStatistics(2, true);
        stats.addValue(new double[] {1, 3});
        stats.addValue(new double[] {2, 2});
        stats.addValue(new double[] {3, 1});
        Locale d = Locale.getDefault();
        Locale.setDefault(Locale.US);
        final String suffix = System.getProperty("line.separator");
        Assert.assertEquals("MultivariateSummaryStatistics:" + suffix+
                     "n: 3" +suffix+
                     "min: 1.0, 1.0" +suffix+
                     "max: 3.0, 3.0" +suffix+
                     "mean: 2.0, 2.0" +suffix+
                     "geometric mean: 1.817..., 1.817..." +suffix+
                     "sum of squares: 14.0, 14.0" +suffix+
                     "sum of logarithms: 1.791..., 1.791..." +suffix+
                     "standard deviation: 1.0, 1.0" +suffix+
                     "covariance: Array2DRowRealMatrix{{1.0,-1.0},{-1.0,1.0}}" +suffix,
                     stats.toString().replaceAll("([0-9]+\\.[0-9][0-9][0-9])[0-9]+", "$1..."));
        Locale.setDefault(d);
    }

    @Test
    public void testShuffledStatistics() {
        // the purpose of this test is only to check the get/set methods
        // we are aware shuffling statistics like this is really not
        // something sensible to do in production ...
        MultivariateSummaryStatistics reference = createMultivariateSummaryStatistics(2, true);
        MultivariateSummaryStatistics shuffled  = createMultivariateSummaryStatistics(2, true);

        StorelessUnivariateStatistic[] tmp = shuffled.getGeoMeanImpl();
        shuffled.setGeoMeanImpl(shuffled.getMeanImpl());
        shuffled.setMeanImpl(shuffled.getMaxImpl());
        shuffled.setMaxImpl(shuffled.getMinImpl());
        shuffled.setMinImpl(shuffled.getSumImpl());
        shuffled.setSumImpl(shuffled.getSumsqImpl());
        shuffled.setSumsqImpl(shuffled.getSumLogImpl());
        shuffled.setSumLogImpl(tmp);

        for (int i = 100; i > 0; --i) {
            reference.addValue(new double[] {i, i});
            shuffled.addValue(new double[] {i, i});
        }

        TestUtils.assertEquals(reference.getMean(),          shuffled.getGeometricMean(), 1.0e-10);
        TestUtils.assertEquals(reference.getMax(),           shuffled.getMean(),          1.0e-10);
        TestUtils.assertEquals(reference.getMin(),           shuffled.getMax(),           1.0e-10);
        TestUtils.assertEquals(reference.getSum(),           shuffled.getMin(),           1.0e-10);
        TestUtils.assertEquals(reference.getSumSq(),         shuffled.getSum(),           1.0e-10);
        TestUtils.assertEquals(reference.getSumLog(),        shuffled.getSumSq(),         1.0e-10);
        TestUtils.assertEquals(reference.getGeometricMean(), shuffled.getSumLog(),        1.0e-10);
    }

    /**
     * Bogus mean implementation to test setter injection.
     * Returns the sum instead of the mean.
     */
    static class SumMean implements StorelessUnivariateStatistic {
        private double sum = 0;
        private long n = 0;
        @Override
        public double evaluate(double[] values, int begin, int length) {
            return 0;
        }
        @Override
        public double evaluate(double[] values) {
            return 0;
        }
        @Override
        public void clear() {
          sum = 0;
          n = 0;
        }
        @Override
        public long getN() {
            return n;
        }
        @Override
        public double getResult() {
            return sum;
        }
        @Override
        public void increment(double d) {
            sum += d;
            n++;
        }
        @Override
        public void incrementAll(double[] values, int start, int length) {
        }
        @Override
        public void incrementAll(double[] values) {
        }
        @Override
        public StorelessUnivariateStatistic copy() {
            return new SumMean();
        }
    }

    @Test
    public void testDimension() {
        try {
            createMultivariateSummaryStatistics(2, true).addValue(new double[3]);
            Assert.fail("Expecting DimensionMismatchException");
        } catch (DimensionMismatchException dme) {
            // expected behavior
        }
    }

    /** test stats */
    @Test
    public void testStats() {
        MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(2, true);
        Assert.assertEquals(0, u.getN());
        u.addValue(new double[] { 1, 2 });
        u.addValue(new double[] { 2, 3 });
        u.addValue(new double[] { 2, 3 });
        u.addValue(new double[] { 3, 4 });
        Assert.assertEquals( 4, u.getN());
        Assert.assertEquals( 8, u.getSum()[0], 1.0e-10);
        Assert.assertEquals(12, u.getSum()[1], 1.0e-10);
        Assert.assertEquals(18, u.getSumSq()[0], 1.0e-10);
        Assert.assertEquals(38, u.getSumSq()[1], 1.0e-10);
        Assert.assertEquals( 1, u.getMin()[0], 1.0e-10);
        Assert.assertEquals( 2, u.getMin()[1], 1.0e-10);
        Assert.assertEquals( 3, u.getMax()[0], 1.0e-10);
        Assert.assertEquals( 4, u.getMax()[1], 1.0e-10);
        Assert.assertEquals(2.4849066497880003102, u.getSumLog()[0], 1.0e-10);
        Assert.assertEquals( 4.276666119016055311, u.getSumLog()[1], 1.0e-10);
        Assert.assertEquals( 1.8612097182041991979, u.getGeometricMean()[0], 1.0e-10);
        Assert.assertEquals( 2.9129506302439405217, u.getGeometricMean()[1], 1.0e-10);
        Assert.assertEquals( 2, u.getMean()[0], 1.0e-10);
        Assert.assertEquals( 3, u.getMean()[1], 1.0e-10);
        Assert.assertEquals(JdkMath.sqrt(2.0 / 3.0), u.getStandardDeviation()[0], 1.0e-10);
        Assert.assertEquals(JdkMath.sqrt(2.0 / 3.0), u.getStandardDeviation()[1], 1.0e-10);
        Assert.assertEquals(2.0 / 3.0, u.getCovariance().getEntry(0, 0), 1.0e-10);
        Assert.assertEquals(2.0 / 3.0, u.getCovariance().getEntry(0, 1), 1.0e-10);
        Assert.assertEquals(2.0 / 3.0, u.getCovariance().getEntry(1, 0), 1.0e-10);
        Assert.assertEquals(2.0 / 3.0, u.getCovariance().getEntry(1, 1), 1.0e-10);
        u.clear();
        Assert.assertEquals(0, u.getN());
    }

    @Test
    public void testN0andN1Conditions() {
        MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(1, true);
        Assert.assertTrue(Double.isNaN(u.getMean()[0]));
        Assert.assertTrue(Double.isNaN(u.getStandardDeviation()[0]));

        /* n=1 */
        u.addValue(new double[] { 1 });
        Assert.assertEquals(1.0, u.getMean()[0], 1.0e-10);
        Assert.assertEquals(1.0, u.getGeometricMean()[0], 1.0e-10);
        Assert.assertEquals(0.0, u.getStandardDeviation()[0], 1.0e-10);

        /* n=2 */
        u.addValue(new double[] { 2 });
        Assert.assertTrue(u.getStandardDeviation()[0] > 0);
    }

    @Test
    public void testNaNContracts() {
        MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(1, true);
        Assert.assertTrue(Double.isNaN(u.getMean()[0]));
        Assert.assertTrue(Double.isNaN(u.getMin()[0]));
        Assert.assertTrue(Double.isNaN(u.getStandardDeviation()[0]));
        Assert.assertTrue(Double.isNaN(u.getGeometricMean()[0]));

        u.addValue(new double[] { 1.0 });
        Assert.assertFalse(Double.isNaN(u.getMean()[0]));
        Assert.assertFalse(Double.isNaN(u.getMin()[0]));
        Assert.assertFalse(Double.isNaN(u.getStandardDeviation()[0]));
        Assert.assertFalse(Double.isNaN(u.getGeometricMean()[0]));
    }

    @Test
    public void testEqualsAndHashCode() {
        MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(2, true);
        MultivariateSummaryStatistics t = null;
        int emptyHash = u.hashCode();
        Assert.assertEquals(u, u);
        Assert.assertNotEquals(u, t);
        Assert.assertFalse(u.equals(Double.valueOf(0)));
        t = createMultivariateSummaryStatistics(2, true);
        Assert.assertEquals(t, u);
        Assert.assertEquals(u, t);
        Assert.assertEquals(emptyHash, t.hashCode());

        // Add some data to u
        u.addValue(new double[] { 2d, 1d });
        u.addValue(new double[] { 1d, 1d });
        u.addValue(new double[] { 3d, 1d });
        u.addValue(new double[] { 4d, 1d });
        u.addValue(new double[] { 5d, 1d });
        Assert.assertFalse(t.equals(u));
        Assert.assertFalse(u.equals(t));
        Assert.assertTrue(u.hashCode() != t.hashCode());

        //Add data in same order to t
        t.addValue(new double[] { 2d, 1d });
        t.addValue(new double[] { 1d, 1d });
        t.addValue(new double[] { 3d, 1d });
        t.addValue(new double[] { 4d, 1d });
        t.addValue(new double[] { 5d, 1d });
        Assert.assertTrue(t.equals(u));
        Assert.assertTrue(u.equals(t));
        Assert.assertEquals(u.hashCode(), t.hashCode());

        // Clear and make sure summaries are indistinguishable from empty summary
        u.clear();
        t.clear();
        Assert.assertTrue(t.equals(u));
        Assert.assertTrue(u.equals(t));
        Assert.assertEquals(emptyHash, t.hashCode());
        Assert.assertEquals(emptyHash, u.hashCode());
    }
}
