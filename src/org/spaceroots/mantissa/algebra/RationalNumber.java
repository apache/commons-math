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
 * This class implements reduced rational numbers.
 * <p>Instances of this class are immutable.</p>

 * @version $Id$
 * @author L. Maisonobe

 */

public class RationalNumber implements Serializable {

  /** Zero as a rational numer. */
  public static final RationalNumber ZERO = new RationalNumber(0l);

  /** One as a rational numer. */
  public static final RationalNumber ONE  = new RationalNumber(1l);

  /**
   * Simple constructor.
   * Build a null rational number
   */
  public RationalNumber() {
    p = BigInteger.ZERO;
    q = BigInteger.ONE;
  }

  /** Simple constructor.
   * Build a rational number from a numerator and a denominator.
   * @param numerator numerator of the rational number
   * @param denominator denominator of the rational number
   * @exception ArithmeticException if the denominator is zero
   */
  public RationalNumber(long numerator, long denominator) {

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

  /** Simple constructor.
   * Build a rational number from a numerator and a denominator.
   * @param numerator numerator of the rational number
   * @param denominator denominator of the rational number
   * @exception ArithmeticException if the denominator is zero
   */
  public RationalNumber(BigInteger numerator, BigInteger denominator) {

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

  /** Simple constructor.
   * Build a rational number from a single integer
   * @param l value of the rational number
   */
  public RationalNumber(long l) {
    p = BigInteger.valueOf(l);
    q = BigInteger.ONE;
  }

  /** Simple constructor.
   * Build a rational number from a single integer
   * @param i value of the rational number
   */
  public RationalNumber(BigInteger i) {
    p = i;
    q = BigInteger.ONE;
  }

  /** Negate the instance.
   * @return a new rational number, opposite to the isntance
   */
  public RationalNumber negate() {
    return new RationalNumber(p.negate(), q);
  }

  /** Add an integer to the instance.
   * @param l integer to add
   * @return a new rational number which is the sum of the instance and l
   */
  public RationalNumber add(long l) {
    return add(BigInteger.valueOf(l));
  }

  /** Add an integer to the instance.
   * @param l integer to add
   * @return a new rational number which is the sum of the instance and l
   */
  public RationalNumber add(BigInteger l) {
    return new RationalNumber(p.add(q.multiply(l)), q);
  }

  /** Add a rational number to the instance.
   * @param r rational number to add
   * @return a new rational number which is the sum of the instance and r
   */
  public RationalNumber add(RationalNumber r) {
    return new RationalNumber(p.multiply(r.q).add(r.p.multiply(q)),
                              q.multiply(r.q));
  }

  /** Subtract an integer from the instance.
   * @param l integer to subtract
   * @return a new rational number which is the difference the instance minus l
   */
  public RationalNumber subtract(long l) {
    return subtract(BigInteger.valueOf(l));
  }

  /** Subtract an integer from the instance.
   * @param l integer to subtract
   * @return a new rational number which is the difference the instance minus l
   */
  public RationalNumber subtract(BigInteger l) {
    return new RationalNumber(p.subtract(q.multiply(l)), q);
  }

  /** Subtract a rational number from the instance.
   * @param r rational number to subtract
   * @return a new rational number which is the difference the instance minus r
   */
  public RationalNumber subtract(RationalNumber r) {
    return new RationalNumber(p.multiply(r.q).subtract(r.p.multiply(q)),
                              q.multiply(r.q));
  }

  /** Multiply the instance by an integer.
   * @param l integer to multiply by
   * @return a new rational number which is the produc of the instance by l
   */
  public RationalNumber multiply(long l) {
    return multiply(BigInteger.valueOf(l));
  }

  /** Multiply the instance by an integer.
   * @param l integer to multiply by
   * @return a new rational number which is the produc of the instance by l
   */
  public RationalNumber multiply(BigInteger l) {
    return new RationalNumber(p.multiply(l), q);
  }

  /** Multiply the instance by a rational number.
   * @param r rational number to multiply the instance with
   * @return a new rational number which is the product of the instance and r
   */
  public RationalNumber multiply(RationalNumber r) {
    return new RationalNumber(p.multiply(r.p), q.multiply(r.q));
  }

  /** Divide the instance by an integer.
   * @param l integer to divide by
   * @return a new rational number which is the quotient of the instance by l
   * @exception ArithmeticException if l is zero
   */
  public RationalNumber divide(long l) {
    return divide(BigInteger.valueOf(l));
  }

  /** Divide the instance by an integer.
   * @param l integer to divide by
   * @return a new rational number which is the quotient of the instance by l
   * @exception ArithmeticException if l is zero
   */
  public RationalNumber divide(BigInteger l) {

    if (l.signum() == 0) {
      throw new ArithmeticException("divide by zero");
    }

    if (l.signum() > 0) {
      return new RationalNumber(p, q.multiply(l));
    }

    return new RationalNumber(p.negate(), q.multiply(l.negate()));

  }

  /** Divide the instance by a rational number.
   * @param r rational number to divide by
   * @return a new rational number which is the quotient of the instance by r
   * @exception ArithmeticException if r is zero
   */
  public RationalNumber divide(RationalNumber r) {

    if (r.p.signum() == 0) {
      throw new ArithmeticException("divide by zero");
    }

    BigInteger newP = p.multiply(r.q);
    BigInteger newQ = q.multiply(r.p);

    return (newQ.signum() < 0) ? new RationalNumber(newP.negate(),
                                                    newQ.negate())
                               : new RationalNumber(newP, newQ);

  }

  /** Invert the instance.
   * @return the inverse of the instance
   * @exception ArithmeticException if the instance is zero
   */
  public RationalNumber invert() {

    if (p.signum() == 0) {
      throw new ArithmeticException("divide by zero");
    }

    return (q.signum() < 0) ? new RationalNumber(q.negate(), p.negate())
                            : new RationalNumber(q, p);

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

  /** Get the numerator.
   * @return the signed numerator
   */
  public BigInteger getNumerator() {
    return p;
  }

  /** Get the denominator.
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

  private static final long serialVersionUID = -324954393137577531L;

}
