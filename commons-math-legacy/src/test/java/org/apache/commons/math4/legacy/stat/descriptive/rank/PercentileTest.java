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

import java.util.Arrays;

import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.math4.legacy.distribution.AbstractRealDistribution;
import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NotANumberException;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.legacy.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math4.legacy.stat.descriptive.UnivariateStatisticAbstractTest;
import org.apache.commons.math4.legacy.stat.ranking.NaNStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 */
public class PercentileTest extends UnivariateStatisticAbstractTest{

    protected Percentile stat;

    private double quantile;

    /**
     * {@link org.apache.commons.math4.legacy.stat.descriptive.rank.Percentile.EstimationType type}
     * of estimation to be used while calling {@link #getUnivariateStatistic()}
     */
    private Percentile.EstimationType type;

    /**
     * {@link NaNStrategy}
     * of estimation to be used while calling {@link #getUnivariateStatistic()}
     */
    private NaNStrategy nanStrategy;

    /**
     * kth selector
     */
    private KthSelector kthSelector;

    /**
     * A default percentile to be used for {@link #getUnivariateStatistic()}
     */
    protected final double DEFAULT_PERCENTILE = 95d;

    /**
     * Before method to ensure defaults retained
     */
    @Before
    public void before() {
        quantile         = 95.0;
        type             = Percentile.EstimationType.LEGACY;
        nanStrategy      = NaNStrategy.REMOVED;
        kthSelector      = new KthSelector(new MedianOf3PivotingStrategy());
    }

