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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;


public class QRDecompositionTest {
    private double[][] testData3x3NonSingular = {
            { 12, -51, 4 },
            { 6, 167, -68 },
            { -4, 24, -41 }, };

    private double[][] testData3x3Singular = {
            { 1, 4, 7, },
            { 2, 5, 8, },
            { 3, 6, 9, }, };

    private double[][] testData3x4 = {
            { 12, -51, 4, 1 },
            { 6, 167, -68, 2 },
            { -4, 24, -41, 3 }, };

    private double[][] testData4x3 = {
            { 12, -51, 4, },
            { 6, 167, -68, },
            { -4, 24, -41, },
            { -5, 34, 7, }, };

    private static final double entryTolerance = 10e-16;

    private static final double normTolerance = 10e-14;

    /** test dimensions */
    @Test
    public void testDimensions() {
        checkDimension(MatrixUtils.createRealMatrix(testData3x3NonSingular));

        checkDimension(MatrixUtils.createRealMatrix(testData4x3));

        checkDimension(MatrixUtils.createRealMatrix(testData3x4));

        Random r = new Random(643895747384642L);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        checkDimension(createTestMatrix(r, p, q));
        checkDimension(createTestMatrix(r, q, p));

    }

    private void checkDimension(RealMatrix m) {
        int rows = m.getRowDimension();
        int columns = m.getColumnDimension();
        QRDecomposition qr = new QRDecomposition(m);
        Assert.assertEquals(rows,    qr.getQ().getRowDimension());
        Assert.assertEquals(rows,    qr.getQ().getColumnDimension());
        Assert.assertEquals(rows,    qr.getR().getRowDimension());
        Assert.assertEquals(columns, qr.getR().getColumnDimension());
    }

    /** test A = QR */
    @Test
    public void testAEqualQR() {
        checkAEqualQR(MatrixUtils.createRealMatrix(testData3x3NonSingular));

        checkAEqualQR(MatrixUtils.createRealMatrix(testData3x3Singular));

        checkAEqualQR(MatrixUtils.createRealMatrix(testData3x4));

        checkAEqualQR(MatrixUtils.createRealMatrix(testData4x3));

        Random r = new Random(643895747384642L);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        checkAEqualQR(createTestMatrix(r, p, q));

        checkAEqualQR(createTestMatrix(r, q, p));

    }

    private void checkAEqualQR(RealMatrix m) {
        QRDecomposition qr = new QRDecomposition(m);
        double norm = qr.getQ().multiply(qr.getR()).subtract(m).getNorm();
        Assert.assertEquals(0, norm, normTolerance);
    }

    /** test the orthogonality of Q */
    @Test
    public void testQOrthogonal() {
        checkQOrthogonal(MatrixUtils.createRealMatrix(testData3x3NonSingular));

        checkQOrthogonal(MatrixUtils.createRealMatrix(testData3x3Singular));

        checkQOrthogonal(MatrixUtils.createRealMatrix(testData3x4));

        checkQOrthogonal(MatrixUtils.createRealMatrix(testData4x3));

        Random r = new Random(643895747384642L);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        checkQOrthogonal(createTestMatrix(r, p, q));

        checkQOrthogonal(createTestMatrix(r, q, p));

    }

    private void checkQOrthogonal(RealMatrix m) {
        QRDecomposition qr = new QRDecomposition(m);
        RealMatrix eye = MatrixUtils.createRealIdentityMatrix(m.getRowDimension());
        double norm = qr.getQT().multiply(qr.getQ()).subtract(eye).getNorm();
        Assert.assertEquals(0, norm, normTolerance);
    }

