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
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.OptimizationData;

/**
 * <a href="https://en.wikipedia.org/wiki/Nelder%E2%80%93Mead_method">Nelder-Mead method</a>.
 */
public class NelderMeadTransform
    implements Simplex.TransformFactory,
               OptimizationData {
    /** Default value for {@link #alpha}: {@value}. */
    private static final double DEFAULT_ALPHA = 1;
    /** Default value for {@link #gamma}: {@value}. */
    private static final double DEFAULT_GAMMA = 2;
    /** Default value for {@link #rho}: {@value}. */
    private static final double DEFAULT_RHO = 0.5;
    /** Default value for {@link #sigma}: {@value}. */
    private static final double DEFAULT_SIGMA = 0.5;
    /** Reflection coefficient. */
    private final double alpha;
    /** Expansion coefficient. */
    private final double gamma;
    /** Contraction coefficient. */
    private final double rho;
    /** Shrinkage coefficient. */
    private final double sigma;

    /**
     * @param alpha Reflection coefficient.
     * @param gamma Expansion coefficient.
     * @param rho Contraction coefficient.
     * @param sigma Shrinkage coefficient.
     */
    public NelderMeadTransform(double alpha,
                               double gamma,
                               double rho,
                               double sigma) {
        this.alpha = alpha;
        this.gamma = gamma;
        this.rho = rho;
        this.sigma = sigma;
    }

    /**
     * Transform with default values.
     */
    public NelderMeadTransform() {
        this(DEFAULT_ALPHA,
             DEFAULT_GAMMA,
             DEFAULT_RHO,
             DEFAULT_SIGMA);
    }

    /** {@inheritDoc} */
    @Override
    public UnaryOperator<Simplex> apply(final MultivariateFunction evaluationFunction,
                                        final Comparator<PointValuePair> comparator) {
        return (original) -> {
            Simplex newSimplex = original;

            // The simplex has n + 1 points if dimension is n.
            final int n = newSimplex.getDimension();

            // Interesting values.
            final PointValuePair best = newSimplex.get(0);
            final PointValuePair secondWorst = newSimplex.get(n - 1);
            final PointValuePair worst = newSimplex.get(n);
            final double[] xWorst = worst.getPoint();

            // Centroid of the best vertices, dismissing the worst point (at index n).
            final double[] centroid = new double[n];
            for (int i = 0; i < n; i++) {
                final double[] x = newSimplex.get(i).getPoint();
                for (int j = 0; j < n; j++) {
                    centroid[j] += x[j];
                }
            }
            final double scaling = 1d / n;
            for (int j = 0; j < n; j++) {
                centroid[j] *= scaling;
            }

            // Reflection.
            final PointValuePair reflected = newPoint(centroid,
                                                      -alpha,
                                                      xWorst,
                                                      evaluationFunction);
            if (comparator.compare(reflected, secondWorst) < 0 &&
                comparator.compare(best, reflected) <= 0) {
                return newSimplex.withReplacement(n, reflected);
            }

            if (comparator.compare(reflected, best) < 0) {
                // Expansion.
                final PointValuePair expanded = newPoint(centroid,
                                                         -gamma,
                                                         xWorst,
                                                         evaluationFunction);
                if (comparator.compare(expanded, reflected) < 0) {
                    return newSimplex.withReplacement(n, expanded);
                } else {
                    return newSimplex.withReplacement(n, reflected);
                }
            }

            if (comparator.compare(reflected, worst) < 0) {
                // Outside contraction.
                final PointValuePair contracted = newPoint(centroid,
                                                           rho,
                                                           reflected.getPoint(),
                                                           evaluationFunction);
                if (comparator.compare(contracted, reflected) < 0) {
                    return newSimplex.withReplacement(n, contracted); // Accept contracted point.
                }
            } else {
                // Inside contraction.
                final PointValuePair contracted = newPoint(centroid,
                                                           rho,
                                                           xWorst,
                                                           evaluationFunction);
                if (comparator.compare(contracted, worst) < 0) {
                    return newSimplex.withReplacement(n, contracted); // Accept contracted point.
                }
            }

            // Shrink.
            final int len = newSimplex.getSize();
            final double[] xBest = best.getPoint();
            for (int i = 1; i < len; i++) {
                newSimplex = newSimplex.withReplacement(i, newPoint(xBest,
                                                                    sigma,
                                                                    newSimplex.get(i).getPoint(),
                                                                    evaluationFunction));
            }
            return newSimplex;
        };
    }

    /**
     * Evaluates point with coordinates \( a_i + s (b_i - a_i) \).
     *
     * @param a Cartesian coordinates.
     * @param s Scaling factor.
     * @param b Cartesian coordinates.
     * @param eval Evaluation function.
     * @return a new point.
     */
    private static PointValuePair newPoint(double[] a,
                                           double s,
                                           double[] b,
                                           MultivariateFunction eval) {
        final int dim = a.length;
        final double[] r = new double[dim];
        for (int i = 0; i < dim; i++) {
            final double m = a[i];
            r[i] = m + s * (b[i] - m);
        }

        return new PointValuePair(r, eval.value(r), false);
    }
}
