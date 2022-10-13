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
 * Test case for trapezoid integrator.
 * <p>
 * Test runs show that for a default relative accuracy of 1E-6, it
 * generally takes 10 to 15 iterations for the integral to converge.
 *
 */
public final class TrapezoidIntegratorTest {

    /**
     * Test of integrator for the sine function.
     */
    @Test
    public void testSinFunction() {
        UnivariateFunction f = new Sin();
        UnivariateIntegrator integrator = new TrapezoidIntegrator();
        double min;
        double max;
        double expected;
        double result;
        double tolerance;

        min = 0; max = JdkMath.PI; expected = 2;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(10000, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < 2500);
        Assert.assertTrue(integrator.getIterations()  < 15);
        Assert.assertEquals(expected, result, tolerance);

        min = -JdkMath.PI/3; max = 0; expected = -0.5;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(10000, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < 2500);
        Assert.assertTrue(integrator.getIterations()  < 15);
        Assert.assertEquals(expected, result, tolerance);
    }

    /**
     * Test of integrator for the quintic function.
     */
    @Test
    public void testQuinticFunction() {
        UnivariateFunction f = new QuinticFunction();
        UnivariateIntegrator integrator = new TrapezoidIntegrator();
        double min;
        double max;
        double expected;
        double result;
        double tolerance;

        min = 0; max = 1; expected = -1.0/48;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(10000, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < 5000);
        Assert.assertTrue(integrator.getIterations()  < 15);
        Assert.assertEquals(expected, result, tolerance);

        min = 0; max = 0.5; expected = 11.0/768;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(10000, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < 2500);
        Assert.assertTrue(integrator.getIterations()  < 15);
        Assert.assertEquals(expected, result, tolerance);

        min = -1; max = 4; expected = 2048/3.0 - 78 + 1.0/48;
        tolerance = JdkMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(10000, f, min, max);
        Assert.assertTrue(integrator.getEvaluations() < 5000);
        Assert.assertTrue(integrator.getIterations()  < 15);
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
            new TrapezoidIntegrator().integrate(1000, f, 1, -1);
            Assert.fail("Expecting NumberIsTooLargeException - bad interval");
        } catch (NumberIsTooLargeException ex) {
            // expected
        }
        try {
            // bad iteration limits
            new TrapezoidIntegrator(5, 4);
            Assert.fail("Expecting NumberIsTooSmallException - bad iteration limits");
        } catch (NumberIsTooSmallException ex) {
            // expected
        }
        try {
            // bad iteration limits
            new TrapezoidIntegrator(10,99);
            Assert.fail("Expecting NumberIsTooLargeException - bad iteration limits");
        } catch (NumberIsTooLargeException ex) {
            // expected
        }
    }
}
