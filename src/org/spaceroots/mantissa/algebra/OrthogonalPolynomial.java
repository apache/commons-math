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

package org.spaceroots.mantissa.algebra;

/**
 * This class is the base class for orthogonal polynomials.

 * <p>Orthogonal polynomials can be defined by recurrence relations like:
 * <pre>
 *      O<sub>0</sub>(X)   = some 0 degree polynomial
 *      O<sub>1</sub>(X)   = some first degree polynomial
 *  a<sub>1,k</sub> O<sub>k+1</sub>(X) = (a<sub>2,k</sub> + a<sub>3,k</sub> X) O<sub>k</sub>(X) - a<sub>4,k</sub> O<sub>k-1</sub>(X)
 * </pre>
 * where a<sub>1,k</sub>, a<sub>2,k</sub>, a<sub>3,k</sub> and
 * a<sub>4,k</sub> are simple expressions which either are
 * constants or depend on k.</p>

 * @version $Id$
 * @author L. Maisonobe

 */
public abstract class OrthogonalPolynomial
  extends Polynomial.Rational {

  /** Simple constructor.
   * Build a degree d orthogonal polynomial
   * @param degree degree of the polynomial
   * @param generator coefficients generator for the current type of polynomials
   */
  protected OrthogonalPolynomial(int degree, CoefficientsGenerator generator) {
    a       = generator.getCoefficients(degree);
  }

}
