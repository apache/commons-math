// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.commons.math3.optimization.fitting;

import java.util.Random;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
import org.apache.commons.math3.optimization.general.GaussNewtonOptimizer;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.optimization.SimpleVectorValueChecker;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.random.RandomDataImpl;
import org.apache.commons.math3.TestUtils;

import org.junit.Test;
import org.junit.Assert;

/**
 * Test for class {@link CurveFitter} where the function to fit is a
 * polynomial.
 */
public class PolynomialFitterTest {
    @Test
    public void testFit() {
        final RandomDataImpl rng = new RandomDataImpl();
        rng.reSeed(64925784252L);

        final LevenbergMarquardtOptimizer optim = new LevenbergMarquardtOptimizer();
        final CurveFitter fitter = new CurveFitter(optim);
        final double[] coeff = { 12.9, -3.4, 2.1 }; // 12.9 - 3.4 x + 2.1 x^2
        final PolynomialFunction f = new PolynomialFunction(coeff);

        // Collect data from a known polynomial.
        for (int i = 0; i < 100; i++) {
            final double x = rng.nextUniform(-100, 100);
            fitter.addObservedPoint(x, f.value(x));
        }

        // Start fit from initial guesses that are far from the optimal values.
        final double[] best = fitter.fit(new PolynomialFunction.Parametric(),
                                         new double[] { -1e-20, 3e15, -5e25 });

        TestUtils.assertEquals("best != coeff", coeff, best, 1e-12);
    }

    @Test
    public void testNoError() {
        Random randomizer = new Random(64925784252l);
        for (int degree = 1; degree < 10; ++degree) {
            PolynomialFunction p = buildRandomPolynomial(degree, randomizer);

            CurveFitter fitter = new CurveFitter(new LevenbergMarquardtOptimizer());
            for (int i = 0; i <= degree; ++i) {
                fitter.addObservedPoint(1.0, i, p.value(i));
            }

            final double[] init = new double[degree + 1];
            PolynomialFunction fitted = new PolynomialFunction(fitter.fit(Integer.MAX_VALUE,
                                                                          new PolynomialFunction.Parametric(),
                                                                          init));

            for (double x = -1.0; x < 1.0; x += 0.01) {
                double error = FastMath.abs(p.value(x) - fitted.value(x)) /
                               (1.0 + FastMath.abs(p.value(x)));
                Assert.assertEquals(0.0, error, 1.0e-6);
            }
        }
    }

    @Test
    public void testSmallError() {
        Random randomizer = new Random(53882150042l);
        double maxError = 0;
        for (int degree = 0; degree < 10; ++degree) {
            PolynomialFunction p = buildRandomPolynomial(degree, randomizer);

            CurveFitter fitter = new CurveFitter(new LevenbergMarquardtOptimizer());
            for (double x = -1.0; x < 1.0; x += 0.01) {
                fitter.addObservedPoint(1.0, x,
                                        p.value(x) + 0.1 * randomizer.nextGaussian());
            }

            final double[] init = new double[degree + 1];
            PolynomialFunction fitted = new PolynomialFunction(fitter.fit(Integer.MAX_VALUE,
                                                                          new PolynomialFunction.Parametric(),
                                                                          init));

            for (double x = -1.0; x < 1.0; x += 0.01) {
                double error = FastMath.abs(p.value(x) - fitted.value(x)) /
                              (1.0 + FastMath.abs(p.value(x)));
                maxError = FastMath.max(maxError, error);
                Assert.assertTrue(FastMath.abs(error) < 0.1);
            }
        }
        Assert.assertTrue(maxError > 0.01);
    }

    @Test
    public void testRedundantSolvable() {
        // Levenberg-Marquardt should handle redundant information gracefully
        checkUnsolvableProblem(new LevenbergMarquardtOptimizer(), true);
    }

    @Test
    public void testRedundantUnsolvable() {
        // Gauss-Newton should not be able to solve redundant information
        checkUnsolvableProblem(new GaussNewtonOptimizer(true, new SimpleVectorValueChecker(1e-15, 1e-15)), false);
    }

    private void checkUnsolvableProblem(DifferentiableMultivariateVectorOptimizer optimizer,
                                        boolean solvable) {
        Random randomizer = new Random(1248788532l);
        for (int degree = 0; degree < 10; ++degree) {
            PolynomialFunction p = buildRandomPolynomial(degree, randomizer);

            CurveFitter fitter = new CurveFitter(optimizer);

            // reusing the same point over and over again does not bring
            // information, the problem cannot be solved in this case for
            // degrees greater than 1 (but one point is sufficient for
            // degree 0)
            for (double x = -1.0; x < 1.0; x += 0.01) {
                fitter.addObservedPoint(1.0, 0.0, p.value(0.0));
            }

            try {
                final double[] init = new double[degree + 1];
                fitter.fit(Integer.MAX_VALUE,
                           new PolynomialFunction.Parametric(),
                           init);
                Assert.assertTrue(solvable || (degree == 0));
            } catch(ConvergenceException e) {
                Assert.assertTrue((! solvable) && (degree > 0));
            }
        }
    }

    private PolynomialFunction buildRandomPolynomial(int degree, Random randomizer) {
        final double[] coefficients = new double[degree + 1];
        for (int i = 0; i <= degree; ++i) {
            coefficients[i] = randomizer.nextGaussian();
        }
        return new PolynomialFunction(coefficients);
    }
}
