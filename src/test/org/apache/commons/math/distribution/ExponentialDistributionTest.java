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

import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:08:38 $
 */
public class ExponentialDistributionTest extends TestCase {
    private ExponentialDistribution exp;
    
    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public ExponentialDistributionTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        exp = DistributionFactory.newInstance().createExponentialDistribution(5.0);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        exp = null;
        super.tearDown();
    }

    public void testInverseCummulativeProbability001() {
        testValue(.005003, .001);
    }
    
    public void testInverseCummulativeProbability010() {
        testValue(0.050252, .010);
    }
    
    public void testInverseCummulativeProbability025() {
        testValue(0.126589, .025);
    }

    public void testInverseCummulativeProbability050() {
        testValue(0.256566, .050);
    }
    
    public void testInverseCummulativeProbability100() {
        testValue(0.526803, .100);
    }

    public void testInverseCummulativeProbability999() {
        testValue(34.5388, .999);
    }
    
    public void testInverseCummulativeProbability990() {
        testValue(23.0259, .990);
    }
    
    public void testInverseCummulativeProbability975() {
        testValue(18.4444, .975);
    }

    public void testInverseCummulativeProbability950() {
        testValue(14.9787, .950);
    }
    
    public void testInverseCummulativeProbability900() {
        testValue(11.5129, .900);
    }

    public void testCummulativeProbability001() {
        testProbability(0.005003, .001);
    }
    
    public void testCummulativeProbability010() {
        testProbability(0.050252, .010);
    }
    
    public void testCummulativeProbability025() {
        testProbability(0.126589, .025);
    }

    public void testCummulativeProbability050() {
        testProbability(0.256566, .050);
    }
    
    public void testCummulativeProbability100() {
        testProbability(0.526803, .100);
    }

    public void testCummulativeProbability999() {
        testProbability(34.5388, .999);
    }
    
    public void testCummulativeProbability990() {
        testProbability(23.0259, .990);
    }
    
    public void testCummulativeProbability975() {
        testProbability(18.4444, .975);
    }

    public void testCummulativeProbability950() {
        testProbability(14.9787, .950);
    }
    
    public void testCummulativeProbability900() {
        testProbability(11.5129, .900);
    }

    public void testCummulativeProbabilityNegative() {
        testProbability(-1.0, 0.0);
    }

    public void testCummulativeProbabilityZero() {
        testProbability(0.0, 0.0);
    }

    public void testInverseCummulativeProbabilityNegative() {
        testValue(Double.NaN, -1.0);
    }

    public void testInverseCummulativeProbabilityZero() {
        testValue(0.0, 0.0);
    }

    public void testInverseCummulativeProbabilityOne() {
        testValue(Double.POSITIVE_INFINITY, 1.0);
    }

    public void testInverseCummulativeProbabilityPositive() {
        testValue(Double.NaN, 2.0);
    }
    
    public void testCummulativeProbability2() {
        double actual = exp.cummulativeProbability(0.25, 0.75);
        assertEquals(0.0905214, actual, 10e-4);
    }
    
    private void testProbability(double x, double expected){
        double actual = exp.cummulativeProbability(x);
        TestUtils.assertEquals(expected, actual, 10e-4);
    }
    
    private void testValue(double expected, double p){
        double actual = exp.inverseCummulativeProbability(p);
        TestUtils.assertEquals(expected, actual, 10e-4);
    }
}
