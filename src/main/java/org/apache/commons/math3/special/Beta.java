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
package org.apache.commons.math3.special;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.ContinuedFraction;
import org.apache.commons.math3.util.FastMath;

/**
 * <p>
 * This is a utility class that provides computation methods related to the
 * Beta family of functions.
 * </p>
 * <p>
 * Implementation of {@link #bcorr(double, double)} is based on the algorithms
 * described in
 * </p>
 * <ul>
 * <li><a href="http://dx.doi.org/10.1145/22721.23109">Didonato and Morris
 * (1986)</a>, <em>Computation of the Incomplete Gamma Function Ratios and
 *     their Inverse</em>, TOMS 12(4), 377-393,</li>
 * <li><a href="http://dx.doi.org/10.1145/131766.131776">Didonato and Morris
 * (1992)</a>, <em>Algorithm 708: Significant Digit Computation of the
 *     Incomplete Beta Function Ratios</em>, TOMS 18(3), 360-373,</li>
 * </ul>
 * <p>
 * and implemented in the
 * <a href="http://www.dtic.mil/docs/citations/ADA476840">NSWC Library of Mathematical Functions</a>,
 * available
 * <a href="http://www.ualberta.ca/CNS/RESEARCH/Software/NumericalNSWC/site.html">here</a>.
 * This library is "approved for public release", and the
 * <a href="http://www.dtic.mil/dtic/pdf/announcements/CopyrightGuidance.pdf">Copyright guidance</a>
 * indicates that unless otherwise stated in the code, all FORTRAN functions in
 * this library are license free. Since no such notice appears in the code these
 * functions can safely be ported to Commons-Math.
 * </p>
 *
 * @version $Id$
 */
public class Beta {
    /** Maximum allowed numerical error. */
    private static final double DEFAULT_EPSILON = 1E-14;

    /**
     * <p>
     * The coefficients of the series expansion of the Δ function. This
     * function is defined as follows
     * </p>
     * <center>Δ(x) = log Γ(x) - (x - 0.5) log a + a - 0.5 log 2π,</center>
     * <p>
     * see equation (23) in Didonato and Morris (1992). The series expansion
     * reads
     * </p>
     * <pre>
     *                n
     *               ====
     *            1  \             i
     *    Δ(x) = ---  >    DELTA  t
     *            x  /          i
     *               ====
     *               i = 0
     * </pre>
     * <p>
     * where {@code t = (10 / x)^2}. This series applies for {@code x >= 10.0}.
     * </p>
     */
    private static final double[] DELTA = {
        .833333333333333333333333333333E-01,
        -.277777777777777777777777752282E-04,
        .793650793650793650791732130419E-07,
        -.595238095238095232389839236182E-09,
        .841750841750832853294451671990E-11,
        -.191752691751854612334149171243E-12,
        .641025640510325475730918472625E-14,
        -.295506514125338232839867823991E-15,
        .179643716359402238723287696452E-16,
        -.139228964661627791231203060395E-17,
        .133802855014020915603275339093E-18,
        -.154246009867966094273710216533E-19,
        .197701992980957427278370133333E-20,
        -.234065664793997056856992426667E-21,
        .171348014966398575409015466667E-22
    };

    /**
     * Default constructor.  Prohibit instantiation.
     */
    private Beta() {}

    /**
     * Returns the
     * <a href="http://mathworld.wolfram.com/RegularizedBetaFunction.html">
     * regularized beta function</a> I(x, a, b).
     *
     * @param x Value.
     * @param a Parameter {@code a}.
     * @param b Parameter {@code b}.
     * @return the regularized beta function I(x, a, b).
     * @throws org.apache.commons.math3.exception.MaxCountExceededException
     * if the algorithm fails to converge.
     */
    public static double regularizedBeta(double x, double a, double b) {
        return regularizedBeta(x, a, b, DEFAULT_EPSILON, Integer.MAX_VALUE);
    }

    /**
     * Returns the
     * <a href="http://mathworld.wolfram.com/RegularizedBetaFunction.html">
     * regularized beta function</a> I(x, a, b).
     *
     * @param x Value.
     * @param a Parameter {@code a}.
     * @param b Parameter {@code b}.
     * @param epsilon When the absolute value of the nth item in the
     * series is less than epsilon the approximation ceases to calculate
     * further elements in the series.
     * @return the regularized beta function I(x, a, b)
     * @throws org.apache.commons.math3.exception.MaxCountExceededException
     * if the algorithm fails to converge.
     */
    public static double regularizedBeta(double x,
                                         double a, double b,
                                         double epsilon) {
        return regularizedBeta(x, a, b, epsilon, Integer.MAX_VALUE);
    }

    /**
     * Returns the regularized beta function I(x, a, b).
     *
     * @param x the value.
     * @param a Parameter {@code a}.
     * @param b Parameter {@code b}.
     * @param maxIterations Maximum number of "iterations" to complete.
     * @return the regularized beta function I(x, a, b)
     * @throws org.apache.commons.math3.exception.MaxCountExceededException
     * if the algorithm fails to converge.
     */
    public static double regularizedBeta(double x,
                                         double a, double b,
                                         int maxIterations) {
        return regularizedBeta(x, a, b, DEFAULT_EPSILON, maxIterations);
    }

