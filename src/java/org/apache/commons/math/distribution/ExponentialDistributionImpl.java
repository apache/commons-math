/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
 * The default implementation of {@link ExponentialDistribution}
 * 
 * @version $Revision: 1.16 $ $Date: 2004/06/02 00:15:16 $
 */
public class ExponentialDistributionImpl
    implements ExponentialDistribution, Serializable  {

    /** Serializable version identifier */
    static final long serialVersionUID = 2401296428283614780L;
    
    /** The mean of this distribution. */
    private double mean;
    
    /**
     * Create a exponential distribution with the given mean.
     * @param mean mean of this distribution.
     */
    public ExponentialDistributionImpl(double mean) {
        super();
        setMean(mean);
    }

    /**
     * Modify the mean.
     * @param mean the new mean.
     * @throws IllegalArgumentException if <code>mean</code> is not positive.
     */
    public void setMean(double mean) {
        if (mean <= 0.0) {
            throw new IllegalArgumentException("mean must be positive.");
        }
        this.mean = mean;
    }

    /**
     * Access the mean.
     * @return the mean.
     */
    public double getMean() {
        return mean;
    }

    /**
     * For this disbution, X, this method returns P(X &lt; x).
     * 
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/ExponentialDistribution.html">
     * Exponential Distribution</a>, equation (1).</li>
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
            ret = 1.0 - Math.exp(-x / getMean());
        }
        return ret;
    }
    
    /**
     * For this distribution, X, this method returns the critical point x, such
     * that P(X &lt; x) = <code>p</code>.
     *
     * @param p the desired probability
     * @return x, such that P(X &lt; x) = <code>p</code>
     * @throws MathException if the inverse cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    public double inverseCumulativeProbability(double p) throws MathException{
        double ret;
        
        if (p < 0.0 || p > 1.0) {
            ret = Double.NaN;
        } else if (p == 1.0) {
            ret = Double.POSITIVE_INFINITY;
        } else {
            ret = -getMean() * Math.log(1.0 - p);
        }
        
        return ret;
    }
    
    /**
     * For this disbution, X, this method returns P(x0 &lt; X &lt; x1).
     * @param x0 the lower bound
     * @param x1 the upper bound
     * @return the cumulative probability. 
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x0, double x1) throws MathException{
        return cumulativeProbability(x1) - cumulativeProbability(x0);
    }
}
