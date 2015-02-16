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

package org.apache.commons.math3.linear;

import org.junit.Test;
import org.junit.Assert;

public class LUDecompositionTest {
    private double[][] testData = {
            { 1.0, 2.0, 3.0},
            { 2.0, 5.0, 3.0},
            { 1.0, 0.0, 8.0}
    };
    private double[][] testDataMinus = {
            { -1.0, -2.0, -3.0},
            { -2.0, -5.0, -3.0},
            { -1.0,  0.0, -8.0}
    };
    private double[][] luData = {
            { 2.0, 3.0, 3.0 },
            { 0.0, 5.0, 7.0 },
            { 6.0, 9.0, 8.0 }
    };

    // singular matrices
    private double[][] singular = {
            { 2.0, 3.0 },
            { 2.0, 3.0 }
    };
    private double[][] bigSingular = {
            { 1.0, 2.0,   3.0,    4.0 },
            { 2.0, 5.0,   3.0,    4.0 },
            { 7.0, 3.0, 256.0, 1930.0 },
            { 3.0, 7.0,   6.0,    8.0 }
    }; // 4th row = 1st + 2nd

    private static final double entryTolerance = 10e-16;

    private static final double normTolerance = 10e-14;

    /** test dimensions */
    @Test
    public void testDimensions() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        LUDecomposition LU = new LUDecomposition(matrix);
        Assert.assertEquals(testData.length, LU.getL().getRowDimension());
        Assert.assertEquals(testData.length, LU.getL().getColumnDimension());
        Assert.assertEquals(testData.length, LU.getU().getRowDimension());
        Assert.assertEquals(testData.length, LU.getU().getColumnDimension());
        Assert.assertEquals(testData.length, LU.getP().getRowDimension());
        Assert.assertEquals(testData.length, LU.getP().getColumnDimension());

    }

    /** test non-square matrix */
    @Test
    public void testNonSquare() {
        try {
            new LUDecomposition(MatrixUtils.createRealMatrix(new double[3][2]));
            Assert.fail("Expecting NonSquareMatrixException");
        } catch (NonSquareMatrixException ime) {
            // expected behavior
        }
    }

    /** test PA = LU */
    @Test
    public void testPAEqualLU() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        LUDecomposition lu = new LUDecomposition(matrix);
        RealMatrix l = lu.getL();
        RealMatrix u = lu.getU();
        RealMatrix p = lu.getP();
        double norm = l.multiply(u).subtract(p.multiply(matrix)).getNorm();
        Assert.assertEquals(0, norm, normTolerance);

        matrix = MatrixUtils.createRealMatrix(testDataMinus);
        lu = new LUDecomposition(matrix);
        l = lu.getL();
        u = lu.getU();
        p = lu.getP();
        norm = l.multiply(u).subtract(p.multiply(matrix)).getNorm();
        Assert.assertEquals(0, norm, normTolerance);

        matrix = MatrixUtils.createRealIdentityMatrix(17);
        lu = new LUDecomposition(matrix);
        l = lu.getL();
        u = lu.getU();
        p = lu.getP();
        norm = l.multiply(u).subtract(p.multiply(matrix)).getNorm();
        Assert.assertEquals(0, norm, normTolerance);

        matrix = MatrixUtils.createRealMatrix(singular);
        lu = new LUDecomposition(matrix);
        Assert.assertFalse(lu.getSolver().isNonSingular());
        Assert.assertNull(lu.getL());
        Assert.assertNull(lu.getU());
        Assert.assertNull(lu.getP());

        matrix = MatrixUtils.createRealMatrix(bigSingular);
        lu = new LUDecomposition(matrix);
        Assert.assertFalse(lu.getSolver().isNonSingular());
        Assert.assertNull(lu.getL());
        Assert.assertNull(lu.getU());
        Assert.assertNull(lu.getP());

    }

    /** test that L is lower triangular with unit diagonal */
    @Test
    public void testLLowerTriangular() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        RealMatrix l = new LUDecomposition(matrix).getL();
        for (int i = 0; i < l.getRowDimension(); i++) {
            Assert.assertEquals(l.getEntry(i, i), 1, entryTolerance);
            for (int j = i + 1; j < l.getColumnDimension(); j++) {
                Assert.assertEquals(l.getEntry(i, j), 0, entryTolerance);
            }
        }
    }

    /** test that U is upper triangular */
    @Test
    public void testUUpperTriangular() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        RealMatrix u = new LUDecomposition(matrix).getU();
        for (int i = 0; i < u.getRowDimension(); i++) {
            for (int j = 0; j < i; j++) {
                Assert.assertEquals(u.getEntry(i, j), 0, entryTolerance);
            }
        }
    }

    /** test that P is a permutation matrix */
    @Test
    public void testPPermutation() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        RealMatrix p   = new LUDecomposition(matrix).getP();

        RealMatrix ppT = p.multiply(p.transpose());
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(p.getRowDimension());
        Assert.assertEquals(0, ppT.subtract(id).getNorm(), normTolerance);

        for (int i = 0; i < p.getRowDimension(); i++) {
            int zeroCount  = 0;
            int oneCount   = 0;
            int otherCount = 0;
            for (int j = 0; j < p.getColumnDimension(); j++) {
                final double e = p.getEntry(i, j);
                if (e == 0) {
                    ++zeroCount;
                } else if (e == 1) {
                    ++oneCount;
                } else {
                    ++otherCount;
                }
            }
            Assert.assertEquals(p.getColumnDimension() - 1, zeroCount);
            Assert.assertEquals(1, oneCount);
            Assert.assertEquals(0, otherCount);
        }

        for (int j = 0; j < p.getColumnDimension(); j++) {
            int zeroCount  = 0;
            int oneCount   = 0;
            int otherCount = 0;
            for (int i = 0; i < p.getRowDimension(); i++) {
                final double e = p.getEntry(i, j);
                if (e == 0) {
                    ++zeroCount;
                } else if (e == 1) {
                    ++oneCount;
                } else {
                    ++otherCount;
                }
            }
            Assert.assertEquals(p.getRowDimension() - 1, zeroCount);
            Assert.assertEquals(1, oneCount);
            Assert.assertEquals(0, otherCount);
        }

    }

    /** test singular */
    @Test
    public void testSingular() {
        LUDecomposition lu =
            new LUDecomposition(MatrixUtils.createRealMatrix(testData));
        Assert.assertTrue(lu.getSolver().isNonSingular());
        lu = new LUDecomposition(MatrixUtils.createRealMatrix(singular));
        Assert.assertFalse(lu.getSolver().isNonSingular());
        lu = new LUDecomposition(MatrixUtils.createRealMatrix(bigSingular));
        Assert.assertFalse(lu.getSolver().isNonSingular());
    }

    /** test matrices values */
    @Test
    public void testMatricesValues1() {
       LUDecomposition lu =
            new LUDecomposition(MatrixUtils.createRealMatrix(testData));
        RealMatrix lRef = MatrixUtils.createRealMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.5, 1.0, 0.0 },
                { 0.5, 0.2, 1.0 }
        });
        RealMatrix uRef = MatrixUtils.createRealMatrix(new double[][] {
                { 2.0,  5.0, 3.0 },
                { 0.0, -2.5, 6.5 },
                { 0.0,  0.0, 0.2 }
        });
        RealMatrix pRef = MatrixUtils.createRealMatrix(new double[][] {
                { 0.0, 1.0, 0.0 },
                { 0.0, 0.0, 1.0 },
                { 1.0, 0.0, 0.0 }
        });
        int[] pivotRef = { 1, 2, 0 };

        // check values against known references
        RealMatrix l = lu.getL();
        Assert.assertEquals(0, l.subtract(lRef).getNorm(), 1.0e-13);
        RealMatrix u = lu.getU();
        Assert.assertEquals(0, u.subtract(uRef).getNorm(), 1.0e-13);
        RealMatrix p = lu.getP();
        Assert.assertEquals(0, p.subtract(pRef).getNorm(), 1.0e-13);
        int[] pivot = lu.getPivot();
        for (int i = 0; i < pivotRef.length; ++i) {
            Assert.assertEquals(pivotRef[i], pivot[i]);
        }

        // check the same cached instance is returned the second time
        Assert.assertTrue(l == lu.getL());
        Assert.assertTrue(u == lu.getU());
        Assert.assertTrue(p == lu.getP());

    }

    /** test matrices values */
    @Test
    public void testMatricesValues2() {
       LUDecomposition lu =
            new LUDecomposition(MatrixUtils.createRealMatrix(luData));
        RealMatrix lRef = MatrixUtils.createRealMatrix(new double[][] {
                {    1.0,    0.0, 0.0 },
                {    0.0,    1.0, 0.0 },
                { 1.0 / 3.0, 0.0, 1.0 }
        });
        RealMatrix uRef = MatrixUtils.createRealMatrix(new double[][] {
                { 6.0, 9.0,    8.0    },
                { 0.0, 5.0,    7.0    },
                { 0.0, 0.0, 1.0 / 3.0 }
        });
        RealMatrix pRef = MatrixUtils.createRealMatrix(new double[][] {
                { 0.0, 0.0, 1.0 },
                { 0.0, 1.0, 0.0 },
                { 1.0, 0.0, 0.0 }
        });
        int[] pivotRef = { 2, 1, 0 };

        // check values against known references
        RealMatrix l = lu.getL();
        Assert.assertEquals(0, l.subtract(lRef).getNorm(), 1.0e-13);
        RealMatrix u = lu.getU();
        Assert.assertEquals(0, u.subtract(uRef).getNorm(), 1.0e-13);
        RealMatrix p = lu.getP();
        Assert.assertEquals(0, p.subtract(pRef).getNorm(), 1.0e-13);
        int[] pivot = lu.getPivot();
        for (int i = 0; i < pivotRef.length; ++i) {
            Assert.assertEquals(pivotRef[i], pivot[i]);
        }

        // check the same cached instance is returned the second time
        Assert.assertTrue(l == lu.getL());
        Assert.assertTrue(u == lu.getU());
        Assert.assertTrue(p == lu.getP());
    }
}
