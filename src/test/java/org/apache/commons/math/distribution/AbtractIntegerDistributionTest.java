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
package org.apache.commons.math.distribution;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for AbstractIntegerDistribution default implementations.
 *
 * @version $Id$
 */
public class AbtractIntegerDistributionTest {

    protected final DiceDistribution diceDistribution = new DiceDistribution();
    protected final double p = diceDistribution.probability(1);
    
    @Test
    public void testCumulativeProbabilitiesSingleIntegerArguments() throws Exception {
        int lower = 1;
        for (int i = 1; i < 7; i++) {
            Assert.assertEquals(p * i,
                    diceDistribution.cumulativeProbability(lower), Double.MIN_VALUE);
            lower++;
        }
        Assert.assertEquals(0,
                diceDistribution.cumulativeProbability(-1), Double.MIN_VALUE);
        Assert.assertEquals(1,
                diceDistribution.cumulativeProbability(7), Double.MIN_VALUE);
    }
    
    @Test
    public void testCumulativeProbabilitiesRangeArguments() throws Exception {
        int lower = 1;
        int upper = 6;
        for (int i = 0; i < 2; i++) {
            // cum(1,6) = p(1 <= X <= 6) = 1, cum(2,5) = 4/6, cum(3,4) = 2/6 
            Assert.assertEquals(1 - p * 2 * i, 
                    diceDistribution.cumulativeProbability(lower, upper), 1E-12);
            lower++;
            upper--;
        }
        for (int i = 1; i < 7; i++) {
            Assert.assertEquals(p, diceDistribution.cumulativeProbability(i, i), 1E-12);
        }
    }
    
    /**
     * Simple distribution modeling a 6-sided die
     */
    class DiceDistribution extends AbstractIntegerDistribution {
        public static final long serialVersionUID = 23734213;

        private final double p = 1d/6d;

        public double probability(int x) {
            if (x < 1 || x > 6) {
                return 0;
            } else {
                return p;
            }
        }

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
        protected int getDomainLowerBound(double p) {
            return 1;
        }

        @Override
        protected int getDomainUpperBound(double p) {
            return 6;
        }

        public double getNumericalMean() {
            return 3.5;
        }

        public double getNumericalVariance() {
            return 12.5 - 3.5 * 3.5;  // E(X^2) - E(X)^2
        }

        public int getSupportLowerBound() {
            return 1;
        }

        public int getSupportUpperBound() {
            return 6;
        }

        public final boolean isSupportConnected() {
            return true;
        }
    }
}
