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
import java.io.Serializable;
import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.rng.UniformRandomProvider;
import org.apache.commons.math4.rng.RandomSource;

/**
 * Base class with default implementation for common methods.
 */
public abstract class BaseProvider
    implements UniformRandomProvider,
               StateSettable {
    /** {@inheritDoc} */
    @Override
    public int nextInt(int n) throws IllegalArgumentException {
        if (n > 0) {
            if ((n & -n) == n) {
                return (int) ((n * (long) (nextInt() >>> 1)) >> 31);
            }
            int bits;
            int val;
            do {
                bits = nextInt() >>> 1;
                val = bits % n;
            } while (bits - val + (n - 1) < 0);
            return val;
        }

        throw new NotStrictlyPositiveException(n);
    }

    /** {@inheritDoc} */
    @Override
    public long nextLong(long n) {
        if (n > 0) {
            long bits;
            long val;
            do {
                bits = nextLong() >>> 1;
                val  = bits % n;
            } while (bits - val + (n - 1) < 0);
            return val;
        }

        throw new NotStrictlyPositiveException(n);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getClass().getName();
    }

    /** {@inheritDoc} */
    @Override
    public RandomSource.State getState() {
        return new State(getStateInternal());
    }

    /** {@inheritDoc} */
    @Override
    public void setState(RandomSource.State state) {
        // Cast will intentionally fail if the argument is not one we created.
        final State s = (State) state;
        setStateInternal(s.getState());
    }

    /**
     * Creates a snapshot of the RNG state.
     *
     * @return the internal state.
     * @throws MathUnsupportedOperationException if not implemented.
     */
    protected byte[] getStateInternal() {
        throw new MathUnsupportedOperationException();
    }

    /**
     * Resets the RNG to the given {@code state}.
     *
     * @param state State (previously obtained by a call to
     * {@link #getStateInternal()}).
     * @throws MathUnsupportedOperationException if not implemented.
     */
    protected void setStateInternal(byte[] state) {
        throw new MathUnsupportedOperationException();
    }

    /**
     * "Black-box" state.
     * Its sole purpose is to store all the data needed to recover
     * the same state in order to restart a sequence where it left
     * off.
     * External code should not to modify the data contained in
     * instances of this class.
     */
    private static class State
        implements RandomSource.State,
                   Serializable {
        /** Serializable version identifier. */
        private static final long serialVersionUID = 4720160226L;
        /** Internal state. */
        private byte[] state;

        /**
         * @param state Mapping of all the data which a subclass of
         * {@link BaseProvider} needs in order to reset its internal
         * state.
         */
        State(byte[] state) {
            this.state = Arrays.copyOf(state, state.length);
        }

        /**
         * @return the internal state.
         */
        byte[] getState() {
            return Arrays.copyOf(state, state.length);
        }
    }
}
