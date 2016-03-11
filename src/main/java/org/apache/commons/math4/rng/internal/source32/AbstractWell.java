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
package org.apache.commons.math4.rng.internal.source32;

import java.util.Arrays;
import org.apache.commons.math4.exception.InsufficientDataException;
import org.apache.commons.math4.rng.internal.util.NumberFactory;

/**
 * This abstract class implements the WELL class of pseudo-random number
 * generator from Fran&ccedil;ois Panneton, Pierre L'Ecuyer and Makoto
 * Matsumoto.
 * <p>
 * This generator is described in a paper by Fran&ccedil;ois Panneton,
 * Pierre L'Ecuyer and Makoto Matsumoto
 * <a href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/wellrng.pdf">
 * Improved Long-Period Generators Based on Linear Recurrences Modulo 2</a>
 * ACM Transactions on Mathematical Software, 32, 1 (2006).
 * The errata for the paper are in
 * <a href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/wellrng-errata.txt">wellrng-errata.txt</a>.
 * </p>
 *
 * @see <a href="http://www.iro.umontreal.ca/~panneton/WELLRNG.html">WELL Random number generator</a>
 *
 * @since 4.0
 */
public abstract class AbstractWell extends IntProvider {
    /** Current index in the bytes pool. */
    protected int index;
    /** Bytes pool. */
    protected final int[] v;

    /**
     * Creates a new random number generator using an int array seed.
     *
     * @param k Number of bits in the pool (not necessarily a multiple of 32).
     * @param seed Initial seed.
     */
    protected AbstractWell(final int k,
                           final int[] seed) {
        final int r = calculateBlockCount(k);
        v = new int[r];
        index = 0;

        // Initialize the pool content.
        setSeedInternal(seed);
    }

    /** {@inheritDoc} */
    @Override
    protected byte[] getStateInternal() {
        final int[] s = Arrays.copyOf(v, v.length + 1);
        s[v.length] = index;

        return NumberFactory.makeByteArray(s);
    }

    /** {@inheritDoc} */
    @Override
    protected void setStateInternal(byte[] s) {
        if (s.length != (v.length + 1) * 4) {
            throw new InsufficientDataException();
        }

        final int[] tmp = NumberFactory.makeIntArray(s);

        System.arraycopy(tmp, 0, v, 0, v.length);
        index = tmp[v.length];
    }

    /**
     * Reinitialize the generator as if just built with the given int array seed.
     *
     * <p>The state of the generator is exactly the same as a new generator built
     * with the same seed.</p>
     *
     * @param seed Seed. Cannot be null.
     */
    private void setSeedInternal(final int[] seed) {
        System.arraycopy(seed, 0, v, 0, Math.min(seed.length, v.length));

        if (seed.length < v.length) {
            for (int i = seed.length; i < v.length; ++i) {
                final long current = v[i - seed.length];
                v[i] = (int) ((1812433253L * (current ^ (current >> 30)) + i) & 0xffffffffL);
            }
        }

        index = 0;
    }

    /**
     * Calculate the number of 32-bits blocks.
     *
     * @param k Number of bits in the pool (not necessarily a multiple of 32).
     * @return the number of 32-bits blocks.
     */
    private static int calculateBlockCount(final int k) {
        // the bits pool contains k bits, k = r w - p where r is the number
        // of w bits blocks, w is the block size (always 32 in the original paper)
        // and p is the number of unused bits in the last block
        final int w = 32;
        final int r = (k + w - 1) / w;
        return r;
    }

    /**
     * Inner class used to store the indirection index table which is fixed for a given
     * type of WELL class of pseudo-random number generator.
     */
    protected static final class IndexTable {
        /** Index indirection table giving for each index its predecessor taking table size into account. */
        private final int[] iRm1;
        /** Index indirection table giving for each index its second predecessor taking table size into account. */
        private final int[] iRm2;
        /** Index indirection table giving for each index the value index + m1 taking table size into account. */
        private final int[] i1;
        /** Index indirection table giving for each index the value index + m2 taking table size into account. */
        private final int[] i2;
        /** Index indirection table giving for each index the value index + m3 taking table size into account. */
        private final int[] i3;

        /** Creates a new pre-calculated indirection index table.
         * @param k number of bits in the pool (not necessarily a multiple of 32)
         * @param m1 first parameter of the algorithm
         * @param m2 second parameter of the algorithm
         * @param m3 third parameter of the algorithm
         */
        public IndexTable(final int k, final int m1, final int m2, final int m3) {

            final int r = calculateBlockCount(k);

            // precompute indirection index tables. These tables are used for optimizing access
            // they allow saving computations like "(j + r - 2) % r" with costly modulo operations
            iRm1 = new int[r];
            iRm2 = new int[r];
            i1 = new int[r];
            i2 = new int[r];
            i3 = new int[r];
            for (int j = 0; j < r; ++j) {
                iRm1[j] = (j + r - 1) % r;
                iRm2[j] = (j + r - 2) % r;
                i1[j] = (j + m1) % r;
                i2[j] = (j + m2) % r;
                i3[j] = (j + m3) % r;
            }
        }

        /**
         * Returns the predecessor of the given index modulo the table size.
         * @param index the index to look at
         * @return (index - 1) % table size
         */
        public int getIndexPred(final int index) {
            return iRm1[index];
        }

        /**
         * Returns the second predecessor of the given index modulo the table size.
         * @param index the index to look at
         * @return (index - 2) % table size
         */
        public int getIndexPred2(final int index) {
            return iRm2[index];
        }

        /**
         * Returns index + M1 modulo the table size.
         * @param index the index to look at
         * @return (index + M1) % table size
         */
        public int getIndexM1(final int index) {
            return i1[index];
        }

        /**
         * Returns index + M2 modulo the table size.
         * @param index the index to look at
         * @return (index + M2) % table size
         */
        public int getIndexM2(final int index) {
            return i2[index];
        }

        /**
         * Returns index + M3 modulo the table size.
         * @param index the index to look at
         * @return (index + M3) % table size
         */
        public int getIndexM3(final int index) {
            return i3[index];
        }
    }
}
