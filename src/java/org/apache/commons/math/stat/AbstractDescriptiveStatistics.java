/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Apache Software Foundation.  All rights
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

import java.util.Arrays;

import org.apache.commons.math.stat.univariate.moment.GeometricMean;
import org.apache.commons.math.stat.univariate.moment.Kurtosis;
import org.apache.commons.math.stat.univariate.moment.Mean;
import org.apache.commons.math.stat.univariate.moment.Skewness;
import org.apache.commons.math.stat.univariate.moment.Variance;
import org.apache.commons.math.stat.univariate.rank.Max;
import org.apache.commons.math.stat.univariate.rank.Min;
import org.apache.commons.math.stat.univariate.rank.Percentile;
import org.apache.commons.math.stat.univariate.summary.Sum;
import org.apache.commons.math.stat.univariate.summary.SumOfSquares;
import org.apache.commons.math.stat.univariate.UnivariateStatistic;

/**
 * Abstract superclass for DescriptiveStatistics implementations. 
 * 
 * @version $Revision: 1.3 $ $Date: 2004/01/25 21:30:41 $
 */
public abstract class AbstractDescriptiveStatistics
    extends DescriptiveStatistics {
        
    /**
     * Create an AbstractDescriptiveStatistics
     */
    public AbstractDescriptiveStatistics() {
    }

    /**
     * Create an AbstractDescriptiveStatistics with a specific Window
     * @param window WindowSIze for stat calculation
     */
    public AbstractDescriptiveStatistics(int window)  {
    	setWindowSize(window);
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getSum()
     */
    public double getSum() {
    	return apply(new Sum());
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getSumsq()
     */
    public double getSumsq() {
    	return apply(new SumOfSquares());
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getMean()
     */
    public double getMean() {
    	return apply(new Mean());
    }

    /**
    * @see org.apache.commons.math.stat.DescriptiveStatistics#getStandardDeviation()
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
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getVariance()
     */
    public double getVariance() {
    	return apply(new Variance());
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getSkewness()
     */
    public double getSkewness() {
    	return apply(new Skewness());
    }

    /**
      * @see org.apache.commons.math.stat.DescriptiveStatistics#getKurtosis()
     */
    public double getKurtosis() {
    	return apply(new Kurtosis());
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getKurtosisClass()
     */
    public int getKurtosisClass() {
    	int kClass = MESOKURTIC;

    	double kurtosis = getKurtosis();
    	if (kurtosis > 0) {
    		kClass = LEPTOKURTIC;
    	} else if (kurtosis < 0) {
    		kClass = PLATYKURTIC;
    	}
    	return (kClass);
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getMax()
     */
    public double getMax() {
    	return apply(new Max());
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getMin()
     */
    public double getMin() {
    	return apply(new Min());
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getGeometricMean()
     */
    public double getGeometricMean() {
    	return apply(new GeometricMean());
    }
    
    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getPercentile()
     */
    public double getPercentile(double p) {
    	return apply(new Percentile(p));
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
    	outBuffer.append("n: " + getN() + "\n");
    	outBuffer.append("min: " + getMin() + "\n");
    	outBuffer.append("max: " + getMax() + "\n");
    	outBuffer.append("mean: " + getMean() + "\n");
    	outBuffer.append("std dev: " + getStandardDeviation() + "\n");
    	outBuffer.append("skewness: " + getSkewness() + "\n");
    	outBuffer.append("kurtosis: " + getKurtosis() + "\n");
    	return outBuffer.toString();
    }
    
    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getSortedValues()
     */
    public double[] getSortedValues() {
        double[] sort = getValues();
        Arrays.sort(sort);
        return sort;
    }
    
    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#addValue(double)
     */
    public abstract void addValue(double value);

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getValues()
     */
    public abstract double[] getValues();

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getElement(int)
     */
    public abstract double getElement(int index);
    
    /**
      * @see org.apache.commons.math.stat.DescriptiveStatistics#apply(UnivariateStatistic)
     */
    public abstract double apply(UnivariateStatistic stat);
    
}
