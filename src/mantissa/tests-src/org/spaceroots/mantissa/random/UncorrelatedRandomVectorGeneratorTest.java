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

import org.spaceroots.mantissa.linalg.SymetricalMatrix;

import junit.framework.*;

public class UncorrelatedRandomVectorGeneratorTest
  extends TestCase {

  public UncorrelatedRandomVectorGeneratorTest(String name) {
    super(name);
    mean = null;
    standardDeviation = null;
    generator = null;
  }

  public void testMeanAndCorrelation() {

    VectorialSampleStatistics sample = new VectorialSampleStatistics();
    for (int i = 0; i < 10000; ++i) {
      sample.add(generator.nextVector());
    }

    double[] estimatedMean = sample.getMean();
    double scale;
    SymetricalMatrix estimatedCorrelation = sample.getCovarianceMatrix(null);
    for (int i = 0; i < estimatedMean.length; ++i) {
      assertEquals(mean[i], estimatedMean[i], 0.07);
      for (int j = 0; j < i; ++j) {
        scale = standardDeviation[i] * standardDeviation[j];
        assertEquals(0, estimatedCorrelation.getElement(i, j) / scale, 0.03);
      }
      scale = standardDeviation[i] * standardDeviation[i];
      assertEquals(1, estimatedCorrelation.getElement(i, i) / scale, 0.025);
    }

  }

  public void setUp() {
    mean              = new double[] {0.0, 1.0, -3.0, 2.3};
    standardDeviation = new double[] {1.0, 2.0, 10.0, 0.1};
    generator =
      new UncorrelatedRandomVectorGenerator(mean, standardDeviation,
                                            new GaussianRandomGenerator(17399225432l));
  }

  public void tearDown() {
    mean = null;
    standardDeviation = null;
    generator = null;
  }

  public static Test suite() {
    return new TestSuite(UncorrelatedRandomVectorGeneratorTest.class);
  }

  private double[] mean;
  private double[] standardDeviation;
  private UncorrelatedRandomVectorGenerator generator;

}
