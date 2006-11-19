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

package org.spaceroots.mantissa.random;

import junit.framework.*;

public class GaussianRandomGeneratorTest
  extends TestCase {

  public GaussianRandomGeneratorTest(String name) {
    super(name);
  }

  public void testMeanAndStandardDeviation() {
    GaussianRandomGenerator generator = new GaussianRandomGenerator(17399225432l);
    ScalarSampleStatistics sample = new ScalarSampleStatistics();
    for (int i = 0; i < 10000; ++i) {
      sample.add(generator.nextDouble());
    }
    assertEquals(0.0, sample.getMean(), 0.012);
    assertEquals(1.0, sample.getStandardDeviation(), 0.01);
  }

  public static Test suite() {
    return new TestSuite(GaussianRandomGeneratorTest.class);
  }

}
