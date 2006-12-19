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

package org.spaceroots.mantissa.fitting;

import java.util.Random;
import junit.framework.*;

import org.spaceroots.mantissa.estimation.EstimatedParameter;
import org.spaceroots.mantissa.estimation.LevenbergMarquardtEstimator;
import org.spaceroots.mantissa.estimation.WeightedMeasurement;

public class AbstractCurveFitterTest
  extends TestCase {

  public AbstractCurveFitterTest(String name) {
    super(name);
    fitter = null;
  }

  public void testAlreadySorted() {
    for (double x = 0.0; x < 100.0; x += 1.0) {
      fitter.addWeightedPair(1.0, x, 0.0);
    }
    checkSorted();
  }

  public void testReversed() {
    for (double x = 0.0; x < 100.0; x += 1.0) {
      fitter.addWeightedPair(1.0, 100.0 - x, 0.0);
    }
    checkSorted();
  }

  public void testRandom() {
    Random randomizer = new Random(86757343594l);
    for (int i = 0; i < 100; ++i) {
      fitter.addWeightedPair(1.0, 10.0 * randomizer.nextDouble(), 0.0);
    }
    checkSorted();
  }

  public void checkSorted() {
    fitter.doSort();

    WeightedMeasurement[] measurements = fitter.getMeasurements();
    for (int i = 1; i < measurements.length; ++i) {
      AbstractCurveFitter.FitMeasurement m1
        = (AbstractCurveFitter.FitMeasurement) measurements[i-1];
      AbstractCurveFitter.FitMeasurement m2
        = (AbstractCurveFitter.FitMeasurement) measurements[i];
      assertTrue(m1.x <= m2.x);
    }

  }

  public static Test suite() {
    return new TestSuite(AbstractCurveFitterTest.class);
  }

  public void setUp() {
    fitter = new DummyFitter();
  }

  public void tearDown() {
    fitter = null;
  }

  private static class DummyFitter
    extends AbstractCurveFitter {

    public DummyFitter() {
      super(10, new LevenbergMarquardtEstimator());
    }

    public double valueAt(double x) {
      return 0.0;
    }

    public double partial(double x, EstimatedParameter p) {
      return 0.0;
    }

    public void doSort() {
      sortMeasurements();
    }

    private static final long serialVersionUID = 4016396219767783678L;

  }

  private DummyFitter fitter;

}
