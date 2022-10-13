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
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.core.MathArrays;

/**
 * Abstract base class for implementations of the {@link UnivariateStatistic} interface.
 * <p>
 * Provides a default implementation of <code>evaluate(double[]),</code>
 * delegating to <code>evaluate(double[], int, int)</code> in the natural way.
 */
public abstract class AbstractUnivariateStatistic
    implements UnivariateStatistic {

    /** Stored data. */
    private double[] storedData;

    /**
     * {@inheritDoc}
     */
    @Override
    public double evaluate(final double[] values) throws MathIllegalArgumentException {
        MathArrays.verifyValues(values, 0, 0);
        return evaluate(values, 0, values.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract double evaluate(double[] values, int begin, int length)
        throws MathIllegalArgumentException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract UnivariateStatistic copy();

    /**
     * Set the data array.
     * <p>
     * The stored value is a copy of the parameter array, not the array itself.
     * </p>
     * @param values data array to store (may be null to remove stored data)
     * @see #evaluate()
     */
    public void setData(final double[] values) {
        storedData = (values == null) ? null : values.clone();
    }

    /**
     * Get a copy of the stored data array.
     * @return copy of the stored data array (may be null)
     */
    public double[] getData() {
        return (storedData == null) ? null : storedData.clone();
    }

    /**
     * Get a reference to the stored data array.
     * @return reference to the stored data array (may be null)
     */
    protected double[] getDataRef() {
        return storedData;
    }

    /**
     * Set the data array.  The input array is copied, not referenced.
     *
     * @param values data array to store
     * @param begin the index of the first element to include
     * @param length the number of elements to include
     * @throws MathIllegalArgumentException if values is null or the indices
     * are not valid
     * @see #evaluate()
     */
    public void setData(final double[] values, final int begin, final int length)
            throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
        }

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
        storedData = new double[length];
        System.arraycopy(values, begin, storedData, 0, length);
    }

    /**
     * Returns the result of evaluating the statistic over the stored data.
     * <p>
     * The stored array is the one which was set by previous calls to {@link #setData(double[])}.
     * </p>
     * @return the value of the statistic applied to the stored data
     * @throws MathIllegalArgumentException if the stored data array is null
     */
    public double evaluate() throws MathIllegalArgumentException {
        return evaluate(storedData);
    }
}
