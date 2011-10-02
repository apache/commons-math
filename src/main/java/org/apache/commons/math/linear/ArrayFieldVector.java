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
package org.apache.commons.math.linear;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * This class implements the {@link FieldVector} interface with a {@link FieldElement} array.
 * @param <T> the type of the field elements
 * @version $Id$
 * @since 2.0
 */
public class ArrayFieldVector<T extends FieldElement<T>> implements FieldVector<T>, Serializable {
    /** Serializable version identifier. */
    private static final long serialVersionUID = 7648186910365927050L;
    /** Entries of the vector. */
    protected T[] data;
    /** Field to which the elements belong. */
    private final Field<T> field;

    /**
     * Build a 0-length vector.
     * Zero-length vectors may be used to initialized construction of vectors
     * by data gathering. We start with zero-length and use either the {@link
     * #ArrayFieldVector(ArrayFieldVector, ArrayFieldVector)} constructor
     * or one of the {@code append} methods ({@link #add(FieldVector)} or
     * {@link #append(ArrayFieldVector)}) to gather data into this vector.
     *
     * @param field field to which the elements belong
     */
    public ArrayFieldVector(final Field<T> field) {
        this(field, 0);
    }

    /**
     * Construct a vector of zeroes.
     *
     * @param field Field to which the elements belong.
     * @param size Size of the vector.
     */
    public ArrayFieldVector(Field<T> field, int size) {
        this.field = field;
        data = buildArray(size);
        Arrays.fill(data, field.getZero());
    }

    /**
     * Construct a vector with preset values.
     *
     * @param size Size of the vector.
     * @param preset All entries will be set with this value.
     */
    public ArrayFieldVector(int size, T preset) {
        this(preset.getField(), size);
        Arrays.fill(data, preset);
    }

    /**
     * Construct a vector from an array, copying the input array.
     * This constructor needs a non-empty {@code d} array to retrieve
     * the field from its first element. This implies it cannot build
     * 0 length vectors. To build vectors from any size, one should
     * use the {@link #ArrayFieldVector(Field, FieldElement[])} constructor.
     *
     * @param d Array.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @throws ZeroException if {@code d} is empty.
     * @see #ArrayFieldVector(Field, FieldElement[])
     */
    public ArrayFieldVector(T[] d) {
        if (d == null) {
            throw new NullArgumentException();
        }
        try {
            field = d[0].getField();
            data = d.clone();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
        }
    }

    /**
     * Construct a vector from an array, copying the input array.
     *
     * @param field Field to which the elements belong.
     * @param d Array.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @see #ArrayFieldVector(FieldElement[])
     */
    public ArrayFieldVector(Field<T> field, T[] d) {
        if (d == null) {
            throw new NullArgumentException();
        }
        this.field = field;
        data = d.clone();
    }

