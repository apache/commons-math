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
import org.apache.commons.math.util.FastMath;

/**
 * Implementation of the binomial distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Binomial_distribution">Binomial distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/BinomialDistribution.html">Binomial Distribution (MathWorld)</a>
 * @version $Id$
 */
public class BinomialDistribution extends AbstractIntegerDistribution
        implements Serializable {
    /** Serializable version identifier. */
    private static final long serialVersionUID = 6751309484392813623L;
    /** The number of trials. */
    private final int numberOfTrials;
    /** The probability of success. */
    private final double probabilityOfSuccess;

    /**
     * Create a binomial distribution with the given number of trials and
     * probability of success.
     *
     * @param trials Number of trials.
     * @param p Probability of success.
     * @throws NotPositiveException if {@code trials < 0}.
     * @throws OutOfRangeException if {@code p < 0} or {@code p > 1}.
     */
    public BinomialDistribution(int trials, double p) {
        if (trials < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_TRIALS,
                                           trials);
        }
        if (p < 0 || p > 1) {
            throw new OutOfRangeException(p, 0, 1);
        }

        probabilityOfSuccess = p;
        numberOfTrials = trials;
    }

    /**
     * Access the number of trials for this distribution.
     *
     * @return the number of trials.
     */
    public int getNumberOfTrials() {
        return numberOfTrials;
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
        return numberOfTrials;
    }

    /** {@inheritDoc} */
    @Override
    public double cumulativeProbability(int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else if (x >= numberOfTrials) {
            ret = 1.0;
        } else {
            ret = 1.0 - Beta.regularizedBeta(getProbabilityOfSuccess(),
                    x + 1.0, numberOfTrials - x);
        }
        return ret;
    }

    /** {@inheritDoc} */
    public double probability(int x) {
        double ret;
        if (x < 0 || x > numberOfTrials) {
            ret = 0.0;
        } else {
            ret = FastMath.exp(SaddlePointExpansion.logBinomialProbability(x,
                    numberOfTrials, probabilityOfSuccess,
                    1.0 - probabilityOfSuccess));
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * This implementation return -1 when {@code p == 0} and
     * {@code Integer.MAX_VALUE} when {@code p == 1}.
     */
    @Override
    public int inverseCumulativeProbability(final double p) {
        // handle extreme values explicitly
        if (p == 0) {
            return -1;
        }
        if (p == 1) {
            return Integer.MAX_VALUE;
        }

        // use default bisection impl
        return super.inverseCumulativeProbability(p);
    }

    /**
     * {@inheritDoc}
     *
     * The lower bound of the support is always 0 no matter the number of trials
     * and probability parameter.
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
     * The upper bound of the support is the number of trials.
     *
     * @return upper bound of the support (equal to number of trials)
     */
    @Override
    public int getSupportUpperBound() {
        return getNumberOfTrials();
    }

    /**
     * {@inheritDoc}
     *
     * For {@code n} trials and probability parameter {@code p}, the mean is
     * {@code n * p}.
     */
    @Override
    protected double calculateNumericalMean() {
        return getNumberOfTrials() * getProbabilityOfSuccess();
    }

    /**
     * {@inheritDoc}
     *
     * For {@code n} trials and probability parameter {@code p}, the variance is
     * {@code n * p * (1 - p)}.
     */
    @Override
    protected double calculateNumericalVariance() {
        final double p = getProbabilityOfSuccess();
        return getNumberOfTrials() * p * (1 - p);
    }
}
