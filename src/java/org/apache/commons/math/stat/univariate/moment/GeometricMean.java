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
 * geometric mean </a> of the available values
 * @version $Revision: 1.16 $ $Date: 2004/02/21 21:35:15 $
 */
public class GeometricMean extends SumOfLogs implements Serializable{

    static final long serialVersionUID = -8178734905303459453L;  
      
    /** */
    protected long n = 0;

    /** */
    private double geoMean = Double.NaN;

    /** */
    private double lastSum = 0.0;

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
        if (lastSum != super.getResult() || n == 1) {
            lastSum = super.getResult();
            geoMean = Math.exp(lastSum / (double) n);
        }
        return geoMean;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        super.clear();
        lastSum = 0.0;
        geoMean = Double.NaN;
        n = 0;
    }

    /**
     * Returns the geometric mean for this collection of values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
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