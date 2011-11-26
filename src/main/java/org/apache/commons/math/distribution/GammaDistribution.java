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
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.util.FastMath;

/**
 * Implementation of the Gamma distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Gamma_distribution">Gamma distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/GammaDistribution.html">Gamma distribution (MathWorld)</a>
 * @version $Id$
 */
public class GammaDistribution extends AbstractContinuousDistribution
    implements Serializable  {
    /**
     * Default inverse cumulative probability accuracy.
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;
    /** Serializable version identifier. */
    private static final long serialVersionUID = -3239549463135430361L;
    /** The shape parameter. */
    private final double alpha;
    /** The scale parameter. */
    private final double beta;
    /** Inverse cumulative probability accuracy. */
    private final double solverAbsoluteAccuracy;

    /**
     * Create a new gamma distribution with the given {@code alpha} and
     * {@code beta} values.
     * @param alpha the shape parameter.
     * @param beta the scale parameter.
     */
    public GammaDistribution(double alpha, double beta) {
        this(alpha, beta, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Create a new gamma distribution with the given {@code alpha} and
     * {@code beta} values.
     *
     * @param alpha Shape parameter.
     * @param beta Scale parameter.
     * @param inverseCumAccuracy Maximum absolute error in inverse
     * cumulative probability estimates (defaults to
     * {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY}).
     * @throws NotStrictlyPositiveException if {@code alpha <= 0} or
     * {@code beta <= 0}.
     * @since 2.1
     */
    public GammaDistribution(double alpha, double beta, double inverseCumAccuracy)
        throws NotStrictlyPositiveException {
        if (alpha <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.ALPHA, alpha);
        }
        if (beta <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.BETA, beta);
        }

        this.alpha = alpha;
        this.beta = beta;
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * {@inheritDoc}
     *
     * The implementation of this method is based on:
     * <ul>
     *  <li>
     *   <a href="http://mathworld.wolfram.com/Chi-SquaredDistribution.html">
     *    Chi-Squared Distribution</a>, equation (9).
     *  </li>
     *  <li>Casella, G., & Berger, R. (1990). <i>Statistical Inference</i>.
     *    Belmont, CA: Duxbury Press.
     *  </li>
     * </ul>
     */
    public double cumulativeProbability(double x) {
        double ret;

        if (x <= 0) {
            ret = 0;
        } else {
            ret = Gamma.regularizedGammaP(alpha, x / beta);
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * Returns {@code 0} when {@code p == 0} and
     * {@code Double.POSITIVE_INFINITY} when {@code p == 1}.
     */
    @Override
    public double inverseCumulativeProbability(final double p) {
        if (p == 0) {
            return 0;
        }
        if (p == 1) {
            return Double.POSITIVE_INFINITY;
        }
        return super.inverseCumulativeProbability(p);
    }

    /**
     * Access the {@code alpha} shape parameter.
     *
     * @return {@code alpha}.
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Access the {@code beta} scale parameter.
     *
     * @return {@code beta}.
     */
    public double getBeta() {
        return beta;
    }

    /** {@inheritDoc} */
    public double density(double x) {
        if (x < 0) {
            return 0;
        }
        return FastMath.pow(x / beta, alpha - 1) / beta *
               FastMath.exp(-x / beta) / FastMath.exp(Gamma.logGamma(alpha));
    }

    /** {@inheritDoc} */
    @Override
    protected double getDomainLowerBound(double p) {
        // TODO: try to improve on this estimate
        return Double.MIN_VALUE;
    }

    /** {@inheritDoc} */
    @Override
    protected double getDomainUpperBound(double p) {
        // TODO: try to improve on this estimate
        // NOTE: gamma is skewed to the left
        // NOTE: therefore, P(X < &mu;) > .5

        double ret;

        if (p < 0.5) {
            // use mean
            ret = alpha * beta;
        } else {
            // use max value
            ret = Double.MAX_VALUE;
        }

        return ret;
    }

    /** {@inheritDoc} */
    @Override
    protected double getInitialDomain(double p) {
        // TODO: try to improve on this estimate
        // Gamma is skewed to the left, therefore, P(X < &mu;) > .5

        double ret;

        if (p < 0.5) {
            // use 1/2 mean
            ret = alpha * beta * 0.5;
        } else {
            // use mean
            ret = alpha * beta;
        }

        return ret;
    }

    /** {@inheritDoc} */
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
    }

    /**
     * {@inheritDoc}
     *
     * The lower bound of the support is always 0 no matter the parameters.
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
     * no matter the parameters.
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
     * For shape parameter {@code alpha} and scale parameter {@code beta}, the
     * mean is {@code alpha * beta}.
     */
    @Override
    protected double calculateNumericalMean() {
        return getAlpha() * getBeta();
    }

    /**
     * {@inheritDoc}
     *
     * For shape parameter {@code alpha} and scale parameter {@code beta}, the
     * variance is {@code alpha * beta^2}.
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalVariance() {
        final double b = getBeta();
        return getAlpha() * b * b;
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
