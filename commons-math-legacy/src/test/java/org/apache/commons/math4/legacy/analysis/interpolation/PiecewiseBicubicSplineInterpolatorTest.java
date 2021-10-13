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

import org.apache.commons.math4.legacy.analysis.BivariateFunction;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.UniformContinuousDistribution;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.InsufficientDataException;
import org.apache.commons.math4.legacy.exception.NonMonotonicSequenceException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the piecewise bicubic interpolator.
 */
public final class PiecewiseBicubicSplineInterpolatorTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() {
        double[] xval = new double[] { 3, 4, 5, 6.5, 7.5 };
        double[] yval = new double[] { -4, -3, -1, 2.5, 3.5 };
        double[][] zval = new double[xval.length][yval.length];

        BivariateGridInterpolator interpolator = new PiecewiseBicubicSplineInterpolator();

        try {
            interpolator.interpolate( null, yval, zval );
            Assert.fail( "Failed to detect x null pointer" );
        } catch ( NullArgumentException iae ) {
            // Expected.
        }

        try {
            interpolator.interpolate( xval, null, zval );
            Assert.fail( "Failed to detect y null pointer" );
        } catch ( NullArgumentException iae ) {
            // Expected.
        }

        try {
            interpolator.interpolate( xval, yval, null );
            Assert.fail( "Failed to detect z null pointer" );
        } catch ( NullArgumentException iae ) {
            // Expected.
        }

        try {
            double xval1[] = { 0.0, 1.0, 2.0, 3.0 };
            interpolator.interpolate( xval1, yval, zval );
            Assert.fail( "Failed to detect insufficient x data" );
        } catch ( InsufficientDataException iae ) {
            // Expected.
        }

        try  {
            double yval1[] = { 0.0, 1.0, 2.0, 3.0 };
            interpolator.interpolate( xval, yval1, zval );
            Assert.fail( "Failed to detect insufficient y data" );
        } catch ( InsufficientDataException iae ) {
            // Expected.
        }

        try {
            double zval1[][] = new double[4][4];
            interpolator.interpolate( xval, yval, zval1 );
            Assert.fail( "Failed to detect insufficient z data" );
        } catch ( InsufficientDataException iae ) {
            // Expected.
        }

        try {
            double xval1[] = { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 };
            interpolator.interpolate( xval1, yval, zval );
            Assert.fail( "Failed to detect data set array with different sizes." );
        } catch ( DimensionMismatchException iae ) {
            // Expected.
        }

        try {
            double yval1[] = { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 };
            interpolator.interpolate( xval, yval1, zval );
            Assert.fail( "Failed to detect data set array with different sizes." );
        } catch ( DimensionMismatchException iae ) {
            // Expected.
        }

        // X values not sorted.
        try {
            double xval1[] = { 0.0, 1.0, 0.5, 7.0, 3.5 };
            interpolator.interpolate( xval1, yval, zval );
            Assert.fail( "Failed to detect unsorted x arguments." );
        } catch ( NonMonotonicSequenceException iae ) {
            // Expected.
        }

        // Y values not sorted.
        try {
            double yval1[] = { 0.0, 1.0, 1.5, 0.0, 3.0 };
            interpolator.interpolate( xval, yval1, zval );
            Assert.fail( "Failed to detect unsorted y arguments." );
        } catch ( NonMonotonicSequenceException iae ) {
            // Expected.
        }
    }

    /**
     * Interpolating a plane.
     * <p>
     * z = 2 x - 3 y + 5
     */
    @Test
    public void testInterpolation1() {
        final int sz = 21;
        double[] xval = new double[sz];
        double[] yval = new double[sz];
        // Coordinate values
        final double delta = 1d / (sz - 1);
        for ( int i = 0; i < sz; i++ ){
            xval[i] = -1 + 15 * i * delta;
            yval[i] = -20 + 30 * i * delta;
        }

        // Function values
        BivariateFunction f = new BivariateFunction() {
                @Override
                public double value( double x, double y ) {
                    return 2 * x - 3 * y + 5;
                }
            };
        double[][] zval = new double[xval.length][yval.length];
        for ( int i = 0; i < xval.length; i++ ) {
            for ( int j = 0; j < yval.length; j++ ) {
                zval[i][j] = f.value(xval[i], yval[j]);
            }
        }

        BivariateGridInterpolator interpolator = new PiecewiseBicubicSplineInterpolator();
        BivariateFunction p = interpolator.interpolate(xval, yval, zval);
        double x;
        double y;

        final UniformRandomProvider rng = RandomSource.WELL_19937_C.create(1234567L);
        final ContinuousDistribution.Sampler distX = UniformContinuousDistribution.of(xval[0], xval[xval.length - 1]).createSampler(rng);
        final ContinuousDistribution.Sampler distY = UniformContinuousDistribution.of(yval[0], yval[yval.length - 1]).createSampler(rng);

        final int numSamples = 50;
        final double tol = 2e-14;
        for ( int i = 0; i < numSamples; i++ ) {
            x = distX.sample();
            for ( int j = 0; j < numSamples; j++ ) {
                y = distY.sample();
//                 System.out.println(x + " " + y + " " + f.value(x, y) + " " + p.value(x, y));
                Assert.assertEquals(f.value(x, y),  p.value(x, y), tol);
            }
//             System.out.println();
        }
    }

    /**
     * Interpolating a paraboloid.
     * <p>
     * z = 2 x<sup>2</sup> - 3 y<sup>2</sup> + 4 x y - 5
     */
    @Test
    public void testInterpolation2() {
        final int sz = 21;
        double[] xval = new double[sz];
        double[] yval = new double[sz];
        // Coordinate values
        final double delta = 1d / (sz - 1);
        for ( int i = 0; i < sz; i++ ) {
            xval[i] = -1 + 15 * i * delta;
            yval[i] = -20 + 30 * i * delta;
        }

        // Function values
        BivariateFunction f = new BivariateFunction() {
                @Override
                public double value( double x, double y ) {
                    return 2 * x * x - 3 * y * y + 4 * x * y - 5;
                }
            };
        double[][] zval = new double[xval.length][yval.length];
        for ( int i = 0; i < xval.length; i++ ) {
            for ( int j = 0; j < yval.length; j++ ) {
                zval[i][j] = f.value(xval[i], yval[j]);
            }
        }

        BivariateGridInterpolator interpolator = new PiecewiseBicubicSplineInterpolator();
        BivariateFunction p = interpolator.interpolate(xval, yval, zval);
        double x;
        double y;

        final UniformRandomProvider rng = RandomSource.WELL_19937_C.create(1234567L);
        final ContinuousDistribution.Sampler distX = UniformContinuousDistribution.of(xval[0], xval[xval.length - 1]).createSampler(rng);
        final ContinuousDistribution.Sampler distY = UniformContinuousDistribution.of(yval[0], yval[yval.length - 1]).createSampler(rng);

        final int numSamples = 50;
        final double tol = 5e-13;
        for ( int i = 0; i < numSamples; i++ ) {
            x = distX.sample();
            for ( int j = 0; j < numSamples; j++ ) {
                y = distY.sample();
//                 System.out.println(x + " " + y + " " + f.value(x, y) + " " + p.value(x, y));
                Assert.assertEquals(f.value(x, y),  p.value(x, y), tol);
            }
//             System.out.println();
        }
    }
}
