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

import java.io.Serializable;

import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.NotPositiveException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.ArithmeticUtils;
import org.apache.commons.math.util.FastMath;

/**
 * <p>
 * Implementation of the Pascal distribution. The Pascal distribution is a
 * special case of the Negative Binomial distribution where the number of
 * successes parameter is an integer.
 * </p>
 * <p>
 * There are various ways to express the probability mass and distribution
 * functions for the Pascal distribution.  The convention employed by the
 * library is to express these functions in terms of the number of failures in
 * a Bernoulli experiment
 * (see <a href="http://en.wikipedia.org/wiki/Negative_binomial_distribution#Waiting_time_in_a_Bernoulli_process">Waiting Time in a Bernoulli Process</a>).
 * </p>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Negative_binomial_distribution">
 * Negative binomial distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/NegativeBinomialDistribution.html">
 * Negative binomial distribution (MathWorld)</a>
 * @version $Id$
 * @since 1.2 (changed to concrete class in 3.0)
 */
public class PascalDistribution extends AbstractIntegerDistribution
    implements Serializable {
    /** Serializable version identifier. */
    private static final long serialVersionUID = 6751309484392813623L;
    /** The number of successes. */
    private final int numberOfSuccesses;
    /** The probability of success. */
    private final double probabilityOfSuccess;

    /**
     * Create a Pascal distribution with the given number of trials and
     * probability of success.
     *
     * @param r Number of successes.
     * @param p Probability of success.
     * @throws NotPositiveException if the number of successes is not positive
     * @throws OutOfRangeException if the probability of success is not in the
     * range [0, 1]
     */
    public PascalDistribution(int r, double p)
        throws NotPositiveException, OutOfRangeException {
        if (r < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_SUCCESSES,
                                           r);
        }
        if (p < 0 || p > 1) {
            throw new OutOfRangeException(p, 0, 1);
        }

        numberOfSuccesses = r;
        probabilityOfSuccess = p;
    }

    /**
     * Access the number of successes for this distribution.
     *
     * @return the number of successes.
     */
    public int getNumberOfSuccesses() {
        return numberOfSuccesses;
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
    @Override
    protected int getDomainLowerBound(double p) {
        return -1;
    }

    /** {@inheritDoc} */
    @Override
    protected int getDomainUpperBound(double p) {
        // use MAX - 1 because MAX causes loop
        return Integer.MAX_VALUE - 1;
    }

    /** {@inheritDoc} */
    @Override
    public double cumulativeProbability(int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else {
            ret = Beta.regularizedBeta(probabilityOfSuccess,
                numberOfSuccesses, x + 1);
        }
        return ret;
    }

    /** {@inheritDoc} */
    public double probability(int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else {
            ret = ArithmeticUtils.binomialCoefficientDouble(x +
                  numberOfSuccesses - 1, numberOfSuccesses - 1) *
                  FastMath.pow(probabilityOfSuccess, numberOfSuccesses) *
                  FastMath.pow(1.0 - probabilityOfSuccess, x);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * Returns {@code -1} when {@code p == 0} and
     * {@code Integer.MAX_VALUE} when {@code p == 1}.
     */
    @Override
    public int inverseCumulativeProbability(final double p) {
        int ret;

        // handle extreme values explicitly
        if (p == 0) {
            ret = -1;
        } else if (p == 1) {
            ret = Integer.MAX_VALUE;
        } else {
            ret = super.inverseCumulativeProbability(p);
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * The lower bound of the support is always 0 no matter the parameters.
     *
     * @return lower bound of the support (always 0)
     */
    @Override
    public int getSupportLowerBound() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * The upper bound of the support is always positive infinity no matter the
     * parameters. Positive infinity is symbolised by {@code Integer.MAX_VALUE}
     * together with {@link #isSupportUpperBoundInclusive()} being
     * {@code false}.
     *
     * @return upper bound of the support (always {@code Integer.MAX_VALUE}
     * for positive infinity)
     */
    @Override
    public int getSupportUpperBound() {
        return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     *
     * For number of successes {@code r} and probability of success {@code p},
     * the mean is {@code (r * p) / (1 - p)}.
     */
    @Override
    protected double calculateNumericalMean() {
        final double p = getProbabilityOfSuccess();
        final double r = getNumberOfSuccesses();
        return (r * p) / (1 - p);
    }

    /**
     * {@inheritDoc}
     *
     * For number of successes {@code r} and probability of success {@code p},
     * the mean is {@code (r * p) / (1 - p)^2}.
     */
    @Override
    protected double calculateNumericalVariance() {
        final double p = getProbabilityOfSuccess();
        final double r = getNumberOfSuccesses();
        final double pInv = 1 - p;
        return (r * p) / (pInv * pInv);
    }

    /**
     * {@inheritDoc}
     *
     * Always returns {@code false}.
     *
     * @see PascalDistribution#getSupportUpperBound() getSupportUpperBound()
     */
    @Override
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }
}
