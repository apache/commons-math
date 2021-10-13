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

package org.apache.commons.math4.legacy.fitting.leastsquares;

import java.awt.geom.Point2D;

import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.UniformContinuousDistribution;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

/**
 * Factory for generating a cloud of points that approximate a straight line.
 */
public class RandomStraightLinePointGenerator {
    /** Slope. */
    private final double slope;
    /** Intercept. */
    private final double intercept;
    /** RNG for the x-coordinate. */
    private final ContinuousDistribution.Sampler x;
    /** RNG for the error on the y-coordinate. */
    private final ContinuousDistribution.Sampler error;

    /**
     * The generator will create a cloud of points whose x-coordinates
     * will be randomly sampled between {@code xLo} and {@code xHi}, and
     * the corresponding y-coordinates will be computed as
     * <pre><code>
     *  y = a x + b + N(0, error)
     * </code></pre>
     * where {@code N(mean, sigma)} is a Gaussian distribution with the
     * given mean and standard deviation.
     *
     * @param a Slope.
     * @param b Intercept.
     * @param sigma Standard deviation on the y-coordinate of the point.
     * @param lo Lowest value of the x-coordinate.
     * @param hi Highest value of the x-coordinate.
     * @param seed RNG seed.
     */
    public RandomStraightLinePointGenerator(double a,
                                            double b,
                                            double sigma,
                                            double lo,
                                            double hi,
                                            long seed) {
        final UniformRandomProvider rng = RandomSource.WELL_44497_B.create(seed);
        slope = a;
        intercept = b;
        error = NormalDistribution.of(0, sigma).createSampler(rng);
        x = UniformContinuousDistribution.of(lo, hi).createSampler(rng);
    }

    /**
     * Point generator.
     *
     * @param n Number of points to create.
     * @return the cloud of {@code n} points.
     */
    public Point2D.Double[] generate(int n) {
        final Point2D.Double[] cloud = new Point2D.Double[n];
        for (int i = 0; i < n; i++) {
            cloud[i] = create();
        }
        return cloud;
    }

    /**
     * Create one point.
     *
     * @return a point.
     */
    private Point2D.Double create() {
        final double abscissa = x.sample();
        final double yModel = slope * abscissa + intercept;
        final double ordinate = yModel + error.sample();

        return new Point2D.Double(abscissa, ordinate);
    }
}
