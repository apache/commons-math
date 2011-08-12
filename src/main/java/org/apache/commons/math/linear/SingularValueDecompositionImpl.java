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

import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Calculates the compact Singular Value Decomposition of a matrix.
 * <p>
 * The Singular Value Decomposition of matrix A is a set of three matrices: U,
 * &Sigma; and V such that A = U &times; &Sigma; &times; V<sup>T</sup>. Let A be
 * a m &times; n matrix, then U is a m &times; p orthogonal matrix, &Sigma; is a
 * p &times; p diagonal matrix with positive or null elements, V is a p &times;
 * n orthogonal matrix (hence V<sup>T</sup> is also orthogonal) where
 * p=min(m,n).
 * </p>
 * @version $Id$
 * @since 2.0
 */
public class SingularValueDecompositionImpl implements SingularValueDecomposition {

    /** Relative threshold for small singular values. */
    private static final double EPS = 0x1.0p-52;

    /** Absolute threshold for small singular values. */
    private static final double TINY = 0x1.0p-966;

    /** Computed singular values. */
    private final double[] singularValues;

    /** max(row dimension, column dimension). */
    private final int m;

    /** min(row dimension, column dimension). */
    private final int n;

    /** Indicator for transposed matrix. */
    private final boolean transposed;

    /** Cached value of U matrix. */
    private final RealMatrix cachedU;

    /** Cached value of transposed U matrix. */
    private RealMatrix cachedUt;

    /** Cached value of S (diagonal) matrix. */
    private RealMatrix cachedS;

    /** Cached value of V matrix. */
    private final RealMatrix cachedV;

    /** Cached value of transposed V matrix. */
    private RealMatrix cachedVt;

