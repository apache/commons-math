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
    private static final long serialVersionUID = -9052751605297201067L;

    /** Entries of LU decomposition. */
    private double lu[][];

    /** Pivot permutation associated with LU decomposition */
    private int[] pivot;

    /** Parity of the permutation associated with the LU decomposition */
    private int parity;

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
     * Build a new instance.
     * <p>Note that either {@link #decompose(RealMatrix)} or
     * {@link #decompose(RealMatrix, double)} <strong>must</strong> be called
     * before any of the {@link #getP()}, {@link #getPivot()}, {@link #getL()},
     * {@link #getU()}, {@link #getDeterminant()}, {@link #isNonSingular()},
     * {@link #solve(double[])}, {@link #solve(RealMatrix)}, {@link #solve(RealVector)}
     * or {@link #solve(RealVectorImpl)} methods can be called.</p>
     * @see #decompose(RealMatrix)
     * @see #decompose(RealMatrix, double)
     */
    public LUDecompositionImpl() {
    }

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
            throw new InvalidMatrixException("LU decomposition requires that the matrix be square.");
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
        parity = 1;
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
                parity = -parity;
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
        checkDecomposed();
        if ((cachedL == null) && !singular) {
            final int m = pivot.length;
            final double[][] lData = new double[m][m];
            for (int i = 0; i < m; ++i) {
                System.arraycopy(lu[i], 0, lData[i], 0, i);
                lData[i][i] = 1.0;
            }
            cachedL = new RealMatrixImpl(lData, false);
        }
        return cachedL;
    }

    /** {@inheritDoc} */
    public RealMatrix getU()
        throws IllegalStateException {
        checkDecomposed();
        if ((cachedU == null) && !singular) {
            final int m = pivot.length;
            final double[][] uData = new double[m][m];
            for (int i = 0; i < m; ++i) {
                System.arraycopy(lu[i], i, uData[i], i, m - i);
            }
            cachedU = new RealMatrixImpl(uData, false);
        }
        return cachedU;
    }

    /** {@inheritDoc} */
    public RealMatrix getP()
        throws IllegalStateException {
        checkDecomposed();
        if ((cachedP == null) && !singular) {
            final int m = pivot.length;
            final double[][] pData = new double[m][m];
            for (int i = 0; i < m; ++i) {
                pData[i][pivot[i]] = 1.0;
            }
            cachedP = new RealMatrixImpl(pData, false);
        }
        return cachedP;
    }

    /** {@inheritDoc} */
    public int[] getPivot()
        throws IllegalStateException {
        checkDecomposed();
        return pivot.clone();
    }

    /** {@inheritDoc} */
    public boolean isNonSingular()
        throws IllegalStateException {
        checkDecomposed();
        return !singular;
    }

    /** {@inheritDoc} */
    public double getDeterminant()
        throws IllegalStateException {
        checkDecomposed();
        if (singular) {
            return 0;
        } else {
            final int m = pivot.length;
            double determinant = parity;
            for (int i = 0; i < m; i++) {
                determinant *= lu[i][i];
            }
            return determinant;
        }
    }

    /** {@inheritDoc} */
    public double[] solve(double[] b)
        throws IllegalStateException, IllegalArgumentException, InvalidMatrixException {

        checkDecomposed();
        final int m = pivot.length;
        if (b.length != m) {
            throw new IllegalArgumentException("constant vector has wrong length");
        }
        if (singular) {
            throw new InvalidMatrixException("Matrix is singular.");
        }

        final double[] bp = new double[m];

        // Apply permutations to b
        for (int row = 0; row < m; row++) {
            bp[row] = b[pivot[row]];
        }

        // Solve LY = b
        for (int col = 0; col < m; col++) {
            for (int i = col + 1; i < m; i++) {
                bp[i] -= bp[col] * lu[i][col];
            }
        }

        // Solve UX = Y
        for (int col = m - 1; col >= 0; col--) {
            bp[col] /= lu[col][col];
            for (int i = 0; i < col; i++) {
                bp[i] -= bp[col] * lu[i][col];
            }
        }

        return bp;

    }

    /** {@inheritDoc} */
    public RealVector solve(RealVector b)
        throws IllegalStateException, IllegalArgumentException, InvalidMatrixException {
        try {
            return solve((RealVectorImpl) b);
        } catch (ClassCastException cce) {

            checkDecomposed();
            final int m = pivot.length;
            if (b.getDimension() != m) {
                throw new IllegalArgumentException("constant vector has wrong length");
            }
            if (singular) {
                throw new InvalidMatrixException("Matrix is singular.");
            }

            final double[] bp = new double[m];

            // Apply permutations to b
            for (int row = 0; row < m; row++) {
                bp[row] = b.getEntry(pivot[row]);
            }

            // Solve LY = b
            for (int col = 0; col < m; col++) {
                for (int i = col + 1; i < m; i++) {
                    bp[i] -= bp[col] * lu[i][col];
                }
            }

            // Solve UX = Y
            for (int col = m - 1; col >= 0; col--) {
                bp[col] /= lu[col][col];
                for (int i = 0; i < col; i++) {
                    bp[i] -= bp[col] * lu[i][col];
                }
            }

            return new RealVectorImpl(bp, false);

        }
    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It is </p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X such that A &times; X = B
     * @throws IllegalArgumentException if matrices dimensions don't match
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealVectorImpl solve(RealVectorImpl b)
        throws IllegalStateException, IllegalArgumentException, InvalidMatrixException {
        return new RealVectorImpl(solve(b.getDataRef()), false);
    }

    /** {@inheritDoc} */
    public RealMatrix solve(RealMatrix b)
        throws IllegalStateException, IllegalArgumentException, InvalidMatrixException {

        checkDecomposed();
        final int m = pivot.length;
        if (b.getRowDimension() != m) {
            throw new IllegalArgumentException("Incorrect row dimension");
        }
        if (singular) {
            throw new InvalidMatrixException("Matrix is singular.");
        }

        final int nColB = b.getColumnDimension();

        // Apply permutations to b
        final double[][] bp = new double[m][nColB];
        for (int row = 0; row < m; row++) {
            final double[] bpRow = bp[row];
            final int pRow = pivot[row];
            for (int col = 0; col < nColB; col++) {
                bpRow[col] = b.getEntry(pRow, col);
            }
        }

        // Solve LY = b
        for (int col = 0; col < m; col++) {
            final double[] bpCol = bp[col];
            for (int i = col + 1; i < m; i++) {
                final double[] bpI = bp[i];
                final double luICol = lu[i][col];
                for (int j = 0; j < nColB; j++) {
                    bpI[j] -= bpCol[j] * luICol;
                }
            }
        }

        // Solve UX = Y
        for (int col = m - 1; col >= 0; col--) {
            final double[] bpCol = bp[col];
            final double luDiag = lu[col][col];
            for (int j = 0; j < nColB; j++) {
                bpCol[j] /= luDiag;
            }
            for (int i = 0; i < col; i++) {
                final double[] bpI = bp[i];
                final double luICol = lu[i][col];
                for (int j = 0; j < nColB; j++) {
                    bpI[j] -= bpCol[j] * luICol;
                }
            }
        }

        return new RealMatrixImpl(bp, false);

    }

    /**
     * Check if either {@link #decompose(RealMatrix)} or {@link
     * #decompose(RealMatrix, double) has been called.
     * @exception IllegalStateException if {@link #decompose(RealMatrix) decompose}
     * has not been called
     */
    private void checkDecomposed()
        throws IllegalStateException {
        if (lu == null) {
            throw new IllegalStateException("no matrix have been decomposed yet");
        }
    }

}
