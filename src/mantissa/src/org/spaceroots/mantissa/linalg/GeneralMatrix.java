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

/** This class represents matrices of the most general type.

 * <p>This class is the basic implementation of matrices to use when
 * nothing special is known about the structure of the matrix.</p>

 * @version $Id$
 * @author L. Maisonobe

 */

public class GeneralMatrix
  extends Matrix {

  /** Simple constructor.
   * Build a matrix with null elements.
   * @param rows number of rows of the matrix
   * @param columns number of columns of the matrix
   */
  public GeneralMatrix(int rows, int columns) {
    super(rows, columns);
  }

  /** Simple constructor.
   * Build a matrix with specified elements.
   * @param rows number of rows of the matrix
   * @param columns number of columns of the matrix
   * @param data table of the matrix elements (stored row after row)
   */
  public GeneralMatrix(int rows, int columns, double[] data) {
    super(rows, columns, data);
  }

  /** Copy constructor.
   * @param m matrix to copy
   */
  public GeneralMatrix(Matrix m) {
    super(m);
  }

  public Matrix duplicate() {
    return new GeneralMatrix(this);
  }

  /** Add a matrix to the instance.
   * This method adds a matrix to the instance. It does modify the instance.
   * @param m matrix to add
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfAdd(Matrix m) {

    // validity check
    if ((rows != m.rows) || (columns != m.columns)) {
      throw new IllegalArgumentException("cannot add a "
                                         + m.rows + 'x' + m.columns
                                         + " matrix to a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }

    // addition loop
    for (int index = 0; index < rows * columns; ++index) {
      data[index] += m.data[index];
    }

  }

  /** Substract a matrix from the instance.
   * This method substracts a matrix from the instance. It does modify the instance.
   * @param m matrix to substract
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfSub(Matrix m) {

    // validity check
    if ((rows != m.rows) || (columns != m.columns)) {
      throw new IllegalArgumentException("cannot substract a "
                                         + m.rows + 'x' + m.columns
                                         + " matrix from a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }

    // substraction loop
    for (int index = 0; index < rows * columns; ++index) {
      data[index] -= m.data[index];
    }

  }

  protected NonNullRange getRangeForRow(int i) {
    return new NonNullRange(0, columns);
  }

  protected NonNullRange getRangeForColumn(int j) {
    return new NonNullRange(0, rows);
  }

  private static final long serialVersionUID = 4350328622456299819L;

}
