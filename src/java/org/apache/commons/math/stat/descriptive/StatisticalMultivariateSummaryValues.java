/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.util.MathUtils;

/**
 *  Value object representing the results of a statistical multivariate summary.
 *
 * @since 1.2
 * @version $Revision: 480440 $ $Date: 2006-11-29 08:14:12 +0100 (mer., 29 nov. 2006) $
 */
public class StatisticalMultivariateSummaryValues
  implements Serializable, StatisticalMultivariateSummary {
   
    /** Serialization id */
    private static final long serialVersionUID = 8152538650791979064L;

    /** Dimension of the data. */
    private final int k;

    /** The sample mean */
    private final double[] mean;
    
    /** The sample covariance */
    private final RealMatrix covariance;

    /** The sample standard deviation. */
    private double[] stdev;
    
    /** The number of observations in the sample */
    private final long n;
    
    /** The maximum value */
    private final double[] max;
    
    /** The minimum value */
    private final double[] min;
    
    /** The sum of the sample values */
    private final double[] sum;
    
    /** The sum of the squares of the sample values */
    private final double[] sumSq;
    
    /** The sum of the logarithms of the sample values */
    private final double[] sumLog;
    
    /**
      * Constructor
      * 
      * @param mean  the sample mean
      * @param covariance  the sample covariance
      * @param stdev  the sample standard deviation
      * @param k dimension of the data
      * @param n  the number of observations in the sample 
      * @param max  the maximum value
      * @param min  the minimum value
      * @param sum  the sum of the values
      * @param sumSq the sum of the squares of the values
      * @param sumLog the sum of the logarithms of the values
      */
    public StatisticalMultivariateSummaryValues(int k, double[] mean,
                                                RealMatrix covariance, double[] stdev,
                                                long n, double[] max, double[] min,
                                                double[] sum, double[] sumSq, double[] sumLog) {
        super();
        this.k = k;
        this.mean = mean;
        this.covariance = covariance;
        this.stdev = stdev;
        this.n = n;
        this.max = max;
        this.min = min;
        this.sum = sum;
        this.sumSq = sumSq;
        this.sumLog = sumLog;
    }

    /** 
     * Returns the dimension of the data
     * @return The dimension of the data
     */
    public int getDimension() {
        return k;
    }

    /**
     * @return Returns the max.
     */
    public double[] getMax() {
        return max;
    }

    /**
     * @return Returns the mean.
     */
    public double[] getMean() {
        return mean;
    }

    /**
     * @return Returns the min.
     */
    public double[] getMin() {
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
    public double[] getSum() {
        return sum;
    }
    
    /**
     * @return Returns the sum of the squares.
     */
    public double[] getSumSq() {
        return sumSq;
    }
    
    /**
     * @return Returns the sum of the logarithms.
     */
    public double[] getSumLog() {
        return sumLog;
    }
    
    /**
     * @return Returns the standard deviation (roots of the diagonal elements)
     */
    public double[] getStandardDeviation() {
        return stdev; 
    }

    /**
     * @return Returns the covariance.
     */
    public RealMatrix getCovariance() {
        return covariance;
    }
    
    /**
     * Returns true iff <code>object</code> is a 
     * <code>StatisticalSummaryValues</code> instance and all statistics have
     *  the same values as this.
     * 
     * @param object the object to test equality against.
     * @return true if object equals this
     */
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
        if (object instanceof StatisticalMultivariateSummaryValues == false) {
            return false;
        }
        StatisticalMultivariateSummaryValues stat = (StatisticalMultivariateSummaryValues) object;
        return ((stat.getDimension() == this.getDimension()) &&
                MathUtils.equals(stat.getMax(), this.getMax()) && 
                MathUtils.equals(stat.getMean(),this.getMean()) &&
                MathUtils.equals(stat.getMin(),this.getMin()) &&
                MathUtils.equals(stat.getN(), this.getN()) &&
                MathUtils.equals(stat.getSum(), this.getSum()) &&
                MathUtils.equals(stat.getSumSq(), this.getSumSq()) &&
                MathUtils.equals(stat.getSumLog(), this.getSumLog()) &&
                MathUtils.equals(stat.getStandardDeviation(), this.getStandardDeviation()) &&
                stat.getCovariance().equals(this.getCovariance()));
    }
    
    /**
     * Returns hash code based on values of statistics
     * 
     * @return hash code
     */
    public int hashCode() {
        int result = getDimension();
        result = result * 31 + MathUtils.hash(getMax());
        result = result * 31 + MathUtils.hash(getMean());
        result = result * 31 + MathUtils.hash(getMin());
        result = result * 31 + MathUtils.hash(getN());
        result = result * 31 + MathUtils.hash(getSum());
        result = result * 31 + MathUtils.hash(getSumSq());
        result = result * 31 + MathUtils.hash(getSumLog());
        result = result * 31 + getCovariance().hashCode();
        result = result * 31 + MathUtils.hash(getStandardDeviation());
        return result;
    }

}
