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

import org.apache.commons.math.DoubleArray;
import org.apache.commons.math.FixedDoubleArray;

/**
 *
 * Accumulates univariate statistics for values fed in 
 * through the addValue() method.  Does not store raw data values.
 * All data are represented internally as doubles.
 * Integers, floats and longs can be added, but will be converted
 * to doubles by addValue().  
 *
 * @author Phil Steitz
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 * @author <a href="mailto:mdiggory@apache.org">Mark Diggory</a>
 * @author Brent Worden
 * @version $Revision: 1.4 $ $Date: 2003/06/14 04:17:49 $
 * 
*/
public class UnivariateImpl implements Univariate, Serializable {

    /** hold the window size **/
    private int windowSize = Univariate.INFINITE_WINDOW;

    /** Just in case, the windowSize is not inifinite, we need to
     *  keep an array to remember values 0 to N
     */
    private DoubleArray doubleArray;

    /** running sum of values that have been added */
    private double sum = 0.0;

    /** running sum of squares that have been added */
    private double sumsq = 0.0;

    /** running sum of 3rd powers that have been added */
    private double sumCube = 0.0;
    
    /** running sum of 4th powers that have been added */
    private double sumQuad = 0.0;
    
    /** count of values that have been added */
    private int n = 0;

    /** min of values that have been added */
    private double min = Double.MAX_VALUE;

    /** max of values that have been added */
    private double max = Double.MIN_VALUE;

    /** product of values that have been added */
    private double product = Double.NaN;

    /** Creates new univariate with an inifinite window */
    public UnivariateImpl() {
        clear();
    }
    
