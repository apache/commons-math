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

package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

/**
 * <a href="http://en.wikipedia.org/wiki/Gaussian_function">
 *  Gaussian</a> function.
 *
 * @version $Id$
 * @since 3.0
 */
public class Gaussian implements DifferentiableUnivariateFunction {
    /** Mean. */
    private final double mean;
    /** Inverse of twice the square of the standard deviation. */
    private final double i2s2;
    /** Normalization factor. */
    private final double norm;

    /**
     * Gaussian with given normalization factor, mean and standard deviation.
     *
     * @param norm Normalization factor.
     * @param mean Mean.
     * @param sigma Standard deviation.
     * @throws NotStrictlyPositiveException if {@code sigma <= 0}.
     */
    public Gaussian(double norm,
                    double mean,
                    double sigma) {
        if (sigma <= 0) {
            throw new NotStrictlyPositiveException(sigma);
        }

        this.norm = norm;
        this.mean = mean;
        this.i2s2 = 1 / (2 * sigma * sigma);
    }

    /**
     * Normalized gaussian with given mean and standard deviation.
     *
     * @param mean Mean.
     * @param sigma Standard deviation.
     * @throws NotStrictlyPositiveException if {@code sigma <= 0}.
     */
    public Gaussian(double mean,
                    double sigma) {
        this(1 / (sigma * FastMath.sqrt(2 * Math.PI)), mean, sigma);
    }

    /**
     * Normalized gaussian with zero mean and unit standard deviation.
     */
    public Gaussian() {
        this(0, 1);
    }

    /** {@inheritDoc} */
    public double value(double x) {
        return value(x - mean, norm, i2s2);
    }

    /** {@inheritDoc} */
    public UnivariateFunction derivative() {
        return new UnivariateFunction() {
            /** {@inheritDoc} */
            public double value(double x) {
                final double diff = x - mean;
                final double g = Gaussian.value(diff, norm, i2s2);

                if (g == 0) {
                    // Avoid returning NaN in case of overflow.
                    return 0;
                } else {
                    return -2 * diff * i2s2 * g;
                }
            }
        };
    }

    /**
     * Parametric function where the input array contains the parameters of
     * the Gaussian, ordered as follows:
     * <ul>
     *  <li>Norm</li>
     *  <li>Mean</li>
     *  <li>Standard deviation</li>
     * </ul>
     */
    public static class Parametric implements ParametricUnivariateFunction {
        /**
         * Computes the value of the Gaussian at {@code x}.
         *
         * @param x Value for which the function must be computed.
         * @param param Values of norm, mean and standard deviation.
         * @return the value of the function.
         * @throws NullArgumentException if {@code param} is {@code null}.
         * @throws DimensionMismatchException if the size of {@code param} is
         * not 3.
         * @throws NotStrictlyPositiveException if {@code param[2]} is negative.
         */
        public double value(double x, double ... param) {
            validateParameters(param);

            final double diff = x - param[1];
            final double i2s2 = 1 / (2 * param[2] * param[2]);
            return Gaussian.value(diff, param[0], i2s2);
        }

        /**
         * Computes the value of the gradient at {@code x}.
         * The components of the gradient vector are the partial
         * derivatives of the function with respect to each of the
         * <em>parameters</em> (norm, mean and standard deviation).
         *
         * @param x Value at which the gradient must be computed.
         * @param param Values of norm, mean and standard deviation.
         * @return the gradient vector at {@code x}.
         * @throws NullArgumentException if {@code param} is {@code null}.
         * @throws DimensionMismatchException if the size of {@code param} is
         * not 3.
         * @throws NotStrictlyPositiveException if {@code param[2]} is negative.
         */
        public double[] gradient(double x, double ... param) {
            validateParameters(param);

            final double norm = param[0];
            final double diff = x - param[1];
            final double sigma = param[2];
            final double i2s2 = 1 / (2 * sigma * sigma);

            final double n = Gaussian.value(diff, 1, i2s2);
            final double m = norm * n * 2 * i2s2 * diff;
            final double s = m * diff / sigma;

            return new double[] { n, m, s };
        }

        /**
         * Validates parameters to ensure they are appropriate for the evaluation of
         * the {@link #value(double,double[])} and {@link #gradient(double,double[])}
         * methods.
         *
         * @param param Values of norm, mean and standard deviation.
         * @throws NullArgumentException if {@code param} is {@code null}.
         * @throws DimensionMismatchException if the size of {@code param} is
         * not 3.
         * @throws NotStrictlyPositiveException if {@code param[2]} is negative.
         */
        private void validateParameters(double[] param) {
            if (param == null) {
                throw new NullArgumentException();
            }
            if (param.length != 3) {
                throw new DimensionMismatchException(param.length, 3);
            }
            if (param[2] <= 0) {
                throw new NotStrictlyPositiveException(param[2]);
            }
        }
    }

    /**
     * @param xMinusMean {@code x - mean}.
     * @param norm Normalization factor.
     * @param i2s2 Inverse of twice the square of the standard deviation.
     * @return the value of the Gaussian at {@code x}.
     */
    private static double value(double xMinusMean,
                                double norm,
                                double i2s2) {
        return norm * FastMath.exp(-xMinusMean * xMinusMean * i2s2);
    }
}
