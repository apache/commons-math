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
package org.apache.commons.math3.stat.descriptive.rank;

import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 */
public class MinTest extends StorelessUnivariateStatisticAbstractTest{

    protected Min stat;

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new Min();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.min;
    }

    @Test
    public void testSpecialValues() {
        double[] testArray = {0d, Double.NaN, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY};
        Min min = new Min();
        Assert.assertTrue(Double.isNaN(min.getResult()));
        min.increment(testArray[0]);
        Assert.assertEquals(0d, min.getResult(), 0);
        min.increment(testArray[1]);
        Assert.assertEquals(0d, min.getResult(), 0);
        min.increment(testArray[2]);
        Assert.assertEquals(0d, min.getResult(), 0);
        min.increment(testArray[3]);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, min.getResult(), 0);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, min.evaluate(testArray), 0);
    }

    @Test
    public void testNaNs() {
        Min min = new Min();
        double nan = Double.NaN;
        Assert.assertEquals(2d, min.evaluate(new double[]{nan, 2d, 3d}), 0);
        Assert.assertEquals(1d, min.evaluate(new double[]{1d, nan, 3d}), 0);
        Assert.assertEquals(1d, min.evaluate(new double[]{1d, 2d, nan}), 0);
        Assert.assertTrue(Double.isNaN(min.evaluate(new double[]{nan, nan, nan})));
    }

}
