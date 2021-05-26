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
package org.apache.commons.math4.transform;

import java.util.function.DoubleUnaryOperator;

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.numbers.complex.Complex;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.Sinc;

/**
 * Test case for {@link FastFourierTransform}.
 * <p>
 * FFT algorithm is exact, the small tolerance number is used only
 * to account for round-off errors.
 */
public final class FastFourierTransformerTest {
    private static final Sin SIN_FUNCTION = new Sin();
    private static final DoubleUnaryOperator SIN = (x) -> SIN_FUNCTION.value(x);

    /** RNG. */
    private static final UniformRandomProvider RNG = RandomSource.create(RandomSource.MWC_256);

    // Precondition checks.

    @Test
    public void testTransformComplexSizeNotAPowerOfTwo() {
        final int n = 127;
        final Complex[] x = createComplexData(n);
        final FastFourierTransform.Norm[] norm = FastFourierTransform.Norm.values();
        for (int i = 0; i < norm.length; i++) {
            for (boolean type : new boolean[] { true, false }) {
                final FastFourierTransform fft = new FastFourierTransform(norm[i], type);
                try {
                    fft.apply(x);
                    Assert.fail(norm[i] + ", " + type +
                        ": IllegalArgumentException was expected");
                } catch (IllegalArgumentException e) {
                    // Expected behaviour
                }
            }
        }
    }

    @Test
    public void testTransformRealSizeNotAPowerOfTwo() {
        final int n = 127;
        final double[] x = createRealData(n);
        final FastFourierTransform.Norm[] norm = FastFourierTransform.Norm.values();
        for (int i = 0; i < norm.length; i++) {
            for (boolean type : new boolean[] { true, false }) {
                final FastFourierTransform fft = new FastFourierTransform(norm[i], type);
                try {
                    fft.apply(x);
                    Assert.fail(norm[i] + ", " + type +
                        ": IllegalArgumentException was expected");
                } catch (IllegalArgumentException e) {
                    // Expected behaviour
                }
            }
        }
    }

    @Test
    public void testTransformFunctionSizeNotAPowerOfTwo() {
        final int n = 127;
        final FastFourierTransform.Norm[] norm = FastFourierTransform.Norm.values();
        for (int i = 0; i < norm.length; i++) {
            for (boolean type : new boolean[] { true, false }) {
                final FastFourierTransform fft = new FastFourierTransform(norm[i], type);
                try {
                    fft.apply(SIN, 0.0, Math.PI, n);
                    Assert.fail(norm[i] + ", " + type +
                        ": IllegalArgumentException was expected");
                } catch (IllegalArgumentException e) {
                    // Expected behaviour
                }
            }
        }
    }

    @Test
    public void testTransformFunctionNotStrictlyPositiveNumberOfSamples() {
        final int n = -128;
        final FastFourierTransform.Norm[] norm = FastFourierTransform.Norm.values();
        for (int i = 0; i < norm.length; i++) {
            for (boolean type : new boolean[] { true, false }) {
                final FastFourierTransform fft = new FastFourierTransform(norm[i], type);
                try {
                    fft.apply(SIN, 0.0, Math.PI, n);
                    fft.apply(SIN, 0.0, Math.PI, n);
                    Assert.fail(norm[i] + ", " + type +
                        ": IllegalArgumentException was expected");
                } catch (IllegalArgumentException e) {
                    // Expected behaviour
                }
            }
        }
    }

    @Test
    public void testTransformFunctionInvalidBounds() {
        final int n = 128;
        final FastFourierTransform.Norm[] norm = FastFourierTransform.Norm.values();
        for (int i = 0; i < norm.length; i++) {
            for (boolean type : new boolean[] { true, false }) {
                final FastFourierTransform fft = new FastFourierTransform(norm[i], type);
                try {
                    fft.apply(SIN, Math.PI, 0.0, n);
                    Assert.fail(norm[i] + ", " + type +
                        ": IllegalArgumentException was expected");
                } catch (IllegalArgumentException e) {
                    // Expected behaviour
                }
            }
        }
    }

