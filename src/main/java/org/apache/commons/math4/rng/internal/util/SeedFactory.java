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
package org.apache.commons.math4.rng.internal.util;

import org.apache.commons.math4.rng.internal.source32.RandomIntSource;
import org.apache.commons.math4.rng.internal.source32.Well44497b;
import org.apache.commons.math4.rng.internal.source64.RandomLongSource;
import org.apache.commons.math4.rng.internal.source64.SplitMix64;

/**
 * Utilities related to seeding.
 *
 * <p>
 * This class provides methods to generate random seeds (single values
 * or arrays of values, of {@code int} or {@code long} types) that can
 * be passed to the {@link org.apache.commons.math4.rng.RandomSource
 * methods that create a generator instance}.
 * <br>
 * Although the seed-generating methods defined in this class will likely
 * return different values for all calls, there is no guarantee that the
 * produced seed will result always in a "good" sequence of numbers (even
 * if the generator initialized with that seed is good).
 * <br>
 * There is <i>no guarantee</i> that sequences will not overlap.
 * </p>
 *
 * @since 4.0
 */
public class SeedFactory {
    /** Generator with a long period. */
    private static final RandomIntSource SEED_GENERATOR;

    static {
        // Another RNG for initializing the "SEED_GENERATOR".
        final long t = System.currentTimeMillis();
        final int h = System.identityHashCode(Runtime.getRuntime());
        final SplitMix64 rng = new SplitMix64(t ^ NumberFactory.makeLong(h, ~h));

        final int blockCount = 1391; // Size of the state array of "Well44497b".
        SEED_GENERATOR = new Well44497b(createIntArray(blockCount, rng));
    }

    /**
     * Class contains only static methods.
     */
    private SeedFactory() {}

    /**
     * Creates a number for use as a seed.
     *
     * @return a random number.
     */
    public static int createInt() {
        return createInt(SEED_GENERATOR, System.identityHashCode(new Object()));
    }

    /**
     * Creates a number for use as a seed.
     *
     * @return a random number.
     */
    public static long createLong() {
        return createLong(SEED_GENERATOR, System.identityHashCode(new Object()));
    }

    /**
     * Creates an array of numbers for use as a seed.
     *
     * @param n Size of the array to create.
     * @return an array of {@code n} random numbers.
     */
    public static int[] createIntArray(int n) {
        return createIntArray(n, SEED_GENERATOR, new Object());
    }

    /**
     * Creates an array of numbers for use as a seed.
     *
     * @param n Size of the array to create.
     * @return an array of {@code n} random numbers.
     */
    public static long[] createLongArray(int n) {
        return createLongArray(n, SEED_GENERATOR, new Object());
    }

    /**
     * Creates an array of numbers for use as a seed.
     *
     * @param n Size of the array to create.
     * @param source Source of randomness.
     * @return an array of {@code n} random numbers drawn from the
     * {@code source}.
     */
    static long[] createLongArray(int n,
                                  RandomIntSource source) {
        return createLongArray(n, source, null);
    }

    /**
     * Creates an array of numbers for use as a seed.
     *
     * @param n Size of the array to create.
     * @param source Source of randomness.
     * @return an array of {@code n} random numbers drawn from the
     * {@code source}.
     */
    static int[] createIntArray(int n,
                                RandomLongSource source) {
        return createIntArray(n, source, null);
    }

    /**
     * Creates an array of numbers for use as a seed.
     *
     * @param n Size of the array to create.
     * @param source Source of randomness.
     * @return an array of {@code n} random numbers drawn from the
     * {@code source}.
     */
    static int[] createIntArray(int n,
                                RandomIntSource source) {
        return createIntArray(n, source, null);
    }

    /**
     * Creates an array of numbers for use as a seed.
     *
     * @param n Size of the array to create.
     * @param source Source of randomness.
     * @param h Arbitrary object whose {@link System#identityHashCode(Object)
     * hash code} will be combined with the next number drawn from
     * the {@code source}.
     * @return an array of {@code n} random numbers.
     */
    private static long[] createLongArray(int n,
                                          RandomIntSource source,
                                          Object h) {
        final long[] array = new long[n];

        final int hash = System.identityHashCode(h);
        for (int i = 0; i < n; i++) {
            array[i] = createLong(source, hash);
        }

        return array;
    }

    /**
     * Creates an array of numbers for use as a seed.
     *
     * @param n Size of the array to create.
     * @param source Source of randomness.
     * @param h Arbitrary object whose {@link System#identityHashCode(Object)
     * hash code} will be combined with the next number drawn from
     * the {@code source}.
     * @return an array of {@code n} random numbers.
     */
    private static int[] createIntArray(int n,
                                        RandomLongSource source,
                                        Object h) {
        final int[] array = new int[n];

        final int hash = System.identityHashCode(h);
        for (int i = 0; i < n; i += 2) {
            final long v = createLong(source, hash);

            array[i] = NumberFactory.extractHi(v);

            if (i + 1 < n) {
                array[i + 1] = NumberFactory.extractLo(v);
            }
        }

        return array;
    }

    /**
     * Creates an array of numbers for use as a seed.
     *
     * @param n Size of the array to create.
     * @param source Source of randomness.
     * @param h Arbitrary object whose {@link System#identityHashCode(Object)
     * hash code} will be combined with the next number drawn from
     * the {@code source}.
     * @return an array of {@code n} random numbers.
     */
    private static int[] createIntArray(int n,
                                        RandomIntSource source,
                                        Object h) {
        final int[] array = new int[n];

        final int hash = System.identityHashCode(h);
        for (int i = 0; i < n; i++) {
            array[i] = createInt(source, hash);
        }

        return array;
    }

    /**
     * Creates a random number by performing an "xor" between the
     * next value in the sequence of the {@code source} and the
     * given {@code number}.
     *
     * @param source Source of randomness.
     * @param number Arbitrary number.
     * @return a random number.
     */
    private static long createLong(RandomLongSource source,
                                   int number) {
        synchronized (source) {
            return source.next() ^ NumberFactory.makeLong(number, number);
        }
    }

    /**
     * Creates a random number by performing an "xor" between the
     * the next value in the sequence of the {@code source} and the
     * given {@code number}.
     *
     * @param source Source of randomness.
     * @param number Arbitrary number.
     * @return a random number.
     */
    private static long createLong(RandomIntSource source,
                                   int number) {
        synchronized (source) {
            return NumberFactory.makeLong(source.next() ^ number,
                                          source.next() ^ number);
        }
    }

    /**
     * Creates a random number by performing an "xor" between the
     * next value in the sequence of the {@code source} and the
     * given {@code number}.
     *
     * @param source Source of randomness.
     * @param number Arbitrary number.
     * @return a random number.
     */
    private static int createInt(RandomIntSource source,
                                 int number) {
        synchronized (source) {
            return source.next() ^ number;
        }
    }
}
