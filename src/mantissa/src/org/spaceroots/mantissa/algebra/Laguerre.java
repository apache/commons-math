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
 * This class implements Laguerre polynomials.

 * <p>Laguerre polynomials can be defined by the following recurrence
 * relations:
 * <pre>
 *        L<sub>0</sub>(X)   = 1
 *        L<sub>1</sub>(X)   = 1 - X
 *  (k+1) L<sub>k+1</sub>(X) = (2k + 1 - X) L<sub>k</sub>(X) - k L<sub>k-1</sub>(X)
 * </pre></p>

 * @version $Id$
 * @author L. Maisonobe

 */
public class Laguerre
  extends OrthogonalPolynomial {

  /** Generator for the Laguerre polynomials. */
  private static final CoefficientsGenerator generator =
    new CoefficientsGenerator(new RationalNumber(1l),
                              new RationalNumber(1l),
                              new RationalNumber(-1l)) {
    public void setRecurrenceCoefficients(int k) {
      // the recurrence relation is
      // (k+1) Lk+1(X) = (2k + 1 - X) Lk(X) - k Lk-1(X)
      long kP1 = k + 1;
      setRecurrenceCoefficients(new RationalNumber(2 * k + 1, kP1),
                                new RationalNumber(-1l, kP1),
                                new RationalNumber(k, kP1));
    }
  };

  /** Simple constructor.
   * Build a degree 0 Laguerre polynomial
   */
  public Laguerre() {
    super(0, generator);
  }

  /** Simple constructor.
   * Build a degree d Laguerre polynomial
   * @param degree degree of the polynomial
   */
  public Laguerre(int degree) {
    super(degree, generator);
  }

  private static final long serialVersionUID = 3213856667479179710L;

}
