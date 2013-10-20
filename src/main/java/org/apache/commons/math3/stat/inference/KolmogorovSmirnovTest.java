/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.apache.commons.math3.stat.inference;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.math3.distribution.KolmogorovSmirnovDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.BigFractionField;
import org.apache.commons.math3.fraction.FractionConversionException;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

/**
 * Implementation of the <a
 * href="http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test">
 * Kolmogorov-Smirnov (K-S) test</a> for equality of continuous distributions.
 * <p>
 * The K-S test uses a statistic based on the maximum deviation of the empirical
 * distribution of sample data points from the distribution expected under the
 * null hypothesis. Specifically, what is computed is \(D_n=\sup_x
 * |F_n(x)-F(x)|\), where \(F\) is the expected distribution and \(F_n\) is the
 * empirical distribution of the \(n\) sample data points. The distribution of
 * \(D_n\) is estimated using a method based on [1] with certain quick decisions
 * for extreme values given in [2].
 * </p>
 * <p>
 * References:
 * <ul>
 * <li>[1] <a href="http://www.jstatsoft.org/v08/i18/"> Evaluating Kolmogorov's
 * Distribution</a> by George Marsaglia, Wai Wan Tsang, and Jingbo Wang</li>
 * <li>[2] <a href="http://www.jstatsoft.org/v39/i11/"> Computing the Two-Sided
 * Kolmogorov-Smirnov Distribution</a> by Richard Simard and Pierre L'Ecuyer</li>
 * </ul>
 * Note that [1] contains an error in computing h, refer to <a
 * href="https://issues.apache.org/jira/browse/MATH-437">MATH-437</a> for
 * details.
 * </p>
 *
 * @since 3.3
 * @version $Id$
 */
public class KolmogorovSmirnovTest {

    /**
     * Bound on the number of partial sums in
     * {@link #ksSum(double, double, long)}
     */
    private static final long MAXIMUM_PARTIAL_SUM_COUNT = 100000;

    /** Convergence criterion for {@link #ksSum(double, double, long)} */
    private static final double KS_SUM_CAUCHY_CRITERION = 1e-15;

    /** Cutoff for default 2-sample test to use K-S distribution approximation */
    private static final long SMALL_SAMPLE_PRODUCT = 10000;

    /**
     * Computes the <i>p-value</i>, or <i>observed significance level</i>, of a
     * one-sample <a
     * href="http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test">
     * Kolmogorov-Smirnov test</a> evaluating the null hypothesis that
     * {@code data} conforms to {@code distribution}. If {@code exact} is true,
     * the distribution used to compute the p-value is computed using extended
     * precision. See {@link #cdfExact(double, int)}.
     *
     * @param distribution reference distribution
     * @param data sample being being evaluated
     * @param exact whether or not to force exact computation of the p-value
     * @return the p-value associated with the null hypothesis that {@code data}
     *         is a sample from {@code distribution}
     */
    public double kolmogorovSmirnovTest(RealDistribution distribution, double[] data, boolean exact) {
        return 1d - cdf(kolmogorovSmirnovStatistic(distribution, data), data.length, exact);
    }

    /**
     * Computes the one-sample Kolmogorov-Smirnov test statistic, \(D_n=\sup_x
     * |F_n(x)-F(x)|\) where \(F\) is the distribution (cdf) function associated
     * with {@code distribution}, \(n\) is the length of {@code data} and
     * \(F_n\) is the empirical distribution that puts mass \(1/n\) at each of
     * the values in {@code data}.
     *
     * @param distribution reference distribution
     * @param data sample being evaluated
     * @return Kolmogorov-Smirnov statistic \(D_n\)
     * @throws MathIllegalArgumentException if {@code data} does not have length
     *         at least 2
     */
    public double kolmogorovSmirnovStatistic(RealDistribution distribution, double[] data) {
        final int n = data.length;
        final double nd = n;
        final double[] dataCopy = new double[n];
        System.arraycopy(data, 0, dataCopy, 0, n);
        Arrays.sort(dataCopy);
        double d = 0d;
        for (int i = 1; i <= n; i++) {
            final double yi = distribution.cumulativeProbability(dataCopy[i - 1]);
            final double currD = FastMath.max(yi - (i - 1) / nd, i / nd - yi);
            if (currD > d) {
                d = currD;
            }
        }
        return d;
    }

