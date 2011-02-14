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

package org.apache.commons.math.analysis.function;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.util.FastMath;

/**
 * <a href="http://en.wikipedia.org/wiki/Gaussian_function">
 *  Gaussian</a> function.
 *
 * @version $Revision$ $Date$
 * @since 3.0
 */
public class Gaussian implements DifferentiableUnivariateRealFunction {
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
        final double diff = x - mean;
        return norm * FastMath.exp(-diff * diff * i2s2);
    }

    /** {@inheritDoc} */
    public UnivariateRealFunction derivative() {
        return new UnivariateRealFunction() {
            /** {@inheritDoc} */
            public double value(double x) {
                final double diff = x - mean;
                final double g = Gaussian.this.value(x);

                if (g == 0) {
                    // Avoid returning NaN in case of overflow.
                    return 0;
                } else {
                    return -2 * diff * i2s2 * g;
                }
            }
        };
    }
}
