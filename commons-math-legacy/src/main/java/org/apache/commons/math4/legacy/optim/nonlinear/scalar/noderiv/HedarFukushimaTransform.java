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
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.UnaryOperator;
import java.util.function.DoublePredicate;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.optim.PointValuePair;

/**
 * DSSA algorithm.
 *
 * Described in
 * <blockquote>
 *  <em>Abdel-Rahman Hedar and Masao Fukushima (2002)</em>,
 *  <b>
 *   Hybrid simulated annealing and direct search method
 *   for nonlinear unconstrained global optimization
 *  </b>,
 *  Optimization Methods and Software, 17:5, 891-912,
 *  DOI: 10.1080/1055678021000030084
 * </blockquote>
 *
 * <p>
 * A note about the {@link #HedarFukushimaTransform(double) "shrink" factor}:
 * Per DSSA's description, the simplex must keep its size during the simulated
 * annealing (SA) phase to avoid premature convergence.  This assumes that the
 * best candidates from the SA phase will each subsequently serve as starting
 * point for another optimization to hone in on the local optimum.
 * Values lower than 1 and no subsequent "best list" search correspond to the
 * "SSA" algorithm in the above paper.
 */
public class HedarFukushimaTransform
    implements Simplex.TransformFactory {
    /** Shrinkage coefficient. */
    private final double sigma;
    /** Sampler for reflection coefficient. */
    private final ContinuousSampler alphaSampler;
    /** No shrink indicator. */
    private final boolean noShrink;

    /**
     * @param sigma Shrink factor.
     * @param rng Random generator.
     * @throws IllegalArgumentException if {@code sigma <= 0} or
     * {@code sigma > 1}.
     */
    public HedarFukushimaTransform(double sigma,
                                   UniformRandomProvider rng) {
        if (sigma <= 0 ||
            sigma > 1) {
            throw new IllegalArgumentException("Shrink factor out of range: " +
                                               sigma);
        }

        this.sigma = sigma;
        alphaSampler = ContinuousUniformSampler.of(rng, 0.9, 1.1);
        noShrink = sigma == 1d;
    }

    /**
     * @param sigma Shrink factor.
     * @throws IllegalArgumentException if {@code sigma <= 0} or
     * {@code sigma > 1}.
     */
    public HedarFukushimaTransform(double sigma) {
        this(sigma, RandomSource.KISS.create());
    }

    /**
     * Disable shrinking of the simplex (as mandated by DSSA).
     */
    public HedarFukushimaTransform() {
        this(1d);
    }

    /** {@inheritDoc} */
    @Override
    public UnaryOperator<Simplex> create(final MultivariateFunction evaluationFunction,
                                         final Comparator<PointValuePair> comparator,
                                         final DoublePredicate saAcceptance) {
        if (saAcceptance == null) {
            throw new IllegalArgumentException("Missing SA acceptance test");
        }

        return original -> transform(original,
                                     saAcceptance,
                                     evaluationFunction,
                                     comparator);
    }

    /**
     * Simulated annealing step (at fixed temperature).
     *
     * @param original Current simplex.  Points must be sorted from best to worst.
     * @param sa Simulated annealing acceptance test.
     * @param eval Evaluation function.
     * @param comp Objective function comparator.
     * @return a new instance.
     */
    private Simplex transform(Simplex original,
                              DoublePredicate sa,
                              MultivariateFunction eval,
                              Comparator<PointValuePair> comp) {
        final int size = original.getSize();
        // Current best point.
        final PointValuePair best = original.get(0);
        final double bestValue = best.getValue();

        for (int k = 1; k < size; k++) {
            // Perform reflections of the "k" worst points.
            final List<PointValuePair> reflected = reflectPoints(original, k, eval);
            Collections.sort(reflected, comp);

            // Check whether the best of the reflected points is better than the
            // current overall best.
            final PointValuePair candidate = reflected.get(0);
            final boolean candidateIsBetter = comp.compare(candidate, best) < 0;
            final boolean candidateIsAccepted = candidateIsBetter ||
                sa.test(candidate.getValue() - bestValue);

            if (candidateIsAccepted) {
                // Replace worst points with the reflected points.
                return original.replaceLast(reflected);
            }
        }

        // No direction provided a better point.
        return noShrink ?
            original :
            original.shrink(sigma, eval);
    }

    /**
     * @param simplex Current simplex (whose points must be sorted, from best
     * to worst).
     * @param nPoints Number of points to reflect.
     * The {@code nPoints} worst points will be reflected through the centroid
     * of the {@code n + 1 - nPoints} best points.
     * @param eval Evaluation function.
     * @return the (unsorted) list of reflected points.
     * @throws IllegalArgumentException if {@code nPoints < 1} or
     * {@code nPoints > n}.
     */
    private List<PointValuePair> reflectPoints(Simplex simplex,
                                               int nPoints,
                                               MultivariateFunction eval) {
        final int size = simplex.getSize();
        if (nPoints < 1 ||
            nPoints >= size) {
            throw new IllegalArgumentException("Out of range: " + nPoints);
        }

        final int nCentroid = size - nPoints;
        final List<PointValuePair> centroidList = simplex.asList(0, nCentroid);
        final List<PointValuePair> reflectList = simplex.asList(nCentroid, size);

        final double[] centroid = Simplex.centroid(centroidList);

        final List<PointValuePair> reflected = new ArrayList<>(nPoints);
        for (int i = 0; i < reflectList.size(); i++) {
            reflected.add(newReflectedPoint(reflectList.get(i),
                                            centroid,
                                            eval));
        }

        return reflected;
    }

    /**
     * @param point Current point.
     * @param centroid Coordinates through which reflection must be performed.
     * @param eval Evaluation function.
     * @return a new point with Cartesian coordinates set to the reflection
     * of {@code point} through {@code centroid}.
     */
    private PointValuePair newReflectedPoint(PointValuePair point,
                                             double[] centroid,
                                             MultivariateFunction eval) {
        final double alpha = alphaSampler.sample();
        return Simplex.newPoint(centroid,
                                -alpha,
                                point.getPoint(),
                                eval);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Hedar-Fukushima [s=" + sigma + "]";
    }
}
