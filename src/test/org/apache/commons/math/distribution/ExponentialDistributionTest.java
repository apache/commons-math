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
import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.13 $ $Date: 2004/05/23 00:33:40 $
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

    public void testInverseCumulativeProbability001() {
        testValue(.005003, .001);
    }

    public void testInverseCumulativeProbability010() {
        testValue(0.050252, .010);
    }

    public void testInverseCumulativeProbability025() {
        testValue(0.126589, .025);
    }

    public void testInverseCumulativeProbability050() {
        testValue(0.256566, .050);
    }

    public void testInverseCumulativeProbability100() {
        testValue(0.526803, .100);
    }

    public void testInverseCumulativeProbability999() {
        testValue(34.5388, .999);
    }

    public void testInverseCumulativeProbability990() {
        testValue(23.0259, .990);
    }

    public void testInverseCumulativeProbability975() {
        testValue(18.4444, .975);
    }

    public void testInverseCumulativeProbability950() {
        testValue(14.9787, .950);
    }

    public void testInverseCumulativeProbability900() {
        testValue(11.5129, .900);
    }

    public void testCumulativeProbability001() {
        testProbability(0.005003, .001);
    }

    public void testCumulativeProbability010() {
        testProbability(0.050252, .010);
    }

    public void testCumulativeProbability025() {
        testProbability(0.126589, .025);
    }

    public void testCumulativeProbability050() {
        testProbability(0.256566, .050);
    }

    public void testCumulativeProbability100() {
        testProbability(0.526803, .100);
    }

    public void testCumulativeProbability999() {
        testProbability(34.5388, .999);
    }

    public void testCumulativeProbability990() {
        testProbability(23.0259, .990);
    }

    public void testCumulativeProbability975() {
        testProbability(18.4444, .975);
    }

    public void testCumulativeProbability950() {
        testProbability(14.9787, .950);
    }

    public void testCumulativeProbability900() {
        testProbability(11.5129, .900);
    }

    public void testCumulativeProbabilityNegative() {
        testProbability(-1.0, 0.0);
    }

    public void testCumulativeProbabilityZero() {
        testProbability(0.0, 0.0);
    }

    public void testInverseCumulativeProbabilityNegative() {
        testValue(Double.NaN, -1.0);
    }

    public void testInverseCumulativeProbabilityZero() {
        testValue(0.0, 0.0);
    }

    public void testInverseCumulativeProbabilityOne() {
        testValue(Double.POSITIVE_INFINITY, 1.0);
    }

    public void testInverseCumulativeProbabilityPositive() {
        testValue(Double.NaN, 2.0);
    }

    public void testCumulativeProbability2() {
        try {
            double actual = exp.cumulativeProbability(0.25, 0.75);
            assertEquals(0.0905214, actual, 10e-4);
        } catch (MathException e) {
            e.printStackTrace();
        }

    }

    private void testProbability(double x, double expected) {
        try {
            double actual = exp.cumulativeProbability(x);
            TestUtils.assertEquals(expected, actual, 10e-4);
        } catch (MathException e) {
            e.printStackTrace();
        }
    }

    private void testValue(double expected, double p) {
        try {
            double actual = exp.inverseCumulativeProbability(p);
            TestUtils.assertEquals(expected, actual, 10e-4);
        } catch (MathException e) {
            e.printStackTrace();
        }
    }
}
