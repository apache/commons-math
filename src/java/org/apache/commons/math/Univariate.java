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

/**
 *
 * Accumulates univariate statistics for values fed in 
 * through the addValue() method.   This interface defines the LCD interface
 * which all Univariate implementations must implement.
 *
 * @author Phil Steitz
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 * @author Mark Diggory
 * @version $Revision: 1.5 $ $Date: 2003/05/21 17:59:19 $
 * 
*/
public interface Univariate {

    /**
     * Adds the value to the set of numbers
     * @param v the value to be added 
     */
    public abstract void addValue(double v);

    /** 
     * Returns the mean of the values that have been added
     * @return mean value
     */
    public abstract double getMean();

    /** 
     * Returns the geometric mean of the values that have been added
     * @return mean value
     */
    public abstract double getGeometricMean();

    /** 
     * Returns the product of all values that have been added
     * @return product of all values
     */
    public abstract double getProduct();

    /** 
     * Returns the variance of the values that have been added
     * @return variance value
     */
    public abstract double getVariance();

    /** 
     * Returns the standard deviation of the values that have been added
     * @return standard deviation value
     */
    public abstract double getStandardDeviation();

    /** Getter for property max.
     * @return Value of property max.
     */
    public abstract double getMax();

    /** Getter for property min.
     * @return Value of property min.
     */
    public abstract double getMin();

    /** Getter for property n.
     * @return Value of property n.
     */
    public abstract int getN();

    /** Getter for property sum.
     * @return Value of property sum.
     */
    public abstract double getSum();

    /** Getter for property sumsq.
     * @return Value of property sumsq.
     */
    public abstract double getSumsq();

    /** Resets all sums to 0, resets min and max */
    public abstract void clear();

    /**
     * This constant signals that a Univariate implementation
     * takes into account the contributions of an infinite number of
     * elements.  In other words, if getWindow returns this
     * constant, there is, in effect, no "window".
     */
    public static final int INIFINTE_WINDOW = -1;

    /**
     * Univariate has the ability to return only measures for the
     * last N elements added to the set of values.  This function returns
     */
    public abstract int getWindowSize();

    /**
     * Sets the window.  windowSize controls the number of value
     * which contribute to the values returned by Univariate.  
     * For example, a window value of 10 means that getMean()
     * will return the mean of the last 10 values added.
     */
    public abstract void setWindowSize(int windowSize);
}
