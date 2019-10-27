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

package org.apache.commons.math4.field.linalg;

import org.apache.commons.numbers.fraction.Fraction;
import org.apache.commons.numbers.field.FractionField;
import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.math4.linear.NonSquareMatrixException;
import org.apache.commons.math4.linear.SingularMatrixException;

public class FieldLUDecompositionTest {
    private final Fraction[][] testData = {
            { Fraction.of(1), Fraction.of(2), Fraction.of(3)},
            { Fraction.of(2), Fraction.of(5), Fraction.of(3)},
            { Fraction.of(1), Fraction.of(0), Fraction.of(8)}
    };
    private final Fraction[][] testDataMinus = {
            { Fraction.of(-1), Fraction.of(-2), Fraction.of(-3)},
            { Fraction.of(-2), Fraction.of(-5), Fraction.of(-3)},
            { Fraction.of(-1), Fraction.of(0), Fraction.of(-8)}
    };
    private final Fraction[][] luData = {
            { Fraction.of(2), Fraction.of(3), Fraction.of(3) },
            { Fraction.of(2), Fraction.of(3), Fraction.of(7) },
            { Fraction.of(6), Fraction.of(6), Fraction.of(8) }
    };

    // singular matrices
    private final Fraction[][] singular = {
            { Fraction.of(2), Fraction.of(3) },
            { Fraction.of(2), Fraction.of(3) }
    };
    private final Fraction[][] bigSingular = {
            { Fraction.of(1), Fraction.of(2), Fraction.of(3), Fraction.of(4) },
            { Fraction.of(2), Fraction.of(5), Fraction.of(3), Fraction.of(4) },
            { Fraction.of(7), Fraction.of(3), Fraction.of(256), Fraction.of(1930) },
            { Fraction.of(3), Fraction.of(7), Fraction.of(6), Fraction.of(8) }
    }; // 4th row = 1st + 2nd

    /**
     * @param data Matrix.
     * @return a {@link FieldDenseMatrix} instance.
     */
    private FieldDenseMatrix<Fraction> create(Fraction[][] data) {
        final FieldDenseMatrix<Fraction> m = FieldDenseMatrix.create(FractionField.get(),
                                                                     data.length,
                                                                     data[0].length);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                m.set(i, j, data[i][j]);
            }
        }

        return m;
    }

    /** test dimensions */
    @Test
    public void testDimensions() {
        FieldDenseMatrix<Fraction> matrix = create(testData);
        FieldLUDecomposition<Fraction> LU = FieldLUDecomposition.of(matrix);
        Assert.assertEquals(testData.length, LU.getL().getRowDimension());
        Assert.assertEquals(testData.length, LU.getU().getRowDimension());
        Assert.assertEquals(testData.length, LU.getP().getRowDimension());
    }

    /** test PA = LU */
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

    /** test that L is lower triangular with unit diagonal */
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

    /** test that U is upper triangular */
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

    /** test that P is a permutation matrix */
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
        FieldDenseMatrix<Fraction> lRef = create(new Fraction[][] {
                { Fraction.of(1), Fraction.of(0), Fraction.of(0) },
                { Fraction.of(2), Fraction.of(1), Fraction.of(0) },
                { Fraction.of(1), Fraction.of(-2), Fraction.of(1) }
            });
        FieldDenseMatrix<Fraction> uRef = create(new Fraction[][] {
                { Fraction.of(1),  Fraction.of(2), Fraction.of(3) },
                { Fraction.of(0), Fraction.of(1), Fraction.of(-3) },
                { Fraction.of(0),  Fraction.of(0), Fraction.of(-1) }
            });
        FieldDenseMatrix<Fraction> pRef = create(new Fraction[][] {
                { Fraction.of(1), Fraction.of(0), Fraction.of(0) },
                { Fraction.of(0), Fraction.of(1), Fraction.of(0) },
                { Fraction.of(0), Fraction.of(0), Fraction.of(1) }
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
        FieldLUDecomposition<Fraction> lu = FieldLUDecomposition.of(create(luData));
        FieldDenseMatrix<Fraction> lRef = create(new Fraction[][] {
                { Fraction.of(1), Fraction.of(0), Fraction.of(0) },
                { Fraction.of(3), Fraction.of(1), Fraction.of(0) },
                { Fraction.of(1), Fraction.of(0), Fraction.of(1) }
            });
        FieldDenseMatrix<Fraction> uRef = create(new Fraction[][] {
                { Fraction.of(2), Fraction.of(3), Fraction.of(3)    },
                { Fraction.of(0), Fraction.of(-3), Fraction.of(-1)  },
                { Fraction.of(0), Fraction.of(0), Fraction.of(4) }
            });
        FieldDenseMatrix<Fraction> pRef = create(new Fraction[][] {
                { Fraction.of(1), Fraction.of(0), Fraction.of(0) },
                { Fraction.of(0), Fraction.of(0), Fraction.of(1) },
                { Fraction.of(0), Fraction.of(1), Fraction.of(0) }
            });
        int[] pivotRef = { 0, 2, 1 };

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
}
