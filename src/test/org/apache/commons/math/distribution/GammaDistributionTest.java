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
 * @version $Revision: 1.16 $ $Date: 2004/05/23 21:34:19 $
 */
public class GammaDistributionTest extends TestCase {
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
}
