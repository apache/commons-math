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

package org.apache.commons.math4.legacy.random;

import java.util.function.Supplier;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;
import org.apache.commons.rng.sampling.distribution.ZigguratNormalizedGaussianSampler;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RectangularCholeskyDecomposition;

/**
 * Generates vectors with with correlated components.
 *
 * <p>Random vectors with correlated components are built by combining
 * the uncorrelated components of another random vector in such a way
 * that the resulting correlations are the ones specified by a positive
 * definite covariance matrix.</p>
 *
 * <p>The main use of correlated random vector generation is for Monte-Carlo
 * simulation of physical problems with several variables (for example to
 * generate error vectors to be added to a nominal vector). A particularly
 * common case is when the generated vector should be drawn from a
 * <a href="http://en.wikipedia.org/wiki/Multivariate_normal_distribution">
 * Multivariate Normal Distribution</a>, usually using Cholesky decomposition.
 * Other distributions are possible as long as the underlying sampler provides
 * normalized values (unit standard deviation).</p>
 *
 * <p>Sometimes, the covariance matrix for a given simulation is not
 * strictly positive definite. This means that the correlations are
 * not all independent from each other. In this case, however, the non
 * strictly positive elements found during the Cholesky decomposition
 * of the covariance matrix should not be negative either, they
 * should be null. Another non-conventional extension handling this case
 * is used here. Rather than computing <code>C = U<sup>T</sup> U</code>
 * where <code>C</code> is the covariance matrix and <code>U</code>
 * is an upper-triangular matrix, we compute <code>C = B B<sup>T</sup></code>
 * where <code>B</code> is a rectangular matrix having more rows than
 * columns. The number of columns of <code>B</code> is the rank of the
 * covariance matrix, and it is the dimension of the uncorrelated
 * random vector that is needed to compute the component of the
 * correlated vector. This class handles this situation automatically.</p>
 */
public class CorrelatedVectorFactory {
    /** Square root of three. */
    private static final double SQRT3 = JdkMath.sqrt(3);
    /** Mean vector. */
    private final double[] mean;
    /** Root of the covariance matrix. */
    private final RealMatrix root;
    /** Size of uncorrelated vector. */
    private final int lengthUncorrelated;
    /** Size of correlated vector. */
    private final int lengthCorrelated;

    /**
     * Correlated vector factory.
     *
     * @param mean Expected mean values of the components.
     * @param covariance Covariance matrix.
     * @param small Diagonal elements threshold under which columns are
     * considered to be dependent on previous ones and are discarded.
     * @throws org.apache.commons.math4.legacy.linear.NonPositiveDefiniteMatrixException
     * if the covariance matrix is not strictly positive definite.
     * @throws DimensionMismatchException if the mean and covariance
     * arrays dimensions do not match.
     */
    public CorrelatedVectorFactory(double[] mean,
                                   RealMatrix covariance,
                                   double small) {
        lengthCorrelated = covariance.getRowDimension();
        if (mean.length != lengthCorrelated) {
            throw new DimensionMismatchException(mean.length, lengthCorrelated);
        }
        this.mean = mean.clone();

        final RectangularCholeskyDecomposition decomposition
            = new RectangularCholeskyDecomposition(covariance, small);
        root = decomposition.getRootMatrix();

        lengthUncorrelated = decomposition.getRank();
    }

    /**
     * Null mean correlated vector factory.
     *
     * @param covariance Covariance matrix.
     * @param small Diagonal elements threshold under which columns are
     * considered to be dependent on previous ones and are discarded.
     * @throws org.apache.commons.math4.legacy.linear.NonPositiveDefiniteMatrixException
     * if the covariance matrix is not strictly positive definite.
     */
    public CorrelatedVectorFactory(RealMatrix covariance,
                                   double small) {
        this(new double[covariance.getRowDimension()],
             covariance,
             small);
    }

    /**
     * @param rng RNG.
     * @return a generator of vectors with correlated components sampled
     * from a uniform distribution.
     */
    public Supplier<double[]> uniform(UniformRandomProvider rng) {
        return with(new ContinuousUniformSampler(rng, -SQRT3, SQRT3));
    }

    /**
     * @param rng RNG.
     * @return a generator of vectors with correlated components sampled
     * from a normal distribution.
     */
    public Supplier<double[]> gaussian(UniformRandomProvider rng) {
        return with(new ZigguratNormalizedGaussianSampler(rng));
    }

    /**
     * @param sampler Generator of samples from a normalized distribution.
     * @return a generator of vectors with correlated components.
     */
    private Supplier<double[]> with(final ContinuousSampler sampler) {
        return new Supplier<double[]>() {
            @Override
            public double[] get() {
                // Uncorrelated vector.
                final double[] uncorrelated = new double[lengthUncorrelated];
                for (int i = 0; i < lengthUncorrelated; i++) {
                    uncorrelated[i] = sampler.sample();
                }

                // Correlated vector.
                final double[] correlated = mean.clone();
                for (int i = 0; i < correlated.length; i++) {
                    for (int j = 0; j < lengthUncorrelated; j++) {
                        correlated[i] += root.getEntry(i, j) * uncorrelated[j];
                    }
                }

                return correlated;
            }
        };
    }
}
