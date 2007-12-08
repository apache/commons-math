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

import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.apache.commons.math.stat.descriptive.summary.SumOfLogs;
import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.math.util.MathUtils;

/**
 * <p>Computes summary statistics for a stream of data values added using the 
 * {@link #addValue(double) addValue} method. The data values are not stored in
 * memory, so this class can be used to compute statistics for very large
 * data streams.</p>
 * 
 * <p>The {@link StorelessUnivariateStatistic} instances used to maintain
 * summary state and compute statistics are configurable via setters.
 * For example, the default implementation for the variance can be overridden by
 * calling {@link #setVarianceImpl(StorelessUnivariateStatistic)}. Actual
 * parameters to these methods must implement the 
 * {@link StorelessUnivariateStatistic} interface and configuration must be
 * completed before <code>addValue</code> is called. No configuration is
 * necessary to use the default, commons-math provided implementations.</p>
 * 
 * <p>Note: This class is not thread-safe. Use 
 * {@link SynchronizedSummaryStatistics} if concurrent access from multiple
 * threads is required.</p>
 *
 * @version $Revision$ $Date$
 */
public class SummaryStatistics implements StatisticalSummary, Serializable {

    /** Serialization UID */
    private static final long serialVersionUID = -3346512372447011854L;
     
    /**
     * Create an instance of a <code>SummaryStatistics</code>
     * 
     * @param cls the type of <code>SummaryStatistics</code> object to
     *        create. 
     * @return a new instance. 
     * @deprecated to be removed in commons-math 2.0
     * @throws InstantiationException is thrown if the object can not be
     *            created.
     * @throws IllegalAccessException is thrown if the type's default
     *            constructor is not accessible.
     */
    public static SummaryStatistics newInstance(Class cls) throws 
        InstantiationException, IllegalAccessException {
        return (SummaryStatistics)cls.newInstance();
    }
    
    /**
     * Create an instance of a <code>SummaryStatistics</code>
     * 
     * @return a new SummaryStatistics instance.
     * @deprecated to be removed in commons-math 2.0 
     */
    public static SummaryStatistics newInstance() {
        SummaryStatistics instance = null;
        try {
            DiscoverClass dc = new DiscoverClass();
            instance = (SummaryStatistics) dc.newInstance(
                SummaryStatistics.class,
                "org.apache.commons.math.stat.descriptive.SummaryStatisticsImpl");
        } catch(Throwable t) {
            return new SummaryStatisticsImpl();
        }
        return instance;
    }
    
    /**
     * Construct a SummaryStatistics instance
     */
    public SummaryStatistics() {
    }
    
    /** count of values that have been added */
    protected long n = 0;
    
    /** SecondMoment is used to compute the mean and variance */
    protected SecondMoment secondMoment = new SecondMoment();
    
    /** sum of values that have been added */
    protected Sum sum = new Sum();

    /** sum of the square of each value that has been added */
    protected SumOfSquares sumsq = new SumOfSquares();

    /** min of values that have been added */
    protected Min min = new Min();

    /** max of values that have been added */
    protected Max max = new Max();

    /** sumLog of values that have been added */
    protected SumOfLogs sumLog = new SumOfLogs();

    /** geoMean of values that have been added */
    protected GeometricMean geoMean = new GeometricMean();

    /** mean of values that have been added */
    protected Mean mean = new Mean();

    /** variance of values that have been added */
    protected Variance variance = new Variance();
    
    //  Statistics implementations - can be reset by setters 
    private StorelessUnivariateStatistic sumImpl = sum;
    private StorelessUnivariateStatistic sumsqImpl = sumsq;
    private StorelessUnivariateStatistic minImpl = min;
    private StorelessUnivariateStatistic maxImpl = max;
    private StorelessUnivariateStatistic sumLogImpl = sumLog;
    private StorelessUnivariateStatistic geoMeanImpl = geoMean;
    private StorelessUnivariateStatistic meanImpl = mean;
    private StorelessUnivariateStatistic varianceImpl = variance;

    /**
     * Return a {@link StatisticalSummaryValues} instance reporting current
     * statistics.
     * 
     * @return Current values of statistics 
     */
    public StatisticalSummary getSummary() {
        return new StatisticalSummaryValues(getMean(), getVariance(), getN(),
                getMax(), getMin(), getSum());
    }
    
