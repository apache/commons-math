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
 * The Gamma Distribution.
 *
 * Instances of GammaDistribution objects should be created using
 * {@link DistributionFactory#createGammaDistribution(double,double)}.
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/GammaDistribution.html">
 * Gamma Distribution</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision$ $Date$
 */
public interface GammaDistribution extends ContinuousDistribution {
    /**
     * Modify the shape parameter, alpha.
     * @param alpha the new shape parameter.
     */
    void setAlpha(double alpha);
    
    /**
     * Access the shape parameter, alpha
     * @return alpha.
     */
    double getAlpha();
    
    /**
     * Modify the scale parameter, beta.
     * @param beta the new scale parameter.
     */
    void setBeta(double beta);
    
    /**
     * Access the scale parameter, beta
     * @return beta.
     */
    double getBeta();
}
