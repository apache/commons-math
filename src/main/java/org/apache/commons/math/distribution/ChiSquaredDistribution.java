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


/**
 * Implementation of the chi-squared distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Chi-squared_distribution">Chi-squared distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/Chi-SquaredDistribution.html">Chi-squared Distribution (MathWorld)</a>
 * @version $Id: ChiSquaredDistribution.java 1206060 2011-11-25 05:16:56Z celestin $
 */
public class ChiSquaredDistribution
    extends AbstractContinuousDistribution
    implements Serializable {
    /**
     * Default inverse cumulative probability accuracy
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;
    /** Serializable version identifier */
    private static final long serialVersionUID = -8352658048349159782L;
    /** Internal Gamma distribution. */
    private final GammaDistribution gamma;
    /** Inverse cumulative probability accuracy */
    private final double solverAbsoluteAccuracy;

    /**
     * Create a Chi-Squared distribution with the given degrees of freedom.
     *
     * @param degreesOfFreedom Degrees of freedom.
     */
    public ChiSquaredDistribution(double degreesOfFreedom) {
        this(degreesOfFreedom, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Create a Chi-Squared distribution with the given degrees of freedom and
     * inverse cumulative probability accuracy.
     *
     * @param degreesOfFreedom Degrees of freedom.
     * @param inverseCumAccuracy the maximum absolute error in inverse
     * cumulative probability estimates (defaults to
     * {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY}).
     * @since 2.1
     */
    public ChiSquaredDistribution(double degreesOfFreedom,
                                      double inverseCumAccuracy) {
        gamma = new GammaDistribution(degreesOfFreedom / 2, 2);
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * Access the number of degrees of freedom.
     *
     * @return the degrees of freedom.
     */
    public double getDegreesOfFreedom() {
        return gamma.getAlpha() * 2.0;
    }

    /** {@inheritDoc} */
    public double density(double x) {
        return gamma.density(x);
    }

    /** {@inheritDoc} */
    public double cumulativeProbability(double x)  {
        return gamma.cumulativeProbability(x);
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
            return 0d;
        }
        if (p == 1) {
            return Double.POSITIVE_INFINITY;
        }
        return super.inverseCumulativeProbability(p);
    }

    /** {@inheritDoc} */
    @Override
    protected double getDomainLowerBound(double p) {
        return Double.MIN_VALUE * gamma.getBeta();
    }

    /** {@inheritDoc} */
    @Override
    protected double getDomainUpperBound(double p) {
        // NOTE: chi squared is skewed to the left
        // NOTE: therefore, P(X < &mu;) > .5

        double ret;

        if (p < .5) {
            // use mean
            ret = getDegreesOfFreedom();
        } else {
            // use max
            ret = Double.MAX_VALUE;
        }

        return ret;
    }

    /** {@inheritDoc} */
    @Override
    protected double getInitialDomain(double p) {
        // NOTE: chi squared is skewed to the left
        // NOTE: therefore, P(X < &mu;) > 0.5

        double ret;

        if (p < 0.5) {
            // use 1/2 mean
            ret = getDegreesOfFreedom() * 0.5;
        } else {
            // use mean
            ret = getDegreesOfFreedom();
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
     * The lower bound of the support is always 0 no matter the
     * degrees of freedom.
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
     * The upper bound of the support is always positive infinity no matter the
     * degrees of freedom.
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
     * For {@code k} degrees of freedom, the mean is {@code k}.
     */
    @Override
    protected double calculateNumericalMean() {
        return getDegreesOfFreedom();
    }

    /**
     * {@inheritDoc}
     *
     * For {@code k} degrees of freedom, the variance is {@code 2 * k}.
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalVariance() {
        return 2*getDegreesOfFreedom();
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
