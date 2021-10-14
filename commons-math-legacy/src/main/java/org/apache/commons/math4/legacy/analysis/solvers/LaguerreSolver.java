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
package org.apache.commons.math4.legacy.analysis.solvers;

import org.apache.commons.numbers.complex.Complex;
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math4.legacy.exception.NoBracketingException;
import org.apache.commons.math4.legacy.exception.NoDataException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.TooManyEvaluationsException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * Implements the <a href="http://mathworld.wolfram.com/LaguerresMethod.html">
 * Laguerre's Method</a> for root finding of real coefficient polynomials.
 * For reference, see
 * <blockquote>
 *  <b>A First Course in Numerical Analysis</b>,
 *  ISBN 048641454X, chapter 8.
 * </blockquote>
 * Laguerre's method is global in the sense that it can start with any initial
 * approximation and be able to solve all roots from that point.
 * The algorithm requires a bracketing condition.
 *
 * @since 1.2
 */
public class LaguerreSolver extends AbstractPolynomialSolver {
    /** Default absolute accuracy. */
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1e-6;
    /** Complex solver. */
    private final ComplexSolver complexSolver = new ComplexSolver();

    /**
     * Construct a solver with default accuracy (1e-6).
     */
    public LaguerreSolver() {
        this(DEFAULT_ABSOLUTE_ACCURACY);
    }
    /**
     * Construct a solver.
     *
     * @param absoluteAccuracy Absolute accuracy.
     */
    public LaguerreSolver(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     */
    public LaguerreSolver(double relativeAccuracy,
                          double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }
    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     * @param functionValueAccuracy Function value accuracy.
     */
    public LaguerreSolver(double relativeAccuracy,
                          double absoluteAccuracy,
                          double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double doSolve()
        throws TooManyEvaluationsException,
               NumberIsTooLargeException,
               NoBracketingException {
        final double min = getMin();
        final double max = getMax();
        final double initial = getStartValue();
        final double functionValueAccuracy = getFunctionValueAccuracy();

        verifySequence(min, initial, max);

        // Return the initial guess if it is good enough.
        final double yInitial = computeObjectiveValue(initial);
        if (JdkMath.abs(yInitial) <= functionValueAccuracy) {
            return initial;
        }

        // Return the first endpoint if it is good enough.
        final double yMin = computeObjectiveValue(min);
        if (JdkMath.abs(yMin) <= functionValueAccuracy) {
            return min;
        }

        // Reduce interval if min and initial bracket the root.
        if (yInitial * yMin < 0) {
            return laguerre(min, initial);
        }

        // Return the second endpoint if it is good enough.
        final double yMax = computeObjectiveValue(max);
        if (JdkMath.abs(yMax) <= functionValueAccuracy) {
            return max;
        }

        // Reduce interval if initial and max bracket the root.
        if (yInitial * yMax < 0) {
            return laguerre(initial, max);
        }

        throw new NoBracketingException(min, max, yMin, yMax);
    }

    /**
     * Find a real root in the given interval.
     *
     * Despite the bracketing condition, the root returned by
     * {@link LaguerreSolver.ComplexSolver#solve(Complex[],Complex)} may
     * not be a real zero inside {@code [min, max]}.
     * For example, <code> p(x) = x<sup>3</sup> + 1, </code>
     * with {@code min = -2}, {@code max = 2}, {@code initial = 0}.
     * When it occurs, this code calls
     * {@link LaguerreSolver.ComplexSolver#solveAll(Complex[],Complex)}
     * in order to obtain all roots and picks up one real root.
     *
     * @param lo Lower bound of the search interval.
     * @param hi Higher bound of the search interval.
     * @return the point at which the function value is zero.
     */
    private double laguerre(double lo, double hi) {
        final Complex c[] = real2Complex(getCoefficients());

        final Complex initial = Complex.ofCartesian(0.5 * (lo + hi), 0);
        final Complex z = complexSolver.solve(c, initial);
        if (complexSolver.isRoot(lo, hi, z)) {
            return z.getReal();
        } else {
            double r = Double.NaN;
            // Solve all roots and select the one we are seeking.
            Complex[] root = complexSolver.solveAll(c, initial);
            for (int i = 0; i < root.length; i++) {
                if (complexSolver.isRoot(lo, hi, root[i])) {
                    r = root[i].getReal();
                    break;
                }
            }
            return r;
        }
    }

    /**
     * Find all complex roots for the polynomial with the given
     * coefficients, starting from the given initial value.
     * <p>
     * Note: This method is not part of the API of {@link BaseUnivariateSolver}.</p>
     *
     * @param coefficients Polynomial coefficients.
     * @param initial Start value.
     * @return the point at which the function value is zero.
     * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException
     * if the maximum number of evaluations is exceeded.
     * @throws NullArgumentException if the {@code coefficients} is
     * {@code null}.
     * @throws NoDataException if the {@code coefficients} array is empty.
     * @since 3.1
     */
    public Complex[] solveAllComplex(double[] coefficients,
                                     double initial)
        throws NullArgumentException,
               NoDataException,
               TooManyEvaluationsException {
        setup(Integer.MAX_VALUE,
              new PolynomialFunction(coefficients),
              Double.NEGATIVE_INFINITY,
              Double.POSITIVE_INFINITY,
              initial);
        return complexSolver.solveAll(real2Complex(coefficients),
                                      Complex.ofCartesian(initial, 0d));
    }

    /**
     * Find a complex root for the polynomial with the given coefficients,
     * starting from the given initial value.
     * <p>
     * Note: This method is not part of the API of {@link BaseUnivariateSolver}.</p>
     *
     * @param coefficients Polynomial coefficients.
     * @param initial Start value.
     * @return the point at which the function value is zero.
     * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException
     * if the maximum number of evaluations is exceeded.
     * @throws NullArgumentException if the {@code coefficients} is
     * {@code null}.
     * @throws NoDataException if the {@code coefficients} array is empty.
     * @since 3.1
     */
    public Complex solveComplex(double[] coefficients,
                                double initial)
        throws NullArgumentException,
               NoDataException,
               TooManyEvaluationsException {
        setup(Integer.MAX_VALUE,
              new PolynomialFunction(coefficients),
              Double.NEGATIVE_INFINITY,
              Double.POSITIVE_INFINITY,
              initial);
        return complexSolver.solve(real2Complex(coefficients),
                                   Complex.ofCartesian(initial, 0d));
    }

    /**
     * Class for searching all (complex) roots.
     */
    private class ComplexSolver {
        /**
         * Check whether the given complex root is actually a real zero
         * in the given interval, within the solver tolerance level.
         *
         * @param min Lower bound for the interval.
         * @param max Upper bound for the interval.
         * @param z Complex root.
         * @return {@code true} if z is a real zero.
         */
        public boolean isRoot(double min, double max, Complex z) {
            if (isSequence(min, z.getReal(), max)) {
                double tolerance = JdkMath.max(getRelativeAccuracy() * z.abs(), getAbsoluteAccuracy());
                return (JdkMath.abs(z.getImaginary()) <= tolerance) ||
                     (z.abs() <= getFunctionValueAccuracy());
            }
            return false;
        }

        /**
         * Find all complex roots for the polynomial with the given
         * coefficients, starting from the given initial value.
         *
         * @param coefficients Polynomial coefficients.
         * @param initial Start value.
         * @return the point at which the function value is zero.
         * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException
         * if the maximum number of evaluations is exceeded.
         * @throws NullArgumentException if the {@code coefficients} is
         * {@code null}.
         * @throws NoDataException if the {@code coefficients} array is empty.
         */
        public Complex[] solveAll(Complex coefficients[], Complex initial)
            throws NullArgumentException,
                   NoDataException,
                   TooManyEvaluationsException {
            if (coefficients == null) {
                throw new NullArgumentException();
            }
            final int n = coefficients.length - 1;
            if (n == 0) {
                throw new NoDataException(LocalizedFormats.POLYNOMIAL);
            }
            // Coefficients for deflated polynomial.
            final Complex c[] = new Complex[n + 1];
            for (int i = 0; i <= n; i++) {
                c[i] = coefficients[i];
            }

            // Solve individual roots successively.
            final Complex root[] = new Complex[n];
            for (int i = 0; i < n; i++) {
                final Complex subarray[] = new Complex[n - i + 1];
                System.arraycopy(c, 0, subarray, 0, subarray.length);
                root[i] = solve(subarray, initial);
                // Polynomial deflation using synthetic division.
                Complex newc = c[n - i];
                Complex oldc = null;
                for (int j = n - i - 1; j >= 0; j--) {
                    oldc = c[j];
                    c[j] = newc;
                    newc = oldc.add(newc.multiply(root[i]));
                }
            }

            return root;
        }

        /**
         * Find a complex root for the polynomial with the given coefficients,
         * starting from the given initial value.
         *
         * @param coefficients Polynomial coefficients.
         * @param initial Start value.
         * @return the point at which the function value is zero.
         * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException
         * if the maximum number of evaluations is exceeded.
         * @throws NullArgumentException if the {@code coefficients} is
         * {@code null}.
         * @throws NoDataException if the {@code coefficients} array is empty.
         */
        public Complex solve(Complex coefficients[], Complex initial)
            throws NullArgumentException,
                   NoDataException,
                   TooManyEvaluationsException {
            if (coefficients == null) {
                throw new NullArgumentException();
            }

            final int n = coefficients.length - 1;
            if (n == 0) {
                throw new NoDataException(LocalizedFormats.POLYNOMIAL);
            }

            final double absoluteAccuracy = getAbsoluteAccuracy();
            final double relativeAccuracy = getRelativeAccuracy();
            final double functionValueAccuracy = getFunctionValueAccuracy();

            final Complex nC  = Complex.ofCartesian(n, 0);
            final Complex n1C = Complex.ofCartesian(n - 1, 0);

            Complex z = initial;
            Complex oldz = Complex.ofCartesian(Double.POSITIVE_INFINITY,
                                       Double.POSITIVE_INFINITY);
            while (true) {
                // Compute pv (polynomial value), dv (derivative value), and
                // d2v (second derivative value) simultaneously.
                Complex pv = coefficients[n];
                Complex dv = Complex.ZERO;
                Complex d2v = Complex.ZERO;
                for (int j = n-1; j >= 0; j--) {
                    d2v = dv.add(z.multiply(d2v));
                    dv = pv.add(z.multiply(dv));
                    pv = coefficients[j].add(z.multiply(pv));
                }
                d2v = d2v.multiply(Complex.ofCartesian(2.0, 0.0));

                // Check for convergence.
                final double tolerance = JdkMath.max(relativeAccuracy * z.abs(),
                                                      absoluteAccuracy);
                if ((z.subtract(oldz)).abs() <= tolerance) {
                    return z;
                }
                if (pv.abs() <= functionValueAccuracy) {
                    return z;
                }

                // Now pv != 0, calculate the new approximation.
                final Complex g = dv.divide(pv);
                final Complex g2 = g.multiply(g);
                final Complex h = g2.subtract(d2v.divide(pv));
                final Complex delta = n1C.multiply((nC.multiply(h)).subtract(g2));
                // Choose a denominator larger in magnitude.
                final Complex deltaSqrt = delta.sqrt();
                final Complex dplus = g.add(deltaSqrt);
                final Complex dminus = g.subtract(deltaSqrt);
                final Complex denominator = dplus.abs() > dminus.abs() ? dplus : dminus;
                // Perturb z if denominator is zero, for instance,
                // p(x) = x^3 + 1, z = 0.
                if (denominator.equals(Complex.ofCartesian(0.0, 0.0))) {
                    z = z.add(Complex.ofCartesian(absoluteAccuracy, absoluteAccuracy));
                    oldz = Complex.ofCartesian(Double.POSITIVE_INFINITY,
                                       Double.POSITIVE_INFINITY);
                } else {
                    oldz = z;
                    z = z.subtract(nC.divide(denominator));
                }
                incrementEvaluationCount();
            }
        }
    }

    /**
     * Converts a {@code double[]} array to a {@code Complex[]} array.
     *
     * @param real array of numbers to be converted to their {@code Complex} equivalent
     * @return {@code Complex} array
     */
    private static Complex[] real2Complex(double[] real) {
        int index = 0;
        final Complex[] c = new Complex[real.length];
        for (final double d : real) {
            c[index] = Complex.ofCartesian(d, 0);
            index++;
        }
        return c;
    }
}
