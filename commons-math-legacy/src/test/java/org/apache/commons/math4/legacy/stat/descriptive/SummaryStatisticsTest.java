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


import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.MathIllegalStateException;
import org.apache.commons.math4.legacy.stat.StatUtils;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
/**
 * Test cases for the {@link SummaryStatistics} class.
 */
public class SummaryStatisticsTest {

    private final double one = 1;
    private final float twoF = 2;
    private final long twoL = 2;
    private final int three = 3;
    private final double mean = 2;
    private final double sumSq = 18;
    private final double sum = 8;
    private final double var = 0.666666666666666666667;
    private final double std = JdkMath.sqrt(var);
    private final double n = 4;
    private final double min = 1;
    private final double max = 3;
    private final double tolerance = 10E-15;

    protected SummaryStatistics createSummaryStatistics() {
        return new SummaryStatistics();
    }

    @Test
    public void testEmpty() {
        final SummaryStatistics stats = createSummaryStatistics();

        final double[] x = {};
        Assertions.assertEquals(StatUtils.sum(x), stats.getSum());
        Assertions.assertEquals(StatUtils.mean(x), stats.getMean());
        final double v = StatUtils.variance(x);
        Assertions.assertEquals(JdkMath.sqrt(v), stats.getStandardDeviation());
        Assertions.assertEquals(v, stats.getVariance());
        Assertions.assertEquals(StatUtils.max(x), stats.getMax());
        Assertions.assertEquals(StatUtils.min(x), stats.getMin());
    }

    /** test stats */
    @Test
    public void testStats() {
        SummaryStatistics u = createSummaryStatistics();
        Assert.assertEquals("total count",0,u.getN(),tolerance);
        u.addValue(one);
        u.addValue(twoF);
        u.addValue(twoL);
        u.addValue(three);
        Assert.assertEquals("N",n,u.getN(),tolerance);
        Assert.assertEquals("sum",sum,u.getSum(),tolerance);
        Assert.assertEquals("var",var,u.getVariance(),tolerance);
        Assert.assertEquals("std",std,u.getStandardDeviation(),tolerance);
        Assert.assertEquals("mean",mean,u.getMean(),tolerance);
        Assert.assertEquals("min",min,u.getMin(),tolerance);
        Assert.assertEquals("max",max,u.getMax(),tolerance);
        u.clear();
        Assert.assertEquals("total count",0,u.getN(),tolerance);
    }

    @Test
    public void testN0andN1Conditions() {
        SummaryStatistics u = createSummaryStatistics();
        Assert.assertTrue("Mean of n = 0 set should be NaN",
                Double.isNaN( u.getMean() ) );
        Assert.assertTrue("Standard Deviation of n = 0 set should be NaN",
                Double.isNaN( u.getStandardDeviation() ) );
        Assert.assertTrue("Variance of n = 0 set should be NaN",
                Double.isNaN(u.getVariance() ) );

        /* n=1 */
        u.addValue(one);
        Assert.assertEquals("mean should be one (n = 1)", one, u.getMean(), 0.0);
        Assert.assertEquals("Std should be zero (n = 1)", 0.0, u.getStandardDeviation(), 0.0);
        Assert.assertEquals("variance should be zero (n = 1)", 0.0, u.getVariance(), 0.0);

        /* n=2 */
        u.addValue(twoF);
        Assert.assertTrue("Std should not be zero (n = 2)",
                u.getStandardDeviation() != 0.0);
        Assert.assertTrue("variance should not be zero (n = 2)",
                u.getVariance() != 0.0);
    }

    @Test
    public void testNaNContracts() {
        SummaryStatistics u = createSummaryStatistics();
        Assert.assertTrue("sum not NaN",Double.isNaN(u.getSum()));
        Assert.assertTrue("mean not NaN",Double.isNaN(u.getMean()));
        Assert.assertTrue("std dev not NaN",Double.isNaN(u.getStandardDeviation()));
        Assert.assertTrue("var not NaN",Double.isNaN(u.getVariance()));
        Assert.assertTrue("max not NaN",Double.isNaN(u.getMax()));
        Assert.assertTrue("min not NaN",Double.isNaN(u.getMin()));

        u.addValue(1.0);

        Assert.assertEquals( "mean not expected", 1.0,
                u.getMean(), Double.MIN_VALUE);
        Assert.assertEquals( "variance not expected", 0.0,
                u.getVariance(), Double.MIN_VALUE);

        //FiXME: test all other NaN contract specs
    }

    @Test
    public void testGetSummary() {
        SummaryStatistics u = createSummaryStatistics();
        StatisticalSummary summary = u.getSummary();
        verifySummary(u, summary);
        u.addValue(1d);
        summary = u.getSummary();
        verifySummary(u, summary);
        u.addValue(2d);
        summary = u.getSummary();
        verifySummary(u, summary);
        u.addValue(2d);
        summary = u.getSummary();
        verifySummary(u, summary);
    }

