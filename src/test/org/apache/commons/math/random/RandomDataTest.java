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
package org.apache.commons.math.random;

import junit.framework.Test;
import junit.framework.TestSuite;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

import org.apache.commons.math.RetryTestCase;
import org.apache.commons.math.stat.Frequency;
import org.apache.commons.math.stat.inference.ChiSquareTestImpl;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * Test cases for the RandomData class.
 *
 * @version $Revision$ $Date$
 */

public class RandomDataTest extends RetryTestCase {

    public RandomDataTest(String name) {
        super(name);
        randomData = new RandomDataImpl();
    }

    protected long smallSampleSize = 1000;
    protected double[] expected = {250,250,250,250};
    protected int largeSampleSize = 10000;
    private String[] hex = 
        {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"}; 
    protected RandomDataImpl randomData = null; 
    protected ChiSquareTestImpl testStatistic = new ChiSquareTestImpl();
    
    public void setUp() { 
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(RandomDataTest.class);
        suite.setName("RandomData Tests");
        return suite;
    }

    public void testNextIntExtremeValues() {
        int x = randomData.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        int y = randomData.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertFalse(x == y);
    }

    public void testNextLongExtremeValues() {
        long x = randomData.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
        long y = randomData.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
        assertFalse(x == y);
    }
    
    /** test dispersion and failure modes for nextInt() */
    public void testNextInt() {
        try {
            randomData.nextInt(4,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        Frequency freq = new Frequency();
        int value = 0;
        for (int i=0;i<smallSampleSize;i++) {
            value = randomData.nextInt(0,3);
            assertTrue("nextInt range",(value >= 0) && (value <= 3));
            freq.addValue(value);  
        }
        long[] observed = new long[4];
        for (int i=0; i<4; i++) {
            observed[i] = freq.getCount(i);
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
            randomData.nextLong(4,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
       Frequency freq = new Frequency();
       long value = 0;
        for (int i=0;i<smallSampleSize;i++) {
            value = randomData.nextLong(0,3);
            assertTrue("nextInt range",(value >= 0) && (value <= 3));
            freq.addValue(value);  
        }
        long[] observed = new long[4];
        for (int i=0; i<4; i++) {
            observed[i] = freq.getCount(i);
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
            randomData.nextSecureLong(4,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        Frequency freq = new Frequency();
        long value = 0;
        for (int i=0;i<smallSampleSize;i++) {
            value = randomData.nextSecureLong(0,3);
            assertTrue("nextInt range",(value >= 0) && (value <= 3));
            freq.addValue(value);  
        }
        long[] observed = new long[4];
        for (int i=0; i<4; i++) {
            observed[i] = freq.getCount(i);
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
            randomData.nextSecureInt(4,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        Frequency freq = new Frequency();
        int value = 0;
        for (int i=0;i<smallSampleSize;i++) {
            value = randomData.nextSecureInt(0,3);
            assertTrue("nextInt range",(value >= 0) && (value <= 3));
            freq.addValue(value);  
        }
        long[] observed = new long[4];
        for (int i=0; i<4; i++) {
            observed[i] = freq.getCount(i);
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
            randomData.nextPoisson(0);
            fail("zero mean -- expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            ;
        }
        Frequency f = new Frequency();
        for (int i = 0; i<largeSampleSize; i++) {
            try {
                f.addValue(randomData.nextPoisson(4.0d));
            } catch (Exception ex) {
                fail(ex.getMessage());
            }
        }
        long cumFreq = f.getCount(0) + f.getCount(1) + f.getCount(2) + 
                        f.getCount(3) + f.getCount(4) + f.getCount(5);
        long sumFreq = f.getSumFreq();
        double cumPct = 
            Double.valueOf(cumFreq).doubleValue()/Double.valueOf(sumFreq).doubleValue();
        assertEquals("cum Poisson(4)",cumPct,0.7851,0.2);
        try {
            randomData.nextPoisson(-1);
            fail("negative mean supplied -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            randomData.nextPoisson(0);
            fail("0 mean supplied -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        
    }
    
    /** test dispersion and failute modes for nextHex() */
    public void testNextHex() {
        try {
            randomData.nextHexString(-1);
            fail("negative length supplied -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            randomData.nextHexString(0);
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
        Frequency f = new Frequency();
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
        long[] observed = new long[16];
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
            randomData.nextSecureHexString(-1);
            fail("negative length -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            randomData.nextSecureHexString(0);
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
        Frequency f = new Frequency();
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
        long[] observed = new long[16];
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
            randomData.nextUniform(4,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            randomData.nextUniform(3,3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        double[] expected = {500,500};
        long[] observed = {0,0};
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
    
    /** test exclusive endpoints of nextUniform **/
    public void testNextUniformExclusiveEndpoints() {
        for (int i = 0; i < 1000; i++) {
            double u = randomData.nextUniform(0.99, 1);
            assertTrue(u > 0.99 && u < 1);
        }
    }
    
    /** test failure modes and distribution of nextGaussian() */  
    public void testNextGaussian() { 
        try {
            randomData.nextGaussian(0,0);
            fail("zero sigma -- IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
        SummaryStatistics u = new SummaryStatistics();
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
            randomData.nextExponential(-1);
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
      NoSuchAlgorithmException {
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
        
        /* remove this test back soon,
         * since it takes about 4 seconds 

        try {
            randomData.setSecureAlgorithm("SHA1PRNG","SUN");
        } catch (NoSuchProviderException ex) {
            ;
        }
        assertTrue("different seeds",
            !hex.equals(randomData.nextSecureHexString(40)));
        try {
            randomData.setSecureAlgorithm("NOSUCHTHING","SUN");
            fail("expecting NoSuchAlgorithmException");
        } catch (NoSuchProviderException ex) {
            ;
        } catch (NoSuchAlgorithmException ex) {
            ;
        }
        
        try {
            randomData.setSecureAlgorithm("SHA1PRNG","NOSUCHPROVIDER");
            fail("expecting NoSuchProviderException");
        } catch (NoSuchProviderException ex) {
            ;
        } 
        */
        
        // test reseeding without first using the generators
        RandomDataImpl rd = new RandomDataImpl();
        rd.reSeed(100);
        rd.nextLong(1,2);
        RandomDataImpl rd2 = new RandomDataImpl();
        rd2.reSeedSecure(2000);
        rd2.nextSecureLong(1,2);
        rd = new RandomDataImpl();
        rd.reSeed();
        rd.nextLong(1,2);
        rd2 = new RandomDataImpl();
        rd2.reSeedSecure();
        rd2.nextSecureLong(1,2);
    }
    
    /** tests for nextSample() sampling from Collection */
    public void testNextSample() {
       Object[][] c = {{"0","1"},{"0","2"},{"0","3"},{"0","4"},{"1","2"},
                        {"1","3"},{"1","4"},{"2","3"},{"2","4"},{"3","4"}};
       long[] observed = {0,0,0,0,0,0,0,0,0,0};
       double[] expected = {100,100,100,100,100,100,100,100,100,100};
       
       HashSet<Object> cPop = new HashSet<Object>();  //{0,1,2,3,4}
       for (int i = 0; i < 5; i++) {
           cPop.add(Integer.toString(i));
       }
       
       Object[] sets = new Object[10]; // 2-sets from 5
       for (int i = 0; i < 10; i ++) {
           HashSet<Object> hs = new HashSet<Object>();
           hs.add(c[i][0]);
           hs.add(c[i][1]);
           sets[i] = hs;
       }
       
       for (int i = 0; i < 1000; i ++) {
           Object[] cSamp = randomData.nextSample(cPop,2);
           observed[findSample(sets,cSamp)]++;
       }
       
        /* Use ChiSquare dist with df = 10-1 = 9, alpha = .001
         * Change to 21.67 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
            testStatistic.chiSquare(expected,observed) < 27.88);  
       
       // Make sure sample of size = size of collection returns same collection
       HashSet<Object> hs = new HashSet<Object>();
       hs.add("one");
       Object[] one = randomData.nextSample(hs,1);
       String oneString = (String) one[0];
       if ((one.length != 1) || !oneString.equals("one")){
           fail("bad sample for set size = 1, sample size = 1");
       }
       
       // Make sure we fail for sample size > collection size
       try {
           one = randomData.nextSample(hs,2);
           fail("sample size > set size, expecting IllegalArgumentException");
       } catch (IllegalArgumentException ex) {
           ;
       }
       
       // Make sure we fail for empty collection
       try {
           hs = new HashSet<Object>();
           one = randomData.nextSample(hs,0);
           fail("n = k = 0, expecting IllegalArgumentException");
       } catch (IllegalArgumentException ex) {
           ;
       }
    }

    @SuppressWarnings("unchecked")
    private int findSample(Object[] u, Object[] samp) {
        for (int i = 0; i < u.length; i++) {
            HashSet<Object> set = (HashSet<Object>) u[i];
            HashSet<Object> sampSet = new HashSet<Object>();
            for (int j = 0; j < samp.length; j++) {
                sampSet.add(samp[j]);
            }
            if (set.equals(sampSet)) {                 
               return i;
           }
        }
        fail("sample not found:{" + samp[0] + "," + samp[1] + "}");
        return -1;
    }
    
    /** tests for nextPermutation */
    public void testNextPermutation() {
        int[][] p = {{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,0,1},{2,1,0}};
        long[] observed = {0,0,0,0,0,0};
        double[] expected = {100,100,100,100,100,100};
        
        for (int i = 0; i < 600; i++) {
            int[] perm = randomData.nextPermutation(3,3);
            observed[findPerm(p,perm)]++;
        }  
        
        /* Use ChiSquare dist with df = 6-1 = 5, alpha = .001
         * Change to 15.09 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
                testStatistic.chiSquare(expected,observed) < 20.52); 
        
        // Check size = 1 boundary case
        int[] perm = randomData.nextPermutation(1,1);
        if ((perm.length != 1) || (perm[0] != 0)){
            fail("bad permutation for n = 1, sample k = 1");
            
            // Make sure we fail for k size > n 
            try {
                perm = randomData.nextPermutation(2,3);
                fail("permutation k > n, expecting IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                ;
            }
            
            // Make sure we fail for n = 0
            try {
                perm = randomData.nextPermutation(0,0);
                fail("permutation k = n = 0, expecting IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                ;
            }  
            
            // Make sure we fail for k < n < 0
            try {
                perm = randomData.nextPermutation(-1,-3);
                fail("permutation k < n < 0, expecting IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                ;
            }  
            
        }       
    }
    
    private int findPerm(int[][] p, int[] samp) {
        for (int i = 0; i < p.length; i++) {
            boolean good = true;
            for (int j = 0; j < samp.length; j++) {
                if (samp[j] != p[i][j]) {
                    good = false;
                }
            }
            if (good)  {
                return i;
            }
        }        
        fail("permutation not found");
        return -1;
    }   
}

