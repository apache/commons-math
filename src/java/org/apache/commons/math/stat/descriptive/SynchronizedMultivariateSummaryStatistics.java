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

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.linear.RealMatrix;

/**
 * Implementation of
 * {@link org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics} that
 * is safe to use in a multithreaded environment.  Multiple threads can safely
 * operate on a single instance without causing runtime exceptions due to race
 * conditions.  In effect, this implementation makes modification and access
 * methods atomic operations for a single instance.  That is to say, as one
 * thread is computing a statistic from the instance, no other thread can modify
 * the instance nor compute another statistic.
 * @since 1.2
 * @version $Revision: 618097 $ $Date: 2008-02-03 22:39:08 +0100 (dim., 03 févr. 2008) $
 */
public class SynchronizedMultivariateSummaryStatistics
  extends MultivariateSummaryStatistics {

    /** Serialization UID */
    private static final long serialVersionUID = 7099834153347155363L;

    /**
     * Construct a SynchronizedMultivariateSummaryStatistics instance
     * @param k dimension of the data
     * @param isCovarianceBiasCorrected if true, the unbiased sample
     * covariance is computed, otherwise the biased population covariance
     * is computed
     */
    public SynchronizedMultivariateSummaryStatistics(int k, boolean isCovarianceBiasCorrected) {
        super(k, isCovarianceBiasCorrected);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#addValue(double[])
     */
    public synchronized void addValue(double[] value)
      throws DimensionMismatchException {
      super.addValue(value);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getDimension()
     */
    public synchronized int getDimension() {
        return super.getDimension();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getN()
     */
    public synchronized long getN() {
        return super.getN();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getSum()
     */
    public synchronized double[] getSum() {
        return super.getSum();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getSumSq()
     */
    public synchronized double[] getSumSq() {
        return super.getSumSq();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getSumLog()
     */
    public synchronized double[] getSumLog() {
        return super.getSumLog();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getMean()
     */
    public synchronized double[] getMean() {
        return super.getMean();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getStandardDeviation()
     */
    public synchronized double[] getStandardDeviation() {
        return super.getStandardDeviation();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getCovariance()
     */
    public synchronized RealMatrix getCovariance() {
        return super.getCovariance();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getMax()
     */
    public synchronized double[] getMax() {
        return super.getMax();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getMin()
     */
    public synchronized double[] getMin() {
        return super.getMin();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getGeometricMean()
     */
    public synchronized double[] getGeometricMean() {
        return super.getGeometricMean();
    }
    
    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#toString()
     */
    public synchronized String toString() {
        return super.toString();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#clear()
     */
    public synchronized void clear() {
        super.clear();
    }
    
    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#equals(Object)
     */
    public synchronized boolean equals(Object object) {
        return super.equals(object);
    }
    
    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#hashCode()
     */
    public synchronized int hashCode() {
        return super.hashCode();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getSumImpl()
     */
    public synchronized StorelessUnivariateStatistic[] getSumImpl() {
        return super.getSumImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#setSumImpl(StorelessUnivariateStatistic[])
     */
    public synchronized void setSumImpl(StorelessUnivariateStatistic[] sumImpl)
      throws DimensionMismatchException {
        super.setSumImpl(sumImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getSumsqImpl()
     */
    public synchronized StorelessUnivariateStatistic[] getSumsqImpl() {
        return super.getSumsqImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#setSumsqImpl(StorelessUnivariateStatistic[])
     */
    public synchronized void setSumsqImpl(StorelessUnivariateStatistic[] sumsqImpl)
      throws DimensionMismatchException {
        super.setSumsqImpl(sumsqImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getMinImpl()
     */
    public synchronized StorelessUnivariateStatistic[] getMinImpl() {
        return super.getMinImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#setMinImpl(StorelessUnivariateStatistic[])
     */
    public synchronized void setMinImpl(StorelessUnivariateStatistic[] minImpl)
      throws DimensionMismatchException {
        super.setMinImpl(minImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getMaxImpl()
     */
    public synchronized StorelessUnivariateStatistic[] getMaxImpl() {
        return super.getMaxImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#setMaxImpl(StorelessUnivariateStatistic[])
     */
    public synchronized void setMaxImpl(StorelessUnivariateStatistic[] maxImpl)
      throws DimensionMismatchException {
        super.setMaxImpl(maxImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getSumLogImpl()
     */
    public synchronized StorelessUnivariateStatistic[] getSumLogImpl() {
        return super.getSumLogImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#setSumLogImpl(StorelessUnivariateStatistic[])
     */
    public synchronized void setSumLogImpl(StorelessUnivariateStatistic[] sumLogImpl)
      throws DimensionMismatchException {
        super.setSumLogImpl(sumLogImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getGeoMeanImpl()
     */
    public synchronized StorelessUnivariateStatistic[] getGeoMeanImpl() {
        return super.getGeoMeanImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#setGeoMeanImpl(StorelessUnivariateStatistic[])
     */
    public synchronized void setGeoMeanImpl(StorelessUnivariateStatistic[] geoMeanImpl)
      throws DimensionMismatchException {
        super.setGeoMeanImpl(geoMeanImpl);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#getMeanImpl()
     */
    public synchronized StorelessUnivariateStatistic[] getMeanImpl() {
        return super.getMeanImpl();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics#setMeanImpl(StorelessUnivariateStatistic[])
     */
    public synchronized void setMeanImpl(StorelessUnivariateStatistic[] meanImpl)
      throws DimensionMismatchException {
        super.setMeanImpl(meanImpl);
    }

}
