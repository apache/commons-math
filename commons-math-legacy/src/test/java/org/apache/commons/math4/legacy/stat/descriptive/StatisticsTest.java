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
package org.apache.commons.math4.legacy.stat.descriptive;

import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.stat.descriptive.Statistics.Percentile;
import org.apache.commons.math4.legacy.stat.descriptive.Statistics.StorelessSumOfSquares;
import org.apache.commons.math4.legacy.stat.descriptive.Statistics.SumOfSquares;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test cases for the {@link Statistics} class.
 */
public class StatisticsTest {
    @Test
    public void testUnsupportedMethods() {
        final double[] x = {1, 2, 3};
        final SumOfSquares s = SumOfSquares.getInstance();
        Assertions.assertThrows(IllegalStateException.class, () -> s.evaluate(x));
    }

    @Test
    public void testUnsupportedStorelessMethods() {
        final double[] x = {1, 2, 3};
        final StorelessSumOfSquares s = StorelessSumOfSquares.create();
        Assertions.assertThrows(IllegalStateException.class, () -> s.incrementAll(x));
        Assertions.assertThrows(IllegalStateException.class, () -> s.incrementAll(x, 0, 1));
        Assertions.assertThrows(IllegalStateException.class, s::getN);
        Assertions.assertThrows(IllegalStateException.class, () -> s.evaluate(x));
        Assertions.assertThrows(IllegalStateException.class, () -> s.evaluate(x, 0, 1));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, 101, Double.NaN})
    public void testInvalidPercentileThrows(double p) {
        final Percentile stat = Percentile.create(50);
        Assertions.assertThrows(OutOfRangeException.class, () -> stat.setQuantile(p));
        Assertions.assertThrows(OutOfRangeException.class, () -> Percentile.create(p));
    }

    @Test
    public void testPercentile() {
        final Percentile stat = Percentile.create(50);
        Assertions.assertThrows(NullArgumentException.class, () -> stat.evaluate(null));
        Assertions.assertEquals(Double.NaN, stat.evaluate(new double[] {}));
        Assertions.assertEquals(1, stat.evaluate(new double[] {1}));
        Assertions.assertEquals(1.5, stat.evaluate(new double[] {1, 2}));
        Assertions.assertEquals(2, stat.evaluate(new double[] {1, 2, 3}));
    }
}
