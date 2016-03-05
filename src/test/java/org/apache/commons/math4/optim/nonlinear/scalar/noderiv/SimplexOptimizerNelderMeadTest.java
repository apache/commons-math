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
import org.apache.commons.math4.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math4.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class SimplexOptimizerNelderMeadTest {
    @Test(expected=MathUnsupportedOperationException.class)
    public void testBoundsUnsupported() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final FourExtrema fourExtrema = new FourExtrema();

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
        final FourExtrema fourExtrema = new FourExtrema();

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
        final FourExtrema fourExtrema = new FourExtrema();

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
        final FourExtrema fourExtrema = new FourExtrema();

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
        final FourExtrema fourExtrema = new FourExtrema();

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

        Rosenbrock rosenbrock = new Rosenbrock();
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

        Assert.assertEquals(rosenbrock.getCount(), optimizer.getEvaluations());
        Assert.assertTrue(optimizer.getEvaluations() > 40);
        Assert.assertTrue(optimizer.getEvaluations() < 50);
        Assert.assertTrue(optimum.getValue() < 8e-4);
    }

    @Test
    public void testPowell() {
        Powell powell = new Powell();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        PointValuePair optimum =
            optimizer.optimize(new MaxEval(200),
                               new ObjectiveFunction(powell),
                               GoalType.MINIMIZE,
                               new InitialGuess(new double[] { 3, -1, 0, 1 }),
                               new NelderMeadSimplex(4));
        Assert.assertEquals(powell.getCount(), optimizer.getEvaluations());
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
        Powell powell = new Powell();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        optimizer.optimize(new MaxEval(20),
                           new ObjectiveFunction(powell),
                           GoalType.MINIMIZE,
                           new InitialGuess(new double[] { 3, -1, 0, 1 }),
                           new NelderMeadSimplex(4));
    }

    private static class FourExtrema implements MultivariateFunction {
        // The following function has 4 local extrema.
        final double xM = -3.841947088256863675365;
        final double yM = -1.391745200270734924416;
        final double xP =  0.2286682237349059125691;
        final double yP = -yM;
        final double valueXmYm = 0.2373295333134216789769; // Local maximum.
        final double valueXmYp = -valueXmYm; // Local minimum.
        final double valueXpYm = -0.7290400707055187115322; // Global minimum.
        final double valueXpYp = -valueXpYm; // Global maximum.

        public double value(double[] variables) {
            final double x = variables[0];
            final double y = variables[1];
            return (x == 0 || y == 0) ? 0 :
                FastMath.atan(x) * FastMath.atan(x + 2) * FastMath.atan(y) * FastMath.atan(y) / (x * y);
        }
    }

    private static class Rosenbrock implements MultivariateFunction {
        private int count;

        public Rosenbrock() {
            count = 0;
        }

        public double value(double[] x) {
            ++count;
            double a = x[1] - x[0] * x[0];
            double b = 1.0 - x[0];
            return 100 * a * a + b * b;
        }

        public int getCount() {
            return count;
        }
    }

    private static class Powell implements MultivariateFunction {
        private int count;

        public Powell() {
            count = 0;
        }

        public double value(double[] x) {
            ++count;
            double a = x[0] + 10 * x[1];
            double b = x[2] - x[3];
            double c = x[1] - 2 * x[2];
            double d = x[0] - x[3];
            return a * a + 5 * b * b + c * c * c * c + 10 * d * d * d * d;
        }

        public int getCount() {
            return count;
        }
    }
}
