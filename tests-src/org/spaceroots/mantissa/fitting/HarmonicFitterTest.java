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

import org.spaceroots.mantissa.estimation.EstimationException;
import org.spaceroots.mantissa.estimation.LevenbergMarquardtEstimator;
import org.spaceroots.mantissa.estimation.WeightedMeasurement;

public class HarmonicFitterTest
  extends TestCase {

  public HarmonicFitterTest(String name) {
    super(name);
  }

  public void testNoError()
    throws EstimationException {
    HarmonicFunction f = new HarmonicFunction(0.2, 3.4, 4.1);

    HarmonicFitter fitter =
      new HarmonicFitter(new LevenbergMarquardtEstimator());
    for (double x = 0.0; x < 1.3; x += 0.01) {
      fitter.addWeightedPair(1.0, x, f.valueAt(x));
    }

    double[] coeffs = fitter.fit();

    HarmonicFunction fitted = new HarmonicFunction(coeffs[0],
                                                   coeffs[1],
                                                   coeffs[2]);
    assertTrue(Math.abs(coeffs[0] - f.getA()) < 1.0e-13);
    assertTrue(Math.abs(coeffs[1] - f.getOmega()) < 1.0e-13);
    assertTrue(Math.abs(coeffs[2] - center(f.getPhi(), coeffs[2])) < 1.0e-13);

    for (double x = -1.0; x < 1.0; x += 0.01) {
      assertTrue(Math.abs(f.valueAt(x) - fitted.valueAt(x)) < 1.0e-13);
    }

  }

  public void test1PercentError()
    throws EstimationException {
    Random randomizer = new Random(64925784252l);
    HarmonicFunction f = new HarmonicFunction(0.2, 3.4, 4.1);

    HarmonicFitter fitter =
      new HarmonicFitter(new LevenbergMarquardtEstimator());
    for (double x = 0.0; x < 10.0; x += 0.1) {
      fitter.addWeightedPair(1.0, x,
                             f.valueAt(x) + 0.01 * randomizer.nextGaussian());
    }

    double[] coeffs = fitter.fit();

    new HarmonicFunction(coeffs[0], coeffs[1], coeffs[2]);
    assertTrue(Math.abs(coeffs[0] - f.getA()) < 7.6e-4);
    assertTrue(Math.abs(coeffs[1] - f.getOmega()) < 2.7e-3);
    assertTrue(Math.abs(coeffs[2] - center(f.getPhi(), coeffs[2])) < 1.3e-2);

    WeightedMeasurement[] measurements = fitter.getMeasurements();
    for (int i = 0; i < measurements.length; ++i) {
      WeightedMeasurement m = measurements[i];
      assertTrue(Math.abs(measurements[i].getMeasuredValue()
                          - m.getTheoreticalValue()) < 0.04);
    }

  }

  public void testUnsorted()
    throws EstimationException {
    Random randomizer = new Random(64925784252l);
    HarmonicFunction f = new HarmonicFunction(0.2, 3.4, 4.1);

    HarmonicFitter fitter =
      new HarmonicFitter(new LevenbergMarquardtEstimator());

    // build a regularly spaced array of measurements
    int size = 100;
    double[] xTab = new double[size];
    double[] yTab = new double[size];
    for (int i = 0; i < size; ++i) {
      xTab[i] = 0.1 * i;
      yTab[i] = f.valueAt (xTab[i]) + 0.01 * randomizer.nextGaussian();
    }

    // shake it
    for (int i = 0; i < size; ++i) {
      int i1 = randomizer.nextInt(size);
      int i2 = randomizer.nextInt(size);
      double xTmp = xTab[i1];
      double yTmp = yTab[i1];
      xTab[i1] = xTab[i2];
      yTab[i1] = yTab[i2];
      xTab[i2] = xTmp;
      yTab[i2] = yTmp;
    }

    // pass it to the fitter
    for (int i = 0; i < size; ++i) {
      fitter.addWeightedPair(1.0, xTab[i], yTab[i]);
    }

    double[] coeffs = fitter.fit();

    new HarmonicFunction(coeffs[0], coeffs[1], coeffs[2]);
    assertTrue(Math.abs(coeffs[0] - f.getA()) < 7.6e-4);
    assertTrue(Math.abs(coeffs[1] - f.getOmega()) < 3.5e-3);
    assertTrue(Math.abs(coeffs[2] - center(f.getPhi(), coeffs[2])) < 1.5e-2);

    WeightedMeasurement[] measurements = fitter.getMeasurements();
    for (int i = 0; i < measurements.length; ++i) {
      WeightedMeasurement m = measurements[i];
      assertTrue(Math.abs(m.getMeasuredValue() - m.getTheoreticalValue())
                 < 0.04);
    }
  }

  public static Test suite() {
    return new TestSuite(HarmonicFitterTest.class);
  }

  /** Center an angle with respect to another one. */
  private static double center(double a, double ref) {
    double twoPi = Math.PI + Math.PI;
    return a - twoPi * Math.floor((a + Math.PI - ref) / twoPi);
  }

  private static class HarmonicFunction {
    public HarmonicFunction(double a, double omega, double phi) {
      this.a     = a;
      this.omega = omega;
      this.phi   = phi;
    }

    public double valueAt(double x) {
      return a * Math.cos(omega * x + phi);
    }

    public double getA() {
      return a;
    }

    public double getOmega() {
      return omega;
    }

    public double getPhi() {
      return phi;
    }

    private double a;
    private double omega;
    private double phi;

  }

}
