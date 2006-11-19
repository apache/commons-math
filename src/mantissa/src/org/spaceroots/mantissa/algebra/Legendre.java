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

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements Legendre polynomials.

 * <p>Legendre polynomials can be defined by the following recurrence
 * relations:
 * <pre>
 *        P0(X)   = 1
 *        P1(X)   = X
 *  (k+1) Pk+1(X) = (2k+1) X Pk(X) - k Pk-1(X)
 * </pre></p>

 * @version $Id: Legendre.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class Legendre
  extends OrthogonalPolynomial {

  /** Simple constructor.
   * Build a degree 0 Legendre polynomial
   */
  public Legendre() {
    super(0, l, maxDegree);
  }

  /** Simple constructor.
   * Build a degree d Legendre polynomial
   * @param d degree of the polynomial
   */
  public Legendre(int d) {
    super(d, l, maxDegree);
  }

  /** Initialize the recurrence coefficients.
   * The recurrence relation is
   *  <pre>(k+1) Pk+1(X) = (2k+1) X Pk(X) - k Ok-1(X)</pre>
   * @param k index of the current step
   * @param b2k coefficient to initialize (b2k = a2k / a1k)
   * @param b3k coefficient to initialize (b3k = a3k / a1k)
   * @param b4k coefficient to initialize (b4k = a4k / a1k)
   */
  protected void initRecurrenceCoefficients(int k,
                                            RationalNumber b2k,
                                            RationalNumber b3k,
                                            RationalNumber b4k) {
    long kP1 = k + 1;
    b2k.reset(0l);
    b3k.reset(2 * k + 1, kP1);
    b4k.reset(k, kP1);
  }

  /** Set the maximal degree of already computed polynomials.
   * @param d maximal degree of already computed polynomials
   */
  protected void setMaxDegree(int d) {
    maxDegree = d;
  }

  private static final long serialVersionUID = 428266828791532209L;

  /** List holding the coefficients of the polynomials computed so far. */
  private static List l;

  /** Maximal degree of the polynomials computed so far. */
  private static int maxDegree;

  /** Build the first two polynomials. */
  static {

    l = new ArrayList ();

    // P0(X)  = 1
    l.add(new RationalNumber(1l));

    // P1(X)  = X
    l.add(new RationalNumber(0l));
    l.add(new RationalNumber(1l));

    maxDegree = 1;

  }

}
