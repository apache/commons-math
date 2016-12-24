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
package org.apache.commons.math4.fitting.leastsquares;

import org.apache.commons.math4.distribution.NormalDistribution;
import org.apache.commons.math4.distribution.RealDistribution;
import org.apache.commons.math4.distribution.UniformRealDistribution;
import org.apache.commons.math4.geometry.euclidean.twod.Vector2D;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.MathUtils;

/**
 * Factory for generating a cloud of points that approximate a circle.
 */
public class RandomCirclePointGenerator {
    /** RNG for the x-coordinate of the center. */
    private final RealDistribution.Sampler cX;
    /** RNG for the y-coordinate of the center. */
    private final RealDistribution.Sampler cY;
    /** RNG for the parametric position of the point. */
    private final RealDistribution.Sampler tP;
    /** Radius of the circle. */
    private final double radius;

    /**
     * @param x Abscissa of the circle center.
     * @param y Ordinate of the circle center.
     * @param radius Radius of the circle.
     * @param xSigma Error on the x-coordinate of the circumference points.
     * @param ySigma Error on the y-coordinate of the circumference points.
     * @param seed RNG seed.
     */
    public RandomCirclePointGenerator(double x,
                                      double y,
                                      double radius,
                                      double xSigma,
                                      double ySigma,
                                      long seed) {
        final UniformRandomProvider rng = RandomSource.create(RandomSource.WELL_44497_B, seed);
        this.radius = radius;
        cX = new NormalDistribution(x, xSigma).createSampler(rng);
        cY = new NormalDistribution(y, ySigma).createSampler(rng);
        tP = new UniformRealDistribution(0, MathUtils.TWO_PI).createSampler(rng);
    }

    /**
     * Point generator.
     *
     * @param n Number of points to create.
     * @return the cloud of {@code n} points.
     */
    public Vector2D[] generate(int n) {
        final Vector2D[] cloud = new Vector2D[n];
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
    private Vector2D create() {
        final double t = tP.sample();
        final double pX = cX.sample() + radius * FastMath.cos(t);
        final double pY = cY.sample() + radius * FastMath.sin(t);

        return new Vector2D(pX, pY);
    }
}
