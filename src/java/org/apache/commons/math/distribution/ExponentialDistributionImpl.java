/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.stat.distribution;

/**
 * The default implementation of {@link ExponentialDistribution}
 * 
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:10:57 $
 */
public class ExponentialDistributionImpl
    implements ExponentialDistribution {

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
     */
    public double cummulativeProbability(double x) {
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
     */
    public double inverseCummulativeProbability(double p) {
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
     * @return the cummulative probability. 
     */
    public double cummulativeProbability(double x0, double x1) {
        return cummulativeProbability(x1) - cummulativeProbability(x0);
    }
}
