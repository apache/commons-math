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
package org.apache.commons.math4.legacy.distribution;

import org.apache.commons.rng.UniformRandomProvider;

/**
 * Base interface for multivariate distributions on the reals.
 *
 * This is based largely on the {@code ContinuousDistribution} interface,
 * but cumulative distribution functions are not required because they
 * are often quite difficult to compute for multivariate distributions.
 *
 * @since 3.1
 */
public interface MultivariateRealDistribution {
    /**
     * Returns the probability density function (PDF) of this distribution
     * evaluated at the specified point {@code x}. In general, the PDF is the
     * derivative of the cumulative distribution function. If the derivative
     * does not exist at {@code x}, then an appropriate replacement should be
     * returned, e.g. {@code Double.POSITIVE_INFINITY}, {@code Double.NaN}, or
     * the limit inferior or limit superior of the difference quotient.
     *
     * @param x Point at which the PDF is evaluated.
     * @return the value of the probability density function at point {@code x}.
     */
    double density(double[] x);

    /**
     * Gets the number of random variables of the distribution.
     * It is the size of the array returned by the {@link Sampler#sample() sample}
     * method.
     *
     * @return the number of variables.
     */
    int getDimension();

    /**
     * Creates a sampler.
     *
     * @param rng Generator of uniformly distributed numbers.
     * @return a sampler that produces random numbers according this
     * distribution.
     *
     * @since 4.0
     */
    Sampler createSampler(UniformRandomProvider rng);

    /**
     * Sampling functionality.
     *
     * @since 4.0
     */
    interface Sampler {
        /**
         * Generates a random value vector sampled from this distribution.
         *
         * @return a random value vector.
         */
        double[] sample();
    }
}
