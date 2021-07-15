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
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.legacy.optim.InitialGuess;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.SimpleBounds;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.legacy.core.MathArrays;
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
                           Simplex.of(new double[] { 0.2, 0.2 }),
                           new MultiDirectionalTransform(),
                           new SimpleBounds(new double[] { -5, -1 },
                                            new double[] { 5, 1 }));
    }

    @Test
    public void testMath283() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-14, 1e-14);
        final OptimTestUtils.Gaussian2D function = new OptimTestUtils.Gaussian2D(0, 0, 1);
        PointValuePair estimate = optimizer.optimize(new MaxEval(1000),
                                                     new ObjectiveFunction(function),
                                                     GoalType.MAXIMIZE,
                                                     new InitialGuess(function.getMaximumPosition()),
                                                     Simplex.of(2),
                                                     new MultiDirectionalTransform());
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
    public void testFourExtremaMinimize1() {
        final OptimTestUtils.FourExtrema f = new OptimTestUtils.FourExtrema();
        doTest(f,
               OptimTestUtils.point(new double[] {-3, 0}, 1e-1),
               GoalType.MINIMIZE,
               105,
               Simplex.of(OptimTestUtils.point(2, 0.2, 1e-2)),
               new PointValuePair(new double[] {f.xM, f.yP}, f.valueXmYp),
               1e-6);
    }
    @Test
    public void testFourExtremaMaximize1() {
        final OptimTestUtils.FourExtrema f = new OptimTestUtils.FourExtrema();
        doTest(f,
               OptimTestUtils.point(new double[] {-3, 0}, 1e-1),
               GoalType.MAXIMIZE,
               100,
               Simplex.of(OptimTestUtils.point(2, 0.2, 1e-2)),
               new PointValuePair(new double[] {f.xM, f.yM}, f.valueXmYm),
               1e-6);
    }
    @Test
    public void testFourExtremaMinimize2() {
        final OptimTestUtils.FourExtrema f = new OptimTestUtils.FourExtrema();
        doTest(f,
               OptimTestUtils.point(new double[] {1, 0}, 1e-1),
               GoalType.MINIMIZE,
               100,
               Simplex.of(OptimTestUtils.point(2, 0.2, 1e-2)),
               new PointValuePair(new double[] {f.xP, f.yM}, f.valueXpYm),
               1e-6);
    }
    @Test
    public void testFourExtremaMaximize2() {
        final OptimTestUtils.FourExtrema f = new OptimTestUtils.FourExtrema();
        doTest(f,
               OptimTestUtils.point(new double[] {1, 0}, 1e-1),
               GoalType.MAXIMIZE,
               110,
               Simplex.of(OptimTestUtils.point(2, 0.2, 1e-2)),
               new PointValuePair(new double[] {f.xP, f.yP}, f.valueXpYp),
               1e-6);
    }

    @Test
    public void testRosenbrock() {
        doTest(new OptimTestUtils.Rosenbrock(),
               OptimTestUtils.point(new double[] { -1.2, 1 }, 1e-1),
               GoalType.MINIMIZE,
               190,
               new PointValuePair(OptimTestUtils.point(2, 1.0), 0.0),
               1e-4);
    }

    @Test
    public void testPowell() {
        doTest(new OptimTestUtils.Powell(),
               OptimTestUtils.point(new double[] { 3, -1, 0, 1 }, 1e-1),
               GoalType.MINIMIZE,
               420,
               new PointValuePair(OptimTestUtils.point(4, 0.0), 0.0),
               2e-4);
    }

    @Test
    public void testRosen() {
        doTest(new OptimTestUtils.Rosen(),
               OptimTestUtils.point(DIM, 0.1),
               GoalType.MINIMIZE,
               186915,
               new PointValuePair(OptimTestUtils.point(DIM, 1.0), 0.0),
               1e-4);
    }

    @Ignore@Test
    public void testEllipse() {
        doTest(new OptimTestUtils.Elli(),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               911,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-9);
     }

    @Ignore@Test
    public void testElliRotated() {
        doTest(new OptimTestUtils.ElliRotated(),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               911,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-9);
    }

    @Test
    public void testCigar() {
        doTest(new OptimTestUtils.Cigar(),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               7000,
               new PointValuePair(OptimTestUtils.point(DIM,0.0), 0.0),
               1e-6);
    }

    @Ignore@Test
    public void testTwoAxes() {
        doTest(new OptimTestUtils.TwoAxes(),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               1219,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-12);
     }

    @Ignore@Test
    public void testCigTab() {
        doTest(new OptimTestUtils.CigTab(),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               827,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-8);
     }

    @Test
    public void testSphere() {
        doTest(new OptimTestUtils.Sphere(),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               2580,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-6);
    }

    @Ignore@Test
    public void testTablet() {
        doTest(new OptimTestUtils.Tablet(),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               911,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-9);
    }

    @Ignore@Test
    public void testDiffPow() {
        doTest(new OptimTestUtils.DiffPow(),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               631,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
    }

    @Test
    public void testSsDiffPow() {
        doTest(new OptimTestUtils.SsDiffPow(),
               OptimTestUtils.point(DIM / 2, 1.0, 1e-1),
               GoalType.MINIMIZE,
               4000,
               new PointValuePair(OptimTestUtils.point(DIM / 2, 0.0), 0.0),
               1e-3);
    }

    @Ignore@Test
    public void testAckley() {
        doTest(new OptimTestUtils.Ackley(),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               7900,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0d),
               1e-11);
    }

    @Ignore@Test
    public void testRastrigin() {
        doTest(new OptimTestUtils.Rastrigin(),
               OptimTestUtils.point(DIM, 1.0, 2e-1),
               GoalType.MINIMIZE,
               4600,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0d),
               1e-6);
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
               Simplex.of(startPoint.length, 1),
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
        final int maxEval = Math.max(maxEvaluations, 12000);
        final SimplexOptimizer optim = new SimplexOptimizer(1e-13, 1e-14);
        final PointValuePair result = optim.optimize(new MaxEval(maxEval),
                                                     new ObjectiveFunction(func),
                                                     goal,
                                                     new InitialGuess(startPoint),
                                                     simplex,
                                                     new NelderMeadTransform());
        final String name = func.getClass().getSimpleName();

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
