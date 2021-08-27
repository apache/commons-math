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

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.UnaryOperator;
import java.util.function.DoublePredicate;

import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.optim.PointValuePair;

/**
 * <a href="https://scholarship.rice.edu/handle/1911/16304">Multi-directional</a> search method.
 */
public class MultiDirectionalTransform
    implements Simplex.TransformFactory {
    /** Reflection coefficient. */
    private static final double ALPHA = 1;
    /** Default value for {@link #gamma}: {@value}. */
    private static final double DEFAULT_GAMMA = 2;
    /** Default value for {@link #sigma}: {@value}. */
    private static final double DEFAULT_SIGMA = 0.5;
    /** Expansion coefficient. */
    private final double gamma;
    /** Contraction coefficient. */
    private final double sigma;

    /**
     * @param gamma Expansion coefficient.
     * @param sigma Shrinkage coefficient.
     */
    public MultiDirectionalTransform(double gamma,
                                     double sigma) {
        if (gamma < 1) {
            throw new IllegalArgumentException("gamma: " + gamma);
        }
        if (sigma < 0 ||
            sigma > 1) {
            throw new IllegalArgumentException("sigma: " + sigma);
        }

        this.gamma = gamma;
        this.sigma = sigma;
    }

    /**
     * Transform with default values.
     */
    public MultiDirectionalTransform() {
        this(DEFAULT_GAMMA,
             DEFAULT_SIGMA);
    }

    /** {@inheritDoc} */
    @Override
    public UnaryOperator<Simplex> create(final MultivariateFunction evaluationFunction,
                                         final Comparator<PointValuePair> comparator,
                                         final DoublePredicate sa) {
        return original -> {
            final PointValuePair best = original.get(0);

            // Perform a reflection step.
            final Simplex reflectedSimplex = transform(original,
                                                       ALPHA,
                                                       comparator,
                                                       evaluationFunction);
            final PointValuePair reflectedBest = reflectedSimplex.get(0);

            if (comparator.compare(reflectedBest, best) < 0) {
                // Compute the expanded simplex.
                final Simplex expandedSimplex = transform(original,
                                                          gamma,
                                                          comparator,
                                                          evaluationFunction);
                final PointValuePair expandedBest = expandedSimplex.get(0);

                if (comparator.compare(expandedBest, reflectedBest) <= 0 ||
                    (sa != null &&
                     sa.test(expandedBest.getValue() - reflectedBest.getValue()))) {
                    return expandedSimplex;
                } else {
                    return reflectedSimplex;
                }
            } else {
                // Compute the contracted simplex.
                return original.shrink(sigma, evaluationFunction);
            }
        };
    }

    /**
     * Computes and evaluates a new simplex.
     *
     * @param original Original simplex.
     * @param coeff Linear coefficient.
     * @param comp Fitness comparator.
     * @param evalFunc Objective function.
     * @return the transformed simplex.
     * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException
     * if the maximal number of evaluations is exceeded.
     */
    private Simplex transform(Simplex original,
                              double coeff,
                              Comparator<PointValuePair> comp,
                              MultivariateFunction evalFunc) {
        // Transformed simplex is the result a linear transformation on all
        // points except the first one.
        final int replSize = original.getSize() - 1;
        final List<PointValuePair> replacement = new ArrayList<>();
        final double[] bestPoint = original.get(0).getPoint();
        for (int i = 0; i < replSize; i++) {
            replacement.add(Simplex.newPoint(bestPoint,
                                             -coeff,
                                             original.get(i + 1).getPoint(),
                                             evalFunc));
        }

        return original.replaceLast(replacement).evaluate(evalFunc, comp);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Multidirectional [g=" + gamma +
            " s=" + sigma + "]";
    }
}
