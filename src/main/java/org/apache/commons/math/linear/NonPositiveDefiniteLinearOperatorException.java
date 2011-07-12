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

import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Exception to be thrown when a symmetric, definite positive
 * {@link RealLinearOperator} is expected.
 * Since the coefficients of the matrix are not accessible, the most
 * general definition is used to check that {@code A} is not positive
 * definite, i.e.  there exists {@code x} such that {@code x' A x <= 0}.
 * In the terminology of this exception, {@code A} is the "offending"
 * linear operator and {@code x} the "offending" vector.
 *
 * @version $Id$
 */
public class NonPositiveDefiniteLinearOperatorException
    extends MathIllegalArgumentException {
    /** The offending linear operator.*/
    private final RealLinearOperator a;
    /** A reference to the offending vector. */
    private final RealVector x;

    /**
     * Creates a new instance of this class.
     *
     * @param a Offending linear operator.
     * @param x Offending vector.
     */
    public NonPositiveDefiniteLinearOperatorException(final RealLinearOperator a,
                                                      final double[] x) {
        this(a, new ArrayRealVector(x, false));
    }

    /**
     * Creates a new instance of this class.
     *
     * @param a Offending linear operator.
     * @param x Offending vector.
     */
    public NonPositiveDefiniteLinearOperatorException(final RealLinearOperator a,
                                                      final RealVector x) {
        super(LocalizedFormats.NON_POSITIVE_DEFINITE_LINEAR_OPERATOR, x);
        this.a = a;
        this.x = x;
    }

    /**
     * Returns a reference to the offending vector.
     * If the exception was raised by a call to
     * {@link #NonPositiveDefiniteLinearOperatorException(RealLinearOperator,
     * double[])}, then a new {@link ArrayRealVector} holding a reference to
     * the actual {@code double[]} is returned.
     *
     * @return the offending vector.
     */
    public RealVector copyOffendingVector() {
        return x;
    }

    /**
     * Returns a reference to the offending linear operator.
     *
     * @return the offending linear operator.
     */
    public RealLinearOperator getOffendingLinearOperator() {
        return a;
    }
}
