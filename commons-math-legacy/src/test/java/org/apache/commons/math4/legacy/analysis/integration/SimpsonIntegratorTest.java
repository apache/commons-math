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
package org.apache.commons.math4.legacy.analysis.integration;

import org.apache.commons.math4.legacy.analysis.QuinticFunction;
import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.analysis.function.Identity;
import org.apache.commons.math4.legacy.analysis.function.Inverse;
import org.apache.commons.math4.legacy.analysis.function.Sin;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for Simpson integrator.
 * <p>
 * Test runs show that for a default relative accuracy of 1E-6, it
 * generally takes 5 to 10 iterations for the integral to converge.
 *
 */
public final class SimpsonIntegratorTest {
    private static final int SIMPSON_MAX_ITERATIONS_COUNT = 30;

    /**
     * Test of integrator for the sine function.
     */
    @Test
    public void testSinFunction() {
        UnivariateFunction f = new Sin();
        UnivariateIntegrator integrator = new SimpsonIntegrator();
        double min;
        double max;
        double expected;
        double result;
        double tolerance;

        min = 0; max = JdkMath.PI; expected = 2;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(1000, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < 100);
        Assert.assertTrue(integrator.getIterations()  < 10);
        Assert.assertEquals(expected, result, tolerance);

        min = -JdkMath.PI/3; max = 0; expected = -0.5;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(1000, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < 50);
        Assert.assertTrue(integrator.getIterations()  < 10);
        Assert.assertEquals(expected, result, tolerance);
    }

    /**
     * Test of integrator for the quintic function.
     */
    @Test
    public void testQuinticFunction() {
        UnivariateFunction f = new QuinticFunction();
        UnivariateIntegrator integrator = new SimpsonIntegrator();
        double min;
        double max;
        double expected;
        double result;
        double tolerance;

        min = 0; max = 1; expected = -1.0/48;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(1000, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < 150);
        Assert.assertTrue(integrator.getIterations()  < 10);
        Assert.assertEquals(expected, result, tolerance);

        min = 0; max = 0.5; expected = 11.0/768;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(1000, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < 100);
        Assert.assertTrue(integrator.getIterations()  < 10);
        Assert.assertEquals(expected, result, tolerance);

        min = -1; max = 4; expected = 2048/3.0 - 78 + 1.0/48;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(1000, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < 150);
        Assert.assertTrue(integrator.getIterations()  < 10);
        Assert.assertEquals(expected, result, tolerance);
    }

    /**
     * Test of parameters for the integrator.
     */
    @Test
    public void testParameters() {
        UnivariateFunction f = new Sin();
        try {
            // bad interval
            new SimpsonIntegrator().integrate(1000, f, 1, -1);
            Assert.fail("Expecting NumberIsTooLargeException - bad interval");
        } catch (NumberIsTooLargeException ex) {
            // expected
        }
        try {
            // bad iteration limits
            new SimpsonIntegrator(5, 4);
            Assert.fail("Expecting NumberIsTooSmallException - bad iteration limits");
        } catch (NumberIsTooSmallException ex) {
            // expected
        }
        try {
            // bad iteration limits
            new SimpsonIntegrator(10, SIMPSON_MAX_ITERATIONS_COUNT + 1);
            Assert.fail("Expecting NumberIsTooLargeException - bad iteration limits");
        } catch (NumberIsTooLargeException ex) {
            // expected
        }
    }

    // Tests for MATH-1458:
    // The SimpsonIntegrator had the following bugs:
    // - minimalIterationCount==1 results in no possible iteration
    // - minimalIterationCount==1 computes incorrect Simpson sum (following no iteration)
    // - minimalIterationCount>1 computes the first iteration sum as the Trapezoid sum
    // - minimalIterationCount>1 computes the second iteration sum as the first Simpson sum

    /**
     * Test iteration is possible when minimalIterationCount==1.
     * <br/>
     * MATH-1458: No iterations were performed when minimalIterationCount==1.
     */
    @Test
    public void testIterationIsPossibleWhenMinimalIterationCountIs1() {
        UnivariateFunction f = new Sin();
        UnivariateIntegrator integrator = new SimpsonIntegrator(1, SIMPSON_MAX_ITERATIONS_COUNT);
        // The range or result is not relevant.
        // This sum should not converge at 1 iteration.
        // This tests iteration occurred.
        integrator.integrate(1000, f, 0, 1);
        // MATH-1458: No iterations were performed when minimalIterationCount==1
        Assert.assertTrue("Iteration is not above 1",
                integrator.getIterations() > 1);
    }

