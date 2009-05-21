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

package org.apache.commons.math.ode;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.fraction.BigFraction;
import org.apache.commons.math.linear.FieldMatrix;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;
import org.junit.Test;

public class NordsieckTransformerTest {

    @Test(expected=InvalidMatrixException.class)
    public void nonInvertible() {
        new NordsieckTransformer(1, 1, 0, 1);
    }

    @Test
    public void dimension2() {
        NordsieckTransformer transformer = new NordsieckTransformer(0, 2, 0, 0);
        double[] nordsieckHistory = new double[] { 1.0,  2.0 };
        double[] multistepHistory = new double[] { 1.0, -1.0 };
        checkVector(nordsieckHistory, transformer.multistepToNordsieck(multistepHistory));
        checkVector(multistepHistory, transformer.nordsieckToMultistep(nordsieckHistory));
    }

    @Test
    public void dimension2Der() {
        NordsieckTransformer transformer = new NordsieckTransformer(0, 1, 0, 1);
        double[] nordsieckHistory = new double[] { 1.0,  2.0 };
        double[] multistepHistory = new double[] { 1.0,  2.0 };
        checkVector(nordsieckHistory, transformer.multistepToNordsieck(multistepHistory));
        checkVector(multistepHistory, transformer.nordsieckToMultistep(nordsieckHistory));
    }

    @Test
    public void dimension3() {
        NordsieckTransformer transformer = new NordsieckTransformer(0, 3, 0, 0);
        double[] nordsieckHistory = new double[] { 1.0,  4.0, 18.0 };
        double[] multistepHistory = new double[] { 1.0, 15.0, 65.0 };
        checkVector(nordsieckHistory, transformer.multistepToNordsieck(multistepHistory));
        checkVector(multistepHistory, transformer.nordsieckToMultistep(nordsieckHistory));
    }

    @Test
    public void dimension3Der() {
        NordsieckTransformer transformer = new NordsieckTransformer(0, 2, 0, 1);
        double[] nordsieckHistory = new double[] { 1.0,  4.0, 18.0 };
        double[] multistepHistory = new double[] { 1.0, 15.0,  4.0 };
        checkVector(nordsieckHistory, transformer.multistepToNordsieck(multistepHistory));
        checkVector(multistepHistory, transformer.nordsieckToMultistep(nordsieckHistory));
    }

    @Test
    public void dimension7() {
        NordsieckTransformer transformer = new NordsieckTransformer(0, 7, 0, 0);
        RealMatrix nordsieckHistory =
            new RealMatrixImpl(new double[][] {
                                   {  1,  2,  3 },
                                   { -2,  1,  0 },
                                   {  1,  1,  1 },
                                   {  0, -1,  1 },
                                   {  1, -1,  2 },
                                   {  2,  0,  1 },
                                   {  1,  1,  2 }
                                }, false);
        RealMatrix multistepHistory =
            new RealMatrixImpl(new double[][] {
                                   {     1,     2,     3 },
                                   {     4,     3,     6 },
                                   {    25,    60,   127 },
                                   {   340,   683,  1362 },
                                   {  2329,  3918,  7635 },
                                   { 10036, 15147, 29278 },
                                   { 32449, 45608, 87951 }
                               }, false);

        RealMatrix m = transformer.multistepToNordsieck(multistepHistory);
        assertEquals(0.0, m.subtract(nordsieckHistory).getNorm(), 1.0e-11);
        m = transformer.nordsieckToMultistep(nordsieckHistory);
        assertEquals(0.0, m.subtract(multistepHistory).getNorm(), 1.0e-11);

    }

    @Test
    public void dimension7Der() {
        NordsieckTransformer transformer = new NordsieckTransformer(0, 6, 0, 1);
        RealMatrix nordsieckHistory =
            new RealMatrixImpl(new double[][] {
                                   {  1,  2,  3 },
                                   { -2,  1,  0 },
                                   {  1,  1,  1 },
                                   {  0, -1,  1 },
                                   {  1, -1,  2 },
                                   {  2,  0,  1 },
                                   {  1,  1,  2 }
                                }, false);
        RealMatrix multistepHistory =
            new RealMatrixImpl(new double[][] {
                                   {     1,     2,     3 },
                                   {     4,     3,     6 },
                                   {    25,    60,   127 },
                                   {   340,   683,  1362 },
                                   {  2329,  3918,  7635 },
                                   { 10036, 15147, 29278 },
                                   {    -2,     1,     0 }
                               }, false);

        RealMatrix m = transformer.multistepToNordsieck(multistepHistory);
        assertEquals(0.0, m.subtract(nordsieckHistory).getNorm(), 1.0e-11);
        m = transformer.nordsieckToMultistep(nordsieckHistory);
        assertEquals(0.0, m.subtract(multistepHistory).getNorm(), 1.0e-11);

    }

    @Test
    public void matrices1() {
        checkMatrix(1, new int[][] { { 1 } },
                    NordsieckTransformer.buildNordsieckToMultistep(0, 1, 0, 0));
    }

    @Test
    public void matrices2() {
        checkMatrix(1, new int[][] { { 1, 0 }, { 1, -1 } },
                    NordsieckTransformer.buildNordsieckToMultistep(0, 2, 0, 0));
    }

