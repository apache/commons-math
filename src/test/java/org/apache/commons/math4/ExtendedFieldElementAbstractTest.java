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
package org.apache.commons.math4;

import org.apache.commons.math4.RealFieldElement;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well1024a;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;

public abstract class ExtendedFieldElementAbstractTest<T extends RealFieldElement<T>> {

    protected abstract T build(double x);

    @Test
    public void testAddField() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(x + y, build(x).add(build(y)));
            }
        }
    }

    @Test
    public void testAddDouble() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(x + y, build(x).add(y));
            }
        }
    }

    @Test
    public void testSubtractField() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(x - y, build(x).subtract(build(y)));
            }
        }
    }

    @Test
    public void testSubtractDouble() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(x - y, build(x).subtract(y));
            }
        }
    }

    @Test
    public void testMultiplyField() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(x * y, build(x).multiply(build(y)));
            }
        }
    }

    @Test
    public void testMultiplyDouble() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(x * y, build(x).multiply(y));
            }
        }
    }

    @Test
    public void testMultiplyInt() {
        for (double x = -3; x < 3; x += 0.2) {
            for (int y = -10; y < 10; y += 1) {
                checkRelative(x * y, build(x).multiply(y));
            }
        }
    }

    @Test
    public void testDivideField() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(x / y, build(x).divide(build(y)));
            }
        }
    }

    @Test
    public void testDivideDouble() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                    checkRelative(x / y, build(x).divide(y));
            }
        }
    }

    @Test
    public void testRemainderField() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(FastMath.IEEEremainder(x, y), build(x).remainder(build(y)));
            }
        }
    }

    @Test
    public void testRemainderDouble() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3.2; y < 3.2; y += 0.25) {
                checkRelative(FastMath.IEEEremainder(x, y), build(x).remainder(y));
            }
        }
    }

    @Test
    public void testCos() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.cos(x), build(x).cos());
        }
    }

    @Test
    public void testAcos() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.acos(x), build(x).acos());
        }
    }

    @Test
    public void testSin() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.sin(x), build(x).sin());
        }
    }

    @Test
    public void testAsin() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.asin(x), build(x).asin());
        }
    }

    @Test
    public void testTan() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.tan(x), build(x).tan());
        }
    }

    @Test
    public void testAtan() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.atan(x), build(x).atan());
        }
    }

    @Test
    public void testAtan2() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(FastMath.atan2(x, y), build(x).atan2(build(y)));
            }
        }
    }

    @Test
    public void testCosh() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.cosh(x), build(x).cosh());
        }
    }

    @Test
    public void testAcosh() {
        for (double x = 1.1; x < 5.0; x += 0.05) {
            checkRelative(FastMath.acosh(x), build(x).acosh());
        }
    }

    @Test
    public void testSinh() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.sinh(x), build(x).sinh());
        }
    }

    @Test
    public void testAsinh() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.asinh(x), build(x).asinh());
        }
    }

    @Test
    public void testTanh() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.tanh(x), build(x).tanh());
        }
    }

    @Test
    public void testAtanh() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.atanh(x), build(x).atanh());
        }
    }

    @Test
    public void testSqrt() {
        for (double x = 0.01; x < 0.9; x += 0.05) {
            checkRelative(FastMath.sqrt(x), build(x).sqrt());
        }
    }

    @Test
    public void testCbrt() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.cbrt(x), build(x).cbrt());
        }
    }

    @Test
    public void testHypot() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(FastMath.hypot(x, y), build(x).hypot(build(y)));
            }
        }
    }

    @Test
    public void testRootN() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            for (int n = 1; n < 5; ++n) {
                if (x < 0) {
                    if (n % 2 == 1) {
                        checkRelative(-FastMath.pow(-x, 1.0 / n), build(x).rootN(n));
                    }
                } else {
                    checkRelative(FastMath.pow(x, 1.0 / n), build(x).rootN(n));
                }
            }
        }
    }

    @Test
    public void testPowField() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            for (double y = 0.1; y < 4; y += 0.2) {
                checkRelative(FastMath.pow(x, y), build(x).pow(build(y)));
            }
        }
    }

    @Test
    public void testPowDouble() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            for (double y = 0.1; y < 4; y += 0.2) {
                checkRelative(FastMath.pow(x, y), build(x).pow(y));
            }
        }
    }

    @Test
    public void testPowInt() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            for (int n = 0; n < 5; ++n) {
                checkRelative(FastMath.pow(x, n), build(x).pow(n));
            }
        }
    }

    @Test
    public void testExp() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.exp(x), build(x).exp());
        }
    }

    @Test
    public void testExpm1() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.expm1(x), build(x).expm1());
        }
    }

    @Test
    public void testLog() {
        for (double x = 0.01; x < 0.9; x += 0.05) {
            checkRelative(FastMath.log(x), build(x).log());
        }
    }

    @Test
    public void testLog1p() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.log1p(x), build(x).log1p());
        }
    }

