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
 * Calculates the QR-decomposition of a matrix.
 * <p>The QR-decomposition of a matrix A consists of two matrices Q and R
 * that satisfy: A = QR, Q is orthogonal (Q<sup>T</sup>Q = I), and R is
 * upper triangular. If A is m&times;n, Q is m&times;m and R m&times;n.</p>
 * <p>This class compute the decomposition using Householder reflectors.</p>
 * <p>For efficiency purposes, the decomposition in packed form is transposed.
 * This allows inner loop to iterate inside rows, which is much more cache-efficient
 * in Java.</p>
 *
 * @see <a href="http://mathworld.wolfram.com/QRDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/QR_decomposition">Wikipedia</a>
 *
 * @version $Revision$ $Date$
 * @since 1.2
 */
public class QRDecompositionImpl implements QRDecomposition {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 7560093145655650408L;

    /**
     * A packed TRANSPOSED representation of the QR decomposition.
     * <p>The elements BELOW the diagonal are the elements of the UPPER triangular
     * matrix R, and the rows ABOVE the diagonal are the Householder reflector vectors
     * from which an explicit form of Q can be recomputed if desired.</p>
     */
    private double[][] qrt;

    /** The diagonal elements of R. */
    private double[] rDiag;

    /** Cached value of Q. */
    private RealMatrix cachedQ;

    /** Cached value of R. */
    private RealMatrix cachedR;

    /** Cached value of H. */
    private RealMatrix cachedH;

    /**
     * Build a new instance.
     * <p>Note that {@link #decompose(RealMatrix)} <strong>must</strong> be called
     * before any of the {@link #getQ()}, {@link #getR()}, {@link #getH()},
     * {@link #isFullRank()}, {@link #solve(double[])}, {@link #solve(RealMatrix)},
     * {@link #solve(RealVector)} or {@link #solve(RealVectorImpl)} methods can be
     * called.</p>
     * @see #decompose(RealMatrix)
     */
    public QRDecompositionImpl() {
    }

    /**
     * Calculates the QR-decomposition of the given matrix. 
     * <p>Calling this constructor is equivalent to first call the no-arguments
     * constructor and then call {@link #decompose(RealMatrix)}.</p>
     * @param matrix The matrix to decompose.
     */
    public QRDecompositionImpl(RealMatrix matrix) {
        decompose(matrix);
    }

