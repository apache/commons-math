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

public class LaguerreTest
  extends TestCase {

  public LaguerreTest(String name) {
    super(name);
  }

  public void testOne() {
    assertTrue(new Laguerre().isOne());
  }

  public void testFirstPolynomials() {

    checkLaguerre(new Laguerre(3), 6l, "6 - 18 x + 9 x^2 - x^3");
    checkLaguerre(new Laguerre(2), 2l, "2 - 4 x + x^2");
    checkLaguerre(new Laguerre(1), 1l, "1 - x");
    checkLaguerre(new Laguerre(0), 1l, "1");

    checkLaguerre(new Laguerre(7), 5040l,
                  "5040 - 35280 x + 52920 x^2 - 29400 x^3"
                  + " + 7350 x^4 - 882 x^5 + 49 x^6 - x^7");
    checkLaguerre(new Laguerre(6),  720l,
                  "720 - 4320 x + 5400 x^2 - 2400 x^3 + 450 x^4"
                  + " - 36 x^5 + x^6");
    checkLaguerre(new Laguerre(5),  120l,
                  "120 - 600 x + 600 x^2 - 200 x^3 + 25 x^4 - x^5");
    checkLaguerre(new Laguerre(4),   24l,
                  "24 - 96 x + 72 x^2 - 16 x^3 + x^4");

  }

  public void testDifferentials() {
    for (int k = 0; k < 12; ++k) {

      Polynomial.Rational Lk0 = new Laguerre(k);
      Polynomial.Rational Lk1 = (Polynomial.Rational) Lk0.getDerivative();
      Polynomial.Rational Lk2 = (Polynomial.Rational) Lk1.getDerivative();

      Polynomial.Rational g0 = new Polynomial.Rational(k);
      Polynomial.Rational g1 = new Polynomial.Rational(-1l, 1l);
      Polynomial.Rational g2 = new Polynomial.Rational(1l, 0l);

      Polynomial.Rational Lk0g0 = Lk0.multiply(g0);
      Polynomial.Rational Lk1g1 = Lk1.multiply(g1);
      Polynomial.Rational Lk2g2 = Lk2.multiply(g2);

      Polynomial.Rational d = Lk0g0.add(Lk1g1.add(Lk2g2));
      assertTrue(d.isZero());

    }
  }

  public void checkLaguerre(Laguerre p, long denominator, String reference) {
    assertTrue(p.multiply(denominator).toString().equals(reference));
  }

  public static Test suite() {
    return new TestSuite(LaguerreTest.class);
  }

}
