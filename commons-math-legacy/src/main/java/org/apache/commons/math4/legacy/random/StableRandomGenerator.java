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

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.core.jdkmath.AccurateMath;

/**
 * <p>This class provides a stable normalized random generator. It samples from a stable
 * distribution with location parameter 0 and scale 1.</p>
 *
 * <p>The implementation uses the Chambers-Mallows-Stuck method as described in
 * <i>Handbook of computational statistics: concepts and methods</i> by
 * James E. Gentle, Wolfgang H&auml;rdle, Yuichi Mori.</p>
 *
 * @since 3.0
 */
public class StableRandomGenerator implements NormalizedRandomGenerator {
    /** Underlying generator. */
    private final UniformRandomProvider generator;
    /** stability parameter. */
    private final double alpha;
    /** skewness parameter. */
    private final double beta;
    /** cache of expression value used in generation. */
    private final double zeta;

    /**
     * Create a new generator.
     *
     * @param generator Underlying random generator
     * @param alpha Stability parameter. Must be in range (0, 2]
     * @param beta Skewness parameter. Must be in range [-1, 1]
     * @throws NullArgumentException if generator is null
     * @throws OutOfRangeException if {@code alpha <= 0} or {@code alpha > 2}
     * or {@code beta < -1} or {@code beta > 1}
     */
    public StableRandomGenerator(final UniformRandomProvider generator,
                                 final double alpha, final double beta)
        throws NullArgumentException, OutOfRangeException {
        if (generator == null) {
            throw new NullArgumentException();
        }

        if (!(alpha > 0d && alpha <= 2d)) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_LEFT,
                    alpha, 0, 2);
        }

        if (!(beta >= -1d && beta <= 1d)) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_SIMPLE,
                    beta, -1, 1);
        }

        this.generator = generator;
        this.alpha = alpha;
        this.beta = beta;
        if (alpha < 2d && beta != 0d) {
            zeta = beta * AccurateMath.tan(AccurateMath.PI * alpha / 2);
        } else {
            zeta = 0d;
        }
    }

    /**
     * Generate a random scalar with zero location and unit scale.
     *
     * @return a random scalar with zero location and unit scale
     */
    @Override
    public double nextNormalizedDouble() {
        // we need 2 uniform random numbers to calculate omega and phi
        double omega = -AccurateMath.log(generator.nextDouble());
        double phi = AccurateMath.PI * (generator.nextDouble() - 0.5);

        // Normal distribution case (Box-Muller algorithm)
        if (alpha == 2d) {
            return AccurateMath.sqrt(2d * omega) * AccurateMath.sin(phi);
        }

        double x;
        // when beta = 0, zeta is zero as well
        // Thus we can exclude it from the formula
        if (beta == 0d) {
            // Cauchy distribution case
            if (alpha == 1d) {
                x = AccurateMath.tan(phi);
            } else {
                x = AccurateMath.pow(omega * AccurateMath.cos((1 - alpha) * phi),
                    1d / alpha - 1d) *
                    AccurateMath.sin(alpha * phi) /
                    AccurateMath.pow(AccurateMath.cos(phi), 1d / alpha);
            }
        } else {
            // Generic stable distribution
            double cosPhi = AccurateMath.cos(phi);
            // to avoid rounding errors around alpha = 1
            if (AccurateMath.abs(alpha - 1d) > 1e-8) {
                double alphaPhi = alpha * phi;
                double invAlphaPhi = phi - alphaPhi;
                x = (AccurateMath.sin(alphaPhi) + zeta * AccurateMath.cos(alphaPhi)) / cosPhi *
                    (AccurateMath.cos(invAlphaPhi) + zeta * AccurateMath.sin(invAlphaPhi)) /
                     AccurateMath.pow(omega * cosPhi, (1 - alpha) / alpha);
            } else {
                double betaPhi = AccurateMath.PI / 2 + beta * phi;
                x = 2d / AccurateMath.PI * (betaPhi * AccurateMath.tan(phi) - beta *
                    AccurateMath.log(AccurateMath.PI / 2d * omega * cosPhi / betaPhi));

                if (alpha != 1d) {
                    x += beta * AccurateMath.tan(AccurateMath.PI * alpha / 2);
                }
            }
        }
        return x;
    }
}
