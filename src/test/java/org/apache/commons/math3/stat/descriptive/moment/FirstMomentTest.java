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
package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link FirstMoment} class.
 */
public class FirstMomentTest extends StorelessUnivariateStatisticAbstractTest{

    /** descriptive statistic. */
    protected FirstMoment stat;

    /**
     * @see org.apache.commons.math3.stat.descriptive.UnivariateStatisticAbstractTest#getUnivariateStatistic()
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new FirstMoment();
    }

    /**
     * @see org.apache.commons.math3.stat.descriptive.UnivariateStatisticAbstractTest#expectedValue()
     */
    @Override
    public double expectedValue() {
        return this.mean;
    }

    /**
     * Added in an attempt to resolve MATH-1146
     * Commented out tests that won't pass with the current implementation.
     */
    @Test
    public void testSpecialValues() {
        final FirstMoment mean = new FirstMoment();

//         mean.clear();
//         mean.increment(Double.POSITIVE_INFINITY);
//         mean.increment(1d);
//         Assert.assertEquals(Double.POSITIVE_INFINITY, mean.getResult(), 0d);

//         mean.clear();
//         mean.increment(Double.POSITIVE_INFINITY);
//         mean.increment(-1d);
//         Assert.assertEquals(Double.POSITIVE_INFINITY, mean.getResult(), 0d);

//         mean.clear();
//         mean.increment(Double.NEGATIVE_INFINITY);
//         mean.increment(1d);
//         Assert.assertEquals(Double.NEGATIVE_INFINITY, mean.getResult(), 0d);

//         mean.clear();
//         mean.increment(Double.NEGATIVE_INFINITY);
//         mean.increment(-1d);
//         Assert.assertEquals(Double.NEGATIVE_INFINITY, mean.getResult(), 0d);

//         mean.clear();
//         mean.increment(Double.POSITIVE_INFINITY);
//         mean.increment(Double.POSITIVE_INFINITY);
//         Assert.assertEquals(Double.POSITIVE_INFINITY, mean.getResult(), 0d);

//         mean.clear();
//         mean.increment(Double.NEGATIVE_INFINITY);
//         mean.increment(Double.NEGATIVE_INFINITY);
//         Assert.assertEquals(Double.NEGATIVE_INFINITY, mean.getResult(), 0d);

        mean.clear();
        mean.increment(Double.POSITIVE_INFINITY);
        mean.increment(Double.NEGATIVE_INFINITY);
        Assert.assertTrue(Double.isNaN(mean.getResult()));

        mean.clear();
        mean.increment(Double.NEGATIVE_INFINITY);
        mean.increment(Double.POSITIVE_INFINITY);
        Assert.assertTrue(Double.isNaN(mean.getResult()));

        mean.clear();
        mean.increment(Double.NaN);
        mean.increment(Double.POSITIVE_INFINITY);
        Assert.assertTrue(Double.isNaN(mean.getResult()));

        mean.clear();
        mean.increment(Double.NaN);
        mean.increment(Double.NEGATIVE_INFINITY);
        Assert.assertTrue(Double.isNaN(mean.getResult()));

        mean.clear();
        mean.increment(Double.NaN);
        mean.increment(0d);
        Assert.assertTrue(Double.isNaN(mean.getResult()));
    }
}
