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
package org.apache.commons.math4.core.jdkmath;

import java.util.function.IntUnaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.LongToIntFunction;
import java.util.function.DoubleSupplier;

/**
 * Wrapper for alternative implementations of {@link Math} functions.
 * For example, a call to {@code Math.sin(x)} can be replaced by a call
 * to {@code JdkMath.sin(x)}.
 *
 * <p>
 * This class is a "drop-in" replacement for both Math and StrictMath,
 * up to the <em>minimal</em> JDK version required by this library
 * (meaning that, although the library can be used on <em>more</em>
 * recent JVMs, the {@code JdkMath} class may be missing the methods
 * that were absent in older JDKs).
 *
 * <p>
 * Based on the value, at class initialization, of the system property
 * {@code org.apache.commons.math.jdkmath}, this class redirects to a
 * specific implementation:
 * <ul>
 *  <li>{@code CM}: {@link AccurateMath}</li>
 *  <li>{@code JDK}: {@link Math}</li>
 * </ul>
 * When the property is undefined, {@code CM} is the default value.
 */
public final class JdkMath {
    /** Constant. */
    public static final double PI;
    /** Constant. */
    public static final double E;
    /** Property identifier.. */
    private static final String PROPERTY_KEY = "org.apache.commons.math.jdkmath";
    /** abs(x). */
    private static final IntUnaryOperator ABS_INT;
    /** abs(x). */
    private static final LongUnaryOperator ABS_LONG;
    /** abs(x). */
    private static final FloatUnaryOperator ABS_FLOAT;
    /** abs(x). */
    private static final DoubleUnaryOperator ABS_DOUBLE;
    /** acos(x). */
    private static final DoubleUnaryOperator ACOS;
    /** acosh(x). */
    private static final DoubleUnaryOperator ACOSH;
    /** addExact(x, y). */
    private static final IntBinaryOperator ADDEXACT_INT;
    /** addExact(x, y). */
    private static final LongBinaryOperator ADDEXACT_LONG;
    /** asin(x). */
    private static final DoubleUnaryOperator ASIN;
    /** asinh(x). */
    private static final DoubleUnaryOperator ASINH;
    /** atan(x). */
    private static final DoubleUnaryOperator ATAN;
    /** atan2(x, y). */
    private static final DoubleBinaryOperator ATAN2;
    /** atanh(x). */
    private static final DoubleUnaryOperator ATANH;
    /** cbrt(x). */
    private static final DoubleUnaryOperator CBRT;
    /** ceil(x). */
    private static final DoubleUnaryOperator CEIL;
    /** copySign(x, y). */
    private static final FloatBinaryOperator COPYSIGN_FLOAT;
    /** copySign(x, y). */
    private static final DoubleBinaryOperator COPYSIGN_DOUBLE;
    /** cos(x). */
    private static final DoubleUnaryOperator COS;
    /** cosh(x). */
    private static final DoubleUnaryOperator COSH;
    /** decrementExact(x). */
    private static final IntUnaryOperator DECREMENTEXACT_INT;
    /** decrementExact(x). */
    private static final LongUnaryOperator DECREMENTEXACT_LONG;
    /** exp(x). */
    private static final DoubleUnaryOperator EXP;
    /** expm1(x). */
    private static final DoubleUnaryOperator EXPM1;
    /** floor(x). */
    private static final DoubleUnaryOperator FLOOR;
    /** floorDiv(x, y). */
    private static final IntBinaryOperator FLOORDIV_INT;
    /** floorDiv(x, y). */
    private static final LongBinaryOperator FLOORDIV_LONG;
    /** floorMod(x, y). */
    private static final IntBinaryOperator FLOORMOD_INT;
    /** floorMod(x, y). */
    private static final LongBinaryOperator FLOORMOD_LONG;
    /** getExponent(x). */
    private static final FloatToIntFunction GETEXPONENT_FLOAT;
    /** getExponent(x). */
    private static final DoubleToIntFunction GETEXPONENT_DOUBLE;
    /** hypot(x, y). */
    private static final DoubleBinaryOperator HYPOT;
    /** IEEEremainder(x, y). */
    private static final DoubleBinaryOperator IEEEREMAINDER;
    /** incrementExact(x). */
    private static final IntUnaryOperator INCREMENTEXACT_INT;
    /** incrementExact(x). */
    private static final LongUnaryOperator INCREMENTEXACT_LONG;
    /** log(x). */
    private static final DoubleUnaryOperator LOG;
    /** log10(x). */
    private static final DoubleUnaryOperator LOG10;
    /** log1p(x). */
    private static final DoubleUnaryOperator LOG1P;
    /** max(x, y). */
    private static final IntBinaryOperator MAX_INT;
    /** max(x, y). */
    private static final LongBinaryOperator MAX_LONG;
    /** max(x, y). */
    private static final FloatBinaryOperator MAX_FLOAT;
    /** max(x, y). */
    private static final DoubleBinaryOperator MAX_DOUBLE;
    /** min(x, y). */
    private static final IntBinaryOperator MIN_INT;
    /** min(x, y). */
    private static final LongBinaryOperator MIN_LONG;
    /** min(x, y). */
    private static final FloatBinaryOperator MIN_FLOAT;
    /** min(x, y). */
    private static final DoubleBinaryOperator MIN_DOUBLE;
    /** multiplyExact(x, y). */
    private static final IntBinaryOperator MULTIPLYEXACT_INT;
    /** multiplyExact(x, y). */
    private static final LongBinaryOperator MULTIPLYEXACT_LONG;
    /** negateExact(x). */
    private static final IntUnaryOperator NEGATEEXACT_INT;
    /** negateExact(x). */
    private static final LongUnaryOperator NEGATEEXACT_LONG;
    /** nextAfter(x, y). */
    private static final FloatDouble2FloatOperator NEXTAFTER_FLOAT;
    /** nextAfter(x, y). */
    private static final DoubleBinaryOperator NEXTAFTER_DOUBLE;
    /** nextDown(x). */
    private static final FloatUnaryOperator NEXTDOWN_FLOAT;
    /** nextDown(x). */
    private static final DoubleUnaryOperator NEXTDOWN_DOUBLE;
    /** nextUp(x). */
    private static final FloatUnaryOperator NEXTUP_FLOAT;
    /** nextUp(x). */
    private static final DoubleUnaryOperator NEXTUP_DOUBLE;
    /** pow(x, y). */
    private static final DoubleBinaryOperator POW;
    /** random(). */
    private static final DoubleSupplier RANDOM;
    /** rint(x). */
    private static final DoubleUnaryOperator RINT;
    /** round(x). */
    private static final DoubleToLongFunction ROUND_DOUBLE;
    /** round(x). */
    private static final FloatToIntFunction ROUND_FLOAT;
    /** scalb(x, y). */
    private static final DoubleInt2DoubleOperator SCALB_DOUBLE;
    /** scalb(x, y). */
    private static final FloatInt2FloatOperator SCALB_FLOAT;
    /** signum(x). */
    private static final FloatUnaryOperator SIGNUM_FLOAT;
    /** signum(x). */
    private static final DoubleUnaryOperator SIGNUM_DOUBLE;
    /** sin(x). */
    private static final DoubleUnaryOperator SIN;
    /** sinh(x). */
    private static final DoubleUnaryOperator SINH;
    /** sqrt(x). */
    private static final DoubleUnaryOperator SQRT;
    /** subtractExact(x, y). */
    private static final IntBinaryOperator SUBTRACTEXACT_INT;
    /** subtractExact(x, y). */
    private static final LongBinaryOperator SUBTRACTEXACT_LONG;
    /** tan(x). */
    private static final DoubleUnaryOperator TAN;
    /** tanh(x). */
    private static final DoubleUnaryOperator TANH;
    /** toDegrees(x). */
    private static final DoubleUnaryOperator TODEGREES;
    /** toIntExact(x). */
    private static final LongToIntFunction TOINTEXACT;
    /** toRadians(x). */
    private static final DoubleUnaryOperator TORADIANS;
    /** ulp(x). */
    private static final DoubleUnaryOperator ULP_DOUBLE;
    /** ulp(x). */
    private static final FloatUnaryOperator ULP_FLOAT;

