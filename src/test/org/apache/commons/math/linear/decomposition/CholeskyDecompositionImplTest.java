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

package org.apache.commons.math.linear.decomposition;

import org.apache.commons.math.MathException;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.decomposition.CholeskyDecomposition;
import org.apache.commons.math.linear.decomposition.CholeskyDecompositionImpl;
import org.apache.commons.math.linear.decomposition.NonSquareMatrixException;
import org.apache.commons.math.linear.decomposition.NotPositiveDefiniteMatrixException;
import org.apache.commons.math.linear.decomposition.NotSymmetricMatrixException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CholeskyDecompositionImplTest extends TestCase {

    private double[][] testData = new double[][] {
            {  1,  2,   4,   7,  11 },
            {  2, 13,  23,  38,  58 },
            {  4, 23,  77, 122, 182 },
            {  7, 38, 122, 294, 430 },
            { 11, 58, 182, 430, 855 }
    };

    public CholeskyDecompositionImplTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CholeskyDecompositionImplTest.class);
        suite.setName("CholeskyDecompositionImpl Tests");
        return suite;
    }

    /** test dimensions */
    public void testDimensions() throws MathException {
        CholeskyDecomposition llt =
            new CholeskyDecompositionImpl(MatrixUtils.createRealMatrix(testData));
        assertEquals(testData.length, llt.getL().getRowDimension());
        assertEquals(testData.length, llt.getL().getColumnDimension());
        assertEquals(testData.length, llt.getLT().getRowDimension());
        assertEquals(testData.length, llt.getLT().getColumnDimension());
    }

    /** test non-square matrix */
    public void testNonSquare() {
        try {
            new CholeskyDecompositionImpl(MatrixUtils.createRealMatrix(new double[3][2]));
        } catch (NonSquareMatrixException ime) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    /** test non-symmetric matrix */
    public void testNotSymmetricMatrixException() {
        try {
            double[][] changed = testData.clone();
            changed[0][changed[0].length - 1] += 1.0e-5;
            new CholeskyDecompositionImpl(MatrixUtils.createRealMatrix(changed));
        } catch (NotSymmetricMatrixException e) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    /** test non positive definite matrix */
    public void testNotPositiveDefinite() {
        try {
            new CholeskyDecompositionImpl(MatrixUtils.createRealMatrix(new double[][] {
                    { 14, 11, 13, 15, 24 },
                    { 11, 34, 13, 8,  25 },
                    { 13, 13, 14, 15, 21 },
                    { 15, 8,  15, 18, 23 },
                    { 24, 25, 21, 23, 45 }
            }));
        } catch (NotPositiveDefiniteMatrixException e) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    /** test A = LLT */
    public void testAEqualLLT() throws MathException {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        CholeskyDecomposition llt = new CholeskyDecompositionImpl(matrix);
        RealMatrix l  = llt.getL();
        RealMatrix lt = llt.getLT();
        double norm = l.multiply(lt).subtract(matrix).getNorm();
        assertEquals(0, norm, 1.0e-15);
    }

    /** test that L is lower triangular */
    public void testLLowerTriangular() throws MathException {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        RealMatrix l = new CholeskyDecompositionImpl(matrix).getL();
        for (int i = 0; i < l.getRowDimension(); i++) {
            for (int j = i + 1; j < l.getColumnDimension(); j++) {
                assertEquals(0.0, l.getEntry(i, j));
            }
        }
    }

    /** test that LT is transpose of L */
    public void testLTTransposed() throws MathException {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        CholeskyDecomposition llt = new CholeskyDecompositionImpl(matrix);
        RealMatrix l  = llt.getL();
        RealMatrix lt = llt.getLT();
        double norm = l.subtract(lt.transpose()).getNorm();
        assertEquals(0, norm, 1.0e-15);
    }

    /** test matrices values */
    public void testMatricesValues() throws MathException {
        RealMatrix lRef = MatrixUtils.createRealMatrix(new double[][] {
                {  1,  0,  0,  0,  0 },
                {  2,  3,  0,  0,  0 },
                {  4,  5,  6,  0,  0 },
                {  7,  8,  9, 10,  0 },
                { 11, 12, 13, 14, 15 }
        });
       CholeskyDecomposition llt =
            new CholeskyDecompositionImpl(MatrixUtils.createRealMatrix(testData));

        // check values against known references
        RealMatrix l = llt.getL();
        assertEquals(0, l.subtract(lRef).getNorm(), 1.0e-13);
        RealMatrix lt = llt.getLT();
        assertEquals(0, lt.subtract(lRef.transpose()).getNorm(), 1.0e-13);

        // check the same cached instance is returned the second time
        assertTrue(l  == llt.getL());
        assertTrue(lt == llt.getLT());
        
    }

}
