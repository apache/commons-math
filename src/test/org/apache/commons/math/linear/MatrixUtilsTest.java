/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.math.BigDecimal;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the {@link MatrixUtils} class.
 *
 * @version $Revision$ $Date$
 */

public final class MatrixUtilsTest extends TestCase {
    
    protected double[][] testData = { {1d,2d,3d}, {2d,5d,3d}, {1d,0d,8d} };
    protected double[][] nullMatrix = null;
    protected double[] row = {1,2,3};
    protected BigDecimal[] bigRow = 
        {new BigDecimal(1),new BigDecimal(2),new BigDecimal(3)};
    protected String[] stringRow = {"1", "2", "3"};
    protected double[][] rowMatrix = {{1,2,3}};
    protected BigDecimal[][] bigRowMatrix = 
        {{new BigDecimal(1), new BigDecimal(2), new BigDecimal(3)}};
    protected String[][] stringRowMatrix = {{"1", "2", "3"}};
    protected double[] col = {0,4,6};
    protected BigDecimal[] bigCol = 
        {new BigDecimal(0),new BigDecimal(4),new BigDecimal(6)};
    protected String[] stringCol = {"0","4","6"};
    protected double[] nullDoubleArray = null;
    protected double[][] colMatrix = {{0},{4},{6}};
    protected BigDecimal[][] bigColMatrix = 
        {{new BigDecimal(0)},{new BigDecimal(4)},{new BigDecimal(6)}};
    protected String[][] stringColMatrix = {{"0"}, {"4"}, {"6"}};
    
    public MatrixUtilsTest(String name) {
        super(name);
    }
    
    public void setUp() {     
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(MatrixUtilsTest.class);
        suite.setName("MatrixUtils Tests");
        return suite;
    }
    
    public void testCreateRealMatrix() {
        assertEquals(new DenseRealMatrix(testData), 
                MatrixUtils.createRealMatrix(testData));
        try {
            MatrixUtils.createRealMatrix(new double[][] {{1}, {1,2}});  // ragged
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        } 
        try {
            MatrixUtils.createRealMatrix(new double[][] {{}, {}});  // no columns
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRealMatrix(null);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        } 
    }
    
    public void testCreateBigMatrix() {
        assertEquals(new BigMatrixImpl(testData), 
                MatrixUtils.createBigMatrix(testData));
        assertEquals(new BigMatrixImpl(BigMatrixImplTest.asBigDecimal(testData), true), 
                MatrixUtils.createBigMatrix(BigMatrixImplTest.asBigDecimal(testData), false));
        assertEquals(new BigMatrixImpl(BigMatrixImplTest.asBigDecimal(testData), false), 
                MatrixUtils.createBigMatrix(BigMatrixImplTest.asBigDecimal(testData), true));
        assertEquals(new BigMatrixImpl(bigColMatrix), 
                MatrixUtils.createBigMatrix(bigColMatrix));
        assertEquals(new BigMatrixImpl(stringColMatrix), 
                MatrixUtils.createBigMatrix(stringColMatrix));
        try {
            MatrixUtils.createBigMatrix(new double[][] {{1}, {1,2}});  // ragged
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        } 
        try {
            MatrixUtils.createBigMatrix(new double[][] {{}, {}});  // no columns
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createBigMatrix(nullMatrix);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        } 
    }
        
    public void testCreateRowRealMatrix() {
        assertEquals(MatrixUtils.createRowRealMatrix(row),
                     new DenseRealMatrix(rowMatrix));
        try {
            MatrixUtils.createRowRealMatrix(new double[] {});  // empty
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRowRealMatrix(null);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        } 
    }
    
    public void testCreateRowBigMatrix() {
        assertEquals((BigMatrixImpl) MatrixUtils.createRowBigMatrix(row),
                new BigMatrixImpl(rowMatrix));
        assertEquals((BigMatrixImpl) MatrixUtils.createRowBigMatrix(bigRow),
                new BigMatrixImpl(bigRowMatrix));
        assertEquals((BigMatrixImpl) MatrixUtils.createRowBigMatrix(stringRow),
                new BigMatrixImpl(stringRowMatrix));
        try {
            MatrixUtils.createRowBigMatrix(new double[] {});  // empty
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRowBigMatrix(nullDoubleArray);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        } 
    }
    
    public void testCreateColumnRealMatrix() {
        assertEquals(MatrixUtils.createColumnRealMatrix(col),
                     new DenseRealMatrix(colMatrix));
        try {
            MatrixUtils.createColumnRealMatrix(new double[] {});  // empty
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createColumnRealMatrix(null);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        } 
    }
    
    public void testCreateColumnBigMatrix() {
        assertEquals((BigMatrixImpl) MatrixUtils.createColumnBigMatrix(col),
                new BigMatrixImpl(colMatrix));
        assertEquals((BigMatrixImpl) MatrixUtils.createColumnBigMatrix(bigCol),
                new BigMatrixImpl(bigColMatrix));
        assertEquals((BigMatrixImpl) MatrixUtils.createColumnBigMatrix(stringCol),
                new BigMatrixImpl(stringColMatrix));   
       
        try {
            MatrixUtils.createColumnBigMatrix(new double[] {});  // empty
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createColumnBigMatrix(nullDoubleArray);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        } 
    }
    
    /**
     * Verifies that the matrix is an identity matrix
     */
    protected void checkIdentityMatrix(RealMatrix m) {
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j =0; j < m.getColumnDimension(); j++) {
                if (i == j) {
                    assertEquals(m.getEntry(i, j), 1d, 0);
                } else {
                    assertEquals(m.getEntry(i, j), 0d, 0);
                }
            }
        }   
    }
    
    public void testCreateIdentityMatrix() {
        checkIdentityMatrix(MatrixUtils.createRealIdentityMatrix(3));
        checkIdentityMatrix(MatrixUtils.createRealIdentityMatrix(2));
        checkIdentityMatrix(MatrixUtils.createRealIdentityMatrix(1));
        try {
            MatrixUtils.createRealIdentityMatrix(0);
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    /**
     * Verifies that the matrix is an identity matrix
     */
    protected void checkIdentityBigMatrix(BigMatrix m) {
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j =0; j < m.getColumnDimension(); j++) {
                if (i == j) {
                    assertEquals(m.getEntry(i, j), BigMatrixImpl.ONE);
                } else {
                    assertEquals(m.getEntry(i, j), BigMatrixImpl.ZERO);
                }
            }
        }   
    }
    
    public void testCreateBigIdentityMatrix() {
        checkIdentityBigMatrix(MatrixUtils.createBigIdentityMatrix(3));
        checkIdentityBigMatrix(MatrixUtils.createBigIdentityMatrix(2));
        checkIdentityBigMatrix(MatrixUtils.createBigIdentityMatrix(1));
        try {
            MatrixUtils.createRealIdentityMatrix(0);
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
        
}

