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
     * A copy constructor. Creates a deep-copy of the {@code original}.
     * 
     * @param original the {@code SynchronizedSummaryStatistics} instance to copy
     */
    public SynchronizedSummaryStatistics(SynchronizedSummaryStatistics original) {
        copy(original, this);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized StatisticalSummary getSummary() {
        return super.getSummary();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addValue(double value) {
        super.addValue(value);
    }

    /** 
     * {@inheritDoc}
     */
    public synchronized long getN() {
        return super.getN();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double getSum() {
        return super.getSum();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double getSumsq() {
        return super.getSumsq();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double getMean() {
        return super.getMean();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double getStandardDeviation() {
        return super.getStandardDeviation();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double getVariance() {
        return super.getVariance();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double getMax() {
        return super.getMax();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double getMin() {
        return super.getMin();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double getGeometricMean() {
        return super.getGeometricMean();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized String toString() {
        return super.toString();
    }

    /** 
     * {@inheritDoc}
     */
    public synchronized void clear() {
        super.clear();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized boolean equals(Object object) {
        return super.equals(object);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized StorelessUnivariateStatistic getSumImpl() {
        return super.getSumImpl();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setSumImpl(StorelessUnivariateStatistic sumImpl) {
        super.setSumImpl(sumImpl);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized StorelessUnivariateStatistic getSumsqImpl() {
        return super.getSumsqImpl();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setSumsqImpl(StorelessUnivariateStatistic sumsqImpl) {
        super.setSumsqImpl(sumsqImpl);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized StorelessUnivariateStatistic getMinImpl() {
        return super.getMinImpl();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setMinImpl(StorelessUnivariateStatistic minImpl) {
        super.setMinImpl(minImpl);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized StorelessUnivariateStatistic getMaxImpl() {
        return super.getMaxImpl();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setMaxImpl(StorelessUnivariateStatistic maxImpl) {
        super.setMaxImpl(maxImpl);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized StorelessUnivariateStatistic getSumLogImpl() {
        return super.getSumLogImpl();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setSumLogImpl(StorelessUnivariateStatistic sumLogImpl) {
        super.setSumLogImpl(sumLogImpl);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized StorelessUnivariateStatistic getGeoMeanImpl() {
        return super.getGeoMeanImpl();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setGeoMeanImpl(StorelessUnivariateStatistic geoMeanImpl) {
        super.setGeoMeanImpl(geoMeanImpl);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized StorelessUnivariateStatistic getMeanImpl() {
        return super.getMeanImpl();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setMeanImpl(StorelessUnivariateStatistic meanImpl) {
        super.setMeanImpl(meanImpl);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized StorelessUnivariateStatistic getVarianceImpl() {
        return super.getVarianceImpl();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setVarianceImpl(StorelessUnivariateStatistic varianceImpl) {
        super.setVarianceImpl(varianceImpl);
    }
    
    /**
     * Returns a copy of this SynchronizedSummaryStatistics instance with the
     * same internal state.
     * 
     * @return a copy of this
     */
    public synchronized SynchronizedSummaryStatistics copy() {
        SynchronizedSummaryStatistics result = 
            new SynchronizedSummaryStatistics();
        copy(this, result);
        return result; 
    }
     
    /**
     * Copies source to dest.
     * <p>Neither source nor dest can be null.</p>
     * <p>Acquires synchronization lock on source, then dest before copying.</p>
     * 
     * @param source SynchronizedSummaryStatistics to copy
     * @param dest SynchronizedSummaryStatistics to copy to
     * @throws NullPointerException if either source or dest is null
     */
    public static void copy(SynchronizedSummaryStatistics source,
            SynchronizedSummaryStatistics dest) {
        synchronized (source) {
            synchronized (dest) {
                SummaryStatistics.copy(source, dest);
            }
        }
    }
    
}
