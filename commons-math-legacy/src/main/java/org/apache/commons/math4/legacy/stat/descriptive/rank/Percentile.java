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
package org.apache.commons.math4.legacy.stat.descriptive.rank;

import java.util.Arrays;
import java.util.BitSet;

import org.apache.commons.numbers.core.Precision;
import org.apache.commons.numbers.arrays.SortInPlace;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.stat.descriptive.AbstractUnivariateStatistic;
import org.apache.commons.math4.legacy.stat.ranking.NaNStrategy;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.math4.legacy.core.MathArrays;

/**
 * Provides percentile computation.
 * <p>
 * There are several commonly used methods for estimating percentiles (a.k.a.
 * quantiles) based on sample data.  For large samples, the different methods
 * agree closely, but when sample sizes are small, different methods will give
 * significantly different results.  The algorithm implemented here works as follows:
 * <ol>
 * <li>Let <code>n</code> be the length of the (sorted) array and
 * <code>0 &lt; p &lt;= 100</code> be the desired percentile.</li>
 * <li>If <code> n = 1 </code> return the unique array element (regardless of
 * the value of <code>p</code>); otherwise </li>
 * <li>Compute the estimated percentile position
 * <code> pos = p * (n + 1) / 100</code> and the difference, <code>d</code>
 * between <code>pos</code> and <code>floor(pos)</code> (i.e. the fractional
 * part of <code>pos</code>).</li>
 * <li> If <code>pos &lt; 1</code> return the smallest element in the array.</li>
 * <li> Else if <code>pos &gt;= n</code> return the largest element in the array.</li>
 * <li> Else let <code>lower</code> be the element in position
 * <code>floor(pos)</code> in the array and let <code>upper</code> be the
 * next element in the array.  Return <code>lower + d * (upper - lower)</code>
 * </li>
 * </ol>
 * <p>
 * To compute percentiles, the data must be at least partially ordered.  Input
 * arrays are copied and recursively partitioned using an ordering definition.
 * The ordering used by <code>Arrays.sort(double[])</code> is the one determined
 * by {@link java.lang.Double#compareTo(Double)}.  This ordering makes
 * <code>Double.NaN</code> larger than any other value (including
 * <code>Double.POSITIVE_INFINITY</code>).  Therefore, for example, the median
 * (50th percentile) of
 * <code>{0, 1, 2, 3, 4, Double.NaN}</code> evaluates to <code>2.5.</code></p>
 * <p>
 * Since percentile estimation usually involves interpolation between array
 * elements, arrays containing  <code>NaN</code> or infinite values will often
 * result in <code>NaN</code> or infinite values returned.</p>
 * <p>
 * Further, to include different estimation types such as R1, R2 as mentioned in
 * <a href="http://en.wikipedia.org/wiki/Quantile">Quantile page(wikipedia)</a>,
 * a type specific NaN handling strategy is used to closely match with the
 * typically observed results from popular tools like R(R1-R9), Excel(R7).</p>
 * <p>
 * Since 2.2, Percentile uses only selection instead of complete sorting
 * and caches selection algorithm state between calls to the various
 * {@code evaluate} methods. This greatly improves efficiency, both for a single
 * percentile and multiple percentile computations. To maximize performance when
 * multiple percentiles are computed based on the same data, users should set the
 * data array once using either one of the {@link #evaluate(double[], double)} or
 * {@link #setData(double[])} methods and thereafter {@link #evaluate(double)}
 * with just the percentile provided.
 * </p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.</p>
 */
public class Percentile extends AbstractUnivariateStatistic {
    /** Maximum number of partitioning pivots cached (each level double the number of pivots). */
    private static final int MAX_CACHED_LEVELS = 10;

    /** Maximum number of cached pivots in the pivots cached array. */
    private static final int PIVOTS_HEAP_LENGTH = 0x1 << MAX_CACHED_LEVELS - 1;

    /** Default KthSelector used with default pivoting strategy. */
    private final KthSelector kthSelector;

    /** Any of the {@link EstimationType}s such as {@link EstimationType#LEGACY CM} can be used. */
    private final EstimationType estimationType;

    /** NaN Handling of the input as defined by {@link NaNStrategy}. */
    private final NaNStrategy nanStrategy;

    /**
     * Determines what percentile is computed when evaluate() is activated
     * with no quantile argument.
     */
    private double quantile;

    /** Cached pivots. */
    private int[] cachedPivots;

    /** Weight. */
    private double[] weights;
    /**
     * Constructs a Percentile with the following defaults.
     * <ul>
     *   <li>default quantile: 50.0, can be reset with {@link #setQuantile(double)}</li>
     *   <li>default estimation type: {@link EstimationType#LEGACY},
     *   can be reset with {@link #withEstimationType(EstimationType)}</li>
     *   <li>default NaN strategy: {@link NaNStrategy#REMOVED},
     *   can be reset with {@link #withNaNStrategy(NaNStrategy)}</li>
     *   <li>a KthSelector that makes use of {@link MedianOf3PivotingStrategy},
     *   can be reset with {@link #withKthSelector(KthSelector)}</li>
     * </ul>
     */
    public Percentile() {
        // No try-catch or advertised exception here - arg is valid
        this(50.0);
    }

    /**
     * Constructs a Percentile with the specific quantile value and the following.
     * <ul>
     *   <li>default method type: {@link EstimationType#LEGACY}</li>
     *   <li>default NaN strategy: {@link NaNStrategy#REMOVED}</li>
     *   <li>a Kth Selector : {@link KthSelector}</li>
     * </ul>
     * @param quantile the quantile
     * @throws MathIllegalArgumentException  if p is not greater than 0 and less
     * than or equal to 100
     */
    public Percentile(final double quantile) {
        this(quantile, EstimationType.LEGACY, NaNStrategy.REMOVED,
             new KthSelector(new MedianOf3PivotingStrategy()));
    }

