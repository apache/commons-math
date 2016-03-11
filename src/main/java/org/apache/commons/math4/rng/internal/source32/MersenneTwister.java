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
 * This class implements a powerful pseudo-random number generator
 * developed by Makoto Matsumoto and Takuji Nishimura during
 * 1996-1997.
 *
 * <p>
 * This generator features an extremely long period
 * (2<sup>19937</sup>-1) and 623-dimensional equidistribution up to
 * 32 bits accuracy.  The home page for this generator is located at
 * <a href="http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html">
 * http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html</a>.
 * </p>
 *
 * <p>
 * This generator is described in a paper by Makoto Matsumoto and
 * Takuji Nishimura in 1998:
 * <a href="http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/ARTICLES/mt.pdf">
 * Mersenne Twister: A 623-Dimensionally Equidistributed Uniform Pseudo-Random
 * Number Generator</a>,
 * ACM Transactions on Modeling and Computer Simulation, Vol. 8, No. 1,
 * January 1998, pp 3--30
 * </p>
 *
 * <p>
 * This class is mainly a Java port of the
 * <a href="http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/MT2002/emt19937ar.html">
 * 2002-01-26 version of the generator</a> written in C by Makoto Matsumoto
 * and Takuji Nishimura. Here is their original copyright:
 * </p>
 *
 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>Copyright (C) 1997 - 2002, Makoto Matsumoto and Takuji Nishimura,
 *     All rights reserved.</td></tr>
 *
 * <tr><td>Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <ol>
 *   <li>Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.</li>
 *   <li>Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.</li>
 *   <li>The names of its contributors may not be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.</li>
 * </ol></td></tr>
 *
 * <tr><td><strong>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.</strong></td></tr>
 * </table>
 *
 * @since 4.0
 */
public class MersenneTwister extends IntProvider {
    /** Mask 32 most significant bits. */
    private static final long INT_MASK_LONG = 0xffffffffL;
    /** Most significant w-r bits. */
    private static final long UPPER_MASK_LONG = 0x80000000L;
    /** Least significant r bits */
    private static final long LOWER_MASK_LONG = 0x7fffffffL;
    /** Most significant w-r bits. */
    private static final int UPPER_MASK = 0x80000000;
    /** Least significant r bits */
    private static final int LOWER_MASK = 0x7fffffff;
    /** Size of the bytes pool. */
    private static final int N = 624;
    /** Period second parameter. */
    private static final int M = 397;
    /** X * MATRIX_A for X = {0, 1}. */
    private static final int[] MAG01 = { 0x0, 0x9908b0df };
    /** Bytes pool. */
    private int[] mt = new int[N];
    /** Current index in the bytes pool. */
    private int mti;

    /**
     * Creates a new random number generator.
     *
     * @param seed Initial seed.
     */
    public MersenneTwister(int[] seed) {
        setSeedInternal(seed);
    }

    /** {@inheritDoc} */
    @Override
    protected byte[] getStateInternal() {
        final int[] s = Arrays.copyOf(mt, N + 1);
        s[N] = mti;

        return NumberFactory.makeByteArray(s);
    }

    /** {@inheritDoc} */
    @Override
    protected void setStateInternal(byte[] s) {
        if (s.length != (N + 1) * 4) {
            throw new InsufficientDataException();
        }

        final int[] tmp = NumberFactory.makeIntArray(s);

        System.arraycopy(tmp, 0, mt, 0, N);
        mti = tmp[N];
    }

    /**
     * Reinitializes the generator as if just built with the given seed.
     *
     * @param seed Initial seed.
     */
    private void setSeedInternal(int[] seed) {
        initState(19650218);
        int i = 1;
        int j = 0;

        for (int k = Math.max(N, seed.length); k != 0; k--) {
            final long l0 = (mt[i] & LOWER_MASK_LONG)   | ((mt[i]   < 0) ? UPPER_MASK_LONG : 0);
            final long l1 = (mt[i-1] & LOWER_MASK_LONG) | ((mt[i-1] < 0) ? UPPER_MASK_LONG : 0);
            final long l  = (l0 ^ ((l1 ^ (l1 >> 30)) * 1664525l)) + seed[j] + j; // non linear
            mt[i]   = (int) (l & INT_MASK_LONG);
            i++; j++;
            if (i >= N) {
                mt[0] = mt[N - 1];
                i = 1;
            }
            if (j >= seed.length) {
                j = 0;
            }
        }

        for (int k = N - 1; k != 0; k--) {
            final long l0 = (mt[i] & LOWER_MASK_LONG)   | ((mt[i]   < 0) ? UPPER_MASK_LONG : 0);
            final long l1 = (mt[i-1] & LOWER_MASK_LONG) | ((mt[i-1] < 0) ? UPPER_MASK_LONG : 0);
            final long l  = (l0 ^ ((l1 ^ (l1 >> 30)) * 1566083941l)) - i; // non linear
            mt[i]   = (int) (l & INT_MASK_LONG);
            i++;
            if (i >= N) {
                mt[0] = mt[N - 1];
                i = 1;
            }
        }

        mt[0] = UPPER_MASK; // MSB is 1; assuring non-zero initial array
    }

    /**
     * Initialize the internal state of this instance.
     *
     * @param seed Seed.
     */
    private void initState(int seed) {
        long longMT = seed & INT_MASK_LONG;
        mt[0]= (int) longMT;
        for (mti = 1; mti < N; ++mti) {
            longMT = (1812433253L * (longMT ^ (longMT >> 30)) + mti) & INT_MASK_LONG;
            mt[mti]= (int) longMT;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int next() {
        int y;

        if (mti >= N) { // Generate N words at one time.
            int mtNext = mt[0];
            for (int k = 0; k < N - M; ++k) {
                int mtCurr = mtNext;
                mtNext = mt[k + 1];
                y = (mtCurr & UPPER_MASK) | (mtNext & LOWER_MASK);
                mt[k] = mt[k + M] ^ (y >>> 1) ^ MAG01[y & 1];
            }
            for (int k = N - M; k < N - 1; ++k) {
                int mtCurr = mtNext;
                mtNext = mt[k + 1];
                y = (mtCurr & UPPER_MASK) | (mtNext & LOWER_MASK);
                mt[k] = mt[k + (M - N)] ^ (y >>> 1) ^ MAG01[y & 1];
            }
            y = (mtNext & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ MAG01[y & 1];

            mti = 0;
        }

        y = mt[mti++];

        // Tempering.
        y ^=  y >>> 11;
        y ^= (y << 7) & 0x9d2c5680;
        y ^= (y << 15) & 0xefc60000;
        y ^=  y >>> 18;

        return y;
    }
}
