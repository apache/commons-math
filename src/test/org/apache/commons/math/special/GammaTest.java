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
package org.apache.commons.math.special;

import org.apache.commons.math.MathException;
import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class GammaTest extends TestCase {
    /**
     * Constructor for BetaTest.
     * @param name
     */
    public GammaTest(String name) {
        super(name);
    }

    private void testRegularizedGamma(double expected, double a, double x) {
        try {
            double actualP = Gamma.regularizedGammaP(a, x);
            double actualQ = Gamma.regularizedGammaQ(a, x);
            TestUtils.assertEquals(expected, actualP, 10e-5);
            TestUtils.assertEquals(actualP, 1.0 - actualQ, 10e-5);
        } catch(MathException ex){
            fail(ex.getMessage());
        }
    }

    private void testLogGamma(double expected, double x) {
        double actual = Gamma.logGamma(x);
        TestUtils.assertEquals(expected, actual, 10e-5);
    }

    public void testRegularizedGammaNanPositive() {
        testRegularizedGamma(Double.NaN, Double.NaN, 1.0);
    }

    public void testRegularizedGammaPositiveNan() {
        testRegularizedGamma(Double.NaN, 1.0, Double.NaN);
    }
    
    public void testRegularizedGammaNegativePositive() {
        testRegularizedGamma(Double.NaN, -1.5, 1.0);
    }
    
    public void testRegularizedGammaPositiveNegative() {
        testRegularizedGamma(Double.NaN, 1.0, -1.0);
    }
    
    public void testRegularizedGammaZeroPositive() {
        testRegularizedGamma(Double.NaN, 0.0, 1.0);
    }
    
    public void testRegularizedGammaPositiveZero() {
        testRegularizedGamma(0.0, 1.0, 0.0);
    }
    
    public void testRegularizedGammaPositivePositive() {
        testRegularizedGamma(0.632121, 1.0, 1.0);
    }
    
    public void testLogGammaNan() {
        testLogGamma(Double.NaN, Double.NaN);
    }
    
    public void testLogGammaNegative() {
        testLogGamma(Double.NaN, -1.0);
    }
    
    public void testLogGammaZero() {
        testLogGamma(Double.NaN, 0.0);
    }
    
    public void testLogGammaPositive() {
        testLogGamma(0.693147, 3.0);
    }
}
