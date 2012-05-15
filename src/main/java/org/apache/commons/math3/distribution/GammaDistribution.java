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
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.FastMath;

/**
 * Implementation of the Gamma distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Gamma_distribution">Gamma distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/GammaDistribution.html">Gamma distribution (MathWorld)</a>
 * @version $Id$
 */
public class GammaDistribution extends AbstractRealDistribution {
    /**
     * Default inverse cumulative probability accuracy.
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;

    /** Serializable version identifier. */
    private static final long serialVersionUID = -3239549463135430361L;

    /** The shape parameter. */
    private final double alpha;

    /** The scale parameter. */
    private final double beta;

    /**
     * The constant value of {@code alpha + g + 0.5}, where {@code alpha} is
     * the shape parameter, and {@code g} is the Lanczos constant
     * {@link Gamma#LANCZOS_G}.
     */
    private final double shiftedShape;

    /**
     * The constant value of
     * {@code alpha / beta * sqrt(e / (2 * pi * (alpha + g + 0.5))) / L(alpha)},
     * where {@code alpha} is the shape parameter, {@code beta} is the scale
     * parameter, and {@code L(alpha)} is the Lanczos approximation returned by
     * {@link Gamma#lanczos(double)}. This prefactor is used in
     * {@link #density(double)}, when no overflow occurs with the natural
     * calculation.
     */
    private final double densityPrefactor1;

    /**
     * The constant value of
     * {@code alpha * sqrt(e / (2 * pi * (alpha + g + 0.5))) / L(alpha)},
     * where {@code alpha} is the shape parameter, and {@code L(alpha)} is the
     * Lanczos approximation returned by {@link Gamma#lanczos(double)}. This
     * prefactor is used in {@link #density(double)}, when overflow occurs with
     * the natural calculation.
     */
    private final double densityPrefactor2;

    /**
     * Lower bound on {@code y = x / beta} for the selection of the computation
     * method in {@link #density(double)}. For {@code y <= minY}, the natural
     * calculation overflows. {@code beta} is the shape parameter.
     */
    private final double minY;

    /**
     * Upper bound on {@code log(y)} ({@code y = x / beta}) for the selection of
     * the computation method in {@link #density(double)}. For
     * {@code log(y) >= maxLogY}, the natural calculation overflows.
     * {@code beta} is the shape parameter.
     */
    private final double maxLogY;

    /** Inverse cumulative probability accuracy. */
    private final double solverAbsoluteAccuracy;

    /**
     * Create a new gamma distribution with the given {@code alpha} and
     * {@code beta} values.
     * @param alpha the shape parameter.
     * @param beta the scale parameter.
     */
    public GammaDistribution(double alpha, double beta) {
        this(alpha, beta, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Create a new gamma distribution with the given {@code alpha} and
     * {@code beta} values.
     *
     * @param alpha Shape parameter.
     * @param beta Scale parameter.
     * @param inverseCumAccuracy Maximum absolute error in inverse
     * cumulative probability estimates (defaults to
     * {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY}).
     * @throws NotStrictlyPositiveException if {@code alpha <= 0} or
     * {@code beta <= 0}.
     * @since 2.1
     */
    public GammaDistribution(double alpha, double beta, double inverseCumAccuracy)
        throws NotStrictlyPositiveException {
        if (alpha <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.ALPHA, alpha);
        }
        if (beta <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.BETA, beta);
        }

        this.alpha = alpha;
        this.beta = beta;
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
        this.shiftedShape = alpha + Gamma.LANCZOS_G + 0.5;
        final double aux = FastMath.E / (2.0 * FastMath.PI * shiftedShape);
        this.densityPrefactor2 = alpha * FastMath.sqrt(aux) / Gamma.lanczos(alpha);
        this.densityPrefactor1 = this.densityPrefactor2 / beta *
                FastMath.pow(shiftedShape, -alpha) *
                FastMath.exp(alpha + Gamma.LANCZOS_G);
        this.minY = alpha + Gamma.LANCZOS_G - FastMath.log(Double.MAX_VALUE);
        this.maxLogY = FastMath.log(Double.MAX_VALUE) / (alpha - 1.0);
    }

    /**
     * Access the {@code alpha} shape parameter.
     *
     * @return {@code alpha}.
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Access the {@code beta} scale parameter.
     *
     * @return {@code beta}.
     */
    public double getBeta() {
        return beta;
    }

