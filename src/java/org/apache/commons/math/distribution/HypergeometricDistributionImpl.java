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

import org.apache.commons.math.util.MathUtils;

/**
 * The default implementation of {@link HypergeometricDistribution}.
 * 
 * @version $Revision: 1.2 $ $Date: 2003/10/13 08:10:57 $
 */
public class HypergeometricDistributionImpl extends AbstractDiscreteDistribution
    implements HypergeometricDistribution
{

    /** The number of successes in the population. */
    private int numberOfSuccesses;
    
    /** The population size. */
    private int populationSize;
    
    /** The sample size. */
    private int sampleSize;
    
    /**
     * Construct a new hypergeometric distribution with the given the population
     * size, the number of successes in the population, and the sample size.
     * @param populationSize the population size.
     * @param numberOfSuccesses number of successes in the population.
     * @param sampleSize the sample size.
     */
    public HypergeometricDistributionImpl(int populationSize,
        int numberOfSuccesses, int sampleSize)
    {
        super();
        setPopulationSize(populationSize);
        setSampleSize(sampleSize);
        setNumberOfSuccesses(numberOfSuccesses);
    }

    /**
     * For this disbution, X, this method returns P(X &le; x).
     * @param x the value at which the PDF is evaluated.
     * @return PDF for this distribution. 
     */
    public double cummulativeProbability(int x) {
        double ret;
        
        int n = getPopulationSize();
        int m = getNumberOfSuccesses();
        int k = getSampleSize();

        int[] domain = getDomain(n, m, k);
        if (x < domain[0]) {
            ret = 0.0;
        } else if(x >= domain[1]) {
            ret = 1.0;
        } else {
            ret = 0.0;
            for (int i = domain[0]; i <= x; ++i){
                ret += probability(n, m, k, i);
            }
        }
        
        return ret;
    }

    /**
     * Return the domain for the given hypergeometric distribution parameters.
     * @param n the population size.
     * @param m number of successes in the population.
     * @param k the sample size.
     * @return a two element array containing the lower and upper bounds of the
     *         hypergeometric distribution.  
     */
    private int[] getDomain(int n, int m, int k){
        return new int[]{
            getLowerDomain(n, m, k),
            getUpperDomain(m, k)
        };
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
        return getLowerDomain(getPopulationSize(), getNumberOfSuccesses(),
            getSampleSize());
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
        return getUpperDomain(getSampleSize(), getNumberOfSuccesses());
    }

    /**
     * Return the lowest domain value for the given hypergeometric distribution
     * parameters.
     * @param n the population size.
     * @param m number of successes in the population.
     * @param k the sample size.
     * @return the lowest domain value of the hypergeometric distribution.  
     */
    private int getLowerDomain(int n, int m, int k) {
        return Math.max(0, m - (n - k));
    }

    /**
     * Access the number of successes.
     * @return the number of successes.
     */
    public int getNumberOfSuccesses() {
        return numberOfSuccesses;
    }

    /**
     * Access the population size.
     * @return the population size.
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Access the sample size.
     * @return the sample size.
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Return the highest domain value for the given hypergeometric distribution
     * parameters.
     * @param m number of successes in the population.
     * @param k the sample size.
     * @return the highest domain value of the hypergeometric distribution.  
     */
    private int getUpperDomain(int m, int k){
        return Math.min(k, m);
    }

    /**
     * For this disbution, X, this method returns P(X = x).
     * @param x the value at which the PMF is evaluated.
     * @return PMF for this distribution. 
     */
    public double probability(int x) {
        double ret;
        
        int n = getPopulationSize();
        int m = getNumberOfSuccesses();
        int k = getSampleSize();

        int[] domain = getDomain(n, m, k);
        if(x < domain[0] || x > domain[1]){
            ret = 0.0;
        } else {
            ret = probability(n, m, k, x);
        }
        
        return ret;
    }

    /**
     * For the disbution, X, defined by the given hypergeometric distribution
     * parameters, this method returns P(X = x).
     * @param n the population size.
     * @param m number of successes in the population.
     * @param k the sample size.
     * @param x the value at which the PMF is evaluated.
     * @return PMF for the distribution. 
     */
    private double probability(int n, int m, int k, int x) {
        return Math.exp(MathUtils.binomialCoefficientLog(m, x) +
            MathUtils.binomialCoefficientLog(n - m, k - x) -
            MathUtils.binomialCoefficientLog(n, k));
    }
    
    /**
     * Modify the number of successes.
     * @param num the new number of successes.
     */
    public void setNumberOfSuccesses(int num) {
        if(num < 0){
            throw new IllegalArgumentException(
                "number of successes must be non-negative.");
        }
        numberOfSuccesses = num;
    }

    /**
     * Modify the population size.
     * @param size the new population size.
     */
    public void setPopulationSize(int size) {
        if(size <= 0){
            throw new IllegalArgumentException(
                "population size must be positive.");
        }
        populationSize = size;
    }

    /**
     * Modify the sample size.
     * @param size the new sample size.
     */
    public void setSampleSize(int size) {
        if(size < 0){
            throw new IllegalArgumentException(
                "sample size must be non-negative.");
        }
        sampleSize = size;
    }
}
