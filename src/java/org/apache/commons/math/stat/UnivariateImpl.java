/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.stat;

import java.io.Serializable;

import org.apache.commons.math.util.DoubleArray;
import org.apache.commons.math.util.FixedDoubleArray;

/**
 *
 * Accumulates univariate statistics for values fed in
 * through the addValue() method.  Does not store raw data values.
 * All data are represented internally as doubles.
 * Integers, floats and longs can be added, but they will be converted
 * to doubles by addValue().
 *
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 * @author Brent Worden
 * @version $Revision: 1.17 $ $Date: 2003/07/07 23:25:13 $
 *
*/
public class UnivariateImpl implements Univariate, Serializable {

    /** hold the window size **/
    private int windowSize = Univariate.INFINITE_WINDOW;

    /** Just in case the windowSize is not infinite, we need to
     *  keep an array to remember values 0 to N
     */
    private DoubleArray doubleArray;

    /** count of values that have been added */
    private int n = 0;

    /** sum of values that have been added */
    private double sum = Double.NaN;

    /** sum of the square of each value that has been added */
    private double sumsq = Double.NaN;

    /** min of values that have been added */
    private double min = Double.NaN;

    /** max of values that have been added */
    private double max = Double.NaN;

    /** sumLog of values that have been added */
    private double sumLog = Double.NaN;

    /** mean of values that have been added */
    private double mean = Double.NaN;

    /** second moment of values that have been added */
    private double m2 = Double.NaN;

    /** third moment of values that have been added */
    private double m3 = Double.NaN;

    /** fourth moment of values that have been added */
    private double m4 = Double.NaN;

    /** variance of values that have been added */
    private double variance = Double.NaN;

    /** skewness of values that have been added */
    private double skewness = Double.NaN;

    /** kurtosis of values that have been added */
    private double kurtosis = Double.NaN;

    /** Creates new univariate with an infinite window */
    public UnivariateImpl() {
    }

