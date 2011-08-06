/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.NonMonotonousSequenceException;
import org.apache.commons.math.exception.NotFiniteNumberException;
import org.apache.commons.math.exception.NotPositiveException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Some useful additions to the built-in functions in {@link Math}.
 * @version $Id$
 */
public final class MathUtils {

    /** Smallest positive number such that 1 - EPSILON is not numerically equal to 1. */
    public static final double EPSILON = 0x1.0p-53;

    /** Safe minimum, such that 1 / SAFE_MIN does not overflow.
     * <p>In IEEE 754 arithmetic, this is also the smallest normalized
     * number 2<sup>-1022</sup>.</p>
     */
    public static final double SAFE_MIN = 0x1.0p-1022;

    /**
     * 2 &pi;.
     * @since 2.1
     */
    public static final double TWO_PI = 2 * FastMath.PI;

    /** -1.0 cast as a byte. */
    private static final byte  NB = (byte)-1;

    /** -1.0 cast as a short. */
    private static final short NS = (short)-1;

    /** 1.0 cast as a byte. */
    private static final byte  PB = (byte)1;

    /** 1.0 cast as a short. */
    private static final short PS = (short)1;

    /** 0.0 cast as a byte. */
    private static final byte  ZB = (byte)0;

    /** 0.0 cast as a short. */
    private static final short ZS = (short)0;

    /** Offset to order signed double numbers lexicographically. */
    private static final long SGN_MASK = 0x8000000000000000L;

    /** Offset to order signed double numbers lexicographically. */
    private static final int SGN_MASK_FLOAT = 0x80000000;

    /** All long-representable factorials */
    private static final long[] FACTORIALS = new long[] {
                       1l,                  1l,                   2l,
                       6l,                 24l,                 120l,
                     720l,               5040l,               40320l,
                  362880l,            3628800l,            39916800l,
               479001600l,         6227020800l,         87178291200l,
           1307674368000l,     20922789888000l,     355687428096000l,
        6402373705728000l, 121645100408832000l, 2432902008176640000l };

    /** Factor used for splitting double numbers: n = 2^27 + 1. */
    private static final int SPLIT_FACTOR = 0x8000001;

    /**
     * Private Constructor
     */
    private MathUtils() {
        super();
    }