    /** {@inheritDoc} */
    public void decompose(RealMatrix matrix) {

        final int m = matrix.getRowDimension();
        final int n = matrix.getColumnDimension();
        qrt = matrix.transpose().getData();
        rDiag = new double[n];
        cachedQ = null;
        cachedR = null;
        cachedH = null;

        /*
         * The QR decomposition of a matrix A is calculated using Householder
         * reflectors by repeating the following operations to each minor
         * A(minor,minor) of A:
         */
        for (int minor = 0; minor < Math.min(m, n); minor++) {

            final double[] qrtMinor = qrt[minor];

            /*
             * Let x be the first column of the minor, and a^2 = |x|^2.
             * x will be in the positions qr[minor][minor] through qr[m][minor].
             * The first column of the transformed minor will be (a,0,0,..)'
             * The sign of a is chosen to be opposite to the sign of the first
             * component of x. Let's find a:
             */
            double xNormSqr = 0;
            for (int row = minor; row < m; row++) {
                final double c = qrtMinor[row];
                xNormSqr += c * c;
            }
            final double a = (qrtMinor[minor] > 0) ? -Math.sqrt(xNormSqr) : Math.sqrt(xNormSqr);
            rDiag[minor] = a;

            if (a != 0.0) {

                /*
                 * Calculate the normalized reflection vector v and transform
                 * the first column. We know the norm of v beforehand: v = x-ae
                 * so |v|^2 = <x-ae,x-ae> = <x,x>-2a<x,e>+a^2<e,e> =
                 * a^2+a^2-2a<x,e> = 2a*(a - <x,e>).
                 * Here <x, e> is now qr[minor][minor].
                 * v = x-ae is stored in the column at qr:
                 */
                qrtMinor[minor] -= a; // now |v|^2 = -2a*(qr[minor][minor])

                /*
                 * Transform the rest of the columns of the minor:
                 * They will be transformed by the matrix H = I-2vv'/|v|^2.
                 * If x is a column vector of the minor, then
                 * Hx = (I-2vv'/|v|^2)x = x-2vv'x/|v|^2 = x - 2<x,v>/|v|^2 v.
                 * Therefore the transformation is easily calculated by
                 * subtracting the column vector (2<x,v>/|v|^2)v from x.
                 *
                 * Let 2<x,v>/|v|^2 = alpha. From above we have
                 * |v|^2 = -2a*(qr[minor][minor]), so
                 * alpha = -<x,v>/(a*qr[minor][minor])
                 */
                for (int col = minor+1; col < n; col++) {
                    final double[] qrtCol = qrt[col];
                    double alpha = 0;
                    for (int row = minor; row < m; row++) {
                        alpha -= qrtCol[row] * qrtMinor[row];
                    }
                    alpha /= a * qrtMinor[minor];

                    // Subtract the column vector alpha*v from x.
                    for (int row = minor; row < m; row++) {
                        qrtCol[row] -= alpha * qrtMinor[row];
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    public RealMatrix getR()
        throws IllegalStateException {

        if (cachedR == null) {

            checkDecomposed();

            // R is supposed to be m x n
            final int n = qrt.length;
            final int m = qrt[0].length;
            double[][] r = new double[m][n];

            // copy the diagonal from rDiag and the upper triangle of qr
            for (int row = Math.min(m, n) - 1; row >= 0; row--) {
                double[] rRow = r[row];
                rRow[row] = rDiag[row];
                for (int col = row + 1; col < n; col++) {
                    rRow[col] = qrt[col][row];
                }
            }

            // cache the matrix for subsequent calls
            cachedR = new RealMatrixImpl(r, false);

        }

        // return the cached matrix
        return cachedR;

    }

    /** {@inheritDoc} */
    public RealMatrix getQ()
        throws IllegalStateException {

        if (cachedQ == null) {

            checkDecomposed();

            // Q is supposed to be m x m
            final int n = qrt.length;
            final int m = qrt[0].length;
            double[][] q = new double[m][m];

            /* 
             * Q = Q1 Q2 ... Q_m, so Q is formed by first constructing Q_m and then 
             * applying the Householder transformations Q_(m-1),Q_(m-2),...,Q1 in 
             * succession to the result 
             */ 
            for (int minor = m - 1; minor >= Math.min(m, n); minor--) {
                q[minor][minor]=1;
            }

            for (int minor = Math.min(m,n)-1; minor >= 0; minor--){
                final double[] qrtMinor = qrt[minor];
                q[minor][minor] = 1;
                if (qrtMinor[minor] != 0.0) {
                    for (int col = minor; col < m; col++) {
                        double alpha = 0;
                        for (int row = minor; row < m; row++) {
                            alpha -= q[row][col] * qrtMinor[row];
                        }
                        alpha /= rDiag[minor] * qrtMinor[minor];

                        for (int row = minor; row < m; row++) {
                            q[row][col] -= alpha * qrtMinor[row];
                        }
                    }
                }
            }

            // cache the matrix for subsequent calls
            cachedQ = new RealMatrixImpl(q, false);

        }

        // return the cached matrix
        return cachedQ;

    }

    /** {@inheritDoc} */
    public RealMatrix getH()
        throws IllegalStateException {

        if (cachedH == null) {

            checkDecomposed();

            final int n = qrt.length;
            final int m = qrt[0].length;
            double[][] hData = new double[m][n];
            for (int i = 0; i < m; ++i) {
                final double[] hDataI = hData[i];
                for (int j = 0; j < Math.min(i + 1, n); ++j) {
                    hDataI[j] = qrt[j][i] / -rDiag[j];
                }
            }

            // cache the matrix for subsequent calls
            cachedH = new RealMatrixImpl(hData, false);

        }

        // return the cached matrix
        return cachedH;

    }

    /** {@inheritDoc} */
    public boolean isFullRank()
        throws IllegalStateException {

        checkDecomposed();

        for (double diag : rDiag) {
            if (diag == 0) {
                return false;
            }
        }
        return true;

    }

    /** {@inheritDoc} */
    public double[] solve(double[] b)
        throws IllegalStateException, IllegalArgumentException, InvalidMatrixException {
 
        checkDecomposed();

        final int n = qrt.length;
        final int m = qrt[0].length;
        if (b.length != m) {
            throw new IllegalArgumentException("Incorrect row dimension");
        }
        if (!isFullRank()) {
            throw new InvalidMatrixException("Matrix is rank-deficient");
        }

        final double[] x = new double[n];
        final double[] y = b.clone();

        // apply Householder transforms to solve Q.y = b
        for (int minor = 0; minor < Math.min(m, n); minor++) {

            final double[] qrtMinor = qrt[minor];
            double dotProduct = 0;
            for (int row = minor; row < m; row++) {
                dotProduct += y[row] * qrtMinor[row];
            }
            dotProduct /= rDiag[minor] * qrtMinor[minor];

            for (int row = minor; row < m; row++) {
                y[row] += dotProduct * qrtMinor[row];
            }

        }

        // solve triangular system R.x = y
        for (int row = n - 1; row >= 0; --row) {
            y[row] /= rDiag[row];
            final double yRow   = y[row];
            final double[] qrtRow = qrt[row];
            x[row] = yRow;
            for (int i = 0; i < row; i++) {
                y[i] -= yRow * qrtRow[i];
            }
        }

        return x;

    }

    /** {@inheritDoc} */
    public RealVector solve(RealVector b)
        throws IllegalStateException, IllegalArgumentException, InvalidMatrixException {
        try {
            return solve((RealVectorImpl) b);
        } catch (ClassCastException cce) {
            checkDecomposed();
            return new RealVectorImpl(solve(b.getData()), false);
        }
    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It is </p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalStateException if {@link #decompose(RealMatrix) decompose}
     * has not been called
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

        final int n = qrt.length;
        final int m = qrt[0].length;
        if (b.getRowDimension() != m) {
            throw new IllegalArgumentException("Incorrect row dimension");
        }
        if (!isFullRank()) {
            throw new InvalidMatrixException("Matrix is rank-deficient");
        }

        final int cols = b.getColumnDimension();
        final double[][] xData = new double[n][cols];
        final double[] y = new double[b.getRowDimension()];

        for (int k = 0; k < cols; ++k) {

            // get the right hand side vector
            for (int j = 0; j < y.length; ++j) {
                y[j] = b.getEntry(j, k);
            }

            // apply Householder transforms to solve Q.y = b
            for (int minor = 0; minor < Math.min(m, n); minor++) {

                final double[] qrtMinor = qrt[minor];
                double dotProduct = 0;
                for (int row = minor; row < m; row++) {
                    dotProduct += y[row] * qrtMinor[row];
                }
                dotProduct /= rDiag[minor] * qrtMinor[minor];

                for (int row = minor; row < m; row++) {
                    y[row] += dotProduct * qrtMinor[row];
                }

            }

            // solve triangular system R.x = y
            for (int row = n - 1; row >= 0; --row) {
                y[row] /= rDiag[row];
                final double yRow = y[row];
                final double[] qrtRow = qrt[row];
                xData[row][k] = yRow;
                for (int i = 0; i < row; i++) {
                   y[i] -= yRow * qrtRow[i];
                }
             }

        }

        return new RealMatrixImpl(xData, false);

    }

    /**
     * Check if {@link #decompose(RealMatrix)} has been called.
     * @exception IllegalStateException if {@link #decompose(RealMatrix) decompose}
     * has not been called
     */
    private void checkDecomposed()
        throws IllegalStateException {
        if (qrt == null) {
            throw new IllegalStateException("no matrix have been decomposed yet");
        }
    }

}