    /**
     * Copy constructor, creates a new {@code Percentile} identical.
     * to the {@code original}
     *
     * @param original the {@code Percentile} instance to copy.
     * Cannot be {@code null}.
     */
    public Percentile(final Percentile original) {
        NullArgumentException.check(original);
        estimationType   = original.getEstimationType();
        nanStrategy      = original.getNaNStrategy();
        kthSelector      = original.getKthSelector();

        setData(original.getDataRef());
        if (original.cachedPivots != null) {
            System.arraycopy(original.cachedPivots, 0, cachedPivots, 0, original.cachedPivots.length);
        }
        setQuantile(original.quantile);
    }

    /**
     * Constructs a Percentile with the specific quantile value,
     * {@link EstimationType}, {@link NaNStrategy} and {@link KthSelector}.
     *
     * @param quantile the quantile to be computed
     * @param estimationType one of the percentile {@link EstimationType  estimation types}
     * @param nanStrategy one of {@link NaNStrategy} to handle with NaNs.
     * Cannot be {@code null}.
     * @param kthSelector a {@link KthSelector} to use for pivoting during search
     * @throws MathIllegalArgumentException if p is not within (0,100]
     */
    protected Percentile(final double quantile,
                         final EstimationType estimationType,
                         final NaNStrategy nanStrategy,
                         final KthSelector kthSelector) {
        setQuantile(quantile);
        cachedPivots = null;
        NullArgumentException.check(estimationType);
        NullArgumentException.check(nanStrategy);
        NullArgumentException.check(kthSelector);
        this.estimationType = estimationType;
        this.nanStrategy = nanStrategy;
        this.kthSelector = kthSelector;
    }

    /** {@inheritDoc} */
    @Override
    public void setData(final double[] values) {
        if (values == null) {
            cachedPivots = null;
        } else {
            cachedPivots = new int[PIVOTS_HEAP_LENGTH];
            Arrays.fill(cachedPivots, -1);
        }
        super.setData(values);
    }
    /**
     * Set the data array.
     * @param values Data array.
     * Cannot be {@code null}.
     * @param sampleWeights corresponding positive and non-NaN weights.
     * Cannot be {@code null}.
     * @throws MathIllegalArgumentException if lengths of values and weights are not equal.
     * @throws org.apache.commons.math4.legacy.exception.NotANumberException if any weight is NaN
     * @throws org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException if any weight is not positive
     */
    public void setData(final double[] values,
                        final double[] sampleWeights) {
        setData(values, sampleWeights, 0, values.length);
    }

    /** {@inheritDoc} */
    @Override
    public void setData(final double[] values, final int begin, final int length) {
        if (values == null) {
            cachedPivots = null;
        } else {
            cachedPivots = new int[PIVOTS_HEAP_LENGTH];
            Arrays.fill(cachedPivots, -1);
        }
        super.setData(values, begin, length);
    }
    /**
     * Set the data and weights arrays.  The input array is copied, not referenced.
     * @param values Data array.
     * Cannot be {@code null}.
     * @param sampleWeights corresponding positive and non-NaN weights.
     * Cannot be {@code null}.
     * @param begin the index of the first element to include
     * @param length the number of elements to include
     * @throws MathIllegalArgumentException if lengths of values and weights are not equal or values or weights is null
     * @throws NotPositiveException if begin or length is not positive
     * @throws NumberIsTooLargeException if begin + length is greater than values.length
     * @throws org.apache.commons.math4.legacy.exception.NotANumberException if any weight is NaN
     * @throws org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException if any weight is not positive
     */
    public void setData(final double[] values,
                        final double[] sampleWeights,
                        final int begin,
                        final int length) {
        if (begin < 0) {
            throw new NotPositiveException(LocalizedFormats.START_POSITION, begin);
        }

        if (length < 0) {
            throw new NotPositiveException(LocalizedFormats.LENGTH, length);
        }

        if (begin + length > values.length) {
            throw new NumberIsTooLargeException(LocalizedFormats.SUBARRAY_ENDS_AFTER_ARRAY_END,
                                                begin + length, values.length, true);
        }

        if (sampleWeights == null) {
            throw new MathIllegalArgumentException(LocalizedFormats.NULL_NOT_ALLOWED);
        }
        cachedPivots = new int[PIVOTS_HEAP_LENGTH];
        Arrays.fill(cachedPivots, -1);

        // Check length
        if (values.length != sampleWeights.length) {
            throw new MathIllegalArgumentException(LocalizedFormats.LENGTH,
                                                   values, sampleWeights);
        }
        // Check weights > 0
        MathArrays.checkPositive(sampleWeights);
        MathArrays.checkNotNaN(sampleWeights);

        super.setData(values, begin, length);
        weights = new double[length];
        System.arraycopy(sampleWeights, begin, weights, 0, length);
    }
    /**
     * Returns the result of evaluating the statistic over the stored data.
     * If weights have been set, it will compute weighted percentile.
     * <p>
     * The stored array is the one which was set by previous calls to
     * {@link #setData(double[])} or {@link #setData(double[], double[], int, int)}
     * </p>
     * @param p the percentile value to compute
     * @return the value of the statistic applied to the stored data
     * @throws MathIllegalArgumentException if lengths of values and weights are not equal or values or weights is null
     * @throws NotPositiveException if begin, length is negative
     * @throws org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException if any weight is not positive
     * @throws org.apache.commons.math4.legacy.exception.NotANumberException if any weight is NaN
     * @throws OutOfRangeException if p is invalid
     * @throws NumberIsTooLargeException if begin + length is greater than values.length
     * (p must be greater than 0 and less than or equal to 100)
     */
    public double evaluate(final double p) {
        if (weights == null) {
            return evaluate(getDataRef(), p);
        } else {
            return evaluate(getDataRef(), weights, p);
        }
    }