    /**
     * {@inheritDoc}
     *
     * For this distribution {@code P(X = x)} always evaluates to 0.
     *
     * @return 0
     */
    public double probability(double x) {
        return 0.0;
    }

    /** {@inheritDoc} */
    public double density(double x) {
       /* The present method must return the value of
        *
        *     1       x a     - x
        * ---------- (-)  exp(---)
        * x Gamma(a)  b        b
        *
        * where a is the shape parameter, and b the scale parameter.
        * Substituting the Lanczos approximation of Gamma(a) leads to the
        * following expression of the density
        *
        * a              e            1         y      a
        * - sqrt(------------------) ---- (-----------)  exp(a - y + g),
        * x      2 pi (a + g + 0.5)  L(a)  a + g + 0.5
        *
        * where y = x / b. The above formula is the "natural" computation, which
        * is implemented when no overflow is likely to occur. If overflow occurs
        * with the natural computation, the following identity is used. It is
        * based on the BOOST library
        * http://www.boost.org/doc/libs/1_35_0/libs/math/doc/sf_and_dist/html/math_toolkit/special/sf_gamma/igamma.html
        * Formula (15) needs adaptations, which are detailed below.
        *
        *       y      a
        * (-----------)  exp(a - y + g)
        *  a + g + 0.5
        *                              y - a - g - 0.5    y (g + 0.5)
        *               = exp(a log1pm(---------------) - ----------- + g),
        *                                a + g + 0.5      a + g + 0.5
        *
        *  where log1pm(z) = log(1 + z) - z. Therefore, the value to be
        *  returned is
        *
        * a              e            1
        * - sqrt(------------------) ----
        * x      2 pi (a + g + 0.5)  L(a)
        *                              y - a - g - 0.5    y (g + 0.5)
        *               * exp(a log1pm(---------------) - ----------- + g).
        *                                a + g + 0.5      a + g + 0.5
        */
        if (x < 0) {
            return 0;
        }
        final double y = x / beta;
        if ((y <= minY) || (FastMath.log(y) >= maxLogY)) {
            /*
             * Overflow.
             */
            final double aux1 = (y - shiftedShape) / shiftedShape;
            final double aux2 = alpha * (FastMath.log1p(aux1) - aux1);
            final double aux3 = -y * (Gamma.LANCZOS_G + 0.5) / shiftedShape +
                    Gamma.LANCZOS_G + aux2;
            return densityPrefactor2 / x * FastMath.exp(aux3);
        }
        /*
         * Natural calculation.
         */
        return densityPrefactor1  * FastMath.exp(-y) *
                FastMath.pow(y, alpha - 1);
    }

    /**
     * {@inheritDoc}
     *
     * The implementation of this method is based on:
     * <ul>
     *  <li>
     *   <a href="http://mathworld.wolfram.com/Chi-SquaredDistribution.html">
     *    Chi-Squared Distribution</a>, equation (9).
     *  </li>
     *  <li>Casella, G., & Berger, R. (1990). <i>Statistical Inference</i>.
     *    Belmont, CA: Duxbury Press.
     *  </li>
     * </ul>
     */
    public double cumulativeProbability(double x) {
        double ret;

        if (x <= 0) {
            ret = 0;
        } else {
            ret = Gamma.regularizedGammaP(alpha, x / beta);
        }

        return ret;
    }

    /** {@inheritDoc} */
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
    }

    /**
     * {@inheritDoc}
     *
     * For shape parameter {@code alpha} and scale parameter {@code beta}, the
     * mean is {@code alpha * beta}.
     */
    public double getNumericalMean() {
        return getAlpha() * getBeta();
    }

    /**
     * {@inheritDoc}
     *
     * For shape parameter {@code alpha} and scale parameter {@code beta}, the
     * variance is {@code alpha * beta^2}.
     *
     * @return {@inheritDoc}
     */
    public double getNumericalVariance() {
        final double b = getBeta();
        return getAlpha() * b * b;
    }

    /**
     * {@inheritDoc}
     *
     * The lower bound of the support is always 0 no matter the parameters.
     *
     * @return lower bound of the support (always 0)
     */
    public double getSupportLowerBound() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * The upper bound of the support is always positive infinity
     * no matter the parameters.
     *
     * @return upper bound of the support (always Double.POSITIVE_INFINITY)
     */
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /** {@inheritDoc} */
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    /** {@inheritDoc} */
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * The support of this distribution is connected.
     *
     * @return {@code true}
     */
    public boolean isSupportConnected() {
        return true;
    }
}
