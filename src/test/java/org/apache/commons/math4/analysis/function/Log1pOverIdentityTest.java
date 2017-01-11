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

public class Log1pOverIdentityTest {
    @Test
    public void testValue() {
        final Log1pOverIdentity f = new Log1pOverIdentity();

        final double testCases[][] = {
            { 1    , 0.6931471805599453 },
            { 0.5  , 0.8109302162163288 },
            { 1e-10, 0.9999999999500000 },
            { 5e-9 , 0.9999999975000000 },
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
        final Log1pOverIdentity f = new Log1pOverIdentity();
        final UnivariateFunction expectedDerivative = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return ( 1/(1+x) - f.value(x) ) / x;
            }
        };

        for (double x = 1e-10; x < 1; x*=2) {
            final double expected = expectedDerivative.value(x);
            final double dfX = f.value(new DerivativeStructure(1, 1, 0, x)).getPartialDerivative(1);
            Assert.assertEquals("x=" + x, expected, dfX, 1e-7);
        }

        for (double x = 1; x < 1e10; x*=2) {
            final double expected = expectedDerivative.value(x);
            final double dfX = f.value(new DerivativeStructure(1, 1, 0, x)).getPartialDerivative(1);
            Assert.assertEquals("x=" + x, expected, dfX, FastMath.ulp(expected));
        }
    }

    @Test
    public void testHigherOrderDerivatives() {
        final Log1pOverIdentity f = new Log1pOverIdentity();
        final DerivativeStructure result = f.value(new DerivativeStructure(1, 5, 0, 2));
        Assert.assertEquals( 0.5493061443340549, result.getPartialDerivative(0), 1.0e-16);
        Assert.assertEquals(-0.1079864055003608, result.getPartialDerivative(1), 1.0e-16);
        Assert.assertEquals( 0.0524308499448052, result.getPartialDerivative(2), 1.0e-16);
        Assert.assertEquals(-0.0416092378801708, result.getPartialDerivative(3), 1.0e-16);
        Assert.assertEquals( 0.0461814387233045, result.getPartialDerivative(4), 1.0e-16);
        Assert.assertEquals(-0.0660708807588785, result.getPartialDerivative(5), 1.0e-16);
    }
}
