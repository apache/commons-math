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

package org.apache.commons.math4.ode.nonstiff;

import org.apache.commons.math4.ode.sampling.StepInterpolator;
import org.apache.commons.math4.util.FastMath;

/**
 * This class implements a step interpolator for the Gill fourth
 * order Runge-Kutta integrator.
 *
 * <p>This interpolator allows to compute dense output inside the last
 * step computed. The interpolation equation is consistent with the
 * integration scheme :
 * <ul>
 *   <li>Using reference point at step start:<br>
 *   y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub>)
 *                    + &theta; (h/6) [ (6 - 9 &theta; + 4 &theta;<sup>2</sup>) y'<sub>1</sub>
 *                                    + (    6 &theta; - 4 &theta;<sup>2</sup>) ((1-1/&radic;2) y'<sub>2</sub> + (1+1/&radic;2)) y'<sub>3</sub>)
 *                                    + (  - 3 &theta; + 4 &theta;<sup>2</sup>) y'<sub>4</sub>
 *                                    ]
 *   </li>
 *   <li>Using reference point at step start:<br>
 *   y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub> + h)
 *                    - (1 - &theta;) (h/6) [ (1 - 5 &theta; + 4 &theta;<sup>2</sup>) y'<sub>1</sub>
 *                                          + (2 + 2 &theta; - 4 &theta;<sup>2</sup>) ((1-1/&radic;2) y'<sub>2</sub> + (1+1/&radic;2)) y'<sub>3</sub>)
 *                                          + (1 +   &theta; + 4 &theta;<sup>2</sup>) y'<sub>4</sub>
 *                                          ]
 *   </li>
 * </ul>
 * </p>
 * where &theta; belongs to [0 ; 1] and where y'<sub>1</sub> to y'<sub>4</sub>
 * are the four evaluations of the derivatives already computed during
 * the step.</p>
 *
 * @see GillIntegrator
 * @since 1.2
 */

class GillStepInterpolator
  extends RungeKuttaStepInterpolator {

    /** First Gill coefficient. */
    private static final double ONE_MINUS_INV_SQRT_2 = 1 - FastMath.sqrt(0.5);

    /** Second Gill coefficient. */
    private static final double ONE_PLUS_INV_SQRT_2 = 1 + FastMath.sqrt(0.5);

    /** Serializable version identifier. */
    private static final long serialVersionUID = 20111120L;

  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * {@link
   * org.apache.commons.math4.ode.sampling.AbstractStepInterpolator#reinitialize}
   * method should be called before using the instance in order to
   * initialize the internal arrays. This constructor is used only
   * in order to delay the initialization in some cases. The {@link
   * RungeKuttaIntegrator} class uses the prototyping design pattern
   * to create the step interpolators by cloning an uninitialized model
   * and later initializing the copy.
   */
  // CHECKSTYLE: stop RedundantModifier
  // the public modifier here is needed for serialization
  public GillStepInterpolator() {
  }
  // CHECKSTYLE: resume RedundantModifier

  /** Copy constructor.
   * @param interpolator interpolator to copy from. The copy is a deep
   * copy: its arrays are separated from the original arrays of the
   * instance
   */
  GillStepInterpolator(final GillStepInterpolator interpolator) {
    super(interpolator);
  }

  /** {@inheritDoc} */
  @Override
  protected StepInterpolator doCopy() {
    return new GillStepInterpolator(this);
  }


  /** {@inheritDoc} */
  @Override
  protected void computeInterpolatedStateAndDerivatives(final double theta,
                                          final double oneMinusThetaH) {

    final double twoTheta   = 2 * theta;
    final double fourTheta2 = twoTheta * twoTheta;
    final double coeffDot1  = theta * (twoTheta - 3) + 1;
    final double cDot23     = twoTheta * (1 - theta);
    final double coeffDot2  = cDot23  * ONE_MINUS_INV_SQRT_2;
    final double coeffDot3  = cDot23  * ONE_PLUS_INV_SQRT_2;
    final double coeffDot4  = theta * (twoTheta - 1);

    if ((previousState != null) && (theta <= 0.5)) {
        final double s         = theta * h / 6.0;
        final double c23       = s * (6 * theta - fourTheta2);
        final double coeff1    = s * (6 - 9 * theta + fourTheta2);
        final double coeff2    = c23  * ONE_MINUS_INV_SQRT_2;
        final double coeff3    = c23  * ONE_PLUS_INV_SQRT_2;
        final double coeff4    = s * (-3 * theta + fourTheta2);
        for (int i = 0; i < interpolatedState.length; ++i) {
            final double yDot1 = yDotK[0][i];
            final double yDot2 = yDotK[1][i];
            final double yDot3 = yDotK[2][i];
            final double yDot4 = yDotK[3][i];
            interpolatedState[i] =
                    previousState[i] + coeff1 * yDot1 + coeff2 * yDot2 + coeff3 * yDot3 + coeff4 * yDot4;
            interpolatedDerivatives[i] =
                    coeffDot1 * yDot1 + coeffDot2 * yDot2 + coeffDot3 * yDot3 + coeffDot4 * yDot4;
        }
    } else {
        final double s      = oneMinusThetaH / 6.0;
        final double c23    = s * (2 + twoTheta - fourTheta2);
        final double coeff1 = s * (1 - 5 * theta + fourTheta2);
        final double coeff2 = c23  * ONE_MINUS_INV_SQRT_2;
        final double coeff3 = c23  * ONE_PLUS_INV_SQRT_2;
        final double coeff4 = s * (1 + theta + fourTheta2);
        for (int i = 0; i < interpolatedState.length; ++i) {
            final double yDot1 = yDotK[0][i];
            final double yDot2 = yDotK[1][i];
            final double yDot3 = yDotK[2][i];
            final double yDot4 = yDotK[3][i];
            interpolatedState[i] =
                    currentState[i] - coeff1 * yDot1 - coeff2 * yDot2 - coeff3 * yDot3 - coeff4 * yDot4;
            interpolatedDerivatives[i] =
                    coeffDot1 * yDot1 + coeffDot2 * yDot2 + coeffDot3 * yDot3 + coeffDot4 * yDot4;
        }
    }

  }

}
