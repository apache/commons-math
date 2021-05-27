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

package org.apache.commons.math4.legacy.field.linalg;

import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.numbers.fraction.Fraction;
import org.apache.commons.numbers.field.FractionField;
import org.apache.commons.math4.legacy.linear.SingularMatrixException;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;

public class FieldLUDecompositionTest {
    private final int[][] testData = {
        { 1, 2, 3 },
        { 2, 5, 3 },
        { 1, 0, 8 }
    };
    private final int[][] testDataMinus = {
        { -1, -2, -3 },
        { -2, -5, -3 },
        { -1, 0, -8 }
    };
    private final int[][] luData = {
        { 2, 3, 3 },
        { 2, 3, 7 },
        { 6, 6, 8 }
    };
    private final int[][] luData2 = {
        { 2, 3, 3 },
        { 0, 5, 7 },
        { 6, 9, 8 }
    };

    // singular matrices
    private int[][] singular = {
        { 2, 3 },
        { 2, 3 }
    };
    private final int[][] bigSingular = {
        { 1, 2,   3,    4 },
        { 2, 5,   3,    4 },
        { 7, 3, 256, 1930 },
        { 3, 7,   6,    8 }
    }; // 4th row = 1st + 2nd

    @Test
    public void testDimensions() {
        FieldDenseMatrix<Fraction> matrix = create(testData);
        FieldLUDecomposition<Fraction> LU = FieldLUDecomposition.of(matrix);
        Assert.assertEquals(testData.length, LU.getL().getRowDimension());
        Assert.assertEquals(testData.length, LU.getU().getRowDimension());
        Assert.assertEquals(testData.length, LU.getP().getRowDimension());
    }

    @Test
    public void testPAEqualLU() {
        FieldDenseMatrix<Fraction> matrix = create(testData);
        FieldLUDecomposition<Fraction> lu = FieldLUDecomposition.of(matrix);
        FieldDenseMatrix<Fraction> l = lu.getL();
        FieldDenseMatrix<Fraction> u = lu.getU();
        FieldDenseMatrix<Fraction> p = lu.getP();
        Assert.assertEquals(p.multiply(matrix), l.multiply(u));

        matrix = create(testDataMinus);
        lu = FieldLUDecomposition.of(matrix);
        l = lu.getL();
        u = lu.getU();
        p = lu.getP();
        Assert.assertEquals(p.multiply(matrix), l.multiply(u));

        matrix = FieldDenseMatrix.identity(FractionField.get(), 17);
        lu = FieldLUDecomposition.of(matrix);
        l = lu.getL();
        u = lu.getU();
        p = lu.getP();
        Assert.assertEquals(p.multiply(matrix), l.multiply(u));
    }

    /* L is lower triangular with unit diagonal */
    @Test
    public void testLLowerTriangular() {
        FieldDenseMatrix<Fraction> matrix = create(testData);
        FieldDenseMatrix<Fraction> l = FieldLUDecomposition.of(matrix).getL();
        for (int i = 0; i < l.getRowDimension(); i++) {
            Assert.assertEquals(Fraction.ONE, l.get(i, i));
            for (int j = i + 1; j < l.getColumnDimension(); j++) {
                Assert.assertEquals(Fraction.ZERO, l.get(i, j));
            }
        }
    }

    /* U is upper triangular */
    @Test
    public void testUUpperTriangular() {
        FieldDenseMatrix<Fraction> matrix = create(testData);
        FieldDenseMatrix<Fraction> u = FieldLUDecomposition.of(matrix).getU();
        for (int i = 0; i < u.getRowDimension(); i++) {
            for (int j = 0; j < i; j++) {
                Assert.assertEquals(Fraction.ZERO, u.get(i, j));
            }
        }
    }

