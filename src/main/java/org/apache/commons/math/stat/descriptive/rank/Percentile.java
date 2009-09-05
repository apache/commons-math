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
package org.apache.commons.math.stat.descriptive.rank;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.stat.descriptive.AbstractUnivariateStatistic;

/**
 * Provides percentile computation.
 * <p>
 * There are several commonly used methods for estimating percentiles (a.k.a.
 * quantiles) based on sample data.  For large samples, the different methods
 * agree closely, but when sample sizes are small, different methods will give
 * significantly different results.  The algorithm implemented here works as follows:
 * <ol>
 * <li>Let <code>n</code> be the length of the (sorted) array and
 * <code>0 < p <= 100</code> be the desired percentile.</li>
 * <li>If <code> n = 1 </code> return the unique array element (regardless of
 * the value of <code>p</code>); otherwise </li>
 * <li>Compute the estimated percentile position
 * <code> pos = p * (n + 1) / 100</code> and the difference, <code>d</code>
 * between <code>pos</code> and <code>floor(pos)</code> (i.e. the fractional
 * part of <code>pos</code>).  If <code>pos >= n</code> return the largest
 * element in the array; otherwise</li>
 * <li>Let <code>lower</code> be the element in position
 * <code>floor(pos)</code> in the array and let <code>upper</code> be the
 * next element in the array.  Return <code>lower + d * (upper - lower)</code>
 * </li>
 * </ol></p>
 * <p>
 * To compute percentiles, the data must be (totally) ordered.  Input arrays
 * are copied and then sorted using  {@link java.util.Arrays#sort(double[])}.
 * The ordering used by <code>Arrays.sort(double[])</code> is the one determined
 * by {@link java.lang.Double#compareTo(Double)}.  This ordering makes
 * <code>Double.NaN</code> larger than any other value (including
 * <code>Double.POSITIVE_INFINITY</code>).  Therefore, for example, the median
 * (50th percentile) of
 * <code>{0, 1, 2, 3, 4, Double.NaN}</code> evaluates to <code>2.5.</code></p>
 * <p>
 * Since percentile estimation usually involves interpolation between array
 * elements, arrays containing  <code>NaN</code> or infinite values will often
 * result in <code>NaN<code> or infinite values returned.</p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.</p>
 *
 * @version $Revision$ $Date$
 */
