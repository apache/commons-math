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
 * Interface defining a real-valued matrix with basic algebraic operations
 * @author  Phil Steitz
 * @version $Revision: 1.1 $ $Date: 2003/05/12 19:03:41 $
 */
public interface RealMatrix {

    public RealMatrix copy();
    
    /**
     * Compute the sum of *this and m
     * @param m    matrix to be added
     * @return     this + m
     * @exception  IllegalArgumentException if m is not the same size as *this
     */
    public RealMatrix add(RealMatrix m);   
    
    /**
     * Compute *this minus m
     * @param m    matrix to be subtracted
     * @return     this + m
     * @exception  IllegalArgumentException if m is not the same size as *this
     */
    public RealMatrix subtract(RealMatrix m);   
    
    /**
     * Returns the rank of the matrix
     * @return     the rank of this matrix
     */
    public int getRank();
    
     /**
     * Returns the result of adding d to each entry of *this
     * @param d    value to be added to each entry
     * @return     d + this
     */
    public RealMatrix scalarAdd(double d);
    
    /**
     * Returns the result multiplying each entry of *this by d
     * @param d    value to multiply all entries by
     * @return     d*this
     */
    public RealMatrix scalarMultiply(double d);
    
    /**
     * Returns the result postmultiplyin *this by m
     * @param m    matrix to postmultiply by
     * @return     this*m
     * @throws     IllegalArgumentException 
     *             if columnDimension(this) != rowDimension(m)
     */
    public RealMatrix multiply(RealMatrix m);
    
    /**
     * Returns matrix entries as a two-dimensional array
     * @return    2-dimensional array of entries
     */
    public double[][] getData();
    
    /**
     * Sets/overwrites the underlying data for the matrix
     * @param    2-dimensional array of entries
     */
    public void setData(double[][] data);
    
    /**
     * Returns the norm of the matrix
     * @return norm
     */
    public double getNorm();
    
    /**
     * Returns entries in row as an array
     * @param row  the row to be fetched
     * @return     array of entries in the row
     * @throws     IllegalArgumentException if row > rowDimension
     */
    public double[] getRow(int row);
    
    /**
     * Returns entries in column as an array
     * @param col  column to fetch
     * @return     array of entries in the column
     * @throws     IllegalArgumentException if column > columnDimension
     */
    public double[] getColumn(int col);
    
    /**
     * Returns the entry in the specified row and column
     * @param row  row location of entry to be fetched  
     * @param col  column location of entry to be fetched 
     * @return     matrix entry in row,column
     * @throws     IllegalArgumentException if entry does not exist
     */
    public double getEntry(int row, int column);
    
    /**
     * Sets the entry in the specified row and column to the specified value
     * @param row    row location of entry to be set 
     * @param col    column location of entry to be set
     * @param value  value to set 
     * @throws IllegalArgumentException if entry does not exist
     */
    public void setEntry(int row, int column, double value);
    
    /**
     * Returns the transpose of this matrix
     * @return transpose matrix
     */
    public RealMatrix transpose();
    
    /**
     * Returns the inverse of this matrix
     * @return inverse matrix
     * @throws IllegalArgumentException if *this is not invertible
     */
    public RealMatrix inverse();
    
    /**
     * Returns the determinant of this matrix
     * @returns determinant
     */
    public double getDeterminant();
    
    /**
     * Is this a square matrix?
     * @return true if the matrix is square (rowDimension = columnDimension)
     */
    public boolean isSquare();
    
    /**
     * Is this a singular matrix?
     * @return true if the matrix is singular
     */
    public boolean isSingular();
    
    /**
     * Returns the number of rows in the matrix
     * @return rowDimension
     */
    public int getRowDimension();
    
    /**
     * Returns the number of columns in the matrix
     * @return columnDimension
     */
    public int getColumnDimension();
    
    /**
     * Returns the trace of the matrix
     * @return trace
     */
    public double getTrace();
    
    /**
     * Returns the result of multiplying this by vector v
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    public double[] operate(double[] v);
    
    /**
     * Returns the result of premultiplying this by vector v
     * @return v*this
     * @throws IllegalArgumentException if rowDimension != v.size()
     */
    public RealMatrix preMultiply(double[] v);    
}

