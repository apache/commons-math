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

package org.spaceroots.mantissa.algebra;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
  public static Test suite() { 

    TestSuite suite = new TestSuite("org.spaceroots.mantissa.algebra"); 

    suite.addTest(RationalNumberTest.suite());
    suite.addTest(PolynomialRationalTest.suite());
    suite.addTest(PolynomialDoubleTest.suite());
    suite.addTest(ChebyshevTest.suite());
    suite.addTest(HermiteTest.suite());
    suite.addTest(LegendreTest.suite());
    suite.addTest(LaguerreTest.suite());
    suite.addTest(PolynomialFractionTest.suite());

    return suite; 

  }
}
