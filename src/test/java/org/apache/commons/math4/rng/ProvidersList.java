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
package org.apache.commons.math4.rng;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The purpose of this class is to provide the list of all generators
 * implemented in the library.
 * The list must be updated with each new RNG implementation.
 *
 * @see #list()
 * @see #list32()
 * @see #list64()
 */
public class ProvidersList {
    /** List of all RNGs implemented in the library. */
    private static final List<Data[]> LIST = new ArrayList<>();
    /** List of 32-bits based RNGs. */
    private static final List<Data[]> LIST32 = new ArrayList<>();
    /** List of 64-bits based RNGs. */
    private static final List<Data[]> LIST64 = new ArrayList<>();

    static {
        try {
            // "int"-based RNGs.
            add(LIST32, RandomSource.JDK, -122333444455555L);
            add(LIST32, RandomSource.MT, new int[] { -123, -234, -345 });
            add(LIST32, RandomSource.WELL_512_A, new int[] { -23, -34, -45 });
            add(LIST32, RandomSource.WELL_1024_A, new int[] { -1234, -2345, -3456 });
            add(LIST32, RandomSource.WELL_19937_A, new int[] { -2123, -3234, -4345 });
            add(LIST32, RandomSource.WELL_19937_C, new int[] { -123, -234, -345, -456 });
            add(LIST32, RandomSource.WELL_44497_A, new int[] { -12345, -23456, -34567 });
            add(LIST32, RandomSource.WELL_44497_B, new int[] { 123, 234, 345 });
            add(LIST32, RandomSource.ISAAC, new int[] { 123, -234, 345, -456 });
            // ... add more here.

            // "long"-based RNGs.
            add(LIST64, RandomSource.SPLIT_MIX_64, -988777666655555L);
            add(LIST64, RandomSource.XOR_SHIFT_1024_S, new long[] { 123456L, 234567L, -345678L });
            add(LIST64, RandomSource.TWO_CMRES, 55443322);
            add(LIST64, RandomSource.TWO_CMRES_SELECT, -987654321, 5, 8);
            add(LIST64, RandomSource.MT_64, new long[] { 1234567L, 2345678L, -3456789L });
            // ... add more here.

            // Do not modify the remaining statements.
            // Complete list.
            LIST.addAll(LIST32);
            LIST.addAll(LIST64);
        } catch (Exception e) {
            System.err.println("Unexpected exception while creating the list of generators: " + e);
            e.printStackTrace(System.err);
            throw e;
        }
    }

    /**
     * Class contains only static methods.
     */
    private ProvidersList() {}

    /**
     * Helper to statisfy Junit requirement that each parameter set contains
     * the same number of objects.
     */
    private static void add(List<Data[]> list,
                            RandomSource source,
                            Object ... data) {
        final RandomSource rng = source;
        final Object seed = data.length > 0 ? data[0] : null;
        final Object[] args = data.length > 1 ? Arrays.copyOfRange(data, 1, data.length) : null;

        list.add(new Data[] { new Data(rng, seed, args) });
    }

    /**
     * Subclasses that are "parametric" tests can forward the call to
     * the "@Parameters"-annotated method to this method.
     *
     * @return the list of all generators.
     */
    public static Iterable<Data[]> list() {
        return Collections.unmodifiableList(LIST);
    }

    /**
     * Subclasses that are "parametric" tests can forward the call to
     * the "@Parameters"-annotated method to this method.
     *
     * @return the list of 32-bits based generators.
     */
    public static Iterable<Data[]> list32() {
        return Collections.unmodifiableList(LIST32);
    }

    /**
     * Subclasses that are "parametric" tests can forward the call to
     * the "@Parameters"-annotated method to this method.
     *
     * @return the list of 32-bits based generators.
     */
    public static Iterable<Data[]> list64() {
        return Collections.unmodifiableList(LIST64);
    }

    /**
     * Helper.
     * Better not to mix Junit assumptions of the usage of "Object[]".
     */
    public static class Data {
        private final RandomSource source;
        private final Object seed;
        private final Object[] args;

        public Data(RandomSource source,
                    Object seed,
                    Object[] args) {
            this.source = source;
            this.seed = seed;
            this.args = args;
        }

        public RandomSource getSource() {
            return source;
        }

        public Object getSeed() {
            return seed;
        }

        public Object[] getArgs() {
            return args == null ? null : Arrays.copyOf(args, args.length);
        }

        @Override
        public String toString() {
            return source.toString() + " seed=" + seed + " args=" + Arrays.toString(args);
        }
    }
}
