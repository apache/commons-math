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

import org.apache.commons.math4.stat.interval.BinomialConfidenceInterval;
import org.apache.commons.math4.stat.interval.ConfidenceInterval;
import org.apache.commons.math4.stat.interval.WilsonScoreInterval;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the WilsonScoreInterval class.
 *
 */
public class WilsonScoreIntervalTest extends BinomialConfidenceIntervalAbstractTest {

    @Override
    protected BinomialConfidenceInterval createBinomialConfidenceInterval() {
        return new WilsonScoreInterval();
    }

    @Test
    public void testStandardInterval() {
        ConfidenceInterval confidenceInterval = createStandardTestInterval();
        Assert.assertEquals(0.08003919, confidenceInterval.getLowerBound(), 1E-5);
        Assert.assertEquals(0.1242664, confidenceInterval.getUpperBound(), 1E-5);
    }

}
