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

package org.apache.commons.math4.legacy.random;

import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * Factory for creating generators of miscellaneous data.
 *
 * @since 4.0
 */
public final class RandomUtils {
    /**
     * Class contains only static methods.
     */
    private RandomUtils() {}

    /**
     * @param rng Underlying generator. Reference is copied so the RNG
     * is shared with the caller.
     * @return a {@link DataGenerator data generator}.
     */
    public static DataGenerator createDataGenerator(final UniformRandomProvider rng) {
        return new DataGenerator(rng);
    }

    /**
     * Various random data generation routines.
     */
    public static class DataGenerator {
        /** Underlying RNG. */
        private final UniformRandomProvider rng;

        /**
         * @param rng Underlying generator.
         */
        DataGenerator(UniformRandomProvider rng) {
            this.rng = rng;
        }

        /**
         * Generates a uniformly distributed random long integer between {@code lower}
         * and {@code upper} (endpoints included).
         *
         * @param lower Lower bound for generated long integer.
         * @param upper Upper bound for generated long integer.
         * @return a random long integer greater than or equal to {@code lower}
         * and less than or equal to {@code upper}
         * @throws NumberIsTooLargeException if {@code lower >= upper}
         */
        public long nextLong(final long lower,
                             final long upper) {
            if (lower >= upper) {
                throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                                                    lower, upper, false);
            }
            final long max = (upper - lower) + 1;
            if (max <= 0) {
                // Range is too wide to fit in a positive long (larger than 2^63);
                // as it covers more than half the long range, we use directly a
                // simple rejection method.
                while (true) {
                    final long r = rng.nextLong();
                    if (r >= lower && r <= upper) {
                        return r;
                    }
                }
            } else if (max < Integer.MAX_VALUE){
                // We can shift the range and generate directly a positive int.
                return lower + rng.nextInt((int) max);
            } else {
                // We can shift the range and generate directly a positive long.
                return lower + rng.nextLong(max);
            }
        }
    }
}
