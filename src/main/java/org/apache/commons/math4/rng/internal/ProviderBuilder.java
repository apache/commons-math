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
package org.apache.commons.math4.rng.internal;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.exception.MathInternalError;
import org.apache.commons.math4.rng.UniformRandomProvider;
import org.apache.commons.math4.rng.internal.util.SeedFactory;
import org.apache.commons.math4.rng.internal.util.NoOpConverter;
import org.apache.commons.math4.rng.internal.util.Int2Long;
import org.apache.commons.math4.rng.internal.util.Long2Int;
import org.apache.commons.math4.rng.internal.util.Long2IntArray;
import org.apache.commons.math4.rng.internal.util.Long2LongArray;
import org.apache.commons.math4.rng.internal.util.IntArray2LongArray;
import org.apache.commons.math4.rng.internal.util.LongArray2IntArray;
import org.apache.commons.math4.rng.internal.util.LongArray2Long;
import org.apache.commons.math4.rng.internal.util.IntArray2Int;
import org.apache.commons.math4.rng.internal.util.SeedConverter;
import org.apache.commons.math4.rng.internal.util.SeedConverterComposer;
import org.apache.commons.math4.rng.internal.source32.JDKRandom;
import org.apache.commons.math4.rng.internal.source32.Well512a;
import org.apache.commons.math4.rng.internal.source32.Well1024a;
import org.apache.commons.math4.rng.internal.source32.Well19937a;
import org.apache.commons.math4.rng.internal.source32.Well19937c;
import org.apache.commons.math4.rng.internal.source32.Well44497a;
import org.apache.commons.math4.rng.internal.source32.Well44497b;
import org.apache.commons.math4.rng.internal.source32.ISAACRandom;
import org.apache.commons.math4.rng.internal.source32.MersenneTwister;
import org.apache.commons.math4.rng.internal.source64.SplitMix64;
import org.apache.commons.math4.rng.internal.source64.XorShift1024Star;
import org.apache.commons.math4.rng.internal.source64.TwoCmres;
import org.apache.commons.math4.rng.internal.source64.MersenneTwister64;

/**
 * RNG builder.
 * <p>
 * It uses reflection to find the factory method of the RNG implementation,
 * and performs seed type conversions.
 * </p>
 */
public class ProviderBuilder {
    /** Length of the seed array (for random seed). */
    private static final int RANDOM_SEED_ARRAY_SIZE = 128;
    /** Seed converter. */
    private static final Long2Int LONG_TO_INT = new Long2Int();
    /** Seed converter. */
    private static final Int2Long INT_TO_LONG = new Int2Long();
    /** Seed converter. */
    private static final Long2IntArray LONG_TO_INT_ARRAY = new Long2IntArray(RANDOM_SEED_ARRAY_SIZE);
    /** Seed converter. */
    private static final Long2LongArray LONG_TO_LONG_ARRAY = new Long2LongArray(RANDOM_SEED_ARRAY_SIZE);
    /** Seed converter. */
    private static final LongArray2Long LONG_ARRAY_TO_LONG = new LongArray2Long();
    /** Seed converter. */
    private static final IntArray2Int INT_ARRAY_TO_INT = new IntArray2Int();
    /** Seed converter. */
    private static final LongArray2IntArray LONG_ARRAY_TO_INT_ARRAY = new LongArray2IntArray();
    /** Seed converter. */
    private static final IntArray2LongArray INT_ARRAY_TO_LONG_ARRAY = new IntArray2LongArray();
    /** Map to convert "Integer" seeds. */
    private static final Map<Class<?>, SeedConverter<Integer,?>> CONV_INT = new HashMap<>();
    /** Map to convert "int[]" seeds. */
    private static final Map<Class<?>, SeedConverter<int[],?>> CONV_INT_ARRAY = new HashMap<>();
    /** Map to convert "Long" seeds. */
    private static final Map<Class<?>, SeedConverter<Long,?>> CONV_LONG = new HashMap<>();
    /** Map to convert "long[]" seeds. */
    private static final Map<Class<?>, SeedConverter<long[],?>> CONV_LONG_ARRAY = new HashMap<>();

