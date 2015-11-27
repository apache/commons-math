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
import org.apache.commons.math4.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.analysis.function.Sigmoid;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NullArgumentException;
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
        final DerivativeStructure f0 = f.value(new DerivativeStructure(1, 1, 0, 0.0));

        Assert.assertEquals(0.25, f0.getPartialDerivative(1), 0);
    }

    @Test
    public void testDerivativesHighOrder() {
        DerivativeStructure s = new Sigmoid(1, 3).value(new DerivativeStructure(1, 5, 0, 1.2));
        Assert.assertEquals(2.5370495669980352859, s.getPartialDerivative(0), 5.0e-16);
        Assert.assertEquals(0.35578888129361140441, s.getPartialDerivative(1), 6.0e-17);
        Assert.assertEquals(-0.19107626464144938116,  s.getPartialDerivative(2), 6.0e-17);
        Assert.assertEquals(-0.02396830286286711696,  s.getPartialDerivative(3), 4.0e-17);
        Assert.assertEquals(0.21682059798981049049,   s.getPartialDerivative(4), 3.0e-17);
        Assert.assertEquals(-0.19186320234632658055,  s.getPartialDerivative(5), 2.0e-16);
    }

    @Test
    public void testDerivativeLargeArguments() {
        final Sigmoid f = new Sigmoid(1, 2);

        Assert.assertEquals(0, f.value(new DerivativeStructure(1, 1, 0, Double.NEGATIVE_INFINITY)).getPartialDerivative(1), 0);
        Assert.assertEquals(0, f.value(new DerivativeStructure(1, 1, 0, -Double.MAX_VALUE)).getPartialDerivative(1), 0);
        Assert.assertEquals(0, f.value(new DerivativeStructure(1, 1, 0, -1e50)).getPartialDerivative(1), 0);
        Assert.assertEquals(0, f.value(new DerivativeStructure(1, 1, 0, -1e3)).getPartialDerivative(1), 0);
        Assert.assertEquals(0, f.value(new DerivativeStructure(1, 1, 0, 1e3)).getPartialDerivative(1), 0);
        Assert.assertEquals(0, f.value(new DerivativeStructure(1, 1, 0, 1e50)).getPartialDerivative(1), 0);
        Assert.assertEquals(0, f.value(new DerivativeStructure(1, 1, 0, Double.MAX_VALUE)).getPartialDerivative(1), 0);
        Assert.assertEquals(0, f.value(new DerivativeStructure(1, 1, 0, Double.POSITIVE_INFINITY)).getPartialDerivative(1), 0);
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
