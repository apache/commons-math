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
package org.apache.commons.math.stat.univariate;

import java.io.Serializable;

/**
 *  Value object representing the results of a univariate statistical summary.
 *
 * @version $Revision: 1.3 $ $Date: 2004/06/23 16:26:16 $
 */
public class StatisticalSummaryValues implements Serializable, 
    StatisticalSummary {
   
    /** Serialization id */
    static final long serialVersionUID = -5108854841843722536L;

    /** The sample mean */
    private double mean = Double.NaN;
    
    /** The sample variance */
    private double variance = Double.NaN;
    
    /** The number of observations in the sample */
    private long n = 0;
    
    /** The maximum value */
    private double max = Double.NaN;
    
    /** The minimum value */
    private double min = Double.NaN;
    
    /** The sum of the sample values */
    private double sum = Double.NaN;
    
    /**
      * Constructor
      * 
      * @param mean  the sample mean
      * @param variance  the sample variance
      * @param n  the number of observations in the sample 
      * @param max  the maximum value
      * @param min  the minimum value
      * @param sum  the sum of the values
     */
    public StatisticalSummaryValues(double mean, double variance, long n,
        double max, double min, double sum) {
        super();
        this.mean = mean;
        this.variance = variance;
        this.n = n;
        this.max = max;
        this.min = min;
        this.sum = sum;
    }
    
    /** Private no argument contstructor */
    private StatisticalSummaryValues() {
        super();
    }

    
    /**
     * @return Returns the max.
     */
    public double getMax() {
        return max;
    }

    /**
     * @return Returns the mean.
     */
    public double getMean() {
        return mean;
    }

    /**
     * @return Returns the min.
     */
    public double getMin() {
        return min;
    }

    /**
     * @return Returns the number of values.
     */
    public long getN() {
        return n;
    }

    /**
     * @return Returns the sum.
     */
    public double getSum() {
        return sum;
    }
    
    /**
     * @return Returns the standard deviation
     */
    public double getStandardDeviation() {
        return Math.sqrt(variance);
    }

    /**
     * @return Returns the variance.
     */
    public double getVariance() {
        return variance;
    }

}
