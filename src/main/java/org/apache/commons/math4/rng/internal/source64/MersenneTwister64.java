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
package org.apache.commons.math4.rng.internal.source64;

import java.util.Arrays;
import org.apache.commons.math4.exception.InsufficientDataException;
import org.apache.commons.math4.rng.internal.util.NumberFactory;

/**
 * This class provides the 64-bits version of the originally 32-bits
 * {@link org.apache.commons.math4.rng.internal.source32.MersenneTwister
 * Mersenne Twister}.
 *
 * <p>
 * This class is mainly a Java port of
 * <a href="http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt64.html">
 *  the 2014/2/23 version of the generator
 * </a> written in C by Takuji Nishimura and Makoto Matsumoto.
 * </p>
 *
 * <p>
 * Here is their original copyright:
 * </p>
 *
 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>Copyright (C) 2004, Makoto Matsumoto and Takuji Nishimura,
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
public class MersenneTwister64 extends LongProvider {
    /** Size of the bytes pool. */
    private static final int NN = 312;
    /** Period second parameter. */
    private static final int MM = 156;
    /** X * MATRIX_A for X = {0, 1}. */
    private static final long[] MAG01 = { 0x0, 0xb5026f5aa96619e9L };
    /** Most significant 33 bits. */
    private static final long UM = 0xffffffff80000000L;
    /** Least significant 31 bits. */
    private static final long LM = 0x7fffffffL;
    /** Bytes pool. */
    private long[] mt = new long[NN];
    /** Current index in the bytes pool. */
    private int mti;

    /**
     * Creates a new random number generator.
     *
     * @param seed Initial seed.
     */
    public MersenneTwister64(long[] seed) {
        setSeedInternal(seed);
    }

    /** {@inheritDoc} */
    @Override
    protected byte[] getStateInternal() {
        final long[] s = Arrays.copyOf(mt, NN + 1);
        s[NN] = mti;

        return NumberFactory.makeByteArray(s);
    }

    /** {@inheritDoc} */
    @Override
    protected void setStateInternal(byte[] s) {
        if (s.length != (NN + 1) * 8) {
            throw new InsufficientDataException();
        }

        final long[] tmp = NumberFactory.makeLongArray(s);

        System.arraycopy(tmp, 0, mt, 0, NN);
        mti = (int) tmp[NN];
    }

    /**
     * Reinitializes the generator as if just built with the given seed.
     *
     * @param seed Initial seed.
     */
    private void setSeedInternal(long[] seed) {
        initState(19650218L);

        int i = 1;
        int j = 0;

        for (int k = Math.max(NN, seed.length); k != 0; k--) {
            final long mm1 = mt[i - 1];
            mt[i] = (mt[i] ^ ((mm1 ^ (mm1 >>> 62)) * 0x369dea0f31a53f85L)) + seed[j] + j; // non linear
            i++;
            j++;
            if (i >= NN) {
                mt[0] = mt[NN - 1];
                i = 1;
            }
            if (j >= seed.length) {
                j = 0;
            }
        }
        for (int k = NN - 1; k != 0; k--) {
            final long mm1 = mt[i - 1];
            mt[i] = (mt[i] ^ ((mm1 ^ (mm1 >>> 62)) * 0x27bb2ee687b0b0fdL)) - i; // non linear
            i++;
            if (i >= NN) {
                mt[0] = mt[NN - 1];
                i = 1;
            }
        }

        mt[0] = 0x8000000000000000L; // MSB is 1; assuring non-zero initial array
    }

    /**
     * Initialize the internal state of this instance.
     *
     * @param seed Seed.
     */
    private void initState(long seed) {
        mt[0] = seed;
        for (mti = 1; mti < NN; mti++) {
            final long mm1 = mt[mti - 1];
            mt[mti] = 0x5851f42d4c957f2dL * (mm1 ^ (mm1 >>> 62)) + mti;
        }
    }

    /** {@inheritDoc} */
    @Override
    public long next() {
        long x;

        if (mti >= NN) { // generate NN words at one time
            for (int i = 0; i < NN - MM; i++) {
                x = (mt[i] & UM) | (mt[i + 1] & LM);
                mt[i] = mt[i + MM] ^ (x >>> 1) ^ MAG01[(int)(x & 0x1L)];
            }
            for (int i = NN - MM; i < NN - 1; i++) {
                x = (mt[i] & UM) | (mt[i + 1] & LM);
                mt[i] = mt[ i + (MM - NN)] ^ (x >>> 1) ^ MAG01[(int)(x & 0x1L)];
            }

            x = (mt[NN - 1] & UM) | (mt[0] & LM);
            mt[NN - 1] = mt[MM - 1] ^ (x >>> 1) ^ MAG01[(int)(x & 0x1L)];

            mti = 0;
        }

        x = mt[mti++];

        x ^= (x >>> 29) & 0x5555555555555555L;
        x ^= (x << 17) & 0x71d67fffeda60000L;
        x ^= (x << 37) & 0xfff7eee000000000L;
        x ^= x >>> 43;

        return x;
    }
}
