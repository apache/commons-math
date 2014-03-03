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
package org.apache.commons.math3.optim.nonlinear.scalar;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.CircleScalar;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;
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
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(753289573253l);
        RandomVectorGenerator generator
            = new UncorrelatedRandomVectorGenerator(new double[] { 50, 50 },
                                                    new double[] { 10, 10 },
                                                    new GaussianRandomGenerator(g));
        int nbStarts = 10;
        MultiStartMultivariateOptimizer optimizer
            = new MultiStartMultivariateOptimizer(underlying, nbStarts, generator);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(1000),
                                 circle.getObjectiveFunction(),
                                 circle.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 98.680, 47.345 }));
        Assert.assertEquals(1000, optimizer.getMaxEvaluations());
        PointValuePair[] optima = optimizer.getOptima();
        Assert.assertEquals(nbStarts, optima.length);
        for (PointValuePair o : optima) {
            // we check the results of all intermediate restarts here (there are 10 such results)
            Vector2D center = new Vector2D(o.getPointRef()[0], o.getPointRef()[1]);
            Assert.assertTrue(69.9592 < circle.getRadius(center));
            Assert.assertTrue(69.9602 > circle.getRadius(center));
            Assert.assertTrue(96.0745 < center.getX());
            Assert.assertTrue(96.0762 > center.getX());
            Assert.assertTrue(48.1344 < center.getY());
            Assert.assertTrue(48.1354 > center.getY());
        }

        Assert.assertTrue(optimizer.getEvaluations() > 850);
        Assert.assertTrue(optimizer.getEvaluations() < 900);

        Assert.assertEquals(3.1267527, optimum.getValue(), 1e-8);
    }

    @Test
    public void testRosenbrock() {
        Rosenbrock rosenbrock = new Rosenbrock();
        SimplexOptimizer underlying
            = new SimplexOptimizer(new SimpleValueChecker(-1, 1e-3));
        NelderMeadSimplex simplex = new NelderMeadSimplex(new double[][] {
                { -1.2,  1.0 },
                { 0.9, 1.2 } ,
                {  3.5, -2.3 }
            });
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(16069223052l);
        RandomVectorGenerator generator
            = new UncorrelatedRandomVectorGenerator(2, new GaussianRandomGenerator(g));
        int nbStarts = 10;
        MultiStartMultivariateOptimizer optimizer
            = new MultiStartMultivariateOptimizer(underlying, nbStarts, generator);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(1100),
                                 new ObjectiveFunction(rosenbrock),
                                 GoalType.MINIMIZE,
                                 simplex,
                                 new InitialGuess(new double[] { -1.2, 1.0 }));
        Assert.assertEquals(nbStarts, optimizer.getOptima().length);

        Assert.assertEquals(rosenbrock.getCount(), optimizer.getEvaluations());
        Assert.assertTrue(optimizer.getEvaluations() > 900);
        Assert.assertTrue(optimizer.getEvaluations() < 1200);
        Assert.assertTrue(optimum.getValue() < 5e-5);
    }

    private static class Rosenbrock implements MultivariateFunction {
        private int count;

        public Rosenbrock() {
            count = 0;
        }

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
}
