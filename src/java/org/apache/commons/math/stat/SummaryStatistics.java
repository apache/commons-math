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

import java.io.Serializable;

import org.apache.commons.discovery.tools.DiscoverClass;

/**
 * Abstract factory class for univariate statistical summaries.
 * 
 * @version $Revision: 1.1 $ $Date: 2004/01/25 21:30:41 $
 */
public abstract class SummaryStatistics implements Serializable, StatisticalSummary{

	/**
	 * Create an instance of a <code>SummaryStatistics</code>
	 * @return a new factory. 
	 */
	public static SummaryStatistics newInstance(String cls) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return newInstance(Class.forName(cls));
	}
	/**
	 * Create an instance of a <code>DescriptiveStatistics</code>
	 * @return a new factory. 
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
				"org.apache.commons.math.stat.SummaryStatisticsImpl");
		} catch(Exception ex) {
			// ignore as default implementation will be used.
		}
		return factory;
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
