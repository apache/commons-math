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
package org.apache.commons.math4.stat.descriptive;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link ListUnivariateImpl} class.
 *
 */

public final class ListUnivariateImplTest {

    private double one = 1;
    private float two = 2;
    private int three = 3;

    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double std = FastMath.sqrt(var);
    private double n = 4;
    private double min = 1;
    private double max = 3;
    private double tolerance = 10E-15;

    /** test stats */
    @Test
    public void testStats() {
        List<Object> externalList = new ArrayList<Object>();

        DescriptiveStatistics u = new ListUnivariateImpl( externalList );

        Assert.assertEquals("total count",0,u.getN(),tolerance);
        u.addValue(one);
        u.addValue(two);
        u.addValue(two);
        u.addValue(three);
        Assert.assertEquals("N",n,u.getN(),tolerance);
        Assert.assertEquals("sum",sum,u.getSum(),tolerance);
        Assert.assertEquals("sumsq",sumSq,u.getSumsq(),tolerance);
        Assert.assertEquals("var",var,u.getVariance(),tolerance);
        Assert.assertEquals("std",std,u.getStandardDeviation(),tolerance);
        Assert.assertEquals("mean",mean,u.getMean(),tolerance);
        Assert.assertEquals("min",min,u.getMin(),tolerance);
        Assert.assertEquals("max",max,u.getMax(),tolerance);
        u.clear();
        Assert.assertEquals("total count",0,u.getN(),tolerance);
    }

    @Test
    public void testN0andN1Conditions() {
        List<Object> list = new ArrayList<Object>();

        DescriptiveStatistics u = new ListUnivariateImpl( list );

        Assert.assertTrue("Mean of n = 0 set should be NaN", Double.isNaN( u.getMean() ) );
        Assert.assertTrue("Standard Deviation of n = 0 set should be NaN", Double.isNaN( u.getStandardDeviation() ) );
        Assert.assertTrue("Variance of n = 0 set should be NaN", Double.isNaN(u.getVariance() ) );

        list.add( Double.valueOf(one));

        Assert.assertTrue( "Mean of n = 1 set should be value of single item n1", u.getMean() == one);
        Assert.assertTrue( "StdDev of n = 1 set should be zero, instead it is: " + u.getStandardDeviation(), u.getStandardDeviation() == 0);
        Assert.assertTrue( "Variance of n = 1 set should be zero", u.getVariance() == 0);
    }

    @Test
    public void testSkewAndKurtosis() {
        DescriptiveStatistics u = new DescriptiveStatistics();

        double[] testArray = { 12.5, 12, 11.8, 14.2, 14.9, 14.5, 21, 8.2, 10.3, 11.3, 14.1,
                                             9.9, 12.2, 12, 12.1, 11, 19.8, 11, 10, 8.8, 9, 12.3 };
        for( int i = 0; i < testArray.length; i++) {
            u.addValue( testArray[i]);
        }

        Assert.assertEquals("mean", 12.40455, u.getMean(), 0.0001);
        Assert.assertEquals("variance", 10.00236, u.getVariance(), 0.0001);
        Assert.assertEquals("skewness", 1.437424, u.getSkewness(), 0.0001);
        Assert.assertEquals("kurtosis", 2.37719, u.getKurtosis(), 0.0001);
    }

    @Test
    public void testProductAndGeometricMean() {
        ListUnivariateImpl u = new ListUnivariateImpl(new ArrayList<Object>());
        u.setWindowSize(10);

        u.addValue( 1.0 );
        u.addValue( 2.0 );
        u.addValue( 3.0 );
        u.addValue( 4.0 );

        Assert.assertEquals( "Geometric mean not expected", 2.213364, u.getGeometricMean(), 0.00001 );

        // Now test rolling - StorelessDescriptiveStatistics should discount the contribution
        // of a discarded element
        for( int i = 0; i < 10; i++ ) {
            u.addValue( i + 2 );
        }
        // Values should be (2,3,4,5,6,7,8,9,10,11)

        Assert.assertEquals( "Geometric mean not expected", 5.755931, u.getGeometricMean(), 0.00001 );


    }

    /** test stats */
    @Test
    public void testSerialization() {

        DescriptiveStatistics u = new ListUnivariateImpl();

        Assert.assertEquals("total count",0,u.getN(),tolerance);
        u.addValue(one);
        u.addValue(two);

        DescriptiveStatistics u2 = (DescriptiveStatistics)TestUtils.serializeAndRecover(u);

        u2.addValue(two);
        u2.addValue(three);

        Assert.assertEquals("N",n,u2.getN(),tolerance);
        Assert.assertEquals("sum",sum,u2.getSum(),tolerance);
        Assert.assertEquals("sumsq",sumSq,u2.getSumsq(),tolerance);
        Assert.assertEquals("var",var,u2.getVariance(),tolerance);
        Assert.assertEquals("std",std,u2.getStandardDeviation(),tolerance);
        Assert.assertEquals("mean",mean,u2.getMean(),tolerance);
        Assert.assertEquals("min",min,u2.getMin(),tolerance);
        Assert.assertEquals("max",max,u2.getMax(),tolerance);

        u2.clear();
        Assert.assertEquals("total count",0,u2.getN(),tolerance);
    }
}

