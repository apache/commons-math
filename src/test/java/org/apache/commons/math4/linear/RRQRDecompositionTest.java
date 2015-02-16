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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;


public class RRQRDecompositionTest {
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

        Random r = new Random(643895747384642l);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        checkDimension(createTestMatrix(r, p, q));
        checkDimension(createTestMatrix(r, q, p));

    }

    private void checkDimension(RealMatrix m) {
        int rows = m.getRowDimension();
        int columns = m.getColumnDimension();
        RRQRDecomposition qr = new RRQRDecomposition(m);
        Assert.assertEquals(rows,    qr.getQ().getRowDimension());
        Assert.assertEquals(rows,    qr.getQ().getColumnDimension());
        Assert.assertEquals(rows,    qr.getR().getRowDimension());
        Assert.assertEquals(columns, qr.getR().getColumnDimension());
    }

    /** test AP = QR */
    @Test
    public void testAPEqualQR() {
        checkAPEqualQR(MatrixUtils.createRealMatrix(testData3x3NonSingular));

        checkAPEqualQR(MatrixUtils.createRealMatrix(testData3x3Singular));

        checkAPEqualQR(MatrixUtils.createRealMatrix(testData3x4));

        checkAPEqualQR(MatrixUtils.createRealMatrix(testData4x3));

        Random r = new Random(643895747384642l);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        checkAPEqualQR(createTestMatrix(r, p, q));

        checkAPEqualQR(createTestMatrix(r, q, p));

    }

    private void checkAPEqualQR(RealMatrix m) {
        RRQRDecomposition rrqr = new RRQRDecomposition(m);
        double norm = rrqr.getQ().multiply(rrqr.getR()).subtract(m.multiply(rrqr.getP())).getNorm();
        Assert.assertEquals(0, norm, normTolerance);
    }

    /** test the orthogonality of Q */
    @Test
    public void testQOrthogonal() {
        checkQOrthogonal(MatrixUtils.createRealMatrix(testData3x3NonSingular));

        checkQOrthogonal(MatrixUtils.createRealMatrix(testData3x3Singular));

        checkQOrthogonal(MatrixUtils.createRealMatrix(testData3x4));

        checkQOrthogonal(MatrixUtils.createRealMatrix(testData4x3));

        Random r = new Random(643895747384642l);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        checkQOrthogonal(createTestMatrix(r, p, q));

        checkQOrthogonal(createTestMatrix(r, q, p));

    }

    private void checkQOrthogonal(RealMatrix m) {
        RRQRDecomposition qr = new RRQRDecomposition(m);
        RealMatrix eye = MatrixUtils.createRealIdentityMatrix(m.getRowDimension());
        double norm = qr.getQT().multiply(qr.getQ()).subtract(eye).getNorm();
        Assert.assertEquals(0, norm, normTolerance);
    }

    /** test that R is upper triangular */
    @Test
    public void testRUpperTriangular() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData3x3NonSingular);
        checkUpperTriangular(new RRQRDecomposition(matrix).getR());

        matrix = MatrixUtils.createRealMatrix(testData3x3Singular);
        checkUpperTriangular(new RRQRDecomposition(matrix).getR());

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        checkUpperTriangular(new RRQRDecomposition(matrix).getR());

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        checkUpperTriangular(new RRQRDecomposition(matrix).getR());

        Random r = new Random(643895747384642l);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        matrix = createTestMatrix(r, p, q);
        checkUpperTriangular(new RRQRDecomposition(matrix).getR());

        matrix = createTestMatrix(r, p, q);
        checkUpperTriangular(new RRQRDecomposition(matrix).getR());

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
        checkTrapezoidal(new RRQRDecomposition(matrix).getH());

        matrix = MatrixUtils.createRealMatrix(testData3x3Singular);
        checkTrapezoidal(new RRQRDecomposition(matrix).getH());

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        checkTrapezoidal(new RRQRDecomposition(matrix).getH());

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        checkTrapezoidal(new RRQRDecomposition(matrix).getH());

        Random r = new Random(643895747384642l);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        matrix = createTestMatrix(r, p, q);
        checkTrapezoidal(new RRQRDecomposition(matrix).getH());

        matrix = createTestMatrix(r, p, q);
        checkTrapezoidal(new RRQRDecomposition(matrix).getH());

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

    @Test(expected=SingularMatrixException.class)
    public void testNonInvertible() {
        RRQRDecomposition qr =
            new RRQRDecomposition(MatrixUtils.createRealMatrix(testData3x3Singular), 3.0e-16);
        qr.getSolver().getInverse();
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

    /** test the rank is returned correctly */
    @Test
    public void testRank() {
        double[][] d = { { 1, 1, 1 }, { 0, 0, 0 }, { 1, 2, 3 } };
        RealMatrix m = new Array2DRowRealMatrix(d);
        RRQRDecomposition qr = new RRQRDecomposition(m);
        Assert.assertEquals(2, qr.getRank(1.0e-16));
    }

}
