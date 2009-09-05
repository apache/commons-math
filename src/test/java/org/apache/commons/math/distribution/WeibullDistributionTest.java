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
 * Test cases for WeibullDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 * @version $Revision$ $Date$
 */
public class WeibullDistributionTest extends ContinuousDistributionAbstractTest  {

    /**
     * Constructor for CauchyDistributionTest.
     * @param arg0
     */
    public WeibullDistributionTest(String arg0) {
        super(arg0);
    }

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public ContinuousDistribution makeDistribution() {
        return new WeibullDistributionImpl(1.2, 2.1);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using Mathematica
        return new double[] {0.00664355181d, 0.04543282833d, 0.09811627374d,
                0.1767135246d, 0.3219468654d, 4.207902826d, 5.23968437d,
                6.232056007d, 7.497630467d, 10.51154969d};
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.900d, 0.950d,
                0.975d, 0.990d, 0.999d};
    }

    //---------------------------- Additional test cases -------------------------

    public void testInverseCumulativeProbabilityExtremes() throws Exception {
        setInverseCumulativeTestPoints(new double[] {0.0, 1.0});
        setInverseCumulativeTestValues(
                new double[] {0.0, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }

    public void testAlpha() {
        WeibullDistribution distribution = (WeibullDistribution) getDistribution();
        double expected = Math.random();
        distribution.setShape(expected);
        assertEquals(expected, distribution.getShape(), 0.0);
    }

    public void testBeta() {
        WeibullDistribution distribution = (WeibullDistribution) getDistribution();
        double expected = Math.random();
        distribution.setScale(expected);
        assertEquals(expected, distribution.getScale(), 0.0);
    }

    public void testSetAlpha() {
        WeibullDistribution distribution = (WeibullDistribution) getDistribution();
        try {
            distribution.setShape(0.0);
            fail("Can not have 0.0 alpha.");
        } catch (IllegalArgumentException ex) {
            // success
        }

        try {
            distribution.setShape(-1.0);
            fail("Can not have negative alpha.");
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    public void testSetBeta() {
        WeibullDistribution distribution = (WeibullDistribution) getDistribution();
        try {
            distribution.setScale(0.0);
            fail("Can not have 0.0 beta.");
        } catch (IllegalArgumentException ex) {
            // success
        }

        try {
            distribution.setScale(-1.0);
            fail("Can not have negative beta.");
        } catch (IllegalArgumentException ex) {
            // success
        }
    }
}
