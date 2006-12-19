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

import junit.framework.*;

public class DiagonalMatrixTest
  extends TestCase {

  public DiagonalMatrixTest(String name) {
    super(name);
  }

  public void testConstantDiagonal() {
    checkMatrix(new DiagonalMatrix(5, 2.7), 2.7);
  }

  public void testNoSetOutsideOfDiagonal() {

    DiagonalMatrix d = new DiagonalMatrix(4);

    for (int i = 0; i < d.getRows(); ++i) {
      for (int j = 0; j < d.getColumns(); ++j) {
        if (i == j) {
          d.setElement(i, j, 2.7);
        } else {
          boolean gotIt = false;
          try {
            d.setElement(i, j, -1.3);
          } catch (ArrayIndexOutOfBoundsException e) {
            gotIt = true;
          }
          assertTrue(gotIt);
        }
      }
    }

    checkMatrix(d, 2.7);

  }

  public void testCopy() {
    DiagonalMatrix d1 = new DiagonalMatrix(7, 4.3);
    DiagonalMatrix d2 = new DiagonalMatrix(d1);

    for (int i = 0; i < d1.getRows(); ++i) {
      d1.setElement(i, i, -1.0);
    }

    assertTrue(d2.getRows() == d1.getRows());
    assertTrue(d2.getColumns() == d1.getColumns());

    checkMatrix(d2, 4.3);

  }

  public void testDuplicate() {
    DiagonalMatrix d1 = new DiagonalMatrix(6, -8.8);

    Matrix d2 = d1.duplicate();
    assertTrue(d2 instanceof DiagonalMatrix);

    for (int i = 0; i < d1.getRows(); ++i) {
      d1.setElement(i, i, -1.0);
    }

    assertTrue(d2.getRows() == d1.getRows());
    assertTrue(d2.getColumns() == d1.getColumns());

    checkMatrix(d2, -8.8);

  }

  public void testTranspose() {

    DiagonalMatrix d = new DiagonalMatrix(5, 3.4);

    Matrix transposed = d.getTranspose();
    assertTrue(transposed instanceof DiagonalMatrix);

    checkMatrix(transposed, 3.4);

  }

  public void testDeterminant() {

    double expected;

    expected = 1.0;
    for (int k = 1; k < 10; ++k) {
      expected *= 2;
      DiagonalMatrix d = new DiagonalMatrix(k, 2.0);
      assertTrue(Math.abs(d.getDeterminant(1.0e-10) - expected) < 1.0e-10);
    }

    expected = 1.0;
    for (int k = 1; k < 10; ++k) {
      expected *= k;
      DiagonalMatrix d = new DiagonalMatrix(k);
      for (int i = 0; i < k; ++i) {
        d.setElement(i, i, i + 1);
      }
      assertTrue(Math.abs(d.getDeterminant(1.0e-10) - expected) < 1.0e-10);
    }

  }

  public void testSolve()
    throws SingularMatrixException {

    DiagonalMatrix d = new DiagonalMatrix(6);
    for (int i = 0; i < d.getRows(); ++i) {
      d.setElement(i, i, i + 1.0);
    }

    GeneralMatrix b = new GeneralMatrix(6, 3);
    for (int i = 0; i < b.getRows(); ++i) {
      b.setElement(i, 0, i + 1.0);
      b.setElement(i, 1, (i + 1.0) * (i + 1.0));
      b.setElement(i, 2, 0.0);
    }

    Matrix result = d.solve(b, 1.0e-10);

    assertTrue(result.getRows() == b.getRows());
    assertTrue(result.getColumns() == b.getColumns());

    for (int i = 0; i < result.getRows(); ++i) {
      assertTrue(Math.abs(result.getElement(i, 0) - 1.0)       < 1.0e-10);
      assertTrue(Math.abs(result.getElement(i, 1) - (i + 1.0)) < 1.0e-10);
      assertTrue(Math.abs(result.getElement(i, 2) - 0.0)       < 1.0e-10);
    }

    boolean gotIt = false;
    try {
      d.setElement(3, 3, 0.0);
      d.solve(b, 1.0e-10);
    } catch (SingularMatrixException e) {
      gotIt = true;
    }
    assertTrue(gotIt);

  }

  public void testInverse()
    throws SingularMatrixException {

    DiagonalMatrix d = new DiagonalMatrix(4);
    for (int i = 0; i < d.getRows (); ++i) {
      d.setElement(i, i, i + 1.0);
    }

    Matrix inverse = d.getInverse(1.0e-10);
    assertTrue(inverse instanceof DiagonalMatrix);

    for (int i = 0; i < inverse.getRows(); ++i) {
      assertTrue(Math.abs(inverse.getElement(i, i) - 1.0 / (i + 1.0)) < 1.0e-10);
    }

  }

  public static Test suite() {
    return new TestSuite(DiagonalMatrixTest.class);
  }

  public void checkMatrix(Matrix d, double value) {
    for (int i = 0; i < d.getRows(); ++i) {
      for (int j = 0; j < d.getColumns(); ++j) {
        double expected = (i == j) ? value : 0.0;
        assertTrue(Math.abs(d.getElement(i, j) - expected) < 1.0e-10);
      }
    }
  }

}