    /**
     * Calculates the compact Singular Value Decomposition of the given matrix.
     *
     * @param matrix Matrix to decompose.
     */
    public SingularValueDecompositionImpl(final RealMatrix matrix) {
        final double[][] A;

         // "m" is always the largest dimension.
        if (matrix.getRowDimension() < matrix.getColumnDimension()) {
            transposed = true;
            A = matrix.transpose().getData();
            m = matrix.getColumnDimension();
            n = matrix.getRowDimension();
        } else {
            transposed = false;
            A = matrix.getData();
            m = matrix.getRowDimension();
            n = matrix.getColumnDimension();
        }
 
        final int nu = FastMath.min(m, n);
        singularValues = new double[FastMath.min(m + 1, n)];
        final double[][] U = new double[m][nu];
        final double[][] V = new double[n][n];
        final double[] e = new double[n];
        final double[] work = new double[m];
        boolean wantu = true;
        boolean wantv = true;
        // Reduce A to bidiagonal form, storing the diagonal elements
        // in s and the super-diagonal elements in e.
        final int nct = FastMath.min(m - 1, n);
        final int nrt = FastMath.max(0, FastMath.min(n - 2, m));
        for (int k = 0; k < FastMath.max(nct, nrt); k++) {
            if (k < nct) {
                // Compute the transformation for the k-th column and
                // place the k-th diagonal in s[k].
                // Compute 2-norm of k-th column without under/overflow.
                singularValues[k] = 0;
                for (int i = k; i < m; i++) {
                    singularValues[k] = FastMath.hypot(singularValues[k], A[i][k]);
                }
                if (singularValues[k] != 0.0) {
                    if (A[k][k] < 0.0) {
                        singularValues[k] = -singularValues[k];
                    }
                    for (int i = k; i < m; i++) {
                        A[i][k] /= singularValues[k];
                    }
                    A[k][k] += 1.0;
                }
                singularValues[k] = -singularValues[k];
            }
            for (int j = k + 1; j < n; j++) {
                if ((k < nct) && (singularValues[k] != 0.0)) {
                    // Apply the transformation.
                    double t = 0;
                    for (int i = k; i < m; i++) {
                        t += A[i][k] * A[i][j];
                    }
                    t = -t / A[k][k];
                    for (int i = k; i < m; i++) {
                        A[i][j] += t * A[i][k];
                    }
                }
                // Place the k-th row of A into e for the
                // subsequent calculation of the row transformation.
                e[j] = A[k][j];
            }
            if (wantu && (k < nct)) {
                // Place the transformation in U for subsequent back
                // multiplication.
                for (int i = k; i < m; i++) {
                    U[i][k] = A[i][k];
                }
            }
            if (k < nrt) {
                // Compute the k-th row transformation and place the
                // k-th super-diagonal in e[k].
                // Compute 2-norm without under/overflow.
                e[k] = 0;
                for (int i = k + 1; i < n; i++) {
                    e[k] = FastMath.hypot(e[k], e[i]);
                }
                if (e[k] != 0.0) {
                    if (e[k + 1] < 0.0) {
                        e[k] = -e[k];
                    }
                    for (int i = k + 1; i < n; i++) {
                        e[i] /= e[k];
                    }
                    e[k + 1] += 1.0;
                }
                e[k] = -e[k];
                if ((k + 1 < m) && (e[k] != 0.0)) {
                    // Apply the transformation.
                    for (int i = k + 1; i < m; i++) {
                        work[i] = 0.0;
                    }
                    for (int j = k + 1; j < n; j++) {
                        for (int i = k + 1; i < m; i++) {
                            work[i] += e[j] * A[i][j];
                        }
                    }
                    for (int j = k + 1; j < n; j++) {
                        final double t = -e[j] / e[k + 1];
                        for (int i = k + 1; i < m; i++) {
                            A[i][j] += t * work[i];
                        }
                    }
                }
                if (wantv) {
                    // Place the transformation in V for subsequent
                    // back multiplication.
                    for (int i = k + 1; i < n; i++) {
                        V[i][k] = e[i];
                    }
                }
            }
        }
        // Set up the final bidiagonal matrix or order p.
        int p = FastMath.min(n, m + 1);
        if (nct < n) {
            singularValues[nct] = A[nct][nct];
        }
        if (m < p) {
            singularValues[p - 1] = 0.0;
        }
        if (nrt + 1 < p) {
            e[nrt] = A[nrt][p - 1];
        }
        e[p - 1] = 0.0;
        // If required, generate U.
        if (wantu) {
            for (int j = nct; j < nu; j++) {
                for (int i = 0; i < m; i++) {
                    U[i][j] = 0.0;
                }
                U[j][j] = 1.0;
            }
            for (int k = nct - 1; k >= 0; k--) {
                if (singularValues[k] != 0.0) {
                    for (int j = k + 1; j < nu; j++) {
                        double t = 0;
                        for (int i = k; i < m; i++) {
                            t += U[i][k] * U[i][j];
                        }
                        t = -t / U[k][k];
                        for (int i = k; i < m; i++) {
                            U[i][j] += t * U[i][k];
                        }
                    }
                    for (int i = k; i < m; i++) {
                        U[i][k] = -U[i][k];
                    }
                    U[k][k] = 1.0 + U[k][k];
                    for (int i = 0; i < k - 1; i++) {
                        U[i][k] = 0.0;
                    }
                } else {
                    for (int i = 0; i < m; i++) {
                        U[i][k] = 0.0;
                    }
                    U[k][k] = 1.0;
                }
            }
        }
        // If required, generate V.
        if (wantv) {
            for (int k = n - 1; k >= 0; k--) {
                if ((k < nrt) && (e[k] != 0.0)) {
                    for (int j = k + 1; j < nu; j++) {
                        double t = 0;
                        for (int i = k + 1; i < n; i++) {
                            t += V[i][k] * V[i][j];
                        }
                        t = -t / V[k + 1][k];
                        for (int i = k + 1; i < n; i++) {
                            V[i][j] += t * V[i][k];
                        }
                    }
                }
                for (int i = 0; i < n; i++) {
                    V[i][k] = 0.0;
                }
                V[k][k] = 1.0;
            }
        }
        // Main iteration loop for the singular values.
        final int pp = p - 1;
        int iter = 0;
        while (p > 0) {
            int k;
            int kase;
            // Here is where a test for too many iterations would go.
            // This section of the program inspects for
            // negligible elements in the s and e arrays.  On
            // completion the variables kase and k are set as follows.
            // kase = 1     if s(p) and e[k-1] are negligible and k<p
            // kase = 2     if s(k) is negligible and k<p
            // kase = 3     if e[k-1] is negligible, k<p, and
            //              s(k), ..., s(p) are not negligible (qr step).
            // kase = 4     if e(p-1) is negligible (convergence).
            for (k = p - 2; k >= -1; k--) {
                if (k == -1) {
                    break;
                }
                final double threshold
                    = TINY + EPS * (FastMath.abs(singularValues[k]) +
                                    FastMath.abs(singularValues[k + 1]));
                if (FastMath.abs(e[k]) <= threshold) {
                    e[k] = 0.0;
                    break;
                }
            }
            if (k == p - 2) {
                kase = 4;
            } else {
                int ks;
                for (ks = p - 1; ks >= k; ks--) {
                    if (ks == k) {
                        break;
                    }
                    final double t = (ks != p ? FastMath.abs(e[ks]) : 0.0) +
                        (ks != k + 1 ? FastMath.abs(e[ks - 1]) : 0.0);
                    if (FastMath.abs(singularValues[ks]) <= TINY + EPS * t) {
                        singularValues[ks] = 0.0;
                        break;
                    }
                }
                if (ks == k) {
                    kase = 3;
                } else if (ks == p - 1) {
                    kase = 1;
                } else {
                    kase = 2;
                    k = ks;
                }
            }
            k++;
            // Perform the task indicated by kase.
            switch (kase) {
                // Deflate negligible s(p).
                case 1: {
                    double f = e[p - 2];
                    e[p - 2] = 0.0;
                    for (int j = p - 2; j >= k; j--) {
                        double t = FastMath.hypot(singularValues[j], f);
                        final double cs = singularValues[j] / t;
                        final double sn = f / t;
                        singularValues[j] = t;
                        if (j != k) {
                            f = -sn * e[j - 1];
                            e[j - 1] = cs * e[j - 1];
                        }
                        if (wantv) {
                            for (int i = 0; i < n; i++) {
                                t = cs * V[i][j] + sn * V[i][p - 1];
                                V[i][p - 1] = -sn * V[i][j] + cs * V[i][p - 1];
                                V[i][j] = t;
                            }
                        }
                    }
                }
                break;
                // Split at negligible s(k).
                case 2: {
                    double f = e[k - 1];
                    e[k - 1] = 0.0;
                    for (int j = k; j < p; j++) {
                        double t = FastMath.hypot(singularValues[j], f);
                        final double cs = singularValues[j] / t;
                        final double sn = f / t;
                        singularValues[j] = t;
                        f = -sn * e[j];
                        e[j] = cs * e[j];
                        if (wantu) {
                            for (int i = 0; i < m; i++) {
                                t = cs * U[i][j] + sn * U[i][k - 1];
                                U[i][k - 1] = -sn * U[i][j] + cs * U[i][k - 1];
                                U[i][j] = t;
                            }
                        }
                    }
                }
                break;
                // Perform one qr step.
                case 3: {
                    // Calculate the shift.
                    final double scale = FastMath.max(FastMath.max(FastMath.max(FastMath.max(
                            FastMath.abs(singularValues[p - 1]), FastMath.abs(singularValues[p - 2])), FastMath.abs(e[p - 2])),
                            FastMath.abs(singularValues[k])), FastMath.abs(e[k]));
                    final double sp = singularValues[p - 1] / scale;
                    final double spm1 = singularValues[p - 2] / scale;
                    final double epm1 = e[p - 2] / scale;
                    final double sk = singularValues[k] / scale;
                    final double ek = e[k] / scale;
                    final double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
                    final double c = (sp * epm1) * (sp * epm1);
                    double shift = 0.0;
                    if ((b != 0.0) | (c != 0.0)) {
                        shift = FastMath.sqrt(b * b + c);
                        if (b < 0.0) {
                            shift = -shift;
                        }
                        shift = c / (b + shift);
                    }
                    double f = (sk + sp) * (sk - sp) + shift;
                    double g = sk * ek;
                    // Chase zeros.
                    for (int j = k; j < p - 1; j++) {
                        double t = FastMath.hypot(f, g);
                        double cs = f / t;
                        double sn = g / t;
                        if (j != k) {
                            e[j - 1] = t;
                        }
                        f = cs * singularValues[j] + sn * e[j];
                        e[j] = cs * e[j] - sn * singularValues[j];
                        g = sn * singularValues[j + 1];
                        singularValues[j + 1] = cs * singularValues[j + 1];
                        if (wantv) {
                            for (int i = 0; i < n; i++) {
                                t = cs * V[i][j] + sn * V[i][j + 1];
                                V[i][j + 1] = -sn * V[i][j] + cs * V[i][j + 1];
                                V[i][j] = t;
                            }
                        }
                        t = FastMath.hypot(f, g);
                        cs = f / t;
                        sn = g / t;
                        singularValues[j] = t;
                        f = cs * e[j] + sn * singularValues[j + 1];
                        singularValues[j + 1] = -sn * e[j] + cs * singularValues[j + 1];
                        g = sn * e[j + 1];
                        e[j + 1] = cs * e[j + 1];
                        if (wantu && (j < m - 1)) {
                            for (int i = 0; i < m; i++) {
                                t = cs * U[i][j] + sn * U[i][j + 1];
                                U[i][j + 1] = -sn * U[i][j] + cs * U[i][j + 1];
                                U[i][j] = t;
                            }
                        }
                    }
                    e[p - 2] = f;
                    iter = iter + 1;
                }
                break;
                // Convergence.
                default: {
                    // Make the singular values positive.
                    if (singularValues[k] <= 0.0) {
                        singularValues[k] = singularValues[k] < 0.0 ? -singularValues[k] : 0.0;
                        if (wantv) {
                            for (int i = 0; i <= pp; i++) {
                                V[i][k] = -V[i][k];
                            }
                        }
                    }
                    // Order the singular values.
                    while (k < pp) {
                        if (singularValues[k] >= singularValues[k + 1]) {
                            break;
                        }
                        double t = singularValues[k];
                        singularValues[k] = singularValues[k + 1];
                        singularValues[k + 1] = t;
                        if (wantv && (k < n - 1)) {
                            for (int i = 0; i < n; i++) {
                                t = V[i][k + 1];
                                V[i][k + 1] = V[i][k];
                                V[i][k] = t;
                            }
                        }
                        if (wantu && (k < m - 1)) {
                            for (int i = 0; i < m; i++) {
                                t = U[i][k + 1];
                                U[i][k + 1] = U[i][k];
                                U[i][k] = t;
                            }
                        }
                        k++;
                    }
                    iter = 0;
                    p--;
                }
                break;
            }
        }

        if (!transposed) {
            cachedU = MatrixUtils.createRealMatrix(U);
            cachedV = MatrixUtils.createRealMatrix(V);
        } else {
            cachedU = MatrixUtils.createRealMatrix(V);
            cachedV = MatrixUtils.createRealMatrix(U);

        }
    }

