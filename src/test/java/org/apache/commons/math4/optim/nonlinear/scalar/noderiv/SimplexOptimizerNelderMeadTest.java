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
import org.apache.commons.math4.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.exception.TooManyEvaluationsException;
import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.optim.InitialGuess;
import org.apache.commons.math4.optim.MaxEval;
import org.apache.commons.math4.optim.PointValuePair;
import org.apache.commons.math4.optim.SimpleBounds;
import org.apache.commons.math4.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.optim.nonlinear.scalar.LeastSquaresConverter;
import org.apache.commons.math4.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

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
                           new NelderMeadSimplex(new double[] { 0.2, 0.2 }),
                           new SimpleBounds(new double[] { -5, -1 },
                                            new double[] { 5, 1 }));
    }

    @Test
    public void testMinimize1() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final OptimTestUtils.FourExtrema fourExtrema = new OptimTestUtils.FourExtrema();

        final PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 new ObjectiveFunction(fourExtrema),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { -3, 0 }),
                                 new NelderMeadSimplex(new double[] { 0.2, 0.2 }));
        Assert.assertEquals(fourExtrema.xM, optimum.getPoint()[0], 2e-7);
        Assert.assertEquals(fourExtrema.yP, optimum.getPoint()[1], 2e-5);
        Assert.assertEquals(fourExtrema.valueXmYp, optimum.getValue(), 6e-12);
        Assert.assertTrue(optimizer.getEvaluations() > 60);
        Assert.assertTrue(optimizer.getEvaluations() < 90);

        // Check that the number of iterations is updated (MATH-949).
        Assert.assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testMinimize2() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final OptimTestUtils.FourExtrema fourExtrema = new OptimTestUtils.FourExtrema();

        final PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 new ObjectiveFunction(fourExtrema),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 1, 0 }),
                                 new NelderMeadSimplex(new double[] { 0.2, 0.2 }));
        Assert.assertEquals(fourExtrema.xP, optimum.getPoint()[0], 5e-6);
        Assert.assertEquals(fourExtrema.yM, optimum.getPoint()[1], 6e-6);
        Assert.assertEquals(fourExtrema.valueXpYm, optimum.getValue(), 1e-11);
        Assert.assertTrue(optimizer.getEvaluations() > 60);
        Assert.assertTrue(optimizer.getEvaluations() < 90);

        // Check that the number of iterations is updated (MATH-949).
        Assert.assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testMaximize1() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final OptimTestUtils.FourExtrema fourExtrema = new OptimTestUtils.FourExtrema();

        final PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 new ObjectiveFunction(fourExtrema),
                                 GoalType.MAXIMIZE,
                                 new InitialGuess(new double[] { -3, 0 }),
                                 new NelderMeadSimplex(new double[] { 0.2, 0.2 }));
        Assert.assertEquals(fourExtrema.xM, optimum.getPoint()[0], 1e-5);
        Assert.assertEquals(fourExtrema.yM, optimum.getPoint()[1], 3e-6);
        Assert.assertEquals(fourExtrema.valueXmYm, optimum.getValue(), 3e-12);
        Assert.assertTrue(optimizer.getEvaluations() > 60);
        Assert.assertTrue(optimizer.getEvaluations() < 90);

        // Check that the number of iterations is updated (MATH-949).
        Assert.assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testMaximize2() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final OptimTestUtils.FourExtrema fourExtrema = new OptimTestUtils.FourExtrema();

        final PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 new ObjectiveFunction(fourExtrema),
                                 GoalType.MAXIMIZE,
                                 new InitialGuess(new double[] { 1, 0 }),
                                 new NelderMeadSimplex(new double[] { 0.2, 0.2 }));
        Assert.assertEquals(fourExtrema.xP, optimum.getPoint()[0], 4e-6);
        Assert.assertEquals(fourExtrema.yP, optimum.getPoint()[1], 5e-6);
        Assert.assertEquals(fourExtrema.valueXpYp, optimum.getValue(), 7e-12);
        Assert.assertTrue(optimizer.getEvaluations() > 60);
        Assert.assertTrue(optimizer.getEvaluations() < 90);

        // Check that the number of iterations is updated (MATH-949).
        Assert.assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testRosenbrock() {

        OptimTestUtils.Rosenbrock rosenbrock = new OptimTestUtils.Rosenbrock();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        PointValuePair optimum
        = optimizer.optimize(new MaxEval(100),
                             new ObjectiveFunction(rosenbrock),
                             GoalType.MINIMIZE,
                             new InitialGuess(new double[] { -1.2, 1 }),
                                new NelderMeadSimplex(new double[][] {
                                        { -1.2,  1 },
                                        { 0.9, 1.2 },
                                        {  3.5, -2.3 } }));
        Assert.assertTrue(optimizer.getEvaluations() > 40);
        Assert.assertTrue(optimizer.getEvaluations() < 50);
        Assert.assertTrue(optimum.getValue() < 8e-4);
    }

    @Test
    public void testPowell() {
        OptimTestUtils.Powell powell = new OptimTestUtils.Powell();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        PointValuePair optimum =
            optimizer.optimize(new MaxEval(200),
                               new ObjectiveFunction(powell),
                               GoalType.MINIMIZE,
                               new InitialGuess(new double[] { 3, -1, 0, 1 }),
                               new NelderMeadSimplex(4));
        Assert.assertTrue(optimizer.getEvaluations() > 110);
        Assert.assertTrue(optimizer.getEvaluations() < 130);
        Assert.assertTrue(optimum.getValue() < 2e-3);
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
                               new NelderMeadSimplex(2));
        Assert.assertEquals( 2, optimum.getPointRef()[0], 3e-5);
        Assert.assertEquals(-3, optimum.getPointRef()[1], 4e-4);
        Assert.assertTrue(optimizer.getEvaluations() > 60);
        Assert.assertTrue(optimizer.getEvaluations() < 80);
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
                               new NelderMeadSimplex(2));
        Assert.assertEquals( 2, optimum.getPointRef()[0], 5e-5);
        Assert.assertEquals(-3, optimum.getPointRef()[1], 8e-4);
        Assert.assertTrue(optimizer.getEvaluations() > 60);
        Assert.assertTrue(optimizer.getEvaluations() < 80);
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
                                 new NelderMeadSimplex(2));
        Assert.assertEquals( 2, optimum.getPointRef()[0], 2e-3);
        Assert.assertEquals(-3, optimum.getPointRef()[1], 8e-4);
        Assert.assertTrue(optimizer.getEvaluations() > 60);
        Assert.assertTrue(optimizer.getEvaluations() < 80);
        Assert.assertTrue(optimum.getValue() < 1e-6);
    }

    @Test(expected=TooManyEvaluationsException.class)
    public void testMaxIterations() {
        OptimTestUtils.Powell powell = new OptimTestUtils.Powell();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        optimizer.optimize(new MaxEval(20),
                           new ObjectiveFunction(powell),
                           GoalType.MINIMIZE,
                           new InitialGuess(new double[] { 3, -1, 0, 1 }),
                           new NelderMeadSimplex(4));
    }

    @Test
    public void testRosen() {
        doTest(new OptimTestUtils.Rosen(),
               OptimTestUtils.point(DIM, 0.1),
               GoalType.MINIMIZE,
               11975,
               new PointValuePair(OptimTestUtils.point(DIM, 1.0), 0.0),
               1e-6);
    }

    @Ignore
    @Test
    public void testEllipse() {
        doTest(new OptimTestUtils.Elli(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               7184,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
     }

    @Ignore
    @Test
    public void testElliRotated() {
        doTest(new OptimTestUtils.ElliRotated(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               7467,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
    }

    @Test
    public void testCigar() {
        doTest(new OptimTestUtils.Cigar(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               9160,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-6);
    }

    @Ignore
    @Test
    public void testTwoAxes() {
        doTest(new OptimTestUtils.TwoAxes(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               3451,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
     }

    @Ignore
    @Test
    public void testCigTab() {
        doTest(new OptimTestUtils.CigTab(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               7454,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
     }

    @Ignore
    @Test
    public void testSphere() {
        doTest(new OptimTestUtils.Sphere(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               3881,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-6);
    }

    @Ignore
    @Test
    public void testTablet() {
        doTest(new OptimTestUtils.Tablet(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               6639,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
    }

    @Ignore
    @Test
    public void testDiffPow() {
        doTest(new OptimTestUtils.DiffPow(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               4105,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               1e-14);
    }

    @Ignore
    @Test
    public void testSsDiffPow() {
        doTest(new OptimTestUtils.SsDiffPow(),
               OptimTestUtils.point(DIM / 2, 1.0),
               GoalType.MINIMIZE,
               3990,
               new PointValuePair(OptimTestUtils.point(DIM / 2, 0.0), 0.0),
               1e-15);
    }

    @Ignore
    @Test
    public void testAckley() {
        doTest(new OptimTestUtils.Ackley(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               2849,
               new PointValuePair(OptimTestUtils.point(DIM, 0.0), 0.0),
               0);
    }

    @Ignore
    @Test
    public void testRastrigin() {
        doTest(new OptimTestUtils.Rastrigin(),
               OptimTestUtils.point(DIM, 1.0),
               GoalType.MINIMIZE,
               2166,
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
        final SimplexOptimizer optim = new SimplexOptimizer(1e-13, 1e-14);
        final PointValuePair result = optim.optimize(new MaxEval(maxEvaluations),
                                                     new ObjectiveFunction(func),
                                                     goal,
                                                     new InitialGuess(startPoint),
                                                     new NelderMeadSimplex(dim, 0.1));
        final double dist = MathArrays.distance(expected.getPoint(),
                                                result.getPoint());
        Assert.assertEquals(0d, dist, tol);
    }
}
