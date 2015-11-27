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

import java.math.BigDecimal;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionConversionException;
import org.apache.commons.math3.fraction.FractionField;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link MatrixUtils} class.
 *
 */

public final class MatrixUtilsTest {

    protected double[][] testData = { {1d,2d,3d}, {2d,5d,3d}, {1d,0d,8d} };
    protected double[][] testData3x3Singular = { { 1, 4, 7, }, { 2, 5, 8, }, { 3, 6, 9, } };
    protected double[][] testData3x4 = { { 12, -51, 4, 1 }, { 6, 167, -68, 2 }, { -4, 24, -41, 3 } };
    protected double[][] nullMatrix = null;
    protected double[] row = {1,2,3};
    protected BigDecimal[] bigRow =
        {new BigDecimal(1),new BigDecimal(2),new BigDecimal(3)};
    protected String[] stringRow = {"1", "2", "3"};
    protected Fraction[] fractionRow =
        {new Fraction(1),new Fraction(2),new Fraction(3)};
    protected double[][] rowMatrix = {{1,2,3}};
    protected BigDecimal[][] bigRowMatrix =
        {{new BigDecimal(1), new BigDecimal(2), new BigDecimal(3)}};
    protected String[][] stringRowMatrix = {{"1", "2", "3"}};
    protected Fraction[][] fractionRowMatrix =
        {{new Fraction(1), new Fraction(2), new Fraction(3)}};
    protected double[] col = {0,4,6};
    protected BigDecimal[] bigCol =
        {new BigDecimal(0),new BigDecimal(4),new BigDecimal(6)};
    protected String[] stringCol = {"0","4","6"};
    protected Fraction[] fractionCol =
        {new Fraction(0),new Fraction(4),new Fraction(6)};
    protected double[] nullDoubleArray = null;
    protected double[][] colMatrix = {{0},{4},{6}};
    protected BigDecimal[][] bigColMatrix =
        {{new BigDecimal(0)},{new BigDecimal(4)},{new BigDecimal(6)}};
    protected String[][] stringColMatrix = {{"0"}, {"4"}, {"6"}};
    protected Fraction[][] fractionColMatrix =
        {{new Fraction(0)},{new Fraction(4)},{new Fraction(6)}};

