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

import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.14 $ $Date: 2004/05/23 21:34:19 $
 */
public class ExponentialDistributionTest extends TestCase {
    private ExponentialDistribution exp;

    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public ExponentialDistributionTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        exp =
            DistributionFactory.newInstance().createExponentialDistribution(
                5.0);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        exp = null;
        super.tearDown();
    }

    public void testInverseCumulativeProbability001() throws Exception {
        testValue(.005003, .001);
    }

    public void testInverseCumulativeProbability010() throws Exception {
        testValue(0.050252, .010);
    }

    public void testInverseCumulativeProbability025() throws Exception {
        testValue(0.126589, .025);
    }

    public void testInverseCumulativeProbability050() throws Exception {
        testValue(0.256566, .050);
    }

    public void testInverseCumulativeProbability100() throws Exception {
        testValue(0.526803, .100);
    }

    public void testInverseCumulativeProbability999() throws Exception {
        testValue(34.5388, .999);
    }

    public void testInverseCumulativeProbability990() throws Exception {
        testValue(23.0259, .990);
    }

    public void testInverseCumulativeProbability975() throws Exception {
        testValue(18.4444, .975);
    }

    public void testInverseCumulativeProbability950() throws Exception {
        testValue(14.9787, .950);
    }

    public void testInverseCumulativeProbability900() throws Exception {
        testValue(11.5129, .900);
    }

    public void testCumulativeProbability001() throws Exception {
        testProbability(0.005003, .001);
    }

    public void testCumulativeProbability010() throws Exception {
        testProbability(0.050252, .010);
    }

    public void testCumulativeProbability025() throws Exception {
        testProbability(0.126589, .025);
    }

    public void testCumulativeProbability050() throws Exception {
        testProbability(0.256566, .050);
    }

    public void testCumulativeProbability100() throws Exception {
        testProbability(0.526803, .100);
    }

    public void testCumulativeProbability999() throws Exception {
        testProbability(34.5388, .999);
    }

    public void testCumulativeProbability990() throws Exception {
        testProbability(23.0259, .990);
    }

    public void testCumulativeProbability975() throws Exception {
        testProbability(18.4444, .975);
    }

    public void testCumulativeProbability950() throws Exception {
        testProbability(14.9787, .950);
    }

    public void testCumulativeProbability900() throws Exception {
        testProbability(11.5129, .900);
    }

    public void testCumulativeProbabilityNegative() throws Exception {
        testProbability(-1.0, 0.0);
    }

    public void testCumulativeProbabilityZero() throws Exception {
        testProbability(0.0, 0.0);
    }

    public void testInverseCumulativeProbabilityNegative() throws Exception {
        testValue(Double.NaN, -1.0);
    }

    public void testInverseCumulativeProbabilityZero() throws Exception {
        testValue(0.0, 0.0);
    }

    public void testInverseCumulativeProbabilityOne() throws Exception {
        testValue(Double.POSITIVE_INFINITY, 1.0);
    }

    public void testInverseCumulativeProbabilityPositive() throws Exception {
        testValue(Double.NaN, 2.0);
    }

    public void testCumulativeProbability2() throws Exception {
        double actual = exp.cumulativeProbability(0.25, 0.75);
        assertEquals(0.0905214, actual, 10e-4);
    }

    private void testProbability(double x, double expected) throws Exception {
        double actual = exp.cumulativeProbability(x);
        TestUtils.assertEquals(expected, actual, 10e-4);
    }

    private void testValue(double expected, double p) throws Exception {
        double actual = exp.inverseCumulativeProbability(p);
        TestUtils.assertEquals(expected, actual, 10e-4);
    }
}
