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

package org.apache.commons.math4.legacy.field.linalg;

import org.apache.commons.numbers.field.Field;
import org.apache.commons.math4.legacy.linear.SingularMatrixException;

/**
 * Calculates the LUP-decomposition of a square matrix.
 *
 * <p>The LUP-decomposition of a matrix A consists of three matrices
 * L, U and P that satisfy: PA = LU, L is lower triangular, and U is
 * upper triangular and P is a permutation matrix. All matrices are
 * m&times;m.</p>
 *
 * <p>Since {@link Field field} elements do not provide an ordering
 * operator, the permutation matrix is computed here only in order to
 * avoid a zero pivot element, no attempt is done to get the largest
 * pivot element.</p>
 *
 * @param <T> Type of the field elements.
 *
 * @see <a href="http://mathworld.wolfram.com/LUDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/LU_decomposition">Wikipedia</a>
 *
 * @since 4.0
 */
public final class FieldLUDecomposition<T> {
    /** Field to which the elements belong. */
    private final Field<T> field;
    /** Entries of LU decomposition. */
    private final FieldDenseMatrix<T> mLU;
    /** Pivot permutation associated with LU decomposition. */
    private final int[] pivot;
    /** Singularity indicator. */
    private final boolean isSingular;
    /** Parity of the permutation associated with the LU decomposition. */
    private final boolean isEven;

    /**
     * Calculates the LU-decomposition of the given {@code matrix}.
     *
     * @param matrix Matrix to decompose.
     * @throws IllegalArgumentException if the matrix is not square.
     */
    private FieldLUDecomposition(FieldDenseMatrix<T> matrix) {
        matrix.checkMultiply(matrix);

        field = matrix.getField();
        final int m = matrix.getRowDimension();
        pivot = new int[m];

        // Initialize permutation array and parity.
        for (int row = 0; row < m; row++) {
            pivot[row] = row;
        }
        mLU = matrix.copy();

        boolean even = true;
        boolean singular = false;
        // Loop over columns.
        for (int col = 0; col < m; col++) {
            T sum = field.zero();

            // Upper.
            for (int row = 0; row < col; row++) {
                sum = mLU.get(row, col);
                for (int i = 0; i < row; i++) {
                    sum = field.subtract(sum,
                                         field.multiply(mLU.get(row, i),
                                                        mLU.get(i, col)));
                }
                mLU.set(row, col, sum);
            }

            // Lower.
            int nonZero = col; // Permutation row.
            for (int row = col; row < m; row++) {
                sum = mLU.get(row, col);
                for (int i = 0; i < col; i++) {
                    sum = field.subtract(sum,
                                         field.multiply(mLU.get(row, i),
                                                        mLU.get(i, col)));
                }
                mLU.set(row, col, sum);

                if (mLU.get(nonZero, col).equals(field.zero())) {
                    // try to select a better permutation choice
                    ++nonZero;
                }
            }

            // Singularity check.
            if (nonZero >= m) {
                singular = true;
            } else {
                // Pivot if necessary.
                if (nonZero != col) {
                    T tmp = field.zero();
                    for (int i = 0; i < m; i++) {
                        tmp = mLU.get(nonZero, i);
                        mLU.set(nonZero, i, mLU.get(col, i));
                        mLU.set(col, i, tmp);
                    }
                    int temp = pivot[nonZero];
                    pivot[nonZero] = pivot[col];
                    pivot[col] = temp;
                    even = !even;
                }

                // Divide the lower elements by the "winning" diagonal element.
                final T luDiag = mLU.get(col, col);
                for (int row = col + 1; row < m; row++) {
                    mLU.set(row, col, field.divide(mLU.get(row, col),
                                                   luDiag));
                }
            }
        }

        isSingular = singular;
        isEven = even;
    }

    /**
     * Factory method.
     *
     * @param <T> Type of the field elements.
     * @param m Matrix to decompose.
     * @return a new instance.
     */
    public static <T> FieldLUDecomposition<T> of(FieldDenseMatrix<T> m) {
        return new FieldLUDecomposition<>(m);
    }

    /**
     * @return {@code true} if the matrix is singular.
     */
    public boolean isSingular() {
        return isSingular;
    }

