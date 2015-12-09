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
 */

public final class StatUtilsTest {

    private static final double ONE = 1;
    private static final float  TWO = 2;
    private static final int    THREE = 3;
    private static final double MEAN = 2;
    private static final double SUMSQ = 18;
    private static final double SUM = 8;
    private static final double VAR = 0.666666666666666666667;
    private static final double MIN = 1;
    private static final double MAX = 3;
    private static final double TOLERANCE = 10E-15;
    private static final double NAN = Double.NaN;

    /** test stats */
    @Test
    public void testStats() {
        double[] values = new double[] { ONE, TWO, TWO, THREE };
        Assert.assertEquals("sum", SUM, StatUtils.sum(values), TOLERANCE);
        Assert.assertEquals("sumsq", SUMSQ, StatUtils.sumSq(values), TOLERANCE);
        Assert.assertEquals("var", VAR, StatUtils.variance(values), TOLERANCE);
        Assert.assertEquals("var with mean", VAR, StatUtils.variance(values, MEAN), TOLERANCE);
        Assert.assertEquals("mean", MEAN, StatUtils.mean(values), TOLERANCE);
        Assert.assertEquals("min", MIN, StatUtils.min(values), TOLERANCE);
        Assert.assertEquals("max", MAX, StatUtils.max(values), TOLERANCE);
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

        values = new double[] { ONE };

        Assert.assertTrue(
            "Mean of n = 1 set should be value of single item n1",
            StatUtils.mean(values) == ONE);
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
        TestUtils.assertEquals(0, StatUtils.sumSq(x), TOLERANCE);
        TestUtils.assertEquals(0, StatUtils.sumSq(x, 0, 0), TOLERANCE);

        // test one
        x = new double[] {TWO};
        TestUtils.assertEquals(4, StatUtils.sumSq(x), TOLERANCE);
        TestUtils.assertEquals(4, StatUtils.sumSq(x, 0, 1), TOLERANCE);

        // test many
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(18, StatUtils.sumSq(x), TOLERANCE);
        TestUtils.assertEquals(8, StatUtils.sumSq(x, 1, 2), TOLERANCE);
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
        TestUtils.assertEquals(1, StatUtils.product(x), TOLERANCE);
        TestUtils.assertEquals(1, StatUtils.product(x, 0, 0), TOLERANCE);

        // test one
        x = new double[] {TWO};
        TestUtils.assertEquals(TWO, StatUtils.product(x), TOLERANCE);
        TestUtils.assertEquals(TWO, StatUtils.product(x, 0, 1), TOLERANCE);

        // test many
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(12, StatUtils.product(x), TOLERANCE);
        TestUtils.assertEquals(4, StatUtils.product(x, 1, 2), TOLERANCE);
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
        TestUtils.assertEquals(0, StatUtils.sumLog(x), TOLERANCE);
        TestUtils.assertEquals(0, StatUtils.sumLog(x, 0, 0), TOLERANCE);

        // test one
        x = new double[] {TWO};
        TestUtils.assertEquals(FastMath.log(TWO), StatUtils.sumLog(x), TOLERANCE);
        TestUtils.assertEquals(FastMath.log(TWO), StatUtils.sumLog(x, 0, 1), TOLERANCE);

        // test many
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(FastMath.log(ONE) + 2.0 * FastMath.log(TWO) + FastMath.log(THREE), StatUtils.sumLog(x), TOLERANCE);
        TestUtils.assertEquals(2.0 * FastMath.log(TWO), StatUtils.sumLog(x, 1, 2), TOLERANCE);
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
        TestUtils.assertEquals(Double.NaN, StatUtils.mean(x, 0, 0), TOLERANCE);

        // test one
        x = new double[] {TWO};
        TestUtils.assertEquals(TWO, StatUtils.mean(x, 0, 1), TOLERANCE);

        // test many
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(2.5, StatUtils.mean(x, 2, 2), TOLERANCE);
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
        TestUtils.assertEquals(Double.NaN, StatUtils.variance(x, 0, 0), TOLERANCE);

        // test one
        x = new double[] {TWO};
        TestUtils.assertEquals(0.0, StatUtils.variance(x, 0, 1), TOLERANCE);

        // test many
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(0.5, StatUtils.variance(x, 2, 2), TOLERANCE);

        // test precomputed mean
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(0.5, StatUtils.variance(x,2.5, 2, 2), TOLERANCE);
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
        TestUtils.assertEquals(Double.NaN, StatUtils.populationVariance(x, 0, 0), TOLERANCE);

        // test one
        x = new double[] {TWO};
        TestUtils.assertEquals(0.0, StatUtils.populationVariance(x, 0, 1), TOLERANCE);

        // test many
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(0.25, StatUtils.populationVariance(x, 0, 2), TOLERANCE);

        // test precomputed mean
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(0.25, StatUtils.populationVariance(x, 2.5, 2, 2), TOLERANCE);
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
        TestUtils.assertEquals(Double.NaN, StatUtils.max(x, 0, 0), TOLERANCE);

        // test one
        x = new double[] {TWO};
        TestUtils.assertEquals(TWO, StatUtils.max(x, 0, 1), TOLERANCE);

        // test many
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(THREE, StatUtils.max(x, 1, 3), TOLERANCE);

        // test first nan is ignored
        x = new double[] {NAN, TWO, THREE};
        TestUtils.assertEquals(THREE, StatUtils.max(x), TOLERANCE);

        // test middle nan is ignored
        x = new double[] {ONE, NAN, THREE};
        TestUtils.assertEquals(THREE, StatUtils.max(x), TOLERANCE);

        // test last nan is ignored
        x = new double[] {ONE, TWO, NAN};
        TestUtils.assertEquals(TWO, StatUtils.max(x), TOLERANCE);

        // test all nan returns nan
        x = new double[] {NAN, NAN, NAN};
        TestUtils.assertEquals(NAN, StatUtils.max(x), TOLERANCE);
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
        TestUtils.assertEquals(Double.NaN, StatUtils.min(x, 0, 0), TOLERANCE);

        // test one
        x = new double[] {TWO};
        TestUtils.assertEquals(TWO, StatUtils.min(x, 0, 1), TOLERANCE);

        // test many
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(TWO, StatUtils.min(x, 1, 3), TOLERANCE);

        // test first nan is ignored
        x = new double[] {NAN, TWO, THREE};
        TestUtils.assertEquals(TWO, StatUtils.min(x), TOLERANCE);

        // test middle nan is ignored
        x = new double[] {ONE, NAN, THREE};
        TestUtils.assertEquals(ONE, StatUtils.min(x), TOLERANCE);

        // test last nan is ignored
        x = new double[] {ONE, TWO, NAN};
        TestUtils.assertEquals(ONE, StatUtils.min(x), TOLERANCE);

        // test all nan returns nan
        x = new double[] {NAN, NAN, NAN};
        TestUtils.assertEquals(NAN, StatUtils.min(x), TOLERANCE);
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
        TestUtils.assertEquals(Double.NaN, StatUtils.percentile(x, 25), TOLERANCE);
        TestUtils.assertEquals(Double.NaN, StatUtils.percentile(x, 0, 0, 25), TOLERANCE);

        // test one
        x = new double[] {TWO};
        TestUtils.assertEquals(TWO, StatUtils.percentile(x, 25), TOLERANCE);
        TestUtils.assertEquals(TWO, StatUtils.percentile(x, 0, 1, 25), TOLERANCE);

        // test many
        x = new double[] {ONE, TWO, TWO, THREE};
        TestUtils.assertEquals(2.5, StatUtils.percentile(x, 70), TOLERANCE);
        TestUtils.assertEquals(2.5, StatUtils.percentile(x, 1, 3, 62.5), TOLERANCE);
    }

