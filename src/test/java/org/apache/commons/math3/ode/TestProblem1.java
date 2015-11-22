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

package org.apache.commons.math3.ode;

import org.apache.commons.math3.util.FastMath;

/**
 * This class is used in the junit tests for the ODE integrators.

 * <p>This specific problem is the following differential equation :
 * <pre>
 *    y' = -y
 * </pre>
 * the solution of this equation is a simple exponential function :
 * <pre>
 *   y (t) = y (t0) exp (t0-t)
 * </pre>
 * </p>

 */
public class TestProblem1
  extends TestProblemAbstract {

  /** theoretical state */
  private double[] y;

  /**
   * Simple constructor.
   */
  public TestProblem1() {
    super();
    double[] y0 = { 1.0, 0.1 };
    setInitialConditions(0.0, y0);
    setFinalConditions(4.0);
    double[] errorScale = { 1.0, 1.0 };
    setErrorScale(errorScale);
    y = new double[y0.length];
  }

  @Override
  public void doComputeDerivatives(double t, double[] y, double[] yDot) {

    // compute the derivatives
    for (int i = 0; i < getDimension(); ++i)
      yDot[i] = -y[i];

  }

  @Override
  public double[] computeTheoreticalState(double t) {
    double c = FastMath.exp (getInitialTime() - t);
    for (int i = 0; i < getDimension(); ++i) {
      y[i] = c * getInitialState()[i];
    }
    return y;
  }

}
