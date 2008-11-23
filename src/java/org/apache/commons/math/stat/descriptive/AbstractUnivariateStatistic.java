/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;

/**
 * Abstract base class for all implementations of the 
 * {@link UnivariateStatistic} interface.
 * <p>
 * Provides a default implementation of <code>evaluate(double[]),</code> 
 * delegating to <code>evaluate(double[], int, int)</code> in the natural way.
 * </p>
 * <p>
 * Also includes a <code>test</code> method that performs generic parameter
 * validation for the <code>evaluate</code> methods.</p>
 * 
 * @version $Revision$ $Date$
 */
public abstract class AbstractUnivariateStatistic
    implements UnivariateStatistic, Serializable {
    
    /** Serialization UID */
    private static final long serialVersionUID = -8007759382851708045L;

    /**
     * {@inheritDoc}
     */
    public double evaluate(final double[] values) {
        test(values, 0, 0);
        return evaluate(values, 0, values.length);
    }

    /**
     * {@inheritDoc}
     */
    public abstract double evaluate(final double[] values, final int begin, final int length);
    
    /**
     * {@inheritDoc}
     */
    public abstract UnivariateStatistic copy();

    /**
     * This method is used by <code>evaluate(double[], int, int)</code> methods
     * to verify that the input parameters designate a subarray of positive length.
     * <p>
     * <ul>
     * <li>returns <code>true</code> iff the parameters designate a subarray of 
     * positive length</li>
     * <li>throws <code>IllegalArgumentException</code> if the array is null or
     * or the indices are invalid</li>
     * <li>returns <code>false</li> if the array is non-null, but 
     * <code>length</code> is 0.
     * </ul></p>
     *
     * @param values the input array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return true if the parameters are valid and designate a subarray of positive length
     * @throws IllegalArgumentException if the indices are invalid or the array is null
     */
    protected boolean test(
        final double[] values,
        final int begin,
        final int length) {

        if (values == null) {
            throw new IllegalArgumentException("input value array is null");
        }
        
        if (begin < 0) {
            throw new IllegalArgumentException("start position cannot be negative");
        }
        
        if (length < 0) {
            throw new IllegalArgumentException("length cannot be negative");
        }
        
        if (begin + length > values.length) {
            throw new IllegalArgumentException(
                "begin + length > values.length");
        }

        if (length == 0) {
            return false;
        }

        return true;

    }
}