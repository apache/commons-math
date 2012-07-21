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
package org.apache.commons.math3.stat;


import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link StatUtils} class.
 * @version $Id$
 */

public final class StatUtilsTest {

    private double one = 1;
    private float two = 2;
    private int three = 3;
    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double min = 1;
    private double max = 3;
    private double tolerance = 10E-15;
    private double nan = Double.NaN;

    /** test stats */
    @Test
    public void testStats() {
        double[] values = new double[] { one, two, two, three };
        Assert.assertEquals("sum", sum, StatUtils.sum(values), tolerance);
        Assert.assertEquals("sumsq", sumSq, StatUtils.sumSq(values), tolerance);
        Assert.assertEquals("var", var, StatUtils.variance(values), tolerance);
        Assert.assertEquals("var with mean", var, StatUtils.variance(values, mean), tolerance);
        Assert.assertEquals("mean", mean, StatUtils.mean(values), tolerance);
        Assert.assertEquals("min", min, StatUtils.min(values), tolerance);
        Assert.assertEquals("max", max, StatUtils.max(values), tolerance);
    }

    @Test
    public void testN0andN1Conditions() {
        double[] values = new double[0];

        Assert.assertTrue(
            "Mean of n = 0 set should be NaN",
            Double.isNaN(StatUtils.mean(values)));
        Assert.assertTrue(
            "Variance of n = 0 set should be NaN",
            Double.isNaN(StatUtils.variance(values)));

        values = new double[] { one };

        Assert.assertTrue(
            "Mean of n = 1 set should be value of single item n1",
            StatUtils.mean(values) == one);
        Assert.assertTrue(
            "Variance of n = 1 set should be zero",
            StatUtils.variance(values) == 0);
    }

    @Test
    public void testArrayIndexConditions() {
        double[] values = { 1.0, 2.0, 3.0, 4.0 };

        Assert.assertEquals(
            "Sum not expected",
            5.0,
            StatUtils.sum(values, 1, 2),
            Double.MIN_VALUE);
        Assert.assertEquals(
            "Sum not expected",
            3.0,
            StatUtils.sum(values, 0, 2),
            Double.MIN_VALUE);
        Assert.assertEquals(
            "Sum not expected",
            7.0,
            StatUtils.sum(values, 2, 2),
            Double.MIN_VALUE);

        try {
            StatUtils.sum(values, 2, 3);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            // expected
        }

        try {
            StatUtils.sum(values, -1, 2);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            // expected
        }

    }

