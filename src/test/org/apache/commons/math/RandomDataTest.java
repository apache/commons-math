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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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
package org.apache.commons.math;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.AssertionFailedError;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
/**
 * Test cases for the RandomData class.
 *
 * @author Phil Steitz
 * @version $Revision: 1.2 $ $Date: 2003/05/22 15:31:38 $
 */

public final class RandomDataTest extends TestCase {

    public RandomDataTest(String name) {
        super(name);
    }

    private long smallSampleSize = 1000;
    private double[] expected = {250,250,250,250};
    private int largeSampleSize = 10000;
    private int tolerance = 50;
    private String[] hex = 
        {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"}; 
    private RandomDataImpl randomData = new RandomDataImpl(); 
    private TestStatisticImpl testStatistic = new TestStatisticImpl();
    
    
    public void setUp() { 
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(RandomDataTest.class);
        suite.setName("RandomData Tests");
        return suite;
    }

    /** test dispersion and failure modes for nextInt() */
    public void testNextInt() {
        try {
            int x = randomData.nextInt(4,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        Freq freq = new Freq();
        int value = 0;
        for (int i=0;i<smallSampleSize;i++) {
            value = randomData.nextInt(0,3);
            assertTrue("nextInt range",(value >= 0) && (value <= 3));
            freq.addValue(value);  
        }
        double[] observed = new double[4];
        for (int i=0; i<4; i++) {
            String iString = new Integer(i).toString();
            observed[i] = freq.getCount(iString);
        } 
        
        /* Use ChiSquare dist with df = 4-1 = 3, alpha = .001
         * Change to 11.34 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
            testStatistic.chiSquare(expected,observed) < 16.27);    
    }
    
    /** test dispersion and failure modes for nextLong() */
    public void testNextLong() {
       try {
            long x = randomData.nextLong(4,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
       Freq freq = new Freq();
       long value = 0;
        for (int i=0;i<smallSampleSize;i++) {
            value = randomData.nextLong(0,3);
            assertTrue("nextInt range",(value >= 0) && (value <= 3));
            freq.addValue(value);  
        }
        double[] observed = new double[4];
        for (int i=0; i<4; i++) {
            String iString = new Integer(i).toString();
            observed[i] = freq.getCount(iString);
        } 
        
        /* Use ChiSquare dist with df = 4-1 = 3, alpha = .001
         * Change to 11.34 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
            testStatistic.chiSquare(expected,observed) < 16.27);    
    }
    
    /** test dispersion and failure modes for nextSecureLong() */
    public void testNextSecureLong() {
        try {
            long x = randomData.nextSecureLong(4,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        Freq freq = new Freq();
        long value = 0;
        for (int i=0;i<smallSampleSize;i++) {
            value = randomData.nextSecureLong(0,3);
            assertTrue("nextInt range",(value >= 0) && (value <= 3));
            freq.addValue(value);  
        }
        double[] observed = new double[4];
        for (int i=0; i<4; i++) {
            String iString = new Integer(i).toString();
            observed[i] = freq.getCount(iString);
        } 
        
        /* Use ChiSquare dist with df = 4-1 = 3, alpha = .001
         * Change to 11.34 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
            testStatistic.chiSquare(expected,observed) < 16.27);    
    }
    
    /** test dispersion and failure modes for nextSecureInt() */
    public void testNextSecureInt() {
        try {
            long x = randomData.nextSecureInt(4,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        Freq freq = new Freq();
        int value = 0;
        for (int i=0;i<smallSampleSize;i++) {
            value = randomData.nextSecureInt(0,3);
            assertTrue("nextInt range",(value >= 0) && (value <= 3));
            freq.addValue(value);  
        }
        double[] observed = new double[4];
        for (int i=0; i<4; i++) {
            String iString = new Integer(i).toString();
            observed[i] = freq.getCount(iString);
        } 
        
        /* Use ChiSquare dist with df = 4-1 = 3, alpha = .001
         * Change to 11.34 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
            testStatistic.chiSquare(expected,observed) < 16.27);    
    }
    
    /** 
     * Make sure that empirical distribution of random Poisson(4)'s 
     * has P(X <= 5) close to actual cumulative Poisson probablity
     * and that nextPoisson fails when mean is non-positive
     * TODO: replace with statistical test, adding test stat to TestStatistic
     */
    public void testNextPoisson() {
        try {
            long x = randomData.nextPoisson(0);
            fail("zero mean -- expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        Freq f = new Freq();
        long v = 0;
        for (int i = 0; i<largeSampleSize; i++) {
            try {
                f.addValue(randomData.nextPoisson(4.0d));
            } catch (Exception ex) {
                fail(ex.getMessage());
            }
        }
        long cumFreq = f.getCount("0") + f.getCount("1") + f.getCount("2") + 
                        f.getCount("3") + f.getCount("4") + f.getCount("5");
        long sumFreq = f.getSumFreq();
        double cumPct = 
            new Double(cumFreq).doubleValue()/new Double(sumFreq).doubleValue();
        assertEquals("cum Poisson(4)",cumPct,0.7851,0.2);
        try {
            long x = randomData.nextPoisson(-1);
            fail("negative mean supplied -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            long x = randomData.nextPoisson(0);
            fail("0 mean supplied -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        
    }
    
    /** test dispersion and failute modes for nextHex() */
    public void testNextHex() {
        try {
            String x = randomData.nextHexString(-1);
            fail("negative length supplied -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            String x = randomData.nextHexString(0);
            fail("zero length supplied -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        String hexString = randomData.nextHexString(3);
        if (hexString.length() != 3) {
                fail("incorrect length for generated string");
        }
        hexString = randomData.nextHexString(1);
        if (hexString.length() != 1) {
                fail("incorrect length for generated string");
        }
        try {
            hexString = randomData.nextHexString(0);
            fail("zero length requested -- expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        if (hexString.length() != 1) {
                fail("incorrect length for generated string");
        }      
        Freq f = new Freq();
        for (int i = 0; i < smallSampleSize; i++) {
            hexString = randomData.nextHexString(100);
            if (hexString.length() != 100) {
                fail("incorrect length for generated string");
            }
            for (int j = 0; j < hexString.length(); j++) {
                f.addValue(hexString.substring(j,j+1));
            }
        }
        double[] expected = new double[16];
        double[] observed = new double[16];
        for (int i = 0; i < 16; i++) {
            expected[i] = (double)smallSampleSize*100/(double)16;
            observed[i] = f.getCount(hex[i]);
        }
        /* Use ChiSquare dist with df = 16-1 = 15, alpha = .001
         * Change to 30.58 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
            testStatistic.chiSquare(expected,observed) < 37.70);    
    }
    
    /** test dispersion and failute modes for nextHex() */
    public void testNextSecureHex() {
        try {
            String x = randomData.nextSecureHexString(-1);
            fail("negative length -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            String x = randomData.nextSecureHexString(0);
            fail("zero length -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        String hexString = randomData.nextSecureHexString(3);
        if (hexString.length() != 3) {
                fail("incorrect length for generated string");
        }
        hexString = randomData.nextSecureHexString(1);
        if (hexString.length() != 1) {
                fail("incorrect length for generated string");
        }
        try {
            hexString = randomData.nextSecureHexString(0);
            fail("zero length requested -- expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        if (hexString.length() != 1) {
                fail("incorrect length for generated string");
        }      
        Freq f = new Freq();
        for (int i = 0; i < smallSampleSize; i++) {
            hexString = randomData.nextSecureHexString(100);
            if (hexString.length() != 100) {
                fail("incorrect length for generated string");
            }
            for (int j = 0; j < hexString.length(); j++) {
                f.addValue(hexString.substring(j,j+1));
            }
        }
        double[] expected = new double[16];
        double[] observed = new double[16];
        for (int i = 0; i < 16; i++) {
            expected[i] = (double)smallSampleSize*100/(double)16;
            observed[i] = f.getCount(hex[i]);
        }
        /* Use ChiSquare dist with df = 16-1 = 15, alpha = .001
         * Change to 30.58 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
            testStatistic.chiSquare(expected,observed) < 37.70);    
    }
    
    /** test failure modes and dispersion of nextUniform() */  
    public void testNextUniform() {    
        try {
            double x = randomData.nextUniform(4,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            double x = randomData.nextUniform(3,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        double[] expected = new double[] {500,500};
        double[] observed = new double[] {0,0};
        double lower = -1d;
        double upper = 20d;
        double midpoint = (lower + upper)/2d;
        double result = 0;
        for (int i = 0; i < 1000; i++) {
            result = randomData.nextUniform(lower,upper);
            if ((result == lower) || (result == upper)) {
                fail("generated value equal to an endpoint: " + result);
            } 
            if (result < midpoint) {
                observed[0]++;
            } else {
                observed[1]++;
            }
        }
        /* Use ChiSquare dist with df = 2-1 = 1, alpha = .001
         * Change to 6.64 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
            testStatistic.chiSquare(expected,observed) < 10.83);  
    }
    
    /** test failure modes and distribution of nextGaussian() */  
    public void testNextGaussian() { 
        try {
            double x = randomData.nextGaussian(0,0);
            fail("zero sigma -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        Univariate u = new UnivariateImpl();
        for (int i = 0; i<largeSampleSize; i++) {
            u.addValue(randomData.nextGaussian(0,1));
        }
        double xbar = u.getMean();
        double s = u.getStandardDeviation();
        double n = (double) u.getN(); 
        /* t-test at .001-level TODO: replace with externalized t-test, with
         * test statistic defined in TestStatistic
         */
        assertTrue(Math.abs(xbar)/(s/Math.sqrt(n))< 3.29);
    }
    
    /** test failure modes and distribution of nextExponential() */  
    public void testNextExponential() {
        try {
            double x = randomData.nextExponential(-1);
            fail("negative mean -- expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        assertEquals("0 mean", 0,randomData.nextExponential(0),10E-8); 
        long cumFreq = 0;
        double v = 0;
        for (int i = 0; i < largeSampleSize; i++) {
            v = randomData.nextExponential(1);
            assertTrue("exponential deviate postive", v > 0);
            if (v < 2) cumFreq++;
        }
        /* TODO: Replace with a statistical test, with statistic added to
         * TestStatistic.  Check below compares observed cumulative distribution
         * evaluated at 2 with exponential CDF 
         */
        assertEquals("exponential cumulative distribution",
            (double)cumFreq/(double)largeSampleSize,0.8646647167633873,.2);
    } 
    
    /** test reseeding, algorithm/provider games */
    public void testConfig() throws NoSuchProviderException, 
      NoSuchAlgorithmException{
        randomData.reSeed(1000);
        double v = randomData.nextUniform(0,1);
        randomData.reSeed();
        assertTrue("different seeds", 
            Math.abs(v - randomData.nextUniform(0,1)) > 10E-12);
        randomData.reSeed(1000);
        assertEquals("same seeds",v,randomData.nextUniform(0,1),10E-12);
        randomData.reSeedSecure(1000);
        String hex = randomData.nextSecureHexString(40);
        randomData.reSeedSecure();
        assertTrue("different seeds",
            !hex.equals(randomData.nextSecureHexString(40)));
        randomData.reSeedSecure(1000);
        assertTrue("same seeds",
            !hex.equals(randomData.nextSecureHexString(40))); 
        
        /* TODO: probably should remove this test as the package grows,
         * since it takes about 4 seconds
         */
        randomData.setSecureAlgorithm("SHA1PRNG","SUN");
        assertTrue("different seeds",
            !hex.equals(randomData.nextSecureHexString(40)));
        try {
            randomData.setSecureAlgorithm("NOSUCHTHING","SUN");
            fail("expecting NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException ex) {
            ;
        }
        
        try {
            randomData.setSecureAlgorithm("SHA1PRNG","NOSUCHPROVIDER");
            fail("expecting NoSuchProviderException");
        } catch (NoSuchProviderException ex) {
            ;
        }      
    }
}

