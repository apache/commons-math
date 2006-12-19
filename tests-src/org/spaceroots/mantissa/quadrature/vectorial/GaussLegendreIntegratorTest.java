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

package org.spaceroots.mantissa.quadrature.vectorial;

import org.spaceroots.mantissa.functions.vectorial.ComputableFunction;
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
          double[] s0 = integrator.integrate(p, -5.0, 15.0);
          double[] s1 = p.exactIntegration(-5.0, 15.0);
          for (int j = 0; j < p.getDimension(); ++j) {
            assertTrue(Math.abs(s0[j] - s1[j]) < 1.0e-12 * (1.0 + Math.abs(s0[j])));
          }
        }
      }

      ++order;

    }
  }

  public static Test suite() {
    return new TestSuite(GaussLegendreIntegratorTest.class);
  }

  private static class Polynom implements ComputableFunction {
    public Polynom (int degree, Random random, double max) {
      coeffs0 = new double[degree + 1];
      coeffs1 = new double[degree + 1];
      for (int i = 0; i <= degree; ++i) {
        coeffs0[i] = 2.0 * max * (random.nextDouble() - 0.5);
        coeffs1[i] = 2.0 * max * (random.nextDouble() - 0.5);
      }
    }

    public int getDimension() {
      return 2;
    }

    public double[] valueAt(double t)
      throws FunctionException {
      double[] y = new double[2];
      y[0] = coeffs0[coeffs0.length - 1];
      for (int i = coeffs0.length - 2; i >= 0; --i) {
        y[0] = y[0] * t + coeffs0[i];
      }
      y[1] = coeffs1 [coeffs1.length - 1];
      for (int i = coeffs1.length - 2; i >= 0; --i) {
        y[1] = y[1] * t + coeffs1[i];
      }
      return y;
    }

    public double[] exactIntegration(double a, double b)
      throws FunctionException {
      double[] res = new double[2];
      double yb = coeffs0[coeffs0.length - 1] / coeffs0.length;
      double ya = yb;
      for (int i = coeffs0.length - 2; i >= 0; --i) {
        yb = yb * b + coeffs0[i] / (i + 1);
        ya = ya * a + coeffs0[i] / (i + 1);
      }
      res[0] = yb * b - ya * a;
      yb = coeffs1[coeffs1.length - 1] / coeffs1.length;
      ya = yb;
      for (int i = coeffs1.length - 2; i >= 0; --i) {
        yb = yb * b + coeffs1[i] / (i + 1);
        ya = ya * a + coeffs1[i] / (i + 1);
      }
      res[1] = yb * b - ya * a;
      return res;
    }

    private double[] coeffs0;
    private double[] coeffs1;

    private static final long serialVersionUID = -8032020368915042278L;

  }

}
