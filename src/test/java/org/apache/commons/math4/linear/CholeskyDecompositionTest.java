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

package org.apache.commons.math4.linear;

import org.apache.commons.math4.linear.CholeskyDecomposition;
import org.apache.commons.math4.linear.MatrixUtils;
import org.apache.commons.math4.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math4.linear.NonSquareMatrixException;
import org.apache.commons.math4.linear.NonSymmetricMatrixException;
import org.apache.commons.math4.linear.RealMatrix;
import org.junit.Test;
import org.junit.Assert;

public class CholeskyDecompositionTest {

    private double[][] testData = new double[][] {
            {  1,  2,   4,   7,  11 },
            {  2, 13,  23,  38,  58 },
            {  4, 23,  77, 122, 182 },
            {  7, 38, 122, 294, 430 },
            { 11, 58, 182, 430, 855 }
    };

    /** test dimensions */
    @Test
    public void testDimensions() {
        CholeskyDecomposition llt =
            new CholeskyDecomposition(MatrixUtils.createRealMatrix(testData));
        Assert.assertEquals(testData.length, llt.getL().getRowDimension());
        Assert.assertEquals(testData.length, llt.getL().getColumnDimension());
        Assert.assertEquals(testData.length, llt.getLT().getRowDimension());
        Assert.assertEquals(testData.length, llt.getLT().getColumnDimension());
    }

    /** test non-square matrix */
    @Test(expected = NonSquareMatrixException.class)
    public void testNonSquare() {
        new CholeskyDecomposition(MatrixUtils.createRealMatrix(new double[3][2]));
    }

    /** test non-symmetric matrix */
    @Test(expected = NonSymmetricMatrixException.class)
    public void testNotSymmetricMatrixException() {
        double[][] changed = testData.clone();
        changed[0][changed[0].length - 1] += 1.0e-5;
        new CholeskyDecomposition(MatrixUtils.createRealMatrix(changed));
    }

    /** test non positive definite matrix */
    @Test(expected = NonPositiveDefiniteMatrixException.class)
    public void testNotPositiveDefinite() {
        new CholeskyDecomposition(MatrixUtils.createRealMatrix(new double[][] {
                { 14, 11, 13, 15, 24 },
                { 11, 34, 13, 8,  25 },
                { 13, 13, 14, 15, 21 },
                { 15, 8,  15, 18, 23 },
                { 24, 25, 21, 23, 45 }
        }));
    }

    @Test(expected = NonPositiveDefiniteMatrixException.class)
    public void testMath274() {
        new CholeskyDecomposition(MatrixUtils.createRealMatrix(new double[][] {
                { 0.40434286, -0.09376327, 0.30328980, 0.04909388 },
                {-0.09376327,  0.10400408, 0.07137959, 0.04762857 },
                { 0.30328980,  0.07137959, 0.30458776, 0.04882449 },
                { 0.04909388,  0.04762857, 0.04882449, 0.07543265 }

        }));
    }

    /** test A = LLT */
    @Test
    public void testAEqualLLT() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        CholeskyDecomposition llt = new CholeskyDecomposition(matrix);
        RealMatrix l  = llt.getL();
        RealMatrix lt = llt.getLT();
        double norm = l.multiply(lt).subtract(matrix).getNorm();
        Assert.assertEquals(0, norm, 1.0e-15);
    }

    /** test that L is lower triangular */
    @Test
    public void testLLowerTriangular() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        RealMatrix l = new CholeskyDecomposition(matrix).getL();
        for (int i = 0; i < l.getRowDimension(); i++) {
            for (int j = i + 1; j < l.getColumnDimension(); j++) {
                Assert.assertEquals(0.0, l.getEntry(i, j), 0.0);
            }
        }
    }

    /** test that LT is transpose of L */
    @Test
    public void testLTTransposed() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        CholeskyDecomposition llt = new CholeskyDecomposition(matrix);
        RealMatrix l  = llt.getL();
        RealMatrix lt = llt.getLT();
        double norm = l.subtract(lt.transpose()).getNorm();
        Assert.assertEquals(0, norm, 1.0e-15);
    }

    /** test matrices values */
    @Test
    public void testMatricesValues() {
        RealMatrix lRef = MatrixUtils.createRealMatrix(new double[][] {
                {  1,  0,  0,  0,  0 },
                {  2,  3,  0,  0,  0 },
                {  4,  5,  6,  0,  0 },
                {  7,  8,  9, 10,  0 },
                { 11, 12, 13, 14, 15 }
        });
       CholeskyDecomposition llt =
            new CholeskyDecomposition(MatrixUtils.createRealMatrix(testData));

        // check values against known references
        RealMatrix l = llt.getL();
        Assert.assertEquals(0, l.subtract(lRef).getNorm(), 1.0e-13);
        RealMatrix lt = llt.getLT();
        Assert.assertEquals(0, lt.subtract(lRef.transpose()).getNorm(), 1.0e-13);

        // check the same cached instance is returned the second time
        Assert.assertTrue(l  == llt.getL());
        Assert.assertTrue(lt == llt.getLT());
    }
}