    private void reset(final double p, final Percentile.EstimationType type) {
        this.quantile = p;
        this.type     = type;
        nanStrategy   = (type == Percentile.EstimationType.LEGACY) ? NaNStrategy.FIXED : NaNStrategy.REMOVED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Percentile getUnivariateStatistic() {
        return new Percentile(quantile).
                withEstimationType(type).
                withNaNStrategy(nanStrategy).
                withKthSelector(kthSelector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.percentile95;
    }

    @Test
    public void testHighPercentile(){
        final double[] d = new double[]{1, 2, 3};
        final Percentile p = new Percentile(75);
        Assert.assertEquals(3.0, p.evaluate(d), 1.0e-5);
    }

    @Test
    public void testLowPercentile() {
        final double[] d = new double[] {0, 1};
        final Percentile p = new Percentile(25);
        Assert.assertEquals(0d, p.evaluate(d), Double.MIN_VALUE);
    }

    @Test
    public void testPercentile() {
        final double[] d = new double[] {1, 3, 2, 4};
        final Percentile p = new Percentile(30);
        Assert.assertEquals(1.5, p.evaluate(d), 1.0e-5);
        p.setQuantile(25);
        Assert.assertEquals(1.25, p.evaluate(d), 1.0e-5);
        p.setQuantile(75);
        Assert.assertEquals(3.75, p.evaluate(d), 1.0e-5);
        p.setQuantile(50);
        Assert.assertEquals(2.5, p.evaluate(d), 1.0e-5);

        // invalid percentiles
        try {
            p.evaluate(d, 0, d.length, -1.0);
            Assert.fail();
        } catch (final MathIllegalArgumentException ex) {
            // success
        }
        try {
            p.evaluate(d, 0, d.length, 101.0);
            Assert.fail();
        } catch (final MathIllegalArgumentException ex) {
            // success
        }
    }

    @Test
    public void testNISTExample() {
        final double[] d = new double[] {95.1772, 95.1567, 95.1937, 95.1959,
                95.1442, 95.0610,  95.1591, 95.1195, 95.1772, 95.0925, 95.1990, 95.1682
        };
        final Percentile p = new Percentile(90);
        Assert.assertEquals(95.1981, p.evaluate(d), 1.0e-4);
        Assert.assertEquals(95.1990, p.evaluate(d,0,d.length, 100d), 0);
    }

    @Test
    public void test5() {
        final Percentile percentile = new Percentile(5);
        Assert.assertEquals(this.percentile5, percentile.evaluate(testArray), getTolerance());
    }

    @Test
    public void testNullEmpty() {
        final Percentile percentile = new Percentile(50);
        final double[] nullArray = null;
        final double[] emptyArray = new double[] {};
        try {
            percentile.evaluate(nullArray);
            Assert.fail("Expecting NullArgumentException for null array");
        } catch (final NullArgumentException ex) {
            // expected
        }
        Assert.assertTrue(Double.isNaN(percentile.evaluate(emptyArray)));
    }

    @Test
    public void testSingleton() {
        final Percentile percentile = new Percentile(50);
        final double[] singletonArray = new double[] {1d};
        Assert.assertEquals(1d, percentile.evaluate(singletonArray), 0);
        Assert.assertEquals(1d, percentile.evaluate(singletonArray, 0, 1), 0);
        Assert.assertEquals(1d, percentile.evaluate(singletonArray, 0, 1, 5), 0);
        Assert.assertEquals(1d, percentile.evaluate(singletonArray, 0, 1, 100), 0);
        Assert.assertTrue(Double.isNaN(percentile.evaluate(singletonArray, 0, 0)));
    }

    @Test
    public void testSpecialValues() {
        final Percentile percentile = new Percentile(50);
        double[] specialValues = new double[] {0d, 1d, 2d, 3d, 4d,  Double.NaN};
        Assert.assertEquals(/*2.5d*/2d, percentile.evaluate(specialValues), 0);
        specialValues =  new double[] {Double.NEGATIVE_INFINITY, 1d, 2d, 3d,
                Double.NaN, Double.POSITIVE_INFINITY};
        Assert.assertEquals(/*2.5d*/2d, percentile.evaluate(specialValues), 0);
        specialValues = new double[] {1d, 1d, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY};
        Assert.assertTrue(Double.isInfinite(percentile.evaluate(specialValues)));
        specialValues = new double[] {1d, 1d, Double.NaN,
                Double.NaN};
        Assert.assertFalse(Double.isNaN(percentile.evaluate(specialValues)));
        Assert.assertEquals(1d, percentile.evaluate(specialValues), 0.0);
        specialValues = new double[] {1d, 1d, Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY};
        // Interpolation results in NEGATIVE_INFINITY + POSITIVE_INFINITY
        Assert.assertTrue(Double.isNaN(percentile.evaluate(specialValues)));
    }

    @Test
    public void testSetQuantile() {
        final Percentile percentile = new Percentile(10);
        percentile.setQuantile(100); // OK
        Assert.assertEquals(100, percentile.getQuantile(), 0);
        try {
            percentile.setQuantile(0);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (final MathIllegalArgumentException ex) {
            // expected
        }
        try {
            new Percentile(0);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (final MathIllegalArgumentException ex) {
            // expected
        }
    }

    //Below tests are basically to run for all estimation types.
    /**
     * While {@link #testHighPercentile()} checks only for the existing
     * implementation; this method verifies for all the types including Percentile.Type.CM Percentile.Type.
     */
    @Test
    public void testAllTechniquesHighPercentile() {
        final double[] d = new double[] { 1, 2, 3 };
        testAssertMappedValues(d, new Object[][] { { Percentile.EstimationType.LEGACY, 3d }, { Percentile.EstimationType.R_1, 3d },
                { Percentile.EstimationType.R_2, 3d }, { Percentile.EstimationType.R_3, 2d }, { Percentile.EstimationType.R_4, 2.25 }, { Percentile.EstimationType.R_5, 2.75 },
                { Percentile.EstimationType.R_6, 3d }, { Percentile.EstimationType.R_7, 2.5 },{ Percentile.EstimationType.R_8, 2.83333 }, {Percentile.EstimationType.R_9,2.81250} },
                75d, 1.0e-5);
    }

    @Test
    public void testAllTechniquesLowPercentile() {
        final double[] d = new double[] { 0, 1 };
        testAssertMappedValues(d, new Object[][] { { Percentile.EstimationType.LEGACY, 0d }, { Percentile.EstimationType.R_1, 0d },
                { Percentile.EstimationType.R_2, 0d }, { Percentile.EstimationType.R_3, 0d }, { Percentile.EstimationType.R_4, 0d }, {Percentile.EstimationType.R_5, 0d}, {Percentile.EstimationType.R_6, 0d},
                { Percentile.EstimationType.R_7, 0.25 }, { Percentile.EstimationType.R_8, 0d }, {Percentile.EstimationType.R_9, 0d} },
                25d, Double.MIN_VALUE);
    }

    public void checkAllTechniquesPercentile() {
        final double[] d = new double[] { 1, 3, 2, 4 };

        testAssertMappedValues(d, new Object[][] { { Percentile.EstimationType.LEGACY, 1.5d },
                { Percentile.EstimationType.R_1, 2d }, { Percentile.EstimationType.R_2, 2d }, { Percentile.EstimationType.R_3, 1d }, { Percentile.EstimationType.R_4, 1.2 }, {Percentile.EstimationType.R_5, 1.7},
                { Percentile.EstimationType.R_6, 1.5 },{ Percentile.EstimationType.R_7, 1.9 }, { Percentile.EstimationType.R_8, 1.63333 },{ Percentile.EstimationType.R_9, 1.65 } },
                30d, 1.0e-05);

        testAssertMappedValues(d, new Object[][] { { Percentile.EstimationType.LEGACY, 1.25d },
                { Percentile.EstimationType.R_1, 1d }, { Percentile.EstimationType.R_2, 1.5d }, { Percentile.EstimationType.R_3, 1d }, { Percentile.EstimationType.R_4, 1d }, {Percentile.EstimationType.R_5, 1.5},
                { Percentile.EstimationType.R_6, 1.25 },{ Percentile.EstimationType.R_7, 1.75 },
                { Percentile.EstimationType.R_8, 1.41667 }, { Percentile.EstimationType.R_9, 1.43750 } }, 25d, 1.0e-05);

        testAssertMappedValues(d, new Object[][] { { Percentile.EstimationType.LEGACY, 3.75d },
                { Percentile.EstimationType.R_1, 3d }, { Percentile.EstimationType.R_2, 3.5d }, { Percentile.EstimationType.R_3, 3d }, { Percentile.EstimationType.R_4, 3d },
                { Percentile.EstimationType.R_5, 3.5d },{ Percentile.EstimationType.R_6, 3.75d }, { Percentile.EstimationType.R_7, 3.25 },
                { Percentile.EstimationType.R_8, 3.58333 },{ Percentile.EstimationType.R_9, 3.56250} }, 75d, 1.0e-05);

        testAssertMappedValues(d, new Object[][] { { Percentile.EstimationType.LEGACY, 2.5d },
                { Percentile.EstimationType.R_1, 2d }, { Percentile.EstimationType.R_2, 2.5d }, { Percentile.EstimationType.R_3, 2d }, { Percentile.EstimationType.R_4, 2d },
                { Percentile.EstimationType.R_5, 2.5 },{ Percentile.EstimationType.R_6, 2.5 },{ Percentile.EstimationType.R_7, 2.5 },
                { Percentile.EstimationType.R_8, 2.5 },{ Percentile.EstimationType.R_9, 2.5 } }, 50d, 1.0e-05);

        // invalid percentiles
        for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
            try {
                reset(-1.0, e);
                getUnivariateStatistic().evaluate(d, 0, d.length);
                Assert.fail();
            } catch (final MathIllegalArgumentException ex) {
                // success
            }
        }

        for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
            try {
                reset(101.0, e);
                getUnivariateStatistic().evaluate(d, 0, d.length);
                Assert.fail();
            } catch (final MathIllegalArgumentException ex) {
                // success
            }
        }
    }

    @Test
    public void testAllTechniquesPercentileUsingMedianOf3Pivoting() {
        kthSelector = new KthSelector(new MedianOf3PivotingStrategy());
        Assert.assertEquals(MedianOf3PivotingStrategy.class,
                            getUnivariateStatistic().getPivotingStrategy().getClass());
        checkAllTechniquesPercentile();
    }

    @Test
    public void testAllTechniquesPercentileUsingCentralPivoting() {
        kthSelector = new KthSelector(new CentralPivotingStrategy());
        Assert.assertEquals(CentralPivotingStrategy.class,
                            getUnivariateStatistic().getPivotingStrategy().getClass());
        checkAllTechniquesPercentile();
    }

    @Test
    public void testAllTechniquesPercentileUsingRandomPivoting() {
        kthSelector = new KthSelector(new RandomPivotingStrategy(RandomSource.WELL_1024_A, 0x268a7fb4194240f6L));
        Assert.assertEquals(RandomPivotingStrategy.class,
                            getUnivariateStatistic().getPivotingStrategy().getClass());
        checkAllTechniquesPercentile();
    }

    @Test
    public void testAllTechniquesNISTExample() {
        final double[] d =
                new double[] { 95.1772, 95.1567, 95.1937, 95.1959, 95.1442,
                        95.0610, 95.1591, 95.1195, 95.1772, 95.0925, 95.1990,
                        95.1682 };

        testAssertMappedValues(d, new Object[][] { { Percentile.EstimationType.LEGACY, 95.1981 },
                { Percentile.EstimationType.R_1, 95.19590 }, { Percentile.EstimationType.R_2, 95.19590 }, { Percentile.EstimationType.R_3, 95.19590 },
                { Percentile.EstimationType.R_4, 95.19546 }, { Percentile.EstimationType.R_5, 95.19683 }, { Percentile.EstimationType.R_6, 95.19807 },
                { Percentile.EstimationType.R_7, 95.19568 }, { Percentile.EstimationType.R_8, 95.19724 }, { Percentile.EstimationType.R_9, 95.19714 } }, 90d,
                1.0e-04);

        for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
            reset(100.0, e);
            Assert.assertEquals(95.1990, getUnivariateStatistic().evaluate(d), 1.0e-4);
        }
    }

    @Test
    public void testAllTechniques5() {
        reset(5, Percentile.EstimationType.LEGACY);
        final UnivariateStatistic percentile = getUnivariateStatistic();
        Assert.assertEquals(this.percentile5, percentile.evaluate(testArray),
                getTolerance());
        testAssertMappedValues(testArray,
                new Object[][] { { Percentile.EstimationType.LEGACY, percentile5 }, { Percentile.EstimationType.R_1, 8.8000 },
                        { Percentile.EstimationType.R_2, 8.8000 }, { Percentile.EstimationType.R_3, 8.2000 }, { Percentile.EstimationType.R_4, 8.2600 },
                        { Percentile.EstimationType.R_5, 8.5600 }, { Percentile.EstimationType.R_6, 8.2900 },
                        { Percentile.EstimationType.R_7, 8.8100 }, { Percentile.EstimationType.R_8, 8.4700 },
                        { Percentile.EstimationType.R_9, 8.4925 }}, 5d, getTolerance());
    }

    @Test
    public void testAllTechniquesNullEmpty() {

        final double[] nullArray = null;
        final double[] emptyArray = new double[] {};
        for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
            reset (50, e);
            final UnivariateStatistic percentile = getUnivariateStatistic();
            try {
                percentile.evaluate(nullArray);
                Assert.fail("Expecting NullArgumentException "
                        + "for null array");
            } catch (final NullArgumentException ex) {
                // expected
            }
            Assert.assertTrue(Double.isNaN(percentile.evaluate(emptyArray)));
        }

    }

