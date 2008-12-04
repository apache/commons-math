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

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.util.MathUtils;


/**
 * Implementation of RealMatrix using a double[][] array to store entries and
 * <a href="http://www.math.gatech.edu/~bourbaki/math2601/Web-notes/2num.pdf">
 * LU decomposition</a> to support linear system
 * solution and inverse.
 * <p>
 * The LU decomposition is performed as needed, to support the following operations: <ul>
 * <li>solve</li>
 * <li>isSingular</li>
 * <li>getDeterminant</li>
 * <li>inverse</li> </ul></p>
 * <p>
 * <strong>Usage notes</strong>:<br>
 * <ul><li>
 * The LU decomposition is cached and reused on subsequent calls.   
 * If data are modified via references to the underlying array obtained using
 * <code>getDataRef()</code>, then the stored LU decomposition will not be
 * discarded.  In this case, you need to explicitly invoke 
 * <code>LUDecompose()</code> to recompute the decomposition
 * before using any of the methods above.</li>
 * <li>
 * As specified in the {@link RealMatrix} interface, matrix element indexing
 * is 0-based -- e.g., <code>getEntry(0, 0)</code>
 * returns the element in the first row, first column of the matrix.</li></ul>
 * </p>
 *
 * @version $Revision$ $Date$
 */
public class RealMatrixImpl implements RealMatrix, Serializable {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = 4970229902484487012L;

    /** Entries of the matrix */
    protected double data[][];

    /** Cached decomposition solver.
     * @deprecated as of release 2.0, since all methods using this are deprecated
     */
    private DecompositionSolver ds;

    /** Cached LU decomposition.
     * @deprecated as of release 2.0, since all methods using this are deprecated
     */
    private LUDecomposition lu;

    /**
     * Creates a matrix with no data
     */
    public RealMatrixImpl() {
    }

    /**
     * Create a new RealMatrix with the supplied row and column dimensions.
     *
     * @param rowDimension  the number of rows in the new matrix
     * @param columnDimension  the number of columns in the new matrix
     * @throws IllegalArgumentException if row or column dimension is not
     *  positive
     */
    public RealMatrixImpl(int rowDimension, int columnDimension) {
        if (rowDimension <= 0 || columnDimension <= 0) {
            throw new IllegalArgumentException(
                    "row and column dimensions must be postive");
        }
        data = new double[rowDimension][columnDimension];
        ds = null;
    }

    /**
     * Create a new RealMatrix using the input array as the underlying
     * data array.
     * <p>The input array is copied, not referenced. This constructor has
     * the same effect as calling {@link #RealMatrixImpl(double[][], boolean)}
     * with the second argument set to <code>true</code>.</p>
     *
     * @param d data for new matrix
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     * @see #RealMatrixImpl(double[][], boolean)
     */
    public RealMatrixImpl(double[][] d) {
        copyIn(d);
        ds = null;
    }

    /**
     * Create a new RealMatrix using the input array as the underlying
     * data array.
     * <p>If an array is built specially in order to be embedded in a
     * RealMatrix and not used directly, the <code>copyArray</code> may be
     * set to <code>false</code. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.</p>
     * @param d data for new matrix
     * @param copyArray if true, the input array will be copied, otherwise
     * it will be referenced
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     * @see #RealMatrixImpl(double[][])
     */
    public RealMatrixImpl(double[][] d, boolean copyArray) {
        if (copyArray) {
            copyIn(d);
        } else {
            if (d == null) {
                throw new NullPointerException();
            }   
            final int nRows = d.length;
            if (nRows == 0) {
                throw new IllegalArgumentException("Matrix must have at least one row."); 
            }
            final int nCols = d[0].length;
            if (nCols == 0) {
                throw new IllegalArgumentException("Matrix must have at least one column."); 
            }
            for (int r = 1; r < nRows; r++) {
                if (d[r].length != nCols) {
                    throw new IllegalArgumentException("All input rows must have the same length.");
                }
            }       
            data = d;
        }
        ds = null;
    }

