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

public class PolynomialDoubleTest
  extends TestCase {

  public PolynomialDoubleTest(String name) {
    super(name);
  }

  public void testConstructors() {

    Polynomial.Double p = new Polynomial.Double(1.0, 3.0, -5.0);
    double[] a = p.getCoefficients();
    assertEquals(a.length, 3);
    assertEquals(-5.0, a[0], 1.0e-12);
    assertEquals(3.0,  a[1], 1.0e-12);
    assertEquals(1.0,  a[2], 1.0e-12);
    assertEquals(p.getDegree(), 2);

    assertEquals(1, new Polynomial.Double(0.0, 3.0, 5.0).getDegree());
    assertEquals(0, new Polynomial.Double(0.0, 0.0, 5.0).getDegree());
    assertEquals(0, new Polynomial.Double(0.0, 0.0, 0.0).getDegree());
    assertEquals(1, new Polynomial.Double(3.0, 5.0).getDegree());
    assertEquals(0, new Polynomial.Double(0.0, 5.0).getDegree());
    assertEquals(0, new Polynomial.Double(0.0, 0.0).getDegree());
    assertEquals(0, new Polynomial.Double(5.0).getDegree());
    assertEquals(0, new Polynomial.Double(0.0).getDegree());

  }

  public void testConversion() {
    Polynomial.Rational r = new Polynomial.Rational(1l, 3l, -5l);
    r = (Polynomial.Rational) r.multiply(new RationalNumber(1l, 2l));
    Polynomial.Double p = new Polynomial.Double(r);
    checkPolynomial(p, "-2.5 + 1.5 x + 0.5 x^2");
  }

  public void testString() {
    Polynomial.Double p = new Polynomial.Double(1.0, 3.0, -5.0);
    checkPolynomial(p, "-5.0 + 3.0 x + x^2");
    checkPolynomial(new Polynomial.Double(3.0, -2.0, 0.0),
                    "-2.0 x + 3.0 x^2");
    checkPolynomial(new Polynomial.Double(3.0, -2.0, 1.0),
                    "1.0 - 2.0 x + 3.0 x^2");
    checkPolynomial(new Polynomial.Double(3.0,  2.0, 0.0),
                    "2.0 x + 3.0 x^2");
    checkPolynomial(new Polynomial.Double(3.0,  2.0, 1.0),
                    "1.0 + 2.0 x + 3.0 x^2");
    checkPolynomial(new Polynomial.Double(3.0,  0.0, 1.0),
                    "1.0 + 3.0 x^2");
    checkPolynomial(new Polynomial.Double(0.0),
                    "0");
  }

  public void testAddition() {

    Polynomial.Double p1 = new Polynomial.Double(1.0, -2.0);
    Polynomial.Double p2 = new Polynomial.Double(0.0, -1.0, 2.0);
    assertTrue(p1.add(p2).isZero());

    p2 = p1.add(p1);
    checkPolynomial(p2, "-4.0 + 2.0 x");

    p1 = new Polynomial.Double(2.0, -4.0, 1.0);
    p2 = new Polynomial.Double(-2.0, 3.0, -1.0);
    p1 = p1.add(p2);
    assertEquals(1, p1.getDegree());
    checkPolynomial(p1, "-x");

  }

  public void testSubtraction() {

    Polynomial.Double p1 = new Polynomial.Double(1.0, -2.0);
    assertTrue(p1.subtract(p1).isZero());

    Polynomial.Double p2 = new Polynomial.Double(6.0, -2.0);
    p2 = p2.subtract(p1);
    checkPolynomial(p2, "5.0 x");

    p1 = new Polynomial.Double(2.0, -4.0, 1.0);
    p2 = new Polynomial.Double(2.0, 3.0, -1.0);
    p1 = p1.subtract(p2);
    assertEquals(1, p1.getDegree());
    checkPolynomial(p1, "2.0 - 7.0 x");

  }

  public void testMultiplication() {

    Polynomial.Double p1 = new Polynomial.Double(2.0, -3.0);
    Polynomial.Double p2 = new Polynomial.Double(1.0, 2.0, 3.0);
    checkPolynomial(p1.multiply(p2), "-9.0 + x^2 + 2.0 x^3");

    p1 = new Polynomial.Double(1.0, 0.0);
    p2 = p1;
    for (int i = 2; i < 10; ++i) {
      p2 = p2.multiply(p1);
      checkPolynomial(p2, "x^" + i);
    }

  }

  public void checkPolynomial(Polynomial.Double p, String reference) {
    assertEquals(reference, p.toString());
  }

  public static Test suite() {
    return new TestSuite(PolynomialDoubleTest.class);
  }

}
