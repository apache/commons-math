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
package org.apache.commons.math.stat.univariate.moment;

import java.io.Serializable;

import org.apache.commons.math.stat.univariate.AbstractStorelessUnivariateStatistic;

/**
 * Computes the first moment (arithmetic mean).  Uses the definitional formula:
 * <p>
 * mean = sum(x_i) / n
 * <p>
 * where <code>n</code> is the number of observations.
 * <p>
 * To limit numeric errors, the value of the statistic is computed using the
 * following recursive updating algorithm:
 * <p>
 * <ol>
 * <li>Initialize <code>m = </code> the first value</li>
 * <li>For each additional value, update using <br>
 *   <code>m = m + (new value - m) / (number of observations)</code></li>
 * </ol>
 * <p>
 *  Returns <code>Double.NaN</code> if the dataset is empty.
 *
 * @version $Revision: 1.17 $ $Date: 2004/06/29 02:14:17 $
 */
public class FirstMoment extends AbstractStorelessUnivariateStatistic implements Serializable{

    /** Serializable version identifier */
    static final long serialVersionUID = -803343206421984070L; 
    
    /** Count of values that have been added */
    protected long n = 0;

    /** First moment of values that have been added */
    protected double m1 = Double.NaN;
    
    /** 
     * Deviation of most recently added value from previous first moment.
     * Retained to prevent repeated computation in higher order moments.
     */
    protected double dev = Double.NaN;
    
    /**
     * Deviation of most recently added value from previous first moment,
     * normalized by previous sample size.  Retained to prevent repeated
     * computation in higher order moments
     */
    protected double nDev = Double.NaN;

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        if (n == 0) {
            m1 = 0.0;
        }
        n++;
        double n0 = (double) n;
        dev = d - m1;
        nDev = dev / n0;
        m1 += nDev;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        m1 = Double.NaN;
        n = 0;
        dev = Double.NaN;
        nDev = Double.NaN;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return m1;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getN()
     */
    public double getN() {
        return n;
    }

}