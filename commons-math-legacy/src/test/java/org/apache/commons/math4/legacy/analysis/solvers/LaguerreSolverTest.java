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
package org.apache.commons.math4.legacy.analysis.solvers;

import org.apache.commons.numbers.complex.Complex;
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math4.legacy.exception.NoBracketingException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.math4.legacy.TestUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for Laguerre solver.
 * <p>
 * Laguerre's method is very efficient in solving polynomials. Test runs
 * show that for a default absolute accuracy of 1E-6, it generally takes
 * less than 5 iterations to find one root, provided solveAll() is not
 * invoked, and 15 to 20 iterations to find all roots for quintic function.
 *
 */
public final class LaguerreSolverTest {
    /**
     * Test of solver for the linear function.
     */
    @Test
    public void testLinearFunction() {
        double min;
        double max;
        double expected;
        double result;
        double tolerance;

        // p(x) = 4x - 1
        double coefficients[] = { -1.0, 4.0 };
        PolynomialFunction f = new PolynomialFunction(coefficients);
        LaguerreSolver solver = new LaguerreSolver();

        min = 0.0; max = 1.0; expected = 0.25;
        tolerance = JdkMath.max(solver.getAbsoluteAccuracy(),
                    JdkMath.abs(expected * solver.getRelativeAccuracy()));
        result = solver.solve(100, f, min, max);
        Assert.assertEquals(expected, result, tolerance);
    }

    /**
     * Test of solver for the quadratic function.
     */
    @Test
    public void testQuadraticFunction() {
        double min;
        double max;
        double expected;
        double result;
        double tolerance;

        // p(x) = 2x^2 + 5x - 3 = (x+3)(2x-1)
        double coefficients[] = { -3.0, 5.0, 2.0 };
        PolynomialFunction f = new PolynomialFunction(coefficients);
        LaguerreSolver solver = new LaguerreSolver();

        min = 0.0; max = 2.0; expected = 0.5;
        tolerance = JdkMath.max(solver.getAbsoluteAccuracy(),
                    JdkMath.abs(expected * solver.getRelativeAccuracy()));
        result = solver.solve(100, f, min, max);
        Assert.assertEquals(expected, result, tolerance);

        min = -4.0; max = -1.0; expected = -3.0;
        tolerance = JdkMath.max(solver.getAbsoluteAccuracy(),
                    JdkMath.abs(expected * solver.getRelativeAccuracy()));
        result = solver.solve(100, f, min, max);
        Assert.assertEquals(expected, result, tolerance);
    }

    /**
     * Test of solver for the quintic function.
     */
    @Test
    public void testQuinticFunction() {
        double min;
        double max;
        double expected;
        double result;
        double tolerance;

        // p(x) = x^5 - x^4 - 12x^3 + x^2 - x - 12 = (x+1)(x+3)(x-4)(x^2-x+1)
        double coefficients[] = { -12.0, -1.0, 1.0, -12.0, -1.0, 1.0 };
        PolynomialFunction f = new PolynomialFunction(coefficients);
        LaguerreSolver solver = new LaguerreSolver();

        min = -2.0; max = 2.0; expected = -1.0;
        tolerance = JdkMath.max(solver.getAbsoluteAccuracy(),
                    JdkMath.abs(expected * solver.getRelativeAccuracy()));
        result = solver.solve(100, f, min, max);
        Assert.assertEquals(expected, result, tolerance);

        min = -5.0; max = -2.5; expected = -3.0;
        tolerance = JdkMath.max(solver.getAbsoluteAccuracy(),
                    JdkMath.abs(expected * solver.getRelativeAccuracy()));
        result = solver.solve(100, f, min, max);
        Assert.assertEquals(expected, result, tolerance);

        min = 3.0; max = 6.0; expected = 4.0;
        tolerance = JdkMath.max(solver.getAbsoluteAccuracy(),
                    JdkMath.abs(expected * solver.getRelativeAccuracy()));
        result = solver.solve(100, f, min, max);
        Assert.assertEquals(expected, result, tolerance);
    }

    /**
     * Test of solver for the quintic function using
     * {@link LaguerreSolver#solveAllComplex(double[],double) solveAllComplex}.
     */
    @Test
    public void testQuinticFunction2() {
        // p(x) = x^5 + 4x^3 + x^2 + 4 = (x+1)(x^2-x+1)(x^2+4)
        final double[] coefficients = { 4.0, 0.0, 1.0, 4.0, 0.0, 1.0 };
        final LaguerreSolver solver = new LaguerreSolver();
        final Complex[] result = solver.solveAllComplex(coefficients, 0);

        for (Complex expected : new Complex[] { Complex.ofCartesian(0, -2),
                                                Complex.ofCartesian(0, 2),
                                                Complex.ofCartesian(0.5, 0.5 * JdkMath.sqrt(3)),
                                                Complex.ofCartesian(-1, 0),
                                                Complex.ofCartesian(0.5, -0.5 * JdkMath.sqrt(3.0)) }) {
            final double tolerance = JdkMath.max(solver.getAbsoluteAccuracy(),
                                                  JdkMath.abs(expected.abs() * solver.getRelativeAccuracy()));
            TestUtils.assertContains(result, expected, tolerance);
        }
    }

    /**
     * Test of parameters for the solver.
     */
    @Test
    public void testParameters() {
        double coefficients[] = { -3.0, 5.0, 2.0 };
        PolynomialFunction f = new PolynomialFunction(coefficients);
        LaguerreSolver solver = new LaguerreSolver();

        try {
            // bad interval
            solver.solve(100, f, 1, -1);
            Assert.fail("Expecting NumberIsTooLargeException - bad interval");
        } catch (NumberIsTooLargeException ex) {
            // expected
        }
        try {
            // no bracketing
            solver.solve(100, f, 2, 3);
            Assert.fail("Expecting NoBracketingException - no bracketing");
        } catch (NoBracketingException ex) {
            // expected
        }
    }
}
