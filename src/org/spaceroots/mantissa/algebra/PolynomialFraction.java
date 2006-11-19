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

/**
 * This class implements fractions of polynomials with one unknown and
 * rational coefficients.

 * @version $Id: PolynomialFraction.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class PolynomialFraction {

  /**
   * Simple constructor.
   * Build a constant null fraction
   */
  public PolynomialFraction() {
    this(new Polynomial.Rational(new RationalNumber(0l)),
         new Polynomial.Rational(new RationalNumber(1l)));
  }

  /**
   * Simple constructor.
   * Build a fraction from a numerator and a denominator.
   * @param numerator numerator of the fraction
   * @param denominator denominator of the fraction
   * @exception ArithmeticException if the denominator is null
   */
  public PolynomialFraction(long numerator, long denominator) {
    this(new Polynomial.Rational(new RationalNumber(numerator)),
         new Polynomial.Rational(new RationalNumber(denominator)));
  }

  /**
   * Simple constructor.
   * Build a fraction from a numerator and a denominator.
   * @param numerator numerator of the fraction
   * @param denominator denominator of the fraction
   * @exception ArithmeticException if the denominator is null
   */
  public PolynomialFraction(BigInteger numerator, BigInteger denominator) {
    this(new Polynomial.Rational(new RationalNumber(numerator)),
         new Polynomial.Rational(new RationalNumber(denominator)));
  }

  /**
   * Simple constructor.
   * Build a fraction from a numerator and a denominator.
   * @param numerator numerator of the fraction
   * @param denominator denominator of the fraction
   * @exception ArithmeticException if the denominator is null
   */
  public PolynomialFraction(RationalNumber numerator,
                            RationalNumber denominator) {
    this(new Polynomial.Rational(numerator),
         new Polynomial.Rational(denominator));
  }

  /**
   * Simple constructor.
   * Build a fraction from a numerator and a denominator.
   * @param numerator numerator of the fraction
   * @param denominator denominator of the fraction
   * @exception ArithmeticException if the denominator is null
   */
  public PolynomialFraction(Polynomial.Rational numerator,
                            Polynomial.Rational denominator) {

    if (denominator.isZero()) {
      throw new ArithmeticException("null denominator");
    }

    p = new Polynomial.Rational(numerator);
    q = new Polynomial.Rational(denominator);

    RationalNumber[] a = q.getCoefficients();
    if (a[a.length - 1].isNegative()) {
      p.negateSelf();
      q.negateSelf();
    }

    simplify();

  }

  /**
   * Simple constructor.
   * Build a fraction from a single integer
   * @param l value of the fraction
   */
  public PolynomialFraction(long l) {
    this(l, 1l);
  }

  /**
   * Simple constructor.
   * Build a fraction from a single integer
   * @param i value of the fraction
   */
  public PolynomialFraction(BigInteger i) {
    this(i, BigInteger.ONE);
  }

  /**
   * Simple constructor.
   * Build a fraction from a single rational number
   * @param r value of the fraction
   */
  public PolynomialFraction(RationalNumber r) {
    this(r.getNumerator(), r.getDenominator());
  }

  /**
   * Simple constructor.
   * Build a fraction from a single Polynom
   * @param p value of the fraction
   */
  public PolynomialFraction(Polynomial.Rational p) {
    this(p, new Polynomial.Rational(new RationalNumber(1l)));
  }

  /**
   * Copy-constructor.
   * @param f fraction to copy
   */
  public PolynomialFraction(PolynomialFraction f) {
    p = new Polynomial.Rational(f.p);
    q = new Polynomial.Rational(f.q);
  }

  /**
   * Negate the instance
   */
  public void negateSelf() {
    p.negateSelf();
  }

  /**
   * Negate a fraction.
   * @param f fraction to negate
   * @return a new fraction which is the opposite of f
   */
  public static PolynomialFraction negate(PolynomialFraction f) {
    PolynomialFraction copy = new PolynomialFraction(f);
    copy.negateSelf();
    return copy;
  }

  /**
   * Add a fraction to the instance.
   * @param f fraction to add.
   */
  public void addToSelf(PolynomialFraction f) {
    PolynomialFraction sum = add(this, f);
    p = sum.p;
    q = sum.q;
  }

  /** Add two fractions.
   * @param f1 first fraction
   * @param f2 second fraction
   * @return a new fraction which is the sum of f1 and f2
   */
  public static PolynomialFraction add(PolynomialFraction f1,
                                       PolynomialFraction f2) {
    Polynomial.Rational num =
      Polynomial.Rational.add(Polynomial.Rational.multiply(f1.p, f2.q),
                              Polynomial.Rational.multiply(f2.p, f1.q));
    Polynomial.Rational den = Polynomial.Rational.multiply(f1.q, f2.q);
    return new PolynomialFraction(num, den);
  }

  /**
   * Subtract a fraction to the instance.
   * @param f fraction to subtract.
   */
  public void subtractFromSelf(PolynomialFraction f) {
    PolynomialFraction diff = subtract(this, f);
    p = diff.p;
    q = diff.q;
  }

  /** Subtract two fractions.
   * @param f1 first fraction
   * @param f2 second fraction
   * @return a new fraction which is the difference f1 minus f2
   */
  public static PolynomialFraction subtract(PolynomialFraction f1,
                                            PolynomialFraction f2) {
    Polynomial.Rational num =
      Polynomial.Rational.subtract(Polynomial.Rational.multiply(f1.p, f2.q),
                                   Polynomial.Rational.multiply(f2.p, f1.q));
    Polynomial.Rational den = Polynomial.Rational.multiply(f1.q, f2.q);
    return new PolynomialFraction(num, den);
  }

  /** Multiply the instance by a fraction.
   * @param f fraction to multiply by
   */
  public void multiplySelf(PolynomialFraction f) {
    p.multiplySelf(f.p);
    q.multiplySelf(f.q);
    simplify();
  }

  /** Multiply two fractions.
   * @param f1 first fraction
   * @param f2 second fraction
   * @return a new fraction which is the product of f1 and f2
   */
  public static PolynomialFraction multiply(PolynomialFraction f1,
                                            PolynomialFraction f2) {
    PolynomialFraction copy = new PolynomialFraction(f1);
    copy.multiplySelf(f2);
    return copy;
  }

  /** Divide the instance by a fraction.
   * @param f fraction to divide by
   * @exception ArithmeticException if f is null
   */
  public void divideSelf(PolynomialFraction f) {

    if (f.p.isZero()) {
      throw new ArithmeticException("divide by zero");
    }

    p.multiplySelf(f.q);
    q.multiplySelf(f.p);

    RationalNumber[] a = q.getCoefficients();
    if (a[a.length - 1].isNegative()) {
      p.negateSelf();
      q.negateSelf();
    }

    simplify();

  }

  /** Divide two fractions.
   * @param f1 first fraction
   * @param f2 second fraction
   * @return a new fraction which is the quotient of f1 by f2
   */
  public static PolynomialFraction divide(PolynomialFraction f1,
                                          PolynomialFraction f2) {
    PolynomialFraction copy = new PolynomialFraction(f1);
    copy.divideSelf(f2);
    return copy;
  }

  /** Invert the instance.
   * Replace the instance by its inverse.
   * @exception ArithmeticException if the instance is null
   */
  public void invertSelf() {

    if (p.isZero()) {
      throw new ArithmeticException("divide by zero");
    }

    Polynomial.Rational tmp = p;
    p = q;
    q = tmp;

    RationalNumber[] a = q.getCoefficients();
    if (a[a.length - 1].isNegative()) {
      p.negateSelf();
      q.negateSelf();
    }

    simplify();

  }

  /** Invert a fraction.
   * @param f fraction to invert
   * @return a new fraction which is the inverse of f
   */
  public static PolynomialFraction invert(PolynomialFraction f) {
    PolynomialFraction copy = new PolynomialFraction(f);
    copy.invertSelf();
    return copy;
  }

  /** Simplify a fraction.
   * If the denominator polynom is a constant polynom, then
   * simplification involves merging this constant in the rational
   * coefficients of the numerator in order to replace the denominator
   * by the constant 1. If the degree of the denominator is non null,
   * then simplification involves both removing common polynomial
   * factors (by euclidian division) and replacing rational
   * coefficients by integer coefficients (multiplying both numerator
   * and denominator by the proper value). The signs of both the
   * numerator and the denominator are adjusted in order to have a
   * positive leeding degree term in the denominator.
   */
  private void simplify() {

    Polynomial.Rational a = new Polynomial.Rational(p);
    Polynomial.Rational b = new Polynomial.Rational(q);
    if (a.getDegree() < b.getDegree()) {
      Polynomial.Rational tmp = a;
      a = b;
      b = tmp;
    }

    Polynomial.DivisionResult res =
      Polynomial.Rational.euclidianDivision(a, b);
    while (res.remainder.getDegree() != 0) {
      a = b;
      b = res.remainder;
      res = Polynomial.Rational.euclidianDivision(a, b);
    }

    if (res.remainder.isZero()) {
      // there is a common factor we can remove
      p = Polynomial.Rational.euclidianDivision(p, b).quotient;
      q = Polynomial.Rational.euclidianDivision(q, b).quotient;
    }

    if (q.getDegree() == 0) {
      if (! q.isOne()) {
        RationalNumber f = q.getCoefficients()[0];
        f.invertSelf();
        p.multiplySelf(f);
        q = new Polynomial.Rational(1l);
      }
    } else {

      BigInteger lcm = p.getDenominatorsLCM();
      if (lcm.compareTo(BigInteger.ONE) != 0) {
        p.multiplySelf(lcm);
        q.multiplySelf(lcm);
      }

      lcm = q.getDenominatorsLCM();
      if (lcm.compareTo(BigInteger.ONE) != 0) {
        p.multiplySelf(lcm);
        q.multiplySelf(lcm);
      }

    }

    if (q.getCoefficients()[q.getDegree()].isNegative()) {
      p.negateSelf();
      q.negateSelf();
    }

  }

  /**
   * Get the numerator.
   * @return the numerator
   */
  public Polynomial.Rational getNumerator() {
    return p;
  }

  /**
   * Get the denominator.
   * @return the denominator (leeding coefficient is always positive)
   */
  public Polynomial.Rational getDenominator() {
    return q;
  }

  /** Set the name of the unknown (to appear during conversions to
   * strings).
   * @param name name to set (if null, the default 'x' value will be
   * used)
   */
  public void setUnknownName(String name) {
    p.setUnknownName(name);
    q.setUnknownName(name);
  }

  public String toString() {
    if (p.isZero()) {
      return "0";
    } else if (q.isOne()) {
      return p.toString();
    } else {

      StringBuffer s = new StringBuffer();

      String pString = p.toString();
      if (pString.indexOf(' ') > 0) {
        s.append('(');
        s.append(pString);
        s.append(')');
      } else {
        s.append(pString);
      }

      s.append('/');

      String qString = q.toString();
      if (qString.indexOf(' ') > 0) {
        s.append('(');
        s.append(qString);
        s.append(')');
      } else {
        s.append(qString);
      }

      return s.toString();

    }
  }

  /** Numerator. */
  private Polynomial.Rational p;

  /** Denominator. */
  private Polynomial.Rational q;

}
