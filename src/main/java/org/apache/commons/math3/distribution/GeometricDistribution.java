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

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

/**
 * Implementation of the geometric distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Geometric_distribution">Geometric distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/GeometricDistribution.html">Geometric Distribution (MathWorld)</a>
 * @version $Id$
 * @since 3.3
 */
public class GeometricDistribution extends AbstractIntegerDistribution {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 20130507L;
    /** The probability of success. */
    private final double probabilityOfSuccess;

    /**
     * Create a geometric distribution with the given probability of success.
     *
     * @param p probability of success.
     * @throws OutOfRangeException if {@code p <= 0} or {@code p > 1}.
     */
    public GeometricDistribution(double p) {
        this(new Well19937c(), p);
    }

    /**
     * Creates a geometric distribution.
     *
     * @param rng Random number generator.
     * @param p Probability of success.
     * @throws OutOfRangeException if {@code p <= 0} or {@code p > 1}.
     */
    public GeometricDistribution(RandomGenerator rng, double p) {
        super(rng);

        if (p <= 0 || p > 1) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_LEFT, p, 0, 1);
        }

        probabilityOfSuccess = p;
    }

    /**
     * Access the probability of success for this distribution.
     *
     * @return the probability of success.
     */
    public double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
    }

    /** {@inheritDoc} */
    public double probability(int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else {
            final double p = probabilityOfSuccess;
            ret = FastMath.pow(1 - p, x) * p;
        }
        return ret;
    }

    /** {@inheritDoc} */
    public double cumulativeProbability(int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else {
            final double p = probabilityOfSuccess;
            ret = 1.0 - FastMath.pow(1 - p, x + 1);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * For probability parameter {@code p}, the mean is {@code (1 - p) / p}.
     */
    public double getNumericalMean() {
        final double p = probabilityOfSuccess;
        return (1 - p) / p;
    }

    /**
     * {@inheritDoc}
     *
     * For probability parameter {@code p}, the variance is
     * {@code (1 - p) / (p * p)}.
     */
    public double getNumericalVariance() {
        final double p = probabilityOfSuccess;
        return (1 - p) / (p * p);
    }

    /**
     * {@inheritDoc}
     *
     * The lower bound of the support is always 0.
     *
     * @return lower bound of the support (always 0)
     */
    public int getSupportLowerBound() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * The upper bound of the support is infinite (which we approximate as
     * {@code Integer.MAX_VALUE}).
     *
     * @return upper bound of the support (always Integer.MAX_VALUE)
     */
    public int getSupportUpperBound() {
        return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     *
     * The support of this distribution is connected.
     *
     * @return {@code true}
     */
    public boolean isSupportConnected() {
        return true;
    }
}
