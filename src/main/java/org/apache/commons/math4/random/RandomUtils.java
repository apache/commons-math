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

import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.math4.exception.MathInternalError;
import org.apache.commons.math4.exception.NotANumberException;
import org.apache.commons.math4.exception.NotFiniteNumberException;
import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
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
     * Wraps an instance of the JDK's {@link Random} class.
     * The actual generation of random numbers will be delegated to that
     * instance.
     * <p>
     * If cryptographically secure data is required, one can use this
     * factory method, with an instance of the {@link java.security.SecureRandom}
     * class as the argument.
     * Note that data generation will be much slower in this case.
     * </p>
     *
     * @param rng Underlying generator. Reference is copied so the RNG
     * is shared with the caller.
     * @return a {@link DataGenerator data generator}.
     */
    public static DataGenerator createDataGenerator(final Random rng) {
        return createDataGenerator(asUniformRandomProvider(rng));
    }

    /**
     * Wraps a {@link Random} instance.
     *
     * @param rng JDK {@link Random} instance to which the random number
     * generation is delegated. Reference is copied so the RNG is shared
     * with the caller.
     * @return a {@link UniformRandomProvider} instance.
     */
    public static UniformRandomProvider asUniformRandomProvider(final Random rng) {
        return new UniformRandomProvider() {
            /** {@inheritDoc} */
            @Override
            public void nextBytes(byte[] bytes) {
                rng.nextBytes(bytes);
            }

            /** {@inheritDoc} */
            @Override
            public void nextBytes(byte[] bytes,
                                  int start,
                                  int len) {
                final byte[] reduced = new byte[len];
                rng.nextBytes(reduced);
                System.arraycopy(reduced, 0, bytes, start, len);
            }

            /** {@inheritDoc} */
            @Override
            public int nextInt() {
                return rng.nextInt();
            }

            /** {@inheritDoc} */
            @Override
            public int nextInt(int n) {
                if (n <= 0) {
                    throw new NotStrictlyPositiveException(n);
                }
                return rng.nextInt(n);
            }

            /** {@inheritDoc} */
            @Override
            public long nextLong() {
                return rng.nextLong();
            }

            /** {@inheritDoc} */
            @Override
            public long nextLong(long n) {
                // Code copied from "o.a.c.m.rng.internal.BaseProvider".

                if (n > 0) {
                    long bits;
                    long val;
                    do {
                        bits = rng.nextLong() >>> 1;
                        val  = bits % n;
                    } while (bits - val + (n - 1) < 0);
                    return val;
                }

                throw new NotStrictlyPositiveException(n);
            }

            /** {@inheritDoc} */
            @Override
            public boolean nextBoolean() {
                return rng.nextBoolean();
            }

            /** {@inheritDoc} */
            @Override
            public float nextFloat() {
                return rng.nextFloat();
            }

            /** {@inheritDoc} */
            @Override
            public double nextDouble() {
                return rng.nextDouble();
            }
        };
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
         * Generates a random string of hex characters of length {@code len}.
         *
         * <strong>Algorithm Description:</strong> how hexadecimal strings are
         * generated depends on the value of the {@code useSha1} argument.
         *
         * <ul>
         *  <li>If {@code useSha1 == false}, a 2-step process is used:
         *   <ol>
         *    <li>
         *     {@code len / 2 + 1} binary bytes are generated using the underlying
         *     generator.
         *    </li>
         *    <li>
         *     Each binary byte is translated into 2 hex digits.
         *    </li>
         *   </ol>
         *  </li>
         *  <li>
         *   If {@code useSha1 == true}, hex strings are generated in 40-byte
         *   segments using a 3-step process:
         *   <ol>
         *    <li>
         *     20 random bytes are generated using the underlying generator.
         *    </li>
         *    <li>
         *     SHA-1 hash is applied to yield a 20-byte binary digest.
         *    </li>
         *    <li>
         *     Each byte of the binary digest is converted to 2 hex digits.
         *    </li>
         *   </ol>
         *  </li>
         * </ul>
         *
         * @param len Length of the generated string.
         * @param useSha1 Whether to use a digest.
         * If {@code true} (resp. {@code false}), the 3-step (resp. 2-step)
         * process will be used.
         * @return the random string.
         * @throws NotStrictlyPositiveException if {@code len <= 0}.
         */
        public String nextHexString(int len,
                                    boolean useSha1) {
            if (len <= 0) {
                throw new NotStrictlyPositiveException(LocalizedFormats.LENGTH, len);
            }

            // Initialize output buffer.
            final StringBuilder outBuffer = new StringBuilder();

            if (!useSha1) {
                // Generate int(len/2)+1 random bytes.
                final byte[] randomBytes = new byte[(len / 2) + 1];
                rng.nextBytes(randomBytes);

                // Convert each byte to 2 hex digits.
                for (int i = 0; i < randomBytes.length; i++) {
                    final Integer c = Integer.valueOf(randomBytes[i]);

                    // Add 128 to byte value to make interval 0-255 before
                    // conversion to hex.
                    // This guarantees <= 2 hex digits from "toHexString".
                    // "toHexString" would otherwise add 2^32 to negative arguments.
                    String hex = Integer.toHexString(c.intValue() + 128);

                    // Make sure we add 2 hex digits for each byte.
                    if (hex.length() == 1) {
                        hex = "0" + hex;
                    }
                    outBuffer.append(hex);
                }
            } else {
                MessageDigest alg = null;
                try {
                    alg = MessageDigest.getInstance("SHA-1");
                } catch (NoSuchAlgorithmException ex) {
                    // Should never happen.
                    throw new MathInternalError(ex);
                }
                alg.reset();

                // Compute number of iterations required (40 bytes each).
                final int numIter = (len / 40) + 1;

                for (int iter = 1; iter < numIter + 1; iter++) {
                    final byte[] randomBytes = new byte[40];
                    rng.nextBytes(randomBytes);
                    alg.update(randomBytes);

                    // Create 20-byte binary hash.
                    final byte[] hash = alg.digest();

                    // Loop over the hash, converting each byte to 2 hex digits
                    for (int i = 0; i < hash.length; i++) {
                        final Integer c = Integer.valueOf(hash[i]);

                        // Add 128 to byte value to make interval 0-255.
                        // This guarantees <= 2 hex digits from "toHexString".
                        // "toHexString" would otherwise add 2^32 to negative arguments.
                        String hex = Integer.toHexString(c.intValue() + 128);

                        // Keep strings uniform length: guarantees 40 bytes.
                        if (hex.length() == 1) {
                            hex = "0" + hex;
                        }
                        outBuffer.append(hex);
                    }
                }
            }

            return outBuffer.toString().substring(0, len);
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
