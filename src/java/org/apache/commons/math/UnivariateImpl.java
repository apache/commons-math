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
package org.apache.commons.math;

import java.io.Serializable;

/**
 *
 * Accumulates univariate statistics for values fed in 
 * through the addValue() method.  Does not store raw data values.
 * All data (including n) are represented internally as doubles.
 * Integers, floats and longs can be added, but will be converted
 * to doubles by addValue().  
 *
 * @author Phil Steitz
 * @version $Revision: 1.4 $ $Date: 2003/05/17 23:24:21 $
 * 
*/
public class UnivariateImpl implements Univariate, Serializable {

	/** hold the window size **/
	private int windowSize = Univariate.INIFINTE_WINDOW;
	
	/** Just in case, the windowSize is not inifinite, we need to
	 *   keep an array to remember values 0 to N
	 */
	private DoubleArray doubleArray;
	
    /** running sum of values that have been added */
    private double sum = 0.0;

    /** running sum of squares that have been added */
    private double sumsq = 0.0;

    /** count of values that have been added */
    private double n = 0.0;

    /** min of values that have been added */
    private double min = Double.MAX_VALUE;

    /** max of values that have been added */
    private double max = Double.MIN_VALUE;

    /** Creates new univariate */
    public UnivariateImpl() {
        clear();
    }
    
    /** Create a new univariate with a fixed window **/
    public UnivariateImpl(int window) {
    	windowSize = window;
   		doubleArray = new FixedDoubleArray( window );
     }

    /**
     * Adds the value, updating running sums.
     * @param v the value to be added 
     */
    public void addValue(double v) {

        insertValue(v);
    }

    /** 
     * Returns the mean of the values that have been added
     * @return mean value
     */
    public double getMean() {
        // FIXME: throw something meaningful if n = 0
        return sum/n;
    }

    /** 
     * Returns the variance of the values that have been added. 
     * @return The variance of a set of values.  Double.NaN is returned for
     * 	           an empty set of values and 0.0 is returned for a single value set.
     */
    public double getVariance() {
    	double variance = Double.NaN;
    	
    	if( n == 1 ) {
			variance = 0.0;
    	} else if( n > 1 ) {
			double xbar = getMean();
			variance =  (sumsq - xbar*xbar*n)/(n-1);
    	}
    	
    	return variance;
    }

    /** 
     * Returns the standard deviation of the values that have been added
     * @return The standard deviation of a set of values.  Double.NaN is returned for
     * 		       an empty set of values and 0.0 is returned for a single value set.
     */
    public double getStandardDeviation() {
        return (new Double(Math.sqrt
            ((new Double(getVariance())).doubleValue()))).doubleValue();
    }

    /**
     * Adds the value, updating running sums.
     * @param v the value to be added 
     */
    private void insertValue(double v) {
    	
    	if( windowSize != Univariate.INIFINTE_WINDOW ) {
    		if( windowSize == n ) {
				double discarded = doubleArray.addElementRolling( v );        	
			
				// Remove the influence of discarded value ONLY
				// if the discard value has any meaning.  In other words
				// don't discount until we "roll".
				if( windowSize > doubleArray.getNumElements() ) {
					// Remove the influence of the discarded
					sum -= discarded;
					sumsq -= discarded * discarded;
				}
			
				// Include the influence of the new
				// TODO: The next two lines seems rather expensive, but
				// I don't see many alternatives.			 
				min = doubleArray.getMin();
				max = doubleArray.getMax();
				sum += v;
				sumsq += v*v;
    		} else {
				doubleArray.addElement( v );        	
	        	n += 1.0;
    	    	if (v < min) min = v;
       			if (v > max) max = v;
        		sum += v;
        		sumsq += v*v;
    		}
    	} else {
			// If the windowSize is inifinite please don't take the time to
			// worry about storing any values.  We don't need to discard the
			// influence of any single item.
			n += 1.0;
			if (v < min) min = v;
			if (v > max) max = v;
			sum += v;
			sumsq += v*v;
    	}
    }

    /** Getter for property max.
     * @return Value of property max.
     */
    public double getMax() {
        return max;
    }

    /** Setter for property max.
     * @param max New value of property max.
     */
    public void setMax(double max) {
        this.max = max;
    }

    /** Getter for property min.
     * @return Value of property min.
     */
    public double getMin() {
        return min;
    }

    /** Getter for property n.
     * @return Value of property n.
     */
    public double getN() {
        return n;
    }

    /** Getter for property sum.
     * @return Value of property sum.
     */
    public double getSum() {
        return sum;
    }

    /** Getter for property sumsq.
     * @return Value of property sumsq.
     */
    public double getSumsq() {
        return sumsq;
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
        return outBuffer.toString();
    }
    
    /** Resets all sums to 0, resets min and max */
    public void clear() {
        this.sum = 0.0;
        this.sumsq = 0.0;
        this.n = 0.0;
        this.min = Double.MAX_VALUE;
        this.max = Double.MIN_VALUE;
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
		throw new RuntimeException( "A fixed window size must be set via the UnivariateImpl constructor");
	}

}
