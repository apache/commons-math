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

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.numbers.core.Precision;


/**
 * Test for {@link FastHadamardTransform}.
 */
public final class FastHadamardTransformerTest {
    /**
     * Test of transformer for the a 8-point FHT (means n=8)
     */
    @Test
    public void test8Points() {
        checkAllTransforms(new int[] {1, 4, -2, 3, 0, 1, 4, -1},
                           new int[] {10, -4, 2, -4, 2, -12, 6, 8});
    }

    /**
     * Test of transformer for the a 4-points FHT (means n=4)
     */
    @Test
    public void test4Points() {
        checkAllTransforms(new int[] {1, 2, 3, 4},
                           new int[] {10, -2, -4, 0});
    }

    /**
     * Test the inverse transform of an integer vector is not always an integer vector
     */
    @Test
    public void testNoIntInverse() {
        final FastHadamardTransform transformer = new FastHadamardTransform(true);
        final double[] x = transformer.apply(new double[] {0, 1, 0, 1});
        Assert.assertEquals(0.5, x[0], 0);
        Assert.assertEquals(-0.5, x[1], 0);
        Assert.assertEquals(0.0, x[2], 0);
        Assert.assertEquals(0.0, x[3], 0);
    }

    /**
     * Test of transformer for wrong number of points
     */
    @Test
    public void test3Points() {
        try {
            new FastHadamardTransform().apply(new double[3]);
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    private void checkAllTransforms(int[] x, int[] y) {
        checkDoubleTransform(x, y);
        checkInverseDoubleTransform(x, y);
        checkIntTransform(x, y);
    }

    private void checkDoubleTransform(int[] x, int[] y) {
        // Initiate the transformer
        final FastHadamardTransform transformer = new FastHadamardTransform();

        // check double transform
        final double[] dX = new double[x.length];
        for (int i = 0; i < dX.length; ++i) {
            dX[i] = x[i];
        }
        final double[] dResult = transformer.apply(dX);
        for (int i = 0; i < dResult.length; i++) {
            // compare computed results to precomputed results
            Assert.assertTrue(Precision.equals(y[i], dResult[i], 1));
        }
    }

    private void checkIntTransform(int[] x, int[] y) {
        // Initiate the transformer
        final FastHadamardTransform transformer = new FastHadamardTransform();

        // check integer transform
        final int[] iResult = transformer.apply(x);
        for (int i = 0; i < iResult.length; i++) {
            // compare computed results to precomputed results
            Assert.assertEquals(y[i], iResult[i]);
        }
    }

    private void checkInverseDoubleTransform(int[]x, int[] y) {
        // Initiate the transformer
        final FastHadamardTransform transformer = new FastHadamardTransform(true);

        // check double transform
        final double[] dY = new double[y.length];
        for (int i = 0; i < dY.length; ++i) {
            dY[i] = y[i];
        }
        final double[] dResult = transformer.apply(dY);
        for (int i = 0; i < dResult.length; i++) {
            // compare computed results to precomputed results
            Assert.assertTrue(Precision.equals(x[i], dResult[i], 1));
        }
    }
}
