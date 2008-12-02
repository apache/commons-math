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

 * <p>We know f (t) at some sampling points t<sub>i</sub> and want to find a,
 * &omega; and &phi; such that f (t) = a cos (&omega; t + &phi;).
 * </p>
 *
 * <p>From the analytical expression, we can compute two primitives :
 * <pre>
 *     If2  (t) = &int; f<sup>2</sup>  = a<sup>2</sup> &times; [t + S (t)] / 2
 *     If'2 (t) = &int; f'<sup>2</sup> = a<sup>2</sup> &omega;<sup>2</sup> &times; [t - S (t)] / 2
 *     where S (t) = sin (2 (&omega; t + &phi;)) / (2 &omega;)
 * </pre>
 * </p>
 *
 * <p>We can remove S between these expressions :
 * <pre>
 *     If'2 (t) = a<sup>2</sup> &omega;<sup>2</sup> t - &omega;<sup>2</sup> If2 (t)
 * </pre>
 * </p>
 *
 * <p>The preceding expression shows that If'2 (t) is a linear
 * combination of both t and If2 (t): If'2 (t) = A &times; t + B &times; If2 (t)
 * </p>
 *
 * <p>From the primitive, we can deduce the same form for definite
 * integrals between t<sub>1</sub> and t<sub>i</sub> for each t<sub>i</sub> :
 * <pre>
 *   If2 (t<sub>i</sub>) - If2 (t<sub>1</sub>) = A &times; (t<sub>i</sub> - t<sub>1</sub>) + B &times; (If2 (t<sub>i</sub>) - If2 (t<sub>1</sub>))
 * </pre>
 * </p>
 *
 * <p>We can find the coefficients A and B that best fit the sample
 * to this linear expression by computing the definite integrals for
 * each sample points.
 * </p>
 *
 * <p>For a bilinear expression z (x<sub>i</sub>, y<sub>i</sub>) = A &times; x<sub>i</sub> + B &times; y<sub>i</sub>, the
 * coefficients A and B that minimize a least square criterion
 * &sum; (z<sub>i</sub> - z (x<sub>i</sub>, y<sub>i</sub>))<sup>2</sup> are given by these expressions:</p>
 * <pre>
 *
 *         &sum;y<sub>i</sub>y<sub>i</sub> &sum;x<sub>i</sub>z<sub>i</sub> - &sum;x<sub>i</sub>y<sub>i</sub> &sum;y<sub>i</sub>z<sub>i</sub>
 *     A = ------------------------
 *         &sum;x<sub>i</sub>x<sub>i</sub> &sum;y<sub>i</sub>y<sub>i</sub> - &sum;x<sub>i</sub>y<sub>i</sub> &sum;x<sub>i</sub>y<sub>i</sub>
 *
 *         &sum;x<sub>i</sub>x<sub>i</sub> &sum;y<sub>i</sub>z<sub>i</sub> - &sum;x<sub>i</sub>y<sub>i</sub> &sum;x<sub>i</sub>z<sub>i</sub>
 *     B = ------------------------
 *         &sum;x<sub>i</sub>x<sub>i</sub> &sum;y<sub>i</sub>y<sub>i</sub> - &sum;x<sub>i</sub>y<sub>i</sub> &sum;x<sub>i</sub>y<sub>i</sub>
 * </pre>
 * </p>
 *
 *
 * <p>In fact, we can assume both a and &omega; are positive and
 * compute them directly, knowing that A = a<sup>2</sup> &omega;<sup>2</sup> and that
 * B = - &omega;<sup>2</sup>. The complete algorithm is therefore:</p>
 * <pre>
 *
 * for each t<sub>i</sub> from t<sub>1</sub> to t<sub>n-1</sub>, compute:
 *   f  (t<sub>i</sub>)
 *   f' (t<sub>i</sub>) = (f (t<sub>i+1</sub>) - f(t<sub>i-1</sub>)) / (t<sub>i+1</sub> - t<sub>i-1</sub>)
 *   x<sub>i</sub> = t<sub>i</sub> - t<sub>1</sub>
 *   y<sub>i</sub> = &int; f<sup>2</sup> from t<sub>1</sub> to t<sub>i</sub>
 *   z<sub>i</sub> = &int; f'<sup>2</sup> from t<sub>1</sub> to t<sub>i</sub>
 *   update the sums &sum;x<sub>i</sub>x<sub>i</sub>, &sum;y<sub>i</sub>y<sub>i</sub>, &sum;x<sub>i</sub>y<sub>i</sub>, &sum;x<sub>i</sub>z<sub>i</sub> and &sum;y<sub>i</sub>z<sub>i</sub>
 * end for
 *
 *            |--------------------------
 *         \  | &sum;y<sub>i</sub>y<sub>i</sub> &sum;x<sub>i</sub>z<sub>i</sub> - &sum;x<sub>i</sub>y<sub>i</sub> &sum;y<sub>i</sub>z<sub>i</sub>
 * a     =  \ | ------------------------
 *           \| &sum;x<sub>i</sub>y<sub>i</sub> &sum;x<sub>i</sub>z<sub>i</sub> - &sum;x<sub>i</sub>x<sub>i</sub> &sum;y<sub>i</sub>z<sub>i</sub>
 *
 *
 *            |--------------------------
 *         \  | &sum;x<sub>i</sub>y<sub>i</sub> &sum;x<sub>i</sub>z<sub>i</sub> - &sum;x<sub>i</sub>x<sub>i</sub> &sum;y<sub>i</sub>z<sub>i</sub>
 * &omega;     =  \ | ------------------------
 *           \| &sum;x<sub>i</sub>x<sub>i</sub> &sum;y<sub>i</sub>y<sub>i</sub> - &sum;x<sub>i</sub>y<sub>i</sub> &sum;x<sub>i</sub>y<sub>i</sub>
 *
 * </pre>
 * </p>

 * <p>Once we know &omega;, we can compute:
 * <pre>
 *    fc = &omega; f (t) cos (&omega; t) - f' (t) sin (&omega; t)
 *    fs = &omega; f (t) sin (&omega; t) + f' (t) cos (&omega; t)
 * </pre>
 * </p>

 * <p>It appears that <code>fc = a &omega; cos (&phi;)</code> and
 * <code>fs = -a &omega; sin (&phi;)</code>, so we can use these
 * expressions to compute &phi;. The best estimate over the sample is
 * given by averaging these expressions.
 * </p>

 * <p>Since integrals and means are involved in the preceding
 * estimations, these operations run in O(n) time, where n is the
 * number of measurements.</p>

 * @version $Id$
 * @author L. Maisonobe

 */

public class HarmonicCoefficientsGuesser
  implements Serializable{

  public HarmonicCoefficientsGuesser(AbstractCurveFitter.FitMeasurement[] measurements) {
    this.measurements =
      (AbstractCurveFitter.FitMeasurement[]) measurements.clone();
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

  /** Estimate a first guess of the a and &omega; coefficients.

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
    double   p0X = p0.x;
    double[] p0Y = p0.y;

    // get the points for the linear model
    while (sampler.hasNext()) {

      VectorialValuedPair point = sampler.nextSamplePoint();
      double   pX = point.x;
      double[] pY = point.y;

      double x = pX    - p0X;
      double y = pY[0] - p0Y[0];
      double z = pY[1] - p0Y[1];

      sx2 += x * x;
      sy2 += y * y;
      sxy += x * y;
      sxz += x * z;
      syz += y * z;

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

  /** Estimate a first guess of the &phi; coefficient.

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
      double   omegaX = omega * point.x;
      double   cosine = Math.cos(omegaX);
      double   sine   = Math.sin(omegaX);
      fcMean += omega * point.y[0] * cosine - point.y[1] *   sine;
      fsMean += omega * point.y[0] *   sine + point.y[1] * cosine;
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
