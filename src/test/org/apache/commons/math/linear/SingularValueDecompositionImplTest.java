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

public class SingularValueDecompositionImplTest extends TestCase {

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

    public SingularValueDecompositionImplTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SingularValueDecompositionImplTest.class);
        suite.setName("SingularValueDecompositionImpl Tests");
        return suite;
    }

    public void testMoreRows() {
        final double[] singularValues = { 123.456, 2.3, 1.001, 0.999 };
        final int rows    = singularValues.length + 2;
        final int columns = singularValues.length;
        Random r = new Random(15338437322523l);
        SingularValueDecomposition svd =
            new SingularValueDecompositionImpl(createTestMatrix(r, rows, columns, singularValues));
        double[] computedSV = svd.getSingularValues();
        assertEquals(singularValues.length, computedSV.length);
        for (int i = 0; i < singularValues.length; ++i) {
            assertEquals(singularValues[i], computedSV[i], 1.0e-10);
        }
    }

    public void testMoreColumns() {
        final double[] singularValues = { 123.456, 2.3, 1.001, 0.999 };
        final int rows    = singularValues.length;
        final int columns = singularValues.length + 2;
        Random r = new Random(732763225836210l);
        SingularValueDecomposition svd =
            new SingularValueDecompositionImpl(createTestMatrix(r, rows, columns, singularValues));
        double[] computedSV = svd.getSingularValues();
        assertEquals(singularValues.length, computedSV.length);
        for (int i = 0; i < singularValues.length; ++i) {
            assertEquals(singularValues[i], computedSV[i], 1.0e-10);
        }
    }

    /** test dimensions */
    public void testDimensions() {
        RealMatrixImpl matrix = new RealMatrixImpl(testSquare, false);
        final int m = matrix.getRowDimension();
        final int n = matrix.getColumnDimension();
        SingularValueDecomposition svd = new SingularValueDecompositionImpl(matrix);
        assertEquals(m, svd.getU().getRowDimension());
        assertEquals(m, svd.getU().getColumnDimension());
        assertEquals(m, svd.getS().getColumnDimension());
        assertEquals(n, svd.getS().getColumnDimension());
        assertEquals(n, svd.getV().getRowDimension());
        assertEquals(n, svd.getV().getColumnDimension());

    }

    /** test A = USVt */
    public void testAEqualUSVt() {
        checkAEqualUSVt(new RealMatrixImpl(testSquare, false));
        checkAEqualUSVt(new RealMatrixImpl(testNonSquare, false));
        checkAEqualUSVt(new RealMatrixImpl(testNonSquare, false).transpose());
    }

    public void checkAEqualUSVt(final RealMatrix matrix) {
        SingularValueDecomposition svd = new SingularValueDecompositionImpl(matrix);
        RealMatrix u = svd.getU();
        RealMatrix s = svd.getS();
        RealMatrix v = svd.getV();
        double norm = u.multiply(s).multiply(v.transpose()).subtract(matrix).getNorm();
        assertEquals(0, norm, normTolerance);

    }

    /** test that U is orthogonal */
    public void testUOrthogonal() {
        checkOrthogonal(new SingularValueDecompositionImpl(new RealMatrixImpl(testSquare, false)).getU());
        checkOrthogonal(new SingularValueDecompositionImpl(new RealMatrixImpl(testNonSquare, false)).getU());
        checkOrthogonal(new SingularValueDecompositionImpl(new RealMatrixImpl(testNonSquare, false).transpose()).getU());
    }

    /** test that V is orthogonal */
    public void testVOrthogonal() {
        checkOrthogonal(new SingularValueDecompositionImpl(new RealMatrixImpl(testSquare, false)).getV());
        checkOrthogonal(new SingularValueDecompositionImpl(new RealMatrixImpl(testNonSquare, false)).getV());
        checkOrthogonal(new SingularValueDecompositionImpl(new RealMatrixImpl(testNonSquare, false).transpose()).getV());
    }

    public void checkOrthogonal(final RealMatrix m) {
        RealMatrix mTm = m.transpose().multiply(m);
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(mTm.getRowDimension());
        assertEquals(0, mTm.subtract(id).getNorm(), normTolerance);
    }

    /** test solve dimension errors */
    public void testSolveDimensionErrors() {
        SingularValueDecomposition svd =
            new SingularValueDecompositionImpl(new RealMatrixImpl(testSquare, false));
        RealMatrix b = new RealMatrixImpl(new double[3][2]);
        try {
            svd.solve(b);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            svd.solve(b.getColumn(0));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            svd.solve(new RealVectorImplTest.RealVectorTestImpl(b.getColumn(0)));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    /** test solve singularity errors */
    public void testSolveSingularityErrors() {
        SingularValueDecomposition svd =
            new SingularValueDecompositionImpl(new RealMatrixImpl(new double[][] {
                                                                      { 1.0, 0.0 },
                                                                      { 0.0, 0.0 }
                                                                  }, false));
        RealMatrix b = new RealMatrixImpl(new double[2][2]);
        try {
            svd.solve(b);
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException ime) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            svd.solve(b.getColumn(0));
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException ime) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            svd.solve(b.getColumnVector(0));
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException ime) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            svd.solve(new RealVectorImplTest.RealVectorTestImpl(b.getColumn(0)));
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException ime) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    /** test solve */
    public void testSolve() {
        SingularValueDecomposition svd =
            new SingularValueDecompositionImpl(new RealMatrixImpl(testSquare, false));
        RealMatrix b = new RealMatrixImpl(new double[][] {
                { 1, 2, 3 }, { 0, -5, 1 }
        });
        RealMatrix xRef = new RealMatrixImpl(new double[][] {
                { -8.0 / 25.0, -263.0 / 75.0, -29.0 / 75.0 },
                { 19.0 / 25.0,   78.0 / 25.0,  49.0 / 25.0 }
        });

        // using RealMatrix
        assertEquals(0, svd.solve(b).subtract(xRef).getNorm(), normTolerance);

        // using double[]
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            assertEquals(0,
                         new RealVectorImpl(svd.solve(b.getColumn(i))).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

        // using RealMatrixImpl
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            assertEquals(0,
                         svd.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

        // using RealMatrix with an alternate implementation
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            RealVectorImplTest.RealVectorTestImpl v =
                new RealVectorImplTest.RealVectorTestImpl(b.getColumn(i));
            assertEquals(0,
                         svd.solve(v).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

    }

    /** test matrices values */
    public void testMatricesValues1() {
       SingularValueDecomposition svd =
            new SingularValueDecompositionImpl(new RealMatrixImpl(testSquare, false));
        RealMatrix uRef = new RealMatrixImpl(new double[][] {
                { 3.0 / 5.0, -4.0 / 5.0 },
                { 4.0 / 5.0,  3.0 / 5.0 }
        });
        RealMatrix sRef = new RealMatrixImpl(new double[][] {
                { 3.0, 0.0 },
                { 0.0, 1.0 }
        });
        RealMatrix vRef = new RealMatrixImpl(new double[][] {
                { 4.0 / 5.0,  3.0 / 5.0 },
                { 3.0 / 5.0, -4.0 / 5.0 }
        });

        // check values against known references
        RealMatrix u = svd.getU();
        assertEquals(0, u.subtract(uRef).getNorm(), normTolerance);
        RealMatrix s = svd.getS();
        assertEquals(0, s.subtract(sRef).getNorm(), normTolerance);
        RealMatrix v = svd.getV();
        assertEquals(0, v.subtract(vRef).getNorm(), normTolerance);

        // check the same cached instance is returned the second time
        assertTrue(u == svd.getU());
        assertTrue(s == svd.getS());
        assertTrue(v == svd.getV());
        
    }

    /** test matrices values */
    public void testMatricesValues2() {

        RealMatrix uRef = new RealMatrixImpl(new double[][] {
            {  0.0 / 5.0,  3.0 / 5.0,  0.0 / 5.0 },
            { -4.0 / 5.0,  0.0 / 5.0, -3.0 / 5.0 },
            {  0.0 / 5.0,  4.0 / 5.0,  0.0 / 5.0 },
            { -3.0 / 5.0,  0.0 / 5.0,  4.0 / 5.0 }
        });
        RealMatrix sRef = new RealMatrixImpl(new double[][] {
            { 4.0, 0.0, 0.0 },
            { 0.0, 3.0, 0.0 },
            { 0.0, 0.0, 2.0 }
        });
        RealMatrix vRef = new RealMatrixImpl(new double[][] {
            {  80.0 / 125.0,  -60.0 / 125.0, 75.0 / 125.0 },
            {  24.0 / 125.0,  107.0 / 125.0, 60.0 / 125.0 },
            { -93.0 / 125.0,  -24.0 / 125.0, 80.0 / 125.0 }
        });

        // check values against known references
        SingularValueDecomposition svd = new SingularValueDecompositionImpl();
        svd.decompose(new RealMatrixImpl(testNonSquare, false));
        RealMatrix u = svd.getU();
        assertEquals(0, u.subtract(uRef).getNorm(), normTolerance);
        RealMatrix s = svd.getS();
        assertEquals(0, s.subtract(sRef).getNorm(), normTolerance);
        RealMatrix v = svd.getV();
        assertEquals(0, v.subtract(vRef).getNorm(), normTolerance);

        // check the same cached instance is returned the second time
        assertTrue(u == svd.getU());
        assertTrue(s == svd.getS());
        assertTrue(v == svd.getV());

    }

    /** test condition number */
    public void testConditionNumber() {
        SingularValueDecompositionImpl svd =
            new SingularValueDecompositionImpl(new RealMatrixImpl(testSquare, false));
        assertEquals(3.0, svd.getConditionNumber(), 1.0e-15);
    }

    private RealMatrix createTestMatrix(final Random r, final int rows, final int columns,
                                        final double[] singularValues) {
        final RealMatrix u =
            EigenDecompositionImplTest.createOrthogonalMatrix(r, rows);
        final RealMatrix d =
            EigenDecompositionImplTest.createDiagonalMatrix(singularValues, rows, columns);
        final RealMatrix v =
            EigenDecompositionImplTest.createOrthogonalMatrix(r, columns);
        return u.multiply(d).multiply(v);
    }

}
