/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.commons.math.stat.inference;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the ChiSquareTestImpl class.
 *
 * @version $Revision: 1.1 $ $Date: 2004/05/03 03:04:54 $
 */

public final class ChiSquareTestTest extends TestCase {

    private ChiSquareTestImpl testStatistic = new ChiSquareTestImpl();

    public ChiSquareTestTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ChiSquareTestTest.class);
        suite.setName("TestStatistic Tests");
        return suite;
    }

    public void testChiSquare() throws Exception {
 
        // Target values computed using R version 1.8.1 
        // Some assembly required ;-)  
        //      Use sum((obs - exp)^2/exp) for the chi-square statistic and
        //      1 - pchisq(sum((obs - exp)^2/exp), obs.length - 1) for the p-value
        
        long[] observed = {10, 9, 11};
        double[] expected = {10, 10, 10};
        assertEquals("chi-square statistic", 0.2,  testStatistic.chiSquare(expected, observed), 10E-12);
        assertEquals("chi-square p-value", 0.9048374, testStatistic.chiSquareTest(expected, observed), 1E-7);
        
        long[] observed1 = { 500, 623, 72, 70, 31 };
        double[] expected1 = { 485, 541, 82, 61, 37 };
        assertEquals( "chi-square test statistic", 16.41311, testStatistic.chiSquare(expected1, observed1), 1E-5);
        assertEquals("chi-square p-value", 0.002512096, testStatistic.chiSquareTest(expected1, observed1), 1E-9);
        assertTrue("chi-square test reject", testStatistic.chiSquareTest(expected1, observed1, 0.003));
        assertTrue("chi-square test accept", !testStatistic.chiSquareTest(expected1, observed1, 0.002));

        try {
            testStatistic.chiSquareTest(expected1, observed1, 95);
            fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }   
        
        long[] tooShortObs = { 0 };
        double[] tooShortEx = { 1 };
        try {
            testStatistic.chiSquare(tooShortEx, tooShortObs);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        long[] unMatchedObs = { 0, 1, 2, 3 };
        double[] unMatchedEx = { 1, 1, 2 };
        try {
            testStatistic.chiSquare(unMatchedEx, unMatchedObs);
            fail("arrays have different lengths, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        expected[0] = 0;
        try {
            testStatistic.chiSquareTest(expected, observed, .01);
            fail("bad expected count, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }

    public void testChiSquareIndependence() throws Exception {
        
        // Target values computed using R version 1.8.1 
        
        long[][] counts = { {40, 22, 43}, {91, 21, 28}, {60, 10, 22}};
        assertEquals( "chi-square test statistic", 22.709, testStatistic.chiSquare(counts), 1E-3);
        assertEquals("chi-square p-value", 0.0001448, testStatistic.chiSquareTest(counts), 1E-7);
        assertTrue("chi-square test reject", testStatistic.chiSquareTest(counts, 0.0002));
        assertTrue("chi-square test accept", !testStatistic.chiSquareTest(counts, 0.0001));    
        
        long[][] counts2 = {{10, 15}, {30, 40}, {60, 90} };
        assertEquals( "chi-square test statistic", 0.169, testStatistic.chiSquare(counts2), 1E-3);
        assertEquals("chi-square p-value", 0.919, testStatistic.chiSquareTest(counts2), 1E-3);
        assertTrue("chi-square test accept", !testStatistic.chiSquareTest(counts2, 0.1)); 
        
        // ragged input array
        long[][] counts3 = { {40, 22, 43}, {91, 21, 28}, {60, 10}};
        try {
            testStatistic.chiSquare(counts3);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        // insufficient data
        long[][] counts4 = {{40, 22, 43}};
        try {
            testStatistic.chiSquare(counts4);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }      
    }    
}
