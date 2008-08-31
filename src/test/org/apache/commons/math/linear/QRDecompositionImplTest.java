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

package org.apache.commons.math.linear;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class QRDecompositionImplTest extends TestCase {
    double[][] testData3x3NonSingular = { 
            { 12, -51, 4 }, 
            { 6, 167, -68 },
            { -4, 24, -41 }, };

    double[][] testData3x3Singular = { 
            { 1, 4, 7, }, 
            { 2, 5, 8, },
            { 3, 6, 9, }, };

    double[][] testData3x4 = { 
            { 12, -51, 4, 1 }, 
            { 6, 167, -68, 2 },
            { -4, 24, -41, 3 }, };

    double[][] testData4x3 = { 
            { 12, -51, 4, }, 
            { 6, 167, -68, },
            { -4, 24, -41, }, 
            { -5, 34, 7, }, };

    private static final double entryTolerance = 10e-16;

    private static final double normTolerance = 10e-14;

    public QRDecompositionImplTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(QRDecompositionImplTest.class);
        suite.setName("QRDecompositionImpl Tests");
        return suite;
    }

    /** test dimensions */
    public void testDimensions() {
        RealMatrixImpl matrix = new RealMatrixImpl(testData3x3NonSingular, false);
        QRDecomposition qr = new QRDecompositionImpl(matrix);
        assertEquals("3x3 Q size", qr.getQ().getRowDimension(), 3);
        assertEquals("3x3 Q size", qr.getQ().getColumnDimension(), 3);
        assertEquals("3x3 R size", qr.getR().getRowDimension(), 3);
        assertEquals("3x3 R size", qr.getR().getColumnDimension(), 3);

        matrix = new RealMatrixImpl(testData4x3, false);
        qr = new QRDecompositionImpl(matrix);
        assertEquals("4x3 Q size", qr.getQ().getRowDimension(), 4);
        assertEquals("4x3 Q size", qr.getQ().getColumnDimension(), 4);
        assertEquals("4x3 R size", qr.getR().getRowDimension(), 4);
        assertEquals("4x3 R size", qr.getR().getColumnDimension(), 3);

        matrix = new RealMatrixImpl(testData3x4, false);
        qr = new QRDecompositionImpl(matrix);
        assertEquals("3x4 Q size", qr.getQ().getRowDimension(), 3);
        assertEquals("3x4 Q size", qr.getQ().getColumnDimension(), 3);
        assertEquals("3x4 R size", qr.getR().getRowDimension(), 3);
        assertEquals("3x4 R size", qr.getR().getColumnDimension(), 4);
    }

    /** test A = QR */
    public void testAEqualQR() {
        RealMatrix A = new RealMatrixImpl(testData3x3NonSingular, false);
        QRDecomposition qr = new QRDecompositionImpl(A);
        RealMatrix Q = qr.getQ();
        RealMatrix R = qr.getR();
        double norm = Q.multiply(R).subtract(A).getNorm();
        assertEquals("3x3 nonsingular A = QR", 0, norm, normTolerance);

        RealMatrix matrix = new RealMatrixImpl(testData3x3Singular, false);
        qr = new QRDecompositionImpl(matrix);
        norm = qr.getQ().multiply(qr.getR()).subtract(matrix).getNorm();
        assertEquals("3x3 singular A = QR", 0, norm, normTolerance);

        matrix = new RealMatrixImpl(testData3x4, false);
        qr = new QRDecompositionImpl(matrix);
        norm = qr.getQ().multiply(qr.getR()).subtract(matrix).getNorm();
        assertEquals("3x4 A = QR", 0, norm, normTolerance);

        matrix = new RealMatrixImpl(testData4x3, false);
        qr = new QRDecompositionImpl(matrix);
        norm = qr.getQ().multiply(qr.getR()).subtract(matrix).getNorm();
        assertEquals("4x3 A = QR", 0, norm, normTolerance);
    }

    /** test the orthogonality of Q */
    public void testQOrthogonal() {
        RealMatrix matrix = new RealMatrixImpl(testData3x3NonSingular, false);
        matrix = new QRDecompositionImpl(matrix).getQ();
        RealMatrix eye = MatrixUtils.createRealIdentityMatrix(3);
        double norm = matrix.transpose().multiply(matrix).subtract(eye)
                .getNorm();
        assertEquals("3x3 nonsingular Q'Q = I", 0, norm, normTolerance);

        matrix = new RealMatrixImpl(testData3x3Singular, false);
        matrix = new QRDecompositionImpl(matrix).getQ();
        eye = MatrixUtils.createRealIdentityMatrix(3);
        norm = matrix.transpose().multiply(matrix).subtract(eye)
                .getNorm();
        assertEquals("3x3 singular Q'Q = I", 0, norm, normTolerance);

        matrix = new RealMatrixImpl(testData3x4, false);
        matrix = new QRDecompositionImpl(matrix).getQ();
        eye = MatrixUtils.createRealIdentityMatrix(3);
        norm = matrix.transpose().multiply(matrix).subtract(eye)
                .getNorm();
        assertEquals("3x4 Q'Q = I", 0, norm, normTolerance);

        matrix = new RealMatrixImpl(testData4x3, false);
        matrix = new QRDecompositionImpl(matrix).getQ();
        eye = MatrixUtils.createRealIdentityMatrix(4);
        norm = matrix.transpose().multiply(matrix).subtract(eye)
                .getNorm();
        assertEquals("4x3 Q'Q = I", 0, norm, normTolerance);
    }

    /** test that R is upper triangular */
    public void testRUpperTriangular() {
        RealMatrixImpl matrix = new RealMatrixImpl(testData3x3NonSingular, false);
        RealMatrix R = new QRDecompositionImpl(matrix).getR();
        for (int i = 0; i < R.getRowDimension(); i++)
            for (int j = 0; j < i; j++)
                assertEquals("R lower triangle", R.getEntry(i, j), 0,
                        entryTolerance);

        matrix = new RealMatrixImpl(testData3x3Singular, false);
        R = new QRDecompositionImpl(matrix).getR();
        for (int i = 0; i < R.getRowDimension(); i++)
            for (int j = 0; j < i; j++)
                assertEquals("R lower triangle", R.getEntry(i, j), 0,
                        entryTolerance);

        matrix = new RealMatrixImpl(testData3x4, false);
        R = new QRDecompositionImpl(matrix).getR();
        for (int i = 0; i < R.getRowDimension(); i++)
            for (int j = 0; j < i; j++)
                assertEquals("R lower triangle", R.getEntry(i, j), 0,
                        entryTolerance);

        matrix = new RealMatrixImpl(testData4x3, false);
        R = new QRDecompositionImpl(matrix).getR();
        for (int i = 0; i < R.getRowDimension(); i++)
            for (int j = 0; j < i; j++)
                assertEquals("R lower triangle", R.getEntry(i, j), 0,
                        entryTolerance);
    }

    /** test that H is trapezoidal */
    public void testHTrapezoidal() {
        RealMatrixImpl matrix = new RealMatrixImpl(testData3x3NonSingular, false);
        RealMatrix H = new QRDecompositionImpl(matrix).getH();
        for (int i = 0; i < H.getRowDimension(); i++)
            for (int j = i + 1; j < H.getColumnDimension(); j++)
                assertEquals(H.getEntry(i, j), 0, entryTolerance);

        matrix = new RealMatrixImpl(testData3x3Singular, false);
        H = new QRDecompositionImpl(matrix).getH();
        for (int i = 0; i < H.getRowDimension(); i++)
            for (int j = i + 1; j < H.getColumnDimension(); j++)
                assertEquals(H.getEntry(i, j), 0, entryTolerance);

        matrix = new RealMatrixImpl(testData3x4, false);
        H = new QRDecompositionImpl(matrix).getH();
        for (int i = 0; i < H.getRowDimension(); i++)
            for (int j = i + 1; j < H.getColumnDimension(); j++)
                assertEquals(H.getEntry(i, j), 0, entryTolerance);

        matrix = new RealMatrixImpl(testData4x3, false);
        H = new QRDecompositionImpl(matrix).getH();
        for (int i = 0; i < H.getRowDimension(); i++)
            for (int j = i + 1; j < H.getColumnDimension(); j++)
                assertEquals(H.getEntry(i, j), 0, entryTolerance);

    }

    /** test rank */
    public void testRank() {
        QRDecomposition qr =
            new QRDecompositionImpl(new RealMatrixImpl(testData3x3NonSingular, false));
        assertTrue(qr.isFullRank());

        qr = new QRDecompositionImpl(new RealMatrixImpl(testData3x3Singular, false));
        assertFalse(qr.isFullRank());

        qr = new QRDecompositionImpl(new RealMatrixImpl(testData3x4, false));
        assertFalse(qr.isFullRank());

        qr = new QRDecompositionImpl(new RealMatrixImpl(testData4x3, false));
        assertTrue(qr.isFullRank());

    }

    /** test solve dimension errors */
    public void testSolveDimensionErrors() {
        QRDecomposition qr =
            new QRDecompositionImpl(new RealMatrixImpl(testData3x3NonSingular, false));
        RealMatrix b = new RealMatrixImpl(new double[2][2]);
        try {
            qr.solve(b);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            qr.solve(b.getColumn(0));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            qr.solve(b.getColumnVector(0));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    /** test solve rank errors */
    public void testSolveRankErrors() {
        QRDecomposition qr =
            new QRDecompositionImpl(new RealMatrixImpl(testData3x3Singular, false));
        RealMatrix b = new RealMatrixImpl(new double[3][2]);
        try {
            qr.solve(b);
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            qr.solve(b.getColumn(0));
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            qr.solve(b.getColumnVector(0));
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    /** test solve */
    public void testSolve() {
        QRDecomposition qr =
            new QRDecompositionImpl(new RealMatrixImpl(testData3x3NonSingular, false));
        RealMatrix b = new RealMatrixImpl(new double[][] {
                { -102, 12250 }, { 544, 24500 }, { 167, -36750 }
        });
        RealMatrix xRef = new RealMatrixImpl(new double[][] {
                { 1, 2515 }, { 2, 422 }, { -3, 898 }
        });

        // using RealMatrix
        assertEquals(0, qr.solve(b).subtract(xRef).getNorm(), 1.0e-13);

        // using double[]
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            assertEquals(0,
                         new RealVectorImpl(qr.solve(b.getColumn(i))).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

        // using RealVectorImpl
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            assertEquals(0,
                         qr.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

        // using RealVector with an alternate implementation
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            RealVectorImplTest.RealVectorTestImpl v =
                new RealVectorImplTest.RealVectorTestImpl(b.getColumn(i));
            assertEquals(0,
                         qr.solve(v).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

    }

    /** test matrices values */
    public void testMatricesValues() {
        QRDecomposition qr =
            new QRDecompositionImpl(new RealMatrixImpl(testData3x3NonSingular, false));
        RealMatrix qRef = new RealMatrixImpl(new double[][] {
                { -12.0 / 14.0,   69.0 / 175.0,  -58.0 / 175.0 },
                {  -6.0 / 14.0, -158.0 / 175.0,    6.0 / 175.0 },
                {   4.0 / 14.0,  -30.0 / 175.0, -165.0 / 175.0 }
        });
        RealMatrix rRef = new RealMatrixImpl(new double[][] {
                { -14.0,  -21.0, 14.0 },
                {   0.0, -175.0, 70.0 },
                {   0.0,    0.0, 35.0 }
        });
        RealMatrix hRef = new RealMatrixImpl(new double[][] {
                { 26.0 / 14.0, 0.0, 0.0 },
                {  6.0 / 14.0, 648.0 / 325.0, 0.0 },
                { -4.0 / 14.0,  36.0 / 325.0, 2.0 }
        });

        // check values against known references
        RealMatrix q = qr.getQ();
        assertEquals(0, q.subtract(qRef).getNorm(), 1.0e-13);
        RealMatrix r = qr.getR();
        assertEquals(0, r.subtract(rRef).getNorm(), 1.0e-13);
        RealMatrix h = qr.getH();
        assertEquals(0, h.subtract(hRef).getNorm(), 1.0e-13);

        // check the same cached instance is returned the second time
        assertTrue(q == qr.getQ());
        assertTrue(r == qr.getR());
        assertTrue(h == qr.getH());
        
    }

    /** test no call to decompose */
    public void testNoDecompose() {
        try {
            new QRDecompositionImpl().isFullRank();
            fail("an exception should have been caught");
        } catch (IllegalStateException ise) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

}
