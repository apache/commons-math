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

import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.NotPositiveException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.math.util.FastMath;

/**
 * The default implementation of {@link PascalDistribution}.
 * @version $Id$
 * @since 1.2
 */
public class PascalDistributionImpl extends AbstractIntegerDistribution
    implements PascalDistribution, Serializable {
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
     */
    public PascalDistributionImpl(int r, double p) {
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
     * {@inheritDoc}
     */
    public int getNumberOfSuccesses() {
        return numberOfSuccesses;
    }

    /**
     * {@inheritDoc}
     */
    public double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
    }

    /**
     * Access the domain value lower bound, based on {@code p}, used to
     * bracket a PDF root.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value lower bound, i.e. {@code P(X < 'lower bound') < p}.
     */
    @Override
    protected int getDomainLowerBound(double p) {
        return -1;
    }

    /**
     * Access the domain value upper bound, based on {@code p}, used to
     * bracket a PDF root.
     *
     * @param p Desired probability for the critical value
     * @return the domain value upper bound, i.e. {@code P(X < 'upper bound') > p}.
     */
    @Override
    protected int getDomainUpperBound(double p) {
        // use MAX - 1 because MAX causes loop
        return Integer.MAX_VALUE - 1;
    }

    /**
     * For this distribution, {@code X}, this method returns {@code P(X <= x)}.
     *
     * @param x Value at which the PDF is evaluated.
     * @return PDF for this distribution.
     * @throws MathException if the cumulative probability can not be computed
     * due to convergence or other numerical errors.
     */
    @Override
    public double cumulativeProbability(int x) throws MathException {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else {
            ret = Beta.regularizedBeta(probabilityOfSuccess,
                numberOfSuccesses, x + 1);
        }
        return ret;
    }

    /**
     * For this distribution, {@code X}, this method returns {@code P(X = x)}.
     *
     * @param x Value at which the PMF is evaluated.
     * @return PMF for this distribution.
     */
    public double probability(int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else {
            ret = MathUtils.binomialCoefficientDouble(x +
                  numberOfSuccesses - 1, numberOfSuccesses - 1) *
                  FastMath.pow(probabilityOfSuccess, numberOfSuccesses) *
                  FastMath.pow(1.0 - probabilityOfSuccess, x);
        }
        return ret;
    }

    /**
     * For this distribution, {@code X}, this method returns the largest
     * {@code x}, such that {@code P(X <= x) <= p}.
     * It will return -1 when p = 0 and {@code Integer.MAX_VALUE} when p = 1.
     *
     * @param p Desired probability.
     * @return the largest {@code x} such that {@code P(X <= x) <= p}.
     * @throws MathException if the inverse cumulative probability can not be
     * computed due to convergence or other numerical errors.
     * @throws OutOfRangeException if {@code p < 0} or {@code p > 1}.
     */
    @Override
    public int inverseCumulativeProbability(final double p)
        throws MathException {
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
     * The upper bound of the support is always positive infinity
     * no matter the parameters. Positive infinity is symbolised
     * by <code>Integer.MAX_VALUE</code> together with
     * {@link #isSupportUpperBoundInclusive()} being <code>false</code>
     *
     * @return upper bound of the support (always <code>Integer.MAX_VALUE</code> for positive infinity)
     */
    @Override
    public int getSupportUpperBound() {
        return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     *
     * For number of successes <code>r</code> and
     * probability of success <code>p</code>, the mean is
     * <code>( r * p ) / ( 1 - p )</code>
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalMean() {
        final double p = getProbabilityOfSuccess();
        final double r = getNumberOfSuccesses();
        return ( r * p ) / ( 1 - p );
    }

    /**
     * {@inheritDoc}
     *
     * For number of successes <code>r</code> and
     * probability of success <code>p</code>, the mean is
     * <code>( r * p ) / ( 1 - p )^2</code>
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalVariance() {
        final double p = getProbabilityOfSuccess();
        final double r = getNumberOfSuccesses();
        final double pInv = 1 - p;
        return ( r * p ) / (pInv * pInv);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }
}