    /** Creates a new univariate with a fixed window **/
    public UnivariateImpl(int window) {
        setWindowSize(window);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.Univariate#getN()
     */
    public int getN() {
        return n;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.Univariate#getSum()
     */
    public double getSum() {
        if (windowSize != Univariate.INFINITE_WINDOW) {
            return StatUtils.sum(doubleArray.getElements());
        }

        return sum;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.Univariate#getSumsq()
     */
    public double getSumsq() {
        if (windowSize != Univariate.INFINITE_WINDOW) {
            return StatUtils.sumSq(doubleArray.getElements());
        }

        return sumsq;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.Univariate#getMean()
     */
    public double getMean() {
        if (windowSize != Univariate.INFINITE_WINDOW) {
            return StatUtils.mean(doubleArray.getElements());
        }

        return mean;
    }

    /**
     * Returns the standard deviation for this collection of values
     * @see org.apache.commons.math.stat.Univariate#getStandardDeviation()
     */
    public double getStandardDeviation() {
        double stdDev = Double.NaN;
        if (getN() != 0) {
            stdDev = Math.sqrt(getVariance());
        }
        return (stdDev);
    }

    /**
     * Returns the variance of the values that have been added via West's
     * algorithm as described by
     * <a href="http://doi.acm.org/10.1145/359146.359152">Chan, T. F. and
     * J. G. Lewis 1979, <i>Communications of the ACM</i>,
     * vol. 22 no. 9, pp. 526-531.</a>.
     *
     * @return The variance of a set of values.  Double.NaN is returned for
     *         an empty set of values and 0.0 is returned for a &lt;= 1 value set.
     */
    public double getVariance() {
        if (windowSize != Univariate.INFINITE_WINDOW) {
            variance = StatUtils.variance(doubleArray.getElements());
        }
        return variance;
    }

    /**
     * Returns the skewness of the values that have been added as described by
     * <a href="http://mathworld.wolfram.com/k-Statistic.html">Equation (6) for k-Statistics</a>.
     *
     * @return The skew of a set of values.  Double.NaN is returned for
     *         an empty set of values and 0.0 is returned for a &lt;= 2 value set.
     */
    public double getSkewness() {
        if (windowSize != Univariate.INFINITE_WINDOW) {
            return StatUtils.skewness(doubleArray.getElements());
        }
        return skewness;
    }

    /**
     * Returns the kurtosis of the values that have been added as described by
     * <a href="http://mathworld.wolfram.com/k-Statistic.html">Equation (7) for k-Statistics</a>.
     *
     * @return The kurtosis of a set of values.  Double.NaN is returned for
     *         an empty set of values and 0.0 is returned for a &lt;= 3 value set.
     */
    public double getKurtosis() {
        if (windowSize != Univariate.INFINITE_WINDOW) {
            return StatUtils.kurtosis(doubleArray.getElements());
        }
        return kurtosis;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.Univariate#getMax()
     */
    public double getMax() {
        if (windowSize != Univariate.INFINITE_WINDOW) {
            return StatUtils.max(doubleArray.getElements());
        }
        return max;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.Univariate#getMin()
     */
    public double getMin() {
        if (windowSize != Univariate.INFINITE_WINDOW) {
            return StatUtils.min(doubleArray.getElements());
        }
        return min;
    }

    /* (non-Javadoc)
    * @see org.apache.commons.math.stat.Univariate#getGeometricMean()
    */
    public double getGeometricMean() {

        if (windowSize != Univariate.INFINITE_WINDOW) {
            return StatUtils.geometricMean(doubleArray.getElements());
        }

        if (n == 0) {
            return Double.NaN;
        } else {
            return Math.exp(sumLog / (double) n);
        }
    }

    /* If windowSize is set to Infinite, moments are calculated using the following 
     * <a href="http://www.spss.com/tech/stat/Algorithms/11.5/descriptives.pdf">
     * recursive strategy
     * </a>.
     * Otherwise, stat methods delegate to StatUtils.
     * @see org.apache.commons.math.stat.Univariate#addValue(double)
     */
    public void addValue(double value) {

        if (windowSize != Univariate.INFINITE_WINDOW) {
            /* then all getters deligate to StatUtils
             * and this clause simply adds/rolls a value in the storage array 
             */
            if (windowSize == n) {
                doubleArray.addElementRolling(value);
            } else {
                n++;
                doubleArray.addElement(value);
            }

        } else {
            /* If the windowSize is infinite don't store any values and there 
             * is no need to discard the influence of any single item.
             */
            n++;

            if (n <= 1) {
                /* if n <= 1, initialize the sumLog, min, max, mean, variance and pre-variance */
                sumLog = 0.0;
                sum = min = max = mean = value;
                sumsq = value * value;
                variance = m2 = 0.0;
                skewness = kurtosis = 0.0;
                m2 = m3 = m4 = 0.0;
            } else {
                /* otherwise calc these values */
                sumLog += Math.log(value);
                sum += value;
                sumsq += value * value;
                min = Math.min(min, value);
                max = Math.max(max, value);

                double dev = value - mean;
                double v = dev / ((double) n);
                double v2 = v * v;

                double n0 = (double) n;
                double n1 = (double) (n - 1);
                double n2 = (double) (n - 2);
                double n3 = (double) (n - 3);

                m4 =
                    m4
                        - (4.0 * v * m3)
                        + (6.0 * v2 * m2)
                        + ((n0 * n0) - 3 * n1) * (v2 * v2 * n1 * n0);

                m3 = m3 - (3.0 * v * m2) + (n0 * n1 * n2 * v2 * v);

                m2 += n1 * dev * v;

                mean += v;

                variance = (n <= 1) ? 0.0 : m2 / n1;

                skewness =
                    (n <= 2 || variance < 10E-20)
                        ? 0.0
                        : (n0 * m3) / (n1 * n2 * Math.sqrt(variance) * variance);

                kurtosis =
                    (n <= 3 || variance < 10E-20)
                        ? 0.0
                        : (n0 * (n0 + 1) * m4 - 3 * m2 * m2 * n1)
                            / (n1 * n2 * n3 * variance * variance);
            }
        }
    }

    /**
     * Generates a text report displaying
     * univariate statistics from values that
     * have been added.
     * @return String with line feeds displaying statistics
     */
    public String toString() {
        StringBuffer outBuffer = new StringBuffer();
        outBuffer.append("UnivariateImpl:\n");
        outBuffer.append("n: " + n + "\n");
        outBuffer.append("min: " + min + "\n");
        outBuffer.append("max: " + max + "\n");
        outBuffer.append("mean: " + getMean() + "\n");
        outBuffer.append("std dev: " + getStandardDeviation() + "\n");
        outBuffer.append("skewness: " + getSkewness() + "\n");
        outBuffer.append("kurtosis: " + getKurtosis() + "\n");
        return outBuffer.toString();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.Univariate#clear()
     */
    public void clear() {
        this.n = 0;
        this.min = this.max = Double.NaN;
        this.sumLog = this.mean = Double.NaN;
        this.variance = this.skewness = this.kurtosis = Double.NaN;
        this.m2 = this.m3 = this.m4 = Double.NaN;
        if (doubleArray != null)
            doubleArray = new FixedDoubleArray(windowSize);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.Univariate#getWindowSize()
     */
    public int getWindowSize() {
        return windowSize;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.Univariate#setWindowSize(int)
     */
    public void setWindowSize(int windowSize) {
        clear();
        this.windowSize = windowSize;
        doubleArray = new FixedDoubleArray(windowSize);
    }

}