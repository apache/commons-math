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
package org.apache.commons.math4.legacy.analysis.interpolation;

import java.util.Random;

import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math4.legacy.core.dfp.Dfp;
import org.apache.commons.math4.legacy.core.dfp.DfpField;
import org.apache.commons.math4.legacy.linear.Dfp25;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NoDataException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

public class FieldHermiteInterpolatorTest {

    @Test
    public void testZero() {
        FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<>();
        interpolator.addSamplePoint(Dfp25.of(0), new Dfp[] { Dfp25.of(0) });
        for (int x = -10; x < 10; x++) {
            Dfp y = interpolator.value(Dfp25.of(x))[0];
            Assert.assertEquals(Dfp25.ZERO, y);
            Dfp[][] derivatives = interpolator.derivatives(Dfp25.of(x), 1);
            Assert.assertEquals(Dfp25.ZERO, derivatives[0][0]);
            Assert.assertEquals(Dfp25.ZERO, derivatives[1][0]);
        }
    }

    @Test
    public void testQuadratic() {
        FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<>();
        interpolator.addSamplePoint(Dfp25.of(0), new Dfp[] { Dfp25.of(2) });
        interpolator.addSamplePoint(Dfp25.of(1), new Dfp[] { Dfp25.of(0) });
        interpolator.addSamplePoint(Dfp25.of(2), new Dfp[] { Dfp25.of(0) });
        for (double x = -10; x < 10; x += 1.0) {
            Dfp y = interpolator.value(Dfp25.of(x))[0];
            Assert.assertEquals((x - 1) * (x - 2), y.toDouble(), 1.0e-15);
            Dfp[][] derivatives = interpolator.derivatives(Dfp25.of(x), 3);
            Assert.assertEquals((x - 1) * (x - 2), derivatives[0][0].toDouble(), 1.0e-15);
            Assert.assertEquals(2 * x - 3, derivatives[1][0].toDouble(), 1.0e-15);
            Assert.assertEquals(2, derivatives[2][0].toDouble(), 1.0e-15);
            Assert.assertEquals(0, derivatives[3][0].toDouble(), 1.0e-15);
        }
    }

    @Test
    public void testMixedDerivatives() {
        FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<>();
        interpolator.addSamplePoint(Dfp25.of(0), new Dfp[] { Dfp25.of(1) }, new Dfp[] { Dfp25.of(2) });
        interpolator.addSamplePoint(Dfp25.of(1), new Dfp[] { Dfp25.of(4) });
        interpolator.addSamplePoint(Dfp25.of(2), new Dfp[] { Dfp25.of(5) }, new Dfp[] { Dfp25.of(2) });
        Dfp[][] derivatives = interpolator.derivatives(Dfp25.of(0), 5);
        Assert.assertEquals(Dfp25.of(  1), derivatives[0][0]);
        Assert.assertEquals(Dfp25.of(  2), derivatives[1][0]);
        Assert.assertEquals(Dfp25.of(  8), derivatives[2][0]);
        Assert.assertEquals(Dfp25.of(-24), derivatives[3][0]);
        Assert.assertEquals(Dfp25.of( 24), derivatives[4][0]);
        Assert.assertEquals(Dfp25.of(  0), derivatives[5][0]);
        derivatives = interpolator.derivatives(Dfp25.of(1), 5);
        Assert.assertEquals(Dfp25.of(  4), derivatives[0][0]);
        Assert.assertEquals(Dfp25.of(  2), derivatives[1][0]);
        Assert.assertEquals(Dfp25.of( -4), derivatives[2][0]);
        Assert.assertEquals(Dfp25.of(  0), derivatives[3][0]);
        Assert.assertEquals(Dfp25.of( 24), derivatives[4][0]);
        Assert.assertEquals(Dfp25.of(  0), derivatives[5][0]);
        derivatives = interpolator.derivatives(Dfp25.of(2), 5);
        Assert.assertEquals(Dfp25.of(  5), derivatives[0][0]);
        Assert.assertEquals(Dfp25.of(  2), derivatives[1][0]);
        Assert.assertEquals(Dfp25.of(  8), derivatives[2][0]);
        Assert.assertEquals(Dfp25.of( 24), derivatives[3][0]);
        Assert.assertEquals(Dfp25.of( 24), derivatives[4][0]);
        Assert.assertEquals(Dfp25.of(  0), derivatives[5][0]);
    }