    @Test
    public void testDifferenceStats() {
        double sample1[] = {1d, 2d, 3d, 4d};
        double sample2[] = {1d, 3d, 4d, 2d};
        double diff[] = {0d, -1d, -1d, 2d};
        double small[] = {1d, 4d};
        double meanDifference = StatUtils.meanDifference(sample1, sample2);
        Assert.assertEquals(StatUtils.sumDifference(sample1, sample2), StatUtils.sum(diff), TOLERANCE);
        Assert.assertEquals(meanDifference, StatUtils.mean(diff), TOLERANCE);
        Assert.assertEquals(StatUtils.varianceDifference(sample1, sample2, meanDifference),
                StatUtils.variance(diff), TOLERANCE);
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
        double expectedSample[] = { -25 / FastMath.sqrt(1250), 25 / FastMath.sqrt(1250) };
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
            sample[i] = FastMath.random();
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

    @Test
    public void testMode() {
        final double[] singleMode = {0, 1, 0, 2, 7, 11, 12};
        final double[] modeSingle = StatUtils.mode(singleMode);
        Assert.assertEquals(0, modeSingle[0], Double.MIN_VALUE);
        Assert.assertEquals(1, modeSingle.length);

        final double[] twoMode = {0, 1, 2, 0, 2, 3, 7, 11};
        final double[] modeDouble = StatUtils.mode(twoMode);
        Assert.assertEquals(0, modeDouble[0], Double.MIN_VALUE);
        Assert.assertEquals(2, modeDouble[1], Double.MIN_VALUE);
        Assert.assertEquals(2, modeDouble.length);

        final double[] nanInfested = {0, 0, 0, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 2, 2, 2, 3, 5};
        final double[] modeNan = StatUtils.mode(nanInfested);
        Assert.assertEquals(0, modeNan[0], Double.MIN_VALUE);
        Assert.assertEquals(2, modeNan[1], Double.MIN_VALUE);
        Assert.assertEquals(2, modeNan.length);

        final double[] infInfested = {0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 2, 2, 3, 5};
        final double[] modeInf = StatUtils.mode(infInfested);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, modeInf[0], Double.MIN_VALUE);
        Assert.assertEquals(0, modeInf[1], Double.MIN_VALUE);
        Assert.assertEquals(2, modeInf[2], Double.MIN_VALUE);
        Assert.assertEquals(Double.POSITIVE_INFINITY, modeInf[3], Double.MIN_VALUE);
        Assert.assertEquals(4, modeInf.length);

        final double[] noData = {};
        final double[] modeNodata = StatUtils.mode(noData);
        Assert.assertEquals(0, modeNodata.length);

        final double[] nansOnly = {Double.NaN, Double.NaN};
        final double[] modeNansOnly = StatUtils.mode(nansOnly);
        Assert.assertEquals(0, modeNansOnly.length);

        final double[] nullArray = null;
        try {
            StatUtils.mode(nullArray);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // Expected
        }
    }

}
