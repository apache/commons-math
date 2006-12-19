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

public class LowerTriangularMatrixTest
  extends TestCase {

  public LowerTriangularMatrixTest(String name) {
    super(name);
  }

  public void testNoSetOutsideOfLowerTriangle() {

    LowerTriangularMatrix l = new LowerTriangularMatrix(4);

    for (int i = 0; i < l.getRows(); ++i) {
      for (int j = 0; j < l.getColumns(); ++j) {

        if (i >= j) {
          l.setElement(i, j, i + 0.1 * j);
        } else {
          boolean gotIt = false;
          try {
            l.setElement(i, j, -1.3);
          } catch(ArrayIndexOutOfBoundsException e) {
            gotIt = true;
          }
          assertTrue(gotIt);
        }
      }
    }

    checkMatrix(l, new BilinearPattern(1.0, 0.1));

  }

  public void testCopy() {

    LowerTriangularMatrix l1 = buildMatrix(4, new BilinearPattern(1.0, 0.01));

    LowerTriangularMatrix l2 = new LowerTriangularMatrix (l1);

    checkMatrix (l2, new BilinearPattern(1.0, 0.01));

  }

  public void testDuplicate() {

    LowerTriangularMatrix l1 = buildMatrix(4, new BilinearPattern(1.0, 0.01));

    Matrix l2 = l1.duplicate();
    assertTrue(l2 instanceof LowerTriangularMatrix);

    checkMatrix(l2, new BilinearPattern(1.0, 0.01));

  }

  public void testTranspose() {

    LowerTriangularMatrix l = buildMatrix(7, new BilinearPattern(1.0, 0.1));

    Matrix transposed = l.getTranspose();
    assertTrue(transposed instanceof UpperTriangularMatrix);

    for (int i = 0; i < transposed.getRows(); ++i){
      for (int j = 0; j < transposed.getColumns(); ++j) {
        double expected = (i > j) ? 0.0 : (j + 0.1 * i);
        assertTrue(Math.abs(transposed.getElement(i, j) - expected) < 1.0e-10);
      }
    }

  }

  public void testSelfAdd() {
    LowerTriangularMatrix l1 = buildMatrix(7, new BilinearPattern(3, -0.2));

    LowerTriangularMatrix l2 = buildMatrix(7, new BilinearPattern(2, -0.4));

    l1.selfAdd(l2);

    checkMatrix(l1, new BilinearPattern(5, -0.6));
  }

  public void testSelfSub() {
    LowerTriangularMatrix l1 = buildMatrix(7, new BilinearPattern(3, -0.2));

    LowerTriangularMatrix l2 = buildMatrix(7, new BilinearPattern(2, -0.4));

    l1.selfSub(l2);

    checkMatrix(l1, new BilinearPattern(1, 0.2));
  }

  public void testDeterminant() {

    LowerTriangularMatrix l = buildMatrix(4, new ElementPattern() {
        public double value(int i, int j) {
          return (i == j) ? 2.0 : 1.0;
        }
      });

    assertTrue(Math.abs(l.getDeterminant(1.0e-10) - Math.pow(2.0, l.getRows()))
               < 1.0e-10);

  }

  public void testSolve()
    throws SingularMatrixException {

    LowerTriangularMatrix l = buildMatrix(7, new ElementPattern() {
        public double value(int i, int j) {
          return 1.0;
        }
      });

    GeneralMatrix b = new GeneralMatrix(l.getRows(), 3);
    for (int i = 0; i < b.getRows(); ++i) {
      b.setElement(i, 0, i + 1.0);
      b.setElement(i, 1, (i + 1.0) * (i + 2.0) / 2.0);
      b.setElement(i, 2, 0.0);
    }

    Matrix result = l.solve(b, 1.0e-10);

    assertTrue(result.getRows() == b.getRows());
    assertTrue(result.getColumns() == b.getColumns());

    for (int i = 0; i < result.getRows(); ++i) {
      assertTrue(Math.abs(result.getElement(i, 0) - 1.0)       < 1.0e-10);
      assertTrue(Math.abs(result.getElement(i, 1) - (i + 1.0)) < 1.0e-10);
      assertTrue(Math.abs(result.getElement(i, 2) - 0.0)       < 1.0e-10);
    }

    boolean gotIt = false;
    try {
      l.setElement(3, 3, 0.0);
      l.solve(b, 1.0e-10);
    } catch(SingularMatrixException e) {
      gotIt = true;
    }
    assertTrue(gotIt);

  }

  public void testInverse()
    throws SingularMatrixException {

    LowerTriangularMatrix l = buildMatrix(5, new ElementPattern() {
        public double value(int i, int j) {
          return 1.0;
        }
      });

    Matrix inverse = l.getInverse(1.0e-10);
    assertTrue(inverse instanceof LowerTriangularMatrix);

    checkMatrix(inverse, new ElementPattern() {
        public double value(int i, int j) {
          return (i == j) ? 1.0 : ((i == j + 1) ? -1.0 : 0.0);
        }
      });

  }

  public static Test suite() {
    return new TestSuite(LowerTriangularMatrixTest.class);
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

  public LowerTriangularMatrix buildMatrix(int order,
                                           ElementPattern pattern) {
    LowerTriangularMatrix m = new LowerTriangularMatrix(order);

    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j <= i; ++j) {
        m.setElement(i, j, pattern.value(i, j));
      }
    }

    return m;

  }

  public void checkMatrix(Matrix m, ElementPattern pattern) {
    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j < m.getColumns(); ++j) {
        double expected = (j <= i) ? pattern.value(i, j) : 0.0;
        assertTrue(Math.abs(m.getElement(i, j) - expected) < 1.0e-10);
      }
    }
  }

}
