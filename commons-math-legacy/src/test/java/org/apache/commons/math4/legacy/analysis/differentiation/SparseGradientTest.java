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
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

public class SparseGradientTest extends ExtendedFieldElementAbstractTest<SparseGradient> {

    @Override
    protected SparseGradient build(final double x) {
        return SparseGradient.createVariable(0, x);
    }

    @Test
    public void testConstant() {
        double c = 1.0;
        SparseGradient grad = SparseGradient.createConstant(c);
        Assert.assertEquals(c, grad.getValue(), 1.0e-15); // returns the value
        Assert.assertEquals(0, grad.numVars(), 1.0e-15); // has no variables
    }

    @Test
    public void testVariable() {
        double v = 1.0;
        int id = 0;
        SparseGradient grad = SparseGradient.createVariable(id, v);
        Assert.assertEquals(v, grad.getValue(), 1.0e-15); // returns the value
        Assert.assertEquals(1, grad.numVars(), 1.0e-15); // has one variable
        Assert.assertEquals(1.0, grad.getDerivative(id), 1.0e-15); // derivative wr.t itself is 1
    }

    @Test
    public void testVarAddition() {
        final double v1 = 1.0;
        final double v2 = 2.0;
        final int id1 = -1;
        final int id2 = 3;
        final SparseGradient var1 = SparseGradient.createVariable(id1, v1);
        final SparseGradient var2 = SparseGradient.createVariable(id2, v2);
        final SparseGradient sum = var1.add(var2);

        Assert.assertEquals(v1 + v2, sum.getValue(), 1.0e-15); // returns the value
        Assert.assertEquals(2, sum.numVars());
        Assert.assertEquals(1.0, sum.getDerivative(id1), 1.0e-15);
        Assert.assertEquals(1.0, sum.getDerivative(id2), 1.0e-15);
    }

    @Test
    public void testSubtraction() {
        final double v1 = 1.0;
        final double v2 = 2.0;
        final int id1 = -1;
        final int id2 = 3;
        final SparseGradient var1 = SparseGradient.createVariable(id1, v1);
        final SparseGradient var2 = SparseGradient.createVariable(id2, v2);
        final SparseGradient sum = var1.subtract(var2);

        Assert.assertEquals(v1 - v2, sum.getValue(), 1.0e-15); // returns the value
        Assert.assertEquals(2, sum.numVars());
        Assert.assertEquals(1.0, sum.getDerivative(id1), 1.0e-15);
        Assert.assertEquals(-1.0, sum.getDerivative(id2), 1.0e-15);
    }

    @Test
    public void testDivision() {
        final double v1 = 1.0;
        final double v2 = 2.0;
        final int id1 = -1;
        final int id2 = 3;
        final SparseGradient var1 = SparseGradient.createVariable(id1, v1);
        final SparseGradient var2 = SparseGradient.createVariable(id2, v2);
        final SparseGradient out = var1.divide(var2);
        Assert.assertEquals(v1 / v2, out.getValue(), 1.0e-15); // returns the value
        Assert.assertEquals(2, out.numVars());
        Assert.assertEquals(1 / v2, out.getDerivative(id1), 1.0e-15);
        Assert.assertEquals(-1 / (v2 * v2), out.getDerivative(id2), 1.0e-15);
    }

    @Test
    public void testMult() {
        final double v1 = 1.0;
        final double c1 = 0.5;
        final double v2 = 2.0;
        final int id1 = -1;
        final int id2 = 3;
        final SparseGradient var1 = SparseGradient.createVariable(id1, v1);
        final SparseGradient unit1 = var1.multiply(c1);
        final SparseGradient unit2 = SparseGradient.createVariable(id2, v2).multiply(var1);
        final SparseGradient sum = unit1.add(unit2);
        Assert.assertEquals(v1 * c1 + v2 * v1, sum.getValue(), 1.0e-15); // returns the value
        Assert.assertEquals(2, sum.numVars());
        Assert.assertEquals(c1 + v2, sum.getDerivative(id1), 1.0e-15);
        Assert.assertEquals(v1, sum.getDerivative(id2), 1.0e-15);
    }

    @Test
    public void testVarMultInPlace() {
        final double v1 = 1.0;
        final double c1 = 0.5;
        final double v2 = 2.0;
        final int id1 = -1;
        final int id2 = 3;
        final SparseGradient var1 = SparseGradient.createVariable(id1, v1);
        final SparseGradient sum = var1.multiply(c1);
        final SparseGradient mult = SparseGradient.createVariable(id2, v2);
        mult.multiplyInPlace(var1);
        sum.addInPlace(mult);
        Assert.assertEquals(v1 * c1 + v2 * v1, sum.getValue(), 1.0e-15); // returns the value
        Assert.assertEquals(2, sum.numVars());
        Assert.assertEquals(c1 + v2, sum.getDerivative(id1), 1.0e-15);
        Assert.assertEquals(v1, sum.getDerivative(id2), 1.0e-15);
    }

    @Test
    public void testPrimitiveAdd() {
        checkF0F1(SparseGradient.createVariable(0, 1.0).add(5), 6.0, 1.0, 0.0, 0.0);
        checkF0F1(SparseGradient.createVariable(1, 2.0).add(5), 7.0, 0.0, 1.0, 0.0);
        checkF0F1(SparseGradient.createVariable(2, 3.0).add(5), 8.0, 0.0, 0.0, 1.0);
    }

