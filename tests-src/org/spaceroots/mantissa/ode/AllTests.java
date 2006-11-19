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

package org.spaceroots.mantissa.ode;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
  public static Test suite() { 

    TestSuite suite = new TestSuite("org.spaceroots.mantissa.ode"); 

    suite.addTest(EulerStepInterpolatorTest.suite());
    suite.addTest(EulerIntegratorTest.suite());
    suite.addTest(MidpointIntegratorTest.suite());
    suite.addTest(ClassicalRungeKuttaIntegratorTest.suite());
    suite.addTest(GillIntegratorTest.suite());
    suite.addTest(ThreeEighthesIntegratorTest.suite());
    suite.addTest(HighamHall54IntegratorTest.suite());
    suite.addTest(DormandPrince54IntegratorTest.suite());
    suite.addTest(DormandPrince853IntegratorTest.suite());
    suite.addTest(GraggBulirschStoerIntegratorTest.suite());
    suite.addTest(FirstOrderConverterTest.suite());
    suite.addTest(StepNormalizerTest.suite());
    suite.addTest(ContinuousOutputModelTest.suite());
    suite.addTest(ClassicalRungeKuttaStepInterpolatorTest.suite());
    suite.addTest(GillStepInterpolatorTest.suite());
    suite.addTest(ThreeEighthesStepInterpolatorTest.suite());
    suite.addTest(DormandPrince853StepInterpolatorTest.suite());
    suite.addTest(DormandPrince54StepInterpolatorTest.suite());
    suite.addTest(HighamHall54StepInterpolatorTest.suite());
    suite.addTest(MidpointStepInterpolatorTest.suite());
    suite.addTest(GraggBulirschStoerStepInterpolatorTest.suite());

    return suite; 

  }
}