    /**
     * Test convergence at iteration 1 when minimalIterationCount==1.
     * <br/>
     * MATH-1458: No iterations were performed when minimalIterationCount==1.
     */
    @Test
    public void testConvergenceIsPossibleAtIteration1() {
        // A linear function y=x should converge immediately
        UnivariateFunction f = new Identity();
        UnivariateIntegrator integrator = new SimpsonIntegrator(1, SIMPSON_MAX_ITERATIONS_COUNT);

        double min;
        double max;
        double expected;
        double result;
        double tolerance;

        min = 0; max = 1; expected = 0.5;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(1000, f, min, max);
        // MATH-1458: No iterations were performed when minimalIterationCount==1
        Assert.assertTrue("Iteration is not above 0",
                integrator.getIterations()  > 0);
        // This should converge immediately
        Assert.assertEquals("Iteration", integrator.getIterations(), 1);
        Assert.assertEquals("Result", expected, result, tolerance);
    }

    /**
     * Compute the integral using the composite Simpson's rule.
     *
     * @param f the function
     * @param a the lower limit
     * @param b the upper limit
     * @param n the number of intervals (must be even)
     * @return the integral between a and b
     * @see <a href="https://en.wikipedia.org/wiki/Simpson%27s_rule#Composite_Simpson's_rule">
     *       Composite_Simpson's_rule</a>
     */
    private static double compositeSimpsonsRule(UnivariateFunction f, double a,
            double b, int n) {
        // Sum interval [a,b] split into n subintervals, with n an even number:
        // sum ~ h/3 * [ f(x0) + 4f(x1) + 2f(x2) + 4f(x3) + 2f(x4) ... + 4f(xn-1) + f(xn) ]
        // h = (b-a)/n
        // f(xi) = f(a + i*h)
        assert n > 0 && (n & 1) == 0 : "n must be strictly positive and even";
        final double h = (b - a) / n;
        double sum4 = 0;
        double sum2 = 0;
        for (int i = 1; i < n; i++) {
            // Alternate sums that are multiplied by 4 and 2
            final double fxi = f.value(a + i * h);
            if ((i & 1) == 0) {
                sum2 += fxi;
            } else {
                sum4 += fxi;
            }
        }
        return (h / 3) * (f.value(a) + 4 * sum4 + 2 * sum2 + f.value(b));
    }

    /**
     * Compute the iteration of Simpson's rule.
     *
     * @param f the function
     * @param a the lower limit
     * @param b the upper limit
     * @param iteration the refinement iteration
     * @return the integral between a and b
     */
    private static double computeSimpsonIteration(UnivariateFunction f, double a,
            double b, int iteration) {
        // The first possible Simpson's sum uses n=2.
        // The next uses n=4. This is the 1st refinement expected when the
        // integrator has performed 1 iteration.
        final int n = 2 << iteration;
        return compositeSimpsonsRule(f, a, b, n);
    }

    /**
     * Test the reference Simpson integration is doing what is expected
     */
    @Test
    public void testReferenceSimpsonItegrationIsCorrect() {
        UnivariateFunction f = new Sin();

        double a;
        double b;
        double h;
        double expected;
        double result;
        double tolerance;

        a = 0.5;
        b = 1;

        double b_a = b - a;

        // First Simpson sum. 1 midpoint evaluation:
        h = b_a / 2;
        double f00 = f.value(a);
        double f01 = f.value(a + 1 * h);
        double f0n = f.value(b);
        expected = (b_a / 6) * (f00 + 4 * f01 + f0n);
        tolerance = JdkMath.abs(expected * SimpsonIntegrator.DEFAULT_RELATIVE_ACCURACY);
        result = computeSimpsonIteration(f, a, b, 0);
        Assert.assertEquals("Result", expected, result, tolerance);

        // Second Simpson sum: 2 more evaluations:
        h = b_a / 4;
        double f11 = f.value(a + 1 * h);
        double f13 = f.value(a + 3 * h);
        expected = (h / 3) * (f00 + 4 * f11 + 2 * f01 + 4 * f13 + f0n);
        tolerance = JdkMath.abs(expected * SimpsonIntegrator.DEFAULT_RELATIVE_ACCURACY);
        result = computeSimpsonIteration(f, a, b, 1);
        Assert.assertEquals("Result", expected, result, tolerance);

        // Third Simpson sum: 4 more evaluations:
        h = b_a / 8;
        double f21 = f.value(a + 1 * h);
        double f23 = f.value(a + 3 * h);
        double f25 = f.value(a + 5 * h);
        double f27 = f.value(a + 7 * h);
        expected = (h / 3) * (f00 + 4 * f21 + 2 * f11 + 4 * f23 + 2 * f01 + 4 * f25 +
                2 * f13 + 4 * f27 + f0n);
        tolerance = JdkMath.abs(expected * SimpsonIntegrator.DEFAULT_RELATIVE_ACCURACY);
        result = computeSimpsonIteration(f, a, b, 2);
        Assert.assertEquals("Result", expected, result, tolerance);
    }

