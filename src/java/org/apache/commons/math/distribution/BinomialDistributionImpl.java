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

import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.MathUtils;

/**
 * The default implementation of {@link BinomialDistribution}.
 * 
 * @version $Revision: 1.2 $ $Date: 2003/10/13 08:10:57 $
 */
public class BinomialDistributionImpl extends AbstractDiscreteDistribution
    implements BinomialDistribution {

    /** The number of trials. */
    private int numberOfTrials;
    
    /** The probability of success. */
    private double probabilityOfSuccess;
    
    /**
     * Create a binomial distribution with the given number of trials and
     * probability of success.
     * @param trials the number of trials.
     * @param p the probability of success.
     */
    public BinomialDistributionImpl(int trials, double p) {
        super();
        setNumberOfTrials(trials);
        setProbabilityOfSuccess(p);
    }
    
    /**
     * Access the number of trials for this distribution.
     * @return the number of trials.
     */
    public int getNumberOfTrials() {
        return numberOfTrials;
    }

    /**
     * Access the probability of success for this distribution.
     * @return the probability of success.
     */
    public double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
    }

    /**
     * Change the number of trials for this distribution.
     * @param trials the new number of trials.
     */
    public void setNumberOfTrials(int trials) {
        if (trials < 0) {
            throw new IllegalArgumentException(
                "number of trials must be non-negative.");
        }
        numberOfTrials = trials;
    }

    /**
     * Change the probability of success for this distribution.
     * @param p the new probability of success.
     */
    public void setProbabilityOfSuccess(double p) {
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException(
                "probability of success must be between 0.0 and 1.0, inclusive.");
        }
        probabilityOfSuccess = p;
    }
    
    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a PDF root.
     * 
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code> 
     */
    protected int getDomainLowerBound(double p) {
        return -1;
    }

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a PDF root.
     * 
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code> 
     */
    protected int getDomainUpperBound(double p) {
        return getNumberOfTrials();
    }

    /**
     * For this disbution, X, this method returns P(X &le; x).
     * @param x the value at which the PDF is evaluated.
     * @return PDF for this distribution. 
     */
    public double cummulativeProbability(int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else if (x >= getNumberOfTrials()) {
            ret = 1.0;
        } else {
            ret = 1.0 - Beta.regularizedBeta(getProbabilityOfSuccess(),
                x + 1.0, getNumberOfTrials() - x);
        }
        return ret;
    }

    /**
     * For this disbution, X, this method returns P(X = x).
     * @param x the value at which the PMF is evaluated.
     * @return PMF for this distribution. 
     */
    public double probability(int x) {
        double ret;
        if (x < 0 || x > getNumberOfTrials()) {
            ret = 0.0;
        } else {
            ret = MathUtils.binomialCoefficientDouble(getNumberOfTrials(), x) *
                Math.pow(getProbabilityOfSuccess(), x) *
                Math.pow(1.0 - getProbabilityOfSuccess(),
                    getNumberOfTrials() - x);
        }
        return ret;
    }
}
