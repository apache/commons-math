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
 * Test cases for CauchyDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 * 
 * @version $Revision$ $Date$
 */
public class CauchyDistributionTest extends ContinuousDistributionAbstractTest  {
    
    /**
     * Constructor for CauchyDistributionTest.
     * @param arg0
     */
    public CauchyDistributionTest(String arg0) {
        super(arg0);
    }
    
    //-------------- Implementations for abstract methods -----------------------
    
    /** Creates the default continuous distribution instance to use in tests. */
    public ContinuousDistribution makeDistribution() {
        return new CauchyDistributionImpl(1.2, 2.1);
    }   
    
    /** Creates the default cumulative probability distribution test input values */
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using Mathematica 
        return new double[] {-667.2485619d, -65.6230835d, -25.48302995d,
                -12.05887818d, -5.263135428d, 7.663135428d, 14.45887818d,
                27.88302995d, 68.0230835d, 669.6485619d};
    }
    
    /** Creates the default cumulative probability density test expected values */
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.900d, 0.950d,
                0.975d, 0.990d, 0.999d};
    }
    
    //---------------------------- Additional test cases -------------------------
    
    public void testInverseCumulativeProbabilityExtremes() throws Exception {
        setInverseCumulativeTestPoints(new double[] {0.0, 1.0});
        setInverseCumulativeTestValues(
                new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }
    
    public void testMedian() {
        CauchyDistribution distribution = (CauchyDistribution) getDistribution();
        double expected = Math.random();
        distribution.setMedian(expected);
        assertEquals(expected, distribution.getMedian(), 0.0);
    }
    
    public void testScale() {
        CauchyDistribution distribution = (CauchyDistribution) getDistribution();
        double expected = Math.random();
        distribution.setScale(expected);
        assertEquals(expected, distribution.getScale(), 0.0);
    }
    
    public void testSetScale() {
        CauchyDistribution distribution = (CauchyDistribution) getDistribution();
        try {
            distribution.setScale(0.0);
            fail("Can not have 0.0 scale.");
        } catch (IllegalArgumentException ex) {
            // success
        }
        
        try {
            distribution.setScale(-1.0);
            fail("Can not have negative scale.");
        } catch (IllegalArgumentException ex) {
            // success
        }
    }
}
