/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.commons.math.transform;

import org.apache.commons.math.analysis.*;
import org.apache.commons.math.complex.*;
import org.apache.commons.math.MathException;
import junit.framework.TestCase;

/**
 * Testcase for fast Fourier transformer.
 * <p>
 * FFT algorithm is exact, the small tolerance number is used only
 * to account for round-off errors.
 * 
 * @version $Revision$ $Date$ 
 */
public final class FastFourierTransformerTest extends TestCase {

    /**
     * Test of transformer for the ad hoc data taken from Mathematica.
     */
    public void testAdHocData() throws MathException {
        FastFourierTransformer transformer = new FastFourierTransformer();
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
            assertEquals(y[i].getReal(), result[i].getReal(), tolerance);
            assertEquals(y[i].getImaginary(), result[i].getImaginary(), tolerance);
        }

        result = transformer.inversetransform(y);
        for (int i = 0; i < result.length; i++) {
            assertEquals(x[i], result[i].getReal(), tolerance);
            assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        double x2[] = {10.4, 21.6, 40.8, 13.6, 23.2, 32.8, 13.6, 19.2};
        transformer.scaleArray(x2, 1.0 / Math.sqrt(x2.length));
        Complex y2[] = y;

        result = transformer.transform2(y2);
        for (int i = 0; i < result.length; i++) {
            assertEquals(x2[i], result[i].getReal(), tolerance);
            assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        result = transformer.inversetransform2(x2);
        for (int i = 0; i < result.length; i++) {
            assertEquals(y2[i].getReal(), result[i].getReal(), tolerance);
            assertEquals(y2[i].getImaginary(), result[i].getImaginary(), tolerance);
        }
    }

    /**
     * Test of transformer for the sine function.
     */
    public void testSinFunction() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        FastFourierTransformer transformer = new FastFourierTransformer();
        Complex result[]; int N = 1 << 8;
        double min, max, tolerance = 1E-12;

        min = 0.0; max = 2.0 * Math.PI;
        result = transformer.transform(f, min, max, N);
        assertEquals(0.0, result[1].getReal(), tolerance);
        assertEquals(-(N >> 1), result[1].getImaginary(), tolerance);
        assertEquals(0.0, result[N-1].getReal(), tolerance);
        assertEquals(N >> 1, result[N-1].getImaginary(), tolerance);
        for (int i = 0; i < N-1; i += (i == 0 ? 2 : 1)) {
            assertEquals(0.0, result[i].getReal(), tolerance);
            assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        min = -Math.PI; max = Math.PI;
        result = transformer.inversetransform(f, min, max, N);
        assertEquals(0.0, result[1].getReal(), tolerance);
        assertEquals(-0.5, result[1].getImaginary(), tolerance);
        assertEquals(0.0, result[N-1].getReal(), tolerance);
        assertEquals(0.5, result[N-1].getImaginary(), tolerance);
        for (int i = 0; i < N-1; i += (i == 0 ? 2 : 1)) {
            assertEquals(0.0, result[i].getReal(), tolerance);
            assertEquals(0.0, result[i].getImaginary(), tolerance);
        }
    }

    /**
     * Test of parameters for the transformer.
     */
    public void testParameters() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        FastFourierTransformer transformer = new FastFourierTransformer();

        try {
            // bad interval
            transformer.transform(f, 1, -1, 64);
            fail("Expecting IllegalArgumentException - bad interval");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.transform(f, -1, 1, 0);
            fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.transform(f, -1, 1, 100);
            fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}
