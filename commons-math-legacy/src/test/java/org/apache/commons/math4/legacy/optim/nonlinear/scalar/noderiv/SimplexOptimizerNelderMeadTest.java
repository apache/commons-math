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
import org.apache.commons.math4.legacy.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.legacy.exception.TooManyEvaluationsException;
import org.apache.commons.math4.legacy.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.optim.InitialGuess;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.SimpleBounds;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.LeastSquaresConverter;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.TestFunction;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

/**
 * Tests for {@link NelderMeadTransform}.
 */
public class SimplexOptimizerNelderMeadTest {
    private static final int DIM = 13;

    @Test(expected=MathUnsupportedOperationException.class)
    public void testBoundsUnsupported() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final OptimTestUtils.FourExtrema fourExtrema = new OptimTestUtils.FourExtrema();

        optimizer.optimize(new MaxEval(100),
                           new ObjectiveFunction(fourExtrema),
                           GoalType.MINIMIZE,
                           new InitialGuess(new double[] { -3, 0 }),
                           Simplex.alongAxes(new double[] { 0.2, 0.2 }),
                           new NelderMeadTransform(),
                           new SimpleBounds(new double[] { -5, -1 },
                                            new double[] { 5, 1 }));
    }

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

    @Test(expected=TooManyEvaluationsException.class)
    public void testMaxIterations() {
        final int dim = 4;
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        optimizer.optimize(new MaxEval(20),
                           new ObjectiveFunction(TestFunction.POWELL.withDimension(dim)),
                           GoalType.MINIMIZE,
                           new InitialGuess(new double[] { 3, -1, 0, 1 }),
                           Simplex.equalSidesAlongAxes(dim, 1d),
                           new NelderMeadTransform());
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

    @Test
    public void testRosenbrock() {
        final int dim = 2;
        doTest(TestFunction.ROSENBROCK.withDimension(dim),
               OptimTestUtils.point(new double[] { -1.2, 1 }, 1e-1),
               GoalType.MINIMIZE,
               180,
               new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0),
               1e-4);
    }

    @Test
    public void testPowell() {
        final int dim = 4;
        doTest(TestFunction.POWELL.withDimension(dim),
               OptimTestUtils.point(new double[] { 3, -1, 0, 1 }, 1e-1),
               GoalType.MINIMIZE,
               420,
               new PointValuePair(OptimTestUtils.point(dim, 0.0), 0.0),
               2e-4);
    }

    @Test
    public void testRosen() {
        doTest(TestFunction.ROSEN.withDimension(DIM),
               OptimTestUtils.point(DIM, 0.1),
               GoalType.MINIMIZE,
               9078,
               new PointValuePair(OptimTestUtils.point(DIM, 1.0), 0.0),
               1e-6);
    }

    @Ignore("See MATH-1552")@Test
    public void testEllipse() {
        doTest(TestFunction.ELLI.withDimension(DIM),
               OptimTestUtils.point(DIM, 1.0, 2e-1),
               GoalType.MINIMIZE,
               15000,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
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

    @Test
    public void testCigar() {
        doTest(TestFunction.CIGAR.withDimension(DIM),
               OptimTestUtils.point(DIM, 1.0, 2e-1),
               GoalType.MINIMIZE,
               7000,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-6);
    }

    @Ignore("See MATH-1552")@Test
    public void testTwoAxes() {
        doTest(TestFunction.TWO_AXES.withDimension(DIM),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               3451,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
    }

    @Ignore("See MATH-1552")@Test
    public void testCigTab() {
        doTest(TestFunction.CIG_TAB.withDimension(DIM),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               7000,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-4);
    }

    @Test
    public void testSphere() {
        doTest(TestFunction.SPHERE.withDimension(DIM),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               3000,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-6);
    }

    @Ignore("See MATH-1552")@Test
    public void testTablet() {
        doTest(TestFunction.TABLET.withDimension(DIM),
               OptimTestUtils.point(DIM, 1.0, 1e-1),
               GoalType.MINIMIZE,
               10000,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
    }

    @Ignore("See MATH-1552")@Test
    public void testDiffPow() {
        doTest(TestFunction.DIFF_POW.withDimension(DIM),
               OptimTestUtils.point(DIM, 1.0, 2e-1),
               GoalType.MINIMIZE,
               7000,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-6);
    }

    @Test
    public void testSsDiffPow() {
        final int dim = DIM / 2;
        doTest(TestFunction.SS_DIFF_POW.withDimension(dim),
               OptimTestUtils.point(dim, 1.0, 2e-1),
               GoalType.MINIMIZE,
               4000,
               new PointValuePair(OptimTestUtils.point(dim, 0.0), 0.0),
               1e-3);
    }

    @Ignore("See MATH-1552")@Test
    public void testAckley() {
        doTest(TestFunction.ACKLEY.withDimension(DIM),
               OptimTestUtils.point(DIM, 1.0, 2e-1),
               GoalType.MINIMIZE,
               7900,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-11);
    }

    @Ignore("See MATH-1552")@Test
    public void testRastrigin() {
        doTest(TestFunction.RASTRIGIN.withDimension(DIM),
               OptimTestUtils.point(DIM, 1.0, 2e-1),
               GoalType.MINIMIZE,
               4600,
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