    /**
     * Computes the <i>p-value</i>, or <i>observed significance level</i>, of a
     * two-sample <a
     * href="http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test">
     * Kolmogorov-Smirnov test</a> evaluating the null hypothesis that {@code x}
     * and {@code y} are samples drawn from the same probability distribution.
     * If {@code exact} is true, the discrete distribution of the test statistic
     * is computed and used directly; otherwise the asymptotic
     * (Kolmogorov-Smirnov) distribution is used to estimate the p-value.
     *
     * @param x first sample dataset
     * @param y second sample dataset
     * @param exact whether or not the exact distribution of the \(D\( statistic
     *        is used
     * @return p-value associated with the null hypothesis that {@code x} and
     *         {@code y} represent samples from the same distribution
     */
    public double kolmogorovSmirnovTest(double[] x, double[] y, boolean exact) {
        if (exact) {
            return exactP(kolmogorovSmirnovStatistic(x, y), x.length, y.length, false);
        } else {
            return approximateP(kolmogorovSmirnovStatistic(x, y), x.length, y.length);
        }
    }

    /**
     * Computes the <i>p-value</i>, or <i>observed significance level</i>, of a
     * two-sample <a
     * href="http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test">
     * Kolmogorov-Smirnov test</a> evaluating the null hypothesis that {@code x}
     * and {@code y} are samples drawn from the same probability distribution.
     * If the product of the lengths of x and y is less than 10,000, the
     * discrete distribution of the test statistic is computed and used
     * directly; otherwise the asymptotic (Kolmogorov-Smirnov) distribution is
     * used to estimate the p-value.
     *
     * @param x first sample dataset
     * @param y second sample dataset
     * @return p-value associated with the null hypothesis that {@code x} and
     *         {@code y} represent samples from the same distribution
     */
    public double kolmogorovSmirnovTest(double[] x, double[] y) {
        if (x.length * y.length < SMALL_SAMPLE_PRODUCT) {
            return kolmogorovSmirnovTest(x, y, true);
        } else {
            return kolmogorovSmirnovTest(x, y, false);
        }
    }

    /**
     * Computes the two-sample Kolmogorov-Smirnov test statistic, \(D_n,m=\sup_x
     * |F_n(x)-F_m(x)|\) \(n\) is the length of {@code x}, \(m\) is the length
     * of {@code y}, \(F_n\) is the empirical distribution that puts mass
     * \(1/n\) at each of the values in {@code x} and \(F_m\) is the empirical
     * distribution of the {@code y} values.
     *
     * @param x first sample
     * @param y second sample
     * @return test statistic \(D_n,m\) used to evaluate the null hypothesis
     *         that {@code x} and {@code y} represent samples from the same
     *         underlying distribution
     * @throws MathIllegalArgumentException if either {@code x} or {@code y}
     *         does not have length at least 2.
     */
    public double kolmogorovSmirnovStatistic(double[] x, double[] y) {
        checkArray(x);
        checkArray(y);
        // Copy and sort the sample arrays
        final double[] sx = MathArrays.copyOf(x);
        final double[] sy = MathArrays.copyOf(y);
        Arrays.sort(sx);
        Arrays.sort(sy);
        final int n = sx.length;
        final int m = sy.length;

        // Compare empirical distribution cdf values at each (combined) sample
        // point.
        // D_n.m is the max difference.
        // cdf value is (insertion point - 1) / length if not an element;
        // index / n if element is in the array.
        double supD = 0d;
        // First walk x points
        for (int i = 0; i < n; i++) {
            final double cdf_x = (i + 1d) / n;
            final int yIndex = Arrays.binarySearch(sy, sx[i]);
            final double cdf_y = yIndex >= 0 ? (yIndex + 1d) / m : (-yIndex - 1d) / m;
            final double curD = FastMath.abs(cdf_x - cdf_y);
            if (curD > supD) {
                supD = curD;
            }
        }
        // Now look at y
        for (int i = 0; i < m; i++) {
            final double cdf_y = (i + 1d) / m;
            final int xIndex = Arrays.binarySearch(sx, sy[i]);
            final double cdf_x = xIndex >= 0 ? (xIndex + 1d) / n : (-xIndex - 1d) / n;
            final double curD = FastMath.abs(cdf_x - cdf_y);
            if (curD > supD) {
                supD = curD;
            }
        }
        return supD;
    }

