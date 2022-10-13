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
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.MathIllegalStateException;
import org.apache.commons.math4.legacy.exception.NoDataException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.core.dfp.Dfp;

/**
 * Test cases for the {@link Array2DRowFieldMatrix} class.
 *
 */

public final class FieldMatrixImplTest {

    // 3 x 3 identity matrix
    protected Dfp[][] id = { {Dfp25.of(1),Dfp25.of(0),Dfp25.of(0)}, {Dfp25.of(0),Dfp25.of(1),Dfp25.of(0)}, {Dfp25.of(0),Dfp25.of(0),Dfp25.of(1)} };

    // Test data for group operations
    protected Dfp[][] testData = { {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)}, {Dfp25.of(2),Dfp25.of(5),Dfp25.of(3)}, {Dfp25.of(1),Dfp25.of(0),Dfp25.of(8)} };
    protected Dfp[][] testDataLU = {{Dfp25.of(2), Dfp25.of(5), Dfp25.of(3)}, {Dfp25.of(1, 2), Dfp25.of(-5, 2), Dfp25.of(13, 2)}, {Dfp25.of(1, 2), Dfp25.of(1, 5), Dfp25.of(1, 5)}};
    protected Dfp[][] testDataPlus2 = { {Dfp25.of(3),Dfp25.of(4),Dfp25.of(5)}, {Dfp25.of(4),Dfp25.of(7),Dfp25.of(5)}, {Dfp25.of(3),Dfp25.of(2),Dfp25.of(10)} };
    protected Dfp[][] testDataMinus = { {Dfp25.of(-1),Dfp25.of(-2),Dfp25.of(-3)}, {Dfp25.of(-2),Dfp25.of(-5),Dfp25.of(-3)},
       {Dfp25.of(-1),Dfp25.of(0),Dfp25.of(-8)} };
    protected Dfp[] testDataRow1 = {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)};
    protected Dfp[] testDataCol3 = {Dfp25.of(3),Dfp25.of(3),Dfp25.of(8)};
    protected Dfp[][] testDataInv =
        { {Dfp25.of(-40),Dfp25.of(16),Dfp25.of(9)}, {Dfp25.of(13),Dfp25.of(-5),Dfp25.of(-3)}, {Dfp25.of(5),Dfp25.of(-2),Dfp25.of(-1)} };
    protected Dfp[] preMultTest = {Dfp25.of(8),Dfp25.of(12),Dfp25.of(33)};
    protected Dfp[][] testData2 ={ {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)}, {Dfp25.of(2),Dfp25.of(5),Dfp25.of(3)}};
    protected Dfp[][] testData2T = { {Dfp25.of(1),Dfp25.of(2)}, {Dfp25.of(2),Dfp25.of(5)}, {Dfp25.of(3),Dfp25.of(3)}};
    protected Dfp[][] testDataPlusInv =
        { {Dfp25.of(-39),Dfp25.of(18),Dfp25.of(12)}, {Dfp25.of(15),Dfp25.of(0),Dfp25.of(0)}, {Dfp25.of(6),Dfp25.of(-2),Dfp25.of(7)} };

    // lu decomposition tests
    protected Dfp[][] luData = { {Dfp25.of(2),Dfp25.of(3),Dfp25.of(3)}, {Dfp25.of(0),Dfp25.of(5),Dfp25.of(7)}, {Dfp25.of(6),Dfp25.of(9),Dfp25.of(8)} };
    protected Dfp[][] luDataLUDecomposition = { {Dfp25.of(6),Dfp25.of(9),Dfp25.of(8)}, {Dfp25.of(0),Dfp25.of(5),Dfp25.of(7)},
            {Dfp25.of(1, 3),Dfp25.of(0),Dfp25.of(1, 3)} };

    // singular matrices
    protected Dfp[][] singular = { {Dfp25.of(2),Dfp25.of(3)}, {Dfp25.of(2),Dfp25.of(3)} };
    protected Dfp[][] bigSingular = {{Dfp25.of(1),Dfp25.of(2),Dfp25.of(3),Dfp25.of(4)}, {Dfp25.of(2),Dfp25.of(5),Dfp25.of(3),Dfp25.of(4)},
        {Dfp25.of(7),Dfp25.of(3),Dfp25.of(256),Dfp25.of(1930)}, {Dfp25.of(3),Dfp25.of(7),Dfp25.of(6),Dfp25.of(8)}}; // 4th row = 1st + 2nd
    protected Dfp[][] detData = { {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)}, {Dfp25.of(4),Dfp25.of(5),Dfp25.of(6)}, {Dfp25.of(7),Dfp25.of(8),Dfp25.of(10)} };
    protected Dfp[][] detData2 = { {Dfp25.of(1), Dfp25.of(3)}, {Dfp25.of(2), Dfp25.of(4)}};

    // vectors
    protected Dfp[] testVector = {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)};
    protected Dfp[] testVector2 = {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3),Dfp25.of(4)};

    // submatrix accessor tests
    protected Dfp[][] subTestData = {{Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4)}, {Dfp25.of(3, 2), Dfp25.of(5, 2), Dfp25.of(7, 2), Dfp25.of(9, 2)},
            {Dfp25.of(2), Dfp25.of(4), Dfp25.of(6), Dfp25.of(8)}, {Dfp25.of(4), Dfp25.of(5), Dfp25.of(6), Dfp25.of(7)}};
    // array selections
    protected Dfp[][] subRows02Cols13 = { {Dfp25.of(2), Dfp25.of(4)}, {Dfp25.of(4), Dfp25.of(8)}};
    protected Dfp[][] subRows03Cols12 = { {Dfp25.of(2), Dfp25.of(3)}, {Dfp25.of(5), Dfp25.of(6)}};
    protected Dfp[][] subRows03Cols123 = { {Dfp25.of(2), Dfp25.of(3), Dfp25.of(4)} , {Dfp25.of(5), Dfp25.of(6), Dfp25.of(7)}};
    // effective permutations
    protected Dfp[][] subRows20Cols123 = { {Dfp25.of(4), Dfp25.of(6), Dfp25.of(8)} , {Dfp25.of(2), Dfp25.of(3), Dfp25.of(4)}};
    protected Dfp[][] subRows31Cols31 = {{Dfp25.of(7), Dfp25.of(5)}, {Dfp25.of(9, 2), Dfp25.of(5, 2)}};
    // contiguous ranges
    protected Dfp[][] subRows01Cols23 = {{Dfp25.of(3),Dfp25.of(4)} , {Dfp25.of(7, 2), Dfp25.of(9, 2)}};
    protected Dfp[][] subRows23Cols00 = {{Dfp25.of(2)} , {Dfp25.of(4)}};
    protected Dfp[][] subRows00Cols33 = {{Dfp25.of(4)}};
    // row matrices
    protected Dfp[][] subRow0 = {{Dfp25.of(1),Dfp25.of(2),Dfp25.of(3),Dfp25.of(4)}};
    protected Dfp[][] subRow3 = {{Dfp25.of(4),Dfp25.of(5),Dfp25.of(6),Dfp25.of(7)}};
    // column matrices
    protected Dfp[][] subColumn1 = {{Dfp25.of(2)}, {Dfp25.of(5, 2)}, {Dfp25.of(4)}, {Dfp25.of(5)}};
    protected Dfp[][] subColumn3 = {{Dfp25.of(4)}, {Dfp25.of(9, 2)}, {Dfp25.of(8)}, {Dfp25.of(7)}};

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;

    /** test dimensions */
    @Test
    public void testDimensions() {
        Array2DRowFieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        Array2DRowFieldMatrix<Dfp> m2 = new Array2DRowFieldMatrix<>(testData2);
        Assert.assertEquals("testData row dimension",3,m.getRowDimension());
        Assert.assertEquals("testData column dimension",3,m.getColumnDimension());
        Assert.assertTrue("testData is square",m.isSquare());
        Assert.assertEquals("testData2 row dimension",m2.getRowDimension(),2);
        Assert.assertEquals("testData2 column dimension",m2.getColumnDimension(),3);
        Assert.assertFalse("testData2 is not square", m2.isSquare());
    }

    /** test copy functions */
    @Test
    public void testCopyFunctions() {
        Array2DRowFieldMatrix<Dfp> m1 = new Array2DRowFieldMatrix<>(testData);
        Array2DRowFieldMatrix<Dfp> m2 = new Array2DRowFieldMatrix<>(m1.getData());
        Assert.assertEquals(m2,m1);
        Array2DRowFieldMatrix<Dfp> m3 = new Array2DRowFieldMatrix<>(testData);
        Array2DRowFieldMatrix<Dfp> m4 = new Array2DRowFieldMatrix<>(m3.getData(), false);
        Assert.assertEquals(m4,m3);
    }

    /** test add */
    @Test
    public void testAdd() {
        Array2DRowFieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        Array2DRowFieldMatrix<Dfp> mInv = new Array2DRowFieldMatrix<>(testDataInv);
        FieldMatrix<Dfp> mPlusMInv = m.add(mInv);
        Dfp[][] sumEntries = mPlusMInv.getData();
        for (int row = 0; row < m.getRowDimension(); row++) {
            for (int col = 0; col < m.getColumnDimension(); col++) {
                Assert.assertEquals(testDataPlusInv[row][col],sumEntries[row][col]);
            }
        }
    }

    /** test add failure */
    @Test
    public void testAddFail() {
        Array2DRowFieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        Array2DRowFieldMatrix<Dfp> m2 = new Array2DRowFieldMatrix<>(testData2);
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
        Array2DRowFieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        Array2DRowFieldMatrix<Dfp> m2 = new Array2DRowFieldMatrix<>(testDataInv);
        TestUtils.assertEquals(m.subtract(m2),m2.scalarMultiply(Dfp25.of(-1)).add(m));
        try {
            m.subtract(new Array2DRowFieldMatrix<>(testData2));
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    /** test multiply */
    @Test
     public void testMultiply() {
        Array2DRowFieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        Array2DRowFieldMatrix<Dfp> mInv = new Array2DRowFieldMatrix<>(testDataInv);
        Array2DRowFieldMatrix<Dfp> identity = new Array2DRowFieldMatrix<>(id);
        Array2DRowFieldMatrix<Dfp> m2 = new Array2DRowFieldMatrix<>(testData2);
        TestUtils.assertEquals(m.multiply(mInv), identity);
        TestUtils.assertEquals(mInv.multiply(m), identity);
        TestUtils.assertEquals(m.multiply(identity), m);
        TestUtils.assertEquals(identity.multiply(mInv), mInv);
        TestUtils.assertEquals(m2.multiply(identity), m2);
        try {
            m.multiply(new Array2DRowFieldMatrix<>(bigSingular));
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    //Additional Test for Array2DRowFieldMatrix<Dfp>Test.testMultiply

    private final Dfp[][] d3 = new Dfp[][] {{Dfp25.of(1),Dfp25.of(2),Dfp25.of(3),Dfp25.of(4)},{Dfp25.of(5),Dfp25.of(6),Dfp25.of(7),Dfp25.of(8)}};
    private final Dfp[][] d4 = new Dfp[][] {{Dfp25.of(1)},{Dfp25.of(2)},{Dfp25.of(3)},{Dfp25.of(4)}};
    private final Dfp[][] d5 = new Dfp[][] {{Dfp25.of(30)},{Dfp25.of(70)}};

    @Test
    public void testMultiply2() {
       FieldMatrix<Dfp> m3 = new Array2DRowFieldMatrix<>(d3);
       FieldMatrix<Dfp> m4 = new Array2DRowFieldMatrix<>(d4);
       FieldMatrix<Dfp> m5 = new Array2DRowFieldMatrix<>(d5);
       TestUtils.assertEquals(m3.multiply(m4), m5);
   }

    @Test
    public void testPower() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        FieldMatrix<Dfp> mInv = new Array2DRowFieldMatrix<>(testDataInv);
        FieldMatrix<Dfp> mPlusInv = new Array2DRowFieldMatrix<>(testDataPlusInv);
        FieldMatrix<Dfp> identity = new Array2DRowFieldMatrix<>(id);

        TestUtils.assertEquals(m.power(0), identity);
        TestUtils.assertEquals(mInv.power(0), identity);
        TestUtils.assertEquals(mPlusInv.power(0), identity);

        TestUtils.assertEquals(m.power(1), m);
        TestUtils.assertEquals(mInv.power(1), mInv);
        TestUtils.assertEquals(mPlusInv.power(1), mPlusInv);

        FieldMatrix<Dfp> C1 = m.copy();
        FieldMatrix<Dfp> C2 = mInv.copy();
        FieldMatrix<Dfp> C3 = mPlusInv.copy();

        // stop at 5 to avoid overflow
        for (int i = 2; i <= 5; ++i) {
            C1 = C1.multiply(m);
            C2 = C2.multiply(mInv);
            C3 = C3.multiply(mPlusInv);

            TestUtils.assertEquals(m.power(i), C1);
            TestUtils.assertEquals(mInv.power(i), C2);
            TestUtils.assertEquals(mPlusInv.power(i), C3);
        }

        try {
            FieldMatrix<Dfp> mNotSquare = new Array2DRowFieldMatrix<>(testData2T);
            mNotSquare.power(2);
            Assert.fail("Expecting NonSquareMatrixException");
        } catch (NonSquareMatrixException ex) {
            // ignored
        }

        try {
            m.power(-1);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    /** test trace */
    @Test
    public void testTrace() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(id);
        Assert.assertEquals("identity trace",Dfp25.of(3),m.getTrace());
        m = new Array2DRowFieldMatrix<>(testData2);
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
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        TestUtils.assertEquals(new Array2DRowFieldMatrix<>(testDataPlus2), m.scalarAdd(Dfp25.of(2)));
    }

    /** test operate */
    @Test
    public void testOperate() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(id);
        TestUtils.assertEquals(testVector, m.operate(testVector));
        TestUtils.assertEquals(testVector, m.operate(new ArrayFieldVector<>(testVector)).toArray());
        m = new Array2DRowFieldMatrix<>(bigSingular);
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
        FieldMatrix<Dfp> a = new Array2DRowFieldMatrix<>(new Dfp[][] {
                { Dfp25.of(1), Dfp25.of(2) }, { Dfp25.of(3), Dfp25.of(4) }, { Dfp25.of(5), Dfp25.of(6) }
        }, false);
        Dfp[] b = a.operate(new Dfp[] { Dfp25.of(1), Dfp25.of(1) });
        Assert.assertEquals(a.getRowDimension(), b.length);
        Assert.assertEquals( Dfp25.of(3), b[0]);
        Assert.assertEquals( Dfp25.of(7), b[1]);
        Assert.assertEquals(Dfp25.of(11), b[2]);
    }

    /** test transpose */
    @Test
    public void testTranspose() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        FieldMatrix<Dfp> mIT = new FieldLUDecomposition<>(m).getSolver().getInverse().transpose();
        FieldMatrix<Dfp> mTI = new FieldLUDecomposition<>(m.transpose()).getSolver().getInverse();
        TestUtils.assertEquals(mIT, mTI);
        m = new Array2DRowFieldMatrix<>(testData2);
        FieldMatrix<Dfp> mt = new Array2DRowFieldMatrix<>(testData2T);
        TestUtils.assertEquals(mt, m.transpose());
    }

    /** test preMultiply by vector */
    @Test
    public void testPremultiplyVector() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        TestUtils.assertEquals(m.preMultiply(testVector), preMultTest);
        TestUtils.assertEquals(m.preMultiply(new ArrayFieldVector<>(testVector).toArray()),
                               preMultTest);
        m = new Array2DRowFieldMatrix<>(bigSingular);
        try {
            m.preMultiply(testVector);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    @Test
    public void testPremultiply() {
        FieldMatrix<Dfp> m3 = new Array2DRowFieldMatrix<>(d3);
        FieldMatrix<Dfp> m4 = new Array2DRowFieldMatrix<>(d4);
        FieldMatrix<Dfp> m5 = new Array2DRowFieldMatrix<>(d5);
        TestUtils.assertEquals(m4.preMultiply(m3), m5);

        Array2DRowFieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        Array2DRowFieldMatrix<Dfp> mInv = new Array2DRowFieldMatrix<>(testDataInv);
        Array2DRowFieldMatrix<Dfp> identity = new Array2DRowFieldMatrix<>(id);
        TestUtils.assertEquals(m.preMultiply(mInv), identity);
        TestUtils.assertEquals(mInv.preMultiply(m), identity);
        TestUtils.assertEquals(m.preMultiply(identity), m);
        TestUtils.assertEquals(identity.preMultiply(mInv), mInv);
        try {
            m.preMultiply(new Array2DRowFieldMatrix<>(bigSingular));
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    @Test
    public void testGetVectors() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        TestUtils.assertEquals(m.getRow(0), testDataRow1);
        TestUtils.assertEquals(m.getColumn(2), testDataCol3);
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
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        Assert.assertEquals("get entry", m.getEntry(0,1), Dfp25.of(2));
        try {
            m.getEntry(10, 4);
            Assert.fail ("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
    }

    /** test examples in user guide */
    @Test
    public void testExamples() {
        // Create a real matrix with two rows and three columns
        Dfp[][] matrixData = {
                {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)},
                {Dfp25.of(2),Dfp25.of(5),Dfp25.of(3)}
        };
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(matrixData);
        // One more with three rows, two columns
        Dfp[][] matrixData2 = {
                {Dfp25.of(1),Dfp25.of(2)},
                {Dfp25.of(2),Dfp25.of(5)},
                {Dfp25.of(1), Dfp25.of(7)}
        };
        FieldMatrix<Dfp> n = new Array2DRowFieldMatrix<>(matrixData2);
        // Now multiply m by n
        FieldMatrix<Dfp> p = m.multiply(n);
        Assert.assertEquals(2, p.getRowDimension());
        Assert.assertEquals(2, p.getColumnDimension());
        // Invert p
        FieldMatrix<Dfp> pInverse = new FieldLUDecomposition<>(p).getSolver().getInverse();
        Assert.assertEquals(2, pInverse.getRowDimension());
        Assert.assertEquals(2, pInverse.getColumnDimension());

        // Solve example
        Dfp[][] coefficientsData = {
                {Dfp25.of(2), Dfp25.of(3), Dfp25.of(-2)},
                {Dfp25.of(-1), Dfp25.of(7), Dfp25.of(6)},
                {Dfp25.of(4), Dfp25.of(-3), Dfp25.of(-5)}
        };
        FieldMatrix<Dfp> coefficients = new Array2DRowFieldMatrix<>(coefficientsData);
        Dfp[] constants = {
            Dfp25.of(1), Dfp25.of(-2), Dfp25.of(1)
        };
        Dfp[] solution;
        solution = new FieldLUDecomposition<>(coefficients)
            .getSolver()
            .solve(new ArrayFieldVector<>(constants, false)).toArray();
        Assert.assertEquals(Dfp25.of(2).multiply(solution[0]).
                            add(Dfp25.of(3).multiply(solution[1])).
                            subtract(Dfp25.of(2).multiply(solution[2])).toDouble(),
                            constants[0].toDouble(),
                            0d);
        Assert.assertEquals(Dfp25.of(-1).multiply(solution[0]).
                            add(Dfp25.of(7).multiply(solution[1])).
                            add(Dfp25.of(6).multiply(solution[2])).toDouble(),
                            constants[1].toDouble(),
                            0d);
        Assert.assertEquals(Dfp25.of(4).multiply(solution[0]).
                            subtract(Dfp25.of(3).multiply(solution[1])).
                            subtract(Dfp25.of(5).multiply(solution[2])).toDouble(),
                            constants[2].toDouble(),
                            0d);
    }

    // test submatrix accessors
    @Test
    public void testGetSubMatrix() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        checkGetSubMatrix(m, subRows23Cols00,  2 , 3 , 0, 0);
        checkGetSubMatrix(m, subRows00Cols33,  0 , 0 , 3, 3);
        checkGetSubMatrix(m, subRows01Cols23,  0 , 1 , 2, 3);
        checkGetSubMatrix(m, subRows02Cols13,  new int[] { 0, 2 }, new int[] { 1, 3 });
        checkGetSubMatrix(m, subRows03Cols12,  new int[] { 0, 3 }, new int[] { 1, 2 });
        checkGetSubMatrix(m, subRows03Cols123, new int[] { 0, 3 }, new int[] { 1, 2, 3 });
        checkGetSubMatrix(m, subRows20Cols123, new int[] { 2, 0 }, new int[] { 1, 2, 3 });
        checkGetSubMatrix(m, subRows31Cols31,  new int[] { 3, 1 }, new int[] { 3, 1 });
        checkGetSubMatrix(m, subRows31Cols31,  new int[] { 3, 1 }, new int[] { 3, 1 });
        checkGetSubMatrix(m, null,  1, 0, 2, 4);
        checkGetSubMatrix(m, null, -1, 1, 2, 2);
        checkGetSubMatrix(m, null,  1, 0, 2, 2);
        checkGetSubMatrix(m, null,  1, 0, 2, 4);
        checkGetSubMatrix(m, null, new int[] {},    new int[] { 0 });
        checkGetSubMatrix(m, null, new int[] { 0 }, new int[] { 4 });
    }

    private void checkGetSubMatrix(FieldMatrix<Dfp> m, Dfp[][] reference,
                                   int startRow, int endRow, int startColumn, int endColumn) {
        try {
            FieldMatrix<Dfp> sub = m.getSubMatrix(startRow, endRow, startColumn, endColumn);
            if (reference != null) {
                Assert.assertEquals(new Array2DRowFieldMatrix<>(reference), sub);
            } else {
                Assert.fail("Expecting OutOfRangeException or NotStrictlyPositiveException"
                     + " or NumberIsTooSmallException or NoDataException");
            }
        } catch (OutOfRangeException e) {
            if (reference != null) {
                throw e;
            }
        } catch (NotStrictlyPositiveException e) {
            if (reference != null) {
                throw e;
            }
        } catch (NumberIsTooSmallException e) {
            if (reference != null) {
                throw e;
            }
        } catch (NoDataException e) {
            if (reference != null) {
                throw e;
            }
        }
    }

    private void checkGetSubMatrix(FieldMatrix<Dfp> m, Dfp[][] reference,
                                   int[] selectedRows, int[] selectedColumns) {
        try {
            FieldMatrix<Dfp> sub = m.getSubMatrix(selectedRows, selectedColumns);
            if (reference != null) {
                Assert.assertEquals(new Array2DRowFieldMatrix<>(reference), sub);
            } else {
                Assert.fail("Expecting OutOfRangeException or NotStrictlyPositiveException"
                     + " or NumberIsTooSmallException or NoDataException");
            }
        } catch (OutOfRangeException e) {
            if (reference != null) {
                throw e;
            }
        } catch (NotStrictlyPositiveException e) {
            if (reference != null) {
                throw e;
            }
        } catch (NumberIsTooSmallException e) {
            if (reference != null) {
                throw e;
            }
        } catch (NoDataException e) {
            if (reference != null) {
                throw e;
            }
        }
    }

    @Test
    public void testCopySubMatrix() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        checkCopy(m, subRows23Cols00,  2 , 3 , 0, 0);
        checkCopy(m, subRows00Cols33,  0 , 0 , 3, 3);
        checkCopy(m, subRows01Cols23,  0 , 1 , 2, 3);
        checkCopy(m, subRows02Cols13,  new int[] { 0, 2 }, new int[] { 1, 3 });
        checkCopy(m, subRows03Cols12,  new int[] { 0, 3 }, new int[] { 1, 2 });
        checkCopy(m, subRows03Cols123, new int[] { 0, 3 }, new int[] { 1, 2, 3 });
        checkCopy(m, subRows20Cols123, new int[] { 2, 0 }, new int[] { 1, 2, 3 });
        checkCopy(m, subRows31Cols31,  new int[] { 3, 1 }, new int[] { 3, 1 });
        checkCopy(m, subRows31Cols31,  new int[] { 3, 1 }, new int[] { 3, 1 });

        checkCopy(m, null,  1, 0, 2, 4);
        checkCopy(m, null, -1, 1, 2, 2);
        checkCopy(m, null,  1, 0, 2, 2);
        checkCopy(m, null,  1, 0, 2, 4);
        checkCopy(m, null, new int[] {},    new int[] { 0 });
        checkCopy(m, null, new int[] { 0 }, new int[] { 4 });
    }

    private void checkCopy(FieldMatrix<Dfp> m, Dfp[][] reference,
                           int startRow, int endRow, int startColumn, int endColumn) {
        try {
            Dfp[][] sub = (reference == null) ?
                             new Dfp[1][1] :
                             new Dfp[reference.length][reference[0].length];
            m.copySubMatrix(startRow, endRow, startColumn, endColumn, sub);
            if (reference != null) {
                Assert.assertEquals(new Array2DRowFieldMatrix<>(reference), new Array2DRowFieldMatrix<>(sub));
            } else {
                Assert.fail("Expecting OutOfRangeException or NumberIsTooSmallException or NoDataException");
            }
        } catch (OutOfRangeException e) {
            if (reference != null) {
                throw e;
            }
        } catch (NumberIsTooSmallException e) {
            if (reference != null) {
                throw e;
            }
        } catch (NoDataException e) {
            if (reference != null) {
                throw e;
            }
        }
    }

    private void checkCopy(FieldMatrix<Dfp> m, Dfp[][] reference,
                           int[] selectedRows, int[] selectedColumns) {
        try {
            Dfp[][] sub = (reference == null) ?
                    new Dfp[1][1] :
                    new Dfp[reference.length][reference[0].length];
            m.copySubMatrix(selectedRows, selectedColumns, sub);
            if (reference != null) {
                Assert.assertEquals(new Array2DRowFieldMatrix<>(reference), new Array2DRowFieldMatrix<>(sub));
            } else {
                Assert.fail("Expecting OutOfRangeException or NumberIsTooSmallException or NoDataException");
            }
        } catch (OutOfRangeException e) {
            if (reference != null) {
                throw e;
            }
        } catch (NumberIsTooSmallException e) {
            if (reference != null) {
                throw e;
            }
        } catch (NoDataException e) {
            if (reference != null) {
                throw e;
            }
        }
    }

    @Test
    public void testGetRowMatrix() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        FieldMatrix<Dfp> mRow0 = new Array2DRowFieldMatrix<>(subRow0);
        FieldMatrix<Dfp> mRow3 = new Array2DRowFieldMatrix<>(subRow3);
        Assert.assertEquals("Row0", mRow0,
                m.getRowMatrix(0));
        Assert.assertEquals("Row3", mRow3,
                m.getRowMatrix(3));
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
    public void testSetRowMatrix() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        FieldMatrix<Dfp> mRow3 = new Array2DRowFieldMatrix<>(subRow3);
        Assert.assertNotSame(mRow3, m.getRowMatrix(0));
        m.setRowMatrix(0, mRow3);
        Assert.assertEquals(mRow3, m.getRowMatrix(0));
        try {
            m.setRowMatrix(-1, mRow3);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.setRowMatrix(0, m);
            Assert.fail("Expecting MatrixDimensionMismatchException");
        } catch (MatrixDimensionMismatchException ex) {
            // expected
        }
    }

    @Test
    public void testGetColumnMatrix() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        FieldMatrix<Dfp> mColumn1 = new Array2DRowFieldMatrix<>(subColumn1);
        FieldMatrix<Dfp> mColumn3 = new Array2DRowFieldMatrix<>(subColumn3);
        Assert.assertEquals("Column1", mColumn1,
                m.getColumnMatrix(1));
        Assert.assertEquals("Column3", mColumn3,
                m.getColumnMatrix(3));
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
    public void testSetColumnMatrix() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        FieldMatrix<Dfp> mColumn3 = new Array2DRowFieldMatrix<>(subColumn3);
        Assert.assertNotSame(mColumn3, m.getColumnMatrix(1));
        m.setColumnMatrix(1, mColumn3);
        Assert.assertEquals(mColumn3, m.getColumnMatrix(1));
        try {
            m.setColumnMatrix(-1, mColumn3);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.setColumnMatrix(0, m);
            Assert.fail("Expecting MatrixDimensionMismatchException");
        } catch (MatrixDimensionMismatchException ex) {
            // expected
        }
    }

    @Test
    public void testGetRowVector() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
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
    public void testSetRowVector() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        FieldVector<Dfp> mRow3 = new ArrayFieldVector<>(subRow3[0]);
        Assert.assertNotSame(mRow3, m.getRowMatrix(0));
        m.setRowVector(0, mRow3);
        Assert.assertEquals(mRow3, m.getRowVector(0));
        try {
            m.setRowVector(-1, mRow3);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.setRowVector(0, new ArrayFieldVector<>(Dfp25.getField(), 5));
            Assert.fail("Expecting MatrixDimensionMismatchException");
        } catch (MatrixDimensionMismatchException ex) {
            // expected
        }
    }

    @Test
    public void testGetColumnVector() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
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

    @Test
    public void testSetColumnVector() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        FieldVector<Dfp> mColumn3 = columnToVector(subColumn3);
        Assert.assertNotSame(mColumn3, m.getColumnVector(1));
        m.setColumnVector(1, mColumn3);
        Assert.assertEquals(mColumn3, m.getColumnVector(1));
        try {
            m.setColumnVector(-1, mColumn3);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.setColumnVector(0, new ArrayFieldVector<>(Dfp25.getField(), 5));
            Assert.fail("Expecting MatrixDimensionMismatchException");
        } catch (MatrixDimensionMismatchException ex) {
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
    public void testGetRow() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        checkArrays(subRow0[0], m.getRow(0));
        checkArrays(subRow3[0], m.getRow(3));
        try {
            m.getRow(-1);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.getRow(4);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
    }

    @Test
    public void testSetRow() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        Assert.assertNotSame(subRow3[0][0], m.getRow(0)[0]);
        m.setRow(0, subRow3[0]);
        checkArrays(subRow3[0], m.getRow(0));
        try {
            m.setRow(-1, subRow3[0]);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.setRow(0, new Dfp[5]);
            Assert.fail("Expecting MatrixDimensionMismatchException");
        } catch (MatrixDimensionMismatchException ex) {
            // expected
        }
    }

    @Test
    public void testGetColumn() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        Dfp[] mColumn1 = columnToArray(subColumn1);
        Dfp[] mColumn3 = columnToArray(subColumn3);
        checkArrays(mColumn1, m.getColumn(1));
        checkArrays(mColumn3, m.getColumn(3));
        try {
            m.getColumn(-1);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.getColumn(4);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
    }

    @Test
    public void testSetColumn() {
        FieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(subTestData);
        Dfp[] mColumn3 = columnToArray(subColumn3);
        Assert.assertNotSame(mColumn3[0], m.getColumn(1)[0]);
        m.setColumn(1, mColumn3);
        checkArrays(mColumn3, m.getColumn(1));
        try {
            m.setColumn(-1, mColumn3);
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // expected
        }
        try {
            m.setColumn(0, new Dfp[5]);
            Assert.fail("Expecting MatrixDimensionMismatchException");
        } catch (MatrixDimensionMismatchException ex) {
            // expected
        }
    }

    private Dfp[] columnToArray(Dfp[][] column) {
        Dfp[] data = new Dfp[column.length];
        for (int i = 0; i < data.length; ++i) {
            data[i] = column[i][0];
        }
        return data;
    }

    private void checkArrays(Dfp[] expected, Dfp[] actual) {
        Assert.assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; ++i) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    public void testEqualsAndHashCode() {
        Array2DRowFieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        Array2DRowFieldMatrix<Dfp> m1 = (Array2DRowFieldMatrix<Dfp>) m.copy();
        Array2DRowFieldMatrix<Dfp> mt = (Array2DRowFieldMatrix<Dfp>) m.transpose();
        Assert.assertTrue(m.hashCode() != mt.hashCode());
        Assert.assertEquals(m.hashCode(), m1.hashCode());
        Assert.assertEquals(m, m);
        Assert.assertEquals(m, m1);
        Assert.assertNotEquals(m, null);
        Assert.assertNotEquals(m, mt);
        Assert.assertNotEquals(m, new Array2DRowFieldMatrix<>(bigSingular));
    }

    @Test
    public void testToString() {
        Array2DRowFieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        Assert.assertEquals("Array2DRowFieldMatrix{{1.,2.,3.},{2.,5.,3.},{1.,0.,8.}}", m.toString());
        m = new Array2DRowFieldMatrix<>(Dfp25.getField());
        Assert.assertEquals("Array2DRowFieldMatrix{}", m.toString());
    }

    @Test
    public void testSetSubMatrix() {
        Array2DRowFieldMatrix<Dfp> m = new Array2DRowFieldMatrix<>(testData);
        m.setSubMatrix(detData2,1,1);
        FieldMatrix<Dfp> expected = new Array2DRowFieldMatrix<>
            (new Dfp[][] {
                    {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)},
                    {Dfp25.of(2),Dfp25.of(1),Dfp25.of(3)},
                    {Dfp25.of(1),Dfp25.of(2),Dfp25.of(4)}
             });
        Assert.assertEquals(expected, m);

        m.setSubMatrix(detData2,0,0);
        expected = new Array2DRowFieldMatrix<>
            (new Dfp[][] {
                    {Dfp25.of(1),Dfp25.of(3),Dfp25.of(3)},
                    {Dfp25.of(2),Dfp25.of(4),Dfp25.of(3)},
                    {Dfp25.of(1),Dfp25.of(2),Dfp25.of(4)}
             });
        Assert.assertEquals(expected, m);

        m.setSubMatrix(testDataPlus2,0,0);
        expected = new Array2DRowFieldMatrix<>
            (new Dfp[][] {
                    {Dfp25.of(3),Dfp25.of(4),Dfp25.of(5)},
                    {Dfp25.of(4),Dfp25.of(7),Dfp25.of(5)},
                    {Dfp25.of(3),Dfp25.of(2),Dfp25.of(10)}
             });
        Assert.assertEquals(expected, m);

        // dimension overflow
        try {
            m.setSubMatrix(testData,1,1);
            Assert.fail("expecting OutOfRangeException");
        } catch (OutOfRangeException e) {
            // expected
        }
        // dimension underflow
        try {
            m.setSubMatrix(testData,-1,1);
            Assert.fail("expecting OutOfRangeException");
        } catch (OutOfRangeException e) {
            // expected
        }
        try {
            m.setSubMatrix(testData,1,-1);
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
        Array2DRowFieldMatrix<Dfp> m2 = new Array2DRowFieldMatrix<>(Dfp25.getField());
        try {
            m2.setSubMatrix(testData,0,1);
            Assert.fail("expecting MathIllegalStateException");
        } catch (MathIllegalStateException e) {
            // expected
        }
        try {
            m2.setSubMatrix(testData,1,0);
            Assert.fail("expecting MathIllegalStateException");
        } catch (MathIllegalStateException e) {
            // expected
        }

        // ragged
        try {
            m.setSubMatrix(new Dfp[][] {{Dfp25.of(1)}, {Dfp25.of(2), Dfp25.of(3)}}, 0, 0);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected
        }

        // empty
        try {
            m.setSubMatrix(new Dfp[][] {{}}, 0, 0);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testWalk() {
        int rows    = 150;
        int columns = 75;

        FieldMatrix<Dfp> m =
            new Array2DRowFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInRowOrder(new SetVisitor());
        GetVisitor getVisitor = new GetVisitor();
        m.walkInOptimizedOrder(getVisitor);
        Assert.assertEquals(rows * columns, getVisitor.getCount());

        m = new Array2DRowFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInRowOrder(new SetVisitor(), 1, rows - 2, 1, columns - 2);
        getVisitor = new GetVisitor();
        m.walkInOptimizedOrder(getVisitor, 1, rows - 2, 1, columns - 2);
        Assert.assertEquals((rows - 2) * (columns - 2), getVisitor.getCount());
        for (int i = 0; i < rows; ++i) {
            Assert.assertEquals(Dfp25.of(0), m.getEntry(i, 0));
            Assert.assertEquals(Dfp25.of(0), m.getEntry(i, columns - 1));
        }
        for (int j = 0; j < columns; ++j) {
            Assert.assertEquals(Dfp25.of(0), m.getEntry(0, j));
            Assert.assertEquals(Dfp25.of(0), m.getEntry(rows - 1, j));
        }

        m = new Array2DRowFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInColumnOrder(new SetVisitor());
        getVisitor = new GetVisitor();
        m.walkInOptimizedOrder(getVisitor);
        Assert.assertEquals(rows * columns, getVisitor.getCount());

        m = new Array2DRowFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInColumnOrder(new SetVisitor(), 1, rows - 2, 1, columns - 2);
        getVisitor = new GetVisitor();
        m.walkInOptimizedOrder(getVisitor, 1, rows - 2, 1, columns - 2);
        Assert.assertEquals((rows - 2) * (columns - 2), getVisitor.getCount());
        for (int i = 0; i < rows; ++i) {
            Assert.assertEquals(Dfp25.of(0), m.getEntry(i, 0));
            Assert.assertEquals(Dfp25.of(0), m.getEntry(i, columns - 1));
        }
        for (int j = 0; j < columns; ++j) {
            Assert.assertEquals(Dfp25.of(0), m.getEntry(0, j));
            Assert.assertEquals(Dfp25.of(0), m.getEntry(rows - 1, j));
        }

        m = new Array2DRowFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInOptimizedOrder(new SetVisitor());
        getVisitor = new GetVisitor();
        m.walkInRowOrder(getVisitor);
        Assert.assertEquals(rows * columns, getVisitor.getCount());

        m = new Array2DRowFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInOptimizedOrder(new SetVisitor(), 1, rows - 2, 1, columns - 2);
        getVisitor = new GetVisitor();
        m.walkInRowOrder(getVisitor, 1, rows - 2, 1, columns - 2);
        Assert.assertEquals((rows - 2) * (columns - 2), getVisitor.getCount());
        for (int i = 0; i < rows; ++i) {
            Assert.assertEquals(Dfp25.of(0), m.getEntry(i, 0));
            Assert.assertEquals(Dfp25.of(0), m.getEntry(i, columns - 1));
        }
        for (int j = 0; j < columns; ++j) {
            Assert.assertEquals(Dfp25.of(0), m.getEntry(0, j));
            Assert.assertEquals(Dfp25.of(0), m.getEntry(rows - 1, j));
        }

        m = new Array2DRowFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInOptimizedOrder(new SetVisitor());
        getVisitor = new GetVisitor();
        m.walkInColumnOrder(getVisitor);
        Assert.assertEquals(rows * columns, getVisitor.getCount());

        m = new Array2DRowFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInOptimizedOrder(new SetVisitor(), 1, rows - 2, 1, columns - 2);
        getVisitor = new GetVisitor();
        m.walkInColumnOrder(getVisitor, 1, rows - 2, 1, columns - 2);
        Assert.assertEquals((rows - 2) * (columns - 2), getVisitor.getCount());
        for (int i = 0; i < rows; ++i) {
            Assert.assertEquals(Dfp25.of(0), m.getEntry(i, 0));
            Assert.assertEquals(Dfp25.of(0), m.getEntry(i, columns - 1));
        }
        for (int j = 0; j < columns; ++j) {
            Assert.assertEquals(Dfp25.of(0), m.getEntry(0, j));
            Assert.assertEquals(Dfp25.of(0), m.getEntry(rows - 1, j));
        }
    }

    @Test
    public void testSerial()  {
        final int r = 2;
        final int c = 3;
        Array2DRowFieldMatrix<BigReal> m = new Array2DRowFieldMatrix<>(BigRealField.getInstance(), r, c);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                m.setEntry(i, j, new BigReal(Math.random()));
            }
        }
        Assert.assertEquals(m,TestUtils.serializeAndRecover(m));
    }

    private static class SetVisitor extends DefaultFieldMatrixChangingVisitor<Dfp> {
        SetVisitor() {
            super(Dfp25.ZERO);
        }
        @Override
        public Dfp visit(int i, int j, Dfp value) {
            return Dfp25.of(i * 1024 + j, 1024);
        }
    }

    private static class GetVisitor extends DefaultFieldMatrixPreservingVisitor<Dfp> {
        private int count;
        GetVisitor() {
            super(Dfp25.ZERO);
            count = 0;
        }
        @Override
        public void visit(int i, int j, Dfp value) {
            ++count;
            Assert.assertEquals(Dfp25.of(i * 1024 + j, 1024), value);
        }
        public int getCount() {
            return count;
        }
    }

    //--------------- -----------------Protected methods

    /** extracts the l  and u matrices from compact lu representation */
    protected void splitLU(FieldMatrix<Dfp> lu,
                           Dfp[][] lowerData,
                           Dfp[][] upperData) {
        if (!lu.isSquare()) {
            throw new NonSquareMatrixException(lu.getRowDimension(), lu.getColumnDimension());
        }
        if (lowerData.length != lowerData[0].length) {
            throw new DimensionMismatchException(lowerData.length, lowerData[0].length);
        }
        if (upperData.length != upperData[0].length) {
            throw new DimensionMismatchException(upperData.length, upperData[0].length);
        }
        if (lowerData.length != upperData.length) {
            throw new DimensionMismatchException(lowerData.length, upperData.length);
        }
        if (lowerData.length != lu.getRowDimension()) {
            throw new DimensionMismatchException(lowerData.length, lu.getRowDimension());
        }
        int n = lu.getRowDimension();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j < i) {
                    lowerData[i][j] = lu.getEntry(i, j);
                    upperData[i][j] = Dfp25.ZERO;
                } else if (i == j) {
                    lowerData[i][j] = Dfp25.ONE;
                    upperData[i][j] = lu.getEntry(i, j);
                } else {
                    lowerData[i][j] = Dfp25.ZERO;
                    upperData[i][j] = lu.getEntry(i, j);
                }
            }
        }
    }

    /** Returns the result of applying the given row permutation to the matrix */
    protected FieldMatrix<Dfp> permuteRows(FieldMatrix<Dfp> matrix, int[] permutation) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(),
                                               matrix.getColumnDimension());
        }
        if (matrix.getRowDimension() != permutation.length) {
            throw new DimensionMismatchException(matrix.getRowDimension(), permutation.length);
        }
        int n = matrix.getRowDimension();
        int m = matrix.getColumnDimension();
        Dfp out[][] = new Dfp[m][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                out[i][j] = matrix.getEntry(permutation[i], j);
            }
        }
        return new Array2DRowFieldMatrix<>(out);
    }
}
