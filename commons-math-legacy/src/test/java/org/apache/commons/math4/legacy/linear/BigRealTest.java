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
package org.apache.commons.math4.legacy.linear;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.exception.MathArithmeticException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

public class BigRealTest {

    @Test
    public void testConstructor() {
        Assert.assertEquals(1.625,
                            new BigReal(new BigDecimal("1.625")).doubleValue(),
                            1.0e-15);
        Assert.assertEquals(-5.0,
                            new BigReal(new BigInteger("-5")).doubleValue(),
                            1.0e-15);
        Assert.assertEquals(-5.0, new BigReal(new BigInteger("-5"),
                                              MathContext.DECIMAL64)
            .doubleValue(), 1.0e-15);
        Assert
            .assertEquals(0.125,
                          new BigReal(new BigInteger("125"), 3).doubleValue(),
                          1.0e-15);
        Assert.assertEquals(0.125, new BigReal(new BigInteger("125"), 3,
                                               MathContext.DECIMAL64)
            .doubleValue(), 1.0e-15);
        Assert.assertEquals(1.625, new BigReal(new char[] {
            '1', '.', '6', '2', '5'
        }).doubleValue(), 1.0e-15);
        Assert.assertEquals(1.625, new BigReal(new char[] {
            'A', 'A', '1', '.', '6', '2', '5', '9'
        }, 2, 5).doubleValue(), 1.0e-15);
        Assert.assertEquals(1.625, new BigReal(new char[] {
            'A', 'A', '1', '.', '6', '2', '5', '9'
        }, 2, 5, MathContext.DECIMAL64).doubleValue(), 1.0e-15);
        Assert.assertEquals(1.625, new BigReal(new char[] {
            '1', '.', '6', '2', '5'
        }, MathContext.DECIMAL64).doubleValue(), 1.0e-15);
        Assert.assertEquals(1.625, new BigReal(1.625).doubleValue(), 1.0e-15);
        Assert.assertEquals(1.625, new BigReal(1.625, MathContext.DECIMAL64)
            .doubleValue(), 1.0e-15);
        Assert.assertEquals(-5.0, new BigReal(-5).doubleValue(), 1.0e-15);
        Assert.assertEquals(-5.0, new BigReal(-5, MathContext.DECIMAL64)
            .doubleValue(), 1.0e-15);
        Assert.assertEquals(-5.0, new BigReal(-5L).doubleValue(), 1.0e-15);
        Assert.assertEquals(-5.0, new BigReal(-5L, MathContext.DECIMAL64)
            .doubleValue(), 1.0e-15);
        Assert.assertEquals(1.625, new BigReal("1.625").doubleValue(), 1.0e-15);
        Assert.assertEquals(1.625, new BigReal("1.625", MathContext.DECIMAL64)
            .doubleValue(), 1.0e-15);
    }

    @Test
    public void testCompareTo() {
        BigReal first = new BigReal(1.0 / 2.0);
        BigReal second = new BigReal(1.0 / 3.0);
        BigReal third = new BigReal(1.0 / 2.0);

        Assert.assertEquals(0, first.compareTo(first));
        Assert.assertEquals(0, first.compareTo(third));
        Assert.assertEquals(1, first.compareTo(second));
        Assert.assertEquals(-1, second.compareTo(first));
    }

    @Test
    public void testAdd() {
        BigReal a = new BigReal("1.2345678");
        BigReal b = new BigReal("8.7654321");
        Assert.assertEquals(9.9999999, a.add(b).doubleValue(), 1.0e-15);
    }

    @Test
    public void testSubtract() {
        BigReal a = new BigReal("1.2345678");
        BigReal b = new BigReal("8.7654321");
        Assert.assertEquals(-7.5308643, a.subtract(b).doubleValue(), 1.0e-15);
    }

    @Test
    public void testNegate() {
        BigReal a = new BigReal("1.2345678");
        BigReal zero = new BigReal("0.0000000");
        Assert.assertEquals(a.negate().add(a), zero);
        Assert.assertEquals(a.add(a.negate()), zero);
        Assert.assertEquals(zero, zero.negate());
    }

    @Test
    public void testDivide() {
        BigReal a = new BigReal("1.0000000000");
        BigReal b = new BigReal("0.0009765625");
        Assert.assertEquals(1024.0, a.divide(b).doubleValue(), 1.0e-15);
    }

    @Test(expected = MathArithmeticException.class)
    public void testDivisionByZero() {
        final BigReal a = BigReal.ONE;
        final BigReal b = BigReal.ZERO;
        a.divide(b);
    }

    @Test
    public void testReciprocal() {
        BigReal a = new BigReal("1.2345678");
        double eps = JdkMath.pow(10., -a.getScale());
        BigReal one = new BigReal("1.0000000");
        BigReal b = a.reciprocal();
        BigReal r = one.subtract(a.multiply(b));
        Assert.assertTrue(JdkMath.abs(r.doubleValue()) <= eps);
        r = one.subtract(b.multiply(a));
        Assert.assertTrue(JdkMath.abs(r.doubleValue()) <= eps);
    }

    @Test(expected = MathArithmeticException.class)
    public void testReciprocalOfZero() {
        BigReal.ZERO.reciprocal();
    }

    @Test
    public void testMultiply() {
        BigReal a = new BigReal("1024.0");
        BigReal b = new BigReal("0.0009765625");
        Assert.assertEquals(1.0, a.multiply(b).doubleValue(), 1.0e-15);
        int n = 1024;
        Assert.assertEquals(1.0, b.multiply(n).doubleValue(), 1.0e-15);
    }

    @Test
    public void testDoubleValue() {
        Assert.assertEquals(0.5, new BigReal(0.5).doubleValue(), 1.0e-15);
    }

    @Test
    public void testBigDecimalValue() {
        BigDecimal pi = new BigDecimal(
                                       "3.1415926535897932384626433832795028841971693993751");
        Assert.assertEquals(pi, new BigReal(pi).bigDecimalValue());
        Assert.assertEquals(new BigDecimal(0.5),
                            new BigReal(1.0 / 2.0).bigDecimalValue());
    }

    @Test
    public void testEqualsAndHashCode() {
        BigReal zero = new BigReal(0.0);
        BigReal nullReal = null;
        Assert.assertEquals(zero, zero);
        Assert.assertNotEquals(zero, nullReal);
        Assert.assertFalse(zero.equals(Double.valueOf(0)));
        BigReal zero2 = new BigReal(0.0);
        Assert.assertEquals(zero, zero2);
        Assert.assertEquals(zero.hashCode(), zero2.hashCode());
        BigReal one = new BigReal(1.0);
        Assert.assertFalse(one.equals(zero) || zero.equals(one));
        Assert.assertEquals(one, BigReal.ONE);
        BigReal oneWithScaleOne = new BigReal(new BigDecimal("1.0"));
        BigReal oneWithScaleTwo = new BigReal(new BigDecimal("1.00"));
        Assert.assertEquals(oneWithScaleOne, oneWithScaleTwo);
        Assert.assertEquals(oneWithScaleOne.hashCode(), oneWithScaleTwo.hashCode());
    }

    @Test
    public void testSerial() {
        BigReal[] Reals = {
            new BigReal(3.0), BigReal.ONE, BigReal.ZERO, new BigReal(17),
            new BigReal(JdkMath.PI), new BigReal(-2.5)
        };
        for (BigReal Real : Reals) {
            Assert.assertEquals(Real, TestUtils.serializeAndRecover(Real));
        }
    }
}
