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

package org.apache.commons.math4.legacy.analysis.differentiation;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math4.legacy.field.ExtendedFieldElementAbstractTest;
import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.numbers.core.ArithmeticUtils;
import org.apache.commons.numbers.combinatorics.Factorial;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.numbers.core.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link DerivativeStructure}.
 */
public class DerivativeStructureTest extends ExtendedFieldElementAbstractTest<DerivativeStructure> {

    @Override
    protected DerivativeStructure build(final double x) {
        return new DerivativeStructure(2, 1, 0, x);
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testWrongVariableIndex() {
        new DerivativeStructure(3, 1, 3, 1.0);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testMissingOrders() {
        new DerivativeStructure(3, 1, 0, 1.0).getPartialDerivative(0, 1);
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testTooLargeOrder() {
        new DerivativeStructure(3, 1, 0, 1.0).getPartialDerivative(1, 1, 2);
    }

    @Test
    public void testVariableWithoutDerivative0() {
        DerivativeStructure v = new DerivativeStructure(1, 0, 0, 1.0);
        Assert.assertEquals(1.0, v.getValue(), 1.0e-15);
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testVariableWithoutDerivative1() {
        DerivativeStructure v = new DerivativeStructure(1, 0, 0, 1.0);
        Assert.assertEquals(1.0, v.getPartialDerivative(1), 1.0e-15);
    }

    @Test
    public void testVariable() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            checkF0F1(new DerivativeStructure(3, maxOrder, 0, 1.0),
                      1.0, 1.0, 0.0, 0.0);
            checkF0F1(new DerivativeStructure(3, maxOrder, 1, 2.0),
                      2.0, 0.0, 1.0, 0.0);
            checkF0F1(new DerivativeStructure(3, maxOrder, 2, 3.0),
                      3.0, 0.0, 0.0, 1.0);
        }
    }

    @Test
    public void testConstant() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            checkF0F1(new DerivativeStructure(3, maxOrder, JdkMath.PI),
                      JdkMath.PI, 0.0, 0.0, 0.0);
        }
    }

    @Test
    public void testCreateConstant() {
        DerivativeStructure a = new DerivativeStructure(3, 2, 0, 1.3);
        DerivativeStructure b = a.createConstant(2.5);
        Assert.assertEquals(a.getFreeParameters(), b.getFreeParameters());
        Assert.assertEquals(a.getOrder(), b.getOrder());
        checkEquals(a.getField().getOne().multiply(2.5), b, 1.0e-15);
    }

