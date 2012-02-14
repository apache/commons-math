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
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.OutOfRangeException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link Logit}.
 */
public class LogitTest {
    private final double EPS = Math.ulp(1d);

    @Test(expected=OutOfRangeException.class)
    public void testPreconditions1() {
        final double lo = -1;
        final double hi = 2;
        final UnivariateFunction f = new Logit(lo, hi);

        f.value(lo - 1);
    }
    @Test(expected=OutOfRangeException.class)
    public void testPreconditions2() {
        final double lo = -1;
        final double hi = 2;
        final UnivariateFunction f = new Logit(lo, hi);

        f.value(hi + 1);
    }

    @Test
    public void testSomeValues() {
        final double lo = 1;
        final double hi = 2;
        final UnivariateFunction f = new Logit(lo, hi);

        Assert.assertEquals(Double.NEGATIVE_INFINITY, f.value(1), EPS);
        Assert.assertEquals(Double.POSITIVE_INFINITY, f.value(2), EPS);
        Assert.assertEquals(0, f.value(1.5), EPS);
    }

    @Test
    public void testDerivative() {
        final double lo = 1;
        final double hi = 2;
        final Logit f = new Logit(lo, hi);
        final UnivariateFunction dfdx = f.derivative();

        Assert.assertEquals(4, dfdx.value(1.5), EPS);
    }

    @Test
    public void testDerivativeLargeArguments() {
        final Logit f = new Logit(1, 2);
        final UnivariateFunction dfdx = f.derivative();

        Assert.assertEquals(0, dfdx.value(Double.NEGATIVE_INFINITY), 0);
        Assert.assertEquals(0, dfdx.value(-Double.MAX_VALUE), 0);
        Assert.assertEquals(0, dfdx.value(-1e155), 0);
        Assert.assertEquals(0, dfdx.value(1e155), 0);
        Assert.assertEquals(0, dfdx.value(Double.MAX_VALUE), 0);
        Assert.assertEquals(0, dfdx.value(Double.POSITIVE_INFINITY), 0);        
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage1() {
        final Logit.Parametric g = new Logit.Parametric();
        g.value(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage2() {
        final Logit.Parametric g = new Logit.Parametric();
        g.value(0, new double[] {0});
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage3() {
        final Logit.Parametric g = new Logit.Parametric();
        g.gradient(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage4() {
        final Logit.Parametric g = new Logit.Parametric();
        g.gradient(0, new double[] {0});
    }

    @Test(expected=OutOfRangeException.class)
    public void testParametricUsage5() {
        final Logit.Parametric g = new Logit.Parametric();
        g.value(-1, new double[] {0, 1});
    }

    @Test(expected=OutOfRangeException.class)
    public void testParametricUsage6() {
        final Logit.Parametric g = new Logit.Parametric();
        g.value(2, new double[] {0, 1});
    }

    @Test
    public void testParametricValue() {
        final double lo = 2;
        final double hi = 3;
        final Logit f = new Logit(lo, hi);

        final Logit.Parametric g = new Logit.Parametric();
        Assert.assertEquals(f.value(2), g.value(2, new double[] {lo, hi}), 0);
        Assert.assertEquals(f.value(2.34567), g.value(2.34567, new double[] {lo, hi}), 0);
        Assert.assertEquals(f.value(3), g.value(3, new double[] {lo, hi}), 0);
    }

    @Test
    public void testValueWithInverseFunction() {
        final double lo = 2;
        final double hi = 3;
        final Logit f = new Logit(lo, hi);
        final Sigmoid g = new Sigmoid(lo, hi);
        final UnivariateFunction id = FunctionUtils.compose(g, f);
        
        for (int i = 0; i < 10; i++) {
            final double x = lo + Math.random() * (hi - lo);
            Assert.assertEquals(x, id.value(x), EPS);
        }

        Assert.assertEquals(lo, id.value(lo), EPS);
        Assert.assertEquals(hi, id.value(hi), EPS);
    }

    @Test
    public void testDerivativeWithInverseFunction() {
        final double lo = 2;
        final double hi = 3;
        final Logit f = new Logit(lo, hi);
        final UnivariateFunction dfdx = f.derivative();
        final Sigmoid g = new Sigmoid(lo, hi);
        final UnivariateFunction dgdx = g.derivative();
        final UnivariateFunction chain
            = FunctionUtils.compose(new Inverse(), FunctionUtils.compose(dgdx, f));
        
        for (int i = 0; i < 10; i++) {
            final double x = lo + Math.random() * (hi - lo);
            final double r = dfdx.value(x);
            Assert.assertEquals(r, chain.value(x), r * 1e-15);
        }

        Assert.assertEquals(dfdx.value(lo), chain.value(lo), 0); // -inf
        Assert.assertEquals(dfdx.value(hi), chain.value(hi), 0); // +inf
    }
}
