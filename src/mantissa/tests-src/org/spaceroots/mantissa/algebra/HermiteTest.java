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

public class HermiteTest
  extends TestCase {

  public HermiteTest(String name) {
    super(name);
  }

  public void testOne() {
    assertTrue(new Hermite().isOne());
  }

  public void testFirstPolynomials() {

    checkPolynomial(new Hermite(3), "-12 x + 8 x^3");
    checkPolynomial(new Hermite(2), "-2 + 4 x^2");
    checkPolynomial(new Hermite(1), "2 x");
    checkPolynomial(new Hermite(0), "1");

    checkPolynomial(new Hermite(7), "-1680 x + 3360 x^3 - 1344 x^5 + 128 x^7");
    checkPolynomial(new Hermite(6), "-120 + 720 x^2 - 480 x^4 + 64 x^6");
    checkPolynomial(new Hermite(5), "120 x - 160 x^3 + 32 x^5");
    checkPolynomial(new Hermite(4), "12 - 48 x^2 + 16 x^4");

  }

  public void testDifferentials() {
    for (int k = 0; k < 12; ++k) {

      Polynomial.Rational Hk0 = new Hermite(k);
      Polynomial.Rational Hk1 = (Polynomial.Rational) Hk0.getDerivative();
      Polynomial.Rational Hk2 = (Polynomial.Rational) Hk1.getDerivative();

      Polynomial.Rational g0 = new Polynomial.Rational(2l * k);
      Polynomial.Rational g1 = new Polynomial.Rational(-2l, 0l);
      Polynomial.Rational g2 = new Polynomial.Rational(1l);

      Polynomial.Rational Hk0g0 = Hk0.multiply(g0);
      Polynomial.Rational Hk1g1 = Hk1.multiply(g1);
      Polynomial.Rational Hk2g2 = Hk2.multiply(g2);

      Polynomial.Rational d = Hk0g0.add(Hk1g1.add(Hk2g2));
      assertTrue(d.isZero());

    }
  }

  public void checkPolynomial(Polynomial.Rational p, String reference) {
    assertTrue(p.toString().equals(reference));
  }

  public static Test suite() {
    return new TestSuite(HermiteTest.class);
  }

}
