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
package org.apache.commons.math4.legacy.distribution;

import org.apache.commons.statistics.distribution.DiscreteDistribution;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathArithmeticException;
import org.apache.commons.math4.legacy.exception.NotANumberException;
import org.apache.commons.math4.legacy.exception.NotFiniteNumberException;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link EnumeratedIntegerDistribution}.
 *
 */
public class EnumeratedIntegerDistributionTest {

    /**
     * The distribution object used for testing.
     */
    private final EnumeratedIntegerDistribution testDistribution;

    /**
     * Creates the default distribution object used for testing.
     */
    public EnumeratedIntegerDistributionTest() {
        // Non-sorted singleton array with duplicates should be allowed.
        // Values with zero-probability do not extend the support.
        testDistribution = new EnumeratedIntegerDistribution(
                new int[]{3, -1, 3, 7, -2, 8},
                new double[]{0.2, 0.2, 0.3, 0.3, 0.0, 0.0});
    }

    /**
     * Tests if the EnumeratedIntegerDistribution constructor throws
     * exceptions for invalid data.
     */
    @Test
    public void testExceptions() {
        EnumeratedIntegerDistribution invalid = null;
        try {
            new EnumeratedIntegerDistribution(new int[]{1, 2}, new double[]{0.0});
            Assert.fail("Expected DimensionMismatchException");
        } catch (DimensionMismatchException e) {
        }
        try {
            new EnumeratedIntegerDistribution(new int[]{1, 2}, new double[]{0.0, -1.0});
            Assert.fail("Expected NotPositiveException");
        } catch (NotPositiveException e) {
        }
        try {
            new EnumeratedIntegerDistribution(new int[]{1, 2}, new double[]{0.0, 0.0});
            Assert.fail("Expected MathArithmeticException");
        } catch (MathArithmeticException e) {
        }
        try {
          new EnumeratedIntegerDistribution(new int[]{1, 2}, new double[]{0.0, Double.NaN});
            Assert.fail("Expected NotANumberException");
        } catch (NotANumberException e) {
        }
        try {
        new EnumeratedIntegerDistribution(new int[]{1, 2}, new double[]{0.0, Double.POSITIVE_INFINITY});
            Assert.fail("Expected NotFiniteNumberException");
        } catch (NotFiniteNumberException e) {
        }
        Assert.assertNull("Expected non-initialized DiscreteRealDistribution", invalid);
    }

    /**
     * Tests if the distribution returns proper probability values.
     */
    @Test
    public void testProbability() {
        int[] points = new int[]{-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8};
        double[] results = new double[]{0, 0.2, 0, 0, 0, 0.5, 0, 0, 0, 0.3, 0};
        for (int p = 0; p < points.length; p++) {
            double probability = testDistribution.probability(points[p]);
            Assert.assertEquals(results[p], probability, 0.0);
        }
    }

    /**
     * Tests if the distribution returns proper cumulative probability values.
     */
    @Test
    public void testCumulativeProbability() {
        int[] points = new int[]{-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8};
        double[] results = new double[]{0, 0.2, 0.2, 0.2, 0.2, 0.7, 0.7, 0.7, 0.7, 1.0, 1.0};
        for (int p = 0; p < points.length; p++) {
            double probability = testDistribution.cumulativeProbability(points[p]);
            Assert.assertEquals(results[p], probability, 1e-10);
        }
    }

    /**
     * Tests if the distribution returns proper mean value.
     */
    @Test
    public void testGetNumericalMean() {
        Assert.assertEquals(3.4, testDistribution.getMean(), 1e-10);
    }

    /**
     * Tests if the distribution returns proper variance.
     */
    @Test
    public void testGetNumericalVariance() {
        Assert.assertEquals(7.84, testDistribution.getVariance(), 1e-10);
    }

    /**
     * Tests if the distribution returns proper lower bound.
     */
    @Test
    public void testGetSupportLowerBound() {
        Assert.assertEquals(-1, testDistribution.getSupportLowerBound());
    }

    /**
     * Tests if the distribution returns proper upper bound.
     */
    @Test
    public void testGetSupportUpperBound() {
        Assert.assertEquals(7, testDistribution.getSupportUpperBound());
    }

    /**
     * Tests sampling.
     */
    @Test
    public void testSample() {
        final int n = 1000000;
        final DiscreteDistribution.Sampler sampler =
            testDistribution.createSampler(RandomSource.XO_RO_SHI_RO_128_PP.create());
        final int[] samples = AbstractIntegerDistribution.sample(n, sampler);
        Assert.assertEquals(n, samples.length);
        double sum = 0;
        double sumOfSquares = 0;
        for (int i = 0; i < samples.length; i++) {
            sum += samples[i];
            sumOfSquares += samples[i] * samples[i];
        }
        final double mean = testDistribution.getMean();
        Assert.assertEquals("Mean", mean, sum / n, mean * 1e-2);
        final double var = testDistribution.getVariance();
        Assert.assertEquals("Variance", var, sumOfSquares / n - JdkMath.pow(sum / n, 2), var * 1e-2);
    }

    @Test
    public void testCreateFromIntegers() {
        final int[] data = new int[] {0, 1, 1, 2, 2, 2};
        EnumeratedIntegerDistribution distribution = new EnumeratedIntegerDistribution(data);
        Assert.assertEquals(0.5, distribution.probability(2), 0);
        Assert.assertEquals(0.5, distribution.cumulativeProbability(1), 0);
    }
}