    /**
     * Returns the regularized beta function I(x, a, b).
     *
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/RegularizedBetaFunction.html">
     * Regularized Beta Function</a>.</li>
     * <li>
     * <a href="http://functions.wolfram.com/06.21.10.0001.01">
     * Regularized Beta Function</a>.</li>
     * </ul>
     *
     * @param x the value.
     * @param a Parameter {@code a}.
     * @param b Parameter {@code b}.
     * @param epsilon When the absolute value of the nth item in the
     * series is less than epsilon the approximation ceases to calculate
     * further elements in the series.
     * @param maxIterations Maximum number of "iterations" to complete.
     * @return the regularized beta function I(x, a, b)
     * @throws org.apache.commons.math3.exception.MaxCountExceededException
     * if the algorithm fails to converge.
     */
    public static double regularizedBeta(double x,
                                         final double a, final double b,
                                         double epsilon, int maxIterations) {
        double ret;

        if (Double.isNaN(x) ||
            Double.isNaN(a) ||
            Double.isNaN(b) ||
            x < 0 ||
            x > 1 ||
            a <= 0.0 ||
            b <= 0.0) {
            ret = Double.NaN;
        } else if (x > (a + 1.0) / (a + b + 2.0)) {
            ret = 1.0 - regularizedBeta(1.0 - x, b, a, epsilon, maxIterations);
        } else {
            ContinuedFraction fraction = new ContinuedFraction() {

                @Override
                protected double getB(int n, double x) {
                    double ret;
                    double m;
                    if (n % 2 == 0) { // even
                        m = n / 2.0;
                        ret = (m * (b - m) * x) /
                            ((a + (2 * m) - 1) * (a + (2 * m)));
                    } else {
                        m = (n - 1.0) / 2.0;
                        ret = -((a + m) * (a + b + m) * x) /
                                ((a + (2 * m)) * (a + (2 * m) + 1.0));
                    }
                    return ret;
                }

                @Override
                protected double getA(int n, double x) {
                    return 1.0;
                }
            };
            ret = FastMath.exp((a * FastMath.log(x)) + (b * FastMath.log(1.0 - x)) -
                FastMath.log(a) - logBeta(a, b, epsilon, maxIterations)) *
                1.0 / fraction.evaluate(x, epsilon, maxIterations);
        }

        return ret;
    }

    /**
     * Returns the natural logarithm of the beta function B(a, b).
     *
     * @param a Parameter {@code a}.
     * @param b Parameter {@code b}.
     * @return log(B(a, b)).
     */
    public static double logBeta(double a, double b) {
        return logBeta(a, b, DEFAULT_EPSILON, Integer.MAX_VALUE);
    }

    /**
     * Returns the natural logarithm of the beta function B(a, b).
     *
     * The implementation of this method is based on:
     * <ul>
     * <li><a href="http://mathworld.wolfram.com/BetaFunction.html">
     * Beta Function</a>, equation (1).</li>
     * </ul>
     *
     * @param a Parameter {@code a}.
     * @param b Parameter {@code b}.
     * @param epsilon When the absolute value of the nth item in the
     * series is less than epsilon the approximation ceases to calculate
     * further elements in the series.
     * @param maxIterations Maximum number of "iterations" to complete.
     * @return log(B(a, b)).
     */
    public static double logBeta(double a, double b,
                                 double epsilon,
                                 int maxIterations) {
        double ret;

        if (Double.isNaN(a) ||
            Double.isNaN(b) ||
            a <= 0.0 ||
            b <= 0.0) {
            ret = Double.NaN;
        } else {
            ret = Gamma.logGamma(a) + Gamma.logGamma(b) -
                Gamma.logGamma(a + b);
        }

        return ret;
    }

    /**
     * Returns the value of Δ(p) + Δ(q) - Δ(p + q), with p, q ≥ 10. Based on
     * the <em>NSWC Library of Mathematics Subroutines</em> implementation,
     * {@code BCORR}.
     *
     * @param p First argument.
     * @param q Second argument.
     * @return the value of {@code Delta(p) + Delta(q) - Delta(p + q)}.
     * @throws NumberIsTooSmallException if {@code p < 10.0} or {@code q < 10.0}.
     */
    static final double bcorr(final double p, final double q) {

        if (p < 10.0) {
            throw new NumberIsTooSmallException(p, 10.0, true);
        }
        if (q < 10.0) {
            throw new NumberIsTooSmallException(q, 10.0, true);
        }

        final double a = FastMath.min(p, q);
        final double b = FastMath.max(p, q);
        final double h = a / b;
        final double c = h / (1.0 + h);
        final double x = 1.0 / (1.0 + h);
        final double x2 = x * x;
        /*
         * Compute s[i] = (1 - x**(2 * i + 1)) / (1 - x)
         */
        final double[] s = new double[DELTA.length];
        s[0] = 1.0;
        for (int i = 1; i < s.length; i++) {
            s[i] = 1.0 + (x + x2 * s[i - 1]);
        }
        /*
         * Set w = Delta(b) - Delta(a + b)
         */
        double tmp = 10.0 / b;
        final double tb = tmp * tmp;
        double w = DELTA[DELTA.length - 1] * s[s.length - 1];
        for (int i = DELTA.length - 2; i >= 0; i--) {
            w = tb * w + DELTA[i] * s[i];
        }
        w *= c / b;
        /*
         * Compute Delta(a) + w
         */
        tmp = 10.0 / a;
        final double ta = tmp * tmp;
        double z = DELTA[DELTA.length - 1];
        for (int i = DELTA.length - 2; i >= 0; i--) {
            z = ta * z + DELTA[i];
        }
        return z / a + w;
    }
}
