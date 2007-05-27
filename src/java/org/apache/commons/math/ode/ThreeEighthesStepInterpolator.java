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
 * This class implements a step interpolator for the 3/8 fourth
 * order Runge-Kutta integrator.

 * <p>This interpolator allows to compute dense output inside the last
 * step computed. The interpolation equation is consistent with the
 * integration scheme :

 * <pre>
 *   y(t_n + theta h) = y (t_n + h)
 *                    - (1 - theta) (h/8) [ (1 - 7 theta + 8 theta^2) y'_1
 *                                      + 3 (1 +   theta - 4 theta^2) y'_2
 *                                      + 3 (1 +   theta)             y'_3
 *                                      +   (1 +   theta + 4 theta^2) y'_4
 *                                        ]
 * </pre>

 * where theta belongs to [0 ; 1] and where y'_1 to y'_4 are the four
 * evaluations of the derivatives already computed during the
 * step.</p>

 * @see ThreeEighthesIntegrator

 * @version $Id: ThreeEighthesStepInterpolator.java 1705 2006-09-17 19:57:39Z luc $

 */

class ThreeEighthesStepInterpolator
  extends RungeKuttaStepInterpolator {
    
  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * {@link AbstractStepInterpolator#reinitialize} method should be called
   * before using the instance in order to initialize the internal arrays. This
   * constructor is used only in order to delay the initialization in
   * some cases. The {@link RungeKuttaIntegrator} class uses the
   * prototyping design pattern to create the step interpolators by
   * cloning an uninitialized model and latter initializing the copy.
   */
  public ThreeEighthesStepInterpolator() {
  }

  /** Copy constructor.
   * @param interpolator interpolator to copy from. The copy is a deep
   * copy: its arrays are separated from the original arrays of the
   * instance
   */
  public ThreeEighthesStepInterpolator(ThreeEighthesStepInterpolator interpolator) {
    super(interpolator);
  }

  /**
   * Clone the instance.
   * the copy is a deep copy: its arrays are separated from the
   * original arrays of the instance
   * @return a copy of the instance
   */
  public Object clone() {
    return new ThreeEighthesStepInterpolator(this);
  }

  /** Compute the state at the interpolated time.
   * This is the main processing method that should be implemented by
   * the derived classes to perform the interpolation.
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

    double fourTheta2 = 4 * theta * theta;
    double s          = oneMinusThetaH / 8.0;
    double coeff1     = s * (1 - 7 * theta + 2 * fourTheta2);
    double coeff2     = 3 * s * (1 + theta - fourTheta2);
    double coeff3     = 3 * s * (1 + theta);
    double coeff4     = s * (1 + theta + fourTheta2);

    for (int i = 0; i < interpolatedState.length; ++i) {
      interpolatedState[i] = currentState[i]
                            - coeff1 * yDotK[0][i] - coeff2 * yDotK[1][i]
                            - coeff3 * yDotK[2][i] - coeff4 * yDotK[3][i];
     }

  }

  private static final long serialVersionUID = -3345024435978721931L;

}
