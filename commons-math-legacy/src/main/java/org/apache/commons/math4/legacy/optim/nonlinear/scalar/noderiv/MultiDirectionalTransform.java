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
package org.apache.commons.math4.legacy.optim.nonlinear.scalar.noderiv;

import java.util.Comparator;
import java.util.function.UnaryOperator;

import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.OptimizationData;

/**
 * Multi-directional search method.
 */
public class MultiDirectionalTransform
    implements Simplex.TransformFactory,
               OptimizationData {
    /** Default value for {@link #gamma}: {@value}. */
    private static final double DEFAULT_GAMMA = 2;
    /** Default value for {@link #rho}: {@value}. */
    private static final double DEFAULT_RHO = 0.5;
    /** Expansion coefficient. */
    private final double gamma;
    /** Contraction coefficient. */
    private final double rho;

    /**
     * @param gamma Expansion coefficient.
     * @param rho Contraction coefficient.
     */
    public MultiDirectionalTransform(double gamma,
                                     double rho) {
        this.gamma = gamma;
        this.rho = rho;
    }

    /**
     * Transform with default values.
     */
    public MultiDirectionalTransform() {
        this(DEFAULT_GAMMA,
             DEFAULT_RHO);
    }

    /** {@inheritDoc} */
    @Override
    public UnaryOperator<Simplex> apply(final MultivariateFunction evaluationFunction,
                                        final Comparator<PointValuePair> comparator) {
        return original -> {
            final PointValuePair best = original.get(0);

            // Perform a reflection step.
            final Simplex reflectedSimplex = transform(original, 1);
            final PointValuePair reflectedBest = reflectedSimplex.get(0);

            if (comparator.compare(reflectedBest, best) < 0) {
                // Compute the expanded simplex.
                final Simplex expandedSimplex = transform(original, gamma);
                final PointValuePair expandedBest = expandedSimplex.get(0);

                return comparator.compare(reflectedBest, expandedBest) <= 0 ?
                    reflectedSimplex :
                    expandedSimplex;
            } else {
                // Compute the contracted simplex.
                return transform(original, rho);
            }
        };
    }

    /**
     * Computes and evaluates a new simplex.
     *
     * @param original Original simplex.
     * @param coeff Linear coefficient.
     * @return the transformed simplex.
     * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException
     * if the maximal number of evaluations is exceeded.
     */
    private Simplex transform(Simplex original,
                              double coeff) {
        // Transformed simplex is the result a linear transformation on all
        // points except the first one.
        final int dim = original.getDimension();
        final int len = original.getSize();
        final double[][] simplex = new double[len][];

        simplex[0] = original.get(0).getPoint();
        final double[] xSmallest = simplex[0];

        for (int i = 1; i < len; i++) {
            final double[] xOriginal = original.get(i).getPoint();
            final double[] xTransformed = new double[dim];
            for (int j = 0; j < dim; j++) {
                xTransformed[j] = xSmallest[j] + coeff * (xSmallest[j] - xOriginal[j]);
            }
            simplex[i] = xTransformed;
        }

        return Simplex.of(simplex);
    }
}
