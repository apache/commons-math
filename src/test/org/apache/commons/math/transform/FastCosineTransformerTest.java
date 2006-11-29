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

import org.apache.commons.math.analysis.*;
import org.apache.commons.math.MathException;
import junit.framework.TestCase;

/**
 * Testcase for fast cosine transformer.
 * <p>
 * FCT algorithm is exact, the small tolerance number is used only
 * to account for round-off errors.
 * 
 * @version $Revision$ $Date$ 
 */
public final class FastCosineTransformerTest extends TestCase {

    /**
     * Test of transformer for the ad hoc data.
     */
    public void testAdHocData() throws MathException {
        FastCosineTransformer transformer = new FastCosineTransformer();
        double result[], tolerance = 1E-12;

        double x[] = { 0.0, 1.0, 4.0, 9.0, 16.0, 25.0, 36.0, 49.0, 64.0 };
        double y[] = { 172.0, -105.096569476353, 27.3137084989848,
                      -12.9593152353742, 8.0, -5.78585076868676,
                       4.68629150101524, -4.15826451958632, 4.0 };

        result = transformer.transform(x);
        for (int i = 0; i < result.length; i++) {
            assertEquals(y[i], result[i], tolerance);
        }

        result = transformer.inversetransform(y);
        for (int i = 0; i < result.length; i++) {
            assertEquals(x[i], result[i], tolerance);
        }

        FastFourierTransformer.scaleArray(x, Math.sqrt(0.5 * (x.length-1)));

        result = transformer.transform2(y);
        for (int i = 0; i < result.length; i++) {
            assertEquals(x[i], result[i], tolerance);
        }

        result = transformer.inversetransform2(x);
        for (int i = 0; i < result.length; i++) {
            assertEquals(y[i], result[i], tolerance);
        }
    }

    /**
     * Test of transformer for the sine function.
     */
    public void testSinFunction() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        FastCosineTransformer transformer = new FastCosineTransformer();
        double min, max, result[], tolerance = 1E-12; int N = 9;

        double expected[] = { 0.0, 3.26197262739567, 0.0,
                             -2.17958042710327, 0.0, -0.648846697642915,
                              0.0, -0.433545502649478, 0.0 };
        min = 0.0; max = 2.0 * Math.PI * N / (N-1);
        result = transformer.transform(f, min, max, N);
        for (int i = 0; i < N; i++) {
            assertEquals(expected[i], result[i], tolerance);
        }

        min = -Math.PI; max = Math.PI * (N+1) / (N-1);
        result = transformer.transform(f, min, max, N);
        for (int i = 0; i < N; i++) {
            assertEquals(-expected[i], result[i], tolerance);
        }
    }

    /**
     * Test of parameters for the transformer.
     */
    public void testParameters() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        FastCosineTransformer transformer = new FastCosineTransformer();

        try {
            // bad interval
            transformer.transform(f, 1, -1, 65);
            fail("Expecting IllegalArgumentException - bad interval");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.transform(f, -1, 1, 1);
            fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.transform(f, -1, 1, 64);
            fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}
