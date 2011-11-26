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

import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Implementation of the exponential distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Exponential_distribution">Exponential distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/ExponentialDistribution.html">Exponential distribution (MathWorld)</a>
 * @version $Id$
 */
public class ExponentialDistribution extends AbstractContinuousDistribution
    implements Serializable {
    /**
     * Default inverse cumulative probability accuracy.
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;
    /** Serializable version identifier */
    private static final long serialVersionUID = 2401296428283614780L;
    /** The mean of this distribution. */
    private final double mean;
    /** Inverse cumulative probability accuracy. */
    private final double solverAbsoluteAccuracy;

    /**
     * Create a exponential distribution with the given mean.
     * @param mean mean of this distribution.
     */
    public ExponentialDistribution(double mean) {
        this(mean, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Create a exponential distribution with the given mean.
     *
     * @param mean Mean of this distribution.
     * @param inverseCumAccuracy Maximum absolute error in inverse
     * cumulative probability estimates (defaults to
     * {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY}).
     * @throws NotStrictlyPositiveException if {@code mean <= 0}.
     * @since 2.1
     */
    public ExponentialDistribution(double mean, double inverseCumAccuracy)
        throws NotStrictlyPositiveException{
        if (mean <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.MEAN, mean);
        }
        this.mean = mean;
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * Access the mean.
     *
     * @return the mean.
     */
    public double getMean() {
        return mean;
    }

    /** {@inheritDoc} */
    public double density(double x) {
        if (x < 0) {
            return 0;
        }
        return FastMath.exp(-x / mean) / mean;
    }

    /**
     * {@inheritDoc}
     *
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/ExponentialDistribution.html">
     * Exponential Distribution</a>, equation (1).</li>
     * </ul>
     */
    public double cumulativeProbability(double x)  {
        double ret;
        if (x <= 0.0) {
            ret = 0.0;
        } else {
            ret = 1.0 - FastMath.exp(-x / mean);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * Returns {@code 0} when {@code p= = 0} and
     * {@code Double.POSITIVE_INFINITY} when {@code p == 1}.
     */
    @Override
    public double inverseCumulativeProbability(double p) throws OutOfRangeException {
        double ret;

        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0.0, 1.0);
        } else if (p == 1.0) {
            ret = Double.POSITIVE_INFINITY;
        } else {
            ret = -mean * FastMath.log(1.0 - p);
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Algorithm Description</strong>: this implementation uses the
     * <a href="http://www.jesus.ox.ac.uk/~clifford/a5/chap1/node5.html">
     * Inversion Method</a> to generate exponentially distributed random values
     * from uniform deviates.</p>
     *
     * @return a random value.
     * @since 2.2
     */
    @Override
    public double sample() {
        return randomData.nextExponential(mean);
    }

    /** {@inheritDoc} */
    @Override
    protected double getDomainLowerBound(double p) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    protected double getDomainUpperBound(double p) {
        // NOTE: exponential is skewed to the left
        // NOTE: therefore, P(X < &mu;) > .5

        if (p < 0.5) {
            // use mean
            return mean;
        } else {
            // use max
            return Double.MAX_VALUE;
        }
    }

    /** {@inheritDoc} */
    @Override
    protected double getInitialDomain(double p) {
        // TODO: try to improve on this estimate
        // TODO: what should really happen here is not derive from
        // AbstractContinuousDistribution
        // TODO: because the inverse cumulative distribution is simple.
        // Exponential is skewed to the left, therefore, P(X < &mu;) > .5
        if (p < 0.5) {
            // use 1/2 mean
            return mean * 0.5;
        } else {
            // use mean
            return mean;
        }
    }

    /** {@inheritDoc} */
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
    }

    /**
     * {@inheritDoc}
     *
     * The lower bound of the support is always 0 no matter the mean parameter.
     *
     * @return lower bound of the support (always 0)
     */
    @Override
    public double getSupportLowerBound() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * The upper bound of the support is always positive infinity
     * no matter the mean parameter.
     *
     * @return upper bound of the support (always Double.POSITIVE_INFINITY)
     */
    @Override
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     *
     * For mean parameter {@code k}, the mean is {@code k}.
     */
    @Override
    protected double calculateNumericalMean() {
        return getMean();
    }

    /**
     * {@inheritDoc}
     *
     * For mean parameter {@code k}, the variance is {@code k^2}.
     */
    @Override
    protected double calculateNumericalVariance() {
        final double m = getMean();
        return m * m;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }
}
