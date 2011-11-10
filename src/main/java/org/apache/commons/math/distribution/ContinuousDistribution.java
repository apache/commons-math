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

import org.apache.commons.math.exception.OutOfRangeException;

/**
 * Base interface for continuous distributions.
 *
 * @version $Id$
 */
public interface ContinuousDistribution extends Distribution {
    /**
     * Computes the quantile function of this distribution. For a random
     * variable {@code X} distributed according to this distribution, the
     * returned value is
     * <ul>
     * <li><code>inf{x in R | P(X<=x) >= p}</code> for {@code 0 < p <= 1},</li>
     * <li><code>inf{x in R | P(X<=x) > 0}</code> for {@code p = 0}.</li>
     * </ul>
     *
     * @param p the cumulative probability
     * @return the smallest {@code p}-quantile of this distribution
     * (largest 0-quantile for {@code p = 0})
     * @throws OutOfRangeException if {@code p < 0} or {@code p > 1}
     */
    double inverseCumulativeProbability(double p) throws OutOfRangeException;

    /**
     * Returns the probability density function (PDF) of this distribution
     * evaluated at the specified point.
     *
     * @param x the point at which the PDF should be evaluated
     * @return the PDF at point {@code x}
     */
    double density(double x);

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
    double sample();

    /**
     * Generate a random sample from the distribution.
     *
     * @param sampleSize number of random values to generate.
     * @return an array representing the random sample.
     * @throws org.apache.commons.math.exception.NotStrictlyPositiveException
     * if {@code sampleSize} is not positive.
     * @since 3.0
     */
    double[] sample(int sampleSize);
}
