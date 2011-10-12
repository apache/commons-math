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
import java.util.Arrays;

import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.MathIllegalArgumentException;
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

    /**
     * Private Constructor
     */
    private MathUtils() {
        super();
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
                final long d = ArithmeticsUtils.gcd(i, j);
                result = (result / (j / d)) * (i / d);
                i++;
            }
        } else {
            // For n > 66, a result overflow might occur, so we check
            // the multiplication, taking care to not overflow
            // unnecessary.
            int i = n - k + 1;
            for (int j = 1; j <= k; j++) {
                final long d = ArithmeticsUtils.gcd(i, j);
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
        int lcm = FastMath.abs(mulAndCheck(a / ArithmeticsUtils.gcd(a, b), b));
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
        long lcm = FastMath.abs(mulAndCheck(a / ArithmeticsUtils.gcd(a, b), b));
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
}
