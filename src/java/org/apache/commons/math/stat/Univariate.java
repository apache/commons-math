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

/**
 *
 * Accumulates univariate statistics for values fed in 
 * through the addValue() method. This interface defines the LCD interface
 * which all Univariate implementations must implement. <p>
 * A "rolling" capability is supported by all implementations with the following
 * contract: <p>
 * <i> Setting the windowSize property limits the domain of all statistics to
 * the last <code>windowSize</code> values added.</i><p>
 * We use the term <i>available values</i> throughout the API documentation
 * to refer to these values when the windowSize is set. For example, if the
 * windowSize is set to 3 and the values {1,2,3,4,5} have been added <strong>
 * in that order</strong> then the <i>available values</i> are {3,4,5} and all
 * reported statistics will be based on these values<p>
 * The default windowSize is "infinite" -- i.e., all values added are included
 * in all computations.
 *
 * @author Phil Steitz
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 * @author Mark Diggory
 * @version $Revision: 1.2 $ $Date: 2003/06/16 20:58:53 $
 * 
*/
public interface Univariate {

    /**
     * Adds the value to the set of numbers
     * @param v the value to be added 
     */
    abstract void addValue(double v);

    /** 
     * Returns the <a href=http://www.xycoon.com/arithmetic_mean.htm>
     * arithmetic mean </a> of the available values 
     * @return mean value, Double.NaN if no values have been added.
     */
    abstract double getMean();

    /** 
     * Returns the <a href=http://www.xycoon.com/geometric_mean.htm>
     * geometric mean </a> of the available values
     * @return The mean value, Double.NaN if no values have been added, or the product
     * of the available values is less than or equal to 0.
     */
    abstract double getGeometricMean();

    /** 
     * Returns the product of the available values
     * @return product of all values or Double.NaN if no values have been added.
     */
    abstract double getProduct();

    /** 
     * Returns the variance of the available values.
     * @return The variance of a set of values, an empty set of values and 0.0 is 
     * returned for a single value set, or Double.NaN if no values have been added.  
     */
    abstract double getVariance();

    /** 
     * Returns the variance of the available values.
     * @return standard deviation value, Double.NaN is returned for an empty set 
     * of values and 0.0 is returned for a single value set. 
     */
    abstract double getStandardDeviation();

	/**
     * Returns the skewness of a given distribution.  Skewness is a 
     * measure of the assymetry of a given distribution.
	 * @return skewness, Double.NaN is returned for an empty set of values 
     * and 0.0 is returned for a value set &lt;=2. 
	 */
	abstract double getSkewness();
	
	/**
     * Returns the Kurtosis of the available values. Kurtosis is a 
     * measure of the "peakedness" of a distribution
     * @return kurtosis, Double.NaN is returned in no values have been added, and 0.0 is 
     * returned for a value set &lt;=3. 
	 */
	abstract double getKurtosis();
		
    /** 
     * Returns the maximum of the available values
     * @return Value of property max, Double.NaN is returned in no values have been added.
     */
    abstract double getMax();

     /** 
     * Returns the minimum of the available values
     * @return Value of property min, Double.NaN is returned in no values have been added.
     */
    abstract double getMin();

    /** 
     * Returns the number of available values
     * @return the number of available values
     */
    abstract int getN();

    /**
     * Returns the sum of the values that have been added to Univariate.
     * @return the sum of the available values or Double.NaN if no values have been added
     */
    abstract double getSum();

    /**
     * Returns the sum of the squares of the available values.
     * Returns 0 if no values have been added.
     * @return the sum of the squares of the available values.
     */
    abstract double getSumsq();

    /** Resets all statistics */
    abstract void clear();

    /**
     * This constant signals that a Univariate implementation
     * takes into account the contributions of an infinite number of
     * elements.  In other words, if getWindow returns this
     * constant, there is, in effect, no "window".
     */
    static final int INFINITE_WINDOW = -1;

    /**
     * Univariate has the ability to return only measures for the
     * last N elements added to the set of values.  This function returns
     */
    abstract int getWindowSize();

    /**
     * Sets the window.  windowSize controls the number of value
     * which contribute to the values returned by Univariate.  
     * For example, a window value of 10 means that getMean()
     * will return the mean of the last 10 values added.
     */
    abstract void setWindowSize(int windowSize);
}