    /** Creates a new univariate with a fixed window **/
    public UnivariateImpl(int window) {
        windowSize = window;
        doubleArray = new FixedDoubleArray( window );
    }

     
    /**
     * @see org.apache.commons.math.stat.Univariate#addValue(double)
     */
    public void addValue(double v) {
        insertValue(v);
    }

    
    /**
     * @see org.apache.commons.math.stat.Univariate#getMean()
     */
    public double getMean() {
        if (n == 0) {
            return Double.NaN;
        } else {
            return (sum / (double) n );
        }
     }

     
    /**
     * @see org.apache.commons.math.stat.Univariate#getGeometricMean()
     */
    public double getGeometricMean() {
        if ((product <= 0.0) || (n == 0)) {
            return Double.NaN; 
        } else {
            return Math.pow(product,( 1.0 / (double) n ) );
        }
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getProduct()
     */
    public double getProduct() {
        return product;
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getStandardDeviation()
     */
    public double getStandardDeviation() {
        double variance = getVariance();
        if ((variance == 0.0) || (variance == Double.NaN)) {
            return variance;
        } else {
            return Math.sqrt(variance);
        }
    }
    
    /**
     * Returns the variance of the values that have been added as described by
     * <a href="http://mathworld.wolfram.com/k-Statistic.html">Equation (5) for k-Statistics</a>.
     * 
     * @return The variance of a set of values.  Double.NaN is returned for
     *         an empty set of values and 0.0 is returned for a &lt;= 1 value set.
     */
    public double getVariance() {
        double variance = Double.NaN;

        if( n == 1 ) {
            variance = 0.0;
        } else if( n > 1 ) {
            variance = (((double) n) * sumsq - (sum * sum)) / (double) (n * (n - 1));    
        }

        return variance < 0 ? 0.0 : variance;
    }
     
    /**
     * Returns the skewness of the values that have been added as described by
     * <a href="http://mathworld.wolfram.com/k-Statistic.html">Equation (6) for k-Statistics</a>.
     * 
     * @return The skew of a set of values.  Double.NaN is returned for
     *         an empty set of values and 0.0 is returned for a &lt;= 2 value set.
     */
    public double getSkewness() {
        
        if( n < 1) return Double.NaN;
        if( n <= 2 ) return 0.0;                  
            
        return ( 2 * Math.pow(sum, 3) - 3 * sum * sumsq + ((double) (n * n)) * sumCube ) / 
               ( (double) (n * (n - 1) * (n - 2)) ) ;  
    }
    
    /**
     * Returns the kurtosis of the values that have been added as described by
     * <a href="http://mathworld.wolfram.com/k-Statistic.html">Equation (7) for k-Statistics</a>.
     * 
     * @return The kurtosis of a set of values.  Double.NaN is returned for
     *         an empty set of values and 0.0 is returned for a &lt;= 3 value set.
     */
    public double getKurtosis() {
        
        if( n < 1) return Double.NaN;
        if( n <= 3 ) return 0.0;
        
        double x1 = -6 * Math.pow(sum, 4);
        double x2 = 12 * ((double) n) * Math.pow(sum, 2) * sumsq;
        double x3 = -3 * ((double) (n * (n - 1))) * Math.pow(sumsq,2);
        double x4 = -4 * ((double) (n * (n + 1))) * sum * sumCube;
        double x5 = Math.pow(((double) n),2) * ((double) (n+1)) * sumQuad;
        
        return (x1 + x2 + x3 + x4 + x5) / 
               ( (double) (n * (n - 1) * (n - 2) * (n - 3)) );
    } 
    
    /**
     * Called in "addValue" to insert a new value into the statistic.
     * @param v The value to be added.
     */
    private void insertValue(double v) {

        // The default value of product is NaN, if you
        // try to retrieve the product for a univariate with
        // no values, we return NaN.
        //
        // If this is the first call to insertValue, we want
        // to set product to 1.0, so that our first element
        // is not "cancelled" out by the NaN.
        if( n == 0 ) {
            product = 1.0;
        }

        if( windowSize != Univariate.INFINITE_WINDOW ) {
            if( windowSize == n ) {
                double discarded = doubleArray.addElementRolling( v );

                // Remove the influence of the discarded
                sum -= discarded;
                sumsq -= discarded * discarded;
                sumCube -= Math.pow(discarded, 3);
                sumQuad -= Math.pow(discarded, 4); 
                
                if(discarded == min) {
                    min = doubleArray.getMin();
                } else if(discarded == max){
                    max = doubleArray.getMax();
                } 
                
                if(product != 0.0){
                    // can safely remove discarded value
                    product *=  v / discarded;
                } else if(discarded == 0.0){
                    // need to recompute product
                    product = 1.0;
                    double[] elements = doubleArray.getElements();
                    for( int i = 0; i < elements.length; i++ ) {
                        product *= elements[i];
                    }
                } // else product = 0 and will still be 0 after discard

            } else {
                doubleArray.addElement( v );            
                n += 1.0;
                if (v < min) {
                    min = v;
                }
                if (v > max) {
                    max = v;
                }
                product *= v;
            }
        } else {
            // If the windowSize is inifinite please don't take the time to
            // worry about storing any values.  We don't need to discard the
            // influence of any single item.
            n += 1.0;
            if (v < min) {
                min = v;
            } 
            if (v > max) {
                max = v;
            } 
            product *= v;
        }
        
        sum += v;
        sumsq += v * v;
        sumCube += Math.pow(v,3);
        sumQuad += Math.pow(v,4);
    }

    /** Getter for property max.
     * @return Value of property max.
     */
    public double getMax() {
        if (n == 0) { 
            return Double.NaN;
        } else {
            return max;
        }
    }

    /** Getter for property min.
     * @return Value of property min.
     */
    public double getMin() {
        if (n == 0) { 
            return Double.NaN;
        } else {
            return min;
        }
    }

    /** Getter for property n.
     * @return Value of property n.
     */
    public int getN() {
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

    /** Getter for property sumCube.
     * @return Value of property sumCube.
     */
    public double getSumCube() {
        return sumCube;
    }
    
    /** Getter for property sumQuad.
     * @return Value of property sumQuad.
     */
    public double getSumQuad() {
        return sumQuad;
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
     * Resets all sums to 0, resets min and max 
     */
    public void clear() {
        this.sum = this.sumsq = this.sumCube = this.sumQuad = 0.0;
        this.n = 0;
        this.min = Double.MAX_VALUE;
        this.max = Double.MIN_VALUE;
        this.product = Double.NaN;
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
        String msg = "A fixed window size must be set via the " +
            "UnivariateImpl constructor";
        throw new RuntimeException( msg );
    }
}
