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
public class MessagesResources_fr
  extends ListResourceBundle {

  /** Simple constructor.
   */
  public MessagesResources_fr() {
  }

  public Object[][] getContents() {
    return (Object[][]) contents.clone();
  }

  static final Object[][] contents = {

    // org.spaceroots.mantissa.estimation.GaussNewtonEstimator
    { "unable to converge in {0} iterations",
      "pas de convergence apr\u00e8s {0} it\u00e9rations" },

    // org.spaceroots.mantissa.estimation.LevenbergMarquardtEstimator
    { "cost relative tolerance is too small ({0}), no further reduction in the sum of squares is possible",
      "trop petite tol\u00e9rance relative sur le co\u00fbt ({0}), aucune r\u00e9duction de la somme des carr\u00e9s n''est possible" },
    { "parameters relative tolerance is too small ({0}), no further improvement in the approximate solution is possible",
      "trop petite tol\u00e9rance relative sur les param\u00e8tres ({0}), aucune am\u00e9lioration de la solution approximative n''est possible" },
    { "orthogonality tolerance is too small ({0}), solution is orthogonal to the jacobian",
      "trop petite tol\u00e9rance sur l''orthogonalit\u00e9 ({0}), la solution est orthogonale \u00e0 la jacobienne" },
    { "maximal number of evaluations exceeded ({0})",
      "nombre maximal d''\u00e9valuations d\u00e9pass\u00e9 ({0})" },

    // org.spaceroots.mantissa.fitting.HarmonicCoefficientsGuesser
    { "unable to guess a first estimate",
      "impossible de trouver une premi\u00e8re estim\u00e9e" },

    // org.spaceroots.mantissa.fitting.HarmonicFitter
    { "sample must contain at least {0} points",
      "l''\u00e9chantillon doit contenir au moins {0} points" },

    // org.spaceroots.mantissa.functions.ExhaustedSampleException
    { "sample contains only {0} elements",
      "l''\u00e9chantillon ne contient que {0} points" },

    // org.spaceroots.mantissa.geometry.CardanEulerSingularityException
    { "Cardan angles singularity",
      "singularit\u00e9 d''angles de Cardan" },
    { "Euler angles singularity",
      "singularit\u00e9 d''angles d''Euler" },

    // org.spaceroots.mantissa.geometry.Rotation
    { "a {0}x{1} matrix cannot be a rotation matrix",
      "une matrice {0}x{1} ne peut pas \u00e9tre une matrice de rotation" },
    { "the closest orthogonal matrix has a negative determinant {0}",
      "la matrice orthogonale la plus proche a un d\u00e9terminant n\u00e9gatif {0}" },
    { "unable to orthogonalize matrix in {0} iterations",
      "impossible de rendre la matrice orthogonale en {0} it\u00e9rations" },

    // org.spaceroots.mantissa.linalg;.SingularMatrixException
    { "singular matrix",
      "matrice singuli\u00e8re" },

    // org.spaceroots.mantissa.ode.AdaptiveStepsizeIntegrator
    { "minimal step size ({0}) reached, integration needs {1}",
      "pas minimal ({0}) atteint, l''int\u00e9gration n\u00e9cessite {1}" },

    // org.spaceroots.mantissa.ode.GraggBulirschStoerIntegrator,
    // org.spaceroots.mantissa.ode.RungeKuttaFehlbergIntegrator,
    // org.spaceroots.mantissa.ode.RungeKuttaIntegrator
    { "dimensions mismatch: ODE problem has dimension {0},"
    + " state vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le probl\u00e8me ODE ({0}),"
    + " et le vecteur d''\u00e9tat ({1})" },
    { "too small integration interval: length = {0}",
      "intervalle d''int\u00e9gration trop petit : {0}" },

    // org.spaceroots.mantissa.optimization.DirectSearchOptimizer
    { "none of the {0} start points lead to convergence",
      "aucun des {0} points de d\u00e9part n''aboutit \u00e0 une convergence"  },

    // org.spaceroots.mantissa.random.CorrelatedRandomVectorGenerator
    { "dimension mismatch {0} != {1}",
      "dimensions incompatibles {0} != {1}" },

    // org.spaceroots.mantissa.random.NotPositiveDefiniteMatrixException
    { "not positive definite matrix",
      "matrice non d\u00e9finie positive" }

  };

}
