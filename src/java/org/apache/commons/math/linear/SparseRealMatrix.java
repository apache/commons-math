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

import org.apache.commons.math.util.OpenIntToDoubleHashMap;

/**
 * Sparse matrix implementation based on an open addressed map.
 * 
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class SparseRealMatrix extends AbstractRealMatrix {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -5962461716457143437L;

    /** Number of rows of the matrix. */
    private final int rowDimension;

    /** Number of columns of the matrix. */
    private final int columnDimension;

    /** Storage for (sparse) matrix elements. */
    private OpenIntToDoubleHashMap entries;

    /**
     * Build a sparse matrix with the supplied row and column dimensions.
     * @param rowDimension number of rows of the matrix
     * @param columnDimension number of columns of the matrix
     */
    public SparseRealMatrix(int rowDimension, int columnDimension) {
        super(rowDimension, columnDimension);
        this.rowDimension = rowDimension;
        this.columnDimension = columnDimension;
        this.entries = new OpenIntToDoubleHashMap(0.0);
    }
  
    /**
     * Build a matrix by copying another one.
     * @param matrix matrix to copy
     */
    public SparseRealMatrix(SparseRealMatrix matrix) {
        this.rowDimension = matrix.rowDimension;
        this.columnDimension = matrix.columnDimension;
        this.entries = new OpenIntToDoubleHashMap(matrix.entries);
    }
  
    /** {@inheritDoc} */
    @Override
    public RealMatrix copy() {
        return new SparseRealMatrix(this);
    }

    /** {@inheritDoc} */
    @Override
    public RealMatrix createMatrix(int rowDimension, int columnDimension)
            throws IllegalArgumentException {
        return new SparseRealMatrix(rowDimension, columnDimension);
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnDimension() {
        return columnDimension;
    }

    /** {@inheritDoc} */
    public RealMatrix add(final RealMatrix m)
        throws IllegalArgumentException {
        try {
            return add((SparseRealMatrix) m);
        } catch (ClassCastException cce) {
            return super.add(m);
        }
    }

    /**
     * Compute the sum of this and <code>m</code>.
     *
     * @param m    matrix to be added
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public RealMatrix add(SparseRealMatrix m) throws IllegalArgumentException {

        // safety check
        checkAdditionCompatible(m);

        final RealMatrix out = new SparseRealMatrix(this);
        for (OpenIntToDoubleHashMap.Iterator iterator = m.entries.iterator(); iterator.hasNext();) {
            iterator.advance();
            final int row = iterator.key() / columnDimension;
            final int col = iterator.key() - row * columnDimension;
            out.setEntry(row, col, getEntry(row, col) + iterator.value());
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix subtract(final RealMatrix m)
        throws IllegalArgumentException {
        try {
            return subtract((SparseRealMatrix) m);
        } catch (ClassCastException cce) {
            return super.add(m);
        }
    }

    /**
     * Compute this minus <code>m</code>.
     *
     * @param m    matrix to be subtracted
     * @return     this - m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public RealMatrix subtract(SparseRealMatrix m) throws IllegalArgumentException {

        // safety check
        checkAdditionCompatible(m);

        final RealMatrix out = new SparseRealMatrix(this);
        for (OpenIntToDoubleHashMap.Iterator iterator = m.entries.iterator(); iterator.hasNext();) {
            iterator.advance();
            final int row = iterator.key() / columnDimension;
            final int col = iterator.key() - row * columnDimension;
            out.setEntry(row, col, getEntry(row, col) - iterator.value());
        }

        return out;

    }

    /** {@inheritDoc} */
    @Override
    public double getEntry(int row, int column) throws MatrixIndexException {
        checkRowIndex(row);
        checkColumnIndex(column);
        return entries.get(computeKey(row, column));
    }

    /** {@inheritDoc} */
    @Override
    public int getRowDimension() {
        return rowDimension;
    }

    /** {@inheritDoc} */
    @Override
    public void setEntry(int row, int column, double value)
            throws MatrixIndexException {
        checkRowIndex(row);
        checkColumnIndex(column);
        if (value == 0.0) {
            entries.remove(computeKey(row, column));
        } else {
            entries.put(computeKey(row, column), value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addToEntry(int row, int column, double increment)
            throws MatrixIndexException {
        checkRowIndex(row);
        checkColumnIndex(column);
        final int key = computeKey(row, column);
        final double value = entries.get(key) + increment;
        if (value == 0.0) {
            entries.remove(key);
        } else {
            entries.put(key, value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void multiplyEntry(int row, int column, double factor)
            throws MatrixIndexException {
        checkRowIndex(row);
        checkColumnIndex(column);
        final int key = computeKey(row, column);
        final double value = entries.get(key) * factor;
        if (value == 0.0) {
            entries.remove(key);
        } else {
            entries.put(key, value);
        }
    }

    /**
     * Compute the key to access a matrix element
     * @param row row index of the matrix element
     * @param column column index of the matrix element
     * @return key within the map to access the matrix element
     */
    private int computeKey(int row, int column) {
        return row * columnDimension + column;
    }

}
