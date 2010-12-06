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
package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.NoBracketingException;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testcase for {@link SecantSolver}.
 *
 * @version $Revision:670469 $ $Date:2008-06-23 10:01:38 +0200 (lun., 23 juin 2008) $
 */
public final class SecantSolverTest {
    @Test
    public void testSinZero() {
        // The sinus function is behaved well around the root at pi. The second
        // order derivative is zero, which means linar approximating methods will
        // still converge quadratically.
        UnivariateRealFunction f = new SinFunction();
        double result;
        UnivariateRealSolver solver = new SecantSolver();

        result = solver.solve(100, f, 3, 4);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 6);
        result = solver.solve(100, f, 1, 4);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 7);
    }

    @Test
    public void testQuinticZero() {
        // The quintic function has zeros at 0, +-0.5 and +-1.
        // Around the root of 0 the function is well behaved, with a second derivative
        // of zero a 0.
        // The other roots are less well to find, in particular the root at 1, because
        // the function grows fast for x>1.
        // The function has extrema (first derivative is zero) at 0.27195613 and 0.82221643,
        // intervals containing these values are harder for the solvers.
        UnivariateRealFunction f = new QuinticFunction();
        double result;
        // Brent-Dekker solver.
        UnivariateRealSolver solver = new SecantSolver();
        result = solver.solve(100, f, -0.2, 0.2);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 3);
        result = solver.solve(100, f, -0.1, 0.3);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 7);
        result = solver.solve(100, f, -0.3, 0.45);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 8);
        result = solver.solve(100, f, 0.3, 0.7);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 9);
        result = solver.solve(100, f, 0.2, 0.6);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 8);
        result = solver.solve(100, f, 0.05, 0.95);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 10);
        result = solver.solve(100, f, 0.85, 1.25);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 12);
        result = solver.solve(100, f, 0.8, 1.2);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 10);
        result = solver.solve(100, f, 0.85, 1.75);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 16);
        // The followig is especially slow because the solver first has to reduce
        // the bracket to exclude the extremum. After that, convergence is rapide.
        result = solver.solve(100, f, 0.55, 1.45);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 9);
        result = solver.solve(100, f, 0.85, 5);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 16);
    }

    @Test
    public void testRootEndpoints() {
        UnivariateRealFunction f = new SinFunction();
        SecantSolver solver = new SecantSolver();

        // endpoint is root
        double result = solver.solve(100, f, FastMath.PI, 4);
        Assert.assertEquals(FastMath.PI, result, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 3, FastMath.PI);
        Assert.assertEquals(FastMath.PI, result, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, FastMath.PI, 4, 3.5);
        Assert.assertEquals(FastMath.PI, result, solver.getAbsoluteAccuracy());

        result = solver.solve(100, f, 3, FastMath.PI, 3.07);
        Assert.assertEquals(FastMath.PI, result, solver.getAbsoluteAccuracy());

    }

    @Test
    public void testBadEndpoints() {
        UnivariateRealFunction f = new SinFunction();
        SecantSolver solver = new SecantSolver();
        try {  // bad interval
            solver.solve(100, f, 1, -1);
            Assert.fail("Expecting NumberIsTooLargeException - bad interval");
        } catch (NumberIsTooLargeException ex) {
            // expected
        }
        try {  // no bracket
            solver.solve(100, f, 1, 1.5);
            Assert.fail("Expecting NoBracketingException - non-bracketing");
        } catch (NoBracketingException ex) {
            // expected
        }
        try {  // no bracket
            solver.solve(100, f, 1, 1.5, 1.2);
            Assert.fail("Expecting NoBracketingException - non-bracketing");
        } catch (NoBracketingException ex) {
            // expected
        }
    }
}
