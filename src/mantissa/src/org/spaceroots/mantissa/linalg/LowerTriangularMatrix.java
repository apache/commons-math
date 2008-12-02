// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.linalg;

/** This class implements lower triangular matrices of linear algebra.

 * @version $Id$
 * @author L. Maisonobe

 */

public class LowerTriangularMatrix
  extends SquareMatrix {

  /** Simple constructor.
   * This constructor builds a lower triangular matrix of specified order, all
   * elements being zeros.
   * @param order order of the matrix
   */
  public LowerTriangularMatrix(int order) {
    super(order);
  }

  /** Simple constructor.
   * Build a matrix with specified elements.
   * @param order order of the matrix
   * @param data table of the matrix elements (stored row after row)
   */
  public LowerTriangularMatrix(int order, double[] data) {
    super(order, data);
  }

  /** Copy constructor.
   * @param l lower triangular matrix to copy
   */
  public LowerTriangularMatrix(LowerTriangularMatrix l) {
    super(l);
  }

  public Matrix duplicate() {
    return new LowerTriangularMatrix(this);
  }

  public void setElement(int i, int j, double value) {
    if (i < j) {
      throw new ArrayIndexOutOfBoundsException("cannot set elements"
                                               + " above diagonal of a"
                                               + " lower triangular matrix");
    }
    super.setElement(i, j, value);
  }

  /** Add a matrix to the instance.
   * This method adds a matrix to the instance. It does modify the instance.
   * @param l lower triangular matrix to add
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfAdd(LowerTriangularMatrix l) {

    // validity check
    if ((rows != l.rows) || (columns != l.columns)) {
      throw new IllegalArgumentException("cannot add a "
                                         + l.rows + 'x' + l.columns
                                         + " matrix to a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }

    // addition loop
    for (int i = 0; i < rows; ++i) {
      for (int index = i * columns; index < i * (columns + 1) + 1; ++index) {
        data[index] += l.data[index];
      }
    }

  }

  /** Substract a matrix from the instance.
   * This method substract a matrix from the instance. It does modify the instance.
   * @param l lower triangular matrix to substract
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfSub(LowerTriangularMatrix l) {

    // validity check
    if ((rows != l.rows) || (columns != l.columns)) {
      throw new IllegalArgumentException("cannot substract a "
                                         + l.rows + 'x' + l.columns
                                         + " matrix from a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }

    // substraction loop
    for (int i = 0; i < rows; ++i) {
      for (int index = i * columns; index < i * (columns + 1) + 1; ++index) {
        data[index] -= l.data[index];
      }
    }

  }

  public double getDeterminant(double epsilon) {
    double determinant = data[0];
    for (int index = columns + 1; index < columns * columns; index += columns + 1) {
      determinant *= data[index];
    }
    return determinant;
  }

  public Matrix solve(Matrix b, double epsilon)
    throws SingularMatrixException {
    // validity check
    if (b.getRows () != rows) {
      throw new IllegalArgumentException("dimension mismatch");
    }

    // prepare the data storage
    int bRows  = b.getRows();
    int bCols  = b.getColumns();

    double[] resultData = new double[bRows * bCols];
    int resultIndex     = 0;
    int lowerElements   = 0;
    int upperElements   = 0;
    int minJ            = columns;
    int maxJ            = 0;

    // solve the linear system
    for (int i = 0; i < rows; ++i) {
      double diag = data[i * (columns + 1)];
      if (Math.abs(diag) < epsilon) {
        throw new SingularMatrixException();
      }
      double inv = 1.0 / diag;

      NonNullRange range = b.getRangeForRow(i);
      minJ = Math.min(minJ, range.begin);
      maxJ = Math.max(maxJ, range.end);

      int j = 0;
      while (j < minJ) {
        resultData[resultIndex] = 0.0;
        ++resultIndex;
        ++j;
      }

      // compute the possibly non null elements
      int bIndex = i * bCols + minJ;
      while (j < maxJ) {

        // compute the current element
        int index1 = i * columns;
        int index2 = j;
        double value = b.data[bIndex];
        while (index1 < i * (columns + 1)) {
          value -= data[index1] * resultData[index2];
          ++index1;
          index2 += bCols;
        }
        value *= inv;
        resultData[resultIndex] = value;

        // count the affected upper and lower elements
        // (in order to deduce the shape of the resulting matrix)
        if (j < i) {
          ++lowerElements;
        } else if (i < j) {
          ++upperElements;
        }

        ++bIndex;
        ++resultIndex;
        ++j;

      }

      while (j < bCols) {
        resultData[resultIndex] = 0.0;
        ++resultIndex;
        ++j;
      }

    }

    return MatrixFactory.buildMatrix(bRows, bCols, resultData,
                                     lowerElements, upperElements);

  }

  public NonNullRange getRangeForRow(int i) {
    return new NonNullRange(0, i + 1);
  }

  public NonNullRange getRangeForColumn(int j) {
    return new NonNullRange(j, rows);
  }

  private static final long serialVersionUID = 3592505328858227281L;

}
