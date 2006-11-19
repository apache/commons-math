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
 * This class implements reduced rational numbers.

 * @version $Id: RationalNumber.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class RationalNumber {

  /**
   * Simple constructor.
   * Build a null rational number
   */
  public RationalNumber() {
    p = BigInteger.ZERO;
    q = BigInteger.ONE;
  }

  /**
   * Simple constructor.
   * Build a rational number from a numerator and a denominator.
   * @param numerator numerator of the rational number
   * @param denominator denominator of the rational number
   * @exception ArithmeticException if the denominator is zero
   */
  public RationalNumber(long numerator, long denominator) {
    reset(numerator, denominator);
  }

  /**
   * Simple constructor.
   * Build a rational number from a numerator and a denominator.
   * @param numerator numerator of the rational number
   * @param denominator denominator of the rational number
   * @exception ArithmeticException if the denominator is zero
   */
  public RationalNumber(BigInteger numerator, BigInteger denominator) {
    reset(numerator, denominator);
  }

  /**
   * Simple constructor.
   * Build a rational number from a single integer
   * @param l value of the rational number
   */
  public RationalNumber(long l) {
    p = BigInteger.valueOf(l);
    q = BigInteger.ONE;
  }

  /**
   * Simple constructor.
   * Build a rational number from a single integer
   * @param i value of the rational number
   */
  public RationalNumber(BigInteger i) {
    p = i;
    q = BigInteger.ONE;
  }

  /**
   * Copy-constructor.
   * @param r rational number to copy
   */
  public RationalNumber(RationalNumber r) {
    p = r.p;
    q = r.q;
  }

  /** Reset the instance from a numerator and a denominator.
   * @param numerator numerator of the rational number
   * @param denominator denominator of the rational number
   * @exception ArithmeticException if the denominator is zero
   */
  public void reset(long numerator, long denominator) {
    if (denominator == 0l) {
      throw new ArithmeticException("divide by zero");
    }

    p = BigInteger.valueOf(numerator);
    q = BigInteger.valueOf(denominator);

    if (q.signum() < 0) {
      p = p.negate();
      q = q.negate();
    }

    simplify();

  }

  /** Reset the instance from a numerator and a denominator.
   * @param numerator numerator of the rational number
   * @param denominator denominator of the rational number
   * @exception ArithmeticException if the denominator is zero
   */
  public void reset(BigInteger numerator, BigInteger denominator) {
    if (denominator.signum() == 0) {
      throw new ArithmeticException("divide by zero");
    }

    p = numerator;
    q = denominator;

    if (q.signum() < 0) {
      p = p.negate();
      q = q.negate();
    }

    simplify();

  }

  /** Reset the instance from a single integer
   * @param l value of the rational number
   */
  public void reset(long l) {
    p = BigInteger.valueOf(l);
    q = BigInteger.ONE;
  }

  /** Reset the instance from a single integer
   * @param i value of the rational number
   */
  public void reset(BigInteger i) {
    p = i;
    q = BigInteger.ONE;
  }

  /** Reset the instance from another rational number.
   * @param r rational number to copy
   */
  public void reset(RationalNumber r) {
    p = r.p;
    q = r.q;
  }

  /**
   * Negate the instance
   */
  public void negateSelf() {
    p = p.negate();
  }

  /**
   * Negate a rational number.
   * @param r rational number to negate
   * @return a new rational number which is the opposite of r
   */
  public static RationalNumber negate(RationalNumber r) {
    RationalNumber copy = new RationalNumber(r);
    copy.negateSelf();
    return copy;
  }

  /**
   * Add a rational number to the instance.
   * @param r rational number to add.
   */
  public void addToSelf(RationalNumber r) {
    p = p.multiply(r.q).add(r.p.multiply(q));
    q = q.multiply(r.q);
    simplify();
  }

  /** Add two rational numbers.
   * @param r1 first rational number
   * @param r2 second rational number
   * @return a new rational number which is the sum of r1 and r2
   */
  public static RationalNumber add(RationalNumber r1, RationalNumber r2) {
    return new RationalNumber(r1.p.multiply(r2.q).add(r2.p.multiply(r1.q)),
                              r1.q.multiply(r2.q));
  }

  /**
   * Subtract a rational number to the instance.
   * @param r rational number to subtract.
   */
  public void subtractFromSelf(RationalNumber r) {
    p = p.multiply(r.q).subtract(r.p.multiply(q));
    q = q.multiply(r.q);
    simplify();
  }

  /** Subtract two rational numbers.
   * @param r1 first rational number
   * @param r2 second rational number
   * @return a new rational number which is the difference r1 minus r2
   */
  public static RationalNumber subtract(RationalNumber r1, RationalNumber r2) {
    return new RationalNumber(r1.p.multiply(r2.q).subtract(r2.p.multiply(r1.q)),
                              r1.q.multiply(r2.q));
  }

  /** Multiply the instance by an integer.
   * @param l integer to multiply by
   */
  public void multiplySelf(long l) {
    p = p.multiply(BigInteger.valueOf(l));
    simplify();
  }

  /** Multiply the instance by an integer.
   * @param i integer to multiply by
   */
  public void multiplySelf(BigInteger i) {
    p = p.multiply(i);
    simplify();
  }

  /** Multiply a rational number by an integer.
   * @param l integer to multiply by
   */
  public static RationalNumber multiply(RationalNumber r, long l) {
    return new RationalNumber(r.p.multiply(BigInteger.valueOf(l)), r.q);
  }

  /** Multiply a rational number by an integer.
   * @param i integer to multiply by
   */
  public static RationalNumber multiply(RationalNumber r, BigInteger i) {
    return new RationalNumber(r.p.multiply(i), r.q);
  }

  /** Multiply the instance by a rational number.
   * @param r rational number to multiply by
   */
  public void multiplySelf(RationalNumber r) {
    p = p.multiply(r.p);
    q = q.multiply(r.q);
    simplify();
  }

  /** Multiply two rational numbers.
   * @param r1 first rational number
   * @param r2 second rational number
   * @return a new rational number which is the product of r1 and r2
   */
  public static RationalNumber multiply(RationalNumber r1, RationalNumber r2) {
    return new RationalNumber(r1.p.multiply(r2.p),
                              r1.q.multiply(r2.q));
  }

  /** Divide the instance by an integer.
   * @param l integer to divide by
   * @exception ArithmeticException if l is zero
   */
  public void divideSelf(long l) {

    if (l == 0l) {
      throw new ArithmeticException("divide by zero");
    } else if (l > 0l) {
      q = q.multiply(BigInteger.valueOf(l));
    } else {
      p = p.negate();
      q = q.multiply(BigInteger.valueOf(-l));
    }

    simplify();

  }

  /** Divide the instance by an integer.
   * @param i integer to divide by
   * @exception ArithmeticException if l is zero
   */
  public void divideSelf(BigInteger i) {

    if (i.signum() == 0) {
      throw new ArithmeticException("divide by zero");
    } else if (i.signum() > 0) {
      q = q.multiply(i);
    } else {
      p = p.negate();
      q = q.multiply(i.negate());
    }

    simplify();

  }

  /** Divide a rational number by an integer
   * @param r rational number
   * @param l integer
   * @return a new rational number which is the quotient of r by l
   * @exception ArithmeticException if l is zero
   */
  public static RationalNumber divide(RationalNumber r, long l) {
    RationalNumber copy = new RationalNumber(r);
    copy.divideSelf(l);
    return copy;
  }

  /** Divide a rational number by an integer
   * @param r rational number
   * @param i integer
   * @return a new rational number which is the quotient of r by l
   * @exception ArithmeticException if l is zero
   */
  public static RationalNumber divide(RationalNumber r, BigInteger i) {
    RationalNumber copy = new RationalNumber(r);
    copy.divideSelf(i);
    return copy;
  }

  /** Divide the instance by a rational number.
   * @param r rational number to divide by
   * @exception ArithmeticException if r is zero
   */
  public void divideSelf(RationalNumber r) {

    if (r.p.signum() == 0) {
      throw new ArithmeticException("divide by zero");
    }

    p = p.multiply(r.q);
    q = q.multiply(r.p);

    if (q.signum() < 0) {
      p = p.negate();
      q = q.negate();
    }

    simplify();

  }

  /** Divide two rational numbers.
   * @param r1 first rational number
   * @param r2 second rational number
   * @return a new rational number which is the quotient of r1 by r2
   * @exception ArithmeticException if r2 is zero
   */
  public static RationalNumber divide(RationalNumber r1, RationalNumber r2) {
    RationalNumber copy = new RationalNumber(r1);
    copy.divideSelf(r2);
    return copy;
  }

  /** Invert the instance.
   * Replace the instance by its inverse.
   * @exception ArithmeticException if the instance is zero
   */
  public void invertSelf() {

    if (p.signum() == 0) {
      throw new ArithmeticException("divide by zero");
    }

    BigInteger tmp = p;
    p = q;
    q = tmp;

    if (q.signum() < 0) {
      p = p.negate();
      q = q.negate();
    }

  }

  /** Invert a rational number.
   * @param r rational number to invert
   * @return a new rational number which is the inverse of r
   * @exception ArithmeticException if r is zero
   */
  public static RationalNumber invert(RationalNumber r) {
    return new RationalNumber(r.q, r.p);
  }

  /**
   * Add the product of two rational numbers to the instance.
   * This operation is equivalent to
   * <code>addToSelf(RationalNumber.multiply(r1, r2))</code> except
   * that no intermediate simplification is attempted.
   * @param r1 first term of the product to add
   * @param r2 second term of the product to add
   */
  public void multiplyAndAddToSelf(RationalNumber r1, RationalNumber r2) {
    BigInteger r1qr2q = r1.q.multiply(r2.q);
    p = p.multiply(r1qr2q).add(r1.p.multiply(r2.p).multiply(q));
    q = q.multiply(r1qr2q);
    simplify();
  }

  /**
   * Subtract the product of two rational numbers from the instance.
   * This operation is equivalent to
   * <code>subtractFromSelf(RationalNumber.multiply(r1, r2))</code>
   * except that no intermediate simplification is attempted.
   * @param r1 first term of the product to subtract
   * @param r2 second term of the product to subtract
   */
  public void multiplyAndSubtractFromSelf(RationalNumber r1, RationalNumber r2) {
    BigInteger r1qr2q = r1.q.multiply(r2.q);
    p = p.multiply(r1qr2q).subtract(r1.p.multiply(r2.p).multiply(q));
    q = q.multiply(r1qr2q);
    simplify();
  }

  /** Simplify a rational number by removing common factors.
   */
  private void simplify() {
    if (p.signum() == 0) {
      q = BigInteger.ONE;
    } else {
      BigInteger gcd = p.gcd(q);
      p = p.divide(gcd);
      q = q.divide(gcd);
    }
  }

  /**
   * Get the numerator.
   * @return the signed numerator
   */
  public BigInteger getNumerator() {
    return p;
  }

  /**
   * Get the denominator.
   * @return the denominator (always positive)
   */
  public BigInteger getDenominator() {
    return q;
  }

  /** Check if the number is zero.
   * @return true if the number is zero
   */
  public boolean isZero() {
    return p.signum() == 0;
  }

  /** Check if the number is one.
   * @return true if the number is one
   */
  public boolean isOne() {
    return (p.compareTo(BigInteger.ONE) == 0)
        && (q.compareTo(BigInteger.ONE) == 0);
  }

  /** Check if the number is integer.
   * @return true if the number is an integer
   */
  public boolean isInteger() {
    return q.compareTo(BigInteger.ONE) == 0;
  }

  /** Check if the number is negative.
   * @return true if the number is negative
   */
  public boolean isNegative() {
    return p.signum() < 0;
  }

  /** Get the absolute value of a rational number.
   * @param r rational number from which we want the absolute value
   * @return a new rational number which is the absolute value of r
   */
  public static RationalNumber abs(RationalNumber r) {
    return new RationalNumber(r.p.abs(), r.q);
  }

  /** Return the <code>double</code> value of the instance.
   * @return the double value of the instance
   */
  public double doubleValue() {
    BigInteger[] result = p.divideAndRemainder(q);
    return result[0].doubleValue()
        + (result[1].doubleValue() / q.doubleValue());
  }

  /** Check if the instance is equal to another rational number.
   * Equality here is having the same value.
   * @return true if the object is a rational number which has the
   * same value as the instance
   */
  public boolean equals(Object o) {
    if (o instanceof RationalNumber) {
      RationalNumber r = (RationalNumber) o;
      return (p.compareTo(r.p) == 0) && (q.compareTo(r.q) == 0);
    }
    return false;
  }

  /** Returns a hash code value for the object.
   * The hash code value is computed from the reduced numerator and
   * denominator, hence equal rational numbers have the same hash code,
   * as required by the method specification.
   * @return a hash code value for this object.
   */
  public int hashCode() {
    return p.hashCode() ^ q.hashCode();
  }

  /** Returns a string representation of the rational number.
   * The representation is reduced: there is no common factor left
   * between the numerator and the denominator. The '/' character and
   * the denominator are displayed only if the denominator is not
   * one. The sign is on the numerator.
   * @return string representation of the rational number
   */
  public String toString() {
    return p + ((q.compareTo(BigInteger.ONE) == 0) ? "" : ("/" + q));
  }

  /** Numerator. */
  private BigInteger p;

  /** Denominator. */
  private BigInteger q;

}
