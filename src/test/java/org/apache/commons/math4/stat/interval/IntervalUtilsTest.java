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
package org.apache.commons.math4.stat.interval;

import org.apache.commons.math4.stat.interval.AgrestiCoullInterval;
import org.apache.commons.math4.stat.interval.ClopperPearsonInterval;
import org.apache.commons.math4.stat.interval.ConfidenceInterval;
import org.apache.commons.math4.stat.interval.IntervalUtils;
import org.apache.commons.math4.stat.interval.NormalApproximationInterval;
import org.apache.commons.math4.stat.interval.WilsonScoreInterval;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the IntervalUtils class.
 *
 */
public class IntervalUtilsTest {

    private final int successes = 50;
    private final int trials = 500;
    private final double confidenceLevel = 0.9;

    // values to test must be exactly the same
    private final double eps = 0.0;

    @Test
    public void testAgrestiCoull() {
        checkConfidenceIntervals(new AgrestiCoullInterval().createInterval(trials, successes, confidenceLevel),
                                 IntervalUtils.getAgrestiCoullInterval(trials, successes, confidenceLevel));
    }

    @Test
    public void testClopperPearson() {
        checkConfidenceIntervals(new ClopperPearsonInterval().createInterval(trials, successes, confidenceLevel),
                                 IntervalUtils.getClopperPearsonInterval(trials, successes, confidenceLevel));
    }

    @Test
    public void testNormalApproximation() {
        checkConfidenceIntervals(new NormalApproximationInterval().createInterval(trials, successes, confidenceLevel),
                                 IntervalUtils.getNormalApproximationInterval(trials, successes, confidenceLevel));
    }

    @Test
    public void testWilsonScore() {
        checkConfidenceIntervals(new WilsonScoreInterval().createInterval(trials, successes, confidenceLevel),
                                 IntervalUtils.getWilsonScoreInterval(trials, successes, confidenceLevel));
    }

    private void checkConfidenceIntervals(ConfidenceInterval expected, ConfidenceInterval actual) {
        Assert.assertEquals(expected.getLowerBound(), actual.getLowerBound(), eps);
        Assert.assertEquals(expected.getUpperBound(), actual.getUpperBound(), eps);
        Assert.assertEquals(expected.getConfidenceLevel(), actual.getConfidenceLevel(), eps);
    }
}
