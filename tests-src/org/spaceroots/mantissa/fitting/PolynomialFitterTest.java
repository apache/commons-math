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
import org.spaceroots.mantissa.estimation.Estimator;
import org.spaceroots.mantissa.estimation.GaussNewtonEstimator;
import org.spaceroots.mantissa.estimation.LevenbergMarquardtEstimator;

public class PolynomialFitterTest
  extends TestCase {

  public PolynomialFitterTest(String name) {
    super(name);
  }

  public void testNoError()
    throws EstimationException {
    Random randomizer = new Random(64925784252l);
    for (int degree = 0; degree < 10; ++degree) {
      Polynom p = new Polynom(degree);
      for (int i = 0; i <= degree; ++i) {
        p.initCoeff (i, randomizer.nextGaussian());
      }

      PolynomialFitter fitter =
        new PolynomialFitter(degree, new LevenbergMarquardtEstimator());
      for (int i = 0; i <= degree; ++i) {
        fitter.addWeightedPair(1.0, i, p.valueAt(i));
      }

      Polynom fitted = new Polynom(fitter.fit());

      for (double x = -1.0; x < 1.0; x += 0.01) {
        double error = Math.abs(p.valueAt(x) - fitted.valueAt(x))
          / (1.0 + Math.abs(p.valueAt(x)));
        assertTrue(Math.abs(error) < 1.0e-5);
      }

    }

  }

  public void testSmallError()
    throws EstimationException {
    Random randomizer = new Random(53882150042l);
    for (int degree = 0; degree < 10; ++degree) {
      Polynom p = new Polynom(degree);
      for (int i = 0; i <= degree; ++i) {
        p.initCoeff(i, randomizer.nextGaussian());
      }

      PolynomialFitter fitter =
        new PolynomialFitter(degree, new LevenbergMarquardtEstimator());
      for (double x = -1.0; x < 1.0; x += 0.01) {
        fitter.addWeightedPair(1.0, x,
                               p.valueAt(x) + 0.1 * randomizer.nextGaussian());
      }

      Polynom fitted = new Polynom(fitter.fit());

      for (double x = -1.0; x < 1.0; x += 0.01) {
        double error = Math.abs(p.valueAt(x) - fitted.valueAt(x))
          / (1.0 + Math.abs(p.valueAt(x)));
        assertTrue(Math.abs(error) < 0.1);
      }
    }

  }

  public void testRedundantSolvable() {
    // Levenberg-Marquardt should handle redundant information gracefully
    checkUnsolvableProblem(new LevenbergMarquardtEstimator(), true);
  }

  public void testRedundantUnsolvable() {
    // Gauss-Newton should not be able to solve redundant information
    checkUnsolvableProblem(new GaussNewtonEstimator(10, 1.0e-7, 1.0e-7,
                                                    1.0e-10),
                           false);
  }

  private void checkUnsolvableProblem(Estimator estimator,
                                      boolean solvable) {
    Random randomizer = new Random(1248788532l);
    for (int degree = 0; degree < 10; ++degree) {
      Polynom p = new Polynom(degree);
      for (int i = 0; i <= degree; ++i) {
        p.initCoeff(i, randomizer.nextGaussian());
      }

      PolynomialFitter fitter = new PolynomialFitter(degree, estimator);

      // reusing the same point over and over again does not bring
      // information, the problem cannot be solved in this case for
      // degrees greater than 1 (but one point is sufficient for
      // degree 0)
      for (double x = -1.0; x < 1.0; x += 0.01) {
        fitter.addWeightedPair(1.0, 0.0, p.valueAt(0.0));
      }

      try {
        fitter.fit();
        assertTrue(solvable || (degree == 0));
      } catch(EstimationException e) {
        assertTrue((! solvable) && (degree > 0));
      }

    }

  }

  public static Test suite() {
    return new TestSuite(PolynomialFitterTest.class);
  }

  private static class Polynom {

    public Polynom(int degree) {
      coeffs = new double[degree + 1];
      for (int i = 0; i < coeffs.length; ++i) {
        coeffs[i] = 0.0;
      }
    }

    public Polynom(double[]coeffs) {
      this.coeffs = coeffs;
    }

    public void initCoeff(int i, double c) {
      coeffs[i] = c;
    }

    public double valueAt(double x) {
      double y = coeffs[coeffs.length - 1];
      for (int i = coeffs.length - 2; i >= 0; --i) {
        y = y * x + coeffs[i];
      }
      return y;
    }

    private double[] coeffs;

  }

}
