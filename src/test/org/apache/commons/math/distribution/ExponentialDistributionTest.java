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

    public void testLowerTailProbability(){
        testProbability(0.005003, .001);
        testProbability(0.050252, .010);
        testProbability(0.126589, .025);
        testProbability(0.256566, .050);
        testProbability(0.526803, .100);
    }

    public void testUpperTailProbability(){
        testProbability(34.5388, .999);
        testProbability(23.0259, .990);
        testProbability(18.4444, .975);
        testProbability(14.9787, .950);
        testProbability(11.5129, .900);
    }
    
    public void testLowerTailValues(){
        testValue(0.005003, .001);
        testValue(0.050252, .010);
        testValue(0.126589, .025);
        testValue(0.256566, .050);
        testValue(0.526803, .100);
    }
    
    public void testUpperTailValues(){
        testValue(34.5388, .999);
        testValue(23.0259, .990);
        testValue(18.4444, .975);
        testValue(14.9787, .950);
        testValue(11.5129, .900);
    }
    
    private void testProbability(double x, double expected){
        double actual = exp.cummulativeProbability(x);
        assertEquals("probability for " + x, expected, actual, 10e-4);
    }
    
    private void testValue(double expected, double p){
        double actual = exp.inverseCummulativeProbability(p);
        assertEquals("value for " + p, expected, actual, 10e-4);
    }
}
