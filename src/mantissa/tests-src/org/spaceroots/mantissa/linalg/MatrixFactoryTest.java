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

public class MatrixFactoryTest
  extends TestCase {

  public MatrixFactoryTest(String name) {
    super(name);
  }

  public void testInvalidDimensions() {
    boolean gotIt;

    gotIt = false;
    try {
      MatrixFactory.buildMatrix(0, 2, null, 1, 1);
    } catch (IllegalArgumentException e) {
      gotIt = true;
    }
    assertTrue(gotIt);

  }

  public void testDiagonal() {
    Matrix m = MatrixFactory.buildMatrix(3, 3, null, 0, 0);
    assertTrue(m instanceof DiagonalMatrix);
  }

  public void testLowerTriangular() {
    Matrix m = MatrixFactory.buildMatrix(3, 3, null, 1, 0);
    assertTrue(m instanceof LowerTriangularMatrix);
  }

  public void testUpperTriangular() {
    Matrix m = MatrixFactory.buildMatrix(3, 3, null, 0, 1);
    assertTrue(m instanceof UpperTriangularMatrix);
  }

  public void testSquare() {
    Matrix m = MatrixFactory.buildMatrix(3, 3, null, 1, 1);
    assertTrue(m instanceof GeneralSquareMatrix);
  }

  public void testGeneral() {
    Matrix m = MatrixFactory.buildMatrix(3, 4, null, 0, 0);
    assertTrue(m instanceof GeneralMatrix);
  }

  public static Test suite() {
    return new TestSuite(MatrixFactoryTest.class);
  }

}
