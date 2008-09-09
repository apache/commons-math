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
import org.apache.commons.math.special.Gamma;

/**
 * The default implementation of {@link GammaDistribution}.
 *
 * @version $Revision$ $Date$
 */
public class GammaDistributionImpl extends AbstractContinuousDistribution
    implements GammaDistribution, Serializable  {

    /** Serializable version identifier */
    private static final long serialVersionUID = -3239549463135430361L;

    /** The shape parameter. */
    private double alpha;
    
    /** The scale parameter. */
    private double beta;
    
    /**
     * Create a new gamma distribution with the given alpha and beta values.
     * @param alpha the shape parameter.
     * @param beta the scale parameter.
     */
    public GammaDistributionImpl(double alpha, double beta) {
        super();
        setAlpha(alpha);
        setBeta(beta);
    }
    
    /**
     * For this distribution, X, this method returns P(X &lt; x).
     * 
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/Chi-SquaredDistribution.html">
     * Chi-Squared Distribution</a>, equation (9).</li>
     * <li>Casella, G., & Berger, R. (1990). <i>Statistical Inference</i>.
     * Belmont, CA: Duxbury Press.</li>
     * </ul>
     * 
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution. 
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException{
        double ret;
    
        if (x <= 0.0) {
            ret = 0.0;
        } else {
            ret = Gamma.regularizedGammaP(getAlpha(), x / getBeta());
        }
    
        return ret;
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
     * Modify the shape parameter, alpha.
     * @param alpha the new shape parameter.
     * @throws IllegalArgumentException if <code>alpha</code> is not positive.
     */
    public void setAlpha(double alpha) {
        if (alpha <= 0.0) {
            throw new IllegalArgumentException("alpha must be positive");
        }
        this.alpha = alpha;
    }
    
    /**
     * Access the shape parameter, alpha
     * @return alpha.
     */
    public double getAlpha() {
        return alpha;
    }
    
    /**
     * Modify the scale parameter, beta.
     * @param beta the new scale parameter.
     * @throws IllegalArgumentException if <code>beta</code> is not positive.
     */
    public void setBeta(double beta) {
        if (beta <= 0.0) {
            throw new IllegalArgumentException("beta must be positive");
        }
        this.beta = beta;
    }
    
    /**
     * Access the scale parameter, beta
     * @return beta.
     */
    public double getBeta() {
        return beta;
    }

    /**
     * Return the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     */
    public double density(Double x) {
        if (x < 0) return 0;
        return Math.pow(x / getBeta(), getAlpha() - 1) / getBeta() * Math.exp(-x / getBeta()) / Math.exp(Gamma.logGamma(getAlpha()));
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
        // TODO: try to improve on this estimate
        return Double.MIN_VALUE;
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
        // TODO: try to improve on this estimate
        // NOTE: gamma is skewed to the left
        // NOTE: therefore, P(X < &mu;) > .5

        double ret;

        if (p < .5) {
            // use mean
            ret = getAlpha() * getBeta();
        } else {
            // use max value
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
    protected double getInitialDomain(double p) {
        // TODO: try to improve on this estimate
        // Gamma is skewed to the left, therefore, P(X < &mu;) > .5

        double ret;

        if (p < .5) {
            // use 1/2 mean
            ret = getAlpha() * getBeta() * .5;
        } else {
            // use mean
            ret = getAlpha() * getBeta();
        }
        
        return ret;
    }
}
