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

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for AbstractIntegerDistribution default implementations.
 *
 */
public class AbstractIntegerDistributionTest {
    protected final DiceDistribution diceDistribution = new DiceDistribution();
    protected final double p = diceDistribution.probability(1);

    @Test
    public void testInverseCumulativeProbabilityMethod() {
        double precision = 0.000000000000001;
        Assert.assertEquals(1, diceDistribution.inverseCumulativeProbability(0));
        Assert.assertEquals(1, diceDistribution.inverseCumulativeProbability((1d-Double.MIN_VALUE)/6d));
        Assert.assertEquals(2, diceDistribution.inverseCumulativeProbability((1d+precision)/6d));
        Assert.assertEquals(2, diceDistribution.inverseCumulativeProbability((2d-Double.MIN_VALUE)/6d));
        Assert.assertEquals(3, diceDistribution.inverseCumulativeProbability((2d+precision)/6d));
        Assert.assertEquals(3, diceDistribution.inverseCumulativeProbability((3d-Double.MIN_VALUE)/6d));
        Assert.assertEquals(4, diceDistribution.inverseCumulativeProbability((3d+precision)/6d));
        Assert.assertEquals(4, diceDistribution.inverseCumulativeProbability((4d-Double.MIN_VALUE)/6d));
        Assert.assertEquals(5, diceDistribution.inverseCumulativeProbability((4d+precision)/6d));
        Assert.assertEquals(5, diceDistribution.inverseCumulativeProbability((5d-precision)/6d));//Can't use Double.MIN
        Assert.assertEquals(6, diceDistribution.inverseCumulativeProbability((5d+precision)/6d));
        Assert.assertEquals(6, diceDistribution.inverseCumulativeProbability((6d-precision)/6d));//Can't use Double.MIN
        Assert.assertEquals(6, diceDistribution.inverseCumulativeProbability(6d/6d));
    }

    @Test
    public void testCumulativeProbabilitiesSingleArguments() {
        for (int i = 1; i < 7; i++) {
            Assert.assertEquals(p * i,
                    diceDistribution.cumulativeProbability(i), Double.MIN_VALUE);
        }
        Assert.assertEquals(0.0,
                diceDistribution.cumulativeProbability(0), Double.MIN_VALUE);
        Assert.assertEquals(1.0,
                diceDistribution.cumulativeProbability(7), Double.MIN_VALUE);
    }

    @Test
    public void testProbabilitiesRangeArguments() {
        int lower = 0;
        int upper = 6;
        for (int i = 0; i < 2; i++) {
            // cum(0,6) = p(0 < X <= 6) = 1, cum(1,5) = 4/6, cum(2,4) = 2/6
            Assert.assertEquals(1 - p * 2 * i,
                    diceDistribution.probability(lower, upper), 1E-12);
            lower++;
            upper--;
        }
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals(p, diceDistribution.probability(i, i+1), 1E-12);
        }
    }

    /**
     * Simple distribution modeling a 6-sided die
     */
    class DiceDistribution extends AbstractIntegerDistribution {
        public static final long serialVersionUID = 23734213;

        private final double p = 1d/6d;

        @Override
        public double probability(int x) {
            if (x < 1 || x > 6) {
                return 0;
            } else {
                return p;
            }
        }

        @Override
        public double cumulativeProbability(int x) {
            if (x < 1) {
                return 0;
            } else if (x >= 6) {
                return 1;
            } else {
                return p * x;
            }
        }

        @Override
        public double getMean() {
            return 3.5;
        }

        @Override
        public double getVariance() {
            return 70/24;  // E(X^2) - E(X)^2
        }

        @Override
        public int getSupportLowerBound() {
            return 1;
        }

        @Override
        public int getSupportUpperBound() {
            return 6;
        }

        @Override
        public final boolean isSupportConnected() {
            return true;
        }
    }
}
