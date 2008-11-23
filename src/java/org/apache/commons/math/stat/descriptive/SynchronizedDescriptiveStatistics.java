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
 * {@link org.apache.commons.math.stat.descriptive.DescriptiveStatistics} that
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
public class SynchronizedDescriptiveStatistics extends DescriptiveStatistics {

    /** Serialization UID */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an instance with infinite window
     */
    public SynchronizedDescriptiveStatistics() {
        this(INFINITE_WINDOW);
    }

    /**
     * Construct an instance with finite window
     * @param window the finite window size.
     */
    public SynchronizedDescriptiveStatistics(int window) {
        super(window);
    }
    
    /**
     * A copy constructor. Creates a deep-copy of the {@code original}.
     * 
     * @param original the {@code SynchronizedDescriptiveStatistics} instance to copy
     */
    public SynchronizedDescriptiveStatistics(SynchronizedDescriptiveStatistics original) {
        copy(original, this);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addValue(double v) {
        super.addValue(v);
    }

    /**
     * Apply the given statistic to this univariate collection.
     * @param stat the statistic to apply
     * @return the computed value of the statistic.
     */
    public synchronized double apply(UnivariateStatistic stat) {
        return super.apply(stat);
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
    public synchronized double getElement(int index) {
        return super.getElement(index);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized long getN() {
        return super.getN();
    }

    /** 
     * Returns the standard deviation of the available values.
     * @return The standard deviation, Double.NaN if no values have been added 
     * or 0.0 for a single value set. 
     */
    public synchronized double getStandardDeviation() {
        return super.getStandardDeviation();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double[] getValues() {
        return super.getValues();
    }

    /**
     * Access the window size.
     * @return the current window size.
     */
    public synchronized int getWindowSize() {
        return super.getWindowSize();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setWindowSize(int windowSize) {
        super.setWindowSize(windowSize);
    }

    /**
     * Generates a text report displaying univariate statistics from values
     * that have been added.  Each statistic is displayed on a separate
     * line.
     * 
     * @return String with line feeds displaying statistics
     */
    public synchronized String toString() {
        return super.toString();
    }
    
    /**
     * Returns a copy of this SynchronizedDescriptiveStatistics instance with the
     * same internal state.
     * 
     * @return a copy of this
     */
    public synchronized SynchronizedDescriptiveStatistics copy() {
        SynchronizedDescriptiveStatistics result = 
            new SynchronizedDescriptiveStatistics();
        copy(this, result);
        return result; 
    }
     
    /**
     * Copies source to dest.
     * <p>Neither source nor dest can be null.</p>
     * <p>Acquires synchronization lock on source, then dest before copying.</p>
     * 
     * @param source SynchronizedDescriptiveStatistics to copy
     * @param dest SynchronizedDescriptiveStatistics to copy to
     * @throws NullPointerException if either source or dest is null
     */
    public static void copy(SynchronizedDescriptiveStatistics source,
            SynchronizedDescriptiveStatistics dest) {
        synchronized (source) {
            synchronized (dest) {
                DescriptiveStatistics.copy(source, dest);
            }
        }
    }
}
