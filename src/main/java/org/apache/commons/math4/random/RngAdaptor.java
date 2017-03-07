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

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.rng.RestorableUniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.core.RandomProviderDefaultState;

/**
 * Adaptor that delegates to a
 * {@link org.apache.commons.rng.UniformRandomProvider} instance.
 * <p>
 * It is provided for users who wish to test the new RNG implementations
 * the <i>current</i> generators (up to version 3.6 of Commons Math) in
 * codes that require the {@code RandomGenerator} interface.
 * </p>
 * <p>
 * Applications should upgrade to use the new generators ASAP.
 * If problems are found that jeopardize the upgrade, please report them
 * on the project's
 * <a href="https://issues.apache.org/jira/browse/MATH">
 *  issue tracking system</a>.
 * </p>
 *
 * <p>
 * <b>Notes:</b>
 * <ul>
 *  <li>
 *   The
 *   {@link RandomGenerator#setSeed(int) setSeed(int)},
 *   {@link RandomGenerator#setSeed(int[]) setSeed(int[])} and
 *   {@link RandomGenerator#setSeed(long) setSeed(long)}
 *   methods of the {@link RandomGenerator} are not part of the
 *   {@link org.apache.commons.rng.UniformRandomProvider new API}.
 *  </li>
 *  <li>
 *   The new RNG implementations are not {@code Serializable}.
 *   Use {@link RestorableUniformRandomProvider#saveState()}
 *   instead.
 *  </li>
 *  <li>
 *   {@link RandomGenerator#nextGaussian() nextGaussian()} is not
 *   part of the {@link org.apache.commons.rng.UniformRandomProvider
 *   new API} as it defines a "post-processing" of the output of a
 *   <i>uniform</i> RNG in order to follow a different distribution.
 *  </li>
 * </p>
 *
 * @since 4.0
 *
 * @deprecated As of 4.0. This class is made available for testing
 * the {@link RandomSource new RNG implementations} in existing
 * applications.
 * It will be removed in the next major release.
 */
@Deprecated
public final class RngAdaptor
    implements RandomGenerator,
               Serializable {
    /** Serializable version identifier. */
    private static final long serialVersionUID = 12345L;
    /** Source. */
    private final RandomSource source;
    /** Delegate. */
    private transient RestorableUniformRandomProvider delegate;
    /** Next gaussian. */
    private double nextGaussian = Double.NaN;

    /**
     * Creates a new instance.
     *
     * @param source Source of randomness.
     */
    public RngAdaptor(RandomSource source) {
        this(source, null);
    }

    /**
     * Creates a new instance.
     *
     * @param source Source of randomness.
     * @param seed Seed.  Can be {@code null}.
     */
    public RngAdaptor(RandomSource source,
                      Object seed) {
        this.source = source;
        delegate = RandomSource.create(source, seed);
    }

    /** {@inheritDoc} */
    @Override
    public void setSeed(int seed) {
        delegate = RandomSource.create(source, seed);
        clear();
    }

    /** {@inheritDoc} */
    @Override
    public void setSeed(int[] seed) {
        delegate = RandomSource.create(source, seed);
        clear();
    }

    /** {@inheritDoc} */
    @Override
    public void setSeed(long seed) {
        delegate = RandomSource.create(source, seed);
        clear();
    }

    /** {@inheritDoc} */
    @Override
    public boolean nextBoolean() {
        return delegate.nextBoolean();
    }

    /** {@inheritDoc} */
    @Override
    public void nextBytes(byte[] bytes) {
        delegate.nextBytes(bytes);
    }

    /** {@inheritDoc} */
    @Override
    public double nextDouble() {
        return delegate.nextDouble();
    }

    /** {@inheritDoc} */
    @Override
    public float nextFloat() {
        return delegate.nextFloat();
    }

    /** {@inheritDoc} */
    @Override
    public double nextGaussian() {
        final double random;
        if (Double.isNaN(nextGaussian)) {
            // generate a new pair of gaussian numbers
            final double x = nextDouble();
            final double y = nextDouble();
            final double alpha = 2 * FastMath.PI * x;
            final double r = FastMath.sqrt(-2 * FastMath.log(y));
            random = r * FastMath.cos(alpha);
            nextGaussian = r * FastMath.sin(alpha);
        } else {
            // use the second element of the pair already generated
            random = nextGaussian;
            nextGaussian = Double.NaN;
        }

        return random;
    }

    /** {@inheritDoc} */
    @Override
    public int nextInt() {
        return delegate.nextInt();
    }

    /** {@inheritDoc} */
    @Override
    public int nextInt(int n) {
        return delegate.nextInt(n);
    }

    /** {@inheritDoc} */
    @Override
    public long nextLong() {
        return delegate.nextLong();
    }

    /**
     * Clears the cache used by the default implementation of
     * {@link #nextGaussian}.
     */
    private void clear() {
        nextGaussian = Double.NaN;
    }

    /**
     * @param out Output stream.
     * @throws IOException if an error occurs.
     */
    private void writeObject(ObjectOutputStream out)
        throws IOException {
        // Write non-transient fields.
        out.defaultWriteObject();

        // Save current state.
        out.writeObject(((RandomProviderDefaultState) delegate.saveState()).getState());
   }

    /**
     * @param in Input stream.
     * @throws IOException if an error occurs.
     * @throws ClassNotFoundException if an error occurs.
     */
    private void readObject(ObjectInputStream in)
        throws IOException,
               ClassNotFoundException {
        // Read non-transient fields.
        in.defaultReadObject();

        // Recreate the "delegate" from serialized info.
        delegate = RandomSource.create(source);
        // And restore its state.
        final RandomProviderDefaultState state = new RandomProviderDefaultState((byte[]) in.readObject());
        delegate.restoreState(state);
    }
}
