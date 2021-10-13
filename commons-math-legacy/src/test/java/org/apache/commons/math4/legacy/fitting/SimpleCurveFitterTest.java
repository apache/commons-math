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
package org.apache.commons.math4.legacy.fitting;

import java.util.Random;

import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.analysis.ParametricUnivariateFunction;
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.UniformContinuousDistribution;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Test;

/**
 * Test for class {@link SimpleCurveFitter}.
 */
public class SimpleCurveFitterTest {
    @Test
    public void testPolynomialFit() {
        final Random randomizer = new Random(53882150042L);
        final ContinuousDistribution.Sampler rng
            = UniformContinuousDistribution.of(-100, 100).createSampler(RandomSource.WELL_512_A.create(64925784253L));

        final double[] coeff = { 12.9, -3.4, 2.1 }; // 12.9 - 3.4 x + 2.1 x^2
        final PolynomialFunction f = new PolynomialFunction(coeff);

        // Collect data from a known polynomial.
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < 100; i++) {
            final double x = rng.sample();
            obs.add(x, f.value(x) + 0.1 * randomizer.nextGaussian());
        }

        final ParametricUnivariateFunction function = new PolynomialFunction.Parametric();
        // Start fit from initial guesses that are far from the optimal values.
        final SimpleCurveFitter fitter
            = SimpleCurveFitter.create(function,
                                       new double[] { -1e20, 3e15, -5e25 });
        final double[] best = fitter.fit(obs.toList());

        TestUtils.assertEquals("best != coeff", coeff, best, 2e-2);
    }
}
