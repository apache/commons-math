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

public abstract class CoefficientsGenerator {

  /** Build a generator with coefficients for two polynomials.
   * <p>The first polynomial must be a degree 0 polynomial
   * P<sub>0</sub>(X)=a<sub>0,0</sub> and the second polynomial
   * must be a degree 1 polynomial P<sub>1</sub>(X)=a<sub>0,1</sub>
   * +a<sub>1,1</sub>X</p>
   * @param a00 constant term for the degree 0 polynomial
   * @param a01 constant term for the degree 1 polynomial
   * @param a11 X term for the degree 1 polynomial
   */
  protected CoefficientsGenerator(RationalNumber a00,
                                  RationalNumber a01, RationalNumber a11) {
    l = new ArrayList();
    l.add(a00);
    l.add(a01);
    l.add(a11);
    maxDegree = 1;
  }

  /** Set the recurrence coefficients.
   * @param b2k b<sub>2,k</sub> coefficient (b<sub>2,k</sub> = a<sub>2,k</sub> / a<sub>1,k</sub>)
   * @param b3k b<sub>3,k</sub> coefficient (b<sub>3,k</sub> = a<sub>3,k</sub> / a<sub>1,k</sub>)
   * @param b4k b<sub>4,k</sub> coefficient (b<sub>4,k</sub> = a<sub>4,k</sub> / a<sub>1,k</sub>)
   */
  protected void setRecurrenceCoefficients(RationalNumber b2k,
                                           RationalNumber b3k,
                                           RationalNumber b4k) {
    this.b2k = b2k;
    this.b3k = b3k;
    this.b4k = b4k;
  }

  /** Set the recurrence coefficients.
   * The recurrence relation is
   *  <pre>a<sub>1,k</sub> O<sub>k+1</sub>(X) =(a<sub>2,k</sub> + a<sub>3,k</sub> X) O<sub>k</sub>(X) - a<sub>4,k</sub> O<sub>k-1</sub>(X)</pre>
   * the method must call {@link #setRecurrenceCoefficients(RationalNumber,
   * RationalNumber, RationalNumber)} to provide the coefficients
   * @param k index of the current step
   */
  protected abstract void setRecurrenceCoefficients(int k);

  /** Compute all the polynomial coefficients up to a given degree.
   * @param degree maximal degree
   */
  private void computeUpToDegree(int degree) {

    int startK = (maxDegree - 1) * maxDegree / 2;
    for (int k = maxDegree; k < degree; ++k) {

      // start indices of two previous polynomials Ok(X) and Ok-1(X)
      int startKm1 = startK;
      startK += k;

      // a1k Ok+1(X) = (a2k + a3k X) Ok(X) - a4k Ok-1(X)
      // we use bik = aik/a1k
      setRecurrenceCoefficients(k);

      RationalNumber ckPrev = null;
      RationalNumber ck     = (RationalNumber) l.get(startK);
      RationalNumber ckm1   = (RationalNumber) l.get(startKm1);

      // degree 0 coefficient
      l.add(ck.multiply(b2k).subtract(ckm1.multiply(b4k)));

      // degree 1 to degree k-1 coefficients
      for (int i = 1; i < k; ++i) {
        ckPrev = ck;
        ck     = (RationalNumber) l.get(startK + i);
        ckm1   = (RationalNumber) l.get(startKm1 + i);
        l.add(ck.multiply(b2k).add(ckPrev.multiply(b3k)).subtract(ckm1.multiply(b4k)));
      }

      // degree k coefficient
      ckPrev = ck;
      ck     = (RationalNumber) l.get(startK + k);
      l.add(ck.multiply(b2k).add(ckPrev.multiply(b3k)));

      // degree k+1 coefficient
      l.add(ck.multiply(b3k));

    }

    maxDegree = degree;

  }

  /** Get the coefficients array for a given degree.
   * @param degree degree of the polynomial
   * @return coefficients array
   */
  public RationalNumber[] getCoefficients(int degree) {

    synchronized (this) {
      if (degree > maxDegree) {
        computeUpToDegree(degree);
      }
    }

    // coefficient  for polynomial 0 is  l [0]
    // coefficients for polynomial 1 are l [1] ... l [2] (degrees 0 ... 1)
    // coefficients for polynomial 2 are l [3] ... l [5] (degrees 0 ... 2)
    // coefficients for polynomial 3 are l [6] ... l [9] (degrees 0 ... 3)
    // coefficients for polynomial 4 are l[10] ... l[14] (degrees 0 ... 4)
    // coefficients for polynomial 5 are l[15] ... l[20] (degrees 0 ... 5)
    // coefficients for polynomial 6 are l[21] ... l[27] (degrees 0 ... 6)
    // ...
    int start = degree * (degree + 1) / 2;

    RationalNumber[] a = new RationalNumber[degree + 1];
    for (int i = 0; i <= degree; ++i) {
      a[i] = (RationalNumber) l.get(start + i);
    }

    return a;

  }
  
  /** List holding the coefficients of the polynomials computed so far. */
  private ArrayList l;

  /** Maximal degree of the polynomials computed so far. */
  private int maxDegree;

  /** b<sub>2,k</sub> coefficient to initialize
   * (b<sub>2,k</sub> = a<sub>2,k</sub> / a<sub>1,k</sub>). */
  private RationalNumber b2k;

  /** b<sub>3,k</sub> coefficient to initialize
   * (b<sub>3,k</sub> = a<sub>3,k</sub> / a<sub>1,k</sub>). */
  private RationalNumber b3k;

  /** b<sub>4,k</sub> coefficient to initialize
   * (b<sub>4,k</sub> = a<sub>4,k</sub> / a<sub>1,k</sub>). */
  private RationalNumber b4k;

}
