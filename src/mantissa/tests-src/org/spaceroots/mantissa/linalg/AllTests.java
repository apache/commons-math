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

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
  public static Test suite() {

    TestSuite suite = new TestSuite("org.spaceroots.mantissa.linalg"); 

    suite.addTest(NonNullRangeTest.suite()); 
    suite.addTest(GeneralMatrixTest.suite()); 
    suite.addTest(DiagonalMatrixTest.suite()); 
    suite.addTest(LowerTriangularMatrixTest.suite()); 
    suite.addTest(UpperTriangularMatrixTest.suite()); 
    suite.addTest(GeneralSquareMatrixTest.suite()); 
    suite.addTest(SymetricalMatrixTest.suite()); 
    suite.addTest(MatrixFactoryTest.suite());

    return suite; 

  }
}