    @Test
    public void testPrimitiveAdd() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            checkF0F1(new DerivativeStructure(3, maxOrder, 0, 1.0).add(5), 6.0, 1.0, 0.0, 0.0);
            checkF0F1(new DerivativeStructure(3, maxOrder, 1, 2.0).add(5), 7.0, 0.0, 1.0, 0.0);
            checkF0F1(new DerivativeStructure(3, maxOrder, 2, 3.0).add(5), 8.0, 0.0, 0.0, 1.0);
        }
    }

    @Test
    public void testAdd() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            DerivativeStructure x = new DerivativeStructure(3, maxOrder, 0, 1.0);
            DerivativeStructure y = new DerivativeStructure(3, maxOrder, 1, 2.0);
            DerivativeStructure z = new DerivativeStructure(3, maxOrder, 2, 3.0);
            DerivativeStructure xyz = x.add(y.add(z));
            checkF0F1(xyz, x.getValue() + y.getValue() + z.getValue(), 1.0, 1.0, 1.0);
        }
    }

    @Test
    public void testPrimitiveSubtract() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            checkF0F1(new DerivativeStructure(3, maxOrder, 0, 1.0).subtract(5), -4.0, 1.0, 0.0, 0.0);
            checkF0F1(new DerivativeStructure(3, maxOrder, 1, 2.0).subtract(5), -3.0, 0.0, 1.0, 0.0);
            checkF0F1(new DerivativeStructure(3, maxOrder, 2, 3.0).subtract(5), -2.0, 0.0, 0.0, 1.0);
        }
    }

    @Test
    public void testSubtract() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            DerivativeStructure x = new DerivativeStructure(3, maxOrder, 0, 1.0);
            DerivativeStructure y = new DerivativeStructure(3, maxOrder, 1, 2.0);
            DerivativeStructure z = new DerivativeStructure(3, maxOrder, 2, 3.0);
            DerivativeStructure xyz = x.subtract(y.subtract(z));
            checkF0F1(xyz, x.getValue() - (y.getValue() - z.getValue()), 1.0, -1.0, 1.0);
        }
    }

    @Test
    public void testPrimitiveMultiply() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            checkF0F1(new DerivativeStructure(3, maxOrder, 0, 1.0).multiply(5),  5.0, 5.0, 0.0, 0.0);
            checkF0F1(new DerivativeStructure(3, maxOrder, 1, 2.0).multiply(5), 10.0, 0.0, 5.0, 0.0);
            checkF0F1(new DerivativeStructure(3, maxOrder, 2, 3.0).multiply(5), 15.0, 0.0, 0.0, 5.0);
        }
    }

    @Test
    public void testMultiply() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            DerivativeStructure x = new DerivativeStructure(3, maxOrder, 0, 1.0);
            DerivativeStructure y = new DerivativeStructure(3, maxOrder, 1, 2.0);
            DerivativeStructure z = new DerivativeStructure(3, maxOrder, 2, 3.0);
            DerivativeStructure xyz = x.multiply(y.multiply(z));
            for (int i = 0; i <= maxOrder; ++i) {
                for (int j = 0; j <= maxOrder; ++j) {
                    for (int k = 0; k <= maxOrder; ++k) {
                        if (i + j + k <= maxOrder) {
                            Assert.assertEquals((i == 0 ? x.getValue() : (i == 1 ? 1.0 : 0.0)) *
                                                (j == 0 ? y.getValue() : (j == 1 ? 1.0 : 0.0)) *
                                                (k == 0 ? z.getValue() : (k == 1 ? 1.0 : 0.0)),
                                                xyz.getPartialDerivative(i, j, k),
                                                1.0e-15);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testNegate() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            checkF0F1(new DerivativeStructure(3, maxOrder, 0, 1.0).negate(), -1.0, -1.0, 0.0, 0.0);
            checkF0F1(new DerivativeStructure(3, maxOrder, 1, 2.0).negate(), -2.0, 0.0, -1.0, 0.0);
            checkF0F1(new DerivativeStructure(3, maxOrder, 2, 3.0).negate(), -3.0, 0.0, 0.0, -1.0);
        }
    }

    @Test
    public void testReciprocal() {
        for (double x = 0.1; x < 1.2; x += 0.1) {
            DerivativeStructure r = new DerivativeStructure(1, 6, 0, x).reciprocal();
            Assert.assertEquals(1 / x, r.getValue(), 1.0e-15);
            for (int i = 1; i < r.getOrder(); ++i) {
                double expected = ArithmeticUtils.pow(-1, i) * Factorial.value(i) /
                                  JdkMath.pow(x, i + 1);
                Assert.assertEquals(expected, r.getPartialDerivative(i), 1.0e-15 * JdkMath.abs(expected));
            }
        }
    }

    @Test
    public void testPow() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            for (int n = 0; n < 10; ++n) {

                DerivativeStructure x = new DerivativeStructure(3, maxOrder, 0, 1.0);
                DerivativeStructure y = new DerivativeStructure(3, maxOrder, 1, 2.0);
                DerivativeStructure z = new DerivativeStructure(3, maxOrder, 2, 3.0);
                List<DerivativeStructure> list = Arrays.asList(x, y, z,
                                                               x.add(y).add(z),
                                                               x.multiply(y).multiply(z));

                if (n == 0) {
                    for (DerivativeStructure ds : list) {
                        checkEquals(ds.getField().getOne(), ds.pow(n), 1.0e-15);
                    }
                } else if (n == 1) {
                    for (DerivativeStructure ds : list) {
                        checkEquals(ds, ds.pow(n), 1.0e-15);
                    }
                } else {
                    for (DerivativeStructure ds : list) {
                        DerivativeStructure p = ds.getField().getOne();
                        for (int i = 0; i < n; ++i) {
                            p = p.multiply(ds);
                        }
                        checkEquals(p, ds.pow(n), 1.0e-15);
                    }
                }
            }
        }
    }

    @Test
    public void testPowDoubleDS() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {

            DerivativeStructure x = new DerivativeStructure(3, maxOrder, 0, 0.1);
            DerivativeStructure y = new DerivativeStructure(3, maxOrder, 1, 0.2);
            DerivativeStructure z = new DerivativeStructure(3, maxOrder, 2, 0.3);
            List<DerivativeStructure> list = Arrays.asList(x, y, z,
                                                           x.add(y).add(z),
                                                           x.multiply(y).multiply(z));

            for (DerivativeStructure ds : list) {
                // the special case a = 0 is included here
                for (double a : new double[] { 0.0, 0.1, 1.0, 2.0, 5.0 }) {
                    DerivativeStructure reference = (a == 0) ?
                                                    x.getField().getZero() :
                                                    new DerivativeStructure(3, maxOrder, a).pow(ds);
                    DerivativeStructure result = DerivativeStructure.pow(a, ds);
                    checkEquals(reference, result, 1.0e-15);
                }
            }

            // negative base: -1^x can be evaluated for integers only, so value is sometimes OK, derivatives are always NaN
            DerivativeStructure negEvenInteger = DerivativeStructure.pow(-2.0, new DerivativeStructure(3,  maxOrder, 0, 2.0));
            Assert.assertEquals(4.0, negEvenInteger.getValue(), 1.0e-15);
            Assert.assertTrue(Double.isNaN(negEvenInteger.getPartialDerivative(1, 0, 0)));
            DerivativeStructure negOddInteger = DerivativeStructure.pow(-2.0, new DerivativeStructure(3,  maxOrder, 0, 3.0));
            Assert.assertEquals(-8.0, negOddInteger.getValue(), 1.0e-15);
            Assert.assertTrue(Double.isNaN(negOddInteger.getPartialDerivative(1, 0, 0)));
            DerivativeStructure negNonInteger = DerivativeStructure.pow(-2.0, new DerivativeStructure(3,  maxOrder, 0, 2.001));
            Assert.assertTrue(Double.isNaN(negNonInteger.getValue()));
            Assert.assertTrue(Double.isNaN(negNonInteger.getPartialDerivative(1, 0, 0)));

            DerivativeStructure zeroNeg = DerivativeStructure.pow(0.0, new DerivativeStructure(3,  maxOrder, 0, -1.0));
            Assert.assertTrue(Double.isNaN(zeroNeg.getValue()));
            Assert.assertTrue(Double.isNaN(zeroNeg.getPartialDerivative(1, 0, 0)));
            DerivativeStructure posNeg = DerivativeStructure.pow(2.0, new DerivativeStructure(3,  maxOrder, 0, -2.0));
            Assert.assertEquals(1.0 / 4.0, posNeg.getValue(), 1.0e-15);
            Assert.assertEquals(JdkMath.log(2.0) / 4.0, posNeg.getPartialDerivative(1, 0, 0), 1.0e-15);

            // very special case: a = 0 and power = 0
            DerivativeStructure zeroZero = DerivativeStructure.pow(0.0, new DerivativeStructure(3,  maxOrder, 0, 0.0));

            // this should be OK for simple first derivative with one variable only ...
            Assert.assertEquals(1.0, zeroZero.getValue(), 1.0e-15);
            Assert.assertEquals(Double.NEGATIVE_INFINITY, zeroZero.getPartialDerivative(1, 0, 0), 1.0e-15);

            // the following checks show a LIMITATION of the current implementation
            // we have no way to tell x is a pure linear variable x = 0
            // we only say: "x is a structure with value = 0.0,
            // first derivative with respect to x = 1.0, and all other derivatives
            // (first order with respect to y and z and higher derivatives) all 0.0.
            // We have function f(x) = a^x and x = 0 so we compute:
            // f(0) = 1, f'(0) = ln(a), f''(0) = ln(a)^2. The limit of these values
            // when a converges to 0 implies all derivatives keep switching between
            // +infinity and -infinity.
            //
            // Function composition rule for first derivatives is:
            // d[f(g(x,y,z))]/dy = f'(g(x,y,z)) * dg(x,y,z)/dy
            // so given that in our case x represents g and does not depend
            // on y or z, we have dg(x,y,z)/dy = 0
            // applying the composition rules gives:
            // d[f(g(x,y,z))]/dy = f'(g(x,y,z)) * dg(x,y,z)/dy
            //                 = -infinity * 0
            //                 = NaN
            // if we knew x is really the x variable and not the identity
            // function applied to x, we would not have computed f'(g(x,y,z)) * dg(x,y,z)/dy
            // and we would have found that the result was 0 and not NaN
            Assert.assertTrue(Double.isNaN(zeroZero.getPartialDerivative(0, 1, 0)));
            Assert.assertTrue(Double.isNaN(zeroZero.getPartialDerivative(0, 0, 1)));

            // Function composition rule for second derivatives is:
            // d2[f(g(x))]/dx2 = f''(g(x)) * [g'(x)]^2 + f'(g(x)) * g''(x)
            // when function f is the a^x root and x = 0 we have:
            // f(0) = 1, f'(0) = ln(a), f''(0) = ln(a)^2 which for a = 0 implies
            // all derivatives keep switching between +infinity and -infinity
            // so given that in our case x represents g, we have g(x) = 0,
            // g'(x) = 1 and g''(x) = 0
            // applying the composition rules gives:
            // d2[f(g(x))]/dx2 = f''(g(x)) * [g'(x)]^2 + f'(g(x)) * g''(x)
            //                 = +infinity * 1^2 + -infinity * 0
            //                 = +infinity + NaN
            //                 = NaN
            // if we knew x is really the x variable and not the identity
            // function applied to x, we would not have computed f'(g(x)) * g''(x)
            // and we would have found that the result was +infinity and not NaN
            if (maxOrder > 1) {
                Assert.assertTrue(Double.isNaN(zeroZero.getPartialDerivative(2, 0, 0)));
                Assert.assertTrue(Double.isNaN(zeroZero.getPartialDerivative(0, 2, 0)));
                Assert.assertTrue(Double.isNaN(zeroZero.getPartialDerivative(0, 0, 2)));
                Assert.assertTrue(Double.isNaN(zeroZero.getPartialDerivative(1, 1, 0)));
                Assert.assertTrue(Double.isNaN(zeroZero.getPartialDerivative(0, 1, 1)));
                Assert.assertTrue(Double.isNaN(zeroZero.getPartialDerivative(1, 1, 0)));
            }

            // very special case: 0^0 where the power is a primitive
            DerivativeStructure zeroDsZeroDouble = new DerivativeStructure(3,  maxOrder, 0, 0.0).pow(0.0);
            boolean first = true;
            for (final double d : zeroDsZeroDouble.getAllDerivatives()) {
                if (first) {
                    Assert.assertEquals(1.0, d, Precision.EPSILON);
                    first = false;
                } else {
                    Assert.assertEquals(0.0, d, Precision.SAFE_MIN);
                }
            }
            DerivativeStructure zeroDsZeroInt = new DerivativeStructure(3,  maxOrder, 0, 0.0).pow(0);
            first = true;
            for (final double d : zeroDsZeroInt.getAllDerivatives()) {
                if (first) {
                    Assert.assertEquals(1.0, d, Precision.EPSILON);
                    first = false;
                } else {
                    Assert.assertEquals(0.0, d, Precision.SAFE_MIN);
                }
            }

            // 0^p with p smaller than 1.0
            DerivativeStructure u = new DerivativeStructure(3, maxOrder, 1, -0.0).pow(0.25);
            for (int i0 = 0; i0 <= maxOrder; ++i0) {
                for (int i1 = 0; i1 <= maxOrder; ++i1) {
                    for (int i2 = 0; i2 <= maxOrder; ++i2) {
                        if (i0 + i1 + i2 <= maxOrder) {
                            Assert.assertEquals(0.0, u.getPartialDerivative(i0, i1, i2), 1.0e-10);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testExpression() {
        double epsilon = 2.5e-13;
        for (double x = 0; x < 2; x += 0.2) {
            DerivativeStructure dsX = new DerivativeStructure(3, 5, 0, x);
            for (double y = 0; y < 2; y += 0.2) {
                DerivativeStructure dsY = new DerivativeStructure(3, 5, 1, y);
                for (double z = 0; z >- 2; z -= 0.2) {
                    DerivativeStructure dsZ = new DerivativeStructure(3, 5, 2, z);

                    // f(x, y, z) = x + 5 x y - 2 z + (8 z x - y)^3
                    DerivativeStructure ds =
                            new DerivativeStructure(1, dsX,
                                                    5, dsX.multiply(dsY),
                                                    -2, dsZ,
                                                    1, new DerivativeStructure(8, dsZ.multiply(dsX),
                                                                               -1, dsY).pow(3));
                    DerivativeStructure dsOther =
                            new DerivativeStructure(1, dsX,
                                                    5, dsX.multiply(dsY),
                                                    -2, dsZ).add(new DerivativeStructure(8, dsZ.multiply(dsX),
                                                                                         -1, dsY).pow(3));
                    double f = x + 5 * x * y - 2 * z + JdkMath.pow(8 * z * x - y, 3);
                    Assert.assertEquals(f, ds.getValue(),
                                        JdkMath.abs(epsilon * f));
                    Assert.assertEquals(f, dsOther.getValue(),
                                        JdkMath.abs(epsilon * f));

                    // df/dx = 1 + 5 y + 24 (8 z x - y)^2 z
                    double dfdx = 1 + 5 * y + 24 * z * JdkMath.pow(8 * z * x - y, 2);
                    Assert.assertEquals(dfdx, ds.getPartialDerivative(1, 0, 0),
                                        JdkMath.abs(epsilon * dfdx));
                    Assert.assertEquals(dfdx, dsOther.getPartialDerivative(1, 0, 0),
                                        JdkMath.abs(epsilon * dfdx));

                    // df/dxdy = 5 + 48 z*(y - 8 z x)
                    double dfdxdy = 5 + 48 * z * (y - 8 * z * x);
                    Assert.assertEquals(dfdxdy, ds.getPartialDerivative(1, 1, 0),
                                        JdkMath.abs(epsilon * dfdxdy));
                    Assert.assertEquals(dfdxdy, dsOther.getPartialDerivative(1, 1, 0),
                                        JdkMath.abs(epsilon * dfdxdy));

                    // df/dxdydz = 48 (y - 16 z x)
                    double dfdxdydz = 48 * (y - 16 * z * x);
                    Assert.assertEquals(dfdxdydz, ds.getPartialDerivative(1, 1, 1),
                                        JdkMath.abs(epsilon * dfdxdydz));
                    Assert.assertEquals(dfdxdydz, dsOther.getPartialDerivative(1, 1, 1),
                                        JdkMath.abs(epsilon * dfdxdydz));
                }
            }
        }
    }

    @Test
    public void testCompositionOneVariableX() {
        double epsilon = 1.0e-13;
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.1) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                for (double y = 0.1; y < 1.2; y += 0.1) {
                    DerivativeStructure dsY = new DerivativeStructure(1, maxOrder, y);
                    DerivativeStructure f = dsX.divide(dsY).sqrt();
                    double f0 = JdkMath.sqrt(x / y);
                    Assert.assertEquals(f0, f.getValue(), JdkMath.abs(epsilon * f0));
                    if (f.getOrder() > 0) {
                        double f1 = 1 / (2 * JdkMath.sqrt(x * y));
                        Assert.assertEquals(f1, f.getPartialDerivative(1), JdkMath.abs(epsilon * f1));
                        if (f.getOrder() > 1) {
                            double f2 = -f1 / (2 * x);
                            Assert.assertEquals(f2, f.getPartialDerivative(2), JdkMath.abs(epsilon * f2));
                            if (f.getOrder() > 2) {
                                double f3 = (f0 + x / (2 * y * f0)) / (4 * x * x * x);
                                Assert.assertEquals(f3, f.getPartialDerivative(3), JdkMath.abs(epsilon * f3));
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testTrigo() {
        double epsilon = 2.0e-12;
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.1) {
                DerivativeStructure dsX = new DerivativeStructure(3, maxOrder, 0, x);
                for (double y = 0.1; y < 1.2; y += 0.1) {
                    DerivativeStructure dsY = new DerivativeStructure(3, maxOrder, 1, y);
                    for (double z = 0.1; z < 1.2; z += 0.1) {
                        DerivativeStructure dsZ = new DerivativeStructure(3, maxOrder, 2, z);
                        DerivativeStructure f = dsX.divide(dsY.cos().add(dsZ.tan())).sin();
                        double a = JdkMath.cos(y) + JdkMath.tan(z);
                        double f0 = JdkMath.sin(x / a);
                        Assert.assertEquals(f0, f.getValue(), JdkMath.abs(epsilon * f0));
                        if (f.getOrder() > 0) {
                            double dfdx = JdkMath.cos(x / a) / a;
                            Assert.assertEquals(dfdx, f.getPartialDerivative(1, 0, 0), JdkMath.abs(epsilon * dfdx));
                            double dfdy =  x * JdkMath.sin(y) * dfdx / a;
                            Assert.assertEquals(dfdy, f.getPartialDerivative(0, 1, 0), JdkMath.abs(epsilon * dfdy));
                            double cz = JdkMath.cos(z);
                            double cz2 = cz * cz;
                            double dfdz = -x * dfdx / (a * cz2);
                            Assert.assertEquals(dfdz, f.getPartialDerivative(0, 0, 1), JdkMath.abs(epsilon * dfdz));
                            if (f.getOrder() > 1) {
                                double df2dx2 = -(f0 / (a * a));
                                Assert.assertEquals(df2dx2, f.getPartialDerivative(2, 0, 0), JdkMath.abs(epsilon * df2dx2));
                                double df2dy2 = x * JdkMath.cos(y) * dfdx / a -
                                                x * x * JdkMath.sin(y) * JdkMath.sin(y) * f0 / (a * a * a * a) +
                                                2 * JdkMath.sin(y) * dfdy / a;
                                Assert.assertEquals(df2dy2, f.getPartialDerivative(0, 2, 0), JdkMath.abs(epsilon * df2dy2));
                                double c4 = cz2 * cz2;
                                double df2dz2 = x * (2 * a * (1 - a * cz * JdkMath.sin(z)) * dfdx - x * f0 / a ) / (a * a * a * c4);
                                Assert.assertEquals(df2dz2, f.getPartialDerivative(0, 0, 2), JdkMath.abs(epsilon * df2dz2));
                                double df2dxdy = dfdy / x  - x * JdkMath.sin(y) * f0 / (a * a * a);
                                Assert.assertEquals(df2dxdy, f.getPartialDerivative(1, 1, 0), JdkMath.abs(epsilon * df2dxdy));
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testSqrtDefinition() {
        double[] epsilon = new double[] { 5.0e-16, 5.0e-16, 2.0e-15, 5.0e-14, 2.0e-12 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure sqrt1 = dsX.pow(0.5);
                DerivativeStructure sqrt2 = dsX.sqrt();
                DerivativeStructure zero = sqrt1.subtract(sqrt2);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testRootNSingularity() {
        for (int n = 2; n < 10; ++n) {
            for (int maxOrder = 0; maxOrder < 12; ++maxOrder) {
                DerivativeStructure dsZero = new DerivativeStructure(1, maxOrder, 0, 0.0);
                DerivativeStructure rootN  = dsZero.rootN(n);
                Assert.assertEquals(0.0, rootN.getValue(), 1.0e-20);
                if (maxOrder > 0) {
                    Assert.assertTrue(Double.isInfinite(rootN.getPartialDerivative(1)));
                    Assert.assertTrue(rootN.getPartialDerivative(1) > 0);
                    for (int order = 2; order <= maxOrder; ++order) {
                        // the following checks shows a LIMITATION of the current implementation
                        // we have no way to tell dsZero is a pure linear variable x = 0
                        // we only say: "dsZero is a structure with value = 0.0,
                        // first derivative = 1.0, second and higher derivatives = 0.0".
                        // Function composition rule for second derivatives is:
                        // d2[f(g(x))]/dx2 = f''(g(x)) * [g'(x)]^2 + f'(g(x)) * g''(x)
                        // when function f is the nth root and x = 0 we have:
                        // f(0) = 0, f'(0) = +infinity, f''(0) = -infinity (and higher
                        // derivatives keep switching between +infinity and -infinity)
                        // so given that in our case dsZero represents g, we have g(x) = 0,
                        // g'(x) = 1 and g''(x) = 0
                        // applying the composition rules gives:
                        // d2[f(g(x))]/dx2 = f''(g(x)) * [g'(x)]^2 + f'(g(x)) * g''(x)
                        //                 = -infinity * 1^2 + +infinity * 0
                        //                 = -infinity + NaN
                        //                 = NaN
                        // if we knew dsZero is really the x variable and not the identity
                        // function applied to x, we would not have computed f'(g(x)) * g''(x)
                        // and we would have found that the result was -infinity and not NaN
                        Assert.assertTrue(Double.isNaN(rootN.getPartialDerivative(order)));
                    }
                }

                // the following shows that the limitation explained above is NOT a bug...
                // if we set up the higher order derivatives for g appropriately, we do
                // compute the higher order derivatives of the composition correctly
                double[] gDerivatives = new double[ 1 + maxOrder];
                gDerivatives[0] = 0.0;
                for (int k = 1; k <= maxOrder; ++k) {
                    gDerivatives[k] = JdkMath.pow(-1.0, k + 1);
                }
                DerivativeStructure correctRoot = new DerivativeStructure(1, maxOrder, gDerivatives).rootN(n);
                Assert.assertEquals(0.0, correctRoot.getValue(), 1.0e-20);
                if (maxOrder > 0) {
                    Assert.assertTrue(Double.isInfinite(correctRoot.getPartialDerivative(1)));
                    Assert.assertTrue(correctRoot.getPartialDerivative(1) > 0);
                    for (int order = 2; order <= maxOrder; ++order) {
                        Assert.assertTrue(Double.isInfinite(correctRoot.getPartialDerivative(order)));
                        if ((order & 1) == 0) {
                            Assert.assertTrue(correctRoot.getPartialDerivative(order) < 0);
                        } else {
                            Assert.assertTrue(correctRoot.getPartialDerivative(order) > 0);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testSqrtPow2() {
        double[] epsilon = new double[] { 1.0e-16, 3.0e-16, 2.0e-15, 6.0e-14, 6.0e-12 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.multiply(dsX).sqrt();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testCbrtDefinition() {
        double[] epsilon = new double[] { 4.0e-16, 9.0e-16, 6.0e-15, 2.0e-13, 4.0e-12 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure cbrt1 = dsX.pow(1.0 / 3.0);
                DerivativeStructure cbrt2 = dsX.cbrt();
                DerivativeStructure zero = cbrt1.subtract(cbrt2);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testCbrtPow3() {
        double[] epsilon = new double[] { 1.0e-16, 5.0e-16, 8.0e-15, 3.0e-13, 4.0e-11 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.multiply(dsX.multiply(dsX)).cbrt();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testPowReciprocalPow() {
        double[] epsilon = new double[] { 2.0e-15, 2.0e-14, 3.0e-13, 8.0e-12, 3.0e-10 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.01) {
                DerivativeStructure dsX = new DerivativeStructure(2, maxOrder, 0, x);
                for (double y = 0.1; y < 1.2; y += 0.01) {
                    DerivativeStructure dsY = new DerivativeStructure(2, maxOrder, 1, y);
                    DerivativeStructure rebuiltX = dsX.pow(dsY).pow(dsY.reciprocal());
                    DerivativeStructure zero = rebuiltX.subtract(dsX);
                    for (int n = 0; n <= maxOrder; ++n) {
                        for (int m = 0; m <= maxOrder; ++m) {
                            if (n + m <= maxOrder) {
                                Assert.assertEquals(0.0, zero.getPartialDerivative(n, m), epsilon[n + m]);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testHypotDefinition() {
        double epsilon = 1.0e-20;
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = -1.7; x < 2; x += 0.2) {
                DerivativeStructure dsX = new DerivativeStructure(2, maxOrder, 0, x);
                for (double y = -1.7; y < 2; y += 0.2) {
                    DerivativeStructure dsY = new DerivativeStructure(2, maxOrder, 1, y);
                    DerivativeStructure hypot = DerivativeStructure.hypot(dsY, dsX);
                    DerivativeStructure ref = dsX.multiply(dsX).add(dsY.multiply(dsY)).sqrt();
                    DerivativeStructure zero = hypot.subtract(ref);
                    for (int n = 0; n <= maxOrder; ++n) {
                        for (int m = 0; m <= maxOrder; ++m) {
                            if (n + m <= maxOrder) {
                                Assert.assertEquals(0, zero.getPartialDerivative(n, m), epsilon);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testHypotNoOverflow() {

        DerivativeStructure dsX = new DerivativeStructure(2, 5, 0, +3.0e250);
        DerivativeStructure dsY = new DerivativeStructure(2, 5, 1, -4.0e250);
        DerivativeStructure hypot = DerivativeStructure.hypot(dsX, dsY);
        Assert.assertEquals(5.0e250, hypot.getValue(), 1.0e235);
        Assert.assertEquals(dsX.getValue() / hypot.getValue(), hypot.getPartialDerivative(1, 0), 1.0e-10);
        Assert.assertEquals(dsY.getValue() / hypot.getValue(), hypot.getPartialDerivative(0, 1), 1.0e-10);

        DerivativeStructure sqrt  = dsX.multiply(dsX).add(dsY.multiply(dsY)).sqrt();
        Assert.assertTrue(Double.isInfinite(sqrt.getValue()));
    }

    @Test
    public void testHypotNeglectible() {

        DerivativeStructure dsSmall = new DerivativeStructure(2, 5, 0, +3.0e-10);
        DerivativeStructure dsLarge = new DerivativeStructure(2, 5, 1, -4.0e25);

        Assert.assertEquals(dsLarge.abs().getValue(),
                            DerivativeStructure.hypot(dsSmall, dsLarge).getValue(),
                            1.0e-10);
        Assert.assertEquals(0,
                            DerivativeStructure.hypot(dsSmall, dsLarge).getPartialDerivative(1, 0),
                            1.0e-10);
        Assert.assertEquals(-1,
                            DerivativeStructure.hypot(dsSmall, dsLarge).getPartialDerivative(0, 1),
                            1.0e-10);

        Assert.assertEquals(dsLarge.abs().getValue(),
                            DerivativeStructure.hypot(dsLarge, dsSmall).getValue(),
                            1.0e-10);
        Assert.assertEquals(0,
                            DerivativeStructure.hypot(dsLarge, dsSmall).getPartialDerivative(1, 0),
                            1.0e-10);
        Assert.assertEquals(-1,
                            DerivativeStructure.hypot(dsLarge, dsSmall).getPartialDerivative(0, 1),
                            1.0e-10);
    }

    @Test
    public void testHypotSpecial() {
        Assert.assertTrue(Double.isNaN(DerivativeStructure.hypot(new DerivativeStructure(2, 5, 0, Double.NaN),
                                                                 new DerivativeStructure(2, 5, 0, +3.0e250)).getValue()));
        Assert.assertTrue(Double.isNaN(DerivativeStructure.hypot(new DerivativeStructure(2, 5, 0, +3.0e250),
                                                                 new DerivativeStructure(2, 5, 0, Double.NaN)).getValue()));
        Assert.assertTrue(Double.isInfinite(DerivativeStructure.hypot(new DerivativeStructure(2, 5, 0, Double.POSITIVE_INFINITY),
                                                                      new DerivativeStructure(2, 5, 0, +3.0e250)).getValue()));
        Assert.assertTrue(Double.isInfinite(DerivativeStructure.hypot(new DerivativeStructure(2, 5, 0, +3.0e250),
                                                                      new DerivativeStructure(2, 5, 0, Double.POSITIVE_INFINITY)).getValue()));
    }

    @Test
    public void testPrimitiveRemainder() {
        double epsilon = 1.0e-15;
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = -1.7; x < 2; x += 0.2) {
                DerivativeStructure dsX = new DerivativeStructure(2, maxOrder, 0, x);
                for (double y = -1.7; y < 2; y += 0.2) {
                    DerivativeStructure remainder = dsX.remainder(y);
                    DerivativeStructure ref = dsX.subtract(x - JdkMath.IEEEremainder(x, y));
                    DerivativeStructure zero = remainder.subtract(ref);
                    for (int n = 0; n <= maxOrder; ++n) {
                        for (int m = 0; m <= maxOrder; ++m) {
                            if (n + m <= maxOrder) {
                                Assert.assertEquals(0, zero.getPartialDerivative(n, m), epsilon);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testRemainder() {
        double epsilon = 2.0e-15;
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = -1.7; x < 2; x += 0.2) {
                DerivativeStructure dsX = new DerivativeStructure(2, maxOrder, 0, x);
                for (double y = -1.7; y < 2; y += 0.2) {
                    DerivativeStructure dsY = new DerivativeStructure(2, maxOrder, 1, y);
                    DerivativeStructure remainder = dsX.remainder(dsY);
                    DerivativeStructure ref = dsX.subtract(dsY.multiply((x - JdkMath.IEEEremainder(x, y)) / y));
                    DerivativeStructure zero = remainder.subtract(ref);
                    for (int n = 0; n <= maxOrder; ++n) {
                        for (int m = 0; m <= maxOrder; ++m) {
                            if (n + m <= maxOrder) {
                                Assert.assertEquals(0, zero.getPartialDerivative(n, m), epsilon);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @Test
    public void testExp() {
        double[] epsilon = new double[] { 1.0e-16, 1.0e-16, 1.0e-16, 1.0e-16, 1.0e-16 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                double refExp = JdkMath.exp(x);
                DerivativeStructure exp = new DerivativeStructure(1, maxOrder, 0, x).exp();
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(refExp, exp.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testExpm1Definition() {
        double epsilon = 3.0e-16;
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure expm11 = dsX.expm1();
                DerivativeStructure expm12 = dsX.exp().subtract(dsX.getField().getOne());
                DerivativeStructure zero = expm11.subtract(expm12);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0, zero.getPartialDerivative(n), epsilon);
                }
            }
        }
    }

    @Override
    @Test
    public void testLog() {
        double[] epsilon = new double[] { 1.0e-16, 1.0e-16, 3.0e-14, 7.0e-13, 3.0e-11 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure log = new DerivativeStructure(1, maxOrder, 0, x).log();
                Assert.assertEquals(JdkMath.log(x), log.getValue(), epsilon[0]);
                for (int n = 1; n <= maxOrder; ++n) {
                    double refDer = -Factorial.value(n - 1) / JdkMath.pow(-x, n);
                    Assert.assertEquals(refDer, log.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testLog1pDefinition() {
        double epsilon = 3.0e-16;
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure log1p1 = dsX.log1p();
                DerivativeStructure log1p2 = dsX.add(dsX.getField().getOne()).log();
                DerivativeStructure zero = log1p1.subtract(log1p2);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0, zero.getPartialDerivative(n), epsilon);
                }
            }
        }
    }

    @Test
    public void testLog10Definition() {
        double[] epsilon = new double[] { 3.0e-16, 3.0e-16, 8.0e-15, 3.0e-13, 8.0e-12 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure log101 = dsX.log10();
                DerivativeStructure log102 = dsX.log().divide(JdkMath.log(10.0));
                DerivativeStructure zero = log101.subtract(log102);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testLogExp() {
        double[] epsilon = new double[] { 2.0e-16, 2.0e-16, 3.0e-16, 2.0e-15, 6.0e-15 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.exp().log();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testLog1pExpm1() {
        double[] epsilon = new double[] { 6.0e-17, 3.0e-16, 5.0e-16, 9.0e-16, 6.0e-15 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.expm1().log1p();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testLog10Power() {
        double[] epsilon = new double[] { 3.0e-16, 3.0e-16, 9.0e-16, 6.0e-15, 6.0e-14 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = new DerivativeStructure(1, maxOrder, 10.0).pow(dsX).log10();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testSinCos() {
        double epsilon = 5.0e-16;
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure sin = dsX.sin();
                DerivativeStructure cos = dsX.cos();
                double s = JdkMath.sin(x);
                double c = JdkMath.cos(x);
                for (int n = 0; n <= maxOrder; ++n) {
                    switch (n % 4) {
                    case 0 :
                        Assert.assertEquals( s, sin.getPartialDerivative(n), epsilon);
                        Assert.assertEquals( c, cos.getPartialDerivative(n), epsilon);
                        break;
                    case 1 :
                        Assert.assertEquals( c, sin.getPartialDerivative(n), epsilon);
                        Assert.assertEquals(-s, cos.getPartialDerivative(n), epsilon);
                        break;
                    case 2 :
                        Assert.assertEquals(-s, sin.getPartialDerivative(n), epsilon);
                        Assert.assertEquals(-c, cos.getPartialDerivative(n), epsilon);
                        break;
                    default :
                        Assert.assertEquals(-c, sin.getPartialDerivative(n), epsilon);
                        Assert.assertEquals( s, cos.getPartialDerivative(n), epsilon);
                        break;
                    }
                }
            }
        }
    }

    @Test
    public void testSinAsin() {
        double[] epsilon = new double[] { 3.0e-16, 5.0e-16, 3.0e-15, 2.0e-14, 4.0e-13 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.sin().asin();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testCosAcos() {
        double[] epsilon = new double[] { 6.0e-16, 6.0e-15, 2.0e-13, 4.0e-12, 2.0e-10 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.cos().acos();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testTanAtan() {
        double[] epsilon = new double[] { 6.0e-17, 2.0e-16, 2.0e-15, 4.0e-14, 2.0e-12 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.tan().atan();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testTangentDefinition() {
        double[] epsilon = new double[] { 5.0e-16, 2.0e-15, 3.0e-14, 5.0e-13, 2.0e-11 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure tan1 = dsX.sin().divide(dsX.cos());
                DerivativeStructure tan2 = dsX.tan();
                DerivativeStructure zero = tan1.subtract(tan2);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Override
    @Test
    public void testAtan2() {
        double[] epsilon = new double[] { 5.0e-16, 3.0e-15, 2.2e-14, 1.0e-12, 8.0e-11 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = -1.7; x < 2; x += 0.2) {
                DerivativeStructure dsX = new DerivativeStructure(2, maxOrder, 0, x);
                for (double y = -1.7; y < 2; y += 0.2) {
                    DerivativeStructure dsY = new DerivativeStructure(2, maxOrder, 1, y);
                    DerivativeStructure atan2 = DerivativeStructure.atan2(dsY, dsX);
                    DerivativeStructure ref = dsY.divide(dsX).atan();
                    if (x < 0) {
                        ref = (y < 0) ? ref.subtract(JdkMath.PI) : ref.add(JdkMath.PI);
                    }
                    DerivativeStructure zero = atan2.subtract(ref);
                    for (int n = 0; n <= maxOrder; ++n) {
                        for (int m = 0; m <= maxOrder; ++m) {
                            if (n + m <= maxOrder) {
                                Assert.assertEquals(0, zero.getPartialDerivative(n, m), epsilon[n + m]);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testAtan2SpecialCases() {

        DerivativeStructure pp =
                DerivativeStructure.atan2(new DerivativeStructure(2, 2, 1, +0.0),
                                          new DerivativeStructure(2, 2, 1, +0.0));
        Assert.assertEquals(0, pp.getValue(), 1.0e-15);
        Assert.assertEquals(+1, JdkMath.copySign(1, pp.getValue()), 1.0e-15);

        DerivativeStructure pn =
                DerivativeStructure.atan2(new DerivativeStructure(2, 2, 1, +0.0),
                                          new DerivativeStructure(2, 2, 1, -0.0));
        Assert.assertEquals(JdkMath.PI, pn.getValue(), 1.0e-15);

        DerivativeStructure np =
                DerivativeStructure.atan2(new DerivativeStructure(2, 2, 1, -0.0),
                                          new DerivativeStructure(2, 2, 1, +0.0));
        Assert.assertEquals(0, np.getValue(), 1.0e-15);
        Assert.assertEquals(-1, JdkMath.copySign(1, np.getValue()), 1.0e-15);

        DerivativeStructure nn =
                DerivativeStructure.atan2(new DerivativeStructure(2, 2, 1, -0.0),
                                          new DerivativeStructure(2, 2, 1, -0.0));
        Assert.assertEquals(-JdkMath.PI, nn.getValue(), 1.0e-15);
    }

    @Test
    public void testSinhDefinition() {
        double[] epsilon = new double[] { 3.0e-16, 3.0e-16, 5.0e-16, 2.0e-15, 6.0e-15 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure sinh1 = dsX.exp().subtract(dsX.exp().reciprocal()).multiply(0.5);
                DerivativeStructure sinh2 = dsX.sinh();
                DerivativeStructure zero = sinh1.subtract(sinh2);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testCoshDefinition() {
        double[] epsilon = new double[] { 3.0e-16, 3.0e-16, 5.0e-16, 2.0e-15, 6.0e-15 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure cosh1 = dsX.exp().add(dsX.exp().reciprocal()).multiply(0.5);
                DerivativeStructure cosh2 = dsX.cosh();
                DerivativeStructure zero = cosh1.subtract(cosh2);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testTanhDefinition() {
        double[] epsilon = new double[] { 3.0e-16, 5.0e-16, 7.0e-16, 3.0e-15, 2.0e-14 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure tanh1 = dsX.exp().subtract(dsX.exp().reciprocal()).divide(dsX.exp().add(dsX.exp().reciprocal()));
                DerivativeStructure tanh2 = dsX.tanh();
                DerivativeStructure zero = tanh1.subtract(tanh2);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testSinhAsinh() {
        double[] epsilon = new double[] { 3.0e-16, 3.0e-16, 4.0e-16, 7.0e-16, 3.0e-15, 8.0e-15 };
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.sinh().asinh();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testCoshAcosh() {
        double[] epsilon = new double[] { 2.0e-15, 1.0e-14, 2.0e-13, 6.0e-12, 3.0e-10, 2.0e-8 };
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.cosh().acosh();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testTanhAtanh() {
        double[] epsilon = new double[] { 3.0e-16, 2.0e-16, 7.0e-16, 4.0e-15, 3.0e-14, 4.0e-13 };
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.tanh().atanh();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testCompositionOneVariableY() {
        double epsilon = 1.0e-13;
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.1) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, x);
                for (double y = 0.1; y < 1.2; y += 0.1) {
                    DerivativeStructure dsY = new DerivativeStructure(1, maxOrder, 0, y);
                    DerivativeStructure f = dsX.divide(dsY).sqrt();
                    double f0 = JdkMath.sqrt(x / y);
                    Assert.assertEquals(f0, f.getValue(), JdkMath.abs(epsilon * f0));
                    if (f.getOrder() > 0) {
                        double f1 = -x / (2 * y * y * f0);
                        Assert.assertEquals(f1, f.getPartialDerivative(1), JdkMath.abs(epsilon * f1));
                        if (f.getOrder() > 1) {
                            double f2 = (f0 - x / (4 * y * f0)) / (y * y);
                            Assert.assertEquals(f2, f.getPartialDerivative(2), JdkMath.abs(epsilon * f2));
                            if (f.getOrder() > 2) {
                                double f3 = (x / (8 * y * f0) - 2 * f0) / (y * y * y);
                                Assert.assertEquals(f3, f.getPartialDerivative(3), JdkMath.abs(epsilon * f3));
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testTaylorPolynomial() {
        for (double x = 0; x < 1.2; x += 0.1) {
            DerivativeStructure dsX = new DerivativeStructure(3, 4, 0, x);
            for (double y = 0; y < 1.2; y += 0.2) {
                DerivativeStructure dsY = new DerivativeStructure(3, 4, 1, y);
                for (double z = 0; z < 1.2; z += 0.2) {
                    DerivativeStructure dsZ = new DerivativeStructure(3, 4, 2, z);
                    DerivativeStructure f = dsX.multiply(dsY).add(dsZ).multiply(dsX).multiply(dsY);
                    for (double dx = -0.2; dx < 0.2; dx += 0.2) {
                        for (double dy = -0.2; dy < 0.2; dy += 0.1) {
                            for (double dz = -0.2; dz < 0.2; dz += 0.1) {
                                double ref = (x + dx) * (y + dy) * ((x + dx) * (y + dy) + (z + dz));
                                Assert.assertEquals(ref, f.taylor(dx, dy, dz), 2.0e-15);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testTaylorAtan2() {
        double[] expected = new double[] { 0.214, 0.0241, 0.00422, 6.48e-4, 8.04e-5 };
        double x0 =  0.1;
        double y0 = -0.3;
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            DerivativeStructure dsX   = new DerivativeStructure(2, maxOrder, 0, x0);
            DerivativeStructure dsY   = new DerivativeStructure(2, maxOrder, 1, y0);
            DerivativeStructure atan2 = DerivativeStructure.atan2(dsY, dsX);
            double maxError = 0;
            for (double dx = -0.05; dx < 0.05; dx += 0.001) {
                for (double dy = -0.05; dy < 0.05; dy += 0.001) {
                    double ref = JdkMath.atan2(y0 + dy, x0 + dx);
                    maxError = JdkMath.max(maxError, JdkMath.abs(ref - atan2.taylor(dx, dy)));
                }
            }
            Assert.assertEquals(0.0, expected[maxOrder] - maxError, 0.01 * expected[maxOrder]);
        }
    }

    @Override
    @Test
    public void testAbs() {

        DerivativeStructure minusOne = new DerivativeStructure(1, 1, 0, -1.0);
        Assert.assertEquals(+1.0, minusOne.abs().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.abs().getPartialDerivative(1), 1.0e-15);

        DerivativeStructure plusOne = new DerivativeStructure(1, 1, 0, +1.0);
        Assert.assertEquals(+1.0, plusOne.abs().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.abs().getPartialDerivative(1), 1.0e-15);

        DerivativeStructure minusZero = new DerivativeStructure(1, 1, 0, -0.0);
        Assert.assertEquals(+0.0, minusZero.abs().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, minusZero.abs().getPartialDerivative(1), 1.0e-15);

        DerivativeStructure plusZero = new DerivativeStructure(1, 1, 0, +0.0);
        Assert.assertEquals(+0.0, plusZero.abs().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, plusZero.abs().getPartialDerivative(1), 1.0e-15);
    }

    @Override
    @Test
    public void testSignum() {

        DerivativeStructure minusOne = new DerivativeStructure(1, 1, 0, -1.0);
        Assert.assertEquals(-1.0, minusOne.signum().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals( 0.0, minusOne.signum().getPartialDerivative(1), 1.0e-15);

        DerivativeStructure plusOne = new DerivativeStructure(1, 1, 0, +1.0);
        Assert.assertEquals(+1.0, plusOne.signum().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals( 0.0, plusOne.signum().getPartialDerivative(1), 1.0e-15);

        DerivativeStructure minusZero = new DerivativeStructure(1, 1, 0, -0.0);
        Assert.assertEquals(-0.0, minusZero.signum().getPartialDerivative(0), 1.0e-15);
        Assert.assertTrue(Double.doubleToLongBits(minusZero.signum().getValue()) < 0);
        Assert.assertEquals( 0.0, minusZero.signum().getPartialDerivative(1), 1.0e-15);

        DerivativeStructure plusZero = new DerivativeStructure(1, 1, 0, +0.0);
        Assert.assertEquals(+0.0, plusZero.signum().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(0, Double.doubleToLongBits(plusZero.signum().getValue()));
        Assert.assertEquals( 0.0, plusZero.signum().getPartialDerivative(1), 1.0e-15);
    }

    @Test
    public void testCeilFloorRintLong() {

        DerivativeStructure x = new DerivativeStructure(1, 1, 0, -1.5);
        Assert.assertEquals(-1.5, x.getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, x.getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(-1.0, x.ceil().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+0.0, x.ceil().getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(-2.0, x.floor().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+0.0, x.floor().getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(-2.0, x.rint().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+0.0, x.rint().getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(-2.0, x.subtract(x.getField().getOne()).rint().getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(-1L, x.round());
    }

    @Test
    public void testCopySign() {

        DerivativeStructure minusOne = new DerivativeStructure(1, 1, 0, -1.0);
        Assert.assertEquals(+1.0, minusOne.copySign(+1.0).getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.copySign(+1.0).getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.copySign(-1.0).getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, minusOne.copySign(-1.0).getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(+1.0, minusOne.copySign(+0.0).getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.copySign(+0.0).getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.copySign(-0.0).getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, minusOne.copySign(-0.0).getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(+1.0, minusOne.copySign(Double.NaN).getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.copySign(Double.NaN).getPartialDerivative(1), 1.0e-15);

        DerivativeStructure plusOne = new DerivativeStructure(1, 1, 0, +1.0);
        Assert.assertEquals(+1.0, plusOne.copySign(+1.0).getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.copySign(+1.0).getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(-1.0, plusOne.copySign(-1.0).getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, plusOne.copySign(-1.0).getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.copySign(+0.0).getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.copySign(+0.0).getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(-1.0, plusOne.copySign(-0.0).getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, plusOne.copySign(-0.0).getPartialDerivative(1), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.copySign(Double.NaN).getPartialDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.copySign(Double.NaN).getPartialDerivative(1), 1.0e-15);
    }

    @Test
    public void testToDegreesDefinition() {
        double epsilon = 3.0e-16;
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                Assert.assertEquals(JdkMath.toDegrees(x), dsX.toDegrees().getValue(), epsilon);
                for (int n = 1; n <= maxOrder; ++n) {
                    if (n == 1) {
                        Assert.assertEquals(180 / JdkMath.PI, dsX.toDegrees().getPartialDerivative(1), epsilon);
                    } else {
                        Assert.assertEquals(0.0, dsX.toDegrees().getPartialDerivative(n), epsilon);
                    }
                }
            }
        }
    }

    @Test
    public void testToRadiansDefinition() {
        double epsilon = 3.0e-16;
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                Assert.assertEquals(JdkMath.toRadians(x), dsX.toRadians().getValue(), epsilon);
                for (int n = 1; n <= maxOrder; ++n) {
                    if (n == 1) {
                        Assert.assertEquals(JdkMath.PI / 180, dsX.toRadians().getPartialDerivative(1), epsilon);
                    } else {
                        Assert.assertEquals(0.0, dsX.toRadians().getPartialDerivative(n), epsilon);
                    }
                }
            }
        }
    }

    @Test
    public void testDegRad() {
        double epsilon = 3.0e-16;
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure rebuiltX = dsX.toDegrees().toRadians();
                DerivativeStructure zero = rebuiltX.subtract(dsX);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon);
                }
            }
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testComposeMismatchedDimensions() {
        new DerivativeStructure(1, 3, 0, 1.2).compose(new double[3]);
    }

    @Test
    public void testCompose() {
        double[] epsilon = new double[] { 1.0e-20, 5.0e-14, 2.0e-13, 3.0e-13, 2.0e-13, 1.0e-20 };
        PolynomialFunction poly =
                new PolynomialFunction(new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 });
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            PolynomialFunction[] p = new PolynomialFunction[maxOrder + 1];
            p[0] = poly;
            for (int i = 1; i <= maxOrder; ++i) {
                p[i] = p[i - 1].polynomialDerivative();
            }
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure dsY1 = dsX.getField().getZero();
                for (int i = poly.degree(); i >= 0; --i) {
                    dsY1 = dsY1.multiply(dsX).add(poly.getCoefficients()[i]);
                }
                double[] f = new double[maxOrder + 1];
                for (int i = 0; i < f.length; ++i) {
                    f[i] = p[i].value(x);
                }
                DerivativeStructure dsY2 = dsX.compose(f);
                DerivativeStructure zero = dsY1.subtract(dsY2);
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(0.0, zero.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testField() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {
            DerivativeStructure x = new DerivativeStructure(3, maxOrder, 0, 1.0);
            checkF0F1(x.getField().getZero(), 0.0, 0.0, 0.0, 0.0);
            checkF0F1(x.getField().getOne(), 1.0, 0.0, 0.0, 0.0);
            Assert.assertEquals(maxOrder, x.getField().getZero().getOrder());
            Assert.assertEquals(3, x.getField().getZero().getFreeParameters());
            Assert.assertEquals(DerivativeStructure.class, x.getField().getRuntimeClass());
        }
    }

    @Test
    public void testOneParameterConstructor() {
        double x = 1.2;
        double cos = JdkMath.cos(x);
        double sin = JdkMath.sin(x);
        DerivativeStructure yRef = new DerivativeStructure(1, 4, 0, x).cos();
        try {
            new DerivativeStructure(1, 4, 0.0, 0.0);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException dme) {
            // expected
        } catch (Exception e) {
            Assert.fail("wrong exceptionc caught " + e.getClass().getName());
        }
        double[] derivatives = new double[] { cos, -sin, -cos, sin, cos };
        DerivativeStructure y = new DerivativeStructure(1,  4, derivatives);
        checkEquals(yRef, y, 1.0e-15);
        TestUtils.assertEquals(derivatives, y.getAllDerivatives(), 1.0e-15);
    }

    @Test
    public void testOneOrderConstructor() {
        double x =  1.2;
        double y =  2.4;
        double z = 12.5;
        DerivativeStructure xRef = new DerivativeStructure(3, 1, 0, x);
        DerivativeStructure yRef = new DerivativeStructure(3, 1, 1, y);
        DerivativeStructure zRef = new DerivativeStructure(3, 1, 2, z);
        try {
            new DerivativeStructure(3, 1, x + y - z, 1.0, 1.0);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException dme) {
            // expected
        } catch (Exception e) {
            Assert.fail("wrong exceptionc caught " + e.getClass().getName());
        }
        double[] derivatives = new double[] { x + y - z, 1.0, 1.0, -1.0 };
        DerivativeStructure t = new DerivativeStructure(3, 1, derivatives);
        checkEquals(xRef.add(yRef.subtract(zRef)), t, 1.0e-15);
        TestUtils.assertEquals(derivatives, xRef.add(yRef.subtract(zRef)).getAllDerivatives(), 1.0e-15);
    }

    @Test
    public void testLinearCombination1DSDS() {
        final DerivativeStructure[] a = new DerivativeStructure[] {
            new DerivativeStructure(6, 1, 0, -1321008684645961.0 / 268435456.0),
            new DerivativeStructure(6, 1, 1, -5774608829631843.0 / 268435456.0),
            new DerivativeStructure(6, 1, 2, -7645843051051357.0 / 8589934592.0)
        };
        final DerivativeStructure[] b = new DerivativeStructure[] {
            new DerivativeStructure(6, 1, 3, -5712344449280879.0 / 2097152.0),
            new DerivativeStructure(6, 1, 4, -4550117129121957.0 / 2097152.0),
            new DerivativeStructure(6, 1, 5, 8846951984510141.0 / 131072.0)
        };

        final DerivativeStructure abSumInline = a[0].linearCombination(a[0], b[0], a[1], b[1], a[2], b[2]);
        final DerivativeStructure abSumArray = a[0].linearCombination(a, b);

        Assert.assertEquals(abSumInline.getValue(), abSumArray.getValue(), 0);
        Assert.assertEquals(-1.8551294182586248737720779899, abSumInline.getValue(), 1.0e-15);
        Assert.assertEquals(b[0].getValue(), abSumInline.getPartialDerivative(1, 0, 0, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(b[1].getValue(), abSumInline.getPartialDerivative(0, 1, 0, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(b[2].getValue(), abSumInline.getPartialDerivative(0, 0, 1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(a[0].getValue(), abSumInline.getPartialDerivative(0, 0, 0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(a[1].getValue(), abSumInline.getPartialDerivative(0, 0, 0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(a[2].getValue(), abSumInline.getPartialDerivative(0, 0, 0, 0, 0, 1), 1.0e-15);
    }

    @Test
    public void testLinearCombination1DoubleDS() {
        final double[] a = new double[] {
            -1321008684645961.0 / 268435456.0,
            -5774608829631843.0 / 268435456.0,
            -7645843051051357.0 / 8589934592.0
        };
        final DerivativeStructure[] b = new DerivativeStructure[] {
            new DerivativeStructure(3, 1, 0, -5712344449280879.0 / 2097152.0),
            new DerivativeStructure(3, 1, 1, -4550117129121957.0 / 2097152.0),
            new DerivativeStructure(3, 1, 2, 8846951984510141.0 / 131072.0)
        };

        final DerivativeStructure abSumInline = b[0].linearCombination(a[0], b[0],
                                                                       a[1], b[1],
                                                                       a[2], b[2]);
        final DerivativeStructure abSumArray = b[0].linearCombination(a, b);

        Assert.assertEquals(abSumInline.getValue(), abSumArray.getValue(), 0);
        Assert.assertEquals(-1.8551294182586248737720779899, abSumInline.getValue(), 1.0e-15);
        Assert.assertEquals(a[0], abSumInline.getPartialDerivative(1, 0, 0), 1.0e-15);
        Assert.assertEquals(a[1], abSumInline.getPartialDerivative(0, 1, 0), 1.0e-15);
        Assert.assertEquals(a[2], abSumInline.getPartialDerivative(0, 0, 1), 1.0e-15);
    }

    @Test
    public void testLinearCombination2DSDS() {
        // we compare accurate versus naive dot product implementations
        // on regular vectors (i.e. not extreme cases like in the previous test)
        UniformRandomProvider random = RandomSource.WELL_1024_A.create(0xc6af886975069f11L);

        for (int i = 0; i < 10000; ++i) {
            final DerivativeStructure[] u = new DerivativeStructure[4];
            final DerivativeStructure[] v = new DerivativeStructure[4];
            for (int j = 0; j < u.length; ++j) {
                u[j] = new DerivativeStructure(u.length, 1, j, 1e17 * random.nextDouble());
                v[j] = new DerivativeStructure(u.length, 1, 1e17 * random.nextDouble());
            }

            DerivativeStructure lin = u[0].linearCombination(u[0], v[0], u[1], v[1]);
            double ref = u[0].getValue() * v[0].getValue() +
                         u[1].getValue() * v[1].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(v[0].getValue(), lin.getPartialDerivative(1, 0, 0, 0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(v[1].getValue(), lin.getPartialDerivative(0, 1, 0, 0), 1.0e-15 * JdkMath.abs(v[1].getValue()));

            lin = u[0].linearCombination(u[0], v[0], u[1], v[1], u[2], v[2]);
            ref = u[0].getValue() * v[0].getValue() +
                  u[1].getValue() * v[1].getValue() +
                  u[2].getValue() * v[2].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(v[0].getValue(), lin.getPartialDerivative(1, 0, 0, 0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(v[1].getValue(), lin.getPartialDerivative(0, 1, 0, 0), 1.0e-15 * JdkMath.abs(v[1].getValue()));
            Assert.assertEquals(v[2].getValue(), lin.getPartialDerivative(0, 0, 1, 0), 1.0e-15 * JdkMath.abs(v[2].getValue()));

            lin = u[0].linearCombination(u[0], v[0], u[1], v[1], u[2], v[2], u[3], v[3]);
            ref = u[0].getValue() * v[0].getValue() +
                  u[1].getValue() * v[1].getValue() +
                  u[2].getValue() * v[2].getValue() +
                  u[3].getValue() * v[3].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(v[0].getValue(), lin.getPartialDerivative(1, 0, 0, 0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(v[1].getValue(), lin.getPartialDerivative(0, 1, 0, 0), 1.0e-15 * JdkMath.abs(v[1].getValue()));
            Assert.assertEquals(v[2].getValue(), lin.getPartialDerivative(0, 0, 1, 0), 1.0e-15 * JdkMath.abs(v[2].getValue()));
            Assert.assertEquals(v[3].getValue(), lin.getPartialDerivative(0, 0, 0, 1), 1.0e-15 * JdkMath.abs(v[3].getValue()));
        }
    }

    @Test
    public void testLinearCombination2DoubleDS() {
        // we compare accurate versus naive dot product implementations
        // on regular vectors (i.e. not extreme cases like in the previous test)
        UniformRandomProvider random = RandomSource.WELL_1024_A.create(0xc6af886975069f11L);

        for (int i = 0; i < 10000; ++i) {
            final double[] u = new double[4];
            final DerivativeStructure[] v = new DerivativeStructure[4];
            for (int j = 0; j < u.length; ++j) {
                u[j] = 1e17 * random.nextDouble();
                v[j] = new DerivativeStructure(u.length, 1, j, 1e17 * random.nextDouble());
            }

            DerivativeStructure lin = v[0].linearCombination(u[0], v[0], u[1], v[1]);
            double ref = u[0] * v[0].getValue() +
                         u[1] * v[1].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(u[0], lin.getPartialDerivative(1, 0, 0, 0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(u[1], lin.getPartialDerivative(0, 1, 0, 0), 1.0e-15 * JdkMath.abs(v[1].getValue()));

            lin = v[0].linearCombination(u[0], v[0], u[1], v[1], u[2], v[2]);
            ref = u[0] * v[0].getValue() +
                  u[1] * v[1].getValue() +
                  u[2] * v[2].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(u[0], lin.getPartialDerivative(1, 0, 0, 0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(u[1], lin.getPartialDerivative(0, 1, 0, 0), 1.0e-15 * JdkMath.abs(v[1].getValue()));
            Assert.assertEquals(u[2], lin.getPartialDerivative(0, 0, 1, 0), 1.0e-15 * JdkMath.abs(v[2].getValue()));

            lin = v[0].linearCombination(u[0], v[0], u[1], v[1], u[2], v[2], u[3], v[3]);
            ref = u[0] * v[0].getValue() +
                  u[1] * v[1].getValue() +
                  u[2] * v[2].getValue() +
                  u[3] * v[3].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(u[0], lin.getPartialDerivative(1, 0, 0, 0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(u[1], lin.getPartialDerivative(0, 1, 0, 0), 1.0e-15 * JdkMath.abs(v[1].getValue()));
            Assert.assertEquals(u[2], lin.getPartialDerivative(0, 0, 1, 0), 1.0e-15 * JdkMath.abs(v[2].getValue()));
            Assert.assertEquals(u[3], lin.getPartialDerivative(0, 0, 0, 1), 1.0e-15 * JdkMath.abs(v[3].getValue()));
        }
    }

    private void checkF0F1(DerivativeStructure ds, double value, double...derivatives) {

        // check dimension
        Assert.assertEquals(derivatives.length, ds.getFreeParameters());

        // check value, directly and also as 0th order derivative
        Assert.assertEquals(value, ds.getValue(), 1.0e-15);
        Assert.assertEquals(value, ds.getPartialDerivative(new int[ds.getFreeParameters()]), 1.0e-15);

        // check first order derivatives
        for (int i = 0; i < derivatives.length; ++i) {
            int[] orders = new int[derivatives.length];
            orders[i] = 1;
            Assert.assertEquals(derivatives[i], ds.getPartialDerivative(orders), 1.0e-15);
        }
    }

    private void checkEquals(DerivativeStructure ds1, DerivativeStructure ds2, double epsilon) {

        // check dimension
        Assert.assertEquals(ds1.getFreeParameters(), ds2.getFreeParameters());
        Assert.assertEquals(ds1.getOrder(), ds2.getOrder());

        int[] derivatives = new int[ds1.getFreeParameters()];
        int sum = 0;
        while (true) {

            if (sum <= ds1.getOrder()) {
                Assert.assertEquals(ds1.getPartialDerivative(derivatives),
                                    ds2.getPartialDerivative(derivatives),
                                    epsilon);
            }

            boolean increment = true;
            sum = 0;
            for (int i = derivatives.length - 1; i >= 0; --i) {
                if (increment) {
                    if (derivatives[i] == ds1.getOrder()) {
                        derivatives[i] = 0;
                    } else {
                        derivatives[i]++;
                        increment = false;
                    }
                }
                sum += derivatives[i];
            }
            if (increment) {
                return;
            }
        }
    }
}
