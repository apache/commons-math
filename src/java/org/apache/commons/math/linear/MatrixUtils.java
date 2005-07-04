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

import java.math.BigDecimal;

/**
 * A collection of static methods that operate on or return matrices.
 * 
 * @version $Revision$ $Date$
 */
public class MatrixUtils {

    /**
     * Default constructor.  Package scope to prevent unwanted instantiation. 
     */
    public MatrixUtils() {
        super();
    }
    
    /**
     * Returns a {@link RealMatrix} whose entries are the the values in the
     * the input array.  The input array is copied, not referenced.
     * 
     * @param data input array
     * @return  RealMatrix containing the values of the array
     * @throws IllegalArgumentException if <code>data</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if data is null
     */
    public static RealMatrix createRealMatrix(double[][] data) {
        return new RealMatrixImpl(data);
    }
    
    /**
     * Returns <code>dimension x dimension</code> identity matrix.
     *
     * @param dimension dimension of identity matrix to generate
     * @return identity matrix
     * @throws IllegalArgumentException if dimension is not positive
     * @since 1.1
     */
    public static RealMatrix createRealIdentityMatrix(int dimension) {
        RealMatrixImpl out = new RealMatrixImpl(dimension, dimension);
        double[][] d = out.getDataRef();
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                d[row][col] = row == col ? 1d : 0d;
            }
        }
        return out;
    }
    
    /**
     * Returns a {@link BigMatrix} whose entries are the the values in the
     * the input array.  The input array is copied, not referenced.
     * 
     * @param data input array
     * @return  RealMatrix containing the values of the array
     * @throws IllegalArgumentException if <code>data</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if data is null
     */
    public static BigMatrix createBigMatrix(double[][] data) {
        return new BigMatrixImpl(data);
    }
    
    /**
     * Returns a {@link BigMatrix} whose entries are the the values in the
     * the input array.  The input array is copied, not referenced.
     * 
     * @param data input array
     * @return  RealMatrix containing the values of the array
     * @throws IllegalArgumentException if <code>data</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if data is null
     */
    public static BigMatrix createBigMatrix(BigDecimal[][] data) {
        return new BigMatrixImpl(data);
    }
    
    /**
     * Returns a {@link BigMatrix} whose entries are the the values in the
     * the input array.  The input array is copied, not referenced.
     * 
     * @param data input array
     * @return  RealMatrix containing the values of the array
     * @throws IllegalArgumentException if <code>data</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if data is null
     */
    public static BigMatrix createBigMatrix(String[][] data) {
        return new BigMatrixImpl(data);
    }
    
    /**
     * Creates a row {@link RealMatrix} using the data from the input
     * array. 
     * 
     * @param rowData the input row data
     * @return a 1 x rowData.length RealMatrix
     * @throws IllegalArgumentException if <code>rowData</code> is empty
     * @throws NullPointerException if <code>rowData</code>is null
     */
    public static RealMatrix createRowRealMatrix(double[] rowData) {
        int nCols = rowData.length;
        double[][] data = new double[1][nCols];
        System.arraycopy(rowData, 0, data[0], 0, nCols);
        return new RealMatrixImpl(data);
    }
    
    /**
     * Creates a row {@link BigMatrix} using the data from the input
     * array. 
     * 
     * @param rowData the input row data
     * @return a 1 x rowData.length BigMatrix
     * @throws IllegalArgumentException if <code>rowData</code> is empty
     * @throws NullPointerException if <code>rowData</code>is null
     */
    public static BigMatrix createRowBigMatrix(double[] rowData) {
        int nCols = rowData.length;
        double[][] data = new double[1][nCols];
        System.arraycopy(rowData, 0, data[0], 0, nCols);
        return new BigMatrixImpl(data);
    }
    
    /**
     * Creates a row {@link BigMatrix} using the data from the input
     * array. 
     * 
     * @param rowData the input row data
     * @return a 1 x rowData.length BigMatrix
     * @throws IllegalArgumentException if <code>rowData</code> is empty
     * @throws NullPointerException if <code>rowData</code>is null
     */
    public static BigMatrix createRowBigMatrix(BigDecimal[] rowData) {
        int nCols = rowData.length;
        BigDecimal[][] data = new BigDecimal[1][nCols];
        System.arraycopy(rowData, 0, data[0], 0, nCols);
        return new BigMatrixImpl(data);
    }
    
    /**
     * Creates a row {@link BigMatrix} using the data from the input
     * array. 
     * 
     * @param rowData the input row data
     * @return a 1 x rowData.length BigMatrix
     * @throws IllegalArgumentException if <code>rowData</code> is empty
     * @throws NullPointerException if <code>rowData</code>is null
     */
    public static BigMatrix createRowBigMatrix(String[] rowData) {
        int nCols = rowData.length;
        String[][] data = new String[1][nCols];
        System.arraycopy(rowData, 0, data[0], 0, nCols);
        return new BigMatrixImpl(data);
    }
    
    /**
     * Creates a column {@link RealMatrix} using the data from the input
     * array.
     * 
     * @param columnData  the input column data
     * @return a columnData x 1 RealMatrix
     * @throws IllegalArgumentException if <code>columnData</code> is empty
     * @throws NullPointerException if <code>columnData</code>is null
     */
    public static RealMatrix createColumnRealMatrix(double[] columnData) {
        int nRows = columnData.length;
        double[][] data = new double[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = columnData[row];
        }
        return new RealMatrixImpl(data);
    }
    
    /**
     * Creates a column {@link BigMatrix} using the data from the input
     * array.
     * 
     * @param columnData  the input column data
     * @return a columnData x 1 BigMatrix
     * @throws IllegalArgumentException if <code>columnData</code> is empty
     * @throws NullPointerException if <code>columnData</code>is null
     */
    public static BigMatrix createColumnBigMatrix(double[] columnData) {
        int nRows = columnData.length;
        double[][] data = new double[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = columnData[row];
        }
        return new BigMatrixImpl(data);
    }
    
    /**
     * Creates a column {@link BigMatrix} using the data from the input
     * array.
     * 
     * @param columnData  the input column data
     * @return a columnData x 1 BigMatrix
     * @throws IllegalArgumentException if <code>columnData</code> is empty
     * @throws NullPointerException if <code>columnData</code>is null
     */
    public static BigMatrix createColumnBigMatrix(BigDecimal[] columnData) {
        int nRows = columnData.length;
        BigDecimal[][] data = new BigDecimal[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = columnData[row];
        }
        return new BigMatrixImpl(data);
    }
    
    /**
     * Creates a column {@link BigMatrix} using the data from the input
     * array.
     * 
     * @param columnData  the input column data
     * @return a columnData x 1 BigMatrix
     * @throws IllegalArgumentException if <code>columnData</code> is empty
     * @throws NullPointerException if <code>columnData</code>is null
     */
    public static BigMatrix createColumnBigMatrix(String[] columnData) {
        int nRows = columnData.length;
        String[][] data = new String[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = columnData[row];
        }
        return new BigMatrixImpl(data);
    }
    
    /**
     * Returns <code>dimension x dimension</code> identity matrix.
     *
     * @param dimension dimension of identity matrix to generate
     * @return identity matrix
     * @throws IllegalArgumentException if dimension is not positive
     * @since 1.1
     */
    public static BigMatrix createBigIdentityMatrix(int dimension) {
        BigMatrixImpl out = new BigMatrixImpl(dimension, dimension);
        BigDecimal[][] d = out.getDataRef();
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                d[row][col] = row == col ? BigMatrixImpl.ONE : BigMatrixImpl.ZERO;
            }
        }
        return out;
    }
    
}

