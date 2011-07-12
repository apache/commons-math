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
 * Exception to be thrown when a self-adjoint {@link RealLinearOperator}
 * is expected.
 * Since the coefficients of the matrix are not accessible, the most
 * general definition is used to check that A is not self-adjoint, i.e.
 * there exist x and y such as {@code | x' A y - y' A x | >= eps},
 * where {@code eps} is a user-specified tolerance, and {@code x'}
 * denotes the transpose of {@code x}.
 * In the terminology of this exception, {@code A} is the "offending"
 * linear operator, {@code x} and {@code y} are the first and second
 * "offending" vectors, respectively.
 *
 * @version $Id$
 */
public class NonSelfAdjointLinearOperatorException
    extends MathIllegalArgumentException {
    /** The offending linear operator, A. */
    private final RealLinearOperator a;
    /** The threshold. */
    private final double threshold;
    /** A reference to the first offending vector*/
    private final RealVector x;
    /** A reference to the second offending vector*/
    private final RealVector y;

    /**
     * Creates a new instance of this class.
     *
     * @param a Offending linear operator.
     * @param x First offending vector.
     * @param y Second offending vector.
     * @param threshold Threshold.
     */
    public NonSelfAdjointLinearOperatorException(final RealLinearOperator a,
                                                 final double[] x,
                                                 final double[] y,
                                                 final double threshold) {
        this(a,
             new ArrayRealVector(x, false),
             new ArrayRealVector(y, false),
             threshold);
    }

    /**
     * Creates a new instance of this class.
     *
     * @param a Offending linear operator.
     * @param x First offending vector.
     * @param y Second offending vector.
     * @param threshold Threshold.
     */
    public NonSelfAdjointLinearOperatorException(final RealLinearOperator a,
                                                 final RealVector x,
                                                 final RealVector y,
                                                 final double threshold) {
        super(LocalizedFormats.NON_SELF_ADJOINT_LINEAR_OPERATOR, threshold, x, y);
        this.a = a;
        this.x = x;
        this.y = y;
        this.threshold = threshold;
    }

    /**
     * Returns a reference to the first offending vector.
     * If the exception was raised by a call to
     * {@link #NonSelfAdjointLinearOperatorException(RealLinearOperator,
     * double[], double[], double)}, then a new {@link ArrayRealVector}
     * holding a reference to the actual {@code double[]} is returned.
     *
     * @return the first offending vector.
     */
    public RealVector getFirstOffendingVector() {
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

    /**
     * Returns a copy of the second offending vector.
     * If the exception was raised by a call to
     * {@link #NonSelfAdjointLinearOperatorException(RealLinearOperator,
     * double[], double[], double)}, then a new {@link ArrayRealVector}
     * holding a reference to the actual {@code double[]} is returned.
     *
     * @return the second offending vector.
     */
    public RealVector getSecondOffendingVector() {
        return y;
    }

    /**
     * Returns the threshold.
     *
     * @return the threshold.
     */
    public double getThreshold() {
        return threshold;
    }
}
