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

package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.sampling.StepInterpolator;

/**
 * This class represents an interpolator over the last step during an
 * ODE integration for the 5(4) Dormand-Prince integrator.
 *
 * @see DormandPrince54Integrator
 *
 * @version $Revision$ $Date$
 * @since 1.2
 */

class DormandPrince54StepInterpolator
  extends RungeKuttaStepInterpolator {

  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * {@link #reinitialize} method should be called before using the
   * instance in order to initialize the internal arrays. This
   * constructor is used only in order to delay the initialization in
   * some cases. The {@link EmbeddedRungeKuttaIntegrator} uses the
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
  public DormandPrince54StepInterpolator(final DormandPrince54StepInterpolator interpolator) {

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

  /** {@inheritDoc} */
  protected StepInterpolator doCopy() {
    return new DormandPrince54StepInterpolator(this);
  }


  /** {@inheritDoc} */
  public void reinitialize(final FirstOrderDifferentialEquations equations,
                           final double[] y, final double[][] yDotK, final boolean forward) {
    super.reinitialize(equations, y, yDotK, forward);
    v1 = null;
    v2 = null;
    v3 = null;
    v4 = null;
    vectorsInitialized = false;
  }

  /** {@inheritDoc} */
  public void storeTime(final double t) {
    super.storeTime(t);
    vectorsInitialized = false;
  }

  /** {@inheritDoc} */
  protected void computeInterpolatedState(final double theta,
                                          final double oneMinusThetaH)
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
          final double yDot0 = yDotK[0][i];
          final double yDot2 = yDotK[2][i];
          final double yDot3 = yDotK[3][i];
          final double yDot4 = yDotK[4][i];
          final double yDot5 = yDotK[5][i];
          final double yDot6 = yDotK[6][i];
          v1[i] = a70 * yDot0 + a72 * yDot2 + a73 * yDot3 + a74 * yDot4 + a75 * yDot5;
          v2[i] = yDot0 - v1[i];
          v3[i] = v1[i] - v2[i] - yDot6;
          v4[i] = d0 * yDot0 + d2 * yDot2 + d3 * yDot3 + d4 * yDot4 + d5 * yDot5 + d6 * yDot6;
      }

      vectorsInitialized = true;

    }

    // interpolate
    final double eta = 1 - theta;
    final double twoTheta = 2 * theta;
    final double dot2 = 1 - twoTheta;
    final double dot3 = theta * (2 - 3 * theta);
    final double dot4 = twoTheta * (1 + theta * (twoTheta - 3));
    for (int i = 0; i < interpolatedState.length; ++i) {
      interpolatedState[i] =
          currentState[i] - oneMinusThetaH * (v1[i] - theta * (v2[i] + theta * (v3[i] + eta * v4[i])));
      interpolatedDerivatives[i] = v1[i] + dot2 * v2[i] + dot3 * v3[i] + dot4 * v4[i];
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

  /** Last row of the Butcher-array internal weights, element 0. */
  private static final double a70 =    35.0 /  384.0;

  // element 1 is zero, so it is neither stored nor used

  /** Last row of the Butcher-array internal weights, element 2. */
  private static final double a72 =   500.0 / 1113.0;

  /** Last row of the Butcher-array internal weights, element 3. */
  private static final double a73 =   125.0 /  192.0;

  /** Last row of the Butcher-array internal weights, element 4. */
  private static final double a74 = -2187.0 / 6784.0;

  /** Last row of the Butcher-array internal weights, element 5. */
  private static final double a75 =    11.0 /   84.0;

  /** Shampine (1986) Dense output, element 0. */
  private static final double d0 =  -12715105075.0 /  11282082432.0;

  // element 1 is zero, so it is neither stored nor used

  /** Shampine (1986) Dense output, element 2. */
  private static final double d2 =   87487479700.0 /  32700410799.0;

  /** Shampine (1986) Dense output, element 3. */
  private static final double d3 =  -10690763975.0 /   1880347072.0;

  /** Shampine (1986) Dense output, element 4. */
  private static final double d4 =  701980252875.0 / 199316789632.0;

  /** Shampine (1986) Dense output, element 5. */
  private static final double d5 =   -1453857185.0 /    822651844.0;

  /** Shampine (1986) Dense output, element 6. */
  private static final double d6 =      69997945.0 /     29380423.0;

  /** Serializable version identifier */
  private static final long serialVersionUID = 4104157279605906956L;

}
