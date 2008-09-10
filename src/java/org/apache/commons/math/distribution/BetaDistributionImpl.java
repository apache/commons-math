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

import org.apache.commons.math.MathException;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.special.Beta;

/**
 * Implements the Beta distribution.
 * <p>
 * References:
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Beta_distribution">
 * Beta distribution</a></li>
 * </ul>
 * </p>
 */
public class BetaDistributionImpl
    extends AbstractContinuousDistribution implements BetaDistribution {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -1221965979403477668L;

    /** First shape parameter. */
    private double alpha;

    /** Second shape parameter. */
    private double beta;

    /** Normalizing factor used in density computations.
     * updated whenever alpha or beta are changed.
     */
    private double z;

    /**
     * Build a new instance.
     * @param alpha first shape parameter (must be positive)
     * @param beta second shape parameter (must be positive)
     */
    public BetaDistributionImpl(double alpha, double beta) {
        this.alpha = alpha;
        this.beta = beta;
        z = Double.NaN;
    }

    /**
     * Modify the shape parameter, alpha.
     *
     * @param alpha the new shape parameter.
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
        z = Double.NaN;
    }

    /**
     * Access the shape parameter, alpha
     *
     * @return alpha.
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Modify the shape parameter, beta.
     *
     * @param beta the new scale parameter.
     */
    public void setBeta(double beta) {
        this.beta = beta;
        z = Double.NaN;
    }

    /**
     * Access the shape parameter, beta
     *
     * @return beta.
     */
    public double getBeta() {
        return beta;
    }

    /**
     * Recompute the normalization factor.
     */
    private void recomputeZ() {
        if (Double.isNaN(z)) {
            z = Gamma.logGamma(alpha) + Gamma.logGamma(beta) - Gamma.logGamma(alpha + beta);
        }
    }

    /**
     * Return the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     */
    public double density(Double x) throws MathException {
        recomputeZ();
        if (x < 0 || x > 1) {
            return 0;
        } else if (x == 0) {
            if (alpha < 1) {
                throw new MathException("Cannot compute beta density at 0 when alpha = {0,number}", new Double[]{alpha});
            }
            return 0;
        } else if (x == 1) {
            if (beta < 1) {
                throw new MathException("Cannot compute beta density at 1 when beta = %.3g", new Double[]{beta});
            }
            return 0;
        } else {
            double logX = Math.log(x);
            double log1mX = Math.log1p(-x);
            return Math.exp((alpha - 1) * logX + (beta - 1) * log1mX - z);
        }
    }

    /**
     * For this distribution, X, this method returns x such that P(X &lt; x) = p.
     *
     * @param p the cumulative probability.
     * @return x.
     * @throws org.apache.commons.math.MathException
     *          if the inverse cumulative probability can not be
     *          computed due to convergence or other numerical errors.
     */
    public double inverseCumulativeProbability(double p) throws MathException {
        if (p == 0) {
            return 0;
        } else if (p == 1) {
            return 1;
        } else {
            return super.inverseCumulativeProbability(p);
        }
    }

    /**
     * Access the initial domain value, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return initial domain value
     */
    protected double getInitialDomain(double p) {
        return p;
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
    protected double getDomainLowerBound(double p) {
        return 0;
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
    protected double getDomainUpperBound(double p) {
        return 1;
    }

    /**
     * For a random variable X whose values are distributed according
     * to this distribution, this method returns P(X &le; x).  In other words,
     * this method represents the  (cumulative) distribution function, or
     * CDF, for this distribution.
     *
     * @param x the value at which the distribution function is evaluated.
     * @return the probability that a random variable with this
     *         distribution takes a value less than or equal to <code>x</code>
     * @throws org.apache.commons.math.MathException
     *          if the cumulative probability can not be
     *          computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException {
        if (x <= 0) {
            return 0;
        } else if (x >= 1) {
            return 1;
        } else {
            return Beta.regularizedBeta(x, alpha, beta);
        }
    }

    /**
     * For a random variable X whose values are distributed according
     * to this distribution, this method returns P(x0 &le; X &le; x1).
     *
     * @param x0 the (inclusive) lower bound
     * @param x1 the (inclusive) upper bound
     * @return the probability that a random variable with this distribution
     *         will take a value between <code>x0</code> and <code>x1</code>,
     *         including the endpoints
     * @throws org.apache.commons.math.MathException
     *                                  if the cumulative probability can not be
     *                                  computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if <code>x0 > x1</code>
     */
    public double cumulativeProbability(double x0, double x1) throws MathException {
        return cumulativeProbability(x1) - cumulativeProbability(x0);
    }
}
