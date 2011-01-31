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
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.util.FastMath;

/**
 * <a href="http://en.wikipedia.org/wiki/Generalised_logistic_function">
 *  Generalised logistic</a> function.
 *
 * @version $Revision$ $Date$
 * @since 3.0
 */
public class Logistic implements UnivariateRealFunction {
    /** Lower asymptote. */
    private final double a;
    /** Upper asymptote. */
    private final double k;
    /** Growth rate. */
    private final double b;
    /** Parameter that affects near which asymptote maximum growth occurs. */
    private final double n;
    /** Parameter that affects the position of the curve along the ordinate axis. */
    private final double q;
    /** Abscissa of maximum growth. */
    private final double m;

    /**
     * @param k If {@code b > 0}, value of the function for x going towards +&infin;.
     * If {@code b < 0}, value of the function for x going towards -&infin;.
     * @param m Abscissa of maximum growth.
     * @param b Growth rate.
     * @param q Parameter that affects the position of the curve along the
     * ordinate axis.
     * @param a If {@code b > 0}, value of the function for x going towards -&infin;.
     * If {@code b < 0}, value of the function for x going towards +&infin;.
     * @param n Parameter that affects near which asymptote the maximum
     * growth occurs.
     * @throws NotStrictlyPositiveException if {@code n <= 0}.
     */
    public Logistic(double k,
                    double m,
                    double b,
                    double q,
                    double a,
                    double n) {
        if (n <= 0) {
            throw new NotStrictlyPositiveException(n);
        }

        this.k = k;
        this.m = m;
        this.b = b;
        this.q = q;
        this.a = a;
        this.n = n;
    }

    /** {@inheritDoc} */
    public double value(double x) {
        return a + (k - a) / FastMath.pow(1 + q * FastMath.exp(b * (m - x)), 1 / n);
    }
}
