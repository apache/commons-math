/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.stat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test cases for the TestStatistic class.
 *
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:08:38 $
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

    public void testChiSquare() {
       double[] observed = {11,24,69,96};
       double[] expected = {8.2,25.2,65.8,100.8};
       assertEquals("chi-square statistic", 
           1.39743495,testStatistic.chiSquare(expected,observed),10E-5);
       
       double[] tooShortObs = {0};
       double[] tooShortEx = {1};
       try {
           testStatistic.chiSquare(tooShortObs,tooShortEx);
           fail("arguments too short, IllegalArgumentException expected");
       } catch (IllegalArgumentException ex) {
           ;
       }
       
       try {
           testStatistic.chiSquareTest(tooShortObs,tooShortEx);
           fail("arguments too short, IllegalArgumentException expected");
       } catch (IllegalArgumentException ex) {
           ;
       }
       
       double[] unMatchedObs = {0,1,2,3};
       double[] unMatchedEx = {1,1,2};
       try {
           testStatistic.chiSquare(unMatchedEx,unMatchedObs);
           fail("arrays have different lengths," + 
                " IllegalArgumentException expected");
       } catch (IllegalArgumentException ex) {
           ;
       }       
       expected[0] = 0;
       try {
           testStatistic.chiSquareTest(expected, observed, .01);
           fail("bad expected count, IllegalArgumentException expected");
       } catch (IllegalArgumentException ex) {
           ;
       }     
       /** from http://www.vsenvirginia.org/stat/classpractice/Voter_Preferences_CP.pdf */
       double[] observed1 = {504, 523, 72, 70, 31};
       double[] expected1 = {480, 540, 84, 60, 36};
       assertEquals("chi-square test statistic", 5.81,
            testStatistic.chiSquare(expected1,observed1),10E-2);
       assertEquals("chi-square p-value", 0.21, 
        testStatistic.chiSquareTest(expected1, observed1),10E-2); 
       assertTrue("chi-square test reject", 
        testStatistic.chiSquareTest(expected1, observed1, 0.3));
       assertTrue("chi-square test accept", 
        !testStatistic.chiSquareTest(expected1, observed1, 0.1));  
       try {
           testStatistic.chiSquareTest(expected1, observed1, 95);
           fail("alpha out of range, IllegalArgumentException expected");
       } catch (IllegalArgumentException ex) {
           ;
       }
    }
       
    public void testT(){
	double[] observed = {93.0, 103.0, 95.0, 101.0, 91.0, 105.0, 96.0,
            94.0, 101.0, 88.0, 98.0, 94.0, 101.0, 92.0, 95.0};
        double mu = 100.0;
        Univariate sampleStats = new UnivariateImpl();
        for (int i = 0; i < observed.length; i++) {
            sampleStats.addValue(observed[i]);
        }
        
        assertEquals("t statistic", -2.82, testStatistic.t(mu, observed),
            10E-3);
        assertEquals("t statistic", -2.82, testStatistic.t(mu, sampleStats),
            10E-3);
        
        double[] nullObserved = null;
        try {
            testStatistic.t(mu, nullObserved);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        
        UnivariateImpl nullStats = null;
        try {
            testStatistic.t(mu, nullStats);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        
        double[] emptyObs = {};
        try {
            testStatistic.t(mu, emptyObs);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        
        Univariate emptyStats = new UnivariateImpl();
        try {
            testStatistic.t(mu, emptyStats);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        
        double[] tooShortObs = {1.0};
        try {
            testStatistic.t(mu, tooShortObs);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            testStatistic.tTest(mu, tooShortObs);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        
        Univariate tooShortStats = new UnivariateImpl();
        tooShortStats.addValue(0d);
        tooShortStats.addValue(2d);
        try {
            testStatistic.t(mu, tooShortStats);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            testStatistic.tTest(mu, tooShortStats);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
            
        /** Moore and McCabe Example 8.3, p 516 */
        double[] oneSidedP = {2d, 0d, 6d, 6d, 3d, 3d, 2d, 3d, -6d, 6d, 6d, 
            6d, 3d, 0d, 1d, 1d, 0d, 2d, 3d, 3d};
        Univariate oneSidedPStats = new UnivariateImpl();
        for (int i = 0; i < oneSidedP.length; i++) {
            oneSidedPStats.addValue(oneSidedP[i]);
        }
        assertEquals("one sample t stat",3.86,
            testStatistic.t(0d,oneSidedP),0.01);
        assertEquals("one sample t stat",3.86,
            testStatistic.t(0d,oneSidedPStats),0.01);
        assertEquals("one sample p value",0.00052,
            testStatistic.tTest(0d,oneSidedP)/2d,10E-5);
        assertEquals("one sample p value",0.00052,
            testStatistic.tTest(0d,oneSidedPStats)/2d,10E-5);
        assertTrue("one sample t-test reject",
            testStatistic.tTest(0d,oneSidedP,0.01));
        assertTrue("one sample t-test reject",
            testStatistic.tTest(0d,oneSidedPStats,0.01));
        assertTrue("one sample t-test accept",
            !testStatistic.tTest(0d,oneSidedP,0.0001));
        assertTrue("one sample t-test accept",
            !testStatistic.tTest(0d,oneSidedPStats,0.0001));
        try {
           testStatistic.tTest(0d,oneSidedP, 95);
           fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           ;
        }
        try {
           testStatistic.tTest(0d,oneSidedPStats, 95);
           fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           ;
        }   
        
        /** Moore and McCabe Example 8.12, p 552 */
        double[] sample1 = {7d, -4d, 18d, 17d, -3d, -5d, 1d, 10d, 11d, -2d};
        double[] sample2 = {-1d, 12d, -1d, -3d, 3d, -5d, 5d, 2d, -11d, -1d, -3d};
        Univariate sampleStats1 = new UnivariateImpl();
        for (int i = 0; i < sample1.length; i++) {
            sampleStats1.addValue(sample1[i]);
        }
        Univariate sampleStats2 = new UnivariateImpl();
        for (int i = 0; i < sample2.length; i++) {
            sampleStats2.addValue(sample2[i]);
        }
        //FIXME: textbook example reported t stat uses pooled variance
        // should replace with R-verified example
        assertEquals("two sample t stat",1.634,
            testStatistic.t(sample1, sample2), 0.1); 
        assertEquals("two sample t stat",1.634,
            testStatistic.t(sampleStats1, sampleStats2), 0.1); 
        // This test is OK, since book reports non-pooled exact p-value
        assertEquals("two sample p value",0.059, 
            testStatistic.tTest(sample1, sample2)/2d, 10E-3);
        assertEquals("two sample p value",0.059, 
            testStatistic.tTest(sampleStats1, sampleStats2)/2d, 10E-3);
        assertTrue("two sample t-test reject",
            testStatistic.tTest(sample1, sample2, 0.2));
        assertTrue("two sample t-test reject",
            testStatistic.tTest(sampleStats1, sampleStats2, 0.2));
        assertTrue("two sample t-test accept",
            !testStatistic.tTest(sample1, sample2,0.1));  
        assertTrue("two sample t-test accept",
            !testStatistic.tTest(sampleStats1, sampleStats2,0.1));  
        try {
           testStatistic.tTest(sample1, sample2, 95);
           fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           ;
        }
        try {
           testStatistic.tTest(sampleStats1, sampleStats2, 95);
           fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           ;
        }
        try {
           testStatistic.tTest(sample1, tooShortObs, .01);
           fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           ;
        }
        try {
           testStatistic.tTest(sampleStats1, tooShortStats, .01);
           fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           ;
        }
        try {
           testStatistic.tTest(sample1, tooShortObs);
           fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           ;
        }
        try {
           testStatistic.tTest(sampleStats1, tooShortStats);
           fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           ;
        }
        try {
           testStatistic.t(sample1, tooShortObs);
           fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           ;
        }
        try {
           testStatistic.t(sampleStats1, tooShortStats);
           fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           ;
        }
    }
}

