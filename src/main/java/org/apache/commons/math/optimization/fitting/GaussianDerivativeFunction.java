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

package org.apache.commons.math.optimization.fitting;

import java.io.Serializable;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.NullArgumentException;

/**
 * The derivative of {@link GaussianFunction}.  Specifically:
 * <p>
 * {@code f'(x) = (-b / (d^2)) * (x - c) * exp(-((x - c)^2) / (2*(d^2)))}
 * <p>
 * Notation key:
 * <ul>
 * <li>{@code x^n}: {@code x} raised to the power of {@code n}
 * <li>{@code exp(x)}: <i>e</i><sup>x</sup>
 * </ul>
 *
 * @since 2.2
 * @version $Revision$ $Date$
 */
public class GaussianDerivativeFunction implements UnivariateRealFunction, Serializable {
    /** Serializable version identifier. */
    private static final long serialVersionUID = -6500229089670174766L;
    /** Parameter b of this function. */
    private final double b;
    /** Parameter c of this function. */
    private final double c;
    /** Square of the parameter d of this function. */
    private final double d2;

    /**
     * Constructs an instance with the specified parameters.
     *
     * @param b {@code b} parameter value.
     * @param c {@code c} parameter value.
     * @param d {@code d} parameter value.
     *
     * @throws IllegalArgumentException if <code>d</code> is 0
     */
    public GaussianDerivativeFunction(double b, double c, double d) {
        if (d == 0.0) {
            throw new ZeroException();
        }
        this.b = b;
        this.c = c;
        this.d2 = d * d;
    }

    /**
     * Constructs an instance with the specified parameters.
     *
     * @param parameters {@code b}, {@code c} and {@code d} parameter values.
     * @throws NullArgumentException if {@code parameters} is {@code null}.
     * @throws DimensionMismatchException if the size of {@code parameters} is
     * not 3.
     * @throws ZeroException if {@code parameters[2]} is 0.
     */
    public GaussianDerivativeFunction(double[] parameters) {
        if (parameters == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
        }
        if (parameters.length != 3) {
            throw new DimensionMismatchException(3, parameters.length);
        }
        if (parameters[2] == 0.0) {
            throw new ZeroException();
        }
        this.b = parameters[0];
        this.c = parameters[1];
        this.d2 = parameters[2] * parameters[2];
    }

    /** {@inheritDoc} */
    public double value(double x) {
        final double xMc = x - c;
        return (-b / d2) * xMc * Math.exp(-(xMc * xMc) / (2.0 * d2));
    }
}
