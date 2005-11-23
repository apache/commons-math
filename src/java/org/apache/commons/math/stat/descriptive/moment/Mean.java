/*
 * Copyright 2003-2004 The Apache Software Foundation.
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
package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;

import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math.stat.descriptive.summary.Sum;

/**
 * Returns the arithmetic mean of the available values. Uses the definitional 
 * formula:
 * <p>
 * mean = sum(x_i) / n
 * <p>
 * where <code>n</code> is the number of observations.
 * <p>
 * The value of the statistic is computed using the following recursive
 * updating algorithm:
 * <p>
 * <ol>
 * <li>Initialize <code>m = </code> the first value</li>
 * <li>For each additional value, update using <br>
 *   <code>m = m + (new value - m) / (number of observations)</code></li>
 * </ol>
 * <p>
 *  Returns <code>Double.NaN</code> if the dataset is empty.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If 
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or 
 * <code>clear()</code> method, it must be synchronized externally.
 * 
 * @version $Revision$ $Date$
 */
public class Mean extends AbstractStorelessUnivariateStatistic 
    implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -1296043746617791564L;    
    
    /** First moment on which this statistic is based. */
    protected FirstMoment moment;

    /** 
     * Determines whether or not this statistic can be incremented or cleared.
     * <p>
     * Statistics based on (constructed from) external moments cannot
     * be incremented or cleared.
     */
    protected boolean incMoment;

    /** Constructs a Mean. */
    public Mean() {
        incMoment = true;
        moment = new FirstMoment();
    }

    /**
     * Constructs a Mean with an External Moment.
     * 
     * @param m1 the moment
     */
    public Mean(final FirstMoment m1) {
        this.moment = m1;
        incMoment = false;
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        if (incMoment) {
            moment.increment(d);
        }
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        if (incMoment) {
            moment.clear();
        }
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return moment.m1;
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic#getN()
     */
    public long getN() {
        return moment.getN();
    }

    /**
     * Returns the arithmetic mean of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     * <p>
     * Throws <code>IllegalArgumentException</code> if the array is null.
     * <p>
     * See {@link Mean} for details on the computing algorithm.
     * 
     * @param values the input array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return the mean of the values or Double.NaN if length = 0
     * @throws IllegalArgumentException if the array is null or the array index
     *  parameters are not valid
     */
    public double evaluate(final double[] values,final int begin, final int length) {
        if (test(values, begin, length)) {
            Sum sum = new Sum();
            return sum.evaluate(values, begin, length) / ((double) length);
        }
        return Double.NaN;
    }
}