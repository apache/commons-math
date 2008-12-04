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
 * Singular Value Decomposition of a real matrix.
 * <p>The Singular Value Decomposition of matrix A is a set of three matrices:
 * U, &Sigma; and V such that A = U &times; &Sigma; &times; V<sup>T</sup>.
 * Let A be an m &times; n matrix, then U is an m &times; m orthogonal matrix,
 * &Sigma; is a m &times; n diagonal matrix with positive diagonal elements,
 * and V is an n &times; n orthogonal matrix.</p>
 * <p>This interface is similar to the class with similar name from the now defunct
 * <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a> library, with the
 * following changes:</p>
 * <ul>
 *   <li><code>solve</code> methods have been added (in the superinterface),</li>
 *   <li>a {@link DecompositionSolver#decompose(RealMatrix) decompose(RealMatrix)}
 *   method has been added (in the superinterface),</li>
 *   <li>a {@link DecompositionSolver#isNonSingular() isNonSingular} method has
 *   been added (in the superinterface),</li>
 *   <li>a {@link DecompositionSolver#getInverse() getInverse} method has been
 *   added (in the superinterface),</li>
 *   <li>the <code>norm2</code> method which has been renamed as {@link #getNorm()
 *   getNorm},</li>
 *   <li>the <code>cond</code> method which has been renamed as {@link
 *   #getConditionNumber() getConditionNumber},</li>
 *   <li>the <code>rank</code> method which has been renamed as {@link #getRank()
 *   getRank}</li>
 * </ul>
 * @see <a href="http://mathworld.wolfram.com/SingularValueDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/Singular_value_decomposition">Wikipedia</a>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface SingularValueDecomposition extends Serializable {

    /**
     * Returns the matrix U of the decomposition. 
     * <p>U is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * @return the U matrix
     * @exception IllegalStateException if {@link
     * DecompositionSolver#decompose(RealMatrix) decompose} has not been called
     * @see #getUT()
     */
    RealMatrix getU() throws IllegalStateException;

    /**
     * Returns the transpose of the matrix U of the decomposition. 
     * <p>U is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * @return the U matrix (or null if decomposed matrix is singular)
     * @exception IllegalStateException if {@link
     * DecompositionSolver#decompose(RealMatrix) decompose} has not been called
     * @see #getU()
     */
    RealMatrix getUT() throws IllegalStateException;

    /**
     * Returns the diagonal matrix &Sigma; of the decomposition. 
     * <p>&Sigma; is a diagonal matrix. The singular values are provided in
     * non-increasing order, for compatibility with Jama.</p>
     * @return the &Sigma; matrix
     * @exception IllegalStateException if {@link
     * DecompositionSolver#decompose(RealMatrix) decompose} has not been called
     */
    RealMatrix getS() throws IllegalStateException;

    /**
     * Returns the diagonal elements of the matrix &Sigma; of the decomposition.
     * <p>The singular values are provided in non-increasing order, for
     * compatibility with Jama.</p>
     * @return the diagonal elements of the &Sigma; matrix
     * @exception IllegalStateException if {@link
     * DecompositionSolver#decompose(RealMatrix) decompose} has not been called
     */
    double[] getSingularValues() throws IllegalStateException;

    /**
     * Returns the matrix V of the decomposition. 
     * <p>V is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * @return the V matrix (or null if decomposed matrix is singular)
     * @exception IllegalStateException if {@link
     * DecompositionSolver#decompose(RealMatrix) decompose} has not been called
     * @see #getVT()
     */
    RealMatrix getV() throws IllegalStateException;

    /**
     * Returns the transpose of the matrix V of the decomposition. 
     * <p>V is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * @return the V matrix (or null if decomposed matrix is singular)
     * @exception IllegalStateException if {@link
     * DecompositionSolver#decompose(RealMatrix) decompose} has not been called
     * @see #getV()
     */
    RealMatrix getVT() throws IllegalStateException;

    /**
     * Returns the L<sub>2</sub> norm of the matrix.
     * <p>The L<sub>2</sub> norm is max(|A &times; u|<sub>2</sub> /
     * |u|<sub>2</sub>), where |.|<sub>2</sub> denotes the vectorial 2-norm
     * (i.e. the traditional euclidian norm).</p>
     * @return norm
     * @exception IllegalStateException if {@link
     * DecompositionSolver#decompose(RealMatrix) decompose} has not been called
     */
    double getNorm() throws IllegalStateException;

    /**
     * Return the condition number of the matrix.
     * @return condition number of the matrix
     * @exception IllegalStateException if {@link
     * DecompositionSolver#decompose(RealMatrix) decompose} has not been called
     */
    double getConditionNumber() throws IllegalStateException;

    /**
     * Return the effective numerical matrix rank.
     * <p>The effective numerical rank is the number of non-negligible
     * singular values. The threshold used to identify non-negligible
     * terms is max(m,n) &times; ulp(s<sub>1</sub>) where ulp(s<sub>1</sub>)
     * is the least significant bit of the largest singular value.</p>
     * @return effective numerical matrix rank
     * @exception IllegalStateException if {@link
     * DecompositionSolver#decompose(RealMatrix) decompose} has not been called
     */
    int getRank() throws IllegalStateException;

}
