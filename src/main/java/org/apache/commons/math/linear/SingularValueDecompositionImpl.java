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

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.util.MathUtils;

/**
 * Calculates the Singular Value Decomposition of a matrix.
 * <p>The Singular Value Decomposition of matrix A is a set of three matrices:
 * U, &Sigma; and V such that A = U &times; &Sigma; &times; V<sup>T</sup>.
 * Let A be an m &times; n matrix, then U is an m &times; m orthogonal matrix,
 * &Sigma; is a m &times; n diagonal matrix with positive diagonal elements,
 * and V is an n &times; n orthogonal matrix.</p>
 *
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class SingularValueDecompositionImpl implements SingularValueDecomposition {

    /** Number of rows of the initial matrix. */
    private int m;

    /** Number of columns of the initial matrix. */
    private int n;

    /** Transformer to bidiagonal. */
    private BiDiagonalTransformer transformer;

    /** Main diagonal of the bidiagonal matrix. */
    private double[] mainBidiagonal;

    /** Secondary diagonal of the bidiagonal matrix. */
    private double[] secondaryBidiagonal;

    /** Main diagonal of the tridiagonal matrix. */
    private double[] mainTridiagonal;

    /** Secondary diagonal of the tridiagonal matrix. */
    private double[] secondaryTridiagonal;

    /** Eigen decomposition of the tridiagonal matrix. */
    private EigenDecomposition eigenDecomposition;

    /** Singular values. */
    private double[] singularValues;

    /** Cached value of U. */
    private RealMatrix cachedU;

    /** Cached value of U<sup>T</sup>. */
    private RealMatrix cachedUt;

    /** Cached value of S. */
    private RealMatrix cachedS;

    /** Cached value of V. */
    private RealMatrix cachedV;

    /** Cached value of V<sup>T</sup>. */
    private RealMatrix cachedVt;

    /**
     * Calculates the Singular Value Decomposition of the given matrix.
     * @param matrix The matrix to decompose.
     * @exception InvalidMatrixException (wrapping a {@link
     * org.apache.commons.math.ConvergenceException} if algorithm fails to converge
     */
    public SingularValueDecompositionImpl(RealMatrix matrix)
        throws InvalidMatrixException {

        m = matrix.getRowDimension();
        n = matrix.getColumnDimension();

        cachedU  = null;
        cachedS  = null;
        cachedV  = null;
        cachedVt = null;

        // transform the matrix to bidiagonal
        transformer         = new BiDiagonalTransformer(matrix);
        mainBidiagonal      = transformer.getMainDiagonalRef();
        secondaryBidiagonal = transformer.getSecondaryDiagonalRef();

        // compute Bt.B (if upper diagonal) or B.Bt (if lower diagonal)
        mainTridiagonal      = new double[mainBidiagonal.length];
        secondaryTridiagonal = new double[mainBidiagonal.length - 1];
        double a = mainBidiagonal[0];
        mainTridiagonal[0] = a * a;
        for (int i = 1; i < mainBidiagonal.length; ++i) {
            final double b  = secondaryBidiagonal[i - 1];
            secondaryTridiagonal[i - 1] = a * b;
            a = mainBidiagonal[i];
            mainTridiagonal[i] = a * a + b * b;
        }

        // compute singular values
        eigenDecomposition =
            new EigenDecompositionImpl(mainTridiagonal, secondaryTridiagonal,
                                       MathUtils.SAFE_MIN);
        singularValues = eigenDecomposition.getRealEigenvalues();
        for (int i = 0; i < singularValues.length; ++i) {
            final double si = singularValues[i];
            singularValues[i] = (si < 0) ? 0.0 : Math.sqrt(si);
        }

    }

    /** {@inheritDoc} */
    public RealMatrix getU()
        throws InvalidMatrixException {

        if (cachedU == null) {

            if (m >= n) {
                // the tridiagonal matrix is Bt.B, where B is upper bidiagonal
                final double[][] eData = eigenDecomposition.getV().getData();
                final double[][] iData = new double[m][];
                double[] ei1 = eData[0];
                iData[0] = ei1;
                for (int i = 0; i < n - 1; ++i) {
                    // compute B.E.S^(-1) where E is the eigenvectors matrix
                    // we reuse the array from matrix E to store the result
                    final double mi = mainBidiagonal[i];
                    final double si = secondaryBidiagonal[i];
                    final double[] ei0 = ei1;
                    ei1 = eData[i + 1];
                    iData[i + 1] = ei1;
                    for (int j = 0; j < n; ++j) {
                        ei0[j] = (mi * ei0[j] + si * ei1[j]) / singularValues[j];
                    }
                }
                // last row
                final double lastMain = mainBidiagonal[n - 1];
                for (int j = 0; j < n; ++j) {
                    ei1[j] *= lastMain / singularValues[j];
                }
                for (int i = n; i < m; ++i) {
                    iData[i] = new double[n];
                }
                cachedU =
                    transformer.getU().multiply(MatrixUtils.createRealMatrix(iData));
            } else {
                // the tridiagonal matrix is B.Bt, where B is lower bidiagonal
                cachedU = transformer.getU().multiply(eigenDecomposition.getV());
            }

        }

        // return the cached matrix
        return cachedU;

    }

    /** {@inheritDoc} */
    public RealMatrix getUT()
        throws InvalidMatrixException {

        if (cachedUt == null) {
            cachedUt = getU().transpose();
        }

        // return the cached matrix
        return cachedUt;

    }

    /** {@inheritDoc} */
    public RealMatrix getS()
        throws InvalidMatrixException {

        if (cachedS == null) {

            // cache the matrix for subsequent calls
            cachedS = MatrixUtils.createRealDiagonalMatrix(singularValues);

        }
        return cachedS;
    }

    /** {@inheritDoc} */
    public double[] getSingularValues()
        throws InvalidMatrixException {
        return singularValues.clone();
    }

    /** {@inheritDoc} */
    public RealMatrix getV()
        throws InvalidMatrixException {

        if (cachedV == null) {

            if (m >= n) {
                // the tridiagonal matrix is Bt.B, where B is upper bidiagonal
                cachedV = transformer.getV().multiply(eigenDecomposition.getV());
            } else {
                // the tridiagonal matrix is B.Bt, where B is lower bidiagonal
                final double[][] eData = eigenDecomposition.getV().getData();
                final double[][] iData = new double[n][];
                double[] ei1 = eData[0];
                iData[0] = ei1;
                for (int i = 0; i < m - 1; ++i) {
                    // compute Bt.E.S^(-1) where E is the eigenvectors matrix
                    // we reuse the array from matrix E to store the result
                    final double mi = mainBidiagonal[i];
                    final double si = secondaryBidiagonal[i];
                    final double[] ei0 = ei1;
                    ei1 = eData[i + 1];
                    iData[i + 1] = ei1;
                    for (int j = 0; j < m; ++j) {
                        ei0[j] = (mi * ei0[j] + si * ei1[j]) / singularValues[j];
                    }
                }
                // last row
                final double lastMain = mainBidiagonal[m - 1];
                for (int j = 0; j < m; ++j) {
                    ei1[j] *= lastMain / singularValues[j];
                }
                for (int i = m; i < n; ++i) {
                    iData[i] = new double[m];
                }
                cachedV =
                    transformer.getV().multiply(MatrixUtils.createRealMatrix(iData));
            }

        }

        // return the cached matrix
        return cachedV;

    }

    /** {@inheritDoc} */
    public RealMatrix getVT()
        throws InvalidMatrixException {

        if (cachedVt == null) {
            cachedVt = getV().transpose();
        }

        // return the cached matrix
        return cachedVt;

    }

    /** {@inheritDoc} */
    public RealMatrix getCovariance(final double minSingularValue) {

        // get the number of singular values to consider
        int dimension = 0;
        while ((dimension < n) && (singularValues[dimension] >= minSingularValue)) {
            ++dimension;
        }

        if (dimension == 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "cutoff singular value is {0}, should be at most {1}",
                  minSingularValue, singularValues[0]);
        }

        final double[][] data = new double[dimension][n];
        getVT().walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            /** {@inheritDoc} */
            @Override
            public void visit(final int row, final int column, final double value) {
                data[row][column] = value / singularValues[row];
            }
        }, 0, dimension - 1, 0, n - 1);

        RealMatrix jv = new Array2DRowRealMatrix(data, false);
        return jv.transpose().multiply(jv);

    }

    /** {@inheritDoc} */
    public double getNorm()
        throws InvalidMatrixException {
        return singularValues[0];
    }

    /** {@inheritDoc} */
    public double getConditionNumber()
        throws InvalidMatrixException {
        return singularValues[0] / singularValues[singularValues.length - 1];
    }

    /** {@inheritDoc} */
    public int getRank()
        throws IllegalStateException {

        final double threshold = Math.max(m, n) * Math.ulp(singularValues[0]);

        for (int i = singularValues.length - 1; i >= 0; --i) {
           if (singularValues[i] > threshold) {
              return i + 1;
           }
        }
        return 0;

    }

    /** {@inheritDoc} */
    public DecompositionSolver getSolver() {
        return new Solver(singularValues, getUT(), getV(),
                          getRank() == singularValues.length);
    }

    /** Specialized solver. */
    private static class Solver implements DecompositionSolver {

        /** Singular values. */
        private final double[] singularValues;

        /** U<sup>T</sup> matrix of the decomposition. */
        private final RealMatrix uT;

        /** V matrix of the decomposition. */
        private final RealMatrix v;

        /** Singularity indicator. */
        private boolean nonSingular;

        /**
         * Build a solver from decomposed matrix.
         * @param singularValues singularValues
         * @param uT U<sup>T</sup> matrix of the decomposition
         * @param v V matrix of the decomposition
         * @param nonSingular singularity indicator
         */
        private Solver(final double[] singularValues, final RealMatrix uT, final RealMatrix v,
                       final boolean nonSingular) {
            this.singularValues = singularValues;
            this.uT             = uT;
            this.v              = v;
            this.nonSingular    = nonSingular;
        }

        /** Solve the linear equation A &times; X = B in least square sense.
         * <p>The m&times;n matrix A may not be square, the solution X is
         * such that ||A &times; X - B|| is minimal.</p>
         * @param b right-hand side of the equation A &times; X = B
         * @return a vector X that minimizes the two norm of A &times; X - B
         * @exception IllegalArgumentException if matrices dimensions don't match
         * @exception InvalidMatrixException if decomposed matrix is singular
         */
        public double[] solve(final double[] b)
            throws IllegalArgumentException, InvalidMatrixException {

            if (b.length != singularValues.length) {
                throw MathRuntimeException.createIllegalArgumentException(
                        "vector length mismatch: got {0} but expected {1}",
                        b.length, singularValues.length);
            }

            final double[] w = uT.operate(b);
            for (int i = 0; i < singularValues.length; ++i) {
                final double si = singularValues[i];
                if (si == 0) {
                    throw new SingularMatrixException();
                }
                w[i] /= si;
            }
            return v.operate(w);

        }

        /** Solve the linear equation A &times; X = B in least square sense.
         * <p>The m&times;n matrix A may not be square, the solution X is
         * such that ||A &times; X - B|| is minimal.</p>
         * @param b right-hand side of the equation A &times; X = B
         * @return a vector X that minimizes the two norm of A &times; X - B
         * @exception IllegalArgumentException if matrices dimensions don't match
         * @exception InvalidMatrixException if decomposed matrix is singular
         */
        public RealVector solve(final RealVector b)
            throws IllegalArgumentException, InvalidMatrixException {

            if (b.getDimension() != singularValues.length) {
                throw MathRuntimeException.createIllegalArgumentException(
                        "vector length mismatch: got {0} but expected {1}",
                         b.getDimension(), singularValues.length);
            }

            final RealVector w = uT.operate(b);
            for (int i = 0; i < singularValues.length; ++i) {
                final double si = singularValues[i];
                if (si == 0) {
                    throw new SingularMatrixException();
                }
                w.setEntry(i, w.getEntry(i) / si);
            }
            return v.operate(w);

        }

        /** Solve the linear equation A &times; X = B in least square sense.
         * <p>The m&times;n matrix A may not be square, the solution X is
         * such that ||A &times; X - B|| is minimal.</p>
         * @param b right-hand side of the equation A &times; X = B
         * @return a matrix X that minimizes the two norm of A &times; X - B
         * @exception IllegalArgumentException if matrices dimensions don't match
         * @exception InvalidMatrixException if decomposed matrix is singular
         */
        public RealMatrix solve(final RealMatrix b)
            throws IllegalArgumentException, InvalidMatrixException {

            if (b.getRowDimension() != singularValues.length) {
                throw MathRuntimeException.createIllegalArgumentException(
                        "dimensions mismatch: got {0}x{1} but expected {2}x{3}",
                        b.getRowDimension(), b.getColumnDimension(),
                        singularValues.length, "n");
            }

            final RealMatrix w = uT.multiply(b);
            for (int i = 0; i < singularValues.length; ++i) {
                final double si  = singularValues[i];
                if (si == 0) {
                    throw new SingularMatrixException();
                }
                final double inv = 1.0 / si;
                for (int j = 0; j < b.getColumnDimension(); ++j) {
                    w.multiplyEntry(i, j, inv);
                }
            }
            return v.multiply(w);

        }

        /**
         * Check if the decomposed matrix is non-singular.
         * @return true if the decomposed matrix is non-singular
         */
        public boolean isNonSingular() {
            return nonSingular;
        }

        /** Get the pseudo-inverse of the decomposed matrix.
         * @return inverse matrix
         * @throws InvalidMatrixException if decomposed matrix is singular
         */
        public RealMatrix getInverse()
            throws InvalidMatrixException {

            if (!isNonSingular()) {
                throw new SingularMatrixException();
            }

            return solve(MatrixUtils.createRealIdentityMatrix(singularValues.length));

        }

    }

}