    @Test
    public void testEqualsAndHashCode() {
        SummaryStatistics u = createSummaryStatistics();
        SummaryStatistics t = null;
        int emptyHash = u.hashCode();
        Assert.assertEquals("reflexive", u, u);
        Assert.assertNotEquals("non-null compared to null", u, t);
        Assert.assertFalse("wrong type", u.equals(Double.valueOf(0)));
        t = createSummaryStatistics();
        Assert.assertEquals("empty instances should be equal", t, u);
        Assert.assertEquals("empty instances should be equal", u, t);
        Assert.assertEquals("empty hash code", emptyHash, t.hashCode());

        // Add some data to u
        u.addValue(2d);
        u.addValue(1d);
        u.addValue(3d);
        u.addValue(4d);
        Assert.assertNotEquals("different n's should make instances not equal", t, u);
        Assert.assertNotEquals("different n's should make instances not equal", u, t);
        Assert.assertTrue("different n's should make hash codes different",
                u.hashCode() != t.hashCode());

        //Add data in same order to t
        t.addValue(2d);
        t.addValue(1d);
        t.addValue(3d);
        t.addValue(4d);
        Assert.assertEquals("summaries based on same data should be equal", t, u);
        Assert.assertEquals("summaries based on same data should be equal", u, t);
        Assert.assertEquals("summaries based on same data should have same hash codes",
                u.hashCode(), t.hashCode());

        // Clear and make sure summaries are indistinguishable from empty summary
        u.clear();
        t.clear();
        Assert.assertEquals("empty instances should be equal", t, u);
        Assert.assertEquals("empty instances should be equal", u, t);
        Assert.assertEquals("empty hash code", emptyHash, t.hashCode());
        Assert.assertEquals("empty hash code", emptyHash, u.hashCode());
    }

    @Test
    public void testCopy() {
        SummaryStatistics u = createSummaryStatistics();
        u.addValue(2d);
        u.addValue(1d);
        u.addValue(3d);
        u.addValue(4d);
        SummaryStatistics v = new SummaryStatistics(u);
        Assert.assertEquals(u, v);
        Assert.assertEquals(v, u);

        // Make sure both behave the same with additional values added
        u.addValue(7d);
        u.addValue(9d);
        u.addValue(11d);
        u.addValue(23d);
        v.addValue(7d);
        v.addValue(9d);
        v.addValue(11d);
        v.addValue(23d);
        Assert.assertEquals(u, v);
        Assert.assertEquals(v, u);

        // Check implementation pointers are preserved
        u.clear();
        u.setSumImpl(new SumStat());
        SummaryStatistics.copy(u,v);
        // This copy should be functionally distinct (i.e. no shared state) but
        // the implementation type should be the same.
        Assert.assertNotSame(u.getSumImpl(), v.getSumImpl());
        Assert.assertEquals(u.getSumImpl().getClass(), v.getSumImpl().getClass());
    }

    private void verifySummary(SummaryStatistics u, StatisticalSummary s) {
        Assert.assertEquals("N",s.getN(),u.getN());
        TestUtils.assertEquals("sum",s.getSum(),u.getSum(),tolerance);
        TestUtils.assertEquals("var",s.getVariance(),u.getVariance(),tolerance);
        TestUtils.assertEquals("std",s.getStandardDeviation(),u.getStandardDeviation(),tolerance);
        TestUtils.assertEquals("mean",s.getMean(),u.getMean(),tolerance);
        TestUtils.assertEquals("min",s.getMin(),u.getMin(),tolerance);
        TestUtils.assertEquals("max",s.getMax(),u.getMax(),tolerance);
    }

    @Test
    public void testSetterInjection() {
        SummaryStatistics u = createSummaryStatistics();
        u.setMeanImpl(new SumStat());
        u.addValue(1);
        u.addValue(3);
        Assert.assertEquals(4, u.getMean(), 1E-14);
        u.clear();
        u.addValue(1);
        u.addValue(2);
        Assert.assertEquals(3, u.getMean(), 1E-14);
        u.clear();
        u.setMeanImpl(new SumStat()); // OK after clear
    }

    @Test
    public void testSetterIllegalState() {
        SummaryStatistics u = createSummaryStatistics();
        u.addValue(1);
        u.addValue(3);
        try {
            u.setMeanImpl(new SumStat());
            Assert.fail("Expecting MathIllegalStateException");
        } catch (MathIllegalStateException ex) {
            // expected
        }
    }

