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
package org.apache.commons.math.linear;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the {@link RealMatrixImpl} class.
 *
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:07:26 $
 */

public final class RealMatrixImplTest extends TestCase {
    
    private double[][] testData = { {1d,2d,3d}, {2d,5d,3d}, {1d,0d,8d} };
    private double[][] testDataPlus2 = { {3d,4d,5d}, {4d,7d,5d}, {3d,2d,10d} };
    private double[][] testDataMinus = { {-1d,-2d,-3d}, {-2d,-5d,-3d}, 
       {-1d,0d,-8d} };
    private double[] testDataRow1 = {1d,2d,3d};
    private double[] testDataCol3 = {3d,3d,8d};
    private double[][] testDataInv = 
        { {-40d,16d,9d}, {13d,-5d,-3d}, {5d,-2d,-1d} };
    private double[][] preMultTest = {{8,12,33}};
    private double[][] testData2 ={ {1d,2d,3d}, {2d,5d,3d}};
    private double[][] testData2T = { {1d,2d}, {2d,5d}, {3d,3d}};
    private double[][] testDataPlusInv = 
        { {-39d,18d,12d}, {15d,0d,0d}, {6d,-2d,7d} };
    private double[][] id = { {1d,0d,0d}, {0d,1d,0d}, {0d,0d,1d} };
    private double[][] luData = { {2d,3d,3d}, {0d,5d,7d}, {6d,9d,8d} };
    private double[][] singular = { {2d,3d}, {2d,3d} };
    private double[][] bigSingular = {{1d,2d,3d,4d}, {2d,5d,3d,4d},
        {7d,3d,256d,1930d}, {3d,7d,6d,8d}}; // 4th row = 1st + 2nd
    private double[][] detData = { {1d,2d,3d}, {4d,5d,6d}, {7d,8d,10d} };
    private double[] testVector = {1,2,3};
    private double[] testVector2 = {1,2,3,4};
    private double entryTolerance = 10E-16;
    private double normTolerance = 10E-14;
    
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
        RealMatrixImpl m3 = new RealMatrixImpl();
        m3.setData(testData);
    } 
    
    /** test copy functions */
    public void testCopyFunctions() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrixImpl m2 = new RealMatrixImpl(testData2);
        m2.setData(m.getData());
        assertClose("getData",m2,m,entryTolerance);
        // no dangling reference...
        m2.setEntry(1,1,2000d);
        RealMatrixImpl m3 = new RealMatrixImpl(testData);
        assertClose("no getData side effect",m,m3,entryTolerance);
        m3 = (RealMatrixImpl) m.copy();
        double[][] stompMe = {{1d,2d,3d}};
        m3.setDataRef(stompMe);
        assertClose("no copy side effect",m,new RealMatrixImpl(testData),
            entryTolerance);
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
        try {
            RealMatrix a = m.subtract(new RealMatrixImpl(testData2));
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }      
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
        try {
            RealMatrix a = m.multiply(new RealMatrixImpl(bigSingular));
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }      
    }   
        
    /** test isSingular */
    public void testIsSingular() {
        RealMatrixImpl m = new RealMatrixImpl(singular);
        assertTrue("singular",m.isSingular());
        m = new RealMatrixImpl(bigSingular);
        assertTrue("big singular",m.isSingular());
        m = new RealMatrixImpl(id);
        assertTrue("identity nonsingular",!m.isSingular());
        m = new RealMatrixImpl(testData);
        assertTrue("testData nonsingular",!m.isSingular());
    }
        
    /** test inverse */
    public void testInverse() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrix mInv = new RealMatrixImpl(testDataInv);
        assertClose("inverse",mInv,m.inverse(),normTolerance);
        assertClose("inverse^2",m,m.inverse().inverse(),10E-12);
    }
    
    /** test solve */
    public void testSolve() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrix mInv = new RealMatrixImpl(testDataInv);
        // being a bit slothful here -- actually testing that X = A^-1 * B
        assertClose("inverse-operate",mInv.operate(testVector),
            m.solve(testVector),normTolerance);
        try {
            double[] x = m.solve(testVector2);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }       
        RealMatrix bs = new RealMatrixImpl(bigSingular);
        try {
            RealMatrix a = bs.solve(bs);
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            RealMatrix a = m.solve(bs);
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            RealMatrix a = (new RealMatrixImpl(testData2)).solve(bs);
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        } 
        try {
            (new RealMatrixImpl(testData2)).LUDecompose();
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }  
    }
    
    /** test determinant */
    public void testDeterminant() {       
        RealMatrix m = new RealMatrixImpl(bigSingular);
        assertEquals("singular determinant",0,m.getDeterminant(),0);
        m = new RealMatrixImpl(detData);
        assertEquals("nonsingular test",-3d,m.getDeterminant(),normTolerance);
        try {
            double a = new RealMatrixImpl(testData2).getDeterminant();
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }      
    }
    
    /** test trace */
    public void testTrace() {
        RealMatrix m = new RealMatrixImpl(id);
        assertEquals("identity trace",3d,m.getTrace(),entryTolerance);
        m = new RealMatrixImpl(testData2);
        try {
            double x = m.getTrace();
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }      
    }
    
    /** test sclarAdd */
    public void testScalarAdd() {
        RealMatrix m = new RealMatrixImpl(testData);
        assertClose("scalar add",new RealMatrixImpl(testDataPlus2),
            m.scalarAdd(2d),entryTolerance);
    }
                    
    /** test operate */
    public void testOperate() {
        RealMatrix m = new RealMatrixImpl(id);
        double[] x = m.operate(testVector);
        assertClose("identity operate",testVector,x,entryTolerance);
        m = new RealMatrixImpl(bigSingular);
        try {
            x = m.operate(testVector);
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }      
    }
    
    /** test transpose */
    public void testTranspose() {
        RealMatrix m = new RealMatrixImpl(testData); 
        assertClose("inverse-transpose",m.inverse().transpose(),
            m.transpose().inverse(),normTolerance);
        m = new RealMatrixImpl(testData2);
        RealMatrix mt = new RealMatrixImpl(testData2T);
        assertClose("transpose",mt,m.transpose(),normTolerance);
    }
    
    /** test preMultiply */
    public void testPremultiply() {
        RealMatrix m = new RealMatrixImpl(testData);
        RealMatrix mp = new RealMatrixImpl(preMultTest);
        assertClose("premultiply",m.preMultiply(testVector),mp,normTolerance);
        m = new RealMatrixImpl(bigSingular);
        try {
            RealMatrix x = m.preMultiply(testVector);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testGetVectors() {
        RealMatrix m = new RealMatrixImpl(testData);
        assertClose("get row",m.getRow(1),testDataRow1,entryTolerance);
        assertClose("get col",m.getColumn(3),testDataCol3,entryTolerance);
        try {
            double[] x = m.getRow(10);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            double[] x = m.getColumn(-1);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testEntryMutators() {
        RealMatrix m = new RealMatrixImpl(testData);
        assertEquals("get entry",m.getEntry(1,2),2d,entryTolerance);
        m.setEntry(1,2,100d);
        assertEquals("get entry",m.getEntry(1,2),100d,entryTolerance);
        try {
            double x = m.getEntry(0,2);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            m.setEntry(1,4,200d);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
        
    
    //--------------- -----------------Private methods
        
    /** verifies that two matrices are close (1-norm) */              
    private void assertClose(String msg, RealMatrix m, RealMatrix n,
        double tolerance) {
        assertTrue(msg,m.subtract(n).getNorm() < tolerance);
    }
    
    /** verifies that two vectors are close (sup norm) */
    private void assertClose(String msg, double[] m, double[] n,
        double tolerance) {
        if (m.length != n.length) {
            fail("vectors not same length");
        }
        for (int i = 0; i < m.length; i++) {
            assertEquals(msg + " " +  i + " elements differ", 
                m[i],n[i],tolerance);
        }
    }
    
    /** Useful for debugging */
    private void dumpMatrix(RealMatrix m) {
          for (int i = 0; i < m.getRowDimension(); i++) {
              String os = "";
              for (int j = 0; j < m.getColumnDimension(); j++) {
                  os += m.getEntry(i+1, j+1) + " ";
              }
              System.out.println(os);
          }
    }
        
}

