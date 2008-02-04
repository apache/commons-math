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

import java.math.BigInteger;

import junit.framework.*;

public class PolynomialRationalTest
  extends TestCase {

  public PolynomialRationalTest(String name) {
    super(name);
  }

  public void testZero() {
    assertTrue(new Polynomial.Rational().isZero());
  }

  public void testConstructors() {

    Polynomial.Rational p = new Polynomial.Rational(1l, 3l, -5l);
    RationalNumber[]  a = p.getCoefficients();
    assertEquals(a.length, 3);
    assertEquals(new RationalNumber(-5l), a[0]);
    assertEquals(new RationalNumber(3l), a[1]);
    assertEquals(new RationalNumber(1l), a[2]);
    assertEquals(2, p.getDegree());

    assertEquals(1, new Polynomial.Rational(0l, 3l, 5l).getDegree());
    assertEquals(0, new Polynomial.Rational(0l, 0l, 5l).getDegree());
    assertEquals(0, new Polynomial.Rational(0l, 0l, 0l).getDegree());
    assertEquals(1, new Polynomial.Rational(3l, 5l).getDegree());
    assertEquals(0, new Polynomial.Rational(0l, 5l).getDegree());
    assertEquals(0, new Polynomial.Rational(0l, 0l).getDegree());
    assertEquals(0, new Polynomial.Rational(5l).getDegree());
    assertEquals(0, new Polynomial.Rational(0l).getDegree());

  }

  public void testString() {

    Polynomial.Rational p = new Polynomial.Rational(1l, 3l, -5l);
    checkPolynomial(p, "-5 + 3 x + x^2");

    checkPolynomial(new Polynomial.Rational(3l, -2l, 0l), "-2 x + 3 x^2");
    checkPolynomial(new Polynomial.Rational(3l, -2l, 1l), "1 - 2 x + 3 x^2");
    checkPolynomial(new Polynomial.Rational(3l,  2l, 0l), "2 x + 3 x^2");
    checkPolynomial(new Polynomial.Rational(3l,  2l, 1l), "1 + 2 x + 3 x^2");
    checkPolynomial(new Polynomial.Rational(3l,  0l, 1l), "1 + 3 x^2");
    checkPolynomial(new Polynomial.Rational(0l), "0");

  }

  public void testAddition() {

    Polynomial.Rational p1 = new Polynomial.Rational(1l, -2l);
    Polynomial.Rational p2 = new Polynomial.Rational(0l, -1l, 2l);
    assertTrue(p1.add(p2).isZero());

    p2 = p1.add(p1);
    checkPolynomial(p2, "-4 + 2 x");

    p1 = new Polynomial.Rational(2l, -4l, 1l);
    p2 = new Polynomial.Rational(-2l, 3l, -1l);
    p1 = p1.add(p2);
    assertEquals(1, p1.getDegree());
    checkPolynomial(p1, "-x");

  }

  public void testSubtraction() {

    Polynomial.Rational p1 = new Polynomial.Rational(1l, -2l);
    assertTrue(p1.subtract(p1).isZero());

    Polynomial.Rational p2 = new Polynomial.Rational(6l, -2l);
    p2 = p2.subtract(p1);
    checkPolynomial(p2, "5 x");

    p1 = new Polynomial.Rational(2l, -4l, 1l);
    p2 = new Polynomial.Rational(2l, 3l, -1l);
    p1 = p1.subtract(p2);
    assertEquals(1, p1.getDegree());
    checkPolynomial(p1, "2 - 7 x");

  }

  public void testMultiplication() {

    Polynomial.Rational p1 = new Polynomial.Rational(2l, -3l);
    Polynomial.Rational p2 = new Polynomial.Rational(1l, 2l, 3l);
    checkPolynomial(p1.multiply(p2), "-9 + x^2 + 2 x^3");

    p1 = new Polynomial.Rational(1l, 0l);
    p2 = p1;
    for (int i = 2; i < 10; ++i) {
      p2 = p2.multiply(p1);
      checkPolynomial(p2, "x^" + i);
    }

  }

  public void testLCM() {
    Polynomial.Rational p = new Polynomial.Rational(new RationalNumber(2l, 5l),
                                                    new RationalNumber(-1l, 6l),
                                                    new RationalNumber(3l, 4l));
    checkPolynomial(p, "3/4 - 1/6 x + 2/5 x^2");
    BigInteger lcm = p.getDenominatorsLCM();
    assertEquals(BigInteger.valueOf(60l), lcm);
    p = (Polynomial.Rational) p.multiply(lcm);
    checkPolynomial(p, "45 - 10 x + 24 x^2");
  }

  public void testEuclidianDivision() {
    Polynomial.Rational p = new Polynomial.Rational(4l, 6l, -3l);
    Polynomial.Rational q = new Polynomial.Rational(3l, 2l);
    Polynomial.DivisionResult res = Polynomial.Rational.euclidianDivision(p, q);
    checkPolynomial(res.quotient,  "10/9 + 4/3 x");
    checkPolynomial(res.remainder, "-47/9");
  }

  public void checkPolynomial(Polynomial.Rational p, String reference) {
    assertEquals(reference, p.toString());
  }

  public static Test suite() {
    return new TestSuite(PolynomialRationalTest.class);
  }

}
