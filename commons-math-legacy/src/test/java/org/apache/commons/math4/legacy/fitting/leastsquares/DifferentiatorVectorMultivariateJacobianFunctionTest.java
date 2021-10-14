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

import org.apache.commons.math4.legacy.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math4.legacy.analysis.differentiation.UnivariateVectorFunctionDifferentiator;
import org.apache.commons.math4.legacy.linear.DiagonalMatrix;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RealVector;
import org.apache.commons.math4.legacy.optim.SimpleVectorValueChecker;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

public class DifferentiatorVectorMultivariateJacobianFunctionTest {

    private static final int POINTS = 20;
    private static final double STEP_SIZE = 0.2;

    private final UnivariateVectorFunctionDifferentiator differentiator = new FiniteDifferencesDifferentiator(POINTS, STEP_SIZE);
    private final LeastSquaresOptimizer optimizer = this.getOptimizer();

    public LeastSquaresBuilder base() {
        return new LeastSquaresBuilder()
                .checkerPair(new SimpleVectorValueChecker(1e-6, 1e-6))
                .maxEvaluations(100)
                .maxIterations(getMaxIterations());
    }

    public LeastSquaresBuilder builder(BevingtonProblem problem, boolean automatic){
        if(automatic) {
            return base()
                    .model(new DifferentiatorVectorMultivariateJacobianFunction(problem.getModelFunction(), differentiator));
        } else {
            return base()
                    .model(problem.getModelFunction(), problem.getModelFunctionJacobian());
        }
    }

    public int getMaxIterations() {
        return 25;
    }

    public LeastSquaresOptimizer getOptimizer() {
        return new LevenbergMarquardtOptimizer();
    }

    /**
     * Non-linear test case: fitting of decay curve (from Chapter 8 of
     * Bevington's textbook, "Data reduction and analysis for the physical sciences").
     */
    @Test
    public void testBevington() {

        // the analytical optimum to compare to
        final LeastSquaresOptimizer.Optimum analyticalOptimum = findBevington(false);
        final RealVector analyticalSolution = analyticalOptimum.getPoint();
        final RealMatrix analyticalCovarianceMatrix = analyticalOptimum.getCovariances(1e-14);

        // the automatic DifferentiatorVectorMultivariateJacobianFunction optimum
        final LeastSquaresOptimizer.Optimum automaticOptimum = findBevington(true);
        final RealVector automaticSolution = automaticOptimum.getPoint();
        final RealMatrix automaticCovarianceMatrix = automaticOptimum.getCovariances(1e-14);

        final int numParams = analyticalOptimum.getPoint().getDimension();

        // Check that the automatic solution is within the reference error range.
        for (int i = 0; i < numParams; i++) {
            final double error = JdkMath.sqrt(analyticalCovarianceMatrix.getEntry(i, i));
            Assert.assertEquals("Parameter " + i, analyticalSolution.getEntry(i), automaticSolution.getEntry(i), error);
        }

        // Check that each entry of the computed covariance matrix is within 1%
        // of the reference analytical matrix entry.
        for (int i = 0; i < numParams; i++) {
            for (int j = 0; j < numParams; j++) {
                Assert.assertEquals("Covariance matrix [" + i + "][" + j + "]",
                        analyticalCovarianceMatrix.getEntry(i, j),
                        automaticCovarianceMatrix.getEntry(i, j),
                        JdkMath.abs(0.01 * analyticalCovarianceMatrix.getEntry(i, j)));
            }
        }

        // Check various measures of goodness-of-fit.
        final double tol = 1e-40;
        Assert.assertEquals(analyticalOptimum.getChiSquare(), automaticOptimum.getChiSquare(), tol);
        Assert.assertEquals(analyticalOptimum.getCost(), automaticOptimum.getCost(), tol);
        Assert.assertEquals(analyticalOptimum.getRMS(), automaticOptimum.getRMS(), tol);
        Assert.assertEquals(analyticalOptimum.getReducedChiSquare(automaticOptimum.getPoint().getDimension()), automaticOptimum.getReducedChiSquare(automaticOptimum.getPoint().getDimension()), tol);
    }

    /**
     * Build the problem and return the optimum, doesn't actually test the results.
     *
     * Pass in if you want to test using analytical derivatives,
     * or the automatic {@link DifferentiatorVectorMultivariateJacobianFunction}
     *
     * @param automatic automatic {@link DifferentiatorVectorMultivariateJacobianFunction}, as opposed to analytical
     * @return the optimum for this test
     */
    private LeastSquaresOptimizer.Optimum findBevington(boolean automatic) {
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

        return optimizer.optimize(
                builder(problem, automatic)
                        .target(dataPoints[1])
                        .weight(new DiagonalMatrix(weights))
                        .start(start)
                        .build()
        );
    }
}
