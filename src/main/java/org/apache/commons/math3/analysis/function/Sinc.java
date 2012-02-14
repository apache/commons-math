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

package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.util.FastMath;

/**
 * <a href="http://en.wikipedia.org/wiki/Sinc_function">Sinc</a> function,
 * defined by
 * <pre><code>
 *   sinc(x) = 1            if x = 0,
 *             sin(x) / x   otherwise.
 * </code></pre>
 *
 * @version $Id$
 * @since 3.0
 */
public class Sinc implements DifferentiableUnivariateFunction {
    /**
     * Value below which the result of the computation will not change
     * anymore due to the finite precision of the "double" representation
     * of real numbers.
     */
    private static final double SHORTCUT = 1e-9;
    /** For normalized sinc function. */
    private final boolean normalized;

    /**
     * The sinc function, {@code sin(x) / x}.
     */
    public Sinc() {
        this(false);
    }

    /**
     * Instantiates the sinc function.
     *
     * @param normalized If {@code true}, the function is
     * <code> sin(&pi;x) / &pi;x</code>, otherwise {@code sin(x) / x}.
     */
    public Sinc(boolean normalized) {
        this.normalized = normalized;
    }

    /** {@inheritDoc} */
    public double value(double x) {
        if (normalized) {
            final double piTimesX = Math.PI * x;
            return sinc(piTimesX);
        } else {
            return sinc(x);
        }
    }

    /** {@inheritDoc} */
    public UnivariateFunction derivative() {
        if (normalized) {
            return new UnivariateFunction() {
                /** {@inheritDoc} */
                public double value(double x) {
                    final double piTimesX = Math.PI * x;
                    return sincDerivative(piTimesX);
                }
            };
        } else {
            return new UnivariateFunction() {
                /** {@inheritDoc} */
                public double value(double x) {
                    return sincDerivative(x);
                }
            };
        }
    }

    /**
     * @param x Argument.
     * @return {@code sin(x) / x}.
     */
    private static double sinc(double x) {
        // The direct assignment to 1 for values below 1e-9 is an efficiency
        // optimization on the ground that the result of the full computation
        // is indistinguishable from 1 due to the limited accuracy of the
        // floating point representation.
        return FastMath.abs(x) < SHORTCUT ? 1 :
            FastMath.sin(x) / x;
    }

    /**
     * @param x Argument.
     * @return {@code (cos(x) - sin(x) / x) / x}.
     */
    private static double sincDerivative(double x) {
        // The direct assignment to 0 for values below 1e-9 is an efficiency
        // optimization on the ground that the result of the full computation
        // is indistinguishable from 1 due to the limited accuracy of the
        // floating point representation.
        return FastMath.abs(x) < SHORTCUT ? 0 :
            (FastMath.cos(x) - FastMath.sin(x) / x) / x;
    }
}
