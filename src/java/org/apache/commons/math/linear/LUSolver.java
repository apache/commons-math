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
 * Solver using LU decomposition to solve A &times; X = B for square matrices A.
 * <p>This class finds only exact linear solution, i.e. when
 * ||A &times; X - B|| is exactly 0.</p>
 *   
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class LUSolver implements DecompositionSolver {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -369589527412301256L;

    /** Underlying solver. */
    private final DecompositionSolver solver;

    /** Determinant. */
    private final double determinant;

    /**
     * Simple constructor.
     * @param decomposition decomposition to use
     */
    public LUSolver(final LUDecomposition decomposition) {
        this.solver      = decomposition.getSolver();
        this.determinant = decomposition.getDeterminant();
    }

    /** Solve the linear equation A &times; X = B for square matrices A.
     * <p>This method only find exact linear solutions, i.e. solutions for
     * which ||A &times; X - B|| is exactly 0.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public double[] solve(final double[] b)
        throws IllegalArgumentException, InvalidMatrixException {
        return solver.solve(b);
    }


    /** Solve the linear equation A &times; X = B for square matrices A.
     * <p>This method only find exact linear solutions, i.e. solutions for
     * which ||A &times; X - B|| is exactly 0.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealVector solve(final RealVector b)
        throws IllegalArgumentException, InvalidMatrixException {
        return solver.solve(b);
    }

    /** Solve the linear equation A &times; X = B for square matrices A.
     * <p>This method only find exact linear solutions, i.e. solutions for
     * which ||A &times; X - B|| is exactly 0.</p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a matrix X that minimizes the two norm of A &times; X - B
     * @exception IllegalArgumentException if matrices dimensions don't match
     * @exception InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix solve(final RealMatrix b)
        throws IllegalArgumentException, InvalidMatrixException {
        return solver.solve(b);
    }

    /**
     * Check if the decomposed matrix is non-singular.
     * @return true if the decomposed matrix is non-singular
     */
    public boolean isNonSingular() {
        return solver.isNonSingular();
    }

    /** Get the inverse of the decomposed matrix.
     * @return inverse matrix
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix getInverse()
        throws InvalidMatrixException {
        return solver.getInverse();
    }

    /**
     * Return the determinant of the matrix
     * @return determinant of the matrix
     */
    public double getDeterminant() {
        return determinant;
    }

}
