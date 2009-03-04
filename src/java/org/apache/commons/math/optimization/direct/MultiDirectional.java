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

package org.apache.commons.math.optimization.direct;

import java.util.Comparator;

import org.apache.commons.math.optimization.ObjectiveException;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.PointValuePair;

/** 
 * This class implements the multi-directional direct search method.
 *
 * @version $Revision$ $Date$
 * @see NelderMead
 * @since 1.2
 */
public class MultiDirectional extends DirectSearchOptimizer {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -5347711305645019145L;

    /** Expansion coefficient. */
    private final double khi;

    /** Contraction coefficient. */
    private final double gamma;

    /** Build a multi-directional optimizer with default coefficients.
     * <p>The default values are 2.0 for khi and 0.5 for gamma.</p>
     */
    public MultiDirectional() {
        this.khi   = 2.0;
        this.gamma = 0.5;
    }

    /** Build a multi-directional optimizer with specified coefficients.
     * @param khi expansion coefficient
     * @param gamma contraction coefficient
     */
    public MultiDirectional(final double khi, final double gamma) {
        this.khi   = khi;
        this.gamma = gamma;
    }

    /** {@inheritDoc} */
    protected void iterateSimplex(final Comparator<PointValuePair> comparator)
        throws ObjectiveException, OptimizationException, IllegalArgumentException {

        final int max = getMaxEvaluations();
        while (getEvaluations() < max) {

            // save the original vertex
            final PointValuePair[] original = simplex;
            final PointValuePair best = original[0];

            // perform a reflection step
            final PointValuePair reflected = evaluateNewSimplex(original, 1.0, comparator);
            if (comparator.compare(reflected, best) < 0) {

                // compute the expanded simplex
                final PointValuePair[] reflectedSimplex = simplex;
                final PointValuePair expanded = evaluateNewSimplex(original, khi, comparator);
                if (comparator.compare(reflected, expanded) <= 0) {
                    // accept the reflected simplex
                    simplex = reflectedSimplex;
                }

                return;

            }

            // compute the contracted simplex
            final PointValuePair contracted = evaluateNewSimplex(original, gamma, comparator);
            if (comparator.compare(contracted, best) < 0) {
                // accept the contracted simplex
                return;
            }

        }

        throw new OptimizationException(
                "maximal number of evaluations exceeded ({0})",
                getEvaluations());

    }

    /** Compute and evaluate a new simplex.
     * @param original original simplex (to be preserved)
     * @param coeff linear coefficient
     * @param comparator comparator to use to sort simplex vertices from best to poorest
     * @return best point in the transformed simplex
     * @exception ObjectiveException if the function cannot be evaluated at
     * some point
     */
    private PointValuePair evaluateNewSimplex(final PointValuePair[] original,
                                              final double coeff,
                                              final Comparator<PointValuePair> comparator)
        throws ObjectiveException {

        final double[] xSmallest = original[0].getPoint();
        final int n = xSmallest.length;

        // create the linearly transformed simplex
        simplex = new PointValuePair[n + 1];
        simplex[0] = original[0];
        for (int i = 1; i <= n; ++i) {
            final double[] xOriginal    = original[i].getPoint();
            final double[] xTransformed = new double[n];
            for (int j = 0; j < n; ++j) {
                xTransformed[j] = xSmallest[j] + coeff * (xSmallest[j] - xOriginal[j]);
            }
            simplex[i] = new PointValuePair(xTransformed, Double.NaN);
        }

        // evaluate it
        evaluateSimplex(comparator);
        return simplex[0];

    }

}
