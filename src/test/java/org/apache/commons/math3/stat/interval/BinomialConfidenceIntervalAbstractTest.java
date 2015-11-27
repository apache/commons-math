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
package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.interval.BinomialConfidenceInterval;
import org.apache.commons.math3.stat.interval.ConfidenceInterval;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for the BinomialConfidenceInterval implementations.
 *
 */
public abstract class BinomialConfidenceIntervalAbstractTest {

    protected BinomialConfidenceInterval testStatistic;

    private final int successes = 50;
    private final int trials = 500;
    private final double confidenceLevel = 0.9;

    protected abstract BinomialConfidenceInterval createBinomialConfidenceInterval();

    /**
     * Returns the confidence interval for the given statistic with the following values:
     *
     * <ul>
     *  <li>trials: 500</li>
     *  <li>successes: 50</li>
     *  <li>confidenceLevel: 0.9</li>
     * </ul>
     * @return the Confidence Interval for the given values
     */
    protected ConfidenceInterval createStandardTestInterval() {
        return testStatistic.createInterval(trials, successes, confidenceLevel);
    }

    @Before
    public void setUp() {
        testStatistic = createBinomialConfidenceInterval();
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testZeroConfidencelevel() {
        testStatistic.createInterval(trials, successes, 0d);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testOneConfidencelevel() {
        testStatistic.createInterval(trials, successes, 1d);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testZeroTrials() {
        testStatistic.createInterval(0, 0, confidenceLevel);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testNegativeSuccesses() {
        testStatistic.createInterval(trials, -1, confidenceLevel);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testSuccessesExceedingTrials() {
        testStatistic.createInterval(trials, trials + 1, confidenceLevel);
    }
}
