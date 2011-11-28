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

package org.apache.commons.math.random;

import java.io.Serializable;

/**
 * <h3>ISAAC: a fast cryptographic pseudo-random number generator</h3>
 * <p/>
 * ISAAC (Indirection, Shift, Accumulate, Add, and Count) generates 32-bit random numbers.<br>
 * ISAAC has been designed to be cryptographically secure and is inspired by RC4.<br>
 * Cycles are guaranteed to be at least 2<sup>40</sup> values long, and they are 2<sup>8295</sup>
 * values long on average.<br>
 * The results are uniformly distributed, unbiased, and unpredictable unless you know the seed.<br>
 * <br>
 * This is Java implementation (with minor changes and improvements) of original algorithm by Bob Jenkins.<br>
 * <br>
 *
 * @see <a href="http://burtleburtle.net/bob/rand/isaacafa.html">ISAAC: a fast cryptographic random number generator</a>
 * @since 3.0
 */
public class ISAACRandom extends BitsStreamGenerator implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 7288197941165002400L;

    /** Log of size of rsl[] and mem[] */
    private static final int SIZE_L = 8;

    /** Size of rsl[] and mem[] */
    private static final int SIZE = 1 << SIZE_L;

    /** Half-size of rsl[] and mem[] */
    private static final int H_SIZE = SIZE >> 1;

    /** For pseudo-random lookup */
    private static final int MASK = SIZE - 1 << 2;

    /** The golden ratio */
    private static final int GLD_RATIO = 0x9e3779b9;

    /** the results given to the user */
    private int[] rsl;

    /** the internal state */
    private int[] mem;

    private transient int[] arr;

    /** count through the results in rsl[] */
    private int count;

    /** accumulator */
    private int a;

    /** the last result */
    private int b;

    /** counter, guarantees cycle is at least 2^40 */
    private int c;

    private transient int x;
    private transient int i;
    private transient int j;


    /**
     * Creates a new ISAAC random number generator.
     * <p>The instance is initialized using a combination of the
     * current time and system hash code of the instance as the seed.</p>
     */
    public ISAACRandom() {
        allocArrays();
        setSeed(System.currentTimeMillis() + System.identityHashCode(this));
    }

    /**
     * Creates a new ISAAC random number generator using a single long seed.
     *
     * @param seed the initial seed (64 bits integer)
     */
    public ISAACRandom(long seed) {
        allocArrays();
        setSeed(seed);
    }

    /**
     * Creates a new ISAAC random number generator using an int array seed.
     *
     * @param seed the initial seed (32 bits integers array), if null the
     *             seed of the generator will be related to the current time
     */
    public ISAACRandom(int[] seed) {
        allocArrays();
        setSeed(seed);
    }

    /** Allocate the pools arrays.
     */
    private void allocArrays() {
        rsl = new int[SIZE];
        mem = new int[SIZE];
        arr = new int[8];
    }

    /** {@inheritDoc} */
    @Override
    public void setSeed(int seed) {
        setSeed(new int[]{seed});
    }

    /** {@inheritDoc} */
    @Override
    public void setSeed(long seed) {
        setSeed(new int[]{(int) (seed >>> 32), (int) (seed & 0xffffffffL)});
    }

    /** {@inheritDoc} */
    @Override
    public void setSeed(int[] seed) {
        if (seed == null) {
            setSeed(System.currentTimeMillis());
            return;
        }
        int seedLen = seed.length, rslLen = rsl.length;
        System.arraycopy(seed, 0, rsl, 0, Math.min(seedLen, rslLen));
        if (seedLen < rslLen) {
            for (i = seedLen; i < rslLen; i++) {
                long k = rsl[i - seedLen];
                rsl[i] = (int) (0x6c078965L * (k ^ k >> 30) + i & 0xffffffffL);
            }
        }
        initState();
    }

    /** {@inheritDoc} */
    @Override
    protected int next(final int bits) {
        if (count < 0) {
            isaac();
            count = SIZE - 1;
        }
        return rsl[count--] >>> 32 - bits;
    }

    /** Generate 256 results */
    private void isaac() {
        i = 0;
        j = H_SIZE;
        b += ++c;
        while (i < H_SIZE) {
            isaac2();
        }
        j = 0;
        while (j < H_SIZE) {
            isaac2();
        }
    }

    /** Intermediate internal loop.
     */
    private void isaac2() {
        x = mem[i];
        a ^= a << 13;
        a += mem[j++];
        isaac3();
        x = mem[i];
        a ^= a >>> 6;
        a += mem[j++];
        isaac3();
        x = mem[i];
        a ^= a << 2;
        a += mem[j++];
        isaac3();
        x = mem[i];
        a ^= a >>> 16;
        a += mem[j++];
        isaac3();
    }

    /** Lowest level internal loop.
     */
    private void isaac3() {
        mem[i] = mem[(x & MASK) >> 2] + a + b;
        b = mem[(mem[i] >> SIZE_L & MASK) >> 2] + x;
        rsl[i++] = b;
    }

    /** Initialize, or reinitialize, this instance of rand.
     */
    private void initState() {
        a = b = c = 0;
        for (i = 0; i < arr.length; i++) {
            arr[i] = GLD_RATIO;
        }
        for (i = 0; i < 4; i++) {
            shuffle();
        }
        // fill in mem[] with messy stuff
        for (i = 0; i < SIZE; i += 8) {
            arr[0] += rsl[i];
            arr[1] += rsl[i + 1];
            arr[2] += rsl[i + 2];
            arr[3] += rsl[i + 3];
            arr[4] += rsl[i + 4];
            arr[5] += rsl[i + 5];
            arr[6] += rsl[i + 6];
            arr[7] += rsl[i + 7];
            shuffle();
            setState();
        }
        // second pass makes all of seed affect all of mem
        for (i = 0; i < SIZE; i += 8) {
            arr[0] += mem[i];
            arr[1] += mem[i + 1];
            arr[2] += mem[i + 2];
            arr[3] += mem[i + 3];
            arr[4] += mem[i + 4];
            arr[5] += mem[i + 5];
            arr[6] += mem[i + 6];
            arr[7] += mem[i + 7];
            shuffle();
            setState();
        }
        isaac();
        count = SIZE - 1;
    }

    /** Shuffle array.
     */
    private void shuffle() {
        arr[0] ^= arr[1] << 11;
        arr[3] += arr[0];
        arr[1] += arr[2];
        arr[1] ^= arr[2] >>> 2;
        arr[4] += arr[1];
        arr[2] += arr[3];
        arr[2] ^= arr[3] << 8;
        arr[5] += arr[2];
        arr[3] += arr[4];
        arr[3] ^= arr[4] >>> 16;
        arr[6] += arr[3];
        arr[4] += arr[5];
        arr[4] ^= arr[5] << 10;
        arr[7] += arr[4];
        arr[5] += arr[6];
        arr[5] ^= arr[6] >>> 4;
        arr[0] += arr[5];
        arr[6] += arr[7];
        arr[6] ^= arr[7] << 8;
        arr[1] += arr[6];
        arr[7] += arr[0];
        arr[7] ^= arr[0] >>> 9;
        arr[2] += arr[7];
        arr[0] += arr[1];
    }

    /** Set the state by copying the internal arrays.
     */
    private void setState() {
        mem[i] = arr[0];
        mem[i + 1] = arr[1];
        mem[i + 2] = arr[2];
        mem[i + 3] = arr[3];
        mem[i + 4] = arr[4];
        mem[i + 5] = arr[5];
        mem[i + 6] = arr[6];
        mem[i + 7] = arr[7];
    }

}