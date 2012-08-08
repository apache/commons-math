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
package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Base class for probability distributions on the multivariate reals.
 * Default implementations are provided for some of the methods that do
 * not vary from distribution to distribution.
 * 
 */
public abstract class AbstractMultivariateRealDistribution
    implements MultivariateRealDistribution {
    /** The number of dimensions or columns in the multivariate distribution. */
    private final int numDimensions;
    /** RNG instance used to generate samples from the distribution. */
    protected final RandomGenerator random;

    /**
     * @param rng Random number generator.
     * @param n Number of dimensions.
     */
    protected AbstractMultivariateRealDistribution(RandomGenerator rng,
                                                   int n) {
        random = rng;
        numDimensions = n;
    }

    /** {@inheritDoc} */
    public void reseedRandomGenerator(long seed) {
        random.setSeed(seed);
    }

    /**
     * 
     * @return the number of dimensions in the multivariate distribution .
     */
    public int getDimensions() {
        return numDimensions;
    }

    /** {@inheritDoc} */
    public abstract double[] sample();

    /** {@inheritDoc} */
    public double[][] sample(final int sampleSize) {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES,
                                                   sampleSize);
        }
        final double[][] out = new double[sampleSize][numDimensions];
        for (int i = 0; i < sampleSize; i++) {
            out[i] = sample();
        }
        return out;
    }

    /** {@inheritDoc} */
    public double probability(double[] x) {
        return 0;
    }

    /** {@inheritDoc} */
    public double getSupportLowerBound() {
        return Double.NEGATIVE_INFINITY;
    }

    /** {@inheritDoc} */
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /** {@inheritDoc} */
    public boolean isSupportLowerBoundInclusive() {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isSupportConnected() {
        return false;
    }
}
