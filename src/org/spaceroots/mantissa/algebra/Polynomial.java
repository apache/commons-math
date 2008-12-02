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
import java.util.Arrays;

/** This class implements polynomials with one unknown.

 * <p>This is an abstract class that only declares general methods but
 * does not hold the coefficients by themselves. Specific subclasses
 * are used to handle exact rational coefficients or approximate real
 * coefficients. This design is taken from the various java.awt.geom
 * classes (Point2D, Rectangle2D ...)</p>

 * <p>The methods implemented deal mainly with the polynomials algebra
 * (addition, multiplication ...) but the analysis aspects are also
 * considered (value of the polynom for a given unknown,
 * derivative).</p>

 * <p>Instances of this class are immutable.</p>

 * @version $Id$
 * @author L. Maisonobe

*/
public abstract class Polynomial implements Serializable {

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
   * @return a new polynomial
   */
  public abstract Polynomial negate();

  /** Multiply the instance by a constant.
   * @param r constant to multiply by
   * @return a new polynomial
   */
  public abstract Polynomial multiply(RationalNumber r);

  /** Multiply the instance by a constant.
   * @param l constant to multiply by
   * @return a new Polynomial
   */
  public abstract Polynomial multiply(long l);

  /** Multiply the instance by a constant.
   * @param i constant to multiply by
   * @return a new Polynomial
   */
  public Polynomial multiply(BigInteger i) {
    return multiply(new RationalNumber(i));
  }

  /** Divide the instance by a constant.
   * @param l constant to multiply by
   * @return a new polynomial
   * @exception ArithmeticException if the constant is zero
   */
  public Polynomial divide(long l) {
    return divide(new RationalNumber(l));
  }

  /** Divide the instance by a constant.
   * @param r constant to multiply by
   * @return a new polynomial
   * @exception ArithmeticException if the constant is zero
   */
  public Polynomial divide(RationalNumber r) {
    return multiply(r.invert());
  }