    /**
     * Add a value to the data
     * 
     * @param value  the value to add
     */
    public void addValue(double value) {
        sumImpl.increment(value);
        sumsqImpl.increment(value);
        minImpl.increment(value);
        maxImpl.increment(value);
        sumLogImpl.increment(value);
        geoMean.increment(value);
        secondMoment.increment(value);
        // If mean or variance have been overridden,
        // need to increment these, since they don't have secondMoment
        if (!(meanImpl instanceof Mean)) {
                meanImpl.increment(value);
        }
        if (!(varianceImpl instanceof Variance)) {
            varianceImpl.increment(value);
        }
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
     * Returns the sum of the values that have been added
     * @return The sum or <code>Double.NaN</code> if no values have been added
     */
    public double getSum() {
        return sumImpl.getResult();
    }

    /**
     * Returns the sum of the squares of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     * 
     * @return The sum of squares
     */
    public double getSumsq() {
        return sumsqImpl.getResult();
    }

    /**
     * Returns the mean of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     * 
     * @return the mean
     */
    public double getMean() {
      if (mean == meanImpl) {
          return new Mean(secondMoment).getResult();
      } else {
          return meanImpl.getResult();
      }
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
        if (varianceImpl == variance) {
            return new Variance(secondMoment).getResult();
        } else {
            return varianceImpl.getResult();
        }
    }

    /**
     * Returns the maximum of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the maximum  
     */
    public double getMax() {
        return maxImpl.getResult();
    }

    /**
     * Returns the minimum of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the minimum  
     */
    public double getMin() {
        return minImpl.getResult();
    }

    /**
     * Returns the geometric mean of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the geometric mean  
     */
    public double getGeometricMean() {
        return geoMeanImpl.getResult();
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
        minImpl.clear();
        maxImpl.clear();
        sumImpl.clear();
        sumLogImpl.clear();
        sumsqImpl.clear();
        geoMeanImpl.clear();
        secondMoment.clear();
        if (meanImpl != mean) {
            meanImpl.clear();
        }
        if (varianceImpl != variance) {
            varianceImpl.clear();
        }
    }
    
    /**
     * Returns true iff <code>object</code> is a <code>SummaryStatistics</code>
     * instance and all statistics have the same values as this.
     * @param object the object to test equality against.
     * @return true if object equals this
     */
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
        if (object instanceof SummaryStatistics == false) {
            return false;
        }
        SummaryStatistics stat = (SummaryStatistics) object;
        return (MathUtils.equals(stat.getGeometricMean(), 
                this.getGeometricMean()) &&
                MathUtils.equals(stat.getMax(), this.getMax()) && 
                MathUtils.equals(stat.getMean(),this.getMean()) &&
                MathUtils.equals(stat.getMin(),this.getMin()) &&
                MathUtils.equals(stat.getN(), this.getN()) &&
                MathUtils.equals(stat.getSum(), this.getSum()) &&
                MathUtils.equals(stat.getSumsq(),this.getSumsq()) &&
                MathUtils.equals(stat.getVariance(),this.getVariance()));
    }
    
    /**
     * Returns hash code based on values of statistics
     * 
     * @return hash code
     */
    public int hashCode() {
        int result = 31 + MathUtils.hash(getGeometricMean());
        result = result * 31 + MathUtils.hash(getGeometricMean());
        result = result * 31 + MathUtils.hash(getMax());
        result = result * 31 + MathUtils.hash(getMean());
        result = result * 31 + MathUtils.hash(getMin());
        result = result * 31 + MathUtils.hash(getN());
        result = result * 31 + MathUtils.hash(getSum());
        result = result * 31 + MathUtils.hash(getSumsq());
        result = result * 31 + MathUtils.hash(getVariance());
        return result;
    }

    // Getters and setters for statistics implementations
    /**
     * Returns the currently configured Sum implementation
     * 
     * @return the StorelessUnivariateStatistic implementing the sum
     */
    public synchronized StorelessUnivariateStatistic getSumImpl() {
        return sumImpl;
    }

    /**
     * <p>Sets the implementation for the Sum.</p>
     * <p>This method must be activated before any data has been added - i.e.,
     * before {@link #addValue(double) addValue} has been used to add data; 
     * otherwise an IllegalStateException will be thrown.</p>
     * 
     * @param sumImpl the StorelessUnivariateStatistic instance to use
     * for computing the Sum
     * @throws IllegalStateException if data has already been added
     *  (i.e if n > 0)
     */
    public synchronized void setSumImpl(StorelessUnivariateStatistic sumImpl) {
        checkEmpty();
        this.sumImpl = sumImpl;
    }

    /**
     * Returns the currently configured sum of squares implementation
     * 
     * @return the StorelessUnivariateStatistic implementing the sum of squares
     */
    public synchronized StorelessUnivariateStatistic getSumsqImpl() {
        return sumsqImpl;
    }

    /**
     * <p>Sets the implementation for the sum of squares.</p>
     * <p>This method must be activated before any data has been added - i.e.,
     * before {@link #addValue(double) addValue} has been used to add data; 
     * otherwise an IllegalStateException will be thrown.</p>
     * 
     * @param sumsqImpl the StorelessUnivariateStatistic instance to use
     * for computing the sum of squares
     * @throws IllegalStateException if data has already been added
     *  (i.e if n > 0)
     */
    public synchronized void setSumsqImpl(
            StorelessUnivariateStatistic sumsqImpl) {
        checkEmpty();
        this.sumsqImpl = sumsqImpl;
    }

    /**
     * Returns the currently configured minimum implementation
     * 
     * @return the StorelessUnivariateStatistic implementing the minimum
     */
    public synchronized StorelessUnivariateStatistic getMinImpl() {
        return minImpl;
    }

