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
 * This class implements the standard Jacobi (diagonal) preconditioner.
 *
 * @version $Id$
 * @since 3.0
 */
public class JacobiPreconditioner
    extends InvertibleRealLinearOperator {

    /** The diagonal coefficients of the preconditioner. */
    private final ArrayRealVector diag;

    /**
     * Creates a new instance of this class.
     *
     * @param diag Diagonal coefficients of the preconditioner.
     * @param deep {@code true} if a deep copy of the above array should be
     *        performed.
     */
    public JacobiPreconditioner(final double[] diag, final boolean deep) {
        this.diag = new ArrayRealVector(diag, deep);
    }

    /**
     * Creates a new instance of this class. This method extracts the diagonal
     * coefficients of the specified linear operator. If {@code a} does not
     * extend {@link AbstractRealMatrix}, then the coefficients of the
     * underlying matrix are not accessible, coefficient extraction is made by
     * matrix-vector products with the basis vectors (and might therefore take
     * some time). With matrices, direct entry access is carried out.
     *
     * @param a Linear operator for which the preconditioner should be built.
     * @return Preconditioner made of the diagonal coefficients of the specified
     *         linear operator.
     * @throws NonSquareOperatorException if {@code a} is not square.
     */
    public static JacobiPreconditioner create(final RealLinearOperator a)
        throws NonSquareOperatorException {
        final int n = a.getColumnDimension();
        if (a.getRowDimension() != n) {
            throw new NonSquareOperatorException(a.getRowDimension(), n);
        }
        final double[] diag = new double[n];
        if (a instanceof AbstractRealMatrix) {
            final AbstractRealMatrix m = (AbstractRealMatrix) a;
            for (int i = 0; i < n; i++) {
                diag[i] = m.getEntry(i, i);
            }
        } else {
            final ArrayRealVector x = new ArrayRealVector(n);
            for (int i = 0; i < n; i++) {
                x.set(0.);
                x.setEntry(i, 1.);
                diag[i] = a.operate(x).getEntry(i);
            }
        }
        return new JacobiPreconditioner(diag, false);
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnDimension() {
        return diag.getDimension();
    }

    /** {@inheritDoc} */
    @Override
    public int getRowDimension() {
        return diag.getDimension();
    }

    /** {@inheritDoc} */
    @Override
    public RealVector operate(final RealVector x) {
        // Dimension check is carried out by ebeMultiply
        return x.ebeMultiply(diag);
    }

    /** {@inheritDoc} */
    @Override
    public RealVector solve(final RealVector b) {
        // Dimension check is carried out by ebeDivide
        return b.ebeDivide(diag);
    }
}
