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

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AkimaSplineInterpolatorTest
{

    @Test
    public void testIllegalArguments()
    {
        // Data set arrays of different size.
        UnivariateInterpolator i = new AkimaSplineInterpolator();

        try
        {
            double yval[] = { 0.0, 1.0, 2.0, 3.0, 4.0 };
            i.interpolate( null, yval );
            Assert.fail( "Failed to detect x null pointer" );
        }
        catch ( NullArgumentException iae )
        {
            // Expected.
        }

        try
        {
            double xval[] = { 0.0, 1.0, 2.0, 3.0, 4.0 };
            i.interpolate( xval, null );
            Assert.fail( "Failed to detect y null pointer" );
        }
        catch ( NullArgumentException iae )
        {
            // Expected.
        }

        try
        {
            double xval[] = { 0.0, 1.0, 2.0, 3.0 };
            double yval[] = { 0.0, 1.0, 2.0, 3.0 };
            i.interpolate( xval, yval );
            Assert.fail( "Failed to detect insufficient data" );
        }
        catch ( NumberIsTooSmallException iae )
        {
            // Expected.
        }

        try
        {
            double xval[] = { 0.0, 1.0, 2.0, 3.0, 4.0 };
            double yval[] = { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 };
            i.interpolate( xval, yval );
            Assert.fail( "Failed to detect data set array with different sizes." );
        }
        catch ( DimensionMismatchException iae )
        {
            // Expected.
        }

        // X values not sorted.
        try
        {
            double xval[] = { 0.0, 1.0, 0.5, 7.0, 3.5, 2.2, 8.0 };
            double yval[] = { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 };
            i.interpolate( xval, yval );
            Assert.fail( "Failed to detect unsorted arguments." );
        }
        catch ( NonMonotonicSequenceException iae )
        {
            // Expected.
        }
    }

    /*
     * Interpolate a straight line. <p> y = 2 x - 5 <p> Tolerances determined by performing same calculation using
     * Math.NET over ten runs of 100 random number draws for the same function over the same span with the same number
     * of elements
     */
    @Test
    public void testInterpolateLine()
    {
        final int numberOfElements = 10;
        final double minimumX = -10;
        final double maximumX = 10;
        final int numberOfSamples = 100;
        final double interpolationTolerance = 1e-15;
        final double maxTolerance = 1e-15;

        UnivariateFunction f = new UnivariateFunction()
        {
            public double value( double x )
            {
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
    public void testInterpolateParabola()
    {
        final int numberOfElements = 10;
        final double minimumX = -10;
        final double maximumX = 10;
        final int numberOfSamples = 100;
        final double interpolationTolerance = 7e-15;
        final double maxTolerance = 6e-14;

        UnivariateFunction f = new UnivariateFunction()
        {
            public double value( double x )
            {
                return ( 3 * x * x ) - ( 5 * x ) + 7;
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
    public void testInterpolateCubic()
    {
        final int numberOfElements = 10;
        final double minimumX = -3;
        final double maximumX = 3;
        final int numberOfSamples = 100;
        final double interpolationTolerance = 0.37;
        final double maxTolerance = 3.8;

        UnivariateFunction f = new UnivariateFunction()
        {
            public double value( double x )
            {
                return ( 3 * x * x * x ) - ( 0.5 * x * x ) + ( 1 * x ) - 1;
            }
        };

        testInterpolation( minimumX, maximumX, numberOfElements, numberOfSamples, f, interpolationTolerance,
                           maxTolerance );
    }

    private void testInterpolation( double minimumX, double maximumX, int numberOfElements, int numberOfSamples,
                                    UnivariateFunction f, double tolerance, double maxTolerance )
    {
        double expected;
        double actual;
        double currentX;
        final double delta = ( maximumX - minimumX ) / ( (double) numberOfElements );
        double xValues[] = new double[numberOfElements];
        double yValues[] = new double[numberOfElements];

        for ( int i = 0; i < numberOfElements; i++ )
        {
            xValues[i] = minimumX + delta * (double) i;
            yValues[i] = f.value( xValues[i] );
        }

        UnivariateFunction interpolation = new AkimaSplineInterpolator().interpolate( xValues, yValues );

        for ( int i = 0; i < numberOfElements; i++ )
        {
            currentX = xValues[i];
            expected = f.value( currentX );
            actual = interpolation.value( currentX );
            assertTrue( Precision.equals( expected, actual ) );
        }

        final RandomGenerator rng = new Well19937c( 1234567L ); // "tol" depends on the seed.
        final UniformRealDistribution distX =
            new UniformRealDistribution( rng, xValues[0], xValues[xValues.length - 1] );

        double sumError = 0;
        for ( int i = 0; i < numberOfSamples; i++ )
        {
            currentX = distX.sample();
            expected = f.value( currentX );
            actual = interpolation.value( currentX );
            sumError += FastMath.abs( actual - expected );
            assertEquals( expected, actual, maxTolerance );
        }

        assertEquals( 0.0, ( sumError / (double) numberOfSamples ), tolerance );
    }
}
