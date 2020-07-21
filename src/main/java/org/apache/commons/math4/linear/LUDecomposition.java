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

package org.apache.commons.math4.linear;

import static org.apache.commons.math4.util.Precision.SAFE_MIN;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.util.FastMath;

/* History:
 * 2016/11/05 (wilbur):
 *  	Due to poor performance: reverted to original Jama implementation.
 *  	Changed some fields (initialized by the constructor) to final.
 * 		Made 'Solver' a non-static inner class, no need for passing parameters to constructor.
 * 	TODO: check class FieldLUDecomposition (very similar, possible reuse of this class?)!
 */

/**
 * Calculates the LUP-decomposition of a square matrix.
 * <p>The LUP-decomposition of a matrix A consists of three matrices L, U and
 * P that satisfy: P&times;A = L&times;U. L is lower triangular (with unit
 * diagonal terms), U is upper triangular and P is a permutation matrix. All
 * matrices are m&times;m.</p>
 * <p>As shown by the presence of the P matrix, this decomposition is
 * implemented using partial pivoting.</p>
 * <p>This class is based on the class with similar name from the
 * <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a> library.</p>
 * <ul>
 *   <li>a {@link #getP() getP} method has been added,</li>
 *   <li>the {@code det} method has been renamed as {@link #getDeterminant()
 *   getDeterminant},</li>
 *   <li>the {@code getDoublePivot} method has been removed (but the int based
 *   {@link #getPivot() getPivot} method has been kept),</li>
 *   <li>the {@code solve} and {@code isNonSingular} methods have been replaced
 *   by a {@link #getSolver() getSolver} method and the equivalent methods
 *   provided by the returned {@link DecompositionSolver}.</li>
 * </ul>
 *
 * @see <a href="http://mathworld.wolfram.com/LUDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/LU_decomposition">Wikipedia</a>
 * @since 2.0 (changed to concrete class in 3.0)
 */
public class LUDecomposition {
	/** Default bound to determine effective singularity in LU decomposition. */
	private static final double DEFAULT_TOO_SMALL = 1e-11;
	/** A small number (safe to divide with), see Num. Recipes (3rd ed, p52). */ 
	private static final double TINY = 1e-40;
	/** Bound to determine effective singularity in LU decomposition. */
	private final double singularityThreshold;
	/** Entries of LU decomposition. */
	private final double[][] lu;
	/** Pivot permutation associated with LU decomposition. */
	private final int[] pivot;
	/** Parity of the permutation associated with the LU decomposition. */
	private final boolean even;
	/** Singularity indicator. */
	private final boolean singular;
	private RealMatrix cachedL = null;
	/** Cached value of U. */
	private RealMatrix cachedU = null;
	/** Cached value of P. */
	private RealMatrix cachedP = null;
	/** Column dimension of input matrix */
	private final int m;
	

	/**
	 * Calculates the LU-decomposition of the given matrix.
	 * This constructor uses 1e-11 as default value for the singularity
	 * threshold.
	 *
	 * @param matrix Matrix to decompose.
	 * @throws NonSquareMatrixException if matrix is not square.
	 */
	public LUDecomposition(RealMatrix matrix) {
		this(matrix, DEFAULT_TOO_SMALL);
	}

