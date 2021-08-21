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
package org.apache.commons.math4.legacy.stat.descriptive.moment;

import org.apache.commons.math4.legacy.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math4.legacy.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 *
 */
public class SkewnessTest extends StorelessUnivariateStatisticAbstractTest{

    protected Skewness stat;

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new Skewness();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.skew;
    }

    /**
     * Make sure Double.NaN is returned iff n < 3
     */
    @Test
    public void testNaN() {
        Skewness skew = new Skewness();
        Assert.assertTrue(Double.isNaN(skew.getResult()));
        skew.increment(1d);
        Assert.assertTrue(Double.isNaN(skew.getResult()));
        skew.increment(1d);
        Assert.assertTrue(Double.isNaN(skew.getResult()));
        skew.increment(1d);
        Assert.assertFalse(Double.isNaN(skew.getResult()));
    }

    @Test
    public void testZeroSkewness() {
        final double[] values = {2, 2, 2, 2};
        Assert.assertEquals(0, new Skewness().evaluate(values), 0.0);
    }
}
