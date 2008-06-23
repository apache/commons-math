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
import org.spaceroots.mantissa.estimation.Estimator;
import org.spaceroots.mantissa.estimation.GaussNewtonEstimator;

/** This class implements a curve fitting specialized for polynomials.

 * <p>Polynomial fitting is a very simple case of curve fitting. The
 * estimated coefficients are the polynom coefficients. They are
 * searched by a least square estimator.</p>

 * <p>This class <emph>is by no means optimized</emph>, neither in
 * space nor in time performance.</p>

 * @see PolynomialCoefficient

 * @version $Id$
 * @author L. Maisonobe

 */

public class PolynomialFitter
  extends AbstractCurveFitter {

  /** Simple constructor.

   * <p>The polynomial fitter built this way are complete polynoms,
   * ie. a n-degree polynom has n+1 coefficients. In order to build
   * fitter for sparse polynoms (for example <code>a x^20 - b
   * x^30</code>, on should first build the coefficients array and
   * provide it to {@link
   * #PolynomialFitter(PolynomialCoefficient[], int, double, double,
   * double)}.</p>
   * @param degree maximal degree of the polynom
   * @param estimator estimator to use for the fitting
   */
  public PolynomialFitter(int degree, Estimator estimator) {
    super(degree + 1, estimator);
    for (int i = 0; i < coefficients.length; ++i) {
      coefficients[i] = new PolynomialCoefficient(i);
    }
  }

  /** Simple constructor.

   * <p>This constructor can be used either when a first estimate of
   * the coefficients is already known (which is of little interest
   * because the fit cost is the same whether a first guess is known or
   * not) or when one needs to handle sparse polynoms like <code>a
   * x^20 - b x^30</code>.</p>

   * @param coefficients first estimate of the coefficients.
   * A reference to this array is hold by the newly created
   * object. Its elements will be adjusted during the fitting process
   * and they will be set to the adjusted coefficients at the end.
   * @param estimator estimator to use for the fitting
   */
  public PolynomialFitter(PolynomialCoefficient[] coefficients,
                          Estimator estimator) {
    super(coefficients, estimator);
  }

  /** Get the value of the function at x according to the current parameters value.
   * @param x abscissa at which the theoretical value is requested
   * @return theoretical value at x
   */
  public double valueAt(double x) {
    double y = coefficients[coefficients.length - 1].getEstimate();
    for (int i = coefficients.length - 2; i >= 0; --i) {
      y = y * x + coefficients[i].getEstimate();
    }
    return y;
  }

  /** Get the derivative of the function at x with respect to parameter p.
   * @param x abscissa at which the partial derivative is requested
   * @param p parameter with respect to which the derivative is requested
   * @return partial derivative
   */
  public double partial(double x, EstimatedParameter p) {
    if (p instanceof PolynomialCoefficient) {
      return Math.pow(x, ((PolynomialCoefficient) p).degree);
    }
    throw new RuntimeException("internal error");
  }

  private static final long serialVersionUID = -744904084649890769L;

}
