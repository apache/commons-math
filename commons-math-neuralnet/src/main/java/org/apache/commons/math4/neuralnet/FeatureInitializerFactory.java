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

package org.apache.commons.math4.neuralnet;

import java.util.function.DoubleUnaryOperator;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;

/**
 * Creates functions that will select the initial values of a neuron's
 * features.
 *
 * @since 3.3
 */
public final class FeatureInitializerFactory {
    /** Class contains only static methods. */
    private FeatureInitializerFactory() {}

    /**
     * Uniform sampling of the given range.
     *
     * @param min Lower bound of the range.
     * @param max Upper bound of the range.
     * @param rng Random number generator used to draw samples from a
     * uniform distribution.
     * @return an initializer such that the features will be initialized with
     * values within the given range.
     * @throws IllegalArgumentException if {@code min >= max}.
     */
    public static FeatureInitializer uniform(final UniformRandomProvider rng,
                                             final double min,
                                             final double max) {
        return randomize(new ContinuousUniformSampler(rng, min, max),
                         function(x -> 0, 0, 0));
    }

    /**
     * Creates an initializer from a univariate function {@code f(x)}.
     * The argument {@code x} is set to {@code init} at the first call
     * and will be incremented at each call.
     *
     * @param f Function.
     * @param init Initial value.
     * @param inc Increment
     * @return the initializer.
     */
    public static FeatureInitializer function(final DoubleUnaryOperator f,
                                              final double init,
                                              final double inc) {
        return new FeatureInitializer() {
            /** Argument. */
            private double arg = init;

            /** {@inheritDoc} */
            @Override
            public double value() {
                final double result = f.applyAsDouble(arg);
                arg += inc;
                return result;
            }
        };
    }

    /**
     * Adds some amount of random data to the given initializer.
     *
     * @param random Random variable distribution sampler.
     * @param orig Original initializer.
     * @return an initializer whose {@link FeatureInitializer#value() value}
     * method will return {@code orig.value() + random.sample()}.
     */
    public static FeatureInitializer randomize(final ContinuousUniformSampler random,
                                               final FeatureInitializer orig) {
        return new FeatureInitializer() {
            /** {@inheritDoc} */
            @Override
            public double value() {
                return orig.value() + random.sample();
            }
        };
    }
}
