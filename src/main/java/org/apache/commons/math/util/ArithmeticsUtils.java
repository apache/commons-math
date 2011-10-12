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

import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.NotPositiveException;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Some useful, arithmetics related, additions to the built-in functions in
 * {@link Math}.
 *
 * @version $Id$
 */
public final class ArithmeticsUtils {

    /** All long-representable factorials */
    static final long[] FACTORIALS = new long[] {
                       1l,                  1l,                   2l,
                       6l,                 24l,                 120l,
                     720l,               5040l,               40320l,
                  362880l,            3628800l,            39916800l,
               479001600l,         6227020800l,         87178291200l,
           1307674368000l,     20922789888000l,     355687428096000l,
        6402373705728000l, 121645100408832000l, 2432902008176640000l };

    /** Private constructor. */
    private ArithmeticsUtils() {
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
        return ArithmeticsUtils.addAndCheck(a, b, LocalizedFormats.OVERFLOW_IN_ADDITION);
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
        return FastMath.floor(FastMath.exp(ArithmeticsUtils.factorialLog(n)) + 0.5);
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
}
