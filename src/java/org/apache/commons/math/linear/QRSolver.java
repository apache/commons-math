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
 * Class using QR decomposition to solve A &times; X = B in least square sense
 * for any matrices A.
 * <p>This class solve A &times; X = B in least squares sense: it finds X
 * such that ||A &times; X - B|| is minimal.</p>
 *   
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class QRSolver implements DecompositionSolver {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -579465076068393818L;

    /** Underlying decomposition. */
    private final QRDecomposition decomposition;

    /**
     * Simple constructor.
     * @param decomposition decomposition to use
     */
    public QRSolver(final QRDecomposition decomposition) {
        this.decomposition = decomposition;
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

        if (decomposition.getR().getRowDimension() != b.length) {
            throw new IllegalArgumentException("constant vector has wrong length");            
        }
        if (!isNonSingular()) {
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
        return new RealVectorImpl(solve(b.getData()), false);
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

        if (decomposition.getR().getRowDimension() != b.getRowDimension()) {
            throw new IllegalArgumentException("Incorrect row dimension");            
        }
        if (!isNonSingular()) {
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
     * @return true if the decomposed matrix is non-singular
     */
    public boolean isNonSingular() {
        final RealMatrix r = decomposition.getR();
        final int p = Math.min(r.getRowDimension(), r.getColumnDimension());
        for (int i = 0; i < p; ++i) {
            if (r.getEntry(i, i) == 0) {
                return false;
            }
        }
        return true;
    }

    /** Get the pseudo-inverse of the decomposed matrix.
     * @return inverse matrix
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix getInverse()
        throws InvalidMatrixException {
        final RealMatrix r = decomposition.getR();
        final int p = Math.min(r.getRowDimension(), r.getColumnDimension());
        return solve(MatrixUtils.createRealIdentityMatrix(p));
    }

}
