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

public class VectorialSampleStatisticsTest
  extends TestCase {

  public VectorialSampleStatisticsTest(String name) {
    super(name);
    points = null;
  }

  public void testSimplistic() {
    VectorialSampleStatistics sample = new VectorialSampleStatistics();
    sample.add(new double[] {-1.0,  1.0});
    sample.add(new double[] { 1.0, -1.0});
    SymetricalMatrix c = sample.getCovarianceMatrix(null);
    assertEquals( 2.0, c.getElement(0, 0), 1.0e-12);
    assertEquals(-2.0, c.getElement(1, 0), 1.0e-12);
    assertEquals( 2.0, c.getElement(1, 1), 1.0e-12);
  }

  public void testBasicStats() {

    VectorialSampleStatistics sample = new VectorialSampleStatistics();
    for (int i = 0; i < points.length; ++i) {
      sample.add(points[i]);
    }

    assertEquals(points.length, sample.size());

    double[] min = sample.getMin();
    double[] max = sample.getMax();
    double[] mean = sample.getMean();
    SymetricalMatrix c = sample.getCovarianceMatrix(null);

    double[]   refMin  = new double[] {-0.70, 0.00, -3.10};
    double[]   refMax  = new double[] { 6.00, 2.30,  5.00};
    double[]   refMean = new double[] { 1.78, 1.62,  3.12};
    double[][] refC    = new double[][] {
      { 8.0470, -1.9195, -3.4445},
      {-1.9195,  1.0470,  3.2795},
      {-3.4445,  3.2795, 12.2070}
    };

    for (int i = 0; i < min.length; ++i) {
      assertEquals(refMin[i],  min[i],  1.0e-12);
      assertEquals(refMax[i],  max[i],  1.0e-12);
      assertEquals(refMean[i], mean[i], 1.0e-12);
      for (int j = 0; j <= i; ++j) {
        assertEquals(refC[i][j], c.getElement(i, j), 1.0e-12);
      }
    }

  }

  public void testAddSample() {

    VectorialSampleStatistics all  = new VectorialSampleStatistics();
    VectorialSampleStatistics even = new VectorialSampleStatistics();
    VectorialSampleStatistics odd  = new VectorialSampleStatistics();
    for (int i = 0; i < points.length; ++i) {
      all.add(points[i]);
      if (i % 2 == 0) {
        even.add(points[i]);
      } else {
        odd.add(points[i]);
      }
    }

    even.add(odd);

    assertEquals(all.size(), even.size());

    double[] min = even.getMin();
    double[] max = even.getMax();
    double[] mean = even.getMean();
    SymetricalMatrix c = even.getCovarianceMatrix(null);

    double[] refMin = all.getMin();
    double[] refMax = all.getMax();
    double[] refMean = all.getMean();
    SymetricalMatrix refC = all.getCovarianceMatrix(null);

    for (int i = 0; i < min.length; ++i) {
      assertEquals(refMin[i],  min[i],  1.0e-12);
      assertEquals(refMax[i],  max[i],  1.0e-12);
      assertEquals(refMean[i], mean[i], 1.0e-12);
      for (int j = 0; j <= i; ++j) {
        assertEquals(refC.getElement(i, j), c.getElement(i, j), 1.0e-12);
      }
    }

  }

  public void testAddArray() {

    VectorialSampleStatistics loop   = new VectorialSampleStatistics();
    VectorialSampleStatistics direct = new VectorialSampleStatistics();
    for (int i = 0; i < points.length; ++i) {
      loop.add(points[i]);
    }
    direct.add(points);

    assertEquals(loop.size(), direct.size());

    double[] min = direct.getMin();
    double[] max = direct.getMax();
    double[] mean = direct.getMean();
    SymetricalMatrix c = direct.getCovarianceMatrix(null);

    double[] refMin = loop.getMin();
    double[] refMax = loop.getMax();
    double[] refMean = loop.getMean();
    SymetricalMatrix refC = loop.getCovarianceMatrix(null);

    for (int i = 0; i < min.length; ++i) {
      assertEquals(refMin[i],  min[i],  1.0e-12);
      assertEquals(refMax[i],  max[i],  1.0e-12);
      assertEquals(refMean[i], mean[i], 1.0e-12);
      for (int j = 0; j <= i; ++j) {
        assertEquals(refC.getElement(i, j), c.getElement(i, j), 1.0e-12);
      }
    }

  }

  public void setUp() {
    points = new double[][] {
      { 1.2, 2.3,  4.5},
      {-0.7, 2.3,  5.0},
      { 3.1, 0.0, -3.1},
      { 6.0, 1.2,  4.2},
      {-0.7, 2.3,  5.0}
    };
  }

  public void tearDown() {
    points = null;
  }

  public static Test suite() {
    return new TestSuite(VectorialSampleStatisticsTest.class);
  }

  private double [][] points;

}