    /**
     * Create a new (column) RealMatrix using <code>v</code> as the
     * data for the unique column of the <code>v.length x 1</code> matrix
     * created.
     * <p>The input array is copied, not referenced.</p>
     *
     * @param v column vector holding data for new matrix
     */
    public RealMatrixImpl(double[] v) {
        final int nRows = v.length;
        data = new double[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = v[row];
        }
    }

    /** {@inheritDoc} */
    public RealMatrix copy() {
        return new RealMatrixImpl(copyOut(), false);
    }

    /** {@inheritDoc} */
    public RealMatrix add(RealMatrix m) throws IllegalArgumentException {
        try {
            return add((RealMatrixImpl) m);
        } catch (ClassCastException cce) {
            final int rowCount    = getRowDimension();
            final int columnCount = getColumnDimension();
            if (columnCount != m.getColumnDimension() || rowCount != m.getRowDimension()) {
                throw MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not" +
                                                                          " addition compatible",
                                                                          new Object[] {
                                                                              getRowDimension(), getColumnDimension(),
                                                                              m.getRowDimension(), m.getColumnDimension()
                                                                          });
            }
            final double[][] outData = new double[rowCount][columnCount];
            for (int row = 0; row < rowCount; row++) {
                final double[] dataRow    = data[row];
                final double[] outDataRow = outData[row];
                for (int col = 0; col < columnCount; col++) {
                    outDataRow[col] = dataRow[col] + m.getEntry(row, col);
                }  
            }
            return new RealMatrixImpl(outData, false);
        }
    }

    /**
     * Compute the sum of this and <code>m</code>.
     *
     * @param m    matrix to be added
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public RealMatrixImpl add(RealMatrixImpl m) throws IllegalArgumentException {
        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        if (columnCount != m.getColumnDimension() || rowCount != m.getRowDimension()) {
            throw MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not" +
                                                                      " addition compatible",
                                                                      new Object[] {
                                                                          getRowDimension(), getColumnDimension(),
                                                                          m.getRowDimension(), m.getColumnDimension()
                                                                      });
        }
        final double[][] outData = new double[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            final double[] dataRow    = data[row];
            final double[] mRow       = m.data[row];
            final double[] outDataRow = outData[row];
            for (int col = 0; col < columnCount; col++) {
                outDataRow[col] = dataRow[col] + mRow[col];
            }  
        }
        return new RealMatrixImpl(outData, false);
    }

    /** {@inheritDoc} */
    public RealMatrix subtract(RealMatrix m) throws IllegalArgumentException {
        try {
            return subtract((RealMatrixImpl) m);
        } catch (ClassCastException cce) {
            final int rowCount    = getRowDimension();
            final int columnCount = getColumnDimension();
            if (columnCount != m.getColumnDimension() || rowCount != m.getRowDimension()) {
                throw MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not" +
                                                                          " subtraction compatible",
                                                                          new Object[] {
                                                                              getRowDimension(), getColumnDimension(),
                                                                              m.getRowDimension(), m.getColumnDimension()
                                                                          });
            }
            final double[][] outData = new double[rowCount][columnCount];
            for (int row = 0; row < rowCount; row++) {
                final double[] dataRow    = data[row];
                final double[] outDataRow = outData[row];
                for (int col = 0; col < columnCount; col++) {
                    outDataRow[col] = dataRow[col] - m.getEntry(row, col);
                }  
            }
            return new RealMatrixImpl(outData, false);
        }
    }

    /**
     * Compute  this minus <code>m</code>.
     *
     * @param m    matrix to be subtracted
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public RealMatrixImpl subtract(RealMatrixImpl m) throws IllegalArgumentException {
        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        if (columnCount != m.getColumnDimension() || rowCount != m.getRowDimension()) {
            throw MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not" +
                                                                      " subtraction compatible",
                                                                      new Object[] {
                                                                          getRowDimension(), getColumnDimension(),
                                                                          m.getRowDimension(), m.getColumnDimension()
                                                                      });
        }
        final double[][] outData = new double[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            final double[] dataRow    = data[row];
            final double[] mRow       = m.data[row];
            final double[] outDataRow = outData[row];
            for (int col = 0; col < columnCount; col++) {
                outDataRow[col] = dataRow[col] - mRow[col];
            }  
        }
        return new RealMatrixImpl(outData, false);
    }

    /** {@inheritDoc} */
    public RealMatrix scalarAdd(double d) {
        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final double[][] outData = new double[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            final double[] dataRow    = data[row];
            final double[] outDataRow = outData[row];
            for (int col = 0; col < columnCount; col++) {
                outDataRow[col] = dataRow[col] + d;
            }
        }
        return new RealMatrixImpl(outData, false);
    }

    /** {@inheritDoc} */
    public RealMatrix scalarMultiply(double d) {
        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final double[][] outData = new double[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            final double[] dataRow    = data[row];
            final double[] outDataRow = outData[row];
            for (int col = 0; col < columnCount; col++) {
                outDataRow[col] = dataRow[col] * d;
            }
        }
        return new RealMatrixImpl(outData, false);
    }

    /** {@inheritDoc} */
    public RealMatrix multiply(RealMatrix m) throws IllegalArgumentException {
        try {
            return multiply((RealMatrixImpl) m);
        } catch (ClassCastException cce) {
            if (this.getColumnDimension() != m.getRowDimension()) {
                throw MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not" +
                                                                          " multiplication compatible",
                                                                          new Object[] {
                                                                              getRowDimension(), getColumnDimension(),
                                                                              m.getRowDimension(), m.getColumnDimension()
                                                                          });
            }
            final int nRows = this.getRowDimension();
            final int nCols = m.getColumnDimension();
            final int nSum = this.getColumnDimension();
            final double[][] outData = new double[nRows][nCols];
            for (int row = 0; row < nRows; row++) {
                final double[] dataRow    = data[row];
                final double[] outDataRow = outData[row];
                for (int col = 0; col < nCols; col++) {
                    double sum = 0;
                    for (int i = 0; i < nSum; i++) {
                        sum += dataRow[i] * m.getEntry(i, col);
                    }
                    outDataRow[col] = sum;
                }
            }
            return new RealMatrixImpl(outData, false);
        }
    }

    /**
     * Returns the result of postmultiplying this by <code>m</code>.
     * @param m    matrix to postmultiply by
     * @return     this*m
     * @throws     IllegalArgumentException
     *             if columnDimension(this) != rowDimension(m)
     */
    public RealMatrixImpl multiply(RealMatrixImpl m) throws IllegalArgumentException {
        if (this.getColumnDimension() != m.getRowDimension()) {
            throw MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not" +
                                                                      " multiplication compatible",
                                                                      new Object[] {
                                                                          getRowDimension(), getColumnDimension(),
                                                                          m.getRowDimension(), m.getColumnDimension()
                                                                      });
        }
        final int nRows = this.getRowDimension();
        final int nCols = m.getColumnDimension();
        final int nSum = this.getColumnDimension();
        final double[][] outData = new double[nRows][nCols];
        for (int row = 0; row < nRows; row++) {
            final double[] dataRow    = data[row];
            final double[] outDataRow = outData[row];
            for (int col = 0; col < nCols; col++) {
                double sum = 0;
                for (int i = 0; i < nSum; i++) {
                    sum += dataRow[i] * m.data[i][col];
                }
                outDataRow[col] = sum;
            }
        }            
        return new RealMatrixImpl(outData, false);
    }

    /** {@inheritDoc} */
    public RealMatrix preMultiply(RealMatrix m) throws IllegalArgumentException {
        return m.multiply(this);
    }

    /** {@inheritDoc} */
    public double[][] getData() {
        return copyOut();
    }

    /**
     * Returns a reference to the underlying data array.
     * <p>
     * Does <strong>not</strong> make a fresh copy of the underlying data.</p>
     *
     * @return 2-dimensional array of entries
     */
    public double[][] getDataRef() {
        return data;
    }

    /** {@inheritDoc} */
    public double getNorm() {
        double maxColSum = 0;
        for (int col = 0; col < this.getColumnDimension(); col++) {
            double sum = 0;
            for (int row = 0; row < this.getRowDimension(); row++) {
                sum += Math.abs(data[row][col]);
            }
            maxColSum = Math.max(maxColSum, sum);
        }
        return maxColSum;
    }
    
    /** {@inheritDoc} */
    public RealMatrix getSubMatrix(int startRow, int endRow,
                                   int startColumn, int endColumn)
        throws MatrixIndexException {

        checkRowIndex(startRow);
        checkRowIndex(endRow);
        if (startRow > endRow) {
            throw new MatrixIndexException("initial row {0} after final row {1}",
                                           new Object[] { startRow, endRow });
        }

        checkColumnIndex(startColumn);
        checkColumnIndex(endColumn);
        if (startColumn > endColumn) {
            throw new MatrixIndexException("initial column {0} after final column {1}",
                                           new Object[] { startColumn, endColumn });
        }

        final double[][] subMatrixData =
            new double[endRow - startRow + 1][endColumn - startColumn + 1];
        for (int i = startRow; i <= endRow; i++) {
            System.arraycopy(data[i], startColumn,
                             subMatrixData[i - startRow], 0,
                             endColumn - startColumn + 1);
        }
        return new RealMatrixImpl(subMatrixData, false);
    }
    
    /** {@inheritDoc} */
    public RealMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns)
        throws MatrixIndexException {

        if (selectedRows.length * selectedColumns.length == 0) {
            if (selectedRows.length == 0) {
                throw new MatrixIndexException("empty selected row index array", null);
            }
            throw new MatrixIndexException("empty selected column index array", null);
        }

        final double[][] subMatrixData =
            new double[selectedRows.length][selectedColumns.length];
        try  {
            for (int i = 0; i < selectedRows.length; i++) {
                final double[] subI = subMatrixData[i];
                final double[] dataSelectedI = data[selectedRows[i]];
                for (int j = 0; j < selectedColumns.length; j++) {
                    subI[j] = dataSelectedI[selectedColumns[j]];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // we redo the loop with checks enabled
            // in order to generate an appropriate message
            for (final int row : selectedRows) {
                checkRowIndex(row);
            }
            for (final int column : selectedColumns) {
                checkColumnIndex(column);
            }
        }
        return new RealMatrixImpl(subMatrixData, false);
    } 

    /**
     * Replace the submatrix starting at <code>row, column</code> using data in
     * the input <code>subMatrix</code> array. Indexes are 0-based.
     * <p> 
     * Example:<br>
     * Starting with <pre>
     * 1  2  3  4
     * 5  6  7  8
     * 9  0  1  2
     * </pre>
     * and <code>subMatrix = {{3, 4} {5,6}}</code>, invoking 
     * <code>setSubMatrix(subMatrix,1,1))</code> will result in <pre>
     * 1  2  3  4
     * 5  3  4  8
     * 9  5  6  2
     * </pre></p>
     * 
     * @param subMatrix  array containing the submatrix replacement data
     * @param row  row coordinate of the top, left element to be replaced
     * @param column  column coordinate of the top, left element to be replaced
     * @throws MatrixIndexException  if subMatrix does not fit into this 
     *    matrix from element in (row, column) 
     * @throws IllegalArgumentException if <code>subMatrix</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>subMatrix</code> is null
     * @since 1.1
     */
    public void setSubMatrix(double[][] subMatrix, int row, int column) 
        throws MatrixIndexException {

        final int nRows = subMatrix.length;
        if (nRows == 0) {
            throw new IllegalArgumentException("Matrix must have at least one row."); 
        }

        final int nCols = subMatrix[0].length;
        if (nCols == 0) {
            throw new IllegalArgumentException("Matrix must have at least one column."); 
        }

        for (int r = 1; r < nRows; r++) {
            if (subMatrix[r].length != nCols) {
                throw new IllegalArgumentException("All input rows must have the same length.");
            }
        }

        if (data == null) {
            if (row > 0) {
                throw MathRuntimeException.createIllegalStateException("first {0} rows are not initialized yet",
                                                                       new Object[] { row });
            }
            if (column > 0) {
                throw MathRuntimeException.createIllegalStateException("first {0} columns are not initialized yet",
                                                                       new Object[] { column });
            }
            data = new double[nRows][nCols];
            System.arraycopy(subMatrix, 0, data, 0, subMatrix.length);          
        } else {
            checkRowIndex(row);
            checkColumnIndex(column);
            checkRowIndex(nRows + row - 1);
            checkColumnIndex(nCols + column - 1);
        }

        for (int i = 0; i < nRows; i++) {
            System.arraycopy(subMatrix[i], 0, data[row + i], column, nCols);
        } 

        ds = null;

    }

    /** {@inheritDoc} */
    public RealMatrix getRowMatrix(int row) throws MatrixIndexException {
        checkRowIndex(row);
        final int ncols = this.getColumnDimension();
        final double[][] out = new double[1][ncols]; 
        System.arraycopy(data[row], 0, out[0], 0, ncols);
        return new RealMatrixImpl(out, false);
    }
    
    /** {@inheritDoc} */
    public RealMatrix getColumnMatrix(int column) throws MatrixIndexException {
        checkColumnIndex(column);
        final int nRows = this.getRowDimension();
        final double[][] out = new double[nRows][1]; 
        for (int row = 0; row < nRows; row++) {
            out[row][0] = data[row][column];
        }
        return new RealMatrixImpl(out, false);
    }

    /** {@inheritDoc} */
    public RealVector getColumnVector(int column) throws MatrixIndexException {
        return new RealVectorImpl(getColumn(column), false);
    }

    /** {@inheritDoc} */
    public RealVector getRowVector(int row) throws MatrixIndexException {
        return new RealVectorImpl(getRow(row), false);
    }

    /** {@inheritDoc} */
    public double[] getRow(int row) throws MatrixIndexException {
        checkRowIndex(row);
        final int ncols = this.getColumnDimension();
        final double[] out = new double[ncols];
        System.arraycopy(data[row], 0, out, 0, ncols);
        return out;
    }

    /** {@inheritDoc} */
    public double[] getColumn(int col) throws MatrixIndexException {
        checkColumnIndex(col);
        final int nRows = this.getRowDimension();
        final double[] out = new double[nRows];
        for (int row = 0; row < nRows; row++) {
            out[row] = data[row][col];
        }
        return out;
    }

    /** {@inheritDoc} */
    public double getEntry(int row, int column)
        throws MatrixIndexException {
        try {
            return data[row][column];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MatrixIndexException("no entry at indices ({0}, {1}) in a {2}x{3} matrix",
                                           new Object[] {
                                               row, column,
                                               getRowDimension(), getColumnDimension()
                                           });
        }
    }

    /** {@inheritDoc} */
    public RealMatrix transpose() {
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        final double[][] outData = new double[nCols][nRows];
        for (int row = 0; row < nRows; row++) {
            final double[] dataRow = data[row];
            for (int col = 0; col < nCols; col++) {
                outData[col][row] = dataRow[col];
            }
        }
        return new RealMatrixImpl(outData, false);
    }

    /** {@inheritDoc} */
    @Deprecated
    public RealMatrix inverse() throws InvalidMatrixException {
        if (ds == null) {
            ds = new DecompositionSolver(this);
            lu = ds.luDecompose(MathUtils.SAFE_MIN);
        }
        return ds.getInverse(lu);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double getDeterminant() throws InvalidMatrixException {
        if (ds == null) {
            ds = new DecompositionSolver(this);
            lu = ds.luDecompose(MathUtils.SAFE_MIN);
        }
        return ds.getDeterminant(lu);
    }

    /** {@inheritDoc} */
    public boolean isSquare() {
        return (this.getColumnDimension() == this.getRowDimension());
    }

    /** {@inheritDoc} */
    @Deprecated
    public boolean isSingular() {
        if (ds == null) {
            ds = new DecompositionSolver(this);
            lu = ds.luDecompose(MathUtils.SAFE_MIN);
        }
        return !ds.isNonSingular(lu);
    }

    /** {@inheritDoc} */
    public int getRowDimension() {
        return data.length;
    }

    /** {@inheritDoc} */
    public int getColumnDimension() {
        return data[0].length;
    }

    /** {@inheritDoc} */
    public double getTrace() throws IllegalArgumentException {
        if (!isSquare()) {
            throw new IllegalArgumentException("matrix is not square");
        }
        double trace = data[0][0];
        for (int i = 1; i < this.getRowDimension(); i++) {
            trace += data[i][i];
        }
        return trace;
    }

    /** {@inheritDoc} */
    public double[] operate(double[] v) throws IllegalArgumentException {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (v.length != nCols) {
            throw new IllegalArgumentException("vector has wrong length");
        }
        final double[] out = new double[nRows];
        for (int row = 0; row < nRows; row++) {
            final double[] dataRow = data[row];
            double sum = 0;
            for (int i = 0; i < nCols; i++) {
                sum += dataRow[i] * v[i];
            }
            out[row] = sum;
        }
        return out;
    }

    /** {@inheritDoc} */
    public RealVector operate(RealVector v) throws IllegalArgumentException {
        try {
            return operate((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            final int nRows = this.getRowDimension();
            final int nCols = this.getColumnDimension();
            if (v.getDimension() != nCols) {
                throw new IllegalArgumentException("vector has wrong length");
            }
            final double[] out = new double[nRows];
            for (int row = 0; row < nRows; row++) {
                final double[] dataRow = data[row];
                double sum = 0;
                for (int i = 0; i < nCols; i++) {
                    sum += dataRow[i] * v.getEntry(i);
                }
                out[row] = sum;
            }
            return new RealVectorImpl(out, false);
        }
    }

    /**
     * Returns the result of multiplying this by the vector <code>v</code>.
     *
     * @param v the vector to operate on
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    public RealVectorImpl operate(RealVectorImpl v) throws IllegalArgumentException {
        return new RealVectorImpl(operate(v.getDataRef()), false);
    }

    /** {@inheritDoc} */
    public double[] preMultiply(double[] v) throws IllegalArgumentException {
        final int nRows = this.getRowDimension();
        if (v.length != nRows) {
            throw new IllegalArgumentException("vector has wrong length");
        }
        final int nCols = this.getColumnDimension();
        final double[] out = new double[nCols];
        for (int col = 0; col < nCols; col++) {
            double sum = 0;
            for (int i = 0; i < nRows; i++) {
                sum += data[i][col] * v[i];
            }
            out[col] = sum;
        }
        return out;
    }

    /** {@inheritDoc} */
    public RealVector preMultiply(RealVector v) throws IllegalArgumentException {
        try {
            return preMultiply((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            final int nRows = this.getRowDimension();
            if (v.getDimension() != nRows) {
                throw new IllegalArgumentException("vector has wrong length");
            }
            final int nCols = this.getColumnDimension();
            final double[] out = new double[nCols];
            for (int col = 0; col < nCols; col++) {
                double sum = 0;
                for (int i = 0; i < nRows; i++) {
                    sum += data[i][col] * v.getEntry(i);
                }
                out[col] = sum;
            }
            return new RealVectorImpl(out, false);
        }
    }

    /**
     * Returns the (row) vector result of premultiplying this by the vector <code>v</code>.
     *
     * @param v the row vector to premultiply by
     * @return v*this
     * @throws IllegalArgumentException if rowDimension != v.size()
     */
    RealVectorImpl preMultiply(RealVectorImpl v) throws IllegalArgumentException {
        return new RealVectorImpl(preMultiply(v.getDataRef()), false);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double[] solve(double[] b) throws IllegalArgumentException, InvalidMatrixException {
        if (ds == null) {
            ds = new DecompositionSolver(this);
            lu = ds.luDecompose(MathUtils.SAFE_MIN);
        }
        return ds.solve(b, lu);
    }

    /** {@inheritDoc} */
    @Deprecated
    public RealMatrix solve(RealMatrix b) throws IllegalArgumentException, InvalidMatrixException  {
        if (ds == null) {
            ds = new DecompositionSolver(this);
            lu = ds.luDecompose(MathUtils.SAFE_MIN);
        }
        return ds.solve(b, lu);
    }

    /**
     * Computes a new
     * <a href="http://www.math.gatech.edu/~bourbaki/math2601/Web-notes/2num.pdf">
     * LU decomposition</a> for this matrix, storing the result for use by other methods.
     * <p>
     * <strong>Implementation Note</strong>:<br>
     * Uses <a href="http://www.damtp.cam.ac.uk/user/fdl/people/sd/lectures/nummeth98/linear.htm">
     * Crout's algorithm</a>, with partial pivoting.</p>
     * <p>
     * <strong>Usage Note</strong>:<br>
     * This method should rarely be invoked directly. Its only use is
     * to force recomputation of the LU decomposition when changes have been
     * made to the underlying data using direct array references. Changes
     * made using setXxx methods will trigger recomputation when needed
     * automatically.</p>
     *
     * @throws InvalidMatrixException if the matrix is non-square or singular.
     * @deprecated as of release 2.0, replaced by {@link LUDecomposition}
     */
    @Deprecated
    public void luDecompose() throws InvalidMatrixException {
        if (ds == null) {
            ds = new DecompositionSolver(this);
            lu = ds.luDecompose(MathUtils.SAFE_MIN);
        }
    }

    /**
     * Get a string representation for this matrix.
     * @return a string representation for this matrix
     */
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("RealMatrixImpl{");
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                if (i > 0) {
                    res.append(",");
                }
                res.append("{");
                for (int j = 0; j < data[0].length; j++) {
                    if (j > 0) {
                        res.append(",");
                    }
                    res.append(data[i][j]);
                } 
                res.append("}");
            } 
        }
        res.append("}");
        return res.toString();
    } 
    
    /**
     * Returns true iff <code>object</code> is a 
     * <code>RealMatrixImpl</code> instance with the same dimensions as this
     * and all corresponding matrix entries are equal.
     * 
     * @param object the object to test equality against.
     * @return true if object equals this
     */
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
        if (object instanceof RealMatrixImpl == false) {
            return false;
        }
        RealMatrix m = (RealMatrix) object;
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
            return false;
        }
        for (int row = 0; row < nRows; row++) {
            final double[] dataRow = data[row];
            for (int col = 0; col < nCols; col++) {
                if (dataRow[col] != m.getEntry(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Computes a hashcode for the matrix.
     * 
     * @return hashcode for matrix
     */
    public int hashCode() {
        int ret = 7;
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        ret = ret * 31 + nRows;
        ret = ret * 31 + nCols;
        for (int row = 0; row < nRows; row++) {
            final double[] dataRow = data[row];
            for (int col = 0; col < nCols; col++) {
               ret = ret * 31 + (11 * (row+1) + 17 * (col+1)) * 
                   MathUtils.hash(dataRow[col]);
           }
        }
        return ret;
    }

    //------------------------ Private methods

    /**
     * Returns a fresh copy of the underlying data array.
     *
     * @return a copy of the underlying data array.
     */
    private double[][] copyOut() {
        final int nRows = this.getRowDimension();
        final double[][] out = new double[nRows][this.getColumnDimension()];
        // can't copy 2-d array in one shot, otherwise get row references
        for (int i = 0; i < nRows; i++) {
            System.arraycopy(data[i], 0, out[i], 0, data[i].length);
        }
        return out;
    }

    /**
     * Replaces data with a fresh copy of the input array.
     * <p>
     * Verifies that the input array is rectangular and non-empty.</p>
     *
     * @param in data to copy in
     * @throws IllegalArgumentException if input array is empty or not
     *    rectangular
     * @throws NullPointerException if input array is null
     */
    private void copyIn(double[][] in) {
        setSubMatrix(in, 0, 0);
    }

    /**
     * Check if a row index is valid.
     * @param row row index to check
     * @exception MatrixIndexException if index is not valid
     */
    private void checkRowIndex(final int row) {
        if (row < 0 || row >= getRowDimension()) {
            throw new MatrixIndexException("row index {0} out of allowed range [{1}, {2}]",
                                           new Object[] { row, 0, getRowDimension() - 1});
        }
    }

    /**
     * Check if a column index is valid.
     * @param column column index to check
     * @exception MatrixIndexException if index is not valid
     */
    private void checkColumnIndex(final int column)
        throws MatrixIndexException {
        if (column < 0 || column >= getColumnDimension()) {
            throw new MatrixIndexException("column index {0} out of allowed range [{1}, {2}]",
                                           new Object[] { column, 0, getColumnDimension() - 1});
        }
    }

}