    /** test that R is upper triangular */
    @Test
    public void testRUpperTriangular() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData3x3NonSingular);
        checkUpperTriangular(new QRDecomposition(matrix).getR());

        matrix = MatrixUtils.createRealMatrix(testData3x3Singular);
        checkUpperTriangular(new QRDecomposition(matrix).getR());

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        checkUpperTriangular(new QRDecomposition(matrix).getR());

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        checkUpperTriangular(new QRDecomposition(matrix).getR());

        Random r = new Random(643895747384642L);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        matrix = createTestMatrix(r, p, q);
        checkUpperTriangular(new QRDecomposition(matrix).getR());

        matrix = createTestMatrix(r, p, q);
        checkUpperTriangular(new QRDecomposition(matrix).getR());

    }

    private void checkUpperTriangular(RealMatrix m) {
        m.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            @Override
            public void visit(int row, int column, double value) {
                if (column < row) {
                    Assert.assertEquals(0.0, value, entryTolerance);
                }
            }
        });
    }

    /** test that H is trapezoidal */
    @Test
    public void testHTrapezoidal() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData3x3NonSingular);
        checkTrapezoidal(new QRDecomposition(matrix).getH());

        matrix = MatrixUtils.createRealMatrix(testData3x3Singular);
        checkTrapezoidal(new QRDecomposition(matrix).getH());

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        checkTrapezoidal(new QRDecomposition(matrix).getH());

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        checkTrapezoidal(new QRDecomposition(matrix).getH());

        Random r = new Random(643895747384642L);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        matrix = createTestMatrix(r, p, q);
        checkTrapezoidal(new QRDecomposition(matrix).getH());

        matrix = createTestMatrix(r, p, q);
        checkTrapezoidal(new QRDecomposition(matrix).getH());

    }

    private void checkTrapezoidal(RealMatrix m) {
        m.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            @Override
            public void visit(int row, int column, double value) {
                if (column > row) {
                    Assert.assertEquals(0.0, value, entryTolerance);
                }
            }
        });
    }
    /** test matrices values */
    @Test
    public void testMatricesValues() {
        QRDecomposition qr =
            new QRDecomposition(MatrixUtils.createRealMatrix(testData3x3NonSingular));
        RealMatrix qRef = MatrixUtils.createRealMatrix(new double[][] {
                { -12.0 / 14.0,   69.0 / 175.0,  -58.0 / 175.0 },
                {  -6.0 / 14.0, -158.0 / 175.0,    6.0 / 175.0 },
                {   4.0 / 14.0,  -30.0 / 175.0, -165.0 / 175.0 }
        });
        RealMatrix rRef = MatrixUtils.createRealMatrix(new double[][] {
                { -14.0,  -21.0, 14.0 },
                {   0.0, -175.0, 70.0 },
                {   0.0,    0.0, 35.0 }
        });
        RealMatrix hRef = MatrixUtils.createRealMatrix(new double[][] {
                { 26.0 / 14.0, 0.0, 0.0 },
                {  6.0 / 14.0, 648.0 / 325.0, 0.0 },
                { -4.0 / 14.0,  36.0 / 325.0, 2.0 }
        });

        // check values against known references
        RealMatrix q = qr.getQ();
        Assert.assertEquals(0, q.subtract(qRef).getNorm(), 1.0e-13);
        RealMatrix qT = qr.getQT();
        Assert.assertEquals(0, qT.subtract(qRef.transpose()).getNorm(), 1.0e-13);
        RealMatrix r = qr.getR();
        Assert.assertEquals(0, r.subtract(rRef).getNorm(), 1.0e-13);
        RealMatrix h = qr.getH();
        Assert.assertEquals(0, h.subtract(hRef).getNorm(), 1.0e-13);

        // check the same cached instance is returned the second time
        Assert.assertSame(q, qr.getQ());
        Assert.assertSame(r, qr.getR());
        Assert.assertSame(h, qr.getH());

    }

    @Test(expected=SingularMatrixException.class)
    public void testNonInvertible() {
        QRDecomposition qr =
            new QRDecomposition(MatrixUtils.createRealMatrix(testData3x3Singular));
        qr.getSolver().getInverse();
    }

    @Test
    public void testInvertTallSkinny() {
        RealMatrix a     = MatrixUtils.createRealMatrix(testData4x3);
        RealMatrix pinv  = new QRDecomposition(a).getSolver().getInverse();
        Assert.assertEquals(0, pinv.multiply(a).subtract(MatrixUtils.createRealIdentityMatrix(3)).getNorm(), 1.0e-6);
    }

    @Test
    public void testInvertShortWide() {
        RealMatrix a = MatrixUtils.createRealMatrix(testData3x4);
        RealMatrix pinv  = new QRDecomposition(a).getSolver().getInverse();
        Assert.assertEquals(0, a.multiply(pinv).subtract(MatrixUtils.createRealIdentityMatrix(3)).getNorm(), 1.0e-6);
        Assert.assertEquals(0, pinv.multiply(a).getSubMatrix(0, 2, 0, 2).subtract(MatrixUtils.createRealIdentityMatrix(3)).getNorm(), 1.0e-6);
    }

    private RealMatrix createTestMatrix(final Random r, final int rows, final int columns) {
        RealMatrix m = MatrixUtils.createRealMatrix(rows, columns);
        m.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor(){
            @Override
            public double visit(int row, int column, double value) {
                return 2.0 * r.nextDouble() - 1.0;
            }
        });
        return m;
    }

    @Test(expected=SingularMatrixException.class)
    public void testQRSingular() {
        final RealMatrix a = MatrixUtils.createRealMatrix(new double[][] {
            { 1, 6, 4 }, { 2, 4, -1 }, { -1, 2, 5 }
        });
        final RealVector b = new ArrayRealVector(new double[]{ 5, 6, 1 });
        new QRDecomposition(a, 1.0e-15).getSolver().solve(b);
    }

}