    @Test
    public void testAllTechniquesSingleton() {
        final double[] singletonArray = new double[] { 1d };
        for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
            reset (50, e);
            final UnivariateStatistic percentile = getUnivariateStatistic();
            Assert.assertEquals(1d, percentile.evaluate(singletonArray), 0);
            Assert.assertEquals(1d, percentile.evaluate(singletonArray, 0, 1),
                    0);
            Assert.assertEquals(1d,
                    new Percentile().evaluate(singletonArray, 0, 1, 5), 0);
            Assert.assertEquals(1d,
                    new Percentile().evaluate(singletonArray, 0, 1, 100), 0);
            Assert.assertTrue(Double.isNaN(percentile.evaluate(singletonArray,
                    0, 0)));
        }
    }

    @Test
    public void testAllTechniquesEmpty() {
        final double[] singletonArray = new double[] { };
        for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
            reset (50, e);
            final UnivariateStatistic percentile = getUnivariateStatistic();
            Assert.assertEquals(Double.NaN, percentile.evaluate(singletonArray),
                    0);
            Assert.assertEquals(Double.NaN, percentile.evaluate(singletonArray,
                    0, 0),
                    0);
            Assert.assertEquals(Double.NaN,
                    new Percentile().evaluate(singletonArray, 0, 0, 5), 0);
            Assert.assertEquals(Double.NaN,
                    new Percentile().evaluate(singletonArray, 0, 0, 100), 0);
            Assert.assertTrue(Double.isNaN(percentile.evaluate(singletonArray,
                    0, 0)));
        }
    }

    @Test
    public void testReplaceNanInRange() {
        final double[] specialValues =
                new double[] { 0d, 1d, 2d, 3d, 4d, Double.NaN, Double.NaN, 5d,
                7d, Double.NaN, 8d};
        Assert.assertEquals(/*Double.NaN*/3.5,new Percentile(50d).evaluate(specialValues),0d);
        reset (50, Percentile.EstimationType.R_1);
        Assert.assertEquals(3d, getUnivariateStatistic().evaluate(specialValues),0d);
        reset (50, Percentile.EstimationType.R_2);
        Assert.assertEquals(3.5d, getUnivariateStatistic().evaluate(specialValues),0d);
        Assert.assertEquals(Double.POSITIVE_INFINITY,new Percentile(70)
                                        .withNaNStrategy(NaNStrategy.MAXIMAL)
                                        .evaluate(specialValues),0d);
    }

    @Test
    public void testRemoveNan() {
        final double[] specialValues =
                new double[] { 0d, 1d, 2d, 3d, 4d, Double.NaN };
        final double[] expectedValues =
                new double[] { 0d, 1d, 2d, 3d, 4d };
        reset (50, Percentile.EstimationType.R_1);
        Assert.assertEquals(2.0, getUnivariateStatistic().evaluate(specialValues), 0d);
        Assert.assertEquals(2.0, getUnivariateStatistic().evaluate(expectedValues),0d);
        Assert.assertTrue(Double.isNaN(getUnivariateStatistic().evaluate(specialValues,5,1)));
        Assert.assertEquals(4d, getUnivariateStatistic().evaluate(specialValues, 4, 2), 0d);
        Assert.assertEquals(3d, getUnivariateStatistic().evaluate(specialValues,3,3),0d);
        reset(50, Percentile.EstimationType.R_2);
        Assert.assertEquals(3.5d, getUnivariateStatistic().evaluate(specialValues,3,3),0d);

    }

    @Test
    public void testPercentileCopy() {
       reset(50d, Percentile.EstimationType.LEGACY);
       final Percentile original = getUnivariateStatistic();
       final Percentile copy = new Percentile(original);
       Assert.assertEquals(original.getNaNStrategy(),copy.getNaNStrategy());
       Assert.assertEquals(original.getQuantile(), copy.getQuantile(),0d);
       Assert.assertEquals(original.getEstimationType(),copy.getEstimationType());
       Assert.assertEquals(NaNStrategy.FIXED, original.getNaNStrategy());
    }

    @Test
    public void testAllTechniquesSpecialValues() {
        reset(50d, Percentile.EstimationType.LEGACY);
        final UnivariateStatistic percentile = getUnivariateStatistic();
        double[] specialValues =
                new double[] { 0d, 1d, 2d, 3d, 4d, Double.NaN };
        Assert.assertEquals(2.5d, percentile.evaluate(specialValues), 0);

        testAssertMappedValues(specialValues, new Object[][] {
                { Percentile.EstimationType.LEGACY, 2.5d }, { Percentile.EstimationType.R_1, 2.0 }, { Percentile.EstimationType.R_2, 2.0 }, { Percentile.EstimationType.R_3, 1.0 },
                { Percentile.EstimationType.R_4, 1.5 }, { Percentile.EstimationType.R_5, 2.0 }, { Percentile.EstimationType.R_6, 2.0 },
                { Percentile.EstimationType.R_7, 2.0 }, { Percentile.EstimationType.R_8, 2.0 }, { Percentile.EstimationType.R_9, 2.0 }}, 50d, 0d);

        specialValues =
                new double[] { Double.NEGATIVE_INFINITY, 1d, 2d, 3d,
                        Double.NaN, Double.POSITIVE_INFINITY };
        Assert.assertEquals(2.5d, percentile.evaluate(specialValues), 0);

        testAssertMappedValues(specialValues, new Object[][] {
                { Percentile.EstimationType.LEGACY, 2.5d }, { Percentile.EstimationType.R_1, 2.0 }, { Percentile.EstimationType.R_2, 2.0 }, { Percentile.EstimationType.R_3, 1.0 },
                { Percentile.EstimationType.R_4, 1.5 }, { Percentile.EstimationType.R_5, 2.0 }, { Percentile.EstimationType.R_7, 2.0 }, { Percentile.EstimationType.R_7, 2.0 },
                { Percentile.EstimationType.R_8, 2.0 }, { Percentile.EstimationType.R_9, 2.0 } }, 50d, 0d);

        specialValues =
                new double[] { 1d, 1d, Double.POSITIVE_INFINITY,
                        Double.POSITIVE_INFINITY };
        Assert.assertTrue(Double.isInfinite(percentile.evaluate(specialValues)));

        testAssertMappedValues(specialValues, new Object[][] {
                // This is one test not matching with R results.
                { Percentile.EstimationType.LEGACY, Double.POSITIVE_INFINITY },
                { Percentile.EstimationType.R_1,/* 1.0 */Double.NaN },
                { Percentile.EstimationType.R_2, /* Double.POSITIVE_INFINITY */Double.NaN },
                { Percentile.EstimationType.R_3, /* 1.0 */Double.NaN }, { Percentile.EstimationType.R_4, /* 1.0 */Double.NaN },
                { Percentile.EstimationType.R_5, Double.POSITIVE_INFINITY },
                { Percentile.EstimationType.R_6, Double.POSITIVE_INFINITY },
                { Percentile.EstimationType.R_7, Double.POSITIVE_INFINITY },
                { Percentile.EstimationType.R_8, Double.POSITIVE_INFINITY },
                { Percentile.EstimationType.R_9, Double.POSITIVE_INFINITY }, }, 50d, 0d);

        specialValues = new double[] { 1d, 1d, Double.NaN, Double.NaN };
        Assert.assertTrue(Double.isNaN(percentile.evaluate(specialValues)));
        testAssertMappedValues(specialValues, new Object[][] {
                { Percentile.EstimationType.LEGACY, Double.NaN }, { Percentile.EstimationType.R_1, 1.0 }, { Percentile.EstimationType.R_2, 1.0 }, { Percentile.EstimationType.R_3, 1.0 },
                { Percentile.EstimationType.R_4, 1.0 }, { Percentile.EstimationType.R_5, 1.0 },{ Percentile.EstimationType.R_6, 1.0 },{ Percentile.EstimationType.R_7, 1.0 },
                { Percentile.EstimationType.R_8, 1.0 }, { Percentile.EstimationType.R_9, 1.0 },}, 50d, 0d);

        specialValues =
                new double[] { 1d, 1d, Double.NEGATIVE_INFINITY,
                        Double.NEGATIVE_INFINITY };

        testAssertMappedValues(specialValues, new Object[][] {
                { Percentile.EstimationType.LEGACY, Double.NaN }, { Percentile.EstimationType.R_1, Double.NaN },
                { Percentile.EstimationType.R_2, Double.NaN }, { Percentile.EstimationType.R_3, Double.NaN }, { Percentile.EstimationType.R_4, Double.NaN },
                { Percentile.EstimationType.R_5, Double.NaN }, { Percentile.EstimationType.R_6, Double.NaN },
                { Percentile.EstimationType.R_7, Double.NaN }, { Percentile.EstimationType.R_8, Double.NaN }, { Percentile.EstimationType.R_9, Double.NaN }
                }, 50d, 0d);

    }

    @Test
    public void testAllTechniquesSetQuantile() {
        for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
            reset(10, e);
            final Percentile percentile = getUnivariateStatistic();
            percentile.setQuantile(100); // OK
            Assert.assertEquals(100, percentile.getQuantile(), 0);
            try {
                percentile.setQuantile(0);
                Assert.fail("Expecting MathIllegalArgumentException");
            } catch (final MathIllegalArgumentException ex) {
                // expected
            }
            try {
                new Percentile(0);
                Assert.fail("Expecting MathIllegalArgumentException");
            } catch (final MathIllegalArgumentException ex) {
                // expected
            }
        }
    }

    @Test
    public void testAllTechniquesEvaluateArraySegmentWeighted() {
        for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
            reset(quantile, e);
            testEvaluateArraySegmentWeighted();
        }
    }

    @Test
    public void testAllTechniquesEvaluateArraySegment() {
        for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
            reset(quantile, e);
            testEvaluateArraySegment();
        }
    }

    @Test
    public void testAllTechniquesWeightedConsistency() {
        for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
            reset(quantile, e);
            testWeightedConsistency();
        }
    }

    @Test
    public void testAllTechniquesEvaluation() {

        testAssertMappedValues(testArray, new Object[][] { { Percentile.EstimationType.LEGACY, 20.820 },
                { Percentile.EstimationType.R_1, 19.800 }, { Percentile.EstimationType.R_2, 19.800 }, { Percentile.EstimationType.R_3, 19.800 },
                { Percentile.EstimationType.R_4, 19.310 }, { Percentile.EstimationType.R_5, 20.280 }, { Percentile.EstimationType.R_6, 20.820 },
                { Percentile.EstimationType.R_7, 19.555 }, { Percentile.EstimationType.R_8, 20.460 },{ Percentile.EstimationType.R_9, 20.415} },
                DEFAULT_PERCENTILE, tolerance);
    }

    @Test
    public void testPercentileWithTechnique() {
        reset (50, Percentile.EstimationType.LEGACY);
        final Percentile p = getUnivariateStatistic();
        Assert.assertEquals(Percentile.EstimationType.LEGACY, p.getEstimationType());
        Assert.assertNotEquals(Percentile.EstimationType.R_1, p.getEstimationType());
    }

    static final int TINY = 10;
    static final int SMALL = 50;
    static final int NOMINAL = 100;
    static final int MEDIUM = 500;
    static final int STANDARD = 1000;
    static final int BIG = 10000;
    static final int VERY_BIG = 50000;
    static final int LARGE = 1000000;
    static final int VERY_LARGE = 10000000;
    static final int[] sampleSizes = {TINY , SMALL , NOMINAL , MEDIUM ,
            STANDARD, BIG };

    @Test
    public void testStoredVsDirect() {
        final ContinuousDistribution.Sampler sampler =
            NormalDistribution.of(4000, 50).createSampler(RandomSource.JDK.create(Long.MAX_VALUE));

        for (final int sampleSize : sampleSizes) {
            final double[] data = AbstractRealDistribution.sample(sampleSize, sampler);
            for (final double p : new double[] { 50d, 95d }) {
                for (final Percentile.EstimationType e : Percentile.EstimationType.values()) {
                    reset(p, e);
                    final Percentile pStoredData = getUnivariateStatistic();
                    pStoredData.setData(data);
                    final double storedDataResult = pStoredData.evaluate();
                    pStoredData.setData(null);
                    final Percentile pDirect = getUnivariateStatistic();
                    Assert.assertEquals("Sample=" + sampleSize + ", P=" + p + " e=" + e,
                                        storedDataResult,
                                        pDirect.evaluate(data), 0d);
                }
            }
        }
    }

    @Test
    public void testPercentileWithDataRef() {
        reset(50.0, Percentile.EstimationType.R_7);
        final Percentile p = getUnivariateStatistic();
        p.setData(testArray);
        Assert.assertEquals(Percentile.EstimationType.R_7, p.getEstimationType());
        Assert.assertNotEquals(Percentile.EstimationType.R_1, p.getEstimationType());
        Assert.assertEquals(12d, p.evaluate(), 0d);
        Assert.assertEquals(12.16d, p.evaluate(60d), 0d);
    }

    @Test(expected=NullArgumentException.class)
    public void testNullEstimation() {
        type = null;
        getUnivariateStatistic();
    }

    @Test
    public void testAllEstimationTechniquesOnlyLimits() {
        final int N=testArray.length;

        final double[] input = Arrays.copyOf(testArray, testArray.length);
        Arrays.sort(input);
        final double min = input[0];
        final double max=input[input.length-1];
        //limits may be ducked by 0.01 to induce the condition of p<pMin
        final Object[][] map =
                new Object[][] { { Percentile.EstimationType.LEGACY, 0d, 1d }, { Percentile.EstimationType.R_1, 0d, 1d },
                        { Percentile.EstimationType.R_2, 0d,1d }, { Percentile.EstimationType.R_3, 0.5/N,1d },
                        { Percentile.EstimationType.R_4, 1d/N-0.001,1d },
                        { Percentile.EstimationType.R_5, 0.5/N-0.001,(N-0.5)/N}, { Percentile.EstimationType.R_6, 0.99d/(N+1),
                            1.01d*N/(N+1)},
                        { Percentile.EstimationType.R_7, 0d,1d}, { Percentile.EstimationType.R_8, 1.99d/3/(N+1d/3),
                            (N-1d/3)/(N+1d/3)},
                        { Percentile.EstimationType.R_9, 4.99d/8/(N+0.25), (N-3d/8)/(N+0.25)} };

        for(final Object[] arr:map) {
            final Percentile.EstimationType t= (Percentile.EstimationType) arr[0];
            double pMin=(Double)arr[1];
            final double pMax=(Double)arr[2];
            Assert.assertEquals("Type:"+t,0d, t.index(pMin, N),0d);
            Assert.assertEquals("Type:"+t,N, t.index(pMax, N),0.5d);
            pMin=pMin==0d?pMin+0.01:pMin;
            testAssertMappedValues(testArray, new Object[][] { { t, min }}, pMin, 0.01);
            testAssertMappedValues(testArray, new Object[][] { { t, max }}, pMax * 100, tolerance);
        }
    }

    @Test
    public void testAllEstimationTechniquesOnly() {
        Assert.assertEquals("Legacy Apache Commons Math",Percentile.EstimationType.LEGACY.getName());
        final Object[][] map =
                new Object[][] { { Percentile.EstimationType.LEGACY, 20.82 }, { Percentile.EstimationType.R_1, 19.8 },
                        { Percentile.EstimationType.R_2, 19.8 }, { Percentile.EstimationType.R_3, 19.8 }, { Percentile.EstimationType.R_4, 19.310 },
                        { Percentile.EstimationType.R_5, 20.280}, { Percentile.EstimationType.R_6, 20.820},
                        { Percentile.EstimationType.R_7, 19.555 }, { Percentile.EstimationType.R_8, 20.460 },{Percentile.EstimationType.R_9,20.415} };
        try {
            Percentile.EstimationType.LEGACY.evaluate(testArray, -1d, new KthSelector(new MedianOf3PivotingStrategy()));
        } catch (final OutOfRangeException oore) {
        }
        try {
            Percentile.EstimationType.LEGACY.evaluate(testArray, 101d, new KthSelector());
        } catch (final OutOfRangeException oore) {
        }
        try {
            Percentile.EstimationType.LEGACY.evaluate(testArray, 50d, new KthSelector());
        } catch(final OutOfRangeException oore) {
        }
        for (final Object[] o : map) {
            final Percentile.EstimationType e = (Percentile.EstimationType) o[0];
            final double expected = (Double) o[1];
            final double result = e.evaluate(testArray, DEFAULT_PERCENTILE, new KthSelector());
            Assert.assertEquals("expected[" + e + "] = " + expected +
                    " but was = " + result, expected, result, tolerance);
        }
    }

    @Test
    public void testAllEstimationTechniquesOnlyForAllPivotingStrategies() {

        Assert.assertEquals("Legacy Apache Commons Math",Percentile.EstimationType.LEGACY.getName());

        for (final PivotingStrategy strategy : new PivotingStrategy[] {
            new MedianOf3PivotingStrategy(),
            new CentralPivotingStrategy(),
            new RandomPivotingStrategy(RandomSource.WELL_1024_A, 0xf097c734e4740053L)
        }) {
            kthSelector = new KthSelector(strategy);
            testAllEstimationTechniquesOnly();
        }
    }

    @Test
    public void testAllEstimationTechniquesOnlyForExtremeIndexes() {
        final double MAX=100;
        final Object[][] map =
                new Object[][] { { Percentile.EstimationType.LEGACY, 0d, MAX}, { Percentile.EstimationType.R_1, 0d,MAX+0.5 },
                { Percentile.EstimationType.R_2, 0d,MAX}, { Percentile.EstimationType.R_3, 0d,MAX }, { Percentile.EstimationType.R_4, 0d,MAX },
                { Percentile.EstimationType.R_5, 0d,MAX }, { Percentile.EstimationType.R_6, 0d,MAX },
                { Percentile.EstimationType.R_7, 0d,MAX }, { Percentile.EstimationType.R_8, 0d,MAX }, { Percentile.EstimationType.R_9, 0d,MAX }  };
        for (final Object[] o : map) {
            final Percentile.EstimationType e = (Percentile.EstimationType) o[0];
                Assert.assertEquals(((Double)o[1]).doubleValue(),
                        e.index(0d, (int)MAX),0d);
                Assert.assertEquals("Enum:"+e,((Double)o[2]).doubleValue(),
                        e.index(1.0, (int)MAX),0d);
            }
    }

    @Test
    public void testAllEstimationTechniquesOnlyForNullsAndOOR() {

        final Object[][] map =
                new Object[][] { { Percentile.EstimationType.LEGACY, 20.82 }, { Percentile.EstimationType.R_1, 19.8 },
                        { Percentile.EstimationType.R_2, 19.8 }, { Percentile.EstimationType.R_3, 19.8 }, { Percentile.EstimationType.R_4, 19.310 },
                        { Percentile.EstimationType.R_5, 20.280}, { Percentile.EstimationType.R_6, 20.820},
                        { Percentile.EstimationType.R_7, 19.555 }, { Percentile.EstimationType.R_8, 20.460 },{ Percentile.EstimationType.R_9, 20.415 } };
        for (final Object[] o : map) {
            final Percentile.EstimationType e = (Percentile.EstimationType) o[0];
            try {
                e.evaluate(null, DEFAULT_PERCENTILE, new KthSelector());
                Assert.fail("Expecting NullArgumentException");
            } catch (final NullArgumentException nae) {
                // expected
            }
            try {
                e.evaluate(testArray, 120, new KthSelector());
                Assert.fail("Expecting OutOfRangeException");
            } catch (final OutOfRangeException oore) {
                // expected
            }
        }
    }

    /**
     * Simple test assertion utility method assuming {@link NaNStrategy default}
     * nan handling strategy specific to each
     * {@link org.apache.commons.math4.legacy.stat.descriptive.rank.Percentile.EstimationType EstimationType type}
     *
     * @param data input data
     * @param map of expected result against a {@link org.apache.commons.math4.legacy.stat.descriptive.rank.Percentile.EstimationType EstimationType}
     * @param p the quantile to compute for
     * @param tolerance the tolerance of difference allowed
     */
    protected void testAssertMappedValues(final double[] data, final Object[][] map,
            final Double p, final Double tolerance) {
        for (final Object[] o : map) {
            final Percentile.EstimationType e = (Percentile.EstimationType) o[0];
            final double expected = (Double) o[1];
            try {
                reset(p, e);
                final double result = getUnivariateStatistic().evaluate(data);
                Assert.assertEquals("expected[" + e + "] = " + expected +
                    " but was = " + result, expected, result, tolerance);
            } catch(final Exception ex) {
                Assert.fail("Exception occured for estimation type "+e+":"+
                        ex.getLocalizedMessage());
            }
        }
    }

    // Some NaNStrategy specific testing
    @Test
    public void testNanStrategySpecific() {
        double[] specialValues = new double[] { 0d, 1d, 2d, 3d, 4d, Double.NaN };
        Assert.assertTrue(Double.isNaN(new Percentile(50d).withEstimationType(Percentile.EstimationType.LEGACY).withNaNStrategy(NaNStrategy.MAXIMAL).evaluate(specialValues, 3, 3)));
        Assert.assertEquals(2d,new Percentile(50d).withEstimationType(Percentile.EstimationType.R_1).withNaNStrategy(NaNStrategy.REMOVED).evaluate(specialValues),0d);
        Assert.assertEquals(Double.NaN,new Percentile(50d).withEstimationType(Percentile.EstimationType.R_5).withNaNStrategy(NaNStrategy.REMOVED).evaluate(new double[] {Double.NaN,Double.NaN,Double.NaN}),0d);
        Assert.assertEquals(50d,new Percentile(50d).withEstimationType(Percentile.EstimationType.R_7).withNaNStrategy(NaNStrategy.MINIMAL).evaluate(new double[] {50d,50d,50d},1,2),0d);

        specialValues = new double[] { 0d, 1d, 2d, 3d, 4d, Double.NaN, Double.NaN };
        Assert.assertEquals(3.5,new Percentile().evaluate(specialValues, 3, 4),0d);
        Assert.assertEquals(4d,new Percentile().evaluate(specialValues, 4, 3),0d);
        Assert.assertTrue(Double.isNaN(new Percentile().evaluate(specialValues, 5, 2)));

        specialValues = new double[] { 0d, 1d, 2d, 3d, 4d, Double.NaN, Double.NaN, 5d, 6d };
        Assert.assertEquals(4.5,new Percentile().evaluate(specialValues, 3, 6),0d);
        Assert.assertEquals(5d,new Percentile().evaluate(specialValues, 4, 5),0d);
        Assert.assertTrue(Double.isNaN(new Percentile().evaluate(specialValues, 5, 2)));
        Assert.assertTrue(Double.isNaN(new Percentile().evaluate(specialValues, 5, 1)));
        Assert.assertEquals(5.5,new Percentile().evaluate(specialValues, 5, 4),0d);
    }

    // Some NaNStrategy specific testing
    @Test(expected=NotANumberException.class)
    public void testNanStrategyFailed() {
        double[] specialValues =
                new double[] { 0d, 1d, 2d, 3d, 4d, Double.NaN };
        new Percentile(50d).
        withEstimationType(Percentile.EstimationType.R_9).
        withNaNStrategy(NaNStrategy.FAILED).
        evaluate(specialValues);
    }

    @Test
    public void testAllTechniquesSpecialValuesWithNaNStrategy() {
        double[] specialValues =
                new double[] { 0d, 1d, 2d, 3d, 4d, Double.NaN };
        try {
            new Percentile(50d).withEstimationType(Percentile.EstimationType.LEGACY).withNaNStrategy(null);
            Assert.fail("Expecting NullArgumentArgumentException "
                    + "for null Nan Strategy");
        } catch (NullArgumentException ex) {
            // expected
        }
        //This is as per each type's default NaNStrategy
        testAssertMappedValues(specialValues, new Object[][] {
                { Percentile.EstimationType.LEGACY, 2.5d }, { Percentile.EstimationType.R_1, 2.0 }, { Percentile.EstimationType.R_2, 2.0 }, { Percentile.EstimationType.R_3, 1.0 },
                { Percentile.EstimationType.R_4, 1.5 }, { Percentile.EstimationType.R_5, 2.0 }, { Percentile.EstimationType.R_6, 2.0 },
                { Percentile.EstimationType.R_7, 2.0 }, { Percentile.EstimationType.R_8, 2.0 }, { Percentile.EstimationType.R_9, 2.0 }}, 50d, 0d);

        //This is as per MAXIMAL and hence the values tend a +0.5 upward
        testAssertMappedValues(specialValues, new Object[][] {
                { Percentile.EstimationType.LEGACY, 2.5d }, { Percentile.EstimationType.R_1, 2.0 }, { Percentile.EstimationType.R_2, 2.5 }, { Percentile.EstimationType.R_3, 2.0 },
                { Percentile.EstimationType.R_4, 2.0 }, { Percentile.EstimationType.R_5, 2.5 }, { Percentile.EstimationType.R_6, 2.5 },
                { Percentile.EstimationType.R_7, 2.5 }, { Percentile.EstimationType.R_8, 2.5 }, { Percentile.EstimationType.R_9, 2.5 }}, 50d, 0d,
                NaNStrategy.MAXIMAL);

        //This is as per MINIMAL and hence the values tend a -0.5 downward
        testAssertMappedValues(specialValues, new Object[][] {
                { Percentile.EstimationType.LEGACY, 1.5d }, { Percentile.EstimationType.R_1, 1.0 }, { Percentile.EstimationType.R_2, 1.5 }, { Percentile.EstimationType.R_3, 1.0 },
                { Percentile.EstimationType.R_4, 1.0 }, { Percentile.EstimationType.R_5, 1.5 }, { Percentile.EstimationType.R_6, 1.5 },
                { Percentile.EstimationType.R_7, 1.5 }, { Percentile.EstimationType.R_8, 1.5 }, { Percentile.EstimationType.R_9, 1.5 }}, 50d, 0d,
                NaNStrategy.MINIMAL);

        //This is as per REMOVED as here Percentile.Type.CM changed its value from default
        //while rest of Estimation types were anyways defaulted to REMOVED
        testAssertMappedValues(specialValues, new Object[][] {
                { Percentile.EstimationType.LEGACY, 2.0 }, { Percentile.EstimationType.R_1, 2.0 }, { Percentile.EstimationType.R_2, 2.0 }, { Percentile.EstimationType.R_3, 1.0 },
                { Percentile.EstimationType.R_4, 1.5 }, { Percentile.EstimationType.R_5, 2.0 }, { Percentile.EstimationType.R_6, 2.0 },
                { Percentile.EstimationType.R_7, 2.0 }, { Percentile.EstimationType.R_8, 2.0 }, { Percentile.EstimationType.R_9, 2.0 }}, 50d, 0d,
                NaNStrategy.REMOVED);
    }

    /**
     * Simple test assertion utility method
     *
     * @param data input data
     * @param map of expected result against a
     * {@link org.apache.commons.math4.legacy.stat.descriptive.rank.Percentile.EstimationType EstimationType}
     * @param p the quantile to compute for
     * @param tolerance the tolerance of difference allowed
     * @param nanStrategy NaNStrategy to be passed
     */
    protected void testAssertMappedValues(double[] data, Object[][] map,
                                          Double p, Double tolerance, NaNStrategy nanStrategy) {
        for (Object[] o : map) {
            Percentile.EstimationType e = (Percentile.EstimationType) o[0];
            double expected = (Double) o[1];
            try {
                double result = new Percentile(p).withEstimationType(e).withNaNStrategy(nanStrategy).evaluate(data);
                Assert.assertEquals("expected[" + e + "] = " + expected + " but was = " + result,
                                    expected, result, tolerance);
            } catch(Exception ex) {
                Assert.fail("Exception occured for estimation type " + e + ":" + ex.getLocalizedMessage());
            }
        }
    }

    // Test if weighted percentile got the same result with the non-weighted one
    // when all weights are equal to each other.
    @Test
    public void testResultWithNonWeightedPercentile() {
        double[] dataset =
                new double[] { Double.NaN, Double.NaN, Double.NaN };
        double[] weights =
                new double[] { 1, 1, 1 };
        Percentile p = new Percentile().
                           withEstimationType(Percentile.EstimationType.R_7).
                           withNaNStrategy(NaNStrategy.MAXIMAL);
        Assert.assertEquals(p.evaluate(dataset, weights, 25d), p.evaluate(dataset, 25d), 0d);
        Assert.assertEquals(p.evaluate(dataset, weights, 50d), p.evaluate(dataset, 50d), 0d);
        Assert.assertEquals(p.evaluate(dataset, weights, 75d), p.evaluate(dataset, 75d), 0d);
        p = new Percentile().
                withEstimationType(Percentile.EstimationType.R_7).
                withNaNStrategy(NaNStrategy.MINIMAL);
        Assert.assertEquals(p.evaluate(dataset, weights, 25d), p.evaluate(dataset, 25d), 0d);
        Assert.assertEquals(p.evaluate(dataset, weights, 50d), p.evaluate(dataset, 50d), 0d);
        Assert.assertEquals(p.evaluate(dataset, weights, 75d), p.evaluate(dataset, 75d), 0d);
        p = new Percentile().
                withEstimationType(Percentile.EstimationType.R_7);
        Assert.assertEquals(p.evaluate(dataset, weights, 25d), p.evaluate(dataset, 25d), 0d);
        Assert.assertEquals(p.evaluate(dataset, weights, 50d), p.evaluate(dataset, 50d), 0d);
        Assert.assertEquals(p.evaluate(dataset, weights, 75d), p.evaluate(dataset, 75d), 0d);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testDataAndWeightsLength() {
        double[] dataset =
                new double[] { 1d, 2d, 3d, 4d, 5d };
        double[] weights =
        new double[] { 1, 1, 1, 1 };
        new Percentile().
        withEstimationType(Percentile.EstimationType.R_7).
        evaluate(dataset, weights, 50d);
    }

    @Test
    public void testWeightedPercentileWithSpecialValues() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { 2, 6, 4, 3};
        Percentile p = new Percentile().
                           withEstimationType(Percentile.EstimationType.R_7);
        Assert.assertEquals( 3.53125, p.evaluate(dataset, weights, 50d), 0d);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testsetDataInputLength() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { 1, 1, 1 };
        new Percentile().setData(dataset, weights);
        new Percentile().setData(dataset, weights, 0, dataset.length);
    }

    @Test(expected=NotANumberException.class)
    public void testsetDataNotANumber() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { 1, 1, 1, Double.NaN };
        new Percentile().setData(dataset, weights);
        new Percentile().setData(dataset, weights, 0, dataset.length);
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testsetDataPositiveWeights() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { -1, -1, -1, -1 };
        new Percentile().setData(dataset, weights);
        new Percentile().setData(dataset, weights, 0, dataset.length);
    }

    @Test(expected=NotPositiveException.class)
    public void testsetDataPositivIndex() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { 1, 1, 1, 1 };
        new Percentile().setData(dataset, weights, -1, dataset.length);
        new Percentile().setData(dataset, weights, 0, -1);
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testsetDataIndexOutBound() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { 1, 1, 1, 1 };
        new Percentile().setData(dataset, weights, 0, dataset.length+1);
    }

    @Test(expected=NullPointerException.class)
    public void testsetDataInputNull() {
        new Percentile().setData(null, null);
        new Percentile().setData(null, null, 0, 0);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testevaluateInputLength() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { 1, 1, 1 };
        Percentile p = new Percentile().withEstimationType(Percentile.EstimationType.R_7);
        p.setData(dataset, weights);
        p.evaluate(50);
        p.evaluate(dataset, weights, 50);
        p.evaluate(dataset, weights, 0, dataset.length);
        p.evaluate(dataset, weights, 0, dataset.length, 50);
    }

    @Test(expected=NotPositiveException.class)
    public void testevaluatePositivIndex() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { 1, 1, 1 ,1};
        Percentile p = new Percentile().withEstimationType(Percentile.EstimationType.R_7);
        p.setData(dataset, weights);
        p.evaluate(50);
        p.evaluate(dataset, weights, 50);
        p.evaluate(dataset, weights, -1, dataset.length);
        p.evaluate(dataset, weights, 0, -1, 50);
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testevaluatePositivWeights() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { -1, -1, -1 , -1};
        Percentile p = new Percentile().withEstimationType(Percentile.EstimationType.R_7);
        p.setData(dataset, weights);
        p.evaluate(50);
        p.evaluate(dataset, weights, 50);
        p.evaluate(dataset, weights, 0, dataset.length);
        p.evaluate(dataset, weights, 0, dataset.length, 50);
    }

    @Test(expected=NotANumberException.class)
    public void testevaluateNotANumber() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { 1, 1, 1, Double.NaN};
        Percentile p = new Percentile().withEstimationType(Percentile.EstimationType.R_7);
        p.setData(dataset, weights);
        p.evaluate(50);
        p.evaluate(dataset, weights, 50);
        p.evaluate(dataset, weights, 0, dataset.length);
        p.evaluate(dataset, weights, 0, dataset.length, 50);
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testevaluatePositiveWeights() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { -1, -1, -1, -1};
        Percentile p = new Percentile().withEstimationType(Percentile.EstimationType.R_7);
        p.setData(dataset, weights);
        p.evaluate(50);
        p.evaluate(dataset, weights, 50);
        p.evaluate(dataset, weights, 0, dataset.length);
        p.evaluate(dataset, weights, 0, dataset.length, 50);
    }

    @Test(expected=OutOfRangeException.class)
    public void testevaluatep() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { 1, 1, 1, 1};
        Percentile p = new Percentile().withEstimationType(Percentile.EstimationType.R_7);
        p.setData(dataset, weights);
        p.evaluate(101);
        p.evaluate(dataset, weights, 101);
        p.evaluate(dataset, weights, 0, dataset.length);
        p.evaluate(dataset, weights, 0, dataset.length, 101);
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testevaluateIndexBound() {
        double[] dataset = new double[] { 3, 4, 2, 9 };
        double[] weights = new double[] { 1, 1, 1, 1};
        Percentile p = new Percentile().withEstimationType(Percentile.EstimationType.R_7);
        p.setData(dataset, weights);
        p.evaluate(50);
        p.evaluate(dataset, weights, 50);
        p.evaluate(dataset, weights, 0, dataset.length + 1);
        p.evaluate(dataset, weights, 0, dataset.length + 1, 50);
    }
}
