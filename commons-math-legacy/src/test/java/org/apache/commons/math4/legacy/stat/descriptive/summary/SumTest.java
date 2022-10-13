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
package org.apache.commons.math4.legacy.stat.descriptive.summary;

import org.apache.commons.math4.legacy.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math4.legacy.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math4.legacy.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link Sum} class.
 */
public class SumTest extends StorelessUnivariateStatisticAbstractTest{

    protected Sum stat;

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new Sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.sum;
    }

    /**Expected value for  the testArray defined in UnivariateStatisticAbstractTest */
    public double expectedWeightedValue() {
        return this.weightedSum;
    }

    @Test
    public void testSpecialValues() {
        Sum sum = new Sum();
        Assert.assertEquals(0, sum.getResult(), 0);
        sum.increment(1);
        Assert.assertEquals(1, sum.getResult(), 0);
        sum.increment(Double.POSITIVE_INFINITY);
        Assert.assertEquals(Double.POSITIVE_INFINITY, sum.getResult(), 0);
        sum.increment(Double.NEGATIVE_INFINITY);
        Assert.assertTrue(Double.isNaN(sum.getResult()));
        sum.increment(1);
        Assert.assertTrue(Double.isNaN(sum.getResult()));
    }

    @Test
    public void testWeightedSum() {
        Sum sum = new Sum();
        Assert.assertEquals(expectedWeightedValue(),
                            sum.evaluate(testArray, testWeightsArray, 0, testArray.length), getTolerance());
        Assert.assertEquals(expectedValue(),
                            sum.evaluate(testArray, unitWeightsArray, 0, testArray.length), getTolerance());
    }

    @Override
    protected void checkClearValue(StorelessUnivariateStatistic statistic) {
        Assert.assertEquals(0, statistic.getResult(), 0);
    }
}
