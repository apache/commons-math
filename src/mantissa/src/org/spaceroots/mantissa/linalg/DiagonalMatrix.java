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

/** This class implements diagonal matrices of linear algebra.

 * @version $Id$
 * @author L. Maisonobe

 */

public class DiagonalMatrix
  extends SquareMatrix {

  /** Simple constructor.
   * This constructor builds a diagonal matrix of specified order, all
   * elements on the diagonal being ones (so this is an identity matrix).
   * @param order order of the matrix
   */
  public DiagonalMatrix(int order) {
    this(order, 1.0);
  }

  /** Simple constructor.
   * This constructor builds a diagonal matrix of specified order and
   * set all diagonal elements to the same value.
   * @param order order of the matrix
   * @param value value for the diagonal elements
   */
  public DiagonalMatrix(int order, double value) {
    super(order);
    for (int index = 0; index < order * order; index += order + 1) {
      data[index] = value;
    }
  }

  /** Simple constructor.
   * Build a matrix with specified elements.
   * @param order order of the matrix
   * @param data table of the matrix elements (stored row after row)
   */
  public DiagonalMatrix(int order, double[] data) {
    super(order, data);
  }

  /** Copy constructor.
   * @param d diagonal matrix to copy
   */
  public DiagonalMatrix(DiagonalMatrix d) {
    super(d);
  }

  public Matrix duplicate() {
    return new DiagonalMatrix(this);
  }

  public void setElement(int i, int j, double value) {
    if (i != j) {
      throw new ArrayIndexOutOfBoundsException("cannot set elements"
                                               + " out of diagonal in a"
                                               + " diagonal matrix");
    }
    super.setElement(i, j, value);
  }

  public double getDeterminant(double epsilon) {
    double determinant = data[0];
    for (int index = columns + 1; index < columns * columns; index += columns + 1) {
      determinant *= data[index];
    }
    return determinant;
  }

  public SquareMatrix getInverse(double epsilon)
    throws SingularMatrixException {

    DiagonalMatrix inv = new DiagonalMatrix (columns);

    for (int index = 0; index < columns * columns; index += columns + 1) {
      if (Math.abs(data[index]) < epsilon) {
        throw new SingularMatrixException();
      }
      inv.data[index] = 1.0 / data[index];
    }

    return inv;

  }

  public Matrix solve(Matrix b, double epsilon)
    throws SingularMatrixException {

    Matrix result = b.duplicate();

    for (int i = 0; i < columns; ++i) {
      double diag = data[i * (columns + 1)];
      if (Math.abs(diag) < epsilon) {
        throw new SingularMatrixException();
      }
      double inv = 1.0 / diag;

      NonNullRange range = result.getRangeForRow(i);
      for (int index = i * b.columns + range.begin;
           index < i * b.columns + range.end;
           ++index) {
        result.data[index] = inv * b.data[index];
      }
    }

    return result;

  }

  public NonNullRange getRangeForRow(int i) {
    return new NonNullRange(i, i + 1);
  }

  public NonNullRange getRangeForColumn(int j) {
    return new NonNullRange(j, j + 1);
  }

  private static final long serialVersionUID = -2965166085913895323L;

}
