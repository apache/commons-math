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
 * @version $Revision: 1.3 $ $Date: 2003/10/13 08:08:38 $
 */
public class FDistributionTest extends TestCase {
    private FDistribution f;
    
    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public FDistributionTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        f = DistributionFactory.newInstance().createFDistribution(5.0, 6.0);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        f = null;
        super.tearDown();
    }

    public void testLowerTailProbability(){
        testProbability(1.0 / 10.67, .010);
        testProbability(1.0 /  6.98, .025);
        testProbability(1.0 /  4.95, .050);
        testProbability(1.0 /  3.40, .100);
    }

    public void testUpperTailProbability(){
        testProbability(8.75, .990);
        testProbability(5.99, .975);
        testProbability(4.39, .950);
        testProbability(3.11, .900);
    }
    
    public void testLowerTailValues(){
        testValue(1.0 / 10.67, .010);
        testValue(1.0 /  6.98, .025);
        testValue(1.0 /  4.95, .050);
        testValue(1.0 /  3.40, .100);
    }
    
    public void testUpperTailValues(){
        testValue(8.75, .990);
        testValue(5.99, .975);
        testValue(4.39, .950);
        testValue(3.11, .900);
    }
    
    private void testProbability(double x, double expected){
        double actual = f.cummulativeProbability(x);
        assertEquals("probability for " + x, expected, actual, 1e-3);
    }
    
    private void testValue(double expected, double p){
        double actual = f.inverseCummulativeProbability(p);
        assertEquals("value for " + p, expected, actual, 1e-2);
    }
}
