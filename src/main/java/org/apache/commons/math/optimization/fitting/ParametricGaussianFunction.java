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

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.optimization.fitting.ParametricRealFunction;

/**
 * A Gaussian function.  Specifically:
 * <p>
 * {@code f(x) = a + b*exp(-((x - c)^2 / (2*d^2)))}
 * <p>
 * The parameters have the following meaning:
 * <ul>
 * <li>{@code a} is a constant offset that shifts {@code f(x)} up or down
 * <li>{@code b} is the height of the peak
 * <li>{@code c} is the position of the center of the peak
 * <li>{@code d} is related to the FWHM by {@code FWHM = 2*sqrt(2*ln(2))*d}
 * </ul>
 * Notation key:
 * <ul>
 * <li>{@code x^n}: {@code x} raised to the power of {@code n}
 * <li>{@code exp(x)}: e<sup>x</sup>
 * <li>{@code sqrt(x)}: square root of {@code x}
 * <li>{@code ln(x)}: natural logarithm of {@code x}
 * </ul>
 * References:
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Gaussian_function">Wikipedia:
 *   Gaussian function</a>
 * </ul>
 *
 * @since 2.2
 * @version $Revision$ $Date$
 */
public class ParametricGaussianFunction implements ParametricRealFunction, Serializable {
    /** Serializable version Id. */
    private static final long serialVersionUID = -3875578602503903233L;

    /**
     * Computes value of function {@code f(x)} for the specified {@code x} and
     * parameters {@code a}, {@code b}, {@code c}, and {@code d}.
     *
     * @param x Value at which to compute the function.
     * @return {@code f(x)}.
     * @param parameters Values of {@code a}, {@code b}, {@code c}, and {@code d}.
     * @throws NullArgumentException if {@code parameters} is {@code null}.
     * @throws DimensionMismatchException if the size of {@code parameters} is
     * not 4.
     * @throws ZeroException if {@code parameters[3]} is 0.
     */
    public double value(double x, double[] parameters) {
        validateParameters(parameters);
        final double a = parameters[0];
        final double b = parameters[1];
        final double c = parameters[2];
        final double d = parameters[3];
        final double xMc = x - c;
        return a + b * Math.exp(-xMc * xMc / (2.0 * (d * d)));
    }

    /**
     * Computes the gradient vector for a four variable version of the function
     * where the parameters, {@code a}, {@code b}, {@code c}, and {@code d},
     * are considered the variables, not {@code x}.  That is, instead of
     * computing the gradient vector for the function {@code f(x)} (which would
     * just be the derivative of {@code f(x)} with respect to {@code x} since
     * it's a one-dimensional function), computes the gradient vector for the
     * function {@code f(a, b, c, d) = a + b*exp(-((x - c)^2 / (2*d^2)))}
     * treating the specified {@code x} as a constant.
     * <p>
     * The components of the computed gradient vector are the partial
     * derivatives of {@code f(a, b, c, d)} with respect to each variable.
     * That is, the partial derivative of {@code f(a, b, c, d)} with respect to
     * {@code a}, the partial derivative of {@code f(a, b, c, d)} with respect
     * to {@code b}, the partial derivative of {@code f(a, b, c, d)} with
     * respect to {@code c}, and the partial derivative of {@code f(a, b, c,
     * d)} with respect to {@code d}.
     *
     * @param x Value to be used as constant in {@code f(x, a, b, c, d)}.
     * @param parameters Values of {@code a}, {@code b}, {@code c}, and {@code d}.
     * @return the gradient vector of {@code f(a, b, c, d)}.
     * @throws NullArgumentException if {@code parameters} is {@code null}.
     * @throws DimensionMismatchException if the size of {@code parameters} is
     * not 4.
     * @throws ZeroException if {@code parameters[3]} is 0.
     */
    public double[] gradient(double x, double[] parameters) {
        validateParameters(parameters);
        final double b = parameters[1];
        final double c = parameters[2];
        final double d = parameters[3];

        final double xMc  = x - c;
        final double d2   = d * d;
        final double exp  = Math.exp(-xMc * xMc / (2 * d2));
        final double f    = b * exp * xMc / d2;

        return new double[] { 1.0, exp, f, f * xMc / d };
    }

    /**
     * Validates parameters to ensure they are appropriate for the evaluation of
     * the {@code value} and {@code gradient} methods.
     *
     * @param parameters Values of {@code a}, {@code b}, {@code c}, and {@code d}.
     * @throws NullArgumentException if {@code parameters} is {@code null}.
     * @throws DimensionMismatchException if the size of {@code parameters} is
     * not 4.
     * @throws ZeroException if {@code parameters[3]} is 0.
     */
    private void validateParameters(double[] parameters) {
        if (parameters == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
        }
        if (parameters.length != 4) {
            throw new DimensionMismatchException(4, parameters.length);
        }
        if (parameters[3] == 0) {
            throw new ZeroException();
        }
    }
}
