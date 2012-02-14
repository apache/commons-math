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
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link Gaussian}.
 */
public class GaussianTest {
    private final double EPS = Math.ulp(1d);

    @Test(expected=NotStrictlyPositiveException.class)
    public void testPreconditions() {
        new Gaussian(1, 2, -1);
    }

    @Test
    public void testSomeValues() {
        final UnivariateFunction f = new Gaussian();

        Assert.assertEquals(1 / FastMath.sqrt(2 * Math.PI), f.value(0), EPS);
    }

    @Test
    public void testLargeArguments() {
        final UnivariateFunction f = new Gaussian();

        Assert.assertEquals(0, f.value(Double.NEGATIVE_INFINITY), 0);
        Assert.assertEquals(0, f.value(-Double.MAX_VALUE), 0);
        Assert.assertEquals(0, f.value(-1e2), 0);
        Assert.assertEquals(0, f.value(1e2), 0);
        Assert.assertEquals(0, f.value(Double.MAX_VALUE), 0);
        Assert.assertEquals(0, f.value(Double.POSITIVE_INFINITY), 0);
    }

    @Test
    public void testDerivative() {
        final Gaussian f = new Gaussian();
        final UnivariateFunction dfdx = f.derivative();

        Assert.assertEquals(0, dfdx.value(0), 0);
    }

    @Test
    public void testDerivativeLargeArguments() {
        final Gaussian f = new Gaussian(0, 1e-50);
        final UnivariateFunction dfdx = f.derivative();

        Assert.assertEquals(0, dfdx.value(Double.NEGATIVE_INFINITY), 0);
        Assert.assertEquals(0, dfdx.value(-Double.MAX_VALUE), 0);
        Assert.assertEquals(0, dfdx.value(-1e50), 0);
        Assert.assertEquals(0, dfdx.value(-1e2), 0);
        Assert.assertEquals(0, dfdx.value(1e2), 0);
        Assert.assertEquals(0, dfdx.value(1e50), 0);
        Assert.assertEquals(0, dfdx.value(Double.MAX_VALUE), 0);
        Assert.assertEquals(0, dfdx.value(Double.POSITIVE_INFINITY), 0);        
    }

    @Test
    public void testDerivativeNaN() {
        final Gaussian f = new Gaussian(0, 1e-50);
        final UnivariateFunction dfdx = f.derivative();

        Assert.assertTrue(Double.isNaN(dfdx.value(Double.NaN)));
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage1() {
        final Gaussian.Parametric g = new Gaussian.Parametric();
        g.value(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage2() {
        final Gaussian.Parametric g = new Gaussian.Parametric();
        g.value(0, new double[] {0});
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testParametricUsage3() {
        final Gaussian.Parametric g = new Gaussian.Parametric();
        g.value(0, new double[] {0, 1, 0});
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage4() {
        final Gaussian.Parametric g = new Gaussian.Parametric();
        g.gradient(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage5() {
        final Gaussian.Parametric g = new Gaussian.Parametric();
        g.gradient(0, new double[] {0});
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testParametricUsage6() {
        final Gaussian.Parametric g = new Gaussian.Parametric();
        g.gradient(0, new double[] {0, 1, 0});
    }

    @Test
    public void testParametricValue() {
        final double norm = 2;
        final double mean = 3;
        final double sigma = 4;
        final Gaussian f = new Gaussian(norm, mean, sigma);

        final Gaussian.Parametric g = new Gaussian.Parametric();
        Assert.assertEquals(f.value(-1), g.value(-1, new double[] {norm, mean, sigma}), 0);
        Assert.assertEquals(f.value(0), g.value(0, new double[] {norm, mean, sigma}), 0);
        Assert.assertEquals(f.value(2), g.value(2, new double[] {norm, mean, sigma}), 0);
    }

    @Test
    public void testParametricGradient() {
        final double norm = 2;
        final double mean = 3;
        final double sigma = 4;
        final Gaussian.Parametric f = new Gaussian.Parametric();

        final double x = 1;
        final double[] grad = f.gradient(1, new double[] {norm, mean, sigma});
        final double diff = x - mean;
        final double n = FastMath.exp(-diff * diff / (2 * sigma * sigma));
        Assert.assertEquals(n, grad[0], EPS);
        final double m = norm * n * diff / (sigma * sigma);
        Assert.assertEquals(m, grad[1], EPS);
        final double s = m * diff / sigma;
        Assert.assertEquals(s, grad[2], EPS);
    }
}