    @Test
    public void testRandomPolynomialsValuesOnly() {

        Random random = new Random(0x42b1e7dbd361a932L);

        for (int i = 0; i < 100; ++i) {

            int maxDegree = 0;
            PolynomialFunction[] p = new PolynomialFunction[5];
            for (int k = 0; k < p.length; ++k) {
                int degree = random.nextInt(7);
                p[k] = randomPolynomial(degree, random);
                maxDegree = JdkMath.max(maxDegree, degree);
            }

            DfpField field = new DfpField(30);
            Dfp step = field.getOne().divide(field.newDfp(10));
            FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<>();
            for (int j = 0; j < 1 + maxDegree; ++j) {
                Dfp x = field.newDfp(j).multiply(step);
                Dfp[] values = new Dfp[p.length];
                for (int k = 0; k < p.length; ++k) {
                    values[k] = field.newDfp(p[k].value(x.getReal()));
                }
                interpolator.addSamplePoint(x, values);
            }

            for (int j = 0; j < 20; ++j) {
                Dfp x = field.newDfp(j).multiply(step);
                Dfp[] values = interpolator.value(x);
                Assert.assertEquals(p.length, values.length);
                for (int k = 0; k < p.length; ++k) {
                    Assert.assertEquals(p[k].value(x.getReal()),
                                        values[k].getReal(),
                                        1.0e-8 * JdkMath.abs(p[k].value(x.getReal())));
                }
            }

        }

    }

    @Test
    public void testRandomPolynomialsFirstDerivative() {

        Random random = new Random(0x570803c982ca5d3bL);

        for (int i = 0; i < 100; ++i) {

            int maxDegree = 0;
            PolynomialFunction[] p      = new PolynomialFunction[5];
            PolynomialFunction[] pPrime = new PolynomialFunction[5];
            for (int k = 0; k < p.length; ++k) {
                int degree = random.nextInt(7);
                p[k]      = randomPolynomial(degree, random);
                pPrime[k] = p[k].polynomialDerivative();
                maxDegree = JdkMath.max(maxDegree, degree);
            }

            DfpField field = new DfpField(30);
            Dfp step = field.getOne().divide(field.newDfp(10));
            FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<>();
            for (int j = 0; j < 1 + maxDegree / 2; ++j) {
                Dfp x = field.newDfp(j).multiply(step);
                Dfp[] values      = new Dfp[p.length];
                Dfp[] derivatives = new Dfp[p.length];
                for (int k = 0; k < p.length; ++k) {
                    values[k]      = field.newDfp(p[k].value(x.getReal()));
                    derivatives[k] = field.newDfp(pPrime[k].value(x.getReal()));
                }
                interpolator.addSamplePoint(x, values, derivatives);
            }

            Dfp h = step.divide(field.newDfp(100000));
            for (int j = 0; j < 20; ++j) {
                Dfp x = field.newDfp(j).multiply(step);
                Dfp[] y  = interpolator.value(x);
                Dfp[] yP = interpolator.value(x.add(h));
                Dfp[] yM = interpolator.value(x.subtract(h));
                Assert.assertEquals(p.length, y.length);
                for (int k = 0; k < p.length; ++k) {
                    Assert.assertEquals(p[k].value(x.getReal()),
                                        y[k].getReal(),
                                        1.0e-8 * JdkMath.abs(p[k].value(x.getReal())));
                    Assert.assertEquals(pPrime[k].value(x.getReal()),
                                        yP[k].subtract(yM[k]).divide(h.multiply(2)).getReal(),
                                        4.0e-8 * JdkMath.abs(p[k].value(x.getReal())));
                }
            }

        }
    }

