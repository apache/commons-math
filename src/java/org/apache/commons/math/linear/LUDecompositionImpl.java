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


/**
 * Calculates the LUP-decomposition of a square matrix.
 * <p>The LUP-decomposition of a matrix A consists of three matrices
 * L, U and P that satisfy: A = LUP, L is lower triangular, and U is
 * upper triangular and P is a permutation matrix. All matrices are
 * m&times;m.</p>
 * <p>As shown by the presence of the P matrix, this decomposition is
 * implemented using partial pivoting.</p>
 *
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class LUDecompositionImpl implements LUDecomposition {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 3446121671437672843L;

    /** Entries of LU decomposition. */
    private double lu[][];

    /** Pivot permutation associated with LU decomposition */
    private int[] pivot;

    /** Parity of the permutation associated with the LU decomposition */
    private boolean even;

    /** Singularity indicator. */
    private boolean singular;

    /** Cached value of L. */
    private RealMatrix cachedL;

    /** Cached value of U. */
    private RealMatrix cachedU;

    /** Cached value of P. */
    private RealMatrix cachedP;

    /** Default bound to determine effective singularity in LU decomposition */
    private static final double DEFAULT_TOO_SMALL = 10E-12;

    /**
     * Calculates the LU-decomposition of the given matrix. 
     * <p>Calling this constructor is equivalent to first call the no-arguments
     * constructor and then call {@link #decompose(RealMatrix)}.</p>
     * @param matrix The matrix to decompose.
     * @exception InvalidMatrixException if matrix is not square
     */
    public LUDecompositionImpl(RealMatrix matrix)
        throws InvalidMatrixException {
        decompose(matrix);
    }

    /**
     * Calculates the LU-decomposition of the given matrix. 
     * <p>Calling this constructor is equivalent to first call the no-arguments
     * constructor and then call {@link #decompose(RealMatrix, double)}.</p>
     * @param matrix The matrix to decompose.
     * @param singularityThreshold threshold (based on partial row norm)
     * under which a matrix is considered singular
     * @exception InvalidMatrixException if matrix is not square
     */
    public LUDecompositionImpl(RealMatrix matrix, double singularityThreshold)
        throws InvalidMatrixException {
        decompose(matrix, singularityThreshold);
    }

    /** {@inheritDoc} */
    public void decompose(RealMatrix matrix)
        throws InvalidMatrixException {
        decompose(matrix, DEFAULT_TOO_SMALL);
    }

    /** {@inheritDoc} */
    public void decompose(RealMatrix matrix, double singularityThreshold)
        throws InvalidMatrixException {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        final int m = matrix.getColumnDimension();
        lu = matrix.getData();
        pivot = new int[m];
        cachedL = null;
        cachedU = null;
        cachedP = null;

        // Initialize permutation array and parity
        for (int row = 0; row < m; row++) {
            pivot[row] = row;
        }
        even     = true;
        singular = false;

        // Loop over columns
        for (int col = 0; col < m; col++) {

            double sum = 0;

            // upper
            for (int row = 0; row < col; row++) {
                final double[] luRow = lu[row];
                sum = luRow[col];
                for (int i = 0; i < row; i++) {
                    sum -= luRow[i] * lu[i][col];
                }
                luRow[col] = sum;
            }

            // lower
            int max = col; // permutation row
            double largest = Double.NEGATIVE_INFINITY;
            for (int row = col; row < m; row++) {
                final double[] luRow = lu[row];
                sum = luRow[col];
                for (int i = 0; i < col; i++) {
                    sum -= luRow[i] * lu[i][col];
                }
                luRow[col] = sum;

                // maintain best permutation choice
                if (Math.abs(sum) > largest) {
                    largest = Math.abs(sum);
                    max = row;
                }
            }

            // Singularity check
            if (Math.abs(lu[max][col]) < singularityThreshold) {
                singular = true;
                return;
            }

            // Pivot if necessary
            if (max != col) {
                double tmp = 0;
                for (int i = 0; i < m; i++) {
                    tmp = lu[max][i];
                    lu[max][i] = lu[col][i];
                    lu[col][i] = tmp;
                }
                int temp = pivot[max];
                pivot[max] = pivot[col];
                pivot[col] = temp;
                even = !even;
            }

            // Divide the lower elements by the "winning" diagonal elt.
            final double luDiag = lu[col][col];
            for (int row = col + 1; row < m; row++) {
                lu[row][col] /= luDiag;
            }
        }

    }

    /** {@inheritDoc} */
    public RealMatrix getL()
        throws IllegalStateException {
        if ((cachedL == null) && !singular) {
            final int m = pivot.length;
            cachedL = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; ++i) {
                final double[] luI = lu[i];
                for (int j = 0; j < i; ++j) {
                    cachedL.setEntry(i, j, luI[j]);
                }
                cachedL.setEntry(i, i, 1.0);
            }
        }
        return cachedL;
    }

    /** {@inheritDoc} */
    public RealMatrix getU()
        throws IllegalStateException {
        if ((cachedU == null) && !singular) {
            final int m = pivot.length;
            cachedU = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; ++i) {
                final double[] luI = lu[i];
                for (int j = i; j < m; ++j) {
                    cachedU.setEntry(i, j, luI[j]);
                }
            }
        }
        return cachedU;
    }

    /** {@inheritDoc} */
    public RealMatrix getP()
        throws IllegalStateException {
        if ((cachedP == null) && !singular) {
            final int m = pivot.length;
            cachedP = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; ++i) {
                cachedP.setEntry(i, pivot[i], 1.0);
            }
        }
        return cachedP;
    }

    /** {@inheritDoc} */
    public int[] getPivot()
        throws IllegalStateException {
        return pivot;
    }

    /** {@inheritDoc} */
    public boolean evenPermutation() {
        return even;
    }

    /** {@inheritDoc} */
    public boolean isSingular()
        throws IllegalStateException {
        return singular;
    }

}
