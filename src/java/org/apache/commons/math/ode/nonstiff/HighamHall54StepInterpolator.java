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
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.StepInterpolator;

/**
 * This class represents an interpolator over the last step during an
 * ODE integration for the 5(4) Higham and Hall integrator.
 *
 * @see HighamHall54Integrator
 *
 * @version $Revision$ $Date$
 * @since 1.2
 */

class HighamHall54StepInterpolator
  extends RungeKuttaStepInterpolator {

  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * {@link AbstractStepInterpolator#reinitialize} method should be called
   * before using the instance in order to initialize the internal arrays. This
   * constructor is used only in order to delay the initialization in
   * some cases. The {@link EmbeddedRungeKuttaIntegrator} uses the
   * prototyping design pattern to create the step interpolators by
   * cloning an uninitialized model and latter initializing the copy.
   */
  public HighamHall54StepInterpolator() {
    super();
  }

  /** Copy constructor.
   * @param interpolator interpolator to copy from. The copy is a deep
   * copy: its arrays are separated from the original arrays of the
   * instance
   */
  public HighamHall54StepInterpolator(final HighamHall54StepInterpolator interpolator) {
    super(interpolator);
  }

  /** {@inheritDoc} */
  protected StepInterpolator doCopy() {
    return new HighamHall54StepInterpolator(this);
  }


  /** {@inheritDoc} */
  protected void computeInterpolatedState(final double theta,
                                          final double oneMinusThetaH)
    throws DerivativeException {

    final double theta2 = theta * theta;

    final double b0 = h * (-1.0/12.0 + theta * (1.0 + theta * (-15.0/4.0 + theta * (16.0/3.0 + theta * -5.0/2.0))));
    final double b2 = h * (-27.0/32.0 + theta2 * (459.0/32.0 + theta * (-243.0/8.0 + theta * 135.0/8.0)));
    final double b3 = h * (4.0/3.0 + theta2 * (-22.0 + theta * (152.0/3.0  + theta * -30.0)));
    final double b4 = h * (-125.0/96.0 + theta2 * (375.0/32.0 + theta * (-625.0/24.0 + theta * 125.0/8.0)));
    final double b5 = h * (-5.0/48.0 + theta2 * (-5.0/16.0 + theta * 5.0/12.0));

    for (int i = 0; i < interpolatedState.length; ++i) {
      interpolatedState[i] = currentState[i] +
                             b0 * yDotK[0][i] + b2 * yDotK[2][i] + b3 * yDotK[3][i] +
                             b4 * yDotK[4][i] + b5 * yDotK[5][i];
    }

  }

  /** Serializable version identifier */
  private static final long serialVersionUID = -3583240427587318654L;

}