    /**
     * Test iteration 1 returns the expected sum when minimalIterationCount==1.
     * <br/>
     * MATH-1458: minimalIterationCount==1 computes incorrect Simpson sum
     * (following no iteration).
     */
    @Test
    public void testIteration1ComputesTheExpectedSimpsonSum() {
        UnivariateFunction f = new Sin();
        // Set convergence criteria to force immediate convergence
        UnivariateIntegrator integrator = new SimpsonIntegrator(
                0, Double.POSITIVE_INFINITY,
                1, SIMPSON_MAX_ITERATIONS_COUNT);
        double min;
        double max;
        double expected;
        double result;
        double tolerance;

        // MATH-1458: minimalIterationCount==1 computes incorrect
        // Simpson sum (following no iteration)
        min = 0;
        max = 1;
        result = integrator.integrate(1000, f, min, max);
        // Immediate convergence
        Assert.assertEquals("Iteration", 1, integrator.getIterations());

        // Check the sum is as expected
        expected = computeSimpsonIteration(f, min, max, 1);
        tolerance = JdkMath.abs(expected * SimpsonIntegrator.DEFAULT_RELATIVE_ACCURACY);
        Assert.assertEquals("Result", expected, result, tolerance);
    }

    /**
     * Test iteration N returns the expected sum when minimalIterationCount==1.
     * <br/>
     * MATH-1458: minimalIterationCount>1 computes the second iteration sum as
     * the first Simpson sum.
     */
    @Test
    public void testIterationNComputesTheExpectedSimpsonSum() {
        // Use 1/x as the function as the sum will asymptote in a monotonic
        // series. The convergence can then be controlled.
        UnivariateFunction f = new Inverse();

        double min;
        double max;
        double expected;
        double result;
        double tolerance;
        int minIteration;
        int maxIteration;

        // Range for integration
        min = 1;
        max = 2;

        // This is the expected sum.
        // Each iteration will monotonically converge to this.
        expected = JdkMath.log(max) - JdkMath.log(min);

        // Test convergence at the given iteration
        minIteration = 2;
        maxIteration = 4;

        // Compute the sums expected for different iterations.
        // Add an additional sum so that the test can compare to the next value.
        double[] sums = new double[maxIteration + 2];
        for (int i = 0; i < sums.length; i++) {
            sums[i] = computeSimpsonIteration(f, min, max, i);
            // Check monotonic
            if (i > 0) {
                Assert.assertTrue("Expected series not monotonic descending",
                        sums[i] < sums[i - 1]);
                // Check monotonic difference
                if (i > 1) {
                    Assert.assertTrue("Expected convergence not monotonic descending",
                           sums[i - 1] - sums[i] < sums[i - 2] - sums[i - 1]);
                }
            }
        }

        // Check the test function is correct.
        tolerance = JdkMath.abs(expected * SimpsonIntegrator.DEFAULT_RELATIVE_ACCURACY);
        Assert.assertEquals("Expected result", expected, sums[maxIteration], tolerance);

        // Set-up to test convergence at a specific iteration.
        // Allow enough function evaluations.
        // Iteration 0 = 3 evaluations
        // Iteration 1 = 5 evaluations
        // Iteration n = 2^(n+1)+1 evaluations
        int evaluations = 2 << (maxIteration + 1) + 1;

        for (int i = minIteration; i <= maxIteration; i++) {
            // Create convergence criteria.
            // (sum - previous) is monotonic descending.
            // So use a point half-way between them:
            // ((sums[i-1] - sums[i]) + (sums[i-2] - sums[i-1])) / 2
            final double absoluteAccuracy = (sums[i - 2] - sums[i]) / 2;

            // Use minimalIterationCount>1
            UnivariateIntegrator integrator = new SimpsonIntegrator(
                    0, absoluteAccuracy,
                    2, SIMPSON_MAX_ITERATIONS_COUNT);

            result = integrator.integrate(evaluations, f, min, max);

            // Check the iteration is as expected
            Assert.assertEquals("Test failed to control iteration", i, integrator.getIterations());

            // MATH-1458: minimalIterationCount>1 computes incorrect Simpson sum
            // for the iteration. Check it is the correct sum.
            // It should be closer to this one than the previous or next.
            final double dp = JdkMath.abs(sums[i-1] - result);
            final double d  = JdkMath.abs(sums[i]   - result);
            final double dn = JdkMath.abs(sums[i+1] - result);

            Assert.assertTrue("Result closer to sum expected from previous iteration: " + i, d < dp);
            Assert.assertTrue("Result closer to sum expected from next iteration: " + i, d < dn);
        }
    }
}
