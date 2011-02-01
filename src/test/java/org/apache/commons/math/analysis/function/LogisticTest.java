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
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.util.FastMath;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link Logistic}.
 */
public class LogisticTest {
    private final double EPS = Math.ulp(1d);

    @Test(expected=NotStrictlyPositiveException.class)
    public void testPreconditions1() {
        new Logistic(1, 0, 1, 1, 0, -1);
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testPreconditions2() {
        new Logistic(1, 0, 1, 1, 0, 0);
    }

    @Test
    public void testCompareSigmoid() {
        final UnivariateRealFunction sig = new Sigmoid();
        final UnivariateRealFunction sigL = new Logistic(1, 0, 1, 1, 0, 1);

        final double min = -2;
        final double max = 2;
        final int n = 100;
        final double delta = (max - min) / n;
        for (int i = 0; i < n; i++) {
            final double x = min + i * delta;
            Assert.assertEquals("x=" + x, sig.value(x), sigL.value(x), EPS);
        }
    }

    @Test
    public void testSomeValues() {
        final double k = 4;
        final double m = 5;
        final double b = 2;
        final double q = 3;
        final double a = -1;
        final double n = 2;

        final UnivariateRealFunction f = new Logistic(k, m, b, q, a, n);

        double x;
        x = m;
        Assert.assertEquals("x=" + x, a + (k - a) / FastMath.sqrt(1 + q), f.value(x), EPS);

        x = Double.NEGATIVE_INFINITY;
        Assert.assertEquals("x=" + x, a, f.value(x), EPS);

        x = Double.POSITIVE_INFINITY;
        Assert.assertEquals("x=" + x, k, f.value(x), EPS);
    }
}
