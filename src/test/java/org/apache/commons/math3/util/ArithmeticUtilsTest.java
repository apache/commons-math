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
package org.apache.commons.math3.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.math.BigInteger;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link ArithmeticUtils} class.
 *
 */
public class ArithmeticUtilsTest {

    @Test
    public void testAddAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, ArithmeticUtils.addAndCheck(big, 0));
        try {
            ArithmeticUtils.addAndCheck(big, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            ArithmeticUtils.addAndCheck(bigNeg, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }

    @Test
    public void testAddAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, ArithmeticUtils.addAndCheck(max, 0L));
        Assert.assertEquals(min, ArithmeticUtils.addAndCheck(min, 0L));
        Assert.assertEquals(max, ArithmeticUtils.addAndCheck(0L, max));
        Assert.assertEquals(min, ArithmeticUtils.addAndCheck(0L, min));
        Assert.assertEquals(1, ArithmeticUtils.addAndCheck(-1L, 2L));
        Assert.assertEquals(1, ArithmeticUtils.addAndCheck(2L, -1L));
        Assert.assertEquals(-3, ArithmeticUtils.addAndCheck(-2L, -1L));
        Assert.assertEquals(min, ArithmeticUtils.addAndCheck(min + 1, -1L));
        Assert.assertEquals(-1, ArithmeticUtils.addAndCheck(min, max));
        testAddAndCheckLongFailure(max, 1L);
        testAddAndCheckLongFailure(min, -1L);
        testAddAndCheckLongFailure(1L, max);
        testAddAndCheckLongFailure(-1L, min);
        testAddAndCheckLongFailure(max, max);
        testAddAndCheckLongFailure(min, min);
    }

    @Test
    public void testGcd() {
        int a = 30;
        int b = 50;
        int c = 77;

        Assert.assertEquals(0, ArithmeticUtils.gcd(0, 0));

        Assert.assertEquals(b, ArithmeticUtils.gcd(0, b));
        Assert.assertEquals(a, ArithmeticUtils.gcd(a, 0));
        Assert.assertEquals(b, ArithmeticUtils.gcd(0, -b));
        Assert.assertEquals(a, ArithmeticUtils.gcd(-a, 0));

        Assert.assertEquals(10, ArithmeticUtils.gcd(a, b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(-a, b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(a, -b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(-a, -b));

        Assert.assertEquals(1, ArithmeticUtils.gcd(a, c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(-a, c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(a, -c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(-a, -c));

        Assert.assertEquals(3 * (1<<15), ArithmeticUtils.gcd(3 * (1<<20), 9 * (1<<15)));

        Assert.assertEquals(Integer.MAX_VALUE, ArithmeticUtils.gcd(Integer.MAX_VALUE, 0));
        Assert.assertEquals(Integer.MAX_VALUE, ArithmeticUtils.gcd(-Integer.MAX_VALUE, 0));
        Assert.assertEquals(1<<30, ArithmeticUtils.gcd(1<<30, -Integer.MIN_VALUE));
        try {
            // gcd(Integer.MIN_VALUE, 0) > Integer.MAX_VALUE
            ArithmeticUtils.gcd(Integer.MIN_VALUE, 0);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Integer.MIN_VALUE) > Integer.MAX_VALUE
            ArithmeticUtils.gcd(0, Integer.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Integer.MIN_VALUE, Integer.MIN_VALUE) > Integer.MAX_VALUE
            ArithmeticUtils.gcd(Integer.MIN_VALUE, Integer.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void testGcdConsistency() {
        int[] primeList = {19, 23, 53, 67, 73, 79, 101, 103, 111, 131};
        ArrayList<Integer> primes = new ArrayList<Integer>();
        for (int i = 0; i < primeList.length; i++) {
            primes.add(Integer.valueOf(primeList[i]));
        }
        RandomDataGenerator randomData = new RandomDataGenerator();
        for (int i = 0; i < 20; i++) {
            Object[] sample = randomData.nextSample(primes, 4);
            int p1 = ((Integer) sample[0]).intValue();
            int p2 = ((Integer) sample[1]).intValue();
            int p3 = ((Integer) sample[2]).intValue();
            int p4 = ((Integer) sample[3]).intValue();
            int i1 = p1 * p2 * p3;
            int i2 = p1 * p2 * p4;
            int gcd = p1 * p2;
            Assert.assertEquals(gcd, ArithmeticUtils.gcd(i1, i2));
            long l1 = i1;
            long l2 = i2;
            Assert.assertEquals(gcd, ArithmeticUtils.gcd(l1, l2));
        }
    }

    @Test
    public void  testGcdLong(){
        long a = 30;
        long b = 50;
        long c = 77;

        Assert.assertEquals(0, ArithmeticUtils.gcd(0L, 0));

        Assert.assertEquals(b, ArithmeticUtils.gcd(0, b));
        Assert.assertEquals(a, ArithmeticUtils.gcd(a, 0));
        Assert.assertEquals(b, ArithmeticUtils.gcd(0, -b));
        Assert.assertEquals(a, ArithmeticUtils.gcd(-a, 0));

        Assert.assertEquals(10, ArithmeticUtils.gcd(a, b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(-a, b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(a, -b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(-a, -b));

        Assert.assertEquals(1, ArithmeticUtils.gcd(a, c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(-a, c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(a, -c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(-a, -c));

        Assert.assertEquals(3L * (1L<<45), ArithmeticUtils.gcd(3L * (1L<<50), 9L * (1L<<45)));

        Assert.assertEquals(1L<<45, ArithmeticUtils.gcd(1L<<45, Long.MIN_VALUE));

        Assert.assertEquals(Long.MAX_VALUE, ArithmeticUtils.gcd(Long.MAX_VALUE, 0L));
        Assert.assertEquals(Long.MAX_VALUE, ArithmeticUtils.gcd(-Long.MAX_VALUE, 0L));
        Assert.assertEquals(1, ArithmeticUtils.gcd(60247241209L, 153092023L));
        try {
            // gcd(Long.MIN_VALUE, 0) > Long.MAX_VALUE
            ArithmeticUtils.gcd(Long.MIN_VALUE, 0);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Long.MIN_VALUE) > Long.MAX_VALUE
            ArithmeticUtils.gcd(0, Long.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Long.MIN_VALUE, Long.MIN_VALUE) > Long.MAX_VALUE
            ArithmeticUtils.gcd(Long.MIN_VALUE, Long.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }


    @Test
    public void testLcm() {
        int a = 30;
        int b = 50;
        int c = 77;

        Assert.assertEquals(0, ArithmeticUtils.lcm(0, b));
        Assert.assertEquals(0, ArithmeticUtils.lcm(a, 0));
        Assert.assertEquals(b, ArithmeticUtils.lcm(1, b));
        Assert.assertEquals(a, ArithmeticUtils.lcm(a, 1));
        Assert.assertEquals(150, ArithmeticUtils.lcm(a, b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(-a, b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(a, -b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(-a, -b));
        Assert.assertEquals(2310, ArithmeticUtils.lcm(a, c));

        // Assert that no intermediate value overflows:
        // The naive implementation of lcm(a,b) would be (a*b)/gcd(a,b)
        Assert.assertEquals((1<<20)*15, ArithmeticUtils.lcm((1<<20)*3, (1<<20)*5));

        // Special case
        Assert.assertEquals(0, ArithmeticUtils.lcm(0, 0));

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            ArithmeticUtils.lcm(Integer.MIN_VALUE, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            ArithmeticUtils.lcm(Integer.MIN_VALUE, 1<<20);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        try {
            ArithmeticUtils.lcm(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void testLcmLong() {
        long a = 30;
        long b = 50;
        long c = 77;

        Assert.assertEquals(0, ArithmeticUtils.lcm(0, b));
        Assert.assertEquals(0, ArithmeticUtils.lcm(a, 0));
        Assert.assertEquals(b, ArithmeticUtils.lcm(1, b));
        Assert.assertEquals(a, ArithmeticUtils.lcm(a, 1));
        Assert.assertEquals(150, ArithmeticUtils.lcm(a, b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(-a, b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(a, -b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(-a, -b));
        Assert.assertEquals(2310, ArithmeticUtils.lcm(a, c));

        Assert.assertEquals(Long.MAX_VALUE, ArithmeticUtils.lcm(60247241209L, 153092023L));

        // Assert that no intermediate value overflows:
        // The naive implementation of lcm(a,b) would be (a*b)/gcd(a,b)
        Assert.assertEquals((1L<<50)*15, ArithmeticUtils.lcm((1L<<45)*3, (1L<<50)*5));

        // Special case
        Assert.assertEquals(0L, ArithmeticUtils.lcm(0L, 0L));

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            ArithmeticUtils.lcm(Long.MIN_VALUE, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            ArithmeticUtils.lcm(Long.MIN_VALUE, 1<<20);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        Assert.assertEquals((long) Integer.MAX_VALUE * (Integer.MAX_VALUE - 1),
            ArithmeticUtils.lcm((long)Integer.MAX_VALUE, Integer.MAX_VALUE - 1));
        try {
            ArithmeticUtils.lcm(Long.MAX_VALUE, Long.MAX_VALUE - 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void testMulAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, ArithmeticUtils.mulAndCheck(big, 1));
        try {
            ArithmeticUtils.mulAndCheck(big, 2);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            ArithmeticUtils.mulAndCheck(bigNeg, 2);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }

    @Test
    public void testMulAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, ArithmeticUtils.mulAndCheck(max, 1L));
        Assert.assertEquals(min, ArithmeticUtils.mulAndCheck(min, 1L));
        Assert.assertEquals(0L, ArithmeticUtils.mulAndCheck(max, 0L));
        Assert.assertEquals(0L, ArithmeticUtils.mulAndCheck(min, 0L));
        Assert.assertEquals(max, ArithmeticUtils.mulAndCheck(1L, max));
        Assert.assertEquals(min, ArithmeticUtils.mulAndCheck(1L, min));
        Assert.assertEquals(0L, ArithmeticUtils.mulAndCheck(0L, max));
        Assert.assertEquals(0L, ArithmeticUtils.mulAndCheck(0L, min));
        Assert.assertEquals(1L, ArithmeticUtils.mulAndCheck(-1L, -1L));
        Assert.assertEquals(min, ArithmeticUtils.mulAndCheck(min / 2, 2));
        testMulAndCheckLongFailure(max, 2L);
        testMulAndCheckLongFailure(2L, max);
        testMulAndCheckLongFailure(min, 2L);
        testMulAndCheckLongFailure(2L, min);
        testMulAndCheckLongFailure(min, -1L);
        testMulAndCheckLongFailure(-1L, min);
    }

    @Test
    public void testSubAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, ArithmeticUtils.subAndCheck(big, 0));
        Assert.assertEquals(bigNeg + 1, ArithmeticUtils.subAndCheck(bigNeg, -1));
        Assert.assertEquals(-1, ArithmeticUtils.subAndCheck(bigNeg, -big));
        try {
            ArithmeticUtils.subAndCheck(big, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            ArithmeticUtils.subAndCheck(bigNeg, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }

    @Test
    public void testSubAndCheckErrorMessage() {
        int big = Integer.MAX_VALUE;
        try {
            ArithmeticUtils.subAndCheck(big, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            Assert.assertTrue(ex.getMessage().length() > 1);
        }
    }

    @Test
    public void testSubAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, ArithmeticUtils.subAndCheck(max, 0));
        Assert.assertEquals(min, ArithmeticUtils.subAndCheck(min, 0));
        Assert.assertEquals(-max, ArithmeticUtils.subAndCheck(0, max));
        Assert.assertEquals(min + 1, ArithmeticUtils.subAndCheck(min, -1));
        // min == -1-max
        Assert.assertEquals(-1, ArithmeticUtils.subAndCheck(-max - 1, -max));
        Assert.assertEquals(max, ArithmeticUtils.subAndCheck(-1, -1 - max));
        testSubAndCheckLongFailure(0L, min);
        testSubAndCheckLongFailure(max, -1L);
        testSubAndCheckLongFailure(min, 1L);
    }

    @Test
    public void testPow() {

        Assert.assertEquals(1801088541, ArithmeticUtils.pow(21, 7));
        Assert.assertEquals(1, ArithmeticUtils.pow(21, 0));
        try {
            ArithmeticUtils.pow(21, -7);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(1801088541, ArithmeticUtils.pow(21, 7));
        Assert.assertEquals(1, ArithmeticUtils.pow(21, 0));
        try {
            ArithmeticUtils.pow(21, -7);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(1801088541l, ArithmeticUtils.pow(21l, 7));
        Assert.assertEquals(1l, ArithmeticUtils.pow(21l, 0));
        try {
            ArithmeticUtils.pow(21l, -7);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        BigInteger twentyOne = BigInteger.valueOf(21l);
        Assert.assertEquals(BigInteger.valueOf(1801088541l), ArithmeticUtils.pow(twentyOne, 7));
        Assert.assertEquals(BigInteger.ONE, ArithmeticUtils.pow(twentyOne, 0));
        try {
            ArithmeticUtils.pow(twentyOne, -7);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(BigInteger.valueOf(1801088541l), ArithmeticUtils.pow(twentyOne, 7l));
        Assert.assertEquals(BigInteger.ONE, ArithmeticUtils.pow(twentyOne, 0l));
        try {
            ArithmeticUtils.pow(twentyOne, -7l);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(BigInteger.valueOf(1801088541l), ArithmeticUtils.pow(twentyOne, BigInteger.valueOf(7l)));
        Assert.assertEquals(BigInteger.ONE, ArithmeticUtils.pow(twentyOne, BigInteger.ZERO));
        try {
            ArithmeticUtils.pow(twentyOne, BigInteger.valueOf(-7l));
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        BigInteger bigOne =
            new BigInteger("1543786922199448028351389769265814882661837148" +
                           "4763915343722775611762713982220306372888519211" +
                           "560905579993523402015636025177602059044911261");
        Assert.assertEquals(bigOne, ArithmeticUtils.pow(twentyOne, 103));
        Assert.assertEquals(bigOne, ArithmeticUtils.pow(twentyOne, 103l));
        Assert.assertEquals(bigOne, ArithmeticUtils.pow(twentyOne, BigInteger.valueOf(103l)));

    }

    @Test
    @Deprecated
    public void testPowDeprecated() {
        Assert.assertEquals(1801088541l, ArithmeticUtils.pow(21l, 7l));
        Assert.assertEquals(1l, ArithmeticUtils.pow(21l, 0l));
        try {
            ArithmeticUtils.pow(21l, -7l);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }
    }

    @Test(expected=MathArithmeticException.class)
    public void testPowIntOverflow() {
        ArithmeticUtils.pow(21, 8);
    }

    @Test
    public void testPowInt() {
        final int base = 21;

        Assert.assertEquals(85766121L,
                            ArithmeticUtils.pow(base, 6));
        Assert.assertEquals(1801088541L,
                            ArithmeticUtils.pow(base, 7));
    }

    @Test(expected=MathArithmeticException.class)
    public void testPowNegativeIntOverflow() {
        ArithmeticUtils.pow(-21, 8);
    }

    @Test
    public void testPowNegativeInt() {
        final int base = -21;

        Assert.assertEquals(85766121,
                            ArithmeticUtils.pow(base, 6));
        Assert.assertEquals(-1801088541,
                            ArithmeticUtils.pow(base, 7));
    }

    @Test
    public void testPowMinusOneInt() {
        final int base = -1;
        for (int i = 0; i < 100; i++) {
            final int pow = ArithmeticUtils.pow(base, i);
            Assert.assertEquals("i: " + i, i % 2 == 0 ? 1 : -1, pow);
        }
    }

    @Test
    public void testPowOneInt() {
        final int base = 1;
        for (int i = 0; i < 100; i++) {
            final int pow = ArithmeticUtils.pow(base, i);
            Assert.assertEquals("i: " + i, 1, pow);
        }
    }

    @Test(expected=MathArithmeticException.class)
    public void testPowLongOverflow() {
        ArithmeticUtils.pow(21, 15);
    }

    @Test
    public void testPowLong() {
        final long base = 21;

        Assert.assertEquals(154472377739119461L,
                            ArithmeticUtils.pow(base, 13));
        Assert.assertEquals(3243919932521508681L,
                            ArithmeticUtils.pow(base, 14));
    }

    @Test(expected=MathArithmeticException.class)
    public void testPowNegativeLongOverflow() {
        ArithmeticUtils.pow(-21L, 15);
    }

    @Test
    public void testPowNegativeLong() {
        final long base = -21;

        Assert.assertEquals(-154472377739119461L,
                            ArithmeticUtils.pow(base, 13));
        Assert.assertEquals(3243919932521508681L,
                            ArithmeticUtils.pow(base, 14));
    }

    @Test
    public void testPowMinusOneLong() {
        final long base = -1;
        for (int i = 0; i < 100; i++) {
            final long pow = ArithmeticUtils.pow(base, i);
            Assert.assertEquals("i: " + i, i % 2 == 0 ? 1 : -1, pow);
        }
    }

    @Test
    public void testPowOneLong() {
        final long base = 1;
        for (int i = 0; i < 100; i++) {
            final long pow = ArithmeticUtils.pow(base, i);
            Assert.assertEquals("i: " + i, 1, pow);
        }
    }

    @Test
    public void testIsPowerOfTwo() {
        final int n = 1025;
        final boolean[] expected = new boolean[n];
        Arrays.fill(expected, false);
        for (int i = 1; i < expected.length; i *= 2) {
            expected[i] = true;
        }
        for (int i = 0; i < expected.length; i++) {
            final boolean actual = ArithmeticUtils.isPowerOfTwo(i);
            Assert.assertTrue(Integer.toString(i), actual == expected[i]);
        }
    }

    private void testAddAndCheckLongFailure(long a, long b) {
        try {
            ArithmeticUtils.addAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }
    }

    private void testMulAndCheckLongFailure(long a, long b) {
        try {
            ArithmeticUtils.mulAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }
    }

    private void testSubAndCheckLongFailure(long a, long b) {
        try {
            ArithmeticUtils.subAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }
    }
}
