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
import org.apache.commons.math4.legacy.analysis.function.Sin;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for midpoint integrator.
 * <p>
 * Test runs show that for a default relative accuracy of 1E-6, it generally
 * takes 10 to 15 iterations for the integral to converge.
 *
 */
public final class MidPointIntegratorTest {
    private static final int NUM_ITER = 30;

    /**
     * The initial iteration contributes 1 evaluation. Each successive iteration
     * contributes 2 points to each previous slice.
     *
     * The total evaluation count == 1 + 2*3^0 + 2*3^1 + ... 2*3^n
     *
     * the series 3^0 + 3^1 + ... + 3^n sums to 3^(n-1) / (3-1), so the total
     * expected evaluations == 1 + 2*(3^(n-1) - 1)/2 == 3^(n-1).
     *
     * The n in the series above is offset by 1 from the MidPointIntegrator
     * iteration count so the actual result == 3^n.
     *
     * Without the incremental implementation, the same result would require
     * (3^(n + 1) - 1) / 2 evaluations; just under 50% more.
     */
    private long expectedEvaluations(int iterations) {
        return (long) JdkMath.pow(3, iterations);
    }

    /**
     * Test of integrator for the sine function.
     */
    @Test
    public void testLowAccuracy() {
        UnivariateFunction f = new QuinticFunction();
        UnivariateIntegrator integrator = new MidPointIntegrator(0.01, 1.0e-10, 2, 4);

        double min = -10;
        double max =  -9;
        double expected = -3697001.0 / 48.0;
        double tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        double result = integrator.integrate(Integer.MAX_VALUE, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < Integer.MAX_VALUE / 3);
        Assert.assertTrue(integrator.getIterations() < NUM_ITER);
        Assert.assertEquals(expectedEvaluations(integrator.getIterations()), integrator.getEvaluations());
        Assert.assertEquals(expected, result, tolerance);
    }

    /**
     * Test of integrator for the sine function.
     */
    @Test
    public void testSinFunction() {
        UnivariateFunction f = new Sin();
        UnivariateIntegrator integrator = new MidPointIntegrator();

        double min = 0;
        double max = JdkMath.PI;
        double expected = 2;
        double tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        double result = integrator.integrate(Integer.MAX_VALUE, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < Integer.MAX_VALUE / 3);
        Assert.assertTrue(integrator.getIterations() < NUM_ITER);
        Assert.assertEquals(expectedEvaluations(integrator.getIterations()), integrator.getEvaluations());
        Assert.assertEquals(expected, result, tolerance);

        min = -JdkMath.PI/3;
        max = 0;
        expected = -0.5;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(Integer.MAX_VALUE, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < Integer.MAX_VALUE / 3);
        Assert.assertTrue(integrator.getIterations() < NUM_ITER);
        Assert.assertEquals(expectedEvaluations(integrator.getIterations()), integrator.getEvaluations());
        Assert.assertEquals(expected, result, tolerance);
    }

    /**
     * Test of integrator for the quintic function.
     */
    @Test
    public void testQuinticFunction() {
        UnivariateFunction f = new QuinticFunction();
        UnivariateIntegrator integrator = new MidPointIntegrator();

        double min = 0;
        double max = 1;
        double expected = -1.0 / 48;
        double tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        double result = integrator.integrate(Integer.MAX_VALUE, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < Integer.MAX_VALUE / 3);
        Assert.assertTrue(integrator.getIterations() < NUM_ITER);
        Assert.assertEquals(expectedEvaluations(integrator.getIterations()), integrator.getEvaluations());
        Assert.assertEquals(expected, result, tolerance);

        min = 0;
        max = 0.5;
        expected = 11.0 / 768;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(Integer.MAX_VALUE, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < Integer.MAX_VALUE / 3);
        Assert.assertTrue(integrator.getIterations() < NUM_ITER);
        Assert.assertEquals(expected, result, tolerance);

        min = -1;
        max = 4;
        expected = 2048 / 3.0 - 78 + 1.0 / 48;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(Integer.MAX_VALUE, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < Integer.MAX_VALUE / 3);
        Assert.assertTrue(integrator.getIterations() < NUM_ITER);
        Assert.assertEquals(expectedEvaluations(integrator.getIterations()), integrator.getEvaluations());
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
            new MidPointIntegrator().integrate(1000, f, 1, -1);
            Assert.fail("Expecting NumberIsTooLargeException - bad interval");
        } catch (NumberIsTooLargeException ex) {
            // expected
        }
        try {
            // bad iteration limits
            new MidPointIntegrator(5, 4);
            Assert.fail("Expecting NumberIsTooSmallException - bad iteration limits");
        } catch (NumberIsTooSmallException ex) {
            // expected
        }
        try {
            // bad iteration limits
            new MidPointIntegrator(10, 99);
            Assert.fail("Expecting NumberIsTooLargeException - bad iteration limits");
        } catch (NumberIsTooLargeException ex) {
            // expected
        }
    }
}
