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

package org.apache.commons.math4.optim.nonlinear.scalar.noderiv;

import org.apache.commons.math4.analysis.MultivariateFunction;
import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.optim.InitialGuess;
import org.apache.commons.math4.optim.MaxEval;
import org.apache.commons.math4.optim.PointValuePair;
import org.apache.commons.math4.optim.SimpleBounds;
import org.apache.commons.math4.optim.SimpleValueChecker;
import org.apache.commons.math4.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

public class SimplexOptimizerMultiDirectionalTest {
    private static final int DIM = 13;

    @Test(expected=MathUnsupportedOperationException.class)
    public void testBoundsUnsupported() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final OptimTestUtils.FourExtrema fourExtrema = new OptimTestUtils.FourExtrema();

        optimizer.optimize(new MaxEval(100),
                           new ObjectiveFunction(fourExtrema),
                           GoalType.MINIMIZE,
                           new InitialGuess(new double[] { -3, 0 }),
                           new NelderMeadSimplex(new double[] { 0.2, 0.2 }),
                           new SimpleBounds(new double[] { -5, -1 },
                                            new double[] { 5, 1 }));
    }

    @Test
    public void testMinimize1() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-11, 1e-30);
        final OptimTestUtils.FourExtrema fourExtrema = new OptimTestUtils.FourExtrema();

        final PointValuePair optimum
            = optimizer.optimize(new MaxEval(200),
                                 new ObjectiveFunction(fourExtrema),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { -3, 0 }),
                                 new MultiDirectionalSimplex(new double[] { 0.2, 0.2 }));
        Assert.assertEquals(fourExtrema.xM, optimum.getPoint()[0], 4e-6);
        Assert.assertEquals(fourExtrema.yP, optimum.getPoint()[1], 3e-6);
        Assert.assertEquals(fourExtrema.valueXmYp, optimum.getValue(), 8e-13);
        Assert.assertTrue(optimizer.getEvaluations() > 120);
        Assert.assertTrue(optimizer.getEvaluations() < 150);

        // Check that the number of iterations is updated (MATH-949).
        Assert.assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testMinimize2() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-11, 1e-30);
        final OptimTestUtils.FourExtrema fourExtrema = new OptimTestUtils.FourExtrema();

        final PointValuePair optimum
            = optimizer.optimize(new MaxEval(200),
                                 new ObjectiveFunction(fourExtrema),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 1, 0 }),
                                 new MultiDirectionalSimplex(new double[] { 0.2, 0.2 }));
        Assert.assertEquals(fourExtrema.xP, optimum.getPoint()[0], 2e-8);
        Assert.assertEquals(fourExtrema.yM, optimum.getPoint()[1], 3e-6);
        Assert.assertEquals(fourExtrema.valueXpYm, optimum.getValue(), 2e-12);
        Assert.assertTrue(optimizer.getEvaluations() > 120);
        Assert.assertTrue(optimizer.getEvaluations() < 150);

        // Check that the number of iterations is updated (MATH-949).
        Assert.assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testMaximize1() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-11, 1e-30);
        final OptimTestUtils.FourExtrema fourExtrema = new OptimTestUtils.FourExtrema();

        final PointValuePair optimum
            = optimizer.optimize(new MaxEval(200),
                                 new ObjectiveFunction(fourExtrema),
                                 GoalType.MAXIMIZE,
                                 new InitialGuess(new double[] { -3.0, 0.0 }),
                                 new MultiDirectionalSimplex(new double[] { 0.2, 0.2 }));
        Assert.assertEquals(fourExtrema.xM, optimum.getPoint()[0], 7e-7);
        Assert.assertEquals(fourExtrema.yM, optimum.getPoint()[1], 3e-7);
        Assert.assertEquals(fourExtrema.valueXmYm, optimum.getValue(), 2e-14);
        Assert.assertTrue(optimizer.getEvaluations() > 120);
        Assert.assertTrue(optimizer.getEvaluations() < 150);

        // Check that the number of iterations is updated (MATH-949).
        Assert.assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testMaximize2() {
        SimplexOptimizer optimizer = new SimplexOptimizer(new SimpleValueChecker(1e-15, 1e-30));
        final OptimTestUtils.FourExtrema fourExtrema = new OptimTestUtils.FourExtrema();

        final PointValuePair optimum
            = optimizer.optimize(new MaxEval(200),
                                 new ObjectiveFunction(fourExtrema),
                                 GoalType.MAXIMIZE,
                                 new InitialGuess(new double[] { 1, 0 }),
                                 new MultiDirectionalSimplex(new double[] { 0.2, 0.2 }));
        Assert.assertEquals(fourExtrema.xP, optimum.getPoint()[0], 2e-8);
        Assert.assertEquals(fourExtrema.yP, optimum.getPoint()[1], 3e-6);
        Assert.assertEquals(fourExtrema.valueXpYp, optimum.getValue(), 2e-12);
        Assert.assertTrue(optimizer.getEvaluations() > 180);
        Assert.assertTrue(optimizer.getEvaluations() < 220);

        // Check that the number of iterations is updated (MATH-949).
        Assert.assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testRosenbrock() {
        final OptimTestUtils.Rosenbrock rosenbrock = new OptimTestUtils.Rosenbrock();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        PointValuePair optimum
           = optimizer.optimize(new MaxEval(100),
                                new ObjectiveFunction(rosenbrock),
                                GoalType.MINIMIZE,
                                new InitialGuess(new double[] { -1.2, 1 }),
                                new MultiDirectionalSimplex(new double[][] {
                                        { -1.2,  1.0 },
                                        { 0.9, 1.2 },
                                        {  3.5, -2.3 } }));
        Assert.assertTrue(optimizer.getEvaluations() > 50);
        Assert.assertTrue(optimizer.getEvaluations() < 100);
        Assert.assertTrue(optimum.getValue() > 1e-2);
    }

    @Test
    public void testPowell() {
        final OptimTestUtils.Powell powell = new OptimTestUtils.Powell();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(1000),
                                 new ObjectiveFunction(powell),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 3, -1, 0, 1 }),
                                 new MultiDirectionalSimplex(4));
        Assert.assertTrue(optimizer.getEvaluations() > 800);
        Assert.assertTrue(optimizer.getEvaluations() < 900);
        Assert.assertTrue(optimum.getValue() > 1e-2);
    }

    @Test
    public void testMath283() {
        // fails because MultiDirectional.iterateSimplex is looping forever
        // the while(true) should be replaced with a convergence check
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-14, 1e-14);
        final OptimTestUtils.Gaussian2D function = new OptimTestUtils.Gaussian2D(0, 0, 1);
        PointValuePair estimate = optimizer.optimize(new MaxEval(1000),
                                                     new ObjectiveFunction(function),
                                                     GoalType.MAXIMIZE,
                                                     new InitialGuess(function.getMaximumPosition()),
                                                     new MultiDirectionalSimplex(2));
        final double EPSILON = 1e-5;
        final double expectedMaximum = function.getMaximum();
        final double actualMaximum = estimate.getValue();
        Assert.assertEquals(expectedMaximum, actualMaximum, EPSILON);

        final double[] expectedPosition = function.getMaximumPosition();
        final double[] actualPosition = estimate.getPoint();
        Assert.assertEquals(expectedPosition[0], actualPosition[0], EPSILON );
        Assert.assertEquals(expectedPosition[1], actualPosition[1], EPSILON );
    }

    @Test
    public void testRosen() {
        doTest(new OptimTestUtils.Rosen(),
               OptimTestUtils.point(DIM, 0.1),
               GoalType.MINIMIZE,
               183861,
               new PointValuePair(OptimTestUtils.point(DIM, 1.0), 0.0),
               1e-4);
    }

    @Test
    public void testEllipse() {
        doTest(new OptimTestUtils.Elli(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               873,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
     }

    @Test
    public void testElliRotated() {
        doTest(new OptimTestUtils.ElliRotated(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               873,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
    }

    @Test
    public void testCigar() {
        doTest(new OptimTestUtils.Cigar(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               925,
               new PointValuePair(OptimTestUtils.point(DIM,0.0), 0.0),
               1e-14);
    }

    @Test
    public void testTwoAxes() {
        doTest(new OptimTestUtils.TwoAxes(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               1159,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
     }

    @Test
    public void testCigTab() {
        doTest(new OptimTestUtils.CigTab(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               795,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
     }

    @Test
    public void testSphere() {
        doTest(new OptimTestUtils.Sphere(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               665,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
    }

    @Test
    public void testTablet() {
        doTest(new OptimTestUtils.Tablet(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               873,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
    }

    @Test
    public void testDiffPow() {
        doTest(new OptimTestUtils.DiffPow(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               614,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
    }

    @Test
    public void testSsDiffPow() {
        doTest(new OptimTestUtils.SsDiffPow(),
               OptimTestUtils.point(DIM / 2, 1.0),
               GoalType.MINIMIZE,
               656,
               new PointValuePair(OptimTestUtils.point(DIM / 2, 0.0), 0.0),
               1e-15);
    }

    @Ignore
    @Test
    public void testAckley() {
        doTest(new OptimTestUtils.Ackley(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               587,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               0);
    }

    @Ignore
    @Test
    public void testRastrigin() {
        doTest(new OptimTestUtils.Rastrigin(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               535,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               0);
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
        final int dim = startPoint.length;
        final SimplexOptimizer optim = new SimplexOptimizer(1e-10, 1e-12);
        final PointValuePair result = optim.optimize(new MaxEval(maxEvaluations),
                                                     new ObjectiveFunction(func),
                                                     goal,
                                                     new InitialGuess(startPoint),
                                                     new MultiDirectionalSimplex(dim, 0.1));
        final double dist = MathArrays.distance(expected.getPoint(),
                                                result.getPoint());
        Assert.assertEquals(0d, dist, tol);
    }
}
