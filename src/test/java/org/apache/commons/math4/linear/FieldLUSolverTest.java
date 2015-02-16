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

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionField;
import org.junit.Assert;
import org.junit.Test;

public class FieldLUSolverTest {
    private int[][] testData = {
            { 1, 2, 3},
            { 2, 5, 3},
            { 1, 0, 8}
    };
    private int[][] luData = {
            { 2, 3, 3 },
            { 0, 5, 7 },
            { 6, 9, 8 }
    };

    // singular matrices
    private int[][] singular = {
            { 2, 3 },
            { 2, 3 }
    };
    private int[][] bigSingular = {
            { 1, 2,   3,    4 },
            { 2, 5,   3,    4 },
            { 7, 3, 256, 1930 },
            { 3, 7,   6,    8 }
    }; // 4th row = 1st + 2nd

    public static FieldMatrix<Fraction> createFractionMatrix(final int[][] data) {
        final int numRows = data.length;
        final int numCols = data[0].length;
        final Array2DRowFieldMatrix<Fraction> m;
        m = new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),
                                                numRows, numCols);
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                m.setEntry(i, j, new Fraction(data[i][j], 1));
            }
        }
        return m;
    }

    /** test singular */
    @Test
    public void testSingular() {
        FieldDecompositionSolver<Fraction> solver;
        solver = new FieldLUDecomposition<Fraction>(createFractionMatrix(testData))
            .getSolver();
        Assert.assertTrue(solver.isNonSingular());
        solver = new FieldLUDecomposition<Fraction>(createFractionMatrix(singular))
            .getSolver();
        Assert.assertFalse(solver.isNonSingular());
        solver = new FieldLUDecomposition<Fraction>(createFractionMatrix(bigSingular))
            .getSolver();
        Assert.assertFalse(solver.isNonSingular());
    }

    /** test solve dimension errors */
    @Test
    public void testSolveDimensionErrors() {
        FieldDecompositionSolver<Fraction> solver;
        solver = new FieldLUDecomposition<Fraction>(createFractionMatrix(testData))
            .getSolver();
        FieldMatrix<Fraction> b = createFractionMatrix(new int[2][2]);
        try {
            solver.solve(b);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException iae) {
            // expected behavior
        }
        try {
            solver.solve(b.getColumnVector(0));
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException iae) {
            // expected behavior
        }
    }

    /** test solve singularity errors */
    @Test
    public void testSolveSingularityErrors() {
        FieldDecompositionSolver<Fraction> solver;
        solver = new FieldLUDecomposition<Fraction>(createFractionMatrix(singular))
            .getSolver();
        FieldMatrix<Fraction> b = createFractionMatrix(new int[2][2]);
        try {
            solver.solve(b);
            Assert.fail("an exception should have been thrown");
        } catch (SingularMatrixException ime) {
            // expected behavior
        }
        try {
            solver.solve(b.getColumnVector(0));
            Assert.fail("an exception should have been thrown");
        } catch (SingularMatrixException ime) {
            // expected behavior
        }
    }

    /** test solve */
    @Test
    public void testSolve() {
        FieldDecompositionSolver<Fraction> solver;
        solver = new FieldLUDecomposition<Fraction>(createFractionMatrix(testData))
            .getSolver();
        FieldMatrix<Fraction> b = createFractionMatrix(new int[][] {
                { 1, 0 }, { 2, -5 }, { 3, 1 }
        });
        FieldMatrix<Fraction> xRef = createFractionMatrix(new int[][] {
                { 19, -71 }, { -6, 22 }, { -2, 9 }
        });

        // using FieldMatrix
        FieldMatrix<Fraction> x = solver.solve(b);
        for (int i = 0; i < x.getRowDimension(); i++){
            for (int j = 0; j < x.getColumnDimension(); j++){
                Assert.assertEquals("(" + i + ", " + j + ")",
                                    xRef.getEntry(i, j), x.getEntry(i, j));
            }
        }

        // using ArrayFieldVector
        for (int j = 0; j < b.getColumnDimension(); j++) {
            final FieldVector<Fraction> xj = solver.solve(b.getColumnVector(j));
            for (int i = 0; i < xj.getDimension(); i++){
                Assert.assertEquals("(" + i + ", " + j + ")",
                                    xRef.getEntry(i, j), xj.getEntry(i));
            }
        }

        // using SparseFieldVector
        for (int j = 0; j < b.getColumnDimension(); j++) {
            final SparseFieldVector<Fraction> bj;
            bj = new SparseFieldVector<Fraction>(FractionField.getInstance(),
                                                 b.getColumn(j));
            final FieldVector<Fraction> xj = solver.solve(bj);
            for (int i = 0; i < xj.getDimension(); i++) {
                Assert.assertEquals("(" + i + ", " + j + ")",
                                    xRef.getEntry(i, j), xj.getEntry(i));
            }
        }
    }

    /** test determinant */
    @Test
    public void testDeterminant() {
        Assert.assertEquals( -1, getDeterminant(createFractionMatrix(testData)), 1E-15);
        Assert.assertEquals(-10, getDeterminant(createFractionMatrix(luData)), 1E-14);
        Assert.assertEquals(  0, getDeterminant(createFractionMatrix(singular)), 1E-15);
        Assert.assertEquals(  0, getDeterminant(createFractionMatrix(bigSingular)), 1E-15);
    }

    private double getDeterminant(final FieldMatrix<Fraction> m) {
        return new FieldLUDecomposition<Fraction>(m).getDeterminant().doubleValue();
    }
}
