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

import java.util.Arrays;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.stat.Frequency;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.exception.MathIllegalArgumentException;
 
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Base class for RandomGenerator tests.
 * 
 * Tests RandomGenerator methods directly and also executes RandomDataTest 
 * test cases against a RandomDataImpl created using the provided generator.
 * 
 * RandomGenerator test classes should extend this class, implementing
 * makeGenerator() to provide a concrete generator to test. The generator
 * returned by makeGenerator should be seeded with a fixed seed.
 *
 * @version $Id$
 */

public abstract class RandomGeneratorAbstractTest extends RandomDataTest {

    /** RandomGenerator under test */
    protected RandomGenerator generator;
    
    /** 
     * Override this method in subclasses to provide a concrete generator to test.  
     * Return a generator seeded with a fixed seed.
     */
    protected abstract RandomGenerator makeGenerator();

    /**
     * Initialize generator and randomData instance in superclass.
     */
    public RandomGeneratorAbstractTest() {
        generator = makeGenerator();
        randomData = new RandomDataImpl(generator);
    }
    
    /**
     * Set a fixed seed for the tests
     */
    @Before
    public void setUp() {
        generator = makeGenerator();
    }
    
    // Omit secureXxx tests, since they do not use the provided generator
    @Override
    public void testNextSecureLong() {}
    
    @Override
    public void testNextSecureInt() {}
    
    @Override
    public void testNextSecureHex() {}

    @Test
    /**
     * Tests uniformity of nextInt(int) distribution by generating 1000
     * samples for each of 10 test values and for each sample performing
     * a chi-square test of homogeneity of the observed distribution with
     * the expected uniform distribution.  Tests are performed at the .01
     * level and an average failure rate higher than 2% (i.e. more than 20
     * null hypothesis rejections) causes the test case to fail.
     * 
     * All random values are generated using the generator instance used by
     * other tests and the generator is not reseeded, so this is a fixed seed
     * test.
     */
    public void testNextIntDirect() throws Exception {
        // Set up test values - end of the array filled randomly
        int[] testValues = new int[] {4, 10, 12, 32, 100, 10000, 0, 0, 0, 0};
        for (int i = 6; i < 10; i++) {
            final int val = generator.nextInt();
            testValues[i] = val < 0 ? -val : val + 1;
        }
        
        final int numTests = 1000;
        for (int i = 0; i < testValues.length; i++) {
            final int n = testValues[i];
            // Set up bins
            int[] binUpperBounds;
            if (n < 32) {
                binUpperBounds = new int[n];
                for (int k = 0; k < n; k++) {
                    binUpperBounds[k] = k;
                }
            } else {
                binUpperBounds = new int[10];
                final int step = n / 10;
                for (int k = 0; k < 9; k++) {
                    binUpperBounds[k] = (k + 1) * step;
                }
                binUpperBounds[9] = n - 1;
            }  
            // Run the tests
            int numFailures = 0;
            final int binCount = binUpperBounds.length;
            final long[] observed = new long[binCount];
            final double[] expected = new double[binCount];
            expected[0] = binUpperBounds[0] == 0 ? (double) smallSampleSize / (double) n :
                (double) ((binUpperBounds[0] + 1) * smallSampleSize) / (double) n;
            for (int k = 1; k < binCount; k++) {
                expected[k] = (double) smallSampleSize * 
                (double) (binUpperBounds[k] - binUpperBounds[k - 1]) / (double) n;
            }
            for (int j = 0; j < numTests; j++) {
                Arrays.fill(observed, 0);
                for (int k = 0; k < smallSampleSize; k++) {
                    final int value = generator.nextInt(n);
                    Assert.assertTrue("nextInt range",(value >= 0) && (value < n));
                    for (int l = 0; l < binCount; l++) {
                        if (binUpperBounds[l] >= value) {
                            observed[l]++;
                            break;
                        }
                    }
                }
                if (testStatistic.chiSquareTest(expected, observed) < 0.01) {
                    numFailures++;
                }  
            }
            if ((double) numFailures / (double) numTests > 0.02) {
                Assert.fail("Too many failures for n = " + n +
                " " + numFailures + " out of " + numTests + " tests failed.");
            }
        }
    }
    
