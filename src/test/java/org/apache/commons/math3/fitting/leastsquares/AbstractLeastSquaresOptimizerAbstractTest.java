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
package org.apache.commons.math3.fitting.leastsquares;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Some of the unit tests are re-implementations of the MINPACK
 * <a href="http://www.netlib.org/minpack/ex/file17">file17</a> and
 * <a href="http://www.netlib.org/minpack/ex/file22">file22</a> test files.
 * The redistribution policy for MINPACK is available
 * <a href="http://www.netlib.org/minpack/disclaimer">here</a>.
 *
 * <T> Concrete implementation of an optimizer.
 *
 * @version $Id$
 */
public abstract class AbstractLeastSquaresOptimizerAbstractTest<T extends AbstractLeastSquaresOptimizer<T>> {
    /**
     * @return a concrete optimizer.
     */
    public abstract T createOptimizer();

    /**
     * @return the default number of allowed iterations (which will be
     * used when not specified otherwise).
     */
    public abstract int getMaxIterations();

    @Test
    public void testShallowCopy() {
        final int maxEval1 = 12;
        final int maxIter1 = 23;
        final double[] target1 = { 3.4 };
        final double[] weight1 = { 4.5 };
        final double[] start1 = { 5.6 };
        final double factor1 = 6.7;
        final MultivariateVectorFunction model1 = new MultivariateVectorFunction() {
                public double[] value(double[] point) {
                    return new double[] {
                        factor1 * factor1 * point[0]
                    };
                }};
        final MultivariateMatrixFunction jac1 = new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) {
                        return new double[][] {
                            { 2 * factor1 * point[0] }
                        };
                    }
                };


        final T optim1 = createOptimizer()
            .withMaxEvaluations(maxEval1)
            .withMaxIterations(maxIter1)
            .withTarget(target1)
            .withWeight(new DiagonalMatrix(weight1))
            .withStartPoint(start1)
            .withModelAndJacobian(model1, jac1);

        final T optim2 = optim1.shallowCopy();

        // Check that all fields have the same values.
        Assert.assertTrue(optim1.getMaxEvaluations() == optim2.getMaxEvaluations());
        Assert.assertTrue(optim1.getMaxIterations() == optim2.getMaxIterations());
        Assert.assertTrue(optim1.getTarget()[0] == optim2.getTarget()[0]);
        Assert.assertTrue(optim1.getWeight().getEntry(0, 0) == optim2.getWeight().getEntry(0, 0));
        Assert.assertTrue(optim1.getStart()[0] == optim2.getStart()[0]);
        Assert.assertTrue(optim1.getModel().value(new double[] {32})[0] == optim2.getModel().value(new double[] {32})[0]);
        Assert.assertTrue(optim1.getJacobian().value(new double[] {54})[0][0] == optim2.getJacobian().value(new double[] {54})[0][0]);

        // Change "optim2".
        final int maxEval2 = 122;
        final int maxIter2 = 232;
        final double[] target2 = { 3.42 };
        final double[] weight2 = { 4.52 };
        final double[] start2 = { 5.62 };
        final double factor2 = 6.72;
        final MultivariateVectorFunction model2 = new MultivariateVectorFunction() {
                public double[] value(double[] point) {
                    return new double[] {
                        factor2 * factor2 * point[0]
                    };
                }};
        final MultivariateMatrixFunction jac2 = new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) {
                        return new double[][] {
                            { 2 * factor2 * point[0] }
                        };
                    }
                };

        optim2
            .withMaxEvaluations(maxEval2)
            .withMaxIterations(maxIter2)
            .withTarget(target2)
            .withWeight(new DiagonalMatrix(weight2))
            .withStartPoint(start2)
            .withModelAndJacobian(model2, jac2);

        // Check that all fields now have different values.
        Assert.assertFalse(optim1.getMaxEvaluations() == optim2.getMaxEvaluations());
        Assert.assertFalse(optim1.getMaxIterations() == optim2.getMaxIterations());
        Assert.assertFalse(optim1.getTarget()[0] == optim2.getTarget()[0]);
        Assert.assertFalse(optim1.getWeight().getEntry(0, 0) == optim2.getWeight().getEntry(0, 0));
        Assert.assertFalse(optim1.getStart()[0] == optim2.getStart()[0]);
        Assert.assertFalse(optim1.getModel().value(new double[] {32})[0] == optim2.getModel().value(new double[] {32})[0]);
        Assert.assertFalse(optim1.getJacobian().value(new double[] {54})[0][0] == optim2.getJacobian().value(new double[] {54})[0][0]);
    }

    @Test
    public void testGetIterations() {
        T optim = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withTarget(new double[] { 1 })
            .withWeight(new DiagonalMatrix(new double[] { 1 }))
            .withStartPoint(new double[] { 3 })
            .withModelAndJacobian(new MultivariateVectorFunction() {
                    public double[] value(double[] point) {
                        return new double[] {
                            FastMath.pow(point[0], 4)
                        };
                    }},
                new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) {
                        return new double[][] {
                            { 0.25 * FastMath.pow(point[0], 3) }
                        };
                    }
                });

        optim.optimize();
        Assert.assertTrue(optim.getIterations() > 0);
    }

    @Test
    public void testTrivial() {
        LinearProblem problem
            = new LinearProblem(new double[][] { { 2 } },
                                new double[] { 3 });
        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1 }))
            .withStartPoint(new double[] { 0 });

        PointVectorValuePair optimum = optimizer.optimize();

        Assert.assertEquals(0, optimizer.computeRMS(optimum.getPoint()), 1e-10);
        Assert.assertEquals(1.5, optimum.getPoint()[0], 1e-10);
        Assert.assertEquals(3.0, optimum.getValue()[0], 1e-10);
    }

    @Test
    public void testQRColumnsPermutation() {
        LinearProblem problem
            = new LinearProblem(new double[][] { { 1, -1 }, { 0, 2 }, { 1, -2 } },
                                new double[] { 4, 6, 1 });

        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1 }))
            .withStartPoint(new double[] { 0, 0 });

        PointVectorValuePair optimum = optimizer.optimize();

        Assert.assertEquals(0, optimizer.computeRMS(optimum.getPoint()), 1e-10);
        Assert.assertEquals(7, optimum.getPoint()[0], 1e-10);
        Assert.assertEquals(3, optimum.getPoint()[1], 1e-10);
        Assert.assertEquals(4, optimum.getValue()[0], 1e-10);
        Assert.assertEquals(6, optimum.getValue()[1], 1e-10);
        Assert.assertEquals(1, optimum.getValue()[2], 1e-10);
    }

    @Test
    public void testNoDependency() {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 2, 0, 0, 0, 0, 0 },
                { 0, 2, 0, 0, 0, 0 },
                { 0, 0, 2, 0, 0, 0 },
                { 0, 0, 0, 2, 0, 0 },
                { 0, 0, 0, 0, 2, 0 },
                { 0, 0, 0, 0, 0, 2 }
        }, new double[] { 0, 1.1, 2.2, 3.3, 4.4, 5.5 });
        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1, 1, 1, 1 }))
            .withStartPoint(new double[] { 0, 0, 0, 0, 0, 0 });

        double[] optimum = optimizer.optimize().getPoint();
        Assert.assertEquals(0, optimizer.computeRMS(optimum), 1e-10);
        for (int i = 0; i < problem.target.length; ++i) {
            Assert.assertEquals(0.55 * i, optimum[i], 1e-10);
        }
    }

    @Test
    public void testOneSet() {
        LinearProblem problem = new LinearProblem(new double[][] {
                {  1,  0, 0 },
                { -1,  1, 0 },
                {  0, -1, 1 }
        }, new double[] { 1, 1, 1});

        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1 }))
            .withStartPoint(new double[] { 0, 0, 0 });

        double[] optimum = optimizer.optimize().getPoint();
        Assert.assertEquals(0, optimizer.computeRMS(optimum), 1e-10);
        Assert.assertEquals(1, optimum[0], 1e-10);
        Assert.assertEquals(2, optimum[1], 1e-10);
        Assert.assertEquals(3, optimum[2], 1e-10);
    }

    @Test
    public void testTwoSets() {
        double epsilon = 1e-7;
        LinearProblem problem = new LinearProblem(new double[][] {
                {  2,  1,   0,  4,       0, 0 },
                { -4, -2,   3, -7,       0, 0 },
                {  4,  1,  -2,  8,       0, 0 },
                {  0, -3, -12, -1,       0, 0 },
                {  0,  0,   0,  0, epsilon, 1 },
                {  0,  0,   0,  0,       1, 1 }
        }, new double[] { 2, -9, 2, 2, 1 + epsilon * epsilon, 2});

        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1, 1, 1, 1 }))
            .withStartPoint(new double[] { 0, 0, 0, 0, 0, 0 });

        double[] optimum = optimizer.optimize().getPoint();

        Assert.assertEquals(0, optimizer.computeRMS(optimum), 1e-10);
        Assert.assertEquals(3, optimum[0], 1e-10);
        Assert.assertEquals(4, optimum[1], 1e-10);
        Assert.assertEquals(-1, optimum[2], 1e-10);
        Assert.assertEquals(-2, optimum[3], 1e-10);
        Assert.assertEquals(1 + epsilon, optimum[4], 1e-10);
        Assert.assertEquals(1 - epsilon, optimum[5], 1e-10);
    }

    @Test(expected=ConvergenceException.class)
    public void testNonInvertible() throws Exception {
        LinearProblem problem = new LinearProblem(new double[][] {
                {  1, 2, -3 },
                {  2, 1,  3 },
                { -3, 0, -9 }
        }, new double[] { 1, 1, 1 });

        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1 }))
            .withStartPoint(new double[] { 0, 0, 0 });

        optimizer.optimize();
    }

    @Test
    public void testIllConditioned() {
        LinearProblem problem1 = new LinearProblem(new double[][] {
                { 10, 7,  8,  7 },
                {  7, 5,  6,  5 },
                {  8, 6, 10,  9 },
                {  7, 5,  9, 10 }
        }, new double[] { 32, 23, 33, 31 });
        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem1.getModelFunction(),
                                  problem1.getModelFunctionJacobian())
            .withTarget(problem1.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1, 1 }))
            .withStartPoint(new double[] { 0, 1, 2, 3 });

        double[] optimum = optimizer.optimize().getPoint();

        Assert.assertEquals(0, optimizer.computeRMS(optimum), 1e-10);
        Assert.assertEquals(1, optimum[0], 1e-10);
        Assert.assertEquals(1, optimum[1], 1e-10);
        Assert.assertEquals(1, optimum[2], 1e-10);
        Assert.assertEquals(1, optimum[3], 1e-10);

        LinearProblem problem2 = new LinearProblem(new double[][] {
                { 10.00, 7.00, 8.10, 7.20 },
                {  7.08, 5.04, 6.00, 5.00 },
                {  8.00, 5.98, 9.89, 9.00 },
                {  6.99, 4.99, 9.00, 9.98 }
        }, new double[] { 32, 23, 33, 31 });

        optimizer = optimizer
            .withModelAndJacobian(problem2.getModelFunction(),
                                  problem2.getModelFunctionJacobian())
            .withTarget(problem2.getTarget());

        optimum = optimizer.optimize().getPoint();

        Assert.assertEquals(0, optimizer.computeRMS(optimum), 1e-10);
        Assert.assertEquals(-81, optimum[0], 1e-8);
        Assert.assertEquals(137, optimum[1], 1e-8);
        Assert.assertEquals(-34, optimum[2], 1e-8);
        Assert.assertEquals( 22, optimum[3], 1e-8);
    }

    @Test
    public void testMoreEstimatedParametersSimple() {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 3, 2,  0, 0 },
                { 0, 1, -1, 1 },
                { 2, 0,  1, 0 }
        }, new double[] { 7, 3, 5 });

        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1 }))
            .withStartPoint(new double[] { 7, 6, 5, 4 });

        double[] optimum = optimizer.optimize().getPoint();
        Assert.assertEquals(0, optimizer.computeRMS(optimum), 1e-10);
    }

    @Test
    public void testMoreEstimatedParametersUnsorted() {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 1, 1,  0,  0, 0,  0 },
                { 0, 0,  1,  1, 1,  0 },
                { 0, 0,  0,  0, 1, -1 },
                { 0, 0, -1,  1, 0,  1 },
                { 0, 0,  0, -1, 1,  0 }
       }, new double[] { 3, 12, -1, 7, 1 });

        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1, 1, 1 }))
            .withStartPoint(new double[] { 2, 2, 2, 2, 2, 2 });

        double[] optimum = optimizer.optimize().getPoint();

        Assert.assertEquals(0, optimizer.computeRMS(optimum), 1e-10);
        Assert.assertEquals(3, optimum[2], 1e-10);
        Assert.assertEquals(4, optimum[3], 1e-10);
        Assert.assertEquals(5, optimum[4], 1e-10);
        Assert.assertEquals(6, optimum[5], 1e-10);
    }

    @Test
    public void testRedundantEquations() {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 1,  1 },
                { 1, -1 },
                { 1,  3 }
        }, new double[] { 3, 1, 5 });

        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1 }))
            .withStartPoint(new double[] { 1, 1 });

        double[] optimum = optimizer.optimize().getPoint();

        Assert.assertEquals(0, optimizer.computeRMS(optimum), 1e-10);
        Assert.assertEquals(2, optimum[0], 1e-10);
        Assert.assertEquals(1, optimum[1], 1e-10);
    }

    @Test
    public void testInconsistentEquations() {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 1,  1 },
                { 1, -1 },
                { 1,  3 }
        }, new double[] { 3, 1, 4 });

        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1 }))
            .withStartPoint(new double[] { 1, 1 });

        double[] optimum = optimizer.optimize().getPoint();

        Assert.assertTrue(optimizer.computeRMS(optimum) > 0.1);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testInconsistentSizes1() {
        LinearProblem problem
            = new LinearProblem(new double[][] { { 1, 0 },
                                                 { 0, 1 } },
                                new double[] { -1, 1 });
        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1 }))
            .withStartPoint(new double[] { 0, 0 });

        double[] optimum = optimizer.optimize().getPoint();

        Assert.assertEquals(0, optimizer.computeRMS(optimum), 1e-10);
        Assert.assertEquals(-1, optimum[0], 1e-10);
        Assert.assertEquals(1, optimum[1], 1e-10);

        optimizer.withWeight(new DiagonalMatrix(new double[] { 1 })).optimize();
    }

    @Test(expected=DimensionMismatchException.class)
    public void testInconsistentSizes2() {
        LinearProblem problem
            = new LinearProblem(new double[][] { { 1, 0 }, { 0, 1 } },
                                new double[] { -1, 1 });

        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(problem.getTarget())
            .withWeight(new DiagonalMatrix(new double[] { 1, 1 }))
            .withStartPoint(new double[] { 0, 0 });

        double[] optimum = optimizer.optimize().getPoint();

        Assert.assertEquals(0, optimizer.computeRMS(optimum), 1e-10);
        Assert.assertEquals(-1, optimum[0], 1e-10);
        Assert.assertEquals(1, optimum[1], 1e-10);

        optimizer
            .withTarget(new double[] { 1 })
            .withWeight(new DiagonalMatrix(new double[] { 1 }))
            .optimize();
    }

    @Test
    public void testCircleFitting() {
        CircleVectorial circle = new CircleVectorial();
        circle.addPoint( 30,  68);
        circle.addPoint( 50,  -6);
        circle.addPoint(110, -20);
        circle.addPoint( 35,  15);
        circle.addPoint( 45,  97);

        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(circle.getModelFunction(),
                                  circle.getModelFunctionJacobian())
            .withTarget(new double[] { 0, 0, 0, 0, 0 })
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1, 1, 1 }))
            .withStartPoint(new double[] { 98.680, 47.345 });

        double[] optimum = optimizer.optimize().getPoint();
        Assert.assertTrue(optimizer.getEvaluations() < 10);

        double rms = optimizer.computeRMS(optimum);
        Assert.assertEquals(1.768262623567235,  FastMath.sqrt(circle.getN()) * rms, 1e-10);

        Vector2D center = new Vector2D(optimum[0], optimum[1]);
        Assert.assertEquals(69.96016176931406, circle.getRadius(center), 1e-6);
        Assert.assertEquals(96.07590211815305, center.getX(), 1e-6);
        Assert.assertEquals(48.13516790438953, center.getY(), 1e-6);

        double[][] cov = optimizer.computeCovariances(optimum, 1e-14);
        Assert.assertEquals(1.839, cov[0][0], 0.001);
        Assert.assertEquals(0.731, cov[0][1], 0.001);
        Assert.assertEquals(cov[0][1], cov[1][0], 1e-14);
        Assert.assertEquals(0.786, cov[1][1], 0.001);

        // add perfect measurements and check errors are reduced
        double  r = circle.getRadius(center);
        for (double d= 0; d < 2 * FastMath.PI; d += 0.01) {
            circle.addPoint(center.getX() + r * FastMath.cos(d), center.getY() + r * FastMath.sin(d));
        }

        double[] target = new double[circle.getN()];
        Arrays.fill(target, 0);
        double[] weights = new double[circle.getN()];
        Arrays.fill(weights, 2);
        optimizer = optimizer.withTarget(target).withWeight(new DiagonalMatrix(weights));
        optimum = optimizer.optimize().getPoint();

        cov = optimizer.computeCovariances(optimum, 1e-14);
        Assert.assertEquals(0.0016, cov[0][0], 0.001);
        Assert.assertEquals(3.2e-7, cov[0][1], 1e-9);
        Assert.assertEquals(cov[0][1], cov[1][0], 1e-14);
        Assert.assertEquals(0.0016, cov[1][1], 0.001);
    }

    @Test
    public void testCircleFittingBadInit() {
        CircleVectorial circle = new CircleVectorial();
        double[][] points = circlePoints;
        double[] target = new double[points.length];
        Arrays.fill(target, 0);
        double[] weights = new double[points.length];
        Arrays.fill(weights, 2);
        for (int i = 0; i < points.length; ++i) {
            circle.addPoint(points[i][0], points[i][1]);
        }
        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(circle.getModelFunction(),
                                  circle.getModelFunctionJacobian())
            .withTarget(target)
            .withWeight(new DiagonalMatrix(weights))
            .withStartPoint(new double[] { -12, -12 });

        double[] optimum = optimizer.optimize().getPoint();

        Vector2D center = new Vector2D(optimum[0], optimum[1]);
        Assert.assertTrue(optimizer.getEvaluations() < 25);
        Assert.assertEquals( 0.043, optimizer.computeRMS(optimum), 1e-3);
        Assert.assertEquals( 0.292235,  circle.getRadius(center), 1e-6);
        Assert.assertEquals(-0.151738,  center.getX(), 1e-6);
        Assert.assertEquals( 0.2075001, center.getY(), 1e-6);
    }

    @Test
    public void testCircleFittingGoodInit() {
        CircleVectorial circle = new CircleVectorial();
        double[][] points = circlePoints;
        double[] target = new double[points.length];
        Arrays.fill(target, 0);
        double[] weights = new double[points.length];
        Arrays.fill(weights, 2);
        for (int i = 0; i < points.length; ++i) {
            circle.addPoint(points[i][0], points[i][1]);
        }
        T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(circle.getModelFunction(),
                                  circle.getModelFunctionJacobian())
            .withTarget(target)
            .withWeight(new DiagonalMatrix(weights))
            .withStartPoint(new double[] { 0, 0 });

        double[] optimum = optimizer.optimize().getPoint();

        Assert.assertEquals(-0.1517383071957963, optimum[0], 1e-6);
        Assert.assertEquals(0.2074999736353867,  optimum[1], 1e-6);
        Assert.assertEquals(0.04268731682389561, optimizer.computeRMS(optimum), 1e-8);
    }

    private final double[][] circlePoints = new double[][] {
        {-0.312967,  0.072366}, {-0.339248,  0.132965}, {-0.379780,  0.202724},
        {-0.390426,  0.260487}, {-0.361212,  0.328325}, {-0.346039,  0.392619},
        {-0.280579,  0.444306}, {-0.216035,  0.470009}, {-0.149127,  0.493832},
        {-0.075133,  0.483271}, {-0.007759,  0.452680}, { 0.060071,  0.410235},
        { 0.103037,  0.341076}, { 0.118438,  0.273884}, { 0.131293,  0.192201},
        { 0.115869,  0.129797}, { 0.072223,  0.058396}, { 0.022884,  0.000718},
        {-0.053355, -0.020405}, {-0.123584, -0.032451}, {-0.216248, -0.032862},
        {-0.278592, -0.005008}, {-0.337655,  0.056658}, {-0.385899,  0.112526},
        {-0.405517,  0.186957}, {-0.415374,  0.262071}, {-0.387482,  0.343398},
        {-0.347322,  0.397943}, {-0.287623,  0.458425}, {-0.223502,  0.475513},
        {-0.135352,  0.478186}, {-0.061221,  0.483371}, { 0.003711,  0.422737},
        { 0.065054,  0.375830}, { 0.108108,  0.297099}, { 0.123882,  0.222850},
        { 0.117729,  0.134382}, { 0.085195,  0.056820}, { 0.029800, -0.019138},
        {-0.027520, -0.072374}, {-0.102268, -0.091555}, {-0.200299, -0.106578},
        {-0.292731, -0.091473}, {-0.356288, -0.051108}, {-0.420561,  0.014926},
        {-0.471036,  0.074716}, {-0.488638,  0.182508}, {-0.485990,  0.254068},
        {-0.463943,  0.338438}, {-0.406453,  0.404704}, {-0.334287,  0.466119},
        {-0.254244,  0.503188}, {-0.161548,  0.495769}, {-0.075733,  0.495560},
        { 0.001375,  0.434937}, { 0.082787,  0.385806}, { 0.115490,  0.323807},
        { 0.141089,  0.223450}, { 0.138693,  0.131703}, { 0.126415,  0.049174},
        { 0.066518, -0.010217}, {-0.005184, -0.070647}, {-0.080985, -0.103635},
        {-0.177377, -0.116887}, {-0.260628, -0.100258}, {-0.335756, -0.056251},
        {-0.405195, -0.000895}, {-0.444937,  0.085456}, {-0.484357,  0.175597},
        {-0.472453,  0.248681}, {-0.438580,  0.347463}, {-0.402304,  0.422428},
        {-0.326777,  0.479438}, {-0.247797,  0.505581}, {-0.152676,  0.519380},
        {-0.071754,  0.516264}, { 0.015942,  0.472802}, { 0.076608,  0.419077},
        { 0.127673,  0.330264}, { 0.159951,  0.262150}, { 0.153530,  0.172681},
        { 0.140653,  0.089229}, { 0.078666,  0.024981}, { 0.023807, -0.037022},
        {-0.048837, -0.077056}, {-0.127729, -0.075338}, {-0.221271, -0.067526}
    };

    public void doTestStRD(final StatisticalReferenceDataset dataset,
                           final double errParams,
                           final double errParamsSd) {
        final double[] w = new double[dataset.getNumObservations()];
        Arrays.fill(w, 1);

        final double[][] data = dataset.getData();
        final double[] initial = dataset.getStartingPoint(0);
        final StatisticalReferenceDataset.LeastSquaresProblem problem = dataset.getLeastSquaresProblem();

        final T optimizer = createOptimizer()
            .withMaxEvaluations(100)
            .withMaxIterations(getMaxIterations())
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(data[1])
            .withWeight(new DiagonalMatrix(w))
            .withStartPoint(initial);

        final double[] actual = optimizer.optimize().getPoint();
        for (int i = 0; i < actual.length; i++) {
            double expected = dataset.getParameter(i);
            double delta = FastMath.abs(errParams * expected);
            Assert.assertEquals(dataset.getName() + ", param #" + i,
                                expected, actual[i], delta);
        }
    }

    @Test
    public void testKirby2() throws IOException {
        doTestStRD(StatisticalReferenceDatasetFactory.createKirby2(), 1E-7, 1E-7);
    }

    @Test
    public void testHahn1() throws IOException {
        doTestStRD(StatisticalReferenceDatasetFactory.createHahn1(), 1E-7, 1E-4);
    }

    static class LinearProblem {
        private final RealMatrix factors;
        private final double[] target;

        public LinearProblem(double[][] factors, double[] target) {
            this.factors = new BlockRealMatrix(factors);
            this.target  = target;
        }

        public double[] getTarget() {
            return target;
        }

        public MultivariateVectorFunction getModelFunction() {
            return new MultivariateVectorFunction() {
                public double[] value(double[] params) {
                    return factors.operate(params);
                }
            };
        }

        public MultivariateMatrixFunction getModelFunctionJacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] params) {
                    return factors.getData();
                }
            };
        }
    }
}
