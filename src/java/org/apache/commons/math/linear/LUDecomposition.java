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

/**
 * An interface to classes that implement an algorithm to calculate the 
 * LU-decomposition of a real matrix.
 * <p>The LU-decomposition of matrix A is a set of three matrices: P, L and U
 * such that P&times;A = L&times;U. P is a rows permutation matrix that is used
 * to rearrange the rows of A before so that it can be decomposed. L is a lower
 * triangular matrix with unit diagonal terms and U is an upper triangular matrix.</p>
 * <p>This interface is based on the class with similar name from the now defunct
 * <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a> library.</p>
 * <ul>
 *   <li>a {@link #getSolver() getSolver} method has been added.</li>
 *   <li>the <code>det</code> method has been renamed as {@link #getDeterminant() getDeterminant}.</li>
 * </ul>
 *   
 * @see <a href="http://mathworld.wolfram.com/LUDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/LU_decomposition">Wikipedia</a>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface LUDecomposition extends Serializable {

    /**
     * Returns the matrix L of the decomposition. 
     * <p>L is an lower-triangular matrix</p>
     * @return the L matrix (or null if decomposed matrix is singular)
     */
    RealMatrix getL();

    /**
     * Returns the matrix U of the decomposition. 
     * <p>U is an upper-triangular matrix</p>
     * @return the U matrix (or null if decomposed matrix is singular)
     */
    RealMatrix getU();

    /**
     * Returns the P rows permutation matrix.
     * <p>P is a sparse matrix with exactly one element set to 1.0 in
     * each row and each column, all other elements being set to 0.0.</p>
     * <p>The positions of the 1 elements are given by the {@link #getPivot()
     * pivot permutation vector}.</p>
     * @return the P rows permutation matrix (or null if decomposed matrix is singular)
     * @see #getPivot()
     */
    RealMatrix getP();

    /**
     * Returns the pivot permutation vector.
     * @return the pivot permutation vector
     * @see #getPermutation()
     */
    int[] getPivot();

    /**
     * Get permutation parity.
     * @return true if there was an even number of permutations
     */
    boolean evenPermutation();

    /**
     * Get the singularity indicator.
     * @return singularity indicator
     */
    boolean isSingular();

    /**
     * Return the determinant of the matrix
     * @return determinant of the matrix
     */
    double getDeterminant();

    /**
     * Get a solver for A &times; X = B.
     * @return a solver
     */
    DecompositionSolver getSolver();

}
