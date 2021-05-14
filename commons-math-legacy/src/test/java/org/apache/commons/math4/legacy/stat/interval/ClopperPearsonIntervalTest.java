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
package org.apache.commons.math4.legacy.stat.interval;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the ClopperPearsonInterval class.
 *
 */
public class ClopperPearsonIntervalTest extends BinomialConfidenceIntervalAbstractTest {

    @Override
    protected BinomialConfidenceInterval createBinomialConfidenceInterval() {
        return new ClopperPearsonInterval();
    }

    @Test
    public void testStandardInterval() {
        ConfidenceInterval confidenceInterval = createStandardTestInterval();
        Assert.assertEquals(0.07873857, confidenceInterval.getLowerBound(), 1E-5);
        Assert.assertEquals(0.1248658, confidenceInterval.getUpperBound(), 1E-5);
    }

    @Test
    public void testMath1401() {
        ConfidenceInterval interval = new ClopperPearsonInterval().createInterval(1, 1, 0.95);
        Assert.assertEquals(0.025, interval.getLowerBound(), 1e-16);
        Assert.assertEquals(1, interval.getUpperBound(), 0d);
    }

    // number of successes = 0, number of trials = N
    @Test
    public void testCase1() {
        // Check correctness against values obtained with the Python statsmodels.stats.proportion.proportion_confint
        final int successes = 0;
        final int trials = 10;
        final double confidenceLevel = 0.95;

        // proportion_confint(0,10,method='beta') = (0, 0.3084971078187608)
        final ConfidenceInterval expected = new ConfidenceInterval(0,
                                                                   0.3084971078187608,
                                                                   confidenceLevel);

        check(expected, createBinomialConfidenceInterval().createInterval(trials, successes, confidenceLevel));
    }

    // number of successes = number of trials = N
    @Test
    public void testCase2() {
        // Check correctness against values obtained with the Python statsmodels.stats.proportion.proportion_confint
        final int successes = 10;
        final int trials = 10;
        final double confidenceLevel = 0.95;

        // prop.proportion_confint(10,10,method='beta') = (0.6915028921812392, 1)
        final ConfidenceInterval expected = new ConfidenceInterval(0.6915028921812392,
                                                                   1,
                                                                   confidenceLevel);

        check(expected, createBinomialConfidenceInterval().createInterval(trials, successes, confidenceLevel));
    }

    // number of successes = k, number of trials = N, 0 < k < N
    @Test
    public void testCase3() {
        // Check correctness against values obtained with the Python statsmodels.stats.proportion.proportion_confint
        final int successes = 3;
        final int trials = 10;
        final double confidenceLevel = 0.95;

        // prop.proportion_confint(3,10,method='beta') = (0.06673951117773447, 0.6524528500599972)
        final ConfidenceInterval expected = new ConfidenceInterval(0.06673951117773447,
                                                                   0.6524528500599972,
                                                                   confidenceLevel);

        check(expected, createBinomialConfidenceInterval().createInterval(trials, successes, confidenceLevel));
    }

    private void check(ConfidenceInterval expected,
                       ConfidenceInterval actual) {
        final double relTol = 1.0e-6; // Reasonable relative tolerance for floating point comparison
        // Compare bounds using a relative tolerance
        Assert.assertEquals(expected.getLowerBound(),
                            actual.getLowerBound(),
                            relTol * (1.0 + Math.abs(expected.getLowerBound())));
        Assert.assertEquals(expected.getUpperBound(),
                            actual.getUpperBound(),
                            relTol * (1.0 + Math.abs(expected.getUpperBound())));
        // The confidence level must be exact
        Assert.assertEquals(expected.getConfidenceLevel(),
                            actual.getConfidenceLevel(),
                            0.0);
    }
}
