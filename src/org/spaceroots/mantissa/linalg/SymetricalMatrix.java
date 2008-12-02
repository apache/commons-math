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

/** This class implements symetrical matrices of linear algebra.

 * @version $Id$
 * @author L. Maisonobe

 */

public class SymetricalMatrix
  extends GeneralSquareMatrix {

  /** Simple constructor.
   * This constructor builds a symetrical matrix of specified order, all
   * elements beeing zeros.
   * @param order order of the matrix
   */
  public SymetricalMatrix(int order) {
    super(order);
  }

  /** Simple constructor.
   * Build a matrix with specified elements.
   * @param order order of the matrix
   * @param data table of the matrix elements (stored row after row)
   */
  public SymetricalMatrix(int order, double[] data) {
    super(order, data);
  }

  /** Copy constructor.
   * @param s square matrix to copy
   */
  public SymetricalMatrix(SymetricalMatrix s) {
    super(s);
  }

  /** Build the symetrical matrix resulting from the product w.A.At.
   * @param w multiplicative factor (weight)
   * @param a base vector used to compute the symetrical contribution
   */
  public SymetricalMatrix(double w, double[] a) {
    super(a.length, new double[a.length * a.length]);

    for (int i = 0; i < a.length; ++i) {
      int indexU = i * (columns + 1);
      int indexL = indexU;

      double factor = w * a[i];
      data[indexU] = factor * a[i];

      for (int j = i + 1; j < columns; ++j) {
        ++indexU;
        indexL += columns;
        data[indexU] = factor * a[j];
        data[indexL] = data[indexU];
      }
    }

  }

  public Matrix duplicate() {
    return new SymetricalMatrix(this);
  }

  /** Set a matrix element.
   * On symetrical matrices, setting separately elements outside of
   * the diagonal is forbidden, so this method throws an
   * ArrayIndexOutOfBoundsException in this case. The {@link
   * #setElementAndSymetricalElement} can be used to set both elements
   * simultaneously.
   * @param i row index, from 0 to rows - 1
   * @param j column index, from 0 to cols - 1
   * @param value value of the element
   * @exception ArrayIndexOutOfBoundsException if the indices are wrong
   * @see #setElementAndSymetricalElement
   * @see Matrix#getElement
   */
  public void setElement(int i, int j, double value) {
    if (i != j) {
      throw new ArrayIndexOutOfBoundsException("cannot separately set"
                                               + " elements out of diagonal"
                                               + " in a symetrical matrix");
    }
    super.setElement(i, j, value);
  }

  /** Set both a matrix element and its symetrical element.
   * @param i row index of first element (column index of second
   * element), from 0 to order - 1
   * @param j column index of first element (row index of second
   * element), from 0 to order - 1
   * @param value value of the elements
   * @exception ArrayIndexOutOfBoundsException if the indices are wrong
   * @see #setElement
   * @see Matrix#getElement
   */
  public void setElementAndSymetricalElement(int i, int j, double value) {
    super.setElement(i, j, value);
    if (i != j) {
      super.setElement(j, i, value);
    }
  }

  /** Add a matrix to the instance.
   * This method adds a matrix to the instance. It does modify the instance.
   * @param s symetrical matrix to add
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfAdd(SymetricalMatrix s) {

    // validity check
    if ((rows != s.rows) || (columns != s.columns)) {
      throw new IllegalArgumentException("cannot add a "
                                         + s.rows + 'x' + s.columns
                                         + " matrix to a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }

    // addition loop
    for (int i = 0; i < rows; ++i) {
      int indexU = i * (columns + 1);
      int indexL = indexU;

      data[indexU] += s.data[indexU];

      for (int j = i + 1; j < columns; ++j) {
        ++indexU;
        indexL += columns;
        data[indexU] += s.data[indexU];
        data[indexL]  = data[indexU];
      }
    }

  }

  /** Substract a matrix from the instance.
   * This method substracts a matrix from the instance. It does modify the instance.
   * @param s symetrical matrix to substract
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfSub(SymetricalMatrix s) {

    // validity check
    if ((rows != s.rows) || (columns != s.columns)) {
      throw new IllegalArgumentException("cannot substract a "
                                         + s.rows + 'x' + s.columns
                                         + " matrix from a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }

    // substraction loop
    for (int i = 0; i < rows; ++i) {
      int indexU = i * (columns + 1);
      int indexL = indexU;

      data[indexU] -= s.data[indexU];

      for (int j = i + 1; j < columns; ++j) {
        ++indexU;
        indexL += columns;
        data[indexU] -= s.data[indexU];
        data[indexL] = data[indexU];
      }
    }

  }

  /** Add the symetrical matrix resulting from the product w.A.At to the instance.
   * This method can be used to build progressively the matrices of
   * least square problems. The instance is modified.
   * @param w multiplicative factor (weight)
   * @param a base vector used to compute the symetrical contribution
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfAddWAAt(double w, double[] a) {
    if (rows != a.length) {
      throw new IllegalArgumentException("cannot add a "
                                         + a.length + 'x' + a.length
                                         + " matrix to a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }

    for (int i = 0; i < rows; ++i) {
      int indexU = i * (columns + 1);
      int indexL = indexU;

      double factor   = w * a[i];
      data[indexU] += factor * a[i];

      for (int j = i + 1; j < columns; ++j) {
        ++indexU;
        indexL += columns;
        data[indexU] += factor * a[j];
        data[indexL]  = data[indexU];
      }
    }

  }

  private static final long serialVersionUID = -2083829252075519221L;

}
