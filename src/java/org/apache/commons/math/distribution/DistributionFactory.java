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
 * <li>HyperGeometric</li>
 * <li>Normal</li>
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
 * @version $Revision: 1.21 $ $Date: 2004/07/10 15:59:14 $
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
                "org.apache.commons.math.distribution.DistributionFactoryImpl");
        } catch(Throwable t) {
            return new DistributionFactoryImpl();
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
 
	/**
	 * Create a new normal distribution with the given mean and standard
	 * deviation values.
	 * @param mean arithmetic mean.
	 * @param sd standard deviation.
	 * @return a new normal distribution.  
	 */           
    public abstract NormalDistribution 
    	createNormalDistribution(double mean, double sd);
    	
	/**
	 * Create a new normal distribution with the mean equal to zero and standard
	 * deviation equal to one.
	 * @return a new normal distribution.  
	 */               
	public abstract NormalDistribution createNormalDistribution();
}