    static {
        // Input seed type is "Long".
        // Key is the implementation's "native" seed type.
        CONV_LONG.put(Integer.class, LONG_TO_INT);
        CONV_LONG.put(Long.class, new NoOpConverter<Long>());
        CONV_LONG.put(int[].class, LONG_TO_INT_ARRAY);
        CONV_LONG.put(long[].class, LONG_TO_LONG_ARRAY);

        // Input seed type is "Integer".
        // Key is the implementation's "native" seed type.
        CONV_INT.put(Integer.class, new NoOpConverter<Integer>());
        CONV_INT.put(Long.class, INT_TO_LONG);
        CONV_INT.put(int[].class, new SeedConverterComposer<Integer,Long,int[]>(INT_TO_LONG, LONG_TO_INT_ARRAY));
        CONV_INT.put(long[].class, new SeedConverterComposer<Integer,Long,long[]>(INT_TO_LONG, LONG_TO_LONG_ARRAY));

        // Input seed type is "int[]".
        // Key is the implementation's "native" seed type.
        CONV_INT_ARRAY.put(Integer.class, INT_ARRAY_TO_INT);
        CONV_INT_ARRAY.put(Long.class, new SeedConverterComposer<int[],Integer,Long>(INT_ARRAY_TO_INT, INT_TO_LONG));
        CONV_INT_ARRAY.put(int[].class, new NoOpConverter<int[]>());
        CONV_INT_ARRAY.put(long[].class, INT_ARRAY_TO_LONG_ARRAY);

        // Input seed type is "long[]".
        // Key is the implementation's "native" seed type.
        CONV_LONG_ARRAY.put(Integer.class, new SeedConverterComposer<long[],Long,Integer>(LONG_ARRAY_TO_LONG, LONG_TO_INT));
        CONV_LONG_ARRAY.put(Long.class, LONG_ARRAY_TO_LONG);
        CONV_LONG_ARRAY.put(int[].class, LONG_ARRAY_TO_INT_ARRAY);
        CONV_LONG_ARRAY.put(long[].class, new NoOpConverter<long[]>());
    }

    /**
     * Class only contains static method.
     */
    private ProviderBuilder() {}

    /**
     * Creates a RNG instance.
     *
     * @param source RNG specification.
     * @param seed Seed value.  It can be {@code null} (in which case a
     * random value will be used).
     * @param args Additional arguments to the implementation's constructor.
     * @return a new RNG instance.
     * @throws MathUnsupportedOperationException if the seed type is
     * invalid.
     */
    public static UniformRandomProvider create(RandomSourceInternal source,
                                               Object seed,
                                               Object[] args) {
        // Convert seed to native type.
        final Object nativeSeed = createSeed(source, seed);

        // Build a single array with all the arguments to be passed
        // (in the right order) to the constructor.
        final List<Object> all = new ArrayList<>();
        all.add(nativeSeed);
        if (args != null) {
            all.addAll(Arrays.asList(args));
        }

        // Instantiate.
        return create(createConstructor(source), all.toArray());
    }

    /**
     * Creates a native seed from any of the supported seed types.
     *
     * @param source Source.
     * @param seed Input seed.
     * @return the native seed.
     * @throw MathUnsupportedOperationException if the {@code seed} type
     * is invalid.
     */
    private static Object createSeed(RandomSourceInternal source,
                                     Object seed) {
        Object nativeSeed = null;

        if (seed == null) {
            // Create a random seed of the appropriate native type.

            if (source.getSeed().equals(Integer.class)) {
                nativeSeed = SeedFactory.createInt();
            } else if (source.getSeed().equals(Long.class)) {
                nativeSeed = SeedFactory.createLong();
            } else if (source.getSeed().equals(int[].class)) {
                nativeSeed = SeedFactory.createIntArray(RANDOM_SEED_ARRAY_SIZE);
            } else if (source.getSeed().equals(long[].class)) {
                nativeSeed = SeedFactory.createLongArray(RANDOM_SEED_ARRAY_SIZE);
            }
        } else {
            // Convert to native type.

            if (seed instanceof Integer) {
                nativeSeed = CONV_INT.get(source.getSeed()).convert((Integer) seed);
            } else if (seed instanceof Long) {
                nativeSeed = CONV_LONG.get(source.getSeed()).convert((Long) seed);
            } else if (seed instanceof int[]) {
                nativeSeed = CONV_INT_ARRAY.get(source.getSeed()).convert((int[]) seed);
            } else if (seed instanceof long[]) {
                nativeSeed = CONV_LONG_ARRAY.get(source.getSeed()).convert((long[]) seed);
            }

            if (nativeSeed == null) {
                // Since the input seed was not null, getting here means that
                // no suitable converter is present in the maps.
                throw new MathUnsupportedOperationException();
            }

            if (!source.isNativeSeed(nativeSeed)) {
                // Conversion setup is wrong.
                throw new MathInternalError();
            }
        }

        return nativeSeed;
    }

