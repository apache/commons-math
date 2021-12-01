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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.UniformContinuousDistribution;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.NonMonotonicSequenceException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.numbers.core.Precision;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

public class AkimaSplineInterpolatorTest {
    @Test
    public void testIllegalArguments() {
        // Data set arrays of different size.
        UnivariateInterpolator i = new AkimaSplineInterpolator();

        try {
            double yval[] = {0.0, 1.0, 2.0, 3.0, 4.0};
            i.interpolate(null, yval);
            Assert.fail("Failed to detect x null pointer");
        } catch (NullArgumentException iae) {
            // Expected.
        }

        try {
            double xval[] = {0.0, 1.0, 2.0, 3.0, 4.0};
            i.interpolate(xval, null);
            Assert.fail("Failed to detect y null pointer");
        } catch (NullArgumentException iae) {
            // Expected.
        }

        try {
            double xval[] = {0.0, 1.0, 2.0, 3.0};
            double yval[] = {0.0, 1.0, 2.0, 3.0};
            i.interpolate(xval, yval);
            Assert.fail("Failed to detect insufficient data");
        } catch (NumberIsTooSmallException iae) {
            // Expected.
        }

        try {
            double xval[] = {0.0, 1.0, 2.0, 3.0, 4.0};
            double yval[] = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0};
            i.interpolate(xval, yval);
            Assert.fail("Failed to detect data set array with different sizes.");
        } catch (DimensionMismatchException iae) {
            // Expected.
        }

