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
 * Test cases for the {@link Univariate} class.
 *
 * @author <a href="mailto:phil@steitz.com">Phil Steitz</a>
 * @version $Revision: 1.1 $ $Date: 2003/05/15 05:39:01 $
 */

public final class UnivariateImplTest extends TestCase {
    private double one = 1;
    private float twoF = 2;
    private long twoL = 2;
    private int three = 3;
    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double std = Math.sqrt(var);
    private double n = 4;
    private double min = 1;
    private double max = 3;
    private double tolerance = 10E-15;
    
    public UnivariateImplTest(String name) {
        super(name);
    }
    
    public void setUp() {  
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(UnivariateImplTest.class);
        suite.setName("Freq Tests");
        return suite;
    }
    
    /** test stats */
    public void testStats() {
        UnivariateImpl u = new UnivariateImpl(); 
        assertEquals("total count",0,u.getN(),tolerance);
        u.addValue(one);
        u.addValue(twoF);
        u.addValue(twoL);
        u.addValue(three);
        assertEquals("N",n,u.getN(),tolerance);
        assertEquals("sum",sum,u.getSum(),tolerance);
        assertEquals("sumsq",sumSq,u.getSumsq(),tolerance);
        assertEquals("var",var,u.getVariance(),tolerance);
        assertEquals("std",std,u.getStandardDeviation(),tolerance);
        assertEquals("mean",mean,u.getMean(),tolerance);
        assertEquals("min",min,u.getMin(),tolerance);
        assertEquals("max",max,u.getMax(),tolerance);
        u.clear();
        assertEquals("total count",0,u.getN(),tolerance);    
    }     
    
    public void testN0andN1Conditions() throws Exception {
    	UnivariateImpl u = new UnivariateImpl();
    	    	
		assertTrue("Mean of n = 0 set should be NaN", Double.isNaN( u.getMean() ) );
		assertTrue("Standard Deviation of n = 0 set should be NaN", Double.isNaN( u.getStandardDeviation() ) );
		assertTrue("Variance of n = 0 set should be NaN", Double.isNaN(u.getVariance() ) );

		u.addValue(one);

		assertTrue( "Mean of n = 1 set should be value of single item n1", u.getMean() == one);
		assertTrue( "Mean of n = 1 set should be zero", u.getStandardDeviation() == 0);
		assertTrue( "Variance of n = 1 set should be zero", u.getVariance() == 0);	
    }
}