	/**
     * Calculates the LU-decomposition of the given matrix.
     * @param matrix The matrix to decompose.
     * @param singularityThreshold threshold (based on partial row norm)
     * under which a matrix is considered singular
     * @throws NonSquareMatrixException if matrix is not square
     */
    public LUDecomposition(RealMatrix matrix, double singularityThreshold) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(),
                                               matrix.getColumnDimension());
        }
        
		this.singularityThreshold = singularityThreshold;
		m = matrix.getColumnDimension();
		lu = matrix.getData();
		pivot = new int[m];

		// Initialize permutation array and parity
		for (int row = 0; row < m; row++) {
			pivot[row] = row;
		}

		boolean evenFlg = true; 
		double[] luCol = new double[m];

		// Loop over columns
		for (int col = 0; col < m; col++) {

			// Make a copy of the j-th column to localize references.
			for (int row = 0; row < m; row++) {
				luCol[row] = lu[row][col];
			}

			// Apply previous transformations.
			for (int row = 0; row < m; row++) {
				double[] luRow = lu[row];
				
				// Most of the time is spent in the following dot product.
				final int kmax = FastMath.min(row, col);
				double sum = 0.0;
				for (int k = 0; k < kmax; k++) {
					sum += luRow[k] * luCol[k];
				}
				luCol[row] = luCol[row] - sum;
				luRow[col] = luCol[row];
			}

			// Find pivot.
			int p = col;
			for (int i = col + 1; i < m; i++) {
	            if (FastMath.abs(luCol[i]) > FastMath.abs(luCol[p])) {
	               p = i;
	            }
	         }
			
            // Singularity check
            if (FastMath.abs(lu[p][col]) < this.singularityThreshold) {
                singular = true;
                even = evenFlg;
                return;
            }

			// Exchange if necessary.
			if (p != col) {
				// swap columns lu[p][*] <-> lu[col][*]
				for (int k = 0; k < m; k++) {
					// swap lu[p][k] <-> lu[col][k]:
					final double tmp = lu[p][k];
					lu[p][k] = lu[col][k];
					lu[col][k] = tmp;
				}
				// swap pivot[p] <-> pivot[col]:
				final int k = pivot[p];
				pivot[p] = pivot[col];
				pivot[col] = k;
				evenFlg = !evenFlg;
			}

			// Divide the lower elements by the "winning" diagonal element.
			final double luDiag = (FastMath.abs(lu[col][col]) > SAFE_MIN) ?
					lu[col][col] : TINY;
			for (int row = col + 1; row < m; row++) {
				lu[row][col] /= luDiag;
			}
		}

		even = evenFlg;
		singular = false;
	}

	/** 
	 * Checks if the input matrix is singular.
	 * @return true if U, and hence A, is singular.
	 * */
