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
package org.apache.commons.math3.linear;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

/**
 * Interface defining a field-valued vector with basic algebraic operations.
 * <p>
 * vector element indexing is 0-based -- e.g., <code>getEntry(0)</code>
 * returns the first element of the vector.
 * </p>
 * <p>
 * The various <code>mapXxx</code> and <code>mapXxxToSelf</code> methods operate
 * on vectors element-wise, i.e. they perform the same operation (adding a scalar,
 * applying a function ...) on each element in turn. The <code>mapXxx</code>
 * versions create a new vector to hold the result and do not change the instance.
 * The <code>mapXxxToSelf</code> versions use the instance itself to store the
 * results, so the instance is changed by these methods. In both cases, the result
 * vector is returned by the methods, this allows to use the <i>fluent API</i>
 * style, like this:
 * </p>
 * <pre>
 *   RealVector result = v.mapAddToSelf(3.0).mapTanToSelf().mapSquareToSelf();
 * </pre>
 *
 * @param <T> the type of the field elements
 * @version $Id$
 * @since 2.0
 */
public interface FieldVector<T extends FieldElement<T>>  {

    /**
     * Get the type of field elements of the vector.
     * @return type of field elements of the vector
     */
    Field<T> getField();

    /**
     * Returns a (deep) copy of this.
     * @return vector copy
     */
    FieldVector<T> copy();

    /**
     * Compute the sum of {@code this} and {@code v}. Implementations should throw
     * {@link org.apache.commons.math3.exception.DimensionMismatchException} if
     * {@code v} is not the same size as {@code this}.
     *
     * @param v vector to be added
     * @return {@code this + v}
     */
    FieldVector<T> add(FieldVector<T> v);

    /**
     * Compute {@code this} minus {@code v}. Implementations should throw
     * {@link org.apache.commons.math3.exception.DimensionMismatchException} if
     * {@code v} is not the same size as {@code this}
     *
     * @param v vector to be subtracted
     * @return {@code this + v}
     */
    FieldVector<T> subtract(FieldVector<T> v);

    /**
     * Map an addition operation to each entry. Implementations should throw
     * {@link org.apache.commons.math3.exception.NullArgumentException} if
     * {@code d} is {@code null}.
     *
     * @param d value to be added to each entry
     * @return {@code this + d}
     */
    FieldVector<T> mapAdd(T d);

    /**
     * Map an addition operation to each entry. The instance <strong>is</strong>
     * changed by this method. Implementations should throw
     * {@link org.apache.commons.math3.exception.NullArgumentException} if
     * {@code d} is {@code null}.
     *
     * @param d value to be added to each entry
     * @return for convenience, return {@code this}
     */
    FieldVector<T> mapAddToSelf(T d);

    /**
     * Map a subtraction operation to each entry. Implementations should throw
     * {@link org.apache.commons.math3.exception.NullArgumentException} if
     * {@code d} is {@code null}.
     *
     * @param d value to be subtracted to each entry
     * @return {@code this - d}
     */
    FieldVector<T> mapSubtract(T d);

    /**
     * Map a subtraction operation to each entry. The instance
     * <strong>is</strong> changed by this method. Implementations should throw
     * {@link org.apache.commons.math3.exception.NullArgumentException} if
     * {@code d} is {@code null}.
     *
     * @param d value to be subtracted to each entry
     * @return for convenience, return {@code this}
     */
    FieldVector<T> mapSubtractToSelf(T d);

    /**
     * Map a multiplication operation to each entry. Implementations should throw
     * {@link org.apache.commons.math3.exception.NullArgumentException} if
     * {@code d} is {@code null}.
     *
     * @param d value to multiply all entries by
     * @return {@code this * d}
     */
    FieldVector<T> mapMultiply(T d);

    /**
     * Map a multiplication operation to each entry. The instance
     * <strong>is</strong> changed by this method. Implementations should throw
     * {@link org.apache.commons.math3.exception.NullArgumentException} if
     * {@code d} is {@code null}.
     *
     * @param d value to multiply all entries by
     * @return for convenience, return {@code this}
     */
    FieldVector<T> mapMultiplyToSelf(T d);

    /**
     * Map a division operation to each entry. Implementations should throw
     * <ul>
     * <li>{@link org.apache.commons.math3.exception.NullArgumentException} if
     *     {@code d} is {@code null},</li>
     * <li>{@link org.apache.commons.math3.exception.MathArithmeticException} if
     *     {@code d} is zero.</li>
     * </ul>
     *
     * @param d value to divide all entries by
     * @return {@code this / d}
     */
    FieldVector<T> mapDivide(T d);

    /**
     * Map a division operation to each entry. The instance <strong>is</strong>
     * changed by this method.
     * <ul>
     * <li>{@link org.apache.commons.math3.exception.NullArgumentException} if
     *     {@code d} is {@code null},</li>
     * <li>{@link org.apache.commons.math3.exception.MathArithmeticException} if
     *     {@code d} is zero.</li>
     * </ul>
     *
     * @param d value to divide all entries by
     * @return for convenience, return {@code this}
     */
    FieldVector<T> mapDivideToSelf(T d);