    /**
     * <p>Sets the implementation for the minimum.</p>
     * <p>This method must be activated before any data has been added - i.e.,
     * before {@link #addValue(double) addValue} has been used to add data; 
     * otherwise an IllegalStateException will be thrown.</p>
     * 
     * @param minImpl the StorelessUnivariateStatistic instance to use
     * for computing the minimum
     * @throws IllegalStateException if data has already been added
     *  (i.e if n > 0)
     */
    public synchronized void setMinImpl(StorelessUnivariateStatistic minImpl) {
        checkEmpty();
        this.minImpl = minImpl;
    }

    /**
     * Returns the currently configured maximum implementation
     * 
     * @return the StorelessUnivariateStatistic implementing the maximum
     */
    public synchronized StorelessUnivariateStatistic getMaxImpl() {
        return maxImpl;
    }

    /**
     * <p>Sets the implementation for the maximum.</p>
     * <p>This method must be activated before any data has been added - i.e.,
     * before {@link #addValue(double) addValue} has been used to add data; 
     * otherwise an IllegalStateException will be thrown.</p>
     * 
     * @param maxImpl the StorelessUnivariateStatistic instance to use
     * for computing the maximum
     * @throws IllegalStateException if data has already been added
     *  (i.e if n > 0)
     */
    public synchronized void setMaxImpl(StorelessUnivariateStatistic maxImpl) {
        checkEmpty();
        this.maxImpl = maxImpl;
    }

    /**
     * Returns the currently configured sum of logs implementation
     * 
     * @return the StorelessUnivariateStatistic implementing the log sum
     */
    public synchronized StorelessUnivariateStatistic getSumLogImpl() {
        return sumLogImpl;
    }

    /**
     * <p>Sets the implementation for the sum of logs.</p>
     * <p>This method must be activated before any data has been added - i.e.,
     * before {@link #addValue(double) addValue} has been used to add data; 
     * otherwise an IllegalStateException will be thrown.</p>
     * 
     * @param sumLogImpl the StorelessUnivariateStatistic instance to use
     * for computing the log sum
     * @throws IllegalStateException if data has already been added 
     *  (i.e if n > 0)
     */
    public synchronized void setSumLogImpl(
            StorelessUnivariateStatistic sumLogImpl) {
        checkEmpty();
        this.sumLogImpl = sumLogImpl;
    }

    /**
     * Returns the currently configured geometric mean implementation
     * 
     * @return the StorelessUnivariateStatistic implementing the geometric mean
     */
    public synchronized StorelessUnivariateStatistic getGeoMeanImpl() {
        return geoMeanImpl;
    }

    /**
     * <p>Sets the implementation for the geometric mean.</p>
     * <p>This method must be activated before any data has been added - i.e.,
     * before {@link #addValue(double) addValue} has been used to add data; 
     * otherwise an IllegalStateException will be thrown.</p>
     * 
     * @param geoMeanImpl the StorelessUnivariateStatistic instance to use
     * for computing the geometric mean
     * @throws IllegalStateException if data has already been added
     *  (i.e if n > 0)
     */
    public synchronized void setGeoMeanImpl(
            StorelessUnivariateStatistic geoMeanImpl) {
        checkEmpty();
        this.geoMeanImpl = geoMeanImpl;
    }

    /**
     * Returns the currently configured mean implementation
     * 
     * @return the StorelessUnivariateStatistic implementing the mean
     */
    public synchronized StorelessUnivariateStatistic getMeanImpl() {
        return meanImpl;
    }

    /**
     * <p>Sets the implementation for the mean.</p>
     * <p>This method must be activated before any data has been added - i.e.,
     * before {@link #addValue(double) addValue} has been used to add data; 
     * otherwise an IllegalStateException will be thrown.</p>
     * 
     * @param meanImpl the StorelessUnivariateStatistic instance to use
     * for computing the mean
     * @throws IllegalStateException if data has already been added
     *  (i.e if n > 0)
     */
    public synchronized void setMeanImpl(
            StorelessUnivariateStatistic meanImpl) {
        checkEmpty();
        this.meanImpl = meanImpl;
    }

    /**
     * Returns the currently configured variance implementation
     * 
     * @return the StorelessUnivariateStatistic implementing the variance
     */
    public synchronized StorelessUnivariateStatistic getVarianceImpl() {
        return varianceImpl;
    }

    /**
     * <p>Sets the implementation for the variance.</p>
     * <p>This method must be activated before any data has been added - i.e.,
     * before {@link #addValue(double) addValue} has been used to add data; 
     * otherwise an IllegalStateException will be thrown.</p>
     * 
     * @param varianceImpl the StorelessUnivariateStatistic instance to use
     * for computing the variance
     * @throws IllegalStateException if data has already been added
     *  (i.e if n > 0)
     */
    public synchronized void setVarianceImpl(
            StorelessUnivariateStatistic varianceImpl) {
        checkEmpty();
        this.varianceImpl = varianceImpl;
    }
    
    /**
     * Throws IllegalStateException if n > 0.
     */
    private void checkEmpty() {
        if (n > 0) {
            throw new IllegalStateException(
                "Implementations must be configured before values are added.");
        }
    }

}
