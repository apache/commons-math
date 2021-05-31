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

import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.core.dfp.Dfp;
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

    public static FieldMatrix<Dfp> createDfpMatrix(final int[][] data) {
        final int numRows = data.length;
        final int numCols = data[0].length;
        final Array2DRowFieldMatrix<Dfp> m;
        m = new Array2DRowFieldMatrix<>(Dfp25.getField(),
                                                numRows, numCols);
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                m.setEntry(i, j, Dfp25.of(data[i][j], 1));
            }
        }
        return m;
    }

    /** test singular */
    @Test
    public void testSingular() {
        FieldDecompositionSolver<Dfp> solver;
        solver = new FieldLUDecomposition<>(createDfpMatrix(testData))
            .getSolver();
        Assert.assertTrue(solver.isNonSingular());
        solver = new FieldLUDecomposition<>(createDfpMatrix(singular))
            .getSolver();
        Assert.assertFalse(solver.isNonSingular());
        solver = new FieldLUDecomposition<>(createDfpMatrix(bigSingular))
            .getSolver();
        Assert.assertFalse(solver.isNonSingular());
    }

    /** test solve dimension errors */
    @Test
    public void testSolveDimensionErrors() {
        FieldDecompositionSolver<Dfp> solver;
        solver = new FieldLUDecomposition<>(createDfpMatrix(testData))
            .getSolver();
        FieldMatrix<Dfp> b = createDfpMatrix(new int[2][2]);
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
        FieldDecompositionSolver<Dfp> solver;
        solver = new FieldLUDecomposition<>(createDfpMatrix(singular))
            .getSolver();
        FieldMatrix<Dfp> b = createDfpMatrix(new int[2][2]);
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
        FieldDecompositionSolver<Dfp> solver;
        solver = new FieldLUDecomposition<>(createDfpMatrix(testData))
            .getSolver();
        FieldMatrix<Dfp> b = createDfpMatrix(new int[][] {
                { 1, 0 }, { 2, -5 }, { 3, 1 }
        });
        FieldMatrix<Dfp> xRef = createDfpMatrix(new int[][] {
                { 19, -71 }, { -6, 22 }, { -2, 9 }
        });

        // using FieldMatrix
        FieldMatrix<Dfp> x = solver.solve(b);
        for (int i = 0; i < x.getRowDimension(); i++){
            for (int j = 0; j < x.getColumnDimension(); j++){
                Assert.assertEquals("(" + i + ", " + j + ")",
                                    xRef.getEntry(i, j), x.getEntry(i, j));
            }
        }

        // using ArrayFieldVector
        for (int j = 0; j < b.getColumnDimension(); j++) {
            final FieldVector<Dfp> xj = solver.solve(b.getColumnVector(j));
            for (int i = 0; i < xj.getDimension(); i++){
                Assert.assertEquals("(" + i + ", " + j + ")",
                                    xRef.getEntry(i, j), xj.getEntry(i));
            }
        }

        // using SparseFieldVector
        for (int j = 0; j < b.getColumnDimension(); j++) {
            final SparseFieldVector<Dfp> bj;
            bj = new SparseFieldVector<>(Dfp25.getField(),
                                                 b.getColumn(j));
            final FieldVector<Dfp> xj = solver.solve(bj);
            for (int i = 0; i < xj.getDimension(); i++) {
                Assert.assertEquals("(" + i + ", " + j + ")",
                                    xRef.getEntry(i, j), xj.getEntry(i));
            }
        }
    }

    /** test determinant */
    @Test
    public void testDeterminant() {
        Assert.assertEquals( -1, getDeterminant(createDfpMatrix(testData)), 1E-15);
        Assert.assertEquals(-10, getDeterminant(createDfpMatrix(luData)), 1E-14);
        Assert.assertEquals(  0, getDeterminant(createDfpMatrix(singular)), 1E-15);
        Assert.assertEquals(  0, getDeterminant(createDfpMatrix(bigSingular)), 1E-15);
    }

    private double getDeterminant(final FieldMatrix<Dfp> m) {
        return new FieldLUDecomposition<>(m).getDeterminant().toDouble();
    }
}
