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

package org.apache.commons.math4.random;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.distribution.RealDistribution;
import org.apache.commons.math4.distribution.NormalDistribution;

/**
 * Random generator that generates normally distributed samples.
 *
 * @since 1.2
 */
public class GaussianRandomGenerator implements NormalizedRandomGenerator {
    /** Gaussian distribution sampler. */
    private final RealDistribution.Sampler sampler;

    /**
     * Creates a new generator.
     *
     * @param generator Underlying random generator.
     */
    public GaussianRandomGenerator(final UniformRandomProvider generator) {
        sampler = new NormalDistribution().createSampler(generator);
    }

    /**
     * Generates a random scalar with zero mean and unit standard deviation.
     *
     * @return a random value sampled from a normal distribution.
     */
    @Override
    public double nextNormalizedDouble() {
        return sampler.sample();
    }
}
