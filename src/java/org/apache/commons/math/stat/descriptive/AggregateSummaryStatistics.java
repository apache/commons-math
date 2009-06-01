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

/**
 * <p>
 * A StatisticalSummary that aggregates statistics from several data sets or
 * data set partitions.  In its simplest usage mode, the client creates an
 * instance via the zero-argument constructor, then uses
 * {@link #createContributingStatistics()} to obtain a {@code SummaryStatistics}
 * for each individual data set / partition.  The per-set statistics objects
 * are used as normal, and at any time the aggregate statistics for all the
 * contributors can be obtained from this object.
 * </p><p>
 * Clients with specialized requirements can use alternative constructors to
 * control the statistics implementations and initial values used by the
 * contributing and the internal aggregate {@code SummaryStatistics} objects.
 * </p>
 *
 * @since 2.0
 * @version $Revision:$ $Date:$
 * 
 */
public class AggregateSummaryStatistics implements StatisticalSummary,
        Serializable {

 
    /** Serializable version identifier */
   private static final long serialVersionUID = -8207112444016386906L;

    /**
     * A SummaryStatistics serving as a prototype for creating SummaryStatistics
     * contributing to this aggregate 
     */
    private SummaryStatistics statisticsPrototype;
    
    /**
     * The SummaryStatistics in which aggregate statistics are accumulated 
     */
    private SummaryStatistics statistics;
    
    /**
     * Initializes a new AggregateSummaryStatistics with default statistics
     * implementations.
     * 
     * @see SummaryStatistics#SummaryStatistics()
     */
    public AggregateSummaryStatistics() {
        this(new SummaryStatistics());
    }
    
    /**
     * Initializes a new AggregateSummaryStatistics with the specified statistics
     * object as a prototype for contributing statistics and for the internal
     * aggregate statistics.  This provides for customized statistics implementations
     * to be used by contributing and aggregate statistics.
     *
     * @param prototypeStatistics a {@code SummaryStatistics} serving as a
     *      prototype both for the internal aggregate statistics and for
     *      contributing statistics obtained via the
     *      {@code createContributingStatistics()} method.  Being a prototype
     *      means that other objects are initialized by copying this object's state. 
     *      If {@code null}, a new, default statistics object is used.  Any statistic
     *      values in the prototype are propagated to contributing statistics
     *      objects and (once) into these aggregate statistics.
     * @see #createContributingStatistics()
     */
    public AggregateSummaryStatistics(SummaryStatistics prototypeStatistics) {
        this(prototypeStatistics, (prototypeStatistics == null ? null :
                new SummaryStatistics(prototypeStatistics)));
    }
    
    /**
     * Initializes a new AggregateSummaryStatistics with the specified statistics
     * object as a prototype for contributing statistics and for the internal
     * aggregate statistics.  This provides for different statistics implementations
     * to be used by contributing and aggregate statistics and for an initial
     * state to be supplied for the aggregate statistics.
     *
     * @param prototypeStatistics a {@code SummaryStatistics} serving as a
     *      prototype both for the internal aggregate statistics and for
     *      contributing statistics obtained via the
     *      {@code createContributingStatistics()} method.  Being a prototype
     *      means that other objects are initialized by copying this object's state. 
     *      If {@code null}, a new, default statistics object is used.  Any statistic
     *      values in the prototype are propagated to contributing statistics
     *      objects, but not into these aggregate statistics.
     * @param initialStatistics a {@code SummaryStatistics} to serve as the
     *      internal aggregate statistics object.  If {@code null}, a new, default
     *      statistics object is used.
     * @see #createContributingStatistics()
     */
    public AggregateSummaryStatistics(SummaryStatistics prototypeStatistics,
            SummaryStatistics initialStatistics) {
        this.statisticsPrototype = ((prototypeStatistics == null) ?
                new SummaryStatistics() : prototypeStatistics);
        this.statistics = ((initialStatistics == null) ?
                new SummaryStatistics() : initialStatistics);
    }
    
    /**
     * {@inheritDoc}.  This version returns the maximum over all the aggregated
     * data.
     *
     * @see StatisticalSummary#getMax()
     */
    public double getMax() {
        return statistics.getMax();
    }

    /**
     * {@inheritDoc}.  This version returns the mean of all the aggregated data.
     *
     * @see StatisticalSummary#getMean()
     */
    public double getMean() {
        return statistics.getMean();
    }

    /**
     * {@inheritDoc}.  This version returns the minimum over all the aggregated
     * data.
     *
     * @see StatisticalSummary#getMin()
     */
    public double getMin() {
        return statistics.getMin();
    }

    /**
     * {@inheritDoc}.  This version returns a count of all the aggregated data.
     *
     * @see StatisticalSummary#getN()
     */
    public long getN() {
        return statistics.getN();
    }

    /**
     * {@inheritDoc}.  This version returns the standard deviation of all the
     * aggregated data.
     *
     * @see StatisticalSummary#getStandardDeviation()
     */
    public double getStandardDeviation() {
        return statistics.getStandardDeviation();
    }

    /**
     * {@inheritDoc}.  This version returns a sum of all the aggregated data.
     *
     * @see StatisticalSummary#getSum()
     */
    public double getSum() {
        return statistics.getSum();
    }

    /**
     * {@inheritDoc}.  This version returns the variance of all the aggregated
     * data.
     *
     * @see StatisticalSummary#getVariance()
     */
    public double getVariance() {
        return statistics.getVariance();
    }

    /**
     * Creates and returns a {@code SummaryStatistics} whose data will be
     * aggregated with those of this {@code AggregateSummaryStatistics}. 
     *
     * @return a {@code SummaryStatistics} whose data will be aggregated with
     *      those of this {@code AggregateSummaryStatistics}.  The initial state
     *      is a copy of the configured prototype statistics.
     */
    public SummaryStatistics createContributingStatistics() {
        SummaryStatistics contributingStatistics
                = new AggregatingSummaryStatistics(statistics);
        
        SummaryStatistics.copy(statisticsPrototype, contributingStatistics);
        
        return contributingStatistics;
    }
    
    /**
     * A SummaryStatistics that also forwards all values added to it to a second
     * {@code SummaryStatistics} for aggregation.
     *
     * @since 2.0
     */
    private static class AggregatingSummaryStatistics extends SummaryStatistics {
        
        /**
         * The serialization version of this class
         */
        private static final long serialVersionUID = 1L;
        
        /**
         * An additional SummaryStatistics into which values added to these
         * statistics (and possibly others) are aggregated
         */
        private SummaryStatistics aggregateStatistics;
        
        /**
         * Initializes a new AggregatingSummaryStatistics with the specified
         * aggregate statistics object
         *
         * @param aggregateStatistics a {@code SummaryStatistics} into which
         *      values added to this statistics object should be aggregated
         */
        public AggregatingSummaryStatistics(SummaryStatistics aggregateStatistics) {
            this.aggregateStatistics = aggregateStatistics;
        }

        /**
         * {@inheritDoc}.  This version adds the provided value to the configured
         * aggregate after adding it to these statistics.
         *
         * @see SummaryStatistics#addValue(double)
         */
        @Override
        public void addValue(double value) {
            super.addValue(value);
            aggregateStatistics.addValue(value);
        }
    }
}
