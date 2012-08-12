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
package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;

/**
 * Base interface for multivariate distributions on the reals.
 *
 * This is based largely on the RealDistribution interface, but cumulative
 * distribution functions are not required because they are often quite
 * difficult to compute for multivariate distributions.
 *
 * @version $Id$
 * @since 3.1
 */
public interface MultivariateRealDistribution {
    /**
     * For a random variable {@code X} whose values are distributed according to
     * this distribution, this method returns {@code P(X = x)}. In other words,
     * this method represents the probability mass function (PMF) for the
     * distribution.
     *
     * @param x Point at which the PMF is evaluated.
     * @return the value of the probability mass function at point {@code x}.
     */
    double probability(double[] x);

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
     * Access the lower bound of the support.
     * This method must return the same value as {@code inverseCumulativeProbability(0)}.
     * In other words, this method must return
     * <p>
     * <code>inf {x in R | P(X <= x) > 0}</code>.
     * </p>
     *
     * @return the lower bound of the support (might be
     * {@code Double.NEGATIVE_INFINITY}).
     */
    double getSupportLowerBound();

    /**
     * Access the upper bound of the support.
     * This method must return the same value as {@code inverseCumulativeProbability(1)}.
     * In other words, this method must return
     * <p>
     * <code>inf {x in R | P(X <= x) = 1}</code>.
     * </p>
     *
     * @return the upper bound of the support (might be
     * {@code Double.POSITIVE_INFINITY}).
     */
    double getSupportUpperBound();

    /**
     * Gets information about whether the lower bound of the support is
     * inclusive or not.
     *
     * @return whether the lower bound of the support is inclusive or not.
     */
    boolean isSupportLowerBoundInclusive();

    /**
     * gets information about whether the upper bound of the support is
     * inclusive or not.
     *
     * @return whether the upper bound of the support is inclusive or not.
     */
    boolean isSupportUpperBoundInclusive();

    /**
     * Gets information about whether the support is connected (i.e. all
     * values between the lower and upper bound of the support are included
     * in the support).
     *
     * @return whether the support is connected or not.
     */
    boolean isSupportConnected();

    /**
     * Reseeds the random generator used to generate samples.
     *
     * @param seed Seed with which to initialize the random number generator.
     */
    void reseedRandomGenerator(long seed);

    /**
     * Generates a random value vector sampled from this distribution.
     *
     * @return a random value vector.
     */
    double[] sample();

    /**
     * Generates a list of a random value vectors from the distribution.
     *
     * @param sampleSize the number of random vectors to generate.
     * @return an array representing the random samples.
     * @throws org.apache.commons.math3.exception.NotStrictlyPositiveException
     * if {@code sampleSize} is not positive.
     */
    double[][] sample(int sampleSize) throws NotStrictlyPositiveException;
}
