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
package org.apache.commons.math4.stat.descriptive.moment;

import org.apache.commons.math4.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math4.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math4.stat.descriptive.moment.Mean;
import org.apache.commons.math4.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math4.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 *
 */
public class StandardDeviationTest extends StorelessUnivariateStatisticAbstractTest{

    protected StandardDeviation stat;

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new StandardDeviation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.std;
    }

    /**
     * Make sure Double.NaN is returned iff n = 0
     *
     */
    @Test
    public void testNaN() {
        StandardDeviation std = new StandardDeviation();
        Assert.assertTrue(Double.isNaN(std.getResult()));
        std.increment(1d);
        Assert.assertEquals(0d, std.getResult(), 0);
    }

    /**
     * Test population version of variance
     */
    @Test
    public void testPopulation() {
        double[] values = {-1.0d, 3.1d, 4.0d, -2.1d, 22d, 11.7d, 3d, 14d};
        double sigma = populationStandardDeviation(values);
        SecondMoment m = new SecondMoment();
        m.incrementAll(values);  // side effect is to add values
        StandardDeviation s1 = new StandardDeviation();
        s1.setBiasCorrected(false);
        Assert.assertEquals(sigma, s1.evaluate(values), 1E-14);
        s1.incrementAll(values);
        Assert.assertEquals(sigma, s1.getResult(), 1E-14);
        s1 = new StandardDeviation(false, m);
        Assert.assertEquals(sigma, s1.getResult(), 1E-14);
        s1 = new StandardDeviation(false);
        Assert.assertEquals(sigma, s1.evaluate(values), 1E-14);
        s1.incrementAll(values);
        Assert.assertEquals(sigma, s1.getResult(), 1E-14);
    }

    /**
     * Definitional formula for population standard deviation
     */
    protected double populationStandardDeviation(double[] v) {
        double mean = new Mean().evaluate(v);
        double sum = 0;
        for (int i = 0; i < v.length; i++) {
            sum += (v[i] - mean) * (v[i] - mean);
        }
        return FastMath.sqrt(sum / v.length);
    }

}