    /**
     * Create a new ArrayFieldVector using the input array as the underlying
     * data array.
     * If an array is built specially in order to be embedded in a
     * ArrayFieldVector and not used directly, the {@code copyArray} may be
     * set to {@code false}. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.
     * This constructor needs a non-empty {@code d} array to retrieve
     * the field from its first element. This implies it cannot build
     * 0 length vectors. To build vectors from any size, one should
     * use the {@link #ArrayFieldVector(Field, FieldElement[], boolean)}
     * constructor.
     *
     * @param d Data for the new vector.
     * @param copyArray If {@code true}, the input array will be copied,
     * otherwise it will be referenced.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @throws ZeroException if {@code d} is empty.
     * @see #ArrayFieldVector(FieldElement[])
     * @see #ArrayFieldVector(Field, FieldElement[], boolean)
     */
    public ArrayFieldVector(T[] d, boolean copyArray) {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
        }
        field = d[0].getField();
        data = copyArray ? d.clone() :  d;
    }

    /**
     * Create a new ArrayFieldVector using the input array as the underlying
     * data array.
     * If an array is built specially in order to be embedded in a
     * ArrayFieldVector and not used directly, the {@code copyArray} may be
     * set to {@code false}. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.
     *
     * @param field Field to which the elements belong.
     * @param d Data for the new vector.
     * @param copyArray If {@code true}, the input array will be copied,
     * otherwise it will be referenced.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @see #ArrayFieldVector(FieldElement[], boolean)
     */
    public ArrayFieldVector(Field<T> field, T[] d, boolean copyArray) {
        if (d == null) {
            throw new NullArgumentException();
        }
        this.field = field;
        data = copyArray ? d.clone() :  d;
    }

    /**
     * Construct a vector from part of a array.
     *
     * @param d Array.
     * @param pos Position of the first entry.
     * @param size Number of entries to copy.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @throws NumberIsTooLargeException if the size of {@code d} is less
     * than {@code pos + size}.
     */
    public ArrayFieldVector(T[] d, int pos, int size) {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        field = d[0].getField();
        data = buildArray(size);
        System.arraycopy(d, pos, data, 0, size);
    }

    /**
     * Construct a vector from part of a array.
     *
     * @param field Field to which the elements belong.
     * @param d Array.
     * @param pos Position of the first entry.
     * @param size Number of entries to copy.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @throws NumberIsTooLargeException if the size of {@code d} is less
     * than {@code pos + size}.
     */
    public ArrayFieldVector(Field<T> field, T[] d, int pos, int size) {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        this.field = field;
        data = buildArray(size);
        System.arraycopy(d, pos, data, 0, size);
    }

    /**
     * Construct a vector from another vector, using a deep copy.
     *
     * @param v Vector to copy.
     * @throws NullArgumentException if {@code v} is {@code null}.
     */
    public ArrayFieldVector(FieldVector<T> v) {
        if (v == null) {
            throw new NullArgumentException();
        }
        field = v.getField();
        data = buildArray(v.getDimension());
        for (int i = 0; i < data.length; ++i) {
            data[i] = v.getEntry(i);
        }
    }

    /**
     * Construct a vector from another vector, using a deep copy.
     *
     * @param v Vector to copy.
     * @throws NullArgumentException if {@code v} is {@code null}.
     */
    public ArrayFieldVector(ArrayFieldVector<T> v) {
        if (v == null) {
            throw new NullArgumentException();
        }
        field = v.getField();
        data = v.data.clone();
    }

    /**
     * Construct a vector from another vector.
     *
     * @param v Vector to copy.
     * @param deep If {@code true} perform a deep copy, otherwise perform
     * a shallow copy
     * @throws NullArgumentException if {@code v} is {@code null}.
     */
    public ArrayFieldVector(ArrayFieldVector<T> v, boolean deep) {
        if (v == null) {
            throw new NullArgumentException();
        }
        field = v.getField();
        data = deep ? v.data.clone() : v.data;
    }

    /**
     * Construct a vector by appending one vector to another vector.
     *
     * @param v1 First vector (will be put in front of the new vector).
     * @param v2 Second vector (will be put at back of the new vector).
     * @throws NullArgumentException if {@code v1} or {@code v2} is
     * {@code null}.
     */
    public ArrayFieldVector(ArrayFieldVector<T> v1, ArrayFieldVector<T> v2) {
        if (v1 == null ||
            v2 == null) {
            throw new NullArgumentException();
        }
        field = v1.getField();
        data = buildArray(v1.data.length + v2.data.length);
        System.arraycopy(v1.data, 0, data, 0, v1.data.length);
        System.arraycopy(v2.data, 0, data, v1.data.length, v2.data.length);
    }

    /**
     * Construct a vector by appending one vector to another vector.
     *
     * @param v1 First vector (will be put in front of the new vector).
     * @param v2 Second vector (will be put at back of the new vector).
     * @throws NullArgumentException if {@code v1} or {@code v2} is
     * {@code null}.
     */
    public ArrayFieldVector(ArrayFieldVector<T> v1, T[] v2) {
        if (v1 == null ||
            v2 == null) {
            throw new NullArgumentException();
        }
        field = v1.getField();
        data = buildArray(v1.data.length + v2.length);
        System.arraycopy(v1.data, 0, data, 0, v1.data.length);
        System.arraycopy(v2, 0, data, v1.data.length, v2.length);
    }

    /**
     * Construct a vector by appending one vector to another vector.
     *
     * @param v1 First vector (will be put in front of the new vector).
     * @param v2 Second vector (will be put at back of the new vector).
     * @throws NullArgumentException if {@code v1} or {@code v2} is
     * {@code null}.
     */
    public ArrayFieldVector(T[] v1, ArrayFieldVector<T> v2) {
        if (v1 == null ||
            v2 == null) {
            throw new NullArgumentException();
        }
        field = v2.getField();
        data = buildArray(v1.length + v2.data.length);
        System.arraycopy(v1, 0, data, 0, v1.length);
        System.arraycopy(v2.data, 0, data, v1.length, v2.data.length);
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * This constructor needs at least one non-empty array to retrieve
     * the field from its first element. This implies it cannot build
     * 0 length vectors. To build vectors from any size, one should
     * use the {@link #ArrayFieldVector(Field, FieldElement[], FieldElement[])}
     * constructor.
     *
     * @param v1 First vector (will be put in front of the new vector).
     * @param v2 Second vector (will be put at back of the new vector).
     * @throws NullArgumentException if {@code v1} or {@code v2} is
     * {@code null}.
     * @throws ZeroException if both arrays are empty.
     * @see #ArrayFieldVector(Field, FieldElement[], FieldElement[])
     */
    public ArrayFieldVector(T[] v1, T[] v2) {
        if (v1 == null ||
            v2 == null) {
            throw new NullArgumentException();
        }
        if (v1.length + v2.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
        }
        data = buildArray(v1.length + v2.length);
        System.arraycopy(v1, 0, data, 0, v1.length);
        System.arraycopy(v2, 0, data, v1.length, v2.length);
        field = data[0].getField();
    }

    /**
     * Construct a vector by appending one vector to another vector.
     *
     * @param field Field to which the elements belong.
     * @param v1 First vector (will be put in front of the new vector).
     * @param v2 Second vector (will be put at back of the new vector).
     * @throws NullArgumentException if {@code v1} or {@code v2} is
     * {@code null}.
     * @throws ZeroException if both arrays are empty.
     * @see #ArrayFieldVector(FieldElement[], FieldElement[])
     */
    public ArrayFieldVector(Field<T> field, T[] v1, T[] v2) {
        if (v1.length + v2.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
        }
        data = buildArray(v1.length + v2.length);
        System.arraycopy(v1, 0, data, 0, v1.length);
        System.arraycopy(v2, 0, data, v1.length, v2.length);
        this.field = field;
    }

    /**
     * Build an array of elements.
     *
     * @param length Size of the array to build.
     * @return a new array.
     */
    @SuppressWarnings("unchecked") // field is of type T
    private T[] buildArray(final int length) {
        return (T[]) Array.newInstance(field.getRuntimeClass(), length);
    }

    /** {@inheritDoc} */
    public Field<T> getField() {
        return field;
    }

    /** {@inheritDoc} */
    public FieldVector<T> copy() {
        return new ArrayFieldVector<T>(this, true);
    }

    /** {@inheritDoc} */
    public FieldVector<T> add(FieldVector<T> v) {
        try {
            return add((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            T[] out = buildArray(data.length);
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i].add(v.getEntry(i));
            }
            return new ArrayFieldVector<T>(field, out, false);
        }
    }

    /**
     * Compute the sum of this and v.
     * @param v vector to be added
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayFieldVector<T> add(ArrayFieldVector<T> v) {
        checkVectorDimensions(v.data.length);
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].add(v.data[i]);
        }
        return new ArrayFieldVector<T>(field, out, false);
    }

    /** {@inheritDoc} */
    public FieldVector<T> subtract(FieldVector<T> v) {
        try {
            return subtract((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            T[] out = buildArray(data.length);
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i].subtract(v.getEntry(i));
            }
            return new ArrayFieldVector<T>(field, out, false);
        }
    }

    /**
     * Compute this minus v.
     * @param v vector to be subtracted
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayFieldVector<T> subtract(ArrayFieldVector<T> v) {
        checkVectorDimensions(v.data.length);
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].subtract(v.data[i]);
        }
        return new ArrayFieldVector<T>(field, out, false);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapAdd(T d) {
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].add(d);
        }
        return new ArrayFieldVector<T>(field, out, false);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapAddToSelf(T d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].add(d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapSubtract(T d) {
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].subtract(d);
        }
        return new ArrayFieldVector<T>(field, out, false);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapSubtractToSelf(T d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].subtract(d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapMultiply(T d) {
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].multiply(d);
        }
        return new ArrayFieldVector<T>(field, out, false);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapMultiplyToSelf(T d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].multiply(d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapDivide(T d) {
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].divide(d);
        }
        return new ArrayFieldVector<T>(field, out, false);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapDivideToSelf(T d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].divide(d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapInv() {
        T[] out = buildArray(data.length);
        final T one = field.getOne();
        for (int i = 0; i < data.length; i++) {
            out[i] = one.divide(data[i]);
        }
        return new ArrayFieldVector<T>(field, out, false);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapInvToSelf() {
        final T one = field.getOne();
        for (int i = 0; i < data.length; i++) {
            data[i] = one.divide(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public FieldVector<T> ebeMultiply(FieldVector<T> v) {
        try {
            return ebeMultiply((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            T[] out = buildArray(data.length);
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i].multiply(v.getEntry(i));
            }
            return new ArrayFieldVector<T>(field, out, false);
        }
    }

    /**
     * Element-by-element multiplication.
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public ArrayFieldVector<T> ebeMultiply(ArrayFieldVector<T> v) {
        checkVectorDimensions(v.data.length);
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].multiply(v.data[i]);
        }
        return new ArrayFieldVector<T>(field, out, false);
    }

    /** {@inheritDoc} */
    public FieldVector<T> ebeDivide(FieldVector<T> v) {
        try {
            return ebeDivide((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            T[] out = buildArray(data.length);
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i].divide(v.getEntry(i));
            }
            return new ArrayFieldVector<T>(field, out, false);
        }
    }

    /**
     * Element-by-element division.
     * @param v vector by which instance elements must be divided
     * @return a vector containing this[i] / v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayFieldVector<T> ebeDivide(ArrayFieldVector<T> v) {
        checkVectorDimensions(v.data.length);
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
                out[i] = data[i].divide(v.data[i]);
        }
        return new ArrayFieldVector<T>(field, out, false);
    }

    /** {@inheritDoc} */
    public T[] getData() {
        return data.clone();
    }

    /**
     * Returns a reference to the underlying data array.
     * <p>Does not make a fresh copy of the underlying data.</p>
     * @return array of entries
     */
    public T[] getDataRef() {
        return data;
    }

    /** {@inheritDoc} */
    public T dotProduct(FieldVector<T> v) {
        try {
            return dotProduct((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            T dot = field.getZero();
            for (int i = 0; i < data.length; i++) {
                dot = dot.add(data[i].multiply(v.getEntry(i)));
            }
            return dot;
        }
    }

    /**
     * Compute the dot product.
     * @param v vector with which dot product should be computed
     * @return the scalar dot product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public T dotProduct(ArrayFieldVector<T> v) {
        checkVectorDimensions(v.data.length);
        T dot = field.getZero();
        for (int i = 0; i < data.length; i++) {
            dot = dot.add(data[i].multiply(v.data[i]));
        }
        return dot;
    }

    /** {@inheritDoc} */
    public FieldVector<T> projection(FieldVector<T> v) {
        return v.mapMultiply(dotProduct(v).divide(v.dotProduct(v)));
    }

   /** Find the orthogonal projection of this vector onto another vector.
     * @param v vector onto which instance must be projected
     * @return projection of the instance onto v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayFieldVector<T> projection(ArrayFieldVector<T> v) {
        return (ArrayFieldVector<T>) v.mapMultiply(dotProduct(v).divide(v.dotProduct(v)));
    }

    /** {@inheritDoc} */
    public FieldMatrix<T> outerProduct(FieldVector<T> v) {
        try {
            return outerProduct((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            final int m = data.length;
            final int n = v.getDimension();
            final FieldMatrix<T> out = new Array2DRowFieldMatrix<T>(field, m, n);
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    out.setEntry(i, j, data[i].multiply(v.getEntry(j)));
                }
            }
            return out;
        }
    }

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the square matrix outer product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public FieldMatrix<T> outerProduct(ArrayFieldVector<T> v) {
        final int m = data.length;
        final int n = v.data.length;
        final FieldMatrix<T> out = new Array2DRowFieldMatrix<T>(field, m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                out.setEntry(i, j, data[i].multiply(v.data[j]));
            }
        }
        return out;
    }

    /** {@inheritDoc} */
    public T getEntry(int index) {
        return data[index];
    }

    /** {@inheritDoc} */
    public int getDimension() {
        return data.length;
    }

    /** {@inheritDoc} */
    public FieldVector<T> append(FieldVector<T> v) {
        try {
            return append((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            return new ArrayFieldVector<T>(this,new ArrayFieldVector<T>(v));
        }
    }

    /**
     * Construct a vector by appending a vector to this vector.
     * @param v vector to append to this one.
     * @return a new vector
     */
    public ArrayFieldVector<T> append(ArrayFieldVector<T> v) {
        return new ArrayFieldVector<T>(this, v);
    }

    /** {@inheritDoc} */
    public FieldVector<T> append(T in) {
        final T[] out = buildArray(data.length + 1);
        System.arraycopy(data, 0, out, 0, data.length);
        out[data.length] = in;
        return new ArrayFieldVector<T>(field, out, false);
    }

    /** {@inheritDoc} */
    public FieldVector<T> getSubVector(int index, int n) {
        ArrayFieldVector<T> out = new ArrayFieldVector<T>(field, n);
        try {
            System.arraycopy(data, index, out.data, 0, n);
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + n - 1);
        }
        return out;
    }

    /** {@inheritDoc} */
    public void setEntry(int index, T value) {
        try {
            data[index] = value;
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
        }
    }

    /** {@inheritDoc} */
    public void setSubVector(int index, FieldVector<T> v) {
        try {
            try {
                set(index, (ArrayFieldVector<T>) v);
            } catch (ClassCastException cce) {
                for (int i = index; i < index + v.getDimension(); ++i) {
                    data[i] = v.getEntry(i-index);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + v.getDimension() - 1);
        }
    }

    /**
     * Set a set of consecutive elements.
     *
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     * @throws OutOfRangeException if the index is
     * inconsistent with vector size
     */
    public void set(int index, ArrayFieldVector<T> v) {
        try {
            System.arraycopy(v.data, 0, data, index, v.data.length);
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + v.data.length - 1);
        }
    }

    /** {@inheritDoc} */
    public void set(T value) {
        Arrays.fill(data, value);
    }

    /** {@inheritDoc} */
    public T[] toArray(){
        return data.clone();
    }

    /**
     * Check if instance and specified vectors have the same dimension.
     * @param v vector to compare instance with
     * @exception IllegalArgumentException if the vectors do not
     * have the same dimension
     */
    protected void checkVectorDimensions(FieldVector<T> v) {
        checkVectorDimensions(v.getDimension());
    }

    /**
     * Check if instance dimension is equal to some expected value.
     *
     * @param n Expected dimension.
     * @throws OutOfRangeException if the dimension is
     * inconsistent with this vector size.
     */
    protected void checkVectorDimensions(int n) {
        if (data.length != n) {
            throw new DimensionMismatchException(data.length, n);
        }
    }

    /**
     * Test for the equality of two vectors.
     *
     * @param other Object to test for equality.
     * @return {@code true} if two vector objects are equal, {@code false}
     * otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        try {
            @SuppressWarnings("unchecked") // May fail, but we ignore ClassCastException
                FieldVector<T> rhs = (FieldVector<T>) other;
            if (data.length != rhs.getDimension()) {
                return false;
            }

            for (int i = 0; i < data.length; ++i) {
                if (!data[i].equals(rhs.getEntry(i))) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException ex) {
            // ignore exception
            return false;
        }
    }

    /**
     * Get a hashCode for the real vector.
     * <p>All NaN values have the same hash code.</p>
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        int h = 3542;
        for (final T a : data) {
            h = h ^ a.hashCode();
        }
        return h;
    }

    /**
     * Check if an index is valid.
     *
     * @param index Index to check.
     * @exception OutOfRangeException if the index is not valid.
     */
    private void checkIndex(final int index) {
        if (index < 0 || index >= getDimension()) {
            throw new OutOfRangeException(LocalizedFormats.INDEX,
                                          index, 0, getDimension() - 1);
        }
    }
}