    /** Available implementations of {@link Math} functions. */
    public enum Impl {
        /** {@link AccurateMath Commons Math}. */
        CM,
        /** {@link Math JDK}. */
        JDK
    }

    static {
        final String prop = System.getProperty(PROPERTY_KEY);
        final Impl impl = prop != null ?
            Impl.valueOf(prop) :
            Impl.CM;


        switch (impl) {
        case CM:
            PI = AccurateMath.PI;
            E = AccurateMath.E;
            ABS_INT = AccurateMath::abs;
            ABS_LONG = AccurateMath::abs;
            ABS_FLOAT = AccurateMath::abs;
            ABS_DOUBLE = AccurateMath::abs;
            ACOS = AccurateMath::acos;
            ACOSH = AccurateMath::acosh;
            ADDEXACT_INT = AccurateMath::addExact;
            ADDEXACT_LONG = AccurateMath::addExact;
            ASIN = AccurateMath::asin;
            ASINH = AccurateMath::asinh;
            ATAN = AccurateMath::atan;
            ATAN2 = AccurateMath::atan2;
            ATANH = AccurateMath::atanh;
            CBRT = AccurateMath::cbrt;
            CEIL = AccurateMath::ceil;
            COPYSIGN_FLOAT = AccurateMath::copySign;
            COPYSIGN_DOUBLE = AccurateMath::copySign;
            COS = AccurateMath::cos;
            COSH = AccurateMath::cosh;
            DECREMENTEXACT_INT = AccurateMath::decrementExact;
            DECREMENTEXACT_LONG = AccurateMath::decrementExact;
            EXP = AccurateMath::exp;
            EXPM1 = AccurateMath::expm1;
            FLOOR = AccurateMath::floor;
            FLOORDIV_INT = AccurateMath::floorDiv;
            FLOORDIV_LONG = AccurateMath::floorDiv;
            FLOORMOD_INT = AccurateMath::floorMod;
            FLOORMOD_LONG = AccurateMath::floorMod;
            GETEXPONENT_FLOAT = AccurateMath::getExponent;
            GETEXPONENT_DOUBLE = AccurateMath::getExponent;
            HYPOT = AccurateMath::hypot;
            IEEEREMAINDER = AccurateMath::IEEEremainder;
            INCREMENTEXACT_INT = AccurateMath::incrementExact;
            INCREMENTEXACT_LONG = AccurateMath::incrementExact;
            LOG = AccurateMath::log;
            LOG10 = AccurateMath::log10;
            LOG1P = AccurateMath::log1p;
            MAX_INT = AccurateMath::max;
            MAX_LONG = AccurateMath::max;
            MAX_FLOAT = AccurateMath::max;
            MAX_DOUBLE = AccurateMath::max;
            MIN_INT = AccurateMath::min;
            MIN_LONG = AccurateMath::min;
            MIN_FLOAT = AccurateMath::min;
            MIN_DOUBLE = AccurateMath::min;
            MULTIPLYEXACT_INT = AccurateMath::multiplyExact;
            MULTIPLYEXACT_LONG = AccurateMath::multiplyExact;
            NEGATEEXACT_INT = Math::negateExact; // Not implemented.
            NEGATEEXACT_LONG = Math::negateExact; // Not implemented.
            NEXTAFTER_FLOAT = AccurateMath::nextAfter;
            NEXTAFTER_DOUBLE = AccurateMath::nextAfter;
            NEXTDOWN_FLOAT = AccurateMath::nextDown;
            NEXTDOWN_DOUBLE = AccurateMath::nextDown;
            NEXTUP_FLOAT = AccurateMath::nextUp;
            NEXTUP_DOUBLE = AccurateMath::nextUp;
            POW = AccurateMath::pow;
            RANDOM = Math::random; // Not implemented.
            RINT = AccurateMath::rint;
            ROUND_DOUBLE = AccurateMath::round;
            ROUND_FLOAT = AccurateMath::round;
            SCALB_DOUBLE = AccurateMath::scalb;
            SCALB_FLOAT = AccurateMath::scalb;
            SIGNUM_DOUBLE = AccurateMath::signum;
            SIGNUM_FLOAT = AccurateMath::signum;
            SQRT = Math::sqrt; // Not implemented.
            SIN = AccurateMath::sin;
            SINH = AccurateMath::sinh;
            SUBTRACTEXACT_INT = AccurateMath::subtractExact;
            SUBTRACTEXACT_LONG = AccurateMath::subtractExact;
            TAN = AccurateMath::tan;
            TANH = AccurateMath::tanh;
            TODEGREES = AccurateMath::toDegrees;
            TOINTEXACT = AccurateMath::toIntExact;
            TORADIANS = AccurateMath::toRadians;
            ULP_DOUBLE = AccurateMath::ulp;
            ULP_FLOAT = AccurateMath::ulp;
            break;

        case JDK:
            PI = Math.PI;
            E = Math.E;
            ABS_INT = Math::abs;
            ABS_LONG = Math::abs;
            ABS_FLOAT = Math::abs;
            ABS_DOUBLE = Math::abs;
            ACOS = Math::acos;
            ACOSH = AccurateMath::acosh; // Not implemented.
            ADDEXACT_INT = Math::addExact;
            ADDEXACT_LONG = Math::addExact;
            ASIN = Math::asin;
            ASINH = AccurateMath::asinh; // Not implemented.
            ATAN = Math::atan;
            ATAN2 = Math::atan2;
            ATANH = AccurateMath::atanh; // Not implemented.
            CBRT = Math::cbrt;
            CEIL = Math::ceil;
            COPYSIGN_FLOAT = Math::copySign;
            COPYSIGN_DOUBLE = Math::copySign;
            COS = Math::cos;
            COSH = Math::cosh;
            DECREMENTEXACT_INT = Math::decrementExact;
            DECREMENTEXACT_LONG = Math::decrementExact;
            EXP = Math::exp;
            EXPM1 = Math::expm1;
            FLOOR = Math::floor;
            FLOORDIV_INT = Math::floorDiv;
            FLOORDIV_LONG = Math::floorDiv;
            FLOORMOD_INT = Math::floorMod;
            FLOORMOD_LONG = Math::floorMod;
            GETEXPONENT_FLOAT = Math::getExponent;
            GETEXPONENT_DOUBLE = Math::getExponent;
            HYPOT = Math::hypot;
            IEEEREMAINDER = Math::IEEEremainder;
            INCREMENTEXACT_INT = Math::incrementExact;
            INCREMENTEXACT_LONG = Math::incrementExact;
            LOG = Math::log;
            LOG10 = Math::log10;
            LOG1P = Math::log1p;
            MAX_INT = Math::max;
            MAX_LONG = Math::max;
            MAX_FLOAT = Math::max;
            MAX_DOUBLE = Math::max;
            MIN_INT = Math::min;
            MIN_LONG = Math::min;
            MIN_FLOAT = Math::min;
            MIN_DOUBLE = Math::min;
            MULTIPLYEXACT_INT = Math::multiplyExact;
            MULTIPLYEXACT_LONG = Math::multiplyExact;
            NEGATEEXACT_INT = Math::negateExact;
            NEGATEEXACT_LONG = Math::negateExact;
            NEXTAFTER_FLOAT = Math::nextAfter;
            NEXTAFTER_DOUBLE = Math::nextAfter;
            NEXTDOWN_FLOAT = Math::nextDown;
            NEXTDOWN_DOUBLE = Math::nextDown;
            NEXTUP_FLOAT = Math::nextUp;
            NEXTUP_DOUBLE = Math::nextUp;
            POW = Math::pow;
            RANDOM = Math::random;
            RINT = Math::rint;
            ROUND_DOUBLE = Math::round;
            ROUND_FLOAT = Math::round;
            SCALB_DOUBLE = Math::scalb;
            SCALB_FLOAT = Math::scalb;
            SIGNUM_DOUBLE = Math::signum;
            SIGNUM_FLOAT = Math::signum;
            SIN = Math::sin;
            SINH = Math::sinh;
            SQRT = Math::sqrt;
            SUBTRACTEXACT_INT = Math::subtractExact;
            SUBTRACTEXACT_LONG = Math::subtractExact;
            TAN = Math::tan;
            TANH = Math::tanh;
            TODEGREES = Math::toDegrees;
            TOINTEXACT = Math::toIntExact;
            TORADIANS = Math::toRadians;
            ULP_DOUBLE = Math::ulp;
            ULP_FLOAT = Math::ulp;
            break;

        default:
            throw new IllegalStateException("Internal error"); // Should never happen.
        }
    }

