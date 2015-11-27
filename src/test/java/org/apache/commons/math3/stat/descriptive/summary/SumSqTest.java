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
package org.apache.commons.math3.stat.descriptive.summary;

import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link SumOfSquares} class.
 *
 */
public class SumSqTest extends StorelessUnivariateStatisticAbstractTest{

    protected SumOfSquares stat;

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new SumOfSquares();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.sumSq;
    }

    @Test
    public void testSpecialValues() {
        SumOfSquares sumSq = new SumOfSquares();
        Assert.assertEquals(0, sumSq.getResult(), 0);
        sumSq.increment(2d);
        Assert.assertEquals(4d, sumSq.getResult(), 0);
        sumSq.increment(Double.POSITIVE_INFINITY);
        Assert.assertEquals(Double.POSITIVE_INFINITY, sumSq.getResult(), 0);
        sumSq.increment(Double.NEGATIVE_INFINITY);
        Assert.assertEquals(Double.POSITIVE_INFINITY, sumSq.getResult(), 0);
        sumSq.increment(Double.NaN);
        Assert.assertTrue(Double.isNaN(sumSq.getResult()));
        sumSq.increment(1);
        Assert.assertTrue(Double.isNaN(sumSq.getResult()));
    }

    @Override
    protected void checkClearValue(StorelessUnivariateStatistic statistic){
        Assert.assertEquals(0, statistic.getResult(), 0);
    }


}
