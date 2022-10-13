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

package org.apache.commons.math4.legacy.fitting.leastsquares;

import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.TooManyEvaluationsException;
import org.apache.commons.math4.legacy.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math4.legacy.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math4.legacy.linear.DiagonalMatrix;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RealVector;
import org.apache.commons.math4.legacy.linear.SingularMatrixException;
import org.apache.commons.math4.legacy.optim.ConvergenceChecker;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.numbers.core.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>Some of the unit tests are re-implementations of the MINPACK <a
 * href="http://www.netlib.org/minpack/ex/file17">file17</a> and <a
 * href="http://www.netlib.org/minpack/ex/file22">file22</a> test files.
 * The redistribution policy for MINPACK is available <a
 * href="http://www.netlib.org/minpack/disclaimer">here</a>.
 *
 */
public class LevenbergMarquardtOptimizerTest
    extends AbstractLeastSquaresOptimizerAbstractTest{

    public LeastSquaresBuilder builder(BevingtonProblem problem){
        return base()
                .model(problem.getModelFunction(), problem.getModelFunctionJacobian());
    }

    public LeastSquaresBuilder builder(CircleProblem problem){
        return base()
                .model(problem.getModelFunction(), problem.getModelFunctionJacobian())
                .target(problem.target())
                .weight(new DiagonalMatrix(problem.weight()));
    }

    @Override
    public int getMaxIterations() {
        return 25;
    }

    @Override
    public LeastSquaresOptimizer getOptimizer() {
        return new LevenbergMarquardtOptimizer();
    }

    @Override
    @Test
    public void testNonInvertible() {
        try{
            /*
             * Overrides the method from parent class, since the default singularity
             * threshold (1e-14) does not trigger the expected exception.
             */
            LinearProblem problem = new LinearProblem(new double[][] {
                    {  1, 2, -3 },
                    {  2, 1,  3 },
                    { -3, 0, -9 }
            }, new double[] { 1, 1, 1 });

            final Optimum optimum = optimizer.optimize(
                    problem.getBuilder().maxIterations(20).build());

            //TODO check that it is a bad fit? Why the extra conditions?
            Assert.assertTrue(JdkMath.sqrt(problem.getTarget().length) * optimum.getRMS() > 0.6);

            optimum.getCovariances(1.5e-14);

            fail(optimizer);
        }catch (SingularMatrixException e){
            //expected
        }
    }

    @Test
    public void testControlParameters() {
        CircleVectorial circle = new CircleVectorial();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);
        checkEstimate(
                circle, 0.1, 10, 1.0e-14, 1.0e-16, 1.0e-10, false);
        checkEstimate(
                circle, 0.1, 10, 1.0e-15, 1.0e-17, 1.0e-10, false);
        checkEstimate(
                circle, 0.1,  5, 1.0e-15, 1.0e-16, 1.0e-10, true);
        circle.addPoint(300, -300);
        //wardev I changed true => false
        //TODO why should this fail? It uses 15 evaluations.
        checkEstimate(
                circle, 0.1, 20, 1.0e-18, 1.0e-16, 1.0e-10, false);
    }

    private void checkEstimate(CircleVectorial circle,
                               double initialStepBoundFactor, int maxCostEval,
                               double costRelativeTolerance, double parRelativeTolerance,
                               double orthoTolerance, boolean shouldFail) {
        try {
            final LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer()
                .withInitialStepBoundFactor(initialStepBoundFactor)
                .withCostRelativeTolerance(costRelativeTolerance)
                .withParameterRelativeTolerance(parRelativeTolerance)
                .withOrthoTolerance(orthoTolerance)
                .withRankingThreshold(Precision.SAFE_MIN);

            final LeastSquaresProblem problem = builder(circle)
                    .maxEvaluations(maxCostEval)
                    .maxIterations(100)
                    .start(new double[] { 98.680, 47.345 })
                    .build();

            optimizer.optimize(problem);

            Assert.assertTrue(!shouldFail);
            //TODO check it got the right answer
        } catch (DimensionMismatchException ee) {
            Assert.assertTrue(shouldFail);
        } catch (TooManyEvaluationsException ee) {
            Assert.assertTrue(shouldFail);
        }
    }

    /**
     * Non-linear test case: fitting of decay curve (from Chapter 8 of
     * Bevington's textbook, "Data reduction and analysis for the physical sciences").
     * XXX The expected ("reference") values may not be accurate and the tolerance too
     * relaxed for this test to be currently really useful (the issue is under
     * investigation).
     */
    @Test
    public void testBevington() {
        final double[][] dataPoints = {
            // column 1 = times
            { 15, 30, 45, 60, 75, 90, 105, 120, 135, 150,
              165, 180, 195, 210, 225, 240, 255, 270, 285, 300,
              315, 330, 345, 360, 375, 390, 405, 420, 435, 450,
              465, 480, 495, 510, 525, 540, 555, 570, 585, 600,
              615, 630, 645, 660, 675, 690, 705, 720, 735, 750,
              765, 780, 795, 810, 825, 840, 855, 870, 885, },
            // column 2 = measured counts
            { 775, 479, 380, 302, 185, 157, 137, 119, 110, 89,
              74, 61, 66, 68, 48, 54, 51, 46, 55, 29,
              28, 37, 49, 26, 35, 29, 31, 24, 25, 35,
              24, 30, 26, 28, 21, 18, 20, 27, 17, 17,
              14, 17, 24, 11, 22, 17, 12, 10, 13, 16,
              9, 9, 14, 21, 17, 13, 12, 18, 10, },
        };
        final double[] start = {10, 900, 80, 27, 225};

        final BevingtonProblem problem = new BevingtonProblem();

        final int len = dataPoints[0].length;
        final double[] weights = new double[len];
        for (int i = 0; i < len; i++) {
            problem.addPoint(dataPoints[0][i],
                             dataPoints[1][i]);

            weights[i] = 1 / dataPoints[1][i];
        }

        final Optimum optimum = optimizer.optimize(
                builder(problem)
                        .target(dataPoints[1])
                        .weight(new DiagonalMatrix(weights))
                        .start(start)
                        .maxIterations(20)
                        .build()
        );

        final RealVector solution = optimum.getPoint();
        final double[] expectedSolution = { 10.4, 958.3, 131.4, 33.9, 205.0 };

        final RealMatrix covarMatrix = optimum.getCovariances(1e-14);
        final double[][] expectedCovarMatrix = {
            { 3.38, -3.69, 27.98, -2.34, -49.24 },
            { -3.69, 2492.26, 81.89, -69.21, -8.9 },
            { 27.98, 81.89, 468.99, -44.22, -615.44 },
            { -2.34, -69.21, -44.22, 6.39, 53.80 },
            { -49.24, -8.9, -615.44, 53.8, 929.45 }
        };

        final int numParams = expectedSolution.length;

        // Check that the computed solution is within the reference error range.
        for (int i = 0; i < numParams; i++) {
            final double error = JdkMath.sqrt(expectedCovarMatrix[i][i]);
            Assert.assertEquals("Parameter " + i, expectedSolution[i], solution.getEntry(i), error);
        }

        // Check that each entry of the computed covariance matrix is within 10%
        // of the reference matrix entry.
        for (int i = 0; i < numParams; i++) {
            for (int j = 0; j < numParams; j++) {
                Assert.assertEquals("Covariance matrix [" + i + "][" + j + "]",
                                    expectedCovarMatrix[i][j],
                                    covarMatrix.getEntry(i, j),
                                    JdkMath.abs(0.1 * expectedCovarMatrix[i][j]));
            }
        }

        // Check various measures of goodness-of-fit.
        final double chi2 = optimum.getChiSquare();
        final double cost = optimum.getCost();
        final double rms = optimum.getRMS();
        final double reducedChi2 = optimum.getReducedChiSquare(start.length);

        // XXX Values computed by the CM code: It would be better to compare
        // with the results from another library.
        final double expectedChi2 = 66.07852350839286;
        final double expectedReducedChi2 = 1.2014277001525975;
        final double expectedCost = 8.128869755900439;
        final double expectedRms = 1.0582887010256337;

        final double tol = 1e-14;
        Assert.assertEquals(expectedChi2, chi2, tol);
        Assert.assertEquals(expectedReducedChi2, reducedChi2, tol);
        Assert.assertEquals(expectedCost, cost, tol);
        Assert.assertEquals(expectedRms, rms, tol);
    }

    @Test
    public void testCircleFitting2() {
        final double xCenter = 123.456;
        final double yCenter = 654.321;
        final double xSigma = 10;
        final double ySigma = 15;
        final double radius = 111.111;
        // The test is extremely sensitive to the seed.
        final RandomCirclePointGenerator factory
            = new RandomCirclePointGenerator(xCenter, yCenter, radius,
                                             xSigma, ySigma);
        final CircleProblem circle = new CircleProblem(xSigma, ySigma);

        final int numPoints = 10;
        factory.samples(numPoints).forEach(circle::addPoint);

        // First guess for the center's coordinates and radius.
        final double[] init = { 118, 659, 115 };

        final Optimum optimum = optimizer.optimize(
                builder(circle).maxIterations(50).start(init).build());

        final double[] paramFound = optimum.getPoint().toArray();

        // Retrieve errors estimation.
        final double[] asymptoticStandardErrorFound = optimum.getSigma(1e-14).toArray();

        // Check that the parameters are found within the assumed error bars.
        Assert.assertEquals(xCenter, paramFound[0], 2 * asymptoticStandardErrorFound[0]);
        Assert.assertEquals(yCenter, paramFound[1], 2 * asymptoticStandardErrorFound[1]);
        Assert.assertEquals(radius, paramFound[2], asymptoticStandardErrorFound[2]);
    }

    @Test
    public void testParameterValidator() {
        // Setup.
        final double xCenter = 123.456;
        final double yCenter = 654.321;
        final double xSigma = 10;
        final double ySigma = 15;
        final double radius = 111.111;
        final RandomCirclePointGenerator factory
            = new RandomCirclePointGenerator(xCenter, yCenter, radius,
                                             xSigma, ySigma);
        final CircleProblem circle = new CircleProblem(xSigma, ySigma);

        final int numPoints = 10;
        factory.samples(numPoints).forEach(circle::addPoint);

        // First guess for the center's coordinates and radius.
        final double[] init = { 118, 659, 115 };

        final Optimum optimum = optimizer.optimize(
                builder(circle).maxIterations(50).start(init).build());

        final int numEval = optimum.getEvaluations();
        Assert.assertTrue(numEval > 1);

        // Build a new problem with a validator that amounts to cheating.

        // Note we cannot return a fixed point.
        // The optimiser relies on computing a predicted reduction in the cost
        // function (preRed) and an actual reduction (actRed). The ratio between them must be
        // non-zero to indicate the step reduced the cost function. If a threshold is not
        // achieved then the step is rejected and the optimiser can cycle through many iterations
        // not moving anywhere until alternative thresholds reduce to a level that terminate
        // the cycle.
        // Here we take the current point and move it towards an acceptable answer
        // given the problem (the previous optimum). This should speed up the optimiser.
        // This can still fail to reduce the iterations when the adjusted step moves
        // to a sub-optimal position in the cost function.
        final ParameterValidator cheatValidator
            = new ParameterValidator() {
                    @Override
                    public RealVector validate(RealVector params) {
                        // Cheat: Move towards the optimum found previously.
                        final RealVector direction = optimum.getPoint().subtract(params);
                        return params.add(direction.mapMultiply(0.75));
                    }
                };

        final Optimum cheatOptimum
            = optimizer.optimize(builder(circle).maxIterations(50).start(init).parameterValidator(cheatValidator).build());
        final int cheatNumEval = cheatOptimum.getEvaluations();
        Assert.assertTrue("n=" + numEval + " nc=" + cheatNumEval, cheatNumEval < numEval);
        // System.out.println("n=" + numEval + " nc=" + cheatNumEval);
    }

    @Test
    public void testEvaluationCount() {
        //setup
        LeastSquaresProblem lsp = new LinearProblem(new double[][] {{1}}, new double[] {1})
                .getBuilder()
                .checker(new ConvergenceChecker<Evaluation>() {
                    @Override
                    public boolean converged(int iteration, Evaluation previous, Evaluation current) {
                        return true;
                    }
                })
                .build();

        //action
        Optimum optimum = optimizer.optimize(lsp);

        //verify
        //check iterations and evaluations are not switched.
        Assert.assertEquals(1, optimum.getIterations());
        Assert.assertEquals(2, optimum.getEvaluations());
    }
}
