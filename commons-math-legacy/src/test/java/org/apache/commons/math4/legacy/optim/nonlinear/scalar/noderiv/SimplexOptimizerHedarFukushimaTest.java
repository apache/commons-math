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

package org.apache.commons.math4.legacy.optim.nonlinear.scalar.noderiv;

import java.util.Arrays;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.optim.InitialGuess;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.SimulatedAnnealing;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.PopulationSize;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.TestFunction;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link HedarFukushimaTransform}.
 */
public class SimplexOptimizerHedarFukushimaTest {
    @Test
    public void testParabola2D() {
        final int dim = 2;
        doTest(TestFunction.PARABOLA.withDimension(dim),
               OptimTestUtils.point(new double[] { -4, 8 }, 5e-1),
               GoalType.MINIMIZE,
               250,
               new PointValuePair(OptimTestUtils.point(dim, 0.0), 0.0),
               1e-4);
    }

    @Test
    public void testRosenbrock2D() {
        final int dim = 2;
        doTest(TestFunction.ROSENBROCK.withDimension(dim),
               OptimTestUtils.point(new double[] { -4, 8 }, 5e-1),
               GoalType.MINIMIZE,
               600,
               new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0),
               1e-4);
    }

    @Test
    public void testRosenbrock5D() {
        final int dim = 5;
        doTest(TestFunction.ROSENBROCK.withDimension(dim),
               OptimTestUtils.point(new double[] { 2, -1, -4, 5, 9 }, 5e-1),
               GoalType.MINIMIZE,
               4650,
               new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0),
               5e-4);
    }

    @Test
    public void testPowell() {
        final int dim = 4;
        doTest(TestFunction.POWELL.withDimension(dim),
               OptimTestUtils.point(new double[] { 3, -1, 0, 1 }, 1e-1),
               GoalType.MINIMIZE,
               1000,
               new PointValuePair(OptimTestUtils.point(dim, 0.0), 0.0),
               1e-2);
    }

    /**
     * @param func Function to optimize.
     * @param startPoint Starting point.
     * @param goal Minimization or maximization.
     * @param maxEvaluations Maximum number of evaluations.
     * @param expected Expected optimum.
     * @param tol Tolerance for checking that the optimum is correct.
     */
    private void doTest(MultivariateFunction func,
                        double[] startPoint,
                        GoalType goal,
                        int maxEvaluations,
                        PointValuePair expected,
                        double tol) {
        doTest(func,
               startPoint,
               goal,
               maxEvaluations,
               Simplex.equalSidesAlongAxes(startPoint.length, 0.2),
               expected,
               tol);
    }

    /**
     * @param func Function to optimize.
     * @param startPoint Starting point.
     * @param goal Minimization or maximization.
     * @param maxEvaluations Maximum number of evaluations.
     * @param simplexSteps Initial simplex.
     * @param expected Expected optimum.
     * @param tol Tolerance for checking that the optimum is correct.
     */
    private void doTest(MultivariateFunction func,
                        double[] startPoint,
                        GoalType goal,
                        int maxEvaluations,
                        Simplex simplex,
                        PointValuePair expected,
                        double tol) {
        final String name = func.toString();
        final int dim = startPoint.length;

        // Simulated annealing setup.
        final SimulatedAnnealing.CoolingSchedule coolSched =
            SimulatedAnnealing.CoolingSchedule.decreasingExponential(0.7);
        final UniformRandomProvider rng = RandomSource.KISS.create();
        final SimulatedAnnealing sa = new SimulatedAnnealing(dim,
                                                             0.9,
                                                             1e-13,
                                                             coolSched,
                                                             rng);
        final PopulationSize popSize = new PopulationSize(dim);

        final int maxEval = Math.max(maxEvaluations, 25000);
        final SimplexOptimizer optim = new SimplexOptimizer(-1, 1e-8);
        final PointValuePair result = optim.optimize(new MaxEval(maxEval),
                                                     new ObjectiveFunction(func),
                                                     goal,
                                                     new InitialGuess(startPoint),
                                                     simplex,
                                                     new HedarFukushimaTransform(),
                                                     popSize,
                                                     sa);
        final double[] endPoint = result.getPoint();
        final double funcValue = result.getValue();
        Assert.assertEquals(name + ": value at " + Arrays.toString(endPoint),
                            expected.getValue(),
                            funcValue, 1e-2);

        final double dist = MathArrays.distance(expected.getPoint(),
                                                endPoint);
        Assert.assertEquals(name + ": distance to optimum", 0d, dist, tol);

        final int nEval = optim.getEvaluations();
        Assert.assertTrue(name + ": nEval=" + nEval,
                          nEval < maxEvaluations);
    }
}
