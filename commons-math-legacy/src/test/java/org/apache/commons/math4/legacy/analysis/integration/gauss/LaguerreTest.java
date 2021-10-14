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
package org.apache.commons.math4.legacy.analysis.integration.gauss;

import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.numbers.gamma.Gamma;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test of the {@link LaguerreRuleFactory}.
 */
public class LaguerreTest {
    private static final GaussIntegratorFactory factory = new GaussIntegratorFactory();

    @Test
    public void testGamma() {
        final double tol = 1e-13;

        for (int i = 2; i < 10; i += 1) {
            final double t = i;

            final UnivariateFunction f = new UnivariateFunction() {
                @Override
                public double value(double x) {
                    return JdkMath.pow(x, t - 1);
                }
            };

            final GaussIntegrator integrator = factory.laguerre(7);
            final double s = integrator.integrate(f);
            Assert.assertEquals(1d, Gamma.value(t) / s, tol);
        }
    }
}
