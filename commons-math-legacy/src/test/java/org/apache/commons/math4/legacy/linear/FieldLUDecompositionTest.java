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

package org.apache.commons.math4.legacy.linear;

import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.core.dfp.Dfp;

public class FieldLUDecompositionTest {
    private Dfp[][] testData = {
            { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3)},
            { Dfp25.of(2), Dfp25.of(5), Dfp25.of(3)},
            { Dfp25.of(1), Dfp25.of(0), Dfp25.of(8)}
    };
    private Dfp[][] testDataMinus = {
            { Dfp25.of(-1), Dfp25.of(-2), Dfp25.of(-3)},
            { Dfp25.of(-2), Dfp25.of(-5), Dfp25.of(-3)},
            { Dfp25.of(-1),  Dfp25.of(0), Dfp25.of(-8)}
    };
    private Dfp[][] luData = {
            { Dfp25.of(2), Dfp25.of(3), Dfp25.of(3) },
            { Dfp25.of(2), Dfp25.of(3), Dfp25.of(7) },
            { Dfp25.of(6), Dfp25.of(6), Dfp25.of(8) }
    };

    // singular matrices
    private Dfp[][] singular = {
            { Dfp25.of(2), Dfp25.of(3) },
            { Dfp25.of(2), Dfp25.of(3) }
    };
    private Dfp[][] bigSingular = {
            { Dfp25.of(1), Dfp25.of(2),   Dfp25.of(3),    Dfp25.of(4) },
            { Dfp25.of(2), Dfp25.of(5),   Dfp25.of(3),    Dfp25.of(4) },
            { Dfp25.of(7), Dfp25.of(3), Dfp25.of(256), Dfp25.of(1930) },
            { Dfp25.of(3), Dfp25.of(7),   Dfp25.of(6),    Dfp25.of(8) }
    }; // 4th row = 1st + 2nd

    /** test dimensions */
    @Test
    public void testDimensions() {
        FieldMatrix<Dfp> matrix =
            new Array2DRowFieldMatrix<>(Dfp25.getField(), testData);
        FieldLUDecomposition<Dfp> LU = new FieldLUDecomposition<>(matrix);
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
            // we don't use Dfp25.getField() for testing purposes
            new FieldLUDecomposition<>(new Array2DRowFieldMatrix<>(new Dfp[][] {
                    { Dfp25.ZERO, Dfp25.ZERO },
                    { Dfp25.ZERO, Dfp25.ZERO },
                    { Dfp25.ZERO, Dfp25.ZERO }
            }));
            Assert.fail("Expected NonSquareMatrixException");
        } catch (NonSquareMatrixException ime) {
            // expected behavior
        }
    }

    /** test PA = LU */
    @Test
    public void testPAEqualLU() {
        FieldMatrix<Dfp> matrix = new Array2DRowFieldMatrix<>(Dfp25.getField(), testData);
        FieldLUDecomposition<Dfp> lu = new FieldLUDecomposition<>(matrix);
        FieldMatrix<Dfp> l = lu.getL();
        FieldMatrix<Dfp> u = lu.getU();
        FieldMatrix<Dfp> p = lu.getP();
        TestUtils.assertEquals(p.multiply(matrix), l.multiply(u));

        matrix = new Array2DRowFieldMatrix<>(Dfp25.getField(), testDataMinus);
        lu = new FieldLUDecomposition<>(matrix);
        l = lu.getL();
        u = lu.getU();
        p = lu.getP();
        TestUtils.assertEquals(p.multiply(matrix), l.multiply(u));

        matrix = new Array2DRowFieldMatrix<>(Dfp25.getField(), 17, 17);
        for (int i = 0; i < matrix.getRowDimension(); ++i) {
            matrix.setEntry(i, i, Dfp25.ONE);
        }
        lu = new FieldLUDecomposition<>(matrix);
        l = lu.getL();
        u = lu.getU();
        p = lu.getP();
        TestUtils.assertEquals(p.multiply(matrix), l.multiply(u));

        matrix = new Array2DRowFieldMatrix<>(Dfp25.getField(), singular);
        lu = new FieldLUDecomposition<>(matrix);
        Assert.assertFalse(lu.getSolver().isNonSingular());
        Assert.assertNull(lu.getL());
        Assert.assertNull(lu.getU());
        Assert.assertNull(lu.getP());

        matrix = new Array2DRowFieldMatrix<>(Dfp25.getField(), bigSingular);
        lu = new FieldLUDecomposition<>(matrix);
        Assert.assertFalse(lu.getSolver().isNonSingular());
        Assert.assertNull(lu.getL());
        Assert.assertNull(lu.getU());
        Assert.assertNull(lu.getP());

    }

    /** test that L is lower triangular with unit diagonal */
    @Test
    public void testLLowerTriangular() {
        FieldMatrix<Dfp> matrix = new Array2DRowFieldMatrix<>(Dfp25.getField(), testData);
        FieldMatrix<Dfp> l = new FieldLUDecomposition<>(matrix).getL();
        for (int i = 0; i < l.getRowDimension(); i++) {
            Assert.assertEquals(Dfp25.ONE, l.getEntry(i, i));
            for (int j = i + 1; j < l.getColumnDimension(); j++) {
                Assert.assertEquals(Dfp25.ZERO, l.getEntry(i, j));
            }
        }
    }

    /** test that U is upper triangular */
    @Test
    public void testUUpperTriangular() {
        FieldMatrix<Dfp> matrix = new Array2DRowFieldMatrix<>(Dfp25.getField(), testData);
        FieldMatrix<Dfp> u = new FieldLUDecomposition<>(matrix).getU();
        for (int i = 0; i < u.getRowDimension(); i++) {
            for (int j = 0; j < i; j++) {
                Assert.assertEquals(Dfp25.ZERO, u.getEntry(i, j));
            }
        }
    }

    /** test that P is a permutation matrix */
    @Test
    public void testPPermutation() {
        FieldMatrix<Dfp> matrix = new Array2DRowFieldMatrix<>(Dfp25.getField(), testData);
        FieldMatrix<Dfp> p   = new FieldLUDecomposition<>(matrix).getP();

        FieldMatrix<Dfp> ppT = p.multiply(p.transpose());
        FieldMatrix<Dfp> id  =
            new Array2DRowFieldMatrix<>(Dfp25.getField(),
                                          p.getRowDimension(), p.getRowDimension());
        for (int i = 0; i < id.getRowDimension(); ++i) {
            id.setEntry(i, i, Dfp25.ONE);
        }
        TestUtils.assertEquals(id, ppT);

        for (int i = 0; i < p.getRowDimension(); i++) {
            int zeroCount  = 0;
            int oneCount   = 0;
            int otherCount = 0;
            for (int j = 0; j < p.getColumnDimension(); j++) {
                final Dfp e = p.getEntry(i, j);
                if (e.equals(Dfp25.ZERO)) {
                    ++zeroCount;
                } else if (e.equals(Dfp25.ONE)) {
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
                final Dfp e = p.getEntry(i, j);
                if (e.equals(Dfp25.ZERO)) {
                    ++zeroCount;
                } else if (e.equals(Dfp25.ONE)) {
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
        FieldLUDecomposition<Dfp> lu =
            new FieldLUDecomposition<>(new Array2DRowFieldMatrix<>(Dfp25.getField(), testData));
        Assert.assertTrue(lu.getSolver().isNonSingular());
        lu = new FieldLUDecomposition<>(new Array2DRowFieldMatrix<>(Dfp25.getField(), singular));
        Assert.assertFalse(lu.getSolver().isNonSingular());
        lu = new FieldLUDecomposition<>(new Array2DRowFieldMatrix<>(Dfp25.getField(), bigSingular));
        Assert.assertFalse(lu.getSolver().isNonSingular());
    }

    /** test matrices values */
    @Test
    public void testMatricesValues1() {
       FieldLUDecomposition<Dfp> lu =
            new FieldLUDecomposition<>(new Array2DRowFieldMatrix<>(Dfp25.getField(), testData));
        FieldMatrix<Dfp> lRef = new Array2DRowFieldMatrix<>(Dfp25.getField(), new Dfp[][] {
                { Dfp25.of(1), Dfp25.of(0), Dfp25.of(0) },
                { Dfp25.of(2), Dfp25.of(1), Dfp25.of(0) },
                { Dfp25.of(1), Dfp25.of(-2), Dfp25.of(1) }
        });
        FieldMatrix<Dfp> uRef = new Array2DRowFieldMatrix<>(Dfp25.getField(), new Dfp[][] {
                { Dfp25.of(1),  Dfp25.of(2), Dfp25.of(3) },
                { Dfp25.of(0), Dfp25.of(1), Dfp25.of(-3) },
                { Dfp25.of(0),  Dfp25.of(0), Dfp25.of(-1) }
        });
        FieldMatrix<Dfp> pRef = new Array2DRowFieldMatrix<>(Dfp25.getField(), new Dfp[][] {
                { Dfp25.of(1), Dfp25.of(0), Dfp25.of(0) },
                { Dfp25.of(0), Dfp25.of(1), Dfp25.of(0) },
                { Dfp25.of(0), Dfp25.of(0), Dfp25.of(1) }
        });
        int[] pivotRef = { 0, 1, 2 };

        // check values against known references
        FieldMatrix<Dfp> l = lu.getL();
        TestUtils.assertEquals(lRef, l);
        FieldMatrix<Dfp> u = lu.getU();
        TestUtils.assertEquals(uRef, u);
        FieldMatrix<Dfp> p = lu.getP();
        TestUtils.assertEquals(pRef, p);
        int[] pivot = lu.getPivot();
        for (int i = 0; i < pivotRef.length; ++i) {
            Assert.assertEquals(pivotRef[i], pivot[i]);
        }

        // check the same cached instance is returned the second time
        Assert.assertSame(l, lu.getL());
        Assert.assertSame(u, lu.getU());
        Assert.assertSame(p, lu.getP());

    }

    /** test matrices values */
    @Test
    public void testMatricesValues2() {
       FieldLUDecomposition<Dfp> lu =
            new FieldLUDecomposition<>(new Array2DRowFieldMatrix<>(Dfp25.getField(), luData));
        FieldMatrix<Dfp> lRef = new Array2DRowFieldMatrix<>(Dfp25.getField(), new Dfp[][] {
                { Dfp25.of(1), Dfp25.of(0), Dfp25.of(0) },
                { Dfp25.of(3), Dfp25.of(1), Dfp25.of(0) },
                { Dfp25.of(1), Dfp25.of(0), Dfp25.of(1) }
        });
        FieldMatrix<Dfp> uRef = new Array2DRowFieldMatrix<>(Dfp25.getField(), new Dfp[][] {
                { Dfp25.of(2), Dfp25.of(3), Dfp25.of(3)    },
                { Dfp25.of(0), Dfp25.of(-3), Dfp25.of(-1)  },
                { Dfp25.of(0), Dfp25.of(0), Dfp25.of(4) }
        });
        FieldMatrix<Dfp> pRef = new Array2DRowFieldMatrix<>(Dfp25.getField(), new Dfp[][] {
                { Dfp25.of(1), Dfp25.of(0), Dfp25.of(0) },
                { Dfp25.of(0), Dfp25.of(0), Dfp25.of(1) },
                { Dfp25.of(0), Dfp25.of(1), Dfp25.of(0) }
        });
        int[] pivotRef = { 0, 2, 1 };

        // check values against known references
        FieldMatrix<Dfp> l = lu.getL();
        TestUtils.assertEquals(lRef, l);
        FieldMatrix<Dfp> u = lu.getU();
        TestUtils.assertEquals(uRef, u);
        FieldMatrix<Dfp> p = lu.getP();
        TestUtils.assertEquals(pRef, p);
        int[] pivot = lu.getPivot();
        for (int i = 0; i < pivotRef.length; ++i) {
            Assert.assertEquals(pivotRef[i], pivot[i]);
        }

        // check the same cached instance is returned the second time
        Assert.assertSame(l, lu.getL());
        Assert.assertSame(u, lu.getU());
        Assert.assertSame(p, lu.getP());
    }

    @Test
    public void testConstructorWithBigReal() {
        BigReal[][] leftMatrixData = new BigReal[][]{
                {new BigReal(1), new BigReal(0), new BigReal(0), new BigReal(0)},
                {new BigReal(1), new BigReal(0), new BigReal(1), new BigReal(0)},
                {new BigReal(1), new BigReal(1), new BigReal(0), new BigReal(0)},
                {new BigReal(1), new BigReal(1), new BigReal(1), new BigReal(1)},
        };

        FieldMatrix<BigReal> matrix = MatrixUtils.createFieldMatrix(leftMatrixData);
        FieldLUDecomposition<BigReal> lu = new FieldLUDecomposition<>(matrix);
        Assert.assertEquals(new BigReal(-1), lu.getDeterminant());
        Assert.assertArrayEquals(new int[]{0, 2, 1, 3}, lu.getPivot());
    }
}