public class Percentile extends AbstractUnivariateStatistic implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -8091216485095130416L;

    /** Determines what percentile is computed when evaluate() is activated
     * with no quantile argument */
    private double quantile = 0.0;

    /**
     * Constructs a Percentile with a default quantile
     * value of 50.0.
     */
    public Percentile() {
        this(50.0);
    }

    /**
     * Constructs a Percentile with the specific quantile value.
     * @param p the quantile
     * @throws IllegalArgumentException  if p is not greater than 0 and less
     * than or equal to 100
     */
    public Percentile(final double p) {
        setQuantile(p);
    }

    /**
     * Copy constructor, creates a new {@code Percentile} identical
     * to the {@code original}
     *
     * @param original the {@code Percentile} instance to copy
     */
    public Percentile(Percentile original) {
        copy(original, this);
    }

    /**
     * Returns an estimate of the <code>p</code>th percentile of the values
     * in the <code>values</code> array.
     * <p>
     * Calls to this method do not modify the internal <code>quantile</code>
     * state of this statistic.</p>
     * <p>
     * <ul>
     * <li>Returns <code>Double.NaN</code> if <code>values</code> has length
     * <code>0</code></li>
     * <li>Returns (for any value of <code>p</code>) <code>values[0]</code>
     *  if <code>values</code> has length <code>1</code></li>
     * <li>Throws <code>IllegalArgumentException</code> if <code>values</code>
     * is null or p is not a valid quantile value (p must be greater than 0
     * and less than or equal to 100) </li>
     * </ul></p>
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values input array of values
     * @param p the percentile value to compute
     * @return the percentile value or Double.NaN if the array is empty
     * @throws IllegalArgumentException if <code>values</code> is null
     *     or p is invalid
     */
    public double evaluate(final double[] values, final double p) {
        test(values, 0, 0);
        return evaluate(values, 0, values.length, p);
    }

    /**
     * Returns an estimate of the <code>quantile</code>th percentile of the
     * designated values in the <code>values</code> array.  The quantile
     * estimated is determined by the <code>quantile</code> property.
     * <p>
     * <ul>
     * <li>Returns <code>Double.NaN</code> if <code>length = 0</code></li>
     * <li>Returns (for any value of <code>quantile</code>)
     * <code>values[begin]</code> if <code>length = 1 </code></li>
     * <li>Throws <code>IllegalArgumentException</code> if <code>values</code>
     * is null,  or <code>start</code> or <code>length</code>
     * is invalid</li>
     * </ul></p>
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values the input array
     * @param start index of the first array element to include
     * @param length the number of elements to include
     * @return the percentile value
     * @throws IllegalArgumentException if the parameters are not valid
     *
     */
    @Override
    public double evaluate( final double[] values, final int start, final int length) {
        return evaluate(values, start, length, quantile);
    }

     /**
     * Returns an estimate of the <code>p</code>th percentile of the values
     * in the <code>values</code> array, starting with the element in (0-based)
     * position <code>begin</code> in the array and including <code>length</code>
     * values.
     * <p>
     * Calls to this method do not modify the internal <code>quantile</code>
     * state of this statistic.</p>
     * <p>
     * <ul>
     * <li>Returns <code>Double.NaN</code> if <code>length = 0</code></li>
     * <li>Returns (for any value of <code>p</code>) <code>values[begin]</code>
     *  if <code>length = 1 </code></li>
     * <li>Throws <code>IllegalArgumentException</code> if <code>values</code>
     *  is null , <code>begin</code> or <code>length</code> is invalid, or
     * <code>p</code> is not a valid quantile value (p must be greater than 0
     * and less than or equal to 100)</li>
     * </ul></p>
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values array of input values
     * @param p  the percentile to compute
     * @param begin  the first (0-based) element to include in the computation
     * @param length  the number of array elements to include
     * @return  the percentile value
     * @throws IllegalArgumentException if the parameters are not valid or the
     * input array is null
     */
    public double evaluate(final double[] values, final int begin,
            final int length, final double p) {

        test(values, begin, length);

        if ((p > 100) || (p <= 0)) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "out of bounds quantile value: {0}, must be in (0, 100]", p);
        }
        if (length == 0) {
            return Double.NaN;
        }
        if (length == 1) {
            return values[begin]; // always return single value for n = 1
        }
        double n = length;
        double pos = p * (n + 1) / 100;
        double fpos = Math.floor(pos);
        int intPos = (int) fpos;
        double dif = pos - fpos;
        double[] sorted = new double[length];
        System.arraycopy(values, begin, sorted, 0, length);
        Arrays.sort(sorted);

        if (pos < 1) {
            return sorted[0];
        }
        if (pos >= n) {
            return sorted[length - 1];
        }
        double lower = sorted[intPos - 1];
        double upper = sorted[intPos];
        return lower + dif * (upper - lower);
    }

    /**
     * Returns the value of the quantile field (determines what percentile is
     * computed when evaluate() is called with no quantile argument).
     *
     * @return quantile
     */
    public double getQuantile() {
        return quantile;
    }

    /**
     * Sets the value of the quantile field (determines what percentile is
     * computed when evaluate() is called with no quantile argument).
     *
     * @param p a value between 0 < p <= 100
     * @throws IllegalArgumentException  if p is not greater than 0 and less
     * than or equal to 100
     */
    public void setQuantile(final double p) {
        if (p <= 0 || p > 100) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "out of bounds quantile value: {0}, must be in (0, 100]", p);
        }
        quantile = p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Percentile copy() {
        Percentile result = new Percentile();
        copy(this, result);
        return result;
    }

    /**
     * Copies source to dest.
     * <p>Neither source nor dest can be null.</p>
     *
     * @param source Percentile to copy
     * @param dest Percentile to copy to
     * @throws NullPointerException if either source or dest is null
     */
    public static void copy(Percentile source, Percentile dest) {
        dest.quantile = source.quantile;
    }

}
