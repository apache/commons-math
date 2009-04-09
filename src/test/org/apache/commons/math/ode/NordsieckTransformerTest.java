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

import java.math.BigInteger;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.fraction.BigFraction;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;

public class NordsieckTransformerTest
extends TestCase {

    public NordsieckTransformerTest(String name) {
        super(name);
    }

    public void testDimension2() {
        NordsieckTransformer transformer = new NordsieckTransformer(2);
        double[] nordsieckHistory = new double[] { 1.0,  2.0 };
        double[] mwdHistory       = new double[] { 1.0, -1.0 };
        double[] multistepHistory = new double[] { 1.0,  2.0 };
        checkVector(nordsieckHistory, transformer.multistepWithoutDerivativesToNordsieck(mwdHistory));
        checkVector(mwdHistory, transformer.nordsieckToMultistepWithoutDerivatives(nordsieckHistory));
        checkVector(nordsieckHistory, transformer.multistepToNordsieck(multistepHistory));
        checkVector(multistepHistory, transformer.nordsieckToMultistep(nordsieckHistory));
    }

    public void testDimension3() {
        NordsieckTransformer transformer = new NordsieckTransformer(3);
        double[] nordsieckHistory = new double[] { 1.0,  4.0, 18.0 };
        double[] mwdHistory       = new double[] { 1.0, 15.0, 65.0 };
        double[] multistepHistory = new double[] { 1.0,  4.0, 15.0 };
        checkVector(nordsieckHistory, transformer.multistepWithoutDerivativesToNordsieck(mwdHistory));
        checkVector(mwdHistory, transformer.nordsieckToMultistepWithoutDerivatives(nordsieckHistory));
        checkVector(nordsieckHistory, transformer.multistepToNordsieck(multistepHistory));
        checkVector(multistepHistory, transformer.nordsieckToMultistep(nordsieckHistory));
    }

    public void testDimension7() {
        NordsieckTransformer transformer = new NordsieckTransformer(7);
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
        RealMatrix mwdHistory       =
            new RealMatrixImpl(new double[][] {
                                   {     1,     2,     3 },
                                   {     4,     3,     6 },
                                   {    25,    60,   127 },
                                   {   340,   683,  1362 },
                                   {  2329,  3918,  7635 },
                                   { 10036, 15147, 29278 },
                                   { 32449, 45608, 87951 }
                               }, false);
        RealMatrix multistepHistory =
            new RealMatrixImpl(new double[][] {
                                   {     1,     2,     3 },
                                   {    -2,     1,     0 },
                                   {     4,     3,     6 },
                                   {    25,    60,   127 },
                                   {   340,   683,  1362 },
                                   {  2329,  3918,  7635 },
                                   { 10036, 15147, 29278 }
                               }, false);

        RealMatrix m = transformer.multistepWithoutDerivativesToNordsieck(mwdHistory);
        assertEquals(0.0, m.subtract(nordsieckHistory).getNorm(), 1.0e-11);
        m = transformer.nordsieckToMultistepWithoutDerivatives(nordsieckHistory);
        assertEquals(0.0, m.subtract(mwdHistory).getNorm(), 1.0e-11);
        m = transformer.multistepToNordsieck(multistepHistory);
        assertEquals(0.0, m.subtract(nordsieckHistory).getNorm(), 1.0e-11);
        m = transformer.nordsieckToMultistep(nordsieckHistory);
        assertEquals(0.0, m.subtract(multistepHistory).getNorm(), 1.0e-11);

    }

    public void testInverseWithoutDerivatives() {
        for (int n = 1; n < 20; ++n) {
            BigInteger[][] nTom =
                NordsieckTransformer.buildNordsieckToMultistepWithoutDerivatives(n);
            BigFraction[][] mTon =
                NordsieckTransformer.buildMultistepWithoutDerivativesToNordsieck(n);
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    BigFraction s = BigFraction.ZERO;
                    for (int k = 0; k < n; ++k) {
                        s = s.add(mTon[i][k].multiply(nTom[k][j]));
                    }
                    assertEquals((i == j) ? BigFraction.ONE : BigFraction.ZERO, s);
                }
            }
        }
    }

    public void testInverse() {
        for (int n = 1; n < 20; ++n) {
            BigInteger[][] nTom =
                NordsieckTransformer.buildNordsieckToMultistep(n);
            BigFraction[][] mTon =
                NordsieckTransformer.buildMultistepToNordsieck(n);
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    BigFraction s = BigFraction.ZERO;
                    for (int k = 0; k < n; ++k) {
                        s = s.add(mTon[i][k].multiply(nTom[k][j]));
                    }
                    assertEquals((i == j) ? BigFraction.ONE : BigFraction.ZERO, s);
                }
            }
        }
    }

    public void testMatrices1() {
        checkMatrix(1, new int[][] { { 1 } },
                    NordsieckTransformer.buildMultistepWithoutDerivativesToNordsieck(1));
        checkMatrix(new int[][] { { 1 } },
                    NordsieckTransformer.buildNordsieckToMultistepWithoutDerivatives(1));
        checkMatrix(1, new int[][] { { 1 } },
                    NordsieckTransformer.buildMultistepToNordsieck(1));
        checkMatrix(new int[][] { { 1 } },
                    NordsieckTransformer.buildNordsieckToMultistep(1));
    }

    public void testMatrices2() {
        checkMatrix(1, new int[][] { { 1, 0 }, { 1, -1 } },
                    NordsieckTransformer.buildMultistepWithoutDerivativesToNordsieck(2));
        checkMatrix(new int[][] { { 1, 0 }, { 1, -1 } },
                    NordsieckTransformer.buildNordsieckToMultistepWithoutDerivatives(2));
        checkMatrix(1, new int[][] { { 1, 0 }, { 0, 1 } },
                    NordsieckTransformer.buildMultistepToNordsieck(2));
        checkMatrix(new int[][] { { 1, 0 }, { 0, 1 } },
                    NordsieckTransformer.buildNordsieckToMultistep(2));
    }

    public void testMatrices3() {
        checkMatrix(2, new int[][] { { 2, 0, 0 }, { 3, -4, 1 }, { 1, -2, 1 } },
                    NordsieckTransformer.buildMultistepWithoutDerivativesToNordsieck(3));
        checkMatrix(new int[][] { { 1, 0, 0 }, { 1, -1, 1 }, { 1, -2, 4 } },
                    NordsieckTransformer.buildNordsieckToMultistepWithoutDerivatives(3));
        checkMatrix(1, new int[][] { { 1, 0, 0 }, { 0, 1, 0 }, { -1, 1, 1} },
                    NordsieckTransformer.buildMultistepToNordsieck(3));
        checkMatrix(new int[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 1, -1, 1 } },
                    NordsieckTransformer.buildNordsieckToMultistep(3));
    }

    public void testMatrices4() {
        checkMatrix(6, new int[][] { { 6, 0, 0, 0 }, { 11, -18, 9, -2 }, { 6, -15, 12, -3 }, { 1, -3, 3, -1 } },
                    NordsieckTransformer.buildMultistepWithoutDerivativesToNordsieck(4));
        checkMatrix(new int[][] { { 1, 0, 0, 0 }, { 1, -1, 1, -1 }, { 1, -2, 4, -8 }, { 1, -3, 9, -27 } },
                    NordsieckTransformer.buildNordsieckToMultistepWithoutDerivatives(4));
        checkMatrix(4, new int[][] { { 4, 0, 0, 0 }, { 0, 4, 0, 0 }, { -7, 6, 8, -1 }, { -3, 2, 4, -1 } },
                    NordsieckTransformer.buildMultistepToNordsieck(4));
        checkMatrix(new int[][] { { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 1, -1, 1, -1 }, { 1, -2, 4, -8 } },
                    NordsieckTransformer.buildNordsieckToMultistep(4));
    }

    public void testPolynomial() {
        Random r = new Random(1847222905841997856l);
        for (int n = 2; n < 9; ++n) {

            // build a polynomial and its derivatives
            double[] coeffs = new double[n + 1];
            for (int i = 0; i < n; ++i) {
                coeffs[i] = 2 * r.nextDouble() - 1.0;
            }
            PolynomialFunction[] polynomials = new PolynomialFunction[n];
            polynomials[0] = new PolynomialFunction(coeffs);
            for (int k = 1; k < polynomials.length; ++k) {
                polynomials[k] = (PolynomialFunction) polynomials[k - 1].derivative();
            }
            double h = 0.01;

            // build a state history in multistep form
            double[] multistepHistory = new double[n];
            multistepHistory[0] = polynomials[0].value(1.0);
            multistepHistory[1] = h * polynomials[1].value(1.0);
            for (int i = 2; i < multistepHistory.length; ++i) {
                multistepHistory[i] = polynomials[0].value(1.0 - (i - 1) * h);
            }

            // build the same state history in multistep without derivatives form
            double[] mwdHistory = new double[n];
            for (int i = 0; i < multistepHistory.length; ++i) {
                mwdHistory[i] = polynomials[0].value(1.0 - i * h);
            }

            // build the same state history in Nordsieck form
            double[] nordsieckHistory = new double[n];
            double scale = 1.0;
            for (int i = 0; i < nordsieckHistory.length; ++i) {
                nordsieckHistory[i] = scale * polynomials[i].value(1.0);
                scale *= h / (i + 1);
            }

            // check the transform is exact for these polynomials states
            NordsieckTransformer transformer = new NordsieckTransformer(n);
            checkVector(nordsieckHistory, transformer.multistepWithoutDerivativesToNordsieck(mwdHistory));
            checkVector(mwdHistory,       transformer.nordsieckToMultistepWithoutDerivatives(nordsieckHistory));
            checkVector(nordsieckHistory, transformer.multistepToNordsieck(multistepHistory));
            checkVector(multistepHistory, transformer.nordsieckToMultistep(nordsieckHistory));

        }
    }

    private void checkVector(double[] reference, double[] candidate) {
        assertEquals(reference.length, candidate.length);
        for (int i = 0; i < reference.length; ++i) {
            assertEquals(reference[i], candidate[i], 1.0e-14);
        }
    }

    private void checkMatrix(int[][] reference, BigInteger[][] candidate) {
        assertEquals(reference.length, candidate.length);
        for (int i = 0; i < reference.length; ++i) {
            int[] rRow = reference[i];
            BigInteger[] cRow = candidate[i];
            assertEquals(rRow.length, cRow.length);
            for (int j = 0; j < rRow.length; ++j) {
                assertEquals(rRow[j], cRow[j].intValue());
            }
        }
    }

    private void checkMatrix(int denominator, int[][] reference, BigFraction[][] candidate) {
        assertEquals(reference.length, candidate.length);
        for (int i = 0; i < reference.length; ++i) {
            int[] rRow = reference[i];
            BigFraction[] cRow = candidate[i];
            assertEquals(rRow.length, cRow.length);
            for (int j = 0; j < rRow.length; ++j) {
                assertEquals(new BigFraction(rRow[j], denominator), cRow[j]);
            }
        }
    }

    public static Test suite() {
        return new TestSuite(NordsieckTransformerTest.class);
      }

}
