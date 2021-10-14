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
package org.apache.commons.math4.legacy.field;

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.numbers.core.Sum;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.legacy.core.RealFieldElement;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.math4.core.jdkmath.JdkMath;

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
                checkRelative(JdkMath.IEEEremainder(x, y), build(x).remainder(build(y)));
            }
        }
    }

    @Test
    public void testRemainderDouble() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3.2; y < 3.2; y += 0.25) {
                checkRelative(JdkMath.IEEEremainder(x, y), build(x).remainder(y));
            }
        }
    }

    @Test
    public void testCos() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.cos(x), build(x).cos());
        }
    }

    @Test
    public void testAcos() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.acos(x), build(x).acos());
        }
    }

    @Test
    public void testSin() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.sin(x), build(x).sin());
        }
    }

    @Test
    public void testAsin() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.asin(x), build(x).asin());
        }
    }

    @Test
    public void testTan() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.tan(x), build(x).tan());
        }
    }

    @Test
    public void testAtan() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.atan(x), build(x).atan());
        }
    }

    @Test
    public void testAtan2() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(JdkMath.atan2(x, y), build(x).atan2(build(y)));
            }
        }
    }

    @Test
    public void testCosh() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.cosh(x), build(x).cosh());
        }
    }

    @Test
    public void testAcosh() {
        for (double x = 1.1; x < 5.0; x += 0.05) {
            checkRelative(JdkMath.acosh(x), build(x).acosh());
        }
    }

    @Test
    public void testSinh() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.sinh(x), build(x).sinh());
        }
    }

    @Test
    public void testAsinh() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.asinh(x), build(x).asinh());
        }
    }

    @Test
    public void testTanh() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.tanh(x), build(x).tanh());
        }
    }

    @Test
    public void testAtanh() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.atanh(x), build(x).atanh());
        }
    }

    @Test
    public void testSqrt() {
        for (double x = 0.01; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.sqrt(x), build(x).sqrt());
        }
    }

    @Test
    public void testCbrt() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.cbrt(x), build(x).cbrt());
        }
    }

    @Test
    public void testHypot() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(JdkMath.hypot(x, y), build(x).hypot(build(y)));
            }
        }
    }

    @Test
    public void testRootN() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            for (int n = 1; n < 5; ++n) {
                if (x < 0) {
                    if (n % 2 == 1) {
                        checkRelative(-JdkMath.pow(-x, 1.0 / n), build(x).rootN(n));
                    }
                } else {
                    checkRelative(JdkMath.pow(x, 1.0 / n), build(x).rootN(n));
                }
            }
        }
    }

    @Test
    public void testPowField() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            for (double y = 0.1; y < 4; y += 0.2) {
                checkRelative(JdkMath.pow(x, y), build(x).pow(build(y)));
            }
        }
    }

    @Test
    public void testPowDouble() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            for (double y = 0.1; y < 4; y += 0.2) {
                checkRelative(JdkMath.pow(x, y), build(x).pow(y));
            }
        }
    }

    @Test
    public void testPowInt() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            for (int n = 0; n < 5; ++n) {
                checkRelative(JdkMath.pow(x, n), build(x).pow(n));
            }
        }
    }

    @Test
    public void testExp() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.exp(x), build(x).exp());
        }
    }

    @Test
    public void testExpm1() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.expm1(x), build(x).expm1());
        }
    }

    @Test
    public void testLog() {
        for (double x = 0.01; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.log(x), build(x).log());
        }
    }

    @Test
    public void testLog1p() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.log1p(x), build(x).log1p());
        }
    }

    @Test
    public void testLog10() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.log10(x), build(x).log10());
        }
    }

    @Test
    public void testAbs() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.abs(x), build(x).abs());
        }
    }

    @Test
    public void testCeil() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.ceil(x), build(x).ceil());
        }
    }

    @Test
    public void testFloor() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.floor(x), build(x).floor());
        }
    }

    @Test
    public void testRint() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.rint(x), build(x).rint());
        }
    }

    @Test
    public void testRound() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            Assert.assertEquals(JdkMath.round(x), build(x).round());
        }
    }

    @Test
    public void testSignum() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            checkRelative(JdkMath.signum(x), build(x).signum());
        }
    }

    @Test
    public void testCopySignField() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(JdkMath.copySign(x, y), build(x).copySign(build(y)));
            }
        }
    }

    @Test
    public void testCopySignDouble() {
        for (double x = -3; x < 3; x += 0.2) {
            for (double y = -3; y < 3; y += 0.2) {
                checkRelative(JdkMath.copySign(x, y), build(x).copySign(y));
            }
        }
    }

    @Test
    public void testScalb() {
        for (double x = -0.9; x < 0.9; x += 0.05) {
            for (int n = -100; n < 100; ++n) {
                checkRelative(JdkMath.scalb(x, n), build(x).scalb(n));
            }
        }
    }

    @Test
    public void testLinearCombinationFaFa() {
        UniformRandomProvider r = RandomSource.WELL_1024_A.create(0xfafaL);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 10);
            double[] bD = generateDouble(r, 10);
            T[] aF      = toFieldArray(aD);
            T[] bF      = toFieldArray(bD);
            checkRelative(Sum.ofProducts(aD, bD).getAsDouble(),
                          aF[0].linearCombination(aF, bF));
        }
    }

    @Test
    public void testLinearCombinationDaFa() {
        UniformRandomProvider r = RandomSource.WELL_1024_A.create(0xdafaL);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 10);
            double[] bD = generateDouble(r, 10);
            T[] bF      = toFieldArray(bD);
            checkRelative(Sum.ofProducts(aD, bD).getAsDouble(),
                          bF[0].linearCombination(aD, bF));
        }
    }

    @Test
    public void testLinearCombinationFF2() {
        UniformRandomProvider r = RandomSource.WELL_1024_A.create(0xff2L);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 2);
            double[] bD = generateDouble(r, 2);
            T[] aF      = toFieldArray(aD);
            T[] bF      = toFieldArray(bD);
            checkRelative(Sum.create()
                          .addProduct(aD[0], bD[0])
                          .addProduct(aD[1], bD[1]).getAsDouble(),
                          aF[0].linearCombination(aF[0], bF[0], aF[1], bF[1]));
        }
    }

    @Test
    public void testLinearCombinationDF2() {
        UniformRandomProvider r = RandomSource.WELL_1024_A.create(0xdf2L);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 2);
            double[] bD = generateDouble(r, 2);
            T[] bF      = toFieldArray(bD);
            checkRelative(Sum.create()
                          .addProduct(aD[0], bD[0])
                          .addProduct(aD[1], bD[1]).getAsDouble(),
                          bF[0].linearCombination(aD[0], bF[0], aD[1], bF[1]));
        }
    }

    @Test
    public void testLinearCombinationFF3() {
        UniformRandomProvider r = RandomSource.WELL_1024_A.create(0xff3L);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 3);
            double[] bD = generateDouble(r, 3);
            T[] aF      = toFieldArray(aD);
            T[] bF      = toFieldArray(bD);
            checkRelative(Sum.create()
                          .addProduct(aD[0], bD[0])
                          .addProduct(aD[1], bD[1])
                          .addProduct(aD[2], bD[2]).getAsDouble(),
                          aF[0].linearCombination(aF[0], bF[0], aF[1], bF[1], aF[2], bF[2]));
        }
    }

    @Test
    public void testLinearCombinationDF3() {
        UniformRandomProvider r = RandomSource.WELL_1024_A.create(0xdf3L);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 3);
            double[] bD = generateDouble(r, 3);
            T[] bF      = toFieldArray(bD);
            checkRelative(Sum.create()
                          .addProduct(aD[0], bD[0])
                          .addProduct(aD[1], bD[1])
                          .addProduct(aD[2], bD[2]).getAsDouble(),
                          bF[0].linearCombination(aD[0], bF[0], aD[1], bF[1], aD[2], bF[2]));
        }
    }

    @Test
    public void testLinearCombinationFF4() {
        UniformRandomProvider r = RandomSource.WELL_1024_A.create(0xff4L);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 4);
            double[] bD = generateDouble(r, 4);
            T[] aF      = toFieldArray(aD);
            T[] bF      = toFieldArray(bD);
            checkRelative(Sum.create()
                          .addProduct(aD[0], bD[0])
                          .addProduct(aD[1], bD[1])
                          .addProduct(aD[2], bD[2])
                          .addProduct(aD[3], bD[3]).getAsDouble(),
                          aF[0].linearCombination(aF[0], bF[0], aF[1], bF[1], aF[2], bF[2], aF[3], bF[3]));
        }
    }

    @Test
    public void testLinearCombinationDF4() {
        UniformRandomProvider r = RandomSource.WELL_1024_A.create(0xdf4L);
        for (int i = 0; i < 50; ++i) {
            double[] aD = generateDouble(r, 4);
            double[] bD = generateDouble(r, 4);
            T[] bF      = toFieldArray(bD);
            checkRelative(Sum.create()
                          .addProduct(aD[0], bD[0])
                          .addProduct(aD[1], bD[1])
                          .addProduct(aD[2], bD[2])
                          .addProduct(aD[3], bD[3]).getAsDouble(),
                          bF[0].linearCombination(aD[0], bF[0], aD[1], bF[1], aD[2], bF[2], aD[3], bF[3]));
        }
    }

    @Test
    public void testGetField() {
        checkRelative(1.0, build(-10).getField().getOne());
        checkRelative(0.0, build(-10).getField().getZero());
    }

    private void checkRelative(double expected, T obtained) {
        Assert.assertEquals(expected, obtained.getReal(), 1.0e-15 * (1 + JdkMath.abs(expected)));
    }

    @Test
    public void testEquals() {
        T t1a = build(1.0);
        T t1b = build(1.0);
        T t2  = build(2.0);
        Assert.assertEquals(t1a, t1a);
        Assert.assertEquals(t1a, t1b);
        Assert.assertNotEquals(t1a, t2);
        Assert.assertNotEquals(t1a, new Object());
    }

    @Test
    public void testHash() {
        T t1a = build(1.0);
        T t1b = build(1.0);
        T t2  = build(2.0);
        Assert.assertEquals(t1a.hashCode(), t1b.hashCode());
        Assert.assertTrue(t1a.hashCode() != t2.hashCode());
    }

    private double[] generateDouble (final UniformRandomProvider r, int n) {
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
