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
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:08:38 $
 */
public class ChiSquareDistributionTest extends TestCase {
    private ChiSquaredDistribution chiSquare;
    
	/**
	 * Constructor for ChiSquareDistributionTest.
	 * @param name
	 */
	public ChiSquareDistributionTest(String name) {
		super(name);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
        chiSquare = DistributionFactory.newInstance().createChiSquareDistribution(5.0);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
        chiSquare = null;
		super.tearDown();
	}

    public void testLowerTailProbability(){
        testProbability( .210, .001);
        testProbability( .554, .010);
        testProbability( .831, .025);
        testProbability(1.145, .050);
        testProbability(1.610, .100);
    }

    public void testUpperTailProbability(){
        testProbability(20.515, .999);
        testProbability(15.086, .990);
        testProbability(12.833, .975);
        testProbability(11.070, .950);
        testProbability( 9.236, .900);
    }
    
    public void testLowerTailValues(){
        testValue(.001,  .210);
        testValue(.010,  .554);
        testValue(.025,  .831);
        testValue(.050, 1.145);
        testValue(.100, 1.610);
    }
    
    public void testUpperTailValues(){
        testValue(.999, 20.515);
        testValue(.990, 15.086);
        testValue(.975, 12.833);
        testValue(.950, 11.070);
        testValue(.900,  9.236);
    }
    
    private void testProbability(double x, double expected){
        double actual = chiSquare.cummulativeProbability(x);
        assertEquals("probability for " + x, expected, actual, 10e-4);
    }
    
    private void testValue(double p, double expected){
        double actual = chiSquare.inverseCummulativeProbability(p);
        assertEquals("value for " + p, expected, actual, 10e-4);
    }
}
