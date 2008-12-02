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
import java.util.ArrayList;
import java.util.List;

import org.spaceroots.mantissa.estimation.*;

/** This class is the base class for all curve fitting classes in the package.

 * <p>This class handles all common features of curve fitting like the
 * sample points handling. It declares two methods ({@link
 * #valueAt} and {@link #partial}) which should be implemented by
 * sub-classes to define the precise shape of the curve they
 * represent.</p>

 * @version $Id$
 * @author L. Maisonobe

 */

public abstract class AbstractCurveFitter
  implements EstimationProblem, Serializable {

  /** Simple constructor.
   * @param n number of coefficients in the underlying function
   * @param estimator estimator to use for the fitting
   */
  protected AbstractCurveFitter(int n, Estimator estimator) {

    coefficients   = new EstimatedParameter[n];
    measurements   = new ArrayList();
    this.estimator = estimator;
  }

  /** Simple constructor.
   * @param coefficients first estimate of the coefficients. A
   * reference to this array is hold by the newly created object. Its
   * elements will be adjusted during the fitting process and they will
   * be set to the adjusted coefficients at the end.
   * @param estimator estimator to use for the fitting
   */
  protected AbstractCurveFitter(EstimatedParameter[] coefficients,
                                Estimator estimator) {

    this.coefficients = coefficients;
    measurements      = new ArrayList();
    this.estimator     = estimator;
  }

  /** Add a weighted (x,y) pair to the sample.
   * @param weight weight of this pair in the fit
   * @param x      abscissa
   * @param y      ordinate, we have <code>y = f (x)</code>
   */
  public void addWeightedPair(double weight, double x, double y) {
    measurements.add(new FitMeasurement(weight, x, y));
  }

  /** Perform the fitting.

   * <p>This method compute the coefficients of the curve that best
   * fit the sample of weighted pairs previously given through calls
   * to the {@link #addWeightedPair addWeightedPair} method.</p>

   * @return coefficients of the curve
   * @exception EstimationException if the fitting is not possible
   * (for example if the sample has to few independant points)

   */
  public double[] fit()
    throws EstimationException {
    // perform the fit
    estimator.estimate(this);

    // extract the coefficients
    double[] fittedCoefficients = new double[coefficients.length];
    for (int i = 0; i < coefficients.length; ++i) {
      fittedCoefficients[i] = coefficients[i].getEstimate();
    }

    return fittedCoefficients;

  }

  public WeightedMeasurement[] getMeasurements() {
    return (WeightedMeasurement[]) measurements.toArray(new FitMeasurement[measurements.size()]);
  }

  /** Get the unbound parameters of the problem.
   * For a curve fitting, none of the function coefficient is bound.
   * @return unbound parameters
   */
  public EstimatedParameter[] getUnboundParameters() {
   return (EstimatedParameter[]) coefficients.clone();
  }

  /** Get all the parameters of the problem.
   * @return parameters
   */
  public EstimatedParameter[] getAllParameters() {
   return (EstimatedParameter[]) coefficients.clone();
  }

  /** Utility method to sort the measurements with respect to the abscissa.

   * <p>This method is provided as a utility for derived classes. As
   * an example, the {@link HarmonicFitter} class needs it in order to
   * compute a first guess of the coefficients to initialize the
   * estimation algorithm.</p>

   */
  protected void sortMeasurements() {

    // Since the samples are almost always already sorted, this
    // method is implemented as an insertion sort that reorders the
    // elements in place. Insertion sort is very efficient in this case.
    FitMeasurement curr = (FitMeasurement) measurements.get(0);
    for (int j = 1; j < measurements.size (); ++j) {
      FitMeasurement prec = curr;
      curr = (FitMeasurement) measurements.get(j);
      if (curr.x < prec.x) {
        // the current element should be inserted closer to the beginning
        int i = j - 1;
        FitMeasurement mI = (FitMeasurement) measurements.get(i);
        while ((i >= 0) && (curr.x < mI.x)) {
          measurements.set(i + 1, mI);
          if (i-- != 0) {
            mI = (FitMeasurement) measurements.get(i);
          } else {
            mI = null;
          }
        }
        measurements.set(i + 1, curr);
        curr = (FitMeasurement) measurements.get(j);
      }
    }

  }

  /** Get the value of the function at x according to the current parameters value.
   * @param x abscissa at which the theoretical value is requested
   * @return theoretical value at x
   */
  public abstract double valueAt(double x);

  /** Get the derivative of the function at x with respect to parameter p.
   * @param x abscissa at which the partial derivative is requested
   * @param p parameter with respect to which the derivative is requested
   * @return partial derivative
   */
  public abstract double partial(double x, EstimatedParameter p);

  /** This class represents the fit measurements.
   * One measurement is a weighted pair (x, y), where <code>y = f
   * (x)</code> is the value of the function at x abscissa. This class
   * is an inner class because the methods related to the computation
   * of f values and derivative are proveded by the fitter
   * implementations.
   */
  public class FitMeasurement
    extends WeightedMeasurement {

    /** Simple constructor.
     * @param weight weight of the measurement in the fitting process
     * @param x abscissa of the measurement
     * @param y ordinate of the measurement
     */
    public FitMeasurement(double weight, double x, double y) {
      super(weight, y);
      this.x = x;
    }

    /** Get the value of the fitted function at x.
     * @return theoretical value at the measurement abscissa
     */
    public double getTheoreticalValue() {
      return valueAt(x);
    }

    /** Partial derivative with respect to one function coefficient.
     * @param p parameter with respect to which the derivative is requested
     * @return partial derivative
     */
    public double getPartial(EstimatedParameter p) {
     return partial(x, p);
    }

    /** Abscissa of the measurement. */
    public final double x;

    private static final long serialVersionUID = -2682582852369995960L;

  }

  /** Coefficients of the function */
  protected EstimatedParameter[] coefficients;

  /** Measurements vector */
  protected List measurements;

  /** Estimator for the fitting problem. */
  private Estimator estimator;

}
