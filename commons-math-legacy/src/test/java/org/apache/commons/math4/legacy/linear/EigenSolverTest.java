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

import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.numbers.core.Precision;
import org.junit.Test;
import org.junit.Assert;

public class EigenSolverTest {

    private double[][] bigSingular = {
        { 1.0, 2.0,   3.0,    4.0 },
        { 2.0, 5.0,   3.0,    4.0 },
        { 7.0, 3.0, 256.0, 1930.0 },
        { 3.0, 7.0,   6.0,    8.0 }
    }; // 4th row = 1st + 2nd

    /** test non invertible matrix */
    @Test
    public void testNonInvertible() {
        Random r = new Random(9994100315209L);
        RealMatrix m =
            EigenDecompositionTest.createTestMatrix(r, new double[] { 1.0, 0.0, -1.0, -2.0, -3.0 });
        DecompositionSolver es = new EigenDecomposition(m).getSolver();
        Assert.assertFalse(es.isNonSingular());
        try {
            es.getInverse();
            Assert.fail("an exception should have been thrown");
        } catch (SingularMatrixException ime) {
            // expected behavior
        }
    }

    /** test invertible matrix */
    @Test
    public void testInvertible() {
        Random r = new Random(9994100315209L);
        RealMatrix m =
            EigenDecompositionTest.createTestMatrix(r, new double[] { 1.0, 0.5, -1.0, -2.0, -3.0 });
        DecompositionSolver es = new EigenDecomposition(m).getSolver();
        Assert.assertTrue(es.isNonSingular());
        RealMatrix inverse = es.getInverse();
        RealMatrix error =
            m.multiply(inverse).subtract(MatrixUtils.createRealIdentityMatrix(m.getRowDimension()));
        Assert.assertEquals(0, error.getNorm(), 4.0e-15);
    }

    /**
     * Verifies operation on very small values.
     * Matrix with eigenvalues {8e-100, -1e-100, -1e-100}
     */
    @Test
    public void testInvertibleTinyValues() {
        final double tiny = 1e-100;
        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {
                {3,  2,  4},
                {2,  0,  2},
                {4,  2,  3}
        });
        m = m.scalarMultiply(tiny);

        final EigenDecomposition ed = new EigenDecomposition(m);
        RealMatrix inv = ed.getSolver().getInverse();

        final RealMatrix id = m.multiply(inv);
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = 0; j < m.getColumnDimension(); j++) {
                if (i == j) {
                    Assert.assertTrue(Precision.equals(1, id.getEntry(i, j), 1e-15));
                } else {
                    Assert.assertTrue(Precision.equals(0, id.getEntry(i, j), 1e-15));
                }
            }
        }
    }

    @Test(expected=SingularMatrixException.class)
    public void testNonInvertibleMath1045() {
        EigenDecomposition eigen =
            new EigenDecomposition(MatrixUtils.createRealMatrix(bigSingular));
        eigen.getSolver().getInverse();
    }

    @Test(expected=SingularMatrixException.class)
    public void testZeroMatrix() {
        EigenDecomposition eigen =
            new EigenDecomposition(MatrixUtils.createRealMatrix(new double[][] {{0}}));
        eigen.getSolver().getInverse();
    }

    @Test
    public void testIsNonSingularTinyOutOfOrderEigenvalue() {
        final EigenDecomposition eigen
            = new EigenDecomposition(MatrixUtils.createRealMatrix(new double[][] {
                        { 1e-13, 0 },
                        { 1,     1 },
                    }));
        Assert.assertFalse("Singular matrix not detected",
                           eigen.getSolver().isNonSingular());
    }

    /** test solve dimension errors */
    @Test
    public void testSolveDimensionErrors() {
        final double[] refValues = new double[] {
            2.003, 2.002, 2.001, 1.001, 1.000, 0.001
        };
        final RealMatrix matrix = EigenDecompositionTest.createTestMatrix(new Random(35992629946426L), refValues);

        DecompositionSolver es = new EigenDecomposition(matrix).getSolver();
        RealMatrix b = MatrixUtils.createRealMatrix(new double[2][2]);
        try {
            es.solve(b);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException iae) {
            // expected behavior
        }
        try {
            es.solve(b.getColumnVector(0));
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException iae) {
            // expected behavior
        }
        try {
            es.solve(new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException iae) {
            // expected behavior
        }
    }

    /** test solve */
    @Test
    public void testSolve() {
        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {
                { 91,  5, 29, 32, 40, 14 },
                {  5, 34, -1,  0,  2, -1 },
                { 29, -1, 12,  9, 21,  8 },
                { 32,  0,  9, 14,  9,  0 },
                { 40,  2, 21,  9, 51, 19 },
                { 14, -1,  8,  0, 19, 14 }
        });
        DecompositionSolver es = new EigenDecomposition(m).getSolver();
        RealMatrix b = MatrixUtils.createRealMatrix(new double[][] {
                { 1561, 269, 188 },
                {   69, -21,  70 },
                {  739, 108,  63 },
                {  324,  86,  59 },
                { 1624, 194, 107 },
                {  796,  69,  36 }
        });
        RealMatrix xRef = MatrixUtils.createRealMatrix(new double[][] {
                { 1,   2, 1 },
                { 2,  -1, 2 },
                { 4,   2, 3 },
                { 8,  -1, 0 },
                { 16,  2, 0 },
                { 32, -1, 0 }
        });

        // using RealMatrix
        RealMatrix solution=es.solve(b);
        Assert.assertEquals(0, solution.subtract(xRef).getNorm(), 2.5e-12);

        // using RealVector
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            Assert.assertEquals(0,
                         es.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(),
                         2.0e-11);
        }

        // using RealVector with an alternate implementation
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            ArrayRealVectorTest.RealVectorTestImpl v =
                new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(i));
            Assert.assertEquals(0,
                         es.solve(v).subtract(xRef.getColumnVector(i)).getNorm(),
                         2.0e-11);
        }
    }
}
