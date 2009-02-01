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
package org.apache.commons.math.analysis.polynomials;

import java.util.ArrayList;

import org.apache.commons.math.fraction.Fraction;

/**
 * A collection of static methods that operate on or return polynomials.
 * 
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class PolynomialsUtils {

    /** Coefficients for Chebyshev polynomials. */
    private static final ArrayList<Fraction> CHEBYSHEV_COEFFICIENTS;

    /** Coefficients for Hermite polynomials. */
    private static final ArrayList<Fraction> HERMITE_COEFFICIENTS;

    /** Coefficients for Laguerre polynomials. */
    private static final ArrayList<Fraction> LAGUERRE_COEFFICIENTS;

    /** Coefficients for Legendre polynomials. */
    private static final ArrayList<Fraction> LEGENDRE_COEFFICIENTS;

    static {

        // initialize recurrence for Chebyshev polynomials
        // T0(X) = 1, T1(X) = 0 + 1 * X
        CHEBYSHEV_COEFFICIENTS = new ArrayList<Fraction>();
        CHEBYSHEV_COEFFICIENTS.add(Fraction.ONE);
        CHEBYSHEV_COEFFICIENTS.add(Fraction.ZERO);
        CHEBYSHEV_COEFFICIENTS.add(Fraction.ONE);

        // initialize recurrence for Hermite polynomials
        // H0(X) = 1, H1(X) = 0 + 2 * X
        HERMITE_COEFFICIENTS = new ArrayList<Fraction>();
        HERMITE_COEFFICIENTS.add(Fraction.ONE);
        HERMITE_COEFFICIENTS.add(Fraction.ZERO);
        HERMITE_COEFFICIENTS.add(Fraction.TWO);

        // initialize recurrence for Laguerre polynomials
        // L0(X) = 1, L1(X) = 1 - 1 * X
        LAGUERRE_COEFFICIENTS = new ArrayList<Fraction>();
        LAGUERRE_COEFFICIENTS.add(Fraction.ONE);
        LAGUERRE_COEFFICIENTS.add(Fraction.ONE);
        LAGUERRE_COEFFICIENTS.add(Fraction.MINUS_ONE);

        // initialize recurrence for Legendre polynomials
        // P0(X) = 1, P1(X) = 0 + 1 * X
        LEGENDRE_COEFFICIENTS = new ArrayList<Fraction>();
        LEGENDRE_COEFFICIENTS.add(Fraction.ONE);
        LEGENDRE_COEFFICIENTS.add(Fraction.ZERO);
        LEGENDRE_COEFFICIENTS.add(Fraction.ONE);

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
            private final Fraction[] coeffs = { Fraction.ZERO, Fraction.TWO, Fraction.ONE};
            /** {@inheritDoc} */
            public Fraction[] generate(int k) {
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
            public Fraction[] generate(int k) {
                return new Fraction[] {
                        Fraction.ZERO,
                        Fraction.TWO,
                        new Fraction(2 * k, 1)};
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
            public Fraction[] generate(int k) {
                final int kP1 = k + 1;
                return new Fraction[] {
                        new Fraction(2 * k + 1, kP1),
                        new Fraction(-1, kP1),
                        new Fraction(k, kP1)};
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
            public Fraction[] generate(int k) {
                final int kP1 = k + 1;
                return new Fraction[] {
                        Fraction.ZERO,
                        new Fraction(k + kP1, kP1),
                        new Fraction(k, kP1)};
            }
        });
    }

    /** Get the coefficients array for a given degree.
     * @param degree degree of the polynomial
     * @param coefficients list where the computed coefficients are stored
     * @param generator recurrence coefficients generator
     * @return coefficients array
     */
    private static PolynomialFunction buildPolynomial(final int degree,
                                                      final ArrayList<Fraction> coefficients,
                                                      final RecurrenceCoefficientsGenerator generator) {

        final int maxDegree = (int) Math.floor(Math.sqrt(2 * coefficients.size())) - 1;
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
                                          final ArrayList<Fraction> coefficients) {

        int startK = (maxDegree - 1) * maxDegree / 2;
        for (int k = maxDegree; k < degree; ++k) {

            // start indices of two previous polynomials Pk(X) and Pk-1(X)
            int startKm1 = startK;
            startK += k;

            // Pk+1(X) = (a[0] + a[1] X) Pk(X) - a[2] Pk-1(X)
            Fraction[] ai = generator.generate(k);

            Fraction ck     = coefficients.get(startK);
            Fraction ckm1   = coefficients.get(startKm1);

            // degree 0 coefficient
            coefficients.add(ck.multiply(ai[0]).subtract(ckm1.multiply(ai[2])));

            // degree 1 to degree k-1 coefficients
            for (int i = 1; i < k; ++i) {
                final Fraction ckPrev = ck;
                ck     = coefficients.get(startK + i);
                ckm1   = coefficients.get(startKm1 + i);
                coefficients.add(ck.multiply(ai[0]).add(ckPrev.multiply(ai[1])).subtract(ckm1.multiply(ai[2])));
            }

            // degree k coefficient
            final Fraction ckPrev = ck;
            ck = coefficients.get(startK + k);
            coefficients.add(ck.multiply(ai[0]).add(ckPrev.multiply(ai[1])));

            // degree k+1 coefficient
            coefficients.add(ck.multiply(ai[1]));

        }

    }

    /** Interface for recurrence coefficients generation. */
    private static interface RecurrenceCoefficientsGenerator {
        /**
         * Generate recurrence coefficients.
         * @param k highest degree of the polynomials used in the recurrence
         * @return an array of three coefficients such that
         * P<sub>k+1</sub>(X) = (a[0] + a[1] X) P<sub>k</sub>(X) - a[2] P<sub>k-1</sub>(X)
         */
        Fraction[] generate(int k);
    }

}
