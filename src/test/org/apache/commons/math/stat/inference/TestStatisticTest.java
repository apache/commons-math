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
package org.apache.commons.math.stat.inference;

import org.apache.commons.math.MathException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.stat.univariate.SummaryStatistics;
/**
 * Test cases for the TestStatistic class.
 *
 * @version $Revision: 1.3 $ $Date: 2004/04/12 02:27:50 $
 */

public final class TestStatisticTest extends TestCase {

    private TestStatisticImpl testStatistic = new TestStatisticImpl();

    public TestStatisticTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(TestStatisticTest.class);
        suite.setName("TestStatistic Tests");
        return suite;
    }

    public void testChiSquare() throws MathException {
        double[] observed = { 11, 24, 69, 96 };
        double[] expected = { 8.2, 25.2, 65.8, 100.8 };
        assertEquals("chi-square statistic", 1.39743495, testStatistic.chiSquare(expected, observed), 10E-5);

        double[] tooShortObs = { 0 };
        double[] tooShortEx = { 1 };
        try {
            testStatistic.chiSquare(tooShortObs, tooShortEx);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.chiSquareTest(tooShortObs, tooShortEx);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }  

        double[] unMatchedObs = { 0, 1, 2, 3 };
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
        
        /** from http://www.vsenvirginia.org/stat/classpractice/Voter_Preferences_CP.pdf */
        double[] observed1 = { 504, 523, 72, 70, 31 };
        double[] expected1 = { 480, 540, 84, 60, 36 };
        assertEquals( "chi-square test statistic", 5.81, testStatistic.chiSquare(expected1, observed1), 10E-2);
        assertEquals("chi-square p-value", 0.21, testStatistic.chiSquareTest(expected1, observed1), 10E-2);
        assertTrue("chi-square test reject", testStatistic.chiSquareTest(expected1, observed1, 0.3));
        assertTrue("chi-square test accept", !testStatistic.chiSquareTest(expected1, observed1, 0.1));

        try {
            testStatistic.chiSquareTest(expected1, observed1, 95);
            fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testT() throws Exception {
        double[] observed =
            {93.0, 103.0, 95.0, 101.0, 91.0, 105.0, 96.0, 94.0, 101.0,  88.0, 98.0, 94.0, 101.0, 92.0, 95.0 };
        double mu = 100.0;
        SummaryStatistics sampleStats = null;
        sampleStats = SummaryStatistics.newInstance();
        for (int i = 0; i < observed.length; i++) {
            sampleStats.addValue(observed[i]);
        }

        assertEquals("t statistic", -2.82, testStatistic.t(mu, observed), 10E-3);
        assertEquals("t statistic", -2.82, testStatistic.t(mu, sampleStats), 10E-3);

        double[] nullObserved = null;
        try {
            testStatistic.t(mu, nullObserved);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        SummaryStatistics nullStats = null;   
        try {
            testStatistic.t(mu, nullStats);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        double[] emptyObs = {};
        try {
            testStatistic.t(mu, emptyObs);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        SummaryStatistics emptyStats =SummaryStatistics.newInstance();   
        try {
            testStatistic.t(mu, emptyStats);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        double[] tooShortObs = { 1.0 };
        try {
            testStatistic.t(mu, tooShortObs);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // exptected
        }
        try {
            testStatistic.tTest(mu, tooShortObs);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           // expected
        }  

        SummaryStatistics tooShortStats = SummaryStatistics.newInstance();     
        tooShortStats.addValue(0d);
        tooShortStats.addValue(2d);
        try {
            testStatistic.t(mu, tooShortStats);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // exptected
        }
        try {
            testStatistic.tTest(mu, tooShortStats);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // exptected
        }  

        /** Moore and McCabe Example 8.3, p 516 */
        double[] oneSidedP =
            {2d, 0d, 6d, 6d, 3d, 3d, 2d, 3d, -6d, 6d, 6d, 6d, 3d, 0d, 1d, 1d, 0d, 2d, 3d, 3d };
        SummaryStatistics oneSidedPStats = SummaryStatistics.newInstance();    
        for (int i = 0; i < oneSidedP.length; i++) {
            oneSidedPStats.addValue(oneSidedP[i]);
        }
        assertEquals("one sample t stat", 3.86, testStatistic.t(0d, oneSidedP), 0.01);
        assertEquals("one sample t stat", 3.86, testStatistic.t(0d, oneSidedPStats), 0.01);
        assertEquals("one sample p value", 0.00052, testStatistic.tTest(0d, oneSidedP) / 2d, 10E-5);
        assertEquals("one sample p value", 0.00052, testStatistic.tTest(0d, oneSidedPStats) / 2d, 10E-5);
        assertTrue("one sample t-test reject", testStatistic.tTest(0d, oneSidedP, 0.01));
        assertTrue("one sample t-test reject", testStatistic.tTest(0d, oneSidedPStats, 0.01));
        assertTrue("one sample t-test accept", !testStatistic.tTest(0d, oneSidedP, 0.0001));
        assertTrue("one sample t-test accept", !testStatistic.tTest(0d, oneSidedPStats, 0.0001));
         
        try {
            testStatistic.tTest(0d, oneSidedP, 95);
            fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // exptected
        }  
        
        try {
            testStatistic.tTest(0d, oneSidedPStats, 95);
            fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }  

        /** Moore and McCabe Example 8.12, p 552 */
        double[] sample1 = { 7d, -4d, 18d, 17d, -3d, -5d, 1d, 10d, 11d, -2d };
        double[] sample2 = { -1d, 12d, -1d, -3d, 3d, -5d, 5d, 2d, -11d, -1d, -3d };
        SummaryStatistics sampleStats1 = SummaryStatistics.newInstance();  
        for (int i = 0; i < sample1.length; i++) {
            sampleStats1.addValue(sample1[i]);
        }
        SummaryStatistics sampleStats2 = SummaryStatistics.newInstance();    
        for (int i = 0; i < sample2.length; i++) {
            sampleStats2.addValue(sample2[i]);
        }
         
        // Target comparison values computed using R version 1.8.1 (Linux version)
        assertEquals("two sample t stat", 1.6037, testStatistic.t(sample1, sample2), 10E-4);
        assertEquals("two sample t stat", 1.6037, testStatistic.t(sampleStats1, sampleStats2), 10E-4);
        assertEquals("two sample p value", 0.0644, testStatistic.tTest(sample1, sample2) / 2d, 10E-4);
        assertEquals("two sample p value", 0.0644, testStatistic.tTest(sampleStats1, sampleStats2) / 2d, 10E-4);
        
        assertTrue("two sample t-test reject", testStatistic.tTest(sample1, sample2, 0.2));
        assertTrue("two sample t-test reject", testStatistic.tTest(sampleStats1, sampleStats2, 0.2));
        assertTrue("two sample t-test accept", !testStatistic.tTest(sample1, sample2, 0.1));
        assertTrue("two sample t-test accept", !testStatistic.tTest(sampleStats1, sampleStats2, 0.1));
     
        try {
            testStatistic.tTest(sample1, sample2, 95);
            fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // exptected
        } 
        
        try {
            testStatistic.tTest(sampleStats1, sampleStats2, 95);
            fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected 
        }  
        
        try {
            testStatistic.tTest(sample1, tooShortObs, .01);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
        
        try {
            testStatistic.tTest(sampleStats1, tooShortStats, .01);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
        
        try {
            testStatistic.tTest(sample1, tooShortObs);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           // expected
        }  
        
        try {
            testStatistic.tTest(sampleStats1, tooShortStats);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
        
        try {
            testStatistic.t(sample1, tooShortObs);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        try {
            testStatistic.t(sampleStats1, tooShortStats);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           // expected
        }
    }
}
