/*
 * Copyright 2011 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.Arrays;
import org.apache.commons.math.util.MathArrays;
import org.apache.commons.math.exception.ConvergenceException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 *
 * @author gregsterijevski
 */
public class PivotingQRDecomposition {

    private double[][] qr;
    /** The diagonal elements of R. */
    private double[] rDiag;
    /** Cached value of Q. */
    private RealMatrix cachedQ;
    /** Cached value of QT. */
    private RealMatrix cachedQT;
    /** Cached value of R. */
    private RealMatrix cachedR;
    /** Cached value of H. */
    private RealMatrix cachedH;
    /** permutation info */
    private int[] permutation;
    /** the rank **/
    private int rank;
    /** vector of column multipliers */
    private double[] beta;

    public boolean isSingular() {
        return rank != qr[0].length;
    }

    public int getRank() {
        return rank;
    }

    public int[] getOrder() {
        return MathArrays.copyOf(permutation);
    }

    public PivotingQRDecomposition(RealMatrix matrix) throws ConvergenceException {
        this(matrix, 1.0e-16, true);
    }

    public PivotingQRDecomposition(RealMatrix matrix, boolean allowPivot) throws ConvergenceException {
        this(matrix, 1.0e-16, allowPivot);
    }

    public PivotingQRDecomposition(RealMatrix matrix, double qrRankingThreshold,
            boolean allowPivot) throws ConvergenceException {
        final int rows = matrix.getRowDimension();
        final int cols = matrix.getColumnDimension();
        qr = matrix.getData();
        rDiag = new double[cols];
        //final double[] norms = new double[cols];
        this.beta = new double[cols];
        this.permutation = new int[cols];
        cachedQ = null;
        cachedQT = null;
        cachedR = null;
        cachedH = null;

        /*- initialize the permutation vector and calculate the norms */
        for (int k = 0; k < cols; ++k) {
            permutation[k] = k;
        }
        // transform the matrix column after column
        for (int k = 0; k < cols; ++k) {
            // select the column with the greatest norm on active components
            int nextColumn = -1;
            double ak2 = Double.NEGATIVE_INFINITY;
            if (allowPivot) {
                for (int i = k; i < cols; ++i) {
                    double norm2 = 0;
                    for (int j = k; j < rows; ++j) {
                        final double aki = qr[j][permutation[i]];
                        norm2 += aki * aki;
                    }
                    if (Double.isInfinite(norm2) || Double.isNaN(norm2)) {
                        throw new ConvergenceException(LocalizedFormats.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN,
                                rows, cols);
                    }
                    if (norm2 > ak2) {
                        nextColumn = i;
                        ak2 = norm2;
                    }
                }
            } else {
                nextColumn = k;
                ak2 = 0.0;
                for (int j = k; j < rows; ++j) {
                    final double aki = qr[j][k];
                    ak2 += aki * aki;
                }
            }
            if (ak2 <= qrRankingThreshold) {
                rank = k;
                for (int i = rank; i < rows; i++) {
                    for (int j = i + 1; j < cols; j++) {
                        qr[i][permutation[j]] = 0.0;
                    }
                }
                return;
            }
            final int pk = permutation[nextColumn];
            permutation[nextColumn] = permutation[k];
            permutation[k] = pk;

            // choose alpha such that Hk.u = alpha ek
            final double akk = qr[k][pk];
            final double alpha = (akk > 0) ? -FastMath.sqrt(ak2) : FastMath.sqrt(ak2);
            final double betak = 1.0 / (ak2 - akk * alpha);
            beta[pk] = betak;

            // transform the current column
            rDiag[pk] = alpha;
            qr[k][pk] -= alpha;

            // transform the remaining columns
            for (int dk = cols - 1 - k; dk > 0; --dk) {
                double gamma = 0;
                for (int j = k; j < rows; ++j) {
                    gamma += qr[j][pk] * qr[j][permutation[k + dk]];
                }
                gamma *= betak;
                for (int j = k; j < rows; ++j) {
                    qr[j][permutation[k + dk]] -= gamma * qr[j][pk];
                }
            }
        }
        rank = cols;
        return;
    }

    /**
     * Returns the matrix Q of the decomposition.
     * <p>Q is an orthogonal matrix</p>
     * @return the Q matrix
     */
    public RealMatrix getQ() {
        if (cachedQ == null) {
            cachedQ = getQT().transpose();
        }
        return cachedQ;
    }

