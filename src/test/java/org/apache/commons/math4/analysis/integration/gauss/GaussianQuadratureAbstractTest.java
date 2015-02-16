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
package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.analysis.function.Power;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import org.junit.Assert;

/**
 * Base class for standard testing of Gaussian quadrature rules,
 * which are exact for polynomials up to a certain degree. In this test, each
 * monomial in turn is tested against the specified quadrature rule.
 *
 */
public abstract class GaussianQuadratureAbstractTest {
    /**
     * The maximum absolute error (for zero testing).
     */
    private final double eps;
    /**
     * The maximum relative error (in ulps).
     */
    private final double numUlps;
    /**
     * The quadrature rule under test.
     */
    private final GaussIntegrator integrator;
    /**
     * Maximum degree of monomials to be tested.
     */
    private final int maxDegree;

    /**
     * Creates a new instance of this abstract test with the specified
     * quadrature rule.
     * If the expected value is non-zero, equality of actual and expected values
     * is checked in the relative sense <center>
     * |x<sub>act</sub>&nbsp;-&nbsp;x<sub>exp</sub>|&nbsp;&le;&nbsp; n&nbsp;
     * <code>Math.ulp(</code>x<sub>exp</sub><code>)</code>, </center> where n is
     * the maximum relative error (in ulps). If the expected value is zero, the
     * test checks that <center> |x<sub>act</sub>|&nbsp;&le;&nbsp;&epsilon;,
     * </center> where &epsilon; is the maximum absolute error.
     *
     * @param integrator Quadrature rule under test.
     * @param maxDegree Maximum degree of monomials to be tested.
     * @param eps &epsilon;.
     * @param numUlps Value of the maximum relative error (in ulps).
     */
    public GaussianQuadratureAbstractTest(GaussIntegrator integrator,
                                          int maxDegree,
                                          double eps,
                                          double numUlps) {
        this.integrator = integrator;
        this.maxDegree = maxDegree;
        this.eps = eps;
        this.numUlps = numUlps;
    }

    /**
     * Returns the expected value of the integral of the specified monomial.
     * The integration is carried out on the natural interval of the quadrature
     * rule under test.
     *
     * @param n Degree of the monomial.
     * @return the expected value of the integral of x<sup>n</sup>.
     */
    public abstract double getExpectedValue(final int n);

    /**
     * Checks that the value of the integral of each monomial
     *   <code>x<sup>0</sup>, ... , x<sup>p</sup></code>
     * returned by the quadrature rule under test conforms with the expected
     * value.
     * Here {@code p} denotes the degree of the highest polynomial for which
     * exactness is to be expected.
     */
    @Test
    public void testAllMonomials() {
        for (int n = 0; n <= maxDegree; n++) {
            final double expected = getExpectedValue(n);

            final Power monomial = new Power(n);
            final double actual = integrator.integrate(monomial);

            // System.out.println(n + "/" + maxDegree + " " + integrator.getNumberOfPoints()
            //                    + " " + expected + " " + actual + " " + Math.ulp(expected));
            if (expected == 0) {
                Assert.assertEquals("while integrating monomial x**" + n +
                                    " with a " +
                                    integrator.getNumberOfPoints() + "-point quadrature rule",
                                    expected, actual, eps);
            } else {
                double err = FastMath.abs(actual - expected) / Math.ulp(expected);
                Assert.assertEquals("while integrating monomial x**" + n + " with a " +
                                    + integrator.getNumberOfPoints() + "-point quadrature rule, " +
                                    " error was " + err + " ulps",
                                    expected, actual, Math.ulp(expected) * numUlps);
            }
        }
    }
}
