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
package org.apache.commons.math.analysis.interpolation;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;

/**
 * Implements the <a href="http://en.wikipedia.org/wiki/Local_regression">
 * Local Regression Algorithm</a> (also Loess, Lowess) for interpolation of
 * real univariate functions.
 * <p/>
 * For reference, see
 * <a href="http://www.math.tau.ac.il/~yekutiel/MA seminar/Cleveland 1979.pdf">
 * William S. Cleveland - Robust Locally Weighted Regression and Smoothing
 * Scatterplots</a>
 * <p/>
 * This class implements both the loess method and serves as an interpolation
 * adapter to it, allowing to build a spline on the obtained loess fit.
 *
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class LoessInterpolator
        implements UnivariateRealInterpolator, Serializable {

    /** Default value of the bandwidth parameter. */
    public static final double DEFAULT_BANDWIDTH = 0.3;

    /** Default value of the number of robustness iterations. */
    public static final int DEFAULT_ROBUSTNESS_ITERS = 2;

    /** Default value for accuracy. */
    public static final double DEFAULT_ACCURACY = 1e-12;

    /** serializable version identifier. */
    private static final long serialVersionUID = 5204927143605193821L;

    /**
     * The bandwidth parameter: when computing the loess fit at
     * a particular point, this fraction of source points closest
     * to the current point is taken into account for computing
     * a least-squares regression.
     * <p/>
     * A sensible value is usually 0.25 to 0.5.
     */
    private final double bandwidth;

    /**
     * The number of robustness iterations parameter: this many
     * robustness iterations are done.
     * <p/>
     * A sensible value is usually 0 (just the initial fit without any
     * robustness iterations) to 4.
     */
    private final int robustnessIters;

    /**
     * If the median residual at a certain robustness iteration
     * is less than this amount, no more iterations are done.
     */
    private final double accuracy;

    /**
     * Constructs a new {@link LoessInterpolator}
     * with a bandwidth of {@link #DEFAULT_BANDWIDTH},
     * {@link #DEFAULT_ROBUSTNESS_ITERS} robustness iterations
     * and an accuracy of {#link #DEFAULT_ACCURACY}.
     * See {@link #LoessInterpolator(double, int, double)} for an explanation of
     * the parameters.
     */
    public LoessInterpolator() {
        this.bandwidth = DEFAULT_BANDWIDTH;
        this.robustnessIters = DEFAULT_ROBUSTNESS_ITERS;
        this.accuracy = DEFAULT_ACCURACY;
    }

    /**
     * Constructs a new {@link LoessInterpolator}
     * with given bandwidth and number of robustness iterations.
     * <p>
     * Calling this constructor is equivalent to calling {link {@link
     * #LoessInterpolator(double, int, double) LoessInterpolator(bandwidth,
     * robustnessIters, LoessInterpolator.DEFAULT_ACCURACY)}
     * </p>
     *
     * @param bandwidth  when computing the loess fit at
     * a particular point, this fraction of source points closest
     * to the current point is taken into account for computing
     * a least-squares regression.</br>
     * A sensible value is usually 0.25 to 0.5, the default value is
     * {@link #DEFAULT_BANDWIDTH}.
     * @param robustnessIters This many robustness iterations are done.</br>
     * A sensible value is usually 0 (just the initial fit without any
     * robustness iterations) to 4, the default value is
     * {@link #DEFAULT_ROBUSTNESS_ITERS}.
     * @throws MathException if bandwidth does not lie in the interval [0,1]
     * or if robustnessIters is negative.
     * @see #LoessInterpolator(double, int, double)
     */
    public LoessInterpolator(double bandwidth, int robustnessIters) throws MathException {
        this(bandwidth, robustnessIters, DEFAULT_ACCURACY);
    }

    /**
     * Constructs a new {@link LoessInterpolator}
     * with given bandwidth, number of robustness iterations and accuracy.
     *
     * @param bandwidth  when computing the loess fit at
     * a particular point, this fraction of source points closest
     * to the current point is taken into account for computing
     * a least-squares regression.</br>
     * A sensible value is usually 0.25 to 0.5, the default value is
     * {@link #DEFAULT_BANDWIDTH}.
     * @param robustnessIters This many robustness iterations are done.</br>
     * A sensible value is usually 0 (just the initial fit without any
     * robustness iterations) to 4, the default value is
     * {@link #DEFAULT_ROBUSTNESS_ITERS}.
     * @param accuracy If the median residual at a certain robustness iteration
     * is less than this amount, no more iterations are done.
     * @throws MathException if bandwidth does not lie in the interval [0,1]
     * or if robustnessIters is negative.
     * @see #LoessInterpolator(double, int)
     * @since 2.1
     */
    public LoessInterpolator(double bandwidth, int robustnessIters, double accuracy) throws MathException {
        if (bandwidth < 0 || bandwidth > 1) {
            throw new MathException("bandwidth must be in the interval [0,1], but got {0}",
                                    bandwidth);
        }
        this.bandwidth = bandwidth;
        if (robustnessIters < 0) {
            throw new MathException("the number of robustness iterations must " +
                                    "be non-negative, but got {0}",
                                    robustnessIters);
        }
        this.robustnessIters = robustnessIters;
        this.accuracy = accuracy;
    }

    /**
     * Compute an interpolating function by performing a loess fit
     * on the data at the original abscissae and then building a cubic spline
     * with a
     * {@link org.apache.commons.math.analysis.interpolation.SplineInterpolator}
     * on the resulting fit.
     *
     * @param xval the arguments for the interpolation points
     * @param yval the values for the interpolation points
     * @return A cubic spline built upon a loess fit to the data at the original abscissae
     * @throws MathException  if some of the following conditions are false:
     * <ul>
     * <li> Arguments and values are of the same size that is greater than zero</li>
     * <li> The arguments are in a strictly increasing order</li>
     * <li> All arguments and values are finite real numbers</li>
     * </ul>
     */
    public final PolynomialSplineFunction interpolate(
            final double[] xval, final double[] yval) throws MathException {
        return new SplineInterpolator().interpolate(xval, smooth(xval, yval));
    }

    /**
     * Compute a weighted loess fit on the data at the original abscissae.
     *
     * @param xval the arguments for the interpolation points
     * @param yval the values for the interpolation points
     * @param weights point weights: coefficients by which the robustness weight of a point is multiplied
     * @return values of the loess fit at corresponding original abscissae
     * @throws MathException if some of the following conditions are false:
     * <ul>
     * <li> Arguments and values are of the same size that is greater than zero</li>
     * <li> The arguments are in a strictly increasing order</li>
     * <li> All arguments and values are finite real numbers</li>
     * </ul>
     * @since 2.1
     */
    public final double[] smooth(final double[] xval, final double[] yval, final double[] weights)
            throws MathException {
        if (xval.length != yval.length) {
            throw new MathException(
                    "Loess expects the abscissa and ordinate arrays " +
                    "to be of the same size, " +
                    "but got {0} abscisssae and {1} ordinatae",
                    xval.length, yval.length);
        }

        final int n = xval.length;

        if (n == 0) {
            throw new MathException("Loess expects at least 1 point");
        }

        checkAllFiniteReal(xval, "all abscissae must be finite real numbers, but {0}-th is {1}");
        checkAllFiniteReal(yval, "all ordinatae must be finite real numbers, but {0}-th is {1}");
        checkAllFiniteReal(weights, "all weights must be finite real numbers, but {0}-th is {1}");

        checkStrictlyIncreasing(xval);

        if (n == 1) {
            return new double[]{yval[0]};
        }

        if (n == 2) {
            return new double[]{yval[0], yval[1]};
        }

        int bandwidthInPoints = (int) (bandwidth * n);

        if (bandwidthInPoints < 2) {
            throw new MathException(
                    "the bandwidth must be large enough to " +
                    "accomodate at least 2 points. There are {0} " +
                    " data points, and bandwidth must be at least {1} " +
                    " but it is only {2}",
                    n, 2.0 / n, bandwidth);
        }

        final double[] res = new double[n];

        final double[] residuals = new double[n];
        final double[] sortedResiduals = new double[n];

        final double[] robustnessWeights = new double[n];

        // Do an initial fit and 'robustnessIters' robustness iterations.
        // This is equivalent to doing 'robustnessIters+1' robustness iterations
        // starting with all robustness weights set to 1.
        Arrays.fill(robustnessWeights, 1);

        for (int iter = 0; iter <= robustnessIters; ++iter) {
            final int[] bandwidthInterval = {0, bandwidthInPoints - 1};
            // At each x, compute a local weighted linear regression
            for (int i = 0; i < n; ++i) {
                final double x = xval[i];

                // Find out the interval of source points on which
                // a regression is to be made.
                if (i > 0) {
                    updateBandwidthInterval(xval, i, bandwidthInterval);
                }

                final int ileft = bandwidthInterval[0];
                final int iright = bandwidthInterval[1];

                // Compute the point of the bandwidth interval that is
                // farthest from x
                final int edge;
                if (xval[i] - xval[ileft] > xval[iright] - xval[i]) {
                    edge = ileft;
                } else {
                    edge = iright;
                }

                // Compute a least-squares linear fit weighted by
                // the product of robustness weights and the tricube
                // weight function.
                // See http://en.wikipedia.org/wiki/Linear_regression
                // (section "Univariate linear case")
                // and http://en.wikipedia.org/wiki/Weighted_least_squares
                // (section "Weighted least squares")
                double sumWeights = 0;
                double sumX = 0;
                double sumXSquared = 0;
                double sumY = 0;
                double sumXY = 0;
                double denom = Math.abs(1.0 / (xval[edge] - x));
                for (int k = ileft; k <= iright; ++k) {
                    final double xk   = xval[k];
                    final double yk   = yval[k];
                    final double dist = (k < i) ? x - xk : xk - x;
                    final double w    = tricube(dist * denom) * robustnessWeights[k] * weights[k];
                    final double xkw  = xk * w;
                    sumWeights += w;
                    sumX += xkw;
                    sumXSquared += xk * xkw;
                    sumY += yk * w;
                    sumXY += yk * xkw;
                }

                final double meanX = sumX / sumWeights;
                final double meanY = sumY / sumWeights;
                final double meanXY = sumXY / sumWeights;
                final double meanXSquared = sumXSquared / sumWeights;

                final double beta;
                if (Math.sqrt(Math.abs(meanXSquared - meanX * meanX)) < accuracy) {
                    beta = 0;
                } else {
                    beta = (meanXY - meanX * meanY) / (meanXSquared - meanX * meanX);
                }

                final double alpha = meanY - beta * meanX;

                res[i] = beta * x + alpha;
                residuals[i] = Math.abs(yval[i] - res[i]);
            }

            // No need to recompute the robustness weights at the last
            // iteration, they won't be needed anymore
            if (iter == robustnessIters) {
                break;
            }

            // Recompute the robustness weights.

            // Find the median residual.
            // An arraycopy and a sort are completely tractable here,
            // because the preceding loop is a lot more expensive
            System.arraycopy(residuals, 0, sortedResiduals, 0, n);
            Arrays.sort(sortedResiduals);
            final double medianResidual = sortedResiduals[n / 2];

            if (Math.abs(medianResidual) < accuracy) {
                break;
            }

            for (int i = 0; i < n; ++i) {
                final double arg = residuals[i] / (6 * medianResidual);
                if (arg >= 1) {
                    robustnessWeights[i] = 0;
                } else {
                    final double w = 1 - arg * arg;
                    robustnessWeights[i] = w * w;
                }
            }
        }

        return res;
    }

    /**
     * Compute a loess fit on the data at the original abscissae.
     *
     * @param xval the arguments for the interpolation points
     * @param yval the values for the interpolation points
     * @return values of the loess fit at corresponding original abscissae
     * @throws MathException if some of the following conditions are false:
     * <ul>
     * <li> Arguments and values are of the same size that is greater than zero</li>
     * <li> The arguments are in a strictly increasing order</li>
     * <li> All arguments and values are finite real numbers</li>
     * </ul>
     */
    public final double[] smooth(final double[] xval, final double[] yval)
            throws MathException {

        final double[] unitWeights = new double[xval.length];
        Arrays.fill(unitWeights, 1.0);

        return smooth(xval, yval, unitWeights);

    }


    /**
     * Given an index interval into xval that embraces a certain number of
     * points closest to xval[i-1], update the interval so that it embraces
     * the same number of points closest to xval[i]
     *
     * @param xval arguments array
     * @param i the index around which the new interval should be computed
     * @param bandwidthInterval a two-element array {left, right} such that: <p/>
     * <tt>(left==0 or xval[i] - xval[left-1] > xval[right] - xval[i])</tt>
     * <p/> and also <p/>
     * <tt>(right==xval.length-1 or xval[right+1] - xval[i] > xval[i] - xval[left])</tt>.
     * The array will be updated.
     */
    private static void updateBandwidthInterval(final double[] xval, final int i,
                                                final int[] bandwidthInterval) {
        final int left = bandwidthInterval[0];
        final int right = bandwidthInterval[1];

        // The right edge should be adjusted if the next point to the right
        // is closer to xval[i] than the leftmost point of the current interval
        if (right < xval.length - 1 &&
           xval[right+1] - xval[i] < xval[i] - xval[left]) {
            bandwidthInterval[0]++;
            bandwidthInterval[1]++;
        }
    }

    /**
     * Compute the
     * <a href="http://en.wikipedia.org/wiki/Local_regression#Weight_function">tricube</a>
     * weight function
     *
     * @param x the argument
     * @return (1-|x|^3)^3
     */
    private static double tricube(final double x) {
        final double tmp = 1 - x * x * x;
        return tmp * tmp * tmp;
    }

    /**
     * Check that all elements of an array are finite real numbers.
     *
     * @param values the values array
     * @param pattern pattern of the error message
     * @throws MathException if one of the values is not a finite real number
     */
    private static void checkAllFiniteReal(final double[] values, final String pattern)
        throws MathException {
        for (int i = 0; i < values.length; i++) {
            final double x = values[i];
            if (Double.isInfinite(x) || Double.isNaN(x)) {
                throw new MathException(pattern, i, x);
            }
        }
    }

    /**
     * Check that elements of the abscissae array are in a strictly
     * increasing order.
     *
     * @param xval the abscissae array
     * @throws MathException if the abscissae array
     * is not in a strictly increasing order
     */
    private static void checkStrictlyIncreasing(final double[] xval)
        throws MathException {
        for (int i = 0; i < xval.length; ++i) {
            if (i >= 1 && xval[i - 1] >= xval[i]) {
                throw new MathException(
                        "the abscissae array must be sorted in a strictly " +
                        "increasing order, but the {0}-th element is {1} " +
                        "whereas {2}-th is {3}",
                        i - 1, xval[i - 1], i, xval[i]);
            }
        }
    }
}
