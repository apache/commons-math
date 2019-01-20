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

import static org.junit.Assert.assertEquals;

import org.apache.commons.math4.stat.interval.BinomialConfidenceInterval;
import org.apache.commons.math4.stat.interval.ClopperPearsonInterval;
import org.apache.commons.math4.stat.interval.ConfidenceInterval;
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

    /*
     * See MATH-1401 for more. Handles special cases for ClopperPearson, when the number
     * of successes is zero, and when the number of successes and number of trials are
     * equals.
     */

    @Test
    public void testNumberOfSuccessesIsZero() {
        ConfidenceInterval ci = testStatistic.createInterval(1, 0, 0.95);
        ConfidenceInterval expected = new ConfidenceInterval(0.0, 0.975, ci.getConfidenceLevel());
        //assertEquals(expected, ci); // TBD: ConfidenceInterval does not contain an equal method yet
        assertEquals(expected.getLowerBound(), ci.getLowerBound(), 0.0d);
        assertEquals(expected.getUpperBound(), ci.getUpperBound(), 0.0d);
    }

    @Test
    public void testNumberOfSuccessesAndNumberOfTrialsAreEquals() {
        ConfidenceInterval ci = testStatistic.createInterval(1, 1, 0.95);
        ConfidenceInterval expected = new ConfidenceInterval(0.025, 1.0, ci.getConfidenceLevel());
        //assertEquals(expected, ci); // TBD: ConfidenceInterval does not contain an equal method yet
        assertEquals(expected.getLowerBound(), ci.getLowerBound(), 0.0001d);
        assertEquals(expected.getUpperBound(), ci.getUpperBound(), 0.0d);
    }
}
