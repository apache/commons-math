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
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:08:38 $
 */
public class TDistributionTest extends TestCase {
    private TDistribution t;
    
    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public TDistributionTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        t = DistributionFactory.newInstance().createTDistribution(5.0);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        t = null;
        super.tearDown();
    }

    public void testInverseCummulativeProbability001() {
        testValue(-5.893, .001);
    }
    
    public void testInverseCumulativeProbability010() {
        testValue(-3.365, .010);
    }
    
    public void testInverseCumulativeProbability025() {
        testValue(-2.571, .025);
    }

    public void testInverseCumulativeProbability050() {
        testValue(-2.015, .050);
    }
    
    public void testInverseCumulativeProbability100() {
        testValue(-1.476, .100);
    }

    public void testInverseCummulativeProbability999() {
        testValue(5.893, .999);
    }
    
    public void testInverseCumulativeProbability990() {
        testValue(3.365, .990);
    }
    
    public void testInverseCumulativeProbability975() {
        testValue(2.571, .975);
    }

    public void testInverseCumulativeProbability950() {
        testValue(2.015, .950);
    }
    
    public void testInverseCumulativeProbability900() {
        testValue(1.476, .900);
    }

    public void testCummulativeProbability001() {
        testProbability(-5.893, .001);
    }
    
    public void testCumulativeProbability010() {
        testProbability(-3.365, .010);
    }
    
    public void testCumulativeProbability025() {
        testProbability(-2.571, .025);
    }

    public void testCumulativeProbability050() {
        testProbability(-2.015, .050);
    }
    
    public void testCumulativeProbability100() {
        testProbability(-1.476, .100);
    }

    public void testCummulativeProbability999() {
        testProbability(5.893, .999);
    }
    
    public void testCumulativeProbability990() {
        testProbability(3.365, .990);
    }
    
    public void testCumulativeProbability975() {
        testProbability(2.571, .975);
    }

    public void testCumulativeProbability950() {
        testProbability(2.015, .950);
    }
    
    public void testCumulativeProbability900() {
        testProbability(1.476, .900);
    }
    
    private void testProbability(double x, double expected){
        double actual = t.cummulativeProbability(x);
        assertEquals(expected, actual, 10e-4);
    }
    
    private void testValue(double expected, double p){
        double actual = t.inverseCummulativeProbability(p);
        assertEquals(expected, actual, 10e-4);
    }
}
