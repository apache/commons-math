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

import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.math4.legacy.exception.MathInternalError;
import org.apache.commons.math4.legacy.exception.NotANumberException;
import org.apache.commons.math4.legacy.exception.NotFiniteNumberException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * Factory for creating generators of miscellaneous data.
 *
 * @since 4.0
 */
public class RandomUtils {
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

        /**
         * Generates a uniformly distributed random value from the open interval
         * {@code (lower, upper)} (i.e., endpoints excluded).
         * <p>
         * <strong>Definition</strong>:
         * <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda3662.htm">
         * Uniform Distribution</a> {@code lower} and {@code upper - lower} are the
         * <a href = "http://www.itl.nist.gov/div898/handbook/eda/section3/eda364.htm">
         * location and scale parameters</a>, respectively.</p>
         * <p>
         * <strong>Algorithm Description</strong>: scales the output of
         * Random.nextDouble(), but rejects 0 values (i.e., will generate another
         * random double if Random.nextDouble() returns 0). This is necessary to
         * provide a symmetric output interval (both endpoints excluded).
         * </p>
         *
         * @param lower Lower bound of the support (excluded).
         * @param upper Upper bound of the support (excluded).
         * @return a uniformly distributed random value between lower and upper
         * (both excluded).
         * @throws NumberIsTooLargeException if {@code lower >= upper}.
         * @throws NotFiniteNumberException if one of the bounds is infinite.
         * @throws NotANumberException if one of the bounds is NaN.
         */
        public double nextUniform(double lower, double upper) {
            return nextUniform(lower, upper, false);
        }

        /**
         * Generates a uniformly distributed random value from the interval
         * {@code (lower, upper)} or the interval {@code [lower, upper)}. The lower
         * bound is thus optionally included, while the upper bound is always
         * excluded.
         * <p>
         * <strong>Definition</strong>:
         * <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda3662.htm">
         * Uniform Distribution</a> {@code lower} and {@code upper - lower} are the
         * <a href = "http://www.itl.nist.gov/div898/handbook/eda/section3/eda364.htm">
         * location and scale parameters</a>, respectively.</p>
         * <p>
         * <strong>Algorithm Description</strong>: if the lower bound is excluded,
         * scales the output of "nextDouble()", but rejects 0 values (i.e. it
         * will generate another random double if "nextDouble()" returns 0).
         * This is necessary to provide a symmetric output interval (both
         * endpoints excluded).
         * </p>
         *
         * @param lower Lower bound of the support.
         * @param upper Exclusive upper bound of the support.
         * @param lowerInclusive {@code true} if the lower bound is inclusive.
         * @return a uniformly distributed random value in the {@code (lower, upper)}
         * interval, if {@code lowerInclusive} is {@code false}, or in the
         * {@code [lower, upper)} interval, if {@code lowerInclusive} is
         * {@code true}.
         * @throws NumberIsTooLargeException if {@code lower >= upper}.
         * @throws NotFiniteNumberException if one of the bounds is infinite.
         * @throws NotANumberException if one of the bounds is NaN.
         */
        public double nextUniform(double lower,
                                  double upper,
                                  boolean lowerInclusive) {
            if (lower >= upper) {
                throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                                                    lower, upper, false);
            }
            if (Double.isInfinite(lower)) {
                throw new NotFiniteNumberException(LocalizedFormats.INFINITE_BOUND, lower);
            }
            if (Double.isInfinite(upper)) {
                throw new NotFiniteNumberException(LocalizedFormats.INFINITE_BOUND, upper);
            }
            if (Double.isNaN(lower) || Double.isNaN(upper)) {
                throw new NotANumberException();
            }

            // Ensure nextDouble() isn't 0.0
            double u = rng.nextDouble();
            while (!lowerInclusive && u <= 0.0) {
                u = rng.nextDouble();
            }

            return u * upper + (1.0 - u) * lower;
        }
    }
}
