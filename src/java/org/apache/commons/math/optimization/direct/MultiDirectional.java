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
import org.apache.commons.math.optimization.ScalarPointValuePair;

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
    protected void iterateSimplex(final Comparator<ScalarPointValuePair> comparator)
        throws ObjectiveException, OptimizationException, IllegalArgumentException {

        while (true) {

            incrementIterationsCounter();

            // save the original vertex
            final ScalarPointValuePair[] original = simplex;
            final ScalarPointValuePair best = original[0];

            // perform a reflection step
            final ScalarPointValuePair reflected = evaluateNewSimplex(original, 1.0, comparator);
            if (comparator.compare(reflected, best) < 0) {

                // compute the expanded simplex
                final ScalarPointValuePair[] reflectedSimplex = simplex;
                final ScalarPointValuePair expanded = evaluateNewSimplex(original, khi, comparator);
                if (comparator.compare(reflected, expanded) <= 0) {
                    // accept the reflected simplex
                    simplex = reflectedSimplex;
                }

                return;

            }

            // compute the contracted simplex
            final ScalarPointValuePair contracted = evaluateNewSimplex(original, gamma, comparator);
            if (comparator.compare(contracted, best) < 0) {
                // accept the contracted simplex
                return;
            }

        }

    }

    /** Compute and evaluate a new simplex.
     * @param original original simplex (to be preserved)
     * @param coeff linear coefficient
     * @param comparator comparator to use to sort simplex vertices from best to poorest
     * @return best point in the transformed simplex
     * @exception ObjectiveException if the function cannot be evaluated at
     * some point
     */
    private ScalarPointValuePair evaluateNewSimplex(final ScalarPointValuePair[] original,
                                              final double coeff,
                                              final Comparator<ScalarPointValuePair> comparator)
        throws ObjectiveException {

        final double[] xSmallest = original[0].getPointRef();
        final int n = xSmallest.length;

        // create the linearly transformed simplex
        simplex = new ScalarPointValuePair[n + 1];
        simplex[0] = original[0];
        for (int i = 1; i <= n; ++i) {
            final double[] xOriginal    = original[i].getPointRef();
            final double[] xTransformed = new double[n];
            for (int j = 0; j < n; ++j) {
                xTransformed[j] = xSmallest[j] + coeff * (xSmallest[j] - xOriginal[j]);
            }
            simplex[i] = new ScalarPointValuePair(xTransformed, Double.NaN, false);
        }

        // evaluate it
        evaluateSimplex(comparator);
        return simplex[0];

    }

}
