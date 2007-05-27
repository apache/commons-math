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

/**
 * This class represents an interpolator over the last step during an
 * ODE integration for the 5(4) Dormand-Prince integrator.
 *
 * @see DormandPrince54Integrator
 *
 * @version $Id: DormandPrince54StepInterpolator.java 1705 2006-09-17 19:57:39Z luc $
 *
 */

class DormandPrince54StepInterpolator
  extends RungeKuttaStepInterpolator {

  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * {@link #reinitialize} method should be called before using the
   * instance in order to initialize the internal arrays. This
   * constructor is used only in order to delay the initialization in
   * some cases. The {@link RungeKuttaFehlbergIntegrator} uses the
   * prototyping design pattern to create the step interpolators by
   * cloning an uninitialized model and latter initializing the copy.
   */
  public DormandPrince54StepInterpolator() {
    super();
    v1 = null;
    v2 = null;
    v3 = null;
    v4 = null;
    vectorsInitialized = false;
  }

  /** Copy constructor.
   * @param interpolator interpolator to copy from. The copy is a deep
   * copy: its arrays are separated from the original arrays of the
   * instance
   */
  public DormandPrince54StepInterpolator(DormandPrince54StepInterpolator interpolator) {

    super(interpolator);

    if (interpolator.v1 == null) {

      v1 = null;
      v2 = null;
      v3 = null;
      v4 = null;
      vectorsInitialized = false;

    } else {

      v1 = (double[]) interpolator.v1.clone();
      v2 = (double[]) interpolator.v2.clone();
      v3 = (double[]) interpolator.v3.clone();
      v4 = (double[]) interpolator.v4.clone();
      vectorsInitialized = interpolator.vectorsInitialized;

    }

  }

  /**
   * Clone the instance.
   * the copy is a deep copy: its arrays are separated from the
   * original arrays of the instance
   * @return a copy of the instance
   */
  public Object clone() {
    return new DormandPrince54StepInterpolator(this);
  }

  /** Reinitialize the instance
   * @param equations set of differential equations being integrated
   * @param y reference to the integrator array holding the state at
   * the end of the step
   * @param yDotK reference to the integrator array holding all the
   * intermediate slopes
   * @param forward integration direction indicator
   */
  public void reinitialize(FirstOrderDifferentialEquations equations,
                           double[] y, double[][] yDotK, boolean forward) {
    super.reinitialize(equations, y, yDotK, forward);
    v1 = null;
    v2 = null;
    v3 = null;
    v4 = null;
    vectorsInitialized = false;
  }

  /** Store the current step time.
   * @param t current time
   */
  public void storeTime(double t) {
    super.storeTime(t);
    vectorsInitialized = false;
  }

  /** Compute the state at the interpolated time.
   * @param theta normalized interpolation abscissa within the step
   * (theta is zero at the previous time step and one at the current time step)
   * @param oneMinusThetaH time gap between the interpolated time and
   * the current time
   * @throws DerivativeException this exception is propagated to the caller if the
   * underlying user function triggers one
   */
  protected void computeInterpolatedState(double theta,
                                          double oneMinusThetaH)
    throws DerivativeException {

    if (! vectorsInitialized) {

      if (v1 == null) {
        v1 = new double[interpolatedState.length];
        v2 = new double[interpolatedState.length];
        v3 = new double[interpolatedState.length];
        v4 = new double[interpolatedState.length];
      }

      // no step finalization is needed for this interpolator

      // we need to compute the interpolation vectors for this time step
      for (int i = 0; i < interpolatedState.length; ++i) {
        v1[i] = h * (a70 * yDotK[0][i] + a72 * yDotK[2][i] + a73 * yDotK[3][i]
                     + a74 * yDotK[4][i] + a75 * yDotK[5][i]);
        v2[i] = h * yDotK[0][i] - v1[i];
        v3[i] = v1[i] - v2[i] - h * yDotK[6][i];
        v4[i] = h * (d0 * yDotK[0][i] + d2 * yDotK[2][i] + d3 * yDotK[3][i]
                     + d4 * yDotK[4][i] + d5 * yDotK[5][i] + d6 * yDotK[6][i]);
      }

      vectorsInitialized = true;

    }

    // interpolate
    double eta = oneMinusThetaH / h;
    for (int i = 0; i < interpolatedState.length; ++i) {
      interpolatedState[i] = currentState[i]
                           - eta * (v1[i]
                                    - theta * (v2[i]
                                               + theta * (v3[i]
                                                          + eta * v4[i])));
    }

  }

  /** First vector for interpolation. */
  private double[] v1;

  /** Second vector for interpolation. */
  private double[] v2;

  /** Third vector for interpolation. */
  private double[] v3;

  /** Fourth vector for interpolation. */
  private double[] v4;

  /** Initialization indicator for the interpolation vectors. */
  private boolean vectorsInitialized;

  // last row of the Butcher-array internal weights, note that a71 is null
  private static final double a70 =    35.0 /  384.0;
  private static final double a72 =   500.0 / 1113.0;
  private static final double a73 =   125.0 /  192.0;
  private static final double a74 = -2187.0 / 6784.0;
  private static final double a75 =    11.0 /   84.0;

  // dense output of Shampine (1986), note that d1 is null
  private static final double d0 =  -12715105075.0 /  11282082432.0;
  private static final double d2 =   87487479700.0 /  32700410799.0;
  private static final double d3 =  -10690763975.0 /   1880347072.0;
  private static final double d4 =  701980252875.0 / 199316789632.0;
  private static final double d5 =   -1453857185.0 /    822651844.0;
  private static final double d6 =      69997945.0 /     29380423.0;

  private static final long serialVersionUID = 4104157279605906956L;
}
