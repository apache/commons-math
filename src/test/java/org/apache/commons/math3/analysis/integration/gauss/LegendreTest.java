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

import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.junit.Test;
import org.junit.Assert;

/**
 * Test of the {@link LegendreRuleFactory}.
 *
 */
public class LegendreTest {
    private static final GaussIntegratorFactory factory = new GaussIntegratorFactory();

    @Test
    public void testCos() {
        final UnivariateFunction cos = new Cos();

        final GaussIntegrator integrator = factory.legendre(7, 0, Math.PI / 2);
        final double s = integrator.integrate(cos);
        // System.out.println("s=" + s + " e=" + 1);
        Assert.assertEquals(1, s, Math.ulp(1d));
    }


    @Test
    public void testInverse() {
        final UnivariateFunction inv = new Inverse();
        final UnivariateFunction log = new Log();

        final double lo = 12.34;
        final double hi = 456.78;

        final GaussIntegrator integrator = factory.legendre(60, lo, hi);
        final double s = integrator.integrate(inv);
        final double expected = log.value(hi) - log.value(lo);
        // System.out.println("s=" + s + " e=" + expected);
        Assert.assertEquals(expected, s, 1e-14);
    }
}
