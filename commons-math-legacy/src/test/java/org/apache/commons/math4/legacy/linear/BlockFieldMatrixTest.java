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

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NoDataException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.core.dfp.Dfp;

/**
 * Test cases for the {@link BlockFieldMatrix} class.
 *
 */

public final class BlockFieldMatrixTest {

    // 3 x 3 identity matrix
    protected Dfp[][] id = {
            {Dfp25.of(1),Dfp25.of(0),Dfp25.of(0)},
            {Dfp25.of(0),Dfp25.of(1),Dfp25.of(0)},
            {Dfp25.of(0),Dfp25.of(0),Dfp25.of(1)}
    };

    // Test data for group operations
    protected Dfp[][] testData = {
            {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)},
            {Dfp25.of(2),Dfp25.of(5),Dfp25.of(3)},
            {Dfp25.of(1),Dfp25.of(0),Dfp25.of(8)}
    };
    protected Dfp[][] testDataLU = {
            {Dfp25.of(2), Dfp25.of(5), Dfp25.of(3)},
            {Dfp25.of(1, 2), Dfp25.of(-5, 2), Dfp25.of(13, 2)},
            {Dfp25.of(1, 2), Dfp25.of(1, 5), Dfp25.of(1, 5)}
    };
    protected Dfp[][] testDataPlus2 = {
            {Dfp25.of(3),Dfp25.of(4),Dfp25.of(5)},
            {Dfp25.of(4),Dfp25.of(7),Dfp25.of(5)},
            {Dfp25.of(3),Dfp25.of(2),Dfp25.of(10)}
    };
    protected Dfp[][] testDataMinus = {
            {Dfp25.of(-1),Dfp25.of(-2),Dfp25.of(-3)},
            {Dfp25.of(-2),Dfp25.of(-5),Dfp25.of(-3)},
            {Dfp25.of(-1),Dfp25.of(0),Dfp25.of(-8)}
    };
    protected Dfp[] testDataRow1 = {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)};
    protected Dfp[] testDataCol3 = {Dfp25.of(3),Dfp25.of(3),Dfp25.of(8)};
    protected Dfp[][] testDataInv = {
            {Dfp25.of(-40),Dfp25.of(16),Dfp25.of(9)},
            {Dfp25.of(13),Dfp25.of(-5),Dfp25.of(-3)},
            {Dfp25.of(5),Dfp25.of(-2),Dfp25.of(-1)}
    };
    protected Dfp[] preMultTest = {Dfp25.of(8), Dfp25.of(12), Dfp25.of(33)};
    protected Dfp[][] testData2 = {
            {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)},
            {Dfp25.of(2),Dfp25.of(5),Dfp25.of(3)}
    };
    protected Dfp[][] testData2T = {
            {Dfp25.of(1),Dfp25.of(2)},
            {Dfp25.of(2),Dfp25.of(5)},
            {Dfp25.of(3),Dfp25.of(3)}
    };
    protected Dfp[][] testDataPlusInv = {
            {Dfp25.of(-39),Dfp25.of(18),Dfp25.of(12)},
            {Dfp25.of(15),Dfp25.of(0),Dfp25.of(0)},
            {Dfp25.of(6),Dfp25.of(-2),Dfp25.of(7)}
    };

    // lu decomposition tests
    protected Dfp[][] luData = {
            {Dfp25.of(2),Dfp25.of(3),Dfp25.of(3)},
            {Dfp25.of(0),Dfp25.of(5),Dfp25.of(7)},
            {Dfp25.of(6),Dfp25.of(9),Dfp25.of(8)}
    };
    protected Dfp[][] luDataLUDecomposition = {
            {Dfp25.of(6),Dfp25.of(9),Dfp25.of(8)},
            {Dfp25.of(0),Dfp25.of(5),Dfp25.of(7)},
            {Dfp25.of(1, 3),Dfp25.of(0),Dfp25.of(1, 3)}
    };

    // singular matrices
    protected Dfp[][] singular = { {Dfp25.of(2),Dfp25.of(3)}, {Dfp25.of(2),Dfp25.of(3)} };
    protected Dfp[][] bigSingular = {
            {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3),Dfp25.of(4)},
            {Dfp25.of(2),Dfp25.of(5),Dfp25.of(3),Dfp25.of(4)},
            {Dfp25.of(7),Dfp25.of(3),Dfp25.of(256),Dfp25.of(1930)},
            {Dfp25.of(3),Dfp25.of(7),Dfp25.of(6),Dfp25.of(8)}
    }; // 4th row = 1st + 2nd
    protected Dfp[][] detData = {
            {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)},
            {Dfp25.of(4),Dfp25.of(5),Dfp25.of(6)},
            {Dfp25.of(7),Dfp25.of(8),Dfp25.of(10)}
    };
    protected Dfp[][] detData2 = { {Dfp25.of(1), Dfp25.of(3)}, {Dfp25.of(2), Dfp25.of(4)}};

    // vectors
    protected Dfp[] testVector = {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)};
    protected Dfp[] testVector2 = {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3),Dfp25.of(4)};

    // submatrix accessor tests
    protected Dfp[][] subTestData = {
            {Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4)},
            {Dfp25.of(3, 2), Dfp25.of(5, 2), Dfp25.of(7, 2), Dfp25.of(9, 2)},
            {Dfp25.of(2), Dfp25.of(4), Dfp25.of(6), Dfp25.of(8)},
            {Dfp25.of(4), Dfp25.of(5), Dfp25.of(6), Dfp25.of(7)}
    };
    // array selections
    protected Dfp[][] subRows02Cols13 = { {Dfp25.of(2), Dfp25.of(4)}, {Dfp25.of(4), Dfp25.of(8)}};
    protected Dfp[][] subRows03Cols12 = { {Dfp25.of(2), Dfp25.of(3)}, {Dfp25.of(5), Dfp25.of(6)}};
    protected Dfp[][] subRows03Cols123 = {
            {Dfp25.of(2), Dfp25.of(3), Dfp25.of(4)},
            {Dfp25.of(5), Dfp25.of(6), Dfp25.of(7)}
    };
    // effective permutations
    protected Dfp[][] subRows20Cols123 = {
            {Dfp25.of(4), Dfp25.of(6), Dfp25.of(8)},
            {Dfp25.of(2), Dfp25.of(3), Dfp25.of(4)}
    };
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
        BlockFieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        BlockFieldMatrix<Dfp> m2 = new BlockFieldMatrix<>(testData2);
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
        Random r = new Random(66636328996002L);
        BlockFieldMatrix<Dfp> m1 = createRandomMatrix(r, 47, 83);
        BlockFieldMatrix<Dfp> m2 = new BlockFieldMatrix<>(m1.getData());
        Assert.assertEquals(m1, m2);
        BlockFieldMatrix<Dfp> m3 = new BlockFieldMatrix<>(testData);
        BlockFieldMatrix<Dfp> m4 = new BlockFieldMatrix<>(m3.getData());
        Assert.assertEquals(m3, m4);
    }

    /** test add */
    @Test
    public void testAdd() {
        BlockFieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        BlockFieldMatrix<Dfp> mInv = new BlockFieldMatrix<>(testDataInv);
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
        BlockFieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        BlockFieldMatrix<Dfp> m2 = new BlockFieldMatrix<>(testData2);
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
        BlockFieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        BlockFieldMatrix<Dfp> m2 = new BlockFieldMatrix<>(testDataInv);
        TestUtils.assertEquals(m.subtract(m2), m2.scalarMultiply(Dfp25.of(-1)).add(m));
        try {
            m.subtract(new BlockFieldMatrix<>(testData2));
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    /** test multiply */
    @Test
    public void testMultiply() {
        BlockFieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        BlockFieldMatrix<Dfp> mInv = new BlockFieldMatrix<>(testDataInv);
        BlockFieldMatrix<Dfp> identity = new BlockFieldMatrix<>(id);
        BlockFieldMatrix<Dfp> m2 = new BlockFieldMatrix<>(testData2);
        TestUtils.assertEquals(m.multiply(mInv), identity);
        TestUtils.assertEquals(mInv.multiply(m), identity);
        TestUtils.assertEquals(m.multiply(identity), m);
        TestUtils.assertEquals(identity.multiply(mInv), mInv);
        TestUtils.assertEquals(m2.multiply(identity), m2);
        try {
            m.multiply(new BlockFieldMatrix<>(bigSingular));
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testSeveralBlocks() {
        FieldMatrix<Dfp> m =
            new BlockFieldMatrix<>(Dfp25.getField(), 37, 41);
        for (int i = 0; i < m.getRowDimension(); ++i) {
            for (int j = 0; j < m.getColumnDimension(); ++j) {
                m.setEntry(i, j, Dfp25.of(i * 11 + j, 11));
            }
        }

        FieldMatrix<Dfp> mT = m.transpose();
        Assert.assertEquals(m.getRowDimension(), mT.getColumnDimension());
        Assert.assertEquals(m.getColumnDimension(), mT.getRowDimension());
        for (int i = 0; i < mT.getRowDimension(); ++i) {
            for (int j = 0; j < mT.getColumnDimension(); ++j) {
                Assert.assertEquals(m.getEntry(j, i), mT.getEntry(i, j));
            }
        }

        FieldMatrix<Dfp> mPm = m.add(m);
        for (int i = 0; i < mPm.getRowDimension(); ++i) {
            for (int j = 0; j < mPm.getColumnDimension(); ++j) {
                Assert.assertEquals(m.getEntry(i, j).multiply(Dfp25.of(2)), mPm.getEntry(i, j));
            }
        }

        FieldMatrix<Dfp> mPmMm = mPm.subtract(m);
        for (int i = 0; i < mPmMm.getRowDimension(); ++i) {
            for (int j = 0; j < mPmMm.getColumnDimension(); ++j) {
                Assert.assertEquals(m.getEntry(i, j).toDouble(),
                                    mPmMm.getEntry(i, j).toDouble(),
                                    0d);
            }
        }

        FieldMatrix<Dfp> mTm = mT.multiply(m);
        for (int i = 0; i < mTm.getRowDimension(); ++i) {
            for (int j = 0; j < mTm.getColumnDimension(); ++j) {
                Dfp sum = Dfp25.ZERO;
                for (int k = 0; k < mT.getColumnDimension(); ++k) {
                    sum = sum.add(Dfp25.of(k * 11 + i, 11).multiply(Dfp25.of(k * 11 + j, 11)));
                }
                Assert.assertEquals(sum, mTm.getEntry(i, j));
            }
        }

        FieldMatrix<Dfp> mmT = m.multiply(mT);
        for (int i = 0; i < mmT.getRowDimension(); ++i) {
            for (int j = 0; j < mmT.getColumnDimension(); ++j) {
                Dfp sum = Dfp25.ZERO;
                for (int k = 0; k < m.getColumnDimension(); ++k) {
                    sum = sum.add(Dfp25.of(i * 11 + k, 11).multiply(Dfp25.of(j * 11 + k, 11)));
                }
                Assert.assertEquals(sum.toDouble(),
                                    mmT.getEntry(i, j).toDouble(),
                                    0d);
            }
        }

        FieldMatrix<Dfp> sub1 = m.getSubMatrix(2, 9, 5, 20);
        for (int i = 0; i < sub1.getRowDimension(); ++i) {
            for (int j = 0; j < sub1.getColumnDimension(); ++j) {
                Assert.assertEquals(Dfp25.of((i + 2) * 11 + (j + 5), 11), sub1.getEntry(i, j));
            }
        }

        FieldMatrix<Dfp> sub2 = m.getSubMatrix(10, 12, 3, 40);
        for (int i = 0; i < sub2.getRowDimension(); ++i) {
            for (int j = 0; j < sub2.getColumnDimension(); ++j) {
                Assert.assertEquals(Dfp25.of((i + 10) * 11 + (j + 3), 11), sub2.getEntry(i, j));
            }
        }

        FieldMatrix<Dfp> sub3 = m.getSubMatrix(30, 34, 0, 5);
        for (int i = 0; i < sub3.getRowDimension(); ++i) {
            for (int j = 0; j < sub3.getColumnDimension(); ++j) {
                Assert.assertEquals(Dfp25.of((i + 30) * 11 + (j + 0), 11), sub3.getEntry(i, j));
            }
        }

        FieldMatrix<Dfp> sub4 = m.getSubMatrix(30, 32, 32, 35);
        for (int i = 0; i < sub4.getRowDimension(); ++i) {
            for (int j = 0; j < sub4.getColumnDimension(); ++j) {
                Assert.assertEquals(Dfp25.of((i + 30) * 11 + (j + 32), 11), sub4.getEntry(i, j));
            }
        }

    }

    //Additional Test for BlockFieldMatrix<Dfp>Test.testMultiply

    private Dfp[][] d3 = new Dfp[][] {
            {Dfp25.of(1),Dfp25.of(2),Dfp25.of(3),Dfp25.of(4)},
            {Dfp25.of(5),Dfp25.of(6),Dfp25.of(7),Dfp25.of(8)}
    };
    private Dfp[][] d4 = new Dfp[][] {
            {Dfp25.of(1)},
            {Dfp25.of(2)},
            {Dfp25.of(3)},
            {Dfp25.of(4)}
    };
    private Dfp[][] d5 = new Dfp[][] {{Dfp25.of(30)},{Dfp25.of(70)}};

    @Test
    public void testMultiply2() {
       FieldMatrix<Dfp> m3 = new BlockFieldMatrix<>(d3);
       FieldMatrix<Dfp> m4 = new BlockFieldMatrix<>(d4);
       FieldMatrix<Dfp> m5 = new BlockFieldMatrix<>(d5);
       TestUtils.assertEquals(m3.multiply(m4), m5);
   }

    /** test trace */
    @Test
    public void testTrace() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(id);
        Assert.assertEquals(Dfp25.of(3),m.getTrace());
        m = new BlockFieldMatrix<>(testData2);
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
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        TestUtils.assertEquals(new BlockFieldMatrix<>(testDataPlus2),
                               m.scalarAdd(Dfp25.of(2)));
    }

    /** test operate */
    @Test
    public void testOperate() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(id);
        TestUtils.assertEquals(testVector, m.operate(testVector));
        TestUtils.assertEquals(testVector, m.operate(new ArrayFieldVector<>(testVector)).toArray());
        m = new BlockFieldMatrix<>(bigSingular);
        try {
            m.operate(testVector);
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    @Test
    public void testOperateLarge() {
        int p = (11 * BlockFieldMatrix.BLOCK_SIZE) / 10;
        int q = (11 * BlockFieldMatrix.BLOCK_SIZE) / 10;
        int r =  BlockFieldMatrix.BLOCK_SIZE / 2;
        Random random = new Random(111007463902334L);
        FieldMatrix<Dfp> m1 = createRandomMatrix(random, p, q);
        FieldMatrix<Dfp> m2 = createRandomMatrix(random, q, r);
        FieldMatrix<Dfp> m1m2 = m1.multiply(m2);
        for (int i = 0; i < r; ++i) {
            TestUtils.assertEquals(m1m2.getColumn(i), m1.operate(m2.getColumn(i)));
        }
    }

    @Test
    public void testOperatePremultiplyLarge() {
        int p = (11 * BlockFieldMatrix.BLOCK_SIZE) / 10;
        int q = (11 * BlockFieldMatrix.BLOCK_SIZE) / 10;
        int r =  BlockFieldMatrix.BLOCK_SIZE / 2;
        Random random = new Random(111007463902334L);
        FieldMatrix<Dfp> m1 = createRandomMatrix(random, p, q);
        FieldMatrix<Dfp> m2 = createRandomMatrix(random, q, r);
        FieldMatrix<Dfp> m1m2 = m1.multiply(m2);
        for (int i = 0; i < p; ++i) {
            TestUtils.assertEquals(m1m2.getRow(i), m2.preMultiply(m1.getRow(i)));
        }
    }

    /** test issue MATH-209 */
    @Test
    public void testMath209() {
        FieldMatrix<Dfp> a = new BlockFieldMatrix<>(new Dfp[][] {
                { Dfp25.of(1), Dfp25.of(2) },
                { Dfp25.of(3), Dfp25.of(4) },
                { Dfp25.of(5), Dfp25.of(6) }
        });
        Dfp[] b = a.operate(new Dfp[] { Dfp25.of(1), Dfp25.of(1) });
        Assert.assertEquals(a.getRowDimension(), b.length);
        Assert.assertEquals( Dfp25.of(3), b[0]);
        Assert.assertEquals( Dfp25.of(7), b[1]);
        Assert.assertEquals(Dfp25.of(11), b[2]);
    }

    /** test transpose */
    @Test
    public void testTranspose() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        FieldMatrix<Dfp> mIT = new FieldLUDecomposition<>(m).getSolver().getInverse().transpose();
        FieldMatrix<Dfp> mTI = new FieldLUDecomposition<>(m.transpose()).getSolver().getInverse();
        TestUtils.assertEquals(mIT, mTI);
        m = new BlockFieldMatrix<>(testData2);
        FieldMatrix<Dfp> mt = new BlockFieldMatrix<>(testData2T);
        TestUtils.assertEquals(mt, m.transpose());
    }

    /** test preMultiply by vector */
    @Test
    public void testPremultiplyVector() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        TestUtils.assertEquals(m.preMultiply(testVector), preMultTest);
        TestUtils.assertEquals(m.preMultiply(new ArrayFieldVector<>(testVector).toArray()),
                               preMultTest);
        m = new BlockFieldMatrix<>(bigSingular);
        try {
            m.preMultiply(testVector);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    @Test
    public void testPremultiply() {
        FieldMatrix<Dfp> m3 = new BlockFieldMatrix<>(d3);
        FieldMatrix<Dfp> m4 = new BlockFieldMatrix<>(d4);
        FieldMatrix<Dfp> m5 = new BlockFieldMatrix<>(d5);
        TestUtils.assertEquals(m4.preMultiply(m3), m5);

        BlockFieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        BlockFieldMatrix<Dfp> mInv = new BlockFieldMatrix<>(testDataInv);
        BlockFieldMatrix<Dfp> identity = new BlockFieldMatrix<>(id);
        TestUtils.assertEquals(m.preMultiply(mInv), identity);
        TestUtils.assertEquals(mInv.preMultiply(m), identity);
        TestUtils.assertEquals(m.preMultiply(identity), m);
        TestUtils.assertEquals(identity.preMultiply(mInv), mInv);
        try {
            m.preMultiply(new BlockFieldMatrix<>(bigSingular));
            Assert.fail("Expecting illegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }

    @Test
    public void testGetVectors() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
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
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        Assert.assertEquals(m.getEntry(0,1),Dfp25.of(2));
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
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(matrixData);
        // One more with three rows, two columns
        Dfp[][] matrixData2 = {
                {Dfp25.of(1),Dfp25.of(2)},
                {Dfp25.of(2),Dfp25.of(5)},
                {Dfp25.of(1), Dfp25.of(7)}
        };
        FieldMatrix<Dfp> n = new BlockFieldMatrix<>(matrixData2);
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
        FieldMatrix<Dfp> coefficients = new BlockFieldMatrix<>(coefficientsData);
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
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
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
                Assert.assertEquals(new BlockFieldMatrix<>(reference), sub);
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
                Assert.assertEquals(new BlockFieldMatrix<>(reference), sub);
            } else {
                Assert.fail("Expecting OutOfRangeException");
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
    public void testGetSetMatrixLarge() {
        int n = 3 * BlockFieldMatrix.BLOCK_SIZE;
        FieldMatrix<Dfp> m =
            new BlockFieldMatrix<>(Dfp25.getField(), n, n);
        FieldMatrix<Dfp> sub =
            new BlockFieldMatrix<>(Dfp25.getField(), n - 4, n - 4).scalarAdd(Dfp25.of(1));

        m.setSubMatrix(sub.getData(), 2, 2);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if ((i < 2) || (i > n - 3) || (j < 2) || (j > n - 3)) {
                    Assert.assertEquals(Dfp25.of(0), m.getEntry(i, j));
                } else {
                    Assert.assertEquals(Dfp25.of(1), m.getEntry(i, j));
                }
            }
        }
        Assert.assertEquals(sub, m.getSubMatrix(2, n - 3, 2, n - 3));
    }

    @Test
    public void testCopySubMatrix() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
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
        checkCopy(m, null, new int[] {}, new int[] { 0 });
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
                Assert.assertEquals(new BlockFieldMatrix<>(reference), new BlockFieldMatrix<>(sub));
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
                Assert.assertEquals(new BlockFieldMatrix<>(reference), new BlockFieldMatrix<>(sub));
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
        FieldMatrix<Dfp> m     = new BlockFieldMatrix<>(subTestData);
        FieldMatrix<Dfp> mRow0 = new BlockFieldMatrix<>(subRow0);
        FieldMatrix<Dfp> mRow3 = new BlockFieldMatrix<>(subRow3);
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
    public void testSetRowMatrix() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
        FieldMatrix<Dfp> mRow3 = new BlockFieldMatrix<>(subRow3);
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
    public void testGetSetRowMatrixLarge() {
        int n = 3 * BlockFieldMatrix.BLOCK_SIZE;
        FieldMatrix<Dfp> m =
            new BlockFieldMatrix<>(Dfp25.getField(), n, n);
        FieldMatrix<Dfp> sub =
            new BlockFieldMatrix<>(Dfp25.getField(), 1, n).scalarAdd(Dfp25.of(1));

        m.setRowMatrix(2, sub);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (i != 2) {
                    Assert.assertEquals(Dfp25.of(0), m.getEntry(i, j));
                } else {
                    Assert.assertEquals(Dfp25.of(1), m.getEntry(i, j));
                }
            }
        }
        Assert.assertEquals(sub, m.getRowMatrix(2));

    }

    @Test
    public void testGetColumnMatrix() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
        FieldMatrix<Dfp> mColumn1 = new BlockFieldMatrix<>(subColumn1);
        FieldMatrix<Dfp> mColumn3 = new BlockFieldMatrix<>(subColumn3);
        Assert.assertEquals(mColumn1, m.getColumnMatrix(1));
        Assert.assertEquals(mColumn3, m.getColumnMatrix(3));
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
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
        FieldMatrix<Dfp> mColumn3 = new BlockFieldMatrix<>(subColumn3);
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
    public void testGetSetColumnMatrixLarge() {
        int n = 3 * BlockFieldMatrix.BLOCK_SIZE;
        FieldMatrix<Dfp> m =
            new BlockFieldMatrix<>(Dfp25.getField(), n, n);
        FieldMatrix<Dfp> sub =
            new BlockFieldMatrix<>(Dfp25.getField(), n, 1).scalarAdd(Dfp25.of(1));

        m.setColumnMatrix(2, sub);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (j != 2) {
                    Assert.assertEquals(Dfp25.of(0), m.getEntry(i, j));
                } else {
                    Assert.assertEquals(Dfp25.of(1), m.getEntry(i, j));
                }
            }
        }
        Assert.assertEquals(sub, m.getColumnMatrix(2));

    }

    @Test
    public void testGetRowVector() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
        FieldVector<Dfp> mRow0 = new ArrayFieldVector<>(subRow0[0]);
        FieldVector<Dfp> mRow3 = new ArrayFieldVector<>(subRow3[0]);
        Assert.assertEquals(mRow0, m.getRowVector(0));
        Assert.assertEquals(mRow3, m.getRowVector(3));
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
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
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
    public void testGetSetRowVectorLarge() {
        int n = 3 * BlockFieldMatrix.BLOCK_SIZE;
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(Dfp25.getField(), n, n);
        FieldVector<Dfp> sub = new ArrayFieldVector<>(n, Dfp25.of(1));

        m.setRowVector(2, sub);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (i != 2) {
                    Assert.assertEquals(Dfp25.of(0), m.getEntry(i, j));
                } else {
                    Assert.assertEquals(Dfp25.of(1), m.getEntry(i, j));
                }
            }
        }
        Assert.assertEquals(sub, m.getRowVector(2));

    }

    @Test
    public void testGetColumnVector() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
        FieldVector<Dfp> mColumn1 = columnToVector(subColumn1);
        FieldVector<Dfp> mColumn3 = columnToVector(subColumn3);
        Assert.assertEquals(mColumn1, m.getColumnVector(1));
        Assert.assertEquals(mColumn3, m.getColumnVector(3));
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
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
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

    @Test
    public void testGetSetColumnVectorLarge() {
        int n = 3 * BlockFieldMatrix.BLOCK_SIZE;
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(Dfp25.getField(), n, n);
        FieldVector<Dfp> sub = new ArrayFieldVector<>(n, Dfp25.of(1));

        m.setColumnVector(2, sub);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (j != 2) {
                    Assert.assertEquals(Dfp25.of(0), m.getEntry(i, j));
                } else {
                    Assert.assertEquals(Dfp25.of(1), m.getEntry(i, j));
                }
            }
        }
        Assert.assertEquals(sub, m.getColumnVector(2));

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
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
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
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
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
    public void testGetSetRowLarge() {
        int n = 3 * BlockFieldMatrix.BLOCK_SIZE;
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(Dfp25.getField(), n, n);
        Dfp[] sub = new Dfp[n];
        Arrays.fill(sub, Dfp25.of(1));

        m.setRow(2, sub);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (i != 2) {
                    Assert.assertEquals(Dfp25.of(0), m.getEntry(i, j));
                } else {
                    Assert.assertEquals(Dfp25.of(1), m.getEntry(i, j));
                }
            }
        }
        checkArrays(sub, m.getRow(2));

    }

    @Test
    public void testGetColumn() {
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
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
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(subTestData);
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

    @Test
    public void testGetSetColumnLarge() {
        int n = 3 * BlockFieldMatrix.BLOCK_SIZE;
        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(Dfp25.getField(), n, n);
        Dfp[] sub = new Dfp[n];
        Arrays.fill(sub, Dfp25.of(1));

        m.setColumn(2, sub);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (j != 2) {
                    Assert.assertEquals(Dfp25.of(0), m.getEntry(i, j));
                } else {
                    Assert.assertEquals(Dfp25.of(1), m.getEntry(i, j));
                }
            }
        }
        checkArrays(sub, m.getColumn(2));

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
        BlockFieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        BlockFieldMatrix<Dfp> m1 = (BlockFieldMatrix<Dfp>) m.copy();
        BlockFieldMatrix<Dfp> mt = (BlockFieldMatrix<Dfp>) m.transpose();
        Assert.assertTrue(m.hashCode() != mt.hashCode());
        Assert.assertEquals(m.hashCode(), m1.hashCode());
        Assert.assertEquals(m, m);
        Assert.assertEquals(m, m1);
        Assert.assertNotEquals(m, null);
        Assert.assertNotEquals(m, mt);
        Assert.assertNotEquals(m, new BlockFieldMatrix<>(bigSingular));
    }

    @Test
    public void testToString() {
        BlockFieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        Assert.assertEquals("BlockFieldMatrix{{1.,2.,3.},{2.,5.,3.},{1.,0.,8.}}", m.toString());
    }

    @Test
    public void testSetSubMatrix() {
        BlockFieldMatrix<Dfp> m = new BlockFieldMatrix<>(testData);
        m.setSubMatrix(detData2,1,1);
        FieldMatrix<Dfp> expected = new BlockFieldMatrix<>
            (new Dfp[][] {{Dfp25.of(1),Dfp25.of(2),Dfp25.of(3)},{Dfp25.of(2),Dfp25.of(1),Dfp25.of(3)},{Dfp25.of(1),Dfp25.of(2),Dfp25.of(4)}});
        Assert.assertEquals(expected, m);

        m.setSubMatrix(detData2,0,0);
        expected = new BlockFieldMatrix<>
            (new Dfp[][] {{Dfp25.of(1),Dfp25.of(3),Dfp25.of(3)},{Dfp25.of(2),Dfp25.of(4),Dfp25.of(3)},{Dfp25.of(1),Dfp25.of(2),Dfp25.of(4)}});
        Assert.assertEquals(expected, m);

        m.setSubMatrix(testDataPlus2,0,0);
        expected = new BlockFieldMatrix<>
            (new Dfp[][] {{Dfp25.of(3),Dfp25.of(4),Dfp25.of(5)},{Dfp25.of(4),Dfp25.of(7),Dfp25.of(5)},{Dfp25.of(3),Dfp25.of(2),Dfp25.of(10)}});
        Assert.assertEquals(expected, m);

        // javadoc example
        BlockFieldMatrix<Dfp> matrix =
            new BlockFieldMatrix<>(new Dfp[][] {
                    {Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4)},
                    {Dfp25.of(5), Dfp25.of(6), Dfp25.of(7), Dfp25.of(8)},
                    {Dfp25.of(9), Dfp25.of(0), Dfp25.of(1) , Dfp25.of(2)}
            });
        matrix.setSubMatrix(new Dfp[][] {
                {Dfp25.of(3), Dfp25.of(4)},
                {Dfp25.of(5), Dfp25.of(6)}
        }, 1, 1);
        expected =
            new BlockFieldMatrix<>(new Dfp[][] {
                    {Dfp25.of(1), Dfp25.of(2), Dfp25.of(3),Dfp25.of(4)},
                    {Dfp25.of(5), Dfp25.of(3), Dfp25.of(4), Dfp25.of(8)},
                    {Dfp25.of(9), Dfp25.of(5) ,Dfp25.of(6), Dfp25.of(2)}
            });
        Assert.assertEquals(expected, matrix);

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
            m.setSubMatrix(null,1,1);
            Assert.fail("expecting NullArgumentException");
        } catch (NullArgumentException e) {
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

        FieldMatrix<Dfp> m = new BlockFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInRowOrder(new SetVisitor());
        GetVisitor getVisitor = new GetVisitor();
        m.walkInOptimizedOrder(getVisitor);
        Assert.assertEquals(rows * columns, getVisitor.getCount());

        m = new BlockFieldMatrix<>(Dfp25.getField(), rows, columns);
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

        m = new BlockFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInColumnOrder(new SetVisitor());
        getVisitor = new GetVisitor();
        m.walkInOptimizedOrder(getVisitor);
        Assert.assertEquals(rows * columns, getVisitor.getCount());

        m = new BlockFieldMatrix<>(Dfp25.getField(), rows, columns);
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

        m = new BlockFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInOptimizedOrder(new SetVisitor());
        getVisitor = new GetVisitor();
        m.walkInRowOrder(getVisitor);
        Assert.assertEquals(rows * columns, getVisitor.getCount());

        m = new BlockFieldMatrix<>(Dfp25.getField(), rows, columns);
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

        m = new BlockFieldMatrix<>(Dfp25.getField(), rows, columns);
        m.walkInOptimizedOrder(new SetVisitor());
        getVisitor = new GetVisitor();
        m.walkInColumnOrder(getVisitor);
        Assert.assertEquals(rows * columns, getVisitor.getCount());

        m = new BlockFieldMatrix<>(Dfp25.getField(), rows, columns);
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
        BlockFieldMatrix<BigReal> m = new BlockFieldMatrix<>(BigRealField.getInstance(), r, c);
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
            return Dfp25.of(i * 11 + j, 11);
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
            Assert.assertEquals(Dfp25.of(i * 11 + j, 11), value);
        }
        public int getCount() {
            return count;
        }
    }

    private BlockFieldMatrix<Dfp> createRandomMatrix(Random r, int rows, int columns) {
        BlockFieldMatrix<Dfp> m =
            new BlockFieldMatrix<>(Dfp25.getField(), rows, columns);
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                int p = r.nextInt(20) - 10;
                int q = r.nextInt(20) - 10;
                if (q == 0) {
                    q = 1;
                }
                m.setEntry(i, j, Dfp25.of(p, q));
            }
        }
        return m;
    }
}

