/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
 * Test cases for ChiSquareDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 * 
 * @version $Revision$ $Date$
 */
public class ChiSquareDistributionTest extends ContinuousDistributionAbstractTest {
    
    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public ChiSquareDistributionTest(String name) {
        super(name);
    }
    
    //-------------- Implementations for abstract methods -----------------------
    
    /** Creates the default continuous distribution instance to use in tests. */
    public ContinuousDistribution makeDistribution() {
        return DistributionFactory.newInstance().createChiSquareDistribution(5.0);
    }   
    
    /** Creates the default cumulative probability distribution test input values */
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 1.8.1 (linux version)
        return new double[] {0.210216d, 0.5542981d, 0.8312116d, 1.145476d, 1.610308d, 
                20.51501d, 15.08627d, 12.83250d, 11.07050d, 9.236357d};
    }
    
    /** Creates the default cumulative probability density test expected values */
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d}; 
    }
    
    /** Creates the default inverse cumulative probability test input values */
    public double[] makeInverseCumulativeTestPoints() {
        return new double[] {0, 0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d, 1};     
    }
    
    /** Creates the default inverse cumulative probability density test expected values */
    public double[] makeInverseCumulativeTestValues() {
        return new double[] {0, 0.210216d, 0.5542981d, 0.8312116d, 1.145476d, 1.610308d, 
                20.51501d, 15.08627d, 12.83250d, 11.07050d, 9.236357d, 
                Double.POSITIVE_INFINITY};
    }
    
 // --------------------- Override tolerance  --------------
    protected void setup() throws Exception {
        super.setUp();
        setTolerance(1E-6);
    }

 //---------------------------- Additional test cases -------------------------
    
    public void testSmallDf() throws Exception {
        setDistribution(DistributionFactory.newInstance().createChiSquareDistribution(0.1d));
        setTolerance(1E-4);
        // quantiles computed using R version 1.8.1 (linux version)
        setCumulativeTestPoints(new double[] {1.168926E-60, 1.168926E-40, 1.063132E-32, 
                1.144775E-26, 1.168926E-20, 5.472917, 2.175255, 1.13438, 
                0.5318646, 0.1526342});
        setInverseCumulativeTestValues(getCumulativeTestPoints());
        setInverseCumulativeTestPoints(getCumulativeTestValues());
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
    }
    
    public void testDfAccessors() {
        ChiSquaredDistribution distribution = (ChiSquaredDistribution) getDistribution();
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
