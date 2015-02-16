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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;


public class SingularValueDecompositionTest {

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

    private static final double normTolerance = 10e-14;

    @Test
    public void testMoreRows() {
        final double[] singularValues = { 123.456, 2.3, 1.001, 0.999 };
        final int rows    = singularValues.length + 2;
        final int columns = singularValues.length;
        Random r = new Random(15338437322523l);
        SingularValueDecomposition svd =
            new SingularValueDecomposition(createTestMatrix(r, rows, columns, singularValues));
        double[] computedSV = svd.getSingularValues();
        Assert.assertEquals(singularValues.length, computedSV.length);
        for (int i = 0; i < singularValues.length; ++i) {
            Assert.assertEquals(singularValues[i], computedSV[i], 1.0e-10);
        }
    }

    @Test
    public void testMoreColumns() {
        final double[] singularValues = { 123.456, 2.3, 1.001, 0.999 };
        final int rows    = singularValues.length;
        final int columns = singularValues.length + 2;
        Random r = new Random(732763225836210l);
        SingularValueDecomposition svd =
            new SingularValueDecomposition(createTestMatrix(r, rows, columns, singularValues));
        double[] computedSV = svd.getSingularValues();
        Assert.assertEquals(singularValues.length, computedSV.length);
        for (int i = 0; i < singularValues.length; ++i) {
            Assert.assertEquals(singularValues[i], computedSV[i], 1.0e-10);
        }
    }