    /**
     * Returns the transpose of the matrix Q of the decomposition.
     * <p>Q is an orthogonal matrix</p>
     * @return the Q matrix
     */
    public RealMatrix getQT() {
        if (cachedQT == null) {

            // QT is supposed to be m x m
            final int m = qr.length;
            cachedQT = MatrixUtils.createRealMatrix(m, m);

            /*
             * Q = Q1 Q2 ... Q_m, so Q is formed by first constructing Q_m and then
             * applying the Householder transformations Q_(m-1),Q_(m-2),...,Q1 in
             * succession to the result
             */
            for (int minor = m - 1; minor >= rank; minor--) {
                cachedQT.setEntry(minor, minor, 1.0);
            }

            for (int minor = rank - 1; minor >= 0; minor--) {
                //final double[] qrtMinor = qrt[minor];
                final int p_minor = permutation[minor];
                cachedQT.setEntry(minor, minor, 1.0);
                //if (qrtMinor[minor] != 0.0) {
                for (int col = minor; col < m; col++) {
                    double alpha = 0.0;
                    for (int row = minor; row < m; row++) {
                        alpha -= cachedQT.getEntry(col, row) * qr[row][p_minor];
                    }
                    alpha /= rDiag[p_minor] * qr[minor][p_minor];
                    for (int row = minor; row < m; row++) {
                        cachedQT.addToEntry(col, row, -alpha * qr[row][p_minor]);
                    }
                }
                //}
            }
        }
        // return the cached matrix
        return cachedQT;
    }

    /**
     * Returns the matrix R of the decomposition.
     * <p>R is an upper-triangular matrix</p>
     * @return the R matrix
     */
    public RealMatrix getR() {
        if (cachedR == null) {
            // R is supposed to be m x n
            final int n = qr[0].length;
            final int m = qr.length;
            cachedR = MatrixUtils.createRealMatrix(m, n);
            // copy the diagonal from rDiag and the upper triangle of qr
            for (int row = rank - 1; row >= 0; row--) {
                cachedR.setEntry(row, row, rDiag[permutation[row]]);
                for (int col = row + 1; col < n; col++) {
                    cachedR.setEntry(row, col, qr[row][permutation[col]]);
                }
            }
        }
        // return the cached matrix
        return cachedR;
    }

    public RealMatrix getH() {
        if (cachedH == null) {
            final int n = qr[0].length;
            final int m = qr.length;
            cachedH = MatrixUtils.createRealMatrix(m, n);
            for (int i = 0; i < m; ++i) {
                for (int j = 0; j < FastMath.min(i + 1, n); ++j) {
                    final int p_j = permutation[j];
                    cachedH.setEntry(i, j, qr[i][p_j] / -rDiag[p_j]);
                }
            }
        }
        // return the cached matrix
        return cachedH;
    }

    public RealMatrix getPermutationMatrix() {
        RealMatrix rm = MatrixUtils.createRealMatrix(qr[0].length, qr[0].length);
        for (int i = 0; i < this.qr[0].length; i++) {
            rm.setEntry(permutation[i], i, 1.0);
        }
        return rm;
    }

    public DecompositionSolver getSolver() {
        return new Solver(qr, rDiag, permutation, rank);
    }

    /** Specialized solver. */
    private static class Solver implements DecompositionSolver {

        /**
         * A packed TRANSPOSED representation of the QR decomposition.
         * <p>The elements BELOW the diagonal are the elements of the UPPER triangular
         * matrix R, and the rows ABOVE the diagonal are the Householder reflector vectors
         * from which an explicit form of Q can be recomputed if desired.</p>
         */
        private final double[][] qr;
        /** The diagonal elements of R. */
        private final double[] rDiag;
        /** The rank of the matrix      */
        private final int rank;
        /** The permutation matrix      */
        private final int[] perm;

        /**
         * Build a solver from decomposed matrix.
         * @param qrt packed TRANSPOSED representation of the QR decomposition
         * @param rDiag diagonal elements of R
         */
        private Solver(final double[][] qr, final double[] rDiag, int[] perm, int rank) {
            this.qr = qr;
            this.rDiag = rDiag;
            this.perm = perm;
            this.rank = rank;
        }

        /** {@inheritDoc} */
        public boolean isNonSingular() {
            if (qr.length >= qr[0].length) {
                return rank == qr[0].length;
            } else { //qr.length < qr[0].length
                return rank == qr.length;
            }
        }

