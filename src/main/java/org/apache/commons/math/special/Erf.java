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
package org.apache.commons.math.special;

import org.apache.commons.math.util.FastMath;

/**
 * This is a utility class that provides computation methods related to the
 * error functions.
 *
 * @version $Id$
 */
public class Erf {

    /**
     * The number {@code X_CRIT} is used by {@link #erf(double, double)} internally.
     * This number solves {@code erf(x)=0.5} within 1ulp.
     * More precisely, the current implementations of
     * {@link #erf(double)} and {@link #erfc(double)} satisfy:<br/>
     * {@code erf(X_CRIT) < 0.5},<br/>
     * {@code erf(Math.nextUp(X_CRIT) > 0.5},<br/>
     * {@code erfc(X_CRIT) = 0.5}, and<br/>
     * {@code erfc(Math.nextUp(X_CRIT) < 0.5}
     */
    private static final double X_CRIT = 0.4769362762044697;

    /**
     * Default constructor.  Prohibit instantiation.
     */
    private Erf() {}

    /**
     * Returns the error function.
     *
     * <p>erf(x) = 2/&radic;&pi; <sub>0</sub>&int;<sup>x</sup> e<sup>-t<sup>2</sup></sup>dt </p>
     *
     * <p>This implementation computes erf(x) using the
     * {@link Gamma#regularizedGammaP(double, double, double, int) regularized gamma function},
     * following <a href="http://mathworld.wolfram.com/Erf.html"> Erf</a>, equation (3)</p>
     *
     * <p>The value returned is always between -1 and 1 (inclusive).
     * If {@code abs(x) > 40}, then {@code erf(x)} is indistinguishable from
     * either 1 or -1 as a double, so the appropriate extreme value is returned.
     * </p>
     *
     * @param x the value.
     * @return the error function erf(x)
     * @throws org.apache.commons.math.exception.MaxCountExceededException
     * if the algorithm fails to converge.
     * @see Gamma#regularizedGammaP(double, double, double, int)
     */
    public static double erf(double x) {
        if (FastMath.abs(x) > 40) {
            return x > 0 ? 1 : -1;
        }
        final double ret = Gamma.regularizedGammaP(0.5, x * x, 1.0e-15, 10000);
        return x < 0 ? -ret : ret;
    }

    /**
     * Returns the complementary error function.
     *
     * <p>erfc(x) = 2/&radic;&pi; <sub>x</sub>&int;<sup>&infin;</sup> e<sup>-t<sup>2</sup></sup>dt
     * <br/>
     *    = 1 - {@link #erf(double) erf(x)} </p>
     *
     * <p>This implementation computes erfc(x) using the
     * {@link Gamma#regularizedGammaQ(double, double, double, int) regularized gamma function},
     * following <a href="http://mathworld.wolfram.com/Erf.html"> Erf</a>, equation (3).</p>
     *
     * <p>The value returned is always between 0 and 2 (inclusive).
     * If {@code abs(x) > 40}, then {@code erf(x)} is indistinguishable from
     * either 0 or 2 as a double, so the appropriate extreme value is returned.
     * </p>
     *
     * @param x the value
     * @return the complementary error function erfc(x)
     * @throws org.apache.commons.math.exception.MaxCountExceededException
     * if the algorithm fails to converge.
     * @see Gamma#regularizedGammaQ(double, double, double, int)
     * @since 2.2
     */
    public static double erfc(double x) {
        if (FastMath.abs(x) > 40) {
            return x > 0 ? 0 : 2;
        }
        final double ret = Gamma.regularizedGammaQ(0.5, x * x, 1.0e-15, 10000);
        return x < 0 ? 2 - ret : ret;
    }

    /**
     * Returns the difference between erf(x1) and erf(x2).
     *
     * The implementation uses either erf(double) or erfc(double)
     * depending on which provides the most precise result.
     *
     * @param x1 the first value
     * @param x2 the second value
     * @return erf(x2) - erf(x1)
     */
    public static double erf(double x1, double x2) {
        if(x1 > x2) {
            return -erf(x2, x1);
        }

        return
        x1 < -X_CRIT ?
            x2 < 0.0 ?
                erfc(-x2) - erfc(-x1) :
                erf(x2) - erf(x1) :
            x2 > X_CRIT && x1 > 0.0 ?
                erfc(x1) - erfc(x2) :
                erf(x2) - erf(x1);
    }
}

