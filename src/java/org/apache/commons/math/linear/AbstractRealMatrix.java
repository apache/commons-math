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
 * Basic implementation of RealMatrix methods regardless of the underlying storage.
 * <p>All the methods implemented here use {@link #getEntry(int, int)} to access
 * matrix elements. Derived class can provide faster implementations. </p>
 *
 * @version $Revision$ $Date$
 * @since 2.0
 */
public abstract class AbstractRealMatrix implements RealMatrix, Serializable {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = -3665653040524315561L;

    /** Cached LU solver.
     * @deprecated as of release 2.0, since all methods using this are deprecated
     */
    private LUSolver lu;

    /**
     * Creates a matrix with no data
     */
    protected AbstractRealMatrix() {
        lu = null;
    }

    /**
     * Create a new RealMatrix with the supplied row and column dimensions.
     *
     * @param rowDimension  the number of rows in the new matrix
     * @param columnDimension  the number of columns in the new matrix
     * @throws IllegalArgumentException if row or column dimension is not positive
     */
    protected AbstractRealMatrix(final int rowDimension, final int columnDimension)
        throws IllegalArgumentException {
        if (rowDimension <= 0 ) {
            throw MathRuntimeException.createIllegalArgumentException("invalid row dimension {0}" +
                                                                      " (must be positive)",
                                                                      new Object[] { rowDimension });
        }
        if (columnDimension <= 0) {
            throw MathRuntimeException.createIllegalArgumentException("invalid column dimension {0}" +
                                                                      " (must be positive)",
                                                                      new Object[] { columnDimension });
        }
        lu = null;
    }

    /** {@inheritDoc} */
    public abstract RealMatrix createMatrix(final int rowDimension, final int columnDimension)
        throws IllegalArgumentException;

    /** {@inheritDoc} */
    public abstract RealMatrix copy();

    /** {@inheritDoc} */
    public RealMatrix add(RealMatrix m) throws IllegalArgumentException {
        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        if (columnCount != m.getColumnDimension() || rowCount != m.getRowDimension()) {
            throw MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not" +
                                                                      " addition compatible",
                                                                      new Object[] {
                                                                          getRowDimension(),
                                                                          getColumnDimension(),
                                                                          m.getRowDimension(),
                                                                          m.getColumnDimension()
                                                                      });
        }

        final RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col) + m.getEntry(row, col));
            }  
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix subtract(final RealMatrix m) throws IllegalArgumentException {
        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        if (columnCount != m.getColumnDimension() || rowCount != m.getRowDimension()) {
            throw MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not" +
                                                                      " subtraction compatible",
                                                                      new Object[] {
                                                                          getRowDimension(),
                                                                          getColumnDimension(),
                                                                          m.getRowDimension(),
                                                                          m.getColumnDimension()
                                                                      });
        }

        final RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col) - m.getEntry(row, col));
            }  
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix scalarAdd(final double d) {

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col) + d);
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix scalarMultiply(final double d) {

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col) * d);
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix multiply(final RealMatrix m)
        throws IllegalArgumentException {
        if (getColumnDimension() != m.getRowDimension()) {
            throw MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not" +
                                                                      " multiplication compatible",
                                                                      new Object[] {
                                                                          getRowDimension(),
                                                                          getColumnDimension(),
                                                                          m.getRowDimension(),
                                                                          m.getColumnDimension()
                                                                      });
        }

        final int nRows = getRowDimension();
        final int nCols = m.getColumnDimension();
        final int nSum = getColumnDimension();
        final RealMatrix out = createMatrix(nRows, nCols);
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                double sum = 0;
                for (int i = 0; i < nSum; ++i) {
                    sum += getEntry(row, i) * m.getEntry(i, col);
                }
                out.setEntry(row, col, sum);
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix preMultiply(final RealMatrix m)
        throws IllegalArgumentException {
        return m.multiply(this);
    }

    /** {@inheritDoc} */
    public abstract double[][] getData();

    /** {@inheritDoc} */
    public double getNorm() {
        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        double maxColSum = 0;
        for (int col = 0; col < columnCount; ++col) {
            double sum = 0;
            for (int row = 0; row < rowCount; ++row) {
                sum += Math.abs(getEntry(row, col));
            }
            maxColSum = Math.max(maxColSum, sum);
        }
        return maxColSum;
    }
    
    /** {@inheritDoc} */
    public double getFrobeniusNorm() {
        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        double sum2 = 0;
        for (int col = 0; col < columnCount; ++col) {
            for (int row = 0; row < rowCount; ++row) {
                final double mij = getEntry(row, col);
                sum2 += mij * mij;
            }
        }
        return Math.sqrt(sum2);
    }
    
    /** {@inheritDoc} */
    public RealMatrix getSubMatrix(final int startRow, final int endRow,
                                   final int startColumn, final int endColumn)
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

        final RealMatrix subMatrix =
            createMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
        for (int i = startRow; i <= endRow; ++i) {
            for (int j = startColumn; j <= endColumn; ++j) {
                subMatrix.setEntry(i - startRow, j - startColumn, getEntry(i, j));
            }
        }

        return subMatrix;

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

        final RealMatrix subMatrix =
            createMatrix(selectedRows.length, selectedColumns.length);
        try  {
            for (int i = 0; i < selectedRows.length; i++) {
                for (int j = 0; j < selectedColumns.length; j++) {
                    subMatrix.setEntry(i, j, getEntry(selectedRows[i], selectedColumns[j]));
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

        return subMatrix;

    } 

    /** {@inheritDoc} */
    public void setSubMatrix(final double[][] subMatrix, final int row, final int column) 
        throws MatrixIndexException {

        final int nRows = subMatrix.length;
        if (nRows == 0) {
            throw new IllegalArgumentException("Matrix must have at least one row."); 
        }

        final int nCols = subMatrix[0].length;
        if (nCols == 0) {
            throw new IllegalArgumentException("Matrix must have at least one column."); 
        }

        for (int r = 1; r < nRows; ++r) {
            if (subMatrix[r].length != nCols) {
                throw new IllegalArgumentException("All input rows must have the same length.");
            }
        }

        checkRowIndex(row);
        checkColumnIndex(column);
        checkRowIndex(nRows + row - 1);
        checkColumnIndex(nCols + column - 1);

        for (int i = 0; i < nRows; ++i) {
            for (int j = 0; j < nCols; ++j) {
                setEntry(row + i, column + j, subMatrix[i][j]);
            }
        } 

        lu = null;

    }

    /** {@inheritDoc} */
    public RealMatrix getRowMatrix(final int row)
        throws MatrixIndexException {

        checkRowIndex(row);
        final int nCols = getColumnDimension();
        final RealMatrix out = createMatrix(1, nCols);
        for (int i = 0; i < nCols; ++i) {
            out.setEntry(0, i, getEntry(row, i));
        }

        return out;

    }
    
    /** {@inheritDoc} */
    public RealMatrix getColumnMatrix(final int column)
        throws MatrixIndexException {

        checkColumnIndex(column);
        final int nRows = getRowDimension();
        final RealMatrix out = createMatrix(nRows, 1);
        for (int i = 0; i < nRows; ++i) {
            out.setEntry(i, 0, getEntry(i, column));
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealVector getColumnVector(final int column)
        throws MatrixIndexException {
        return new RealVectorImpl(getColumn(column), false);
    }

    /** {@inheritDoc} */
    public RealVector getRowVector(final int row)
        throws MatrixIndexException {
        return new RealVectorImpl(getRow(row), false);
    }

    /** {@inheritDoc} */
    public double[] getRow(final int row)
        throws MatrixIndexException {

        checkRowIndex(row);
        final int nCols = getColumnDimension();
        final double[] out = new double[nCols];
        for (int i = 0; i < nCols; ++i) {
            out[i] = getEntry(row, i);
        }

        return out;

    }

    /** {@inheritDoc} */
    public double[] getColumn(final int column)
        throws MatrixIndexException {

        checkColumnIndex(column);
        final int nRows = getRowDimension();
        final double[] out = new double[nRows];
        for (int i = 0; i < nRows; ++i) {
            out[i] = getEntry(i, column);
        }

        return out;

    }

    /** {@inheritDoc} */
    public abstract double getEntry(int row, int column)
        throws MatrixIndexException;

    /** {@inheritDoc} */
    public abstract void setEntry(int row, int column, double value)
        throws MatrixIndexException;

    /** {@inheritDoc} */
    public RealMatrix transpose() {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        final RealMatrix out = createMatrix(nCols, nRows);
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                out.setEntry(col, row, getEntry(row, col));
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    @Deprecated
    public RealMatrix inverse()
        throws InvalidMatrixException {
        if (lu == null) {
            lu = new LUSolver(new LUDecompositionImpl(this, MathUtils.SAFE_MIN));
        }
        return lu.getInverse();
    }

    /** {@inheritDoc} */
    @Deprecated
    public double getDeterminant()
        throws InvalidMatrixException {
        if (lu == null) {
            lu = new LUSolver(new LUDecompositionImpl(this, MathUtils.SAFE_MIN));
        }
        return lu.getDeterminant();
    }

    /** {@inheritDoc} */
    public boolean isSquare() {
        return (getColumnDimension() == getRowDimension());
    }

    /** {@inheritDoc} */
    @Deprecated
    public boolean isSingular() {
        if (lu == null) {
            lu = new LUSolver(new LUDecompositionImpl(this, MathUtils.SAFE_MIN));
       }
        return !lu.isNonSingular();
    }

    /** {@inheritDoc} */
    public abstract int getRowDimension();

    /** {@inheritDoc} */
    public abstract int getColumnDimension();

    /** {@inheritDoc} */
    public double getTrace()
        throws NonSquareMatrixException {
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (nRows != nCols) {
            throw new NonSquareMatrixException(nRows, nCols);
       }
        double trace = 0;
        for (int i = 0; i < nRows; ++i) {
            trace += getEntry(i, i);
        }
        return trace;
    }

    /** {@inheritDoc} */
    public double[] operate(final double[] v)
        throws IllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.length != nCols) {
            throw MathRuntimeException.createIllegalArgumentException("vector length mismatch:" +
                                                                      " got {0} but expected {1}",
                                                                      new Object[] {
                                                                          v.length, nCols
                                                                      });
        }

        final double[] out = new double[nRows];
        for (int row = 0; row < nRows; ++row) {
            double sum = 0;
            for (int i = 0; i < nCols; ++i) {
                sum += getEntry(row, i) * v[i];
            }
            out[row] = sum;
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealVector operate(final RealVector v)
        throws IllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.getDimension() != nCols) {
            throw MathRuntimeException.createIllegalArgumentException("vector length mismatch:" +
                                                                      " got {0} but expected {1}",
                                                                      new Object[] {
                                                                          v.getDimension(), nCols
                                                                      });
        }

        final double[] out = new double[nRows];
        for (int row = 0; row < nRows; ++row) {
            double sum = 0;
            for (int i = 0; i < nCols; ++i) {
                sum += getEntry(row, i) * v.getEntry(i);
            }
            out[row] = sum;
        }

        return new RealVectorImpl(out, false);

    }

    /** {@inheritDoc} */
    public double[] preMultiply(final double[] v)
        throws IllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.length != nRows) {
            throw MathRuntimeException.createIllegalArgumentException("vector length mismatch:" +
                                                                      " got {0} but expected {1}",
                                                                      new Object[] {
                                                                          v.length, nRows
                                                                      });
        }

        final double[] out = new double[nCols];
        for (int col = 0; col < nCols; ++col) {
            double sum = 0;
            for (int i = 0; i < nRows; ++i) {
                sum += getEntry(i, col) * v[i];
            }
            out[col] = sum;
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealVector preMultiply(final RealVector v)
        throws IllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.getDimension() != nRows) {
            throw MathRuntimeException.createIllegalArgumentException("vector length mismatch:" +
                                                                      " got {0} but expected {1}",
                                                                      new Object[] {
                                                                          v.getDimension(), nRows
                                                                      });
        }

        final double[] out = new double[nCols];
        for (int col = 0; col < nCols; ++col) {
            double sum = 0;
            for (int i = 0; i < nRows; ++i) {
                sum += getEntry(i, col) * v.getEntry(i);
            }
            out[col] = sum;
        }

        return new RealVectorImpl(out);

    }

    /** {@inheritDoc} */
    @Deprecated
    public double[] solve(final double[] b)
        throws IllegalArgumentException, InvalidMatrixException {
        if (lu == null) {
            lu = new LUSolver(new LUDecompositionImpl(this, MathUtils.SAFE_MIN));
        }
        return lu.solve(b);
    }

    /** {@inheritDoc} */
    @Deprecated
    public RealMatrix solve(final RealMatrix b)
        throws IllegalArgumentException, InvalidMatrixException  {
        if (lu == null) {
            lu = new LUSolver(new LUDecompositionImpl(this, MathUtils.SAFE_MIN));
        }
        return lu.solve(b);
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
    public void luDecompose()
        throws InvalidMatrixException {
        if (lu == null) {
            lu = new LUSolver(new LUDecompositionImpl(this, MathUtils.SAFE_MIN));
        }
    }

    /**
     * Get a string representation for this matrix.
     * @return a string representation for this matrix
     */
    public String toString() {
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        final StringBuffer res = new StringBuffer();
        res.append("RealMatrixImpl{");

        for (int i = 0; i < nRows; ++i) {
            if (i > 0) {
                res.append(",");
            }
            res.append("{");
            for (int j = 0; j < nCols; ++j) {
                if (j > 0) {
                    res.append(",");
                }
                res.append(getEntry(i, j));
            } 
            res.append("}");
        } 

        res.append("}");
        return res.toString();

    } 
    
    /**
     * Returns true iff <code>object</code> is a
     * <code>RealMatrix</code> instance with the same dimensions as this
     * and all corresponding matrix entries are equal.
     * 
     * @param object the object to test equality against.
     * @return true if object equals this
     */
    public boolean equals(final Object object) {
        if (object == this ) {
            return true;
        }
        if (object instanceof RealMatrix == false) {
            return false;
        }
        RealMatrix m = (RealMatrix) object;
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
            return false;
        }
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                if (getEntry(row, col) != m.getEntry(row, col)) {
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
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
               ret = ret * 31 + (11 * (row+1) + 17 * (col+1)) * 
                   MathUtils.hash(getEntry(row, col));
           }
        }
        return ret;
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