    @Test
    public void testSine() {
        DfpField field = new DfpField(30);
        FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<>();
        for (Dfp x = field.getZero(); x.getReal() < JdkMath.PI; x = x.add(0.5)) {
            interpolator.addSamplePoint(x, new Dfp[] { x.sin() });
        }
        for (Dfp x = field.newDfp(0.1); x.getReal() < 2.9; x = x.add(0.01)) {
            Dfp y = interpolator.value(x)[0];
            Assert.assertEquals( x.sin().getReal(), y.getReal(), 3.5e-5);
        }
    }

    @Test
    public void testSquareRoot() {
        DfpField field = new DfpField(30);
        FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<>();
        for (Dfp x = field.getOne(); x.getReal() < 3.6; x = x.add(0.5)) {
            interpolator.addSamplePoint(x, new Dfp[] { x.sqrt() });
        }
        for (Dfp x = field.newDfp(1.1); x.getReal() < 3.5; x = x.add(0.01)) {
            Dfp y = interpolator.value(x)[0];
            Assert.assertEquals(x.sqrt().getReal(), y.getReal(), 1.5e-4);
        }
    }

    @Test
    public void testWikipedia() {
        // this test corresponds to the example from Wikipedia page:
        // http://en.wikipedia.org/wiki/Hermite_interpolation
        FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<>();
        interpolator.addSamplePoint(Dfp25.of(-1),
                                    new Dfp[] { Dfp25.of( 2) },
                                    new Dfp[] { Dfp25.of(-8) },
                                    new Dfp[] { Dfp25.of(56) });
        interpolator.addSamplePoint(Dfp25.of( 0),
                                    new Dfp[] { Dfp25.of( 1) },
                                    new Dfp[] { Dfp25.of( 0) },
                                    new Dfp[] { Dfp25.of( 0) });
        interpolator.addSamplePoint(Dfp25.of( 1),
                                    new Dfp[] { Dfp25.of( 2) },
                                    new Dfp[] { Dfp25.of( 8) },
                                    new Dfp[] { Dfp25.of(56) });
        for (Dfp x = Dfp25.of(-1); x.toDouble() <= 1.0; x = x.add(Dfp25.of(1, 8))) {
            Dfp y = interpolator.value(x)[0];
            Dfp x2 = x.multiply(x);
            Dfp x4 = x2.multiply(x2);
            Dfp x8 = x4.multiply(x4);
            Assert.assertEquals(x8.add(Dfp25.of(1)), y);
        }
    }

    @Test
    public void testOnePointParabola() {
        FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<>();
        interpolator.addSamplePoint(Dfp25.of(0),
                                    new Dfp[] { Dfp25.of(1) },
                                    new Dfp[] { Dfp25.of(1) },
                                    new Dfp[] { Dfp25.of(2) });
        for (Dfp x = Dfp25.of(-1); x.toDouble() <= 1.0; x = x.add(Dfp25.of(1, 8))) {
            Dfp y = interpolator.value(x)[0];
            Assert.assertEquals(Dfp25.ONE.add(x.multiply(Dfp25.ONE.add(x))), y);
        }
    }

    private PolynomialFunction randomPolynomial(int degree, Random random) {
        double[] coeff = new double[ 1 + degree];
        for (int j = 0; j < degree; ++j) {
            coeff[j] = random.nextDouble();
        }
        return new PolynomialFunction(coeff);
    }

    @Test(expected=NoDataException.class)
    public void testEmptySampleValue() {
        new FieldHermiteInterpolator<Dfp>().value(Dfp25.ZERO);
    }

    @Test(expected=NoDataException.class)
    public void testEmptySampleDerivative() {
        new FieldHermiteInterpolator<Dfp>().derivatives(Dfp25.ZERO, 1);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testDuplicatedAbscissa() {
        FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<>();
        interpolator.addSamplePoint(Dfp25.of(1), new Dfp[] { Dfp25.of(0) });
        interpolator.addSamplePoint(Dfp25.of(1), new Dfp[] { Dfp25.of(1) });
    }

}