    @Test
    public void testAdd() {
        SparseGradient x = SparseGradient.createVariable(0, 1.0);
        SparseGradient y = SparseGradient.createVariable(1, 2.0);
        SparseGradient z = SparseGradient.createVariable(2, 3.0);
        SparseGradient xyz = x.add(y.add(z));
        checkF0F1(xyz, x.getValue() + y.getValue() + z.getValue(), 1.0, 1.0, 1.0);
    }

    @Test
    public void testPrimitiveSubtract() {
        checkF0F1(SparseGradient.createVariable(0, 1.0).subtract(5), -4.0, 1.0, 0.0, 0.0);
        checkF0F1(SparseGradient.createVariable(1, 2.0).subtract(5), -3.0, 0.0, 1.0, 0.0);
        checkF0F1(SparseGradient.createVariable(2, 3.0).subtract(5), -2.0, 0.0, 0.0, 1.0);
    }

    @Test
    public void testSubtract() {
        SparseGradient x = SparseGradient.createVariable(0, 1.0);
        SparseGradient y = SparseGradient.createVariable(1, 2.0);
        SparseGradient z = SparseGradient.createVariable(2, 3.0);
        SparseGradient xyz = x.subtract(y.subtract(z));
        checkF0F1(xyz, x.getValue() - (y.getValue() - z.getValue()), 1.0, -1.0, 1.0);
    }

    @Test
    public void testPrimitiveMultiply() {
        checkF0F1(SparseGradient.createVariable(0, 1.0).multiply(5),  5.0, 5.0, 0.0, 0.0);
        checkF0F1(SparseGradient.createVariable(1, 2.0).multiply(5), 10.0, 0.0, 5.0, 0.0);
        checkF0F1(SparseGradient.createVariable(2, 3.0).multiply(5), 15.0, 0.0, 0.0, 5.0);
    }

    @Test
    public void testMultiply() {
        SparseGradient x = SparseGradient.createVariable(0, 1.0);
        SparseGradient y = SparseGradient.createVariable(1, 2.0);
        SparseGradient z = SparseGradient.createVariable(2, 3.0);
        SparseGradient xyz = x.multiply(y.multiply(z));
        checkF0F1(xyz, 6.0, 6.0, 3.0, 2.0);
    }

    @Test
    public void testNegate() {
        checkF0F1(SparseGradient.createVariable(0, 1.0).negate(), -1.0, -1.0, 0.0, 0.0);
        checkF0F1(SparseGradient.createVariable(1, 2.0).negate(), -2.0, 0.0, -1.0, 0.0);
        checkF0F1(SparseGradient.createVariable(2, 3.0).negate(), -3.0, 0.0, 0.0, -1.0);
    }

    @Test
    public void testReciprocal() {
        for (double x = 0.1; x < 1.2; x += 0.1) {
            SparseGradient r = SparseGradient.createVariable(0, x).reciprocal();
            Assert.assertEquals(1 / x, r.getValue(), 1.0e-15);
            final double expected = -1 / (x * x);
            Assert.assertEquals(expected, r.getDerivative(0), 1.0e-15 * JdkMath.abs(expected));
        }
    }

    @Test
    public void testPow() {
        for (int n = 0; n < 10; ++n) {

            SparseGradient x = SparseGradient.createVariable(0, 1.0);
            SparseGradient y = SparseGradient.createVariable(1, 2.0);
            SparseGradient z = SparseGradient.createVariable(2, 3.0);
            List<SparseGradient> list = Arrays.asList(x, y, z,
                                                      x.add(y).add(z),
                                                      x.multiply(y).multiply(z));

            if (n == 0) {
                for (SparseGradient sg : list) {
                    Assert.assertEquals(sg.getField().getOne(), sg.pow(n));
                }
            } else if (n == 1) {
                for (SparseGradient sg : list) {
                    Assert.assertEquals(sg, sg.pow(n));
                }
            } else {
                for (SparseGradient sg : list) {
                    SparseGradient p = sg.getField().getOne();
                    for (int i = 0; i < n; ++i) {
                        p = p.multiply(sg);
                    }
                    Assert.assertEquals(p, sg.pow(n));
                }
            }
        }
    }

    @Test
    public void testPowDoubleDS() {
        for (int maxOrder = 1; maxOrder < 5; ++maxOrder) {

            SparseGradient x = SparseGradient.createVariable(0, 0.1);
            SparseGradient y = SparseGradient.createVariable(1, 0.2);
            SparseGradient z = SparseGradient.createVariable(2, 0.3);
            List<SparseGradient> list = Arrays.asList(x, y, z,
                                                      x.add(y).add(z),
                                                      x.multiply(y).multiply(z));

            for (SparseGradient sg : list) {
                // the special case a = 0 is included here
                for (double a : new double[] { 0.0, 0.1, 1.0, 2.0, 5.0 }) {
                    SparseGradient reference = (a == 0) ?
                                               x.getField().getZero() :
                                               SparseGradient.createConstant(a).pow(sg);
                    SparseGradient result = SparseGradient.pow(a, sg);
                    Assert.assertEquals(reference, result);
                }
            }

            // negative base: -1^x can be evaluated for integers only, so value is sometimes OK, derivatives are always NaN
            SparseGradient negEvenInteger = SparseGradient.pow(-2.0, SparseGradient.createVariable(0, 2.0));
            Assert.assertEquals(4.0, negEvenInteger.getValue(), 1.0e-15);
            Assert.assertTrue(Double.isNaN(negEvenInteger.getDerivative(0)));
            SparseGradient negOddInteger = SparseGradient.pow(-2.0, SparseGradient.createVariable(0, 3.0));
            Assert.assertEquals(-8.0, negOddInteger.getValue(), 1.0e-15);
            Assert.assertTrue(Double.isNaN(negOddInteger.getDerivative(0)));
            SparseGradient negNonInteger = SparseGradient.pow(-2.0, SparseGradient.createVariable(0, 2.001));
            Assert.assertTrue(Double.isNaN(negNonInteger.getValue()));
            Assert.assertTrue(Double.isNaN(negNonInteger.getDerivative(0)));

            SparseGradient zeroNeg = SparseGradient.pow(0.0, SparseGradient.createVariable(0, -1.0));
            Assert.assertTrue(Double.isNaN(zeroNeg.getValue()));
            Assert.assertTrue(Double.isNaN(zeroNeg.getDerivative(0)));
            SparseGradient posNeg = SparseGradient.pow(2.0, SparseGradient.createVariable(0, -2.0));
            Assert.assertEquals(1.0 / 4.0, posNeg.getValue(), 1.0e-15);
            Assert.assertEquals(JdkMath.log(2.0) / 4.0, posNeg.getDerivative(0), 1.0e-15);

            // very special case: a = 0 and power = 0
            SparseGradient zeroZero = SparseGradient.pow(0.0, SparseGradient.createVariable(0, 0.0));

            // this should be OK for simple first derivative with one variable only ...
            Assert.assertEquals(1.0, zeroZero.getValue(), 1.0e-15);
            Assert.assertEquals(Double.NEGATIVE_INFINITY, zeroZero.getDerivative(0), 1.0e-15);
            Assert.assertEquals(0.0, zeroZero.getDerivative(1), 1.0e-15);
            Assert.assertEquals(0.0, zeroZero.getDerivative(2), 1.0e-15);
        }
    }

