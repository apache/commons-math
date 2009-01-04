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

/** 
 * French localization message resources for the commons-math library.
 * @version $Revision$ $Date$
 * @since 1.2
 */
public class MessagesResources_fr
  extends ListResourceBundle {

  /** 
   * Simple constructor.
   */
  public MessagesResources_fr() {
  }

  /** 
   * Get the non-translated/translated messages arrays from this resource bundle.
   * @return non-translated/translated messages arrays
   */
  public Object[][] getContents() {
    return (Object[][]) contents.clone();
  }

  /** Non-translated/translated messages arrays. */
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
    { "Overflow trying to convert {0} to fraction ({1}/{2})",
      "D\u00e9passement de capacit\u00e9 lors de la conversion de {0} en fraction ({1}/{2})" },

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

    // org.apache.commons.math.estimation.AbstractEstimator
    { "maximal number of evaluations exceeded ({0})",
      "nombre maximal d''\u00e9valuations d\u00e9pass\u00e9 ({0})" },
    { "unable to compute covariances: singular problem",
      "impossible de calculer les covariances : probl\u00e8me singulier"},
    { "no degrees of freedom ({0} measurements, {1} parameters)",
      "aucun degr\u00e9 de libert\u00e9 ({0} mesures, {1} param\u00e8tres)" },

    // org.apache.commons.math.estimation.GaussNewtonEstimator
    { "unable to solve: singular problem",
      "r\u00e9solution impossible : probl\u00e8me singulier" },

    // org.apache.commons.math.estimation.LevenbergMarquardtEstimator
    { "cost relative tolerance is too small ({0}), no further reduction in the sum of squares is possible",
      "trop petite tol\u00e9rance relative sur le co\u00fbt ({0}), aucune r\u00e9duction de la somme des carr\u00e9s n''est possible" },
    { "parameters relative tolerance is too small ({0}), no further improvement in the approximate solution is possible",
      "trop petite tol\u00e9rance relative sur les param\u00e8tres ({0}), aucune am\u00e9lioration de la solution approximative n''est possible" },
    { "orthogonality tolerance is too small ({0}), solution is orthogonal to the jacobian",
      "trop petite tol\u00e9rance sur l''orthogonalit\u00e9 ({0}), la solution est orthogonale \u00e0 la jacobienne" },
    { "unable to perform Q.R decomposition on the {0}x{1} jacobian matrix",
      "impossible de calculer la factorisation Q.R de la matrice jacobienne {0}x{1}" },

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

    // org.apache.commons.math.ode.nonstiff.AdaptiveStepsizeIntegrator
    { "minimal step size ({0}) reached, integration needs {1}",
      "pas minimal ({0}) atteint, l''int\u00e9gration n\u00e9cessite {1}" },
    { "dimensions mismatch: state vector has dimension {0}," +
      " absolute tolerance vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le vecteur d''\u00e9tat ({0})," +
      " et le vecteur de tol\u00e9rance absolue ({1})" },
    { "dimensions mismatch: state vector has dimension {0}," +
      " relative tolerance vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le vecteur d''\u00e9tat ({0})," +
      " et le vecteur de tol\u00e9rance relative ({1})" },

    // org.apache.commons.math.ode.nonstiff.AdaptiveStepsizeIntegrator,
    // org.apache.commons.math.ode.nonstiff.RungeKuttaIntegrator
    { "dimensions mismatch: ODE problem has dimension {0}," +
      " initial state vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le probl\u00e8me ODE ({0})," +
      " et le vecteur d''\u00e9tat initial ({1})" },
    { "dimensions mismatch: ODE problem has dimension {0}," +
      " final state vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le probl\u00e8me ODE ({0})," +
      " et le vecteur d''\u00e9tat final ({1})" },
    { "too small integration interval: length = {0}",
      "intervalle d''int\u00e9gration trop petit : {0}" },

    // org.apache.commons.math.ode.ContinuousOutputModel
    // org.apache.commons.math.optimization.DirectSearchOptimizer
    { "unexpected exception caught",
      "exception inattendue lev\u00e9e" },

    // org.apache.commons.math.optimization.DirectSearchOptimizer
    { "none of the {0} start points lead to convergence",
      "aucun des {0} points de d\u00e9part n''aboutit \u00e0 une convergence"  },

    // org.apache.commons.math.random.EmpiricalDistributionImpl
    { "no bin selected",
      "aucun compartiment s\u00e9lectionn\u00e9" },

    // org.apache.commons.math.linear.EigenDecompositionImpl
    { "cannot solve degree {0} equation",
      "impossible de r\u00e9soudre une \u00e9quation de degr\u00e9 {0}" },
    { "eigen decomposition of assymetric matrices not supported yet",
      "la d\u00e9composition en valeurs/vecteurs propres de matrices non sym\u00e9triques n''est pas encore disponible" },

    // org.apache.commons.math.linear.NonSquareMatrixException
    { "a {0}x{1} matrix was provided instead of a square matrix",
      "une matrice {0}x{1} a \u00e9t\u00e9 fournie \u00e0 la place d''une matrice carr\u00e9e" },

    // org.apache.commons.math.linear.SingularMatrixException
    { "matrix is singular",
      "matrice singuli\u00e8re" },

    // org.apache.commons.math.linear.RealVectorImpl
    { "index {0} out of allowed range [{1}, {2}]",
      "index {0} hors de la plage autoris\u00e9e [{1}, {2}]" },

    // org.apache.commons.math.linear.AbstractRealMatrix
    { "invalid row dimension: {0} (must be positive)",
      "nombre de lignes invalide : {0} (doit \u00eatre positif)" },
    { "invalid column dimension: {0} (must be positive)",
      "nombre de colonnes invalide : {0} (doit \u00eatre positif)" },
    { "vector length mismatch: got {0} but expected {1}",
      "taille de vecteur invalide : {0} au lieu de {1} attendue" },
    { "dimensions mismatch: got {0}x{1} but expected {2}x{3}",
      "dimensions incoh\u00e9rentes : {0}x{1} \u00e0 la place de {2}x{3}" },
    { "matrix must have at least one row",
      "une matrice doit comporter au moins une ligne" },
    { "matrix must have at least one column",
      "une matrice doit comporter au moins une colonne" },
    { "some rows have length {0} while others have length {1}",
      "certaines lignes ont une longueur de {0} alors que d''autres ont une longueur de {1}" },
    { "{0}x{1} and {2}x{3} matrices are not addition compatible",
      "les dimensions {0}x{1} et {2}x{3} sont incompatibles pour l'addition matricielle" },
    { "{0}x{1} and {2}x{3} matrices are not subtraction compatible",
      "les dimensions {0}x{1} et {2}x{3} sont incompatibles pour la soustraction matricielle" },
    { "{0}x{1} and {2}x{3} matrices are not multiplication compatible",
      "les dimensions {0}x{1} et {2}x{3} sont incompatibles pour la multiplication matricielle" },

    // org.apache.commons.math.linear.DenseRealMatrix
    { "wrong array shape (block length = {0}, expected {1})",
      "forme de tableau erron\u00e9e (bloc de longueur {0} au lieu des {1} attendus)" },

    // org.apache.commons.math.linear.BigMatrixImpl
    // org.apache.commons.math.linear.RealMatrixImpl
    { "row index {0} out of allowed range [{1}, {2}]",
      "index de ligne {0} hors de la plage autoris\u00e9e [{1}, {2}]" },
    { "column index {0} out of allowed range [{1}, {2}]",
      "index de colonne {0} hors de la plage autoris\u00e9e [{1}, {2}]" },
    { "no entry at indices ({0}, {1}) in a {2}x{3} matrix",
      "pas d''entr\u00e9e aux indices ({0}, {1}) dans une matrice {2}x{3}" },
    { "initial row {0} after final row {1}",
      "ligne initiale {0} apr\u00e8s la ligne finale {1}" },
    { "initial column {0} after final column {1}",
      "colonne initiale {0} apr\u00e8s la colonne finale {1}" },
    { "empty selected row index array",
      "tableau des indices de lignes s\u00e9lectionn\u00e9es vide" },
    { "empty selected column index array",
      "tableau des indices de colonnes s\u00e9lectionn\u00e9es vide" },

   // org.apache.commons.math.random.EmpiricalDistributionImpl
   // org.apache.commons.math.random.ValueServer
   { "URL {0} contains no data",
     "l''adresse {0} ne contient aucune donn\u00e9e" },

   // org.apache.commons.math.complex.Complex
   { "cannot compute nth root for null or negative n: {0}",
     "impossible de calculer la racine ni\u00e8me pour n n\u00e9gatif ou nul : {0}" },

   // org.apache.commons.math.complex.ComplexFormat
   { "unparseable complex number: \"{0}\"",
     "\u00e9chec d''analyse du nombre complexe \"{0}\"" },

   // org.apache.commons.math.fraction.FractionFormat
   { "unparseable fraction number: \"{0}\"",
     "\u00e9chec d''analyse du nombre rationnel \"{0}\"" },

   // org.apache.commons.math.geometry.Vector3DFormat
   { "unparseable 3D vector: \"{0}\"",
     "\u00e9chec d''analyse du vecteur de dimension 3 \"{0}\"" },

   // org.apache.commons.math.linear.RealVectorFormat
   { "unparseable real vector: \"{0}\"",
     "\u00e9chec d''analyse du vecteur r\u00e9el \"{0}\"" },

   // org.apache.commons.math.util.ResizableDoubleArray
   { "the index specified: {0} is larger than the current maximal index {1}",
     "l''index sp\u00e9cifi\u00e9 ({0}) d\u00e9passe l''index maximal courant ({1})" },
   { "elements cannot be retrieved from a negative array index {0}",
     "impossible d''extraire un \u00e9l\u00e9ment \u00e0 un index n\u00e9gatif ({0})" },
   { "cannot set an element at a negative index {0}",
     "impossible de mettre un \u00e9l\u00e9ment \u00e0 un index n\u00e9gatif ({0})" },
   { "cannot substitute an element from an empty array",
     "impossible de substituer un \u00e9l\u00e9ment dans un tableau vide" },

   // org.apache.commons.math.analysis.PolynomialFunctionLagrangeForm
   { "identical abscissas x[{0}] == x[{1}] == {2} cause division by zero",
     "division par z\u00e9ro caus\u00e9e par les abscisses identiques x[{0}] == x[{1}] == {2}" },

   // org.apache.commons.math.analysis.UnivariateRealSolverImpl
   { "function to solve cannot be null",
     "la fonction \u00e0 r\u00e9soudre ne peux pas \u00eatre nulle" },

   // org.apache.commons.math.analysis.LaguerreSolver
   { "function is not polynomial",
     "la fonction n''est pas p\u00f4lynomiale" },

   // org.apache.commons.math.analysis.NewtonSolver
   { "function is not differentiable",
     "la fonction n''est pas diff\u00e9rentiable" },

   // org.apache.commons.math.fraction.Fraction
   { "zero denominator in fraction {0}/{1}",
     "d\u00e9nominateur null dans le nombre rationnel {0}/{1}" },
   { "overflow in fraction {0}/{1}, cannot negate",
     "d\u00e9passement de capacit\u00e9 pour la fraction {0}/{1}, son signe ne peut \u00eatre chang\u00e9" },
   { "overflow, numerator too large after multiply: {0}",
     "d\u00e9passement de capacit\u00e9 pour le num\u00e9rateur apr\u00e8s multiplication : {0}" },
   { "the fraction to divide by must not be zero: {0}/{1}",
     "division par un nombre rationnel nul : {0}/{1}" },

   // org.apache.commons.math.geometry.Rotation
   { "zero norm for rotation axis",
     "norme nulle pour un axe de rotation" },

   // org.apache.commons.math.geometry.Vector3D
   // org.apache.commons.math.linear.RealVectorImpl
   { "cannot normalize a zero norm vector",
     "impossible de normer un vecteur de norme nulle" },
   { "zero norm",
     "norme nulle" },

   // org.apache.commons.math.analysis.UnivariateRealIntegratorImpl
   // org.apache.commons.math.analysis.UnivariateRealSolverImpl
   { "no result available",
     "aucun r\u00e9sultat n''est disponible" },

   // org.apache.commons.math.linear.BigMatrixImpl
   { "first {0} rows are not initialized yet",
     "les {0} premi\u00e8res lignes ne sont pas encore initialis\u00e9es" },
   { "first {0} columns are not initialized yet",
     "les {0} premi\u00e8res colonnes ne sont pas encore initialis\u00e9es" },

   // org.apache.commons.math.random.EmpiricalDistributionImpl
   { "distribution not loaded",
     "aucune distribution n''a \u00e9t\u00e9 charg\u00e9e" },

   // org.apache.commons.math.random.ValueServer
   { "unknown mode {0}, known modes: {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}) and {11} ({12})",
     "mode {0} inconnu, modes connus : {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}) et {11} ({12})" },
   { "digest not initialized",
     "mod\u00e8le empirique non initialis\u00e9" },

   // org.apache.commons.math.stat.descriptive.moment.GeometricMean
   // org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics
   // org.apache.commons.math.stat.descriptive.SummaryStatistics
   { "{0} values have been added before statistic is configured",
     "{0} valeurs ont \u00e9t\u00e9 ajout\u00e9es avant que la statistique ne soit configur\u00e9e" },

   // org.apache.commons.math.stat.descriptive.moment.Kurtosis
   { "statistics constructed from external moments cannot be incremented",
     "les statistiques bas\u00e9es sur des moments externes ne peuvent pas \u00eatre incr\u00e9ment\u00e9es" },
   { "statistics constructed from external moments cannot be cleared",
     "les statistiques bas\u00e9es sur des moments externes ne peuvent pas \u00eatre remises \u00e0 z\u00e9ro" },

   // org.apache.commons.math.distribution.ZipfDistributionImpl
   { "invalid number of elements {0} (must be positive)",
     "nombre d''\u00e9l\u00e9ments {0} invalide (doit \u00eatre positif)" },
   { "invalid exponent {0} (must be positive)",
     "exposant {0} invalide (doit \u00eatre positif)" },

   // org.apache.commons.math.transform.FastHadamardTransformer
   { "{0} is not a power of 2",
     "{0} n''est pas une puissance de 2" },

   // org.apache.commons.math.transform.FastFourierTransformer
   { "cannot compute 0-th root of unity, indefinite result",
     "impossible de calculer la racine z\u00e9roi\u00e8me de l''unit\u00e9, r\u00e9sultat ind\u00e9fini" },
   { "number of sample is not positive: {0}",
     "le nombre d''\u00e9chantillons n''est pas positif : {0}" },
   { "{0} is not a power of 2, consider padding for fix",
     "{0} n''est pas une puissance de 2, ajoutez des \u00e9l\u00e9ments pour corriger" },
   { "endpoints do not specify an interval: [{0}, {1}]",
     "les extr\u00e9mit\u00e9s ne constituent pas un intervalle : [{0}, {1}]" },
   { "some dimensions don't math: {0} != {1}",
     "certaines dimensions sont incoh\u00e9rentes : {0} != {1}" },

   // org.apache.commons.math.transform.FastCosineTransformer
   { "{0} is not a power of 2 plus one",
     "{0} n''est pas une puissance de 2 plus un" },

   // org.apache.commons.math.transform.FastSineTransformer
   { "first element is not 0: {0}",
     "le premier \u00e9l\u00e9ment n''est pas nul : {0}" },

   // org.apache.commons.math.util.OpenIntToDoubleHashMap
   { "map has been modified while iterating",
     "la table d''adressage a \u00e9t\u00e9 modifi\u00e9e pendant l''it\u00e9ration" },
   { "iterator exhausted",
     "it\u00e9ration achev\u00e9e" }

  };

}
