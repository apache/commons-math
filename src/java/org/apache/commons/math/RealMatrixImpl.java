/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.commons.math;

/**
 * Implementation for RealMatrix using double[][] array
 * @author  Phil Stetiz
 * @version $Revision: 1.1 $ $Date: 2003/05/12 19:04:10 $
 */
public class RealMatrixImpl implements RealMatrix {

    private double data[][];
    
    public RealMatrixImpl() {
    }

     /**
     * Create a new RealMatrix with the supplied row and column dimensions
     * @param rowDimension      the number of rows in the new matrix
     * @param columnDimension   the number of columns in the new matrix
     * @return                  newly created matrix
     */ 
    public RealMatrixImpl(int rowDimension,
        int columnDimension) {
        data = new double[rowDimension][columnDimension];
    }
    
    public RealMatrixImpl(double[][] data) {
        this.data = data;
    }
    
    /**
     * Create a new RealMatrix which is a copy of *this
     * @return  the cloned matrix
     */
    public RealMatrix copy() {
        return null;
    }
    
    /**
     * Compute the sum of *this and m
     * @param m    matrix to be added
     * @return     this + m
     * @exception  IllegalArgumentException if m is not the same size as *this
     */
    public RealMatrix add(RealMatrix m) {
        if (this.getColumnDimension() != m.getColumnDimension() ||
            this.getRowDimension() != m.getRowDimension()) {
                throw new IllegalArgumentException("matrix dimension mismatch");
        }
        int rowCount = this.getRowDimension();
        int columnCount = this.getColumnDimension();
        double[][] outData = new double[rowCount][columnCount];
        double[][] mData = m.getData();
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                outData[row][col] = data[row][col] + mData[row][col];
            }
        }
        return new RealMatrixImpl(outData);
    }
    
    /**
     * Compute *this minus m
     * @param m    matrix to be subtracted
     * @return     this + m
     * @exception  IllegalArgumentException if m is not the same size as *this
     */
    public RealMatrix subtract(RealMatrix m) {
        if (this.getColumnDimension() != m.getColumnDimension() ||
            this.getRowDimension() != m.getRowDimension()) {
                throw new IllegalArgumentException("matrix dimension mismatch");
        }
        int rowCount = this.getRowDimension();
        int columnCount = this.getColumnDimension();
        double[][] outData = new double[rowCount][columnCount];
        double[][] mData = m.getData();
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                outData[row][col] = data[row][col] - mData[row][col];
            }
        }
        return new RealMatrixImpl(outData);
    }
    
    /**
     * Returns the rank of the matrix
     * @return     the rank of this matrix
     */
    public int getRank() {
        throw new UnsupportedOperationException("not implemented yet");
    }
        
    
     /**
     * Returns the result of adding d to each entry of *this
     * @param d    value to be added to each entry
     * @return     d + this
     */
    public RealMatrix scalarAdd(double d) {
        int rowCount = this.getRowDimension();
        int columnCount = this.getColumnDimension();
        double[][] outData = new double[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                outData[row][col] = data[row][col] + d;
            }
        }
        return new RealMatrixImpl(outData);
    }
     
    /**
     * Returns the result multiplying each entry of *this by d
     * @param d    value to multiply all entries by
     * @return     d*this
     */
    public RealMatrix scalarMultiply(double d) {
        int rowCount = this.getRowDimension();
        int columnCount = this.getColumnDimension();
        double[][] outData = new double[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                outData[row][col] = data[row][col]*d;
            }
        }
        return new RealMatrixImpl(outData);
    }
    
    /**
     * Returns the result postmultiplying *this by m
     * @param m    matrix to postmultiply by
     * @return     this*m
     * @throws     IllegalArgumentException 
     *             if columnDimension(this) != rowDimension(m)
     */
    public RealMatrix multiply(RealMatrix m) {
      if (this.getColumnDimension() != m.getRowDimension()) {
         throw new IllegalArgumentException
            ("Matrices are not multiplication compatible.");
      }
      double[][] mData = m.getData();
      double[][] outData = 
        new double[this.getRowDimension()][m.getColumnDimension()];
      double sum = 0;
      for (int row = 0; row < this.getRowDimension(); row++) {
         for (int col = 0; col < m.getColumnDimension(); col++) {
            sum = 0;
            for (int i = 0; i < this.getColumnDimension(); i++) {
                sum += data[row][i] * mData[i][col];
            }
            outData[row][col] = sum;
         }
      }
      return new RealMatrixImpl(outData);
    }
    
    /**
     * Returns matrix entries as a two-dimensional array
     * @return    2-dimensional array of entries
     */
    public double[][] getData() {
        return data;
    }
    
    /**
     * Sets/overwrites the underlying data for the matrix
     * @param    2-dimensional array of entries
     */
    public void setData(double[][] data) {
        this.data = data;
    }
    
    /**
     * Returns the 1-norm of the matrix (max column sum) 
     * @return norm
     */
    public double getNorm() {
      double maxColSum = 0;
      for (int col = 0; col < this.getColumnDimension(); col++) {
         double sum = 0;
         for (int row = 0; row < this.getRowDimension(); row++) {
            sum += Math.abs(data[row][col]);
         }
         maxColSum = Math.max(maxColSum,sum);
      }
      return maxColSum;
    }
    
    /**
     * Returns entries in row as an array
     * @param row  the row to be fetched
     * @return     array of entries in the row
     * @throws     IllegalArgumentException if row > rowDimension
     */
    public double[] getRow(int row) {
        return data[row];
    }
    
    /**
     * Returns entries in column as an array
     * @param col  column to fetch
     * @return     array of entries in the column
     * @throws     IllegalArgumentException if column > columnDimension
     */
    public double[] getColumn(int col) {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    /**
     * Returns the entry in the specified row and column
     * @param row  row location of entry to be fetched  
     * @param col  column location of entry to be fetched 
     * @return     matrix entry in row,column
     * @throws     IllegalArgumentException if entry does not exist
     */
    public double getEntry(int row, int column) {
        if (row < 1 || column < 1 || row > this.getRowDimension() 
            || column > this.getColumnDimension()) {
                throw new IllegalArgumentException
                    ("matrix entry does not exist");
        }
        return data[row-1][column-1];
    }
    
    /**
     * Sets the entry in the specified row and column to the specified value
     * @param row    row location of entry to be set 
     * @param col    column location of entry to be set
     * @param value  value to set 
     * @throws IllegalArgumentException if entry does not exist
     */
    public void setEntry(int row, int column, double value) {
        if (row < 1 || column < 1 || row > this.getRowDimension()
            || column > this.getColumnDimension()) {
                throw new IllegalArgumentException
                    ("matrix entry does not exist");
        }
        data[row-1][column-1] = value;
    }
    
    /**
     * Returns the transpose of this matrix
     * @return transpose matrix
     */
    public RealMatrix transpose() {
        throw new UnsupportedOperationException("not implemented yet");
    }
        
    
    /**
     * Returns the inverse of this matrix
     * @return inverse matrix
     * @throws IllegalArgumentException if *this is not invertible
     */
    public RealMatrix inverse() {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    /**
     * Returns the determinant of this matrix
     * @return determinant
     */
    public double getDeterminant() {
        throw new UnsupportedOperationException("not implemented yet");
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
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    /**
     * Returns the number of rows in the matrix
     * @return rowDimension
     */
    public int getRowDimension() {
        return data.length;  
    }
    
    /**
     * Returns the number of columns in the matrix
     * @return columnDimension
     */
    public int getColumnDimension() {
        return data[0].length;
    }
    
    /**
     * Returns the trace of the matrix
     * @return trace
     */
    public double getTrace() {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    /**
     * Returns the result of multiplying this by the vector b
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    public double[] operate(double[] v) {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    /**
     * Returns the result of premultiplying this by the vector v
     * @return v*this
     * @throws IllegalArgumentException if rowDimension != v.size()
     */
    public RealMatrix preMultiply(double[] v) {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
}
