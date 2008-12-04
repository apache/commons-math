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

import org.apache.commons.math.ConvergenceException;
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

    /** Serializable version identifier. */
    private static final long serialVersionUID = -238768772547767847L;

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
    double[] mainTridiagonal;

    /** Secondary diagonal of the tridiagonal matrix. */
    double[] secondaryTridiagonal;

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
     * <p>Calling this constructor is equivalent to first call the no-arguments
     * constructor and then call {@link #decompose(RealMatrix)}.</p>
     * @param matrix The matrix to decompose.
     * @exception InvalidMatrixException (wrapping a {@link ConvergenceException}
     * if algorithm fails to converge
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
        singularValues = eigenDecomposition.getEigenvalues();
        for (int i = 0; i < singularValues.length; ++i) {
            singularValues[i] = Math.sqrt(singularValues[i]);
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
                    // compute Bt.E.S^(-1) where E is the eigenvectors matrix
                    // we reuse the array from matrix E to store the result 
                    final double[] ei0 = ei1;
                    ei1 = eData[i + 1];
                    iData[i + 1] = ei1;
                    for (int j = 0; j < n; ++j) {
                        ei0[j] = (mainBidiagonal[i] * ei0[j] +
                                  secondaryBidiagonal[i] * ei1[j]) / singularValues[j];
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
                    transformer.getU().multiply(new RealMatrixImpl(iData, false));
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

            final int p = singularValues.length;
            final double[][] sData = new double[p][p];
            for (int i = 0; i < p; ++i) {
                sData[i][i] = singularValues[i];
            }

            // cache the matrix for subsequent calls
            cachedS = new RealMatrixImpl(sData, false);

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
                    final double[] ei0 = ei1;
                    ei1 = eData[i + 1];
                    iData[i + 1] = ei1;
                    for (int j = 0; j < m; ++j) {
                        ei0[j] = (mainBidiagonal[i] * ei0[j] +
                                  secondaryBidiagonal[i] * ei1[j]) / singularValues[j];
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
                    transformer.getV().multiply(new RealMatrixImpl(iData, false));
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

}
