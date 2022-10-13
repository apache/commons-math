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
package org.apache.commons.math4.legacy.field.linalg;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.numbers.field.Field;
import org.apache.commons.math4.legacy.linear.AnyMatrix;

/**
 * Square matrix whose elements define a {@link Field}.
 *
 * @param <T> Type of the field elements.
 *
 * @since 4.0
 */
public final class FieldDenseMatrix<T>
    implements AnyMatrix {
    /** Field. */
    private final Field<T> field;
    /** Number of rows. */
    private final int rows;
    /** Number of columns. */
    private final int columns;
    /**
     * Data storage (in row-major order).
     *
     * <p>Note: This is an Object[] that has been cast to T[] for convenience. It should not be
     * exposed to avoid heap pollution. It is expected all entries stored in the array are
     * instances of T.
     */
    private final T[] data;

    /**
     * @param f Field.
     * @param r Number of rows.
     * @param c Number of columns.
     * @throws IllegalArgumentException if {@code r <= 0} or {@code c <= 0}.
     */
    @SuppressWarnings("unchecked")
    private FieldDenseMatrix(Field<T> f,
                             int r,
                             int c) {
        if (r <= 0 ||
            c <= 0) {
            throw new IllegalArgumentException("Negative size");
        }

        field = f;
        rows = r;
        columns = c;
        data = (T[]) new Object[r * c];
    }

    /**
     * Factory method.
     *
     * @param <T> Type of the field elements.
     * @param f Field.
     * @param r Number of rows.
     * @param c Number of columns.
     * @return a new instance.
     * @throws IllegalArgumentException if {@code r <= 0} or {@code c <= 0}.
     */
    public static <T> FieldDenseMatrix<T> create(Field<T> f,
                                                 int r,
                                                 int c) {
        return new FieldDenseMatrix<>(f, r, c);
    }

    /**
     * Factory method.
     *
     * @param <T> Type of the field elements.
     * @param f Field.
     * @param r Number of rows.
     * @param c Number of columns.
     * @return a matrix with elements zet to {@link Field#zero() zero}.
     * @throws IllegalArgumentException if {@code r <= 0} or {@code c <= 0}.
     */
    public static <T> FieldDenseMatrix<T> zero(Field<T> f,
                                               int r,
                                               int c) {
        return create(f, r, c).fill(f.zero());
    }

    /**
     * Factory method.
     *
     * @param <T> Type of the field elements.
     * @param f Field.
     * @param n Dimension of the matrix.
     * @throws IllegalArgumentException if {@code n <= 0}.
     * @return the identity matrix.
     */
    public static <T> FieldDenseMatrix<T> identity(Field<T> f,
                                                   int n) {
        final FieldDenseMatrix<T> r = zero(f, n, n);

        for (int i = 0; i < n; i++) {
            r.set(i, i, f.one());
        }

        return r;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            if (other instanceof FieldDenseMatrix) {
                final FieldDenseMatrix<?> m = (FieldDenseMatrix<?>) other;
                return field.equals(m.field) &&
                    rows == m.rows &&
                    columns == m.columns &&
                    Arrays.equals(data, m.data);
            } else {
                return false;
            }
        }
    }

    /**
     * Copies this matrix.
     *
     * @return a new instance.
     */
    public FieldDenseMatrix<T> copy() {
        final FieldDenseMatrix<T> r = create(field, rows, columns);
        System.arraycopy(data, 0, r.data, 0, data.length);
        return r;
    }

    /**
     * Transposes this matrix.
     *
     * @return a new instance.
     */
    public FieldDenseMatrix<T> transpose() {
        final FieldDenseMatrix<T> r = create(field, columns, rows);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                r.set(j, i, get(i, j));
            }
        }

        return r;
    }

    /** {@inheritDoc} */
    @Override
    public int getRowDimension() {
        return rows;
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnDimension() {
        return columns;
    }

    /**
     * @return the field associated with the matrix entries.
     */
    public Field<T> getField() {
        return field;
    }

    /**
     * Sets all elements to the given value.
     *
     * @param value Value of the elements of the matrix.
     * @return {@code this}.
     */
    public FieldDenseMatrix<T> fill(T value) {
        Arrays.fill(data, value);
        return this;
    }

    /**
     * Gets an element.
     *
     * @param i Row.
     * @param j Column.
     * @return the element at (i, j).
     */
    public T get(int i,
                 int j) {
        return data[i * columns + j];
    }

    /**
     * Sets an element.
     *
     * @param i Row.
     * @param j Column.
     * @param value Value.
     */
    public void set(int i,
                    int j,
                    T value) {
        data[i * columns + j] = value;
    }

    /**
     * Addition.
     *
     * @param other Matrix to add.
     * @return a new instance with the result of the addition.
     * @throws IllegalArgumentException if the dimensions do not match.
     */
    public FieldDenseMatrix<T> add(FieldDenseMatrix<T> other) {
        checkAdd(other);
        final FieldDenseMatrix<T> r = create(field, rows, columns);

        for (int i = 0; i < data.length; i++) {
            r.data[i] = field.add(data[i], other.data[i]);
        }

        return r;
    }

    /**
     * Subtraction.
     *
     * @param other Matrix to subtract.
     * @return a new instance with the result of the subtraction.
     * @throws IllegalArgumentException if the dimensions do not match.
     */
    public FieldDenseMatrix<T> subtract(FieldDenseMatrix<T> other) {
        checkAdd(other);
        final FieldDenseMatrix<T> r = create(field, rows, columns);

        for (int i = 0; i < data.length; i++) {
            r.data[i] = field.subtract(data[i], other.data[i]);
        }

        return r;
    }

    /**
     * Negate.
     *
     * @return a new instance with the opposite matrix.
     */
    public FieldDenseMatrix<T> negate() {
        final FieldDenseMatrix<T> r = create(field, rows, columns);

        for (int i = 0; i < data.length; i++) {
            r.data[i] = field.negate(data[i]);
        }

        return r;
    }

    /**
     * Multiplication.
     *
     * @param other Matrix to multiply with.
     * @return a new instance with the result of the multiplication.
     * @throws IllegalArgumentException if the dimensions do not match.
     */
    public FieldDenseMatrix<T> multiply(FieldDenseMatrix<T> other) {
        checkMultiply(other);
        final FieldDenseMatrix<T> r = zero(field, rows, other.columns);

        for (int i = 0; i < rows; i++) {
            final int o1 = i * columns;
            final int o2 = i * r.columns;
            for (int j = 0; j < other.columns; j++) {
                final int o3 = o2 + j;
                for (int k = 0; k < columns; k++) {
                    r.data[o3] = field.add(r.data[o3],
                                           field.multiply(data[o1 + k],
                                                          other.data[k * other.columns + j]));
                }
            }
        }

        return r;
    }

    /**
     * Multiplies the matrix with itself {@code p} times.
     *
     * @param p Exponent.
     * @return a new instance.
     * @throws IllegalArgumentException if {@code p < 0}.
     */
    public FieldDenseMatrix<T> pow(int p) {
        checkMultiply(this);

        if (p < 0) {
            throw new IllegalArgumentException("Negative exponent: " + p);
        }

        if (p == 0) {
            return identity(field, rows);
        }

        if (p == 1) {
            return copy();
        }

        final int power = p - 1;

        // Only log_2(p) operations are necessary by doing as follows:
        //    5^214 = 5^128 * 5^64 * 5^16 * 5^4 * 5^2
        // The same approach is used for A^p.

        final char[] binary = Integer.toBinaryString(power).toCharArray();
        final ArrayList<Integer> nonZeroPositions = new ArrayList<>();

        for (int i = 0; i < binary.length; i++) {
            if (binary[i] == '1') {
                final int pos = binary.length - i - 1;
                nonZeroPositions.add(pos);
            }
        }

        final List<FieldDenseMatrix<T>> results = new ArrayList<>(binary.length);
        results.add(this);
        for (int i = 1; i < binary.length; i++) {
            final FieldDenseMatrix<T> s = results.get(i - 1);
            final FieldDenseMatrix<T> r = s.multiply(s);
            results.add(r);
        }

        FieldDenseMatrix<T> r = this;
        for (Integer i : nonZeroPositions) {
            r = r.multiply(results.get(i));
        }

        return r;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        assert false : "hashCode not designed";
        return 42; // Any arbitrary constant will do.
    }
}
