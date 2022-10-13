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
package org.apache.commons.math4.legacy.stat.descriptive;

import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.numbers.core.Precision;

/**
 * Abstract base class for implementations of the
 * {@link StorelessUnivariateStatistic} interface.
 * <p>
 * Provides default {@code evaluate(double[],...)} and {@code incrementAll(double[])}
 * implementations.
 * <p>
 * <strong>Note that these implementations are not synchronized.</strong>
 */
public abstract class AbstractStorelessUnivariateStatistic
    implements StorelessUnivariateStatistic {

    /**
     * This default implementation creates a copy of this {@link StorelessUnivariateStatistic}
     * instance, calls {@link #clear} on it, then calls {@link #incrementAll} with the specified
     * portion of the input array, and then uses {@link #getResult} to compute the return value.
     * <p>
     * Note that this implementation does not change the internal state of the statistic.
     * <p>
     * Implementations may override this method with a more efficient and possibly more
     * accurate implementation that works directly with the input array.
     * <p>
     * If the array is null, a MathIllegalArgumentException is thrown.
     *
     * @param values input array
     * @return the value of the statistic applied to the input array
     * @throws MathIllegalArgumentException if values is null
     * @see org.apache.commons.math4.legacy.stat.descriptive.UnivariateStatistic#evaluate(double[])
     */
    @Override
    public double evaluate(final double[] values) throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
        }
        return evaluate(values, 0, values.length);
    }

    /**
     * This default implementation creates a copy of this {@link StorelessUnivariateStatistic}
     * instance, calls {@link #clear} on it, then calls {@link #incrementAll} with the specified
     * portion of the input array, and then uses {@link #getResult} to compute the return value.
     * <p>
     * Note that this implementation does not change the internal state of the statistic.
     * <p>
     * Implementations may override this method with a more efficient and possibly more
     * accurate implementation that works directly with the input array.
     * <p>
     * If the array is null or the index parameters are not valid, an
     * MathIllegalArgumentException is thrown.
     *
     * @param values the input array
     * @param begin the index of the first element to include
     * @param length the number of elements to include
     * @return the value of the statistic applied to the included array entries
     * @throws MathIllegalArgumentException if the array is null or the indices are not valid
     * @see org.apache.commons.math4.legacy.stat.descriptive.UnivariateStatistic#evaluate(double[], int, int)
     */
    @Override
    public double evaluate(final double[] values, final int begin, final int length)
        throws MathIllegalArgumentException {

        if (MathArrays.verifyValues(values, begin, length)) {
            final StorelessUnivariateStatistic stat = copy();
            stat.clear();
            stat.incrementAll(values, begin, length);
            return stat.getResult();
        }
        return Double.NaN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract StorelessUnivariateStatistic copy();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void clear();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract double getResult();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void increment(double d);

    /**
     * This default implementation just calls {@link #increment} in a loop over
     * the input array.
     * <p>
     * Throws IllegalArgumentException if the input values array is null.
     *
     * @param values values to add
     * @throws MathIllegalArgumentException if values is null
     * @see StorelessUnivariateStatistic#incrementAll(double[])
     */
    @Override
    public void incrementAll(double[] values) throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
        }
        incrementAll(values, 0, values.length);
    }

    /**
     * This default implementation just calls {@link #increment} in a loop over
     * the specified portion of the input array.
     * <p>
     * Throws IllegalArgumentException if the input values array is null.
     *
     * @param values  array holding values to add
     * @param begin   index of the first array element to add
     * @param length  number of array elements to add
     * @throws MathIllegalArgumentException if values is null
     * @see StorelessUnivariateStatistic#incrementAll(double[], int, int)
     */
    @Override
    public void incrementAll(double[] values, int begin, int length) throws MathIllegalArgumentException {
        if (MathArrays.verifyValues(values, begin, length)) {
            int k = begin + length;
            for (int i = begin; i < k; i++) {
                increment(values[i]);
            }
        }
    }

    /**
     * Returns true iff <code>object</code> is the same type of
     * {@link StorelessUnivariateStatistic} (the object's class equals this
     * instance) returning the same values as this for <code>getResult()</code>
     * and <code>getN()</code>.
     *
     * @param object object to test equality against.
     * @return true if object returns the same value as this
     */
    @Override
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }
        StorelessUnivariateStatistic stat = (StorelessUnivariateStatistic) object;
        return Precision.equalsIncludingNaN(stat.getResult(), this.getResult()) &&
               Precision.equalsIncludingNaN(stat.getN(), this.getN());
    }

    /**
     * Returns hash code based on getResult() and getN().
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return 31 * (31 + Double.hashCode(getResult())) + Double.hashCode(getN());
    }
}