    /**
     * Builds the "L" matrix of the decomposition.
     *
     * @return the lower triangular matrix.
     * @throws SingularMatrixException if the matrix is singular.
     */
    public FieldDenseMatrix<T> getL() {
        if (isSingular) {
            throw new SingularMatrixException();
        }

        final int m = pivot.length;
        final FieldDenseMatrix<T> mL = FieldDenseMatrix.zero(field, m, m);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < i; j++) {
                mL.set(i, j, mLU.get(i, j));
            }
            mL.set(i, i, field.one());
        }

        return mL;
    }

    /**
     * Builds the "U" matrix of the decomposition.
     *
     * @return the upper triangular matrix.
     * @throws SingularMatrixException if the matrix is singular.
     */
    public FieldDenseMatrix<T> getU() {
        if (isSingular) {
            throw new SingularMatrixException();
        }

        final int m = pivot.length;
        final FieldDenseMatrix<T> mU = FieldDenseMatrix.zero(field, m, m);
        for (int i = 0; i < m; i++) {
            for (int j = i; j < m; j++) {
                mU.set(i, j, mLU.get(i, j));
            }
        }

        return mU;
    }

    /**
     * Builds the "P" matrix.
     *
     * <p>P is a matrix with exactly one element set to {@link Field#one() one} in
     * each row and each column, all other elements being set to {@link Field#zero() zero}.
     * The positions of the "one" elements are given by the {@link #getPivot()
     * pivot permutation vector}.</p>
     * @return the "P" rows permutation matrix.
     * @throws SingularMatrixException if the matrix is singular.
     *
     * @see #getPivot()
     */
    public FieldDenseMatrix<T> getP() {
        if (isSingular) {
            throw new SingularMatrixException();
        }

        final int m = pivot.length;
        final FieldDenseMatrix<T> mP = FieldDenseMatrix.zero(field, m, m);

        for (int i = 0; i < m; i++) {
            mP.set(i, pivot[i], field.one());
        }

        return mP;
    }

    /**
     * Gets the pivot permutation vector.
     *
     * @return the pivot permutation vector.
     *
     * @see #getP()
     */
    public int[] getPivot() {
        return pivot.clone();
    }

    /**
     * Return the determinant of the matrix.
     * @return determinant of the matrix
     */
    public T getDeterminant() {
        if (isSingular) {
            return field.zero();
        } else {
            final int m = pivot.length;
            T determinant = isEven ?
                field.one() :
                field.negate(field.one());

            for (int i = 0; i < m; i++) {
                determinant = field.multiply(determinant,
                                             mLU.get(i, i));
            }

            return determinant;
        }
    }

    /**
     * Creates a solver for finding the solution {@code X} of the linear
     * system of equations {@code A X = B}.
     *
     * @return a solver.
     * @throws SingularMatrixException if the matrix is singular.
     */
    public FieldDecompositionSolver<T> getSolver() {
        if (isSingular) {
            throw new SingularMatrixException();
        }

        return new Solver<>(mLU, pivot);
    }

    /**
     * Specialized solver.
     *
     * @param <T> Type of the field elements.
     */
    private static final class Solver<T> implements FieldDecompositionSolver<T> {
        /** Field to which the elements belong. */
        private final Field<T> field;
        /** LU decomposition. */
        private final FieldDenseMatrix<T> mLU;
        /** Pivot permutation associated with LU decomposition. */
        private final int[] pivot;

        /**
         * Builds a solver from a LU-decomposed matrix.
         *
         * @param mLU LU matrix.
         * @param pivot Pivot permutation associated with the decomposition.
         */
        private Solver(final FieldDenseMatrix<T> mLU,
                       final int[] pivot) {
            field = mLU.getField();
            this.mLU = mLU.copy();
            this.pivot = pivot.clone();
        }

        /** {@inheritDoc} */
        @Override
        public FieldDenseMatrix<T> solve(final FieldDenseMatrix<T> b) {
            mLU.checkMultiply(b);

            final FieldDenseMatrix<T> bp = b.copy();
            final int nColB = b.getColumnDimension();
            final int m = pivot.length;

            // Apply permutations.
            for (int row = 0; row < m; row++) {
                final int pRow = pivot[row];
                for (int col = 0; col < nColB; col++) {
                    bp.set(row, col,
                           b.get(row, col));
                }
            }

            // Solve LY = b
            for (int col = 0; col < m; col++) {
                for (int i = col + 1; i < m; i++) {
                    for (int j = 0; j < nColB; j++) {
                        bp.set(i, j,
                               field.subtract(bp.get(i, j),
                                              field.multiply(bp.get(col, j),
                                                             mLU.get(i, col))));
                    }
                }
            }

            // Solve UX = Y
            for (int col = m - 1; col >= 0; col--) {
                for (int j = 0; j < nColB; j++) {
                    bp.set(col, j,
                           field.divide(bp.get(col, j),
                                        mLU.get(col, col)));
                }
                for (int i = 0; i < col; i++) {
                    for (int j = 0; j < nColB; j++) {
                        bp.set(i, j,
                               field.subtract(bp.get(i, j),
                                              field.multiply(bp.get(col, j),
                                                             mLU.get(i, col))));
                    }
                }
            }

            return bp;
        }

        /** {@inheritDoc} */
        @Override
        public FieldDenseMatrix<T> getInverse() {
            return solve(FieldDenseMatrix.identity(field, pivot.length));
        }
    }
}
