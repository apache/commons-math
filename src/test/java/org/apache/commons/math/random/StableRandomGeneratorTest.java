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

import junit.framework.TestCase;

import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * The class <code>StableRandomGeneratorTest</code> contains tests for the class
 * {@link StableRandomGenerator}
 * 
 * @version $Revision$
 */
public class StableRandomGeneratorTest extends TestCase {

    private RandomGenerator rg = new Well19937c(100);
    private final static int sampleSize = 10000;

    /**
     * Construct new test instance
     * 
     * @param name the test name
     */
    public StableRandomGeneratorTest(String name) {
        super(name);
    }

    /**
     * Run the double nextDouble() method test Due to leptokurtic property the
     * acceptance range is widened.
     * 
     * TODO: verify that tolerance this wide is really OK
     */
    public void testNextDouble() {
        StableRandomGenerator generator = new StableRandomGenerator(rg, 1.3,
                0.1);
        double[] sample = new double[2 * sampleSize];
        for (int i = 0; i < sample.length; ++i) {
            sample[i] = generator.nextNormalizedDouble();
        }
        assertEquals(0.0, StatUtils.mean(sample), 0.3);
    }

    /**
     * If alpha = 2, than it must be Gaussian distribution
     */
    public void testGaussianCase() {
        StableRandomGenerator generator = new StableRandomGenerator(rg, 2d, 0.0);

        double[] sample = new double[sampleSize];
        for (int i = 0; i < sample.length; ++i) {
            sample[i] = generator.nextNormalizedDouble();
        }
        assertEquals(0.0, StatUtils.mean(sample), 0.02);
        assertEquals(1.0, StatUtils.variance(sample), 0.02);
    }

    /**
     * If alpha = 1, than it must be Cauchy distribution
     */
    public void testCauchyCase() {
        StableRandomGenerator generator = new StableRandomGenerator(rg, 1d, 0.0);
        DescriptiveStatistics summary = new DescriptiveStatistics();

        for (int i = 0; i < sampleSize; ++i) {
            double sample = generator.nextNormalizedDouble();
            summary.addValue(sample);
        }

        // Standard Cauchy distribution should have zero median and mode
        double median = summary.getPercentile(50);
        assertEquals(0.0, median, 0.2);
    }

    /**
     * Input parameter range tests
     */
    public void testAlphaRangeBelowZero() {
        try {
            new StableRandomGenerator(rg,
                    -1.0, 0.0);
            fail("Expected OutOfRangeException");
        } catch (OutOfRangeException e) {
            assertEquals(-1.0, e.getArgument());
        }
    }

    public void testAlphaRangeAboveTwo() {
        try {
            new StableRandomGenerator(rg,
                    3.0, 0.0);
            fail("Expected OutOfRangeException");
        } catch (OutOfRangeException e) {
            assertEquals(3.0, e.getArgument());
        }
    }

    public void testBetaRangeBelowMinusOne() {
        try {
            new StableRandomGenerator(rg,
                    1.0, -2.0);
            fail("Expected OutOfRangeException");
        } catch (OutOfRangeException e) {
            assertEquals(-2.0, e.getArgument());
        }
    }

    public void testBetaRangeAboveOne() {
        try {
            new StableRandomGenerator(rg,
                    1.0, 2.0);
            fail("Expected OutOfRangeException");
        } catch (OutOfRangeException e) {
            assertEquals(2.0, e.getArgument());
        }
    }
}