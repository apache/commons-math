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

import java.io.Serializable;

import org.spaceroots.mantissa.functions.FunctionException;
import org.spaceroots.mantissa.functions.ExhaustedSampleException;
import org.spaceroots.mantissa.functions.vectorial.SampledFunctionIterator;
import org.spaceroots.mantissa.functions.vectorial.VectorialValuedPair;

import org.spaceroots.mantissa.quadrature.vectorial.EnhancedSimpsonIntegratorSampler;

import org.spaceroots.mantissa.estimation.EstimationException;

/** This class guesses harmonic coefficients from a sample.

 * <p>The algorithm used to guess the coefficients is as follows:</p>

 * <p>We know f (t) at some sampling points ti and want to find a,
 * omega and phi such that f (t) = a cos (omega t + phi).
 * </p>
 *
 * <p>From the analytical expression, we can compute two primitives :
 * <pre>
 *     If2  (t) = int (f^2)  = a^2 * [t + S (t)] / 2
 *     If'2 (t) = int (f'^2) = a^2 * omega^2 * [t - S (t)] / 2
 *     where S (t) = sin (2 * (omega * t + phi)) / (2 * omega)
 * </pre>
 * </p>
 *
 * <p>We can remove S between these expressions :
 * <pre>
 *     If'2 (t) = a^2 * omega ^ 2 * t - omega ^ 2 * If2 (t)
 * </pre>
 * </p>
 *
 * <p>The preceding expression shows that If'2 (t) is a linear
 * combination of both t and If2 (t): If'2 (t) = A * t + B * If2 (t)
 * </p>
 *
 * <p>From the primitive, we can deduce the same form for definite
 * integrals between t1 and ti for each ti :
 * <pre>
 *   If2 (ti) - If2 (t1) = A * (ti - t1) + B * (If2 (ti) - If2 (t1))
 * </pre>
 * </p>
 *
 * <p>We can find the coefficients A and B that best fit the sample
 * to this linear expression by computing the definite integrals for
 * each sample points.
 * </p>
 *
 * <p>For a bilinear expression z (xi, yi) = A * xi + B * yi, the
 * coefficients a and b that minimize a least square criterion
 * Sum ((zi - z (xi, yi))^2) are given by these expressions:</p>
 * <pre>
 *
 *         Sum (yi^2) Sum (xi zi) - Sum (xi yi) Sum (yi zi)
 *     A = ------------------------------------------------
 *         Sum (xi^2) Sum (yi^2)  - Sum (xi yi) Sum (xi yi)
 *
 *         Sum (xi^2) Sum (yi zi) - Sum (xi yi) Sum (xi zi)
 *     B = ------------------------------------------------
 *         Sum (xi^2) Sum (yi^2)  - Sum (xi yi) Sum (xi yi)
 * </pre>
 * </p>
 *
 *
 * <p>In fact, we can assume both a and omega are positive and
 * compute them directly, knowing that A = a^2 * omega^2 and that
 * B = - omega^2. The complete algorithm is therefore:</p>
 * <pre>
 *
 * for each ti from t1 to t(n-1), compute:
 *   f  (ti)
 *   f' (ti) = (f (t(i+1)) - f(t(i-1))) / (t(i+1) - t(i-1))
 *   xi = ti - t1
 *   yi = int (f^2) from t1 to ti
 *   zi = int (f'^2) from t1 to ti
 *   update the sums Sum (xi^2), Sum (yi^2),
 *                   Sum (xi yi), Sum (xi zi)
 *                   and Sum (yi zi)
 * end for
 *
 *            |-------------------------------------------------
 *         \  | Sum (yi^2) Sum (xi zi) - Sum (xi yi) Sum (yi zi)
 * a     =  \ | ------------------------------------------------
 *           \| Sum (xi yi) Sum (xi zi) - Sum (xi^2) Sum (yi zi)
 *
 *
 *            |-------------------------------------------------
 *         \  | Sum (xi yi) Sum (xi zi) - Sum (xi^2) Sum (yi zi)
 * omega =  \ | ------------------------------------------------
 *           \| Sum (xi^2) Sum (yi^2)  - Sum (xi yi) Sum (xi yi)
 *
 * </pre>
 * </p>

 * <p>Once we know omega, we can compute:
 * <pre>
 *    fc = omega * f (t) * cos (omega * t) - f' (t) * sin (omega * t)
 *    fs = omega * f (t) * sin (omega * t) + f' (t) * cos (omega * t)
 * </pre>
 * </p>

 * <p>It appears that <code>fc = a * omega * cos (phi)</code> and
 * <code>fs = -a * omega * sin (phi)</code>, so we can use these
 * expressions to compute phi. The best estimate over the sample is
 * given by averaging these expressions.
 * </p>

 * <p>Since integrals and means are involved in the preceding
 * estimations, these operations run in O(n) time, where n is the
 * number of measurements.</p>

 * @version $Id: HarmonicCoefficientsGuesser.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class HarmonicCoefficientsGuesser
  implements Serializable{

  public HarmonicCoefficientsGuesser(AbstractCurveFitter.FitMeasurement[] measurements) {
    this.measurements = measurements;
    a                 = Double.NaN;
    omega             = Double.NaN;
  }

  /** Estimate a first guess of the coefficients.

   * @exception ExhaustedSampleException if the sample is exhausted.

   * @exception FunctionException if the integrator throws one.

   * @exception EstimationException if the sample is too short or if
   * the first guess cannot be computed (when the elements under the
   * square roots are negative).
   * */
  public void guess()
    throws ExhaustedSampleException, FunctionException, EstimationException {
    guessAOmega();
    guessPhi();
  }

  /** Estimate a first guess of the a and omega coefficients.

   * @exception ExhaustedSampleException if the sample is exhausted.

   * @exception FunctionException if the integrator throws one.

   * @exception EstimationException if the sample is too short or if
   * the first guess cannot be computed (when the elements under the
   * square roots are negative).

   */
  private void guessAOmega()
    throws ExhaustedSampleException, FunctionException, EstimationException {

    // initialize the sums for the linear model between the two integrals
    double sx2 = 0.0;
    double sy2 = 0.0;
    double sxy = 0.0;
    double sxz = 0.0;
    double syz = 0.0;

    // build the integrals sampler
    F2FP2Iterator iter = new F2FP2Iterator(measurements);
    SampledFunctionIterator sampler =
      new EnhancedSimpsonIntegratorSampler(iter);
    VectorialValuedPair p0 = sampler.nextSamplePoint();
    double   p0X = p0.getX();
    double[] p0Y = p0.getY();

    // get the points for the linear model
    while (sampler.hasNext()) {

      VectorialValuedPair point = sampler.nextSamplePoint();
      double   pX = point.getX();
      double[] pY = point.getY();

      double dx  = pX    - p0X;
      double dy0 = pY[0] - p0Y[0];
      double dy1 = pY[1] - p0Y[1];

      sx2 += dx  * dx;
      sy2 += dy0 * dy0;
      sxy += dx  * dy0;
      sxz += dx  * dy1;
      syz += dy0 * dy1;

    }

    // compute the amplitude and pulsation coefficients
    double c1 = sy2 * sxz - sxy * syz;
    double c2 = sxy * sxz - sx2 * syz;
    double c3 = sx2 * sy2 - sxy * sxy;
    if ((c1 / c2 < 0.0) || (c2 / c3 < 0.0)) {
      throw new EstimationException("unable to guess a first estimate");
    }
    a     = Math.sqrt(c1 / c2);
    omega = Math.sqrt(c2 / c3);

  }

  /** Estimate a first guess of the phi coefficient.

   * @exception ExhaustedSampleException if the sample is exhausted.

   * @exception FunctionException if the sampler throws one.

   */
  private void guessPhi()
    throws ExhaustedSampleException, FunctionException {

    SampledFunctionIterator iter = new FFPIterator(measurements);

    // initialize the means
    double fcMean = 0.0;
    double fsMean = 0.0;

    while (iter.hasNext()) {
      VectorialValuedPair point = iter.nextSamplePoint();
      double   omegaX = omega * point.getX();
      double[] pY     = point.getY();
      double   cosine = Math.cos(omegaX);
      double   sine   = Math.sin(omegaX);
      fcMean += omega * pY[0] * cosine - pY[1] *   sine;
      fsMean += omega * pY[0] *   sine + pY[1] * cosine;
    }

    phi = Math.atan2(-fsMean, fcMean);

  }

  public double getOmega() {
    return omega;
  }

  public double getA() {
    return a;
  }

  public double getPhi() {
    return phi;
  }

  private AbstractCurveFitter.FitMeasurement[] measurements;
  private double a;
  private double omega;
  private double phi;

  private static final long serialVersionUID = 2400399048702758814L;

}
