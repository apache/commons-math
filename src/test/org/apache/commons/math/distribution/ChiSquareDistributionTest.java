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

import junit.framework.TestCase;

/**
 * @version $Revision: 1.14 $ $Date: 2004/05/23 21:34:19 $
 */
public class ChiSquareDistributionTest extends TestCase {
    private ChiSquaredDistribution chiSquare;
    
	/**
	 * Constructor for ChiSquareDistributionTest.
	 * @param name
	 */
	public ChiSquareDistributionTest(String name) {
		super(name);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
        chiSquare = DistributionFactory.newInstance().createChiSquareDistribution(5.0);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
        chiSquare = null;
		super.tearDown();
	}

    public void testLowerTailProbability() throws Exception {
        testProbability( .210, .001);
        testProbability( .554, .010);
        testProbability( .831, .025);
        testProbability(1.145, .050);
        testProbability(1.610, .100);
    }

    public void testUpperTailProbability() throws Exception {
        testProbability(20.515, .999);
        testProbability(15.086, .990);
        testProbability(12.833, .975);
        testProbability(11.070, .950);
        testProbability( 9.236, .900);
    }
    
    public void testLowerTailValues() throws Exception {
        testValue(.001,  .210);
        testValue(.010,  .554);
        testValue(.025,  .831);
        testValue(.050, 1.145);
        testValue(.100, 1.610);
    }
    
    public void testUpperTailValues() throws Exception {
        testValue(.999, 20.515);
        testValue(.990, 15.086);
        testValue(.975, 12.833);
        testValue(.950, 11.070);
        testValue(.900,  9.236);
    }
    
    private void testProbability(double x, double expected) throws Exception {
        double actual = chiSquare.cumulativeProbability(x);
        assertEquals("probability for " + x, expected, actual, 10e-4);
    }
    
    private void testValue(double p, double expected) throws Exception {
        double actual = chiSquare.inverseCumulativeProbability(p);
        assertEquals("value for " + p, expected, actual, 10e-4);
    }
}
