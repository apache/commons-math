/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the MathUtils class.
 *
 * @version $Revision: 1.15 $ $Date: 2004/10/14 04:01:04 $
 */

public final class MathUtilsTest extends TestCase {

    public MathUtilsTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(MathUtilsTest.class);
        suite.setName("MathUtils Tests");
        return suite;
    }
    
    public void testBinomialCoefficient() {
        long[] bcoef5 = {1,5,10,10,5,1};
        long[] bcoef6 = {1,6,15,20,15,6,1};
        for (int i = 0; i < 6; i++) {
            assertEquals("5 choose " + i, bcoef5[i], 
                MathUtils.binomialCoefficient(5,i));
        }
        for (int i = 0; i < 7; i++) {
            assertEquals("6 choose " + i, bcoef6[i], 
                MathUtils.binomialCoefficient(6,i));
        }
        
        for (int n = 1; n < 10; n++) {
            for (int k = 0; k <= n; k++) {
                assertEquals(n + " choose " + k, binomialCoefficient(n, k), 
                    MathUtils.binomialCoefficient(n, k));
                assertEquals(n + " choose " + k,(double) binomialCoefficient(n, k), 
                    MathUtils.binomialCoefficientDouble(n, k),Double.MIN_VALUE);
                assertEquals(n + " choose " + k,
                    Math.log((double) binomialCoefficient(n, k)), 
                    MathUtils.binomialCoefficientLog(n, k),10E-12);
            }
        }
      
      /* 
       * Takes a long time for recursion to unwind, but succeeds 
       * and yields exact value = 2,333,606,220
        
        assertEquals(MathUtils.binomialCoefficient(34,17),
            binomialCoefficient(34,17));
       */
    }
    
    /** Verify that b(0,0) = 1 */
    public void test0Choose0() {
        assertEquals(MathUtils.binomialCoefficientDouble(0, 0), 1d, 0);
        assertEquals(MathUtils.binomialCoefficientLog(0, 0), 0d, 0);
        assertEquals(MathUtils.binomialCoefficient(0, 0), 1);
    }
    
    public void testBinomialCoefficientFail() {
        try {
            long x = MathUtils.binomialCoefficient(4,5);
            fail ("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        
        try {
            double x = MathUtils.binomialCoefficientDouble(4,5);
            fail ("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        
        try {
            double x = MathUtils.binomialCoefficientLog(4,5);
            fail ("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            long x = MathUtils.binomialCoefficient(67,34);
            fail ("expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            ;
        }
        double x = MathUtils.binomialCoefficientDouble(1030,515);
        assertTrue("expecting infinite binomial coefficient",
            Double.isInfinite(x));
    }

    public void testFactorial() {
        for (int i = 1; i < 10; i++) {
            assertEquals(i + "! ",factorial(i),MathUtils.factorial(i));
            assertEquals(i + "! ",(double)factorial(i),
                MathUtils.factorialDouble(i),Double.MIN_VALUE);
            assertEquals(i + "! ",Math.log((double)factorial(i)),
                MathUtils.factorialLog(i),10E-12);
        }
        assertEquals("0", 1, MathUtils.factorial(0));
        assertEquals("0", 1.0d, MathUtils.factorialDouble(0), 1E-14);
        assertEquals("0", 0.0d, MathUtils.factorialLog(0), 1E-14);
    }

    public void testFactorialFail() {
        try {
            long x = MathUtils.factorial(-1);
            fail ("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            double x = MathUtils.factorialDouble(-1);
            fail ("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            double x = MathUtils.factorialLog(-1);
            fail ("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            double x = MathUtils.factorial(21);
            fail ("expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            ;
        }
        assertTrue("expecting infinite factorial value",
            Double.isInfinite(MathUtils.factorialDouble(171)));
    }


    /**
     * Exact recursive implementation to test against
     */
    private long binomialCoefficient(int n, int k) {
        if ((n == k) || (k == 0)) {
            return 1;
        }
        if ((k == 1) || (k == n - 1)) {
            return n;
        }
        return binomialCoefficient(n - 1, k - 1) +
            binomialCoefficient(n - 1, k);
    }

    /**
     * Finds the largest values of n for which binomialCoefficient and
     * binomialCoefficientDouble will return values that fit in a long, double,
     * resp.  Remove comments around test below to get this in test-report
     *
        public void testLimits() {
            findBinomialLimits();
        }
     */

    private void findBinomialLimits() {
        /**
         * will kick out 66 as the limit for long
         */
        boolean foundLimit = false;
        int test = 10;
        while (!foundLimit) {
            try {
                double x = MathUtils.binomialCoefficient(test, test / 2);
            } catch (ArithmeticException ex) {
                foundLimit = true;
                System.out.println
                    ("largest n for binomialCoefficient = " + (test - 1) );
            }
            test++;
        }

       /**
        * will kick out 1029 as the limit for double
        */
        foundLimit = false;
        test = 10;
        while (!foundLimit) {
            double x = MathUtils.binomialCoefficientDouble(test, test / 2);
            if (Double.isInfinite(x)) {
                foundLimit = true;
                System.out.println
                    ("largest n for binomialCoefficientD = " + (test - 1) );
            }
            test++;
        }
    }

    /**
     * Finds the largest values of n for which factiorial and
     * factorialDouble will return values that fit in a long, double,
     * resp.  Remove comments around test below to get this in test-report

        public void testFactiorialLimits() {
            findFactorialLimits();
        }
     */

    private void findFactorialLimits() {
        /**
         * will kick out 20 as the limit for long
         */
        boolean foundLimit = false;
        int test = 10;
        while (!foundLimit) {
            try {
                double x = MathUtils.factorial(test);
            } catch (ArithmeticException ex) {
                foundLimit = true;
                System.out.println
                    ("largest n for factorial = " + (test - 1) );
            }
            test++;
        }

       /**
        * will kick out 170 as the limit for double
        */
        foundLimit = false;
        test = 10;
        while (!foundLimit) {
            double x = MathUtils.factorialDouble(test);
            if (Double.isInfinite(x)) {
                foundLimit = true;
                System.out.println
                    ("largest n for factorialDouble = " + (test - 1) );
            }
            test++;
        }
    }


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

    public void testSignDouble() {
        double delta = 0.0 ;
        assertEquals( 1.0, MathUtils.indicator( 2.0 ), delta ) ;
        assertEquals( -1.0, MathUtils.indicator( -2.0 ), delta ) ;
    }

    public void testSignFloat() {
        float delta = 0.0F ;
        assertEquals( 1.0F, MathUtils.indicator( 2.0F ), delta ) ;
        assertEquals( -1.0F, MathUtils.indicator( -2.0F ), delta ) ;
    }

    public void testSignByte() {
        assertEquals( (byte)1, MathUtils.indicator( (byte)2 ) ) ;
        assertEquals( (byte)(-1), MathUtils.indicator( (byte)(-2) ) ) ;
    }

    public void testSignShort() {
        assertEquals( (short)1, MathUtils.indicator( (short)2 ) ) ;
        assertEquals( (short)(-1), MathUtils.indicator( (short)(-2) ) ) ;
    }

    public void testSignInt() {
        assertEquals( (int)1, MathUtils.indicator( (int)(2) ) ) ;
        assertEquals( (int)(-1), MathUtils.indicator( (int)(-2) ) ) ;
    }

    public void testSignLong() {
        assertEquals( 1L, MathUtils.indicator( 2L ) ) ;
        assertEquals( -1L, MathUtils.indicator( -2L ) ) ;
    }
   
    public void testIndicatorDouble() {
        double delta = 0.0 ;
        assertEquals( 1.0, MathUtils.indicator( 2.0 ), delta ) ;
        assertEquals( 1.0, MathUtils.indicator( 0.0 ), delta ) ;
        assertEquals( -1.0, MathUtils.indicator( -2.0 ), delta ) ;
    }
    
    public void testIndicatorFloat() {
        float delta = 0.0F ;
        assertEquals( 1.0F, MathUtils.indicator( 2.0F ), delta ) ;
        assertEquals( 1.0F, MathUtils.indicator( 0.0F ), delta ) ;
        assertEquals( -1.0F, MathUtils.indicator( -2.0F ), delta ) ;
    }
    
    public void testIndicatorByte() {
        assertEquals( (byte)1, MathUtils.indicator( (byte)2 ) ) ;
        assertEquals( (byte)1, MathUtils.indicator( (byte)0 ) ) ;
        assertEquals( (byte)(-1), MathUtils.indicator( (byte)(-2) ) ) ;
    }
    
    public void testIndicatorShort() {
        assertEquals( (short)1, MathUtils.indicator( (short)2 ) ) ;
        assertEquals( (short)1, MathUtils.indicator( (short)0 ) ) ;
        assertEquals( (short)(-1), MathUtils.indicator( (short)(-2) ) ) ;
    }
    
    public void testIndicatorInt() {
        assertEquals( (int)1, MathUtils.indicator( (int)(2) ) ) ;
        assertEquals( (int)1, MathUtils.indicator( (int)(0) ) ) ;
        assertEquals( (int)(-1), MathUtils.indicator( (int)(-2) ) ) ;
    }
    
    public void testIndicatorLong() {
        assertEquals( 1L, MathUtils.indicator( 2L ) ) ;
        assertEquals( 1L, MathUtils.indicator( 0L ) ) ;
        assertEquals( -1L, MathUtils.indicator( -2L ) ) ;
    }
    
    public void testCosh() {
        double x = 3.0;
        double expected = 10.06766;
        assertEquals(expected, MathUtils.cosh(x), 1.0e-5);
    }   
    
    public void testSinh() {
        double x = 3.0;
        double expected = 10.01787;
        assertEquals(expected, MathUtils.sinh(x), 1.0e-5);
    }   
    
    public void testCoshNaN() {
        assertTrue(Double.isNaN(MathUtils.cosh(Double.NaN)));
    }   
    
    public void testSinhNaN() {
        assertTrue(Double.isNaN(MathUtils.sinh(Double.NaN)));
    } 
    
    public void testEquals() {
        double[] testArray = {Double.NaN, Double.POSITIVE_INFINITY, 
                Double.NEGATIVE_INFINITY, 1d, 0d};
        for (int i = 0; i < testArray.length; i++) {
            for (int j = 0; j < testArray.length; j ++) {
                if (i == j) {
                    assertTrue(MathUtils.equals(testArray[i], testArray[j]));
                    assertTrue(MathUtils.equals(testArray[j], testArray[i]));
                } else {
                    assertTrue(!MathUtils.equals(testArray[i], testArray[j]));
                    assertTrue(!MathUtils.equals(testArray[j], testArray[i]));
                }
            }
        } 
    }
    
    public void testHash() {
        double[] testArray = {Double.NaN, Double.POSITIVE_INFINITY, 
                Double.NEGATIVE_INFINITY, 1d, 0d, 1E-14, (1 + 1E-14), 
                Double.MIN_VALUE, Double.MAX_VALUE};
        for (int i = 0; i < testArray.length; i++) {
            for (int j = 0; j < testArray.length; j ++) {
                if (i == j) {
                    assertEquals(MathUtils.hash(testArray[i]), MathUtils.hash(testArray[j]));
                    assertEquals(MathUtils.hash(testArray[j]), MathUtils.hash(testArray[i]));
                } else {
                    assertTrue(MathUtils.hash(testArray[i]) != MathUtils.hash(testArray[j]));
                    assertTrue(MathUtils.hash(testArray[j]) != MathUtils.hash(testArray[i]));
                }
            }
        } 
    }
}