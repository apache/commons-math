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

import org.apache.commons.math.optimization.ObjectiveException;
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

    /** Compute the next simplex of the algorithm.
     * @exception ObjectiveException if the function cannot be evaluated at
     * some point
     */
    protected void iterateSimplex() throws ObjectiveException {

        while (true) {

            // save the original vertex
            final PointValuePair[] original = simplex;
            final double originalValue = original[0].getValue();

            // perform a reflection step
            final double reflectedValue = evaluateNewSimplex(original, 1.0);
            if (reflectedValue < originalValue) {

                // compute the expanded simplex
                final PointValuePair[] reflected = simplex;
                final double expandedValue = evaluateNewSimplex(original, khi);
                if (reflectedValue <= expandedValue) {
                    // accept the reflected simplex
                    simplex = reflected;
                }

                return;

            }

            // compute the contracted simplex
            final double contractedValue = evaluateNewSimplex(original, gamma);
            if (contractedValue < originalValue) {
                // accept the contracted simplex
                return;
            }

        }

    }

    /** Compute and evaluate a new simplex.
     * @param original original simplex (to be preserved)
     * @param coeff linear coefficient
     * @return smallest value in the transformed simplex
     * @exception ObjectiveException if the function cannot be evaluated at
     * some point
     */
    private double evaluateNewSimplex(final PointValuePair[] original,
                                      final double coeff)
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
        evaluateSimplex();
        return simplex[0].getValue();

    }

}