    @Test(expected=MathIllegalArgumentException.class)
    public void testNextIntIAE() {
        try {
            generator.nextInt(-1);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        generator.nextInt(0);
    }

    @Test
    public void testNextLongDirect() {
        long q1 = Long.MAX_VALUE/4;
        long q2 = 2 *  q1;
        long q3 = 3 * q1;

        Frequency freq = new Frequency();
        long val = 0;
        int value = 0;
        for (int i=0; i<smallSampleSize; i++) {
            val = generator.nextLong();
            val = val < 0 ? -val : val;
            if (val < q1) {
                value = 0;
            } else if (val < q2) {
                value = 1;
            } else if (val < q3) {
                value = 2;
            } else {
                value = 3;
            }
            freq.addValue(value);
        }
        long[] observed = new long[4];
        for (int i=0; i<4; i++) {
            observed[i] = freq.getCount(i);
        }

        /* Use ChiSquare dist with df = 4-1 = 3, alpha = .001
         * Change to 11.34 for alpha = .01
         */
        Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times",
                testStatistic.chiSquare(expected,observed) < 16.27);
    }

    @Test
    public void testNextBooleanDirect() {
        long halfSampleSize = smallSampleSize / 2;
        double[] expected = {halfSampleSize, halfSampleSize};
        long[] observed = new long[2];
        for (int i=0; i<smallSampleSize; i++) {
            if (generator.nextBoolean()) {
                observed[0]++;
            } else {
                observed[1]++;
            }
        }
        /* Use ChiSquare dist with df = 2-1 = 1, alpha = .001
         * Change to 6.635 for alpha = .01
         */
        Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times",
                testStatistic.chiSquare(expected,observed) < 10.828);
    }

    @Test
    public void testNextFloatDirect() {
        Frequency freq = new Frequency();
        float val = 0;
        int value = 0;
        for (int i=0; i<smallSampleSize; i++) {
            val = generator.nextFloat();
            if (val < 0.25) {
                value = 0;
            } else if (val < 0.5) {
                value = 1;
            } else if (val < 0.75) {
                value = 2;
            } else {
                value = 3;
            }
            freq.addValue(value);
        }
        long[] observed = new long[4];
        for (int i=0; i<4; i++) {
            observed[i] = freq.getCount(i);
        }

        /* Use ChiSquare dist with df = 4-1 = 3, alpha = .001
         * Change to 11.34 for alpha = .01
         */
        Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times",
                testStatistic.chiSquare(expected,observed) < 16.27);
    }

    @Test
    public void testDoubleDirect() {
        SummaryStatistics sample = new SummaryStatistics();
        for (int i = 0; i < 10000; ++i) {
            sample.addValue(generator.nextDouble());
        }
        Assert.assertEquals(0.5, sample.getMean(), 0.02);
        Assert.assertEquals(1.0 / (2.0 * FastMath.sqrt(3.0)),
                     sample.getStandardDeviation(),
                     0.01);
    }

    @Test
    public void testFloatDirect() {
        SummaryStatistics sample = new SummaryStatistics();
        for (int i = 0; i < 1000; ++i) {
            sample.addValue(generator.nextFloat());
        }
        Assert.assertEquals(0.5, sample.getMean(), 0.01);
        Assert.assertEquals(1.0 / (2.0 * FastMath.sqrt(3.0)),
                     sample.getStandardDeviation(),
                     0.01);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testNextIntNeg() {
        generator.nextInt(-1);
    }

    @Test
    public void testNextInt2() {
        int walk = 0;
        for (int k = 0; k < 10000; ++k) {
           if (generator.nextInt() >= 0) {
               ++walk;
           } else {
               --walk;
           }
        }
        Assert.assertTrue("Walked too far astray: " + walk, FastMath.abs(walk) < 120);
    }

    @Test
    public void testNextLong2() {
        int walk = 0;
        for (int k = 0; k < 1000; ++k) {
           if (generator.nextLong() >= 0) {
               ++walk;
           } else {
               --walk;
           }
        }
        Assert.assertTrue("Walked too far astray: " + walk, FastMath.abs(walk) < 100);
    }

    @Test
    public void testNexBoolean2() {
        int walk = 0;
        for (int k = 0; k < 10000; ++k) {
           if (generator.nextBoolean()) {
               ++walk;
           } else {
               --walk;
           }
        }
        Assert.assertTrue(FastMath.abs(walk) < 250);
    }

    @Test
    public void testNexBytes() throws Exception {
        long[] count = new long[256];
        byte[] bytes = new byte[10];
        double[] expected = new double[256];
        final int sampleSize = 100000;
        
        for (int i = 0; i < 256; i++) {
            expected[i] = (double) sampleSize / 265f;
        }
        
        for (int k = 0; k < sampleSize; ++k) {
           generator.nextBytes(bytes);
           for (byte b : bytes) {
               ++count[b + 128];
           }
        }
        
        TestUtils.assertChiSquareAccept(expected, count, 0.001);
        
    }

}
