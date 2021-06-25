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

import java.util.Arrays;
import java.util.Collection;
import java.util.function.DoubleUnaryOperator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.Sinc;

/**
 * Test case for {@link FastCosineTransform}.
 * <p>
 * FCT algorithm is exact, the small tolerance number is used only to account
 * for round-off errors.
 */
@RunWith(value = Parameterized.class)
public final class FastCosineTransformerTest
    extends RealTransformerAbstractTest {

    private final FastCosineTransform.Norm normalization;

    private final int[] invalidDataSize;

    private final double[] relativeTolerance;

    private final int[] validDataSize;

    public FastCosineTransformerTest(final FastCosineTransform.Norm normalization) {
        this.normalization = normalization;
        this.validDataSize = new int[] {
            2, 3, 5, 9, 17, 33, 65, 129
        };
        this.invalidDataSize = new int[] {
            128
        };
        this.relativeTolerance = new double[] {
            1e-15, 1e-15, 1e-14, 1e-13, 1e-13, 1e-12, 1e-11, 1e-10
        };
    }

    /**
     * Returns an array containing {@code true, false} in order to
     * check both standard and orthogonal DCTs.
     *
     * @return an array of parameters for this parameterized test
     */
    @Parameters
    public static Collection<Object[]> data() {
        final FastCosineTransform.Norm[] normalization = FastCosineTransform.Norm.values();
        final Object[][] data = new FastCosineTransform.Norm[normalization.length][1];
        for (int i = 0; i < normalization.length; i++) {
            data[i][0] = normalization[i];
        }
        return Arrays.asList(data);
    }

    @Override
    RealTransform createRealTransformer(boolean inverse) {
        return new FastCosineTransform(normalization, inverse);
    }

    @Override
    int getInvalidDataSize(final int i) {
        return invalidDataSize[i];
    }

    @Override
    int getNumberOfInvalidDataSizes() {
        return invalidDataSize.length;
    }

    @Override
    int getNumberOfValidDataSizes() {
        return validDataSize.length;
    }

    @Override
    double getRelativeTolerance(final int i) {
        return relativeTolerance[i];
    }

    @Override
    int getValidDataSize(final int i) {
        return validDataSize[i];
    }

    @Override
    DoubleUnaryOperator getValidFunction() {
        final UnivariateFunction sinc = new Sinc();
        return x -> sinc.value(x);
    }

    @Override
    double getValidLowerBound() {
        return 0.0;
    }

    @Override
    double getValidUpperBound() {
        return Math.PI;
    }

    @Override
    double[] transform(final double[] x,
                       final boolean inverse) {
        final int n = x.length;
        final double[] y = new double[n];
        final double[] cos = new double[2 * (n - 1)];
        for (int i = 0; i < cos.length; i++) {
            cos[i] = Math.cos(Math.PI * i / (n - 1.0));
        }
        int sgn = 1;
        for (int j = 0; j < n; j++) {
            double yj = 0.5 * (x[0] + sgn * x[n - 1]);
            for (int i = 1; i < n - 1; i++) {
                yj += x[i] * cos[(i * j) % cos.length];
            }
            y[j] = yj;
            sgn *= -1;
        }
        final double s;
        if (!inverse) {
            if (normalization == FastCosineTransform.Norm.STD) {
                s = 1.0;
            } else if (normalization == FastCosineTransform.Norm.ORTHO) {
                s = Math.sqrt(2.0 / (n - 1.0));
            } else {
                throw new IllegalStateException();
            }
        } else {
            if (normalization == FastCosineTransform.Norm.STD) {
                s = 2.0 / (n - 1.0);
            } else if (normalization == FastCosineTransform.Norm.ORTHO) {
                s = Math.sqrt(2.0 / (n - 1.0));
            } else {
                throw new IllegalStateException();
            }
        }
        TransformUtils.scaleInPlace(y, s);
        return y;
    }

    // Additional tests.

    /** Test of transformer for the ad hoc data. */
    @Test
    public void testAdHocData() {
        FastCosineTransform transformer;
        double[] result;
        double tolerance = 1e-12;

        final double[] x = {
            0.0, 1.0, 4.0, 9.0, 16.0, 25.0, 36.0, 49.0, 64.0
        };
        final double[] y = {
            172.0, -105.096569476353, 27.3137084989848, -12.9593152353742,
            8.0, -5.78585076868676, 4.68629150101524, -4.15826451958632,
            4.0
        };

        transformer = new FastCosineTransform(FastCosineTransform.Norm.STD);
        result = transformer.apply(x);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(y[i], result[i], tolerance);
        }

        transformer = new FastCosineTransform(FastCosineTransform.Norm.STD, true);
        result = transformer.apply(y);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(x[i], result[i], tolerance);
        }

        TransformUtils.scaleInPlace(x, Math.sqrt(0.5 * (x.length - 1)));

        transformer = new FastCosineTransform(FastCosineTransform.Norm.ORTHO);
        result = transformer.apply(y);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(x[i], result[i], tolerance);
        }

        transformer = new FastCosineTransform(FastCosineTransform.Norm.ORTHO, true);
        result = transformer.apply(x);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(y[i], result[i], tolerance);
        }
    }

    /** Test of parameters for the transformer. */
    @Test
    public void testParameters() throws Exception {
        final UnivariateFunction sinFunction = new Sin();
        final DoubleUnaryOperator f = x -> sinFunction.value(x);
        final FastCosineTransform transformer = new FastCosineTransform(FastCosineTransform.Norm.STD);

        try {
            // bad interval
            transformer.apply(f, 1, -1, 65);
            Assert.fail("Expecting IllegalArgumentException - bad interval");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.apply(f, -1, 1, 1);
            Assert.fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.apply(f, -1, 1, 64);
            Assert.fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /** Test of transformer for the sine function. */
    @Test
    public void testSinFunction() {
        final UnivariateFunction sinFunction = new Sin();
        final DoubleUnaryOperator f = x -> sinFunction.value(x);
        final FastCosineTransform transformer = new FastCosineTransform(FastCosineTransform.Norm.STD);
        double tolerance = 1e-12;
        int size = 9;

        final double[] expected = {
            0.0, 3.26197262739567, 0.0, -2.17958042710327, 0.0,
            -0.648846697642915, 0.0, -0.433545502649478, 0.0
        };
        double min = 0.0;
        double max = 2.0 * Math.PI * size / (size - 1);
        double[] result = transformer.apply(f, min, max, size);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(expected[i], result[i], tolerance);
        }

        min = -Math.PI;
        max = Math.PI * (size + 1) / (size - 1);
        result = transformer.apply(f, min, max, size);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(-expected[i], result[i], tolerance);
        }
    }
}
