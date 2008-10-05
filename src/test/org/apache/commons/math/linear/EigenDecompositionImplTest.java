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

import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class EigenDecompositionImplTest extends TestCase {

    private double[] refValues;
    private RealMatrix matrix;

    private static final double normTolerance = 1.e-10;

    public EigenDecompositionImplTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(EigenDecompositionImplTest.class);
        suite.setName("EigenDecompositionImpl Tests");
        return suite;
    }

    /** test dimensions */
    public void testDimensions() {
        final int m = matrix.getRowDimension();
        EigenDecomposition ed = new EigenDecompositionImpl(matrix);
        assertEquals(m, ed.getV().getRowDimension());
        assertEquals(m, ed.getV().getColumnDimension());
        assertEquals(m, ed.getD().getColumnDimension());
        assertEquals(m, ed.getD().getColumnDimension());
        assertEquals(m, ed.getVT().getRowDimension());
        assertEquals(m, ed.getVT().getColumnDimension());
    }

    /** test eigenvalues */
    public void testEigenvalues() {
        EigenDecomposition ed = new EigenDecompositionImpl(matrix);
        double[] eigenValues = ed.getEigenvalues();
        assertEquals(refValues.length, eigenValues.length);
        for (int i = 0; i < refValues.length; ++i) {
            assertEquals(refValues[i], eigenValues[eigenValues.length - 1 - i], 3.0e-15);
        }
    }

    /** test eigenvectors */
    public void testEigenvectors() {
        EigenDecomposition ed = new EigenDecompositionImpl(matrix);
        for (int i = 0; i < matrix.getRowDimension(); ++i) {
            double lambda = ed.getEigenvalue(i);
            RealVector v  = ed.getEigenvector(i);
            RealVector mV = matrix.operate(v);
            assertEquals(0, mV.subtract(v.mapMultiplyToSelf(lambda)).getNorm(), 1.0e-13);
        }
    }

    /** test A = VDVt */
    public void testAEqualVDVt() {
        EigenDecomposition ed = new EigenDecompositionImpl(matrix);
        RealMatrix v  = ed.getV();
        RealMatrix d  = ed.getD();
        RealMatrix vT = ed.getVT();
        double norm = v.multiply(d).multiply(vT).subtract(matrix).getNorm();
        assertEquals(0, norm, normTolerance);
    }

    /** test that V is orthogonal */
    public void testVOrthogonal() {
        RealMatrix v = new EigenDecompositionImpl(matrix).getV();
        RealMatrix vTv = v.transpose().multiply(v);
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(vTv.getRowDimension());
        assertEquals(0, vTv.subtract(id).getNorm(), normTolerance);
    }

    /** test solve dimension errors */
    public void testSolveDimensionErrors() {
        EigenDecomposition ed = new EigenDecompositionImpl(matrix);
        RealMatrix b = new RealMatrixImpl(new double[2][2]);
        try {
            ed.solve(b);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            ed.solve(b.getColumn(0));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            ed.solve(new RealVectorImplTest.RealVectorTestImpl(b.getColumn(0)));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    /** test solve */
    public void testSolve() {
        RealMatrix m = new RealMatrixImpl(new double[][] {
                { 91,  5, 29, 32, 40, 14 },
                {  5, 34, -1,  0,  2, -1 },
                { 29, -1, 12,  9, 21,  8 },
                { 32,  0,  9, 14,  9,  0 },
                { 40,  2, 21,  9, 51, 19 },
                { 14, -1,  8,  0, 19, 14 }
        });
        EigenDecomposition ed = new EigenDecompositionImpl(m);
        assertEquals(184041, ed.getDeterminant(), 2.0e-8);
        RealMatrix b = new RealMatrixImpl(new double[][] {
                { 1561, 269, 188 },
                {   69, -21,  70 },
                {  739, 108,  63 },
                {  324,  86,  59 },
                { 1624, 194, 107 },
                {  796,  69,  36 }
        });
        RealMatrix xRef = new RealMatrixImpl(new double[][] {
                { 1,   2, 1 },
                { 2,  -1, 2 },
                { 4,   2, 3 },
                { 8,  -1, 0 },
                { 16,  2, 0 },
                { 32, -1, 0 }
        });

        // using RealMatrix
        assertEquals(0, ed.solve(b).subtract(xRef).getNorm(), normTolerance);

        // using double[]
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            assertEquals(0,
                         new RealVectorImpl(ed.solve(b.getColumn(i))).subtract(xRef.getColumnVector(i)).getNorm(),
                         2.0e-11);
        }

        // using RealMatrixImpl
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            assertEquals(0,
                         ed.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(),
                         2.0e-11);
        }

        // using RealMatrix with an alternate implementation
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            RealVectorImplTest.RealVectorTestImpl v =
                new RealVectorImplTest.RealVectorTestImpl(b.getColumn(i));
            assertEquals(0,
                         ed.solve(v).subtract(xRef.getColumnVector(i)).getNorm(),
                         2.0e-11);
        }

    }

    public void setUp() {
        refValues = new double[] {
                2.003, 2.002, 2.001, 1.001, 1.000, 0.001
        };
        matrix = createTestMatrix(new Random(35992629946426l), refValues);
    }

    public void tearDown() {
        refValues = null;
        matrix    = null;
    }

    private RealMatrix createTestMatrix(final Random r, final double[] eigenValues) {
        final RealMatrix v = createOrthogonalMatrix(r, eigenValues.length);
        final RealMatrix d = createDiagonalMatrix(eigenValues, eigenValues.length);
        return v.multiply(d).multiply(v.transpose());
    }

    private RealMatrix createOrthogonalMatrix(final Random r, final int size) {
        final double[][] data = new double[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                data[i][j] = 2 * r.nextDouble() - 1;
            }
        }
        final RealMatrix m = new RealMatrixImpl(data, false);
        return new QRDecompositionImpl(m).getQ();
    }

    private RealMatrix createDiagonalMatrix(final double[] data, final int rows) {
        final double[][] dData = new double[rows][rows];
        for (int i = 0; i < data.length; ++i) {
            dData[i][i] = data[i];
        }
        return new RealMatrixImpl(dData, false);
    }

}
