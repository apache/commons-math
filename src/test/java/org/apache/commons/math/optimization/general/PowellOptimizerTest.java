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
package org.apache.commons.math.optimization.general;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.SumSincFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.MultivariateRealOptimizer;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link PowellOptimizer}.
 */
public class PowellOptimizerTest {

    @Test
    public void testSumSinc() throws MathException {
        final MultivariateRealFunction func = new SumSincFunction(-1);

        int dim = 2;
        final double[] minPoint = new double[dim];
        for (int i = 0; i < dim; i++) {
            minPoint[i] = 0;
        }

        double[] init = new double[dim];

        // Initial is minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = minPoint[i];
        }
        doTest(func, minPoint, init,  GoalType.MINIMIZE, 1e-9, 1e-7);

        // Initial is far from minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = minPoint[i] + 3;
        }
        doTest(func, minPoint, init,  GoalType.MINIMIZE, 1e-9, 1e-7);
    }

    @Test
    public void testQuadratic() throws MathException {
        final MultivariateRealFunction func = new MultivariateRealFunction() {
                public double value(double[] x)
                    throws FunctionEvaluationException {
                    final double a = x[0] - 1;
                    final double b = x[1] - 1;
                    return a * a + b * b + 1;
                }
            };

        int dim = 2;
        final double[] minPoint = new double[dim];
        for (int i = 0; i < dim; i++) {
            minPoint[i] = 1;
        }

        double[] init = new double[dim];

        // Initial is minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = minPoint[i];
        }
        doTest(func, minPoint, init, GoalType.MINIMIZE, 1e-9, 1e-8);

        // Initial is far from minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = minPoint[i] - 20;
        }
        doTest(func, minPoint, init, GoalType.MINIMIZE, 1e-9, 1e-8);
    }

    @Test
    public void testMaximizeQuadratic() throws MathException {
        final MultivariateRealFunction func = new MultivariateRealFunction() {
                public double value(double[] x)
                    throws FunctionEvaluationException {
                    final double a = x[0] - 1;
                    final double b = x[1] - 1;
                    return -a * a - b * b + 1;
                }
            };

        int dim = 2;
        final double[] maxPoint = new double[dim];
        for (int i = 0; i < dim; i++) {
            maxPoint[i] = 1;
        }

        double[] init = new double[dim];

        // Initial is minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = maxPoint[i];
        }
        doTest(func, maxPoint, init,  GoalType.MAXIMIZE, 1e-9, 1e-8);

        // Initial is far from minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = maxPoint[i] - 20;
        }
        doTest(func, maxPoint, init, GoalType.MAXIMIZE, 1e-9, 1e-8);
    }

    /**
     * @param func Function to optimize.
     * @param optimum Expected optimum.
     * @param init Starting point.
     * @param goal Minimization or maximization.
     * @param fTol Tolerance (relative error on the objective function) for
     * "Powell" algorithm.
     * @param pointTol Tolerance for checking that the optimum is correct.
     */
    private void doTest(MultivariateRealFunction func,
                        double[] optimum,
                        double[] init,
                        GoalType goal,
                        double fTol,
                        double pointTol)
        throws MathException {
        final MultivariateRealOptimizer optim = new PowellOptimizer();
        optim.setMaxEvaluations(1000);
        optim.setConvergenceChecker(new SimpleScalarValueChecker(fTol, Math.ulp(1d)));

        final RealPointValuePair result = optim.optimize(func, goal, init);
        final double[] found = result.getPoint();

        for (int i = 0, dim = optimum.length; i < dim; i++) {
            Assert.assertEquals(optimum[i], found[i], pointTol);
        }
    }
}
