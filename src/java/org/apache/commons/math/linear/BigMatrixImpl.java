/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.math.BigDecimal;

/**
 * Implementation of {@link BigMatrix} using a BigDecimal[][] array to store entries
 * and <a href="http://www.math.gatech.edu/~bourbaki/math2601/Web-notes/2num.pdf">
 * LU decompostion</a> to support linear system 
 * solution and inverse.
 * <p>
 * The LU decompostion is performed as needed, to support the following operations: <ul>
 * <li>solve</li>
 * <li>isSingular</li>
 * <li>getDeterminant</li>
 * <li>inverse</li> </ul>
 * <p>
* <strong>Usage notes</strong>:<br>
 * <ul><li>
 * The LU decomposition is stored and reused on subsequent calls.  If matrix
 * data are modified using any of the public setXxx methods, the saved
 * decomposition is discarded.  If data are modified via references to the
 * underlying array obtained using <code>getDataRef()</code>, then the stored
 * LU decomposition will not be discarded.  In this case, you need to
 * explicitly invoke <code>LUDecompose()</code> to recompute the decomposition
 * before using any of the methods above.</li>
 * <li>
 * As specified in the {@link BigMatrix} interface, matrix element indexing
 * is 0-based -- e.g., <code>getEntry(0, 0)</code>
 * returns the element in the first row, first column of the matrix.</li></ul>
 * @version $Revision$ $Date$
 */
public class BigMatrixImpl implements BigMatrix, Serializable {
    
    /** Serialization id */
    private static final long serialVersionUID = -1011428905656140431L;
    
    /** Entries of the matrix */
    private BigDecimal data[][] = null;
    
    /** Entries of cached LU decomposition.
     *  All updates to data (other than luDecompose()) *must* set this to null
     */
    private BigDecimal lu[][] = null;
    
    /** Permutation associated with LU decompostion */
    private int[] permutation = null;
    
    /** Parity of the permutation associated with the LU decomposition */
    private int parity = 1;
    
    /** Rounding mode for divisions **/
    private int roundingMode = BigDecimal.ROUND_HALF_UP;
    
    /*** BigDecimal scale ***/
    private int scale = 64;
    
    /** Bound to determine effective singularity in LU decomposition */
    protected static BigDecimal TOO_SMALL = new BigDecimal(10E-12);
    
    /** BigDecimal 0 */
    static final BigDecimal ZERO = new BigDecimal(0);
    /** BigDecimal 1 */
    static final BigDecimal ONE = new BigDecimal(1);
    
    /** 
     * Creates a matrix with no data
     */
    public BigMatrixImpl() {
    }
    
    /**
     * Create a new BigMatrix with the supplied row and column dimensions.
     *
     * @param rowDimension      the number of rows in the new matrix
     * @param columnDimension   the number of columns in the new matrix
     * @throws IllegalArgumentException if row or column dimension is not
     *  positive
     */
    public BigMatrixImpl(int rowDimension, int columnDimension) {
        if (rowDimension <=0 || columnDimension <=0) {
            throw new IllegalArgumentException
            ("row and column dimensions must be positive");
        }
        data = new BigDecimal[rowDimension][columnDimension];
        lu = null;
    }
    
    /**
     * Create a new BigMatrix using the <code>data</code> as the underlying
     * data array.
     * <p>
     * The input array is copied, not referenced.
     *
     * @param d data for new matrix
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     */
    public BigMatrixImpl(BigDecimal[][] d) {
        this.copyIn(d);
        lu = null;
    }
    
    /**
     * Create a new BigMatrix using the <code>data</code> as the underlying
     * data array.
     * <p>
     * The input array is copied, not referenced.
     *
     * @param d data for new matrix
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     */
    public BigMatrixImpl(double[][] d) {
        int nRows = d.length;
        if (nRows == 0) {
            throw new IllegalArgumentException(
            "Matrix must have at least one row."); 
        }
        int nCols = d[0].length;
        if (nCols == 0) {
            throw new IllegalArgumentException(
            "Matrix must have at least one column."); 
        }
        for (int row = 1; row < nRows; row++) {
            if (d[row].length != nCols) {
                throw new IllegalArgumentException(
                "All input rows must have the same length.");
            }
        }
        this.copyIn(d);
        lu = null;
    }
    
    /**
     * Create a new BigMatrix using the values represented by the strings in 
     * <code>data</code> as the underlying data array.
     *
     * @param d data for new matrix
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     */
    public BigMatrixImpl(String[][] d) {
        int nRows = d.length;
        if (nRows == 0) {
            throw new IllegalArgumentException(
            "Matrix must have at least one row."); 
        }
        int nCols = d[0].length;
        if (nCols == 0) {
            throw new IllegalArgumentException(
            "Matrix must have at least one column."); 
        }
        for (int row = 1; row < nRows; row++) {
            if (d[row].length != nCols) {
                throw new IllegalArgumentException(
                "All input rows must have the same length.");
            }
        }
        this.copyIn(d);
        lu = null;
    }
    
