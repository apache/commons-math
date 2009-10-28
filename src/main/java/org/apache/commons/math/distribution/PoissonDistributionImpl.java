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
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.util.MathUtils;

/**
 * Implementation for the {@link PoissonDistribution}.
 *
 * @version $Revision$ $Date$
 */
public class PoissonDistributionImpl extends AbstractIntegerDistribution
        implements PoissonDistribution, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -3349935121172596109L;

    /** Distribution used to compute normal approximation. */
    private NormalDistribution normal;

    /**
     * Holds the Poisson mean for the distribution.
     */
    private double mean;

    /**
     * Create a new Poisson distribution with the given the mean. The mean value
     * must be positive; otherwise an <code>IllegalArgument</code> is thrown.
     *
     * @param p the Poisson mean
     * @throws IllegalArgumentException if p &le; 0
     */
    public PoissonDistributionImpl(double p) {
        this(p, new NormalDistributionImpl());
    }

    /**
     * Create a new Poisson distribution with the given the mean. The mean value
     * must be positive; otherwise an <code>IllegalArgument</code> is thrown.
     *
     * @param p the Poisson mean
     * @param z a normal distribution used to compute normal approximations.
     * @throws IllegalArgumentException if p &le; 0
     * @since 1.2
     */
    public PoissonDistributionImpl(double p, NormalDistribution z) {
        super();
        setNormal(z);
        setMean(p);
    }

    /**
     * Get the Poisson mean for the distribution.
     *
     * @return the Poisson mean for the distribution.
     */
    public double getMean() {
        return this.mean;
    }

    /**
     * Set the Poisson mean for the distribution. The mean value must be
     * positive; otherwise an <code>IllegalArgument</code> is thrown.
     *
     * @param p the Poisson mean value
     * @throws IllegalArgumentException if p &le; 0
     */
    public void setMean(double p) {
        if (p <= 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "the Poisson mean must be positive ({0})", p);
        }
        this.mean = p;
        normal.setMean(p);
        normal.setStandardDeviation(Math.sqrt(p));
    }

    /**
     * The probability mass function P(X = x) for a Poisson distribution.
     *
     * @param x the value at which the probability density function is
     *            evaluated.
     * @return the value of the probability mass function at x
     */
    public double probability(int x) {
        double ret;
        if (x < 0 || x == Integer.MAX_VALUE) {
            ret = 0.0;
        } else if (x == 0) {
            ret = Math.exp(-mean);
        } else {
            ret = Math.exp(-SaddlePointExpansion.getStirlingError(x) -
                  SaddlePointExpansion.getDeviancePart(x, mean)) /
                  Math.sqrt(MathUtils.TWO_PI * x);
        }
        return ret;
    }

    /**
     * The probability distribution function P(X <= x) for a Poisson
     * distribution.
     *
     * @param x the value at which the PDF is evaluated.
     * @return Poisson distribution function evaluated at x
     * @throws MathException if the cumulative probability can not be computed
     *             due to convergence or other numerical errors.
     */
    @Override
    public double cumulativeProbability(int x) throws MathException {
        if (x < 0) {
            return 0;
        }
        if (x == Integer.MAX_VALUE) {
            return 1;
        }
        return Gamma.regularizedGammaQ((double) x + 1, mean, 1E-12,
                Integer.MAX_VALUE);
    }

    /**
     * Calculates the Poisson distribution function using a normal
     * approximation. The <code>N(mean, sqrt(mean))</code> distribution is used
     * to approximate the Poisson distribution.
     * <p>
     * The computation uses "half-correction" -- evaluating the normal
     * distribution function at <code>x + 0.5</code>
     * </p>
     *
     * @param x the upper bound, inclusive
     * @return the distribution function value calculated using a normal
     *         approximation
     * @throws MathException if an error occurs computing the normal
     *             approximation
     */
    public double normalApproximateProbability(int x) throws MathException {
        // calculate the probability using half-correction
        return normal.cumulativeProbability(x + 0.5);
    }

    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a CDF root. This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain lower bound
     */
    @Override
    protected int getDomainLowerBound(double p) {
        return 0;
    }

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a CDF root. This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain upper bound
     */
    @Override
    protected int getDomainUpperBound(double p) {
        return Integer.MAX_VALUE;
    }

    /**
     * Modify the normal distribution used to compute normal approximations. The
     * caller is responsible for insuring the normal distribution has the proper
     * parameter settings.
     *
     * @param value the new distribution
     * @since 1.2
     */
    public void setNormal(NormalDistribution value) {
        normal = value;
    }

}
