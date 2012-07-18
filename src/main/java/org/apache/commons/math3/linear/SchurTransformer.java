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

package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

/**
 * Class transforming a general real matrix to Schur form.
 * <p>A m &times; m matrix A can be written as the product of three matrices: A = P
 * &times; T &times; P<sup>T</sup> with P an orthogonal matrix and T an quasi-triangular
 * matrix. Both P and T are m &times; m matrices.</p>
 * <p>Transformation to Schur form is often not a goal by itself, but it is an
 * intermediate step in more general decomposition algorithms like
 * {@link EigenDecomposition eigen decomposition}. This class is therefore
 * intended for internal use by the library and is not public. As a consequence
 * of this explicitly limited scope, many methods directly returns references to
 * internal arrays, not copies.</p>
 * <p>This class is based on the method hqr2 in class EigenvalueDecomposition
 * from the <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a> library.</p>
 *
 * @see <a href="http://mathworld.wolfram.com/SchurDecomposition.html">Schur Decomposition - MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/Schur_decomposition">Schur Decomposition - Wikipedia</a>
 * @see <a href="http://en.wikipedia.org/wiki/Householder_transformation">Householder Transformations</a>
 * @version $Id$
 * @since 3.1
 */
class SchurTransformer {
    /** P matrix. */
    private final double matrixP[][];
    /** T matrix. */
    private final double matrixT[][];
    /** Cached value of P. */
    private RealMatrix cachedP;
    /** Cached value of T. */
    private RealMatrix cachedT;
    /** Cached value of PT. */
    private RealMatrix cachedPt;

    /** Maximum allowed iterations for convergence of the transformation. */
    private final int maxIterations = 100;

    /** Epsilon criteria taken from JAMA code (originally was 2^-52). */
    private final double epsilon = Precision.EPSILON;

