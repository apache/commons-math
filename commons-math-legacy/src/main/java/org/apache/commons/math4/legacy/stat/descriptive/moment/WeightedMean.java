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
package org.apache.commons.math4.legacy.stat.descriptive.moment;

import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.stat.descriptive.WeightedEvaluation;

/**
 * Computes the weighted mean of a set of values. Uses the formula:
 * <p>
 * mean = sum(w_i * x_i) / sum(w_i)
 * </p>
 * <p>where <code>w_i</code> is the weight for observation <code>x_i</code>.
 * </p>
 * <p> If used to compute the mean of an array
 * of stored values, a two-pass, corrected algorithm is used, starting with
 * the definitional formula computed using the array of stored values and then
 * correcting this by adding the mean deviation of the data values from the
 * arithmetic mean. See, e.g. "Comparison of Several Algorithms for Computing
 * Sample Means and Variances," Robert F. Ling, Journal of the American
 * Statistical Association, Vol. 69, No. 348 (Dec., 1974), pp. 859-866. </p>
 * <p>
 *  Returns <code>Double.NaN</code> if the dataset is empty. Note that
 *  Double.NaN may also be returned if the input includes NaN and / or infinite
 *  values.
 * </p>
 */
public final class WeightedMean implements WeightedEvaluation {
    /** An instance. */
    private static final WeightedMean INSTANCE = new WeightedMean();

    /** Create an instance. */
    private WeightedMean() {
        // Do nothing
    }

    /**
     * Gets an instance.
     *
     * @return an instance
     */
    public static WeightedMean getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the weighted arithmetic mean of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     * <p>
     * Throws <code>IllegalArgumentException</code> if either array is null.</p>
     * <p>
     * See {@link WeightedMean} for details on the computing algorithm. The two-pass algorithm
     * described above is used here, with weights applied in computing both the original
     * estimate and the correction factor.</p>
     * <p>
     * Throws <code>IllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     *     <li>the weights array is null</li>
     *     <li>the weights array does not have the same length as the values array</li>
     *     <li>the weights array contains one or more infinite values</li>
     *     <li>the weights array contains one or more NaN values</li>
     *     <li>the weights array contains negative values</li>
     *     <li>the start and length arguments do not determine a valid array</li>
     * </ul>
     *
     * @param values the input array
     * @param weights the weights array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return the mean of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     * @since 2.1
     */
    @Override
    public double evaluate(final double[] values, final double[] weights,
                           final int begin, final int length) throws MathIllegalArgumentException {
        if (MathArrays.verifyValues(values, weights, begin, length)) {

            // Compute initial estimate using definitional formula
            final int end = begin + length;
            double sumw = sum(weights, begin, end);
            double xbarw = sum(values, weights, begin, end) / sumw;

            // Compute correction factor in second pass
            double correction = 0;
            for (int i = begin; i < end; i++) {
                correction += weights[i] * (values[i] - xbarw);
            }
            return xbarw + (correction / sumw);
        }
        return Double.NaN;
    }

    /**
     * Returns the weighted arithmetic mean of the entries in the input array.
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if either array is null.</p>
     * <p>
     * See {@link WeightedMean} for details on the computing algorithm. The two-pass algorithm
     * described above is used here, with weights applied in computing both the original
     * estimate and the correction factor.</p>
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     *     <li>the weights array is null</li>
     *     <li>the weights array does not have the same length as the values array</li>
     *     <li>the weights array contains one or more infinite values</li>
     *     <li>the weights array contains one or more NaN values</li>
     *     <li>the weights array contains negative values</li>
     * </ul>
     *
     * @param values the input array
     * @param weights the weights array
     * @return the mean of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     * @since 2.1
     */
    @Override
    public double evaluate(final double[] values, final double[] weights)
        throws MathIllegalArgumentException {
        return evaluate(values, weights, 0, values.length);
    }


    /**
     * Compute the sum of the values.
     *
     * @param values the values
     * @param begin inclusive start index
     * @param end exclusive end index
     * @return the sum
     */
    private static double sum(double[] values, int begin, int end) {
        double sum = 0;
        for (int i = begin; i < end; i++) {
            sum += values[i];
        }
        return sum;
    }

    /**
     * Compute the weighted sum of the values.
     *
     * @param values the values
     * @param weights the weights
     * @param begin inclusive start index
     * @param end exclusive end index
     * @return the sum
     */
    private static double sum(double[] values, double[] weights, int begin, int end) {
        double sum = 0;
        for (int i = begin; i < end; i++) {
            sum += values[i] * weights[i];
        }
        return sum;
    }
}
