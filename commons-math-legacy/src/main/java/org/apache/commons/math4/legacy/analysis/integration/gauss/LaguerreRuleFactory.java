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
package org.apache.commons.math4.legacy.analysis.integration.gauss;

import java.util.Arrays;

import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialsUtils;
import org.apache.commons.math4.legacy.linear.EigenDecomposition;
import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.core.Pair;

/**
 * Factory that creates Gauss-type quadrature rule using Laguerre polynomials.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Gauss%E2%80%93Laguerre_quadrature">Gauss-Laguerre quadrature (Wikipedia)</a>
 * @since 4.0
 */
public class LaguerreRuleFactory extends BaseRuleFactory<Double> {
    /** {@inheritDoc} */
    @Override
    protected Pair<Double[], Double[]> computeRule(int numberOfPoints) {
        final RealMatrix companionMatrix = companionMatrix(numberOfPoints);
        final EigenDecomposition eigen = new EigenDecomposition(companionMatrix);
        final double[] roots = eigen.getRealEigenvalues();
        Arrays.sort(roots);

        final Double[] points = new Double[numberOfPoints];
        final Double[] weights = new Double[numberOfPoints];

        final int n1 = numberOfPoints + 1;
        final long n1Squared = n1 * (long) n1;
        final PolynomialFunction laguerreN1 = PolynomialsUtils.createLaguerrePolynomial(n1);
        for (int i = 0; i < numberOfPoints; i++) {
            final double xi = roots[i];
            points[i] = xi;

            final double val = laguerreN1.value(xi);
            weights[i] = xi / n1Squared / (val * val);
        }

        return new Pair<>(points, weights);
    }

    /**
     * @param degree Matrix dimension.
     * @return a square matrix.
     */
    private RealMatrix companionMatrix(final int degree) {
        final RealMatrix c = MatrixUtils.createRealMatrix(degree, degree);

        for (int i = 0; i < degree; i++) {
            c.setEntry(i, i, 2.0 * i + 1);
            if (i + 1 < degree) {
                // subdiagonal
                c.setEntry(i+1, i, -(i + 1));
            }
            if (i - 1 >= 0) {
                // superdiagonal
                c.setEntry(i-1, i, -i);
            }
        }

        return c;
    }
}
