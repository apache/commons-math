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
package org.apache.commons.math.stat.univariate;

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

/**
 * Abstract superclass for DescriptiveStatistics implementations. 
 * 
 * @version $Revision: 1.2 $ $Date: 2004/05/03 14:32:25 $
 */
public abstract class AbstractDescriptiveStatistics
    extends DescriptiveStatistics {
        
    /**
     * Create an AbstractDescriptiveStatistics
     */
    protected AbstractDescriptiveStatistics() {
        super();
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getSum()
     */
    public double getSum() {
    	return apply(new Sum());
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getSumsq()
     */
    public double getSumsq() {
    	return apply(new SumOfSquares());
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getMean()
     */
    public double getMean() {
    	return apply(new Mean());
    }

    /**
    * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getStandardDeviation()
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
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getVariance()
     */
    public double getVariance() {
    	return apply(new Variance());
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getSkewness()
     */
    public double getSkewness() {
    	return apply(new Skewness());
    }

    /**
      * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getKurtosis()
     */
    public double getKurtosis() {
    	return apply(new Kurtosis());
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getKurtosisClass()
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
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getMax()
     */
    public double getMax() {
    	return apply(new Max());
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getMin()
     */
    public double getMin() {
    	return apply(new Min());
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getGeometricMean()
     */
    public double getGeometricMean() {
    	return apply(new GeometricMean());
    }
    
    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getPercentile(double)
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
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getSortedValues()
     */
    public double[] getSortedValues() {
        double[] sort = getValues();
        Arrays.sort(sort);
        return sort;
    }
    
    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#addValue(double)
     */
    public abstract void addValue(double value);

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getValues()
     */
    public abstract double[] getValues();

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getElement(int)
     */
    public abstract double getElement(int index);
    
    /**
      * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#apply(UnivariateStatistic)
     */
    public abstract double apply(UnivariateStatistic stat);
    
}
