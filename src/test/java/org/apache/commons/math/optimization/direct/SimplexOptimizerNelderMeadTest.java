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

package org.apache.commons.math.optimization.direct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.exception.TooManyEvaluationsException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.LeastSquaresConverter;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;

public class SimplexOptimizerNelderMeadTest {
    @Test
    public void testMinimizeMaximize() {

        // the following function has 4 local extrema:
        final double xM        = -3.841947088256863675365;
        final double yM        = -1.391745200270734924416;
        final double xP        =  0.2286682237349059125691;
        final double yP        = -yM;
        final double valueXmYm =  0.2373295333134216789769; // local  maximum
        final double valueXmYp = -valueXmYm;                // local  minimum
        final double valueXpYm = -0.7290400707055187115322; // global minimum
        final double valueXpYp = -valueXpYm;                // global maximum
        MultivariateRealFunction fourExtrema = new MultivariateRealFunction() {
                private static final long serialVersionUID = -7039124064449091152L;
                public double value(double[] variables) {
                    final double x = variables[0];
                    final double y = variables[1];
                    return (x == 0 || y == 0) ? 0 :
                        (Math.atan(x) * Math.atan(x + 2) * Math.atan(y) * Math.atan(y) / (x * y));
                }
            };

        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        optimizer.setSimplex(new NelderMeadSimplex(new double[] { 0.2, 0.2 }));
        RealPointValuePair optimum;

        // minimization
        optimum = optimizer.optimize(100, fourExtrema, GoalType.MINIMIZE, new double[] { -3, 0 });
        assertEquals(xM,        optimum.getPoint()[0], 2e-7);
        assertEquals(yP,        optimum.getPoint()[1], 2e-5);
        assertEquals(valueXmYp, optimum.getValue(),    6e-12);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 90);

        optimum = optimizer.optimize(100, fourExtrema, GoalType.MINIMIZE, new double[] { 1, 0 });
        assertEquals(xP,        optimum.getPoint()[0], 5e-6);
        assertEquals(yM,        optimum.getPoint()[1], 6e-6);
        assertEquals(valueXpYm, optimum.getValue(),    1e-11);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 90);

        // maximization
        optimum = optimizer.optimize(100, fourExtrema, GoalType.MAXIMIZE, new double[] { -3, 0 });
        assertEquals(xM,        optimum.getPoint()[0], 1e-5);
        assertEquals(yM,        optimum.getPoint()[1], 3e-6);
        assertEquals(valueXmYm, optimum.getValue(),    3e-12);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 90);

        optimum = optimizer.optimize(100, fourExtrema, GoalType.MAXIMIZE, new double[] { 1, 0 });
        assertEquals(xP,        optimum.getPoint()[0], 4e-6);
        assertEquals(yP,        optimum.getPoint()[1], 5e-6);
        assertEquals(valueXpYp, optimum.getValue(),    7e-12);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 90);
    }

    @Test
    public void testRosenbrock() {

        Rosenbrock rosenbrock = new Rosenbrock();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        optimizer.setSimplex(new NelderMeadSimplex(new double[][] {
                    { -1.2,  1 }, { 0.9, 1.2 } , {  3.5, -2.3 }
                }));
        RealPointValuePair optimum =
            optimizer.optimize(100, rosenbrock, GoalType.MINIMIZE, new double[] { -1.2, 1 });

        assertEquals(rosenbrock.getCount(), optimizer.getEvaluations());
        assertTrue(optimizer.getEvaluations() > 40);
        assertTrue(optimizer.getEvaluations() < 50);
        assertTrue(optimum.getValue() < 8e-4);
    }

    @Test
    public void testPowell() {

        Powell powell = new Powell();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        optimizer.setSimplex(new NelderMeadSimplex(4));
        RealPointValuePair optimum =
            optimizer.optimize(200, powell, GoalType.MINIMIZE, new double[] { 3, -1, 0, 1 });
        assertEquals(powell.getCount(), optimizer.getEvaluations());
        assertTrue(optimizer.getEvaluations() > 110);
        assertTrue(optimizer.getEvaluations() < 130);
        assertTrue(optimum.getValue() < 2e-3);
    }

    @Test
    public void testLeastSquares1() {

        final RealMatrix factors =
            new Array2DRowRealMatrix(new double[][] {
                    { 1, 0 },
                    { 0, 1 }
                }, false);
        LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorialFunction() {
                public double[] value(double[] variables) {
                    return factors.operate(variables);
                }
            }, new double[] { 2.0, -3.0 });
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-6);
        optimizer.setSimplex(new NelderMeadSimplex(2));
        RealPointValuePair optimum =
            optimizer.optimize(200, ls, GoalType.MINIMIZE, new double[] { 10, 10 });
        assertEquals( 2, optimum.getPointRef()[0], 3e-5);
        assertEquals(-3, optimum.getPointRef()[1], 4e-4);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 80);
        assertTrue(optimum.getValue() < 1.0e-6);
    }

    @Test
    public void testLeastSquares2() {

        final RealMatrix factors =
            new Array2DRowRealMatrix(new double[][] {
                    { 1, 0 },
                    { 0, 1 }
                }, false);
        LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorialFunction() {
                public double[] value(double[] variables) {
                    return factors.operate(variables);
                }
            }, new double[] { 2, -3 }, new double[] { 10, 0.1 });
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-6);
        optimizer.setSimplex(new NelderMeadSimplex(2));
        RealPointValuePair optimum =
            optimizer.optimize(200, ls, GoalType.MINIMIZE, new double[] { 10, 10 });
        assertEquals( 2, optimum.getPointRef()[0], 5e-5);
        assertEquals(-3, optimum.getPointRef()[1], 8e-4);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 80);
        assertTrue(optimum.getValue() < 1e-6);
    }

    @Test
    public void testLeastSquares3() {

        final RealMatrix factors =
            new Array2DRowRealMatrix(new double[][] {
                    { 1, 0 },
                    { 0, 1 }
                }, false);
        LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorialFunction() {
                public double[] value(double[] variables) {
                    return factors.operate(variables);
                }
            }, new double[] { 2, -3 }, new Array2DRowRealMatrix(new double [][] {
                    { 1, 1.2 }, { 1.2, 2 }
                }));
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-6);
        optimizer.setSimplex(new NelderMeadSimplex(2));
        RealPointValuePair optimum =
            optimizer.optimize(200, ls, GoalType.MINIMIZE, new double[] { 10, 10 });
        assertEquals( 2, optimum.getPointRef()[0], 2e-3);
        assertEquals(-3, optimum.getPointRef()[1], 8e-4);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 80);
        assertTrue(optimum.getValue() < 1e-6);
    }

    @Test(expected = TooManyEvaluationsException.class)
    public void testMaxIterations() {
        Powell powell = new Powell();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        optimizer.setSimplex(new NelderMeadSimplex(4));
        optimizer.optimize(20, powell, GoalType.MINIMIZE, new double[] { 3, -1, 0, 1 });
    }

    private static class Rosenbrock implements MultivariateRealFunction {
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

    private static class Powell implements MultivariateRealFunction {
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
