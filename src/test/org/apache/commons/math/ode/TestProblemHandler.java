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

package org.apache.commons.math.ode;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.StepHandler;
import org.apache.commons.math.ode.StepInterpolator;

/**
 * This class is used to handle steps for the test problems
 * integrated during the junit tests for the ODE integrators.
 */
class TestProblemHandler
  implements StepHandler {

  /** Associated problem. */
  private TestProblemAbstract problem;

  /** Maximal error encountered during the integration. */
  private double maxError;

  /** Error at the end of the integration. */
  private double lastError;

  /** Time at the end of integration. */
  private double lastTime;

  /**
   * Simple constructor.
   * @param problem problem for which steps should be handled
   */
  public TestProblemHandler(TestProblemAbstract problem) {
    this.problem = problem;
    reset();
  }

  public boolean requiresDenseOutput() {
    return true;
  }

  public void reset() {
    maxError  = 0;
    lastError = 0;
  }

  public void handleStep(StepInterpolator interpolator,
                         boolean isLast)
    throws DerivativeException {

    double pT = interpolator.getPreviousTime();
    double cT = interpolator.getCurrentTime();
    double[] errorScale = problem.getErrorScale();

    // store the error at the last step
    if (isLast) {
      double[] interpolatedY = interpolator.getInterpolatedState();
      double[] theoreticalY  = problem.computeTheoreticalState(cT);
      for (int i = 0; i < interpolatedY.length; ++i) {
        double error = Math.abs(interpolatedY[i] - theoreticalY[i]);
        if (error > lastError) {
          lastError = error;
        }
      }
      lastTime = cT;
    }

    // walk through the step
    for (int k = 0; k <= 20; ++k) {

      double time = pT + (k * (cT - pT)) / 20;
      interpolator.setInterpolatedTime(time);
      double[] interpolatedY = interpolator.getInterpolatedState();
      double[] theoreticalY  = problem.computeTheoreticalState(interpolator.getInterpolatedTime());

      // update the errors
      for (int i = 0; i < interpolatedY.length; ++i) {
        double error = errorScale[i] * Math.abs(interpolatedY[i] - theoreticalY[i]);
        if (error > maxError) {
          maxError = error;
        }
      }

    }
  }

  /**
   * Get the maximal error encountered during integration.
   * @return maximal error
   */
  public double getMaximalError() {
    return maxError;
  }

  /**
   * Get the error at the end of the integration.
   * @return error at the end of the integration
   */
  public double getLastError() {
    return lastError;
  }

  /**
   * Get the time at the end of the integration.
   * @return time at the end of the integration.
   */
  public double getLastTime() {
    return lastTime;
  }

}
