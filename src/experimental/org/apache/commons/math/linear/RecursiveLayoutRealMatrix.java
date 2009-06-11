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

/**
 * Cache-friendly implementation of RealMatrix using recursive array layouts to store
 * the matrix elements.
 * <p>
 * As of 2009-02-13, this implementation does not work! The padding at left and bottom
 * sides of the matrix should be cleared after some operations like scalerAdd
 * and is not. Also there is a limitation in the multiplication that can only
 * process matrices with sizes similar enough to have the same power of two
 * number of tiles in all three matrices A, B and C such that C = A*B. These
 * parts have not been fixed since the performance gain with respect to
 * BlockRealMatrix are not very important, and the numerical stability is not
 * good. This may well be due to a bad implementation. This code has been put
 * in the experimental part for the record, putting it into production would
 * require solving all these issues.
 * </p>
 * <p>
 * This implementation is based on the 2002 paper: <a
 * href="http://www.cs.duke.edu/~alvy/papers/matrix-tpds.pdf">Recursive Array Layouts
 * and Fast Matrix Multiplication</a> by Siddhartha Chatterjee, Alvin R. Lebeck,
 * Praveen K. Patnala and Mithuna Thottethodi.
 * </p>
 * <p>
 * The matrix is split into several rectangular tiles. The tiles are laid out using
 * a space-filling curve in a 2<sup>k</sup>&times;2<sup>k</sup> square. This
 * implementation uses the Gray-Morton layout which starts as follows for a three-level
 * recursion (i.e. an 8x8 matrix). The tiles size are adjusted in order to have the
 * 2<sup>k</sup>&times;2<sup>k</sup> square. This may require padding at the right and
 * bottom sides of the matrix (see above paper for a discussion of this padding feature).
 * </p>
 * <pre>
 *                    |
 *    00 01 | 06 07   |   24  25 | 30  31
 *    03 02 | 05 04   |   27  26 | 29  28
 *    ------+------   |   -------+-------
 *    12 13 | 10 11   |   20  21 | 18  19
 *    15 14 | 09 08   |   23  22 | 17  16
 *                    |
 * -------------------+--------------------
 *                    |
 *    48 49 | 54 55   |   40  41 | 46  47
 *    51 50 | 53 52   |   43  42 | 45  44
 *    ------+------   |   -------+-------
 *    60 61 | 58 59   |   36  37 | 34  35
 *    63 62 | 57 56   |   39  38 | 33  32
 *                    |
 * </pre>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class RecursiveLayoutRealMatrix extends AbstractRealMatrix implements Serializable {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = 1607919006739190004L;

    /** Maximal allowed tile size in bytes.
     * <p>In order to avoid cache miss during multiplication,
     * a suggested value is cache_size/3.</p>
     */
    private static final int MAX_TILE_SIZE_BYTES = (64 * 1024) / 3;
    //private static final int MAX_TILE_SIZE_BYTES = 32;

    /** Storage array for matrix elements. */
    private final double data[];

    /** Number of rows of the matrix. */
    private final int rows;

    /** Number of columns of the matrix. */
    private final int columns;

    /** Number of terminal tiles along rows and columns (guaranteed to be a power of 2). */
    private final int tileNumber;

    /** Number of rows in each terminal tile. */
    private final int tileSizeRows;

    /** Number of columns in each terminal tile. */
    private final int tileSizeColumns;

    /**
     * Create a new matrix with the supplied row and column dimensions.
     *
     * @param rows  the number of rows in the new matrix
     * @param columns  the number of columns in the new matrix
     * @throws IllegalArgumentException if row or column dimension is not
     *  positive
     */
    public RecursiveLayoutRealMatrix(final int rows, final int columns)
        throws IllegalArgumentException {

        super(rows, columns);
        this.rows    = rows;
        this.columns = columns;

        // compute optimal layout
        tileNumber      = tilesNumber(rows, columns);
        tileSizeRows    = tileSize(rows, tileNumber);
        tileSizeColumns = tileSize(columns, tileNumber);

        // create storage array
        data = new double[tileNumber * tileNumber * tileSizeRows * tileSizeColumns];

    }

    /**
     * Create a new dense matrix copying entries from raw layout data.
     * <p>The input array <em>must</em> be in raw layout.</p>
     * <p>Calling this constructor is equivalent to call:
     * <pre>matrix = new RecursiveLayoutRealMatrix(rawData.length, rawData[0].length,
     *                                             toRecursiveLayout(rawData), false);</pre>
     * </p>
     * @param rawData data for new matrix, in raw layout
     *
     * @exception IllegalArgumentException if <code>rawData</code> shape is
     * inconsistent with tile layout
     * @see #RecursiveLayoutRealMatrix(int, int, double[][], boolean)
     */
    public RecursiveLayoutRealMatrix(final double[][] rawData)
        throws IllegalArgumentException {
        this(rawData.length, rawData[0].length, toRecursiveLayout(rawData), false);
    }

    /**
     * Create a new dense matrix copying entries from recursive layout data.
     * <p>The input array <em>must</em> already be in recursive layout.</p>
     * @param rows  the number of rows in the new matrix
     * @param columns  the number of columns in the new matrix
     * @param data data for new matrix, in recursive layout
     * @param copyArray if true, the input array will be copied, otherwise
     * it will be referenced
     *
     * @exception IllegalArgumentException if <code>data</code> size is
     * inconsistent with matrix size
     * @see #toRecursiveLayout(double[][])
     * @see #RecursiveLayoutRealMatrix(double[][])
     */
    public RecursiveLayoutRealMatrix(final int rows, final int columns,
                                     final double[] data, final boolean copyArray)
        throws IllegalArgumentException {

        super(rows, columns);
        this.rows    = rows;
        this.columns = columns;

        // compute optimal layout
        tileNumber      = tilesNumber(rows, columns);
        tileSizeRows    = tileSize(rows, tileNumber);
        tileSizeColumns = tileSize(columns, tileNumber);

        // create storage array
        final int expectedLength = tileNumber * tileNumber * tileSizeRows * tileSizeColumns;
        if (data.length != expectedLength) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "wrong array size (got {0}, expected {1})",
                    data.length, expectedLength);
        }

        if (copyArray) {
            // allocate storage array
            this.data = data.clone();
        } else {
            // reference existing array
            this.data = data;
        }

    }

    /**
     * Convert a data array from raw layout to recursive layout.
     * <p>
     * Raw layout is the straightforward layout where element at row i and
     * column j is in array element <code>rawData[i][j]</code>. Recursive layout
     * is the layout used in {@link RecursiveLayoutRealMatrix} instances, where the matrix
     * is stored in a dimension 1 array using a space-filling curve to spread the matrix
     * elements along the array.
     * </p>
     * @param rawData data array in raw layout
     * @return a new data array containing the same entries but in recursive layout
     * @exception IllegalArgumentException if <code>rawData</code> is not rectangular
     *  (not all rows have the same length)
     * @see #RecursiveLayoutRealMatrix(int, int, double[], boolean)
     */
    public static double[] toRecursiveLayout(final double[][] rawData)
        throws IllegalArgumentException {

        final int rows    = rawData.length;
        final int columns = rawData[0].length;

        // compute optimal layout
        final int tileNumber      = tilesNumber(rows, columns);
        final int tileSizeRows    = tileSize(rows, tileNumber);
        final int tileSizeColumns = tileSize(columns, tileNumber);

        // safety checks
        for (int i = 0; i < rawData.length; ++i) {
            final int length = rawData[i].length;
            if (length != columns) {
                throw MathRuntimeException.createIllegalArgumentException(
                        "some rows have length {0} while others have length {1}",
                        columns, length); 
            }
        }

        // convert array row after row
        final double[] data = new double[tileNumber * tileNumber * tileSizeRows * tileSizeColumns];
        for (int i = 0; i < rawData.length; ++i) {
            final int iTile = i / tileSizeRows;
            final double[] rawDataI = rawData[i];
            for (int jTile = 0; jTile < tileNumber; ++jTile) {
                final int tileStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns;
                final int dataStart = tileStart + (i - iTile * tileSizeRows) * tileSizeColumns;
                final int jStart    = jTile * tileSizeColumns;
                if (jStart < columns) {
                    final int jEnd      = Math.min(jStart + tileSizeColumns, columns);
                    System.arraycopy(rawDataI, jStart, data, dataStart, jEnd - jStart);
                }
            }
        }

        return data;

    }

    /** {@inheritDoc} */
    public RealMatrix createMatrix(final int rowDimension, final int columnDimension)
        throws IllegalArgumentException {
        return new RecursiveLayoutRealMatrix(rowDimension, columnDimension);
    }

    /** {@inheritDoc} */
    public RealMatrix copy() {
        return new RecursiveLayoutRealMatrix(rows, columns, data, true);
    }

    /** {@inheritDoc} */
    public RealMatrix add(final RealMatrix m)
        throws IllegalArgumentException {
        try {
            return add((RecursiveLayoutRealMatrix) m);
        } catch (ClassCastException cce) {

            // safety check
            checkAdditionCompatible(m);

            final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(rows, columns);

            // perform addition tile-wise, to ensure good cache behavior
            for (int index = 0; index < tileNumber * tileNumber; ++index) {

                // perform addition on the current tile
                final int tileStart = index * tileSizeRows * tileSizeColumns;
                final long indices  = tilesIndices(index);
                final int iTile     = (int) (indices >> 32);
                final int jTile     = (int) (indices & 0xffffffff);
                final int pStart    = iTile * tileSizeRows;
                final int pEnd      = Math.min(pStart + tileSizeRows, rows);
                final int qStart    = jTile * tileSizeColumns;
                final int qEnd      = Math.min(qStart + tileSizeColumns, columns);
                for (int p = pStart; p < pEnd; ++p) {
                    final int kStart = tileStart + (p - pStart) * tileSizeColumns;
                    for (int q = qStart, k = kStart; q < qEnd; ++q, ++k) {
                        out.data[k] = data[k] + m.getEntry(p, q);
                    }
                }

            }

            return out;

        }
    }

    /**
     * Compute the sum of this and <code>m</code>.
     *
     * @param m    matrix to be added
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public RecursiveLayoutRealMatrix add(final RecursiveLayoutRealMatrix m)
        throws IllegalArgumentException {

        // safety check
        checkAdditionCompatible(m);

        final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(rows, columns);

        // streamlined addition
        for (int i = 0; i < data.length; ++i) {
            out.data[i] = data[i] + m.data[i];
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix subtract(final RealMatrix m)
        throws IllegalArgumentException {
        try {
            return subtract((RecursiveLayoutRealMatrix) m);
        } catch (ClassCastException cce) {

            // safety check
            checkSubtractionCompatible(m);

            final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(rows, columns);

            // perform subtraction tile-wise, to ensure good cache behavior
            for (int index = 0; index < tileNumber * tileNumber; ++index) {

                // perform addition on the current tile
                final int tileStart = index * tileSizeRows * tileSizeColumns;
                final long indices  = tilesIndices(index);
                final int iTile     = (int) (indices >> 32);
                final int jTile     = (int) (indices & 0xffffffff);
                final int pStart    = iTile * tileSizeRows;
                final int pEnd      = Math.min(pStart + tileSizeRows, rows);
                final int qStart    = jTile * tileSizeColumns;
                final int qEnd      = Math.min(qStart + tileSizeColumns, columns);
                for (int p = pStart; p < pEnd; ++p) {
                    final int kStart = tileStart + (p - pStart) * tileSizeColumns;
                    for (int q = qStart, k = kStart; q < qEnd; ++q, ++k) {
                        out.data[k] = data[k] - m.getEntry(p, q);
                    }
                }

            }

            return out;

        }
    }

    /**
     * Compute this minus <code>m</code>.
     *
     * @param m    matrix to be subtracted
     * @return     this - m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public RecursiveLayoutRealMatrix subtract(final RecursiveLayoutRealMatrix m)
        throws IllegalArgumentException {

        // safety check
        checkSubtractionCompatible(m);

        final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(rows, columns);

        // streamlined subtraction
        for (int i = 0; i < data.length; ++i) {
            out.data[i] = data[i] - m.data[i];
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix scalarAdd(final double d)
        throws IllegalArgumentException {

        final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(rows, columns);

        // streamlined addition
        for (int i = 0; i < data.length; ++i) {
            out.data[i] = data[i] + d;
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix scalarMultiply(final double d)
        throws IllegalArgumentException {

        final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(rows, columns);

        // streamlined multiplication
        for (int i = 0; i < data.length; ++i) {
            out.data[i] = data[i] * d;
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix multiply(final RealMatrix m)
        throws IllegalArgumentException {
        try {
            return multiply((RecursiveLayoutRealMatrix) m);
        } catch (ClassCastException cce) {

            // safety check
            checkMultiplicationCompatible(m);

            final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(rows, m.getColumnDimension());

            // perform multiplication tile-wise, to ensure good cache behavior
            for (int index = 0; index < out.tileNumber * out.tileNumber; ++index) {
                final int tileStart = index * out.tileSizeRows * out.tileSizeColumns;
                final long indices  = tilesIndices(index);
                final int iTile     = (int) (indices >> 32);
                final int jTile     = (int) (indices & 0xffffffff);
                final int iStart    = iTile * out.tileSizeRows;
                final int iEnd      = Math.min(iStart + out.tileSizeRows, out.rows);
                final int jStart    = jTile * out.tileSizeColumns;
                final int jEnd      = Math.min(jStart + out.tileSizeColumns, out.columns);

                // perform multiplication for current tile
                for (int kTile = 0; kTile < tileNumber; ++kTile) {
                    final int kTileStart = tileIndex(iTile, kTile) * tileSizeRows * tileSizeColumns;
                    for (int i = iStart, lStart = kTileStart, oStart = tileStart;
                         i < iEnd;
                         ++i, lStart += tileSizeColumns, oStart += out.tileSizeColumns) {
                        final int lEnd = Math.min(lStart + tileSizeColumns, columns);
                        for (int j = jStart, o = oStart; j < jEnd; ++j, ++o) {
                            double sum = 0;
                            for (int l = lStart, k = kTile * tileSizeColumns; l < lEnd; ++l, ++k) {
                                sum += data[l] * m.getEntry(k, j);
                            }
                            out.data[o] += sum;
                        }
                    }
                }
            }

            return out;

        }
    }

    /**
     * Returns the result of postmultiplying this by m.
     * <p>The Strassen matrix multiplication method is used here. This
     * method computes C = A &times; B recursively by splitting all matrices
     * in four quadrants and computing:</p>
     * <pre>
     * P<sub>1</sub> = (A<sub>1,1</sub> + A<sub>2,2</sub>) &times; (B<sub>1,1</sub> + B<sub>2,2</sub>)
     * P<sub>2</sub> = (A<sub>2,1</sub> + A<sub>2,2</sub>) &times; (B<sub>1,1</sub>)
     * P<sub>3</sub> = (A<sub>1,1</sub>) &times; (B<sub>1,2</sub> - B<sub>2,2</sub>)
     * P<sub>4</sub> = (A<sub>2,2</sub>) &times; (B<sub>2,1</sub> - B<sub>1,1</sub>)
     * P<sub>5</sub> = (A<sub>1,1</sub> + A<sub>1,2</sub>) &times; B<sub>2,2</sub>
     * P<sub>6</sub> = (A<sub>2,1</sub> - A<sub>1,1</sub>) &times; (B<sub>1,1</sub> + B<sub>1,2</sub>)
     * P<sub>7</sub> = (A<sub>1,2</sub> - A<sub>2,2</sub>) &times; (B<sub>2,1</sub> + B<sub>2,2</sub>)
     *
     * C<sub>1,1</sub> = P<sub>1</sub> + P<sub>4</sub> - P<sub>5</sub> + P<sub>7</sub>
     * C<sub>1,2</sub> = P<sub>3</sub> + P<sub>5</sub>
     * C<sub>2,1</sub> = P<sub>2</sub> + P<sub>4</sub>
     * C<sub>2,2</sub> = P<sub>1</sub> + P<sub>3</sub> - P<sub>2</sub> + P<sub>6</sub>
     * </pre>
     * <p>
     * This implementation is based on the 2002 paper: <a
     * href="http://www.cs.duke.edu/~alvy/papers/matrix-tpds.pdf">Recursive Array Layouts
     * and Fast Matrix Multiplication</a> by Siddhartha Chatterjee, Alvin R. Lebeck,
     * Praveen K. Patnala and Mithuna Thottethodi.
     * </p>
     *
     * @param m    matrix to postmultiply by
     * @return     this * m
     * @throws     IllegalArgumentException
     *             if columnDimension(this) != rowDimension(m)
     */
    public RecursiveLayoutRealMatrix multiply(RecursiveLayoutRealMatrix m)
        throws IllegalArgumentException {

        // safety check
        checkMultiplicationCompatible(m);

        final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(rows, m.columns);
        if ((tileNumber != m.tileNumber) || (tileNumber != out.tileNumber)) {
            // TODO get rid of this test
            throw new RuntimeException("multiplication " + rows + "x" + columns + " * " +
                                       m.rows + "x" + m.columns + " -> left matrix: " + tileNumber +
                                       " tiles, right matrix: " + m.tileNumber + " tiles, result matrix " +
                                       out.tileNumber + " tiles");
        }
        strassenMultiply(data, 0, true, m.data, 0, true, out.data, 0, tileNumber,
                         tileSizeRows, m.tileSizeColumns, tileSizeColumns);
        
        return out;

    }

    /**
     * Perform recursive multiplication using Strassen's algorithm.
     * @param a left term of multiplication
     * @param aStart start index in a
     * @param aDirect direct/reversed orientation flag for a
     * @param b right term of multiplication
     * @param bStart start index in b
     * @param bDirect direct/reversed orientation flag for b
     * @param result result array (will have same orientation as b)
     * @param resultStart start index in result
     * @param nTiles number of elements to add
     * @param bsRows number of rows in result tiles
     * @param bsColumns number of columns in result tiles
     * @param bsMultiplicands number of rows/columns in multiplicands
     */
    private static void strassenMultiply(final double[] a, final int aStart, final boolean aDirect,
                                         final double[] b, final int bStart, final boolean bDirect,
                                         final double[] result, final int resultStart, final int nTiles,
                                         final int bsRows, final int bsColumns, final int bsMultiplicands) {
        if (nTiles == 1) {
            // leaf recursion tile: perform traditional multiplication
            final int bsColumns2 = 2 * bsColumns;
            final int bsColumns3 = 3 * bsColumns;
            final int bsColumns4 = 4 * bsColumns;
            for (int i = 0; i < bsRows; ++i) {
                for (int j = 0; j < bsColumns; ++j) {
                    double sum = 0;
                    int k  = 0;
                    int aK = aStart + i * bsMultiplicands;
                    int bK = bStart + j;
                    while (k < bsMultiplicands - 3) {
                        sum += a[aK]     * b[bK] +
                               a[aK + 1] * b[bK + bsColumns] +
                               a[aK + 2] * b[bK + bsColumns2] +
                               a[aK + 3] * b[bK + bsColumns3];
                        k  += 4;
                        aK += 4;
                        bK += bsColumns4;
                    }
                    while (k < bsMultiplicands) {
                        sum += a[aK] * b[bK];
                        k  += 1;
                        aK += 1;
                        bK += bsColumns;
                    }
                    result[resultStart + i * bsColumns + j] = sum;
                }
            }
        } else {
            // regular recursion node: use recursive Strassen implementation
            final int n2            = nTiles / 2;
            final int aQuadrantSize = bsRows          * n2 * bsMultiplicands * n2;
            final int bQuadrantSize = bsMultiplicands * n2 * bsColumns       * n2;
            final int cQuadrantSize = bsRows          * n2 * bsColumns       * n2;
            final double[] sA = new double[aQuadrantSize];
            final double[] sB = new double[bQuadrantSize];
            final boolean nonLeafQuadrants = n2 > 1;

            // identify A quadrants start indices
            final int a11Start, a12Start, a21Start, a22Start;
            if (aDirect) {
                a11Start = aStart;
                a12Start = aStart +     aQuadrantSize;
                a21Start = aStart + 3 * aQuadrantSize;
                a22Start = aStart + 2 * aQuadrantSize;
            } else {
                a11Start = aStart + 2 * aQuadrantSize;
                a12Start = aStart + 3 * aQuadrantSize;
                a21Start = aStart +     aQuadrantSize;
                a22Start = aStart;
            }

            // identify B and C quadrants start indices
            // (C is constructed with the same orientation as B)
            final int b11Start, b12Start, b21Start, b22Start;
            final int c11Start, c12Start, c21Start, c22Start;
            if (bDirect) {
                b11Start = bStart;
                b12Start = bStart +     bQuadrantSize;
                b21Start = bStart + 3 * bQuadrantSize;
                b22Start = bStart + 2 * bQuadrantSize;
                c11Start = resultStart;
                c12Start = resultStart +     cQuadrantSize;
                c21Start = resultStart + 3 * cQuadrantSize;
                c22Start = resultStart + 2 * cQuadrantSize;
            } else {
                b11Start = bStart + 2 * bQuadrantSize;
                b12Start = bStart + 3 * bQuadrantSize;
                b21Start = bStart +     bQuadrantSize;
                b22Start = bStart;
                c11Start = resultStart + 2 * cQuadrantSize;
                c12Start = resultStart + 3 * cQuadrantSize;
                c21Start = resultStart +     cQuadrantSize;
                c22Start = resultStart;
            }

            // optimal order for cache efficiency: P3, P6, P2, P1, P5, P7, P4

            // P3  = (A11)(B12 - B22)
            // C12 = P3 + ...
            tilesSubtract(b, b12Start, false, b, b22Start, false, sB, 0,
                          bQuadrantSize, nonLeafQuadrants);
            strassenMultiply(a, a11Start, true, sB, 0, false, result, c12Start,
                             n2, bsRows, bsColumns, bsMultiplicands);

            // P6  = (A21 - A11)(B11 + B12)
            // C22 = P3 + P6 + ...
            final double[] p67 = new double[cQuadrantSize];
            tilesSubtract(a, a21Start, true, a, a11Start, true, sA, 0,
                          aQuadrantSize, nonLeafQuadrants);
            tilesAdd(b, b11Start, true, b, b12Start, false, sB, 0,
                     bQuadrantSize, nonLeafQuadrants);
            strassenMultiply(sA, 0, true, sB, 0, true, p67, 0,
                             n2, bsRows, bsColumns, bsMultiplicands);
            tilesAdd(result, c12Start, false, p67, 0, true, result, c22Start,
                     cQuadrantSize, nonLeafQuadrants);

            // P2  = (A21 + A22)(B11)
            // C21 = P2 + ...
            // C22 = P3 + P6 - P2 + ...
            tilesAdd(a, a21Start, true, a, a22Start, false, sA, 0,
                     aQuadrantSize, nonLeafQuadrants);
            strassenMultiply(sA, 0, true, b, b11Start, true, result, c21Start,
                             n2, bsRows, bsColumns, bsMultiplicands);
            tilesSelfSubtract(result, c22Start, false, result, c21Start, true,
                              cQuadrantSize, nonLeafQuadrants);

            // P1  = (A11 + A22)(B11 + B22)
            // C11 = P1 + ...
            // C22 = P3 + P6 - P2 + P1
            tilesAdd(a, a11Start, true, a, a22Start, false, sA, 0,
                     aQuadrantSize, nonLeafQuadrants);
            tilesAdd(b, b11Start, true, b, b22Start, false, sB, 0,
                     bQuadrantSize, nonLeafQuadrants);
            strassenMultiply(sA, 0, true, sB, 0, true, result, c11Start,
                             n2, bsRows, bsColumns, bsMultiplicands);
            tilesSelfAdd(result, c22Start, false, result, c11Start, true,
                         cQuadrantSize, nonLeafQuadrants);

            // P5  = (A11 + A12)B22
            // beware: there is a sign error here in Chatterjee et al. paper
            // in figure 1, table b they subtract A12 from A11 instead of adding it
            // C12 = P3 + P5
            // C11 = P1 - P5 + ...
            final double[] p45 = new double[cQuadrantSize];
            tilesAdd(a, a11Start, true, a, a12Start, false, sA, 0,
                     aQuadrantSize, nonLeafQuadrants);
            strassenMultiply(sA, 0, true, b, b22Start, false, p45, 0,
                             n2, bsRows, bsColumns, bsMultiplicands);
            tilesSelfAdd(result, c12Start, false, p45, 0, false,
                         cQuadrantSize, nonLeafQuadrants);
            tilesSelfSubtract(result, c11Start, true, p45, 0, false,
                              cQuadrantSize, nonLeafQuadrants);

            // P7  = (A12 - A22)(B21 + B22)
            // C11 = P1 - P5 + P7 + ...
            tilesSubtract(a, a12Start, false, a, a22Start, false, sA, 0,
                          aQuadrantSize, nonLeafQuadrants);
            tilesAdd(b, b21Start, true, b, b22Start, false, sB, 0,
                     bQuadrantSize, nonLeafQuadrants);
            strassenMultiply(sA, 0, false, sB, 0, true, p67, 0,
                             n2, bsRows, bsColumns, bsMultiplicands);
            tilesSelfAdd(result, c11Start, true, p67, 0, true,
                         cQuadrantSize, nonLeafQuadrants);

            // P4  = (A22)(B21 - B11)
            // C11 = P1 - P5 + P7 + P4
            // C21 = P2 + P4
            tilesSubtract(b, b21Start, true, b, b11Start, true, sB, 0,
                          bQuadrantSize, nonLeafQuadrants);
            strassenMultiply(a, a22Start, false, sB, 0, true, p45, 0,
                             n2, bsRows, bsColumns, bsMultiplicands);
            tilesSelfAdd(result, c11Start, true, p45, 0, true,
                         cQuadrantSize, nonLeafQuadrants);
            tilesSelfAdd(result, c21Start, true, p45, 0, true,
                         cQuadrantSize, nonLeafQuadrants);

        }
    }

    /**
     * Perform an addition on a few tiles in arrays.
     * @param a left term of addition
     * @param aStart start index in a
     * @param aDirect direct/reversed orientation flag for a
     * @param b right term of addition
     * @param bStart start index in b
     * @param bDirect direct/reversed orientation flag for b
     * @param result result array (will have same orientation as a)
     * @param resultStart start index in result
     * @param n number of elements to add
     * @param nonLeafQuadrants if true the quadrant can be further decomposed
     */
    private static void tilesAdd(final double[] a, final int aStart, final boolean aDirect,
                                 final double[] b, final int bStart, final boolean bDirect,
                                 final double[] result, final int resultStart,
                                 final int n, final boolean nonLeafQuadrants) {
        if ((aDirect ^ bDirect) & nonLeafQuadrants) {
            // a and b have different orientations
            // perform addition in two half
            final int n2 = n / 2;
            addLoop(a, aStart,      b, bStart + n2, result, resultStart,      n2);
            addLoop(a, aStart + n2, b, bStart,      result, resultStart + n2, n2);
        } else {
            // a and b have same orientations
            // perform addition in one loop
            addLoop(a, aStart, b, bStart, result, resultStart, n);
        }
    }

    /**
     * Perform an addition loop.
     * @param a left term of addition
     * @param aStart start index in a
     * @param b right term of addition
     * @param bStart start index in b
     * @param result result array (will have same orientation as a)
     * @param resultStart start index in result
     * @param n number of elements to add
     */
    private static void addLoop(final double[] a, final int aStart,
                                final double[] b, final int bStart,
                                final double[] result, final int resultStart,
                                final int n) {
        int i = 0;
        while (i < n - 3) {
            final int r0 = resultStart + i;
            final int a0 = aStart      + i;
            final int b0 = bStart      + i;
            result[r0]     = a[a0]     + b[b0];
            result[r0 + 1] = a[a0 + 1] + b[b0 + 1];
            result[r0 + 2] = a[a0 + 2] + b[b0 + 2];
            result[r0 + 3] = a[a0 + 3] + b[b0 + 3];
            i += 4;
        }
        while (i < n) {
            result[resultStart + i] = a[aStart + i] + b[bStart + i];
            ++i;
        }
    }

    /**
     * Perform a subtraction on a few tiles in arrays.
     * @param a left term of subtraction
     * @param aStart start index in a
     * @param aDirect direct/reversed orientation flag for a
     * @param b right term of subtraction
     * @param bStart start index in b
     * @param bDirect direct/reversed orientation flag for b
     * @param result result array (will have same orientation as a)
     * @param resultStart start index in result
     * @param n number of elements to subtract
     * @param nonLeafQuadrants if true the quadrant can be further decomposed
     */
    private static void tilesSubtract(final double[] a, final int aStart, final boolean aDirect,
                                      final double[] b, final int bStart, final boolean bDirect,
                                      final double[] result, final int resultStart,
                                      final int n, final boolean nonLeafQuadrants) {
        if ((aDirect ^ bDirect) & nonLeafQuadrants) {
            // a and b have different orientations
            // perform subtraction in two half
            final int n2 = n / 2;
            subtractLoop(a, aStart,      b, bStart + n2, result, resultStart,      n2);
            subtractLoop(a, aStart + n2, b, bStart,      result, resultStart + n2, n2);
        } else {
            // a and b have same orientations
            // perform subtraction in one loop
            subtractLoop(a, aStart, b, bStart, result, resultStart, n);
        }
    }

    /**
     * Perform a subtraction loop.
     * @param a left term of subtraction
     * @param aStart start index in a
     * @param b right term of subtraction
     * @param bStart start index in b
     * @param result result array (will have same orientation as a)
     * @param resultStart start index in result
     * @param n number of elements to subtract
     */
    private static void subtractLoop(final double[] a, final int aStart,
                                     final double[] b, final int bStart,
                                     final double[] result, final int resultStart,
                                     final int n) {
        int i = 0;
        while (i < n - 3) {
            final int r0 = resultStart + i;
            final int a0 = aStart      + i;
            final int b0 = bStart      + i;
            result[r0]     = a[a0]     - b[b0];
            result[r0 + 1] = a[a0 + 1] - b[b0 + 1];
            result[r0 + 2] = a[a0 + 2] - b[b0 + 2];
            result[r0 + 3] = a[a0 + 3] - b[b0 + 3];
            i += 4;
        }
        while (i < n) {
            result[resultStart + i] = a[aStart + i] - b[bStart + i];
            ++i;
        }
    }

    /**
     * Perform a self-addition on a few tiles in arrays.
     * @param a left term of addition (will be overwritten with result)
     * @param aStart start index in a
     * @param aDirect direct/reversed orientation flag for a
     * @param b right term of addition
     * @param bStart start index in b
     * @param bDirect direct/reversed orientation flag for b
     * @param n number of elements to add
     * @param nonLeafQuadrants if true the quadrant can be further decomposed
     */
    private static void tilesSelfAdd(final double[] a, final int aStart, final boolean aDirect,
                                     final double[] b, final int bStart, final boolean bDirect,
                                     final int n, final boolean nonLeafQuadrants) {
        if ((aDirect ^ bDirect) & nonLeafQuadrants) {
            // a and b have different orientations
            // perform addition in two half
            final int n2 = n / 2;
            selfAddLoop(a, aStart,      b, bStart + n2, n2);
            selfAddLoop(a, aStart + n2, b, bStart,      n2);
        } else {
            // a and b have same orientations
            // perform addition in one loop
            selfAddLoop(a, aStart, b, bStart, n);
        }
    }

    /**
     * Perform a self-addition loop.
     * @param a left term of addition (will be overwritten with result)
     * @param aStart start index in a
     * @param b right term of addition
     * @param bStart start index in b
     * @param n number of elements to add
     */
    private static void selfAddLoop(final double[] a, final int aStart,
                                    final double[] b, final int bStart,
                                    final int n) {
        int i = 0;
        while (i < n - 3) {
            final int a0 = aStart + i;
            final int b0 = bStart + i;
            a[a0]     += b[b0];
            a[a0 + 1] += b[b0 + 1];
            a[a0 + 2] += b[b0 + 2];
            a[a0 + 3] += b[b0 + 3];
            i += 4;
        }
        while (i < n) {
            a[aStart + i] += b[bStart + i];
            ++i;
        }
    }

    /**
     * Perform a self-subtraction on a few tiles in arrays.
     * @param a left term of subtraction (will be overwritten with result)
     * @param aStart start index in a
     * @param aDirect direct/reversed orientation flag for a
     * @param b right term of subtraction
     * @param bStart start index in b
     * @param bDirect direct/reversed orientation flag for b
     * @param n number of elements to subtract
     * @param nonLeafQuadrants if true the quadrant can be further decomposed
     */
    private static void tilesSelfSubtract(final double[] a, final int aStart, final boolean aDirect,
                                          final double[] b, final int bStart, final boolean bDirect,
                                          final int n, final boolean nonLeafQuadrants) {
        if ((aDirect ^ bDirect) & nonLeafQuadrants) {
            // a and b have different orientations
            // perform subtraction in two half
            final int n2 = n / 2;
            selfSubtractLoop(a, aStart,      b, bStart + n2, n2);
            selfSubtractLoop(a, aStart + n2, b, bStart,      n2);
        } else {
            // a and b have same orientations
            // perform subtraction in one loop
            selfSubtractLoop(a, aStart, b, bStart, n);
        }
    }

    /**
     * Perform a self-subtraction loop.
     * @param a left term of subtraction (will be overwritten with result)
     * @param aStart start index in a
     * @param b right term of subtraction
     * @param bStart start index in b
     * @param n number of elements to subtract
     */
    private static void selfSubtractLoop(final double[] a, final int aStart,
                                         final double[] b, final int bStart,
                                         final int n) {
        int i = 0;
        while (i < n - 3) {
            final int a0 = aStart + i;
            final int b0 = bStart + i;
            a[a0]     -= b[b0];
            a[a0 + 1] -= b[b0 + 1];
            a[a0 + 2] -= b[b0 + 2];
            a[a0 + 3] -= b[b0 + 3];
            i += 4;
        }
        while (i < n) {
            a[aStart + i] -= b[bStart + i];
            ++i;
        }
    }

    /** {@inheritDoc} */
    public double[][] getData() {

        final double[][] out = new double[rows][columns];

        // perform extraction tile-wise, to ensure good cache behavior
        for (int index = 0; index < tileNumber * tileNumber; ++index) {

            // perform extraction on the current tile
            final int tileStart = index * tileSizeRows * tileSizeColumns;
            final long indices  = tilesIndices(index);
            final int iTile     = (int) (indices >> 32);
            final int jTile     = (int) (indices & 0xffffffff);
            final int pStart    = iTile * tileSizeRows;
            final int qStart    = jTile * tileSizeColumns;
            if (pStart < rows && qStart < columns) {
                final int pEnd = Math.min(pStart + tileSizeRows, rows);
                final int qEnd = Math.min(qStart + tileSizeColumns, columns);
                int tileRowStart = tileStart;
                for (int p = pStart; p < pEnd; ++p) {
                    System.arraycopy(data, tileRowStart, out[p], qStart, qEnd - qStart);
                    tileRowStart += tileSizeColumns;
                }
            }

        }

        return out;
        
    }

    /** {@inheritDoc} */
    public double getFrobeniusNorm() {
        double sum2 = 0;
        for (final double entry : data) {
            sum2 += entry * entry;
        }
        return Math.sqrt(sum2);
    }

    /** {@inheritDoc} */
    public RealMatrix getSubMatrix(final int startRow, final int endRow,
                                   final int startColumn, final int endColumn)
        throws MatrixIndexException {

        // safety checks
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);

        // create the output matrix
        final RecursiveLayoutRealMatrix out =
            new RecursiveLayoutRealMatrix(endRow - startRow + 1, endColumn - startColumn + 1);

        // perform extraction tile-wise, to ensure good cache behavior
        for (int iTile = 0; iTile < out.tileNumber; ++iTile) {
            final int iStart = startRow + iTile * out.tileSizeRows;
            final int iEnd   = Math.min(startRow + Math.min((iTile + 1) * out.tileSizeRows, out.rows),
                                        endRow + 1);
            for (int jTile = 0; jTile < out.tileNumber; ++jTile) {
                final int jStart = startColumn + jTile * out.tileSizeColumns;
                final int jEnd   = Math.min(startColumn + Math.min((jTile + 1) * out.tileSizeColumns, out.columns),
                                            endColumn + 1);

                // the current output tile may expand on more than one instance tile
                for (int pTile = iStart / tileSizeRows; pTile * tileSizeRows < iEnd; ++pTile) {
                    final int p0     = pTile * tileSizeRows;
                    final int pStart = Math.max(p0, iStart);
                    final int pEnd   = Math.min(Math.min(p0 + tileSizeRows, endRow + 1), iEnd);
                    for (int qTile = jStart / tileSizeColumns; qTile * tileSizeColumns < jEnd; ++qTile) {
                        final int q0     = qTile * tileSizeColumns;
                        final int qStart = Math.max(q0, jStart);
                        final int qEnd   = Math.min(Math.min(q0 + tileSizeColumns, endColumn + 1), jEnd);

                        // copy the overlapping part of instance and output tiles
                        int outIndex = tileIndex(iTile, jTile) * out.tileSizeRows * out.tileSizeColumns +
                                       (pStart - iStart) * out.tileSizeColumns + (qStart - jStart);
                        int index    = tileIndex(pTile, qTile) * tileSizeRows * tileSizeColumns +
                                       (pStart - p0) * tileSizeColumns + (qStart - q0);
                        for (int p = pStart; p < pEnd; ++p) {
                            System.arraycopy(data, index, out.data, outIndex, qEnd - qStart);
                            outIndex += out.tileSizeColumns;
                            index    += tileSizeColumns;
                        }
                        

                    }
               }

            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setSubMatrix(final double[][] subMatrix, final int row, final int column)
        throws MatrixIndexException {

        // safety checks
        final int refLength = subMatrix[0].length;
        if (refLength < 1) {
            throw MathRuntimeException.createIllegalArgumentException("matrix must have at least one column",
                                                                      null);             
        }
        final int endRow    = row + subMatrix.length - 1;
        final int endColumn = column + refLength - 1;
        checkSubMatrixIndex(row, endRow, column, endColumn);
        for (final double[] subRow : subMatrix) {
            if (subRow.length != refLength) {
                throw MathRuntimeException.createIllegalArgumentException(
                        "some rows have length {0} while others have length {1}",
                        refLength, subRow.length); 
            }
        }

        // compute tiles bounds
        final int tileStartRow    = row / tileSizeRows;
        final int tileEndRow      = (endRow + tileSizeRows) / tileSizeRows;
        final int tileStartColumn = column / tileSizeColumns;
        final int tileEndColumn   = (endColumn + tileSizeColumns) / tileSizeColumns;

        // perform copy tile-wise, to ensure good cache behavior
        for (int iTile = tileStartRow; iTile < tileEndRow; ++iTile) {
            final int firstRow = iTile * tileSizeRows;
            final int iStart   = Math.max(row,    firstRow);
            final int iEnd     = Math.min(endRow + 1, firstRow + tileSizeRows);

            for (int jTile = tileStartColumn; jTile < tileEndColumn; ++jTile) {
                final int firstColumn = jTile * tileSizeColumns;
                final int jStart      = Math.max(column,    firstColumn);
                final int jEnd        = Math.min(endColumn + 1, firstColumn + tileSizeColumns);
                final int jLength     = jEnd - jStart;
                final int tileStart   = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns;

                // handle one tile, row by row
                for (int i = iStart; i < iEnd; ++i) {
                    System.arraycopy(subMatrix[i - row], jStart - column,
                                     data, tileStart + (i - firstRow) * tileSizeColumns + (jStart - firstColumn),
                                     jLength);
                }

            }
        }
    }

    /** {@inheritDoc} */
    public RealMatrix getRowMatrix(final int row)
        throws MatrixIndexException {

        checkRowIndex(row);
        final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(1, columns);

        // a row matrix has always only one large tile,
        // because a single row cannot be split into 2^k tiles
        // perform copy tile-wise, to ensure good cache behavior
        final int iTile     = row / tileSizeRows;
        final int rowOffset = row - iTile * tileSizeRows;
        int outIndex        = 0;
        for (int jTile = 0; jTile < tileNumber; ++jTile) {
            final int kStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns +
                               rowOffset * tileSizeColumns;
            final int length = Math.min(outIndex + tileSizeColumns, columns) - outIndex;
            System.arraycopy(data, kStart, out.data, outIndex, length);
            outIndex += length;
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setRowMatrix(final int row, final RealMatrix matrix)
        throws MatrixIndexException, InvalidMatrixException {
        try {
            setRowMatrix(row, (RecursiveLayoutRealMatrix) matrix);
        } catch (ClassCastException cce) {
            super.setRowMatrix(row, matrix);
        }
    }

    /**
     * Sets the entries in row number <code>row</code>
     * as a row matrix.  Row indices start at 0.
     *
     * @param row the row to be set
     * @param matrix row matrix (must have one row and the same number of columns
     * as the instance)
     * @throws MatrixIndexException if the specified row index is invalid
     * @throws InvalidMatrixException if the matrix dimensions do not match one
     * instance row
     */
    public void setRowMatrix(final int row, final RecursiveLayoutRealMatrix matrix)
        throws MatrixIndexException, InvalidMatrixException {

        checkRowIndex(row);
        final int nCols = getColumnDimension();
        if ((matrix.getRowDimension() != 1) ||
            (matrix.getColumnDimension() != nCols)) {
            throw new InvalidMatrixException(
                    "dimensions mismatch: got {0}x{1} but expected {2}x{3}",
                    matrix.getRowDimension(), matrix.getColumnDimension(),
                    1, nCols);
        }

        // a row matrix has always only one large tile,
        // because a single row cannot be split into 2^k tiles
        // perform copy tile-wise, to ensure good cache behavior
        final int iTile     = row / tileSizeRows;
        final int rowOffset = row - iTile * tileSizeRows;
        int outIndex        = 0;
        for (int jTile = 0; jTile < tileNumber; ++jTile) {
            final int kStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns +
                               rowOffset * tileSizeColumns;
            final int length = Math.min(outIndex + tileSizeColumns, columns) - outIndex;
            System.arraycopy(matrix.data, outIndex, data, kStart, length);
            outIndex += length;
        }

    }
    
    /** {@inheritDoc} */
    public RealMatrix getColumnMatrix(final int column)
        throws MatrixIndexException {

        checkColumnIndex(column);
        final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(rows, 1);

        // a column matrix has always only one large tile,
        // because a single column cannot be split into 2^k tiles
        // perform copy tile-wise, to ensure good cache behavior
        final int jTile        = column / tileSizeColumns;
        final int columnOffset = column - jTile * tileSizeColumns;
        for (int iTile = 0; iTile < tileNumber; ++iTile) {
            final int pStart = iTile * tileSizeRows;
            final int pEnd   = Math.min(pStart + tileSizeRows, rows);
            final int kStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns +
                               columnOffset;
            for (int p = pStart, k = kStart; p < pEnd; ++p, k += tileSizeColumns) {
                out.data[p] = data[k];
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setColumnMatrix(final int column, final RealMatrix matrix)
        throws MatrixIndexException, InvalidMatrixException {
        try {
            setColumnMatrix(column, (RecursiveLayoutRealMatrix) matrix);
        } catch (ClassCastException cce) {
            super.setColumnMatrix(column, matrix);
        }
    }

    /**
     * Sets the entries in column number <code>column</code>
     * as a column matrix.  Column indices start at 0.
     *
     * @param column the column to be set
     * @param matrix column matrix (must have one column and the same number of rows
     * as the instance)
     * @throws MatrixIndexException if the specified column index is invalid
     * @throws InvalidMatrixException if the matrix dimensions do not match one
     * instance column
     */
    void setColumnMatrix(final int column, final RecursiveLayoutRealMatrix matrix)
        throws MatrixIndexException, InvalidMatrixException {

        checkColumnIndex(column);
        final int nRows = getRowDimension();
        if ((matrix.getRowDimension() != nRows) ||
            (matrix.getColumnDimension() != 1)) {
            throw new InvalidMatrixException(
                    "dimensions mismatch: got {0}x{1} but expected {2}x{3}",
                    matrix.getRowDimension(), matrix.getColumnDimension(),
                    nRows, 1);
        }

        // a column matrix has always only one large tile,
        // because a single column cannot be split into 2^k tiles
        // perform copy tile-wise, to ensure good cache behavior
        final int jTile        = column / tileSizeColumns;
        final int columnOffset = column - jTile * tileSizeColumns;
        for (int iTile = 0; iTile < tileNumber; ++iTile) {
            final int pStart = iTile * tileSizeRows;
            final int pEnd   = Math.min(pStart + tileSizeRows, rows);
            final int kStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns +
                               columnOffset;
            for (int p = pStart, k = kStart; p < pEnd; ++p, k += tileSizeColumns) {
                data[k] = matrix.data[p];
            }
        }

    }
    
    /** {@inheritDoc} */
    public void setRowVector(final int row, final RealVector vector)
        throws MatrixIndexException, InvalidMatrixException {
        try {
            setRow(row, ((RealVectorImpl) vector).getDataRef());
        } catch (ClassCastException cce) {
            checkRowIndex(row);
            if (vector.getDimension() != columns) {
                throw new InvalidMatrixException(
                        "dimensions mismatch: got {0}x{1} but expected {2}x{3}",
                        1, vector.getDimension(), 1, columns);
            }

            // perform copy tile-wise, to ensure good cache behavior
            final int iTile     = row / tileSizeRows;
            final int rowOffset = row - iTile * tileSizeRows;
            int outIndex        = 0;
            for (int jTile = 0; jTile < tileNumber; ++jTile) {
                final int kStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns +
                                   rowOffset * tileSizeColumns;
                final int length = Math.min(outIndex + tileSizeColumns, columns) - outIndex;
                for (int l = 0; l < length; ++l) {
                    data[kStart + l] = vector.getEntry(outIndex + l);
                }
                outIndex += length;
            }
        }
    }

    /** {@inheritDoc} */
    public void setColumnVector(final int column, final RealVector vector)
        throws MatrixIndexException, InvalidMatrixException {
        try {
            setColumn(column, ((RealVectorImpl) vector).getDataRef());
        } catch (ClassCastException cce) {
            checkColumnIndex(column);
            if (vector.getDimension() != rows) {
                throw new InvalidMatrixException(
                        "dimensions mismatch: got {0}x{1} but expected {2}x{3}",
                        vector.getDimension(), 1, rows, 1);
            }

            // perform copy tile-wise, to ensure good cache behavior
            final int jTile        = column / tileSizeColumns;
            final int columnOffset = column - jTile * tileSizeColumns;
            for (int iTile = 0; iTile < tileNumber; ++iTile) {
                final int pStart = iTile * tileSizeRows;
                final int pEnd   = Math.min(pStart + tileSizeRows, rows);
                final int kStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns +
                                   columnOffset;
                for (int p = pStart, k = kStart; p < pEnd; ++p, k += tileSizeColumns) {
                    data[k] = vector.getEntry(p);
                }
            }
        }
    }

    /** {@inheritDoc} */
    public double[] getRow(final int row)
        throws MatrixIndexException {

        checkRowIndex(row);
        final double[] out = new double[columns];

        // perform copy tile-wise, to ensure good cache behavior
        final int iTile     = row / tileSizeRows;
        final int rowOffset = row - iTile * tileSizeRows;
        int outIndex        = 0;
        for (int jTile = 0; jTile < tileNumber; ++jTile) {
            final int kStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns +
                               rowOffset * tileSizeColumns;
            final int length = Math.min(outIndex + tileSizeColumns, columns) - outIndex;
            System.arraycopy(data, kStart, out, outIndex, length);
            outIndex += length;
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setRow(final int row, final double[] array)
        throws MatrixIndexException, InvalidMatrixException {

        checkRowIndex(row);
        if (array.length != columns) {
            throw new InvalidMatrixException(
                    "dimensions mismatch: got {0}x{1} but expected {2}x{3}",
                    1, array.length, 1, columns);
        }

        // perform copy tile-wise, to ensure good cache behavior
        final int iTile     = row / tileSizeRows;
        final int rowOffset = row - iTile * tileSizeRows;
        int outIndex        = 0;
        for (int jTile = 0; jTile < tileNumber; ++jTile) {
            final int kStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns +
                               rowOffset * tileSizeColumns;
            final int length = Math.min(outIndex + tileSizeColumns, columns) - outIndex;
            System.arraycopy(array, outIndex, data, kStart, length);
            outIndex += length;
        }

    }

    /** {@inheritDoc} */
    public double[] getColumn(final int column)
        throws MatrixIndexException {

        checkColumnIndex(column);
        final double[] out = new double[rows];

        // perform copy tile-wise, to ensure good cache behavior
        final int jTile        = column / tileSizeColumns;
        final int columnOffset = column - jTile * tileSizeColumns;
        for (int iTile = 0; iTile < tileNumber; ++iTile) {
            final int pStart = iTile * tileSizeRows;
            final int pEnd   = Math.min(pStart + tileSizeRows, rows);
            final int kStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns +
                               columnOffset;
            for (int p = pStart, k = kStart; p < pEnd; ++p, k += tileSizeColumns) {
                out[p] = data[k];
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setColumn(final int column, final double[] array)
        throws MatrixIndexException, InvalidMatrixException {

        checkColumnIndex(column);
        if (array.length != rows) {
            throw new InvalidMatrixException(
                    "dimensions mismatch: got {0}x{1} but expected {2}x{3}",
                    array.length, 1, rows, 1);
        }

        // perform copy tile-wise, to ensure good cache behavior
        final int jTile        = column / tileSizeColumns;
        final int columnOffset = column - jTile * tileSizeColumns;
        for (int iTile = 0; iTile < tileNumber; ++iTile) {
            final int pStart = iTile * tileSizeRows;
            final int pEnd   = Math.min(pStart + tileSizeRows, rows);
            final int kStart = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns +
                               columnOffset;
            for (int p = pStart, k = kStart; p < pEnd; ++p, k += tileSizeColumns) {
                data[k] = array[p];
            }
        }

    }

    /** {@inheritDoc} */
    public double getEntry(final int row, final int column)
        throws MatrixIndexException {
        if ((row < 0) || (row >= rows) || (column < 0) || (column >= columns)) {
            throw new MatrixIndexException(
                    "no entry at indices ({0}, {1}) in a {2}x{3} matrix",
                    row, column, getRowDimension(), getColumnDimension());
        }
        return data[index(row, column)];
    }

    /** {@inheritDoc} */
    public void setEntry(final int row, final int column, final double value)
        throws MatrixIndexException {
        if ((row < 0) || (row >= rows) || (column < 0) || (column >= columns)) {
            throw new MatrixIndexException(
                    "no entry at indices ({0}, {1}) in a {2}x{3} matrix",
                    row, column, getRowDimension(), getColumnDimension());
        }
        data[index(row, column)] = value;
    }

    /** {@inheritDoc} */
    public void addToEntry(final int row, final int column, final double increment)
        throws MatrixIndexException {
        if ((row < 0) || (row >= rows) || (column < 0) || (column >= columns)) {
            throw new MatrixIndexException(
                    "no entry at indices ({0}, {1}) in a {2}x{3} matrix",
                    row, column, getRowDimension(), getColumnDimension());
        }
        data[index(row, column)] += increment;
    }

    /** {@inheritDoc} */
    public void multiplyEntry(final int row, final int column, final double factor)
        throws MatrixIndexException {
        if ((row < 0) || (row >= rows) || (column < 0) || (column >= columns)) {
            throw new MatrixIndexException(
                    "no entry at indices ({0}, {1}) in a {2}x{3} matrix",
                    row, column, getRowDimension(), getColumnDimension());
        }
        data[index(row, column)] *= factor;
    }

    /** {@inheritDoc} */
    public RealMatrix transpose() {

        final RecursiveLayoutRealMatrix out = new RecursiveLayoutRealMatrix(columns, rows);

        // perform transpose tile-wise, to ensure good cache behavior
        for (int index = 0; index < tileNumber * tileNumber; ++index) {
            final int tileStart    = index * tileSizeRows * tileSizeColumns;
            final long indices     = tilesIndices(index);
            final int outJTile     = (int) (indices >> 32);        // iTile in the instance
            final int outITile     = (int) (indices & 0xffffffff); // jTile in the instance
            final int outIndex     = tileIndex(outITile, outJTile);
            final int outTileStart = outIndex * tileSizeRows * tileSizeColumns;

            // transpose current tile
            final int outPStart = outITile * tileSizeColumns;
            final int outPEnd   = Math.min(outPStart + tileSizeColumns, columns);
            final int outQStart = outJTile * tileSizeRows;
            final int outQEnd   = Math.min(outQStart + tileSizeRows, rows);
            for (int outP = outPStart; outP < outPEnd; ++outP) {
                final int dP = outP - outPStart;
                int k = outTileStart + dP * tileSizeRows;
                int l = tileStart + dP;
                for (int outQ = outQStart; outQ < outQEnd; ++outQ) {
                    out.data[k++] = data[l];
                    l+= tileSizeColumns;
                }
            }

        }

        return out;

    }

    /** {@inheritDoc} */
    public int getRowDimension() {
        return rows;
    }

    /** {@inheritDoc} */
    public int getColumnDimension() {
        return columns;
    }

    /** {@inheritDoc} */
    public double[] operate(final double[] v)
        throws IllegalArgumentException {

        if (v.length != columns) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "vector length mismatch: got {0} but expected {1}",
                    v.length, columns);
        }
        final double[] out = new double[rows];

        // perform multiplication tile-wise, to ensure good cache behavior
        for (int index = 0; index < tileNumber * tileNumber; ++index) {
            final int tileStart = index * tileSizeRows * tileSizeColumns;
            final long indices  = tilesIndices(index);
            final int iTile     = (int) (indices >> 32);
            final int jTile     = (int) (indices & 0xffffffff);
            final int pStart    = iTile * tileSizeRows;
            final int pEnd      = Math.min(pStart + tileSizeRows, rows);
            final int qStart    = jTile * tileSizeColumns;
            final int qEnd      = Math.min(qStart + tileSizeColumns, columns);
            for (int p = pStart, k = tileStart; p < pEnd; ++p) {
                double sum = 0;
                int    q   = qStart;
                while (q < qEnd - 3) {
                    sum += data[k]     * v[q]     +
                           data[k + 1] * v[q + 1] +
                           data[k + 2] * v[q + 2] +
                           data[k + 3] * v[q + 3];
                    k += 4;
                    q += 4;
                }
                while (q < qEnd) {
                    sum += data[k++] * v[q++];
                }
                out[p] += sum;
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public double[] preMultiply(final double[] v)
        throws IllegalArgumentException {

        if (v.length != rows) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "vector length mismatch: got {0} but expected {1}",
                    v.length, rows);
        }
        final double[] out = new double[columns];

        final int offset1 = tileSizeColumns;
        final int offset2 = offset1 + offset1;
        final int offset3 = offset2 + offset1;
        final int offset4 = offset3 + offset1;

        // perform multiplication tile-wise, to ensure good cache behavior
        for (int index = 0; index < tileNumber * tileNumber; ++index) {
            final int tileStart = index * tileSizeRows * tileSizeColumns;
            final long indices  = tilesIndices(index);
            final int iTile     = (int) (indices >> 32);
            final int jTile     = (int) (indices & 0xffffffff);
            final int pStart    = iTile * tileSizeRows;
            final int pEnd      = Math.min(pStart + tileSizeRows, rows);
            final int qStart    = jTile * tileSizeColumns;
            final int qEnd      = Math.min(qStart + tileSizeColumns, columns);
            for (int q = qStart; q < qEnd; ++q) {
                int k = tileStart + q - qStart;
                double sum = 0;
                int p = pStart;
                while (p < pEnd - 3) {
                    sum += data[k]           * v[p]     +
                           data[k + offset1] * v[p + 1] +
                           data[k + offset2] * v[p + 2] +
                           data[k + offset3] * v[p + 3];
                    k += offset4;
                    p += 4;
                }
                while (p < pEnd) {
                    sum += data[k] * v[p++];
                    k   += offset1;
                }
                out[q] += sum;
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor)
        throws MatrixVisitorException {
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int iTile = 0; iTile < tileNumber; ++iTile) {
            final int pStart = iTile * tileSizeRows;
            final int pEnd   = Math.min(pStart + tileSizeRows, rows);
            for (int p = pStart; p < pEnd; ++p) {
                for (int jTile = 0; jTile < tileNumber; ++jTile) {
                    final int qStart    = jTile * tileSizeColumns;
                    final int qEnd      = Math.min(qStart + tileSizeColumns, columns);
                    final int tileStart = tileIndex(iTile, jTile) *
                                          tileSizeRows * tileSizeColumns;
                    final int kStart    = tileStart + (p - pStart) * tileSizeColumns;
                    for (int q = qStart, k = kStart; q < qEnd; ++q, ++k) {
                        data[k] = visitor.visit(p, q, data[k]);
                    }
                }
             }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor)
        throws MatrixVisitorException {
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int iTile = 0; iTile < tileNumber; ++iTile) {
            final int pStart = iTile * tileSizeRows;
            final int pEnd   = Math.min(pStart + tileSizeRows, rows);
            for (int p = pStart; p < pEnd; ++p) {
                for (int jTile = 0; jTile < tileNumber; ++jTile) {
                    final int qStart    = jTile * tileSizeColumns;
                    final int qEnd      = Math.min(qStart + tileSizeColumns, columns);
                    final int tileStart = tileIndex(iTile, jTile) *
                                          tileSizeRows * tileSizeColumns;
                    final int kStart    = tileStart + (p - pStart) * tileSizeColumns;
                    for (int q = qStart, k = kStart; q < qEnd; ++q, ++k) {
                        visitor.visit(p, q, data[k]);
                    }
                }
             }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor,
                                 final int startRow, final int endRow,
                                 final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
        for (int iTile = startRow / tileSizeRows; iTile < 1 + endRow / tileSizeRows; ++iTile) {
            final int p0     = iTile * tileSizeRows;
            final int pStart = Math.max(startRow, p0);
            final int pEnd   = Math.min((iTile + 1) * tileSizeRows, 1 + endRow);
            for (int p = pStart; p < pEnd; ++p) {
                for (int jTile = startColumn / tileSizeColumns; jTile < 1 + endColumn / tileSizeColumns; ++jTile) {
                    final int q0        = jTile * tileSizeColumns;
                    final int qStart    = Math.max(startColumn, q0);
                    final int qEnd      = Math.min((jTile + 1) * tileSizeColumns, 1 + endColumn);
                    final int tileStart = tileIndex(iTile, jTile) *
                                          tileSizeRows * tileSizeColumns;
                    final int kStart    = tileStart + (p - p0) * tileSizeColumns + (qStart - q0);
                    for (int q = qStart, k = kStart; q < qEnd; ++q, ++k) {
                        data[k] = visitor.visit(p, q, data[k]);
                    }
                }
             }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor,
                                 final int startRow, final int endRow,
                                 final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
        for (int iTile = startRow / tileSizeRows; iTile < 1 + endRow / tileSizeRows; ++iTile) {
            final int p0     = iTile * tileSizeRows;
            final int pStart = Math.max(startRow, p0);
            final int pEnd   = Math.min((iTile + 1) * tileSizeRows, 1 + endRow);
            for (int p = pStart; p < pEnd; ++p) {
                for (int jTile = startColumn / tileSizeColumns; jTile < 1 + endColumn / tileSizeColumns; ++jTile) {
                    final int q0        = jTile * tileSizeColumns;
                    final int qStart    = Math.max(startColumn, q0);
                    final int qEnd      = Math.min((jTile + 1) * tileSizeColumns, 1 + endColumn);
                    final int tileStart = tileIndex(iTile, jTile) *
                                          tileSizeRows * tileSizeColumns;
                    final int kStart    = tileStart + (p - p0) * tileSizeColumns + (qStart - q0);
                    for (int q = qStart, k = kStart; q < qEnd; ++q, ++k) {
                        visitor.visit(p, q, data[k]);
                    }
                }
             }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInColumnOrder(final RealMatrixChangingVisitor visitor)
        throws MatrixVisitorException {
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int jTile = 0; jTile < tileNumber; ++jTile) {
            final int qStart = jTile * tileSizeColumns;
            final int qEnd   = Math.min(qStart + tileSizeColumns, columns);
            for (int q = qStart; q < qEnd; ++q) {
                for (int iTile = 0; iTile < tileNumber; ++iTile) {
                    final int pStart    = iTile * tileSizeRows;
                    final int pEnd      = Math.min(pStart + tileSizeRows, rows);
                    final int tileStart = tileIndex(iTile, jTile) *
                                          tileSizeRows * tileSizeColumns;
                    final int kStart    = tileStart + (q - qStart);
                    for (int p = pStart, k = kStart; p < pEnd; ++p, k += tileSizeColumns) {
                        data[k] = visitor.visit(p, q, data[k]);
                    }
                }
             }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor)
        throws MatrixVisitorException {
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int jTile = 0; jTile < tileNumber; ++jTile) {
            final int qStart = jTile * tileSizeColumns;
            final int qEnd   = Math.min(qStart + tileSizeColumns, columns);
            for (int q = qStart; q < qEnd; ++q) {
                for (int iTile = 0; iTile < tileNumber; ++iTile) {
                    final int pStart    = iTile * tileSizeRows;
                    final int pEnd      = Math.min(pStart + tileSizeRows, rows);
                    final int tileStart = tileIndex(iTile, jTile) *
                                          tileSizeRows * tileSizeColumns;
                    final int kStart    = tileStart + (q - qStart);
                    for (int p = pStart, k = kStart; p < pEnd; ++p, k += tileSizeColumns) {
                        visitor.visit(p, q, data[k]);
                    }
                }
             }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInColumnOrder(final RealMatrixChangingVisitor visitor,
                                    final int startRow, final int endRow,
                                    final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int jTile = 0; jTile < tileNumber; ++jTile) {
            final int q0     = jTile * tileSizeColumns;
            final int qStart = Math.max(startColumn, q0);
            final int qEnd   = Math.min((jTile + 1) * tileSizeColumns, 1 + endColumn);
            for (int q = qStart; q < qEnd; ++q) {
                for (int iTile = 0; iTile < tileNumber; ++iTile) {
                    final int p0        = iTile * tileSizeRows;
                    final int pStart    = Math.max(startRow, p0);
                    final int pEnd      = Math.min((iTile + 1) * tileSizeRows, 1 + endRow);
                    final int tileStart = tileIndex(iTile, jTile) *
                                          tileSizeRows * tileSizeColumns;
                    final int kStart = tileStart + (pStart - p0) * tileSizeColumns + (q - q0);
                    for (int p = pStart, k = kStart; p < pEnd; ++p, k += tileSizeColumns) {
                        data[k] = visitor.visit(p, q, data[k]);
                    }
                }
             }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor,
                                    final int startRow, final int endRow,
                                    final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int jTile = 0; jTile < tileNumber; ++jTile) {
            final int q0     = jTile * tileSizeColumns;
            final int qStart = Math.max(startColumn, q0);
            final int qEnd   = Math.min((jTile + 1) * tileSizeColumns, 1 + endColumn);
            for (int q = qStart; q < qEnd; ++q) {
                for (int iTile = 0; iTile < tileNumber; ++iTile) {
                    final int p0        = iTile * tileSizeRows;
                    final int pStart    = Math.max(startRow, p0);
                    final int pEnd      = Math.min((iTile + 1) * tileSizeRows, 1 + endRow);
                    final int tileStart = tileIndex(iTile, jTile) *
                                           tileSizeRows * tileSizeColumns;
                    final int kStart = tileStart + (pStart - p0) * tileSizeColumns + (q - q0);
                    for (int p = pStart, k = kStart; p < pEnd; ++p, k += tileSizeColumns) {
                        visitor.visit(p, q, data[k]);
                    }
                }
             }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInOptimizedOrder(final RealMatrixChangingVisitor visitor)
        throws MatrixVisitorException {
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int index = 0; index < tileNumber * tileNumber; ++index) {
            final int tileStart = index * tileSizeRows * tileSizeColumns;
            final long indices  = tilesIndices(index);
            final int iTile     = (int) (indices >> 32);
            final int jTile     = (int) (indices & 0xffffffff);
            final int pStart    = iTile * tileSizeRows;
            final int pEnd      = Math.min(pStart + tileSizeRows, rows);
            final int qStart    = jTile * tileSizeColumns;
            final int qEnd      = Math.min(qStart + tileSizeColumns, columns);
            for (int p = pStart; p < pEnd; ++p) {
                final int kStart = tileStart + (p - pStart) * tileSizeColumns;
                for (int q = qStart, k = kStart; q < qEnd; ++q, ++k) {
                    data[k] = visitor.visit(p, q, data[k]);
                }
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInOptimizedOrder(final RealMatrixPreservingVisitor visitor)
        throws MatrixVisitorException {
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int index = 0; index < tileNumber * tileNumber; ++index) {
            final int tileStart = index * tileSizeRows * tileSizeColumns;
            final long indices  = tilesIndices(index);
            final int iTile     = (int) (indices >> 32);
            final int jTile     = (int) (indices & 0xffffffff);
            final int pStart    = iTile * tileSizeRows;
            final int pEnd      = Math.min(pStart + tileSizeRows, rows);
            final int qStart    = jTile * tileSizeColumns;
            final int qEnd      = Math.min(qStart + tileSizeColumns, columns);
            for (int p = pStart; p < pEnd; ++p) {
                final int kStart = tileStart + (p - pStart) * tileSizeColumns;
                for (int q = qStart, k = kStart; q < qEnd; ++q, ++k) {
                    visitor.visit(p, q, data[k]);
                }
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInOptimizedOrder(final RealMatrixChangingVisitor visitor,
                                       final int startRow, final int endRow,
                                       final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
        for (int index = 0; index < tileNumber * tileNumber; ++index) {
            final int tileStart = index * tileSizeRows * tileSizeColumns;
            final long indices  = tilesIndices(index);
            final int iTile     = (int) (indices >> 32);
            final int jTile     = (int) (indices & 0xffffffff);
            final int p0        = iTile * tileSizeRows;
            final int pStart    = Math.max(startRow, p0);
            final int pEnd      = Math.min((iTile + 1) * tileSizeRows, 1 + endRow);
            final int q0        = jTile * tileSizeColumns;
            final int qStart    = Math.max(startColumn, q0);
            final int qEnd      = Math.min((jTile + 1) * tileSizeColumns, 1 + endColumn);
            for (int p = pStart; p < pEnd; ++p) {
                final int kStart = tileStart + (p - p0) * tileSizeColumns + (qStart - q0);
                for (int q = qStart, k = kStart; q < qEnd; ++q, ++k) {
                    data[k] = visitor.visit(p, q, data[k]);
                }
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInOptimizedOrder(final RealMatrixPreservingVisitor visitor,
                                       final int startRow, final int endRow,
                                       final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
        for (int index = 0; index < tileNumber * tileNumber; ++index) {
            final int tileStart = index * tileSizeRows * tileSizeColumns;
            final long indices  = tilesIndices(index);
            final int iTile     = (int) (indices >> 32);
            final int jTile     = (int) (indices & 0xffffffff);
            final int p0        = iTile * tileSizeRows;
            final int pStart    = Math.max(startRow, p0);
            final int pEnd      = Math.min((iTile + 1) * tileSizeRows, 1 + endRow);
            final int q0        = jTile * tileSizeColumns;
            final int qStart    = Math.max(startColumn, q0);
            final int qEnd      = Math.min((jTile + 1) * tileSizeColumns, 1 + endColumn);
            for (int p = pStart; p < pEnd; ++p) {
                final int kStart = tileStart + (p - p0) * tileSizeColumns + (qStart - q0);
                for (int q = qStart, k = kStart; q < qEnd; ++q, ++k) {
                    visitor.visit(p, q, data[k]);
                }
            }
        }
        return visitor.end();
    }

    /**
     * Get the index of an element.
     * @param row row index of the element
     * @param column column index of the element
     * @return index of the element
     */
    private int index(final int row, final int columns) {
        final int iTile       = row     / tileSizeRows;
        final int jTile       = columns / tileSizeColumns;
        final int tileStart   = tileIndex(iTile, jTile) * tileSizeRows * tileSizeColumns;
        final int indexInTile = (row % tileSizeRows) * tileSizeColumns +
                                (columns % tileSizeColumns);
        return tileStart + indexInTile;
    }

    /**
     * Get the index of a tile.
     * @param iTile row index of the tile
     * @param jTile column index of the tile
     * @return index of the tile
     */
    private static int tileIndex(int iTile, int jTile) {

        // compute n = 2^k such that a nxn square contains the indices
        int n = Integer.highestOneBit(Math.max(iTile, jTile)) << 1;

        // start recursion by noting the index is somewhere in the nxn
        // square whose lowest index is 0 and which has direct orientation
        int lowIndex   = 0;
        boolean direct = true;

        // the tail-recursion on the square size is replaced by an iteration here
        while (n > 1) {

            // reduce square to 4 quadrants
            n >>= 1;
            final int n2 = n * n;

            // check in which quadrant the element is,
            // updating the lowest index of the quadrant and its orientation
            if (iTile < n) {
                if (jTile < n) {
                    // the element is in the top-left quadrant
                    if (!direct) {
                        lowIndex += 2 * n2;
                        direct = true;
                    }
                } else {
                    // the element is in the top-right quadrant
                    jTile -= n;
                    if (direct) {
                        lowIndex += n2;
                        direct = false;
                    } else {
                        lowIndex += 3 * n2;
                    }
                }
            } else {
                iTile -= n;
                if (jTile < n) {
                    // the element is in the bottom-left quadrant
                    if (direct) {
                        lowIndex += 3 * n2;
                    } else {
                        lowIndex += n2;
                        direct = true;
                    }
                } else {
                    // the element is in the bottom-right quadrant
                    jTile -= n;
                    if (direct) {
                        lowIndex += 2 * n2;
                        direct = false;
                    }
                }
            }
        }

        // the lowest index of the remaining 1x1 quadrant is the requested index
        return lowIndex;

    }

    /**
     * Get the row and column tile indices of a tile.
     * @param index index of the tile in the layout
     * @return row and column indices packed in one long (row tile index
     * in 32 high order bits, column tile index in low order bits)
     */
    private static long tilesIndices(int index) {

        // compute n = 2^k such that a nxn square contains the index
        int n = Integer.highestOneBit((int) Math.sqrt(index)) << 1;

        // start recursion by noting the index is somewhere in the nxn
        // square whose lowest index is 0 and which has direct orientation
        int iLow       = 0;
        int jLow       = 0;
        boolean direct = true;

        // the tail-recursion on the square size is replaced by an iteration here
        while (n > 1) {

            // reduce square to 4 quadrants
            n >>= 1;
            final int n2 = n * n;

            // check in which quadrant the element is,
            // updating the low indices of the quadrant and its orientation
            switch (index / n2) {
            case 0 :
                if (!direct) {
                    iLow += n;
                    jLow += n;
                }
                break;
            case 1 :
                if (direct) {
                    jLow += n;
                } else {
                    iLow += n;
                }
                index -= n2;
                direct = !direct;
                break;
            case 2 :
                if (direct) {
                    iLow += n;
                    jLow += n;
                }
                index -= 2 * n2;
                direct = !direct;
                break;
            default :
                if (direct) {
                    iLow += n;
                } else {
                    jLow += n;
                }
            index -= 3 * n2;
            }

        }

        // the lowest indices of the remaining 1x1 quadrant are the requested indices
        return (((long) iLow) << 32) | (long) jLow;

    }

    /**
     * Compute the power of two number of tiles for a matrix.
     * @param rows number of rows
     * @param columns number of columns
     * @return power of two number of tiles
     */
    private static int tilesNumber(final int rows, final int columns) {

        // find the minimal number of tiles, given that one double variable is 8 bytes
        final int nbElements         = rows * columns;
        final int maxElementsPerTile = MAX_TILE_SIZE_BYTES / 8;
        final int minTiles           = nbElements / maxElementsPerTile;

        // the number of tiles must be a 2^k x 2^k square
        int twoK = 1;
        for (int nTiles = minTiles; nTiles != 0; nTiles >>= 2) {
            twoK <<= 1;
        }

        // make sure the tiles have at least one row and one column each
        // (this may lead to tile sizes greater than MAX_BLOCK_SIZE_BYTES,
        //  in degenerate cases like a 3000x1 matrix)
        while (twoK > Math.min(rows, columns)) {
            twoK >>= 1;
        }

        return twoK;

    }

    /**
     * Compute optimal tile size for a row or column count.
     * @param count row or column count
     * @param twoK optimal tile number (must be a power of 2)
     * @return optimal tile size
     */
    private static int tileSize(final int count, final int twoK) {
        return (count + twoK - 1) / twoK;        
    }

}
