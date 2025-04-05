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
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link StorelessUnivariateStatistic} classes.
 */
public abstract class StorelessUnivariateStatisticAbstractTest
    extends UnivariateStatisticAbstractTest {

    /** Small sample arrays */
    protected double[][] smallSamples = {{}, {1}, {1,2}, {1,2,3}, {1,2,3,4}};

    /** Return a new instance of the statistic */
    @Override
    public abstract UnivariateStatistic getUnivariateStatistic();

    /**Expected value for  the testArray defined in UnivariateStatisticAbstractTest */
    @Override
    public abstract double expectedValue();

    /**
     *  Verifies that increment() and incrementAll work properly.
     */
    @Test
    public void testIncrementation() {

        StorelessUnivariateStatistic statistic =
            (StorelessUnivariateStatistic) getUnivariateStatistic();

        // Add testArray one value at a time and check result
        for (int i = 0; i < testArray.length; i++) {
            statistic.increment(testArray[i]);
        }

        Assert.assertEquals(expectedValue(), statistic.getResult(), getTolerance());
        Assert.assertEquals(testArray.length, statistic.getN());

        statistic.clear();

        // Add testArray all at once and check again
        statistic.incrementAll(testArray);
        Assert.assertEquals(expectedValue(), statistic.getResult(), getTolerance());
        Assert.assertEquals(testArray.length, statistic.getN());

        statistic.clear();

        // Cleared
        checkClearValue(statistic);
        Assert.assertEquals(0, statistic.getN());
    }

    protected void checkClearValue(StorelessUnivariateStatistic statistic){
        Assert.assertTrue(Double.isNaN(statistic.getResult()));
    }

    @Test
    public void testEqualsAndHashCode() {
        StorelessUnivariateStatistic statistic =
            (StorelessUnivariateStatistic) getUnivariateStatistic();
        StorelessUnivariateStatistic statistic2 = null;

        Assert.assertFalse("non-null, compared to null", statistic.equals(statistic2));
        Assert.assertEquals("reflexive, non-null", statistic, statistic);

        int emptyHash = statistic.hashCode();
        statistic2 = (StorelessUnivariateStatistic) getUnivariateStatistic();
        Assert.assertEquals("empty stats should be equal", statistic, statistic2);
        Assert.assertEquals("empty stats should have the same hash code",
                emptyHash, statistic2.hashCode());

        statistic.increment(1d);
        Assert.assertEquals("reflexive, non-empty", statistic, statistic);
        Assert.assertNotEquals("non-empty, compared to empty", statistic, statistic2);
        Assert.assertNotEquals("non-empty, compared to empty", statistic2, statistic);
        Assert.assertTrue("non-empty stat should have different hash code from empty stat",
                statistic.hashCode() != emptyHash);

        statistic2.increment(1d);
        Assert.assertEquals("stats with same data should be equal", statistic, statistic2);
        Assert.assertEquals("stats with same data should have the same hash code",
                statistic.hashCode(), statistic2.hashCode());

        statistic.increment(Double.POSITIVE_INFINITY);
        Assert.assertNotEquals("stats with different n's should not be equal", statistic2, statistic);
        Assert.assertTrue("stats with different n's should have different hash codes",
                statistic.hashCode() != statistic2.hashCode());

        statistic2.increment(Double.POSITIVE_INFINITY);
        Assert.assertEquals("stats with same data should be equal", statistic, statistic2);
        Assert.assertEquals("stats with same data should have the same hash code",
                statistic.hashCode(), statistic2.hashCode());

        statistic.clear();
        statistic2.clear();
        Assert.assertEquals("cleared stats should be equal", statistic, statistic2);
        Assert.assertEquals("cleared stats should have thash code of empty stat",
                emptyHash, statistic2.hashCode());
        Assert.assertEquals("cleared stats should have thash code of empty stat",
                emptyHash, statistic.hashCode());
    }

    /**
     * Make sure that evaluate(double[]) and inrementAll(double[]),
     * getResult() give same results.
     */
    @Test
    public void testConsistency() {
        StorelessUnivariateStatistic stat = (StorelessUnivariateStatistic) getUnivariateStatistic();
        stat.incrementAll(testArray);
        Assert.assertEquals(stat.getResult(), stat.evaluate(testArray), getTolerance());
        for (int i = 0; i < smallSamples.length; i++) {
            stat.clear();
            for (int j =0; j < smallSamples[i].length; j++) {
                stat.increment(smallSamples[i][j]);
            }
            TestUtils.assertEquals(stat.getResult(), stat.evaluate(smallSamples[i]), getTolerance());
        }
    }

    /**
     * Verifies that copied statistics remain equal to originals when
     * incremented the same way.
     */
    @Test
    public void testCopyConsistency() {

        StorelessUnivariateStatistic master =
            (StorelessUnivariateStatistic) getUnivariateStatistic();

        StorelessUnivariateStatistic replica = null;

        // Randomly select a portion of testArray to load first
        long index = JdkMath.round((JdkMath.random()) * testArray.length);

        // Put first half in master and copy master to replica
        master.incrementAll(testArray, 0, (int) index);
        replica = master.copy();

        // Check same
        Assert.assertEquals(replica, master);
        Assert.assertEquals(master, replica);

        // Now add second part to both and check again
        master.incrementAll(testArray,
                (int) index, (int) (testArray.length - index));
        replica.incrementAll(testArray,
                (int) index, (int) (testArray.length - index));
        Assert.assertEquals(replica, master);
        Assert.assertEquals(master, replica);
    }

    /**
     * Make sure that evaluate(double[]) does not alter the internal state.
     */
    @Test
    public void testEvaluateInternalState() {
        StorelessUnivariateStatistic stat = (StorelessUnivariateStatistic) getUnivariateStatistic();
        stat.evaluate(testArray);
        Assert.assertEquals(0, stat.getN());

        stat.incrementAll(testArray);

        StorelessUnivariateStatistic savedStatistic = stat.copy();

        Assert.assertNotEquals(stat.getResult(), stat.evaluate(testArray, 0, 5), getTolerance());

        Assert.assertEquals(savedStatistic.getResult(), stat.getResult(), 0.0);
        Assert.assertEquals(savedStatistic.getN(), stat.getN());
    }
}
