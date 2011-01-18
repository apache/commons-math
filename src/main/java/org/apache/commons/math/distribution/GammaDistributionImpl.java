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
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.util.FastMath;

/**
 * The default implementation of {@link GammaDistribution}.
 *
 * @version $Revision$ $Date$
 */
public class GammaDistributionImpl extends AbstractContinuousDistribution
    implements GammaDistribution, Serializable  {
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
     * Create a new gamma distribution with the given alpha and beta values.
     * @param alpha the shape parameter.
     * @param beta the scale parameter.
     */
    public GammaDistributionImpl(double alpha, double beta) {
        this(alpha, beta, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Create a new gamma distribution with the given alpha and beta values.
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
    public GammaDistributionImpl(double alpha, double beta, double inverseCumAccuracy) {
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
     * For this distribution, {@code X}, this method returns {@code P(X < x)}.
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
     *
     * @param x Value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws MathException if the cumulative probability can not be
     * computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException{
        double ret;

        if (x <= 0) {
            ret = 0;
        } else {
            ret = Gamma.regularizedGammaP(alpha, x / beta);
        }

        return ret;
    }

    /**
     * For this distribution, {@code X}, this method returns the critical
     * point {@code x}, such that {@code P(X < x) = p}.
     * It will return 0 when p = 0 and {@code Double.POSITIVE_INFINITY}
     * when p = 1.
     *
     * @param p Desired probability.
     * @return {@code x}, such that {@code P(X < x) = p}.
     * @throws MathException if the inverse cumulative probability cannot be
     * computed due to convergence or other numerical errors.
     * @throws org.apache.commons.math.exception.OutOfRangeException if
     * {@code p} is not a valid probability.
     */
    @Override
    public double inverseCumulativeProbability(final double p)
        throws MathException {
        if (p == 0) {
            return 0;
        }
        if (p == 1) {
            return Double.POSITIVE_INFINITY;
        }
        return super.inverseCumulativeProbability(p);
    }

    /**
     * {@inheritDoc}
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * {@inheritDoc}
     */
    public double getBeta() {
        return beta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(double x) {
        if (x < 0) return 0;
        return FastMath.pow(x / beta, alpha - 1) / beta *
            FastMath.exp(-x / beta) / FastMath.exp(Gamma.logGamma(alpha));
    }

    /**
     * Access the domain value lower bound, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value lower bound, i.e. {@code P(X < 'lower bound') < p}.
     */
    @Override
    protected double getDomainLowerBound(double p) {
        // TODO: try to improve on this estimate
        return Double.MIN_VALUE;
    }

    /**
     * Access the domain value upper bound, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value upper bound, i.e. {@code P(X < 'upper bound') > p}.
     */
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

    /**
     * Access the initial domain value, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the initial domain value.
     */
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

    /**
     * Return the absolute accuracy setting of the solver used to estimate
     * inverse cumulative probabilities.
     *
     * @return the solver absolute accuracy.
     * @since 2.1
     */
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
     * For shape parameter <code>alpha</code> and scale
     * parameter <code>beta</code>, the mean is
     * <code>alpha * beta</code>
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalMean() {
        return getAlpha() * getBeta();
    }

    /**
     * {@inheritDoc}
     *
     * For shape parameter <code>alpha</code> and scale
     * parameter <code>beta</code>, the variance is
     * <code>alpha * beta^2</code>
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalVariance() {
        final double b = getBeta();
        return getAlpha() * b * b;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }
}
