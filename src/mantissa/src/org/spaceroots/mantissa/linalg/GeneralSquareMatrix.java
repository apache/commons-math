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

/** This class implements general square matrices of linear algebra.

 * @version $Id$
 * @author L. Maisonobe

 */

public class GeneralSquareMatrix
  extends SquareMatrix {

  /** Simple constructor.
   * This constructor builds a square matrix of specified order, all
   * elements beeing zeros.
   * @param order order of the matrix
   */
  public GeneralSquareMatrix(int order) {
    super(order);
    permutations     = null;
    evenPermutations = true;
    lower            = null;
    upper            = null;
  }

  /** Simple constructor.
   * Build a matrix with specified elements.
   * @param order order of the matrix
   * @param data table of the matrix elements (stored row after row)
   */
  public GeneralSquareMatrix(int order, double[] data) {
    super(order, data);
    permutations     = null;
    evenPermutations = true;
    lower            = null;
    upper            = null;
  }

  /** Copy constructor.
   * @param s square matrix to copy
   */
  public GeneralSquareMatrix(GeneralSquareMatrix s) {
    super(s);

    if (s.permutations != null) {
      permutations     = (int[]) s.permutations.clone();
      evenPermutations = s.evenPermutations;
      lower            = new LowerTriangularMatrix(s.lower);
      upper            = new UpperTriangularMatrix(s.upper);
    } else {
      permutations     = null;
      evenPermutations = true;
      lower            = null;
      upper            = null;
    }

  }

  public Matrix duplicate() {
    return new GeneralSquareMatrix(this);
  }

  public void setElement(int i, int j, double value) {
    super.setElement(i, j, value);
    permutations     = null;
    evenPermutations = true;
    lower            = null;
    upper            = null;
  }

  /** Add a matrix to the instance.
   * This method adds a matrix to the instance. It does modify the instance.
   * @param s square matrix to add
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfAdd(SquareMatrix s) {

    // validity check
    if ((rows != s.rows) || (columns != s.columns)) {
      throw new IllegalArgumentException("cannot add a "
                                         + s.rows + 'x' + s.columns
                                         + " matrix to a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }

    // addition loop
    for (int index = 0; index < rows * columns; ++index) {
      data[index] += s.data[index];
    }

  }

  /** Substract a matrix from the instance.
   * This method substracts a matrix from the instance. It does modify the instance.
   * @param s square matrix to substract
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfSub(SquareMatrix s) {

    // validity check
    if ((rows != s.rows) || (columns != s.columns)) {
      throw new IllegalArgumentException("cannot substract a "
                                         + s.rows + 'x' + s.columns
                                         + " matrix from a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }
      
    // substraction loop
    for (int index = 0; index < rows * columns; ++index) {
      data[index] -= s.data[index];
    }

  }

  public double getDeterminant(double epsilon) {
    try {
      if (permutations == null)
        computeLUFactorization(epsilon);
      double d = upper.getDeterminant(epsilon);
      return evenPermutations ? d : -d;
    } catch (SingularMatrixException e) {
      return 0.0;
    }
  }

  public Matrix solve(Matrix b, double epsilon)
    throws SingularMatrixException {
    // validity check
    if (b.getRows() != rows) {
      throw new IllegalArgumentException("dimension mismatch");
    }

    if (permutations == null) {
      computeLUFactorization(epsilon);
    }

    // apply the permutations to the second member
    double[] permData = new double[b.data.length];
    int bCols = b.getColumns();
    for (int i = 0; i < rows; ++i) {
      NonNullRange range = b.getRangeForRow(permutations[i]);
      for (int j = range.begin; j < range.end; ++j) {
        permData[i * bCols + j] = b.data[permutations[i] * bCols + j];
      }
    }
    Matrix permB = MatrixFactory.buildMatrix(b.getRows(), bCols, permData);

    // solve the permuted system
    return upper.solve(lower.solve(permB, epsilon), epsilon);

  }

  protected NonNullRange getRangeForRow(int i) {
    return new NonNullRange(0, columns);
  }

  protected NonNullRange getRangeForColumn(int j) {
    return new NonNullRange(0, rows);
  }

  private void computeLUFactorization(double epsilon)
    throws SingularMatrixException {
    // build a working copy of the matrix data
    double[] work = new double[rows * columns];
    for (int index = 0; index < work.length; ++index) {
      work[index] = data[index];
    }

    // initialize the permutations table to identity
    permutations = new int[rows];
    for (int i = 0; i < rows; ++i) {
      permutations[i] = i;
    }
    evenPermutations = true;

    for (int k = 0; k < rows; ++k) {

      // find the maximal element in the column
      double maxElt = Math.abs(work[permutations[k] * columns + k]);
      int    jMax   = k;
      for (int i = k + 1; i < rows; ++i) {
        double curElt = Math.abs(work[permutations[i] * columns + k]);
        if (curElt > maxElt) {
          maxElt = curElt;
          jMax   = i;
        }
      }

      if (maxElt < epsilon) {
        throw new SingularMatrixException();
      }

      if (k != jMax) {
        // do the permutation to have a large enough diagonal element
        int tmp            = permutations[k];
        permutations[k]    = permutations[jMax];
        permutations[jMax] = tmp;
        evenPermutations   = ! evenPermutations;
      }

      double inv = 1.0 / work[permutations[k] * columns + k];

      // compute the contribution of the row to the triangular matrices
      for (int i = k + 1; i < rows; ++i) {
        double factor = inv * work[permutations[i] * columns + k];

        // lower triangular matrix
        work[permutations[i] * columns + k] = factor;

        // upper triangular matrix
        int index1 = permutations[i] * columns + k;
        int index2 = permutations[k] * columns + k;
        for (int j = k + 1; j < columns; ++j) {
          work[++index1] -= factor * work[++index2];
        }
      }
    }

    // build the matrices
    double[] lowerData = new double[rows * columns];
    double[] upperData = new double[rows * columns];

    int index = 0;
    for (int i = 0; i < rows; ++i) {
      int workIndex = permutations[i] * columns;
      int j         = 0;

      // lower part
      while (j++ < i) {
        lowerData[index]   = work[workIndex++];
        upperData[index++] = 0.0;
      }

      // diagonal
      lowerData[index]   = 1.0;
      upperData[index++] = work[workIndex++];

      // upper part
      while (j++ < columns) {
        lowerData[index]   = 0.0;
        upperData[index++] = work[workIndex++];
      }
    }

    lower = new LowerTriangularMatrix(rows, lowerData);
    upper = new UpperTriangularMatrix(rows, upperData);

  }

  private int[]                 permutations;
  private boolean               evenPermutations;
  private LowerTriangularMatrix lower;
  private UpperTriangularMatrix upper;

  private static final long serialVersionUID = -506293526695298279L;

}
