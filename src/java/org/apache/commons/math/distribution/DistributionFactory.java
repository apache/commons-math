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

import org.apache.commons.discovery.tools.DiscoverClass;

/**
 * This factory provids the means to create common statistical distributions.
 * The following distributions are supported:
 * <ul>
 * <li>Binomial</li>
 * <li>Chi-Squared</li>
 * <li>Exponential</li>
 * <li>F</li>
 * <li>Gamma</li>
 * <li>Student's t</li>
 * </ul>
 * 
 * Common usage:<pre>
 * DistributionFactory factory = DistributionFactory.newInstance();
 * 
 * // create a Chi-Square distribution with 5 degrees of freedom.
 * ChiSquaredDistribution chi = factory.createChiSquareDistribution(5.0);
 * </pre>
 * 
 * @version $Revision: 1.13 $ $Date: 2003/10/13 08:10:57 $
 */
public abstract class DistributionFactory {
    /**
     * Default constructor.
     */
    protected DistributionFactory() {
        super();
    }
    
    /**
     * Create an instance of a <code>DistributionFactory</code>
     * @return a new factory. 
     */
    public static DistributionFactory newInstance() {
        DistributionFactory factory = null;
        try {
            DiscoverClass dc = new DiscoverClass();
            factory = (DistributionFactory) dc.newInstance(
                DistributionFactory.class,
                "org.apache.commons.math.stat.distribution.DistributionFactoryImpl");
        } catch(Exception ex) {
            // ignore as default implementation will be used.
        }
        return factory;
    }

    /**
     * Create a binomial distribution with the given number of trials and
     * probability of success.
     * @param numberOfTrials the number of trials.
     * @param probabilityOfSuccess the probability of success.
     * @return a new binomial distribution.
     */
    public abstract BinomialDistribution createBinomialDistribution(
        int numberOfTrials, double probabilityOfSuccess);
        
    /**
     * Create a new chi-square distribution with the given degrees of freedom.
     * @param degreesOfFreedom degrees of freedom.
     * @return a new chi-square distribution.  
     */
    public abstract ChiSquaredDistribution createChiSquareDistribution(
        double degreesOfFreedom);
    
    /**
     * Create a new exponential distribution with the given degrees of freedom.
     * @param mean mean.
     * @return a new exponential distribution.  
     */
    public abstract ExponentialDistribution createExponentialDistribution(
        double mean);
    
    /**
     * Create a new F-distribution with the given degrees of freedom.
     * @param numeratorDegreesOfFreedom numerator degrees of freedom.
     * @param denominatorDegreesOfFreedom denominator degrees of freedom.
     * @return a new F-distribution.  
     */
    public abstract FDistribution createFDistribution(
        double numeratorDegreesOfFreedom, double denominatorDegreesOfFreedom);
    
    /**
     * Create a new gamma distribution with the given alpha and beta values.
     * @param alpha the shape parameter.
     * @param beta the scale parameter.
     * @return a new gamma distribution.  
     */
    public abstract GammaDistribution createGammaDistribution(
        double alpha, double beta);

    /**
     * Create a new t distribution with the given degrees of freedom.
     * @param degreesOfFreedom degrees of freedom.
     * @return a new t distribution.  
     */
    public abstract TDistribution createTDistribution(double degreesOfFreedom);
    
    /**
     * Create a new hypergeometric distribution with the given the population
     * size, the number of successes in the population, and the sample size.
     * @param populationSize the population size.
     * @param numberOfSuccesses number of successes in the population.
     * @param sampleSize the sample size.
     * @return a new hypergeometric desitribution.
     */
    public abstract HypergeometricDistribution
        createHypergeometricDistribution(int populationSize,
            int numberOfSuccesses, int sampleSize);
}