    /**
     * Map the 1/x function to each entry. Implementations should throw
     * {@link org.apache.commons.math3.exception.MathArithmeticException} if
     * one of the entries is zero.
     *
     * @return a vector containing the result of applying the function to each
     * entry.
     */
    FieldVector<T> mapInv();

    /**
     * Map the 1/x function to each entry. The instance <strong>is</strong>
     * changed by this method. Implementations should throw
     * {@link org.apache.commons.math3.exception.MathArithmeticException} if
     * one of the entries is zero.
     *
     * @return for convenience, return {@code this}
     */
    FieldVector<T> mapInvToSelf();

    /**
     * Element-by-element multiplication. Implementations should throw
     * {@link org.apache.commons.math3.exception.DimensionMismatchException} if
     * {@code v} is not the same size as {@code this}.
     *
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing {@code this[i] * v[i]} for all {@code i}
     */
    FieldVector<T> ebeMultiply(FieldVector<T> v);

    /**
     * Element-by-element division. Implementations should throw
     * <ul>
     * <li>{@link org.apache.commons.math3.exception.DimensionMismatchException}
     *     if {@code v} is not the same size as {@code this},</li>
     * <li>{@link org.apache.commons.math3.exception.MathArithmeticException}
     *     if one entry of {@code v} is zero.
     * </li>
     * </ul>
     *
     * @param v vector by which instance elements must be divided
     * @return a vector containing {@code this[i] / v[i]} for all {@code i}
     */
    FieldVector<T> ebeDivide(FieldVector<T> v);

    /**
     * Returns vector entries as a T array.
     * @return T array of entries
     */
     T[] getData();

    /**
     * Compute the dot product. Implementations should throw
     * {@link org.apache.commons.math3.exception.DimensionMismatchException} if
     * {@code v} is not the same size as {@code this}.
     *
     * @param v vector with which dot product should be computed
     * @return the scalar dot product of {@code this} and {@code v}
     */
    T dotProduct(FieldVector<T> v);

    /**
     * Find the orthogonal projection of this vector onto another vector.
     * Implementations should throw
     * <ul>
     * <li>{@link org.apache.commons.math3.exception.DimensionMismatchException}
     *     if {@code v} is not the same size as {@code this},</li>
     * <li>{@link org.apache.commons.math3.exception.MathArithmeticException}
     *     if {@code v} is the null vector.
     * </li>
     * </ul>
     *
     * @param v vector onto which {@code this} must be projected
     * @return projection of {@code this} onto {@code v}
     */
    FieldVector<T> projection(FieldVector<T> v);

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the matrix outer product between instance and v
     */
    FieldMatrix<T> outerProduct(FieldVector<T> v);

    /**
     * Returns the entry in the specified index. Implementations should throw
     * {@link org.apache.commons.math3.exception.OutOfRangeException} if the
     * index is not valid.
     *
     * @param index Index location of entry to be fetched.
     * @return the vector entry at {@code index}.
     * @see #setEntry(int, FieldElement)
     */
    T getEntry(int index);

    /**
     * Set a single element. Implementations should throw
     * <ul>
     * <li>{@link org.apache.commons.math3.exception.OutOfRangeException} if the
     *     index is not valid,</li>
     * <li>{@link org.apache.commons.math3.exception.NullArgumentException} if
     *     the value is {@code null}.</li>
     * </ul>
     *
     * @param index element index.
     * @param value new value for the element.
     * @see #getEntry(int)
     */
    void setEntry(int index, T value);

    /**
     * Returns the size of the vector.
     * @return size
     */
    int getDimension();

    /**
     * Construct a vector by appending a vector to this vector.
     * @param v vector to append to this one.
     * @return a new vector
     */
    FieldVector<T> append(FieldVector<T> v);

    /**
     * Construct a vector by appending a T to this vector.
     * @param d T to append.
     * @return a new vector
     */
    FieldVector<T> append(T d);

    /**
     * Get a subvector from consecutive elements. Implementations should throw
     * <ul>
     * <li>{@link org.apache.commons.math3.exception.OutOfRangeException} if the
     *     index is not valid,</li>
     * <li>{@link org.apache.commons.math3.exception.NotPositiveException} if
     *     the number of elements is not positive.</li>
     * </ul>
     *
     * @param index index of first element.
     * @param n number of elements to be retrieved.
     * @return a vector containing n elements.
     */
    FieldVector<T> getSubVector(int index, int n);

    /**
     * Set a set of consecutive elements. Implementations should throw
     * {@link org.apache.commons.math3.exception.OutOfRangeException} if the
     * index is not valid.
     *
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     */
    void setSubVector(int index, FieldVector<T> v);

    /**
     * Set all elements to a single value.
     * @param value single value to set for all elements
     */
    void set(T value);

    /**
     * Convert the vector to a T array.
     * <p>The array is independent from vector data, it's elements
     * are copied.</p>
     * @return array containing a copy of vector elements
     */
    T[] toArray();

}
