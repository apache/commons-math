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
 * A base interface to decomposition algorithms that can solve A &times; X = B.
 * <p>This interface is the common base of decomposition algorithms like
 * {@link QRDecomposition} or {@link LUDecomposition}. All these algorithms
 * decompose an A matrix has a product of several specific matrices from
 * which they can solve A &times; X = B.</p>
 * <p>Depending on the solver, the solution is either an exact linear solution
 * or a least squares solution. When an exact linear solution exist, both the
 * linear and the least squares solution are equal. When no exact linear solution
 * exist, a least square solution gives an X which such that A &times; X is the
 * closest possible to B.</p>
 *   
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface DecompositionSolver extends Serializable {

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It is </p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @throws IllegalArgumentException if matrices dimensions don't match
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    double[] solve(double[] b)
      throws IllegalArgumentException, InvalidMatrixException;

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It is </p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a vector X that minimizes the two norm of A &times; X - B
     * @throws IllegalArgumentException if matrices dimensions don't match
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    RealVector solve(RealVector b)
      throws IllegalArgumentException, InvalidMatrixException;

    /** Solve the linear equation A &times; X = B.
     * <p>The A matrix is implicit here. It is </p>
     * @param b right-hand side of the equation A &times; X = B
     * @return a matrix X that minimizes the two norm of A &times; X - B
     * @throws IllegalArgumentException if matrices dimensions don't match
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    RealMatrix solve(RealMatrix b)
      throws IllegalArgumentException, InvalidMatrixException;

}
