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
import org.apache.commons.math4.exception.NotStrictlyPositiveException;

/**
 * A {@link RandomGenerator} adapter that delegates the random number
 * generation to the standard {@link java.util.Random} class.
 *
 * @since 1.1
 */
public class JDKRandomGenerator
    implements RandomGenerator {
    /** Serializable version identifier. */
    private static final long serialVersionUID = 20151227L;
    /** JDK's RNG. */
    private final Random delegate;

    /**
     * Creates an instance with an arbitrary seed.
     */
    public JDKRandomGenerator() {
        delegate = new Random();
    }

    /**
     * Creates an instance with the given seed.
     *
     * @param seed Initial seed.
     * @since 3.6
     */
    public JDKRandomGenerator(long seed) {
        delegate = new Random(seed);
    }

    /** {@inheritDoc} */
    @Override
    public void setSeed(int seed) {
        delegate.setSeed((long) seed);
    }

    /** {@inheritDoc} */
    @Override
    public void setSeed(long seed) {
        delegate.setSeed( seed);
    }

    /** {@inheritDoc} */
    @Override
    public void setSeed(int[] seed) {
        delegate.setSeed(RandomGeneratorFactory.convertToLong(seed));
    }

    /** {@inheritDoc} */
    @Override
    public void nextBytes(byte[] bytes) {
        delegate.nextBytes(bytes);
    }

    /** {@inheritDoc} */
    @Override
    public int nextInt() {
        return delegate.nextInt();
    }

    /** {@inheritDoc} */
    @Override
    public long nextLong() {
        return delegate.nextLong();
    }

    /** {@inheritDoc} */
    @Override
    public boolean nextBoolean() {
        return delegate.nextBoolean();
    }

    /** {@inheritDoc} */
    @Override
    public float nextFloat() {
        return delegate.nextFloat();
    }

    /** {@inheritDoc} */
    @Override
    public double nextDouble() {
        return delegate.nextDouble();
    }

    /** {@inheritDoc} */
    @Override
    public double nextGaussian() {
        return delegate.nextGaussian();
    }

    /** {@inheritDoc} */
    @Override
    public int nextInt(int n) {
        try {
            return delegate.nextInt(n);
        } catch (IllegalArgumentException e) {
            throw new NotStrictlyPositiveException(n);
        }
    }
}
