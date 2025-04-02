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
 * Computes the variance of the available values.  By default, the unbiased
 * "sample variance" definitional formula is used:
 * <p>
 * variance = sum((x_i - mean)^2) / (n - 1) </p>
 * <p>
 * where mean is the {@link Mean} and <code>n</code> is the number
 * of sample observations.</p>
 * <p>
 * The "population variance"  ( sum((x_i - mean)^2) / n ) can also
 * be computed using this statistic.  The <code>isBiasCorrected</code>
 * property determines whether the "population" or "sample" value is
 * returned by the <code>evaluate</code> methods.
 * To compute population variances, set this property to <code>false.</code>
 * </p>
 * <p>
Ø */
public class Variance implements WeightedEvaluation {
    /**
     * Whether or not bias correction is applied when computing the
     * value of the statistic. True means that bias is corrected.  See
     * {@link Variance} for details on the formula.
     */
    private boolean isBiasCorrected = true;

    /**
     * Constructs a Variance.
     */
    public Variance() {
        // Do nothing
    }

    /**
     * <p>Returns the weighted variance of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.</p>
     * <p>
     * Uses the formula <div style="white-space: pre"><code>
     *   &Sigma;(weights[i]*(values[i] - weightedMean)<sup>2</sup>)/(&Sigma;(weights[i]) - 1)
     * </code></div>
     * where weightedMean is the weighted mean
     * <p>
     * This formula will not return the same result as the unweighted variance when all
     * weights are equal, unless all weights are equal to 1. The formula assumes that
     * weights are to be treated as "expansion values," as will be the case if for example
     * the weights represent frequency counts. To normalize weights so that the denominator
     * in the variance computation equals the length of the input vector minus one, use <pre>
     *   <code>evaluate(values, MathArrays.normalizeArray(weights, values.length)); </code>
     * </pre>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.</p>
     * <p>
     * Throws <code>IllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     *     <li>the weights array is null</li>
     *     <li>the weights array does not have the same length as the values array</li>
     *     <li>the weights array contains one or more infinite values</li>
     *     <li>the weights array contains one or more NaN values</li>
     *     <li>the weights array contains negative values</li>
     *     <li>the weights array does not contain at least one non-zero value (applies when length is non zero)</li>
     *     <li>the start and length arguments do not determine a valid array</li>
     * </ul>
     * <p>
     * Does not change the internal state of the statistic.</p>
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if either array is null.</p>
     *
     * @param values the input array
     * @param weights the weights array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return the weighted variance of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     * @since 2.1
     */
    @Override
    public double evaluate(final double[] values, final double[] weights,
                           final int begin, final int length) throws MathIllegalArgumentException {

        double var = Double.NaN;

        if (MathArrays.verifyValues(values, weights, begin, length)) {
            if (length == 1) {
                var = 0.0;
            } else if (length > 1) {
                Mean mean = new Mean();
                double m = mean.evaluate(values, weights, begin, length);
                var = evaluate(values, weights, m, begin, length);
            }
        }
        return var;
    }

    /**
     * <p>
     * Returns the weighted variance of the entries in the input array.</p>
     * <p>
     * Uses the formula <div style="white-space:pre"><code>
     *   &Sigma;(weights[i]*(values[i] - weightedMean)<sup>2</sup>)/(&Sigma;(weights[i]) - 1)
     * </code></div>
     * where weightedMean is the weighted mean
     * <p>
     * This formula will not return the same result as the unweighted variance when all
     * weights are equal, unless all weights are equal to 1. The formula assumes that
     * weights are to be treated as "expansion values," as will be the case if for example
     * the weights represent frequency counts. To normalize weights so that the denominator
     * in the variance computation equals the length of the input vector minus one, use <pre>
     *   <code>evaluate(values, MathArrays.normalizeArray(weights, values.length)); </code>
     * </pre>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.</p>
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     *     <li>the weights array is null</li>
     *     <li>the weights array does not have the same length as the values array</li>
     *     <li>the weights array contains one or more infinite values</li>
     *     <li>the weights array contains one or more NaN values</li>
     *     <li>the weights array contains negative values</li>
     *     <li>the weights array does not contain at least one non-zero value (applies when length is non zero)</li>
     * </ul>
     * <p>
     * Does not change the internal state of the statistic.</p>
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if either array is null.</p>
     *
     * @param values the input array
     * @param weights the weights array
     * @return the weighted variance of the values
     * @throws MathIllegalArgumentException if the parameters are not valid
     * @since 2.1
     */
    @Override
    public double evaluate(final double[] values, final double[] weights)
        throws MathIllegalArgumentException {
        return evaluate(values, weights, 0, values.length);
    }