    /** test dimensions */
    @Test
    public void testDimensions() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testSquare);
        final int m = matrix.getRowDimension();
        final int n = matrix.getColumnDimension();
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
        Assert.assertEquals(m, svd.getU().getRowDimension());
        Assert.assertEquals(m, svd.getU().getColumnDimension());
        Assert.assertEquals(m, svd.getS().getColumnDimension());
        Assert.assertEquals(n, svd.getS().getColumnDimension());
        Assert.assertEquals(n, svd.getV().getRowDimension());
        Assert.assertEquals(n, svd.getV().getColumnDimension());

    }

    /** Test based on a dimension 4 Hadamard matrix. */
    @Test
    public void testHadamard() {
        RealMatrix matrix = new Array2DRowRealMatrix(new double[][] {
                {15.0 / 2.0,  5.0 / 2.0,  9.0 / 2.0,  3.0 / 2.0 },
                { 5.0 / 2.0, 15.0 / 2.0,  3.0 / 2.0,  9.0 / 2.0 },
                { 9.0 / 2.0,  3.0 / 2.0, 15.0 / 2.0,  5.0 / 2.0 },
                { 3.0 / 2.0,  9.0 / 2.0,  5.0 / 2.0, 15.0 / 2.0 }
        }, false);
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
        Assert.assertEquals(16.0, svd.getSingularValues()[0], 1.0e-14);
        Assert.assertEquals( 8.0, svd.getSingularValues()[1], 1.0e-14);
        Assert.assertEquals( 4.0, svd.getSingularValues()[2], 1.0e-14);
        Assert.assertEquals( 2.0, svd.getSingularValues()[3], 1.0e-14);

        RealMatrix fullCovariance = new Array2DRowRealMatrix(new double[][] {
                {  85.0 / 1024, -51.0 / 1024, -75.0 / 1024,  45.0 / 1024 },
                { -51.0 / 1024,  85.0 / 1024,  45.0 / 1024, -75.0 / 1024 },
                { -75.0 / 1024,  45.0 / 1024,  85.0 / 1024, -51.0 / 1024 },
                {  45.0 / 1024, -75.0 / 1024, -51.0 / 1024,  85.0 / 1024 }
        }, false);
        Assert.assertEquals(0.0,
                     fullCovariance.subtract(svd.getCovariance(0.0)).getNorm(),
                     1.0e-14);

        RealMatrix halfCovariance = new Array2DRowRealMatrix(new double[][] {
                {   5.0 / 1024,  -3.0 / 1024,   5.0 / 1024,  -3.0 / 1024 },
                {  -3.0 / 1024,   5.0 / 1024,  -3.0 / 1024,   5.0 / 1024 },
                {   5.0 / 1024,  -3.0 / 1024,   5.0 / 1024,  -3.0 / 1024 },
                {  -3.0 / 1024,   5.0 / 1024,  -3.0 / 1024,   5.0 / 1024 }
        }, false);
        Assert.assertEquals(0.0,
                     halfCovariance.subtract(svd.getCovariance(6.0)).getNorm(),
                     1.0e-14);

    }

    /** test A = USVt */
    @Test
    public void testAEqualUSVt() {
        checkAEqualUSVt(MatrixUtils.createRealMatrix(testSquare));
        checkAEqualUSVt(MatrixUtils.createRealMatrix(testNonSquare));
        checkAEqualUSVt(MatrixUtils.createRealMatrix(testNonSquare).transpose());
    }

    public void checkAEqualUSVt(final RealMatrix matrix) {
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
        RealMatrix u = svd.getU();
        RealMatrix s = svd.getS();
        RealMatrix v = svd.getV();
        double norm = u.multiply(s).multiply(v.transpose()).subtract(matrix).getNorm();
        Assert.assertEquals(0, norm, normTolerance);

    }

    /** test that U is orthogonal */
    @Test
    public void testUOrthogonal() {
        checkOrthogonal(new SingularValueDecomposition(MatrixUtils.createRealMatrix(testSquare)).getU());
        checkOrthogonal(new SingularValueDecomposition(MatrixUtils.createRealMatrix(testNonSquare)).getU());
        checkOrthogonal(new SingularValueDecomposition(MatrixUtils.createRealMatrix(testNonSquare).transpose()).getU());
    }

    /** test that V is orthogonal */
    @Test
    public void testVOrthogonal() {
        checkOrthogonal(new SingularValueDecomposition(MatrixUtils.createRealMatrix(testSquare)).getV());
        checkOrthogonal(new SingularValueDecomposition(MatrixUtils.createRealMatrix(testNonSquare)).getV());
        checkOrthogonal(new SingularValueDecomposition(MatrixUtils.createRealMatrix(testNonSquare).transpose()).getV());
    }

    public void checkOrthogonal(final RealMatrix m) {
        RealMatrix mTm = m.transpose().multiply(m);
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(mTm.getRowDimension());
        Assert.assertEquals(0, mTm.subtract(id).getNorm(), normTolerance);
    }

    /** test matrices values */
    // This test is useless since whereas the columns of U and V are linked
    // together, the actual triplet (U,S,V) is not uniquely defined.
    public void testMatricesValues1() {
       SingularValueDecomposition svd =
            new SingularValueDecomposition(MatrixUtils.createRealMatrix(testSquare));
        RealMatrix uRef = MatrixUtils.createRealMatrix(new double[][] {
                { 3.0 / 5.0, -4.0 / 5.0 },
                { 4.0 / 5.0,  3.0 / 5.0 }
        });
        RealMatrix sRef = MatrixUtils.createRealMatrix(new double[][] {
                { 3.0, 0.0 },
                { 0.0, 1.0 }
        });
        RealMatrix vRef = MatrixUtils.createRealMatrix(new double[][] {
                { 4.0 / 5.0,  3.0 / 5.0 },
                { 3.0 / 5.0, -4.0 / 5.0 }
        });

        // check values against known references
        RealMatrix u = svd.getU();
        Assert.assertEquals(0, u.subtract(uRef).getNorm(), normTolerance);
        RealMatrix s = svd.getS();
        Assert.assertEquals(0, s.subtract(sRef).getNorm(), normTolerance);
        RealMatrix v = svd.getV();
        Assert.assertEquals(0, v.subtract(vRef).getNorm(), normTolerance);

        // check the same cached instance is returned the second time
        Assert.assertTrue(u == svd.getU());
        Assert.assertTrue(s == svd.getS());
        Assert.assertTrue(v == svd.getV());

    }

    /** test matrices values */
    // This test is useless since whereas the columns of U and V are linked
    // together, the actual triplet (U,S,V) is not uniquely defined.
    public void useless_testMatricesValues2() {

        RealMatrix uRef = MatrixUtils.createRealMatrix(new double[][] {
            {  0.0 / 5.0,  3.0 / 5.0,  0.0 / 5.0 },
            { -4.0 / 5.0,  0.0 / 5.0, -3.0 / 5.0 },
            {  0.0 / 5.0,  4.0 / 5.0,  0.0 / 5.0 },
            { -3.0 / 5.0,  0.0 / 5.0,  4.0 / 5.0 }
        });
        RealMatrix sRef = MatrixUtils.createRealMatrix(new double[][] {
            { 4.0, 0.0, 0.0 },
            { 0.0, 3.0, 0.0 },
            { 0.0, 0.0, 2.0 }
        });
        RealMatrix vRef = MatrixUtils.createRealMatrix(new double[][] {
            {  80.0 / 125.0,  -60.0 / 125.0, 75.0 / 125.0 },
            {  24.0 / 125.0,  107.0 / 125.0, 60.0 / 125.0 },
            { -93.0 / 125.0,  -24.0 / 125.0, 80.0 / 125.0 }
        });

        // check values against known references
        SingularValueDecomposition svd =
            new SingularValueDecomposition(MatrixUtils.createRealMatrix(testNonSquare));
        RealMatrix u = svd.getU();
        Assert.assertEquals(0, u.subtract(uRef).getNorm(), normTolerance);
        RealMatrix s = svd.getS();
        Assert.assertEquals(0, s.subtract(sRef).getNorm(), normTolerance);
        RealMatrix v = svd.getV();
        Assert.assertEquals(0, v.subtract(vRef).getNorm(), normTolerance);

        // check the same cached instance is returned the second time
        Assert.assertTrue(u == svd.getU());
        Assert.assertTrue(s == svd.getS());
        Assert.assertTrue(v == svd.getV());

    }

     /** test MATH-465 */
    @Test
    public void testRank() {
        double[][] d = { { 1, 1, 1 }, { 0, 0, 0 }, { 1, 2, 3 } };
        RealMatrix m = new Array2DRowRealMatrix(d);
        SingularValueDecomposition svd = new SingularValueDecomposition(m);
        Assert.assertEquals(2, svd.getRank());
    }

    /** test MATH-583 */
    @Test
    public void testStability1() {
        RealMatrix m = new Array2DRowRealMatrix(201, 201);
        loadRealMatrix(m,"matrix1.csv");
        try {
            new SingularValueDecomposition(m);
        } catch (Exception e) {
            Assert.fail("Exception whilst constructing SVD");
        }
    }

    /** test MATH-327 */
    @Test
    public void testStability2() {
        RealMatrix m = new Array2DRowRealMatrix(7, 168);
        loadRealMatrix(m,"matrix2.csv");
        try {
            new SingularValueDecomposition(m);
        } catch (Throwable e) {
            Assert.fail("Exception whilst constructing SVD");
        }
    }

    private void loadRealMatrix(RealMatrix m, String resourceName) {
        try {
            DataInputStream in = new DataInputStream(getClass().getResourceAsStream(resourceName));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int row = 0;
            while ((strLine = br.readLine()) != null) {
                if (!strLine.startsWith("#")) {
                    int col = 0;
                    for (String entry : strLine.split(",")) {
                        m.setEntry(row, col++, Double.parseDouble(entry));
                    }
                    row++;
                }
            }
            in.close();
        } catch (IOException e) {}
    }

    /** test condition number */
    @Test
    public void testConditionNumber() {
        SingularValueDecomposition svd =
            new SingularValueDecomposition(MatrixUtils.createRealMatrix(testSquare));
        // replace 1.0e-15 with 1.5e-15
        Assert.assertEquals(3.0, svd.getConditionNumber(), 1.5e-15);
    }

    @Test
    public void testInverseConditionNumber() {
        SingularValueDecomposition svd =
            new SingularValueDecomposition(MatrixUtils.createRealMatrix(testSquare));
        Assert.assertEquals(1.0/3.0, svd.getInverseConditionNumber(), 1.5e-15);
    }

    private RealMatrix createTestMatrix(final Random r, final int rows, final int columns,
                                        final double[] singularValues) {
        final RealMatrix u = EigenDecompositionTest.createOrthogonalMatrix(r, rows);
        final RealMatrix d = new Array2DRowRealMatrix(rows, columns);
        d.setSubMatrix(MatrixUtils.createRealDiagonalMatrix(singularValues).getData(), 0, 0);
        final RealMatrix v = EigenDecompositionTest.createOrthogonalMatrix(r, columns);
        return u.multiply(d).multiply(v);
    }

    @Test
    public void testIssue947() {
        double[][] nans = new double[][] {
            { Double.NaN, Double.NaN },
            { Double.NaN, Double.NaN }
        };
        RealMatrix m = new Array2DRowRealMatrix(nans, false);
        SingularValueDecomposition svd = new SingularValueDecomposition(m);
        Assert.assertTrue(Double.isNaN(svd.getSingularValues()[0]));
        Assert.assertTrue(Double.isNaN(svd.getSingularValues()[1]));
    }

}
