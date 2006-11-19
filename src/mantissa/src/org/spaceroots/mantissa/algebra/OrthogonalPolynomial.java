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

import java.util.List;

/**
 * This class is the base class for orthogonal polynomials.

 * <p>Orthogonal polynomials can be defined by recurrence relations like:
 * <pre>
 *      O0(X)   = some 0 degree polynomial
 *      O1(X)   = some first degree polynomial
 *  a1k Ok+1(X) = (a2k + a3k X) Ok(X) - a4k Ok-1(X)
 * </pre>
 * where a0k, a1k, a2k and a3k are simple expressions which either are
 * constants or depend on k.</p>

 * @version $Id: OrthogonalPolynomial.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */
public abstract class OrthogonalPolynomial
  extends Polynomial.Rational {

  /** Simple constructor.
   * Build a degree d orthogonal polynomial
   * @param d degree of the polynomial
   * @param l list containing all coefficients already computed
   * @param maxDegree maximal degree of computed coefficients, this
   * coefficient <em>must</em> be greater or equal to 1, i.e. the
   * derived class <em>must</em> have initialized the first two
   * polynomials of degree 0 and 1 before this constructor can be
   * called.
   */
  protected OrthogonalPolynomial(int d, List l, int maxDegree) {
    if (d > maxDegree) {
      computeUpToDegree(d, l, maxDegree);
    }

    // coefficient  for polynomial 0 is  l [0]
    // coefficient  for polynomial 1 are l [1] ... l [2] (degrees 0 ... 1)
    // coefficients for polynomial 2 are l [3] ... l [5] (degrees 0 ... 2)
    // coefficients for polynomial 3 are l [6] ... l [9] (degrees 0 ... 3)
    // coefficients for polynomial 4 are l[10] ... l[14] (degrees 0 ... 4)
    // coefficients for polynomial 5 are l[15] ... l[20] (degrees 0 ... 5)
    // coefficients for polynomial 6 are l[21] ... l[27] (degrees 0 ... 6)
    // ...
    int start = d * (d + 1) / 2;

    a = new RationalNumber[d+1];
    for (int i = 0; i <= d; ++i) {
      a[i] = new RationalNumber((RationalNumber) l.get(start + i));
    }

    unknown = null;

  }

  /** Initialize the recurrence coefficients.
   * The recurrence relation is
   *  <pre>a1k Ok+1(X) = (a2k + a3k X) Ok(X) - a4k Ok-1(X)</pre>
   * @param k index of the current step
   * @param b2k coefficient to initialize (b2k = a2k / a1k)
   * @param b3k coefficient to initialize (b3k = a3k / a1k)
   * @param b4k coefficient to initialize (b4k = a4k / a1k)
   */
  protected abstract void initRecurrenceCoefficients(int k,
                                                     RationalNumber b2k,
                                                     RationalNumber b3k,
                                                     RationalNumber b4k);

  /** Set the maximal degree of already computed polynomials.
   * @param d maximal degree of already computed polynomials
   */
  protected abstract void setMaxDegree(int d);

  /** Compute all the polynomial coefficients up to a given degree.
   * @param d maximal degree
   * @param l list containing all coefficients already computed
   * @param maxDegree maximal degree of computed coefficients
   */
  protected void computeUpToDegree(int d, List l, int maxDegree) {

    RationalNumber b2k = new RationalNumber();
    RationalNumber b3k = new RationalNumber();
    RationalNumber b4k = new RationalNumber();

    int startK = (maxDegree - 1) * maxDegree / 2;
    for (int k = maxDegree; k < d; ++k) {

      // start indices of two previous polynomials Ok(X) and Ok-1(X)
      int startKm1 = startK;
      startK += k;

      // a1k Ok+1(X) = (a2k + a3k X) Ok(X) - a4k Ok-1(X)
      // we use bik = aik/a1k
      initRecurrenceCoefficients(k, b2k, b3k, b4k);

      RationalNumber ckPrev = null;
      RationalNumber ck     = (RationalNumber)l.get(startK);
      RationalNumber ckm1   = (RationalNumber)l.get(startKm1);

      // degree 0 coefficient
      RationalNumber coeff  = RationalNumber.multiply(ck, b2k);
      coeff.multiplyAndSubtractFromSelf(ckm1, b4k);
      l.add(coeff);

      // degree 1 to degree k-1 coefficients
      for (int i = 1; i < k; ++i) {
        ckPrev = ck;
        ck     = (RationalNumber)l.get(startK + i);
        ckm1   = (RationalNumber)l.get(startKm1 + i);
        coeff  = RationalNumber.multiply(ck, b2k);
        coeff.multiplyAndAddToSelf(ckPrev, b3k);
        coeff.multiplyAndSubtractFromSelf(ckm1, b4k);
        l.add(coeff);
      }

      // degree k coefficient
      ckPrev = ck;
      ck     = (RationalNumber)l.get(startK + k);
      coeff  = RationalNumber.multiply(ck, b2k);
      coeff.multiplyAndAddToSelf(ckPrev, b3k);
      l.add(coeff);

      // degree k+1 coefficient
      l.add(RationalNumber.multiply(ck, b3k));

    }

    setMaxDegree(d);

  }

}
