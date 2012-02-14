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

package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link Sigmoid}.
 */
public class SigmoidTest {
    private final double EPS = Math.ulp(1d);

    @Test
    public void testSomeValues() {
        final UnivariateFunction f = new Sigmoid();

        Assert.assertEquals(0.5, f.value(0), EPS);
        Assert.assertEquals(0, f.value(Double.NEGATIVE_INFINITY), EPS);
        Assert.assertEquals(1, f.value(Double.POSITIVE_INFINITY), EPS);
    }

    @Test
    public void testDerivative() {
        final Sigmoid f = new Sigmoid();
        final UnivariateFunction dfdx = f.derivative();

        Assert.assertEquals(0.25, dfdx.value(0), 0);
    }

    @Test
    public void testDerivativeLargeArguments() {
        final Sigmoid f = new Sigmoid(1, 2);
        final UnivariateFunction dfdx = f.derivative();

        Assert.assertEquals(0, dfdx.value(Double.NEGATIVE_INFINITY), 0);
        Assert.assertEquals(0, dfdx.value(-Double.MAX_VALUE), 0);
        Assert.assertEquals(0, dfdx.value(-1e50), 0);
        Assert.assertEquals(0, dfdx.value(-1e3), 0);
        Assert.assertEquals(0, dfdx.value(1e3), 0);
        Assert.assertEquals(0, dfdx.value(1e50), 0);
        Assert.assertEquals(0, dfdx.value(Double.MAX_VALUE), 0);
        Assert.assertEquals(0, dfdx.value(Double.POSITIVE_INFINITY), 0);        
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage1() {
        final Sigmoid.Parametric g = new Sigmoid.Parametric();
        g.value(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage2() {
        final Sigmoid.Parametric g = new Sigmoid.Parametric();
        g.value(0, new double[] {0});
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage3() {
        final Sigmoid.Parametric g = new Sigmoid.Parametric();
        g.gradient(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage4() {
        final Sigmoid.Parametric g = new Sigmoid.Parametric();
        g.gradient(0, new double[] {0});
    }

    @Test
    public void testParametricValue() {
        final double lo = 2;
        final double hi = 3;
        final Sigmoid f = new Sigmoid(lo, hi);

        final Sigmoid.Parametric g = new Sigmoid.Parametric();
        Assert.assertEquals(f.value(-1), g.value(-1, new double[] {lo, hi}), 0);
        Assert.assertEquals(f.value(0), g.value(0, new double[] {lo, hi}), 0);
        Assert.assertEquals(f.value(2), g.value(2, new double[] {lo, hi}), 0);
    }
}
