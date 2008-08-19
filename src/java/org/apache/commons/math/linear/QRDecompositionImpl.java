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
 * Calculates the QR-decomposition of a matrix. In the QR-decomposition of
 * a matrix A consists of two matrices Q and R that satisfy: A = QR, Q is
 * orthogonal (Q<sup>T</sup>Q = I), and R is upper triangular. If A is
 * m&times;n, Q is m&times;m and R m&times;n. 
 * <p>
 * Implemented using Householder reflectors.</p>
 *
 * @see <a href="http://mathworld.wolfram.com/QRDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/QR_decomposition">Wikipedia</a>
 * 
 * @version $Revision$ $Date$
 * @since 1.2
 */
public class QRDecompositionImpl implements QRDecomposition {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 3965943878043764074L;

    /**
     * A packed representation of the QR decomposition. The elements above the 
     * diagonal are the elements of R, and the columns of the lower triangle 
     * are the Householder reflector vectors of which an explicit form of Q can
     * be calculated. 
     */
    private final double[][] qr;

    /**
     * The diagonal elements of R.
     */
    private final double[] rDiag;

    /** Cached value of Q. */
    private RealMatrix cachedQ;

    /** Cached value of R. */
    private RealMatrix cachedR;

    /** Cached value of H. */
    private RealMatrix cachedH;

    /**
     * The row dimension of the given matrix. The size of Q will be m x m, the 
     * size of R will be m x n. 
     */
    private final int m;

    /**
     * The column dimension of the given matrix. The size of R will be m x n. 
     */
    private final int n;

    /**
     * Calculates the QR decomposition of the given matrix. 
     * 
     * @param matrix The matrix to decompose.
     */
    public QRDecompositionImpl(RealMatrix matrix) {
        m = matrix.getRowDimension();
        n = matrix.getColumnDimension();
        qr = matrix.getData();
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
            /*
             * Let x be the first column of the minor, and a^2 = |x|^2.
             * x will be in the positions qr[minor][minor] through qr[m][minor].
             * The first column of the transformed minor will be (a,0,0,..)'
             * The sign of a is chosen to be opposite to the sign of the first
             * component of x. Let's find a:
             */
            double xNormSqr = 0;
            for (int row = minor; row < m; row++) {
                final double c = qr[row][minor];
                xNormSqr += c * c;
            }
            final double a = (qr[minor][minor] > 0) ? -Math.sqrt(xNormSqr) : Math.sqrt(xNormSqr);
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
                qr[minor][minor] -= a; // now |v|^2 = -2a*(qr[minor][minor])

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
                for (int col = minor + 1; col < n; col++) {
                    double alpha = 0;
                    for (int row = minor; row < m; row++) {
                        final double[] qrRow = qr[row];
                        alpha -= qrRow[col] * qrRow[minor];
                    }
                    alpha /= a * qr[minor][minor];

                    // Subtract the column vector alpha*v from x.
                    for (int row = minor; row < m; row++) {
                        final double[] qrRow = qr[row];
                        qrRow[col] -= alpha * qrRow[minor];
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    public RealMatrix getR() {

        if (cachedR == null) {

            // R is supposed to be m x n
            double[][] r = new double[m][n];

            // copy the diagonal from rDiag and the upper triangle of qr
            for (int row = Math.min(m,n)-1; row >= 0; row--) {
                final double[] rRow = r[row];
                rRow[row] = rDiag[row];
                System.arraycopy(qr[row], row + 1, rRow, row + 1, n - row - 1);
            }

            // cache the matrix for subsequent calls
            cachedR = new RealMatrixImpl(r, false);

        }

        // return the cached matrix
        return cachedR;

    }

    /** {@inheritDoc} */
    public RealMatrix getQ() {

        if (cachedQ == null) {

            // Q is supposed to be m x m
            double[][] Q = new double[m][m];

            /* 
             * Q = Q1 Q2 ... Q_m, so Q is formed by first constructing Q_m and then 
             * applying the Householder transformations Q_(m-1),Q_(m-2),...,Q1 in 
             * succession to the result 
             */ 
            for (int minor = m-1; minor >= Math.min(m,n); minor--) {
                Q[minor][minor]=1;
            }

            for (int minor = Math.min(m,n)-1; minor >= 0; minor--){
                Q[minor][minor] = 1;
                if (qr[minor][minor] != 0.0) {
                    for (int col = minor; col < m; col++) {
                        double alpha = 0;
                        for (int row = minor; row < m; row++) {
                            alpha -= Q[row][col] * qr[row][minor];
                        }
                        alpha /= rDiag[minor]*qr[minor][minor];

                        for (int row = minor; row < m; row++) {
                            Q[row][col] -= alpha*qr[row][minor];
                        }
                    }
                }
            }

            // cache the matrix for subsequent calls
            cachedQ = new RealMatrixImpl(Q, false);

        }

        // return the cached matrix
        return cachedQ;

    }

    /** {@inheritDoc} */
    public RealMatrix getH() {
        if (cachedH == null) {

            double[][] hData = new double[m][n];
            for (int i = 0; i < m; ++i) {
                for (int j = 0; j < Math.min(i + 1, n); ++j) {
                    hData[i][j] = qr[i][j] / -rDiag[j];
                }
            }

            // cache the matrix for subsequent calls
            cachedH = new RealMatrixImpl(hData, false);

        }

        // return the cached matrix
        return cachedH;

    }

    /** {@inheritDoc} */
    public boolean isFullRank() {
        for (double diag : rDiag) {
            if (diag == 0) {
                return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    public double[] solve(double[] b)
        throws IllegalArgumentException, InvalidMatrixException {
 
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

                double dotProduct = 0;
                for (int row = minor; row < m; row++) {
                    dotProduct += y[row] * qr[row][minor];
                }
                dotProduct /= rDiag[minor] * qr[minor][minor];

                for (int row = minor; row < m; row++) {
                    y[row] += dotProduct * qr[row][minor];
                }

            }

            // solve triangular system R.x = y
            for (int row = n - 1; row >= 0; --row) {
                y[row] /= rDiag[row];
                final double yRow = y[row];
                x[row] = yRow;
                for (int i = 0; i < row; i++) {
                    y[i] -= yRow * qr[i][row];
                }
            }

            return x;

    }

    /** {@inheritDoc} */
    public RealVector solve(RealVector b)
        throws IllegalArgumentException, InvalidMatrixException {
        try {
            return solve((RealVectorImpl) b);
        } catch (ClassCastException cce) {
            return new RealVectorImpl(solve(b.getData()), false);
        }
    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It is </p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @throws IllegalArgumentException if matrices dimensions don't match
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealVectorImpl solve(RealVectorImpl b)
        throws IllegalArgumentException, InvalidMatrixException {
        return new RealVectorImpl(solve(b.getDataRef()), false);
    }

    /** {@inheritDoc} */
    public RealMatrix solve(RealMatrix b)
        throws IllegalArgumentException, InvalidMatrixException {

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

                double dotProduct = 0;
                for (int row = minor; row < m; row++) {
                    dotProduct += y[row] * qr[row][minor];
                }
                dotProduct /= rDiag[minor] * qr[minor][minor];

                for (int row = minor; row < m; row++) {
                    y[row] += dotProduct * qr[row][minor];
                }

            }

            // solve triangular system R.x = y
            for (int row = n - 1; row >= 0; --row) {
                y[row] /= rDiag[row];
                final double yRow = y[row];
                xData[row][k] = yRow;
                for (int i = 0; i < row; i++) {
                   y[i] -= yRow * qr[i][row];
                }
             }

        }

        return new RealMatrixImpl(xData, false);

    }

}
