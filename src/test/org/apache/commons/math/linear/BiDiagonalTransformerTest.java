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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BiDiagonalTransformerTest extends TestCase {

    private double[][] testSquare = {
            { 24.0 / 25.0, 43.0 / 25.0 },
            { 57.0 / 25.0, 24.0 / 25.0 }
    };

    private double[][] testNonSquare = {
        {  -540.0 / 625.0,  963.0 / 625.0, -216.0 / 625.0 },
        { -1730.0 / 625.0, -744.0 / 625.0, 1008.0 / 625.0 },
        {  -720.0 / 625.0, 1284.0 / 625.0, -288.0 / 625.0 },
        {  -360.0 / 625.0,  192.0 / 625.0, 1756.0 / 625.0 },
    };

    public BiDiagonalTransformerTest(String name) {
        super(name);
    }

    public void testDimensions() {
        checkdimensions(MatrixUtils.createRealMatrix(testSquare));
        checkdimensions(MatrixUtils.createRealMatrix(testNonSquare));
        checkdimensions(MatrixUtils.createRealMatrix(testNonSquare).transpose());
    }

    private void checkdimensions(RealMatrix matrix) {
        final int m = matrix.getRowDimension();
        final int n = matrix.getColumnDimension();
        BiDiagonalTransformer transformer = new BiDiagonalTransformer(matrix);
        assertEquals(m, transformer.getU().getRowDimension());
        assertEquals(m, transformer.getU().getColumnDimension());
        assertEquals(m, transformer.getB().getRowDimension());
        assertEquals(n, transformer.getB().getColumnDimension());
        assertEquals(n, transformer.getV().getRowDimension());
        assertEquals(n, transformer.getV().getColumnDimension());

    }

    public void testAEqualUSVt() {
        checkAEqualUSVt(MatrixUtils.createRealMatrix(testSquare));
        checkAEqualUSVt(MatrixUtils.createRealMatrix(testNonSquare));
        checkAEqualUSVt(MatrixUtils.createRealMatrix(testNonSquare).transpose());
    }

    private void checkAEqualUSVt(RealMatrix matrix) {
        BiDiagonalTransformer transformer = new BiDiagonalTransformer(matrix);
        RealMatrix u = transformer.getU();
        RealMatrix b = transformer.getB();
        RealMatrix v = transformer.getV();
        double norm = u.multiply(b).multiply(v.transpose()).subtract(matrix).getNorm();
        assertEquals(0, norm, 1.0e-14);
    }

    public void testUOrthogonal() {
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testSquare)).getU());
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare)).getU());
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare).transpose()).getU());
    }

    public void testVOrthogonal() {
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testSquare)).getV());
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare)).getV());
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare).transpose()).getV());
    }

    private void checkOrthogonal(RealMatrix m) {
        RealMatrix mTm = m.transpose().multiply(m);
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(mTm.getRowDimension());
        assertEquals(0, mTm.subtract(id).getNorm(), 1.0e-14);        
    }

    public void testBBiDiagonal() {
        checkBiDiagonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testSquare)).getB());
        checkBiDiagonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare)).getB());
        checkBiDiagonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare).transpose()).getB());
    }

    private void checkBiDiagonal(RealMatrix m) {
        final int rows = m.getRowDimension();
        final int cols = m.getColumnDimension();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (rows < cols) {
                    if ((i < j) || (i > j + 1)) {
                        assertEquals(0, m.getEntry(i, j), 1.0e-16);
                    }                    
                } else {
                    if ((i < j - 1) || (i > j)) {
                        assertEquals(0, m.getEntry(i, j), 1.0e-16);
                    }
                }
            }
        }
    }

    public void testMatricesValues() {
       BiDiagonalTransformer transformer =
            new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testSquare));
       final double s17 = Math.sqrt(17.0);
        RealMatrix uRef = MatrixUtils.createRealMatrix(new double[][] {
                {  -8 / (5 * s17), 19 / (5 * s17) },
                { -19 / (5 * s17), -8 / (5 * s17) }
        });
        RealMatrix bRef = MatrixUtils.createRealMatrix(new double[][] {
                { -3 * s17 / 5, 32 * s17 / 85 },
                {      0.0,     -5 * s17 / 17 }
        });
        RealMatrix vRef = MatrixUtils.createRealMatrix(new double[][] {
                { 1.0,  0.0 },
                { 0.0, -1.0 }
        });

        // check values against known references
        RealMatrix u = transformer.getU();
        assertEquals(0, u.subtract(uRef).getNorm(), 1.0e-14);
        RealMatrix b = transformer.getB();
        assertEquals(0, b.subtract(bRef).getNorm(), 1.0e-14);
        RealMatrix v = transformer.getV();
        assertEquals(0, v.subtract(vRef).getNorm(), 1.0e-14);

        // check the same cached instance is returned the second time
        assertTrue(u == transformer.getU());
        assertTrue(b == transformer.getB());
        assertTrue(v == transformer.getV());
        
    }

    public void testUpperOrLower() {
        assertTrue(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testSquare)).isUpperBiDiagonal());
        assertTrue(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare)).isUpperBiDiagonal());
        assertFalse(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare).transpose()).isUpperBiDiagonal());
    }

    public static Test suite() {
        return new TestSuite(BiDiagonalTransformerTest.class);
    }

}
