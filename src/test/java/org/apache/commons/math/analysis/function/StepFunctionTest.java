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

package org.apache.commons.math.analysis.function;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NonMonotonousSequenceException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.NoDataException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link StepFunction}.
 */
public class StepFunctionTest {
    private final double EPS = Math.ulp(1d);

    @Test
    public void testPreconditions() {
        try {
            final UnivariateRealFunction f = new StepFunction(null,
                                                              new double[] {0, -1, -2});
        } catch (NullArgumentException e) {
            // Expected.
        }
        try {
            final UnivariateRealFunction f = new StepFunction(new double[] {0, 1},
                                                              null);
        } catch (NullArgumentException e) {
            // Expected.
        }

        try {
            final UnivariateRealFunction f = new StepFunction(new double[] {0},
                                                              new double[] {});
        } catch (NoDataException e) {
            // Expected.
        }

        try {
            final UnivariateRealFunction f = new StepFunction(new double[] {},
                                                              new double[] {0});
        } catch (NoDataException e) {
            // Expected.
        }

        try {
            final UnivariateRealFunction f = new StepFunction(new double[] {0, 1},
                                                              new double[] {0, -1, -2});
        } catch (DimensionMismatchException e) {
            // Expected.
        }

        try {
            final UnivariateRealFunction f = new StepFunction(new double[] {1, 0, 1},
                                                              new double[] {0, -1, -2});
        } catch (NonMonotonousSequenceException e) {
            // Expected.
        }
    }

    @Test
    public void testSomeValues() {
        final double[] x = { -2, -0.5, 0, 1.9, 7.4, 21.3 };
        final double[] y = { 4, -1, -5.5, 0.4, 5.8, 51.2 };

        final UnivariateRealFunction f = new StepFunction(x, y);

        Assert.assertEquals(4, f.value(Double.NEGATIVE_INFINITY), EPS);
        Assert.assertEquals(4, f.value(-10), EPS);
        Assert.assertEquals(-1, f.value(-0.4), EPS);
        Assert.assertEquals(-5.5, f.value(0), EPS);
        Assert.assertEquals(0.4, f.value(2), EPS);
        Assert.assertEquals(5.8, f.value(10), EPS);
        Assert.assertEquals(51.2, f.value(30), EPS);
        Assert.assertEquals(51.2, f.value(Double.POSITIVE_INFINITY), EPS);
    }
}
