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
 * StoreUnivariate implements the Univariate interface but maintains the set of values 
 * which contribute to the values being returned.  This implementation of Univariate
 * provides additional functionality such as skewness, kurtosis, and mode.  This additional
 * functionality comes with a price of increased storage costs.
 * 
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 */
public interface StoreUnivariate extends Univariate {

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
     * Returns the mode of the values that have been added.  The mode is
     * the element which occurs with the most frequency
     * @return the mode
     */
    public abstract double getMode();

    /** 
     * Returns the skewness of a given distribution.  Skewness is a 
     * measure of the assymetry of a given distribution. 
     * 
     * @return The skewness of this distribution
     */
    public abstract double getSkewness();

    /** 
     * Kurtosis is a measure of the "peakedness" of a distribution
     * 
     * @return the mode
     */
    public abstract double getKurtosis();

    /**
     * Returns the Kurtosis "classification" a distribution can be 
     * leptokurtic (high peak), platykurtic (flat peak), 
     * or mesokurtic (zero kurtosis).  
     * 
     * @return A static constant defined in this interface, 
     *         StoredDeviation.LEPTOKURITC, StoredDeviation.PLATYKURTIC, or 
     *         StoredDeviation.MESOKURTIC
     */
    public abstract int getKurtosisClass();

    /**
     * Returns the current set of values in an array of double primitives.  
     * The order of addition is preserved
     * 
     * @return returns the current set of numbers in the order in which they 
     *         were added to this set
     */
    public abstract double[] getValues();

    /**
     * Returns the element at the specified index
     * 
     * @return return the element at the specified index
     */
    public abstract double getElement(int index);

}
