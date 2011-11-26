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
package org.apache.commons.math.distribution;

import org.apache.commons.math.exception.NumberIsTooLargeException;

/**
 * Base interface for probability distributions.
 *
 * @version $Id$
 */
public interface Distribution {
    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(X = x)}. In other
     * words, this method represents the probability mass function (PMF)
     * for the distribution.
     *
     * @param x the value at which the PMF is evaluated
     * @return the value of the probability mass function at {@code x}
     */
    double probability(double x);

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(X <= x)}. In other
     * words, this method represents the (cumulative) distribution function
     * (CDF) for this distribution.
     *
     * @param x the value at which the CDF is evaluated
     * @return the probability that a random variable with this
     * distribution takes a value less than or equal to {@code x}
     */
    double cumulativeProbability(double x);

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(x0 < X <= x1)}.
     *
     * @param x0 the exclusive lower bound
     * @param x1 the inclusive upper bound
     * @return the probability that a random variable with this distribution
     * takes a value between {@code x0} and {@code x1},
     * excluding the lower and including the upper endpoint
     * @throws NumberIsTooLargeException if {@code x0 > x1}
     */
    double cumulativeProbability(double x0, double x1) throws NumberIsTooLargeException;

    /**
     * Use this method to get the numerical value of the mean of this
     * distribution.
     *
     * @return the mean or Double.NaN if it's not defined
     */
    double getNumericalMean();

    /**
     * Use this method to get the numerical value of the variance of this
     * distribution.
     *
     * @return the variance (possibly Double.POSITIVE_INFINITY as
     * for certain cases in {@link TDistribution}) or
     * Double.NaN if it's not defined
     */
    double getNumericalVariance();

    /**
     * Use this method to get information about whether the lower bound
     * of the support is inclusive or not.
     *
     * @return whether the lower bound of the support is inclusive or not
     */
    boolean isSupportLowerBoundInclusive();

    /**
     * Use this method to get information about whether the upper bound
     * of the support is inclusive or not.
     *
     * @return whether the upper bound of the support is inclusive or not
     */
    boolean isSupportUpperBoundInclusive();

    /**
     * Use this method to get information about whether the support is connected,
     * i.e. whether all values between the lower and upper bound of the support
     * is included in the support.
     *
     * For {@link AbstractIntegerDistribution} the support is discrete, so
     * if this is true, then the support is
     * {lower bound, lower bound + 1, ..., upper bound}.
     *
     * For {@link AbstractContinuousDistribution} the support is continuous, so
     * if this is true, then the support is the interval
     * [lower bound, upper bound]
     * where the limits are inclusive or not according to
     * {@link #isSupportLowerBoundInclusive()} and {@link #isSupportUpperBoundInclusive()}
     * (in the example both are true). If both are false, then the support is the interval
     * (lower bound, upper bound)
     *
     * @return whether the support limits given by subclassed methods are connected or not
     */
    boolean isSupportConnected();
}
