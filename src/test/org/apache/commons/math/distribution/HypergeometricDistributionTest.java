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
 * @version $Revision: 1.10 $ $Date: 2004/02/21 21:35:17 $
 */
public class HypergeometricDistributionTest extends TestCase {
    private HypergeometricDistribution h;

    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public HypergeometricDistributionTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        h =
            DistributionFactory.newInstance().createHypergeometricDistribution(
                10,
                5,
                5);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        h = null;
        super.tearDown();
    }

    public void testInverseCumulativeProbability001() {
        testValue(-1, .001);
    }

    public void testInverseCumulativeProbability010() {
        testValue(0, .010);
    }

    public void testInverseCumulativeProbability025() {
        testValue(0, .025);
    }

    public void testInverseCumulativeProbability050() {
        testValue(0, .050);
    }

    public void testInverseCumulativeProbability100() {
        testValue(0, .100);
    }

    public void testInverseCumulativeProbability999() {
        testValue(4, .999);
    }

    public void testInverseCumulativeProbability990() {
        testValue(3, .990);
    }

    public void testInverseCumulativeProbability975() {
        testValue(3, .975);
    }

    public void testInverseCumulativeProbability950() {
        testValue(3, .950);
    }

    public void testInverseCumulativeProbability900() {
        testValue(3, .900);
    }

    public void testCumulativeProbability0() {
        testProbability(0, .00400);
    }

    public void testCumulativeProbability1() {
        testProbability(1, .10318);
    }

    public void testCumulativeProbability2() {
        testProbability(2, .50000);
    }

    public void testCumulativeProbability3() {
        testProbability(3, .89683);
    }

    public void testCumulativeProbability4() {
        testProbability(4, .99603);
    }

    public void testCumulativeProbability5() {
        testProbability(5, 1.00000);
    }

    private void testProbability(int x, double expected) {
        try {
            double actual = h.cumulativeProbability(x);
            assertEquals(expected, actual, 10e-4);
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void testValue(int expected, double p) {
        try {
            int actual = h.inverseCumulativeProbability(p);
            assertEquals(expected, actual);
            assertTrue(h.cumulativeProbability(actual) <= p);
            assertTrue(h.cumulativeProbability(actual + 1) >= p);
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
