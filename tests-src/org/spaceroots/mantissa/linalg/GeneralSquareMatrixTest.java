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

public class GeneralSquareMatrixTest
  extends TestCase {

  public GeneralSquareMatrixTest(String name) {
    super(name);
  }

  public void testDimensions() {
    GeneralSquareMatrix m = new GeneralSquareMatrix(3);
    assertTrue(m.getRows() == 3);
    assertTrue(m.getColumns() == 3);
  }

  public void testInvalidDimensions() {
    boolean gotIt;

    gotIt = false;
    try {
      new GeneralSquareMatrix(0);
    } catch(IllegalArgumentException e) {
      gotIt = true;
    }
    assertTrue(gotIt);

    gotIt = false;
    try {
      new GeneralSquareMatrix(-3, null);
    } catch(IllegalArgumentException e) {
      gotIt = true;
    }
    assertTrue(gotIt);

  }

  public void testElements() {
    Matrix m = buildMatrix(5, new BilinearPattern(1.0, 0.01));

    checkMatrix(m, new BilinearPattern(1.0, 0.01));

  }

  public void testCopy() {
    GeneralSquareMatrix m1 = buildMatrix(5, new BilinearPattern(1.0, 0.01));

    GeneralSquareMatrix m2 = new GeneralSquareMatrix(m1);

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
    GeneralSquareMatrix m1 = buildMatrix(5, new BilinearPattern(1.0, 0.01));

    Matrix m2 = m1.duplicate();
    assertTrue(m2 instanceof GeneralSquareMatrix);

    for (int i = 0; i < m1.getRows(); ++i) {
      for (int j = 0; j < m1.getColumns(); ++j) {
        m1.setElement(i, j, -1.0);
      }
    }

    assertTrue(m2.getRows() == m1.getRows());
    assertTrue(m2.getColumns() == m1.getColumns());

    checkMatrix(m2, new BilinearPattern(1.0, 0.01));

  }

  public void testSelfAdd() {
    GeneralSquareMatrix m1 = buildMatrix(5, new BilinearPattern(1.0, 0.01));

    GeneralSquareMatrix m2 = buildMatrix(5, new BilinearPattern(2, -0.03));


    m1.selfAdd(m2);

    checkMatrix(m1, new BilinearPattern(3, -0.02));

  }

  public void testSelfSub() {
    GeneralSquareMatrix m1 = buildMatrix(5, new BilinearPattern(1.0, 0.01));

    GeneralSquareMatrix m2 = buildMatrix(5, new BilinearPattern(2, -0.03));


    m1.selfSub(m2);

    checkMatrix(m1, new BilinearPattern(-1, 0.04));

  }

  public void testDeterminant() {

    GeneralSquareMatrix m1 = buildProblem1().a;
    assertTrue(Math.abs(m1.getDeterminant(1.0e-10) - 6.0) < 1.0e-10);

    GeneralSquareMatrix m2 = buildProblem2().a;
    assertTrue(Math.abs(m2.getDeterminant(1.0e-10) + 0.9999999) < 1.0e-10);

    GeneralSquareMatrix m3 = buildProblem3().a;
    assertTrue(Math.abs(m3.getDeterminant(1.0e-10) - 0.0) < 1.0e-10);

  }

  public void testSolve()
    throws SingularMatrixException {

    LinearProblem p;
    Matrix result;

    p = buildProblem1();
    result = p.a.solve(p.b, 1.0e-10);
    checkSolve(p, result);

    p = buildProblem2();
    result = p.a.solve(p.b, 1.0e-10);
    checkSolve(p, result);

    try {
      p = buildProblem3();
      result = p.a.solve(p.b, 1.0e-10);
      fail("got " + result + ", should have caught an exception");
    } catch(SingularMatrixException e) {
      // expected
    } catch(Exception e) {
      fail("wrong exception caught: " + e.getMessage());
    }
 
  }

  public void testInverse()
    throws SingularMatrixException {

    SquareMatrix a, inverse;

    a = buildProblem1().a;
    inverse = a.getInverse(1.0e-10);
    checkMatrix(a.mul(inverse), new IdentityPattern());
    
    a = buildProblem2().a;
    inverse = a.getInverse(1.0e-10);
    checkMatrix(a.mul(inverse), new IdentityPattern());

    try {
      a = buildProblem3().a;
      inverse = a.getInverse(1.0e-10);
      fail("got " + inverse + ", should have caught an exception");
    } catch(SingularMatrixException e) {
      // expected
    } catch(Exception e) {
      fail("wrong exception caught: " + e.getMessage());
    }

  }

  public static Test suite() {
    return new TestSuite(GeneralSquareMatrixTest.class);
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

  private static class IdentityPattern implements ElementPattern {
    public double value(int i, int j) {
      return (i == j) ? 1.0 : 0.0;
    }
  }

  public GeneralSquareMatrix buildMatrix(int order,
                                         ElementPattern pattern) {
    GeneralSquareMatrix m = new GeneralSquareMatrix(order);

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

  private static class LinearProblem {
    public GeneralSquareMatrix a;
    public Matrix              x;
    public Matrix              b;
    public LinearProblem(GeneralSquareMatrix a, Matrix x, Matrix b) {
      this.a = a;
      this.x = x;
      this.b = b;
    }
  }

  private LinearProblem buildProblem1() {

    GeneralSquareMatrix a = new GeneralSquareMatrix(4);

    a.setElement(0, 0,   2.0);
    a.setElement(0, 1,   1.0);
    a.setElement(0, 2,   0.0);
    a.setElement(0, 3,   4.0);

    a.setElement(1, 0,  -4.0);
    a.setElement(1, 1,  -2.0);
    a.setElement(1, 2,   3.0);
    a.setElement(1, 3,  -7.0);

    a.setElement(2, 0,   4.0);
    a.setElement(2, 1,   1.0);
    a.setElement(2, 2,  -2.0);
    a.setElement(2, 3,   8.0);

    a.setElement(3, 0,   0.0);
    a.setElement(3, 1,  -3.0);
    a.setElement(3, 2, -12.0);
    a.setElement(3, 3,  -1.0);

    GeneralMatrix x = new GeneralMatrix(4, 1);

    x.setElement(0, 0,  3.0);
    x.setElement(1, 0,  4.0);
    x.setElement(2, 0, -1.0);
    x.setElement(3, 0, -2.0);

    GeneralMatrix b = new GeneralMatrix(4, 1);

    b.setElement(0, 0,  2.0);
    b.setElement(1, 0, -9.0);
    b.setElement(2, 0,  2.0);
    b.setElement(3, 0,  2.0);

    return new LinearProblem(a, x, b);

  }

  private LinearProblem buildProblem2()
  {

    double epsilon = 1.0e-7;

    GeneralSquareMatrix a = new GeneralSquareMatrix(2);

    a.setElement(0, 0, epsilon);
    a.setElement(0, 1, 1.0);

    a.setElement(1, 0, 1.0);
    a.setElement(1, 1, 1.0);

    GeneralMatrix x = new GeneralMatrix(2, 2);

    x.setElement(0, 0, 1.0 + epsilon);
    x.setElement(1, 0, 1.0 - epsilon);

    x.setElement(0, 1, epsilon);
    x.setElement(1, 1, 1.0);

    GeneralMatrix b = new GeneralMatrix(2, 2);

    b.setElement(0, 0, 1.0 + epsilon * epsilon);
    b.setElement(1, 0, 2.0);

    b.setElement(0, 1, 1.0 + epsilon * epsilon);
    b.setElement(1, 1, 1.0 + epsilon);

    return new LinearProblem(a, x, b);

  }

  private LinearProblem buildProblem3 ()
  {

    GeneralSquareMatrix a = new GeneralSquareMatrix(3);

    a.setElement(0, 0,  1.0);
    a.setElement(0, 1,  2.0);
    a.setElement(0, 1, -3.0);

    a.setElement(1, 0,  2.0);
    a.setElement(1, 1,  1.0);
    a.setElement(1, 1,  3.0);

    a.setElement(2, 0, -3.0);
    a.setElement(2, 1,  0.0);
    a.setElement(2, 1, -9.0);

    GeneralMatrix x = new GeneralMatrix(3, 1);
    GeneralMatrix b = new GeneralMatrix(3, 1);

    return new LinearProblem(a, x, b);

  }

  private void checkSolve(LinearProblem p, Matrix result)
  {

    Matrix residual = p.a.mul(result).sub(p.b);
    for (int i = 0; i < residual.getRows(); ++i) {
      for (int j = 0; j < residual.getColumns(); ++j) {
        assertTrue(Math.abs(residual.getElement(i, j)) < 1.0e-10);
      }
    }

    for (int i = 0; i < result.getRows(); ++i) {
      for (int j = 0; j < result.getColumns(); ++j) {
        assertTrue(Math.abs(result.getElement(i, j) - p.x.getElement(i, j))
                   < 1.0e-10);
      }
    }

  }

}
