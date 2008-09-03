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

import java.io.Serializable;

/**
 * Class transforming any matrix to bi-diagonal shape.
 * <p>Any m &times; n matrix A can be written as the product of three matrices:
 * A = U &times; B &times; V<sup>T</sup> with U an m &times; m orthogonal matrix,
 * B an m &times; n bi-diagonal matrix (lower diagonal if m &lt; n, upper diagonal
 * otherwise), and V an n &times; n orthogonal matrix.</p>
 * <p>Transformation to bi-diagonal shape is often not a goal by itself, but it is
 * an intermediate step in more general decomposition algorithms like {@link
 * SingularValueDecomposition Singular Value Decomposition}. This class is therefore
 * intended for internal use by the library and is not public. As a consequence of
 * this explicitly limited scope, many methods directly returns references to
 * internal arrays, not copies.</p>
 * @version $Revision$ $Date$
 * @since 2.0
 */
class BiDiagonalTransformer implements Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 8935390784125343332L;

    /** Householder vectors. */
    private final double householderVectors[][];

    /** Main diagonal. */
    private final double[] main;

    /** Secondary diagonal. */
    private final double[] secondary;

    /** Cached value of U. */
    private RealMatrix cachedU;

    /** Cached value of B. */
    private RealMatrix cachedB;

    /** Cached value of V. */
    private RealMatrix cachedV;

    /**
     * Build the transformation to bi-diagonal shape of a matrix. 
     * @param matrix The matrix to transform.
     */
    public BiDiagonalTransformer(RealMatrix matrix)
        throws InvalidMatrixException {

        final int m = matrix.getRowDimension();
        final int n = matrix.getColumnDimension();
        final int p = Math.min(m, n);
        householderVectors = matrix.getData();
        main      = new double[p];
        secondary = new double[p - 1];
        cachedU   = null;
        cachedB   = null;
        cachedV   = null;

        // transform matrix
        if (m >= n) {
            transformToUpperBiDiagonal();
        } else {
            transformToLowerBiDiagonal();
        }

    }

    /**
     * Returns the matrix U of the transform. 
     * <p>U is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * @return the U matrix
     */
    public RealMatrix getU() {

        if (cachedU == null) {

            final int m = householderVectors.length;
            final int n = householderVectors[0].length;
            final int p = main.length;
            final int diagOffset    = (m >= n) ? 0 : 1;
            final double[] diagonal = (m >= n) ? main : secondary;
            final double[][] uData  = new double[m][m];

            // fill up the part of the matrix not affected by Householder transforms
            for (int k = m - 1; k >= p; --k) {
                uData[k][k] = 1;
            }

            // build up first part of the matrix by applying Householder transforms
            for (int k = p - 1; k >= diagOffset; --k) {
                final double[] hK = householderVectors[k];
                uData[k][k] = 1;
                if (hK[k - diagOffset] != 0.0) {
                    for (int j = k; j < m; ++j) {
                        double alpha = 0;
                        for (int i = k; i < m; ++i) {
                            alpha -= uData[i][j] * householderVectors[i][k - diagOffset];
                        }
                        alpha /= diagonal[k - diagOffset] * hK[k - diagOffset];

                        for (int i = k; i < m; ++i) {
                            uData[i][j] -= alpha * householderVectors[i][k - diagOffset];
                        }
                    }
                }
            }
            if (diagOffset > 0) {
                uData[0][0] = 1;
            }

            // cache the matrix for subsequent calls
            cachedU = new RealMatrixImpl(uData, false);

        }

        // return the cached matrix
        return cachedU;

    }

    /**
     * Returns the bi-diagonal matrix B of the transform. 
     * @return the B matrix
     */
    public RealMatrix getB() {

        if (cachedB == null) {

            final int m = householderVectors.length;
            final int n = householderVectors[0].length;
            double[][] bData = new double[m][n];
            for (int i = 0; i < main.length; ++i) {
                bData[i][i] = main[i];
                if (i < main.length - 1) {
                    if (m < n) {
                        bData[i + 1][i] = secondary[i];
                    } else {
                        bData[i][i + 1] = secondary[i];
                    }
                }
            }

            // cache the matrix for subsequent calls
            cachedB = new RealMatrixImpl(bData, false);

        }

        // return the cached matrix
        return cachedB;

    }

    /**
     * Returns the matrix V of the transform. 
     * <p>V is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * @return the V matrix
     */
    public RealMatrix getV() {

        if (cachedV == null) {

            final int m = householderVectors.length;
            final int n = householderVectors[0].length;
            final int p = main.length;
            final int diagOffset    = (m >= n) ? 1 : 0;
            final double[] diagonal = (m >= n) ? secondary : main;
            final double[][] vData  = new double[n][n];

            // fill up the part of the matrix not affected by Householder transforms
            for (int k = n - 1; k >= p; --k) {
                vData[k][k] = 1;
            }

            // build up first part of the matrix by applying Householder transforms
            for (int k = p - 1; k >= diagOffset; --k) {
                final double[] hK = householderVectors[k - diagOffset];
                vData[k][k] = 1;
                if (hK[k] != 0.0) {
                    for (int j = k; j < n; ++j) {
                        double beta = 0;
                        for (int i = k; i < n; ++i) {
                            beta -= vData[i][j] * hK[i];
                        }
                        beta /= diagonal[k - diagOffset] * hK[k];

                        for (int i = k; i < n; ++i) {
                            vData[i][j] -= beta * hK[i];
                        }
                    }
                }
            }
            if (diagOffset > 0) {
                vData[0][0] = 1;
            }

            // cache the matrix for subsequent calls
            cachedV = new RealMatrixImpl(vData, false);

        }

        // return the cached matrix
        return cachedV;

    }

    /**
     * Get the Householder vectors of the transform.
     * <p>Note that since this class is only intended for internal use,
     * it returns directly a reference to its internal arrays, not a copy.</p>
     * @return the main diagonal elements of the B matrix
     */
    double[][] getHouseholderVectorsRef() {
        return householderVectors;
    }

    /**
     * Get the main diagonal elements of the matrix B of the transform.
     * <p>Note that since this class is only intended for internal use,
     * it returns directly a reference to its internal arrays, not a copy.</p>
     * @return the main diagonal elements of the B matrix
     */
    double[] getMainDiagonalRef() {
        return main;
    }

    /**
     * Get the secondary diagonal elements of the matrix B of the transform.
     * <p>Note that since this class is only intended for internal use,
     * it returns directly a reference to its internal arrays, not a copy.</p>
     * @return the secondary diagonal elements of the B matrix
     */
    double[] getSecondaryDiagonalRef() {
        return secondary;
    }

    /**
     * Check if the matrix is transformed to upper bi-diagonal.
     * @return true if the matrix is transformed to upper bi-diagonal
     */
    boolean isUpperBiDiagonal() {
        return householderVectors.length >=  householderVectors[0].length;
    }

    /**
     * Transform original matrix to upper bi-diagonal form.
     * <p>Transformation is done using alternate Householder transforms
     * on columns and rows.</p>
     */
    private void transformToUpperBiDiagonal() {

        final int m = householderVectors.length;
        final int n = householderVectors[0].length;
        for (int k = 0; k < n; k++) {

            //zero-out a column
            double xNormSqr = 0;
            for (int i = k; i < m; ++i) {
                final double c = householderVectors[i][k];
                xNormSqr += c * c;
            }
            final double a = (householderVectors[k][k] > 0) ? -Math.sqrt(xNormSqr) : Math.sqrt(xNormSqr);
            main[k] = a;
            if (a != 0.0) {
                householderVectors[k][k] -= a;
                for (int j = k + 1; j < n; ++j) {
                    double alpha = 0;
                    for (int i = k; i < m; ++i) {
                        final double[] uvI = householderVectors[i];
                        alpha -= uvI[j] * uvI[k];
                    }
                    alpha /= a * householderVectors[k][k];
                    for (int i = k; i < m; ++i) {
                        final double[] uvI = householderVectors[i];
                        uvI[j] -= alpha * uvI[k];
                    }
                }
            }

            if (k < n - 1) {
                //zero-out a row
                final double[] uvK = householderVectors[k];
                xNormSqr = 0;
                for (int j = k + 1; j < n; ++j) {
                    final double c = uvK[j];
                    xNormSqr += c * c;
                }
                final double b = (uvK[k + 1] > 0) ? -Math.sqrt(xNormSqr) : Math.sqrt(xNormSqr);
                secondary[k] = b;
                if (b != 0.0) {
                    uvK[k + 1] -= b;
                    for (int i = k + 1; i < m; ++i) {
                        final double[] uvI = householderVectors[i];
                        double beta = 0;
                        for (int j = k + 1; j < n; ++j) {
                            beta -= uvI[j] * uvK[j];
                        }
                        beta /= b * uvK[k + 1];
                        for (int j = k + 1; j < n; ++j) {
                            uvI[j] -= beta * uvK[j];
                        }
                    }
                }
            }

        }
    }

    /**
     * Transform original matrix to lower bi-diagonal form.
     * <p>Transformation is done using alternate Householder transforms
     * on rows and columns.</p>
     */
    private void transformToLowerBiDiagonal() {

        final int m = householderVectors.length;
        final int n = householderVectors[0].length;
        for (int k = 0; k < m; k++) {

            //zero-out a row
            final double[] uvK = householderVectors[k];
            double xNormSqr = 0;
            for (int j = k; j < n; ++j) {
                final double c = uvK[j];
                xNormSqr += c * c;
            }
            final double a = (uvK[k] > 0) ? -Math.sqrt(xNormSqr) : Math.sqrt(xNormSqr);
            main[k] = a;
            if (a != 0.0) {
                uvK[k] -= a;
                for (int i = k + 1; i < m; ++i) {
                    final double[] uvI = householderVectors[i];
                    double alpha = 0;
                    for (int j = k; j < n; ++j) {
                        alpha -= uvI[j] * uvK[j];
                    }
                    alpha /= a * householderVectors[k][k];
                    for (int j = k; j < n; ++j) {
                        uvI[j] -= alpha * uvK[j];
                    }
                }
            }

            if (k < m - 1) {
                //zero-out a column
                xNormSqr = 0;
                for (int i = k + 1; i < m; ++i) {
                    final double c = householderVectors[i][k];
                    xNormSqr += c * c;
                }
                final double b = (householderVectors[k + 1][k] > 0) ? -Math.sqrt(xNormSqr) : Math.sqrt(xNormSqr);
                secondary[k] = b;
                if (b != 0.0) {
                    householderVectors[k + 1][k] -= b;
                    for (int j = k + 1; j < n; ++j) {
                        double beta = 0;
                        for (int i = k + 1; i < m; ++i) {
                            final double[] uvI = householderVectors[i];
                            beta -= uvI[j] * uvI[k];
                        }
                        beta /= b * householderVectors[k + 1][k];
                        for (int i = k + 1; i < m; ++i) {
                            final double[] uvI = householderVectors[i];
                            uvI[j] -= beta * uvI[k];
                        }
                    }
                }
            }

        }
    }

}
