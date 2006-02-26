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

/**
 * A concrete distribution factory.  This is the default factory used by
 * Commons-Math.
 *  
 * @version $Revision$ $Date$
 */
public class DistributionFactoryImpl extends DistributionFactory {

    /**
     * Default constructor.  Package scope to prevent unwanted instantiation. 
     */
    public DistributionFactoryImpl() {
        super();
    }
    
    /**
     * Create a new chi-square distribution with the given degrees of freedom.
     * 
     * @param degreesOfFreedom degrees of freedom
     * @return a new chi-square distribution  
     */
    public ChiSquaredDistribution createChiSquareDistribution(
        final double degreesOfFreedom) {
            
        return new ChiSquaredDistributionImpl(degreesOfFreedom);
    }
    
    /**
     * Create a new gamma distribution the given shape and scale parameters.
     * 
     * @param alpha the shape parameter
     * @param beta the scale parameter
     * @return a new gamma distribution  
     */
    public GammaDistribution createGammaDistribution(
        double alpha, double beta) {

        return new GammaDistributionImpl(alpha, beta);
    }

    /**
     * Create a new t distribution with the given degrees of freedom.
     * 
     * @param degreesOfFreedom degrees of freedom
     * @return a new t distribution.  
     */
    public TDistribution createTDistribution(double degreesOfFreedom) {
        return new TDistributionImpl(degreesOfFreedom);
    }

    /**
     * Create a new F-distribution with the given degrees of freedom.
     * 
     * @param numeratorDegreesOfFreedom numerator degrees of freedom
     * @param denominatorDegreesOfFreedom denominator degrees of freedom
     * @return a new F-distribution 
     */
    public FDistribution createFDistribution(
        double numeratorDegreesOfFreedom,
        double denominatorDegreesOfFreedom) {
        return new FDistributionImpl(numeratorDegreesOfFreedom,
            denominatorDegreesOfFreedom);
    }

    /**
     * Create a new exponential distribution with the given degrees of freedom.
     * 
     * @param mean mean
     * @return a new exponential distribution  
     */
    public ExponentialDistribution createExponentialDistribution(double mean) {
        return new ExponentialDistributionImpl(mean);
    }    

    /**
     * Create a binomial distribution with the given number of trials and
     * probability of success.
     * 
     * @param numberOfTrials the number of trials
     * @param probabilityOfSuccess the probability of success
     * @return a new binomial distribution
     */
    public BinomialDistribution createBinomialDistribution(
        int numberOfTrials, double probabilityOfSuccess) {
        return new BinomialDistributionImpl(numberOfTrials,
            probabilityOfSuccess);
    }

    /**
     * Create a new hypergeometric distribution with the given the population
     * size, the number of successes in the population, and the sample size.
     * 
     * @param populationSize the population size
     * @param numberOfSuccesses number of successes in the population
     * @param sampleSize the sample size
     * @return a new hypergeometric desitribution
     */
    public HypergeometricDistribution createHypergeometricDistribution(
        int populationSize, int numberOfSuccesses, int sampleSize) {
        return new HypergeometricDistributionImpl(populationSize,
            numberOfSuccesses, sampleSize);
    }

    /**
     * Create a new normal distribution with the given mean and standard
     * deviation.
     *  
     * @param mean the mean of the distribution
     * @param sd standard deviation
     * @return a new normal distribution 
     */   
    public NormalDistribution createNormalDistribution(double mean, double sd) {
        return new NormalDistributionImpl(mean, sd);
    }

    /**
     * Create a new normal distribution with the mean zero and standard
     * deviation one.
     * 
     * @return a new normal distribution  
     */ 
    public NormalDistribution createNormalDistribution() {
        return new NormalDistributionImpl();
    }
    
    /**
     * Create a new Poisson distribution with poisson parameter lambda.
     * <p>
     * lambda must be postive; otherwise an 
     * <code>IllegalArgumentException</code> is thrown.
     * 
     * @param lambda poisson parameter
     * @return a new Poisson distribution  
     * @throws IllegalArgumentException if lambda &le; 0
     */               
    public PoissonDistribution  createPoissonDistribution(double lambda) {
        return new PoissonDistributionImpl(lambda);
    }

    /**
     * Create a Pascal distribution with the given number of successes and
     * probability of success.
     * 
     * @param numberOfSuccesses the number of successes.
     * @param probabilityOfSuccess the probability of success
     * @return a new Pascal distribution
     */
    public PascalDistribution createPascalDistribution(int numberOfSuccesses, double probabilityOfSuccess) {
        return new PascalDistributionImpl(numberOfSuccesses, probabilityOfSuccess);
    }

}
