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

public class UpperTriangularMatrixTest
  extends TestCase {

  public UpperTriangularMatrixTest(String name) {
    super(name);
  }

  public void testNoSetOutsideOfUpperTriangle() {

    UpperTriangularMatrix u = new UpperTriangularMatrix(4);

    for (int i = 0; i < u.getRows(); ++i) {
      for (int j = 0; j < u.getColumns(); ++j) {
        if (i <= j) {
          u.setElement(i, j, i + 0.1 * j);
        } else {
          boolean gotIt = false;
          try {
            u.setElement(i, j, -1.3);
          } catch(ArrayIndexOutOfBoundsException e) {
            gotIt = true;
          }
          assertTrue(gotIt);
        }
      }
    }

    checkMatrix(u, new BilinearPattern(1.0, 0.1));

  }

  public void testCopy() {

    UpperTriangularMatrix u1 = buildMatrix(4, new BilinearPattern(1.0, 0.1));

    UpperTriangularMatrix u2 = new UpperTriangularMatrix(u1);

    checkMatrix(u2, new BilinearPattern(1.0, 0.1));

  }

  public void testDuplicate() {

    UpperTriangularMatrix u1 = buildMatrix(4, new BilinearPattern(1.0, 0.1));

    Matrix u2 = u1.duplicate();
    assertTrue(u2 instanceof UpperTriangularMatrix);

    checkMatrix(u2, new BilinearPattern(1.0, 0.1));

  }

  public void testTranspose() {

    UpperTriangularMatrix u = buildMatrix(7, new BilinearPattern(1.0, 0.1));

    Matrix transposed = u.getTranspose();
    assertTrue(transposed instanceof LowerTriangularMatrix);

    for (int i = 0; i < transposed.getRows(); ++i){
      for (int j = 0; j < transposed.getColumns(); ++j) {
        double expected = (i < j) ? 0.0 : (j + 0.1 * i);
        assertTrue(Math.abs(transposed.getElement(i, j) - expected) < 1.0e-10);
      }
    }

  }

  public void testSelfAdd() {
    UpperTriangularMatrix u1 = buildMatrix(7, new BilinearPattern(3, -0.2));

    UpperTriangularMatrix u2 = buildMatrix(7, new BilinearPattern(2, -0.4));

    u1.selfAdd(u2);

    checkMatrix(u1, new BilinearPattern(5, -0.6));
  }

  public void testSelfSub() {
    UpperTriangularMatrix u1 = buildMatrix(7, new BilinearPattern(3, -0.2));

    UpperTriangularMatrix u2 = buildMatrix(7, new BilinearPattern(2, -0.4));

    u1.selfSub(u2);

    checkMatrix(u1, new BilinearPattern(1, 0.2));
  }

  public void testDeterminant() {

    UpperTriangularMatrix u = buildMatrix(4, new ElementPattern() {
        public double value(int i, int j) {
          return (i == j) ? 2.0 : 1.0;
        }
      });

    assertTrue(Math.abs(u.getDeterminant(1.0e-10) - Math.pow(2.0, u.getRows()))
               < 1.0e-10);

  }

  public void testSolve()
    throws SingularMatrixException {

    int rows = 7;
    UpperTriangularMatrix u = buildMatrix(rows, new ElementPattern() {
        public double value(int i, int j) {
          return 1.0;
        }
      });

    GeneralMatrix b = new GeneralMatrix(rows, 3);
    for (int i = 0; i < rows; ++i) {
      b.setElement(i, 0, rows - i);
      b.setElement(i, 1, (rows - i) * (rows + 1 - i) / 2.0);
      b.setElement(i, 2, 0.0);
    }

    Matrix result = u.solve(b, 1.0e-10);

    assertTrue(result.getRows() == b.getRows());
    assertTrue(result.getColumns() == b.getColumns());

    for (int i = 0; i < result.getRows(); ++i) {
      assertTrue(Math.abs(result.getElement(i, 0) - 1.0)        < 1.0e-10);
      assertTrue(Math.abs(result.getElement(i, 1) - (rows - i)) < 1.0e-10);
      assertTrue(Math.abs(result.getElement(i, 2) - 0.0)        < 1.0e-10);
    }

    boolean gotIt = false;
    try {
      u.setElement(3, 3, 0.0);
      u.solve(b, 1.0e-10);
    } catch(SingularMatrixException e) {
      gotIt = true;
    }
    assertTrue(gotIt);

  }

  public void testInverse()
    throws SingularMatrixException {

    UpperTriangularMatrix u = buildMatrix(5, new ElementPattern() {
        public double value(int i, int j) {
          return 1.0;
        }
      });

    Matrix inverse = u.getInverse(1.0e-10);
    assertTrue(inverse instanceof UpperTriangularMatrix);

    checkMatrix(inverse, new ElementPattern() {
        public double value(int i, int j) {
          return (i == j) ? 1.0 : ((i == j - 1) ? -1.0 : 0.0);
        }
      });

  }

  public static Test suite() {
    return new TestSuite(UpperTriangularMatrixTest.class);
  }

  public interface ElementPattern {
    public double value(int i, int j);
  }

  private static class BilinearPattern implements ElementPattern {
    public BilinearPattern(double coeffI, double coeffJ) {
      this.coeffI = coeffI;
      this.coeffJ = coeffJ;
    }
    public double value(int i, int j) {
      return coeffI * i + coeffJ * j;
    }
    private final double coeffI;
    private final double coeffJ;
  }

  public UpperTriangularMatrix buildMatrix(int order,
                                           ElementPattern pattern) {
    UpperTriangularMatrix m = new UpperTriangularMatrix (order);

    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = i; j < m.getColumns(); ++j) {
        m.setElement(i, j, pattern.value(i, j));
      }
    }

    return m;

  }

  public void checkMatrix(Matrix m, ElementPattern pattern) {
    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j < m.getColumns(); ++j) {
        double expected = (i <= j) ? pattern.value(i, j) : 0.0;
        assertTrue(Math.abs(m.getElement(i, j) - expected) < 1.0e-10);
      }
    }
  }

}
