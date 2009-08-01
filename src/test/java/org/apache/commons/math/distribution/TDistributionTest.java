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
 * Test cases for TDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 * @version $Revision$ $Date$
 */
public class TDistributionTest extends ContinuousDistributionAbstractTest {

    /**
     * Constructor for TDistributionTest.
     * @param name
     */
    public TDistributionTest(String name) {
        super(name);
    }

//-------------- Implementations for abstract methods -----------------------

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public ContinuousDistribution makeDistribution() {
        return new TDistributionImpl(5.0);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 1.8.1 (linux version)
        return new double[] {-5.89343,-3.36493, -2.570582, -2.015048,
            -1.475884, 0.0, 5.89343, 3.36493, 2.570582,
            2.015048, 1.475884};
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.5d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d};
    }

    // --------------------- Override tolerance  --------------
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setTolerance(1E-6);
    }

    //---------------------------- Additional test cases -------------------------
    /**
     * @see <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=27243">
     *      Bug report that prompted this unit test.</a>
     */
    public void testCumulativeProbabilityAgaintStackOverflow() throws Exception {
        TDistributionImpl td = new TDistributionImpl(5.);
        td.cumulativeProbability(.1);
        td.cumulativeProbability(.01);
    }

    public void testSmallDf() throws Exception {
        setDistribution(new TDistributionImpl(1d));
        setTolerance(1E-4);
        // quantiles computed using R version 1.8.1 (linux version)
        setCumulativeTestPoints(new double[] {-318.3088, -31.82052, -12.70620, -6.313752,
            -3.077684, 0.0, 318.3088, 31.82052, 12.70620,
            6.313752, 3.077684});
        setInverseCumulativeTestValues(getCumulativeTestPoints());
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
    }

    public void testInverseCumulativeProbabilityExtremes() throws Exception {
        setInverseCumulativeTestPoints(new double[] {0, 1});
        setInverseCumulativeTestValues(
                new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }

    public void testDfAccessors() {
        TDistribution distribution = (TDistribution) getDistribution();
        assertEquals(5d, distribution.getDegreesOfFreedom(), Double.MIN_VALUE);
        distribution.setDegreesOfFreedom(4d);
        assertEquals(4d, distribution.getDegreesOfFreedom(), Double.MIN_VALUE);
        try {
            distribution.setDegreesOfFreedom(0d);
            fail("Expecting IllegalArgumentException for df = 0");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

}
