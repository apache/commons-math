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
package org.apache.commons.math4.legacy.analysis.interpolation;

import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;

/**
 * Clamped cubic spline interpolator.
 * The interpolating function consists in cubic polynomial functions defined over the
 * subintervals determined by the "knot points".
 *
 * The interpolating polynomials satisfy:
 * <ol>
 *  <li>The value of the interpolating function at each of the input {@code x} values
 *   equals the corresponding input {@code y} value.</li>
 *  <li>Adjacent polynomials are equal through two derivatives at the knot points
 *   (i.e., adjacent polynomials "match up" at the knot points, as do their first and
 *   second derivatives).</li>
 *  <li>The clamped boundary condition, i.e. the interpolating function takes "a
 *   specific direction" at both its start point and its end point by providing the
 *   desired first derivative values (slopes) as function parameters to
 *   {@link #interpolate(double[], double[], double, double)}.</li>
 * </ol>
 *
 * The algorithm is implemented as described in
 * <blockquote>
 *  R.L. Burden, J.D. Faires,
 *  <em>Numerical Analysis</em>, 9th Ed., 2010, Cengage Learning, ISBN 0-538-73351-9, pp 153-156.
 * </blockquote>
 *
 */
public class ClampedSplineInterpolator implements UnivariateInterpolator {
    /**
     *
     * The first derivatives evaluated at the first and last knot points are
     * approximated from a natural/unclamped spline that passes through the same
     * set of points.
     *
     * @param x Arguments for the interpolation points.
     * @param y Values for the interpolation points.
     * @return the interpolating function.
     * @throws DimensionMismatchException if {@code x} and {@code y} have different sizes.
     * @throws NumberIsTooSmallException if the size of {@code x < 3}.
     * @throws org.apache.commons.math4.legacy.exception.NonMonotonicSequenceException if {@code x} is not sorted in strict increasing order.
     */
    @Override
    public PolynomialSplineFunction interpolate(final double[] x,
                                                final double[] y) {
        final SplineInterpolator spliner = new SplineInterpolator();
        final PolynomialSplineFunction spline = spliner.interpolate(x, y);
        final PolynomialSplineFunction derivativeSpline = spline.polynomialSplineDerivative();
        final double fpStart = derivativeSpline.value(x[0]);
        final double fpEnd = derivativeSpline.value(x[x.length - 1]);

        return this.interpolate(x, y, fpStart, fpEnd);
    }

    /**
     * Computes an interpolating function for the data set with defined
     * boundary conditions.
     *
     * @param x Arguments for the interpolation points.
     * @param y Values for the interpolation points.
     * @param fpStart First derivative at the starting point of the returned
     * spline function (starting slope).
     * @param fpEnd First derivative at the ending point of the returned
     * spline function (ending slope).
     * @return the interpolating function.
     * @throws DimensionMismatchException if {@code x} and {@code y} have different sizes.
     * @throws NumberIsTooSmallException if the size of {@code x < 3}.
     * @throws org.apache.commons.math4.legacy.exception.NonMonotonicSequenceException if {@code x} is not sorted in strict increasing order.
     */
    public PolynomialSplineFunction interpolate(final double[] x,
                                                final double[] y,
                                                final double fpStart,
                                                final double fpEnd) {
        if (x.length != y.length) {
            throw new DimensionMismatchException(x.length, y.length);
        }

        if (x.length < 3) {
            throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_OF_POINTS,
                                                x.length, 3, true);
        }

        // Number of intervals.  The number of data points is n + 1.
        final int n = x.length - 1;

        MathArrays.checkOrder(x);

        // Differences between knot points.
        final double[] h = new double[n];
        for (int i = 0; i < n; i++) {
            h[i] = x[i + 1] - x[i];
        }

        final double[] mu = new double[n];
        final double[] z = new double[n + 1];

        final double alpha0 = 3 * ((y[1] - y[0]) / h[0] - fpStart);
        final double alphaN = 3 * (fpEnd - (y[n] - y[n - 1]) / h[n - 1]);

        mu[0] = 0.5;
        final double ell0 = 2 * h[0];
        z[0] = alpha0 / ell0;

        for (int i = 1; i < n; i++) {
            final double alpha = 3 * ((y[i + 1] - y[i]) / h[i] - (y[i] - y[i - 1]) / h[i - 1]);
            final double ell = 2 * (x[i + 1] - x[i - 1]) - h[i - 1] * mu[i - 1];
            mu[i] = h[i] / ell;
            z[i] = (alpha - h[i - 1] * z[i - 1]) / ell;
        }

        // Cubic spline coefficients.
        final double[] b = new double[n]; // Linear.
        final double[] c = new double[n + 1]; // Quadratic.
        final double[] d = new double[n]; // Cubic.

        final double ellN = h[n - 1] * (2 - mu[n - 1]);
        z[n] = (alphaN - h[n - 1] * z[n - 1]) / ellN;
        c[n] = z[n];

        for (int j = n - 1; j >= 0; j--) {
            c[j] = z[j] - mu[j] * c[j + 1];
            b[j] = ((y[j + 1] - y[j]) / h[j]) - h[j] * (c[j + 1] + 2 * c[j]) / 3;
            d[j] = (c[j + 1] - c[j]) / (3 * h[j]);
        }

        final PolynomialFunction[] polynomials = new PolynomialFunction[n];
        final double[] coefficients = new double[4];
        for (int i = 0; i < n; i++) {
            coefficients[0] = y[i];
            coefficients[1] = b[i];
            coefficients[2] = c[i];
            coefficients[3] = d[i];
            polynomials[i] = new PolynomialFunction(coefficients);
        }

        return new PolynomialSplineFunction(x, polynomials);
    }
}
