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
 * Test case for {@link FastSineTransform}.
 * <p>
 * FST algorithm is exact, the small tolerance number is used only
 * to account for round-off errors.
 */
@RunWith(value = Parameterized.class)
public final class FastSineTransformerTest extends RealTransformerAbstractTest {

    private final FastSineTransform.Norm normalization;

    private final int[] invalidDataSize;

    private final double[] relativeTolerance;

    private final int[] validDataSize;

    public FastSineTransformerTest(final FastSineTransform.Norm normalization) {
        this.normalization = normalization;
        this.validDataSize = new int[] {
            1, 2, 4, 8, 16, 32, 64, 128
        };
        this.invalidDataSize = new int[] {
            129
        };
        this.relativeTolerance = new double[] {
            1e-15, 1e-15, 1e-14, 1e-14, 1e-13, 1e-12, 1e-11, 1e-11
        };
    }

    /**
     * Returns an array containing {@code true, false} in order to check both
     * standard and orthogonal DSTs.
     *
     * @return an array of parameters for this parameterized test.
     */
    @Parameters
    public static Collection<Object[]> data() {
        final FastSineTransform.Norm[] normalization = FastSineTransform.Norm.values();
        final Object[][] data = new FastSineTransform.Norm[normalization.length][1];
        for (int i = 0; i < normalization.length; i++) {
            data[i][0] = normalization[i];
        }
        return Arrays.asList(data);
    }

    /**
     * {@inheritDoc}
     *
     * Overriding the default implementation allows to ensure that the first
     * element of the data set is zero.
     */
    @Override
    double[] createRealData(final int n) {
        final double[] data = super.createRealData(n);
        data[0] = 0;
        return data;
    }

    @Override
    RealTransform createRealTransformer(boolean inverse) {
        return new FastSineTransform(normalization, inverse);
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
    double[] transform(final double[] x, boolean inverse) {
        final int n = x.length;
        final double[] y = new double[n];
        final double[] sin = new double[2 * n];
        for (int i = 0; i < sin.length; i++) {
            sin[i] = Math.sin(Math.PI * i / n);
        }
        for (int j = 0; j < n; j++) {
            double yj = 0.0;
            for (int i = 0; i < n; i++) {
                yj += x[i] * sin[(i * j) % sin.length];
            }
            y[j] = yj;
        }
        final double s;
        if (!inverse) {
            if (normalization == FastSineTransform.Norm.STD) {
                s = 1;
            } else if (normalization == FastSineTransform.Norm.ORTHO) {
                s = Math.sqrt(2d / n);
            } else {
                throw new IllegalStateException();
            }
        } else {
            if (normalization == FastSineTransform.Norm.STD) {
                s = 2d / n;
            } else if (normalization == FastSineTransform.Norm.ORTHO) {
                s = Math.sqrt(2d / n);
            } else {
                throw new IllegalStateException();
            }
        }

        TransformUtils.scaleInPlace(y, s);
        return y;
    }

    // Additional tests.

    @Test
    public void testTransformRealFirstElementNotZero() {
        final double[] data = new double[] {
            1, 1, 1, 1
        };
        for (boolean type : new boolean[] {true, false}) {
            try {
                final RealTransform transformer = createRealTransformer(type);
                transformer.apply(data);
                Assert.fail("type=" + type);
            } catch (IllegalArgumentException e) {
                // Expected: do nothing
            }
        }
    }

    // Additional (legacy) tests.

    /**
     * Test of transformer for the ad hoc data.
     */
    @Test
    public void testAdHocData() {
        FastSineTransform transformer;
        double tolerance = 1e-12;

        final double[] x = {
            0, 1, 2, 3, 4, 5, 6, 7
        };
        final double[] y = {
            0.0, 20.1093579685034, -9.65685424949238,
            5.98642305066196, -4.0, 2.67271455167720,
            -1.65685424949238, 0.795649469518633
        };

        transformer = new FastSineTransform(FastSineTransform.Norm.STD);
        double[] result = transformer.apply(x);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(y[i], result[i], tolerance);
        }

        transformer = new FastSineTransform(FastSineTransform.Norm.STD, true);
        result = transformer.apply(y);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(x[i], result[i], tolerance);
        }

        TransformUtils.scaleInPlace(x, Math.sqrt(x.length / 2d));
        transformer = new FastSineTransform(FastSineTransform.Norm.ORTHO);

        result = transformer.apply(y);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(x[i], result[i], tolerance);
        }

        transformer = new FastSineTransform(FastSineTransform.Norm.ORTHO, true);
        result = transformer.apply(x);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(y[i], result[i], tolerance);
        }
    }

    /**
     * Test of transformer for the sine function.
     */
    @Test
    public void testSinFunction() {
        final UnivariateFunction sinFunction = new Sin();
        final DoubleUnaryOperator f = x -> sinFunction.value(x);
        final FastSineTransform transformer = new FastSineTransform(FastSineTransform.Norm.STD);
        double tolerance = 1e-12;
        int size = 1 << 8;

        double min = 0.0;
        double max = 2 * Math.PI;
        double[] result = transformer.apply(f, min, max, size);
        Assert.assertEquals(size >> 1, result[2], tolerance);
        for (int i = 0; i < size; i += i == 1 ? 2 : 1) {
            Assert.assertEquals(0.0, result[i], tolerance);
        }

        min = -Math.PI;
        max = Math.PI;
        result = transformer.apply(f, min, max, size);
        Assert.assertEquals(-(size >> 1), result[2], tolerance);
        for (int i = 0; i < size; i += i == 1 ? 2 : 1) {
            Assert.assertEquals(0.0, result[i], tolerance);
        }
    }

    /**
     * Test of parameters for the transformer.
     */
    @Test
    public void testParameters() throws Exception {
        final UnivariateFunction sinFunction = new Sin();
        final DoubleUnaryOperator f = x -> sinFunction.value(x);
        final FastSineTransform transformer = new FastSineTransform(FastSineTransform.Norm.STD);

        try {
            // bad interval
            transformer.apply(f, 1, -1, 64);
            Assert.fail("Expecting IllegalArgumentException - bad interval");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.apply(f, -1, 1, 0);
            Assert.fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.apply(f, -1, 1, 100);
            Assert.fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}