//  TODO: add this test in 4.0, as it is not possible to do it in 3.2
//  due to incompatibility of the return type in the Dfp class
//    @Test
//    public void testLog10() {
//        for (double x = -0.9; x < 0.9; x += 0.05) {
//            checkRelative(FastMath.log10(x), build(x).log10());
//        }
//    }

    @Test
    public void testAbs() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.abs(x), build(x).abs());
        }
    }

    @Test
    public void testCeil() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.ceil(x), build(x).ceil());
        }
    }

    @Test
    public void testFloor() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.floor(x), build(x).floor());
        }
    }

    @Test
    public void testRint() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.rint(x), build(x).rint());
        }
    }

    @Test
    public void testRound() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            Assert.assertEquals(FastMath.round(x), build(x).round());
        }
    }

    @Test
    public void testSignum() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(FastMath.signum(x), build(x).signum());
        }
    }

    @Test
    public void testCopySignField() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(FastMath.copySign(x, y), build(x).copySign(build(y)));
            }
        }
    }

    @Test
    public void testCopySignDouble() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(FastMath.copySign(x, y), build(x).copySign(y));
            }
        }
    }

    @Test
    public void testScalb() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            for (int n = -100; n < 100; ++n) {
                checkRelative(FastMath.scalb(x, n), build(x).scalb(n));
            }
        }
    }

    @Test
    public void testLinearCombinationFaFa() {
        RandomGenerator r = new Well1024a(0xfafal);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 10);
            double[] bD = generateDouble(r, 10);
            T[] aF      = toFieldArray(aD);
            T[] bF      = toFieldArray(bD);
            checkRelative(MathArrays.linearCombination(aD, bD),
                          aF[0].linearCombination(aF, bF));
        }
    }

    @Test
    public void testLinearCombinationDaFa() {
        RandomGenerator r = new Well1024a(0xdafal);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 10);
            double[] bD = generateDouble(r, 10);
            T[] bF      = toFieldArray(bD);
            checkRelative(MathArrays.linearCombination(aD, bD),
                          bF[0].linearCombination(aD, bF));
        }
    }

    @Test
    public void testLinearCombinationFF2() {
        RandomGenerator r = new Well1024a(0xff2l);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 2);
            double[] bD = generateDouble(r, 2);
            T[] aF      = toFieldArray(aD);
            T[] bF      = toFieldArray(bD);
            checkRelative(MathArrays.linearCombination(aD[0], bD[0], aD[1], bD[1]),
                          aF[0].linearCombination(aF[0], bF[0], aF[1], bF[1]));
        }
    }

    @Test
    public void testLinearCombinationDF2() {
        RandomGenerator r = new Well1024a(0xdf2l);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 2);
            double[] bD = generateDouble(r, 2);
            T[] bF      = toFieldArray(bD);
            checkRelative(MathArrays.linearCombination(aD[0], bD[0], aD[1], bD[1]),
                          bF[0].linearCombination(aD[0], bF[0], aD[1], bF[1]));
        }
    }

    @Test
    public void testLinearCombinationFF3() {
        RandomGenerator r = new Well1024a(0xff3l);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 3);
            double[] bD = generateDouble(r, 3);
            T[] aF      = toFieldArray(aD);
            T[] bF      = toFieldArray(bD);
            checkRelative(MathArrays.linearCombination(aD[0], bD[0], aD[1], bD[1], aD[2], bD[2]),
                          aF[0].linearCombination(aF[0], bF[0], aF[1], bF[1], aF[2], bF[2]));
        }
    }

    @Test
    public void testLinearCombinationDF3() {
        RandomGenerator r = new Well1024a(0xdf3l);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 3);
            double[] bD = generateDouble(r, 3);
            T[] bF      = toFieldArray(bD);
            checkRelative(MathArrays.linearCombination(aD[0], bD[0], aD[1], bD[1], aD[2], bD[2]),
                          bF[0].linearCombination(aD[0], bF[0], aD[1], bF[1], aD[2], bF[2]));
        }
    }

    @Test
    public void testLinearCombinationFF4() {
        RandomGenerator r = new Well1024a(0xff4l);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 4);
            double[] bD = generateDouble(r, 4);
            T[] aF      = toFieldArray(aD);
            T[] bF      = toFieldArray(bD);
            checkRelative(MathArrays.linearCombination(aD[0], bD[0], aD[1], bD[1], aD[2], bD[2], aD[3], bD[3]),
                          aF[0].linearCombination(aF[0], bF[0], aF[1], bF[1], aF[2], bF[2], aF[3], bF[3]));
        }
    }

    @Test
    public void testLinearCombinationDF4() {
        RandomGenerator r = new Well1024a(0xdf4l);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 4);
            double[] bD = generateDouble(r, 4);
            T[] bF      = toFieldArray(bD);
            checkRelative(MathArrays.linearCombination(aD[0], bD[0], aD[1], bD[1], aD[2], bD[2], aD[3], bD[3]),
                          bF[0].linearCombination(aD[0], bF[0], aD[1], bF[1], aD[2], bF[2], aD[3], bF[3]));
        }
    }

    @Test
    public void testGetField() {
        checkRelative(1.0, build(-10).getField().getOne());
        checkRelative(0.0, build(-10).getField().getZero());
    }

    private void checkRelative(double expected, T obtained) {
        Assert.assertEquals(expected, obtained.getReal(), 1.0e-15 * (1 + FastMath.abs(expected)));
    }

    @Test
    public void testEquals() {
        T t1a = build(1.0);
        T t1b = build(1.0);
        T t2  = build(2.0);
        Assert.assertTrue(t1a.equals(t1a));
        Assert.assertTrue(t1a.equals(t1b));
        Assert.assertFalse(t1a.equals(t2));
        Assert.assertFalse(t1a.equals(new Object()));
    }

    @Test
    public void testHash() {
        T t1a = build(1.0);
        T t1b = build(1.0);
        T t2  = build(2.0);
        Assert.assertEquals(t1a.hashCode(), t1b.hashCode());
        Assert.assertTrue(t1a.hashCode() != t2.hashCode());
    }

    private double[] generateDouble (final RandomGenerator r, int n) {
        double[] a = new double[n];
        for (int i = 0; i < n; ++i) {
            a[i] = r.nextDouble();
        }
        return a;
    }

    private T[] toFieldArray (double[] a) {
        T[] f = MathArrays.buildArray(build(0).getField(), a.length);
        for (int i = 0; i < a.length; ++i) {
            f[i] = build(a[i]);
        }
        return f;
    }

}
