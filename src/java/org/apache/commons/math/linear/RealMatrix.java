/*
 * Copyright 2003-2004 The Apache Software Foundation.
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

/**
 * Interface defining a real-valued matrix with basic algebraic operations
 * @version $Revision: 1.16 $ $Date: 2004/04/27 04:37:58 $
 */
public interface RealMatrix {

    /**
     * Returns a (deep) copy of this.
     *
     * @return matrix copy
     */
    RealMatrix copy();
    
    /**
     * Compute the sum of this and m.
     *
     * @param m    matrix to be added
     * @return     this + m
     * @exception  IllegalArgumentException if m is not the same size as this
     */
    RealMatrix add(RealMatrix m) throws IllegalArgumentException;   
    
    /**
     * Compute this minus m.
     *
     * @param m    matrix to be subtracted
     * @return     this + m
     * @exception  IllegalArgumentException if m is not the same size as this
     */
    RealMatrix subtract(RealMatrix m) throws IllegalArgumentException;   
    
    /**
     * Returns the rank of the matrix.
     *
     * @return the rank of this matrix
     */
    int getRank();
    
     /**
     * Returns the result of adding d to each entry of this.
     *
     * @param d    value to be added to each entry
     * @return     d + this
     */
    RealMatrix scalarAdd(double d);
    
    /**
     * Returns the result multiplying each entry of this by d.
     *
     * @param d    value to multiply all entries by
     * @return     d * this
     */
    RealMatrix scalarMultiply(double d);
    
    /**
     * Returns the result postmultiplying this by m.
     *
     * @param m    matrix to postmultiply by
     * @return     this * m
     * @throws     IllegalArgumentException 
     *             if columnDimension(this) != rowDimension(m)
     */
    RealMatrix multiply(RealMatrix m) throws IllegalArgumentException;
    
    /**
     * Returns the result premultiplying this by <code>m</code>.
     * @param m    matrix to premultiply by
     * @return     m * this
     * @throws     IllegalArgumentException
     *             if rowDimension(this) != columnDimension(m)
     */
    public RealMatrix preMultiply(RealMatrix m) throws IllegalArgumentException;
    
    /**
     * Returns matrix entries as a two-dimensional array.
     *
     * @return    2-dimensional array of entries
     */
    double[][] getData();
    
    /**
     * Overwrites the underlying data for the matrix with
     * a fresh copy of <code>data</code>.
     *
     * @param  data  2-dimensional array of entries
     */
    void setData(double[][] data);
    
    /**
     * Returns the <a href="http://mathworld.wolfram.com/MaximumAbsoluteRowSumNorm.html">
     * maximum absolute row sum norm</a> of the matrix.
     *
     * @return norm
     */
    double getNorm();
    
    /**
     * Returns the entries in row number <code>row</code> as an array.
     *
     * @param row the row to be fetched
     * @return array of entries in the row
     * @throws MatrixIndexException if the specified row is greater 
     *                              than the number of rows in this matrix
     */
    double[] getRow(int row) throws MatrixIndexException;
    
    /**
     * Returns the entries in column number <code>col</code> as an array.
     *
     * @param col  column to fetch
     * @return array of entries in the column
     * @throws MatrixIndexException if the specified column is greater
     *                              than the number of columns in this matrix
     */
    double[] getColumn(int col) throws MatrixIndexException;
    
    /**
     * Returns the entry in the specified row and column.
     *
     * @param row  row location of entry to be fetched  
     * @param column  column location of entry to be fetched
     * @return matrix entry in row,column
     * @throws MatrixIndexException if the specified coordinate is outside 
     *                              the dimensions of this matrix
     */
    double getEntry(int row, int column) throws MatrixIndexException;
    
    /**
     * Sets the entry in the specified row and column to the specified value.
     *
     * @param row    row location of entry to be set 
     * @param column    column location of entry to be set
     * @param value  value to set 
     * @throws MatrixIndexException if the specified coordinate is outside
     *                              he dimensions of this matrix
     */
    void setEntry(int row, int column, double value) 
        throws MatrixIndexException;
    
    /**
     * Returns the transpose of this matrix.
     *
     * @return transpose matrix
     */
    RealMatrix transpose();
    
    /**
     * Returns the inverse of this matrix.
     *
     * @return inverse matrix
     * @throws InvalidMatrixException if  this is not invertible
     */
    RealMatrix inverse() throws InvalidMatrixException;
    
    /**
     * Returns the determinant of this matrix.
     *
     * @return determinant
     */
    double getDeterminant();
    
    /**
     * Is this a square matrix?
     * @return true if the matrix is square (rowDimension = columnDimension)
     */
    boolean isSquare();
    
    /**
     * Is this a singular matrix?
     * @return true if the matrix is singular
     */
    boolean isSingular();
    
    /**
     * Returns the number of rows in the matrix.
     *
     * @return rowDimension
     */
    int getRowDimension();
    
    /**
     * Returns the number of columns in the matrix.
     *
     * @return columnDimension
     */
    int getColumnDimension();
    
    /**
     * Returns the <a href="http://mathworld.wolfram.com/MatrixTrace.html">
     * trace</a> of the matrix (the sum of the elements on the main diagonal).
     *
     * @return trace
     */
    double getTrace();
    
    /**
     * Returns the result of multiplying this by the vector <code>v</code>.
     *
     * @param v the vector to operate on
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    double[] operate(double[] v) throws IllegalArgumentException;
    
    /**
     * Returns the (row) vector result of premultiplying this by the vector <code>v</code>.
     *
     * @param v the row vector to premultiply by
     * @return v*this
     * @throws IllegalArgumentException if rowDimension != v.size()
     */
    double[] preMultiply(double[] v) throws IllegalArgumentException;  
    
    /**
     * Returns the solution vector for a linear system with coefficient
     * matrix = this and constant vector = <code>b</code>.
     *
     * @param b  constant vector
     * @return vector of solution values to AX = b, where A is *this
     * @throws IllegalArgumentException if this.rowDimension != b.length 
     * @throws InvalidMatrixException if this matrix is not square or is singular
     */
    double[] solve(double[] b) throws IllegalArgumentException, InvalidMatrixException;
    
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
    RealMatrix solve(RealMatrix b) throws IllegalArgumentException, InvalidMatrixException;
}