    /** Utility class. */
    private JdkMath() {}

    /**
     * @param x Number.
     * @return abs(x).
     *
     * @see Math#abs(int)
     */
    public static int abs(int x) {
        return ABS_INT.applyAsInt(x);
    }

    /**
     * @param x Number.
     * @return abs(x).
     *
     * @see Math#abs(long)
     */
    public static long abs(long x) {
        return ABS_LONG.applyAsLong(x);
    }

    /**
     * @param x Number.
     * @return abs(x).
     *
     * @see Math#abs(float)
     */
    public static float abs(float x) {
        return ABS_FLOAT.applyAsFloat(x);
    }

    /**
     * @param x Number.
     * @return abs(x).
     *
     * @see Math#abs(double)
     */
    public static double abs(double x) {
        return ABS_DOUBLE.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return acos(x).
     *
     * @see Math#acos(double)
     */
    public static double acos(double x) {
        return ACOS.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return acosh(x).
     */
    public static double acosh(double x) {
        return ACOSH.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return addExact(x, y).
     *
     * @see Math#addExact(int,int)
     */
    public static int addExact(int x,
                               int y) {
        return ADDEXACT_INT.applyAsInt(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return addExact(x, y).
     *
     * @see Math#addExact(long,long)
     */
    public static long addExact(long x,
                                long y) {
        return ADDEXACT_LONG.applyAsLong(x, y);
    }

    /**
     * @param x Number.
     * @return asin(x).
     *
     * @see Math#asin(double)
     */
    public static double asin(double x) {
        return ASIN.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return asinh(x).
     */
    public static double asinh(double x) {
        return ASINH.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return atan(x).
     *
     * @see Math#atan(double)
     */
    public static double atan(double x) {
        return ATAN.applyAsDouble(x);
    }

    /**
     * @param y Number.
     * @param x Number.
     * @return atan2(y, x).
     *
     * @see Math#atan2(double,double)
     */
    public static double atan2(double y,
                               double x) {
        return ATAN2.applyAsDouble(y, x);
    }

    /**
     * @param x Number.
     * @return atanh(x).
     */
    public static double atanh(double x) {
        return ATANH.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return cbrt(x).
     *
     * @see Math#cbrt(double)
     */
    public static double cbrt(double x) {
        return CBRT.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return ceil(x).
     *
     * @see Math#ceil(double)
     */
    public static double ceil(double x) {
        return CEIL.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return copySign(x, y).
     *
     * @see Math#copySign(float,float)
     */
    public static float copySign(float x,
                                 float y) {
        return COPYSIGN_FLOAT.applyAsFloat(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return copySign(x, y).
     *
     * @see Math#copySign(double,double)
     */
    public static double copySign(double x,
                                  double y) {
        return COPYSIGN_DOUBLE.applyAsDouble(x, y);
    }

    /**
     * @param x Number.
     * @return cos(x).
     *
     * @see Math#cos(double)
     */
    public static double cos(double x) {
        return COS.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return cosh(x).
     *
     * @see Math#cosh(double)
     */
    public static double cosh(double x) {
        return COSH.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return decrementExact(x).
     *
     * @see Math#decrementExact(int)
     */
    public static int decrementExact(int x) {
        return DECREMENTEXACT_INT.applyAsInt(x);
    }

    /**
     * @param x Number.
     * @return decrementExact(x).
     *
     * @see Math#decrementExact(long)
     */
    public static long decrementExact(long x) {
        return DECREMENTEXACT_LONG.applyAsLong(x);
    }

    /**
     * @param x Number.
     * @return exp(x).
     *
     * @see Math#exp(double)
     */
    public static double exp(double x) {
        return EXP.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return expm1(x).
     *
     * @see Math#expm1(double)
     */
    public static double expm1(double x) {
        return EXPM1.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return floor(x).
     *
     * @see Math#floor(double)
     */
    public static double floor(double x) {
        return FLOOR.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return floorDiv(x, y).
     *
     * @see Math#floorDiv(int,int)
     */
    public static int floorDiv(int x,
                               int y) {
        return FLOORDIV_INT.applyAsInt(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return floorDiv(x, y).
     *
     * @see Math#floorDiv(long,long)
     */
    public static long floorDiv(long x,
                                long y) {
        return FLOORDIV_LONG.applyAsLong(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return floorMod(x, y).
     *
     * @see Math#floorMod(int,int)
     */
    public static int floorMod(int x,
                               int y) {
        return FLOORMOD_INT.applyAsInt(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return floorMod(x, y).
     *
     * @see Math#floorMod(long,long)
     */
    public static long floorMod(long x,
                                long y) {
        return FLOORMOD_LONG.applyAsLong(x, y);
    }

    /**
     * @param x Number.
     * @return getExponent(x).
     *
     * @see Math#getExponent(float)
     */
    public static int getExponent(float x) {
        return GETEXPONENT_FLOAT.applyAsInt(x);
    }

    /**
     * @param x Number.
     * @return getExponent(x).
     *
     * @see Math#getExponent(double)
     */
    public static int getExponent(double x) {
        return GETEXPONENT_DOUBLE.applyAsInt(x);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return hypot(x, y).
     *
     * @see Math#hypot(double,double)
     */
    public static double hypot(double x,
                               double y) {
        return HYPOT.applyAsDouble(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return IEEEremainder(x, y).
     *
     * @see Math#IEEEremainder(double,double)
     */
    public static double IEEEremainder(double x,
                                       double y) {
        return IEEEREMAINDER.applyAsDouble(x, y);
    }

    /**
     * @param x Number.
     * @return incrementExact(x).
     *
     * @see Math#incrementExact(int)
     */
    public static int incrementExact(int x) {
        return INCREMENTEXACT_INT.applyAsInt(x);
    }

    /**
     * @param x Number.
     * @return incrementExact(x).
     *
     * @see Math#incrementExact(long)
     */
    public static long incrementExact(long x) {
        return INCREMENTEXACT_LONG.applyAsLong(x);
    }

    /**
     * @param x Number.
     * @return log(x).
     *
     * @see Math#log(double)
     */
    public static double log(double x) {
        return LOG.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return log10(x).
     *
     * @see Math#log10(double)
     */
    public static double log10(double x) {
        return LOG10.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return log1p(x).
     *
     * @see Math#log1p(double)
     */
    public static double log1p(double x) {
        return LOG1P.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return max(x, y).
     *
     * @see Math#max(int,int)
     */
    public static int max(int x,
                          int y) {
        return MAX_INT.applyAsInt(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return max(x, y).
     *
     * @see Math#max(long,long)
     */
    public static long max(long x,
                           long y) {
        return MAX_LONG.applyAsLong(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return max(x, y).
     *
     * @see Math#max(float,float)
     */
    public static float max(float x,
                            float y) {
        return MAX_FLOAT.applyAsFloat(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return max(x, y).
     *
     * @see Math#max(double,double)
     */
    public static double max(double x,
                             double y) {
        return MAX_DOUBLE.applyAsDouble(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return min(x, y).
     *
     * @see Math#min(int,int)
     */
    public static int min(int x,
                          int y) {
        return MIN_INT.applyAsInt(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return min(x, y).
     *
     * @see Math#min(long,long)
     */
    public static long min(long x,
                           long y) {
        return MIN_LONG.applyAsLong(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return min(x, y).
     *
     * @see Math#min(float,float)
     */
    public static float min(float x,
                            float y) {
        return MIN_FLOAT.applyAsFloat(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return min(x, y).
     *
     * @see Math#min(double,double)
     */
    public static double min(double x,
                             double y) {
        return MIN_DOUBLE.applyAsDouble(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return multiplyExact(x, y).
     *
     * @see Math#multiplyExact(int,int)
     */
    public static int multiplyExact(int x,
                                    int y) {
        return MULTIPLYEXACT_INT.applyAsInt(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return multiplyExact(x, y).
     *
     * @see Math#multiplyExact(long,long)
     */
    public static long multiplyExact(long x,
                                     long y) {
        return MULTIPLYEXACT_LONG.applyAsLong(x, y);
    }

    /**
     * @param x Number.
     * @return negateExact(x).
     *
     * @see Math#negateExact(int)
     */
    public static int negateExact(int x) {
        return NEGATEEXACT_INT.applyAsInt(x);
    }

    /**
     * @param x Number.
     * @return negateExact(x).
     *
     * @see Math#negateExact(long)
     */
    public static long negateExact(long x) {
        return NEGATEEXACT_LONG.applyAsLong(x);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return nextAfter(x, y).
     *
     * @see Math#nextAfter(double, double)
     */
    public static double nextAfter(double x,
                                   double y) {
        return NEXTAFTER_DOUBLE.applyAsDouble(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return nextAfter(x, y).
     *
     * @see Math#nextAfter(float,double)
     */
    public static float nextAfter(float x,
                                  double y) {
        return NEXTAFTER_FLOAT.applyAsFloat(x, y);
    }

    /**
     * @param x Number.
     * @return nextDown(x).
     *
     * @see Math#nextDown(double)
     */
    public static double nextDown(double x) {
        return NEXTDOWN_DOUBLE.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return nextDown(x).
     *
     * @see Math#nextDown(float)
     */
    public static float nextDown(float x) {
        return NEXTDOWN_FLOAT.applyAsFloat(x);
    }

    /**
     * @param x Number.
     * @return nextUp(x).
     *
     * @see Math#nextUp(double)
     */
    public static double nextUp(double x) {
        return NEXTUP_DOUBLE.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return nextUp(x).
     *
     * @see Math#nextUp(float)
     */
    public static float nextUp(float x) {
        return NEXTUP_FLOAT.applyAsFloat(x);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return pow(x, y).
     *
     * @see Math#pow(double,double)
     */
    public static double pow(double x,
                             double y) {
        return POW.applyAsDouble(x, y);
    }

    /**
     * @return a random number between 0 and 1.
     *
     * @see Math#random()
     */
    public static double random() {
        return RANDOM.getAsDouble();
    }

    /**
     * @param x Number.
     * @return rint(x).
     *
     * @see Math#rint(double)
     */
    public static double rint(double x) {
        return RINT.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return round(x).
     *
     * @see Math#round(float)
     */
    public static int round(float x) {
        return ROUND_FLOAT.applyAsInt(x);
    }

    /**
     * @param x Number.
     * @return round(x).
     *
     * @see Math#round(double)
     */
    public static long round(double x) {
        return ROUND_DOUBLE.applyAsLong(x);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return scalb(x, y).
     *
     * @see Math#scalb(double,int)
     */
    public static double scalb(double x,
                               int y) {
        return SCALB_DOUBLE.applyAsDouble(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return scalb(x, y).
     *
     * @see Math#scalb(float,int)
     */
    public static float scalb(float x,
                              int y) {
        return SCALB_FLOAT.applyAsFloat(x, y);
    }

    /**
     * @param x Number.
     * @return signum(x).
     *
     * @see Math#signum(double)
     */
    public static double signum(double x) {
        return SIGNUM_DOUBLE.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return signum(x).
     *
     * @see Math#signum(float)
     */
    public static float signum(float x) {
        return SIGNUM_FLOAT.applyAsFloat(x);
    }

    /**
     * @param x Number.
     * @return sin(x).
     *
     * @see Math#sin(double)
     */
    public static double sin(double x) {
        return SIN.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return sinh(x).
     *
     * @see Math#sinh(double)
     */
    public static double sinh(double x) {
        return SINH.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return sqrt(x).
     *
     * @see Math#sqrt(double)
     */
    public static double sqrt(double x) {
        return SQRT.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return subtractExact(x, y).
     *
     * @see Math#subtractExact(int,int)
     */
    public static int subtractExact(int x,
                                    int y) {
        return SUBTRACTEXACT_INT.applyAsInt(x, y);
    }

    /**
     * @param x Number.
     * @param y Number.
     * @return subtractExact(x, y).
     *
     * @see Math#subtractExact(long,long)
     */
    public static long subtractExact(long x,
                                     long y) {
        return SUBTRACTEXACT_LONG.applyAsLong(x, y);
    }

    /**
     * @param x Number.
     * @return tan(x).
     *
     * @see Math#tan(double)
     */
    public static double tan(double x) {
        return TAN.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return tanh(x).
     *
     * @see Math#tanh(double)
     */
    public static double tanh(double x) {
        return TANH.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return toDegrees(x).
     *
     * @see Math#toDegrees(double)
     */
    public static double toDegrees(double x) {
        return TODEGREES.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return toIntExact(x).
     *
     * @see Math#toIntExact(long)
     */
    public static int toIntExact(long x) {
        return TOINTEXACT.applyAsInt(x);
    }

    /**
     * @param x Number.
     * @return toRadians(x).
     *
     * @see Math#toRadians(double)
     */
    public static double toRadians(double x) {
        return TORADIANS.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return ulp(x).
     *
     * @see Math#ulp(double)
     */
    public static double ulp(double x) {
        return ULP_DOUBLE.applyAsDouble(x);
    }

    /**
     * @param x Number.
     * @return ulp(x).
     *
     * @see Math#ulp(float)
     */
    public static float ulp(float x) {
        return ULP_FLOAT.applyAsFloat(x);
    }

    /** Interface missing from "java.util.function" package. */
    private interface FloatUnaryOperator {
        /**
         * @param x Operand.
         * @return the result of applying this operator.
         */
        float applyAsFloat(float x);
    }

    /** Interface missing from "java.util.function" package. */
    private interface FloatBinaryOperator {
        /**
         * @param x Operand.
         * @param y Operand.
         * @return the result of applying this operator.
         */
        float applyAsFloat(float x, float y);
    }

    /** Interface missing from "java.util.function" package. */
    private interface FloatDouble2FloatOperator {
        /**
         * @param x Operand.
         * @param y Operand.
         * @return the result of applying this operator.
         */
        float applyAsFloat(float x, double y);
    }

    /** Interface missing from "java.util.function" package. */
    private interface FloatToIntFunction {
        /**
         * @param x Operand.
         * @return the result of applying this operator.
         */
        int applyAsInt(float x);
    }

    /** Interface missing from "java.util.function" package. */
    private interface FloatInt2FloatOperator {
        /**
         * @param x Operand.
         * @param y Operand.
         * @return the result of applying this operator.
         */
        float applyAsFloat(float x, int y);
    }

    /** Interface missing from "java.util.function" package. */
    private interface DoubleInt2DoubleOperator {
        /**
         * @param x Operand.
         * @param y Operand.
         * @return the result of applying this operator.
         */
        double applyAsDouble(double x, int y);
    }
}
