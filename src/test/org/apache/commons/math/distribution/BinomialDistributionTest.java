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
 * @version $Revision: 1.12 $ $Date: 2004/02/21 21:35:17 $
 */
public class BinomialDistributionTest extends TestCase {
    private BinomialDistribution b;

    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public BinomialDistributionTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        b =
            DistributionFactory.newInstance().createBinomialDistribution(
                10,
                0.70);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        b = null;
        super.tearDown();
    }

    public void testInverseCumulativeProbability001() {
        testValue(1, .001);
    }

    public void testInverseCumulativeProbability010() {
        testValue(2, .010);
    }

    public void testInverseCumulativeProbability025() {
        testValue(3, .025);
    }

    public void testInverseCumulativeProbability050() {
        testValue(4, .050);
    }

    public void testInverseCumulativeProbability100() {
        testValue(4, .100);
    }

    public void testInverseCumulativeProbability999() {
        testValue(9, .999);
    }

    public void testInverseCumulativeProbability990() {
        testValue(9, .990);
    }

    public void testInverseCumulativeProbability975() {
        testValue(9, .975);
    }

    public void testInverseCumulativeProbability950() {
        testValue(8, .950);
    }

    public void testInverseCumulativeProbability900() {
        testValue(8, .900);
    }

    public void testCumulativeProbability1() {
        testProbability(1, .00014);
    }

    public void testCumulativeProbability2() {
        testProbability(2, .00159);
    }

    public void testCumulativeProbability3() {
        testProbability(3, .01059);
    }

    public void testCumulativeProbability4() {
        testProbability(4, .04735);
    }

    public void testCumulativeProbability9() {
        testProbability(9, .97175);
    }

    public void testcumulativeProbability8() {
        testProbability(8, .85069);
    }

    private void testProbability(int x, double expected) {
        try {
            double actual = b.cumulativeProbability(x);
            assertEquals(expected, actual, 10e-4);
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void testValue(int expected, double p) {
        try {
            int actual = b.inverseCumulativeProbability(p);
            assertEquals(expected, actual);
            assertTrue(b.cumulativeProbability(actual) <= p);
            assertTrue(b.cumulativeProbability(actual + 1) >= p);
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
