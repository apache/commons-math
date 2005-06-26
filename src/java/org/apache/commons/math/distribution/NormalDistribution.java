/*
 * Copyright 2004 The Apache Software Foundation.
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
 * Normal (Gauss) Distribution.
 * Instances of NormalDistribution objects should be created using
 * {@link DistributionFactory#createNormalDistribution(double, double)}.<p>
 *
 * <p>
 * References:<p>
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/NormalDistribution.html">
 * Normal Distribution</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision$ $Date$
 */
public interface NormalDistribution extends ContinuousDistribution {
    /**
     * Access the mean.
     * @return mean for this distribution
     */
    double getMean();
    /**
     * Modify the mean.
     * @param mean for this distribution
     */
    void setMean(double mean);
    /**
     * Access the standard deviation.
     * @return standard deviation for this distribution
     */
    double getStandardDeviation();
    /**
     * Modify the standard deviation.
     * @param sd standard deviation for this distribution
     */
    void setStandardDeviation(double sd);
}
