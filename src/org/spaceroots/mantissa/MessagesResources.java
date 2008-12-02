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

package org.spaceroots.mantissa;

import java.util.ListResourceBundle;

/** This class gather the message resources for the mantissa library.
 * @version $Id$
 * @author L. Maisonobe
 */

public class MessagesResources
  extends ListResourceBundle {

  /** Simple constructor.
   */
  public MessagesResources() {
  }

  public Object[][] getContents() {
    return (Object[][]) contents.clone();
  }

  static final Object[][] contents = {

    // org.spaceroots.mantissa.estimation.GaussNewtonEstimator
    { "unable to converge in {0} iterations",
      "unable to converge in {0} iterations" },

    // org.spaceroots.mantissa.estimation.LevenbergMarquardtEstimator
    { "cost relative tolerance is too small ({0}), no further reduction in the sum of squares is possible",
      "cost relative tolerance is too small ({0}), no further reduction in the sum of squares is possible" },
    { "parameters relative tolerance is too small ({0}), no further improvement in the approximate solution is possible",
      "parameters relative tolerance is too small ({0}), no further improvement in the approximate solution is possible" },
    { "orthogonality tolerance is too small ({0}), solution is orthogonal to the jacobian",
      "orthogonality tolerance is too small ({0}), solution is orthogonal to the jacobian" },
    { "maximal number of evaluations exceeded ({0})",
      "maximal number of evaluations exceeded ({0})" },

    // org.spaceroots.mantissa.fitting.HarmonicCoefficientsGuesser
    { "unable to guess a first estimate",
      "unable to guess a first estimate" },

    // org.spaceroots.mantissa.fitting.HarmonicFitter
    { "sample must contain at least {0} points",
      "sample must contain at least {0} points" },

    // org.spaceroots.mantissa.functions.ExhaustedSampleException
    { "sample contains only {0} elements",
      "sample contains only {0} elements" },

    // org.spaceroots.mantissa.geometry.CardanEulerSingularityException
    { "Cardan angles singularity",
      "Cardan angles singularity" },
    { "Euler angles singularity",
      "Euler angles singularity" },

    // org.spaceroots.mantissa.geometry.Rotation
    { "a {0}x{1} matrix cannot be a rotation matrix",
      "a {0}x{1} matrix cannot be a rotation matrix" },
    { "the closest orthogonal matrix has a negative determinant {0}",
      "the closest orthogonal matrix has a negative determinant {0}" },
    { "unable to orthogonalize matrix in {0} iterations",
      "unable to orthogonalize matrix in {0} iterations" },

    // org.spaceroots.mantissa.linalg;.SingularMatrixException
    { "singular matrix",
      "singular matrix" },

    // org.spaceroots.mantissa.ode.AdaptiveStepsizeIntegrator
    { "minimal step size ({0}) reached, integration needs {1}",
      "minimal step size ({0}) reached, integration needs {1}" },

    // org.spaceroots.mantissa.ode.GraggBulirschStoerIntegrator,
    // org.spaceroots.mantissa.ode.RungeKuttaFehlbergIntegrator,
    // org.spaceroots.mantissa.ode.RungeKuttaIntegrator
    { "dimensions mismatch: ODE problem has dimension {0},"
    + " state vector has dimension {1}",
      "dimensions mismatch: ODE problem has dimension {0},"
    + " state vector has dimension {1}" },
    { "too small integration interval: length = {0}",
      "too small integration interval: length = {0}" },

    // org.spaceroots.mantissa.optimization.DirectSearchOptimizer
    { "none of the {0} start points lead to convergence",
      "none of the {0} start points lead to convergence"  },

    // org.spaceroots.mantissa.random.CorrelatedRandomVectorGenerator
    { "dimension mismatch {0} != {1}",
      "dimension mismatch {0} != {1}" },

    // org.spaceroots.mantissa.random.NotPositiveDefiniteMatrixException
    { "not positive definite matrix",
      "not positive definite matrix" }

  };
  
}
