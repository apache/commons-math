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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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
package org.apache.commons.math;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test cases for the TestStatistic class.
 *
 * @author Phil Steitz
 * @version $Revision: 1.2 $ $Date: 2003/05/16 03:55:34 $
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
           double x = testStatistic.chiSquare(tooShortObs,tooShortEx);
           fail("arguments too short, IllegalArgumentException expected");
       } catch (IllegalArgumentException ex) {
           ;
       }
       
       double[] unMatchedObs = {0,1,2,3};
       double[] unMatchedEx = {1,1,2};
       try {
           double x = testStatistic.chiSquare(unMatchedEx,unMatchedObs);
           fail("arrays have different lengths, IllegalArgumentException expected");
       } catch (IllegalArgumentException ex) {
           ;
       }
       
       expected[0] = 0;
       assertEquals("chi-square statistic", Double.POSITIVE_INFINITY,
            testStatistic.chiSquare(expected,observed),Double.MIN_VALUE);
    }
       
}