    /**
     * Create a new (column) BigMatrix using <code>v</code> as the
     * data for the unique column of the <code>v.length x 1</code> matrix 
     * created.
     * <p>
     * The input array is copied, not referenced.
     *
     * @param v column vector holding data for new matrix
     */
    public BigMatrixImpl(BigDecimal[] v) {
        int nRows = v.length;
        data = new BigDecimal[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = v[row];
        }
    }
    
    /**
     * Create a new BigMatrix which is a copy of this.
     *
     * @return  the cloned matrix
     */
    public BigMatrix copy() {
        return new BigMatrixImpl(this.copyOut());
    }
    
    /**
     * Compute the sum of this and <code>m</code>.
     *
     * @param m    matrix to be added
     * @return     this + m
     * @exception  IllegalArgumentException if m is not the same size as this
     */
    public BigMatrix add(BigMatrix m) throws IllegalArgumentException {
        if (this.getColumnDimension() != m.getColumnDimension() ||
                this.getRowDimension() != m.getRowDimension()) {
            throw new IllegalArgumentException("matrix dimension mismatch");
        }
        int rowCount = this.getRowDimension();
        int columnCount = this.getColumnDimension();
        BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                outData[row][col] = data[row][col].add(m.getEntry(row, col));
            }
        }
        return new BigMatrixImpl(outData);
    }
    
    /**
     * Compute  this minus <code>m</code>.
     *
     * @param m    matrix to be subtracted
     * @return     this + m
     * @exception  IllegalArgumentException if m is not the same size as *this
     */
    public BigMatrix subtract(BigMatrix m) throws IllegalArgumentException {
        if (this.getColumnDimension() != m.getColumnDimension() ||
                this.getRowDimension() != m.getRowDimension()) {
            throw new IllegalArgumentException("matrix dimension mismatch");
        }
        int rowCount = this.getRowDimension();
        int columnCount = this.getColumnDimension();
        BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                outData[row][col] = data[row][col].subtract(m.getEntry(row, col));
            }
        }
        return new BigMatrixImpl(outData);
    }
    
    /**
     * Returns the result of adding d to each entry of this.
     *
     * @param d    value to be added to each entry
     * @return     d + this
     */
    public BigMatrix scalarAdd(BigDecimal d) {
        int rowCount = this.getRowDimension();
        int columnCount = this.getColumnDimension();
        BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                outData[row][col] = data[row][col].add(d);
            }
        }
        return new BigMatrixImpl(outData);
    }
    
    /**
     * Returns the result multiplying each entry of this by <code>d</code>
     * @param d  value to multiply all entries by
     * @return d * this
     */
    public BigMatrix scalarMultiply(BigDecimal d) {
        int rowCount = this.getRowDimension();
        int columnCount = this.getColumnDimension();
        BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                outData[row][col] = data[row][col].multiply(d);
            }
        }
        return new BigMatrixImpl(outData);
    }
    
    /**
     * Returns the result of postmultiplying this by <code>m</code>.
     * @param m    matrix to postmultiply by
     * @return     this*m
     * @throws     IllegalArgumentException
     *             if columnDimension(this) != rowDimension(m)
     */
    public BigMatrix multiply(BigMatrix m) throws IllegalArgumentException {
        if (this.getColumnDimension() != m.getRowDimension()) {
            throw new IllegalArgumentException("Matrices are not multiplication compatible.");
        }
        int nRows = this.getRowDimension();
        int nCols = m.getColumnDimension();
        int nSum = this.getColumnDimension();
        BigDecimal[][] outData = new BigDecimal[nRows][nCols];
        BigDecimal sum = ZERO;
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                sum = ZERO;
                for (int i = 0; i < nSum; i++) {
                    sum = sum.add(data[row][i].multiply(m.getEntry(i, col)));
                }
                outData[row][col] = sum;
            }
        }
        return new BigMatrixImpl(outData);
    }
    
    /**
     * Returns the result premultiplying this by <code>m</code>.
     * @param m    matrix to premultiply by
     * @return     m * this
     * @throws     IllegalArgumentException
     *             if rowDimension(this) != columnDimension(m)
     */
    public BigMatrix preMultiply(BigMatrix m) throws IllegalArgumentException {
        return m.multiply(this);
    }
    
    /**
     * Returns matrix entries as a two-dimensional array.
     * <p>
     * Makes a fresh copy of the underlying data.
     *
     * @return    2-dimensional array of entries
     */
    public BigDecimal[][] getData() {
        return copyOut();
    }
    
    /**
     * Returns matrix entries as a two-dimensional array.
     * <p>
     * Makes a fresh copy of the underlying data converted to
     * <code>double</code> values.
     *
     * @return    2-dimensional array of entries
     */
    public double[][] getDataAsDoubleArray() {
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        double d[][] = new double[nRows][nCols];
        for (int i = 0; i < nRows; i++) {
            for (int j=0; j<nCols;j++) {
                d[i][j] = data[i][j].doubleValue();
            }
        }
        return d;
    }
    
    /**
     * Returns a reference to the underlying data array.
     * <p>
     * Does not make a fresh copy of the underlying data.
     *
     * @return 2-dimensional array of entries
     */
    public BigDecimal[][] getDataRef() {
        return data;
    }
    
    /***
     * Gets the rounding mode for division operations
     * The default is {@link java.math.BigDecimal#ROUND_HALF_UP}
     * @see BigDecimal
     * @return the rounding mode.
     */ 
    public int getRoundingMode() {
        return roundingMode;
    }
    
    /***
     * Sets the rounding mode for decimal divisions.
     * @see BigDecimal
     * @param roundingMode
     */
    public void setRoundingMode(int roundingMode) {
        this.roundingMode = roundingMode;
    }
    
    /***
     * Sets the scale for division operations.
     * The default is 64
     * @see BigDecimal
     * @return the scale
     */
    public int getScale() {
        return scale;
    }
    
    /***
     * Sets the scale for division operations.
     * @see BigDecimal
     * @param scale
     */
    public void setScale(int scale) {
        this.scale = scale;
    }
    
    /**
     * Returns the <a href="http://mathworld.wolfram.com/MaximumAbsoluteRowSumNorm.html">
     * maximum absolute row sum norm</a> of the matrix.
     *
     * @return norm
     */
    public BigDecimal getNorm() {
        BigDecimal maxColSum = ZERO;
        for (int col = 0; col < this.getColumnDimension(); col++) {
            BigDecimal sum = ZERO;
            for (int row = 0; row < this.getRowDimension(); row++) {
                sum = sum.add(data[row][col].abs());
            }
            maxColSum = maxColSum.max(sum);
        }
        return maxColSum;
    }
    
    /**
     * Gets a submatrix. Rows and columns are indicated
     * counting from 0 to n-1.
     *
     * @param startRow Initial row index
     * @param endRow Final row index
     * @param startColumn Initial column index
     * @param endColumn Final column index
     * @return The subMatrix containing the data of the
     *         specified rows and columns
     * @exception MatrixIndexException if row or column selections are not valid
     */
    public BigMatrix getSubMatrix(int startRow, int endRow, int startColumn,
            int endColumn) throws MatrixIndexException {
        if (startRow < 0 || startRow > endRow || endRow > data.length ||
                startColumn < 0 || startColumn > endColumn ||
                endColumn > data[0].length ) {
            throw new MatrixIndexException(
            "invalid row or column index selection");
        }
        BigMatrixImpl subMatrix = new BigMatrixImpl(endRow - startRow+1,
                endColumn - startColumn+1);
        BigDecimal[][] subMatrixData = subMatrix.getDataRef();
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                subMatrixData[i - startRow][j - startColumn] = data[i][j];
            }
        }
        return subMatrix;
    }
    
    /**
     * Gets a submatrix. Rows and columns are indicated
     * counting from 0 to n-1.
     *
     * @param selectedRows Array of row indices must be non-empty
     * @param selectedColumns Array of column indices must be non-empty
     * @return The subMatrix containing the data in the
     *     specified rows and columns
     * @exception MatrixIndexException  if supplied row or column index arrays
     *     are not valid
     */
    public BigMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns)
    throws MatrixIndexException {
        if (selectedRows.length * selectedColumns.length == 0) {
            throw new MatrixIndexException(
            "selected row and column index arrays must be non-empty");
        }
        BigMatrixImpl subMatrix = new BigMatrixImpl(selectedRows.length,
                selectedColumns.length);
        BigDecimal[][] subMatrixData = subMatrix.getDataRef();
        try  {
            for (int i = 0; i < selectedRows.length; i++) {
                for (int j = 0; j < selectedColumns.length; j++) {
                    subMatrixData[i][j] = data[selectedRows[i]][selectedColumns[j]];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new MatrixIndexException("matrix dimension mismatch");
        }
        return subMatrix;
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
     * </pre>
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
    public void setSubMatrix(BigDecimal[][] subMatrix, int row, int column) 
    throws MatrixIndexException {
        if ((row < 0) || (column < 0)){
            throw new MatrixIndexException
            ("invalid row or column index selection");          
        }
        int nRows = subMatrix.length;
        if (nRows == 0) {
            throw new IllegalArgumentException(
            "Matrix must have at least one row."); 
        }
        int nCols = subMatrix[0].length;
        if (nCols == 0) {
            throw new IllegalArgumentException(
            "Matrix must have at least one column."); 
        }
        for (int r = 1; r < nRows; r++) {
            if (subMatrix[r].length != nCols) {
                throw new IllegalArgumentException(
                "All input rows must have the same length.");
            }
        }       
        if (data == null) {
            if ((row > 0)||(column > 0)) throw new MatrixIndexException
            ("matrix must be initialized to perfom this method");
            data = new BigDecimal[nRows][nCols];
            System.arraycopy(subMatrix, 0, data, 0, subMatrix.length);          
        }   
        if (((nRows + row) > this.getRowDimension()) ||
            (nCols + column > this.getColumnDimension()))
            throw new MatrixIndexException(
            "invalid row or column index selection");                   
        for (int i = 0; i < nRows; i++) {
            System.arraycopy(subMatrix[i], 0, data[row + i], column, nCols);
        } 
        lu = null;
    }
    
    /**
     * Returns the entries in row number <code>row</code>
     * as a row matrix.  Row indices start at 0.
     *
     * @param row the row to be fetched
     * @return row matrix
     * @throws MatrixIndexException if the specified row index is invalid
     */
    public BigMatrix getRowMatrix(int row) throws MatrixIndexException {
        if ( !isValidCoordinate( row, 0)) {
            throw new MatrixIndexException("illegal row argument");
        }
        int ncols = this.getColumnDimension();
        BigDecimal[][] out = new BigDecimal[1][ncols]; 
        System.arraycopy(data[row], 0, out[0], 0, ncols);
        return new BigMatrixImpl(out);
    } 
    
    /**
     * Returns the entries in column number <code>column</code>
     * as a column matrix.  Column indices start at 0.
     *
     * @param column the column to be fetched
     * @return column matrix
     * @throws MatrixIndexException if the specified column index is invalid
     */
    public BigMatrix getColumnMatrix(int column) throws MatrixIndexException {
        if ( !isValidCoordinate( 0, column)) {
            throw new MatrixIndexException("illegal column argument");
        }
        int nRows = this.getRowDimension();
        BigDecimal[][] out = new BigDecimal[nRows][1]; 
        for (int row = 0; row < nRows; row++) {
            out[row][0] = data[row][column];
        }
        return new BigMatrixImpl(out);
    }
    
    /**
     * Returns the entries in row number <code>row</code> as an array.
     * <p>
     * Row indices start at 0.  A <code>MatrixIndexException</code> is thrown
     * unless <code>0 <= row < rowDimension.</code>
     *
     * @param row the row to be fetched
     * @return array of entries in the row
     * @throws MatrixIndexException if the specified row index is not valid
     */
    public BigDecimal[] getRow(int row) throws MatrixIndexException {
        if ( !isValidCoordinate( row, 0 ) ) {
            throw new MatrixIndexException("illegal row argument");
        }
        int ncols = this.getColumnDimension();
        BigDecimal[] out = new BigDecimal[ncols];
        System.arraycopy(data[row], 0, out, 0, ncols);
        return out;
    }
    
     /**
     * Returns the entries in row number <code>row</code> as an array
     * of double values.
     * <p>
     * Row indices start at 0.  A <code>MatrixIndexException</code> is thrown
     * unless <code>0 <= row < rowDimension.</code>
     *
     * @param row the row to be fetched
     * @return array of entries in the row
     * @throws MatrixIndexException if the specified row index is not valid
     */
    public double[] getRowAsDoubleArray(int row) throws MatrixIndexException {
        if ( !isValidCoordinate( row, 0 ) ) {
            throw new MatrixIndexException("illegal row argument");
        }
        int ncols = this.getColumnDimension();
        double[] out = new double[ncols];
        for (int i=0;i<ncols;i++) {
            out[i] = data[row][i].doubleValue();
        }
        return out;
    }
    
     /**
     * Returns the entries in column number <code>col</code> as an array.
     * <p>
     * Column indices start at 0.  A <code>MatrixIndexException</code> is thrown
     * unless <code>0 <= column < columnDimension.</code>
     *
     * @param col the column to be fetched
     * @return array of entries in the column
     * @throws MatrixIndexException if the specified column index is not valid
     */
    public BigDecimal[] getColumn(int col) throws MatrixIndexException {
        if ( !isValidCoordinate(0, col) ) {
            throw new MatrixIndexException("illegal column argument");
        }
        int nRows = this.getRowDimension();
        BigDecimal[] out = new BigDecimal[nRows];
        for (int i = 0; i < nRows; i++) {
            out[i] = data[i][col];
        }
        return out;
    }
    
    /**
     * Returns the entries in column number <code>col</code> as an array
     * of double values.
     * <p>
     * Column indices start at 0.  A <code>MatrixIndexException</code> is thrown
     * unless <code>0 <= column < columnDimension.</code>
     *
     * @param col the column to be fetched
     * @return array of entries in the column
     * @throws MatrixIndexException if the specified column index is not valid
     */
    public double[] getColumnAsDoubleArray(int col) throws MatrixIndexException {
        if ( !isValidCoordinate( 0, col ) ) {
            throw new MatrixIndexException("illegal column argument");
        }
        int nrows = this.getRowDimension();
        double[] out = new double[nrows];
        for (int i=0;i<nrows;i++) {
            out[i] = data[i][col].doubleValue();
        }
        return out;
    }
    
     /**
     * Returns the entry in the specified row and column.
     * <p>
     * Row and column indices start at 0 and must satisfy 
     * <ul>
     * <li><code>0 <= row < rowDimension</code></li>
     * <li><code> 0 <= column < columnDimension</code></li>
     * </ul>
     * otherwise a <code>MatrixIndexException</code> is thrown.
     *
     * @param row  row location of entry to be fetched  
     * @param column  column location of entry to be fetched
     * @return matrix entry in row,column
     * @throws MatrixIndexException if the row or column index is not valid
     */
    public BigDecimal getEntry(int row, int column)
    throws MatrixIndexException {
        if (!isValidCoordinate(row,column)) {
            throw new MatrixIndexException("matrix entry does not exist");
        }
        return data[row][column];
    }
    
    /**
     * Returns the entry in the specified row and column as a double.
     * <p>
     * Row and column indices start at 0 and must satisfy 
     * <ul>
     * <li><code>0 <= row < rowDimension</code></li>
     * <li><code> 0 <= column < columnDimension</code></li>
     * </ul>
     * otherwise a <code>MatrixIndexException</code> is thrown.
     *
     * @param row  row location of entry to be fetched
     * @param column  column location of entry to be fetched
     * @return matrix entry in row,column
     * @throws MatrixIndexException if the row
     * or column index is not valid
     */
    public double getEntryAsDouble(int row, int column) throws MatrixIndexException {
        return getEntry(row,column).doubleValue();
    }
    
    /**
     * Returns the transpose matrix.
     *
     * @return transpose matrix
     */
    public BigMatrix transpose() {
        int nRows = this.getRowDimension();
        int nCols = this.getColumnDimension();
        BigMatrixImpl out = new BigMatrixImpl(nCols, nRows);
        BigDecimal[][] outData = out.getDataRef();
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                outData[col][row] = data[row][col];
            }
        }
        return out;
    }
    
    /**
     * Returns the inverse matrix if this matrix is invertible.
     * 
     * @return inverse matrix
     * @throws InvalidMatrixException if this is not invertible
     */
    public BigMatrix inverse() throws InvalidMatrixException {
        return solve(MatrixUtils.createBigIdentityMatrix
                (this.getRowDimension()));
    }
    
    /**
     * Returns the determinant of this matrix.
     *
     * @return determinant
     * @throws InvalidMatrixException if matrix is not square
     */
    public BigDecimal getDeterminant() throws InvalidMatrixException {
        if (!isSquare()) {
            throw new InvalidMatrixException("matrix is not square");
        }
        if (isSingular()) {   // note: this has side effect of attempting LU decomp if lu == null
            return ZERO;
        } else {
            BigDecimal det = (parity == 1) ? ONE : ONE.negate();
            for (int i = 0; i < this.getRowDimension(); i++) {
                det = det.multiply(lu[i][i]);
            }
            return det;
        }
    }
    
     /**
     * Is this a square matrix?
     * @return true if the matrix is square (rowDimension = columnDimension)
     */
    public boolean isSquare() {
        return (this.getColumnDimension() == this.getRowDimension());
    }
    
    /**
     * Is this a singular matrix?
     * @return true if the matrix is singular
     */
    public boolean isSingular() {
        if (lu == null) {
            try {
                luDecompose();
                return false;
            } catch (InvalidMatrixException ex) {
                return true;
            }
        } else { // LU decomp must have been successfully performed
            return false; // so the matrix is not singular
        }
    }
    
    /**
     * Returns the number of rows in the matrix.
     *
     * @return rowDimension
     */
    public int getRowDimension() {
        return data.length;
    }
    
    /**
     * Returns the number of columns in the matrix.
     *
     * @return columnDimension
     */
    public int getColumnDimension() {
        return data[0].length;
    }
    
     /**
     * Returns the <a href="http://mathworld.wolfram.com/MatrixTrace.html">
     * trace</a> of the matrix (the sum of the elements on the main diagonal).
     *
     * @return trace
     * 
     * @throws IllegalArgumentException if this matrix is not square.
     */
    public BigDecimal getTrace() throws IllegalArgumentException {
        if (!isSquare()) {
            throw new IllegalArgumentException("matrix is not square");
        }
        BigDecimal trace = data[0][0];
        for (int i = 1; i < this.getRowDimension(); i++) {
            trace = trace.add(data[i][i]);
        }
        return trace;
    }
    
    /**
     * Returns the result of multiplying this by the vector <code>v</code>.
     *
     * @param v the vector to operate on
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    public BigDecimal[] operate(BigDecimal[] v) throws IllegalArgumentException {
        if (v.length != this.getColumnDimension()) {
            throw new IllegalArgumentException("vector has wrong length");
        }
        int nRows = this.getRowDimension();
        int nCols = this.getColumnDimension();
        BigDecimal[] out = new BigDecimal[v.length];
        for (int row = 0; row < nRows; row++) {
            BigDecimal sum = ZERO;
            for (int i = 0; i < nCols; i++) {
                sum = sum.add(data[row][i].multiply(v[i]));
            }
            out[row] = sum;
        }
        return out;
    }
    
    /**
     * Returns the result of multiplying this by the vector <code>v</code>.
     *
     * @param v the vector to operate on
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    public BigDecimal[] operate(double[] v) throws IllegalArgumentException {
        BigDecimal bd[] = new BigDecimal[v.length];
        for (int i=0;i<bd.length;i++) {
            bd[i] = new BigDecimal(v[i]);
        }
        return operate(bd);
    }
    
    /**
     * Returns the (row) vector result of premultiplying this by the vector <code>v</code>.
     *
     * @param v the row vector to premultiply by
     * @return v*this
     * @throws IllegalArgumentException if rowDimension != v.size()
     */
    public BigDecimal[] preMultiply(BigDecimal[] v) throws IllegalArgumentException {
        int nRows = this.getRowDimension();
        if (v.length != nRows) {
            throw new IllegalArgumentException("vector has wrong length");
        }
        int nCols = this.getColumnDimension();
        BigDecimal[] out = new BigDecimal[nCols];
        for (int col = 0; col < nCols; col++) {
            BigDecimal sum = ZERO;
            for (int i = 0; i < nRows; i++) {
                sum = sum.add(data[i][col].multiply(v[i]));
            }
            out[col] = sum;
        }
        return out;
    }
    
    /**
     * Returns a matrix of (column) solution vectors for linear systems with
     * coefficient matrix = this and constant vectors = columns of
     * <code>b</code>. 
     *
     * @param b  array of constants forming RHS of linear systems to
     * to solve
     * @return solution array
     * @throws IllegalArgumentException if this.rowDimension != row dimension
     * @throws InvalidMatrixException if this matrix is not square or is singular
     */
    public BigDecimal[] solve(BigDecimal[] b) throws IllegalArgumentException, InvalidMatrixException {
        int nRows = this.getRowDimension();
        if (b.length != nRows) {
            throw new IllegalArgumentException("constant vector has wrong length");
        }
        BigMatrix bMatrix = new BigMatrixImpl(b);
        BigDecimal[][] solution = ((BigMatrixImpl) (solve(bMatrix))).getDataRef();
        BigDecimal[] out = new BigDecimal[nRows];
        for (int row = 0; row < nRows; row++) {
            out[row] = solution[row][0];
        }
        return out;
    }
    
    /**
     * Returns a matrix of (column) solution vectors for linear systems with
     * coefficient matrix = this and constant vectors = columns of
     * <code>b</code>. 
     *
     * @param b  array of constants forming RHS of linear systems to
     * to solve
     * @return solution array
     * @throws IllegalArgumentException if this.rowDimension != row dimension
     * @throws InvalidMatrixException if this matrix is not square or is singular
     */
    public BigDecimal[] solve(double[] b) throws IllegalArgumentException, InvalidMatrixException {
        BigDecimal bd[] = new BigDecimal[b.length];
        for (int i=0;i<bd.length;i++) {
            bd[i] = new BigDecimal(b[i]);
        }
        return solve(bd);
    }
    
    /**
     * Returns a matrix of (column) solution vectors for linear systems with
     * coefficient matrix = this and constant vectors = columns of
     * <code>b</code>. 
     *
     * @param b  matrix of constant vectors forming RHS of linear systems to
     * to solve
     * @return matrix of solution vectors
     * @throws IllegalArgumentException if this.rowDimension != row dimension
     * @throws InvalidMatrixException if this matrix is not square or is singular
     */
    public BigMatrix solve(BigMatrix b) throws IllegalArgumentException, InvalidMatrixException  {
        if (b.getRowDimension() != this.getRowDimension()) {
            throw new IllegalArgumentException("Incorrect row dimension");
        }
        if (!this.isSquare()) {
            throw new InvalidMatrixException("coefficient matrix is not square");
        }
        if (this.isSingular()) { // side effect: compute LU decomp
            throw new InvalidMatrixException("Matrix is singular.");
        }
        
        int nCol = this.getColumnDimension();
        int nColB = b.getColumnDimension();
        int nRowB = b.getRowDimension();
        
        // Apply permutations to b
        BigDecimal[][] bp = new BigDecimal[nRowB][nColB];
        for (int row = 0; row < nRowB; row++) {
            for (int col = 0; col < nColB; col++) {
                bp[row][col] = b.getEntry(permutation[row], col);
            }
        }
        
        // Solve LY = b
        for (int col = 0; col < nCol; col++) {
            for (int i = col + 1; i < nCol; i++) {
                for (int j = 0; j < nColB; j++) {
                    bp[i][j] = bp[i][j].subtract(bp[col][j].multiply(lu[i][col]));
                }
            }
        }
        
        // Solve UX = Y
        for (int col = nCol - 1; col >= 0; col--) {
            for (int j = 0; j < nColB; j++) {
                bp[col][j] = bp[col][j].divide(lu[col][col], scale, roundingMode);
            }
            for (int i = 0; i < col; i++) {
                for (int j = 0; j < nColB; j++) {
                    bp[i][j] = bp[i][j].subtract(bp[col][j].multiply(lu[i][col]));
                }
            }
        }
        
        BigMatrixImpl outMat = new BigMatrixImpl(bp);
        return outMat;
    }
    
    /**
     * Computes a new 
     * <a href="http://www.math.gatech.edu/~bourbaki/math2601/Web-notes/2num.pdf">
     * LU decompostion</a> for this matrix, storing the result for use by other methods. 
     * <p>
     * <strong>Implementation Note</strong>:<br>
     * Uses <a href="http://www.damtp.cam.ac.uk/user/fdl/people/sd/lectures/nummeth98/linear.htm">
     * Crout's algortithm</a>, with partial pivoting.
     * <p>
     * <strong>Usage Note</strong>:<br>
     * This method should rarely be invoked directly. Its only use is
     * to force recomputation of the LU decomposition when changes have been
     * made to the underlying data using direct array references. Changes
     * made using setXxx methods will trigger recomputation when needed
     * automatically.
     *
     * @throws InvalidMatrixException if the matrix is non-square or singular.
     */
    public void luDecompose() throws InvalidMatrixException {
        
        int nRows = this.getRowDimension();
        int nCols = this.getColumnDimension();
        if (nRows != nCols) {
            throw new InvalidMatrixException("LU decomposition requires that the matrix be square.");
        }
        lu = this.getData();
        
        // Initialize permutation array and parity
        permutation = new int[nRows];
        for (int row = 0; row < nRows; row++) {
            permutation[row] = row;
        }
        parity = 1;
        
        // Loop over columns
        for (int col = 0; col < nCols; col++) {
            
            BigDecimal sum = ZERO;
            
            // upper
            for (int row = 0; row < col; row++) {
                sum = lu[row][col];
                for (int i = 0; i < row; i++) {
                    sum = sum.subtract(lu[row][i].multiply(lu[i][col]));
                }
                lu[row][col] = sum;
            }
            
            // lower
            int max = col; // permutation row
            BigDecimal largest = ZERO;
            for (int row = col; row < nRows; row++) {
                sum = lu[row][col];
                for (int i = 0; i < col; i++) {
                    sum = sum.subtract(lu[row][i].multiply(lu[i][col]));
                }
                lu[row][col] = sum;
                
                // maintain best permutation choice
                if (sum.abs().compareTo(largest) == 1) {
                    largest = sum.abs();
                    max = row;
                }
            }
            
            // Singularity check
            if (lu[max][col].abs().compareTo(TOO_SMALL) <= 0) {
                lu = null;
                throw new InvalidMatrixException("matrix is singular");
            }
            
            // Pivot if necessary
            if (max != col) {
                BigDecimal tmp = ZERO;
                for (int i = 0; i < nCols; i++) {
                    tmp = lu[max][i];
                    lu[max][i] = lu[col][i];
                    lu[col][i] = tmp;
                }
                int temp = permutation[max];
                permutation[max] = permutation[col];
                permutation[col] = temp;
                parity = -parity;
            }
            
            //Divide the lower elements by the "winning" diagonal elt.
            for (int row = col + 1; row < nRows; row++) {
                lu[row][col] = lu[row][col].divide(lu[col][col], scale, roundingMode);
            }
            
        }
        
    }
    
    /**
     * 
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("BigMatrixImpl{");
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                if (i > 0)
                    res.append(",");
                res.append("{");
                for (int j = 0; j < data[0].length; j++) {
                    if (j > 0)
                        res.append(",");
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
     * <code>BigMatrixImpl</code> instance with the same dimensions as this
     * and all corresponding matrix entries are equal.  BigDecimal.equals
     * is used to compare corresponding entries.
     * 
     * @param object the object to test equality against.
     * @return true if object equals this
     */
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
        if (object instanceof BigMatrixImpl == false) {
            return false;
        }
        BigMatrix m = (BigMatrix) object;
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
            return false;
        }
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                if (!data[row][col].equals(m.getEntry(row, col))) {
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
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        ret = ret * 31 + nRows;
        ret = ret * 31 + nCols;
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                ret = ret * 31 + (11 * (row+1) + 17 * (col+1)) * 
                data[row][col].hashCode();
            }
        }   
        return ret;
    }
    
    //------------------------ Protected methods
    
    /**
     * Returns <code>dimension x dimension</code> identity matrix.
     *
     * @param dimension dimension of identity matrix to generate
     * @return identity matrix
     * @throws IllegalArgumentException if dimension is not positive
     * @deprecated  use {@link MatrixUtils#createBigIdentityMatrix}
     */
    protected BigMatrix getIdentity(int dimension) {
        return MatrixUtils.createBigIdentityMatrix(dimension);
    }
    
    /**
     *  Returns the LU decomposition as a BigMatrix.
     *  Returns a fresh copy of the cached LU matrix if this has been computed; 
     *  otherwise the composition is computed and cached for use by other methods.   
     *  Since a copy is returned in either case, changes to the returned matrix do not 
     *  affect the LU decomposition property. 
     * <p>
     * The matrix returned is a compact representation of the LU decomposition. 
     * Elements below the main diagonal correspond to entries of the "L" matrix;   
     * elements on and above the main diagonal correspond to entries of the "U"
     * matrix.
     * <p>
     * Example: <pre>
     * 
     *     Returned matrix                L                  U
     *         2  3  1                   1  0  0            2  3  1          
     *         5  4  6                   5  1  0            0  4  6
     *         1  7  8                   1  7  1            0  0  8          
     * </pre>
     * 
     * The L and U matrices satisfy the matrix equation LU = permuteRows(this), <br>
     *  where permuteRows reorders the rows of the matrix to follow the order determined
     *  by the <a href=#getPermutation()>permutation</a> property.
     * 
     * @return LU decomposition matrix
     * @throws InvalidMatrixException if the matrix is non-square or singular.
     */
    protected BigMatrix getLUMatrix() throws InvalidMatrixException {
        if (lu == null) {
            luDecompose();
        }
        return new BigMatrixImpl(lu);
    }
    
    /**
     * Returns the permutation associated with the lu decomposition.
     * The entries of the array represent a permutation of the numbers 0, ... , nRows - 1.
     * <p>
     * Example:
     * permutation = [1, 2, 0] means current 2nd row is first, current third row is second
     * and current first row is last.
     * <p>
     * Returns a fresh copy of the array.
     * 
     * @return the permutation
     */
    protected int[] getPermutation() {
        int[] out = new int[permutation.length];
        System.arraycopy(permutation, 0, out, 0, permutation.length);
        return out;
    }
    
    //------------------------ Private methods
    
    /**
     * Returns a fresh copy of the underlying data array.
     *
     * @return a copy of the underlying data array.
     */
    private BigDecimal[][] copyOut() {
        int nRows = this.getRowDimension();
        BigDecimal[][] out = new BigDecimal[nRows][this.getColumnDimension()];
        // can't copy 2-d array in one shot, otherwise get row references
        for (int i = 0; i < nRows; i++) {
            System.arraycopy(data[i], 0, out[i], 0, data[i].length);
        }
        return out;
    }
    
    /**
     * Replaces data with a fresh copy of the input array.
     * <p>
     * Verifies that the input array is rectangular and non-empty.
     *
     * @param in data to copy in
     * @throws IllegalArgumentException if input array is emtpy or not
     *    rectangular
     * @throws NullPointerException if input array is null
     */
    private void copyIn(BigDecimal[][] in) {
        setSubMatrix(in,0,0);
    }
    
    /**
     * Replaces data with a fresh copy of the input array.
     *
     * @param in data to copy in
     */
    private void copyIn(double[][] in) {
        int nRows = in.length;
        int nCols = in[0].length;
        data = new BigDecimal[nRows][nCols];
        for (int i = 0; i < nRows; i++) {
            for (int j=0; j < nCols; j++) {
                data[i][j] = new BigDecimal(in[i][j]);
            }
        }
        lu = null;
    }
    
    /**
     * Replaces data with BigDecimals represented by the strings in the input
     * array.
     *
     * @param in data to copy in
     */
    private void copyIn(String[][] in) {
        int nRows = in.length;
        int nCols = in[0].length;
        data = new BigDecimal[nRows][nCols];
        for (int i = 0; i < nRows; i++) {
            for (int j=0; j < nCols; j++) {
                data[i][j] = new BigDecimal(in[i][j]);
            }
        }
        lu = null;
    }
    
    /**
     * Tests a given coordinate as being valid or invalid
     *
     * @param row the row index.
     * @param col the column index.
     * @return true if the coordinate is with the current dimensions
     */
    private boolean isValidCoordinate(int row, int col) {
        int nRows = this.getRowDimension();
        int nCols = this.getColumnDimension();
        
        return !(row < 0 || row >= nRows || col < 0 || col >= nCols);
    }
    
}
