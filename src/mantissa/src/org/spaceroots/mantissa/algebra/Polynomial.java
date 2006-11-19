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
 * This class implements polynomials with one unknown.

 * <p>This is an abstract class that only declares general methods but
 * does not hold the coefficients by themselves. Specific subclasses
 * are used to handle exact rational coefficients or approximate real
 * coefficients. This design is taken from the various java.awt.geom
 * classes (Point2D, Rectangle2D ...)</p>

 * <p>The methods implemented deal mainly with the polynomials algebra
 * (addition, multiplication ...) but the analysis aspects are also
 * considered (value of the polynom for a given unknown,
 * derivative).</p>

 * @version $Id: Polynomial.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

*/
public abstract class Polynomial
  implements Cloneable, Serializable {

  /** Create a copy of the instance.
   * @return a copy of the instance
   */
  public abstract Object clone();

  /** Check if the instance is the null polynomial.
   * @return true if the polynomial is null
   */
  public abstract boolean isZero();

  /** Check if the instance is the constant unit polynomial.
   * @return true if the polynomial is the constant unit polynomial
   */
  public abstract boolean isOne();

  /** Check if the instance is the identity polynomial.
   * @return true if the polynomial is the identity polynomial
   */
  public abstract boolean isIdentity();

  /** Get the polynomial degree.
   * @return degree
   */
  public abstract int getDegree();

  /** Negate the instance.
   */
  public abstract void negateSelf();

  /** Multiply the instance by a constant.
   * @param r constant to multiply by
   */
  public abstract void multiplySelf(RationalNumber r);

  /** Multiply the instance by a constant.
   * @param l constant to multiply by
   */
  public abstract void multiplySelf(long l);

  /** Multiply the instance by a constant.
   * @param i constant to multiply by
   */
  public void multiplySelf(BigInteger i) {
    multiplySelf(new RationalNumber(i));
  }

  /** Get the value of the polynomial for a specified unknown.
   * @param x value of the unknown
   * @return value of the polynomial
   */
  public abstract double valueAt(double x);

  /** Get the derivative of the instance with respect to the unknown.
   * The derivative of a n degree polynomial is a n-1 degree polynomial of
   * the same type.
   * @return a new polynomial which is the derivative of the instance
   */
  public abstract Polynomial getDerivative();

  /** Set the name of the unknown (to appear during conversions to strings).
   * @param name name to set (if null, the default 'x' value  will be used)
   */
  public abstract void setUnknownName(String name);

  /** This class implements polynomials with one unknown and rational
   * coefficients.

   * <p>In addition to classical algebra operations, euclidian
   * division and remainder are handled.</p>

   */
  public static class Rational extends Polynomial {

    /** Simple constructor.
     * Build a null polynomial
     */
    public Rational() {
      a = new RationalNumber[1];
      a[0] = new RationalNumber(0l);
      unknown = null;
    }

    /** Simple constructor.
     * Build a constant polynomial
     * @param value constant value of the polynomial
     */
    public Rational(long value) {
      this(new RationalNumber(value));
    }

    /** Simple constructor.
     * Build a constant polynomial
     * @param value constant value of the polynomial
     */
    public Rational(RationalNumber value) {
      a = new RationalNumber[1];
      a[0] = value;
      unknown = null;
    }

    /** Simple constructor.
     * Build a first degree polynomial
     * @param a1 leeding degree coefficient
     * @param a0 constant term
     */
    public Rational(long a1, long a0) {
      this(new RationalNumber(a1), new RationalNumber(a0));
    }

    /** Simple constructor.
     * Build a first degree polynomial
     * @param a1 leeding degree coefficient
     * @param a0 constant term
     */
    public Rational(RationalNumber a1, RationalNumber a0) {
      if (! a1.isZero()) {
        a = new RationalNumber[2];
        a[1] = a1;
      } else {
        a = new RationalNumber[1];
      }
      a[0] = a0;
      unknown = null;
    }

    /** Simple constructor.
     * Build a second degree polynomial
     * @param a2 leeding degree coefficient
     * @param a1 first degree coefficient
     * @param a0 constant term
     */
    public Rational(long a2, long a1, long a0) {
      this(new RationalNumber(a2),
           new RationalNumber(a1),
           new RationalNumber(a0));
    }

    /** Simple constructor.
     * Build a second degree polynomial
     * @param a2 leeding degree coefficient
     * @param a1 first degree coefficient
     * @param a0 constant term
     */
    public Rational(RationalNumber a2, RationalNumber a1, RationalNumber a0) {
      if (! a2.isZero()) {
        a = new RationalNumber[3];
        a[2] = a2;
        a[1] = a1;
      } else {
        if (! a1.isZero()) {
          a = new RationalNumber[2];
          a[1] = a1;
        } else {
          a = new RationalNumber[1];
        }
      }
      a[0] = a0;
      unknown = null;
    }

    /** Simple constructor.
     * Build a polynomial from its coefficients
     * @param a coefficients array, the a[0] array element is the
     * constant term while the a[a.length-1] element is the leeding
     * degree coefficient. The array is copied in a new array, so it
     * can be changed once the constructor as returned.
     */
    public Rational(RationalNumber[] a) {

      // remove null high degree coefficients
      int i = a.length - 1;
      while ((i > 0) && (a[i].isZero())) {
        --i;
      }

      // copy the remaining coefficients
      this.a = new RationalNumber[i + 1];
      System.arraycopy(a, 0, this.a, 0, i + 1);

      unknown = null;

    }

    /** Simple constructor.
     * Build a one term polynomial from one coefficient and the corresponding degree
     * @param c coefficient
     * @param degree degree associated with the coefficient
     */
    public Rational(RationalNumber c, int degree) {

      if (c.isZero() || degree < 0) {
        a = new RationalNumber[1];
        a[0] = new RationalNumber(0l);
      } else {
        a = new RationalNumber[degree + 1];
        for (int i = 0; i < degree; ++i) {
          a[i] = new RationalNumber(0l);
        }
        a[degree] = new RationalNumber(c);
      }

      unknown = null;

    }

    /** Copy constructor.
     * The copy is a deep copy: the polynomials do <em>not</em> share
     * their coefficients arrays
     * @param p polynomial to copy
     */
    public Rational(Rational p) {

      a = new RationalNumber[p.a.length];
      for (int i = 0; i < a.length; ++i) {
        a[i] = new RationalNumber(p.a[i]);
      }

      if (p.unknown == null) {
        unknown = null;
      } else {
        unknown = new String(p.unknown);
      }

    }

    /** Create a copy of the instance.
     * @return a copy of the instance
     */
    public Object clone() {
      return new Rational(this);
    }

    /** Check if the instance is the null polynomial.
     * @return true if the polynomial is null
     */
    public boolean isZero() {
      return (a.length == 1) && a[0].isZero();
    }

    /** Check if the instance is the constant unit polynomial.
     * @return true if the polynomial is the constant unit polynomial
     */
    public boolean isOne() {
      return (a.length == 1) && a[0].isOne();
    }

    /** Check if the instance is the identity polynomial.
     * @return true if the polynomial is the identity polynomial
     */
    public boolean isIdentity() {
      return (a.length == 2) && a[0].isZero() && a[1].isOne();
    }

    /** Get the polynomial degree.
     * @return degree
     */
    public int getDegree() {
      return a.length - 1;
    }

    /** Get the coefficients of the polynomial.
     * @return a reference to the internal coefficients array, the array
     * element at index 0 is the constant term while the element at
     * index a.length-1 is the leeding degree coefficient
     */
    public RationalNumber[] getCoefficients() {
      return a;
    }

    /** Set the name of the unknown (to appear during conversions to strings).
     * @param name name to set (if null, the default 'x' value  will be used)
     */
    public void setUnknownName(String name) {
      unknown = name;
    }

    /** Simplify the polynomial, by removing null high degree terms.
     */
    private void simplify() {

      int i = a.length - 1;
      while ((i > 0) && a[i].isZero()) {
        --i;
      }

      if (i < a.length - 1) {
        RationalNumber[] newA = new RationalNumber[i + 1];
        System.arraycopy(a, 0, newA, 0, i + 1);
        a = newA;
      }

    }

    /** Add a polynomial to the instance.
     * @param p polynomial to add
     */
    public void addToSelf(Rational p) {

      if (p.a.length > a.length) {
        RationalNumber[] newA = new RationalNumber[p.a.length];
        System.arraycopy(a, 0, newA, 0, a.length);
        for (int i = a.length; i < newA.length; ++i) {
          newA[i] = new RationalNumber(0l);
        }
        a = newA;
      }

      for (int i = 0; i < p.a.length; ++i) {
        a[i].addToSelf(p.a[i]);
      }

      simplify();

    }

    /** Add two polynomials.
     * @param p1 first polynomial
     * @param p2 second polynomial
     * @return a new polynomial which is the sum of p1 and p2
     */
    public static Rational add(Rational p1, Rational p2) {
      Rational copy = new Rational(p1);
      copy.addToSelf(p2);
      return copy;
    }

    /** Subtract a polynomial from the instance.
     * @param p polynomial to subtract
     */
    public void subtractFromSelf(Rational p) {

      if (p.a.length > a.length) {
        RationalNumber[] newA = new RationalNumber[p.a.length];
        System.arraycopy(a, 0, newA, 0, a.length);
        for (int i = a.length; i < newA.length; ++i) {
          newA[i] = new RationalNumber(0l);
        }
        a = newA;
      }

      for (int i = 0; i < p.a.length; ++i) {
        a[i].subtractFromSelf(p.a[i]);
      }

      simplify();

    }

    /** Subtract two polynomials.
     * @param p1 first polynomial
     * @param p2 second polynomial
     * @return a new polynomial which is the difference p1 minus p2
     */
    public static Rational subtract(Rational p1, Rational p2) {
      Rational copy = new Rational(p1);
      copy.subtractFromSelf(p2);
      return copy;
    }

    /** Negate the instance.
     */
    public void negateSelf() {
      for (int i = 0; i < a.length; ++i) {
        a[i].negateSelf();
      }
    }

    /** Negate a polynomial.
     * @param p polynomial to negate
     * @return a new polynomial which is the opposite of p
     */
    public static Rational negate(Rational p) {
      Rational copy = new Rational(p);
      copy.negateSelf();
      return copy;
    }

    /** Multiply the instance by a polynomial.
     * @param p polynomial to multiply by
     */
    public void multiplySelf(Rational p) {

      RationalNumber[] newA = new RationalNumber[a.length + p.a.length - 1];

      for (int i = 0; i < newA.length; ++i) {
        newA[i] = new RationalNumber(0l);
        for (int j = Math.max(0, i + 1 - p.a.length);
             j < Math.min(a.length, i + 1);
             ++j) {
          newA[i].addToSelf(RationalNumber.multiply(a[j], p.a[i-j]));
        }
      }

      a = newA;

    }

    /** Multiply two polynomials.
     * @param p1 first polynomial
     * @param p2 second polynomial
     * @return a new polynomial which is the product of p1 and p2
     */
    public static Rational multiply(Rational p1, Rational p2) {
      Rational copy = new Rational(p1);
      copy.multiplySelf(p2);
      return copy;
    }

    /** Multiply the instance by a constant.
     * @param r constant to multiply by
     */
    public void multiplySelf(RationalNumber r) {

      if (r.isZero()) {
        a = new RationalNumber[1];
        a[0] = new RationalNumber(0l);
      }

      for (int i = 0; i < a.length; ++i) {
        a[i].multiplySelf(r);
      }

    }

    /** Multiply a polynomial by a constant.
     * @param p polynomial
     * @param r constant
     * @return a new polynomial which is the product of p and r
     */
    public static Rational multiply(Rational p, RationalNumber r) {
      Rational copy = new Rational(p);
      copy.multiplySelf(r);
      return copy;
    }

    /** Multiply the instance by a constant.
     * @param l constant to multiply by
     */
    public void multiplySelf(long l) {

      if (l == 0l) {
        a = new RationalNumber[1];
        a[0] = new RationalNumber(0l);
      }

      for (int i = 0; i < a.length; ++i) {
        a[i].multiplySelf(l);
      }

    }

    /** Multiply a polynomial by a constant.
     * @param p polynomial
     * @param l constant
     * @return a new polynomial which is the product of p and l
     */
    public static Rational multiply(Rational p, long l) {
      Rational copy = new Rational(p);
      copy.multiplySelf(l);
      return copy;
    }

    /** Get the value of the polynomial for a specified unknown.
     * @param x value of the unknown
     * @return value of the polynomial
     */
    public double valueAt(double x) {
      double y = 0;
      for (int i = a.length - 1; i >= 0; --i) {
        y = y * x + a[i].doubleValue();
      }
      return y;
    }

    /** Get the derivative of the instance with respect to the unknown.
     * The derivative of a n degree polynomial is a n-1 degree polynomial of
     * the same type.
     * @return a new polynomial which is the derivative of the instance
     */
    public Polynomial getDerivative() {
      Rational derivative = new Rational();
      if (a.length == 1) {
        return derivative;
      }
      derivative.a = new RationalNumber[a.length - 1];
      for (int i = 1; i < a.length; ++i) {
        derivative.a[i-1] = RationalNumber.multiply(a[i], i);
      }
      return derivative;
    }

    /** Perform the euclidian division of two polynomials.
     * @param dividend numerator polynomial
     * @param divisor  denominator polynomial
     * @return an object containing the quotient and the remainder of the division
     */
    public static DivisionResult euclidianDivision(Rational dividend,
                                                   Rational divisor) {

      Rational quotient  = new Rational(0l);
      Rational remainder = new Rational(dividend);

      int divisorDegree   = divisor.getDegree();
      int remainderDegree = remainder.getDegree();
      while ((! remainder.isZero()) && (remainderDegree >= divisorDegree)) {

        RationalNumber c = RationalNumber.divide(remainder.a[remainderDegree],
                                                 divisor.a[divisorDegree]);
        Rational monomial = new Rational(c, remainderDegree - divisorDegree);

        remainder.subtractFromSelf(Rational.multiply(monomial, divisor));
        quotient.addToSelf(monomial);

        remainderDegree = remainder.getDegree();

      }

      return new DivisionResult(quotient, remainder);

    }

    /** Get the Least Common Multiple of the coefficients denominators.
     * This number is the smallest integer by which we should multiply
     * the instance to get a polynomial whose coefficients are all integers.
     * @return the Least Common Multiple of the coefficients denominators
     */
    public BigInteger getDenominatorsLCM() {

      BigInteger lcm = BigInteger.ONE;

      for (int i = 0; i < a.length; ++i) {
        RationalNumber newCoeff = RationalNumber.multiply(a[i], lcm);
        if (! newCoeff.isInteger()) {
          lcm = lcm.multiply(newCoeff.getDenominator());
        }
      }

      return lcm;

    }

    /** Returns a string representation of the polynomial.

    * <p>The representation is user oriented. Terms are displayed lowest
    * degrees first. The multiplications signs, coefficients equals to
    * one and null terms are not displayed (except if the polynomial is 0,
    * in which case the 0 constant term is displayed). Addition of terms
    * with negative coefficients are replaced by subtraction of terms
    * with positive coefficients except for the first displayed term
    * (i.e. we display <code>-3</code> for a constant negative polynomial,
    * but <code>1 - 3 x + x^2</code> if the negative coefficient is not
    * the first one displayed).</p>

    * <p>The name of the unknown is <code>x</code> by default, but can
    * be changed using the {@link #setUnknownName setUnknownName}
    * method.</p>

    * @return a string representation of the polynomial

    */
    public String toString() {

      StringBuffer s = new StringBuffer();
      if (a[0].isZero()) {
        if (a.length == 1) {
          return "0";
        }
      } else {
        s.append(a[0].toString());
      }

      for (int i = 1; i < a.length; ++i) {

        if (! a[i].isZero()) {

          if (s.length() > 0) {
            if (a[i].isNegative()) {
              s.append(" - ");
            } else {
              s.append(" + ");
            }
          } else {
            if (a[i].isNegative()) {
              s.append("-");
            }
          }

          RationalNumber absAi = RationalNumber.abs(a[i]);
          if (! absAi.isOne()) {
            s.append(absAi.toString());
            s.append(' ');
          }

          s.append((unknown == null) ? defaultUnknown : unknown);
          if (i > 1) {
            s.append('^');
            s.append(Integer.toString(i));
          }
        }

      }

      return s.toString();

    }

    /** Coefficients array. */
    protected RationalNumber[] a;

    /** Name of the unknown. */
    protected String unknown;

    private static final long serialVersionUID = 3035650338772911046L;

  }

  /** This class stores the result of the euclidian division of two polynomials.
   * This class is a simple placeholder, it does not provide any
   * processing method
   * @see Polynomial.Rational#euclidianDivision
   */
  public static class DivisionResult {

    /** The quotient of the division. */
    public final Rational quotient;

    /** The remainder of the division. */
    public final Rational remainder;

    /** Simple constructor. */
    public DivisionResult(Rational quotient, Rational remainder) {
      this.quotient  = quotient;
      this.remainder = remainder;
    }

  }

  /** This class implements polynomials with one unknown and real
   * coefficients.
   */
  public static class Double extends Polynomial {

    /** Simple constructor.
     * Build a null polynomial
     */
    public Double() {
      a = new double[1];
      a[0] = 0;
      unknown = null;
    }

    /** Simple constructor.
     * Build a constant polynomial
     * @param value constant value of the polynomial
     */
    public Double(double value) {
      a = new double[1];
      a[0] = value;
      unknown = null;
    }

    /** Simple constructor.
     * Build a first degree polynomial
     * @param a1 leeding degree coefficient
     * @param a0 constant term
     */
    public Double(double a1, double a0) {
      if (Math.abs(a1) > 1.0e-12) {
        a = new double[2];
        a[1] = a1;
      } else {
        a = new double[1];
      }
      a[0] = a0;
      unknown = null;
    }

    /** Simple constructor.
     * Build a second degree polynomial
     * @param a2 leeding degree coefficient
     * @param a1 first degree coefficient
     * @param a0 constant term
     */
    public Double(double a2, double a1, double a0) {
      if (Math.abs(a2) > 1.0e-12) {
        a = new double[3];
        a[2] = a2;
        a[1] = a1;
      } else {
        if (Math.abs(a1) > 1.0e-12) {
          a = new double[2];
          a[1] = a1;
        } else {
          a = new double[1];
        }
      }
      a[0] = a0;
      unknown = null;
    }

    /** Simple constructor.
     * Build a polynomial from its coefficients
     * @param a coefficients array, the a[0] array element is the
     * constant term while the a[a.length-1] element is the leeding
     * degree coefficient. The array is copied in a new array, so it
     * can be changed once the constructor as returned.
     */
    public Double(double[] a) {

      // remove null high degree coefficients
      int i = a.length - 1;
      while ((i > 0) && (Math.abs(a[i]) <= 1.0e-12)) {
        --i;
      }

      // copy the remaining coefficients
      this.a = new double[i + 1];
      System.arraycopy(a, 0, this.a, 0, i + 1);

      unknown = null;

    }

    /** Simple constructor.
     * Build a one term polynomial from one coefficient and the corresponding degree
     * @param c coefficient
     * @param degree degree associated with the coefficient
     */
    public Double(double c, int degree) {

      if ((Math.abs(c) <= 1.0e-12) || degree < 0) {
        a = new double[1];
        a[0] = 0;
      } else {
        a = new double[degree + 1];
        for (int i = 0; i < degree; ++i) {
          a[i] = 0;
        }
        a[degree] = c;
      }

      unknown = null;

    }

    /** Copy constructor.
     * The copy is a deep copy: the polynomials do <em>not</em> share
     * their coefficients arrays
     * @param p polynomial to copy
     */
    public Double(Double p) {

      a = new double[p.a.length];
      for (int i = 0; i < a.length; ++i) {
        a[i] = p.a[i];
      }

      if (p.unknown == null) {
        unknown = null;
      } else {
        unknown = new String(p.unknown);
      }

    }

    /** Copy constructor.
     * The copy is a deep copy: the polynomials do <em>not</em> share
     * their coefficients arrays
     * @param p polynomial to copy
     */
    public Double(Rational p) {

      RationalNumber[] pA = p.getCoefficients();
      a = new double[pA.length];
      for (int i = 0; i < a.length; ++i) {
        a[i] = pA[i].doubleValue();
      }

      if (p.unknown == null) {
        unknown = null;
      } else {
        unknown = new String(p.unknown);
      }

    }

    /** Create a copy of the instance.
     * @return a copy of the instance
     */
    public Object clone() {
      return new Double(this);
    }

    /** Check if the instance is the null polynomial.
     * @return true if the polynomial is null
     */
    public boolean isZero() {
      return (a.length == 1) && (Math.abs(a[0]) < 1.0e-12);
    }

    /** Check if the instance is the constant unit polynomial.
     * @return true if the polynomial is the constant unit polynomial
     */
    public boolean isOne() {
      return (a.length == 1) && (Math.abs(a[0] - 1) < 1.0e-12);
    }

    /** Check if the instance is the identity polynomial.
     * @return true if the polynomial is the identity polynomial
     */
    public boolean isIdentity() {
      return (a.length == 2)
        && (Math.abs(a[0]) < 1.0e-12)
        && (Math.abs(a[1] - 1) < 1.0e-12);
    }

    /** Get the polynomial degree.
     * @return degree
     */
    public int getDegree() {
      return a.length - 1;
    }

    /** Get the coefficients of the polynomial.
     * @return a reference to the internal coefficients array, the array
     * element at index 0 is the constant term while the element at
     * index a.length-1 is the leeding degree coefficient
     */
    public double[] getCoefficients() {
      return a;
    }

    /** Simplify the polynomial, by removing null high degree terms.
     */
    private void simplify() {

      int i = a.length - 1;
      while ((i > 0) && (Math.abs(a[i]) <= 1.0e-12)) {
        --i;
      }

      if (i < a.length - 1) {
        double[] newA = new double[i + 1];
        System.arraycopy(a, 0, newA, 0, i + 1);
        a = newA;
      }

    }

    /** Add a polynomial to the instance.
     * @param p polynomial to add
     */
    public void addToSelf(Double p) {

      if (p.a.length > a.length) {
        double[] newA = new double[p.a.length];
        System.arraycopy(a, 0, newA, 0, a.length);
        for (int i = a.length; i < newA.length; ++i) {
          newA[i] = 0;
        }
        a = newA;
      }

      for (int i = 0; i < p.a.length; ++i) {
        a[i] += p.a[i];
      }

      simplify();

    }

    /** Add two polynomials.
     * @param p1 first polynomial
     * @param p2 second polynomial
     * @return a new polynomial which is the sum of p1 and p2
     */
    public static Double add(Double p1, Double p2) {
      Double copy = new Double(p1);
      copy.addToSelf(p2);
      return copy;
    }

    /** Subtract a polynomial from the instance.
     * @param p polynomial to subtract
     */
    public void subtractFromSelf(Double p) {

      if (p.a.length > a.length) {
        double[] newA = new double[p.a.length];
        System.arraycopy(a, 0, newA, 0, a.length);
        for (int i = a.length; i < newA.length; ++i) {
          newA[i] = 0;
        }
        a = newA;
      }

      for (int i = 0; i < p.a.length; ++i) {
        a[i] -= p.a[i];
      }

      simplify();

    }

    /** Subtract two polynomials.
     * @param p1 first polynomial
     * @param p2 second polynomial
     * @return a new polynomial which is the difference p1 minus p2
     */
    public static Double subtract(Double p1, Double p2) {
      Double copy = new Double(p1);
      copy.subtractFromSelf(p2);
      return copy;
    }

    /** Negate the instance.
     */
    public void negateSelf() {
      for (int i = 0; i < a.length; ++i) {
        a[i] = -a[i];
      }
    }

    /** Negate a polynomial.
     * @param p polynomial to negate
     * @return a new polynomial which is the opposite of p
     */
    public static Double negate(Double p) {
      Double copy = new Double(p);
      copy.negateSelf();
      return copy;
    }

    /** Multiply the instance by a polynomial.
     * @param p polynomial to multiply by
     */
    public void multiplySelf(Double p) {

      double[] newA = new double[a.length + p.a.length - 1];

      for (int i = 0; i < newA.length; ++i) {
        newA[i] = 0;
        for (int j = Math.max(0, i + 1 - p.a.length);
             j < Math.min(a.length, i + 1);
             ++j) {
          newA[i] += a[j] * p.a[i-j];
        }
      }

      a = newA;

    }

    /** Multiply two polynomials.
     * @param p1 first polynomial
     * @param p2 second polynomial
     * @return a new polynomial which is the product of p1 and p2
     */
    public static Double multiply(Double p1, Double p2) {
      Double copy = new Double(p1);
      copy.multiplySelf(p2);
      return copy;
    }

    /** Multiply the instance by a constant.
     * @param r constant to multiply by
     */
    public void multiplySelf(double r) {

      if (Math.abs(r) < 1.0e-12) {
        a = new double[1];
        a[0] = 0;
      }

      for (int i = 0; i < a.length; ++i) {
        a[i] *= r;
      }

    }

    /** Multiply a polynomial by a constant.
     * @param p polynomial
     * @param r constant
     * @return a new polynomial which is the product of p and r
     */
    public static Double multiply(Double p, double r) {
      Double copy = new Double(p);
      copy.multiplySelf(r);
      return copy;
    }

    /** Multiply the instance by a constant.
     * @param r constant to multiply by
     */
    public void multiplySelf(RationalNumber r) {

      if (r.isZero()) {
        a = new double[1];
        a[0] = 0;
      }

      double rValue = r.doubleValue();
      for (int i = 0; i < a.length; ++i) {
        a[i] *= rValue;
      }

    }

    /** Multiply the instance by a constant.
     * @param l constant to multiply by
     */
    public void multiplySelf(long l) {

      if (l == 0l) {
        a = new double[1];
        a[0] = 0;
      }

      for (int i = 0; i < a.length; ++i) {
        a[i] *= l;
      }

    }

    /** Multiply a polynomial by a constant.
     * @param p polynomial
     * @param l constant
     * @return a new polynomial which is the product of p and l
     */
    public static Double multiply(Double p, long l) {
      Double copy = new Double(p);
      copy.multiplySelf(l);
      return copy;
    }

    /** Get the value of the polynomial for a specified unknown.
     * @param x value of the unknown
     * @return value of the polynomial
     */
    public double valueAt(double x) {
      double y = 0;
      for (int i = a.length - 1; i >= 0; --i) {
        y = y * x + a[i];
      }
      return y;
    }

    /** Get the derivative of the instance with respect to the unknown.
     * The derivative of a n degree polynomial is a n-1 degree polynomial of
     * the same type.
     * @return a new polynomial which is the derivative of the instance
     */
    public Polynomial getDerivative() {
      Double derivative = new Double();
      if (a.length == 1) {
        return derivative;
      }
      derivative.a = new double[a.length - 1];
      for (int i = 1; i < a.length; ++i) {
        derivative.a[i-1] = a[i] * i;
      }
      return derivative;
    }

    /** Set the name of the unknown (to appear during conversions to strings).
     * @param name name to set (if null, the default 'x' value  will be used)
     */
    public void setUnknownName(String name) {
      unknown = name;
    }

    /** Returns a string representation of the polynomial.

    * <p>The representation is user oriented. Terms are displayed lowest
    * degrees first. The multiplications signs, coefficients equals to
    * one and null terms are not displayed (except if the polynomial is 0,
    * in which case the 0 constant term is displayed). Addition of terms
    * with negative coefficients are replaced by subtraction of terms
    * with positive coefficients except for the first displayed term
    * (i.e. we display <code>-3</code> for a constant negative polynomial,
    * but <code>1 - 3 x + x^2</code> if the negative coefficient is not
    * the first one displayed).</p>

    * <p>The name of the unknown is <code>x</code> by default, but can
    * be changed using the {@link #setUnknownName setUnknownName}
    * method.</p>

    * @return a string representation of the polynomial

    */
    public String toString() {

      double maxCoeff = 0;
      for (int i = 0; i < a.length; ++i) {
        double abs = Math.abs(a[i]);
        if (abs > maxCoeff) {
          maxCoeff = abs;
        }
      }
      double epsilon = 1.0e-12 * maxCoeff;

      StringBuffer s = new StringBuffer();
      if (Math.abs(a[0]) <= epsilon) {
        if (a.length == 1) {
          return "0";
        }
      } else {
        s.append(a[0]);
      }

      for (int i = 1; i < a.length; ++i) {

        if (Math.abs(a[i]) > epsilon) {

          if (s.length() > 0) {
            if (a[i] < 0) {
              s.append(" - ");
            } else {
              s.append(" + ");
            }
          } else {
            if (a[i] < 0) {
              s.append("-");
            }
          }

          double absAi = Math.abs(a[i]);
          if (Math.abs(absAi - 1) > 1.0e-12) {
            s.append(absAi);
            s.append(' ');
          }

          s.append((unknown == null) ? defaultUnknown : unknown);
          if (i > 1) {
            s.append('^');
            s.append(Integer.toString(i));
          }
        }

      }

      return s.toString();

    }

    /** Coefficients array. */
    protected double[] a;

    /** Name of the unknown. */
    protected String unknown;

    private static final long serialVersionUID = -5907669461605191069L;

  }

  /** Default name of unknowns. */
  protected static String defaultUnknown = new String("x");

}
