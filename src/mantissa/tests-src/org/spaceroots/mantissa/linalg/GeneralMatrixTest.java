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

public class GeneralMatrixTest
  extends TestCase {

  public GeneralMatrixTest(String name) {
    super(name);
  }

  public void testDimensions() {
    GeneralMatrix m = new GeneralMatrix(3, 4);
    assertTrue(m.getRows() == 3);
    assertTrue(m.getColumns() == 4);
  }

  public void testInvalidDimensions() {
    boolean gotIt;

    gotIt = false;
    try {
      new GeneralMatrix(0, 2);
    } catch(IllegalArgumentException e) {
      gotIt = true;
    }
    assertTrue(gotIt);

    gotIt = false;
    try {
      new GeneralMatrix(1, -3, null);
    } catch(IllegalArgumentException e) {
      gotIt = true;
    }
    assertTrue(gotIt);

  }

  public void testElements() {
    Matrix m = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    checkMatrix(m, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

  }

  public void testCopy() {
    Matrix m1 = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    GeneralMatrix m2 = new GeneralMatrix(m1);

    for (int i = 0; i < m1.getRows(); ++i) {
      for (int j = 0; j < m1.getColumns(); ++j) {
        m1.setElement(i, j, -1.0);
      }
    }

    assertTrue(m2.getRows() == m1.getRows());
    assertTrue(m2.getColumns() == m1.getColumns());

    checkMatrix(m2, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

  }

  public void testDuplicate() {
    Matrix m1 = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    Matrix m2 = m1.duplicate();
    assertTrue(m2 instanceof GeneralMatrix);

    for (int i = 0; i < m1.getRows(); ++i) {
      for (int j = 0; j < m1.getColumns(); ++j) {
        m1.setElement(i, j, -1.0);
      }
    }

    assertTrue(m2.getRows() == m1.getRows());
    assertTrue(m2.getColumns() == m1.getColumns());

    checkMatrix (m2, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

  }

  public void testAddKO() {
    boolean gotIt = false;
    try {
      new GeneralMatrix(2, 3).add(new GeneralMatrix(3, 2));
    } catch(IllegalArgumentException e) {
      gotIt = true;
    }
    assertTrue(gotIt);
  }

  public void testAddOK() {

    Matrix m1 = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    Matrix m2 = buildMatrix(m1.getRows(),
                            m1.getColumns(),
                            new ElementPattern() {
                              public double value(int i, int j) {
                                return 100 * i - 0.01 * j;
                              }
                            });

    Matrix m3 = m1.add(m2);

    checkMatrix(m3, new ElementPattern() {
        public double value(int i, int j) {
          return 101 * i;
        }
      });

  }

  public void testSelfAdd() {

    GeneralMatrix m1 = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    Matrix m2 = buildMatrix(m1.getRows(),
                            m1.getColumns(),
                            new ElementPattern() {
                              public double value(int i, int j) {
                                return 100 * i - 0.01 * j;
                              }
                            });

    m1.selfAdd(m2);

    checkMatrix(m1, new ElementPattern() {
        public double value(int i, int j) {
          return 101 * i;
        }
      });

  }

  public void testSubKO() {
    boolean gotIt = false;
    try {
      new GeneralMatrix(2, 3).sub(new GeneralMatrix(3, 2));
    } catch(IllegalArgumentException e) {
      gotIt = true;
    }
    assertTrue(gotIt);
  }

  public void testSubOK() {

    Matrix m1 = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    Matrix m2 = buildMatrix(m1.getRows(),
                            m1.getColumns(),
                            new ElementPattern() {
                              public double value(int i, int j) {
                                return 100 * i - 0.01 * j;
                              }
                            });

    Matrix m3 = m1.sub(m2);

    checkMatrix(m3, new ElementPattern() {
        public double value(int i, int j) {
          return 0.02 * j - 99 * i;
        }
      });

  }

  public void testSelfSub() {

    GeneralMatrix m1 = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    Matrix m2 = buildMatrix(m1.getRows(),
                            m1.getColumns(),
                            new ElementPattern() {
                              public double value(int i, int j) {
                                return 100 * i - 0.01 * j;
                              }
                            });

    m1.selfSub(m2);

    checkMatrix(m1, new ElementPattern() {
        public double value(int i, int j) {
          return 0.02 * j - 99 * i;
        }
      });

  }

  public void testMulMKO() {
    boolean gotIt = false;
    try {
      new GeneralMatrix(2, 3).mul(new GeneralMatrix(2, 3));
    } catch(IllegalArgumentException e) {
      gotIt = true;
    }
    assertTrue(gotIt);
  }

  public void testMulMOK() {

    Matrix m1 = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    Matrix m2 = buildMatrix(m1.getColumns(), 4, new ElementPattern() {
        public double value(int i, int j) {
          return 2 * i - j;
        }
      });

    Matrix m3 = m1.mul(m2);

    checkMatrix(m3, new ElementPattern() {
        public double value(int i, int j) {
          int p = 10; // must be equal to m1.getColumns()
          return p * ((2 * i - 0.01 *j) * (p - 1) / 2.0
                      - i* j
                      + (p - 1) * (2 * p - 1) / 300.0);
        }
      });

  }

  public void testMulD() {

    Matrix m1 = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    Matrix m2 = m1.mul(2.5);

    checkMatrix(m2, new ElementPattern() {
        public double value(int i, int j) {
          return 2.5 * (i + 0.01 * j);
        }
      });

  }

  public void testSelfMul() {

    Matrix m = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    m.selfMul(2.5);

    checkMatrix(m, new ElementPattern() {
        public double value(int i, int j) {
          return 2.5 * (i + 0.01 * j);
        }
      });

  }

  public void testTranspose() {

    Matrix m1 = buildMatrix(5, 10, new ElementPattern() {
        public double value(int i, int j) {
          return i + 0.01 * j;
        }
      });

    Matrix m2 = m1.getTranspose();

    assertTrue(m1.getRows() == m2.getColumns());
    assertTrue(m1.getColumns() == m2.getRows());

    checkMatrix(m2, new ElementPattern() {
        public double value(int i, int j) {
          return 0.01 * i + j;
        }
      });

  }

  public static Test suite() {
    return new TestSuite(GeneralMatrixTest.class);
  }

  public interface ElementPattern {
    public double value(int i, int j);
  }

  public GeneralMatrix buildMatrix(int rows, int columns,
                                   ElementPattern pattern) {
    GeneralMatrix m = new GeneralMatrix(rows, columns);

    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j < m.getColumns(); ++j){
        m.setElement(i, j, pattern.value(i, j));
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
