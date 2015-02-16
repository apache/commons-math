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

package org.apache.commons.math3.optim.nonlinear.vector.jacobian;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.vector.Target;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunctionJacobian;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>Some of the unit tests are re-implementations of the MINPACK <a
 * href="http://www.netlib.org/minpack/ex/file17">file17</a> and <a
 * href="http://www.netlib.org/minpack/ex/file22">file22</a> test files.
 * The redistribution policy for MINPACK is available <a
 * href="http://www.netlib.org/minpack/disclaimer">here</a>, for
 * convenience, it is reproduced below.</p>

 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>
 *    Minpack Copyright Notice (1999) University of Chicago.
 *    All rights reserved
 * </td></tr>
 * <tr><td>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <ol>
 *  <li>Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.</li>
 * <li>The end-user documentation included with the redistribution, if any,
 *     must include the following acknowledgment:
 *     <code>This product includes software developed by the University of
 *           Chicago, as Operator of Argonne National Laboratory.</code>
 *     Alternately, this acknowledgment may appear in the software itself,
 *     if and wherever such third-party acknowledgments normally appear.</li>
 * <li><strong>WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED "AS IS"
 *     WITHOUT WARRANTY OF ANY KIND. THE COPYRIGHT HOLDER, THE
 *     UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, AND
 *     THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES
 *     OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE
 *     OR NON-INFRINGEMENT, (2) DO NOT ASSUME ANY LEGAL LIABILITY
 *     OR RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR
 *     USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE OF
 *     THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4)
 *     DO NOT WARRANT THAT THE SOFTWARE WILL FUNCTION
 *     UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT ANY ERRORS WILL
 *     BE CORRECTED.</strong></li>
 * <li><strong>LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT
 *     HOLDER, THE UNITED STATES, THE UNITED STATES DEPARTMENT OF
 *     ENERGY, OR THEIR EMPLOYEES: BE LIABLE FOR ANY INDIRECT,
 *     INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OF
 *     ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF
 *     PROFITS OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER
 *     SUCH LIABILITY IS ASSERTED ON THE BASIS OF CONTRACT, TORT
 *     (INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR OTHERWISE,
 *     EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE
 *     POSSIBILITY OF SUCH LOSS OR DAMAGES.</strong></li>
 * <ol></td></tr>
 * </table>

 * @author Argonne National Laboratory. MINPACK project. March 1980 (original fortran minpack tests)
 * @author Burton S. Garbow (original fortran minpack tests)
 * @author Kenneth E. Hillstrom (original fortran minpack tests)
 * @author Jorge J. More (original fortran minpack tests)
 * @author Luc Maisonobe (non-minpack tests and minpack tests Java translation)
 */
@Deprecated
public class LevenbergMarquardtOptimizerTest
    extends AbstractLeastSquaresOptimizerAbstractTest {
    @Override
    public AbstractLeastSquaresOptimizer createOptimizer() {
        return new LevenbergMarquardtOptimizer();
    }

    @Test(expected=MathUnsupportedOperationException.class)
    public void testConstraintsUnsupported() {
        createOptimizer().optimize(new MaxEval(100),
                                   new Target(new double[] { 2 }),
                                   new Weight(new double[] { 1 }),
                                   new InitialGuess(new double[] { 1, 2 }),
                                   new SimpleBounds(new double[] { -10, 0 },
                                                    new double[] { 20, 30 }));
    }

    @Override
    @Test(expected=SingularMatrixException.class)
    public void testNonInvertible() {
        /*
         * Overrides the method from parent class, since the default singularity
         * threshold (1e-14) does not trigger the expected exception.
         */
        LinearProblem problem = new LinearProblem(new double[][] {
                {  1, 2, -3 },
                {  2, 1,  3 },
                { -3, 0, -9 }
        }, new double[] { 1, 1, 1 });

        AbstractLeastSquaresOptimizer optimizer = createOptimizer();
        PointVectorValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getModelFunction(),
                                 problem.getModelFunctionJacobian(),
                                 problem.getTarget(),
                                 new Weight(new double[] { 1, 1, 1 }),
                                 new InitialGuess(new double[] { 0, 0, 0 }));
        Assert.assertTrue(FastMath.sqrt(optimizer.getTargetSize()) * optimizer.getRMS() > 0.6);

        optimizer.computeCovariances(optimum.getPoint(), 1.5e-14);
    }

    @Test
    public void testControlParameters() {
        CircleVectorial circle = new CircleVectorial();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);
        checkEstimate(circle.getModelFunction(),
                      circle.getModelFunctionJacobian(),
                      0.1, 10, 1.0e-14, 1.0e-16, 1.0e-10, false);
        checkEstimate(circle.getModelFunction(),
                      circle.getModelFunctionJacobian(),
                      0.1, 10, 1.0e-15, 1.0e-17, 1.0e-10, true);
        checkEstimate(circle.getModelFunction(),
                      circle.getModelFunctionJacobian(),
                      0.1,  5, 1.0e-15, 1.0e-16, 1.0e-10, true);
        circle.addPoint(300, -300);
        checkEstimate(circle.getModelFunction(),
                      circle.getModelFunctionJacobian(),
                      0.1, 20, 1.0e-18, 1.0e-16, 1.0e-10, true);
    }

    private void checkEstimate(ModelFunction problem,
                               ModelFunctionJacobian problemJacobian,
                               double initialStepBoundFactor, int maxCostEval,
                               double costRelativeTolerance, double parRelativeTolerance,
                               double orthoTolerance, boolean shouldFail) {
        try {
            LevenbergMarquardtOptimizer optimizer
                = new LevenbergMarquardtOptimizer(initialStepBoundFactor,
                                                  costRelativeTolerance,
                                                  parRelativeTolerance,
                                                  orthoTolerance,
                                                  Precision.SAFE_MIN);
            optimizer.optimize(new MaxEval(maxCostEval),
                               problem,
                               problemJacobian,
                               new Target(new double[] { 0, 0, 0, 0, 0 }),
                               new Weight(new double[] { 1, 1, 1, 1, 1 }),
                               new InitialGuess(new double[] { 98.680, 47.345 }));
            Assert.assertTrue(!shouldFail);
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

        final BevingtonProblem problem = new BevingtonProblem();

        final int len = dataPoints[0].length;
        final double[] weights = new double[len];
        for (int i = 0; i < len; i++) {
            problem.addPoint(dataPoints[0][i],
                             dataPoints[1][i]);

            weights[i] = 1 / dataPoints[1][i];
        }

        final LevenbergMarquardtOptimizer optimizer
            = new LevenbergMarquardtOptimizer();

        final PointVectorValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getModelFunction(),
                                 problem.getModelFunctionJacobian(),
                                 new Target(dataPoints[1]),
                                 new Weight(weights),
                                 new InitialGuess(new double[] { 10, 900, 80, 27, 225 }));

        final double[] solution = optimum.getPoint();
        final double[] expectedSolution = { 10.4, 958.3, 131.4, 33.9, 205.0 };

        final double[][] covarMatrix = optimizer.computeCovariances(solution, 1e-14);
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
            final double error = FastMath.sqrt(expectedCovarMatrix[i][i]);
            Assert.assertEquals("Parameter " + i, expectedSolution[i], solution[i], error);
        }

        // Check that each entry of the computed covariance matrix is within 10%
        // of the reference matrix entry.
        for (int i = 0; i < numParams; i++) {
            for (int j = 0; j < numParams; j++) {
                Assert.assertEquals("Covariance matrix [" + i + "][" + j + "]",
                                    expectedCovarMatrix[i][j],
                                    covarMatrix[i][j],
                                    FastMath.abs(0.1 * expectedCovarMatrix[i][j]));
            }
        }
    }

    @Test
    public void testCircleFitting2() {
        final double xCenter = 123.456;
        final double yCenter = 654.321;
        final double xSigma = 10;
        final double ySigma = 15;
        final double radius = 111.111;
        // The test is extremely sensitive to the seed.
        final long seed = 59421061L;
        final RandomCirclePointGenerator factory
            = new RandomCirclePointGenerator(xCenter, yCenter, radius,
                                             xSigma, ySigma,
                                             seed);
        final CircleProblem circle = new CircleProblem(xSigma, ySigma);

        final int numPoints = 10;
        for (Vector2D p : factory.generate(numPoints)) {
            circle.addPoint(p.getX(), p.getY());
        }

        // First guess for the center's coordinates and radius.
        final double[] init = { 90, 659, 115 };

        final LevenbergMarquardtOptimizer optimizer
            = new LevenbergMarquardtOptimizer();
        final PointVectorValuePair optimum = optimizer.optimize(new MaxEval(100),
                                                                circle.getModelFunction(),
                                                                circle.getModelFunctionJacobian(),
                                                                new Target(circle.target()),
                                                                new Weight(circle.weight()),
                                                                new InitialGuess(init));

        final double[] paramFound = optimum.getPoint();

        // Retrieve errors estimation.
        final double[] asymptoticStandardErrorFound = optimizer.computeSigma(paramFound, 1e-14);

        // Check that the parameters are found within the assumed error bars.
        Assert.assertEquals(xCenter, paramFound[0], asymptoticStandardErrorFound[0]);
        Assert.assertEquals(yCenter, paramFound[1], asymptoticStandardErrorFound[1]);
        Assert.assertEquals(radius, paramFound[2], asymptoticStandardErrorFound[2]);
    }

    private static class BevingtonProblem {
        private List<Double> time;
        private List<Double> count;

        public BevingtonProblem() {
            time = new ArrayList<Double>();
            count = new ArrayList<Double>();
        }

        public void addPoint(double t, double c) {
            time.add(t);
            count.add(c);
        }

        public ModelFunction getModelFunction() {
            return new ModelFunction(new MultivariateVectorFunction() {
                    public double[] value(double[] params) {
                        double[] values = new double[time.size()];
                        for (int i = 0; i < values.length; ++i) {
                            final double t = time.get(i);
                            values[i] = params[0] +
                                params[1] * FastMath.exp(-t / params[3]) +
                                params[2] * FastMath.exp(-t / params[4]);
                        }
                        return values;
                    }
                });
        }

        public ModelFunctionJacobian getModelFunctionJacobian() {
            return new ModelFunctionJacobian(new MultivariateMatrixFunction() {
                    public double[][] value(double[] params) {
                        double[][] jacobian = new double[time.size()][5];

                        for (int i = 0; i < jacobian.length; ++i) {
                            final double t = time.get(i);
                            jacobian[i][0] = 1;

                            final double p3 =  params[3];
                            final double p4 =  params[4];
                            final double tOp3 = t / p3;
                            final double tOp4 = t / p4;
                            jacobian[i][1] = FastMath.exp(-tOp3);
                            jacobian[i][2] = FastMath.exp(-tOp4);
                            jacobian[i][3] = params[1] * FastMath.exp(-tOp3) * tOp3 / p3;
                            jacobian[i][4] = params[2] * FastMath.exp(-tOp4) * tOp4 / p4;
                        }
                        return jacobian;
                    }
                });
        }
    }
}
