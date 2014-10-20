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
package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.Precision;

/**
 * Computes a cubic spline interpolation for the data set using the Akima
 * algorithm, as originally formulated by Hiroshi Akima in his 1970 paper
 * "A New Method of Interpolation and Smooth Curve Fitting Based on Local Procedures."
 * J. ACM 17, 4 (October 1970), 589-602. DOI=10.1145/321607.321609
 * http://doi.acm.org/10.1145/321607.321609
 * <p>
 * This implementation is based on the Akima implementation in the CubicSpline
 * class in the Math.NET Numerics library. The method referenced is
 * CubicSpline.InterpolateAkimaSorted
 * <p>
 * The {@link #interpolate(double[], double[])} method returns a
 * {@link PolynomialSplineFunction} consisting of n cubic polynomials, defined
 * over the subintervals determined by the x values, x[0] < x[i] ... < x[n]. The
 * Akima algorithm requires that n >= 5.
 * </p>
 * <p>
 */

public class AkimaSplineInterpolator
    implements UnivariateInterpolator {


    /**
     * The minimum number of points that are needed to compute the function
     */
    public static final int MINIMUM_NUMBER_POINTS = 5;

    /**
     * Default constructor. Builds an AkimaSplineInterpolator object
     */
    public AkimaSplineInterpolator() {

    }

    /**
     * Computes an interpolating function for the data set.
     *
     * @param xvals the arguments for the interpolation points
     * @param yvals the values for the interpolation points
     * @return a function which interpolates the data set
     * @throws DimensionMismatchException if {@code x} and {@code y} have
     *         different sizes.
     * @throws NonMonotonicSequenceException if {@code x} is not sorted in
     *         strict increasing order.
     * @throws NumberIsTooSmallException if the size of {@code x} is smaller
     *         than 5.
     */
    public PolynomialSplineFunction interpolate(double[] xvals, double[] yvals)
        throws DimensionMismatchException, NumberIsTooSmallException,
        NonMonotonicSequenceException {
        if (xvals == null || yvals == null) {
            throw new NullArgumentException();
        }

        if (xvals.length != yvals.length) {
            throw new DimensionMismatchException(xvals.length, yvals.length);
        }

        if (xvals.length < MINIMUM_NUMBER_POINTS) {
            throw new NumberIsTooSmallException(
                                                LocalizedFormats.NUMBER_OF_POINTS,
                                                xvals.length,
                                                MINIMUM_NUMBER_POINTS, true);
        }

        MathArrays.checkOrder(xvals);

        final int numberOfDiffAndWeightElements = xvals.length - 1;

        double differences[] = new double[numberOfDiffAndWeightElements];
        double weights[] = new double[numberOfDiffAndWeightElements];

        for (int i = 0; i < differences.length; i++) {
            differences[i] = (yvals[i + 1] - yvals[i]) /
                             (xvals[i + 1] - xvals[i]);
        }

        for (int i = 1; i < weights.length; i++) {
            weights[i] = FastMath.abs(differences[i] - differences[i - 1]);
        }

        /* Prepare Hermite interpolation scheme */

        double firstDerivatives[] = new double[xvals.length];

        for (int i = 2; i < firstDerivatives.length - 2; i++) {
            if (Precision.equals(weights[i - 1], 0.0) &&
                Precision.equals(weights[i + 1], 0.0)) {
                firstDerivatives[i] = (((xvals[i + 1] - xvals[i]) * differences[i - 1]) + ((xvals[i] - xvals[i - 1]) * differences[i])) /
                                      (xvals[i + 1] - xvals[i - 1]);
            } else {
                firstDerivatives[i] = ((weights[i + 1] * differences[i - 1]) + (weights[i - 1] * differences[i])) /
                                      (weights[i + 1] + weights[i - 1]);
            }
        }

        firstDerivatives[0] = this.differentiateThreePoint(xvals, yvals, 0, 0,
                                                           1, 2);
        firstDerivatives[1] = this.differentiateThreePoint(xvals, yvals, 1, 0,
                                                           1, 2);
        firstDerivatives[xvals.length - 2] = this
            .differentiateThreePoint(xvals, yvals, xvals.length - 2,
                                     xvals.length - 3, xvals.length - 2,
                                     xvals.length - 1);
        firstDerivatives[xvals.length - 1] = this
            .differentiateThreePoint(xvals, yvals, xvals.length - 1,
                                     xvals.length - 3, xvals.length - 2,
                                     xvals.length - 1);

        return this.interpolateHermiteSorted(xvals, yvals, firstDerivatives);
    }

    /**
     * Three point differentiation helper, modeled off of the same method in the
     * Math.NET CubicSpline class. This is used by both the Apache Math and the
     * Math.NET Akima Cubic Spline algorithms
     *
     * @param xvals x values to calculate the numerical derivative with
     * @param yvals y values to calculate the numerical derivative with
     * @param indexOfDifferentiation index of the elemnt we are calculating the derivative around
     * @param indexOfFirstSample index of the first element to sample for the three point method
     * @param indexOfSecondsample index of the second element to sample for the three point method
     * @param indexOfThirdSample index of the third element to sample for the three point method
     * @return the derivative
     */
    private double differentiateThreePoint(double[] xvals, double[] yvals,
                                           int indexOfDifferentiation,
                                           int indexOfFirstSample,
                                           int indexOfSecondsample,
                                           int indexOfThirdSample) {
        double x0 = yvals[indexOfFirstSample];
        double x1 = yvals[indexOfSecondsample];
        double x2 = yvals[indexOfThirdSample];

        double t = xvals[indexOfDifferentiation] - xvals[indexOfFirstSample];
        double t1 = xvals[indexOfSecondsample] - xvals[indexOfFirstSample];
        double t2 = xvals[indexOfThirdSample] - xvals[indexOfFirstSample];

        double a = (x2 - x0 - (t2 / t1 * (x1 - x0))) / (t2 * t2 - t1 * t2);
        double b = (x1 - x0 - a * t1 * t1) / t1;
        return (2 * a * t) + b;
    }

    /**
     * Creates a Hermite cubic spline interpolation from the set of (x,y) value
     * pairs and their derivatives. This is modeled off of the
     * InterpolateHermiteSorted method in the Math.NET CubicSpline class.
     *
     * @param xvals x values for interpolation
     * @param yvals y values for interpolation
     * @param firstDerivatives first derivative values of the function
     * @return polynomial that fits the function
     */
    private PolynomialSplineFunction interpolateHermiteSorted(double[] xvals,
                                                              double[] yvals,
                                                              double[] firstDerivatives) {
        if (xvals.length != yvals.length) {
            throw new DimensionMismatchException(xvals.length, yvals.length);
        }

        if (xvals.length != firstDerivatives.length) {
            throw new DimensionMismatchException(xvals.length,
                                                 firstDerivatives.length);
        }

        final int minimumLength = 2;
        if (xvals.length < minimumLength) {
            throw new NumberIsTooSmallException(
                                                LocalizedFormats.NUMBER_OF_POINTS,
                                                xvals.length, minimumLength,
                                                true);
        }

        int size = xvals.length - 1;
        final PolynomialFunction polynomials[] = new PolynomialFunction[size];
        final double coefficients[] = new double[4];

        for (int i = 0; i < polynomials.length; i++) {
            double w = xvals[i + 1] - xvals[i];
            double w2 = w * w;
            coefficients[0] = yvals[i];
            coefficients[1] = firstDerivatives[i];
            coefficients[2] = (3 * (yvals[i + 1] - yvals[i]) / w - 2 *
                               firstDerivatives[i] - firstDerivatives[i + 1]) /
                              w;
            coefficients[3] = (2 * (yvals[i] - yvals[i + 1]) / w +
                               firstDerivatives[i] + firstDerivatives[i + 1]) /
                              w2;
            polynomials[i] = new PolynomialFunction(coefficients);
        }

        return new PolynomialSplineFunction(xvals, polynomials);

    }
}