    // Utility methods for checking (successful) transforms.

    private static Complex[] createComplexData(final int n) {
        final Complex[] data = new Complex[n];
        for (int i = 0; i < n; i++) {
            final double re = 2 * RNG.nextDouble() - 1;
            final double im = 2 * RNG.nextDouble() - 1;
            data[i] = Complex.ofCartesian(re, im);
        }
        return data;
    }

    private static double[] createRealData(final int n) {
        final double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = 2 * RNG.nextDouble() - 1;
        }
        return data;
    }

    /** Naive implementation of DFT, for reference. */
    private static Complex[] dft(final Complex[] x, final int sgn) {
        final int n = x.length;
        final double[] cos = new double[n];
        final double[] sin = new double[n];
        final Complex[] y = new Complex[n];
        for (int i = 0; i < n; i++) {
            final double arg = 2.0 * Math.PI * i / n;
            cos[i] = Math.cos(arg);
            sin[i] = Math.sin(arg);
        }
        for (int i = 0; i < n; i++) {
            double yr = 0.0;
            double yi = 0.0;
            for (int j = 0; j < n; j++) {
                final int index = (i * j) % n;
                final double c = cos[index];
                final double s = sin[index];
                final double xr = x[j].getReal();
                final double xi = x[j].getImaginary();
                yr += c * xr - sgn * s * xi;
                yi += sgn * s * xr + c * xi;
            }
            y[i] = Complex.ofCartesian(yr, yi);
        }
        return y;
    }

    private static void doTestTransformComplex(final int n,
                                               final double tol,
                                               final FastFourierTransform.Norm normalization,
                                               boolean inverse) {
        final FastFourierTransform fft = new FastFourierTransform(normalization, inverse);
        final Complex[] x = createComplexData(n);
        final Complex[] expected;
        final double s;
        if (!inverse) {
            expected = dft(x, -1);
            if (normalization == FastFourierTransform.Norm.STD){
                s = 1.0;
            } else {
                s = 1.0 / Math.sqrt(n);
            }
        } else {
            expected = dft(x, 1);
            if (normalization == FastFourierTransform.Norm.STD) {
                s = 1.0 / n;
            } else {
                s = 1.0 / Math.sqrt(n);
            }
        }
        final Complex[] actual = fft.apply(x);
        for (int i = 0; i < n; i++) {
            final String msg;
            msg = String.format("%s, %s, %d, %d", normalization, inverse, n, i);
            final double re = s * expected[i].getReal();
            Assert.assertEquals(msg, re, actual[i].getReal(),
                                tol * Math.abs(re));
            final double im = s * expected[i].getImaginary();
            Assert.assertEquals(msg, im, actual[i].getImaginary(),
                                tol * Math.abs(re));
        }
    }

    private static void doTestTransformReal(final int n,
                                            final double tol,
                                            final FastFourierTransform.Norm normalization,
                                            final boolean inverse) {
        final FastFourierTransform fft = new FastFourierTransform(normalization, inverse);
        final double[] x = createRealData(n);
        final Complex[] xc = new Complex[n];
        for (int i = 0; i < n; i++) {
            xc[i] = Complex.ofCartesian(x[i], 0.0);
        }
        final Complex[] expected;
        final double s;
        if (!inverse) {
            expected = dft(xc, -1);
            if (normalization == FastFourierTransform.Norm.STD) {
                s = 1.0;
            } else {
                s = 1.0 / Math.sqrt(n);
            }
        } else {
            expected = dft(xc, 1);
            if (normalization == FastFourierTransform.Norm.STD) {
                s = 1.0 / n;
            } else {
                s = 1.0 / Math.sqrt(n);
            }
        }
        final Complex[] actual = fft.apply(x);
        for (int i = 0; i < n; i++) {
            final String msg;
            msg = String.format("%s, %s, %d, %d", normalization, inverse, n, i);
            final double re = s * expected[i].getReal();
            Assert.assertEquals(msg, re, actual[i].getReal(),
                                tol * Math.abs(re));
            final double im = s * expected[i].getImaginary();
            Assert.assertEquals(msg, im, actual[i].getImaginary(),
                                tol * Math.abs(re));
        }
    }

    private static void doTestTransformFunction(final DoubleUnaryOperator f,
                                                final double min,
                                                final double max,
                                                int n,
                                                final double tol,
                                                final FastFourierTransform.Norm normalization,
                                                final boolean inverse) {
        final FastFourierTransform fft = new FastFourierTransform(normalization, inverse);
        final Complex[] x = new Complex[n];
        for (int i = 0; i < n; i++) {
            final double t = min + i * (max - min) / n;
            x[i] = Complex.ofCartesian(f.applyAsDouble(t), 0);
        }
        final Complex[] expected;
        final double s;
        if (!inverse) {
            expected = dft(x, -1);
            if (normalization == FastFourierTransform.Norm.STD) {
                s = 1.0;
            } else {
                s = 1.0 / Math.sqrt(n);
            }
        } else {
            expected = dft(x, 1);
            if (normalization == FastFourierTransform.Norm.STD) {
                s = 1.0 / n;
            } else {
                s = 1.0 / Math.sqrt(n);
            }
        }
        final Complex[] actual = fft.apply(f, min, max, n);
        for (int i = 0; i < n; i++) {
            final String msg = String.format("%d, %d", n, i);
            final double re = s * expected[i].getReal();
            Assert.assertEquals(msg, re, actual[i].getReal(),
                                tol * Math.abs(re));
            final double im = s * expected[i].getImaginary();
            Assert.assertEquals(msg, im, actual[i].getImaginary(),
                                tol * Math.abs(re));
        }
    }

    // Tests of standard transform (when data is valid).

    @Test
    public void testTransformComplex() {
        final FastFourierTransform.Norm[] norm = FastFourierTransform.Norm.values();
        for (int i = 0; i < norm.length; i++) {
            for (boolean type : new boolean[] { true, false }) {
                doTestTransformComplex(2, 1e-15, norm[i], type);
                doTestTransformComplex(4, 1e-14, norm[i], type);
                doTestTransformComplex(8, 1e-13, norm[i], type);
                doTestTransformComplex(16, 1e-13, norm[i], type);
                doTestTransformComplex(32, 1e-12, norm[i], type);
                doTestTransformComplex(64, 1e-11, norm[i], type);
                doTestTransformComplex(128, 1e-11, norm[i], type);
            }
        }
    }

    @Test
    public void testStandardTransformReal() {
        final FastFourierTransform.Norm[] norm = FastFourierTransform.Norm.values();
        for (int i = 0; i < norm.length; i++) {
            for (boolean type : new boolean[] { true, false }) {
                doTestTransformReal(2, 1e-15, norm[i], type);
                doTestTransformReal(4, 1e-14, norm[i], type);
                doTestTransformReal(8, 1e-13, norm[i], type);
                doTestTransformReal(16, 1e-13, norm[i], type);
                doTestTransformReal(32, 1e-12, norm[i], type);
                doTestTransformReal(64, 1e-12, norm[i], type);
                doTestTransformReal(128, 1e-11, norm[i], type);
            }
        }
    }

    @Test
    public void testStandardTransformFunction() {
        final UnivariateFunction sinc = new Sinc();
        final DoubleUnaryOperator f = (x) -> sinc.value(x);

        final double min = -Math.PI;
        final double max = Math.PI;
        final FastFourierTransform.Norm[] norm = FastFourierTransform.Norm.values();

        for (int i = 0; i < norm.length; i++) {
            for (boolean type : new boolean[] { true, false }) {
                doTestTransformFunction(f, min, max, 2, 1e-15, norm[i], type);
                doTestTransformFunction(f, min, max, 4, 1e-14, norm[i], type);
                doTestTransformFunction(f, min, max, 8, 1e-14, norm[i], type);
                doTestTransformFunction(f, min, max, 16, 1e-13, norm[i], type);
                doTestTransformFunction(f, min, max, 32, 1e-13, norm[i], type);
                doTestTransformFunction(f, min, max, 64, 1e-12, norm[i], type);
                doTestTransformFunction(f, min, max, 128, 1e-11, norm[i], type);
            }
        }
    }

    // Additional tests for 1D data.

    /**
     * Test of transformer for the ad hoc data taken from Mathematica.
     */
    @Test
    public void testAdHocData() {
        FastFourierTransform transformer;
        Complex result[]; double tolerance = 1e-12;

        final double x[] = {1.3, 2.4, 1.7, 4.1, 2.9, 1.7, 5.1, 2.7};
        final Complex y[] = {
            Complex.ofCartesian(21.9, 0.0),
            Complex.ofCartesian(-2.09497474683058, 1.91507575950825),
            Complex.ofCartesian(-2.6, 2.7),
            Complex.ofCartesian(-1.10502525316942, -4.88492424049175),
            Complex.ofCartesian(0.1, 0.0),
            Complex.ofCartesian(-1.10502525316942, 4.88492424049175),
            Complex.ofCartesian(-2.6, -2.7),
            Complex.ofCartesian(-2.09497474683058, -1.91507575950825)};

        transformer = new FastFourierTransform(FastFourierTransform.Norm.STD);
        result = transformer.apply(x);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(y[i].getReal(), result[i].getReal(), tolerance);
            Assert.assertEquals(y[i].getImaginary(), result[i].getImaginary(), tolerance);
        }

        transformer = new FastFourierTransform(FastFourierTransform.Norm.STD, true);
        result = transformer.apply(y);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(x[i], result[i].getReal(), tolerance);
            Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        double x2[] = {10.4, 21.6, 40.8, 13.6, 23.2, 32.8, 13.6, 19.2};
        TransformUtils.scaleInPlace(x2, 1.0 / Math.sqrt(x2.length));
        Complex y2[] = y;

        transformer = new FastFourierTransform(FastFourierTransform.Norm.UNIT);
        result = transformer.apply(y2);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(x2[i], result[i].getReal(), tolerance);
            Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        transformer = new FastFourierTransform(FastFourierTransform.Norm.UNIT, true);
        result = transformer.apply(x2);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(y2[i].getReal(), result[i].getReal(), tolerance);
            Assert.assertEquals(y2[i].getImaginary(), result[i].getImaginary(), tolerance);
        }
    }

    /**
     * Test of transformer for the sine function.
     */
    @Test
    public void testSinFunction() {
        FastFourierTransform transformer;
        Complex result[]; int N = 1 << 8;
        double min, max, tolerance = 1e-12;

        min = 0.0;
        max = 2 * Math.PI;
        transformer = new FastFourierTransform(FastFourierTransform.Norm.STD);
        result = transformer.apply(SIN, min, max, N);
        Assert.assertEquals(0.0, result[1].getReal(), tolerance);
        Assert.assertEquals(-(N >> 1), result[1].getImaginary(), tolerance);
        Assert.assertEquals(0.0, result[N-1].getReal(), tolerance);
        Assert.assertEquals(N >> 1, result[N-1].getImaginary(), tolerance);
        for (int i = 0; i < N-1; i += (i == 0 ? 2 : 1)) {
            Assert.assertEquals(0.0, result[i].getReal(), tolerance);
            Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        min = -Math.PI;
        max = Math.PI;
        transformer = new FastFourierTransform(FastFourierTransform.Norm.STD, true);
        result = transformer.apply(SIN, min, max, N);
        Assert.assertEquals(0.0, result[1].getReal(), tolerance);
        Assert.assertEquals(-0.5, result[1].getImaginary(), tolerance);
        Assert.assertEquals(0.0, result[N-1].getReal(), tolerance);
        Assert.assertEquals(0.5, result[N-1].getImaginary(), tolerance);
        for (int i = 0; i < N-1; i += (i == 0 ? 2 : 1)) {
            Assert.assertEquals(0.0, result[i].getReal(), tolerance);
            Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
        }
    }
}
