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
        final UnivariateRealFunction f = new Gaussian();

        Assert.assertEquals(1 / FastMath.sqrt(2 * Math.PI), f.value(0), EPS);
    }

    @Test
    public void testLargeArguments() {
        final UnivariateRealFunction f = new Gaussian();

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
        final UnivariateRealFunction dfdx = f.derivative();

        Assert.assertEquals(0, dfdx.value(0), 0);
    }

    @Test
    public void testDerivativeLargeArguments() {
        final Gaussian f = new Gaussian(0, 1e-50);
        final UnivariateRealFunction dfdx = f.derivative();

        Assert.assertEquals(0, dfdx.value(Double.NEGATIVE_INFINITY), 0);
        Assert.assertEquals(0, dfdx.value(-Double.MAX_VALUE), 0);
        Assert.assertEquals(0, f.value(-1e50), 0);
        Assert.assertEquals(0, f.value(-1e2), 0);
        Assert.assertEquals(0, f.value(1e2), 0);
        Assert.assertEquals(0, f.value(1e50), 0);
        Assert.assertEquals(0, dfdx.value(Double.MAX_VALUE), 0);
        Assert.assertEquals(0, dfdx.value(Double.POSITIVE_INFINITY), 0);        
    }

    @Test
    public void testDerivativeNaN() {
        final Gaussian f = new Gaussian(0, 1e-50);
        final UnivariateRealFunction dfdx = f.derivative();

        Assert.assertTrue(Double.isNaN(dfdx.value(Double.NaN)));
    }
}
