/*
 * Copyright 2006 The Apache Software Foundation.
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
 * The Pascal Distribution.
 *
 * Instances of PascalDistribution objects should be created using
 * {@link DistributionFactory#createPascalDistribution(int, double)}.
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/NegativeBinomialDistribution.html">
 * Negative Binomial Distribution</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision:$
 */
public interface PascalDistribution extends IntegerDistribution {
    /**
     * Access the number of successes for this distribution.
     * 
     * @return the number of successes
     */
    int getNumberOfSuccesses();
    
    /**
     * Access the probability of success for this distribution.
     * 
     * @return the probability of success
     */
    double getProbabilityOfSuccess();
    
    /**
     * Change the number of successes for this distribution.
     * 
     * @param successes the new number of successes
     */
    void setNumberOfSuccesses(int successes);
    
    /**
     * Change the probability of success for this distribution.
     * 
     * @param p the new probability of success
     */
    void setProbabilityOfSuccess(double p);
}