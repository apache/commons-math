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
 * The Exponential Distribution.
 *
 * Instances of ExponentialDistribution objects should be created using
 * {@link DistributionFactory#createExponentialDistribution(double)}.
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/ExponentialDistribution.html">
 * Exponential Distribution</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision$ $Date$
 */
public interface ExponentialDistribution extends ContinuousDistribution {
    /**
     * Modify the mean.
     * @param mean the new mean.
     */
    void setMean(double mean);
    
    /**
     * Access the mean.
     * @return the mean.
     */
    double getMean();
}