        /** {@inheritDoc} */
        public RealVector solve(RealVector b) {
            final int n = qr[0].length;
            final int m = qr.length;
            if (b.getDimension() != m) {
                throw new DimensionMismatchException(b.getDimension(), m);
            }
            if (!isNonSingular()) {
                throw new SingularMatrixException();
            }

            final double[] x = new double[n];
            final double[] y = b.toArray();

            // apply Householder transforms to solve Q.y = b
            for (int minor = 0; minor < rank; minor++) {
                final int m_idx = perm[minor];
                double dotProduct = 0;
                for (int row = minor; row < m; row++) {
                    dotProduct += y[row] * qr[row][m_idx];
                }
                dotProduct /= rDiag[m_idx] * qr[minor][m_idx];
                for (int row = minor; row < m; row++) {
                    y[row] += dotProduct * qr[row][m_idx];
                }
            }
            // solve triangular system R.x = y
            for (int row = rank - 1; row >= 0; --row) {
                final int m_row = perm[row];
                y[row] /= rDiag[m_row];
                final double yRow = y[row];
                //final double[] qrtRow = qrt[row];
                x[perm[row]] = yRow;
                for (int i = 0; i < row; i++) {
                    y[i] -= yRow * qr[i][m_row];
                }
            }
            return new ArrayRealVector(x, false);
        }

        /** {@inheritDoc} */
        public RealMatrix solve(RealMatrix b) {
            final int cols = qr[0].length;
            final int rows = qr.length;
            if (b.getRowDimension() != rows) {
                throw new DimensionMismatchException(b.getRowDimension(), rows);
            }
            if (!isNonSingular()) {
                throw new SingularMatrixException();
            }

            final int columns = b.getColumnDimension();
            final int blockSize = BlockRealMatrix.BLOCK_SIZE;
            final int cBlocks = (columns + blockSize - 1) / blockSize;
            final double[][] xBlocks = BlockRealMatrix.createBlocksLayout(cols, columns);
            final double[][] y = new double[b.getRowDimension()][blockSize];
            final double[] alpha = new double[blockSize];
            //final BlockRealMatrix result = new BlockRealMatrix(cols, columns, xBlocks, false);
            for (int kBlock = 0; kBlock < cBlocks; ++kBlock) {
                final int kStart = kBlock * blockSize;
                final int kEnd = FastMath.min(kStart + blockSize, columns);
                final int kWidth = kEnd - kStart;
                // get the right hand side vector
                b.copySubMatrix(0, rows - 1, kStart, kEnd - 1, y);

                // apply Householder transforms to solve Q.y = b
                for (int minor = 0; minor < rank; minor++) {
                    final int m_idx = perm[minor];
                    final double factor = 1.0 / (rDiag[m_idx] * qr[minor][m_idx]);

                    Arrays.fill(alpha, 0, kWidth, 0.0);
                    for (int row = minor; row < rows; ++row) {
                        final double d = qr[row][m_idx];
                        final double[] yRow = y[row];
                        for (int k = 0; k < kWidth; ++k) {
                            alpha[k] += d * yRow[k];
                        }
                    }
                    for (int k = 0; k < kWidth; ++k) {
                        alpha[k] *= factor;
                    }

                    for (int row = minor; row < rows; ++row) {
                        final double d = qr[row][m_idx];
                        final double[] yRow = y[row];
                        for (int k = 0; k < kWidth; ++k) {
                            yRow[k] += alpha[k] * d;
                        }
                    }
                }

                // solve triangular system R.x = y
                for (int j = rank - 1; j >= 0; --j) {
                    final int jBlock = perm[j] / blockSize; //which block
                    final int jStart = jBlock * blockSize;  // idx of top corner of block in my coord
                    final double factor = 1.0 / rDiag[perm[j]];
                    final double[] yJ = y[j];
                    final double[] xBlock = xBlocks[jBlock * cBlocks + kBlock];
                    int index = (perm[j] - jStart) * kWidth; //to local (block) coordinates
                    for (int k = 0; k < kWidth; ++k) {
                        yJ[k] *= factor;
                        xBlock[index++] = yJ[k];
                    }
                    for (int i = 0; i < j; ++i) {
                        final double rIJ = qr[i][perm[j]];
                        final double[] yI = y[i];
                        for (int k = 0; k < kWidth; ++k) {
                            yI[k] -= yJ[k] * rIJ;
                        }
                    }
                }
            }
            //return result;
            return new BlockRealMatrix(cols, columns, xBlocks, false);
        }

        /** {@inheritDoc} */
        public RealMatrix getInverse() {
            return solve(MatrixUtils.createRealIdentityMatrix(rDiag.length));
        }
    }
}
