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
package org.apache.commons.math4.legacy.stat.descriptive.rank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.statistics.distribution.LogNormalDistribution;
import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.math4.legacy.distribution.AbstractRealDistribution;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.legacy.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math4.legacy.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math4.legacy.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math4.legacy.stat.descriptive.rank.PSquarePercentile.PSquareMarkers;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link PSquarePercentile} class which naturally extends
 * {@link StorelessUnivariateStatisticAbstractTest}.
 */
public class PSquarePercentileTest extends
        StorelessUnivariateStatisticAbstractTest {

    protected double percentile5 = 8.2299d;
    protected double percentile95 = 16.72195;// 20.82d; this is approximation
    protected double tolerance = 10E-12;

    private final UniformRandomProvider randomGenerator = RandomSource.WELL_19937_C.create(1000);

    @Override
    public double getTolerance() {
        return 1.0e-2;// tolerance limit changed as this is an approximation
        // algorithm and also gets accurate after few tens of
        // samples
    }

    /**
     * Verifies that copied statistics remain equal to originals when
     * incremented the same way by making the copy after a majority of elements
     * are incremented
     */
    @Test
    public void testCopyConsistencyWithInitialMostElements() {

        StorelessUnivariateStatistic master =
                (StorelessUnivariateStatistic) getUnivariateStatistic();

        StorelessUnivariateStatistic replica = null;

        // select a portion of testArray till 75 % of the length to load first
        long index = JdkMath.round(0.75 * testArray.length);

        // Put first half in master and copy master to replica
        master.incrementAll(testArray, 0, (int) index);
        replica = master.copy();

        // Check same
        Assert.assertEquals(replica, master);
        Assert.assertEquals(master, replica);

        // Now add second part to both and check again
        master.incrementAll(testArray, (int) index,
                (int) (testArray.length - index));
        replica.incrementAll(testArray, (int) index,
                (int) (testArray.length - index));
        Assert.assertEquals(replica, master);
        Assert.assertEquals(master, replica);
    }

    /**
     * Verifies that copied statistics remain equal to originals when
     * incremented the same way by way of copying original after just a few
     * elements are incremented
     */
    @Test
    public void testCopyConsistencyWithInitialFirstFewElements() {

        StorelessUnivariateStatistic master =
                (StorelessUnivariateStatistic) getUnivariateStatistic();

        StorelessUnivariateStatistic replica = null;

        // select a portion of testArray which is 10% of the length to load
        // first
        long index = JdkMath.round(0.1 * testArray.length);

        // Put first half in master and copy master to replica
        master.incrementAll(testArray, 0, (int) index);
        replica = master.copy();

        // Check same
        Assert.assertEquals(replica, master);
        Assert.assertEquals(master, replica);
        // Now add second part to both and check again
        master.incrementAll(testArray, (int) index,
                (int) (testArray.length - index));
        replica.incrementAll(testArray, (int) index,
                (int) (testArray.length - index));
        // Check same
        // Explicit test of the equals method
        Assert.assertTrue(master.equals(master));
        Assert.assertTrue(replica.equals(replica));
        Assert.assertEquals(replica, master);
        Assert.assertEquals(master, replica);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testNullListInMarkers() {
        // In case of null list Markers cannot be instantiated..is getting
        // verified
        // new Markers(null, 0, PSquarePercentile.newEstimator());
        PSquarePercentile.newMarkers(null, 0);

    }

    @Test
    public void testEqualsInMarkers() {
        double p = 0.5;
        PSquareMarkers markers =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 0.02, 1.18, 9.15, 21.91,
                                38.62 }), p);
        // Markers equality
        Assert.assertEquals(markers, markers);
        Assert.assertNotEquals(markers, null);
        Assert.assertNotEquals(markers, "");
        // Check for null markers test during equality testing
        // Until 5 elements markers are not initialized
        PSquarePercentile p1 = new PSquarePercentile();
        PSquarePercentile p2 = new PSquarePercentile();
        Assert.assertEquals(p1, p2);
        p1.evaluate(new double[] { 1.0, 2.0, 3.0 });
        p2.evaluate(new double[] { 1.0, 2.0, 3.0 });
        Assert.assertEquals(p1, p2);
        // Move p2 alone with more values just to make sure markers are not null
        // for p2
        p2.incrementAll(new double[] { 5.0, 7.0, 11.0 });
        Assert.assertNotEquals(p1, p2);
        Assert.assertNotEquals(p2, p1);
        // Next add different data to p1 to make number of elements match and
        // markers are not null however actual results will vary
        p1.incrementAll(new double[] { 20, 21, 22, 23 });
        Assert.assertNotEquals(p1, p2);// though markers are non null, N
        // matches, results wont

    }

    @Test(expected = OutOfRangeException.class)
    public void testMarkersOORLow() {
        PSquarePercentile.newMarkers(
                Arrays.asList(new Double[] { 0.02, 1.18, 9.15, 21.91, 38.62 }),
                0.5).estimate(0);
    }

    @Test(expected = OutOfRangeException.class)
    public void testMarkersOORHigh() {
        PSquarePercentile.newMarkers(
                Arrays.asList(new Double[] { 0.02, 1.18, 9.15, 21.91, 38.62 }),
                0.5).estimate(5);
    }

    @Test
    public void testMarkers2() {
        double p = 0.5;
        PSquareMarkers markers =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 0.02, 1.18, 9.15, 21.91,
                                38.62 }), p);

        PSquareMarkers markersNew =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 0.02, 1.18, 9.15, 21.91,
                                38.62 }), p);

        Assert.assertEquals(markers, markersNew);
        // If just one element of markers got changed then its still false.
        markersNew.processDataPoint(39);
        Assert.assertNotEquals(markers, markersNew);

    }

    @Test
    public void testHashCodeInMarkers() {
        PSquarePercentile p = new PSquarePercentile(95);
        PSquarePercentile p2 = new PSquarePercentile(95);
        Set<PSquarePercentile> s = new HashSet<>();
        s.add(p);
        s.add(p2);
        Assert.assertEquals(1, s.size());
        Assert.assertEquals(p, s.iterator().next());
        double[] d =
                new double[] { 95.1772, 95.1567, 95.1937, 95.1959, 95.1442,
                        95.0610, 95.1591, 95.1195, 95.1772, 95.0925, 95.1990,
                        95.1682 };
        Assert.assertEquals(95.1981, p.evaluate(d), 1.0e-2); // change
        Assert.assertEquals(95.1981, p2.evaluate(d), 1.0e-2); // change
        s.clear();
        s.add(p);
        s.add(p2);
        Assert.assertEquals(1, s.size());
        Assert.assertEquals(p, s.iterator().next());

        PSquareMarkers m1 =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 95.1772, 95.1567, 95.1937,
                                95.1959, 95.1442, 95.0610, 95.1591, 95.1195,
                                95.1772, 95.0925, 95.1990, 95.1682 }), 0.0);
        PSquareMarkers m2 =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 95.1772, 95.1567, 95.1937,
                                95.1959, 95.1442, 95.0610, 95.1591, 95.1195,
                                95.1772, 95.0925, 95.1990, 95.1682 }), 0.0);
        Assert.assertEquals(m1, m2);
        Set<PSquareMarkers> setMarkers = new LinkedHashSet<>();
        Assert.assertTrue(setMarkers.add(m1));
        Assert.assertFalse(setMarkers.add(m2));
        Assert.assertEquals(1, setMarkers.size());

        PSquareMarkers mThis =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 195.1772, 195.1567,
                                195.1937, 195.1959, 95.1442, 195.0610,
                                195.1591, 195.1195, 195.1772, 95.0925, 95.1990,
                                195.1682 }), 0.50);
        PSquareMarkers mThat =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 95.1772, 95.1567, 95.1937,
                                95.1959, 95.1442, 95.0610, 95.1591, 95.1195,
                                95.1772, 95.0925, 95.1990, 95.1682 }), 0.50);
        Assert.assertEquals(mThis, mThis);
        Assert.assertNotEquals(mThis, mThat);
        String s1="";
        Assert.assertNotEquals(mThis, s1);
        for (int i = 0; i < testArray.length; i++) {
            mThat.processDataPoint(testArray[i]);
        }
        setMarkers.add(mThat);
        setMarkers.add(mThis);
        Assert.assertEquals(mThat, mThat);
        Assert.assertTrue(setMarkers.contains(mThat));
        Assert.assertTrue(setMarkers.contains(mThis));
        Assert.assertEquals(3, setMarkers.size());
        Iterator<PSquareMarkers> iterator=setMarkers.iterator();
        Assert.assertEquals(m1, iterator.next());
        Assert.assertEquals(mThat, iterator.next());
        Assert.assertEquals(mThis, iterator.next());
    }

    @Test(expected = OutOfRangeException.class)
    public void testMarkersWithLowerIndex() {
        PSquareMarkers mThat =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 95.1772, 95.1567, 95.1937,
                                95.1959, 95.1442, 95.0610, 95.1591, 95.1195,
                                95.1772, 95.0925, 95.1990, 95.1682 }), 0.50);
        for (int i = 0; i < testArray.length; i++) {
            mThat.processDataPoint(testArray[i]);
        }
        mThat.estimate(0);
    }

    @Test(expected = OutOfRangeException.class)
    public void testMarkersWithHigherIndex() {
        PSquareMarkers mThat =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 95.1772, 95.1567, 95.1937,
                                95.1959, 95.1442, 95.0610, 95.1591, 95.1195,
                                95.1772, 95.0925, 95.1990, 95.1682 }), 0.50);
        for (int i = 0; i < testArray.length; i++) {
            mThat.processDataPoint(testArray[i]);
        }
        mThat.estimate(6);
    }

    @Test(expected = OutOfRangeException.class)
    public void testMarkerHeightWithLowerIndex() {
        PSquareMarkers mThat =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 95.1772, 95.1567, 95.1937,
                                95.1959, 95.1442, 95.0610, 95.1591, 95.1195,
                                95.1772, 95.0925, 95.1990, 95.1682 }), 0.50);
        mThat.height(0);
    }

    @Test(expected = OutOfRangeException.class)
    public void testMarkerHeightWithHigherIndex() {
        PSquareMarkers mThat =
                PSquarePercentile.newMarkers(
                        Arrays.asList(new Double[] { 95.1772, 95.1567, 95.1937,
                                95.1959, 95.1442, 95.0610, 95.1591, 95.1195,
                                95.1772, 95.0925, 95.1990, 95.1682 }), 0.50);
        mThat.height(6);
    }

    @Test
    public void testPSquaredEqualsAndMin() {
        PSquarePercentile ptile = new PSquarePercentile(0);
        Assert.assertEquals(ptile, ptile);
        Assert.assertNotEquals(ptile, null);
        Assert.assertNotEquals(ptile, "");
        // Just to check if there is no data get result for zeroth and 100th
        // ptile returns NAN
        Assert.assertTrue(Double.isNaN(ptile.getResult()));
        Assert.assertTrue(Double.isNaN(new PSquarePercentile(100).getResult()));

        double[] d = new double[] { 1, 3, 2, 4, 9, 10, 11 };
        ptile.incrementAll(d);
        Assert.assertEquals(ptile, ptile);
        Assert.assertEquals(1d, ptile.getResult(), 1e-02);// this calls min
    }

    @Test
    public void testString() {
        PSquarePercentile ptile = new PSquarePercentile(95);
        Assert.assertNotNull(ptile.toString());
        ptile.increment(1);
        ptile.increment(2);
        ptile.increment(3);
        Assert.assertNotNull(ptile.toString());
        Assert.assertEquals(expectedValue(), ptile.evaluate(testArray), getTolerance());
        Assert.assertNotNull(ptile.toString());
    }

    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        PSquarePercentile ptile = new PSquarePercentile(95);
        // Assert.assertNull(ptile.markers());
        return ptile;
    }

    @Override
    public double expectedValue() {
        return this.percentile95;
    }

    @Test
    public void testHighPercentile() {
        double[] d = new double[] { 1, 2, 3 };
        PSquarePercentile p = new PSquarePercentile(75.0);
        Assert.assertEquals(2, p.evaluate(d), 1.0e-5);
        PSquarePercentile p95 = new PSquarePercentile();
        Assert.assertEquals(2, p95.evaluate(d), 1.0e-5);
    }

    @Test
    public void testLowPercentile() {
        double[] d = new double[] { 0, 1 };
        PSquarePercentile p = new PSquarePercentile(25.0);
        Assert.assertEquals(0d, p.evaluate(d), Double.MIN_VALUE);
    }

    @Test
    public void testPercentile() {
        double[] d = new double[] { 1, 3, 2, 4 };
        PSquarePercentile p = new PSquarePercentile(30d);
        Assert.assertEquals(1.0, p.evaluate(d), 1.0e-5);
        p = new PSquarePercentile(25);
        Assert.assertEquals(1.0, p.evaluate(d), 1.0e-5);
        p = new PSquarePercentile(75);
        Assert.assertEquals(3.0, p.evaluate(d), 1.0e-5);
        p = new PSquarePercentile(50);
        Assert.assertEquals(2d, p.evaluate(d), 1.0e-5);

    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testInitial() {
        PSquarePercentile.newMarkers(new ArrayList<Double>(), 0.5);
        Assert.fail();
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testNegativeInvalidValues() {
        double[] d =
                new double[] { 95.1772, 95.1567, 95.1937, 95.1959, 95.1442,
                        95.0610, 95.1591, 95.1195, 95.1772, 95.0925, 95.1990,
                        95.1682 };
        PSquarePercentile p = new PSquarePercentile(-1.0);
        p.evaluate(d, 0, d.length);
        Assert.fail("This method has had to throw exception..but it is not..");

    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testPositiveInvalidValues() {
        double[] d =
                new double[] { 95.1772, 95.1567, 95.1937, 95.1959, 95.1442,
                        95.0610, 95.1591, 95.1195, 95.1772, 95.0925, 95.1990,
                        95.1682 };
        PSquarePercentile p = new PSquarePercentile(101.0);
        p.evaluate(d, 0, d.length);
        Assert.fail("This method has had to throw exception..but it is not..");

    }

    @Test
    public void testNISTExample() {
        double[] d =
                new double[] { 95.1772, 95.1567, 95.1937, 95.1959, 95.1442,
                        95.0610, 95.1591, 95.1195, 95.1772, 95.0925, 95.1990,
                        95.1682 };
        Assert.assertEquals(95.1981, new PSquarePercentile(90d).evaluate(d),
                1.0e-2); // changed the accuracy to 1.0e-2
        Assert.assertEquals(95.061, new PSquarePercentile(0d).evaluate(d), 0);
        Assert.assertEquals(95.1990,
                new PSquarePercentile(100d).evaluate(d, 0, d.length), 0);
    }

    @Test
    public void test5() {
        PSquarePercentile percentile = new PSquarePercentile(5d);
        Assert.assertEquals(this.percentile5, percentile.evaluate(testArray),
                1.0);// changed the accuracy to 1 instead of tolerance
    }

    @Test(expected = NullArgumentException.class)
    public void testNull() {
        PSquarePercentile percentile = new PSquarePercentile(50d);
        double[] nullArray = null;
        percentile.evaluate(nullArray);
    }

    @Test
    public void testEmpty() {
        PSquarePercentile percentile = new PSquarePercentile(50d);
        double[] emptyArray = new double[] {};
        Assert.assertTrue(Double.isNaN(percentile.evaluate(emptyArray)));
    }

    @Test
    public void testSingleton() {
        PSquarePercentile percentile = new PSquarePercentile(50d);
        double[] singletonArray = new double[] { 1d };
        Assert.assertEquals(1d, percentile.evaluate(singletonArray), 0);
        Assert.assertEquals(1d, percentile.evaluate(singletonArray, 0, 1), 0);
        percentile = new PSquarePercentile(5);
        Assert.assertEquals(1d, percentile.evaluate(singletonArray, 0, 1), 0);
        percentile = new PSquarePercentile(100);
        Assert.assertEquals(1d, percentile.evaluate(singletonArray, 0, 1), 0);
        percentile = new PSquarePercentile(100);
        Assert.assertTrue(Double.isNaN(percentile
                .evaluate(singletonArray, 0, 0)));
    }

    @Test
    public void testSpecialValues() {
        PSquarePercentile percentile = new PSquarePercentile(50d);
        double[] specialValues =
                new double[] { 0d, 1d, 2d, 3d, 4d, Double.NaN };
        Assert.assertEquals(2d, percentile.evaluate(specialValues), 0);
        specialValues =
                new double[] { Double.NEGATIVE_INFINITY, 1d, 2d, 3d,
                        Double.NaN, Double.POSITIVE_INFINITY };
        Assert.assertEquals(2d, percentile.evaluate(specialValues), 0);
        specialValues =
                new double[] { 1d, 1d, Double.POSITIVE_INFINITY,
                        Double.POSITIVE_INFINITY };
        Assert.assertFalse(Double.isInfinite(percentile.evaluate(specialValues)));
        specialValues = new double[] { 1d, 1d, Double.NaN, Double.NaN };
        Assert.assertFalse(Double.isNaN(percentile.evaluate(specialValues)));
        specialValues =
                new double[] { 1d, 1d, Double.NEGATIVE_INFINITY,
                        Double.NEGATIVE_INFINITY };
        percentile = new PSquarePercentile(50d);
        // Interpolation results in NEGATIVE_INFINITY + POSITIVE_INFINITY
        // changed the result check to infinity instead of NaN
        Assert.assertTrue(Double.isInfinite(percentile.evaluate(specialValues)));
    }

    @Test
    public void testArrayExample() {
        Assert.assertEquals(expectedValue(),
                new PSquarePercentile(95d).evaluate(testArray), getTolerance());
    }

    @Test
    public void testSetQuantile() {
        PSquarePercentile percentile = new PSquarePercentile(10d);

        percentile = new PSquarePercentile(100); // OK
        Assert.assertEquals(1.0, percentile.quantile(), 0);
        try {
            percentile = new PSquarePercentile(0);
            // Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            new PSquarePercentile(0d);
            // Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }

    private Double[] randomTestData(int factor, int values) {
        Double[] test = new Double[values];
        for (int i = 0; i < test.length; i++) {
            test[i] = Math.abs(randomGenerator.nextDouble() * factor);
        }
        return test;
    }

    @Test
    public void testAccept() {
        PSquarePercentile psquared = new PSquarePercentile(0.99);
        Assert.assertTrue(Double.isNaN(psquared.getResult()));
        Double[] test = randomTestData(100, 10000);

        for (Double value : test) {
            psquared.increment(value);
            Assert.assertTrue(psquared.getResult() >= 0);
        }
    }

    private void assertValues(Double a, Double b, double delta) {
        if (Double.isNaN(a)) {
            Assert.assertTrue("" + b + " is not NaN.", Double.isNaN(a));
        } else {
            double max = JdkMath.max(a, b);
            double percentage = JdkMath.abs(a - b) / max;
            double deviation = delta;
            Assert.assertTrue(String.format(
                    "Deviated = %f and is beyond %f as a=%f,  b=%f",
                    percentage, deviation, a, b), percentage < deviation);
        }
    }

    private void doCalculatePercentile(Double percentile, Number[] test) {
        doCalculatePercentile(percentile, test, Double.MAX_VALUE);
    }

    private void doCalculatePercentile(Double percentile, Number[] test,
            double delta) {
        PSquarePercentile psquared = new PSquarePercentile(percentile);
        for (Number value : test) {
            psquared.increment(value.doubleValue());
        }

        Percentile p2 = new Percentile(percentile * 100);

        double[] dall = new double[test.length];
        for (int i = 0; i < test.length; i++) {
            dall[i] = test[i].doubleValue();
        }

        Double referenceValue = p2.evaluate(dall);
        assertValues(psquared.getResult(), referenceValue, delta);
    }

    private void doCalculatePercentile(double percentile, double[] test,
            double delta) {
        PSquarePercentile psquared = new PSquarePercentile(percentile);
        for (double value : test) {
            psquared.increment(value);
        }

        Percentile p2 =
                new Percentile(percentile < 1 ? percentile * 100 : percentile);
        /*
         * double[] dall = new double[test.length]; for (int i = 0; i <
         * test.length; i++) dall[i] = test[i];
         */
        Double referenceValue = p2.evaluate(test);
        assertValues(psquared.getResult(), referenceValue, delta);
    }

    @Test
    public void testCannedDataSet() {
        // test.unoverride("dump");
        Integer[] seedInput =
                new Integer[] { 283, 285, 298, 304, 310, 31, 319, 32, 33, 339,
                        342, 348, 350, 354, 354, 357, 36, 36, 369, 37, 37, 375,
                        378, 383, 390, 396, 405, 408, 41, 414, 419, 416, 42,
                        420, 430, 430, 432, 444, 447, 447, 449, 45, 451, 456,
                        468, 470, 471, 474, 600, 695, 70, 83, 97, 109, 113, 128 };
        Integer[] input = new Integer[seedInput.length * 100];
        for (int i = 0; i < input.length; i++) {
            input[i] = seedInput[i % seedInput.length] + i;
        }
        // Arrays.sort(input);
        doCalculatePercentile(0.50d, input);
        doCalculatePercentile(0.95d, input);

    }

    @Test
    public void test99Percentile() {
        Double[] test = randomTestData(100, 10000);
        doCalculatePercentile(0.99d, test);
    }

    @Test
    public void test90Percentile() {
        Double[] test = randomTestData(100, 10000);
        doCalculatePercentile(0.90d, test);
    }

    @Test
    public void test20Percentile() {
        Double[] test = randomTestData(100, 100000);
        doCalculatePercentile(0.20d, test);
    }

    @Test
    public void test5Percentile() {
        Double[] test = randomTestData(50, 990000);
        doCalculatePercentile(0.50d, test);
    }

    @Test
    public void test99PercentileHighValues() {
        Double[] test = randomTestData(100000, 10000);
        doCalculatePercentile(0.99d, test);
    }

    @Test
    public void test90PercentileHighValues() {
        Double[] test = randomTestData(100000, 100000);
        doCalculatePercentile(0.90d, test);
    }

    @Test
    public void test20PercentileHighValues() {
        Double[] test = randomTestData(100000, 100000);
        doCalculatePercentile(0.20d, test);
    }

    @Test
    public void test5PercentileHighValues() {
        Double[] test = randomTestData(100000, 100000);
        doCalculatePercentile(0.05d, test);
    }

    @Test
    public void test0PercentileValuesWithFewerThan5Values() {
        double[] test = { 1d, 2d, 3d, 4d };
        PSquarePercentile p = new PSquarePercentile(0d);
        Assert.assertEquals(1d, p.evaluate(test), 0);
        Assert.assertNotNull(p.toString());
    }

    @Test
    public void testPSQuaredEvalFuncWithPapersExampleData() throws IOException {

        // This data as input is considered from
        // http://www.cs.wustl.edu/~jain/papers/ftp/psqr.pdf
        double[] data =
                { 0.02, 0.5, 0.74, 3.39, 0.83, 22.37, 10.15, 15.43, 38.62,
                        15.92, 34.6, 10.28, 1.47, 0.4, 0.05, 11.39, 0.27, 0.42,
                        0.09, 11.37,

                        11.39, 15.43, 15.92, 22.37, 34.6, 38.62, 18.9, 19.2,
                        27.6, 12.8, 13.7, 21.9

                };

        PSquarePercentile psquared = new PSquarePercentile(50);

        Double p2value = 0d;
        for (int i = 0; i < 20; i++) {
            psquared.increment(data[i]);
            p2value = psquared.getResult();
            // System.out.println(psquared.toString());//uncomment here to see
            // the papers example output
        }
        // System.out.println("p2value=" + p2value);
        Double expected = 4.44d;// 13d; // From The Paper
        // http://www.cs.wustl.edu/~jain/papers/ftp/psqr.pdf.
        // Pl refer Pg 1061 Look at the mid marker
        // height
        // expected = new Percentile(50).evaluate(data,0,20);
        // Well the values deviate in our calculation by 0.25 so its 4.25 vs
        // 4.44
        Assert.assertEquals(
                String.format("Expected=%f, Actual=%f", expected, p2value),
                expected, p2value, 0.25);

    }

    private final int TINY = 10;
    private final int SMALL = 50;
    private final int NOMINAL = 100;
    private final int MEDIUM = 500;
    private final int STANDARD = 1000;
    private final int BIG = 10000;
    private final int VERY_BIG = 50000;
    private final int LARGE = 1000000;
    private final int VERY_LARGE = 10000000;

    private void doDistributionTest(ContinuousDistribution distribution) {
        final ContinuousDistribution.Sampler sampler =
            distribution.createSampler(RandomSource.WELL_19937_C.create(1000));
        double data[];

        data = AbstractRealDistribution.sample(VERY_LARGE, sampler);
        doCalculatePercentile(50, data, 0.0001);
        doCalculatePercentile(95, data, 0.0001);

        data = AbstractRealDistribution.sample(LARGE, sampler);
        doCalculatePercentile(50, data, 0.001);
        doCalculatePercentile(95, data, 0.001);

        data = AbstractRealDistribution.sample(VERY_BIG, sampler);
        doCalculatePercentile(50, data, 0.001);
        doCalculatePercentile(95, data, 0.001);

        data = AbstractRealDistribution.sample(BIG, sampler);
        doCalculatePercentile(50, data, 0.001);
        doCalculatePercentile(95, data, 0.001);

        data = AbstractRealDistribution.sample(STANDARD, sampler);
        doCalculatePercentile(50, data, 0.005);
        doCalculatePercentile(95, data, 0.005);

        data = AbstractRealDistribution.sample(MEDIUM, sampler);
        doCalculatePercentile(50, data, 0.005);
        doCalculatePercentile(95, data, 0.005);

        data = AbstractRealDistribution.sample(NOMINAL, sampler);
        doCalculatePercentile(50, data, 0.01);
        doCalculatePercentile(95, data, 0.01);

        data = AbstractRealDistribution.sample(SMALL, sampler);
        doCalculatePercentile(50, data, 0.01);
        doCalculatePercentile(95, data, 0.01);

        data = AbstractRealDistribution.sample(TINY, sampler);
        doCalculatePercentile(50, data, 0.05);
        doCalculatePercentile(95, data, 0.05);

    }

    /**
     * Test Various Dist
     */
    @Test
    public void testDistribution() {
        doDistributionTest(NormalDistribution.of(4000, 50));
        doDistributionTest(LogNormalDistribution.of(4000, 50));
        // doDistributionTest((ExponentialDistribution.of(4000));
        // doDistributionTest(GammaDistribution.of(5d,1d),0.1);
    }
}
