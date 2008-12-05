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
 * Solver using LU decomposition to solve A &times; X = B for square matrices A.
 * <p>This class finds only exact linear solution, i.e. when
 * ||A &times; X - B|| is exactly 0.</p>
 *   
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class LUSolver implements DecompositionSolver {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -8775006035077527661L;

    /** Underlying decomposition. */
    private final LUDecomposition decomposition;

    /**
     * Simple constructor.
     * @param decomposition decomposition to use
     */
    public LUSolver(final LUDecomposition decomposition) {
        this.decomposition = decomposition;
    }

    /** Solve the linear equation A &times; X = B for square matrices A.
     * <p>This method only find exact linear solutions, i.e. solutions for
     * which ||A &times; X - B|| is exactly 0.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public double[] solve(final double[] b)
        throws IllegalArgumentException, InvalidMatrixException {

        final int[] pivot = decomposition.getPivot();
        final int m = pivot.length;
        if (b.length != m) {
            throw new IllegalArgumentException("constant vector has wrong length");
        }
        if (decomposition.isSingular()) {
            throw new SingularMatrixException();
        }

        final double[] bp = new double[m];

        // Apply permutations to b
        for (int row = 0; row < m; row++) {
            bp[row] = b[pivot[row]];
        }

        // Solve LY = b
        final RealMatrix l = decomposition.getL();
        for (int col = 0; col < m; col++) {
            for (int i = col + 1; i < m; i++) {
                bp[i] -= bp[col] * l.getEntry(i, col);
            }
        }

        // Solve UX = Y
        final RealMatrix u = decomposition.getU();
        for (int col = m - 1; col >= 0; col--) {
            bp[col] /= u.getEntry(col, col);
            for (int i = 0; i < col; i++) {
                bp[i] -= bp[col] * u.getEntry(i, col);
            }
        }

        return bp;

    }


    /** Solve the linear equation A &times; X = B for square matrices A.
     * <p>This method only find exact linear solutions, i.e. solutions for
     * which ||A &times; X - B|| is exactly 0.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealVector solve(final RealVector b)
        throws IllegalArgumentException, InvalidMatrixException {

        final int[] pivot = decomposition.getPivot();
        final int m = pivot.length;
        if (b.getDimension() != m) {
            throw new IllegalArgumentException("constant vector has wrong length");
        }
        if (decomposition.isSingular()) {
            throw new SingularMatrixException();
        }

        final double[] bp = new double[m];

        // Apply permutations to b
        for (int row = 0; row < m; row++) {
            bp[row] = b.getEntry(pivot[row]);
        }

        // Solve LY = b
        final RealMatrix l = decomposition.getL();
        for (int col = 0; col < m; col++) {
            for (int i = col + 1; i < m; i++) {
                bp[i] -= bp[col] * l.getEntry(i, col);
            }
        }

        // Solve UX = Y
        final RealMatrix u = decomposition.getU();
        for (int col = m - 1; col >= 0; col--) {
            bp[col] /= u.getEntry(col, col);
            for (int i = 0; i < col; i++) {
                bp[i] -= bp[col] * u.getEntry(i, col);
            }
        }

        return new RealVectorImpl(bp, false);
  
    }

    /** Solve the linear equation A &times; X = B for square matrices A.
     * <p>This method only find exact linear solutions, i.e. solutions for
     * which ||A &times; X - B|| is exactly 0.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a matrix X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix solve(final RealMatrix b)
        throws IllegalArgumentException, InvalidMatrixException {

        final int[] pivot = decomposition.getPivot();
        final int m = pivot.length;
        if (b.getRowDimension() != m) {
            throw new IllegalArgumentException("Incorrect row dimension");
        }
        if (decomposition.isSingular()) {
            throw new SingularMatrixException();
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
        final RealMatrix l = decomposition.getL();
        for (int col = 0; col < m; col++) {
            final double[] bpCol = bp[col];
            for (int i = col + 1; i < m; i++) {
                final double[] bpI = bp[i];
                final double luICol = l.getEntry(i, col);
                for (int j = 0; j < nColB; j++) {
                    bpI[j] -= bpCol[j] * luICol;
                }
            }
        }

        // Solve UX = Y
        final RealMatrix u = decomposition.getU();
        for (int col = m - 1; col >= 0; col--) {
            final double[] bpCol = bp[col];
            final double luDiag = u.getEntry(col, col);
            for (int j = 0; j < nColB; j++) {
                bpCol[j] /= luDiag;
            }
            for (int i = 0; i < col; i++) {
                final double[] bpI = bp[i];
                final double luICol = u.getEntry(i, col);
                for (int j = 0; j < nColB; j++) {
                    bpI[j] -= bpCol[j] * luICol;
                }
            }
        }

        return new RealMatrixImpl(bp, false);

    }

    /**
     * Return the determinant of the matrix
     * @return determinant of the matrix
     * @see #isNonSingular()
     */
    public double getDeterminant() {
        if (decomposition.isSingular()) {
            return 0;
        } else {
            final int m = decomposition.getPivot().length;
            final RealMatrix u = decomposition.getU();
            double determinant = decomposition.evenPermutation() ? 1 : -1;
            for (int i = 0; i < m; i++) {
                determinant *= u.getEntry(i, i);
            }
            return determinant;
        }
    }

    /**
     * Check if the decomposed matrix is non-singular.
     * @return true if the decomposed matrix is non-singular
     */
    public boolean isNonSingular() {
        return !decomposition.isSingular();
    }

    /** Get the inverse of the decomposed matrix.
     * @return inverse matrix
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix getInverse()
        throws InvalidMatrixException {
        final int m = decomposition.getPivot().length;
        return solve(MatrixUtils.createRealIdentityMatrix(m));
    }

}
