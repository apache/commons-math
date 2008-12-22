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
 * Class using singular value decomposition decomposition to solve A &times;
 * X = B in least square sense for any matrices A.
 * <p>This class solve A &times; X = B in least squares sense: it finds X
 * such that ||A &times; X - B|| is minimal.</p>
 *   
 * @version $Revision: 723496 $ $Date: 2008-12-05 00:48:18 +0100 (ven., 05 déc. 2008) $
 * @since 2.0
 */
public class SingularValueSolver implements DecompositionSolver {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 4388219358640335388L;

    /** Underlying solver. */
    private final DecompositionSolver solver;

    /**
     * Simple constructor.
     * @param decomposition decomposition to use
     */
    public SingularValueSolver(final SingularValueDecomposition decomposition) {
        this.solver = decomposition.getSolver();
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
        return solver.solve(b);
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
        return solver.solve(b);
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
        return solver.solve(b);
    }

    /**
     * Check if the decomposed matrix is non-singular.
     * @return true if the decomposed matrix is non-singular
     */
    public boolean isNonSingular() {
        return solver.isNonSingular();
    }

    /** Get the pseudo-inverse of the decomposed matrix.
     * @return inverse matrix
     * @throws InvalidMatrixException if decomposed matrix is singular
     */
    public RealMatrix getInverse()
        throws InvalidMatrixException {
        return solver.getInverse();
    }

}
