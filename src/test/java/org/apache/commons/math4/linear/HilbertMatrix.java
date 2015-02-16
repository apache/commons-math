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
package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;

/** This class implements Hilbert Matrices as {@link RealLinearOperator}. */
public class HilbertMatrix
    extends RealLinearOperator {

    /** The size of the matrix. */
    private final int n;

    /**
     * Creates a new instance of this class.
     *
     * @param n Size of the matrix to be created..
     */
    public HilbertMatrix(final int n) {
        this.n = n;
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnDimension() {
        return n;
    }

    /** {@inheritDoc} */
    @Override
    public int getRowDimension() {
        return n;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector operate(final RealVector x) {
        if (x.getDimension() != n) {
            throw new DimensionMismatchException(x.getDimension(), n);
        }
        final double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            double pos = 0.;
            double neg = 0.;
            for (int j = 0; j < n; j++) {
                final double xj = x.getEntry(j);
                final double coeff = 1. / (i + j + 1.);
                // Positive and negative values are sorted out in order to limit
                // catastrophic cancellations (do not forget that Hilbert
                // matrices are *very* ill-conditioned!
                if (xj > 0.) {
                    pos += coeff * xj;
                } else {
                    neg += coeff * xj;
                }
            }
            y[i] = pos + neg;
        }
        return new ArrayRealVector(y, false);
    }
}
