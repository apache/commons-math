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
package org.apache.commons.math4.legacy.stat.descriptive.summary;

import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.stat.descriptive.WeightedEvaluation;
import org.apache.commons.math4.legacy.core.MathArrays;

/**
 * Returns the weighted sum of the available values.
 * <p>
 * If there are no values in the dataset, then 0 is returned.
 * If any of the values are
 * {@code NaN}, then {@code NaN} is returned.
 */
public final class WeightedSum implements WeightedEvaluation {
    /** An instance. */
    private static final WeightedSum INSTANCE = new WeightedSum();

    /** Create an instance. */
    private WeightedSum() {
        // Do nothing
    }

    /**
     * Gets an instance.
     *
     * @return an instance
     */
    public static WeightedSum getInstance() {
        return INSTANCE;
    }

    /**
     * The weighted sum of the entries in the specified portion of
     * the input array, or 0 if the designated subarray
     * is empty.
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     *     <li>the weights array is null</li>
     *     <li>the weights array does not have the same length as the values array</li>
     *     <li>the weights array contains one or more infinite values</li>
     *     <li>the weights array contains one or more NaN values</li>
     *     <li>the weights array contains negative values</li>
     *     <li>the start and length arguments do not determine a valid array</li>
     * </ul>
     * <p>
     * Uses the formula, <pre>
     *    weighted sum = &Sigma;(values[i] * weights[i])
     * </pre>
     *
     * @param values the input array
     * @param weights the weights array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return the sum of the values or 0 if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final double[] weights,
                           final int begin, final int length) throws MathIllegalArgumentException {
        // Zero length is allowed
        MathArrays.verifyValues(values, weights, begin, length, true);
        double sum = 0;
        final int end = begin + length;
        for (int i = begin; i < end; i++) {
            sum += values[i] * weights[i];
        }
        return sum;
    }

    /**
     * The weighted sum of the entries in the input array.
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     *     <li>the weights array is null</li>
     *     <li>the weights array does not have the same length as the values array</li>
     *     <li>the weights array contains one or more infinite values</li>
     *     <li>the weights array contains one or more NaN values</li>
     *     <li>the weights array contains negative values</li>
     * </ul>
     * <p>
     * Uses the formula, <pre>
     *    weighted sum = &Sigma;(values[i] * weights[i])
     * </pre>
     *
     * @param values the input array
     * @param weights the weights array
     * @return the sum of the values or 0 if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     */
    @Override
    public double evaluate(final double[] values, final double[] weights) throws MathIllegalArgumentException {
        return evaluate(values, weights, 0, values.length);
    }
}
