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

import org.apache.commons.math4.legacy.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test cases for the {@link ListUnivariateImpl} class.
 */
public final class MixedListUnivariateImplTest {
    private final double one = 1;
    private final float two = 2;
    private final int three = 3;

    private final double mean = 2;
    private final double sumSq = 18;
    private final double sum = 8;
    private final double var = 0.666666666666666666667;
    private final double std = FastMath.sqrt(var);
    private final double n = 4;
    private final double min = 1;
    private final double max = 3;
    private final double tolerance = 10E-15;


    public MixedListUnivariateImplTest() {


    }

    /** test stats */
    @Test
    public void testStats() {
        List<Double> externalList = new ArrayList<>();

        DescriptiveStatistics u = new ListUnivariateImpl(externalList);

        Assert.assertEquals("total count", 0, u.getN(), tolerance);
        u.addValue(one);
        u.addValue(two);
        u.addValue(two);
        u.addValue(three);
        Assert.assertEquals("N", n, u.getN(), tolerance);
        Assert.assertEquals("sum", sum, u.getSum(), tolerance);
        Assert.assertEquals("sumsq", sumSq, u.getSumsq(), tolerance);
        Assert.assertEquals("var", var, u.getVariance(), tolerance);
        Assert.assertEquals("std", std, u.getStandardDeviation(), tolerance);
        Assert.assertEquals("mean", mean, u.getMean(), tolerance);
        Assert.assertEquals("min", min, u.getMin(), tolerance);
        Assert.assertEquals("max", max, u.getMax(), tolerance);
        u.clear();
        Assert.assertEquals("total count", 0, u.getN(), tolerance);
    }

    @Test
    public void testN0andN1Conditions() {
        DescriptiveStatistics u = new ListUnivariateImpl(new ArrayList<>());

        Assert.assertTrue(
            "Mean of n = 0 set should be NaN",
            Double.isNaN(u.getMean()));
        Assert.assertTrue(
            "Standard Deviation of n = 0 set should be NaN",
            Double.isNaN(u.getStandardDeviation()));
        Assert.assertTrue(
            "Variance of n = 0 set should be NaN",
            Double.isNaN(u.getVariance()));

        u.addValue(one);

        Assert.assertTrue(
            "Mean of n = 1 set should be value of single item n1, instead it is " + u.getMean() ,
            u.getMean() == one);

        Assert.assertTrue(
            "StdDev of n = 1 set should be zero, instead it is: "
                + u.getStandardDeviation(),
            u.getStandardDeviation() == 0);
        Assert.assertTrue(
            "Variance of n = 1 set should be zero",
            u.getVariance() == 0);
    }

    @Test
    public void testSkewAndKurtosis() {
        ListUnivariateImpl u =
            new ListUnivariateImpl(new ArrayList<>());

        u.addValue(12.5);
        u.addValue(12);
        u.addValue(11.8);
        u.addValue(14.2);
        u.addValue(14.5);
        u.addValue(14.9);
        u.addValue(12.0);
        u.addValue(21);
        u.addValue(8.2);
        u.addValue(10.3);
        u.addValue(11.3);
        u.addValue(14.1f);
        u.addValue(9.9);
        u.addValue(12.2);
        u.addValue(12.1);
        u.addValue(11);
        u.addValue(19.8);
        u.addValue(11);
        u.addValue(10);
        u.addValue(8.8);
        u.addValue(9);
        u.addValue(12.3);


        Assert.assertEquals("mean", 12.40455, u.getMean(), 0.0001);
        Assert.assertEquals("variance", 10.00236, u.getVariance(), 0.0001);
        Assert.assertEquals("skewness", 1.437424, u.getSkewness(), 0.0001);
        Assert.assertEquals("kurtosis", 2.37719, u.getKurtosis(), 0.0001);
    }

    @Test
    public void testProductAndGeometricMean() {
        ListUnivariateImpl u = new ListUnivariateImpl(new ArrayList<>());
        u.setWindowSize(10);

        u.addValue(1.0);
        u.addValue(2.0);
        u.addValue(3.0);
        u.addValue(4.0);

        Assert.assertEquals(
            "Geometric mean not expected",
            2.213364,
            u.getGeometricMean(),
            0.00001);

        // Now test rolling - StorelessDescriptiveStatistics should discount the contribution
        // of a discarded element
        for (int i = 0; i < 10; i++) {
            u.addValue(i + 2);
        }
        // Values should be (2,3,4,5,6,7,8,9,10,11)
        Assert.assertEquals(
            "Geometric mean not expected",
            5.755931,
            u.getGeometricMean(),
            0.00001);

    }
}
