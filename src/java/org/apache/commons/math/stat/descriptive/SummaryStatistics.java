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

import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.commons.math.util.MathUtils;

/**
 * Abstract factory class for univariate statistical summaries.
 *
 * @version $Revision$ $Date$
 */
public abstract class SummaryStatistics implements StatisticalSummary, Serializable {

    /** Serialization UID */
    private static final long serialVersionUID = -6400596334135654825L;
     
    /**
     * Create an instance of a <code>SummaryStatistics</code>
     * 
     * @param cls the type of <code>SummaryStatistics</code> object to
     *        create. 
     * @return a new factory. 
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
     * Adds the value to the data to be summarized
     * @param v the value to be added 
     */
    public abstract void addValue(double v);

    /** 
     * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
     * arithmetic mean </a> of the available values 
     * @return The mean or Double.NaN if no values have been added.
     */
    public abstract double getMean();

    /** 
     * Returns the <a href="http://www.xycoon.com/geometric_mean.htm">
     * geometric mean </a> of the available values
     * @return The geometricMean, Double.NaN if no values have been added, 
     * or if the productof the available values is less than or equal to 0.
     */
    public abstract double getGeometricMean();

    /** 
     * Returns the variance of the available values.
     * @return The variance, Double.NaN if no values have been added 
     * or 0.0 for a single value set.  
     */
    public abstract double getVariance();

    /** 
     * Returns the standard deviation of the available values.
     * @return The standard deviation, Double.NaN if no values have been added 
     * or 0.0 for a single value set. 
     */
    public abstract double getStandardDeviation();
    
    /** 
     * Returns the maximum of the available values
     * @return The max or Double.NaN if no values have been added.
     */
    public abstract double getMax();

    /** 
    * Returns the minimum of the available values
    * @return The min or Double.NaN if no values have been added.
    */
    public abstract double getMin();

    /** 
     * Returns the number of available values
     * @return The number of available values
     */
    public abstract long getN();

    /**
     * Returns the sum of the values that have been added to Univariate.
     * @return The sum or Double.NaN if no values have been added
     */
    public abstract double getSum();

    /**
     * Returns the sum of the squares of the available values.
     * @return The sum of the squares or Double.NaN if no 
     * values have been added.
     */
    public abstract double getSumsq();

    /** 
     * Resets all statistics
     */
    public abstract void clear();
    
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

}
