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

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.MaxCountExceededException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.util.IterationManager;

/**
 * This abstract class defines preconditioned iterative solvers. When A is
 * ill-conditioned, instead of solving system A &middot; x = b directly, it is
 * preferable to solve M<sup>-1</sup> &middot; A &middot; x = M<sup>-1</sup>
 * &middot; b, where M approximates in some way A, while remaining comparatively
 * easier to invert. M (not M<sup>-1</sup>!) is called the
 * <em>preconditionner</em>.
 *
 * @version $Id$
 * @since 3.0
 */
public abstract class PreconditionedIterativeLinearSolver
    extends IterativeLinearSolver {

    /**
     * Creates a new instance of this class, with default iteration manager.
     *
     * @param maxIterations Maximum number of iterations.
     */
    public PreconditionedIterativeLinearSolver(final int maxIterations) {
        super(maxIterations);
    }

    /**
     * Creates a new instance of this class, with custom iteration manager.
     *
     * @param manager Custom iteration manager.
     */
    public PreconditionedIterativeLinearSolver(final IterationManager manager) {
        super(manager);
    }

    /**
     * Performs all dimension checks on the parameters of
     * {@link #solve(RealLinearOperator, InvertibleRealLinearOperator, RealVector, RealVector, boolean)}
     * , and throws an exception if one of the checks fails.
     *
     * @param a Linear operator A of the system.
     * @param m Preconditioner (can be {@code null}).
     * @param b Right-hand side vector.
     * @param x0 Initial guess of the solution (can be {@code null} if
     *        {@code inPlace} is set to {@code false}).
     * @param inPlace {@code true} if the initial guess is to be updated with
     *        the current estimate of the solution.
     * @throws NullArgumentException if one of the parameters is {@code null}.
     * @throws NonSquareLinearOperatorException if {@code a} or {@code m} is not
     *         square.
     * @throws DimensionMismatchException if {@code m}, {@code b} or {@code x}
     *         have dimensions inconsistent with {@code a}.
     */
    protected static void checkParameters(final RealLinearOperator a,
                                          final InvertibleRealLinearOperator m,
                                          final RealVector b,
                                          final RealVector x0,
                                          final boolean inPlace)
        throws NullArgumentException, NonSquareLinearOperatorException,
        DimensionMismatchException {
        checkParameters(a, b, x0, inPlace);
        if (m != null) {
            if (m.getColumnDimension() != m.getRowDimension()) {
                throw new NonSquareLinearOperatorException(
                                                           m.getColumnDimension(),
                                                           m.getRowDimension());
            }
            if (m.getRowDimension() != a.getRowDimension()) {
                throw new DimensionMismatchException(m.getRowDimension(),
                                                     a.getRowDimension());
            }
        }
    }

    /**
     * Returns an estimate of the solution to the linear system A &middot; x =
     * b. If no initial estimate of the solution is provided, (0, &hellip;, 0)
     * is assumed.
     *
     * @param a Linear operator A of the system.
     * @param m Preconditioner (can be {@code null}).
     * @param b Right-hand side vector.
     * @param x0 Initial guess of the solution (can be {@code null} if
     *        {@code inPlace} is set to {@code false}).
     * @param inPlace {@code true} if the initial guess is to be updated with
     *        the current estimate of the solution.
     * @return A reference to {@code x0} (shallow copy) if {@code update} was
     *         set to {@code true}. Otherwise, a new vector containing the
     *         solution.
     * @throws NullArgumentException if one of the parameters is {@code null}.
     * @throws NonSquareLinearOperatorException if {@code a} or {@code m} is not
     *         square.
     * @throws DimensionMismatchException if {@code m}, {@code b} or {@code x}
     *         have dimensions inconsistent with {@code a}.
     * @throws MaxCountExceededException at exhaustion of the iteration count,
     *         unless a custom {@link MaxCountExceededCallback callback} has
     *         been set at construction.
     */
    public abstract RealVector solve(RealLinearOperator a,
                                     InvertibleRealLinearOperator m,
                                     RealVector b, RealVector x0,
                                     final boolean inPlace)
        throws NullArgumentException, NonSquareLinearOperatorException,
        DimensionMismatchException, MaxCountExceededException;

    /** {@inheritDoc} */
    @Override
    public RealVector solve(final RealLinearOperator a, final RealVector b,
                            final RealVector x0, final boolean inPlace)
        throws NullArgumentException, NonSquareLinearOperatorException,
        DimensionMismatchException, MaxCountExceededException {
        checkParameters(a, b, x0, inPlace);
        return solve(a, null, b, x0, inPlace);
    }
}
