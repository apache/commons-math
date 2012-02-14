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
package org.apache.commons.math3.analysis.polynomials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;

/**
 * A collection of static methods that operate on or return polynomials.
 *
 * @version $Id$
 * @since 2.0
 */
public class PolynomialsUtils {

    /** Coefficients for Chebyshev polynomials. */
    private static final List<BigFraction> CHEBYSHEV_COEFFICIENTS;

    /** Coefficients for Hermite polynomials. */
    private static final List<BigFraction> HERMITE_COEFFICIENTS;

    /** Coefficients for Laguerre polynomials. */
    private static final List<BigFraction> LAGUERRE_COEFFICIENTS;

    /** Coefficients for Legendre polynomials. */
    private static final List<BigFraction> LEGENDRE_COEFFICIENTS;

    /** Coefficients for Jacobi polynomials. */
    private static final Map<JacobiKey, List<BigFraction>> JACOBI_COEFFICIENTS;

    static {

        // initialize recurrence for Chebyshev polynomials
        // T0(X) = 1, T1(X) = 0 + 1 * X
        CHEBYSHEV_COEFFICIENTS = new ArrayList<BigFraction>();
        CHEBYSHEV_COEFFICIENTS.add(BigFraction.ONE);
        CHEBYSHEV_COEFFICIENTS.add(BigFraction.ZERO);
        CHEBYSHEV_COEFFICIENTS.add(BigFraction.ONE);

        // initialize recurrence for Hermite polynomials
        // H0(X) = 1, H1(X) = 0 + 2 * X
        HERMITE_COEFFICIENTS = new ArrayList<BigFraction>();
        HERMITE_COEFFICIENTS.add(BigFraction.ONE);
        HERMITE_COEFFICIENTS.add(BigFraction.ZERO);
        HERMITE_COEFFICIENTS.add(BigFraction.TWO);

        // initialize recurrence for Laguerre polynomials
        // L0(X) = 1, L1(X) = 1 - 1 * X
        LAGUERRE_COEFFICIENTS = new ArrayList<BigFraction>();
        LAGUERRE_COEFFICIENTS.add(BigFraction.ONE);
        LAGUERRE_COEFFICIENTS.add(BigFraction.ONE);
        LAGUERRE_COEFFICIENTS.add(BigFraction.MINUS_ONE);

        // initialize recurrence for Legendre polynomials
        // P0(X) = 1, P1(X) = 0 + 1 * X
        LEGENDRE_COEFFICIENTS = new ArrayList<BigFraction>();
        LEGENDRE_COEFFICIENTS.add(BigFraction.ONE);
        LEGENDRE_COEFFICIENTS.add(BigFraction.ZERO);
        LEGENDRE_COEFFICIENTS.add(BigFraction.ONE);

        // initialize map for Jacobi polynomials
        JACOBI_COEFFICIENTS = new HashMap<JacobiKey, List<BigFraction>>();

    }

    /**
     * Private constructor, to prevent instantiation.
     */
    private PolynomialsUtils() {
    }

    /**
     * Create a Chebyshev polynomial of the first kind.
     * <p><a href="http://mathworld.wolfram.com/ChebyshevPolynomialoftheFirstKind.html">Chebyshev
     * polynomials of the first kind</a> are orthogonal polynomials.
     * They can be defined by the following recurrence relations:
     * <pre>
     *  T<sub>0</sub>(X)   = 1
     *  T<sub>1</sub>(X)   = X
     *  T<sub>k+1</sub>(X) = 2X T<sub>k</sub>(X) - T<sub>k-1</sub>(X)
     * </pre></p>
     * @param degree degree of the polynomial
     * @return Chebyshev polynomial of specified degree
     */
    public static PolynomialFunction createChebyshevPolynomial(final int degree) {
        return buildPolynomial(degree, CHEBYSHEV_COEFFICIENTS,
                new RecurrenceCoefficientsGenerator() {
            private final BigFraction[] coeffs = { BigFraction.ZERO, BigFraction.TWO, BigFraction.ONE };
            /** {@inheritDoc} */
            public BigFraction[] generate(int k) {
                return coeffs;
            }
        });
    }

