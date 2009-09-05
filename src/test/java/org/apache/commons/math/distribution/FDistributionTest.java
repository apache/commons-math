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


/**
 * Test cases for FDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 * @version $Revision$ $Date$
 */
public class FDistributionTest extends ContinuousDistributionAbstractTest {

    /**
     * Constructor for FDistributionTest.
     * @param name
     */
    public FDistributionTest(String name) {
        super(name);
    }

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public ContinuousDistribution makeDistribution() {
        return new FDistributionImpl(5.0, 6.0);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 1.8.1 (linux version)
        return new double[] {0.03468084d ,0.09370091d, 0.1433137d,
            0.2020084d, 0.2937283d, 20.80266d, 8.745895d, 5.987565d,
            4.387374d, 3.107512d};
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
        setTolerance(4e-6);
    }

    //---------------------------- Additional test cases -------------------------

    public void testCumulativeProbabilityExtremes() throws Exception {
        setCumulativeTestPoints(new double[] {-2, 0});
        setCumulativeTestValues(new double[] {0, 0});
        verifyCumulativeProbabilities();
    }

    public void testInverseCumulativeProbabilityExtremes() throws Exception {
        setInverseCumulativeTestPoints(new double[] {0, 1});
        setInverseCumulativeTestValues(new double[] {0, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }

    public void testDfAccessors() {
        FDistribution distribution = (FDistribution) getDistribution();
        assertEquals(5d, distribution.getNumeratorDegreesOfFreedom(), Double.MIN_VALUE);
        distribution.setNumeratorDegreesOfFreedom(4d);
        assertEquals(4d, distribution.getNumeratorDegreesOfFreedom(), Double.MIN_VALUE);
        assertEquals(6d, distribution.getDenominatorDegreesOfFreedom(), Double.MIN_VALUE);
        distribution.setDenominatorDegreesOfFreedom(4d);
        assertEquals(4d, distribution.getDenominatorDegreesOfFreedom(), Double.MIN_VALUE);
        try {
            distribution.setNumeratorDegreesOfFreedom(0d);
            fail("Expecting IllegalArgumentException for df = 0");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            distribution.setDenominatorDegreesOfFreedom(0d);
            fail("Expecting IllegalArgumentException for df = 0");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testLargeDegreesOfFreedom() throws Exception {
        org.apache.commons.math.distribution.FDistributionImpl fd =
            new org.apache.commons.math.distribution.FDistributionImpl(
                100000., 100000.);
        double p = fd.cumulativeProbability(.999);
        double x = fd.inverseCumulativeProbability(p);
        assertEquals(.999, x, 1.0e-5);
    }

    public void testSmallDegreesOfFreedom() throws Exception {
        org.apache.commons.math.distribution.FDistributionImpl fd =
            new org.apache.commons.math.distribution.FDistributionImpl(
                1.0, 1.0);
        double p = fd.cumulativeProbability(0.975);
        double x = fd.inverseCumulativeProbability(p);
        assertEquals(0.975, x, 1.0e-5);

        fd.setDenominatorDegreesOfFreedom(2.0);
        p = fd.cumulativeProbability(0.975);
        x = fd.inverseCumulativeProbability(p);
        assertEquals(0.975, x, 1.0e-5);
    }

}
