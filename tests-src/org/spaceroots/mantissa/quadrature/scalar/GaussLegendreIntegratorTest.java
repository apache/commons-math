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

package org.spaceroots.mantissa.quadrature.scalar;

import org.spaceroots.mantissa.functions.scalar.ComputableFunction;
import org.spaceroots.mantissa.functions.FunctionException;

import java.util.Random;

import junit.framework.*;

public class GaussLegendreIntegratorTest
  extends TestCase {

  public GaussLegendreIntegratorTest(String name) {
    super(name);
  }

  public void testExactIntegration()
    throws FunctionException {
    Random random = new Random(86343623467878363l);
    int order = 0;
    while (true) {
      GaussLegendreIntegrator integrator = new GaussLegendreIntegrator(order,
                                                                       7.0);
      int availableOrder = integrator.getEvaluationsPerStep();
      if (availableOrder < order) {
        // we have tested all available orders
        return;
      }

      // an order n Gauss-Legendre integrator integrates
      // 2n-1 degree polynoms exactly
      for (int degree = 0; degree <= 2 * availableOrder - 1; ++degree) {
        for (int i = 0; i < 10; ++i) {
          Polynom p = new Polynom(degree, random, 100.0);
          double s0 = integrator.integrate(p, -5.0, 15.0);
          double s1 = p.exactIntegration(-5.0, 15.0);
          assertTrue(Math.abs(s0 - s1) < 1.0e-12 * (1.0 + Math.abs(s0)));
        }
      }

      ++order;

    }
  }

  public static Test suite() {
    return new TestSuite(GaussLegendreIntegratorTest.class);
  }

  private static class Polynom implements ComputableFunction {

    public Polynom(int degree, Random random, double max) {
      coeffs = new double[degree + 1];
      for (int i = 0; i <= degree; ++i) {
        coeffs[i] = 2.0 * max * (random.nextDouble() - 0.5);
      }
    }

    public double valueAt(double t)
      throws FunctionException {
      double y = coeffs[coeffs.length - 1];
      for (int i = coeffs.length - 2; i >= 0; --i) {
        y = y * t + coeffs[i];
      }
      return y;
    }

    public double exactIntegration(double a, double b)
      throws FunctionException {
      double yb = coeffs[coeffs.length - 1] / coeffs.length;
      double ya = yb;
      for (int i = coeffs.length - 2; i >= 0; --i) {
        yb = yb * b + coeffs[i] / (i + 1);
        ya = ya * a + coeffs[i] / (i + 1);
      }
      return yb * b - ya * a;
    }

    private double[] coeffs;

     private static final long serialVersionUID = -7304282612679254557L;

  }

}
