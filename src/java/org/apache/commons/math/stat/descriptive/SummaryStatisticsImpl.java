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
package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;
import org.apache.commons.math.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.apache.commons.math.stat.descriptive.summary.SumOfLogs;
import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;

/**
 * Provides a default {@link SummaryStatistics} implementation.
 *
 * @version $Revision$ $Date$  
 */
public class SummaryStatisticsImpl extends SummaryStatistics implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 8787174276883311692L;

    /** count of values that have been added */
    protected long n = 0;
    
    /** SecondMoment is used to compute the mean and variance */
    protected SecondMoment secondMoment = null;
    
    /** sum of values that have been added */
    protected Sum sum = null;

    /** sum of the square of each value that has been added */
    protected SumOfSquares sumsq = null;

    /** min of values that have been added */
    protected Min min = null;

    /** max of values that have been added */
    protected Max max = null;

    /** sumLog of values that have been added */
    protected SumOfLogs sumLog = null;

    /** geoMean of values that have been added */
    protected GeometricMean geoMean = null;

    /** mean of values that have been added */
    protected Mean mean = null;

    /** variance of values that have been added */
    protected Variance variance = null;

    /**
     * Construct a SummaryStatistics
     */
    public SummaryStatisticsImpl() {
        sum = new Sum();
        sumsq = new SumOfSquares();
        min = new Min();
        max = new Max();
        sumLog = new SumOfLogs();
        geoMean = new GeometricMean();
        secondMoment = new SecondMoment();
    }

    /**
     * Add a value to the data
     * 
     * @param value  the value to add
     */
    public void addValue(double value) {
        sum.increment(value);
        sumsq.increment(value);
        min.increment(value);
        max.increment(value);
        sumLog.increment(value);
        geoMean.increment(value);
        secondMoment.increment(value);
        n++;
    }

    /** 
     * Returns the number of available values
     * @return The number of available values
     */
    public long getN() {
        return n;
    }

    /**
     * Returns the sum of the values that have been added to Univariate.
     * @return The sum or Double.NaN if no values have been added
     */
    public double getSum() {
        return sum.getResult();
    }

    /**
     * Returns the sum of the squares of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     * 
     * @return The sum of squares
     */
    public double getSumsq() {
        return sumsq.getResult();
    }

    /**
     * Returns the mean of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     * 
     * @return the mean
     */
    public double getMean() {
      return new Mean(secondMoment).getResult();
    }

    /**
     * Returns the standard deviation of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     * 
     * @return the standard deviation
     */
    public double getStandardDeviation() {
        double stdDev = Double.NaN;
        if (getN() > 0) {
            if (getN() > 1) {
                stdDev = Math.sqrt(getVariance());
            } else {
                stdDev = 0.0;
            }
        }
        return (stdDev);
    }

    /**
     * Returns the variance of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the variance 
     */
    public double getVariance() {
        return new Variance(secondMoment).getResult();
    }

    /**
     * Returns the maximum of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the maximum  
     */
    public double getMax() {
        return max.getResult();
    }

    /**
     * Returns the minimum of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the minimum  
     */
    public double getMin() {
        return min.getResult();
    }

    /**
     * Returns the geometric mean of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the geometric mean  
     */
    public double getGeometricMean() {
        return geoMean.getResult();
    }
    
    /**
     * Generates a text report displaying
     * summary statistics from values that
     * have been added.
     * @return String with line feeds displaying statistics
     */
    public String toString() {
        StringBuffer outBuffer = new StringBuffer();
        outBuffer.append("SummaryStatistics:\n");
        outBuffer.append("n: " + getN() + "\n");
        outBuffer.append("min: " + getMin() + "\n");
        outBuffer.append("max: " + getMax() + "\n");
        outBuffer.append("mean: " + getMean() + "\n");
        outBuffer.append("geometric mean: " + getGeometricMean() + "\n");
        outBuffer.append("variance: " + getVariance() + "\n");
        outBuffer.append("sum of squares: " + getSumsq() + "\n");
        outBuffer.append("standard deviation: " + getStandardDeviation() + "\n");
        return outBuffer.toString();
    }

    /** 
     * Resets all statistics and storage
     */
    public void clear() {
        this.n = 0;
        min.clear();
        max.clear();
        sum.clear();
        sumLog.clear();
        sumsq.clear();
        geoMean.clear();
        secondMoment.clear();
    }

}