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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
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

package org.apache.commons.math.linear;

/**
 * Interface defining a real-valued matrix with basic algebraic operations
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:09:45 $
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
     * Returns the <a href="http://mathworld.wolfram.com/
     * MaximumAbsoluteRowSumNorm.html">maximum absolute row sum norm</a> 
     * of the matrix.
     *
     * @return norm
     */
    double getNorm();
    
    /**
     * Returns the entries in row number <code>row</code> as an array.
     *
     * @param row the row to be fetched
     * @return array of entries in the row
     * @throws IllegalArgumentException if row > rowDimension
     */
    double[] getRow(int row) throws IllegalArgumentException;
    
    /**
     * Returns the entries in column number <code>col</code> as an array.
     *
     * @param col  column to fetch
     * @return array of entries in the column
     * @throws IllegalArgumentException if column > columnDimension
     */
    double[] getColumn(int col) throws IllegalArgumentException;
    
    /**
     * Returns the entry in the specified row and column.
     *
     * @param row  row location of entry to be fetched  
     * @param column  column location of entry to be fetched
     * @return     matrix entry in row,column
     * @throws     IllegalArgumentException if entry does not exist
     */
    double getEntry(int row, int column) throws IllegalArgumentException;
    
    /**
     * Sets the entry in the specified row and column to the specified value.
     *
     * @param row    row location of entry to be set 
     * @param column    column location of entry to be set
     * @param value  value to set 
     * @throws IllegalArgumentException if entry does not exist
     */
    void setEntry(int row, int column, double value) 
        throws IllegalArgumentException;
    
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
     * @throws IllegalArgumentException if *this is not invertible
     */
    RealMatrix inverse() throws IllegalArgumentException;
    
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
     * Returns the result of premultiplying this by the vector <code>v</code>.
     *
     * @param v the row vector to premultiply by
     * @return v*this
     * @throws IllegalArgumentException if rowDimension != v.size()
     */
    RealMatrix preMultiply(double[] v) throws IllegalArgumentException;  
    
    /**
     * Returns the solution vector for a linear system with coefficient
     * matrix = this and constant vector = <code>b</code>.
     *
     * @param b  constant vector
     * @return   vector of solution values to AX = b, where A is *this
     * @throws   IllegalArgumentException if rowDimension != b.length or matrix 
     *           is singular
     */
    double[] solve(double[] b) throws IllegalArgumentException;
    
    /**
     * Returns a matrix of (column) solution vectors for linear systems with
     * coefficient matrix = this and constant vectors = columns of
     * <code>b</code>. 
     *
     * @param b  matrix of constant vectors forming RHS of linear systems to
     * to solve
     * @return matrix of solution vectors
     * @throws IllegalArgumentException if rowDimension != row dimension of b
     * or this is not square or singular
     */
    RealMatrix solve(RealMatrix b) throws IllegalArgumentException;
}

