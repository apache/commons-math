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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
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

import org.apache.commons.math.stat.univariate.UnivariateStatistic;
import org.apache.commons.math.stat.univariate.moment.FourthMoment;
import org.apache.commons.math.stat.univariate.moment.GeometricMean;
import org.apache.commons.math.stat.univariate.moment.Kurtosis;
import org.apache.commons.math.stat.univariate.moment.Mean;
import org.apache.commons.math.stat.univariate.moment.Skewness;
import org.apache.commons.math.stat.univariate.moment.Variance;
import org.apache.commons.math.stat.univariate.rank.Max;
import org.apache.commons.math.stat.univariate.rank.Min;
import org.apache.commons.math.stat.univariate.summary.Sum;
import org.apache.commons.math.stat.univariate.summary.SumOfLogs;
import org.apache.commons.math.stat.univariate.summary.SumOfSquares;

/**
 * Provides univariate measures for an array of doubles.
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:10:56 $  
 */
public abstract class AbstractUnivariate implements Univariate {

    /** hold the window size **/
    protected int windowSize = Univariate.INFINITE_WINDOW;

    /** count of values that have been added */
    protected int n = 0;

    /** FourthMoment is used in calculating mean, variance,skew and kurtosis */
    protected FourthMoment moment = null;
    
    /** sum of values that have been added */
    protected Sum sum = null;

    /** sum of the square of each value that has been added */
    protected SumOfSquares sumsq = null;

    /** min of values that have been added */
    protected Min min = null;

    /** max of values that have been added */
    protected Max max = null;

    /** sumLog of values that have been added */
    protected SumOfLogs sumLog = null;

    /** geoMean of values that have been added */
    protected GeometricMean geoMean = null;

    /** mean of values that have been added */
    protected Mean mean = null;

    /** variance of values that have been added */
    protected Variance variance = null;

    /** skewness of values that have been added */
    protected Skewness skewness = null;

    /** kurtosis of values that have been added */
    protected Kurtosis kurtosis = null;

    /**
     * Construct an AbstractUnivariate
     */
    public AbstractUnivariate() {
        super();
        
        sum = new Sum();
        sumsq = new SumOfSquares();
        min = new Min();
        max = new Max();
        sumLog = new SumOfLogs();
        geoMean = new GeometricMean();

        moment = new FourthMoment();
        mean = new Mean(moment);
        variance = new Variance(moment);
        skewness = new Skewness(moment);
        kurtosis = new Kurtosis(moment);
    }

    /**
     * Construct an AbstractUnivariate with a window
     * @param window The Window Size
     */
    public AbstractUnivariate(int window) {
        this();
        setWindowSize(window);
    }

    /**
     * Apply the given statistic to this univariate collection.
     * @param stat the statistic to apply
     * @return the computed value of the statistic.
     */
    public abstract double apply(UnivariateStatistic stat);
    

    /**
     * If windowSize is set to Infinite, 
     * statistics are calculated using the following 
     * <a href="http://www.spss.com/tech/stat/Algorithms/11.5/descriptives.pdf">
     * recursive strategy
     * </a>.
     * @see org.apache.commons.math.stat.Univariate#addValue(double)
     */
    public abstract void addValue(double value);

    /**
     * @see org.apache.commons.math.stat.Univariate#getN()
     */
    public int getN() {
        return n;
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getSum()
     */
    public double getSum() {
        return apply(sum);
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getSumsq()
     */
    public double getSumsq() {
        return apply(sumsq);
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getMean()
     */
    public double getMean() {
        return apply(mean);
    }

    /**
     * Returns the standard deviation for this collection of values
     * @see org.apache.commons.math.stat.Univariate#getStandardDeviation()
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
     * Returns the variance of the values that have been added via West's
     * algorithm as described by
     * <a href="http://doi.acm.org/10.1145/359146.359152">Chan, T. F. and
     * J. G. Lewis 1979, <i>Communications of the ACM</i>,
     * vol. 22 no. 9, pp. 526-531.</a>.
     *
     * @return The variance of a set of values.  
     *         Double.NaN is returned for an empty 
     *         set of values and 0.0 is returned for 
     *         a &lt;= 1 value set.
     */
    public double getVariance() {
        return apply(variance);
    }

    /**
     * Returns the skewness of the values that have been added as described by
     * <a href="http://mathworld.wolfram.com/k-Statistic.html">
     * Equation (6) for k-Statistics</a>.
     * @return The skew of a set of values.  Double.NaN is returned for
     *         an empty set of values and 0.0 is returned for a 
     *         &lt;= 2 value set.
     */
    public double getSkewness() {
        return apply(skewness);
    }

    /**
     * Returns the kurtosis of the values that have been added as described by
     * <a href="http://mathworld.wolfram.com/k-Statistic.html">
     * Equation (7) for k-Statistics</a>.
     *
     * @return The kurtosis of a set of values.  Double.NaN is returned for
     *         an empty set of values and 0.0 is returned for a &lt;= 3 
     *         value set.
     */
    public double getKurtosis() {
        return apply(kurtosis);
    }

    /**
     * @see org.apache.commons.math.stat.StoreUnivariate#getKurtosisClass()
     */
    public int getKurtosisClass() {
        int kClass = Univariate.MESOKURTIC;

        double kurtosis = getKurtosis();
        if (kurtosis > 0) {
            kClass = Univariate.LEPTOKURTIC;
        } else if (kurtosis < 0) {
            kClass = Univariate.PLATYKURTIC;
        }
        return (kClass);
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getMax()
     */
    public double getMax() {
        return apply(max);
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getMin()
     */
    public double getMin() {
        return apply(min);
    }

    /**
    * @see org.apache.commons.math.stat.Univariate#getGeometricMean()
    */
    public double getGeometricMean() {
        return apply(geoMean);
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

    /**
     * @see org.apache.commons.math.stat.Univariate#clear()
     */
    public void clear() {
        this.n = 0;
        min.clear();
        max.clear();
        sum.clear();
        sumLog.clear();
        sumsq.clear();
        geoMean.clear();
        
        moment.clear();
        mean.clear();
        variance.clear();
        skewness.clear();
        kurtosis.clear();
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getWindowSize()
     */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#setWindowSize(int)
     */
    public void setWindowSize(int windowSize) {
        clear();
        this.windowSize = windowSize;
    }

}