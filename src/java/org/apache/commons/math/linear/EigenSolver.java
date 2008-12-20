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
 * Solver using eigen decomposition to solve A &times; X = B for symmetric matrices A.
 * <p>This class finds only exact linear solution, i.e. when
 * ||A &times; X - B|| is exactly 0.</p>
 *   
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class EigenSolver implements DecompositionSolver {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 4339008311386325953L;

    /** Underlying decomposition. */
    private final EigenDecomposition decomposition;

    /**
     * Simple constructor.
     * @param decomposition decomposition to use
     */
    public EigenSolver(final EigenDecomposition decomposition) {
        this.decomposition = decomposition;
    }

    /** Solve the linear equation A &times; X = B for symmetric matrices A.
     * <p>This method only find exact linear solutions, i.e. solutions for
     * which ||A &times; X - B|| is exactly 0.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public double[] solve(final double[] b)
        throws IllegalArgumentException, InvalidMatrixException {

        if (!isNonSingular()) {
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

    /** Solve the linear equation A &times; X = B for symmetric matrices A.
     * <p>This method only find exact linear solutions, i.e. solutions for
     * which ||A &times; X - B|| is exactly 0.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealVector solve(final RealVector b)
        throws IllegalArgumentException, InvalidMatrixException {

        if (!isNonSingular()) {
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

    /** Solve the linear equation A &times; X = B for symmetric matrices A.
     * <p>This method only find exact linear solutions, i.e. solutions for
     * which ||A &times; X - B|| is exactly 0.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a matrix X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix solve(final RealMatrix b)
        throws IllegalArgumentException, InvalidMatrixException {

        if (!isNonSingular()) {
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

        return MatrixUtils.createRealMatrix(bp);

    }

    /**
     * Return the determinant of the matrix
     * @return determinant of the matrix
     * @see #isNonSingular()
     */
    public double getDeterminant() {
        double determinant = 1;
        for (double lambda : decomposition.getEigenvalues()) {
            determinant *= lambda;
        }
        return determinant;
    }

    /**
     * Check if the decomposed matrix is non-singular.
     * @return true if the decomposed matrix is non-singular
     */
    public boolean isNonSingular() {
        for (double lambda : decomposition.getEigenvalues()) {
            if (lambda == 0) {
                return false;
            }
        }
        return true;
    }

    /** Get the inverse of the decomposed matrix.
     * @return inverse matrix
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix getInverse()
        throws InvalidMatrixException {

        if (!isNonSingular()) {
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
        return MatrixUtils.createRealMatrix(invData);

    }

}
