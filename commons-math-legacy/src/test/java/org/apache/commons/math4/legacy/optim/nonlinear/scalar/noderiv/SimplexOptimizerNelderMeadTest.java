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
import org.apache.commons.math4.legacy.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.legacy.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.optim.InitialGuess;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.LeastSquaresConverter;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

/**
 * Tests for {@link NelderMeadTransform}.
 */
public class SimplexOptimizerNelderMeadTest {
    private static final int DIM = 13;

    @Test
    public void testLeastSquares1() {
        final RealMatrix factors
            = new Array2DRowRealMatrix(new double[][] {
                    { 1, 0 },
                    { 0, 1 }
                }, false);
        LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorFunction() {
                @Override
                public double[] value(double[] variables) {
                    return factors.operate(variables);
                }
            }, new double[] { 2.0, -3.0 });
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-6);
        PointValuePair optimum =
            optimizer.optimize(new MaxEval(200),
                               new ObjectiveFunction(ls),
                               GoalType.MINIMIZE,
                               new InitialGuess(new double[] { 10, 10 }),
                               Simplex.equalSidesAlongAxes(2, 1d),
                               new NelderMeadTransform());
        Assert.assertEquals( 2, optimum.getPointRef()[0], 1e-3);
        Assert.assertEquals(-3, optimum.getPointRef()[1], 4e-4);
        final int nEval = optimizer.getEvaluations();
        Assert.assertTrue("nEval=" + nEval, nEval > 60);
        Assert.assertTrue("nEval=" + nEval, nEval < 80);
        Assert.assertTrue(optimum.getValue() < 1.0e-6);
    }

    @Test
    public void testLeastSquares2() {
        final RealMatrix factors
            = new Array2DRowRealMatrix(new double[][] {
                    { 1, 0 },
                    { 0, 1 }
                }, false);
        LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorFunction() {
                @Override
                public double[] value(double[] variables) {
                    return factors.operate(variables);
                }
            }, new double[] { 2, -3 }, new double[] { 10, 0.1 });
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-6);
        PointValuePair optimum =
            optimizer.optimize(new MaxEval(200),
                               new ObjectiveFunction(ls),
                               GoalType.MINIMIZE,
                               new InitialGuess(new double[] { 10, 10 }),
                               Simplex.equalSidesAlongAxes(2, 1d),
                               new NelderMeadTransform());
        Assert.assertEquals( 2, optimum.getPointRef()[0], 1e-4);
        Assert.assertEquals(-3, optimum.getPointRef()[1], 8e-4);
        final int nEval = optimizer.getEvaluations();
        Assert.assertTrue("nEval=" + nEval, nEval > 70);
        Assert.assertTrue("nEval=" + nEval, nEval < 85);
        Assert.assertTrue(optimum.getValue() < 1e-6);
    }

    @Test
    public void testLeastSquares3() {
        final RealMatrix factors =
            new Array2DRowRealMatrix(new double[][] {
                    { 1, 0 },
                    { 0, 1 }
                }, false);
        LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorFunction() {
                @Override
                public double[] value(double[] variables) {
                    return factors.operate(variables);
                }
            }, new double[] { 2, -3 }, new Array2DRowRealMatrix(new double [][] {
                    { 1, 1.2 }, { 1.2, 2 }
                }));
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-6);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(200),
                                 new ObjectiveFunction(ls),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 10, 10 }),
                                 Simplex.equalSidesAlongAxes(2, 1d),
                                 new NelderMeadTransform());
        Assert.assertEquals( 2, optimum.getPointRef()[0], 1e-2);
        Assert.assertEquals(-3, optimum.getPointRef()[1], 1e-2);
        final int nEval = optimizer.getEvaluations();
        Assert.assertTrue("nEval=" + nEval, nEval > 60);
        Assert.assertTrue("nEval=" + nEval, nEval < 80);
        Assert.assertTrue(optimum.getValue() < 1e-6);
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
               7467,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
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
               Simplex.equalSidesAlongAxes(startPoint.length, 1d),
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

        final int maxEval = Math.max(maxEvaluations, 12000);
        final SimplexOptimizer optim = new SimplexOptimizer(1e-13, 1e-14);
        final PointValuePair result = optim.optimize(new MaxEval(maxEval),
                                                     new ObjectiveFunction(func),
                                                     goal,
                                                     new InitialGuess(startPoint),
                                                     simplex,
                                                     new NelderMeadTransform());

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