    /**
     * Computes the <i>p-value</i>, or <i>observed significance level</i>, of a
     * one-sample <a
     * href="http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test">
     * Kolmogorov-Smirnov test</a> evaluating the null hypothesis that
     * {@code data} conforms to {@code distribution}.
     *
     * @param distribution reference distribution
     * @param data sample being being evaluated
     * @return the p-value associated with the null hypothesis that {@code data}
     *         is a sample from {@code distribution}
     */
    public double kolmogorovSmirnovTest(RealDistribution distribution, double[] data) {
        return kolmogorovSmirnovTest(distribution, data, false);
    }

    /**
     * Performs a <a
     * href="http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test">
     * Kolmogorov-Smirnov test</a> evaluating the null hypothesis that
     * {@code data} conforms to {@code distribution}.
     *
     * @param distribution reference distribution
     * @param data sample being being evaluated
     * @param alpha significance level of the test
     * @return true iff the null hypothesis that {@code data} is a sample from
     *         {@code distribution} can be rejected with confidence 1 -
     *         {@code alpha}
     */
    public boolean kolmogorovSmirnovTest(RealDistribution distribution, double[] data, double alpha) {
        if ((alpha <= 0) || (alpha > 0.5)) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, alpha, 0, 0.5);
        }
        return kolmogorovSmirnovTest(distribution, data) < alpha;
    }

    /**
     * Calculates {@code P(D_n < d)} using method described in [1] with quick
     * decisions for extreme values given in [2] (see above). The result is not
     * exact as with {@link KolmogorovSmirnovDistribution#cdfExact(double)}
     * because calculations are based on {@code double} rather than
     * {@link org.apache.commons.math3.fraction.BigFraction}.
     *
     * @param d statistic
     * @return the two-sided probability of {@code P(D_n < d)}
     * @throws MathArithmeticException if algorithm fails to convert {@code h}
     *         to a {@link org.apache.commons.math3.fraction.BigFraction} in
     *         expressing {@code d} as {@code (k - h) / m} for integer
     *         {@code k, m} and {@code 0 <= h < 1}.
     */
    public double cdf(double d, int n)
        throws MathArithmeticException {
        return cdf(d, n, false);
    }

    /**
     * Calculates {@code P(D_n < d)}. The result is exact in the sense that
     * BigFraction/BigReal is used everywhere at the expense of very slow
     * execution time. Almost never choose this in real applications unless you
     * are very sure; this is almost solely for verification purposes. Normally,
     * you would choose {@link KolmogorovSmirnovDistribution#cdf(double)}. See
     * the class javadoc for definitions and algorithm description.
     *
     * @param d statistic
     * @return the two-sided probability of {@code P(D_n < d)}
     * @throws MathArithmeticException if the algorithm fails to convert
     *         {@code h} to a
     *         {@link org.apache.commons.math3.fraction.BigFraction} in
     *         expressing {@code d} as {@code (k - h) / m} for integer
     *         {@code k, m} and {@code 0 <= h < 1}.
     */
    public double cdfExact(double d, int n)
        throws MathArithmeticException {
        return cdf(d, n, true);
    }

    /**
     * Calculates {@code P(D_n < d)} using method described in [1] with quick
     * decisions for extreme values given in [2] (see above).
     *
     * @param d statistic
     * @param exact whether the probability should be calculated exact using
     *        {@link org.apache.commons.math3.fraction.BigFraction} everywhere
     *        at the expense of very slow execution time, or if {@code double}
     *        should be used convenient places to gain speed. Almost never
     *        choose {@code true} in real applications unless you are very sure;
     *        {@code true} is almost solely for verification purposes.
     * @return the two-sided probability of {@code P(D_n < d)}
     * @throws MathArithmeticException if algorithm fails to convert {@code h}
     *         to a {@link org.apache.commons.math3.fraction.BigFraction} in
     *         expressing {@code d} as {@code (k - h) / m} for integer
     *         {@code k, m} and {@code 0 <= h < 1}.
     */
    public double cdf(double d, int n, boolean exact)
        throws MathArithmeticException {

        final double ninv = 1 / ((double) n);
        final double ninvhalf = 0.5 * ninv;

        if (d <= ninvhalf) {
            return 0;
        } else if (ninvhalf < d && d <= ninv) {
            double res = 1;
            final double f = 2 * d - ninv;
            // n! f^n = n*f * (n-1)*f * ... * 1*x
            for (int i = 1; i <= n; ++i) {
                res *= i * f;
            }
            return res;
        } else if (1 - ninv <= d && d < 1) {
            return 1 - 2 * Math.pow(1 - d, n);
        } else if (1 <= d) {
            return 1;
        }
        return exact ? exactK(d, n) : roundedK(d, n);
    }

    /**
     * Calculates the exact value of {@code P(D_n < d)} using method described
     * in [1] and {@link org.apache.commons.math3.fraction.BigFraction} (see
     * above).
     *
     * @param d statistic
     * @return the two-sided probability of {@code P(D_n < d)}
     * @throws MathArithmeticException if algorithm fails to convert {@code h}
     *         to a {@link org.apache.commons.math3.fraction.BigFraction} in
     *         expressing {@code d} as {@code (k - h) / m} for integer
     *         {@code k, m} and {@code 0 <= h < 1}.
     */
    private double exactK(double d, int n)
        throws MathArithmeticException {

        final int k = (int) Math.ceil(n * d);

        final FieldMatrix<BigFraction> H = this.createH(d, n);
        final FieldMatrix<BigFraction> Hpower = H.power(n);

        BigFraction pFrac = Hpower.getEntry(k - 1, k - 1);

        for (int i = 1; i <= n; ++i) {
            pFrac = pFrac.multiply(i).divide(n);
        }

        /*
         * BigFraction.doubleValue converts numerator to double and the
         * denominator to double and divides afterwards. That gives NaN quite
         * easy. This does not (scale is the number of digits):
         */
        return pFrac.bigDecimalValue(20, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * Calculates {@code P(D_n < d)} using method described in [1] and doubles
     * (see above).
     *
     * @param d statistic
     * @return the two-sided probability of {@code P(D_n < d)}
     * @throws MathArithmeticException if algorithm fails to convert {@code h}
     *         to a {@link org.apache.commons.math3.fraction.BigFraction} in
     *         expressing {@code d} as {@code (k - h) / m} for integer
     *         {@code k, m} and {@code 0 <= h < 1}.
     */
    private double roundedK(double d, int n)
        throws MathArithmeticException {

        final int k = (int) Math.ceil(n * d);
        final FieldMatrix<BigFraction> HBigFraction = this.createH(d, n);
        final int m = HBigFraction.getRowDimension();

        /*
         * Here the rounding part comes into play: use RealMatrix instead of
         * FieldMatrix<BigFraction>
         */
        final RealMatrix H = new Array2DRowRealMatrix(m, m);

        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < m; ++j) {
                H.setEntry(i, j, HBigFraction.getEntry(i, j).doubleValue());
            }
        }

        final RealMatrix Hpower = H.power(n);

        double pFrac = Hpower.getEntry(k - 1, k - 1);

        for (int i = 1; i <= n; ++i) {
            pFrac *= (double) i / (double) n;
        }

        return pFrac;
    }

    /***
     * Creates {@code H} of size {@code m x m} as described in [1] (see above).
     *
     * @param d statistic
     * @return H matrix
     * @throws NumberIsTooLargeException if fractional part is greater than 1
     * @throws FractionConversionException if algorithm fails to convert
     *         {@code h} to a
     *         {@link org.apache.commons.math3.fraction.BigFraction} in
     *         expressing {@code d} as {@code (k - h) / m} for integer
     *         {@code k, m} and {@code 0 <= h < 1}.
     */
    private FieldMatrix<BigFraction> createH(double d, int n)
        throws NumberIsTooLargeException, FractionConversionException {

        final int k = (int) Math.ceil(n * d);

        final int m = 2 * k - 1;
        final double hDouble = k - n * d;

        if (hDouble >= 1) {
            throw new NumberIsTooLargeException(hDouble, 1.0, false);
        }

        BigFraction h = null;

        try {
            h = new BigFraction(hDouble, 1.0e-20, 10000);
        } catch (final FractionConversionException e1) {
            try {
                h = new BigFraction(hDouble, 1.0e-10, 10000);
            } catch (final FractionConversionException e2) {
                h = new BigFraction(hDouble, 1.0e-5, 10000);
            }
        }

        final BigFraction[][] Hdata = new BigFraction[m][m];

        /*
         * Start by filling everything with either 0 or 1.
         */
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < m; ++j) {
                if (i - j + 1 < 0) {
                    Hdata[i][j] = BigFraction.ZERO;
                } else {
                    Hdata[i][j] = BigFraction.ONE;
                }
            }
        }

        /*
         * Setting up power-array to avoid calculating the same value twice:
         * hPowers[0] = h^1 ... hPowers[m-1] = h^m
         */
        final BigFraction[] hPowers = new BigFraction[m];
        hPowers[0] = h;
        for (int i = 1; i < m; ++i) {
            hPowers[i] = h.multiply(hPowers[i - 1]);
        }

        /*
         * First column and last row has special values (each other reversed).
         */
        for (int i = 0; i < m; ++i) {
            Hdata[i][0] = Hdata[i][0].subtract(hPowers[i]);
            Hdata[m - 1][i] = Hdata[m - 1][i].subtract(hPowers[m - i - 1]);
        }

        /*
         * [1] states: "For 1/2 < h < 1 the bottom left element of the matrix
         * should be (1 - 2*h^m + (2h - 1)^m )/m!" Since 0 <= h < 1, then if h >
         * 1/2 is sufficient to check:
         */
        if (h.compareTo(BigFraction.ONE_HALF) == 1) {
            Hdata[m - 1][0] = Hdata[m - 1][0].add(h.multiply(2).subtract(1).pow(m));
        }

        /*
         * Aside from the first column and last row, the (i, j)-th element is
         * 1/(i - j + 1)! if i - j + 1 >= 0, else 0. 1's and 0's are already
         * put, so only division with (i - j + 1)! is needed in the elements
         * that have 1's. There is no need to calculate (i - j + 1)! and then
         * divide - small steps avoid overflows. Note that i - j + 1 > 0 <=> i +
         * 1 > j instead of j'ing all the way to m. Also note that it is started
         * at g = 2 because dividing by 1 isn't really necessary.
         */
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < i + 1; ++j) {
                if (i - j + 1 > 0) {
                    for (int g = 2; g <= i - j + 1; ++g) {
                        Hdata[i][j] = Hdata[i][j].divide(g);
                    }
                }
            }
        }

        return new Array2DRowFieldMatrix<BigFraction>(BigFractionField.getInstance(), Hdata);
    }

    /**
     * Verifies that array has length at least 2, throwing MIAE if not.
     *
     * @param array array to test
     * @throws NullArgumentException if array is null
     * @throws MathIllegalArgumentException if array is too short
     */
    private void checkArray(double[] array) {
        if (array == null) {
            throw new NullArgumentException(LocalizedFormats.NULL_NOT_ALLOWED);
        }
        if (array.length < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE,
                                                   array.length, 2);
        }
    }

    /**
     * Compute \( \sum_{k=-\infty}^\infty (-1)^k e^{-2 k^2 x^2} = 1 + 2
     * \sum_{k=1}^\infty (-1)^k e^{-2 k^2 x^2} = \frac{\sqrt{2\pi}}{x}
     * \sum_{k=1}^\infty \exp(-(2k-1)^2\pi^2/(8x^2)) \) See e.g. J. Durbin
     * (1973), Distribution Theory for Tests Based on the Sample Distribution
     * Function. SIAM. The 'standard' series expansion obviously cannot be used
     * close to 0; we use the alternative series for x < 1, and a rather crude
     * estimate of the series remainder term in this case, in particular using
     * that \(ue^(-lu^2) \le e^(-lu^2 + u) \le e^(-(l-1)u^2 - u^2+u) \le
     * e^(-(l-1))\) provided that u and l are >= 1. (But note that for
     * reasonable tolerances, one could simply take 0 as the value for x < 0.2,
     * and use the standard expansion otherwise.)
     */
    public double pkstwo(double x, double tol) {
        final double M_PI_2 = Math.PI / 2;
        final double M_PI_4 = Math.PI / 4;
        final double M_1_SQRT_2PI = 1 / Math.sqrt(Math.PI * 2);
        double newx, old, s;
        int k;
        final int k_max = (int) Math.sqrt(2 - Math.log(tol));
        if (x < 1) {
            final double z = -(M_PI_2 * M_PI_4) / (x * x);
            final double w = Math.log(x);
            s = 0;
            for (k = 1; k < k_max; k += 2) {
                s += Math.exp(k * k * z - w);
            }
            return s / M_1_SQRT_2PI;
        } else {
            final double z = -2 * x * x;
            s = -1;
            k = 1;
            old = 0;
            newx = 1;
            while (Math.abs(old - newx) > tol) {
                old = newx;
                newx += 2 * s * Math.exp(z * k * k);
                s *= -1;
                k++;
            }
            return newx;
        }
    }

    /**
     * Computes \( 1 + 2 \sum_{i=1}^\infty (-1)^i e^{-2 i^2 t^2} \) stopping
     * when successive partial sums are within {@code tolerance} of one another,
     * or when {@code maxIter} partial sums have been computed. If the sum does
     * not converge before {@code maxIter} iterations a
     * {@link TooManyIterationsException} is thrown.
     *
     * @param t argument
     * @param tolerance Cauchy criterion for partial sums
     * @param maxIter maximum number of partial sums to compute
     * @throws TooManyIterationsException if the series does not converge
     */
    public double ksSum(double t, double tolerance, long maxIter) {
        final double x = -2 * t * t;
        double sign = -1;
        int i = 1;
        double lastPartialSum = -1d;
        double partialSum = 0.5d;
        long iterationCount = 0;
        while (FastMath.abs(lastPartialSum - partialSum) > tolerance && iterationCount < maxIter) {
            lastPartialSum = partialSum;
            partialSum += sign * FastMath.exp(x * i * i);
            sign *= -1;
            i++;
        }
        if (iterationCount == maxIter) {
            throw new TooManyIterationsException(maxIter);
        }
        return partialSum * 2;
    }

    public double exactP(double d, int n, int m, boolean strict) {
        Iterator<int[]> combinationsIterator = CombinatoricsUtils.combinationsIterator(n + m, n);
        long tail = 0;
        final double[] nSet = new double[n];
        final double[] mSet = new double[m];
        while (combinationsIterator.hasNext()) {
            // Generate an n-set
            final int[] nSetI = combinationsIterator.next();
            // Copy the n-set to nSet and its complement to mSet
            int j = 0;
            int k = 0;
            for (int i = 0; i < n + m; i++) {
                if (j < n && nSetI[j] == i) {
                    nSet[j++] = i;
                } else {
                    mSet[k++] = i;
                }
            }
            final double curD = kolmogorovSmirnovStatistic(nSet, mSet);
            if (curD > d) {
                tail++;
            } else if (curD == d && !strict) {
                tail++;
            }
        }
        return (double) tail / (double) CombinatoricsUtils.binomialCoefficient(n + m, n);
    }

    public double approximateP(double d, int n, int m) {
        return 1 - ksSum(d * FastMath.sqrt((double) (m * n) / (double) (m + n)), KS_SUM_CAUCHY_CRITERION,
                         MAXIMUM_PARTIAL_SUM_COUNT);
    }

}
