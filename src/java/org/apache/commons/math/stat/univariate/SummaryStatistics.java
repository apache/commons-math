/*
 * Copyright 2004 The Apache Software Foundation.
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

import java.io.Serializable;

import org.apache.commons.discovery.tools.DiscoverClass;

/**
 * Abstract factory class for univariate statistical summaries.
 * 
 * @version $Revision: 1.4 $ $Date: 2004/05/18 04:19:53 $
 */
public abstract class SummaryStatistics implements Serializable, StatisticalSummary {

	/**
	 * Create an instance of a <code>SummaryStatistics</code>
     * @param cls the type of <code>SummaryStatistics</code> object to
     *        create. 
     * @return a new factory. 
     * @exception InstantiationException is thrown if the object can not be
     *            created.
     * @exception IllegalAccessException is thrown if the type's default
     *            constructor is not accessible.
     * @exception ClassNotFoundException if the named
     *            <code>SummaryStatistics</code> type can not be found.
	 */
	public static SummaryStatistics newInstance(String cls) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return newInstance(Class.forName(cls));
	}
     
	/**
	 * Create an instance of a <code>DescriptiveStatistics</code>
     * @param cls the type of <code>SummaryStatistics</code> object to
     *        create. 
     * @return a new factory. 
     * @exception InstantiationException is thrown if the object can not be
     *            created.
     * @exception IllegalAccessException is thrown if the type's default
     *            constructor is not accessible.
	 */
	public static SummaryStatistics newInstance(Class cls) throws InstantiationException, IllegalAccessException {
		return (SummaryStatistics)cls.newInstance();
	}
	
	/**
	 * Create an instance of a <code>DescriptiveStatistics</code>
	 * @return a new factory. 
	 */
	public static SummaryStatistics newInstance() {
		SummaryStatistics factory = null;
		try {
			DiscoverClass dc = new DiscoverClass();
			factory = (SummaryStatistics) dc.newInstance(
				SummaryStatistics.class,
				"org.apache.commons.math.stat.univariate.SummaryStatisticsImpl");
		} catch(Exception ex) {
			// ignore as default implementation will be used.
		}
		return factory;
	}
	

	/**
	 * Return a StatisticalSummaryValues instance reporting current statistics.
	 * 
	 * @return Current values of statistics 
	 */
	public StatisticalSummary getSummary() {
	    return new StatisticalSummaryValues(getMean(), getVariance(), getN(),
	            getMax(), getMin(), getSum());
	}
	
	/**
	 * Adds the value to the data to be summarized
	 * @param v the value to be added 
	 */
	public abstract void addValue(double v);

	/** 
	 * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
	 * arithmetic mean </a> of the available values 
	 * @return The mean or Double.NaN if no values have been added.
	 */
	public abstract double getMean();

	/** 
	 * Returns the <a href="http://www.xycoon.com/geometric_mean.htm">
	 * geometric mean </a> of the available values
	 * @return The geometricMean, Double.NaN if no values have been added, 
	 * or if the productof the available values is less than or equal to 0.
	 */
	public abstract double getGeometricMean();

	/** 
	 * Returns the variance of the available values.
	 * @return The variance, Double.NaN if no values have been added 
	 * or 0.0 for a single value set.  
	 */
	public abstract double getVariance();

	/** 
	 * Returns the standard deviation of the available values.
	 * @return The standard deviation, Double.NaN if no values have been added 
	 * or 0.0 for a single value set. 
	 */
	public abstract double getStandardDeviation();
    
	/** 
	 * Returns the maximum of the available values
	 * @return The max or Double.NaN if no values have been added.
	 */
	public abstract double getMax();

	/** 
	* Returns the minimum of the available values
	* @return The min or Double.NaN if no values have been added.
	*/
	public abstract double getMin();

	/** 
	 * Returns the number of available values
	 * @return The number of available values
	 */
	public abstract long getN();

	/**
	 * Returns the sum of the values that have been added to Univariate.
	 * @return The sum or Double.NaN if no values have been added
	 */
	public abstract double getSum();

	/**
	 * Returns the sum of the squares of the available values.
	 * @return The sum of the squares or Double.NaN if no 
	 * values have been added.
	 */
	public abstract double getSumsq();

	/** 
	 * Resets all statistics
	 */
	public abstract void clear();

}
