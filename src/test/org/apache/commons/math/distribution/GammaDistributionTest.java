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
 * Test cases for GammaDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 * 
 * @version $Revision$ $Date$
 */
public class GammaDistributionTest extends ContinuousDistributionAbstractTest {
    
    /**
     * Constructor for GammaDistributionTest.
     * @param name
     */
    public GammaDistributionTest(String name) {
        super(name);
    }
    
    //-------------- Implementations for abstract methods -----------------------
    
    /** Creates the default continuous distribution instance to use in tests. */
    public ContinuousDistribution makeDistribution() {
        return DistributionFactory.newInstance().createGammaDistribution(4d, 2d);
    }   
    
    /** Creates the default cumulative probability distribution test input values */
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 1.8.1 (linux version)
        return new double[] {0.8571048, 1.646497, 2.179731, 2.732637,
            3.489539, 26.12448, 20.09024, 17.53455,
            15.50731, 13.36157};
    }
    
    /** Creates the default cumulative probability density test expected values */
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d}; 
    }
    
    // --------------------- Override tolerance  --------------
    protected void setup() throws Exception {
        super.setUp();
        setTolerance(1E-6);
    }

    //---------------------------- Additional test cases -------------------------
    public void testParameterAccessors() {
        GammaDistribution distribution = (GammaDistribution) getDistribution();
        assertEquals(4d, distribution.getAlpha(), 0);
        distribution.setAlpha(3d);
        assertEquals(3d, distribution.getAlpha(), 0);
        assertEquals(2d, distribution.getBeta(), 0);
        distribution.setBeta(4d);
        assertEquals(4d, distribution.getBeta(), 0);
        try {
            distribution.setAlpha(0d);
            fail("Expecting IllegalArgumentException for alpha = 0");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            distribution.setBeta(0d);
            fail("Expecting IllegalArgumentException for beta = 0");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    } 
    
    public void testProbabilities() throws Exception {
        testProbability(-1.000, 4.0, 2.0, .0000);
        testProbability(15.501, 4.0, 2.0, .9499);
        testProbability(0.504, 4.0, 1.0, .0018);
        testProbability(10.011, 1.0, 2.0, .9933);
        testProbability(5.000, 2.0, 2.0, .7127);
    }

    public void testValues() throws Exception {
        testValue(15.501, 4.0, 2.0, .9499);
        testValue(0.504, 4.0, 1.0, .0018);
        testValue(10.011, 1.0, 2.0, .9933);
        testValue(5.000, 2.0, 2.0, .7127);
    }

    private void testProbability(double x, double a, double b, double expected) throws Exception {
        DistributionFactory factory = DistributionFactory.newInstance();
        GammaDistribution distribution = factory.createGammaDistribution( a, b );
        double actual = distribution.cumulativeProbability(x);
        assertEquals("probability for " + x, expected, actual, 10e-4);
    }

    private void testValue(double expected, double a, double b, double p) throws Exception {
        DistributionFactory factory = DistributionFactory.newInstance();
        GammaDistribution distribution = factory.createGammaDistribution( a, b );
        double actual = distribution.inverseCumulativeProbability(p);
        assertEquals("critical value for " + p, expected, actual, 10e-4);
    }
    
    public void testInverseCumulativeProbabilityExtremes() throws Exception {
        setInverseCumulativeTestPoints(new double[] {0, 1});
        setInverseCumulativeTestValues(new double[] {0, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }
}