    /**
     * Creates a constructor.
     *
     * @param source RNG specification.
     * @return a RNG constructor.
     */
    private static Constructor<?> createConstructor(RandomSourceInternal source) {
        try {
            return source.getRng().getConstructor(source.getArgs());
        } catch (NoSuchMethodException e) {
            // Info in "RandomSourceInternal" is inconsistent with the
            // constructor of the implementation.
            throw new MathInternalError(e);
        }
    }

    /**
     * Creates a RNG.
     *
     * @param rng RNG specification.
     * @param args Arguments to the implementation's constructor.
     * @return a new RNG instance.
     */
    private static UniformRandomProvider create(Constructor<?> rng,
                                                Object[] args) {
        try {
            return (UniformRandomProvider) rng.newInstance(args);
        } catch (InvocationTargetException |
                 InstantiationException |
                 IllegalArgumentException |
                 IllegalAccessException e) {
            throw new MathInternalError(e);
        }
    }

    /**
     * Identifiers of the generators.
     */
    public enum RandomSourceInternal {
        /** Source of randomness is {@link JDKRandom}. */
        JDK(JDKRandom.class,
            Long.class),
        /** Source of randomness is {@link Well512a}. */
        WELL_512_A(Well512a.class,
                   int[].class),
        /** Source of randomness is {@link Well1024a}. */
        WELL_1024_A(Well1024a.class,
                    int[].class),
        /** Source of randomness is {@link Well19937a}. */
        WELL_19937_A(Well19937a.class,
                     int[].class),
        /** Source of randomness is {@link Well19937c}. */
        WELL_19937_C(Well19937c.class,
                     int[].class),
        /** Source of randomness is {@link Well44497a}. */
        WELL_44497_A(Well44497a.class,
                     int[].class),
        /** Source of randomness is {@link Well44497b}. */
        WELL_44497_B(Well44497b.class,
                     int[].class),
        /** Source of randomness is {@link MersenneTwister}. */
        MT(MersenneTwister.class,
           int[].class),
        /** Source of randomness is {@link ISAACRandom}. */
        ISAAC(ISAACRandom.class,
              int[].class),
        /** Source of randomness is {@link SplitMix64}. */
        SPLIT_MIX_64(SplitMix64.class,
                     Long.class),
        /** Source of randomness is {@link XorShift1024Star}. */
        XOR_SHIFT_1024_S(XorShift1024Star.class,
                         long[].class),
        /** Source of randomness is {@link TwoCmres}. */
        TWO_CMRES(TwoCmres.class,
                  Integer.class),
        /**
         * Source of randomness is {@link TwoCmres} with explicit selection
         * of the two subcycle generators.
         */
        TWO_CMRES_SELECT(TwoCmres.class,
                         Integer.class,
                         Integer.TYPE,
                         Integer.TYPE),
        /** Source of randomness is {@link MersenneTwister64}. */
        MT_64(MersenneTwister64.class,
              long[].class);

        /** Source type. */
        private final Class<? extends UniformRandomProvider> rng;
        /** Data needed to build the generator. */
        private final Class<?>[] args;

        /**
         * @param rng Source type.
         * @param args Data needed to create a generator instance.
         * The first element must be the native seed type.
         */
        RandomSourceInternal(Class<? extends UniformRandomProvider> rng,
                             Class<?> ... args) {
            this.rng = rng;
            this.args = Arrays.copyOf(args, args.length);
        }

        /**
         * @return the source type.
         */
        public Class<?> getRng() {
            return rng;
        }

        /**
         * @return the seed type.
         */
        Class<?> getSeed() {
            return args[0];
        }

        /**
         * @return the data needed to build the generator.
         */
        Class<?>[] getArgs() {
            return args;
        }

        /**
         * Checks whether the type of given {@code seed} is the native type
         * of the implementation.
         *
         * @param <SEED> Seed type.
         *
         * @param seed Seed value.
         * @return {@code true} if the seed can be passed to the builder
         * for this RNG type.
         */
        public <SEED> boolean isNativeSeed(SEED seed) {
            return getSeed().equals(seed.getClass());
        }
    }
}
