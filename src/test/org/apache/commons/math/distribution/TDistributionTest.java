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

import org.apache.commons.math.MathException;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.13 $ $Date: 2004/02/28 21:58:33 $
 */
public class TDistributionTest extends TestCase {
    private TDistribution t;

    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public TDistributionTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        t = DistributionFactory.newInstance().createTDistribution(5.0);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        t = null;
        super.tearDown();
    }

    public void testInverseCumulativeProbability001() {
        testValue(-5.893, .001);
    }

    public void testInverseCumulativeProbability010() {
        testValue(-3.365, .010);
    }

    public void testInverseCumulativeProbability025() {
        testValue(-2.571, .025);
    }

    public void testInverseCumulativeProbability050() {
        testValue(-2.015, .050);
    }

    public void testInverseCumulativeProbability100() {
        testValue(-1.476, .100);
    }

    public void testInverseCumulativeProbability999() {
        testValue(5.893, .999);
    }

    public void testInverseCumulativeProbability990() {
        testValue(3.365, .990);
    }

    public void testInverseCumulativeProbability975() {
        testValue(2.571, .975);
    }

    public void testInverseCumulativeProbability950() {
        testValue(2.015, .950);
    }

    public void testInverseCumulativeProbability900() {
        testValue(1.476, .900);
    }

    public void testCumulativeProbability001() {
        testProbability(-5.893, .001);
    }

    public void testCumulativeProbability010() {
        testProbability(-3.365, .010);
    }

    public void testCumulativeProbability025() {
        testProbability(-2.571, .025);
    }

    public void testCumulativeProbability050() {
        testProbability(-2.015, .050);
    }

    public void testCumulativeProbability100() {
        testProbability(-1.476, .100);
    }

    public void testCumulativeProbability999() {
        testProbability(5.893, .999);
    }

    public void testCumulativeProbability990() {
        testProbability(3.365, .990);
    }

    public void testCumulativeProbability975() {
        testProbability(2.571, .975);
    }

    public void testCumulativeProbability950() {
        testProbability(2.015, .950);
    }

    public void testCumulativeProbability900() {
        testProbability(1.476, .900);
    }

    /**
     * @see <a href="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=27243">
     *      Bug report that prompted this unit test.</a>
     */
    public void testCumulativeProbabilityAgaintStackOverflow() {
    	try {
	    	TDistributionImpl td = new TDistributionImpl(5.);
	    	double est;
	    	est = td.cumulativeProbability(.1);
	    	est = td.cumulativeProbability(.01);
    	} catch(MathException ex) {
    		fail(ex.getMessage());
    	}
    }
    
    private void testProbability(double x, double expected) {
        try {
            double actual = t.cumulativeProbability(x);
            assertEquals(expected, actual, 10e-4);
        } catch (MathException e) {
        	fail(e.getMessage());
        }
    }
    private void testValue(double expected, double p) {
        try {
            double actual = t.inverseCumulativeProbability(p);
            assertEquals(expected, actual, 10e-4);
        } catch (MathException e) {
        	fail(e.getMessage());
        }
    }
}
