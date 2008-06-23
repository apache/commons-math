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

/** This class factor all services common to square matrices of linear algebra.

 * <p>This class is the base class of all square matrix
 * implementations. It extends the {@link Matrix} class with methods
 * specific to square matrices.</p>

 * @version $Id$
 * @author L. Maisonobe

 */

public abstract class SquareMatrix
  extends Matrix {
  /** Simple constructor.
   * Build a matrix with null elements.
   * @param order order of the matrix
   */
  protected SquareMatrix(int order) {
    super(order, order);
  }

  /** Simple constructor.
   * Build a matrix with specified elements.
   * @param order order of the matrix
   * @param data table of the matrix elements (stored row after row)
   */
  protected SquareMatrix(int order, double[] data) {
    super(order, order, data);
  }

  /** Copy constructor.
   * @param m matrix to copy
   */
  protected SquareMatrix(SquareMatrix m) {
    super(m);
  }

  /** Get the determinant of the matrix.
   * @param epsilon threshold on matrix elements below which the
   * matrix is considered singular (this is used by the derived
   * classes that use a factorization to compute the determinant)
   * @return the determinant of the matrix
   */
  public abstract double getDeterminant(double epsilon);

  /** Invert the instance.
   * @param epsilon threshold on matrix elements below which the
   * matrix is considered singular
   * @return the inverse matrix of the instance
   * @exception SingularMatrixException if the matrix is singular
   */
  public SquareMatrix getInverse(double epsilon)
    throws SingularMatrixException {
    return solve(new DiagonalMatrix (columns), epsilon);
  }


  /** Solve the <tt>A.X = B</tt> equation.
   * @param b second term of the equation
   * @param epsilon threshold on matrix elements below which the
   * matrix is considered singular
   * @return a matrix X such that <tt>A.X = B</tt>, where A is the instance
   * @exception SingularMatrixException if the matrix is singular
   */
  public abstract Matrix solve(Matrix b, double epsilon)
    throws SingularMatrixException;

  /** Solve the <tt>A.X = B</tt> equation.
   * @param b second term of the equation
   * @param epsilon threshold on matrix elements below which the
   * matrix is considered singular
   * @return a matrix X such that <tt>A.X = B</tt>, where A is the instance
   * @exception SingularMatrixException if the matrix is singular
   */
  public SquareMatrix solve(SquareMatrix b, double epsilon)
    throws SingularMatrixException {
    return (SquareMatrix) solve((Matrix) b, epsilon);
  }

}
