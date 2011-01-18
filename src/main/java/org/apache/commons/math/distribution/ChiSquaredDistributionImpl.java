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

/**
 * The default implementation of {@link ChiSquaredDistribution}
 *
 * @version $Revision$ $Date$
 */
public class ChiSquaredDistributionImpl
    extends AbstractContinuousDistribution
    implements ChiSquaredDistribution, Serializable {
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
    public ChiSquaredDistributionImpl(double degreesOfFreedom) {
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
    public ChiSquaredDistributionImpl(double degreesOfFreedom,
                                      double inverseCumAccuracy) {
        gamma = new GammaDistributionImpl(degreesOfFreedom / 2, 2);
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * {@inheritDoc}
     */
    public double getDegreesOfFreedom() {
        return gamma.getAlpha() * 2.0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(double x) {
        return gamma.density(x);
    }

    /**
     * For this distribution, {@code X}, this method returns {@code P(X < x)}.
     *
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws MathException if the cumulative probability cannot be
     * computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException {
        return gamma.cumulativeProbability(x);
    }

    /**
     * For this distribution, X, this method returns the critical point
     * {@code x}, such that {@code P(X < x) = p}.
     * It will return 0 when p = 0 and {@code Double.POSITIVE_INFINITY}
     * when p = 1.
     *
     * @param p Desired probability.
     * @return {@code x}, such that {@code P(X < x) = p}.
     * @throws MathException if the inverse cumulative probability can not be
     * computed due to convergence or other numerical errors.
     * @throws org.apache.commons.math.exception.OutOfRangeException if
     * {@code p} is not a valid probability.
     */
    @Override
    public double inverseCumulativeProbability(final double p)
        throws MathException {
        if (p == 0) {
            return 0d;
        }
        if (p == 1) {
            return Double.POSITIVE_INFINITY;
        }
        return super.inverseCumulativeProbability(p);
    }

    /**
     * Access the domain value lower bound, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e. {@code P(X < 'lower bound') < p}.
     */
    @Override
    protected double getDomainLowerBound(double p) {
        return Double.MIN_VALUE * gamma.getBeta();
    }

    /**
     * Access the domain value upper bound, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return domain value upper bound, i.e. {@code P(X < 'upper bound') > p}.
     */
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
     * For <code>k</code> degrees of freedom, the mean is
     * <code>k</code>
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalMean() {
        return getDegreesOfFreedom();
    }

    /**
     * {@inheritDoc}
     *
     * For <code>k</code> degrees of freedom, the variance is
     * <code>2 * k</code>
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalVariance() {
        return 2*getDegreesOfFreedom();
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