    /**
     * Create a Hermite polynomial.
     * <p><a href="http://mathworld.wolfram.com/HermitePolynomial.html">Hermite
     * polynomials</a> are orthogonal polynomials.
     * They can be defined by the following recurrence relations:
     * <pre>
     *  H<sub>0</sub>(X)   = 1
     *  H<sub>1</sub>(X)   = 2X
     *  H<sub>k+1</sub>(X) = 2X H<sub>k</sub>(X) - 2k H<sub>k-1</sub>(X)
     * </pre></p>

     * @param degree degree of the polynomial
     * @return Hermite polynomial of specified degree
     */
    public static PolynomialFunction createHermitePolynomial(final int degree) {
        return buildPolynomial(degree, HERMITE_COEFFICIENTS,
                new RecurrenceCoefficientsGenerator() {
            /** {@inheritDoc} */
            public BigFraction[] generate(int k) {
                return new BigFraction[] {
                        BigFraction.ZERO,
                        BigFraction.TWO,
                        new BigFraction(2 * k)};
            }
        });
    }

    /**
     * Create a Laguerre polynomial.
     * <p><a href="http://mathworld.wolfram.com/LaguerrePolynomial.html">Laguerre
     * polynomials</a> are orthogonal polynomials.
     * They can be defined by the following recurrence relations:
     * <pre>
     *        L<sub>0</sub>(X)   = 1
     *        L<sub>1</sub>(X)   = 1 - X
     *  (k+1) L<sub>k+1</sub>(X) = (2k + 1 - X) L<sub>k</sub>(X) - k L<sub>k-1</sub>(X)
     * </pre></p>
     * @param degree degree of the polynomial
     * @return Laguerre polynomial of specified degree
     */
    public static PolynomialFunction createLaguerrePolynomial(final int degree) {
        return buildPolynomial(degree, LAGUERRE_COEFFICIENTS,
                new RecurrenceCoefficientsGenerator() {
            /** {@inheritDoc} */
            public BigFraction[] generate(int k) {
                final int kP1 = k + 1;
                return new BigFraction[] {
                        new BigFraction(2 * k + 1, kP1),
                        new BigFraction(-1, kP1),
                        new BigFraction(k, kP1)};
            }
        });
    }

    /**
     * Create a Legendre polynomial.
     * <p><a href="http://mathworld.wolfram.com/LegendrePolynomial.html">Legendre
     * polynomials</a> are orthogonal polynomials.
     * They can be defined by the following recurrence relations:
     * <pre>
     *        P<sub>0</sub>(X)   = 1
     *        P<sub>1</sub>(X)   = X
     *  (k+1) P<sub>k+1</sub>(X) = (2k+1) X P<sub>k</sub>(X) - k P<sub>k-1</sub>(X)
     * </pre></p>
     * @param degree degree of the polynomial
     * @return Legendre polynomial of specified degree
     */
    public static PolynomialFunction createLegendrePolynomial(final int degree) {
        return buildPolynomial(degree, LEGENDRE_COEFFICIENTS,
                               new RecurrenceCoefficientsGenerator() {
            /** {@inheritDoc} */
            public BigFraction[] generate(int k) {
                final int kP1 = k + 1;
                return new BigFraction[] {
                        BigFraction.ZERO,
                        new BigFraction(k + kP1, kP1),
                        new BigFraction(k, kP1)};
            }
        });
    }

