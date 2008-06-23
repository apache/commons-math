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

package org.spaceroots.mantissa.fitting;

import org.spaceroots.mantissa.estimation.EstimatedParameter;
import org.spaceroots.mantissa.estimation.EstimationException;
import org.spaceroots.mantissa.estimation.Estimator;
import org.spaceroots.mantissa.estimation.GaussNewtonEstimator;
import org.spaceroots.mantissa.functions.ExhaustedSampleException;
import org.spaceroots.mantissa.functions.FunctionException;

/** This class implements a curve fitting specialized for sinusoids.

 * <p>Harmonic fitting is a very simple case of curve fitting. The
 * estimated coefficients are the amplitude a, the pulsation omega and
 * the phase phi: <code>f (t) = a cos (omega t + phi)</code>. They are
 * searched by a least square estimator initialized with a rough guess
 * based on integrals.</p>

 * <p>This class <emph>is by no means optimized</emph>, neither versus
 * space nor versus time performance.</p>

 * @version $Id$
 * @author L. Maisonobe

 */

public class HarmonicFitter
  extends AbstractCurveFitter {

  /** Simple constructor.
   * @param estimator estimator to use for the fitting
   */
  public HarmonicFitter(Estimator estimator) {
    super(3, estimator);
    coefficients[0]  = new EstimatedParameter("a", 2.0 * Math.PI);
    coefficients[1]  = new EstimatedParameter("omega", 0.0);
    coefficients[2]  = new EstimatedParameter("phi", 0.0);
    firstGuessNeeded = true;
  }

  /**
   * Simple constructor.

   * <p>This constructor can be used when a first estimate of the
   * coefficients is already known.</p>

   * @param coefficients first estimate of the coefficients.
   * A reference to this array is hold by the newly created
   * object. Its elements will be adjusted during the fitting process
   * and they will be set to the adjusted coefficients at the end.
   * @param estimator estimator to use for the fitting

   */
  public HarmonicFitter(EstimatedParameter[] coefficients,
                        Estimator estimator) {
    super(coefficients, estimator);
    firstGuessNeeded = false;
  }

  public double[] fit()
    throws EstimationException {
    if (firstGuessNeeded) {
      if (measurements.size() < 4) {
        throw new EstimationException("sample must contain at least {0} points",
                                      new String[] {
                                        Integer.toString(4)
                                      });
      }

      sortMeasurements();

      try {
        HarmonicCoefficientsGuesser guesser =
          new HarmonicCoefficientsGuesser((FitMeasurement[]) getMeasurements());
        guesser.guess();

        coefficients[0].setEstimate(guesser.getA());
        coefficients[1].setEstimate(guesser.getOmega());
        coefficients[2].setEstimate(guesser.getPhi());
      } catch(ExhaustedSampleException e) {
        throw new EstimationException(e);
      } catch(FunctionException e) {
        throw new EstimationException(e);
      }

      firstGuessNeeded = false;

    }

    return super.fit();

  }

  /** Get the current amplitude coefficient estimate.
   * Get a, where <code>f (t) = a cos (omega t + phi)</code>
   * @return current amplitude coefficient estimate
   */
  public double getAmplitude() {
    return coefficients[0].getEstimate();
  }

  /** Get the current pulsation coefficient estimate.
   * Get omega, where <code>f (t) = a cos (omega t + phi)</code>
   * @return current pulsation coefficient estimate
   */
  public double getPulsation() {
    return coefficients[1].getEstimate();
  }

  /** Get the current phase coefficient estimate.
   * Get phi, where <code>f (t) = a cos (omega t + phi)</code>
   * @return current phase coefficient estimate
   */
  public double getPhase() {
    return coefficients[2].getEstimate();
  }

  /** Get the value of the function at x according to the current parameters value.
   * @param x abscissa at which the theoretical value is requested
   * @return theoretical value at x
   */
  public double valueAt(double x) {
    double a     = coefficients[0].getEstimate();
    double omega = coefficients[1].getEstimate();
    double phi   = coefficients[2].getEstimate();
    return a * Math.cos(omega * x + phi);
  }

  /** Get the derivative of the function at x with respect to parameter p.
   * @param x abscissa at which the partial derivative is requested
   * @param p parameter with respect to which the derivative is requested
   * @return partial derivative
   */
  public double partial(double x, EstimatedParameter p) {
    double a     = coefficients[0].getEstimate();
    double omega = coefficients[1].getEstimate();
    double phi   = coefficients[2].getEstimate();
    if (p == coefficients[0]) {
      return Math.cos(omega * x + phi);
    } else if (p == coefficients[1]) {
      return -a * x * Math.sin(omega * x + phi);
    } else {
      return -a * Math.sin(omega * x + phi);
    }
  }

  /** Indicator of the need to compute a first guess of the coefficients. */
  private boolean firstGuessNeeded;

  private static final long serialVersionUID = -8722683066277473450L;

}
