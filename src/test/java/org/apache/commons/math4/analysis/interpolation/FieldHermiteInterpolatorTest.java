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
package org.apache.commons.math4.analysis.interpolation;

import java.util.Random;

import org.apache.commons.math4.analysis.interpolation.FieldHermiteInterpolator;
import org.apache.commons.math4.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math4.dfp.Dfp;
import org.apache.commons.math4.dfp.DfpField;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.NoDataException;
import org.apache.commons.math4.fraction.BigFraction;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class FieldHermiteInterpolatorTest {

    @Test
    public void testZero() {
        FieldHermiteInterpolator<BigFraction> interpolator = new FieldHermiteInterpolator<BigFraction>();
        interpolator.addSamplePoint(new BigFraction(0), new BigFraction[] { new BigFraction(0) });
        for (int x = -10; x < 10; x++) {
            BigFraction y = interpolator.value(new BigFraction(x))[0];
            Assert.assertEquals(BigFraction.ZERO, y);
            BigFraction[][] derivatives = interpolator.derivatives(new BigFraction(x), 1);
            Assert.assertEquals(BigFraction.ZERO, derivatives[0][0]);
            Assert.assertEquals(BigFraction.ZERO, derivatives[1][0]);
        }
    }

    @Test
    public void testQuadratic() {
        FieldHermiteInterpolator<BigFraction> interpolator = new FieldHermiteInterpolator<BigFraction>();
        interpolator.addSamplePoint(new BigFraction(0), new BigFraction[] { new BigFraction(2) });
        interpolator.addSamplePoint(new BigFraction(1), new BigFraction[] { new BigFraction(0) });
        interpolator.addSamplePoint(new BigFraction(2), new BigFraction[] { new BigFraction(0) });
        for (double x = -10; x < 10; x += 1.0) {
            BigFraction y = interpolator.value(new BigFraction(x))[0];
            Assert.assertEquals((x - 1) * (x - 2), y.doubleValue(), 1.0e-15);
            BigFraction[][] derivatives = interpolator.derivatives(new BigFraction(x), 3);
            Assert.assertEquals((x - 1) * (x - 2), derivatives[0][0].doubleValue(), 1.0e-15);
            Assert.assertEquals(2 * x - 3, derivatives[1][0].doubleValue(), 1.0e-15);
            Assert.assertEquals(2, derivatives[2][0].doubleValue(), 1.0e-15);
            Assert.assertEquals(0, derivatives[3][0].doubleValue(), 1.0e-15);
        }
    }

    @Test
    public void testMixedDerivatives() {
        FieldHermiteInterpolator<BigFraction> interpolator = new FieldHermiteInterpolator<BigFraction>();
        interpolator.addSamplePoint(new BigFraction(0), new BigFraction[] { new BigFraction(1) }, new BigFraction[] { new BigFraction(2) });
        interpolator.addSamplePoint(new BigFraction(1), new BigFraction[] { new BigFraction(4) });
        interpolator.addSamplePoint(new BigFraction(2), new BigFraction[] { new BigFraction(5) }, new BigFraction[] { new BigFraction(2) });
        BigFraction[][] derivatives = interpolator.derivatives(new BigFraction(0), 5);
        Assert.assertEquals(new BigFraction(  1), derivatives[0][0]);
        Assert.assertEquals(new BigFraction(  2), derivatives[1][0]);
        Assert.assertEquals(new BigFraction(  8), derivatives[2][0]);
        Assert.assertEquals(new BigFraction(-24), derivatives[3][0]);
        Assert.assertEquals(new BigFraction( 24), derivatives[4][0]);
        Assert.assertEquals(new BigFraction(  0), derivatives[5][0]);
        derivatives = interpolator.derivatives(new BigFraction(1), 5);
        Assert.assertEquals(new BigFraction(  4), derivatives[0][0]);
        Assert.assertEquals(new BigFraction(  2), derivatives[1][0]);
        Assert.assertEquals(new BigFraction( -4), derivatives[2][0]);
        Assert.assertEquals(new BigFraction(  0), derivatives[3][0]);
        Assert.assertEquals(new BigFraction( 24), derivatives[4][0]);
        Assert.assertEquals(new BigFraction(  0), derivatives[5][0]);
        derivatives = interpolator.derivatives(new BigFraction(2), 5);
        Assert.assertEquals(new BigFraction(  5), derivatives[0][0]);
        Assert.assertEquals(new BigFraction(  2), derivatives[1][0]);
        Assert.assertEquals(new BigFraction(  8), derivatives[2][0]);
        Assert.assertEquals(new BigFraction( 24), derivatives[3][0]);
        Assert.assertEquals(new BigFraction( 24), derivatives[4][0]);
        Assert.assertEquals(new BigFraction(  0), derivatives[5][0]);
    }

    @Test
    public void testRandomPolynomialsValuesOnly() {

        Random random = new Random(0x42b1e7dbd361a932l);

        for (int i = 0; i < 100; ++i) {

            int maxDegree = 0;
            PolynomialFunction[] p = new PolynomialFunction[5];
            for (int k = 0; k < p.length; ++k) {
                int degree = random.nextInt(7);
                p[k] = randomPolynomial(degree, random);
                maxDegree = FastMath.max(maxDegree, degree);
            }

            DfpField field = new DfpField(30);
            Dfp step = field.getOne().divide(field.newDfp(10));
            FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<Dfp>();
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
                                        1.0e-8 * FastMath.abs(p[k].value(x.getReal())));
                }
            }

        }

    }

    @Test
    public void testRandomPolynomialsFirstDerivative() {

        Random random = new Random(0x570803c982ca5d3bl);

        for (int i = 0; i < 100; ++i) {

            int maxDegree = 0;
            PolynomialFunction[] p      = new PolynomialFunction[5];
            PolynomialFunction[] pPrime = new PolynomialFunction[5];
            for (int k = 0; k < p.length; ++k) {
                int degree = random.nextInt(7);
                p[k]      = randomPolynomial(degree, random);
                pPrime[k] = p[k].polynomialDerivative();
                maxDegree = FastMath.max(maxDegree, degree);
            }

            DfpField field = new DfpField(30);
            Dfp step = field.getOne().divide(field.newDfp(10));
            FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<Dfp>();
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
                                        1.0e-8 * FastMath.abs(p[k].value(x.getReal())));
                    Assert.assertEquals(pPrime[k].value(x.getReal()),
                                        yP[k].subtract(yM[k]).divide(h.multiply(2)).getReal(),
                                        4.0e-8 * FastMath.abs(p[k].value(x.getReal())));
                }
            }

        }
    }

    @Test
    public void testSine() {
        DfpField field = new DfpField(30);
        FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<Dfp>();
        for (Dfp x = field.getZero(); x.getReal() < FastMath.PI; x = x.add(0.5)) {
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
        FieldHermiteInterpolator<Dfp> interpolator = new FieldHermiteInterpolator<Dfp>();
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
        FieldHermiteInterpolator<BigFraction> interpolator = new FieldHermiteInterpolator<BigFraction>();
        interpolator.addSamplePoint(new BigFraction(-1),
                                    new BigFraction[] { new BigFraction( 2) },
                                    new BigFraction[] { new BigFraction(-8) },
                                    new BigFraction[] { new BigFraction(56) });
        interpolator.addSamplePoint(new BigFraction( 0),
                                    new BigFraction[] { new BigFraction( 1) },
                                    new BigFraction[] { new BigFraction( 0) },
                                    new BigFraction[] { new BigFraction( 0) });
        interpolator.addSamplePoint(new BigFraction( 1),
                                    new BigFraction[] { new BigFraction( 2) },
                                    new BigFraction[] { new BigFraction( 8) },
                                    new BigFraction[] { new BigFraction(56) });
        for (BigFraction x = new BigFraction(-1); x.doubleValue() <= 1.0; x = x.add(new BigFraction(1, 8))) {
            BigFraction y = interpolator.value(x)[0];
            BigFraction x2 = x.multiply(x);
            BigFraction x4 = x2.multiply(x2);
            BigFraction x8 = x4.multiply(x4);
            Assert.assertEquals(x8.add(new BigFraction(1)), y);
        }
    }

    @Test
    public void testOnePointParabola() {
        FieldHermiteInterpolator<BigFraction> interpolator = new FieldHermiteInterpolator<BigFraction>();
        interpolator.addSamplePoint(new BigFraction(0),
                                    new BigFraction[] { new BigFraction(1) },
                                    new BigFraction[] { new BigFraction(1) },
                                    new BigFraction[] { new BigFraction(2) });
        for (BigFraction x = new BigFraction(-1); x.doubleValue() <= 1.0; x = x.add(new BigFraction(1, 8))) {
            BigFraction y = interpolator.value(x)[0];
            Assert.assertEquals(BigFraction.ONE.add(x.multiply(BigFraction.ONE.add(x))), y);
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
        new FieldHermiteInterpolator<BigFraction>().value(BigFraction.ZERO);
    }

    @Test(expected=NoDataException.class)
    public void testEmptySampleDerivative() {
        new FieldHermiteInterpolator<BigFraction>().derivatives(BigFraction.ZERO, 1);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testDuplicatedAbscissa() {
        FieldHermiteInterpolator<BigFraction> interpolator = new FieldHermiteInterpolator<BigFraction>();
        interpolator.addSamplePoint(new BigFraction(1), new BigFraction[] { new BigFraction(0) });
        interpolator.addSamplePoint(new BigFraction(1), new BigFraction[] { new BigFraction(1) });
    }

}

