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

import org.apache.commons.math.MathRuntimeException;

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
    implements UnivariateStatistic {

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
            throw MathRuntimeException.createIllegalArgumentException("input values array is null");
        }

        if (begin < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "start position cannot be negative ({0})", begin);
        }

        if (length < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "length cannot be negative ({0})", length);
        }

        if (begin + length > values.length) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "subarray ends after array end");
        }

        if (length == 0) {
            return false;
        }

        return true;

    }

    /**
     * This method is used by <code>evaluate(double[], double[], int, int)</code> methods
     * to verify that the begin and length parameters designate a subarray of positive length
     * and the weights are all non-negative, non-NaN, finite, and not all zero.
     * <p>
     * <ul>
     * <li>returns <code>true</code> iff the parameters designate a subarray of
     * positive length and the weights array contains legitimate values.</li>
     * <li>throws <code>IllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     *     <li>the weights array is null</li>
     *     <li>the weights array does not have the same length as the values array</li>
     *     <li>the weights array contains one or more infinite values</li>
     *     <li>the weights array contains one or more NaN values</li>
     *     <li>the weights array contains negative values</li>
     *     <li>the start and length arguments do not determine a valid array</li></ul>
     * </li>
     * <li>returns <code>false</li> if the array is non-null, but
     * <code>length</code> is 0.
     * </ul></p>
     *
     * @param values the input array
     * @param weights the weights array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return true if the parameters are valid and designate a subarray of positive length
     * @throws IllegalArgumentException if the indices are invalid or the array is null
     * @since 2.1
     */
    protected boolean test(
        final double[] values,
        final double[] weights,
        final int begin,
        final int length) {

        if (weights == null) {
            throw MathRuntimeException.createIllegalArgumentException("input weights array is null");
        }

        if (weights.length !=  values.length) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "Different number of weights and values");
        }

        boolean containsPositiveWeight = false;
        for (int i = begin; i < begin + length; i++) {
            if (Double.isNaN(weights[i])) {
                throw MathRuntimeException.createIllegalArgumentException(
                        "NaN weight at index {0}", i);
            }
            if (Double.isInfinite(weights[i])) {
                throw MathRuntimeException.createIllegalArgumentException(
                        "Infinite weight at index {0}", i);
            }
            if (weights[i] < 0) {
                throw MathRuntimeException.createIllegalArgumentException(
                      "negative weight {0} at index {1} ", weights[i], i);
            }
            if (!containsPositiveWeight && weights[i] > 0.0) {
                containsPositiveWeight = true;
            }
        }

        if (!containsPositiveWeight) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "weight array must contain at least one non-zero value");
        }

        return test(values, begin, length);
    }
}

