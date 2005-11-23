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
package org.apache.commons.math.stat.descriptive.summary;

import java.io.Serializable;

import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;

/**
 * Returns the product of the available values.
 * <p>
 * If there are no values in the dataset, or any of the values are 
 * <code>NaN</code>, then <code>NaN</code> is returned.  
* <p>
 * <strong>Note that this implementation is not synchronized.</strong> If 
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or 
 * <code>clear()</code> method, it must be synchronized externally.
 * 
 * @version $Revision$ $Date$
 */
public class Product extends AbstractStorelessUnivariateStatistic implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 2824226005990582538L;   
     
    /**The number of values that have been added */
    private long n;
    
    /**
     * The current Running Product.
     */
    private double value;

    /**
     * Create a Product instance
     */
    public Product() {
        n = 0;
        value = Double.NaN;
    }
    
    /**
     * @see org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        if (n == 0) {
            value = d;
        } else {
            value *= d;
        }
        n++;
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return value;
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic#getN()
     */
    public long getN() {
        return n;
    }
    
    /**
     * @see org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        value = Double.NaN;
        n = 0;
    }

    /**
     * Returns the product of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     * <p>
     * Throws <code>IllegalArgumentException</code> if the array is null.
     * 
     * @param values the input array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return the product of the values or Double.NaN if length = 0
     * @throws IllegalArgumentException if the array is null or the array index
     *  parameters are not valid
     */
    public double evaluate(final double[] values, final int begin, final int length) {
        double product = Double.NaN;
        if (test(values, begin, length)) {
            product = 1.0;
            for (int i = begin; i < begin + length; i++) {
                product *= values[i];
            }
        }
        return product;
    }

}