    /** {@inheritDoc} */
    public RealMatrix getU() {
        // return the cached matrix
        return cachedU;

    }

    /** {@inheritDoc} */
    public RealMatrix getUT() {
        if (cachedUt == null) {
            cachedUt = getU().transpose();
        }
        // return the cached matrix
        return cachedUt;
    }

    /** {@inheritDoc} */
    public RealMatrix getS() {
        if (cachedS == null) {
            // cache the matrix for subsequent calls
            cachedS = MatrixUtils.createRealDiagonalMatrix(singularValues);
        }
        return cachedS;
    }

    /** {@inheritDoc} */
    public double[] getSingularValues() {
        return singularValues.clone();
    }

    /** {@inheritDoc} */
    public RealMatrix getV() {
        // return the cached matrix
        return cachedV;
    }

    /** {@inheritDoc} */
    public RealMatrix getVT() {
        if (cachedVt == null) {
            cachedVt = getV().transpose();
        }
        // return the cached matrix
        return cachedVt;
    }

    /** {@inheritDoc} */
    public RealMatrix getCovariance(final double minSingularValue) {
        // get the number of singular values to consider
        final int p = singularValues.length;
        int dimension = 0;
        while ((dimension < p) && (singularValues[dimension] >= minSingularValue)) {
            ++dimension;
        }

        if (dimension == 0) {
            throw new NumberIsTooLargeException(LocalizedFormats.TOO_LARGE_CUTOFF_SINGULAR_VALUE,
                                                minSingularValue, singularValues[0], true);
        }

        final double[][] data = new double[dimension][p];
        getVT().walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            /** {@inheritDoc} */
            @Override
            public void visit(final int row, final int column,
                    final double value) {
                data[row][column] = value / singularValues[row];
            }
        }, 0, dimension - 1, 0, p - 1);

        RealMatrix jv = new Array2DRowRealMatrix(data, false);
        return jv.transpose().multiply(jv);
    }

    /** {@inheritDoc} */
    public double getNorm() {
        return singularValues[0];
    }

    /** {@inheritDoc} */
    public double getConditionNumber() {
        return singularValues[0] / singularValues[FastMath.min(m, n) - 1];
    }

    /** {@inheritDoc} */
    public int getRank() {
        final double tol = m * singularValues[0] * EPS;
        int r = 0;
        for (int i = 0; i < singularValues.length; i++) {
            if (singularValues[i] > tol) {
                r++;
            }
        }
        return r;
    }

    /** {@inheritDoc} */
    public DecompositionSolver getSolver() {
        return new Solver(singularValues, getUT(), getV(), getRank() == m);
    }

    /** Specialized solver. */
    private static class Solver implements DecompositionSolver {
        /** Pseudo-inverse of the initial matrix. */
        private final RealMatrix pseudoInverse;
        /** Singularity indicator. */
        private boolean nonSingular;

        /**
         * Build a solver from decomposed matrix.
         *
         * @param singularValues Singular values.
         * @param uT U<sup>T</sup> matrix of the decomposition.
         * @param v V matrix of the decomposition.
         * @param nonSingular Singularity indicator.
         */
        private Solver(final double[] singularValues, final RealMatrix uT,
                       final RealMatrix v, final boolean nonSingular) {
            final double[][] suT = uT.getData();
            for (int i = 0; i < singularValues.length; ++i) {
                final double a;
                if (singularValues[i] > 0) {
                    a = 1 / singularValues[i];
                } else {
                    a = 0;
                }
                final double[] suTi = suT[i];
                for (int j = 0; j < suTi.length; ++j) {
                    suTi[j] *= a;
                }
            }
            pseudoInverse = v.multiply(new Array2DRowRealMatrix(suT, false));
            this.nonSingular = nonSingular;
        }

        /**
         * Solve the linear equation A &times; X = B in least square sense.
         * <p>
         * The m&times;n matrix A may not be square, the solution X is such that
         * ||A &times; X - B|| is minimal.
         * </p>
         * @param b Right-hand side of the equation A &times; X = B
         * @return a vector X that minimizes the two norm of A &times; X - B
         * @throws org.apache.commons.math.exception.DimensionMismatchException
         * if the matrices dimensions do not match.
         */
        public double[] solve(final double[] b) {
            return pseudoInverse.operate(b);
        }

        /**
         * Solve the linear equation A &times; X = B in least square sense.
         * <p>
         * The m&times;n matrix A may not be square, the solution X is such that
         * ||A &times; X - B|| is minimal.
         * </p>
         * @param b Right-hand side of the equation A &times; X = B
         * @return a vector X that minimizes the two norm of A &times; X - B
         * @throws org.apache.commons.math.exception.DimensionMismatchException
         * if the matrices dimensions do not match.
         */
        public RealVector solve(final RealVector b) {
            return pseudoInverse.operate(b);
        }

        /**
         * Solve the linear equation A &times; X = B in least square sense.
         * <p>
         * The m&times;n matrix A may not be square, the solution X is such that
         * ||A &times; X - B|| is minimal.
         * </p>
         *
         * @param b Right-hand side of the equation A &times; X = B
         * @return a matrix X that minimizes the two norm of A &times; X - B
         * @throws org.apache.commons.math.exception.DimensionMismatchException
         * if the matrices dimensions do not match.
         */
        public double[][] solve(final double[][] b) {
            return pseudoInverse.multiply(MatrixUtils.createRealMatrix(b)).getData();
        }

        /**
         * Solve the linear equation A &times; X = B in least square sense.
         * <p>
         * The m&times;n matrix A may not be square, the solution X is such that
         * ||A &times; X - B|| is minimal.
         * </p>
         *
         * @param b Right-hand side of the equation A &times; X = B
         * @return a matrix X that minimizes the two norm of A &times; X - B
         * @throws org.apache.commons.math.exception.DimensionMismatchException
         * if the matrices dimensions do not match.
         */
        public RealMatrix solve(final RealMatrix b) {
            return pseudoInverse.multiply(b);
        }

        /**
         * Check if the decomposed matrix is non-singular.
         *
         * @return {@code true} if the decomposed matrix is non-singular.
         */
        public boolean isNonSingular() {
            return nonSingular;
        }

        /**
         * Get the pseudo-inverse of the decomposed matrix.
         *
         * @return the inverse matrix.
         */
        public RealMatrix getInverse() {
            return pseudoInverse;
        }
    }
}
