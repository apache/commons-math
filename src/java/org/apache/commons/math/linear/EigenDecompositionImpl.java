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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math.ConvergenceException;

/**
 * Calculates the eigen decomposition of a matrix.
 * <p>The eigen decomposition of matrix A is a set of two matrices:
 * V and D such that A = V &times; D &times; V<sup>T</sup>.
 * Let A be an m &times; n matrix, then V is an m &times; m orthogonal matrix
 * and D is a m &times; n diagonal matrix.</p>
 * <p>This implementation is based on Inderjit Singh Dhillon thesis
 * <a href="http://www.cs.utexas.edu/users/inderjit/public_papers/thesis.pdf">A
 * New O(n<sup>2</sup>) Algorithm for the Symmetric Tridiagonal Eigenvalue/Eigenvector
 * Problem</a>.</p>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class EigenDecompositionImpl implements EigenDecomposition {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -8550254713195393577L;

    /** Eigenvalues. */
    private double[] eigenvalues;

    /** Eigenvectors. */
    private RealVectorImpl[] eigenvectors;

    /** Cached value of V. */
    private RealMatrix cachedV;

    /** Cached value of D. */
    private RealMatrix cachedD;

    /** Cached value of Vt. */
    private RealMatrix cachedVt;

    /**
     * Build a new instance.
     * <p>Note that {@link #decompose(RealMatrix)} <strong>must</strong> be called
     * before any of the {@link #getV()}, {@link #getD()}, {@link #getVT()},
     * {@link #getEignevalues()}, {@link #solve(double[])}, {@link #solve(RealMatrix)},
     * {@link #solve(RealVector)} or {@link #solve(RealVectorImpl)} methods can be
     * called.</p>
     * @see #decompose(RealMatrix)
     */
    public EigenDecompositionImpl() {
    }

    /**
     * Calculates the eigen decomposition of the given matrix. 
     * <p>Calling this constructor is equivalent to first call the no-arguments
     * constructor and then call {@link #decompose(RealMatrix)}.</p>
     * @param matrix The matrix to decompose.
     * @exception InvalidMatrixException (wrapping a {@link ConvergenceException}
     * if algorithm fails to converge
     */
    public EigenDecompositionImpl(RealMatrix matrix)
        throws InvalidMatrixException {
        decompose(matrix);
    }

    /** {@inheritDoc} */
    public void decompose(RealMatrix matrix)
        throws InvalidMatrixException {

        cachedV  = null;
        cachedD  = null;
        cachedVt = null;

        // transform the matrix to tridiagonal
        TriDiagonalTransformer transformer = new TriDiagonalTransformer(matrix);
        final double[] main      = transformer.getMainDiagonalRef();
        final double[] secondary = transformer.getSecondaryDiagonalRef();
        final int m = main.length;

        // pre-compute the square of the secondary diagonal
        double[] squaredSecondary = new double[secondary.length];
        for (int i = 0; i < squaredSecondary.length; ++i) {
            final double s = secondary[i];
            squaredSecondary[i] = s * s;
        }

        // compute the eigenvalues bounds
        List<GershgorinCirclesUnion> bounds =
            getEigenvaluesBounds(main, secondary);

        // TODO this implementation is not finished yet
        // the MRRR algorithm is NOT implemented, Gershgorin circles are
        // merged together when they could be separated, we only perform blindly
        // the basic steps, we search all eigenvalues with an arbitrary
        // threshold, we use twisted factorization afterwards with no
        // heuristic to speed up the selection of the twist index ...
        // The decomposition does work in its current state and seems reasonably
        // efficient when eigenvalues are separated. However, it is expected to
        // fail in difficult cases and its performances can obviously be improved
        // for now, it is slower than JAMA for dimensions below 100 and faster
        // for dimensions above 100. The speed gain with respect to JAMA increase
        // regularly with dimension

        // find eigenvalues using bisection
        eigenvalues = new double[m];
        final double low  = bounds.get(0).getLow();
        final double high = bounds.get(bounds.size() - 1).getHigh();
        final double threshold =
            1.0e-15 * Math.max(Math.abs(low), Math.abs(high));
        findEigenvalues(main, squaredSecondary, low, high, threshold, 0, m);

        // find eigenvectors
        eigenvectors = new RealVectorImpl[m];
        final double[] eigenvector = new double[m];
        final double[] lp          = new double[m - 1];
        final double[] dp          = new double[m];
        final double[] um          = new double[m - 1];
        final double[] dm          = new double[m];
        final double[] gamma       = new double[m];
        for (int i = 0; i < m; ++i) {

            // find the eigenvector of the tridiagonal matrix
            findEigenvector(eigenvalues[i], eigenvector,
                            main, secondary, lp, dp, um, dm, gamma);

            // find the eigenvector of the original matrix
            eigenvectors[i] =
                new RealVectorImpl(transformer.getQ().operate(eigenvector), true);

        }

    }

    /** {@inheritDoc} */
    public RealMatrix getV()
        throws InvalidMatrixException {

        if (cachedV == null) {
            cachedV = getVT().transpose();
        }

        // return the cached matrix
        return cachedV;

    }

    /** {@inheritDoc} */
    public RealMatrix getD()
        throws InvalidMatrixException {

        if (cachedD == null) {

            checkDecomposed();

            final int m = eigenvalues.length;
            final double[][] sData = new double[m][m];
            for (int i = 0; i < m; ++i) {
                sData[i][i] = eigenvalues[i];
            }

            // cache the matrix for subsequent calls
            cachedD = new RealMatrixImpl(sData, false);

        }
        return cachedD;
    }

    /** {@inheritDoc} */
    public RealMatrix getVT()
        throws InvalidMatrixException {

        if (cachedVt == null) {

            checkDecomposed();

            final double[][] vtData = new double[eigenvectors.length][];
            for (int k = 0; k < eigenvectors.length; ++k) {
                vtData[k] = eigenvectors[k].getData();
            }

            // cache the matrix for subsequent calls
            cachedVt = new RealMatrixImpl(vtData, false);

        }

        // return the cached matrix
        return cachedVt;

    }

    /** {@inheritDoc} */
    public double[] getEigenvalues()
        throws InvalidMatrixException {
        checkDecomposed();
        return eigenvalues.clone();
    }

    /** {@inheritDoc} */
    public double getEigenvalue(final int i)
        throws InvalidMatrixException, ArrayIndexOutOfBoundsException {
        checkDecomposed();
        return eigenvalues[i];
    }

    /** {@inheritDoc} */
    public RealVector getEigenvector(final int i)
        throws InvalidMatrixException, ArrayIndexOutOfBoundsException {
        checkDecomposed();
        return eigenvectors[i].copy();
    }

    /** {@inheritDoc} */
    public boolean isNonSingular()
        throws IllegalStateException {
        for (double lambda : eigenvalues) {
            if (lambda == 0) {
                return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    public double[] solve(final double[] b)
        throws IllegalArgumentException, InvalidMatrixException {

        checkNonSingular();

        final int m = eigenvalues.length;
        if (b.length != m) {
            throw new IllegalArgumentException("constant vector has wrong length");
        }

        final double[] bp = new double[m];
        for (int i = 0; i < m; ++i) {
            final RealVectorImpl v = eigenvectors[i];
            final double s = v.dotProduct(b) / eigenvalues[i];
            final double[] vData = v.getDataRef();
            for (int j = 0; j < m; ++j) {
                bp[j] += s * vData[j];
            }
        }

        return bp;

    }

    /** {@inheritDoc} */
    public RealVector solve(final RealVector b)
        throws IllegalArgumentException, InvalidMatrixException {
        try {
            return solve((RealVectorImpl) b);
        } catch (ClassCastException cce) {

            checkNonSingular();

            final int m = eigenvalues.length;
            if (b.getDimension() != m) {
                throw new IllegalArgumentException("constant vector has wrong length");
            }

            final double[] bp = new double[m];
            for (int i = 0; i < m; ++i) {
                final RealVectorImpl v = eigenvectors[i];
                final double s = v.dotProduct(b) / eigenvalues[i];
                final double[] vData = v.getDataRef();
                for (int j = 0; j < m; ++j) {
                    bp[j] += s * vData[j];
                }
            }

            return new RealVectorImpl(bp, false);

        }
    }

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It is </p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X such that A &times; X = B
     * @throws IllegalArgumentException if matrices dimensions don't match
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealVectorImpl solve(final RealVectorImpl b)
        throws IllegalArgumentException, InvalidMatrixException {
        return new RealVectorImpl(solve(b.getDataRef()), false);
    }

    /** {@inheritDoc} */
    public RealMatrix solve(final RealMatrix b)
        throws IllegalArgumentException, InvalidMatrixException {

        checkNonSingular();

        final int m = eigenvalues.length;
        if (b.getRowDimension() != m) {
            throw new IllegalArgumentException("Incorrect row dimension");
        }

        final int nColB = b.getColumnDimension();
        final double[][] bp = new double[m][nColB];
        for (int k = 0; k < nColB; ++k) {
            for (int i = 0; i < m; ++i) {
                final double[] vData = eigenvectors[i].getDataRef();
                double s = 0;
                for (int j = 0; j < m; ++j) {
                    s += vData[j] * b.getEntry(j, k);
                }
                s /= eigenvalues[i];
                for (int j = 0; j < m; ++j) {
                    bp[j][k] += s * vData[j];
                }
            }
        }

        return new RealMatrixImpl(bp, false);

    }

    /** {@inheritDoc} */
    public RealMatrix getInverse()
        throws IllegalStateException, InvalidMatrixException {

        checkNonSingular();
        final int m = eigenvalues.length;
        final double[][] invData = new double[m][m];

        for (int i = 0; i < m; ++i) {
            final double[] invI = invData[i];
            for (int j = 0; j < m; ++j) {
                double invIJ = 0;
                for (int k = 0; k < m; ++k) {
                    final double[] vK = eigenvectors[k].getDataRef();
                    invIJ += vK[i] * vK[j] / eigenvalues[k];
                }
                invI[j] = invIJ;
            }
        }
        return new RealMatrixImpl(invData, false);

    }

    /** {@inheritDoc} */
    public double getDeterminant()
        throws IllegalStateException {
        double determinant = 1;
        for (double lambda : eigenvalues) {
            determinant *= lambda;
        }
        return determinant;
    }

    /**
     * Compute a set of possible bounding intervals for eigenvalues
     * of a symmetric tridiagonal matrix.
     * <p>The intervals are computed by applying the Gershgorin circle theorem.</p>
     * @param main main diagonal
     * @param secondary secondary diagonal of the tridiagonal matrix
     * @return a collection of disjoint intervals where eigenvalues must lie,
     * sorted in increasing order
     */
    private List<GershgorinCirclesUnion> getEigenvaluesBounds(final double[] main,
                                                              final double[] secondary) {

        final SortedSet<GershgorinCirclesUnion> rawCircles =
            new TreeSet<GershgorinCirclesUnion>();
        final int m = main.length;

        // compute all the Gershgorin circles independently
        rawCircles.add(new GershgorinCirclesUnion(main[0],
                                                  Math.abs(secondary[0])));
        for (int i = 1; i < m - 1; ++i) {
            rawCircles.add(new GershgorinCirclesUnion(main[i],
                                                      Math.abs(secondary[i - 1]) +
                                                      Math.abs(secondary[i])));
        }
        rawCircles.add(new GershgorinCirclesUnion(main[m - 1],
                                                  Math.abs(secondary[m - 2])));

        // combine intersecting circles
        final ArrayList<GershgorinCirclesUnion> combined =
            new ArrayList<GershgorinCirclesUnion>();
        GershgorinCirclesUnion current = null;
        for (GershgorinCirclesUnion rawCircle : rawCircles) {
            if (current == null) {
                current = rawCircle;
            } else if (current.intersects(rawCircle)) {
                current.swallow(rawCircle);
            } else {
                combined.add(current);
                current = rawCircle;
            }
        }
        if (current != null) {
            combined.add(current);
        }
        
        return combined;

    }

    /** Find eigenvalues in an interval.
     * @param main main diagonal of the tridiagonal matrix
     * @param squaredSecondary squared secondary diagonal of the tridiagonal matrix
     * @param low lower bound of the search interval
     * @param high higher bound of the search interval
     * @param threshold convergence threshold
     * @param iStart index of the first eigenvalue to find
     * @param iEnd index one unit past the last eigenvalue to find
     */
    private void findEigenvalues(final double[] main, final double[] squaredSecondary,
                                 final double low, final double high, final double threshold,
                                 final int iStart, final int iEnd) {

        // use a simple loop to handle tail-recursion cases
        double currentLow   = low;
        double currentHigh  = high;
        int    currentStart = iStart;
        while (true) {

            final double middle = 0.5 * (currentLow + currentHigh);

            if (currentHigh - currentLow < threshold) {
                // we have found an elementary interval containing one or more eigenvalues
                Arrays.fill(eigenvalues, currentStart, iEnd, middle);
                return;
            }

            // compute the number of eigenvalues below the middle interval point
            final int iMiddle = countEigenValues(main, squaredSecondary, middle);
            if (iMiddle == currentStart) {
                // all eigenvalues are in the upper half of the search interval
                // update the interval and iterate
                currentLow = middle;
            } else if (iMiddle == iEnd) {
                // all eigenvalues are in the lower half of the search interval
                // update the interval and iterate
                currentHigh = middle;                
            } else {
                // split the interval and search eigenvalues in both sub-intervals
                findEigenvalues(main, squaredSecondary, currentLow, middle, threshold,
                                currentStart, iMiddle);
                currentLow   = middle;
                currentStart = iMiddle;
            }

        }

    }

    /**
     * Count the number of eigenvalues below a point.
     * @param main main diagonal of the tridiagonal matrix
     * @param squaredSecondary squared secondary diagonal of the tridiagonal matrix
     * @param mu value below which we must count the number of eigenvalues
     * @return number of eigenvalues smaller than mu
     */
    private int countEigenValues(final double[] main, final double[] squaredSecondary,
                                 final double mu) {
        double ratio = main[0] - mu;
        int count = (ratio > 0) ? 0 : 1;
        for (int i = 1; i < main.length; ++i) {
            ratio = main[i] - squaredSecondary[i - 1] / ratio - mu;
            if (ratio <= 0) {
                ++count;
            }
        }
        return count;
    }

    /**
     * Decompose the shifted tridiagonal matrix A - lambda I as L<sub>+</sub>
     * &times; D<sub>+</sub> &times; U<sub>+</sub>.
     * <p>A shifted symmetric tridiagonal matrix can be decomposed as
     * L<sub>+</sub> &times; D<sub>+</sub> &times; U<sub>+</sub> where L<sub>+</sub>
     * is a lower bi-diagonal matrix with unit diagonal, D<sub>+</sub> is a diagonal
     * matrix and U<sub>+</sub> is the transpose of L<sub>+</sub>. The '+' indice
     * comes from Dhillon's notation since decomposition is done in
     * increasing rows order).</p>
     * @param main main diagonal of the tridiagonal matrix
     * @param secondary secondary diagonal of the tridiagonal matrix
     * @param lambda shift to apply to the matrix before decomposing it
     * @param r index at which factorization should stop (if r is
     * <code>main.length</code>, complete factorization is performed)
     * @param lp placeholder where to put the (r-1) first off-diagonal
     * elements of the L<sub>+</sub> matrix
     * @param dp placeholder where to put the r first diagonal elements
     * of the D<sub>+</sub> matrix
     */
    private void lduDecomposition(final double[] main, final double[] secondary,
                                  final double lambda, final int r,
                                  final double[] lp, final double[] dp) {
        double di = main[0] - lambda;
        dp[0] = di;
        for (int i = 1; i < r; ++i) {
            final double eiM1 = secondary[i - 1];
            final double ratio = eiM1 / di;
            di = main[i] - lambda - eiM1 * ratio;
            lp[i - 1] = ratio;
            dp[i] = di;
        }
    }

    /**
     * Decompose the shifted tridiagonal matrix A - lambda I as U<sub>-</sub>
     * &times; D<sub>-</sub> &times; L<sub>-</sub>.
     * <p>A shifted symmetric tridiagonal matrix can be decomposed as
     * U<sub>-</sub> &times; D<sub>-</sub> &times; L<sub>-</sub> where U<sub>-</sub>
     * is an upper bi-diagonal matrix with unit diagonal, D<sub>-</sub> is a diagonal
     * matrix and L<sub>-</sub> is the transpose of U<sub>-</sub>. The '-' indice
     * comes from Dhillon's notation since decomposition is done in
     * decreasing rows order).</p>
     * @param main main diagonal of the tridiagonal matrix
     * @param secondary secondary diagonal of the tridiagonal matrix
     * @param lambda shift to apply to the matrix before decomposing it
     * @param r index at which factorization should stop (if r is 0, complete
     * factorization is performed)
     * @param um placeholder where to put the m-(r-1) last off-diagonal elements
     * of the U<sub>-</sub> matrix, where m is the size of the original matrix
     * @param dm placeholder where to put the m-r last diagonal elements
     * of the D<sub>-</sub> matrix, where m is the size of the original matrix
     */
    private void udlDecomposition(final double[] main, final double[] secondary,
                                  final double lambda, final int r,
                                  final double[] um, final double[] dm) {
        final int mM1 = main.length - 1;
        double di = main[mM1] - lambda;
        dm[mM1] = di;
        for (int i = mM1 - 1; i >= r; --i) {
            final double ei = secondary[i];
            final double ratio = ei / di;
            di = main[i] - lambda - ei * ratio;
            um[i] = ratio;
            dm[i] = di;
        }
    }

    /**
     * Find an eigenvector corresponding to an eigenvalue.
     * @param eigenvalue eigenvalue for which eigenvector is desired
     * @param eigenvector placeholder where to put the eigenvector
     * @param main main diagonal of the tridiagonal matrix
     * @param secondary secondary diagonal of the tridiagonal matrix
     * @param lp placeholder where to put the off-diagonal elements of the
     * L<sub>+</sub> matrix
     * @param dp placeholder where to put the diagonal elements of the
     * D<sub>+</sub> matrix
     * @param um placeholder where to put the off-diagonal elements of the
     * U<sub>-</sub> matrix
     * @param dm placeholder where to put the diagonal elements of the
     * D<sub>-</sub> matrix
     * @param gamma placeholder where to put the twist elements for all
     * possible twist indices
     */
    private void findEigenvector(final double eigenvalue, final double[] eigenvector,
                                 final double[] main, final double[] secondary,
                                 final double[] lp, final double[] dp,
                                 final double[] um, final double[] dm,
                                 final double[] gamma) {

        // compute the LDU and UDL decomposition of the
        // perfectly shifted tridiagonal matrix
        final int m = main.length;
        lduDecomposition(main, secondary, eigenvalue, m, lp, dp);
        udlDecomposition(main, secondary, eigenvalue, 0, um, dm);

        // select the twist index leading to
        // the least diagonal element in the twisted factorization
        int r = 0;
        double g = dp[0] + dm[0] + eigenvalue - main[0];
        gamma[0] = g;
        double minG = Math.abs(g);
        for (int i = 1; i < m; ++i) {
            if (i < m - 1) {
                g *= dm[i + 1] / dp[i];
            } else {
                g = dp[m - 1] + dm[m - 1] + eigenvalue - main[m - 1];
            }
            gamma[i] = g;
            final double absG = Math.abs(g);
            if (absG < minG) {
                r = i;
                minG = absG;
            }
        }

        // solve the singular system by ignoring the equation
        // at twist index and propagating upwards and downwards
        double n2 = 1;
        eigenvector[r] = 1;
        double z = 1;
        for (int i = r - 1; i >= 0; --i) {
            z *= -lp[i];
            eigenvector[i] = z;
            n2 += z * z;
        }
        z = 1;
        for (int i = r + 1; i < m; ++i) {
            z *= -um[i-1];
            eigenvector[i] = z;
            n2 += z * z;
        }

        // normalize vector
        final double inv = 1.0 / Math.sqrt(n2);
        for (int i = 0; i < m; ++i) {
            eigenvector[i] *= inv;
        }

    }

    /**
     * Check if decomposition has been performed.
     * @exception IllegalStateException if {@link #decompose(RealMatrix) decompose}
     * has not been called
     */
    private void checkDecomposed()
        throws IllegalStateException {
        if (eigenvalues == null) {
            throw new IllegalStateException("no matrix have been decomposed yet");
        }
    }

    /**
     * Check if decomposed matrix is non singular.
     * @exception IllegalStateException if {@link #decompose(RealMatrix) decompose}
     * has not been called
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    private void checkNonSingular()
        throws IllegalStateException, InvalidMatrixException {
        checkDecomposed();
        if (!isNonSingular()) {
            throw new IllegalStateException("matrix is singular");
        }
    }

}
