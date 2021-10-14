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

import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.optim.InitialGuess;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.SimpleBounds;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.PopulationSize;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.TestFunction;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link CMAESOptimizer}.
 */
public class CMAESOptimizerTest {

    static final int DIM = 13;
    static final int LAMBDA = 4 + (int)(3.*JdkMath.log(DIM));

    @Test(expected = NumberIsTooLargeException.class)
    public void testInitOutofbounds1() {
        final int dim = 12;
        double[] startPoint = OptimTestUtils.point(dim, 3);
        double[] insigma = OptimTestUtils.point(dim, 0.3);
        double[][] boundaries = boundaries(dim, -1, 2);
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }
    @Test(expected = NumberIsTooSmallException.class)
    public void testInitOutofbounds2() {
        final int dim = 12;
        double[] startPoint = OptimTestUtils.point(dim, -2);
        double[] insigma = OptimTestUtils.point(dim, 0.3);
        double[][] boundaries = boundaries(dim, -1, 2);
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testBoundariesDimensionMismatch() {
        final int dim = 12;
        double[] startPoint = OptimTestUtils.point(dim, 0.5);
        double[] insigma = OptimTestUtils.point(dim, 0.3);
        double[][] boundaries = boundaries(dim + 1,-1,2);
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test(expected = NotPositiveException.class)
    public void testInputSigmaNegative() {
        final int dim = 12;
        double[] startPoint = OptimTestUtils.point(dim, 0.5);
        double[] insigma = OptimTestUtils.point(dim, -0.5);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test(expected = OutOfRangeException.class)
    public void testInputSigmaOutOfRange() {
        final int dim = 12;
        double[] startPoint = OptimTestUtils.point(dim, 0.5);
        double[] insigma = OptimTestUtils.point(dim, 1.1);
        double[][] boundaries = boundaries(dim, -0.5,0.5);
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testInputSigmaDimensionMismatch() {
        final int dim = 12;
        double[] startPoint = OptimTestUtils.point(dim, 0.5);
        double[] insigma = OptimTestUtils.point(dim + 1, 0.5);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test
    public void testRosen() {
        final int dim = 12;
        double[] startPoint = OptimTestUtils.point(dim, 0.1);
        double[] insigma = OptimTestUtils.point(dim, 0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, false, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test
    public void testMaximize() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),1.0);
        doTest(TestFunction.MINUS_ELLI.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MAXIMIZE, LAMBDA, true, 0, 1.0-1e-13,
                2e-10, 5e-6, 100000, expected);
        doTest(TestFunction.MINUS_ELLI.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MAXIMIZE, LAMBDA, false, 0, 1.0-1e-13,
                2e-10, 5e-6, 100000, expected);
        boundaries = boundaries(DIM,-0.3,0.3);
        startPoint = OptimTestUtils.point(DIM,0.1);
        doTest(TestFunction.MINUS_ELLI.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MAXIMIZE, LAMBDA, true, 0, 1.0-1e-13,
                2e-10, 5e-6, 100000, expected);
    }

    @Test
    public void testMath1466() {
        final CMAESOptimizer optimizer
            = new CMAESOptimizer(30000, Double.NEGATIVE_INFINITY, true, 10,
                                 0, RandomSource.MT_64.create(), false, null);
        final MultivariateFunction fitnessFunction = new MultivariateFunction() {
                @Override
                public double value(double[] x) {
                    return x[0] * x[0] - 100;
                }
            };

        final double[] start = { 100 };
        final double[] sigma = { 1e-1 };
        final double[] result = optimizer.optimize(new MaxEval(10000),
                                                   new ObjectiveFunction(fitnessFunction),
                                                   SimpleBounds.unbounded(1),
                                                   GoalType.MINIMIZE,
                                                   new PopulationSize(5),
                                                   new CMAESOptimizer.Sigma(sigma),
                                                   new InitialGuess(start)).getPoint();
        Assert.assertEquals(0, result[0], 1e-7);
    }

    @Test
    public void testEllipse() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(TestFunction.ELLI.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
        doTest(TestFunction.ELLI.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, false, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test
    public void testElliRotated() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(new OptimTestUtils.ElliRotated(), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
        doTest(new OptimTestUtils.ElliRotated(), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, false, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test
    public void testCigar() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(TestFunction.CIGAR.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 200000, expected);
        doTest(TestFunction.CIGAR.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, false, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test
    public void testCigarWithBoundaries() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = boundaries(DIM, -1e100, Double.POSITIVE_INFINITY);
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(TestFunction.CIGAR.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 200000, expected);
        doTest(TestFunction.CIGAR.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, false, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test
    public void testTwoAxes() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(TestFunction.TWO_AXES.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, 2*LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 200000, expected);
        doTest(TestFunction.TWO_AXES.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, 2*LAMBDA, false, 0, 1e-13,
                1e-8, 1e-3, 200000, expected);
    }

    @Test
    public void testCigTab() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,0.3);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(TestFunction.CIG_TAB.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 5e-5, 100000, expected);
        doTest(TestFunction.CIG_TAB.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, false, 0, 1e-13,
                1e-13, 5e-5, 100000, expected);
    }

    @Test
    public void testSphere() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(TestFunction.SPHERE.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
        doTest(TestFunction.SPHERE.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, false, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test
    public void testTablet() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(TestFunction.TABLET.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
        doTest(TestFunction.TABLET.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, false, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test
    public void testSumPow() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(TestFunction.SUM_POW.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, 10, true, 0, 1e-13,
                1e-8, 1e-1, 100000, expected);
        doTest(TestFunction.SUM_POW.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, 10, false, 0, 1e-13,
                1e-8, 2e-1, 100000, expected);
    }

    @Test
    public void testAckley() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[] insigma = OptimTestUtils.point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(TestFunction.ACKLEY.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, 2*LAMBDA, true, 0, 1e-13,
                1e-9, 1e-5, 100000, expected);
        doTest(TestFunction.ACKLEY.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, 2*LAMBDA, false, 0, 1e-13,
                1e-9, 1e-5, 100000, expected);
    }

    @Test
    public void testRastrigin() {
        double[] startPoint = OptimTestUtils.point(DIM,0.1);
        double[] insigma = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(TestFunction.RASTRIGIN.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, (int)(200*JdkMath.sqrt(DIM)), true, 0, 1e-13,
                1e-13, 1e-6, 200000, expected);
        doTest(TestFunction.RASTRIGIN.withDimension(DIM), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, (int)(200*JdkMath.sqrt(DIM)), false, 0, 1e-13,
                1e-13, 1e-6, 200000, expected);
    }

    @Test
    public void testConstrainedRosen() {
        final int dim = 12;
        double[] startPoint = OptimTestUtils.point(dim, 0.1);
        double[] insigma = OptimTestUtils.point(dim, 0.1);
        double[][] boundaries = boundaries(dim, -1, 2);
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, 2*LAMBDA, true, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, 2*LAMBDA, false, 0, 1e-13,
                1e-13, 1e-6, 100000, expected);
    }

    @Test
    public void testDiagonalRosen() {
        final int dim = 12;
        double[] startPoint = OptimTestUtils.point(dim, 0.1);
        double[] insigma = OptimTestUtils.point(dim, 0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(dim, 1.0), 0.0);
        doTest(TestFunction.ROSENBROCK.withDimension(dim), startPoint, insigma, boundaries,
                GoalType.MINIMIZE, LAMBDA, false, 1, 1e-13,
                1e-10, 1e-4, 1000000, expected);
     }

    @Test
    public void testMath864() {
        final CMAESOptimizer optimizer
            = new CMAESOptimizer(30000, 0, true, 10,
                                 0, RandomSource.MT_64.create(), false, null);
        final MultivariateFunction fitnessFunction = new MultivariateFunction() {
                @Override
                public double value(double[] parameters) {
                    final double target = 1;
                    final double error = target - parameters[0];
                    return error * error;
                }
            };

        final double[] start = { 0 };
        final double[] lower = { -1e6 };
        final double[] upper = { 1.5 };
        final double[] sigma = { 1e-1 };
        final double[] result = optimizer.optimize(new MaxEval(10000),
                                                   new ObjectiveFunction(fitnessFunction),
                                                   GoalType.MINIMIZE,
                                                   new PopulationSize(5),
                                                   new CMAESOptimizer.Sigma(sigma),
                                                   new InitialGuess(start),
                                                   new SimpleBounds(lower, upper)).getPoint();
        Assert.assertTrue("Out of bounds (" + result[0] + " > " + upper[0] + ")",
                          result[0] <= upper[0]);
    }

    /**
     * Cf. MATH-867
     */
    @Test
    public void testFitAccuracyDependsOnBoundary() {
        final CMAESOptimizer optimizer
            = new CMAESOptimizer(30000, 0, true, 10,
                                 0, RandomSource.MT_64.create(), false, null);
        final MultivariateFunction fitnessFunction = new MultivariateFunction() {
                @Override
                public double value(double[] parameters) {
                    final double target = 11.1;
                    final double error = target - parameters[0];
                    return error * error;
                }
            };

        final double[] start = { 1 };

        // No bounds.
        PointValuePair result = optimizer.optimize(new MaxEval(100000),
                                                   new ObjectiveFunction(fitnessFunction),
                                                   GoalType.MINIMIZE,
                                                   SimpleBounds.unbounded(1),
                                                   new PopulationSize(5),
                                                   new CMAESOptimizer.Sigma(new double[] { 1e-1 }),
                                                   new InitialGuess(start));
        final double resNoBound = result.getPoint()[0];

        // Optimum is near the lower bound.
        final double[] lower = { -20 };
        final double[] upper = { 5e16 };
        final double[] sigma = { 10 };
        result = optimizer.optimize(new MaxEval(100000),
                                    new ObjectiveFunction(fitnessFunction),
                                    GoalType.MINIMIZE,
                                    new PopulationSize(5),
                                    new CMAESOptimizer.Sigma(sigma),
                                    new InitialGuess(start),
                                    new SimpleBounds(lower, upper));
        final double resNearLo = result.getPoint()[0];

        // Optimum is near the upper bound.
        lower[0] = -5e16;
        upper[0] = 20;
        result = optimizer.optimize(new MaxEval(100000),
                                    new ObjectiveFunction(fitnessFunction),
                                    GoalType.MINIMIZE,
                                    new PopulationSize(5),
                                    new CMAESOptimizer.Sigma(sigma),
                                    new InitialGuess(start),
                                    new SimpleBounds(lower, upper));
        final double resNearHi = result.getPoint()[0];

        // System.out.println("resNoBound=" + resNoBound +
        //                    " resNearLo=" + resNearLo +
        //                    " resNearHi=" + resNearHi);

        // The two values currently differ by a substantial amount, indicating that
        // the bounds definition can prevent reaching the optimum.
        Assert.assertEquals(resNoBound, resNearLo, 1e-3);
        Assert.assertEquals(resNoBound, resNearHi, 1e-3);
    }

    /**
     * @param func Function to optimize.
     * @param startPoint Starting point.
     * @param inSigma Individual input sigma.
     * @param boundaries Upper / lower point limit.
     * @param goal Minimization or maximization.
     * @param lambda Population size used for offspring.
     * @param isActive Covariance update mechanism.
     * @param diagonalOnly Simplified covariance update.
     * @param stopValue Termination criteria for optimization.
     * @param fTol Tolerance relative error on the objective function.
     * @param pointTol Tolerance for checking that the optimum is correct.
     * @param maxEvaluations Maximum number of evaluations.
     * @param expected Expected point / value.
     */
    private void doTest(MultivariateFunction func,
                        double[] startPoint,
                        double[] inSigma,
                        double[][] boundaries,
                        GoalType goal,
                        int lambda,
                        boolean isActive,
                        int diagonalOnly,
                        double stopValue,
                        double fTol,
                        double pointTol,
                        int maxEvaluations,
                        PointValuePair expected) {
        int dim = startPoint.length;
        // test diagonalOnly = 0 - slow but normally fewer feval#
        CMAESOptimizer optim = new CMAESOptimizer(30000, stopValue, isActive, diagonalOnly,
                                                  0, RandomSource.MT_64.create(), false, null);
        PointValuePair result = boundaries == null ?
            optim.optimize(new MaxEval(maxEvaluations),
                           new ObjectiveFunction(func),
                           goal,
                           new InitialGuess(startPoint),
                           SimpleBounds.unbounded(dim),
                           new CMAESOptimizer.Sigma(inSigma),
                           new PopulationSize(lambda)) :
            optim.optimize(new MaxEval(maxEvaluations),
                           new ObjectiveFunction(func),
                           goal,
                           new SimpleBounds(boundaries[0],
                                            boundaries[1]),
                           new InitialGuess(startPoint),
                           new CMAESOptimizer.Sigma(inSigma),
                           new PopulationSize(lambda));

        Assert.assertEquals(expected.getValue(), result.getValue(), fTol);
        for (int i = 0; i < dim; i++) {
            Assert.assertEquals(expected.getPoint()[i], result.getPoint()[i], pointTol);
        }

        Assert.assertTrue(optim.getIterations() > 0);
    }

    private static double[][] boundaries(int dim,
            double lower, double upper) {
        double[][] boundaries = new double[2][dim];
        for (int i = 0; i < dim; i++) {
            boundaries[0][i] = lower;
        }
        for (int i = 0; i < dim; i++) {
            boundaries[1][i] = upper;
        }
        return boundaries;
    }
}
