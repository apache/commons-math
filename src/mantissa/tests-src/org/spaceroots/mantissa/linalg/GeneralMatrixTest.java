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
    Matrix m = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    checkMatrix(m, new BilinearPattern(1.0, 0.01));

  }

  public void testCopy() {
    Matrix m1 = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    GeneralMatrix m2 = new GeneralMatrix(m1);

    for (int i = 0; i < m1.getRows(); ++i) {
      for (int j = 0; j < m1.getColumns(); ++j) {
        m1.setElement(i, j, -1.0);
      }
    }

    assertTrue(m2.getRows() == m1.getRows());
    assertTrue(m2.getColumns() == m1.getColumns());

    checkMatrix(m2, new BilinearPattern(1.0, 0.01));

  }

  public void testDuplicate() {
    Matrix m1 = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    Matrix m2 = m1.duplicate();
    assertTrue(m2 instanceof GeneralMatrix);

    for (int i = 0; i < m1.getRows(); ++i) {
      for (int j = 0; j < m1.getColumns(); ++j) {
        m1.setElement(i, j, -1.0);
      }
    }

    assertTrue(m2.getRows() == m1.getRows());
    assertTrue(m2.getColumns() == m1.getColumns());

    checkMatrix (m2, new BilinearPattern(1.0, 0.01));

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

    Matrix m1 = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    Matrix m2 = buildMatrix(m1.getRows(),
                            m1.getColumns(),
                            new BilinearPattern(100, -0.01));

    Matrix m3 = m1.add(m2);

    checkMatrix(m3, new BilinearPattern(101, 0));

  }

  public void testSelfAdd() {

    GeneralMatrix m1 = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    Matrix m2 = buildMatrix(m1.getRows(),
                            m1.getColumns(),
                            new BilinearPattern(100, -0.01));

    m1.selfAdd(m2);

    checkMatrix(m1, new BilinearPattern(101, 0));

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

    Matrix m1 = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    Matrix m2 = buildMatrix(m1.getRows(),
                            m1.getColumns(),
                            new BilinearPattern(100, -0.01));

    Matrix m3 = m1.sub(m2);

    checkMatrix(m3, new BilinearPattern(-99, 0.02));

  }

  public void testSelfSub() {

    GeneralMatrix m1 = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    Matrix m2 = buildMatrix(m1.getRows(),
                            m1.getColumns(),
                            new BilinearPattern(100, -0.01));

    m1.selfSub(m2);

    checkMatrix(m1, new BilinearPattern(-99, 0.02));

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

    Matrix m1 = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    Matrix m2 = buildMatrix(m1.getColumns(), 4, new BilinearPattern(2, -1));

    Matrix m3 = m1.mul(m2);

    checkMatrix(m3, new ComplexPattern(m1.getColumns()));

  }

  public void testMulD() {

    Matrix m1 = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    Matrix m2 = m1.mul(2.5);

    checkMatrix(m2, new BilinearPattern(2.5, 0.025));

  }

  public void testSelfMul() {

    Matrix m = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    m.selfMul(2.5);

    checkMatrix(m, new BilinearPattern(2.5, 0.025));

  }

  public void testTranspose() {

    Matrix m1 = buildMatrix(5, 10, new BilinearPattern(1.0, 0.01));

    Matrix m2 = m1.getTranspose();

    assertTrue(m1.getRows() == m2.getColumns());
    assertTrue(m1.getColumns() == m2.getRows());

    checkMatrix(m2, new BilinearPattern(0.01, 1.0));

  }

  public static Test suite() {
    return new TestSuite(GeneralMatrixTest.class);
  }

  private interface ElementPattern {
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

  private static class ComplexPattern implements ElementPattern {
    public ComplexPattern(int p) {
      this.p = p;
    }
    public double value(int i, int j) {
      return p * ((2 * i - 0.01 *j) * (p - 1) / 2.0
                  - i* j
                  + (p - 1) * (2 * p - 1) / 300.0);
    }
    private final int p;
  }
  
  public GeneralMatrix buildMatrix(int rows, int columns,
                                   BilinearPattern pattern) {
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
