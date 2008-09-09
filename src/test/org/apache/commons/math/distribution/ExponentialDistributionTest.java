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
 * Test cases for ExponentialDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 * 
 * @version $Revision$ $Date$
 */
public class ExponentialDistributionTest extends ContinuousDistributionAbstractTest {

    /**
     * Constructor for ExponentialDistributionTest.
     * @param name
     */
    public ExponentialDistributionTest(String name) {
        super(name);
    }

    //-------------- Implementations for abstract methods -----------------------
    
    /** Creates the default continuous distribution instance to use in tests. */
    public ContinuousDistribution makeDistribution() {
        return new ExponentialDistributionImpl(5.0);
    }   
    
    /** Creates the default cumulative probability distribution test input values */
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 1.8.1 (linux version)
        return new double[] {0.005002502d, 0.05025168d, 0.1265890d, 0.2564665d, 0.5268026d, 
                34.53878d, 23.02585d, 18.44440d, 14.97866d, 11.51293d};
    }
    
    /** Creates the default cumulative probability density test expected values */
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d}; 
    }
    
    //------------ Additional tests -------------------------------------------
 
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

    public void testCumulativeProbability2() throws Exception {
        double actual = getDistribution().cumulativeProbability(0.25, 0.75);
        assertEquals(0.0905214, actual, 10e-4);
    }

    public void testDensity() throws MathException {
        ExponentialDistribution d1 = new ExponentialDistributionImpl(1);
        assertEquals(0.0, d1.density(-1e-9));
        assertEquals(1.0, d1.density(0.0));
        assertEquals(0.0, d1.density(1000.0));
        assertEquals(Math.exp(-1), d1.density(1.0));
        assertEquals(Math.exp(-2), d1.density(2.0));

        ExponentialDistribution d2 = new ExponentialDistributionImpl(3);
        assertEquals(1/3.0, d2.density(0.0));
        // computed using  print(dexp(1, rate=1/3), digits=10) in R 2.5
        assertEquals(0.2388437702, d2.density(1.0), 1e-8);

        // computed using  print(dexp(2, rate=1/3), digits=10) in R 2.5
        assertEquals(0.1711390397, d2.density(2.0), 1e-8);
    }
    
    public void testMeanAccessors() {
        ExponentialDistribution distribution = (ExponentialDistribution) getDistribution();
        assertEquals(5d, distribution.getMean(), Double.MIN_VALUE);
        distribution.setMean(2d);
        assertEquals(2d, distribution.getMean(), Double.MIN_VALUE);
        try {
            distribution.setMean(0);
            fail("Expecting IllegalArgumentException for 0 mean");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
   
}
