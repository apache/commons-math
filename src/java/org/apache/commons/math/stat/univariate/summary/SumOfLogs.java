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
package org.apache.commons.math.stat.univariate.summary;

import java.io.Serializable;

import org.apache.commons.math.stat.univariate.AbstractStorelessUnivariateStatistic;

/**
 * Returns the sum of the natural logs for this collection of values.  
 * <p>
 * Uses {@link java.lang.Math#log(double)} to compute the logs.  Therefore,
 * <ul>
 * <li>If any of values are < 0, the result is <code>NaN.</code></li>
 * <li>If all values are non-negative and less than <code>Double.POSITIVE_INFINITY</code>, 
 * but at least one value is 0, the result is <code>Double.NEGATIVE_INFINITY.</code></li>
 * <li>If both <code>Double.POSITIVE_INFINITY</code> and 
 * <code>Double.NEGATIVE_INFINITY</code> are among the values, the result is
 * <code>NaN.</code></li>
 * </ul>
 * 
 * @version $Revision: 1.18 $ $Date: 2004/06/18 06:32:07 $
 */
public class SumOfLogs extends AbstractStorelessUnivariateStatistic implements Serializable {

    /** Serializable version identifier */
    static final long serialVersionUID = -370076995648386763L;    

    /**Number of values that have been added */
    private int n = 0;
    
    /**
     * The currently running value
     */
    private double value = 0d;

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        value += Math.log(d);
        n++;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        if (n > 0) {
            return value;
        } else {
            return Double.NaN;
        }
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getN()
     */
    public double getN() {
        return n;
    }
    
    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        value = 0d;
        n = 0;
    }

    /**
     * Returns the sum of the natural logs for this collection of values.
     * <p>
     * See {@link SumOfLogs}.
     * 
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the sumLog value or Double.NaN if the array is empty
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public double evaluate(final double[] values, final int begin, final int length) {
        double sumLog = Double.NaN;
        if (test(values, begin, length)) {
            sumLog = 0.0;
            for (int i = begin; i < begin + length; i++) {
                sumLog += Math.log(values[i]);
            }
        }
        return sumLog;
    }
}