//	private boolean checkSingularity() {
//		for (int i = 0; i < m; i++) {
//			if (FastMath.abs(lu[i][i]) < singularityThreshold)
//				return true;
//		}
//		return false;
//	}

    /**
     * Returns the matrix L of the decomposition.
     * <p>L is a lower-triangular matrix</p>
     * @return the L matrix (or null if decomposed matrix is singular)
     */
    public RealMatrix getL() {
        if ((cachedL == null) && !singular) {
            final int m = pivot.length;
            cachedL = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; i++) {
                final double[] luI = lu[i];
                for (int j = 0; j < i; j++) {
                    cachedL.setEntry(i, j, luI[j]);
                }
                cachedL.setEntry(i, i, 1.0);
            }
        }
        return cachedL;
    }

    /**
     * Returns the matrix U of the decomposition.
     * <p>U is an upper-triangular matrix</p>
     * @return the U matrix (or null if decomposed matrix is singular)
     */
    public RealMatrix getU() {
        if ((cachedU == null) && !singular) {
            final int m = pivot.length;
            cachedU = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; i++) {
                final double[] luI = lu[i];
                for (int j = i; j < m; j++) {
                    cachedU.setEntry(i, j, luI[j]);
                }
            }
        }
        return cachedU;
    }

    /**
     * Returns the P rows permutation matrix.
     * <p>P is a sparse matrix with exactly one element set to 1.0 in
     * each row and each column, all other elements being set to 0.0.</p>
     * <p>The positions of the 1 elements are given by the {@link #getPivot()
     * pivot permutation vector}.</p>
     * @return the P rows permutation matrix (or null if decomposed matrix is singular)
     * @see #getPivot()
     */
    public RealMatrix getP() {
        if ((cachedP == null) && !singular) {
            final int m = pivot.length;
            cachedP = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; i++) {
                cachedP.setEntry(i, pivot[i], 1.0);
            }
        }
        return cachedP;
    }

	/**
	 * Returns the pivot permutation vector.
	 * @return the pivot permutation vector
	 * @see #getP()
	 */
	public int[] getPivot() {
		return pivot.clone();
	}

	/**
	 * Calculates and returns the determinant of the input matrix (A).
	 * @return determinant of the matrix
	 */
	public double getDeterminant() {
        if (singular) {
            return 0.0;
        }
        else {
			double determinant = even ? 1 : -1;
			for (int i = 0; i < m; i++) {
				determinant *= lu[i][i];
			}
			return determinant;
        }
	}

	/**
	 * Get a solver for finding the A &times; X = B solution in exact linear
	 * sense.
	 * @return a new solver
	 */
	public DecompositionSolver getSolver() {
		return new Solver();
	}

	/** Specialized solver. */
	private class Solver implements DecompositionSolver {

		/** No instantiation from outside */
		private Solver() {
		}

		/** {@inheritDoc} */
		@Override
		public boolean isNonSingular() {
			return !singular;
		}

		/** {@inheritDoc} */
		@Override
		public RealVector solve(RealVector b) {
			if (b.getDimension() != m) {
				throw new DimensionMismatchException(b.getDimension(), m);
			}
			if (!isNonSingular()) {
				throw new SingularMatrixException();
			}

			final double[] bp = new double[m];
			
			// Apply permutations to b
			for (int row = 0; row < m; row++) {
				bp[row] = b.getEntry(pivot[row]);
			}

			// Solve LY = b
			for (int col = 0; col < m; col++) {
                final double bpCol = bp[col];
                for (int i = col + 1; i < m; i++) {
                    bp[i] -= bpCol * lu[i][col];
                }
            }

			// Solve UX = Y
			for (int col = m - 1; col >= 0; col--) {
                bp[col] /= lu[col][col];
                final double bpCol = bp[col];
                for (int i = 0; i < col; i++) {
                    bp[i] -= bpCol * lu[i][col];
                }
            }

			return new ArrayRealVector(bp, false);
		}

		/** {@inheritDoc} */
		@Override
		public RealMatrix solve(RealMatrix B) {
			
			if (B.getRowDimension() != m) {
				throw new DimensionMismatchException(B.getRowDimension(), m);
			}
			if (!isNonSingular()) {
				throw new SingularMatrixException();
			}

			final int nColB = B.getColumnDimension();

			// Apply permutations to B
			final double[][] bp = new double[m][nColB];
			for (int row = 0; row < m; row++) {
				final double[] bpRow = bp[row];
				final int pRow = pivot[row];
				for (int col = 0; col < nColB; col++) {
					bpRow[col] = B.getEntry(pRow, col);
				}
			}

			// Solve LY = b
			for (int col = 0; col < m; col++) {
                final double[] bpCol = bp[col];
                for (int i = col + 1; i < m; i++) {
                    final double[] bpI = bp[i];
                    final double luICol = lu[i][col];
                    for (int j = 0; j < nColB; j++) {
                        bpI[j] -= bpCol[j] * luICol;
                    }
                }
            }

			// Solve UX = Y
			for (int col = m - 1; col >= 0; col--) {
                final double[] bpCol = bp[col];
                final double luDiag = lu[col][col];
                for (int j = 0; j < nColB; j++) {
                    bpCol[j] /= luDiag;
                }
                for (int i = 0; i < col; i++) {
                    final double[] bpI = bp[i];
                    final double luICol = lu[i][col];
                    for (int j = 0; j < nColB; j++) {
                        bpI[j] -= bpCol[j] * luICol;
                    }
                }
            }
			
			return new Array2DRowRealMatrix(bp, false);
		}

		/**
		 * Get the inverse of the decomposed matrix.
		 * @return the inverse matrix.
		 * @throws SingularMatrixException if the decomposed matrix is singular.
		 */
		@Override
		public RealMatrix getInverse() {
			return solve(MatrixUtils.createRealIdentityMatrix(m));
		}
	}
}
