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

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * A collection of static methods that operate on or return matrices.
 * 
 * @version $Revision$ $Date$
 */
public class MatrixUtils {

    /**
     * Default constructor.
     */
    public MatrixUtils() {
        super();
    }
    
    /**
     * Returns a {@link RealMatrix} with specified dimensions.
     * <p>The matrix elements are all set to 0.0.</p>
     * @param rows number of rows of the matrix
     * @param columns number of columns of the matrix
     * @return  RealMatrix with specified dimensions
     * @see #createRealMatrix(double[][])
     */
    public static RealMatrix createRealMatrix(final int rows, final int columns) {
        return new DenseRealMatrix(rows, columns);
    }

    /**
     * Returns a {@link RealMatrix} whose entries are the the values in the
     * the input array.  The input array is copied, not referenced.
     * 
     * @param data input array
     * @return  RealMatrix containing the values of the array
     * @throws IllegalArgumentException if <code>data</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>data</code> is null
     * @see #createRealMatrix(int, int)
     */
    public static RealMatrix createRealMatrix(double[][] data) {
        return new DenseRealMatrix(data);
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
        final RealMatrix m = createRealMatrix(dimension, dimension);
        for (int i = 0; i < dimension; ++i) {
            m.setEntry(i, i, 1.0);
        }
        return m;
    }
    
    /**
     * Returns a diagonal matrix with specified elements.
     *
     * @param diagonal diagonal elements of the matrix (the array elements
     * will be copied)
     * @return diagonal matrix
     * @since 2.0
     */
    public static RealMatrix createRealDiagonalMatrix(final double[] diagonal) {
        final RealMatrix m = createRealMatrix(diagonal.length, diagonal.length);
        for (int i = 0; i < diagonal.length; ++i) {
            m.setEntry(i, i, diagonal[i]);
        }
        return m;
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
     * the input array.
     * <p>If an array is built specially in order to be embedded in a
     * BigMatrix and not used directly, the <code>copyArray</code> may be
     * set to <code>false</code. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.</p>
     * @param data data for new matrix
     * @param copyArray if true, the input array will be copied, otherwise
     * it will be referenced
     * @return  BigMatrix containing the values of the array
     * @throws IllegalArgumentException if <code>data</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>data</code> is null
     * @see #createRealMatrix(double[][])
     */
    public static BigMatrix createBigMatrix(BigDecimal[][] data, boolean copyArray) {
        return new BigMatrixImpl(data, copyArray);
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
     * Creates a {@link RealVector} using the data from the input array. 
     * 
     * @param data the input data
     * @return a data.length RealVector
     * @throws IllegalArgumentException if <code>data</code> is empty
     * @throws NullPointerException if <code>data</code>is null
     */
    public static RealVector createRealVector(double[] data) {
        return new RealVectorImpl(data, true);
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
        final int nCols = rowData.length;
        final RealMatrix m = createRealMatrix(1, nCols);
        for (int i = 0; i < nCols; ++i) {
            m.setEntry(0, i, rowData[i]);
        }
        return m;
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
        final int nCols = rowData.length;
        final BigDecimal[][] data = new BigDecimal[1][nCols];
        for (int i = 0; i < nCols; ++i) {
            data[0][i] = new BigDecimal(rowData[i]);
        }
        return new BigMatrixImpl(data, false);
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
        final int nCols = rowData.length;
        final BigDecimal[][] data = new BigDecimal[1][nCols];
        System.arraycopy(rowData, 0, data[0], 0, nCols);
        return new BigMatrixImpl(data, false);
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
        final int nCols = rowData.length;
        final BigDecimal[][] data = new BigDecimal[1][nCols];
        for (int i = 0; i < nCols; ++i) {
            data[0][i] = new BigDecimal(rowData[i]);
        }
        return new BigMatrixImpl(data, false);
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
        final int nRows = columnData.length;
        final RealMatrix m = createRealMatrix(nRows, 1);
        for (int i = 0; i < nRows; ++i) {
            m.setEntry(i, 0, columnData[i]);
        }
        return m;
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
        final int nRows = columnData.length;
        final BigDecimal[][] data = new BigDecimal[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = new BigDecimal(columnData[row]);
        }
        return new BigMatrixImpl(data, false);
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
        final int nRows = columnData.length;
        final BigDecimal[][] data = new BigDecimal[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = columnData[row];
        }
        return new BigMatrixImpl(data, false);
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
        final BigDecimal[][] data = new BigDecimal[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = new BigDecimal(columnData[row]);
        }
        return new BigMatrixImpl(data, false);
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
        final BigDecimal[][] d = new BigDecimal[dimension][dimension];
        for (int row = 0; row < dimension; row++) {
            final BigDecimal[] dRow = d[row];
            Arrays.fill(dRow, BigMatrixImpl.ZERO);
            dRow[row] = BigMatrixImpl.ONE;
        }
        return new BigMatrixImpl(d, false);
    }
    
}

