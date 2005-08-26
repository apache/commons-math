/*
 * Copyright 2003-2005 The Apache Software Foundation.
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
 * @version $Revision$ $Date$
 */

public final class RealMatrixImplTest extends TestCase {
    
    // 3 x 3 identity matrix
    protected double[][] id = { {1d,0d,0d}, {0d,1d,0d}, {0d,0d,1d} };
    
    // Test data for group operations
    protected double[][] testData = { {1d,2d,3d}, {2d,5d,3d}, {1d,0d,8d} };
    protected double[][] testDataLU = {{2d, 5d, 3d}, {.5d, -2.5d, 6.5d}, {0.5d, 0.2d, .2d}};
    protected double[][] testDataPlus2 = { {3d,4d,5d}, {4d,7d,5d}, {3d,2d,10d} };
    protected double[][] testDataMinus = { {-1d,-2d,-3d}, {-2d,-5d,-3d}, 
       {-1d,0d,-8d} };
    protected double[] testDataRow1 = {1d,2d,3d};
    protected double[] testDataCol3 = {3d,3d,8d};
    protected double[][] testDataInv = 
        { {-40d,16d,9d}, {13d,-5d,-3d}, {5d,-2d,-1d} };
    protected double[] preMultTest = {8,12,33};
    protected double[][] testData2 ={ {1d,2d,3d}, {2d,5d,3d}};
    protected double[][] testData2T = { {1d,2d}, {2d,5d}, {3d,3d}};
    protected double[][] testDataPlusInv = 
        { {-39d,18d,12d}, {15d,0d,0d}, {6d,-2d,7d} };
    
    // lu decomposition tests
    protected double[][] luData = { {2d,3d,3d}, {0d,5d,7d}, {6d,9d,8d} };
    protected double[][] luDataLUDecomposition = { {6d,9d,8d}, {0d,5d,7d},
            {0.33333333333333,0d,0.33333333333333} };
    
    // singular matrices
    protected double[][] singular = { {2d,3d}, {2d,3d} };
    protected double[][] bigSingular = {{1d,2d,3d,4d}, {2d,5d,3d,4d},
        {7d,3d,256d,1930d}, {3d,7d,6d,8d}}; // 4th row = 1st + 2nd
    protected double[][] detData = { {1d,2d,3d}, {4d,5d,6d}, {7d,8d,10d} };
    protected double[][] detData2 = { {1d, 3d}, {2d, 4d}};
    
    // vectors
    protected double[] testVector = {1,2,3};
    protected double[] testVector2 = {1,2,3,4};
    
    // submatrix accessor tests
    protected double[][] subTestData = {{1, 2, 3, 4}, {1.5, 2.5, 3.5, 4.5},
            {2, 4, 6, 8}, {4, 5, 6, 7}}; 
    // array selections
    protected double[][] subRows02Cols13 = { {2, 4}, {4, 8}};
    protected double[][] subRows03Cols12 = { {2, 3}, {5, 6}};
    protected double[][] subRows03Cols123 = { {2, 3, 4} , {5, 6, 7}};
    // effective permutations
    protected double[][] subRows20Cols123 = { {4, 6, 8} , {2, 3, 4}};
    protected double[][] subRows31Cols31 = {{7, 5}, {4.5, 2.5}};
    // contiguous ranges
    protected double[][] subRows01Cols23 = {{3,4} , {3.5, 4.5}};
    protected double[][] subRows23Cols00 = {{2} , {4}};
    protected double[][] subRows00Cols33 = {{4}};
    // row matrices
    protected double[][] subRow0 = {{1,2,3,4}};
    protected double[][] subRow3 = {{4,5,6,7}};
    // column matrices
    protected double[][] subColumn1 = {{2}, {2.5}, {4}, {5}};
    protected double[][] subColumn3 = {{4}, {4.5}, {8}, {7}};
    
    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;
    
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
    
    /** test copy functions */
    public void testCopyFunctions() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrixImpl m2 = new RealMatrixImpl(m.getData());
        assertEquals(m2,m);
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
            (new RealMatrixImpl(testData2)).luDecompose();
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
        
        // Examples verified against R (version 1.8.1, Red Hat Linux 9)
        m = new RealMatrixImpl(detData2);
        assertEquals("nonsingular R test 1",-2d,m.getDeterminant(),normTolerance);
        m = new RealMatrixImpl(testData);
        assertEquals("nonsingular  R test 2",-1d,m.getDeterminant(),normTolerance);

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
    
