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
    implements ChiSquaredDistribution, Serializable  {

    /** Serializable version identifier */
    private static final long serialVersionUID = -8352658048349159782L;

    /** Internal Gamma distribution. */
    private GammaDistribution gamma;

    /**
     * Create a Chi-Squared distribution with the given degrees of freedom.
     * @param df degrees of freedom.
     */
    public ChiSquaredDistributionImpl(double df) {
        this(df, new GammaDistributionImpl(df / 2.0, 2.0));
    }

    /**
     * Create a Chi-Squared distribution with the given degrees of freedom.
     * @param df degrees of freedom.
     * @param g the underlying gamma distribution used to compute probabilities.
     * @since 1.2
     */
    public ChiSquaredDistributionImpl(double df, GammaDistribution g) {
        super();
        setGamma(g);
        setDegreesOfFreedom(df);
    }

    /**
     * Modify the degrees of freedom.
     * @param degreesOfFreedom the new degrees of freedom.
     */
    public void setDegreesOfFreedom(double degreesOfFreedom) {
        getGamma().setAlpha(degreesOfFreedom / 2.0);
    }

    /**
     * Access the degrees of freedom.
     * @return the degrees of freedom.
     */
    public double getDegreesOfFreedom() {
        return getGamma().getAlpha() * 2.0;
    }

    /**
     * Return the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     */
    public double density(Double x) {
        return gamma.density(x);
    }

    /**
     * For this distribution, X, this method returns P(X &lt; x).
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException {
        return getGamma().cumulativeProbability(x);
    }

    /**
     * For this distribution, X, this method returns the critical point x, such
     * that P(X &lt; x) = <code>p</code>.
     * <p>
     * Returns 0 for p=0 and <code>Double.POSITIVE_INFINITY</code> for p=1.</p>
     *
     * @param p the desired probability
     * @return x, such that P(X &lt; x) = <code>p</code>
     * @throws MathException if the inverse cumulative probability can not be
     *         computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if <code>p</code> is not a valid
     *         probability.
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
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code>
     */
    @Override
    protected double getDomainLowerBound(double p) {
        return Double.MIN_VALUE * getGamma().getBeta();
    }

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code>
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
     * Access the initial domain value, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return initial domain value
     */
    @Override
    protected double getInitialDomain(double p) {
        // NOTE: chi squared is skewed to the left
        // NOTE: therefore, P(X < &mu;) > .5

        double ret;

        if (p < .5) {
            // use 1/2 mean
            ret = getDegreesOfFreedom() * .5;
        } else {
            // use mean
            ret = getDegreesOfFreedom();
        }

        return ret;
    }

    /**
     * Modify the underlying gamma distribution.  The caller is responsible for
     * insuring the gamma distribution has the proper parameter settings.
     * @param g the new distribution.
     * @since 1.2 made public
     */
    public void setGamma(GammaDistribution g) {
        this.gamma = g;

    }

    /**
     * Access the Gamma distribution.
     * @return the internal Gamma distribution.
     */
    private GammaDistribution getGamma() {
        return gamma;
    }
}
