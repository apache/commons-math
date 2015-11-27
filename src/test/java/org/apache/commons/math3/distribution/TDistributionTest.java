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
package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.math3.TestUtils;
/**
 * Test cases for TDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 */
public class TDistributionTest extends RealDistributionAbstractTest {

//-------------- Implementations for abstract methods -----------------------

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public TDistribution makeDistribution() {
        return new TDistribution(5.0);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 2.9.2
        return new double[] {-5.89342953136, -3.36492999891, -2.57058183564, -2.01504837333, -1.47588404882,
                5.89342953136, 3.36492999891, 2.57058183564, 2.01504837333, 1.47588404882};
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001, 0.01, 0.025, 0.05, 0.1, 0.999,
                0.990, 0.975, 0.950, 0.900};
    }

    /** Creates the default probability density test expected values */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] {0.000756494565517, 0.0109109752919, 0.0303377878006, 0.0637967988952, 0.128289492005,
                0.000756494565517, 0.0109109752919, 0.0303377878006, 0.0637967988952, 0.128289492005};
    }

    // --------------------- Override tolerance  --------------
    @Override
    public void setUp() {
        super.setUp();
        setTolerance(1E-9);
    }

    //---------------------------- Additional test cases -------------------------
    /**
     * @see <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=27243">
     *      Bug report that prompted this unit test.</a>
     */
    @Test
    public void testCumulativeProbabilityAgainstStackOverflow() {
        TDistribution td = new TDistribution(5.);
        td.cumulativeProbability(.1);
        td.cumulativeProbability(.01);
    }

    @Test
    public void testSmallDf() {
        setDistribution(new TDistribution(1d));
        // quantiles computed using R version 2.9.2
        setCumulativeTestPoints(new double[] {-318.308838986, -31.8205159538, -12.7062047362,
                -6.31375151468, -3.07768353718, 318.308838986, 31.8205159538, 12.7062047362,
                 6.31375151468, 3.07768353718});
        setDensityTestValues(new double[] {3.14158231817e-06, 0.000314055924703, 0.00195946145194,
                0.00778959736375, 0.0303958893917, 3.14158231817e-06, 0.000314055924703,
                0.00195946145194, 0.00778959736375, 0.0303958893917});
        setInverseCumulativeTestValues(getCumulativeTestPoints());
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
        verifyDensities();
    }

    @Test
    public void testInverseCumulativeProbabilityExtremes() {
        setInverseCumulativeTestPoints(new double[] {0, 1});
        setInverseCumulativeTestValues(
                new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }

    @Test
    public void testCumulativeProbablilityExtremes() {
        TDistribution dist;
        for (int i = 1; i < 11; i++) {
            dist = new TDistribution(i * 5);
            Assert.assertEquals(1,
                dist.cumulativeProbability(Double.POSITIVE_INFINITY), Double.MIN_VALUE);
            Assert.assertEquals(0,
                dist.cumulativeProbability(Double.NEGATIVE_INFINITY), Double.MIN_VALUE);
        }
    }

    @Test
    public void testDfAccessors() {
        TDistribution dist = (TDistribution) getDistribution();
        Assert.assertEquals(5d, dist.getDegreesOfFreedom(), Double.MIN_VALUE);
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testPreconditions() {
        new TDistribution(0);
    }

    @Test
    public void testMoments() {
        final double tol = 1e-9;
        TDistribution dist;

        dist = new TDistribution(1);
        Assert.assertTrue(Double.isNaN(dist.getNumericalMean()));
        Assert.assertTrue(Double.isNaN(dist.getNumericalVariance()));

        dist = new TDistribution(1.5);
        Assert.assertEquals(dist.getNumericalMean(), 0, tol);
        Assert.assertTrue(Double.isInfinite(dist.getNumericalVariance()));

        dist = new TDistribution(5);
        Assert.assertEquals(dist.getNumericalMean(), 0, tol);
        Assert.assertEquals(dist.getNumericalVariance(), 5d / (5d - 2d), tol);
    }

    /*
     * Adding this test to benchmark against tables published by NIST
     * http://itl.nist.gov/div898/handbook/eda/section3/eda3672.htm
     * Have chosen tabulated results for degrees of freedom 2,10,30,100
     * Have chosen problevels from 0.10 to 0.001
     */
    @Test
    public void nistData(){
        double[] prob = new double[]{ 0.10,0.05,0.025,0.01,0.005,0.001};
        double[] args2 = new double[]{1.886,2.920,4.303,6.965,9.925,22.327};
        double[] args10 = new double[]{1.372,1.812,2.228,2.764,3.169,4.143};
        double[] args30 = new double[]{1.310,1.697,2.042,2.457,2.750,3.385};
        double[] args100= new double[]{1.290,1.660,1.984,2.364,2.626,3.174};
        TestUtils.assertEquals(prob, makeNistResults(args2, 2), 1.0e-4);
        TestUtils.assertEquals(prob, makeNistResults(args10, 10), 1.0e-4);
        TestUtils.assertEquals(prob, makeNistResults(args30, 30), 1.0e-4);
        TestUtils.assertEquals(prob, makeNistResults(args100, 100), 1.0e-4);
        return;
    }
    private double[] makeNistResults(double[] args, int df){
        TDistribution td =  new TDistribution(df);
        double[] res  = new double[ args.length ];
        for( int i = 0 ; i < res.length ; i++){
            res[i] = 1.0 - td.cumulativeProbability(args[i]);
        }
        return res;
    }
}
