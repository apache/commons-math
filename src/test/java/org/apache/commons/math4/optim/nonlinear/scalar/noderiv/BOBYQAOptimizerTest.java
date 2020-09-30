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
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.exception.TooManyEvaluationsException;
import org.apache.commons.math4.optim.InitialGuess;
import org.apache.commons.math4.optim.MaxEval;
import org.apache.commons.math4.optim.PointValuePair;
import org.apache.commons.math4.optim.SimpleBounds;
import org.apache.commons.math4.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.optim.nonlinear.scalar.ObjectiveFunction;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for {@link BOBYQAOptimizer}.
 */
public class BOBYQAOptimizerTest {
    private static final int DIM = 13;

    @Test(expected=NumberIsTooLargeException.class)
    public void testInitOutOfBounds() {
        double[] startPoint = OptimTestUtils.point(DIM, 3);
        double[][] boundaries = boundaries(DIM, -1, 2);
        doTest(new OptimTestUtils.Rosen(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 2000, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testBoundariesDimensionMismatch() {
        double[] startPoint = OptimTestUtils.point(DIM, 0.5);
        double[][] boundaries = boundaries(DIM + 1, -1, 2);
        doTest(new OptimTestUtils.Rosen(), startPoint, boundaries,
               GoalType.MINIMIZE,
               1e-13, 1e-6, 2000, null);
    }

    @Test(expected=NumberIsTooSmallException.class)
    public void testProblemDimensionTooSmall() {
        double[] startPoint = OptimTestUtils.point(1, 0.5);
        doTest(new OptimTestUtils.Rosen(), startPoint, null,
               GoalType.MINIMIZE,
               1e-13, 1e-6, 2000, null);
    }

    @Test(expected=TooManyEvaluationsException.class)
    public void testMaxEvaluations() {
        final int lowMaxEval = 2;
        double[] startPoint = OptimTestUtils.point(DIM, 0.1);
        double[][] boundaries = null;
        doTest(new OptimTestUtils.Rosen(), startPoint, boundaries,
               GoalType.MINIMIZE,
               1e-13, 1e-6, lowMaxEval, null);
     }

    @Test
    public void testRosen() {
        double[] startPoint = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected = new PointValuePair(OptimTestUtils.point(DIM,1.0),0.0);
        doTest(new OptimTestUtils.Rosen(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 2000, expected);
     }

    @Test
    public void testMaximize() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected = new PointValuePair(OptimTestUtils.point(DIM,0.0),1.0);
        doTest(new OptimTestUtils.MinusElli(), startPoint, boundaries,
                GoalType.MAXIMIZE,
                2e-10, 5e-6, 1000, expected);
        boundaries = boundaries(DIM,-0.3,0.3);
        startPoint = OptimTestUtils.point(DIM,0.1);
        doTest(new OptimTestUtils.MinusElli(), startPoint, boundaries,
                GoalType.MAXIMIZE,
                2e-10, 5e-6, 1000, expected);
    }

    @Test
    public void testEllipse() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(new OptimTestUtils.Elli(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 1000, expected);
     }

    @Test
    public void testElliRotated() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(new OptimTestUtils.ElliRotated(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-12, 1e-6, 10000, expected);
    }

    @Test
    public void testCigar() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(new OptimTestUtils.Cigar(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 100, expected);
    }

    @Test
    public void testTwoAxes() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(new OptimTestUtils.TwoAxes(), startPoint, boundaries,
                GoalType.MINIMIZE, 2*
                1e-13, 1e-6, 100, expected);
     }

    @Test
    public void testCigTab() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(new OptimTestUtils.CigTab(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 5e-5, 100, expected);
     }

    @Test
    public void testSphere() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(new OptimTestUtils.Sphere(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 100, expected);
    }

    @Test
    public void testTablet() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(new OptimTestUtils.Tablet(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 100, expected);
    }

    @Test
    public void testDiffPow() {
        double[] startPoint = OptimTestUtils.point(DIM/2,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM/2,0.0),0.0);
        doTest(new OptimTestUtils.DiffPow(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-8, 1e-1, 21000, expected);
    }

    @Test
    public void testSsDiffPow() {
        double[] startPoint = OptimTestUtils.point(DIM/2,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM/2,0.0),0.0);
        doTest(new OptimTestUtils.SsDiffPow(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-2, 1.3e-1, 50000, expected);
    }

    @Test
    public void testAckley() {
        double[] startPoint = OptimTestUtils.point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(new OptimTestUtils.Ackley(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-7, 1e-5, 1000, expected);
    }

    @Test
    public void testRastrigin() {
        double[] startPoint = OptimTestUtils.point(DIM,1.0);

        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,0.0),0.0);
        doTest(new OptimTestUtils.Rastrigin(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 1000, expected);
    }

    @Test
    public void testConstrainedRosen() {
        double[] startPoint = OptimTestUtils.point(DIM,0.1);

        double[][] boundaries = boundaries(DIM,-1,2);
        PointValuePair expected =
            new PointValuePair(OptimTestUtils.point(DIM,1.0),0.0);
        doTest(new OptimTestUtils.Rosen(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 2000, expected);
    }

    // See MATH-728
    // TODO: this test is temporarily disabled for 3.2 release as a bug in Cobertura
    //       makes it run for several hours before completing
    @Ignore @Test
    public void testConstrainedRosenWithMoreInterpolationPoints() {
        final double[] startPoint = OptimTestUtils.point(DIM, 0.1);
        final double[][] boundaries = boundaries(DIM, -1, 2);
        final PointValuePair expected = new PointValuePair(OptimTestUtils.point(DIM, 1.0), 0.0);

        // This should have been 78 because in the code the hard limit is
        // said to be
        //   ((DIM + 1) * (DIM + 2)) / 2 - (2 * DIM + 1)
        // i.e. 78 in this case, but the test fails for 48, 59, 62, 63, 64,
        // 65, 66, ...
        final int maxAdditionalPoints = 47;

        for (int num = 1; num <= maxAdditionalPoints; num++) {
            doTest(new OptimTestUtils.Rosen(), startPoint, boundaries,
                   GoalType.MINIMIZE,
                   1e-12, 1e-6, 2000,
                   num,
                   expected,
                   "num=" + num);
        }
    }

    /**
     * @param func Function to optimize.
     * @param startPoint Starting point.
     * @param boundaries Upper / lower point limit.
     * @param goal Minimization or maximization.
     * @param fTol Tolerance relative error on the objective function.
     * @param pointTol Tolerance for checking that the optimum is correct.
     * @param maxEvaluations Maximum number of evaluations.
     * @param expected Expected point / value.
     */
    private void doTest(MultivariateFunction func,
                        double[] startPoint,
                        double[][] boundaries,
                        GoalType goal,
                        double fTol,
                        double pointTol,
                        int maxEvaluations,
                        PointValuePair expected) {
        doTest(func,
               startPoint,
               boundaries,
               goal,
               fTol,
               pointTol,
               maxEvaluations,
               0,
               expected,
               "");
    }

    /**
     * @param func Function to optimize.
     * @param startPoint Starting point.
     * @param boundaries Upper / lower point limit.
     * @param goal Minimization or maximization.
     * @param fTol Tolerance relative error on the objective function.
     * @param pointTol Tolerance for checking that the optimum is correct.
     * @param maxEvaluations Maximum number of evaluations.
     * @param additionalInterpolationPoints Number of interpolation to used
     * in addition to the default (2 * dim + 1).
     * @param expected Expected point / value.
     */
    private void doTest(MultivariateFunction func,
                        double[] startPoint,
                        double[][] boundaries,
                        GoalType goal,
                        double fTol,
                        double pointTol,
                        int maxEvaluations,
                        int additionalInterpolationPoints,
                        PointValuePair expected,
                        String assertMsg) {

//         System.out.println(func.getClass().getName() + " BEGIN"); // XXX

        int dim = startPoint.length;
        final int numIterpolationPoints = 2 * dim + 1 + additionalInterpolationPoints;
        BOBYQAOptimizer optim = new BOBYQAOptimizer(numIterpolationPoints);
        PointValuePair result = boundaries == null ?
            optim.optimize(new MaxEval(maxEvaluations),
                           new ObjectiveFunction(func),
                           goal,
                           SimpleBounds.unbounded(dim),
                           new InitialGuess(startPoint)) :
            optim.optimize(new MaxEval(maxEvaluations),
                           new ObjectiveFunction(func),
                           goal,
                           new InitialGuess(startPoint),
                           new SimpleBounds(boundaries[0],
                                            boundaries[1]));
//        System.out.println(func.getClass().getName() + " = "
//              + optim.getEvaluations() + " f(");
//        for (double x: result.getPoint())  System.out.print(x + " ");
//        System.out.println(") = " +  result.getValue());
        Assert.assertEquals(assertMsg, expected.getValue(), result.getValue(), fTol);
        for (int i = 0; i < dim; i++) {
            Assert.assertEquals(expected.getPoint()[i],
                                result.getPoint()[i], pointTol);
        }

//         System.out.println(func.getClass().getName() + " END"); // XXX
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
