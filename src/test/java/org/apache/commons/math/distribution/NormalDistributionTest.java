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

package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

/**
 * Test cases for NormalDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 * @version $Revision$ $Date$
 */
public class NormalDistributionTest extends ContinuousDistributionAbstractTest  {

    /**
     * Constructor for NormalDistributionTest.
     * @param arg0
     */
    public NormalDistributionTest(String arg0) {
        super(arg0);
    }

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public ContinuousDistribution makeDistribution() {
        return new NormalDistributionImpl(2.1, 1.4);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R
        return new double[] {-2.226325d, -1.156887d, -0.6439496d, -0.2027951d, 0.3058278d,
                6.426325d, 5.356887d, 4.84395d, 4.402795d, 3.894172d};
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d};
    }

    // --------------------- Override tolerance  --------------
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setTolerance(1E-6);
    }

    //---------------------------- Additional test cases -------------------------

    private void verifyQuantiles() throws Exception {
        NormalDistribution distribution = (NormalDistribution) getDistribution();
        double mu = distribution.getMean();
        double sigma = distribution.getStandardDeviation();
        setCumulativeTestPoints( new double[] {mu - 2 *sigma, mu - sigma,
                mu, mu + sigma, mu +2 * sigma,  mu +3 * sigma, mu + 4 * sigma,
                mu + 5 * sigma});
        // Quantiles computed using R (same as Mathematica)
        setCumulativeTestValues(new double[] {0.02275013, 0.1586553, 0.5, 0.8413447,
                0.9772499, 0.9986501, 0.9999683,  0.9999997});
        verifyCumulativeProbabilities();
    }

    public void testQuantiles() throws Exception {
        verifyQuantiles();
        setDistribution(new NormalDistributionImpl(0, 1));
        verifyQuantiles();
        setDistribution(new NormalDistributionImpl(0, 0.1));
        verifyQuantiles();
    }

    public void testInverseCumulativeProbabilityExtremes() throws Exception {
        setInverseCumulativeTestPoints(new double[] {0, 1});
        setInverseCumulativeTestValues(
                new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }

    public void testGetMean() {
        NormalDistribution distribution = (NormalDistribution) getDistribution();
        assertEquals(2.1, distribution.getMean(), 0);
    }

    public void testSetMean() throws Exception {
        double mu = Math.random();
        NormalDistribution distribution = (NormalDistribution) getDistribution();
        distribution.setMean(mu);
        verifyQuantiles();
    }

    public void testGetStandardDeviation() {
        NormalDistribution distribution = (NormalDistribution) getDistribution();
        assertEquals(1.4, distribution.getStandardDeviation(), 0);
    }

    public void testSetStandardDeviation() throws Exception {
        double sigma = 0.1d + Math.random();
        NormalDistribution distribution = (NormalDistribution) getDistribution();
        distribution.setStandardDeviation(sigma);
        assertEquals(sigma, distribution.getStandardDeviation(), 0);
        verifyQuantiles();
        try {
            distribution.setStandardDeviation(0);
            fail("Expecting IllegalArgumentException for sd = 0");
        } catch (IllegalArgumentException ex) {
            // Expected
        }
    }

    public void testDensity() {
        double [] x = new double[]{-2, -1, 0, 1, 2};
        // R 2.5: print(dnorm(c(-2,-1,0,1,2)), digits=10)
        checkDensity(0, 1, x, new double[]{0.05399096651, 0.24197072452, 0.39894228040, 0.24197072452, 0.05399096651});
        // R 2.5: print(dnorm(c(-2,-1,0,1,2), mean=1.1), digits=10)
        checkDensity(1.1, 1, x, new double[]{0.003266819056,0.043983595980,0.217852177033,0.396952547477,0.266085249899});
    }

    private void checkDensity(double mean, double sd, double[] x, double[] expected) {
        NormalDistribution d = new NormalDistributionImpl(mean, sd);
        for (int i = 0; i < x.length; i++) {
            assertEquals(expected[i], d.density(x[i]), 1e-9);
        }
    }

    /**
     * Check to make sure top-coding of extreme values works correctly.
     * Verifies fix for JIRA MATH-167
     */
    public void testExtremeValues() throws Exception {
        NormalDistribution distribution = (NormalDistribution) getDistribution();
        distribution.setMean(0);
        distribution.setStandardDeviation(1);
        for (int i = 0; i < 100; i+=5) { // make sure no convergence exception
            double lowerTail = distribution.cumulativeProbability(-i);
            double upperTail = distribution.cumulativeProbability(i);
            if (i < 10) { // make sure not top-coded
                assertTrue(lowerTail > 0.0d);
                assertTrue(upperTail < 1.0d);
            }
            else { // make sure top coding not reversed
                assertTrue(lowerTail < 0.00001);
                assertTrue(upperTail > 0.99999);
            }
        }
   }

    public void testMath280() throws MathException {
        NormalDistribution normal = new NormalDistributionImpl(0,1);
        double result = normal.inverseCumulativeProbability(0.9772498680518209);
        assertEquals(2.0, result, 1.0e-12);
    }

}
