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

package org.apache.commons.math.analysis.function;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;

/**
 * <a href="http://en.wikipedia.org/wiki/Sinc_function">Sinc</a> function.
 *
 * @version $Id$
 * @since 3.0
 */
public class Sinc implements UnivariateRealFunction {
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

    /**
     * @param x Argument.
     * @return {@code sin(x) / x}.
     */
    private static double sinc(double x) {
        // The direct assignment to 1 for values below 1e-9 is an efficiency
        // optimization on the ground that the result of the full computation
        // is indistinguishable from 1 due to the limited accuracy of the
        // floating point representation.
        return FastMath.abs(x) < 1e-9 ? 1 : FastMath.sin(x) / x;
    }
}
