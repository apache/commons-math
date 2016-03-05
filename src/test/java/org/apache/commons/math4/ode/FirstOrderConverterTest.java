/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math4.ode;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MaxCountExceededException;
import org.apache.commons.math4.exception.NoBracketingException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.ode.FirstOrderConverter;
import org.apache.commons.math4.ode.SecondOrderDifferentialEquations;
import org.apache.commons.math4.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;


public class FirstOrderConverterTest {

  @Test
  public void testDoubleDimension() {
    for (int i = 1; i < 10; ++i) {
      SecondOrderDifferentialEquations eqn2 = new Equations(i, 0.2);
      FirstOrderConverter eqn1 = new FirstOrderConverter(eqn2);
      Assert.assertTrue(eqn1.getDimension() == (2 * eqn2.getDimension()));
    }
  }

  @Test
  public void testDecreasingSteps()
      throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {

    double previousError = Double.NaN;
    for (int i = 0; i < 10; ++i) {

      double step  = FastMath.pow(2.0, -(i + 1));
      double error = integrateWithSpecifiedStep(4.0, 0.0, 1.0, step)
                   - FastMath.sin(4.0);
      if (i > 0) {
        Assert.assertTrue(FastMath.abs(error) < FastMath.abs(previousError));
      }
      previousError = error;

    }
  }

  @Test
  public void testSmallStep()
      throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    double error = integrateWithSpecifiedStep(4.0, 0.0, 1.0, 1.0e-4)
                   - FastMath.sin(4.0);
    Assert.assertTrue(FastMath.abs(error) < 1.0e-10);
  }

  @Test
  public void testBigStep()
      throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    double error = integrateWithSpecifiedStep(4.0, 0.0, 1.0, 0.5)
                   - FastMath.sin(4.0);
    Assert.assertTrue(FastMath.abs(error) > 0.1);
  }

  private static class Equations
    implements SecondOrderDifferentialEquations {

     private int n;

      private double omega2;

      public Equations(int n, double omega) {
        this.n = n;
        omega2 = omega * omega;
      }

      public int getDimension() {
        return n;
      }

      public void computeSecondDerivatives(double t, double[] y, double[] yDot,
                                           double[] yDDot) {
        for (int i = 0; i < n; ++i) {
          yDDot[i] = -omega2 * y[i];
        }
    }

  }

  private double integrateWithSpecifiedStep(double omega,
                                            double t0, double t,
                                            double step) throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    double[] y0 = new double[2];
    y0[0] = FastMath.sin(omega * t0);
    y0[1] = omega * FastMath.cos(omega * t0);
    ClassicalRungeKuttaIntegrator i = new ClassicalRungeKuttaIntegrator(step);
    double[] y = new double[2];
    i.integrate(new FirstOrderConverter(new Equations(1, omega)), t0, y0, t, y);
    return y[0];
  }

}
