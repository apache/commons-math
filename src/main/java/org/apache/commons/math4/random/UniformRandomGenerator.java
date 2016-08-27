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

import org.apache.commons.math4.util.FastMath;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * This class implements a normalized uniform random generator.
 *
 * <p>
 * It generates values from a uniform distribution with mean
 * equal to 0 and standard deviation equal to 1.
 * Generated values fall in the range \( [-\sqrt{3}, +\sqrt{3}] \).
 * </p>
 *
 * @since 1.2
 */
public class UniformRandomGenerator implements NormalizedRandomGenerator {
    /** Square root of three. */
    private static final double SQRT3 = FastMath.sqrt(3);
    /** Underlying generator. */
    private final UniformRandomProvider generator;

    /**
     * Creates a new generator.
     *
     * @param generator Underlying random generator.
     */
    public UniformRandomGenerator(UniformRandomProvider generator) {
        this.generator = generator;
    }

    /**
     * Generates a random scalar with zero mean and unit standard deviation.
     *
     * @return a random scalar in the range \( [-\sqrt{3}, +\sqrt{3}] \).
     */
    @Override
    public double nextNormalizedDouble() {
        return SQRT3 * (2 * generator.nextDouble() - 1);
    }
}
