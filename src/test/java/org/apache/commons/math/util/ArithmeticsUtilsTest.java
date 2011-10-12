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

import java.util.ArrayList;

import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.random.RandomDataImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link ArithmeticsUtils} class.
 *
 * @version $Id$
 */
public class ArithmeticsUtilsTest {
    /**
     * Exact direct multiplication implementation to test against
     */
    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    @Test
    public void testAddAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, ArithmeticsUtils.addAndCheck(big, 0));
        try {
            ArithmeticsUtils.addAndCheck(big, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            ArithmeticsUtils.addAndCheck(bigNeg, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }


    @Test
    public void testAddAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, ArithmeticsUtils.addAndCheck(max, 0L));
        Assert.assertEquals(min, ArithmeticsUtils.addAndCheck(min, 0L));
        Assert.assertEquals(max, ArithmeticsUtils.addAndCheck(0L, max));
        Assert.assertEquals(min, ArithmeticsUtils.addAndCheck(0L, min));
        Assert.assertEquals(1, ArithmeticsUtils.addAndCheck(-1L, 2L));
        Assert.assertEquals(1, ArithmeticsUtils.addAndCheck(2L, -1L));
        Assert.assertEquals(-3, ArithmeticsUtils.addAndCheck(-2L, -1L));
        Assert.assertEquals(min, ArithmeticsUtils.addAndCheck(min + 1, -1L));
        testAddAndCheckLongFailure(max, 1L);
        testAddAndCheckLongFailure(min, -1L);
        testAddAndCheckLongFailure(1L, max);
        testAddAndCheckLongFailure(-1L, min);
    }

    private void testAddAndCheckLongFailure(long a, long b) {
        try {
            ArithmeticsUtils.addAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }
    }

    @Test
    public void testSubAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, ArithmeticsUtils.subAndCheck(big, 0));
        Assert.assertEquals(bigNeg + 1, ArithmeticsUtils.subAndCheck(bigNeg, -1));
        Assert.assertEquals(-1, ArithmeticsUtils.subAndCheck(bigNeg, -big));
        try {
            ArithmeticsUtils.subAndCheck(big, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            ArithmeticsUtils.subAndCheck(bigNeg, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }

    @Test
    public void testSubAndCheckErrorMessage() {
        int big = Integer.MAX_VALUE;
        try {
            ArithmeticsUtils.subAndCheck(big, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            Assert.assertTrue(ex.getMessage().length() > 1);
        }
    }

    @Test
    public void testSubAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, ArithmeticsUtils.subAndCheck(max, 0));
        Assert.assertEquals(min, ArithmeticsUtils.subAndCheck(min, 0));
        Assert.assertEquals(-max, ArithmeticsUtils.subAndCheck(0, max));
        Assert.assertEquals(min + 1, ArithmeticsUtils.subAndCheck(min, -1));
        // min == -1-max
        Assert.assertEquals(-1, ArithmeticsUtils.subAndCheck(-max - 1, -max));
        Assert.assertEquals(max, ArithmeticsUtils.subAndCheck(-1, -1 - max));
        testSubAndCheckLongFailure(0L, min);
        testSubAndCheckLongFailure(max, -1L);
        testSubAndCheckLongFailure(min, 1L);
    }

    private void testSubAndCheckLongFailure(long a, long b) {
        try {
            ArithmeticsUtils.subAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }

    }

    @Test
    public void testFactorial() {
        for (int i = 1; i < 21; i++) {
            Assert.assertEquals(i + "! ", factorial(i), ArithmeticsUtils.factorial(i));
            Assert.assertEquals(i + "! ", factorial(i), ArithmeticsUtils.factorialDouble(i), Double.MIN_VALUE);
            Assert.assertEquals(i + "! ", FastMath.log(factorial(i)), ArithmeticsUtils.factorialLog(i), 10E-12);
        }

        Assert.assertEquals("0", 1, ArithmeticsUtils.factorial(0));
        Assert.assertEquals("0", 1.0d, ArithmeticsUtils.factorialDouble(0), 1E-14);
        Assert.assertEquals("0", 0.0d, ArithmeticsUtils.factorialLog(0), 1E-14);
    }

    @Test
    public void testFactorialFail() {
        try {
            ArithmeticsUtils.factorial(-1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            ArithmeticsUtils.factorialDouble(-1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            ArithmeticsUtils.factorialLog(-1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            ArithmeticsUtils.factorial(21);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // ignored
        }
        Assert.assertTrue("expecting infinite factorial value", Double.isInfinite(ArithmeticsUtils.factorialDouble(171)));
    }


    @Test
    public void testGcd() {
        int a = 30;
        int b = 50;
        int c = 77;

        Assert.assertEquals(0, ArithmeticsUtils.gcd(0, 0));

        Assert.assertEquals(b, ArithmeticsUtils.gcd(0, b));
        Assert.assertEquals(a, ArithmeticsUtils.gcd(a, 0));
        Assert.assertEquals(b, ArithmeticsUtils.gcd(0, -b));
        Assert.assertEquals(a, ArithmeticsUtils.gcd(-a, 0));

        Assert.assertEquals(10, ArithmeticsUtils.gcd(a, b));
        Assert.assertEquals(10, ArithmeticsUtils.gcd(-a, b));
        Assert.assertEquals(10, ArithmeticsUtils.gcd(a, -b));
        Assert.assertEquals(10, ArithmeticsUtils.gcd(-a, -b));

        Assert.assertEquals(1, ArithmeticsUtils.gcd(a, c));
        Assert.assertEquals(1, ArithmeticsUtils.gcd(-a, c));
        Assert.assertEquals(1, ArithmeticsUtils.gcd(a, -c));
        Assert.assertEquals(1, ArithmeticsUtils.gcd(-a, -c));

        Assert.assertEquals(3 * (1<<15), ArithmeticsUtils.gcd(3 * (1<<20), 9 * (1<<15)));

        Assert.assertEquals(Integer.MAX_VALUE, ArithmeticsUtils.gcd(Integer.MAX_VALUE, 0));
        Assert.assertEquals(Integer.MAX_VALUE, ArithmeticsUtils.gcd(-Integer.MAX_VALUE, 0));
        Assert.assertEquals(1<<30, ArithmeticsUtils.gcd(1<<30, -Integer.MIN_VALUE));
        try {
            // gcd(Integer.MIN_VALUE, 0) > Integer.MAX_VALUE
            ArithmeticsUtils.gcd(Integer.MIN_VALUE, 0);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Integer.MIN_VALUE) > Integer.MAX_VALUE
            ArithmeticsUtils.gcd(0, Integer.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Integer.MIN_VALUE, Integer.MIN_VALUE) > Integer.MAX_VALUE
            ArithmeticsUtils.gcd(Integer.MIN_VALUE, Integer.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void  testGcdLong(){
        long a = 30;
        long b = 50;
        long c = 77;

        Assert.assertEquals(0, ArithmeticsUtils.gcd(0L, 0));

        Assert.assertEquals(b, ArithmeticsUtils.gcd(0, b));
        Assert.assertEquals(a, ArithmeticsUtils.gcd(a, 0));
        Assert.assertEquals(b, ArithmeticsUtils.gcd(0, -b));
        Assert.assertEquals(a, ArithmeticsUtils.gcd(-a, 0));

        Assert.assertEquals(10, ArithmeticsUtils.gcd(a, b));
        Assert.assertEquals(10, ArithmeticsUtils.gcd(-a, b));
        Assert.assertEquals(10, ArithmeticsUtils.gcd(a, -b));
        Assert.assertEquals(10, ArithmeticsUtils.gcd(-a, -b));

        Assert.assertEquals(1, ArithmeticsUtils.gcd(a, c));
        Assert.assertEquals(1, ArithmeticsUtils.gcd(-a, c));
        Assert.assertEquals(1, ArithmeticsUtils.gcd(a, -c));
        Assert.assertEquals(1, ArithmeticsUtils.gcd(-a, -c));

        Assert.assertEquals(3L * (1L<<45), ArithmeticsUtils.gcd(3L * (1L<<50), 9L * (1L<<45)));

        Assert.assertEquals(1L<<45, ArithmeticsUtils.gcd(1L<<45, Long.MIN_VALUE));

        Assert.assertEquals(Long.MAX_VALUE, ArithmeticsUtils.gcd(Long.MAX_VALUE, 0L));
        Assert.assertEquals(Long.MAX_VALUE, ArithmeticsUtils.gcd(-Long.MAX_VALUE, 0L));
        Assert.assertEquals(1, ArithmeticsUtils.gcd(60247241209L, 153092023L));
        try {
            // gcd(Long.MIN_VALUE, 0) > Long.MAX_VALUE
            ArithmeticsUtils.gcd(Long.MIN_VALUE, 0);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Long.MIN_VALUE) > Long.MAX_VALUE
            ArithmeticsUtils.gcd(0, Long.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Long.MIN_VALUE, Long.MIN_VALUE) > Long.MAX_VALUE
            ArithmeticsUtils.gcd(Long.MIN_VALUE, Long.MIN_VALUE);
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
        RandomDataImpl randomData = new RandomDataImpl();
        for (int i = 0; i < 20; i++) {
            Object[] sample = randomData.nextSample(primes, 4);
            int p1 = ((Integer) sample[0]).intValue();
            int p2 = ((Integer) sample[1]).intValue();
            int p3 = ((Integer) sample[2]).intValue();
            int p4 = ((Integer) sample[3]).intValue();
            int i1 = p1 * p2 * p3;
            int i2 = p1 * p2 * p4;
            int gcd = p1 * p2;
            Assert.assertEquals(gcd, ArithmeticsUtils.gcd(i1, i2));
            long l1 = i1;
            long l2 = i2;
            Assert.assertEquals(gcd, ArithmeticsUtils.gcd(l1, l2));
        }
    }
}
