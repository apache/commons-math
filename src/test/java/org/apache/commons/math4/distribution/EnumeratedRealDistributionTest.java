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
package org.apache.commons.math4.distribution;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.distribution.EnumeratedDistribution;
import org.apache.commons.math4.distribution.EnumeratedRealDistribution;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.exception.NotANumberException;
import org.apache.commons.math4.exception.NotFiniteNumberException;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Pair;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link EnumeratedRealDistribution}.
 *
 */
public class EnumeratedRealDistributionTest {

    /**
     * The distribution object used for testing.
     */
    private final EnumeratedRealDistribution testDistribution;

    /**
     * Creates the default distribution object used for testing.
     */
    public EnumeratedRealDistributionTest() {
        // Non-sorted singleton array with duplicates should be allowed.
        // Values with zero-probability do not extend the support.
        testDistribution = new EnumeratedRealDistribution(
                new double[]{3.0, -1.0, 3.0, 7.0, -2.0, 8.0},
                new double[]{0.2, 0.2, 0.3, 0.3, 0.0, 0.0});
    }

    /**
     * Tests if the {@link EnumeratedRealDistribution} constructor throws
     * exceptions for invalid data.
     */
    @Test
    public void testExceptions() {
        EnumeratedRealDistribution invalid = null;
        try {
            invalid = new EnumeratedRealDistribution(new double[]{1.0, 2.0}, new double[]{0.0});
            Assert.fail("Expected DimensionMismatchException");
        } catch (DimensionMismatchException e) {
        }
        try{
        invalid = new EnumeratedRealDistribution(new double[]{1.0, 2.0}, new double[]{0.0, -1.0});
            Assert.fail("Expected NotPositiveException");
        } catch (NotPositiveException e) {
        }
        try {
            invalid = new EnumeratedRealDistribution(new double[]{1.0, 2.0}, new double[]{0.0, 0.0});
            Assert.fail("Expected MathArithmeticException");
        } catch (MathArithmeticException e) {
        }
        try {
            invalid = new EnumeratedRealDistribution(new double[]{1.0, 2.0}, new double[]{0.0, Double.NaN});
            Assert.fail("Expected NotANumberException");
        } catch (NotANumberException e) {
        }
        try {
            invalid = new EnumeratedRealDistribution(new double[]{1.0, 2.0}, new double[]{0.0, Double.POSITIVE_INFINITY});
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
        double[] points = new double[]{-2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
        double[] results = new double[]{0, 0.2, 0, 0, 0, 0.5, 0, 0, 0, 0.3, 0};
        for (int p = 0; p < points.length; p++) {
            double density = testDistribution.probability(points[p]);
            Assert.assertEquals(results[p], density, 0.0);
        }
    }

    /**
     * Tests if the distribution returns proper density values.
     */
    @Test
    public void testDensity() {
        double[] points = new double[]{-2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
        double[] results = new double[]{0, 0.2, 0, 0, 0, 0.5, 0, 0, 0, 0.3, 0};
        for (int p = 0; p < points.length; p++) {
            double density = testDistribution.density(points[p]);
            Assert.assertEquals(results[p], density, 0.0);
        }
    }

    /**
     * Tests if the distribution returns proper cumulative probability values.
     */
    @Test
    public void testCumulativeProbability() {
        double[] points = new double[]{-2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
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
        Assert.assertEquals(3.4, testDistribution.getNumericalMean(), 1e-10);
    }

    /**
     * Tests if the distribution returns proper variance.
     */
    @Test
    public void testGetNumericalVariance() {
        Assert.assertEquals(7.84, testDistribution.getNumericalVariance(), 1e-10);
    }

    /**
     * Tests if the distribution returns proper lower bound.
     */
    @Test
    public void testGetSupportLowerBound() {
        Assert.assertEquals(-1, testDistribution.getSupportLowerBound(), 0);
    }

    /**
     * Tests if the distribution returns proper upper bound.
     */
    @Test
    public void testGetSupportUpperBound() {
        Assert.assertEquals(7, testDistribution.getSupportUpperBound(), 0);
    }

    /**
     * Tests if the distribution returns properly that the support is connected.
     */
    @Test
    public void testIsSupportConnected() {
        Assert.assertTrue(testDistribution.isSupportConnected());
    }

    /**
     * Tests sampling.
     */
    @Test
    public void testSample() {
        final int n = 1000000;
        testDistribution.reseedRandomGenerator(-334759360); // fixed seed
        final double[] samples = testDistribution.sample(n);
        Assert.assertEquals(n, samples.length);
        double sum = 0;
        double sumOfSquares = 0;
        for (int i = 0; i < samples.length; i++) {
            sum += samples[i];
            sumOfSquares += samples[i] * samples[i];
        }
        Assert.assertEquals(testDistribution.getNumericalMean(),
                sum / n, 1e-2);
        Assert.assertEquals(testDistribution.getNumericalVariance(),
                sumOfSquares / n - FastMath.pow(sum / n, 2), 1e-2);
    }

    @Test
    public void testIssue942() {
        List<Pair<Object,Double>> list = new ArrayList<Pair<Object, Double>>();
        list.add(new Pair<Object, Double>(new Object() {}, new Double(0)));
        list.add(new Pair<Object, Double>(new Object() {}, new Double(1)));
        Assert.assertEquals(1, new EnumeratedDistribution<Object>(list).sample(1).length);
    }

    @Test
    public void testIssue1065() {
        // Test Distribution for inverseCumulativeProbability
        //
        //         ^
        //         |
        // 1.000   +--------------------------------o===============
        //         |                               3|
        //         |                                |
        //         |                             1o=
        // 0.750   +-------------------------> o==  .
        //         |                          3|  . .
        //         |                   0       |  . .
        // 0.5625  +---------------> o==o======   . .
        //         |                 |  .      .  . .
        //         |                 |  .      .  . .
        //         |                5|  .      .  . .
        //         |                 |  .      .  . .
        //         |             o===   .      .  . .
        //         |             |   .  .      .  . .
        //         |            4|   .  .      .  . .
        //         |             |   .  .      .  . .
        // 0.000   +=============----+--+------+--+-+--------------->
        //                      14  18 21     28 31 33
        //
        // sum  = 4+5+0+3+1+3 = 16

        EnumeratedRealDistribution distribution = new EnumeratedRealDistribution(
                new double[] { 14.0, 18.0, 21.0, 28.0, 31.0, 33.0 },
                new double[] { 4.0 / 16.0, 5.0 / 16.0, 0.0 / 16.0, 3.0 / 16.0, 1.0 / 16.0, 3.0 / 16.0 });

        assertEquals(14.0, distribution.inverseCumulativeProbability(0.0000), 0.0);
        assertEquals(14.0, distribution.inverseCumulativeProbability(0.2500), 0.0);
        assertEquals(33.0, distribution.inverseCumulativeProbability(1.0000), 0.0);

        assertEquals(18.0, distribution.inverseCumulativeProbability(0.5000), 0.0);
        assertEquals(18.0, distribution.inverseCumulativeProbability(0.5624), 0.0);
        assertEquals(28.0, distribution.inverseCumulativeProbability(0.5626), 0.0);
        assertEquals(31.0, distribution.inverseCumulativeProbability(0.7600), 0.0);
        assertEquals(18.0, distribution.inverseCumulativeProbability(0.5625), 0.0);
        assertEquals(28.0, distribution.inverseCumulativeProbability(0.7500), 0.0);
    }

    @Test
    public void testCreateFromDoubles() {
        final double[] data = new double[] {0, 1, 1, 2, 2, 2};
        EnumeratedRealDistribution distribution = new EnumeratedRealDistribution(data);
        assertEquals(0.5, distribution.probability(2), 0);
        assertEquals(0.5, distribution.cumulativeProbability(1), 0);
    }
}
