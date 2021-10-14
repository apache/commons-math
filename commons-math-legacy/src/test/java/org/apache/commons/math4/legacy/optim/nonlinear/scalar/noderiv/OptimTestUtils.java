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
package org.apache.commons.math4.legacy.optim.nonlinear.scalar.noderiv;

import java.util.Arrays;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.simple.ThreadLocalRandomSource;
import org.apache.commons.rng.sampling.distribution.MarsagliaNormalizedGaussianSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * Utilities for testing the optimizers.
 */
final class OptimTestUtils {

    /** No instances. */
    private OptimTestUtils() {}


    static class ElliRotated implements MultivariateFunction {
        private Basis B = new Basis();
        private double factor;

        ElliRotated() {
            this(1e3);
        }

        ElliRotated(double axisratio) {
            factor = axisratio * axisratio;
        }

        @Override
        public double value(double[] x) {
            double f = 0;
            x = B.Rotate(x);
            for (int i = 0; i < x.length; ++i) {
                f += JdkMath.pow(factor, i / (x.length - 1.)) * x[i] * x[i];
            }
            return f;
        }
    }

    static class FourExtrema implements MultivariateFunction {
        // The following function has 4 local extrema.
        static final double xM = -3.841947088256863675365;
        static final double yM = -1.391745200270734924416;
        static final double xP =  0.2286682237349059125691;
        static final double yP = -yM;
        static final double valueXmYm = 0.2373295333134216789769; // Local maximum.
        static final double valueXmYp = -valueXmYm; // Local minimum.
        static final double valueXpYm = -0.7290400707055187115322; // Global minimum.
        static final double valueXpYp = -valueXpYm; // Global maximum.

        @Override
        public double value(double[] variables) {
            final double x = variables[0];
            final double y = variables[1];
            return (x == 0 || y == 0) ? 0 :
                JdkMath.atan(x) * JdkMath.atan(x + 2) * JdkMath.atan(y) * JdkMath.atan(y) / (x * y);
        }
    }

    static double[] point(int n, double value) {
        final double[] ds = new double[n];
        Arrays.fill(ds, value);
        return ds;
    }

    /**
     * @param n Dimension.
     * @param value Value.
     * @param jitter Random noise to add to {@code value}.
     */
    static double[] point(int n,
                          double value,
                          double jitter) {
        final double[] ds = new double[n];
        Arrays.fill(ds, value);
        return point(ds, jitter);
    }

    /**
     * @param point Point.
     * @param jitter Random noise to add to each {@code value} element.
     */
    static double[] point(double[] value,
                          double jitter) {
        final ContinuousUniformSampler s = new ContinuousUniformSampler(rng(),
                                                                        -jitter,
                                                                        jitter);
        final double[] ds = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            ds[i] = value[i] + s.sample();
        }
        return ds;
    }

    /** Creates a RNG instance. */
    static UniformRandomProvider rng() {
        return ThreadLocalRandomSource.current(RandomSource.MWC_256);
    }

    private static class Basis {
        private double[][] basis;
        private final MarsagliaNormalizedGaussianSampler rand = MarsagliaNormalizedGaussianSampler.of(rng()); // use not always the same basis

        double[] Rotate(double[] x) {
            GenBasis(x.length);
            double[] y = new double[x.length];
            for (int i = 0; i < x.length; ++i) {
                y[i] = 0;
                for (int j = 0; j < x.length; ++j) {
                    y[i] += basis[i][j] * x[j];
                }
            }
            return y;
        }

        void GenBasis(int dim) {
            if (basis != null ? basis.length == dim : false) {
                return;
            }

            double sp;
            int i;
            int j;
            int k;

            /* generate orthogonal basis */
            basis = new double[dim][dim];
            for (i = 0; i < dim; ++i) {
                /* sample components gaussian */
                for (j = 0; j < dim; ++j) {
                    basis[i][j] = rand.sample();
                }
                /* substract projection of previous vectors */
                for (j = i - 1; j >= 0; --j) {
                    for (sp = 0., k = 0; k < dim; ++k) {
                        sp += basis[i][k] * basis[j][k]; /* scalar product */
                    }
                    for (k = 0; k < dim; ++k) {
                        basis[i][k] -= sp * basis[j][k]; /* substract */
                    }
                }
                /* normalize */
                for (sp = 0., k = 0; k < dim; ++k) {
                    sp += basis[i][k] * basis[i][k]; /* squared norm */
                }
                for (k = 0; k < dim; ++k) {
                    basis[i][k] /= JdkMath.sqrt(sp);
                }
            }
        }
    }
}
