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
 * This class implements Hermite polynomials.

 * <p>Hermite polynomials can be defined by the following recurrence
 * relations:
 * <pre>
 *  H<sub>0</sub>(X)   = 1
 *  H<sub>1</sub>(X)   = 2X
 *  H<sub>k+1</sub>(X) = 2X H<sub>k</sub>(X) - 2k H<sub>k-1</sub>(X)
 * </pre></p>

 * @version $Id$
 * @author L. Maisonobe

 */
public class Hermite
  extends OrthogonalPolynomial {

  /** Generator for the Hermite polynomials. */
  private static final CoefficientsGenerator generator =
    new CoefficientsGenerator(new RationalNumber(1l),
                              new RationalNumber(0l),
                              new RationalNumber(2l)) {
    public void setRecurrenceCoefficients(int k) {
      // the recurrence relation is
      // Hk+1(X) = 2X Hk(X) - 2k Hk-1(X)
      setRecurrenceCoefficients(new RationalNumber(0l),
                                new RationalNumber(2l),
                                new RationalNumber(k * 2l));
    }
  };

  /** Simple constructor.
   * Build a degree 0 Hermite polynomial
   */
  public Hermite() {
    super(0, generator);
  }

  /** Simple constructor.
   * Build a degree d Hermite polynomial
   * @param degree degree of the polynomial
   */
  public Hermite(int degree) {
    super(degree, generator);
  }

  private static final long serialVersionUID = 7910082423686662133L;

}