    @Test
    public void testCreateRealMatrix() {
        Assert.assertEquals(new BlockRealMatrix(testData),
                MatrixUtils.createRealMatrix(testData));
        try {
            MatrixUtils.createRealMatrix(new double[][] {{1}, {1,2}});  // ragged
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRealMatrix(new double[][] {{}, {}});  // no columns
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRealMatrix(null);  // null
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testcreateFieldMatrix() {
        Assert.assertEquals(new Array2DRowFieldMatrix<Fraction>(asFraction(testData)),
                     MatrixUtils.createFieldMatrix(asFraction(testData)));
        Assert.assertEquals(new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(), fractionColMatrix),
                     MatrixUtils.createFieldMatrix(fractionColMatrix));
        try {
            MatrixUtils.createFieldMatrix(asFraction(new double[][] {{1}, {1,2}}));  // ragged
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createFieldMatrix(asFraction(new double[][] {{}, {}}));  // no columns
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createFieldMatrix((Fraction[][])null);  // null
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testCreateRowRealMatrix() {
        Assert.assertEquals(MatrixUtils.createRowRealMatrix(row),
                     new BlockRealMatrix(rowMatrix));
        try {
            MatrixUtils.createRowRealMatrix(new double[] {});  // empty
            Assert.fail("Expecting NotStrictlyPositiveException");
        } catch (NotStrictlyPositiveException ex) {
            // expected
        }
        try {
            MatrixUtils.createRowRealMatrix(null);  // null
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testCreateRowFieldMatrix() {
        Assert.assertEquals(MatrixUtils.createRowFieldMatrix(asFraction(row)),
                     new Array2DRowFieldMatrix<Fraction>(asFraction(rowMatrix)));
        Assert.assertEquals(MatrixUtils.createRowFieldMatrix(fractionRow),
                     new Array2DRowFieldMatrix<Fraction>(fractionRowMatrix));
        try {
            MatrixUtils.createRowFieldMatrix(new Fraction[] {});  // empty
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRowFieldMatrix((Fraction[]) null);  // null
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testCreateColumnRealMatrix() {
        Assert.assertEquals(MatrixUtils.createColumnRealMatrix(col),
                     new BlockRealMatrix(colMatrix));
        try {
            MatrixUtils.createColumnRealMatrix(new double[] {});  // empty
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createColumnRealMatrix(null);  // null
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testCreateColumnFieldMatrix() {
        Assert.assertEquals(MatrixUtils.createColumnFieldMatrix(asFraction(col)),
                     new Array2DRowFieldMatrix<Fraction>(asFraction(colMatrix)));
        Assert.assertEquals(MatrixUtils.createColumnFieldMatrix(fractionCol),
                     new Array2DRowFieldMatrix<Fraction>(fractionColMatrix));

        try {
            MatrixUtils.createColumnFieldMatrix(new Fraction[] {});  // empty
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createColumnFieldMatrix((Fraction[]) null);  // null
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // expected
        }
    }

    /**
     * Verifies that the matrix is an identity matrix
     */
    protected void checkIdentityMatrix(RealMatrix m) {
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j =0; j < m.getColumnDimension(); j++) {
                if (i == j) {
                    Assert.assertEquals(m.getEntry(i, j), 1d, 0);
                } else {
                    Assert.assertEquals(m.getEntry(i, j), 0d, 0);
                }
            }
        }
    }

    @Test
    public void testCreateIdentityMatrix() {
        checkIdentityMatrix(MatrixUtils.createRealIdentityMatrix(3));
        checkIdentityMatrix(MatrixUtils.createRealIdentityMatrix(2));
        checkIdentityMatrix(MatrixUtils.createRealIdentityMatrix(1));
        try {
            MatrixUtils.createRealIdentityMatrix(0);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }

    /**
     * Verifies that the matrix is an identity matrix
     */
    protected void checkIdentityFieldMatrix(FieldMatrix<Fraction> m) {
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j =0; j < m.getColumnDimension(); j++) {
                if (i == j) {
                    Assert.assertEquals(m.getEntry(i, j), Fraction.ONE);
                } else {
                    Assert.assertEquals(m.getEntry(i, j), Fraction.ZERO);
                }
            }
        }
    }

    @Test
    public void testcreateFieldIdentityMatrix() {
        checkIdentityFieldMatrix(MatrixUtils.createFieldIdentityMatrix(FractionField.getInstance(), 3));
        checkIdentityFieldMatrix(MatrixUtils.createFieldIdentityMatrix(FractionField.getInstance(), 2));
        checkIdentityFieldMatrix(MatrixUtils.createFieldIdentityMatrix(FractionField.getInstance(), 1));
        try {
            MatrixUtils.createRealIdentityMatrix(0);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testBigFractionConverter() {
        BigFraction[][] bfData = {
                { new BigFraction(1), new BigFraction(2), new BigFraction(3) },
                { new BigFraction(2), new BigFraction(5), new BigFraction(3) },
                { new BigFraction(1), new BigFraction(0), new BigFraction(8) }
        };
        FieldMatrix<BigFraction> m = new Array2DRowFieldMatrix<BigFraction>(bfData, false);
        RealMatrix converted = MatrixUtils.bigFractionMatrixToRealMatrix(m);
        RealMatrix reference = new Array2DRowRealMatrix(testData, false);
        Assert.assertEquals(0.0, converted.subtract(reference).getNorm(), 0.0);
    }

    @Test
    public void testFractionConverter() {
        Fraction[][] fData = {
                { new Fraction(1), new Fraction(2), new Fraction(3) },
                { new Fraction(2), new Fraction(5), new Fraction(3) },
                { new Fraction(1), new Fraction(0), new Fraction(8) }
        };
        FieldMatrix<Fraction> m = new Array2DRowFieldMatrix<Fraction>(fData, false);
        RealMatrix converted = MatrixUtils.fractionMatrixToRealMatrix(m);
        RealMatrix reference = new Array2DRowRealMatrix(testData, false);
        Assert.assertEquals(0.0, converted.subtract(reference).getNorm(), 0.0);
    }

    public static final Fraction[][] asFraction(double[][] data) {
        Fraction d[][] = new Fraction[data.length][];
        try {
            for (int i = 0; i < data.length; ++i) {
                double[] dataI = data[i];
                Fraction[] dI  = new Fraction[dataI.length];
                for (int j = 0; j < dataI.length; ++j) {
                    dI[j] = new Fraction(dataI[j]);
                }
                d[i] = dI;
            }
        } catch (FractionConversionException fce) {
            Assert.fail(fce.getMessage());
        }
        return d;
    }

    public static final Fraction[] asFraction(double[] data) {
        Fraction d[] = new Fraction[data.length];
        try {
            for (int i = 0; i < data.length; ++i) {
                d[i] = new Fraction(data[i]);
            }
        } catch (FractionConversionException fce) {
            Assert.fail(fce.getMessage());
        }
        return d;
    }

    @Test
    public void testSolveLowerTriangularSystem(){
        RealMatrix rm = new Array2DRowRealMatrix(
                new double[][] { {2,0,0,0 }, { 1,1,0,0 }, { 3,3,3,0 }, { 3,3,3,4 } },
                       false);
        RealVector b = new ArrayRealVector(new double[] { 2,3,4,8 }, false);
        MatrixUtils.solveLowerTriangularSystem(rm, b);
        TestUtils.assertEquals( new double[]{1,2,-1.66666666666667, 1.0}  , b.toArray() , 1.0e-12);
    }


    /*
     * Taken from R manual http://stat.ethz.ch/R-manual/R-patched/library/base/html/backsolve.html
     */
    @Test
    public void testSolveUpperTriangularSystem(){
        RealMatrix rm = new Array2DRowRealMatrix(
                new double[][] { {1,2,3 }, { 0,1,1 }, { 0,0,2 } },
                       false);
        RealVector b = new ArrayRealVector(new double[] { 8,4,2 }, false);
        MatrixUtils.solveUpperTriangularSystem(rm, b);
        TestUtils.assertEquals( new double[]{-1,3,1}  , b.toArray() , 1.0e-12);
    }

    /**
     * This test should probably be replaced by one that could show
     * whether this algorithm can sometimes perform better (precision- or
     * performance-wise) than the direct inversion of the whole matrix.
     */
    @Test
    public void testBlockInverse() {
        final double[][] data = {
            { -1, 0, 123, 4 },
            { -56, 78.9, -0.1, -23.4 },
            { 5.67, 8, -9, 1011 },
            { 12, 345, -67.8, 9 },
        };

        final RealMatrix m = new Array2DRowRealMatrix(data);
        final int len = data.length;
        final double tol = 1e-14;

        for (int splitIndex = 0; splitIndex < 3; splitIndex++) {
            final RealMatrix mInv = MatrixUtils.blockInverse(m, splitIndex);
            final RealMatrix id = m.multiply(mInv);

            // Check that we recovered the identity matrix.
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < len; j++) {
                    final double entry = id.getEntry(i, j);
                    if (i == j) {
                        Assert.assertEquals("[" + i + "][" + j + "]",
                                            1, entry, tol);
                    } else {
                        Assert.assertEquals("[" + i + "][" + j + "]",
                                            0, entry, tol);
                    }
                }
            }
        }
    }

    @Test(expected=SingularMatrixException.class)
    public void testBlockInverseNonInvertible() {
        final double[][] data = {
            { -1, 0, 123, 4 },
            { -56, 78.9, -0.1, -23.4 },
            { 5.67, 8, -9, 1011 },
            { 5.67, 8, -9, 1011 },
        };

        MatrixUtils.blockInverse(new Array2DRowRealMatrix(data), 2);
    }

    @Test
    public void testIsSymmetric() {
        final double eps = Math.ulp(1d);

        final double[][] dataSym = {
            { 1, 2, 3 },
            { 2, 2, 5 },
            { 3, 5, 6 },
        };
        Assert.assertTrue(MatrixUtils.isSymmetric(MatrixUtils.createRealMatrix(dataSym), eps));

        final double[][] dataNonSym = {
            { 1, 2, -3 },
            { 2, 2, 5 },
            { 3, 5, 6 },
        };
        Assert.assertFalse(MatrixUtils.isSymmetric(MatrixUtils.createRealMatrix(dataNonSym), eps));
    }

    @Test
    public void testIsSymmetricTolerance() {
        final double eps = 1e-4;

        final double[][] dataSym1 = {
            { 1,   1, 1.00009 },
            { 1,   1, 1       },
            { 1.0, 1, 1       },
        };
        Assert.assertTrue(MatrixUtils.isSymmetric(MatrixUtils.createRealMatrix(dataSym1), eps));
        final double[][] dataSym2 = {
            { 1,   1, 0.99990 },
            { 1,   1, 1       },
            { 1.0, 1, 1       },
        };
        Assert.assertTrue(MatrixUtils.isSymmetric(MatrixUtils.createRealMatrix(dataSym2), eps));

        final double[][] dataNonSym1 = {
            { 1,   1, 1.00011 },
            { 1,   1, 1       },
            { 1.0, 1, 1       },
        };
        Assert.assertFalse(MatrixUtils.isSymmetric(MatrixUtils.createRealMatrix(dataNonSym1), eps));
        final double[][] dataNonSym2 = {
            { 1,   1, 0.99989 },
            { 1,   1, 1       },
            { 1.0, 1, 1       },
        };
        Assert.assertFalse(MatrixUtils.isSymmetric(MatrixUtils.createRealMatrix(dataNonSym2), eps));
    }

    @Test
    public void testCheckSymmetric1() {
        final double[][] dataSym = {
            { 1, 2, 3 },
            { 2, 2, 5 },
            { 3, 5, 6 },
        };
        MatrixUtils.checkSymmetric(MatrixUtils.createRealMatrix(dataSym), Math.ulp(1d));
    }

    @Test(expected=NonSymmetricMatrixException.class)
    public void testCheckSymmetric2() {
        final double[][] dataNonSym = {
            { 1, 2, -3 },
            { 2, 2, 5 },
            { 3, 5, 6 },
        };
        MatrixUtils.checkSymmetric(MatrixUtils.createRealMatrix(dataNonSym), Math.ulp(1d));
    }

    @Test(expected=SingularMatrixException.class)
    public void testInverseSingular() {
        RealMatrix m = MatrixUtils.createRealMatrix(testData3x3Singular);
        MatrixUtils.inverse(m);
    }

    @Test(expected=NonSquareMatrixException.class)
    public void testInverseNonSquare() {
        RealMatrix m = MatrixUtils.createRealMatrix(testData3x4);
        MatrixUtils.inverse(m);
    }

    @Test
    public void testInverseDiagonalMatrix() {
        final double[] data = { 1, 2, 3 };
        final RealMatrix m = new DiagonalMatrix(data);
        final RealMatrix inverse = MatrixUtils.inverse(m);

        final RealMatrix result = m.multiply(inverse);
        TestUtils.assertEquals("MatrixUtils.inverse() returns wrong result",
                MatrixUtils.createRealIdentityMatrix(data.length), result, Math.ulp(1d));
    }

    @Test
    public void testInverseRealMatrix() {
        RealMatrix m = MatrixUtils.createRealMatrix(testData);
        final RealMatrix inverse = MatrixUtils.inverse(m);

        final RealMatrix result = m.multiply(inverse);
        TestUtils.assertEquals("MatrixUtils.inverse() returns wrong result",
                MatrixUtils.createRealIdentityMatrix(testData.length), result, 1e-12);
    }

}
