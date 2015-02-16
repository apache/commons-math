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
package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Constant;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;
import org.junit.Assert;

/**
 * Test for {@link GaussIntegrator} class.
 *
 */
public class GaussIntegratorTest {
    @Test
    public void testGetWeights() {
        final double[] points = { 0, 1.2, 3.4 };
        final double[] weights = { 9.8, 7.6, 5.4 };

        final GaussIntegrator integrator
            = new GaussIntegrator(new Pair<double[], double[]>(points, weights));

        Assert.assertEquals(weights.length, integrator.getNumberOfPoints());

        for (int i = 0; i < integrator.getNumberOfPoints(); i++) {
            Assert.assertEquals(weights[i], integrator.getWeight(i), 0d);
        }
    }

    @Test
    public void testGetPoints() {
        final double[] points = { 0, 1.2, 3.4 };
        final double[] weights = { 9.8, 7.6, 5.4 };

        final GaussIntegrator integrator
            = new GaussIntegrator(new Pair<double[], double[]>(points, weights));

        Assert.assertEquals(points.length, integrator.getNumberOfPoints());

        for (int i = 0; i < integrator.getNumberOfPoints(); i++) {
            Assert.assertEquals(points[i], integrator.getPoint(i), 0d);
        }
    }

    @Test
    public void testIntegrate() {
        final double[] points = { 0, 1, 2, 3, 4, 5 };
        final double[] weights = { 1, 1, 1, 1, 1, 1 };

        final GaussIntegrator integrator
            = new GaussIntegrator(new Pair<double[], double[]>(points, weights));

        final double val = 123.456;
        final UnivariateFunction c = new Constant(val);

        final double s = integrator.integrate(c);
        Assert.assertEquals(points.length * val, s, 0d);
    }
}
