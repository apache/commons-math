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
 * @version $Revision: 1.13 $ $Date: 2004/05/23 21:34:19 $
 */
public class FDistributionTest extends TestCase {
    private FDistribution f;

    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public FDistributionTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        f = DistributionFactory.newInstance().createFDistribution(5.0, 6.0);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        f = null;
        super.tearDown();
    }

    public void testLowerTailProbability() throws Exception {
        testProbability(1.0 / 10.67, .010);
        testProbability(1.0 / 6.98, .025);
        testProbability(1.0 / 4.95, .050);
        testProbability(1.0 / 3.40, .100);
    }

    public void testUpperTailProbability() throws Exception {
        testProbability(8.75, .990);
        testProbability(5.99, .975);
        testProbability(4.39, .950);
        testProbability(3.11, .900);
    }

    public void testLowerTailValues() throws Exception {
        testValue(1.0 / 10.67, .010);
        testValue(1.0 / 6.98, .025);
        testValue(1.0 / 4.95, .050);
        testValue(1.0 / 3.40, .100);
    }

    public void testUpperTailValues() throws Exception {
        testValue(8.75, .990);
        testValue(5.99, .975);
        testValue(4.39, .950);
        testValue(3.11, .900);
    }

    private void testProbability(double x, double expected) throws Exception {
		double actual = f.cumulativeProbability(x);
        assertEquals("probability for " + x, expected, actual, 1e-3);
    }

    private void testValue(double expected, double p) throws Exception {
        double actual = f.inverseCumulativeProbability(p);
        assertEquals("value for " + p, expected, actual, 1e-2);
    }
}
