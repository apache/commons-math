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

/**
 * Interface for discrete distributions of integer-valued random variables.
 *
 * @version $Id$
 */
public interface IntegerDistribution extends DiscreteDistribution {
    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(X = x)}. In other
     * words, this method represents the probability mass function for the
     * distribution.
     *
     * @param x Value at which the probability density function is evaluated.
     * @return the value of the probability density function at {@code x}.
     */
    double probability(int x);

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(X <= x)}.  In other
     * words, this method represents the probability distribution function, or
     * PDF for the distribution.
     *
     * @param x Value at which the PDF is evaluated.
     * @return PDF for this distribution.
     */
    double cumulativeProbability(int x);

    /**
     * For this distribution, {@code X}, this method returns
     * {@code P(x0 <= X <= x1)}.
     *
     * @param x0 the inclusive, lower bound
     * @param x1 the inclusive, upper bound
     * @return the cumulative probability.
     * @throws IllegalArgumentException if {@code x0 > x1}.
     */
    double cumulativeProbability(int x0, int x1);

    /**
     * For this distribution, {@code X}, this method returns the largest
     * {@code x} such that {@code P(X <= x) <= p}.
     * <br/>
     * Note that this definition implies:
     * <ul>
     *  <li> If there is a minimum value, {@code m}, with positive
     *   probability under (the density of) {@code X}, then {@code m - 1} is
     *   returned by {@code inverseCumulativeProbability(0).}  If there is
     *   no such value {@code m},  {@code Integer.MIN_VALUE} is returned.
     *  </li>
     *  <li> If there is a maximum value, {@code M}, such that
     *   {@code P(X <= M) = 1}, then {@code M} is returned by
     *   {@code inverseCumulativeProbability(1)}.
     *   If there is no such value, {@code M}, {@code Integer.MAX_VALUE} is
     *   returned.
     *  </li>
     * </ul>
     *
     * @param p Cumulative probability.
     * @return the largest {@code x} such that {@code P(X < x) <= p}.
     * @throws IllegalArgumentException if {@code p} is not between 0 and 1
     * (inclusive).
     */
    int inverseCumulativeProbability(double p);

    /**
     * Reseed the random generator used to generate samples.
     *
     * @param seed New seed.
     * @since 3.0
     */
    void reseedRandomGenerator(long seed);

    /**
     * Generate a random value sampled from this distribution.
     *
     * @return a random value.
     * @since 3.0
     */
    int sample();

    /**
     * Generate a random sample from the distribution.
     *
     * @param sampleSize number of random values to generate.
     * @return an array representing the random sample.
     * @throws org.apache.commons.math.exception.NotStrictlyPositiveException
     * if {@code sampleSize} is not positive.
     * @since 3.0
     */
    int[] sample(int sampleSize);
}
