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

package org.apache.commons.math4.analysis.function;

import org.apache.commons.math4.analysis.UnivariateFunction;
import org.apache.commons.math4.analysis.function.StepFunction;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NoDataException;
import org.apache.commons.math4.exception.NonMonotonicSequenceException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link StepFunction}.
 */
public class StepFunctionTest {
    private final double EPS = Math.ulp(1d);

    @Test(expected=NullArgumentException.class)
    public void testPreconditions1() {
        new StepFunction(null, new double[] {0, -1, -2});
    }

    @Test(expected=NullArgumentException.class)
    public void testPreconditions2() {
        new StepFunction(new double[] {0, 1}, null);
    }

    @Test(expected=NoDataException.class)
    public void testPreconditions3() {
        new StepFunction(new double[] {0}, new double[] {});
    }

    @Test(expected=NoDataException.class)
    public void testPreconditions4() {
        new StepFunction(new double[] {}, new double[] {0});
    }

    @Test(expected=DimensionMismatchException.class)
    public void testPreconditions5() {
        new StepFunction(new double[] {0, 1}, new double[] {0, -1, -2});
    }

    @Test(expected=NonMonotonicSequenceException.class)
    public void testPreconditions6() {
        new StepFunction(new double[] {1, 0, 1}, new double[] {0, -1, -2});
    }

    @Test
    public void testSomeValues() {
        final double[] x = { -2, -0.5, 0, 1.9, 7.4, 21.3 };
        final double[] y = { 4, -1, -5.5, 0.4, 5.8, 51.2 };

        final UnivariateFunction f = new StepFunction(x, y);

        Assert.assertEquals(4, f.value(Double.NEGATIVE_INFINITY), EPS);
        Assert.assertEquals(4, f.value(-10), EPS);
        Assert.assertEquals(-1, f.value(-0.4), EPS);
        Assert.assertEquals(-5.5, f.value(0), EPS);
        Assert.assertEquals(0.4, f.value(2), EPS);
        Assert.assertEquals(5.8, f.value(10), EPS);
        Assert.assertEquals(51.2, f.value(30), EPS);
        Assert.assertEquals(51.2, f.value(Double.POSITIVE_INFINITY), EPS);
    }

    @Test
    public void testEndpointBehavior() {
        final double[] x = {0, 1, 2, 3};
        final double[] xp = {-8, 1, 2, 3};
        final double[] y = {1, 2, 3, 4};
        final UnivariateFunction f = new StepFunction(x, y);
        final UnivariateFunction fp = new StepFunction(xp, y);
        Assert.assertEquals(f.value(-8), fp.value(-8), EPS);
        Assert.assertEquals(f.value(-10), fp.value(-10), EPS);
        Assert.assertEquals(f.value(0), fp.value(0), EPS);
        Assert.assertEquals(f.value(0.5), fp.value(0.5), EPS);
        for (int i = 0; i < x.length; i++) {
           Assert.assertEquals(y[i], f.value(x[i]), EPS);
           if (i > 0) {
               Assert.assertEquals(y[i - 1], f.value(x[i] - 0.5), EPS);
           } else {
               Assert.assertEquals(y[0], f.value(x[i] - 0.5), EPS);
           }
        }
    }

    @Test
    public void testHeaviside() {
        final UnivariateFunction h = new StepFunction(new double[] {-1, 0},
                                                          new double[] {0, 1});

        Assert.assertEquals(0, h.value(Double.NEGATIVE_INFINITY), 0);
        Assert.assertEquals(0, h.value(-Double.MAX_VALUE), 0);
        Assert.assertEquals(0, h.value(-2), 0);
        Assert.assertEquals(0, h.value(-Double.MIN_VALUE), 0);
        Assert.assertEquals(1, h.value(0), 0);
        Assert.assertEquals(1, h.value(2), 0);
        Assert.assertEquals(1, h.value(Double.POSITIVE_INFINITY), 0);
    }
}
