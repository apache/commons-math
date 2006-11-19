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

import org.spaceroots.mantissa.linalg.Matrix;
import org.spaceroots.mantissa.linalg.GeneralMatrix;
import org.spaceroots.mantissa.linalg.SymetricalMatrix;

import junit.framework.*;

public class CorrelatedRandomVectorGeneratorTest
  extends TestCase {

  public CorrelatedRandomVectorGeneratorTest(String name) {
    super(name);
  }

  public void testRank() {
    assertEquals(3, generator.getRank());
  }

  public void testRootMatrix() {
    Matrix b = generator.getRootMatrix();
    Matrix bbt = b.mul(b.getTranspose());
    for (int i = 0; i < covariance.getRows(); ++i) {
      for (int j = 0; j < covariance.getColumns(); ++j) {
        assertEquals(covariance.getElement(i, j),
                     bbt.getElement(i, j),
                     1.0e-12);
      }
    }
  }

  public void testMeanAndCovariance() {

    VectorialSampleStatistics sample = new VectorialSampleStatistics();
    for (int i = 0; i < 5000; ++i) {
      sample.add(generator.nextVector());
    }

    double[] estimatedMean = sample.getMean(null);
    SymetricalMatrix estimatedCovariance = sample.getCovarianceMatrix(null);
    for (int i = 0; i < estimatedMean.length; ++i) {
      assertEquals(mean[i], estimatedMean[i], 0.07);
      for (int j = 0; j <= i; ++j) {
        assertEquals(covariance.getElement(i, j),
                     estimatedCovariance.getElement(i, j),
                     0.1 * (1.0 + Math.abs(mean[i])) * (1.0 + Math.abs(mean[j])));
      }
    }

  }

  public void setUp() {
    try {
      mean = new double[] { 0.0, 1.0, -3.0, 2.3};

      GeneralMatrix b = new GeneralMatrix(4, 3);
      int counter = 0;
      for (int i = 0; i < b.getRows(); ++i) {
        for (int j = 0; j < b.getColumns(); ++j) {
          b.setElement(i, j, 1.0 + 0.1 * ++counter);
        }
      }
      Matrix bbt = b.mul(b.getTranspose());
      covariance = new SymetricalMatrix(mean.length);
      for (int i = 0; i < covariance.getRows(); ++i) {
        covariance.setElement(i, i, bbt.getElement(i, i));
        for (int j = 0; j < covariance.getColumns(); ++j) {
          covariance.setElementAndSymetricalElement(i, j,
                                                    bbt.getElement(i, j));
        }
      }

      GaussianRandomGenerator rawGenerator = new GaussianRandomGenerator(17399225432l);
      generator = new CorrelatedRandomVectorGenerator(mean, covariance, rawGenerator);
    } catch (NotPositiveDefiniteMatrixException e) {
      fail("not positive definite matrix");
    }
  }

  public void tearDown() {
    mean       = null;
    covariance = null;
    generator  = null;
  }

  public static Test suite() {
    return new TestSuite(CorrelatedRandomVectorGeneratorTest.class);
  }

  private double[] mean;
  private SymetricalMatrix covariance;
  private CorrelatedRandomVectorGenerator generator;

}