    /** test preMultiply by vector */
    public void testPremultiplyVector() {
        RealMatrix m = new RealMatrixImpl(testData);
        assertClose("premultiply",m.preMultiply(testVector),preMultTest,normTolerance);
        m = new RealMatrixImpl(bigSingular);
        try {
            m.preMultiply(testVector);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testPremultiply() {
        RealMatrix m3 = new RealMatrixImpl(d3);   
        RealMatrix m4 = new RealMatrixImpl(d4);
        RealMatrix m5 = new RealMatrixImpl(d5);
        assertClose("m3*m4=m5", m4.preMultiply(m3), m5, entryTolerance);
        
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrixImpl mInv = new RealMatrixImpl(testDataInv);
        RealMatrixImpl identity = new RealMatrixImpl(id);
        RealMatrixImpl m2 = new RealMatrixImpl(testData2);
        assertClose("inverse multiply",m.preMultiply(mInv),
                identity,entryTolerance);
        assertClose("inverse multiply",mInv.preMultiply(m),
                identity,entryTolerance);
        assertClose("identity multiply",m.preMultiply(identity),
                m,entryTolerance);
        assertClose("identity multiply",identity.preMultiply(mInv),
                mInv,entryTolerance);
        try {
            RealMatrix a = m.preMultiply(new RealMatrixImpl(bigSingular));
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }      
    }
    
    public void testGetVectors() {
        RealMatrix m = new RealMatrixImpl(testData);
        assertClose("get row",m.getRow(0),testDataRow1,entryTolerance);
        assertClose("get col",m.getColumn(2),testDataCol3,entryTolerance);
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
    
    public void testGetEntry() {
        RealMatrix m = new RealMatrixImpl(testData);
        assertEquals("get entry",m.getEntry(0,1),2d,entryTolerance);
        try {
            m.getEntry(10, 4);
            fail ("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }
        
    public void testLUDecomposition() throws Exception {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrix lu = m.getLUMatrix();
        assertClose("LU decomposition", lu, (RealMatrix) new RealMatrixImpl(testDataLU), normTolerance);
        verifyDecomposition(m, lu);
        // access LU decomposition on same object to verify caching.
        lu = m.getLUMatrix();
        assertClose("LU decomposition", lu, (RealMatrix) new RealMatrixImpl(testDataLU), normTolerance);
        verifyDecomposition(m, lu);

        m = new RealMatrixImpl(luData);
        lu = m.getLUMatrix();
        assertClose("LU decomposition", lu, (RealMatrix) new RealMatrixImpl(luDataLUDecomposition), normTolerance);
        verifyDecomposition(m, lu);
        m = new RealMatrixImpl(testDataMinus);
        lu = m.getLUMatrix();
        verifyDecomposition(m, lu);
        m = new RealMatrixImpl(id);
        lu = m.getLUMatrix();
        verifyDecomposition(m, lu);
        try {
            m = new RealMatrixImpl(bigSingular); // singular
            lu = m.getLUMatrix();
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            // expected
        }
        try {
            m = new RealMatrixImpl(testData2);  // not square
            lu = m.getLUMatrix();
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            // expected
        }
    }
    
    /** test examples in user guide */
    public void testExamples() {
        // Create a real matrix with two rows and three columns
        double[][] matrixData = { {1d,2d,3d}, {2d,5d,3d}};
        RealMatrix m = new RealMatrixImpl(matrixData);
        // One more with three rows, two columns
        double[][] matrixData2 = { {1d,2d}, {2d,5d}, {1d, 7d}};
        RealMatrix n = new RealMatrixImpl(matrixData2);
        // Now multiply m by n
        RealMatrix p = m.multiply(n);
        assertEquals(2, p.getRowDimension());
        assertEquals(2, p.getColumnDimension());
        // Invert p
        RealMatrix pInverse = p.inverse(); 
        assertEquals(2, pInverse.getRowDimension());
        assertEquals(2, pInverse.getColumnDimension());
        
        // Solve example
        double[][] coefficientsData = {{2, 3, -2}, {-1, 7, 6}, {4, -3, -5}};
        RealMatrix coefficients = new RealMatrixImpl(coefficientsData);
        double[] constants = {1, -2, 1};
        double[] solution = coefficients.solve(constants);
        assertEquals(2 * solution[0] + 3 * solution[1] -2 * solution[2], constants[0], 1E-12);
        assertEquals(-1 * solution[0] + 7 * solution[1] + 6 * solution[2], constants[1], 1E-12);
        assertEquals(4 * solution[0] - 3 * solution[1] -5 * solution[2], constants[2], 1E-12);   
        
    }
    
    // test submatrix accessors
    public void testSubMatrix() {
        RealMatrix m = new RealMatrixImpl(subTestData);
        RealMatrix mRows23Cols00 = new RealMatrixImpl(subRows23Cols00);
        RealMatrix mRows00Cols33 = new RealMatrixImpl(subRows00Cols33);
        RealMatrix mRows01Cols23 = new RealMatrixImpl(subRows01Cols23);
        RealMatrix mRows02Cols13 = new RealMatrixImpl(subRows02Cols13);
        RealMatrix mRows03Cols12 = new RealMatrixImpl(subRows03Cols12);
        RealMatrix mRows03Cols123 = new RealMatrixImpl(subRows03Cols123);
        RealMatrix mRows20Cols123 = new RealMatrixImpl(subRows20Cols123);
        RealMatrix mRows31Cols31 = new RealMatrixImpl(subRows31Cols31);
        assertEquals("Rows23Cols00", mRows23Cols00, 
                m.getSubMatrix(2 , 3 , 0, 0));
        assertEquals("Rows00Cols33", mRows00Cols33, 
                m.getSubMatrix(0 , 0 , 3, 3));
        assertEquals("Rows01Cols23", mRows01Cols23,
                m.getSubMatrix(0 , 1 , 2, 3));   
        assertEquals("Rows02Cols13", mRows02Cols13,
                m.getSubMatrix(new int[] {0,2}, new int[] {1,3}));  
        assertEquals("Rows03Cols12", mRows03Cols12,
                m.getSubMatrix(new int[] {0,3}, new int[] {1,2}));  
        assertEquals("Rows03Cols123", mRows03Cols123,
                m.getSubMatrix(new int[] {0,3}, new int[] {1,2,3})); 
        assertEquals("Rows20Cols123", mRows20Cols123,
                m.getSubMatrix(new int[] {2,0}, new int[] {1,2,3})); 
        assertEquals("Rows31Cols31", mRows31Cols31,
                m.getSubMatrix(new int[] {3,1}, new int[] {3,1})); 
        assertEquals("Rows31Cols31", mRows31Cols31,
                m.getSubMatrix(new int[] {3,1}, new int[] {3,1})); 
        
        try {
            m.getSubMatrix(1,0,2,4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(-1,1,2,2);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(1,0,2,2);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(1,0,2,4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(new int[] {}, new int[] {0});
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(new int[] {0}, new int[] {4});
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }
    
    public void testGetRowMatrix() {
        RealMatrix m = new RealMatrixImpl(subTestData);
        RealMatrix mRow0 = new RealMatrixImpl(subRow0);
        RealMatrix mRow3 = new RealMatrixImpl(subRow3);
        assertEquals("Row0", mRow0, 
                m.getRowMatrix(0));
        assertEquals("Row3", mRow3, 
                m.getRowMatrix(3));
        try {
            m.getRowMatrix(-1);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getRowMatrix(4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }
    
    public void testGetColumnMatrix() {
        RealMatrix m = new RealMatrixImpl(subTestData);
        RealMatrix mColumn1 = new RealMatrixImpl(subColumn1);
        RealMatrix mColumn3 = new RealMatrixImpl(subColumn3);
        assertEquals("Column1", mColumn1, 
                m.getColumnMatrix(1));
        assertEquals("Column3", mColumn3, 
                m.getColumnMatrix(3));
        try {
            m.getColumnMatrix(-1);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getColumnMatrix(4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }
    
    public void testEqualsAndHashCode() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        RealMatrixImpl m1 = (RealMatrixImpl) m.copy();
        RealMatrixImpl mt = (RealMatrixImpl) m.transpose();
        assertTrue(m.hashCode() != mt.hashCode());
        assertEquals(m.hashCode(), m1.hashCode());
        assertEquals(m, m);
        assertEquals(m, m1);
        assertFalse(m.equals(null));
        assertFalse(m.equals(mt));
        assertFalse(m.equals(new RealMatrixImpl(bigSingular))); 
    }
    
    public void testToString() {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        assertEquals("RealMatrixImpl{{1.0,2.0,3.0},{2.0,5.0,3.0},{1.0,0.0,8.0}}",
                m.toString());
        m = new RealMatrixImpl();
        assertEquals("RealMatrixImpl{}",
                m.toString());
    }
    
    public void testSetSubMatrix() throws Exception {
        RealMatrixImpl m = new RealMatrixImpl(testData);
        m.setSubMatrix(detData2,1,1);
        RealMatrix expected = MatrixUtils.createRealMatrix
            (new double[][] {{1.0,2.0,3.0},{2.0,1.0,3.0},{1.0,2.0,4.0}});
        assertEquals(expected, m);  
        
        m.setSubMatrix(detData2,0,0);
        expected = MatrixUtils.createRealMatrix
            (new double[][] {{1.0,3.0,3.0},{2.0,4.0,3.0},{1.0,2.0,4.0}});
        assertEquals(expected, m);  
        
        m.setSubMatrix(testDataPlus2,0,0);      
        expected = MatrixUtils.createRealMatrix
            (new double[][] {{3.0,4.0,5.0},{4.0,7.0,5.0},{3.0,2.0,10.0}});
        assertEquals(expected, m);   
        
        // javadoc example
        RealMatrixImpl matrix = (RealMatrixImpl) MatrixUtils.createRealMatrix
            (new double[][] {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 0, 1 , 2}});
        matrix.setSubMatrix(new double[][] {{3, 4}, {5, 6}}, 1, 1);
        expected = MatrixUtils.createRealMatrix
            (new double[][] {{1, 2, 3, 4}, {5, 3, 4, 8}, {9, 5 ,6, 2}});
        assertEquals(expected, matrix);   
        
        // dimension overflow
        try {  
            m.setSubMatrix(testData,1,1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException e) {
            // expected
        }
        // dimension underflow
        try {  
            m.setSubMatrix(testData,-1,1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException e) {
            // expected
        }
        try {  
            m.setSubMatrix(testData,1,-1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException e) {
            // expected
        }
        
        // null
        try {
            m.setSubMatrix(null,1,1);
            fail("expecting NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        RealMatrixImpl m2 = new RealMatrixImpl();
        try {
            m2.setSubMatrix(testData,0,1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException e) {
            // expected
        }
        try {
            m2.setSubMatrix(testData,1,0);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException e) {
            // expected
        }
        
        // ragged
        try {
            m.setSubMatrix(new double[][] {{1}, {2, 3}}, 0, 0);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
       
        // empty
        try {
            m.setSubMatrix(new double[][] {{}}, 0, 0);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        
    }
    
    //--------------- -----------------Protected methods
        
    /** verifies that two matrices are close (1-norm) */              
    protected void assertClose(String msg, RealMatrix m, RealMatrix n,
        double tolerance) {
        assertTrue(msg,m.subtract(n).getNorm() < tolerance);
    }
    
    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, double[] m, double[] n,
        double tolerance) {
        if (m.length != n.length) {
            fail("vectors not same length");
        }
        for (int i = 0; i < m.length; i++) {
            assertEquals(msg + " " +  i + " elements differ", 
                m[i],n[i],tolerance);
        }
    }
    
    /** extracts the l  and u matrices from compact lu representation */
    protected void splitLU(RealMatrix lu, double[][] lowerData, double[][] upperData) throws InvalidMatrixException {   
        if (!lu.isSquare() || lowerData.length != lowerData[0].length || upperData.length != upperData[0].length ||
                lowerData.length != upperData.length
                || lowerData.length != lu.getRowDimension()) {
            throw new InvalidMatrixException("incorrect dimensions");
        }    
        int n = lu.getRowDimension();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j < i) {
                    lowerData[i][j] = lu.getEntry(i, j);
                    upperData[i][j] = 0d;
                } else if (i == j) {
                    lowerData[i][j] = 1d;
                    upperData[i][j] = lu.getEntry(i, j);
                } else {
                    lowerData[i][j] = 0d;
                    upperData[i][j] = lu.getEntry(i, j);
                }   
            }
        }
    }
    
    /** Returns the result of applying the given row permutation to the matrix */
    protected RealMatrix permuteRows(RealMatrix matrix, int[] permutation) {
        if (!matrix.isSquare() || matrix.getRowDimension() != permutation.length) {
            throw new IllegalArgumentException("dimension mismatch");
        }
        int n = matrix.getRowDimension();
        int m = matrix.getColumnDimension();
        double out[][] = new double[m][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                out[i][j] = matrix.getEntry(permutation[i], j);
            }
        }
        return new RealMatrixImpl(out);
    }
    
    /** Extracts l and u matrices from lu and verifies that matrix = l times u modulo permutation */
    protected void verifyDecomposition(RealMatrix matrix, RealMatrix lu) throws Exception{
        int n = matrix.getRowDimension();
        double[][] lowerData = new double[n][n];
        double[][] upperData = new double[n][n];
        splitLU(lu, lowerData, upperData);
        RealMatrix lower =new RealMatrixImpl(lowerData);
        RealMatrix upper = new RealMatrixImpl(upperData);
        int[] permutation = ((RealMatrixImpl) matrix).getPermutation();
        RealMatrix permuted = permuteRows(matrix, permutation);
        assertClose("lu decomposition does not work", permuted, lower.multiply(upper), normTolerance);
    }
      
    
    /** Useful for debugging */
    private void dumpMatrix(RealMatrix m) {
          for (int i = 0; i < m.getRowDimension(); i++) {
              String os = "";
              for (int j = 0; j < m.getColumnDimension(); j++) {
                  os += m.getEntry(i, j) + " ";
              }
              System.out.println(os);
          }
    }
        
}