        // X values not sorted.
        try {
            double xval[] = {0.0, 1.0, 0.5, 7.0, 3.5, 2.2, 8.0};
            double yval[] = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
            i.interpolate(xval, yval);
            Assert.fail("Failed to detect unsorted arguments.");
        } catch (NonMonotonicSequenceException iae) {
            // Expected.
        }
    }

    /*
     * Interpolate a straight line. <p> y = 2 x - 5 <p> Tolerances determined by performing same calculation using
     * Math.NET over ten runs of 100 random number draws for the same function over the same span with the same number
     * of elements
     */
    @Test
    public void testInterpolateLine() {
        final int numberOfElements = 10;
        final double minimumX = -10;
        final double maximumX = 10;
        final int numberOfSamples = 100;
        final double interpolationTolerance = 1e-15;
        final double maxTolerance = 1e-15;

        UnivariateFunction f = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return 2 * x - 5;
            }
        };

        testInterpolation( minimumX, maximumX, numberOfElements, numberOfSamples, f, interpolationTolerance,
                           maxTolerance );
    }

    /*
     * Interpolate a straight line. <p> y = 3 x<sup>2</sup> - 5 x + 7 <p> Tolerances determined by performing same
     * calculation using Math.NET over ten runs of 100 random number draws for the same function over the same span with
     * the same number of elements
     */

    @Test
    public void testInterpolateParabola() {
        final int numberOfElements = 10;
        final double minimumX = -10;
        final double maximumX = 10;
        final int numberOfSamples = 100;
        final double interpolationTolerance = 7e-15;
        final double maxTolerance = 6e-14;

        UnivariateFunction f = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return (3 * x * x) - (5 * x) + 7;
            }
        };

        testInterpolation( minimumX, maximumX, numberOfElements, numberOfSamples, f, interpolationTolerance,
                           maxTolerance );
    }

    /*
     * Interpolate a straight line. <p> y = 3 x<sup>3</sup> - 0.5 x<sup>2</sup> + x - 1 <p> Tolerances determined by
     * performing same calculation using Math.NET over ten runs of 100 random number draws for the same function over
     * the same span with the same number of elements
     */
    @Test
    public void testInterpolateCubic() {
        final int numberOfElements = 10;
        final double minimumX = -3;
        final double maximumX = 3;
        final int numberOfSamples = 100;
        final double interpolationTolerance = 0.37;
        final double maxTolerance = 3.8;

        UnivariateFunction f = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return (3 * x * x * x) - (0.5 * x * x) + (1 * x) - 1;
            }
        };

        testInterpolation( minimumX, maximumX, numberOfElements, numberOfSamples, f, interpolationTolerance,
                           maxTolerance );
    }

    // Test currently fails but it is not clear whether
    //   https://issues.apache.org/jira/browse/MATH-1635
    // actually describes a bug, or a limitation of the algorithm.
    @Ignore
    @Test
    public void testMath1635() {
        final double[] x = {
            5994, 6005, 6555, 6588, 6663,
            6760, 6770, 6792, 6856, 6964,
            7028, 7233, 7426, 7469, 7619,
            7910, 8038, 8178, 8414, 8747,
            8983, 9316, 9864, 9875
        };

        final double[] y = {
            3.0, 2.0, 2.0, 2.0, 2.0,
            2.0, 2.0, 2.0, 2.0, 2.0,
            2.0, 2.0, 2.0, 2.0, 2.0,
            2.0, 2.0, 2.0, 2.0, 2.0,
            2.0, 2.0, 2.0, 3.0
        };

        final AkimaSplineInterpolator interpolator = new AkimaSplineInterpolator(true);
        final PolynomialSplineFunction interpolate = interpolator.interpolate(x, y);
        final double value = interpolate.value(9584);
        final double expected = 2;
        Assert.assertEquals(expected, value, 1e-4);
    }

    @Test
    public void testOriginalVsModified() {
        final UnivariateFunction f = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return x < -1 ? -1 :
                    x < 1 ? x : 1;
            }
        };

        final double[] xS = new double[] {-1, 0, 1, 2, 3 };
        final double[] yS = new double[xS.length];

        for (int i = 0; i < xS.length; i++) {
            yS[i] = f.value(xS[i]);
        }

        final UnivariateFunction iOriginal = new AkimaSplineInterpolator(false).interpolate(xS, yS);
        final UnivariateFunction iModified = new AkimaSplineInterpolator(true).interpolate(xS, yS);

        final int n = 100;
        final double delta = 1d / n;
        for (int i = 1; i < n - 1; i++) {
            final double x = 2 - i * delta;

            final double value = f.value(x);
            final double diffOriginal = Math.abs(iOriginal.value(x) - value);
            final double diffModified = Math.abs(iModified.value(x) - value);

            // In interval (1, 2), the modified algorithm eliminates interpolation artefacts.
            Assert.assertTrue(diffOriginal > 0);
            Assert.assertEquals(0d, diffModified, 0d);
        }
    }

    private void testInterpolation( double minimumX, double maximumX, int numberOfElements, int numberOfSamples,
                                    UnivariateFunction f, double tolerance, double maxTolerance ) {
        double expected;
        double actual;
        double currentX;
        final double delta = ( maximumX - minimumX ) / ( (double) numberOfElements );
        double xValues[] = new double[numberOfElements];
        double yValues[] = new double[numberOfElements];

        for (int i = 0; i < numberOfElements; i++) {
            xValues[i] = minimumX + delta * (double) i;
            yValues[i] = f.value(xValues[i]);
        }

        UnivariateFunction interpolation = new AkimaSplineInterpolator().interpolate( xValues, yValues );

        for (int i = 0; i < numberOfElements; i++) {
            currentX = xValues[i];
            expected = f.value(currentX);
            actual = interpolation.value( currentX );
            assertTrue( Precision.equals( expected, actual ) );
        }

        final UniformRandomProvider rng = RandomSource.WELL_19937_C.create(1234567L); // "tol" depends on the seed.
        final ContinuousDistribution.Sampler distX =
            UniformContinuousDistribution.of(xValues[0], xValues[xValues.length - 1]).createSampler(rng);

        double sumError = 0;
        for (int i = 0; i < numberOfSamples; i++) {
            currentX = distX.sample();
            expected = f.value(currentX);
            actual = interpolation.value( currentX );
            sumError += JdkMath.abs( actual - expected );
            assertEquals( expected, actual, maxTolerance );
        }

        assertEquals( 0.0, sumError / (double) numberOfSamples, tolerance );
    }
}