    /**
     * Test when all the default implementations are overridden.
     */
    @Test
    public void testSetterAll() {
        final SummaryStatistics u = createSummaryStatistics();
        Assertions.assertThrows(NullPointerException.class, () -> u.setSumImpl(null));
        Assertions.assertThrows(NullPointerException.class, () -> u.setMinImpl(null));
        Assertions.assertThrows(NullPointerException.class, () -> u.setMaxImpl(null));
        Assertions.assertThrows(NullPointerException.class, () -> u.setMeanImpl(null));
        Assertions.assertThrows(NullPointerException.class, () -> u.setVarianceImpl(null));
        // Distinct implementations
        u.setSumImpl(new SumStat(1));
        u.setMinImpl(new SumStat(2));
        u.setMaxImpl(new SumStat(3));
        u.setMeanImpl(new SumStat(4));
        u.setVarianceImpl(new SumStat(5));
        u.addValue(1);
        Assertions.assertEquals(2, u.getSum());
        Assertions.assertEquals(3, u.getMin());
        Assertions.assertEquals(4, u.getMax());
        Assertions.assertEquals(5, u.getMean());
        Assertions.assertEquals(6, u.getVariance());
        // Test getters return the correct implementation
        Assertions.assertEquals(2, u.getSumImpl().getResult());
        Assertions.assertEquals(3, u.getMinImpl().getResult());
        Assertions.assertEquals(4, u.getMaxImpl().getResult());
        Assertions.assertEquals(5, u.getMeanImpl().getResult());
        Assertions.assertEquals(6, u.getVarianceImpl().getResult());
        // Test copy
        final SummaryStatistics v = u.copy();
        Assertions.assertEquals(2, v.getSum());
        Assertions.assertEquals(3, v.getMin());
        Assertions.assertEquals(4, v.getMax());
        Assertions.assertEquals(5, v.getMean());
        Assertions.assertEquals(6, v.getVariance());
        // Test the return NaN contract when empty
        u.clear();
        Assertions.assertEquals(Double.NaN, u.getSum());
        Assertions.assertEquals(Double.NaN, u.getMin());
        Assertions.assertEquals(Double.NaN, u.getMax());
        Assertions.assertEquals(Double.NaN, u.getMean());
        Assertions.assertEquals(Double.NaN, u.getVariance());
        // Test refilling
        u.addValue(1);
        Assertions.assertEquals(1, u.getSum());
        Assertions.assertEquals(1, u.getMin());
        Assertions.assertEquals(1, u.getMax());
        Assertions.assertEquals(1, u.getMean());
        Assertions.assertEquals(1, u.getVariance());
    }

    /**
     * JIRA: MATH-691.
     * Setting the variance implementation causes the StandardDevitaion to be NaN.
     */
    @Test
    public void testOverrideVarianceWithMathClass() {
        double[] scores = {1, 2, 3, 4};
        SummaryStatistics stats = new SummaryStatistics();
        stats.setVarianceImpl(new SumStat());
        for(double i : scores) {
          stats.addValue(i);
        }
        final double expected = new SumStat().evaluate(scores);
        Assert.assertEquals(expected, stats.getVariance(), 0);
        Assert.assertEquals(JdkMath.sqrt(expected), stats.getStandardDeviation(), 0);
    }

    @Test
    public void testOverrideMeanWithMathClass() {
        double[] scores = {1, 2, 3, 4};
        SummaryStatistics stats = new SummaryStatistics();
        stats.setMeanImpl(new SumStat());
        for(double i : scores) {
          stats.addValue(i);
        }
        final double expected = new SumStat().evaluate(scores);
        Assert.assertEquals(expected, stats.getMean(), 0);
    }

    @Test
    public void testToString() {
        SummaryStatistics u = createSummaryStatistics();
        for (int i = 0; i < 5; i++) {
            u.addValue(i);
        }
        final String[] labels = {"min", "max", "sum", "variance", "standard deviation"};
        final double[] values = {u.getMin(), u.getMax(), u.getSum(),
                u.getVariance(), u.getStandardDeviation()};
        final String toString = u.toString();
        Assert.assertTrue(toString.indexOf("n: " + u.getN()) > 0); // getN() returns a long
        for (int i = 0; i < values.length; i++) {
            Assert.assertTrue(toString.indexOf(labels[i] + ": " + String.valueOf(values[i])) > 0);
        }
    }

    private static final class SumStat implements StorelessUnivariateStatistic {
        private double s = 0;

        /** Create an instance. */
        SumStat() {}

        /**
         * Create an instance.
         * 
         * @param sum the sum
         */
        SumStat(double sum) {
            s = sum;
        }

        @Override
        public double evaluate(double[] values) throws MathIllegalArgumentException {
            double s = 0;
            for (final double x : values) {
                s += x;
            }
            return s;
        }

        @Override
        public double evaluate(double[] values, int begin, int length) throws MathIllegalArgumentException {
            throw new IllegalStateException();
        }

        @Override
        public void increment(double d) {
            s += d;
        }

        @Override
        public void incrementAll(double[] values) throws MathIllegalArgumentException {
            throw new IllegalStateException();
        }

        @Override
        public void incrementAll(double[] values, int start, int length) throws MathIllegalArgumentException {
            throw new IllegalStateException();
        }

        @Override
        public double getResult() {
            return s;
        }

        @Override
        public long getN() {
            throw new IllegalStateException();
        }

        @Override
        public void clear() {
            s = 0;
        }

        @Override
        public StorelessUnivariateStatistic copy() {
            final SumStat r = new SumStat();
            r.s = s;
            return r;
        } 
    }
}
