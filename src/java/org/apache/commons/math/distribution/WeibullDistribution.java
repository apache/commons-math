/*
 * Copyright 2005 The Apache Software Foundation.
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
 * Weibull Distribution.  This interface defines the two parameter form of the
 * distribution as defined by
 * <a href="http://mathworld.wolfram.com/WeibullDistribution.html">
 * Weibull Distribution</a>, equations (1) and (2).
 *
 * Instances of WeibullDistribution objects should be created using
 * {@link DistributionFactory#createWeibullDistribution(double, double)}
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/WeibullDistribution.html">
 * Weibull Distribution</a></li>
 * </ul>
 * </p>
 *
 * @since 1.1
 * @version $Revision: 1.12 $ $Date: 2004-06-23 11:26:18 -0500 (Wed, 23 Jun 2004) $
 */
public interface WeibullDistribution extends ContinuousDistribution {

    /**
     * Access the shape parameter.
     * @return the shape parameter.
     */
    double getShape();
    
    /**
     * Access the scale parameter.
     * @return the scale parameter.
     */
    double getScale();
    
    /**
     * Modify the shape parameter.
     * @param alpha The new shape parameter value.
     */
    void setShape(double alpha);
    
    /**
     * Modify the scale parameter.
     * @param beta The new scale parameter value.
     */
    void setScale(double beta);
}
