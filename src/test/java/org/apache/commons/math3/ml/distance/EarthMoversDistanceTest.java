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
package org.apache.commons.math3.ml.distance;

import org.junit.Assert;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.junit.Test;

/**
 * Tests for {@link EarthMoversDistance} class.
 */
public class EarthMoversDistanceTest {

    final DistanceMeasure distance = new EarthMoversDistance();
    final double[] a = { 0.1, 0.3, 0.2, 0.0, 0.25, 0.15 };
    final double[] b = { 0.3, 0.0, 0.4, 0.1, 0.0, 0.2 };

    @Test
    public void testZero() {
        Assert.assertEquals(0, distance.compute(a, a), 0d);
    }

    @Test
    public void testZero2() {
        final double[] zero = new double[] { 0, 0 };
        Assert.assertEquals(0, distance.compute(zero, zero), 0d);
    }

    @Test
    public void test() {
        double expected = 0.65; //0.2 + 0.1 + 0.1 + 0.2 + 0.05 + 0.0;
        Assert.assertEquals(expected, distance.compute(a, b), 1e-10);
        Assert.assertEquals(expected, distance.compute(b, a), 1e-10);
    }
}