    /**
     * Add two integers, checking for overflow.
     *
     * @param x an addend
     * @param y an addend
     * @return the sum {@code x+y}
     * @throws MathArithmeticException if the result can not be represented
     * as an {@code int}.
     * @since 1.1
     */
    public static int addAndCheck(int x, int y) {
        long s = (long)x + (long)y;
        if (s < Integer.MIN_VALUE || s > Integer.MAX_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, x, y);
        }
        return (int)s;
    }

    /**
     * Add two long integers, checking for overflow.
     *
     * @param a an addend
     * @param b an addend
     * @return the sum {@code a+b}
     * @throws MathArithmeticException if the result can not be represented as an
     *         long
     * @since 1.2
     */
    public static long addAndCheck(long a, long b) {
        return addAndCheck(a, b, LocalizedFormats.OVERFLOW_IN_ADDITION);
    }

    /**
     * Add two long integers, checking for overflow.
     *
     * @param a Addend.
     * @param b Addend.
     * @param pattern Pattern to use for any thrown exception.
     * @return the sum {@code a + b}.
     * @throws MathArithmeticException if the result cannot be represented
     * as a {@code long}.
     * @since 1.2
     */
    private static long addAndCheck(long a, long b, Localizable pattern) {
        long ret;
        if (a > b) {
            // use symmetry to reduce boundary cases
            ret = addAndCheck(b, a, pattern);
        } else {
            // assert a <= b

            if (a < 0) {
                if (b < 0) {
                    // check for negative overflow
                    if (Long.MIN_VALUE - b <= a) {
                        ret = a + b;
                    } else {
                        throw new MathArithmeticException(pattern, a, b);
                    }
                } else {
                    // opposite sign addition is always safe
                    ret = a + b;
                }
            } else {
                // assert a >= 0
                // assert b >= 0

                // check for positive overflow
                if (a <= Long.MAX_VALUE - b) {
                    ret = a + b;
                } else {
                    throw new MathArithmeticException(pattern, a, b);
                }
            }
        }
        return ret;
    }

    /**
     * Returns an exact representation of the <a
     * href="http://mathworld.wolfram.com/BinomialCoefficient.html"> Binomial
     * Coefficient</a>, "{@code n choose k}", the number of
     * {@code k}-element subsets that can be selected from an
     * {@code n}-element set.
     * <p>
     * <Strong>Preconditions</strong>:
     * <ul>
     * <li> {@code 0 <= k <= n } (otherwise
     * {@code IllegalArgumentException} is thrown)</li>
     * <li> The result is small enough to fit into a {@code long}. The
     * largest value of {@code n} for which all coefficients are
     * {@code  < Long.MAX_VALUE} is 66. If the computed value exceeds
     * {@code Long.MAX_VALUE} an {@code ArithMeticException} is
     * thrown.</li>
     * </ul></p>
     *
     * @param n the size of the set
     * @param k the size of the subsets to be counted
     * @return {@code n choose k}
     * @throws MathIllegalArgumentException if preconditions are not met.
     * @throws MathArithmeticException if the result is too large to be
     * represented by a long integer.
     */
    public static long binomialCoefficient(final int n, final int k) {
        checkBinomial(n, k);
        if ((n == k) || (k == 0)) {
            return 1;
        }
        if ((k == 1) || (k == n - 1)) {
            return n;
        }
        // Use symmetry for large k
        if (k > n / 2) {
            return binomialCoefficient(n, n - k);
        }

        // We use the formula
        // (n choose k) = n! / (n-k)! / k!
        // (n choose k) == ((n-k+1)*...*n) / (1*...*k)
        // which could be written
        // (n choose k) == (n-1 choose k-1) * n / k
        long result = 1;
        if (n <= 61) {
            // For n <= 61, the naive implementation cannot overflow.
            int i = n - k + 1;
            for (int j = 1; j <= k; j++) {
                result = result * i / j;
                i++;
            }
        } else if (n <= 66) {
            // For n > 61 but n <= 66, the result cannot overflow,
            // but we must take care not to overflow intermediate values.
            int i = n - k + 1;
            for (int j = 1; j <= k; j++) {
                // We know that (result * i) is divisible by j,
                // but (result * i) may overflow, so we split j:
                // Filter out the gcd, d, so j/d and i/d are integer.
                // result is divisible by (j/d) because (j/d)
                // is relative prime to (i/d) and is a divisor of
                // result * (i/d).
                final long d = gcd(i, j);
                result = (result / (j / d)) * (i / d);
                i++;
            }
        } else {
            // For n > 66, a result overflow might occur, so we check
            // the multiplication, taking care to not overflow
            // unnecessary.
            int i = n - k + 1;
            for (int j = 1; j <= k; j++) {
                final long d = gcd(i, j);
                result = mulAndCheck(result / (j / d), i / d);
                i++;
            }
        }
        return result;
    }

    /**
     * Returns a {@code double} representation of the <a
     * href="http://mathworld.wolfram.com/BinomialCoefficient.html"> Binomial
     * Coefficient</a>, "{@code n choose k}", the number of
     * {@code k}-element subsets that can be selected from an
     * {@code n}-element set.
     * <p>
     * <Strong>Preconditions</strong>:
     * <ul>
     * <li> {@code 0 <= k <= n } (otherwise
     * {@code IllegalArgumentException} is thrown)</li>
     * <li> The result is small enough to fit into a {@code double}. The
     * largest value of {@code n} for which all coefficients are <
     * Double.MAX_VALUE is 1029. If the computed value exceeds Double.MAX_VALUE,
     * Double.POSITIVE_INFINITY is returned</li>
     * </ul></p>
     *
     * @param n the size of the set
     * @param k the size of the subsets to be counted
     * @return {@code n choose k}
     * @throws IllegalArgumentException if preconditions are not met.
     */
    public static double binomialCoefficientDouble(final int n, final int k) {
        checkBinomial(n, k);
        if ((n == k) || (k == 0)) {
            return 1d;
        }
        if ((k == 1) || (k == n - 1)) {
            return n;
        }
        if (k > n/2) {
            return binomialCoefficientDouble(n, n - k);
        }
        if (n < 67) {
            return binomialCoefficient(n,k);
        }

        double result = 1d;
        for (int i = 1; i <= k; i++) {
             result *= (double)(n - k + i) / (double)i;
        }

        return FastMath.floor(result + 0.5);
    }

    /**
     * Returns the natural {@code log} of the <a
     * href="http://mathworld.wolfram.com/BinomialCoefficient.html"> Binomial
     * Coefficient</a>, "{@code n choose k}", the number of
     * {@code k}-element subsets that can be selected from an
     * {@code n}-element set.
     * <p>
     * <Strong>Preconditions</strong>:
     * <ul>
     * <li> {@code 0 <= k <= n } (otherwise
     * {@code IllegalArgumentException} is thrown)</li>
     * </ul></p>
     *
     * @param n the size of the set
     * @param k the size of the subsets to be counted
     * @return {@code n choose k}
     * @throws IllegalArgumentException if preconditions are not met.
     */
    public static double binomialCoefficientLog(final int n, final int k) {
        checkBinomial(n, k);
        if ((n == k) || (k == 0)) {
            return 0;
        }
        if ((k == 1) || (k == n - 1)) {
            return FastMath.log(n);
        }

        /*
         * For values small enough to do exact integer computation,
         * return the log of the exact value
         */
        if (n < 67) {
            return FastMath.log(binomialCoefficient(n,k));
        }

        /*
         * Return the log of binomialCoefficientDouble for values that will not
         * overflow binomialCoefficientDouble
         */
        if (n < 1030) {
            return FastMath.log(binomialCoefficientDouble(n, k));
        }

        if (k > n / 2) {
            return binomialCoefficientLog(n, n - k);
        }

        /*
         * Sum logs for values that could overflow
         */
        double logSum = 0;

        // n!/(n-k)!
        for (int i = n - k + 1; i <= n; i++) {
            logSum += FastMath.log(i);
        }

        // divide by k!
        for (int i = 2; i <= k; i++) {
            logSum -= FastMath.log(i);
        }

        return logSum;
    }

    /**
     * Check binomial preconditions.
     *
     * @param n Size of the set.
     * @param k Size of the subsets to be counted.
     * @throws NotPositiveException if {@code n < 0}.
     * @throws NumberIsTooLargeException if {@code k > n}.
     */
    private static void checkBinomial(final int n, final int k) {
        if (n < k) {
            throw new NumberIsTooLargeException(LocalizedFormats.BINOMIAL_INVALID_PARAMETERS_ORDER,
                                                k, n, true);
        }
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.BINOMIAL_NEGATIVE_PARAMETER, n);
        }
    }

    /**
     * Compares two numbers given some amount of allowed error.
     *
     * @param x the first number
     * @param y the second number
     * @param eps the amount of error to allow when checking for equality
     * @return <ul><li>0 if  {@link #equals(double, double, double) equals(x, y, eps)}</li>
     *       <li>&lt; 0 if !{@link #equals(double, double, double) equals(x, y, eps)} &amp;&amp; x &lt; y</li>
     *       <li>> 0 if !{@link #equals(double, double, double) equals(x, y, eps)} &amp;&amp; x > y</li></ul>
     */
    public static int compareTo(double x, double y, double eps) {
        if (equals(x, y, eps)) {
            return 0;
        } else if (x < y) {
            return -1;
        }
        return 1;
    }

    /**
     * Compares two numbers given some amount of allowed error.
     * Two float numbers are considered equal if there are {@code (maxUlps - 1)}
     * (or fewer) floating point numbers between them, i.e. two adjacent floating
     * point numbers are considered equal.
     * Adapted from <a
     * href="http://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm">
     * Bruce Dawson</a>
     *
     * @param x first value
     * @param y second value
     * @param maxUlps {@code (maxUlps - 1)} is the number of floating point
     * values between {@code x} and {@code y}.
     * @return <ul><li>0 if  {@link #equals(double, double, int) equals(x, y, maxUlps)}</li>
     *       <li>&lt; 0 if !{@link #equals(double, double, int) equals(x, y, maxUlps)} &amp;&amp; x &lt; y</li>
     *       <li>> 0 if !{@link #equals(double, double, int) equals(x, y, maxUlps)} &amp;&amp; x > y</li></ul>
     */
    public static int compareTo(final double x, final double y, final int maxUlps) {
        if (equals(x, y, maxUlps)) {
            return 0;
        } else if (x < y) {
            return -1;
        }
        return 1;
    }

    /**
     * Returns the <a href="http://mathworld.wolfram.com/HyperbolicCosine.html">
     * hyperbolic cosine</a> of x.
     *
     * @param x double value for which to find the hyperbolic cosine
     * @return hyperbolic cosine of x
     */
    public static double cosh(double x) {
        return (FastMath.exp(x) + FastMath.exp(-x)) / 2.0;
    }

    /**
     * Returns true iff they are equal as defined by
     * {@link #equals(float,float,int) equals(x, y, 1)}.
     *
     * @param x first value
     * @param y second value
     * @return {@code true} if the values are equal.
     */
    public static boolean equals(float x, float y) {
        return equals(x, y, 1);
    }

    /**
     * Returns true if both arguments are NaN or neither is NaN and they are
     * equal as defined by {@link #equals(float,float) equals(x, y, 1)}.
     *
     * @param x first value
     * @param y second value
     * @return {@code true} if the values are equal or both are NaN.
     * @since 2.2
     */
    public static boolean equalsIncludingNaN(float x, float y) {
        return (Float.isNaN(x) && Float.isNaN(y)) || equals(x, y, 1);
    }

    /**
     * Returns true if both arguments are equal or within the range of allowed
     * error (inclusive).
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return {@code true} if the values are equal or within range of each other.
     * @since 2.2
     */
    public static boolean equals(float x, float y, float eps) {
        return equals(x, y, 1) || FastMath.abs(y - x) <= eps;
    }

    /**
     * Returns true if both arguments are NaN or are equal or within the range
     * of allowed error (inclusive).
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return {@code true} if the values are equal or within range of each other,
     * or both are NaN.
     * @since 2.2
     */
    public static boolean equalsIncludingNaN(float x, float y, float eps) {
        return equalsIncludingNaN(x, y) || (FastMath.abs(y - x) <= eps);
    }

    /**
     * Returns true if both arguments are equal or within the range of allowed
     * error (inclusive).
     * Two float numbers are considered equal if there are {@code (maxUlps - 1)}
     * (or fewer) floating point numbers between them, i.e. two adjacent floating
     * point numbers are considered equal.
     * Adapted from <a
     * href="http://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm">
     * Bruce Dawson</a>
     *
     * @param x first value
     * @param y second value
     * @param maxUlps {@code (maxUlps - 1)} is the number of floating point
     * values between {@code x} and {@code y}.
     * @return {@code true} if there are fewer than {@code maxUlps} floating
     * point values between {@code x} and {@code y}.
     * @since 2.2
     */
    public static boolean equals(float x, float y, int maxUlps) {
        int xInt = Float.floatToIntBits(x);
        int yInt = Float.floatToIntBits(y);

        // Make lexicographically ordered as a two's-complement integer.
        if (xInt < 0) {
            xInt = SGN_MASK_FLOAT - xInt;
        }
        if (yInt < 0) {
            yInt = SGN_MASK_FLOAT - yInt;
        }

        final boolean isEqual = FastMath.abs(xInt - yInt) <= maxUlps;

        return isEqual && !Float.isNaN(x) && !Float.isNaN(y);
    }

    /**
     * Returns true if both arguments are NaN or if they are equal as defined
     * by {@link #equals(float,float,int) equals(x, y, maxUlps)}.
     *
     * @param x first value
     * @param y second value
     * @param maxUlps {@code (maxUlps - 1)} is the number of floating point
     * values between {@code x} and {@code y}.
     * @return {@code true} if both arguments are NaN or if there are less than
     * {@code maxUlps} floating point values between {@code x} and {@code y}.
     * @since 2.2
     */
    public static boolean equalsIncludingNaN(float x, float y, int maxUlps) {
        return (Float.isNaN(x) && Float.isNaN(y)) || equals(x, y, maxUlps);
    }

    /**
     * Returns true iff both arguments are null or have same dimensions and all
     * their elements are equal as defined by
     * {@link #equals(float,float)}.
     *
     * @param x first array
     * @param y second array
     * @return true if the values are both null or have same dimension
     * and equal elements.
     */
    public static boolean equals(float[] x, float[] y) {
        if ((x == null) || (y == null)) {
            return !((x == null) ^ (y == null));
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!equals(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true iff both arguments are null or have same dimensions and all
     * their elements are equal as defined by
     * {@link #equalsIncludingNaN(double,double) this method}.
     *
     * @param x first array
     * @param y second array
     * @return true if the values are both null or have same dimension and
     * equal elements
     * @since 2.2
     */
    public static boolean equalsIncludingNaN(float[] x, float[] y) {
        if ((x == null) || (y == null)) {
            return !((x == null) ^ (y == null));
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!equalsIncludingNaN(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true iff they are equal as defined by
     * {@link #equals(double,double,int) equals(x, y, 1)}.
     *
     * @param x first value
     * @param y second value
     * @return {@code true} if the values are equal.
     */
    public static boolean equals(double x, double y) {
        return equals(x, y, 1);
    }

    /**
     * Returns true if both arguments are NaN or neither is NaN and they are
     * equal as defined by {@link #equals(double,double) equals(x, y, 1)}.
     *
     * @param x first value
     * @param y second value
     * @return {@code true} if the values are equal or both are NaN.
     * @since 2.2
     */
    public static boolean equalsIncludingNaN(double x, double y) {
        return (Double.isNaN(x) && Double.isNaN(y)) || equals(x, y, 1);
    }

    /**
     * Returns {@code true} if there is no double value strictly between the
     * arguments or the difference between them is within the range of allowed
     * error (inclusive).
     *
     * @param x First value.
     * @param y Second value.
     * @param eps Amount of allowed absolute error.
     * @return {@code true} if the values are two adjacent floating point
     * numbers or they are within range of each other.
     */
    public static boolean equals(double x, double y, double eps) {
        return equals(x, y, 1) || FastMath.abs(y - x) <= eps;
    }

    /**
     * Returns true if both arguments are NaN or are equal or within the range
     * of allowed error (inclusive).
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return {@code true} if the values are equal or within range of each other,
     * or both are NaN.
     * @since 2.2
     */
    public static boolean equalsIncludingNaN(double x, double y, double eps) {
        return equalsIncludingNaN(x, y) || (FastMath.abs(y - x) <= eps);
    }

    /**
     * Returns true if both arguments are equal or within the range of allowed
     * error (inclusive).
     * Two float numbers are considered equal if there are {@code (maxUlps - 1)}
     * (or fewer) floating point numbers between them, i.e. two adjacent floating
     * point numbers are considered equal.
     * Adapted from <a
     * href="http://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm">
     * Bruce Dawson</a>
     *
     * @param x first value
     * @param y second value
     * @param maxUlps {@code (maxUlps - 1)} is the number of floating point
     * values between {@code x} and {@code y}.
     * @return {@code true} if there are fewer than {@code maxUlps} floating
     * point values between {@code x} and {@code y}.
     */
    public static boolean equals(double x, double y, int maxUlps) {
        long xInt = Double.doubleToLongBits(x);
        long yInt = Double.doubleToLongBits(y);

        // Make lexicographically ordered as a two's-complement integer.
        if (xInt < 0) {
            xInt = SGN_MASK - xInt;
        }
        if (yInt < 0) {
            yInt = SGN_MASK - yInt;
        }

        final boolean isEqual = FastMath.abs(xInt - yInt) <= maxUlps;

        return isEqual && !Double.isNaN(x) && !Double.isNaN(y);
    }

    /**
     * Returns true if both arguments are NaN or if they are equal as defined
     * by {@link #equals(double,double,int) equals(x, y, maxUlps)}.
     *
     * @param x first value
     * @param y second value
     * @param maxUlps {@code (maxUlps - 1)} is the number of floating point
     * values between {@code x} and {@code y}.
     * @return {@code true} if both arguments are NaN or if there are less than
     * {@code maxUlps} floating point values between {@code x} and {@code y}.
     * @since 2.2
     */
    public static boolean equalsIncludingNaN(double x, double y, int maxUlps) {
        return (Double.isNaN(x) && Double.isNaN(y)) || equals(x, y, maxUlps);
    }

    /**
     * Returns {@code true} iff both arguments are {@code null} or have same
     * dimensions and all their elements are equal as defined by
     * {@link #equals(double,double)}.
     *
     * @param x First array.
     * @param y Second array.
     * @return {@code true} if the values are both {@code null} or have same
     * dimension and equal elements.
     */
    public static boolean equals(double[] x, double[] y) {
        if ((x == null) || (y == null)) {
            return !((x == null) ^ (y == null));
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!equals(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} iff both arguments are {@code null} or have same
     * dimensions and all their elements are equal as defined by
     * {@link #equalsIncludingNaN(double,double) this method}.
     *
     * @param x First array.
     * @param y Second array.
     * @return {@code true} if the values are both {@code null} or have same
     * dimension and equal elements.
     * @since 2.2
     */
    public static boolean equalsIncludingNaN(double[] x, double[] y) {
        if ((x == null) || (y == null)) {
            return !((x == null) ^ (y == null));
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!equalsIncludingNaN(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns n!. Shorthand for {@code n} <a
     * href="http://mathworld.wolfram.com/Factorial.html"> Factorial</a>, the
     * product of the numbers {@code 1,...,n}.
     * <p>
     * <Strong>Preconditions</strong>:
     * <ul>
     * <li> {@code n >= 0} (otherwise
     * {@code IllegalArgumentException} is thrown)</li>
     * <li> The result is small enough to fit into a {@code long}. The
     * largest value of {@code n} for which {@code n!} <
     * Long.MAX_VALUE} is 20. If the computed value exceeds {@code Long.MAX_VALUE}
     * an {@code ArithMeticException } is thrown.</li>
     * </ul>
     * </p>
     *
     * @param n argument
     * @return {@code n!}
     * @throws MathArithmeticException if the result is too large to be represented
     * by a {@code long}.
     * @throws NotPositiveException if {@code n < 0}.
     * @throws MathArithmeticException if {@code n > 20}: The factorial value is too
     * large to fit in a {@code long}.
     */
    public static long factorial(final int n) {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER,
                                           n);
        }
        if (n > 20) {
            throw new MathArithmeticException();
        }
        return FACTORIALS[n];
    }

    /**
     * Compute n!, the<a href="http://mathworld.wolfram.com/Factorial.html">
     * factorial</a> of {@code n} (the product of the numbers 1 to n), as a
     * {@code double}.
     * The result should be small enough to fit into a {@code double}: The
     * largest {@code n} for which {@code n! < Double.MAX_VALUE} is 170.
     * If the computed value exceeds {@code Double.MAX_VALUE},
     * {@code Double.POSITIVE_INFINITY} is returned.
     *
     * @param n Argument.
     * @return {@code n!}
     * @throws NotPositiveException if {@code n < 0}.
     */
    public static double factorialDouble(final int n) {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER,
                                           n);
        }
        if (n < 21) {
            return factorial(n);
        }
        return FastMath.floor(FastMath.exp(factorialLog(n)) + 0.5);
    }

    /**
     * Compute the natural logarithm of the factorial of {@code n}.
     *
     * @param n Argument.
     * @return {@code n!}
     * @throws NotPositiveException if {@code n < 0}.
     */
    public static double factorialLog(final int n) {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER,
                                           n);
        }
        if (n < 21) {
            return FastMath.log(factorial(n));
        }
        double logSum = 0;
        for (int i = 2; i <= n; i++) {
            logSum += FastMath.log(i);
        }
        return logSum;
    }

    /**
     * <p>
     * Gets the greatest common divisor of the absolute value of two numbers,
     * using the "binary gcd" method which avoids division and modulo
     * operations. See Knuth 4.5.2 algorithm B. This algorithm is due to Josef
     * Stein (1961).
     * </p>
     * Special cases:
     * <ul>
     * <li>The invocations
     * {@code gcd(Integer.MIN_VALUE, Integer.MIN_VALUE)},
     * {@code gcd(Integer.MIN_VALUE, 0)} and
     * {@code gcd(0, Integer.MIN_VALUE)} throw an
     * {@code ArithmeticException}, because the result would be 2^31, which
     * is too large for an int value.</li>
     * <li>The result of {@code gcd(x, x)}, {@code gcd(0, x)} and
     * {@code gcd(x, 0)} is the absolute value of {@code x}, except
     * for the special cases above.
     * <li>The invocation {@code gcd(0, 0)} is the only one which returns
     * {@code 0}.</li>
     * </ul>
     *
     * @param p Number.
     * @param q Number.
     * @return the greatest common divisor, never negative.
     * @throws MathArithmeticException if the result cannot be represented as
     * a non-negative {@code int} value.
     * @since 1.1
     */
    public static int gcd(final int p, final int q) {
        int u = p;
        int v = q;
        if ((u == 0) || (v == 0)) {
            if ((u == Integer.MIN_VALUE) || (v == Integer.MIN_VALUE)) {
                throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS,
                                                  p, q);
            }
            return FastMath.abs(u) + FastMath.abs(v);
        }
        // keep u and v negative, as negative integers range down to
        // -2^31, while positive numbers can only be as large as 2^31-1
        // (i.e. we can't necessarily negate a negative number without
        // overflow)
        /* assert u!=0 && v!=0; */
        if (u > 0) {
            u = -u;
        } // make u negative
        if (v > 0) {
            v = -v;
        } // make v negative
        // B1. [Find power of 2]
        int k = 0;
        while ((u & 1) == 0 && (v & 1) == 0 && k < 31) { // while u and v are
                                                            // both even...
            u /= 2;
            v /= 2;
            k++; // cast out twos.
        }
        if (k == 31) {
            throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS,
                                              p, q);
        }
        // B2. Initialize: u and v have been divided by 2^k and at least
        // one is odd.
        int t = ((u & 1) == 1) ? v : -(u / 2)/* B3 */;
        // t negative: u was odd, v may be even (t replaces v)
        // t positive: u was even, v is odd (t replaces u)
        do {
            /* assert u<0 && v<0; */
            // B4/B3: cast out twos from t.
            while ((t & 1) == 0) { // while t is even..
                t /= 2; // cast out twos
            }
            // B5 [reset max(u,v)]
            if (t > 0) {
                u = -t;
            } else {
                v = t;
            }
            // B6/B3. at this point both u and v should be odd.
            t = (v - u) / 2;
            // |u| larger: t positive (replace u)
            // |v| larger: t negative (replace v)
        } while (t != 0);
        return -u * (1 << k); // gcd is u*2^k
    }

    /**
     * <p>
     * Gets the greatest common divisor of the absolute value of two numbers,
     * using the "binary gcd" method which avoids division and modulo
     * operations. See Knuth 4.5.2 algorithm B. This algorithm is due to Josef
     * Stein (1961).
     * </p>
     * Special cases:
     * <ul>
     * <li>The invocations
     * {@code gcd(Long.MIN_VALUE, Long.MIN_VALUE)},
     * {@code gcd(Long.MIN_VALUE, 0L)} and
     * {@code gcd(0L, Long.MIN_VALUE)} throw an
     * {@code ArithmeticException}, because the result would be 2^63, which
     * is too large for a long value.</li>
     * <li>The result of {@code gcd(x, x)}, {@code gcd(0L, x)} and
     * {@code gcd(x, 0L)} is the absolute value of {@code x}, except
     * for the special cases above.
     * <li>The invocation {@code gcd(0L, 0L)} is the only one which returns
     * {@code 0L}.</li>
     * </ul>
     *
     * @param p Number.
     * @param q Number.
     * @return the greatest common divisor, never negative.
     * @throws MathArithmeticException if the result cannot be represented as
     * a non-negative {@code long} value.
     * @since 2.1
     */
    public static long gcd(final long p, final long q) {
        long u = p;
        long v = q;
        if ((u == 0) || (v == 0)) {
            if ((u == Long.MIN_VALUE) || (v == Long.MIN_VALUE)){
                throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_64_BITS,
                                                  p, q);
            }
            return FastMath.abs(u) + FastMath.abs(v);
        }
        // keep u and v negative, as negative integers range down to
        // -2^63, while positive numbers can only be as large as 2^63-1
        // (i.e. we can't necessarily negate a negative number without
        // overflow)
        /* assert u!=0 && v!=0; */
        if (u > 0) {
            u = -u;
        } // make u negative
        if (v > 0) {
            v = -v;
        } // make v negative
        // B1. [Find power of 2]
        int k = 0;
        while ((u & 1) == 0 && (v & 1) == 0 && k < 63) { // while u and v are
                                                            // both even...
            u /= 2;
            v /= 2;
            k++; // cast out twos.
        }
        if (k == 63) {
            throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_64_BITS,
                                              p, q);
        }
        // B2. Initialize: u and v have been divided by 2^k and at least
        // one is odd.
        long t = ((u & 1) == 1) ? v : -(u / 2)/* B3 */;
        // t negative: u was odd, v may be even (t replaces v)
        // t positive: u was even, v is odd (t replaces u)
        do {
            /* assert u<0 && v<0; */
            // B4/B3: cast out twos from t.
            while ((t & 1) == 0) { // while t is even..
                t /= 2; // cast out twos
            }
            // B5 [reset max(u,v)]
            if (t > 0) {
                u = -t;
            } else {
                v = t;
            }
            // B6/B3. at this point both u and v should be odd.
            t = (v - u) / 2;
            // |u| larger: t positive (replace u)
            // |v| larger: t negative (replace v)
        } while (t != 0);
        return -u * (1L << k); // gcd is u*2^k
    }

    /**
     * Returns an integer hash code representing the given double value.
     *
     * @param value the value to be hashed
     * @return the hash code
     */
    public static int hash(double value) {
        return new Double(value).hashCode();
    }

    /**
     * Returns an integer hash code representing the given double array.
     *
     * @param value the value to be hashed (may be null)
     * @return the hash code
     * @since 1.2
     */
    public static int hash(double[] value) {
        return Arrays.hashCode(value);
    }

    /**
     * For a byte value x, this method returns (byte)(+1) if x >= 0 and
     * (byte)(-1) if x < 0.
     *
     * @param x the value, a byte
     * @return (byte)(+1) or (byte)(-1), depending on the sign of x
     */
    public static byte indicator(final byte x) {
        return (x >= ZB) ? PB : NB;
    }

    /**
     * For a double precision value x, this method returns +1.0 if x >= 0 and
     * -1.0 if x < 0. Returns {@code NaN} if {@code x} is
     * {@code NaN}.
     *
     * @param x the value, a double
     * @return +1.0 or -1.0, depending on the sign of x
     */
    public static double indicator(final double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return (x >= 0.0) ? 1.0 : -1.0;
    }

    /**
     * For a float value x, this method returns +1.0F if x >= 0 and -1.0F if x <
     * 0. Returns {@code NaN} if {@code x} is {@code NaN}.
     *
     * @param x the value, a float
     * @return +1.0F or -1.0F, depending on the sign of x
     */
    public static float indicator(final float x) {
        if (Float.isNaN(x)) {
            return Float.NaN;
        }
        return (x >= 0.0F) ? 1.0F : -1.0F;
    }

    /**
     * For an int value x, this method returns +1 if x >= 0 and -1 if x < 0.
     *
     * @param x the value, an int
     * @return +1 or -1, depending on the sign of x
     */
    public static int indicator(final int x) {
        return (x >= 0) ? 1 : -1;
    }

    /**
     * For a long value x, this method returns +1L if x >= 0 and -1L if x < 0.
     *
     * @param x the value, a long
     * @return +1L or -1L, depending on the sign of x
     */
    public static long indicator(final long x) {
        return (x >= 0L) ? 1L : -1L;
    }

    /**
     * For a short value x, this method returns (short)(+1) if x >= 0 and
     * (short)(-1) if x < 0.
     *
     * @param x the value, a short
     * @return (short)(+1) or (short)(-1), depending on the sign of x
     */
    public static short indicator(final short x) {
        return (x >= ZS) ? PS : NS;
    }

    /**
     * <p>
     * Returns the least common multiple of the absolute value of two numbers,
     * using the formula {@code lcm(a,b) = (a / gcd(a,b)) * b}.
     * </p>
     * Special cases:
     * <ul>
     * <li>The invocations {@code lcm(Integer.MIN_VALUE, n)} and
     * {@code lcm(n, Integer.MIN_VALUE)}, where {@code abs(n)} is a
     * power of 2, throw an {@code ArithmeticException}, because the result
     * would be 2^31, which is too large for an int value.</li>
     * <li>The result of {@code lcm(0, x)} and {@code lcm(x, 0)} is
     * {@code 0} for any {@code x}.
     * </ul>
     *
     * @param a Number.
     * @param b Number.
     * @return the least common multiple, never negative.
     * @throws MathArithmeticException if the result cannot be represented as
     * a non-negative {@code int} value.
     * @since 1.1
     */
    public static int lcm(int a, int b) {
        if (a == 0 || b == 0){
            return 0;
        }
        int lcm = FastMath.abs(mulAndCheck(a / gcd(a, b), b));
        if (lcm == Integer.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.LCM_OVERFLOW_32_BITS,
                                              a, b);
        }
        return lcm;
    }

    /**
     * <p>
     * Returns the least common multiple of the absolute value of two numbers,
     * using the formula {@code lcm(a,b) = (a / gcd(a,b)) * b}.
     * </p>
     * Special cases:
     * <ul>
     * <li>The invocations {@code lcm(Long.MIN_VALUE, n)} and
     * {@code lcm(n, Long.MIN_VALUE)}, where {@code abs(n)} is a
     * power of 2, throw an {@code ArithmeticException}, because the result
     * would be 2^63, which is too large for an int value.</li>
     * <li>The result of {@code lcm(0L, x)} and {@code lcm(x, 0L)} is
     * {@code 0L} for any {@code x}.
     * </ul>
     *
     * @param a Number.
     * @param b Number.
     * @return the least common multiple, never negative.
     * @throws MathArithmeticException if the result cannot be represented
     * as a non-negative {@code long} value.
     * @since 2.1
     */
    public static long lcm(long a, long b) {
        if (a == 0 || b == 0){
            return 0;
        }
        long lcm = FastMath.abs(mulAndCheck(a / gcd(a, b), b));
        if (lcm == Long.MIN_VALUE){
            throw new MathArithmeticException(LocalizedFormats.LCM_OVERFLOW_64_BITS,
                                              a, b);
        }
        return lcm;
    }

    /**
     * <p>Returns the
     * <a href="http://mathworld.wolfram.com/Logarithm.html">logarithm</a>
     * for base {@code b} of {@code x}.
     * </p>
     * <p>Returns {@code NaN} if either argument is negative. If
     * {@code base} is 0 and {@code x} is positive, 0 is returned.
     * If {@code base} is positive and {@code x} is 0,
     * {@code Double.NEGATIVE_INFINITY} is returned.  If both arguments
     * are 0, the result is {@code NaN}.</p>
     *
     * @param base the base of the logarithm, must be greater than 0
     * @param x argument, must be greater than 0
     * @return the value of the logarithm - the number y such that base^y = x.
     * @since 1.2
     */
    public static double log(double base, double x) {
        return FastMath.log(x)/FastMath.log(base);
    }

    /**
     * Multiply two integers, checking for overflow.
     *
     * @param x Factor.
     * @param y Factor.
     * @return the product {@code x * y}.
     * @throws MathArithmeticException if the result can not be
     * represented as an {@code int}.
     * @since 1.1
     */
    public static int mulAndCheck(int x, int y) {
        long m = ((long)x) * ((long)y);
        if (m < Integer.MIN_VALUE || m > Integer.MAX_VALUE) {
            throw new MathArithmeticException();
        }
        return (int)m;
    }

    /**
     * Multiply two long integers, checking for overflow.
     *
     * @param a Factor.
     * @param b Factor.
     * @return the product {@code a * b}.
     * @throws MathArithmeticException if the result can not be represented
     * as a {@code long}.
     * @since 1.2
     */
    public static long mulAndCheck(long a, long b) {
        long ret;
        if (a > b) {
            // use symmetry to reduce boundary cases
            ret = mulAndCheck(b, a);
        } else {
            if (a < 0) {
                if (b < 0) {
                    // check for positive overflow with negative a, negative b
                    if (a >= Long.MAX_VALUE / b) {
                        ret = a * b;
                    } else {
                        throw new MathArithmeticException();
                    }
                } else if (b > 0) {
                    // check for negative overflow with negative a, positive b
                    if (Long.MIN_VALUE / b <= a) {
                        ret = a * b;
                    } else {
                        throw new MathArithmeticException();

                    }
                } else {
                    // assert b == 0
                    ret = 0;
                }
            } else if (a > 0) {
                // assert a > 0
                // assert b > 0

                // check for positive overflow with positive a, positive b
                if (a <= Long.MAX_VALUE / b) {
                    ret = a * b;
                } else {
                    throw new MathArithmeticException();
                }
            } else {
                // assert a == 0
                ret = 0;
            }
        }
        return ret;
    }

    /**
     * Normalize an angle in a 2&pi wide interval around a center value.
     * <p>This method has three main uses:</p>
     * <ul>
     *   <li>normalize an angle between 0 and 2&pi;:<br/>
     *       {@code a = MathUtils.normalizeAngle(a, FastMath.PI);}</li>
     *   <li>normalize an angle between -&pi; and +&pi;<br/>
     *       {@code a = MathUtils.normalizeAngle(a, 0.0);}</li>
     *   <li>compute the angle between two defining angular positions:<br>
     *       {@code angle = MathUtils.normalizeAngle(end, start) - start;}</li>
     * </ul>
     * <p>Note that due to numerical accuracy and since &pi; cannot be represented
     * exactly, the result interval is <em>closed</em>, it cannot be half-closed
     * as would be more satisfactory in a purely mathematical view.</p>
     * @param a angle to normalize
     * @param center center of the desired 2&pi; interval for the result
     * @return a-2k&pi; with integer k and center-&pi; &lt;= a-2k&pi; &lt;= center+&pi;
     * @since 1.2
     */
     public static double normalizeAngle(double a, double center) {
         return a - TWO_PI * FastMath.floor((a + FastMath.PI - center) / TWO_PI);
     }

    /**
     * <p>Reduce {@code |a - offset|} to the primary interval
     * {@code [0, |period|)}.</p>
     *
     * <p>Specifically, the value returned is <br/>
     * {@code a - |period| * floor((a - offset) / |period|) - offset}.</p>
     *
     * <p>If any of the parameters are {@code NaN} or infinite, the result is
     * {@code NaN}.</p>
     *
     * @param a Value to reduce.
     * @param period Period.
     * @param offset Value that will be mapped to {@code 0}.
     * @return the value, within the interval {@code [0 |period|)},
     * that corresponds to {@code a}.
     */
    public static double reduce(double a,
                                double period,
                                double offset) {
        final double p = FastMath.abs(period);
        return a - p * FastMath.floor((a - offset) / p) - offset;
    }

     /**
      * <p>Normalizes an array to make it sum to a specified value.
      * Returns the result of the transformation <pre>
      *    x |-> x * normalizedSum / sum
      * </pre>
      * applied to each non-NaN element x of the input array, where sum is the
      * sum of the non-NaN entries in the input array.</p>
      *
      * <p>Throws IllegalArgumentException if {@code normalizedSum} is infinite
      * or NaN and ArithmeticException if the input array contains any infinite elements
      * or sums to 0</p>
      *
      * <p>Ignores (i.e., copies unchanged to the output array) NaNs in the input array.</p>
      *
      * @param values input array to be normalized
      * @param normalizedSum target sum for the normalized array
      * @return normalized array
      * @throws MathArithmeticException if the input array contains infinite elements or sums to zero
      * @throws MathIllegalArgumentException if the target sum is infinite or NaN
      * @since 2.1
      */
     public static double[] normalizeArray(double[] values, double normalizedSum) {
         if (Double.isInfinite(normalizedSum)) {
             throw new MathIllegalArgumentException(LocalizedFormats.NORMALIZE_INFINITE);
         }
         if (Double.isNaN(normalizedSum)) {
             throw new MathIllegalArgumentException(LocalizedFormats.NORMALIZE_NAN);
         }
         double sum = 0d;
         final int len = values.length;
         double[] out = new double[len];
         for (int i = 0; i < len; i++) {
             if (Double.isInfinite(values[i])) {
                 throw new MathIllegalArgumentException(LocalizedFormats.INFINITE_ARRAY_ELEMENT, values[i], i);
             }
             if (!Double.isNaN(values[i])) {
                 sum += values[i];
             }
         }
         if (sum == 0) {
             throw new MathArithmeticException(LocalizedFormats.ARRAY_SUMS_TO_ZERO);
         }
         for (int i = 0; i < len; i++) {
             if (Double.isNaN(values[i])) {
                 out[i] = Double.NaN;
             } else {
                 out[i] = values[i] * normalizedSum / sum;
             }
         }
         return out;
     }

    /**
     * Round the given value to the specified number of decimal places. The
     * value is rounded using the {@link BigDecimal#ROUND_HALF_UP} method.
     *
     * @param x the value to round.
     * @param scale the number of digits to the right of the decimal point.
     * @return the rounded value.
     * @since 1.1
     */
    public static double round(double x, int scale) {
        return round(x, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * <p>Round the given value to the specified number of decimal places. The
     * value is rounded using the given method which is any method defined in
     * {@link BigDecimal}.</p>
     *
     * <p>If {@code x} is infinite or NaN, then the value of {@code x} is
     * returned unchanged, regardless of the other parameters.</p>
     *
     * @param x the value to round.
     * @param scale the number of digits to the right of the decimal point.
     * @param roundingMethod the rounding method as defined in
     *        {@link BigDecimal}.
     * @return the rounded value.
     * @throws ArithmeticException if roundingMethod==ROUND_UNNECESSARY and the
     * specified scaling operation would require rounding.
     * @throws IllegalArgumentException if roundingMethod does not represent a
     * valid rounding mode.
     * @since 1.1
     */
    public static double round(double x, int scale, int roundingMethod) {
        try {
            return (new BigDecimal
                   (Double.toString(x))
                   .setScale(scale, roundingMethod))
                   .doubleValue();
        } catch (NumberFormatException ex) {
            if (Double.isInfinite(x)) {
                return x;
            } else {
                return Double.NaN;
            }
        }
    }

    /**
     * Round the given value to the specified number of decimal places. The
     * value is rounded using the {@link BigDecimal#ROUND_HALF_UP} method.
     *
     * @param x the value to round.
     * @param scale the number of digits to the right of the decimal point.
     * @return the rounded value.
     * @since 1.1
     */
    public static float round(float x, int scale) {
        return round(x, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Round the given value to the specified number of decimal places. The
     * value is rounded using the given method which is any method defined in
     * {@link BigDecimal}.
     *
     * @param x the value to round.
     * @param scale the number of digits to the right of the decimal point.
     * @param roundingMethod the rounding method as defined in
     *        {@link BigDecimal}.
     * @return the rounded value.
     * @since 1.1
     */
    public static float round(float x, int scale, int roundingMethod) {
        float sign = indicator(x);
        float factor = (float)FastMath.pow(10.0f, scale) * sign;
        return (float)roundUnscaled(x * factor, sign, roundingMethod) / factor;
    }

    /**
     * Round the given non-negative value to the "nearest" integer. Nearest is
     * determined by the rounding method specified. Rounding methods are defined
     * in {@link BigDecimal}.
     *
     * @param unscaled Value to round.
     * @param sign Sign of the original, scaled value.
     * @param roundingMethod Rounding method, as defined in {@link BigDecimal}.
     * @return the rounded value.
     * @throws MathIllegalArgumentException if {@code roundingMethod} is not a valid rounding method.
     * @since 1.1
     */
    private static double roundUnscaled(double unscaled,
                                        double sign,
                                        int roundingMethod) {
        switch (roundingMethod) {
        case BigDecimal.ROUND_CEILING :
            if (sign == -1) {
                unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
            } else {
                unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
            }
            break;
        case BigDecimal.ROUND_DOWN :
            unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
            break;
        case BigDecimal.ROUND_FLOOR :
            if (sign == -1) {
                unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
            } else {
                unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
            }
            break;
        case BigDecimal.ROUND_HALF_DOWN : {
            unscaled = FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY);
            double fraction = unscaled - FastMath.floor(unscaled);
            if (fraction > 0.5) {
                unscaled = FastMath.ceil(unscaled);
            } else {
                unscaled = FastMath.floor(unscaled);
            }
            break;
        }
        case BigDecimal.ROUND_HALF_EVEN : {
            double fraction = unscaled - FastMath.floor(unscaled);
            if (fraction > 0.5) {
                unscaled = FastMath.ceil(unscaled);
            } else if (fraction < 0.5) {
                unscaled = FastMath.floor(unscaled);
            } else {
                // The following equality test is intentional and needed for rounding purposes
                if (FastMath.floor(unscaled) / 2.0 == FastMath.floor(Math
                    .floor(unscaled) / 2.0)) { // even
                    unscaled = FastMath.floor(unscaled);
                } else { // odd
                    unscaled = FastMath.ceil(unscaled);
                }
            }
            break;
        }
        case BigDecimal.ROUND_HALF_UP : {
            unscaled = FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY);
            double fraction = unscaled - FastMath.floor(unscaled);
            if (fraction >= 0.5) {
                unscaled = FastMath.ceil(unscaled);
            } else {
                unscaled = FastMath.floor(unscaled);
            }
            break;
        }
        case BigDecimal.ROUND_UNNECESSARY :
            if (unscaled != FastMath.floor(unscaled)) {
                throw new MathArithmeticException();
            }
            break;
        case BigDecimal.ROUND_UP :
            unscaled = FastMath.ceil(FastMath.nextAfter(unscaled,  Double.POSITIVE_INFINITY));
            break;
        default :
            throw new MathIllegalArgumentException(LocalizedFormats.INVALID_ROUNDING_METHOD,
                                                   roundingMethod,
                                                   "ROUND_CEILING", BigDecimal.ROUND_CEILING,
                                                   "ROUND_DOWN", BigDecimal.ROUND_DOWN,
                                                   "ROUND_FLOOR", BigDecimal.ROUND_FLOOR,
                                                   "ROUND_HALF_DOWN", BigDecimal.ROUND_HALF_DOWN,
                                                   "ROUND_HALF_EVEN", BigDecimal.ROUND_HALF_EVEN,
                                                   "ROUND_HALF_UP", BigDecimal.ROUND_HALF_UP,
                                                   "ROUND_UNNECESSARY", BigDecimal.ROUND_UNNECESSARY,
                                                   "ROUND_UP", BigDecimal.ROUND_UP);
        }
        return unscaled;
    }

    /**
     * Returns the <a href="http://mathworld.wolfram.com/Sign.html"> sign</a>
     * for byte value {@code x}.
     * <p>
     * For a byte value x, this method returns (byte)(+1) if x > 0, (byte)(0) if
     * x = 0, and (byte)(-1) if x < 0.</p>
     *
     * @param x the value, a byte
     * @return (byte)(+1), (byte)(0), or (byte)(-1), depending on the sign of x
     */
    public static byte sign(final byte x) {
        return (x == ZB) ? ZB : (x > ZB) ? PB : NB;
    }

    /**
     * Returns the <a href="http://mathworld.wolfram.com/Sign.html"> sign</a>
     * for double precision {@code x}.
     * <p>
     * For a double value {@code x}, this method returns
     * {@code +1.0} if {@code x > 0}, {@code 0.0} if
     * {@code x = 0.0}, and {@code -1.0} if {@code x < 0}.
     * Returns {@code NaN} if {@code x} is {@code NaN}.</p>
     *
     * @param x the value, a double
     * @return +1.0, 0.0, or -1.0, depending on the sign of x
     */
    public static double sign(final double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return (x == 0.0) ? 0.0 : (x > 0.0) ? 1.0 : -1.0;
    }

    /**
     * Returns the <a href="http://mathworld.wolfram.com/Sign.html"> sign</a>
     * for float value {@code x}.
     * <p>
     * For a float value x, this method returns +1.0F if x > 0, 0.0F if x =
     * 0.0F, and -1.0F if x < 0. Returns {@code NaN} if {@code x}
     * is {@code NaN}.</p>
     *
     * @param x the value, a float
     * @return +1.0F, 0.0F, or -1.0F, depending on the sign of x
     */
    public static float sign(final float x) {
        if (Float.isNaN(x)) {
            return Float.NaN;
        }
        return (x == 0.0F) ? 0.0F : (x > 0.0F) ? 1.0F : -1.0F;
    }

    /**
     * Returns the <a href="http://mathworld.wolfram.com/Sign.html"> sign</a>
     * for int value {@code x}.
     * <p>
     * For an int value x, this method returns +1 if x > 0, 0 if x = 0, and -1
     * if x < 0.</p>
     *
     * @param x the value, an int
     * @return +1, 0, or -1, depending on the sign of x
     */
    public static int sign(final int x) {
        return (x == 0) ? 0 : (x > 0) ? 1 : -1;
    }

    /**
     * Returns the <a href="http://mathworld.wolfram.com/Sign.html"> sign</a>
     * for long value {@code x}.
     * <p>
     * For a long value x, this method returns +1L if x > 0, 0L if x = 0, and
     * -1L if x < 0.</p>
     *
     * @param x the value, a long
     * @return +1L, 0L, or -1L, depending on the sign of x
     */
    public static long sign(final long x) {
        return (x == 0L) ? 0L : (x > 0L) ? 1L : -1L;
    }

    /**
     * Compute the <a href="http://mathworld.wolfram.com/Sign.html">sign</a>
     * of the argument.
     *
     * @param x the value, a short
     * @return 1 if {@code x > 0}, 0 if {@code x == 0}, and -1 if {@code x < 0}.
     */
    public static short sign(final short x) {
        return (x == ZS) ? ZS : (x > ZS) ? PS : NS;
    }

    /**
     * Compute the <a href="http://mathworld.wolfram.com/HyperbolicSine.html">
     * hyperbolic sine</a> of the argument.
     *
     * @param x Value for which to find the hyperbolic sine.
     * @return hyperbolic sine of {@code x}.
     */
    public static double sinh(double x) {
        return (FastMath.exp(x) - FastMath.exp(-x)) / 2.0;
    }

    /**
     * Subtract two integers, checking for overflow.
     *
     * @param x Minuend.
     * @param y Subtrahend.
     * @return the difference {@code x - y}.
     * @throws MathArithmeticException if the result can not be represented
     * as an {@code int}.
     * @since 1.1
     */
    public static int subAndCheck(int x, int y) {
        long s = (long)x - (long)y;
        if (s < Integer.MIN_VALUE || s > Integer.MAX_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, x, y);
        }
        return (int)s;
    }

    /**
     * Subtract two long integers, checking for overflow.
     *
     * @param a Value.
     * @param b Value.
     * @return the difference {@code a - b}.
     * @throws MathArithmeticException if the result can not be represented as a
     * {@code long}.
     * @since 1.2
     */
    public static long subAndCheck(long a, long b) {
        long ret;
        if (b == Long.MIN_VALUE) {
            if (a < 0) {
                ret = a - b;
            } else {
                throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, a, -b);
            }
        } else {
            // use additive inverse
            ret = addAndCheck(a, -b, LocalizedFormats.OVERFLOW_IN_ADDITION);
        }
        return ret;
    }

    /**
     * Raise an int to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if {@code e < 0}.
     */
    public static int pow(final int k, int e) {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }

        int result = 1;
        int k2p    = k;
        while (e != 0) {
            if ((e & 0x1) != 0) {
                result *= k2p;
            }
            k2p *= k2p;
            e = e >> 1;
        }

        return result;
    }

    /**
     * Raise an int to a long power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if {@code e < 0}.
     */
    public static int pow(final int k, long e) {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }

        int result = 1;
        int k2p    = k;
        while (e != 0) {
            if ((e & 0x1) != 0) {
                result *= k2p;
            }
            k2p *= k2p;
            e = e >> 1;
        }

        return result;
    }

    /**
     * Raise a long to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if {@code e < 0}.
     */
    public static long pow(final long k, int e) {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }

        long result = 1l;
        long k2p    = k;
        while (e != 0) {
            if ((e & 0x1) != 0) {
                result *= k2p;
            }
            k2p *= k2p;
            e = e >> 1;
        }

        return result;
    }

    /**
     * Raise a long to a long power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if {@code e < 0}.
     */
    public static long pow(final long k, long e) {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }

        long result = 1l;
        long k2p    = k;
        while (e != 0) {
            if ((e & 0x1) != 0) {
                result *= k2p;
            }
            k2p *= k2p;
            e = e >> 1;
        }

        return result;
    }

    /**
     * Raise a BigInteger to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if {@code e < 0}.
     */
    public static BigInteger pow(final BigInteger k, int e) {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }

        return k.pow(e);
    }

    /**
     * Raise a BigInteger to a long power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if {@code e < 0}.
     */
    public static BigInteger pow(final BigInteger k, long e) {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }

        BigInteger result = BigInteger.ONE;
        BigInteger k2p    = k;
        while (e != 0) {
            if ((e & 0x1) != 0) {
                result = result.multiply(k2p);
            }
            k2p = k2p.multiply(k2p);
            e = e >> 1;
        }

        return result;

    }

    /**
     * Raise a BigInteger to a BigInteger power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if {@code e < 0}.
     */
    public static BigInteger pow(final BigInteger k, BigInteger e) {
        if (e.compareTo(BigInteger.ZERO) < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }

        BigInteger result = BigInteger.ONE;
        BigInteger k2p    = k;
        while (!BigInteger.ZERO.equals(e)) {
            if (e.testBit(0)) {
                result = result.multiply(k2p);
            }
            k2p = k2p.multiply(k2p);
            e = e.shiftRight(1);
        }

        return result;
    }

    /**
     * Calculates the L<sub>1</sub> (sum of abs) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub>1</sub> distance between the two points
     */
    public static double distance1(double[] p1, double[] p2) {
        double sum = 0;
        for (int i = 0; i < p1.length; i++) {
            sum += FastMath.abs(p1[i] - p2[i]);
        }
        return sum;
    }

    /**
     * Calculates the L<sub>1</sub> (sum of abs) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub>1</sub> distance between the two points
     */
    public static int distance1(int[] p1, int[] p2) {
      int sum = 0;
      for (int i = 0; i < p1.length; i++) {
          sum += FastMath.abs(p1[i] - p2[i]);
      }
      return sum;
    }

    /**
     * Calculates the L<sub>2</sub> (Euclidean) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub>2</sub> distance between the two points
     */
    public static double distance(double[] p1, double[] p2) {
        double sum = 0;
        for (int i = 0; i < p1.length; i++) {
            final double dp = p1[i] - p2[i];
            sum += dp * dp;
        }
        return FastMath.sqrt(sum);
    }

    /**
     * Calculates the L<sub>2</sub> (Euclidean) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub>2</sub> distance between the two points
     */
    public static double distance(int[] p1, int[] p2) {
      double sum = 0;
      for (int i = 0; i < p1.length; i++) {
          final double dp = p1[i] - p2[i];
          sum += dp * dp;
      }
      return FastMath.sqrt(sum);
    }

    /**
     * Calculates the L<sub>&infin;</sub> (max of abs) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub>&infin;</sub> distance between the two points
     */
    public static double distanceInf(double[] p1, double[] p2) {
        double max = 0;
        for (int i = 0; i < p1.length; i++) {
            max = FastMath.max(max, FastMath.abs(p1[i] - p2[i]));
        }
        return max;
    }

    /**
     * Calculates the L<sub>&infin;</sub> (max of abs) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub>&infin;</sub> distance between the two points
     */
    public static int distanceInf(int[] p1, int[] p2) {
        int max = 0;
        for (int i = 0; i < p1.length; i++) {
            max = FastMath.max(max, FastMath.abs(p1[i] - p2[i]));
        }
        return max;
    }

    /**
     * Specification of ordering direction.
     */
    public static enum OrderDirection {
        /** Constant for increasing direction. */
        INCREASING,
        /** Constant for decreasing direction. */
        DECREASING
    }

    /**
     * Check that the given array is sorted.
     *
     * @param val Values.
     * @param dir Ordering direction.
     * @param strict Whether the order should be strict.
     * @param abort Whether to throw an exception if the check fails.
     * @return {@code true} if the array is sorted.
     * @throws NonMonotonousSequenceException if the array is not sorted
     * and {@code abort} is {@code true}.
     */
    public static boolean checkOrder(double[] val, OrderDirection dir,
                                     boolean strict, boolean abort) {
        double previous = val[0];
        boolean ok = true;

        int max = val.length;
        for (int i = 1; i < max; i++) {
            switch (dir) {
            case INCREASING:
                if (strict) {
                    if (val[i] <= previous) {
                        ok = false;
                    }
                } else {
                    if (val[i] < previous) {
                        ok = false;
                    }
                }
                break;
            case DECREASING:
                if (strict) {
                    if (val[i] >= previous) {
                        ok = false;
                    }
                } else {
                    if (val[i] > previous) {
                        ok = false;
                    }
                }
                break;
            default:
                // Should never happen.
                throw new IllegalArgumentException();
            }

            if (!ok &&
                abort) {
                throw new NonMonotonousSequenceException(val[i], previous, i, dir, strict);
            }
            previous = val[i];
        }

        return ok;
    }

    /**
     * Check that the given array is sorted.
     *
     * @param val Values.
     * @param dir Ordering direction.
     * @param strict Whether the order should be strict.
     * @throws NonMonotonousSequenceException if the array is not sorted.
     * @since 2.2
     */
    public static void checkOrder(double[] val, OrderDirection dir,
                                  boolean strict) {
        checkOrder(val, dir, strict, true);
    }

    /**
     * Check that the given array is sorted in strictly increasing order.
     *
     * @param val Values.
     * @throws NonMonotonousSequenceException if the array is not sorted.
     * @since 2.2
     */
    public static void checkOrder(double[] val) {
        checkOrder(val, OrderDirection.INCREASING, true);
    }

    /**
     * Check that the argument is a real number.
     *
     * @param x Argument.
     * @throws NotFiniteNumberException if {@code x} is not a
     * finite real number.
     */
    public static void checkFinite(final double x) {
        if (Double.isInfinite(x) || Double.isNaN(x)) {
            throw new NotFiniteNumberException(x);
        }
    }

    /**
     * Check that all the elements are real number.
     *
     * @param val Arguments.
     * @throws NotFiniteNumberException if any values of the array is not a
     * finite real number.
     */
    public static void checkFinite(final double[] val) {
        for (int i = 0; i < val.length; i++) {
            final double x = val[i];
            if (Double.isInfinite(x) || Double.isNaN(x)) {
                throw new NotFiniteNumberException(LocalizedFormats.ARRAY_ELEMENT, x, i);
            }
        }
    }

    /**
     * Returns the Cartesian norm (2-norm), handling both overflow and underflow.
     * Translation of the minpack enorm subroutine.
     *
     * The redistribution policy for MINPACK is available <a
     * href="http://www.netlib.org/minpack/disclaimer">here</a>, for convenience, it
     * is reproduced below.</p>
     *
     * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
     * <tr><td>
     *    Minpack Copyright Notice (1999) University of Chicago.
     *    All rights reserved
     * </td></tr>
     * <tr><td>
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions
     * are met:
     * <ol>
     *  <li>Redistributions of source code must retain the above copyright
     *      notice, this list of conditions and the following disclaimer.</li>
     * <li>Redistributions in binary form must reproduce the above
     *     copyright notice, this list of conditions and the following
     *     disclaimer in the documentation and/or other materials provided
     *     with the distribution.</li>
     * <li>The end-user documentation included with the redistribution, if any,
     *     must include the following acknowledgment:
     *     {@code This product includes software developed by the University of
     *           Chicago, as Operator of Argonne National Laboratory.}
     *     Alternately, this acknowledgment may appear in the software itself,
     *     if and wherever such third-party acknowledgments normally appear.</li>
     * <li><strong>WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED "AS IS"
     *     WITHOUT WARRANTY OF ANY KIND. THE COPYRIGHT HOLDER, THE
     *     UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, AND
     *     THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES, EXPRESS OR
     *     IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES
     *     OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE
     *     OR NON-INFRINGEMENT, (2) DO NOT ASSUME ANY LEGAL LIABILITY
     *     OR RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR
     *     USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE OF
     *     THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4)
     *     DO NOT WARRANT THAT THE SOFTWARE WILL FUNCTION
     *     UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT ANY ERRORS WILL
     *     BE CORRECTED.</strong></li>
     * <li><strong>LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT
     *     HOLDER, THE UNITED STATES, THE UNITED STATES DEPARTMENT OF
     *     ENERGY, OR THEIR EMPLOYEES: BE LIABLE FOR ANY INDIRECT,
     *     INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OF
     *     ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF
     *     PROFITS OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER
     *     SUCH LIABILITY IS ASSERTED ON THE BASIS OF CONTRACT, TORT
     *     (INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR OTHERWISE,
     *     EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE
     *     POSSIBILITY OF SUCH LOSS OR DAMAGES.</strong></li>
     * <ol></td></tr>
     * </table>
     *
     * @param v vector of doubles
     * @return the 2-norm of the vector
     * @since 2.2
     */
    public static double safeNorm(double[] v) {
        double rdwarf = 3.834e-20;
        double rgiant = 1.304e+19;
        double s1 = 0;
        double s2 = 0;
        double s3 = 0;
        double x1max = 0;
        double x3max = 0;
        double floatn = (double) v.length;
        double agiant = rgiant / floatn;
        for (int i = 0; i < v.length; i++) {
            double xabs = Math.abs(v[i]);
            if (xabs < rdwarf || xabs > agiant) {
                if (xabs > rdwarf) {
                    if (xabs > x1max) {
                        double r = x1max / xabs;
                        s1= 1 + s1 * r * r;
                        x1max = xabs;
                    } else {
                        double r = xabs / x1max;
                        s1 += r * r;
                    }
                } else {
                    if (xabs > x3max) {
                        double r = x3max / xabs;
                        s3= 1 + s3 * r * r;
                        x3max = xabs;
                    } else {
                        if (xabs != 0) {
                            double r = xabs / x3max;
                            s3 += r * r;
                        }
                    }
                }
            } else {
                s2 += xabs * xabs;
            }
        }
        double norm;
        if (s1 != 0) {
            norm = x1max * Math.sqrt(s1 + (s2 / x1max) / x1max);
        } else {
            if (s2 == 0) {
                norm = x3max * Math.sqrt(s3);
            } else {
                if (s2 >= x3max) {
                    norm = Math.sqrt(s2 * (1 + (x3max / s2) * (x3max * s3)));
                } else {
                    norm = Math.sqrt(x3max * ((s2 / x3max) + (x3max * s3)));
                }
            }
        }
        return norm;
    }

    /**
     * Sort an array in increasing order, performing the same reordering of
     * entries on other arrays.
     *
     * @param x Array to be sorted.
     * @param yList Set of arrays whose permutations of entries must follow
     * those performed on {@code x}.
     * @throws DimensionMismatchException if any {@code y} has not the same
     * size as {@code x}.
     */
    public static void sortInPlace(double[] x,
                                   double[] ... yList) {
        sortInPlace(x, OrderDirection.INCREASING, yList);
    }

    /**
     * Sort an array, performing the same reordering of entries on other arrays.
     *
     * @param x Array to be sorted.
     * @param dir Order direction.
     * @param yList Set of arrays whose permutations of entries must follow
     * those performed on {@code x}.
     * @throws DimensionMismatchException if any {@code y} has not the same
     * size as {@code x}.
     */
    public static void sortInPlace(double[] x,
                                   final OrderDirection dir,
                                   double[] ... yList) {
        if (x == null ||
            yList == null) {
            throw new NullArgumentException();
        }

        final int len = x.length;
        final List<Pair<Double, double[]>> list
            = new ArrayList<Pair<Double, double[]>>(len);

        final int yListLen = yList.length;
        for (int i = 0; i < len; i++) {
            final double[] yValues = new double[yListLen];
            for (int j = 0; j < yListLen; j++) {
                double[] y = yList[j];
                if (y.length != len) {
                    throw new DimensionMismatchException(y.length, len);
                }
                yValues[j] = y[i];
            }
            list.add(new Pair<Double, double[]>(x[i], yValues));
        }

        final Comparator<Pair<Double, double[]>> comp
            = new Comparator<Pair<Double, double[]>>() {
            public int compare(Pair<Double, double[]> o1,
                               Pair<Double, double[]> o2) {
                int val;
                switch (dir) {
                case INCREASING:
                    val = o1.getKey().compareTo(o2.getKey());
                break;
                case DECREASING:
                    val = o2.getKey().compareTo(o1.getKey());
                break;
                default:
                    // Should never happen.
                    throw new IllegalArgumentException();
                }
                return val;
            }
        };

        Collections.sort(list, comp);

        for (int i = 0; i < len; i++) {
            final Pair<Double, double[]> e = list.get(i);
            x[i] = e.getKey();
            final double[] yValues = e.getValue();
            for (int j = 0; j < yListLen; j++) {
                yList[j][i] = yValues[j];
            }
        }
    }

    /**
     * Creates a copy of the {@code source} array.
     *
     * @param source Array to be copied.
     * @return the copied array.
     */
     public static int[] copyOf(int[] source) {
         return copyOf(source, source.length);
     }

    /**
     * Creates a copy of the {@code source} array.
     *
     * @param source Array to be copied.
     * @return the copied array.
     */
     public static double[] copyOf(double[] source) {
         return copyOf(source, source.length);
     }

    /**
     * Creates a copy of the {@code source} array.
     *
     * @param source Array to be copied.
     * @param len Number of entries to copy. If smaller then the source
     * length, the copy will be truncated, if larger it will padded with
     * zeroes.
     * @return the copied array.
     */
    public static int[] copyOf(int[] source, int len) {
         final int[] output = new int[len];
         System.arraycopy(source, 0, output, 0, FastMath.min(len, source.length));
         return output;
     }

    /**
     * Creates a copy of the {@code source} array.
     *
     * @param source Array to be copied.
     * @param len Number of entries to copy. If smaller then the source
     * length, the copy will be truncated, if larger it will padded with
     * zeroes.
     * @return the copied array.
     */
    public static double[] copyOf(double[] source, int len) {
         final double[] output = new double[len];
         System.arraycopy(source, 0, output, 0, FastMath.min(len, source.length));
         return output;
     }

    /**
     * Checks that an object is not null.
     *
     * @param o Object to be checked.
     * @param pattern Message pattern.
     * @param args Arguments to replace the placeholders in {@code pattern}.
     * @throws NullArgumentException if {@code o} is {@code null}.
     */
    public static void checkNotNull(Object o,
                                    Localizable pattern,
                                    Object ... args) {
        if (o == null) {
            throw new NullArgumentException(pattern, args);
        }
    }

    /**
     * Checks that an object is not null.
     *
     * @param o Object to be checked.
     * @throws NullArgumentException if {@code o} is {@code null}.
     */
    public static void checkNotNull(Object o)
        throws NullArgumentException {
        if (o == null) {
            throw new NullArgumentException();
        }
    }

    /**
     * Compute a linear combination accurately.
     * <p>
     * This method computes a<sub>1</sub>&times;b<sub>1</sub> +
     * a<sub>2</sub>&times;b<sub>2</sub> to high accuracy. It does
     * so by using specific multiplication and addition algorithms to
     * preserve accuracy and reduce cancellation effects. It is based
     * on the 2005 paper <a
     * href="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547">
     * Accurate Sum and Dot Product</a> by Takeshi Ogita,
     * Siegfried M. Rump, and Shin'ichi Oishi published in SIAM J. Sci. Comput.
     * </p>
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @return a<sub>1</sub>&times;b<sub>1</sub> +
     * a<sub>2</sub>&times;b<sub>2</sub>
     * @see #linearCombination(double, double, double, double, double, double)
     * @see #linearCombination(double, double, double, double, double, double, double, double)
     */
    public static double linearCombination(final double a1, final double b1,
                                           final double a2, final double b2) {

        // the code below is split in many additions/subtractions that may
        // appear redundant. However, they shoud NOT be simplified, as they
        // do use IEEE754 floating point arithmetic rouding properties.
        // as an example, the expression "ca1 - (ca1 - a1)" is NOT the same as "a1"
        // The variables naming conventions are that xyzHigh contains the most significant
        // bits of xyz and xyzLow contains its least significant bits. So theoretically
        // xyz is the sum xyzHigh + xyzLow, but in many cases below, this sum cannot
        // be represented in only one double precision number so we preserve two numbers
        // to hold it as long as we can, combining the high and low order bits together
        // only at the end, after cancellation may have occurred on high order bits

        // split a1 and b1 as two 26 bits numbers
        final double ca1        = SPLIT_FACTOR * a1;
        final double a1High     = ca1 - (ca1 - a1);
        final double a1Low      = a1 - a1High;
        final double cb1        = SPLIT_FACTOR * b1;
        final double b1High     = cb1 - (cb1 - b1);
        final double b1Low      = b1 - b1High;

        // accurate multiplication a1 * b1
        final double prod1High  = a1 * b1;
        final double prod1Low   = a1Low * b1Low - (((prod1High - a1High * b1High) - a1Low * b1High) - a1High * b1Low);

        // split a2 and b2 as two 26 bits numbers
        final double ca2        = SPLIT_FACTOR * a2;
        final double a2High     = ca2 - (ca2 - a2);
        final double a2Low      = a2 - a2High;
        final double cb2        = SPLIT_FACTOR * b2;
        final double b2High     = cb2 - (cb2 - b2);
        final double b2Low      = b2 - b2High;

        // accurate multiplication a2 * b2
        final double prod2High  = a2 * b2;
        final double prod2Low   = a2Low * b2Low - (((prod2High - a2High * b2High) - a2Low * b2High) - a2High * b2Low);

        // accurate addition a1 * b1 + a2 * b2
        final double s12High    = prod1High + prod2High;
        final double s12Prime   = s12High - prod2High;
        final double s12Low     = (prod2High - (s12High - s12Prime)) + (prod1High - s12Prime);

        // final rounding, s12 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        return s12High + (prod1Low + prod2Low + s12Low);

    }

    /**
     * Compute a linear combination accurately.
     * <p>
     * This method computes a<sub>1</sub>&times;b<sub>1</sub> +
     * a<sub>2</sub>&times;b<sub>2</sub> + a<sub>3</sub>&times;b<sub>3</sub>
     * to high accuracy. It does so by using specific multiplication and
     * addition algorithms to preserve accuracy and reduce cancellation effects.
     * It is based on the 2005 paper <a
     * href="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547">
     * Accurate Sum and Dot Product</a> by Takeshi Ogita,
     * Siegfried M. Rump, and Shin'ichi Oishi published in SIAM J. Sci. Comput.
     * </p>
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @param a3 first factor of the third term
     * @param b3 second factor of the third term
     * @return a<sub>1</sub>&times;b<sub>1</sub> +
     * a<sub>2</sub>&times;b<sub>2</sub> + a<sub>3</sub>&times;b<sub>3</sub>
     * @see #linearCombination(double, double, double, double)
     * @see #linearCombination(double, double, double, double, double, double, double, double)
     */
    public static double linearCombination(final double a1, final double b1,
                                           final double a2, final double b2,
                                           final double a3, final double b3) {

        // the code below is split in many additions/subtractions that may
        // appear redundant. However, they shoud NOT be simplified, as they
        // do use IEEE754 floating point arithmetic rouding properties.
        // as an example, the expression "ca1 - (ca1 - a1)" is NOT the same as "a1"
        // The variables naming conventions are that xyzHigh contains the most significant
        // bits of xyz and xyzLow contains its least significant bits. So theoretically
        // xyz is the sum xyzHigh + xyzLow, but in many cases below, this sum cannot
        // be represented in only one double precision number so we preserve two numbers
        // to hold it as long as we can, combining the high and low order bits together
        // only at the end, after cancellation may have occurred on high order bits

        // split a1 and b1 as two 26 bits numbers
        final double ca1        = SPLIT_FACTOR * a1;
        final double a1High     = ca1 - (ca1 - a1);
        final double a1Low      = a1 - a1High;
        final double cb1        = SPLIT_FACTOR * b1;
        final double b1High     = cb1 - (cb1 - b1);
        final double b1Low      = b1 - b1High;

        // accurate multiplication a1 * b1
        final double prod1High  = a1 * b1;
        final double prod1Low   = a1Low * b1Low - (((prod1High - a1High * b1High) - a1Low * b1High) - a1High * b1Low);

        // split a2 and b2 as two 26 bits numbers
        final double ca2        = SPLIT_FACTOR * a2;
        final double a2High     = ca2 - (ca2 - a2);
        final double a2Low      = a2 - a2High;
        final double cb2        = SPLIT_FACTOR * b2;
        final double b2High     = cb2 - (cb2 - b2);
        final double b2Low      = b2 - b2High;

        // accurate multiplication a2 * b2
        final double prod2High  = a2 * b2;
        final double prod2Low   = a2Low * b2Low - (((prod2High - a2High * b2High) - a2Low * b2High) - a2High * b2Low);

        // split a3 and b3 as two 26 bits numbers
        final double ca3        = SPLIT_FACTOR * a3;
        final double a3High     = ca3 - (ca3 - a3);
        final double a3Low      = a3 - a3High;
        final double cb3        = SPLIT_FACTOR * b3;
        final double b3High     = cb3 - (cb3 - b3);
        final double b3Low      = b3 - b3High;

        // accurate multiplication a3 * b3
        final double prod3High  = a3 * b3;
        final double prod3Low   = a3Low * b3Low - (((prod3High - a3High * b3High) - a3Low * b3High) - a3High * b3Low);

        // accurate addition a1 * b1 + a2 * b2
        final double s12High    = prod1High + prod2High;
        final double s12Prime   = s12High - prod2High;
        final double s12Low     = (prod2High - (s12High - s12Prime)) + (prod1High - s12Prime);

        // accurate addition a1 * b1 + a2 * b2 + a3 * b3
        final double s123High   = s12High + prod3High;
        final double s123Prime  = s123High - prod3High;
        final double s123Low    = (prod3High - (s123High - s123Prime)) + (s12High - s123Prime);

        // final rounding, s123 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        return s123High + (prod1Low + prod2Low + prod3Low + s12Low + s123Low);

    }

    /**
     * Compute a linear combination accurately.
     * <p>
     * This method computes a<sub>1</sub>&times;b<sub>1</sub> +
     * a<sub>2</sub>&times;b<sub>2</sub> + a<sub>3</sub>&times;b<sub>3</sub> +
     * a<sub>4</sub>&times;b<sub>4</sub>
     * to high accuracy. It does so by using specific multiplication and
     * addition algorithms to preserve accuracy and reduce cancellation effects.
     * It is based on the 2005 paper <a
     * href="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547">
     * Accurate Sum and Dot Product</a> by Takeshi Ogita,
     * Siegfried M. Rump, and Shin'ichi Oishi published in SIAM J. Sci. Comput.
     * </p>
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @param a3 first factor of the third term
     * @param b3 second factor of the third term
     * @param a4 first factor of the third term
     * @param b4 second factor of the third term
     * @return a<sub>1</sub>&times;b<sub>1</sub> +
     * a<sub>2</sub>&times;b<sub>2</sub> + a<sub>3</sub>&times;b<sub>3</sub> +
     * a<sub>4</sub>&times;b<sub>4</sub>
     * @see #linearCombination(double, double, double, double)
     * @see #linearCombination(double, double, double, double, double, double)
     */
    public static double linearCombination(final double a1, final double b1,
                                           final double a2, final double b2,
                                           final double a3, final double b3,
                                           final double a4, final double b4) {

        // the code below is split in many additions/subtractions that may
        // appear redundant. However, they shoud NOT be simplified, as they
        // do use IEEE754 floating point arithmetic rouding properties.
        // as an example, the expression "ca1 - (ca1 - a1)" is NOT the same as "a1"
        // The variables naming conventions are that xyzHigh contains the most significant
        // bits of xyz and xyzLow contains its least significant bits. So theoretically
        // xyz is the sum xyzHigh + xyzLow, but in many cases below, this sum cannot
        // be represented in only one double precision number so we preserve two numbers
        // to hold it as long as we can, combining the high and low order bits together
        // only at the end, after cancellation may have occurred on high order bits

        // split a1 and b1 as two 26 bits numbers
        final double ca1        = SPLIT_FACTOR * a1;
        final double a1High     = ca1 - (ca1 - a1);
        final double a1Low      = a1 - a1High;
        final double cb1        = SPLIT_FACTOR * b1;
        final double b1High     = cb1 - (cb1 - b1);
        final double b1Low      = b1 - b1High;

        // accurate multiplication a1 * b1
        final double prod1High  = a1 * b1;
        final double prod1Low   = a1Low * b1Low - (((prod1High - a1High * b1High) - a1Low * b1High) - a1High * b1Low);

        // split a2 and b2 as two 26 bits numbers
        final double ca2        = SPLIT_FACTOR * a2;
        final double a2High     = ca2 - (ca2 - a2);
        final double a2Low      = a2 - a2High;
        final double cb2        = SPLIT_FACTOR * b2;
        final double b2High     = cb2 - (cb2 - b2);
        final double b2Low      = b2 - b2High;

        // accurate multiplication a2 * b2
        final double prod2High  = a2 * b2;
        final double prod2Low   = a2Low * b2Low - (((prod2High - a2High * b2High) - a2Low * b2High) - a2High * b2Low);

        // split a3 and b3 as two 26 bits numbers
        final double ca3        = SPLIT_FACTOR * a3;
        final double a3High     = ca3 - (ca3 - a3);
        final double a3Low      = a3 - a3High;
        final double cb3        = SPLIT_FACTOR * b3;
        final double b3High     = cb3 - (cb3 - b3);
        final double b3Low      = b3 - b3High;

        // accurate multiplication a3 * b3
        final double prod3High  = a3 * b3;
        final double prod3Low   = a3Low * b3Low - (((prod3High - a3High * b3High) - a3Low * b3High) - a3High * b3Low);

        // split a4 and b4 as two 26 bits numbers
        final double ca4        = SPLIT_FACTOR * a4;
        final double a4High     = ca4 - (ca4 - a4);
        final double a4Low      = a4 - a4High;
        final double cb4        = SPLIT_FACTOR * b4;
        final double b4High     = cb4 - (cb4 - b4);
        final double b4Low      = b4 - b4High;

        // accurate multiplication a4 * b4
        final double prod4High  = a4 * b4;
        final double prod4Low   = a4Low * b4Low - (((prod4High - a4High * b4High) - a4Low * b4High) - a4High * b4Low);

        // accurate addition a1 * b1 + a2 * b2
        final double s12High    = prod1High + prod2High;
        final double s12Prime   = s12High - prod2High;
        final double s12Low     = (prod2High - (s12High - s12Prime)) + (prod1High - s12Prime);

        // accurate addition a1 * b1 + a2 * b2 + a3 * b3
        final double s123High   = s12High + prod3High;
        final double s123Prime  = s123High - prod3High;
        final double s123Low    = (prod3High - (s123High - s123Prime)) + (s12High - s123Prime);

        // accurate addition a1 * b1 + a2 * b2 + a3 * b3 + a4 * b4
        final double s1234High  = s123High + prod4High;
        final double s1234Prime = s1234High - prod4High;
        final double s1234Low   = (prod4High - (s1234High - s1234Prime)) + (s123High - s1234Prime);

        // final rounding, s1234 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        return s1234High + (prod1Low + prod2Low + prod3Low + prod4Low + s12Low + s123Low + s1234Low);

    }

    /**
     * Compute a linear combination accurately.
     * This method computes the sum of the products
     * <code>a<sub>i</sub> b<sub>i</sub></code> to high accuracy.
     * It does so by using specific multiplication and addition algorithms to
     * preserve accuracy and reduce cancellation effects.
     * <br/>
     * It is based on the 2005 paper
     * <a href="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547">
     * Accurate Sum and Dot Product</a> by Takeshi Ogita, Siegfried M. Rump,
     * and Shin'ichi Oishi published in SIAM J. Sci. Comput.
     *
     * @param a Factors.
     * @param b Factors.
     * @return <code>&Sigma;<sub>i</sub> a<sub>i</sub> b<sub>i</sub></code>.
     */
    public static double linearCombination(final double[] a, final double[] b) {
        final int len = a.length;
        if (len != b.length) {
            throw new DimensionMismatchException(len, b.length);
        }

        final double[] prodHigh = new double[len];
        double prodLowSum = 0;

        for (int i = 0; i < len; i++) {
            final double ai = a[i];
            final double ca = SPLIT_FACTOR * ai;
            final double aHigh = ca - (ca - ai);
            final double aLow = ai - aHigh;

            final double bi = b[i];
            final double cb = SPLIT_FACTOR * bi;
            final double bHigh = cb - (cb - bi);
            final double bLow = bi - bHigh;
            prodHigh[i] = ai * bi;
            final double prodLow = aLow * bLow - (((prodHigh[i] -
                                                    aHigh * bHigh) -
                                                   aLow * bHigh) -
                                                  aHigh * bLow);
            prodLowSum += prodLow;
        }


        final double prodHighCur = prodHigh[0];
        double prodHighNext = prodHigh[1];
        double sHighPrev = prodHighCur + prodHighNext;
        double sPrime = sHighPrev - prodHighNext;
        double sLowSum = (prodHighNext - (sHighPrev - sPrime)) + (prodHighCur - sPrime);

        final int lenMinusOne = len - 1;
        for (int i = 1; i < lenMinusOne; i++) {
            prodHighNext = prodHigh[i + 1];
            final double sHighCur = sHighPrev + prodHighNext;
            sPrime = sHighCur - prodHighNext;
            sLowSum += (prodHighNext - (sHighCur - sPrime)) + (sHighPrev - sPrime);
            sHighPrev = sHighCur;
        }

        return sHighPrev + (prodLowSum + sLowSum);
    }
}
