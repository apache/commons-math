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

import java.io.Serializable;
import java.math.BigInteger;

/**
 * This class implements fractions of polynomials with one unknown and
 * rational coefficients.
 * <p>Instances of this class are immutable.</p>

 * @version $Id$
 * @author L. Maisonobe

 */

public class PolynomialFraction implements Serializable {

  /**
   * Simple constructor.
   * Build a constant null fraction
   */
  public PolynomialFraction() {
    this(new Polynomial.Rational(RationalNumber.ZERO),
         new Polynomial.Rational(RationalNumber.ONE));
  }

  /**
   * Simple constructor.
   * Build a fraction from a numerator and a denominator.
   * @param numerator numerator of the fraction
   * @param denominator denominator of the fraction
   * @exception ArithmeticException if the denominator is null
   */
  public PolynomialFraction(long numerator, long denominator) {
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

    p = numerator;
    q = denominator;

    RationalNumber[] a = q.getCoefficients();
    if (a[a.length - 1].isNegative()) {
      p = (Polynomial.Rational) p.negate();
      q = (Polynomial.Rational) q.negate();
    }

    simplify();

  }

  /** Simple constructor.
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

  /** Simple constructor.
   * Build a fraction from a single rational number
   * @param r value of the fraction
   */
  public PolynomialFraction(RationalNumber r) {
    this(r.getNumerator(), r.getDenominator());
  }

  /** Simple constructor.
   * Build a fraction from a single Polynomial
   * @param p value of the fraction
   */
  public PolynomialFraction(Polynomial.Rational p) {
    this(p, new Polynomial.Rational(1l));
  }

  /** Negate the instance.
   * @return a new polynomial fraction opposite to the instance
   */
  public PolynomialFraction negate() {
    return new PolynomialFraction((Polynomial.Rational) p.negate(), q);
  }

  /** Add a polynomial fraction to the instance.
   * @param f polynomial fraction to add.
   * @return a new polynomial fraction
   */
  public PolynomialFraction add(PolynomialFraction f) {
    return new PolynomialFraction(p.multiply(f.q).add(f.p.multiply(q)),
                                  q.multiply(f.q));
  }

  /** Subtract a fraction from the instance.
   * @param f polynomial fraction to subtract.
   * @return a new polynomial fraction
   */
  public PolynomialFraction subtract(PolynomialFraction f) {
    return new PolynomialFraction(p.multiply(f.q).subtract(f.p.multiply(q)),
                                  q.multiply(f.q));
  }

  /** Multiply the instance by a polynomial fraction.
   * @param f polynomial fraction to multiply by
   * @return a new polynomial fraction
   */
  public PolynomialFraction multiply(PolynomialFraction f) {
    PolynomialFraction product =
      new PolynomialFraction(p.multiply(f.p), q.multiply(f.q));
    product.simplify();
    return product;
  }

  /** Divide the instance by a polynomial fraction.
   * @param f polynomial fraction to divide by
   * @return a new polynomial fraction
   * @exception ArithmeticException if f is null
   */
  public PolynomialFraction divide(PolynomialFraction f) {

    if (f.p.isZero()) {
      throw new ArithmeticException("divide by zero");
    }

    Polynomial.Rational newP = p.multiply(f.q);
    Polynomial.Rational newQ = q.multiply(f.p);

    RationalNumber[] a = newQ.getCoefficients();
    if (a[a.length - 1].isNegative()) {
      newP = (Polynomial.Rational) newP.negate();
      newQ = (Polynomial.Rational) newQ.negate();
    }

    PolynomialFraction result = new PolynomialFraction(newP, newQ);
    result.simplify();
    return result;

  }

  /** Invert the instance.
   * @return the inverse of the instance
   * @exception ArithmeticException if the instance is zero
   */
  public PolynomialFraction invert() {

    if (p.isZero()) {
      throw new ArithmeticException("divide by zero");
    }

    RationalNumber[] a = p.getCoefficients();
    PolynomialFraction inverse =
      (a[a.length - 1].isNegative())
      ? new PolynomialFraction((Polynomial.Rational) q.negate(),
                               (Polynomial.Rational) p.negate())
      : new PolynomialFraction(q, p);
    inverse.simplify();
    return inverse;

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

    Polynomial.Rational a = p;
    Polynomial.Rational b = q;
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
        p = (Polynomial.Rational) p.divide(q.getCoefficients()[0]);
        q = new Polynomial.Rational(1l);
      }
    } else {

      BigInteger lcm = p.getDenominatorsLCM();
      if (lcm.compareTo(BigInteger.ONE) != 0) {
        p = (Polynomial.Rational) p.multiply(lcm);
        q = (Polynomial.Rational) q.multiply(lcm);
      }

      lcm = q.getDenominatorsLCM();
      if (lcm.compareTo(BigInteger.ONE) != 0) {
        p = (Polynomial.Rational) p.multiply(lcm);
        q = (Polynomial.Rational) q.multiply(lcm);
      }

    }

    if (q.getCoefficients()[q.getDegree()].isNegative()) {
      p = (Polynomial.Rational) p.negate();
      q = (Polynomial.Rational) q.negate();
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

  private static final long serialVersionUID = 6033909492898954748L;

}
