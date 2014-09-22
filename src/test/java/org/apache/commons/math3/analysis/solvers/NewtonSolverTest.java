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
package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;


/**
 * @deprecated
 */
@Deprecated
public final class NewtonSolverTest {
    /**
     *
     */
    @Test
    public void testSinZero() {
        DifferentiableUnivariateFunction f = new Sin();
        double result;

        NewtonSolver solver = new NewtonSolver();
        result = solver.solve(100, f, 3, 4);
        Assert.assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 1, 4);
        Assert.assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());

        Assert.assertTrue(solver.getEvaluations() > 0);
    }

    /**
     *
     */
    @Test
    public void testQuinticZero() {
        final UnivariateDifferentiableFunction q = new QuinticFunction();
        DifferentiableUnivariateFunction f = new DifferentiableUnivariateFunction() {

            public double value(double x) {
                return q.value(x);
            }

            public UnivariateFunction derivative() {
                return new UnivariateFunction() {
                    public double value(double x) {
                        return q.value(new DerivativeStructure(1, 1, 0, x)).getPartialDerivative(1);
                    }
                };
            }

        };
        double result;

        NewtonSolver solver = new NewtonSolver();
        result = solver.solve(100, f, -0.2, 0.2);
        Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, -0.1, 0.3);
        Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, -0.3, 0.45);
        Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 0.3, 0.7);
        Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 0.2, 0.6);
        Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 0.05, 0.95);
        Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 0.85, 1.25);
        Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 0.8, 1.2);
        Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 0.85, 1.75);
        Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 0.55, 1.45);
        Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 0.85, 5);
        Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
    }
}
