/*
 * Copyright 2006 The Apache Software Foundation.
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

/**
 * Calculates the QR-decomposition of a matrix. In the QR-decomposition of
 * a matrix A consists of two matrices Q and R that satisfy: A = QR, Q is
 * orthogonal (Q<sup>T</sup>Q = I), and R is upper triangular. If A is
 * m&times;n, Q is m&times;m and R m&times;n. 
 * <p>
 * Implemented using Householder reflectors.
 *
 *
 * @see <a href="http://mathworld.wolfram.com/QRDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/QR_decomposition">Wikipedia</a>
 */
public class QRDecompositionImpl implements QRDecomposition {

    /**
     * A packed representation of the QR decomposition. The elements above the 
     * diagonal are the elements of R, and the columns of the lower triangle 
     * are the Householder reflector vectors of which an explicit form of Q can
     * be calculated. 
     */
    private double[][] qr;

    /**
     * The diagonal elements of R.
     */
    private double[] rDiag;

    /**
     * The row dimension of the given matrix. The size of Q will be m x m, the 
     * size of R will be m x n. 
     */
    private int m;

    /**
     * The column dimension of the given matrix. The size of R will be m x n. 
     */
    private int n;

    /**
     * Calculates the QR decomposition of the given matrix. 
     * 
     * @param matrix The matrix to factorize.
     */
    public QRDecompositionImpl(RealMatrix matrix) {
        m = matrix.getRowDimension();
        n = matrix.getColumnDimension();
        qr = matrix.getData();
        rDiag = new double[n];

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
                xNormSqr += qr[row][minor]*qr[row][minor];
            }
            double a = Math.sqrt(xNormSqr);
            if (qr[minor][minor] > 0) a = -a;
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
                for (int col = minor+1; col < n; col++) {
                    double alpha = 0;
                    for (int row = minor; row < m; row++) {
                        alpha -= qr[row][col]*qr[row][minor];
                    }
                    alpha /= a*qr[minor][minor];

                    // Subtract the column vector alpha*v from x.
                    for (int row = minor; row < m; row++) {
                        qr[row][col] -= alpha*qr[row][minor];
                    }
                }
            }
        }
    }

    /**
     * Returns the matrix R of the QR-decomposition. 
     */
    public RealMatrix getR()
    {
        // R is supposed to be m x n
        RealMatrixImpl ret = new RealMatrixImpl(m,n);
        double[][] r = ret.getDataRef();

        // copy the diagonal from rDiag and the upper triangle of qr
        for (int row = Math.min(m,n)-1; row >= 0; row--) {
            r[row][row] = rDiag[row];
            for (int col = row+1; col < n; col++) {
                r[row][col] = qr[row][col];
            }
        }
        return ret;
    }

    /**
     * Returns the matrix Q of the QR-decomposition.
     */
    public RealMatrix getQ()
    {
        // Q is supposed to be m x m
        RealMatrixImpl ret = new RealMatrixImpl(m,m);
        double[][] Q = ret.getDataRef();

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

        return ret;
    }
}
