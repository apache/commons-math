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

package org.spaceroots.mantissa.estimation;

import java.util.Random;
import junit.framework.*;

public class GaussNewtonEstimatorTest
  extends TestCase
  implements EstimationProblem {

  public GaussNewtonEstimatorTest(String name) {
    super(name);
  }

  public void testNoMeasurementError()
    throws EstimationException {
    initRandomizedGrid(2.3);
    initProblem(0.0);
    GaussNewtonEstimator estimator =
      new GaussNewtonEstimator(100, 1.0e-7, 1.0e-10, 1.0e-10);
    estimator.estimate(this);
    checkGrid(0.01);
  }

  public void testSmallMeasurementError()
    throws EstimationException {
    initRandomizedGrid(2.3);
    initProblem(0.02);
    GaussNewtonEstimator estimator =
      new GaussNewtonEstimator(100, 1.0e-7, 1.0e-10, 1.0e-10);
    estimator.estimate(this);
    checkGrid(0.1);
  }

  public void testNoError()
    throws EstimationException {
    initRandomizedGrid(0.0);
    initProblem(0.0);
    GaussNewtonEstimator estimator =
      new GaussNewtonEstimator(100, 1.0e-7, 1.0e-10, 1.0e-10);
    estimator.estimate(this);
    checkGrid(1.0e-10);
  }

  public void testUnsolvableProblem() {

    initRandomizedGrid(2.3);
    initProblem(0.0);

    // reduce the number of measurements below the limit threshold
    int unknowns = unboundPars.length;
    WeightedMeasurement[] reducedSet = new WeightedMeasurement[unknowns - 1];
    for (int i = 0; i < reducedSet.length; ++i) {
      reducedSet[i] = measurements[i];
    }
    measurements = reducedSet;

    boolean gotIt = false;
    try {
      GaussNewtonEstimator estimator =
        new GaussNewtonEstimator(100, 1.0e-7, 1.0e-10, 1.0e-10);
      estimator.estimate(this);
    } catch(EstimationException e) {
      gotIt = true;
    }

    assertTrue(gotIt);

  }

  public static Test suite() {
    return new TestSuite(GaussNewtonEstimatorTest.class);
  }

  public void setUp() {
    initPerfectGrid(5);
  }

  public void tearDown() {
    perfectPars    = null;
    randomizedPars = null;
    unboundPars    = null;
    measurements   = null;
  }

  private void initPerfectGrid(int gridSize) {
    perfectPars = new EstimatedParameter[gridSize * gridSize * 2];

    int k = 0;
    for (int i = 0; i < gridSize; ++i) {
      for (int j = 0; j < gridSize; ++j) {

        String name            = Integer.toString(k);
        perfectPars[2 * k]     = new EstimatedParameter("x" + name, i);
        perfectPars[2 * k + 1] = new EstimatedParameter("y" + name, j);
        ++k;
      }
    }

  }

  private void initRandomizedGrid(double initialGuessError) {
    Random randomizer = new Random(2353995334l);
    randomizedPars    = new EstimatedParameter[perfectPars.length];

    // add an error to every point coordinate
    for (int k = 0; k < randomizedPars.length; ++k) {
      String name  = perfectPars[k].getName();
      double value = perfectPars[k].getEstimate();
      double error = randomizer.nextGaussian() * initialGuessError;
      randomizedPars[k] = new EstimatedParameter(name, value + error);
    }

  }

  private void initProblem(double measurementError) {

    int pointsNumber       = randomizedPars.length / 2;
    int measurementsNumber = pointsNumber * (pointsNumber - 1) / 2;
    measurements           = new WeightedMeasurement[measurementsNumber];

    Random randomizer = new Random(5785631926l);

    // for the test, we consider that the perfect grid is the reality
    // and that the randomized grid is the first (wrong) estimate.
    int i = 0;
    for (int l = 0; l < (pointsNumber - 1); ++l) {
      for (int m = l + 1; m < pointsNumber; ++m) {
        // perfect measurements on the real data
        double dx = perfectPars[2 * l].getEstimate()
          - perfectPars[2 * m].getEstimate();
        double dy = perfectPars[2 * l + 1].getEstimate()
          - perfectPars[2 * m + 1].getEstimate();
        double d = Math.sqrt(dx * dx + dy * dy);

        // adding a noise to the measurements
        d += randomizer.nextGaussian() * measurementError;

        // add the measurement to the current problem
        measurements[i++] = new Distance(1.0, d,
                                         randomizedPars[2 * l],
                                         randomizedPars[2 * l + 1],
                                         randomizedPars[2 * m],
                                         randomizedPars[2 * m + 1]);

      }
    }

    // fix three values in the randomized grid and bind them (there
    // are two abscissas and one ordinate, so if there were no error
    // at all, the estimated grid should be correctly centered on the
    // perfect grid)
    int oddNumber = 2 * (randomizedPars.length / 4) - 1;
    for (int k = 0; k < 2 * oddNumber + 1; k += oddNumber) {
      randomizedPars[k].setEstimate(perfectPars[k].getEstimate());
      randomizedPars[k].setBound(true);
    }

    // store the unbound parameters in a specific table
    unboundPars = new EstimatedParameter[randomizedPars.length - 3];
    for (int src = 0, dst = 0; src < randomizedPars.length; ++src) {
      if (! randomizedPars[src].isBound()) {
        unboundPars[dst++] = randomizedPars[src];
      }
    }

  }

  private void checkGrid(double threshold) {

    double rms = 0;
    for (int i = 0; i < perfectPars.length; ++i) {
      rms += perfectPars[i].getEstimate() - randomizedPars[i].getEstimate();
    }
    rms = Math.sqrt(rms / perfectPars.length);

    assertTrue(rms <= threshold);

  }

  private static class Distance extends WeightedMeasurement {

    public Distance(double weight, double measuredValue,
                    EstimatedParameter x1, EstimatedParameter y1,
                    EstimatedParameter x2, EstimatedParameter y2) {
      super(weight, measuredValue);
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }

    public double getTheoreticalValue() {
      double dx = x2.getEstimate() - x1.getEstimate();
      double dy = y2.getEstimate() - y1.getEstimate();
      return Math.sqrt(dx * dx + dy * dy);
    }

    public double getPartial(EstimatedParameter p) {

      // first quick answer for most parameters
      if ((p != x1) && (p != y1) && (p != x2) && (p != y2)) {
        return 0.0;
      }

      // compute the value now as we know we depend on the specified parameter
      double distance = getTheoreticalValue();

      if (p == x1) {
        return (x1.getEstimate() - x2.getEstimate()) / distance;
      } else if (p == x2) {
        return (x2.getEstimate() - x1.getEstimate()) / distance;
      } else if (p == y1) {
        return (y1.getEstimate() - y2.getEstimate()) / distance;
      } else {
        return (y2.getEstimate() - y1.getEstimate()) / distance;
      }

    }

    private EstimatedParameter x1;
    private EstimatedParameter y1;
    private EstimatedParameter x2;
    private EstimatedParameter y2;
    private static final long serialVersionUID = 4090004243280980746L;

  }

  public WeightedMeasurement[] getMeasurements() {
    return (WeightedMeasurement[]) measurements.clone();
  }

  public EstimatedParameter[] getUnboundParameters() {
    return (EstimatedParameter[]) unboundPars.clone();
  }

  public EstimatedParameter[] getAllParameters() {
    return (EstimatedParameter[]) randomizedPars.clone();
  }

  private EstimatedParameter[]  perfectPars;
  private EstimatedParameter[]  randomizedPars;
  private EstimatedParameter[]  unboundPars;
  private WeightedMeasurement[] measurements;

}