    /**
     * Returns the weighted variance of the entries in the specified portion of
     * the input array, using the precomputed weighted mean value.  Returns
     * <code>Double.NaN</code> if the designated subarray is empty.
     * <p>
     * Uses the formula <div style="white-space:pre"><code>
     *   &Sigma;(weights[i]*(values[i] - mean)<sup>2</sup>)/(&Sigma;(weights[i]) - 1)
     * </code></div>
     * <p>
     * The formula used assumes that the supplied mean value is the weighted arithmetic
     * mean of the sample data, not a known population parameter. This method
     * is supplied only to save computation when the mean has already been
     * computed.</p>
     * <p>
     * This formula will not return the same result as the unweighted variance when all
     * weights are equal, unless all weights are equal to 1. The formula assumes that
     * weights are to be treated as "expansion values," as will be the case if for example
     * the weights represent frequency counts. To normalize weights so that the denominator
     * in the variance computation equals the length of the input vector minus one, use <pre>
     *   <code>evaluate(values, MathArrays.normalizeArray(weights, values.length), mean); </code>
     * </pre>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.</p>
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     *     <li>the weights array is null</li>
     *     <li>the weights array does not have the same length as the values array</li>
     *     <li>the weights array contains one or more infinite values</li>
     *     <li>the weights array contains one or more NaN values</li>
     *     <li>the weights array contains negative values</li>
     *     <li>the weights array does not contain at least one non-zero value (applies when length is non zero)</li>
     *     <li>the start and length arguments do not determine a valid array</li>
     * </ul>
     * <p>
     * Does not change the internal state of the statistic.</p>
     *
     * @param values the input array
     * @param weights the weights array
     * @param mean the precomputed weighted mean value
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return the variance of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     * @since 2.1
     */
    public double evaluate(final double[] values, final double[] weights,
                           final double mean, final int begin, final int length)
        throws MathIllegalArgumentException {

        double var = Double.NaN;

        if (MathArrays.verifyValues(values, weights, begin, length)) {
            if (length == 1) {
                var = 0.0;
            } else if (length > 1) {
                double accum = 0.0;
                double dev = 0.0;
                double accum2 = 0.0;
                double sumWts = 0;
                int end = begin + length;
                for (int i = begin; i < end; i++) {
                    dev = values[i] - mean;
                    accum += weights[i] * (dev * dev);
                    accum2 += weights[i] * dev;
                    sumWts += weights[i];
                }

                if (isBiasCorrected) {
                    // Note: For this to be valid the weights should correspond to counts
                    // of each observation where the weights are positive integers; the
                    // sum of the weights is the total number of observations and should
                    // be at least 2.
                    var = (accum - (accum2 * accum2 / sumWts)) / (sumWts - 1.0);
                } else {
                    var = (accum - (accum2 * accum2 / sumWts)) / sumWts;
                }
            }
        }
        return var;
    }

    /**
     * <p>Returns the weighted variance of the values in the input array, using
     * the precomputed weighted mean value.</p>
     * <p>
     * Uses the formula <div style="white-space:pre"><code>
     *   &Sigma;(weights[i]*(values[i] - mean)<sup>2</sup>)/(&Sigma;(weights[i]) - 1)
     * </code></div>
     * <p>
     * The formula used assumes that the supplied mean value is the weighted arithmetic
     * mean of the sample data, not a known population parameter. This method
     * is supplied only to save computation when the mean has already been
     * computed.</p>
     * <p>
     * This formula will not return the same result as the unweighted variance when all
     * weights are equal, unless all weights are equal to 1. The formula assumes that
     * weights are to be treated as "expansion values," as will be the case if for example
     * the weights represent frequency counts. To normalize weights so that the denominator
     * in the variance computation equals the length of the input vector minus one, use <pre>
     *   <code>evaluate(values, MathArrays.normalizeArray(weights, values.length), mean); </code>
     * </pre>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.</p>
     * <p>
     * Throws <code>MathIllegalArgumentException</code> if any of the following are true:
     * <ul><li>the values array is null</li>
     *     <li>the weights array is null</li>
     *     <li>the weights array does not have the same length as the values array</li>
     *     <li>the weights array contains one or more infinite values</li>
     *     <li>the weights array contains one or more NaN values</li>
     *     <li>the weights array contains negative values</li>
     *     <li>the weights array does not contain at least one non-zero value (applies when length is non zero)</li>
     * </ul>
     * <p>
     * Does not change the internal state of the statistic.</p>
     *
     * @param values the input array
     * @param weights the weights array
     * @param mean the precomputed weighted mean value
     * @return the variance of the values or Double.NaN if length = 0
     * @throws MathIllegalArgumentException if the parameters are not valid
     * @since 2.1
     */
    public double evaluate(final double[] values, final double[] weights, final double mean)
        throws MathIllegalArgumentException {
        return evaluate(values, weights, mean, 0, values.length);
    }

    /**
     * @return the isBiasCorrected.
     */
    public boolean isBiasCorrected() {
        return isBiasCorrected;
    }

    /**
     * @param biasCorrected The isBiasCorrected to set.
     */
    public void setBiasCorrected(boolean biasCorrected) {
        this.isBiasCorrected = biasCorrected;
    }
}
