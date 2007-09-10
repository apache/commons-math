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

package org.apache.commons.math;

import java.util.ListResourceBundle;

/** French localization message resources for the commons-math library.
 * @version $Revision:$
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

    // org.apache.commons.math.FunctionEvaluationException
    { "Evaluation failed for argument = {0}",
      "Erreur d''\u00e9valuation pour l''argument {0}" },

    // org.apache.commons.math.DuplicateSampleAbscissaException
    { "Abscissa {0} is duplicated at both indices {1} and {2}",
      "Abscisse {0} dupliqu\u00e9e aux indices {1} et {2}" },

    // org.apache.commons.math.ConvergenceException
    { "Convergence failed",
      "\u00c9chec de convergence" },

    // org.apache.commons.math.ArgumentOutsideDomainException
    { "Argument {0} outside domain [{1} ; {2}]",
      "Argument {0} hors du domaine [{1} ; {2}]" },

    // org.apache.commons.math.MaxIterationsExceededException
    { "Maximal number of iterations ({0}) exceeded",
      "Nombre maximal d''it\u00e9rations ({0}) d\u00e9pass\u00e9" },

    // org.apache.commons.math.DimensionMismatchException
    { "dimension mismatch {0} != {1}",
      "dimensions incompatibles {0} != {1}" },

    // org.apache.commons.math.random.NotPositiveDefiniteMatrixException
    { "not positive definite matrix",
      "matrice non d\u00e9finie positive" },

    // org.apache.commons.math.fraction.FractionConversionException
    { "Unable to convert {0} to fraction after {1} iterations",
      "Impossible de convertir {0} en fraction apr\u00e8s {1} it\u00e9rations" },

    // org.apache.commons.math.analysis.UnivariateRealSolverUtils
    { "Number of iterations={0}, maximum iterations={1}, initial={2}, lower bound={3}, upper bound={4}," +
          " final a value={5}, final b value={6}, f(a)={7}, f(b)={8}",
      "Nombre d''it\u00e9rations = {0}, it\u00e9rations maximum = {1}, valeur initiale = {2}," +
          " borne inf\u00e9rieure = {3}, borne sup\u00e9rieure = {4}," +
          " valeur a finale = {5}, valeur b finale = {6}, f(a) = {7}, f(b) = {8}" },

    // org.apache.commons.math.util.ContinuedFraction
    { "Continued fraction convergents diverged to +/- infinity for value {0}",
      "Divergence de fraction continue \u00e0 l''infini pour la valeur {0}" },
    { "Continued fraction convergents failed to converge for value {0}",
      "\u00c9chec de convergence de fraction continue pour la valeur {0}" },

    // org.apache.commons.math.util.DefaultTransformer
    { "Conversion Exception in Transformation, Object is null",
      "Exception de conversion dans une transformation, l''objet est nul" },
    { "Conversion Exception in Transformation: {0}",
      "Exception de conversion dans une transformation : {0}" },

    // org.apache.commons.math.estimation.GaussNewtonEstimator
    { "unable to converge in {0} iterations",
      "pas de convergence apr\u00e8s {0} it\u00e9rations" },

    // org.apache.commons.math.estimation.LevenbergMarquardtEstimator
    { "cost relative tolerance is too small ({0}), no further reduction in the sum of squares is possible",
      "trop petite tol\u00e9rance relative sur le co\u00fbt ({0}), aucune r\u00e9duction de la somme des carr\u00e9s n''est possible" },
    { "parameters relative tolerance is too small ({0}), no further improvement in the approximate solution is possible",
      "trop petite tol\u00e9rance relative sur les param\u00e8tres ({0}), aucune am\u00e9lioration de la solution approximative n''est possible" },
    { "orthogonality tolerance is too small ({0}), solution is orthogonal to the jacobian",
      "trop petite tol\u00e9rance sur l''orthogonalit\u00e9 ({0}), la solution est orthogonale \u00e0 la jacobienne" },
    { "maximal number of evaluations exceeded ({0})",
      "nombre maximal d''\u00e9valuations d\u00e9pass\u00e9 ({0})" },

    // org.apache.commons.math.geometry.CardanEulerSingularityException
    { "Cardan angles singularity",
      "singularit\u00e9 d''angles de Cardan" },
    { "Euler angles singularity",
      "singularit\u00e9 d''angles d''Euler" },

    // org.apache.commons.math.geometry.Rotation
    { "a {0}x{1} matrix cannot be a rotation matrix",
      "une matrice {0}x{1} ne peut pas \u00eatre une matrice de rotation" },
    { "the closest orthogonal matrix has a negative determinant {0}",
      "la matrice orthogonale la plus proche a un d\u00e9terminant n\u00e9gatif {0}" },
    { "unable to orthogonalize matrix in {0} iterations",
      "impossible de rendre la matrice orthogonale en {0} it\u00e9rations" },

    // org.apache.commons.math.ode.AdaptiveStepsizeIntegrator
    { "minimal step size ({0}) reached, integration needs {1}",
      "pas minimal ({0}) atteint, l''int\u00e9gration n\u00e9cessite {1}" },
    { "dimensions mismatch: state vector has dimension {0},"
    + " absolute tolerance vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le vecteur d''\u00e9tat ({0}),"
    + " et le vecteur de tol\u00e9rance absolue ({1})" },
    { "dimensions mismatch: state vector has dimension {0},"
    + " relative tolerance vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le vecteur d''\u00e9tat ({0}),"
    + " et le vecteur de tol\u00e9rance relative ({1})" },

    // org.apache.commons.math.ode.AdaptiveStepsizeIntegrator,
    // org.apache.commons.math.ode.RungeKuttaIntegrator
    { "dimensions mismatch: ODE problem has dimension {0},"
    + " initial state vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le probl\u00e8me ODE ({0}),"
    + " et le vecteur d''\u00e9tat initial ({1})" },
    { "dimensions mismatch: ODE problem has dimension {0},"
    + " final state vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le probl\u00e8me ODE ({0}),"
    + " et le vecteur d''\u00e9tat final ({1})" },
    { "too small integration interval: length = {0}",
      "intervalle d''int\u00e9gration trop petit : {0}" },

    // org.apache.commons.math.optimization.DirectSearchOptimizer
    { "none of the {0} start points lead to convergence",
      "aucun des {0} points de d\u00e9part n''aboutit \u00e0 une convergence"  }

  };

}
