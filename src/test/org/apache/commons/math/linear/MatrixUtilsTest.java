/*
 * Copyright 2004 The Apache Software Foundation.
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
 * Test cases for the {@link MatrixUtils} class.
 *
 * @version $Revision: 1.1 $ $Date: 2004/10/12 06:27:44 $
 */

public final class MatrixUtilsTest extends TestCase {
    
    protected double[][] testData = { {1d,2d,3d}, {2d,5d,3d}, {1d,0d,8d} };
    protected double[] row = {1,2,3};
    protected double[][] rowMatrix = {{1,2,3}};
    protected double[] col = {0,4,6};
    protected double[][] colMatrix = {{0},{4},{6}};
    
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
        assertEquals(new RealMatrixImpl(testData), 
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
        
    public void testCreateRowRealMatrix() {
        assertEquals((RealMatrixImpl) MatrixUtils.createRowRealMatrix(row),
               new RealMatrixImpl(rowMatrix));
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
    
    public void testCreateColumnRealMatrix() {
        assertEquals((RealMatrixImpl) MatrixUtils.createColumnRealMatrix(col),
                new RealMatrixImpl(colMatrix));
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
        
}