    /**
     * Create a Jacobi polynomial.
     * <p><a href="http://mathworld.wolfram.com/JacobiPolynomial.html">Jacobi
     * polynomials</a> are orthogonal polynomials.
     * They can be defined by the following recurrence relations:
     * <pre>
     *        P<sub>0</sub><sup>vw</sup>(X)   = 1
     *        P<sub>-1</sub><sup>vw</sup>(X)  = 0
     *  2k(k + v + w)(2k + v + w - 2) P<sub>k</sub><sup>vw</sup>(X) =
     *  (2k + v + w - 1)[(2k + v + w)(2k + v + w - 2) X + v<sup>2</sup> - w<sup>2</sup>] P<sub>k-1</sub><sup>vw</sup>(X)
     *  - 2(k + v - 1)(k + w - 1)(2k + v + w) P<sub>k-2</sub><sup>vw</sup>(X)
     * </pre></p>
     * @param degree degree of the polynomial
     * @param v first exponent
     * @param w second exponent
     * @return Jacobi polynomial of specified degree
     */
    public static PolynomialFunction createJacobiPolynomial(final int degree, final int v, final int w) {

        // select the appropriate list
        final JacobiKey key = new JacobiKey(v, w);

        if (!JACOBI_COEFFICIENTS.containsKey(key)) {

            // allocate a new list for v, w
            final List<BigFraction> list = new ArrayList<BigFraction>();
            JACOBI_COEFFICIENTS.put(key, list);

            // Pv,w,0(x) = 1;
            list.add(BigFraction.ONE);

            // P1(x) = (v - w) / 2 + (2 + v + w) * X / 2
            list.add(new BigFraction(v - w, 2));
            list.add(new BigFraction(2 + v + w, 2));

        }

        return buildPolynomial(degree, JACOBI_COEFFICIENTS.get(key),
                               new RecurrenceCoefficientsGenerator() {
            /** {@inheritDoc} */
            public BigFraction[] generate(int k) {
                k++;
                final int kvw      = k + v + w;
                final int twoKvw   = kvw + k;
                final int twoKvwM1 = twoKvw - 1;
                final int twoKvwM2 = twoKvw - 2;
                final int den      = 2 * k *  kvw * twoKvwM2;

                return new BigFraction[] {
                    new BigFraction(twoKvwM1 * (v * v - w * w), den),
                    new BigFraction(twoKvwM1 * twoKvw * twoKvwM2, den),
                    new BigFraction(2 * (k + v - 1) * (k + w - 1) * twoKvw, den)
                };
            }
        });

    }

    /** Inner class for Jacobi polynomials keys. */
    private static class JacobiKey {

        /** First exponent. */
        private final int v;

        /** Second exponent. */
        private final int w;

        /** Simple constructor.
         * @param v first exponent
         * @param w second exponent
         */
        public JacobiKey(final int v, final int w) {
            this.v = v;
            this.w = w;
        }

        /** Get hash code.
         * @return hash code
         */
        @Override
        public int hashCode() {
            return (v << 16) ^ w;
        }

        /** Check if the instance represent the same key as another instance.
         * @param key other key
         * @return true if the instance and the other key refer to the same polynomial
         */
        @Override
        public boolean equals(final Object key) {

            if ((key == null) || !(key instanceof JacobiKey)) {
                return false;
            }

            final JacobiKey otherK = (JacobiKey) key;
            return (v == otherK.v) && (w == otherK.w);

        }
    }

    /**
     * Compute the coefficients of the polynomial <code>P<sub>s</sub>(x)</code>
     * whose values at point {@code x} will be the same as the those from the
     * original polynomial <code>P(x)</code> when computed at {@code x + shift}.
     * Thus, if <code>P(x) = &Sigma;<sub>i</sub> a<sub>i</sub> x<sup>i</sup></code>,
     * then
     * <pre>
     *  <table>
     *   <tr>
     *    <td><code>P<sub>s</sub>(x)</td>
     *    <td>= &Sigma;<sub>i</sub> b<sub>i</sub> x<sup>i</sup></code></td>
     *   </tr>
     *   <tr>
     *    <td></td>
     *    <td>= &Sigma;<sub>i</sub> a<sub>i</sub> (x + shift)<sup>i</sup></code></td>
     *   </tr>
     *  </table>
     * </pre>
     *
     * @param coefficients Coefficients of the original polynomial.
     * @param shift Shift value.
     * @return the coefficients <code>b<sub>i</sub></code> of the shifted
     * polynomial.
     */
    public static double[] shift(final double[] coefficients,
                                 final double shift) {
        final int dp1 = coefficients.length;
        final double[] newCoefficients = new double[dp1];

        // Pascal triangle.
        final int[][] coeff = new int[dp1][dp1];
        for (int i = 0; i < dp1; i++){
            for(int j = 0; j <= i; j++){
                coeff[i][j] = (int) ArithmeticUtils.binomialCoefficient(i, j);
            }
        }

        // First polynomial coefficient.
        for (int i = 0; i < dp1; i++){
            newCoefficients[0] += coefficients[i] * FastMath.pow(shift, i);
        }

        // Superior order.
        final int d = dp1 - 1;
        for (int i = 0; i < d; i++) {
            for (int j = i; j < d; j++){
                newCoefficients[i + 1] += coeff[j + 1][j - i] *
                    coefficients[j + 1] * FastMath.pow(shift, j - i);
            }
        }

        return newCoefficients;
    }


