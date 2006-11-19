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

package org.spaceroots.mantissa.estimation;

import java.io.Serializable;

/** This class implements a solver for estimation problems.
 * @deprecated this class has been replaced by the {@link
 * org.spaceroots.mantissa.estimation.GaussNewtonEstimator GaussNewtonEstimator}
 * class. It is now a simple wrapper delegating everything to {@link
 * org.spaceroots.mantissa.estimation.GaussNewtonEstimator GaussNewtonEstimator}
 * @version $Id: LeastSquaresEstimator.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe
 */
public class LeastSquaresEstimator implements Estimator, Serializable {

  /** Simple constructor.
   * @see org.spaceroots.mantissa.estimation.GaussNewtonEstimator#GaussNewtonEstimator(int,
   * double, double, double)
   */
  public LeastSquaresEstimator(int maxIterations,
                               double convergence,
                               double steadyStateThreshold,
                               double epsilon) {
    estimator = new GaussNewtonEstimator(maxIterations,
                                         convergence,
                                         steadyStateThreshold,
                                         epsilon);
  }

  /** Solve an estimation problem using a least squares criterion.
   * @see org.spaceroots.mantissa.estimation.GaussNewtonEstimator#estimate
   */
  public void estimate(EstimationProblem problem)
    throws EstimationException {
    estimator.estimate(problem);
   }

  /** Estimate the solution of a linear least square problem.
   * @see org.spaceroots.mantissa.estimation.GaussNewtonEstimator#linearEstimate
   */
  public void linearEstimate(EstimationProblem problem)
    throws EstimationException {
    estimator.linearEstimate(problem);
  }

  /** Get the Root Mean Square value.
   * @see org.spaceroots.mantissa.estimation.GaussNewtonEstimator#getRMS
   */
  public double getRMS(EstimationProblem problem) {
    return estimator.getRMS(problem);
  }

  private GaussNewtonEstimator estimator;

  private static final long serialVersionUID = -7542643494637247770L;

}
