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

import org.apache.commons.math3.analysis.function.Sin;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link TransformUtils}.
 */
public class TransformUtilsTest {

    private static final Sin SIN_FUNCTION = new Sin();
    private static final DoubleUnaryOperator SIN = x -> SIN_FUNCTION.value(x);

    @Test
    public void testSampleWrongBounds() {
        assertThrows(TransformException.class, () ->
                TransformUtils.sample(SIN, Math.PI, 0.0, 10));
    }

    @Test
    public void testSampleNegativeNumberOfPoints() {
        assertThrows(TransformException.class, () ->
                TransformUtils.sample(SIN, 0.0, Math.PI, -1));
    }

    @Test
    public void testSampleNullNumberOfPoints() {
        assertThrows(TransformException.class, () ->
                TransformUtils.sample(SIN, 0.0, Math.PI, 0));
    }

    @Test
    public void testSample() {
        final int n = 11;
        final double min = 0.0;
        final double max = Math.PI;
        final double[] actual = TransformUtils.sample(SIN, min, max, n);
        for (int i = 0; i < n; i++) {
            final double x = min + (max - min) / n * i;
            Assert.assertEquals("x = " + x, Math.sin(x), actual[i], 1e-15);
        }
    }

}
