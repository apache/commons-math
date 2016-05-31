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
package org.apache.commons.math4.analysis.integration.gauss;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.apache.commons.math4.analysis.integration.gauss.GaussIntegratorFactory;
import org.apache.commons.math4.analysis.integration.gauss.HermiteRuleFactory;
import org.apache.commons.math4.util.FastMath;

/**
 * Test of the {@link HermiteRuleFactory}.
 * This parameterized test extends the standard test for Gaussian quadrature
 * rule, where each monomial is tested in turn.
 * Parametrization allows to test automatically 0, 1, ... , {@link #MAX_NUM_POINTS}
 * quadrature rules.
 *
 */
@RunWith(value=Parameterized.class)
public class HermiteParametricTest extends GaussianQuadratureAbstractTest {
    private static final double SQRT_PI = FastMath.sqrt(Math.PI);
    private static final GaussIntegratorFactory factory = new GaussIntegratorFactory();

    /**
     * The highest order quadrature rule to be tested.
     */
    public static final int MAX_NUM_POINTS = 30;

    /**
     * Creates a new instance of this test, with the specified number of nodes
     * for the Gauss-Hermite quadrature rule.
     *
     * @param numberOfPoints Order of integration rule.
     * @param maxDegree Maximum degree of monomials to be tested.
     * @param eps Value of &epsilon;.
     * @param numUlps Value of the maximum relative error (in ulps).
     */
    public HermiteParametricTest(int numberOfPoints,
                                 int maxDegree,
                                 double eps,
                                 double numUlps) {
        super(factory.hermite(numberOfPoints),
              maxDegree, eps, numUlps);
    }

    /**
     * Returns the collection of parameters to be passed to the constructor of
     * this class.
     * Gauss-Hermite quadrature rules of order 1, ..., {@link #MAX_NUM_POINTS}
     * will be constructed.
     *
     * @return the collection of parameters for this parameterized test.
     */
    @SuppressWarnings("boxing") // OK here
    @Parameters
    public static Collection<Object[]> getParameters() {
        final ArrayList<Object[]> parameters = new ArrayList<Object[]>();
        for (int k = 1; k <= MAX_NUM_POINTS; k++) {
            parameters.add(new Object[] { k, 2 * k - 1, Math.ulp(1d), 195 });
        }
        return parameters;
    }

    @Override
    public double getExpectedValue(final int n) {
        if (n % 2 == 1) {
            return 0;
        }

        final int iMax = n / 2;
        double p = 1;
        double q = 1;
        for (int i = 0; i < iMax; i++) {
            p *= 2 * i + 1;
            q *= 2;
        }

        return p / q * SQRT_PI;
    }
}
