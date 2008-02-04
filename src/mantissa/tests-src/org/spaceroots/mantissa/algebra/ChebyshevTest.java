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

import junit.framework.*;

public class ChebyshevTest
  extends TestCase {

  public ChebyshevTest(String name) {
    super(name);
  }

  public void testOne() {
    assertTrue(new Chebyshev().isOne());
  }

  public void testFirstPolynomials() {

    checkPolynomial(new Chebyshev(3), "-3 x + 4 x^3");
    checkPolynomial(new Chebyshev(2), "-1 + 2 x^2");
    checkPolynomial(new Chebyshev(1), "x");
    checkPolynomial(new Chebyshev(0), "1");

    checkPolynomial(new Chebyshev(7), "-7 x + 56 x^3 - 112 x^5 + 64 x^7");
    checkPolynomial(new Chebyshev(6), "-1 + 18 x^2 - 48 x^4 + 32 x^6");
    checkPolynomial(new Chebyshev(5), "5 x - 20 x^3 + 16 x^5");
    checkPolynomial(new Chebyshev(4), "1 - 8 x^2 + 8 x^4");

  }

  public void testBounds() {
    for (int k = 0; k < 12; ++k) {
      OrthogonalPolynomial Tk = new Chebyshev(k);
      for (double x = -1.0; x <= 1.0; x += 0.02) {
        assertTrue(Math.abs(Tk.valueAt(x)) < (1.0 + 1.0e-12));
      }
    }
  }

  public void testDifferentials() {
    for (int k = 0; k < 12; ++k) {

      Polynomial.Rational Tk0 = new Chebyshev(k);
      Polynomial.Rational Tk1 = (Polynomial.Rational) Tk0.getDerivative();
      Polynomial.Rational Tk2 = (Polynomial.Rational) Tk1.getDerivative();

      Polynomial.Rational g0 = new Polynomial.Rational(k * k);
      Polynomial.Rational g1 = new Polynomial.Rational(-1l, 0l);
      Polynomial.Rational g2 = new Polynomial.Rational(-1l, 0l, 1l);

      Polynomial.Rational Tk0g0 = Tk0.multiply(g0);
      Polynomial.Rational Tk1g1 = Tk1.multiply(g1);
      Polynomial.Rational Tk2g2 = Tk2.multiply(g2);

      Polynomial.Rational d = Tk0g0.add(Tk1g1.add(Tk2g2));
      assertTrue(d.isZero());

    }
  }

  public void checkPolynomial(Polynomial.Rational p, String reference) {
    assertTrue(p.toString().equals(reference));
  }

  public static Test suite() {
    return new TestSuite(ChebyshevTest.class);
  }

}