    /**
     * Build the transformation to Schur form of a general real matrix.
     *
     * @param matrix matrix to transform
     * @throws NonSquareMatrixException if the matrix is not square
     */
    public SchurTransformer(final RealMatrix matrix) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(),
                                               matrix.getColumnDimension());
        }

        HessenbergTransformer transformer = new HessenbergTransformer(matrix);
        matrixT = transformer.getH().getData();
        matrixP = transformer.getP().getData();
        cachedT = null;
        cachedP = null;
        cachedPt = null;

        // transform matrix
        transform();
    }

    /**
     * Returns the matrix P of the transform.
     * <p>P is an orthogonal matrix, i.e. its inverse is also its transpose.</p>
     *
     * @return the P matrix
     */
    public RealMatrix getP() {
        if (cachedP == null) {
            cachedP = MatrixUtils.createRealMatrix(matrixP);
        }
        return cachedP;
    }

    /**
     * Returns the transpose of the matrix P of the transform.
     * <p>P is an orthogonal matrix, i.e. its inverse is also its transpose.</p>
     *
     * @return the transpose of the P matrix
     */
    public RealMatrix getPT() {
        if (cachedPt == null) {
            cachedPt = getP().transpose();
        }

        // return the cached matrix
        return cachedPt;
    }

    /**
     * Returns the quasi-triangular Schur matrix T of the transform.
     *
     * @return the T matrix
     */
    public RealMatrix getT() {
        if (cachedT == null) {
            cachedT = MatrixUtils.createRealMatrix(matrixT);
        }

        // return the cached matrix
        return cachedT;
    }

    /**
     * Transform original matrix to Schur form.
     * @throws MaxCountExceededException if the transformation does not converge
     */
    private void transform() {
        final int n = matrixT.length;

        // compute matrix norm
        final double norm = getNorm();

        // shift information
        final ShiftInfo shift = new ShiftInfo();

        // Outer loop over eigenvalue index
        int iteration = 0;
        int idx = n - 1;
        while (idx >= 0) {

            // Look for single small sub-diagonal element
            final int l = findSmallSubDiagonalElement(idx, norm);

            // Check for convergence
            if (l == idx) {
                // One root found
                matrixT[idx][idx] = matrixT[idx][idx] + shift.exShift;
                idx--;
                iteration = 0;
            } else if (l == idx - 1) {
                // Two roots found
                shift.w = matrixT[idx][idx - 1] * matrixT[idx - 1][idx];
                double p = (matrixT[idx - 1][idx - 1] - matrixT[idx][idx]) / 2.0;
                double q = p * p + shift.w;
                double z = FastMath.sqrt(FastMath.abs(q));
                matrixT[idx][idx] = matrixT[idx][idx] + shift.exShift;
                matrixT[idx - 1][idx - 1] = matrixT[idx - 1][idx - 1] + shift.exShift;
                shift.x = matrixT[idx][idx];

                if (q >= 0) {
                    if (p >= 0) {
                        z = p + z;
                    } else {
                        z = p - z;
                    }
                    shift.x = matrixT[idx][idx - 1];
                    double s = FastMath.abs(shift.x) + FastMath.abs(z);
                    p = shift.x / s;
                    q = z / s;
                    double r = FastMath.sqrt(p * p + q * q);
                    p = p / r;
                    q = q / r;

                    // Row modification
                    for (int j = idx - 1; j < n; j++) {
                        z = matrixT[idx - 1][j];
                        matrixT[idx - 1][j] = q * z + p * matrixT[idx][j];
                        matrixT[idx][j] = q * matrixT[idx][j] - p * z;
                    }

                    // Column modification
                    for (int i = 0; i <= idx; i++) {
                        z = matrixT[i][idx - 1];
                        matrixT[i][idx - 1] = q * z + p * matrixT[i][idx];
                        matrixT[i][idx] = q * matrixT[i][idx] - p * z;
                    }

                    // Accumulate transformations
                    for (int i = 0; i <= n - 1; i++) {
                        z = matrixP[i][idx - 1];
                        matrixP[i][idx - 1] = q * z + p * matrixP[i][idx];
                        matrixP[i][idx] = q * matrixP[i][idx] - p * z;
                    }
                }
                idx -= 2;
                iteration = 0;
            } else {
                // No convergence yet

                computeShift(l, idx, iteration, shift);

                // stop transformation after too many iterations
                if (++iteration > maxIterations) {
                    throw new MaxCountExceededException(LocalizedFormats.CONVERGENCE_FAILED,
                                                        maxIterations);
                }

                // Look for two consecutive small sub-diagonal elements
                int m = idx - 2;

                // the initial houseHolder vector for the QR step
                final double[] hVec = new double[3];

                while (m >= l) {
                    double z = matrixT[m][m];
                    hVec[2] = shift.x - z;
                    double s = shift.y - z;
                    hVec[0] = (hVec[2] * s - shift.w) / matrixT[m + 1][m] + matrixT[m][m + 1];
                    hVec[1] = matrixT[m + 1][m + 1] - z - hVec[2] - s;
                    hVec[2] = matrixT[m + 2][m + 1];
                    s = FastMath.abs(hVec[0]) + FastMath.abs(hVec[1]) + FastMath.abs(hVec[2]);

                    if (m == l) {
                        break;
                    }

                    for (int i = 0; i < hVec.length; i++) {
                        hVec[i] /= s;
                    }

                    final double lhs = FastMath.abs(matrixT[m][m - 1]) *
                            (FastMath.abs(hVec[1]) + FastMath.abs(hVec[2]));

                    final double rhs = FastMath.abs(hVec[0]) *
                            (FastMath.abs(matrixT[m - 1][m - 1]) + FastMath.abs(z) +
                             FastMath.abs(matrixT[m + 1][m + 1]));

                    if (lhs < epsilon * rhs) {
                        break;
                    }
                    m--;
                }

                performDoubleQRStep(l, m, idx, shift, hVec);
            }
        }
    }

    /**
     * Computes the L1 norm of the (quasi-)triangular matrix T.
     *
     * @return the L1 norm of matrix T
     */
    private double getNorm() {
        double norm = 0.0;
        for (int i = 0; i < matrixT.length; i++) {
            // as matrix T is (quasi-)triangular, also take the sub-diagonal element into account
            for (int j = FastMath.max(i - 1, 0); j < matrixT.length; j++) {
                norm += FastMath.abs(matrixT[i][j]);
            }
        }
        return norm;
    }

    /**
     * Find the first small sub-diagonal element and returns its index.
     *
     * @param startIdx the starting index for the search
     * @param norm the L1 norm of the matrix
     * @return the index of the first small sub-diagonal element
     */
    private int findSmallSubDiagonalElement(final int startIdx, final double norm) {
        int l = startIdx;
        while (l > 0) {
            double s = FastMath.abs(matrixT[l - 1][l - 1]) + FastMath.abs(matrixT[l][l]);
            if (Precision.equals(s, 0.0, epsilon)) {
                s = norm;
            }
            if (FastMath.abs(matrixT[l][l - 1]) < epsilon * s) {
                break;
            }
            l--;
        }
        return l;
    }

    /**
     * Compute the shift for the current iteration.
     *
     * @param l the index of the small sub-diagonal element
     * @param idx the current eigenvalue index
     * @param iteration the current iteration
     * @param shift holder for shift information
     */
    private void computeShift(final int l, final int idx, final int iteration, final ShiftInfo shift) {
        // Form shift
        shift.x = matrixT[idx][idx];
        shift.y = shift.w = 0.0;
        if (l < idx) {
            shift.y = matrixT[idx - 1][idx - 1];
            shift.w = matrixT[idx][idx - 1] * matrixT[idx - 1][idx];
        }

        // Wilkinson's original ad hoc shift
        if (iteration == 10) {
            shift.exShift += shift.x;
            for (int i = 0; i <= idx; i++) {
                matrixT[i][i] -= shift.x;
            }
            double s = FastMath.abs(matrixT[idx][idx - 1]) + FastMath.abs(matrixT[idx - 1][idx - 2]);
            shift.x = shift.y = 0.75 * s;
            shift.w = -0.4375 * s * s;
        }

        // MATLAB's new ad hoc shift
        if (iteration == 30) {
            double s = (shift.y - shift.x) / 2.0;
            s = s * s + shift.w;
            if (Precision.compareTo(s, 0.0d, epsilon) > 0) {
                s = FastMath.sqrt(s);
                if (shift.y < shift.x) {
                    s = -s;
                }
                s = shift.x - shift.w / ((shift.y - shift.x) / 2.0 + s);
                for (int i = 0; i <= idx; i++) {
                    matrixT[i][i] -= s;
                }
                shift.exShift += s;
                shift.x = shift.y = shift.w = 0.964;
            }
        }
    }

    /**
     * Perform a double QR step involving rows l:idx and columns m:n
     *
     * @param l the index of the small sub-diagonal element
     * @param m the start index for the QR step
     * @param idx the current eigenvalue index
     * @param shift shift information holder
     * @param hVec the initial houseHolder vector
     */
    private void performDoubleQRStep(final int l, final int m, final int idx,
                                     final ShiftInfo shift, final double[] hVec) {

        final int n = matrixT.length;
        double p = hVec[0];
        double q = hVec[1];
        double r = hVec[2];

        for (int k = m; k <= idx - 1; k++) {
            boolean notlast = k != idx - 1;
            if (k != m) {
                p = matrixT[k][k - 1];
                q = matrixT[k + 1][k - 1];
                r = notlast ? matrixT[k + 2][k - 1] : 0.0;
                shift.x = FastMath.abs(p) + FastMath.abs(q) + FastMath.abs(r);
                if (!Precision.equals(shift.x, 0.0, epsilon)) {
                    p = p / shift.x;
                    q = q / shift.x;
                    r = r / shift.x;
                }
            }
            if (Precision.equals(shift.x, 0.0, epsilon)) {
                break;
            }
            double s = FastMath.sqrt(p * p + q * q + r * r);
            if (Precision.compareTo(p, 0.0, epsilon) < 0) {
                s = -s;
            }
            if (!Precision.equals(s, 0.0, epsilon)) {
                if (k != m) {
                    matrixT[k][k - 1] = -s * shift.x;
                } else if (l != m) {
                    matrixT[k][k - 1] = -matrixT[k][k - 1];
                }
                p = p + s;
                shift.x = p / s;
                shift.y = q / s;
                double z = r / s;
                q = q / p;
                r = r / p;

                // Row modification
                for (int j = k; j < n; j++) {
                    p = matrixT[k][j] + q * matrixT[k + 1][j];
                    if (notlast) {
                        p = p + r * matrixT[k + 2][j];
                        matrixT[k + 2][j] = matrixT[k + 2][j] - p * z;
                    }
                    matrixT[k][j] = matrixT[k][j] - p * shift.x;
                    matrixT[k + 1][j] = matrixT[k + 1][j] - p * shift.y;
                }

                // Column modification
                for (int i = 0; i <= FastMath.min(idx, k + 3); i++) {
                    p = shift.x * matrixT[i][k] + shift.y * matrixT[i][k + 1];
                    if (notlast) {
                        p = p + z * matrixT[i][k + 2];
                        matrixT[i][k + 2] = matrixT[i][k + 2] - p * r;
                    }
                    matrixT[i][k] = matrixT[i][k] - p;
                    matrixT[i][k + 1] = matrixT[i][k + 1] - p * q;
                }

                // Accumulate transformations
                final int high = matrixT.length - 1;
                for (int i = 0; i <= high; i++) {
                    p = shift.x * matrixP[i][k] + shift.y * matrixP[i][k + 1];
                    if (notlast) {
                        p = p + z * matrixP[i][k + 2];
                        matrixP[i][k + 2] = matrixP[i][k + 2] - p * r;
                    }
                    matrixP[i][k] = matrixP[i][k] - p;
                    matrixP[i][k + 1] = matrixP[i][k + 1] - p * q;
                }
            }  // (s != 0)
        }  // k loop

        // clean up pollution due to round-off errors
        for (int i = m+2; i <= idx; i++) {
            matrixT[i][i-2] = 0.0;
            if (i > m+2) {
                matrixT[i][i-3] = 0.0;
            }
        }
    }

    /**
     * Internal data structure holding the current shift information.
     * Contains variable names as present in the original JAMA code.
     */
    private static class ShiftInfo {
        // CHECKSTYLE: stop all

        /** x shift info */
        double x;
        /** y shift info */
        double y;
        /** w shift info */
        double w;
        /** Indicates an exceptional shift. */
        double exShift;

        // CHECKSTYLE: resume all
    }
}
