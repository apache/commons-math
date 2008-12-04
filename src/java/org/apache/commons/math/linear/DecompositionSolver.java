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

import org.apache.commons.math.util.MathUtils;

/**
 * Class handling decomposition algorithms that can solve A &times; X = B.
 * <p>This class is the entry point for decomposition algorithms like
 * {@link QRDecomposition}, {@link LUDecomposition}, {@link
 * SingularValueDecomposition} or {@link EigenDecomposition}. All these
 * algorithms decompose an A matrix has a product of several specific matrices
 * from which they can solve A &times; X = B in least squares sense: they find X
 * such that ||A &times; X - B|| is minimal.</p>
 * <p>Some solvers like {@link LUDecomposition} can only find the solution for
 * square matrices and when the solution is an exact linear solution, i.e. when
 * ||A &times; X - B|| is exactly 0. Other solvers can also find solutions
 * with non-square matrix A and with non-null minimal norm. If an exact linear
 * solution exists it is also the minimal norm solution.</p>
 *   
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class DecompositionSolver implements Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 182675257956465253L;

    /** Matrix to decompose. */
    private final RealMatrix matrix;

    /**
     * Build a decomposition solver for a matrix.
     * @param matrix matrix to decompose
     */
    public DecompositionSolver(final RealMatrix matrix) {
        this.matrix = matrix;
    }

    /**
     * Decompose a matrix using eigendecomposition.
     * <p>The split tolerance is set by default to {@link MathUtils#SAFE_MIN}.</p>
     * @exception InvalidMatrixException if matrix does not fulfill
     * the decomposition requirements (for example non-square matrix
     * for {@link LUDecomposition})
     */
    public EigenDecomposition eigenDecompose()
        throws InvalidMatrixException {
        return new EigenDecompositionImpl(matrix, MathUtils.SAFE_MIN);
    }

    /**
     * Decompose a matrix using eigendecomposition.
     * @param splitTolerance tolerance on the off-diagonal elements relative to the
     * geometric mean to split the tridiagonal matrix (a suggested value is
     * {@link MathUtils#SAFE_MIN})
     * @exception InvalidMatrixException if matrix does not fulfill
     * the decomposition requirements (for example non-square matrix
     * for {@link LUDecomposition})
     */
    public EigenDecomposition eigenDecompose(final double splitTolerance)
        throws InvalidMatrixException {
        return new EigenDecompositionImpl(matrix, splitTolerance);
    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public double[] solve(final double[] b, final EigenDecomposition decomposition)
        throws IllegalArgumentException, InvalidMatrixException {

        if (!isNonSingular(decomposition)) {
            throw new SingularMatrixException();
        }

        final double[] eigenvalues = decomposition.getEigenvalues();
        final int m = eigenvalues.length;
        if (b.length != m) {
            throw new IllegalArgumentException("constant vector has wrong length");
        }

        final double[] bp = new double[m];
        for (int i = 0; i < m; ++i) {
            final RealVector v = decomposition.getEigenvector(i);
            final double s = v.dotProduct(b) / eigenvalues[i];
            for (int j = 0; j < m; ++j) {
                bp[j] += s * v.getEntry(j);
            }
        }

        return bp;

    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealVector solve(final RealVector b, final EigenDecomposition decomposition)
        throws IllegalArgumentException, InvalidMatrixException {

        if (!isNonSingular(decomposition)) {
            throw new SingularMatrixException();
        }

        final double[] eigenvalues = decomposition.getEigenvalues();
        final int m = eigenvalues.length;
        if (b.getDimension() != m) {
            throw new IllegalArgumentException("constant vector has wrong length");
        }

        final double[] bp = new double[m];
        for (int i = 0; i < m; ++i) {
            final RealVector v = decomposition.getEigenvector(i);
            final double s = v.dotProduct(b) / eigenvalues[i];
            for (int j = 0; j < m; ++j) {
                bp[j] += s * v.getEntry(j);
            }
        }

        return new RealVectorImpl(bp, false);

    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a matrix X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix solve(final RealMatrix b, final EigenDecomposition decomposition)
        throws IllegalArgumentException, InvalidMatrixException {

        if (!isNonSingular(decomposition)) {
            throw new SingularMatrixException();
        }

        final double[] eigenvalues = decomposition.getEigenvalues();
        final int m = eigenvalues.length;
        if (b.getRowDimension() != m) {
            throw new IllegalArgumentException("Incorrect row dimension");
        }

        final int nColB = b.getColumnDimension();
        final double[][] bp = new double[m][nColB];
        for (int k = 0; k < nColB; ++k) {
            for (int i = 0; i < m; ++i) {
                final RealVector v = decomposition.getEigenvector(i);
                double s = 0;
                for (int j = 0; j < m; ++j) {
                    s += v.getEntry(j) * b.getEntry(j, k);
                }
                s /= eigenvalues[i];
                for (int j = 0; j < m; ++j) {
                    bp[j][k] += s * v.getEntry(j);
                }
            }
        }

        return new RealMatrixImpl(bp, false);

    }

    /**
     * Return the determinant of the matrix
     * @param decomposition decomposition of the matrix A
     * @return determinant of the matrix
     * @see #isNonSingular()
     */
    public double getDeterminant(final EigenDecomposition decomposition) {
        double determinant = 1;
        for (double lambda : decomposition.getEigenvalues()) {
            determinant *= lambda;
        }
        return determinant;
    }

    /**
     * Check if the decomposed matrix is non-singular.
     * @param decomposition decomposition of the matrix A
     * @return true if the decomposed matrix is non-singular
     */
    public boolean isNonSingular(final EigenDecomposition decomposition) {
        for (double lambda : decomposition.getEigenvalues()) {
            if (lambda == 0) {
                return false;
            }
        }
        return true;
    }

    /** Get the inverse of the decomposed matrix.
     * @param decomposition decomposition of the matrix A
     * @return inverse matrix
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix getInverse(final EigenDecomposition decomposition)
        throws InvalidMatrixException {

        if (!isNonSingular(decomposition)) {
            throw new SingularMatrixException();
        }

        final double[] eigenvalues = decomposition.getEigenvalues();
        final int m = eigenvalues.length;
        final double[][] invData = new double[m][m];

        for (int i = 0; i < m; ++i) {
            final double[] invI = invData[i];
            for (int j = 0; j < m; ++j) {
                double invIJ = 0;
                for (int k = 0; k < m; ++k) {
                    final RealVector vK = decomposition.getEigenvector(k);
                    invIJ += vK.getEntry(i) * vK.getEntry(j) / eigenvalues[k];
                }
                invI[j] = invIJ;
            }
        }
        return new RealMatrixImpl(invData, false);

    }

    /**
     * Decompose a matrix using singular value composition.
     * @exception InvalidMatrixException if matrix does not fulfill
     * the decomposition requirements (for example non-square matrix
     * for {@link LUDecomposition})
     */
    public SingularValueDecomposition singularDecompose()
        throws InvalidMatrixException {
        return new SingularValueDecompositionImpl(matrix);
    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public double[] solve(final double[] b, final SingularValueDecomposition decomposition)
        throws IllegalArgumentException, InvalidMatrixException {

        final double[] singularValues = decomposition.getSingularValues();
        if (b.length != singularValues.length) {
            throw new IllegalArgumentException("constant vector has wrong length");
        }

        final double[] w = decomposition.getUT().operate(b);
        for (int i = 0; i < singularValues.length; ++i) {
            final double si = singularValues[i];
            if (si == 0) {
                throw new SingularMatrixException();
            }
            w[i] /= si;
        }
        return decomposition.getV().operate(w);

    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealVector solve(final RealVector b, final SingularValueDecomposition decomposition)
        throws IllegalArgumentException, InvalidMatrixException {

        final double[] singularValues = decomposition.getSingularValues();
        if (b.getDimension() != singularValues.length) {
            throw new IllegalArgumentException("constant vector has wrong length");
        }

        final RealVector w = decomposition.getUT().operate(b);
        for (int i = 0; i < singularValues.length; ++i) {
            final double si = singularValues[i];
            if (si == 0) {
                throw new SingularMatrixException();
            }
            w.set(i, w.getEntry(i) / si);
        }
        return decomposition.getV().operate(w);

    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a matrix X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix solve(final RealMatrix b, final SingularValueDecomposition decomposition)
        throws IllegalArgumentException, InvalidMatrixException {

        final double[] singularValues = decomposition.getSingularValues();
        if (b.getRowDimension() != singularValues.length) {
            throw new IllegalArgumentException("Incorrect row dimension");
        }

        final RealMatrixImpl w = (RealMatrixImpl) decomposition.getUT().multiply(b);
        final double[][] wData = w.getDataRef();
        for (int i = 0; i < singularValues.length; ++i) {
            final double si  = singularValues[i];
            if (si == 0) {
                throw new SingularMatrixException();
            }
            final double inv = 1.0 / si;
            final double[] wi = wData[i];
            for (int j = 0; j < b.getColumnDimension(); ++j) {
                wi[j] *= inv;
            }
        }
        return decomposition.getV().multiply(w);

    }

    /**
     * Check if the decomposed matrix is non-singular.
     * @param decomposition decomposition of the matrix A
     * @return true if the decomposed matrix is non-singular
     */
    public boolean isNonSingular(final SingularValueDecomposition decomposition) {
        return decomposition.getRank() == decomposition.getSingularValues().length;
    }

    /** Get the inverse of the decomposed matrix.
     * @param decomposition decomposition of the matrix A
     * @return inverse matrix
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix getInverse(final SingularValueDecomposition decomposition)
        throws InvalidMatrixException {

        if (!isNonSingular(decomposition)) {
            throw new SingularMatrixException();
        }

        return solve(MatrixUtils.createRealIdentityMatrix(decomposition.getSingularValues().length),
                     decomposition);

    }

    /**
     * Decompose a matrix using QR decomposition.
     */
    public QRDecomposition qrDecompose() {
        return new QRDecompositionImpl(matrix);
    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public double[] solve(final double[] b, final QRDecomposition decomposition)
        throws IllegalArgumentException, InvalidMatrixException {

        if (decomposition.getR().getRowDimension() != b.length) {
            throw new IllegalArgumentException("constant vector has wrong length");            
        }
        if (!isNonSingular(decomposition)) {
            throw new SingularMatrixException();
        }

        // solve Q.y = b, using the fact Q is orthogonal
        final double[] y = decomposition.getQT().operate(b);

        // solve triangular system R.x = y
        final RealMatrix r = decomposition.getR();
        final double[] x = new double[r.getColumnDimension()];
        System.arraycopy(y, 0, x, 0, r.getRowDimension());
        for (int i = r.getRowDimension() - 1; i >= 0; --i) {
            x[i] /= r.getEntry(i, i);
            final double lastX = x[i];
            for (int j = i - 1; j >= 0; --j) {
                x[j] -= lastX * r.getEntry(j, i);
            }
        }

        return x;

    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealVector solve(final RealVector b, final QRDecomposition decomposition)
        throws IllegalArgumentException, InvalidMatrixException {
        return new RealVectorImpl(solve(b.getData(), decomposition), false);
    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a matrix X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix solve(final RealMatrix b, final QRDecomposition decomposition)
        throws IllegalArgumentException, InvalidMatrixException {

        if (decomposition.getR().getRowDimension() != b.getRowDimension()) {
            throw new IllegalArgumentException("Incorrect row dimension");            
        }
        if (!isNonSingular(decomposition)) {
            throw new SingularMatrixException();
        }

        // solve Q.y = b, using the fact Q is orthogonal
        final RealMatrix y = decomposition.getQT().multiply(b);

        // solve triangular system R.x = y
        final RealMatrix r = decomposition.getR();
        final double[][] xData =
            new double[r.getColumnDimension()][b.getColumnDimension()];
        for (int i = 0; i < r.getRowDimension(); ++i) {
            final double[] xi = xData[i];
            for (int k = 0; k < xi.length; ++k) {
                xi[k] = y.getEntry(i, k);
            }
        }
        for (int i = r.getRowDimension() - 1; i >= 0; --i) {
            final double rii = r.getEntry(i, i);
            final double[] xi = xData[i];
            for (int k = 0; k < xi.length; ++k) {
                xi[k] /= rii;
                final double lastX = xi[k];
                for (int j = i - 1; j >= 0; --j) {
                    xData[j][k] -= lastX * r.getEntry(j, i);
                }
            }
        }

        return new RealMatrixImpl(xData, false);

    }

    /**
     * Check if the decomposed matrix is non-singular.
     * @param decomposition decomposition of the matrix A
     * @return true if the decomposed matrix is non-singular
     */
    public boolean isNonSingular(final QRDecomposition decomposition) {
        final RealMatrix r = decomposition.getR();
        final int p = Math.min(r.getRowDimension(), r.getColumnDimension());
        for (int i = 0; i < p; ++i) {
            if (r.getEntry(i, i) == 0) {
                return false;
            }
        }
        return true;
    }

    /** Get the inverse of the decomposed matrix.
     * @param decomposition decomposition of the matrix A
     * @return inverse matrix
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix getInverse(final QRDecomposition decomposition)
        throws InvalidMatrixException {
        final RealMatrix r = decomposition.getR();
        final int p = Math.min(r.getRowDimension(), r.getColumnDimension());
        return solve(MatrixUtils.createRealIdentityMatrix(p), decomposition);
    }

    /**
     * Decompose a matrix using LU decomposition.
     * @exception InvalidMatrixException if matrix is non-square)
     */
    public LUDecomposition luDecompose()
        throws InvalidMatrixException {
        return new LUDecompositionImpl(matrix);
    }

    /**
     * Decompose a matrix using LU decomposition.
     * @param singularityThreshold threshold (based on partial row norm)
     * under which a matrix is considered singular
     * @exception InvalidMatrixException if matrix is non-square)
     */
    public LUDecomposition luDecompose(final double singularityThreshold)
        throws InvalidMatrixException {
        return new LUDecompositionImpl(matrix, singularityThreshold);
    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public double[] solve(final double[] b, final LUDecomposition decomposition)
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


    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealVector solve(final RealVector b, final LUDecomposition decomposition)
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

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It <strong>must</strong> have
     * already been provided by a previous call to {@link #decompose(RealMatrix)}.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @param decomposition decomposition of the matrix A
     * @return a matrix X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix solve(final RealMatrix b, final LUDecomposition decomposition)
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
     * @param decomposition decomposition of the matrix A
     * @return determinant of the matrix
     * @see #isNonSingular()
     */
    public double getDeterminant(final LUDecomposition decomposition) {
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
     * @param decomposition decomposition of the matrix A
     * @return true if the decomposed matrix is non-singular
     */
    public boolean isNonSingular(final LUDecomposition decomposition) {
        return !decomposition.isSingular();
    }

    /** Get the inverse of the decomposed matrix.
     * @param decomposition decomposition of the matrix A
     * @return inverse matrix
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix getInverse(final LUDecomposition decomposition)
        throws InvalidMatrixException {
        final int m = decomposition.getPivot().length;
        return solve(MatrixUtils.createRealIdentityMatrix(m), decomposition);
    }

}
