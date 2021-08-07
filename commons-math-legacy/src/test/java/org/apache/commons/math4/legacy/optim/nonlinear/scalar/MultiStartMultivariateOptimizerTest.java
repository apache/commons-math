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
package org.apache.commons.math4.legacy.optim.nonlinear.scalar;

import java.util.function.Supplier;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.optim.InitialGuess;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.SimpleValueChecker;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.gradient.CircleScalar;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.noderiv.NelderMeadTransform;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.noderiv.Simplex;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.sampling.distribution.GaussianSampler;
import org.apache.commons.rng.sampling.distribution.ZigguratNormalizedGaussianSampler;
import org.junit.Assert;
import org.junit.Test;

public class MultiStartMultivariateOptimizerTest {
    @Test
    public void testCircleFitting() {
        CircleScalar circle = new CircleScalar();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);
        // TODO: the wrapper around NonLinearConjugateGradientOptimizer is a temporary hack for
        // version 3.1 of the library. It should be removed when NonLinearConjugateGradientOptimizer
        // will officially be declared as implementing MultivariateDifferentiableOptimizer
        GradientMultivariateOptimizer underlying
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-10, 1e-10));
        final Supplier<double[]> generator = gaussianRandom(new double[] { 50, 50 },
                                                            new double[] { 10, 10 },
                                                            RandomSource.MT_64.create());
        int nbStarts = 10;
        MultiStartMultivariateOptimizer optimizer
            = new MultiStartMultivariateOptimizer(underlying, nbStarts, generator);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(1000),
                                 circle.getObjectiveFunction(),
                                 circle.getObjectiveFunctionGradient(),
                                 new NelderMeadTransform(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 98.680, 47.345 }));
        Assert.assertEquals(1000, optimizer.getMaxEvaluations());
        PointValuePair[] optima = optimizer.getOptima();
        Assert.assertEquals(nbStarts, optima.length);
        for (PointValuePair o : optima) {
            // we check the results of all intermediate restarts here (there are 10 such results)
            Vector2D center = Vector2D.of(o.getPointRef()[0], o.getPointRef()[1]);
            Assert.assertEquals(69.9597, circle.getRadius(center), 1e-3);
            Assert.assertEquals(96.07535, center.getX(), 1.4e-3);
            Assert.assertEquals(48.1349, center.getY(), 5e-3);
        }

        final int numEval = optimizer.getEvaluations();
        Assert.assertTrue("n=" + numEval, numEval > 750);
        Assert.assertTrue("n=" + numEval, numEval < 950);

        Assert.assertEquals(3.1267527, optimum.getValue(), 1e-8);
    }

    @Test
    public void testRosenbrock() {
        Rosenbrock rosenbrock = new Rosenbrock();
        SimplexOptimizer underlying
            = new SimplexOptimizer(new SimpleValueChecker(-1, 1e-3));
        final Simplex simplex = Simplex.of(new double[][] {
                { -1.2,  1.0 },
                { 0.9, 1.2 } ,
                {  3.5, -2.3 }
            });
        final Supplier<double[]> generator = gaussianRandom(new double[] { 0, 0 },
                                                            new double[] { 1, 1 },
                                                            RandomSource.MT_64.create());
        int nbStarts = 10;
        MultiStartMultivariateOptimizer optimizer
            = new MultiStartMultivariateOptimizer(underlying, nbStarts, generator);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(1100),
                                 new ObjectiveFunction(rosenbrock),
                                 GoalType.MINIMIZE,
                                 simplex,
                                 new NelderMeadTransform(),
                                 new InitialGuess(new double[] { -1.2, 1.0 }));
        Assert.assertEquals(nbStarts, optimizer.getOptima().length);

        final int numEval = optimizer.getEvaluations();
        Assert.assertEquals(rosenbrock.getCount(), numEval);
        Assert.assertTrue("numEval=" + numEval, numEval > 700);
        Assert.assertTrue("numEval=" + numEval, numEval < 1200);
        Assert.assertTrue("optimum=" + optimum.getValue(), optimum.getValue() < 5e-5);
    }

    private static class Rosenbrock implements MultivariateFunction {
        private int count;

        Rosenbrock() {
            count = 0;
        }

        @Override
        public double value(double[] x) {
            ++count;
            double a = x[1] - x[0] * x[0];
            double b = 1 - x[0];
            return 100 * a * a + b * b;
        }

        public int getCount() {
            return count;
        }
    }

    /**
     * @param mean Means.
     * @param stdev Standard deviations.
     * @param rng Underlying RNG.
     * @return a random array generator where each element is a Gaussian
     * sampling with the given mean and standard deviation.
     */
    private Supplier<double[]> gaussianRandom(final double[] mean,
                                              final double[] stdev,
                                              final UniformRandomProvider rng) {
        final ZigguratNormalizedGaussianSampler normalized = new ZigguratNormalizedGaussianSampler(rng);
        final GaussianSampler[] samplers = new GaussianSampler[mean.length];
        for (int i = 0; i < mean.length; i++) {
            samplers[i] = new GaussianSampler(normalized, mean[i], stdev[i]);
        }

        return new Supplier<double[]>() {
            @Override
            public double[] get() {
                final double[] s = new double[mean.length];
                for (int i = 0; i < mean.length; i++) {
                    s[i] = samplers[i].sample();
                }
                return s;
            }
        };
    }
}