    /* P is a permutation matrix */
    @Test
    public void testPPermutation() {
        FieldDenseMatrix<Fraction> matrix = create(testData);
        FieldDenseMatrix<Fraction> p = FieldLUDecomposition.of(matrix).getP();

        FieldDenseMatrix<Fraction> ppT = p.multiply(p.transpose());
        FieldDenseMatrix<Fraction> id = FieldDenseMatrix.identity(FractionField.get(),
                                                                  p.getRowDimension());
        Assert.assertEquals(id, ppT);

        for (int i = 0; i < p.getRowDimension(); i++) {
            int zeroCount = 0;
            int oneCount = 0;
            int otherCount = 0;
            for (int j = 0; j < p.getColumnDimension(); j++) {
                final Fraction e = p.get(i, j);
                if (e.equals(Fraction.ZERO)) {
                    ++zeroCount;
                } else if (e.equals(Fraction.ONE)) {
                    ++oneCount;
                } else {
                    ++otherCount;
                }
            }
            Assert.assertEquals(p.getRowDimension() - 1, zeroCount);
            Assert.assertEquals(1, oneCount);
            Assert.assertEquals(0, otherCount);
        }

        for (int j = 0; j < p.getRowDimension(); j++) {
            int zeroCount = 0;
            int oneCount = 0;
            int otherCount = 0;
            for (int i = 0; i < p.getColumnDimension(); i++) {
                final Fraction e = p.get(i, j);
                if (e.equals(Fraction.ZERO)) {
                    ++zeroCount;
                } else if (e.equals(Fraction.ONE)) {
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

    @Test
    public void testIsSingular1() {
        FieldLUDecomposition<Fraction> lu = FieldLUDecomposition.of(create(testData));
        Assert.assertFalse(lu.isSingular());
        lu.getSolver();
    }
    @Test(expected=SingularMatrixException.class)
    public void testIsSingular2() {
        FieldLUDecomposition<Fraction> lu = FieldLUDecomposition.of(create(singular));
        Assert.assertTrue(lu.isSingular());
        lu.getSolver();
    }
    @Test(expected=SingularMatrixException.class)
    public void testIsSingular3() {
        FieldLUDecomposition<Fraction> lu = FieldLUDecomposition.of(create(bigSingular));
        Assert.assertTrue(lu.isSingular());
        lu.getSolver();
    }

    @Test
    public void testMatricesValues1() {
        FieldLUDecomposition<Fraction> lu = FieldLUDecomposition.of(create(testData));
        FieldDenseMatrix<Fraction> lRef = create(new int[][] {
                { 1, 0, 0 },
                { 2, 1, 0 },
                { 1, -2, 1 }
            });
        FieldDenseMatrix<Fraction> uRef = create(new int[][] {
                { 1,  2, 3 },
                { 0, 1, -3 },
                { 0,  0, -1 }
            });
        FieldDenseMatrix<Fraction> pRef = create(new int[][] {
                { 1, 0, 0 },
                { 0, 1, 0 },
                { 0, 0, 1 }
            });
        int[] pivotRef = { 0, 1, 2 };

        // check values against known references
        FieldDenseMatrix<Fraction> l = lu.getL();
        Assert.assertEquals(lRef, l);
        FieldDenseMatrix<Fraction> u = lu.getU();
        Assert.assertEquals(uRef, u);
        FieldDenseMatrix<Fraction> p = lu.getP();
        Assert.assertEquals(pRef, p);
        int[] pivot = lu.getPivot();
        for (int i = 0; i < pivotRef.length; ++i) {
            Assert.assertEquals(pivotRef[i], pivot[i]);
        }
    }

    @Test
    public void testMatricesValues2() {
        final FieldLUDecomposition<Fraction> lu = FieldLUDecomposition.of(create(luData));
        final FieldDenseMatrix<Fraction> lRef = create(new int[][] {
                { 1, 0, 0 },
                { 3, 1, 0 },
                { 1, 0, 1 }
            });
        final FieldDenseMatrix<Fraction> uRef = create(new int[][] {
                { 2, 3, 3 },
                { 0, -3, -1 },
                { 0, 0, 4 }
            });
        final FieldDenseMatrix<Fraction> pRef = create(new int[][] {
                { 1, 0, 0 },
                { 0, 0, 1 },
                { 0, 1, 0 }
            });
        int[] pivotRef = { 0, 2, 1 };

        // check values against known references
        final FieldDenseMatrix<Fraction> l = lu.getL();
        Assert.assertEquals(lRef, l);
        final FieldDenseMatrix<Fraction> u = lu.getU();
        Assert.assertEquals(uRef, u);
        final FieldDenseMatrix<Fraction> p = lu.getP();
        Assert.assertEquals(pRef, p);
        int[] pivot = lu.getPivot();
        for (int i = 0; i < pivotRef.length; i++) {
            Assert.assertEquals(pivotRef[i], pivot[i]);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testSolveDimensionErrors() {
        FieldLUDecomposition
            .of(create(testData))
            .getSolver()
            .solve(create(new int[2][2]));
    }

    @Test
    public void testSolve() {
        final FieldDecompositionSolver<Fraction> solver = FieldLUDecomposition
            .of(create(testData))
            .getSolver();
        final FieldDenseMatrix<Fraction> b = create(new int[][] {
                { 1, 0 },
                { 2, -5 },
                { 3, 1 }
            });
        final FieldDenseMatrix<Fraction> xRef = create(new int[][] {
                { 19, -71 },
                { -6, 22 },
                { -2, 9 }
            });

        final FieldDenseMatrix<Fraction> x = solver.solve(b);
        for (int i = 0; i < x.getRowDimension(); i++){
            for (int j = 0; j < x.getColumnDimension(); j++){
                Assert.assertEquals("(" + i + ", " + j + ")",
                                    xRef.get(i, j), x.get(i, j));
            }
        }
    }

    @Test
    public void testDeterminant() {
        Assert.assertEquals(-1, determinant(testData), 1e-15);
        Assert.assertEquals(24, determinant(luData), 1e-14);
        Assert.assertEquals(-10, determinant(luData2), 1e-14);
        Assert.assertEquals(0, determinant(singular), 1e-15);
        Assert.assertEquals(0, determinant(bigSingular), 1e-15);
    }

    private static double determinant(int[][] data) {
        return FieldLUDecomposition.of(create(data)).getDeterminant().doubleValue();
    }

    private static FieldDenseMatrix<Fraction> create(final int[][] data) {
        final int numRows = data.length;
        final int numCols = data[0].length;
        final FieldDenseMatrix<Fraction> m = FieldDenseMatrix
            .create(FractionField.get(), numRows, numCols);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                m.set(i, j, Fraction.of(data[i][j], 1));
            }
        }

        return m;
    }
}
