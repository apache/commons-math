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

import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.ObjectSampler;
import org.apache.commons.rng.sampling.UnitSphereSampler;
import org.apache.commons.rng.simple.RandomSource;

/**
 * Factory for generating a cloud of points that approximate a circle.
 */
public class RandomCirclePointGenerator implements ObjectSampler<double[]> {
    /** RNG for the x-coordinate of the center. */
    private final ContinuousDistribution.Sampler cX;
    /** RNG for the y-coordinate of the center. */
    private final ContinuousDistribution.Sampler cY;
    /** Sampler for the unit circle position of the point. */
    private final UnitSphereSampler sampler;
    /** Radius of the circle. */
    private final double radius;

    /**
     * @param x Abscissa of the circle center.
     * @param y Ordinate of the circle center.
     * @param radius Radius of the circle.
     * @param xSigma Error on the x-coordinate of the circumference points.
     * @param ySigma Error on the y-coordinate of the circumference points.
     */
    public RandomCirclePointGenerator(double x,
                                      double y,
                                      double radius,
                                      double xSigma,
                                      double ySigma) {
        final UniformRandomProvider rng = RandomSource.XO_SHI_RO_256_PP.create();
        this.radius = radius;
        cX = NormalDistribution.of(x, xSigma).createSampler(rng);
        cY = NormalDistribution.of(y, ySigma).createSampler(rng);
        sampler = UnitSphereSampler.of(rng, 2);
    }

    @Override
    public double[] sample() {
        // Sample on a unit circle
        final double[] xy = sampler.sample();
        // Scale the circle and add error
        xy[0] = radius * xy[0] + cX.sample();
        xy[1] = radius * xy[1] + cY.sample();
        return xy;
    }
}
