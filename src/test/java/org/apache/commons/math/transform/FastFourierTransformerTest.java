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
package org.apache.commons.math.transform;

import java.util.Random;

import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateFunction;
import org.apache.commons.math.analysis.function.Sin;
import org.apache.commons.math.analysis.function.Sinc;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for fast Fourier transformer.
 * <p>
 * FFT algorithm is exact, the small tolerance number is used only
 * to account for round-off errors.
 *
 * @version $Id$
 */
public final class FastFourierTransformerTest {
    /** The common seed of all random number generators used in this test. */
    private final static long SEED = 20110111L;

    /*
     * Precondition checks for standard transform.
     */

    @Test(expected = MathIllegalArgumentException.class)
    public void testStandardTransformComplexSizeNotAPowerOfTwo() {
        final int n = 127;
        final Complex[] x = createComplexData(n);
        final FastFourierTransformer fft = FastFourierTransformer.create();
        fft.transform(x);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testStandardTransformRealSizeNotAPowerOfTwo() {
        final int n = 127;
        final double[] x = createRealData(n);
        final FastFourierTransformer fft = FastFourierTransformer.create();
        fft.transform(x);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testStandardTransformFunctionSizeNotAPowerOfTwo() {
        final int n = 127;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.create();
        fft.transform(f, 0.0, Math.PI, n);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testStandardTransformFunctionNotStrictlyPositiveNumberOfSamples() {
        final int n = -128;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.create();
        fft.transform(f, 0.0, Math.PI, n);
    }

    @Test(expected = NumberIsTooLargeException.class)
    public void testStandardTransformFunctionInvalidBounds() {
        final int n = 128;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.create();
        fft.transform(f, Math.PI, 0.0, n);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testStandardInverseTransformComplexSizeNotAPowerOfTwo() {
        final int n = 127;
        final Complex[] x = createComplexData(n);
        final FastFourierTransformer fft = FastFourierTransformer.create();
        fft.inverseTransform(x);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testStandardInverseTransformRealSizeNotAPowerOfTwo() {
        final int n = 127;
        final double[] x = createRealData(n);
        final FastFourierTransformer fft = FastFourierTransformer.create();
        fft.inverseTransform(x);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testStandardInverseTransformFunctionSizeNotAPowerOfTwo() {
        final int n = 127;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.create();
        fft.inverseTransform(f, 0.0, Math.PI, n);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testStandardInverseTransformFunctionNotStrictlyPositiveNumberOfSamples() {
        final int n = -128;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.create();
        fft.inverseTransform(f, 0.0, Math.PI, n);
    }

    @Test(expected = NumberIsTooLargeException.class)
    public void testStandardInverseTransformFunctionInvalidBounds() {
        final int n = 128;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.create();
        fft.transform(f, Math.PI, 0.0, n);
    }

    /*
     * Precondition checks for unitary transform.
     */

    @Test(expected = MathIllegalArgumentException.class)
    public void testUnitaryTransformComplexSizeNotAPowerOfTwo() {
        final int n = 127;
        final Complex[] x = createComplexData(n);
        final FastFourierTransformer fft = FastFourierTransformer.createUnitary();
        fft.transform(x);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testUnitaryTransformRealSizeNotAPowerOfTwo() {
        final int n = 127;
        final double[] x = createRealData(n);
        final FastFourierTransformer fft = FastFourierTransformer.createUnitary();
        fft.transform(x);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testUnitaryTransformFunctionSizeNotAPowerOfTwo() {
        final int n = 127;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.createUnitary();
        fft.transform(f, 0.0, Math.PI, n);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testUnitaryTransformFunctionNotStrictlyPositiveNumberOfSamples() {
        final int n = -128;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.createUnitary();
        fft.transform(f, 0.0, Math.PI, n);
    }

    @Test(expected = NumberIsTooLargeException.class)
    public void testUnitaryTransformFunctionInvalidBounds() {
        final int n = 128;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.createUnitary();
        fft.transform(f, Math.PI, 0.0, n);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testUnitaryInverseTransformComplexSizeNotAPowerOfTwo() {
        final int n = 127;
        final Complex[] x = createComplexData(n);
        final FastFourierTransformer fft = FastFourierTransformer.createUnitary();
        fft.inverseTransform(x);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testUnitaryInverseTransformRealSizeNotAPowerOfTwo() {
        final int n = 127;
        final double[] x = createRealData(n);
        final FastFourierTransformer fft = FastFourierTransformer.createUnitary();
        fft.inverseTransform(x);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testUnitaryInverseTransformFunctionSizeNotAPowerOfTwo() {
        final int n = 127;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.createUnitary();
        fft.inverseTransform(f, 0.0, Math.PI, n);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testUnitaryInverseTransformFunctionNotStrictlyPositiveNumberOfSamples() {
        final int n = -128;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.createUnitary();
        fft.inverseTransform(f, 0.0, Math.PI, n);
    }

    @Test(expected = NumberIsTooLargeException.class)
    public void testUnitaryInverseTransformFunctionInvalidBounds() {
        final int n = 128;
        final UnivariateFunction f = new Sin();
        final FastFourierTransformer fft = FastFourierTransformer.createUnitary();
        fft.transform(f, Math.PI, 0.0, n);
    }

    /*
     * Utility methods for checking (successful) transforms.
     */

    private static Complex[] createComplexData(final int n) {
        final Random random = new Random(SEED);
        final Complex[] data = new Complex[n];
        for (int i = 0; i < n; i++) {
            final double re = 2.0 * random.nextDouble() - 1.0;
            final double im = 2.0 * random.nextDouble() - 1.0;
            data[i] = new Complex(re, im);
        }
        return data;
    }

    private static double[] createRealData(final int n) {
        final Random random = new Random(SEED);
        final double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = 2.0 * random.nextDouble() - 1.0;
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
            final double arg = 2.0 * FastMath.PI * i / n;
            cos[i] = FastMath.cos(arg);
            sin[i] = FastMath.sin(arg);
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
            y[i] = new Complex(yr, yi);
        }
        return y;
    }

    private static void doTestTransformComplex(final int n, final double tol,
        final boolean forward, final boolean standard) {
        final FastFourierTransformer fft;
        if (standard) {
            fft = FastFourierTransformer.create();
        } else {
            fft = FastFourierTransformer.createUnitary();
        }
        final Complex[] x = createComplexData(n);
        final Complex[] expected;
        final Complex[] actual;
        final double s;
        if (forward) {
            expected = dft(x, -1);
            s = standard ? 1.0 : 1.0 / FastMath.sqrt(n);
            actual = fft.transform(x);
        } else {
            expected = dft(x, 1);
            s = standard ? 1.0 / n : 1.0 / FastMath.sqrt(n);
            actual = fft.inverseTransform(x);
        }
        for (int i = 0; i < n; i++) {
            final String msg = String.format("%d, %d", n, i);
            final double re = s * expected[i].getReal();
            Assert.assertEquals(msg, re, actual[i].getReal(),
                tol * FastMath.abs(re));
            final double im = s * expected[i].getImaginary();
            Assert.assertEquals(msg, im, actual[i].getImaginary(), tol *
                FastMath.abs(re));
        }
    }

    private static void doTestTransformReal(final int n, final double tol,
        final boolean forward, final boolean standard) {
        final FastFourierTransformer fft;
        if (standard) {
            fft = FastFourierTransformer.create();
        } else {
            fft = FastFourierTransformer.createUnitary();
        }
        final double[] x = createRealData(n);
        final Complex[] xc = new Complex[n];
        for (int i = 0; i < n; i++) {
            xc[i] = new Complex(x[i], 0.0);
        }
        final Complex[] expected;
        final Complex[] actual;
        final double s;
        if (forward) {
            expected = dft(xc, -1);
            s = standard ? 1.0 : 1.0 / FastMath.sqrt(n);
            actual = fft.transform(x);
        } else {
            expected = dft(xc, 1);
            s = standard ? 1.0 / n : 1.0 / FastMath.sqrt(n);
            actual = fft.inverseTransform(x);
        }
        for (int i = 0; i < n; i++) {
            final String msg = String.format("%d, %d", n, i);
            final double re = s * expected[i].getReal();
            Assert.assertEquals(msg, re, actual[i].getReal(),
                tol * FastMath.abs(re));
            final double im = s * expected[i].getImaginary();
            Assert.assertEquals(msg, im, actual[i].getImaginary(), tol *
                FastMath.abs(re));
        }
    }

    private static void doTestTransformFunction(final UnivariateFunction f,
        final double min, final double max, int n, final double tol,
        final boolean forward, final boolean standard) {
        final FastFourierTransformer fft;
        if (standard) {
            fft = FastFourierTransformer.create();
        } else {
            fft = FastFourierTransformer.createUnitary();
        }
        final Complex[] x = new Complex[n];
        for (int i = 0; i < n; i++) {
            final double t = min + i * (max - min) / n;
            x[i] = new Complex(f.value(t));
        }
        final Complex[] expected;
        final Complex[] actual;
        final double s;
        if (forward) {
            expected = dft(x, -1);
            s = standard ? 1.0 : 1.0 / FastMath.sqrt(n);
            actual = fft.transform(f, min, max, n);
        } else {
            expected = dft(x, 1);
            s = standard ? 1.0 / n : 1.0 / FastMath.sqrt(n);
            actual = fft.inverseTransform(f, min, max, n);
        }
        for (int i = 0; i < n; i++) {
            final String msg = String.format("%d, %d", n, i);
            final double re = s * expected[i].getReal();
            Assert.assertEquals(msg, re, actual[i].getReal(),
                tol * FastMath.abs(re));
            final double im = s * expected[i].getImaginary();
            Assert.assertEquals(msg, im, actual[i].getImaginary(), tol *
                FastMath.abs(re));
        }
    }

    /*
     * Tests of standard transform (when data is valid).
     */

    @Test
    public void testStandardTransformComplex() {
        final boolean forward = true;
        final boolean standard = true;
        doTestTransformComplex(2, 1.0E-15, forward, standard);
        doTestTransformComplex(4, 1.0E-14, forward, standard);
        doTestTransformComplex(8, 1.0E-14, forward, standard);
        doTestTransformComplex(16, 1.0E-13, forward, standard);
        doTestTransformComplex(32, 1.0E-13, forward, standard);
        doTestTransformComplex(64, 1.0E-13, forward, standard);
        doTestTransformComplex(128, 1.0E-12, forward, standard);
    }

    @Test
    public void testStandardTransformReal() {
        final boolean forward = true;
        final boolean standard = true;
        doTestTransformReal(2, 1.0E-15, forward, standard);
        doTestTransformReal(4, 1.0E-14, forward, standard);
        doTestTransformReal(8, 1.0E-14, forward, standard);
        doTestTransformReal(16, 1.0E-13, forward, standard);
        doTestTransformReal(32, 1.0E-13, forward, standard);
        doTestTransformReal(64, 1.0E-13, forward, standard);
        doTestTransformReal(128, 1.0E-11, forward, standard);
    }

    @Test
    public void testStandardTransformFunction() {
        final UnivariateFunction f = new Sinc();
        final double min = -FastMath.PI;
        final double max = FastMath.PI;
        final boolean forward = true;
        final boolean standard = true;
        doTestTransformFunction(f, min, max, 2, 1.0E-15, forward, standard);
        doTestTransformFunction(f, min, max, 4, 1.0E-14, forward, standard);
        doTestTransformFunction(f, min, max, 8, 1.0E-14, forward, standard);
        doTestTransformFunction(f, min, max, 16, 1.0E-13, forward, standard);
        doTestTransformFunction(f, min, max, 32, 1.0E-13, forward, standard);
        doTestTransformFunction(f, min, max, 64, 1.0E-12, forward, standard);
        doTestTransformFunction(f, min, max, 128, 1.0E-11, forward, standard);
    }

    @Test
    public void testStandardInverseTransformComplex() {
        final boolean forward = false;
        final boolean standard = true;
        doTestTransformComplex(2, 1.0E-15, forward, standard);
        doTestTransformComplex(4, 1.0E-14, forward, standard);
        doTestTransformComplex(8, 1.0E-14, forward, standard);
        doTestTransformComplex(16, 1.0E-13, forward, standard);
        doTestTransformComplex(32, 1.0E-13, forward, standard);
        doTestTransformComplex(64, 1.0E-12, forward, standard);
        doTestTransformComplex(128, 1.0E-12, forward, standard);
    }

    @Test
    public void testStandardInverseTransformReal() {
        final boolean forward = false;
        final boolean standard = true;
        doTestTransformReal(2, 1.0E-15, forward, standard);
        doTestTransformReal(4, 1.0E-14, forward, standard);
        doTestTransformReal(8, 1.0E-14, forward, standard);
        doTestTransformReal(16, 1.0E-13, forward, standard);
        doTestTransformReal(32, 1.0E-13, forward, standard);
        doTestTransformReal(64, 1.0E-12, forward, standard);
        doTestTransformReal(128, 1.0E-11, forward, standard);
    }

    @Test
    public void testStandardInverseTransformFunction() {
        final UnivariateFunction f = new Sinc();
        final double min = -FastMath.PI;
        final double max = FastMath.PI;
        final boolean forward = false;
        final boolean standard = true;
        doTestTransformFunction(f, min, max, 2, 1.0E-15, forward, standard);
        doTestTransformFunction(f, min, max, 4, 1.0E-14, forward, standard);
        doTestTransformFunction(f, min, max, 8, 1.0E-14, forward, standard);
        doTestTransformFunction(f, min, max, 16, 1.0E-13, forward, standard);
        doTestTransformFunction(f, min, max, 32, 1.0E-13, forward, standard);
        doTestTransformFunction(f, min, max, 64, 1.0E-12, forward, standard);
        doTestTransformFunction(f, min, max, 128, 1.0E-11, forward, standard);
    }

    /*
     * Tests of unitary transform (when data is valid).
     */

    @Test
    public void testUnitaryTransformComplex() {
        final boolean forward = true;
        final boolean standard = false;
        doTestTransformComplex(2, 1.0E-15, forward, standard);
        doTestTransformComplex(4, 1.0E-14, forward, standard);
        doTestTransformComplex(8, 1.0E-14, forward, standard);
        doTestTransformComplex(16, 1.0E-13, forward, standard);
        doTestTransformComplex(32, 1.0E-13, forward, standard);
        doTestTransformComplex(64, 1.0E-13, forward, standard);
        doTestTransformComplex(128, 1.0E-12, forward, standard);
    }

    @Test
    public void testUnitaryTransformReal() {
        final boolean forward = true;
        final boolean standard = false;
        doTestTransformReal(2, 1.0E-15, forward, standard);
        doTestTransformReal(4, 1.0E-14, forward, standard);
        doTestTransformReal(8, 1.0E-14, forward, standard);
        doTestTransformReal(16, 1.0E-13, forward, standard);
        doTestTransformReal(32, 1.0E-13, forward, standard);
        doTestTransformReal(64, 1.0E-13, forward, standard);
        doTestTransformReal(128, 1.0E-11, forward, standard);
    }

    @Test
    public void testUnitaryTransformFunction() {
        final UnivariateFunction f = new Sinc();
        final double min = -FastMath.PI;
        final double max = FastMath.PI;
        final boolean forward = true;
        final boolean standard = false;
        doTestTransformFunction(f, min, max, 2, 1.0E-15, forward, standard);
        doTestTransformFunction(f, min, max, 4, 1.0E-14, forward, standard);
        doTestTransformFunction(f, min, max, 8, 1.0E-14, forward, standard);
        doTestTransformFunction(f, min, max, 16, 1.0E-13, forward, standard);
        doTestTransformFunction(f, min, max, 32, 1.0E-13, forward, standard);
        doTestTransformFunction(f, min, max, 64, 1.0E-12, forward, standard);
        doTestTransformFunction(f, min, max, 128, 1.0E-11, forward, standard);
    }

    @Test
    public void testUnitaryInverseTransformComplex() {
        final boolean forward = false;
        final boolean standard = false;
        doTestTransformComplex(2, 1.0E-15, forward, standard);
        doTestTransformComplex(4, 1.0E-14, forward, standard);
        doTestTransformComplex(8, 1.0E-14, forward, standard);
        doTestTransformComplex(16, 1.0E-13, forward, standard);
        doTestTransformComplex(32, 1.0E-13, forward, standard);
        doTestTransformComplex(64, 1.0E-12, forward, standard);
        doTestTransformComplex(128, 1.0E-12, forward, standard);
    }

    @Test
    public void testUnitaryInverseTransformReal() {
        final boolean forward = false;
        final boolean standard = false;
        doTestTransformReal(2, 1.0E-15, forward, standard);
        doTestTransformReal(4, 1.0E-14, forward, standard);
        doTestTransformReal(8, 1.0E-14, forward, standard);
        doTestTransformReal(16, 1.0E-13, forward, standard);
        doTestTransformReal(32, 1.0E-13, forward, standard);
        doTestTransformReal(64, 1.0E-12, forward, standard);
        doTestTransformReal(128, 1.0E-11, forward, standard);
    }

    @Test
    public void testUnitaryInverseTransformFunction() {
        final UnivariateFunction f = new Sinc();
        final double min = -FastMath.PI;
        final double max = FastMath.PI;
        final boolean forward = false;
        final boolean standard = false;
        doTestTransformFunction(f, min, max, 2, 1.0E-15, forward, standard);
        doTestTransformFunction(f, min, max, 4, 1.0E-14, forward, standard);
        doTestTransformFunction(f, min, max, 8, 1.0E-14, forward, standard);
        doTestTransformFunction(f, min, max, 16, 1.0E-13, forward, standard);
        doTestTransformFunction(f, min, max, 32, 1.0E-13, forward, standard);
        doTestTransformFunction(f, min, max, 64, 1.0E-12, forward, standard);
        doTestTransformFunction(f, min, max, 128, 1.0E-11, forward, standard);
    }

    /*
     * Additional tests for 1D data.
     */

    /**
     * Test of transformer for the ad hoc data taken from Mathematica.
     */
    @Test
    public void testAdHocData() {
        FastFourierTransformer transformer = FastFourierTransformer.create();
        Complex result[]; double tolerance = 1E-12;

        double x[] = {1.3, 2.4, 1.7, 4.1, 2.9, 1.7, 5.1, 2.7};
        Complex y[] = {
            new Complex(21.9, 0.0),
            new Complex(-2.09497474683058, 1.91507575950825),
            new Complex(-2.6, 2.7),
            new Complex(-1.10502525316942, -4.88492424049175),
            new Complex(0.1, 0.0),
            new Complex(-1.10502525316942, 4.88492424049175),
            new Complex(-2.6, -2.7),
            new Complex(-2.09497474683058, -1.91507575950825)};

        result = transformer.transform(x);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(y[i].getReal(), result[i].getReal(), tolerance);
            Assert.assertEquals(y[i].getImaginary(), result[i].getImaginary(), tolerance);
        }

        result = transformer.inverseTransform(y);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(x[i], result[i].getReal(), tolerance);
            Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        double x2[] = {10.4, 21.6, 40.8, 13.6, 23.2, 32.8, 13.6, 19.2};
        TransformUtils.scaleArray(x2, 1.0 / FastMath.sqrt(x2.length));
        Complex y2[] = y;

        transformer = FastFourierTransformer.createUnitary();
        result = transformer.transform(y2);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(x2[i], result[i].getReal(), tolerance);
            Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        result = transformer.inverseTransform(x2);
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
        UnivariateFunction f = new SinFunction();
        FastFourierTransformer transformer = FastFourierTransformer.create();
        Complex result[]; int N = 1 << 8;
        double min, max, tolerance = 1E-12;

        min = 0.0; max = 2.0 * FastMath.PI;
        result = transformer.transform(f, min, max, N);
        Assert.assertEquals(0.0, result[1].getReal(), tolerance);
        Assert.assertEquals(-(N >> 1), result[1].getImaginary(), tolerance);
        Assert.assertEquals(0.0, result[N-1].getReal(), tolerance);
        Assert.assertEquals(N >> 1, result[N-1].getImaginary(), tolerance);
        for (int i = 0; i < N-1; i += (i == 0 ? 2 : 1)) {
            Assert.assertEquals(0.0, result[i].getReal(), tolerance);
            Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        min = -FastMath.PI; max = FastMath.PI;
        result = transformer.inverseTransform(f, min, max, N);
        Assert.assertEquals(0.0, result[1].getReal(), tolerance);
        Assert.assertEquals(-0.5, result[1].getImaginary(), tolerance);
        Assert.assertEquals(0.0, result[N-1].getReal(), tolerance);
        Assert.assertEquals(0.5, result[N-1].getImaginary(), tolerance);
        for (int i = 0; i < N-1; i += (i == 0 ? 2 : 1)) {
            Assert.assertEquals(0.0, result[i].getReal(), tolerance);
            Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
        }
    }

    /*
     * Additional tests for 2D data.
     */

    @Test
    public void test2DData() {
        FastFourierTransformer transformer = FastFourierTransformer.create();
        double tolerance = 1E-12;
        Complex[][] input = new Complex[][] {new Complex[] {new Complex(1, 0),
                                                            new Complex(2, 0)},
                                             new Complex[] {new Complex(3, 1),
                                                            new Complex(4, 2)}};
        Complex[][] goodOutput = new Complex[][] {new Complex[] {new Complex(5,
                1.5), new Complex(-1, -.5)}, new Complex[] {new Complex(-2,
                -1.5), new Complex(0, .5)}};
        for (int i = 0; i < goodOutput.length; i++) {
            TransformUtils.scaleArray(
                goodOutput[i],
                FastMath.sqrt(goodOutput[i].length) *
                    FastMath.sqrt(goodOutput.length));
        }
        Complex[][] output = (Complex[][])transformer.mdfft(input, true);
        Complex[][] output2 = (Complex[][])transformer.mdfft(output, false);

        Assert.assertEquals(input.length, output.length);
        Assert.assertEquals(input.length, output2.length);
        Assert.assertEquals(input[0].length, output[0].length);
        Assert.assertEquals(input[0].length, output2[0].length);
        Assert.assertEquals(input[1].length, output[1].length);
        Assert.assertEquals(input[1].length, output2[1].length);

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                Assert.assertEquals(input[i][j].getImaginary(), output2[i][j].getImaginary(),
                             tolerance);
                Assert.assertEquals(input[i][j].getReal(), output2[i][j].getReal(), tolerance);
                Assert.assertEquals(goodOutput[i][j].getImaginary(), output[i][j].getImaginary(),
                             tolerance);
                Assert.assertEquals(goodOutput[i][j].getReal(), output[i][j].getReal(), tolerance);
            }
        }
    }

    @Test
    public void test2DDataUnitary() {
        FastFourierTransformer transformer = FastFourierTransformer.createUnitary();
        double tolerance = 1E-12;
        Complex[][] input = new Complex[][] {new Complex[] {new Complex(1, 0),
                                                            new Complex(2, 0)},
                                             new Complex[] {new Complex(3, 1),
                                                            new Complex(4, 2)}};
        Complex[][] goodOutput = new Complex[][] {new Complex[] {new Complex(5,
                1.5), new Complex(-1, -.5)}, new Complex[] {new Complex(-2,
                -1.5), new Complex(0, .5)}};
        Complex[][] output = (Complex[][])transformer.mdfft(input, true);
        Complex[][] output2 = (Complex[][])transformer.mdfft(output, false);

        Assert.assertEquals(input.length, output.length);
        Assert.assertEquals(input.length, output2.length);
        Assert.assertEquals(input[0].length, output[0].length);
        Assert.assertEquals(input[0].length, output2[0].length);
        Assert.assertEquals(input[1].length, output[1].length);
        Assert.assertEquals(input[1].length, output2[1].length);

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                Assert.assertEquals(input[i][j].getImaginary(), output2[i][j].getImaginary(),
                             tolerance);
                Assert.assertEquals(input[i][j].getReal(), output2[i][j].getReal(), tolerance);
                Assert.assertEquals(goodOutput[i][j].getImaginary(), output[i][j].getImaginary(),
                             tolerance);
                Assert.assertEquals(goodOutput[i][j].getReal(), output[i][j].getReal(), tolerance);
            }
        }
    }

}