    @Test
    public void matrices3() {
        checkMatrix(1, new int[][] { { 1, 0, 0 }, { 1, -1, 1 }, { 1, -2, 4 } },
                    NordsieckTransformer.buildNordsieckToMultistep(0, 3, 0, 0));
    }

    @Test
    public void matrices4() {
        checkMatrix(1,
                    new int[][] {
                         { 1, 0, 0, 0 },
                         { 1, -1, 1, -1 },
                         { 1, -2, 4, -8 },
                         { 1, -3, 9, -27 }
                    }, NordsieckTransformer.buildNordsieckToMultistep(0, 4, 0, 0));
    }

    @Test
    public void adamsBashforth2() {
        checkMatrix(1,
                    new int[][] {
                        { 1, 0,  0 },
                        { 0, 1,  0 },
                        { 0, 1, -2 }
                    }, NordsieckTransformer.buildNordsieckToMultistep(0, 1, 0, 2));
    }

    @Test
    public void adamsBashforth3() {
        checkMatrix(1,
                    new int[][] {
                        { 1, 0,  0,  0 },
                        { 0, 1,  0,  0 },
                        { 0, 1, -2,  3 },
                        { 0, 1, -4, 12 }
                    }, NordsieckTransformer.buildNordsieckToMultistep(0, 1, 0, 3));
    }

    @Test
    public void adamsBashforth4() {
        checkMatrix(1,
                    new int[][] {
                        { 1, 0,  0,  0,    0 },
                        { 0, 1,  0,  0,    0 },
                        { 0, 1, -2,  3,   -4 },
                        { 0, 1, -4, 12,  -32 },
                        { 0, 1, -6, 27, -108 }
                    }, NordsieckTransformer.buildNordsieckToMultistep(0, 1, 0, 4));
    }

    @Test
    public void adamsBashforth5() {
        checkMatrix(1,
                    new int[][] {
                        { 1, 0,  0,  0,    0,    0 },
                        { 0, 1,  0,  0,    0,    0 },
                        { 0, 1, -2,  3,   -4,    5 },
                        { 0, 1, -4, 12,  -32,   80 },
                        { 0, 1, -6, 27, -108,  405 },
                        { 0, 1, -8, 48, -256, 1280 }
                    }, NordsieckTransformer.buildNordsieckToMultistep(0, 1, 0, 5));
    }

    @Test
    public void polynomial() {
        Random random = new Random(1847222905841997856l);
        for (int n = 2; n < 10; ++n) {
            for (int m = 0; m < 10; ++m) {

                // choose p, q, r, s
                int qMinusP = 1 + random.nextInt(n);
                int sMinusR = n - qMinusP;
                int p       = random.nextInt(5) - 2; // possible values: -2, -1, 0, 1, 2
                int q       = p + qMinusP;
                int r       = random.nextInt(5) - 2; // possible values: -2, -1, 0, 1, 2
                int s       = r + sMinusR;

                // build a polynomial and its derivatives
                double[] coeffs = new double[n + 1];
                for (int i = 0; i < n; ++i) {
                    coeffs[i] = 2.0 * random.nextDouble() - 1.0;
                }
                PolynomialFunction[] polynomials = new PolynomialFunction[n];
                polynomials[0] = new PolynomialFunction(coeffs);
                for (int i = 1; i < polynomials.length; ++i) {
                    polynomials[i] = (PolynomialFunction) polynomials[i - 1].derivative();
                }

                double x = 0.75;
                double h = 0.01;

                // build a state history in multistep form
                double[] multistepHistory = new double[n];
                for (int k = p; k < q; ++k) {
                    multistepHistory[k-p] = polynomials[0].value(x - k * h);
                }
                for (int k = r; k < s; ++k) {
                    multistepHistory[k + qMinusP - r] = h * polynomials[1].value(x - k * h);
                }

                // build the same state history in Nordsieck form
                double[] nordsieckHistory = new double[n];
                double scale = 1.0;
                for (int i = 0; i < nordsieckHistory.length; ++i) {
                    nordsieckHistory[i] = scale * polynomials[i].value(x);
                    scale *= h / (i + 1);
                }

                // check the transform is exact for these polynomials states
                NordsieckTransformer transformer = new NordsieckTransformer(p, q, r, s);
                checkVector(nordsieckHistory, transformer.multistepToNordsieck(multistepHistory));
                checkVector(multistepHistory, transformer.nordsieckToMultistep(nordsieckHistory));

            }
        }
    }

    private void checkVector(double[] reference, double[] candidate) {
        assertEquals(reference.length, candidate.length);
        for (int i = 0; i < reference.length; ++i) {
            assertEquals(reference[i], candidate[i], 2.0e-12);
        }
    }

    private void checkMatrix(int denominator, int[][] reference, FieldMatrix<BigFraction> candidate) {
        assertEquals(reference.length, candidate.getRowDimension());
        for (int i = 0; i < reference.length; ++i) {
            int[] rRow = reference[i];
            for (int j = 0; j < rRow.length; ++j) {
                assertEquals(new BigFraction(rRow[j], denominator), candidate.getEntry(i, j));
            }
        }
    }

}
