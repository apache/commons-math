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
 * Test cases for the {@link RealMatrixImpl} class.
 *
 * @author Phil Steitz
 * @version $Revision: 1.1 $ $Date: 2003/05/12 19:02:53 $
 */

public final class RealMatrixImplTest extends TestCase {
    
    private double[][] testData = { {1d,2d,3d}, {2d,5d,3d}, {1d,0d,8d} };
    private double[][] testDataInv = 
        { {-40d,16d,9d}, {13d,-5d,-3d}, {5d,-2d,-1d} };
    private double[][] testData2 ={ {1d,2d,3d}, {2d,5d,3d}};
    private double[][] testDataPlusInv = 
        { {-39d,18d,12d}, {15d,0d,0d}, {6d,-2d,7d} };
    private double[][] id = { {1d,0d,0d}, {0d,1d,0d}, {0d,0d,1d} };
    private double[] testVector = {1,2,3};
    private double entryTolerance = Math.pow(2,-64);
    private double normTolerance = Math.pow(2,-64);
    
    public RealMatrixImplTest(String name) {
        super(name);
    }
    
    public void setUp() {
        
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(RealMatrixImplTest.class);
        suite.setName("RealMatrixImpl Tests");
        return suite;
    }
    
    /** test dimensions */
    public void testDimensions() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrixImpl m2 = new RealMatrixImpl(testData2);
        assertEquals("testData row dimension",3,m.getRowDimension());
        assertEquals("testData column dimension",3,m.getColumnDimension());
        assertTrue("testData is square",m.isSquare());
        assertEquals("testData2 row dimension",m2.getRowDimension(),2);
        assertEquals("testData2 column dimension",m2.getColumnDimension(),3);
        assertTrue("testData2 is not square",!m2.isSquare());
    }     
    
    /** test add */
    public void testAdd() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrixImpl mInv = new RealMatrixImpl(testDataInv);
        RealMatrixImpl mPlusMInv = (RealMatrixImpl)m.add(mInv);
        double[][] sumEntries = mPlusMInv.getData();
        for (int row = 0; row < m.getRowDimension(); row++) {
            for (int col = 0; col < m.getColumnDimension(); col++) {
                assertEquals("sum entry entry",
                    testDataPlusInv[row][col],sumEntries[row][col],
                        entryTolerance);
            }
        }    
    }
    
    /** test add failure */
    public void testAddFail() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrixImpl m2 = new RealMatrixImpl(testData2);
        try {
            RealMatrixImpl mPlusMInv = (RealMatrixImpl)m.add(m2);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    /** test norm */
    public void testNorm() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrixImpl m2 = new RealMatrixImpl(testData2);
        assertEquals("testData norm",14d,m.getNorm(),entryTolerance);
        assertEquals("testData2 norm",7d,m2.getNorm(),entryTolerance);
    }
    
     /** test m-n = m + -n */
    public void testPlusMinus() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrixImpl m2 = new RealMatrixImpl(testDataInv);
        assertClose("m-n = m + -n",m.subtract(m2),
            m2.scalarMultiply(-1d).add(m),entryTolerance);
    }
    
    /** test multiply */
     public void testMultiply() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrixImpl mInv = new RealMatrixImpl(testDataInv);
        RealMatrixImpl identity = new RealMatrixImpl(id);
        RealMatrixImpl m2 = new RealMatrixImpl(testData2);
        assertClose("inverse multiply",m.multiply(mInv),
            identity,entryTolerance);
        assertClose("inverse multiply",mInv.multiply(m),
            identity,entryTolerance);
        assertClose("identity multiply",m.multiply(identity),
            m,entryTolerance);
        assertClose("identity multiply",identity.multiply(mInv),
            mInv,entryTolerance);
        assertClose("identity multiply",m2.multiply(identity),
            m2,entryTolerance);       
    }
    
    private void assertClose(String msg, RealMatrix m, RealMatrix n,
        double tolerance) {
        assertTrue(msg,m.subtract(n).getNorm() < tolerance);
    }
        
}