    @Test
    public void testSumSq() {
        double[] x = null;

        // test null
        try {
            StatUtils.sumSq(x);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        try {
            StatUtils.sumSq(x, 0, 4);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        // test empty
        x = new double[] {};
        TestUtils.assertEquals(0, StatUtils.sumSq(x), tolerance);
        TestUtils.assertEquals(0, StatUtils.sumSq(x, 0, 0), tolerance);

        // test one
        x = new double[] {two};
        TestUtils.assertEquals(4, StatUtils.sumSq(x), tolerance);
        TestUtils.assertEquals(4, StatUtils.sumSq(x, 0, 1), tolerance);

        // test many
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(18, StatUtils.sumSq(x), tolerance);
        TestUtils.assertEquals(8, StatUtils.sumSq(x, 1, 2), tolerance);
    }

    @Test
    public void testProduct() {
        double[] x = null;

        // test null
        try {
            StatUtils.product(x);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        try {
            StatUtils.product(x, 0, 4);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        // test empty
        x = new double[] {};
        TestUtils.assertEquals(1, StatUtils.product(x), tolerance);
        TestUtils.assertEquals(1, StatUtils.product(x, 0, 0), tolerance);

        // test one
        x = new double[] {two};
        TestUtils.assertEquals(two, StatUtils.product(x), tolerance);
        TestUtils.assertEquals(two, StatUtils.product(x, 0, 1), tolerance);

        // test many
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(12, StatUtils.product(x), tolerance);
        TestUtils.assertEquals(4, StatUtils.product(x, 1, 2), tolerance);
    }

    @Test
    public void testSumLog() {
        double[] x = null;

        // test null
        try {
            StatUtils.sumLog(x);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        try {
            StatUtils.sumLog(x, 0, 4);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        // test empty
        x = new double[] {};
        TestUtils.assertEquals(0, StatUtils.sumLog(x), tolerance);
        TestUtils.assertEquals(0, StatUtils.sumLog(x, 0, 0), tolerance);

        // test one
        x = new double[] {two};
        TestUtils.assertEquals(FastMath.log(two), StatUtils.sumLog(x), tolerance);
        TestUtils.assertEquals(FastMath.log(two), StatUtils.sumLog(x, 0, 1), tolerance);

        // test many
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(FastMath.log(one) + 2.0 * FastMath.log(two) + FastMath.log(three), StatUtils.sumLog(x), tolerance);
        TestUtils.assertEquals(2.0 * FastMath.log(two), StatUtils.sumLog(x, 1, 2), tolerance);
    }

    @Test
    public void testMean() {
        double[] x = null;

        try {
            StatUtils.mean(x, 0, 4);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        // test empty
        x = new double[] {};
        TestUtils.assertEquals(Double.NaN, StatUtils.mean(x, 0, 0), tolerance);

        // test one
        x = new double[] {two};
        TestUtils.assertEquals(two, StatUtils.mean(x, 0, 1), tolerance);

        // test many
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(2.5, StatUtils.mean(x, 2, 2), tolerance);
    }

    @Test
    public void testVariance() {
        double[] x = null;

        try {
            StatUtils.variance(x, 0, 4);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        // test empty
        x = new double[] {};
        TestUtils.assertEquals(Double.NaN, StatUtils.variance(x, 0, 0), tolerance);

        // test one
        x = new double[] {two};
        TestUtils.assertEquals(0.0, StatUtils.variance(x, 0, 1), tolerance);

        // test many
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(0.5, StatUtils.variance(x, 2, 2), tolerance);

        // test precomputed mean
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(0.5, StatUtils.variance(x,2.5, 2, 2), tolerance);
    }
    
    @Test
    public void testPopulationVariance() {
        double[] x = null;

        try {
            StatUtils.variance(x, 0, 4);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        // test empty
        x = new double[] {};
        TestUtils.assertEquals(Double.NaN, StatUtils.populationVariance(x, 0, 0), tolerance);

        // test one
        x = new double[] {two};
        TestUtils.assertEquals(0.0, StatUtils.populationVariance(x, 0, 1), tolerance);

        // test many
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(0.25, StatUtils.populationVariance(x, 0, 2), tolerance);

        // test precomputed mean
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(0.25, StatUtils.populationVariance(x, 2.5, 2, 2), tolerance);
    }


    @Test
    public void testMax() {
        double[] x = null;

        try {
            StatUtils.max(x, 0, 4);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        // test empty
        x = new double[] {};
        TestUtils.assertEquals(Double.NaN, StatUtils.max(x, 0, 0), tolerance);

        // test one
        x = new double[] {two};
        TestUtils.assertEquals(two, StatUtils.max(x, 0, 1), tolerance);

        // test many
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(three, StatUtils.max(x, 1, 3), tolerance);

        // test first nan is ignored
        x = new double[] {nan, two, three};
        TestUtils.assertEquals(three, StatUtils.max(x), tolerance);

        // test middle nan is ignored
        x = new double[] {one, nan, three};
        TestUtils.assertEquals(three, StatUtils.max(x), tolerance);

        // test last nan is ignored
        x = new double[] {one, two, nan};
        TestUtils.assertEquals(two, StatUtils.max(x), tolerance);

        // test all nan returns nan
        x = new double[] {nan, nan, nan};
        TestUtils.assertEquals(nan, StatUtils.max(x), tolerance);
    }

    @Test
    public void testMin() {
        double[] x = null;

        try {
            StatUtils.min(x, 0, 4);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        // test empty
        x = new double[] {};
        TestUtils.assertEquals(Double.NaN, StatUtils.min(x, 0, 0), tolerance);

        // test one
        x = new double[] {two};
        TestUtils.assertEquals(two, StatUtils.min(x, 0, 1), tolerance);

        // test many
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(two, StatUtils.min(x, 1, 3), tolerance);

        // test first nan is ignored
        x = new double[] {nan, two, three};
        TestUtils.assertEquals(two, StatUtils.min(x), tolerance);

        // test middle nan is ignored
        x = new double[] {one, nan, three};
        TestUtils.assertEquals(one, StatUtils.min(x), tolerance);

        // test last nan is ignored
        x = new double[] {one, two, nan};
        TestUtils.assertEquals(one, StatUtils.min(x), tolerance);

        // test all nan returns nan
        x = new double[] {nan, nan, nan};
        TestUtils.assertEquals(nan, StatUtils.min(x), tolerance);
    }

    @Test
    public void testPercentile() {
        double[] x = null;

        // test null
        try {
            StatUtils.percentile(x, .25);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        try {
            StatUtils.percentile(x, 0, 4, 0.25);
            Assert.fail("null is not a valid data array.");
        } catch (MathIllegalArgumentException ex) {
            // success
        }

        // test empty
        x = new double[] {};
        TestUtils.assertEquals(Double.NaN, StatUtils.percentile(x, 25), tolerance);
        TestUtils.assertEquals(Double.NaN, StatUtils.percentile(x, 0, 0, 25), tolerance);

        // test one
        x = new double[] {two};
        TestUtils.assertEquals(two, StatUtils.percentile(x, 25), tolerance);
        TestUtils.assertEquals(two, StatUtils.percentile(x, 0, 1, 25), tolerance);

        // test many
        x = new double[] {one, two, two, three};
        TestUtils.assertEquals(2.5, StatUtils.percentile(x, 70), tolerance);
        TestUtils.assertEquals(2.5, StatUtils.percentile(x, 1, 3, 62.5), tolerance);
    }

    @Test
    public void testDifferenceStats() {
        double sample1[] = {1d, 2d, 3d, 4d};
        double sample2[] = {1d, 3d, 4d, 2d};
        double diff[] = {0d, -1d, -1d, 2d};
        double small[] = {1d, 4d};
        double meanDifference = StatUtils.meanDifference(sample1, sample2);
        Assert.assertEquals(StatUtils.sumDifference(sample1, sample2), StatUtils.sum(diff), tolerance);
        Assert.assertEquals(meanDifference, StatUtils.mean(diff), tolerance);
        Assert.assertEquals(StatUtils.varianceDifference(sample1, sample2, meanDifference),
                StatUtils.variance(diff), tolerance);
        try {
            StatUtils.meanDifference(sample1, small);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            StatUtils.varianceDifference(sample1, small, meanDifference);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            double[] single = {1.0};
            StatUtils.varianceDifference(single, single, meanDifference);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testGeometricMean() {
        double[] test = null;
        try {
            StatUtils.geometricMean(test);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        test = new double[] {2, 4, 6, 8};
        Assert.assertEquals(FastMath.exp(0.25d * StatUtils.sumLog(test)),
                StatUtils.geometricMean(test), Double.MIN_VALUE);
        Assert.assertEquals(FastMath.exp(0.5 * StatUtils.sumLog(test, 0, 2)),
                StatUtils.geometricMean(test, 0, 2), Double.MIN_VALUE);
    }
    
    
    /**
     * Run the test with the values 50 and 100 and assume standardized values    
     */

    @Test
    public void testNormalize1() {
        double sample[] = { 50, 100 };
        double expectedSample[] = { -25 / Math.sqrt(1250), 25 / Math.sqrt(1250) };
        double[] out = StatUtils.normalize(sample);
        for (int i = 0; i < out.length; i++) {
            Assert.assertTrue(Precision.equals(out[i], expectedSample[i], 1));
        }

    }

    /**
     * Run with 77 random values, assuming that the outcome has a mean of 0 and a standard deviation of 1 with a
     * precision of 1E-10.
     */

    @Test
    public void testNormalize2() {
        // create an sample with 77 values    
        int length = 77;
        double sample[] = new double[length];
        for (int i = 0; i < length; i++) {
            sample[i] = Math.random();
        }
        // normalize this sample
        double standardizedSample[] = StatUtils.normalize(sample);

        DescriptiveStatistics stats = new DescriptiveStatistics();
        // Add the data from the array
        for (int i = 0; i < length; i++) {
            stats.addValue(standardizedSample[i]);
        }
        // the calculations do have a limited precision    
        double distance = 1E-10;
        // check the mean an standard deviation
        Assert.assertEquals(0.0, stats.getMean(), distance);
        Assert.assertEquals(1.0, stats.getStandardDeviation(), distance);

    }
    
}
