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

package org.apache.commons.math4.legacy.field.linalg;

/**
 * Interface handling decomposition algorithms that can solve {@code A X = B}.
 *
 * <p>Decomposition algorithms decompose an A matrix has a product of several specific
 * matrices from which they can solve the above system of equations in a least-squares
 * sense: Find X such that {@code ||A X - B||} is minimal.</p>
 *
 * <p>Some solvers like {@link FieldLUDecomposition} can only find the solution for
 * square matrices and when the solution is an exact linear solution, i.e. when
 * {@code ||A X - B||} is exactly 0.
 * Other solvers can also find solutions with non-square matrix {@code A} and with
 * non-zero minimal norm.
 * If an exact linear solution exists it is also the minimal norm solution.</p>
 *
 * @param <T> Type of the field elements.
 *
 * @since 4.0
 */
public interface FieldDecompositionSolver<T> {
    /**
     * Solves the linear equation {@code A X = B}.
     *
     * <p>Matrix {@code A} is implicit: It is provided by the underlying
     * decomposition algorithm.</p>
     *
     * @param b Right-hand side of the equation.
     * @return the matrix {@code X} that minimizes {@code ||A X - B||}.
     * @throws IllegalArgumentException if the dimensions do not match.
     */
    FieldDenseMatrix<T> solve(FieldDenseMatrix<T> b);

    /**
     * Computes the inverse of a decomposed (square) matrix.
     *
     * @return the inverse matrix.
     */
    FieldDenseMatrix<T> getInverse();
}
