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
package org.apache.commons.math.stat.distribution;

import junit.framework.TestCase;

/**
 * @author Brent Worden
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
        b = DistributionFactory.newInstance().createBinomailDistribution(10, 0.70);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        b = null;
        super.tearDown();
    }

    public void testInverseCummulativeProbability001() {
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

    public void testInverseCummulativeProbability999() {
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

    public void testCummulativeProbability1() {
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

    public void testCummulativeProbability8() {
        testProbability(8, .85069);
    }
    
    private void testProbability(int x, double expected){
        double actual = b.cummulativeProbability(x);
        assertEquals(expected, actual, 10e-4);
    }
    
    private void testValue(int expected, double p){
        int actual = b.inverseCummulativeProbability(p);
        assertEquals(expected, actual);
        assertTrue(b.cummulativeProbability(actual) <= p);
        assertTrue(b.cummulativeProbability(actual + 1) >= p);
    }
}