    /**
     * Returns an estimate of the <code>p</code>th percentile of the values
     * in the <code>values</code> array.
     * <p>
     * Calls to this method do not modify the internal <code>quantile</code>
     * state of this statistic.</p>
     * <ul>
     * <li>Returns <code>Double.NaN</code> if <code>values</code> has length
     * <code>0</code></li>
     * <li>Returns (for any value of <code>p</code>) <code>values[0]</code>
     *  if <code>values</code> has length <code>1</code></li>
     * <li>Throws <code>MathIllegalArgumentException</code> if <code>values</code>
     * is null or p is not a valid quantile value (p must be greater than 0
     * and less than or equal to 100) </li>
     * </ul>
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values input array of values
     * @param p the percentile value to compute
     * @return the percentile value or Double.NaN if the array is empty
     * @throws MathIllegalArgumentException if <code>values</code> is null or p is invalid
     */
    public double evaluate(final double[] values, final double p) {
        MathArrays.verifyValues(values, 0, 0);
        return evaluate(values, 0, values.length, p);
    }
    /**
     * Returns an estimate of the <code>p</code>th percentile of the values
     * in the <code>values</code> array with their weights.
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     * @param values input array of values
     * @param sampleWeights weights of values
     * @param p the percentile value to compute
     * @return the weighted percentile value or Double.NaN if the array is empty
     * @throws MathIllegalArgumentException if lengths of values and weights are not equal or values or weights is null
     * @throws NotPositiveException if begin, length is negative
     * @throws org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException if any weight is not positive
     * @throws org.apache.commons.math4.legacy.exception.NotANumberException if any weight is NaN
     * @throws OutOfRangeException if p is invalid
     * @throws NumberIsTooLargeException if begin + length is greater than values.length
     */
    public double evaluate(final double[] values, final double[] sampleWeights, final double p) {
        MathArrays.verifyValues(values, 0, 0);
        MathArrays.verifyValues(sampleWeights, 0, 0);
        return evaluate(values, sampleWeights, 0, values.length, p);
    }

    /**
     * Returns an estimate of the <code>quantile</code>th percentile of the
     * designated values in the <code>values</code> array.  The quantile
     * estimated is determined by the <code>quantile</code> property.
     * <ul>
     * <li>Returns <code>Double.NaN</code> if <code>length = 0</code></li>
     * <li>Returns (for any value of <code>quantile</code>)
     * <code>values[begin]</code> if <code>length = 1 </code></li>
     * <li>Throws <code>MathIllegalArgumentException</code> if <code>values</code>
     * is null, or <code>start</code> or <code>length</code> is invalid</li>
     * </ul>
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values the input array
     * @param start index of the first array element to include
     * @param length the number of elements to include
     * @return the percentile value
     * @throws MathIllegalArgumentException if the parameters are not valid
     *
     */
    @Override
    public double evaluate(final double[] values, final int start, final int length) {
        return evaluate(values, start, length, quantile);
    }
    /**
     * Returns an estimate of the weighted <code>quantile</code>th percentile of the
     * designated values in the <code>values</code> array.  The quantile
     * estimated is determined by the <code>quantile</code> property.
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values the input array
     * @param sampleWeights the weights of values
     * @param start index of the first array element to include
     * @param length the number of elements to include
     * @return the percentile value
     * @throws MathIllegalArgumentException if lengths of values and weights are not equal or values or weights is null
     * @throws NotPositiveException if begin, length is negative
     * @throws org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException if any weight is not positive
     * @throws org.apache.commons.math4.legacy.exception.NotANumberException if any weight is NaN
     * @throws OutOfRangeException if p is invalid
     * @throws NumberIsTooLargeException if begin + length is greater than values.length
     */
    public double evaluate(final double[] values, final double[] sampleWeights,
                           final int start, final int length) {
        return evaluate(values, sampleWeights, start, length, quantile);
    }

