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

package org.apache.commons.math4.legacy.ode;

import org.apache.commons.math4.legacy.ode.events.EventHandler;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * This class is used in the junit tests for the ODE integrators.

 * <p>This specific problem is the following differential equation :
 * <pre>
 *    x'' = -x
 * </pre>
 * And when x decreases down to 0, the state should be changed as follows :
 * <pre>
 *   x' -> -x'
 * </pre>
 * The theoretical solution of this problem is x = |sin(t+a)|
 * </p>

 */
public class TestProblem4
  extends TestProblemAbstract {

  /** Time offset. */
  private double a;

  /** theoretical state */
  private double[] y;

  /** Simple constructor. */
  public TestProblem4() {
    super();
    a = 1.2;
    double[] y0 = { JdkMath.sin(a), JdkMath.cos(a) };
    setInitialConditions(0.0, y0);
    setFinalConditions(15);
    double[] errorScale = { 1.0, 0.0 };
    setErrorScale(errorScale);
    y = new double[y0.length];
  }

  @Override
  public EventHandler[] getEventsHandlers() {
    return new EventHandler[] { new Bounce(), new Stop() };
  }

  /**
   * Get the theoretical events times.
   * @return theoretical events times
   */
  @Override
  public double[] getTheoreticalEventsTimes() {
      return new double[] {
          1 * JdkMath.PI - a,
          2 * JdkMath.PI - a,
          3 * JdkMath.PI - a,
          4 * JdkMath.PI - a,
          12.0
      };
  }

  @Override
  public void doComputeDerivatives(double t, double[] y, double[] yDot) {
    yDot[0] =  y[1];
    yDot[1] = -y[0];
  }

  @Override
  public double[] computeTheoreticalState(double t) {
    double sin = JdkMath.sin(t + a);
    double cos = JdkMath.cos(t + a);
    y[0] = JdkMath.abs(sin);
    y[1] = (sin >= 0) ? cos : -cos;
    return y;
  }

  private static class Bounce implements EventHandler {

    private int sign;

    Bounce() {
      sign = +1;
    }

    @Override
    public void init(double t0, double[] y0, double t) {
    }

    @Override
    public double g(double t, double[] y) {
      return sign * y[0];
    }

    @Override
    public Action eventOccurred(double t, double[] y, boolean increasing) {
      // this sign change is needed because the state will be reset soon
      sign = -sign;
      return Action.RESET_STATE;
    }

    @Override
    public void resetState(double t, double[] y) {
      y[0] = -y[0];
      y[1] = -y[1];
    }

  }

  private static class Stop implements EventHandler {

    Stop() {
    }

    @Override
    public void init(double t0, double[] y0, double t) {
    }

    @Override
    public double g(double t, double[] y) {
      return t - 12.0;
    }

    @Override
    public Action eventOccurred(double t, double[] y, boolean increasing) {
      return Action.STOP;
    }

    @Override
    public void resetState(double t, double[] y) {
    }

  }

}
