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
 * Abstract Implementation for UnivariateStatistics.
 * Provides the ability to extend polymophically so that
 * indiviual statistics do not need to implement these methods.
 * @version $Revision: 1.15 $ $Date: 2004/04/12 05:22:11 $
 */
public abstract class AbstractUnivariateStatistic
    implements UnivariateStatistic {

    /**
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[])
     */
    public double evaluate(final double[] values) {
        return evaluate(values, 0, values.length);
    }

    /**
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public abstract double evaluate(
        final double[] values,
        final int begin,
        final int length);

    /**
     * This method is used by all evaluation methods to verify that the content
     * of the array and indices are correct.
     * <p>
     *  It is used by an individual statistic to determine if calculation
     *  should continue, or return <code>Double.NaN</code> </p>
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return true if the parameters are valid and designate a subarray of positive length
     * @throws IllegalArgumentException if the indices are invalid or the array is null
     */
    protected boolean test(
        final double[] values,
        final int begin,
        final int length) {

        if (begin < 0) {
            throw new IllegalArgumentException("start position cannot be negative");
        }
        
        if (length < 0) {
            throw new IllegalArgumentException("length cannot be negative");
        }
        
        if (values == null) {
            throw new IllegalArgumentException("input value array is null");
        }

        if (begin + length > values.length) {
            throw new IllegalArgumentException(
                "begin + length > values.length");
        }

        if (length == 0) {
            return false;
        }

        return true;

    }
}