    /** Get the coefficients array for a given degree.
     * @param degree degree of the polynomial
     * @param coefficients list where the computed coefficients are stored
     * @param generator recurrence coefficients generator
     * @return coefficients array
     */
    private static PolynomialFunction buildPolynomial(final int degree,
                                                      final List<BigFraction> coefficients,
                                                      final RecurrenceCoefficientsGenerator generator) {

        final int maxDegree = (int) FastMath.floor(FastMath.sqrt(2 * coefficients.size())) - 1;
        synchronized (PolynomialsUtils.class) {
            if (degree > maxDegree) {
                computeUpToDegree(degree, maxDegree, generator, coefficients);
            }
        }

        // coefficient  for polynomial 0 is  l [0]
        // coefficients for polynomial 1 are l [1] ... l [2] (degrees 0 ... 1)
        // coefficients for polynomial 2 are l [3] ... l [5] (degrees 0 ... 2)
        // coefficients for polynomial 3 are l [6] ... l [9] (degrees 0 ... 3)
        // coefficients for polynomial 4 are l[10] ... l[14] (degrees 0 ... 4)
        // coefficients for polynomial 5 are l[15] ... l[20] (degrees 0 ... 5)
        // coefficients for polynomial 6 are l[21] ... l[27] (degrees 0 ... 6)
        // ...
        final int start = degree * (degree + 1) / 2;

        final double[] a = new double[degree + 1];
        for (int i = 0; i <= degree; ++i) {
            a[i] = coefficients.get(start + i).doubleValue();
        }

        // build the polynomial
        return new PolynomialFunction(a);

    }

    /** Compute polynomial coefficients up to a given degree.
     * @param degree maximal degree
     * @param maxDegree current maximal degree
     * @param generator recurrence coefficients generator
     * @param coefficients list where the computed coefficients should be appended
     */
    private static void computeUpToDegree(final int degree, final int maxDegree,
                                          final RecurrenceCoefficientsGenerator generator,
                                          final List<BigFraction> coefficients) {

        int startK = (maxDegree - 1) * maxDegree / 2;
        for (int k = maxDegree; k < degree; ++k) {

            // start indices of two previous polynomials Pk(X) and Pk-1(X)
            int startKm1 = startK;
            startK += k;

            // Pk+1(X) = (a[0] + a[1] X) Pk(X) - a[2] Pk-1(X)
            BigFraction[] ai = generator.generate(k);

            BigFraction ck     = coefficients.get(startK);
            BigFraction ckm1   = coefficients.get(startKm1);

            // degree 0 coefficient
            coefficients.add(ck.multiply(ai[0]).subtract(ckm1.multiply(ai[2])));

            // degree 1 to degree k-1 coefficients
            for (int i = 1; i < k; ++i) {
                final BigFraction ckPrev = ck;
                ck     = coefficients.get(startK + i);
                ckm1   = coefficients.get(startKm1 + i);
                coefficients.add(ck.multiply(ai[0]).add(ckPrev.multiply(ai[1])).subtract(ckm1.multiply(ai[2])));
            }

            // degree k coefficient
            final BigFraction ckPrev = ck;
            ck = coefficients.get(startK + k);
            coefficients.add(ck.multiply(ai[0]).add(ckPrev.multiply(ai[1])));

            // degree k+1 coefficient
            coefficients.add(ck.multiply(ai[1]));

        }

    }

    /** Interface for recurrence coefficients generation. */
    private interface RecurrenceCoefficientsGenerator {
        /**
         * Generate recurrence coefficients.
         * @param k highest degree of the polynomials used in the recurrence
         * @return an array of three coefficients such that
         * P<sub>k+1</sub>(X) = (a[0] + a[1] X) P<sub>k</sub>(X) - a[2] P<sub>k-1</sub>(X)
         */
        BigFraction[] generate(int k);
    }

}