  /** Divide the instance by a constant.
   * @param i constant to multiply by
   * @return a new polynomial
   * @exception ArithmeticException if the constant is zero
   */
  public Polynomial divide(BigInteger i) {
    return divide(new RationalNumber(i));
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
      a = new RationalNumber[] { RationalNumber.ZERO };
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
      a = new RationalNumber[] { value };
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
        a = new RationalNumber[] { a0, a1 };
      } else {
        a = new RationalNumber[] { a0 };
      }
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
        a = new RationalNumber[] { a0, a1, a2 };
      } else {
        if (! a1.isZero()) {
          a = new RationalNumber[] { a0, a1 };
        } else {
          a = new RationalNumber[] { a0 };
        }
      }
    }

    /** Simple constructor.
     * Build a third degree polynomial
     * @param a3 leeding degree coefficient
     * @param a2 second degree coefficient
     * @param a1 first degree coefficient
     * @param a0 constant term
     */
    public Rational(long a3, long a2, long a1, long a0) {
      this(new RationalNumber(a3),
           new RationalNumber(a2),
           new RationalNumber(a1),
           new RationalNumber(a0));
    }

    /** Simple constructor.
     * Build a third degree polynomial
     * @param a3 leeding degree coefficient
     * @param a2 second degree coefficient
     * @param a1 first degree coefficient
     * @param a0 constant term
     */
    public Rational(RationalNumber a3, RationalNumber a2,
                    RationalNumber a1, RationalNumber a0) {
      if (! a3.isZero()) {
        a = new RationalNumber[] { a0, a1, a2, a3 };
      } else {
        if (! a2.isZero()) {
          a = new RationalNumber[] { a0, a1, a2 };
        } else {
          if (! a1.isZero()) {
            a = new RationalNumber[] { a0, a1 };
          } else {
            a = new RationalNumber[] { a0 };
          }
        }
      }
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

    }

    /** Simple constructor.
     * Build a one term polynomial from one coefficient and the corresponding degree
     * @param c coefficient
     * @param degree degree associated with the coefficient
     */
    public Rational(RationalNumber c, int degree) {

      if (c.isZero() || degree < 0) {
        a = new RationalNumber[] { RationalNumber.ZERO };
      } else {
        a = new RationalNumber[degree + 1];
        Arrays.fill(a, 0, degree, RationalNumber.ZERO);
        a[degree] = c;
      }

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
     * @return a copy of the coefficients array, the array
     * element at index 0 is the constant term while the element at
     * index a.length-1 is the leading degree coefficient
     */
    public RationalNumber[] getCoefficients() {
      return (RationalNumber[]) a.clone();
    }

    /** Add a polynomial to the instance
     * @param p polynomial to add
     * @return a new polynomial which is the sum of the instance and p
     */
    public Rational add(Rational p) {

      // identify the lowest degree polynomial
      int lowLength  = Math.min(a.length, p.a.length);
      int highLength = Math.max(a.length, p.a.length);

      // build the coefficients array
      RationalNumber[] newA = new RationalNumber[highLength];
      for (int i = 0; i < lowLength; ++i) {
        newA[i] = a[i].add(p.a[i]);
      }
      System.arraycopy((a.length < p.a.length) ? p.a : a,
                       lowLength, newA, lowLength, highLength - lowLength);

      return new Rational(newA);

    }

    /** Subtract a polynomial from the instance.
     * @param p polynomial to subtract
     * @return a new polynomial which is the difference the instance minus p
     */
    public Rational subtract(Rational p) {

      // identify the lowest degree polynomial
      int lowLength  = Math.min(a.length, p.a.length);
      int highLength = Math.max(a.length, p.a.length);

      // build the coefficients array
      RationalNumber[] newA = new RationalNumber[highLength];
      for (int i = 0; i < lowLength; ++i) {
        newA[i] = a[i].subtract(p.a[i]);
      }
      if (a.length < p.a.length) {
        for (int i = lowLength; i < highLength; ++i) {
          newA[i] = p.a[i].negate();
        }
      } else {
        System.arraycopy(a, lowLength, newA, lowLength, highLength - lowLength);
      }

      return new Rational(newA);

    }

    /** Negate the instance.
     * @return a new polynomial
     */
    public Polynomial negate() {
      RationalNumber[] newA = new RationalNumber[a.length];
      for (int i = 0; i < a.length; ++i) {
        newA[i] = a[i].negate();
      }
      return new Rational(newA);
    }

    /** Multiply the instance by a polynomial.
     * @param p polynomial to multiply by
     * @return a new polynomial
     */
    public Rational multiply(Rational p) {

      RationalNumber[] newA = new RationalNumber[a.length + p.a.length - 1];

      for (int i = 0; i < newA.length; ++i) {
        newA[i] = RationalNumber.ZERO;
        for (int j = Math.max(0, i + 1 - p.a.length);
             j < Math.min(a.length, i + 1);
             ++j) {
          newA[i] = newA[i].add(a[j].multiply(p.a[i-j]));
        }
      }

      return new Rational(newA);

    }

    /** Multiply the instance by a constant.
     * @param l constant to multiply by
     * @return a new polynomial
     */
    public Polynomial multiply(long l) {
      return multiply(new RationalNumber(l));
    }

    /** Multiply the instance by a constant.
     * @param r constant to multiply by
     * @return a new polynomial
     */
    public Polynomial multiply(RationalNumber r) {

      if (r.isZero()) {
        return new Rational(new RationalNumber[] { RationalNumber.ZERO });
      }

      if (r.isOne()) {
        return this;
      }

      RationalNumber[] newA = new RationalNumber[a.length];
      for (int i = 0; i < a.length; ++i) {
        newA[i] = a[i].multiply(r);
      }
      return new Rational(newA);

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
      if (a.length == 1) {
        return new Rational();
      }
      RationalNumber[] newA = new RationalNumber[a.length - 1];
      for (int i = 1; i < a.length; ++i) {
        newA[i - 1] = a[i].multiply(i);
      }
      return new Rational(newA);
    }

    /** Perform the euclidian division of two polynomials.
     * @param dividend numerator polynomial
     * @param divisor  denominator polynomial
     * @return an object containing the quotient and the remainder of the division
     */
    public static DivisionResult euclidianDivision(Rational dividend,
                                                   Rational divisor) {

      Rational quotient  = new Rational(0l);
      Rational remainder = dividend;

      int divisorDegree   = divisor.getDegree();
      int remainderDegree = remainder.getDegree();
      while ((! remainder.isZero()) && (remainderDegree >= divisorDegree)) {

        RationalNumber c =
          remainder.a[remainderDegree].divide(divisor.a[divisorDegree]);
        Rational monomial = new Rational(c, remainderDegree - divisorDegree);

        remainder = remainder.subtract(monomial.multiply(divisor));
        quotient  = quotient.add(monomial);

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
        RationalNumber newCoeff = a[i].multiply(lcm);
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

          s.append("x");
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

    private static final long serialVersionUID = -794133890636181115L;

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
      a = new double[] { 0.0 };
    }

    /** Simple constructor.
     * Build a constant polynomial
     * @param value constant value of the polynomial
     */
    public Double(long value) {
      this((double) value);
    }

    /** Simple constructor.
     * Build a constant polynomial
     * @param value constant value of the polynomial
     */
    public Double(double value) {
      a = new double[] { value };
    }

    /** Simple constructor.
     * Build a first degree polynomial
     * @param a1 leeding degree coefficient
     * @param a0 constant term
     */
    public Double(long a1, long a0) {
      this((double) a1, (double) a0);
    }

    /** Simple constructor.
     * Build a first degree polynomial
     * @param a1 leeding degree coefficient
     * @param a0 constant term
     */
    public Double(double a1, double a0) {
      if (a1 != 0) {
        a = new double[] { a0, a1 };
      } else {
        a = new double[] { a0 };
      }
    }

    /** Simple constructor.
     * Build a second degree polynomial
     * @param a2 leeding degree coefficient
     * @param a1 first degree coefficient
     * @param a0 constant term
     */
    public Double(long a2, long a1, long a0) {
      this((double) a2, (double) a1, (double) a0);
    }

    /** Simple constructor.
     * Build a second degree polynomial
     * @param a2 leeding degree coefficient
     * @param a1 first degree coefficient
     * @param a0 constant term
     */
    public Double(double a2, double a1, double a0) {
      if (a2 != 0) {
        a = new double[] { a0, a1, a2 };
      } else {
        if (a1 != 0) {
          a = new double[] { a0, a1 };
        } else {
          a = new double[] { a0 };
        }
      }
    }

    /** Simple constructor.
     * Build a third degree polynomial
     * @param a3 leeding degree coefficient
     * @param a2 second degree coefficient
     * @param a1 first degree coefficient
     * @param a0 constant term
     */
    public Double(long a3, long a2, long a1, long a0) {
      this((double) a3, (double) a2, (double) a1, (double) a0);
    }

    /** Simple constructor.
     * Build a third degree polynomial
     * @param a3 leeding degree coefficient
     * @param a2 second degree coefficient
     * @param a1 first degree coefficient
     * @param a0 constant term
     */
    public Double(double a3, double a2, double a1, double a0) {
      if (a3 != 0) {
        a = new double[] { a0, a1, a2, a3 };
      } else {
        if (a2 != 0) {
          a = new double[] { a0, a1, a2 };
        } else {
          if (a1 != 0) {
            a = new double[] { a0, a1 };
          } else {
            a = new double[] { a0 };
          }
        }
      }
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
      while ((i > 0) && (a[i] == 0)) {
        --i;
      }

      // copy the remaining coefficients
      this.a = new double[i + 1];
      System.arraycopy(a, 0, this.a, 0, i + 1);

    }

    /** Simple constructor.
     * Build a one term polynomial from one coefficient and the corresponding degree
     * @param c coefficient
     * @param degree degree associated with the coefficient
     */
    public Double(double c, int degree) {
      if ((c == 0) || degree < 0) {
        a = new double[] { 0.0 };
      } else {
        a = new double[degree + 1];
        Arrays.fill(a, 0, degree, 0.0);
        a[degree] = c;
      }
    }

    /** Simple constructor.
     * Build a {@link Polynomial.Double Polynomial.Double} from a
     * {@link Polynomial.Rational Polynomial.Rational}
     * @param r a rational polynomial
     */
    public Double(Rational r) {
      // convert the coefficients
      a = new double[r.a.length];
      for (int i = 0; i < a.length; ++i) {
        a[i] = r.a[i].doubleValue();
      }
    }

    /** Check if the instance is the null polynomial.
     * @return true if the polynomial is null
     */
    public boolean isZero() {
      return (a.length == 1) && (a[0] == 0);
    }

    /** Check if the instance is the constant unit polynomial.
     * @return true if the polynomial is the constant unit polynomial
     */
    public boolean isOne() {
      return (a.length == 1) && ((a[0] - 1.0) == 0);
    }

    /** Check if the instance is the identity polynomial.
     * @return true if the polynomial is the identity polynomial
     */
    public boolean isIdentity() {
      return (a.length == 2) && (a[0] == 0) && ((a[1] - 1.0) == 0);
    }

    /** Get the polynomial degree.
     * @return degree
     */
    public int getDegree() {
      return a.length - 1;
    }

    /** Get the coefficients of the polynomial.
     * @return a copy of the coefficients array, the array
     * element at index 0 is the constant term while the element at
     * index a.length-1 is the leading degree coefficient
     */
    public double[] getCoefficients() {
      return (double[]) a.clone();
    }

    /** Add a polynomial to the instance
     * @param p polynomial to add
     * @return a new polynomial which is the sum of the instance and p
     */
    public Double add(Double p) {

      // identify the lowest degree polynomial
      int lowLength  = Math.min(a.length, p.a.length);
      int highLength = Math.max(a.length, p.a.length);

      // build the coefficients array
      double[] newA = new double[highLength];
      for (int i = 0; i < lowLength; ++i) {
        newA[i] = a[i] + p.a[i];
      }
      System.arraycopy((a.length < p.a.length) ? p.a : a,
                       lowLength, newA, lowLength, highLength - lowLength);

      return Double.valueOf(newA);

    }

    /** Subtract a polynomial from the instance.
     * @param p polynomial to subtract
     * @return a new polynomial which is the difference the instance minus p
     */
    public Double subtract(Double p) {

      // identify the lowest degree polynomial
      int lowLength  = Math.min(a.length, p.a.length);
      int highLength = Math.max(a.length, p.a.length);

      // build the coefficients array
      double[] newA = new double[highLength];
      for (int i = 0; i < lowLength; ++i) {
        newA[i] = a[i] - p.a[i];
      }
      if (a.length < p.a.length) {
        for (int i = lowLength; i < highLength; ++i) {
          newA[i] = -p.a[i];
        }
      } else {
        System.arraycopy(a, lowLength, newA, lowLength, highLength - lowLength);
      }

      return Double.valueOf(newA);

    }

    /** Negate the instance.
     * @return a new polynomial
     */
    public Polynomial negate() {
      double[] newA = new double[a.length];
      for (int i = 0; i < a.length; ++i) {
        newA[i] = -a[i];
      }
      return Double.valueOf(newA);
    }

    /** Multiply the instance by a polynomial.
     * @param p polynomial to multiply by
     * @return a new polynomial
     */
    public Double multiply(Double p) {

      double[] newA = new double[a.length + p.a.length - 1];

      for (int i = 0; i < newA.length; ++i) {
        newA[i] = 0.0;
        for (int j = Math.max(0, i + 1 - p.a.length);
             j < Math.min(a.length, i + 1);
             ++j) {
          newA[i] += a[j] * p.a[i-j];
        }
      }

      return Double.valueOf(newA);

    }

    /** Multiply the instance by a constant.
     * @param l constant to multiply by
     * @return a new polynomial
     */
    public Polynomial multiply(long l) {
      return multiply((double) l);
    }

    /** Multiply the instance by a constant.
     * @param r constant to multiply by
     * @return a new polynomial
     */
    public Polynomial multiply(RationalNumber r) {
      return multiply(r.doubleValue());
    }

    /** Multiply the instance by a constant.
     * @param r constant to multiply by
     * @return a new polynomial
     */
    public Polynomial multiply(double r) {

      if (r == 0) {
        return Double.valueOf(new double[] { 0.0 });
      }

      double[] newA = new double[a.length];
      for (int i = 0; i < a.length; ++i) {
        newA[i] = a[i] * r;
      }
      return Double.valueOf(newA);

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
      if (a.length == 1) {
        return Double.valueOf();
      }
      double[] newA = new double[a.length - 1];
      for (int i = 1; i < a.length; ++i) {
        newA[i - 1] = a[i] * i;
      }
      return Double.valueOf(newA);
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

    * @return a string representation of the polynomial

    */
    public String toString() {

      StringBuffer s = new StringBuffer();
      if (a[0] == 0.0) {
        if (a.length == 1) {
          return "0";
        }
      } else {
        s.append(java.lang.Double.toString(a[0]));
      }

      for (int i = 1; i < a.length; ++i) {

        if (a[i] != 0) {

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
          if ((absAi - 1) != 0) {
            s.append(java.lang.Double.toString(absAi));
            s.append(' ');
          }

          s.append("x");
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

    private static final long serialVersionUID = -4210522025715687648L;

  }

}
