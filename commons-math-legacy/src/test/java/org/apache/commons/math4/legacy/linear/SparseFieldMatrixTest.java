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
import org.apache.commons.math4.legacy.core.Field;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NoDataException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.core.dfp.Dfp;

/**
 * Test cases for the {@link SparseFieldMatrix} class.
 *
 */
public class SparseFieldMatrixTest {
    // 3 x 3 identity matrix
    protected Dfp[][] id = { {Dfp25.of(1), Dfp25.of(0), Dfp25.of(0) }, { Dfp25.of(0), Dfp25.of(1), Dfp25.of(0) }, { Dfp25.of(0), Dfp25.of(0), Dfp25.of(1) } };
    // Test data for group operations
    protected Dfp[][] testData = { { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3) }, { Dfp25.of(2), Dfp25.of(5), Dfp25.of(3) },
            { Dfp25.of(1), Dfp25.of(0), Dfp25.of(8) } };
    protected Dfp[][] testDataLU = null;
    protected Dfp[][] testDataPlus2 = { { Dfp25.of(3), Dfp25.of(4), Dfp25.of(5) }, { Dfp25.of(4), Dfp25.of(7), Dfp25.of(5) },
            { Dfp25.of(3), Dfp25.of(2), Dfp25.of(10) } };
    protected Dfp[][] testDataMinus = { { Dfp25.of(-1), Dfp25.of(-2), Dfp25.of(-3) },
            { Dfp25.of(-2), Dfp25.of(-5), Dfp25.of(-3) }, { Dfp25.of(-1), Dfp25.of(0), Dfp25.of(-8) } };
    protected Dfp[] testDataRow1 = { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3) };
    protected Dfp[] testDataCol3 = { Dfp25.of(3), Dfp25.of(3), Dfp25.of(8) };
    protected Dfp[][] testDataInv = { { Dfp25.of(-40), Dfp25.of(16), Dfp25.of(9) }, { Dfp25.of(13), Dfp25.of(-5), Dfp25.of(-3) },
            { Dfp25.of(5), Dfp25.of(-2), Dfp25.of(-1) } };
    protected Dfp[] preMultTest = { Dfp25.of(8), Dfp25.of(12), Dfp25.of(33) };
    protected Dfp[][] testData2 = { { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3) }, { Dfp25.of(2), Dfp25.of(5), Dfp25.of(3) } };
    protected Dfp[][] testData2T = { { Dfp25.of(1), Dfp25.of(2) }, { Dfp25.of(2), Dfp25.of(5) }, { Dfp25.of(3), Dfp25.of(3) } };
    protected Dfp[][] testDataPlusInv = { { Dfp25.of(-39), Dfp25.of(18), Dfp25.of(12) },
            { Dfp25.of(15), Dfp25.of(0), Dfp25.of(0) }, { Dfp25.of(6), Dfp25.of(-2), Dfp25.of(7) } };

    // lu decomposition tests
    protected Dfp[][] luData = { { Dfp25.of(2), Dfp25.of(3), Dfp25.of(3) }, { Dfp25.of(0), Dfp25.of(5), Dfp25.of(7) }, { Dfp25.of(6), Dfp25.of(9), Dfp25.of(8) } };
    protected Dfp[][] luDataLUDecomposition = null;

    // singular matrices
    protected Dfp[][] singular = { { Dfp25.of(2), Dfp25.of(3) }, { Dfp25.of(2), Dfp25.of(3) } };
    protected Dfp[][] bigSingular = { { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4) },
            { Dfp25.of(2), Dfp25.of(5), Dfp25.of(3), Dfp25.of(4) }, { Dfp25.of(7), Dfp25.of(3), Dfp25.of(256), Dfp25.of(1930) }, { Dfp25.of(3), Dfp25.of(7), Dfp25.of(6), Dfp25.of(8) } }; // 4th

    // row
    // =
    // 1st
    // +
    // 2nd
    protected Dfp[][] detData = { { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3) }, { Dfp25.of(4), Dfp25.of(5), Dfp25.of(6) },
            { Dfp25.of(7), Dfp25.of(8), Dfp25.of(10) } };
    protected Dfp[][] detData2 = { { Dfp25.of(1), Dfp25.of(3) }, { Dfp25.of(2), Dfp25.of(4) } };

    // vectors
    protected Dfp[] testVector = { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3) };
    protected Dfp[] testVector2 = { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4) };

    // submatrix accessor tests
    protected Dfp[][] subTestData = null;

    // array selections
    protected Dfp[][] subRows02Cols13 = { {Dfp25.of(2), Dfp25.of(4) }, { Dfp25.of(4), Dfp25.of(8) } };
    protected Dfp[][] subRows03Cols12 = { { Dfp25.of(2), Dfp25.of(3) }, { Dfp25.of(5), Dfp25.of(6) } };
    protected Dfp[][] subRows03Cols123 = { { Dfp25.of(2), Dfp25.of(3), Dfp25.of(4) }, { Dfp25.of(5), Dfp25.of(6), Dfp25.of(7) } };

    // effective permutations
    protected Dfp[][] subRows20Cols123 = { { Dfp25.of(4), Dfp25.of(6), Dfp25.of(8) }, { Dfp25.of(2), Dfp25.of(3), Dfp25.of(4) } };
    protected Dfp[][] subRows31Cols31 = null;

    // contiguous ranges
    protected Dfp[][] subRows01Cols23 = null;
    protected Dfp[][] subRows23Cols00 = { { Dfp25.of(2) }, { Dfp25.of(4) } };
    protected Dfp[][] subRows00Cols33 = { { Dfp25.of(4) } };

    // row matrices
    protected Dfp[][] subRow0 = { { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4) } };
    protected Dfp[][] subRow3 = { { Dfp25.of(4), Dfp25.of(5), Dfp25.of(6), Dfp25.of(7) } };

    // column matrices
    protected Dfp[][] subColumn1 = null;
    protected Dfp[][] subColumn3 = null;

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;
    protected Field<Dfp> field = Dfp25.getField();

    public SparseFieldMatrixTest() {
        testDataLU = new Dfp[][]{ { Dfp25.of(2), Dfp25.of(5), Dfp25.of(3) }, { Dfp25.of(.5d), Dfp25.of(-2.5d), Dfp25.of(6.5d) },
                                  { Dfp25.of(0.5d), Dfp25.of(0.2d), Dfp25.of(.2d) } };
        luDataLUDecomposition = new Dfp[][]{ { Dfp25.of(6), Dfp25.of(9), Dfp25.of(8) },
                                             { Dfp25.of(0), Dfp25.of(5), Dfp25.of(7) }, { Dfp25.of(0.33333333333333), Dfp25.of(0), Dfp25.of(0.33333333333333) } };
        subTestData = new Dfp[][]{ { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4) },
                                   { Dfp25.of(1.5), Dfp25.of(2.5), Dfp25.of(3.5), Dfp25.of(4.5) }, { Dfp25.of(2), Dfp25.of(4), Dfp25.of(6), Dfp25.of(8) }, { Dfp25.of(4), Dfp25.of(5), Dfp25.of(6), Dfp25.of(7) } };
        subRows31Cols31 = new Dfp[][]{ { Dfp25.of(7), Dfp25.of(5) }, { Dfp25.of(4.5), Dfp25.of(2.5) } };
        subRows01Cols23 = new Dfp[][]{ { Dfp25.of(3), Dfp25.of(4) }, { Dfp25.of(3.5), Dfp25.of(4.5) } };
        subColumn1 = new Dfp[][]{ { Dfp25.of(2) }, { Dfp25.of(2.5) }, { Dfp25.of(4) }, { Dfp25.of(5) } };
        subColumn3 = new Dfp[][]{ { Dfp25.of(4) }, { Dfp25.of(4.5) }, { Dfp25.of(8) }, { Dfp25.of(7) } };
    }

    /** test dimensions */
    @Test
    public void testDimensions() {
        SparseFieldMatrix<Dfp> m = createSparseMatrix(testData);
        SparseFieldMatrix<Dfp> m2 = createSparseMatrix(testData2);
        Assert.assertEquals("testData row dimension", 3, m.getRowDimension());
        Assert.assertEquals("testData column dimension", 3, m.getColumnDimension());
        Assert.assertTrue("testData is square", m.isSquare());
        Assert.assertEquals("testData2 row dimension", m2.getRowDimension(), 2);
        Assert.assertEquals("testData2 column dimension", m2.getColumnDimension(), 3);
        Assert.assertFalse("testData2 is not square", m2.isSquare());
    }

    /** test copy functions */
    @Test
    public void testCopyFunctions() {
        SparseFieldMatrix<Dfp> m1 = createSparseMatrix(testData);
        FieldMatrix<Dfp> m2 = m1.copy();
        Assert.assertEquals(m1.getClass(), m2.getClass());
        Assert.assertEquals(m2, m1);
        SparseFieldMatrix<Dfp> m3 = createSparseMatrix(testData);
        FieldMatrix<Dfp> m4 = m3.copy();
        Assert.assertEquals(m3.getClass(), m4.getClass());
        Assert.assertEquals(m4, m3);
    }

    /** test add */
    @Test
    public void testAdd() {
        SparseFieldMatrix<Dfp> m = createSparseMatrix(testData);
        SparseFieldMatrix<Dfp> mInv = createSparseMatrix(testDataInv);
        SparseFieldMatrix<Dfp> mDataPlusInv = createSparseMatrix(testDataPlusInv);
        FieldMatrix<Dfp> mPlusMInv = m.add(mInv);
        for (int row = 0; row < m.getRowDimension(); row++) {
            for (int col = 0; col < m.getColumnDimension(); col++) {
                Assert.assertEquals("sum entry entry",
                    mDataPlusInv.getEntry(row, col).toDouble(), mPlusMInv.getEntry(row, col).toDouble(),
                    entryTolerance);
            }
        }
    }

    /** test add failure */
    @Test
    public void testAddFail() {
        SparseFieldMatrix<Dfp> m = createSparseMatrix(testData);
        SparseFieldMatrix<Dfp> m2 = createSparseMatrix(testData2);
        try {
            m.add(m2);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }


    /** test m-n = m + -n */
    @Test
    public void testPlusMinus() {
        SparseFieldMatrix<Dfp> m = createSparseMatrix(testData);
        SparseFieldMatrix<Dfp> n = createSparseMatrix(testDataInv);
        assertClose("m-n = m + -n", m.subtract(n),
            n.scalarMultiply(Dfp25.of(-1)).add(m), entryTolerance);
        try {
            m.subtract(createSparseMatrix(testData2));
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    /** test multiply */
    @Test
    public void testMultiply() {
        SparseFieldMatrix<Dfp> m = createSparseMatrix(testData);
        SparseFieldMatrix<Dfp> mInv = createSparseMatrix(testDataInv);
        SparseFieldMatrix<Dfp> identity = createSparseMatrix(id);
        SparseFieldMatrix<Dfp> m2 = createSparseMatrix(testData2);
        assertClose("inverse multiply", m.multiply(mInv), identity,
                entryTolerance);
        assertClose("inverse multiply", m.multiply(new Array2DRowFieldMatrix<>(Dfp25.getField(), testDataInv)), identity,
                    entryTolerance);
        assertClose("inverse multiply", mInv.multiply(m), identity,
                entryTolerance);
        assertClose("identity multiply", m.multiply(identity), m,
                entryTolerance);
        assertClose("identity multiply", identity.multiply(mInv), mInv,
                entryTolerance);
        assertClose("identity multiply", m2.multiply(identity), m2,
                entryTolerance);
        try {
            m.multiply(createSparseMatrix(bigSingular));
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    // Additional Test for Array2DRowRealMatrixTest.testMultiply

    private Dfp[][] d3 = new Dfp[][] { { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4) }, { Dfp25.of(5), Dfp25.of(6), Dfp25.of(7), Dfp25.of(8) } };
    private Dfp[][] d4 = new Dfp[][] { { Dfp25.of(1) }, { Dfp25.of(2) }, { Dfp25.of(3) }, { Dfp25.of(4) } };
    private Dfp[][] d5 = new Dfp[][] { { Dfp25.of(30) }, { Dfp25.of(70) } };

    @Test
    public void testMultiply2() {
        FieldMatrix<Dfp> m3 = createSparseMatrix(d3);
        FieldMatrix<Dfp> m4 = createSparseMatrix(d4);
        FieldMatrix<Dfp> m5 = createSparseMatrix(d5);
        assertClose("m3*m4=m5", m3.multiply(m4), m5, entryTolerance);
    }

    /** test trace */
    @Test
    public void testTrace() {
        FieldMatrix<Dfp> m = createSparseMatrix(id);
        Assert.assertEquals("identity trace", 3d, m.getTrace().toDouble(), entryTolerance);
        m = createSparseMatrix(testData2);
        try {
            m.getTrace();
            Assert.fail("Expecting NonSquareMatrixException");
        } catch (NonSquareMatrixException ex) {
            // ignored
        }
    }

    /** test scalarAdd */
    @Test
    public void testScalarAdd() {
        FieldMatrix<Dfp> m = createSparseMatrix(testData);
        assertClose("scalar add", createSparseMatrix(testDataPlus2),
            m.scalarAdd(Dfp25.of(2)), entryTolerance);
    }

    /** test operate */
    @Test
    public void testOperate() {
        FieldMatrix<Dfp> m = createSparseMatrix(id);
        assertClose("identity operate", testVector, m.operate(testVector),
                entryTolerance);
        assertClose("identity operate", testVector, m.operate(
                new ArrayFieldVector<>(testVector)).toArray(), entryTolerance);
        m = createSparseMatrix(bigSingular);
        try {
            m.operate(testVector);
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    /** test issue MATH-209 */
    @Test
    public void testMath209() {
        FieldMatrix<Dfp> a = createSparseMatrix(new Dfp[][] {
                { Dfp25.of(1), Dfp25.of(2) }, { Dfp25.of(3), Dfp25.of(4) }, { Dfp25.of(5), Dfp25.of(6) } });
        Dfp[] b = a.operate(new Dfp[] { Dfp25.of(1), Dfp25.of(1) });
        Assert.assertEquals(a.getRowDimension(), b.length);
        Assert.assertEquals(3.0, b[0].toDouble(), 1.0e-12);
        Assert.assertEquals(7.0, b[1].toDouble(), 1.0e-12);
        Assert.assertEquals(11.0, b[2].toDouble(), 1.0e-12);
    }

    /** test transpose */
    @Test
    public void testTranspose() {
        FieldMatrix<Dfp> m = createSparseMatrix(testData);
        FieldMatrix<Dfp> mIT = new FieldLUDecomposition<>(m).getSolver().getInverse().transpose();
        FieldMatrix<Dfp> mTI = new FieldLUDecomposition<>(m.transpose()).getSolver().getInverse();
        assertClose("inverse-transpose", mIT, mTI, normTolerance);
        m = createSparseMatrix(testData2);
        FieldMatrix<Dfp> mt = createSparseMatrix(testData2T);
        assertClose("transpose",mt,m.transpose(),normTolerance);
    }

    /** test preMultiply by vector */
    @Test
    public void testPremultiplyVector() {
        FieldMatrix<Dfp> m = createSparseMatrix(testData);
        assertClose("premultiply", m.preMultiply(testVector), preMultTest,
            normTolerance);
        assertClose("premultiply", m.preMultiply(
            new ArrayFieldVector<>(testVector).toArray()), preMultTest, normTolerance);
        m = createSparseMatrix(bigSingular);
        try {
            m.preMultiply(testVector);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    @Test
    public void testPremultiply() {
        FieldMatrix<Dfp> m3 = createSparseMatrix(d3);
        FieldMatrix<Dfp> m4 = createSparseMatrix(d4);
        FieldMatrix<Dfp> m5 = createSparseMatrix(d5);
        assertClose("m3*m4=m5", m4.preMultiply(m3), m5, entryTolerance);

        SparseFieldMatrix<Dfp> m = createSparseMatrix(testData);
        SparseFieldMatrix<Dfp> mInv = createSparseMatrix(testDataInv);
        SparseFieldMatrix<Dfp> identity = createSparseMatrix(id);
        assertClose("inverse multiply", m.preMultiply(mInv), identity,
                entryTolerance);
        assertClose("inverse multiply", mInv.preMultiply(m), identity,
                entryTolerance);
        assertClose("identity multiply", m.preMultiply(identity), m,
                entryTolerance);
        assertClose("identity multiply", identity.preMultiply(mInv), mInv,
                entryTolerance);
        try {
            m.preMultiply(createSparseMatrix(bigSingular));
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    @Test
    public void testGetVectors() {
        FieldMatrix<Dfp> m = createSparseMatrix(testData);
        assertClose("get row", m.getRow(0), testDataRow1, entryTolerance);
        assertClose("get col", m.getColumn(2), testDataCol3, entryTolerance);
        try {
            m.getRow(10);
            Assert.fail("expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // ignored
        }
        try {
            m.getColumn(-1);
            Assert.fail("expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // ignored
        }
    }

    @Test
    public void testGetEntry() {
        FieldMatrix<Dfp> m = createSparseMatrix(testData);
        Assert.assertEquals("get entry", m.getEntry(0, 1).toDouble(), 2d, entryTolerance);
        try {
            m.getEntry(10, 4);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
    }

    /** test examples in user guide */
    @Test
    public void testExamples() {
        // Create a real matrix with two rows and three columns
        Dfp[][] matrixData = { { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3) }, { Dfp25.of(2), Dfp25.of(5), Dfp25.of(3) } };
        FieldMatrix<Dfp> m = createSparseMatrix(matrixData);
        // One more with three rows, two columns
        Dfp[][] matrixData2 = { { Dfp25.of(1), Dfp25.of(2) }, { Dfp25.of(2), Dfp25.of(5) }, { Dfp25.of(1), Dfp25.of(7) } };
        FieldMatrix<Dfp> n = createSparseMatrix(matrixData2);
        // Now multiply m by n
        FieldMatrix<Dfp> p = m.multiply(n);
        Assert.assertEquals(2, p.getRowDimension());
        Assert.assertEquals(2, p.getColumnDimension());
        // Invert p
        FieldMatrix<Dfp> pInverse = new FieldLUDecomposition<>(p).getSolver().getInverse();
        Assert.assertEquals(2, pInverse.getRowDimension());
        Assert.assertEquals(2, pInverse.getColumnDimension());

        // Solve example
        Dfp[][] coefficientsData = { { Dfp25.of(2), Dfp25.of(3), Dfp25.of(-2) }, { Dfp25.of(-1), Dfp25.of(7), Dfp25.of(6) },
                { Dfp25.of(4), Dfp25.of(-3), Dfp25.of(-5) } };
        FieldMatrix<Dfp> coefficients = createSparseMatrix(coefficientsData);
        Dfp[] constants = { Dfp25.of(1), Dfp25.of(-2), Dfp25.of(1) };
        Dfp[] solution;
        solution = new FieldLUDecomposition<>(coefficients)
            .getSolver()
            .solve(new ArrayFieldVector<>(constants, false)).toArray();
        Assert.assertEquals((Dfp25.of(2).multiply(solution[0]).add(Dfp25.of(3).multiply(solution[1])).subtract(Dfp25.of(2).multiply(solution[2]))).toDouble(),
                constants[0].toDouble(), 1E-12);
        Assert.assertEquals(((Dfp25.of(-1).multiply(solution[0])).add(Dfp25.of(7).multiply(solution[1])).add(Dfp25.of(6).multiply(solution[2]))).toDouble(),
                constants[1].toDouble(), 1E-12);
        Assert.assertEquals(((Dfp25.of(4).multiply(solution[0])).subtract(Dfp25.of(3).multiply( solution[1])).subtract(Dfp25.of(5).multiply(solution[2]))).toDouble(),
                constants[2].toDouble(), 1E-12);
    }

    // test submatrix accessors
    @Test
    public void testSubMatrix() {
        FieldMatrix<Dfp> m = createSparseMatrix(subTestData);
        FieldMatrix<Dfp> mRows23Cols00 = createSparseMatrix(subRows23Cols00);
        FieldMatrix<Dfp> mRows00Cols33 = createSparseMatrix(subRows00Cols33);
        FieldMatrix<Dfp> mRows01Cols23 = createSparseMatrix(subRows01Cols23);
        FieldMatrix<Dfp> mRows02Cols13 = createSparseMatrix(subRows02Cols13);
        FieldMatrix<Dfp> mRows03Cols12 = createSparseMatrix(subRows03Cols12);
        FieldMatrix<Dfp> mRows03Cols123 = createSparseMatrix(subRows03Cols123);
        FieldMatrix<Dfp> mRows20Cols123 = createSparseMatrix(subRows20Cols123);
        FieldMatrix<Dfp> mRows31Cols31 = createSparseMatrix(subRows31Cols31);
        Assert.assertEquals("Rows23Cols00", mRows23Cols00, m.getSubMatrix(2, 3, 0, 0));
        Assert.assertEquals("Rows00Cols33", mRows00Cols33, m.getSubMatrix(0, 0, 3, 3));
        Assert.assertEquals("Rows01Cols23", mRows01Cols23, m.getSubMatrix(0, 1, 2, 3));
        Assert.assertEquals("Rows02Cols13", mRows02Cols13,
            m.getSubMatrix(new int[] { 0, 2 }, new int[] { 1, 3 }));
        Assert.assertEquals("Rows03Cols12", mRows03Cols12,
            m.getSubMatrix(new int[] { 0, 3 }, new int[] { 1, 2 }));
        Assert.assertEquals("Rows03Cols123", mRows03Cols123,
            m.getSubMatrix(new int[] { 0, 3 }, new int[] { 1, 2, 3 }));
        Assert.assertEquals("Rows20Cols123", mRows20Cols123,
            m.getSubMatrix(new int[] { 2, 0 }, new int[] { 1, 2, 3 }));
        Assert.assertEquals("Rows31Cols31", mRows31Cols31,
            m.getSubMatrix(new int[] { 3, 1 }, new int[] { 3, 1 }));
        Assert.assertEquals("Rows31Cols31", mRows31Cols31,
            m.getSubMatrix(new int[] { 3, 1 }, new int[] { 3, 1 }));

        try {
            m.getSubMatrix(1, 0, 2, 4);
            Assert.fail("Expecting NumberIsTooSmallException");
        } catch (NumberIsTooSmallException ex) {
            // expected
        }
        try {
            m.getSubMatrix(-1, 1, 2, 2);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.getSubMatrix(1, 0, 2, 2);
            Assert.fail("Expecting NumberIsTooSmallException");
        } catch (NumberIsTooSmallException ex) {
            // expected
        }
        try {
            m.getSubMatrix(1, 0, 2, 4);
            Assert.fail("Expecting NumberIsTooSmallException");
        } catch (NumberIsTooSmallException ex) {
            // expected
        }
        try {
            m.getSubMatrix(new int[] {}, new int[] { 0 });
            Assert.fail("Expecting NoDataException");
        } catch (NoDataException ex) {
            // expected
        }
        try {
            m.getSubMatrix(new int[] { 0 }, new int[] { 4 });
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
    }

    @Test
    public void testGetRowMatrix() {
        FieldMatrix<Dfp> m = createSparseMatrix(subTestData);
        FieldMatrix<Dfp> mRow0 = createSparseMatrix(subRow0);
        FieldMatrix<Dfp> mRow3 = createSparseMatrix(subRow3);
        Assert.assertEquals("Row0", mRow0, m.getRowMatrix(0));
        Assert.assertEquals("Row3", mRow3, m.getRowMatrix(3));
        try {
            m.getRowMatrix(-1);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.getRowMatrix(4);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
    }

    @Test
    public void testGetColumnMatrix() {
        FieldMatrix<Dfp> m = createSparseMatrix(subTestData);
        FieldMatrix<Dfp> mColumn1 = createSparseMatrix(subColumn1);
        FieldMatrix<Dfp> mColumn3 = createSparseMatrix(subColumn3);
        Assert.assertEquals("Column1", mColumn1, m.getColumnMatrix(1));
        Assert.assertEquals("Column3", mColumn3, m.getColumnMatrix(3));
        try {
            m.getColumnMatrix(-1);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.getColumnMatrix(4);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
    }

    @Test
    public void testGetRowVector() {
        FieldMatrix<Dfp> m = createSparseMatrix(subTestData);
        FieldVector<Dfp> mRow0 = new ArrayFieldVector<>(subRow0[0]);
        FieldVector<Dfp> mRow3 = new ArrayFieldVector<>(subRow3[0]);
        Assert.assertEquals("Row0", mRow0, m.getRowVector(0));
        Assert.assertEquals("Row3", mRow3, m.getRowVector(3));
        try {
            m.getRowVector(-1);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.getRowVector(4);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
    }

    @Test
    public void testGetColumnVector() {
        FieldMatrix<Dfp> m = createSparseMatrix(subTestData);
        FieldVector<Dfp> mColumn1 = columnToVector(subColumn1);
        FieldVector<Dfp> mColumn3 = columnToVector(subColumn3);
        Assert.assertEquals("Column1", mColumn1, m.getColumnVector(1));
        Assert.assertEquals("Column3", mColumn3, m.getColumnVector(3));
        try {
            m.getColumnVector(-1);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.getColumnVector(4);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
    }

    private FieldVector<Dfp> columnToVector(Dfp[][] column) {
        Dfp[] data = new Dfp[column.length];
        for (int i = 0; i < data.length; ++i) {
            data[i] = column[i][0];
        }
        return new ArrayFieldVector<>(data, false);
    }

    @Test
    public void testEqualsAndHashCode() {
        SparseFieldMatrix<Dfp> m = createSparseMatrix(testData);
        SparseFieldMatrix<Dfp> m1 = (SparseFieldMatrix<Dfp>) m.copy();
        SparseFieldMatrix<Dfp> mt = (SparseFieldMatrix<Dfp>) m.transpose();
        Assert.assertTrue(m.hashCode() != mt.hashCode());
        Assert.assertEquals(m.hashCode(), m1.hashCode());
        Assert.assertEquals(m, m);
        Assert.assertEquals(m, m1);
        Assert.assertNotEquals(m, null);
        Assert.assertNotEquals(m, mt);
        Assert.assertNotEquals(m, createSparseMatrix(bigSingular));
    }

    /* Disable for now
    @Test
    public void testToString() {
        SparseFieldMatrix<Dfp> m = createSparseMatrix(testData);
        Assert.assertEquals("SparseFieldMatrix<Dfp>{{1.0,2.0,3.0},{2.0,5.0,3.0},{1.0,0.0,8.0}}",
            m.toString());
        m = new SparseFieldMatrix<Dfp>(field, 1, 1);
        Assert.assertEquals("SparseFieldMatrix<Dfp>{{0.0}}", m.toString());
    }
    */

    @Test
    public void testSetSubMatrix() {
        SparseFieldMatrix<Dfp> m = createSparseMatrix(testData);
        m.setSubMatrix(detData2, 1, 1);
        FieldMatrix<Dfp> expected = createSparseMatrix(new Dfp[][] {
                { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3) }, { Dfp25.of(2), Dfp25.of(1), Dfp25.of(3) }, { Dfp25.of(1), Dfp25.of(2), Dfp25.of(4) } });
        Assert.assertEquals(expected, m);

        m.setSubMatrix(detData2, 0, 0);
        expected = createSparseMatrix(new Dfp[][] {
                { Dfp25.of(1), Dfp25.of(3), Dfp25.of(3) }, { Dfp25.of(2), Dfp25.of(4), Dfp25.of(3) }, { Dfp25.of(1), Dfp25.of(2), Dfp25.of(4) } });
        Assert.assertEquals(expected, m);

        m.setSubMatrix(testDataPlus2, 0, 0);
        expected = createSparseMatrix(new Dfp[][] {
                { Dfp25.of(3), Dfp25.of(4), Dfp25.of(5) }, { Dfp25.of(4), Dfp25.of(7), Dfp25.of(5) }, { Dfp25.of(3), Dfp25.of(2), Dfp25.of(10) } });
        Assert.assertEquals(expected, m);

        // javadoc example
        SparseFieldMatrix<Dfp> matrix =
            createSparseMatrix(new Dfp[][] {
        { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4) }, { Dfp25.of(5), Dfp25.of(6), Dfp25.of(7), Dfp25.of(8) }, { Dfp25.of(9), Dfp25.of(0), Dfp25.of(1), Dfp25.of(2) } });
        matrix.setSubMatrix(new Dfp[][] { { Dfp25.of(3), Dfp25.of(4) }, { Dfp25.of(5), Dfp25.of(6) } }, 1, 1);
        expected = createSparseMatrix(new Dfp[][] {
                { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4) }, { Dfp25.of(5), Dfp25.of(3), Dfp25.of(4), Dfp25.of(8) }, { Dfp25.of(9), Dfp25.of(5), Dfp25.of(6), Dfp25.of(2) } });
        Assert.assertEquals(expected, matrix);

        // dimension overflow
        try {
            m.setSubMatrix(testData, 1, 1);
            Assert.fail("expecting OutOfRangeException");
        } catch (OutOfRangeException e) {
            // expected
        }
        // dimension underflow
        try {
            m.setSubMatrix(testData, -1, 1);
            Assert.fail("expecting OutOfRangeException");
        } catch (OutOfRangeException e) {
            // expected
        }
        try {
            m.setSubMatrix(testData, 1, -1);
            Assert.fail("expecting OutOfRangeException");
        } catch (OutOfRangeException e) {
            // expected
        }

        // null
        try {
            m.setSubMatrix(null, 1, 1);
            Assert.fail("expecting NullArgumentException");
        } catch (NullArgumentException e) {
            // expected
        }
        try {
            new SparseFieldMatrix<>(field, 0, 0);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected
        }

        // ragged
        try {
            m.setSubMatrix(new Dfp[][] { { Dfp25.of(1) }, { Dfp25.of(2), Dfp25.of(3) } }, 0, 0);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected
        }

        // empty
        try {
            m.setSubMatrix(new Dfp[][] { {} }, 0, 0);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected
        }
    }

    // --------------- -----------------Protected methods

    /** verifies that two matrices are close (1-norm) */
    protected void assertClose(String msg, FieldMatrix<Dfp> m, FieldMatrix<Dfp> n,
            double tolerance) {
        for(int i=0; i < m.getRowDimension(); i++){
            for(int j=0; j < m.getColumnDimension(); j++){
                Assert.assertEquals(msg, m.getEntry(i,j).toDouble(), n.getEntry(i,j).toDouble(), tolerance);
            }
        }
    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, Dfp[] m, Dfp[] n,
            double tolerance) {
        if (m.length != n.length) {
            Assert.fail("vectors not same length");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(msg + " " + i + " elements differ", m[i].toDouble(), n[i].toDouble(),
                    tolerance);
        }
    }

    private SparseFieldMatrix<Dfp> createSparseMatrix(Dfp[][] data) {
        SparseFieldMatrix<Dfp> matrix = new SparseFieldMatrix<>(field, data.length, data[0].length);
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                matrix.setEntry(row, col, data[row][col]);
            }
        }
        return matrix;
    }
}
