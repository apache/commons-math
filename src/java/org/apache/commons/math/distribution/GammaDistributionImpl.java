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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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

import org.apache.commons.math.special.Gamma;

/**
 * The Gamma Distribution defined by
 * http://mathworld.wolfram.com/GammaDistribution.html.
 * <br/>
 * Along with the Gamma Distribution, this class can be used as a Chi-Squared
 * Distribution defined by
 * http://mathworld.wolfram.com/Chi-SquaredDistribution.html
 * 
 * @author Brent Worden
 */
public class GammaDistributionImpl extends AbstractContinuousDistribution
    implements GammaDistribution {

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
     * For this disbution, X, this method returns P(X &lt; x).  This
     * implementation is based on the formulas found in Abramowitz and Stegun,
     * "Handbook of Mathematical Functions."
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution. 
     */
    public double cummulativeProbability(double x) {
        double ret;
    
        if (x <= 0.0) {
            ret = 0.0;
        } else {
            ret = Gamma.regularizedGammaP(getAlpha(), x / getBeta());
        }
    
        return ret;
    }
    
    /**
     * Modify the shape parameter, alpha.
     * @param alpha the new shape parameter.
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
	 * @return
	 */
	public double getDomainLowerBound() {
		return Double.MIN_VALUE;
	}

	/**
	 * @return
	 */
	public double getDomainUpperBound() {
		return Double.MAX_VALUE;
	}

	/**
	 * @return
	 */
	public double getMean() {
		return getAlpha() * getBeta();
	}

}
