/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.linear;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the {@link RealMatrixImpl} class.
 *
 * @version $Revision: 1.12 $ $Date: 2004/04/03 22:19:24 $
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
    
    //Additional Test for RealMatrixImplTest.testMultiply

    private double[][] d3 = new double[][] {{1,2,3,4},{5,6,7,8}};
    private double[][] d4 = new double[][] {{1},{2},{3},{4}};
    private double[][] d5 = new double[][] {{30},{70}};
     
    public void testMultiply2() { 
       RealMatrix m3 = new RealMatrixImpl(d3);   
       RealMatrix m4 = new RealMatrixImpl(d4);
       RealMatrix m5 = new RealMatrixImpl(d5);
       assertClose("m3*m4=m5", m3.multiply(m4), m5, entryTolerance);
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
        
        // Not square
        m = new RealMatrixImpl(testData2);
        try {
            m.inverse();
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            // expected
        }
        
        // Singular
        m = new RealMatrixImpl(singular);
        try {
            m.inverse();
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            // expected
        }
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
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            ;
        }
        try {
            RealMatrix a = m.solve(bs);
            fail("Expecting IllegalArgumentException");
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
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
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
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
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
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            ;
        }
        try {
            double[] x = m.getColumn(-1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
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
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            ;
        }
        try {
            m.setEntry(1,4,200d);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
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

