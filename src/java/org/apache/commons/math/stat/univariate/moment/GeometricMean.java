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
package org.apache.commons.math.stat.univariate.moment;

import java.io.Serializable;

import org.apache.commons.math.stat.univariate.summary.SumOfLogs;

/**
 * Returns the <a href="http://www.xycoon.com/geometric_mean.htm">
 * geometric mean </a> of the available values.
 * <p>
 * Uses {@link SumOfLogs} superclass to compute sum of logs and returns
 * <code> exp( 1/n  (sum of logs) ).</code>  Therefore,
 * <ul>
 * <li>If any of values are < 0, the result is <code>NaN.</code></li>
 * <li>If all values are non-negative and less than <code>Double.POSITIVE_INFINITY</code>, 
 * but at least one value is 0, the result is <code>0.</code></li>
 * <li>If both <code>Double.POSITIVE_INFINITY</code> and 
 * <code>Double.NEGATIVE_INFINITY</code> are among the values, the result is
 * <code>NaN.</code></li>
 * </ul>
 * 
 * 
 * @version $Revision: 1.19 $ $Date: 2004/06/18 07:03:40 $
 */
public class GeometricMean extends SumOfLogs implements Serializable{

    /** Serializable version identifier */
    static final long serialVersionUID = -8178734905303459453L;  
      
    /**Number of values that have been added */
    protected long n = 0;

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        n++;
        super.increment(d);
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        if (n > 0) {
            return Math.exp(super.getResult() / (double) n);
        } else {
            return Double.NaN;
        }
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        super.clear();
        n = 0;
    }

    /**
     * Returns the geometric mean for this collection of values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the geometric mean or Double.NaN if the array is empty or
     * any of the values are &lt;= 0.
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public double evaluate(
        final double[] values,
        final int begin,
        final int length) {
        return Math.exp(
            super.evaluate(values, begin, length) / (double) length);
    }

}