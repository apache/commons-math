/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the MathUtils class.
 *
 * @version $Revision: 1.3 $ $Date: 2003/10/13 08:07:11 $
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
    
    public void testBinomialCoefficientFail() {
        try {
            long x = MathUtils.binomialCoefficient(0,0);
            fail ("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            long x = MathUtils.binomialCoefficient(4,5);
            fail ("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            double x = MathUtils.binomialCoefficientDouble(0,0);
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
            double x = MathUtils.binomialCoefficientLog(0,0);
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
    }

    public void testFactorialFail() {
        try {
            long x = MathUtils.factorial(0);
            fail ("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            double x = MathUtils.factorialDouble(0);
            fail ("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            double x = MathUtils.factorialLog(0);
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
        assertEquals( 1.0, MathUtils.sign( 2.0 ), delta ) ;
        assertEquals( -1.0, MathUtils.sign( -2.0 ), delta ) ;
    }


    public void testSignFloat() {
        float delta = 0.0F ;
        assertEquals( 1.0F, MathUtils.sign( 2.0F ), delta ) ;
        assertEquals( -1.0F, MathUtils.sign( -2.0F ), delta ) ;
    }


    public void testSignByte() {
        assertEquals( (byte)1, MathUtils.sign( (byte)2 ) ) ;
        assertEquals( (byte)(-1), MathUtils.sign( (byte)(-2) ) ) ;
    }


    public void testSignShort() {
        assertEquals( (short)1, MathUtils.sign( (short)2 ) ) ;
        assertEquals( (short)(-1), MathUtils.sign( (short)(-2) ) ) ;
    }


    public void testSignInt() {
        assertEquals( (int)1, MathUtils.sign( (int)(2) ) ) ;
        assertEquals( (int)(-1), MathUtils.sign( (int)(-2) ) ) ;
    }


    public void testSignLong() {
        assertEquals( 1L, MathUtils.sign( 2L ) ) ;
        assertEquals( -1L, MathUtils.sign( -2L ) ) ;
    }
}