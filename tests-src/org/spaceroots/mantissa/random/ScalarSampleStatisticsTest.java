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

public class ScalarSampleStatisticsTest
  extends TestCase {

  public ScalarSampleStatisticsTest(String name) {
    super(name);
    points = null;
  }

  public void testBasicStats() {

    ScalarSampleStatistics sample = new ScalarSampleStatistics();
    for (int i = 0; i < points.length; ++i) {
      sample.add(points[i]);
    }

    assertEquals(points.length, sample.size());
    assertEquals(-5.0, sample.getMin(), 1.0e-12);
    assertEquals(10.4, sample.getMax(), 1.0e-12);
    assertEquals( 3.0, sample.getMean(), 1.0e-12);
    assertEquals( 3.920034013457876, sample.getStandardDeviation(),
                  1.0e-12);

  }

  public void testAddSample() {

    ScalarSampleStatistics all  = new ScalarSampleStatistics();
    ScalarSampleStatistics even = new ScalarSampleStatistics();
    ScalarSampleStatistics odd  = new ScalarSampleStatistics();
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
    assertEquals(all.getMin(), even.getMin(), 1.0e-12);
    assertEquals(all.getMax(), even.getMax(), 1.0e-12);
    assertEquals(all.getMean(), even.getMean(), 1.0e-12);
    assertEquals(all.getStandardDeviation(), even.getStandardDeviation(),
                 1.0e-12);

  }

  public void testAddArray() {

    ScalarSampleStatistics loop   = new ScalarSampleStatistics();
    ScalarSampleStatistics direct = new ScalarSampleStatistics();
    for (int i = 0; i < points.length; ++i) {
      loop.add(points[i]);
    }
    direct.add(points);

    assertEquals(loop.size(), direct.size());
    assertEquals(loop.getMin(), direct.getMin(), 1.0e-12);
    assertEquals(loop.getMax(), direct.getMax(), 1.0e-12);
    assertEquals(loop.getMean(), direct.getMean(), 1.0e-12);
    assertEquals(loop.getStandardDeviation(), direct.getStandardDeviation(),
                 1.0e-12);

  }

  public void setUp() {
    points = new double[] {1.0, 4.2, -5, 4.0, 2.9, 10.4, 0.0, 4.1, 4.2, 4.2};
  }

  public void tearDown() {
    points = null;
  }

  public static Test suite() {
    return new TestSuite(ScalarSampleStatisticsTest.class);
  }

  private double[] points;

}
