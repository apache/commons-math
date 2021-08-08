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

package org.apache.commons.math4.ga.utils;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.simple.ThreadLocalRandomSource;

/**
 * An utility to generate per thread {@link UniformRandomProvider} instance.
 * @since 4.0
 */
public final class RandomGenerator {

    /**
     * constructs the singleton instance.
     */
    private RandomGenerator() {
    }

    /**
     * Returns the (static) random generator.
     * @return the static random generator shared by GA implementation classes
     */
    public static UniformRandomProvider getRandomGenerator() {
        return ThreadLocalRandomSource.current(RandomSource.XO_RO_SHI_RO_128_PP);
    }

}
