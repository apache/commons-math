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
 * Test cases for HyperGeometriclDistribution.
 * Extends IntegerDistributionAbstractTest.  See class javadoc for
 * IntegerDistributionAbstractTest for details.
 * 
 * @version $Revision: 1.13 $ $Date: 2004/11/07 03:32:49 $
 */
public class HypergeometricDistributionTest extends IntegerDistributionAbstractTest {

    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public HypergeometricDistributionTest(String name) {
        super(name);
    }

//-------------- Implementations for abstract methods -----------------------
    
    /** Creates the default discrete distribution instance to use in tests. */
    public IntegerDistribution makeDistribution() {
        return DistributionFactory.newInstance().createHypergeometricDistribution(10,5, 5);
    }
    
    /** Creates the default probability density test input values */
    public int[] makeDensityTestPoints() {
        return new int[] {-1, 0, 1, 2, 3, 4, 5, 10};
    }
    
    /** Creates the default probability density test expected values */
    public double[] makeDensityTestValues() {
        return new double[] {0d, 0.003968d, 0.099206d, 0.396825d, 0.396825d, 
                0.099206d, 0.003968d, 0d};
    }
    
    /** Creates the default cumulative probability density test input values */
    public int[] makeCumulativeTestPoints() {
        return makeDensityTestPoints();
    }
    
    /** Creates the default cumulative probability density test expected values */
    public double[] makeCumulativeTestValues() {
        return new double[] {0d, .003968d, .103175d, .50000d, .896825d, .996032d,
                1.00000d, 1d};
    }
    
    /** Creates the default inverse cumulative probability test input values */
    public double[] makeInverseCumulativeTestPoints() {
        return new double[] {0d, 0.001d, 0.010d, 0.025d, 0.050d, 0.100d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d, 1d}; 
    }
    
    /** Creates the default inverse cumulative probability density test expected values */
    public int[] makeInverseCumulativeTestValues() {
        return new int[] {-1, -1, 0, 0, 0, 0, 4, 3, 3, 3, 3, 5};
    }
    
    //-------------------- Additional test cases ------------------------------
    
    /** Verify that if there are no failures, mass is concentrated on sampleSize */
    public void testDegenerateNoFailures() throws Exception {
        setDistribution(DistributionFactory.newInstance().createHypergeometricDistribution(5,5,3));
        setCumulativeTestPoints(new int[] {-1, 0, 1, 3, 10 });
        setCumulativeTestValues(new double[] {0d, 0d, 0d, 1d, 1d});
        setDensityTestPoints(new int[] {-1, 0, 1, 3, 10});
        setDensityTestValues(new double[] {0d, 0d, 0d, 1d, 0d});
        setInverseCumulativeTestPoints(new double[] {0.1d, 0.5d});
        setInverseCumulativeTestValues(new int[] {2, 2});
        verifyDensities();
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();     
    }
    
    /** Verify that if there are no successes, mass is concentrated on 0 */
    public void testDegenerateNoSuccesses() throws Exception {
        setDistribution(DistributionFactory.newInstance().createHypergeometricDistribution(5,0,3));
        setCumulativeTestPoints(new int[] {-1, 0, 1, 3, 10 });
        setCumulativeTestValues(new double[] {0d, 1d, 1d, 1d, 1d});
        setDensityTestPoints(new int[] {-1, 0, 1, 3, 10});
        setDensityTestValues(new double[] {0d, 1d, 0d, 0d, 0d});
        setInverseCumulativeTestPoints(new double[] {0.1d, 0.5d});
        setInverseCumulativeTestValues(new int[] {-1, -1});
        verifyDensities();
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();     
    }
    
    /** Verify that if sampleSize = populationSize, mass is concentrated on numberOfSuccesses */
    public void testDegenerateFullSample() throws Exception {
        setDistribution(DistributionFactory.newInstance().createHypergeometricDistribution(5,3,5));
        setCumulativeTestPoints(new int[] {-1, 0, 1, 3, 10 });
        setCumulativeTestValues(new double[] {0d, 0d, 0d, 1d, 1d});
        setDensityTestPoints(new int[] {-1, 0, 1, 3, 10});
        setDensityTestValues(new double[] {0d, 0d, 0d, 1d, 0d});
        setInverseCumulativeTestPoints(new double[] {0.1d, 0.5d});
        setInverseCumulativeTestValues(new int[] {2, 2});
        verifyDensities();
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();     
    }

}
