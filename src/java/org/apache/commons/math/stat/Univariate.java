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
 * @version $Revision: 1.10 $ $Date: 2003/10/13 08:10:56 $
*/
public interface Univariate extends Applyable{
    /**
     * A LEPTOKURTIC set has a positive kurtosis (a high peak) 
     */
    public static int LEPTOKURTIC = 1;
    /**
     * A MESOKURTIC set has a kurtosis of 0 - it is a normal distribution
     */
    public static int MESOKURTIC = 0;
    /**
     * A PLATYKURTIC set has a negative kurtosis (a flat "peak")
     */
    public static int PLATYKURTIC = -1;

    /**
     * Adds the value to the set of numbers
     * @param v the value to be added 
     */
    void addValue(double v);

    /** 
     * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
     * arithmetic mean </a> of the available values 
     * @return The mean or Double.NaN if no values have been added.
     */
    double getMean();

    /** 
     * Returns the <a href="http://www.xycoon.com/geometric_mean.htm">
     * geometric mean </a> of the available values
     * @return The geometricMean, Double.NaN if no values have been added, 
     * or if the productof the available values is less than or equal to 0.
     */
    double getGeometricMean();

    /** 
     * Returns the variance of the available values.
     * @return The variance, Double.NaN if no values have been added 
     * or 0.0 for a single value set.  
     */
    double getVariance();

    /** 
     * Returns the standard deviation of the available values.
     * @return The standard deviation, Double.NaN if no values have been added 
     * or 0.0 for a single value set. 
     */
    double getStandardDeviation();

    /**
     * Returns the skewness of the available values. Skewness is a 
     * measure of the assymetry of a given distribution.
     * @return The skewness, Double.NaN if no values have been added 
     * or 0.0 for a value set &lt;=2. 
     */
    double getSkewness();

    /**
     * Returns the Kurtosis of the available values. Kurtosis is a 
     * measure of the "peakedness" of a distribution
     * @return The kurtosis, Double.NaN if no values have been added, or 0.0 
     * for a value set &lt;=3. 
     */
    double getKurtosis();

    /**
     * Returns the Kurtosis "classification" a distribution can be 
     * leptokurtic (high peak), platykurtic (flat peak), 
     * or mesokurtic (zero kurtosis).  
     * 
     * @return A static constant defined in this interface, 
     *         StoredDeviation.LEPTOKURITC, StoredDeviation.PLATYKURTIC, or 
     *         StoredDeviation.MESOKURTIC
     */
    int getKurtosisClass();
    
    /** 
     * Returns the maximum of the available values
     * @return The max or Double.NaN if no values have been added.
     */
    double getMax();

    /** 
    * Returns the minimum of the available values
    * @return The min or Double.NaN if no values have been added.
    */
    double getMin();

    /** 
     * Returns the number of available values
     * @return The number of available values
     */
    int getN();

    /**
     * Returns the sum of the values that have been added to Univariate.
     * @return The sum or Double.NaN if no values have been added
     */
    double getSum();

    /**
     * Returns the sum of the squares of the available values.
     * @return The sum of the squares or Double.NaN if no 
     * values have been added.
     */
    double getSumsq();

    /** 
     * Resets all statistics and storage
     */
    void clear();

    /**
     * This constant signals that a Univariate implementation
     * takes into account the contributions of an infinite number of
     * elements.  In other words, if getWindow returns this
     * constant, there is, in effect, no "window".
     */
    static final int INFINITE_WINDOW = -1;

    /**
     * Univariate has the ability to return only measures for the
     * last N elements added to the set of values.
     * @return The current window size or -1 if its Infinite.
     */

    int getWindowSize();

    /**
     * WindowSize controls the number of values which contribute 
     * to the values returned by Univariate.  For example, if 
     * windowSize is set to 3 and the values {1,2,3,4,5} 
     * have been added <strong> in that order</strong> 
     * then the <i>available values</i> are {3,4,5} and all
     * reported statistics will be based on these values
     * @param windowSize sets the size of the window.
     */
    void setWindowSize(int windowSize);
}
