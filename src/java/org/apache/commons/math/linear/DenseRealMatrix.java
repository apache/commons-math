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
import java.util.Arrays;

import org.apache.commons.math.MathRuntimeException;

/**
 * Implementation of RealMatrix using a flat arrays to store square blocks of the matrix.
 * <p>
 * This implementation is cache-friendly. Square blocks are stored as small arrays and allow
 * efficient traversal of data both in row major direction and columns major direction. This
 * greatly increases performances for algorithms that use crossed directions loops like
 * multiplication or transposition.
 * </p>
 * <p>
 * The layout complexity overhead versus simple mapping of matrices to java
 * arrays is negligible for small matrices (about 1%). The gain from cache efficiency leads
 * to up to 3-fold improvements for matrices of moderate to large size.
 * </p>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class DenseRealMatrix extends AbstractRealMatrix implements Serializable {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = 4991895511313664478L;

    /** Block size. */
    private static final int BLOCK_SIZE = 52;

    /** Blocks of matrix entries. */
    private final double blocks[][];

    /** Number of rows of the matrix. */
    private final int rows;

    /** Number of columns of the matrix. */
    private final int columns;

    /** Number of block rows of the matrix. */
    private final int blockRows;

    /** Number of block columns of the matrix. */
    private final int blockColumns;

    /**
     * Create a new matrix with the supplied row and column dimensions.
     *
     * @param rows  the number of rows in the new matrix
     * @param columns  the number of columns in the new matrix
     * @throws IllegalArgumentException if row or column dimension is not
     *  positive
     */
    public DenseRealMatrix(final int rows, final int columns)
        throws IllegalArgumentException {

        super(rows, columns);
        this.rows    = rows;
        this.columns = columns;

        // number of blocks
        blockRows    = (rows    + BLOCK_SIZE - 1) / BLOCK_SIZE;
        blockColumns = (columns + BLOCK_SIZE - 1) / BLOCK_SIZE;

        // number of lines in smaller blocks at the bottom side of the matrix
        final int lastLines = rows - (blockRows - 1) * BLOCK_SIZE;

        // number of columns in smaller blocks at the right side of the matrix
        final int lastColumns = columns - (blockColumns - 1) * BLOCK_SIZE;

        // allocate storage blocks, taking care of smaller ones at right and bottom
        blocks       = new double[blockRows * blockColumns][];
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < (blockRows - 1); ++iBlock) {
            for (int jBlock = 0; jBlock < (blockColumns - 1); ++jBlock) {
                blocks[blockIndex++] = new double[BLOCK_SIZE * BLOCK_SIZE];
            }
            blocks[blockIndex++] = new double[BLOCK_SIZE * lastColumns];
        }
        for (int jBlock = 0; jBlock < (blockColumns - 1); ++jBlock) {
            blocks[blockIndex++] = new double[lastLines * BLOCK_SIZE];
        }
        blocks[blockIndex++] = new double[lastLines * lastColumns];

    }

    /**
     * Create a new RealMatrix using the input array as the underlying
     * data array.
     * <p>The input array is copied (and data rearranged), it is not referenced.</p>
     *
     * @param d data for new matrix
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length)
     */
    public DenseRealMatrix(final double[][] d)
        throws IllegalArgumentException {

        // build empty instance
        this(d.length, d[0].length);

        // fill in instance
        for (int i = 0; i < d.length; ++i) {
            final double[] rowI = d[i];
            if (rowI.length != columns) {
                throw MathRuntimeException.createIllegalArgumentException("some rows have length {0} while others have length {1}",
                                                                          new Object[] {
                                                                              columns, rowI.length
                                                                          }); 
            }
            for (int j = 0; j < rowI.length; ++j) {
                setEntry(i, j, rowI[j]);
            }
        }

    }

    /** {@inheritDoc} */
    public RealMatrix createMatrix(final int rowDimension, final int columnDimension)
        throws IllegalArgumentException {
        return new DenseRealMatrix(rowDimension, columnDimension);
    }

    /** {@inheritDoc} */
    public RealMatrix copy() {

        // create an empty matrix
        DenseRealMatrix copied = new DenseRealMatrix(rows, columns);

        // copy the blocks
        for (int i = 0; i < blocks.length; ++i) {
            System.arraycopy(blocks[i], 0, copied.blocks[i], 0, blocks[i].length);
        }

        return copied;

    }

    /** {@inheritDoc} */
    public RealMatrix add(final RealMatrix m)
        throws IllegalArgumentException {
        try {
            return add((DenseRealMatrix) m);
        } catch (ClassCastException cce) {

            // safety check
            checkAdditionCompatible(m);

            final DenseRealMatrix out = new DenseRealMatrix(rows, columns);

            // perform addition block-wise, to ensure good cache behavior
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
                for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {

                    // perform addition on the current block
                    final double[] outBlock = out.blocks[blockIndex];
                    final double[] tBlock   = blocks[blockIndex];
                    final int      pStart   = iBlock * BLOCK_SIZE;
                    final int      pEnd     = Math.min(pStart + BLOCK_SIZE, rows);
                    final int      qStart   = jBlock * BLOCK_SIZE;
                    final int      qEnd     = Math.min(qStart + BLOCK_SIZE, columns);
                    for (int p = pStart, k = 0; p < pEnd; ++p) {
                        for (int q = qStart; q < qEnd; ++q, ++k) {
                            outBlock[k] = tBlock[k] + m.getEntry(p, q);
                        }
                    }

                    // go to next block
                    ++blockIndex;

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
    public DenseRealMatrix add(final DenseRealMatrix m)
        throws IllegalArgumentException {

        // safety check
        checkAdditionCompatible(m);

        final DenseRealMatrix out = new DenseRealMatrix(rows, columns);

        // perform addition block-wise, to ensure good cache behavior
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final double[] outBlock = out.blocks[blockIndex];
            final double[] tBlock   = blocks[blockIndex];
            final double[] mBlock   = m.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k] + mBlock[k];
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix subtract(final RealMatrix m)
        throws IllegalArgumentException {
        try {
            return subtract((DenseRealMatrix) m);
        } catch (ClassCastException cce) {

            // safety check
            checkSubtractionCompatible(m);

            final DenseRealMatrix out = new DenseRealMatrix(rows, columns);

            // perform subtraction block-wise, to ensure good cache behavior
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
                for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {

                    // perform subtraction on the current block
                    final double[] outBlock = out.blocks[blockIndex];
                    final double[] tBlock   = blocks[blockIndex];
                    final int      pStart   = iBlock * BLOCK_SIZE;
                    final int      pEnd     = Math.min(pStart + BLOCK_SIZE, rows);
                    final int      qStart   = jBlock * BLOCK_SIZE;
                    final int      qEnd     = Math.min(qStart + BLOCK_SIZE, columns);
                    for (int p = pStart, k = 0; p < pEnd; ++p) {
                        for (int q = qStart; q < qEnd; ++q, ++k) {
                            outBlock[k] = tBlock[k] - m.getEntry(p, q);
                        }
                    }

                    // go to next block
                    ++blockIndex;

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
    public DenseRealMatrix subtract(final DenseRealMatrix m)
        throws IllegalArgumentException {

        // safety check
        checkSubtractionCompatible(m);

        final DenseRealMatrix out = new DenseRealMatrix(rows, columns);

        // perform subtraction block-wise, to ensure good cache behavior
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final double[] outBlock = out.blocks[blockIndex];
            final double[] tBlock   = blocks[blockIndex];
            final double[] mBlock   = m.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k] - mBlock[k];
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix scalarAdd(final double d)
        throws IllegalArgumentException {

        final DenseRealMatrix out = new DenseRealMatrix(rows, columns);

        // perform subtraction block-wise, to ensure good cache behavior
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final double[] outBlock = out.blocks[blockIndex];
            final double[] tBlock   = blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k] + d;
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix scalarMultiply(final double d)
        throws IllegalArgumentException {

        final DenseRealMatrix out = new DenseRealMatrix(rows, columns);

        // perform subtraction block-wise, to ensure good cache behavior
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final double[] outBlock = out.blocks[blockIndex];
            final double[] tBlock   = blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k] * d;
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix multiply(final RealMatrix m)
        throws IllegalArgumentException {
        try {
            return multiply((DenseRealMatrix) m);
        } catch (ClassCastException cce) {

            // safety check
            checkMultiplicationCompatible(m);

            final DenseRealMatrix out = new DenseRealMatrix(rows, m.getColumnDimension());

            // perform multiplication block-wise, to ensure good cache behavior
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {

                final int pStart = iBlock * BLOCK_SIZE;
                final int pEnd   = Math.min(pStart + BLOCK_SIZE, rows);

                for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {

                    final int qStart = jBlock * BLOCK_SIZE;
                    final int qEnd   = Math.min(qStart + BLOCK_SIZE, m.getColumnDimension());

                    // select current block
                    final double[] outBlock = out.blocks[blockIndex];

                    // perform multiplication on current block
                    for (int kBlock = 0; kBlock < blockColumns; ++kBlock) {
                        final int kWidth      = blockWidth(kBlock);
                        final double[] tBlock = blocks[iBlock * blockColumns + kBlock];
                        final int rStart      = kBlock * BLOCK_SIZE;
                        for (int p = pStart, k = 0; p < pEnd; ++p) {
                            final int lStart = (p - pStart) * kWidth;
                            final int lEnd   = lStart + kWidth;
                            for (int q = qStart; q < qEnd; ++q) {
                                double sum = 0;
                                for (int l = lStart, r = rStart; l < lEnd; ++l, ++r) {
                                    sum += tBlock[l] * m.getEntry(r, q);
                                }
                                outBlock[k++] += sum;
                            }
                        }
                    }

                    // go to next block
                    ++blockIndex;

                }
            }

            return out;

        }
    }

    /**
     * Returns the result of postmultiplying this by m.
     *
     * @param m    matrix to postmultiply by
     * @return     this * m
     * @throws     IllegalArgumentException
     *             if columnDimension(this) != rowDimension(m)
     */
    public DenseRealMatrix multiply(DenseRealMatrix m) throws IllegalArgumentException {

        // safety check
        checkMultiplicationCompatible(m);

        final DenseRealMatrix out = new DenseRealMatrix(rows, m.columns);

        // perform multiplication block-wise, to ensure good cache behavior
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {

            final int pStart = iBlock * BLOCK_SIZE;
            final int pEnd   = Math.min(pStart + BLOCK_SIZE, rows);

            for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                final int jWidth = out.blockWidth(jBlock);
                final int jWidth2 = jWidth  + jWidth;
                final int jWidth3 = jWidth2 + jWidth;
                final int jWidth4 = jWidth3 + jWidth;

                // select current block
                final double[] outBlock = out.blocks[blockIndex];

                // perform multiplication on current block
                for (int kBlock = 0; kBlock < blockColumns; ++kBlock) {
                    final int kWidth = blockWidth(kBlock);
                    final double[] tBlock = blocks[iBlock * blockColumns + kBlock];
                    final double[] mBlock = m.blocks[kBlock * m.blockColumns + jBlock];
                    for (int p = pStart, k = 0; p < pEnd; ++p) {
                        final int lStart = (p - pStart) * kWidth;
                        final int lEnd   = lStart + kWidth;
                        for (int nStart = 0; nStart < jWidth; ++nStart) {
                            double sum = 0;
                            int l = lStart;
                            int n = nStart;
                            while (l < lEnd - 3) {
                                sum += tBlock[l] * mBlock[n] +
                                       tBlock[l + 1] * mBlock[n + jWidth] +
                                       tBlock[l + 2] * mBlock[n + jWidth2] +
                                       tBlock[l + 3] * mBlock[n + jWidth3];
                                l += 4;
                                n += jWidth4;
                            }
                            while (l < lEnd) {
                                sum += tBlock[l++] * mBlock[n];
                                n += jWidth;
                            }
                            outBlock[k++] += sum;
                        }
                    }
                }

                // go to next block
                ++blockIndex;

            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public double[][] getData() {

        final double[][] data = new double[getRowDimension()][getColumnDimension()];
        final int lastColumns = columns - (blockColumns - 1) * BLOCK_SIZE;

        for (int iBlock = 0; iBlock < blockRows; ++iBlock) {
            final int pStart = iBlock * BLOCK_SIZE;
            final int pEnd   = Math.min(pStart + BLOCK_SIZE, rows);
            int regularPos   = 0;
            int lastPos      = 0;
            for (int p = pStart; p < pEnd; ++p) {
                final double[] dataP = data[p];
                int blockIndex = iBlock * blockColumns;
                int dataPos    = 0;
                for (int jBlock = 0; jBlock < blockColumns - 1; ++jBlock) {
                    System.arraycopy(blocks[blockIndex++], regularPos, dataP, dataPos, BLOCK_SIZE);
                    dataPos += BLOCK_SIZE;
                }
                System.arraycopy(blocks[blockIndex], lastPos, dataP, dataPos, lastColumns);
                regularPos += BLOCK_SIZE;
                lastPos    += lastColumns;
            }
        }

        return data;
        
    }

    /** {@inheritDoc} */
    public double getNorm() {
        final double[] colSums = new double[BLOCK_SIZE];
        double maxColSum = 0;
        for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
            final int jWidth = blockWidth(jBlock);
            Arrays.fill(colSums, 0, jWidth, 0.0);
            for (int iBlock = 0; iBlock < blockRows; ++iBlock) {
                final int iHeight = blockHeight(iBlock);
                final double[] block = blocks[iBlock * blockColumns + jBlock];
                for (int j = 0; j < jWidth; ++j) {
                    double sum = 0;
                    for (int i = 0; i < iHeight; ++i) {
                        sum += Math.abs(block[i * jWidth + j]);
                    }
                    colSums[j] += sum;
                }
            }
            for (int j = 0; j < jWidth; ++j) {
                maxColSum = Math.max(maxColSum, colSums[j]);
            }
        }
        return maxColSum;
    }
    
    /** {@inheritDoc} */
    public double getFrobeniusNorm() {
        double sum2 = 0;
        for (int blockIndex = 0; blockIndex < blocks.length; ++blockIndex) {
            for (final double entry : blocks[blockIndex]) {
                sum2 += entry * entry;
            }
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
        final DenseRealMatrix out =
            new DenseRealMatrix(endRow - startRow + 1, endColumn - startColumn + 1);

        // compute blocks shifts
        final int blockStartRow    = startRow    / BLOCK_SIZE;
        final int rowsShift        = startRow    % BLOCK_SIZE;
        final int blockStartColumn = startColumn / BLOCK_SIZE;
        final int columnsShift     = startColumn % BLOCK_SIZE;

        // perform extraction block-wise, to ensure good cache behavior
        for (int iBlock = 0, pBlock = blockStartRow; iBlock < out.blockRows; ++iBlock, ++pBlock) {
            final int iHeight = out.blockHeight(iBlock);
            for (int jBlock = 0, qBlock = blockStartColumn; jBlock < out.blockColumns; ++jBlock, ++qBlock) {
                final int jWidth = out.blockWidth(jBlock);

                // handle one block of the output matrix
                final int      outIndex = iBlock * out.blockColumns + jBlock;
                final double[] outBlock = out.blocks[outIndex];
                final int      index    = pBlock * blockColumns + qBlock;
                final int      width    = blockWidth(index);

                final int heightExcess = iHeight + rowsShift - BLOCK_SIZE;
                final int widthExcess  = jWidth + columnsShift - BLOCK_SIZE;
                if (heightExcess > 0) {
                    // the submatrix block spans on two blocks rows from the original matrix
                    if (widthExcess > 0) {
                        // the submatrix block spans on two blocks columns from the original matrix
                        final int width2 = blockWidth(index + 1);
                        copyBlockPart(blocks[index], width,
                                      rowsShift, BLOCK_SIZE,
                                      columnsShift, BLOCK_SIZE,
                                      outBlock, jWidth, 0, 0);
                        copyBlockPart(blocks[index + 1], width2,
                                      rowsShift, BLOCK_SIZE,
                                      0, widthExcess,
                                      outBlock, jWidth, 0, jWidth - widthExcess);
                        copyBlockPart(blocks[index + blockColumns], width,
                                      0, heightExcess,
                                      columnsShift, BLOCK_SIZE,
                                      outBlock, jWidth, iHeight - heightExcess, 0);
                        copyBlockPart(blocks[index + blockColumns + 1], width2,
                                      0, heightExcess,
                                      0, widthExcess,
                                      outBlock, jWidth, iHeight - heightExcess, jWidth - widthExcess);
                    } else {
                        // the submatrix block spans on one block column from the original matrix
                        copyBlockPart(blocks[index], width,
                                      rowsShift, BLOCK_SIZE,
                                      columnsShift, jWidth + columnsShift,
                                      outBlock, jWidth, 0, 0);
                        copyBlockPart(blocks[index + blockColumns], width,
                                      0, heightExcess,
                                      columnsShift, jWidth + columnsShift,
                                      outBlock, jWidth, iHeight - heightExcess, 0);
                    }
                } else {
                    // the submatrix block spans on one block row from the original matrix
                    if (widthExcess > 0) {
                        // the submatrix block spans on two blocks columns from the original matrix
                        final int width2 = blockWidth(index + 1);
                        copyBlockPart(blocks[index], width,
                                      rowsShift, iHeight + rowsShift,
                                      columnsShift, BLOCK_SIZE,
                                      outBlock, jWidth, 0, 0);
                        copyBlockPart(blocks[index + 1], width2,
                                      rowsShift, iHeight + rowsShift,
                                      0, widthExcess,
                                      outBlock, jWidth, 0, jWidth - widthExcess);
                    } else {
                        // the submatrix block spans on one block column from the original matrix
                        copyBlockPart(blocks[index], width,
                                      rowsShift, iHeight + rowsShift,
                                      columnsShift, jWidth + columnsShift,
                                      outBlock, jWidth, 0, 0);
                    }
               }

            }
        }

        return out;

    }

    /**
     * Copy a part of a block into another one
     * <p>This method can be called only when the specified part fits in both
     * blocks, no verification is done here.</p>
     * @param srcBlock source block
     * @param srcWidth source block width ({@link #BLOCK_SIZE} or smaller)
     * @param srcStartRow start row in the source block
     * @param srcEndRow end row (exclusive) in the source block
     * @param srcStartColumn start column in the source block
     * @param srcEndColumn end column (exclusive) in the source block
     * @param dstBlock destination block
     * @param dstWidth destination block width ({@link #BLOCK_SIZE} or smaller)
     * @param dstStartRow start row in the destination block
     * @param dstStartColumn start column in the destination block
     */
    private void copyBlockPart(final double[] srcBlock, final int srcWidth,
                               final int srcStartRow, final int srcEndRow,
                               final int srcStartColumn, final int srcEndColumn,
                               final double[] dstBlock, final int dstWidth,
                               final int dstStartRow, final int dstStartColumn) {
        final int length = srcEndColumn - srcStartColumn;
        int srcPos = srcStartRow * srcWidth + srcStartColumn;
        int dstPos = dstStartRow * dstWidth + dstStartColumn;
        for (int srcRow = srcStartRow; srcRow < srcEndRow; ++srcRow) {
            System.arraycopy(srcBlock, srcPos, dstBlock, dstPos, length);
            srcPos += srcWidth;
            dstPos += dstWidth;
        }
    }

    /** {@inheritDoc} */
    public double getEntry(final int row, final int column)
        throws MatrixIndexException {
        try {
            final int iBlock = row    / BLOCK_SIZE;
            final int jBlock = column / BLOCK_SIZE;
            final int k      = (row    - iBlock * BLOCK_SIZE) * blockWidth(jBlock) +
                               (column - jBlock * BLOCK_SIZE);
            return blocks[iBlock * blockColumns + jBlock][k];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MatrixIndexException("no entry at indices ({0}, {1}) in a {2}x{3} matrix",
                                           new Object[] {
                                               row, column,
                                               getRowDimension(), getColumnDimension()
                                           });
        }
    }

    /** {@inheritDoc} */
    public void setEntry(final int row, final int column, final double value)
        throws MatrixIndexException {
        try {
            final int iBlock = row    / BLOCK_SIZE;
            final int jBlock = column / BLOCK_SIZE;
            final int k      = (row    - iBlock * BLOCK_SIZE) * blockWidth(jBlock) +
                               (column - jBlock * BLOCK_SIZE);
            blocks[iBlock * blockColumns + jBlock][k] = value;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MatrixIndexException("no entry at indices ({0}, {1}) in a {2}x{3} matrix",
                                           new Object[] {
                                               row, column,
                                               getRowDimension(), getColumnDimension()
                                           });
        }
    }

    /** {@inheritDoc} */
    public void addToEntry(final int row, final int column, final double increment)
        throws MatrixIndexException {
        try {
            final int iBlock = row    / BLOCK_SIZE;
            final int jBlock = column / BLOCK_SIZE;
            final int k      = (row    - iBlock * BLOCK_SIZE) * blockWidth(jBlock) +
                               (column - jBlock * BLOCK_SIZE);
            blocks[iBlock * blockColumns + jBlock][k] += increment;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MatrixIndexException("no entry at indices ({0}, {1}) in a {2}x{3} matrix",
                                           new Object[] {
                                               row, column,
                                               getRowDimension(), getColumnDimension()
                                           });
        }
    }

    /** {@inheritDoc} */
    public void multiplyEntry(final int row, final int column, final double factor)
        throws MatrixIndexException {
        try {
            final int iBlock = row    / BLOCK_SIZE;
            final int jBlock = column / BLOCK_SIZE;
            final int k      = (row    - iBlock * BLOCK_SIZE) * blockWidth(jBlock) +
                               (column - jBlock * BLOCK_SIZE);
            blocks[iBlock * blockColumns + jBlock][k] *= factor;
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
        final DenseRealMatrix out = new DenseRealMatrix(nCols, nRows);

        // perform transpose block-wise, to ensure good cache behavior
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < blockColumns; ++iBlock) {
            for (int jBlock = 0; jBlock < blockRows; ++jBlock) {

                // transpose current block
                final double[] outBlock = out.blocks[blockIndex];
                final double[] tBlock   = blocks[jBlock * blockColumns + iBlock];
                final int      pStart   = iBlock * BLOCK_SIZE;
                final int      pEnd     = Math.min(pStart + BLOCK_SIZE, columns);
                final int      qStart   = jBlock * BLOCK_SIZE;
                final int      qEnd     = Math.min(qStart + BLOCK_SIZE, rows);
                for (int p = pStart, k = 0; p < pEnd; ++p) {
                    final int lInc = pEnd - pStart;
                    for (int q = qStart, l = p - pStart; q < qEnd; ++q, l+= lInc) {
                        outBlock[k++] = tBlock[l];
                    }
                }

                // go to next block
                ++blockIndex;

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

        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (v.length != nCols) {
            throw new IllegalArgumentException("vector has wrong length");
        }
        final double[] out = new double[nRows];

        // perform multiplication block-wise, to ensure good cache behavior
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < blockRows; ++iBlock) {
            for (int jBlock = 0; jBlock < blockColumns; ++jBlock) {
                final double[] block  = blocks[blockIndex];
                final int      pStart = iBlock * BLOCK_SIZE;
                final int      pEnd   = Math.min(pStart + BLOCK_SIZE, rows);
                final int      qStart = jBlock * BLOCK_SIZE;
                final int      qEnd   = Math.min(qStart + BLOCK_SIZE, columns);
                for (int p = pStart, k = 0; p < pEnd; ++p) {
                    double sum = 0;
                    int q = qStart;
                    while (q < qEnd - 3) {
                        sum += block[k]     * v[q]     +
                               block[k + 1] * v[q + 1] +
                               block[k + 2] * v[q + 2] +
                               block[k + 3] * v[q + 3];
                        ++k;
                        ++q;
                    }
                    while (q < qEnd) {
                        sum += block[k++] * v[q++];
                    }
                    out[p] += sum;
                }
                ++blockIndex;
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealVector operate(final RealVector v)
        throws IllegalArgumentException {
        try {
            return operate((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            return super.operate(v);
        }
    }

    /**
     * Returns the result of multiplying this by the vector <code>v</code>.
     *
     * @param v the vector to operate on
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    public RealVectorImpl operate(final RealVectorImpl v)
        throws IllegalArgumentException {
        return new RealVectorImpl(operate(v.getDataRef()), false);
    }

    /**
     * Get the height of a block.
     * @param blockRow row index (in block sense) of the block
     * @return height (number of rows) of the block
     */
    private int blockHeight(final int blockRow) {
        return (blockRow == blockRows - 1) ? rows - blockRow * BLOCK_SIZE : BLOCK_SIZE;
    }

    /**
     * Get the width of a block.
     * @param blockColumn column index (in block sense) of the block
     * @return width (number of columns) of the block
     */
    private int blockWidth(final int blockColumn) {
        return (blockColumn == blockColumns - 1) ? columns - blockColumn * BLOCK_SIZE : BLOCK_SIZE;
    }

}
