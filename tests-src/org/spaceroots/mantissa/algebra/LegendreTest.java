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

public class LegendreTest
  extends TestCase {

  public LegendreTest(String name) {
    super(name);
  }

  public void testOne() {
    assertTrue(new Legendre().isOne());
  }

  public void testFirstPolynomials() {

    checkLegendre(new Legendre(3),  2l, "-3 x + 5 x^3");
    checkLegendre(new Legendre(2),  2l, "-1 + 3 x^2");
    checkLegendre(new Legendre(1),  1l, "x");
    checkLegendre(new Legendre(0),  1l, "1");

    checkLegendre(new Legendre(7), 16l, "-35 x + 315 x^3 - 693 x^5 + 429 x^7");
    checkLegendre(new Legendre(6), 16l, "-5 + 105 x^2 - 315 x^4 + 231 x^6");
    checkLegendre(new Legendre(5),  8l, "15 x - 70 x^3 + 63 x^5");
    checkLegendre(new Legendre(4),  8l, "3 - 30 x^2 + 35 x^4");

  }

  public void testDifferentials() {
    for (int k = 0; k < 12; ++k) {

      Polynomial.Rational Pk0 = new Legendre(k);
      Polynomial.Rational Pk1 = (Polynomial.Rational) Pk0.getDerivative();
      Polynomial.Rational Pk2 = (Polynomial.Rational) Pk1.getDerivative();

      Polynomial.Rational g0 = new Polynomial.Rational(k * (k + 1));
      Polynomial.Rational g1 = new Polynomial.Rational(-2l, 0l);
      Polynomial.Rational g2 = new Polynomial.Rational(-1l, 0l, 1l);

      Polynomial.Rational Pk0g0 = Pk0.multiply(g0);
      Polynomial.Rational Pk1g1 = Pk1.multiply(g1);
      Polynomial.Rational Pk2g2 = Pk2.multiply(g2);

      Polynomial.Rational d = Pk0g0.add(Pk1g1.add(Pk2g2));
      assertTrue(d.isZero());

    }
  }

  public void testHighDegree() {
    checkLegendre(new Legendre(40), 274877906944l,
                  "34461632205"
                + " - 28258538408100 x^2"
                + " + 3847870979902950 x^4"
                + " - 207785032914759300 x^6"
                + " + 5929294332103310025 x^8"
                + " - 103301483474866556880 x^10"
                + " + 1197358103913226000200 x^12"
                + " - 9763073770369381232400 x^14"
                + " + 58171647881784229843050 x^16"
                + " - 260061484647976556945400 x^18"
                + " + 888315281771246239250340 x^20"
                + " - 2345767627188139419665400 x^22"
                + " + 4819022625419112503443050 x^24"
                + " - 7710436200670580005508880 x^26"
                + " + 9566652323054238154983240 x^28"
                + " - 9104813935044723209570256 x^30"
                + " + 6516550296251767619752905 x^32"
                + " - 3391858621221953912598660 x^34"
                + " + 1211378079007840683070950 x^36"
                + " - 265365894974690562152100 x^38"
                + " + 26876802183334044115405 x^40");
  }

  public void checkLegendre(Legendre p, long denominator, String reference) {
    assertTrue(p.multiply(denominator).toString().equals(reference));
  }

  public static Test suite() {
    return new TestSuite(LegendreTest.class);
  }

}
