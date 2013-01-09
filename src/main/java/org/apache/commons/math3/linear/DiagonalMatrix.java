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

import java.io.Serializable;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;

/**
 * Implementation of a diagonal matrix.
 * <br/>
 * Caveat: This implementation is minimal; it is currently solely aimed
 * at solving issue MATH-924. In particular many methods just throw
 * {@code MathUnsupportedOperationException}.
 *
 * @version $Id$
 */
public class DiagonalMatrix extends AbstractRealMatrix
    implements Serializable {
    /** Serializable version identifier. */
    private static final long serialVersionUID = 20121229L;
    /** Entries of the diagonal. */
    private final double[] data;

    /**
     * Creates a matrix with the supplied dimension.
     *
     * @param dimension Number of rows and columns in the new matrix.
     * @throws NotStrictlyPositiveException if the dimension is
     * not positive.
     */
    public DiagonalMatrix(final int dimension)
        throws NotStrictlyPositiveException {
        super(dimension, dimension);
        data = new double[dimension];
    }

    /**
     * Creates a matrix using the input array as the underlying data.
     * <br/>
     * The input array is copied, not referenced.
     *
     * @param d Data for the new matrix.
     */
    public DiagonalMatrix(final double[] d) {
        this(d, true);
    }

    /**
     * Creates a matrix using the input array as the underlying data.
     * <br/>
     * If an array is created specially in order to be embedded in a
     * this instance and not used directly, the {@code copyArray} may be
     * set to {@code false}.
     * This will prevent the copying and improve performance as no new
     * array will be built and no data will be copied.
     *
     * @param d Data for new matrix.
     * @param copyArray if {@code true}, the input array will be copied,
     * otherwise it will be referenced.
     */
    public DiagonalMatrix(final double[] d, final boolean copyArray) {
        data = copyArray ? d.clone() : d;
    }

    /**
     * {@inheritDoc}
     *
     * @throws DimensionMismatchException if the requested dimensions are not equal.
     */
    @Override
    public RealMatrix createMatrix(final int rowDimension,
                                   final int columnDimension)
        throws NotStrictlyPositiveException,
               DimensionMismatchException {
        if (rowDimension != columnDimension) {
            throw new DimensionMismatchException(rowDimension, columnDimension);
        }

        return new DiagonalMatrix(rowDimension);
    }

    /** {@inheritDoc} */
    @Override
    public RealMatrix copy() {
        return new DiagonalMatrix(data);
    }

    /**
     * Compute the sum of {@code this} and {@code m}.
     *
     * @param m Matrix to be added.
     * @return {@code this + m}.
     * @throws MatrixDimensionMismatchException if {@code m} is not the same
     * size as {@code this}.
     */
    public DiagonalMatrix add(final DiagonalMatrix m)
        throws MatrixDimensionMismatchException {
        // Safety check.
        MatrixUtils.checkAdditionCompatible(this, m);

        final int dim = getRowDimension();
        final double[] outData = new double[dim];
        for (int i = 0; i < dim; i++) {
            outData[i] = data[i] + m.data[i];
        }

        return new DiagonalMatrix(outData, false);
    }

    /**
     * Returns {@code this} minus {@code m}.
     *
     * @param m Matrix to be subtracted.
     * @return {@code this - m}
     * @throws MatrixDimensionMismatchException if {@code m} is not the same
     * size as {@code this}.
     */
    public DiagonalMatrix subtract(final DiagonalMatrix m)
        throws MatrixDimensionMismatchException {
        MatrixUtils.checkSubtractionCompatible(this, m);

        final int dim = getRowDimension();
        final double[] outData = new double[dim];
        for (int i = 0; i < dim; i++) {
            outData[i] = data[i] - m.data[i];
        }

        return new DiagonalMatrix(outData, false);
    }

    /**
     * Returns the result of postmultiplying {@code this} by {@code m}.
     *
     * @param m matrix to postmultiply by
     * @return {@code this * m}
     * @throws DimensionMismatchException if
     * {@code columnDimension(this) != rowDimension(m)}
     */
    public DiagonalMatrix multiply(final DiagonalMatrix m)
        throws DimensionMismatchException {
        MatrixUtils.checkMultiplicationCompatible(this, m);

        final int dim = getRowDimension();
        final double[] outData = new double[dim];
        for (int i = 0; i < dim; i++) {
            outData[i] = data[i] * m.data[i];
        }

        return new DiagonalMatrix(outData, false);
    }

    /**
     * Returns the result of postmultiplying {@code this} by {@code m}.
     *
     * @param m matrix to postmultiply by
     * @return {@code this * m}
     * @throws DimensionMismatchException if
     * {@code columnDimension(this) != rowDimension(m)}
     */
    public RealMatrix multiply(final RealMatrix m)
        throws DimensionMismatchException {
        if (m instanceof DiagonalMatrix) {
            return multiply((DiagonalMatrix) m);
        } else {
            MatrixUtils.checkMultiplicationCompatible(this, m);
            final int nRows = m.getRowDimension();
            final int nCols = m.getColumnDimension();
            final double[][] product = new double[nRows][nCols];
            for (int r = 0; r < nRows; r++) {
                for (int c = 0; c < nCols; c++) {
                    product[r][c] = data[r] * m.getEntry(r, c);
                }
            }
            return new Array2DRowRealMatrix(product, false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public double[][] getData() {
        final int dim = getRowDimension();
        final double[][] out = new double[dim][dim];

        for (int i = 0; i < dim; i++) {
            out[i][i] = data[i];
        }

        return out;
    }

    /**
     * Gets a reference to the underlying data array.
     *
     * @return 1-dimensional array of entries.
     */
    public double[] getDataRef() {
        return data;
    }

    /** {@inheritDoc}
     * @throws MathUnsupportedOperationException
     */
    @Override
    public void setSubMatrix(final double[][] subMatrix,
                             final int row,
                             final int column)
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public double getEntry(final int row, final int column)
        throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        return row == column ? data[row] : 0;
    }

    /** {@inheritDoc}
     * @throws MathUnsupportedOperationException if {@code row != column}.
     */
    @Override
    public void setEntry(final int row, final int column, final double value)
        throws OutOfRangeException,
               MathUnsupportedOperationException {
        if (row != column) {
            throw new MathUnsupportedOperationException();
        }
        MatrixUtils.checkMatrixIndex(this, row, column);
        data[row] = value;
    }

    /** {@inheritDoc}
     * @throws MathUnsupportedOperationException if {@code row != column}.
     */
    @Override
    public void addToEntry(final int row,
                           final int column,
                           final double increment)
        throws OutOfRangeException,
               MathUnsupportedOperationException {
        if (row != column) {
            throw new MathUnsupportedOperationException();
        }
        MatrixUtils.checkMatrixIndex(this, row, column);
        data[row] += increment;
    }

    /** {@inheritDoc}
     * @throws MathUnsupportedOperationException if {@code row != column}.
     */
    @Override
    public void multiplyEntry(final int row,
                              final int column,
                              final double factor)
        throws OutOfRangeException,
               MathUnsupportedOperationException {
        if (row != column) {
            throw new MathUnsupportedOperationException();
        }
        MatrixUtils.checkMatrixIndex(this, row, column);
        data[row] *= factor;
    }

    /** {@inheritDoc} */
    @Override
    public int getRowDimension() {
        return data == null ? 0 : data.length;
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnDimension() {
        return getRowDimension();
    }

    /** {@inheritDoc} */
    @Override
    public double[] operate(final double[] v)
        throws DimensionMismatchException {
        return multiply(new DiagonalMatrix(v, false)).getDataRef();
    }

    /** {@inheritDoc} */
    @Override
    public double[] preMultiply(final double[] v)
        throws DimensionMismatchException {
        return operate(v);
    }

    /** {@inheritDoc} */
    @Override
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor)
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor)
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor,
                                 final int startRow, final int endRow,
                                 final int startColumn, final int endColumn)
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor,
                                 final int startRow, final int endRow,
                                 final int startColumn, final int endColumn)
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public double walkInColumnOrder(final RealMatrixChangingVisitor visitor)
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor)
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public double walkInColumnOrder(final RealMatrixChangingVisitor visitor,
                                    final int startRow, final int endRow,
                                    final int startColumn, final int endColumn)
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor,
                                    final int startRow, final int endRow,
                                    final int startColumn, final int endColumn)
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
}
