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
    /** Default Implementation is CommonMathImplementation. */
    private static final MathImplementation DEFAULT_IMPLEMENTATION = new CommonsMathImplementation();

    static {
        MathImplementation mathImplementation = getMathImplementation();
        PI = mathImplementation.getPI();
        E = mathImplementation.getE();
        ABS_INT = mathImplementation.getABS_INT();
        ABS_LONG = mathImplementation.getABS_LONG();
        ABS_FLOAT = mathImplementation.getABS_FLOAT();
        ABS_DOUBLE = mathImplementation.getABS_DOUBLE();
        ACOS = mathImplementation.getACOS();
        ACOSH = mathImplementation.getACOSH();
        ADDEXACT_INT = mathImplementation.getADDEXACT_INT();
        ADDEXACT_LONG = mathImplementation.getADDEXACT_LONG();
        ASIN = mathImplementation.getASIN();
        ASINH = mathImplementation.getASINH();
        ATAN = mathImplementation.getATAN();
        ATAN2 = mathImplementation.getATAN2();
        ATANH = mathImplementation.getATANH();
        CBRT = mathImplementation.getCBRT();
        CEIL = mathImplementation.getCEIL();
        COPYSIGN_FLOAT = mathImplementation.getCOPYSIGN_FLOAT();
        COPYSIGN_DOUBLE = mathImplementation.getCOPYSIGN_DOUBLE();
        COS = mathImplementation.getCOS();
        COSH = mathImplementation.getCOSH();
        DECREMENTEXACT_INT = mathImplementation.getDECREMENTEXACT_INT();
        DECREMENTEXACT_LONG = mathImplementation.getDECREMENTEXACT_LONG();
        EXP = mathImplementation.getEXP();
        EXPM1 = mathImplementation.getEXPM1();
        FLOOR = mathImplementation.getFLOOR();
        FLOORDIV_INT = mathImplementation.getFLOORDIV_INT();
        FLOORDIV_LONG = mathImplementation.getFLOORDIV_LONG();
        FLOORMOD_INT = mathImplementation.getFLOORMOD_INT();
        FLOORMOD_LONG = mathImplementation.getFLOORMOD_LONG();
        GETEXPONENT_FLOAT = mathImplementation.getGETEXPONENT_FLOAT();
        GETEXPONENT_DOUBLE = mathImplementation.getGETEXPONENT_DOUBLE();
        HYPOT = mathImplementation.getHYPOT();
        IEEEREMAINDER = mathImplementation.getIEEEREMAINDER();
        INCREMENTEXACT_INT = mathImplementation.getINCREMENTEXACT_INT();
        INCREMENTEXACT_LONG = mathImplementation.getINCREMENTEXACT_LONG();
        LOG = mathImplementation.getLOG();
        LOG10 = mathImplementation.getLOG10();
        LOG1P = mathImplementation.getLOG1P();
        MAX_INT = mathImplementation.getMAX_INT();
        MAX_LONG = mathImplementation.getMAX_LONG();
        MAX_FLOAT = mathImplementation.getMAX_FLOAT();
        MAX_DOUBLE = mathImplementation.getMAX_DOUBLE();
        MIN_INT = mathImplementation.getMIN_INT();
        MIN_LONG = mathImplementation.getMIN_LONG();
        MIN_FLOAT = mathImplementation.getMIN_FLOAT();
        MIN_DOUBLE = mathImplementation.getMIN_DOUBLE();
        MULTIPLYEXACT_INT = mathImplementation.getMULTIPLYEXACT_INT();
        MULTIPLYEXACT_LONG = mathImplementation.getMULTIPLYEXACT_LONG();
        NEGATEEXACT_INT = mathImplementation.getNEGATEEXACT_INT(); // Not implemented.
        NEGATEEXACT_LONG = mathImplementation.getNEGATEEXACT_LONG(); // Not implemented.
        NEXTAFTER_FLOAT = mathImplementation.getNEXTAFTER_FLOAT();
        NEXTAFTER_DOUBLE = mathImplementation.getNEXTAFTER_DOUBLE();
        NEXTDOWN_FLOAT = mathImplementation.getNEXTDOWN_FLOAT();
        NEXTDOWN_DOUBLE = mathImplementation.getNEXTDOWN_DOUBLE();
        NEXTUP_FLOAT = mathImplementation.getNEXTUP_FLOAT();
        NEXTUP_DOUBLE = mathImplementation.getNEXTUP_DOUBLE();
        POW = mathImplementation.getPOW();
        RANDOM = mathImplementation.getRANDOM(); // Not implemented.
        RINT = mathImplementation.getRINT();
        ROUND_DOUBLE = mathImplementation.getROUND_DOUBLE();
        ROUND_FLOAT = mathImplementation.getROUND_FLOAT();
        SCALB_DOUBLE = mathImplementation.getSCALB_DOUBLE();
        SCALB_FLOAT = mathImplementation.getSCALB_FLOAT();
        SIGNUM_DOUBLE = mathImplementation.getSIGNUM_DOUBLE();
        SIGNUM_FLOAT = mathImplementation.getSIGNUM_FLOAT();
        SQRT = mathImplementation.getSQRT(); // Not implemented.
        SIN = mathImplementation.getSIN();
        SINH = mathImplementation.getSINH();
        SUBTRACTEXACT_INT = mathImplementation.getSUBTRACTEXACT_INT();
        SUBTRACTEXACT_LONG = mathImplementation.getSUBTRACTEXACT_LONG();
        TAN = mathImplementation.getTAN();
        TANH = mathImplementation.getTANH();
        TODEGREES = mathImplementation.getTODEGREES();
        TOINTEXACT = mathImplementation.getTOINTEXACT();
        TORADIANS = mathImplementation.getTORADIANS();
        ULP_DOUBLE = mathImplementation.getULP_DOUBLE();
        ULP_FLOAT = mathImplementation.getULP_FLOAT();
    }

    /** Utility class. */
    private JdkMath() {}

    /** Available implementations of {@link Math} functions. */
    private interface MathImplementation {
        double getPI();
        double getE();
        IntUnaryOperator getABS_INT();
        LongUnaryOperator getABS_LONG();
        FloatUnaryOperator getABS_FLOAT();
        DoubleUnaryOperator getABS_DOUBLE();
        DoubleUnaryOperator getACOS();
        DoubleUnaryOperator getACOSH();
        IntBinaryOperator getADDEXACT_INT();
        LongBinaryOperator getADDEXACT_LONG();
        DoubleUnaryOperator getASIN();
        DoubleUnaryOperator getASINH();
        DoubleUnaryOperator getATAN();
        DoubleBinaryOperator getATAN2();
        DoubleUnaryOperator getATANH();
        DoubleUnaryOperator getCBRT();
        DoubleUnaryOperator getCEIL();
        FloatBinaryOperator getCOPYSIGN_FLOAT();
        DoubleBinaryOperator getCOPYSIGN_DOUBLE();
        DoubleUnaryOperator getCOS();
        DoubleUnaryOperator getCOSH();
        IntUnaryOperator getDECREMENTEXACT_INT();
        LongUnaryOperator getDECREMENTEXACT_LONG();
        DoubleUnaryOperator getEXP();
        DoubleUnaryOperator getEXPM1();
        DoubleUnaryOperator getFLOOR();
        IntBinaryOperator getFLOORDIV_INT();
        LongBinaryOperator getFLOORDIV_LONG();
        IntBinaryOperator getFLOORMOD_INT();
        LongBinaryOperator getFLOORMOD_LONG();
        FloatToIntFunction getGETEXPONENT_FLOAT();
        DoubleToIntFunction getGETEXPONENT_DOUBLE();
        DoubleBinaryOperator getHYPOT();
        DoubleBinaryOperator getIEEEREMAINDER();
        IntUnaryOperator getINCREMENTEXACT_INT();
        LongUnaryOperator getINCREMENTEXACT_LONG();
        DoubleUnaryOperator getLOG();
        DoubleUnaryOperator getLOG10();
        DoubleUnaryOperator getLOG1P();
        IntBinaryOperator getMAX_INT();
        LongBinaryOperator getMAX_LONG();
        FloatBinaryOperator getMAX_FLOAT();
        DoubleBinaryOperator getMAX_DOUBLE();
        IntBinaryOperator getMIN_INT();
        LongBinaryOperator getMIN_LONG();
        FloatBinaryOperator getMIN_FLOAT();
        DoubleBinaryOperator getMIN_DOUBLE();
        IntBinaryOperator getMULTIPLYEXACT_INT();
        LongBinaryOperator getMULTIPLYEXACT_LONG();
        IntUnaryOperator getNEGATEEXACT_INT();
        LongUnaryOperator getNEGATEEXACT_LONG();
        FloatDouble2FloatOperator getNEXTAFTER_FLOAT();
        DoubleBinaryOperator getNEXTAFTER_DOUBLE();
        FloatUnaryOperator getNEXTDOWN_FLOAT();
        DoubleUnaryOperator getNEXTDOWN_DOUBLE();
        FloatUnaryOperator getNEXTUP_FLOAT();
        DoubleUnaryOperator getNEXTUP_DOUBLE();
        DoubleBinaryOperator getPOW();
        DoubleSupplier getRANDOM();
        DoubleUnaryOperator getRINT();
        DoubleToLongFunction getROUND_DOUBLE();
        FloatToIntFunction getROUND_FLOAT();
        DoubleInt2DoubleOperator getSCALB_DOUBLE();
        FloatInt2FloatOperator getSCALB_FLOAT();
        FloatUnaryOperator getSIGNUM_FLOAT();
        DoubleUnaryOperator getSIGNUM_DOUBLE();
        DoubleUnaryOperator getSIN();
        DoubleUnaryOperator getSINH();
        DoubleUnaryOperator getSQRT();
        IntBinaryOperator getSUBTRACTEXACT_INT();
        LongBinaryOperator getSUBTRACTEXACT_LONG();
        DoubleUnaryOperator getTAN();
        DoubleUnaryOperator getTANH();
        DoubleUnaryOperator getTODEGREES();
        LongToIntFunction getTOINTEXACT();
        DoubleUnaryOperator getTORADIANS();
        DoubleUnaryOperator getULP_DOUBLE();
        FloatUnaryOperator getULP_FLOAT();
    }

    /** {@link AccurateMath Commons Math}. */
    private static class CommonsMathImplementation implements MathImplementation {
        @Override
        public double getPI() {
            return AccurateMath.PI;
        }
        @Override
        public double getE() {
            return AccurateMath.E;
        }
        @Override
        public IntUnaryOperator getABS_INT() {
            return AccurateMath::abs;
        }
        @Override
        public LongUnaryOperator getABS_LONG() {
            return AccurateMath::abs;
        }
        @Override
        public FloatUnaryOperator getABS_FLOAT() {
            return AccurateMath::abs;
        }
        @Override
        public DoubleUnaryOperator getABS_DOUBLE() {
            return AccurateMath::abs;
        }
        @Override
        public DoubleUnaryOperator getACOS() {
            return AccurateMath::acos;
        }

        @Override
        public DoubleUnaryOperator getACOSH() {
            return AccurateMath::acosh;
        }
        @Override
        public IntBinaryOperator getADDEXACT_INT() {
            return AccurateMath::addExact;
        }
        @Override
        public LongBinaryOperator getADDEXACT_LONG() {
            return AccurateMath::addExact;
        }
        @Override
        public DoubleUnaryOperator getASIN() {
            return AccurateMath::asin;
        }
        @Override
        public DoubleUnaryOperator getASINH() {
            return AccurateMath::asinh;
        }
        @Override
        public DoubleUnaryOperator getATAN() {
            return AccurateMath::atan;
        }
        @Override
        public DoubleBinaryOperator getATAN2() {
            return AccurateMath::atan2;
        }
        @Override
        public DoubleUnaryOperator getATANH() {
            return AccurateMath::atanh;
        }
        @Override
        public DoubleUnaryOperator getCBRT() {
            return AccurateMath::cbrt;
        }
        @Override
        public DoubleUnaryOperator getCEIL() {
            return AccurateMath::ceil;
        }
        @Override
        public FloatBinaryOperator getCOPYSIGN_FLOAT() {
            return AccurateMath::copySign;
        }
        @Override
        public DoubleBinaryOperator getCOPYSIGN_DOUBLE() {
            return AccurateMath::copySign;
        }
        @Override
        public DoubleUnaryOperator getCOS() {
            return AccurateMath::cos;
        }
        @Override
        public DoubleUnaryOperator getCOSH() {
            return AccurateMath::cosh;
        }
        @Override
        public IntUnaryOperator getDECREMENTEXACT_INT() {
            return AccurateMath::decrementExact;
        }
        @Override
        public LongUnaryOperator getDECREMENTEXACT_LONG() {
            return AccurateMath::decrementExact;
        }
        @Override
        public DoubleUnaryOperator getEXP() {
            return AccurateMath::exp;
        }
        @Override
        public DoubleUnaryOperator getEXPM1() {
            return AccurateMath::expm1;
        }
        @Override
        public DoubleUnaryOperator getFLOOR() {
            return AccurateMath::floor;
        }

        @Override
        public IntBinaryOperator getFLOORDIV_INT() {
            return AccurateMath::floorDiv;
        }
        @Override
        public LongBinaryOperator getFLOORDIV_LONG() {
            return AccurateMath::floorDiv;
        }
        @Override
        public IntBinaryOperator getFLOORMOD_INT() {
            return AccurateMath::floorMod;
        }
        @Override
        public LongBinaryOperator getFLOORMOD_LONG() {
            return AccurateMath::floorMod;
        }
        @Override
        public FloatToIntFunction getGETEXPONENT_FLOAT() {
            return AccurateMath::getExponent;
        }
        @Override
        public DoubleToIntFunction getGETEXPONENT_DOUBLE() {
            return AccurateMath::getExponent;
        }
        @Override
        public DoubleBinaryOperator getHYPOT() {
            return AccurateMath::hypot;
        }
        @Override
        public DoubleBinaryOperator getIEEEREMAINDER() {
            return AccurateMath::IEEEremainder;
        }
        @Override
        public IntUnaryOperator getINCREMENTEXACT_INT() {
            return AccurateMath::incrementExact;
        }
        @Override
        public LongUnaryOperator getINCREMENTEXACT_LONG() {
            return AccurateMath::incrementExact;
        }
        @Override
        public DoubleUnaryOperator getLOG() {
            return AccurateMath::log;
        }
        @Override
        public DoubleUnaryOperator getLOG10() {
            return AccurateMath::log10;
        }
        @Override
        public DoubleUnaryOperator getLOG1P() {
            return AccurateMath::log1p;
        }
        @Override
        public IntBinaryOperator getMAX_INT() {
            return AccurateMath::max;
        }
        @Override
        public LongBinaryOperator getMAX_LONG() {
            return AccurateMath::max;
        }
        @Override
        public FloatBinaryOperator getMAX_FLOAT() {
            return AccurateMath::max;
        }
        @Override
        public DoubleBinaryOperator getMAX_DOUBLE() {
            return AccurateMath::max;
        }
        @Override
        public IntBinaryOperator getMIN_INT() {
            return AccurateMath::min;
        }
        @Override
        public LongBinaryOperator getMIN_LONG() {
            return AccurateMath::min;
        }
        @Override
        public FloatBinaryOperator getMIN_FLOAT() {
            return AccurateMath::min;
        }
        @Override
        public DoubleBinaryOperator getMIN_DOUBLE() {
            return AccurateMath::min;
        }
        @Override
        public IntBinaryOperator getMULTIPLYEXACT_INT() {
            return AccurateMath::multiplyExact;
        }
        @Override
        public LongBinaryOperator getMULTIPLYEXACT_LONG() {
            return AccurateMath::multiplyExact;
        }
        @Override
        public IntUnaryOperator getNEGATEEXACT_INT() {
            return Math::negateExact;
        }
        @Override
        public LongUnaryOperator getNEGATEEXACT_LONG() {
            return Math::negateExact;
        }
        @Override
        public FloatDouble2FloatOperator getNEXTAFTER_FLOAT() {
            return AccurateMath::nextAfter;
        }
        @Override
        public DoubleBinaryOperator getNEXTAFTER_DOUBLE() {
            return AccurateMath::nextAfter;
        }
        @Override
        public FloatUnaryOperator getNEXTDOWN_FLOAT() {
            return AccurateMath::nextDown;
        }
        @Override
        public DoubleUnaryOperator getNEXTDOWN_DOUBLE() {
            return AccurateMath::nextDown;
        }
        @Override
        public FloatUnaryOperator getNEXTUP_FLOAT() {
            return AccurateMath::nextUp;
        }
        @Override
        public DoubleUnaryOperator getNEXTUP_DOUBLE() {
            return AccurateMath::nextUp;
        }
        @Override
        public DoubleBinaryOperator getPOW() {
            return AccurateMath::pow;
        }
        @Override
        public DoubleSupplier getRANDOM() {
            return Math::random;
        }
        @Override
        public DoubleUnaryOperator getRINT() {
            return AccurateMath::rint;
        }
        @Override
        public DoubleToLongFunction getROUND_DOUBLE() {
            return AccurateMath::round;
        }
        @Override
        public FloatToIntFunction getROUND_FLOAT() {
            return AccurateMath::round;
        }
        @Override
        public DoubleInt2DoubleOperator getSCALB_DOUBLE() {
            return AccurateMath::scalb;
        }
        @Override
        public FloatInt2FloatOperator getSCALB_FLOAT() {
            return AccurateMath::scalb;
        }
        @Override
        public FloatUnaryOperator getSIGNUM_FLOAT() {
            return AccurateMath::signum;
        }
        @Override
        public DoubleUnaryOperator getSIGNUM_DOUBLE() {
            return AccurateMath::signum;
        }
        @Override
        public DoubleUnaryOperator getSIN() {
            return AccurateMath::sin;
        }
        @Override
        public DoubleUnaryOperator getSINH() {
            return AccurateMath::sinh;
        }
        @Override
        public DoubleUnaryOperator getSQRT() {
            return Math::sqrt;
        }
        @Override
        public IntBinaryOperator getSUBTRACTEXACT_INT() {
            return AccurateMath::subtractExact;
        }
        @Override
        public LongBinaryOperator getSUBTRACTEXACT_LONG() {
            return AccurateMath::subtractExact;
        }
        @Override
        public DoubleUnaryOperator getTAN() {
            return AccurateMath::tan;
        }
        @Override
        public DoubleUnaryOperator getTANH() {
            return AccurateMath::tanh;
        }
        @Override
        public DoubleUnaryOperator getTODEGREES() {
            return AccurateMath::toDegrees;
        }
        @Override
        public LongToIntFunction getTOINTEXACT() {
            return AccurateMath::toIntExact;
        }
        @Override
        public DoubleUnaryOperator getTORADIANS() {
            return AccurateMath::toRadians;
        }
        @Override
        public DoubleUnaryOperator getULP_DOUBLE() {
            return AccurateMath::ulp;
        }
        @Override
        public FloatUnaryOperator getULP_FLOAT() {
            return AccurateMath::ulp;
        }
    }

    /** {@link Math JDK}. */
    private static class JdkMathImplementation implements MathImplementation {
        @Override
        public double getPI() {
            return Math.PI;
        }
        @Override
        public double getE() {
            return Math.E;
        }
        @Override
        public IntUnaryOperator getABS_INT() {
            return Math::abs;
        }
        @Override
        public LongUnaryOperator getABS_LONG() {
            return Math::abs;
        }
        @Override
        public FloatUnaryOperator getABS_FLOAT() {
            return Math::abs;
        }
        @Override
        public DoubleUnaryOperator getABS_DOUBLE() {
            return Math::abs;
        }
        @Override
        public DoubleUnaryOperator getACOS() {
            return Math::acos;
        }
        @Override
        public DoubleUnaryOperator getACOSH() {
            return AccurateMath::acosh;
        }
        @Override
        public IntBinaryOperator getADDEXACT_INT() {
            return Math::addExact;
        }
        @Override
        public LongBinaryOperator getADDEXACT_LONG() {
            return Math::addExact;
        }
        @Override
        public DoubleUnaryOperator getASIN() {
            return Math::asin;
        }
        @Override
        public DoubleUnaryOperator getASINH() {
            return AccurateMath::asinh;
        }
        @Override
        public DoubleUnaryOperator getATAN() {
            return Math::atan;
        }
        @Override
        public DoubleBinaryOperator getATAN2() {
            return Math::atan2;
        }
        @Override
        public DoubleUnaryOperator getATANH() {
            return AccurateMath::atanh;
        }
        @Override
        public DoubleUnaryOperator getCBRT() {
            return Math::cbrt;
        }
        @Override
        public DoubleUnaryOperator getCEIL() {
            return Math::ceil;
        }
        @Override
        public FloatBinaryOperator getCOPYSIGN_FLOAT() {
            return Math::copySign;
        }
        @Override
        public DoubleBinaryOperator getCOPYSIGN_DOUBLE() {
            return Math::copySign;
        }
        @Override
        public DoubleUnaryOperator getCOS() {
            return Math::cos;
        }
        @Override
        public DoubleUnaryOperator getCOSH() {
            return Math::cosh;
        }
        @Override
        public IntUnaryOperator getDECREMENTEXACT_INT() {
            return Math::decrementExact;
        }
        @Override
        public LongUnaryOperator getDECREMENTEXACT_LONG() {
            return Math::decrementExact;
        }
        @Override
        public DoubleUnaryOperator getEXP() {
            return Math::exp;
        }
        @Override
        public DoubleUnaryOperator getEXPM1() {
            return Math::expm1;
        }
        @Override
        public DoubleUnaryOperator getFLOOR() {
            return Math::floor;
        }
        @Override
        public IntBinaryOperator getFLOORDIV_INT() {
            return Math::floorDiv;
        }
        @Override
        public LongBinaryOperator getFLOORDIV_LONG() {
            return Math::floorDiv;
        }
        @Override
        public IntBinaryOperator getFLOORMOD_INT() {
            return Math::floorMod;
        }
        @Override
        public LongBinaryOperator getFLOORMOD_LONG() {
            return Math::floorMod;
        }
        @Override
        public FloatToIntFunction getGETEXPONENT_FLOAT() {
            return Math::getExponent;
        }
        @Override
        public DoubleToIntFunction getGETEXPONENT_DOUBLE() {
            return Math::getExponent;
        }
        @Override
        public DoubleBinaryOperator getHYPOT() {
            return Math::hypot;
        }
        @Override
        public DoubleBinaryOperator getIEEEREMAINDER() {
            return Math::IEEEremainder;
        }
        @Override
        public IntUnaryOperator getINCREMENTEXACT_INT() {
            return Math::incrementExact;
        }
        @Override
        public LongUnaryOperator getINCREMENTEXACT_LONG() {
            return Math::incrementExact;
        }
        @Override
        public DoubleUnaryOperator getLOG() {
            return Math::log;
        }
        @Override
        public DoubleUnaryOperator getLOG10() {
            return Math::log10;
        }
        @Override
        public DoubleUnaryOperator getLOG1P() {
            return Math::log1p;
        }
        @Override
        public IntBinaryOperator getMAX_INT() {
            return Math::max;
        }
        @Override
        public LongBinaryOperator getMAX_LONG() {
            return Math::max;
        }
        @Override
        public FloatBinaryOperator getMAX_FLOAT() {
            return Math::max;
        }
        @Override
        public DoubleBinaryOperator getMAX_DOUBLE() {
            return Math::max;
        }
        @Override
        public IntBinaryOperator getMIN_INT() {
            return Math::min;
        }
        @Override
        public LongBinaryOperator getMIN_LONG() {
            return Math::min;
        }
        @Override
        public FloatBinaryOperator getMIN_FLOAT() {
            return Math::min;
        }
        @Override
        public DoubleBinaryOperator getMIN_DOUBLE() {
            return Math::min;
        }
        @Override
        public IntBinaryOperator getMULTIPLYEXACT_INT() {
            return Math::multiplyExact;
        }
        @Override
        public LongBinaryOperator getMULTIPLYEXACT_LONG() {
            return Math::multiplyExact;
        }
        @Override
        public IntUnaryOperator getNEGATEEXACT_INT() {
            return Math::negateExact;
        }
        @Override
        public LongUnaryOperator getNEGATEEXACT_LONG() {
            return Math::negateExact;
        }
        @Override
        public FloatDouble2FloatOperator getNEXTAFTER_FLOAT() {
            return Math::nextAfter;
        }
        @Override
        public DoubleBinaryOperator getNEXTAFTER_DOUBLE() {
            return Math::nextAfter;
        }
        @Override
        public FloatUnaryOperator getNEXTDOWN_FLOAT() {
            return Math::nextDown;
        }
        @Override
        public DoubleUnaryOperator getNEXTDOWN_DOUBLE() {
            return Math::nextDown;
        }
        @Override
        public FloatUnaryOperator getNEXTUP_FLOAT() {
            return Math::nextUp;
        }
        @Override
        public DoubleUnaryOperator getNEXTUP_DOUBLE() {
            return Math::nextUp;
        }
        @Override
        public DoubleBinaryOperator getPOW() {
            return Math::pow;
        }
        @Override
        public DoubleSupplier getRANDOM() {
            return Math::random;
        }
        @Override
        public DoubleUnaryOperator getRINT() {
            return Math::rint;
        }
        @Override
        public DoubleToLongFunction getROUND_DOUBLE() {
            return Math::round;
        }
        @Override
        public FloatToIntFunction getROUND_FLOAT() {
            return Math::round;
        }
        @Override
        public DoubleInt2DoubleOperator getSCALB_DOUBLE() {
            return Math::scalb;
        }
        @Override
        public FloatInt2FloatOperator getSCALB_FLOAT() {
            return Math::scalb;
        }
        @Override
        public FloatUnaryOperator getSIGNUM_FLOAT() {
            return Math::signum;
        }
        @Override
        public DoubleUnaryOperator getSIGNUM_DOUBLE() {
            return Math::signum;
        }
        @Override
        public DoubleUnaryOperator getSIN() {
            return Math::sin;
        }
        @Override
        public DoubleUnaryOperator getSINH() {
            return Math::sinh;
        }
        @Override
        public DoubleUnaryOperator getSQRT() {
            return Math::sqrt;
        }
        @Override
        public IntBinaryOperator getSUBTRACTEXACT_INT() {
            return Math::subtractExact;
        }
        @Override
        public LongBinaryOperator getSUBTRACTEXACT_LONG() {
            return Math::subtractExact;
        }
        @Override
        public DoubleUnaryOperator getTAN() {
            return Math::tan;
        }
        @Override
        public DoubleUnaryOperator getTANH() {
            return Math::tanh;
        }
        @Override
        public DoubleUnaryOperator getTODEGREES() {
            return Math::toDegrees;
        }
        @Override
        public LongToIntFunction getTOINTEXACT() {
            return Math::toIntExact;
        }
        @Override
        public DoubleUnaryOperator getTORADIANS() {
            return Math::toRadians;
        }
        @Override
        public DoubleUnaryOperator getULP_DOUBLE() {
            return Math::ulp;
        }
        @Override
        public FloatUnaryOperator getULP_FLOAT() {
            return Math::ulp;
        }
    }

    /**
     * Sets the implementation type.
     * @return the MathImplementation object.
     */
    private static MathImplementation getMathImplementation() {
        final String prop = System.getProperty(PROPERTY_KEY);
        return prop != null && prop.equalsIgnoreCase("JDK") ?
                new JdkMathImplementation() :
                DEFAULT_IMPLEMENTATION;
    }

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
