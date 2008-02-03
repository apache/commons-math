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

/**
 * Implementation of
 * {@link org.apache.commons.math.stat.descriptive.SummaryStatistics} that
 * is safe to use in a multithreaded environment.  Multiple threads can safely
 * operate on a single instance without causing runtime exceptions due to race
 * conditions.  In effect, this implementation makes modification and access
 * methods atomic operations for a single instance.  That is to say, as one
 * thread is computing a statistic from the instance, no other thread can modify
 * the instance nor compute another statistic. 
 *
 * @since 1.2
 * @version $Revision$ $Date$
 */
public class SynchronizedSummaryStatistics extends SummaryStatistics {

    /** Serialization UID */
    private static final long serialVersionUID = 1909861009042253704L;

    /**
     * Construct a SynchronizedSummaryStatistics instance
     */
    public SynchronizedSummaryStatistics() {
        super();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getSummary()
     */
    public synchronized StatisticalSummary getSummary() {
        return super.getSummary();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#addValue(double)
     */
    public synchronized void addValue(double value) {
        super.addValue(value);
    }

    /** 
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getN()
     */
    public synchronized long getN() {
        return super.getN();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getSum()
     */
    public synchronized double getSum() {
        return super.getSum();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getSumsq()
     */
    public synchronized double getSumsq() {
        return super.getSumsq();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getMean()
     */
    public synchronized double getMean() {
        return super.getMean();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getStandardDeviation()
     */
    public synchronized double getStandardDeviation() {
        return super.getStandardDeviation();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getVariance()
     */
    public synchronized double getVariance() {
        return super.getVariance();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getMax()
     */
    public synchronized double getMax() {
        return super.getMax();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getMin()
     */
    public synchronized double getMin() {
        return super.getMin();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getGeometricMean()
     */
    public synchronized double getGeometricMean() {
        return super.getGeometricMean();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#toString()
     */
    public synchronized String toString() {
        return super.toString();
    }

    /** 
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#clear()
     */
    public synchronized void clear() {
        super.clear();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#equals(Object)
     */
    public synchronized boolean equals(Object object) {
        return super.equals(object);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#hashCode()
     */
    public synchronized int hashCode() {
        return super.hashCode();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getSumImpl()
     */
    public synchronized StorelessUnivariateStatistic getSumImpl() {
        return super.getSumImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#setSumImpl(StorelessUnivariateStatistic)
     */
    public synchronized void setSumImpl(StorelessUnivariateStatistic sumImpl) {
        super.setSumImpl(sumImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getSumsqImpl()
     */
    public synchronized StorelessUnivariateStatistic getSumsqImpl() {
        return super.getSumsqImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#setSumsqImpl(StorelessUnivariateStatistic)
     */
    public synchronized void setSumsqImpl(StorelessUnivariateStatistic sumsqImpl) {
        super.setSumsqImpl(sumsqImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getMinImpl()
     */
    public synchronized StorelessUnivariateStatistic getMinImpl() {
        return super.getMinImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#setMinImpl(StorelessUnivariateStatistic)
     */
    public synchronized void setMinImpl(StorelessUnivariateStatistic minImpl) {
        super.setMinImpl(minImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getMaxImpl()
     */
    public synchronized StorelessUnivariateStatistic getMaxImpl() {
        return super.getMaxImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#setMaxImpl(StorelessUnivariateStatistic)
     */
    public synchronized void setMaxImpl(StorelessUnivariateStatistic maxImpl) {
        super.setMaxImpl(maxImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getSumLogImpl()
     */
    public synchronized StorelessUnivariateStatistic getSumLogImpl() {
        return super.getSumLogImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#setSumLogImpl(StorelessUnivariateStatistic)
     */
    public synchronized void setSumLogImpl(StorelessUnivariateStatistic sumLogImpl) {
        super.setSumLogImpl(sumLogImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getGeoMeanImpl()
     */
    public synchronized StorelessUnivariateStatistic getGeoMeanImpl() {
        return super.getGeoMeanImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#setGeoMeanImpl(StorelessUnivariateStatistic)
     */
    public synchronized void setGeoMeanImpl(StorelessUnivariateStatistic geoMeanImpl) {
        super.setGeoMeanImpl(geoMeanImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getMeanImpl()
     */
    public synchronized StorelessUnivariateStatistic getMeanImpl() {
        return super.getMeanImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#setMeanImpl(StorelessUnivariateStatistic)
     */
    public synchronized void setMeanImpl(StorelessUnivariateStatistic meanImpl) {
        super.setMeanImpl(meanImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#getVarianceImpl()
     */
    public synchronized StorelessUnivariateStatistic getVarianceImpl() {
        return super.getVarianceImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.SummaryStatistics#setVarianceImpl(StorelessUnivariateStatistic)
     */
    public synchronized void setVarianceImpl(StorelessUnivariateStatistic varianceImpl) {
        super.setVarianceImpl(varianceImpl);
    }
    
}
