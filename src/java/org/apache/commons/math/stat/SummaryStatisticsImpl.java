/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
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
 *    nor may "Apache" appear in their name without prior written
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

import org.apache.commons.math.stat.univariate.moment.SecondMoment;
import org.apache.commons.math.stat.univariate.moment.FirstMoment;
import org.apache.commons.math.stat.univariate.moment.GeometricMean;
import org.apache.commons.math.stat.univariate.moment.Mean;
import org.apache.commons.math.stat.univariate.moment.Variance;
import org.apache.commons.math.stat.univariate.rank.Max;
import org.apache.commons.math.stat.univariate.rank.Min;
import org.apache.commons.math.stat.univariate.summary.Sum;
import org.apache.commons.math.stat.univariate.summary.SumOfLogs;
import org.apache.commons.math.stat.univariate.summary.SumOfSquares;

/**
 * Provides a default {@link SummaryStatistics} implementation.
 * 
 * @version $Revision: 1.2 $ $Date: 2004/01/27 02:43:44 $  
 */
public class SummaryStatisticsImpl extends SummaryStatistics {

    /** count of values that have been added */
    protected long n = 0;
    
    /** SecondMoment is used to compute the mean and variance */
    protected SecondMoment secondMoment = null;
    
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

    /**
     * Construct a SummaryStatistics
     */
    public SummaryStatisticsImpl() {
        sum = new Sum();
        sumsq = new SumOfSquares();
        min = new Min();
        max = new Max();
        sumLog = new SumOfLogs();
        geoMean = new GeometricMean();
        secondMoment = new SecondMoment();
    }

    /**
     * Add a value to the data
     * 
     * @param value  the value to add
     */
    public void addValue(double value) {
    	sum.increment(value);
    	sumsq.increment(value);
    	min.increment(value);
    	max.increment(value);
    	sumLog.increment(value);
    	geoMean.increment(value);
    	secondMoment.increment(value);
    	n++;
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getN()
     */
    public long getN() {
        return n;
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getSum()
     */
    public double getSum() {
        return sum.getResult();
    }

    /**
     * Returns the sum of the squares of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     * 
     * @return The sum of squares
     */
    public double getSumsq() {
        return sumsq.getResult();
    }

    /**
     * Returns the mean of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     * 
     * @return the mean
     */
    public double getMean() {
      return new Mean(secondMoment).getResult();
    }

    /**
     * Returns the standard deviation of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     * 
     * @return the standard deviation
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
     * Returns the variance of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the variance 
     */
    public double getVariance() {
        return new Variance(secondMoment).getResult();
    }

    /**
     * Returns the maximum of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the maximum  
     */
    public double getMax() {
        return max.getResult();
    }

    /**
     * Returns the minimum of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the minimum  
     */
    public double getMin() {
        return min.getResult();
    }

    /**
     * Returns the geometric mean of the values that have been added.
     * <p>
     *  Double.NaN is returned if no values have been added.</p>
     *
     * @return the geometric mean  
     */
    public double getGeometricMean() {
        return geoMean.getResult();
    }
    
    /**
     * Generates a text report displaying
     * summary statistics from values that
     * have been added.
     * @return String with line feeds displaying statistics
     */
    public String toString() {
        StringBuffer outBuffer = new StringBuffer();
        outBuffer.append("SummaryStatistics:\n");
        outBuffer.append("n: " + n + "\n");
        outBuffer.append("min: " + min + "\n");
        outBuffer.append("max: " + max + "\n");
        outBuffer.append("mean: " + getMean() + "\n");
        outBuffer.append("std dev: " + getStandardDeviation() + "\n");
        return outBuffer.toString();
    }

    /** 
	 * Resets all statistics and storage
	 */
    public void clear() {
        this.n = 0;
        min.clear();
        max.clear();
        sum.clear();
        sumLog.clear();
        sumsq.clear();
        geoMean.clear();
        secondMoment.clear();
    }

}