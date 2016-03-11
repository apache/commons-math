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

import org.apache.commons.math4.exception.InsufficientDataException;
import org.apache.commons.math4.rng.internal.util.NumberFactory;

/**
 * A fast RNG, with 64 bits of state, that can be used to initialize the
 * state of other generators.
 *
 * @see <a href="http://xorshift.di.unimi.it/splitmix64.c">
 * Original source code</a>
 *
 * @since 4.0
 */
public class SplitMix64 extends LongProvider {
    /** State. */
    private long state;

    /**
     * Creates a new instance.
     *
     * @param seed Initial seed.
     */
    public SplitMix64(Long seed) {
        setSeedInternal(seed);
    }

    /**
     * Seeds the RNG.
     *
     * @param seed Seed.
     */
    private void setSeedInternal(Long seed) {
        state = seed;
    }

    /** {@inheritDoc} */
    @Override
    public long next() {
        long z = state += 0x9e3779b97f4a7c15L;
        z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
        return z ^ (z >>> 31);
    }

    /** {@inheritDoc} */
    @Override
    protected byte[] getStateInternal() {
        return NumberFactory.makeByteArray(state);
    }

    /** {@inheritDoc} */
    @Override
    protected void setStateInternal(byte[] s) {
        if (s.length != 8) {
            throw new InsufficientDataException();
        }

        state = NumberFactory.makeLong(s);
    }
}
