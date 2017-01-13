/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class Expm1OverIdentityTest {
    @Test
    public void testValue() {
        final Expm1OverIdentity f = new Expm1OverIdentity();

        final double testCases[][] = {
            { 1    , 1.7182818284590452 },
            { 0.5  , 1.2974425414002563 },
            { 1e-10, 1.0000000000500000 },
            { 5e-9 , 1.0000000025000000 },
        };

        for (final double[] testCase : testCases) {
            final double x = testCase[0];
            final double y = testCase[1];
            final double fX = f.value(x);
            Assert.assertEquals("x=" + x, y, fX, FastMath.ulp(y));
        }
    }

    @Test
    public void testDerivative() {
        final Expm1OverIdentity f = new Expm1OverIdentity();
        final UnivariateFunction expectedDerivative = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return ( (x-1)*FastMath.exp(x) + 1 ) / (x*x);
            }
        };

        for (double x = 1; x < 100; x++) {
            final double expected = expectedDerivative.value(x);
            final double dfX = f.value(new DerivativeStructure(1, 1, 0, x)).getPartialDerivative(1);
            Assert.assertEquals("x=" + x, expected, dfX, FastMath.ulp(expected));
        }

        for (double x = 1e-5; x < 1; x*=1.1) {
            final double expected = expectedDerivative.value(x);
            final double dfX = f.value(new DerivativeStructure(1, 1, 0, x)).getPartialDerivative(1);
            Assert.assertEquals("x=" + x, expected, dfX, 1e-5);
        }
    }

    @Test
    public void testHigherOrderDerivatives() {
        final Expm1OverIdentity f = new Expm1OverIdentity();
        final DerivativeStructure result = f.value(new DerivativeStructure(1, 5, 0, 2));
        Assert.assertEquals(3.1945280494653251, result.getPartialDerivative(0), 1.0e-16);
        Assert.assertEquals(2.0972640247326626, result.getPartialDerivative(1), 1.0e-16);
        Assert.assertEquals(1.5972640247326626, result.getPartialDerivative(2), 1.0e-16);
        Assert.assertEquals(1.2986320123663313, result.getPartialDerivative(3), 1.0e-15);
        Assert.assertEquals(1.0972640247326626, result.getPartialDerivative(4), 1.0e-15);
        Assert.assertEquals(0.9513679876336687, result.getPartialDerivative(5), 1.0e-15);
    }
}
