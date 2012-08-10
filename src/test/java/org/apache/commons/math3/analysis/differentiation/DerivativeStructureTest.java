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

package org.apache.commons.math3.analysis.differentiation;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link DerivativeStructure}.
 */
public class DerivativeStructureTest {

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
            checkF0F1(new DerivativeStructure(3, maxOrder, FastMath.PI),
                      FastMath.PI, 0.0, 0.0, 0.0);
        }
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
                double expected = ArithmeticUtils.pow(-1, i) * ArithmeticUtils.factorial(i) /
                                  FastMath.pow(x, i + 1);
                Assert.assertEquals(expected, r.getPartialDerivative(i), 1.0e-15 * FastMath.abs(expected));
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
                    double f = x + 5 * x * y - 2 * z + FastMath.pow(8 * z * x - y, 3);
                    Assert.assertEquals(f, ds.getValue(),
                                        FastMath.abs(epsilon * f));

                    // df/dx = 1 + 5 y + 24 (8 z x - y)^2 z
                    double dfdx = 1 + 5 * y + 24 * z * FastMath.pow(8 * z * x - y, 2);
                    Assert.assertEquals(dfdx, ds.getPartialDerivative(1, 0, 0),
                                        FastMath.abs(epsilon * dfdx));

                    // df/dxdy = 5 + 48 z*(y - 8 z x)
                    double dfdxdy = 5 + 48 * z * (y - 8 * z * x);
                    Assert.assertEquals(dfdxdy, ds.getPartialDerivative(1, 1, 0),
                                        FastMath.abs(epsilon * dfdxdy));

                    // df/dxdydz = 48 (y - 16 z x)
                    double dfdxdydz = 48 * (y - 16 * z * x);
                    Assert.assertEquals(dfdxdydz, ds.getPartialDerivative(1, 1, 1),
                                        FastMath.abs(epsilon * dfdxdydz));

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
                    double f0 = FastMath.sqrt(x / y);
                    Assert.assertEquals(f0, f.getValue(), FastMath.abs(epsilon * f0));
                    if (f.getOrder() > 0) {
                        double f1 = 1 / (2 * FastMath.sqrt(x * y));
                        Assert.assertEquals(f1, f.getPartialDerivative(1), FastMath.abs(epsilon * f1));
                        if (f.getOrder() > 1) {
                            double f2 = -f1 / (2 * x); 
                            Assert.assertEquals(f2, f.getPartialDerivative(2), FastMath.abs(epsilon * f2));
                            if (f.getOrder() > 2) {
                                double f3 = (f0 + x / (2 * y * f0)) / (4 * x * x * x); 
                                Assert.assertEquals(f3, f.getPartialDerivative(3), FastMath.abs(epsilon * f3));
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
                        double a = FastMath.cos(y) + FastMath.tan(z);
                        double f0 = FastMath.sin(x / a);
                        Assert.assertEquals(f0, f.getValue(), FastMath.abs(epsilon * f0));
                        if (f.getOrder() > 0) {
                            double dfdx = FastMath.cos(x / a) / a;
                            Assert.assertEquals(dfdx, f.getPartialDerivative(1, 0, 0), FastMath.abs(epsilon * dfdx));
                            double dfdy =  x * FastMath.sin(y) * dfdx / a;
                            Assert.assertEquals(dfdy, f.getPartialDerivative(0, 1, 0), FastMath.abs(epsilon * dfdy));
                            double cz = FastMath.cos(z);
                            double cz2 = cz * cz;
                            double dfdz = -x * dfdx / (a * cz2);
                            Assert.assertEquals(dfdz, f.getPartialDerivative(0, 0, 1), FastMath.abs(epsilon * dfdz));
                            if (f.getOrder() > 1) {
                                double df2dx2 = -(f0 / (a * a));
                                Assert.assertEquals(df2dx2, f.getPartialDerivative(2, 0, 0), FastMath.abs(epsilon * df2dx2));
                                double df2dy2 = x * FastMath.cos(y) * dfdx / a -
                                                x * x * FastMath.sin(y) * FastMath.sin(y) * f0 / (a * a * a * a) +
                                                2 * FastMath.sin(y) * dfdy / a;
                                Assert.assertEquals(df2dy2, f.getPartialDerivative(0, 2, 0), FastMath.abs(epsilon * df2dy2));
                                double c4 = cz2 * cz2;
                                double df2dz2 = x * (2 * a * (1 - a * cz * FastMath.sin(z)) * dfdx - x * f0 / a ) / (a * a * a * c4);
                                Assert.assertEquals(df2dz2, f.getPartialDerivative(0, 0, 2), FastMath.abs(epsilon * df2dz2));
                                double df2dxdy = dfdy / x  - x * FastMath.sin(y) * f0 / (a * a * a);
                                Assert.assertEquals(df2dxdy, f.getPartialDerivative(1, 1, 0), FastMath.abs(epsilon * df2dxdy));
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
    public void testExp() {
        double[] epsilon = new double[] { 1.0e-16, 1.0e-16, 1.0e-16, 1.0e-16, 1.0e-16 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                double refExp = FastMath.exp(x);
                DerivativeStructure exp = new DerivativeStructure(1, maxOrder, 0, x).exp();
                for (int n = 0; n <= maxOrder; ++n) {
                    Assert.assertEquals(refExp, exp.getPartialDerivative(n), epsilon[n]);
                }
            }
        }
    }

    @Test
    public void testLog() {
        double[] epsilon = new double[] { 1.0e-16, 1.0e-16, 3.0e-14, 7.0e-13, 3.0e-11 };
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure log = new DerivativeStructure(1, maxOrder, 0, x).log();
                Assert.assertEquals(FastMath.log(x), log.getValue(), epsilon[0]);
                for (int n = 1; n <= maxOrder; ++n) {
                    double refDer = -ArithmeticUtils.factorial(n - 1) / FastMath.pow(-x, n);
                    Assert.assertEquals(refDer, log.getPartialDerivative(n), epsilon[n]);
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
    public void testSinCos() {
        double epsilon = 5.0e-16;
        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.001) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
                DerivativeStructure sin = dsX.sin();
                DerivativeStructure cos = dsX.cos();
                double s = FastMath.sin(x);
                double c = FastMath.cos(x);
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

    @Test
    public void testCompositionOneVariableY() {
        double epsilon = 1.0e-13;
        for (int maxOrder = 0; maxOrder < 5; ++maxOrder) {
            for (double x = 0.1; x < 1.2; x += 0.1) {
                DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, x);
                for (double y = 0.1; y < 1.2; y += 0.1) {
                    DerivativeStructure dsY = new DerivativeStructure(1, maxOrder, 0, y);
                    DerivativeStructure f = dsX.divide(dsY).sqrt();
                    double f0 = FastMath.sqrt(x / y);
                    Assert.assertEquals(f0, f.getValue(), FastMath.abs(epsilon * f0));
                    if (f.getOrder() > 0) {
                        double f1 = -x / (2 * y * y * f0);
                        Assert.assertEquals(f1, f.getPartialDerivative(1), FastMath.abs(epsilon * f1));
                        if (f.getOrder() > 1) {
                            double f2 = (f0 - x / (4 * y * f0)) / (y * y); 
                            Assert.assertEquals(f2, f.getPartialDerivative(2), FastMath.abs(epsilon * f2));
                            if (f.getOrder() > 2) {
                                double f3 = (x / (8 * y * f0) - 2 * f0) / (y * y * y); 
                                Assert.assertEquals(f3, f.getPartialDerivative(3), FastMath.abs(epsilon * f3));
                            }
                        }
                    }
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