     /**
     * Returns an estimate of the <code>p</code>th percentile of the values
     * in the <code>values</code> array, starting with the element in (0-based)
     * position <code>begin</code> in the array and including <code>length</code>
     * values.
     * <p>
     * Calls to this method do not modify the internal <code>quantile</code>
     * state of this statistic.</p>
     * <ul>
     * <li>Returns <code>Double.NaN</code> if <code>length = 0</code></li>
     * <li>Returns (for any value of <code>p</code>) <code>values[begin]</code>
     *  if <code>length = 1 </code></li>
     * <li>Throws <code>MathIllegalArgumentException</code> if <code>values</code>
     *  is null , <code>begin</code> or <code>length</code> is invalid, or
     * <code>p</code> is not a valid quantile value (p must be greater than 0
     * and less than or equal to 100)</li>
     * </ul>
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values array of input values
     * @param p the percentile to compute
     * @param begin the first (0-based) element to include in the computation
     * @param length the number of array elements to include
     * @return the percentile value.
     * @throws MathIllegalArgumentException if the parameters are not valid.
     */
    public double evaluate(final double[] values, final int begin,
                           final int length, final double p) {
        MathArrays.verifyValues(values, begin, length);
        if (p > 100 || p <= 0) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE,
                                          p, 0, 100);
        }
        if (length == 0) {
            return Double.NaN;
        }
        if (length == 1) {
            return values[begin]; // always return single value for n = 1
        }

        final double[] work = getWorkArray(values, begin, length);
        final int[] pivotsHeap = getPivots(values);
        return work.length == 0 ?
            Double.NaN :
            estimationType.evaluate(work, pivotsHeap, p, kthSelector);
    }
     /**
     * Returns an estimate of the <code>p</code>th percentile of the values
     * in the <code>values</code> array with <code>sampleWeights</code>, starting with the element in (0-based)
     * position <code>begin</code> in the array and including <code>length</code>
     * values.
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values array of input values
     * @param sampleWeights positive and non-NaN weights of values
     * @param begin  the first (0-based) element to include in the computation
     * @param length  the number of array elements to include
     * @param p  percentile to compute
     * @return the weighted percentile value
     * @throws MathIllegalArgumentException if lengths of values and weights are not equal or values or weights is null
     * @throws NotPositiveException if begin, length is negative
     * @throws org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException if any weight is not positive
     * @throws org.apache.commons.math4.legacy.exception.NotANumberException if any weight is NaN
     * @throws OutOfRangeException if p is invalid
     * @throws NumberIsTooLargeException if begin + length is greater than values.length
     */
    public double evaluate(final double[] values, final double[] sampleWeights, final int begin,
                           final int length, final double p) {
        if (values == null || sampleWeights == null) {
            throw new MathIllegalArgumentException(LocalizedFormats.NULL_NOT_ALLOWED);
        }
        // Check length
        if (values.length != sampleWeights.length) {
            throw new MathIllegalArgumentException(LocalizedFormats.LENGTH,
                                                   values, sampleWeights);
        }
        MathArrays.verifyValues(values, begin, length);
        MathArrays.verifyValues(sampleWeights, begin, length);
        MathArrays.checkPositive(sampleWeights);
        MathArrays.checkNotNaN(sampleWeights);

        if (p > 100 || p <= 0) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE,
                                          p, 0, 100);
        }
        if (length == 0) {
            return Double.NaN;
        }
        if (length == 1) {
            // Always return single value for n = 1
            return values[begin];
        }

        final double[] work = getWorkArray(values, begin, length);
        final double[] workWeights = getWorkArray(values, sampleWeights, begin, length);
        return work.length == 0 ? Double.NaN :
                    estimationType.evaluate(work, workWeights, p);
    }
    /**
     * Returns the value of the quantile field (determines what percentile is
     * computed when evaluate() is called with no quantile argument).
     *
     * @return quantile set while construction or {@link #setQuantile(double)}
     */
    public double getQuantile() {
        return quantile;
    }

    /**
     * Sets the value of the quantile field (determines what percentile is
     * computed when evaluate() is called with no quantile argument).
     *
     * @param p a value between 0 &lt; p &lt;= 100
     * @throws MathIllegalArgumentException  if p is not greater than 0 and less
     * than or equal to 100
     */
    public void setQuantile(final double p) {
        if (p <= 0 || p > 100) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE,
                                          p, 0, 100);
        }
        quantile = p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Percentile copy() {
        return new Percentile(this);
    }

    /**
     * Get the work array to operate. Makes use of prior {@code storedData} if
     * it exists or else do a check on NaNs and copy a subset of the array
     * defined by begin and length parameters. The set {@link #nanStrategy} will
     * be used to either retain/remove/replace any NaNs present before returning
     * the resultant array.
     *
     * @param values the array of numbers
     * @param begin index to start reading the array
     * @param length the length of array to be read from the begin index
     * @return work array sliced from values in the range [begin,begin+length)
     * @throws MathIllegalArgumentException if values or indices are invalid
     */
    private double[] getWorkArray(final double[] values, final int begin, final int length) {
        final double[] work;
        if (values == getDataRef()) {
            work = getDataRef();
        } else {
            switch (nanStrategy) {
                case MAXIMAL: // Replace NaNs with +INFs
                    work = replaceAndSlice(values, begin, length, Double.NaN, Double.POSITIVE_INFINITY);
                    break;
                case MINIMAL: // Replace NaNs with -INFs
                    work = replaceAndSlice(values, begin, length, Double.NaN, Double.NEGATIVE_INFINITY);
                    break;
                case REMOVED: // Drop NaNs from data
                    work = removeAndSlice(values, begin, length, Double.NaN);
                    break;
                case FAILED: // NaN is not acceptable
                    work = copyOf(values, begin, length);
                    MathArrays.checkNotNaN(work);
                    break;
                default: // FIXED
                    work = copyOf(values,begin,length);
                    break;
            }
        }
        return work;
    }
    /**
     * Get the work arrays of weights to operate.
     *
     * @param values the array of numbers
     * @param sampleWeights the array of weights
     * @param begin index to start reading the array
     * @param length the length of array to be read from the begin index
     * @return work array sliced from values in the range [begin,begin+length)
     */
    protected double[] getWorkArray(final double[] values, final double[] sampleWeights,
                                    final int begin, final int length) {
        final double[] work;
        if (values == getDataRef()) {
            work = this.weights;
        } else {
            switch (nanStrategy) {
                case REMOVED: // Drop weight if the data is NaN
                    work = removeAndSliceByRef(values, sampleWeights, begin, length, Double.NaN);
                    break;
                default: // FIXED
                    work = copyOf(sampleWeights, begin, length);
                    break;
            }
        }
        return work;
    }
    /**
     * Make a copy of the array for the slice defined by array part from.
     * [begin, begin+length)
     * @param values the input array
     * @param begin start index of the array to include
     * @param length number of elements to include from begin
     * @return copy of a slice of the original array
     */
    private static double[] copyOf(final double[] values, final int begin, final int length) {
        MathArrays.verifyValues(values, begin, length);
        return Arrays.copyOfRange(values, begin, begin + length);
    }

    /**
     * Replace every occurrence of a given value with a replacement value in a
     * copied slice of array defined by array part from [begin, begin+length).
     *
     * @param values the input array
     * @param begin start index of the array to include
     * @param length number of elements to include from begin
     * @param original the value to be replaced with
     * @param replacement the value to be used for replacement
     * @return the copy of sliced array with replaced values
     */
    private static double[] replaceAndSlice(final double[] values,
                                            final int begin, final int length,
                                            final double original,
                                            final double replacement) {
        final double[] temp = copyOf(values, begin, length);
        for(int i = 0; i < length; i++) {
            temp[i] = Precision.equalsIncludingNaN(original, temp[i]) ?
                replacement :
                temp[i];
        }

        return temp;
    }
    /**
     * Remove the occurrence of a given value in a copied slice of array
     * defined by the array part from [begin, begin+length).
     * @param values the input array
     * @param begin start index of the array to include
     * @param length number of elements to include from begin
     * @param removedValue the value to be removed from the sliced array
     * @return the copy of the sliced array after removing the removedValue
     */
    private static double[] removeAndSlice(final double[] values,
                                           final int begin, final int length,
                                           final double removedValue) {
        MathArrays.verifyValues(values, begin, length);
        final double[] temp;
        // Indicates where the removedValue is located
        final BitSet bits = new BitSet(length);
        for (int i = begin; i < begin+length; i++) {
            if (Precision.equalsIncludingNaN(removedValue, values[i])) {
                bits.set(i - begin);
            }
        }
        // Check if empty then create a new copy
        if (bits.isEmpty()) {
            // Nothing removed, just copy
            temp = copyOf(values, begin, length);
        } else if(bits.cardinality() == length) {
            // All removed, just empty
            temp = new double[0];
        } else {
            // Some removable, so new
            temp = new double[length - bits.cardinality()];
            // Index from source array (i.e values)
            int start = begin;
            // Index in destination array(i.e temp)
            int dest = 0;
            // Index of bit set of next one
            int nextOne = -1;
            // Start index pointer of bitset
            int bitSetPtr = 0;
            while ((nextOne = bits.nextSetBit(bitSetPtr)) != -1) {
                final int lengthToCopy = nextOne - bitSetPtr;
                System.arraycopy(values, start, temp, dest, lengthToCopy);
                dest += lengthToCopy;
                start = begin + (bitSetPtr = bits.nextClearBit(nextOne));
            }
            // Copy any residue past start index till begin+length
            if (start < begin + length) {
                System.arraycopy(values,start,temp,dest,begin + length - start);
            }
        }
        return temp;
    }
    /**
     * Remove weights element if the corresponding data is equal to the given value.
     * in [begin, begin+length)
     *
     * @param values the input array
     * @param sampleWeights weights of the input array
     * @param begin start index of the array to include
     * @param length number of elements to include from begin
     * @param removedValue the value to be removed from the sliced array
     * @return the copy of the sliced array after removing weights
     */
    private static double[] removeAndSliceByRef(final double[] values,
                                               final double[] sampleWeights,
                                               final int begin, final int length,
                                               final double removedValue) {
        MathArrays.verifyValues(values, begin, length);
        final double[] temp;
        //BitSet(length) to indicate where the removedValue is located
        final BitSet bits = new BitSet(length);
        for (int i = begin; i < begin+length; i++) {
            if (Precision.equalsIncludingNaN(removedValue, values[i])) {
                bits.set(i - begin);
            }
        }
        //Check if empty then create a new copy
        if (bits.isEmpty()) {
            temp = copyOf(sampleWeights, begin, length); // Nothing removed, just copy
        } else if(bits.cardinality() == length) {
            temp = new double[0];                 // All removed, just empty
        }else {                                   // Some removable, so new
            temp = new double[length - bits.cardinality()];
            int start = begin;  //start index from source array (i.e sampleWeights)
            int dest = 0;       //dest index in destination array(i.e temp)
            int nextOne = -1;   //nextOne is the index of bit set of next one
            int bitSetPtr = 0;  //bitSetPtr is start index pointer of bitset
            while ((nextOne = bits.nextSetBit(bitSetPtr)) != -1) {
                final int lengthToCopy = nextOne - bitSetPtr;
                System.arraycopy(sampleWeights, start, temp, dest, lengthToCopy);
                dest += lengthToCopy;
                start = begin + (bitSetPtr = bits.nextClearBit(nextOne));
            }
            //Copy any residue past start index till begin+length
            if (start < begin + length) {
                System.arraycopy(sampleWeights,start,temp,dest,begin + length - start);
            }
        }
        return temp;
    }
    /**
     * Get pivots which is either cached or a newly created one.
     *
     * @param values array containing the input numbers
     * @return cached pivots or a newly created one
     */
    private int[] getPivots(final double[] values) {
        final int[] pivotsHeap;
        if (values == getDataRef()) {
            pivotsHeap = cachedPivots;
        } else {
            pivotsHeap = new int[PIVOTS_HEAP_LENGTH];
            Arrays.fill(pivotsHeap, -1);
        }
        return pivotsHeap;
    }

    /**
     * Get the estimation {@link EstimationType type} used for computation.
     *
     * @return the {@code estimationType} set
     */
    public EstimationType getEstimationType() {
        return estimationType;
    }

    /**
     * Build a new instance similar to the current one except for the
     * {@link EstimationType estimation type}.
     * <p>
     * This method is intended to be used as part of a fluent-type builder
     * pattern. Building finely tune instances should be done as follows:
     * </p>
     * <pre>
     *   Percentile customized = new Percentile(quantile).
     *                           withEstimationType(estimationType).
     *                           withNaNStrategy(nanStrategy).
     *                           withKthSelector(kthSelector);
     * </pre>
     * <p>
     * If any of the {@code withXxx} method is omitted, the default value for
     * the corresponding customization parameter will be used.
     * </p>
     * @param newEstimationType estimation type for the new instance.
     * Cannot be {@code null}.
     * @return a new instance, with changed estimation type
     */
    public Percentile withEstimationType(final EstimationType newEstimationType) {
        return new Percentile(quantile, newEstimationType, nanStrategy, kthSelector);
    }

    /**
     * Get the {@link NaNStrategy NaN Handling} strategy used for computation.
     * @return {@code NaN Handling} strategy set during construction
     */
    public NaNStrategy getNaNStrategy() {
        return nanStrategy;
    }

    /**
     * Build a new instance similar to the current one except for the
     * {@link NaNStrategy NaN handling} strategy.
     * <p>
     * This method is intended to be used as part of a fluent-type builder
     * pattern. Building finely tune instances should be done as follows:
     * </p>
     * <pre>
     *   Percentile customized = new Percentile(quantile).
     *                           withEstimationType(estimationType).
     *                           withNaNStrategy(nanStrategy).
     *                           withKthSelector(kthSelector);
     * </pre>
     * <p>
     * If any of the {@code withXxx} method is omitted, the default value for
     * the corresponding customization parameter will be used.
     * </p>
     * @param newNaNStrategy NaN strategy for the new instance.
     * Cannot be {@code null}.
     * @return a new instance, with changed NaN handling strategy
     */
    public Percentile withNaNStrategy(final NaNStrategy newNaNStrategy) {
        return new Percentile(quantile, estimationType, newNaNStrategy, kthSelector);
    }

    /**
     * Get the {@link KthSelector kthSelector} used for computation.
     * @return the {@code kthSelector} set
     */
    public KthSelector getKthSelector() {
        return kthSelector;
    }

    /**
     * Get the {@link PivotingStrategy} used in KthSelector for computation.
     * @return the pivoting strategy set
     */
    public PivotingStrategy getPivotingStrategy() {
        return kthSelector.getPivotingStrategy();
    }

    /**
     * Build a new instance similar to the current one except for the
     * {@link KthSelector kthSelector} instance specifically set.
     * <p>
     * This method is intended to be used as part of a fluent-type builder
     * pattern. Building finely tune instances should be done as follows:
     * </p>
     * <pre>
     *   Percentile customized = new Percentile(quantile).
     *                           withEstimationType(estimationType).
     *                           withNaNStrategy(nanStrategy).
     *                           withKthSelector(newKthSelector);
     * </pre>
     * <p>
     * If any of the {@code withXxx} method is omitted, the default value for
     * the corresponding customization parameter will be used.
     * </p>
     * @param newKthSelector KthSelector for the new instance.
     * Cannot be {@code null}.
     * @return a new instance, with changed KthSelector
     */
    public Percentile withKthSelector(final KthSelector newKthSelector) {
        return new Percentile(quantile, estimationType, nanStrategy, newKthSelector);
    }

    /**
     * An enum for various estimation strategies of a percentile referred in
     * <a href="http://en.wikipedia.org/wiki/Quantile">wikipedia on quantile</a>
     * with the names of enum matching those of types mentioned in
     * wikipedia.
     * <p>
     * Each enum corresponding to the specific type of estimation in wikipedia
     * implements  the respective formulae that specializes in the below aspects
     * <ul>
     * <li>An <b>index method</b> to calculate approximate index of the
     * estimate</li>
     * <li>An <b>estimate method</b> to estimate a value found at the earlier
     * computed index</li>
     * <li>A <b> minLimit</b> on the quantile for which first element of sorted
     * input is returned as an estimate </li>
     * <li>A <b> maxLimit</b> on the quantile for which last element of sorted
     * input is returned as an estimate </li>
     * </ul>
     * <p>
     * Users can now create {@link Percentile} by explicitly passing this enum;
     * such as by invoking {@link Percentile#withEstimationType(EstimationType)}
     * <p>
     * References:
     * <ol>
     * <li>
     * <a href="http://en.wikipedia.org/wiki/Quantile">Wikipedia on quantile</a>
     * </li>
     * <li>
     * <a href="https://www.amherst.edu/media/view/129116/.../Sample+Quantiles.pdf">
     * Hyndman, R. J. and Fan, Y. (1996) Sample quantiles in statistical
     * packages, American Statistician 50, 361–365</a> </li>
     * <li>
     * <a href="http://stat.ethz.ch/R-manual/R-devel/library/stats/html/quantile.html">
     * R-Manual </a></li>
     * </ol>
     */
    public enum EstimationType {
        /**
         * This is the default type used in the {@link Percentile}.This method.
         * has the following formulae for index and estimates<br>
         * \( \begin{align}
         * &amp;index    = (N+1)p\ \\
         * &amp;estimate = x_{\lceil h\,-\,1/2 \rceil} \\
         * &amp;minLimit = 0 \\
         * &amp;maxLimit = 1 \\
         * \end{align}\)
         */
        LEGACY("Legacy Apache Commons Math") {
            /**
             * {@inheritDoc}.This method in particular makes use of existing
             * Apache Commons Math style of picking up the index.
             */
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0d;
                final double maxLimit = 1d;
                return Double.compare(p, minLimit) == 0 ? 0 :
                       Double.compare(p, maxLimit) == 0 ?
                               length : p * (length + 1);
            }
            @Override
            public double evaluate(final double[] work, final double[] sampleWeights,
                                   final double p) {
                throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_OPERATION);
            }
        },
        /**
         * The method R_1 has the following formulae for index and estimates.<br>
         * \( \begin{align}
         * &amp;index= Np + 1/2\,  \\
         * &amp;estimate= x_{\lceil h\,-\,1/2 \rceil} \\
         * &amp;minLimit = 0 \\
         * \end{align}\)
         */
        R_1("R-1") {

            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0d;
                return Double.compare(p, minLimit) == 0 ? 0 : length * p + 0.5;
            }

            /**
             * {@inheritDoc}This method in particular for R_1 uses ceil(pos-0.5)
             */
            @Override
            protected double estimate(final double[] values,
                                      final int[] pivotsHeap, final double pos,
                                      final int length, final KthSelector selector) {
                return super.estimate(values, pivotsHeap, JdkMath.ceil(pos - 0.5), length, selector);
            }
            @Override
            public double evaluate(final double[] work, final double[] sampleWeights,
                                   final double p) {
                throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_OPERATION);
            }
        },
        /**
         * The method R_2 has the following formulae for index and estimates.<br>
         * \( \begin{align}
         * &amp;index= Np + 1/2\, \\
         * &amp;estimate=\frac{x_{\lceil h\,-\,1/2 \rceil} +
         * x_{\lfloor h\,+\,1/2 \rfloor}}{2} \\
         * &amp;minLimit = 0 \\
         * &amp;maxLimit = 1 \\
         * \end{align}\)
         */
        R_2("R-2") {

            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0d;
                final double maxLimit = 1d;
                return Double.compare(p, maxLimit) == 0 ? length :
                       Double.compare(p, minLimit) == 0 ? 0 : length * p + 0.5;
            }

            /**
             * {@inheritDoc}This method in particular for R_2 averages the
             * values at ceil(p+0.5) and floor(p-0.5).
             */
            @Override
            protected double estimate(final double[] values,
                                      final int[] pivotsHeap, final double pos,
                                      final int length, final KthSelector selector) {
                final double low =
                        super.estimate(values, pivotsHeap, JdkMath.ceil(pos - 0.5), length, selector);
                final double high =
                        super.estimate(values, pivotsHeap,JdkMath.floor(pos + 0.5), length, selector);
                return (low + high) / 2;
            }
            @Override
            public double evaluate(final double[] work, final double[] sampleWeights,
                                   final double p) {
                throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_OPERATION);
            }
        },
        /**
         * The method R_3 has the following formulae for index and estimates.<br>
         * \( \begin{align}
         * &amp;index= Np \\
         * &amp;estimate= x_{\lfloor h \rceil}\, \\
         * &amp;minLimit = 0.5/N \\
         * \end{align}\)
         */
        R_3("R-3") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 1d/2 / length;
                return Double.compare(p, minLimit) <= 0 ?
                        0 : JdkMath.rint(length * p);
            }
            @Override
            public double evaluate(final double[] work, final double[] sampleWeights,
                                   final double p) {
                throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_OPERATION);
            }
        },
        /**
         * The method R_4 has the following formulae for index and estimates.<br>
         * \( \begin{align}
         * &amp;index= Np\, \\
         * &amp;estimate= x_{\lfloor h \rfloor} + (h -
         * \lfloor h \rfloor) (x_{\lfloor h \rfloor + 1} - x_{\lfloor h
         * \rfloor}) \\
         * &amp;minLimit = 1/N \\
         * &amp;maxLimit = 1 \\
         * \end{align}\)
         */
        R_4("R-4") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 1d / length;
                final double maxLimit = 1d;
                return Double.compare(p, minLimit) < 0 ? 0 :
                       Double.compare(p, maxLimit) == 0 ? length : length * p;
            }
            @Override
            public double evaluate(final double[] work, final double[] sampleWeights,
                                   final double p) {
                throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_OPERATION);
            }
        },
        /**
         * The method R_5 has the following formulae for index and estimates.<br>
         * \( \begin{align}
         * &amp;index= Np + 1/2\\
         * &amp;estimate= x_{\lfloor h \rfloor} + (h -
         * \lfloor h \rfloor) (x_{\lfloor h \rfloor + 1} - x_{\lfloor h
         * \rfloor}) \\
         * &amp;minLimit = 0.5/N \\
         * &amp;maxLimit = (N-0.5)/N
         * \end{align}\)
         */
        R_5("R-5") {

            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 1d/2 / length;
                final double maxLimit = (length - 0.5) / length;
                return Double.compare(p, minLimit) < 0 ? 0 :
                       Double.compare(p, maxLimit) >= 0 ?
                               length : length * p + 0.5;
            }
            @Override
            public double evaluate(final double[] work, final double[] sampleWeights,
                                   final double p) {
                throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_OPERATION);
            }
        },
        /**
         * The method R_6 has the following formulae for index and estimates.<br>
         * \( \begin{align}
         * &amp;index= (N + 1)p \\
         * &amp;estimate= x_{\lfloor h \rfloor} + (h -
         * \lfloor h \rfloor) (x_{\lfloor h \rfloor + 1} - x_{\lfloor h
         * \rfloor}) \\
         * &amp;minLimit = 1/(N+1) \\
         * &amp;maxLimit = N/(N+1) \\
         * \end{align}\)
         * <p>
         * <b>Note:</b> This method computes the index in a manner very close to
         * the default Commons Math Percentile existing implementation. However
         * the difference to be noted is in picking up the limits with which
         * first element (p&lt;1(N+1)) and last elements (p&gt;N/(N+1))are done.
         * While in default case; these are done with p=0 and p=1 respectively.
         */
        R_6("R-6") {

            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 1d / (length + 1);
                final double maxLimit = 1d * length / (length + 1);
                return Double.compare(p, minLimit) < 0 ? 0 :
                       Double.compare(p, maxLimit) >= 0 ?
                               length : (length + 1) * p;
            }
            @Override
            public double evaluate(final double[] work, final double[] sampleWeights,
                                   final double p) {
                throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_OPERATION);
            }
        },

        /**
         * The method R_7 implements Microsoft Excel style computation has the
         * following formulae for index and estimates.<br>
         * \( \begin{align}
         * &amp;index = (N-1)p + 1 \\
         * &amp;estimate = x_{\lfloor h \rfloor} + (h -
         * \lfloor h \rfloor) (x_{\lfloor h \rfloor + 1} - x_{\lfloor h
         * \rfloor}) \\
         * &amp;minLimit = 0 \\
         * &amp;maxLimit = 1 \\
         * \end{align}\)
         * The formula to evaluate weighted percentiles is as following.<br>
         * \( \begin{align}
         * &amp;S_k = (k-1)w_k + (n-1)\sum_{i=1}^{k-1}w_i
         * &amp;Then find k s.t. \frac{S_k}{S_n}\leq p \leq \frac{S_{k+1}}{S_n}
         * \end{align}\)
         */
        R_7("R-7") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0d;
                final double maxLimit = 1d;
                return Double.compare(p, minLimit) == 0 ? 0 :
                       Double.compare(p, maxLimit) == 0 ?
                               length : 1 + (length - 1) * p;
            }

            @Override
            public double evaluate(final double[] work, final double[] sampleWeights,
                                   final double p) {
                SortInPlace.ASCENDING.apply(work, sampleWeights);
                double[] sk = new double[work.length];
                for(int k = 0; k < work.length; k++) {
                    sk[k] = 0;
                    for (int j = 0; j < k; j++) {
                        sk[k] += sampleWeights[j];
                    }
                    sk[k] = k * sampleWeights[k] + (work.length - 1) * sk[k];
                }

                double qsn = (p / 100) * sk[sk.length-1];
                int k = searchSk(qsn, sk, 0, work.length - 1);

                double ret;
                if (qsn == sk[k] && k == work.length - 1) {
                    ret = work[k] - (work[k] - work[k-1]) * (1 - (qsn - sk[k]) / (sk[k] - sk[k-1]));
                } else {
                    ret = work[k] + (work[k+1] - work[k]) * (qsn - sk[k]) / (sk[k+1] - sk[k]);
                }
                return ret;
            }
        },

        /**
         * The method R_8 has the following formulae for index and estimates.<br>
         * \( \begin{align}
         * &amp;index = (N + 1/3)p + 1/3  \\
         * &amp;estimate = x_{\lfloor h \rfloor} + (h -
           \lfloor h \rfloor) (x_{\lfloor h \rfloor + 1} - x_{\lfloor h
         * \rfloor}) \\
         * &amp;minLimit = (2/3)/(N+1/3) \\
         * &amp;maxLimit = (N-1/3)/(N+1/3) \\
         * \end{align}\)
         * <p>
         * As per Ref [2,3] this approach is most recommended as it provides
         * an approximate median-unbiased estimate regardless of distribution.
         */
        R_8("R-8") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 2 * (1d / 3) / (length + 1d / 3);
                final double maxLimit =
                        (length - 1d / 3) / (length + 1d / 3);
                return Double.compare(p, minLimit) < 0 ? 0 :
                       Double.compare(p, maxLimit) >= 0 ? length :
                           (length + 1d / 3) * p + 1d / 3;
            }
            @Override
            public double evaluate(final double[] work, final double[] sampleWeights,
                                   final double p) {
                throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_OPERATION);
            }
        },

        /**
         * The method R_9 has the following formulae for index and estimates.<br>
         * \( \begin{align}
         * &amp;index = (N + 1/4)p + 3/8\\
         * &amp;estimate = x_{\lfloor h \rfloor} + (h -
           \lfloor h \rfloor) (x_{\lfloor h \rfloor + 1} - x_{\lfloor h
         * \rfloor}) \\
         * &amp;minLimit = (5/8)/(N+1/4) \\
         * &amp;maxLimit = (N-3/8)/(N+1/4) \\
         * \end{align}\)
         */
        R_9("R-9") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 5d/8 / (length + 0.25);
                final double maxLimit = (length - 3d/8) / (length + 0.25);
                return Double.compare(p, minLimit) < 0 ? 0 :
                       Double.compare(p, maxLimit) >= 0 ? length :
                               (length + 0.25) * p + 3d/8;
            }
            @Override
            public double evaluate(final double[] work, final double[] sampleWeights,
                                   final double p) {
                throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_OPERATION);
            }
        },
        ;

        /** Simple name such as R-1, R-2 corresponding to those in wikipedia. */
        private final String name;

        /**
         * Constructor.
         *
         * @param type name of estimation type as per wikipedia
         */
        EstimationType(final String type) {
            this.name = type;
        }

        /**
         * Finds the index of array that can be used as starting index to
         * {@link #estimate(double[], int[], double, int, KthSelector) estimate}
         * percentile. The calculation of index calculation is specific to each
         * {@link EstimationType}.
         *
         * @param p the p<sup>th</sup> quantile
         * @param length the total number of array elements in the work array
         * @return a computed real valued index as explained in the wikipedia
         */
        protected abstract double index(double p, int length);

        /**
         * Estimation based on K<sup>th</sup> selection. This may be overridden
         * in specific enums to compute slightly different estimations.
         *
         * @param work array of numbers to be used for finding the percentile
         * @param pos indicated positional index prior computed from calling
         *            {@link #index(double, int)}
         * @param pivotsHeap an earlier populated cache if exists; will be used
         * @param length size of array considered
         * @param selector a {@link KthSelector} used for pivoting during search
         * @return estimated percentile
         */
        protected double estimate(final double[] work, final int[] pivotsHeap,
                                  final double pos, final int length,
                                  final KthSelector selector) {

            final double fpos = JdkMath.floor(pos);
            final int intPos = (int) fpos;
            final double dif = pos - fpos;

            if (pos < 1) {
                return selector.select(work, pivotsHeap, 0);
            }
            if (pos >= length) {
                return selector.select(work, pivotsHeap, length - 1);
            }

            final double lower = selector.select(work, pivotsHeap, intPos - 1);
            final double upper = selector.select(work, pivotsHeap, intPos);
            return lower + dif * (upper - lower);
        }

        /**
         * Evaluate method to compute the percentile for a given bounded array
         * using earlier computed pivots heap.<br>
         * This basically calls the {@link #index(double, int) index} and then
         * {@link #estimate(double[], int[], double, int, KthSelector) estimate}
         * functions to return the estimated percentile value.
         *
         * @param work array of numbers to be used for finding the percentile.
         * Cannot be {@code null}.
         * @param pivotsHeap a prior cached heap which can speed up estimation
         * @param p the p<sup>th</sup> quantile to be computed
         * @param selector a {@link KthSelector} used for pivoting during search
         * @return estimated percentile
         * @throws OutOfRangeException if p is out of range
         */
        protected double evaluate(final double[] work, final int[] pivotsHeap, final double p,
                                  final KthSelector selector) {
            NullArgumentException.check(work);
            if (p > 100 || p <= 0) {
                throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE,
                                              p, 0, 100);
            }
            return estimate(work, pivotsHeap, index(p/100d, work.length), work.length, selector);
        }

        /**
         * Evaluate method to compute the percentile for a given bounded array.
         * This basically calls the {@link #index(double, int) index} and then
         * {@link #estimate(double[], int[], double, int, KthSelector) estimate}
         * functions to return the estimated percentile value. Please
         * note that this method does not make use of cached pivots.
         *
         * @param work array of numbers to be used for finding the percentile.
         * Cannot be {@code null}.
         * @param p the p<sup>th</sup> quantile to be computed
         * @return estimated percentile
         * @param selector a {@link KthSelector} used for pivoting during search
         * @throws OutOfRangeException if length or p is out of range
         */
        public double evaluate(final double[] work, final double p, final KthSelector selector) {
            return this.evaluate(work, null, p, selector);
        }
        /**
         * Evaluate weighted percentile by estimation rule specified in {@link EstimationType}.
         * @param work array of numbers to be used for finding the percentile
         * @param sampleWeights the corresponding weights of data in work
         * @param p the p<sup>th</sup> quantile to be computed
         * @return estimated weighted percentile
         * @throws MathIllegalArgumentException if weighted percentile is not supported by the current estimationType
         */
        public abstract double evaluate(double[] work, double[] sampleWeights,
                                        double p);
        /**
         * Search the interval q*sn locates in.
         * @param qsn q*sn, where n refers to the data size
         * @param sk the cumulative weights array
         * @param lo start position to search qsn
         * @param hi end position to search qsn
         * @return the index of lower bound qsn locates in
         */
        private static int searchSk(double qsn, double[] sk, int lo, int hi) {
            if (sk.length == 1) {
                return 0;
            }
            if (hi - lo == 1) {
                if (qsn == sk[hi]) {
                    return hi;
                }
                return lo;
            } else {
                int mid = (lo + hi) >>> 1;
                if (qsn == sk[mid]) {
                  return mid;
                } else if (qsn > sk[mid]) {
                    return searchSk(qsn, sk, mid, hi);
                } else {
                    return searchSk(qsn, sk, lo, mid);
                }
            }
        }
        /**
         * Gets the name of the enum.
         *
         * @return the name
         */
        String getName() {
            return name;
        }
    }
}
