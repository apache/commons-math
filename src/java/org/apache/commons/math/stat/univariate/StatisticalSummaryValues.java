/*
 * 
 * Copyright (c) 2004 The Apache Software Foundation. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *  
 */
package org.apache.commons.math.stat.univariate;

import java.io.Serializable;

/**
 *  Value object representing the results of a univariate statistical summary.
 *
 * @version $Revision: 1.1 $ $Date: 2004/05/18 04:17:29 $
 */
public class StatisticalSummaryValues implements Serializable, StatisticalSummary {
   
    /** Serialization id */
    static final long serialVersionUID = -5108854841843722536L;

    private double mean = Double.NaN;
    private double variance = Double.NaN;
    private long n = 0;
    private double max = Double.NaN;
    private double min = Double.NaN;
    private double sum = Double.NaN;
    
    /**
      * Constructor
      * 
      * @param mean
      * @param variance
      * @param n
      * @param max
      * @param min
      * @param sum
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
