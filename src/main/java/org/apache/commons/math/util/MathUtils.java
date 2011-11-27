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

import java.util.Arrays;

import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.NotFiniteNumberException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Miscellaneous utility functions.
 *
 * @see ArithmeticUtils
 * @see Precision
 * @see MathArrays
 *
 * @version $Id$
 */
public final class MathUtils {
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
     * Returns the first argument with the sign of the second argument.
     *
     * @param magnitude Magnitude of the returned value.
     * @param sign Sign of the returned value.
     * @return a value with magnitude equal to {@code magnitude} and with the
     * same sign as the {@code sign} argument.
     * @throws MathArithmeticException if {@code magnitude == Byte.MIN_VALUE}
     * and {@code sign >= 0}.
     */
    public static byte copySign(byte magnitude, byte sign) {
        if ((magnitude >= 0 && sign >= 0) ||
            (magnitude < 0 && sign < 0)) { // Sign is OK.
            return magnitude;
        } else if (sign >= 0 &&
                   magnitude == Byte.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW);
        } else {
            return (byte) -magnitude; // Flip sign.
        }
    }

    /**
     * Returns the first argument with the sign of the second argument.
     *
     * @param magnitude Magnitude of the returned value.
     * @param sign Sign of the returned value.
     * @return a value with magnitude equal to {@code magnitude} and with the
     * same sign as the {@code sign} argument.
     * @throws MathArithmeticException if {@code magnitude == Short.MIN_VALUE}
     * and {@code sign >= 0}.
     */
    public static short copySign(short magnitude, short sign) {
        if ((magnitude >= 0 && sign >= 0) ||
            (magnitude < 0 && sign < 0)) { // Sign is OK.
            return magnitude;
        } else if (sign >= 0 &&
                   magnitude == Short.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW);
        } else {
            return (short) -magnitude; // Flip sign.
        }
    }

    /**
     * Returns the first argument with the sign of the second argument.
     *
     * @param magnitude Magnitude of the returned value.
     * @param sign Sign of the returned value.
     * @return a value with magnitude equal to {@code magnitude} and with the
     * same sign as the {@code sign} argument.
     * @throws MathArithmeticException if {@code magnitude == Integer.MIN_VALUE}
     * and {@code sign >= 0}.
     */
    public static int copySign(int magnitude, int sign) {
        if ((magnitude >= 0 && sign >= 0) ||
            (magnitude < 0 && sign < 0)) { // Sign is OK.
            return magnitude;
        } else if (sign >= 0 &&
                   magnitude == Integer.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW);
        } else {
            return -magnitude; // Flip sign.
        }
    }

    /**
     * Returns the first argument with the sign of the second argument.
     *
     * @param magnitude Magnitude of the returned value.
     * @param sign Sign of the returned value.
     * @return a value with magnitude equal to {@code magnitude} and with the
     * same sign as the {@code sign} argument.
     * @throws MathArithmeticException if {@code magnitude == Long.MIN_VALUE}
     * and {@code sign >= 0}.
     */
    public static long copySign(long magnitude, long sign) {
        if ((magnitude >= 0 && sign >= 0) ||
            (magnitude < 0 && sign < 0)) { // Sign is OK.
            return magnitude;
        } else if (sign >= 0 &&
                   magnitude == Long.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW);
        } else {
            return -magnitude; // Flip sign.
        }
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
     * Check that all the elements are real numbers.
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
