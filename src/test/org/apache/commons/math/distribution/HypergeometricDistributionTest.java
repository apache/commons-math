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

package org.apache.commons.math.stat.distribution;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.2 $ $Date: 2003/10/13 08:08:38 $
 */
public class HypergeometricDistributionTest extends TestCase {
    private HypergeometricDistribution h;
    
    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public HypergeometricDistributionTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        h = DistributionFactory.newInstance().createHypergeometricDistribution(10, 5, 5);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        h = null;
        super.tearDown();
    }

    public void testInverseCummulativeProbability001() {
        testValue(-1, .001);
    }
    
    public void testInverseCumulativeProbability010() {
        testValue(0, .010);
    }
    
    public void testInverseCumulativeProbability025() {
        testValue(0, .025);
    }

    public void testInverseCumulativeProbability050() {
        testValue(0, .050);
    }
    
    public void testInverseCumulativeProbability100() {
        testValue(0, .100);
    }

    public void testInverseCummulativeProbability999() {
        testValue(4, .999);
    }
    
    public void testInverseCumulativeProbability990() {
        testValue(3, .990);
    }
    
    public void testInverseCumulativeProbability975() {
        testValue(3, .975);
    }

    public void testInverseCumulativeProbability950() {
        testValue(3, .950);
    }
    
    public void testInverseCumulativeProbability900() {
        testValue(3, .900);
    }

    public void testCummulativeProbability0() {
        testProbability(0, .00400);
    }

    public void testCummulativeProbability1() {
        testProbability(1, .10318);
    }
    
    public void testCumulativeProbability2() {
        testProbability(2, .50000);
    }
    
    public void testCumulativeProbability3() {
        testProbability(3, .89683);
    }

    public void testCumulativeProbability4() {
        testProbability(4, .99603);
    }
    
    public void testCumulativeProbability5() {
        testProbability(5, 1.00000);
    }

    private void testProbability(int x, double expected){
        double actual = h.cummulativeProbability(x);
        assertEquals(expected, actual, 10e-4);
    }
    
    private void testValue(int expected, double p){
        int actual = h.inverseCummulativeProbability(p);
        assertEquals(expected, actual);
        assertTrue(h.cummulativeProbability(actual) <= p);
        assertTrue(h.cummulativeProbability(actual + 1) >= p);
    }
}
