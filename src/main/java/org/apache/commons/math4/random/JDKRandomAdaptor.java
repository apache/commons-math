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
import java.io.ObjectOutputStream;
import java.io.IOException;
import org.apache.commons.math4.exception.MathInternalError;
import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.distribution.RealDistribution;
import org.apache.commons.math4.distribution.NormalDistribution;

/**
 * Extension of {@link java.util.Random} that delegates the number
 * generation to a {@link UniformRandomProvider}.
 *
 * <p>
 * This class allows usage of JDK utilities that take an instance
 * of type {@code Random} as argument.
 * <br>
 * Other than for this specific purpose, usage of this class
 * is best avoided; indeed, because of the following limitations:
 * <ul>
 *  <li>
 *   {@code MathUnsupportedOperationException} will be raised if
 *   serialization is attempted.
 *  </li>
 *  <li>
 *   Reseeding is not supported.
 *  </li>
 * </ul>
 * an instance of this class cannot be a substitute for an instance
 * of the parent class if those functionalities are required.
 * </p>
 *
 * @since 4.0
 */
public final class JDKRandomAdaptor extends Random {
    /** Serial version identifier. */
    private static final long serialVersionUID = 666L;
    /** Delegate. */
    private final transient UniformRandomProvider rng;
    /** Cf. "nextGaussian()" method. */
    private final transient RealDistribution.Sampler gauss;

    /**
     * Creates an adaptor.
     *
     * @param rng Generator.
     */
    public JDKRandomAdaptor(UniformRandomProvider rng) {
        super(0L);

        this.rng = rng;
        gauss = new NormalDistribution().createSampler(rng);
    }

    /** {@inheritDoc} */
    @Override
    public boolean nextBoolean() {
        return rng.nextBoolean();
    }

    /** {@inheritDoc} */
    @Override
    public void nextBytes(byte[] bytes) {
        rng.nextBytes(bytes);
    }

    /** {@inheritDoc} */
    @Override
    public double nextDouble() {
        return rng.nextDouble();
    }

    /** {@inheritDoc} */
    @Override
    public float nextFloat() {
        return rng.nextFloat();
    }

    /** {@inheritDoc} */
    @Override
    public double nextGaussian() {
        return gauss.sample();
    }

    /** {@inheritDoc} */
    @Override
    public int nextInt() {
        return rng.nextInt();
    }

    /** {@inheritDoc} */
    @Override
    public int nextInt(int n) {
        return rng.nextInt(n);
    }

    /** {@inheritDoc} */
    @Override
    public long nextLong() {
        return rng.nextLong();
    }

    /** {@inheritDoc} */
    @Override
    protected int next(int bits) {
        // Should never happen: it means that some methods were not overridden.
        throw new MathInternalError();
    }

    /**
     * Seeding is not supported.
     *
     * @param seed Ignored.
     */
    @Override
    public void setSeed(long seed) {
        // Cannot throw because the constructor of "Random" calls it.
        // throw new MathUnsupportedOperationException();
    }

    /**
     * @param out Ignored.
     * @throws IOException Ignored.
     * @throws MathUnsupportedOperationException if called.
     */
    private void writeObject(ObjectOutputStream out)
        throws IOException {
        throw new MathUnsupportedOperationException();
    }
}
