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
package org.apache.commons.math.stat.univariate;

/**
 * Extends the definition of {@link UnivariateStatistic} with an {@link #increment}
 * method for adding values and updating internal state incrementally.  This interface
 * is designed to be used for calculating statistics that can be computed in one pass through
 * the data without storing the full array of sample values.
 * 
 * @version $Revision: 1.14 $ $Date: 2004/02/21 21:35:15 $
 */
public interface StorelessUnivariateStatistic extends UnivariateStatistic {

    /**
     * Updates the internal state of the statistic to reflect the addition of the new value.
     * @param d  the new value.
     */
    void increment(double d);

    /**
     * Returns the current value of the Statistic.
     * @return value of the statistic, <code>Double.NaN</code> if it
     * has been cleared or just instantiated.
     */
    double getResult();

    /**
     * Returns the number of values that have been added.
     * @return the number of values.
     */
    double getN();

    /**
     * Clears all the internal state of the Statistic
     */
    void clear();

}