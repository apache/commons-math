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
import org.apache.commons.math4.legacy.optim.InitialGuess;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

/**
 * Tests for {@link MultiDirectionalTransform}.
 */
public class SimplexOptimizerMultiDirectionalTest {
    private static final int DIM = 13;

    @Test
    public void testMath283() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-14, 1e-14);
        final Gaussian2D function = new Gaussian2D(0, 0, 1);
        PointValuePair estimate = optimizer.optimize(new MaxEval(1000),
                                                     new ObjectiveFunction(function),
                                                     GoalType.MAXIMIZE,
                                                     new InitialGuess(function.getMaximumPosition()),
                                                     Simplex.equalSidesAlongAxes(2, 1d),
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
               Simplex.alongAxes(OptimTestUtils.point(2, 0.2, 1e-2)),
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
               Simplex.alongAxes(OptimTestUtils.point(2, 0.2, 1e-2)),
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
               Simplex.alongAxes(OptimTestUtils.point(2, 0.2, 1e-2)),
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
               Simplex.alongAxes(OptimTestUtils.point(2, 0.2, 1e-2)),
               new PointValuePair(new double[] {f.xP, f.yP}, f.valueXpYp),
               1e-6);
    }

    @Ignore("See MATH-1552")@Test
    public void testElliRotated() {
        doTest(new OptimTestUtils.ElliRotated(),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               911,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-9);
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
               Simplex.equalSidesAlongAxes(startPoint.length, 1),
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
        final String name = func.toString();

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

    private static class Gaussian2D implements MultivariateFunction {
        private final double[] maximumPosition;
        private final double std;

        Gaussian2D(double xOpt, double yOpt, double std) {
            maximumPosition = new double[] { xOpt, yOpt };
            this.std = std;
        }

        public double getMaximum() {
            return value(maximumPosition);
        }

        public double[] getMaximumPosition() {
            return maximumPosition.clone();
        }

        @Override
        public double value(double[] point) {
            final double x = point[0];
            final double y = point[1];
            final double twoS2 = 2.0 * std * std;
            return 1.0 / (twoS2 * JdkMath.PI) * JdkMath.exp(-(x * x + y * y) / twoS2);
        }
    }
}