    @Test
    public void testExpression() {
        double epsilon = 2.5e-13;
        for (double x = 0; x < 2; x += 0.2) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            for (double y = 0; y < 2; y += 0.2) {
                SparseGradient sgY = SparseGradient.createVariable(1, y);
                for (double z = 0; z >- 2; z -= 0.2) {
                    SparseGradient sgZ = SparseGradient.createVariable(2, z);

                    // f(x, y, z) = x + 5 x y - 2 z + (8 z x - y)^3
                    SparseGradient sg =
                            sgZ.linearCombination(1, sgX,
                                                  5, sgX.multiply(sgY),
                                                 -2, sgZ,
                                                 1, sgZ.linearCombination(8, sgZ.multiply(sgX), -1, sgY).pow(3));
                    double f = x + 5 * x * y - 2 * z + JdkMath.pow(8 * z * x - y, 3);
                    Assert.assertEquals(f, sg.getValue(), JdkMath.abs(epsilon * f));

                    // df/dx = 1 + 5 y + 24 (8 z x - y)^2 z
                    double dfdx = 1 + 5 * y + 24 * z * JdkMath.pow(8 * z * x - y, 2);
                    Assert.assertEquals(dfdx, sg.getDerivative(0), JdkMath.abs(epsilon * dfdx));
                }
            }
        }
    }

    @Test
    public void testCompositionOneVariableX() {
        double epsilon = 1.0e-13;
        for (double x = 0.1; x < 1.2; x += 0.1) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            for (double y = 0.1; y < 1.2; y += 0.1) {
                SparseGradient sgY = SparseGradient.createConstant(y);
                SparseGradient f = sgX.divide(sgY).sqrt();
                double f0 = JdkMath.sqrt(x / y);
                Assert.assertEquals(f0, f.getValue(), JdkMath.abs(epsilon * f0));
                double f1 = 1 / (2 * JdkMath.sqrt(x * y));
                Assert.assertEquals(f1, f.getDerivative(0), JdkMath.abs(epsilon * f1));
            }
        }
    }

    @Test
    public void testTrigo() {
        double epsilon = 2.0e-12;
            for (double x = 0.1; x < 1.2; x += 0.1) {
                SparseGradient sgX = SparseGradient.createVariable(0, x);
                for (double y = 0.1; y < 1.2; y += 0.1) {
                    SparseGradient sgY = SparseGradient.createVariable(1, y);
                    for (double z = 0.1; z < 1.2; z += 0.1) {
                        SparseGradient sgZ = SparseGradient.createVariable(2, z);
                        SparseGradient f = sgX.divide(sgY.cos().add(sgZ.tan())).sin();
                        double a = JdkMath.cos(y) + JdkMath.tan(z);
                        double f0 = JdkMath.sin(x / a);
                        Assert.assertEquals(f0, f.getValue(), JdkMath.abs(epsilon * f0));
                        double dfdx = JdkMath.cos(x / a) / a;
                        Assert.assertEquals(dfdx, f.getDerivative(0), JdkMath.abs(epsilon * dfdx));
                        double dfdy =  x * JdkMath.sin(y) * dfdx / a;
                        Assert.assertEquals(dfdy, f.getDerivative(1), JdkMath.abs(epsilon * dfdy));
                        double cz = JdkMath.cos(z);
                        double cz2 = cz * cz;
                        double dfdz = -x * dfdx / (a * cz2);
                        Assert.assertEquals(dfdz, f.getDerivative(2), JdkMath.abs(epsilon * dfdz));
                    }
                }
            }
    }

    @Test
    public void testSqrtDefinition() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient sqrt1 = sgX.pow(0.5);
            SparseGradient sqrt2 = sgX.sqrt();
            SparseGradient zero = sqrt1.subtract(sqrt2);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testRootNSingularity() {
        for (int n = 2; n < 10; ++n) {
            SparseGradient sgZero = SparseGradient.createVariable(0, 0.0);
            SparseGradient rootN  = sgZero.rootN(n);
            Assert.assertEquals(0.0, rootN.getValue(), 1.0e-5);
            Assert.assertTrue(Double.isInfinite(rootN.getDerivative(0)));
            Assert.assertTrue(rootN.getDerivative(0) > 0);
        }
    }

    @Test
    public void testSqrtPow2() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.multiply(sgX).sqrt();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testCbrtDefinition() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient cbrt1 = sgX.pow(1.0 / 3.0);
            SparseGradient cbrt2 = sgX.cbrt();
            SparseGradient zero = cbrt1.subtract(cbrt2);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testCbrtPow3() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.multiply(sgX.multiply(sgX)).cbrt();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testPowReciprocalPow() {
        for (double x = 0.1; x < 1.2; x += 0.01) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            for (double y = 0.1; y < 1.2; y += 0.01) {
                SparseGradient sgY = SparseGradient.createVariable(1, y);
                SparseGradient rebuiltX = sgX.pow(sgY).pow(sgY.reciprocal());
                SparseGradient zero = rebuiltX.subtract(sgX);
                checkF0F1(zero, 0.0, 0.0, 0.0);
            }
        }
    }

    @Test
    public void testHypotDefinition() {
        for (double x = -1.7; x < 2; x += 0.2) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            for (double y = -1.7; y < 2; y += 0.2) {
                SparseGradient sgY = SparseGradient.createVariable(1, y);
                SparseGradient hypot = SparseGradient.hypot(sgY, sgX);
                SparseGradient ref = sgX.multiply(sgX).add(sgY.multiply(sgY)).sqrt();
                SparseGradient zero = hypot.subtract(ref);
                checkF0F1(zero, 0.0, 0.0, 0.0);
            }
        }
    }

    @Test
    public void testHypotNoOverflow() {

        SparseGradient sgX = SparseGradient.createVariable(0, +3.0e250);
        SparseGradient sgY = SparseGradient.createVariable(1, -4.0e250);
        SparseGradient hypot = SparseGradient.hypot(sgX, sgY);
        Assert.assertEquals(5.0e250, hypot.getValue(), 1.0e235);
        Assert.assertEquals(sgX.getValue() / hypot.getValue(), hypot.getDerivative(0), 1.0e-10);
        Assert.assertEquals(sgY.getValue() / hypot.getValue(), hypot.getDerivative(1), 1.0e-10);

        SparseGradient sqrt  = sgX.multiply(sgX).add(sgY.multiply(sgY)).sqrt();
        Assert.assertTrue(Double.isInfinite(sqrt.getValue()));
    }

    @Test
    public void testHypotNeglectible() {

        SparseGradient sgSmall = SparseGradient.createVariable(0, +3.0e-10);
        SparseGradient sgLarge = SparseGradient.createVariable(1, -4.0e25);

        Assert.assertEquals(sgLarge.abs().getValue(),
                            SparseGradient.hypot(sgSmall, sgLarge).getValue(),
                            1.0e-10);
        Assert.assertEquals(0,
                            SparseGradient.hypot(sgSmall, sgLarge).getDerivative(0),
                            1.0e-10);
        Assert.assertEquals(-1,
                            SparseGradient.hypot(sgSmall, sgLarge).getDerivative(1),
                            1.0e-10);

        Assert.assertEquals(sgLarge.abs().getValue(),
                            SparseGradient.hypot(sgLarge, sgSmall).getValue(),
                            1.0e-10);
        Assert.assertEquals(0,
                            SparseGradient.hypot(sgLarge, sgSmall).getDerivative(0),
                            1.0e-10);
        Assert.assertEquals(-1,
                            SparseGradient.hypot(sgLarge, sgSmall).getDerivative(1),
                            1.0e-10);
    }

    @Test
    public void testHypotSpecial() {
        Assert.assertTrue(Double.isNaN(SparseGradient.hypot(SparseGradient.createVariable(0, Double.NaN),
                                                                 SparseGradient.createVariable(0, +3.0e250)).getValue()));
        Assert.assertTrue(Double.isNaN(SparseGradient.hypot(SparseGradient.createVariable(0, +3.0e250),
                                                                 SparseGradient.createVariable(0, Double.NaN)).getValue()));
        Assert.assertTrue(Double.isInfinite(SparseGradient.hypot(SparseGradient.createVariable(0, Double.POSITIVE_INFINITY),
                                                                      SparseGradient.createVariable(0, +3.0e250)).getValue()));
        Assert.assertTrue(Double.isInfinite(SparseGradient.hypot(SparseGradient.createVariable(0, +3.0e250),
                                                                      SparseGradient.createVariable(0, Double.POSITIVE_INFINITY)).getValue()));
    }

    @Test
    public void testPrimitiveRemainder() {
        for (double x = -1.7; x < 2; x += 0.2) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            for (double y = -1.7; y < 2; y += 0.2) {
                SparseGradient remainder = sgX.remainder(y);
                SparseGradient ref = sgX.subtract(x - JdkMath.IEEEremainder(x, y));
                SparseGradient zero = remainder.subtract(ref);
                checkF0F1(zero, 0.0, 0.0, 0.0);
            }
        }
    }

    @Test
    public void testRemainder() {
        for (double x = -1.7; x < 2; x += 0.2) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            for (double y = -1.7; y < 2; y += 0.2) {
                SparseGradient sgY = SparseGradient.createVariable(1, y);
                SparseGradient remainder = sgX.remainder(sgY);
                SparseGradient ref = sgX.subtract(sgY.multiply((x - JdkMath.IEEEremainder(x, y)) / y));
                SparseGradient zero = remainder.subtract(ref);
                checkF0F1(zero, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    @Test
    public void testExp() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            double refExp = JdkMath.exp(x);
            checkF0F1(SparseGradient.createVariable(0, x).exp(), refExp, refExp);
        }
    }

    @Test
    public void testExpm1Definition() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient expm11 = sgX.expm1();
            SparseGradient expm12 = sgX.exp().subtract(sgX.getField().getOne());
            SparseGradient zero = expm11.subtract(expm12);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Override
    @Test
    public void testLog() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            checkF0F1(SparseGradient.createVariable(0, x).log(), JdkMath.log(x), 1.0 / x);
        }
    }

    @Test
    public void testLog1pDefinition() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient log1p1 = sgX.log1p();
            SparseGradient log1p2 = sgX.add(sgX.getField().getOne()).log();
            SparseGradient zero = log1p1.subtract(log1p2);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testLog10Definition() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient log101 = sgX.log10();
            SparseGradient log102 = sgX.log().divide(JdkMath.log(10.0));
            SparseGradient zero = log101.subtract(log102);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testLogExp() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.exp().log();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testLog1pExpm1() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.expm1().log1p();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testLog10Power() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = SparseGradient.pow(10.0, sgX).log10();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testSinCos() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient sin = sgX.sin();
            SparseGradient cos = sgX.cos();
            double s = JdkMath.sin(x);
            double c = JdkMath.cos(x);
            checkF0F1(sin, s, c);
            checkF0F1(cos, c, -s);
        }
    }

    @Test
    public void testSinAsin() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.sin().asin();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testCosAcos() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.cos().acos();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testTanAtan() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.tan().atan();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testTangentDefinition() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient tan1 = sgX.sin().divide(sgX.cos());
            SparseGradient tan2 = sgX.tan();
            SparseGradient zero = tan1.subtract(tan2);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Override
    @Test
    public void testAtan2() {
        for (double x = -1.7; x < 2; x += 0.2) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            for (double y = -1.7; y < 2; y += 0.2) {
                SparseGradient sgY = SparseGradient.createVariable(1, y);
                SparseGradient atan2 = SparseGradient.atan2(sgY, sgX);
                SparseGradient ref = sgY.divide(sgX).atan();
                if (x < 0) {
                    ref = (y < 0) ? ref.subtract(JdkMath.PI) : ref.add(JdkMath.PI);
                }
                SparseGradient zero = atan2.subtract(ref);
                checkF0F1(zero, 0.0, 0.0);
            }
        }
    }

    @Test
    public void testAtan2SpecialCases() {

        SparseGradient pp =
                SparseGradient.atan2(SparseGradient.createVariable(1, +0.0),
                                          SparseGradient.createVariable(1, +0.0));
        Assert.assertEquals(0, pp.getValue(), 1.0e-15);
        Assert.assertEquals(+1, JdkMath.copySign(1, pp.getValue()), 1.0e-15);

        SparseGradient pn =
                SparseGradient.atan2(SparseGradient.createVariable(1, +0.0),
                                          SparseGradient.createVariable(1, -0.0));
        Assert.assertEquals(JdkMath.PI, pn.getValue(), 1.0e-15);

        SparseGradient np =
                SparseGradient.atan2(SparseGradient.createVariable(1, -0.0),
                                          SparseGradient.createVariable(1, +0.0));
        Assert.assertEquals(0, np.getValue(), 1.0e-15);
        Assert.assertEquals(-1, JdkMath.copySign(1, np.getValue()), 1.0e-15);

        SparseGradient nn =
                SparseGradient.atan2(SparseGradient.createVariable(1, -0.0),
                                          SparseGradient.createVariable(1, -0.0));
        Assert.assertEquals(-JdkMath.PI, nn.getValue(), 1.0e-15);
    }

    @Test
    public void testSinhDefinition() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient sinh1 = sgX.exp().subtract(sgX.exp().reciprocal()).multiply(0.5);
            SparseGradient sinh2 = sgX.sinh();
            SparseGradient zero = sinh1.subtract(sinh2);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testCoshDefinition() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient cosh1 = sgX.exp().add(sgX.exp().reciprocal()).multiply(0.5);
            SparseGradient cosh2 = sgX.cosh();
            SparseGradient zero = cosh1.subtract(cosh2);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testTanhDefinition() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient tanh1 = sgX.exp().subtract(sgX.exp().reciprocal()).divide(sgX.exp().add(sgX.exp().reciprocal()));
            SparseGradient tanh2 = sgX.tanh();
            SparseGradient zero = tanh1.subtract(tanh2);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testSinhAsinh() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.sinh().asinh();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testCoshAcosh() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.cosh().acosh();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testTanhAtanh() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.tanh().atanh();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testCompositionOneVariableY() {
        for (double x = 0.1; x < 1.2; x += 0.1) {
            SparseGradient sgX = SparseGradient.createConstant(x);
            for (double y = 0.1; y < 1.2; y += 0.1) {
                SparseGradient sgY = SparseGradient.createVariable(0, y);
                SparseGradient f = sgX.divide(sgY).sqrt();
                double f0 = JdkMath.sqrt(x / y);
                double f1 = -x / (2 * y * y * f0);
                checkF0F1(f, f0, f1);
            }
        }
    }

    @Test
    public void testTaylorPolynomial() {
        for (double x = 0; x < 1.2; x += 0.1) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            for (double y = 0; y < 1.2; y += 0.2) {
                SparseGradient sgY = SparseGradient.createVariable(1, y);
                for (double z = 0; z < 1.2; z += 0.2) {
                    SparseGradient sgZ = SparseGradient.createVariable(2, z);
                    SparseGradient f = sgX.multiply(3).add(sgZ.multiply(-2)).add(sgY.multiply(5));
                    for (double dx = -0.2; dx < 0.2; dx += 0.2) {
                        for (double dy = -0.2; dy < 0.2; dy += 0.1) {
                            for (double dz = -0.2; dz < 0.2; dz += 0.1) {
                                double ref = 3 * (x + dx) + 5 * (y + dy) -2 * (z + dz);
                                Assert.assertEquals(ref, f.taylor(dx, dy, dz), 3.0e-15);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testTaylorAtan2() {
        double x0 =  0.1;
        double y0 = -0.3;
            SparseGradient sgX   = SparseGradient.createVariable(0, x0);
            SparseGradient sgY   = SparseGradient.createVariable(1, y0);
            SparseGradient atan2 = SparseGradient.atan2(sgY, sgX);
            double maxError = 0;
            for (double dx = -0.05; dx < 0.05; dx += 0.001) {
                for (double dy = -0.05; dy < 0.05; dy += 0.001) {
                    double ref = JdkMath.atan2(y0 + dy, x0 + dx);
                    maxError = JdkMath.max(maxError, JdkMath.abs(ref - atan2.taylor(dx, dy)));
                }
            }
            double expectedError = 0.0241;
            Assert.assertEquals(expectedError, maxError, 0.01 * expectedError);
    }

    @Override
    @Test
    public void testAbs() {

        SparseGradient minusOne = SparseGradient.createVariable(0, -1.0);
        Assert.assertEquals(+1.0, minusOne.abs().getValue(), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.abs().getDerivative(0), 1.0e-15);

        SparseGradient plusOne = SparseGradient.createVariable(0, +1.0);
        Assert.assertEquals(+1.0, plusOne.abs().getValue(), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.abs().getDerivative(0), 1.0e-15);

        SparseGradient minusZero = SparseGradient.createVariable(0, -0.0);
        Assert.assertEquals(+0.0, minusZero.abs().getValue(), 1.0e-15);
        Assert.assertEquals(-1.0, minusZero.abs().getDerivative(0), 1.0e-15);

        SparseGradient plusZero = SparseGradient.createVariable(0, +0.0);
        Assert.assertEquals(+0.0, plusZero.abs().getValue(), 1.0e-15);
        Assert.assertEquals(+1.0, plusZero.abs().getDerivative(0), 1.0e-15);
    }

    @Override
    @Test
    public void testSignum() {

        SparseGradient minusOne = SparseGradient.createVariable(0, -1.0);
        Assert.assertEquals(-1.0, minusOne.signum().getValue(), 1.0e-15);
        Assert.assertEquals( 0.0, minusOne.signum().getDerivative(0), 1.0e-15);

        SparseGradient plusOne = SparseGradient.createVariable(0, +1.0);
        Assert.assertEquals(+1.0, plusOne.signum().getValue(), 1.0e-15);
        Assert.assertEquals( 0.0, plusOne.signum().getDerivative(0), 1.0e-15);

        SparseGradient minusZero = SparseGradient.createVariable(0, -0.0);
        Assert.assertEquals(-0.0, minusZero.signum().getValue(), 1.0e-15);
        Assert.assertTrue(Double.doubleToLongBits(minusZero.signum().getValue()) < 0);
        Assert.assertEquals( 0.0, minusZero.signum().getDerivative(0), 1.0e-15);

        SparseGradient plusZero = SparseGradient.createVariable(0, +0.0);
        Assert.assertEquals(+0.0, plusZero.signum().getValue(), 1.0e-15);
        Assert.assertEquals(0, Double.doubleToLongBits(plusZero.signum().getValue()));
        Assert.assertEquals( 0.0, plusZero.signum().getDerivative(0), 1.0e-15);
    }

    @Test
    public void testCeilFloorRintLong() {

        SparseGradient x = SparseGradient.createVariable(0, -1.5);
        Assert.assertEquals(-1.5, x.getValue(), 1.0e-15);
        Assert.assertEquals(+1.0, x.getDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, x.ceil().getValue(), 1.0e-15);
        Assert.assertEquals(+0.0, x.ceil().getDerivative(0), 1.0e-15);
        Assert.assertEquals(-2.0, x.floor().getValue(), 1.0e-15);
        Assert.assertEquals(+0.0, x.floor().getDerivative(0), 1.0e-15);
        Assert.assertEquals(-2.0, x.rint().getValue(), 1.0e-15);
        Assert.assertEquals(+0.0, x.rint().getDerivative(0), 1.0e-15);
        Assert.assertEquals(-2.0, x.subtract(x.getField().getOne()).rint().getValue(), 1.0e-15);
        Assert.assertEquals(-1L, x.round(), 1.0e-15);
    }

    @Test
    public void testCopySign() {

        SparseGradient minusOne = SparseGradient.createVariable(0, -1.0);
        Assert.assertEquals(+1.0, minusOne.copySign(+1.0).getValue(), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.copySign(+1.0).getDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.copySign(-1.0).getValue(), 1.0e-15);
        Assert.assertEquals(+1.0, minusOne.copySign(-1.0).getDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, minusOne.copySign(+0.0).getValue(), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.copySign(+0.0).getDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.copySign(-0.0).getValue(), 1.0e-15);
        Assert.assertEquals(+1.0, minusOne.copySign(-0.0).getDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, minusOne.copySign(Double.NaN).getValue(), 1.0e-15);
        Assert.assertEquals(-1.0, minusOne.copySign(Double.NaN).getDerivative(0), 1.0e-15);

        SparseGradient plusOne = SparseGradient.createVariable(0, +1.0);
        Assert.assertEquals(+1.0, plusOne.copySign(+1.0).getValue(), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.copySign(+1.0).getDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, plusOne.copySign(-1.0).getValue(), 1.0e-15);
        Assert.assertEquals(-1.0, plusOne.copySign(-1.0).getDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.copySign(+0.0).getValue(), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.copySign(+0.0).getDerivative(0), 1.0e-15);
        Assert.assertEquals(-1.0, plusOne.copySign(-0.0).getValue(), 1.0e-15);
        Assert.assertEquals(-1.0, plusOne.copySign(-0.0).getDerivative(0), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.copySign(Double.NaN).getValue(), 1.0e-15);
        Assert.assertEquals(+1.0, plusOne.copySign(Double.NaN).getDerivative(0), 1.0e-15);
    }

    @Test
    public void testToDegreesDefinition() {
        double epsilon = 3.0e-16;
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                SparseGradient sgX = SparseGradient.createVariable(0, x);
                Assert.assertEquals(JdkMath.toDegrees(x), sgX.toDegrees().getValue(), epsilon);
                Assert.assertEquals(180 / JdkMath.PI, sgX.toDegrees().getDerivative(0), epsilon);
            }
        }
    }

    @Test
    public void testToRadiansDefinition() {
        double epsilon = 3.0e-16;
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                SparseGradient sgX = SparseGradient.createVariable(0, x);
                Assert.assertEquals(JdkMath.toRadians(x), sgX.toRadians().getValue(), epsilon);
                Assert.assertEquals(JdkMath.PI / 180, sgX.toRadians().getDerivative(0), epsilon);
            }
        }
    }

    @Test
    public void testDegRad() {
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient rebuiltX = sgX.toDegrees().toRadians();
            SparseGradient zero = rebuiltX.subtract(sgX);
            checkF0F1(zero, 0, 0);
        }
    }

    @Test
    public void testCompose() {
        PolynomialFunction poly =
                new PolynomialFunction(new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 });
        for (double x = 0.1; x < 1.2; x += 0.001) {
            SparseGradient sgX = SparseGradient.createVariable(0, x);
            SparseGradient sgY1 = sgX.getField().getZero();
            for (int i = poly.degree(); i >= 0; --i) {
                sgY1 = sgY1.multiply(sgX).add(poly.getCoefficients()[i]);
            }
            SparseGradient sgY2 = sgX.compose(poly.value(x), poly.polynomialDerivative().value(x));
            SparseGradient zero = sgY1.subtract(sgY2);
            checkF0F1(zero, 0.0, 0.0);
        }
    }

    @Test
    public void testField() {
            SparseGradient x = SparseGradient.createVariable(0, 1.0);
            checkF0F1(x.getField().getZero(), 0.0, 0.0, 0.0, 0.0);
            checkF0F1(x.getField().getOne(), 1.0, 0.0, 0.0, 0.0);
            Assert.assertEquals(SparseGradient.class, x.getField().getRuntimeClass());
    }

    @Test
    public void testLinearCombination1DSDS() {
        final SparseGradient[] a = new SparseGradient[] {
            SparseGradient.createVariable(0, -1321008684645961.0 / 268435456.0),
            SparseGradient.createVariable(1, -5774608829631843.0 / 268435456.0),
            SparseGradient.createVariable(2, -7645843051051357.0 / 8589934592.0)
        };
        final SparseGradient[] b = new SparseGradient[] {
            SparseGradient.createVariable(3, -5712344449280879.0 / 2097152.0),
            SparseGradient.createVariable(4, -4550117129121957.0 / 2097152.0),
            SparseGradient.createVariable(5, 8846951984510141.0 / 131072.0)
        };

        final SparseGradient abSumInline = a[0].linearCombination(a[0], b[0], a[1], b[1], a[2], b[2]);
        final SparseGradient abSumArray = a[0].linearCombination(a, b);

        Assert.assertEquals(abSumInline.getValue(), abSumArray.getValue(), 1.0e-15);
        Assert.assertEquals(-1.8551294182586248737720779899, abSumInline.getValue(), 1.0e-15);
        Assert.assertEquals(b[0].getValue(), abSumInline.getDerivative(0), 1.0e-15);
        Assert.assertEquals(b[1].getValue(), abSumInline.getDerivative(1), 1.0e-15);
        Assert.assertEquals(b[2].getValue(), abSumInline.getDerivative(2), 1.0e-15);
        Assert.assertEquals(a[0].getValue(), abSumInline.getDerivative(3), 1.0e-15);
        Assert.assertEquals(a[1].getValue(), abSumInline.getDerivative(4), 1.0e-15);
        Assert.assertEquals(a[2].getValue(), abSumInline.getDerivative(5), 1.0e-15);
    }

    @Test
    public void testLinearCombination1DoubleDS() {
        final double[] a = new double[] {
            -1321008684645961.0 / 268435456.0,
            -5774608829631843.0 / 268435456.0,
            -7645843051051357.0 / 8589934592.0
        };
        final SparseGradient[] b = new SparseGradient[] {
            SparseGradient.createVariable(0, -5712344449280879.0 / 2097152.0),
            SparseGradient.createVariable(1, -4550117129121957.0 / 2097152.0),
            SparseGradient.createVariable(2, 8846951984510141.0 / 131072.0)
        };

        final SparseGradient abSumInline = b[0].linearCombination(a[0], b[0],
                                                                       a[1], b[1],
                                                                       a[2], b[2]);
        final SparseGradient abSumArray = b[0].linearCombination(a, b);

        Assert.assertEquals(abSumInline.getValue(), abSumArray.getValue(), 1.0e-15);
        Assert.assertEquals(-1.8551294182586248737720779899, abSumInline.getValue(), 1.0e-15);
        Assert.assertEquals(a[0], abSumInline.getDerivative(0), 1.0e-15);
        Assert.assertEquals(a[1], abSumInline.getDerivative(1), 1.0e-15);
        Assert.assertEquals(a[2], abSumInline.getDerivative(2), 1.0e-15);
    }

    @Test
    public void testLinearCombination2DSDS() {
        // we compare accurate versus naive dot product implementations
        // on regular vectors (i.e. not extreme cases like in the previous test)
        UniformRandomProvider random = RandomSource.WELL_1024_A.create(0xc6af886975069f11L);

        for (int i = 0; i < 10000; ++i) {
            final SparseGradient[] u = new SparseGradient[4];
            final SparseGradient[] v = new SparseGradient[4];
            for (int j = 0; j < u.length; ++j) {
                u[j] = SparseGradient.createVariable(j, 1e17 * random.nextDouble());
                v[j] = SparseGradient.createConstant(1e17 * random.nextDouble());
            }

            SparseGradient lin = u[0].linearCombination(u[0], v[0], u[1], v[1]);
            double ref = u[0].getValue() * v[0].getValue() +
                         u[1].getValue() * v[1].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(v[0].getValue(), lin.getDerivative(0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(v[1].getValue(), lin.getDerivative(1), 1.0e-15 * JdkMath.abs(v[1].getValue()));

            lin = u[0].linearCombination(u[0], v[0], u[1], v[1], u[2], v[2]);
            ref = u[0].getValue() * v[0].getValue() +
                  u[1].getValue() * v[1].getValue() +
                  u[2].getValue() * v[2].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(v[0].getValue(), lin.getDerivative(0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(v[1].getValue(), lin.getDerivative(1), 1.0e-15 * JdkMath.abs(v[1].getValue()));
            Assert.assertEquals(v[2].getValue(), lin.getDerivative(2), 1.0e-15 * JdkMath.abs(v[2].getValue()));

            lin = u[0].linearCombination(u[0], v[0], u[1], v[1], u[2], v[2], u[3], v[3]);
            ref = u[0].getValue() * v[0].getValue() +
                  u[1].getValue() * v[1].getValue() +
                  u[2].getValue() * v[2].getValue() +
                  u[3].getValue() * v[3].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(v[0].getValue(), lin.getDerivative(0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(v[1].getValue(), lin.getDerivative(1), 1.0e-15 * JdkMath.abs(v[1].getValue()));
            Assert.assertEquals(v[2].getValue(), lin.getDerivative(2), 1.0e-15 * JdkMath.abs(v[2].getValue()));
            Assert.assertEquals(v[3].getValue(), lin.getDerivative(3), 1.0e-15 * JdkMath.abs(v[3].getValue()));
        }
    }

    @Test
    public void testLinearCombination2DoubleDS() {
        // we compare accurate versus naive dot product implementations
        // on regular vectors (i.e. not extreme cases like in the previous test)
        UniformRandomProvider random = RandomSource.WELL_1024_A.create(0xc6af886975069f11L);

        for (int i = 0; i < 10000; ++i) {
            final double[] u = new double[4];
            final SparseGradient[] v = new SparseGradient[4];
            for (int j = 0; j < u.length; ++j) {
                u[j] = 1e17 * random.nextDouble();
                v[j] = SparseGradient.createVariable(j, 1e17 * random.nextDouble());
            }

            SparseGradient lin = v[0].linearCombination(u[0], v[0], u[1], v[1]);
            double ref = u[0] * v[0].getValue() +
                         u[1] * v[1].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(u[0], lin.getDerivative(0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(u[1], lin.getDerivative(1), 1.0e-15 * JdkMath.abs(v[1].getValue()));

            lin = v[0].linearCombination(u[0], v[0], u[1], v[1], u[2], v[2]);
            ref = u[0] * v[0].getValue() +
                  u[1] * v[1].getValue() +
                  u[2] * v[2].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(u[0], lin.getDerivative(0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(u[1], lin.getDerivative(1), 1.0e-15 * JdkMath.abs(v[1].getValue()));
            Assert.assertEquals(u[2], lin.getDerivative(2), 1.0e-15 * JdkMath.abs(v[2].getValue()));

            lin = v[0].linearCombination(u[0], v[0], u[1], v[1], u[2], v[2], u[3], v[3]);
            ref = u[0] * v[0].getValue() +
                  u[1] * v[1].getValue() +
                  u[2] * v[2].getValue() +
                  u[3] * v[3].getValue();
            Assert.assertEquals(ref, lin.getValue(), 1.0e-15 * JdkMath.abs(ref));
            Assert.assertEquals(u[0], lin.getDerivative(0), 1.0e-15 * JdkMath.abs(v[0].getValue()));
            Assert.assertEquals(u[1], lin.getDerivative(1), 1.0e-15 * JdkMath.abs(v[1].getValue()));
            Assert.assertEquals(u[2], lin.getDerivative(2), 1.0e-15 * JdkMath.abs(v[2].getValue()));
            Assert.assertEquals(u[3], lin.getDerivative(3), 1.0e-15 * JdkMath.abs(v[3].getValue()));
        }
    }

    private void checkF0F1(SparseGradient sg, double value, double...derivatives) {

        // check value
        Assert.assertEquals(value, sg.getValue(), 1.0e-13);

        // check first order derivatives
        for (int i = 0; i < derivatives.length; ++i) {
            Assert.assertEquals(derivatives[i], sg.getDerivative(i), 1.0e-13);
        }
    }
}
