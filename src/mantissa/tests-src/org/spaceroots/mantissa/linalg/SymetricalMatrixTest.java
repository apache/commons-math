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

public class SymetricalMatrixTest
  extends TestCase {

  public SymetricalMatrixTest(String name) {
    super(name);
  }

  public void testBuildWAAt() {

    double[] a = { 1.0, 2.0, 3.0 };
    SymetricalMatrix s = new SymetricalMatrix(0.99, a);

    checkMatrix(s, new ElementPattern() {
        public double value(int i, int j) {
          return 0.99 * (i + 1) * (j + 1);
        }
      });

  }

  public void testNoSetOutsideOfDiagonal() {

    SymetricalMatrix s = new SymetricalMatrix(4);

    for (int i = 0; i < s.getRows(); ++i) {
      for (int j = 0; j < s.getColumns(); ++j) {
        if (i == j) {
          s.setElement(i, j, 0.5);
        } else {
          boolean gotIt = false;
          try {
            s.setElement
              (i, j, -1.3);
          } catch(ArrayIndexOutOfBoundsException e) {
            gotIt = true;
          }
          assertTrue(gotIt);
        }
      }
    }

    checkMatrix(s, new ElementPattern() {
        public double value(int i, int j) {
          return (i == j) ? 0.5 : 0.0;
        }
      });

  }

  public void testSetElementAndSymetricalElement() {
    SymetricalMatrix s = new SymetricalMatrix(5);
    s.setElementAndSymetricalElement(1, 2, 3.4);
    assertTrue(Math.abs(s.getElement(1, 2) - 3.4) < 1.0e-10);
    assertTrue(Math.abs(s.getElement(2, 1) - 3.4) < 1.0e-10);
  }

  public void testCopy() {
    SymetricalMatrix m1 = buildMatrix(5, new ElementPattern() {
        public double value(int i, int j) {
          return i * i + j * j;
        }
      });

    SymetricalMatrix m2 = new SymetricalMatrix(m1);

    for (int i = 0; i < m1.getRows(); ++i) {
      for (int j = i; j < m1.getColumns(); ++j) {
        m1.setElementAndSymetricalElement(i, j, -1.0);
      }
    }

    assertTrue(m2.getRows() == m1.getRows());
    assertTrue(m2.getColumns() == m1.getColumns());

    checkMatrix(m2, new ElementPattern() {
        public double value(int i, int j) {
          return i * i + j * j;
        }
      });

  }

  public void testDuplicate() {
    SymetricalMatrix m1 = buildMatrix(5, new ElementPattern() {
        public double value(int i, int j) {
          return i * j;
        }
      });

    Matrix m2 = m1.duplicate();
    assertTrue(m2 instanceof SymetricalMatrix);

    for (int i = 0; i < m1.getRows(); ++i) {
      for (int j = i; j < m1.getColumns(); ++j) {
        m1.setElementAndSymetricalElement(i, j, -1.0);
      }
    }

    assertTrue(m2.getRows() == m1.getRows());
    assertTrue(m2.getColumns() == m1.getColumns());

    checkMatrix(m2, new ElementPattern() {
        public double value(int i, int j) {
          return i * j;
        }
      });

  }

  public void testSelfAdd() {
    double[] a1 = { 2.0, 4.0, 8.0, 16.0 };
    SymetricalMatrix s1 = new SymetricalMatrix(0.5, a1);

    double[] a2 = { 3.0, 9.0, 27.0, 81.0 };
    SymetricalMatrix s2 = new SymetricalMatrix(1.0, a2);

    s1.selfAdd(s2);

    checkMatrix(s1, new ElementPattern() {
        public double value(int i, int j) {
          return 0.5 * Math.pow(2.0, i + 1) * Math.pow(2.0, j + 1)
            + Math.pow(3.0, i + 1) * Math.pow(3.0, j + 1);
        }
      });
  }

  public void testSelfSub() {
    double[] a1 = { 2.0, 4.0, 8.0, 16.0 };
    SymetricalMatrix s1 = new SymetricalMatrix(0.5, a1);

    double[] a2 = { 3.0, 9.0, 27.0, 81.0 };
    SymetricalMatrix s2 = new SymetricalMatrix(1.0, a2);

    s1.selfSub(s2);

    checkMatrix(s1, new ElementPattern() {
        public double value(int i, int j) {
          return 0.5 * Math.pow(2.0, i + 1) * Math.pow(2.0, j + 1)
            - Math.pow(3.0, i + 1) * Math.pow(3.0, j + 1);
        }
      });
  }

  public void testSelfAddWAAt() {

    SymetricalMatrix s = new SymetricalMatrix(3);

    double[] a1 = { 1.0, 2.0, 3.0 };
    s.selfAddWAAt(1.0, a1);

    double[] a2 = { 0.1, 0.2, 0.3 };
    s.selfAddWAAt(2.0, a2);

    checkMatrix(s, new ElementPattern() {
        public double value(int i, int j) {
          return 1.02 * (i + 1) * (j + 1);
        }
      });

  }

  public void testSingular()
    throws SingularMatrixException {
    SymetricalMatrix s = new SymetricalMatrix(3);

    double[] a1 = { 1.0, 2.0, 3.0 };
    s.selfAddWAAt(1.0, a1);

    double[] a2 = { 0.1, 0.2, 0.3 };
    s.selfAddWAAt(2.0, a2);

    Matrix b = new GeneralMatrix(3, 1);
    b.setElement(0, 0,  6.12);
    b.setElement(1, 0, 12.24);
    b.setElement(2, 0, 18.36);

    boolean gotIt = false;
    try {
      s.solve(b, 1.0e-10);
    } catch(SingularMatrixException e) {
      gotIt = true;
    }
    assertTrue(gotIt);

  }

  public void testSolve()
    throws SingularMatrixException {
    SymetricalMatrix s = new SymetricalMatrix(3);

    double[] a1 = { 1.0, 2.0, 3.0 };
    s.selfAddWAAt(1.0, a1);

    double[] a2 = { 0.1, 0.2, 0.3 };
    s.selfAddWAAt(2.0, a2);

    double[] a3 = { 1.2, -3.0, 2.1 };
    s.selfAddWAAt(3.0, a3);

    double[] a4 = { 0.4, 0.1, 3.1 };
    s.selfAddWAAt(2.0, a4);

    Matrix b = new GeneralMatrix(3, 1);
    b.setElement(0, 0, 10.08);
    b.setElement(1, 0, 10.26);
    b.setElement(2, 0, 42.57);

    Matrix x = s.solve(b, 1.0e-10);

    checkMatrix (x, new ElementPattern() {
        public double value(int i, int j) {
          return 1.0;
        }
      });

    assertTrue(Math.abs(s.getDeterminant(1.0e-10) - 782.846532) < 1.0e-10);

  }

  public static Test suite() {
    return new TestSuite(SymetricalMatrixTest.class);
  }

  public interface ElementPattern {
    public double value(int i, int j);
  }

  public SymetricalMatrix buildMatrix(int order,
                                      ElementPattern pattern) {
    SymetricalMatrix m = new SymetricalMatrix(order);

    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = i; j < m.getColumns(); ++j) {
        m.setElementAndSymetricalElement(i, j, pattern.value(i, j));
      }
    }

    return m;

  }

  public void checkMatrix(Matrix m, ElementPattern pattern) {
    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j < m.getColumns(); ++j) {
        assertTrue(Math.abs(m.getElement(i, j) - pattern.value(i, j))
                   < 1.0e-10);
      }
    }
  }

}
